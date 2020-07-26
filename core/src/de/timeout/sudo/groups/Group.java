package de.timeout.sudo.groups;

import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.timeout.sudo.permissions.GroupContainer;
import de.timeout.sudo.permissions.PermissionHolder;
import de.timeout.sudo.users.User;
import de.timeout.sudo.utils.Storable;

/**
 * Represents a group which handles permissions 
 * @author Timeout
 *
 */
public abstract class Group implements Comparable<Group>, PermissionHolder, Storable {
	
	protected final GroupContainer permissions;
		
	public Group(@NotNull String name, @NotNull Collection<String> permissions, @Nullable String prefix, @Nullable String suffix) {
		// Validate
		Validate.notEmpty(name, "Groupname can neither be null nor empty");
		Validate.notNull(permissions, "Permissions cannot be null");
		
		this.permissions = new GroupContainer(this, name, permissions, prefix, suffix);
	}

	@Override
	public boolean hasPermission(String permission) {
		// return true if this group has permission
		return permissions.hasPermission(permission);
	}

	@Override
	public Collection<String> getPermissions() {
		// return list
		return permissions.getPermissions();
	}
		
	@Override
	public String getName() {
		return permissions.getName();
	}
	
	public boolean addMember(User element) {		
		return permissions.add(element);
	}

	public boolean removeMember(User element) {
		return permissions.remove(element);
	}
	
	public boolean addPermission(String permission) {
		return this.permissions.addPermission(permission);
	}

	/**
	 * Remove a permission from this group. <br>
	 * Returns false if the permission is null
	 * 
	 * @param permission the permission to remove
	 * @param executor the executor of the method
	 * @return if it succeed
	 */
	@Override
	public boolean removePermission(String permission) {
		return this.permissions.removePermission(permission);
	}

	public Collection<User> getMembers() {
		return permissions.getMembers();
	}
	
	public boolean isMember(User user) {
		return permissions.isMember(user);
	}

	@Override
	public int compareTo(Group arg0) {
		// Validate
		Validate.notNull(arg0, "Other group cannot be null");
		
		return this.getName().compareTo(arg0.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(permissions);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		return Objects.equals(permissions, other.permissions);
	}

	@Override
	public GroupContainer getPermissionContainer() {
		return new GroupContainer(permissions);
	}
}
