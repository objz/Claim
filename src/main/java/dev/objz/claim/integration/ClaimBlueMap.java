package dev.objz.claim.integration;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.ShapeMarker;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import dev.objz.claim.Claim;
import dev.objz.claim.model.ClaimRegion;
import org.bukkit.Bukkit;
import org.bukkit.util.BoundingBox;

public class ClaimBlueMap {

	private final Claim plugin;
	private final String MARKER_SET_ID = "claim-markers";

	public ClaimBlueMap(Claim plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		BlueMapAPI.onEnable(api -> {
			Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, (task) -> updateMarkers(api), 20L,
					1200L);
		});
	}

	public void disable() {
	}

	private void updateMarkers(BlueMapAPI api) {
		for (var map : api.getMaps()) {
			MarkerSet markerSet = map.getMarkerSets().computeIfAbsent(MARKER_SET_ID,
					id -> MarkerSet.builder().label("Claims").build());

			for (ClaimRegion claim : plugin.getClaimManager().getAllClaims()) {
				if (!claim.getWorldName().equals(map.getWorld().getId()))
					continue;

				BoundingBox box = claim.getRegion();

				Shape shape = Shape.createRect(box.getMinX(), box.getMinZ(), box.getMaxX(),
						box.getMaxZ());

				ShapeMarker marker = ShapeMarker.builder()
						.shape(shape, (float) box.getMinY())
						.label(claim.getName())
						.fillColor(new Color(0, 100, 255, 50))
						.lineColor(new Color(0, 100, 255, 255))
						.build();

				markerSet.put(claim.getId().toString(), marker);
			}
		}
	}
}
