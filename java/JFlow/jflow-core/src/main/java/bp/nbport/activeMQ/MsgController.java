package bp.nbport.activeMQ;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Queue;
@RestController
@RequestMapping("msg")
public class MsgController {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue queue;
    @RequestMapping("send")
    public String send(String msg){
        jmsMessagingTemplate.convertAndSend(queue,msg);

        return "发送成功"+Thread.currentThread().getName();
    }
}
