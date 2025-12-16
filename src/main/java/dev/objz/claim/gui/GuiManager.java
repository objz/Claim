package dev.objz.claim.gui;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.menu.ClaimGuiHolder;
import dev.objz.claim.gui.menu.MainMenu;
import dev.objz.claim.model.ClaimRegion;
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
		if (event.getInventory().getHolder() instanceof ClaimGuiHolder holder) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null) {
				holder.handleClick((Player) event.getWhoClicked(), event.getSlot());
			}
		}
	}

	public void openMainMenu(Player player, ClaimRegion claim) {
		player.openInventory(new MainMenu(plugin, claim, player).getInventory());
	}
}
