package de.timeout.sudo.bungee.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.netty.packets.PacketRemoteInSudoUsage;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Executes a command as root
 * 
 * Syntax: sudo [COMMAND]
 * @author Timeout
 *
 */
public class SudoCommand extends Command {
	
	private static final String NO_SUDOER = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &c%s is not in the sudoers file. This incident will be reported");
	
	private static final Sudo main = Sudo.getInstance();
	
	public SudoCommand() {
		super("sudo");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		// Validate arguments
		if(args.length > 0) {	
			if(sender instanceof ProxiedPlayer) {
				// get sudoer
				Sudoer sudoer = getSudoer((ProxiedPlayer) sender);
				if(sudoer != null) { 
					// check if user uses -i
					if(!"-i".equalsIgnoreCase(args[0])) {
						// get Command
						String command = String.join(" ", args);
						// if command is a bungeecord command
						if(main.getProxy().getPluginManager().isExecutableCommand(command, sender)) {
							// execute command
							if(awaitingAuthorization(sudoer, command)) {
								// enable root
								sudoer.enableRoot();
								// execute command
								main.getProxy().getPluginManager().dispatchCommand(sender, command);
								// disable root after execution
								sudoer.disableRoot();
							}
						} else {
							// send to bukkit
							PacketRemoteInSudoUsage packet = new PacketRemoteInSudoUsage(((ProxiedPlayer) sender).getUniqueId(), command);
							main.getNettyServer().sendPacket(((ProxiedPlayer) sender).getServer(), packet);
						}
					} else if(awaitingAuthorization(sudoer, "sudo -i")) {
						// enabled root permanently
						sudoer.enableRoot();
					} 
				}
			} else main.getProxy().getPluginManager().dispatchCommand(sender, String.join(" ", args));
		} else sendHelp(sender);
	}
	
	private boolean awaitingAuthorization(@NotNull Sudoer sudoer, String command) {
		// return true if the sudoer is authorized
		if(!sudoer.isAuthorized()) {
			main.getSudoHandler().awaitAuthorization(sudoer, command);
			return false;
		}
		
		return true;
	}
	
	@Nullable
	private Sudoer getSudoer(ProxiedPlayer player) {
		// get User
		User user = main.getUserManager().getUser(player);
		// stop if user is no sudoer
		if(user instanceof Sudoer) {
			// get Sudoer
			return (Sudoer) user;
		} else player.sendMessage(new TextComponent(String.format(NO_SUDOER, player.getName())));
		
		// return false
		return null;
	}

	private void sendHelp(CommandSender sender) {
		// TODO: Write help here.
	}
}
