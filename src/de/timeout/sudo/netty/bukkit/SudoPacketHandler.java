package de.timeout.sudo.netty.bukkit;

import java.lang.reflect.Field;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import de.timeout.libs.Reflections;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.bukkit.permissions.BukkitUser;
import de.timeout.sudo.bukkit.security.BukkitSudoer;
import de.timeout.sudo.netty.packets.Packet;
import de.timeout.sudo.netty.packets.PacketRemoteInAuthorizeSudoer;
import de.timeout.sudo.netty.packets.PacketRemoteInAuthorizeSudoer.AuthorizationResult;
import de.timeout.sudo.netty.packets.PacketRemoteInInitializeSudoGroup;
import de.timeout.sudo.netty.packets.PacketRemoteInSudoUsage;
import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Sudo handler which executes sudo relevant tasks
 * @author Timeout
 *
 */
class SudoPacketHandler extends SimpleChannelInboundHandler<Packet<?>> implements Root {
	
	private static final Field authorizedField = Reflections.getField(BukkitSudoer.class, "authorized");
	
	private static final Sudo main = Sudo.getInstance();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet<?> raw) throws Exception {
		// subtyping packet
		if(raw instanceof PacketRemoteInSudoUsage) {
			// get Packet
			PacketRemoteInSudoUsage packet = (PacketRemoteInSudoUsage) raw;
			// get User
			User user = main.getUserManager().getUser(packet.getUniqueID());
			// await sudo authentification
			main.getSudoHandler().awaitAuthentification(user, packet.getCommand());
		} else if(raw instanceof PacketRemoteInAuthorizeSudoer) {
			// get Packet
			PacketRemoteInAuthorizeSudoer packet = (PacketRemoteInAuthorizeSudoer) raw;
			// get User and result
			User user = main.getUserManager().getUser(Bukkit.getOfflinePlayer(packet.getUniqueID()));
			AuthorizationResult result = packet.getAuthResult();
			
			// upgrade to sudoer if the result id success
			if(result == AuthorizationResult.SUCCESS) {
				// upgrade sudoer
				BukkitSudoer sudoer = user instanceof Sudoer ? (BukkitSudoer) user : 
					BukkitSudoer.upgradeUserToSudoer((BukkitUser) user, this);
				// set authorized to true
				Reflections.setField(authorizedField, sudoer, true);
				// enables root access
				sudoer.enableRoot();
				// call sudo handler
				main.getSudoHandler().finishAuthorization(sudoer, false);
			} else if(result == AuthorizationResult.NO_SUDOER) {
				// send message to player
				Bukkit.getPlayer(user.getUniqueID()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cUnable to aquire sudo access service. Are you root?"));
			} else if(result == AuthorizationResult.BLOCKED) {
				// block user
				main.getSudoHandler().blockUser(packet.getUniqueID());
			}
		} else if(raw instanceof PacketRemoteInInitializeSudoGroup) {
			// get Permission
			Set<String> permissions = ((PacketRemoteInInitializeSudoGroup) raw).getPermissions();
			// add permission to sudogroup
			permissions.forEach(permission -> main.getGroupManager().getSudoGroup().addPermission(permission, this));
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
