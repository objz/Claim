package dev.objz.claim.listener;

import dev.objz.claim.Claim;
import dev.objz.claim.model.PlayerFlags;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemListener extends AbstractListener {

	public ItemListener(Claim plugin) {
		super(plugin);
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		if (!checkPermission(event.getPlayer(), event.getPlayer().getLocation(), PlayerFlags.DROP_ITEMS)) {
			event.setCancelled(true);
			sendDenyMessage(event.getPlayer());
		}
	}

	@EventHandler
	public void onPickupItem(PlayerAttemptPickupItemEvent event) {
		if (!checkPermission(event.getPlayer(), event.getItem().getLocation(), PlayerFlags.PICKUP_ITEMS)) {
			event.setCancelled(true);
		}
	}
}
