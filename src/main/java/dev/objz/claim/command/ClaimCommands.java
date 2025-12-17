package dev.objz.claim.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.objz.claim.Claim;
import dev.objz.claim.command.sub.*;

public class ClaimCommands {

	private final Claim plugin;

	public ClaimCommands(Claim plugin) {
		this.plugin = plugin;
	}

	public void register() {
		new CommandAPICommand("claim")
				.withSubcommand(new ToolCommand(plugin).getCommand())
				.withSubcommand(new CreateCommand(plugin).getCommand())
				.withSubcommand(new ListCommand(plugin).getCommand())
				.withSubcommand(new ShowCommand(plugin).getCommand())
				.withSubcommand(new DeleteCommand(plugin).getCommand())
				.withSubcommand(new TransferCommand(plugin).getCommand())
				.withSubcommand(new AdminCommand(plugin).getCommand())
				.withSubcommand(new ResizeCommand(plugin).getCommand())
				.withSubcommand(new MemberCommand(plugin).getCommand())
				.withSubcommand(new FlagCommand(plugin).getCommand())
				.withSubcommand(new RenameCommand(plugin).getCommand())
				.executesPlayer((player, args) -> {
					new InfoCommand(plugin).execute(player, args);
				})
				.register();
	}
}
