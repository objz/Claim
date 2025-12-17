package dev.objz.claim.gui.menus.players.add;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.Menu;
import dev.objz.claim.gui.menus.players.PlayerManagementMenu;
import dev.objz.claim.model.Region;
import dev.objz.claim.model.Roles;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class SelectRole extends Menu {
	private final Claim plugin;
	private final Player targetPlayer;

	public SelectRole(Claim plugin, Region claim, Player targetPlayer) {
		super(claim, SIZE_SMALL, Component.text("Select Role for " + targetPlayer.getName()));
		this.plugin = plugin;
		this.targetPlayer = targetPlayer;
		build();
	}

	@Override
	protected void build() {
		setItem(11, Material.NETHER_STAR, Component.text("Admin", NamedTextColor.GOLD),
				List.of(
						Component.text("Full permissions", NamedTextColor.GRAY),
						Component.text("Can manage everything", NamedTextColor.GRAY)));

		setItem(13, Material.IRON_PICKAXE, Component.text("Builder", NamedTextColor.BLUE),
				List.of(
						Component.text("Build permissions", NamedTextColor.GRAY),
						Component.text("Can break/place blocks", NamedTextColor.GRAY)));

		setItem(15, Material.SPYGLASS, Component.text("Visitor", NamedTextColor.GRAY),
				List.of(
						Component.text("Basic permissions", NamedTextColor.GRAY),
						Component.text("Limited interactions", NamedTextColor.GRAY)));

		setItem(22, HeadUtil.createCustomHead("Back", HeadUtil.ARROW_LEFT,
				List.of(Component.text("Cancel and go back", NamedTextColor.GRAY))));

		fillBorders();
	}

	@Override
	public void handleClick(Player player, int slot, ClickType clickType) {
		Roles selectedRole = null;

		switch (slot) {
			case 11 -> selectedRole = Roles.ADMIN;
			case 13 -> selectedRole = Roles.BUILDER;
			case 15 -> selectedRole = Roles.VISITOR;
			case 22 -> {
				playBackSound(player);
				player.openInventory(new AddPlayer(plugin, claim).getInventory());
				return;
			}
		}

		if (selectedRole != null) {
			claim.setRole(targetPlayer.getUniqueId(), selectedRole);
			player.sendMessage(Component.text(
					"Added " + targetPlayer.getName() + " as " + selectedRole.getDisplayName(),
					NamedTextColor.GREEN));
			playSuccessSound(player);
			player.openInventory(new PlayerManagementMenu(plugin, claim).getInventory());
		}
	}
}
