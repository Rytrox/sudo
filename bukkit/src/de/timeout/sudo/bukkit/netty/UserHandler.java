package de.timeout.sudo.bukkit.netty;

import java.util.UUID;
import java.util.logging.Level;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.netty.packets.PacketAbstractUser;
import de.timeout.sudo.netty.packets.PacketRemoteInAddUserPermission;
import de.timeout.sudo.netty.packets.PacketRemoteInApplyContainer;
import de.timeout.sudo.netty.packets.PacketRemoteInLoadUser;
import de.timeout.sudo.netty.packets.PacketRemoteInRemoveUserPermission;
import de.timeout.sudo.netty.packets.PacketRemoteInUpdateUserProfile;
import de.timeout.sudo.netty.packets.PacketRemoteInUserExecutesCommand;
import de.timeout.sudo.netty.packets.PacketRemoteInUserJoinsGroup;
import de.timeout.sudo.netty.packets.PacketRemoteInUserLeavesGroup;
import de.timeout.sudo.users.User;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class UserHandler extends SimpleChannelInboundHandler<PacketAbstractUser> {

	private static final Sudo main = Sudo.getInstance();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PacketAbstractUser packet) {
		// get user
		User user = main.getUserManager().getUser(packet.getUserID());
		
		// only continue if user could be loaded
		if(user != null) {
			// check packettype
			if(packet instanceof PacketRemoteInAddUserPermission) {
				// add permission to user
				user.addPermission(((PacketRemoteInAddUserPermission) packet).getPermission());
			} else if(packet instanceof PacketRemoteInApplyContainer) {
				// get owner
				User owner = getPermissionHolder(((PacketRemoteInApplyContainer) packet).getContainerOwner());
				
				// apply container to user
				user.applyPermissionContainer(owner);
			} else if(packet instanceof PacketRemoteInLoadUser) {
				// load user from uuid
				main.getUserManager().loadUser(packet.getUserID());
			} else if(packet instanceof PacketRemoteInRemoveUserPermission) {
				// remove permission
				user.removePermission(((PacketRemoteInRemoveUserPermission) packet).getPermission());
			} else if(packet instanceof PacketRemoteInUpdateUserProfile) {
				// update prefix and suffix
				user.setPrefix(((PacketRemoteInUpdateUserProfile) packet).getPrefix());
				user.setSuffix(((PacketRemoteInUpdateUserProfile) packet).getSuffix());
			} else if(packet instanceof PacketRemoteInUserExecutesCommand) {
				// executes command for user
				Bukkit.getPlayer(user.getUniqueID()).performCommand(((PacketRemoteInUserExecutesCommand) packet).getCommand());
			} else if(packet instanceof PacketRemoteInUserJoinsGroup) {
				// join group
				user.joinGroup(main.getGroupManager().getGroupByName(((PacketRemoteInUserJoinsGroup) packet).getGroup()));
			} else if(packet instanceof PacketRemoteInUserLeavesGroup) {
				// leave group
				user.leaveGroup(main.getGroupManager().getGroupByName(((PacketRemoteInUserLeavesGroup) packet).getGroupName()));
			}
		} else Sudo.log().log(Level.WARNING, String.format("&cUnable to load user with id: %s", packet.getUserID().toString()));
	}

	/**
	 * Returns the user by its name or literal uuid
	 * @param name root for root or literal uuid of a player
	 * @return the uuid of the profile
	 */
	@NotNull
	private User getPermissionHolder(@NotNull String name) {
		// Validate
		Validate.notEmpty(name, "PermissionHolder cannot be empty");
		
		return name.equalsIgnoreCase("root") ? main.getUserManager().getRoot() : main.getUserManager().getUser(UUID.fromString(name)); 
	}
}
