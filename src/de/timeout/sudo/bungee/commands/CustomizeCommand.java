package de.timeout.sudo.bungee.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.ArrayUtils;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.bungee.permissions.ProxyUser;
import de.timeout.sudo.utils.Customizable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.event.EventHandler;


/**
 * Command for customization: 
 * 
 * ./customize prefix suffix -> customize yourself with a custom prefix and suffix
 * ./customize user <user> prefix suffix -> customize another user with a custom prefix
 * ./customize user <user> group <group> -> customize another user with the group you decide
 * 
 * @author timeout
 *
 */
public class CustomizeCommand extends Command implements TabExecutor {

	private static final String USER = "user";
	
	private static final Sudo main = Sudo.getInstance();
	
	private CustomizeHelper helper;
	
	public CustomizeCommand() {
		super("customize");
		
		// register listener
		helper = new CustomizeHelper();
		main.getProxy().getPluginManager().registerListener(main, helper);
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		// TODO: CHECK ARGS AUFBAU -> Letztes Argument, ist es null oder doch was anderes?
		
		
		// get a list of all unused params
		List<String> unused = getUnusedParams(args);
		
		// get a playerlist if the sender uses the user param
		if(args.length > 1 && !unused.contains(USER) && args[args.length -2].equalsIgnoreCase(USER)) {
			// get last word
			String lastIn = args[args.length - 1];
			
			// return a list containing all users filtered by last name
			return main.getProxy().getPlayers()
					.stream()
					.map(ProxiedPlayer::getName)
					.filter(name -> lastIn != null && name.toLowerCase(Locale.ENGLISH).contains(lastIn.toLowerCase()))
					.collect(Collectors.toList());
		} else {
			// remove tabcomplete for user if sender is not permited to change others 
			unused.removeIf(arg -> sender.hasPermission("sudo.command.customize.prefix.other") || sender.hasPermission("sudo.command.customize.suffix.other"));
		}
		return unused;
	}
	
	/**
	 * Filters all used params and returns a list of all unused params 
	 * @author timeout
	 * 
	 * @param args the arguments of the command
	 * @return a list containing all unused parameters
	 */
	@Nonnull
	private List<String> getUnusedParams(String[] args) {
		// return a list 
		return Arrays.asList(USER, "prefix", "suffix", "group")
				.stream()
				.filter(arg -> Arrays.stream(args).noneMatch(arg::equalsIgnoreCase))
				.collect(Collectors.toList());
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		// send help if the command is empty
		if(args.length > 0) {
			// get Customizable
			Customizable user = getCustomizeable(sender, args);
			
			// validate user (is not null if console changes a user or player changes a user or player changes himself)
			if(user != null) {
				boolean other = true;
				
				// check if user modifies its own profile
				if(sender instanceof ProxiedPlayer) other = !user.equals((Customizable) main.getUserManager().getUser((ProxiedPlayer) sender));
				

				if(ArrayUtils.contains(args, "prefix")) {
					// check for permissions
					if(sender.hasPermission(String.format("sudo.command.customize.prefix.%s", other ? "other" : "self"))) {
						// request customization requests
						helper.requestPrefixChange(user);
					}
				}
				
				if(ArrayUtils.contains(args, "suffix")) {
					// check for permissions
					if(sender.hasPermission(String.format("sudo.command.customize.suffix.%s", other ? "other" : "self"))) {
						// request customization request
						helper.requestSuffixChange(user);
					}
				}
			}
		}
	}
	
	@Nullable
	private Customizable getCustomizeable(CommandSender sender, String[] args) {
		// check if args are using param user
		int index = ArrayUtils.indexOf(args, USER);
		if(index != -1) {
			// get profile of other player
			return (Customizable) main.getUserManager().getUser(args.length > index ? main.getProxy().getPlayer(args[index + 1]) : null);
		}
		
		// return sender's user if sender is a player. Otherwise null
		return sender instanceof ProxiedPlayer ? (Customizable) main.getUserManager().getUser((ProxiedPlayer) sender) : null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(helper);
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
		CustomizeCommand other = (CustomizeCommand) obj;
		return Objects.equals(helper, other.helper);
	}

	private static class CustomizeHelper implements Listener {
		
		private final Set<Customizable> prefixChange = new HashSet<>();
		private final Set<Customizable> suffixChange = new HashSet<>();
		
		@EventHandler
		public void onChat(ChatEvent event) {
			// check if sender is a player
			if(event.getSender() instanceof ProxiedPlayer) {
				// get User
				ProxyUser user = (ProxyUser) main.getUserManager().getUser((ProxiedPlayer) event.getSender());
				
				// check if user has required prefix change
				if(prefixChange.contains(user)) {
					// cancel event
					event.setCancelled(true);
					
					// check if message is valid
					if(event.getMessage().length() > 16) {
						// apply prefix
						user.setPrefix(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
						// remove from prefix change
						prefixChange.remove(user);
					}
				} else if(suffixChange.contains(user)) {
					// cancel event
					event.setCancelled(true);
					
					// check for length of message
					if(event.getMessage().length() > 16) {
						// apply suffix
						user.setSuffix(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
						// remove from suffix change
						suffixChange.remove(user);
					}
				}
			}
		}
		
		/**
		 * Requests a prefix change for the user
		 * @author timeout
		 * 
		 * @param customizable the user itself
		 * @return true if the request succeed, false if the user has currently an open request
		 */
		public boolean requestPrefixChange(@Nonnull Customizable customizable) {
			return prefixChange.add(customizable);
		}
		
		/**
		 * Requests a suffix change for the user
		 * @author timeout
		 * 
		 * @param customizable the user itself
		 * @return true if the request succeed, false if the user has currently an open request
		 */
		public boolean requestSuffixChange(@Nonnull Customizable customizable) {
			return suffixChange.add(customizable);
		}
	}
	
}
