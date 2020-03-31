package de.timeout.sudo.bukkit.messenger;

import java.util.Locale;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;

import de.timeout.sudo.bukkit.Sudo;

/**
 * Abstract Future which requests data from BungeeCord
 * @author Timeout
 *
 */
public abstract class PluginMessageFuture implements Future<JsonObject>, PluginMessageListener {
	
	private static final Sudo main = Sudo.getInstance();
	
	protected JsonObject data;
	protected final String channel;
	protected boolean cancel;

	/**
	 * Creates a new PluginMessageFuture and sends a request to BungeeCord
	 * @author Timeout
	 *
	 * @param out the request which will be sent to BungeeCord. Cannot be null
	 * 
	 * @throws IllegalArgumentException if the request is null
	 */
	public PluginMessageFuture(@Nonnull String channel, @Nonnull ByteArrayDataOutput out) {
		// Validate
		Validate.notNull(out, "Request cannot be null");
		Validate.notNull(channel, "Channel cannot be null");
		// this.channel = String.format("sudo:%s", channel.toLowerCase(Locale.ENGLISH));
		this.channel = channel;
		registerPluginChannels();
		// send request to bungeecord over loadUser
		System.out.println("Nachricht gesendet...");
		Bukkit.getServer().sendPluginMessage(main, channel, out.toByteArray());

	}
	
	/**
	 * registers the new incoming plugin message listener
	 * @author Timeout
	 *
	 */
	private void registerPluginChannels() {
		// register outgoing channel
		Bukkit.getMessenger().registerOutgoingPluginChannel(main, this.channel);
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		// check if channel is correct
		if(channel.equals(this.channel)) {
			// get Input
			this.data = readData(ByteStreams.newDataInput(message));
		}
	}
	
	/**
	 * Read and return the json-object.
	 * This method is called, when a message receives through this channel
	 * @author Timeout
	 * 
	 * @param input the input of the data unread
	 * @return the jsonobject which you read in the channel
	 */
	protected abstract JsonObject readData(ByteArrayDataInput input);
	
	/**
	* Returns if the request is done and the data is received
	* @author Timeout
	* 
	* @return if the data is received
	*/
	@Override
	public boolean isDone() {
		return data != null;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// only cancel if task is not done
		if(!isDone()) {
			// interrupt here
			cancel = true;
			// unregister Listener
			Bukkit.getMessenger().unregisterIncomingPluginChannel(main, this.channel, this);
		}
		// return cancel
		return cancel;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}
	
	@Override
	public JsonObject get() throws InterruptedException {
		// wait until it is done
		while(!isDone()) {
			// interrupt thread if future is canceled
			if(!cancel) {
				// wait for the next tick
				Thread.sleep(1000 / 20);
			} else {
				// throw interruptedException
				throw new InterruptedException();
			}
		}
		// return data for success
		return data;
	}
	
	@Override
	public JsonObject get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
		// get time
		final long time = unit.toMillis(timeout);
		// get current time
		long current = 0;
		// wait until it's done
		while(!isDone()) {
			// only continue if the time is not reached
			if(current < time) {
				// interrupt if it's canceled
				if(!cancel) {
					// wait for next tick
					Thread.sleep(1000 / 20);
					// reallocate current
					current += 1000 / 20;
				} else {
					// throw interrupted exception
					throw new InterruptedException("Thread interrupted while waiting for data from BungeeCord");
				}
			} else throw new TimeoutException("Connection timed out...");
		}
		
		return data;
	}

}
