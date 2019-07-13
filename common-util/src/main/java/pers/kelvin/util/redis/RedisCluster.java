package pers.kelvin.util.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import pers.kelvin.util.log.LogUtil;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author KelvinYe
 */
public class RedisCluster {
    private static final Logger logger = LogUtil.getLogger(RedisCluster.class);

    private static JedisCluster redisConnect(String nodesFilePath) throws FileNotFoundException {
        HashSet<HostAndPort> jedisClusterNodes = new HashSet<>();
        for (String node : readNodesAddress(nodesFilePath)) {
            String[] address = node.split(":");
            String ip = address[0];
            int port = address.length == 1 ? 0 : Integer.parseInt(address[1]);
            jedisClusterNodes.add(new HostAndPort(ip, port));

        }
        return new JedisCluster(jedisClusterNodes);
    }

    public static String get(String nodesFilePath, String key) {
        String value;
        try {
            JedisCluster redis = redisConnect(nodesFilePath);
            value = redis.get(key);
            redis.close();
        } catch (IOException e) {
            value = String.format("%s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
        return value;
    }

    public static String set(String nodesFilePath, String key, String value) {
        String setResult = "0";
        try {
            JedisCluster redis = redisConnect(nodesFilePath);
            setResult = redis.set(key, value);
            redis.close();
        } catch (IOException e) {
            logger.error(String.format("%s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace())));
        }
        return setResult;
    }

    public static boolean exists(String nodesFilePath, String key) {
        boolean isExisted = false;
        try {
            JedisCluster redis = redisConnect(nodesFilePath);
            isExisted = redis.exists(key);
            redis.close();
        } catch (IOException e) {
            logger.error(String.format("%s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace())));
        }
        return isExisted;
    }

    public static String del(String nodesFilePath, String key) {
        long delResult = 0;
        try {
            JedisCluster redis = redisConnect(nodesFilePath);
            delResult = redis.del(key);
            redis.close();
        } catch (IOException e) {
            logger.error(String.format("%s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace())));
        }
        return String.valueOf(delResult);

    }

    public static String append(String nodesFilePath, String key, String value) {
        long appendResult = 0;
        try {
            JedisCluster redis = redisConnect(nodesFilePath);
            redis.append(key, value);
            redis.close();
        } catch (IOException e) {
            logger.error(String.format("%s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace())));
        }
        return String.valueOf(appendResult);
    }

    /**
     * 根据json配置文件读取redis集群的ip:port列表
     * json报文类型为list
     * e.g. ["ip:port","ip:port"]
     */
    private static ArrayList<String> readNodesAddress(String filePath) throws FileNotFoundException {
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);
        Type arrayList = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(reader, arrayList);
    }
}