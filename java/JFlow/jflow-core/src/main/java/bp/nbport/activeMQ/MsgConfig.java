package bp.nbport.activeMQ;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

@Configuration
@ConditionalOnClass(ActiveMQQueue.class)
public class MsgConfig {
    @Value("spring.activemq.queue-name")
    private String queueName;

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("ZGB/PUSH/ZNSPTest/08Ur9");
    }
}
