package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.objz.claim.Claim;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ToolCommand {
	private final Claim plugin;

	public ToolCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("tool")
				.executesPlayer((player, args) -> {
					plugin.getSelectionManager().giveTool(player);
					player.sendMessage(Component.text(
							"Received claim tool. Left/Right click to select corners.",
							NamedTextColor.GREEN));
				});
	}
}
