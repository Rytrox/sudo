package de.timeout.sudo.bungee.permissions;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.Group;

import net.md_5.bungee.config.Configuration;

public class GroupManager {

	private static final Sudo main = Sudo.getInstance();
	
	private final List<Group> loadedGroups = new ArrayList<>();
	
	public GroupManager() {
		// load groups.yml
		main.getGroupConfig().getKeys().forEach(this::getGroup);
	}
	
	/**
	 * Gets a group by its name
	 * @author Timeout
	 * 
	 * @param name the name of the group
	 * @return the group if the group is in groups.yml or loaded. Else null
	 */
	@Nullable
	public Group getGroup(String name) {
		// search in loadedGroups
		for(Group group : loadedGroups) {
			// check if groupname is equal
			if(group.getName().equals(name)) return group;
		}
		
		// create new group or get null if the group cannot be found
		Configuration section = main.getGroupConfig().getSection(name);
		// return null if the group does not exist
		if(section != null) { 
			Group group =  new ProxyGroup(name, section);
			// add group to loaded group
			loadedGroups.add(group);
			// return group
			return group;
		} else return null;
	}
}
