package de.timeout.sudo.bukkit.security;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Abstract class for commands which requires root access
 * @author Timeout
 *
 */
public abstract class RootRequiredCommand extends Command {
	
	private static final String NO_ROOT = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cUnable to unlock resource. Are you root?");
	protected static final Sudo main = Sudo.getInstance();
	
	private final Root console = new RootConsole();

	public RootRequiredCommand(@Nonnull String name, @Nullable String permission, String... aliases) {
		super(name, permission, aliases);
	}

	public RootRequiredCommand(@Nonnull String name) {
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
	public abstract void execute(@Nonnull CommandSender sender, @Nonnull Root root, String[] args);
	
	@Nullable
	protected Root getSuperUser(CommandSender sender) {
		// return root console if sender is console
		if(!sender.equals(main.getProxy().getConsole())) {
			// get User of the player
			User user = main.getGroupManager().getUser((ProxiedPlayer) sender);
			// return sudoer if the user is a sudoer and authorized. Else return null
			return user instanceof Sudoer && ((Sudoer) user).isAuthorized() ? (Sudoer) user : null;
		} return console;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(console);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RootRequiredCommand other = (RootRequiredCommand) obj;
		return Objects.equals(console, other.console);
	}
}
