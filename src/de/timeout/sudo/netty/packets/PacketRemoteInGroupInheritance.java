package de.timeout.sudo.netty.packets;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.groups.UserGroup;

import io.netty.buffer.ByteBuf;

/**
 * Packet to transfer inheritances of groups
 * @author Timeout
 *
 */
public class PacketRemoteInGroupInheritance extends Packet<PacketRemoteInGroupInheritance> {
		
	private String name;
	private String inheritance;

	/**
	 * Constructor to create a new Packet. BungeeCord only!
	 * @author Timeout
	 *
	 * @param group the group to convert
	 */
	public PacketRemoteInGroupInheritance(@Nonnull UserGroup group, @Nonnull String inheritance) {
		super(PacketRemoteInGroupInheritance.class);
		// Validate
		Validate.notNull(group, "Group cannot be null");
		Validate.notNull(inheritance, "Inheritance cannot be null");
		
		this.name = group.getName();
		this.inheritance = inheritance;
	}
	
	/**
	 * Constructor for decoders
	 * @author Timeout
	 *
	 */
	public PacketRemoteInGroupInheritance() {
		super(PacketRemoteInGroupInheritance.class);
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		// read name
		name = readString(input);
		// read inheritance
		inheritance = readString(input);
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// do super call
		super.encode(output);
		
		// write group name
		writeString(output, name);
		// write inheritance
		writeString(output, inheritance);
	}
	
	@Nonnull
	public String getName() {
		return name;
	}
	
	@Nonnull
	public String getInheritance() {
		return inheritance;
	}
}
