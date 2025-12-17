package dev.objz.claim. gui.menu;

import dev. objz.claim.Claim;
import dev.objz.claim.model.Region;
import dev.objz.claim.model. Roles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit. Bukkit;
import org.bukkit.Material;
import org.bukkit. OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory. meta. SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemberListMenu extends ClaimGuiHolder {
	private final Claim plugin;
	private final List<UUID> members;

	public MemberListMenu(Claim plugin, Region claim) {
		super(claim, SIZE_LARGE, Component.text("Member List"));
		this.plugin = plugin;
		this.members = new ArrayList<>(claim.getMembers().keySet());

		fillBorders();

		int slot = 10;
		for (UUID uuid : members) {
			if ((slot + 1) % 9 == 0) slot += 2;
			if (slot >= 44) break;

			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
			String name = op.getName() == null ? "Unknown" : op.getName();
			Roles role = claim.getRole(uuid);

			ItemStack head = new ItemStack(Material. PLAYER_HEAD);
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			meta.setOwningPlayer(op);
			meta.displayName(Component.text(name, NamedTextColor.WHITE));

			List<Component> lore = new ArrayList<>();
			lore.add(Component.empty());
			lore.add(Component.text("Role: " + role.getDisplayName(), role == Roles.OWNER ? NamedTextColor.RED : NamedTextColor. GRAY));
			lore.add(Component.empty());

			if (role != Roles.OWNER) {
				lore.add(Component. text("Left-Click ", NamedTextColor.YELLOW).append(Component.text("to Cycle Role", NamedTextColor. GRAY)));
				lore.add(Component.text("Right-Click ", NamedTextColor.RED).append(Component.text("to Kick", NamedTextColor. GRAY)));
			} else {
				lore.add(Component.text("Owner cannot be modified.", NamedTextColor.RED));
			}

			meta.lore(lore);
			head.setItemMeta(meta);
			inventory.setItem(slot++, head);
		}

		addBackButton(49);
	}

	@Override
	public void handleClick(Player player, int slot) {
		if (slot == 49) {
			player.openInventory(new MainMenu(plugin, claim).getInventory());
			return;
		}

		int currentSlot = 10;
		for (UUID targetUuid : members) {
			if ((currentSlot + 1) % 9 == 0) currentSlot += 2;

			if (currentSlot == slot) {
				if (targetUuid.equals(claim.getOwner())) {
					player.sendMessage(Component.text("You cannot modify the owner.", NamedTextColor.RED));
					return;
				}

				Roles currentRole = claim.getRole(targetUuid);
				Roles nextRole = getNextRole(currentRole);

				claim.setRole(targetUuid, nextRole);
				player.openInventory(new MemberListMenu(plugin, claim).getInventory());
				return;
			}
			currentSlot++;
		}
	}

	private Roles getNextRole(Roles current) {
		return switch (current) {
			case ADMIN -> Roles.BUILDER;
			case BUILDER -> Roles. VISITOR;
			default -> Roles.ADMIN;
		};
	}
}
