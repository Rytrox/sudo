package de.timeout.sudo.bungee.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.utils.PermissionTree;

import net.md_5.bungee.config.Configuration;

public class ProxyGroup implements Group {
	
	private final PermissionTree permissions = new PermissionTree();
	private final PermissionTree allPermissions = new PermissionTree();
	
	private final Set<User> members = new HashSet<>();
	private final Set<Group> inheritance = new HashSet<>();
	
	private String name;
	private String prefix;
	private String suffix;
	private boolean defaultGroup;
	
	public ProxyGroup(Configuration groupSection) {
		
	}
	
	/**
	 * Constructor for inheritances
	 */
	protected ProxyGroup(String name, String prefix, String suffix, boolean defaultGroup) {
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
		return user != null ? members.add(user) : false;
	}

	@Override
	public boolean kick(User user) {
		return user != null ? members.remove(user) : false;
	}

	@Override
	public boolean addPermission(String permission) {
		// if permission is not null or empty
		if(permission != null && !permission.isEmpty()) {
			// add permission to list
			return permissions.add(permission);
		}
		// return false
		return false;
	}

	@Override
	public boolean removePermission(String permission) {
		// if permission is not null
		if(permission != null) {
			// remove permission from collection
			return permissions.remove(permission);
		}
		// return false
		return false;
	}

	@Override
	public boolean hasPermission(String permission) {
		// validate
		Validate.notNull(permission, "permission cannot be null");
		// check in list and in super group
		boolean search = permissions.contains(permission);
		// run trough supergroups
		for (Group group : inheritance) {
			// break if permission is found
			if(!search) {
				// search in this group
				search = group.hasPermission(permission);
			} else break;
		}
		// return search
		return search;
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
}
