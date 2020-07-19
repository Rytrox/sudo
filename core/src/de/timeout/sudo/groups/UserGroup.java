package de.timeout.sudo.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.utils.Customizable;

public abstract class UserGroup extends Group implements Customizable, Inheritable<UserGroup> {
		
	protected final Set<UserGroup> groups = new HashSet<>();
	
	protected String prefix;
	protected String suffix;
	protected boolean def;
	
	/**
	 * Constructor for inheritances
	 */
	protected UserGroup(@Nonnull String name, @Nullable String prefix, @Nullable String suffix, boolean isDefault) {
		super(name);
		// Validate and ban sudo name
		Validate.isTrue(!"sudo".equalsIgnoreCase(name), "UserGroup cannot be named with sudo");
		
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
