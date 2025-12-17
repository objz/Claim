package dev.objz.claim.gui.framework;

import dev.objz.claim.Claim;
import dev.objz.claim.model.PlayerFlags;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Permissions extends Menu {
	protected final Claim plugin;
	protected final Map<Integer, PlayerFlags> slotToFlag;

	public Permissions(Claim plugin, Region claim, Component title) {
		super(claim, SIZE_SMALL, title);
		this.plugin = plugin;
		this.slotToFlag = new HashMap<>();
	}

	protected abstract Boolean getPermissionState(PlayerFlags flag);

	protected abstract void setPermissionState(PlayerFlags flag, Boolean value);

	protected abstract void onBack(Player player);

	@Override
	protected void build() {
		PlayerFlags[] flags = PlayerFlags.values();
		int[] slots = { 10, 11, 12, 14, 15, 16 };

		for (int i = 0; i < Math.min(flags.length, slots.length); i++) {
			PlayerFlags flag = flags[i];
			Boolean state = getPermissionState(flag);

			slotToFlag.put(slots[i], flag);

			List<Component> lore = new ArrayList<>();
			Component status;

			if (state == null) {
				status = Component.text("INHERIT", NamedTextColor.GRAY);
			} else if (state) {
				status = Component.text("ENABLED", NamedTextColor.GREEN);
			} else {
				status = Component.text("DISABLED", NamedTextColor.RED);
			}

			lore.add(Component.text("Current: ", NamedTextColor.GRAY).append(status));
			lore.add(Component.empty());
			lore.add(Component.text("Left-Click to Enable", NamedTextColor.GREEN));
			lore.add(Component.text("Right-Click to Disable", NamedTextColor.RED));

			if (supportsInheritance()) {
				lore.add(Component.text("Middle-Click to Inherit", NamedTextColor.YELLOW));
			}

			setItem(slots[i], flag.getIcon(), Component.text(flag.getDisplayName(), NamedTextColor.AQUA),
					lore, state != null && state);
		}

		setItem(22, HeadUtil.createCustomHead("Back", HeadUtil.ARROW_LEFT,
				List.of(Component.text("Return to previous menu", NamedTextColor.GRAY))));

		fillBorders();
	}

	protected boolean supportsInheritance() {
		return false;
	}

	@Override
	public void handleClick(Player player, int slot, ClickType clickType) {
		if (slot == 22) {
			playBackSound(player);
			onBack(player);
			return;
		}

		PlayerFlags flag = slotToFlag.get(slot);
		if (flag != null) {
			Boolean currentState = getPermissionState(flag);
			Boolean newState = currentState;

			if (clickType == ClickType.LEFT) {
				newState = true;
			} else if (clickType == ClickType.RIGHT) {
				newState = false;
			} else if (clickType == ClickType.MIDDLE && supportsInheritance()) {
				newState = null;
			} else {
				return;
			}

			if (newState != currentState || (newState == null && currentState != null)) {
				setPermissionState(flag, newState);
				playSuccessSound(player);
				inventory.clear();
				build();
			} else {
				playErrorSound(player);
			}
		}
	}
}
