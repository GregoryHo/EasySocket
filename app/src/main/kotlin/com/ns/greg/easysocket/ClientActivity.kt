package com.ns.greg.easysocket

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ns.greg.socket.ConnectionState
import com.ns.greg.socket.ConnectionState.CLOSED
import com.ns.greg.socket.ConnectionState.CONNECTED
import com.ns.greg.socket.ConnectionState.DISCONNECTED
import com.ns.greg.socket.SocketClient
import com.ns.greg.socket.interfacies.SocketConnection
import com.ns.greg.socket.interfacies.SocketConnectionObserver
import com.ns.greg.socket.interfacies.SocketDevice

/**
 * @author gregho
 * @since 2018/8/23
 */
class ClientActivity : AppCompatActivity() {

  private lateinit var client: SocketClient
  private lateinit var connection: SocketConnection

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    client = SocketClient("192.168.1.15" /* server ip */, 5050 /* server port */)
    // create connection
    connection = client.getDevice()
        .createConnection()
    // subscribe the connection between server
    connection.subscribe(object : SocketConnectionObserver {
      override fun onConnectionStateChanged(state: ConnectionState) {
        println("STATE: $state")
        when (state) {
          CONNECTED -> connection.write("Hi, i am client.".toByteArray())
          DISCONNECTED -> println("disconnected")
          CLOSED -> println("closed")
          else -> println("unknown")
        }
      }

      override fun onRead(packet: ByteArray) {
        println("RECEIVED: ${String(packet)}")
      }
    })
    // open the connection (connect to the server)
    connection.open()
  }

  override fun onDestroy() {
    super.onDestroy()
    // close the connection, when you invoked this function
    // you can't re-open the connection again,
    // must create new connection and open new one
    connection.close()
    connection.unsubscribe()
  }
}