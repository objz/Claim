package dev.objz.claim.model;

import org.bukkit.Material;

public enum PlayerFlags {
	// Column 1: Construction
	BLOCK_BREAK("Break Blocks", Material.IRON_PICKAXE, false),
	BLOCK_PLACE("Place Blocks", Material.GRASS_BLOCK, false),
	BUCKET_FILL("Scoop Liquids", Material.BUCKET, false),

	// Column 2: Interaction
	INTERACT_DOORS("Use Doors", Material.OAK_DOOR, true),
	INTERACT_REDSTONE("Use Redstone", Material.LEVER, false),
	INTERACT_CONTAINERS("Open Containers", Material.CHEST, false),

	// Column 3: Entities
	INTERACT_ENTITY("Interact Entities", Material.SHEARS, false),
	DAMAGE_ENTITY("Damage Entities", Material.IRON_SWORD, false),
	USE_ENDER_PEARL("Use Ender Pearl", Material.ENDER_PEARL, true),

	// Column 4: Items & Misc
	ITEM_DROP("Drop Items", Material.BUNDLE, true),
	ITEM_PICKUP("Pickup Items", Material.HOPPER, true),
	USE_FLINT_AND_STEEL("Use Flint & Steel", Material.FLINT_AND_STEEL, false);

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
