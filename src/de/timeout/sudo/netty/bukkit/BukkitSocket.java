package de.timeout.sudo.netty.bukkit;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.netty.ByteToPacketDecoder;
import de.timeout.sudo.netty.Closeable;
import de.timeout.sudo.netty.PacketToByteEncoder;
import de.timeout.sudo.netty.packets.Packet;
import de.timeout.sudo.netty.packets.PacketProxyInAuthorize;

import net.jafama.FastMath;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class BukkitSocket implements Runnable, Closeable {
	
	private static final Sudo main = Sudo.getInstance();
	private static final boolean EPOLL = Epoll.isAvailable();
	private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
	
	private final int maxAttempts = main.getConfig().getInt("bungeecord.reconnect", 100);
	
	private String host;
	private int port;
	
	private Bootstrap boot;
	private ChannelFuture channel;
	private ReconnectOnCloseListener reconnectListener;
		
	public BukkitSocket(@Nonnull String host, @Nonnegative int port) {
		// Validate
		Validate.notNull(host, "BungeeCord's address cannot be null");
		
		this.host = host;
		this.port = FastMath.abs(port);
		
		// create bootstrap for netty client
		createBootstrap();
	}
	
	/**
	 * Creates a Bootstrap for the SocketClient
	 * @author Timeout
	 * 
	 * @param group the event loop group
	 * @return
	 */
	public void createBootstrap() {
		this.reconnectListener = new ReconnectOnCloseListener(this);
		boot = new Bootstrap();
		// create loop group
		EventLoopGroup group = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
		// check for OS
		Sudo.log().log(Level.INFO, String.format("&9%s &adetected&7, using &3%s-ServerSockets", (EPOLL ? "Linux" : "Windows"), (EPOLL ? "Epoll" : "NIO")));
		
		boot.group(group);
		boot.channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class);
		boot.option(ChannelOption.SO_KEEPALIVE, true);
		// register Handler
		boot.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel channel) throws Exception {				
				// register decoder and encoder
				channel.pipeline().addLast("decoder", new ByteToPacketDecoder());
				channel.pipeline().addLast("encoder", new PacketToByteEncoder());
				
				// define handler
				channel.pipeline().addLast("authorize", new AuthorizeHandler());
				channel.pipeline().addLast("initialization", new GroupInitializationHandler());
			}
			
		});
		// log start of client
		Sudo.log().log(Level.INFO, "&7Creating &5Connection &7to &2BungeeCord");
	}
	
	@Override
	public void run() {
		// disconnect channel connection
		channel = null;
		// set current attempts to 0
		int currentAttempt = 0;
		// connect while attempts left
		while(maxAttempts < 0 || currentAttempt < maxAttempts) {
			// log increase current attempts
			Sudo.log().log(Level.INFO, String.format("&aStart connecting (Attempt %d of %d)...", ++currentAttempt, maxAttempts));
			// connect to bungeecord
			channel = boot.connect(host, port);
			try {
				// try to connect
				channel.await(2, TimeUnit.SECONDS);
				// wait until next attempt else break
				if(!channel.isSuccess()) Thread.sleep(2000);
				else break;
			} catch (InterruptedException e) {
				Sudo.log().log(Level.WARNING, "&4Unable to sleep task for 2 Seconds", e);
				Thread.currentThread().interrupt();
			}

		}
		// hold if channel is loaded
		if(channel == null || !channel.channel().isOpen()) {
			// log
			Sudo.log().log(Level.SEVERE, String.format("&4Unable to connect to BungeeCord. Giving up after %d attempts. Disable plugin now!", maxAttempts));
			// disable plugin now
			Bukkit.getScheduler().runTask(main, () -> Bukkit.getPluginManager().disablePlugin(main));
		} else {
			channel = channel.channel().closeFuture();
			// authorize
			if(authorize()) {
				// add reconnect listener and sync to server
				channel.addListener(new ChannelFutureListener() {
						
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						// if future lost connection
						if(!future.channel().isOpen() && !future.channel().eventLoop().isShuttingDown()) {
							// log
							Sudo.log().log(Level.WARNING, "&cLost connection to Bungeecord. Start reconnecting");
							// sleep for 5 seconds
							Thread.sleep(5 * 1000L);
							// create new bootstrap
							createBootstrap();
							// reconnect
							run();
						}
					}
						
				}).syncUninterruptibly();
			}
		}
	}
	
	public void connect() {
		// connect to bungeecord
		this.channel = boot.connect(host, port);
		// add reconnect listener
		channel.addListener(future -> {
			// reconnect if channel is not reconnected
			if(channel.isSuccess()) {
				// 
			} else reconnectListener.scheduleReconnect();
		});
	}
	
	public boolean isConnected() {
		return this.channel != null && this.channel.isSuccess();
	}
	
	private boolean authorize() {
		// get UUID
		UUID uuid = getBungeeCordID();
		// check if settings could be read
		if(uuid != null) {
			sendPacket(new PacketProxyInAuthorize(uuid, main.getServer().getPort()));
			// connection succeed. log
			Sudo.log().log(Level.INFO, "&aConnected to &2BungeeCord! &7Sending &5Authentification");
			// return true for success
			return true;
		} else close();
		
		return false;
	}
	
	/**
	 * Returns the uuid of the config or null if the UUID is incorrect
	 * @author Timeout
	 * 
	 * @return the uuid from config or null if the config cannot be converted
	 */
	@Nullable
	private UUID getBungeeCordID() {
		// get String
		String idString = main.getConfig().getString("bungeecord.uuid", "NOT_FOUND");
		System.out.println(idString);
		// check if uuid is valid
		if(idString.matches(UUID_REGEX)) {
			Sudo.log().log(Level.INFO, "Passt");
			// return uuid
			return UUID.fromString(idString);
		} else Sudo.log().log(Level.WARNING, "&cUnable to use uuid from config. Invalid format. Please check your config.yml");
		return null;
	}
		
	public void sendPacket(Packet<?> packet) {
		channel.channel().writeAndFlush(packet, channel.channel().voidPromise());
	}
	
	@Override
	public void close() {
		// close if connection is not already closed
		if(this.channel != null && this.channel.channel().isOpen()) {
			this.channel.channel().close();
			this.channel.channel().eventLoop().shutdownGracefully();
			// shutdown plugin
			Bukkit.getScheduler().runTask(main, () -> Bukkit.getPluginManager().disablePlugin(main));
		}
	}
}
