package dev.objz.claim.gui.menus.players.edit;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.Menu;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditPermissions extends Menu {
	private final Claim plugin;
	private final UUID targetPlayer;

	public EditPermissions(Claim plugin, Region claim, UUID targetPlayer) {
		super(claim, SIZE_SMALL, Component.text("Permission Override"));
		this.plugin = plugin;
		this.targetPlayer = targetPlayer;
		build();
	}

	@Override
	protected void build() {
		OfflinePlayer op = Bukkit.getOfflinePlayer(targetPlayer);
		String name = op.getName() == null ? "Unknown" : op.getName();

		setItem(4, op.getPlayerProfile().getTextures().getSkin() != null ? org.bukkit.Material.PLAYER_HEAD
				: org.bukkit.Material.PAPER,
				Component.text(name, NamedTextColor.WHITE),
				List.of(Component.text("Editing permissions for this player", NamedTextColor.GRAY)));

		PlayerFlags[] flags = PlayerFlags.values();
		int[] slots = { 10, 11, 12, 14, 15, 16 };

		for (int i = 0; i < Math.min(flags.length, slots.length); i++) {
			PlayerFlags flag = flags[i];
			boolean currentValue = claim.getFlag(targetPlayer, flag);

			List<Component> lore = new ArrayList<>();
			lore.add(Component.text("Current: ", NamedTextColor.GRAY)
					.append(Component.text(currentValue ? "ENABLED" : "DISABLED",
							currentValue ? NamedTextColor.GREEN : NamedTextColor.RED)));
			lore.add(Component.empty());
			lore.add(Component.text("Click to toggle", NamedTextColor.YELLOW));

			setItem(slots[i], flag.getIcon(), Component.text(flag.getDisplayName(), NamedTextColor.AQUA),
					lore, currentValue);
		}

		setItem(22, HeadUtil.createCustomHead("Back", HeadUtil.ARROW_LEFT,
				List.of(Component.text("Return to member list", NamedTextColor.GRAY))));

		fillBorders();
	}

	@Override
	public void handleClick(Player player, int slot, ClickType clickType) {
		if (slot == 22) {
			playBackSound(player);
			player.openInventory(new ListMembers(plugin, claim).getInventory());
			return;
		}

		// Handle flag toggles
		PlayerFlags[] flags = PlayerFlags.values();
		int[] slots = { 10, 11, 12, 14, 15, 16 };

		for (int i = 0; i < Math.min(flags.length, slots.length); i++) {
			if (slots[i] == slot) {
				// For now, show a message and play sound
				playClickSound(player);
				player.sendMessage(Component.text("Player-specific overrides coming soon!",
						NamedTextColor.YELLOW));
				return;
			}
		}
	}
}
