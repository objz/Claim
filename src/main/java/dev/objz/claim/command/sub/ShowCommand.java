package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class ShowCommand {
	private final Claim plugin;

	public ShowCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("show")
				.executesPlayer((player, args) -> {
					if (plugin.getBorderVisualizer().isActive(player)) {
						plugin.getBorderVisualizer().hideBorder(player);
						player.sendMessage(Component.text("Claim borders hidden",
								NamedTextColor.YELLOW));
						return;
					}

					Optional<Region> claim = plugin.getClaimManager()
							.getClaimAt(player.getLocation());

					if (claim.isPresent()) {
						plugin.getBorderVisualizer().showBorder(player, claim.get().getRegion(),
								claim.get().getWorldName());
						player.sendMessage(Component.text(
								"Claim borders shown.  Type /claim show to hide",
								NamedTextColor.GREEN));
					} else {
						player.sendMessage(Component.text("No claim here to show",
								NamedTextColor.RED));
					}
				});
	}
}
