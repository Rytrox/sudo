package de.timeout.sudo.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.User;
import de.timeout.sudo.utils.Collectable;
import de.timeout.sudo.utils.PermissibleBase;
import de.timeout.sudo.utils.PermissionTree;

/**
 * Represents a group which handles permissions 
 * @author Timeout
 *
 */
public abstract class Group implements Comparable<Group>, PermissibleBase, Collectable<User> {
	
	protected final Set<User> members = new HashSet<>();

	protected final PermissionTree permissions = new PermissionTree();
	
	protected String name;
	
	public Group(@NotNull String name) {
		// Validate
		Validate.notEmpty(name, "Groupname can neither be null nor empty");
		this.name = name;
	}

	@Override
	public boolean hasPermission(String permission) {
		// return true if this group has permission
		return permissions.contains(permission);
	}

	@Override
	public Set<String> getPermissions() {
		// return list
		return permissions.toSet();
	}
		
	/**
	 * Returns the name of this instance
	 * @return the name of this instance
	 */
	@NotNull
	public String getName() {
		return name;
	}
	
	@Override
	public boolean add(User element, Root executor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(User element, Root executor) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean addPermission(String permission, Root executor) {
		Validate.notEmpty(permission, "Permission can neither be null nor empty");
		Validate.notNull(executor, "Executor cannot be null");
		Validate.isTrue(executor.isRoot(), "Executor needs to be root!");
		
		return this.permissions.add(permission);
	}

	/**
	 * Remove a permission from this group. <br>
	 * Returns false if the permission is null
	 * 
	 * @param permission the permission to remove
	 * @param executor the executor of the method
	 * @return if it succeed
	 */
	public boolean removePermission(String permission, Root executor) {
		Validate.notEmpty(permission, "Permission can neither be null nor empty");
		Validate.notNull(executor, "Executor cannot be null");
		Validate.isTrue(executor.isRoot(), "Executor needs to be root!");
		
		return this.permissions.remove(permission);
	}

	@Override
	public Collection<User> getMembers() {
		return new ArrayList<>(members);
	}
	
	@Override
	public boolean isMember(User user) {
		return members.contains(user);
	}

	@Override
	public int compareTo(Group arg0) {
		// Validate
		Validate.notNull(arg0, "Other group cannot be null");
		
		return this.name.compareTo(arg0.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(members, name, permissions);
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
		return Objects.equals(name, other.name);
	}
}
