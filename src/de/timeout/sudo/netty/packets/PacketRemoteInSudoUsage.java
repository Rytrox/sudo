package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import io.netty.buffer.ByteBuf;

/**
 * Packet for sending a sudo execution to spigot. <br>
 * This packet will be sent if the command is not a bungeecord command but the player uses the sudo prefix
 * 
 * @author Timeout
 *
 */
public class PacketRemoteInSudoUsage extends Packet<PacketRemoteInSudoUsage> {

	private UUID uuid;
	private String command;
	
	/**
	 * Constructor for decoders
	 * @author Timeout
	 *
	 */
	public PacketRemoteInSudoUsage() {
		super(PacketRemoteInSudoUsage.class);
	}
	
	/**
	 * Creates a new Packet
	 * @author Timeout
	 *
	 * @param uuid the uuid of the executor
	 * @param command the command without sudo prefix
	 */
	public PacketRemoteInSudoUsage(@Nonnull UUID uuid, @Nonnull String command) {
		super(PacketRemoteInSudoUsage.class);
		// Validate
		Validate.notNull(uuid, "UUID cannot be null");
		Validate.notEmpty(command, "Command can neither be empty nor null");
		
		this.uuid = uuid;
		this.command = command;
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		this.uuid = UUID.fromString(readString(input));
		this.command = readString(input);
	}
	
	@Override
	public void encode(ByteBuf output) throws IOException {
		// Do super call!
		super.encode(output);
		// write data in bytebuf
		writeString(output, uuid.toString());
		writeString(output, command);
	}

	/**
	 * Returns the uuid of the executor. Cannot be null
	 * @author Timeout
	 * 
	 * @return the uuid of the executor. Cannot be null
	 */
	@Nonnull
	public UUID getUniqueID() {
		return uuid;
	}
	
	/**
	 * Returns the command without sudo prefix. Cannot be null
	 * @author Timeout
	 * 
	 * @return the command without sudo prefix. Cannot be null
	 */
	@Nonnull
	public String getCommand() {
		return command;
	}
}
