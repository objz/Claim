package dev.objz.claim.listener;

import dev.objz.claim.Claim;
import dev.objz.claim.model.PlayerFlags;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener extends AbstractListener {

	public BlockListener(Claim plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBreak(BlockBreakEvent event) {
		if (!checkPermission(event.getPlayer(), event.getBlock().getLocation(), PlayerFlags.BLOCK_BREAK)) {
			event.setCancelled(true);
			sendDenyMessage(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlace(BlockPlaceEvent event) {
		if (!checkPermission(event.getPlayer(), event.getBlock().getLocation(), PlayerFlags.BLOCK_PLACE)) {
			event.setCancelled(true);
			sendDenyMessage(event.getPlayer());
		}
	}
}
