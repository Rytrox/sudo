package de.timeout.sudo.netty.bungeecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.netty.ByteToPacketDecoder;
import de.timeout.sudo.netty.Closeable;
import de.timeout.sudo.netty.PacketToByteEncoder;
import de.timeout.sudo.netty.packets.Packet;

import net.jafama.FastMath;
import net.md_5.bungee.api.connection.Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class BungeeSocketServer implements Runnable, Closeable {
	
	private static final boolean EPOLL = Epoll.isAvailable();
	
	private final Map<Integer, Channel> connections = new HashMap<>();
	
	private int port;	
	
	/**
	 * Creates a new SocketServer on a certain port
	 * @author Timeout
	 *
	 * @param port the port
	 */
	public BungeeSocketServer(@Nonnegative int port) {
		this.port = FastMath.abs(port);
	}
	
	@Override
	public void run() {
		// log start
		Sudo.log().log(Level.INFO, "&7Start &6SocketServer");
		// create boss and worker group
		EventLoopGroup bossGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
		EventLoopGroup workerGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
		// log OS
		Sudo.log().log(Level.INFO, String.format("&9%s &adetected&7, using &3%s-Server", (EPOLL ? "Linux" : "Windows"), (EPOLL ? "Epoll" : "NIO")));
		
		// create Bootstrap
		try {
			 ChannelFuture channelFuture = new ServerBootstrap()
					.group(bossGroup, workerGroup)
					.channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel channel) throws Exception {							
							// link decoder and encoder
							channel.pipeline().addLast("encoder", new PacketToByteEncoder());
							channel.pipeline().addLast("decoder", new ByteToPacketDecoder());
							
							// link handlers
							channel.pipeline().addLast("authorize", new AuthorizeHandler());
							channel.pipeline().addLast("login", new LoginHandler());
						}

					})
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.bind(port).sync();
			 
			 // sync
			 channelFuture.syncUninterruptibly();
		} catch (InterruptedException e) {
			Sudo.log().log(Level.SEVERE, "&4Unable to start Netty-Server. Thread interrupted...", e);
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * Authorize a ChannelHandlerContext and adds it to collections
	 * @author Timeout
	 * 
	 * @param port the port of the connection
	 * @param ctx the new connection
	 * @throws IllegalArgumentException if the connection is null
	 * @return if the connection could be added
	 */
	public boolean authorize(@Nonnegative int port, @Nonnull ChannelHandlerContext ctx) {
		// Validate
		Validate.notNull(ctx, "ChannelHandlerContext cannot be null");
		// log
		Sudo.log().log(Level.INFO, String.format("&aBukkit-Server %s:%d connected!", ctx.channel().remoteAddress().toString().split(":")[0], port));
		
		// close old connection if channel is reconnecting
		ctx.channel().closeFuture().addListener(new ChannelFutureListener() {
			
			/**
			 * Will be executed when the connection close
			 */
			@Override
			public void operationComplete(ChannelFuture arg0) throws Exception {
				// remove channelhandlercontext from initializer
				for(Entry<Integer, Channel> entry : connections.entrySet()) {
					if(entry.getValue().equals(ctx.channel())) {
						// Log disconnection
						Sudo.log().log(Level.INFO, 
								String.format("&cBukkit-Server %s:%d disconnected!",
									arg0.channel().remoteAddress().toString().split(":")[0], entry.getKey()));
						// remove key
						connections.remove(entry.getKey());
					}	
				}
			}
		});
		
		// disconnect old connection if there was an old connection
		if(connections.containsKey(port)) connections.remove(port).close();
		
		// add to set
		return connections.put(FastMath.abs(port), ctx.channel()) != null;
	}
	
	/**
	 * Sends a packet to all connected and authorized Servers.
	 * This method does nothing if the packet is null
	 * @author Timeout
	 * 
	 * @param packet the packet you want to broadcast
	 */
	public void broadcastPacket(Packet<?> packet) {
		// do nothing if the packet is null
		if(packet != null)
			// for each connection
			connections.values().forEach(connection -> 
				connection.writeAndFlush(packet, connection.voidPromise())
			);
	}

	/**
	 * Sends a packet to a certain server
	 * @author Timeout
	 * 
	 * @param server the server you want to send the packet
	 * @param packet the packet you want to send
	 * @throws IllegalStateException if the server is not connected with this SocketServer
	 */
	public void sendPacket(Server server, Packet<?> packet) {
		// do nothing if any argument is null
		if(server != null && packet != null) {
			// get port
			Channel channel = connections.get(Integer.parseInt(server.getSocketAddress().toString().split(":")[1]));
			// send packet if channel is not null
			if(channel != null) channel.writeAndFlush(packet, channel.voidPromise());
			else throw new IllegalStateException("A Server with that port is not connected with Sudo-SocketServer");
		}
	}
	
	@Override
	public void close() {
		// disconnect to all connections
		connections.values().forEach(Channel::close);
	}
}
