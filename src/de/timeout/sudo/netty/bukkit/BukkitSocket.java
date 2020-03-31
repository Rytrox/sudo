package de.timeout.sudo.netty.bukkit;

import java.util.logging.Level;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import com.google.common.io.ByteArrayDataOutput;

import de.timeout.sudo.bukkit.Sudo;

import net.jafama.FastMath;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class BukkitSocket implements Runnable {
	
	private static final boolean EPOLL = Epoll.isAvailable();
	
	private String host;
	private int port;
	private ChannelFuture channel;
	
	public BukkitSocket(@Nonnull String host, @Nonnegative int port) {
		// Validate
		Validate.notNull(host, "BungeeCord's address cannot be null");
		
		this.host = host;
		this.port = FastMath.abs(port);
	}
	
	@Override
	public void run() {
		// log start of client
		Sudo.log().log(Level.INFO, "&7Creating &5Connection &7to &2BungeeCord");
		// check for OS
		Sudo.log().log(Level.INFO, String.format("&9%s &adetected&7, using &3%s-ServerSockets", (EPOLL ? "Linux" : "Windows"), (EPOLL ? "Epoll" : "NIO")));
		
		// create bootstrap for netty client
		Bootstrap boot = new Bootstrap();
		// create loop group
		EventLoopGroup workerGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
		
		boot.group(workerGroup);
		boot.channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class);
		boot.option(ChannelOption.SO_KEEPALIVE, true);
		// register Handler
		boot.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel arg0) throws Exception {
				// register decoder and encoder
			}
		});
		
		Sudo.log().log(Level.INFO, "&aCreation succeed. Start connecting...");
		try {
			// connect to bungeecord
			channel = boot.connect(host, port).sync().channel().closeFuture();
			// connection succeed. log
			Sudo.log().log(Level.INFO, "&cConnected to BungeeCord!");
			// keep synchronization
			channel.syncUninterruptibly();
		} catch (InterruptedException e) {
			Sudo.log().log(Level.SEVERE, "&cUnable to hold Connection to BungeeCord. Thread interrupted", e);
			Thread.currentThread().interrupt();
		} finally {
			// shutdown connection
			workerGroup.shutdownGracefully();
		}
	}

	public ChannelFuture sendMessageToBungeeCord(ByteArrayDataOutput output) {
		return this.channel.channel().writeAndFlush(output, channel.channel().voidPromise()).syncUninterruptibly();
	}
}
