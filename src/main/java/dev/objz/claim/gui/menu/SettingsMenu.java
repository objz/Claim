package dev.objz.claim.gui.menu;

import dev.objz.claim.Claim;
import dev.objz.claim.model.GlobalFlags;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import dev.objz.claim.model.Roles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenu extends ClaimGuiHolder {
	private final Claim plugin;
	private final boolean global;
	private final Roles role;
	private final List<Object> displayedFlags = new ArrayList<>();
	private final java.util.Map<Integer, Object> slotMapping = new java.util.HashMap<>();

	public SettingsMenu(Claim plugin, Region claim, boolean global, Roles role) {
		super(claim, SIZE_SMALL,
				Component.text(global ? "Global Settings" : role.getDisplayName() + " Permissions"));
		this.plugin = plugin;
		this.global = global;
		this.role = role;

		if (global) {
			for (GlobalFlags flag : GlobalFlags.values()) {
				displayedFlags.add(flag);
			}
		} else {
			for (PlayerFlags flag : PlayerFlags.values()) {
				displayedFlags.add(flag);
			}
		}

		fillBorders();
		loadFlags();
		addBackButton(22);
	}

	private void loadFlags() {
		int[] slots = displayedFlags.size() <= 4 ? new int[] { 11, 12, 14, 15 }
				: new int[] { 10, 11, 12, 14, 15, 16 };

		for (int i = 0; i < displayedFlags.size() && i < slots.length; i++) {
			Object flag = displayedFlags.get(i);
			int slot = slots[i];

			boolean isEnabled = getCurrentFlagValue(flag);
			String status = isEnabled ? "ENABLED" : "DISABLED";
			NamedTextColor color = isEnabled ? NamedTextColor.GREEN : NamedTextColor.RED;

			setItem(slot, getFlagIcon(flag), getFlagName(flag), List.of(
					Component.text("Current Status:  ", NamedTextColor.GRAY)
							.append(Component.text(status, color)),
					Component.empty(),
					Component.text("Click to modify", NamedTextColor.YELLOW)), isEnabled);

			slotMapping.put(slot, flag);
		}
	}

	@Override
	public void handleClick(Player player, int slot) {
		if (slot == 22) {
			player.openInventory(new RoleSelectionMenu(plugin, claim).getInventory());
			return;
		}

		if (slotMapping.containsKey(slot)) {
			Object flag = slotMapping.get(slot);
			player.openInventory(new FlagToggleMenu(plugin, claim, flag, global, role).getInventory());
		}
	}

	private boolean getCurrentFlagValue(Object flag) {
		if (global && flag instanceof GlobalFlags gf) {
			return claim.getFlag(gf);
		} else if (!global && flag instanceof PlayerFlags pf) {
			return claim.getFlag(role, pf);
		}
		return false;
	}

	private static String getFlagName(Object flag) {
		if (flag instanceof GlobalFlags gf)
			return gf.getDisplayName();
		if (flag instanceof PlayerFlags pf)
			return pf.getDisplayName();
		return "Unknown";
	}

	private static org.bukkit.Material getFlagIcon(Object flag) {
		if (flag instanceof GlobalFlags gf)
			return gf.getIcon();
		if (flag instanceof PlayerFlags pf)
			return pf.getIcon();
		return org.bukkit.Material.BARRIER;
	}
}
