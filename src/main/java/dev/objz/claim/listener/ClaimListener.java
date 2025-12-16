package dev.objz.claim.listener;

import dev.objz.claim.Claim;
import dev.objz.claim.model.ClaimFlag;
import dev.objz.claim.model.ClaimRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

public class ClaimListener implements Listener {

	private final Claim plugin;

	public ClaimListener(Claim plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBreak(BlockBreakEvent event) {
		if (!checkPermission(event.getPlayer(), event.getBlock().getLocation(), ClaimFlag.BLOCK_BREAK)) {
			event.setCancelled(true);
			sendDenyMessage(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlace(BlockPlaceEvent event) {
		if (!checkPermission(event.getPlayer(), event.getBlock().getLocation(), ClaimFlag.BLOCK_PLACE)) {
			event.setCancelled(true);
			sendDenyMessage(event.getPlayer());
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null)
			return;
		if (!checkPermission(event.getPlayer(), event.getClickedBlock().getLocation(), ClaimFlag.INTERACT)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPvp(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Optional<ClaimRegion> claim = plugin.getClaimManager()
					.getClaimAt(event.getEntity().getLocation());
			if (claim.isPresent() && !claim.get().getFlag(ClaimFlag.PVP)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		Optional<ClaimRegion> claim = plugin.getClaimManager().getClaimAt(event.getLocation());
		if (claim.isPresent() && !claim.get().getFlag(ClaimFlag.EXPLOSIONS)) {
			event.setCancelled(true);
			event.blockList().clear();
		}
	}

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent event) {
		if (event.getEntity() instanceof Monster
				&& event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
			Optional<ClaimRegion> claim = plugin.getClaimManager().getClaimAt(event.getLocation());
			if (claim.isPresent() && !claim.get().getFlag(ClaimFlag.MOB_SPAWNING)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEnterClaim(PlayerMoveEvent event) {
		if (event.getFrom().getBlockX() == event.getTo().getBlockX()
				&& event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return;

		Optional<ClaimRegion> to = plugin.getClaimManager().getClaimAt(event.getTo());
		Optional<ClaimRegion> from = plugin.getClaimManager().getClaimAt(event.getFrom());

		if (to.isPresent() && (from.isEmpty() || !from.get().getId().equals(to.get().getId()))) {
			event.getPlayer().sendActionBar(
					Component.text("Entered " + to.get().getName(), NamedTextColor.AQUA));
		} else if (from.isPresent() && to.isEmpty()) {
			event.getPlayer().sendActionBar(
					Component.text("Left " + from.get().getName(), NamedTextColor.YELLOW));
		}
	}

	private boolean checkPermission(Player player, Location loc, ClaimFlag flag) {
		Optional<ClaimRegion> claimOpt = plugin.getClaimManager().getClaimAt(loc);
		if (claimOpt.isEmpty())
			return true;

		ClaimRegion claim = claimOpt.get();
		return claim.getPlayerFlag(player.getUniqueId(), flag);
	}

	private void sendDenyMessage(Player player) {
		player.sendActionBar(Component.text("You do not have permission to do that here.", NamedTextColor.RED));
	}
}
