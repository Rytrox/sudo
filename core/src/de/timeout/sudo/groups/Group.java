package de.timeout.sudo.groups;

import de.timeout.sudo.permissions.ContainerHolder;
import de.timeout.sudo.users.User;
import de.timeout.sudo.utils.Collectable;
/**
 * Represents a group which handles permissions 
 * @author Timeout
 *
 */
public interface Group extends ContainerHolder, Collectable<User> {
	
}
