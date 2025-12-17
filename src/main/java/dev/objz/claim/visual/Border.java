package dev.objz.claim.visual;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import dev.objz.claim.Claim;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Border {

	private final Claim plugin;
	private final ProtocolManager protocolManager;
	private final Map<UUID, VisualizationData> activeVisuals = new ConcurrentHashMap<>();
	private final AtomicInteger entityIdCounter = new AtomicInteger(Integer.MAX_VALUE - 1000000);

	private static final int VERTICAL_SPACING = 1;
	private static final int HORIZONTAL_SPACING = 1;
	private static final int VERTICAL_RANGE = 10;
	private static final float ITEM_SCALE = 0.5f;
	private static final long UPDATE_INTERVAL_TICKS = 10L;

	public Border(Claim plugin) {
		this.plugin = plugin;
		this.protocolManager = ProtocolLibrary.getProtocolManager();
	}

	public void showBorder(Player player, BoundingBox box, String worldName) {
		hideBorder(player);
		VisualizationData data = new VisualizationData(box, worldName);
		activeVisuals.put(player.getUniqueId(), data);
		startUpdateTask(player, data);
	}

	public void hideBorder(Player player) {
		VisualizationData data = activeVisuals.remove(player.getUniqueId());
		if (data == null)
			return;

		if (data.updateTask != null) {
			data.updateTask.cancel();
		}

		destroyEntities(player, data.spawnedEntityIds);
		data.spawnedEntityIds.clear();
	}

	public boolean isActive(Player player) {
		return activeVisuals.containsKey(player.getUniqueId());
	}

	public void cleanup() {
		for (Map.Entry<UUID, VisualizationData> entry : activeVisuals.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			if (player != null && player.isOnline()) {
				hideBorder(player);
			}
		}
		activeVisuals.clear();
	}

	private void startUpdateTask(Player player, VisualizationData data) {
		data.updateTask = player.getScheduler().runAtFixedRate(plugin, (task) -> {
			if (!player.isOnline() || !activeVisuals.containsKey(player.getUniqueId())) {
				task.cancel();
				return;
			}

			if (!player.getWorld().getName().equals(data.worldName)) {
				return;
			}

			updateMarkers(player, data);
		}, null, 1L, UPDATE_INTERVAL_TICKS);
	}

	private void updateMarkers(Player player, VisualizationData data) {
		BoundingBox box = data.box;
		Location playerLoc = player.getLocation();

		int playerY = playerLoc.getBlockY();
		int baseHeight = (playerY / VERTICAL_SPACING) * VERTICAL_SPACING;

		int minY = Math.max((int) box.getMinY(), baseHeight - VERTICAL_RANGE);
		int maxY = Math.min((int) box.getMaxY(), baseHeight + VERTICAL_RANGE);

		Set<MarkerPosition> newMarkers = calculateMarkerPositions(box, minY, maxY, playerLoc,
				player.getWorld());

		Set<MarkerPosition> toAdd = new HashSet<>(newMarkers);
		toAdd.removeAll(data.currentMarkers);

		Set<MarkerPosition> toRemove = new HashSet<>(data.currentMarkers);
		toRemove.removeAll(newMarkers);

		List<Integer> idsToRemove = new ArrayList<>();
		for (MarkerPosition pos : toRemove) {
			Integer id = data.markerToEntityId.remove(pos);
			if (id != null) {
				idsToRemove.add(id);
				data.spawnedEntityIds.remove(id);
			}
		}
		if (!idsToRemove.isEmpty()) {
			destroyEntities(player, idsToRemove);
		}

		for (MarkerPosition pos : toAdd) {
			int entityId = entityIdCounter.getAndDecrement();
			spawnMarkerEntity(player, pos, entityId);
			data.markerToEntityId.put(pos, entityId);
			data.spawnedEntityIds.add(entityId);
		}

		data.currentMarkers = newMarkers;
	}

	private Set<MarkerPosition> calculateMarkerPositions(BoundingBox box, int minY, int maxY, Location playerLoc,
			World world) {
		Set<MarkerPosition> markers = new HashSet<>();

		int boxMinX = (int) Math.floor(box.getMinX());
		int boxMaxX = (int) Math.floor(box.getMaxX());
		int boxMinZ = (int) Math.floor(box.getMinZ());
		int boxMaxZ = (int) Math.floor(box.getMaxZ());

		int renderDistance = 32;
		int playerX = playerLoc.getBlockX();
		int playerZ = playerLoc.getBlockZ();

		java.util.function.BiPredicate<Integer, Integer> isCorner = (x, z) -> (x == boxMinX || x == boxMaxX)
				&& (z == boxMinZ || z == boxMaxZ);

		java.util.function.Predicate<MarkerPosition> isVisible = (pos) -> {
			if ((pos.x + pos.y + pos.z) % 2 == 0)
				return false;
			Block block = world.getBlockAt(pos.x, pos.y, pos.z);
			return !block.getType().isOccluding();
		};

		for (int y = minY; y <= maxY; y += VERTICAL_SPACING) {
			for (int z = boxMinZ; z <= boxMaxZ; z += HORIZONTAL_SPACING) {
				if (isCorner.test(boxMinX, z))
					continue;
				if (isInRange(boxMinX, z, playerX, playerZ, renderDistance)) {
					MarkerPosition pos = new MarkerPosition(boxMinX, y, z, WallSide.WEST);
					if (isVisible.test(pos))
						markers.add(pos);
				}
			}
		}

		for (int y = minY; y <= maxY; y += VERTICAL_SPACING) {
			for (int z = boxMinZ; z <= boxMaxZ; z += HORIZONTAL_SPACING) {
				if (isCorner.test(boxMaxX, z))
					continue;
				if (isInRange(boxMaxX, z, playerX, playerZ, renderDistance)) {
					MarkerPosition pos = new MarkerPosition(boxMaxX, y, z, WallSide.EAST);
					if (isVisible.test(pos))
						markers.add(pos);
				}
			}
		}

		for (int y = minY; y <= maxY; y += VERTICAL_SPACING) {
			for (int x = boxMinX; x <= boxMaxX; x += HORIZONTAL_SPACING) {
				if (isCorner.test(x, boxMinZ))
					continue;
				if (isInRange(x, boxMinZ, playerX, playerZ, renderDistance)) {
					MarkerPosition pos = new MarkerPosition(x, y, boxMinZ, WallSide.NORTH);
					if (isVisible.test(pos))
						markers.add(pos);
				}
			}
		}

		for (int y = minY; y <= maxY; y += VERTICAL_SPACING) {
			for (int x = boxMinX; x <= boxMaxX; x += HORIZONTAL_SPACING) {
				if (isCorner.test(x, boxMaxZ))
					continue;
				if (isInRange(x, boxMaxZ, playerX, playerZ, renderDistance)) {
					MarkerPosition pos = new MarkerPosition(x, y, boxMaxZ, WallSide.SOUTH);
					if (isVisible.test(pos))
						markers.add(pos);
				}
			}
		}

		return markers;
	}

	private boolean isInRange(int x, int z, int playerX, int playerZ, int range) {
		return Math.abs(x - playerX) <= range && Math.abs(z - playerZ) <= range;
	}

	private void spawnMarkerEntity(Player player, MarkerPosition pos, int entityId) {
		try {
			PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);

			UUID entityUUID = UUID.randomUUID();

			spawnPacket.getIntegers().write(0, entityId);
			spawnPacket.getUUIDs().write(0, entityUUID);
			spawnPacket.getEntityTypeModifier().write(0, EntityType.ITEM_DISPLAY);

			double x = pos.x + 0.5;
			double y = pos.y + 0.5;
			double z = pos.z + 0.5;

			float yaw = 0;
			switch (pos.side) {
				case NORTH -> yaw = 0;
				case SOUTH -> yaw = 180;
				case WEST -> yaw = 90;
				case EAST -> yaw = 270;
			}

			byte yawByte = (byte) (yaw * 256.0F / 360.0F);

			spawnPacket.getDoubles().write(0, x);
			spawnPacket.getDoubles().write(1, y);
			spawnPacket.getDoubles().write(2, z);

			spawnPacket.getIntegers().write(1, 0);
			spawnPacket.getIntegers().write(2, 0);
			spawnPacket.getIntegers().write(3, 0);

			spawnPacket.getBytes().write(0, (byte) 0);
			spawnPacket.getBytes().write(1, yawByte);
			spawnPacket.getBytes().write(2, yawByte);

			spawnPacket.getIntegers().write(4, 0);

			protocolManager.sendServerPacket(player, spawnPacket);
			sendMetadataPacket(player, entityId);

		} catch (Exception e) {
			plugin.getLogger().warning("Failed to spawn marker entity: " + e.getMessage());
		}
	}

	private void sendMetadataPacket(Player player, int entityId) {
		try {
			PacketContainer metadataPacket = protocolManager
					.createPacket(PacketType.Play.Server.ENTITY_METADATA);
			metadataPacket.getIntegers().write(0, entityId);

			List<WrappedDataValue> dataValues = new ArrayList<>();

			byte flags = 0x20;
			dataValues.add(new WrappedDataValue(0, WrappedDataWatcher.Registry.get((Type) Byte.class),
					flags));

			Vector3f scale = new Vector3f(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
			dataValues.add(new WrappedDataValue(12, WrappedDataWatcher.Registry.get((Type) Vector3f.class),
					scale));

			ItemStack displayItem = new ItemStack(Material.BARRIER);
			Object nmsItem = BukkitConverters.getItemStackConverter().getGeneric(displayItem);
			dataValues.add(new WrappedDataValue(23,
					WrappedDataWatcher.Registry.getItemStackSerializer(false), nmsItem));

			dataValues.add(new WrappedDataValue(24, WrappedDataWatcher.Registry.get((Type) Byte.class),
					(byte) 8));
			dataValues.add(new WrappedDataValue(15, WrappedDataWatcher.Registry.get((Type) Byte.class),
					(byte) 0));

			int brightness = (15 << 4) | (15 << 20);
			dataValues.add(new WrappedDataValue(16, WrappedDataWatcher.Registry.get((Type) Integer.class),
					brightness));
			dataValues.add(new WrappedDataValue(17, WrappedDataWatcher.Registry.get((Type) Float.class),
					1.0f));

			metadataPacket.getDataValueCollectionModifier().write(0, dataValues);
			protocolManager.sendServerPacket(player, metadataPacket);

		} catch (Exception e) {
			plugin.getLogger().warning("Failed to send metadata packet: " + e.getMessage());
		}
	}

	private void destroyEntities(Player player, Collection<Integer> entityIds) {
		if (entityIds.isEmpty())
			return;

		try {
			PacketContainer destroyPacket = protocolManager
					.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
			destroyPacket.getIntLists().write(0, new ArrayList<>(entityIds));
			protocolManager.sendServerPacket(player, destroyPacket);
		} catch (Exception e) {
			plugin.getLogger().warning("Failed to destroy entities: " + e.getMessage());
		}
	}

	private static class VisualizationData {
		final BoundingBox box;
		final String worldName;
		final Set<Integer> spawnedEntityIds = ConcurrentHashMap.newKeySet();
		final Map<MarkerPosition, Integer> markerToEntityId = new ConcurrentHashMap<>();
		Set<MarkerPosition> currentMarkers = new HashSet<>();
		ScheduledTask updateTask;

		VisualizationData(BoundingBox box, String worldName) {
			this.box = box;
			this.worldName = worldName;
		}
	}

	private record MarkerPosition(int x, int y, int z, WallSide side) {
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			MarkerPosition that = (MarkerPosition) o;
			return x == that.x && y == that.y && z == that.z && side == that.side;
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y, z, side);
		}
	}

	private enum WallSide {
		NORTH, SOUTH, EAST, WEST
	}
}
