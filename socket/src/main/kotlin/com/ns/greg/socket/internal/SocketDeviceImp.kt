package com.ns.greg.socket.internal

import com.ns.greg.socket.ConnectionState
import com.ns.greg.socket.interfacies.SocketConnection
import com.ns.greg.socket.interfacies.SocketDevice

/**
 * @author gregho
 * @since 2018/8/13
 */
internal class SocketDeviceImp(
  private val ip: String,
  private val port: Int
) : SocketDevice {

  private lateinit var connection: SocketConnectionImp

  override fun createConnection(): SocketConnection {
    connection = SocketConnectionImp(ip, port)
    return connection
  }

  override fun getConnectionState(): ConnectionState {
    return connection.getConnectionState()
  }
}