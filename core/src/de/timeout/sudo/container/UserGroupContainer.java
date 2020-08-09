package de.timeout.sudo.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.timeout.sudo.groups.Inheritable;
import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.permissions.PermissionHolder;
import de.timeout.sudo.users.User;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserGroupContainer extends PermissibleContainer<User> implements Inheritable<UserGroup> {
	
	private final Set<UserGroup> extendGroups = new HashSet<>();

	/**
	 * Creates a new container for a group
	 * @param owner
	 * @param name
	 * @param permissions
	 */
	public UserGroupContainer(@NotNull PermissionHolder owner, @NotNull String name,
			@Nullable String prefix, @Nullable String suffix, @NotNull Collection<User> members, @NotNull Collection<String> permissions) {
		super(owner, name, prefix, suffix, members, permissions);
	}
	
	/**
	 * Creates a copy of this group container
	 * @param other the original group container
	 */
	public UserGroupContainer(@NotNull UserGroupContainer other) {
		super(other.owner, other.name, other.prefix, other.suffix, other.getMembers(), other.getPermissions());	
	}

	@Override
	public Collection<UserGroup> getExtendedGroups() {
		return new ArrayList<>(extendGroups);
	}

	@Override
	public void extend(UserGroup other) {
		// Validate
		Validate.notNull(other, "Extended group cannot be null");
		
		this.extendGroups.add(other);
	}

	
}
