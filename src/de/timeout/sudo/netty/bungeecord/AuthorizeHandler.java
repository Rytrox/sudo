package de.timeout.sudo.netty.bungeecord;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.netty.packets.Packet;
import de.timeout.sudo.netty.packets.PacketProxyInAuthorize;
import de.timeout.sudo.netty.packets.PacketRemoteInAuthorize;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuthorizeHandler extends SimpleChannelInboundHandler<Packet<?>> {
	
	private static final Sudo main = Sudo.getInstance();
	private static final UUID bungeeID = loadProxyUUID();
	
	private boolean authorized;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet<?> packet) throws Exception {
		// send packet to next handler if the client is authorized
		if(!authorized) {
			// get IP
			String remote = ctx.channel().remoteAddress().toString();
			// block pipeline if packet is send but not authorized
			if(packet instanceof PacketProxyInAuthorize) {
				// compare results (return true if both are equal) 
				authorized = bungeeID.compareTo(((PacketProxyInAuthorize) packet).getProxyID()) == 0;
				// send result to remote
				PacketRemoteInAuthorize authorize = new PacketRemoteInAuthorize(authorized);
				ctx.writeAndFlush(authorize, ctx.voidPromise());
				
				// drop connection if authorization failed
				if(!authorized) {
					ctx.close();
					// log result
					Sudo.log().log(Level.INFO, String.format("&2Remote-Server &a%s &7was trying to connect &cwithout authorization&7. &cDropping connection immediately", remote));
					Sudo.log().log(Level.INFO, "&cPlease have a look at https://www.spigotmc.org/wiki/firewall-guide/ and activate a firewall for this server too!");
				} else {
					// authorize in bungeecord
					main.getNettyServer().authorize(ctx);
					// log
					Sudo.log().log(Level.INFO, String.format("&2Remote-Server &a%s &7was &asuccessfully authorized.", remote));
				}
			} else Sudo.log().log(Level.WARNING, String.format("&cRemote %s tries to send %s without being authorized!", remote, packet.getClass().getSimpleName()));
		} 
	}
	
	

	/**
	 * Loads the UUID from the BungeeCord Configuration
	 * @author Timeout
	 * 
	 * @return the UUID of the proxy
	 */
	private static UUID loadProxyUUID() {
		try {
			// get bungeecord configuration
			Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class)
					.load(new File(main.getDataFolder().getParentFile().getParentFile(), "config.yml"));
			// read uuid 
			return UUID.fromString(config.getString("stats"));
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "&cUnable to read Bungeecord Configuration-File", e);
		}
		// return random UUID for errors
		return UUID.randomUUID();
	}

	/**
	 * Send packet to next handler if the connection is authorized!
	 */
	@Override
	public boolean acceptInboundMessage(Object arg0) throws Exception {
		return !authorized;
	}
}
