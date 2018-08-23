package com.ns.greg.socket;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * @author gregho
 * @since 2018/8/10
 */
public class InternetUtils {

  /**
   * Returns MAC address of the given interface name.
   *
   * @param interfaceName eth0, wlan0 or NULL=use first interface
   * @return mac address or empty string
   */
  public static String getMacAddress(String interfaceName) {
    try {
      List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
      for (NetworkInterface intf : interfaces) {
        if (interfaceName != null) {
          if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
        }
        byte[] mac = intf.getHardwareAddress();
        if (mac == null) return "";
        StringBuilder buf = new StringBuilder();
        for (byte aMac : mac) buf.append(String.format("%02X:", aMac));
        if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
        return buf.toString();
      }
    } catch (Exception ignored) {
    } // for now eat exceptions

    return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
  }

  /**
   * Returns IP address from first non-localhost interface
   *
   * @param ipV4 true=return ipv4, false=return ipv6
   * @return address or empty string
   */
  public static String getIPAddress(boolean ipV4) {
    try {
      List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
      for (NetworkInterface networkInterface : networkInterfaces) {
        List<InetAddress> addresses = Collections.list(networkInterface.getInetAddresses());
        for (InetAddress address : addresses) {
          if (!address.isLoopbackAddress()) {
            String hostAddress = address.getHostAddress();
            boolean isIPv4 = hostAddress.indexOf(':') < 0;
            if (ipV4) {
              if (isIPv4) {
                return hostAddress;
              }
            } else {
              if (!isIPv4) {
                int delim = hostAddress.indexOf('%'); // drop ip6 zone suffix
                return delim < 0 ? hostAddress.toUpperCase() : hostAddress.substring(0, delim).toUpperCase();
              }
            }
          }
        }
      }
    } catch (Exception ignored) {
    } // for now eat exceptions

    return "";
  }
}
