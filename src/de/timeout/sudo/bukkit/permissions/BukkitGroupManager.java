package de.timeout.sudo.bukkit.permissions;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import com.google.gson.JsonObject;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.GroupManager;
import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.groups.exception.CircularInheritanceException;

public class BukkitGroupManager extends GroupManager {
	
	private static final Sudo main = Sudo.getInstance();
			
	public BukkitGroupManager(boolean bukkit) {
		super(main.bungeecordEnabled() ? new ArrayList<>() : main.getConfig().getStringList("sudo.permissions"));
		// register PluginMessageChannel if bungeecord is enabled
		if(!bukkit) {
			// info server for using Bungeecord
			Sudo.log().log(Level.INFO, "&2Bungeecord &7in &6spigot.yml &aenabled&7. Requesting data from &2Bungeecord&7...");
		} else loadGroupsFromFile();
	}
	
	/**
	 * Converts the Groups-Array from BungeeCord into BukkitGroups
	 * @author Timeout
	 * 
	 * @param data the groups array from bungeecords message
	 */
	public void loadGroupFromBungeecord(@Nonnull JsonObject data) {
		// create group
		UserGroup group = new BukkitGroup(data);
		// add group to cache
		groups.addNode(group);
	}
	
	/**
	 * Loads the inheritances of one group. <br>
	 * Will be executed by receiving {@link PacketRemoteInGroupInheritances}
	 * @author Timeout
	 * 
	 * @param name the name of the group. Cannot be null
	 * @param inheritances the name of the extended group. Cannot be null
	 * @throws IllegalArgumentException if the group with the name cannot be found or any argument is null
	 */
	public void loadInheritance(@Nonnull String name, @Nonnull String inheritance) {
		// Validate
		Validate.notNull(name, "Group name cannot be null");
		Validate.notNull(inheritance, "Inheritance cannot be null");
		// get Group for name
		Group group = getGroupByName(name);
		// throw exception if the name cannot be found
		if(group != null) {
			// get group
			Group extend = getGroupByName(inheritance);
			// log if group cannot be found
			if(extend != null) {
				// bind inheritance
				try {
					bindInheritance((UserGroup) group, (UserGroup) extend);
				} catch (CircularInheritanceException e) {
					Sudo.log().log(Level.WARNING, String.format("&cUnable to bind inheritances from group %s to group %s. A circular inheritance is detected!", name, inheritance), e);
				}
			} else Sudo.log().log(Level.WARNING, String.format("&cTry to load group %s but this group does not exist", inheritance));
		} else throw new IllegalArgumentException("Group cannot not be found");
	}
	
	private void loadGroupsFromFile() {
		// log load from bukkit files
		Sudo.log().log(Level.INFO, "&9Bukkit-Mode &aenabled. Load groups from File");
		// load all groups
		main.getGroupConfig().getKeys(false).forEach(this::loadGroup);
	}
	
	@Override
	protected UserGroup loadGroup(String name) {
		// ban group name sudo
		if("sudo".equalsIgnoreCase(name)) {
			// log
			Sudo.log().log(Level.INFO, "&8[&6Sudo&8] &cSudo group cannot be overwritten.");
			return null;
		}
		
		// get ConfigurationSection
		ConfigurationSection section = main.getGroupConfig().getConfigurationSection(name);
		// check if group is not loaded
		Group group = getGroupByName(name);
		// check if section is valid and group is not already loaded
		if((group == null || group instanceof UserGroup) && section != null) {
			// load Group
			group = new BukkitGroup(section);
			// add edge in graph
			groups.addNode((UserGroup) group);
			// load inheritances
			for(String extendName : section.getStringList("extends")) {
				// try to load group
				Group extend = Optional.ofNullable(getGroupByName(extendName)).orElse(loadGroup(extendName));
				// only continue if usergroup could be loaded
				if(extend instanceof UserGroup) {
					// bind inheritance
					try {
						bindInheritance((UserGroup) group, (UserGroup) extend);
					} catch (CircularInheritanceException e) {
						Sudo.log().log(Level.WARNING, String.format("&cUnable to load Group %s. Circular Inheritance detected", name), e);
					}
				}
			}
		}
		// return group
		return (UserGroup) group;
	}
}
