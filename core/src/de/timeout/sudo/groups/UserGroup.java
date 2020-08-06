package de.timeout.sudo.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.permissions.PermissionHolder;
import de.timeout.sudo.permissions.UserGroupContainer;
import de.timeout.sudo.users.User;
import de.timeout.sudo.utils.Customizable;
import de.timeout.sudo.utils.Storable;

public abstract class UserGroup implements Group, Customizable, Inheritable<UserGroup>, PermissionHolder, Storable {
			
	protected final UserGroupContainer container;
	
	protected boolean def;
	
	/**
	 * Constructor for inheritances
	 */
	protected UserGroup(@Nonnull String name, @Nullable String prefix, @Nullable String suffix, boolean isDefault, @NotNull Collection<String> permissions) {
		// Validate and ban sudo name
		Validate.isTrue(!"sudo".equalsIgnoreCase(name), "UserGroup cannot be named with sudo");
		
		this.def = isDefault;
		
		this.container = new UserGroupContainer(this, name, prefix, suffix, new ArrayList<>(), permissions);
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return container.hasPermission(permission);
	}
	
	@Override
	public String getPrefix() {
		return container.getPrefix();
	}

	@Override
	public String getSuffix() {
		return container.getSuffix();
	}

	public boolean isDefault() {
		return def;
	}

	@Override
	public Collection<UserGroup> getExtendedGroups() {
		return container.getExtendedGroups();
	}

	@Override
	public int hashCode() {
		return Objects.hash(container);
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
		return Objects.equals(container, other.container);
	}

	@Override
	public void setPrefix(String prefix) {
		this.container.setPrefix(prefix);
	}

	@Override
	public void setSuffix(String suffix) {
		this.container.setSuffix(suffix);
	}

	@Override
	public void extend(UserGroup other) {
		container.extend(other);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public UserGroupContainer getPermissionContainer() {
		return container;
	}

	@Override
	public String getName() {
		return container.getName();
	}

	@Override
	public Collection<String> getPermissions() {
		return container.getPermissions();
	}

	@Override
	public boolean addPermission(String permission) {
		return container.addPermission(permission);
	}

	@Override
	public boolean removePermission(String permission) {
		return container.removePermission(permission);
	}

	@Override
	public boolean isMember(User element) {
		return container.add(element);
	}

	@Override
	public Collection<User> getMembers() {
		return container.getMembers();
	}

	@Override
	public boolean add(User element) {
		return container.add(element);
	}

	@Override
	public boolean remove(User element) {
		return container.remove(element);
	}
	
	
}
