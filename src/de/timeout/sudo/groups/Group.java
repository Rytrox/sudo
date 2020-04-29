package de.timeout.sudo.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import com.google.gson.JsonObject;

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
	
	public Group(@Nonnull String name) {
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
	@Nonnull
	public String getName() {
		return name;
	}
	
	@Nonnull
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
		return Objects.equals(members, other.members) && Objects.equals(name, other.name)
				&& Objects.equals(permissions, other.permissions);
	}

	/**
	 * Compiles the group into Json-Objects
	 * @author Timeout
	 * 
	 * @return the group as JsonObject
	 */
	@Nonnull
	public abstract JsonObject toJson();
}
