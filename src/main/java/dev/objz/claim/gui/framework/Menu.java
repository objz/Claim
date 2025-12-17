package dev.objz.claim.gui.framework;

import dev.objz.claim.model.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class Menu implements InventoryHolder {
	protected final Region claim;
	protected final Inventory inventory;
	protected final int size;

	public static final int SIZE_SMALL = 27;
	public static final int SIZE_MEDIUM = 45;
	public static final int SIZE_LARGE = 54;

	public Menu(Region claim, int size, Component title) {
		this.claim = claim;
		this.size = size;
		this.inventory = Bukkit.createInventory(this, size, title);
	}

	@Override
	public @NotNull Inventory getInventory() {
		return inventory;
	}

	protected abstract void build();

	public abstract void handleClick(Player player, int slot, ClickType clickType);

	protected void playClickSound(Player player) {
		player.playSound(player.getLocation(), Sound.UI_STONECUTTER_SELECT_RECIPE, 1f, 1.2f);
	}

	protected void playSuccessSound(Player player) {
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
	}

	protected void playBackSound(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 0.8f);
	}

	protected void playErrorSound(Player player) {
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
	}

	protected void setItem(int slot, Material material, Component name, List<Component> lore) {
		setItem(slot, material, name, lore, false);
	}

	protected void setItem(int slot, Material material, Component name, List<Component> lore, boolean glowing) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();

		meta.displayName(name.decoration(TextDecoration.ITALIC, false));

		if (lore != null && !lore.isEmpty()) {
			meta.lore(lore.stream()
					.map(c -> c.decoration(TextDecoration.ITALIC, false))
					.toList());
		}

		if (glowing) {
			meta.addEnchant(Enchantment.UNBREAKING, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		inventory.setItem(slot, item);
	}

	protected void setItem(int slot, ItemStack item) {
		inventory.setItem(slot, item);
	}

	protected void fillBorders() {
		ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta meta = filler.getItemMeta();
		meta.displayName(Component.empty());
		filler.setItemMeta(meta);

		for (int i = 0; i < size; i++) {
			if (inventory.getItem(i) == null) {
				inventory.setItem(i, filler);
			}
		}
	}

	public Region getClaim() {
		return claim;
	}

	public enum ClickType {
		LEFT,
		RIGHT,
		SHIFT_LEFT,
		SHIFT_RIGHT,
		MIDDLE,
		OTHER
	}
}
