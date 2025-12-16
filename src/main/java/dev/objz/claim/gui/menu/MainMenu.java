package dev.objz.claim.gui.menu;

import dev.objz.claim.Claim;
import dev.objz.claim.model.ClaimRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class MainMenu extends ClaimGuiHolder {
	private final Claim plugin;

	public MainMenu(Claim plugin, ClaimRegion claim, Player player) {
		super(claim, 27, Component.text("Claim: " + claim.getName()));
		this.plugin = plugin;

		setItem(10, Material.PLAYER_HEAD, "Members", List.of("Manage added players"));
		setItem(11, Material.EMERALD, "Add Player", List.of("Add a new player"));
		setItem(13, Material.COMPARATOR, "Global Settings", List.of("PvP, Explosions, etc."));
		setItem(15, Material.NAME_TAG, "Role Settings", List.of("Configure permissions per role"));
		setItem(22, Material.BARRIER, "Delete Claim", List.of("Shift-Click to delete"));

		boolean isVisualizing = plugin.getBorderVisualizer().isVisualizing(player);
		String borderAction = isVisualizing ? "Hide Borders" : "Show Borders";
		setItem(26, Material.BEACON, borderAction, List.of("Click to toggle boundaries"));
	}

	@Override
	public void handleClick(Player player, int slot) {
		switch (slot) {
			case 10 -> player.openInventory(new MemberListMenu(plugin, claim).getInventory());
			case 11 -> player.openInventory(new AddPlayerMenu(plugin, claim).getInventory());
			case 13 -> player.openInventory(new SettingsMenu(plugin, claim, true, null).getInventory());
			case 15 -> player.openInventory(new RoleSelectionMenu(plugin, claim).getInventory());
			case 22 -> {
				if (player.isSneaking()) {
					plugin.getClaimManager().deleteClaim(claim.getId());
					plugin.getBorderVisualizer().resetBorder(player);
					player.closeInventory();
					player.sendMessage(Component.text("Claim deleted.", NamedTextColor.RED));
				} else {
					player.sendMessage(Component.text("Sneak + Click to confirm delete.",
							NamedTextColor.RED));
				}
			}
			case 26 -> {
				if (plugin.getBorderVisualizer().isVisualizing(player)) {
					plugin.getBorderVisualizer().resetBorder(player);
					player.sendMessage(Component.text("Borders hidden.", NamedTextColor.YELLOW));
				} else {
					plugin.getBorderVisualizer().showClaimBorder(player, claim);
					player.sendMessage(Component.text("Borders shown.", NamedTextColor.GREEN));
				}
				player.closeInventory();
			}
		}
	}
}
