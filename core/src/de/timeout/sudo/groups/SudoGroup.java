package de.timeout.sudo.groups;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

/**
 * Representation of the SudoGroup
 * @author Timeout
 *
 */
public abstract class SudoGroup extends Group {
	
	/**
	 * Creates a new SudoGroup
	 * @author Timeout
	 *
	 * @param permissions
	 */
	public SudoGroup(@Nonnull List<String> permissions) {
		super("sudo");
		// add all permissions
		permissions.forEach(this.permissions::add);
	}

	@Override
	public boolean hasPermission(String permission) {
		return permission != null && !permission.isEmpty() && permissions.contains(permission);
	}

	@Override
	public Set<String> getPermissions() {
		return permissions.toSet();
	}

	@Override
	public boolean add(User element, Root executor) {
		// Validate
		Validate.isTrue(element instanceof Sudoer, "Element needs to be a sudoer");
		
		return super.add(element, executor);
	}
}
