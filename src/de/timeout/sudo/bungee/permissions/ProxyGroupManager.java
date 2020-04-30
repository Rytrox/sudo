package de.timeout.sudo.bungee.permissions;

import java.util.Optional;
import java.util.logging.Level;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.GroupManager;
import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.groups.exception.CircularInheritanceException;

import net.md_5.bungee.config.Configuration;

public class ProxyGroupManager extends GroupManager {

	private static final Sudo main = Sudo.getInstance();
		
	public ProxyGroupManager() {
		super(main.getConfig().getStringList("sudo.permissions"));
		// load groups.yml
		main.getGroupConfig().getKeys().forEach(this::loadGroup);
		// log data
		Sudo.log().log(Level.INFO, "&6groups.yml &asuccessfully loaded&7.");
	}
	
	@Override
	public Group getGroupByName(String name) {
		return !"sudo".equalsIgnoreCase(name) ? super.getGroupByName(name) : getSudoGroup();
	}

	@Override
	protected UserGroup loadGroup(String name) {
		// ban group name sudo
		if("sudo".equalsIgnoreCase(name)) {
			// log
			Sudo.log().log(Level.INFO, "&8[&6Sudo&8] &cSudo group cannot be overwritten.");
			return null;
		}
		
		// create new group or get null if the group cannot be found
		Configuration section = main.getGroupConfig().getSection(name);
		// check if group is already loaded
		Group group = getGroupByName(name);
		// load group if section is found and group is not loaded yet
		if((group == null || group instanceof UserGroup) && section != null) {
			group = new ProxyGroup(name, section);
			// add edge to graph
			groups.addNode((UserGroup) group);
			// load inheritances
			for(String extendedGroupName : section.getStringList("extends")) {
				// load supergroup
				Group superGroup = Optional.ofNullable(getGroupByName(extendedGroupName))
								.orElse(loadGroup(extendedGroupName));
				// only continue if group could be loaded
				if(superGroup != null && group instanceof UserGroup) {
					// bind inheritance
					try {
						bindInheritance((UserGroup) group, (UserGroup) superGroup);
					} catch (CircularInheritanceException e) {
						// log error
						Sudo.log().log(Level.SEVERE, String.format("&cInvalid group configuration for Group %s", name), e);
					}
				}
			}
			
			// return group
			return (UserGroup) group;
		}
		return null;
	}
}
