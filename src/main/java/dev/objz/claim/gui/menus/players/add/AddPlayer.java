package dev.objz.claim.gui.menus.players.add;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.PaginatedMenu;
import dev.objz.claim.gui.menus.players.PlayerManagementMenu;
import dev.objz.claim.model.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class AddPlayer extends PaginatedMenu<Player> {
	private final Claim plugin;

	public AddPlayer(Claim plugin, Region claim) {
		super(claim, Component.text("Add Player"), getAvailablePlayers(claim), ITEM_SLOTS_NORMAL);
		this.plugin = plugin;
	}

	private static List<Player> getAvailablePlayers(Region claim) {
		List<Player> available = new ArrayList<>(Bukkit.getOnlinePlayers());
		available.removeIf(p -> claim.getMembers().containsKey(p.getUniqueId()));
		return available;
	}

	@Override
	protected void build() {
		super.build();

		if (items.isEmpty()) {
			setItem(10, Material.BARRIER,
					Component.text("No Players Available", NamedTextColor.RED),
					List.of(
							Component.text("All online players are", NamedTextColor.GRAY),
							Component.text("already added to this claim",
									NamedTextColor.GRAY)));
		}
	}

	@Override
	public void handleClick(Player player, int slot, ClickType clickType) {
		if (items.isEmpty() && slot == 10) {
			playErrorSound(player);
			return;
		}
		super.handleClick(player, slot, clickType);
	}

	@Override
	protected ItemStack createItemStack(Player player, int index) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta.setOwningPlayer(player);
		meta.displayName(Component.text(player.getName(), NamedTextColor.GREEN));
		meta.lore(List.of(
				Component.text("Click to select role", NamedTextColor.GRAY)));
		head.setItemMeta(meta);
		return head;
	}

	@Override
	protected void handleItemClick(Player clicker, Player target, int index, ClickType clickType) {
		clicker.openInventory(new SelectRole(plugin, claim, target).getInventory());
	}

	@Override
	protected void handleBack(Player player) {
		player.openInventory(new PlayerManagementMenu(plugin, claim).getInventory());
	}
}
