package de.timeout.sudo.bungee.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.ArrayUtils;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.utils.Customizable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
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
	
	private static final String NO_PERMISSION = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDu hast nicht die benötigte Berechtigung, diesen Befehl auszuführen!");
	private static final String NOT_ONLINE = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDer angegebene Spieler ist nicht online"); 
	private static final String NO_PLAYER = ChatColor.translateAlternateColorCodes('&', "&8[&cSudo&8] &cNur ein Spieler kann diesen Befehl ausführen!");
	private static final String ALREADY_REQUEST = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDu bist bereits in einem Registrationsprozess!");
	private static final String REQUEST_PREFIX = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &aBitte gebe jetzt einen neuen Prefix ein. Farbcodes werden mit '&' notiert.");
	private static final String REQUEST_SUFFIX = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &aBitte gebe jetzt einen neuen Suffix ein. Farbcodes werden mit '&' notiert.");
	private static final String REQUEST_FINISHED = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &aRegistrationsprozess vollendet!");
	
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
		// only players can perform this command
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) sender; 
			// send help if the command is empty
			if(args.length > 0) {
				// get Customizable
				Customizable user = getCustomizeable(player, args);
				
				// validate user (is not null if user is not online)
				if(user != null) {
					// check if user is not in process
					if(!helper.isInRegistrationProcess(player)) {
						boolean other = !user.equals((Customizable) main.getUserManager().getUser((ProxiedPlayer) sender));
						boolean prefix = ArrayUtils.contains(args, "prefix");				
		
						if(prefix) {
							// check for permissions
							if(sender.hasPermission(String.format("sudo.command.customize.prefix.%s", other ? "other" : "self"))) {
								// request customization requests
								helper.requestPrefixChange(player, user);
								sender.sendMessage(new TextComponent(REQUEST_PREFIX));
							} else sender.sendMessage(new TextComponent(NO_PERMISSION));
						}
						
						if(ArrayUtils.contains(args, "suffix")) {
							// check for permissions
							if(sender.hasPermission(String.format("sudo.command.customize.suffix.%s", other ? "other" : "self"))) {
								// request customization request
								helper.requestSuffixChange(player, user);
								// send message if message is not already sent
								if(!prefix) sender.sendMessage(new TextComponent(REQUEST_SUFFIX));
							} else sender.sendMessage(new TextComponent(NO_PERMISSION));
						}
					} else sender.sendMessage(new TextComponent(ALREADY_REQUEST));
				} else sender.sendMessage(new TextComponent(NOT_ONLINE));
			} else sendHelp(player);
		} else sender.sendMessage(new TextComponent(NO_PLAYER));
	}
	
	@Nullable
	private Customizable getCustomizeable(ProxiedPlayer sender, String[] args) {
		// check if args are using param user
		int index = ArrayUtils.indexOf(args, USER);
		if(index != -1) {
			// get profile of other player
			return (Customizable) main.getUserManager().getUser(args.length > index ? main.getProxy().getPlayer(args[index + 1]) : null);
		}
		
		return (Customizable) main.getUserManager().getUser(sender);
	}
	
	private void sendHelp(@Nonnull CommandSender sender) {
		// TODO: Hilfe schreiben
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
		
		private final Map<ProxiedPlayer, Customizable> cache = new HashMap<>();
		
		private final Set<ProxiedPlayer> prefixChange = new HashSet<>();
		private final Set<ProxiedPlayer> suffixChange = new HashSet<>();
		
		@EventHandler
		public void onChat(ChatEvent event) {
			// check if sender is a player
			if(event.getSender() instanceof ProxiedPlayer) {
				// get Sender
				ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
				// get User
				Customizable user = cache.get(sender);
						
				// only continue if player is in cache
				if(user != null) {		
					// cancel event
					event.setCancelled(true);
					
					// check if message is valid
					if(event.getMessage().length() <= 16) {
						String fix = ChatColor.translateAlternateColorCodes('&', event.getMessage());
						
						// check if sender has required prefix change
						if(prefixChange.contains(sender)) {					
							// apply prefix
							user.setPrefix(fix);
							// remove from prefix change
							prefixChange.remove(sender);
							
							// finish registration
							if(!suffixChange.contains(sender)) {
								// finish registration
								finishRegistrationProcess(sender);
							}
						} else if(suffixChange.contains(sender)) {						
							// apply suffix
							user.setSuffix(fix);
							// remove from suffix change
							suffixChange.remove(sender);
							
							// finish registration process
							finishRegistrationProcess(sender);
						}
					}
				}
			}
		}
		
		/**
		 * Checks if the player is currently in registration process
		 * @author timeout
		 * 
		 * @param player the player you want to check
		 * @return true if he is in RegistrationProcess, false otherwise
		 */
		public boolean isInRegistrationProcess(@Nonnull ProxiedPlayer player) {
			return cache.containsKey(player);
		}
		
		/**
		 * Requests a prefix change for the user
		 * @author timeout
		 * 
		 * @param customizable the user itself
		 * @return true if the request succeed, false if the user has currently an open request
		 */
		public boolean requestPrefixChange(@Nonnull ProxiedPlayer sender, @Nonnull Customizable customizable) {
			// return false if it is in registration process
			if(!isInRegistrationProcess(sender)) {
				// add to cache
				cache.put(sender, customizable);
				return prefixChange.add(sender);
			}
			
			return false;
		}
		
		/**
		 * Finishes the registration process
		 * @author timeout
		 * 
		 * @param player the player who finish
		 */
		public void finishRegistrationProcess(@Nonnull ProxiedPlayer player) {
			cache.remove(player);
			prefixChange.remove(player);
			suffixChange.remove(player);
			
			player.sendMessage(new TextComponent(REQUEST_FINISHED));
		}
		
		/**
		 * Requests a suffix change for the user
		 * @author timeout
		 * 
		 * @param customizable the user itself
		 * @return true if the request succeed, false if the user has currently an open request
		 */
		public boolean requestSuffixChange(@Nonnull ProxiedPlayer sender, @Nonnull Customizable customizable) {
			// return false if sender is in registration process
			if(!isInRegistrationProcess(sender)) {
				cache.put(sender, customizable);
				return suffixChange.add(sender);
			}
			
			return false;
		}
	}
	
}
