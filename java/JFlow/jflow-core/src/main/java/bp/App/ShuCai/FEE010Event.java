package bp.App.ShuCai;

import bp.difference.SystemConfig;
import bp.wf.FlowEventBase;

import java.util.Objects;

public class FEE010Event extends FlowEventBase {
    @Override
    public String getFlowMark() {


        return "010,";

    }

    @Override
    public String SendSuccess() throws Exception {

        //特殊处理海南的需求.
        if ("HNJflow".equals(SystemConfig.getCustomerNo()))
        {
            try{
                if (this.getHisNode().getNodeID() == 1001) {
                    ND10Rpt nd10Rpt = new ND10Rpt();
                    nd10Rpt.setOID(this.getWorkID());
                    nd10Rpt.Retrieve();
                    nd10Rpt.setDKSta(1);
                    nd10Rpt.Update();
                }

                if (this.getHisNode().getNodeID() == 1002) {
                    ND10Rpt nd10Rpt = new ND10Rpt();
                    nd10Rpt.setOID(this.getWorkID());
                    nd10Rpt.Retrieve();
                    nd10Rpt.setDKSta(2);
                    nd10Rpt.Update();
                }
            }catch(Exception ex){
                throw  ex;
            }
        }

        return super.SendSuccess();
    }


}
