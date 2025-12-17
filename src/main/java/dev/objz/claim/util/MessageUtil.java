package dev.objz.claim.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class MessageUtil {

	private static final MiniMessage MM = MiniMessage.miniMessage();
	private static final String PREFIX = "<gradient:#00aaff:#00ffaa><bold>Claim</bold></gradient> <dark_gray>»</dark_gray> ";

	public static void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(MM.deserialize(PREFIX + "<gray>" + message));
	}

	public static void sendSuccess(CommandSender sender, String message) {
		sender.sendMessage(MM.deserialize(PREFIX + "<green>" + message));
	}

	public static void sendError(CommandSender sender, String message) {
		sender.sendMessage(MM.deserialize(PREFIX + "<red>" + message));
	}

	public static void sendInfo(CommandSender sender, String message) {
		sender.sendMessage(MM.deserialize(PREFIX + "<aqua>" + message));
	}

	public static Component parse(String message) {
		return MM.deserialize(message);
	}

	public static Component parse(String message, boolean withPrefix) {
		return withPrefix ? MM.deserialize(PREFIX + message) : MM.deserialize(message);
	}
}
