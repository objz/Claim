package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.objz.claim.Claim;

public class ToolCommand {
	private final Claim plugin;

	public ToolCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("tool")
				.executesPlayer((player, args) -> {
					plugin.getSelectionManager().toggleTool(player);
				});
	}
}
