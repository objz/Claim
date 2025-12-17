package dev.objz.claim.gui.menus.players.edit;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.Permissions;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EditPermissions extends Permissions {
	private final UUID targetPlayer;

	public EditPermissions(Claim plugin, Region claim, UUID targetPlayer) {
		super(plugin, claim, Component.text("Permission Override"));
		this.targetPlayer = targetPlayer;
		build();
	}

	@Override
	protected void build() {
		super.build();
		OfflinePlayer op = Bukkit.getOfflinePlayer(targetPlayer);
		ItemStack head = HeadUtil.createPlayerHead(op, null);
		setItem(4, head);
	}

	@Override
	protected Boolean getPermissionState(PlayerFlags flag) {
		return claim.getPlayerFlagOverride(targetPlayer, flag);
	}

	@Override
	protected void setPermissionState(PlayerFlags flag, Boolean value) {
		claim.setPlayerFlag(targetPlayer, flag, value);
		plugin.getClaimManager().saveClaims();
	}

	@Override
	protected boolean supportsInheritance() {
		return true;
	}

	@Override
	protected void onBack(Player player) {
		player.openInventory(new ListMembers(plugin, claim).getInventory());
	}
}
