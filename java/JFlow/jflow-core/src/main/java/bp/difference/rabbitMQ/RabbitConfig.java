package bp.difference.rabbitMQ;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;


@Configuration
@ConditionalOnProperty(name="spring.rabbitmq.enable", havingValue = "true", matchIfMissing = false)
public class RabbitConfig {

    @Autowired
    private ConnectionFactory rabbitConnectionFactory;

    @Autowired
    private RabbitProperties properties;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(){
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(rabbitConnectionFactory);
        // 并发消费者数量
        containerFactory.setConcurrentConsumers(1);
        containerFactory.setMaxConcurrentConsumers(20);
        // 预加载消息数量 -- QOS
        containerFactory.setPrefetchCount(1);
        // 应答模式（此处设置为手动）
        containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 消息序列化方式
        containerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        // 设置通知调用链（这里设置的是重试机制的调用链）
        containerFactory.setAdviceChain(
                RetryInterceptorBuilder
                        .stateless()
                        .recoverer(new RejectAndDontRequeueRecoverer())
                        .retryOperations(rabbitRetryTemplate())
                        .build()
        );
        return containerFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory);
        // 默认是用jdk序列化
        // 数据转换json存入消息队列，方便可视化界面查看消息数据
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        // 设置开启Mandatory，才能出触发调函数，无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);
        // 此处设置重试template后，会在生产者发送消息的时候，调用该template中的调用链
        rabbitTemplate.setRetryTemplate(rabbitRetryTemplate());
        //
        rabbitTemplate.setConfirmCallback(
                ((correlationData, b, s) -> {
                    System.out.println("ConfirmCallback 相关数据：" + correlationData);
                    System.out.println("ConfirmCallback 确认情况：" + b);
                    System.out.println("ConfirmCallback 原因：" + s);
                })
        );
        rabbitTemplate.setReturnCallback(((message, i, s, s1, s2) -> {
            System.out.println("RetrunCallback 消息：" + message);
            System.out.println("RetrunCallback 回应码：" + i);
            System.out.println("RetrunCallback 回应消息：" + s);
            System.out.println("RetrunCallback 交换机：" + s1);
            System.out.println("RetrunCallback 路由键：" + s2);
        }));

        return rabbitTemplate;
    }

    @Bean
    public RetryTemplate rabbitRetryTemplate(){
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.registerListener(new RetryListener() {
            @Override
            public <T, E extends Throwable> boolean open(RetryContext retryContext, RetryCallback<T, E> retryCallback) {
                // 执行之前调用（返回false时会终止执行）
                return true;
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext retryContext, RetryCallback<T, E> retryCallback, Throwable throwable) {
                // 重试结束的时候调用（最后一次重试）
                System.out.println("--------------------最后一次调用");
                return;
            }

            @Override
            public <T, E extends Throwable> void onError(RetryContext retryContext, RetryCallback<T, E> retryCallback, Throwable throwable) {
                // 异常 都会调用
                System.out.println("-------第{}次调用" + retryContext.getRetryCount());
            }
        });
        retryTemplate.setBackOffPolicy(backOffPilicyByProperties());
        retryTemplate.setRetryPolicy(retryPolicyByProperties());
        return retryTemplate;
    }

    @Bean
    public ExponentialBackOffPolicy backOffPilicyByProperties(){
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        long maxInterval = properties.getListener().getSimple().getRetry().getMaxInterval().getSeconds();
        long initialInterval = properties.getListener().getSimple().getRetry().getInitialInterval().getSeconds();
        double multiplier = properties.getListener().getSimple().getRetry().getMultiplier();
        // 重试间隔
        backOffPolicy.setInitialInterval(initialInterval * 1000);
        // 重试最大间隔
        backOffPolicy.setMaxInterval(maxInterval * 1000);
        // 重试间隔乘法策略
        backOffPolicy.setMultiplier(multiplier);
        return backOffPolicy;
    }

    @Bean
    public SimpleRetryPolicy retryPolicyByProperties(){
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        int maxAttempts = properties.getListener().getSimple().getRetry().getMaxAttempts();
        retryPolicy.setMaxAttempts(maxAttempts);
        return retryPolicy;
    }
}
