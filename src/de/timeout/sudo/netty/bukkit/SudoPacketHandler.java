package de.timeout.sudo.netty.bukkit;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;

import de.timeout.libs.Reflections;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.bukkit.permissions.BukkitUser;
import de.timeout.sudo.bukkit.security.BukkitSudoer;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.netty.packets.Packet;
import de.timeout.sudo.netty.packets.PacketRemoteInAuthorizeSudoer;
import de.timeout.sudo.netty.packets.PacketRemoteInAuthorizeSudoer.AuthorizationResult;
import de.timeout.sudo.netty.packets.PacketRemoteInSudoUsage;
import de.timeout.sudo.security.Root;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

class SudoPacketHandler extends SimpleChannelInboundHandler<Packet<?>> implements Root {
	
	private static final Field authorizedField = Reflections.getField(BukkitSudoer.class, "authorized");
	
	private static final Sudo main = Sudo.getInstance();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet<?> raw) throws Exception {
		// subtyping packet
		if(raw instanceof PacketRemoteInSudoUsage) {
			// get Packet
			PacketRemoteInSudoUsage packet = (PacketRemoteInSudoUsage) raw;
			// await sudo authentification
		} else if(raw instanceof PacketRemoteInAuthorizeSudoer) {
			// get Packet
			PacketRemoteInAuthorizeSudoer packet = (PacketRemoteInAuthorizeSudoer) raw;
			// get User and result
			User user = main.getGroupManager().getUser(Bukkit.getOfflinePlayer(packet.getUniqueID()));
			AuthorizationResult result = packet.getAuthResult();
			
			// upgrade to sudoer if the result id success
			if(result == AuthorizationResult.SUCCESS) {
				// upgrade sudoer
				BukkitSudoer sudoer = BukkitSudoer.upgradeUserToSudoer((BukkitUser) user, "", this);
				// set authorized to true
				Reflections.setField(authorizedField, sudoer, true);
				// upgrade user
				main.getGroupManager().upgradeUser(sudoer, this);
			}
		}
	}

	@Override
	public boolean enableRoot() {
		return false;
	}

	@Override
	public boolean disableRoot() {
		return false;
	}

	@Override
	public boolean isRoot() {
		return true;
	}

}
