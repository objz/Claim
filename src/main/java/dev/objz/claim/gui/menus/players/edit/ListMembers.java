package dev.objz.claim.gui.menus.players.edit;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.PaginatedMenu;
import dev.objz.claim.gui.menus.players.PlayerManagementMenu;
import dev.objz.claim.model.Region;
import dev.objz.claim.model.Roles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListMembers extends PaginatedMenu<Map.Entry<UUID, Roles>> {
	private final Claim plugin;

	public ListMembers(Claim plugin, Region claim) {
		super(claim, Component.text("Member List"), new ArrayList<>(claim.getMembers().entrySet()),
				ITEM_SLOTS_NORMAL);
		this.plugin = plugin;
	}

	@Override
	protected ItemStack createItemStack(Map.Entry<UUID, Roles> entry, int index) {
		UUID uuid = entry.getKey();
		Roles role = entry.getValue();

		OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
		String name = op.getName() == null ? "Unknown" : op.getName();

		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta.setOwningPlayer(op);
		meta.displayName(Component.text(name, NamedTextColor.WHITE));

		List<Component> lore = new ArrayList<>();
		lore.add(Component.empty());
		lore.add(Component.text("Role: ", NamedTextColor.GRAY).append(role.getFormattedName()));
		lore.add(Component.empty());

		if (role != Roles.OWNER) {
			lore.add(Component.text("Left-Click ", NamedTextColor.YELLOW)
					.append(Component.text("Change Role", NamedTextColor.GRAY)));
			lore.add(Component.text("Right-Click ", NamedTextColor.RED)
					.append(Component.text("Permission Override", NamedTextColor.GRAY)));
		} else {
			lore.add(Component.text("Owner cannot be modified", NamedTextColor.RED));
		}

		meta.lore(lore);
		head.setItemMeta(meta);
		return head;
	}

	@Override
	protected void handleItemClick(Player player, Map.Entry<UUID, Roles> entry, int index, ClickType clickType) {
		UUID targetUuid = entry.getKey();
		Roles currentRole = entry.getValue();

		if (currentRole == Roles.OWNER) {
			playErrorSound(player);
			return;
		}

		if (clickType == ClickType.LEFT) {
			playClickSound(player);
			Roles nextRole = getNextRole(currentRole);
			claim.setRole(targetUuid, nextRole);
			player.sendMessage(Component.text("Role changed to " + nextRole.getDisplayName(),
					NamedTextColor.GREEN));
			inventory.clear();
			build();
		} else if (clickType == ClickType.RIGHT) {
			playClickSound(player);
			player.openInventory(
					new EditPermissions(plugin, claim, targetUuid).getInventory());
		}
	}

	@Override
	protected void handleBack(Player player) {
		playBackSound(player);
		player.openInventory(new PlayerManagementMenu(plugin, claim).getInventory());
	}

	private Roles getNextRole(Roles current) {
		return switch (current) {
			case ADMIN -> Roles.BUILDER;
			case BUILDER -> Roles.VISITOR;
			default -> Roles.ADMIN;
		};
	}
}
