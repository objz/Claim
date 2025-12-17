package dev.objz.claim.gui.menu;

import dev.objz.claim.Claim;
import dev.objz. claim.model.Region;
import dev.objz. claim.util.HeadUtil;
import net.kyori.adventure.text. Component;
import net.kyori.adventure.text.format. NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

public class UtilsMenu extends ClaimGuiHolder {
	private final Claim plugin;

	public UtilsMenu(Claim plugin, Region claim) {
		super(claim, SIZE_SMALL, Component.text("Claim Utilities"));
		this.plugin = plugin;

		setCustomHead(11, HeadUtil.INFO, "Claim Info", List.of(
				Component.text("Name: ", NamedTextColor.GRAY).append(Component.text(claim.getName(), NamedTextColor.WHITE)),
				Component.text("Owner: ", NamedTextColor. GRAY).append(Component.text((claim.getOwner().equals(claim.getOwner()) ? "You" : "Unknown"), NamedTextColor.WHITE)),
				Component.text("World: ", NamedTextColor. GRAY).append(Component.text(claim.getWorldName(), NamedTextColor.WHITE)),
				Component.text("Center: ", NamedTextColor. GRAY).append(Component.text(claim.getRegion().getCenter().getBlockX() + ", " + claim.getRegion().getCenter().getBlockZ(), NamedTextColor.WHITE))));

		setCustomHead(13, HeadUtil.BARRIER, "Toggle Borders", List.of(
				Component.text("Show/Hide the visual", NamedTextColor.GRAY),
				Component.text("boundary of the claim.", NamedTextColor. GRAY)));

		setCustomHead(15, HeadUtil.DELETE, "Delete Claim", List. of(
				Component.text("Permanently remove this claim.", NamedTextColor.GRAY),
				Component.text("", NamedTextColor.GRAY),
				Component.text("WARNING:  Irreversible!", NamedTextColor.RED),
				Component.text("Click to delete", NamedTextColor.RED)));

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
			case 13 -> {
				if (plugin.getBorderVisualizer().isActive(player)) {
					plugin.getBorderVisualizer().hideBorder(player);
					player.sendMessage(Component.text("Borders hidden.", NamedTextColor.YELLOW));
				} else {
					plugin.getBorderVisualizer().showBorder(player, claim. getRegion(), claim.getWorldName());
					player.sendMessage(Component.text("Borders shown.", NamedTextColor.GREEN));
				}
				player.closeInventory();
			}
			case 15 -> player.openInventory(new DeleteConfirmMenu(plugin, claim).getInventory());
		}
	}
}
