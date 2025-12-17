package dev.objz.claim.manager;

import dev.objz. claim.Claim;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit. Material;
import org.bukkit. NamespacedKey;
import org.bukkit.Sound;
import org.bukkit. enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org. bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory. EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit. inventory.ItemStack;
import org.bukkit.inventory.meta. ItemMeta;
import org. bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

import java.util. HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectionManager implements Listener {

	private final Claim plugin;
	private final Map<UUID, Location> pos1 = new HashMap<>();
	private final Map<UUID, Location> pos2 = new HashMap<>();

	private static final Material TOOL_MATERIAL = Material.GOLDEN_SHOVEL;
	private static final Component TOOL_NAME = Component.text("Claim Tool", NamedTextColor. GOLD);
	private static final String TOOL_KEY_NAME = "claim_tool";

	public SelectionManager(Claim plugin) {
		this.plugin = plugin;
	}

	public void toggleTool(Player player) {
		if (hasTool(player)) {
			removeTool(player);
			clearSelection(player);
			plugin.getBorderVisualizer().hideBorder(player);
		} else {
			giveTool(player);
		}
	}

	public boolean hasSelection(Player player) {
		return pos1.containsKey(player. getUniqueId()) && pos2.containsKey(player.getUniqueId());
	}

	public Location getPos1(Player player) {
		return pos1.get(player.getUniqueId());
	}

	public Location getPos2(Player player) {
		return pos2.get(player.getUniqueId());
	}

	public void clearSelection(Player player) {
		pos1.remove(player.getUniqueId());
		pos2.remove(player.getUniqueId());
	}

	public void removeTool(Player player) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (isTool(item)) {
				player.getInventory().remove(item);
				break;
			}
		}
	}

	private boolean hasTool(Player player) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (isTool(item)) {
				return true;
			}
		}
		return false;
	}

	private void giveTool(Player player) {
		ItemStack tool = new ItemStack(TOOL_MATERIAL);
		ItemMeta meta = tool.getItemMeta();
		meta.displayName(TOOL_NAME);
		meta.lore(java.util.List.of(
				Component.text("Left-Click: Set Position 1", NamedTextColor.YELLOW),
				Component.text("Right-Click: Set Position 2", NamedTextColor.YELLOW)));

		meta.addEnchant(Enchantment.UNBREAKING, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

		NamespacedKey key = new NamespacedKey(plugin, TOOL_KEY_NAME);
		meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

		tool.setItemMeta(meta);
		player.getInventory().addItem(tool);

		player.sendMessage(Component. text("Left/Right click to select corners", NamedTextColor.GREEN));
	}

	private boolean isTool(ItemStack item) {
		if (item == null || item.getType() != TOOL_MATERIAL)
			return false;

		NamespacedKey key = new NamespacedKey(plugin, TOOL_KEY_NAME);
		return item.hasItemMeta()
				&& item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
	}

	private void updateVisuals(Player player) {
		if (hasSelection(player)) {
			Location p1 = pos1.get(player.getUniqueId());
			Location p2 = pos2.get(player.getUniqueId());

			int minX = Math.min(p1.getBlockX(), p2.getBlockX());
			int minZ = Math.min(p1.getBlockZ(), p2.getBlockZ());
			int maxX = Math.max(p1.getBlockX(), p2.getBlockX()) + 1;
			int maxZ = Math.max(p1.getBlockZ(), p2.getBlockZ()) + 1;

			BoundingBox box = new BoundingBox(minX, 0, minZ, maxX, 256, maxZ);
			plugin.getBorderVisualizer().showBorder(player, box, player.getWorld().getName());
		}
	}

	private String formatLoc(Location loc) {
		return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		if (isTool(item)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot. HAND)
			return;

		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		if (!isTool(item))
			return;

		Action action = event.getAction();
		
		boolean isLeftClick = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
		boolean isRightClick = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;

		if (! isLeftClick && !isRightClick)
			return;

		event.setCancelled(true);

		RayTraceResult result = player.rayTraceBlocks(100, FluidCollisionMode. NEVER);

		if (result == null || result.getHitBlock() == null) {
			player.sendMessage(Component.text("You must look at a block to select it", NamedTextColor.RED));
			return;
		}

		Location target = result.getHitBlock().getLocation();

		if (isLeftClick) {
			pos1.put(player.getUniqueId(), target);
			player.sendMessage(Component.text("Position 1 set at " + formatLoc(target), NamedTextColor.GREEN));
		} else if (isRightClick) {
			pos2.put(player. getUniqueId(), target);
			player.sendMessage(Component.text("Position 2 set at " + formatLoc(target), NamedTextColor.GREEN));
		}

		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2.0f);
		updateVisuals(player);
	}
}
