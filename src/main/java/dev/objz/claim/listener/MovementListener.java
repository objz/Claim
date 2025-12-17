package dev.objz.claim.listener;

import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
					Component.text("Entered " + to.get().getName(), NamedTextColor.AQUA));
		} else if (from.isPresent() && to.isEmpty()) {
			event.getPlayer().sendActionBar(
					Component.text("Left " + from.get().getName(), NamedTextColor.YELLOW));
		}
	}
}
