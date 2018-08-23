package com.ns.greg.socket

/**
 * @author gregho
 * @since 2018/6/11
 */
enum class ConnectionState(private val state: Int) {

  FAILURE(-1),
  CONNECTED(0),
  DISCONNECTED(1),
  CLOSED(2);

  override fun toString(): String {
    return "STATE: ${when (state) {
      -1 -> "FAILURE"
      0 -> "CONNECTED"
      1 -> "DISCONNECTED"
      2 -> "CLOSE"
      else -> "UNKNOWN"
    }}"
  }
}