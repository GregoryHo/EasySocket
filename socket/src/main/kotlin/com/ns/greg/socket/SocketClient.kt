package com.ns.greg.socket

import com.ns.greg.socket.interfacies.SocketDevice
import com.ns.greg.socket.internal.SocketDeviceImp
import kotlin.LazyThreadSafetyMode.NONE

/**
 * @author gregho
 * @since 2018/8/10
 */
class SocketClient(
  ip: String,
  port: Int
) {

  private val device: SocketDeviceImp by lazy(NONE) {
    SocketDeviceImp(ip, port)
  }

  fun getDevice(): SocketDevice {
    return device
  }
}