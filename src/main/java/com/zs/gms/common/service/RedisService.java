package com.zs.gms.common.service;

import com.zs.gms.common.configure.RedisConfig;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.StaticConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class RedisService {

    private static Map<Integer, RedisTemplate> redisTemplates = new ConcurrentHashMap<>();


    /**
     * 选择库操作
     */
    public static RedisTemplate<String, Object> getTemplate(Integer index) {
        if (redisTemplates.containsKey(index)) {
            return redisTemplates.get(index);
        }
        RedisConfig redisConfig=new RedisConfig();
        RedisTemplate<String, Object> template = redisConfig.getRedisTemplate(index);
        redisTemplates.put(index, template);
        return template;
    }

    /**
     * 添加监听配置
     * */
    public static void addConfig(){
        String str="return redis.call('config','set','notify-keyspace-events','$E')";
        RedisService.getTemplate(StaticConfig.KEEP_DB).execute(new DefaultRedisScript<String>(str), null);
    }

    public static boolean lockService(String key, String value, long seconds) {
        String scriptStr = "if 1 == redis.call('setnx',KEYS[1],ARGV[1]) then" +
                " redis.call('expire',KEYS[1],ARGV[2])"+
                " return 1;" +
                " elseif ARGV[1] == redis.call('get',KEYS[1]) then" +
                " return 1;" +
                " else "+
                " return 0;"+
                " end;";
        return  execScript(scriptStr, key, value,String.valueOf(seconds));
    }

    /**
     * 判断间隔时间内键是否存在，不存在则添加并设置实效时间，返回true，存在则返回false
     * */
    public static boolean intervalLock(String key, String value, long seconds) {
        String scriptStr = "if 1 == redis.call('setnx',KEYS[1],ARGV[1]) then" +
                " redis.call('PEXPIRE',KEYS[1],ARGV[2])"+//健不存在
                " return 1;" +
                " else "+
                " return 0;"+ //健存在
                " end;";
        return  execScript(scriptStr, key, value,String.valueOf(seconds));
    }

    public static boolean releaseLock(String key, String... values) {
        String scriptStr = " if ARGV[1] == redis.call('get',KEYS[1]) then" +
                " redis.call('del',KEYS[1])" +
                " return 1;" +
                " else" +
                " return 0;" +
                " end;";
        return execScript(scriptStr, key, values);
    }

    public static Boolean execScript(String scriptStr, String key, String... values) {
        RedisTemplate<String, Object> template = getTemplate(StaticConfig.KEEP_DB);
        List<String> keys = new ArrayList<>();
        keys.add(key);
        return template.execute(new DefaultRedisScript<Boolean>(scriptStr, Boolean.class), keys, values);
    }


    /**
     * 设置过期时间
     */
    public static <T> boolean set(int index, String key, T value, Long lifeCycle, TimeUnit timeUnit) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (StringUtils.isEmpty(key) || !ObjectUtils.allNotNull(value, lifeCycle, timeUnit)) {
            log.info("参数异常");
            return false;
        }
        try {
            template.opsForValue().set(key, value, lifeCycle, timeUnit);

        } catch (Exception e) {
            log.error("添加数据异常", e);
            return false;
        }
        return true;
    }

    /**
     * 不设置过期时间
     */
    public static <T> boolean set(int index, String key, T value) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (StringUtils.isEmpty(key) || null == value) {
            log.info("参数异常");
            return false;
        }
        try {
            template.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("添加数据异常", e);
            return false;
        }
        return true;
    }


    /**
     * 获取键值，根据泛型返回类型
     */
    public static <T> T get(int index, String key, Class<T> clazz) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (StringUtils.isEmpty(key) || null == clazz) {
            log.info("参数异常");
            return null;
        }
        try {
            return (T) template.opsForValue().get(key);
        } catch (Exception e) {
            log.error("查询异常", e);
            return null;
        }
    }

    /**
     * 获取键值
     */
    public static Object get(int index, String key) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (StringUtils.isEmpty(key)) {
            log.info("参数异常");
            return null;
        }
        try {
            return template.opsForValue().get(key);
        } catch (Exception e) {
            log.error("查询异常", e);
            return null;
        }
    }

    /**
     * 判断键是否存在
     */
    public static boolean existsKey(int index, String key) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (StringUtils.isEmpty(key)) {
            log.info("参数异常");
            return false;
        }
        try {
            return template.hasKey(key);
        } catch (Exception e) {
            log.error("查询异常", e);
            return false;
        }
    }

    /**
     * 重名名key，如果newKey已经存在，则newKey的原值被覆盖
     */
    public static void renameKey(int index, String oldKey, String newKey) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (StringUtils.isAnyEmpty(oldKey, newKey)) {
            log.info("参数异常");
            return;
        }
        template.rename(oldKey, newKey);
    }

    /**
     * newKey不存在时才重命名
     */
    public static boolean renameKeyNotExist(int index, String oldKey, String newKey) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (StringUtils.isAnyEmpty(oldKey, newKey)) {
            log.info("参数异常");
            return false;
        }
        return template.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 删除key
     */
    public static void deleteKey(int index, String key) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (StringUtils.isEmpty(key)) {
            log.info("参数异常");
            return;
        }
        template.delete(key);
    }

    /**
     * 删除多个key
     */
    public static void deleteKey(int index, String... keys) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (ArrayUtils.isEmpty(keys)) {
            log.info("参数异常");
            return;
        }
        try {
            Set<String> kSet = Stream.of(keys).map(k -> k).collect(Collectors.toSet());
            template.delete(kSet);
        } catch (Exception e) {
            log.error("删除异常", e);
            return;
        }
    }

    /**
     * 模糊删除
     */
    public static void deleteLikeKey(int index, String prefix) {
        deleteKey(index, getLikeKey(index, prefix));
    }

    /**
     * 匹配前缀,获取键值集合
     */
    public static Collection<String> getLikeKey(int index, String prefix) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (StringUtils.isEmpty(prefix)) {
            log.info("参数异常");
            return null;
        }
        return template.keys(prefix + "*");
    }

    /**
     * 删除Key的集合
     */
    public static void deleteKey(int index, Collection<String> keys) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (CollectionUtils.isEmpty(keys)) {
            log.info("参数异常,keys为空");
            return;
        }
        try {
            Set<String> kSet = keys.stream().map(k -> k).collect(Collectors.toSet());
            template.delete(kSet);
        } catch (Exception e) {
            log.error("删除异常", e);
            return;
        }
    }

    /**
     * 设置key的生命周期
     */
    public static void expireKey(int index, String key, long time, TimeUnit timeUnit) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (StringUtils.isEmpty(key) || !ObjectUtils.anyNotNull(time, timeUnit) || time < 0) {
            log.info("参数异常");
            return;
        }
        try {
            template.expire(key, time, timeUnit);
        } catch (Exception e) {
            log.error("设置超时时间异常", e);
            return;
        }

    }

    /**
     * 指定key在指定的日期过期
     */
    public static void expireKeyAt(int index, String key, Date date) {
        RedisTemplate<String, Object> template=getTemplate(index);
        template.expireAt(key, date);
    }

    /**
     * 查询key的生命周期
     */
    public static Long getKeyExpire(int index, String key, TimeUnit timeUnit) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (StringUtils.isEmpty(key) || timeUnit == null) {
            log.info("参数异常");
            return 0L;
        }
        try {
            return template.getExpire(key, timeUnit);
        } catch (Exception e) {
            log.error("查询过期时间异常", e);
            return 0L;
        }

    }

    /**
     * 将key设置为永久有效
     */
    public static void persistKey(int index, String key) {
        RedisTemplate<String, Object> template=getTemplate(index);
        if (StringUtils.isEmpty(key)) {
            log.info("参数异常");
            return;
        }
        template.persist(key);
    }


    /**
     * 对hash类型的数据操作
     */
    public static HashOperations<String, String, Object> hashOperations(int index) {
        RedisTemplate<String, Object> template=getTemplate(index);
        return template.opsForHash();
    }

    /**
     * 对redis字符串类型数据操作
     */
    public static ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> template) {
        return template.opsForValue();
    }

    /**
     * 对链表类型的数据操作
     */
    public static ListOperations<String, Object> listOperations(RedisTemplate<String, Object> template) {
        return template.opsForList();
    }

    /**
     * 对无序集合类型的数据操作
     */
    public static SetOperations<String, Object> setOperations(RedisTemplate<String, Object> template) {
        return template.opsForSet();
    }

    /**
     * 对有序集合类型的数据操作
     */
    public static ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> template) {
        return template.opsForZSet();
    }
}
