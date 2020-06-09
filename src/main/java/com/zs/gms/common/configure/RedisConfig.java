package com.zs.gms.common.configure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.utils.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.*;
import org.springframework.util.ErrorHandler;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 缓存配置-使用Lettuce客户端，自动注入配置的方式
 */
@Configuration
@EnableCaching //启用缓存
@Slf4j
public class RedisConfig extends CachingConfigurerSupport {

    @Autowired
    private RedisProperties properties;

    public static RedisConfig instance;

    @PostConstruct
    public void init(){
        instance=this;
        properties = this.properties;
    }

    /**
     * @param index 数据库
     */
    public RedisTemplate<String,Object> getRedisTemplate(int index) {
        return redisTemplate(instance.properties,index);
    }

    /**
     * 自定义缓存key的生成策略。默认的生成策略是看不懂的(乱码内容) 通过Spring 的依赖注入特性进行自定义的配置注入并且此类是一个配置类可以更多程度的自定义配置
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            Arrays.stream(params).map(Object::toString).forEach(sb::append);
            return sb.toString();
        };
    }

    /**
     * 缓存配置管理器,使用缓存时走这个配置
     */
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory factory) {
        // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        // 设置缓存的默认过期时间，也是使用Duration设置
        config = config.entryTtl(Duration.ofMinutes(5))
                // 设置 key为string序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value为json序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer()))
                // 不缓存空值
                .disableCachingNullValues();

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(factory).cacheDefaults(config);
        return builder.build();
    }

    @Bean
    @Primary
    public RedisSerializer jackson2JsonRedisSerializer() {
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        //此种序列化方式结果清晰、容易阅读、存储字节少、速度快，所以推荐更换
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }


    /**
     * 多数据源
     */
    public RedisTemplate<String, Object> redisTemplate(RedisProperties properties,int index) {
        //基本配置
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(properties.getHost());
        configuration.setPort(properties.getPort());
        configuration.setDatabase(index);
        //连接池通用配置
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(properties.getLettuce().getPool().getMaxIdle());
        genericObjectPoolConfig.setMinIdle(properties.getLettuce().getPool().getMinIdle());
        genericObjectPoolConfig.setMaxTotal(properties.getLettuce().getPool().getMaxActive());
        //lettuce pool
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.poolConfig(genericObjectPoolConfig);
        builder.commandTimeout(Duration.ofSeconds(properties.getLettuce().getPool().getMaxWait().toMillis()));

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(configuration, builder.build());
        connectionFactory.afterPropertiesSet();
        return createRedisTemplate(connectionFactory);
    }


    /**
     * 获取缓存操作助手对象
     */
    @Bean(name = "redisTemplate")
    @Primary
    public RedisTemplate<String, Object> createRedisTemplate(LettuceConnectionFactory factory) {
        //创建Redis缓存操作助手RedisTemplate对象
        RedisTemplate<String, Object> template = new RedisTemplate();
        template.setConnectionFactory(factory);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer redisSerializer = new StringRedisSerializer();
        template.setKeySerializer(redisSerializer);
        template.setValueSerializer(redisSerializer);//二进制数据使用JdkSerializationRedisSerializer

        template.setHashKeySerializer(redisSerializer);
        template.setHashValueSerializer(redisSerializer);
        template.afterPropertiesSet();
        return template;
    }


    @Bean
    RedisMessageListenerContainer keyListenerContainer(LettuceConnectionFactory factory) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(factory);
        List<Topic> topics = new ArrayList<>();
        //topics.add(new PatternTopic("__keyspace@0__:test*"));
        topics.add(new PatternTopic("__keyevent@" + StaticConfig.MONITOR_DB + "__:set"));
        listenerContainer.addMessageListener((message, pattern) -> {
            //处理业务逻辑
            MessageUtil.handeListenerResult(message);
        }, topics);
        listenerContainer.setErrorHandler(t -> log.error("redis监听异常处理",t));
        return listenerContainer;
    }
}