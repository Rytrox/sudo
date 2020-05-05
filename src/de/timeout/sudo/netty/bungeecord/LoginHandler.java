package de.timeout.sudo.netty.bungeecord;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.netty.packets.PacketProxyInLogin;
import de.timeout.sudo.netty.packets.PacketRemoteInGroupInheritance;
import de.timeout.sudo.netty.packets.PacketRemoteInInitializeGroup;
import de.timeout.sudo.netty.packets.PacketRemoteInInitializeSudoGroup;
import de.timeout.sudo.netty.packets.PacketRemoteInLoadUser;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LoginHandler extends SimpleChannelInboundHandler<PacketProxyInLogin> {

	private static final Sudo main = Sudo.getInstance();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PacketProxyInLogin packet) throws Exception {
		// sending sudo group
		ctx.writeAndFlush(new PacketRemoteInInitializeSudoGroup(main.getGroupManager().getSudoGroup().getPermissions()));
		
		// sending usergroup initializsation packet to client
		main.getGroupManager().getGroups().forEach(group -> 
			// create packet
			ctx.writeAndFlush(new PacketRemoteInInitializeGroup(group))
		);
		// sending usergroup inheritance to client
		main.getGroupManager().getGroups().forEach(group -> 
			group.getExtendedGroups().forEach(inheritance -> 
				// create packet
				ctx.writeAndFlush(new PacketRemoteInGroupInheritance(group, inheritance.getName()))
			)
		);
		
		// sending all current users to server
		main.getUserManager().getUsers().forEach(user -> 
			// create packet
			ctx.writeAndFlush(new PacketRemoteInLoadUser(user))
		);
	}

}
