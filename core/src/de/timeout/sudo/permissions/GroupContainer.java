package de.timeout.sudo.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.timeout.sudo.users.User;
import de.timeout.sudo.utils.Collectable;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GroupContainer extends AbstractContainer implements Collectable<User> {
	
	private final Set<User> members = new HashSet<>();

	/**
	 * Creates a new container for a group
	 * @param owner
	 * @param name
	 * @param permissions
	 */
	public GroupContainer(@NotNull PermissionHolder owner, @NotNull String name, @NotNull Collection<String> permissions,
			@Nullable String prefix, @Nullable String suffix) {
		super(owner, name, permissions, prefix, suffix);
	}
	
	/**
	 * Creates a copy of this group container
	 * @param other the original group container
	 */
	public GroupContainer(@NotNull GroupContainer other) {
		super(other.owner, other.name, other.getPermissions(), other.prefix, other.suffix);
		
		this.members.addAll(other.getMembers());
	}

	@Override
	public boolean isMember(User element) {
		return members.contains(element);
	}

	@Override
	public Collection<User> getMembers() {
		return new ArrayList<>(members);
	}

	@Override
	public boolean add(User element) {
		Validate.notNull(element, "User cannot be null");
		
		return members.add(element);
	}

	@Override
	public boolean remove(User element) {
		return members.remove(element);
	}

	
}
