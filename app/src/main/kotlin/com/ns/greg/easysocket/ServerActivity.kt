package com.ns.greg.easysocket

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ns.greg.socket.SocketServer
import com.ns.greg.socket.interfacies.SocketServerObserver
import java.net.Socket

/**
 * @author gregho
 * @since 2018/8/23
 */
class ServerActivity : AppCompatActivity() {

  private lateinit var server: SocketServer

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // create the server with the custom port
    // you can choose the mode to support multiple client or just single
    server = SocketServer(5050 /* server port */, true)
    server.subscribe(object : SocketServerObserver {
      override fun onConnected(client: Socket) {
        println("$client connected")
      }

      override fun onDisconnected(client: Socket) {
        println("$client disconnected")
      }

      override fun onRead(
        client: Socket,
        packet: ByteArray
      ) {
        println("$client send ${String(packet)}")
        server.write(client, "Hi there, you are accepted.".toByteArray())
      }
    })
    // host the server
    server.open()
  }

  override fun onDestroy() {
    super.onDestroy()
    // close the server
    server.close()
    server.unsubscribe()
  }
}