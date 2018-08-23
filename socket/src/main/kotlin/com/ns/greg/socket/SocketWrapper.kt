package com.ns.greg.socket

import com.ns.greg.socket.internal.SocketHook
import com.ns.greg.library.fasthook.BaseRunnable
import com.ns.greg.library.fasthook.functions.BaseRun
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.LazyThreadSafetyMode.NONE

/**
 * @author gregho
 * @since 2018/8/13
 */
class SocketWrapper(private val socket: Socket) {

  companion object {

    const val STREAM_SIZE = 4096
    const val BUFFER_SIZE = 1024
  }

  private val baos = ByteArrayOutputStream(STREAM_SIZE)
  private val buffer = ByteArray(BUFFER_SIZE)
  private val bis: BufferedInputStream by lazy(NONE) {
    BufferedInputStream(socket.getInputStream())
  }
  private val bos: BufferedOutputStream by lazy(NONE) {
    BufferedOutputStream(socket.getOutputStream())
  }
  private val writer: Writer by lazy(NONE) {
    Writer(bos)
  }

  init {
    SocketHook.instance.addTask(writer)
        .start()
  }

  fun read(): ByteArray? {
    var tmp: ByteArray? = null
    var read = -1
    try {
      if (bis.read(buffer).also { read = it } != -1) {
        baos.write(buffer, 0, read)
        baos.flush()
        tmp = baos.toByteArray()
        baos.reset()
      }
    } catch (e: IOException) {
    }

    return tmp
  }

  fun write(packet: ByteArray) {
    writer.payload = packet
  }

  @Throws(IOException::class)
  fun close() {
    writer.enable.set(false)
    socket.use { _ ->
      bos.use { _ ->
        bis.close()
      }
    }
  }

  internal fun getSocket(): Socket {
    return socket
  }

  private class Writer(private val bos: BufferedOutputStream) : BaseRunnable<BaseRun>() {

    internal val enable = AtomicBoolean(true)
    internal var payload: ByteArray? = null

    override fun runImp(): BaseRun? {
      while (enable.get()) {
        if (payload != null) {
          with(bos) {
            try {
              bos.write(payload)
              bos.flush()
              payload = null
            } catch (e: IOException) {
            }
          }
        }
      }

      return null
    }
  }
}

