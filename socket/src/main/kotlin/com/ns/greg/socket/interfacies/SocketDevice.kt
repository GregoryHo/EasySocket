package com.ns.greg.socket.interfacies

import com.ns.greg.socket.ConnectionState

/**
 * @author gregho
 * @since 2018/8/13
 */
interface SocketDevice {

  fun createConnection(): SocketConnection

  fun getConnectionState(): ConnectionState
}