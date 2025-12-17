package dev.objz.claim.gui.framework;

import dev.objz.claim.model.Region;
import dev.objz.claim.util.HeadUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class PaginatedMenu<T> extends Menu {
	protected final List<T> items;
	protected int currentPage = 0;
	protected final int itemsPerPage;
	protected final int[] itemSlots;

	public static final int[] ITEM_SLOTS_NORMAL = {
			10, 11, 12, 13, 14, 15, 16,
			19, 20, 21, 22, 23, 24, 25,
			28, 29, 30, 31, 32, 33, 34,
			37, 38, 39, 40, 41, 42, 43
	};

	public PaginatedMenu(Region claim, Component title, List<T> items, int[] itemSlots) {
		super(claim, SIZE_LARGE, title);
		this.items = items;
		this.itemSlots = itemSlots;
		this.itemsPerPage = itemSlots.length;
		build();
	}

	@Override
	protected void build() {
		fillBorders();

		int maxPage = (int) Math.ceil((double) items.size() / itemsPerPage) - 1;
		int startIndex = currentPage * itemsPerPage;
		int endIndex = Math.min(startIndex + itemsPerPage, items.size());

		for (int i = startIndex; i < endIndex; i++) {
			int slotIndex = i - startIndex;
			if (slotIndex < itemSlots.length) {
				T item = items.get(i);
				inventory.setItem(itemSlots[slotIndex], createItemStack(item, i));
			}
		}

		if (currentPage > 0) {
			setItem(48, HeadUtil.createCustomHead("Previous Page", HeadUtil.ARROW_LEFT,
					List.of(Component.text("Go to page " + currentPage, NamedTextColor.GRAY))));
		}

		if (currentPage < maxPage) {
			setItem(50, HeadUtil.createCustomHead("Next Page", HeadUtil.ARROW_RIGHT,
					List.of(Component.text("Go to page " + (currentPage + 2),
							NamedTextColor.GRAY))));
		}

		setItem(49, HeadUtil.createCustomHead("Back", HeadUtil.ARROW_LEFT,
				List.of(Component.text("Return to previous menu", NamedTextColor.GRAY))));
	}

	@Override
	public void handleClick(Player player, int slot, ClickType clickType) {
		if (slot == 48 && currentPage > 0) {
			currentPage--;
			inventory.clear();
			build();
			return;
		}

		if (slot == 50) {
			int maxPage = (int) Math.ceil((double) items.size() / itemsPerPage) - 1;
			if (currentPage < maxPage) {
				currentPage++;
				inventory.clear();
				build();
				return;
			}
		}

		if (slot == 49) {
			handleBack(player);
			return;
		}

		for (int i = 0; i < itemSlots.length; i++) {
			if (itemSlots[i] == slot) {
				int itemIndex = currentPage * itemsPerPage + i;
				if (itemIndex < items.size()) {
					handleItemClick(player, items.get(itemIndex), itemIndex, clickType);
				}
				return;
			}
		}
	}

	protected abstract ItemStack createItemStack(T item, int index);

	protected abstract void handleItemClick(Player player, T item, int index, ClickType clickType);

	protected abstract void handleBack(Player player);
}
