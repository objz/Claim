package dev.objz.claim.gui.framework;

import dev.objz.claim.model.Region;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public class ConfirmationMenu extends Menu {
	private final Component message;
	private final List<Component> confirmLore;
	private final Consumer<Player> onConfirm;
	private final Consumer<Player> onCancel;

	public ConfirmationMenu(Region claim, Component title, Component message, List<Component> confirmLore,
			Consumer<Player> onConfirm, Consumer<Player> onCancel) {
		super(claim, SIZE_SMALL, title);
		this.message = message;
		this.confirmLore = confirmLore;
		this.onConfirm = onConfirm;
		this.onCancel = onCancel;
		build();
	}

	@Override
	protected void build() {
		setItem(11, HeadUtil.createCustomHead("CONFIRM", HeadUtil.CONFIRM,
				confirmLore != null ? confirmLore
						: List.of(
								Component.text("Click to confirm",
										NamedTextColor.GREEN))));

		setItem(13, HeadUtil.createCustomHead("Confirmation", HeadUtil.INFO,
				List.of(message)));

		setItem(15, HeadUtil.createCustomHead("CANCEL", HeadUtil.CANCEL,
				List.of(Component.text("Click to cancel", NamedTextColor.GRAY))));

		fillBorders();
	}

	@Override
	public void handleClick(Player player, int slot, ClickType clickType) {
		if (slot == 11) {
			if (onConfirm != null) {
				playClickSound(player);
				onConfirm.accept(player);
			}
		} else if (slot == 13) {
			playErrorSound(player);
		} else if (slot == 15) {
			if (onCancel != null) {
				playBackSound(player);
				onCancel.accept(player);
			}
		}
	}
}
