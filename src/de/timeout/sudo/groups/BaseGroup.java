package de.timeout.sudo.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import de.timeout.sudo.utils.PermissionTree;

public class BaseGroup implements Group, Comparable<Group> {
	
	protected final PermissionTree permissions = new PermissionTree();
	protected final PermissionTree allPermissions = new PermissionTree();
	
	protected final Set<User> members = new HashSet<>();
	protected final Set<Group> inheritance = new HashSet<>();
	
	protected String name;
	protected String prefix;
	protected String suffix;
	protected boolean defaultGroup;
	
	/**
	 * Constructor for inheritances
	 */
	protected BaseGroup(String name, String prefix, String suffix, boolean defaultGroup) {
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
		this.defaultGroup = defaultGroup;
	}
	
	@Override
	public boolean isMember(User user) {
		return members.contains(user);
	}

	@Override
	public boolean join(User user) {
		return members.add(user);
	}

	@Override
	public boolean kick(User user) {
		return members.remove(user);
	}

	@Override
	public boolean addPermission(String permission) {
		// if permission is not null or empty
		if(permission != null && !permission.isEmpty()) {
			// add permission to list
			return permissions.add(permission) && allPermissions.add(permission);
		}
		// return false
		return false;
	}

	@Override
	public boolean removePermission(String permission) {
		// if permission is not null
		if(permission != null) {
			// remove permission from collection
			return permissions.remove(permission) && allPermissions.remove(permission);
		}
		// return false
		return false;
	}

	@Override
	public boolean hasPermission(String permission) {
		// return search
		return allPermissions.contains(permission);
	}

	@Override
	public Set<String> getPermissions() {
		// return list
		return permissions.toSet();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getSuffix() {
		return suffix;
	}

	@Override
	public boolean isDefault() {
		return defaultGroup;
	}

	@Override
	public Set<String> getAllPermissions() {
		return allPermissions.toSet();
	}

	@Override
	public Collection<Group> getExtendedGroups() {
		return new ArrayList<>(inheritance);
	}

	@Override
	public int compareTo(Group o) {
		return this.name.compareTo(o.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(allPermissions, defaultGroup, inheritance, members, name, permissions, prefix, suffix);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseGroup other = (BaseGroup) obj;
		return Objects.equals(name, other.name) && Objects.equals(permissions, other.permissions);
	}

}
