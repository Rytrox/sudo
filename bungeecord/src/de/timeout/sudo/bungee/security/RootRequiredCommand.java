package de.timeout.sudo.bungee.security;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * Abstract class for commands which requires root access
 * @author Timeout
 *
 */
public abstract class RootRequiredCommand extends Command implements TabExecutor {
	
	private static final String NO_ROOT = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cUnable to unlock resource. Are you root?");
	protected static final Sudo main = Sudo.getInstance();
	
	public RootRequiredCommand(@NotNull String name, @Nullable String permission, String... aliases) {
		super(name, permission, aliases);
	}

	public RootRequiredCommand(@NotNull String name) {
		super(name);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		// get SuperUser
		Root superuser = getSuperUser(sender);
		// execute if user is a superuser
		if(superuser != null && superuser.isRoot()) {
			execute(sender, superuser, args);
		} else sender.sendMessage(new TextComponent(NO_ROOT)); 
	}
	
	/**
	 * Method which will be executed if a root is executing this command
	 * @author Timeout
	 * 
	 * @param sender the executor of the command
	 * @param root the profile of the executor
	 * @param args the arguments of the command
	 */
	public abstract void execute(@NotNull CommandSender sender, @NotNull Root root, String[] args);
	
	@Nullable
	protected Root getSuperUser(CommandSender sender) {
		// return root console if sender is console
		if(!sender.equals(main.getProxy().getConsole())) {
			// get User of the player
			User user = main.getUserManager().getUser((ProxiedPlayer) sender);
			// return sudoer if the user is a sudoer and authorized. Else return null
			return user instanceof Sudoer && ((Sudoer) user).isAuthorized() ? (Sudoer) user : null;
		} return main.getUserManager().getConsoleUser();
	}
}
