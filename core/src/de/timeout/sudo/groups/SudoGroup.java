package de.timeout.sudo.groups;

import java.util.ArrayList;

import de.timeout.sudo.container.GroupContainer;

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
}
