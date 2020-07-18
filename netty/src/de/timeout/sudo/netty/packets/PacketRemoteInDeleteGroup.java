package de.timeout.sudo.netty.packets;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import io.netty.buffer.ByteBuf;

/**
 * Packet which will be sent if a certain group is deleted by the proxy
 * @author Timeout
 *
 */
public class PacketRemoteInDeleteGroup extends Packet<PacketRemoteInDeleteGroup> {

	private String group;
	
	/**
	 * Constructor for Decoders
	 * @author Timeout
	 *
	 */
	public PacketRemoteInDeleteGroup() {
		super(PacketRemoteInDeleteGroup.class);
	}

	/**
	 * Creates a new Packet. 
	 * @author Timeout
	 *
	 * @param groupname the name of the group. Can neither be null nor empty
	 * @throws IllegalArgumentException if the groupname is null, empty or is sudo.
	 */
	public PacketRemoteInDeleteGroup(@Nonnull String groupname) {
		super(PacketRemoteInDeleteGroup.class);
		
		Validate.notEmpty(groupname, "Group name can neither be null nor empty");
		Validate.isTrue(!groupname.equalsIgnoreCase("sudo"), "Sudo cannot be deleted");
		this.group = groupname;
	}
	
	/**
	 * Returns the name of the group
	 * @author Timeout
	 * 
	 * @return the name of the group
	 */
	@Nonnull
	public String getGroupName() {
		return group;
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		this.group = readString(input);
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// DO SUPER CALL
		super.encode(output);
		
		// write groupname
		writeString(output, group);
	}
}
