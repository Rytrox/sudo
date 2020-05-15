package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import io.netty.buffer.ByteBuf;

public abstract class Packet<T> {

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
}
