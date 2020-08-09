package de.timeout.sudo.bukkit.netty;

import java.util.logging.Level;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.netty.packets.PacketAbstractGroup;
import de.timeout.sudo.netty.packets.PacketRemoteInAddGroupPermission;
import de.timeout.sudo.netty.packets.PacketRemoteInDeleteGroup;
import de.timeout.sudo.netty.packets.PacketRemoteInGroupInheritance;
import de.timeout.sudo.netty.packets.PacketRemoteInRemoveGroupPermission;
import de.timeout.sudo.netty.packets.PacketRemoteInUpdateGroupProfile;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GroupHandler extends SimpleChannelInboundHandler<PacketAbstractGroup> {

	private static final Sudo main = Sudo.getInstance();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PacketAbstractGroup packet) {
		// get group
		Group group = main.getGroupManager().getGroupByName(packet.getGroupName());
		
		// only continue if group could be read
		if(group instanceof UserGroup) {
			// check packet type
			if(packet instanceof PacketRemoteInAddGroupPermission) {
				// add permission to group
				((UserGroup) group).addPermission(((PacketRemoteInAddGroupPermission) packet).getPermission());
			} else if(packet instanceof PacketRemoteInDeleteGroup) {
				// delete group
				main.getGroupManager().deleteGroup((UserGroup) group);
			} else if(packet instanceof PacketRemoteInGroupInheritance) {
				// add inheritance to this group
				main.getGroupManager().loadInheritance(group.getName(), ((PacketRemoteInGroupInheritance) packet).getInheritance());
			} else if(packet instanceof PacketRemoteInRemoveGroupPermission) {
				// remove permission from group
				((UserGroup) group).removePermission(((PacketRemoteInRemoveGroupPermission) packet).getPermission());
			} else if(packet instanceof PacketRemoteInUpdateGroupProfile) {
				// update prefix and suffix
				((UserGroup) group).setPrefix(((PacketRemoteInUpdateGroupProfile) packet).getPrefix());
				((UserGroup) group).setSuffix(((PacketRemoteInUpdateGroupProfile) packet).getSuffix());
			}
		} else Sudo.log().log(Level.WARNING, String.format("&cHandler tried to load &5Group&c: &d%s&c but group does not exist", packet.getGroupName()));
	}

}
