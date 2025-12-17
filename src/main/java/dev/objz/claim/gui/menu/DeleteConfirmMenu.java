package dev.objz.claim.gui.menu;

import dev.objz.claim.Claim;
import dev.objz. claim.model.Region;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text. Component;
import net.kyori.adventure.text.format. NamedTextColor;
import org.bukkit.Sound;
import org.bukkit. entity.Player;

import java. util.List;

public class DeleteConfirmMenu extends ClaimGuiHolder {
	private final Claim plugin;

	public DeleteConfirmMenu(Claim plugin, Region claim) {
		super(claim, SIZE_SMALL, Component.text("Confirm Deletion"));
		this.plugin = plugin;

		setCustomHead(11, HeadUtil. CONFIRM, "CONFIRM DELETE", List.of(
				Component.text("Permanently delete this claim.", NamedTextColor.RED),
				Component.text("", NamedTextColor.GRAY),
				Component.text("Cannot be undone!", NamedTextColor. DARK_RED)));

		setCustomHead(13, HeadUtil.DELETE, "Deletion", List.of(
				Component. text("Are you sure you want", NamedTextColor.GRAY),
				Component.text("to delete this claim?", NamedTextColor.GRAY)));

		setCustomHead(15, HeadUtil.CANCEL, "CANCEL", List.of(
				Component.text("Return to utilities", NamedTextColor. GRAY)));

		fillBorders();
	}

	@Override
	public void handleClick(Player player, int slot) {
		if (slot == 11) {
			plugin.getClaimManager().deleteClaim(claim. getId());
			plugin.getBorderVisualizer().hideBorder(player);
			player.closeInventory();
			player.sendMessage(Component.text("Claim deleted successfully.", NamedTextColor.RED));
			player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
		} else if (slot == 15) {
			player.openInventory(new UtilsMenu(plugin, claim).getInventory());
		}
	}
}
