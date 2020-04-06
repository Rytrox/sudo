package de.timeout.sudo.netty;

import java.nio.charset.StandardCharsets;

import de.timeout.sudo.netty.packets.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketToByteEncoder extends MessageToByteEncoder<Packet<?>> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf output) throws Exception {
		System.out.println("Encode Packet " + packet.getClass().getName());
		// write output to packet
		packet.encode(output);
		System.out.println(output.toString(StandardCharsets.UTF_8));
	}

}
