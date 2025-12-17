package dev.objz.claim.listener;

import dev.objz.claim.Claim;
import dev.objz.claim.model.GlobalFlags;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.Optional;

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
			return;
		}

		// Sponge check
		if (event.getBlock().getType() == Material.SPONGE
				|| event.getBlock().getType() == Material.WET_SPONGE) {
			Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
			if (claim.isPresent() && !claim.get().getFlag(GlobalFlags.SPONGE_ABSORB)) {
				// We don't cancel placement here as we can't easily predict absorb,
				// but absorption event will handle it.
			}
		}
	}

	@EventHandler
	public void onSpongeAbsorb(SpongeAbsorbEvent event) {
		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
		if (claim.isPresent() && !claim.get().getFlag(GlobalFlags.SPONGE_ABSORB)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onTrample(PlayerInteractEvent event) {
		if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null) {
			if (event.getClickedBlock().getType() == Material.FARMLAND) {
				Optional<Region> claim = plugin.getClaimManager()
						.getClaimAt(event.getClickedBlock().getLocation());
				if (claim.isPresent() && !claim.get().getFlag(GlobalFlags.CROP_TRAMPLING)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		Optional<Region> claimOpt = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
		if (claimOpt.isEmpty())
			return;
		Region claim = claimOpt.get();

		if (!claim.getFlag(GlobalFlags.BLOCK_EXPLOSIONS)) {
			event.setCancelled(true);
			return;
		}

		if (!claim.getFlag(GlobalFlags.BLOCK_EXPLOSION_BLOCK_DAMAGE)) {
			event.blockList().clear();
		}
	}

	@EventHandler
	public void onIgnite(BlockIgniteEvent event) {
		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
		if (claim.isEmpty())
			return;

		// Check for player ignition bypass
		if (event.getPlayer() != null) {
			if (checkPermission(event.getPlayer(), event.getBlock().getLocation(),
					PlayerFlags.USE_FLINT_AND_STEEL)) {
				return;
			}
		}

		if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
			if (!claim.get().getFlag(GlobalFlags.FIRE_SPREAD)) {
				event.setCancelled(true);
			}
		} else {
			if (!claim.get().getFlag(GlobalFlags.FIRE_IGNITION)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBurn(BlockBurnEvent event) {
		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
		if (claim.isPresent() && !claim.get().getFlag(GlobalFlags.FIRE_SPREAD)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onLiquidFlow(BlockFromToEvent event) {
		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getToBlock().getLocation());
		if (claim.isEmpty())
			return;

		Material type = event.getBlock().getType();
		if (type == Material.LAVA || type == Material.LAVA_BUCKET) {
			if (!claim.get().getFlag(GlobalFlags.LAVA_FLOW)) {
				event.setCancelled(true);
			}
		} else if (type == Material.WATER || type == Material.WATER_BUCKET) {
			if (!claim.get().getFlag(GlobalFlags.WATER_FLOW)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onTreeGrow(StructureGrowEvent event) {
		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getLocation());
		if (claim.isPresent() && !claim.get().getFlag(GlobalFlags.TREE_GROWTH)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockGrow(BlockGrowEvent event) {
		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
		if (claim.isEmpty())
			return;

		if (Tag.CROPS.isTagged(event.getNewState().getType()) ||
				event.getNewState().getType() == Material.SUGAR_CANE ||
				event.getNewState().getType() == Material.CACTUS ||
				event.getNewState().getType() == Material.BAMBOO) {
			if (!claim.get().getFlag(GlobalFlags.CROP_GROWTH)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
		if (claim.isEmpty())
			return;

		Material type = event.getSource().getType();
		if (type == Material.VINE || type == Material.WEEPING_VINES || type == Material.TWISTING_VINES) {
			if (!claim.get().getFlag(GlobalFlags.VINE_GROWTH)) {
				event.setCancelled(true);
			}
		} else if (type == Material.RED_MUSHROOM || type == Material.BROWN_MUSHROOM) {
			if (!claim.get().getFlag(GlobalFlags.MUSHROOM_SPREAD)) {
				event.setCancelled(true);
			}
		} else if (type == Material.GRASS_BLOCK || type == Material.MYCELIUM) {
			if (!claim.get().getFlag(GlobalFlags.GRASS_SPREAD)) {
				event.setCancelled(true);
			}
		} else if (type == Material.FIRE) {
			if (!claim.get().getFlag(GlobalFlags.FIRE_SPREAD)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onLeafDecay(LeavesDecayEvent event) {
		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
		if (claim.isPresent() && !claim.get().getFlag(GlobalFlags.LEAF_DECAY)) {
			event.setCancelled(true);
		}
	}
}
