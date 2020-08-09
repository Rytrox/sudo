package de.timeout.sudo.bungee.netty;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.netty.packets.PacketProxyInSaveUser;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handler for PacketProxyInSaveUser
 * @author Timeout
 *
 */
public class SaveUserHandler extends SimpleChannelInboundHandler<PacketProxyInSaveUser> {

	private static final Sudo main = Sudo.getInstance();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PacketProxyInSaveUser packet) throws Exception {
		// save user
		main.getUserManager().getUser(packet.getUserID()).save();
	}

}
