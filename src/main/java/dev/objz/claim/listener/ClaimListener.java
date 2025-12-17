package dev.objz.claim.listener;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dev.objz.claim.Claim;
import dev.objz.claim.model.GlobalFlags;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.HeadUtil;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

import java.util.Optional;

public class ClaimListener implements Listener {

	private final Claim plugin;

	public ClaimListener(Claim plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		// Cache player texture
		PlayerProfile profile = event.getPlayer().getPlayerProfile();
		for (ProfileProperty property : profile.getProperties()) {
			if ("textures".equals(property.getName())) {
				HeadUtil.cacheTexture(event.getPlayer().getUniqueId(), property.getValue());
				break;
			}
		}
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

	@EventHandler
	public void onPvp(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getEntity().getLocation());
			if (claim.isPresent() && !claim.get().getFlag(GlobalFlags.PVP)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getLocation());
		if (claim.isPresent() && !claim.get().getFlag(GlobalFlags.EXPLOSIONS)) {
			event.setCancelled(true);
			event.blockList().clear();
		}
	}

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent event) {
		if (event.getEntity() instanceof Monster
				&& event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
			Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getLocation());
			if (claim.isPresent() && !claim.get().getFlag(GlobalFlags.MOB_SPAWNING)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEnterClaim(PlayerMoveEvent event) {
		if (event.getFrom().getBlockX() == event.getTo().getBlockX()
				&& event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return;

		Optional<Region> to = plugin.getClaimManager().getClaimAt(event.getTo());
		Optional<Region> from = plugin.getClaimManager().getClaimAt(event.getFrom());

		if (to.isPresent() && (from.isEmpty() || !from.get().getId().equals(to.get().getId()))) {
			event.getPlayer().sendActionBar(
					Component.text("Entered " + to.get().getName(), NamedTextColor.AQUA));
		} else if (from.isPresent() && to.isEmpty()) {
			event.getPlayer().sendActionBar(
					Component.text("Left " + from.get().getName(), NamedTextColor.YELLOW));
		}
	}

	private boolean checkPermission(Player player, Location loc, PlayerFlags flag) {
		Optional<Region> claimOpt = plugin.getClaimManager().getClaimAt(loc);
		if (claimOpt.isEmpty())
			return true;

		Region claim = claimOpt.get();
		return claim.getFlag(player.getUniqueId(), flag);
	}

	private void sendDenyMessage(Player player) {
		player.sendActionBar(Component.text("You do not have permission to do that here.", NamedTextColor.RED));
	}

	private boolean isContainer(org.bukkit.Material material) {
		return switch (material) {
			case CHEST, TRAPPED_CHEST, BARREL, SHULKER_BOX, FURNACE, BLAST_FURNACE,
					SMOKER, HOPPER, DROPPER, DISPENSER, BREWING_STAND, ENDER_CHEST ->
				true;
			default -> material.name().endsWith("_SHULKER_BOX");
		};
	}
}
