package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import io.netty.buffer.ByteBuf;

public abstract class Packet<T> {
	
	private static final String UUID_REGEX = "/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i";

	protected Class<T> type;
	
	protected Packet(@Nonnull Class<T> classname) {
		// validate
		Validate.notNull(classname, "Class cannot be null");
		// packet names cannot be null
		this.type = classname;
	}
	
	/**
	 * Method to read Packets data from the input ByteBuf
	 * @author Timeout
	 * 
	 * @param input the ByteBuf where you should read your data
	 * @throws IOException if the input is not readable
	 */
	public abstract void decode(ByteBuf input) throws IOException;
	
	/**
	 * Method to write Packets into tne output ByteBuf
	 * @author Timeout
	 * 
	 * @param output the ByteBuf where you should write your data
	 * @throws IOException if the output is not writable
	 */
	public void encode(ByteBuf output) throws IOException {
		// write PacketName into output
		writeString(output, type.getSimpleName());
	}
	
	/**
	 * Writes a String with UTF-8 Charset into the current ByteBuf
	 * @author Timeout
	 * 
	 * @param data the bytebuf where the string should be written in
	 * @param input the string you want to write
	 * @throws IOException if the data is not writable
	 */
	public static void writeString(@Nonnull ByteBuf data, @Nonnull String input) throws IOException {
		// Validate
		Validate.notNull(data, "ByteBuf cannot be null");
		Validate.notNull(input, "Input cannot be null");
		// check if bytebuf is writeable
		if(data.isWritable()) {
			// write int length
			data.writeInt(input.getBytes(StandardCharsets.UTF_8).length);
			// write char sequence
			data.writeCharSequence(input, StandardCharsets.UTF_8);
		} else throw new IOException("ByteBuf is not writable");
	}
	
	/**
	 * Reads a String with UTF-8 Charset of a ByteBuf
	 * @author Timeout
	 * 
	 * @param data the bytebuf where the string is located
	 * @return the string itself
	 * @throws IOException if the bytebuf is not readable
	 */
	@Nonnull
	public static String readString(@Nonnull ByteBuf data) throws IOException {
		// Validate
		Validate.notNull(data, "ByteBuf cannot be null");
		// check if data is readable
		if(data.isReadable()) {
			// read int length first
			int length = data.readInt();
			// read string
			return data.readCharSequence(length, StandardCharsets.UTF_8).toString();
		} else throw new IOException("ByteBuf is not readable");
	}	
	
	/**
	 * Reads a UUID of a ByteBuf
	 * @author Timeout
	 * 
	 * @param data the bytebuf you want to read
	 * @return the uuid or null if the string is not a uuid
	 * @throws IOException if the bytebuf is not readable
	 */
	@Nullable
	public static UUID readUUID(@Nonnull ByteBuf data) throws IOException {
		// get String
		String uuidString = readString(data);
		
		// return uuid if id is valid. Otherwise null
		return uuidString.matches(UUID_REGEX) ? UUID.fromString(uuidString) : null;
	}
}
