package de.timeout.sudo.bungee.groups;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import net.md_5.bungee.config.Configuration;

public class ProxyGroup implements Group {
	
	private final SortedSet<String> permissions = new TreeSet<>();
	private final SortedSet<User> members = new TreeSet<>();
	
	private ProxyGroup superGroup;
	private String name;
	private String prefix;
	private String suffix;
	private boolean defaultGroup;
	
	public ProxyGroup(Configuration groupSection) {
		
	}
	
	/**
	 * Constructor for inheritances
	 */
	protected ProxyGroup(String name, ProxyGroup proxyGroup, String prefix, String suffix, boolean defaultGroup) {
		this.name = name;
		this.superGroup = proxyGroup;
		this.prefix = prefix;
		this.suffix = suffix;
		this.defaultGroup = defaultGroup;
	}

	public List<Group> getSuperGroups() {
		return null;
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
		return permissions.contains(permission) || (superGroup != null && superGroup.hasPermission(permission));
	}

	@Override
	public Set<String> getPermissions() {
		// create new SortedSet
		Set<String> copy = new TreeSet<>(permissions);
		// add all permissions of supergroup in this set
		if(superGroup != null) copy.addAll(superGroup.getPermissions());
		// return list
		return copy;
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
}
