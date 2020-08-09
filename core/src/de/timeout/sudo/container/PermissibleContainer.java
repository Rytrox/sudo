package de.timeout.sudo.container;

import java.util.Collection;
import java.util.Optional;

import de.timeout.sudo.permissions.PermissionHolder;
import de.timeout.sudo.utils.PermissionTree;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PermissibleContainer<E> extends CollectableContainer<E> {
	
	private static final String PERMISSIONS_NULL = "Permission cannot be null";

	protected final PermissionTree permissions = new PermissionTree();
	protected final PermissionHolder owner;
	
	protected String prefix;
	protected String suffix;
	
	public PermissibleContainer(@NotNull PermissionHolder owner, @NotNull String name, @Nullable String prefix,
			@Nullable String suffix, @NotNull Collection<E> members, @NotNull Collection<String> permissions) {
		super(name, members);
		
		Validate.notNull(owner, "PermissionHolder cannot be null");
		Validate.notNull(permissions, "Permissions cannot be null");
		
		this.owner = owner;
		setPrefix(prefix);
		setSuffix(suffix);
		
		permissions.forEach(this::addPermission);
	}
	
	/**
	 * Checks if the container contains a permission
	 * @param permission the permission you want to check
	 * @return true if the permission could be found, false otherwise
	 */
	public boolean hasPermission(@NotNull String permission) {
		Validate.notNull(permission, PERMISSIONS_NULL);
		
		return permissions.contains(permission);
	}
	
	/**
	 * Adds a new permission to the tree and returns a result whether the tree is modified due to this operation or not
	 * @param permission the permission you want to add. Cannot be null
	 * @return true if the tree was modified due to this operation, false otherwise
	 * @throws IllegalArgumentException if any argument is null
	 */
	public boolean addPermission(@NotNull String permission) {
		// Validate
		Validate.notNull(permission, PERMISSIONS_NULL);
		
		// add permission
		return permissions.add(permission);
	}
	
	/**
	 * Removes a permission from the tree and returns a result whether the tree was modified due to this operation or not
	 * @param permission the permission you want to remove. Cannot be null
	 * @return true if the tree was modified due to this operation, false otherwise
	 * @throws IllegalArgumentException if any argument is null
	 */
	public boolean removePermission(@NotNull String permission) {
		// Validate
		Validate.notNull(permission, PERMISSIONS_NULL);
		
		// remove permission
		return permissions.remove(permission);
	}

	/**
	 * Returns a set of all permissions
	 * @return the permissions
	 */
	@NotNull
	public Collection<String> getPermissions() {
		return permissions.toSet();
	}

	/**
	 * Returns the owner of this Container
	 * @return the owner
	 */
	@NotNull
	public PermissionHolder getOwner() {
		return owner;
	}
	
	/**
	 * Returns the  prefix of this container
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Updates the prefix of this container
	 * @param prefix the  prefix to set. Please don't insert ColorCodes here!
	 */
	public void setPrefix(@Nullable String prefix) {
		this.prefix = Optional.ofNullable(prefix).orElse("");
	}

	/**
	 * Returns the  suffix of this container
	 * @return the suffix
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * Updates the suffix of this container
	 * @param suffix the  suffix to set. Please don't insert ColorCodes here!
	 */
	public void setSuffix(@Nullable String suffix) {
		this.suffix = Optional.ofNullable(suffix).orElse("");
	}
}
