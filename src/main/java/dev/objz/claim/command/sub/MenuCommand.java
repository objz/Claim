package dev.objz.claim.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.objz.claim.Claim;

public class MenuCommand {
	private final Claim plugin;

	public MenuCommand(Claim plugin) {
		this.plugin = plugin;
	}

	public CommandAPICommand getCommand() {
		return new CommandAPICommand("menu")
				.executesPlayer((player, args) -> {
					new InfoCommand(plugin).execute(player, args);
				});
	}
}
