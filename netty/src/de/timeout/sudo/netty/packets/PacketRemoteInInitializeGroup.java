package de.timeout.sudo.netty.packets;

import java.io.IOException;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.groups.UserGroup;

import io.netty.buffer.ByteBuf;

/**
 * Packet to initialize a group without their inheritances
 * @author Timeout
 *
 */
public class PacketRemoteInInitializeGroup extends Packet<PacketRemoteInInitializeGroup> {

	private String name;
	private String prefix;
	private String suffix;
	private boolean isDefault;
	
	/**
	 * Constructor for Decoders
	 * @author Timeout
	 *
	 */
	public PacketRemoteInInitializeGroup() {
		super(PacketRemoteInInitializeGroup.class);
	}
	
	/**
	 * Constructor to create a new Packet from Serverside (BungeeCord)
	 * @author Timeout
	 *
	 * @param group the group you want to compile. Cannot be null
	 */
	public PacketRemoteInInitializeGroup(@NotNull UserGroup group) {
		super(PacketRemoteInInitializeGroup.class);
		// Validate
		Validate.notNull(group, "Group cannot be null");
		this.name = group.getName();
		this.prefix = group.getPrefix();
		this.suffix = group.getSuffix();
		this.isDefault = group.isDefault();
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		// write group
		this.name = readString(input);
		this.prefix = readString(input);
		this.suffix = readString(input);
		this.isDefault = input.readBoolean();
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// do super call
		super.encode(output);
		// write data in bytebuf
		writeString(output, name);
		writeString(output, prefix);
		writeString(output, suffix);
		output.writeBoolean(isDefault);
	}
	
	@NotNull
	public String getName() {
		return name;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	
	@NotNull
	public String getPrefix() {
		return prefix;
	}
	
	@NotNull
	public String getSuffix() {
		return suffix;
	}
}
