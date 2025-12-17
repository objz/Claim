package dev.objz.claim.command.sub;

import dev.jorel.commandapi.executors.CommandArguments;
import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public class InfoCommand {
	private final Claim plugin;

	public InfoCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public void execute(Player player, CommandArguments args) {
		Optional<Region> claim = plugin.getClaimManager().getClaimAt(player.getLocation());

		if (claim.isPresent()) {
			if (claim.get().getOwner().equals(player.getUniqueId()) || player.isOp()) {
				plugin.getGuiManager().openMainMenu(player, claim.get());
			} else {
				OfflinePlayer owner = Bukkit.getOfflinePlayer(claim.get().getOwner());
				MessageUtil.sendInfo(player,
						"You are in claim: <yellow>" + claim.get().getName() + "</yellow>");
				MessageUtil.sendInfo(player, "Owner: <white>"
						+ (owner.getName() != null ? owner.getName() : "Unknown") + "</white>");
			}
		} else {
			MessageUtil.sendError(player, "No claim at this location.");
		}
	}
}
