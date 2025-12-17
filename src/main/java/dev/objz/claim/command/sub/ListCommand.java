package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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
						player.sendMessage(Component.text("You do not own any claims",
								NamedTextColor.YELLOW));
						return;
					}

					player.sendMessage(Component.text("Your Claims:", NamedTextColor.GOLD));
					for (Region claim : claims) {
						int x = (int) claim.getRegion().getCenter().getX();
						int z = (int) claim.getRegion().getCenter().getZ();

						player.sendMessage(Component
								.text("- " + claim.getName(), NamedTextColor.AQUA)
								.append(Component.text(
										" (" + x + ", " + z + " in "
												+ claim.getWorldName()
												+ ")",
										NamedTextColor.GRAY)));
					}
				});
	}
}
