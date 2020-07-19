package de.timeout.sudo.bungee.netty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.netty.packets.PacketProxyInAuthorizeSudoer;
import de.timeout.sudo.netty.packets.PacketRemoteInAuthorizeSudoer;
import de.timeout.sudo.netty.packets.PacketRemoteInAuthorizeSudoer.AuthorizationResult;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handler which answers authorization-requests
 * @author Timeout
 *
 */
public class SudoerHandler extends SimpleChannelInboundHandler<PacketProxyInAuthorizeSudoer> {

	private static final Sudo main = Sudo.getInstance();
	
	private final int maxAttempts = main.getConfig().getInt("sudo.maxAttempts");
	
	private final Map<UUID, Integer> attempts = new HashMap<>();
	private final Set<UUID> blocked = new HashSet<>();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PacketProxyInAuthorizeSudoer packet) throws Exception {
		// get User from packet
		User user = main.getUserManager().getUser(packet.getUniqueID());
		
		// get result
		AuthorizationResult result;
		// check if user is a sudoer
		if(user instanceof Sudoer) {
			// if user is not blocked
			if(!blocked.contains(packet.getUniqueID())) {
				// check password
				if(((Sudoer) user).authorize(packet.getEncodedPassword())) {
					// define authorize
					result = AuthorizationResult.SUCCESS;
					// remove from attempts
					attempts.remove(packet.getUniqueID());
				} else {
					// increase attempts
					int current = Optional.ofNullable(attempts.get(packet.getUniqueID())).orElse(0).intValue() + 1;
					
					// check if current is smaller than allowed maxed value
					if(current < maxAttempts) {
						result = AuthorizationResult.PASSWORD_FAILED;
						// increase current value
						attempts.put(packet.getUniqueID(), current);
					} else {
						result = AuthorizationResult.BLOCKED;
						// block user
						blocked.add(packet.getUniqueID());
					}
				}
			} else result = AuthorizationResult.BLOCKED;
		} else result = AuthorizationResult.NO_SUDOER;
		
		// create answer-packet
		PacketRemoteInAuthorizeSudoer authorize = new PacketRemoteInAuthorizeSudoer(user.getUniqueID(), result);
		// send result
		ctx.write(authorize, ctx.voidPromise());
	}

	
}
