package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.objz.claim.Claim;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

public class CreateCommand {
	private final Claim plugin;

	public CreateCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("create")
				.withArguments(new StringArgument("name"))
				.executesPlayer((player, args) -> {
					String name = (String) args.get("name");

					if (!plugin.getSelectionManager().hasSelection(player)) {
						player.sendMessage(Component.text(
								"Please select a region with '/claim tool' first",
								NamedTextColor.RED));
						return;
					}

					boolean nameExists = plugin.getClaimManager().getAllClaims().stream()
							.anyMatch(c -> c.getOwner().equals(player.getUniqueId())
									&& c.getName().equalsIgnoreCase(name));

					if (nameExists) {
						player.sendMessage(Component.text(
								"You already have a claim named '" + name + "'",
								NamedTextColor.RED));
						return;
					}

					Location p1 = plugin.getSelectionManager().getPos1(player);
					Location p2 = plugin.getSelectionManager().getPos2(player);

					int minX = Math.min(p1.getBlockX(), p2.getBlockX());
					int minZ = Math.min(p1.getBlockZ(), p2.getBlockZ());
					int maxX = Math.max(p1.getBlockX(), p2.getBlockX()) + 1;
					int maxZ = Math.max(p1.getBlockZ(), p2.getBlockZ()) + 1;
					double minY = p1.getWorld().getMinHeight();
					double maxY = p1.getWorld().getMaxHeight();

					BoundingBox newBox = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);

					if (plugin.getClaimManager().isOverlapping(newBox, p1.getWorld().getName())) {
						player.sendMessage(Component.text(
								"You cannot create a claim here because it overlaps with another claim.",
								NamedTextColor.RED));
						return;
					}

					plugin.getClaimManager().createClaim(player.getUniqueId(), name, p1, p2);

					plugin.getSelectionManager().clearSelection(player);
					plugin.getSelectionManager().removeTool(player);
					plugin.getBorderVisualizer().hideBorder(player);

					player.sendMessage(Component.text("Claim '" + name + "' created!",
							NamedTextColor.GREEN));
				});
	}
}
