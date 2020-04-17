package de.timeout.sudo.bukkit.permissions;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import com.google.gson.JsonObject;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.bukkit.listener.VanillaPermissionOverrider;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.groups.exception.CircularInheritanceException;
import de.timeout.sudo.netty.packets.PacketRemoteInGroupInheritances;
import de.timeout.sudo.permissions.GroupManager;
import de.timeout.sudo.security.Sudoer;

public class BukkitGroupManager extends GroupManager<ServerOperator> {
	
	private static final Sudo main = Sudo.getInstance();
			
	public BukkitGroupManager(boolean bukkit) {
		// register PluginMessageChannel if bungeecord is enabled
		if(!bukkit) {
			// info server for using Bungeecord
			Sudo.log().log(Level.INFO, "&2Bungeecord &7in &6spigot.yml &aenabled&7. Requesting data from &2Bungeecord&7...");
		} else loadGroupsFromFile();
	}
	
	/**
	 * Returns the User of the operator. Cannot be null
	 * @author Timeout
	 * 
	 * @param operator the operator you want to get
	 * @return the User of the Player or null
	 */
	@Nonnull
	public User getUserFromOperator(ServerOperator operator) {
		// returns from cache if user is loaded. else load him before
		return Optional.ofNullable(profiles.get(operator)).orElse(new BukkitUser(operator));
	}
	
	/**
	 * Converts the Groups-Array from BungeeCord into BukkitGroups
	 * @author Timeout
	 * 
	 * @param data the groups array from bungeecords message
	 */
	public void loadGroupFromBungeecord(@Nonnull JsonObject data) {
		// create group
		Group group = new BukkitGroup(data);
		// add group to cache
		groups.addNode(group);
	}
	
	/**
	 * Loads the inheritances of one group. <br>
	 * Will be executed by receiving {@link PacketRemoteInGroupInheritances}
	 * @author Timeout
	 * 
	 * @param name the name of the group. Cannot be null
	 * @param inheritances a list of inheritances of the group. Cannot be null
	 * @throws IllegalArgumentException if the group with the name cannot be found or any argument is null
	 */
	public void loadInheritances(@Nonnull String name, @Nonnull Set<String> inheritances) {
		// Validate
		Validate.notNull(name, "Group name cannot be null");
		Validate.notNull(inheritances, "Inheritances cannot be null");
		// get Group for name
		Group group = getGroupByName(name);
		// throw exception if the name cannot be found
		if(group != null) {
			// load inheritances
			inheritances.forEach(extendName -> {
				// get group
				Group extend = getGroupByName(extendName);
				// log if group cannot be found
				if(extend != null) {
					// bind inheritance
					try {
						bindInheritance(group, extend);
					} catch (CircularInheritanceException e) {
						Sudo.log().log(Level.WARNING, String.format("&cUnable to bind inheritances from group %s to group %s. A circular inheritance is detected!", name, extendName), e);
					}
				} else Sudo.log().log(Level.WARNING, String.format("&cTry to load group %s but this group does not exist", extendName));
			});
		} else throw new IllegalArgumentException("Group cannot not be found");
	}
	
	/**
	 * Converts the users array from Bungeecord into BukkitUsers
	 * @author Timeout
	 * 
	 * @param data the users array from bungeecord message
	 */
	public void loadUserFromBungeecord(@Nonnull JsonObject data) {
		// validate
		Validate.notNull(data, "Json-Data cannot be null");
		// create user
		BukkitUser user = new BukkitUser(data);
		// get OfflinePlayer
		OfflinePlayer op = Bukkit.getOfflinePlayer(user.getUniqueID());
		// cache user
		profiles.put(op, user);
		// overrides profile if user is already online
		if(op.isOnline()) VanillaPermissionOverrider.overridePermissionSystem(op.getPlayer(), user);
	}
	
	/**
	 * Loads a new user from its configuration-file.
	 * Throws an exception if the server runs in bungeecord-mode
	 * 
	 * @author Timeout
	 * 
	 * @param uuid the uuid of the user you want to load
	 * @throws IllegalArgumentException if the uuid is null
	 * @throws IllegalStateException if the server runs in bungeecord-mode
	 */
	public void loadUserFromFile(@Nonnull UUID uuid) {
		// Validate
		Validate.notNull(uuid, "UUID cannot be null");
		// throw illegal state exception if bukkit mode is disabled
		if(!main.bungeecordEnabled()) {
			// get OfflinePlayer
			OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(uuid);
			// get User and load from file
			User user = Optional.ofNullable(profiles.get(player)).orElse(new BukkitUser(player));
			// apply user to online player
			Player p = player.getPlayer();
			if(p != null) VanillaPermissionOverrider.overridePermissionSystem(p, (BukkitUser) user);
			// cache in database
			profiles.putIfAbsent(player, user);
		} else throw new IllegalStateException("Users cannot be loaded from files while bungeecord is enabled!");
	}
	
	/**
	 * Unloads a user and saves its data in a file
	 * @author Timeout
	 * 
	 * @param player the player you want to unload
	 * @throws IllegalStateException if the server runs in bungeecord mode
	 * @throws IllegalArgumentException if the player is null or online
	 */
	public void unloadUserToFile(@Nonnull OfflinePlayer player) {
		// Validate
		Validate.notNull(player, "Player cannot be null");
		// throws illegal state exception if bukkit mode is disabled
		if(!main.bungeecordEnabled() && !player.isOnline()) {
			// unload user from profiles
			BukkitUser user = (BukkitUser) profiles.remove(player);
			// save in file
			try {
				user.save();
			} catch (IOException e) {
				Sudo.log().log(Level.WARNING, String.format("&cUnable to write data file of player %s", player.getPlayer()), e);
			}
		} else throw new IllegalStateException("Users cannot be saved in files while bungeecord is enabled or player is online.");
	}
	
	private void loadGroupsFromFile() {
		// log load from bukkit files
		Sudo.log().log(Level.INFO, "&9Bukkit-Mode &aenabled. Load groups from File");
		// load all groups
		main.getGroupConfig().getKeys(false).forEach(this::loadGroup);
	}
	
	@Override
	protected Group loadGroup(String name) {
		// get ConfigurationSection
		ConfigurationSection section = main.getGroupConfig().getConfigurationSection(name);
		// check if group is not loaded
		Group group = getGroupByName(name);
		// check if section is valid and group is not already loaded
		if(group == null && section != null) {
			// load Group
			group = new BukkitGroup(section);
			// add edge in graph
			groups.addNode(group);
			// load inheritances
			for(String extendName : section.getStringList("extends")) {
				// try to load group
				Group extend = Optional.ofNullable(getGroupByName(extendName)).orElse(loadGroup(extendName));
				// only continue if group could be loaded
				if(extend != null) {
					// bind inheritance
					try {
						bindInheritance(group, extend);
					} catch (CircularInheritanceException e) {
						Sudo.log().log(Level.WARNING, String.format("&cUnable to load Group %s. Circular Inheritance detected", name), e);
					}
				}
			}
		}
		// return group
		return group;
	}

	@Override
	public void upgradeUser(Sudoer superUser, Sudoer executor) {
		Validate.notNull(superUser);
		Validate.notNull(object);
		// if executor is authorized
		if(executor.isAuthorized()) {
			// 
		}
	}
}
