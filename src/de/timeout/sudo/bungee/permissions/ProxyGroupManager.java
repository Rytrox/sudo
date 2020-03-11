package de.timeout.sudo.bungee.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.permissions.GroupManager;

import net.md_5.bungee.config.Configuration;

public class ProxyGroupManager implements GroupManager {

	private static final Sudo main = Sudo.getInstance();
	
	private final List<Group> loadedGroups = new ArrayList<>();

	public ProxyGroupManager() {
		// load groups.yml
		main.getGroupConfig().getKeys().forEach(this::getGroup);
		// log data
		Sudo.log().log(Level.FINE, "&6groups.yml &asuccessfully loaded&7.");
	}
	
	@Override
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
