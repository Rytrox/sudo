package de.timeout.sudo.groups;

import java.util.ArrayList;
import java.util.Collection;

import de.timeout.sudo.permissions.GroupContainer;
import de.timeout.sudo.users.User;

/**
 * Representation of the SudoGroup
 * @author Timeout
 *
 */
public class SudoGroup implements Group {
	
	protected final GroupContainer container;
	
	public SudoGroup() {
		container = new GroupContainer("sudo", new ArrayList<>());
	}

	@Override
	public String getName() {
		return container.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public GroupContainer getPermissionContainer() {
		return container;
	}

	@Override
	public boolean isMember(User element) {
		return false;
	}

	@Override
	public Collection<User> getMembers() {
		return container.getMembers();
	}

	@Override
	public boolean add(User element) {
		return container.add(element);
	}

	@Override
	public boolean remove(User element) {
		return container.remove(element);
	}

}
