package dev.objz.claim.gui.menu;

import dev.objz.claim.model.Region;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ClaimGuiHolder implements InventoryHolder {
	protected final Region claim;
	protected final Inventory inventory;

	protected static final int SIZE_SMALL = 27;
	protected static final int SIZE_LARGE = 54;

	public ClaimGuiHolder(Region claim, int size, Component title) {
		this.claim = claim;
		this.inventory = Bukkit.createInventory(this, size, title);
	}

	@Override
	public @NotNull Inventory getInventory() {
		return inventory;
	}

	public abstract void handleClick(Player player, int slot);

	protected void setItem(int slot, Material mat, String name, List<Component> lore) {
		setItem(slot, mat, name, lore, false);
	}

	protected void setItem(int slot, Material mat, String name, List<Component> lore, boolean glowing) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();

		meta.displayName(Component.text(name, NamedTextColor.AQUA)
				.decoration(TextDecoration.ITALIC, false));

		if (lore != null) {
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

	protected void setCustomHead(int slot, String base64, String name, List<Component> lore) {
		// Fix: Added 'true' as the 4th argument to target the List<Component> overload in HeadUtil
		inventory.setItem(slot, HeadUtil.getCustomHead(name, base64, lore));
	}

	protected void fillBorders() {
		ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta meta = filler.getItemMeta();
		meta.displayName(Component.empty());
		filler.setItemMeta(meta);

		int size = inventory.getSize();

		for (int i = 0; i < size; i++) {
			if (inventory.getItem(i) == null) {
				inventory.setItem(i, filler);
			}
		}
	}

	protected void addBackButton(int slot) {
		setCustomHead(slot, HeadUtil.ARROW_LEFT, "Back", 
            List.of(Component.text("Return to previous menu", NamedTextColor.GRAY)));
	}
}
