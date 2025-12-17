package dev.objz.claim.model;

import org.bukkit.Material;

public enum GlobalFlags {
	PVP("PvP", Material.IRON_SWORD, false),
	EXPLOSIONS("Explosions", Material.TNT, false),
	FIRE_SPREAD("Fire Spread", Material.FLINT_AND_STEEL, false),
	MOB_SPAWNING("Mob Spawning", Material.ZOMBIE_HEAD, true);

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
