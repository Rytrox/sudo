package de.timeout.sudo.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.timeout.sudo.utils.PermissionTree;

public class BaseGroup implements Group, Comparable<Group>, Customizable {
	
	protected static Group defaultGroup;
	
	protected final PermissionTree permissions = new PermissionTree();
	protected final Set<User> members = new HashSet<>();
	protected final Set<Group> groups = new HashSet<>();
	
	protected String name;
	protected String prefix;
	protected String suffix;
	protected boolean isDefault;
	
	/**
	 * Constructor for inheritances
	 */
	protected BaseGroup(String name, String prefix, String suffix, boolean isDefault) {
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
		this.isDefault = isDefault;
			
		// select first loaded group as default group
		if(defaultGroup != null) {
			// select new default group if this group is default
			if(isDefault) defaultGroup = this;
		} else defaultGroup = this;
	}
	
	/**
	 * Returns the default group 
	 * @author Timeout
	 * 
	 * @return
	 */
	@Nonnull
	public static Group getDefaultGroup() {
		return defaultGroup;
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
		// return true if this group has permission
		if(!permissions.contains(permission)) {
			// search in extended groups
			for(Group extended : getExtendedGroups()) {
				// search for permission, return true if found
				if(extended.hasPermission(permission)) return true;
			}
			// not found. return false
			return false;
		} else return true;
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
		return isDefault;
	}

	@Override
	public Collection<Group> getExtendedGroups() {
		return new ArrayList<>(groups);
	}

	@Override
	public int compareTo(Group o) {
		return this.name.compareTo(o.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(members, name, permissions, prefix, suffix);
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

	@Override
	public JsonObject toJson() {
		// create JsonObject
		JsonObject object = new JsonObject();
		// write primitives in object
		object.addProperty("name", name);
		object.addProperty("default", isDefault);
		object.addProperty("prefix", prefix);
		object.addProperty("suffix", suffix);
		
		// create jsonarray for permissions
		JsonArray permissionsArray = new JsonArray();
		// write all permissions into array
		this.permissions.toSet().forEach(permission -> permissionsArray.add(new JsonPrimitive(permission)));
		// write array into object
		object.add("permissions", permissionsArray);
		
		// create JsonArray for inheritances
		JsonArray inheritancesArray = new JsonArray();
		// write all inheritances into array
		groups.forEach(group -> inheritancesArray.add(new JsonPrimitive(group.getName())));
		// write array into object
		object.add("extends", inheritancesArray);
		
		// return object
		return object;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public void extend(Group other) {
		// Validate
		Validate.notNull(other, "Other group cannot be null");
		// add to set
		groups.add(other);
	}
}
