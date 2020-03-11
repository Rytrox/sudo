package de.timeout.sudo.permissions;

import javax.annotation.Nullable;

import de.timeout.sudo.groups.Group;

@FunctionalInterface
public interface GroupManager {
	
	/**
	 * Gets a group by its name
	 * @author Timeout
	 * 
	 * @param name the name of the group
	 * @return the group if the group is in groups.yml or loaded. Else null
	 */
	@Nullable
	public Group getGroup(String name);

}
