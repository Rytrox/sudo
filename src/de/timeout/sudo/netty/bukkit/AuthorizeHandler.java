package de.timeout.sudo.netty.bukkit;

import java.util.logging.Level;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.netty.packets.PacketProxyInLogin;
import de.timeout.sudo.netty.packets.PacketRemoteInAuthorize;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handler which manages Authorization for Remote-Servers (Bukkit / Spigot)
 * @author Timeout
 *
 */
public class AuthorizeHandler extends SimpleChannelInboundHandler<PacketRemoteInAuthorize> {
		
	private boolean success;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PacketRemoteInAuthorize packet) throws Exception {
		// only read if success is not marked
		if(!success) {
			// update success
			success = packet.isSucceed();
			// continue if succeed
			if(success) {
				// log result
				Sudo.log().log(Level.INFO,"&2Connection &7is &aauthorized&7. Ready to receive information from &2BungeeCord");
				// send login packet to bungeecord. Ask for data
				ctx.writeAndFlush(new PacketProxyInLogin(), ctx.voidPromise());
			} else Sudo.log().log(Level.INFO, "&2Connection &ccannot be authorized&7. &cPlease check your configuration and try again.");
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		success = false;
	}


	/**
	 * Send packet to next handler if the connection is authorized!
	 */
	@Override
	public boolean acceptInboundMessage(Object arg0) throws Exception {
		return !success;
	}

	/**
	 * This method checks if the current channel is authorized
	 * @author Timeout
	 * 
	 * @return if the connection is authorized
	 */
	public boolean isAuthorized() {
		return success;
	}
}
