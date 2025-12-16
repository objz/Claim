package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.objz.claim.Claim;
import dev.objz.claim.model.ClaimRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class DeleteCommand {
	private final Claim plugin;

	public DeleteCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("delete")
				.executesPlayer((player, args) -> {
					Optional<ClaimRegion> claim = plugin.getClaimManager()
							.getClaimAt(player.getLocation());

					if (claim.isEmpty()) {
						player.sendMessage(Component.text("You are not standing in a claim.",
								NamedTextColor.RED));
						return;
					}

					if (!claim.get().getOwner().equals(player.getUniqueId()) && !player.isOp()) {
						player.sendMessage(Component.text(
								"You do not have permission to delete this claim.",
								NamedTextColor.RED));
						return;
					}

					plugin.getClaimManager().deleteClaim(claim.get().getId());
					plugin.getBorderVisualizer().resetBorder(player);

					player.sendMessage(Component.text(
							"Claim '" + claim.get().getName() + "' has been deleted.",
							NamedTextColor.GREEN));
				});
	}
}
