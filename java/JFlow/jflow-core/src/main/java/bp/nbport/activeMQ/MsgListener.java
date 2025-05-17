package bp.nbport.activeMQ;

import bp.nbport.OrgEmpCRUD;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name="spring.activemq.listener.enabled", havingValue="true", matchIfMissing=false)
public class MsgListener {

    OrgEmpCRUD orgEmpCRUD = new OrgEmpCRUD();
    @JmsListener(destination = "ZGB/PUSH/ZNSPTest/08Ur9" )
    public void receiveMessage(String message) throws Exception {
        Thread.sleep(1000);
        System.out.println("接收到的消息："+message);
        orgEmpCRUD.dealOrgEmp(message);
    }
}
