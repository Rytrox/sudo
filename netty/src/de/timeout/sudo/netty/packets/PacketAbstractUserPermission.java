package de.timeout.sudo.netty.packets;

import de.timeout.sudo.users.User;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public abstract class PacketAbstractUserPermission extends PacketAbstractUser {

	private static final long serialVersionUID = -5535034339283006091L;
	
	private String permission;

	public PacketAbstractUserPermission(@NotNull User user, @NotNull String permission) {
		super(user);
		
		Validate.notEmpty(permission, "Permission can neither be null nor empty");
		this.permission = permission;
	}
	
	/**
	 * Constructor for Decoders
	 */
	protected PacketAbstractUserPermission() {
		super();
	}
	
	/**
	 * Returns the permission. Cannot be null
	 * @return the permission. Cannot be null
	 */
	public String getPermission() {
		return permission;
	}
}
