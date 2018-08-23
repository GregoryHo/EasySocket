package com.ns.greg.socket.internal

import com.ns.greg.socket.ConnectionState
import com.ns.greg.socket.ConnectionState.CLOSED
import com.ns.greg.socket.ConnectionState.CONNECTED
import com.ns.greg.socket.ConnectionState.DISCONNECTED
import com.ns.greg.socket.ConnectionState.FAILURE
import com.ns.greg.socket.SocketServer
import com.ns.greg.socket.SocketWrapper
import com.ns.greg.socket.interfacies.SocketConnection
import com.ns.greg.socket.interfacies.SocketConnectionObserver
import com.ns.greg.library.fasthook.BaseRunnable
import com.ns.greg.library.fasthook.BaseThreadTask
import com.ns.greg.library.fasthook.functions.BaseRun
import java.net.Socket

/**
 * @author gregho
 * @since 2018/8/13
 */
internal class SocketConnectionImp(
  private val ip: String,
  private val port: Int
) : SocketConnection {

  private lateinit var socketWrapper: SocketWrapper
  private lateinit var connectionTask: BaseThreadTask
  @Volatile private var state = DISCONNECTED

  private var observer: SocketConnectionObserver? = null

  override fun subscribe(socketConnectionObserver: SocketConnectionObserver) {
    this.observer = socketConnectionObserver
  }

  override fun unsubscribe() {
    this.observer = null
  }

  override fun open() {
    when (state) {
      FAILURE, DISCONNECTED -> {
        connectionTask = SocketHook.instance.addTask(createConnection())
            .start()
      }
      else -> {
        // nothing to do
      }
    }

  }

  override fun close() {
    // send message to server, request close by server
    write(byteArrayOf(SocketServer.DISCONNECTED_CODE))
    setConnectionState(CLOSED)
  }

  override fun write(packet: ByteArray) {
    socketWrapper.write(packet)
  }

  private fun createConnection(): BaseRunnable<BaseRun> {
    return object : BaseRunnable<BaseRun>() {
      override fun runImp(): BaseRun? {
        try {
          socketWrapper = SocketWrapper(Socket(ip, port))
          with(socketWrapper) {
            while (getSocket().isConnected) {
              setConnectionState(CONNECTED)
              read()?.run {
                observer?.onRead(this)
              }
            }
          }
        } finally {
          setConnectionState(DISCONNECTED)
        }

        return null
      }
    }
  }

  fun setConnectionState(state: ConnectionState) {
    synchronized(this) {
      if (this.state != CLOSED && this.state != state) {
        this.state = state
        observer?.onConnectionStateChanged(state)
      }
    }
  }

  fun getConnectionState(): ConnectionState {
    synchronized(this) {
      return state
    }
  }
}