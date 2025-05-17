package bp.tools;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

public class JflowThreadPoolTaskExecutor {
    private  JflowThreadPoolTaskExecutor(){};
    private  static ThreadPoolTaskExecutor threadPool=null;

    public synchronized static ThreadPoolTaskExecutor getInstance(){
        if(threadPool==null){
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            // 核心线程：核心线程一直存活
            executor.setCorePoolSize(20);
            // 队列容量：任务将队列塞满之后，扩展核心线程，线程总数最多不超过最大线程数
            executor.setQueueCapacity(10);
            // 最大线程
            executor.setMaxPoolSize(100);
            // 闲置线程存活时长
            executor.setKeepAliveSeconds(60);
            // 拒绝策略：任务超出线程池容量后，新任务交还主线程处理
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            // 线程名前缀
            executor.setThreadNamePrefix("expensive-task-worker-");
            // 初始化线程池
            executor.initialize();
            return executor;
        }
        return threadPool;

    }



}
