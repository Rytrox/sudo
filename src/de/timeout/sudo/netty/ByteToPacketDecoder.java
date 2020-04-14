package de.timeout.sudo.netty;

import java.util.List;

import de.timeout.sudo.netty.packets.Packet;
import de.timeout.sudo.netty.packets.PacketProxyInAuthorize;
import de.timeout.sudo.netty.packets.PacketProxyInLogin;
import de.timeout.sudo.netty.packets.PacketRemoteInAuthorize;
import de.timeout.sudo.netty.packets.PacketRemoteInGroupInheritances;
import de.timeout.sudo.netty.packets.PacketRemoteInInitializeGroup;
import de.timeout.sudo.netty.packets.PacketRemoteInLoadUser;

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
		switch(name) {
		case "PacketProxyInAuthorize":
			return new PacketProxyInAuthorize();
		case "PacketProxyInLogin":
			return new PacketProxyInLogin();
		case "PacketRemoteInAuthorize":
			return new PacketRemoteInAuthorize();
		case "PacketRemoteInGroupInheritances":
			return new PacketRemoteInGroupInheritances();
		case "PacketRemoteInInitializeGroup":
			return new PacketRemoteInInitializeGroup();
		case "PacketRemoteInLoadUser":
			return new PacketRemoteInLoadUser();
		default: 
			return null;
		}
	}
}
