package de.timeout.sudo.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.timeout.sudo.users.User;
import de.timeout.sudo.utils.Customizable;

public class UserGroup extends Group implements Customizable, Inheritable<UserGroup> {
		
	protected final Set<UserGroup> groups = new HashSet<>();
	
	protected String prefix;
	protected String suffix;
	protected boolean def;
	
	/**
	 * Constructor for inheritances
	 */
	protected UserGroup(@Nonnull String name, @Nullable String prefix, @Nullable String suffix, boolean isDefault) {
		super(name);
		this.prefix = prefix;
		this.suffix = suffix;
		this.def = isDefault;
	}
	
	@Override
	public boolean hasPermission(String permission) {
		// return true if this group has permission
		if(!super.hasPermission(permission)) {
			// search in extended groups
			for(Group extended : getExtendedGroups()) {
				// search for permission, return true if found
				if(extended.hasPermission(permission)) return true;
			}
			// not found. return false
			return false;
		} else return true;
	}
	
	/**
	 * Adds a permission to this group. <br>
	 * Returns false if the permission is null
	 * 
	 * @param permission the permission to add
	 * @return if it succeed
	 */
	public boolean addPermission(String permission) {
		// if permission is not null or empty
		if(permission != null && !permission.isEmpty()) {
			// add permission to list
			return permissions.add(permission);
		}
		// return false
		return false;
	}

	/**
	 * Remove a permission from this group. <br>
	 * Returns false if the permission is null
	 * 
	 * @param permission the permission to remove
	 * @return if it succeed
	 */
	public boolean removePermission(String permission) {
		// if permission is not null
		if(permission != null) {
			// remove permission from collection
			return permissions.remove(permission);
		}
		// return false
		return false;
	}

	/**
	 * Adds a user to this group. <br>
	 * Returns false if the user is null
	 * 
	 * @param user the user
	 * @return true if the remove succeed else false
	 */
	public boolean join(User user) {
		return members.add(user);
	}

	/**
	 * Removes a user from this group <br>
	 * Returns false if the user is null
	 * 
	 * @param user the user
	 * @return true if the remove succeed else false
	 */
	public boolean kick(User user) {
		return members.remove(user);
	}
	
	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getSuffix() {
		return suffix;
	}

	public boolean isDefault() {
		return def;
	}

	@Override
	public Collection<UserGroup> getExtendedGroups() {
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
		UserGroup other = (UserGroup) obj;
		return Objects.equals(name, other.name) && Objects.equals(permissions, other.permissions);
	}

	@Override
	public JsonObject toJson() {
		// create JsonObject
		JsonObject object = new JsonObject();
		// write primitives in object
		object.addProperty("name", name);
		object.addProperty("default", def);
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
	public void extend(UserGroup other) {
		// Validate
		Validate.notNull(other, "Other group cannot be null");
		// add to set
		groups.add(other);
	}
}
