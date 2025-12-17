package dev.objz.claim.gui.menus.utils;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.ConfirmationMenu;
import dev.objz.claim.gui.framework.Menu;
import dev.objz.claim.gui.menus.main.MainMenu;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class UtilitiesMenu extends Menu {
	private final Claim plugin;

	public UtilitiesMenu(Claim plugin, Region claim) {
		super(claim, SIZE_SMALL, Component.text("Utilities"));
		this.plugin = plugin;
		build();
	}

	@Override
	protected void build() {
		setItem(11, HeadUtil.createCustomHead("Claim Info", HeadUtil.INFO,
				List.of(
						Component.text("Name:  ", NamedTextColor.GRAY).append(
								Component.text(claim.getName(), NamedTextColor.WHITE)),
						Component.text("World: ", NamedTextColor.GRAY)
								.append(Component.text(claim.getWorldName(),
										NamedTextColor.WHITE)),
						Component.text("Center: ", NamedTextColor.GRAY).append(Component.text(
								claim.getRegion().getCenterX() + ", "
										+ claim.getRegion().getCenterZ(),
								NamedTextColor.WHITE)),
						Component.text("Size: ", NamedTextColor.GRAY).append(Component.text(
								(int) claim.getRegion().getWidthX() + "x"
										+ (int) claim.getRegion().getWidthZ(),
								NamedTextColor.WHITE)))));

		setItem(13, HeadUtil.createCustomHead("Toggle Borders", HeadUtil.BARRIER,
				List.of(
						Component.text("Show/hide claim borders", NamedTextColor.GRAY),
						Component.text("Visual boundary markers", NamedTextColor.GRAY))));

		setItem(15, HeadUtil.createCustomHead("Delete Claim", HeadUtil.DELETE,
				List.of(
						Component.text("Permanently remove claim", NamedTextColor.RED),
						Component.text("⚠ This cannot be undone!", NamedTextColor.DARK_RED))));

		setItem(22, HeadUtil.createCustomHead("Back", HeadUtil.ARROW_LEFT,
				List.of(Component.text("Return to main menu", NamedTextColor.GRAY))));

		fillBorders();
	}

	@Override
	public void handleClick(Player player, int slot, ClickType clickType) {
		switch (slot) {
			case 11 -> {
				playErrorSound(player);
			}
			case 13 -> {
				playErrorSound(player);
				if (plugin.getBorderVisualizer().isActive(player)) {
					plugin.getBorderVisualizer().hideBorder(player);
				} else {
					plugin.getBorderVisualizer().showBorder(player, claim.getRegion(),
							claim.getWorldName());
				}
				player.closeInventory();
			}
			case 16, 15 -> {
				playClickSound(player);
				ConfirmationMenu confirmMenu = new ConfirmationMenu(
						claim,
						Component.text("Confirm Deletion"),
						Component.text("Are you sure you want to delete this claim?",
								NamedTextColor.RED),
						List.of(
								Component.text("This will permanently your claim",
										NamedTextColor.RED),
								Component.text("⚠ This cannot be undone!",
										NamedTextColor.DARK_RED)),
						p -> {
							plugin.getClaimManager().deleteClaim(claim.getId());
							plugin.getBorderVisualizer().hideBorder(p);
							p.closeInventory();
							p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f,
									1f);
						},
						p -> {
							playBackSound(p);
							p.openInventory(new UtilitiesMenu(plugin, claim)
									.getInventory());
						});
				player.openInventory(confirmMenu.getInventory());
			}
			case 22 -> {
				playBackSound(player);
				player.openInventory(new MainMenu(plugin, claim).getInventory());
			}
		}
	}
}
