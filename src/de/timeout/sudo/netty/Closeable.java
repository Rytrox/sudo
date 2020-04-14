package de.timeout.sudo.netty;

/**
 * Interface for object which can close connections
 * @author Timeout
 *
 */
@FunctionalInterface
public interface Closeable {

	/**
	 * Closes a pipeline or channel
	 * @author Timeout
	 *
	 */
	public void close();
}
