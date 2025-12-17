package dev.objz.claim.gui.menus.settings;

import dev.objz.claim.Claim;
import dev.objz.claim.gui.framework.Menu;
import dev.objz.claim.gui.menus.main.MainMenu;
import dev.objz.claim.model.GlobalFlags;
import dev.objz.claim.model.Region;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsMenu extends Menu {
	private final Claim plugin;
	private final Map<Integer, GlobalFlags> slotToFlag;

	private static final GlobalFlags[] ROW_1 = {
			GlobalFlags.BLOCK_EXPLOSIONS,
			GlobalFlags.BLOCK_EXPLOSION_BLOCK_DAMAGE,
			GlobalFlags.BLOCK_EXPLOSION_ENTITY_DAMAGE,
			GlobalFlags.ENTITY_EXPLOSIONS,
			GlobalFlags.ENTITY_EXPLOSION_BLOCK_DAMAGE,
			GlobalFlags.ENTITY_EXPLOSION_ENTITY_DAMAGE,
			GlobalFlags.WIND_CHARGE
	};

	private static final GlobalFlags[] ROW_2 = {
			GlobalFlags.FIRE_SPREAD,
			GlobalFlags.FIRE_IGNITION,
			GlobalFlags.LAVA_FLOW,
			GlobalFlags.WATER_FLOW,
			GlobalFlags.LAVA_PICKUP,
			GlobalFlags.WATER_PICKUP,
			GlobalFlags.SPONGE_ABSORB
	};

	private static final GlobalFlags[] ROW_3 = {
			GlobalFlags.MONSTER_SPAWNING,
			GlobalFlags.ANIMAL_SPAWNING,
			GlobalFlags.ENTITY_DAMAGE,
			GlobalFlags.ENTITY_GRIEF,
			GlobalFlags.ENTITY_INTERACT,
			GlobalFlags.ENTITY_DESPAWN,
			GlobalFlags.ENTITY_TARGET_PLAYER
	};

	private static final GlobalFlags[] ROW_4 = {
			GlobalFlags.TREE_GROWTH,
			GlobalFlags.CROP_GROWTH,
			GlobalFlags.CROP_TRAMPLING,
			GlobalFlags.VINE_GROWTH,
			GlobalFlags.LEAF_DECAY,
			GlobalFlags.MUSHROOM_SPREAD,
			GlobalFlags.GRASS_SPREAD
	};

	public SettingsMenu(Claim plugin, Region claim) {
		super(claim, SIZE_LARGE, Component.text("Settings"));
		this.plugin = plugin;
		this.slotToFlag = new HashMap<>();
		build();
	}

	@Override
	protected void build() {
		fillRow(10, ROW_1);
		fillRow(19, ROW_2);
		fillRow(28, ROW_3);
		fillRow(37, ROW_4);

		setItem(49, HeadUtil.createCustomHead("Back", HeadUtil.ARROW_LEFT,
				List.of(Component.text("Return to main menu", NamedTextColor.GRAY))));

		fillBorders();
	}

	private void fillRow(int startSlot, GlobalFlags[] flags) {
		for (int i = 0; i < flags.length; i++) {
			GlobalFlags flag = flags[i];
			if (flag == null)
				continue;

			int slot = startSlot + i;
			boolean currentValue = claim.getFlag(flag);

			slotToFlag.put(slot, flag);

			List<Component> lore = new ArrayList<>();
			lore.add(Component.text("Current:  ", NamedTextColor.GRAY)
					.append(Component.text(currentValue ? "ENABLED" : "DISABLED",
							currentValue ? NamedTextColor.GREEN : NamedTextColor.RED)));
			lore.add(Component.empty());
			lore.add(Component.text("Left-Click to Enable", NamedTextColor.GREEN));
			lore.add(Component.text("Right-Click to Disable", NamedTextColor.RED));

			setItem(slot, flag.getIcon(), Component.text(flag.getDisplayName(), NamedTextColor.AQUA),
					lore, currentValue);
		}
	}

	@Override
	public void handleClick(Player player, int slot, ClickType clickType) {
		if (slot == 49) {
			playBackSound(player);
			player.openInventory(new MainMenu(plugin, claim).getInventory());
			return;
		}

		GlobalFlags flag = slotToFlag.get(slot);
		if (flag != null) {
			boolean currentValue = claim.getFlag(flag);
			boolean newValue = currentValue;

			if (clickType == ClickType.LEFT) {
				newValue = true;
			} else if (clickType == ClickType.RIGHT) {
				newValue = false;
			} else {
				return;
			}

			if (newValue != currentValue) {
				claim.setFlag(flag, newValue);
				playSuccessSound(player);
				build();
			} else {
				playErrorSound(player);
			}
		}
	}
}
