package dev.objz.claim.gui.menus.roles;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.Permissions;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import dev.objz.claim.model.Roles;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PermissionMenu extends Permissions {
	private final Roles role;

	public PermissionMenu(Claim plugin, Region claim, Roles role) {
		super(plugin, claim, Component.text(role.getDisplayName() + " Permissions"));
		this.role = role;
		build();
	}

	@Override
	protected Boolean getPermissionState(PlayerFlags flag) {
		return claim.getFlag(role, flag);
	}

	@Override
	protected void setPermissionState(PlayerFlags flag, Boolean value) {
		if (value == null)
			value = flag.getDefaultValue();
		claim.setFlag(role, flag, value);
		plugin.getClaimManager().saveClaims();
	}

	@Override
	protected void onBack(Player player) {
		player.openInventory(new RoleMenu(plugin, claim).getInventory());
	}
}
