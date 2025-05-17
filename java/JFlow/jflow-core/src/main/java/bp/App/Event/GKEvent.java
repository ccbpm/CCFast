package bp.App.Event;
import bp.da.DBAccess;
import bp.da.LogType;
import bp.difference.SystemConfig;
import bp.sys.*;
import bp.tools.HttpClientUtil;
import bp.web.WebUser;
import bp.wf.GenerWorkFlow;
import bp.wf.data.GERpt;
import bp.wf.data.GERptAttr;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GKEvent extends bp.sys.base.EventBase{

    public GKEvent() throws Exception{
        this.setTitle("海南国空事件处理");
    }

    @Override
    public void Do() {
        try {
            GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "国空事件："+gwf.getNodeID());
            this.SucessInfo ="";
            switch (this.getEventSource()) {
                case EventListNode.SendSuccess://发送成功后
                    DoEventBase(gwf);
                    break;
                case EventListNode.UndoneAfter://撤销发送之后
                    break;
                case EventListNode.SendWhen://发送前
                    break;
                case EventListNode.ReturnAfter://退回后
                    break;
                case EventListFlow.FlowOverAfter://流程结束后
                    break;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 事件
     */
    private void DoEventBase(GenerWorkFlow gwf){
        switch (gwf.getNodeID()) {
            case 1202:
                Cg_doData(gwf);
                break;
        }
    }
    /**
     * 数据项
     */
    public void Cg_doData(GenerWorkFlow gwf){
        try {
            GERpt rpt = new GERpt("ND12Rpt");
            rpt.Retrieve(GERptAttr.OID,gwf.getWorkID());
            String czlx= rpt.GetValByKey("CunZhuangMingCheng").toString();//数据类型
            String sjxmc= rpt.GetValByKey("ShuJuXiangMingCheng").toString();//数据项名称
            String czxz= rpt.GetValByKey("CunZhuangXuanZe").toString();//村庄选择
            String sssx= rpt.GetValByKey("SuoShuShiXian").toString();//所属市县
            String sqdw= rpt.GetValByKey("ShenQingDanWei").toString();//申请人
            String sqr= rpt.GetValByKey("ShenQingRen").toString();
            String lxdh= rpt.GetValByKey("LianXiDianHua").toString();
            String GXPL= rpt.GetValByKey("GXPL").toString();
            String GXFS = rpt.GetValByKey("GongXiangFangShi").toString();

            //获取最大id
            String idSql = "SELECT max(id) FROM  gk_cghzlx";
            int id = DBAccess.RunSQLReturnValInt(idSql) + 1;

            czlx = czlx.substring(czlx.lastIndexOf(",")+1);
            String czlz_type = null;
            String dataitemid = null;

            //分四种类型 村规、城规、总规、其它
            if("170".equals(czlx)){
                //如果村庄规划，插入关联表数据
                czlz_type = "cz";
                String[] parts = czxz.split(",");

                //先删后增
                DBAccess.RunSQL("delete from GK_Cglink where dataitemid = '"+id+"')");
                for (String part : parts) {
                    DBAccess.RunSQL("insert into GK_Cglink(dataitemid,xzqid)" +
                            "values('"+id+"','"+part+ "')");
                }
            }else if("100".equals(czlx)){
                czlz_type = "kg";
            }else if("99".equals(czlx)){
                czlz_type = "zg";
            }else{
                czlz_type = "zt";
            }
            //获取加密手机号
            String sjhjm_url = "http://59.212.146.96:85/encrypt/encryptVal";
            Map<String, String> map = new HashMap<>();
            map.put("val", lxdh);
//            String data = HttpClientUtil.doPost(sjhjm_url, map, null, null);
//            bp.da.Log.DefaultLogWriteLine(LogType.Info, "手机号加密数据："+ data);
//            JSONObject jd = JSONObject.parseObject(data);
//            if (jd.get("code").toString().equals("200")) {
//                lxdh = jd.get("data").toString();
//            } else {
//                bp.da.Log.DefaultLogWriteLine(LogType.Info, "手机号加密数据失败："+ jd.get("msg").toString());
//                //执行失败
//                this.SucessInfo = "err@手机号加密数据失败，" + jd.get("msg").toString();
//            }

            dataitemid = czlz_type +"_prjid_"+id;
            DBAccess.RunSQL("insert into GK_Cghzlx(id,dataitemid,dataitemname,dataitemtype,sxlx,sqdw,sqr,shzt,lxdh,gxpl,gxfs)" +
                    "values('"+id+"','"+dataitemid+"','"+sjxmc+ "','" +czlx+"','"+czlz_type+"','"+sqdw+"','"+sqr+"','0','"+lxdh+"','"+GXPL+"','"+GXFS+"')");

            //更新12流程表cghzlxno
            DBAccess.RunSQL("update nd12rpt set cghzlxno = '"+dataitemid+"' where oid = '"+gwf.getWorkID()+"'");

        }
        catch (Exception ex){
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "国空异常："+ ex.getMessage());
        }

    }

}
