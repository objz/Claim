package dev.objz.claim.gui.menu;

import dev.objz.claim.Claim;
import dev.objz.claim.model.ClaimFlag;
import dev.objz.claim.model.ClaimRegion;
import dev.objz.claim.model.ClaimRole;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class SettingsMenu extends ClaimGuiHolder {
	private final Claim plugin;
	private final boolean global;
	private final ClaimRole role;

	public SettingsMenu(Claim plugin, ClaimRegion claim, boolean global, ClaimRole role) {
		super(claim, 54, Component.text(global ? "Global Settings" : role.getDisplayName() + " Settings"));
		this.plugin = plugin;
		this.global = global;
		this.role = role;
		refresh();
	}

	private void refresh() {
		int slot = 0;
		for (ClaimFlag flag : ClaimFlag.values()) {
			if (flag.isGlobal() != global)
				continue;

			boolean value = global ? claim.getFlag(flag) : claim.getRolePermission(role, flag);
			String status = value ? "ENABLED" : "DISABLED";
			Material icon = value ? Material.LIME_DYE : Material.GRAY_DYE;

			setItem(slot, icon, flag.getDisplayName(), List.of("Current: " + status, "Click to toggle"));
			slot++;
		}
		setItem(49, Material.ARROW, "Back", null);
	}

	@Override
	public void handleClick(Player player, int slot) {
		if (slot == 49) {
			player.openInventory(new MainMenu(plugin, claim, player).getInventory());
			return;
		}

		int currentSlot = 0;
		for (ClaimFlag flag : ClaimFlag.values()) {
			if (flag.isGlobal() != global)
				continue;

			if (currentSlot == slot) {
				boolean currentVal = global ? claim.getFlag(flag) : claim.getRolePermission(role, flag);
				if (global)
					claim.setGlobalFlag(flag, !currentVal);
				else
					claim.setRolePermission(role, flag, !currentVal);
				refresh();
				return;
			}
			currentSlot++;
		}
	}
}
