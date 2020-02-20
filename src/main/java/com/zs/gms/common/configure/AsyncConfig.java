package com.zs.gms.common.configure;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class AsyncConfig  {

    /**
     * 线程池 ThreadPoolTaskExecutor
     * */

    @Bean(name = "gmsAsyncThreadPool")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor pool=new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        //核心线程池数量，方法: 返回可用处理器的Java虚拟机的数量。
        pool.setCorePoolSize(processors);
        //最大线程数量
        pool.setMaxPoolSize(processors*10);
        //线程池的队列容量
        pool.setQueueCapacity(processors*20);
        pool.setKeepAliveSeconds(30);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setAwaitTerminationSeconds(60);
        //线程名称的前缀
        pool.setThreadNamePrefix("gms-async-thread");
        //当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        pool.initialize();
        return pool;
    }

    /**
     * 异步任务中异常处理
     * */

    @Bean
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
