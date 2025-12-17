package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.model.Roles;
import dev.objz.claim.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class MemberCommand {
	private final Claim plugin;

	public MemberCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("member")
				.withSubcommand(getAddCommand())
				.withSubcommand(getRemoveCommand())
				.withSubcommand(getSetRoleCommand());
	}

	private CommandAPICommand getAddCommand() {
		return new CommandAPICommand("add")
				.withArguments(new EntitySelectorArgument.OnePlayer("target"))
				.executesPlayer((player, args) -> {
					Player target = (Player) args.get("target");
					if (target == null)
						return;

					Region claim = getOwnedClaimAtLocation(player);
					if (claim == null)
						return;

					if (claim.getMembers().containsKey(target.getUniqueId())) {
						MessageUtil.sendError(player, target.getName()
								+ " is already a member of this claim");
						return;
					}

					claim.setRole(target.getUniqueId(), Roles.VISITOR);
					plugin.getClaimManager().saveClaims();

					MessageUtil.sendSuccess(player, "Added " + target.getName() + " as a Visitor");
					MessageUtil.sendInfo(target, "You have been added to claim " + claim.getName());
				});
	}

	private CommandAPICommand getRemoveCommand() {
		return new CommandAPICommand("remove")
				.withArguments(new StringArgument("member_name")
						.replaceSuggestions(ArgumentSuggestions.strings(info -> {
							if (info.sender() instanceof Player p) {
								Optional<Region> claim = plugin.getClaimManager()
										.getClaimAt(p.getLocation());
								if (claim.isPresent() && claim.get().getOwner()
										.equals(p.getUniqueId())) {
									return claim.get().getMembers().keySet()
											.stream()
											.map(uuid -> Bukkit
													.getOfflinePlayer(
															uuid)
													.getName())
											.filter(n -> n != null)
											.toArray(String[]::new);
								}
							}
							return new String[0];
						})))
				.executesPlayer((player, args) -> {
					String targetName = (String) args.get("member_name");
					Region claim = getOwnedClaimAtLocation(player);
					if (claim == null)
						return;

					UUID targetUUID = null;
					for (UUID uuid : claim.getMembers().keySet()) {
						String name = Bukkit.getOfflinePlayer(uuid).getName();
						if (name != null && name.equals(targetName)) {
							targetUUID = uuid;
							break;
						}
					}

					if (targetUUID == null) {
						MessageUtil.sendError(player, "Member not found in this claim");
						return;
					}

					if (targetUUID.equals(player.getUniqueId())) {
						MessageUtil.sendError(player, "You cannot remove yourself");
						return;
					}

					claim.setRole(targetUUID, null);
					plugin.getClaimManager().saveClaims();
					MessageUtil.sendSuccess(player, "Removed " + targetName + " from claim");
				});
	}

	private CommandAPICommand getSetRoleCommand() {
		return new CommandAPICommand("role")
				.withArguments(new StringArgument("member_name")
						.replaceSuggestions(ArgumentSuggestions.strings(info -> {
							if (info.sender() instanceof Player p) {
								Optional<Region> claim = plugin.getClaimManager()
										.getClaimAt(p.getLocation());
								if (claim.isPresent() && claim.get().getOwner()
										.equals(p.getUniqueId())) {
									return claim.get().getMembers().keySet()
											.stream()
											.map(uuid -> Bukkit
													.getOfflinePlayer(
															uuid)
													.getName())
											.filter(n -> n != null)
											.toArray(String[]::new);
								}
							}
							return new String[0];
						})))
				.withArguments(new MultiLiteralArgument("role", "ADMIN", "BUILDER", "VISITOR"))
				.executesPlayer((player, args) -> {
					String targetName = (String) args.get("member_name");
					String roleName = (String) args.get("role");
					Roles newRole = Roles.valueOf(roleName);

					Region claim = getOwnedClaimAtLocation(player);
					if (claim == null)
						return;

					UUID targetUUID = null;
					for (UUID uuid : claim.getMembers().keySet()) {
						String name = Bukkit.getOfflinePlayer(uuid).getName();
						if (name != null && name.equals(targetName)) {
							targetUUID = uuid;
							break;
						}
					}

					if (targetUUID == null) {
						MessageUtil.sendError(player, "Member not found in this claim");
						return;
					}

					if (targetUUID.equals(player.getUniqueId())) {
						MessageUtil.sendError(player, "You cannot change your own role");
						return;
					}

					claim.setRole(targetUUID, newRole);
					plugin.getClaimManager().saveClaims();
					MessageUtil.sendSuccess(player,
							"Set " + targetName + "'s role to " + newRole.getDisplayName());
				});
	}

	private Region getOwnedClaimAtLocation(Player player) {
		Optional<Region> claimOpt = plugin.getClaimManager().getClaimAt(player.getLocation());
		if (claimOpt.isEmpty()) {
			MessageUtil.sendError(player, "You must be standing inside a claim");
			return null;
		}
		Region claim = claimOpt.get();
		if (!claim.getOwner().equals(player.getUniqueId()) && !player.isOp()) {
			MessageUtil.sendError(player, "You do not own this claim");
			return null;
		}
		return claim;
	}
}
