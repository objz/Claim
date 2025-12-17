package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.MessageUtil;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;

import java.util.Optional;

public class ResizeCommand {
	private final Claim plugin;

	public ResizeCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("resize")
				.withArguments(new IntegerArgument("amount"))
				.executesPlayer((player, args) -> {
					int amount = (int) args.get("amount");
					if (amount == 0)
						return;

					Optional<Region> claimOpt = plugin.getClaimManager()
							.getClaimAt(player.getLocation());
					if (claimOpt.isEmpty()) {
						MessageUtil.sendError(player,
								"You must be standing inside a claim to resize it");
						return;
					}

					Region claim = claimOpt.get();
					if (!claim.getOwner().equals(player.getUniqueId()) && !player.isOp()) {
						MessageUtil.sendError(player,
								"You do not have permission to resize this claim");
						return;
					}

					BlockFace facing = player.getFacing();
					switch (facing) {
						case NORTH, SOUTH, EAST, WEST -> {
						}
						default -> {
							MessageUtil.sendError(player,
									"Please look directly North, South, East, or West");
							return;
						}
					}

					BoundingBox currentBox = claim.getRegion();
					double minX = currentBox.getMinX();
					double minZ = currentBox.getMinZ();
					double maxX = currentBox.getMaxX();
					double maxZ = currentBox.getMaxZ();

					switch (facing) {
						case NORTH -> minZ -= amount; // North is negative Z
						case SOUTH -> maxZ += amount; // South is positive Z
						case WEST -> minX -= amount; // West is negative X
						case EAST -> maxX += amount; // East is positive X
						default -> {
						}
					}

					if (maxX - minX < 5 || maxZ - minZ < 5) {
						MessageUtil.sendError(player, "Claim would become too small");
						return;
					}

					BoundingBox newBox = new BoundingBox(minX, currentBox.getMinY(), minZ, maxX,
							currentBox.getMaxY(), maxZ);

					boolean overlaps = plugin.getClaimManager().getAllClaims().stream()
							.filter(c -> !c.getId().equals(claim.getId())) // Ignore self
							.filter(c -> c.getWorldName().equals(claim.getWorldName()))
							.anyMatch(c -> c.getRegion().overlaps(newBox));

					if (overlaps) {
						MessageUtil.sendError(player,
								"Resizing would overlap with another claim");
						return;
					}

					plugin.getClaimManager().resizeClaim(claim, newBox);

					if (plugin.getBorderVisualizer().isActive(player)) {
						plugin.getBorderVisualizer().showBorder(player, newBox,
								claim.getWorldName());
					}

					MessageUtil.sendSuccess(player, "Claim resized successfully!");
				});
	}
}
