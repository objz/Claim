package dev.objz.claim.gui;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.Menu;
import dev.objz.claim.gui.menus.main.MainMenu;
import dev.objz.claim.model.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiManager implements Listener {
	private final Claim plugin;

	public GuiManager(Claim plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getInventory().getHolder() instanceof Menu menu) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getWhoClicked() instanceof Player player) {
				Menu.ClickType clickType = convertClickType(event.getClick());
				menu.handleClick(player, event.getSlot(), clickType);
			}
		}
	}

	private Menu.ClickType convertClickType(org.bukkit.event.inventory.ClickType bukkitClickType) {
		return switch (bukkitClickType) {
			case LEFT -> Menu.ClickType.LEFT;
			case RIGHT -> Menu.ClickType.RIGHT;
			case SHIFT_LEFT -> Menu.ClickType.SHIFT_LEFT;
			case SHIFT_RIGHT -> Menu.ClickType.SHIFT_RIGHT;
			case MIDDLE -> Menu.ClickType.MIDDLE;
			default -> Menu.ClickType.OTHER;
		};
	}

	public void openMainMenu(Player player, Region claim) {
		player.openInventory(new MainMenu(plugin, claim).getInventory());
	}
}
