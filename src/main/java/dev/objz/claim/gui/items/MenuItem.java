package dev.objz.claim.gui.items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.List;

public class MenuItem {
	private final Material material;
	private final Component name;
	private final List<Component> lore;
	private final boolean glowing;
	private final String headTexture;

	private MenuItem(Builder builder) {
		this.material = builder.material;
		this.name = builder.name;
		this.lore = builder.lore;
		this.glowing = builder.glowing;
		this.headTexture = builder.headTexture;
	}

	public Material getMaterial() {
		return material;
	}

	public Component getName() {
		return name;
	}

	public List<Component> getLore() {
		return lore;
	}

	public boolean isGlowing() {
		return glowing;
	}

	public String getHeadTexture() {
		return headTexture;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Material material = Material.STONE;
		private Component name;
		private List<Component> lore;
		private boolean glowing = false;
		private String headTexture;

		public Builder material(Material material) {
			this.material = material;
			return this;
		}

		public Builder name(Component name) {
			this.name = name;
			return this;
		}

		public Builder lore(List<Component> lore) {
			this.lore = lore;
			return this;
		}

		public Builder glowing(boolean glowing) {
			this.glowing = glowing;
			return this;
		}

		public Builder headTexture(String texture) {
			this.headTexture = texture;
			this.material = Material.PLAYER_HEAD;
			return this;
		}

		public MenuItem build() {
			return new MenuItem(this);
		}
	}
}
