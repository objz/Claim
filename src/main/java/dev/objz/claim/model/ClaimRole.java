package dev.objz.claim.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum ClaimRole {
	OWNER("Owner", NamedTextColor.RED, 100),
	ADMIN("Admin", NamedTextColor.GOLD, 50),
	BUILDER("Builder", NamedTextColor.BLUE, 20),
	SPECTATOR("Spectator", NamedTextColor.GRAY, 10),
	VISITOR("Visitor", NamedTextColor.WHITE, 0);

	private final String displayName;
	private final NamedTextColor color;
	private final int priority;

	ClaimRole(String displayName, NamedTextColor color, int priority) {
		this.displayName = displayName;
		this.color = color;
		this.priority = priority;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Component getFormattedName() {
		return Component.text(displayName, color);
	}

	public boolean isAtLeast(ClaimRole other) {
		return this.priority >= other.priority;
	}
}
