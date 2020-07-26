package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.UUID;

import de.timeout.sudo.users.User;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import io.netty.buffer.ByteBuf;

/**
 * Packet which will be sent if a user executes a bukkit command with sudo prefix
 * @author Timeout
 *
 */
public class PacketRemoteInExecuteSudoCommand extends Packet<PacketRemoteInExecuteSudoCommand> {

	private UUID user;
	private String executorname;
	private String command;
	
	/**
	 * Constructor for Sudo Commands
	 */
	public PacketRemoteInExecuteSudoCommand() {
		super(PacketRemoteInExecuteSudoCommand.class);
	}

	public PacketRemoteInExecuteSudoCommand(@NotNull User user, @NotNull String command) {
		super(PacketRemoteInExecuteSudoCommand.class);
		
		Validate.notNull(user, "User cannot be null");
		Validate.notEmpty(command, "Command can neither be null nor empty");
		Validate.isTrue(user.isSudoer(), "User must be sudoer to execute this command");
		
		this.user = user.getUniqueID();
		this.executorname = user.getName();
		this.command = command;
	}
	
	@Override
	public void decode(ByteBuf input) throws IOException {
		this.user = readUUID(input);
		this.executorname = readString(input);
		this.command = readString(input);
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// Do super call!
		super.encode(output);
		
		writeString(output, user.toString());
		writeString(output, executorname);
		writeString(output, command);
	}

	/**
	 * Returns the user who executes the command
	 * @return the user. Cannot be null
	 */
	@NotNull
	public UUID getUser() {
		return user;
	}

	/**
	 * Returns the name of the profile the user is going to use
	 * @return the executorname the name of the execution profile. Cannot be null
	 */
	public String getExecutorname() {
		return executorname;
	}

	/**
	 * Returns the executed command without sudo-prefix
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
}
