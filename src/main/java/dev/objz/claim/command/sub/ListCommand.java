package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.MessageUtil;

import java.util.List;

public class ListCommand {
	private final Claim plugin;

	public ListCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("list")
				.executesPlayer((player, args) -> {
					List<Region> claims = plugin.getClaimManager().getAllClaims().stream()
							.filter(c -> c.getOwner().equals(player.getUniqueId()))
							.toList();

					if (claims.isEmpty()) {
						MessageUtil.sendInfo(player, "You do not own any claims.");
						return;
					}

					MessageUtil.sendInfo(player, "Your Claims:");
					for (Region claim : claims) {
						int x = (int) claim.getRegion().getCenter().getX();
						int z = (int) claim.getRegion().getCenter().getZ();

						player.sendMessage(MessageUtil.parse(
								" <dark_gray>-</dark_gray> <aqua>" + claim.getName()
										+ "</aqua> <gray>(" + x + ", " + z
										+ " in " + claim.getWorldName()
										+ ")</gray>"));
					}
				});
	}
}
