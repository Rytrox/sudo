package de.timeout.sudo.netty.bukkit;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.netty.packets.Packet;
import de.timeout.sudo.netty.packets.PacketRemoteInDeleteGroup;
import de.timeout.sudo.netty.packets.PacketRemoteInGroupInheritance;
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
		} else if(packet instanceof PacketRemoteInGroupInheritance) {
			// get Packet
			PacketRemoteInGroupInheritance inheritances = (PacketRemoteInGroupInheritance) packet;
			// linking groups
			main.getGroupManager().loadInheritance(inheritances.getName(), inheritances.getInheritance());
		} else if(packet instanceof PacketRemoteInLoadUser) {
			// get Packet
			main.getUserManager().loadUserFromBungeecord(((PacketRemoteInLoadUser) packet).getUserData());
		} else if(packet instanceof PacketRemoteInDeleteGroup) {
			// get group
			Group group = main.getGroupManager().getGroupByName(((PacketRemoteInDeleteGroup) packet).getGroupName());
			
			// delete group
			if(group != null) main.getGroupManager().deleteGroup(group);
		}
	}

}
