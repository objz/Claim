package dev.objz.claim.gui.menu;

import dev.objz.claim.Claim;
import dev.objz.claim.model.ClaimRegion;
import dev.objz.claim.model.ClaimRole;
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
import java.util.UUID;

public class MemberListMenu extends ClaimGuiHolder {
	private final Claim plugin;
	private final List<UUID> members;

	public MemberListMenu(Claim plugin, ClaimRegion claim) {
		super(claim, 54, Component.text("Members"));
		this.plugin = plugin;
		this.members = new ArrayList<>(claim.getMembers().keySet());
		int slot = 0;

		for (UUID uuid : members) {
			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
			String name = op.getName() == null ? "Unknown" : op.getName();
			ClaimRole role = claim.getRole(uuid);

			ItemStack head = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			meta.setOwningPlayer(op);
			meta.displayName(Component.text(name, NamedTextColor.WHITE));
			meta.lore(List.of(
					Component.text("Role: " + role.getDisplayName(),
							role == ClaimRole.OWNER ? NamedTextColor.RED
									: NamedTextColor.GRAY),
					Component.text("Left-Click to Change Role"),
					Component.text("Right-Click to Kick")));
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
		if (slot < members.size()) {
			UUID target = members.get(slot);
			if (target.equals(claim.getOwner()))
				return;

			claim.setMemberRole(target, ClaimRole.VISITOR);
			player.openInventory(new MemberListMenu(plugin, claim).getInventory());
		}
	}
}
