package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.groups.Group;
import de.timeout.sudo.users.User;
import io.netty.buffer.ByteBuf;

public class PacketRemoteInUserJoinsGroup extends Packet<PacketRemoteInUserJoinsGroup> {
	
	private UUID userID;
	private String group;

	/**
	 * Constructor for decoders
	 */
	public PacketRemoteInUserJoinsGroup() {
		super(PacketRemoteInUserJoinsGroup.class);
	}

	/**
	 * Creates a new packet
	 * 
	 * @param user the user who joins a group. Cannot be null
	 * @param group the group which the user joins. Cannot be null
	 * 
	 * @throws IllegalArgumentException if any argument is null
	 */
	public PacketRemoteInUserJoinsGroup(@NotNull User user, @NotNull Group group) {
		super(PacketRemoteInUserJoinsGroup.class);
		
		Validate.notNull(user, "User cannot be null");
		Validate.notNull(group, "Group cannot be null");
		
		this.userID = user.getUniqueID();
		this.group = group.getName();
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		this.userID = readUUID(input);
		this.group = readString(input);
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// Do Super call!
		super.encode(output);
		
		writeString(output, userID.toString());
		writeString(output, group);
	}

	/**
	 * Returns the id of the user
	 * @return the userID
	 */
	@NotNull
	public UUID getUserID() {
		return userID;
	}

	/**
	 * Returns the name of the group
	 * @return the group
	 */
	@NotNull
	public String getGroup() {
		return group;
	}
}
