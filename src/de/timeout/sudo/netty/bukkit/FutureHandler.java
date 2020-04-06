package de.timeout.sudo.netty.bukkit;

import java.nio.BufferOverflowException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.Validate;

import com.google.gson.JsonObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class FutureHandler extends SimpleChannelInboundHandler<JsonObject> {
	
	private ChannelHandlerContext ctx;
	private BlockingQueue<Promise<JsonObject>> queue = new ArrayBlockingQueue<>(16);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		this.ctx = ctx;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		synchronized (this) {
			// cancel every promise
			Promise<JsonObject> promise;
			while((promise = queue.poll()) != null)
				promise.setFailure(new TimeoutException("Connection timed out"));
			// diable queue
			queue = null;
		}
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, JsonObject element) throws Exception {
		synchronized (this) {
			// check if channel runs
			if(queue != null) {
				// set success
				queue.poll().setSuccess(element);
			}
		}
	}
	
	public synchronized Future<JsonObject> sendRequest(JsonObject message) {
		Validate.notNull(ctx, "Unable to send request. Channel is inactive");
		return this.sendRequest(message, ctx.executor().newPromise());
	}
	
	public synchronized void sendPacket(JsonObject message) {
		// send packet if ctx is not null
		if(ctx != null) ctx.writeAndFlush(message);
	}

	private synchronized Future<JsonObject> sendRequest(JsonObject message, Promise<JsonObject> promise) {
		// cancel if channel is inactive
		if(queue != null) {
			// check if queue offers
			if(queue.offer(promise)) {
				// send to server
				ctx.writeAndFlush(message);
			} else promise.setFailure(new BufferOverflowException());
		} else promise.setFailure(new IllegalStateException("Channel is inactive"));
		
		// return promise
		return promise;
	}
}
