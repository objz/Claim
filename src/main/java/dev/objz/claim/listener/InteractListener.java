package dev.objz.claim.listener;

import dev.objz.claim.Claim;
import dev.objz.claim.model.GlobalFlags;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

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

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		if (!checkPermission(event.getPlayer(), event.getBlock().getLocation(), PlayerFlags.BLOCK_BREAK)) {
			event.setCancelled(true);
			sendDenyMessage(event.getPlayer());
			return;
		}

		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
		if (claim.isPresent()) {
			Material mat = event.getBlock().getType();
			if ((mat == Material.LAVA) && !claim.get().getFlag(GlobalFlags.LAVA_PICKUP)) {
				event.setCancelled(true);
				sendDenyMessage(event.getPlayer());
			} else if ((mat == Material.WATER) && !claim.get().getFlag(GlobalFlags.WATER_PICKUP)) {
				event.setCancelled(true);
				sendDenyMessage(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		if (!checkPermission(event.getPlayer(), event.getBlock().getLocation(), PlayerFlags.BLOCK_PLACE)) {
			event.setCancelled(true);
			sendDenyMessage(event.getPlayer());
			return;
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
