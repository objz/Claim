package dev.objz.claim.gui.menu;

import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.model.Roles;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class RoleSelectionMenu extends ClaimGuiHolder {
	private final Claim plugin;

	public RoleSelectionMenu(Claim plugin, Region claim) {
		super(claim, SIZE_SMALL, Component.text("Select Category"));
		this.plugin = plugin;

		// 1. Global
		setCustomHead(10, HeadUtil.GLOBAL, "Global Settings", List.of(
				Component.text("Manage claim-wide rules", NamedTextColor.GRAY),
				Component.text("like PvP, Explosions, etc.", NamedTextColor.GRAY)));

		// 2. Admin
		setItem(12, Material.NETHER_STAR, "Admin Role", List.of(
				Component.text("Edit permissions for Admins", NamedTextColor.GRAY)));

		// 3. Builder
		setItem(14, Material.IRON_PICKAXE, "Builder Role", List.of(
				Component.text("Edit permissions for Builders", NamedTextColor.GRAY)));

		// 4. Spectator
		setItem(16, Material.SPYGLASS, "Spectator Role", List.of(
				Component.text("Edit permissions for Spectators", NamedTextColor.GRAY)));

		addBackButton(22);
		fillBorders();
	}

	@Override
	public void handleClick(Player player, int slot) {
		if (slot == 22) {
			player.openInventory(new MainMenu(plugin, claim).getInventory());
			return;
		}

		switch (slot) {
			case 10 -> player.openInventory(new SettingsMenu(plugin, claim, true, null).getInventory());
			case 12 -> player.openInventory(
					new SettingsMenu(plugin, claim, false, Roles.ADMIN).getInventory());
			case 14 -> player.openInventory(
					new SettingsMenu(plugin, claim, false, Roles.BUILDER).getInventory());
			case 16 -> player.openInventory(
					new SettingsMenu(plugin, claim, false, Roles.VISITOR).getInventory());
		}
	}
}
