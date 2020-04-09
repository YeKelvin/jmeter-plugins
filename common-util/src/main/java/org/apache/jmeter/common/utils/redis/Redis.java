package org.apache.jmeter.common.utils.redis;


import redis.clients.jedis.Jedis;

/**
 * @author KelvinYe
 */
public class Redis {
    /**
     * @param redisNodeAddress redis地址，格式为 ip:port
     * @return Jedis实例
     */
    public static Jedis redisConnect(String redisNodeAddress) {
        String[] address = redisNodeAddress.split(":");
        Jedis redis = new Jedis(address[0], Integer.parseInt(address[1]));
        redis.connect();
        return redis;
    }

    public static void flushDB(String host, int port) {
        Jedis redis = new Jedis(host, port);
        redis.connect();
        redis.flushDB();
        redis.close();
    }

    public static void set(String host, int port, String key, String value) {
        Jedis redis = new Jedis(host, port);
        redis.connect();
        redis.set(key, value);
        redis.close();
    }

    public static String get(String host, int port, String key) {
        Jedis redis = new Jedis(host, port);
        redis.connect();
        String value = redis.get(key);
        redis.close();
        return value;
    }

    public static boolean exists(String host, int port, String key) {
        Jedis redis = new Jedis(host, port);
        redis.connect();
        boolean isExisted = redis.exists(key);
        redis.close();
        return isExisted;
    }

    public static String del(String host, int port, String key) {
        Jedis redis = new Jedis(host, port);
        redis.connect();
        long delResult = redis.del(key);
        redis.close();
        return String.valueOf(delResult);
    }

    public static String append(String host, int port, String key, String value) {
        Jedis redis = new Jedis(host, port);
        redis.connect();
        long appendResult = redis.append(key, value);
        redis.close();
        return String.valueOf(appendResult);
    }
}
