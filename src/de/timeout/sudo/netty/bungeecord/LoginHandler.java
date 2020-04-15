package de.timeout.sudo.netty.bungeecord;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.netty.packets.PacketProxyInLogin;
import de.timeout.sudo.netty.packets.PacketRemoteInGroupInheritances;
import de.timeout.sudo.netty.packets.PacketRemoteInInitializeGroup;
import de.timeout.sudo.netty.packets.PacketRemoteInLoadUser;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LoginHandler extends SimpleChannelInboundHandler<PacketProxyInLogin> {

	private static final Sudo main = Sudo.getInstance();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PacketProxyInLogin packet) throws Exception {
		// sending packet to server
		main.getGroupManager().getGroups().forEach(group -> 
			// create packet
			ctx.writeAndFlush(new PacketRemoteInInitializeGroup(group))
		);
		// sending linkings to server
		main.getGroupManager().getGroups().forEach(group -> 
			// create packet
			ctx.writeAndFlush(new PacketRemoteInGroupInheritances(group))
		);
//		// sending all current users to server
//		main.getGroupManager().getUsers().forEach(user -> 
//			// create packet
//			ctx.writeAndFlush(new PacketRemoteInLoadUser(user))
//		);
	}

}
