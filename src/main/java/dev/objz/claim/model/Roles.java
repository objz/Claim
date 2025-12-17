package dev.objz.claim.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum Roles {
	OWNER("Owner", NamedTextColor.RED, 40),
	ADMIN("Admin", NamedTextColor.GOLD, 30),
	BUILDER("Builder", NamedTextColor.BLUE, 20),
	VISITOR("Visitor", NamedTextColor.GRAY, 10);

	private final String displayName;
	private final NamedTextColor color;
	private final int priority;

	Roles(String displayName, NamedTextColor color, int priority) {
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

	public boolean isAtLeast(Roles other) {
		return this.priority >= other.priority;
	}
}
