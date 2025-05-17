package bp.ccbill;

import bp.ccbill.template.Collection;
import bp.ccbill.template.Collections;
import bp.ccbill.template.Method;
import bp.ccbill.template.*;
import bp.da.*;
import bp.difference.ContextHolderUtils;
import bp.difference.StringHelper;
import bp.difference.SystemConfig;
import bp.difference.handler.CommonFileUtils;
import bp.en.*;
import bp.en.Map;
import bp.sys.*;
import bp.tools.DateUtils;
import bp.tools.Encodes;
import bp.tools.HttpClientUtil;
import bp.tools.Json;
import bp.web.WebUser;
import bp.wf.*;
import bp.wf.httphandler.WF_WorkOpt;
import bp.wf.template.FrmNode;
import bp.wf.template.WhoIsPK;
import bp.wf.template.frm.*;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLDecoder;
import java.util.*;

/**
 * 页面功能实体
 */
public class WF_CCBill extends bp.difference.handler.DirectoryPageBase {

    ///#region 构造方法.

    /**
     * 方法ID
     */
    public final String getMethodID() {
        return this.GetRequestVal("MethodID");
    }

    /**
     * 方法编号
     */
    public final String getMethodNo() {
        return this.GetRequestVal("MethodNo");
    }

    /**
     * 构造函数
     */
    public WF_CCBill() {
    }

    ///#endregion 构造方法.


    ///#region 方法处理.
    public final String MyDict_DoBill_Start() throws Exception {
        //创建单据
        long workid = bp.ccbill.Dev2Interface.CreateBlankBillID(this.getFrmID());

        String workids = GetRequestVal("WorkIDs");
        if (DataType.IsNullOrEmpty(workids) == true) {
            return "err@请选择需要操作的行";
        }
        String fromFrmID = GetRequestVal("FromFrmID");

        ///#region 把实体表单的数据集合拷贝到单据从表数据中
        GEEntityOIDs ens = new GEEntityOIDs(fromFrmID);
        QueryObject qo = new QueryObject(ens);
        qo.AddWhereIn("OID", "(" + workids + ")");
        qo.DoQuery();
        GEDtl gedtl = null;
        String mapdtlNo = this.getFrmID() + "Dtl1";
        GEDtls gedtls = new GEDtls(mapdtlNo);
        gedtls.Retrieve(GEDtlAttr.RefPK, workid, null);
        for (GEEntityOID en : ens.ToJavaList()) {
            //先判断从表中是不是存在该实体数据，存在continue;
            if (gedtls.getIsExits("DictOID", en.getOID()) == true) {
                continue;
            }
            gedtl = new GEDtl(mapdtlNo);
            gedtl.Copy(en);
            gedtl.setRefPKInt64(workid);
            gedtl.SetValByKey("DictOID", en.getOID());
            gedtl.setOID(0);
            gedtl.Insert();
        }

        ///#endregion 把实体表单的数据集合拷贝到单据从表数据中

        return "./MyBill.htm?FrmID=" + this.getFrmID() + "&WorkID=" + workid;
    }

    public final String MyDict_DoFlowBatchBaseData_StartFlow() throws Exception {
        //创建工作.
        long workid = bp.wf.Dev2Interface.Node_CreateBlankWork(this.getFlowNo());

        String workids = GetRequestVal("WorkIDs");
        if (DataType.IsNullOrEmpty(workids) == true) {
            return "err@请选择需要操作的行";
        }
        String fromFrmID = GetRequestVal("FromFrmID");

        ///#region 把实体表单的数据集合拷贝到流程从表数据中
        GEEntityOIDs ens = new GEEntityOIDs(fromFrmID);
        QueryObject qo = new QueryObject(ens);
        qo.AddWhereIn("OID", "(" + workids + ")");
        qo.DoQuery();
        GEDtl gedtl = null;
        String mapdtlNo = "ND" + Integer.parseInt(this.getFlowNo()) + "01" + "Dtl1";
        GEDtls gedtls = new GEDtls(mapdtlNo);
        gedtls.Retrieve(GEDtlAttr.RefPK, workid, null);
        for (GEEntityOID en : ens.ToJavaList()) {
            //先判断从表中是不是存在该实体数据，存在continue;
            if (gedtls.getIsExits("DictOID", en.getOID()) == true) {
                continue;
            }
            gedtl = new GEDtl(mapdtlNo);
            gedtl.Copy(en);
            gedtl.setRefPKInt64(workid);
            gedtl.SetValByKey("DictOID", en.getOID());
            gedtl.setOID(0);
            gedtl.Insert();
        }

        ///#endregion 把实体表单的数据集合拷贝到单据从表数据中

        //更新标记, 表示:该流程被谁发起.
        GenerWorkFlow gwf = new GenerWorkFlow(workid);
        gwf.setPWorkID(this.getWorkID());
        gwf.setPFlowNo(fromFrmID);

        gwf.SetPara("FlowBaseData", "1"); //启动了修改基础资料流程..
        gwf.SetPara("MethodNo", this.getMethodNo()); //启动了修改基础资料流程..
        gwf.SetPara("DictFrmID", fromFrmID); //启动了修改基础资料流程..
        gwf.SetPara("DictWorkID", workids); //启动了修改基础资料流程..
        gwf.Update();

        //写日志.
        bp.ccbill.Dev2Interface.WriteTrack(fromFrmID, "0", FrmActionType.StartFlow, "启动:" + gwf.getFlowName() + ",标题:" + gwf.getTitle());
        return "../MyFlow.htm?FK_Flow=" + this.getFlowNo() + "&WorkID=" + workid;
    }

    /**
     * 执行流程:变更基础资料
     *
     * @return
     */
    public final String MyDict_DoFlowBaseData_StartFlow() throws Exception {
        Method md = new Method(this.getMethodNo());

        GEEntity en = new GEEntity(md.getFrmID(), this.getWorkID());

        Hashtable ht = new Hashtable();

        Attrs attrs = en.getEnMap().getAttrs();
        for (Attr item : attrs) {
            if (item.getKey().equals("BillNo") == false && bp.wf.Glo.getFlowFields().contains("," + item.getKey() + ",") == true) {
                continue;
            }

            String val = en.GetValStrByKey(item.getKey());
            ht.put(item.getKey(), val);
            ht.put("bak" + item.getKey(), val);
        }

        //创建工作.
        long workid = bp.wf.Dev2Interface.Node_CreateBlankWork(md.getFlowNo(), ht);

        //更新标记, 表示:该流程被谁发起.
        GenerWorkFlow gwf = new GenerWorkFlow(workid);
        gwf.setPWorkID(this.getWorkID());
        gwf.setPFlowNo(md.getFrmID());

        gwf.SetPara("FlowBaseData", "1"); //启动了修改基础资料流程..
        gwf.SetPara("MethodNo", this.getMethodNo()); //启动了修改基础资料流程..
        gwf.SetPara("DictFrmID", md.getFrmID()); //启动了修改基础资料流程..
        gwf.SetPara("DictWorkID", this.getWorkID()); //启动了修改基础资料流程..
        gwf.Update();

        //写日志.
        bp.ccbill.Dev2Interface.WriteTrack(md.getFrmID(), String.valueOf(this.getWorkID()), FrmActionType.StartFlow, "启动:" + gwf.getFlowName() + ",标题:" + gwf.getTitle(), null, md.getFlowNo(), md.getName(), Integer.parseInt(md.getFlowNo() + "01"), workid);
        return "../MyFlow.htm?FK_Flow=" + md.getFlowNo() + "&WorkID=" + workid;
    }

    /**
     * 发起其他业务流程
     *
     * @return
     */
    public final String MyDict_DoFlowEtc_StartFlow() throws Exception {
        Method md = new Method(this.getMethodNo());

        GEEntity en = new GEEntity(md.getFrmID(), this.getWorkIDStr());

        //#region 处理发起限制.
        //判断是否可以发起流程? 处理发起限制.
        DBRoles rls = new DBRoles();
        rls.Retrieve("FrmID", md.getFrmID(), "DBRole", "StartLimit","Docs", md.getFlowNo());
        if (rls.size() != 0)
        {
            for (DBRole item : rls.ToJavaList())
            {
                if (item.getMarkID().equals("OnlyStartSelfCreateRec") == true && en.GetValByKey("Starter").equals(WebUser.getNo()) == false)
                    return "err@改单据不是您创建的，您不能发起流程。";
                //如果有未完成的流程，就不能发起.
                if (item.getMarkID().equals("UnOverFlow") == true)
                {
                    String sql = "SELECT Title FROM WF_GenerWorkFlow WHERE PWorkID='"+this.getWorkID()+"' AND WFState IN(5,2)";
                    DataTable dt = DBAccess.RunSQLReturnTable(sql);
                    if (dt.Rows.size() != 0)
                        return "err@有未完成的流程，当前流程不能启动，需要等待上一个流程完成后，该流程才可以重新发起。";
                }
            }
        }
        // #endregion 处理发起限制.
        Hashtable ht = new Hashtable();

        Attrs attrs = en.getEnMap().getAttrs();
        for (Attr item : attrs) {
            if (bp.wf.Glo.getFlowFields().contains("," + item.getKey() + ",") == true) {
                continue;
            }

            String val = en.GetValStrByKey(item.getKey());
            ht.put(item.getKey(), val);
            ht.put("bak"+item.getKey(), val);
        }


        //创建工作.
        long workid = bp.wf.Dev2Interface.Node_CreateBlankWork(md.getMethodID(), ht);
        //更新标记, 表示:该流程被谁发起.
        GenerWorkFlow gwf = new GenerWorkFlow(workid);
        gwf.setPWorkID(this.getWorkID());
        gwf.setPFlowNo(this.getFrmID());
        gwf.SetPara("DictFlowEtc", "1"); //启动了其他业务流程.
        gwf.Update();
        bp.wf.Dev2Interface.Node_SetDraft(workid);
        int nodeID = Integer.parseInt(Integer.parseInt(md.getMethodID())+"01");
        Node nd = new Node(nodeID);
        //判断当前节点是否是绑定当前表单
        if(nd.getHisFormType() == NodeFormType.RefOneFrmTree){
            FrmNode frmNode = new FrmNode();
            frmNode.setMyPK(this.getFrmID()+"_"+nodeID+"_"+nd.getFlowNo());
            int i = frmNode.RetrieveFromDBSources();
            if(i == 1 && frmNode.getWhoIsPK() == WhoIsPK.PWorkID){
                //绑定表单并且是PWorkID,不需要拷贝数据
                Work wk = nd.getHisWork();
                wk.Delete(WorkAttr.OID,workid);
                bp.ccbill.Dev2Interface.WriteTrack(md.getFrmID(), String.valueOf(this.getWorkID()), FrmActionType.StartFlow, "启动:" + gwf.getFlowName() + ",标题:" + gwf.getTitle(), null, md.getFlowNo(), md.getName(), Integer.parseInt(md.getFlowNo() + "01"), workid);
                return "../MyFlow.htm?FK_Flow=" + md.getFlowNo() + "&WorkID=" + workid+"&PWorkID="+this.getWorkID();
            }
        }
        //复制明细,从表
        //复制从表数据.
        MapDtls dtls = new MapDtls(md.getFrmID());
        //获取当前流程开始节点的表单
        String frmID =nd.getNodeFrmID();
        MapData mapData = new MapData(frmID);
        MapDtls dtls1 = mapData.getMapDtls();
        for (MapDtl dtl : dtls.ToJavaList())
        {
            //判断从表是否存在，根据别名计算
            Entity enn = dtls1.GetEntityByKey(MapDtlAttr.Alias,dtl.getAlias());
            if(enn == null) continue;
            MapDtl dtl1 = (MapDtl)enn;
            //删除旧的数据.
            DBAccess.RunSQL("DELETE FROM " + dtl1.getPTable() + " WHERE RefPK='" + workid + "'");

            GEDtls ensDtl = new GEDtls(dtl.getNo());
            ensDtl.Retrieve(GEDtlAttr.RefPK, String.valueOf(this.getWorkID()));

            for (GEDtl enDtl : ensDtl.ToJavaList())
            {
                enDtl.setRefPK(String.valueOf(workid));
                enDtl.InsertAsNew();
            }
        }

        //复制附件数据.
        FrmAttachments aths = new FrmAttachments(md.getFrmID());
        FrmAttachments aths1 = new FrmAttachments(mapData.getNo());
        for (FrmAttachment ath : aths.ToJavaList())
        {
            Entity enn = aths1.GetEntityByKey(FrmAttachmentAttr.NoOfObj,ath.getNoOfObj());
            if(enn==null) continue;
            //删除可能存在的新oid数据。
            DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE NoOfObj='" + ath.getNoOfObj() + "' AND RefPKVal='" + workid + "'");

            //找出旧数据.
            FrmAttachmentDBs athDBs = new FrmAttachmentDBs(md.getFrmID(), String.valueOf(this.getWorkID()));
            for (FrmAttachmentDB athDB : athDBs.ToJavaList())
            {
                FrmAttachmentDB athDB_N = new FrmAttachmentDB();
                athDB_N.Copy(athDB);

                athDB_N.setFrmID(mapData.getNo());
                athDB_N.setRefPKVal(String.valueOf(workid));

                if (athDB_N.getHisAttachmentUploadType() == AttachmentUploadType.Single)
                {
                    /*如果是单附件.*/
                    athDB_N.setMyPK(athDB_N.getFKFrmAttachment() + "_" + workid);
                    if (athDB_N.getIsExits() == true)
                    {
                        continue; //说明上一个节点或者子线程已经copy过了, 但是还有子线程向合流点传递数据的可能，所以不能用break.
                    }

                    athDB_N.Insert();
                }
                else
                {
                    athDB_N.setMyPK(DBAccess.GenerGUID());
                    athDB_N.Insert();
                }
            }
        }

        //写日志.
        bp.ccbill.Dev2Interface.WriteTrack(md.getFrmID(), String.valueOf(this.getWorkID()), FrmActionType.StartFlow, "启动:" + gwf.getFlowName() + ",标题:" + gwf.getTitle(), null, md.getFlowNo(), md.getName(), Integer.parseInt(md.getFlowNo() + "01"), workid);
        return "../MyFlow.htm?FK_Flow=" + md.getFlowNo() + "&WorkID=" + workid+"&PWorkID="+this.getWorkID();
    }

    ///#endregion

    /**
     * 发起列表.
     *
     * @return
     */
    public final String Start_Init() throws Exception {
        //获得发起列表.
        DataTable dt = bp.ccbill.Dev2Interface.DB_StartBills(WebUser.getNo());

        //返回组合
        return bp.tools.Json.ToJson(dt);
    }

    /**
     * 草稿列表
     *
     * @return
     */
    public final String Draft_Init() throws Exception {
        //草稿列表.
        DataTable dt = bp.ccbill.Dev2Interface.DB_Draft(this.getFrmID(), WebUser.getNo());

        //返回组合
        return bp.tools.Json.ToJson(dt);
    }

    /**
     * 单据初始化
     *
     * @return
     */
    public final String MyBill_Init() throws Exception {
        //获得发起列表.
        DataTable dt = bp.ccbill.Dev2Interface.DB_StartBills(WebUser.getNo());
        //返回组合
        return bp.tools.Json.ToJson(dt);
    }

    /**
     * 执行
     *
     * @return 返回执行结果
     */
    public final String DoMethod_ExeSQL() throws Exception {
        MethodFunc func = new MethodFunc(this.getMyPK());
        String doc = func.getMethodDocSQL();
        String workID = this.getWorkIDStr();
        if (DataType.IsNullOrEmpty(workID) == true) {
            //批量执行方法
            String workids = this.GetRequestVal("WorkIDs");
            if (DataType.IsNullOrEmpty(workids) == true) {
                throw new RuntimeException("err@执行方法获取到的WorkID或者WorkIDs不能为空");
            }
            String[] strs = workids.split(",");
            workID = strs[0];
            doc = doc.replace("@WorkIDs", workids);
        }
        GEEntity en = new GEEntity(func.getFrmID(), workID);

        doc = bp.wf.Glo.DealExp(doc, en, null); //替换里面的内容.
        String sql = MidStrEx(doc, "/*", "*/");
        try {
            DBAccess.RunSQLs(sql);
            if (func.getMsgSuccess().equals("")) {
                func.setMsgSuccess("执行成功.");
            }

            bp.ccbill.Dev2Interface.WriteTrack(this.getFrmID(), workID, "执行方法", func.getName());

            return func.getMsgSuccess();
        } catch (RuntimeException ex) {
            if (func.getMsgErr().equals("")) {
                func.setMsgErr("执行失败(DoMethod_ExeSQL).");
            }
            return "err@" + func.getMsgErr() + " @ " + ex.getMessage();
        }
    }

    public final String DoMethod_ExecFunc() throws Exception {
        MethodFunc func = new MethodFunc(this.getMyPK());
        String doc = func.getDocs();
        BuessUnitBase en = bp.sys.base.Glo.GetBuessUnitEntityByEnName(doc);
        if (en==null)
            throw new Exception("err@类名错误："+doc);

        try {
            String workID = this.getWorkIDStr();
            en.WorkID = Long.parseLong(workID);
            en.DoIt();

            bp.ccbill.Dev2Interface.WriteTrack(this.getFrmID(), workID, "执行方法", func.getName());
            return func.getMsgSuccess();
        } catch (RuntimeException ex) {
            if (func.getMsgErr().equals("")) {
                func.setMsgErr("执行失败(DoMethod_ExecFunc).");
            }
            return "err@" + func.getMsgErr() + " @ " + ex.getMessage();
        }
    }

    /**
     * 解析异常
     * @param ex
     * @param sql
     * @return
     */
    public String DealException(Exception ex, String sql) {
        String errmsg = "";
        //字段不存在的异常
        String exMessage = ex.getMessage().toLowerCase();
        if(exMessage != null && (exMessage.contains("does not exist") || exMessage.contains("unknown column"))){
            errmsg = "err@执行失败，物理表的字段不存在，请创建字段.";
        }
        //表不存在的异常
        if(exMessage != null && (exMessage.contains("doesn't exist"))){
            errmsg = "err@执行失败，物理表不存在，请您创建物理表或者表名配置错误.";
        }
        if (!DataType.IsNullOrEmpty(sql)) {
            errmsg += "执行的SQL=" + sql;
        }
        return errmsg;
    }
    /**
     * 执行SQL
     *
     * @return
     */
    public final String DoMethodPara_ExeSQL() throws Exception {
        MethodFunc func = new MethodFunc(this.getPKVal());
        String doc = func.getMethodDocSQL();
        String workID = this.getWorkIDStr();
        if (DataType.IsNullOrEmpty(workID) == true) {
            //批量执行方法
            String workids = this.GetRequestVal("WorkIDs");
            if (DataType.IsNullOrEmpty(workids) == true) {
                throw new RuntimeException("err@执行方法获取到的WorkID或者WorkIDs不能为空");
            }
            String[] strs = workids.split(",");
            workID = strs[0];
            doc = doc.replace("@WorkIDs", workids);
        }
        GEEntity en = new GEEntity(func.getFrmID(), workID);


        ///#region 替换参数变量.
        if (doc.contains("@") == true) {
            MapAttrs mattrs = new MapAttrs();
            mattrs.Retrieve(MapAttrAttr.FK_MapData, this.getPKVal(), null);
            for (MapAttr item : mattrs.ToJavaList()) {
                if (doc.contains("@") == false) {
                    break;
                }
                if (item.getUIContralType() == UIContralType.TB) {
                    doc = doc.replace("@" + item.getKeyOfEn(), this.GetRequestVal("TB_" + item.getKeyOfEn()));
                    continue;
                }

                if (item.getUIContralType() == UIContralType.DDL) {
                    doc = doc.replace("@" + item.getKeyOfEn(), this.GetRequestVal("DDL_" + item.getKeyOfEn()));
                    continue;
                }


                if (item.getUIContralType() == UIContralType.CheckBok) {
                    doc = doc.replace("@" + item.getKeyOfEn(), this.GetRequestVal("CB_" + item.getKeyOfEn()));
                    continue;
                }

                if (item.getUIContralType() == UIContralType.RadioBtn) {
                    doc = doc.replace("@" + item.getKeyOfEn(), this.GetRequestVal("RB_" + item.getKeyOfEn()));
                    continue;
                }
            }
        }

        ///#endregion 替换参数变量.

        doc = bp.wf.Glo.DealExp(doc, en, null); //替换里面的内容.
        String sql = MidStrEx(doc, "/*", "*/");

        ///#region 开始执行SQLs.
        try {
            DBAccess.RunSQLs(sql);
            if (func.getMsgSuccess().equals("")) {
                func.setMsgSuccess("执行成功.");
            }

            return func.getMsgSuccess();
        } catch (RuntimeException ex) {
            String de = DealException(ex, sql);
            if (DataType.IsNullOrEmpty(de) == false)
            {
                return de;
            }
            if (func.getMsgErr().equals("")) {
                func.setMsgErr("执行失败.");
            }

            return "err@" + func.getMsgErr() + " @ " + ex.getMessage();
        }

        ///#endregion 开始执行SQLs.

    }

    public String DoMethodPara_ExeSQL_V3() throws Exception {
        MethodFunc func = new MethodFunc(this.getPKVal());
        String doc = func.getMethodDocSQL();
        String workID = this.getWorkIDStr();
        if (DataType.IsNullOrEmpty(workID) == true)
        {
            //批量执行方法
            String workids = this.GetRequestVal("WorkIDs");
            if (DataType.IsNullOrEmpty(workids) == true)
                throw new Exception("err@执行方法获取到的WorkID或者WorkIDs不能为空");
            String[] strs = workids.split(",");
            workID = strs[0];
            doc = doc.replace("@WorkIDs", workids);
        }
        GEEntity en = new GEEntity(func.getFrmID(), workID);

           /// #region 替换参数变量.
        if (doc.contains("@") == true)
        {
            MapAttrs mattrs = new MapAttrs();
            mattrs.Retrieve(MapAttrAttr.FK_MapData, this.getPKVal());
            for (MapAttr item : mattrs.ToJavaList())
            {
                if (doc.contains("@") == false)
                    break;
                if (item.getUIContralType() == UIContralType.TB)
                {
                    doc = doc.replace("@" + item.getKeyOfEn(), this.GetRequestVal(item.getKeyOfEn()));
                    continue;
                }

                if (item.getUIContralType() == UIContralType.DDL)
                {
                    doc = doc.replace("@" + item.getKeyOfEn(), this.GetRequestVal(item.getKeyOfEn()));
                    continue;
                }


                if (item.getUIContralType() == UIContralType.CheckBok)
                {
                    doc = doc.replace("@" + item.getKeyOfEn(), this.GetRequestVal(item.getKeyOfEn()));
                    continue;
                }

                if (item.getUIContralType() == UIContralType.RadioBtn)
                {
                    doc = doc.replace("@" + item.getKeyOfEn(), this.GetRequestVal(item.getKeyOfEn()));
                    continue;
                }
            }
        }
           /// #endregion 替换参数变量.

        doc = bp.wf.Glo.DealExp(doc, en, null); //替换里面的内容.
        String sql = MidStrEx(doc, "/*", "*/");
           /// #region 开始执行SQLs.
        try
        {
            DBAccess.RunSQLs(sql);
            if ("".equals(func.getMsgSuccess()))
                func.setMsgSuccess("执行成功.");

            return func.getMsgSuccess();
        }
        catch (Exception ex)
        {
            String de = DealException(ex, sql);
            if (DataType.IsNullOrEmpty(de) == false)
            {
                return de;
            }
            if ("".equals(func.getMsgErr()))
                func.setMsgErr("执行失败.");

            bp.ccbill.Dev2Interface.WriteTrack(this.getFrmID(), workID, "执行方法失败", func.getName() + func.getMsgErr());

            return "err@" + func.getMsgErr() + " @ " + ex.getMessage();
        }
           /// #endregion 开始执行SQLs.

        //无法执行到这里
        //bp.ccbill.Dev2Interface.WriteTrack(this.getFrmID(), workID, "执行方法", func.getName());

        //return "err@" + func.getMethodDocTypeOfFunc() + ",执行的类型没有解析.";
    }
    /**
     * 执行url.
     *
     * @return
     */
    public final String DoMethodPara_ExeUrl() throws Exception {
        MethodFunc func = new MethodFunc(this.getPKVal());
        String doc = func.getMethodDocUrl();
        if (this.getWorkID() == 0) {
            //批量执行方法
            String workids = this.GetRequestVal("WorkIDs");
            if (DataType.IsNullOrEmpty(workids) == true) {
                throw new RuntimeException("err@执行方法获取到的WorkID或者WorkIDs不能为空");
            }
            String[] strs = workids.split(",");
            this.setWorkID(Long.parseLong(strs[0]));
            doc = doc.replace("@WorkIDs", workids);
        }
        GEEntity en = new GEEntity(func.getFrmID(), this.getWorkID());


        ///#region 替换参数变量.
        if (doc.contains("@") == true) {
            MapAttrs mattrs = new MapAttrs();
            mattrs.Retrieve(MapAttrAttr.FK_MapData, this.getPKVal(), null);
            for (MapAttr item : mattrs.ToJavaList()) {
                if (doc.contains("@") == false) {
                    break;
                }
                if (item.getUIContralType() == UIContralType.TB) {
                    doc = doc.replace("@" + item.getKeyOfEn(), this.GetRequestVal("TB_" + item.getKeyOfEn()));
                    continue;
                }

                if (item.getUIContralType() == UIContralType.DDL) {
                    doc = doc.replace("@" + item.getKeyOfEn(), this.GetRequestVal("DDL_" + item.getKeyOfEn()));
                    continue;
                }


                if (item.getUIContralType() == UIContralType.CheckBok) {
                    doc = doc.replace("@" + item.getKeyOfEn(), this.GetRequestVal("CB_" + item.getKeyOfEn()));
                    continue;
                }

                if (item.getUIContralType() == UIContralType.RadioBtn) {
                    doc = doc.replace("@" + item.getKeyOfEn(), this.GetRequestVal("RB_" + item.getKeyOfEn()));
                    continue;
                }
            }
        }

        ///#endregion 替换参数变量.

        doc = bp.wf.Glo.DealExp(doc, en, null); //替换里面的内容.


        ///#region 开始执行SQLs.
        try {
            doc += "&MethodName=" + func.getMethodID();
            DataType.ReadURLContext(doc, 99999);
            if (func.getMsgSuccess().equals("")) {
                func.setMsgSuccess("执行成功.");
            }

            return func.getMsgSuccess();
        } catch (RuntimeException ex) {
            String de = DealException(ex, doc);
            if (DataType.IsNullOrEmpty(de) == false)
            {
                return de;
            }
            if (func.getMsgErr().equals("")) {
                func.setMsgErr("执行失败.");
            }

            return "err@" + func.getMsgErr() + " @ " + ex.getMessage();
        }

        ///#endregion 开始执行SQLs.

    }


