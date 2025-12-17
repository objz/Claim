package dev.objz.claim.integration;

import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.ShapeMarker;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import dev.objz.claim.Claim;
import dev.objz.claim.model.Region;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlueMap {

	private final Claim plugin;
	private final Map<String, MarkerSet> markerSets = new ConcurrentHashMap<>();
	private static final String MARKER_SET_ID = "claim-markers";
	private static final String MARKER_SET_LABEL = "Claims";

	public BlueMap(Claim plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		if (Bukkit.getPluginManager().isPluginEnabled("BlueMap")) {
			BlueMapAPI.getInstance().ifPresent(this::onBlueMapEnable);
		}
	}

	public void disable() {
		markerSets.clear();
	}

	private void onBlueMapEnable(BlueMapAPI api) {
		plugin.getLogger().info("BlueMap API detected. Enabling integration...");
		markerSets.clear();
		for (Region claim : plugin.getClaimManager().getAllClaims()) {
			updateClaim(claim);
		}
	}

	public void updateClaim(Region claim) {
		if (!Bukkit.getPluginManager().isPluginEnabled("BlueMap"))
			return;

		BlueMapAPI.getInstance().ifPresent(api -> {
			World bukkitWorld = Bukkit.getWorld(claim.getWorldName());
			if (bukkitWorld == null)
				return;

			api.getWorld(bukkitWorld).ifPresent(world -> {
				MarkerSet set = markerSets.computeIfAbsent(claim.getWorldName(), k -> {
					MarkerSet newSet = MarkerSet.builder()
							.label(MARKER_SET_LABEL)
							.build();

					for (BlueMapMap map : world.getMaps()) {
						map.getMarkerSets().put(MARKER_SET_ID, newSet);
					}
					return newSet;
				});

				BoundingBox box = claim.getRegion();

				Vector2d p1 = new Vector2d(box.getMinX(), box.getMinZ());
				Vector2d p2 = new Vector2d(box.getMaxX(), box.getMinZ());
				Vector2d p3 = new Vector2d(box.getMaxX(), box.getMaxZ());
				Vector2d p4 = new Vector2d(box.getMinX(), box.getMaxZ());

				Shape shape = new Shape(new Vector2d[] { p1, p2, p3, p4 });

				OfflinePlayer owner = Bukkit.getOfflinePlayer(claim.getOwner());
				String ownerName = owner.getName() != null ? owner.getName() : "Unknown";

				Color lineColor = computeLineColor(claim.getOwner());
				Color fillColor = computeFillColor(claim.getOwner());
				
				int centerX = (int) box.getCenterX();
				int centerZ = (int) box.getCenterZ();
				int surfaceY = bukkitWorld.getHighestBlockYAt(centerX, centerZ) + 1;

				ShapeMarker marker = ShapeMarker.builder()
						.shape(shape, (float) surfaceY)
						.label(claim.getName())
						.detail("Owner: " + ownerName + "<br>Name: " + claim.getName())
						.lineColor(lineColor)
						.fillColor(fillColor)
						.lineWidth(3)
						.depthTestEnabled(false)
						.build();

				set.getMarkers().put(claim.getId().toString(), marker);
			});
		});
	}

	public void removeClaim(UUID claimId) {
		if (!Bukkit.getPluginManager().isPluginEnabled("BlueMap"))
			return;

		BlueMapAPI.getInstance().ifPresent(api -> {
			for (MarkerSet set : markerSets.values()) {
				set.getMarkers().remove(claimId.toString());
			}
		});
	}

	private Color computeLineColor(UUID owner) {
		float hue = deterministicHue(owner);
		int rgb = java.awt.Color.HSBtoRGB(hue, 0.75f, 0.9f);
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		return new Color(r, g, b, 1.0f);
	}

	private Color computeFillColor(UUID owner) {
		float hue = deterministicHue(owner);
		int rgb = java.awt.Color.HSBtoRGB(hue, 0.75f, 0.95f);
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		return new Color(r, g, b, 0.25f);
	}

	private float deterministicHue(UUID owner) {
		long bits = owner.getMostSignificantBits() ^ owner.getLeastSignificantBits();
		int h = (int) (Math.abs(bits % 360L));
		return (h / 360f);
	}
}
