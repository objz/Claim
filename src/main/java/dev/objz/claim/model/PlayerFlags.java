package dev.objz.claim.model;

import org.bukkit.Material;

public enum PlayerFlags {
	BLOCK_BREAK("Break Blocks", Material.IRON_PICKAXE, false),
	BLOCK_PLACE("Place Blocks", Material.GRASS_BLOCK, false),
	INTERACT("Interact", Material.LEVER, true),
	CONTAINER_ACCESS("Open Containers", Material.CHEST, false),
	DROP_ITEMS("Drop Items", Material.BUNDLE, true),
	PICKUP_ITEMS("Pickup Items", Material.HOPPER, true);

	private final String displayName;
	private final Material icon;
	private final boolean defaultValue;

	PlayerFlags(String displayName, Material icon, boolean defaultValue) {
		this.displayName = displayName;
		this.icon = icon;
		this.defaultValue = defaultValue;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Material getIcon() {
		return icon;
	}

	public boolean getDefaultValue() {
		return defaultValue;
	}
}
