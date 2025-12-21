package dev.objz.claim.manager;

import dev.objz.claim.Claim;
import dev.objz.claim.util.MessageUtil;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SelectionManager implements Listener {

	private final Claim plugin;
	private final Map<UUID, Location> pos1 = new HashMap<>();
	private final Map<UUID, Location> pos2 = new HashMap<>();

	private static final Material TOOL_MATERIAL = Material.GOLDEN_SHOVEL;
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
		return pos1.containsKey(player.getUniqueId()) && pos2.containsKey(player.getUniqueId());
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

	public boolean hasTool(Player player) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (isTool(item)) {
				return true;
			}
		}
		return false;
	}

	public void giveTool(Player player) {
		ItemStack tool = new ItemStack(TOOL_MATERIAL);
		ItemMeta meta = tool.getItemMeta();
		meta.displayName(MessageUtil.parse("<gold>Claim Tool</gold>", false));
		meta.lore(List.of(
				MessageUtil.parse("<yellow>Left-Click:</yellow> <gray>Set Position 1</gray>", false),
				MessageUtil.parse("<yellow>Right-Click:</yellow> <gray>Set Position 2</gray>", false)));

		meta.addEnchant(Enchantment.UNBREAKING, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

		NamespacedKey key = new NamespacedKey(plugin, TOOL_KEY_NAME);
		meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

		tool.setItemMeta(meta);
		player.getInventory().addItem(tool);

		MessageUtil.sendInfo(player, "Left/Right click to select corners");
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
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		if (!isTool(item))
			return;

		Action action = event.getAction();

		boolean isLeftClick = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
		boolean isRightClick = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;

		if (!isLeftClick && !isRightClick)
			return;

		event.setCancelled(true);

		RayTraceResult result = player.rayTraceBlocks(100, FluidCollisionMode.NEVER);

		if (result == null || result.getHitBlock() == null) {
			MessageUtil.sendError(player, "You must look at a block to select it");
			return;
		}

		Location target = result.getHitBlock().getLocation();

		if (plugin.getClaimManager().getClaimAt(target).isPresent()) {

		}

		if (isLeftClick) {
			pos1.put(player.getUniqueId(), target);

			if (isSelectionOverlapping(player)) {
			}

			MessageUtil.sendInfo(player, "Position 1 set at <yellow>" + formatLoc(target) + "</yellow>");
		} else if (isRightClick) {
			pos2.put(player.getUniqueId(), target);

			if (isSelectionOverlapping(player)) {
			}

			MessageUtil.sendInfo(player, "Position 2 set at <yellow>" + formatLoc(target) + "</yellow>");
		}

		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2.0f);
		updateVisuals(player);
	}

	private boolean isSelectionOverlapping(Player player) {
		if (!hasSelection(player))
			return false;

		Location p1 = pos1.get(player.getUniqueId());
		Location p2 = pos2.get(player.getUniqueId());

		if (!p1.getWorld().equals(p2.getWorld()))
			return false;

		int minX = Math.min(p1.getBlockX(), p2.getBlockX());
		int minZ = Math.min(p1.getBlockZ(), p2.getBlockZ());
		int maxX = Math.max(p1.getBlockX(), p2.getBlockX()) + 1;
		int maxZ = Math.max(p1.getBlockZ(), p2.getBlockZ()) + 1;

		double minY = p1.getWorld().getMinHeight();
		double maxY = p1.getWorld().getMaxHeight();

		BoundingBox box = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);

		return plugin.getClaimManager().isOverlapping(box, p1.getWorld().getName());
	}
}
