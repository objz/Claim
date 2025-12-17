package dev.objz.claim.gui.menus.roles;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.Menu;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import dev.objz.claim.model.Roles;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionMenu extends Menu {
	private final Claim plugin;
	private final Roles role;
	private final Map<Integer, PlayerFlags> slotToFlag;

	public PermissionMenu(Claim plugin, Region claim, Roles role) {
		super(claim, SIZE_SMALL, Component.text(role.getDisplayName() + " Permissions"));
		this.plugin = plugin;
		this.role = role;
		this.slotToFlag = new HashMap<>();
		build();
	}

	@Override
	protected void build() {
		PlayerFlags[] flags = PlayerFlags.values();
		int[] slots = { 10, 11, 12, 14, 15, 16 };

		for (int i = 0; i < Math.min(flags.length, slots.length); i++) {
			PlayerFlags flag = flags[i];
			boolean currentValue = claim.getFlag(role, flag);

			slotToFlag.put(slots[i], flag);

			List<Component> lore = new ArrayList<>();
			lore.add(Component.text("Current: ", NamedTextColor.GRAY)
					.append(Component.text(currentValue ? "ENABLED" : "DISABLED",
							currentValue ? NamedTextColor.GREEN : NamedTextColor.RED)));
			lore.add(Component.empty());
			lore.add(Component.text("Left-Click to Enable", NamedTextColor.GREEN));
			lore.add(Component.text("Right-Click to Disable", NamedTextColor.RED));

			setItem(slots[i], flag.getIcon(), Component.text(flag.getDisplayName(), NamedTextColor.AQUA),
					lore, currentValue);
		}

		setItem(22, HeadUtil.createCustomHead("Back", HeadUtil.ARROW_LEFT,
				List.of(Component.text("Return to role list", NamedTextColor.GRAY))));

		fillBorders();
	}

	@Override
	public void handleClick(Player player, int slot, ClickType clickType) {
		if (slot == 22) {
			playBackSound(player);
			player.openInventory(new RoleMenu(plugin, claim).getInventory());
			return;
		}

		PlayerFlags flag = slotToFlag.get(slot);
		if (flag != null) {
			boolean currentValue = claim.getFlag(role, flag);
			boolean newValue = currentValue;

			if (clickType == ClickType.LEFT) {
				newValue = true;
			} else if (clickType == ClickType.RIGHT) {
				newValue = false;
			} else {
				return;
			}

			if (newValue != currentValue) {
				claim.setFlag(role, flag, newValue);
				playSuccessSound(player);
				player.openInventory(new PermissionMenu(plugin, claim, role).getInventory());
			} else {
				playErrorSound(player);
			}
		}
	}
}
