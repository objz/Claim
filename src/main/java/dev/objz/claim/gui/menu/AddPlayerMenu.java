package dev.objz.claim.gui.menu;

import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.model.Roles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class AddPlayerMenu extends ClaimGuiHolder {
	private final Claim plugin;
	private final List<Player> nearby;

	public AddPlayerMenu(Claim plugin, Region claim) {
		super(claim, SIZE_LARGE, Component.text("Add Player"));
		this.plugin = plugin;
		this.nearby = new ArrayList<>(Bukkit.getOnlinePlayers());

		nearby.removeIf(p -> claim.getMembers().containsKey(p.getUniqueId()));

		fillBorders();

		if (nearby.isEmpty()) {
			setItem(10, Material.BARRIER, "No Players Found", List.of(
					Component.text("There are no other players", NamedTextColor.GRAY),
					Component.text("online to invite.", NamedTextColor.GRAY)));
		} else {
			int slot = 10;
			for (Player p : nearby) {
				if ((slot + 1) % 9 == 0)
					slot += 2;
				if (slot >= 44)
					break;

				ItemStack head = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) head.getItemMeta();
				meta.setOwningPlayer(p);
				meta.displayName(Component.text(p.getName(), NamedTextColor.GREEN));
				meta.lore(List.of(Component.text("Click to add as Visitor", NamedTextColor.GRAY)));
				head.setItemMeta(meta);
				inventory.setItem(slot, head);

				slot++;
			}
		}

		addBackButton(49);
	}

	@Override
	public void handleClick(Player player, int slot) {
		if (slot == 49) {
			player.openInventory(new MainMenu(plugin, claim).getInventory());
			return;
		}

		ItemStack clicked = inventory.getItem(slot);
		if (clicked == null || clicked.getType() != Material.PLAYER_HEAD)
			return;

		SkullMeta meta = (SkullMeta) clicked.getItemMeta();
		if (meta.getOwningPlayer() == null || meta.getOwningPlayer().getName() == null)
			return;

		String targetName = meta.getOwningPlayer().getName();
		Player target = Bukkit.getPlayer(targetName);

		if (target != null) {
			claim.setRole(target.getUniqueId(), Roles.VISITOR);
			player.sendMessage(Component.text("Added " + target.getName() + " as Visitor.",
					NamedTextColor.GREEN));
			player.openInventory(new MainMenu(plugin, claim).getInventory());
		}
	}
}
