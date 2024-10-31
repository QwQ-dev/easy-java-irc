## easy-java-irc

A simple Netty-based IRC Library.

## Overview

A simple Netty-based Java IRC Library.

This is a simple IRC Library based on Netty, which allows developers to quickly create an IRC system. You only need to
create objects and start them.

## Developers

- [**QwQ-dev / 2000000**](https://github.com/InkerBot)
- [**InkerBot**](https://github.com/InkerBot)

## Preparation Before Starting

You need to read the source code and find the TODO segments we've marked. These TODOs are very simple and relate to
exception handling and information reception.

Once you complete these, simply copy all the server source code into your project to finish setting up the server.

The client can be set up in the same way.

## Starting the Service

For the server, you can create the service
using [ServiceData](ejic-server/src/main/java/me/qwqdev/ejic/server/data/ServiceData.java)
and [NettyServer](ejic-server/src/main/java/me/qwqdev/ejic/server/NettyServer.java).

```java
package me.qwqdev.ejic.server;

import me.qwqdev.ejic.server.data.ServiceData;
import me.qwqdev.ejic.server.NettyServer;

/**
 * @author : qwq-dev
 * @since : 2024-10-31 16:48
 */
public class Main {
    public static void createMsgServer() {
        // 127.0.0.1:24
        ServiceData serviceData = new ServiceData("127.0.0.1", 24);
        new NettyServer(serviceData).start();
    }
}
```

The service will start in the corresponding thread. Additionally, if you want to use a custom ChannelGroup, we also
allow this:

```java
public ServiceData(String address, int port, ChannelGroup channelGroup) {
    // Implementation omitted 
}
```

For the client, you can create the service
using [ClientHandler](ejic-client/src/main/java/me/qwqdev/ejic/client/handler/ClientHandler.java)
and [NettyClient](ejic-client/src/main/java/me/qwqdev/ejic/client/NettyClient.java):

```java
package me.qwqdev.ejic.client;

/**
 * @author : qwq-dev
 * @since : 2024-10-31 17:09
 */
public class Main {
    public static void main(String[] args) {
        // 127.0.0.1:24
        NettyClient nettyClient = new NettyClient("127.0.0.1", 24, 5000, 0, true);
        nettyClient.start();
    }
}
```

```java
 public NettyClient(String host, int port, int reconnectMilliseconds, int initialConnectionMillisecondsDelay, boolean asyncConnection) {
    // Implementation omitted
}
```

This code means we will connect to the service at 127.0.0.1:24. If the connection drops, it will attempt to reconnect
every 5000 milliseconds. The first connection delay when starting the client service is 0ms, and the connection is
asynchronous. Asynchronous connection means that after calling the start method, the thread will not block.