/**
 * Copyright 2012 Ronen Hamias, Anton Kharenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicefabric.socketio.session;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicReference;

import io.servicefabric.socketio.TransportType;
import io.servicefabric.socketio.packets.Packet;
import io.servicefabric.socketio.packets.PacketType;
import io.servicefabric.socketio.packets.PacketsFrame;

import io.netty.channel.Channel;

public abstract class AbstractPollingSession extends AbstractSession {

	private final Packet ackPacket = new Packet(PacketType.ACK);
	private final PollingQueue messagesQueue = new PollingQueue();
	private final AtomicReference<Channel> outChannelHolder = new AtomicReference<Channel>();

	public AbstractPollingSession(final Channel channel, final String sessionId, final String origin,
			final ISessionDisconnectHandler disconnectHandler, final TransportType upgradedFromTransportType, final int localPort,
			final SocketAddress remoteAddress) {
		super(channel, sessionId, origin, disconnectHandler, upgradedFromTransportType, localPort, remoteAddress);
	}

	@Override
	public boolean connect(Channel channel) {
		boolean initialConnect = super.connect(channel);
		if (!initialConnect) {
			bindChannel(channel);
		}
		return initialConnect;
	}

	private void bindChannel(final Channel channel) {
		if (getState() == State.DISCONNECTING) {
			disconnect(channel);
		} else {
			flush(channel);
		}
	}

	private void flush(final Channel channel) {
		synchronized (messagesQueue) {
			if (messagesQueue.isEmpty()) {
				outChannelHolder.set(channel);
			} else {
				PacketsFrame packetsFrame = messagesQueue.takeAll();
				sendPacketToChannel(channel, packetsFrame);
			}
		}
	}

	@Override
	public void sendPacket(final Packet packet) {
		if (packet == null) {
			throw new IllegalArgumentException("Packet is null");
		}

		Channel channel = outChannelHolder.getAndSet(null);
		if (channel != null && channel.isActive()) {
			sendPacketToChannel(channel, packet);
		} else {
			synchronized (messagesQueue) {
				messagesQueue.add(packet);
			}
		}
	}

	@Override
	public void disconnect() {
		if (getState() == State.DISCONNECTED) {
			return;
		}
		if (getState() != State.DISCONNECTING) {
			setState(State.DISCONNECTING);

			// Check if there is active polling channel and disconnect 
			// otherwise schedule forced disconnect
			Channel channel = outChannelHolder.getAndSet(null);
			if (channel != null && channel.isActive()) {
				disconnect(channel);
			} else {
				heartbeatScheduler.scheduleDisconnect();
			}
		} else {
			//forced disconnect
			disconnect(null);
		}
	}

	@Override
	public void acceptPacket(final Channel channel, final Packet packet) {
		if (packet.getSequenceNumber() == 0) {
			sendPacketToChannel(channel, ackPacket);
		}
	}

}
