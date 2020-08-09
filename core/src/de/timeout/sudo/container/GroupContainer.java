package de.timeout.sudo.container;

import java.util.Collection;

import de.timeout.sudo.users.User;

public class GroupContainer extends CollectableContainer<User> {

	public GroupContainer(String name, Collection<User> members) {
		super(name, members);
	}

}
