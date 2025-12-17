package dev.objz.claim.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HeadUtil {

	private static final Map<UUID, String> textureCache = new HashMap<>();

	public static final String CANCEL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=";
	public static final String CONFIRM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=";
	public static final String DELETE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE2M2IwZjU2ZjdlYzY0ZWFjYmI3MWZjYTMxNTQ5ZDAyMjc0MGQ5YjdkNGI2MTc2MmEyZWZlNTg0MWE0YmYyNSJ9fX0=";
	public static final String BARRIER = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWZkMjQwMDAwMmFkOWZiYmJkMDA2Njk0MWViNWIxYTM4NGFiOWIwZTQ4YTE3OGVlOTZlNGQxMjlhNTIwODY1NCJ9fX0=";
	public static final String INFO = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmEyYWZhN2JiMDYzYWMxZmYzYmJlMDhkMmM1NThhN2RmMmUyYmFjZGYxNWRhYzJhNjQ2NjJkYzQwZjhmZGJhZCJ9fX0=";
	public static final String UTILITIES = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGI2MDdmNDUyZGVlYTZiZDlmODExNGQ0M2RkNDM0MDJlOTNmOWFmMmM4ZmE2ODBmMjkwNDhhZDBiNDdhZWVhMyJ9fX0=";
	public static final String ARROW_LEFT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGUxZGZjMTFhODM3MTExZDIyYjAwMWExNDQ2MWY5YTdmYzA5MzUyMmY4OGM1OGZhZWZkNmFkZWZmY2Q0ZTlhYiJ9fX0=";
	public static final String ARROW_RIGHT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M2OWQ0MTA3NmE4ZGVhNGYwNmQzZjFhOWFjNDdjYzk5Njk4OGI3NGEwOTEzYWIyYWMxYTc0Y2FmNzA4MTkxOCJ9fX0=";
	public static final String CROSS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzkwZjYyZWM1ZmEyZTkzZTY3Y2YxZTAwZGI4YWY0YjQ3YWM3YWM3NjlhYTA5YTIwM2ExZjU3NWExMjcxMGIxMCJ9fX0=";
	public static final String PLAYERS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjliOTU3ODgyNTA0OGQyZTU0NWM4Y2M4MDQ5NWFjYzJjZGNmOTVlMzY2MjVmNmI1N2FiMTllMWUyYThiOWRhOCJ9fX0=";
	public static final String PLAYER = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODg3MGFiOTgwYTgzYTQwNmMwMWJkYTUwNmRkNzlkMjUzYjdlMTkwMzEyYjM0NGQwMTVmNjE3MDg0M2UzOGY0ZCJ9fX0=";
	public static final String SETTINGS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzhmYzE3ZGQwYTdmNGI5ZjZjODg3NDhlZjllZDExYmQyY2M0ODliODk2NzhmM2JiY2FmY2MxNDVhZjBkMTZjZSJ9fX0=";

	public static void cacheTexture(UUID uuid, String texture) {
		textureCache.put(uuid, texture);
	}

	public static void loadCache(File file) {
		if (!file.exists())
			return;
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (String key : config.getKeys(false)) {
			try {
				UUID uuid = UUID.fromString(key);
				String texture = config.getString(key);
				textureCache.put(uuid, texture);
			} catch (IllegalArgumentException ignored) {
			}
		}
	}

	public static void saveCache(File file) {
		YamlConfiguration config = new YamlConfiguration();
		for (Map.Entry<UUID, String> entry : textureCache.entrySet()) {
			config.set(entry.getKey().toString(), entry.getValue());
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ItemStack createCustomHead(String name, String base64, List<Component> lore) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		applyTexture(meta, base64);
		meta.displayName(Component.text(name, NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
		if (lore != null) {
			meta.lore(lore.stream().map(l -> l.decoration(TextDecoration.ITALIC, false)).toList());
		}
		head.setItemMeta(meta);
		return head;
	}

	public static ItemStack createPlayerHead(OfflinePlayer player, List<Component> lore) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();

		String name = player.getName() != null ? player.getName() : "Unknown";
		meta.displayName(Component.text(name, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));

		if (!player.isOnline() && textureCache.containsKey(player.getUniqueId())) {
			applyTexture(meta, textureCache.get(player.getUniqueId()));
		} else {
			meta.setOwningPlayer(player);
		}

		if (lore != null) {
			meta.lore(lore.stream().map(l -> l.decoration(TextDecoration.ITALIC, false)).toList());
		}

		head.setItemMeta(meta);
		return head;
	}

	private static void applyTexture(SkullMeta meta, String base64) {
		UUID uuid = UUID.nameUUIDFromBytes(base64.getBytes());
		PlayerProfile profile = Bukkit.createProfile(uuid, "custom_head");
		profile.setProperty(new ProfileProperty("textures", base64));
		meta.setPlayerProfile(profile);
	}
}
