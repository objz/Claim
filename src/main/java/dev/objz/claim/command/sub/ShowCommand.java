package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.MessageUtil;

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
						MessageUtil.sendInfo(player, "Claim borders hidden.");
						return;
					}

					Optional<Region> claim = plugin.getClaimManager()
							.getClaimAt(player.getLocation());

					if (claim.isPresent()) {
						plugin.getBorderVisualizer().showBorder(player, claim.get().getRegion(),
								claim.get().getWorldName());
						MessageUtil.sendSuccess(player,
								"Claim borders shown. Type <yellow>/claim show</yellow> to hide.");
					} else {
						MessageUtil.sendError(player, "No claim here to show.");
					}
				});
	}
}
