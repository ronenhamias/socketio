[![Build Status](https://travis-ci.org/servicefabric/socketio.svg?branch=add-travis)](https://travis-ci.org/servicefabric/socketio)

Socket.IO Java Server
=======================

Performance: 
Tested on VM: OS: CentOS, 4vCPU, 2GB RAM,Java 7

Client Sessions:

can support:   
- 10,000 long-polling sessions on single node
- 50,000 WebSocket    sessions on single node

TPS:
- 4,000 requests per second per single channel.
- 80,000 requests per second total. 

SocketIo4Netty is a simple [Socket.IO](http://socket.io) Java server implementation based on 
[Netty](http://netty.io) server framework. Supports 0.7+ up to 0.9.16 versions of 
Socket.IO-client.

Supported transport protocols:
* WebSocket
* Flash Socket
* XHR-Polling
* JSONP-Polling

How to use
-----------------------

``` java
	SocketIOServer socketIoServer = new SocketIOServer();
	socketIoServer.setPort(5000);
	socketIoServer.setListener(new SocketIOAdapter() {
		public void onMessage(ISession session, ByteBuf message) {
			System.out.println("Received: " + message.toString(CharsetUtil.UTF_8));
			message.release();
		}
	});
	socketIoServer.start();
```

For more examples, see [SocketIo4j Examples](https://github.com/ServiceFabric/socketio-examples). 

Maven
---------------------- 

``` maven
<dependency>
	<groupId>io.servicefabric</groupId>
	<artifactId>socketio</artifactId>
	<version>2.0.0</version>
</dependency>
```

Maven dependency for versions up to 1.1.2:
 
``` maven
<dependency>
	<groupId>com.github.socketIo4Netty</groupId>
	<artifactId>socketIo4Netty</artifactId>
	<version>1.1.2</version>
</dependency>
``` 
