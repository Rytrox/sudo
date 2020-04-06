package de.timeout.sudo.netty.bungeecord;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.netty.packets.PacketProxyInAuthorize;
import de.timeout.sudo.netty.packets.PacketRemoteInAuthorize;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuthorizeHandler extends SimpleChannelInboundHandler<PacketProxyInAuthorize> {
	
	private static final Sudo main = Sudo.getInstance();
	private static final UUID bungeeID = loadProxyUUID();
	
	private boolean authorized;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PacketProxyInAuthorize packet) throws Exception {
		System.out.println("Channel liest.");
		// only accept authorize if it is not authorized yet
		if(!authorized) {
			// compare results (return true if both are equal) 
			authorized = bungeeID.compareTo(packet.getProxyID()) == 0;
			// send result to remote
			PacketRemoteInAuthorize authorize = new PacketRemoteInAuthorize(authorized);
			ctx.writeAndFlush(authorize);
			
			// authorize if the uuid is correct
			if(authorized) {
				System.out.println("Hat geklappt!");
			} else {
				// block Connection and drop...
				System.out.println("Hat geklappt, ID ist aber falsch");
			}
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
}
