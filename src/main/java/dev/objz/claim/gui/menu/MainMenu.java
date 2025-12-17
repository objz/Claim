package dev.objz.claim.gui.menu;

import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

public class MainMenu extends ClaimGuiHolder {
	private final Claim plugin;

	public MainMenu(Claim plugin, Region claim) {
		super(claim, SIZE_SMALL, Component.text("Claim Control Panel"));
		this.plugin = plugin;

		// 1. Member List
		setCustomHead(10, HeadUtil.MEMBER, "Member List", List.of(
				Component.text("Manage members in this claim.", NamedTextColor.GRAY),
				Component.text("View and edit existing members.", NamedTextColor.GRAY)));

		// 2. Add Player
		setCustomHead(12, HeadUtil.ADD_PLAYER, "Add Player", List.of(
				Component.text("Invite a new player", NamedTextColor.GRAY),
				Component.text("to join this claim.", NamedTextColor.GRAY)));

		// 3. Permissions/Roles
		setCustomHead(14, HeadUtil.PERMISSIONS, "Role Permissions", List.of(
				Component.text("Configure role permissions", NamedTextColor.GRAY),
				Component.text("and global claim settings.", NamedTextColor.GRAY)));

		// 4. Utils
		setCustomHead(16, HeadUtil.UTILITIES, "Utilities", List.of(
				Component.text("Extra tools, visualizers,", NamedTextColor.GRAY),
				Component.text("info, and deletion.", NamedTextColor.GRAY)));
		
		// Back button to close inventory
		setCustomHead(22, HeadUtil.CANCEL, "Close", List.of(
				Component.text("Close the menu", NamedTextColor.GRAY)
		));
		
		fillBorders();
	}

	@Override
	public void handleClick(Player player, int slot) {
		switch (slot) {
			case 10 -> player.openInventory(new MemberListMenu(plugin, claim).getInventory());
			case 12 -> player.openInventory(new AddPlayerMenu(plugin, claim).getInventory());
			case 14 -> player.openInventory(new RoleSelectionMenu(plugin, claim).getInventory());
			case 16 -> player.openInventory(new UtilsMenu(plugin, claim).getInventory());
			case 22 -> player.closeInventory();
		}
	}
}
