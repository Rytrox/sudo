package de.timeout.sudo.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import de.timeout.sudo.groups.Collectable;
import de.timeout.sudo.groups.PermissibleBase;
import de.timeout.sudo.utils.PermissionTree;

/**
 * Representation of the SudoGroup
 * @author Timeout
 *
 */
public final class SudoGroup implements PermissibleBase, Collectable<Sudoer> {
	
	private final Set<Sudoer> sudoers = new HashSet<>();
	private final PermissionTree permissions = new PermissionTree();
	
	/**
	 * Creates a new SudoGroup
	 * @author Timeout
	 *
	 * @param permissions
	 */
	public SudoGroup(@Nonnull List<String> permissions) {
		// add all permissions
		permissions.forEach(this.permissions::add);
	}

	@Override
	public boolean join(Sudoer element) {
		// return false if the user is null or the user is already added
		return element != null && sudoers.add(element);
	}

	@Override
	public boolean kick(Sudoer element) {
		return element != null && sudoers.remove(element);
	}

	@Override
	public boolean isMember(Sudoer element) {
		// return true if element is not null and is in group
		return element != null && sudoers.contains(element);
	}

	@Override
	public boolean addPermission(String permission) {
		// add permission if permission is not null
		return permission != null && !permission.isEmpty() && permissions.add(permission);
	}

	@Override
	public boolean removePermission(String permission) {
		// remove permission if permission is not null
		return permission != null && permissions.remove(permission);
	}

	@Override
	public boolean hasPermission(String permission) {
		return permission != null && !permission.isEmpty() && permissions.contains(permission);
	}

	@Override
	public Set<String> getPermissions() {
		return permissions.toSet();
	}

	/**
	 * Returns a collection of all sudoers. Cannot be null
	 * @author Timeout
	 * 
	 * @return a collection of all sudoers. Cannot be null
	 */
	@Nonnull
	public Collection<Sudoer> getSudoers() {
		return new ArrayList<>(sudoers);
	}
}
