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
package io.servicefabric.socketio.packets;

import java.util.LinkedList;
import java.util.List;


public class PacketsFrame extends AbstractPacket {

	private List<Packet> packets = new LinkedList<Packet>();

	public PacketsFrame() {
	}

	public List<Packet> getPackets() {
		return packets;
	}
	
	@Override
	public String toString() {
		return "PacketsFrame [" + super.toString() + ", packets=" + packets + "]";
	}
	
}
