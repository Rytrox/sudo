package de.timeout.sudo.bungee.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.bungee.permissions.ProxySudoExecution;
import de.timeout.sudo.bungee.users.ProxyUser;
import de.timeout.sudo.netty.packets.PacketRemoteInApplyContainer;
import de.timeout.sudo.security.Authorizable;
import de.timeout.sudo.users.AuthorizableUser;
import de.timeout.sudo.users.User;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Executes a command as root
 * 
 * Syntax: sudo [COMMAND]
 * @author Timeout
 *
 */
public class SudoCommand extends Command implements Listener {
	
	private static final String ENTER_PASSWORD_AGAIN = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cThis didn't worked. Please try again");
	private static final String ERROR_MAX_REACHED = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cSudo-Authentification failed.");
	private static final String ENTER_PASSWORD = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &7Enter password");
	private static final String NO_SUDOER = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &c%s is not in the sudoers file. This incident will be reported");
	
	private static final Sudo main = Sudo.getInstance();
	
	private final Map<AuthorizableUser, ProxySudoExecution> cache = new HashMap<>();
	
	public SudoCommand() {
		super("sudo");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		// get User
		ProxyUser executor = (ProxyUser) main.getUserManager().getUser(sender);
		
		// check if args length is valid
		if(args.length > 0) {
			// create sudo attempt
			ProxySudoExecution attempts = cache.put(executor, new ProxySudoExecution(executor, String.join(" ", args)));
			
			// execute command if user is authorized
			if(!attempts.isAuthorized()) {
				// send password required to user
				sender.sendMessage(new TextComponent(ENTER_PASSWORD));
			} else executeCommand(executor);
		}
	}
	
	@EventHandler
	public void onSendingPassword(ChatEvent event) {
		// check if sender is a player
		if(event.getSender() instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) event.getSender();
			
			// only continue if user is authrizable
			User user = main.getUserManager().getUser(player);
			if(user instanceof Authorizable) {
			
				// get Execution
				ProxySudoExecution execution = cache.get((Authorizable) user);
				
				// execute command if 
				if(!execution.authorize(event.getMessage())) {
					// check if max is reached
					if(execution.isMaxReached()) {
						// send message to player
						player.sendMessage(new TextComponent(ERROR_MAX_REACHED));
						
						// delete execution
						cache.remove(execution.getUser());
					} else player.sendMessage(new TextComponent(ENTER_PASSWORD_AGAIN));
				} else executeCommand((User) execution.getUser());
			}
		}
	}
	
	private void executeCommand(User user) {
		// delete SudoExecution
		ProxySudoExecution execution = cache.remove(user);
		
		if(execution != null) {
			// report if user is not in sudo group
			if(user.isSudoer()) {		
				// execute command if it's a bungeecord command
				if(main.getProxy().getPluginManager().isExecutableCommand(execution.getCommand(),
						main.getProxy().getPlayer(user.getUniqueID()))) {
					// apply root container
					user.applyPermissionContainer(main.getUserManager().getRoot());
					
					// execute command
					main.getProxy().getPluginManager().dispatchCommand(execution.getPlayer(), execution.getCommand());
					
					// apply old container
					user.applyPermissionContainer(null);
				} else {
					// apply profile for player
					main.getNettyServer().sendPacket(execution.getPlayer().getServer(),
							new PacketRemoteInApplyContainer(user, main.getUserManager().getRoot().getPermissionContainer()));
					// send packet to remote server
//					main.getNettyServer().sendPacket(execution.getPlayer().getServer(), 
//							new PacketRemoteInExecuteSudoCommand(user, execution.getCommand()));
				}
			} else execution.getPlayer().sendMessage(new TextComponent(String.format(NO_SUDOER, user.getName())));
		}
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(cache);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SudoCommand other = (SudoCommand) obj;
		return Objects.equals(cache, other.cache);
	}
}
