package com.grunvald.sorm.util;

import java.net.InetAddress;


public class IpAddressUtil {

    public static boolean isValidIp(String ip) {
        try {
            return InetAddress.getByName(ip).getHostAddress().equals(ip);
        } catch (Exception e) {
            return false;
        }
    }
}