package com.ns.greg.socket.interfacies

/**
 * @author gregho
 * @since 2018/8/13
 */
interface SocketConnection {

  fun subscribe(socketConnectionObserver: SocketConnectionObserver)

  fun unsubscribe()

  fun open()

  fun close()

  fun write(packet: ByteArray)
}