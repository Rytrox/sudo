package de.timeout.sudo.netty.bungeecord;

import java.util.logging.Level;

import javax.annotation.Nonnegative;

import de.timeout.sudo.bungee.Sudo;

import net.jafama.FastMath;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class BungeeSocketServer implements Runnable {
	
	private static final boolean EPOLL = Epoll.isAvailable();

	private int port;
	private ChannelFuture channel;
	
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
			this.channel = new ServerBootstrap()
					.group(bossGroup, workerGroup)
					.channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel arg0) throws Exception {
							// Wird aufgerufen, wenn verbunden wird
							
						}
						
					})
					.option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.bind(port).sync().channel().closeFuture();
			
			// sync
			this.channel.sync();
		} catch (InterruptedException e) {
			Sudo.log().log(Level.SEVERE, "&4Unable to start Netty-Server. Thread interrupted...", e);
			Thread.currentThread().interrupt();
		}
	}
}
