package dev.objz.claim.gui.menu;

import dev.objz.claim.Claim;
import dev.objz.claim.model.ClaimRegion;
import dev.objz.claim.model.ClaimRole;
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

	public AddPlayerMenu(Claim plugin, ClaimRegion claim) {
		super(claim, 54, Component.text("Add Player"));
		this.plugin = plugin;
		this.nearby = new ArrayList<>(Bukkit.getOnlinePlayers());
		nearby.removeIf(p -> claim.getMembers().containsKey(p.getUniqueId()));

		int slot = 0;
		for (Player p : nearby) {
			ItemStack head = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			meta.setOwningPlayer(p);
			meta.displayName(Component.text(p.getName()));
			head.setItemMeta(meta);
			inventory.setItem(slot++, head);
		}
		setItem(49, Material.ARROW, "Back", null);
	}

	@Override
	public void handleClick(Player player, int slot) {
		if (slot == 49) {
			player.openInventory(new MainMenu(plugin, claim, player).getInventory());
			return;
		}
		if (slot < nearby.size()) {
			Player target = nearby.get(slot);
			claim.setMemberRole(target.getUniqueId(), ClaimRole.SPECTATOR);
			player.sendMessage(Component.text("Added " + target.getName() + " as Spectator.",
					NamedTextColor.GREEN));
			player.openInventory(new MainMenu(plugin, claim, player).getInventory());
		}
	}
}
