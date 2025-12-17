package dev.objz.claim.gui.menu;

import dev.objz.claim.Claim;
import dev.objz. claim.model.GlobalFlags;
import dev. objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import dev.objz. claim.model.Roles;
import dev.objz.claim. util.HeadUtil;
import net.kyori.adventure.text. Component;
import net.kyori.adventure.text.format. NamedTextColor;
import org.bukkit.Sound;
import org.bukkit. entity.Player;

import java.util.List;

public class FlagToggleMenu extends ClaimGuiHolder {
	private final Claim plugin;
	private final Object flag;
	private final boolean global;
	private final Roles role;

	public FlagToggleMenu(Claim plugin, Region claim, Object flag, boolean global, Roles role) {
		super(claim, SIZE_SMALL, Component.text("Edit:  " + getFlagName(flag)));
		this.plugin = plugin;
		this.flag = flag;
		this.global = global;
		this.role = role;

		boolean currentValue = getCurrentValue();

		setCustomHead(11, HeadUtil. CONFIRM, "Enable", List.of(
				Component.text("Set " + getFlagName(flag) + " to ", NamedTextColor.GRAY)
						.append(Component.text("TRUE", NamedTextColor.GREEN)),
				Component.empty(),
				currentValue ? Component.text("CURRENTLY SELECTED", NamedTextColor.GREEN)
						: Component.text("Click to select", NamedTextColor.GRAY)));

		setItem(13, getFlagIcon(flag), getFlagName(flag), List.of(
				Component.text("Modifying setting for:", NamedTextColor.GRAY),
				Component.text(global ? "Global Claim" : role.getDisplayName() + " Role", NamedTextColor.WHITE)));

		setCustomHead(15, HeadUtil.CANCEL, "Disable", List.of(
				Component.text("Set " + getFlagName(flag) + " to ", NamedTextColor.GRAY)
						.append(Component.text("FALSE", NamedTextColor.RED)),
				Component.empty(),
				! currentValue ? Component.text("CURRENTLY SELECTED", NamedTextColor.GREEN)
						: Component.text("Click to select", NamedTextColor.GRAY)));

		addBackButton(22);
		fillBorders();
	}

	@Override
	public void handleClick(Player player, int slot) {
		if (slot == 22) {
			player.openInventory(new SettingsMenu(plugin, claim, global, role).getInventory());
			return;
		}

		if (slot == 11) {
			updateSetting(true);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
			player.openInventory(new SettingsMenu(plugin, claim, global, role).getInventory());
		} else if (slot == 15) {
			updateSetting(false);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 0.5f);
			player.openInventory(new SettingsMenu(plugin, claim, global, role).getInventory());
		}
	}

	private boolean getCurrentValue() {
		if (global && flag instanceof GlobalFlags gf) {
			return claim.getFlag(gf);
		} else if (! global && flag instanceof PlayerFlags pf) {
			return claim.getFlag(role, pf);
		}
		return false;
	}

	private void updateSetting(boolean value) {
		if (global && flag instanceof GlobalFlags gf) {
			claim.setFlag(gf, value);
		} else if (!global && flag instanceof PlayerFlags pf) {
			claim.setFlag(role, pf, value);
		}
	}

	private static String getFlagName(Object flag) {
		if (flag instanceof GlobalFlags gf) return gf.getDisplayName();
		if (flag instanceof PlayerFlags pf) return pf.getDisplayName();
		return "Unknown";
	}

	private static org.bukkit.Material getFlagIcon(Object flag) {
		if (flag instanceof GlobalFlags gf) return gf.getIcon();
		if (flag instanceof PlayerFlags pf) return pf.getIcon();
		return org.bukkit.Material.BARRIER;
	}
}
