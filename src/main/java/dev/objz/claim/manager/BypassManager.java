package dev.objz.claim.manager;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BypassManager {
	private final Set<UUID> bypassingPlayers = new HashSet<>();

	public boolean toggleBypass(Player player) {
		if (bypassingPlayers.contains(player.getUniqueId())) {
			bypassingPlayers.remove(player.getUniqueId());
			return false;
		} else {
			bypassingPlayers.add(player.getUniqueId());
			return true;
		}
	}

	public boolean isBypassing(Player player) {
		return bypassingPlayers.contains(player.getUniqueId());
	}
}
