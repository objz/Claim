package dev.objz.claim.model;

import org.bukkit.Material;

public enum ClaimFlag {
	// Global Settings
	PVP("PvP", Material.IRON_SWORD, true, false),
	EXPLOSIONS("Explosions", Material.TNT, true, false),
	FIRE_SPREAD("Fire Spread", Material.FLINT_AND_STEEL, true, false),
	MOB_SPAWNING("Mob Spawning", Material.ZOMBIE_HEAD, true, true),

	// Player Permissions
	BLOCK_BREAK("Break Blocks", Material.IRON_PICKAXE, false, false),
	BLOCK_PLACE("Place Blocks", Material.GRASS_BLOCK, false, false),
	INTERACT("Interact", Material.LEVER, false, true),
	CONTAINER_ACCESS("Open Containers", Material.CHEST, false, false),
	DROP_ITEMS("Drop Items", Material.BUNDLE, false, true),
	PICKUP_ITEMS("Pickup Items", Material.HOPPER, false, true);

	private final String displayName;
	private final Material icon;
	private final boolean isGlobal;
	private final boolean defaultValue;

	ClaimFlag(String displayName, Material icon, boolean isGlobal, boolean defaultValue) {
		this.displayName = displayName;
		this.icon = icon;
		this.isGlobal = isGlobal;
		this.defaultValue = defaultValue;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Material getIcon() {
		return icon;
	}

	public boolean isGlobal() {
		return isGlobal;
	}

	public boolean getDefaultValue() {
		return defaultValue;
	}
}
