package dev.objz.claim.listener;

import dev.objz.claim.Claim;
import dev.objz.claim.model.GlobalFlags;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class InteractListener extends AbstractListener {

	public InteractListener(Claim plugin) {
		super(plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.PHYSICAL)
			return;
		if (event.getClickedBlock() == null) {
			if (event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
				if (event.getAction() == Action.RIGHT_CLICK_AIR
						|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (!checkPermission(event.getPlayer(), event.getPlayer().getLocation(),
							PlayerFlags.USE_ENDER_PEARL)) {
						event.setCancelled(true);
						sendDenyMessage(event.getPlayer());
					}
				}
			}
			return;
		}

		Block block = event.getClickedBlock();
		Material type = block.getType();

		if (isContainer(type)) {
			if (!checkPermission(event.getPlayer(), block.getLocation(), PlayerFlags.INTERACT_CONTAINERS)) {
				event.setCancelled(true);
				sendDenyMessage(event.getPlayer());
			}
			return;
		}

		if (Tag.DOORS.isTagged(type) || Tag.TRAPDOORS.isTagged(type) || Tag.FENCE_GATES.isTagged(type)) {
			if (!checkPermission(event.getPlayer(), block.getLocation(), PlayerFlags.INTERACT_DOORS)) {
				event.setCancelled(true);
				sendDenyMessage(event.getPlayer());
			}
			return;
		}

		if (isRedstoneInteract(type)) {
			if (!checkPermission(event.getPlayer(), block.getLocation(), PlayerFlags.INTERACT_REDSTONE)) {
				event.setCancelled(true);
				sendDenyMessage(event.getPlayer());
			}
			return;
		}

	}

	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		if (!checkPermission(event.getPlayer(), event.getRightClicked().getLocation(),
				PlayerFlags.INTERACT_ENTITY)) {
			event.setCancelled(true);
			sendDenyMessage(event.getPlayer());
		}
	}

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		if (checkPermission(event.getPlayer(), event.getBlock().getLocation(), PlayerFlags.BUCKET_FILL)) {
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
					SMOKER, HOPPER, DROPPER, DISPENSER, BREWING_STAND ->
				true;
			default -> material.name().endsWith("_SHULKER_BOX");
		};
	}

	private boolean isRedstoneInteract(Material material) {
		return switch (material) {
			case LEVER, DAYLIGHT_DETECTOR, NOTE_BLOCK, REPEATER, COMPARATOR -> true;
			default -> material.name().contains("BUTTON") || material.name().contains("PRESSURE_PLATE");
		};
	}
}
