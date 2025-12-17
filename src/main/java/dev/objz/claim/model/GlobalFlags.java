package dev.objz.claim.model;

import org.bukkit.Material;

public enum GlobalFlags {
	// Row 1: Explosions & Force
	BLOCK_EXPLOSIONS("Block Explosions", Material.TNT, false),
	BLOCK_EXPLOSION_BLOCK_DAMAGE("Block Exp. Damage", Material.COBBLESTONE, false),
	BLOCK_EXPLOSION_ENTITY_DAMAGE("Block Exp. Hurt", Material.LEATHER_CHESTPLATE, true),
	ENTITY_EXPLOSIONS("Entity Explosions", Material.CREEPER_HEAD, false),
	ENTITY_EXPLOSION_BLOCK_DAMAGE("Entity Exp. Damage", Material.DIRT, false),
	ENTITY_EXPLOSION_ENTITY_DAMAGE("Entity Exp. Hurt", Material.IRON_CHESTPLATE, true),
	WIND_CHARGE("Wind Charge", Material.FEATHER, false),

	// Row 2: Fire & Fluids
	FIRE_SPREAD("Fire Spread", Material.FLINT_AND_STEEL, false),
	FIRE_IGNITION("Fire Ignition", Material.FIRE_CHARGE, false),
	LAVA_FLOW("Lava Flow", Material.LAVA_BUCKET, false),
	WATER_FLOW("Water Flow", Material.WATER_BUCKET, true),
	LAVA_PICKUP("Lava Pickup", Material.BUCKET, false),
	WATER_PICKUP("Water Pickup", Material.BUCKET, true),
	SPONGE_ABSORB("Sponge Absorb", Material.SPONGE, true),

	// Row 3: Entities & AI
	MONSTER_SPAWNING("Monster Spawning", Material.ZOMBIE_HEAD, false),
	ANIMAL_SPAWNING("Animal Spawning", Material.PIG_SPAWN_EGG, true),
	ENTITY_DAMAGE("Entity Damage", Material.RED_DYE, true),
	ENTITY_GRIEF("Entity Griefing", Material.GRASS_BLOCK, false),
	ENTITY_INTERACT("Entity Interact", Material.OAK_PRESSURE_PLATE, false),
	ENTITY_DESPAWN("Entity Despawn", Material.NAME_TAG, true),
	ENTITY_TARGET_PLAYER("Target Players", Material.PLAYER_HEAD, false),

	// Row 4: Growth & Natural Ticks
	TREE_GROWTH("Tree Growth", Material.OAK_SAPLING, true),
	CROP_GROWTH("Crop Growth", Material.WHEAT, true),
	CROP_TRAMPLING("Crop Trampling", Material.FARMLAND, false),
	VINE_GROWTH("Vine Growth", Material.VINE, true),
	LEAF_DECAY("Leaf Decay", Material.OAK_LEAVES, true),
	MUSHROOM_SPREAD("Mushroom Spread", Material.RED_MUSHROOM, true),
	GRASS_SPREAD("Grass Spread", Material.MOSS_BLOCK, true);

	private final String displayName;
	private final Material icon;
	private final boolean defaultValue;

	GlobalFlags(String displayName, Material icon, boolean defaultValue) {
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