    ///#region 单据处理.

    public final String GL_DictRefBill() throws Exception {
        String billID = this.GetRequestVal("Tag1");
        String dictFrmID = this.getFrmID();//  this.GetRequestVal("DictFrmID");
        String refDictNo = this.GetRequestVal("RefDictNo");
        String refDictName = this.GetRequestVal("RefDictName");


        //获得她的值.
        MapData md = new MapData(dictFrmID);
        String dictEnNo = DBAccess.RunSQLReturnString("SELECT BillNo FROM " + md.getPTable() + " WHERE OID=" + this.getWorkID());

        GEEntitys dtls = new GEEntitys(billID);
        QueryObject qo = new QueryObject(dtls);
        qo.AddWhere(refDictNo, dictEnNo);
        qo.addAnd();
        qo.AddWhere("BillState", ">", 0);
        qo.DoQuery();


        //生成数据.
        DataSet ds = new DataSet();
        ds.Tables.add(dtls.ToDataTableField());

        //构造显示的列.
        MapAttrs attrs = new MapAttrs();
        attrs.Retrieve("FK_MapData", billID, "GroupID,Idx");
        DataTable dtCol = new DataTable();
        dtCol.Columns.Add("Key");
        dtCol.Columns.Add("Name");
        dtCol.Columns.Add("IsShow");
        for (MapAttr item : attrs.ToJavaList())
        {
            if (item.getUIVisible() == false)
                continue;
            if (item.getKeyOfEn().equals(refDictNo)==true)
                continue;
            if (item.getKeyOfEn().equals(refDictName) == true)
                continue;
            switch (item.getKeyOfEn())
            {
                case "FID":
                case "OrgNo":
                case "OID":
                case "Rec":
                case "AtPara":
                case "Name":
                    continue;
                default:
                    break;
            }

            DataRow dr = dtCol.NewRow();
            dr.setValue(0, item.getKeyOfEn().toString());
            dr.setValue(1, item.getName());

            if (item.getUIVisible())
                dr.setValue(2, 1);
            else
                dr.setValue(2, 0);

            dtCol.Rows.add(dr);
        }
        ds.Tables.add(dtCol);
        return bp.tools.Json.ToJson(ds);
    }

    /**
     * 维度转换
     * @return
     * @throws Exception
     */
    public String D2_Init() throws Exception {
        //把json转化datatable.
        String json = this.GetRequestVal("json");
        DataTable mydt = bp.tools.Json.ToDataTable(json);

        String clo1 = mydt.Columns.get(0).ColumnName;
        String clo2 = mydt.Columns.get(2).ColumnName;
        //移除
        mydt.Columns.remove(clo1);
        mydt.Columns.remove(clo2);

        //转换2D模式.
        DataTable dt2d = bp.tools.PubGlo.DataTable2D(mydt);
        return bp.tools.Json.ToJson(dt2d);
    }
    // #region 单据处理.
    public String MyBill_CreateCheckFlowNo() throws Exception {
        FrmBill md = new FrmBill(this.getFrmID());
        if (md.getBillCheckModel().equals("ByFlowNo") == false)
            md.SetValByKey("BillCheckModel", "ByFlowNo");

        //检查流程编号.
        String flowNo = md.getBillCheckTag();
        if (DataType.IsNullOrEmpty(flowNo) == true)
        {
            //   BP.WF.CCFlowAPI.cre
            flowNo = "";
        }
        return flowNo;
    }
    /// <summary>
    /// 发起的单据
    /// </summary>
    /// <returns></returns>
    public String DB_StartDicts()
    {
        return bp.tools.Json.ToJson(bp.ccbill.Dev2Interface.DB_StartDicts());
    }

    public String MyDict_ToolBarInit_EntityNoName() throws Exception {

        //  string billState = en.GetValStrByKey("EntityState");
        FrmDict fm = new FrmDict(this.getFrmID());

        String sql = "";
        sql = "SELECT EntityState,RecNo FROM " + fm.getPTable() + " WHERE No='" + this.getNo() + "'";

        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        if (dt.Rows.size()==0)
            throw new Exception("数据已经不存在:"+sql);
        //返回json.
        Hashtable ht = new Hashtable();
        ht.put("EntityState", dt.Rows.get(0).getValue(0).toString());
        ht.put("RecNo", dt.Rows.get(0).getValue(1).toString());

        int dictState = Integer.parseInt( dt.Rows.get(0).getValue(0).toString());

        int NewEnable = 0;
        int SaveEnable = 0;
        int DeleteEnable = 0;
        int FilingDoneEnable = 0;
        int FilingUnEnable = 0;

        //空白状态.
        if (dictState == 0)
        {
            NewEnable = 1; //新建.
            SaveEnable = 1; //保存.
            DeleteEnable = 1; //删除.
            FilingDoneEnable = 0; //归档.
            FilingUnEnable = 0; //撤销归档.
        }

        //草稿.
        if (dictState == 1)
        {
            NewEnable = 1; //新建.
            SaveEnable = 1; //保存.
            DeleteEnable = 1; //删除.
            FilingDoneEnable = 0; //归档.
            FilingUnEnable = 0; //撤销归档.
        }

        //编辑中.
        if (dictState == 2)
        {
            NewEnable = 1; //新建.
            SaveEnable = 1; //保存.
            DeleteEnable = 1; //删除.
            FilingDoneEnable = 1; //归档.
            FilingUnEnable = 0; //撤销归档.
        }

        //归档.
        if (dictState == 3)
        {
            NewEnable = 1; //新建.
            SaveEnable = 0; //保存.
            DeleteEnable = 0; //删除.
            FilingDoneEnable = 0; //归档.
            FilingUnEnable = 1; //撤销归档.
        }

        //    #region 整理参数
        DBRoles rols = new DBRoles();
        rols.Retrieve("FrmID", this.getFrmID());
        String mydepts = "" + WebUser.getDeptNo() + ","; //我的部门.
        String mystas = ""; //我的角色.
        DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.getUserID() + "'");
        for (DataRow dr : mydeptsDT.Rows)
        {
            mydepts += dr.getValue(0).toString() + ",";
            mystas += dr.getValue(1).toString() + ",";
        }
        //    #endregion 整理参数

        if (NewEnable == 1 && CheckRoles(rols, "RecNew", mydepts, mystas) == false)
            NewEnable = 0;

        if (SaveEnable == 1 && CheckRoles(rols, "RecSave", mydepts, mystas) == false)
            SaveEnable = 0;

        if (DeleteEnable == 1 && CheckRoles(rols, "RecDelete", mydepts, mystas) == false)
            DeleteEnable = 0;

        if (FilingDoneEnable == 1 && CheckRoles(rols, "RecFiling", mydepts, mystas) == false)
            FilingDoneEnable = 0;

        if (FilingUnEnable == 1 && CheckRoles(rols, "RecFiling", mydepts, mystas) == false)
            FilingUnEnable = 0;

