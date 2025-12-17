package dev.objz.claim.listener;

import dev.objz.claim.Claim;
import dev.objz.claim.model.GlobalFlags;
import dev.objz.claim.model.Region;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Optional;

public class EntityListener extends AbstractListener {

	public EntityListener(Claim plugin) {
		super(plugin);
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
}
