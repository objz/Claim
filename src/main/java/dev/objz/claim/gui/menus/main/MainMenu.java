package dev.objz.claim.gui.menus.main;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.Menu;
import dev.objz.claim.gui.menus.players.PlayerManagementMenu;
import dev.objz.claim.gui.menus.roles.RoleMenu;
import dev.objz.claim.gui.menus.settings.SettingsMenu;
import dev.objz.claim.gui.menus.utils.UtilitiesMenu;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

public class MainMenu extends Menu {
	private final Claim plugin;

	public MainMenu(Claim plugin, Region claim) {
		super(claim, SIZE_SMALL, Component.text("Claim Control Panel"));
		this.plugin = plugin;
		build();
	}

	@Override
	protected void build() {
		setItem(10, HeadUtil.createCustomHead("Players", HeadUtil.PLAYERS,
				List.of(
						Component.text("Manage claim members", NamedTextColor.GRAY),
						Component.text("Add, remove, and view players", NamedTextColor.GRAY))));

		setItem(12, HeadUtil.createCustomHead("Roles", HeadUtil.PLAYER,
				List.of(
						Component.text("Configure role permissions", NamedTextColor.GRAY),
						Component.text("Edit what each role can do", NamedTextColor.GRAY))));

		setItem(14, HeadUtil.createCustomHead("Settings", HeadUtil.SETTINGS,
				List.of(
						Component.text("Global claim settings", NamedTextColor.GRAY),
						Component.text("PvP, explosions, and more", NamedTextColor.GRAY))));

		setItem(16, HeadUtil.createCustomHead("Utilities", HeadUtil.UTILITIES,
				List.of(
						Component.text("Tools and information", NamedTextColor.GRAY),
						Component.text("Borders, info, and deletion", NamedTextColor.GRAY))));

		setItem(22, HeadUtil.createCustomHead("Close", HeadUtil.CANCEL,
				List.of(Component.text("Close this menu", NamedTextColor.GRAY))));

		fillBorders();
	}

	@Override
	public void handleClick(Player player, int slot, ClickType clickType) {
		if (slot == 22) {
			playBackSound(player);
			player.closeInventory();
		} else if (slot == 10 || slot == 12 || slot == 14 || slot == 16) {
			playClickSound(player);
		}

		switch (slot) {
			case 10 -> player.openInventory(new PlayerManagementMenu(plugin, claim).getInventory());
			case 12 -> player.openInventory(new RoleMenu(plugin, claim).getInventory());
			case 14 -> player.openInventory(new SettingsMenu(plugin, claim).getInventory());
			case 16 -> player.openInventory(new UtilitiesMenu(plugin, claim).getInventory());
		}
	}
}