        ht.put("NewEnable", String.valueOf(NewEnable));
        ht.put("SaveEnable", String.valueOf(SaveEnable));
        ht.put("DeleteEnable", String.valueOf(DeleteEnable));
        ht.put("FilingDoneEnable",String.valueOf( FilingDoneEnable));
        ht.put("FilingUnEnable", String.valueOf(FilingUnEnable));
        return Json.ToJson(ht);
    }
    /**
     * 创建空白的WorkID.
     *
     * @return
     */
    public final String MyBill_CreateBlankBillID() throws Exception {

       // #region 检查一下单据发起的模式.
        FrmBill md = new FrmBill(this.getFrmID());
        if (md.getBillCheckModel().equals("ByFlowNo") == true)
        {
            String flowNo = md.getBillCheckTag();
            int workID = (int) bp.wf.Dev2Interface.Node_CreateBlankWork(flowNo);

            GenerWorkFlow gwf = new GenerWorkFlow(workID);

            GEEntityOID geBill = new GEEntityOID(this.getFrmID());
            geBill.setOID(workID);
            if (geBill.RetrieveFromDBSources() == 0)
            {
                geBill.SetValByKey("Title", gwf.getTitle());
                geBill.SetValByKey("BillNo", gwf.getBillNo());
                geBill.SetValByKey("BillState", (int)BillState.Checking.getValue());

                geBill.SetValByKey("DeptNo", WebUser.getDeptName());
                geBill.SetValByKey("Starter", WebUser.getNo());
                geBill.SetValByKey("StarterName", WebUser.getName());

                geBill.Insert();
            }
            else
            {
                geBill.SetValByKey("Title", gwf.getTitle());
                geBill.SetValByKey("BillNo", gwf.getBillNo());
                geBill.SetValByKey("BillState", (int)BillState.Checking.getValue());

                geBill.SetValByKey("DeptNo", WebUser.getDeptName());
                geBill.SetValByKey("Starter", WebUser.getNo());
                geBill.SetValByKey("StarterName", WebUser.getName());

                geBill.Update();
            }
            //设置参数.
            bp.wf.Dev2Interface.Flow_SetFlowParas(flowNo, workID, "@BillFrmID=" + this.getFrmID());
            return workID + "@" + gwf.getFlowNo();
        }
         //   #endregion 检查一下单据发起的模式.

        String PFrmID = this.GetRequestVal("PFrmID");
        String pWorkID = this.GetRequestVal("PWorkID");
        if (DataType.IsNullOrEmpty(pWorkID) == true) {
            pWorkID = "0";
        }
        int billOID = 0;
        try{
            billOID = (int) Dev2Interface.CreateBlankBillID(this.getFrmID(), WebUser.getNo(), null, PFrmID, Long.parseLong(pWorkID));
        }catch(Exception e){
            billOID = (int) Dev2Interface.CreateBlankBillID(this.getFrmID(), WebUser.getNo(), null, PFrmID, Long.parseLong(pWorkID));
        }
        return String.valueOf(billOID);
    }
    /**
     * 返回单据的状态
     * @return
     */
    public String MyDict_ToolBarInit() throws Exception {
        MapData md = new MapData(this.getFrmID());
        if (md.getEntityType() == EntityType.FrmEntityNoName)
            return MyDict_ToolBarInit_EntityNoName();

        GEEntity en = new GEEntity(this.getFrmID(), this.getWorkID());
        String billState = en.GetValStrByKey("BillState");

        FrmDict fm = new FrmDict(this.getFrmID());

        String wfstate = "-1";
        String PTbale = fm.getPTable();
        String sql = "SELECT -1 as WFState,BillState,Starter FROM " + PTbale + " WHERE OID=" + this.getWorkID();
        if (DBAccess.IsExitsTableCol(PTbale, "WFState") == true)
        {
            sql = "SELECT WFState,BillState,Starter FROM "+PTbale+" WHERE OID="+this.getWorkID();
        }

        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        String starter =  dt.Rows.get(0).getValue(2).toString();
        //返回json.
        Hashtable ht = new Hashtable();
        ht.put("WFState", dt.Rows.get(0).getValue(0).toString());
        ht.put("BillState", dt.Rows.get(0).getValue(1).toString());
        ht.put("Starter", starter);

        int dictState = Integer.parseInt(dt.Rows.get(0).getValue(1).toString());
        String wfStateStr = dt.Rows.get(0).getValue(0).toString();
        if(DataType.IsNullOrEmpty(wfStateStr))
            wfStateStr="-1";
        int wfState =Integer.parseInt(wfStateStr);
        if (dictState == 100) dictState = 3;
        if (wfState == 3) dictState = 3;

        Integer NewEnable = 0;
        Integer SaveEnable = 0;
        Integer DeleteEnable = 0;
        Integer FilingDoneEnable = 0;
        Integer FilingUnEnable = 0;

        //空白状态.
        if (dictState == 0)
        {
            NewEnable = 1; //新建.
            SaveEnable = 1; //保存.
            DeleteEnable = 1; //删除.
            FilingDoneEnable = 0; //归档.
            FilingUnEnable = 0; //撤销归档.
        }

        //草稿.
        if (dictState == 1)
        {
            NewEnable = 1; //新建.
            SaveEnable = 1; //保存.
            DeleteEnable = 1; //删除.
            FilingDoneEnable = 0; //归档.
            FilingUnEnable = 0; //撤销归档.
        }

        //编辑中.
        if (dictState == 2)
        {
            NewEnable = 1; //新建.
            SaveEnable = 1; //保存.
            DeleteEnable = 1; //删除.
            FilingDoneEnable = 1; //归档.
            FilingUnEnable = 0; //撤销归档.
        }

        //归档.
        if (dictState == 3)
        {
            NewEnable = 1; //新建.
            SaveEnable = 0; //保存.
            DeleteEnable = 0; //删除.
            FilingDoneEnable = 0; //归档.
            FilingUnEnable = 1; //撤销归档.
        }


        ///#region 整理参数
        DBRoles rols = new DBRoles();
        rols.Retrieve("FrmID", this.getFrmID());
        String mydepts = "" + WebUser.getDeptNo() + ","; //我的部门.
        String mystas = ""; //我的角色.
        DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.getUserID() + "'");
        for (DataRow dr : mydeptsDT.Rows)
        {
            mydepts += dr.getValue(0).toString() + ",";
            mystas += dr.getValue(1).toString() + ",";
        }
        ///#endregion 整理参数


        if (NewEnable == 1 && CheckRoles(rols,"RecNew",mydepts,mystas,starter)==false)
            NewEnable = 0;

        if (SaveEnable == 1 && CheckRoles(rols, "RecSave", mydepts, mystas,starter) == false)
            SaveEnable = 0;

        if (DeleteEnable == 1 && CheckRoles(rols, "RecDelete", mydepts, mystas,starter) == false)
            DeleteEnable = 0;

        if (FilingDoneEnable == 1 && CheckRoles(rols, "RecFiling", mydepts, mystas,starter) == false)
            FilingDoneEnable = 0;

        if (FilingUnEnable == 1 && CheckRoles(rols, "RecFiling", mydepts, mystas,starter) == false)
            FilingUnEnable = 0;

        ht.put("NewEnable", NewEnable.toString());
        ht.put("SaveEnable", SaveEnable.toString());
        ht.put("DeleteEnable", DeleteEnable.toString());
        ht.put("FilingDoneEnable", FilingDoneEnable.toString());
        ht.put("FilingUnEnable", FilingUnEnable.toString());
        return bp.tools.Json.ToJson(ht);
    }

    /**
     * 创建空白的DictID.
     *
     * @return
     */
    public final String MyDict_CreateBlankDictID() throws Exception {
        MapData md = new MapData(this.getFrmID());

        if (md.getEntityType() == EntityType.FrmEntityNoName)
            return Dev2Interface.CreateBlankEntityNoName(this.getFrmID(), null, null);
        else
            return String.valueOf(bp.ccbill.Dev2Interface.CreateBlankDictID(this.getFrmID(), null, null));
    }

    /**
     * 执行保存
     *
     * @return
     */
    public final String MyBill_SaveIt() throws Exception {
        //创建entity 并执行copy方法.
        GEEntityOID rpt = new GEEntityOID(this.getFrmID(), this.getWorkID());
        Attrs attrs = rpt.getEnMap().getAttrs();

        try {
            Hashtable ht = this.GetMainTableHT();
            for (Object item : ht.keySet()) {
                if (item == null)
                    continue;
                rpt.SetValByKey(item.toString(), ht.get(item));
            }
            rpt.SetValByKey("OrgNo",WebUser.getOrgNo());
            // log
            JSONObject log = new JSONObject();
            log.put("frmID", this.getFrmID());
            log.put("workID", this.getWorkID());
            log.put("params", JSONObject.fromObject(ht));
            bp.sys.base.Glo.WriteUserLog(log.toString(), "低代码单据数据保存");
            // end
        } catch (RuntimeException ex) {
            return "err@方法：MyBill_SaveIt错误，在执行  GetMainTableHT 期间" + ex.getMessage();
        }
        //执行保存.
        try {
            rpt.setOID(this.getWorkID());
            //rpt.SetValByKey("BillState", BillState.Editing.getValue());
            rpt.Update();
            String str = bp.ccbill.Dev2Interface.SaveBillWork(this.getFrmID(), this.getWorkID());
            return str;
        } catch (RuntimeException ex) {
            return "err@方法：MyBill_SaveIt 错误，在执行 SaveWork 期间出现错误:" + ex.getMessage();
        }
    }

    public final String MyBill_Submit() throws Exception {
        //执行保存.
        GEEntityOID rpt = new GEEntityOID(this.getFrmID(), this.getWorkID());
        Hashtable ht = GetMainTableHT();
        for (Object item : ht.keySet()) {
            if (item == null)
                continue;
            rpt.SetValByKey(item.toString(), ht.get(item));
        }

        rpt.setOID(this.getWorkID());
        rpt.SetValByKey("BillState", BillState.FrmOver.getValue());
        rpt.Update();

        String str = bp.ccbill.Dev2Interface.SubmitWork(this.getFrmID(), this.getWorkID());
        return str;
    }
    /// <summary>
    /// 撤销归档
    /// </summary>
    /// <returns></returns>
    public final String MyBill_ArchiveUn() throws Exception {
        FrmBill fb = new FrmBill(this.getFrmID());

        GenerBill gb = new GenerBill();
        gb.setWorkID(this.getWorkID());
        int i = gb.RetrieveFromDBSources();
        if (i == 0)
            return "err@信息丢失";

        //设置为归档状态.
        gb.setBillState(BillState.Editing);
        gb.Update();
        GEEntityOID rpt = new GEEntityOID(this.getFrmID(), this.getWorkID());
        rpt.setOID(this.getWorkID());
        rpt.SetValByKey("BillState", BillState.Editing.getValue());
        rpt.Update();

        bp.ccbill.Dev2Interface.WriteTrack(gb.getFrmID(), String.valueOf(this.getWorkID()), FrmActionType.UnArchive, "撤销归档成功.");
        return "撤销归档成功";
    }
    /// <summary>
    /// 预置审核人,这个时候，仅仅是把审核人暂存到参数里.
    /// 如果提交的时候，就把这些审核人取出来，发起流程.
    /// </summary>
    /// <returns></returns>
    public String MyBill_PreplaceChecker() throws Exception {
        return bp.ccbill.Dev2Interface.Bill_PreplaceChecker(this.getWorkID(), this.getRefNo());
    }
    /// <summary>
    /// 撤销发送
    /// </summary>
    /// <returns></returns>
    public String MyBill_UnSend() throws Exception {
        //执行保存.
        GEEntityOID rpt = new GEEntityOID(this.getFrmID(), this.getWorkID());
        Hashtable ht = GetMainTableHT();
        for (Object item : ht.keySet())
        {
            rpt.SetValByKey((String) item, ht.get(item));
        }

        rpt.setOID(this.getWorkID());
        //    rpt.SetValByKey("BillState", (int)BillState.Editing);
        rpt.Update();

        String str = bp.ccbill.Dev2Interface.Bill_UnSend(this.getFrmID(), this.getWorkID());
        return str;
    }

    public final String MyBill_RebackFlow() throws Exception {
        //处理单据审核回滚.
        GenerBill gb = new GenerBill(this.getWorkID());
        MapData md = new MapData(gb.getFrmID());
        GEEntity ge = new GEEntity(gb.getFrmID(), this.getWorkID());
        //执行回滚
        String frmEvent = ExecEvent.DoFrm(md, EventListFrm.Reback, ge);
        if (DataType.IsNullOrEmpty(frmEvent) == false && frmEvent.contains("err@") == true)
            return frmEvent;

        gb.setBillState(BillState.ReturnSta);
        gb.SetValByKey("SDTOfFlow", "无");
        gb.SetValByKey("SDTOfNode", "无");
        gb.SetValByKey("Idx", 0);
        gb.SetValByKey("CurrIdx", 0); //当前的步骤.

        //设置当前处理人.
        gb.SetValByKey("CurrCheckerNos", gb.getStarter());
        gb.SetValByKey("CurrCheckerNames", gb.getStarterName());

        String info2 = "回滚审批成功:发起人[" + gb.getStarter() + "," + gb.getStarterName() + "] \t\n 原因:" + this.getMsg()
                + "\t\n 执行人:" + WebUser.getNo() + "," + WebUser.getName() + " 日期:" + DataType.getCurrentDateTime();
        gb.SetValByKey("Msg", info2); //消息.
        gb.Update();

        GenerWorkers wks = new GenerWorkers();
        wks.Retrieve("WorkID", this.getWorkID(), "Idx");
        for (GenerWorker wk : wks.ToJavaList())
        {
            if (wk.getIdx() == 100)
            {
                wk.Delete();
                continue; //删除最后的一个通知节点.
            }
            wk.setRDT(DataType.getCurrentDateTime());
            if (wk.getIdx() == 0)
            {
                wk.setPassSta(PassSta.ReturnSta.getValue()); //退回状态.
                wk.Update();
            }
            else
            {
                wk.setPassSta(PassSta.UnPass.getValue()); //未通过.
            }
            wk.setSendDT("无");
            wk.Update();
        }
        return info2 + frmEvent ;
    }
    /**
     删除按钮是否可用?
     @return
     */
    public final String MyBillBtnsEnable_Delete() throws Exception {
        DBRoles rls = new DBRoles();
        rls.Retrieve("FrmID", this.getFrmID(), "DBRole", "RecDelete", "IsEnable", 1);
        GenerBill gb = new GenerBill(this.getWorkID());
        //首先检查默认的,自己紧急删除自己的.
        if (rls.size() == 0 && gb.getStarter().equals(WebUser.getNo()) == true)
        {
            return "1";
        }

        for (DBRole rl : rls.ToJavaList())
        {
            if (rl.getMarkID().equals("None") == true)
            {
                return "1";
            }

            if (rl.getMarkID().equals("SelfOnly") == true && gb.getStarter().equals(WebUser.getNo()) == true)
            {
                return "1";
            }

            if (rl.getMarkID().equals("DeptLeader") == true)
            {
                String sql = "SELECT Leader FROM Port_Dept WHERE No IN (SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp='" + WebUser.getNo() + "')";
                String empNo = DBAccess.RunSQLReturnStringIsNull(sql, "");
                if (empNo.equals(WebUser.getNo()) == true)
                {
                    return "1";
                }
            }

            if (rl.getMarkID().equals("ByStations") == true)
            {
                String sql = "SELECT FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.getNo() + "'";
                DataTable dt = DBAccess.RunSQLReturnTable(sql);
                String vals = "," + rl.getDocs() + ",";
                for (DataRow dr : dt.Rows)
                {
                    if (vals.contains("," + dr.get(0).toString() + ",") == true)
                    {
                        return "1";
                    }
                }
            }

            if (rl.getMarkID().equals("ByDepts") == true)
            {
                String sql = "SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp='" + WebUser.getNo() + "'";
                DataTable dt = DBAccess.RunSQLReturnTable(sql);
                String vals = "," + rl.getDocs() + ",";
                for (DataRow dr : dt.Rows)
                {
                    if (vals.contains("," + dr.get(0).toString() + ",") == true)
                    {
                        return "1";
                    }
                }
            }

            if (rl.getMarkID().equals("ByEmps") == true)
            {
                String vals = "," + rl.getDocs() + ",";
                if (vals.contains("," + WebUser.getNo() + ",") == true)
                {
                    return "1";
                }
            }

            if (rl.getMarkID().equals("Adminer") == true && WebUser.getNo().equals("admin"))
            {
                return "1";
            }

            if (rl.getMarkID().equals("Admin2") == true && WebUser.getIsAdmin() == true)
            {
                return "1";
            }
        }
        return "0";
    }
    /// <summary>
    /// 是否可以发起流程？
    /// </summary>
    /// <returns></returns>
    public final String MyBillBtnsEnable_FlowEtcIsCanStartFlow()
    {
        String sql = "";
        return "";
    }
    /**
     提交审核的权限

     @return
     */
    public final String MyBillBtnsEnable_SubmitCheck() throws Exception {
        DBRoles rls = new DBRoles();
        rls.Retrieve("FrmID", this.getFrmID(), "DBRole", "SubmitCheck", "IsEnable", 1);
        GenerBill gb = new GenerBill(this.getWorkID());
        //首先检查默认的,自己紧急删除自己的.
        if (rls.size() == 0 && gb.getStarter().equals(WebUser.getNo()) == true)
        {
            return "1";
        }

        for (DBRole rl : rls.ToJavaList())
        {
            if (rl.getMarkID().equals("None") == true)
            {
                return "1";
            }

            if (rl.getMarkID().equals("SelfOnly") == true && gb.getStarter().equals(WebUser.getNo()) == true)
            {
                return "1";
            }

            if (rl.getMarkID().equals("DeptLeader") == true)
            {
                String sql = "SELECT Leader FROM Port_Dept WHERE No IN (SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp='" + WebUser.getNo() + "')";
                String empNo = DBAccess.RunSQLReturnStringIsNull(sql, "");
                if (empNo.equals(WebUser.getNo()) == true)
                {
                    return "1";
                }
            }

            if (rl.getMarkID().equals("ByStations") == true)
            {
                String sql = "SELECT FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.getNo() + "'";
                DataTable dt = DBAccess.RunSQLReturnTable(sql);
                String vals = "," + rl.getDocs() + ",";
                for (DataRow dr : dt.Rows)
                {
                    if (vals.contains("," + dr.get(0).toString() + ",") == true)
                    {
                        return "1";
                    }
                }
            }
            if (rl.getMarkID().equals("ByDepts") == true)
            {
                String sql = "SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp='" + WebUser.getNo() + "'";
                DataTable dt = DBAccess.RunSQLReturnTable(sql);
                String vals = "," + rl.getDocs() + ",";
                for (DataRow dr : dt.Rows)
                {
                    if (vals.contains("," + dr.get(0).toString() + ",") == true)
                    {
                        return "1";
                    }
                }
            }
            if (rl.getMarkID().equals("ByEmps") == true)
            {
                String vals = "," + rl.getDocs() + ",";
                if (vals.contains("," + WebUser.getNo() + ",") == true)
                {
                    return "1";
                }
            }
            if (rl.getMarkID().equals("Adminer") == true && WebUser.getNo().equals("admin"))
            {
                return "1";
            }
            if (rl.getMarkID().equals("Admin2") == true && WebUser.getIsAdmin() == true)
            {
                return "1";
            }
        }
        return "0";
    }

    public String WriteTrack() throws Exception {
        bp.ccbill.Dev2Interface.WriteTrack(this.getFrmID(), this.getPKVal().toString(), "Info", this.getMsg());
        return "写入成功.";
    }
    public String MyBill_Send() throws Exception {
        return bp.ccbill.Dev2Interface.MyBill_Send(this.getFrmID(), this.getWorkID(), this.getRefNo());
    }
    public String MyBill_ReturnWork() throws Exception {
        String msg = this.GetRequestVal("Msg");
        String idx = this.GetRequestVal("ReturnToIdx");
        if (DataType.IsNullOrEmpty(idx) == true)
            idx = "0"; //退回到开始节点.
        return bp.ccbill.Dev2Interface.MyBill_ReturnWork(this.getWorkID(), Integer.parseInt(idx), msg);
    }
    /// <summary>
    /// 开始节点读取通知内容结束.
    /// </summary>
    /// <returns></returns>
    public String MyBill_StarterReadOver() throws Exception {
        GenerBill gb = new GenerBill(this.getWorkID());
        if (gb.getBillState() != BillState.FlowOver)
            return "err@数据错误，当前不是完成状态.";
        DBAccess.RunSQL("DELETE FROM Frm_GenerWorker WHERE WorkID="+this.getWorkID());
        String str= gb.GetValStrByKey("Msg"); //提示完成信息.
        if (DataType.IsNullOrEmpty(str) == true)
            return "单据简易审核已经结束.";
        return str;
    }
    /// <summary>
    /// 撤销提交-OLD 应该深处嗲
    /// </summary>
    /// <returns></returns>
    public String MyBill_UnSubmit() throws Exception {
        //执行保存.
        GEEntityOID rpt = new GEEntityOID(this.getFrmID(), this.getWorkID());
        Hashtable ht = GetMainTableHT();
        for (Object item : ht.keySet())
        {
            rpt.SetValByKey(item.toString(), ht.get(item));
        }

        rpt.setOID(this.getWorkID());
        rpt.SetValByKey("BillState", BillState.Editing);
        rpt.Update();

        String str = bp.ccbill.Dev2Interface.UnSubmitWork(this.getFrmID(), this.getWorkID());
        return str;
    }
    /// <summary>
    /// 开始节点初始化审核信息
    /// </summary>
    /// <returns></returns>
    public String MyBill_CheckerInit() throws Exception {
        FrmBill frm = new FrmBill(this.getFrmID());

        if (DataType.IsNullOrEmpty(frm.getBillCheckModel()) == true || frm.getBillCheckModel().equals("None"))
            return "err@当前表单不需要审核.";

        if (frm.getBillCheckModel().equals("SelfCheck") == true)
            return "info@SelfCheck,当前是自定义审核路径，请打开自定义审核路径页面.";
        if (frm.getBillCheckModel().equals("ByFlowNo") == true)
            return "info@ByFlowNo,当前是按照流程审核,请打开流程." + frm.getBillCheckTag();

        //如果是按照API创建的,设置到审核队列，不允许修改.
        if (frm.getBillCheckModel().equals("ByAPI") == true)
        {
            GenerBill gb = new GenerBill(this.getWorkID());
            String emps = gb.GetParaString("PreplaceChecker"); //获取预置的处理人.
            if (DataType.IsNullOrEmpty(emps) == true)
                return "err@当前简易审核按照API设置的，但是没有获取到，预置的审核人员。";
            //设置处理人.
            String str = Dev2Interface.Bill_CheckerGoToOrder(this.getFrmID(), this.getWorkID(), emps);
            bp.ccbill.Dev2Interface.WriteTrack(this.getFrmID(), String.valueOf(this.getWorkID()), "Info", this.getMsg() + " 启动信息:" + str);
            //执行事件.
            MapData md = new MapData(gb.getFrmID());
            GEEntityOID ge = new GEEntityOID(gb.getFrmID(), this.getWorkID());
            String frmEvent = ExecEvent.DoFrm(md, EventListFrm.CheckStart, ge);
            return "启动成功:" + str + "." + frmEvent;

        }
        //如果是按照设置的人员，不允许修改.
        if (frm.getBillCheckModel().equals("BySettingEmpNos") == true)
        {
            String empNos = "";

            for (int index = 1; index < 9; index++)
            {
                String emps = DBAccess.RunSQLReturnStringIsNull("SELECT CheckEmpNo" + index + " FROM Sys_MapData WHERE No='" + this.getFrmID() + "'", null);
                if (emps == null || DataType.IsNullOrEmpty(emps) == true)
                    break;
                empNos += "," + emps;
            }
            if (DataType.IsNullOrEmpty(empNos) == true)
                return "err@当前简易审核按照预置人员设置的，但是没有获取到，预置的审核人员，请在单据简易审核中维护审批人员路径。";

            //设置处理人.
            String str = Dev2Interface.Bill_CheckerGoToOrder(this.getFrmID(), this.getWorkID(), empNos);
            bp.ccbill.Dev2Interface.WriteTrack(this.getFrmID(), String.valueOf(this.getWorkID()), "Info", this.getMsg() + " 启动信息:" + str);
            //执行事件.
            MapData md = new MapData(this.getFrmID());
            GEEntityOID ge = new GEEntityOID(this.getFrmID(), this.getWorkID());
            String frmEvent = ExecEvent.DoFrm(md, EventListFrm.CheckStart, ge);
            return str+frmEvent;
        }
        //如果是按照SQL创建的,设置到审核队列，不允许修改.
        if (frm.getBillCheckModel().equals("BySQL") == true)
        {
            String sql = frm.getBillCheckTag();
            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            String emps = "";
            for (DataRow dr : dt.Rows)
            emps += dr.get(0).toString() + ",";
            //sql= GlowWF
            //GenerBill gb = new GenerBill(this.getWorkID());
            //string emps = gb.GetParaString("PreplaceChecker"); //获取预置的处理人.
            //if (DataType.IsNullOrEmpty(emps) == true)
            //    return "err@当前简易审核按照API设置的，但是没有获取到，预置的审核人员。";
            //设置处理人.
            String str = Dev2Interface.Bill_CheckerGoToOrder(this.getFrmID(), this.getWorkID(), emps);
            bp.ccbill.Dev2Interface.WriteTrack(this.getFrmID(), String.valueOf(this.getWorkID()), "Info", this.getMsg() + " 启动信息:" + str);
            //执行事件.
            MapData md = new MapData(this.getFrmID());
            GEEntityOID ge = new GEEntityOID(this.getFrmID(), this.getWorkID());
            String frmEvent = ExecEvent.DoFrm(md, EventListFrm.CheckStart, ge);
            return str+""+ frmEvent;
        }

        return "err@没有判断的支持类型." + frm.getBillCheckModel();

    }
    /// <summary>
    /// 开始节点提交审核
    /// </summary>
    /// <returns></returns>
    public String MyBill_CheckSubmit() throws Exception {
        //执行保存.
        GEEntityOID rpt = new GEEntityOID(this.getFrmID(), this.getWorkID());
        Hashtable ht = GetMainTableHT();
        for (Object item : ht.keySet())
        {
            rpt.SetValByKey(item.toString(), ht.get(item));
        }

        rpt.setOID(this.getWorkID());
        rpt.SetValByKey("BillState", BillState.Editing);
        rpt.Update();

        String str = bp.ccbill.Dev2Interface.UnSubmitWork(this.getFrmID(), this.getWorkID());
        return str;
    }
    public String MyEntityNoName_SaveIt()
    {
        try
        {
            //执行保存.
            MapData md = new MapData(this.getFrmID());
            GEEntityNoName rpt = new GEEntityNoName(this.getFrmID(), this.getNo());
            //rpt = BP.Pub.PubClass.CopyFromRequest(rpt) as GEEntity;
            Hashtable ht = GetMainTableHT();

            for (Object item : ht.keySet()) {
                if (item == null)
                    continue;
                rpt.SetValByKey(item.toString(), ht.get(item));
            }
            // 处理变量，这个rpt2有可能会重新查询.
            GEEntityNoName rpt2 = new GEEntityNoName(this.getFrmID());
            rpt2.setRow((Row) rpt.getRow().clone());
            //执行保存前事件
            ExecEvent.DoFrm(md, EventListFrm.SaveBefore, rpt2, null);

            rpt.setNo(this.getNo());
            rpt.SetValByKey("EntityState", 2); //设置编辑中.
            rpt.Update();

            //执行保存后事件
            ExecEvent.DoFrm(md, EventListFrm.SaveAfter, rpt, null);
            return "保存成功.";
        }
        catch (Exception ex)
        {
            return "err@" + ex.getMessage();
        }
    }
    /**
     * 执行保存
     *
     * @return
     */
    public final String MyDict_SaveIt() throws Exception {
        //执行保存.

        MapData md = new MapData(this.getFrmID());
        GEEntityOID rpt = new GEEntityOID(this.getFrmID(), this.getWorkID());
        //rpt = BP.Pub.PubClass.CopyFromRequest(rpt) as GEEntity;
        Hashtable ht = GetMainTableHT();
        for (Object item : ht.keySet()) {
            if (item == null)
                continue;
            rpt.SetValByKey(item.toString(), ht.get(item));
        }
        // log
        JSONObject log = new JSONObject();
        log.put("frmID", this.getFrmID());
        log.put("workID", this.getWorkID());
        log.put("params", JSONObject.fromObject(ht));
        bp.sys.base.Glo.WriteUserLog(log.toString(), "低代码实体数据保存");
        // end
        // 处理变量，这个rpt2有可能会重新查询.
        GEEntityOID rpt2 = new GEEntityOID(this.getFrmID());
        rpt2.setRow((Row) rpt.getRow().clone());

        //执行保存前事件
        ExecEvent.DoFrm(md, EventListFrm.SaveBefore, rpt2, null);

        rpt.setOID(this.getWorkID());
        rpt.SetValByKey("BillState", BillState.Editing.getValue());
        rpt.Update();


        //执行保存后事件
        ExecEvent.DoFrm(md, EventListFrm.SaveAfter, rpt, null);
        return "保存成功.";
    }

    /**
     * 设置归档
     * @return
     * @throws Exception
     */
    public String MyDict_FilingDone() throws Exception {
        GEEntityOID rpt = new GEEntityOID(this.getFrmID(), this.getWorkID());
        rpt.setOID(this.getWorkID());
        rpt.SetValByKey("BillState", DictState.Filing.getValue()); //设置归档.
        rpt.Update();
        return "设置成功.";
    }
    public String MyEntityNoName_FilingDone() throws Exception {
        GEEntityNoName rpt = new GEEntityNoName(this.getFrmID(), this.getNo());
        rpt.SetValByKey("EntityState", DictState.Filing); //设置编辑中.
        rpt.Update();
        return "设置成功.";
    }
    public String MyEntityNoName_FilingDoneUn() throws Exception {
        GEEntityNoName rpt = new GEEntityNoName(this.getFrmID(), this.getNo());
        rpt.SetValByKey("EntityState", 2); //设置编辑中.
        rpt.Update();
        return "设置成功.";
    }

    public String MyEntityNoName_Delete() throws Exception {
        GEEntityNoName rpt = new GEEntityNoName(this.getFrmID(), this.getNo());
        rpt.SetValByKey("EntityState", -1); //设置删除状态.
        rpt.Update();
        return "设置成功.";
    }
    public String MyEntityTree_Deletes() throws Exception {
        return bp.ccbill.Dev2Interface.MyDict_DeleteDicts(this.getFrmID(), this.GetRequestVal("Nos"));
    }
    public String MyEntityNoName_Deletes() throws Exception {
        return bp.ccbill.Dev2Interface.MyEntityNoName_Deletes(this.getFrmID(), this.GetRequestVal("Nos"));
    }
    /**
     * 设置撤销归档 （状态改为编辑中）
     * @return
     * @throws Exception
     */
    public String MyDict_FilingDoneUn() throws Exception {
        GEEntityOID rpt = new GEEntityOID(this.getFrmID(), this.getWorkID());
        rpt.setOID(this.getWorkID());
        rpt.SetValByKey("BillState", DictState.Editing.getValue()); //设置编辑中.
        rpt.Update();
        return "设置成功.";
    }
  /*  public String MyDict_Turn2EntityNoName() throws Exception {
        MapData md = new MapData(this.getFrmID());
        md.setEntityType(EntityType.FrmEntityNoName);
        md.SetValByKey("EnPK", "No");
        md.Update();

        String mypk = md.getNo() + "_BillNo";
        MapAttr mapAttr = new MapAttr(mypk);
        mapAttr.SetValByKey("KeyOfEn", "No");
        mapAttr.Insert();
        mapAttr.setIdx(0);
        mapAttr.Update();

        mypk = md.getNo() + "_Title";
        mapAttr = new MapAttr(mypk);
        mapAttr.SetValByKey("KeyOfEn", "Name");
        mapAttr.Insert();
        mapAttr.setIdx(1);
        mapAttr.Update();

        mypk = md.getNo() + "_BillState";
        mapAttr = new MapAttr(mypk);
        mapAttr.SetValByKey("KeyOfEn", "EntityState");
        mapAttr.Insert();
        mapAttr.setIdx(1);
        mapAttr.Update();

        //删除描述字段.
        DBAccess.RunSQL("DELETE FROM " + md.getPTable() + " WHERE KeyOfEn IN ('Title','BillNo','OID') AND FK_MapData='" + this.getFrmID() + "'");

        //重新创建表. Name,No 字段.
        GEEntityNoName geEn = new GEEntityNoName(this.getFrmID());
        try
        {
            geEn.CheckPhysicsTable();
        }
        catch (Exception e)
        {
        }

        //删除PK.
        DBAccess.DropTablePK(md.getPTable());
        // DBAccess.CreateTableColumn(md.PTable, "");


        GEEntity rpt = new GEEntity(this.getFrmID(), this.getWorkID());
        rpt.setOID(this.getWorkID());
        rpt.Delete();
        return "删除成功.";
    }*/
    /**
     * 打印rtf
     * @return
     * @throws Exception
     */
    public String MyDict_PrintRTF() throws Exception {
        String frmID = this.GetRequestVal("FrmID");
        FrmDict frmDict = new FrmDict(frmID);
        //方法ID.
        String mID = this.GetRequestVal("Method");

        Method md = new Method();
        md.setNo(mID);
        md.RetrieveFromDBSources();
        FrmPrintTemplate temp = new FrmPrintTemplate();
        //判断文件是否存在
        String filepath = SystemConfig.getPathOfDataUser() + "CyclostyleFile/" + md.getNo()+".rtf";
        if(new File(filepath).exists()==false){
            FrmPrintTemplates templetes = new FrmPrintTemplates();
            templetes.Retrieve(FrmPrintTemplateAttr.FrmID, frmID, null);
            if(templetes.size()==1)
                temp = (FrmPrintTemplate)templetes.get(0) ;
            if(templetes.size() == 0)
                return "err@请上传打印模板";
            return templetes.ToJson("dt");
        }else{
            temp.setMyPK(md.getNo());
            int i = temp.RetrieveFromDBSources();
            String tempInfo = "@FilePath="+md.GetValStringByKey("WebPath")+"@FileName="+md.getNo()+"@FileExt=.rtf";
            temp.setTempFilePath(md.getNo()); //这里需要路径支持,
            temp.setFrmID(this.getFrmID());
            temp.setTemplateFileModel(TemplateFileModel.VSTOForWord); //word模式。
            temp.setHisPrintFileType(PrintFileType.forValue(md.GetValIntByKey("PrintFileType",1))); // = 1; //word模式。
            if( i== 0)
                temp.Insert();
            else
                temp.Update();

        }

        WF_WorkOpt opt = new WF_WorkOpt();
        String fileUrl = "";
        if(frmDict.getEntityType() != EntityType.FrmEntityNoName)
            fileUrl = opt.PrintDoc_FormDoneIt(null, this.getWorkID(), 0, this.getFrmID(), temp);
        else
            fileUrl = opt.PrintDoc_EntityNoNameDoneIt(this.getNo(),this.getFrmID(), temp) ;
        if(temp.getHisPrintFileType() == PrintFileType.Word)
            fileUrl = fileUrl.replace("file@word@","");
        if(temp.getHisPrintFileType() == PrintFileType.PDF)
            fileUrl = fileUrl.replace("file@pdf@","");

        Hashtable ht = new Hashtable();
        ht.put("FileName", md.getName());
        ht.put("FileUrl", fileUrl);
        return Json.ToJson(ht);
    }
    /**
     * 执行保存
     *
     * @return
     */
    public final String MyDict_Submit() throws Exception {
        //   return "err@不在支持提交功能.";
        //执行保存.
        MapData md = new MapData(this.getFrmID());
        GEEntityOID rpt = new GEEntityOID(this.getFrmID(), this.getWorkID());
        //rpt = BP.Pub.PubClass.CopyFromRequest(rpt) as GEEntity;

        Hashtable ht = GetMainTableHT();
        for (Object item : ht.keySet())
        {
            rpt.SetValByKey(item.toString(), ht.get(item));
        }

        //执行保存前事件
        ExecEvent.DoFrm(md, EventListFrm.SaveBefore, rpt, null);

        rpt.setOID(this.getWorkID());
        rpt.SetValByKey("BillState", BillState.FrmOver.getValue());
        rpt.Update();

        //执行保存后事件
        ExecEvent.DoFrm(md, EventListFrm.SaveAfter, rpt, null);
        return "归档成功.";
    }
    public String MyDict_Draft() throws Exception {
        //   return "err@不在支持提交功能.";
        //执行保存.
        MapData md = new MapData(this.getFrmID());
        GEEntityOID rpt = new GEEntityOID(this.getFrmID(), this.getWorkID());
        //rpt = BP.Pub.PubClass.CopyFromRequest(rpt) as GEEntity;

        Hashtable ht = GetMainTableHT();
        for (Object item : ht.keySet())
        {
            rpt.SetValByKey(item.toString(), ht.get(item));
        }

        //执行保存前事件
        ExecEvent.DoFrm(md, EventListFrm.SaveBefore, rpt, null);

        rpt.setOID(this.getWorkID());
        rpt.SetValByKey("BillState", BillState.Draft.getValue());
        rpt.Update();
        if(md.getEntityType() == EntityType.FrmBill){
            GenerBill gb = new GenerBill();
            gb.setWorkID(this.getWorkID());
            if (gb.RetrieveFromDBSources() == 1)
            {
                gb.setBillState(BillState.Draft);
                gb.Update();
            }
        }

        //执行保存后事件
        ExecEvent.DoFrm(md, EventListFrm.SaveAfter, rpt, null);
        return "设置草稿成功..";
    }
    public final String GetFrmEntitys() throws Exception {
        GEEntitys rpts = new GEEntitys(this.getFrmID());
        QueryObject qo = new QueryObject(rpts);
        qo.AddWhere("BillState", " != ", 0);
        qo.DoQuery();
        return bp.tools.Json.ToJson(rpts.ToDataTableField("dt"));
    }

    private Hashtable GetMainTableHT() throws Exception {
        Hashtable htMain = new Hashtable();
        for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
            if (key == null) {
                continue;
            }

            String myKey = key;
            String val = ContextHolderUtils.getRequest().getParameter(key);
            myKey = myKey.replace("TB_", "");
            myKey = myKey.replace("DDL_", "");
            myKey = myKey.replace("CB_", "");
            myKey = myKey.replace("RB_", "");
            try {
                val = URLDecoder.decode(val, "UTF-8");
            }catch (RuntimeException e) {
            }

            if (htMain.containsKey(myKey) == true) {
                htMain.put(myKey, val);
            } else {
                htMain.put(myKey, val);
            }
        }

        return htMain;
    }

    public final String MyBill_SaveAsDraft() throws Exception {
        String str = bp.ccbill.Dev2Interface.SaveBillWork(this.getFrmID(), this.getWorkID());
        return str;
    }

    /// <summary>
    /// 删除单据
    /// </summary>
    /// <returns></returns>
    public final String MyBill_Delete() throws Exception {
        return bp.ccbill.Dev2Interface.MyBill_Delete(this.getFrmID(), this.getWorkID());
    }

    public final String MyBill_Deletes() throws Exception {
        return bp.ccbill.Dev2Interface.MyBill_DeleteBills(this.getFrmID(), this.GetRequestVal("WorkIDs"));
    }


    //删除实体
    public final String MyDict_Delete() throws Exception {
        //return bp.ccbill.Dev2Interface.MyDict_Delete(this.getFrmID(), this.getWorkID());
        GEEntityOID rpt = new GEEntityOID(this.getFrmID(), this.getWorkID());
        rpt.setOID(this.getWorkID());
        rpt.Delete();
        return "删除成功.";
    }

    /**
     * 删除多个
     *
     * @return
     */
    public final String MyDict_Deletes() throws Exception {
        return bp.ccbill.Dev2Interface.MyDict_DeleteDicts(this.getFrmID(), this.GetRequestVal("WorkIDs"));
    }

    public final String MyEntityTree_Delete() throws Exception {
        return bp.ccbill.Dev2Interface.MyEntityTree_Delete(this.getFrmID(), this.GetRequestVal("BillNo"));
    }

    /**
     * 表单数据复制
     * @param frmID 表单ID
     * @param workid WorkID
     * @return 复制的主表数据
     */
    public static String FlowFrm_Copy(String frmID, long workid) throws Exception {
        //旧的WorkID
        long oldWorkID = workid;
        //复制主表数据（包括二进制）
        GEEntityOID en = new GEEntityOID(frmID, oldWorkID);
        en.SaveAsNew();
        //新的WorkID
        long newWorkID = en.getOID();

        //判断是否有从表，并进行复制
        MapDtls mds = new MapDtls(frmID);
        if(mds != null && mds.size() > 0)
        {
            //遍历从表s
            for(MapDtl mdtl : mds.ToJavaList())
            {
                //获取旧的从表数据
                GEDtls dtls = new GEDtls(mdtl.getNo());
                dtls.Retrieve(GEDtlAttr.RefPK, oldWorkID);

                //遍历旧的从表数据
                for(GEDtl gedtl : dtls.ToJavaList())
                {
                    //替换新的RefPK
                    gedtl.setRefPK(String.valueOf(newWorkID));
                    //执行插入，开始复制从表数据
                    gedtl.SaveAsNew();
                }
            }
        }

        return en.ToJson();
    }
    /**
     重新生成title.

     @return
     */
    public final String MyBill_GenerTitle() throws Exception {
        GenerBill gb = new GenerBill(this.getWorkID());
        FrmBill fb = new FrmBill(gb.getFrmID());
        GEEntity ge = new GEEntity(gb.getFrmID(), this.getWorkID());

        String title = Dev2Interface.GenerTitle(fb.getTitleRole(), ge);
        gb.SetValByKey("Title", title);
        gb.Update();

        ge.SetValByKey("Title", title);
        ge.Update();
        return "设置成功.";
    }
    /**
     设置标题.

     @return
     */
    public final String MyBill_SetTitle() throws Exception {
        GenerBill gb = new GenerBill(this.getWorkID());

        FrmBill fb = new FrmBill(gb.getFrmID());
        GEEntity ge = new GEEntity(gb.getFrmID(), this.getWorkID());

        gb.SetValByKey("Title", this.getMsg());
        gb.Update();
        ge.SetValByKey("Title", this.getMsg());
        ge.Update();

        return "设置成功.";
    }

    /**
     * 复制单据数据
     *
     * @return
     */
    public final String MyBill_Copy() throws Exception {
        return bp.ccbill.Dev2Interface.MyBill_Copy(this.getFrmID(), this.getWorkID());
    }

    ///#endregion 单据处理.

    ///#region 单据处理.GL

    /**
     * 待办
     * @return
     */
    public String DB_Todolist() throws Exception {
        //获得发起列表.
        DataTable dt = bp.ccbill.Dev2Interface.DB_Todolist(WebUser.getNo(), this.getFrmID());

        //返回组合
        return bp.tools.Json.ToJson(dt);
    }

    /**
     * 发起列表.
     * @return
     */
    public String DB_StartBills() throws Exception {
        //获得发起列表.
        DataTable dt = bp.ccbill.Dev2Interface.DB_StartBills(bp.web.WebUser.getNo());

        //返回组合
        return bp.tools.Json.ToJson(dt);
    }

    /**
     * 草稿列表
     * @return
     */
    public String DB_Draft() throws Exception {
        //草稿列表.
        DataTable dt = bp.ccbill.Dev2Interface.DB_Draft(this.getFrmID(), bp.web.WebUser.getNo());

        //返回组合
        return bp.tools.Json.DataTableToJson(dt, false);
    }
    public String DB_Recent() throws Exception {
        //获得发起列表.
        DataTable dt = bp.ccbill.Dev2Interface.DB_Recent(WebUser.getNo());
        //返回组合
        return bp.tools.Json.DataTableToJson(dt, false);
    }

    ///#endregion 单据处理.

    ///#region 获取查询条件
    public final String Search_ToolBar() throws Exception {
        DataSet ds = new DataSet();

        DataTable dt = new DataTable();

        //根据FrmID获取Mapdata
        MapData md = new MapData(this.getFrmID());
        if (md.getEntityType() == EntityType.DBList) {
            DBListDBSrc dbList = new DBListDBSrc(this.getFrmID());
            ds.Tables.add(dbList.ToDataTableField("Sys_DBList"));
        }
        //如果设置按照时间字段的月度，季度，年度查询数据，需要查询数据显示的最小年份
        if (md.getDTSearchWay() != DTSearchWay.None && md.GetParaInt("DTShowWay", 0) == 1) {
            GEEntity en = new GEEntity(this.getFrmID());
            try {
                String sql = "SELECT min(" + md.getDTSearchWay() + ") From " + en.getEnMap().getPhysicsTable();
                md.SetPara("DateShowYear", DBAccess.RunSQLReturnStringIsNull(sql, ""));
            } catch (RuntimeException e) {
                GEEntity rpt = new GEEntity(this.getFrmID());
                rpt.CheckPhysicsTable();
                String sql = "SELECT min(" + md.getDTSearchWay() + ") From " + en.getEnMap().getPhysicsTable();
                md.SetPara("DateShowYear", DBAccess.RunSQLReturnStringIsNull(sql, ""));
            }

        }
        ds.Tables.add(md.ToDataTableField("Sys_MapData"));
        //获取字段属性
        MapAttrs attrs = new MapAttrs(this.getFrmID());


        ///#region //增加枚举/外键字段信息
        dt.Columns.Add("Field", String.class);
        dt.Columns.Add("Name", String.class);
        dt.Columns.Add("Width", Integer.class);
        dt.TableName = "Attrs";
        //dt.PrimaryKey = new DataColumn[] {dt.Columns.get("Field")};
        ds.Tables.add(dt);
        String[] ctrls = md.getRptSearchKeys().split("\\*");
        DataTable dtNoName = null;

        MapAttr mapattr;
        DataRow dr = null;
        MapExts mapExts = new MapExts();
        QueryObject qo = new QueryObject(mapExts);
        qo.AddWhere("FK_MapData", this.getFrmID());
        qo.addAnd();
        qo.AddWhereIn("ExtType", "('ActiveDDLSearchCond','AutoFullDLLSearchCond')");
        qo.DoQuery();
        ds.Tables.add(mapExts.ToDataTableField("Sys_MapExt"));
        for (String ctrl : ctrls) {
            //增加判断，如果URL中有传参，则不进行此SearchAttr的过滤条件显示
            if (DataType.IsNullOrEmpty(ctrl) || !DataType.IsNullOrEmpty(this.GetRequestVal(ctrl))) {
                continue;
            }

            Object tempVar = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, ctrl);
            mapattr = tempVar instanceof MapAttr ? (MapAttr) tempVar : null;
            if (mapattr == null) {
                continue;
            }

            dr = dt.NewRow();
            dr.setValue("Field", mapattr.getKeyOfEn());
            dr.setValue("Name", mapattr.getName());
            dr.setValue("Width", mapattr.getUIWidth());
            dt.Rows.add(dr);

            Attr attr = mapattr.getHisAttr();
            if (mapattr == null) {
                continue;
            }

            if (attr.getKey().equals("FK_Dept") || attr.getKey().equals("DeptNo")) {
                continue;
            }

            if (attr.getItIsEnum() == true) {
                SysEnums ses = new SysEnums(mapattr.getUIBindKey());
                DataTable dtEnum = ses.ToDataTableField("dt");
                dtEnum.TableName = mapattr.getKeyOfEn();
                ds.Tables.add(dtEnum);
                continue;
            }
            if (attr.getItIsFK() == true) {
                Entities ensFK = attr.getHisFKEns();
                if (ensFK != null) {
                    ensFK.RetrieveAll();
                    DataTable dtEn = ensFK.ToDataTableField("dt");
                    dtEn.TableName = attr.getKey();
                    ds.Tables.add(dtEn);
                }


            }
            //绑定SQL的外键
            if (ds.contains(attr.getKey()) == false) {
                DataTable dtSQl = null;
                Object tempVar2 = mapExts.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLLSearchCond, MapExtAttr.AttrOfOper, attr.getKey());
                MapExt mapExt = tempVar2 instanceof MapExt ? (MapExt) tempVar2 : null;
                if (mapExt != null) {
                    Object tempVar3 = mapExt.getDoc();
                    String fullSQL = tempVar3 instanceof String ? (String) tempVar3 : null;
                    if (fullSQL == null) {
                        throw new RuntimeException("err@字段[" + attr.getKey() + "]下拉框AutoFullDLLSearchCond，没有配置SQL");
                    }

                    fullSQL = fullSQL.replace("~", "'");
                    fullSQL = bp.wf.Glo.DealExp(fullSQL, null, null);
                    dtSQl = DBAccess.RunSQLReturnTable(fullSQL);
                } else if (DataType.IsNullOrEmpty(attr.getUIBindKey()) == false) {
                    dtSQl = bp.pub.PubClass.GetDataTableByUIBineKey(attr.getUIBindKey(), null);
                }
                if (dtSQl != null) {
                    for (DataColumn col : dtSQl.Columns) {
                        String colName = col.ColumnName.toLowerCase();
                        switch (colName) {
                            case "no":
                            case "NO":
                                col.ColumnName = "No";
                                break;
                            case "name":
                            case "NAME":
                                col.ColumnName = "Name";
                                break;
                            case "parentno":
                            case "PARENTNO":
                                col.ColumnName = "ParentNo";
                                break;
                            default:
                                break;
                        }
                    }
                    dtSQl.TableName = attr.getKey();
                    ds.Tables.add(dtSQl);
                }
            }

        }


        //数据查询权限除只查看自己创建的数据外增加部门的查询条件
        SearchDataRole searchDataRole = SearchDataRole.forValue(md.GetParaInt("SearchDataRole", 0));
        if (searchDataRole != SearchDataRole.ByOnlySelf) {
            DataTable dd = GetDeptDataTable(searchDataRole, md);
            if (dd.Rows.size() == 0 && md.GetParaInt("SearchDataRoleByDeptStation", 0) == 1) {
                dd = GetDeptAndSubLevel();
            }
            if (dd.Rows.size() != 0) {
                //增加部门的查询条件
                if (dt.Rows.contains("FK_Dept") == false) {
                    dr = dt.NewRow();
                    dr.setValue("Field", "FK_Dept");
                    dr.setValue("Name", "部门");
                    dr.setValue("Width", 120);
                    dt.Rows.add(dr);
                }

                dd.TableName = "FK_Dept";
                ds.Tables.add(dd);

            }
        }
        Methods methods = new Methods();
        //实体类方法
        try {
            methods.Retrieve(MethodAttr.FrmID, this.getFrmID(), MethodAttr.IsSearchBar, 1, MethodAttr.Idx);
        } catch (RuntimeException e) {
            methods.getNewEntity().CheckPhysicsTable();
            methods.Retrieve(MethodAttr.FrmID, this.getFrmID(), MethodAttr.IsSearchBar, 1, MethodAttr.IsEnable, 1, MethodAttr.Idx);

        }
        ds.Tables.add(methods.ToDataTableField("Frm_Method"));


        Collections colls = Search_BtnPower();
        ds.Tables.add(methods.ToDataTableField("ToolBtns"));
        ds.Tables.add(colls.ToDataTableField("Frm_Collection"));

        return bp.tools.Json.ToJson(ds);

    }

    ///#endregion 查询条件

    private DataTable GetDeptDataTable(SearchDataRole searchDataRole, MapData md) throws Exception {
        //增加部门的外键
        DataTable dt = new DataTable();
        String sql = "";
        if (searchDataRole == SearchDataRole.ByDept) {
            sql = "SELECT D.No,D.Name From Port_Dept D,Port_DeptEmp E WHERE D.No=E.FK_Dept AND E.FK_Emp='" + WebUser.getNo() + "'";
            dt = DBAccess.RunSQLReturnTable(sql);
        }
        if (searchDataRole == SearchDataRole.ByDeptAndSSubLevel) {
            dt = GetDeptAndSubLevel();
        }
        if (searchDataRole == SearchDataRole.ByStationDept) {
            sql = "SELECT D.No,D.Name From Port_Dept D WHERE D.No IN(SELECT F.FK_Dept FROM Frm_StationDept F,Port_DeptEmpStation P Where F.FK_Station = P.FK_Station AND F.FK_Frm='" + md.getNo() + "' AND P.FK_Emp='" + WebUser.getUserID() + "')";
            dt = DBAccess.RunSQLReturnTable(sql);
        }
        for (DataColumn col : dt.Columns) {
            String colName = col.ColumnName.toLowerCase();
            switch (colName) {
                case "no":
                    col.ColumnName = "No";
                    break;
                case "name":
                    col.ColumnName = "Name";
                    break;

                default:
                    break;
            }

        }
        return dt;
    }

    private DataTable GetDeptAndSubLevel() {
        //获取本部门和兼职部门
        String sql = "SELECT D.No,D.Name From Port_Dept D,Port_DeptEmp E WHERE D.No=E.FK_Dept AND E.FK_Emp='" + WebUser.getNo() + "'";
        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        //dt.PrimaryKey = new DataColumn[] {dt.Columns.get("No"]};
        DataTable dd = dt.copy();
        for (DataRow dr : dd.Rows) {
            GetSubLevelDeptByParentNo(dt, dr.getValue(0).toString());
        }
        return dt;
    }

    private void GetSubLevelDeptByParentNo(DataTable dt, String parentNo) {
        String sql = "SELECT No,Name FROM Port_Dept Where ParentNo='" + parentNo + "'";
        DataTable dd = DBAccess.RunSQLReturnTable(sql);

        for (DataRow dr : dd.Rows) {
            if (dt.Rows.contains(dr.getValue(0).toString()) == true) {
                continue;
            }
            dt.Rows.add(dr);

            GetSubLevelDeptByParentNo(dt, dr.getValue(0).toString());

        }
    }

    public final String Search_TreeData() throws Exception {
        MapData mapData = new MapData(this.getFrmID());
        int listShowWay = mapData.GetParaInt("ListShowWay", 0);
        String listShowWayKey = mapData.GetParaString("ListShowWayKey");
        if (DataType.IsNullOrEmpty(listShowWayKey) == true) {
            return "err@树形结构展示的字段不存在，请检查查询条件设置中展示方式配置是否正确";
        }
        MapAttr mapAttr = new MapAttr(this.getFrmID() + "_" + listShowWayKey);
        //获取绑定的数据源
        if (DataType.IsNullOrEmpty(mapAttr.getUIBindKey()) == true) {
            return "err@字段" + mapAttr.getName() + "绑定的外键或者外部数据源不存在,请检查字段属性[外键SFTable]是否为空";
        }
        DataTable dt = bp.pub.PubClass.GetDataTableByUIBineKey(mapAttr.getUIBindKey(), null);
        return bp.tools.Json.ToJson(dt);

    }

    /**
     * 实体、单据列表显示的字段
     *
     * @return
     */
    public final String Search_MapAttr() throws Exception {
        FrmDict frmDict = new FrmDict(this.getFrmID());
        if (frmDict.getEntityType() == EntityType.DBList) {
            return Search_MapAttrForDB();
        }

        ///#region 查询显示的列
        MapAttrs mattrs = new MapAttrs();
        mattrs.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.Idx);

        MapExts mapExts = new MapExts();
        QueryObject qo = new QueryObject(mapExts);
        qo.AddWhere(MapExtAttr.FK_MapData, this.getFrmID());
        qo.addAnd();
        qo.AddWhereIn(MapExtAttr.ExtType, "('MultipleChoiceSmall','SingleChoiceSmall')");
        qo.DoQuery();
        for (MapExt mapExt : mapExts.ToJavaList()) {
            //获取mapAttr
            Object tempVar = mattrs.GetEntityByKey(this.getFrmID() + "_" + mapExt.getAttrOfOper());
            MapAttr mapAttr = tempVar instanceof MapAttr ? (MapAttr) tempVar : null;
            if(mapAttr == null)
                continue;
            String searchVisable = mapAttr.getatPara().GetValStrByKey("SearchVisable");
            if (Objects.equals(searchVisable, "0")) {
                continue;
            }
            mapAttr.SetPara("SearchVisable", 0);
            mapAttr.Update();
            Object tempVar2 = mattrs.GetEntityByKey(this.getFrmID() + "_" + mapExt.getAttrOfOper() + "T");
            mapAttr = tempVar2 instanceof MapAttr ? (MapAttr) tempVar2 : null;
            mapAttr.SetPara("SearchVisable", 1);
            mapAttr.Update();
        }
        DataRow row = null;
        DataTable dt = new DataTable("Attrs");
        dt.Columns.Add("KeyOfEn", String.class);
        dt.Columns.Add("Name", String.class);
        dt.Columns.Add("Width", Integer.class);
        dt.Columns.Add("UIContralType", Integer.class);
        dt.Columns.Add("LGType", Integer.class);
        dt.Columns.Add("MyDataType", Integer.class);
        dt.Columns.Add("UIBindKey", String.class);
        dt.Columns.Add("AtPara", String.class);
        dt.Columns.Add("IsRichText", Integer.class);
        int showColModel = frmDict.GetValIntByKey("ShowColModel");
        //设置标题、单据号位于开始位置
        for (MapAttr attr : mattrs.ToJavaList()) {
            if (showColModel == 0 && attr.getUIVisible() == false)
                continue;
            if(showColModel == 1){
                String searchVisable = attr.getatPara().GetValStrByKey("SearchVisable");
                if (Objects.equals(searchVisable, "0")) {
                    continue;
                }
                if (DataType.IsNullOrEmpty(searchVisable) == true && attr.getUIVisible() == false) {
                    continue;
                }
            }

            row = dt.NewRow();
            row.setValue("KeyOfEn", attr.getKeyOfEn());
            row.setValue("Name", attr.getName());
            row.setValue("Width", attr.getUIWidthInt());
            row.setValue("UIContralType", attr.getUIContralType().getValue());
            row.setValue("LGType", attr.getLGType().getValue());
            row.setValue("MyDataType", attr.getMyDataType());
            row.setValue("UIBindKey", attr.getUIBindKey());
            row.setValue("AtPara", attr.GetValStringByKey("AtPara"));
            row.setValue("IsRichText", attr.getTextModel() == 3 ? 1 : 0);
            dt.Rows.add(row);
        }


        ///#endregion 查询显示的列
        DataSet ds = new DataSet();
        ds.Tables.add(dt);
        //增加枚举
        MapData mapData = new MapData(this.getFrmID());
        ds.Tables.add(mapData.getSysEnums().ToDataTableField("Sys_Enum"));
        //查询一行数据的操作
        Methods methods = new Methods();
        methods.Retrieve(MethodAttr.FrmID, this.getFrmID(), MethodAttr.IsList, 1, MethodAttr.Idx);

        ds.Tables.add(methods.ToDataTableField("Frm_Method"));

        return bp.tools.Json.ToJson(ds);
    }

    /**
     * 获取查询列表的按钮权限
     *
     * @return
     */
    public final Collections Search_BtnPower() throws Exception {
        //获取该表单所有操作按钮的权限
        Collections colls = new Collections();
        QueryObject qo = new QueryObject(colls);
        qo.AddWhere(CollectionAttr.FrmID, this.getFrmID());
        qo.addAnd();
        qo.AddWhere(CollectionAttr.IsEnable,">",0);
        qo.addOrderBy("Idx");
        qo.DoQuery();
        if (colls.size() == 0) {
            //查询
            Collection collection = new Collection();
            collection.setFrmID(this.getFrmID());
            collection.setMethodID("Search");
            collection.setName("查询");
            collection.setMethodModel("Search");
            collection.setMark("Search");
            collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
            collection.SetValByKey("Idx", 0);
            collection.Insert();

            //新建
            collection = new Collection();
            collection.setFrmID(this.getFrmID());
            collection.setMethodID("New");
            collection.setName("新建");
            collection.setMethodModel("New");
            collection.setMark("New");
            collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
            collection.SetValByKey("Idx", 1);
            collection.Insert();

            //删除
            collection = new Collection();
            collection.setFrmID(this.getFrmID());
            collection.setMethodID("Delete");
            collection.setName("删除");
            collection.setMethodModel("Delete");
            collection.setMark("Delete");
            collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
            collection.SetValByKey("Idx", 2);
            collection.SetPara("EnName","TS.CCBill.CollectionDelete");
            collection.Insert();

            //分析
            collection = new Collection();
            collection.setFrmID(this.getFrmID());
            collection.setMethodID("Group");
            collection.setName("分析");
            collection.setMethodModel("Group");
            collection.setMark("Group");
            collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
            collection.SetValByKey("Idx", 3);
            collection.SetValByKey("IsEnable", false);
            collection.Insert();

            //导出
            collection = new Collection();
            collection.setFrmID(this.getFrmID());
            collection.setMethodID("ExpExcel");
            collection.setName("导出Excel");
            collection.setMethodModel("ExpExcel");
            collection.setMark("ExpExcel");
            collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
            collection.SetValByKey("Idx", 4);
            collection.Insert();

            //导入
            collection = new Collection();
            collection.setFrmID(this.getFrmID());
            collection.setMethodID("ImpExcel");
            collection.setName("导入Excel");
            collection.setMethodModel("ImpExcel");
            collection.setMark("ImpExcel");
            collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
            collection.SetValByKey("Idx", 5);
            collection.Insert();

            colls.Retrieve(GroupMethodAttr.FrmID, this.getFrmID(), "Idx");
        }

        ///#region 整理参数
        DBRoles rols = new DBRoles();
        rols.Retrieve("FrmID", this.getFrmID());

        String mydepts = "" + WebUser.getDeptNo() + ","; //我的部门.
        String mystas = ""; //我的角色.

        DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.getUserID() + "'");
        for (DataRow dr : mydeptsDT.Rows) {
            mydepts += dr.getValue(0).toString() + ",";
            mystas += dr.getValue(1).toString() + ",";
        }
        ///#endregion 整理参数

        //组织集合.
        Collections collsNew = new Collections();
        for (Collection item : colls.ToJavaList()) {
            ///#region 判断新建按钮
            if (item.getMethodID().equals("New") == true)
            {
                if (this.CheckRoles(rols, "RecNew", mydepts, mystas) == true)
                {
                    collsNew.AddEntity(item);
                    continue;
                }
                continue;
            }
            ///#endregion 判断新建按钮
            ///#region 导出
            if (item.getMethodID().equals("ExpExcel") == true)
            {
                if (this.CheckRoles(rols, "ExpExcel", mydepts, mystas) == true)
                {
                    collsNew.AddEntity(item);
                    continue;
                }
                continue;
            }
            ///#endregion 导出

            ///#region 导入
            if (item.getMethodID().equals("ImpExcel") == true)
            {
                if (this.CheckRoles(rols, "ImpExcel", mydepts, mystas) == true)
                {
                    collsNew.AddEntity(item);
                    continue;
                }
                continue;
            }
            ///#endregion 导出
            collsNew.AddEntity(item);
        }
        //判断是否可以新建.
        return collsNew;
    }
    public Boolean CheckRoles(DBRoles rols, String dbRole, String strDepps, String strStas)throws Exception {
        return CheckRoles(rols,dbRole,strDepps,strStas,"");
    }
    public Boolean CheckRoles(DBRoles rols, String dbRole, String strDepps, String strStas,String starter) throws Exception {
        if (rols.GetCountByKey("DBRole", dbRole) == 0)
            return true;

        int num = 0;
        for (DBRole rol : rols.ToJavaList())
        {
            if (rol.GetValStringByKey("DBRole").equals(dbRole) == false)
                continue;

            num++;
            String markID = rol.GetValStringByKey("MarkID");
            String docs = rol.GetValStringByKey("Docs");
            if (markID.equals("None") == true)
                return true;

            if (markID.equals("ByStations") == true && bp.da.DataType.IsHaveIt(docs, strStas) == true)
                return true;
            if (markID.equals("ByDepts") == true && bp.da.DataType.IsHaveIt(docs, strDepps) == true)
                return true;
            if (markID.equals("ByEmps") == true && bp.da.DataType.IsHaveIt(docs, "," + bp.web.WebUser.getNo() + ",") == true)
                return true;
            if (markID.equals("Adminer") == true && bp.web.WebUser.getNo().equals("admin") == true)
                return true;
            if (markID.equals("Admin2") == true && bp.web.WebUser.getIsAdmin() == true)
                return true;
            if (markID.equals("SQL") == true)
            {
                String sql = bp.wf.Glo.DealExp(docs, null, "");
                if (DBAccess.RunSQLReturnValFloat(sql) > 0)
                    return true;
            }
            if(markID.equals("SelfOnly") == true && starter.equals(WebUser.getNo()))
                return true;
        }
        if (num == 0) return true;
        return false;
    }


    /**
     * 获取数据源实体列表显示的列及操作列方法
     *
     * @return
     */
    public final String Search_MapAttrForDB() throws Exception {
        DBList dblist = new DBList(this.getFrmID());

        ///#region 查询显示的列
        MapAttrs mapattrs = new MapAttrs();
        mapattrs.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.Idx);

        //查询列表数据源显示的列
        if (DataType.IsNullOrEmpty(dblist.getExpList()) == true) {
            return "err@数据源实体的列表数据源不能为空，请联系设计人员，检查错误原因.";
        }

        //查询结果集返回的字段列表
        DataTable listDT = null;
        String explist = dblist.getExpList();
        //替换
        if ((new Integer(dblist.getDBType())).equals("local")) {

            if (explist.toUpperCase().contains("WHERE") == false) {
                explist += " WHERE 1=2";
            } else {
                explist += " AND 1=2";
            }
            if (DataType.IsNullOrEmpty(dblist.getDBSrc()) == true) {
                dblist.setDBSrc("local");
            }
            explist = bp.wf.Glo.DealExp(explist, null);
            SFDBSrc dbSrc = new SFDBSrc(dblist.getDBSrc());
            listDT = dbSrc.RunSQLReturnTable(explist);
        }


        DataRow row = null;
        DataTable dt = new DataTable("Attrs");
        dt.Columns.Add("KeyOfEn", String.class);
        dt.Columns.Add("Name", String.class);
        dt.Columns.Add("Width", Integer.class);
        dt.Columns.Add("UIContralType", Integer.class);
        dt.Columns.Add("LGType", Integer.class);
        dt.Columns.Add("MyDataType", Integer.class);
        dt.Columns.Add("UIBindKey", String.class);
        dt.Columns.Add("AtPara", String.class);
        if (listDT == null) {
            for (MapAttr attr : mapattrs.ToJavaList()) {
                String searchVisable = attr.getatPara().GetValStrByKey("SearchVisable");
                if (Objects.equals(searchVisable, "0")) {
                    continue;
                }
                if (DataType.IsNullOrEmpty(searchVisable) == true && attr.getUIVisible() == false) {
                    continue;
                }
                row = dt.NewRow();
                row.setValue("KeyOfEn", attr.getKeyOfEn());
                row.setValue("Name", attr.getName());
                row.setValue("Width", attr.getUIWidthInt());
                row.setValue("UIContralType", attr.getUIContralType().getValue());
                row.setValue("LGType", attr.getLGType().getValue());
                row.setValue("MyDataType", attr.getMyDataType());
                row.setValue("UIBindKey", attr.getUIBindKey());
                row.setValue("AtPara", attr.GetValStringByKey("AtPara"));
                dt.Rows.add(row);
            }
        } else {
            //设置标题、单据号位于开始位置
            for (DataColumn col : listDT.Columns) {
                //获取key
                String key = col.ColumnName;
                if (DataType.IsNullOrEmpty(key) == true) {
                    continue;
                }
                Object tempVar = mapattrs.GetEntityByKey(this.getFrmID() + "_" + key);
                MapAttr attr = tempVar instanceof MapAttr ? (MapAttr) tempVar : null;
                row = dt.NewRow();
                if (attr == null) {
                    row.setValue("KeyOfEn", key);
                    row.setValue("Name", key);
                    row.setValue("Width", 120);
                    row.setValue("UIContralType", UIContralType.TB.getValue());
                    row.setValue("LGType", FieldTypeS.Normal.getValue());
                    row.setValue("MyDataType", DataType.AppString);
                    row.setValue("UIBindKey", "");
                    row.setValue("AtPara", "");
                    dt.Rows.add(row);
                    continue;
                }
                String searchVisable = attr.getatPara().GetValStrByKey("SearchVisable");
                if (Objects.equals(searchVisable, "0")) {
                    continue;
                }
                if (DataType.IsNullOrEmpty(searchVisable) == true && attr.getUIVisible() == false) {
                    continue;
                }
                row.setValue("KeyOfEn", attr.getKeyOfEn());
                row.setValue("Name", attr.getName());
                row.setValue("Width", attr.getUIWidthInt());
                row.setValue("UIContralType", attr.getUIContralType().getValue());
                row.setValue("LGType", attr.getLGType().getValue());
                row.setValue("MyDataType", attr.getMyDataType());
                row.setValue("UIBindKey", attr.getUIBindKey());
                row.setValue("AtPara", attr.GetValStringByKey("AtPara"));
                dt.Rows.add(row);
            }

        }


        ///#endregion 查询显示的列
        DataSet ds = new DataSet();
        ds.Tables.add(dt);
        //增加枚举
        MapData mapData = new MapData(this.getFrmID());
        ds.Tables.add(mapData.getSysEnums().ToDataTableField("Sys_Enum"));

        ///#region 把外键表加入 DataSet
        DataTable ddlTable = new DataTable();
        ddlTable.Columns.Add("No");

        for (MapAttr attr : mapattrs.ToJavaList()) {
            //为空、枚举值就continue.
            if (DataType.IsNullOrEmpty(attr.getUIBindKey()) == true || attr.getLGType() == FieldTypeS.Enum) {
                continue;
            }
            DataTable mydt = bp.pub.PubClass.GetDataTableByUIBineKey(attr.getUIBindKey(), null);
            if (mydt == null) {
                DataRow ddldr = ddlTable.NewRow();
                ddldr.setValue("No", attr.getUIBindKey());
                ddlTable.Rows.add(ddldr);
            } else {
                ds.Tables.add(mydt);
            }
        }
        ddlTable.TableName = "UIBindKey";
        ds.Tables.add(ddlTable);

        ///#endregion End把外键表加入DataSet

        //查询一行数据的操作
        Methods methods = new Methods();
        methods.Retrieve(MethodAttr.FrmID, this.getFrmID(), MethodAttr.IsList, 1, MethodAttr.Idx);

        ds.Tables.add(methods.ToDataTableField("Frm_Method"));

        return bp.tools.Json.ToJson(ds);
    }

    public final String SearchDB_UrlSearchData(String urlExt, String postData) throws Exception {
        urlExt = bp.wf.Glo.DealExp(urlExt, null);
        if (urlExt.contains("http") == false) {
            /*如果没有绝对路径 */
            if (SystemConfig.isBSsystem()) {
                /*在cs模式下自动获取*/
                String host = SystemConfig.getHostURL(); //BP.Sys.Base.Glo.Request.Url.Host;
                if (urlExt.contains("@AppPath")) {
                    urlExt = urlExt.replace("@AppPath", "http://" + host + getRequest().getRequestURI()); //BP.Sys.Base.Glo.Request.ApplicationPath
                } else {
                    urlExt = "http://" + getRequest().getRequestURI() + urlExt;
                }
            }

            if (SystemConfig.isBSsystem() == false) {
                /*在cs模式下它的baseurl 从web.config中获取.*/
                String cfgBaseUrl = (String) SystemConfig.getAppSettings().get("HostURL");
                if (DataType.IsNullOrEmpty(cfgBaseUrl)) {
                    String err = "调用url失败:没有在web.config中配置BaseUrl,导致url事件不能被执行.";
                    Log.DebugWriteError(err);
                    throw new RuntimeException(err);
                }
                urlExt = cfgBaseUrl + urlExt;
            }
        }

        String json = HttpClientUtil.doPostJson(urlExt, postData);
        return json;
    }
    /**
     权限控制.
     @param dt
     @param dbScop All=全部权限,Dept=本部门的权限,Self=自己的权限.
     @return
     */
    private boolean CheckDB(DataTable dt, String dbScop)
    {
        if (dt.Rows.size() == 0)
        {
            return true;
        }
        //#region 是否是本组织或者下级组织.
        if (dbScop.equals("POrg") == true || dbScop.equals("OrgOnly") == true)
        {
            for (DataRow dr : dt.Rows)
            {
                String markID = dr.getValue(0).toString();
                String strs = "," + dr.getValue(1).toString() + ",";
                String sql = "";
                //如果是部门.
                if (markID.equals("POrg") == true)
                    return true;
                if (markID.equals("OrgOnly") == true || markID.equals("POrg") == true)
                    return true;
            }
        }
         // #endregion 是否是本组织或者下级组织.

        //#region 可以被直线父级组织所看到
        if (dbScop.equals("NOrg") == true)
        {
            for (DataRow dr : dt.Rows)
            {
                String markID = dr.getValue(0).toString();
                String strs = "," + dr.getValue(1).toString() + ",";
                String sql = "";
                //如果是部门.
                if (markID.equals("NOrg") == true)
                    return true;
            }
        }
        ///#region 判断全部范围.
        if (dbScop.equals("All") == true)
        {
            for (DataRow dr : dt.Rows)
            {
                String markID = dr.getValue(0).toString();
                String strs = ","+dr.getValue(1).toString()+",";
                String sql = "";
                //如果是部门.
                if (markID.equals("ByDepts") == true)
                {
                    sql = "SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp='" + WebUser.getNo() + "'";
                    sql += " UNION ";
                    sql += "SELECT FK_Dept FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.getNo() + "'";

                    DataTable mydt = DBAccess.RunSQLReturnTable(sql);
                    for (DataRow mydr : mydt.Rows)
                    {
                        String myNo = mydr.get(0).toString();
                        if (strs.contains("," + myNo + ",") == true)
                        {
                            return true;
                        }
                    }
                }

                //如果标记是岗位.
                if (markID.equals("ByStations") == true)
                {
                    sql = "SELECT FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.getNo() + "'";
                    DataTable mydt = DBAccess.RunSQLReturnTable(sql);
                    for (DataRow mydr : mydt.Rows)
                    {
                        String myNo = mydr.getValue(0).toString();
                        if (strs.contains("," + myNo + ",") == true)
                            return true;
                    }
                }

                //指定的人是否可以查看全部数据?.
                if (markID.equals("ByEmps") == true)
                {
                    if (strs.contains("," + WebUser.getNo() + ",") == true)
                        return true;
                }
                //指定的人是否可以查看全部数据?.
                if (markID.equals("Adminer") == true && WebUser.getNo().equals("admin")==true)
                {
                    return true;
                }
                //指定的人是否可以查看全部数据?.
                if (markID.equals("Admin2") == true && WebUser.getIsAdmin())
                {
                    return true;
                }
            }
            return false;
        }
        ///#endregion 判断全部范围.

        ///#region 判断部门范围.
        if (dbScop.equals("Dept") == true)
        {
            for (DataRow dr : dt.Rows)
            {
                String markID = dr.getValue("MarkID").toString();
                String sql = "";
                //如果是部门, 判断他是否是领导?
                if (markID.equals("DeptLeader") == true)
                {
                    sql = "SELECT No FROM Port_Dept WHERE Leader='" + WebUser.getNo() + "' AND No='" + WebUser.getDeptNo() + "'";
                    DataTable mydt = DBAccess.RunSQLReturnTable(sql);
                    if (mydt.Rows.size() == 1)
                    {
                        return true;
                    }
                }

                //是否可以查看同部门的数据..
                if (markID.equals("DeptOnly") == true)
                {
                    return true;
                }
            }
        }
        ///#endregion 判断全部范围.
        //#region 判断Exp
        if (dbScop.equals("Exp") == true)
        {
            for(DataRow dr : dt.Rows)
            {
                String markID = dr.getValue(0).toString();
                if (markID.equals("ByExp") == true)
                    return true;
            }
            return false;
        }
        // #endregion 判断Exp
        return false;
    }
    private boolean isNormalDB () {
        return SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL;
    }
    private int times = 0;

    public final String Search_Init() throws Exception {
        DataSet ds = new DataSet();
        DataTable dt = null;
        try {

            ///#region 查询语句
            MapData md = new MapData(this.getFrmID());

            //取出来查询条件.
            UserRegedit ur = new UserRegedit(WebUser.getNo(), this.getFrmID() + "_SearchAttrs");

            Entities rpts = null;

            if (md.getEntityType() == EntityType.FrmEntityNoName)
                rpts = new GEEntityNoNames(this.getFrmID());
            else
                rpts = new GEEntitys(this.getFrmID());

            Attrs attrs = rpts.getNewEntity().getEnMap().getAttrs();

            QueryObject qo = new QueryObject(rpts);
            boolean isFirst = true; //是否第一次拼接SQL


            ///#region 关键字字段.
            String keyWord = ur.getSearchKey();

            if (!DataType.IsNullOrEmpty(keyWord) && !keyWord.isEmpty()) {
                Attr attrPK = new Attr();
                for (Attr attr : attrs) {
                    if (attr.getItIsPK()) {
                        attrPK = attr;
                        break;
                    }
                }
                int i = 0;
                String enumKey = ","; //求出枚举值外键.
                for (Attr attr : attrs) {
                    switch (attr.getMyFieldType()) {
                        case Enum:
                            enumKey = "," + attr.getKey() + "Text,";
                            break;
                        case FK:
                            continue;
                        default:
                            break;
                    }

                    if (attr.getMyDataType() != DataType.AppString) {
                        continue;
                    }

                    //排除枚举值关联refText.
                    if (attr.getMyFieldType() == FieldType.RefText) {
                        if (enumKey.contains("," + attr.getKey() + ",") == true) {
                            continue;
                        }
                    }

                    if (Objects.equals(attr.getKey(), "FK_Dept")|| Objects.equals(attr.getKey(),"DeptNo")) {
                        continue;
                    }

                    i++;
                    if (i == 1) {
                        isFirst = false;
                        /* 第一次进来。 */
                        qo.addLeftBracket();
                        if (isNormalDB() || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "?")) {
                            qo.AddWhere(attr.getKey(), " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
                        } else {
                            qo.AddWhere(attr.getKey(), " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
                        }
                        continue;
                    }
                    qo.addOr();

                    if (isNormalDB() || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "?")) {
                        qo.AddWhere(attr.getKey(), " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? ("CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : ("'%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
                    } else {
                        qo.AddWhere(attr.getKey(), " LIKE ", "'%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
                    }

                }
                qo.getMyParas().Add("SKey", keyWord, false);
                qo.addRightBracket();
            } else if (!DataType.IsNullOrEmpty(md.GetParaString("StringSearchKeys"))) {
                String field = ""; //字段名
                String fieldValue = ""; //字段值
                int idx = 0;

                //获取查询的字段
                String[] searchFields = md.GetParaString("StringSearchKeys").split("\\*");
                for (String str : searchFields) {
                    if (DataType.IsNullOrEmpty(str)) {
                        continue;
                    }

                    //字段名
                    String[] items = str.split(",");
                    if (items.length == 2 && DataType.IsNullOrEmpty(items[0])) {
                        continue;
                    }
                    field = items[0];
                    //字段名对应的字段值
                    fieldValue = ur.GetParaString(field);
                    if (DataType.IsNullOrEmpty(fieldValue)) {
                        continue;
                    }
                    idx++;
                    if (idx == 1) {
                        isFirst = false;
                        /* 第一次进来。 */
                        qo.addLeftBracket();
                        if (isNormalDB() || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "?")) {
                            qo.AddWhere(field, " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + field + ",'%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + field + "+'%'"));
                        } else {
                            qo.AddWhere(field, " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + field + "||'%'");
                        }
                        qo.getMyParas().Add(field, fieldValue, false);
                        continue;
                    }
                    qo.addAnd();

                    if (isNormalDB() || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "?")) {
                        qo.AddWhere(field, " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? ("CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + field + ",'%')") : ("'%'+" + SystemConfig.getAppCenterDBVarStr() + field + "+'%'"));
                    } else {
                        qo.AddWhere(field, " LIKE ", "'%'||" + SystemConfig.getAppCenterDBVarStr() + field + "||'%'");
                    }
                    qo.getMyParas().Add(field, fieldValue, false);


                }
                if (idx != 0) {
                    qo.addRightBracket();
                }
            }


            ///#endregion 关键字段查询


            ///#region 时间段的查询
            if (md.GetParaInt("DTSearchWay", 0) != DTSearchWay.None.getValue() && !DataType.IsNullOrEmpty(ur.getDTFrom())) {
                String dtFrom = ur.getDTFrom(); // this.GetTBByID("TB_S_From").Text.Trim().replace("/", "-");
                String dtTo = ur.getDTTo(); // this.GetTBByID("TB_S_To").Text.Trim().replace("/", "-");

                //按日期查询
                if (md.GetParaInt("DTSearchWay", 0) == DTSearchWay.ByDate.getValue()) {
                    if (!isFirst) {
                        qo.addAnd();
                    } else {
                        isFirst = false;
                    }
                    qo.addLeftBracket();
                    dtTo += " 23:59:59";
                    qo.setSQL(md.GetParaString("DTSearchKey") + " >= '" + dtFrom + "'");
                    qo.addAnd();
                    qo.setSQL(md.GetParaString("DTSearchKey") + " <= '" + dtTo + "'");
                    qo.addRightBracket();
                }

                if (md.GetParaInt("DTSearchWay", 0) == DTSearchWay.ByDateTime.getValue()) {
                    //取前一天的24：00
                    if (dtFrom.trim().length() == 10) //2017-09-30
                    {
                        dtFrom += " 00:00:00";
                    }
                    if (dtFrom.trim().length() == 16) //2017-09-30 00:00
                    {
                        dtFrom += ":00";
                    }

                    dtFrom = DateUtils.addDay(DateUtils.parse(dtFrom, "yyyy-MM-dd"), -1) + " 24:00";

                    if (dtTo.trim().length() < 11 || dtTo.trim().indexOf(' ') == -1) {
                        dtTo += " 24:00";
                    }

                    if (!isFirst) {
                        qo.addAnd();
                    } else {
                        isFirst = false;
                    }
                    qo.addLeftBracket();
                    qo.setSQL(md.GetParaString("DTSearchKey") + " >= '" + dtFrom + "'");
                    qo.addAnd();
                    qo.setSQL(md.GetParaString("DTSearchKey") + " <= '" + dtTo + "'");
                    qo.addRightBracket();
                }
            }

            ///#endregion 时间段的查询


            ///#region 外键或者枚举的查询

            //获得关键字.
            AtPara ap = new AtPara(ur.getVals());
            Attr ddattr = null;
            for (String str : ap.getHisHT().keySet()) {
                String val = ap.GetValStrByKey(str);
                if (val.equals("all") || val.equals("null") || val.isEmpty()) {
                    continue;
                }
                if (isFirst == false) {
                    qo.addAnd();
                } else {
                    isFirst = false;
                }

                qo.addLeftBracket();
                ddattr = attrs.GetAttrByKeyOfEn(str);
                if (val.indexOf(",") != -1) {
                    if (ddattr.getItIsNum() == true) {
                        qo.AddWhere(str, "IN", "(" + val + ")");
                        qo.addRightBracket();
                        continue;
                    }
                    val = "('" + val.replace(",", "','") + "')";
                    qo.AddWhere(str, "IN", val);
                    qo.addRightBracket();
                    continue;
                }
                if (SystemConfig.getAppCenterDBFieldIsParaDBType() == true) {
                    String typeVal = (String) bp.sys.base.Glo.GenerRealType(attrs, str, ap.GetValStrByKey(str));
                    qo.AddWhere(str, typeVal);

                } else {
                    qo.AddWhere(str, ap.GetValStrByKey(str));
                }

                qo.addRightBracket();
            }

            ///#endregion 外键或者枚举的查询
            ///#region 设置隐藏字段的过滤查询
            FrmBill frmBill = new FrmBill(this.getFrmID());
            String hidenField = frmBill.GetParaString("HidenField");
            if(frmBill.GetParaInt("HidenWay") == 1) {
                if (DataType.IsNullOrEmpty(hidenField) == false) {
                    hidenField = hidenField.replace("_WebUser", "@WebUser");
                    hidenField = hidenField.replace("@WebUser.getNo()", WebUser.getNo());
                    hidenField = hidenField.replace("@WebUser.Name", WebUser.getName());
                    hidenField = hidenField.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
                    hidenField = hidenField.replace("@WebUser.DeptName", WebUser.getDeptName());
                    hidenField = hidenField.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
                    hidenField = hidenField.replace("@WebUser.DeptNo", WebUser.getDeptNo());
                    hidenField = hidenField.replace("@WebUser.OrgNo", WebUser.getOrgNo());
                    hidenField = hidenField.replace( "~", "'");

                    if (hidenField.indexOf("_") != -1) {
                        return "err@隐藏条件" + hidenField + "还有未替换的_符号";
                    }

                    if (isFirst == false) {
                        qo.addAnd();
                    } else {
                        isFirst = false;
                    }
                    qo.addSQL(hidenField);
                }
            }
            ///#endregion 设置隐藏字段的查询
            ///#endregion 查询语句

            if (isFirst == false) {
                qo.addAnd();
            }

            if (md.getEntityType() == EntityType.FrmEntityNoName)
            {
                qo.AddWhere("EntityState", ">", 0);
            }
            else
            {
                qo.AddWhere("BillState", ">", 0);
            }

            isFirst = false;

            //增加表单字段的查询
            for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
                if (DataType.IsNullOrEmpty(key) || key.equals("T") == true || key.equals("t") == true || key.equals("HttpHandlerName") == true || key.equals("DoMethod") == true || key.equals("DoType") == true) {
                    continue;
                }
                if (attrs.contains(key) == true) {
                    if (isFirst == false) {
                        qo.addAnd();
                    }
                    qo.AddWhere(key, ContextHolderUtils.getRequest().getParameter(key));
                    continue;
                }

            }
//            if(SystemConfig.getCCBPMRunModel()!=CCBPMRunModel.Single && attrs.contains("OrgNo")){
//                if (isFirst == false) {
//                    qo.addAnd();
//                }
//                qo.AddWhere("OrgNo", WebUser.getOrgNo());
//            }

            //获取配置信息
            String fieldSet = frmBill.getFieldSet();
            String oper = "";
            if (DataType.IsNullOrEmpty(fieldSet) == false) {
                String ptable = rpts.getNewEntity().getEnMap().getPhysicsTable();
                dt = new DataTable("Search_FieldSet");
                dt.Columns.Add("Field");
                dt.Columns.Add("Type");
                dt.Columns.Add("Value");
                DataRow dr;
                String[] strs = fieldSet.split("@");
                for (String str : strs) {
                    if (DataType.IsNullOrEmpty(str) == true) {
                        continue;
                    }
                    String[] item = str.split("=");
                    if (item.length == 2) {
                        if (item[1].contains(",") == true) {
                            String[] ss = item[1].split(",");
                            for (String s : ss) {
                                dr = dt.NewRow();
                                dr.setValue("Field", attrs.GetAttrByKey(s).getDesc());
                                dr.setValue("Type", item[0]);
                                dt.Rows.add(dr);

                                oper += item[0] + "(" + ptable + "." + s + ")" + ",";
                            }
                        } else {
                            dr = dt.NewRow();
                            dr.setValue("Field", attrs.GetAttrByKey(item[1]).getDesc());
                            dr.setValue("Type", item[0]);
                            dt.Rows.add(dr);

                            oper += item[0] + "(" + ptable + "." + item[1] + ")" + ",";
                        }
                    }
                }
                oper = oper.substring(0, oper.length() - 1);
                DataTable dd = qo.GetSumOrAvg(oper);

                for (int i = 0; i < dt.Rows.size(); i++) {
                    DataRow ddr = dt.Rows.get(i);
                    ddr.setValue("Value", dd.Rows.get(0).getValue(i));
                }
                ds.Tables.add(dt);
            }
            ///#region 处理数据权限.
            Paras paras = new Paras();
            paras.SQL="SELECT MarkID,Docs FROM Frm_DBRole WHERE FrmID=" + SystemConfig.getAppCenterDBVarStr() + "FrmID AND DBRole='DBList' AND IsEnable=1 ";
            paras.Add("FrmID",this.getFrmID());
            DataTable dtDBRole = DBAccess.RunSQLReturnTable(paras);
            //首先判断是否有全部的权限。
            if (this.CheckDB(dtDBRole, "All") == true)
            {
                //不处理.
            }
            else if (this.CheckDB(dtDBRole, "NOrg") == true)
            {
                /*是否可以查看本组织以及下级组织的数据.*/
                //最后按照人员的权限判断.
                if (isFirst == false)
                    qo.addAnd();
                else
                    isFirst = false;

                //查看当前人员所有的下级组织.
                String sql = "SELECT No FROM Port_Org WHERE TreeNos LIKE '%," + WebUser.getOrgNo() + ",%'";
                qo.AddWhereInSQL("OrgNo", sql);
            }
            else if (this.CheckDB(dtDBRole, "POrg") == true)
            {
                /*是否可以查看本组织以及下级组织的数据.*/
                //最后按照人员的权限判断.
                if (isFirst == false)
                    qo.addAnd();
                else
                    isFirst = false;

                String sql = "SELECT No FROM Port_Dept WHERE No='" + WebUser.getOrgNo() + "' OR ParentNo='" + WebUser.getOrgNo() + "'";
                qo.AddWhereInSQL("OrgNo", sql);
            }
            else if (this.CheckDB(dtDBRole, "OrgOnly") == true)
            {
                /*是否可以查看本组织以及下级组织的数据.*/
                //最后按照人员的权限判断.
                if (isFirst == false)
                    qo.addAnd();
                else
                    isFirst = false;
                qo.AddWhereInSQL("OrgNo", "=", WebUser.getOrgNo());
            }
            else if (this.CheckDB(dtDBRole, "Exp") == true)
            {
                /*判断是否有表达式，有表达式优先*/
                paras.SQL="SELECT Docs FROM Frm_DBRole WHERE FrmID=" + SystemConfig.getAppCenterDBVarStr() + "FrmID AND DBRole='DBList' AND MarkID='ByExp' AND IsEnable=1 ";
                String exp = DBAccess.RunSQLReturnString(paras);
                exp = bp.wf.Glo.DealExp(exp, null);
                if (isFirst == false) {
                    qo.addAnd();
                } else {
                    isFirst = false;
                }
                qo.addSQL(exp);
            }
            else
            {
                //检查是否有部门的权限?
                if (this.CheckDB(dtDBRole, "Dept") == true)
                {
                    if (isFirst == false) {
                        qo.addAnd();
                    } else {
                        isFirst = false;
                    }
                    qo.AddWhere("DeptNo", "=", WebUser.getDeptNo());
                }
                else
                {
                    //最后按照人员的权限判断.
                    if (isFirst == false) {
                        qo.addAnd();
                    } else {
                        isFirst = false;
                    }
                    if (md.getEntityType() == EntityType.FrmEntityNoName)
                        qo.AddWhere("RecNo", "=", WebUser.getNo());
                    else
                        qo.AddWhere("Starter", "=", WebUser.getNo());
                }
            }
            ///#endregion 处理数据权限.

            //获得行数.
            ur.SetPara("RecCount", qo.GetCount());
            ur.Save();

            String pkVal = "OID";
            if (md.getEntityType() == EntityType.FrmEntityNoName)
                pkVal = "No";

            if (DataType.IsNullOrEmpty(ur.getOrderBy()) == false && DataType.IsNullOrEmpty(ur.getOrderWay()) == false) {
                qo.DoQuery(pkVal, this.getPageSize(), this.getPageIdx(), ur.getOrderBy(), ur.getOrderWay());
            } else {
                qo.DoQuery(pkVal, this.getPageSize(), this.getPageIdx());
            }

            DataTable mydt = rpts.ToDataTableField("dt");
            mydt.TableName = "DT";

            ds.Tables.add(mydt); //把数据加入里面.

        } catch (Exception ex) {
            if (ex.getMessage() != null && (ex.getMessage().contains("无效") == true || ex.getMessage().toLowerCase().contains("unknown column") == true)) {
                if (times == 0)
                {
                    times++;

                    GEEntity en = new GEEntity(this.getFrmID());
                    en.CheckPhysicsTable();
                    return Search_Init();
                }
            }
            return "err@" + ex.getMessage();
        }


        return bp.tools.Json.ToJson(ds);
    }

    public final String SearchDB_Init() throws Exception {

        DataSet ds = new DataSet();

        ///#region 查询语句
        DBList md = new DBList(this.getFrmID());
        if (DataType.IsNullOrEmpty(md.getExpList()) == true) {
            return "err@列表数据源和的查询不能为空";
        }

        String expList = md.getExpList();
        expList = bp.wf.Glo.DealExp(expList, null);
        //取出来查询条件.
        UserRegedit ur = new UserRegedit(WebUser.getNo(), this.getFrmID() + "_SearchAttrs");

        GEEntitys rpts = new GEEntitys(this.getFrmID());

        Attrs attrs = rpts.getNewEntity().getEnMap().getAttrs();
        String systemKeys = "BillState,RDT,Starter,StarterName,OrgNo,AtPara,"; //创建表单时的系统字段

        //获取查询条件
        DataTable whereDT = new DataTable();
        whereDT.Columns.Add("Key");
        whereDT.Columns.Add("Oper");
        whereDT.Columns.Add("Value");
        whereDT.Columns.Add("Type");
        DataRow dr;

        ///#region 关键字字段.
        String keyWord = ur.getSearchKey();
        Hashtable ht = new Hashtable();

        if (DataType.IsNullOrEmpty(md.GetParaString("StringSearchKeys")) == true) {
            if (DataType.IsNullOrEmpty(keyWord) == false) {
                Attr attrPK = new Attr();
                for (Attr attr : attrs) {
                    if (attr.getItIsPK()) {
                        attrPK = attr;
                        break;
                    }
                }
                int i = 0;
                String enumKey = ","; //求出枚举值外键.
                for (Attr attr : attrs) {
                    if (systemKeys.indexOf(attr.getKey() + ",") != -1) {
                        continue;
                    }
                    switch (attr.getMyFieldType()) {
                        case Enum:
                            enumKey = "," + attr.getKey() + "Text,";
                            break;
                        case FK:
                            continue;
                        default:
                            break;
                    }

                    if (attr.getMyDataType() != DataType.AppString) {
                        continue;
                    }

                    //排除枚举值关联refText.
                    if (attr.getMyFieldType() == FieldType.RefText) {
                        if (enumKey.contains("," + attr.getKey() + ",") == true) {
                            continue;
                        }
                    }

                    if (Objects.equals(attr.getKey(), "FK_Dept")|| Objects.equals(attr.getKey(),"DeptNo")) {
                        continue;
                    }
                    i++;
                    dr = whereDT.NewRow();
                    dr.setValue("Key", attr.getKey());
                    dr.setValue("Oper", "like");
                    dr.setValue("Value", keyWord);
                    dr.setValue("Type", "SearchKey");
                    whereDT.Rows.add(dr);

                }
                ht.put("SearchKey", keyWord);

            }

        } else if (DataType.IsNullOrEmpty(md.GetParaString("StringSearchKeys")) == false) {
            String field = ""; //字段名
            String fieldValue = ""; //字段值

            //获取查询的字段
            String[] searchFields = md.GetParaString("StringSearchKeys").split("\\*");
            for (String str : searchFields) {
                if (DataType.IsNullOrEmpty(str) == true) {
                    continue;
                }

                //字段名
                String[] items = str.split(",");
                if (items.length == 2 && DataType.IsNullOrEmpty(items[0]) == true) {
                    continue;
                }
                field = items[0];
                //字段名对应的字段值
                fieldValue = ur.GetParaString(field);
                if (DataType.IsNullOrEmpty(fieldValue) == true) {
                    ht.put(field, "");
                    continue;
                }

                dr = whereDT.NewRow();
                dr.setValue("Key", field);
                dr.setValue("Oper", "like");
                dr.setValue("Value", fieldValue);
                dr.setValue("Type", "StringKey");
                whereDT.Rows.add(dr);
                ht.put(field, fieldValue);
            }

        }


        ///#endregion 关键字段查询


        ///#region 时间段的查询
        if (md.GetParaInt("DTSearchWay", 0) != DTSearchWay.None.getValue()) {
            if (DataType.IsNullOrEmpty(ur.getDTFrom()) == true) {
                ht.put("DTFrom", ur.getDTFrom());
                ht.put("DTTo", ur.getDTTo());

            } else {
                String dtFrom = ur.getDTFrom(); // this.GetTBByID("TB_S_From").Text.Trim().replace("/", "-");
                String dtTo = ur.getDTTo(); // this.GetTBByID("TB_S_To").Text.Trim().replace("/", "-");

                //按日期查询
                if (md.GetParaInt("DTSearchWay", 0) == DTSearchWay.ByDate.getValue()) {
                    dr = whereDT.NewRow();
                    dr.setValue("Key", md.GetParaString("DTSearchKey"));
                    dr.setValue("Oper", ">=");
                    dr.setValue("Value", dtFrom);
                    dr.setValue("Type", "Date");
                    whereDT.Rows.add(dr);
                    dtTo += " 23:59:59";
                    dr = whereDT.NewRow();
                    dr.setValue("Key", md.GetParaString("DTSearchKey"));
                    dr.setValue("Oper", "<=");
                    dr.setValue("Value", dtTo);
                    dr.setValue("Type", "Date");
                    whereDT.Rows.add(dr);
                    ht.put("DTFrom", dtFrom);
                    ht.put("DTTo", dtTo);
                }

                if (md.GetParaInt("DTSearchWay", 0) == DTSearchWay.ByDateTime.getValue()) {
                    //取前一天的24：00
                    if (dtFrom.trim().length() == 10) //2017-09-30
                    {
                        dtFrom += " 00:00:00";
                    }
                    if (dtFrom.trim().length() == 16) //2017-09-30 00:00
                    {
                        dtFrom += ":00";
                    }

                    dtFrom = DateUtils.addDay(DateUtils.parse(dtFrom, "yyyy-MM-dd"), -1) + " 24:00";

                    if (dtTo.trim().length() < 11 || dtTo.trim().indexOf(' ') == -1) {
                        dtTo += " 24:00";
                    }

                    dr = whereDT.NewRow();
                    dr.setValue("Key", md.GetParaString("DTSearchKey"));
                    dr.setValue("Oper", ">=");
                    dr.setValue("Value", dtFrom);
                    dr.setValue("Type", "Date");
                    whereDT.Rows.add(dr);
                    dr = whereDT.NewRow();
                    dr.setValue("Key", md.GetParaString("DTSearchKey"));
                    dr.setValue("Oper", "<=");
                    dr.setValue("Value", dtTo);
                    dr.setValue("Type", "Date");
                    whereDT.Rows.add(dr);
                    ht.put("DTFrom", dtFrom);
                    ht.put("DTTo", dtTo);
                }
            }

        }

        ///#endregion 时间段的查询


        ///#region 外键或者枚举的查询

        //获得关键字.
        AtPara ap = new AtPara(ur.getVals());
        Attr ddattr = null;
        for (String str : ap.getHisHT().keySet()) {
            String val = ap.GetValStrByKey(str);
            if (val.equals("all")) {
                ht.put(str, "");
                continue;
            }

            dr = whereDT.NewRow();
            dr.setValue("Key", str);
            dr.setValue("Oper", "=");
            if (val.indexOf(",") != -1) {
                dr.setValue("Oper", "IN");
            }

            dr.setValue("Value", val);
            dr.setValue("Type", "Select");
            whereDT.Rows.add(dr);
            ht.put(str, ap.GetValStrByKey(str));
        }

        ///#endregion 外键或者枚举的查询


        //增加表单字段的查询
        for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
            if (DataType.IsNullOrEmpty(key) || key.equals("T") == true || key.equals("t") == true || key.equals("HttpHandlerName") == true || key.equals("DoMethod") == true || key.equals("DoType") == true) {
                continue;
            }
            if (attrs.contains(key) == true) {
                dr = whereDT.NewRow();
                dr.setValue("Key", key);
                dr.setValue("Oper", "=");
                dr.setValue("Value", ContextHolderUtils.getRequest().getParameter(key));
                dr.setValue("Type", "Normal");
                whereDT.Rows.add(dr);
                ht.put(key, ContextHolderUtils.getRequest().getParameter(key));
                continue;
            }

        }
        if(DataType.IsNullOrEmpty(md.getDBSrc())){
            md.setDBSrc("local");
        }
        SFDBSrc dbsrc = new SFDBSrc(md.getDBSrc());
        String dbSrcType = dbsrc.getDBSrcType();
        if(dbSrcType.equals("WebApi") == true)
            md.setDBType(1);
        if(dbSrcType.equals("Dubbo") == false && dbSrcType.equals("WebApi") == false){
            if(expList.toUpperCase().contains("SELECT") == false || expList.trim().contains(" ")==false)
                md.setDBType(2);
        }
        ///#endregion

        ///#region 数据源SQL
        if (md.getDBType() == 0) {

            String mainTable = "A.";
            String mainTablePK = md.getMainTablePK();

            String whereSQL = "";
            boolean isFirstSearchKey = true;
            boolean isFirstDateKey = true;

            for (DataRow dataRow : whereDT.Rows) {
                String key = dataRow.get("Key").toString();

                if (expList.indexOf("@" + getKey()) != -1) {
                    expList = expList.replace("@" + getKey(), dataRow.get("Value").toString());
                    continue;
                }
                String type = dataRow.get("Type").toString();
                if (type.equals("SearchKey") == true) {
                    if (isFirstSearchKey) {
                        isFirstSearchKey = false;
                        whereSQL += " AND (" + mainTable + key + " like '%" + dataRow.get("Value").toString() + "%' ";
                    } else {
                        whereSQL += " OR " + mainTable + key + " like '%" + dataRow.get("Value").toString() + "%' ";
                    }
                }
                if (isFirstSearchKey == false && type.equals("SearchKey") == false) {
                    whereSQL += ")";
                    isFirstSearchKey = true;
                }

                if (type.equals("StringKey") == true) {
                    whereSQL += " AND " + mainTable + key + " like '%" + dataRow.get("Value").toString() + "%' ";
                }
                //时间解析
                if (type.equals("Date") == true) {

                    if (isFirstDateKey == true) {
                        isFirstDateKey = false;
                        whereSQL += " AND (" + mainTable + key + " " + dataRow.get("Oper").toString() + " '" + dataRow.get("Value").toString() + "' ";
                        continue;
                    }
                    if (isFirstDateKey == false) {
                        whereSQL += " AND " + mainTable + key + " " + dataRow.get("Oper").toString() + " '" + dataRow.get("Value").toString() + "')";
                    }
                }
                if (type.equals("Select") == true || type.equals("Normal") == true) {
                    String oper = dataRow.get("Oper").toString();
                    String val = dataRow.get("Value").toString();
                    if (oper.equals("IN") == true) {
                        ddattr = attrs.GetAttrByKeyOfEn(key);
                        if (ddattr != null) {
                            if (ddattr.getItIsNum()) {
                                whereSQL += " AND " + mainTable + key + " " + oper + " (" + val + ") ";
                            } else {
                                val = "('" + val.replace(",", "','") + "')";
                                whereSQL += " AND " + mainTable + key + " " + oper + val;
                            }
                        }

                    } else {
                        whereSQL += " AND " + mainTable + key + " " + oper + " '" + val + "'";
                    }

                }
            }

            if (isFirstSearchKey == false) {
                whereSQL += ")";
            }
            //expCount = expCount + whereSQL;
            //expList = expList + whereSQL;
            String hidenField = md.GetParaString("HidenField");

            if (DataType.IsNullOrEmpty(hidenField) == false) {
                hidenField = hidenField.replace("@WebUser.getNo()", WebUser.getNo());
                hidenField = hidenField.replace("@WebUser.Name", WebUser.getName());
                hidenField = hidenField.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
                hidenField = hidenField.replace("@WebUser.DeptName", WebUser.getDeptName());
                hidenField = hidenField.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
                hidenField = hidenField.replace("@WebUser.DeptNo", WebUser.getDeptNo());
                hidenField = hidenField.replace("@WebUser.OrgNo", WebUser.getOrgNo());

                if (hidenField.indexOf("@") != -1) {
                    return "err@隐藏条件" + hidenField + "还有未替换的_符号";
                }
                whereSQL += " AND (" + hidenField + ")";
            }

            expList = "SELECT * From(" + expList + ") AS A WHERE 1=1 " + whereSQL; //查询列数的
            String expCount = "SELECT Count(*) From(" + expList + ") AS A WHERE 1=1 " + whereSQL; //查询总条数的
            String expPageSize = "SELECT A.OID  From(" + expList + ") AS A WHERE 1=1 " + whereSQL; //查询分页使用的SQL语句

            if (DataType.IsNullOrEmpty(md.getDBSrc()) == true) {
                md.setDBSrc("local");
            }

            expCount = bp.wf.Glo.DealExp(expCount, null, null);
            expPageSize = bp.wf.Glo.DealExp(expPageSize, null, null);



            int count = dbsrc.RunSQLReturnInt(expCount, 0);


            dbsrc.DoQuery(rpts, expList, expPageSize, "OID", attrs, count, this.getPageSize(), this.getPageIdx(), ur.getOrderBy(), false);
            ur.SetPara("RecCount", count);
            ur.Save();
            DataTable dt = rpts.ToDataTableField("DT");
            ds.Tables.add(dt); //把数据加入里面.

        }

        ///#endregion 数据源SQL

        ///#region URL请求数据
        if (md.getDBType() == 1) {
            ht.put("PageSize", this.getPageSize());
            ht.put("PageIdx", this.getPageIdx());
            // 请求的参数作为JSON字符串发送给列表URL
            String postData = bp.tools.Json.ToJson(ht);
            if (DataType.IsNullOrEmpty(md.getExpList()) == true) {
                return "err@根据URL请求数据的URL为空，请检查配置";
            }
            String json = SearchDB_UrlSearchData(md.getExpList(), postData);
            if (DataType.IsNullOrEmpty(json) == true) {
                return "err@根据URL请求数据列表数据为空";
            }
            DataTable jd = Json.ToDataTable(json);
            String count = jd.Rows.get(0).getValue("count").toString();
            if (DataType.IsNullOrEmpty(count) == true) {
                ur.SetPara("RecCount", 0);
            } else {
                ur.SetPara("RecCount", Integer.parseInt(count));
            }
            ur.Save();
            String data = jd.Rows.get(0).getValue("data").toString();
            DataTable dt = new DataTable("DT");
            if (DataType.IsNullOrEmpty(data) == false) {
                dt = bp.tools.Json.ToDataTable(data);
                dt.TableName = "DT";
                ds.Tables.add(dt); //把数据加入里面.
            }

        }

        ///#endregion URL请求数据


        ///#region 存储过程的查询
        if (md.getDBType() == 2) {
            String sql = md.getExpList();
            sql = sql.replace("~", "'");
            Paras paras = new Paras();
            for (Object key : ht.keySet()) {
                paras.add(Integer.valueOf(key.toString()), (Para) ht.get(key));
            }
            String hidenField = md.GetParaString("HidenField");

            if (DataType.IsNullOrEmpty(hidenField) == false) {
                hidenField = hidenField.replace("_WebUser.getNo()", WebUser.getNo());
                hidenField = hidenField.replace("_WebUser.Name", WebUser.getName());
                hidenField = hidenField.replace("_WebUser.FK_DeptName", WebUser.getDeptName());
                hidenField = hidenField.replace("_WebUser.DeptName", WebUser.getDeptName());
                hidenField = hidenField.replace("_WebUser.FK_Dept", WebUser.getDeptNo());
                hidenField = hidenField.replace("_WebUser.DeptNo", WebUser.getDeptNo());
                hidenField = hidenField.replace("_WebUser.OrgNo", WebUser.getOrgNo());

                if (hidenField.indexOf("@") != -1) {
                    return "err@隐藏条件" + hidenField + "还有未替换的_符号";
                }
                String[] strs = hidenField.split(",");
                for (String str : strs) {
                    if (DataType.IsNullOrEmpty(str) == true) {
                        continue;
                    }
                    String[] strVal = str.split("=");
                    if (strVal.length == 1) {
                        paras.Add(strVal[0], "", false);
                    } else {
                        paras.Add(strVal[0], strVal[1], false);
                    }
                }

            }
            if(sql.toLowerCase().startsWith("call") == false)
                sql="CALL "+sql;
            DataTable dt = DBAccess.RunProcReturnTable(sql, paras);
            dt.TableName = "DT";
            ds.Tables.add(dt); //把数据加入里面.
        }

        ///#endregion 存储过程的查询

        return bp.tools.Json.ToJson(ds);
    }

    /**
     * 初始化
     *
     * @return
     */
    public final String GenerBill_Init() throws Exception {
        GenerBills bills = new GenerBills();
        bills.Retrieve(GenerBillAttr.Starter, WebUser.getNo(), null);
        return bills.ToJson("dt");
    }

    /**
     * 查询初始化
     *
     * @return
     */
    public final String SearchData_Init() throws Exception {
        DataSet ds = new DataSet();
        String sql = "";

        String tSpan = this.GetRequestVal("TSpan");
        if (Objects.equals(tSpan, "")) {
            tSpan = null;
        }


        ///#region 1、获取时间段枚举/总数.
        SysEnums ses = new SysEnums("TSpan");
        DataTable dtTSpan = ses.ToDataTableField("dt");
        dtTSpan.TableName = "TSpan";
        ds.Tables.add(dtTSpan);

        GenerBill gb = new GenerBill();
        gb.CheckPhysicsTable();

        sql = "SELECT TSpan as No, COUNT(WorkID) as Num FROM Frm_GenerBill WHERE FrmID='" + this.getFrmID() + "'  AND Starter='" + WebUser.getNo() + "' AND BillState >= 1 GROUP BY TSpan";

        DataTable dtTSpanNum = DBAccess.RunSQLReturnTable(sql);
        for (DataRow drEnum : dtTSpan.Rows) {
            String no = drEnum.get("IntKey").toString();
            for (DataRow dr : dtTSpanNum.Rows) {
                if (Objects.equals(dr.getValue("No").toString(), no)) {
                    drEnum.setValue("Lab", drEnum.get("Lab").toString() + "(" + dr.getValue("Num") + ")");
                    break;
                }
            }
        }

        ///#endregion


        ///#region 2、处理流程类别列表.
        sql = " SELECT  A.BillState as No, B.Lab as Name, COUNT(WorkID) as Num FROM Frm_GenerBill A, " + bp.sys.base.Glo.SysEnum() + " B ";
        sql += " WHERE A.BillState=B.IntKey AND B.EnumKey='BillState' AND  A.Starter='" + WebUser.getNo() + "' AND BillState >=1";
        if (tSpan.equals("-1") == false) {
            sql += "  AND A.TSpan=" + tSpan;
        }

        sql += "  GROUP BY A.BillState, B.Lab  ";

        DataTable dtFlows = DBAccess.RunSQLReturnTable(sql);
        if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None) {
            dtFlows.Columns.get(0).setColumnName("No");
            dtFlows.Columns.get(1).setColumnName("Name");
            dtFlows.Columns.get(2).setColumnName("Num");
        }
        dtFlows.TableName = "Flows";
        ds.Tables.add(dtFlows);

        ///#endregion


        ///#region 3、处理流程实例列表.
        String sqlWhere = "";
        sqlWhere = "(1 = 1)AND Starter = '" + WebUser.getNo() + "' AND BillState >= 1";
        if (tSpan.equals("-1") == false) {
            sqlWhere += "AND (TSpan = '" + tSpan + "') ";
        }

        if (this.getFlowNo() != null) {
            sqlWhere += "AND (FrmID = '" + this.getFrmID() + "')  ";
        } else {
            // sqlWhere += ")";
        }
        sqlWhere += "ORDER BY RDT DESC";

        String fields = " WorkID,FrmID,FrmName,Title,BillState,Starter,StarterName,Sender,RDT ";

        switch (SystemConfig.getAppCenterDBType()) {
            case MySQL:
            case PostgreSQL:
            case UX:
            case HGDB:
            case GBASE8CByMySQL:
            case GBASE8A:
                sql = "SELECT  " + fields + " FROM Frm_GenerBill WHERE " + sqlWhere + " LIMIT 50";
                break;
            case MSSQL:
                sql = "SELECT  TOP 50 " + fields + " FROM Frm_GenerBill WHERE " + sqlWhere;
                break;
            case Oracle:
            case DM:
            case KingBaseR3:
            case KingBaseR6:
            case GBASE8CByOracle:
                sql = "SELECT " + fields + " FROM (SELECT * FROM Frm_GenerBill WHERE " + sqlWhere + ") WHERE rownum <= 50";
                break;
            default:
                throw new RuntimeException("err@没有判断的数据库类型.");
        }


        DataTable mydt = DBAccess.RunSQLReturnTable(sql);
        if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None) {
            mydt.Columns.get(0).ColumnName = "WorkID";
            mydt.Columns.get(1).ColumnName = "FrmID";
            mydt.Columns.get(2).ColumnName = "FrmName";
            mydt.Columns.get(3).ColumnName = "Title";
            mydt.Columns.get(4).ColumnName = "BillState";
            mydt.Columns.get(5).ColumnName = "Starter";
            mydt.Columns.get(6).ColumnName = "StarterName";
            mydt.Columns.get(7).ColumnName = "Sender";
            mydt.Columns.get(8).ColumnName = "RDT";
        }

        mydt.TableName = "Frm_Bill";
        if (mydt != null) {
            mydt.Columns.Add("TDTime");
            for (DataRow dr : mydt.Rows) {
                //   dr["TDTime"] =  GetTraceNewTime(dr["FK_Flow").toString(), int.Parse(dr["WorkID").toString()), int.Parse(dr["FID").toString()));
            }
        }

        ///#endregion

        ds.Tables.add(mydt);

        return bp.tools.Json.ToJson(ds);
    }

    ///#endregion 查询.


    ///#region 单据导出
    public final String Search_Exp() throws Exception {
        FrmBill frmBill = new FrmBill(this.getFrmID());
        GEEntitys rpts = new GEEntitys(this.getFrmID());

        String name = "数据导出";
        String filename = frmBill.getName() + "_" + DataType.getCurrentDateTime() + ".xls";
        String filePath = bp.tools.ExportExcelUtil.ExportDGToExcel(Search_Data(), rpts.getNewEntity(), null, null, filename);
        return filePath;
    }

    public final DataTable Search_Data() throws Exception {
        DataSet ds = new DataSet();


        ///#region 查询语句

        MapData md = new MapData(this.getFrmID());


        //取出来查询条件.
        UserRegedit ur = new UserRegedit(WebUser.getNo(), this.getFrmID() + "_SearchAttrs");

        GEEntitys rpts = new GEEntitys(this.getFrmID());

        Attrs attrs = rpts.getNewEntity().getEnMap().getAttrs();

        QueryObject qo = new QueryObject(rpts);


        ///#region 关键字字段.
        String keyWord = ur.getSearchKey();
        boolean isFirst = true; //是否第一次拼接SQL

        if (!DataType.IsNullOrEmpty(keyWord) && !keyWord.isEmpty()) {
            Attr attrPK = new Attr();
            for (Attr attr : attrs) {
                if (attr.getItIsPK()) {
                    attrPK = attr;
                    break;
                }
            }
            int i = 0;
            String enumKey = ","; //求出枚举值外键.
            for (Attr attr : attrs) {
                switch (attr.getMyFieldType()) {
                    case Enum:
                        enumKey = "," + attr.getKey() + "Text,";
                        break;
                    case FK:
                        continue;
                    default:
                        break;
                }

                if (attr.getMyDataType() != DataType.AppString) {
                    continue;
                }

                //排除枚举值关联refText.
                if (attr.getMyFieldType() == FieldType.RefText) {
                    if (enumKey.contains("," + attr.getKey() + ",")) {
                        continue;
                    }
                }

                if (Objects.equals(attr.getKey(), "FK_Dept") || Objects.equals(attr.getKey(),"DeptNo")) {
                    continue;
                }

                i++;
                if (i == 1) {
                    isFirst = false;
                    /* 第一次进来。 */
                    qo.addLeftBracket();
                    if (isNormalDB()|| Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@")) {
                        qo.AddWhere(attr.getKey(), " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
                    } else {
                        qo.AddWhere(attr.getKey(), " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
                    }
                    continue;
                }
                qo.addOr();
                if (isNormalDB() || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@")) {
                    qo.AddWhere(attr.getKey(), " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? ("CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : ("'%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
                } else {
                    qo.AddWhere(attr.getKey(), " LIKE ", "'%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
                }

            }
            qo.getMyParas().Add("SKey", keyWord, false);
            qo.addRightBracket();
        } else if (!DataType.IsNullOrEmpty(md.GetParaString("StringSearchKeys"))) {
            String field = ""; //字段名
            String fieldValue = ""; //字段值
            int idx = 0;

            //获取查询的字段
            String[] searchFields = md.GetParaString("StringSearchKeys").split("\\*");
            for (String str : searchFields) {
                if (DataType.IsNullOrEmpty(str)) {
                    continue;
                }

                //字段名
                String[] items = str.split(",");
                if (items.length == 2 && DataType.IsNullOrEmpty(items[0])) {
                    continue;
                }
                field = items[0];
                //字段名对应的字段值
                fieldValue = ur.GetParaString(field);
                if (DataType.IsNullOrEmpty(fieldValue)) {
                    continue;
                }
                idx++;
                if (idx == 1) {
                    isFirst = false;
                    /* 第一次进来。 */
                    qo.addLeftBracket();
                    if (isNormalDB() || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@")) {
                        qo.AddWhere(field, " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + field + ",'%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + field + "+'%'"));
                    } else {
                        qo.AddWhere(field, " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + field + "||'%'");
                    }
                    qo.getMyParas().Add(field, fieldValue, false);
                    continue;
                }
                qo.addAnd();

                if (isNormalDB() || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@")) {
                    qo.AddWhere(field, " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? ("CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + field + ",'%')") : ("'%'+" + SystemConfig.getAppCenterDBVarStr() + field + "+'%'"));
                } else {
                    qo.AddWhere(field, " LIKE ", "'%'||" + SystemConfig.getAppCenterDBVarStr() + field + "||'%'");
                }
                qo.getMyParas().Add(field, fieldValue, false);


            }
            if (idx != 0) {
                qo.addRightBracket();
            }
        }


        ///#endregion 关键字段查询


        ///#region 时间段的查询
        if (md.GetParaInt("DTSearchWay", 0) != DTSearchWay.None.getValue() && DataType.IsNullOrEmpty(ur.getDTFrom()) == false) {
            String dtFrom = ur.getDTFrom(); // this.GetTBByID("TB_S_From").Text.Trim().replace("/", "-");
            String dtTo = ur.getDTTo(); // this.GetTBByID("TB_S_To").Text.Trim().replace("/", "-");

            //按日期查询
            if (md.GetParaInt("DTSearchWay", 0) == DTSearchWay.ByDate.getValue()) {
                if (isFirst == false) {
                    qo.addAnd();
                } else {
                    isFirst = false;
                }
                qo.addLeftBracket();
                dtTo += " 23:59:59";
                qo.setSQL(md.GetParaString("DTSearchKey") + " >= '" + dtFrom + "'");
                qo.addAnd();
                qo.setSQL(md.GetParaString("DTSearchKey") + " <= '" + dtTo + "'");
                qo.addRightBracket();
            }

            if (md.GetParaInt("DTSearchWay", 0) == DTSearchWay.ByDateTime.getValue()) {
                //取前一天的24：00
                if (dtFrom.trim().length() == 10) //2017-09-30
                {
                    dtFrom += " 00:00:00";
                }
                if (dtFrom.trim().length() == 16) //2017-09-30 00:00
                {
                    dtFrom += ":00";
                }

                dtFrom = DateUtils.addDay(DateUtils.parse(dtFrom, "yyyy-MM-dd"), -1) + " 24:00";

                if (dtTo.trim().length() < 11 || dtTo.trim().indexOf(' ') == -1) {
                    dtTo += " 24:00";
                }

                if (isFirst == false) {
                    qo.addAnd();
                } else {
                    isFirst = false;
                }
                qo.addLeftBracket();
                qo.setSQL(md.GetParaString("DTSearchKey") + " >= '" + dtFrom + "'");
                qo.addAnd();
                qo.setSQL(md.GetParaString("DTSearchKey") + " <= '" + dtTo + "'");
                qo.addRightBracket();
            }
        }

        ///#endregion 时间段的查询


        ///#region 外键或者枚举的查询

        //获得关键字.
        AtPara ap = new AtPara(ur.getVals());
        for (String str : ap.getHisHT().keySet()) {

            String val = ap.GetValStrByKey(str);
            if (val.equals("all")) {
                continue;
            }
            if (isFirst == false) {
                qo.addAnd();
            } else {
                isFirst = false;
            }

            qo.addLeftBracket();


            if (SystemConfig.getAppCenterDBFieldIsParaDBType() == true) {
                Object typeVal = bp.sys.base.Glo.GenerRealType(attrs, str, ap.GetValStrByKey(str));
                qo.AddWhere(str, typeVal);

            } else {
                qo.AddWhere(str, ap.GetValStrByKey(str));
            }

            qo.addRightBracket();
        }

        ///#endregion 外键或者枚举的查询


        ///#region 设置隐藏字段的过滤查询
        FrmBill frmBill = new FrmBill(this.getFrmID());
        String hidenField = frmBill.GetParaString("HidenField");

        if (DataType.IsNullOrEmpty(hidenField) == false) {
            hidenField = hidenField.replace("_WebUser", "@WebUser");
            hidenField = hidenField.replace("_WebUser.getNo()", WebUser.getNo());
            hidenField = hidenField.replace("_WebUser.Name", WebUser.getName());
            hidenField = hidenField.replace("_WebUser.FK_DeptName", WebUser.getDeptName());
            hidenField = hidenField.replace("_WebUser.DeptName", WebUser.getDeptName());
            hidenField = hidenField.replace("_WebUser.FK_Dept", WebUser.getDeptNo());
            hidenField = hidenField.replace("_WebUser.DeptNo", WebUser.getDeptNo());
            hidenField = hidenField.replace("_WebUser.OrgNo", WebUser.getOrgNo());

            if (isFirst == false) {
                qo.addAnd();
            } else {
                isFirst = false;
            }
            qo.addSQL(hidenField);
        }
        ///#endregion 设置隐藏字段的查询
        if (isFirst == false) {
            qo.addAnd();
        }

        qo.AddWhere("BillState", "!=", 0);

        ///#endregion 查询语句
        qo.addOrderBy("OID");
        qo.DoQuery();
        return rpts.ToDataTableField();

    }

    ///#endregion  执行导出


    ///#region 单据导入
    public final String ImpData_Done() throws Exception {
        if (SystemConfig.getCustomerNo().equals("ASSET") == true) {
            return ImpData_ASSETDone();
        }
        HttpServletRequest request = getRequest();
        long fileSize=0;
        try{
            fileSize = CommonFileUtils.getFilesSize(request, "File_Upload");
        }catch(Exception e){
            fileSize = CommonFileUtils.getFilesSize(request, "file");
        }
        if (fileSize == 0) {
            return "err@请选择要导入的数据信息。";
        }
        String fileName = "";
        try{
            fileName = CommonFileUtils.getOriginalFilename(request, "File_Upload");
        }catch(Exception e){
            fileName = CommonFileUtils.getOriginalFilename(request, "file");
        }

        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!prefix.equals("xls") && !prefix.equals("xlsx")) {

            return "err@上传的文件必须是Excel文件.";
        }

        String errInfo = "";
        String ext = ".xls";
        if (fileName.contains(".xlsx")) {
            ext = ".xlsx";
        }


        //设置文件名
        String fileNewName = DateUtils.format(new Date(), "yyyyMMddHHmmss");

        //文件存放路径
        String filePath = SystemConfig.getPathOfTemp() + "/" + fileNewName+ext;
        try {
            CommonFileUtils.upload(request, "File_Upload", new File(filePath));
        } catch (Exception e) {
            try{
                CommonFileUtils.upload(request, "file",  new File(filePath));
            }catch (Exception e1){
                e1.printStackTrace();
                return "err@执行失败";
            }
        }

        //从excel里面获得数据表.
        DataTable dt = DBLoad.ReadExcelFileToDataTable(filePath, 0);

        //删除临时文件
        (new File(filePath)).delete();

        if (dt.Rows.size() == 0) {
            return "err@没有获取到导入数据，请检查Excle模板表头是否按规范正确设置.";
        }

        //获得entity.
        FrmBill bill = new FrmBill(this.getFrmID());
        if(bill.getEntityType() == EntityType.FrmEntityNoName)
            return EntityNoName_ImpData(dt,bill);
        GEEntitys rpts = new GEEntitys(this.getFrmID());
        GEEntity en = new GEEntity(this.getFrmID());


        String noColName = ""; //编号(针对实体表单).
        String nameColName = ""; //名称(针对实体表单).

        Map map = en.getEnMap();
        Attr attr = map.GetAttrByKey("BillNo");
        noColName = attr.getDesc();
        String codeStruct = bill.getEnMap().getCodeStruct();
        attr = map.GetAttrByKey("Title");
        nameColName = attr.getDesc();

        //定义属性.
        Attrs attrs = map.getAttrs();

        int impWay = this.GetRequestValInt("ImpWay");


        ///#region 清空方式导入.
        //清空方式导入.
        int count = 0; //导入的行数
        int changeCount = 0; //更新的行数
        String successInfo = "";
        if (impWay == 0) {
            rpts.ClearTable();
            GEEntityOID myen = new GEEntityOID(this.getFrmID());

            for (DataRow dr : dt.Rows) {
                String no = "";
                if (dt.Columns.contains(noColName) == true) {
                    no = dr.getValue(noColName).toString();
                }
                String name = "";
                if (dt.Columns.contains(nameColName) == true) {
                    name = dr.getValue(nameColName).toString();
                }
                myen.setOID(0);

                //判断是否是自增序列，序列的格式
                //if (DataType.IsNullOrEmpty(codeStruct) == false && DataType.IsNullOrEmpty(no) == false) {
                //    no = StringHelper.padLeft(no, Integer.parseInt(codeStruct), '0');
               // }

                myen.SetValByKey("BillNo", no);
                if (bill.getEntityType() == EntityType.FrmDict) {
                    if (myen.Retrieve("BillNo", no) == 1) {
                        errInfo += "err@编号[" + no + "][" + name + "]重复.";
                        continue;
                    }
                }


                //给实体赋值
                errInfo += SetEntityAttrVal(no, dr, attrs, myen, dt, 0, bill);
                count++;
                successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + myen.GetValStrByKey("BillNo") + "的导入成功</span><br/>";
            }
        }


        ///#endregion 清空方式导入.


        ///#region 更新方式导入
        if (impWay == 1 || impWay == 2) {
            for (DataRow dr : dt.Rows) {
                String no = "";
                if (dt.Columns.contains(noColName) == true) {
                    no = dr.getValue(noColName).toString();
                }

                String name = "";
                if (dt.Columns.contains(nameColName) == true) {
                    name = dr.getValue(nameColName).toString();
                }
                //判断是否是自增序列，序列的格式
                //if (DataType.IsNullOrEmpty(codeStruct) == false && DataType.IsNullOrEmpty(no) == false) {
                 //   no = StringHelper.padLeft(no, Integer.parseInt(codeStruct), '0');
                //}
                GEEntityOID tempVar = (GEEntityOID) rpts.getNewEntity();
                GEEntityOID myen = tempVar instanceof GEEntityOID ? (GEEntityOID) tempVar : null;
                myen.SetValByKey("BillNo", no);
                if (myen.Retrieve("BillNo", no) == 1 && bill.getEntityType() == EntityType.FrmDict) {
                    //给实体赋值
                    errInfo += SetEntityAttrVal(no,dr, attrs, myen, dt, 1, bill);
                    changeCount++;
                    successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的更新成功</span><br/>";
                    continue;
                }


                //给实体赋值
                errInfo += SetEntityAttrVal(no,dr, attrs, myen, dt, 0, bill);
                count++;
                successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + myen.GetValStrByKey("BillNo") + "的导入成功</span><br/>";
            }
        }

        ///#endregion

        return "errInfo=" + errInfo + "@Split" + "count=" + count + "@Split" + "successInfo=" + successInfo + "@Split" + "changeCount=" + changeCount;
    }
    private String SetEntityAttrVal(String no,DataRow dr, Attrs attrs, GEEntityOID en, DataTable dt, int saveType, FrmBill fbill) throws Exception {

        //单据数据不存在
        if (saveType == 0) {
            long oid = 0;
            if (fbill.getEntityType() == EntityType.FrmDict) {
                oid = bp.ccbill.Dev2Interface.CreateBlankDictID(fbill.getNo(), WebUser.getNo(), null);
            }
            if (fbill.getEntityType() == EntityType.FrmBill) {
                oid = bp.ccbill.Dev2Interface.CreateBlankBillID(fbill.getNo(), WebUser.getNo(), null);
            }
            en.setOID(oid);
            en.RetrieveFromDBSources();
        }

        String errInfo = "";
        //按照属性赋值.
        for (Attr item : attrs) {
            if (item.getKey().equals("BillNo") && dt.Columns.contains(item.getDesc()) == true) {
                en.SetValByKey(item.getKey(), no);
                continue;
            }
            if (item.getKey().equals("Title") && dt.Columns.contains(item.getDesc()) == true) {
                en.SetValByKey(item.getKey(), dr.getValue(item.getDesc()).toString());
                continue;
            }

            if (dt.Columns.contains(item.getDesc()) == false) {
                continue;
            }

            //枚举处理.
            if (item.getMyFieldType() == FieldType.Enum) {
                String val = dr.getValue(item.getDesc()).toString();

                SysEnum se = new SysEnum();
                int i = se.Retrieve(SysEnumAttr.EnumKey, item.getUIBindKey(), SysEnumAttr.Lab, val);

                if (i == 0) {
                    errInfo += "err@枚举[" + item.getKey() + "][" + item.getDesc() + "]，值[" + val + "]不存在.";
                    continue;
                }

                en.SetValByKey(item.getKey(), se.getIntKey());
                continue;
            }

            //外键处理.
            if (item.getMyFieldType() == FieldType.FK) {
                String val = dr.getValue(item.getDesc()).toString();
                Entity attrEn = item.getHisFKEn();
                int i = attrEn.Retrieve("Name", val);
                if (i == 0) {
                    errInfo += "err@外键[" + item.getKey() + "][" + item.getDesc() + "]，值[" + val + "]不存在.";
                    continue;
                }

                if (i != 1) {
                    errInfo += "err@外键[" + item.getKey() + "][" + item.getDesc() + "]，值[" + val + "]重复..";
                    continue;
                }

                //把编号值给他.
                en.SetValByKey(item.getKey(), attrEn.GetValByKey("No"));
                continue;
            }

            //boolen类型的处理..
            if (item.getMyDataType() == DataType.AppBoolean) {
                String val = dr.getValue(item.getDesc()).toString();
                if (Objects.equals(val, "是") || Objects.equals(val, "有")) {
                    en.SetValByKey(item.getKey(), 1);
                } else {
                    en.SetValByKey(item.getKey(), 0);
                }
                continue;
            }
            String myval = dr.getValue(item.getDesc()).toString();
            en.SetValByKey(item.getKey(), myval);
        }
        if (DataType.IsNullOrEmpty(en.GetValStrByKey("BillNo")) == true && DataType.IsNullOrEmpty(fbill.getBillNoFormat()) == false) {
            en.SetValByKey("BillNo", Dev2Interface.GenerBillNo(fbill.getBillNoFormat(), en.getOID(), en, fbill.getNo()));
        }

        if (DataType.IsNullOrEmpty(en.GetValStrByKey("Title")) == true && DataType.IsNullOrEmpty(fbill.getTitleRole()) == false) {
            en.SetValByKey("Title", Dev2Interface.GenerTitle(fbill.getTitleRole(), en));
        }

        en.SetValByKey("BillState", BillState.Editing.getValue());
        en.Update();

        GenerBill gb = new GenerBill();
        gb.setWorkID(en.getOID());
        if (gb.RetrieveFromDBSources() == 0) {
            gb.setBillState(BillState.FrmOver); //初始化状态.
            gb.setStarter(WebUser.getNo());
            gb.setStarterName(WebUser.getName());
            gb.setFrmName(fbill.getName()); //单据名称.
            gb.setFrmID(fbill.getNo()); //单据ID
            if (en.getRow().containsKey("Title") == true) {
                gb.setTitle(en.GetValStringByKey("Title"));
            }
            if (en.getRow().containsKey("BillNo") == true) {
                gb.setBillNo(en.GetValStringByKey("BillNo"));
            }
            gb.setFrmTreeNo(fbill.getFormTreeNo()); //单据类别.
            gb.setRDT(DataType.getCurrentDateTime());
           /* gb.setIdx(1);
            gb.setIdxName("启动");*/
            gb.Insert();

        } else {
            gb.setBillState(BillState.Editing);
            if (en.getRow().containsKey("Title") == true) {
                gb.setTitle(en.GetValStringByKey("Title"));
            }
            if (en.getRow().containsKey("BillNo") == true) {
                gb.setBillNo(en.GetValStringByKey("BillNo"));
            }
            gb.Update();
        }

        return errInfo;
    }
    private String EntityNoName_ImpData(DataTable dt,FrmBill bill) throws Exception {
        String errInfo = "";
        GEEntityNoNames rpts = new GEEntityNoNames(this.getFrmID());
        GEEntityNoName en = new GEEntityNoName(this.getFrmID());


        String noColName = ""; //编号(针对实体表单).
        String nameColName = ""; //名称(针对实体表单).

        Map map = en.getEnMap();
        Attr attr = map.GetAttrByKey("No");
        noColName = attr.getDesc();
        String codeStruct = bill.getEnMap().getCodeStruct();
        attr = map.GetAttrByKey("Name");
        nameColName = attr.getDesc();

        //定义属性.
        Attrs attrs = map.getAttrs();

        int impWay = this.GetRequestValInt("ImpWay");


        ///#region 清空方式导入.
        //清空方式导入.
        int count = 0; //导入的行数
        int changeCount = 0; //更新的行数
        String successInfo = "";
        if (impWay == 0) {
            rpts.ClearTable();
            GEEntityNoName myen = new GEEntityNoName(this.getFrmID());

            for (DataRow dr : dt.Rows) {
                String no = "";
                if (dt.Columns.contains(noColName) == true) {
                    no = dr.getValue(noColName).toString();
                }
                String name = "";
                if (dt.Columns.contains(nameColName) == true) {
                    name = dr.getValue(nameColName).toString();
                }
                //myen.setOID(0);

                //判断是否是自增序列，序列的格式
                /*if (DataType.IsNullOrEmpty(codeStruct) == false && DataType.IsNullOrEmpty(no) == false) {
                    no = StringHelper.padLeft(no, Integer.parseInt(codeStruct), '0');
                }*/

                myen.SetValByKey("No", no);
                if (myen.Retrieve("No", no) == 1) {
                    errInfo += "err@编号[" + no + "][" + name + "]重复.";
                    continue;
                }

                //给实体赋值
                errInfo += SetEntityNoNameAttrVal(no, dr, attrs, myen, dt, 0, bill);
                count++;
                successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + myen.GetValStrByKey("No") + "的导入成功</span><br/>";
            }
        }


        ///#endregion 清空方式导入.


        ///#region 更新方式导入
        if (impWay == 1 || impWay == 2) {
            for (DataRow dr : dt.Rows) {
                String no = "";
                if (dt.Columns.contains(noColName) == true) {
                    no = dr.getValue(noColName).toString();
                }

                String name = "";
                if (dt.Columns.contains(nameColName) == true) {
                    name = dr.getValue(nameColName).toString();
                }
                //判断是否是自增序列，序列的格式
                /*if (DataType.IsNullOrEmpty(codeStruct) == false && DataType.IsNullOrEmpty(no) == false) {
                    no = StringHelper.padLeft(no, Integer.parseInt(codeStruct), '0');
                }*/
                GEEntityNoName tempVar = (GEEntityNoName) rpts.getNewEntity();
                GEEntityNoName myen = tempVar instanceof GEEntityNoName ? (GEEntityNoName) tempVar : null;
                myen.SetValByKey("No", no);
                if (myen.Retrieve("No", no) == 1 ) {
                    //给实体赋值
                    errInfo += SetEntityNoNameAttrVal(no,dr, attrs, myen, dt, 1, bill);
                    changeCount++;
                    successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的更新成功</span><br/>";
                    continue;
                }


                //给实体赋值
                errInfo += SetEntityNoNameAttrVal(no,dr, attrs, myen, dt, 0, bill);
                count++;
                successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + myen.GetValStrByKey("BillNo") + "的导入成功</span><br/>";
            }
        }

        ///#endregion

        return "errInfo=" + errInfo + "@Split" + "count=" + count + "@Split" + "successInfo=" + successInfo + "@Split" + "changeCount=" + changeCount;
    }
    private String SetEntityNoNameAttrVal(String no,DataRow dr, Attrs attrs, GEEntityNoName en, DataTable dt, int saveType, FrmBill fbill) throws Exception {

        //单据数据不存在
        if (saveType == 0) {
            if(DataType.IsNullOrEmpty(no) == true){
                no = bp.ccbill.Dev2Interface.CreateBlankEntityNoName(fbill.getNo(), WebUser.getNo(), null);
                en.setNo(no);
                en.RetrieveFromDBSources();
            }
            else
            {
                en.SetValByKey("RecNo", WebUser.getNo());
                en.SetValByKey("RecName", WebUser.getName());
                en.SetValByKey("DeptNo", WebUser.getDeptNo());
                en.SetValByKey("OrgNo", WebUser.getOrgNo());
                en.SetValByKey("RDT", DataType.getCurrentDate());
                en.Insert();
            }

        }

        String errInfo = "";
        //按照属性赋值.
        for (Attr item : attrs) {
            if (item.getKey().equals("No") && dt.Columns.contains(item.getDesc()) == true) {
                en.SetValByKey(item.getKey(), no);
                continue;
            }
            if (item.getKey().equals("Name") && dt.Columns.contains(item.getDesc()) == true) {
                en.SetValByKey(item.getKey(), dr.getValue(item.getDesc()).toString());
                continue;
            }

            if (dt.Columns.contains(item.getDesc()) == false) {
                continue;
            }

            //枚举处理.
            if (item.getMyFieldType() == FieldType.Enum) {
                String val = dr.getValue(item.getDesc()).toString();

                SysEnum se = new SysEnum();
                int i = se.Retrieve(SysEnumAttr.EnumKey, item.getUIBindKey(), SysEnumAttr.Lab, val);

                if (i == 0) {
                    errInfo += "err@枚举[" + item.getKey() + "][" + item.getDesc() + "]，值[" + val + "]不存在.";
                    continue;
                }

                en.SetValByKey(item.getKey(), se.getIntKey());
                continue;
            }

            //外键处理.
            if (item.getMyFieldType() == FieldType.FK) {
                String val = dr.getValue(item.getDesc()).toString();
                Entity attrEn = item.getHisFKEn();
                int i = attrEn.Retrieve("Name", val);
                if (i == 0) {
                    errInfo += "err@外键[" + item.getKey() + "][" + item.getDesc() + "]，值[" + val + "]不存在.";
                    continue;
                }

                if (i != 1) {
                    errInfo += "err@外键[" + item.getKey() + "][" + item.getDesc() + "]，值[" + val + "]重复..";
                    continue;
                }

                //把编号值给他.
                en.SetValByKey(item.getKey(), attrEn.GetValByKey("No"));
                continue;
            }

            //boolen类型的处理..
            if (item.getMyDataType() == DataType.AppBoolean) {
                String val = dr.getValue(item.getDesc()).toString();
                if (Objects.equals(val, "是") || Objects.equals(val, "有")) {
                    en.SetValByKey(item.getKey(), 1);
                } else {
                    en.SetValByKey(item.getKey(), 0);
                }
                continue;
            }
            String myval = dr.getValue(item.getDesc()).toString();
            en.SetValByKey(item.getKey(), myval);
        }
        /*if (DataType.IsNullOrEmpty(en.GetValStrByKey("BillNo")) == true && DataType.IsNullOrEmpty(fbill.getBillNoFormat()) == false) {
            en.SetValByKey("BillNo", Dev2Interface.GenerBillNo(fbill.getBillNoFormat(), en.getOID(), en, fbill.getNo()));
        }

        if (DataType.IsNullOrEmpty(en.GetValStrByKey("Title")) == true && DataType.IsNullOrEmpty(fbill.getTitleRole()) == false) {
            en.SetValByKey("Title", Dev2Interface.GenerTitle(fbill.getTitleRole(), en));
        }*/

        en.SetValByKey("EntityState", BillState.Editing.getValue());
        en.Update();
        return errInfo;
    }


    ///#endregion

    /**
     * 针对于北京农芯科技的单据导入的处理
     */
    public final String ImpData_ASSETDone() throws Exception {
        HttpServletRequest request = getRequest();
        if (CommonFileUtils.getFilesSize(request, "File_Upload") == 0) {
            return "err@请选择要导入的数据信息。";
        }

        String fileName = CommonFileUtils.getOriginalFilename(request, "File_Upload");
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!prefix.equals("xls") && !prefix.equals("xlsx")) {

            return "err@上传的文件必须是Excel文件.";
        }

        String errInfo = "";
        String ext = ".xls";
        if (fileName.contains(".xlsx")) {
            ext = ".xlsx";
        }


        //设置文件名
        String fileNewName = DateUtils.format(new Date(), "yyyyMMddHHmmss") + ext;

        //文件存放路径
        String filePath = SystemConfig.getPathOfTemp() + "/" + fileNewName;
        try {
            CommonFileUtils.upload(request, "File_Upload", new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
            return "err@执行失败";
        }

        //从excel里面获得数据表.
        DataTable dt = DBLoad.ReadExcelFileToDataTable(filePath, 0);

        //删除临时文件
        (new File(filePath)).delete();

        if (dt.Rows.size() == 0) {
            return "err@无导入的数据";
        }

        //获得entity.
        FrmBill bill = new FrmBill(this.getFrmID());
        GEEntitys rpts = new GEEntitys(this.getFrmID());
        GEEntity en = new GEEntity(this.getFrmID());


        String noColName = ""; //编号(唯一值)
        String nameColName = ""; //名称
        Map map = en.getEnMap();
        //获取表单的主键，合同类的(合同编号),人员信息类的(身份证号),其他(BillNo)
        boolean isContractBill = false;
        boolean isPersonBill = false;

        if (dt.Columns.contains("合同编号") == true) {
            noColName = "合同编号";
            isContractBill = true;
        } else if (dt.Columns.contains("身份证号") == true) {
            noColName = "身份证号";
            isPersonBill = true;
        } else {

            Attr attr = map.GetAttrByKey("BillNo");
            noColName = attr.getDesc();
            attr = map.GetAttrByKey("Title");
            nameColName = attr.getDesc();
        }


        String codeStruct = bill.getEnMap().getCodeStruct();


        //定义属性.
        Attrs attrs = map.getAttrs();

        int impWay = this.GetRequestValInt("ImpWay");


        ///#region 清空方式导入.
        //清空方式导入.
        int count = 0; //导入的行数
        int changeCount = 0; //更新的行数
        String successInfo = "";
        if (impWay == 0) {
            rpts.ClearTable();
            GEEntityOID myen = new GEEntityOID(this.getFrmID());

            for (DataRow dr : dt.Rows) {
                //如果是实体单据,导入的excel必须包含BillNo
                if (bill.getEntityType() == EntityType.FrmDict && dt.Columns.contains(noColName) == false) {
                    return "err@导入的excel不包含编号列";
                }
                String no = "";
                if (dt.Columns.contains(noColName) == true) {
                    no = dr.getValue(noColName).toString();
                }
                String name = "";
                if (dt.Columns.contains(nameColName) == true) {
                    name = dr.getValue(nameColName).toString();
                }
                myen.setOID(0);

                if (isContractBill == false && isPersonBill == false) {
                    //判断是否是自增序列，序列的格式
                    if (DataType.IsNullOrEmpty(codeStruct) == false && DataType.IsNullOrEmpty(no) == false) {
                        no = StringHelper.padLeft(no, Integer.parseInt(codeStruct), '0');
                    }

                    myen.SetValByKey("BillNo", no);
                    if (bill.getEntityType() == EntityType.FrmDict) {
                        if (myen.Retrieve("BillNo", no) == 1) {
                            errInfo += "err@编号[" + no + "][" + name + "]重复.";
                            continue;
                        }
                    }
                }


                //给实体赋值
                errInfo += SetEntityAttrValForASSET(no, dr, attrs, myen, dt, 0, bill);
                count++;
                successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的导入成功</span><br/>";
            }
        }


        ///#endregion 清空方式导入.


        ///#region 更新方式导入
        if (impWay == 1 || impWay == 2) {
            for (DataRow dr : dt.Rows) {
                //如果是实体单据,导入的excel必须包含BillNo
                if (bill.getEntityType() == EntityType.FrmDict && dt.Columns.contains(noColName) == false) {
                    return "err@导入的excel不包含编号列";
                }
                String no = "";
                if (dt.Columns.contains(noColName) == true) {
                    no = dr.getValue(noColName).toString();
                }

                String name = "";
                if (dt.Columns.contains(nameColName) == true) {
                    name = dr.getValue(nameColName).toString();
                }

                GEEntityOID tempVar = (GEEntityOID) rpts.getNewEntity();
                GEEntityOID myen = tempVar instanceof GEEntityOID ? (GEEntityOID) tempVar : null;
                //合同类
                if (isContractBill == true || isPersonBill == true) {
                    Attr attr = map.GetAttrByDesc(noColName);
                    myen.SetValByKey(attr.getKey(), no);
                    //存在就编辑修改数据
                    if (myen.Retrieve(attr.getKey(), no) == 1) {
                        //给实体赋值
                        errInfo += SetEntityAttrValForASSET(no, dr, attrs, myen, dt, 1, bill);
                        changeCount++;
                        successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的更新成功</span><br/>";
                        continue;
                    } else {
                        //给实体赋值
                        errInfo += SetEntityAttrValForASSET(no, dr, attrs, myen, dt, 0, bill);
                        count++;
                        successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的导入成功</span><br/>";
                        continue;
                    }

                } else {
                    //判断是否是自增序列，序列的格式
                    if (DataType.IsNullOrEmpty(codeStruct) == false && DataType.IsNullOrEmpty(no) == false) {
                        no = StringHelper.padLeft(no, Integer.parseInt(codeStruct), '0');
                    }
                    myen.SetValByKey("BillNo", no);
                    if (myen.Retrieve("BillNo", no) == 1 && bill.getEntityType() == EntityType.FrmDict) {
                        //给实体赋值
                        errInfo += SetEntityAttrValForASSET(no, dr, attrs, myen, dt, 1, bill);
                        changeCount++;
                        successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的更新成功</span><br/>";
                        continue;
                    }
                }

                //给实体赋值
                errInfo += SetEntityAttrValForASSET(no, dr, attrs, myen, dt, 0, bill);
                count++;
                successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的导入成功</span><br/>";
            }
        }

        ///#endregion

        return "errInfo=" + errInfo + "@Split" + "count=" + count + "@Split" + "successInfo=" + successInfo + "@Split" + "changeCount=" + changeCount;
    }

    private String SetEntityAttrValForASSET(String no, DataRow dr, Attrs attrs, GEEntityOID en, DataTable dt, int saveType, FrmBill fbill) throws Exception {

        //单据数据不存在
        if (saveType == 0) {
            long oid = 0;
            if (fbill.getEntityType() == EntityType.FrmDict) {
                oid = bp.ccbill.Dev2Interface.CreateBlankDictID(fbill.getNo(), WebUser.getNo(), null);
            }
            if (fbill.getEntityType() == EntityType.FrmBill) {
                oid = bp.ccbill.Dev2Interface.CreateBlankBillID(fbill.getNo(), WebUser.getNo(), null);
            }
            en.setOID(oid);
            en.RetrieveFromDBSources();
        }

        String errInfo = "";
        //按照属性赋值.
        for (Attr item : attrs) {
            if (item.getKey().equals("BillNo") && dt.Columns.contains(item.getDesc()) == true) {
                en.SetValByKey(item.getKey(), no);
                continue;
            }
            if (item.getKey().equals("Title") && dt.Columns.contains(item.getDesc()) == true) {
                en.SetValByKey(item.getKey(), dr.getValue(item.getDesc()).toString());
                continue;
            }

            if (dt.Columns.contains(item.getDesc()) == false) {
                continue;
            }
            String val = dr.getValue(item.getDesc()).toString();
            //枚举处理.
            if (item.getMyFieldType() == FieldType.Enum) {
                SysEnum se = new SysEnum();
                int i = se.Retrieve(SysEnumAttr.EnumKey, item.getUIBindKey(), SysEnumAttr.Lab, val);

                if (i == 0) {
                    errInfo += "err@枚举[" + item.getKey() + "][" + item.getDesc() + "]，值[" + val + "]不存在.";
                    continue;
                }

                en.SetValByKey(item.getKey(), se.getIntKey());
                //en.SetValByKey(item.getKey().replace("Code",""), val);
                continue;
            }


            //外键处理.
            if (item.getMyFieldType() == FieldType.FK) {
                Entity attrEn = item.getHisFKEn();
                int i = attrEn.Retrieve("Name", val);
                if (i == 0) {
                    errInfo += "err@外键[" + item.getKey() + "][" + item.getDesc() + "]，值[" + val + "]不存在.";
                    continue;
                }

                if (i != 1) {
                    errInfo += "err@外键[" + item.getKey() + "][" + item.getDesc() + "]，值[" + val + "]重复..";
                    continue;
                }

                //把编号值给他.
                en.SetValByKey(item.getKey(), attrEn.GetValByKey("No"));
                if (item.getKey().endsWith("BaseCode") == true) {
                    en.SetValByKey(item.getKey().replace("BaseCode", "BaseName"), val);
                } else {
                    en.SetValByKey(item.getKey().replace("Code", ""), val);
                }
                continue;
            }
            //外部数据源
            if (item.getMyFieldType() == FieldType.Normal && item.getMyDataType() == DataType.AppString && item.getUIContralType() == UIContralType.DDL) {
                String uiBindKey = item.getUIBindKey();
                if (DataType.IsNullOrEmpty(uiBindKey) == true) {
                    errInfo += "err@外部数据源[" + item.getKey() + "][" + item.getDesc() + "]，绑定的外键为空";
                }
                DataTable mydt = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null);
                if (mydt.Rows.size() == 0) {
                    errInfo += "err@外部数据源[" + item.getKey() + "][" + item.getDesc() + "],对应的外键没有获取到外键列表";
                }
                boolean isHave = false;

                //给赋值名称
                if (item.getKey().endsWith("BaseCode") == true) {
                    en.SetValByKey(item.getKey().replace("BaseCode", "BaseName"), val);
                } else {
                    en.SetValByKey(item.getKey().replace("Code", ""), val);
                }

                en.SetValByKey(item.getKey() + "T", val);
                for (DataRow mydr : mydt.Rows) {
                    if (mydr.getValue("Name").toString().equals(val) == true) {
                        en.SetValByKey(item.getKey(), mydr.getValue("No").toString());
                        isHave = true;
                        break;
                    }
                }

                if (isHave == false) {
                    errInfo += "err@外部数据源[" + item.getKey() + "][" + item.getDesc() + "],没有获取到" + val + "对应的Code值";
                }


                continue;
            }

            //boolen类型的处理..
            if (item.getMyDataType() == DataType.AppBoolean) {
                if (Objects.equals(val, "是") || Objects.equals(val, "有")) {
                    en.SetValByKey(item.getKey(), 1);
                } else {
                    en.SetValByKey(item.getKey(), 0);
                }
                continue;
            }
            if (item.getMyDataType() == DataType.AppDate) {
                if (DataType.IsNullOrEmpty(val) == false) {

                }

            }

            if (item.getKey().endsWith("BaseName") == true) {
                bp.port.Depts depts = new bp.port.Depts();
                depts.Retrieve(bp.port.DeptAttr.Name, val, null);
                if (!depts.isEmpty()) {
                    en.SetValByKey(item.getKey().replace("BaseName", "BaseCode"), (depts.get(0) instanceof bp.port.Dept ? (bp.port.Dept) depts.get(0) : null).getNo());
                }
                en.SetValByKey(item.getKey(), val);
                continue;
            } else {
                if (item.getKey().equals("CI_SmallBusinessFormatCode")) {
                    String mypk = "MultipleChoiceSmall_" + fbill.getNo() + "_" + item.getKey();
                    MapExt mapExt = new MapExt();
                    mapExt.setMyPK(mypk);
                    if (mapExt.RetrieveFromDBSources() == 1 && mapExt.getDoWay().equals("3") && DataType.IsNullOrEmpty(mapExt.getTag3()) == false) {
                        String newVal = "," + val + ",";
                        String keyVal = "";
                        DataTable dataTable = bp.pub.PubClass.GetDataTableByUIBineKey(mapExt.getTag3(), null);
                        for (DataRow drr : dataTable.Rows) {
                            if (drr.getValue("Name") != null && newVal.contains("," + drr.getValue("Name").toString() + ",") == true) {
                                keyVal += drr.getValue("No").toString() + ",";
                            }
                        }
                        keyVal = keyVal.substring(0, keyVal.length() - 1);

                        en.SetValByKey(item.getKey(), keyVal);
                        en.SetValByKey(item.getKey().replace("Code", ""), val);
                        en.SetValByKey(item.getKey() + "T", val);
                    } else {
                        en.SetValByKey(item.getKey(), val);
                    }
                } else {
                    if (item.getItIsNum()) {
                        if (DataType.IsNullOrEmpty(val) == true || val.equals("null") == true) {
                            val = "0";
                        }
                    }
                    en.SetValByKey(item.getKey(), val);
                }


            }


        }
        if (DataType.IsNullOrEmpty(en.GetValStrByKey("BillNo")) == true && DataType.IsNullOrEmpty(fbill.getBillNoFormat()) == false) {
            en.SetValByKey("BillNo", Dev2Interface.GenerBillNo(fbill.getBillNoFormat(), en.getOID(), en, fbill.getNo()));
        }

        if (DataType.IsNullOrEmpty(en.GetValStrByKey("Title")) == true && DataType.IsNullOrEmpty(fbill.getTitleRole()) == false) {
            en.SetValByKey("Title", Dev2Interface.GenerTitle(fbill.getTitleRole(), en));
        }
        en.SetValByKey("Rec", WebUser.getNo());
        en.SetValByKey("BillState", BillState.Editing.getValue());
        en.SetValByKey("WFState", WFState.CompleteEnd);
        en.Update();

        GenerBill gb = new GenerBill();
        gb.setWorkID(en.getOID());
        if (gb.RetrieveFromDBSources() == 0) {
            gb.setBillState(BillState.FrmOver); //初始化状态.
            gb.setStarter(WebUser.getNo());
            gb.setStarterName(WebUser.getName());
            gb.setFrmName(fbill.getName()); //单据名称.
            gb.setFrmID(fbill.getNo()); //单据ID
            if (en.getRow().containsKey("Title") == true) {
                gb.setTitle(en.GetValStringByKey("Title"));
            }
            if (en.getRow().containsKey("BillNo") == true) {
                gb.setBillNo(en.GetValStringByKey("BillNo"));
            }
            gb.setFrmTreeNo(fbill.getFormTreeNo()); //单据类别.
            gb.setRDT(DataType.getCurrentDateTime());
//            gb.setIdx(1);
//            gb.setIdxName("启动");
            gb.Insert();

        } else {
            gb.setBillState(BillState.Editing);
            if (en.getRow().containsKey("Title") == true) {
                gb.setTitle(en.GetValStringByKey("Title"));
            }
            if (en.getRow().containsKey("BillNo") == true) {
                gb.setBillNo(en.GetValStringByKey("BillNo"));
            }
            gb.Update();
        }

        return errInfo;
    }


    ///#region 执行父类的重写方法.

    /**
     * 默认执行的方法
     *
     * @return
     */
    @Override
    protected String DoDefaultMethod() {
        switch (this.getDoType()) {
            case "DtlFieldUp": //字段上移
                return "执行成功.";
            default:
                break;
        }

        //找不不到标记就抛出异常.
        throw new RuntimeException("@标记[" + this.getDoType() + "]，没有找到. @RowURL:" + getRequest().getRequestURL().toString() + "?" + getRequest().getQueryString());
    }

    ///#endregion 执行父类的重写方法.


    ///#region 获得demo信息.
    public final String MethodDocDemoJS_Init() throws Exception {
        MethodFunc func = new MethodFunc(this.getMyPK());
        return func.getMethodDocJavaScriptDemo();
    }

    public final String MethodDocDemoSQL_Init() throws Exception {
        MethodFunc func = new MethodFunc(this.getMyPK());
        return func.getMethodDocSQLDemo();
    }

    ///#endregion 获得demo信息.


    ///#region 处理SQL文中注释信息.
    public static String MidStrEx(String sourse, String startstr, String endstr) {
        int startindex, endindex;
        String tmpstr = "";
        String tmpstr2 = "";
        try {
            startindex = sourse.indexOf(startstr);
            if (startindex == -1) {
                return sourse;
            }
            int i = 0;
            while (startindex != -1) {
                if (i == 0) {
                    endindex = sourse.indexOf(endstr);
                    if (startindex != 0) {
                        endindex = endindex - startindex;
                    }
                    tmpstr = StringHelper.remove(sourse, startindex, endindex + endstr.length());
                } else {
                    endindex = tmpstr.indexOf(endstr);
                    if (startindex != 0) {
                        endindex = endindex - startindex;
                    }
                    tmpstr = StringHelper.remove(tmpstr, startindex, endindex + endstr.length());

                }

                if (endindex == -1) {
                    return tmpstr;
                }
                // tmpstr = tmpstr.Substring(endindex + endstr.length());
                startindex = tmpstr.indexOf(startstr);
                i++;
            }
            //result = tmpstr.Remove(endindex);

        } catch (RuntimeException ex) {
            Log.DebugWriteError("MidStrEx Err:" + ex.getMessage());
        }
        return tmpstr;
    }

    ///#endregion 处理SQL文中注释信息..


    ///#region 实体单据查询启动指定子流程显示的字段
    public final String DictFlow_MapAttrs() throws Exception {
        DataSet ds = new DataSet();
        String fk_mapData = "ND" + Integer.parseInt(this.getFlowNo()) + "01";

        //查询出单流程的所有字段
        MapAttrs mattrs = new MapAttrs();
        mattrs.Retrieve(MapAttrAttr.FK_MapData, fk_mapData, MapAttrAttr.Idx);

        ds.Tables.add(mattrs.ToDataTableField("Sys_MapAttr"));

        MapAttrs mattrsOfSystem1 = new MapAttrs();
        //判断表单中是否存在默认值@WebUser.getNo(),@WebUser.FK_Dept,@RDT
        boolean isHaveNo = false;
        boolean isHaveRDT = false;
        boolean isHaveTitle = false;

        //系统字段字符串
        String sysFields = "";
        for (MapAttr mapAttr : mattrs.ToJavaList()) {

            if (mapAttr.getKeyOfEn().equals(GERptAttr.Rec) || mapAttr.getKeyOfEn().equals(GERptAttr.RDT) || mapAttr.getKeyOfEn().equals(GERptAttr.CDT)) {
                continue;
            }
            if (mapAttr.getKeyOfEn().equals(GERptAttr.Title) == true) {
                mattrsOfSystem1.AddEntity(mapAttr);
                isHaveTitle = true;
                continue;
            }

            switch (mapAttr.getDefValReal()) {

                case "@WebUser.getNo()":
                case "@WebUser.Name":
                    sysFields += "," + mapAttr.getKeyOfEn();
                    isHaveNo = true;
                    mattrsOfSystem1.AddEntity(mapAttr);

                    break;

                case "@RDT":
                    mattrsOfSystem1.AddEntity(mapAttr);
                    isHaveRDT = true;
                    sysFields += "," + mapAttr.getKeyOfEn();
                    break;
                default:
                    break;
            }
        }


        //默认显示的系统字段 标题、发起人、发起时间、当前所在节点、状态 , 系统字段需要在RPT中查找
        String fields = "(";
        if (isHaveTitle == false) {
            fields += "'" + GERptAttr.Title + "',";
        }
        if (isHaveNo == false) {
            fields += "'" + GERptAttr.FlowStarter + "',";
        }

        if (isHaveRDT == false) {
            fields += "'" + GERptAttr.FlowStartRDT + "',";
        }
        fields += "'" + GERptAttr.WFState + "','" + GERptAttr.FlowEndNode + "')";
        MapAttrs mattrsOfSystem = new MapAttrs();
        QueryObject qo = new QueryObject(mattrsOfSystem);
        qo.AddWhere(MapAttrAttr.FK_MapData, "ND" + Integer.parseInt(this.getFlowNo()) + "Rpt");
        qo.addAnd();
        qo.AddWhereIn(MapAttrAttr.KeyOfEn, fields);
        //qo.addOrderBy(MapAttrAttr.Idx);
        //qo.addOrderByOfSelf("CHARINDEX(" + MapAttrAttr.getKeyOfEn() + ",'" + fields.replace("'", "") + "')");
        qo.DoQuery();
        mattrsOfSystem.AddEntities(mattrsOfSystem1);

        ds.Tables.add(mattrsOfSystem.ToDataTableField("Sys_MapAttrOfSystem"));

        //系统字段字符串
        fields = fields.replace("(", "").replace(")", "").replace("'", "") + ",";
        sysFields += ",OID,FID,RDT,CDT,Rec,FK_Dept,DeptNo,MyNum,FK_NY,Emps,Title," + fields;
        DataTable dt = new DataTable();
        dt.Columns.Add("Field");
        dt.TableName = "Sys_Fields";
        DataRow dr = dt.NewRow();
        dr.setValue("Field", sysFields);
        dt.Rows.add(dr);
        ds.Tables.add(dt);

        //用户查询注册信息中记录使用到的流程业务表中的字段
        UserRegedit ur = new UserRegedit(WebUser.getNo(), "ND" + Integer.parseInt(this.getFlowNo()) + "Rpt_SearchAttrs");
        ur.SetPara("RptField", "," + fields);
        ur.Update();

        return bp.tools.Json.ToJson(ds);
    }

    ///#endregion


    ///#region 实体单据启动多个子流程的查询
    public final String DictFlow_Search() throws Exception {
        //实体单据的信息
        String frmID = this.GetRequestVal("FrmID");
        String frmOID = this.GetRequestVal("FrmOID");

        //表单编号
        String fk_mapData = "ND" + Integer.parseInt(this.getFlowNo()) + "01";

        //当前用户查询信息表
        UserRegedit ur = new UserRegedit(WebUser.getNo(), "ND" + Integer.parseInt(this.getFlowNo()) + "Rpt_SearchAttrs");

        //表单属性
        MapData mapData = new MapData(fk_mapData);

        //流程的系统字段
        String rptFields = ur.GetParaString("RptField");
        rptFields = rptFields.substring(1, rptFields.length());
        rptFields = "('" + rptFields.replace(",", "','") + "'" + ",'" + GERptAttr.FlowStarter + "','" + GERptAttr.FK_Dept + "','" + GERptAttr.FlowEmps + "','" + GERptAttr.FlowEndNode + "','" + GERptAttr.PWorkID + "','" + GERptAttr.PFlowNo + "')";
        MapAttrs mattrsOfSystem = new MapAttrs();
        QueryObject qo = new QueryObject(mattrsOfSystem);
        qo.AddWhere(MapAttrAttr.FK_MapData, "ND" + Integer.parseInt(this.getFlowNo()) + "Rpt");
        qo.addAnd();
        qo.AddWhereIn(MapAttrAttr.KeyOfEn, rptFields);
        qo.DoQuery();

        //流程表单对应的所有字段
        MapAttrs attrs = new MapAttrs();
        attrs.Retrieve(MapAttrAttr.FK_MapData, fk_mapData, MapAttrAttr.Idx);
        attrs.AddEntities(mattrsOfSystem);

        //流程表单对应的流程数据
        GEEntitys ens = new GEEntitys(fk_mapData);
        bp.en.Entity tempVar = ens.getNewEntity();
        GEEntity en = tempVar instanceof GEEntity ? (GEEntity) tempVar : null;
        for (MapAttr mapAttr : mattrsOfSystem.ToJavaList()) {
            en.getEnMap().AddAttr(mapAttr.getHisAttr());
        }
        Cache.getSQL_Cache().remove(fk_mapData);

        qo = new QueryObject(ens);
        qo.AddWhere(GERptAttr.PWorkID, frmOID);
        qo.addAnd();
        qo.AddWhere(GERptAttr.PFlowNo, frmID);
        qo.AddWhere(" AND  WFState > 1 ");
        //qo.addAnd();
        qo.AddWhere(" AND FID = 0 ");
        if (DataType.IsNullOrEmpty(ur.getOrderBy()) == false) {
            if (ur.getOrderWay().toUpperCase().equals("DESC") == true) {
                qo.addOrderByDesc(ur.getOrderBy());
            } else {
                qo.addOrderBy(ur.getOrderBy());
            }
        }
        ur.Update();
        qo.DoQuery();

        return bp.tools.Json.ToJson(ens.ToDataTableField("FlowSearch_Data"));
    }

    ///#endregion  实体单据启动多个子流程的查询

    public final String RefDict_CreateBillWorkID() throws Exception {
        long refOID = GetRequestValInt64("RefOID");
        String refDict = GetRequestVal("RefDict");
        //获取关联实体表单的数据信息
        GERpt refRpt = new GERpt(refDict, refOID);
        String billNo = this.GetRequestVal("BillNo");
        long workID = bp.ccbill.Dev2Interface.CreateBlankBillID(this.getFrmID(), WebUser.getNo(), null, billNo);

        GenerBill gb = new GenerBill(workID);
        gb.setBillState(BillState.Draft);
        gb.Update();
        //获取当前单据表单的数据信息
        GERpt rpt = new GERpt(this.getFrmID(), workID);
        rpt.Copy(refRpt);
        rpt.SetValByKey("BillState", gb.getBillState().getValue());
        rpt.Update();
        return String.valueOf(workID);
    }


    ///#region 外部流程网页授权URL
    public final String DictFlow_Qcode() throws Exception {
        String state = "FlowNo_" + this.getFlowNo() + "|OrgNo_" + WebUser.getOrgNo() + "|FrmID_" + this.getFrmID() + "|FrmOID_" + this.GetRequestVal("FrmOID");
        //回调url
        String redirect_uri = Encodes.urlEncode("http://www.ccbpm.cn/WF/CCBill/DictFlowStart.htm");
        //授权链接
        String oatuth2 = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + SystemConfig.getAppID() + "&redirect_uri=" + redirect_uri + "&response_type=code&scope=snsapi_userinfo&&state=" + state + "#wechat_redirect";
        return oatuth2;
    }

    ///#endregion 外部流程网页授权URL
}
