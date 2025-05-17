package bp.ccbill;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.difference.handler.CommonFileUtils;
import bp.sys.*;
import bp.en.*;
import bp.tools.DateUtils;
import bp.wf.httphandler.*;
import bp.wf.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;

import static bp.sys.Glo.getRequest;

/**
 * 页面功能实体
 */
public class WF_CCBill_OptComponents extends bp.difference.handler.DirectoryPageBase {

    ///#region 构造方法.

    /**
     * 构造函数
     */
    public WF_CCBill_OptComponents() {
    }

    ///#endregion 构造方法.


    ///#region 关联单据.

    /**
     * 设置父子关系.
     *
     * @return
     */
    public final String RefBill_Done() throws Exception {
        try {
            String frmID = this.GetRequestVal("FrmID");
            long workID = this.GetRequestValInt64("WorkID");
            GERpt rpt = new GERpt(frmID, workID);

            String pFrmID = this.GetRequestVal("PFrmID");
            long pWorkID = this.GetRequestValInt64("PWorkID");

            //把数据copy到当前的子表单里.
            GERpt rptP = new GERpt(pFrmID, pWorkID);
            rpt.Copy(rptP);
            rpt.setPWorkID(pWorkID);
            rpt.SetValByKey("PFrmID", pFrmID);
            rpt.Update();

            //更新控制表,设置父子关系.
            GenerBill gbill = new GenerBill(workID);
            gbill.setPFrmID(pFrmID);
            gbill.setPWorkID(pWorkID);
            gbill.Update();
            return "执行成功";
        } catch (RuntimeException ex) {
            return "err@" + ex.getMessage();
        }
    }

    /**
     * 单据初始化
     *
     * @return
     */
    public final String RefBill_Init() throws Exception {
        DataSet ds = new DataSet();


        ///#region 查询显示的列
        MapAttrs mattrs = new MapAttrs();
        mattrs.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.Idx);

        DataRow row = null;
        DataTable dt = new DataTable("Attrs");
        dt.Columns.Add("KeyOfEn", String.class);
        dt.Columns.Add("Name", String.class);
        dt.Columns.Add("Width", Integer.class);
        dt.Columns.Add("UIContralType", Integer.class);
        dt.Columns.Add("LGType", Integer.class);

        //设置标题、单据号位于开始位置


        for (MapAttr attr : mattrs.ToJavaList()) {
            String searchVisable = attr.getatPara().GetValStrByKey("SearchVisable");
            if (Objects.equals(searchVisable, "0")) {
                continue;
            }
            if (attr.getUIVisible() == false) {
                continue;
            }
            row = dt.NewRow();
            row.setValue("KeyOfEn", attr.getKeyOfEn());
            row.setValue("Name", attr.getName());
            row.setValue("Width", attr.getUIWidthInt());
            row.setValue("UIContralType", attr.getUIContralType().getValue());
            row.setValue("LGType", attr.getLGType().getValue());
            dt.Rows.add(row);
        }
        ds.Tables.add(dt);

        ///#endregion 查询显示的列


        ///#region 查询语句

        MapData md = new MapData(this.getFrmID());

        GEEntitys rpts = new GEEntitys(this.getFrmID());

        Attrs attrs = rpts.getNewEntity().getEnMap().getAttrs();

        QueryObject qo = new QueryObject(rpts);


        ///#region 关键字字段.
        String keyWord = this.GetRequestVal("SearchKey");

