package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.objz.claim.Claim;
import dev.objz.claim.model.GlobalFlags;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.MessageUtil;

import java.util.Arrays;
import java.util.Optional;

public class FlagCommand {
	private final Claim plugin;

	public FlagCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("flag")
				.withArguments(new StringArgument("flag_name")
						.replaceSuggestions(ArgumentSuggestions.strings(
								Arrays.stream(GlobalFlags.values())
										.map(Enum::name)
										.toArray(String[]::new))))
				.withArguments(new BooleanArgument("value"))
				.executesPlayer((player, args) -> {
					String flagName = (String) args.get("flag_name");
					boolean value = (boolean) args.get("value");

					Optional<Region> claimOpt = plugin.getClaimManager()
							.getClaimAt(player.getLocation());
					if (claimOpt.isEmpty()) {
						MessageUtil.sendError(player, "You must be standing inside a claim");
						return;
					}

					Region claim = claimOpt.get();
					if (!claim.getOwner().equals(player.getUniqueId()) && !player.isOp()) {
						MessageUtil.sendError(player,
								"You do not have permission to edit this claim");
						return;
					}

					try {
						GlobalFlags flag = GlobalFlags.valueOf(flagName);
						claim.setFlag(flag, value);
						plugin.getClaimManager().saveClaims();

						MessageUtil.sendSuccess(player,
								"Set flag <aqua>" + flag.getDisplayName()
										+ "</aqua> to " +
										(value ? "<green>TRUE</green>"
												: "<red>FALSE</red>"));
					} catch (IllegalArgumentException e) {
						MessageUtil.sendError(player, "Invalid flag name");
					}
				});
	}
}
