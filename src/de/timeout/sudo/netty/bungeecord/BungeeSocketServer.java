package de.timeout.sudo.netty.bungeecord;

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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class BungeeSocketServer implements Runnable, Closeable {
	
	private static final boolean EPOLL = Epoll.isAvailable();
	
	private final ChannelGroup connections = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
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
							// Wird aufgerufen, wenn verbunden wird
							
							// link decoder and encoder
							channel.pipeline().addLast("encoder", new PacketToByteEncoder());
							channel.pipeline().addLast("decoder", new ByteToPacketDecoder());
							
							// link handlers
							channel.pipeline().addLast("authorize", new AuthorizeHandler());
							channel.pipeline().addLast("login", new LoginHandler());
							
							
							Sudo.log().log(Level.INFO, String.format("&aBukkit-Server %s connected!", channel.remoteAddress().toString()));
						}

						/**
						 * Remove channel from connection if this connection is closed
						 */
						@Override
						public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
							// remove channelhandlercontext from initializer
							connections.remove(ctx.channel());
						}
					})
					.option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.bind(port).sync().channel().closeFuture();
			
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
	 * @param ctx the new connection
	 * @throws IllegalArgumentException if the connection is null
	 * @return if the connection could be added
	 */
	public boolean authorize(@Nonnull ChannelHandlerContext ctx) {
		// Validate
		Validate.notNull(ctx, "ChannelHandlerContext cannot be null");
		// add to set
		return connections.add(ctx.channel());
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
			connections.forEach(connection -> {
				connection.writeAndFlush(packet, connection.voidPromise());
			});
	}

	@Override
	public void close() {
		// disconnect to all connections
		connections.forEach(Channel::close);
	}
}
