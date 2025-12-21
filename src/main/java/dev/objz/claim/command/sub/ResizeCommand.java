package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

import java.util.Optional;

public class ResizeCommand {
	private final Claim plugin;

	public ResizeCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("resize")
				.executesPlayer((player, args) -> {
					Optional<Region> claimOpt = plugin.getClaimManager()
							.getClaimAt(player.getLocation());

					if (claimOpt.isEmpty()) {
						MessageUtil.sendError(player,
								"You must be standing inside the claim you want to resize");
						return;
					}

					Region claim = claimOpt.get();
					if (!claim.getOwner().equals(player.getUniqueId()) && !player.isOp()) {
						MessageUtil.sendError(player,
								"You do not have permission to resize this claim");
						return;
					}

					if (!plugin.getSelectionManager().hasSelection(player)) {
						if (!plugin.getSelectionManager().hasTool(player)) {
							plugin.getSelectionManager().giveTool(player);
						}

						plugin.getBorderVisualizer().showBorder(player, claim.getRegion(),
								claim.getWorldName());

						MessageUtil.sendInfo(player, "<b>Resize Mode Started</b>");
						MessageUtil.sendInfo(player,
								"1. Use the <gold>Golden Shovel</gold> to select new corners.");
						MessageUtil.sendInfo(player,
								"2. Type <yellow>/claim resize</yellow> again to confirm changes.");
						return;
					}

					Location p1 = plugin.getSelectionManager().getPos1(player);
					Location p2 = plugin.getSelectionManager().getPos2(player);

					if (!p1.getWorld().getName().equals(claim.getWorldName()) ||
							!p2.getWorld().getName().equals(claim.getWorldName())) {
						MessageUtil.sendError(player,
								"Selection must be in the same world as the claim");
						return;
					}

					int minX = Math.min(p1.getBlockX(), p2.getBlockX());
					int minZ = Math.min(p1.getBlockZ(), p2.getBlockZ());
					int maxX = Math.max(p1.getBlockX(), p2.getBlockX()) + 1;
					int maxZ = Math.max(p1.getBlockZ(), p2.getBlockZ()) + 1;

					double minY = p1.getWorld().getMinHeight();
					double maxY = p1.getWorld().getMaxHeight();

					if (maxX - minX < 5 || maxZ - minZ < 5) {
						MessageUtil.sendError(player, "Claim must be at least 5x5 blocks");
						return;
					}

					BoundingBox newBox = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);

					boolean overlaps = plugin.getClaimManager().getAllClaims().stream()
							.filter(c -> !c.getId().equals(claim.getId())) // Ignore self
							.filter(c -> c.getWorldName().equals(claim.getWorldName()))
							.anyMatch(c -> c.getRegion().overlaps(newBox));

					if (overlaps) {
						MessageUtil.sendError(player,
								"New size would overlap with another claim");
						return;
					}

					plugin.getClaimManager().resizeClaim(claim, newBox);

					plugin.getSelectionManager().clearSelection(player);
					plugin.getSelectionManager().removeTool(player);
					plugin.getBorderVisualizer().showBorder(player, newBox, claim.getWorldName());

					MessageUtil.sendSuccess(player, "Claim resized successfully!");
				});
	}
}
