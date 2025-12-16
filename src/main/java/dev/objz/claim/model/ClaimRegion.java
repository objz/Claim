package dev.objz.claim.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class ClaimRegion {

	private final UUID id;
	private final UUID owner;
	private String name;
	private final String worldName;
	private final BoundingBox region;

	private final Map<ClaimFlag, Boolean> globalFlags = new HashMap<>();
	private final Map<UUID, ClaimRole> members = new HashMap<>();

	private final Map<ClaimRole, Map<ClaimFlag, Boolean>> rolePermissions = new EnumMap<>(ClaimRole.class);

	private final Map<UUID, Map<ClaimFlag, Boolean>> playerFlagOverrides = new HashMap<>();

	public ClaimRegion(UUID owner, String name, Location pos1, Location pos2) {
		this.id = UUID.randomUUID();
		this.owner = owner;
		this.name = name;
		this.worldName = pos1.getWorld().getName();

		int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
		int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
		int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

		int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX()) + 1;
		int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY()) + 1;
		int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ()) + 1;

		this.region = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);

		this.members.put(owner, ClaimRole.OWNER);
		initializeDefaults();
	}

	private void initializeDefaults() {
		for (ClaimFlag flag : ClaimFlag.values()) {
			if (flag.isGlobal()) {
				globalFlags.put(flag, flag.getDefaultValue());
			}
		}

		for (ClaimRole role : ClaimRole.values()) {
			Map<ClaimFlag, Boolean> perms = new HashMap<>();
			for (ClaimFlag flag : ClaimFlag.values()) {
				if (!flag.isGlobal()) {
					if (role == ClaimRole.OWNER || role == ClaimRole.ADMIN) {
						perms.put(flag, true);
					} else if (role == ClaimRole.BUILDER) {
						perms.put(flag, flag == ClaimFlag.BLOCK_BREAK
								|| flag == ClaimFlag.BLOCK_PLACE
								|| flag == ClaimFlag.INTERACT);
					} else {
						perms.put(flag, flag.getDefaultValue());
					}
				}
			}
			rolePermissions.put(role, perms);
		}
	}

	public UUID getId() {
		return id;
	}

	public UUID getOwner() {
		return owner;
	}

	public String getName() {
		return name;
	}

	public BoundingBox getRegion() {
		return region;
	}

	public String getWorldName() {
		return worldName;
	}

	public World getWorld() {
		return Bukkit.getWorld(worldName);
	}

	public boolean contains(Location loc) {
		return loc.getWorld().getName().equals(worldName) && region.contains(loc.toVector());
	}

	public boolean getFlag(ClaimFlag flag) {
		if (!flag.isGlobal())
			throw new IllegalArgumentException("Use getPlayerFlag for non-global flags");
		return globalFlags.getOrDefault(flag, flag.getDefaultValue());
	}

	public void setGlobalFlag(ClaimFlag flag, boolean value) {
		if (!flag.isGlobal())
			return;
		globalFlags.put(flag, value);
	}

	public ClaimRole getRole(UUID player) {
		if (player.equals(owner))
			return ClaimRole.OWNER;
		return members.getOrDefault(player, ClaimRole.VISITOR);
	}

	public void setMemberRole(UUID player, ClaimRole role) {
		if (role == null || role == ClaimRole.VISITOR) {
			members.remove(player);
		} else {
			members.put(player, role);
		}
	}

	public Map<UUID, ClaimRole> getMembers() {
		return Collections.unmodifiableMap(members);
	}

	public boolean getPlayerFlag(UUID player, ClaimFlag flag) {
		if (flag.isGlobal())
			return getFlag(flag);

		if (playerFlagOverrides.containsKey(player) && playerFlagOverrides.get(player).containsKey(flag)) {
			return playerFlagOverrides.get(player).get(flag);
		}

		ClaimRole role = getRole(player);
		return rolePermissions.getOrDefault(role, Collections.emptyMap()).getOrDefault(flag,
				flag.getDefaultValue());
	}

	public void setRolePermission(ClaimRole role, ClaimFlag flag, boolean value) {
		rolePermissions.computeIfAbsent(role, k -> new HashMap<>()).put(flag, value);
	}

	public boolean getRolePermission(ClaimRole role, ClaimFlag flag) {
		return rolePermissions.getOrDefault(role, Collections.emptyMap()).getOrDefault(flag,
				flag.getDefaultValue());
	}
}
