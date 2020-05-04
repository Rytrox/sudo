package de.timeout.sudo.netty;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.timeout.libs.Reflections;
import de.timeout.sudo.netty.packets.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ByteToPacketDecoder extends ByteToMessageDecoder {
		
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> messages) throws Exception {
		System.out.println("Decoder decode packet...");
		// read packet
		Packet<?> packet = getPacketByName(Packet.readString(input));
		// check if packet could be read
		if(packet != null) { 
			System.out.println("Received " + packet.getClass().getSimpleName());
			// decode packet
			packet.decode(input);
			System.out.println("Decoded!");
			// add packet to received messages
			messages.add(packet);
		}
	}

	/**
	 * Returns an instance of the Packet or null if the Packet cannot be found
	 * @author Timeout
	 * 
	 * @param name the name of the name
	 * @return the packet or null
	 */
	private static Packet<?> getPacketByName(String name) {
		// return null if the name is null
		if(name != null && !name.isEmpty()) {
			try {
				// get Class
				Class<?> packetClass = Reflections.getClass(
						String.format("de.timeout.sudo.netty.packets.%s", name));
				// load class
				return (Packet<?>) packetClass.getConstructor().newInstance();
			} catch (InstantiationException e) {
				Logger.getGlobal().log(Level.WARNING, String.format("Cannot create instance of class %s", name), e);
			} catch (IllegalAccessException e) {
				Logger.getGlobal().log(Level.WARNING, String.format("Unable to get access of class %s", name), e);
			} catch (InvocationTargetException e) {
				Logger.getGlobal().log(Level.WARNING, String.format("Unable to create a target for class %s", name), e);
			} catch (NoSuchMethodException e) {
				Logger.getGlobal().log(Level.WARNING, String.format("Class %s has no required default constructor", name), e);
			} catch (SecurityException e) {
				Logger.getGlobal().log(Level.WARNING, String.format("Unhandled security error while accessing class %s", name), e);
			}
		}
		return null;
	}
}
