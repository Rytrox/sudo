package de.timeout.sudo.netty.packets;

import de.timeout.sudo.groups.Group;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public abstract class PacketAbstractGroupPermission extends PacketAbstractGroup {

	private static final long serialVersionUID = -3062284237730059495L;
	
	protected String permission;
	
	public PacketAbstractGroupPermission(@NotNull Group group, @NotNull String permission) {
		super(group);
		
		// Validate
		Validate.notEmpty(permission, "Permission can neither be null nor empty");
		this.permission = permission;
	}
	
	/**
	 * Constructor for decoders
	 */
	protected PacketAbstractGroupPermission() {
		super();
	}
	
	/**
	 * Returns the permission. Cannot be null
	 * @return the permission. Cannot be null
	 */
	@NotNull
	public String getPermission() {
		return permission;
	}
}
