package de.timeout.sudo.bungee.messenger;

import java.util.Locale;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.bungee.Sudo;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Abstract class for handling Plugin-Message-Channels
 * @author Timeout
 *
 */
public abstract class AbstractPluginMessager implements Listener {
	
	private static final Sudo main = Sudo.getInstance();
	
	protected String channel;
	
	public AbstractPluginMessager(String channel) {
		this.channel = String.format("sudo:%s", channel.toLowerCase(Locale.ENGLISH));
		// register channel
		main.getProxy().registerChannel(this.channel);
	}
	
	/**
	 * Sends a Byte-Message to a certain server
	 * @author Timeout
	 * 
	 * @param server the server you want to send the data
	 * @param message the data itself
	 */
	protected void sendPluginMessage(@Nonnull ServerInfo server, byte[] message) {
		// Validate
		Validate.notNull(channel, "Channel cannot be null");
		Validate.notNull(server, "Receiver cannot be null");
		server.sendData(channel, message);
	}

	@EventHandler
	public abstract void onPluginMessageReceived(PluginMessageEvent event);
	
	@EventHandler
	public void test(PluginMessageEvent event) {
		Sudo.log().log(Level.INFO, "TEST in Abstrakter Class");
	}
}
