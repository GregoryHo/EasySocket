package com.ns.greg.socket.interfacies

import java.net.Socket

/**
 * @author gregho
 * @since 2018/8/10
 */
interface SocketServerObserver {

  fun onConnected(client: Socket)

  fun onDisconnected(client: Socket)

  fun onRead(
    client: Socket,
    packet: ByteArray
  )
}