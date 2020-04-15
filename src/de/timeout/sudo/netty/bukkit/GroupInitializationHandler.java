package de.timeout.sudo.netty.bukkit;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.netty.packets.Packet;
import de.timeout.sudo.netty.packets.PacketRemoteInGroupInheritances;
import de.timeout.sudo.netty.packets.PacketRemoteInInitializeGroup;
import de.timeout.sudo.netty.packets.PacketRemoteInLoadUser;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handler to receive and convert Group Initializations
 * @author Timeout
 *
 */
public class GroupInitializationHandler extends SimpleChannelInboundHandler<Packet<?>> {
	
	private static final Sudo main = Sudo.getInstance();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet<?> packet) throws Exception {
		// Channel is always authorized if this packet arrives
		if(packet instanceof PacketRemoteInInitializeGroup) {
			// group initialization
			main.getGroupManager().loadGroupFromBungeecord(((PacketRemoteInInitializeGroup) packet).getGroupData());
		} else if(packet instanceof PacketRemoteInGroupInheritances) {
			// get Packet
			PacketRemoteInGroupInheritances inheritances = (PacketRemoteInGroupInheritances) packet;
			// linking groups
			main.getGroupManager().loadInheritances(inheritances.getName(), inheritances.getInheritances());
		} else if(packet instanceof PacketRemoteInLoadUser) {
			// get Packet
			main.getGroupManager().loadUserFromBungeecord(((PacketRemoteInLoadUser) packet).getUserData());
		}
	}

}
