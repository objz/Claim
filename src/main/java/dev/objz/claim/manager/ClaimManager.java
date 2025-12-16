package dev.objz.claim.manager;

import dev.objz.claim.Claim;
import dev.objz.claim.model.ClaimFlag;
import dev.objz.claim.model.ClaimRegion;
import dev.objz.claim.model.ClaimRole;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ClaimManager {
	private final Claim plugin;
	private final Map<UUID, ClaimRegion> claims = new HashMap<>();
	private final File claimsFile;

	public ClaimManager(Claim plugin) {
		this.plugin = plugin;
		this.claimsFile = new File(plugin.getDataFolder(), "claims.yml");
		loadClaims();
	}

	public ClaimRegion createClaim(UUID owner, String name, Location pos1, Location pos2) {
		int x1 = pos1.getBlockX();
		int z1 = pos1.getBlockZ();
		int x2 = pos2.getBlockX();
		int z2 = pos2.getBlockZ();

		double minX = Math.min(x1, x2);
		double minZ = Math.min(z1, z2);
		double maxX = Math.max(x1, x2) + 1.0;
		double maxZ = Math.max(z1, z2) + 1.0;

		double minY = pos1.getWorld().getMinHeight();
		double maxY = pos1.getWorld().getMaxHeight();

		Location lower = new Location(pos1.getWorld(), minX, minY, minZ);
		Location upper = new Location(pos1.getWorld(), maxX, maxY, maxZ);

		ClaimRegion claim = new ClaimRegion(owner, name, lower, upper);
		claims.put(claim.getId(), claim);
		saveClaims();
		return claim;
	}

	public void deleteClaim(UUID claimId) {
		claims.remove(claimId);
		saveClaims();
	}

	public Optional<ClaimRegion> getClaimAt(Location location) {
		if (location == null || location.getWorld() == null)
			return Optional.empty();
		return claims.values().stream()
				.filter(c -> c.contains(location))
				.findFirst();
	}

	public Collection<ClaimRegion> getAllClaims() {
		return claims.values();
	}

	public void saveClaims() {
		YamlConfiguration config = new YamlConfiguration();

		for (ClaimRegion claim : claims.values()) {
			String path = "claims." + claim.getId().toString();
			config.set(path + ".owner", claim.getOwner().toString());
			config.set(path + ".name", claim.getName());
			config.set(path + ".world", claim.getWorldName());
			config.set(path + ".minX", claim.getRegion().getMinX());
			config.set(path + ".minY", claim.getRegion().getMinY());
			config.set(path + ".minZ", claim.getRegion().getMinZ());
			config.set(path + ".maxX", claim.getRegion().getMaxX());
			config.set(path + ".maxY", claim.getRegion().getMaxY());
			config.set(path + ".maxZ", claim.getRegion().getMaxZ());

			for (ClaimFlag flag : ClaimFlag.values()) {
				if (flag.isGlobal()) {
					config.set(path + ".flags." + flag.name(), claim.getFlag(flag));
				}
			}

			List<String> memberList = new ArrayList<>();
			claim.getMembers().forEach((uuid, role) -> memberList.add(uuid + ":" + role.name()));
			config.set(path + ".members", memberList);
		}

		try {
			config.save(claimsFile);
		} catch (IOException e) {
			plugin.getLogger().severe("Could not save claims: " + e.getMessage());
		}
	}

	private void loadClaims() {
		if (!claimsFile.exists())
			return;

		YamlConfiguration config = YamlConfiguration.loadConfiguration(claimsFile);
		if (!config.contains("claims"))
			return;

		for (String key : config.getConfigurationSection("claims").getKeys(false)) {
			try {
				String path = "claims." + key;
				UUID owner = UUID.fromString(config.getString(path + ".owner"));
				String name = config.getString(path + ".name");
				String world = config.getString(path + ".world");
				double minX = config.getDouble(path + ".minX");
				double minY = config.getDouble(path + ".minY");
				double minZ = config.getDouble(path + ".minZ");
				double maxX = config.getDouble(path + ".maxX");
				double maxY = config.getDouble(path + ".maxY");
				double maxZ = config.getDouble(path + ".maxZ");

				if (Bukkit.getWorld(world) == null)
					continue;

				Location p1 = new Location(Bukkit.getWorld(world), minX, minY, minZ);
				Location p2 = new Location(Bukkit.getWorld(world), maxX, maxY, maxZ);

				ClaimRegion claim = new ClaimRegion(owner, name, p1, p2);

				if (config.contains(path + ".flags")) {
					for (String flagName : config.getConfigurationSection(path + ".flags")
							.getKeys(false)) {
						try {
							ClaimFlag flag = ClaimFlag.valueOf(flagName);
							claim.setGlobalFlag(flag,
									config.getBoolean(path + ".flags." + flagName));
						} catch (IllegalArgumentException ignored) {
						}
					}
				}

				for (String entry : config.getStringList(path + ".members")) {
					String[] parts = entry.split(":");
					if (parts.length == 2) {
						try {
							claim.setMemberRole(UUID.fromString(parts[0]),
									ClaimRole.valueOf(parts[1]));
						} catch (Exception ignored) {
						}
					}
				}

				claims.put(claim.getId(), claim);

			} catch (Exception e) {
				plugin.getLogger().warning("Failed to load claim " + key);
			}
		}
	}
}
