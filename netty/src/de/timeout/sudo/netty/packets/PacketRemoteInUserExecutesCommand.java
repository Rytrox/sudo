package de.timeout.sudo.netty.packets;

import de.timeout.sudo.users.User;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

/**
 * Packet which will be sent when the user executes a Bukkit-Command with Sudo-Prefix
 * @author Timeout
 *
 */
public class PacketRemoteInUserExecutesCommand extends PacketAbstractUser {

	private static final long serialVersionUID = 7836492280646535383L;
	
	private String command;
	
	/**
	 * Constructor for decoders
	 */
	public PacketRemoteInUserExecutesCommand() {
		super();
	}
	
	/**
	 * Creates a new Packet
	 * @param user the user who executes the command
	 * @param command the bukkit command which is going to be executed by the player
	 */
	public PacketRemoteInUserExecutesCommand(@NotNull User user, @NotNull String command) {
		super(user);
		
		Validate.notEmpty(command, "Command can neither be null nor empty");
		this.command = command;
	}
	
	@NotNull
	public String getCommand() {
		return command;
	}
}
