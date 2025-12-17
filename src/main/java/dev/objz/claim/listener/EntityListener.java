package dev.objz.claim.listener;

import dev.objz.claim.Claim;
import dev.objz.claim.model.GlobalFlags;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.*;

import java.util.Optional;

public class EntityListener extends AbstractListener {

	public EntityListener(Claim plugin) {
		super(plugin);
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		Optional<Region> claimOpt = plugin.getClaimManager().getClaimAt(event.getLocation());
		if (claimOpt.isEmpty())
			return;
		Region claim = claimOpt.get();

		Entity entity = event.getEntity();
		boolean isBlockSource = entity instanceof TNTPrimed;
		boolean isWindCharge = entity.getType().name().contains("WIND_CHARGE");

		if (isWindCharge) {
			if (!claim.getFlag(GlobalFlags.WIND_CHARGE)) {
				event.setCancelled(true);
				return;
			}
		} else if (isBlockSource) {
			if (!claim.getFlag(GlobalFlags.BLOCK_EXPLOSIONS)) {
				event.setCancelled(true);
				return;
			}
			if (!claim.getFlag(GlobalFlags.BLOCK_EXPLOSION_BLOCK_DAMAGE)) {
				event.blockList().clear();
			}
		} else {
			if (!claim.getFlag(GlobalFlags.ENTITY_EXPLOSIONS)) {
				event.setCancelled(true);
				return;
			}
			if (!claim.getFlag(GlobalFlags.ENTITY_EXPLOSION_BLOCK_DAMAGE)) {
				event.blockList().clear();
			}
		}
	}

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM)
			return;

		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getLocation());
		if (claim.isPresent()) {
			if (event.getEntity() instanceof Monster) {
				if (!claim.get().getFlag(GlobalFlags.MONSTER_SPAWNING)) {
					event.setCancelled(true);
				}
			} else if (event.getEntity() instanceof Animals || event.getEntity() instanceof Ambient
					|| event.getEntity() instanceof WaterMob) {
				if (!claim.get().getFlag(GlobalFlags.ANIMAL_SPAWNING)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onEntityGrief(EntityChangeBlockEvent event) {
		if (event.getEntity() instanceof Player)
			return;

		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
		if (claim.isPresent()) {
			if (!claim.get().getFlag(GlobalFlags.ENTITY_GRIEF)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityInteract(EntityInteractEvent event) {
		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
		if (claim.isPresent()) {
			if (event.getBlock().getType().name().equals("FARMLAND")) {
				if (!claim.get().getFlag(GlobalFlags.CROP_TRAMPLING)) {
					event.setCancelled(true);
				}
			} else {
				if (!claim.get().getFlag(GlobalFlags.ENTITY_INTERACT)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getEntity().getLocation());
		if (claim.isEmpty())
			return;

		if (event instanceof EntityDamageByEntityEvent edbe) {
			Entity damager = edbe.getDamager();
			if (damager.getType().name().contains("WIND_CHARGE")) {
				if (!claim.get().getFlag(GlobalFlags.WIND_CHARGE)) {
					event.setCancelled(true);
					return;
				}
			}

			if (damager instanceof Player p) {
				if (checkPermission(p, event.getEntity().getLocation(), PlayerFlags.DAMAGE_ENTITY)) {
					return;
				} else {
					event.setCancelled(true);
					sendDenyMessage(p);
					return;
				}
			}
		}

		if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
				|| event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {

			if (event instanceof EntityDamageByEntityEvent edbe) {
				Entity damager = edbe.getDamager();
				if (damager instanceof TNTPrimed) {
					if (!claim.get().getFlag(GlobalFlags.BLOCK_EXPLOSION_ENTITY_DAMAGE)) {
						event.setCancelled(true);
						return;
					}
				} else {
					if (!claim.get().getFlag(GlobalFlags.ENTITY_EXPLOSION_ENTITY_DAMAGE)) {
						event.setCancelled(true);
						return;
					}
				}
			} else if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
				if (!claim.get().getFlag(GlobalFlags.BLOCK_EXPLOSION_ENTITY_DAMAGE)) {
					event.setCancelled(true);
					return;
				}
			}
		}

		if (!(event.getEntity() instanceof Player)) {
			if (!(event instanceof EntityDamageByEntityEvent edbe && edbe.getDamager() instanceof Player)) {
				if (!claim.get().getFlag(GlobalFlags.ENTITY_DAMAGE)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if (event.getTarget() instanceof Player) {
			Optional<Region> claim = plugin.getClaimManager().getClaimAt(event.getEntity().getLocation());
			if (claim.isPresent() && !claim.get().getFlag(GlobalFlags.ENTITY_TARGET_PLAYER)) {
				event.setCancelled(true);
			}
		}
	}
}
