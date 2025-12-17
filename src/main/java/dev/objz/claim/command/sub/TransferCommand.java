package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.Optional;

public class TransferCommand {
	private final Claim plugin;

	public TransferCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("transfer")
				.withArguments(new EntitySelectorArgument.OnePlayer("player"))
				.executesPlayer((player, args) -> {
					Player target = (Player) args.get("player");
					if (target == null)
						return;

					if (target.getUniqueId().equals(player.getUniqueId())) {
						MessageUtil.sendError(player,
								"You cannot transfer a claim to yourself.");
						return;
					}

					Optional<Region> claimOpt = plugin.getClaimManager()
							.getClaimAt(player.getLocation());

					if (claimOpt.isEmpty()) {
						MessageUtil.sendError(player, "You are not standing in a claim.");
						return;
					}

					Region claim = claimOpt.get();

					if (!claim.getOwner().equals(player.getUniqueId())) {
						MessageUtil.sendError(player, "You do not own this claim.");
						return;
					}

					if (plugin.getClaimManager().getAllClaims().stream()
							.anyMatch(c -> c.getOwner().equals(target.getUniqueId()) && c
									.getName().equalsIgnoreCase(claim.getName()))) {
						MessageUtil.sendError(player,
								target.getName() + " already has a claim named '"
										+ claim.getName() + "'.");
						return;
					}

					plugin.getClaimManager().transferClaim(claim, target.getUniqueId());
					MessageUtil.sendSuccess(player,
							"Successfully transferred <yellow>" + claim.getName()
									+ "</yellow> to <aqua>" + target.getName()
									+ "</aqua>.");
					MessageUtil.sendInfo(target,
							"You have received ownership of claim <yellow>"
									+ claim.getName() + "</yellow> from <aqua>"
									+ player.getName() + "</aqua>.");
				});
	}
}
