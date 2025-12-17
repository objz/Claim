package dev.objz.claim.listener;

import dev.objz.claim.Claim;
import dev.objz.claim.model.PlayerFlags;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener extends AbstractListener {

	public InteractListener(Claim plugin) {
		super(plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null)
			return;

		if (isContainer(event.getClickedBlock().getType())) {
			if (!checkPermission(event.getPlayer(), event.getClickedBlock().getLocation(),
					PlayerFlags.CONTAINER_ACCESS)) {
				event.setCancelled(true);
				sendDenyMessage(event.getPlayer());
				return;
			}
		}

		if (!checkPermission(event.getPlayer(), event.getClickedBlock().getLocation(), PlayerFlags.INTERACT)) {
			event.setCancelled(true);
			sendDenyMessage(event.getPlayer());
		}
	}

	private boolean isContainer(Material material) {
		return switch (material) {
			case CHEST, TRAPPED_CHEST, BARREL, SHULKER_BOX, FURNACE, BLAST_FURNACE,
					SMOKER, HOPPER, DROPPER, DISPENSER, BREWING_STAND, ENDER_CHEST ->
				true;
			default -> material.name().endsWith("_SHULKER_BOX");
		};
	}
}
