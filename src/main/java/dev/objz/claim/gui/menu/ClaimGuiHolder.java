package dev.objz.claim.gui.menu;

import dev.objz.claim.model.ClaimRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ClaimGuiHolder implements InventoryHolder {
	protected final ClaimRegion claim;
	protected final Inventory inventory;

	public ClaimGuiHolder(ClaimRegion claim, int size, Component title) {
		this.claim = claim;
		this.inventory = Bukkit.createInventory(this, size, title);
	}

	@Override
	public @NotNull Inventory getInventory() {
		return inventory;
	}

	public abstract void handleClick(Player player, int slot);

	protected void setItem(int slot, Material mat, String name, List<String> lore) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.displayName(Component.text(name, NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
		if (lore != null) {
			meta.lore(lore.stream()
					.map(l -> Component.text(l, NamedTextColor.GRAY)
							.decoration(TextDecoration.ITALIC, false))
					.toList());
		}
		item.setItemMeta(meta);
		inventory.setItem(slot, item);
	}
}
