package de.timeout.sudo.netty.packets;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.timeout.sudo.groups.Group;

import io.netty.buffer.ByteBuf;

/**
 * Packet to initialize a group without their inheritances
 * @author Timeout
 *
 */
public class PacketRemoteInInitializeGroup extends Packet<PacketRemoteInInitializeGroup> {

	private JsonObject group;
	
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
	public PacketRemoteInInitializeGroup(@Nonnull Group group) {
		super(PacketRemoteInInitializeGroup.class);
		// Validate
		Validate.notNull(group, "Group cannot be null");
		this.group = group.toJson();
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		// write group
		group = new JsonParser().parse(readString(input)).getAsJsonObject();
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// do super call
		super.encode(output);
		// write data in bytebuf
		writeString(output, group.toString());
	}
	
	/**
	 * Receives the group data as a Json-Object. Cannot be null
	 * @author Timeout
	 * 
	 * @return the group data as a Json-Object.
	 */
	@Nonnull
	public JsonObject getGroupData() {
		return group;
	}
}
