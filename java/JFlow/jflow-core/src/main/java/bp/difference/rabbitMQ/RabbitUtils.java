package bp.difference.rabbitMQ;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name="spring.rabbitmq.enable", havingValue = "true", matchIfMissing = false)
public class RabbitUtils {
    private final RabbitTemplate rabbitTemplate;

    public RabbitUtils(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送消息到指定的交换机和路由键
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void sendMessage(String exchange, String routingKey, Object message){
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
