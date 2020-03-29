package de.timeout.sudo.bukkit.messenger;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataOutput;
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
	protected boolean cancel;

	/**
	 * Creates a new PluginMessageFuture and sends a request to BungeeCord
	 * @author Timeout
	 *
	 * @param out the request which will be sent to BungeeCord. Cannot be null
	 * @throws IllegalArgumentException if the request is null
	 */
	public PluginMessageFuture(@Nonnull ByteArrayDataOutput out) {
		// Validate
		Validate.notNull(out, "Request cannot be null");
		// send request to bungeecord over loadUser
		Bukkit.getServer().sendPluginMessage(main, "sudo", out.toByteArray());
		registerIncomingPluginChannel();
	}
	
	/**
	 * registers the new incoming plugin message listener
	 * @author Timeout
	 *
	 */
	private void registerIncomingPluginChannel() {
		// register this BungeeCord message listener
		Bukkit.getMessenger().registerIncomingPluginChannel(main, "sudo", this);
	}
	
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
			Bukkit.getMessenger().unregisterIncomingPluginChannel(main, "sudo", this);
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
				} else {
					// throw interrupted exception
					throw new InterruptedException();
				}
			} else throw new TimeoutException();
		}
		
		return data;
	}

}
