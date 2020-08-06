package de.timeout.sudo.permissions;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.UserGroup;

/**
 * Container class for permissiontrees of different users
 * @author Timeout
 *
 */
public class UserContainer extends PermissibleContainer<Group> {
		
	/**
	 * Creates a new Container of a user
	 * @param owner the owner of the container. Cannot be null
	 * @param permissions a set of permissions of the user. Cannot be null
	 * @throws IllegalArgumentException if any argument is null
	 */
	public UserContainer(@NotNull PermissionHolder owner, @NotNull Collection<String> permissions,
			@NotNull Collection<Group> groups, @NotNull String name,
			@Nullable String prefix, @Nullable String suffix) {
		super(owner, name, prefix, suffix, groups, permissions);
	}
	
	
	@Override
	public boolean hasPermission(String permission) {
		// search in groups for that permission
		for(Group group : members) {
			// ignore sudo-group
			if(group instanceof UserGroup && ((UserGroup) group).hasPermission(permission)) return true;
		}
		
		// search in own container if no other group contains this permission
		return super.hasPermission(permission);
	}

	/**
	 * Creates a copy of the permission container
	 * @param original the original you want to copy. Cannot be null
	 * @throws IllegalArgumentException if the original is null
	 */
	public UserContainer(@NotNull UserContainer original) {
		super(original.owner, original.name, original.prefix, original.suffix, original.members, original.permissions.toSet());			
	}
	
}
