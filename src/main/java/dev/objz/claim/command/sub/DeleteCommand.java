package dev.objz.claim.command. sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel. commandapi.arguments.StringArgument;
import dev.objz. claim.Claim;
import dev.objz.claim.model.Region;
import net.kyori.adventure.text. Component;
import net.kyori.adventure.text.format. NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DeleteCommand {
	private final Claim plugin;

	public DeleteCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("delete")
				.withOptionalArguments(new StringArgument("name")
						.replaceSuggestions(ArgumentSuggestions.strings(info -> {
							if (info.sender() instanceof Player player) {
								return plugin.getClaimManager().getAllClaims().stream()
										.filter(c -> c.getOwner().equals(player.getUniqueId()))
										.map(Region::getName)
										.toArray(String[]::new);
							}
							return new String[0];
						})))
				.executesPlayer((player, args) -> {
					String name = (String) args.get("name");

					Region targetClaim = null;

					if (name != null) {
						Optional<Region> namedClaim = plugin.getClaimManager().getAllClaims().stream()
								.filter(c -> c.getOwner().equals(player.getUniqueId()) && c.getName().equalsIgnoreCase(name))
								.findFirst();

						if (namedClaim.isEmpty()) {
							player.sendMessage(Component.text("You do not own a claim named '" + name + "'", NamedTextColor. RED));
							return;
						}
						targetClaim = namedClaim.get();
					} else {
						Optional<Region> locClaim = plugin.getClaimManager().getClaimAt(player.getLocation());

						if (locClaim.isEmpty()) {
							player.sendMessage(Component.text("You are not standing in a claim", NamedTextColor.RED));
							return;
						}

						if (! locClaim.get().getOwner().equals(player.getUniqueId()) && !player.isOp()) {
							player.sendMessage(Component.text("You do not have permission to delete this claim", NamedTextColor.RED));
							return;
						}
						targetClaim = locClaim.get();
					}

					plugin. getClaimManager().deleteClaim(targetClaim.getId());
					plugin.getBorderVisualizer().hideBorder(player);

					player.sendMessage(Component. text("Claim '" + targetClaim.getName() + "' has been deleted", NamedTextColor.GREEN));
				});
	}
}
