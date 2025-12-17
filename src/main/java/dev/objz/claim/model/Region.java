package dev.objz.claim.model;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class Region {

	private final UUID id;
	private UUID owner;
	private String name;
	private final String worldName;
	private BoundingBox region;

	private final Map<GlobalFlags, Boolean> globalFlags = new EnumMap<>(GlobalFlags.class);
	private final Map<UUID, Roles> members = new HashMap<>();
	private final Map<Roles, Map<PlayerFlags, Boolean>> rolePermissions = new EnumMap<>(Roles.class);
	private final Map<UUID, Map<PlayerFlags, Boolean>> playerFlagOverrides = new HashMap<>();

	public Region(UUID owner, String name, Location pos1, Location pos2) {
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

		this.members.put(owner, Roles.OWNER);
		initializeDefaults();
	}

	public UUID getId() {
		return id;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.members.remove(this.owner);
		this.owner = owner;
		this.members.put(owner, Roles.OWNER);
	}

	public String getName() {
		return name;
	}

	public String getWorldName() {
		return worldName;
	}

	public BoundingBox getRegion() {
		return region;
	}

	public void resize(BoundingBox newRegion) {
		this.region = newRegion;
	}

	public boolean contains(Location loc) {
		return loc.getWorld().getName().equals(worldName) && region.contains(loc.toVector());
	}

	public boolean getFlag(GlobalFlags flag) {
		return globalFlags.getOrDefault(flag, flag.getDefaultValue());
	}

	public void setFlag(GlobalFlags flag, boolean value) {
		globalFlags.put(flag, value);
	}

	public boolean getFlag(UUID player, PlayerFlags flag) {
		if (playerFlagOverrides.containsKey(player) && playerFlagOverrides.get(player).containsKey(flag)) {
			return playerFlagOverrides.get(player).get(flag);
		}

		Roles role = getRole(player);
		return rolePermissions.getOrDefault(role, Collections.emptyMap()).getOrDefault(flag,
				flag.getDefaultValue());
	}

	public Boolean getPlayerFlagOverride(UUID player, PlayerFlags flag) {
		if (playerFlagOverrides.containsKey(player)) {
			return playerFlagOverrides.get(player).get(flag);
		}
		return null;
	}

	public void setPlayerFlag(UUID player, PlayerFlags flag, Boolean value) {
		if (value == null) {
			if (playerFlagOverrides.containsKey(player)) {
				playerFlagOverrides.get(player).remove(flag);
				if (playerFlagOverrides.get(player).isEmpty()) {
					playerFlagOverrides.remove(player);
				}
			}
		} else {
			playerFlagOverrides.computeIfAbsent(player, k -> new EnumMap<>(PlayerFlags.class)).put(flag,
					value);
		}
	}

	public void setFlag(Roles role, PlayerFlags flag, boolean value) {
		rolePermissions.computeIfAbsent(role, k -> new EnumMap<>(PlayerFlags.class)).put(flag, value);
	}

	public boolean getFlag(Roles role, PlayerFlags flag) {
		return rolePermissions.getOrDefault(role, Collections.emptyMap()).getOrDefault(flag,
				flag.getDefaultValue());
	}

	public Roles getRole(UUID player) {
		if (player.equals(owner))
			return Roles.OWNER;
		return members.getOrDefault(player, Roles.VISITOR);
	}

	public void setRole(UUID player, Roles role) {
		if (role == null) {
			members.remove(player);
		} else {
			members.put(player, role);
		}
	}

	public Map<UUID, Roles> getMembers() {
		return Collections.unmodifiableMap(members);
	}

	public Map<UUID, Map<PlayerFlags, Boolean>> getPlayerFlagOverrides() {
		return Collections.unmodifiableMap(playerFlagOverrides);
	}

	private void initializeDefaults() {
		for (GlobalFlags flag : GlobalFlags.values()) {
			globalFlags.put(flag, flag.getDefaultValue());
		}

		for (Roles role : Roles.values()) {
			Map<PlayerFlags, Boolean> perms = new EnumMap<>(PlayerFlags.class);
			for (PlayerFlags flag : PlayerFlags.values()) {
				if (role == Roles.OWNER || role == Roles.ADMIN) {
					perms.put(flag, true);
				} else if (role == Roles.BUILDER) {
					boolean builderPerm = switch (flag) {
						case BLOCK_BREAK, BLOCK_PLACE, BUCKET_FILL -> true;
						case INTERACT_DOORS, INTERACT_REDSTONE, INTERACT_CONTAINERS -> true;
						case ITEM_DROP, ITEM_PICKUP, USE_ENDER_PEARL -> true;
						default -> false;
					};
					perms.put(flag, builderPerm);
				} else {
					perms.put(flag, flag.getDefaultValue());
				}
			}
			rolePermissions.put(role, perms);
		}
	}
}
