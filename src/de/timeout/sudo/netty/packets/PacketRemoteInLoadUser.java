package de.timeout.sudo.netty.packets;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.timeout.sudo.groups.User;

import io.netty.buffer.ByteBuf;

/**
 * Packet to send user data 
 * @author Timeout
 *
 */
public class PacketRemoteInLoadUser extends Packet<PacketRemoteInLoadUser> {
	
	private JsonObject data;

	/**
	 * Constructor to create a new Packet. BungeeCord only!
	 * @author Timeout
	 *
	 * @param user the user you want to send
	 */
	public PacketRemoteInLoadUser(User user) {
		super(PacketRemoteInLoadUser.class);
		this.data = user.toJson();
	}
	
	/**
	 * Constructor for Decoders
	 * @author Timeout
	 *
	 */
	public PacketRemoteInLoadUser() {
		super(PacketRemoteInLoadUser.class);
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		// decode data
		data = new JsonParser().parse(readString(input)).getAsJsonObject();
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// do super call
		super.encode(output);
		// write data into packet
		writeString(output, data.getAsString());
	}
	
	/**
	 * Returns the data of the user. Cannot be null
	 * @author Timeout
	 * 
	 * @return the user as Json-Object. Cannot be null
	 */
	@Nonnull
	public JsonObject getUserData() {
		return data;
	}
}
