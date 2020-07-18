package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;

/**
 * Packet which initializes the sudo group
 * @author Timeout
 *
 */
public class PacketRemoteInInitializeSudoGroup extends Packet<PacketRemoteInInitializeSudoGroup> {

	private final Set<String> permissions = new HashSet<>();
	
	/**
	 * Constructor for decoders
	 * @author Timeout
	 *
	 */
	public PacketRemoteInInitializeSudoGroup() {
		super(PacketRemoteInInitializeSudoGroup.class);
	}
	
	/**
	 * Creates a new Packet
	 * @author Timeout
	 *
	 * @param permissions the permission of the sudo group
	 */
	public PacketRemoteInInitializeSudoGroup(Set<String> permissions) {
		super(PacketRemoteInInitializeSudoGroup.class);
		this.permissions.addAll(permissions);
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		// read length of permissions
		int length = input.readInt();
		// read List
		for(int i = 0; i < length; i++) permissions.add(readString(input));
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// Do super call!
		super.encode(output);
		// write length
		output.writeInt(permissions.size());
		// write each permission in output
		for(String permission : permissions) writeString(output, permission);
	}
	
	/**
	 * Returns a set of all permissions which are blocked by sudo. Cannot be null
	 * @author Timeout
	 * 
	 * @return a set containing all blocked permissions.
	 */
	@Nonnull
	public Set<String> getPermissions() {
		return new HashSet<>(permissions);
	}
}
