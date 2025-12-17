package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.MessageUtil;

import java.util.Optional;

public class RenameCommand {
	private final Claim plugin;

	public RenameCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("rename")
				.withArguments(new StringArgument("new_name"))
				.executesPlayer((player, args) -> {
					String newName = (String) args.get("new_name");
					if (newName == null || newName.isBlank())
						return;

					Optional<Region> claimOpt = plugin.getClaimManager()
							.getClaimAt(player.getLocation());

					if (claimOpt.isEmpty()) {
						MessageUtil.sendError(player,
								"You must be standing inside a claim to rename it");
						return;
					}

					Region claim = claimOpt.get();

					if (!claim.getOwner().equals(player.getUniqueId()) && !player.isOp()) {
						MessageUtil.sendError(player,
								"You do not have permission to rename this claim");
						return;
					}

					boolean nameExists = plugin.getClaimManager().getAllClaims().stream()
							.anyMatch(c -> c.getOwner().equals(claim.getOwner())
									&& c.getName().equalsIgnoreCase(newName)
									&& !c.getId().equals(claim.getId()));

					if (nameExists) {
						MessageUtil.sendError(player,
								"You already have a claim named '" + newName + "'");
						return;
					}

					String oldName = claim.getName();
					plugin.getClaimManager().renameClaim(claim, newName);
					MessageUtil.sendSuccess(player,
							"Renamed claim from <gray>" + oldName + "</gray> to <yellow>"
									+ newName + "</yellow>");
				});
	}
}
