package de.timeout.sudo.bungee.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.groups.exception.CircularInheritanceException;
import de.timeout.sudo.permissions.GroupManager;
import de.timeout.sudo.permissions.UserConfigHandler;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.JsonConfiguration;
import net.md_5.bungee.event.EventHandler;

public class ProxyGroupManager extends GroupManager<UUID> implements Listener, UserConfigHandler<Configuration> {

	private static final Sudo main = Sudo.getInstance();
	private static final File userFolder = new File(main.getDataFolder(), "users");
		
	public ProxyGroupManager() {
		// create users folder
		if(userFolder.exists())
			try {
				Files.createDirectory(userFolder.toPath());
			} catch (IOException e) {
				Sudo.log().log(Level.WARNING, "&4Unable to create user folder", e);
			}
		// register Listener
		main.getProxy().getPluginManager().registerListener(main, this);
		// load groups.yml
		main.getGroupConfig().getKeys().forEach(this::loadGroup);
		// log data
		Sudo.log().log(Level.FINE, "&6groups.yml &asuccessfully loaded&7.");
	}
	
	/**
	 * Returns a list with all online users
	 * @author Timeout
	 * 
	 * @return a list containing a
	 */
	public List<User> getAllLoggedUsers() {
		// create new list
		List<User> onlineUsers = new ArrayList<>();
		// run through onlineplayers
		main.getProxy().getPlayers().forEach(player -> onlineUsers.add(profiles.get(player.getUniqueId())));
		// return list
		return onlineUsers;
	}
	
	/**
	 * Returns the User of the Player if the player is loaded. <br>
	 * Return null if the player is null
	 * @author Timeout
	 * 
	 * @param player the player you want to get. Can be null
	 * @return the user or null if the player is null
	 */
	public User getUser(ProxiedPlayer player) {
		return player != null ? profiles.get(player.getUniqueId()) : null;
	}
	
	@Override
	protected Group loadGroup(String name) {	
		// create new group or get null if the group cannot be found
		Configuration section = main.getGroupConfig().getSection(name);
		// check if group is already loaded
		Group group = getGroupByName(name);
		// load group if section is found and group is not loaded yet
		if(group != null && section != null) {
			group = new ProxyGroup(name, section);
			// add edge to graph
			groups.addNode(group);
			// load inheritances
			for(String extendedGroupName : section.getStringList("extends")) {
				// load supergroup
				Group superGroup = Optional.ofNullable(getGroupByName(extendedGroupName))
								.orElse(loadGroup(extendedGroupName));
				// only continue if group could be loaded
				if(superGroup != null) {
					// bind inheritance
					try {
						bindInheritance(group, superGroup);
					} catch (CircularInheritanceException e) {
						// log error
						Sudo.log().log(Level.SEVERE, String.format("&cInvalid group configuration for Group %s", name), e);
					}
				}
			}
		}
		// return group
		return group;
	}
	
	/**
	 * loads the user while player joins on the proxy
	 * @author Timeout
	 * 
	 * @param event the playerhandshakeevent
	 */
	@EventHandler
	public void onUserLoad(PlayerHandshakeEvent event) {
		try {
			// get User 
			User user = new ProxyUser(event.getConnection());
			// add user to cache
			profiles.put(event.getConnection().getUniqueId(), user);
			// send user to all subserver
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, 
					String.format("&cUnable to access %s.json", event.getConnection().getUniqueId().toString()), e);
		}
	}
	
	@EventHandler
	public void onPermissionCheck(PermissionCheckEvent event) {
		// get ProxiedPlayer
		CommandSender sender = event.getSender();
		// if sender is a player
		if(sender instanceof ProxiedPlayer) {
			// get Player
			ProxiedPlayer player = (ProxiedPlayer) sender;
			// override permissions
			event.setHasPermission(getUser(player).hasPermission(event.getPermission()));
		}
	}

	@Override
	public Configuration getUserConfiguration(UUID uuid) throws IOException {
		// get user-file
		File file = new File(userFolder, String.format("%s.json", uuid.toString()));
		// read file if file exists and is not empty
		if(file.exists() && file.length() > 0L) {
			return ConfigurationProvider.getProvider(JsonConfiguration.class).load(userFolder);
		} else return null;
	}
}
