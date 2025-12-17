package dev.objz.claim.listener;

import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

public class MovementListener extends AbstractListener {

	public MovementListener(Claim plugin) {
		super(plugin);
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
					MessageUtil.parse("<gray>Entered</gray> <gradient:#00aaff:#00ffaa><bold>"
							+ to.get().getName()
							+ "</bold></gradient>", false));
		} else if (from.isPresent() && to.isEmpty()) {
			event.getPlayer().sendActionBar(
					MessageUtil.parse(
							"<gray>Left</gray> <gradient:#ffaa00:#ff5555><bold>"
									+ from.get().getName() + "</bold></gradient>",
							false));
		}
	}
}
