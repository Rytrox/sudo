package de.timeout.sudo.netty.bukkit;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import de.timeout.sudo.bukkit.Sudo;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ReconnectOnCloseListener implements ChannelFutureListener {
	
	private static final Sudo main = Sudo.getInstance();
	
	private final int maxAttempts = main.getConfig().getInt("bungeecord.reconnect", 10);
	
	private BukkitSocket client;
	private boolean disconnectRequested = false;
	private int currentTry = 0;
	
	public ReconnectOnCloseListener(BukkitSocket client) {
		
		this.client = client;
		
	}

	@Override
	public void operationComplete(ChannelFuture arg0) throws Exception {
		// disconnect from channel
		arg0.channel().disconnect();
		// reconnect
		scheduleReconnect();
	}

	public void scheduleReconnect() {
		// stop if plugin shuts down
		if(!disconnectRequested) {
			// log
			Sudo.log().log(Level.WARNING, "&cLost connection to Bungeecord. Start reconnecting");
			// run task 
			new BukkitRunnable() {
				
				@Override
				public void run() {
					// stop task if disconnect is requested
					if(!disconnectRequested) {
						// try to connect
						if(++currentTry <= maxAttempts) {
							// try to connect
							client.
						} else {
							// log
							Sudo.log().log(Level.SEVERE, String.format("&4Unable to connect to BungeeCord. Giving up after %d attempts. Disable plugin now!", maxAttempts));
							// disable plugin now
							Bukkit.getScheduler().runTask(main, () -> Bukkit.getPluginManager().disablePlugin(main));
						}
					} else cancel();
				}
			}.runTaskTimerAsynchronously(main, 20L * 2, 20L * 2);
		}
	}
}
