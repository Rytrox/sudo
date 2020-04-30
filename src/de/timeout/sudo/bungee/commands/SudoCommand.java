package de.timeout.sudo.bungee.commands;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.netty.packets.PacketRemoteInSudoUsage;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SudoCommand extends Command {
	
	private static final Sudo main = Sudo.getInstance();
	
	public SudoCommand() {
		super("sudo");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		// write error if the executor is no player
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			// send help if command has no arguments
			if(args.length > 0) { 
				// filter command (remove all overused sudos)
				List<String> commandArgs = Arrays.stream(args).filter(element -> !"sudo".equalsIgnoreCase(element)).collect(Collectors.toList());
				
				// get user
				User user = main.getUserManager().getUser(p);
				// check if user is a sudoer
				if(user instanceof Sudoer) {
					// check if command is bukkit command
					if(main.getProxy().getPluginManager().isExecutableCommand(args[0], sender)) {
						// get sudoer
						Sudoer sudoer = (Sudoer) user;
						// execute command if sudoer is already authorized
						if(!sudoer.isAuthorized()) {
							// await authorisation
							main.getSudoHandler().awaitAuthorization(sudoer, String.join(" ", commandArgs));
						} else main.getProxy().getPluginManager().dispatchCommand(p, String.join(" ", commandArgs));
					} else {
						// send to bukkit-server
						PacketRemoteInSudoUsage sudo = new PacketRemoteInSudoUsage(p.getUniqueId(), String.join(" ", commandArgs));
						main.getNettyServer().sendPacket(p.getServer(), sudo);
					}
				} else p.sendMessage(new TextComponent(
							ChatColor.translateAlternateColorCodes('&', 
									String.join("&8[&6Sudo&8] &c%s is not in the sudoers file. This incident will be reported", p.getName()))));

			} else sendHelp(sender);
		} else sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cOnly players can perform sudo on bungeecord")));
	}

	private void sendHelp(CommandSender sender) {
		// TODO: Write help here.
	}
}
