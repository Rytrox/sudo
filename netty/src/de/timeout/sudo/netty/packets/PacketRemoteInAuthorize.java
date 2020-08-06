package de.timeout.sudo.netty.packets;

/**
 * Packet which is sent by BungeeCord to send the result of Authorization
 * @author Timeout
 *
 */
public class PacketRemoteInAuthorize implements Packet {
	
	private static final long serialVersionUID = -4909347759026387478L;
	
	private boolean success;

	/**
	 * Constructor for BungeeCord
	 * @author Timeout
	 *
	 * @param success if the authorization succeed
	 */
	public PacketRemoteInAuthorize(boolean success) {
		this.success = success;
	}
	
	/**
	 * Checks if the authorization with BungeeCord succeed
	 * @author Timeout
	 * 
	 * @return the authorization with bungeecord
	 */
	public boolean isSucceed() {
		return success;
	}
}