        if (DataType.IsNullOrEmpty(keyWord) == false && keyWord.length() >= 1) {
            qo.addLeftBracket();
            if (Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "?")) {
                qo.AddWhere("Title", " LIKE ", bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : (" '%'+" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
            } else {
                qo.AddWhere("Title", " LIKE ", " '%'||" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
            }
            qo.addOr();
            if (Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "?")) {
                qo.AddWhere("BillNo", " LIKE ", bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL ? ("CONCAT('%'," + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : ("'%'+" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
            } else {
                qo.AddWhere("BillNo", " LIKE ", "'%'||" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
            }

            qo.getMyParas().Add("SKey", keyWord, false);
            qo.addRightBracket();

        } else {
            qo.AddHD();
        }

        ///#endregion 关键字段查询


        ///#region 时间段的查询
        String dtFrom = this.GetRequestVal("DTFrom");
        String dtTo = this.GetRequestVal("DTTo");
        if (DataType.IsNullOrEmpty(dtFrom) == false) {

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

            qo.addAnd();
            qo.addLeftBracket();
            qo.setSQL(" RDT>= '" + dtFrom + "'");
            qo.addAnd();
            qo.setSQL("RDT <= '" + dtTo + "'");
            qo.addRightBracket();
        }

        ///#endregion 时间段的查询

        qo.DoQuery("OID", this.getPageSize(), this.getPageIdx());


        ///#endregion

        DataTable mydt = rpts.ToDataTableField("dt");
        mydt.TableName = "DT";

        ds.Tables.add(mydt); //把数据加入里面.

        return bp.tools.Json.ToJson(ds);
    }

    ///#endregion 关联单据.


    ///#region 数据版本.

    /**
     * 初始化
     *
     * @return
     */
    public final String DataVer_Init() {
        return null;
    }

    public final String DataVer_AppFieldData() throws Exception {
        try {
            int verNum = this.GetRequestValInt("VerNum");

            EnVer en = new EnVer();
            int i = en.Retrieve(EnVerAttr.EnVer, verNum, EnVerAttr.FrmID, this.getFrmID(), EnVerDtlAttr.EnPKValue, this.getWorkID());
            if (i == 0) {
                return "err@版本号输入错误";
            }

            String keyOfEn = this.GetRequestVal("KeyOfEn");
            EnVerDtl dtl = new EnVerDtl();
            i = dtl.Retrieve(EnVerDtlAttr.RefPK, en.getMyPK(), EnVerDtlAttr.AttrKey, keyOfEn);

            if (i == 0) {
                return "err@该版本下没有查询到字段[" + keyOfEn + "]的值.";
            }

            GEEntity ge = new GEEntity(this.getFrmID(), this.getWorkID());
            ge.SetValByKey(keyOfEn, dtl.getMyVal());
            ge.Update();

            String msg = "字段：[" + dtl.getAttrKey() + "],已经修改为:" + dtl.getMyVal();

            bp.ccbill.Dev2Interface.WriteTrack(this.getFrmID(), String.valueOf(this.getWorkID()), FrmActionType.DataVerReback, msg);
            return msg;
        } catch (RuntimeException ex) {
            return "err@" + ex.getMessage();
        }
    }

    public String Track_Init() throws Exception {
        String workid = this.GetRequestVal("WorkID");

        bp.ccbill.Tracks ens = new bp.ccbill.Tracks();
        QueryObject qo = new QueryObject(ens);
        qo.AddWhere("WorkID", workid);
        qo.addAnd();
        qo.addLeftBracket();
        qo.AddWhere("FrmID", this.getFrmID());
        qo.addOr();
        qo.AddWhere("FrmID", this.GetRequestVal("PTable"));
        qo.addRightBracket();
        qo.addOrderBy("RDT");

        qo.Top = 200;
        qo.DoQuery();

        return ens.ToJson();
    }
    /**
     * 回滚数据
     *
     * @return
     */
    public final String DataVer_Reback() throws Exception {
        EnVerDtls dtls = new EnVerDtls();
        dtls.Retrieve(EnVerDtlAttr.RefPK, this.getMyPK(), null);

        GEEntity ge = new GEEntity(this.getFrmID(), this.getWorkID());

        for (EnVerDtl item : dtls.ToJavaList()) {
            if (item.getLGType() == 0) {
                ge.SetValByKey(item.getAttrKey(), item.getMyVal());
                continue;
            }

            //外键枚举存储的格式为 [0][女]    [01][山东]
            String val = item.getMyVal();
            ge.SetValByKey(item.getAttrKey(), val);

        }
        ge.Update();

        // BP.CCBill.Dev2Interface.MyEntityTree_Delete

        bp.ccbill.Dev2Interface.WriteTrack(this.getFrmID(), String.valueOf(this.getWorkID()), FrmActionType.DataVerReback, "数据回滚");
        return "已经成功还原...";
    }

    public final String DataVer_NewVer() throws Exception {
        GEEntity en = new GEEntity(this.getFrmID(), this.getWorkID());
        EnVer.NewVer(en);
        bp.ccbill.Dev2Interface.WriteTrack(this.getFrmID(), String.valueOf(this.getWorkID()), FrmActionType.DataVerReback, "创建数据版本.");
        return "创建成功.";

        ////创建实体.
        //EnVer ev = new EnVer();
        //ev.setMyPK(DBAccess.GenerGUID());
        //ev.setRecNo(WebUser.getNo();
        //ev.RecName = WebUser.getName();
        //ev.RDT = DataType.getCurrentDateTime()CN;
        //ev.FrmID = this.FrmID;
        //ev.EnPKValue = this.WorkID.ToString();
        //ev.MyNote = this.GetRequestVal("MyNote");

        //MapData md = new MapData(this.FrmID);
        //ev.setName(md.Name;

        //// 获得最大的版本号.
        //int maxVer = DBAccess.RunSQLReturnValInt("SELECT MAX(EnVer) as Num FROM Sys_EnVer WHERE  FrmID='" + this.getFrmID() + "' AND EnPKValue='" + this.WorkID + "'", 0);

        //ev.Ver = maxVer + 1; //设置版本号.
        //ev.Insert(); //执行插入.

        ////创建变更数据.
        //EnVerDtl dtl = new EnVerDtl();

        ////不需要存储的字段.
        //String sysFiels = ",AtPara,OID,WorkID,WFState,BillNo,Title,RDT,CDT,OrgNo,Starter,StarterName,BillState,FK_Dept,";

        //MapAttrs mattrs = new MapAttrs(this.FrmID);
        //foreach (MapAttr attr in mattrs)
        //{
        //    //如果是非数据控件.
        //    if ((int)attr.UIContralType >= 4)
        //        continue;

        //    if (sysFiels.contains("," + attr.getKeyOfEn() + ",") == true)
        //        continue;

        //    dtl.setMyPK(DBAccess.GenerGUID());
        //    dtl.setRefPK(ev.MyPK; //设置关联主键.

        //    dtl.getFrmID() = ev.FrmID;
        //    dtl.EnPKValue = this.WorkID.ToString(); //设置为主键.

        //    dtl.AttrKey = attr.getKeyOfEn();
        //    dtl.AttrName = attr.Name;

        //    //逻辑类型.
        //    dtl.setLGType((int)attr.LGType;

        //    //if (attr.getLGType() == FieldType.Enum)
        //    //    dtl.setLGType(1;
        //    //if (attr.getMyFieldType() == FieldType.FK)
        //    //    dtl.setLGType(2;

        //    //设置外键.
        //    dtl.BindKey = attr.getUIBindKey();
        //    if (attr.getLGType() == FieldTypeS.Normal)
        //    {
        //        //设置值.
        //        dtl.MyVal = en.GetValByKey(attr.getKeyOfEn()).ToString();
        //    }
        //    else
        //    {
        //        //设置值.
        //        dtl.MyVal = "[" + en.GetValByKey(attr.getKeyOfEn()).ToString() + "][" + en.GetValRefTextByKey(attr.getKeyOfEn()) + "]";
        //    }
        //    dtl.Insert();
        //}

        //BP.CCBill.Dev2Interface.Dict_AddTrack(this.FrmID, this.WorkID.ToString(), FrmActionType.DataVerReback, "创建数据版本.");

        //return "版本创建成功.";
    }

    ///#endregion 数据版本.


    ///#region 二维码.

    /**
     * 初始化
     *
     * @return
     */
    public final String QRCode_Init() throws Exception {
        String workIDStr = this.GetRequestVal("WorkID");

        String url = SystemConfig.getHostURL()+ "/WF/CCBill/OptComponents/QRCode.htm?DoType=MyDict&WorkID=" + workIDStr + "&FrmID=" + this.getFrmID() + "&MethodNo=" + this.GetRequestVal("MethodNo");

        bp.tools.QrCodeUtil.createQrCode(url,SystemConfig.getPathOfTemp(),workIDStr+".png","png");

        //返回url.
        return url;
    }

    public final String QRCode_Init_Vue3() throws Exception {
        String workIDStr = this.GetRequestVal("WorkID");
        String menuNo = this.GetRequestVal("MenuNo");

        String url = bp.difference.SystemConfig.getHostURLVue3() + "/#/CCMobile/ScanGuide?DoType=MyDict&WorkID=" + workIDStr + "&FrmID=" + this.getFrmID() + "&MethodNo=" + this.GetRequestVal("MethodNo") + "&MenuNo=" + menuNo;

        bp.tools.QrCodeUtil.createQrCode(url,SystemConfig.getPathOfTemp(),workIDStr+".png","png");

        //返回url.
        return url;
    }

    /**
     * 扫描要做的工作
     *
     * @return
     */
    public final String QRCodeScan_Init() {
        String doType = this.getDoType();
        String url = "";

        url = "/CCMobile/CCBill/MyDict.htm?WorkID=" + this.getWorkID() + "&FrmID=" + this.getFrmID() + "&MethodNo=" + this.GetRequestVal("MethodNo") + "&IsReadonly=" + GetRequestValInt("IsReadonly");

        //返回url.
        return url;
    }

    ///#endregion 二维码.


    ///#region 评论回复附件上传
    public final String FrmBBs_UploadFile() throws Exception {
        HttpServletRequest request = getRequest();
        String contentType = request.getContentType();
        if (contentType == null || contentType.indexOf("multipart/form-data") == -1)
            return "err@附件上传页面Form表单类型设置错误";
        MultipartHttpServletRequest mrequest = CommonFileUtils.getMultipartHttpServletRequest(request);
        if(mrequest.getFileMap().size()==0)
            return "err@获取附件信息有误。";

        //上传附件
        MultipartFile file = mrequest.getFile("file");
        String filepath = bp.difference.SystemConfig.getPathOfDataUser() + "UploadFile" + File.separator + "FrmBBS"+ File.separator + DataType.getCurrentYearMonth();
        if ((new File(filepath)).isDirectory() == false) {
            (new File(filepath)).mkdirs();
        }
        filepath = filepath + File.separator + DBAccess.GenerGUID(0, null, null) + file.getOriginalFilename();
        try
        {
            CommonFileUtils.upload(request, "file", new File(filepath));
        }
        catch (RuntimeException ex)
        {
        }
        bp.ccbill.FrmBBS bbs = new bp.ccbill.FrmBBS(this.getNo());
        String fileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
        bbs.SetValByKey("MyFileName", fileName);
        bbs.SetValByKey("MyFilePath", filepath);
        bbs.SetValByKey("MyFileExt", file.getOriginalFilename().replace(fileName, ""));
        bbs.Update();
        return "附件保存成功";
    }

    ///#endregion 评论回复附件上传

}
