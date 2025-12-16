package dev.objz.claim.command.sub;

import dev.objz.claim.Claim;
import dev.objz.claim.model.ClaimRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import dev.jorel.commandapi.executors.CommandArguments;

import java.util.Optional;

public class InfoCommand {
	private final Claim plugin;

	public InfoCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public void execute(Player player, CommandArguments args) {
		Optional<ClaimRegion> claim = plugin.getClaimManager().getClaimAt(player.getLocation());

		if (claim.isPresent()) {
			if (claim.get().getOwner().equals(player.getUniqueId()) || player.isOp()) {
				plugin.getGuiManager().openMainMenu(player, claim.get());
			} else {
				player.sendMessage(Component.text("You are in claim: " + claim.get().getName(),
						NamedTextColor.AQUA));
				player.sendMessage(Component.text("Owner: " + claim.get().getOwner(),
						NamedTextColor.GRAY));
			}
		} else {
			player.sendMessage(Component.text("No claim at this location.", NamedTextColor.YELLOW));
		}
	}
}
