package dev.objz.claim.gui.menus.roles;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.Menu;
import dev.objz.claim.gui.menus.main.MainMenu;
import dev.objz.claim.model.Region;
import dev.objz.claim.model.Roles;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class RoleMenu extends Menu {
	private final Claim plugin;

	public RoleMenu(Claim plugin, Region claim) {
		super(claim, SIZE_SMALL, Component.text("Role Permissions"));
		this.plugin = plugin;
		build();
	}

	@Override
	protected void build() {
		setItem(11, Material.NETHER_STAR, Component.text("Admin Role", NamedTextColor.GOLD),
				List.of(
						Component.text("Edit Admin permissions", NamedTextColor.GRAY),
						Component.text("Highest permission level", NamedTextColor.GRAY)));

		setItem(13, Material.IRON_PICKAXE, Component.text("Builder Role", NamedTextColor.BLUE),
				List.of(
						Component.text("Edit Builder permissions", NamedTextColor.GRAY),
						Component.text("Construction focus", NamedTextColor.GRAY)));

		setItem(15, Material.SPYGLASS, Component.text("Visitor Role", NamedTextColor.GRAY),
				List.of(
						Component.text("Edit Visitor permissions", NamedTextColor.GRAY),
						Component.text("Basic access level", NamedTextColor.GRAY)));

		setItem(22, HeadUtil.createCustomHead("Back", HeadUtil.ARROW_LEFT,
				List.of(Component.text("Return to main menu", NamedTextColor.GRAY))));

		fillBorders();
	}

	@Override
	public void handleClick(Player player, int slot, ClickType clickType) {
		if (slot == 22) {
			playBackSound(player);
			player.openInventory(new MainMenu(plugin, claim).getInventory());
			return;
		}

		if (slot == 11 || slot == 13 || slot == 15) {
			playClickSound(player);
		}

		switch (slot) {
			case 11 ->
				player.openInventory(new PermissionMenu(plugin, claim, Roles.ADMIN).getInventory());
			case 13 -> player.openInventory(
					new PermissionMenu(plugin, claim, Roles.BUILDER).getInventory());
			case 15 -> player.openInventory(
					new PermissionMenu(plugin, claim, Roles.VISITOR).getInventory());
		}
	}
}
