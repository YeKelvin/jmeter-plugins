package org.apache.jmeter.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Kelvin.Ye
 */
public class RedisCluster {

    private static final Logger log = LoggerFactory.getLogger(RedisCluster.class);

    private static JedisCluster redisConnect(String nodes) throws FileNotFoundException {
        String[] nodeArray = nodes.split(",");
        Set<HostAndPort> jedisClusterNode = new HashSet<>();
        for (String node : nodeArray) {
            String[] address = node.split(":");
            String ip = address[0];
            int port = address.length == 1 ? 0 : Integer.parseInt(address[1]);
            jedisClusterNode.add(new HostAndPort(ip, port));

        }
        return new JedisCluster(jedisClusterNode);
    }

    public static String get(String nodes, String key) {
        String value;
        try {
            JedisCluster redis = redisConnect(nodes);
            value = redis.get(key);
            redis.close();
        } catch (IOException e) {
            value = String.format("%s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
        return value;
    }

    public static String set(String nodes, String key, String value) {
        String setResult = "0";
        try {
            JedisCluster redis = redisConnect(nodes);
            setResult = redis.set(key, value);
            redis.close();
        } catch (IOException e) {
            log.error("{}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
        return setResult;
    }

    public static boolean exists(String nodes, String key) {
        boolean isExisted = false;
        try {
            JedisCluster redis = redisConnect(nodes);
            isExisted = redis.exists(key);
            redis.close();
        } catch (IOException e) {
            log.error("{}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
        return isExisted;
    }

    public static String del(String nodes, String key) {
        long delResult = 0;
        try {
            JedisCluster redis = redisConnect(nodes);
            delResult = redis.del(key);
            redis.close();
        } catch (IOException e) {
            log.error("{}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
        return String.valueOf(delResult);

    }

    public static String append(String nodes, String key, String value) {
        long appendResult = 0;
        try {
            JedisCluster redis = redisConnect(nodes);
            redis.append(key, value);
            redis.close();
        } catch (IOException e) {
            log.error("{}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
        return String.valueOf(appendResult);
    }

}