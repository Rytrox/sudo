package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.groups.Group;

import io.netty.buffer.ByteBuf;

/**
 * Packet to transfer inheritances of groups
 * @author Timeout
 *
 */
public class PacketRemoteInGroupInheritances extends Packet<PacketRemoteInGroupInheritances> {
	
	private final Set<String> inheritances = new HashSet<>();
	
	private String name;

	/**
	 * Constructor to create a new Packet. BungeeCord only!
	 * @author Timeout
	 *
	 * @param group the group to convert
	 */
	public PacketRemoteInGroupInheritances(@Nonnull Group group) {
		super(PacketRemoteInGroupInheritances.class);
		// Validate
		Validate.notNull(group, "Group cannot be null");
		
		this.name = group.getName();
		// add extended group
		group.getExtendedGroups().forEach(extend -> inheritances.add(extend.getName()));
	}
	
	/**
	 * Constructor for decoders
	 * @author Timeout
	 *
	 */
	public PacketRemoteInGroupInheritances() {
		super(PacketRemoteInGroupInheritances.class);
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		// read name
		name = readString(input);
		// read length of groups
		int length = input.readInt();
		// continue after finish
		for(int i = 0; i < length; i++) inheritances.add(readString(input));
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// do super call
		super.encode(output);
		
		// write group name
		writeString(output, name);
		// add length to output
		output.writeInt(inheritances.size());
		// add each element
		for(String group : inheritances) writeString(output, group);
	}
	
	@Nonnull
	public String getName() {
		return name;
	}
	
	@Nonnull
	public Set<String> getInheritances() {
		return new HashSet<>(inheritances);
	}
}
