package de.timeout.sudo.permissions;

import org.jetbrains.annotations.NotNull;

public interface ChangeablePermissionHolder<T extends PermissionHolder> extends PermissionHolder {

	/**
	 * Apply the permission-container to this object.
	 * This operation will change its permissions.
	 * If the holder is null it will apply its own container
	 * @param holder the owner of the container.
	 */
	@NotNull
	public void applyPermissionContainer(T holder);
}
