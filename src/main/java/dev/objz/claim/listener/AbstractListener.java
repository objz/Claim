package dev.objz.claim.listener;

import dev.objz.claim.Claim;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Optional;

public abstract class AbstractListener implements Listener {
	protected final Claim plugin;

	protected AbstractListener(Claim plugin) {
		this.plugin = plugin;
	}

	protected boolean checkPermission(Player player, Location loc, PlayerFlags flag) {
		Optional<Region> claimOpt = plugin.getClaimManager().getClaimAt(loc);
		if (claimOpt.isEmpty())
			return true;

		Region claim = claimOpt.get();
		return claim.getFlag(player.getUniqueId(), flag);
	}

	protected void sendDenyMessage(Player player) {
		player.sendActionBar(Component.text("You do not have permission to do that here", NamedTextColor.RED));
	}
}
