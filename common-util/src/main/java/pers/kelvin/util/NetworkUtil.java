package pers.kelvin.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkUtil {

    /**
     * 判断ip、端口是否可连接，主要的原理是如果对该主机的特定端口号能建立一个socket,则说明该主机的该端口在使用。
     */
    public static boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 判断ip是否可以连接
     */
    public static boolean isHostReachable(String host, int timeOut) {
        try {
            return InetAddress.getByName(host).isReachable(timeOut);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
