package com.ns.greg.socket

import com.ns.greg.socket.interfacies.SocketServerObserver
import com.ns.greg.socket.internal.SocketHook
import com.ns.greg.library.fasthook.BaseRunnable
import com.ns.greg.library.fasthook.BaseThreadTask
import com.ns.greg.library.fasthook.functions.BaseRun
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

/**
 * @author gregho
 * @since 2018/8/10
 */
class SocketServer(
  private val port: Int,
  private val multipleClient: Boolean
) {

  internal companion object {

    const val DISCONNECTED_CODE = 0x31.toByte()
  }

  private lateinit var serverSocket: ServerSocket
  private lateinit var connectionTask: BaseThreadTask
  private val clients = ArrayList<Socket>()
  private val socketWrapperMaps = HashMap<Socket, SocketWrapper>()
  private var observer: SocketServerObserver? = null

  fun subscribe(bleServerObserver: SocketServerObserver) {
    this.observer = bleServerObserver
  }

  fun unsubscribe() {
    this.observer = null
  }

  fun open() {
    serverSocket = ServerSocket(port)
    println("Server(address: ${InternetUtils.getIPAddress(true)}) started.")
    connectionTask = SocketHook.instance.addTask(object : BaseRunnable<BaseRun>() {
      override fun runImp(): BaseRun? {
        while (!serverSocket.isClosed) {
          var accepted: Socket? = null
          try {
            if (multipleClient || clients.isEmpty()) {
              println("waiting client to connected...")
              accepted = serverSocket.accept()
                  ?.also {
                    addClient(it)
                    acceptedClient(it)
                  }
            }
          } catch (e: IOException) {
            println("accepted client occurs IO exception.")
            accepted?.run {
              removeClient(this)
            }
          }
        }

        clearClients()
        return null
      }
    })
        .start()
  }

  @Throws(IOException::class) fun close() {
    serverSocket.close()
  }

  fun write(
    client: Socket,
    packet: ByteArray
  ) {
    socketWrapperMaps[client]?.run {
      write(packet)
    }
  }

  private fun acceptedClient(accept: Socket) {
    SocketHook.instance.addTask(object : BaseRunnable<BaseRun>() {
      override fun runImp(): BaseRun? {
        socketWrapperMaps[accept]?.run {
          with(getSocket()) {
            connection@ while (isConnected) {
              val read = read()
              if (read != null) {
                if (read.size == 1 && read[0] == DISCONNECTED_CODE) {
                  break@connection
                } else {
                  observer?.onRead(this, read)
                }
              }
            }

            removeClient(getSocket())
          }
        }

        return null
      }
    })
        .start()
  }

  private fun addClient(client: Socket) {
    synchronized(clients) {
      var isAdded = false
      val iterator = clients.iterator()
      while (iterator.hasNext()) {
        if (iterator.next() == client) {
          isAdded = true
          break
        }
      }

      if (!isAdded) {
        println("add client = [${client}]")
        clients.add(client)
        socketWrapperMaps[client] = SocketWrapper(client)
        observer?.onConnected(client)
      }
    }
  }

  private fun removeClient(client: Socket) {
    synchronized(clients) {
      val iterator = clients.iterator()
      while (iterator.hasNext()) {
        if (iterator.next() == client) {
          println("remove client = [${client}]")
          observer?.onDisconnected(client)
          socketWrapperMaps[client]?.close()
          socketWrapperMaps.remove(client)
          iterator.remove()
          break
        }
      }
    }
  }

  private fun clearClients() {
    synchronized(clients) {
      val iterator = clients.iterator()
      while (iterator.hasNext()) {
        val client = iterator.next()
        observer?.onDisconnected(client)
        println("remove client = [${client}]")
        socketWrapperMaps[client]?.close()
        socketWrapperMaps.remove(client)
        iterator.remove()
      }
    }
  }
}
