package dev.objz.claim.gui.menus.players;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.Menu;
import dev.objz.claim.gui.menus.main.MainMenu;
import dev.objz.claim.gui.menus.players.add.AddPlayer;
import dev.objz.claim.gui.menus.players.edit.ListMembers;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerManagementMenu extends Menu {
	private final Claim plugin;

	public PlayerManagementMenu(Claim plugin, Region claim) {
		super(claim, SIZE_SMALL, Component.text("Player Management"));
		this.plugin = plugin;
		build();
	}

	@Override
	protected void build() {
		setItem(11, HeadUtil.createCustomHead("Add Player", HeadUtil.CROSS,
				List.of(
						Component.text("Invite a new player", NamedTextColor.GRAY),
						Component.text("to join this claim", NamedTextColor.GRAY))));

		setItem(15, HeadUtil.createCustomHead("Edit Members", HeadUtil.PLAYER,
				List.of(
						Component.text("See all claim members", NamedTextColor.GRAY),
						Component.text("Edit roles and permissions", NamedTextColor.GRAY),
						Component.empty(),
						Component.text("Count: " + claim.getMembers().size(),
								NamedTextColor.AQUA))));

		setItem(22, HeadUtil.createCustomHead("Back", HeadUtil.ARROW_LEFT,
				List.of(Component.text("Return to main menu", NamedTextColor.GRAY))));

		fillBorders();
	}

	@Override
	public void handleClick(Player player, int slot, ClickType clickType) {
		switch (slot) {
			case 11 -> {
				playClickSound(player);
				player.openInventory(new AddPlayer(plugin, claim).getInventory());
			}
			case 15 -> {
				playClickSound(player);
				player.openInventory(new ListMembers(plugin, claim).getInventory());
			}
			case 22 -> {
				playBackSound(player);
				player.openInventory(new MainMenu(plugin, claim).getInventory());
			}
		}
	}
}
