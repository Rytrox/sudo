package de.timeout.sudo.netty.bungeecord;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.netty.packets.PacketProxyInAuthorizeSudoer;
import de.timeout.sudo.netty.packets.PacketRemoteInAuthorizeSudoer;
import de.timeout.sudo.netty.packets.PacketRemoteInAuthorizeSudoer.AuthorizationResult;
import de.timeout.sudo.security.Sudoer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handler which answers authorization-requests
 * @author Timeout
 *
 */
public class SudoerHandler extends SimpleChannelInboundHandler<PacketProxyInAuthorizeSudoer> {

	private static final Sudo main = Sudo.getInstance();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PacketProxyInAuthorizeSudoer packet) throws Exception {
		// create answer-packet
		PacketRemoteInAuthorizeSudoer authorize;
		// get User from packet
		User user = main.getGroupManager().getUser(packet.getUniqueID());
		
		// check if user is a sudoer
		if(user instanceof Sudoer) {
			// check password
			if(((Sudoer) user).authorize(packet.getEncodedPassword())) 
				authorize = new PacketRemoteInAuthorizeSudoer(user.getUniqueID(), AuthorizationResult.SUCCESS);
			else authorize = new PacketRemoteInAuthorizeSudoer(user.getUniqueID(), AuthorizationResult.PASSWORD_FAILED);
		} else authorize = new PacketRemoteInAuthorizeSudoer(user.getUniqueID(), AuthorizationResult.NO_SUDOER);
		
		// send result
		ctx.write(authorize, ctx.voidPromise());
	}

}
