package dev.objz.claim.manager;

import dev.objz.claim.Claim;
import dev.objz.claim.model.GlobalFlags;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import dev.objz.claim.model.Roles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ClaimManager {
	private final Claim plugin;
	private final Map<UUID, Region> claims = new HashMap<>();
	private final File claimsFile;

	public ClaimManager(Claim plugin) {
		this.plugin = plugin;
		this.claimsFile = new File(plugin.getDataFolder(), "claims.yml");
		loadClaims();
	}

	public Region createClaim(UUID owner, String name, Location pos1, Location pos2) {
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

		Region claim = new Region(owner, name, lower, upper);
		claims.put(claim.getId(), claim);
		saveClaims();
		return claim;
	}

	public void resizeClaim(Region claim, BoundingBox newBox) {
		claim.resize(newBox);
		saveClaims();
	}

	public void transferClaim(Region claim, UUID newOwner) {
		claim.setOwner(newOwner);
		saveClaims();
	}

	public boolean isOverlapping(BoundingBox box, String worldName) {
		for (Region claim : claims.values()) {
			if (claim.getWorldName().equals(worldName)) {
				if (claim.getRegion().overlaps(box)) {
					return true;
				}
			}
		}
		return false;
	}

	public void deleteClaim(UUID claimId) {
		claims.remove(claimId);
		saveClaims();
	}

	public Optional<Region> getClaimAt(Location location) {
		if (location == null || location.getWorld() == null)
			return Optional.empty();
		return claims.values().stream().filter(c -> c.contains(location)).findFirst();
	}

	public Collection<Region> getAllClaims() {
		return Collections.unmodifiableCollection(claims.values());
	}

	public void saveClaims() {
		YamlConfiguration config = new YamlConfiguration();

		for (Region claim : claims.values()) {
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

			for (GlobalFlags flag : GlobalFlags.values()) {
				config.set(path + ".globalFlags." + flag.name(), claim.getFlag(flag));
			}

			for (Roles role : Roles.values()) {
				for (PlayerFlags flag : PlayerFlags.values()) {
					config.set(path + ".rolePermissions." + role.name() + "." + flag.name(),
							claim.getFlag(role, flag));
				}
			}

			if (!claim.getPlayerFlagOverrides().isEmpty()) {
				for (Map.Entry<UUID, Map<PlayerFlags, Boolean>> entry : claim.getPlayerFlagOverrides()
						.entrySet()) {
					String uuid = entry.getKey().toString();
					for (Map.Entry<PlayerFlags, Boolean> flagEntry : entry.getValue().entrySet()) {
						config.set(path + ".playerOverrides." + uuid + "."
								+ flagEntry.getKey().name(), flagEntry.getValue());
					}
				}
			}

			List<String> memberList = new ArrayList<>();
			claim.getMembers().forEach((uuid, role) -> memberList.add(uuid + ":" + role.name()));
			config.set(path + ".members", memberList);
		}

		try {
			config.save(claimsFile);
		} catch (IOException e) {
			plugin.getLogger().severe("Could not save claims:  " + e.getMessage());
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

				Region claim = new Region(owner, name, p1, p2);

				if (config.contains(path + ".globalFlags")) {
					for (String flagName : config.getConfigurationSection(path + ".globalFlags")
							.getKeys(false)) {
						try {
							GlobalFlags flag = GlobalFlags.valueOf(flagName);
							claim.setFlag(flag, config
									.getBoolean(path + ".globalFlags." + flagName));
						} catch (IllegalArgumentException ignored) {
						}
					}
				}

				if (config.contains(path + ".rolePermissions")) {
					for (String roleName : config.getConfigurationSection(path + ".rolePermissions")
							.getKeys(false)) {
						try {
							Roles role = Roles.valueOf(roleName);
							for (String flagName : config.getConfigurationSection(
									path + ".rolePermissions." + roleName)
									.getKeys(false)) {
								try {
									PlayerFlags flag = PlayerFlags
											.valueOf(flagName);
									claim.setFlag(role, flag, config.getBoolean(path
											+ ".rolePermissions." + roleName
											+ "." + flagName));
								} catch (IllegalArgumentException ignored) {
								}
							}
						} catch (IllegalArgumentException ignored) {
						}
					}
				}

				if (config.contains(path + ".playerOverrides")) {
					for (String uuidStr : config.getConfigurationSection(path + ".playerOverrides")
							.getKeys(false)) {
						try {
							UUID playerUuid = UUID.fromString(uuidStr);
							for (String flagName : config
									.getConfigurationSection(path
											+ ".playerOverrides." + uuidStr)
									.getKeys(false)) {
								try {
									PlayerFlags flag = PlayerFlags
											.valueOf(flagName);
									boolean value = config.getBoolean(path
											+ ".playerOverrides." + uuidStr
											+ "." + flagName);
									claim.setPlayerFlag(playerUuid, flag, value);
								} catch (IllegalArgumentException ignored) {
								}
							}
						} catch (IllegalArgumentException ignored) {
						}
					}
				}

				for (String entry : config.getStringList(path + ".members")) {
					String[] parts = entry.split(":");
					if (parts.length == 2) {
						try {
							claim.setRole(UUID.fromString(parts[0]),
									Roles.valueOf(parts[1]));
						} catch (Exception ignored) {
						}
					}
				}

				claims.put(claim.getId(), claim);

			} catch (Exception e) {
				plugin.getLogger().warning("Failed to load claim " + key + ": " + e.getMessage());
			}
		}
	}
}
