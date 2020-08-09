package de.timeout.sudo.netty.packets;

import de.timeout.sudo.users.User;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

/**
 * 
 * @author Timeout
 *
 */
public class PacketRemoteInApplyContainer extends PacketAbstractUser {
	
	private static final long serialVersionUID = -2968004607353367639L;
	
	private String containerOwner;

	public PacketRemoteInApplyContainer(@NotNull User user, @NotNull String profile) {
		super(user);
		
		Validate.notEmpty(profile, "Profile cannot be null");
		this.containerOwner = profile;
	}
	
	/**
	 * Returns the name of the container's owner
	 * @return the name of the container's owner
	 */
	@NotNull
	public String getContainerOwner() {
		return containerOwner;
	}
}
