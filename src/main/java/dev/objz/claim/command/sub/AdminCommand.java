package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerProfileArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

public class AdminCommand {
	private final Claim plugin;

	public AdminCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("admin")
				.withPermission("claim.admin")
				.withSubcommand(getListCommand())
				.withSubcommand(getDeleteCommand())
				.withSubcommand(getBypassCommand())
				.withSubcommand(getTransferCommand());
	}

	private CommandAPICommand getListCommand() {
		return new CommandAPICommand("list")
				.executesPlayer((player, args) -> {
					MessageUtil.sendInfo(player, "Server Claims:");
					for (Region claim : plugin.getClaimManager().getAllClaims()) {
						String ownerName = Bukkit.getOfflinePlayer(claim.getOwner()).getName();
						if (ownerName == null)
							ownerName = "Unknown";

						player.sendMessage(MessageUtil.parse(
								" <dark_gray>-</dark_gray> <aqua>" + claim.getName()
										+ "</aqua> <gray>(Owner: <white>"
										+ ownerName + "</white>, World: "
										+ claim.getWorldName() + ")</gray>"));
					}
				});
	}

	private CommandAPICommand getDeleteCommand() {
		return new CommandAPICommand("delete")
				.withArguments(new TextArgument("claim_id")
						.replaceSuggestions(ArgumentSuggestions.strings(
								info -> plugin.getClaimManager().getAllClaims().stream()
										.map(c -> {
											String owner = Bukkit
													.getOfflinePlayer(
															c.getOwner())
													.getName();
											return (owner != null ? owner
													: "Unknown")
													+ "."
													+ c.getName();
										})
										.toArray(String[]::new))))
				.executesPlayer((player, args) -> {
					String id = (String) args.get("claim_id");
					if (id == null)
						return;

					Optional<Region> target = plugin.getClaimManager().getAllClaims().stream()
							.filter(c -> {
								String owner = Bukkit.getOfflinePlayer(c.getOwner())
										.getName();
								String check = (owner != null ? owner : "Unknown") + "."
										+ c.getName();
								return check.equals(id);
							})
							.findFirst();

					if (target.isPresent()) {
						plugin.getClaimManager().deleteClaim(target.get().getId());
						MessageUtil.sendSuccess(player, "Deleted claim <yellow>"
								+ target.get().getName() + "</yellow> owned by "
								+ Bukkit.getOfflinePlayer(target.get().getOwner())
										.getName());
					} else {
						MessageUtil.sendError(player, "Claim not found");
					}
				});
	}

	private CommandAPICommand getBypassCommand() {
		return new CommandAPICommand("bypass")
				.executesPlayer((player, args) -> {
					boolean state = plugin.getBypassManager().toggleBypass(player);
					if (state) {
						MessageUtil.sendSuccess(player,
								"Admin bypass <green>ENABLED</green>. You can now interact with all claims");
					} else {
						MessageUtil.sendInfo(player, "Admin bypass <red>DISABLED</red>");
					}
				});
	}

	private CommandAPICommand getTransferCommand() {
		return new CommandAPICommand("transfer")
				.withArguments(new PlayerProfileArgument("new_owner"))
				.executesPlayer((player, args) -> {
					OfflinePlayer newOwner = (OfflinePlayer) args.get("new_owner");
					if (newOwner == null)
						return;

					Optional<Region> claim = plugin.getClaimManager()
							.getClaimAt(player.getLocation());
					if (claim.isEmpty()) {
						MessageUtil.sendError(player,
								"You must be standing in a claim to transfer it");
						return;
					}

					Region region = claim.get();
					plugin.getClaimManager().transferClaim(region, newOwner.getUniqueId());
					MessageUtil.sendSuccess(player, "Transferred claim <yellow>" + region.getName()
							+ "</yellow> to <aqua>" + newOwner.getName() + "</aqua>");
				});
	}
}
