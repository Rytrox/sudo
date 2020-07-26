package de.timeout.sudo.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.utils.Collectable;

/**
 * Container class for permissiontrees of different users
 * @author Timeout
 *
 */
public class UserContainer extends AbstractContainer implements Collectable<UserGroup> {
	
	private final Set<UserGroup> groups = new HashSet<>();
	
	/**
	 * Creates a new Container of a user
	 * @param owner the owner of the container. Cannot be null
	 * @param permissions a set of permissions of the user. Cannot be null
	 * @throws IllegalArgumentException if any argument is null
	 */
	public UserContainer(@NotNull PermissionHolder owner, @NotNull Collection<String> permissions,
			@NotNull Collection<UserGroup> groups, @NotNull String name,
			@Nullable String prefix, @Nullable String suffix) {
		super(owner, name, permissions, prefix, suffix);
		
		Validate.notNull(groups, "Groups cannot be null");
		
		groups.forEach(this.groups::add);
	}
	
	/**
	 * Creates a copy of the permission container
	 * @param original the original you want to copy. Cannot be null
	 * @throws IllegalArgumentException if the original is null
	 */
	public UserContainer(@NotNull UserContainer original) {
		super(original.owner, original.name, original.getPermissions(), original.prefix, original.suffix);		
		
		this.groups.addAll(new ArrayList<>(original.groups));
	}
	

	@Override
	public boolean isMember(UserGroup element) {
		return groups.contains(element);
	}

	@Override
	public Collection<UserGroup> getMembers() {
		return new ArrayList<>(groups);
	}

	@Override
	public boolean add(UserGroup element) {
		// Validate
		Validate.notNull(element, "Group cannot be null");
		
		return groups.add(element);
	}

	@Override
	public boolean remove(UserGroup element) {
		return groups.remove(element);
	}
}
