package bp.wf.httphandler;

import bp.ccbill.DBList;
import bp.da.*;
import bp.difference.handler.CommonFileUtils;
import bp.pub.PubClass;
import bp.sys.*;
import bp.tools.*;
import bp.web.*;
import bp.wf.CCFormAPI;
import bp.wf.Glo;
import bp.wf.minio.MinioHelper;
import bp.wf.template.*;
import bp.en.*;
import bp.difference.*;
import bp.sys.frmui.*;
import bp.wf.template.sflow.*;
import bp.wf.template.frm.*;
import bp.wf.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.List;

/**
 * 表单功能界面
 */
public class WF_CCForm extends bp.difference.handler.DirectoryPageBase {
    /**
     * 根据拼音
     *
     * @return
     */
    public String FrmAPI_GenerMyDataType() throws Exception {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(this.GetRequestVal("name"));
        return bp.sys.CCFormAPI.GenerDataTypeByCHName(arrayList);
    }

    /**
     * 根据中文字段名获得
     *
     * @return
     */
    public String FrmAPI_GenerFieldEndInfo() throws Exception {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(this.GetRequestVal("name"));
        return bp.sys.CCFormAPI.GenerFildEndInfoByCHName(arrayList);
    }

    ///#region 多附件.

    /**
     * 获得数据
     *
     * @return
     */
    public final String Ath_Init() throws Exception {
        try {
            DataSet ds = new DataSet();

            FrmAttachment athDesc = this.GenerAthDesc();

            //查询出来数据实体.
            String pkVal = this.GetRequestVal("RefOID");
            if (DataType.IsNullOrEmpty(pkVal) == true) {
                pkVal = this.GetRequestVal("OID");
            }
            if (DataType.IsNullOrEmpty(pkVal) == true) {
                pkVal = String.valueOf(this.getWorkID());
            }
            DataTable athDt = null;
            //是否轨迹表单查看
            boolean ishistoryData = this.GetRequestValBoolen("IshistoryData");
            if (ishistoryData == false) {
                FrmAttachmentDBs dbs = bp.wf.CCFormAPI.GenerFrmAttachmentDBs(athDesc, pkVal, this.getFKFrmAttachment(), this.getWorkID(), this.getFID(), this.getPWorkID(), true, this.getNodeID(), this.getFrmID());
                athDt = dbs.ToDataTableField("DBAths");
            } else {
                //获取历史数据
                String trackID = this.GetRequestVal("TrackID");
                if (DataType.IsNullOrEmpty(trackID) == false) {
                    //根据TrackID从Sys_FrmDBVer表中获取历史数据
                    FrmDBVer ver = new FrmDBVer();
                    int i = ver.Retrieve(FrmDBVerAttr.TrackID, trackID);
                    if (i == 1) {
                        String athStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", ver.getMyPK(), "FrmAthDB");
                        DataSet athDS = bp.tools.Json.ToDataSet(athStr);
                        athDt = athDS.GetTableByName(athDesc.getNoOfObj());
                        athDt.TableName = "DBAths";
                    }
                } else {
                    FrmAttachmentDBs dbs = bp.wf.CCFormAPI.GenerFrmAttachmentDBs(athDesc, pkVal, this.getFKFrmAttachment(), this.getWorkID(), this.getFID(), this.getPWorkID(), true, this.getNodeID(), this.getFrmID());
                    athDt = dbs.ToDataTableField("DBAths");
                }
            }

            ///#region 如果图片显示.(先不考虑.)
            if (athDesc.getFileShowWay() == FileShowWay.Pict) {
                /* 如果是图片轮播，就在这里根据数据输出轮播的html代码.*/
                if (athDt.Rows.size() == 0 && athDesc.getItIsUpload() == true) {
                    /*没有数据并且，可以上传,就转到上传的界面上去.*/
                    return "url@AthImg.htm?1=1" + this.getRequestParas();
                }
            }
            ///#region 处理权限问题.
            // 处理权限问题, 有可能当前节点是可以上传或者删除，但是当前节点上不能让此人执行工作。
            // bool isDel = athDesc.IsDeleteInt == 0 ? false : true;
            boolean isDel = athDesc.getHisDeleteWay() == AthDeleteWay.None ? false : true;
            boolean isUpdate = athDesc.getItIsUpload();
            //if (isDel == true || isUpdate == true)
            //{
            //    if (this.WorkID != 0
            //        && DataType.IsNullOrEmpty(this.FlowNo) == false
            //        && this.getNodeID() != 0)
            //    {
            //        isDel = BP.WF.Dev2Interface.Flow_IsCanDoCurrentWork(this.FlowNo, this.getNodeID(), this.WorkID, WebUser.getNo());
            //        if (isDel == false)
            //            isUpdate = false;
            //    }
            //}
            athDesc.setItIsUpload(isUpdate);
            //athDesc.HisDeleteWay = AthDeleteWay.DelAll;
            ///#endregion 处理权限问题.

            String sort = athDesc.getSort().trim();
            if (sort.contains("SELECT") == true || sort.contains("select") == true) {
                String sql = Glo.DealExp(sort, null, null);
                DataTable dt = DBAccess.RunSQLReturnTable(sql);
                String strs = "";
                for (DataRow dr : dt.Rows) {
                    strs += dr.get(0) + ",";
                }
                athDesc.setSort(strs);
            }


            //增加附件描述.
            ds.Tables.add(athDesc.ToDataTableField("AthDesc"));

            //增加附件.
            ds.Tables.add(athDt);

            //返回.
            return Json.ToJson(ds);
        } catch (RuntimeException ex) {
            return "err@" + ex.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final String Ath_SaveIdx() throws Exception {
        String athMyPKs = this.GetRequestVal("AthMyPKs");

        if (StringUtils.isEmpty(athMyPKs)) {
            return "err@请传入需要排序的附件id";
        }
        String[] ids = athMyPKs.split(",");
        int idx = 0;
        for (String id : ids) {
            if (StringUtils.isEmpty(id)) {
                continue;
            }
            FrmAttachmentDB item = new FrmAttachmentDB(id);
            item.setIdx(idx);
            idx++;
            item.DirectUpdate();
        }
        return "执行成功";
    }

    /**
     * 扩展处理.
     *
     * @return
     */
    public final String HandlerMapExt() throws Exception {
        String fk_mapExt = this.GetRequestVal("FK_MapExt").toString();
        String oid = this.GetRequestVal("OID");

        String kvs = this.GetRequestVal("KVs");

        MapExt me = new MapExt(fk_mapExt);
        DataTable dt = null;
        String sql = "";
        String key = this.GetRequestVal("Key");
        key = key.trim();
        key = key.replace("'", ""); //去掉单引号.

        String dbsrc = me.getDBSrcNo();
        SFDBSrc sfdb = null;
        if (DataType.IsNullOrEmpty(dbsrc) == false && dbsrc.equals("local") == false) {
            sfdb = new SFDBSrc(dbsrc);
        }
        switch (me.getExtType()) {
            case MapExtXmlList.FullData: // 填充其他控件.
                switch (this.GetRequestVal("DoTypeExt")) {
                    case "ReqCtrl":
                        String requestbody = me.getTag4(); //body
                        requestbody = requestbody.replace("'", "\"");
                        requestbody = DealSQL(requestbody, key);
                        requestbody = requestbody.replace("@fulltype", "0");
                        String urlCtrl = DealSQL(me.getDoc(), key);
                        String strCtrl = bp.tools.HttpClientUtil.doPost(urlCtrl, requestbody, null);
                        JSONObject jsonCtrl = JSONObject.fromObject(strCtrl);
                        //code=0，表示请求成功，否则失败
                        if (jsonCtrl.get("code") == null) {
                            return "err@执行URL返回结果失败";
                        }
                        if (jsonCtrl.get("code").toString().equals("0") == false) {
                            return "err@执行URL返回结果失败";
                        }
                        String ctrlData = jsonCtrl.get("data").toString();
                        return ctrlData;
                    case "ReqDtlFullList":
                        /* 获取填充的从表集合. */
                        DataTable dtDtl = new DataTable("Head");
                        dtDtl.Columns.Add("Dtl", String.class);
                        String dbType = me.getDBType(); //0 执行SQL 1 执行URL
                        String requestMesthod = me.getTag3(); // Get ,Post
                        String questbody = me.getTag4(); //body
                        String type = me.GetValStringByKey("Tag5");
                        if (type.equals("1") && dbType.equals("1")) {
                            String url = DealSQL(me.getTag1(), key);
                            String result = "";
                            if (requestMesthod.toLowerCase().equals("get")) {
                                result = DataType.ReadURLContext(url, 9000);
                            } else {
                                questbody = questbody.replace("'", "\"");
                                questbody = DealSQL(questbody, key);
                                questbody = questbody.replace("@fulltype", "1");
                                result = bp.tools.HttpClientUtil.doPost(url, questbody, null);
                            }
                            if (DataType.IsNullOrEmpty(result) == true) {
                                return "err@请求失败";
                            }
                            //数据序列化
                            JSONObject jsonData = JSONObject.fromObject(result);
                            //code=0，表示请求成功，否则失败
                            if (jsonData.get("code").toString().equals("0") == false) {
                                return "err@执行URL返回结果失败";
                            }
                            String data = jsonData.get("data").toString();
                            ObjectMapper objectMapper = new ObjectMapper();

                            // 解析JSON数据
                            JsonNode jToken = objectMapper.readTree(data);


                            //JToken jToken = JToken.Parse(postData);
                            if (jToken.getNodeType() == JsonNodeType.OBJECT) {
                                JSONObject jsonItem = JSONObject.fromObject(data);
                                for (Object item : jsonItem.keySet()) {
                                    String value = jsonItem.getString(item.toString());
                                    jToken = objectMapper.readTree(value);
                                    //判断值是不是集合
                                    if (jToken.getNodeType() != JsonNodeType.ARRAY)
                                        continue;
                                    JSONArray arr = JSONArray.fromObject(value);
                                    if (arr.size() == 0)
                                        continue;
                                    dt = Json.ConvertToDataTable(arr);
                                    MapDtl dtl = new MapDtl();
                                    dtl.setNo(item.toString());
                                    int i = dtl.RetrieveFromDBSources();
                                    if (i == 0) {
                                        continue;
                                    }

                                    try {
                                        DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPK=" + oid);
                                    } catch (RuntimeException ex) {
                                        GEDtl mydtl = new GEDtl(dtl.getNo());
                                        mydtl.CheckPhysicsTable();
                                    }

                                    for (DataRow dr : dt.Rows) {
                                        GEDtl mydtl = new GEDtl(dtl.getNo());
                                        for (DataColumn dc : dt.Columns) {
                                            mydtl.SetValByKey(dc.ColumnName, dr.get(dc.ColumnName).toString());
                                        }
                                        mydtl.setRefPKInt(Integer.parseInt(oid));
                                        if (mydtl.getOID() > 100) {
                                            mydtl.InsertAsOID(mydtl.getOID());
                                        } else {
                                            mydtl.setOID(0);
                                            mydtl.Insert();
                                        }

                                    }
                                    DataRow drRe = dtDtl.NewRow();
                                    drRe.setValue(0, dtl.getNo());
                                    dtDtl.Rows.add(drRe);
                                }
                            }
                            return Json.ToJson(dtDtl);
                        }
                        String[] strsDtl = me.getTag1().split("[$]", -1);
                        for (String str : strsDtl) {
                            if (DataType.IsNullOrEmpty(str)) {
                                continue;
                            }

                            String[] ss = str.split("[:]", -1);
                            String fk_dtl = ss[0];
                            MapDtl dtl = new MapDtl(fk_dtl);
                            String mysql = str.replace(dtl.getNo() + ":", "");
                            if (Objects.equals(mysql, "") || mysql == null) {
                                continue;
                            }
                            String dtlKey = this.GetRequestVal("DtlKey");
                            if (dtlKey == null) {
                                dtlKey = key;
                            }
                            if (dtlKey.indexOf(",") != -1) {
                                dtlKey = "'" + dtlKey.replace(",", "','") + "'";
                            }
                            mysql = DealSQL(mysql, dtlKey);
                            if (mysql.length() <= 10) {
                                continue;
                            }
                            if (mysql.contains("@")) {
                                return "请求的语句" + mysql + "还有未替换的@符号";
                            }

                            GEDtls dtls = new GEDtls(fk_dtl);

                            DataTable dtDtlFull = null;
                            if (me.getDBType().equals("0")) {
                                try {
                                    if (sfdb != null) {
                                        dtDtlFull = sfdb.RunSQLReturnTable(mysql);
                                    } else {
                                        dtDtlFull = DBAccess.RunSQLReturnTable(mysql);
                                    }
                                } catch (RuntimeException ex) {
                                    throw new RuntimeException("err@执行填充从表出现错误,[" + dtl.getNo() + " - " + dtl.getName() + "]设置的SQL" + mysql);
                                }
                            }
                            if (me.getDBType().equals("1")) {
                                String json = DataType.ReadURLContext(mysql, 8000);
                                if (DataType.IsNullOrEmpty(json) == true) {
                                    return "err@执行URL没有返回结果值";
                                }

                                dtDtlFull = Json.ToDataTable(json);
                            }

                            try {
                                DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPK=" + oid);
                            } catch (RuntimeException ex) {
                                GEDtl mydtl = new GEDtl(fk_dtl);
                                mydtl.CheckPhysicsTable();
                            }

                            for (DataRow dr : dtDtlFull.Rows) {
                                GEDtl mydtl = new GEDtl(fk_dtl);
                                //  mydtl.OID = dtls.Count + 1;
                                dtls.AddEntity(mydtl);
                                for (DataColumn dc : dtDtlFull.Columns) {
                                    mydtl.SetValByKey(dc.ColumnName, dr.get(dc.ColumnName).toString());
                                }
                                mydtl.setRefPKInt(Integer.parseInt(oid));
                                if (mydtl.getOID() > 100) {
                                    mydtl.InsertAsOID(mydtl.getOID());
                                } else {
                                    mydtl.setOID(0);
                                    mydtl.Insert();
                                }

                            }
                            DataRow drRe = dtDtl.NewRow();
                            drRe.setValue(0, fk_dtl);
                            dtDtl.Rows.add(drRe);
                        }
                        return Json.ToJson(dtDtl);
                }
                return "";
            default:
                return "err@没有解析的标记" + me.getExtType();
        }

    }

    /**
     * 处理sql.
     *
     * @param sql 要执行的sql
     * @param key 关键字值
     * @return 执行sql返回的json
     */
    private String DealSQL(String sql, String key) {
        sql = sql.replace("@Key", key);
        sql = sql.replace("@key", key);
        sql = sql.replace("@Val", key);
        sql = sql.replace("@val", key);
        sql = sql.replace("@val", key);

        sql = sql.replace("\n", "");
        sql = sql.replace("\n", "");
        sql = sql.replace("\n", "");
        sql = sql.replace("\n", "");
        sql = sql.replace("\n", "");


        sql = sql.replace("@WebUser.No", WebUser.getNo());
        sql = sql.replace("@WebUser.getName()", WebUser.getName());
        sql = sql.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
        sql = sql.replace("@WebUser.DeptNo", WebUser.getDeptNo());

        String oid = this.GetRequestVal("OID");
        if (oid != null) {
            sql = sql.replace("@OID", oid);
        }

        String kvs = this.GetRequestVal("KVs");

        if (DataType.IsNullOrEmpty(kvs) == false && sql.contains("@") == true) {
            String[] strs = kvs.split("[~]", -1);
            for (String s : strs) {
                if (DataType.IsNullOrEmpty(s) || s.contains("=") == false) {
                    continue;
                }

                String[] mykv = s.split("[=]", -1);
                sql = sql.replace("@" + mykv[0], mykv[1]);

                if (sql.contains("@") == false) {
                    break;
                }
            }
        }

        if (sql.contains("@") == true) {
            for (String mykey : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
                sql = sql.replace("@" + mykey, this.GetRequestVal(mykey));
            }
        }

        dealSQL = sql;
        return sql;
    }

    private String dealSQL = "";


    ///#endregion HanderMapExt


    ///#region 执行父类的重写方法.

    /**
     * 构造函数
     */
    public WF_CCForm() {
    }

    /**
     * 默认执行的方法
     *
     * @return
     */
    @Override
    protected String DoDefaultMethod() {
        //找不不到标记就抛出异常.
        throw new RuntimeException("@标记[" + this.getDoType() + "]，没有找到.@原始URL:" + ContextHolderUtils.getRequest().getRequestURI());
    }

    ///#region frm.htm 主表.

    /**
     * 执行数据初始化
     *
     * @return
     */
    public final String Frm_Init() throws Exception {
        boolean IsMobile = GetRequestValBoolen("IsMobile");
        if (this.GetRequestVal("IsTest") != null) {
            MapData mymd = new MapData(this.getEnsName());
            mymd.RepairMap();
            SystemConfig.DoClearCache();
        }

        MapData md = new MapData(this.getEnsName());
        Node nd = null;
        if (this.getNodeID() != 0)
            nd = new Node(this.getNodeID());
        ///#region 判断是否是返回的URL.
        if ((nd != null && nd.getHisFormType() == NodeFormType.SelfForm) || md.getHisFrmType() == FrmType.Url) {
            String no = this.GetRequestVal("NO");
            String urlParas = "OID=" + this.getRefOID() + "&NO=" + no + "&WorkID=" + this.getWorkID() + "&FK_Node=" + this.getNodeID() + "&UserNo=" + WebUser.getNo() + "&Token=" + this.getSID() + "&FID=" + this.getFID() + "&PWorkID=" + this.getWorkID();

            String url = md.getUrlExt();
            if (nd != null && DataType.IsNullOrEmpty(url))
                url = nd.getFormUrl();
            /*如果是URL.*/
            if (md.getUrlExt().contains("?") == true) {
                url = md.getUrlExt() + "&" + urlParas;
            } else {
                url = md.getUrlExt() + "?" + urlParas;
            }
            String pageType = this.GetRequestVal("PageType");
            if (DataType.IsNullOrEmpty(pageType) == false && pageType.equals("Vue3"))
                return "url@FrmSelf.htm?" + urlParas;
            return "url@" + url;
        }

        if (md.getHisFrmType() == FrmType.Entity) {
            String no = this.GetRequestVal("NO");
            String urlParas = "OID=" + this.getRefOID() + "&NO=" + no + "&WorkID=" + this.getWorkID() + "&FK_Node=" + this.getNodeID() + "&UserNo=" + WebUser.getNo() + "&Token=" + this.getSID();

            Entities ens = ClassFactory.GetEns(md.getPTable());

            Entity en = ens.getNewEntity();

            if (en.getItIsOIDEntity() == true) {
                EntityOID enOID = en instanceof EntityOID ? (EntityOID) en : null;

                if (enOID == null) {
                    return "err@系统错误，无法将" + md.getPTable() + "转化成 BP.En.EntityOID.";
                }

                enOID.SetValByKey("OID", this.getWorkID());

                if (en.RetrieveFromDBSources() == 0) {
                    for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
                        enOID.SetValByKey(key, this.GetRequestVal(key));
                    }
                    enOID.SetValByKey("OID", this.getWorkID());

                    enOID.InsertAsOID(this.getWorkID());
                }
            }
            return "url@../Comm/En.htm?EnName=" + md.getPTable() + "&PKVal=" + this.getWorkID();
        }

        if (md.getHisFrmType() == FrmType.VSTOForExcel && this.GetRequestVal("IsFreeFrm") == null) {
            String url = "FrmVSTO.htm?1=1" + this.getRequestParasOfAll();
            url = url.replace("&&", "&");
            return "url@" + url;
        }


        if (md.getHisFrmType() == FrmType.WordFrm || md.getHisFrmType() == FrmType.WPSFrm) {
            String no = this.GetRequestVal("NO");
            String urlParas = "OID=" + this.getRefOID() + "&NO=" + no + "&WorkID=" + this.getWorkID() + "&FK_Node=" + this.getNodeID() + "&UserNo=" + WebUser.getNo() + "&Token=" + this.getSID() + "&FK_MapData=" + this.getFrmID() + "&OIDPKVal=" + this.getOID() + "&FID=" + this.getFID() + "&FK_Flow=" + this.getFlowNo();
            /*如果是URL.*/
            String requestParas = this.getRequestParasOfAll();
            String[] parasArrary = this.getRequestParasOfAll().split("[&]", -1);
            for (String str : parasArrary) {
                if (DataType.IsNullOrEmpty(str) || str.contains("=") == false) {
                    continue;
                }
                String[] kvs = str.split("[=]", -1);
                if (urlParas.contains(kvs[0])) {
                    continue;
                }
                urlParas += "&" + kvs[0] + "=" + kvs[1];
            }


            String frm = "FrmWord";
            if (md.getHisFrmType() == FrmType.WPSFrm) {
                frm = "WpsFrm";
            }

            if (md.getUrlExt().contains("?") == true) {
                return "url@" + frm + ".htm?1=2" + "&" + urlParas;
            } else {
                return "url@" + frm + ".htm" + "?" + urlParas;
            }
        }

        if (md.getHisFrmType() == FrmType.ExcelFrm) {
            return "url@FrmExcel.htm?1=2" + this.getRequestParasOfAll();
        }


        ///#endregion 判断是否是返回的URL.

        //处理参数.
        String paras = this.getRequestParasOfAll();
        paras = paras.replace("&DoType=Frm_Init", "");
        FrmNode fn = null;


        ///#region 流程的独立运行的表单.
        if (this.getNodeID() != 0 && this.getNodeID() != 999999) {
            fn = new FrmNode(this.getNodeID(), this.getFrmID());
            if (fn.getFrmSln() == FrmSln.Readonly) {
                paras = paras + "&IsReadonly=1";
            }

            if (fn != null && fn.getWhoIsPK() != WhoIsPK.OID) {
                //太爷孙关系
                if (fn.getWhoIsPK() == WhoIsPK.P3WorkID) {
                    //根据PWorkID 获取P3WorkID
                    String sql = "Select PWorkID From WF_GenerWorkFlow Where WorkID=(Select PWorkID From WF_GenerWorkFlow Where WorkID=" + this.getPWorkID() + ")";
                    String p3workID = DBAccess.RunSQLReturnString(sql);
                    if (DataType.IsNullOrEmpty(p3workID) == true || Objects.equals(p3workID, "0")) {
                        throw new RuntimeException("err@不存在太爷孙流程关系，请联系管理员检查流程设计是否正确");
                    }

                    long workID = Long.parseLong(p3workID);
                    paras = paras.replace("&OID=" + this.getWorkID(), "&OID=" + workID);
                    paras = paras.replace("&WorkID=" + this.getWorkID(), "&WorkID=" + workID);
                    paras = paras.replace("&PKVal=" + this.getWorkID(), "&PKVal=" + workID);
                }

                if (fn.getWhoIsPK() == WhoIsPK.P2WorkID) {
                    //根据PWorkID 获取PPWorkID
                    GenerWorkFlow gwf = new GenerWorkFlow(this.getPWorkID());
                    if (gwf != null && gwf.getPWorkID() != 0) {
                        paras = paras.replace("&OID=" + this.getWorkID(), "&OID=" + gwf.getPWorkID());
                        paras = paras.replace("&WorkID=" + this.getWorkID(), "&WorkID=" + gwf.getPWorkID());
                        paras = paras.replace("&PKVal=" + this.getWorkID(), "&PKVal=" + gwf.getPWorkID());
                    } else {
                        throw new RuntimeException("err@不存在爷孙流程关系，请联系管理员检查流程设计是否正确");
                    }
                }

                if (fn.getWhoIsPK() == WhoIsPK.PWorkID) {
                    paras = paras.replace("&OID=" + this.getWorkID(), "&OID=" + this.getPWorkID());
                    paras = paras.replace("&WorkID=" + this.getWorkID(), "&WorkID=" + this.getPWorkID());
                    paras = paras.replace("&PKVal=" + this.getWorkID(), "&PKVal=" + this.getPWorkID());
                }

                if (fn.getWhoIsPK() == WhoIsPK.FID) {
                    paras = paras.replace("&OID=" + this.getWorkID(), "&OID=" + this.getFID());
                    paras = paras.replace("&WorkID=" + this.getWorkID(), "&WorkID=" + this.getFID());
                    paras = paras.replace("&PKVal=" + this.getWorkID(), "&PKVal=" + this.getFID());
                }

                if ((this.GetRequestVal("ShowFrmType") != null && this.GetRequestVal("ShowFrmType").equals("FrmFool") == true) || md.getHisFrmType() == FrmType.Develop || md.getHisFrmType() == FrmType.FoolForm) {
                    if (IsMobile == true) {
                        return "url@../FrmView.htm?1=2" + paras;
                    }

                    if (this.GetRequestValBoolen("Readonly") == true || this.GetRequestValBoolen("IsEdit") == false) {
                        return "url@FrmGener.htm?1=2" + paras;
                    } else {
                        return "url@FrmGener.htm?1=2" + paras;
                    }
                }

                if (md.getHisFrmType() == FrmType.VSTOForExcel || md.getHisFrmType() == FrmType.ExcelFrm) {
                    if (this.GetRequestValBoolen("Readonly") == true || this.GetRequestValBoolen("IsEdit") == false) {
                        return "url@FrmVSTO.htm?1=2" + paras;
                    } else {
                        return "url@FrmVSTO.htm?1=2" + paras;
                    }
                }

                if (IsMobile == true) {
                    return "url@../FrmView.htm?1=2" + paras;
                }

                if (md.getHisFrmType() == FrmType.ChapterFrm) {
                    if (this.GetRequestValBoolen("Readonly") == true) {
                        return "url@ChapterFrmView.htm?1=2" + paras;
                    } else {
                        return "url@ChapterFrm.htm?1=2" + paras;
                    }
                }

                if (this.GetRequestValBoolen("Readonly") == true) {
                    return "url@FrmGener.htm?1=2" + paras;
                } else {
                    return "url@FrmGener.htm?1=2" + paras;
                }
            }
        }

        ///#endregion 非流程的独立运行的表单.


        ///#region 非流程的独立运行的表单.
        if (md.getHisFrmType() == FrmType.ChapterFrm) {
            if (paras.contains("FrmID=") == false) {
                paras = paras.replace("FK_MapData=", "FrmID=");
            }

            //  || this.GetRequestValBoolen("IsEdit") == false @yln 这个值判断吗？
            if (IsMobile == true) {
                return "url@../FrmView.htm?1=2" + paras;
            }
            if ((fn != null && fn.getFrmSln() == FrmSln.Readonly) || this.GetRequestValBoolen("Readonly") == true) {
                return "url@ChapterFrmView.htm?1=2" + paras;
            } else {
                return "url@ChapterFrm.htm?1=2" + paras;
            }
        }

        if (md.getHisFrmType() == FrmType.FoolForm) {
            if (IsMobile == true) {
                return "url@../FrmView.htm?1=2" + paras;
            }
            if ((fn != null && fn.getFrmSln() == FrmSln.Readonly) || this.GetRequestValBoolen("Readonly") == true) {
                return "url@FrmGener.htm?1=2" + paras;
            } else {
                return "url@FrmGener.htm?1=2" + paras;
            }
        }


        if (md.getHisFrmType() == FrmType.WordFrm) {
            if ((fn != null && fn.getFrmSln() == FrmSln.Readonly) || this.GetRequestValBoolen("Readonly") == true) {
                return "url@FrmWord.htm?1=2" + paras;
            } else {
                return "url@FrmWord.htm?1=2" + paras;
            }
        }

        if (md.getHisFrmType() == FrmType.VSTOForExcel || md.getHisFrmType() == FrmType.ExcelFrm) {
            if ((fn != null && fn.getFrmSln() == FrmSln.Readonly) || this.GetRequestValBoolen("Readonly") == true) {
                return "url@FrmVSTO.htm?1=2" + paras;
            } else {
                return "url@FrmVSTO.htm?1=2" + paras;
            }
        }

        if (IsMobile == true) {
            return "url@../FrmView.htm?1=2" + paras;
        }

        if (this.GetRequestValBoolen("Readonly") == true) {
            return "url@FrmGener.htm?1=2" + paras;
        } else {
            return "url@FrmGener.htm?1=2" + paras;
        }


        ///#endregion 非流程的独立运行的表单.

    }

    /**
     * 附件图片
     *
     * @return
     */
    public final String FrmImgAthDB_Init() throws Exception {
        String ImgAthPK = this.GetRequestVal("ImgAth");

        FrmImgAthDBs imgAthDBs = new FrmImgAthDBs();
        QueryObject obj = new QueryObject(imgAthDBs);
        obj.AddWhere(FrmImgAthDBAttr.FK_MapData, this.getFrmID());
        obj.addAnd();
        obj.AddWhere(FrmImgAthDBAttr.FK_FrmImgAth, ImgAthPK);
        obj.addAnd();
        obj.AddWhere(FrmImgAthDBAttr.RefPKVal, this.getRefPKVal());
        obj.DoQuery();

        //return BP.Tools.Entitis2Json.ConvertEntities2ListJson(imgAthDBs);
        DataTable dt = imgAthDBs.ToDataTableField("dt");
        dt.TableName = "FrmImgAthDB";
        return Json.ToJson(dt);
    }

    /**
     * 上传编辑图片
     *
     * @return
     */
    public final String FrmImgAthDB_Upload() throws Exception {
        String CtrlID = this.GetRequestVal("CtrlID");
        int zoomW = this.GetRequestValInt("zoomW");
        int zoomH = this.GetRequestValInt("zoomH");
        String myName = "";
        String fk_mapData = this.getFK_MapData();
        if (fk_mapData.contains("ND") == true)
            myName = CtrlID + "_" + this.getRefPKVal();
        else
            myName = fk_mapData + "_" + CtrlID + "_" + this.getRefPKVal();

        // 生成新路径，解决返回相同src后图片不切换问题
        String webPath = "/DataUser/ImgAth/Data/" + myName + ".png";
        String saveToPath = SystemConfig.getPathOfDataUser() + "ImgAth/Data/";
        saveToPath = saveToPath.replace("\\", "/");
        String fileUPloadPath = SystemConfig.getPathOfDataUser() + "ImgAth/Upload/";
        fileUPloadPath = fileUPloadPath.replace("\\", "/");

        // 如果路径不存在就创建,否则拷贝文件报异常
        File sPath = new File(saveToPath);
        File uPath = new File(fileUPloadPath);
        if (!sPath.isDirectory()) {
            sPath.mkdirs();
        }
        if (!uPath.isDirectory()) {
            uPath.mkdirs();
        }

        saveToPath = saveToPath + myName + ".png";
        fileUPloadPath = fileUPloadPath + myName + ".png";
        File sFile = new File(saveToPath);
        File uFile = new File(fileUPloadPath);

        HttpServletRequest request = getRequest();
        String contentType = request.getContentType();
        if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {

            String fileName = CommonFileUtils.getOriginalFilename(request, "file");
            String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
            try {
                CommonFileUtils.upload(request, "file", sFile);
            } catch (Exception e) {
                e.printStackTrace();
                return "err@上传图片保存失败";
            }
        }
        // 复制一份到uFile
        try {
            copyFileUsingFileChannels(sFile, uFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 获取文件大小;
        long fileSize = uFile.length();
        if (fileSize > 0) {
            return "{\"SourceImage\":\"" + webPath + "\"}";
        }
        return "@err没有选择文件";
    }

    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    public final String ImgUpload_Del() throws Exception {
        //执行删除.
        String delPK = this.GetRequestVal("DelPKVal");

        FrmImgAthDB delDB = new FrmImgAthDB();
        delDB.setMyPK(delPK == null ? this.getMyPK() : delPK);
        delDB.RetrieveFromDBSources();
        delDB.Delete(); //删除上传的文件.

        String saveToPath = SystemConfig.getPathOfWebApp() + (Glo.getCCFlowAppPath() + "DataUser/ImgAth/Data");

        File fileInfo = new File(saveToPath + "/" + delDB.getFileName());
        fileInfo.delete();
        return "删除成功.";
    }

    /**
     * 剪切图片
     *
     * @return
     */
    public final String FrmImgAthDB_Cut() throws Exception {
        String CtrlID = this.GetRequestVal("CtrlID");

        int zoomW = this.GetRequestValInt("zoomW");
        int zoomH = this.GetRequestValInt("zoomH");
        int x = this.GetRequestValInt("cX");
        int y = this.GetRequestValInt("cY");
        int w = this.GetRequestValInt("cW");
        int h = this.GetRequestValInt("cH");

        String newName = "";
        String fk_mapData = this.getFK_MapData();
        String fileFullName = "";
        if (fk_mapData.contains("ND") == true) {
            newName = CtrlID + "_" + this.getRefPKVal();
        } else {
            newName = fk_mapData + "_" + CtrlID + "_" + this.getRefPKVal();
        }
        // string newName = ImgAthPK + "_" + this.MyPK + "_" +
        // DateTime.Now.ToString("yyyyMMddHHmmss");
        String webPath = bp.wf.Glo.getCCFlowAppPath() + "DataUser/ImgAth/Data/" + newName + ".png";
        String savePath = SystemConfig.getCCFlowAppPath() + "DataUser/ImgAth/Data/" + newName + ".png";
        // 获取上传的大图片
        // string strImgPath =
        // this.context.Server.MapPath(SystemConfig.getCCFlowWebPath() +
        // "DataUser/ImgAth/Upload/" + newName + ".png");
        String strImgPath = SystemConfig.getPathOfWebApp() + SystemConfig.getCCFlowWebPath() + "DataUser/ImgAth/Upload/"
                + newName + ".png";
        if ((new File(strImgPath)).isFile() == true) {
            // 剪切图
            Crop(strImgPath, savePath, x, y, w, h);
            return webPath;
        }
        return webPath;
    }

    /**
     * 剪裁图像
     *
     * @param filePath
     * @param newFilePath
     * @param x
     * @param y
     * @return
     */
    public static void Crop(String filePath, String newFilePath, int x, int y, int w, int h) {
        // 加载图片到内存
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(filePath));
            // 构造一个reader
            Iterator it = ImageIO.getImageReadersByFormatName("png");
            ImageReader reader = (ImageReader) it.next();
            // 绑定inputStream
            reader.setInput(iis);
            // 取得剪裁区域
            ImageReadParam param = reader.getDefaultReadParam();
            Rectangle rect = new Rectangle(x, y, w, h);
            param.setSourceRegion(rect);
            // 从reader得到bufferImage
            BufferedImage bi = reader.read(0, param);
            // 将bufferedImage写成,通过ImageIO
            ImageIO.write(bi, "png", new File(newFilePath));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final String DtlFrm_Init() throws Exception {
        long pk = this.getRefOID();
        if (pk == 0) {
            pk = this.getOID();
        }
        if (pk == 0) {
            pk = this.getWorkID();
        }

        if (pk != 0) {
            return FrmGener_Init();
        }

        MapDtl dtl = new MapDtl(this.getEnsName());

        GEEntityOID en = new GEEntityOID(this.getEnsName());
        if (SystemConfig.isBSsystem() == true) {
            // 处理传递过来的参数。
            for (String k : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
                en.SetValByKey(k, this.GetRequestVal(k));
            }
        }


        //设置主键.
        //en.setOID(DBAccess.GenerOID(this.getEnsName()));
        en.SetValByKey("OID",  DBAccess.GenerOID(this.getEnsName()));
        ///#region 处理权限方案。
        if (this.getNodeID() != 0 && this.getNodeID() != 999999) {
            Node nd = new Node(this.getNodeID());
            if (nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.RefOneFrmTree) {
                FrmNode fn = new FrmNode(nd.getNodeID(), this.getFrmID());
                if (fn.getFrmSln() == FrmSln.Self) {
                    String no = this.getEnsName() + "_" + nd.getNodeID();
                    MapDtl mdtlSln = new MapDtl();
                    mdtlSln.setNo(no);
                    int result = mdtlSln.RetrieveFromDBSources();
                    if (result != 0) {
                        dtl = mdtlSln;
                    }
                }
            }
        }
        ///#endregion 处理权限方案。
        //给从表赋值.
        switch (dtl.getDtlOpenType()) {
            case ForEmp: // 按人员来控制.

                en.SetValByKey("RefPK", this.getRefPKVal());
                en.SetValByKey("FID", this.getRefPKVal());
                break;
            case ForWorkID: // 按工作ID来控制
                en.SetValByKey("RefPK", this.getRefPKVal());
                en.SetValByKey("FID", this.getRefPKVal());
                break;
            case ForFID: // 按流程ID来控制.
                en.SetValByKey("RefPK", this.getRefPKVal());
                en.SetValByKey("FID", this.getFID());
                break;
        }
        en.SetValByKey("RefPK", this.getRefPKVal());

        en.Insert();
        String paras = "";
        String systemPara = "DoType,DoType1,DoMethod,HttpHandlerName,OID,FID,WorkID,PWorkID,RefPKVal,FK_Flow,FK_Node,IsReadonly,EnsName,FK_MapData,";
        for (String str : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
            if (DataType.IsNullOrEmpty(str) == true || str.equals("T") == true || str.equals("t") == true) {
                continue;
            }
            if (str.equals("IsNew") == true || str.equals("FrmType") == true || systemPara.contains(str + ",") == true) {
                continue;
            }

            if (paras.contains(str + "=") == true) {
                continue;
            }
            paras += "&" + str + "=" + this.GetRequestVal(str);
        }
        MapData md = new MapData();
        md.setNo(this.getEnsName());
        if (md.RetrieveFromDBSources() == 0) {
            md.setName(dtl.getName());
            md.setFormEventEntity("");
            md.setEnPK("");
            md.Insert();
        }
        md.RepairMap(true);
        return "url@DtlFrm.htm?EnsName=" + this.getEnsName() + "&RefPKVal=" + this.getRefPKVal() + "&FrmType=" + dtl.getHisEditModel().getValue() + "&OID=" + en.getPKVal() + "&IsNew=1" + paras;
    }

    public final String DtlFrm_Delete() {
        try {
            GEEntityOID en = new GEEntityOID(this.getEnsName());
            en.SetValByKey("OID", this.getOID());
            en.Delete();

            //如果可以上传附件这删除相应的附件信息
            FrmAttachmentDBs dbs = new FrmAttachmentDBs();
            dbs.Delete(FrmAttachmentDBAttr.FK_MapData, this.getEnsName(), FrmAttachmentDBAttr.RefPKVal, this.getRefOID(), FrmAttachmentDBAttr.NodeID, this.getNodeID());


            return "删除成功.";
        } catch (RuntimeException ex) {
            return "err@删除错误:" + ex.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    ///#endregion DtlFrm


    ///#region frmFree

    /**
     * 实体类的初始化
     *
     * @return
     */
    public final String FrmGener_Init_ForBPClass() throws Exception {
        try {

            MapData md = new MapData(this.getEnsName());
            DataSet ds = bp.sys.CCFormAPI.GenerHisDataSet(md.getNo(), null, null);

            ///#region 把主表数据放入.
            String atParas = "";
            Entities ens = ClassFactory.GetEns(this.getEnsName());
            Entity en = ens.getNewEntity();
            en.setPKVal(this.getPKVal());

            if (en.RetrieveFromDBSources() == 0) {
                en.Insert();
            }

            //把参数放入到 En 的 Row 里面。
            if (DataType.IsNullOrEmpty(atParas) == false) {
                AtPara ap = new AtPara(atParas);
                for (String key : ap.getHisHT().keySet()) {
                    if (en.getRow().containsKey(key) == true) //有就该变.
                        en.getRow().SetValByKey(key, ap.GetValStrByKey(key));
                    else
                        en.getRow().put(key, ap.GetValStrByKey(key)); //增加他.
                }
            }

            if (SystemConfig.isBSsystem() == true) {
                // 处理传递过来的参数。
                for (String k : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
                    en.SetValByKey(k, this.GetRequestVal(k));
                }
            }

            // 执行表单事件. FrmLoadBefore .
            String msg = ExecEvent.DoFrm(md, EventListFrm.FrmLoadBefore, en);
            if (DataType.IsNullOrEmpty(msg) == false) {
                return "err@错误:" + msg;
            }


            //重设默认值.
            en.ResetDefaultVal(null, null, 0);

            //执行装载填充.
            MapExt me = new MapExt();
            if (me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.PageLoadFull, MapExtAttr.FK_MapData, this.getEnsName()) == 1) {
                //执行通用的装载方法.
                MapAttrs attrs = new MapAttrs(this.getEnsName());
                MapDtls dtls = new MapDtls(this.getEnsName());
                Entity tempVar = bp.wf.CCFormAPI.DealPageLoadFull(en, me, attrs, dtls);
                en = tempVar instanceof GEEntity ? (GEEntity) tempVar : null;
            }

            //执行事件
            ExecEvent.DoFrm(md, EventListFrm.SaveBefore, en, null);


            //增加主表数据.
            DataTable mainTable = en.ToDataTableField(md.getNo());
            mainTable.TableName = "MainTable";

            ds.Tables.add(mainTable);
            ///#region 把外键表加入DataSet
            DataTable dtMapAttr = ds.GetTableByName("Sys_MapAttr");

            MapExts mes = md.getMapExts();
            DataTable ddlTable = new DataTable();
            ddlTable.Columns.Add("No");
            for (DataRow dr : dtMapAttr.Rows) {
                String lgType = dr.get("LGType").toString();
                if (lgType.equals("2") == false) {
                    continue;
                }

                String UIIsEnable = dr.get("UIVisible").toString();
                if (Objects.equals(UIIsEnable, "0")) {
                    continue;
                }
                String uiBindKey = dr.get("UIBindKey").toString();
                if (DataType.IsNullOrEmpty(uiBindKey) == true) {
                    String myPK = dr.get("MyPK").toString();
                    /*如果是空的*/
                    //   throw new Exception("@属性字段数据不完整，流程:" + fl.No + fl.Name + ",节点:" + nd.NodeID + nd.Name + ",属性:" + myPK + ",的UIBindKey IsNull ");
                }

                // 检查是否有下拉框自动填充。
                String keyOfEn = dr.get("KeyOfEn").toString();
                String fk_mapData = dr.get("FK_MapData").toString();

                ///#region 处理下拉框数据范围. for 小杨.
                Object tempVar2 = mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL, MapExtAttr.AttrOfOper, keyOfEn);
                me = tempVar2 instanceof MapExt ? (MapExt) tempVar2 : null;
                if (me != null) {
                    Object tempVar3 = me.getDoc();
                    String fullSQL = tempVar3 instanceof String ? (String) tempVar3 : null;
                    fullSQL = fullSQL.replace("~", ",");
                    fullSQL = Glo.DealExp(fullSQL, en, null);
                    DataTable dt = DBAccess.RunSQLReturnTable(fullSQL);
                    dt.TableName = keyOfEn; //可能存在隐患，如果多个字段，绑定同一个表，就存在这样的问题.
                    ds.Tables.add(dt);
                    continue;
                }
                ///#endregion 处理下拉框数据范围.

                // 判断是否存在.
                if (ds.Tables.contains(uiBindKey) == true) {
                    continue;
                }

                DataTable dataTable = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null);
                if (dataTable != null) {
                    ds.Tables.add(dataTable);
                } else {
                    DataRow ddldr = ddlTable.NewRow();
                    ddldr.setValue("No", uiBindKey);
                    ddlTable.Rows.add(ddldr);
                }
            }
            ddlTable.TableName = "UIBindKey";
            ds.Tables.add(ddlTable);
            ///#endregion End把外键表加入DataSet

            return Json.ToJson(ds);
        } catch (RuntimeException ex) {
            GEEntity myen = new GEEntity(this.getEnsName());
            myen.CheckPhysicsTable();

            bp.sys.CCFormAPI.RepareCCForm(this.getEnsName());
            return "err@装载表单期间出现如下错误 FrmGener_Init_ForBPClass ,ccform有自动诊断修复功能请在刷新一次，如果仍然存在请联系管理员. @" + ex.getMessage();
        }
    }

    public final String FrmGener_Init_ForDBList() throws Exception {
        try {
            DBList dblist = new DBList(this.getEnsName());
            MapData md = new MapData(this.getEnsName());
            DataSet ds = bp.sys.CCFormAPI.GenerHisDataSet(dblist.getNo(), null, null);

            ///#region 把主表数据放入.
            String atParas = "";
            GEEntity en = new GEEntity(this.getEnsName());

            String pk = this.GetRequestVal("OID");
            if (DataType.IsNullOrEmpty(pk) == true) {
                pk = this.GetRequestVal("WorkID");
            }
            String expEn = dblist.getExpEn();
            expEn = expEn.replace("@Key", pk);
            if (expEn.contains("@") == true) {
                expEn = expEn.replace("@WebUser.No", WebUser.getNo());
                expEn = expEn.replace("@WebUser.getName()", WebUser.getName());
                expEn = expEn.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
                expEn = expEn.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
                expEn = expEn.replace("@WebUser.DeptNo", WebUser.getDeptNo());
            }
            if (dblist.getDBType() == 0) {
                expEn = expEn.replace("~~", "\"");
                expEn = expEn.replace("~", "'");
                if (DataType.IsNullOrEmpty(dblist.getDBSrc()) == true) {
                    dblist.setDBSrc("local");
                }

                SFDBSrc sf = new SFDBSrc(dblist.getDBSrc());
                DataTable dt = sf.RunSQLReturnTable(expEn);
                if (dt.Rows.size() == 1) {
                    //把DataTable中的内容转换成Entity的值
                    for (MapAttr attr : md.getMapAttrs().ToJavaList()) {
                        if (dt.Columns.contains(attr.getKeyOfEn()) == true) {
                            en.getRow().SetValByKey(attr.getKeyOfEn(), dt.Rows.get(0).getValue(attr.getKeyOfEn()));
                        }
                    }
                }
            }

            if (dblist.getDBType() == 1) {
                if (expEn.contains("http") == false) {
                    /*如果没有绝对路径 */
                    if (SystemConfig.isBSsystem()) {
                        /*在cs模式下自动获取*/
                        String host = SystemConfig.getHostURL(); //BP.Sys.Base.Glo.Request.Url.Host;
                        if (expEn.contains("@AppPath")) {
                            expEn = expEn.replace("@AppPath", "http://" + host + getRequest().getRequestURI()); //bp.sys.base.Glo.Request.ApplicationPath
                        } else {
                            expEn = "http://" + getRequest().getRequestURI() + expEn;
                        }
                    }

                    if (SystemConfig.isBSsystem() == false) {
                        /*在cs模式下它的baseurl 从web.config中获取.*/
                        String cfgBaseUrl = SystemConfig.GetValByKey("HostURL", "");
                        if (DataType.IsNullOrEmpty(cfgBaseUrl)) {
                            String err = "调用url失败:没有在web.config中配置BaseUrl,导致url事件不能被执行.";
                            Log.DebugWriteError(err);
                            throw new RuntimeException(err);
                        }
                        expEn = cfgBaseUrl + expEn;
                    }
                }
                String json = DataType.ReadURLContext(expEn, 8000);
                if (DataType.IsNullOrEmpty(json) == false) {
                    DataTable dt = Json.ToDataTable(json);
                    if (dt.Rows.size() == 1) {
                        //把DataTable中的内容转换成Entity的值
                        for (MapAttr attr : md.getMapAttrs().ToJavaList()) {
                            if (dt.Columns.contains(attr.getKeyOfEn()) == true) {
                                en.getRow().SetValByKey(attr.getKeyOfEn(), dt.Rows.get(0).getValue(attr.getKeyOfEn()));
                            }
                        }
                    }
                }

            }


            //把参数放入到 En 的 Row 里面。
            if (DataType.IsNullOrEmpty(atParas) == false) {
                AtPara ap = new AtPara(atParas);
                for (String key : ap.getHisHT().keySet()) {
                    if (en.getRow().containsKey(key) == true) //有就该变.
                    {
                        en.getRow().SetValByKey(key, ap.GetValStrByKey(key));
                    } else {
                        en.getRow().put(key, ap.GetValStrByKey(key)); //增加他.
                    }
                }
            }

            if (SystemConfig.isBSsystem() == true) {
                // 处理传递过来的参数。
                for (String k : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
                    en.SetValByKey(k, this.GetRequestVal(k));
                }
            }

            // 执行表单事件. FrmLoadBefore .
            en.SetPara("FrmType", "DBList");
            String msg = ExecEvent.DoFrm(md, EventListFrm.FrmLoadBefore, en);
            if (DataType.IsNullOrEmpty(msg) == false) {
                return "err@错误:" + msg;
            }

            //增加主表数据.
            DataTable mainTable = en.ToDataTableField(md.getNo());
            mainTable.TableName = "MainTable";

            ds.Tables.add(mainTable);
            ///#region 把外键表加入DataSet
            DataTable dtMapAttr = ds.GetTableByName("Sys_MapAttr");

            MapExts mes = md.getMapExts();
            DataTable ddlTable = new DataTable();
            ddlTable.Columns.Add("No");
            for (DataRow dr : dtMapAttr.Rows) {
                String lgType = dr.get("LGType").toString();
                if (lgType.equals("2") == false) {
                    continue;
                }

                String UIIsEnable = dr.get("UIVisible").toString();
                if (Objects.equals(UIIsEnable, "0")) {
                    continue;
                }


                String uiBindKey = dr.get("UIBindKey").toString();
                if (DataType.IsNullOrEmpty(uiBindKey) == true) {
                    String myPK = dr.get("MyPK").toString();

                }

                // 检查是否有下拉框自动填充。
                String keyOfEn = dr.get("KeyOfEn").toString();
                String fk_mapData = dr.get("FK_MapData").toString();


                ///#region 处理下拉框数据范围. for 小杨.
                Object tempVar = mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL, MapExtAttr.AttrOfOper, keyOfEn);
                MapExt me = tempVar instanceof MapExt ? (MapExt) tempVar : null;
                if (me != null) {
                    Object tempVar2 = me.getDoc();
                    String fullSQL = tempVar2 instanceof String ? (String) tempVar2 : null;
                    fullSQL = fullSQL.replace("~", ",");
                    fullSQL = Glo.DealExp(fullSQL, en, null);
                    DataTable dt = DBAccess.RunSQLReturnTable(fullSQL);
                    dt.TableName = keyOfEn; //可能存在隐患，如果多个字段，绑定同一个表，就存在这样的问题.
                    ds.Tables.add(dt);
                    continue;
                }

                ///#endregion 处理下拉框数据范围.

                // 判断是否存在.
                if (ds.Tables.contains(uiBindKey) == true) {
                    continue;
                }

                DataTable dataTable = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null);
                if (dataTable != null) {
                    ds.Tables.add(dataTable);
                } else {
                    DataRow ddldr = ddlTable.NewRow();
                    ddldr.setValue("No", uiBindKey);
                    ddlTable.Rows.add(ddldr);
                }
            }
            ddlTable.TableName = "UIBindKey";
            ds.Tables.add(ddlTable);

            return Json.ToJson(ds);
        } catch (RuntimeException ex) {
            GEEntity myen = new GEEntity(this.getEnsName());
            myen.CheckPhysicsTable();

            bp.sys.CCFormAPI.RepareCCForm(this.getEnsName());
            return "err@装载表单期间出现如下错误 FrmGener_Init_ForDBList ,ccform有自动诊断修复功能请在刷新一次，如果仍然存在请联系管理员. @" + ex.getMessage();
        }
    }

    /**
     * 表单打开
     *
     * @return
     */
    public final String WpsFrm_Init() throws Exception {
        long workID = this.getWorkID();

        GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
        if (gwf.getPWorkID() != 0) {
            workID = gwf.getPWorkID();
        }
        /**获得表单wps.
         */
        MapFrmWps md = new MapFrmWps(this.getFrmID());

        //首先从数据表里获取二进制表单实例.
        String file = SystemConfig.getPathOfTemp() + "\\" + workID + "." + this.getFrmID() + ".docx";

        String templateFilePath = SystemConfig.getPathOfCyclostyleFile() + md.getNo() + "." + md.getMyFileExt();

        //生成文件.
        byte[] val = DBAccess.GetFileFromDB(file, md.getPTable(), "OID", String.valueOf(workID), "DBFile");
        if (val == null) {
            Files.copy(Paths.get(templateFilePath), Paths.get(file), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/DataUser/Temp/" + workID + "." + this.getFrmID() + ".docx";
    }

    /**
     * 保存文件
     *
     * @return
     */
    public final String WpsFrm_SaveFile() throws Exception {
        // String fileName = "c:\\xxxx\temp.px";
        long workID = this.getWorkID();

        GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
        if (gwf.getPWorkID() != 0) {
            workID = gwf.getPWorkID();
        }

        String fileName = SystemConfig.getPathOfTemp() + "\\" + workID + "." + this.getFrmID() + ".docx";


        MapData md = new MapData(this.getFrmID());
        CommonFileUtils.upload(ContextHolderUtils.getRequest(), "file", new File(fileName));
        //HttpPostedFile file = HttpContextHelper.RequestFiles().get(0); //context.Request.Files;
        //file.SaveAs(fileName);

        //保存文件.
        DBAccess.SaveFileToDB(fileName, md.getPTable(), "OID", String.valueOf(workID), "DBFile");


        return "上传成功.";
    }
    public String FrmGener_Init_ForEntityNoName() throws Exception {
        MapData md = new MapData(this.getEnsName());
        //是否启用装载填充？
        boolean isLoadData = true;
        try
        {
            //主表实体.
            GEEntityNoName en = new GEEntityNoName(this.getEnsName());
            String pk = this.getRefNo();
             //   #region 根据who is pk 获取数据.
            en.setNo(pk);
            if ( DataType.IsNullOrEmpty(pk) ==true)
            {
                en.ResetDefaultVal();
            }
            else
            {
                if (en.RetrieveFromDBSources() == 0)
                    en.Insert();
            }
            //    #endregion 根据who is pk 获取数据.

            DataSet ds = bp.sys.CCFormAPI.GenerHisDataSet(this.getEnsName(),"",null);

            //    #region 附加参数数据.
            if (SystemConfig.isBSsystem() == true)
            {
                // 处理传递过来的参数。
                for (String k : ContextHolderUtils.getRequest().getParameterMap().keySet())
                {
                    en.SetValByKey(k, ContextHolderUtils.getRequest().getParameter(k));
                }
            }

            // 执行表单事件. FrmLoadBefore .
            String msg = ExecEvent.DoFrm(md, EventListFrm.FrmLoadBefore, en);
            if (DataType.IsNullOrEmpty(msg) == false)
                return "err@错误:" + msg;

            //重设默认值.
            if (this.GetRequestValBoolen("IsReadonly") == false)
                en.ResetDefaultVal();

            // #endregion 附加参数数据.

            // #region 执行装载填充.与相关的事件.
            MapExts mes = md.getMapExts();

            MapExt me = (MapExt) mes.GetEntityByKey("ExtType", MapExtXmlList.PageLoadFull);
            if (me == null)
                me = (bp.sys.MapExt) mes.GetEntityByKey("ExtModel", MapExtXmlList.PageLoadFullMainTable);
            if (isLoadData == true && md.getItIsPageLoadFull() && me != null && GetRequestValInt("IsTest") != 1)
            {
                //执行通用的装载方法.
                MapAttrs attrs = new MapAttrs(this.getEnsName());
                MapDtls dtls = new MapDtls(this.getEnsName());
                //判断是否自定义权限.
                boolean IsSelf = false;
                //单据或者是单据实体表单

                en = (GEEntityNoName)bp.wf.CCFormAPI.DealPageLoadFull(en, me, attrs, dtls, IsSelf, 0, this.getWorkID());
            }
              //  #endregion 执行装载填充.与相关的事件.

             //   #region 把外键表加入 DataSet.
            DataTable dtMapAttr = ds.get("Sys_MapAttr");

            DataTable ddlTable = new DataTable();
            ddlTable.Columns.Add("No");
            for (DataRow dr : dtMapAttr.Rows)
            {
                String lgType = dr.get("LGType").toString();
                String uiBindKey = dr.get("UIBindKey").toString();

                String uiVisible = dr.get("UIVisible").toString();
                if (uiVisible.equals("0") == true)
                    continue;

                if (DataType.IsNullOrEmpty(uiBindKey) == true)
                    continue; //为空就continue.

                if (lgType.equals("1") == true)
                    continue; //枚举值就continue;

                String uiIsEnable = dr.get("UIIsEnable").toString();
                if (uiIsEnable.equals("0") == true && lgType.equals("1") == true)
                    continue; //如果是外键，并且是不可以编辑的状态.

                if (uiIsEnable.equals("0") == true && lgType.equals("0") == true)
                    continue; //如果是外部数据源，并且是不可以编辑的状态.

                // 检查是否有下拉框自动填充。
                String keyOfEn = dr.get("KeyOfEn").toString();
                String fk_mapData = dr.get("FK_MapData").toString();

                //   #region 处理下拉框数据范围. for 小杨.
                me = (MapExt) mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL, MapExtAttr.AttrOfOper, keyOfEn);
                if (me != null)
                {
                    String fullSQL = me.getDoc();
                    fullSQL = fullSQL.replace("~", ",");
                    fullSQL = bp.wf.Glo.DealExp(fullSQL, en, null);
                    DataTable dt = DBAccess.RunSQLReturnTable(fullSQL);
                    if (bp.difference.SystemConfig.AppCenterDBFieldCaseModel.equals(FieldCaseModel.UpperCase))
                    {
                        if (dt.Columns.contains("NO") == true)
                            dt.Columns.get("NO").ColumnName = "No";
                        if (dt.Columns.contains("NAME") == true)
                            dt.Columns.get("NAME").ColumnName = "Name";
                        if (dt.Columns.contains("PARENTNO") == true)
                            dt.Columns.get("PARENTNO").ColumnName = "ParentNo";
                    }

                    if (bp.difference.SystemConfig.AppCenterDBFieldCaseModel.equals( FieldCaseModel.Lowercase))
                    {
                        if (dt.Columns.contains("no") == true)
                            dt.Columns.get("no").ColumnName = "No";
                        if (dt.Columns.contains("name") == true)
                            dt.Columns.get("name").ColumnName = "Name";
                        if (dt.Columns.contains("parentno") == true)
                            dt.Columns.get("parentno").ColumnName = "ParentNo";
                    }

                    dt.TableName = keyOfEn; //可能存在隐患，如果多个字段，绑定同一个表，就存在这样的问题.
                    ds.Tables.add(dt);
                    continue;
                }
                //    #endregion 处理下拉框数据范围.

                // 判断是否存在.
                if (ds.Tables.contains(uiBindKey) == true)
                    continue;

                DataTable dataTable = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey);

                if (dataTable != null)
                    ds.Tables.add(dataTable);
                else
                {
                    DataRow ddldr = ddlTable.NewRow();
                    ddldr.set("No",uiBindKey);
                    ddlTable.Rows.add(ddldr);
                }
            }
            ddlTable.TableName = "UIBindKey";
            ds.Tables.add(ddlTable);
           //     #endregion End把外键表加入DataSet


          //      #region 加入主表的数据.
            //增加主表数据.
            DataTable mainTable = en.ToDataTableField("MainTable");
            ds.Tables.add(mainTable);
            //    #endregion 加入主表的数据.
            // 执行表单事件. FrmLoadAfter .
            msg = ExecEvent.DoFrm(md, EventListFrm.FrmLoadAfter, en);
            if (DataType.IsNullOrEmpty(msg) == false)
                return "err@错误:" + msg;
            String json = Json.ToJson(ds);

            //DataType.WriteFile("c:/aaa.txt", json);
            return json;
        }
        catch (Exception ex)
        {
            GEEntityNoName myen = new GEEntityNoName(this.getEnsName());
            myen.CheckPhysicsTable();

            bp.sys.CCFormAPI.RepareCCForm(this.getEnsName());
            return "err@装载表单期间出现如下错误FrmGener_Init,ccform有自动诊断修复功能请在刷新一次，如果仍然存在请联系管理员. @" + ex.getMessage();
        }

    }
    /**
     * 执行数据初始化
     *
     * @return
     */
    public final String FrmGener_Init() throws Exception {
        if (this.getFrmID() != null && this.getFrmID().toUpperCase().contains("BP.") == true) {
            return FrmGener_Init_ForBPClass();
        }
        MapData md = new MapData(this.getEnsName());
        if (md.getEntityType() == EntityType.DBList) {
            return FrmGener_Init_ForDBList();
        }
        if (md.getEntityType() == EntityType.FrmEntityNoName)
            return FrmGener_Init_ForEntityNoName();
        ///#region 定义流程信息的所用的 配置entity.
        //节点与表单的权限控制.
        FrmNode fn = null;

        //是否启用装载填充？
        boolean isLoadData = true;
        //定义节点变量.
        Node nd = null;
        if (this.getNodeID() != 0 && this.getNodeID() != 999999) {
            nd = new Node(this.getNodeID());
            nd.WorkID = this.getWorkID(); //为获取表单ID ( NodeFrmID )提供参数.
            //if (nd.HisFormType== NodeFormType.FoolTruck)

            fn = new FrmNode(this.getNodeID(), this.getFrmID());
            isLoadData = fn.getItIsEnableLoadData();
        }
        ///#endregion 定义流程信息的所用的 配置entity.

        try {
            ///#region 特殊判断 适应累加表单.
            String fromWhere = this.GetRequestVal("FromWorkOpt");
            if (fromWhere != null && fromWhere.equals("1") && this.getNodeID() != 0 && this.getNodeID() != 999999) {
                //如果是累加表单.
                if (nd.getHisFormType() == NodeFormType.FoolTruck) {
                    DataSet myds = CCFlowAPI.GenerWorkNode(this.getFlowNo(), nd, this.getWorkID(), this.getFID(), WebUser.getNo(), this.getWorkID(), this.GetRequestVal("FromWorkOpt"));

                    return Json.ToJson(myds);
                }
            }

            ///#endregion 特殊判断.适应累加表单.


            //主表实体.
            GEEntityOID en = new GEEntityOID(this.getEnsName());

            long pk = this.getRefOID();
            if (pk == 0) {
                pk = this.getOID();
            }
            if (pk == 0) {
                pk = this.getWorkID();
            }

            ///#region 根据who is pk 获取数据.
            en.setOID(pk);
            if (en.getOID() == 0) {
                en.ResetDefaultVal(null, null, 0);
            } else {
                if (en.RetrieveFromDBSources() == 0) {
                    en.Insert();
                }
            }
            ///#endregion 根据who is pk 获取数据.
            String frmID = md.getNo();
            //根据表单存储的数据获取获取使用表单的版本号
            int frmVer = 0;
            if (en.getEnMap().getAttrs().contains("AtPara") == true) {
                frmVer = en.GetParaInt("FrmVer", 0);
                if (frmVer != 0 && frmVer != md.getVer2022()) {
                    frmID = md.getNo() + "." + frmVer;
                    if (nd.getFormType() != NodeFormType.FoolTruck) {
                        en = new GEEntityOID(frmID);
                        en.setOID(pk);
                        en.RetrieveFromDBSources();
                    }

                }
            }

            DataSet ds = bp.sys.CCFormAPI.GenerHisDataSet(frmID, null, null);
            //现在版本不是主版本的情况
            if (frmID.equals(this.getFrmID()) == false) {
                DataTable mddt = ds.GetTableByName("Sys_MapData");
                mddt.Rows.get(0).setValue("AtPara", mddt.Rows.get(0).getValue("AtPara") + "@MainFrmID=" + this.getFrmID());
                //如果是经典表单
                if (md.getHisFrmType() == FrmType.FoolForm) {
                    DataTable athdt = ds.GetTableByName("Sys_FrmAttachment");
                    if (frmVer != 0 && athdt.Rows.size() != 0) {
                        DataTable gfdt = ds.GetTableByName("Sys_GroupField");
                        for (DataRow dr : athdt.Rows) {
                            DataRow[] gfr = gfdt.Select("CtrlID='" + dr.get("MyPK") + "'");
                            if (gfr.length != 0) {
                                gfr[0].setValue("CtrlID", md.getNo() + "_" + dr.get("NoOfObj"));
                            }
                            dr.setValue("MyPK", md.getNo() + "_" + dr.get("NoOfObj"));

                        }

                    }

                }


            }

            //如果有框架
            if (ds.GetTableByName("Sys_MapFrame").Rows.size() > 0) {
                //把流程信息表发送过去.
                GenerWorkFlow gwf = new GenerWorkFlow();
                gwf.setWorkID(pk);
                gwf.RetrieveFromDBSources();
                ds.Tables.add(gwf.ToDataTableField("WF_GenerWorkFlow"));
            }


            ///#region 附加参数数据.
            if (SystemConfig.isBSsystem() == true) {
                // 处理传递过来的参数。
                for (String k : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
                    en.SetValByKey(k, this.GetRequestVal(k));
                }
            }

            // 执行表单事件. FrmLoadBefore .
            String msg = ExecEvent.DoFrm(md, EventListFrm.FrmLoadBefore, en);
            if (DataType.IsNullOrEmpty(msg) == false) {
                return "err@错误:" + msg;
            }

            //重设默认值.
            if (this.GetRequestValBoolen("IsReadonly") == false) {
                en.ResetDefaultVal(null, null, 0);
            }
            ///#region 执行装载填充.与相关的事件.
            MapExts mes = md.getMapExts();

            Object tempVar = mes.GetEntityByKey("ExtType", MapExtXmlList.PageLoadFull);
            MapExt me = tempVar instanceof MapExt ? (MapExt) tempVar : null;
            if (me == null) {
                Object tempVar2 = mes.GetEntityByKey("ExtModel", MapExtXmlList.PageLoadFullMainTable);
                me = tempVar2 instanceof MapExt ? (MapExt) tempVar2 : null;
            }
            if (isLoadData == true && md.getItIsPageLoadFull() && me != null && GetRequestValInt("IsTest") != 1) {
                //执行通用的装载方法.
                MapAttrs attrs = new MapAttrs(this.getEnsName());
                MapDtls dtls = new MapDtls(this.getEnsName());
                //判断是否自定义权限.
                boolean IsSelf = false;
                //单据或者是单据实体表单
                if (nd == null) {
                    Entity tempVar3 = CCFormAPI.DealPageLoadFull(en, me, attrs, dtls, IsSelf, 0, this.getWorkID());
                    en = tempVar3 instanceof GEEntityOID ? (GEEntityOID) tempVar3 : null;
                } else {
                    if ((nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.RefOneFrmTree) && (fn.getFrmSln() == FrmSln.Self)) {
                        IsSelf = true;
                    }
                    Entity tempVar4 = CCFormAPI.DealPageLoadFull(en, me, attrs, dtls, IsSelf, nd.getNodeID(), this.getWorkID());
                    en = tempVar4 instanceof GEEntityOID ? (GEEntityOID) tempVar4 : null;
                }
            }

            //执行事件, 不应该加.
            if (1 == 2) {
                ExecEvent.DoFrm(md, EventListFrm.SaveBefore, en, null);
            }
            ///#region 把外键表加入 DataSet.
            DataTable dtMapAttr = ds.GetTableByName("Sys_MapAttr");

            DataTable ddlTable = new DataTable();
            ddlTable.Columns.Add("No");
            for (DataRow dr : dtMapAttr.Rows) {
                String lgType = dr.get("LGType").toString();
                String uiBindKey = dr.get("UIBindKey").toString();

                String uiVisible = dr.get("UIVisible").toString();
                if (uiVisible.equals("0") == true) {
                    continue;
                }

                if (DataType.IsNullOrEmpty(uiBindKey) == true) {
                    continue; //为空就continue.
                }

                if (lgType.equals("1") == true) {
                    continue; //枚举值就continue;
                }

                String uiIsEnable = dr.get("UIIsEnable").toString();
                if (uiIsEnable.equals("0") == true && lgType.equals("1") == true) {
                    continue; //如果是外键，并且是不可以编辑的状态.
                }

                if (uiIsEnable.equals("0") == true && lgType.equals("0") == true) {
                    continue; //如果是外部数据源，并且是不可以编辑的状态.
                }

                // 检查是否有下拉框自动填充。
                String keyOfEn = dr.get("KeyOfEn").toString();
                String fk_mapData = dr.get("FK_MapData").toString();

                ///#region 处理下拉框数据范围. for 小杨.
                Object tempVar5 = mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL, MapExtAttr.AttrOfOper, keyOfEn);
                me = tempVar5 instanceof MapExt ? (MapExt) tempVar5 : null;
                if (me != null) {
                    Object tempVar6 = me.getDoc();
                    String fullSQL = tempVar6 instanceof String ? (String) tempVar6 : null;
                    fullSQL = fullSQL.replace("~", ",");
                    fullSQL = Glo.DealExp(fullSQL, en, null);

                    DataTable dt = DBAccess.RunSQLReturnTable(fullSQL);
                    if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase) {
                        if (dt.Columns.contains("NO") == true) {
                            dt.Columns.get("NO").ColumnName = "No";
                        }
                        if (dt.Columns.contains("NAME") == true) {
                            dt.Columns.get("NAME").ColumnName = "Name";
                        }
                        if (dt.Columns.contains("PARENTNO") == true) {
                            dt.Columns.get("PARENTNO").ColumnName = "ParentNo";
                        }
                    }

                    if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase) {
                        if (dt.Columns.contains("no") == true) {
                            dt.Columns.get("no").ColumnName = "No";
                        }
                        if (dt.Columns.contains("name") == true) {
                            dt.Columns.get("name").ColumnName = "Name";
                        }
                        if (dt.Columns.contains("parentno") == true) {
                            dt.Columns.get("parentno").ColumnName = "ParentNo";
                        }
                    }

                    dt.TableName = keyOfEn; //可能存在隐患，如果多个字段，绑定同一个表，就存在这样的问题.
                    ds.Tables.add(dt);
                    continue;
                }
                ///#endregion 处理下拉框数据范围.

                // 判断是否存在.
                if (ds.Tables.contains(uiBindKey) == true) {
                    continue;
                }

                DataTable dataTable = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null);

                if (dataTable != null) {
                    ds.Tables.add(dataTable);
                } else {
                    DataRow ddldr = ddlTable.NewRow();
                    ddldr.setValue("No", uiBindKey);
                    ddlTable.Rows.add(ddldr);
                }
            }
            ddlTable.TableName = "UIBindKey";
            ds.Tables.add(ddlTable);
            ///#region 加入组件的状态信息, 在解析表单的时候使用.
            if (nd != null && fn.isEnableFWC() != FrmWorkCheckSta.Disable) {
                FrmNodeComponent fnc = new FrmNodeComponent(nd.getNodeID());
                if (!nd.getNodeFrmID().equals("ND" + nd.getNodeID())) {
                    /*说明这是引用到了其他节点的表单，就需要把一些位置元素修改掉.*/
                    int refNodeID = 0;
                    if (nd.getNodeFrmID().indexOf("ND") == -1) {
                        refNodeID = nd.getNodeID();
                    } else {
                        refNodeID = Integer.parseInt(nd.getNodeFrmID().replace("ND", ""));
                    }

                    FrmNodeComponent refFnc = new FrmNodeComponent(refNodeID);

                    fnc.SetValByKey(NodeWorkCheckAttr.FWC_H, refFnc.GetValFloatByKey(NodeWorkCheckAttr.FWC_H));

                    fnc.SetValByKey(FrmSubFlowAttr.SF_H, refFnc.GetValFloatByKey(FrmSubFlowAttr.SF_H));


                    fnc.SetValByKey(FrmTrackAttr.FrmTrack_H, refFnc.GetValFloatByKey(FrmTrackAttr.FrmTrack_H));

                    fnc.SetValByKey(FTCAttr.FTC_H, refFnc.GetValFloatByKey(FTCAttr.FTC_H));
                    if (md.getHisFrmType() == FrmType.FoolForm) {
                        //判断是否是经典表单，如果是，就要判断该经典表单是否有审核组件groupfield ,没有的话就增加上.
                        DataTable gf = ds.GetTableByName("Sys_GroupField");
                        boolean isHave = false;
                        for (DataRow dr : gf.Rows) {
                            String cType = dr.get("CtrlType") instanceof String ? (String) dr.get("CtrlType") : null;
                            if (cType == null) {
                                continue;
                            }

                            if (cType.equals("FWC") == true) {
                                isHave = true;
                            }
                        }

                        if (isHave == false) {
                            DataRow dr = gf.NewRow();

                            nd.WorkID = this.getWorkID(); //为获取表单ID提供参数.
                            dr.setValue(GroupFieldAttr.OID, 100);
                            dr.setValue(GroupFieldAttr.FrmID, nd.getNodeFrmID());
                            dr.setValue(GroupFieldAttr.CtrlType, "FWC");
                            dr.setValue(GroupFieldAttr.CtrlID, "FWCND" + nd.getNodeID());
                            dr.setValue(GroupFieldAttr.Idx, 100);
                            dr.setValue(GroupFieldAttr.Lab, "审核信息");
                            gf.Rows.add(dr);

                            ds.Tables.remove("Sys_GroupField");
                            ds.Tables.add(gf);

                            //执行更新,就自动生成那个丢失的字段分组.
                            refFnc.Update(); //这里生成到了NDxxx表单上去了。

                        }
                    }
                }
                ///#region 没有审核组件分组就增加上审核组件分组.
                if (fn.isEnableFWC() != FrmWorkCheckSta.Disable) {
                    //如果启用了审核组件，并且 节点表单与当前一致。
                    if (md.getHisFrmType() == FrmType.FoolForm || md.getHisFrmType() == FrmType.WPSFrm) {
                        //判断是否是经典表单，如果是，就要判断该经典表单是否有审核组件groupfield ,没有的话就增加上.
                        DataTable gf = ds.GetTableByName("Sys_GroupField");
                        boolean isHave = false;
                        for (DataRow dr : gf.Rows) {
                            String cType = dr.get("CtrlType") instanceof String ? (String) dr.get("CtrlType") : null;
                            if (cType == null) {
                                continue;
                            }

                            if (cType.equals("FWC") == true) {
                                isHave = true;
                            }
                        }

                        if (isHave == false) {
                            DataRow dr = gf.NewRow();

                            nd.WorkID = this.getWorkID(); //为获取表单ID提供参数.
                            dr.setValue(GroupFieldAttr.OID, 100);
                            dr.setValue(GroupFieldAttr.FrmID, nd.getNodeFrmID());
                            dr.setValue(GroupFieldAttr.CtrlType, "FWC");
                            dr.setValue(GroupFieldAttr.CtrlID, "FWCND" + nd.getNodeID());
                            dr.setValue(GroupFieldAttr.Idx, 100);
                            dr.setValue(GroupFieldAttr.Lab, "审核信息");
                            gf.Rows.add(dr);

                            ds.Tables.remove("Sys_GroupField");
                            ds.Tables.add(gf);

                            //更新,为了让其表单上自动增加审核分组.
                            FrmNodeComponent refFnc = new FrmNodeComponent(nd.getNodeID());
                            NodeWorkCheck fwc = new NodeWorkCheck(nd.getNodeID());
                            if (fn.getFrmSln() == FrmSln.Self || fn.getFrmSln() == FrmSln.Default) {
                                fwc.setHisFrmWorkCheckSta(FrmWorkCheckSta.Enable);
                            } else {
                                fwc.setHisFrmWorkCheckSta(FrmWorkCheckSta.Readonly);
                            }

                            refFnc.Update();
                        }
                    }
                }

                ///#endregion 没有审核组件分组就增加上审核组件分组.
                ds.Tables.add(fnc.ToDataTableField("WF_FrmNodeComponent"));
            }
            if (nd != null) {
                ds.Tables.add(nd.ToDataTableField("WF_Node"));
            }

            if (nd != null) {
                ds.Tables.add(fn.ToDataTableField("WF_FrmNode"));
            }


            ///#endregion 加入组件的状态信息, 在解析表单的时候使用.


            ///#region 处理权限方案
            if (nd != null && nd.getFormType() == NodeFormType.SheetTree) {

                ///#region 只读方案.
                if (fn.getFrmSln() == FrmSln.Readonly) {
                    for (DataRow dr : dtMapAttr.Rows) {
                        dr.setValue(MapAttrAttr.UIIsEnable, 0);
                    }

                    //改变他的属性. 不知道是否应该这样写？
                    ds.Tables.remove("Sys_MapAttr");
                    ds.Tables.add(dtMapAttr);
                }
                ///#region 自定义方案.
                if (fn.getFrmSln() == FrmSln.Self) {
                    //查询出来自定义的数据.
                    FrmFields ffs = new FrmFields();
                    ffs.Retrieve(FrmFieldAttr.FK_Node, nd.getNodeID(), FrmFieldAttr.FrmID, md.getNo(), null);

                    //遍历属性集合.
                    for (DataRow dr : dtMapAttr.Rows) {
                        String keyOfEn = dr.getValue(MapAttrAttr.KeyOfEn).toString();
                        for (FrmField ff : ffs.ToJavaList()) {
                            if (ff.getKeyOfEn().equals(keyOfEn) == false) {
                                continue;
                            }

                            dr.setValue(MapAttrAttr.UIIsEnable, ff.getUIIsEnable()); //是否只读?
                            dr.setValue(MapAttrAttr.UIVisible, ff.getUIVisible()); //是否可见?
                            dr.setValue(MapAttrAttr.UIIsInput, ff.getItIsNotNull()); //是否必填?
                            dr.setValue(MapAttrAttr.IsSigan, ff.getItIsSigan());


                            dr.setValue(MapAttrAttr.DefVal, ff.getDefVal()); //默认值.

                            Attr attr = new Attr();
                            attr.setMyDataType(DataType.AppString);
                            attr.setDefaultValOfReal(ff.getDefVal());
                            attr.setKey(ff.getKeyOfEn());

                            if (Objects.equals(dr.get(MapAttrAttr.UIIsEnable).toString(), "0")) {
                                attr.setUIIsReadonly(true);
                            } else {
                                attr.setUIIsReadonly(false);
                            }


                            //处理默认值.
                            if (DataType.IsNullOrEmpty(ff.getDefVal()) == true) {
                                continue;
                            }

                            //数据类型.
                            attr.setMyDataType(Integer.parseInt(dr.get(MapAttrAttr.MyDataType).toString()));
                            String v = ff.getDefVal();

                            //设置默认值.
                            String myval = en.GetValStrByKey(ff.getKeyOfEn());

                            // 设置默认值.
                            switch (ff.getDefVal()) {
                                case "@WebUser.No":
                                    if (attr.getUIIsReadonly() == true) {
                                        en.SetValByKey(attr.getKey(), WebUser.getNo());
                                    } else {
                                        if (DataType.IsNullOrEmpty(myval) || myval.equals(v)) {
                                            en.SetValByKey(attr.getKey(), WebUser.getNo());
                                        }
                                    }
                                    continue;
                                case "@WebUser.getName()":
                                    if (attr.getUIIsReadonly() == true) {
                                        en.SetValByKey(attr.getKey(), WebUser.getName());
                                    } else {
                                        if (DataType.IsNullOrEmpty(myval) || myval.equals(v)) {
                                            en.SetValByKey(attr.getKey(), WebUser.getName());
                                        }
                                    }
                                    continue;
                                case "@WebUser.FK_Dept":
                                case "@WebUser.DeptNo":
                                    if (attr.getUIIsReadonly() == true) {
                                        en.SetValByKey(attr.getKey(), WebUser.getDeptNo());
                                    } else {
                                        if (DataType.IsNullOrEmpty(myval) || myval.equals(v)) {
                                            en.SetValByKey(attr.getKey(), WebUser.getDeptNo());
                                        }
                                    }
                                    continue;
                                case "@WebUser.FK_DeptName":
                                    if (attr.getUIIsReadonly() == true) {
                                        en.SetValByKey(attr.getKey(), WebUser.getDeptName());
                                    } else {
                                        if (DataType.IsNullOrEmpty(myval) || myval.equals(v)) {
                                            en.SetValByKey(attr.getKey(), WebUser.getDeptName());
                                        }
                                    }
                                    continue;
                                case "@WebUser.FK_DeptNameOfFull":
                                case "@WebUser.FK_DeptFullName":
                                    if (attr.getUIIsReadonly() == true) {
                                        en.SetValByKey(attr.getKey(), WebUser.getDeptNameOfFull());
                                    } else {
                                        if (DataType.IsNullOrEmpty(myval) || myval.equals(v)) {
                                            en.SetValByKey(attr.getKey(), WebUser.getDeptNameOfFull());
                                        }
                                    }
                                    continue;
                                case "@RDT":
                                    if (attr.getUIIsReadonly() == true) {
                                        if (attr.getMyDataType() == DataType.AppDate || myval.equals(v)) {
                                            en.SetValByKey(attr.getKey(), DataType.getCurrentDate());
                                        }

                                        if (attr.getMyDataType() == DataType.AppDateTime || myval.equals(v)) {
                                            en.SetValByKey(attr.getKey(), DataType.getCurrentDateTime());
                                        }
                                    } else {
                                        if (DataType.IsNullOrEmpty(myval) || myval.equals(v)) {
                                            if (attr.getMyDataType() == DataType.AppDate) {
                                                en.SetValByKey(attr.getKey(), DataType.getCurrentDate());
                                            } else {
                                                en.SetValByKey(attr.getKey(), DataType.getCurrentDateTime());
                                            }
                                        }
                                    }
                                    continue;
                                case "@yyyy年MM月dd日":
                                case "@yyyy年MM月dd日HH时mm分":
                                case "@yy年MM月dd日":
                                case "@yy年MM月dd日HH时mm分":
                                    if (attr.getUIIsReadonly() == true)
                                        en.SetValByKey(attr.getKey(), DateUtils.format(new Date(), v.replace("@", "")));
                                    else if (DataType.IsNullOrEmpty(myval) || myval.equals(v))
                                        en.SetValByKey(attr.getKey(), DateUtils.format(new Date(), v.replace("@", "")));
                                    continue;
                                default:
                                    continue;
                            }
                        }
                    }

                    //改变他的属性. 不知道是否应该这样写？
                    ds.Tables.remove("Sys_MapAttr");
                    ds.Tables.add(dtMapAttr);

                    //处理radiobutton的模式的控件.
                }

                ///#endregion 自定义方案.

            }

            ///#endregion 处理权限方案s


            ///#region 加入主表的数据.
            //增加主表数据.
            DataTable mainTable = en.ToDataTableField("MainTable");
            ds.Tables.add(mainTable);

            ///#endregion 加入主表的数据.
            // 执行表单事件. FrmLoadAfter .
            msg = ExecEvent.DoFrm(md, EventListFrm.FrmLoadAfter, en);
            if (DataType.IsNullOrEmpty(msg) == false) {
                return "err@错误:" + msg;
            }
            String json = Json.ToJson(ds);

            //DataType.WriteFile("c:/aaa.txt", json);
            return json;
        } catch (RuntimeException ex) {
            GEEntity myen = new GEEntity(this.getEnsName());
            myen.CheckPhysicsTable();

            bp.sys.CCFormAPI.RepareCCForm(this.getEnsName());
            return "err@装载表单期间出现如下错误FrmGener_Init,ccform有自动诊断修复功能请在刷新一次，如果仍然存在请联系管理员. @" + ex.getMessage();
        }
    }

    public final String FrmFreeReadonly_Init() throws Exception {
        try {
            MapData md = new MapData(this.getEnsName());
            DataSet ds = bp.sys.CCFormAPI.GenerHisDataSet(md.getNo(), null, null);


            ///#region 把主表数据放入.
            String atParas = "";
            //主表实体.
            GEEntityOID en = new GEEntityOID(this.getEnsName());


            ///#region 求出 who is pk 值.
            long pk = this.getRefOID();
            if (pk == 0) {
                pk = this.getOID();
            }
            if (pk == 0) {
                pk = this.getWorkID();
            }

            if (this.getNodeID() != 0 && DataType.IsNullOrEmpty(this.getFlowNo()) == false) {
                /*说明是流程调用它， 就要判断谁是表单的PK.*/
                FrmNode fn = new FrmNode(this.getNodeID(), this.getFrmID());
                switch (fn.getWhoIsPK()) {
                    case FID:
                        pk = this.getFID();
                        if (pk == 0) {
                            throw new RuntimeException("@没有接收到参数FID");
                        }
                        break;
                    case PWorkID: //父流程ID
                        pk = this.getPWorkID();
                        if (pk == 0) {
                            throw new RuntimeException("@没有接收到参数PWorkID");
                        }
                        break;
                    case OID:
                    default:
                        break;
                }
            }

            ///#endregion  求who is PK.

            en.setOID(pk);
            en.RetrieveFromDBSources();

            //增加主表数据.
            DataTable mainTable = en.ToDataTableField(md.getNo());
            mainTable.TableName = "MainTable";

            ds.Tables.add(mainTable);

            ///#endregion 把主表数据放入.

            return Json.ToJson(ds);
        } catch (RuntimeException ex) {
            GEEntity myen = new GEEntity(this.getEnsName());
            myen.CheckPhysicsTable();

            bp.sys.CCFormAPI.RepareCCForm(this.getEnsName());
            return "err@装载表单期间出现如下错误FrmFreeReadonly_Init,ccform有自动诊断修复功能请在刷新一次，如果仍然存在请联系管理员. @" + ex.getMessage();
        }
    }

    /**
     * 执行保存
     *
     * @return
     */
    public final String FrmGener_Save() {
        try {
            //保存主表数据.
            GEEntityOID en = new GEEntityOID(this.getEnsName());
            ///#region 求出 who is pk 值.
            long pk = this.getRefOID();
            if (pk == 0) {
                pk = this.getOID();
            }
            if (pk == 0) {
                pk = this.getWorkID();
            }

            if (this.getNodeID() != 0 && DataType.IsNullOrEmpty(this.getFlowNo()) == false) {
                /*说明是流程调用它， 就要判断谁是表单的PK.*/
                FrmNode fn = new FrmNode(this.getNodeID(), this.getFrmID());
                switch (fn.getWhoIsPK()) {
                    case FID:
                        pk = this.getFID();
                        if (pk == 0) {
                            throw new RuntimeException("@没有接收到参数FID");
                        }
                        break;
                    case PWorkID: //父流程ID
                        pk = this.getPWorkID();
                        if (pk == 0) {
                            throw new RuntimeException("@没有接收到参数PWorkID");
                        }
                        break;
                    case OID:
                    default:
                        break;
                }

                if (fn.getFrmSln() == FrmSln.Readonly) {
                    /*如果是不可以编辑*/
                    return "";
                }
            }

            ///#endregion  求who is PK.

            en.SetValByKey("OID",pk);
            int i = en.RetrieveFromDBSources();
            en.ResetDefaultVal(null, null, 0);

            try {
                Hashtable ht = bp.pub.PubClass.GetMainTableHT();
                for (Object item : ht.keySet()) {
                    if (item == null)
                        continue;
                    en.SetValByKey(item.toString(), ht.get(item));
                }
            } catch (RuntimeException ex) {
                return "err@方法：MyBill_SaveIt错误，在执行  GetMainTableHT 期间" + ex.getMessage();
            }

            en.setOID(pk);

            // 处理表单保存前事件.
            MapData md = new MapData(this.getEnsName());


            ///#region 调用事件.
            //是不是从表的保存.
            if (this.GetRequestValInt("IsForDtl") == 1) {

                ///#region 从表保存前处理事件.
                //获得主表事件.
                FrmEvents fes = new FrmEvents(this.getEnsName()); //获得事件.
                GEEntity mainEn = null;
                if (!fes.isEmpty()) {
                    String msg = fes.DoEventNode(EventListFrm.DtlRowSaveBefore, en);
                    if (DataType.IsNullOrEmpty(msg) == false) {
                        return "err@" + msg;
                    }
                }

                MapDtl mdtl = new MapDtl(this.getEnsName());
                if (mdtl.getFEBD().length() != 0) {
                    String str = mdtl.getFEBD();
                    bp.sys.base.FormEventBaseDtl febd = bp.sys.base.Glo.GetFormDtlEventBaseByEnName(mdtl.getNo());

                    if (febd != null) {
                        febd.HisEn = mdtl.GenerGEMainEntity(this.getRefPKVal());
                        febd.HisEnDtl = en;

                        febd.DoIt(EventListFrm.DtlRowSaveBefore, febd.HisEn, en, null);
                    }
                }

                ///#endregion 从表保存前处理事件.
            } else {
                ExecEvent.DoFrm(md, EventListFrm.SaveBefore, en);
            }

            ///#endregion 调用事件
            if (i == 0) {
                en.Insert();
            } else {
                en.Update();
            }


            ///#region 调用事件.
            if (this.GetRequestValInt("IsForDtl") == 1) {

                ///#region 从表保存前处理事件.
                //获得主表事件.
                FrmEvents fes = new FrmEvents(this.getEnsName()); //获得事件.
                GEEntity mainEn = null;
                if (!fes.isEmpty()) {
                    String msg = fes.DoEventNode(EventListFrm.DtlRowSaveBefore, en);
                    if (DataType.IsNullOrEmpty(msg) == false) {
                        return "err@" + msg;
                    }
                }

                MapDtl mdtl = new MapDtl(this.getEnsName());

                if (mdtl.getFEBD().length() != 0) {
                    String str = mdtl.getFEBD();
                    bp.sys.base.FormEventBaseDtl febd = bp.sys.base.Glo.GetFormDtlEventBaseByEnName(mdtl.getNo());

                    if (febd != null) {
                        febd.HisEn = mdtl.GenerGEMainEntity(this.getRefPKVal());
                        febd.HisEnDtl = en;

                        febd.DoIt(EventListFrm.DtlRowSaveAfter, febd.HisEn, en, null);
                    }
                }

                ///#endregion 从表保存前处理事件.
            } else {
                ExecEvent.DoFrm(md, EventListFrm.SaveAfter, en);
            }

            ///#endregion 调用事件

            return "保存成功.";
        } catch (Exception ex) {
            return "err@" + ex.getMessage();
        }
    }

    ///#endregion


    ///#region dtl.htm 从表.

    /**
     * 初始化从表数据
     *
     * @return 返回结果数据
     */
    public final String Dtl_Init() throws Exception {
        DataSet ds = Dtl_Init_Dataset();
        return Json.ToJson(ds);
    }

    private DataSet Dtl_Init_Dataset() throws Exception {

        ///#region 组织参数.
        MapDtl mdtl = new MapDtl(this.getEnsName());
        mdtl.setNo(this.getEnsName());

        String dtlRefPKVal = this.getRefPKVal(); //从表的RefPK
        MapAttr attr = new MapAttr();
        attr.setMyPK(mdtl.getNo() + "_Idx");
        if (attr.RetrieveFromDBSources() == 0) {
            attr.setFrmID(mdtl.getNo());
            attr.setEditType(EditType.Readonly);
            attr.setKeyOfEn("Idx");
            attr.setName("Idx");
            attr.setMyDataType(DataType.AppInt);
            attr.setUIContralType(UIContralType.TB);
            attr.setLGType(FieldTypeS.Normal);
            attr.setUIVisible(false);
            attr.setUIIsEnable(false);
            attr.setDefVal("0");
            attr.Insert();
            //增加表字段
            GEDtl dtl = new GEDtl(mdtl.getNo());
            boolean isExit = DBAccess.IsExitsObject(dtl.getEnMap().getEnDBUrl(), dtl.getEnMap().getPhysicsTable());
            if (isExit == false)
                dtl.CheckPhysicsTable();
            //增加表字段
            if (DBAccess.IsExitsTableCol(mdtl.getPTable(), "Idx") == false) {
                switch (SystemConfig.getAppCenterDBType()) {
                    case MSSQL:
                    case Oracle:
                    case DM:
                    case MySQL:
                    case KingBaseR3:
                    case KingBaseR6:
                    case PostgreSQL:
                    case UX:
                    case HGDB:
                    case GBASE8CByOracle:
                    case GBASE8CByMySQL:
                    case GBASE8A:
                        DBAccess.RunSQL("ALTER TABLE " + mdtl.getPTable() + " ADD Idx INT DEFAULT 0 NULL");
                        break;
                    default:
                        throw new RuntimeException("err@未解析的数据库类型" + SystemConfig.getAppCenterDBType());
                }
            }
            Cache.SetMap(mdtl.getNo(), null);
            Cache.getSQL_Cache().remove(mdtl.getNo());

        }

        ///#region 如果是测试，就创建表.
        if (this.getNodeID() == 999999 || this.GetRequestVal("IsTest") != null) {
            GEDtl dtl = new GEDtl(mdtl.getNo());
            dtl.CheckPhysicsTable();
            dtlRefPKVal = "0";
        }

        ///#endregion 如果是测试，就创建表.

        String frmID = mdtl.getFrmID();

        if (this.getNodeID() != 0 && this.getNodeID() != 999999) {
            frmID = frmID.replace("_" + this.getNodeID(), "");
        }

        if (this.getNodeID() != 0 && mdtl.getFrmID().equals("Temp") == false && this.getNodeID() != 999999) {
            Node nd = new Node(this.getNodeID());

            Flow flow = new Flow(nd.getFlowNo());

            FrmNode fn = new FrmNode(nd.getNodeID(), frmID);

            if (flow.getFlowDevModel() == FlowDevModel.JiJian || nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.RefOneFrmTree || nd.getHisFormType() == NodeFormType.FoolTruck) {
                /*如果
                 * 1,传来节点ID, 不等于0.
                 * 2,不是节点表单.  就要判断是否是独立表单，如果是就要处理权限方案。*/
                if (fn.getFrmSln() == FrmSln.Readonly) {
                    mdtl.setItIsInsert(false);
                    mdtl.setItIsDelete(false);
                    mdtl.setItIsUpdate(false);
                    mdtl.setItIsReadonly(true);
                }

                /**自定义权限.*/
                if (fn.getFrmSln() == FrmSln.Self) {
                    mdtl.setNo(this.getEnsName() + "_" + this.getNodeID());
                    if (mdtl.RetrieveFromDBSources() == 0) {
                        mdtl.setNo(this.getEnsName());
                        /*如果设置了自定义方案，但是没有定义，从表属性，就需要去默认值. */
                    }
                }
            }
            dtlRefPKVal = Dev2Interface.GetDtlRefPKVal(this.getWorkID(), this.getPWorkID(), this.getFID(), this.getNodeID(), frmID, mdtl);
            if (dtlRefPKVal.equals("0") == true || DataType.IsNullOrEmpty(dtlRefPKVal)) {
                dtlRefPKVal = this.getRefPKVal();
            }

        }

        if (this.GetRequestValInt("IsReadonly") == 1) {
            mdtl.setItIsInsert(false);
            mdtl.setItIsDelete(false);
            mdtl.setItIsUpdate(false);
        }


        String strs = this.getRequestParas();
        strs = strs.replace("?", "@");
        strs = strs.replace("&", "@");

        ///#endregion 组织参数.

        //获得他的描述,与数据.
        DataSet ds = bp.wf.CCFormAPI.GenerDBForCCFormDtl(frmID, mdtl, this.getRefPKVal(), strs, dtlRefPKVal, this.getFID(), this.GetRequestValBoolen("IshistoryData"), this.GetRequestVal("TrackID"));
        return ds;
    }

    /**
     * 识别发票
     * @return
     */
    public String Dtl_Invoice() throws Exception {
       //#region 获取上传的文件并转成Base64
        long filesSize = CommonFileUtils.getFilesSize(getRequest(), "file");
        if (filesSize == 0) {
            return "err@请上传需要识别的发票";
        }
        //判断上传的图片格式
        String fileName = CommonFileUtils.getOriginalFilename(getRequest(), "file");
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        String exts = "PNG、JPG、JPEG、BMP";
        if (exts.contains(ext.toUpperCase()) == false)
            return "err@上传的文件格式有误,请上传" + exts + "格式的图片";
        InputStream stream = CommonFileUtils.getInputStream(getRequest(), "file");//new MemoryStream();
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        Base64.Encoder encoder = Base64.getEncoder();
        String imgParam = encoder.encodeToString(bytes);
        stream.close();
        //#endregion 获取上传的文件

        //#region 百度云识别发票
        String token = bp.wf.port.baiduocr.BaiDu.getAccessToken();
        JSONObject jd = JSONObject.fromObject(token);
        String host = "https://aip.baidubce.com/rest/2.0/ocr/v1/multiple_invoice?access_token=" + jd.getString("access_token");
        // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
        Hashtable<String,String> map = new Hashtable<String,String>();
        map.put("access_token",jd.getString("access_token"));
        map.put("id_card_side","front");
        map.put("image",imgParam);

        String result =HttpClientUtil.doPost(host, map, "Content-Type","application/x-www-form-urlencoded");
        if (result.contains("error_code"))
        {
            JSONObject resultObj = JSONObject.fromObject(result);
            String code = resultObj.getString("error_code");
            String msg = resultObj.getString("error_msg");
            return "err@发票识别错误，错误码：" + code + "错误信息：" + msg + "，请您在[百度云官方API](https://cloud.baidu.com/doc/OCR/s/dk3h7y5vr)中根据错误码查询错误原因";
        }
        //#endregion 百度云识别发票

        //#region 开始保存数据.
        //JObject jObj = new JObject(result);
        result = JSONObject.fromObject(result).getString("words_result");
        JSONArray json = JSONArray.fromObject(result);
        GEDtl dtl = new GEDtl(this.getFrmID());
        dtl.SetValByKey("RefPK", this.getRefPKVal());
        for(int i =0;i<json.size();i++)
        {
            String strr = json.getString(i);
            JSONObject item = JSONObject.fromObject(strr);
            String resultStr = item.getString("result");
            JSONObject resultObj = JSONObject.fromObject(resultStr);
            //发票类型
            String type = item.getString("type");
            String typeStr = "";
            switch (type)
            {
                case "vat_invoice":
                    String strObj = resultObj.getString("InvoiceType");
                    typeStr = JSONArray.fromObject(strObj).optJSONObject(0).getString("word");
                    //获取发票代码
                    String invoiceCode = resultObj.getString("InvoiceCode");
                    if (JSONArray.fromObject(invoiceCode).size() == 0)
                        dtl.SetValByKey("InvoiceID", "");
                    else
                        dtl.SetValByKey("InvoiceID", JSONArray.fromObject(invoiceCode).optJSONObject(0).getString("word"));
                    // 发票号码
                    String invoiceNum = resultObj.getString("InvoiceNum");
                    if (JSONArray.fromObject(invoiceNum).size() == 0)
                        dtl.SetValByKey("InvoiceCode", "");
                    else
                        dtl.SetValByKey("InvoiceCode", JSONArray.fromObject(invoiceNum).optJSONObject(0).getString("word"));
                    //开票日期
                    String invoiceDate = resultObj.getString("InvoiceDate");
                    if (JSONArray.fromObject(invoiceDate).size() == 0)
                        dtl.SetValByKey("InvoiceRelDT", "");
                    else
                        dtl.SetValByKey("InvoiceRelDT", JSONArray.fromObject(invoiceDate).optJSONObject(0).getString("word"));

                    //发票金额(价税合计)
                    String amountInFiguers = resultObj.getString("AmountInFiguers");
                    if (JSONArray.fromObject(amountInFiguers).size() == 0)
                        dtl.SetValByKey("InvoiceJE", "");
                    else
                        dtl.SetValByKey("InvoiceJE", JSONArray.fromObject(amountInFiguers).optJSONObject(0).getString("word"));
                    //税额
                    String commodityTax = resultObj.getString("CommodityTax");
                    if (JSONArray.fromObject(commodityTax).size() == 0)
                        dtl.SetValByKey("InvoiceTaxJE", "");
                    else
                        dtl.SetValByKey("InvoiceTaxJE", JSONArray.fromObject(commodityTax).optJSONObject(0).getString("word"));
                    // 使用的税率
                    String commodityTaxRate = resultObj.getString("CommodityTaxRate");
                    if (JSONArray.fromObject(commodityTaxRate).size() == 0)
                        dtl.SetValByKey("InvoiceTaxRate", "");
                    else
                        dtl.SetValByKey("InvoiceTaxRate", JSONArray.fromObject(commodityTaxRate).optJSONObject(0).getString("word"));
                    //购买方名称
                    String purchaserName = resultObj.getString("PurchaserName");
                    if (JSONArray.fromObject(purchaserName).size() == 0)
                        dtl.SetValByKey("InvoiceBuyName", "");
                    else
                        dtl.SetValByKey("InvoiceBuyName", JSONArray.fromObject(purchaserName).optJSONObject(0).getString("word"));
                    //购买方ID
                    String purchaserRegisterNum = resultObj.getString("PurchaserRegisterNum");
                    if (JSONArray.fromObject(purchaserRegisterNum).size() == 0)
                        dtl.SetValByKey("InvoiceBuyCode", "");
                    else
                        dtl.SetValByKey("InvoiceBuyCode", JSONArray.fromObject(purchaserRegisterNum).optJSONObject(0).getString("word"));
                    //购买方地址电话
                    String purchaserAddress = resultObj.getString("PurchaserAddress");
                    if (JSONArray.fromObject(purchaserAddress).size() == 0)
                        dtl.SetValByKey("InvoiceBuyAddr", "");
                    else
                        dtl.SetValByKey("InvoiceBuyAddr", JSONArray.fromObject(purchaserAddress).optJSONObject(0).getString("word"));
                    //购买方开户行及账号
                    String purchaserBank = resultObj.getString("PurchaserBank");
                    if (JSONArray.fromObject(purchaserBank).size() == 0)
                        dtl.SetValByKey("InvoiceBuyBankInfo", "");
                    else
                        dtl.SetValByKey("InvoiceBuyBankInfo", JSONArray.fromObject(purchaserBank).optJSONObject(0).getString("word"));
                    //销售方名称
                    String sellerName = resultObj.getString("SellerName");
                    if (JSONArray.fromObject(sellerName).size() == 0)
                        dtl.SetValByKey("InvoiceSaleName", "");
                    else
                        dtl.SetValByKey("InvoiceSaleName", JSONArray.fromObject(sellerName).optJSONObject(0).getString("word"));
                    //销售方ID
                    String sellerRegisterNum = resultObj.getString("SellerRegisterNum");
                    if (JSONArray.fromObject(sellerRegisterNum).size() == 0)
                        dtl.SetValByKey("InvoiceSaleCode", "");
                    else
                        dtl.SetValByKey("InvoiceSaleCode", JSONArray.fromObject(sellerRegisterNum).optJSONObject(0).getString("word"));
                    //销售方地址电话
                    String sellerAddress = resultObj.getString("SellerAddress");
                    if (JSONArray.fromObject(sellerAddress).size() == 0)
                        dtl.SetValByKey("InvoiceSaleAddr", "");
                    else
                        dtl.SetValByKey("InvoiceSaleAddr", JSONArray.fromObject(sellerAddress).optJSONObject(0).getString("word"));
                    //销售方开户行及账号
                    String sellerBank = resultObj.getString("SellerBank");
                    if (JSONArray.fromObject(sellerBank).size() == 0)
                        dtl.SetValByKey("InvoiceSaleBankInfo", "");
                    else
                        dtl.SetValByKey("InvoiceSaleBankInfo", JSONArray.fromObject(sellerBank).optJSONObject(0).getString("word"));
                    break;
                case "taxi_receipt":
                    typeStr = "出租车票";


                    break;
                case "train_ticket":
                    typeStr = "火车票";
                    //车票号
                    String ticket_num = JSONArray.fromObject(resultObj.getString("ticket_num")).optJSONObject(0).getString("word");
                    //始发站
                    String starting_station = JSONArray.fromObject(resultObj.getString("starting_station")).optJSONObject(0).getString("word");
                    //车次号
                    String train_num = JSONArray.fromObject(resultObj.getString("train_num")).optJSONObject(0).getString("word");
                    //到达站
                    String destination_station = JSONArray.fromObject(resultObj.getString("destination_station")).optJSONObject(0).getString("word");
                    //出发日期
                    String ticket_date = JSONArray.fromObject(resultObj.getString("date")).optJSONObject(0).getString("word");
                    //车票金额
                    String ticket_rates = JSONArray.fromObject(resultObj.getString("ticket_rates")).optJSONObject(0).getString("word");
                    //席别
                    String seat_category = JSONArray.fromObject(resultObj.getString("seat_category")).optJSONObject(0).getString("word");
                    //乘客姓名
                    String ticket_name = JSONArray.fromObject(resultObj.getString("name")).optJSONObject(0).getString("word");
                    //身份证号
                    String id_num = JSONArray.fromObject(resultObj.getString("ID_card")).optJSONObject(0).getString("word");
                    //序列号
                    String serial_number =  JSONArray.fromObject(resultObj.getString("serial_number")).optJSONObject(0).getString("word");
                    //售站
                    String sales_station = JSONArray.fromObject(resultObj.getString("sales_station")).optJSONObject(0).getString("word");

                    //发票代码
                    dtl.SetValByKey("InvoiceID", ticket_num);
                    // 发票号码
                    String invoice_num = resultObj.getString("invoice_num");
                    if (JSONArray.fromObject(invoice_num).size() == 0)
                        dtl.SetValByKey("InvoiceCode", invoice_num);
                    else
                        dtl.SetValByKey("InvoiceCode", JSONArray.fromObject(resultObj.getString("invoice_num")).optJSONObject(0).getString("word"));
                    //开票日期
                    String invoice_date = resultObj.getString("invoice_date");
                    if (JSONArray.fromObject(invoice_date).size() == 0)
                        dtl.SetValByKey("InvoiceRelDT", invoice_date);
                    else
                        dtl.SetValByKey("InvoiceRelDT", JSONArray.fromObject(resultObj.getString("invoice_date")).optJSONObject(0).getString("word"));
                    //不含税金额
                    String fare = JSONArray.fromObject(resultObj.getString("fare")).optJSONObject(0).getString("word");

                    //发票金额(价税合计)
                    dtl.SetValByKey("InvoiceJE", ticket_rates);
                    //税额
                    String tax = resultObj.getString("tax");
                    if (JSONArray.fromObject(tax).size() == 0)
                        dtl.SetValByKey("InvoiceTaxJE", invoice_date);
                    else
                        dtl.SetValByKey("InvoiceTaxJE", JSONArray.fromObject(resultObj.getString("tax")).optJSONObject(0).getString("word"));
                    // 使用的税率
                    String tax_rate = resultObj.getString("tax_rate");
                    if (JSONArray.fromObject(tax_rate).size() == 0)
                        dtl.SetValByKey("InvoiceTaxRate", tax_rate);
                    else
                        dtl.SetValByKey("InvoiceTaxRate", JSONArray.fromObject(resultObj.getString("tax_rate")).optJSONObject(0).getString("word"));
                    //购买方名称
                    dtl.SetValByKey("InvoiceBuyName", ticket_name);
                    //购买方ID
                    dtl.SetValByKey("InvoiceBuyCode", id_num);
                    //销售方名称
                    dtl.SetValByKey("InvoiceSaleName", sales_station);
                    //获取其他信息
                    dtl.SetValByKey("Note",ticket_name+"乘坐"+train_num+"列车于"+ticket_date+"出发,从"+starting_station+"到"+destination_station+"");
                    break;
                case "quota_invoice":
                    typeStr = "定额发票";
                    break;
                case "air_ticket":
                    typeStr = "飞机行程单";
                    String name = JSONArray.fromObject(resultObj.getString("name")).optJSONObject(0).getString("word");
                    String carrier = JSONArray.fromObject(resultObj.getString("carrier")).optJSONObject(0).getString("word");
                    String date = JSONArray.fromObject(resultObj.getString("date")).optJSONObject(0).getString("word");
                    //发票金额(价税合计)
                    dtl.SetValByKey("InvoiceJE", JSONArray.fromObject(resultObj.getString("ticket_rates")).optJSONObject(0).getString("word"));
                    dtl.SetValByKey("InvoiceRelDT", date);
                    dtl.SetValByKey("InvoiceBuyName", name);
                    dtl.SetValByKey("InvoiceSaleName", carrier);
                    dtl.SetValByKey("InvoiceSaleCode", JSONArray.fromObject(resultObj.getString("agent_code")).optJSONObject(0).getString("word"));
                    //获取其他信息
                    String startingStation = JSONArray.fromObject(resultObj.getString("starting_station")).optJSONObject(0).getString("word");
                    String destinationStation = JSONArray.fromObject(resultObj.getString("destination_station")).optJSONObject(0).getString("word");
                    String time = JSONArray.fromObject(resultObj.getString("time")).optJSONObject(0).getString("word");
                    String flight = JSONArray.fromObject(resultObj.getString("flight")).optJSONObject(0).getString("word");
                    dtl.SetValByKey("Note",name+"乘坐"+carrier+flight+date+" "+time+"从"+startingStation+"到"+destinationStation+"出发");

                    break;
                case "roll_normal_invoice":
                    typeStr = "卷票";
                    break;
                case "printed_invoice":
                    typeStr = "机打发票";
                    break;
                case "bus_ticket":
                    typeStr = "汽车票";
                    break;
                case "toll_invoice":
                    typeStr = "过路过桥费发票";
                    break;
                case "ferry_ticket":
                    typeStr = "船票";
                    break;
                case "motor_vehicle_invoice":
                    typeStr = "机动车销售发票";
                    break;
                case "used_vehicle_invoice":
                    typeStr = "二手车发票";
                    break;
                case "taxi_online_ticket":
                    typeStr = "网约车行程单";
                    break;
                case "limit_invoice":
                    typeStr = "限额发票";
                    break;
                case "shopping_receipt":
                    typeStr = "购物小票";
                    break;
                case "pos_invoice":
                    typeStr = "POS小票";
                    break;
                default:
                    typeStr = "POS小票";
                    break;
            }
            dtl.SetValByKey("InvoiceTypeStr", typeStr);


        }

        dtl.Insert();
        //把附件保存到temp文件中
        String targetFilePath=SystemConfig.getPathOfTemp()+"/"+DataType.getDateByFormart(new Date(),"HHmmss")+fileName;
        CommonFileUtils.upload(getRequest(),"file",new File(targetFilePath));
        //保存附件信息
        Dev2Interface.CCForm_AddAth(0,null,dtl.getOID(),this.getFrmID()+"_InvoiceFJ",this.getFrmID(),targetFilePath,fileName);
        //#endregion 开始保存数据.
        return "";
    }

    /**
     * 执行从表的保存.
     *
     * @return
     */
    public final String Dtl_Save() throws Exception {
        MapDtl mdtl = new MapDtl(this.getEnsName());
        String dtlRefPKVal = this.getRefPKVal();
        String fk_mapDtl = this.getMapDtlNo();
        FrmEvents fes = new FrmEvents(this.getEnsName()); //获得事件.
        GEEntity mainEn = null;
        //获取集合
        String str = this.GetRequestVal("Json");
        if (DataType.IsNullOrEmpty(str) == true) {
            return "不需要保存";
        }
        ///#region 从表保存前处理事件.
        if (!fes.isEmpty()) {
            mainEn = mdtl.GenerGEMainEntity(this.getRefPKVal());
            String msg = fes.DoEventNode(EventListFrm.DtlRowSaveBefore, mainEn);
            if (msg != null) {
                throw new RuntimeException(msg);
            }
        }

        ///#region 处理权限方案。
        if (this.getNodeID() != 0 && this.getNodeID() != 999999) {
            Node nd = new Node(this.getNodeID());
            if (nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.RefOneFrmTree) {
                FrmNode fn = new FrmNode(nd.getNodeID(), this.getFrmID());
                if (fn.getFrmSln() == FrmSln.Self) {
                    String no = fk_mapDtl + "_" + nd.getNodeID();
                    MapDtl mdtlSln = new MapDtl();
                    mdtlSln.setNo(no);
                    int result = mdtlSln.RetrieveFromDBSources();
                    if (result != 0) {
                        mdtl = mdtlSln;
                        fk_mapDtl = no;
                    }
                }
            }

            dtlRefPKVal = Dev2Interface.GetDtlRefPKVal(this.getWorkID(), this.getPWorkID(), this.getFID(), this.getNodeID(), this.getFrmID(), mdtl);
            if (dtlRefPKVal.equals("0") == true) {
                dtlRefPKVal = this.getRefPKVal();
            }
        }
        ///#endregion 处理权限方案。

        ///#region 保存的业务逻辑.
        GEDtls dtls = new GEDtls(this.getEnsName());
        bp.en.Entity tempVar = dtls.getNewEntity();
        GEDtl dtl = tempVar instanceof GEDtl ? (GEDtl) tempVar : null;
        Attrs attrs = dtl.getEnMap().getAttrs();
        JSONArray json = JSONArray.fromObject(str);
        int idx = 0;
        for (int i = 0; i < json.size(); i++) {
            String strr = json.getString(i);
            JSONObject item = JSONObject.fromObject(strr);
            Object result = item.get("OID");
            long oid = 0;
            if (result != null) {
                oid = Long.parseLong(result.toString());
            }

            for (Attr attr : attrs) {
                if (attr.getItIsRefAttr() == true) {
                    continue;
                }
                String val = item.get(attr.getField()) != null ? item.get(attr.getField()).toString() : "";

                if (attr.getMyDataType() == DataType.AppDateTime || attr.getMyDataType() == DataType.AppDate) {
//                    if (attr.getUIIsReadonly() == true) {
//                        continue;
//                    }
                    dtl.SetValByKey(attr.getKey(), val);
                    continue;
                }
                if (attr.getUIContralType() == UIContralType.CheckBok && attr.getMyDataType() == DataType.AppInt) {
                    if (val.equals("0")) {
                        dtl.SetValByKey(attr.getKey(), 0);
                    } else {
                        dtl.SetValByKey(attr.getKey(), 1);
                    }
                    continue;
                }
                dtl.SetValByKey(attr.getKey(), val);
            }
            dtl.SetValByKey("OID", oid);
            //关联主赋值.
            dtl.setRefPK(dtlRefPKVal);
            if (this.getFID() == 0 && DataType.IsNumStr(dtl.getRefPK())) {
                dtl.setFID(Long.parseLong(dtl.getRefPK()));
            } else {
                dtl.setFID(this.getFID());
            }

            //是ForFID的模式.
            if (mdtl.getDtlOpenType() == DtlOpenType.ForFID) {
                dtl.setRefPK(Long.toString(this.getFID()));
            }
            dtl.setRec(WebUser.getNo());
            dtl.SetValByKey("Idx", idx);
            if (oid == 0) {
                dtl.Insert();
                //处理从表行数据插入成功后，更新FrmEleDB中数据
                String dbstr = SystemConfig.getAppCenterDBVarStr();
                Paras paras = new Paras();
                if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.DM || bp.difference.SystemConfig.getAppCenterDBType() == DBType.Oracle || bp.difference.SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || bp.difference.SystemConfig.getAppCenterDBType() == DBType.UX || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
                    paras.SQL = "UPDATE Sys_FrmEleDB SET RefPKVal=" + dbstr + "RefPKVal,MyPK=EleID||'_'||" + dtl.getOID() + "||'_'||Tag1  WHERE RefPKVal=" + dbstr + "OldRefPKVal AND FK_MapData='" + mdtl.getNo() + "'";
                else if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
                    paras.SQL = "UPDATE Sys_FrmEleDB SET RefPKVal=" + dbstr + "RefPKVal,MyPK=CONCAT(EleID,'_'," + dtl.getOID() + ",'_',Tag1)  WHERE RefPKVal=" + dbstr + "OldRefPKVal AND FK_MapData='" + mdtl.getNo() + "'";
                else
                    paras.SQL = "UPDATE Sys_FrmEleDB SET RefPKVal=" + dbstr + "RefPKVal,MyPK= EleID+'_'+'" + dtl.getOID() + "'+'_'+Tag1  WHERE RefPKVal=" + dbstr + "OldRefPKVal AND FK_MapData='" + mdtl.getNo() + "'";

                paras.Add("RefPKVal", String.valueOf(dtl.getOID()), false);
                paras.Add("OldRefPKVal", this.getRefPKVal() + "_" + idx, false);
                DBAccess.RunSQL(paras);
                //处理从表行数据插入成功后，更新FrmAttachmentDB中数据
                paras.SQL = "UPDATE Sys_FrmAttachmentDB SET RefPKVal=" + dbstr + "RefPKVal  WHERE RefPKVal=" + dbstr + "OldRefPKVal AND FK_MapData='" + mdtl.getNo() + "'";
                DBAccess.RunSQL(paras);
                idx++;
                continue;
            }
            dtl.Update();
            idx++;
        }
        if (!fes.isEmpty()) {
            String msg = fes.DoEventNode(EventListFrm.DtlRowSaveAfter, mainEn);
            if (msg != null) {
                throw new RuntimeException(msg);
            }
        }

        ///#endregion 保存的业务逻辑.
        DataTable dt = CCFormAPI.GetDtlInfo(mdtl, mainEn, dtlRefPKVal);

        return bp.tools.Json.ToJson(dt);
    }

    /**
     * 从表移动
     *
     * @return
     */
    public final String DtlList_Move() {
        int newIdx = this.GetRequestValInt("newIdx");
        int oldIdx = this.GetRequestValInt("oldIdx");
        long newOID = this.GetRequestValInt("newOID");
        long oldOID = this.GetRequestValInt("oldOID");
        //处理从表行数据插入成功后，更新FrmAttachmentDB中数据
        Paras paras = new Paras();
        paras.Add("FK_MapData", this.getFrmID(), false);

        String dbstr = SystemConfig.getAppCenterDBVarStr();
        String athSQL = "UPDATE Sys_FrmAttachmentDB SET RefPKVal=" + dbstr + "RefPKVal  WHERE RefPKVal=" + dbstr + "OldRefPKVal AND FK_MapData=" + dbstr + "FK_MapData";
        String eleSQL = "UPDATE Sys_FrmEleDB SET RefPKVal=" + dbstr + "RefPKVal  WHERE RefPKVal=" + dbstr + "OldRefPKVal AND FK_MapData=" + dbstr + "FK_MapData";
        String refPKVal = this.getRefPKVal();
        String newRefPKVal = refPKVal + "_" + newIdx + "_" + newIdx;
        String oldRefPKVal = refPKVal + "_" + oldIdx;
        if (newOID == 0 && oldOID == 0) {
            paras.Add("RefPKVal", newRefPKVal);
            paras.Add("OldRefPKVal", oldRefPKVal);
            paras.SQL = athSQL;
            DBAccess.RunSQL(paras);
            paras.SQL = eleSQL;
            DBAccess.RunSQL(paras);
            newRefPKVal = refPKVal + "_" + oldIdx + "_" + oldIdx;
            oldRefPKVal = refPKVal + "_" + newIdx;
            paras.Add("RefPKVal", newRefPKVal);
            paras.Add("OldRefPKVal", oldRefPKVal);
            paras.SQL = athSQL;
            DBAccess.RunSQL(paras);
            paras.SQL = eleSQL;
            DBAccess.RunSQL(paras);
            return "从表拖拽完成";
        }
        if (newOID == 0) {
            paras.Add("RefPKVal", newRefPKVal);
            paras.Add("OldRefPKVal", oldRefPKVal);
            paras.SQL = athSQL;
            DBAccess.RunSQL(paras);
            paras.SQL = eleSQL;
            DBAccess.RunSQL(paras);
        }
        if (oldOID == 0) {
            newRefPKVal = refPKVal + "_" + oldIdx + "_" + oldIdx;
            oldRefPKVal = refPKVal + "_" + newIdx;
            paras.Add("RefPKVal", newRefPKVal);
            paras.Add("OldRefPKVal", oldRefPKVal);
        }
        paras.SQL = athSQL;
        DBAccess.RunSQL(paras);
        paras.SQL = eleSQL;
        DBAccess.RunSQL(paras);
        return "从表拖拽完成";
    }

    public final String DtlList_MoveAfter() {
        Paras paras = new Paras();
        paras.Add("FK_MapData", this.getFrmID(), false);
        int newIdx = this.GetRequestValInt("newIdx");
        String dbstr = SystemConfig.getAppCenterDBVarStr();
        String athSQL = "";
        String eleSQL = "";
        switch (SystemConfig.getAppCenterDBType()) {
            case MSSQL:
                athSQL = "UPDATE Sys_FrmAttachmentDB SET RefPKVal=SUBSTRING(RefPKVal,0,CHARINDEX('_', RefPKVal, CHARINDEX('_', RefPKVal) + 1))  WHERE RefPKVal LIKE " + dbstr + "RefPKVal+'[_]%[_]%' AND FK_MapData=" + dbstr + "FK_MapData";
                eleSQL = "UPDATE Sys_FrmEleDB SET RefPKVal=SUBSTRING(RefPKVal,0,CHARINDEX('_', RefPKVal, CHARINDEX('_', RefPKVal) + 1))  WHERE RefPKVal LIKE " + dbstr + "RefPKVal+'[_]%[_]%' AND FK_MapData=" + dbstr + "FK_MapData";
                break;
            case Oracle:
            case KingBaseR3:
            case KingBaseR6:
            case GBASE8CByOracle:
                athSQL = "UPDATE Sys_FrmAttachmentDB SET RefPKVal=SUBSTR(RefPKVal,0,INSTR(RefPKVal,'_',1,2))  WHERE RefPKVal LIKE " + dbstr + "RefPKVal||'\\_%\\_%'  ESCAPE '\\' AND FK_MapData=" + dbstr + "FK_MapData";
                eleSQL = "UPDATE Sys_FrmEleDB SET RefPKVal=SUBSTR(RefPKVal,0,INSTR(RefPKVal,'_',1,2))  WHERE RefPKVal LIKE " + dbstr + "RefPKVal||'\\_%\\_%'  ESCAPE '\\' AND FK_MapData=" + dbstr + "FK_MapData";
                break;
            case MySQL:
            case GBASE8CByMySQL:
            case GBASE8A:
                athSQL = "UPDATE Sys_FrmAttachmentDB SET RefPKVal=SUBSTRING(RefPKVal,1,LOCATE('_', RefPKVal, LOCATE('_', RefPKVal)+1)-1)  WHERE RefPKVal LIKE CONCAT(" + dbstr + "RefPKVal,'\\\\_%\\\\_%') AND FK_MapData=" + dbstr + "FK_MapData";
                eleSQL = "UPDATE Sys_FrmEleDB SET RefPKVal=SUBSTRING(RefPKVal,1,LOCATE('_', RefPKVal, LOCATE('_', RefPKVal)+1)-1)  WHERE RefPKVal LIKE CONCAT(" + dbstr + "RefPKVal,'\\\\_%\\\\_%') AND FK_MapData=" + dbstr + "FK_MapData";
                break;
            case PostgreSQL:
            case HGDB:
                athSQL = "UPDATE Sys_FrmAttachmentDB SET RefPKVal=SUBSTRING(RefPKVal,1,CHAR_LENGTH(RefPKVal)-POSITION('_' in REVERSE(RefPKVal)))  WHERE RefPKVal LIKE CONCAT(" + dbstr + "RefPKVal,'!_%!_%')  ESCAPE '!' AND FK_MapData=" + dbstr + "FK_MapData";
                eleSQL = "UPDATE Sys_FrmEleDB SET RefPKVal=SUBSTRING(RefPKVal,1,CHAR_LENGTH(RefPKVal)-POSITION('_' in REVERSE(RefPKVal)))  WHERE RefPKVal LIKE CONCAT(" + dbstr + "RefPKVal,'!_%!_%')  ESCAPE '!' AND FK_MapData=" + dbstr + "FK_MapData";
                break;
            default:
                return "err@" + SystemConfig.getAppCenterDBType() + "还未解析";
        }
        paras.Add("RefPKVal", this.getRefPKVal(), false);
        paras.SQL = athSQL;
        DBAccess.RunSQL(paras);
        paras.SQL = eleSQL;
        DBAccess.RunSQL(paras);
        return "";
    }

    /**
     * 导出excel与附件信息,并且压缩一个压缩包.
     *
     * @return 返回下载路径
     */
    public final String Dtl_ExpZipFiles() throws Exception {
        DataSet ds = Dtl_Init_Dataset();

        return "err@尚未wancheng.";
    }

    /**
     * 保存单行数据
     *
     * @return
     */
    public final String Dtl_SaveRow() throws Exception {
        //从表.
        String fk_mapDtl = this.getMapDtlNo();
        String RowIndex = this.GetRequestVal("RowIndex");
        MapDtl mdtl = new MapDtl(fk_mapDtl);
        String dtlRefPKVal = this.getRefPKVal();

        String isRead = mdtl.getRow().GetValByKey(MapDtlAttr.IsReadonly).toString();
        //String isRead = this.GetRequestVal("IsReadonly");
        if (DataType.IsNullOrEmpty(isRead) == false && "1".equals(isRead) == true) {
            return "";
        }


        ///#region 处理权限方案。
        if (this.getNodeID() != 0 && this.getNodeID() != 999999 && mdtl.getNo().contains("ND") == false) {
            Node nd = new Node(this.getNodeID());
            if (nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.RefOneFrmTree) {
                FrmNode fn = new FrmNode(nd.getNodeID(), this.getFrmID());
                if (fn.getFrmSln() == FrmSln.Self) {
                    String no = fk_mapDtl + "_" + nd.getNodeID();
                    MapDtl mdtlSln = new MapDtl();
                    mdtlSln.setNo(no);
                    int result = mdtlSln.RetrieveFromDBSources();
                    if (result != 0) {
                        mdtl = mdtlSln;
                        fk_mapDtl = no;
                    }
                }
            }

            dtlRefPKVal = Dev2Interface.GetDtlRefPKVal(this.getWorkID(), this.getPWorkID(), this.getFID(), this.getNodeID(), this.getFrmID(), mdtl);
            if (dtlRefPKVal.equals("0") == true) {
                dtlRefPKVal = this.getRefPKVal();
            }
        }

        ///#endregion 处理权限方案。

        if (mdtl.getItIsReadonly() == true) {
            return "err@不允许保存.IsReadonly=1," + mdtl.getName() + " ID:" + mdtl.getNo();
        }

        if (mdtl.getItIsInsert() == false && mdtl.getItIsUpdate() == false) {
            return "err@不允许保存. IsInsert=false,IsUpdate=false " + mdtl.getName() + " ID:" + mdtl.getNo();
        }

        //从表实体.
        GEDtl dtl = new GEDtl(fk_mapDtl);
        int oid = this.getRefOID();
        if (oid != 0) {
            dtl.setOID(oid);
            dtl.RetrieveFromDBSources();
        } else {
            //关联主赋值.
            dtl.setRefPK(dtlRefPKVal);
            if (this.getFID() == 0) {
                dtl.setFID(Long.parseLong(dtl.getRefPK()));
            } else {
                dtl.setFID(this.getFID());
            }

            //如果是新建，并且里面是ForFID的模式.
            if (mdtl.getDtlOpenType() == DtlOpenType.ForFID) {
                dtl.setFID(Long.parseLong(dtlRefPKVal));
            }
        }


        ///#region 给实体循环赋值/并保存.
        Attrs attrs = dtl.getEnMap().getAttrs();
        for (Attr attr : attrs) {
            if (attr.getKey().equals("FID") == true || attr.getKey().equals("RefPK") == true) {
                continue;
            }

            dtl.SetValByKey(attr.getKey(), this.GetRequestVal(attr.getKey()));

        }
        dtl.SetValByKey("Idx", RowIndex);

        ///#region 从表保存前处理事件.
        //获得主表事件.
        FrmEvents fes = new FrmEvents(fk_mapDtl); //获得事件.
        GEEntity mainEn = null;
        if (!fes.isEmpty()) {
            mainEn = mdtl.GenerGEMainEntity(this.getRefPKVal());
            String msg = fes.DoEventNode(EventListFrm.DtlRowSaveBefore, mainEn);
            if (DataType.IsNullOrEmpty(msg) == false) {
                return "err@" + msg;
            }
        }

        if (mdtl.getFEBD().length() != 0) {
            String str = mdtl.getFEBD();
            bp.sys.base.FormEventBaseDtl febd = bp.sys.base.Glo.GetFormDtlEventBaseByEnName(mdtl.getNo());

            febd.HisEn = mdtl.GenerGEMainEntity(this.getRefPKVal());
            febd.HisEnDtl = dtl;

            febd.DoIt(EventListFrm.DtlRowSaveBefore, febd.HisEn, dtl, null);
        }

        ///#endregion 从表保存前处理事件.

        //一直找不到refpk  值为null .
        if (DataType.IsNullOrEmpty(dtl.getRefPK()) == true) {
            dtl.setRefPK(this.getRefPKVal());
        }

        if (dtl.getOID() == 0) {
            try {
                dtl.Insert();
            } catch (RuntimeException ex) {
                return "err@在保存行:" + RowIndex + " 错误: " + ex.getMessage();
            }
            boolean isVue3 = this.GetRequestValBoolen("IsVue3");
            if (isVue3 == false) {
                //dtl生成oid后，将pop弹出的FrmEleDB表中的数据用oid替换掉
                String refval = this.getRefPKVal() + "_" + RowIndex;
                //处理从表行数据插入成功后，更新FrmEleDB中数据
                String dbstr = SystemConfig.getAppCenterDBVarStr();
                Paras paras = new Paras();
                if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle) {
                    paras.SQL = "UPDATE Sys_FrmEleDB SET RefPKVal=" + dbstr + "RefPKVal,MyPK=EleID||'_'||" + String.valueOf(dtl.getOID()) + "||'_'||Tag1  WHERE RefPKVal=" + dbstr + "OldRefPKVal AND FK_MapData=" + dbstr + "FK_MapData";
                } else if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A) {
                    paras.SQL = "UPDATE Sys_FrmEleDB SET RefPKVal=" + dbstr + "RefPKVal,MyPK=CONCAT(EleID,'_'," + String.valueOf(dtl.getOID()) + ",'_',Tag1)  WHERE RefPKVal=" + dbstr + "OldRefPKVal AND FK_MapData=" + dbstr + "FK_MapData";
                } else {
                    paras.SQL = "UPDATE Sys_FrmEleDB SET RefPKVal=" + dbstr + "RefPKVal,MyPK= EleID+'_'+'" + String.valueOf(dtl.getOID()) + "'+'_'+Tag1  WHERE RefPKVal=" + dbstr + "OldRefPKVal AND FK_MapData=" + dbstr + "FK_MapData";
                }
                paras.Add("RefPKVal", String.valueOf(dtl.getOID()), false);
                paras.Add("OldRefPKVal", refval, false);
                paras.Add("FK_MapData", fk_mapDtl, false);
                DBAccess.RunSQL(paras);
                //处理从表行数据插入成功后，更新FrmAttachmentDB中数据
                paras.SQL = "UPDATE Sys_FrmAttachmentDB SET RefPKVal=" + dbstr + "RefPKVal  WHERE RefPKVal=" + dbstr + "OldRefPKVal AND FK_MapData=" + dbstr + "FK_MapData";
                DBAccess.RunSQL(paras);
            }
            if (isVue3 == true) {
                //把从表中的子表RefPK=0的改成当前从表的OID
                MapDtls childDtls = new MapDtls(mdtl.getNo());
                for (MapDtl childDtl : childDtls.ToJavaList()) {
                    DBAccess.RunSQL("UPDATE " + childDtl.getPTable() + " SET RefPK='" + dtl.getOID() + "' WHERE RefPK='" + this.getRefPKVal() + "'");
                }
            }

        } else {
            dtl.Update();
        }

        ///#endregion 给实体循环赋值/并保存.


        ///#region 从表保存后处理事件。
        //页面定义的事件.
        if (!fes.isEmpty()) {
            String msg = fes.DoEventNode(EventListFrm.DtlRowSaveAfter, mainEn);
            if (DataType.IsNullOrEmpty(msg) == false) {
                return "err@" + msg;
            }
        }
        //事件实体类.
        if (mdtl.getFEBD().length() != 0) {
            String str = mdtl.getFEBD();
            bp.sys.base.FormEventBaseDtl febd = bp.sys.base.Glo.GetFormDtlEventBaseByEnName(mdtl.getNo());

            febd.HisEn = mdtl.GenerGEMainEntity(this.getRefPKVal());
            febd.HisEnDtl = dtl;
            febd.DoIt(EventListFrm.DtlRowSaveAfter, febd.HisEn, dtl, null);
        }

        ///#endregion 处理事件.

        //返回当前数据存储信息.
        return dtl.ToJson(true);
    }

    public final String Dtl_ChangePopAndAthIdx() {
        int oldRowIdx = GetRequestValInt("oldRowIdx");
        int newRowIdx = GetRequestValInt("newRowIdx");
        //dtl生成oid后，将pop弹出的FrmEleDB表中的数据用oid替换掉
        String refval = this.getWorkID() + "_" + oldRowIdx;
        String newRefVal = this.getWorkID() + "_" + newRowIdx;
        //处理从表行数据插入成功后，更新FrmEleDB中数据
        String dbstr = SystemConfig.getAppCenterDBVarStr();
        Paras paras = new Paras();
        if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle) {
            paras.SQL = "UPDATE Sys_FrmEleDB SET RefPKVal=" + dbstr + "RefPKVal,MyPK=EleID||'_'||'" + newRefVal + "'||'_'||Tag1  WHERE RefPKVal=" + dbstr + "OldRefPKVal";
        } else if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A) {
            paras.SQL = "UPDATE Sys_FrmEleDB SET RefPKVal=" + dbstr + "RefPKVal,MyPK=CONCAT(EleID,'_','" + newRefVal + "','_',Tag1)  WHERE RefPKVal=" + dbstr + "OldRefPKVal";
        } else {
            paras.SQL = "UPDATE Sys_FrmEleDB SET RefPKVal=" + dbstr + "RefPKVal,MyPK= EleID+'_'+'" + newRefVal + "'+'_'+Tag1  WHERE RefPKVal=" + dbstr + "OldRefPKVal";
        }
        paras.Add("RefPKVal", newRefVal, false);
        paras.Add("OldRefPKVal", refval, false);
        DBAccess.RunSQL(paras);
        //处理从表行数据插入成功后，更新FrmAttachmentDB中数据
        paras.SQL = "UPDATE Sys_FrmAttachmentDB SET RefPKVal=" + dbstr + "RefPKVal  WHERE RefPKVal=" + dbstr + "OldRefPKVal AND FK_MapData=" + dbstr + "FK_MapData";
        paras.Add("FK_MapData", this.getFrmID(), false);
        DBAccess.RunSQL(paras);
        return "执行成功";
    }

    /**
     * 删除
     *
     * @return
     */
    public final String Dtl_DeleteRow() throws Exception {
        GEDtl dtl = new GEDtl(this.getMapDtlNo());
        dtl.setOID(this.getRefOID());


        ///#region 从表 删除 前处理事件.
        //获得主表事件.
        FrmEvents fes = new FrmEvents(this.getMapDtlNo()); //获得事件.
        GEEntity mainEn = null;
        if (!fes.isEmpty()) {
            String msg = fes.DoEventNode(EventListFrm.DtlRowDelBefore, dtl);
            if (DataType.IsNullOrEmpty(msg) == false) {
                return "err@" + msg;
            }
        }

        MapDtl mdtl = new MapDtl(this.getMapDtlNo());
        if (mdtl.getFEBD().length() != 0) {
            String str = mdtl.getFEBD();
            bp.sys.base.FormEventBaseDtl febd = bp.sys.base.Glo.GetFormDtlEventBaseByEnName(mdtl.getNo());
            if (febd != null) {
                febd.HisEn = mdtl.GenerGEMainEntity(this.getRefPKVal());
                febd.HisEnDtl = dtl;
                febd.DoIt(EventListFrm.DtlRowDelBefore, febd.HisEn, dtl, null);
            }
        }

        ///#endregion 从表 删除 前处理事件.

        //执行删除.
        dtl.Delete();


        ///#region 从表 删除 后处理事件.
        //获得主表事件.
        fes = new FrmEvents(this.getMapDtlNo()); //获得事件.
        if (!fes.isEmpty()) {
            String msg = fes.DoEventNode(EventListFrm.DtlRowDelAfter, dtl);
            if (DataType.IsNullOrEmpty(msg) == false) {
                return "err@" + msg;
            }
        }

        if (mdtl.getFEBD().length() != 0) {
            String str = mdtl.getFEBD();
            bp.sys.base.FormEventBaseDtl febd = bp.sys.base.Glo.GetFormDtlEventBaseByEnName(mdtl.getNo());
            if (febd != null) {
                febd.HisEn = mdtl.GenerGEMainEntity(this.getRefPKVal());
                febd.HisEnDtl = dtl;

                febd.DoIt(EventListFrm.DtlRowDelAfter, febd.HisEn, dtl, null);
            }
        }


        ///#endregion 从表 删除 后处理事件.

        //如果有pop，删除相关存储
        FrmEleDBs FrmEleDBs = new FrmEleDBs();
        QueryObject qo = new QueryObject(FrmEleDBs);
        qo.AddWhere(FrmEleDBAttr.FK_MapData, this.getMapDtlNo());
        qo.addAnd();
        qo.AddWhere(FrmEleDBAttr.RefPKVal, String.valueOf(dtl.getOID()));
        qo.DoQuery();
        if (FrmEleDBs != null && !FrmEleDBs.isEmpty()) {
            for (FrmEleDB FrmEleDB : FrmEleDBs.ToJavaList()) {
                FrmEleDB.Delete();
            }
        }
        //如果可以上传附件这删除相应的附件信息
        FrmAttachmentDBs dbs = new FrmAttachmentDBs();
        dbs.Delete(FrmAttachmentDBAttr.FK_MapData, this.getMapDtlNo(), FrmAttachmentDBAttr.RefPKVal, this.getRefOID());
        return "删除成功";
    }

    /**
     * 重新获取单个ddl数据
     *
     * @return
     */
    public final String Dtl_ReloadDdl() {
        String Doc = this.GetRequestVal("Doc");
        DataTable dt = DBAccess.RunSQLReturnTable(Doc);
        dt.TableName = "ReloadDdl";
        return Json.ToJson(dt);
    }

    ///#endregion dtl.htm 从表.
    //#region DtlFrmGener
    public String DtlFrmGener_Init() throws Exception {
        DataSet myds = new DataSet();

        MapDtl dtl = new MapDtl(this.getEnsName());

        DataTable Sys_MapDtl = dtl.ToDataTableField("Sys_MapDtl");
        myds.Tables.add(Sys_MapDtl);

        //明细表的表单描述
        MapAttrs attrs = dtl.getMapAttrs();
        DataTable Sys_MapAttr = attrs.ToDataTableField("Sys_MapAttr");
        myds.Tables.add(Sys_MapAttr);

        //明细表的配置信息.
        MapExts mes = dtl.getMapExts();
        DataTable Sys_MapExt = mes.ToDataTableField("Sys_MapExt");
        myds.Tables.add(Sys_MapExt);
        //#region 把从表的- 外键表/枚举 加入 DataSet.

        MapExt me = null;
        DataTable ddlTable = new DataTable();
        ddlTable.Columns.Add("No");
        for (MapAttr attr : attrs.ToJavaList()) {

            //没有绑定外键
            String uiBindKey = attr.getUIBindKey();
            if (DataType.IsNullOrEmpty(uiBindKey) == true)
                continue;

            //#region 枚举字段
            if (attr.getLGType() == FieldTypeS.Enum) {
                // 如果是枚举值, 判断是否存在.
                if (myds.Tables.contains(uiBindKey) == true)
                    continue;
                String mysql = "SELECT IntKey AS No, Lab as Name FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey='" + uiBindKey + "' ORDER BY IntKey ";
                DataTable dtEnum = DBAccess.RunSQLReturnTable(mysql);
                dtEnum.TableName = uiBindKey;

                dtEnum.Columns.get(0).ColumnName = "No";
                dtEnum.Columns.get(1).ColumnName = "Name";

                myds.Tables.add(dtEnum);
                continue;
            }
            //#endregion

            // 检查是否有下拉框自动填充。
            String keyOfEn = attr.getKeyOfEn();

            //#region 处理下拉框数据范围. for 小杨.
            me = (MapExt) mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL,
                    MapExtAttr.AttrOfOper, keyOfEn);
            if (me != null && me.getDoWay().equals("0") == false && myds.Tables.contains(uiBindKey) == false) //是否存在.
            {
                String fullSQL = me.getDoc();
                fullSQL = fullSQL.replace("~", "'");
                fullSQL = bp.wf.Glo.DealExp(fullSQL, null);

                if (DataType.IsNullOrEmpty(fullSQL) == true)
                    throw new Exception("err@没有给AutoFullDLL配置SQL：MapExt：=" + me.getMyPK() + ",原始的配置SQL为:" + me.getDoc());

                DataTable dt = DBAccess.RunSQLReturnTable(fullSQL);

                if (uiBindKey.toLowerCase().equals("blank"))
                    dt.TableName = keyOfEn;
                else
                    dt.TableName = uiBindKey;

                if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase) {
                    if (dt.Columns.contains("NO") == true)
                        dt.Columns.get("NO").ColumnName = "No";
                    if (dt.Columns.contains("NAME") == true)
                        dt.Columns.get("NAME").ColumnName = "Name";
                    if (dt.Columns.contains("PARENTNO") == true)
                        dt.Columns.get("PARENTNO").ColumnName = "ParentNo";
                }

                if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase) {
                    if (dt.Columns.contains("no") == true)
                        dt.Columns.get("no").ColumnName = "No";
                    if (dt.Columns.contains("name") == true)
                        dt.Columns.get("name").ColumnName = "Name";
                    if (dt.Columns.contains("parentno") == true)
                        dt.Columns.get("parentno").ColumnName = "ParentNo";
                }

                myds.Tables.add(dt);
                continue;
            }
            //#endregion 处理下拉框数据范围.

            //#region 外键字段

            // 判断是否存在.
            if (myds.Tables.contains(uiBindKey) == true)
                continue;

            // 获得数据.
            DataTable mydt = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null);
            if (uiBindKey.toLowerCase().equals("blank"))
                mydt.TableName = keyOfEn;

            if (mydt == null) {
                DataRow ddldr = ddlTable.NewRow();
                ddldr.setValue("No", uiBindKey);
                ddlTable.Rows.add(ddldr);
            } else {
                myds.Tables.add(mydt);
            }
            //#endregion 外键字段
        }
        ddlTable.TableName = "UIBindKey";
        myds.Tables.add(ddlTable);
        //#endregion 把从表的- 外键表/枚举 加入 DataSet.

        //放入一个空白的实体，用与获取默认值.
        GEDtl dtlBlank = new GEDtl(dtl.getNo());
        dtlBlank.ResetDefaultVal();
        myds.Tables.add(dtlBlank.ToDataTableField("Blank"));

        return bp.tools.Json.ToJson(myds);
    }
    //#endregion DtlFrmGener

    public String DtlBatchUpdate_EleDB() throws Exception {
        String oids = this.GetRequestVal("OIDs");
        if (DataType.IsNullOrEmpty(oids) == true)
            return "err@参数明细表的集合OIDs不能为空";
        String keys = this.GetRequestVal("Keys");
        if (DataType.IsNullOrEmpty(keys) == false && keys.endsWith(",") == true) {
            keys = keys.substring(0, keys.length() - 1);
            keys = "('" + keys.replace(",", "','") + "')";
        }
        String[] strs = oids.trim().split(",");
        FrmEleDBs eleDBs = new FrmEleDBs();
        QueryObject qo = new QueryObject(eleDBs);
        qo.AddWhere("RefPKVal", strs[0]);
        qo.addAnd();
        qo.AddWhere("FK_MapData", this.getEnsName());
        qo.addAnd();
        qo.AddWhereIn("EleID", keys);
        qo.DoQuery();
        if (eleDBs.size() == 0)
            return "更新成功";
        FrmEleDB eleDB = null;
        for (int i = 1; i < strs.length; i++) {
            if (DataType.IsNullOrEmpty(strs[i]))
                continue;
            //删除其他OID的Sys_EleDBs
            String sql = "DELETE Sys_FrmEleDB WHERE FK_MapData='" + this.getEnsName() + "' AND RefPKVal='" + strs[i] + "' AND EleID IN" + keys;
            DBAccess.RunSQL(sql);
            for (FrmEleDB item : eleDBs.ToJavaList()) {
                eleDB = item;
                eleDB.setRefPKVal(strs[i]);
                eleDB.setMyPK(eleDB.getEleID() + "_" + eleDB.getRefPKVal() + "_" + eleDB.getTag1());
                eleDB.Insert();

            }
        }
        return bp.tools.Json.ToJson(eleDBs.ToDataTableField());
    }

    ///#region dtl.Card

    /**
     * 初始化
     *
     * @return
     */
    public final String DtlCard_Init() throws Exception {
        DataSet ds = new DataSet();

        MapDtl md = new MapDtl(this.getEnsName());
        if (this.getNodeID() != 0 && md.getFrmID().equals("Temp") == false && this.getNodeID() != 999999) {
            Node nd = new Node(this.getNodeID());
            /*如果
             * 1,传来节点ID, 不等于0.
             * 2,不是节点表单.  就要判断是否是独立表单，如果是就要处理权限方案。*/
            FrmNode fn = new FrmNode(nd.getNodeID(), this.getFrmID());
            Flow flow = new Flow(nd.getFlowNo());
            if (flow.getFlowDevModel() == FlowDevModel.JiJian || nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.RefOneFrmTree || nd.getHisFormType() == NodeFormType.FoolTruck) {
                /*如果
                 * 1,传来节点ID, 不等于0.
                 * 2,不是节点表单.  就要判断是否是独立表单，如果是就要处理权限方案。*/
                if (fn.getFrmSln() == FrmSln.Readonly) {
                    md.setItIsInsert(false);
                    md.setItIsDelete(false);
                    md.setItIsUpdate(false);
                    md.setItIsReadonly(true);
                }

                /**自定义权限.*/
                if (fn.getFrmSln() == FrmSln.Self) {
                    md.setNo(this.getEnsName() + "_" + this.getNodeID());
                    if (md.RetrieveFromDBSources() == 0) {
                        md.setNo(this.getEnsName());
                        /*如果设置了自定义方案，但是没有定义，从表属性，就需要去默认值. */
                    }
                }
            }
        }

        //主表数据.
        DataTable dt = md.ToDataTableField("Main");
        ds.Tables.add(dt);

        //主表字段.
        MapAttrs attrs = md.getMapAttrs();
        ds.Tables.add(attrs.ToDataTableField("MapAttrs"));

        MapExts exts = md.getMapExts();
        ds.Tables.add(exts.ToDataTableField("MapExts"));

        //从表.
        MapDtls dtls = md.getMapDtls();
        ds.Tables.add(dtls.ToDataTableField("MapDtls"));

        //从表的从表.
        for (MapDtl dtl : dtls.ToJavaList()) {
            MapAttrs subAttrs = new MapAttrs(dtl.getNo());
            ds.Tables.add(subAttrs.ToDataTableField(dtl.getNo()));
        }

        //从表的数据.
        //GEDtls enDtls = new GEDtls(this.EnsName);

        ///#region  把从表的数据放入.
        GEDtls enDtls = new GEDtls(md.getNo());
        QueryObject qo = null;
        try {
            qo = new QueryObject(enDtls);
            switch (md.getDtlOpenType()) {
                case ForEmp: // 按人员来控制.
                    qo.AddWhere(GEDtlAttr.RefPK, this.getRefPKVal());
                    qo.addAnd();
                    qo.AddWhere(GEDtlAttr.Rec, WebUser.getNo());
                    break;
                case ForWorkID: // 按工作ID来控制
                    qo.AddWhere(GEDtlAttr.RefPK, this.getRefPKVal());
                    break;
                case ForFID: // 按流程ID来控制.
                    if (this.getFID() == 0) {
                        qo.AddWhere(GEDtlAttr.FID, this.getRefPKVal());
                    } else {
                        qo.AddWhere(GEDtlAttr.FID, this.getFID());
                    }
                    break;
                case ForWorkIDAndSpecEmpNo:
                    qo.AddWhere(GEDtlAttr.RefPK, this.getRefPKVal());
                    qo.addAnd();
                    String attr = md.GetParaString("DtlOpenPara");
                    if (DataType.IsNullOrEmpty(attr) == true)
                        throw new Exception("err@当前数据显示规则按照ForWorkIDAndSpecEmpNo计算，但是您没有设置人员账号字段名称,");
                    qo.AddWhere(attr, WebUser.getNo());
                    break;
            }
        } catch (RuntimeException ex) {
            dtls.getNewEntity().CheckPhysicsTable();
            throw ex;
        }

        //条件过滤.
        if (DataType.IsNullOrEmpty(md.getFilterSQLExp()) == false) {
            String[] strs = md.getFilterSQLExp().split("[=]", -1);
            qo.addAnd();
            qo.AddWhere(strs[0], strs[1]);
        }

        //排序.
        if (DataType.IsNullOrEmpty(md.getOrderBySQLExp()) == false) {
            qo.addOrderBy(md.getOrderBySQLExp());
        } else {
            //增加排序.
            qo.addOrderBy(GEDtlAttr.OID);
        }


        //从表
        DataTable dtDtl = qo.DoQueryToTable();
        dtDtl.TableName = "DTDtls";
        ds.Tables.add(dtDtl);

        return Json.ToJson(ds);

    }

    public final String Dtl_InitCount() throws Exception {
        MapDtl mapDtl = new MapDtl(this.getEnsName());
        //实体.
        GEEntityOID en = new GEEntityOID(mapDtl.getFrmID());
        en.setOID(this.getWorkID());
        if (en.RetrieveFromDBSources() == 0)
            en.Insert();
        String dtlRefPKVal = bp.wf.Dev2Interface.GetDtlRefPKVal(this.getWorkID(), this.getPWorkID(), this.getFID(), this.getNodeID(), mapDtl.getFrmID(), mapDtl);
        if (dtlRefPKVal.equals("0") == true
                || DataType.IsNullOrEmpty(dtlRefPKVal))
            dtlRefPKVal = this.getRefPKVal();
        //获取初始化数据，不存在就创建
        return String.valueOf(bp.wf.CCFormAPI.GetDtlCount(mapDtl, en, dtlRefPKVal, false));
    }

    /**
     * 获得从表的从表数据
     *
     * @return
     */
    public final String DtlCard_Init_Dtl() throws Exception {
        DataSet ds = new DataSet();

        MapDtl md = new MapDtl(this.getEnsName());

        //主表数据.
        DataTable dt = md.ToDataTableField("Main");
        ds.Tables.add(dt);

        //主表字段.
        MapAttrs attrs = md.getMapAttrs();
        ds.Tables.add(attrs.ToDataTableField("MapAttrs"));

        GEDtls enDtls = new GEDtls(this.getEnsName());
        enDtls.Retrieve(GEDtlAttr.RefPK, this.getRefPKVal(), null);
        ds.Tables.add(enDtls.ToDataTableField("DTDtls"));

        return Json.ToJson(ds);
    }

    ///#endregion dtl.Card

    //#region 从表二维，三维显示
    /// <summary>
    /// 二维、三维的初始化
    /// </summary>
    /// <returns></returns>
    public String DtlRpt_Init() throws Exception {
        DataSet ds = new DataSet();
        MapDtl mapDtl = new MapDtl(this.getEnsName());
        ds.Tables.add(mapDtl.ToDataTableField("Sys_MapDtl"));

        int listModel = mapDtl.GetValIntByKey(MapDtlAttr.ListShowModel);
        //实体.
        GEEntityOID en = new GEEntityOID(mapDtl.getFrmID());
        en.setOID(this.getWorkID());
        if (en.RetrieveFromDBSources() == 0)
            en.Insert();
        //关联的RefPKVal
        String dtlRefPKVal = bp.wf.Dev2Interface.GetDtlRefPKVal(this.getWorkID(), this.getPWorkID(), this.getFID(), this.getNodeID(), mapDtl.getFrmID(), mapDtl);
        if (dtlRefPKVal.equals("0") == true
                || DataType.IsNullOrEmpty(dtlRefPKVal))
            dtlRefPKVal = this.getRefPKVal();
        //获取初始化数据，不存在就创建
        DataTable dtDtl = bp.wf.CCFormAPI.GetDtlInfo(mapDtl, en, dtlRefPKVal);
        //从表的字段集合
        MapAttrs mapAttrs = mapDtl.getMapAttrs();
        ds.Tables.add(mapAttrs.ToDataTableField("Sys_MapAttr"));
        //扩展属性
        MapExts mes = mapDtl.getMapExts();
        //#region 把从表的- 外键表/枚举 加入 DataSet.

        MapExt me = null;
        DataTable ddlTable = new DataTable();
        ddlTable.Columns.Add("No");
        for (MapAttr attr : mapAttrs.ToJavaList()) {
            //没有绑定外键
            String uiBindKey = attr.getUIBindKey();
            if (DataType.IsNullOrEmpty(uiBindKey) == true)
                continue;
            //#region 枚举字段
            if (attr.getLGType() == FieldTypeS.Enum) {
                // 如果是枚举值, 判断是否存在.
                if (ds.Tables.contains(uiBindKey) == true)
                    continue;

                SysEnums enums = new SysEnums();
                enums.Retrieve(SysEnumAttr.EnumKey, uiBindKey, SysEnumAttr.IntKey);
                DataTable dtEnum = new DataTable();
                dtEnum.TableName = uiBindKey;
                dtEnum.Columns.Add("No");
                dtEnum.Columns.Add("Name");
                DataRow dr = null;
                for (SysEnum sysEnum : enums.ToJavaList()) {
                    dr = dtEnum.NewRow();
                    if (DataType.IsNullOrEmpty(sysEnum.getStrKey()) == false)
                        dr.setValue(0, sysEnum.getStrKey());
                    else
                        dr.setValue(0, sysEnum.getIntKey());
                    dr.setValue(1, sysEnum.getLab());
                    dtEnum.Rows.add(dr);
                }
                ds.Tables.add(dtEnum);
                continue;
            }
            //#endregion

            // 检查是否有下拉框自动填充。
            String keyOfEn = attr.getKeyOfEn();

            //#region 处理下拉框数据范围
            me = (MapExt) mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL,
                    MapExtAttr.AttrOfOper, keyOfEn);
            if (me != null && me.getDoWay().equals("0") == false && ds.Tables.contains(uiBindKey) == false) //是否存在.
            {
                String fullSQL = me.getDoc();
                fullSQL = fullSQL.replace("~", "'");
                fullSQL = bp.wf.Glo.DealExp(fullSQL, en, null);

                if (DataType.IsNullOrEmpty(fullSQL) == true)
                    throw new Exception("err@没有给AutoFullDLL配置SQL：MapExt：=" + me.getMyPK() + ",原始的配置SQL为:" + me.getDoc());

                DataTable dt = DBAccess.RunSQLReturnTable(fullSQL);

                if (uiBindKey.toLowerCase().equals("blank"))
                    dt.TableName = keyOfEn;
                else
                    dt.TableName = uiBindKey;

                if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase) {
                    if (dt.Columns.contains("NO") == true)
                        dt.Columns.get("NO").ColumnName = "No";
                    if (dt.Columns.contains("NAME") == true)
                        dt.Columns.get("NAME").ColumnName = "Name";
                    if (dt.Columns.contains("PARENTNO") == true)
                        dt.Columns.get("PARENTNO").ColumnName = "ParentNo";
                }

                if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase) {
                    if (dt.Columns.contains("no") == true)
                        dt.Columns.get("no").ColumnName = "No";
                    if (dt.Columns.contains("name") == true)
                        dt.Columns.get("name").ColumnName = "Name";
                    if (dt.Columns.contains("parentno") == true)
                        dt.Columns.get("parentno").ColumnName = "ParentNo";
                }

                ds.Tables.add(dt);
                continue;
            }
            //#endregion 处理下拉框数据范围.

            //#region 外键字段

            // 判断是否存在.
            if (ds.Tables.contains(uiBindKey) == true)
                continue;

            // 获得数据.
            DataTable mydt = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, en.getRow());
            if (uiBindKey.toLowerCase().equals("blank"))
                mydt.TableName = keyOfEn;
            if (mydt == null) {
                DataRow ddldr = ddlTable.NewRow();
                ddldr.setValue("No", uiBindKey);
                ddlTable.Rows.add(ddldr);
            } else {
                ds.Tables.add(mydt);
            }
            //#endregion 外键字段
        }
        ddlTable.TableName = "UIBindKey";
        ds.Tables.add(ddlTable);
        //#endregion
        String d1 = mapDtl.GetParaString("D1");
        if (DataType.IsNullOrEmpty(d1) == true)
            return "err@明细表[" + mapDtl.getName() + "]没有配置维度1字段";
        MapAttr mapAttr = (MapAttr) mapAttrs.GetEntityByKey(MapAttrAttr.KeyOfEn, d1);
        //维度1字段为文本字段时
        if (mapAttr.getMyDataType() == DataType.AppString && mapAttr.getUIContralType() == UIContralType.TB && mapAttr.getLGType() == FieldTypeS.Normal) {
            dtDtl.TableName = "DBDtl";
            ds.Tables.add(dtDtl);
            return bp.tools.Json.ToJson(ds);
        }

        //如果从表数据不存在，则创建二维，三维数据
        if (dtDtl.Rows.size() == 0) {
            GEDtls dtls = new GEDtls(mapDtl.getNo());
            GEDtl endtl = null;

            String d2 = mapDtl.GetParaString("D2");
            if (DataType.IsNullOrEmpty(d2) == true)
                return "err@明细表[" + mapDtl.getName() + "]没有配置维度2字段";

            DataTable dt = ds.GetTableByName(mapAttr.getUIBindKey());
            if (dt == null || dt.Rows.size() == 0)
                return "err@维度字段[" + mapAttr.getName() + "]没有对应的外键或者枚举集合";
            MapAttr mapAttr2 = (MapAttr) mapAttrs.GetEntityByKey(MapAttrAttr.KeyOfEn, d2);
            DataTable dt2 = ds.GetTableByName(mapAttr2.getUIBindKey());
            if (dt2 == null || dt2.Rows.size() == 0)
                return "err@维度字段[" + mapAttr2.getName() + "]没有对应的外键或者枚举集合";
            //二维
            if (listModel == 3) {
                int idx = 0;
                for (DataRow dr : dt.Rows) {
                    for (DataRow dr2 : dt2.Rows) {
                        endtl = new GEDtl(mapDtl.getNo());
                        endtl.ResetDefaultVal();
                        endtl.SetValByKey(mapAttr.getKeyOfEn(), dr.getValue(0));
                        if (mapAttr.getUIContralType() == UIContralType.DDL && mapAttr.getLGType() == FieldTypeS.Normal)
                            endtl.SetValByKey(mapAttr.getKeyOfEn() + 'T', dr.getValue(1));

                        endtl.SetValByKey(mapAttr2.getKeyOfEn(), dr2.getValue(0));
                        if (mapAttr2.getUIContralType() == UIContralType.DDL && mapAttr2.getLGType() == FieldTypeS.Normal)
                            endtl.SetValByKey(mapAttr2.getKeyOfEn() + 'T', dr2.getValue(1));
                        endtl.setRefPK(dtlRefPKVal);
                        endtl.setFID(this.getFID());
                        endtl.SetValByKey("Idx", idx);
                        endtl.SetValByKey("IsEnable", 1);
                        endtl.Insert();
                        dtls.AddEntity(endtl);
                        idx++;
                    }
                }
                dtDtl = dtls.ToDataTableField();
            }
            if (listModel == 4 || listModel == 5) {
                String d3 = mapDtl.GetParaString("D3");
                if (DataType.IsNullOrEmpty(d3) == true)
                    return "err@明细表[" + mapDtl.getName() + "]没有配置维度3字段";
                MapAttr mapAttr3 = (MapAttr) mapAttrs.GetEntityByKey(MapAttrAttr.KeyOfEn, d3);
                DataTable dt3 = ds.GetTableByName(mapAttr3.getUIBindKey());
                if (dt3 == null || dt3.Rows.size() == 0)
                    return "err@维度字段[" + mapAttr3.getName() + "]没有对应的外键或者枚举集合";
                int idx = 0;
                for (DataRow dr : dt.Rows) {
                    for (DataRow dr2 : dt2.Rows) {
                        for (DataRow dr3 : dt3.Rows) {
                            endtl = new GEDtl(mapDtl.getNo());
                            endtl.ResetDefaultVal();
                            endtl.SetValByKey(mapAttr.getKeyOfEn(), dr.getValue(0));
                            if (mapAttr.getUIContralType() == UIContralType.DDL && mapAttr.getLGType() == FieldTypeS.Normal)
                                endtl.SetValByKey(mapAttr.getKeyOfEn() + 'T', dr.getValue(1));

                            endtl.SetValByKey(mapAttr2.getKeyOfEn(), dr2.getValue(0));
                            if (mapAttr2.getUIContralType() == UIContralType.DDL && mapAttr2.getLGType() == FieldTypeS.Normal)
                                endtl.SetValByKey(mapAttr2.getKeyOfEn() + 'T', dr2.getValue(1));

                            endtl.SetValByKey(mapAttr3.getKeyOfEn(), dr3.getValue(0));
                            if (mapAttr3.getUIContralType() == UIContralType.DDL && mapAttr3.getLGType() == FieldTypeS.Normal)
                                endtl.SetValByKey(mapAttr3.getKeyOfEn() + 'T', dr3.getValue(1));

                            endtl.setRefPK(dtlRefPKVal);
                            endtl.setFID(this.getFID());
                            endtl.SetValByKey("Idx", idx);
                            endtl.SetValByKey("IsEnable", 1);
                            endtl.Insert();
                            dtls.AddEntity(endtl);
                            idx++;
                        }
                    }
                }
                dtDtl = dtls.ToDataTableField();
            }
        }

        dtDtl.TableName = "DBDtl";
        ds.Tables.add(dtDtl);
        return bp.tools.Json.ToJson(ds);

    }
    //#endregion 从表二维，三维显示
    ///#region 保存手写签名图片

    /**
     * 保存手写签名图片
     *
     * @return 返回保存结果
     */
    public final String HandWriting_Save() {
        try {
            String basePath = SystemConfig.getPathOfDataUser() + "HandWritingImg";
            String ny = DataType.getCurrentDateByFormart("yyyy_MM");
            String tempPath = basePath + "/" + ny + "/" + this.getFK_Node() + "/";
            String tempName = this.getWorkID() + "_" + this.getKeyOfEn() + "_"
                    + DataType.getCurrentDateByFormart("HHmmss") + ".png";
            File file = new File(tempPath);
            if (file.exists() == false)
                file.mkdirs();
            // 删除改目录下WORKID的文件
            String[] files = file.list();
            for (String f : files) {
                if (f.contains(this.getWorkID() + "_" + this.getKeyOfEn()) == true)
                    new File(f).delete();
            }

            String pic_Path = tempPath + tempName;

            String imgData = this.GetValFromFrmByKey("imageData");
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] imgByte = decoder.decode(imgData);
            InputStream in = new ByteArrayInputStream(imgByte);
            byte[] b = new byte[1024];
            int nRead = 0;

            OutputStream o = new FileOutputStream(pic_Path);

            while ((nRead = in.read(b)) != -1) {
                o.write(b, 0, nRead);
            }

            o.flush();
            o.close();

            in.close();

            return "info@" + pic_Path.replace("\\", "/");
        } catch (Exception e) {
            return "err@" + e.getMessage();
        }
    }

    /**
     * 图片转二进制流
     *
     * @return
     */
    public String ImageDatabytes() throws Exception {
        String FilePath = SystemConfig.getPathOfDataUser() + GetRequestVal("src");
        File file = new File(FilePath);
        if (file.exists() == false)
            return "";

        BufferedImage bi;
        Base64.Encoder encoder = Base64.getEncoder();
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            /*Base64.Encoder encoder = Base64.getEncoder();
            String imgParam = encoder.encodeToString(bytes);

            bi = ImageIO.read(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", baos);  //经测试转换的图片是格式这里就什么格式，否则会失真

            byte[] bytes = baos.toByteArray();
            */
            return encoder.encodeToString(bytes).trim();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
        return null;

    }

    /**
     * 处理SQL的表达式.
     *
     * @param exp 表达式
     * @return 从from里面替换的表达式.
     */
    public final String DealExpByFromVals(String exp) {
        for (String strKey : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
            if (exp.contains("@") == false) {
                return exp;
            }
            String str = strKey.replace("TB_", "").replace("CB_", "").replace("DDL_", "").replace("RB_", "");

            exp = exp.replace("@" + str, this.GetRequestVal(strKey));
        }
        return exp;
    }


    //单附件上传方法
    private void SingleAttach(String attachPk, long workid, long fid, int fk_node, String ensName) throws Exception {
        FrmAttachment frmAth = new FrmAttachment();
        frmAth.setMyPK(attachPk);
        frmAth.RetrieveFromDBSources();

        String athDBPK = attachPk + "_" + workid;

        Node currND = new Node(fk_node);
        Work currWK = currND.getHisWork();
        currWK.setOID(workid);
        currWK.Retrieve();
        //处理保存路径.
        String saveTo = frmAth.getSaveTo();

        if (saveTo.contains("*") || saveTo.contains("@")) {
            /*如果路径里有变量.*/
            saveTo = saveTo.replace("*", "@");
            saveTo = Glo.DealExp(saveTo, currWK, null);
        }

        try {
            saveTo = SystemConfig.getPathOfWebApp() + saveTo; //context.Server.MapPath("~/" + saveTo);
        } catch (Exception e) {
            //saveTo = saveTo;
        }

        if ((new File(saveTo)).isDirectory() == false) {
            (new File(saveTo)).mkdirs();
        }
        HttpServletRequest request = ContextHolderUtils.getRequest();
        String fileName = CommonFileUtils.getOriginalFilename(request, "file");
        if (fileName.indexOf("\\") > -1) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        }
        if (fileName.indexOf("/") > -1) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }
        String exts = FileAccess.getExtensionName(fileName).toLowerCase().replace(".", "");
        saveTo = saveTo + "/" + athDBPK + "." + fileName.substring(fileName.lastIndexOf('.') + 1);
        File info = new File(saveTo);
        CommonFileUtils.upload(request, "file", info);

        FrmAttachmentDB dbUpload = new FrmAttachmentDB();
        dbUpload.setMyPK(athDBPK);
        dbUpload.setFKFrmAttachment(attachPk);
        dbUpload.setRefPKVal(String.valueOf(this.getWorkID()));
        dbUpload.setFID(fid);
        dbUpload.setFrmID(ensName);

        dbUpload.setFileExts(exts);


        ///#region 处理文件路径，如果是保存到数据库，就存储pk.
        if (frmAth.getAthSaveWay() == AthSaveWay.IISServer) {
            //文件方式保存
            dbUpload.setFileFullName(saveTo);
        }

        if (frmAth.getAthSaveWay() == AthSaveWay.DB) {
            //保存到数据库
            dbUpload.setFileFullName(dbUpload.getMyPK());
        }

        ///#endregion 处理文件路径，如果是保存到数据库，就存储pk.


        dbUpload.setFileName(fileName);
        dbUpload.setFileSize((float) info.length());
        dbUpload.setRec(WebUser.getNo());
        dbUpload.setRecName(WebUser.getName());
        dbUpload.setDeptNo(WebUser.getDeptNo());
        dbUpload.setDeptName(WebUser.getDeptName());
        dbUpload.setRDT(DataType.getCurrentDateTime());

        dbUpload.setNodeID(fk_node);
        dbUpload.Save();

        if (frmAth.getAthSaveWay() == AthSaveWay.DB) {
            //执行文件保存.
            DBAccess.SaveFileToDB(saveTo, dbUpload.getEnMap().getPhysicsTable(), "MyPK", dbUpload.getMyPK(), "FDB");
        }
    }

    //多附件上传方法
    public final String MoreAttach() throws Exception {

        String empNo = this.GetRequestVal("UserNo");
        String orgNo = this.GetRequestVal("OrgNo");
        if (DataType.IsNullOrEmpty(empNo) == false) {
            if (StringUtils.isEmpty(orgNo)) {
                Dev2Interface.Port_Login(empNo);
            } else {
                Dev2Interface.Port_Login(empNo, orgNo);
            }
        }

        String uploadFileM = ""; //上传附件数据的MyPK,用逗号分开
        String pkVal = this.GetRequestVal("PKVal");
        String attachPk = this.GetRequestVal("AttachPK");
        String paras = this.GetRequestVal("parasData");
        String sort = this.GetRequestVal("Sort");
        String latitude = this.GetRequestVal("Latitude");
        String longitude = this.GetRequestVal("Longitude");
        String direction = this.GetRequestVal("Direction");
        //获取sort
        if (DataType.IsNullOrEmpty(sort)) {
            if (paras != null && paras.length() > 0) {
                for (String para : paras.split("[@]", -1)) {
                    if (para.indexOf("Sort") != -1) {
                        sort = para.split("[=]", -1)[1];
                    }
                }
            }
        }
        // 多附件描述.
        FrmAttachment athDesc = new FrmAttachment(attachPk);
        MapData mapData = null;
        GEEntity en =null;
        String msg = "";
        if(athDesc.getFrmID().startsWith("TS.")==true)
            pkVal = this.getPKVal();
        else{
            mapData= new MapData(athDesc.getFrmID());
            //求出来实体记录，方便执行事件.
            en = new GEEntity(athDesc.getFrmID());
            en.setPKVal(pkVal);
            if (en.RetrieveFromDBSources() == 0) {
                en.setPKVal(this.getFID());
                if (en.RetrieveFromDBSources() == 0) {
                    en.setPKVal(this.getPWorkID());
                    en.RetrieveFromDBSources();
                }
            }

            //求主键. 如果该表单挂接到流程上.
            if (this.getNodeID() != 0 && !(athDesc.getNoOfObj().contains("AthMDtl") == true || athDesc.GetParaBoolen("IsDtlAth") == true)) {
                //判断表单方案。
                FrmNode fn = new FrmNode(this.getNodeID(), this.getFrmID());
                if (fn.getFrmSln() == FrmSln.Self) {
                    FrmAttachment myathDesc = new FrmAttachment();
                    myathDesc.setMyPK(attachPk + "_" + this.getNodeID());
                    if (myathDesc.RetrieveFromDBSources() != 0) {
                        athDesc.setHisCtrlWay(myathDesc.getHisCtrlWay());
                    }
                }
                pkVal = Dev2Interface.GetAthRefPKVal(this.getWorkID(), this.getPWorkID(), this.getFID(), this.getNodeID(), this.getFrmID(), athDesc);
            }
        }



        bp.da.Log.DefaultLogWriteLine(LogType.Info, "开始上传");


        //获取上传文件是否需要加密
        boolean fileEncrypt = SystemConfig.isEnableAthEncrypt();
        HttpServletRequest request = ContextHolderUtils.getRequest();
        String contentType = request.getContentType();
        MultipartHttpServletRequest multipartRequest = null;
        if (contentType != null && contentType.indexOf("multipart/form-data") != -1)
            multipartRequest = CommonFileUtils.getMultipartHttpServletRequest(request);
        if (multipartRequest == null)
            return "err@请求出错";
        List<MultipartFile> items = multipartRequest.getFiles("file");
        bp.da.Log.DefaultLogWriteLine(LogType.Info, "文件信息MultipartFile:" + items.size());
        for (MultipartFile item : items) {
            String fileName = item.getOriginalFilename();
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "文件信息:" + fileName + "," + item.getSize());
            if (fileName.indexOf("\\") > -1) {
                fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
            }
            if (fileName.indexOf("/") > -1) {
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            }
            fileName = fileName.replace(" ", "");
            // 扩展名
            String exts = FileAccess.getExtensionName(fileName).toLowerCase().replace(".", "");

            ///#region 文件上传的iis服务器上 or db数据库里.
            if (athDesc.getAthSaveWay() == AthSaveWay.IISServer) {
                String savePath = athDesc.getSaveTo();
                if (savePath.contains("@") == true || savePath.contains("*") == true) {
                    //*如果有变量*//*
                    savePath = savePath.replace("*", "@");

                    if (savePath.contains("@") && this.getNodeID() != 0) {
                        //*如果包含 @ *//*
                        Flow flow = new Flow(this.getFlowNo());
                        GERpt myen = flow.getHisGERpt();
                        myen.setOID(this.getWorkID());
                        myen.RetrieveFromDBSources();
                        savePath = Glo.DealExp(savePath, myen, null);
                    }
                    if (savePath.contains("@") == true) {
                        throw new RuntimeException("@路径配置错误,变量没有被正确的替换下来." + savePath);
                    }
                } else {
                    savePath = athDesc.getSaveTo() + "/" + pkVal;
                }

                // 替换关键的字串.
                savePath = savePath.replace("\\\\", "/");

                bp.da.Log.DefaultLogWriteLine(LogType.Info, "savePath:" + savePath);
                try {
//                    if (savePath.indexOf(":") == -1 && SystemConfig.isJarRun() == false)
//                        savePath = ContextHolderUtils.getRequest().getSession().getServletContext().getRealPath(savePath);


                    File fileInfo = new File(savePath);

                    if (fileInfo.exists() == false)
                        fileInfo.mkdirs();

                } catch (Exception ex) {
                    throw new RuntimeException("@创建路径出现错误，可能是没有权限或者路径配置有问题:"
                            + ContextHolderUtils.getRequest().getSession().getServletContext().getRealPath("~/" + savePath)
                            + "===" + savePath + "@技术问题:" + ex.getMessage());

                }

                String guid = DBAccess.GenerGUID(0, null, null);


//                String realSaveTo = savePath + "/" + guid + "." + fileName;   //带有原上传文件名
                String realSaveTo = savePath + "/" + guid + "." + exts;         //随机文件名称

                realSaveTo = realSaveTo.replace("~", "-");
                realSaveTo = realSaveTo.replace("'", "-");
                realSaveTo = realSaveTo.replace("*", "-");
                String saveTo = realSaveTo;
                if (fileEncrypt == true)
                    saveTo = realSaveTo + ".tmp";
                File file = new File(saveTo); // 获取根目录对应的真实物理路径

                try {
                    // 构造临时对象
                    InputStream is = item.getInputStream();
                    int buffer = 1024; // 定义缓冲区的大小
                    int length = 0;
                    byte[] b = new byte[buffer];
                    FileOutputStream fos = new FileOutputStream(file);
                    while ((length = is.read(b)) != -1) {
                        // 计算上传文件的百分比
                        fos.write(b, 0, length); // 向文件输出流写读取的数据
                    }
                    fos.close();
                } catch (RuntimeException ex) {

                    throw new RuntimeException("@文件存储失败,有可能是路径的表达式出问题,导致是非法的路径名称:" + ex.getMessage());
                }

                if (fileEncrypt == true) {
                    File fileT = new File(saveTo);
                    AesEncodeUtil.encryptFile(saveTo, realSaveTo);
                    fileT.delete();//删除临时文件
                }

                //执行附件上传前事件，added by liuxc,2017-7-15
                if(mapData!=null)
                    msg = ExecEvent.DoFrm(mapData, EventListFrm.AthUploadeBefore, en, "@FK_FrmAttachment=" + athDesc.getMyPK() + "@FileFullName=" + realSaveTo);
                if (!DataType.IsNullOrEmpty(msg)) {
                    bp.sys.base.Glo.WriteLineError("@AthUploadeBefore事件返回信息，文件：" + file.getName() + "，" + msg);

                    try {
                        (new File(realSaveTo)).delete();
                    } catch (Exception e) {
                    }
                }

                File info = new File(realSaveTo);
                FrmAttachmentDB dbUpload = new FrmAttachmentDB();
                dbUpload.setMyPK(guid); // athDesc.FrmID + oid.ToString();
                dbUpload.setNodeID(this.getNodeID());
                dbUpload.setSort(sort);
                dbUpload.setFrmID(athDesc.getFrmID());
                dbUpload.setFKFrmAttachment(attachPk);
                dbUpload.setFileExts(exts);
                dbUpload.setFID(this.getFID());
                if (fileEncrypt == true) {
                    dbUpload.SetPara("IsEncrypt", 1);
                }

                if (athDesc.getItIsExpCol() == true) {
                    if (paras != null && paras.length() > 0) {
                        for (String para : paras.split("[@]", -1)) {
                            dbUpload.SetPara(para.split("[=]", -1)[0], para.split("[=]", -1)[1]);
                        }
                    }
                }


                ///#region 处理文件路径，如果是保存到数据库，就存储pk.
                if (athDesc.getAthSaveWay() == AthSaveWay.IISServer) {
                    //文件方式保存
                    dbUpload.setFileFullName(realSaveTo);
                }

                if (athDesc.getAthSaveWay() == AthSaveWay.FTPServer) {
                    //保存到数据库
                    dbUpload.setFileFullName(dbUpload.getMyPK());
                }

                ///#endregion 处理文件路径，如果是保存到数据库，就存储pk.

                dbUpload.setFileName(fileName);
                dbUpload.setFileSize((float) info.length());
                dbUpload.setRDT(DataType.getCurrentDateTimess());
                dbUpload.setRec(WebUser.getNo());
                dbUpload.setRecName(WebUser.getName());
                dbUpload.setDeptNo(WebUser.getDeptNo());
                dbUpload.setDeptName(WebUser.getDeptName());
                dbUpload.setRefPKVal(pkVal);
                dbUpload.setFID(this.getFID());

                dbUpload.setUploadGUID(guid);

                dbUpload.SetPara("Latitude", latitude);
                dbUpload.SetPara("Longitude", longitude);
                dbUpload.SetPara("Direction", direction);
                dbUpload.Insert();
                uploadFileM += dbUpload.getMyPK() + ",";

                bp.da.Log.DefaultLogWriteLine(LogType.Info, "数据库增加记录成功:" + dbUpload.ToJson(true));

                //bp.da.Log.DefaultLogWriteLine(LogType.Info, "数据库增加记录成功");

                if (athDesc.getAthSaveWay() == AthSaveWay.DB) {
                    //执行文件保存.
                    DBAccess.SaveFileToDB(realSaveTo, dbUpload.getEnMap().getPhysicsTable(), "MyPK", dbUpload.getMyPK(), "FDB");
                }

                //执行附件上传后事件，added by liuxc,2017-7-15
                if(mapData!=null)
                    msg = ExecEvent.DoFrm(mapData, EventListFrm.AthUploadeAfter, en, "@FK_FrmAttachment=" + dbUpload.getFKFrmAttachment() + "@FK_FrmAttachmentDB=" + dbUpload.getMyPK() + "@FileFullName=" + dbUpload.getFileFullName());
                if (!DataType.IsNullOrEmpty(msg)) {
                    bp.sys.base.Glo.WriteLineError("@AthUploadeAfter事件返回信息，文件：" + dbUpload.getFileName() + "，" + msg);
                }
            }

            ///#endregion 文件上传的iis服务器上 or db数据库里.
            if(athDesc.getAthSaveWay() == AthSaveWay.Minio){
                String savePath = athDesc.getSaveTo()+"/"+pkVal+"/";
                // 替换关键的字串.
                savePath = savePath.replace("\\\\", "/");

                String  previewUrl=MinioHelper.putObject(item,savePath);
                String guid=DBAccess.GenerGUID();
                FrmAttachmentDB dbUpload = new FrmAttachmentDB();
                dbUpload.setMyPK(guid); // athDesc.FrmID + oid.ToString();
                dbUpload.setNodeID(this.getNodeID());
                dbUpload.setSort(sort);
                dbUpload.setFrmID(athDesc.getFrmID());
                dbUpload.setFKFrmAttachment(attachPk);
                dbUpload.setFileExts(exts);
                dbUpload.setFID(this.getFID());
                dbUpload.setFileFullName(previewUrl);

                dbUpload.setFileName(fileName);
                dbUpload.setFileSize(item.getSize());
                dbUpload.setRDT(DataType.getCurrentDateTimess());
                dbUpload.setRec(WebUser.getNo());
                dbUpload.setRecName(WebUser.getName());
                dbUpload.setDeptNo(WebUser.getDeptNo());
                dbUpload.setDeptName(WebUser.getDeptName());
                dbUpload.setRefPKVal(pkVal);
                dbUpload.setFID(this.getFID());
                dbUpload.setUploadGUID(guid);
                dbUpload.SetPara("Latitude", latitude);
                dbUpload.SetPara("Longitude", longitude);
                dbUpload.SetPara("Direction", direction);
                dbUpload.SetPara("ContentType", item.getContentType());
                dbUpload.Insert();
            }

            ///#region 保存到数据库 / FTP服务器上 / OSS服务器上.
            if (athDesc.getAthSaveWay() == AthSaveWay.DB || athDesc.getAthSaveWay() == AthSaveWay.FTPServer || athDesc.getAthSaveWay() == AthSaveWay.OSS) {
                String guid = DBAccess.GenerGUID(0, null, null);

                //把文件临时保存到一个位置.
                String temp = SystemConfig.getPathOfTemp() + "" + guid + ".tmp";

                String tempD = temp;
                if (fileEncrypt == true)
                    tempD = SystemConfig.getPathOfTemp() + "/" + "" + guid + "_Desc" + ".tmp";
                File tempFile = new File(tempD);
                InputStream is = null;
                try {
                    // 构造临时对象
                    is = item.getInputStream();
                    int buffer = 1024; // 定义缓冲区的大小
                    int length = 0;
                    byte[] b = new byte[buffer];
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    while ((length = is.read(b)) != -1) {
                        fos.write(b, 0, length); // 向文件输出流写读取的数据
                    }
                    fos.close();
                    is.close();
                } catch (Exception ex) {
                    tempFile.delete();
                    throw new RuntimeException("@文件存储失败,有可能是路径的表达式出问题,导致是非法的路径名称:" + ex.getMessage());

                }
                if (fileEncrypt == true) {
                    File fileTD = new File(tempD);
                    AesEncodeUtil.encryptFile(tempD, temp);//加密
                    fileTD.delete();//删除临时文件
                }

                // 执行附件上传前事件，added by liuxc,2017-7-15
                if(mapData!=null)
                    msg = ExecEvent.DoFrm(mapData, EventListFrm.AthUploadeBefore, en, "@FK_FrmAttachment=" + athDesc.getMyPK() + "@FileFullName=" + temp);

                if (DataType.IsNullOrEmpty(msg) == false) {
                    bp.sys.base.Glo.WriteLineError("@AthUploadeBefore事件返回信息，文件：" + fileName + "，" + msg);

                    tempFile.delete();

                    throw new Exception("err@上传附件错误：" + msg);
                }
                File info = new File(temp);
                FrmAttachmentDB dbUpload = new FrmAttachmentDB();
                dbUpload.setMyPK(DBAccess.GenerGUID(0, null, null));
                dbUpload.setSort(sort);
                dbUpload.setNodeID(this.getNodeID());
                dbUpload.setFrmID(athDesc.getFrmID());
                dbUpload.setFKFrmAttachment(athDesc.getMyPK());
                dbUpload.setFID(this.getFID()); //流程id.
                if (fileEncrypt == true) {
                    dbUpload.SetPara("IsEncrypt", 1);
                }

                dbUpload.setRefPKVal(pkVal.toString());
                dbUpload.setFKFrmAttachment(athDesc.getMyPK());
                dbUpload.setFileName(fileName);
                dbUpload.setFileSize((float) info.length());
                dbUpload.setRDT(DataType.getCurrentDateTimess());
                dbUpload.setRec(WebUser.getNo());
                dbUpload.setRecName(WebUser.getName());
                dbUpload.setDeptNo(WebUser.getDeptNo());
                dbUpload.setDeptName(WebUser.getDeptName());
                if (athDesc.getItIsExpCol() == true) {
                    if (paras != null && paras.length() > 0) {
                        for (String para : paras.split("[@]", -1)) {
                            dbUpload.SetPara(para.split("[=]", -1)[0], para.split("[=]", -1)[1]);
                        }
                    }
                }

                dbUpload.setUploadGUID(guid);

                if (athDesc.getAthSaveWay() == AthSaveWay.DB) {
                    dbUpload.Insert();
                    //把文件保存到指定的字段里.
                    dbUpload.SaveFileToDB("FileDB", temp);
                }

                if (athDesc.getAthSaveWay() == AthSaveWay.FTPServer) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
                    String ny = sdf.format(new Date());

                    String workDir = ny + "/" + athDesc.getFrmID() + "/";

                    //特殊处理文件路径.
                    if (SystemConfig.getCustomerNo().equals("BWDA")) {

                        sdf = new SimpleDateFormat("yyyy_MM_dd");
                        ny = sdf.format(new Date());

                        ny = ny.replace("_", "/");
                        ny = ny.replace("_", "/");

                        workDir = ny + "/" + WebUser.getNo() + "/";
                    }

                    boolean isOK = false;

                    if (SystemConfig.getFTPServerType().equals("FTP")) {

                        FtpUtil ftpUtil = bp.wf.Glo.getFtpUtil();
                        ftpUtil.openConnection();
                        ftpUtil.changeWorkingDirectory(workDir, true);

                        // 把文件放在FTP服务器上去.
                        isOK = ftpUtil.uploadFile(guid + "." + dbUpload.getFileExts(), temp);

                        ftpUtil.releaseConnection();
                    }

                    if (SystemConfig.getFTPServerType().equals("SFTP")) {

                        SftpUtil ftpUtil = bp.wf.Glo.getSftpUtil();

                        ftpUtil.changeWorkingDirectory(workDir, true);
                        // 把文件放在FTP服务器上去.
                        isOK = ftpUtil.uploadFile(guid + "." + dbUpload.getFileExts(), temp);
                        ftpUtil.releaseConnection();
                    }
                    // 设置路径.
                    dbUpload.setFileFullName(workDir + guid + "." + dbUpload.getFileExts());
                    // 删除临时文件
                    tempFile.delete();
                    new File(SystemConfig.getPathOfTemp() + "" + guid + "_Desc" + ".tmp").delete();

                    if (isOK == false)
                        throw new RuntimeException("err文件上传失败，请检查ftp服务器配置信息");

                    dbUpload.Insert();

                }

                //#region 文件上传到OSS服务器上
                if (athDesc.getAthSaveWay() == AthSaveWay.OSS) {
                    String subDir = "";
                    //判断是否有子目录路径
                    if (DataType.IsNullOrEmpty(bp.difference.SystemConfig.getBucketSubPath()) == false && "/".equals(bp.difference.SystemConfig.getBucketSubPath()) == false) {
                        subDir = bp.difference.SystemConfig.getBucketSubPath() + "/";
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
                    String ny = sdf.format(new Date());

                    String workDir = subDir + ny + File.separator + athDesc.getFrmID() + File.separator;
                    // 构造临时对象
                    InputStream inputStream = item.getInputStream();

                    String fileNameU = workDir + guid + "." + dbUpload.getFileExts();

                    OSSUploadFileUtils.uploadFile(fileNameU, inputStream);

                    dbUpload.setFileFullName(fileNameU);

                    // 设置路径.
                    dbUpload.setFileFullName(workDir + guid + "." + dbUpload.getFileExts());

                    // 删除临时文件
                    tempFile.delete();
                    new File(SystemConfig.getPathOfTemp() + guid + "_Desc" + ".tmp").delete();
                    dbUpload.SetPara("Latitude", latitude);
                    dbUpload.SetPara("Longitude", longitude);
                    dbUpload.SetPara("Direction", direction);
                    dbUpload.Insert();
                }
                //#endregion 文件上传到OSS服务器上

                // 执行附件上传后事件，added by liuxc,2017-7-15
                if(mapData!=null)
                    msg = ExecEvent.DoFrm(mapData, EventListFrm.AthUploadeAfter, en, "@FK_FrmAttachment=" + dbUpload.getFKFrmAttachment() + "@FK_FrmAttachmentDB=" + dbUpload.getMyPK()
                        + "@FileFullName=" + temp);

                if (DataType.IsNullOrEmpty(msg) == false)
                    bp.sys.base.Glo.WriteLineError("@AthUploadeAfter事件返回信息，文件：" + dbUpload.getFileName() + "，" + msg);

            }

            ///#endregion 保存到数据库.

        }
        //需要判断是否存在AthNum字段
        if (en!=null && en.getRow().get("AthNum") != null) {
            int athNum = Integer.parseInt(en.getRow().get("AthNum").toString());
            en.getRow().SetValByKey("AthNum", athNum + 1);
            en.Update();
        }
        if (DataType.IsNullOrEmpty(empNo)) {
            return uploadFileM;
        } else {
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "上传成功");
            return "{\"msg\":\"上传成功\"}";
        }

    }

    /**
     * 删除附件
     *
     * @return
     */
    public final String DelWorkCheckAttach() throws Exception {
        FrmAttachmentDB athDB = new FrmAttachmentDB();
        athDB.RetrieveByAttr(FrmAttachmentDBAttr.MyPK, this.getMyPK());

        //删除文件
        if (athDB.getFileFullName() != null) {
            if ((new File(athDB.getFileFullName())).isFile() == true) {
                (new File(athDB.getFileFullName())).delete();
            }
        }

        int i = athDB.Delete(FrmAttachmentDBAttr.MyPK, this.getMyPK());
        if (i > 0) {
            return "true";
        }
        return "false";
    }

    public final String FrmVSTO_Init() {
        return "";
    }

    /**
     * 表单处理加载
     *
     * @return
     */
    public final String FrmSingle_Init() throws Exception {
        if ((this.getFrmID() == null || this.getFrmID().isEmpty())) {
            throw new RuntimeException("FK_MapData参数不能为空");
        }

        MapData md = new MapData();
        md.setNo(this.getFrmID());

        if (md.RetrieveFromDBSources() == 0) {
            throw new RuntimeException("未检索到FK_MapData=" + this.getFrmID() + "的表单，请核对参数");
        }

        int minOID = 10000000; //最小OID设置为一千万
        long oid = this.getOID();
        Hashtable ht = new Hashtable();
        GEEntityOID en = md.getHisGEEn();

        if (oid == 0) {
            oid = minOID;
        }

        en.setOID(oid);

        if (en.RetrieveFromDBSources() == 0) {
            ht.put("IsExist", 0);
        } else {
            ht.put("IsExist", 1);
        }

        ht.put("OID", oid);
        ht.put("UserNo", WebUser.getNo());
        ht.put("Token", WebUser.getToken());

        return Json.ToJsonEntityModel(ht);
    }


    ///#region 从表的选项.

    /**
     * 初始化数据
     *
     * @return
     */
    public final String DtlOpt_Init() throws Exception {
        MapDtl dtl = new MapDtl(this.getMapDtlNo());

        if (dtl.getImpModel() == 0) {
            return "err@该从表不允许导入.";
        }

        if (dtl.getImpModel() == 2) {
            return "url@DtlImpByExcel.htm?FK_MapDtl=" + this.getMapDtlNo();
        }


        if (DataType.IsNullOrEmpty(dtl.getImpSQLInit())) {
            return "err@从表加载语句为空，请设置从表加载的sql语句。";
        }

        DataSet ds = new DataSet();
        DataTable dt = DBAccess.RunSQLReturnTable(dtl.getImpSQLInit());

        return Json.ToJson(dt);

    }

    /**
     * 增加
     *
     * @return
     */
    public final String DtlOpt_Add() throws Exception {
        MapDtl dtl = new MapDtl(this.getMapDtlNo());
        String pks = this.GetRequestVal("PKs");

        String[] strs = pks.split("[,]", -1);
        int i = 0;
        for (String str : strs) {
            if (DataType.IsNullOrEmpty(str) == true || str.equals("CheckAll") == true) {
                continue;
            }

            GEDtl gedtl = new GEDtl(this.getMapDtlNo());
            String sql = dtl.getImpSQLFullOneRow();
            sql = sql.replace("@Key", str);

            DataTable dt = DBAccess.RunSQLReturnTable(sql);

            if (dt.Rows.size() == 0) {
                return "err@导入数据失败:" + sql;
            }

            gedtl.Copy(dt.Rows.get(0));
            gedtl.setRefPK(this.GetRequestVal("RefPKVal"));
            gedtl.InsertAsNew();
            i++;
        }

        return "成功的导入了[" + i + "]行数据...";
    }

    /**
     * 执行查询.
     *
     * @return
     */
    public final String DtlOpt_Search() throws Exception {
        MapDtl dtl = new MapDtl(this.getMapDtlNo());

        String sql = dtl.getImpSQLSearch();
        sql = sql.replace("@Key", this.GetRequestVal("Key"));
        sql = sql.replace("@WebUser.No", WebUser.getNo());
        sql = sql.replace("@WebUser.getName()", WebUser.getName());
        sql = sql.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
        sql = sql.replace("@WebUser.DeptNo", WebUser.getDeptNo());

        DataSet ds = new DataSet();
        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        return Json.ToJson(dt);
    }

    ///#endregion 从表的选项.


    ///#region SQL从表导入.
    public final String DtlImpBySQL_Delete() throws Exception {
        MapDtl dtl = new MapDtl(this.getEnsName());
        DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPK='" + this.getRefPKVal() + "'");
        return "";
    }

    /**
     * SQL从表导入
     *
     * @return
     */
    public final String DtlImpBySQl_Imp() throws Exception {
        //获取参数
        String ensName = this.getEnsName();
        String refpk = this.getRefPKVal();
        long pworkID = this.getPWorkID();
        int fkNode = this.getNodeID();
        long fid = this.getFID();
        String pk = this.GetRequestVal("PKs");
        GEDtls dtls = new GEDtls(ensName);
        QueryObject qo = new QueryObject(dtls);
        //获取从表权限
        MapDtl dtl = new MapDtl(ensName);

        ///#region 处理权限方案。
        if (this.getNodeID() != 0 && this.getNodeID() != 999999) {
            Node nd = new Node(this.getNodeID());
            if (nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.RefOneFrmTree) {
                FrmNode fn = new FrmNode(nd.getNodeID(), dtl.getFrmID());
                if (fn.getFrmSln() == FrmSln.Self) {
                    String no = this.getMapDtlNo() + "_" + nd.getNodeID();
                    MapDtl mdtlSln = new MapDtl();
                    mdtlSln.setNo(no);
                    int result = mdtlSln.RetrieveFromDBSources();
                    if (result != 0) {
                        dtl = mdtlSln;

                    }
                }
            }
        }

        ///#endregion 处理权限方案。

        //判断是否重复导入
        boolean isInsert = true;
        if (DataType.IsNullOrEmpty(pk) == false) {
            String[] pks = pk.split("[@]", -1);
            int idx = 0;
            for (String k : pks) {
                if (DataType.IsNullOrEmpty(k)) {
                    continue;
                }
                if (idx == 0) {
                    qo.AddWhere(k, this.GetRequestVal(k));
                } else {
                    qo.addAnd();
                    qo.AddWhere(k, this.GetRequestVal(k));
                }
                idx++;
            }
            switch (dtl.getDtlOpenType()) {
                case ForEmp: // 按人员来控制.
                    qo.addAnd();
                    qo.AddWhere("RefPk", refpk);
                    qo.addAnd();
                    qo.AddWhere("Rec", this.GetRequestVal("UserNo"));
                    break;
                case ForWorkID: // 按工作ID来控制
                    qo.addAnd();
                    qo.addLeftBracket();
                    qo.AddWhere("RefPk", refpk);
                    qo.addOr();
                    qo.AddWhere("FID", fid);
                    qo.addRightBracket();
                    break;
                case ForFID: // 按流程ID来控制.
                    qo.addAnd();
                    qo.AddWhere("FID", fid);
                    break;
            }

            int count = qo.GetCount();
            if (count > 0) {
                isInsert = false;
            }
        }
        //导入数据
        if (isInsert == true) {

            bp.en.Entity tempVar = dtls.getNewEntity();
            GEDtl dtlEn = tempVar instanceof GEDtl ? (GEDtl) tempVar : null;
            //遍历属性，循环赋值.
            for (Attr attr : dtlEn.getEnMap().getAttrs()) {
                dtlEn.SetValByKey(attr.getKey(), this.GetRequestVal(attr.getKey()));

            }
            switch (dtl.getDtlOpenType()) {
                case ForEmp: // 按人员来控制.
                    dtlEn.setRefPKInt(Integer.parseInt(refpk));
                    break;
                case ForWorkID: // 按工作ID来控制
                    dtlEn.setRefPKInt(Integer.parseInt(refpk));
                    dtlEn.SetValByKey("FID", Integer.parseInt(refpk));
                    break;
                case ForFID: // 按流程ID来控制.
                    dtlEn.setRefPKInt(Integer.parseInt(refpk));
                    dtlEn.SetValByKey("FID", fid);
                    break;
            }
            dtlEn.SetValByKey("RDT", DataType.getCurrentDate());
            dtlEn.SetValByKey("Rec", this.GetRequestVal("UserNo"));
            //dtlen.setOID( (int)DBAccess.GenerOID(ensName);

            dtlEn.Insert();
            return String.valueOf(dtlEn.getOID());
        }

        return "";

    }

    ///#endregion SQL从表导入


    ///#region Excel导入.

    /**
     * 导入excel.
     *
     * @return
     */
    public final String DtlImpByExcel_Imp() throws Exception {
        String tempPath = SystemConfig.getPathOfTemp();
        try {
            HttpServletRequest request = getRequest();
            String flieName = "File_Upload";
            String pageType = this.GetRequestVal("PageType");
            if (DataType.IsNullOrEmpty(pageType) == false && "Vue3".equals(pageType)) {
                flieName = "file";
            }
            if (CommonFileUtils.getFilesSize(request, flieName) == 0) {
                return "err@请选择要上传的从表模版。";
            }

            String fileName = CommonFileUtils.getOriginalFilename(request, flieName);
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
            File file = null;
            try {
                file = File.createTempFile(fileNewName, ext);
            } catch (IOException e1) {
                file = new File(System.getProperty("java.io.tmpdir"), fileNewName + ext);
            }
            file.deleteOnExit();
            try {
                CommonFileUtils.upload(request, flieName, file);
            } catch (Exception e) {
                e.printStackTrace();
                return "err@执行失败";
            }

            GEDtls dtls = new GEDtls(this.getFK_MapDtl());
            DataTable dt = DBLoad.GetTableByExt(file.getAbsolutePath());

            String FK_MapDtl = this.getMapDtlNo();
            if (FK_MapDtl.contains("BP") == true)
            {
                return BPDtlImpByExcel_Imp(dt, FK_MapDtl);
            }
            MapDtl dtl = new MapDtl(this.getMapDtlNo());

             ///#region 处理权限方案。
            if (this.getNodeID() != 0 && this.getNodeID() != 999999)
            {
                Node nd = new Node(this.getNodeID());
                if (nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.RefOneFrmTree)
                {
                    FrmNode fn = new FrmNode(nd.getNodeID(), this.getFrmID());
                    if (fn.getFrmSln() == FrmSln.Self)
                    {
                        String no = this.getMapDtlNo() + "_" + nd.getNodeID();
                        MapDtl mdtlSln = new MapDtl();
                        mdtlSln.setNo(no);
                        int result = mdtlSln.RetrieveFromDBSources();
                        if (result != 0)
                        {
                            dtl = mdtlSln;
                            //fk_mapDtl = no;
                        }
                    }
                }
            }
            ///#endregion 处理权限方案。

            /// #region 检查两个文件是否一致。 生成要导入的属性
            Attrs attrs = dtls.getNewEntity().getEnMap().getAttrs();
            Attrs attrsExp = new Attrs();

            boolean isHave = false;
            for (DataColumn dc : dt.Columns) {
                for (Attr attr : attrs) {
                    if (dc.ColumnName.equals(attr.getDesc())) {
                        isHave = true;
                        attrsExp.Add(attr);
                        continue;
                    }

                    if (dc.ColumnName.toLowerCase().equals(attr.getKey().toLowerCase())) {
                        isHave = true;
                        attrsExp.Add(attr);
                        dc.ColumnName = attr.getDesc();
                    }
                }
            }
            if (isHave == false)
                throw new Exception("@您导入的excel文件不符合系统要求的格式，请下载模版文件重新填入。");
            /// #endregion

            /// #region 执行导入数据.

            if (this.GetRequestValInt("DDL_ImpWay") == 0)
                DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPK='" + this.getWorkID() + "'");

            int i = 0;
            long oid = DBAccess.GenerOID("Dtl", dt.Rows.size());
            String rdt = DataType.getCurrentDate();

            //获取枚举
            SysEnums enums = new SysEnums();
            enums.RetrieveInSQL(SysEnumAttr.EnumKey, "SELECT UIBindKey From Sys_MapAttr WHERE FK_MapData='" + dtl.getNo() + "' AND LGType=1");
            DataTable dtt = null;
            //获取外键、外部数据源
            DataSet ds = new DataSet();
            String treeKey = dtl.getInitDBAttrs();
            Attr attrKey = null;
            for (Attr attr : attrs.ToJavaList())
            {
                if (DataType.IsNullOrEmpty(attr.getUIBindKey()) == true)
                {
                    continue;
                }
                if (attr.getMyFieldType() == FieldType.Enum || attr.getMyFieldType() == FieldType.PKEnum)
                {
                    continue;
                }
                if (ds.Tables.contains(attr.getUIBindKey()) == true)
                {
                    continue;
                }
                dtt = PubClass.GetDataTableByUIBineKey(attr.getUIBindKey());

                dtt.TableName = attr.getUIBindKey();
                ds.Tables.add(dtt);
                if (DataType.IsNullOrEmpty(treeKey) == false && attr.getKey().equals(treeKey))
                {
                    attrKey = attr;
                }
            }

            String errMsg = "";
            String treeNames = ",";
            int errIdx = 0;

            for (DataRow dr : dt.Rows) {
                GEDtl dtlEn = (GEDtl) dtls.getNewEntity();
                dtlEn.ResetDefaultVal();
                String rowMsg = "";
                for (Attr attr : attrsExp) {
                    if (attr.getUIVisible() == false || dr.getValue(attr.getDesc()) == "")
                        continue;
                    String val = dr.getValue(attr.getDesc()).toString();
                    if (val == null)
                        continue;
                    val = val.trim();
                    if (attrKey != null && attr.getKey().equals(treeKey))
                        treeNames += val + ",";
                    switch (attr.getMyFieldType()) {
                        case Enum:
                        case PKEnum:
                            SysEnums ses = new SysEnums(attr.getUIBindKey());
                            boolean isHavel = false;
                            //多选 val用英文逗号分隔
                            if (val.indexOf(",") != -1){
                                String[] vals = val.split(",");
                                int idx = 0;
                                String enumVal = "";
                                for (SysEnum se : ses.ToJavaList()) {
                                    for (String s : vals){
                                        if (s.equals(se.getLab())) {
                                            enumVal += String.valueOf(se.getIntKey());
                                            enumVal +=",";
                                            idx ++;
                                        }
                                    }
                                }
                                val = enumVal;
                                if(vals.length == idx)
                                    isHavel = true;

                                break;
                            }

                            for (SysEnum se : ses.ToJavaList()) {
                                if (val.equals(se.getLab())) {
                                    if (DataType.IsNullOrEmpty(se.getStrKey()) == false)
                                        val = se.getStrKey();
                                    else
                                        val = String.valueOf(se.getIntKey());
                                    isHavel = true;
                                    break;
                                }
                            }

                            if (isHavel == false) {
                                rowMsg += "@数据格式不规范,第(" + i + ")行，列(" + attr.getDesc() + ")，数据(" + val
                                        + ")不符合格式,该值没有在枚举列表里.";
                                val = attr.getDefaultVal().toString();
                            }
                            break;
                        case FK:
                        case PKFK:
                            dtt = ds.GetTableByName(attr.getUIBindKey());
                            boolean isHavelIt = false;
                            for (DataRow drr : dtt.Rows)
                            {
                                if (val.equals(drr.get("Name").toString()))
                                {
                                    val = drr.get("No").toString();
                                    isHavelIt = true;
                                    break;
                                }
                            }
                            if (isHavelIt == false)
                            {
                                rowMsg += "@数据格式不规范,第(" + i + ")行，列(" + attr.getDesc() + ")，数据(" + val + ")不符合格式,改值没有在外键数据列表里.";
                            }

                            break;
                        default:
                            if (attr.getUIContralType() == UIContralType.DDL)
                            {
                                dtt = ds.GetTableByName(attr.getUIBindKey());
                                boolean isHaveIt = false;
                                for (DataRow drr : dtt.Rows)
                                {
                                    if (val.equals(drr.get("Name").toString()))
                                    {
                                        val = drr.get("No").toString();
                                        isHaveIt = true;
                                        dtlEn.SetValByKey(attr.getKey() + "T", drr.get("Name").toString());
                                        break;
                                    }
                                }
                                if (isHaveIt == false)
                                    rowMsg += "@数据格式不规范,第(" + i + ")行，列(" + attr.getDesc() + ")，数据(" + val + ")不符合格式,改值没有在外键数据列表里.";
                                break;
                            }
                            break;
                    }

                    if (attr.getMyDataType() == DataType.AppBoolean) {
                        if (val.trim().equals("是") || val.trim().toLowerCase().equals("yes"))
                            val = "1";

                        if (val.trim().equals("否") || val.trim().toLowerCase().equals("no"))
                            val = "0";
                    }

                    dtlEn.SetValByKey(attr.getKey(), val);
                }
                if (DataType.IsNullOrEmpty(rowMsg) == false)
                {
                    errMsg += rowMsg;
                    errIdx++;
                    continue;
                }
                dtlEn.setRefPKInt(Integer.valueOf(this.getRefPKVal()));
                // 关联主赋值.
                switch (dtl.getDtlOpenType()) {
                    case ForEmp: // 按人员来控制.
                        dtlEn.setRefPKInt((int) this.getWorkID());
                        break;
                    case ForWorkID: // 按工作ID来控制
                        dtlEn.setRefPKInt((int) this.getWorkID());
                        dtl.SetValByKey("FID", this.getWorkID());
                        break;
                    case ForFID: // 按流程ID来控制.
                        dtlEn.setRefPKInt((int) this.getWorkID());
                        dtl.SetValByKey("FID", this.getFID());
                        break;
                }

                dtlEn.SetValByKey("RDT", rdt);
                dtlEn.SetValByKey("Rec", WebUser.getNo());
                i++;

                dtlEn.SaveAsOID(oid);
                oid++;
            }
            /// #endregion 执行导入数据.

            //如果从表是损溢表展示，需要把其他的数据插入
            if (dtl.GetValIntByKey(MapDtlAttr.ListShowModel) == 6 && DataType.IsNullOrEmpty(treeKey) == false)
            {
                dtt = ds.GetTableByName(attrKey.getUIBindKey());
                for (DataRow dr : dtt.Rows)
                {
                    if (treeNames.contains("," + dr.get(1).toString() + ","))
                    {
                        continue;
                    }
                    GEDtl dtlEn = dtls.getNewEntity() instanceof GEDtl ? (GEDtl)dtls.getNewEntity() : null;
                    dtlEn.ResetDefaultVal();
                    dtlEn.SetValByKey(attrKey.getKey(), dr.get(0));
                    if (attrs.contains(attrKey.getKey() + "T"))
                    {
                        dtlEn.SetValByKey(attrKey.getKey() + "T", dr.get(1));
                    }
                    dtlEn.setRefPK(this.getRefPKVal());
                    switch (dtl.getDtlOpenType())
                    {
                        case ForEmp: // 按人员来控制.
                            dtlEn.setRefPKInt((int)this.getWorkID());
                            break;
                        case ForWorkID: // 按工作ID来控制
                            dtlEn.setRefPKInt((int)this.getWorkID());
                            dtlEn.SetValByKey("FID", this.getWorkID());
                            break;
                        case ForFID: // 按流程ID来控制.
                            dtlEn.setRefPKInt((int)this.getWorkID());
                            dtlEn.SetValByKey("FID", this.getFID());
                            break;
                    }
                    dtlEn.SetValByKey("RDT", rdt);
                    dtlEn.SetValByKey("Rec", WebUser.getNo());
                    dtlEn.Insert();
                }
            }

            if (StringUtils.isEmpty(errMsg) == true)
                return "info@共有(" + i + ")条数据导入成功。";
            else
                return "共有(" + errIdx + ")条数据导入成功，但是出现如下错误:" + errMsg;

        } catch (Exception ex) {
            String msg = ex.getMessage().replace("'", "‘");
            return "err@" + msg;
        }
    }

    /**
     * BP类从表导入
     *
     * @return
     */
    private String BPDtlImpByExcel_Imp(DataTable dt, String fk_mapdtl) {
        try {
            ///#region 检查两个文件是否一致。 生成要导入的属性
            Entities dtls = ClassFactory.GetEns(this.getMapDtlNo());
            bp.en.Entity tempVar = dtls.getNewEntity();
            EntityOID dtlEn = tempVar instanceof EntityOID ? (EntityOID) tempVar : null;
            Attrs attrs = dtlEn.getEnMap().getAttrs();
            Attrs attrsExp = new Attrs();

            boolean isHave = false;
            for (DataColumn dc : dt.Columns) {
                for (Attr attr : attrs) {
                    if (Objects.equals(dc.ColumnName, attr.getDesc())) {
                        isHave = true;
                        attrsExp.add(attr);
                        continue;
                    }

                    if (Objects.equals(dc.ColumnName.toLowerCase(), attr.getKey().toLowerCase())) {
                        isHave = true;
                        attrsExp.add(attr);
                        dc.ColumnName = attr.getDesc();
                    }
                }
            }
            if (isHave == false) {
                return "err@您导入的excel文件不符合系统要求的格式，请下载模版文件重新填入。";
            }

            ///#region 执行导入数据.

            if (this.GetRequestValInt("DDL_ImpWay") == 0) {
                DBAccess.RunSQL("DELETE FROM " + dtlEn.getEnMap().getPhysicsTable() + " WHERE RefPK='" + this.GetRequestVal("RefPKVal") + "'");
            }

            int i = 0;
            long oid = DBAccess.GenerOID(dtlEn.getEnMap().getPhysicsTable(), dt.Rows.size());
            String rdt = DataType.getCurrentDate();

            String errMsg = "";
            for (DataRow dr : dt.Rows) {
                bp.en.Entity tempVar2 = dtls.getNewEntity();
                dtlEn = tempVar2 instanceof EntityOID ? (EntityOID) tempVar2 : null;
                dtlEn.ResetDefaultVal(null, null, 0);

                for (Attr attr : attrsExp) {
                    if (attr.getUIVisible() == false || dr.getValue(attr.getDesc()) == null) {
                        continue;
                    }
                    String val = dr.getValue(attr.getDesc()).toString();
                    if (val == null) {
                        continue;
                    }
                    val = val.trim();
                    switch (attr.getMyFieldType()) {
                        case Enum:
                        case PKEnum:
                            SysEnums ses = new SysEnums(attr.getUIBindKey());
                            boolean isHavel = false;
                            for (SysEnum se : ses.ToJavaList()) {
                                if (Objects.equals(val, se.getLab())) {
                                    val = String.valueOf(se.getIntKey());
                                    isHavel = true;
                                    break;
                                }
                            }
                            if (isHavel == false) {
                                errMsg += "@数据格式不规范,第(" + i + ")行，列(" + attr.getDesc() + ")，数据(" + val + ")不符合格式,该值没有在枚举列表里.";
                                val = attr.getDefaultVal().toString();
                            }
                            break;
                        case FK:
                        case PKFK:
                            Entities ens = null;
                            if (attr.getUIBindKey().contains(".")) {
                                ens = ClassFactory.GetEns(attr.getUIBindKey());
                            } else {
                                ens = new GENoNames(attr.getUIBindKey(), "desc");
                            }

                            ens.RetrieveAll();
                            boolean isHavelIt = false;
                            for (Entity en : ens) {
                                if (Objects.equals(val, en.GetValStrByKey("Name"))) {
                                    val = en.GetValStrByKey("No");
                                    isHavelIt = true;
                                    break;
                                }
                            }
                            if (isHavelIt == false) {
                                errMsg += "@数据格式不规范,第(" + i + ")行，列(" + attr.getDesc() + ")，数据(" + val + ")不符合格式,该值没有在外键数据列表里.";
                            }
                            break;
                        default:
                            break;
                    }

                    if (attr.getMyDataType() == DataType.AppBoolean) {
                        if (Objects.equals(val.trim(), "是") || Objects.equals(val.trim().toLowerCase(), "yes")) {
                            val = "1";
                        }

                        if (Objects.equals(val.trim(), "否") || Objects.equals(val.trim().toLowerCase(), "no")) {
                            val = "0";
                        }
                    }

                    dtlEn.SetValByKey(attr.getKey(), val);
                }

                dtlEn.SetValByKey("RefPK", this.GetRequestVal("RefPKVal"));
                i++;

                dtlEn.Insert();
            }

            ///#endregion 执行导入数据.

            if (DataType.IsNullOrEmpty(errMsg) == true) {
                return "info@共有(" + i + ")条数据导入成功。";
            } else {
                return "共有(" + i + ")条数据导入成功，但是出现如下错误:" + errMsg;
            }

        } catch (Exception ex) {
            String msg = ex.getMessage().replace("'", "‘");
            return "err@" + msg;
        }
    }

    ///#endregion  Excel导入.


    ///#region 打印.
    public final String Print_Init() throws Exception {
        String ApplicationPath = SystemConfig.getHostURLOfBS();

        Node nd = new Node(this.getFK_Node());
        String path = ApplicationPath + "DataUser/CyclostyleFile/FlowFrm/" + nd.getFlowNo() + "/" + nd.getNodeID()
                + "/";
        String[] fls = null;
        try {
            fls = (new File(path)).list();
        } catch (RuntimeException ex) {
            return "err@" + ex.getMessage();
        }

        DataTable dt = new DataTable();
        dt.Columns.Add("BillNo");
        dt.Columns.Add("BillName");

        int idx = 0;
        int fileIdx = -1;
        for (String f : fls) {
            fileIdx++;
            String myfile = f.replace(path, "");
            String[] strs = myfile.split("[.]", -1);
            idx++;

            DataRow dr = dt.NewRow();
            dr.setValue("BillNo", strs[0]);
            dr.setValue("BillName", strs[1]);

            dt.Rows.add(dr);
        }

        // 返回json.
        return bp.tools.Json.ToJson(dt);
    }

    ///#endregion 打印.


    ///#region 附件组件.

    /**
     * 执行删除
     *
     * @return
     */
    public final String AttachmentUpload_Del() throws Exception {
        //执行删除.
        String delPK = this.GetRequestVal("DelPKVal");

        FrmAttachmentDB delDB = new FrmAttachmentDB();
        delDB.setMyPK(delPK == null ? this.getMyPK() : delPK);
        delDB.RetrieveFromDBSources();
        delDB.Delete(); //删除上传的文件.
        return "删除成功.";
    }

    public final String AttachmentUpload_DownByStream() throws Exception {
        // return AttachmentUpload_Down(true);
        return AttachmentUpload_Down();
    }

    /**
     * 下载
     *
     * @return
     */
    public final String AttachmentUpload_Down() throws Exception {
        //获取文件是否加密
        boolean fileEncrypt = SystemConfig.isEnableAthEncrypt();
        FrmAttachmentDB downDB = new FrmAttachmentDB();

        downDB.setMyPK(this.getMyPK());
        downDB.Retrieve();
        FrmAttachment dbAtt = new FrmAttachment();
        dbAtt.setMyPK(downDB.getFKFrmAttachment());
        dbAtt.Retrieve();

        if (dbAtt.getReadRole() != 0 && this.getNodeID() != 0) {
            //标记已经阅读了.
            GenerWorkerList gwf = new GenerWorkerList();
            int count = gwf.Retrieve(GenerWorkerListAttr.FK_Emp, WebUser.getNo(), GenerWorkerListAttr.FK_Node, this.getNodeID(), GenerWorkerListAttr.WorkID, this.getWorkID());
            if (count != 0) {
                String str = gwf.GetParaString(dbAtt.getNoOfObj());
                str += "," + downDB.getMyPK();
                gwf.SetPara(dbAtt.getNoOfObj(), str);
                gwf.Update();
            }
        }

        boolean isEncrypt = downDB.GetParaBoolen("IsEncrypt");
        String filepath = "";
        if (dbAtt.getAthSaveWay() == AthSaveWay.IISServer) {

            ///#region 解密下载
            //1、先解密到本地
            filepath = downDB.getFileFullName() + ".tmp";
            if (fileEncrypt == true && isEncrypt == true) {
                if ((new File(filepath)).isFile() == true) {
                    (new File(filepath)).delete();
                }
                AesEncodeUtil.decryptFile(downDB.getFileFullName(), filepath);
            } else {
                filepath = downDB.getFileFullName();
            }

            ///#endregion
        }

        if (dbAtt.getAthSaveWay() == AthSaveWay.FTPServer) {
            //下载文件的临时位置
            String tempFile = downDB.GenerTempFile(dbAtt.getAthSaveWay());
            filepath = tempFile + ".temp";
            if (fileEncrypt == true && isEncrypt == true) {
                AesEncodeUtil.decryptFile(tempFile, filepath);
            } else {
                filepath = tempFile;
            }
        }

        if(dbAtt.getAthSaveWay()==AthSaveWay.Minio){
            String tempFile=downDB.GenerTempFile(dbAtt.getAthSaveWay());
            MinioHelper.download(downDB.getFileFullName(),downDB.getatPara().GetValStrByKey("ContentType"),tempFile);
            filepath=tempFile;
        }

        if (dbAtt.getAthSaveWay() == AthSaveWay.DB) {
            String guid = bp.da.DBAccess.GenerGUID();
            // 把文件临时保存到一个位置.
            filepath = SystemConfig.getPathOfTemp() + "" + guid + ".tmp";
            DBAccess.GetFileFromDB(filepath, "Sys_FrmAttachmentDB", "MyPK", downDB.getMyPK(), "FileDB");

        }
        HttpHandlerGlo.DownloadFile(filepath, downDB.getFileName());
        return DataType.PraseStringToUrlFileName(filepath);
    }


    public final void AttachmentDownFromByte() throws Exception {
        FrmAttachmentDB downDB = new FrmAttachmentDB();
        downDB.setMyPK(this.getMyPK());
        downDB.Retrieve();
        downDB.setFileName(URLEncoder.encode(downDB.getFileName()));
        byte[] byteList = downDB.GetFileFromDB("FileDB", null);
        if (byteList != null) {
            ContextHolderUtils.ResponseWriteFile(byteList, downDB.getFileName(), "application/octet-stream;charset=gb2312");
        }
    }

    /**
     * 附件ID.
     */
    public final String getFKFrmAttachment() {
        return this.GetRequestVal("FK_FrmAttachment");
    }

    public final FrmAttachment GenerAthDescOfFoolTruck() throws Exception {
        FoolTruckNodeFrm sln = new FoolTruckNodeFrm();
        sln.setFrmSln(-1);
        String fromFrm = this.GetRequestVal("FromFrm");
        sln.setMyPK(fromFrm + "_" + this.getNodeID() + "_" + this.getFlowNo());
        int result = sln.RetrieveFromDBSources();
        FrmAttachment athDesc = new FrmAttachment();
        athDesc.setMyPK(this.getFKFrmAttachment());
        athDesc.RetrieveFromDBSources();

        /*没有查询到解决方案, 就是只读方案 */
        if (result == 0 || sln.getFrmSln() == 1) {
            athDesc.setItIsUpload(false);
            athDesc.setItIsDownload(true);
            athDesc.setHisDeleteWay(AthDeleteWay.None); //删除模式.
            return athDesc;
        }
        //默认方案
        if (sln.getFrmSln() == 0) {
            return athDesc;
        }

        //如果是自定义方案,就查询自定义方案信息.
        if (sln.getFrmSln() == 2) {
            FrmAttachment athDescNode = new FrmAttachment();
            athDescNode.setMyPK(this.getFKFrmAttachment() + "_" + this.getNodeID());
            if (athDescNode.RetrieveFromDBSources() == 0) {
                //没有设定附件权限，保持原来的附件权限模式
                return athDesc;
            }
            return athDescNode;
        }

        return null;
    }

    /**
     * 生成描述
     *
     * @return
     */
    public final FrmAttachment GenerAthDesc() throws Exception {

        ///#region 为累加表单做的特殊判断.
        if (this.GetRequestValInt("FormType") == 10) {
            if (this.getFKFrmAttachment().contains(this.getFrmID()) == false) {
                return GenerAthDescOfFoolTruck(); //如果当前表单的ID。
            }
        }

        ///#endregion

        FrmAttachment athDesc = new FrmAttachment();
        athDesc.setMyPK(this.getFKFrmAttachment());
        if (this.getNodeID() == 0 || this.getFlowNo() == null) {
            athDesc.RetrieveFromDBSources();
            return athDesc;
        }

        athDesc.setMyPK(this.getFKFrmAttachment());
        int result = athDesc.RetrieveFromDBSources();


        ///#region 判断是否是明细表的多附件.
        if (result == 0 && DataType.IsNullOrEmpty(this.getFlowNo()) == false && this.getFKFrmAttachment().contains("AthMDtl")) {
            athDesc.setFrmID(this.getFrmID());
            athDesc.setNoOfObj("AthMDtl");
            athDesc.setName("我的从表附件");
            athDesc.setUploadType(AttachmentUploadType.Multi);
            athDesc.Insert();
        }

        ///#endregion 判断是否是明细表的多附件。


        ///#region 判断是否可以查询出来，如果查询不出来，就可能是公文流程。
        if (result == 0 && DataType.IsNullOrEmpty(this.getFlowNo()) == false && this.getFKFrmAttachment().contains("DocMultiAth")) {
            /*如果没有查询到它,就有可能是公文多附件被删除了.*/
            athDesc.setMyPK(this.getFKFrmAttachment());
            athDesc.setNoOfObj("DocMultiAth");
            athDesc.setFrmID(this.getFrmID());
            athDesc.setExts("*.*");

            //存储路径.
            // athDesc.SaveTo = "/DataUser/UploadFile/";
            athDesc.setItIsNote(false); //不显示note字段.
            athDesc.setItIsVisable(false); // 让其在form 上不可见.

            //位置.
            athDesc.setH((float) 150);

            //多附件.
            athDesc.setUploadType(AttachmentUploadType.Multi);
            athDesc.setName("公文多附件(系统自动增加)");
            athDesc.SetValByKey("AtPara", "@IsWoEnablePageset=1@IsWoEnablePrint=1@IsWoEnableViewModel=1@IsWoEnableReadonly=0@IsWoEnableSave=1@IsWoEnableWF=1@IsWoEnableProperty=1@IsWoEnableRevise=1@IsWoEnableIntoKeepMarkModel=1@FastKeyIsEnable=0@IsWoEnableViewKeepMark=1@FastKeyGenerRole=@IsWoEnableTemplete=1");
            athDesc.Insert();

            //有可能在其其它的节点上没有这个附件，所以也要循环增加上它.
            Nodes nds = new Nodes(this.getFlowNo());
            for (Node nd : nds.ToJavaList()) {
                athDesc.setFrmID("ND" + nd.getNodeID());
                athDesc.setMyPK(athDesc.getFrmID() + "_" + athDesc.getNoOfObj());
                if (athDesc.getIsExits() == true) {
                    continue;
                }

                athDesc.Insert();
            }

            //重新查询一次，把默认值加上.
            athDesc.RetrieveFromDBSources();
        }

        ///#endregion 判断是否可以查询出来，如果查询不出来，就可能是公文流程。


        ///#region 处理权限方案。
        if (this.getNodeID() != 0) {
            String fk_mapdata = this.getFrmID();
            if (this.getFKFrmAttachment().contains("AthMDtl") == true) {
                fk_mapdata = this.GetRequestVal("FFK_MapData");
            }

            if ((fk_mapdata == null || fk_mapdata.equals(""))) {
                fk_mapdata = this.GetRequestVal("FK_MapData");
            }


            Node nd = new Node(this.getNodeID());
            Flow flow = new Flow(nd.getFlowNo());
            if (nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.RefOneFrmTree || flow.getFlowDevModel() == FlowDevModel.JiJian) {
                //如果是绑定表单树中的表单，重新赋值绑定表单的名字
                if (nd.getHisFormType() == NodeFormType.RefOneFrmTree || flow.getFlowDevModel() == FlowDevModel.JiJian) {
                    fk_mapdata = nd.getNodeFrmID();
                }
                FrmNode fn = new FrmNode(nd.getNodeID(), fk_mapdata);
				/*if (fn.FrmSln == FrmSln.Default)
				{
				    if (fn.WhoIsPK == WhoIsPK.FID)
				        athDesc.HisCtrlWay = AthCtrlWay.FID;

				    if (fn.WhoIsPK == WhoIsPK.PWorkID)
				        athDesc.HisCtrlWay = AthCtrlWay.PWorkID;

				    if (fn.WhoIsPK == WhoIsPK.P2WorkID)
				        athDesc.HisCtrlWay = AthCtrlWay.P2WorkID;


				}*/

                if (fn.getFrmSln() == FrmSln.Readonly) {

                    athDesc.setHisDeleteWay(AthDeleteWay.None);
                    athDesc.setItIsUpload(false);
                    athDesc.setItIsDownload(true);
                    athDesc.setMyPK(this.getFKFrmAttachment());
                    return athDesc;
                }

                if (fn.getFrmSln() == FrmSln.Self) {
                    if (this.getFKFrmAttachment().contains("AthMDtl") == true) {
                        athDesc.setMyPK(this.getFrmID() + "_" + nd.getNodeID() + "_AthMDtl");
                        athDesc.RetrieveFromDBSources();
                    } else {
                        athDesc.setMyPK(this.getFKFrmAttachment() + "_" + nd.getNodeID());
                        athDesc.RetrieveFromDBSources();
                    }
                    athDesc.setMyPK(this.getFKFrmAttachment());
                    return athDesc;
                }
            }
        }

        ///#endregion 处理权限方案。

        return athDesc;
    }

    public String getFK_FrmAttachment() {
        return this.GetRequestVal("FK_FrmAttachment");
    }

    /**
     * 打包下载.
     *
     * @return
     */
    public final String AttachmentUpload_DownZip() throws Exception {
        String zipName = this.getWorkID() + "_" + this.getFK_FrmAttachment();
        /// #region 处理权限控制.
        FrmAttachment athDesc = this.GenerAthDesc();

        // 查询出来数据实体.
        FrmAttachmentDBs dbs = bp.wf.CCFormAPI.GenerFrmAttachmentDBs(athDesc, this.getPKVal(), this.getFK_FrmAttachment(), Long.parseLong(this.getPKVal()));
        /// #endregion 处理权限控制.

        if (dbs.size() == 0)
            return "err@文件不存在，不需打包下载。";

        String basePath = SystemConfig.getPathOfDataUser() + "Temp";
        String tempUserPath = basePath + "/" + WebUser.getNo();
        String tempFilePath = basePath + "/" + WebUser.getNo() + "/" + this.getWorkID();
        String zipPath = basePath + "/" + WebUser.getNo();
        String zipFile = zipPath + "/" + zipName + ".zip";
        String info = "";

        File tempFile = new File(tempFilePath);
        try {
            // 删除临时文件，保证一个用户只能存一份，减少磁盘占用空间.
            info = "@创建用户临时目录:" + tempUserPath;
            File file = new File(tempUserPath);
            if (file.exists() == false)
                file.mkdirs();

            // 如果有这个临时的目录就把他删除掉.
            if (tempFile.exists() == true) {
                // tempFile.delete();
                boolean success = FileAccess.deletesFile(tempFile);
                if (!success) {
                    Log.DebugWriteInfo("删除临时目录失败");
                }
            }
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
        } catch (Exception ex) {
            return "err@组织临时目录出现错误:" + ex.getMessage();
        }

        try {
            for (FrmAttachmentDB db : dbs.ToJavaList()) {
                String copyToPath = tempFilePath;

                // 求出文件路径.
                String fileTempPath = db.GenerTempFile(athDesc.getAthSaveWay());
                if (athDesc.getAthSaveWay() == AthSaveWay.FTPServer) {
                    FtpUtil ftpUtil = bp.wf.Glo.getFtpUtil();
                    ftpUtil.downloadFile(db.getFileFullName(), fileTempPath);
                }
                if (athDesc.getAthSaveWay() == AthSaveWay.OSS) {
                    OSSUploadFileUtils.downloadFile(db.getFileFullName(), fileTempPath);
                }

                if (DataType.IsNullOrEmpty(db.getSort()) == false) {
                    copyToPath = tempFilePath + "/" + db.getSort();
                    File copyPath = new File(copyToPath);
                    if (copyPath.exists() == false)
                        copyPath.mkdirs();
                }
                // 新文件目录
                copyToPath = copyToPath + "/" + db.getFileName();
                try {
                    InputStream is = new FileInputStream(fileTempPath);
                    ;
                    int buffer = 1024; // 定义缓冲区的大小
                    int length = 0;
                    byte[] b = new byte[buffer];
                    double percent = 0;
                    FileOutputStream fos = new FileOutputStream(new File(copyToPath));
                    while ((length = is.read(b)) != -1) {
                        // percent += length / (double) new
                        // File(saveTo).supFileSize * 100D; // 计算上传文件的百分比
                        fos.write(b, 0, length); // 向文件输出流写读取的数据

                    }
                    fos.close();
                    new File(fileTempPath).delete();
                    if (new File(fileTempPath).exists() == true)
                        new File(fileTempPath).delete();
                } catch (RuntimeException ex) {
                    ex.getMessage();
                }
                // File.Copy(fileTempPath, copyToPath, true);
            }
        } catch (Exception ex) {
            return "err@组织文件期间出现错误:" + ex.getMessage();
        }

        File zipFileFile = new File(zipFile);
        try {
            while (zipFileFile.exists() == true) {
                zipFileFile.delete();
            }
            // 执行压缩.
            ZipCompress fz = new ZipCompress(zipFile, tempFilePath);
            fz.zip();
            // 删除临时文件夹
            tempFile.delete();
        } catch (Exception ex) {
            return "err@执行压缩出现错误:" + ex.getMessage() + ",路径tempPath:" + tempFilePath + ",zipFile=" + zipFile;
        }

        if (zipFileFile.exists() == false)
            return "err@压缩文件未生成成功,请在点击一次.";

        zipName = DataType.PraseStringToUrlFileName(zipName);

        String url = getRequest().getContextPath() + "DataUser/Temp/" + WebUser.getNo() + "/" + zipName + ".zip";
        return "url@" + url;

    }

    public final String AthSingle_Init() throws Exception {
        String mypk = this.GetRequestVal("AthMyPK");
        if (DataType.IsNullOrEmpty(mypk) == true) {
            return "err@请求参数MyPK的值不能为空";
        }
        //PKVal
        String pkVal = DataType.IsNullOrEmpty(this.getPKVal()) == true ? null : this.getPKVal();
        FrmAttachmentSingle ath = new FrmAttachmentSingle(mypk);
        //设计器中的预览。
        if (pkVal == null) {
            String file = SystemConfig.getPathOfTemp() + "/" + mypk + ".docx";
            if (ath.getAthSingleRole() != 0) {
                DBAccess.GetFileFromDB(file, ath.getEnMap().getPhysicsTable(), "MyPK", mypk, "TemplateFile");
                ath.SetPara("IsHaveFile", 0);
            }
            if ((new File(file)).isFile() == false) {
                CreateDocFile(file);
            }
            return ath.ToJson(false);
        }
        //获取athDB的数据
        FrmAttachmentDBs dbs = new FrmAttachmentDBs();
        dbs.Retrieve(FrmAttachmentDBAttr.FK_MapData, ath.getFrmID(), FrmAttachmentDBAttr.FK_FrmAttachment, ath.getMyPK(), FrmAttachmentDBAttr.RefPKVal, pkVal, null);
        FrmAttachmentDB db = null;
        String filePath = "";
        if (!dbs.isEmpty()) {
            db = dbs.get(0) instanceof FrmAttachmentDB ? (FrmAttachmentDB) dbs.get(0) : null;
            filePath = db.getFileFullName();
        }
        boolean isReadonly = this.GetRequestValBoolen("IsReadonly");
        if (isReadonly == false && ath.getAthSingleRole() != 0 && (new File(filePath)).isFile() == false) {
            if ((new File(ath.getSaveTo() + pkVal)).isDirectory() == false) {
                (new File(ath.getSaveTo() + pkVal)).mkdirs();
            }
            String file = SystemConfig.getPathOfTemp() + "/" + mypk + ".docx";
            DBAccess.GetFileFromDB(file, ath.getEnMap().getPhysicsTable(), "MyPK", mypk, "TemplateFile");
            Files.copy(Paths.get(file), Paths.get(ath.getSaveTo() + pkVal + "/" + ath.getMyPK() + ".docx"), StandardCopyOption.COPY_ATTRIBUTES);
        }

        //判断公文文件是否存在
        if ((new File(db.getFileFullName())).isFile() == false) {
            ath.SetPara("IsHaveFile", 0);
        } else {
            ath.SetPara("IsHaveFile", 1);
        }
        return ath.ToJson(false);
    }

    public final String AthSingle_Upload() throws Exception {
        String mypk = this.GetRequestVal("AthMyPK");
        FrmAttachmentSingle ath = new FrmAttachmentSingle(mypk);
        FrmAttachmentDBs dbs = new FrmAttachmentDBs();
        String pkVal = DataType.IsNullOrEmpty(this.getPKVal()) == true ? null : this.getPKVal();
        String fileName = mypk + ".docx";
        String filePath = ath.getSaveTo() + pkVal + "/" + fileName;
        HttpServletRequest request = ContextHolderUtils.getRequest();
        if (CommonFileUtils.getFilesSize(request, "file") > 0) {
            CommonFileUtils.upload(request, "file", new File(filePath));
        }

        dbs.Retrieve(FrmAttachmentDBAttr.FK_MapData, ath.getFrmID(), FrmAttachmentDBAttr.FK_FrmAttachment, ath.getMyPK(), FrmAttachmentDBAttr.RefPKVal, pkVal, null);
        if (dbs.isEmpty() && ath.getAthEditModel() != 0) {
            //增加一条数据
            FrmAttachmentDB db = new FrmAttachmentDB();
            db.setMyPK(DBAccess.GenerGUID(0, null, null));
            db.setNodeID(this.getNodeID());
            db.setFrmID(ath.getFrmID());
            db.setFKFrmAttachment(ath.getMyPK());
            db.setFID(this.getFID()); //流程id.
            db.setRefPKVal(pkVal);
            db.setFileExts("docx");
            db.setFileName(ath.getMyPK() + "." + db.getFileExts());
            db.setRDT(DataType.getCurrentDateTimess());
            db.setRec(WebUser.getNo());
            db.setRecName(WebUser.getName());
            db.setDeptNo(WebUser.getDeptNo());
            //设置路径.
            db.setFileFullName(ath.getSaveTo() + db.getRefPKVal() + "/" + db.getFileName());
            db.Insert();
        }
        return "";
    }

    public final String AthSingle_TemplateData() throws Exception {
        //获取表单数据
        String pkVal = DataType.IsNullOrEmpty(this.getPKVal()) == true ? null : this.getPKVal();
        if (this.getNodeID() == 0) {
            return "err@没有获取到FK_Node的值";
        }
        if (pkVal == null) {
            return "err@没有获取到OID的值";
        }
        Node nd = new Node(this.getNodeID());
        Work wk = nd.getHisWork();
        wk.setOID(Long.parseLong(pkVal));
        wk.RetrieveFromDBSources();
        Attrs attrs = wk.getEnMap().getAttrs();
        DataTable dt = new DataTable();
        dt.Columns.Add("name");
        dt.Columns.Add("text");
        dt.Columns.Add("type");
        DataRow dr = null;
        for (Attr attr : attrs) {
            dr = dt.NewRow();
            dr.setValue("name", attr.getKey());
            dr.setValue("text", wk.GetValByKey(attr.getKey()));
            dr.setValue("type", "text");
            dt.Rows.add(dr);
        }
        return Json.ToJson(dt);

    }


    public final String DearFileName(String fileName) {
        fileName = DearFileNameExt(fileName, "+", "%2B");
        fileName = DearFileNameExt(fileName, " ", "%20");
        fileName = DearFileNameExt(fileName, "/", "%2F");
        fileName = DearFileNameExt(fileName, "?", "%3F");
        fileName = DearFileNameExt(fileName, "%", "%25");
        fileName = DearFileNameExt(fileName, "#", "%23");
        fileName = DearFileNameExt(fileName, "&", "%26");
        fileName = DearFileNameExt(fileName, "=", "%3D");
        return fileName;
    }

    private void CreateDocFile(String filePath) {
        //Spire.Doc.Document doc = new Spire.Doc.Document();

        //doc.SaveToFile(filePath, Spire.Doc.FileFormat.Docx2013);
    }

    public final String DearFileNameExt(String fileName, String val, String replVal) {
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        return fileName;
    }

    ///#endregion 附件组件


    ///#region 必须传递参数

    /**
     * 执行的内容
     */
    public final String getDoWhat() {
        String str = this.GetRequestVal("DoWhat");
        if (DataType.IsNullOrEmpty(str)) {
            return "Frm";
        }
        return str;
    }

    /**
     * 当前的用户
     */
    public final String getUserNo() {
        return this.GetRequestVal("UserNo");
    }

    /**
     * 用户的安全校验码(请参考集成章节)
     */
    public final String getSID() {
        return this.GetRequestVal("Token");
    }

    public final String getAppPath() {
        return Glo.getCCFlowAppPath();
    }

    public final String Port_Init() throws Exception {

        ///#region 安全性校验.
        if (this.getUserNo() == null || this.getSID() == null || this.getDoWhat() == null || this.getFrmID() == null) {
            return "err@必要的参数没有传入，请参考接口规则。";
        }

        if (Dev2Interface.Port_CheckUserLogin(this.getUserNo(), this.getSID()) == false) {
            return "err@非法的访问，请与管理员联系。SID=" + this.getSID();
        }

        if (!Objects.equals(WebUser.getNo(), this.getUserNo())) {
            Dev2Interface.Port_SigOut();
            Dev2Interface.Port_Login(this.getUserNo());
        }


        ///#endregion 安全性校验.


        ///#region 生成参数串.
        String paras = "";
        for (String str : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
            String val = this.GetRequestVal(str);
            if (val.indexOf('@') != -1) {
                throw new RuntimeException("您没有能参数: [ " + str + " ," + val + " ] 给值 ，URL 将不能被执行。");
            }
            switch (str.toLowerCase()) {
                case "fk_mapdata":
                case "workid":
                case "fk_node":
                case "Token":
                    break;
                default:
                    paras += "&" + str + "=" + val;
                    break;
            }
        }

        ///#endregion 生成参数串.

        String url = "";
        switch (this.getDoWhat()) {
            case "Frm": //如果是调用Frm的查看界面.
                url = "Frm.htm?FK_MapData=" + this.getFrmID() + "&OID=" + this.getOID() + paras;
                break;
            case "Search": //调用查询界面.
                url = "../Comm/Search.htm?EnsName=" + this.getFrmID() + paras;
                break;
            case "Group": //分组分析界面.
                url = "../Comm/Group.htm?EnsName=" + this.getFrmID() + paras;
                break;
            default:
                break;
        }
        return "url@" + url;
    }

    ///#endregion


    ///#region 前台SQL转移处理
    public final String RunSQL_Init() {
        String sql = GetRequestVal("SQL");
        DBAccess.RunSQLReturnTable(sql);
        DataTable dt = new DataTable();
        return Json.ToJson(dt);
    }

    ///#endregion


    ///#region 表单数据版本比对

    public final String FrmDB_DoCompare() throws Exception {
        FrmDBVer ver = new FrmDBVer(this.getMyPK());
        String mainData = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", this.getMyPK(), "FrmDB");
        DataTable dt = Json.ToDataTable(mainData);
        dt.TableName = "mainData";
        MapDtls dtls = new MapDtls(ver.getFrmID());
        MapData md = new MapData(ver.getFrmID());
        DataSet ds = new DataSet();
        ds.Tables.add(dt);
        ds.Tables.add(ver.ToDataTableField("Sys_FrmDBVer"));
        ds.Tables.add(dtls.ToDataTableField("Sys_MapDtl"));
        ds.Tables.add(md.ToDataTableField("Sys_MapData"));
        return Json.ToJson(ds);
    }

    public final String FrmDBVerAndRemark_Init() throws Exception {
        DataSet ds = new DataSet();
        String field = GetRequestVal("Field");
        int fieldType = GetRequestValInt("FieldType"); // 0 表单字段 1从表
        String frmID = this.getFrmID();
        String rfrmID = this.GetRequestVal("RFrmID");
        //文本字段
        if (fieldType == 0) {
            FrmDBRemarks remarks = new FrmDBRemarks();
            remarks.Retrieve(FrmDBRemarkAttr.FrmID, rfrmID, FrmDBRemarkAttr.Field, field, FrmDBRemarkAttr.RefPKVal, this.getRefPKVal(), FrmDBRemarkAttr.RDT);
            ds.Tables.add(remarks.ToDataTableField("Sys_FrmDBRemark"));
            FrmDBVers vers = new FrmDBVers();
            vers.Retrieve(FrmDBVerAttr.FrmID, rfrmID, FrmDBVerAttr.RefPKVal, this.getRefPKVal(), FrmDBVerAttr.RDT);

            for (FrmDBVer ver : vers.ToJavaList()) {
                String json = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", ver.getMyPK(), "FrmDB");
                if (DataType.IsNullOrEmpty(json) == true) {
                    ver.setTrackID("");
                    continue;
                }

                DataTable dt = Json.ToDataTable(json);
                ver.setTrackID(dt.Rows.get(0).getValue(field).toString());
            }

            ds.Tables.add(vers.ToDataTableField("Sys_FrmDBVer"));
            return Json.ToJson(ds);
        }

        return FrmDB_DtlCompare(field, rfrmID);
        //return BP.Tools.Json.ToJson(ds);
    }

    /**
     * 从表数据的比对
     *
     * @return
     */
    public final String FrmDB_DtlCompare(String mapDtl, String rfrmID) throws Exception {
        DataSet myds = new DataSet();

        ///#region 加载从表表单模版信息.
        MapDtl dtl = new MapDtl(mapDtl);
        DataTable Sys_MapDtl = dtl.ToDataTableField("Sys_MapDtl");
        myds.Tables.add(Sys_MapDtl);

        //明细表的表单描述
        MapAttrs attrs = dtl.getMapAttrs();
        DataTable Sys_MapAttr = attrs.ToDataTableField("Sys_MapAttr");
        myds.Tables.add(Sys_MapAttr);

        //明细表的配置信息.
        DataTable Sys_MapExt = dtl.getMapExts().ToDataTableField("Sys_MapExt");
        myds.Tables.add(Sys_MapExt);

        //启用附件，增加附件信息
        DataTable Sys_FrmAttachment = dtl.getFrmAttachments().ToDataTableField("Sys_FrmAttachment");
        myds.Tables.add(Sys_FrmAttachment);

        ///#endregion 加载从表表单模版信息.


        ///#region  把从表的数据放入.
        FrmDBVers vers = new FrmDBVers();
        vers.Retrieve(FrmDBVerAttr.FrmID, rfrmID, FrmDBVerAttr.RefPKVal, this.getRefPKVal(), FrmDBVerAttr.RDT);

        for (FrmDBVer ver : vers.ToJavaList()) {
            String json = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", ver.getMyPK(), "FrmDtlDB");
            ver.setTrackID(json);
        }
        myds.Tables.add(vers.ToDataTableField("Sys_FrmDBVer"));


        ///#endregion  把从表的数据放入.
        return Json.ToJson(myds);
    }

    ///#endregion 表单数据版本比对


    ///#region 章节表单.
    public final String ChapterFrm_Init() throws Exception {
        MapData md = new MapData(this.getFrmID());
        DataSet ds = bp.sys.CCFormAPI.GenerHisDataSet(this.getFrmID(), null, null);
        GEEntityOID en = new GEEntityOID(this.getFrmID());
        en.setOID(this.getOID());
        if (en.getOID() == 0) {
            en.ResetDefaultVal(null, null, 0);
        }
        if (en.RetrieveFromDBSources() == 0) {
            en.InsertAsOID(this.getOID());
        }

        GroupFields gfs = md.getGroupFields();

        MapAttrs attrs = md.getMapAttrs();


        //获取外键下拉
        DataTable ddlTable = new DataTable();
        ddlTable.Columns.Add("No");
        MapExts mes = md.getMapExts();
        MapExt me = null;
        for (MapAttr attr : attrs.ToJavaList()) {
            FieldTypeS lgType = attr.getLGType();
            String uiBindKey = attr.getUIBindKey();

            if (DataType.IsNullOrEmpty(uiBindKey) == true) {
                continue; //为空就continue.
            }

            if (lgType == FieldTypeS.Enum) {
                continue; //枚举值就continue;
            }
            if (attr.getUIVisible() == false) {
                continue;
            }

            boolean uiIsEnable = attr.getUIIsEnable();
            if (uiIsEnable == false && lgType == FieldTypeS.Enum) {
                continue; //如果是外键，并且是不可以编辑的状态.
            }

            if (uiIsEnable == false && lgType == FieldTypeS.Normal) {
                continue; //如果是外部数据源，并且是不可以编辑的状态.
            }

            // 检查是否有下拉框自动填充。
            String keyOfEn = attr.getKeyOfEn();
            String fk_mapData = attr.getFrmID();


            ///#region 处理下拉框数据范围. for 小杨.
            Object tempVar = mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL, MapExtAttr.AttrOfOper, keyOfEn);
            me = tempVar instanceof MapExt ? (MapExt) tempVar : null;
            if (me != null) {
                Object tempVar2 = me.getDoc();
                String fullSQL = tempVar2 instanceof String ? (String) tempVar2 : null;
                fullSQL = fullSQL.replace("~", ",");
                fullSQL = Glo.DealExp(fullSQL, en, null);
                DataTable dt = DBAccess.RunSQLReturnTable(fullSQL);
                if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None) {
                    String columnName = "";
                    for (DataColumn col : dt.Columns) {
                        columnName = col.ColumnName.toUpperCase();
                        switch (columnName) {
                            case "NO":
                                col.ColumnName = "No";
                                break;
                            case "NAME":
                                col.ColumnName = "Name";
                                break;
                            case "PARENTNO":
                                col.ColumnName = "ParentNo";
                                break;
                        }
                    }
                }

                dt.TableName = keyOfEn; //可能存在隐患，如果多个字段，绑定同一个表，就存在这样的问题.
                ds.Tables.add(dt);
                continue;
            }

            ///#endregion 处理下拉框数据范围.

            // 判断是否存在.
            if (ds.Tables.contains(uiBindKey) == true) {
                continue;
            }

            DataTable dataTable = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null);

            if (dataTable != null) {
                ds.Tables.add(dataTable);
            } else {
                DataRow ddldr = ddlTable.NewRow();
                ddldr.setValue("No", uiBindKey);
                ddlTable.Rows.add(ddldr);
            }
        }
        ddlTable.TableName = "UIBindKey";
        ds.Tables.add(ddlTable);

        ///#region 获得数据，是否打勾？
        //获得已经有数据的字段.
        String ptable = en.getEnMap().getPhysicsTable();
        String atparas = DBAccess.RunSQLReturnString("SELECT AtPara FROM " + ptable + " WHERE OID=" + String.valueOf(this.getOID()));
        AtPara ap = new AtPara(atparas);
        String fileds = ap.GetValStrByKey("ChapterFrmSaveFields");
        if (DataType.IsNullOrEmpty(fileds) == false) {
            //增加星号标记.
            for (MapAttr attr : attrs.ToJavaList()) {
                if (fileds.contains("," + attr.getKeyOfEn() + ",") == true) {
                    attr.SetPara("IsStar", "1");
                }
            }

            //为分组字段设置 IsStar 标记.  标记该分组下，是否所有的字段都已经填写完毕?
            for (GroupField gf : gfs.ToJavaList()) {
                //章节表单Attr
                if (gf.getCtrlType().equals("Attr") == true) {
                    Object tempVar3 = attrs.GetEntitiesByKey("GroupID", String.valueOf(gf.getOID()));
                    MapAttrs newAttrs = tempVar3 instanceof MapAttrs ? (MapAttrs) tempVar3 : null;
                    int blankNum = 0;
                    if (newAttrs != null) {
                        for (MapAttr item : newAttrs.ToJavaList()) {
                            if (item.getUIIsInput() == true) {
                                String val = en.GetValStrByKey(item.getKeyOfEn());
                                if (DataType.IsNullOrEmpty(val) == true) {
                                    blankNum++;
                                }
                            }
                        }
                    }

                    if (blankNum == 0) {
                        gf.SetPara("IsStar", "1");
                        continue;
                    }
                }

                //判断是否是从表? 取从表的数量.
                if (gf.getCtrlType().equals("Dtl") == true) {
                    GEEntity geen = new GEEntity(gf.getCtrlID());
                    try {
                        String sql = "SELECT count(*) as num FROM " + geen.getEnMap().getPhysicsTable() + " WHERE refpk='" + this.getWorkID() + "'";
                        if (DBAccess.RunSQLReturnValInt(sql) > 0) {
                            gf.SetPara("IsStar", "1");
                            continue;
                        }
                    } catch (RuntimeException ex) {
                        geen.CheckPhysicsTable();
                        gf.SetPara("IsStar", "0");
                        continue;
                    }
                }

                //是否是附件？
                if (gf.getCtrlType().equals("Ath") == true) {
                    String sql = "SELECT COUNT(*) AS NUM FROM Sys_FrmattachmentDB WHERE RefPKVal=" + this.getWorkID() + " ";
                    if (DBAccess.RunSQLReturnValInt(sql) > 0) {
                        gf.SetPara("IsStar", "1");
                        continue;
                    }
                }

                //表单链接, 检查是否有空白项？
                if (gf.getCtrlType().equals("ChapterFrmLinkFrm") == true) {
                    GEEntityOID ge = new GEEntityOID(gf.getCtrlID());
                    ge.setOID(this.getWorkID());
                    if (ge.RetrieveFromDBSources() == 1) {
                        int blankNum = 0;
                        MapAttrs attr1s = new MapAttrs(gf.getCtrlID());
                        for (MapAttr item : attr1s.ToJavaList()) {
                            if (item.getUIIsInput() == true) {
                                String val = ge.GetValStrByKey(item.getKeyOfEn());
                                if (DataType.IsNullOrEmpty(val) == true) {
                                    blankNum++;
                                }
                            }
                        }
                        if (blankNum == 0) {
                            gf.SetPara("IsStar", "1");
                            continue;
                        }
                    }
                }


                //判断字段.
                if (DataType.IsNullOrEmpty(gf.getCtrlType()) == false) {
                    continue;
                }

                //是否有未填写的字段？
                boolean isHaveBlank = false;
                for (MapAttr attr : attrs.ToJavaList()) {
                    if (attr.getGroupID() != gf.getOID()) {
                        continue;
                    }
                    if (attr.getMyDataType() != DataType.AppString || attr.getUIVisible() == false) {
                        continue;
                    }
                    //if (attr.MaxLen < 2000)
                    //    continue;
                    if (fileds.contains(attr.getKeyOfEn() + ",") == false) {
                        isHaveBlank = true;
                    }
                }
                if (isHaveBlank == false) {
                    gf.SetPara("IsStar", "1");
                }
            }
        }

        ///#endregion 获得数据，是否打勾？

        //组装数据，返回出去.

        //ds.Tables.add(gfs.ToDataTableField("GroupFields"));
        DataTable mainTable = en.ToDataTableField("MainTable");
        ds.Tables.add(mainTable);
        ds.Tables.remove("Sys_GroupField");
        ds.Tables.remove("Sys_MapAttr");
        ds.Tables.add(attrs.ToDataTableField("Sys_MapAttr"));
        ds.Tables.add(gfs.ToDataTableField("GroupFields"));
        GenerWorkFlow gwf = new GenerWorkFlow(this.getOID());
        ds.Tables.add(gwf.ToDataTableField("WF_GenerWorkFlow"));
        // ds.WriteXml("d:\\xxx.xml");

        return Json.ToJson(ds);
    }

    public final String ChapterFrm_AttrInit() throws Exception {
        GEEntityOID en = new GEEntityOID(this.getFrmID());
        en.setOID(this.getOID());
        en.Retrieve();
        return en.ToJson(true);
    }

    /**
     * 检查目录是否可以加星.
     *
     * @return
     */
    public final String ChapterFrm_CheckGroupFieldStar() throws Exception {
        GroupField gf = new GroupField(this.getGroupField());

        //判断是否是从表? 取从表的数量.
        if (gf.getCtrlType().equals("Dtl") == true) {
            GEEntity geen = new GEEntity(gf.getCtrlID());
            String sql = "SELECT count(*) as num FROM " + geen.getEnMap().getPhysicsTable() + " WHERE refpk='" + this.getWorkID() + "'";
            if (DBAccess.RunSQLReturnValInt(sql) > 0) {
                return "1";
            }
            return "0";
        }

        //是否是附件？
        if (gf.getCtrlType().equals("Ath") == true) {
            String sql = "SELECT COUNT(*) AS NUM FROM Sys_FrmattachmentDB WHERE RefPKVal=" + this.getWorkID() + " ";
            if (DBAccess.RunSQLReturnValInt(sql) > 0) {
                return "1";
            }
            return "0";
        }

        //表单链接, 检查是否有空白项？
        if (gf.getCtrlType().equals("ChapterFrmLinkFrm") == true) {
            GEEntityOID ge = new GEEntityOID(gf.getCtrlID());
            ge.setOID(this.getWorkID());
            if (ge.RetrieveFromDBSources() == 1) {
                int blankNum = 0;
                MapAttrs attr1s = new MapAttrs(gf.getCtrlID());
                for (MapAttr item : attr1s.ToJavaList()) {
                    if (item.getUIIsInput() == true) {
                        String val = ge.GetValStrByKey(item.getKeyOfEn());
                        if (DataType.IsNullOrEmpty(val) == true) {
                            blankNum++;
                        }
                    }
                }
                if (blankNum == 0) {
                    return "1";
                }
            }
            return "0";
        }
        //章节表单Attr
        if (gf.getCtrlType().equals("Attr") == true) {
            GEEntityOID ge = new GEEntityOID(gf.getCtrlID());
            ge.setOID(this.getWorkID());
            if (ge.RetrieveFromDBSources() == 1) {
                int blankNum = 0;
                MapAttrs attr1s = new MapAttrs();
                attr1s.Retrieve(MapAttrAttr.FK_MapData, gf.getCtrlID(), MapAttrAttr.GroupID, gf.getOID(), null);
                for (MapAttr item : attr1s.ToJavaList()) {
                    if (item.getUIIsInput() == true) {
                        String val = ge.GetValStrByKey(item.getKeyOfEn());
                        if (DataType.IsNullOrEmpty(val) == true) {
                            blankNum++;
                        }
                    }
                }
                if (blankNum == 0) {
                    return "1";
                }
            }
            return "0";
        }
        return "err@没有判断." + gf.ToJson(true);
    }


    public final String ChapterFrm_InitOneField() throws Exception {
        if (DataType.IsNullOrEmpty(this.getKeyOfEn()) == true) {
            return "err@没有传来字段KeyOfEn的值.";
        }

        String ptable = DBAccess.RunSQLReturnString("SELECT PTable FROM Sys_MapData WHERE No='" + this.getFrmID() + "'", null);
        if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle) {
            return DBAccess.RunSQLReturnStringIsNull("SELECT " + this.getKeyOfEn() + " From " + ptable + " WHERE OID=" + this.getOID(), "");
        }

        return DBAccess.GetBigTextFromDB(ptable, "OID", String.valueOf(this.getOID()), this.getKeyOfEn());
    }

    public final String ChapterFrm_SaveOneField() throws Exception {
        String ptable = DBAccess.RunSQLReturnString("SELECT PTable FROM Sys_MapData WHERE No='" + this.getFrmID() + "'", null);
        String vals = this.getVals();
        if (vals == null) {
            vals = "";
        }

        try {
            if (SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle) {
                try {
                    Paras ps = new Paras();
                    ps.SQL = "UPDATE " + ptable + " SET " + this.getKeyOfEn() + "=" + SystemConfig.getAppCenterDBVarStr() + "KeyOfEn WHERE OID=" + this.getOID();
                    ps.Add("KeyOfEn", vals, false);
                    DBAccess.RunSQL(ps);
                } catch (RuntimeException ex) {
                    if (ex.getMessage().contains("的值太大") == true) {
                        //更改当前字段的长度
                        DBAccess.RunSQL("ALTER table " + ptable + " modify " + this.getKeyOfEn() + " VARCHAR2(4000)");
                        Paras ps = new Paras();
                        ps.SQL = "UPDATE " + ptable + " SET " + this.getKeyOfEn() + "=" + SystemConfig.getAppCenterDBVarStr() + "KeyOfEn WHERE OID=" + this.getOID();
                        ps.Add("KeyOfEn", vals, false);
                        DBAccess.RunSQL(ps);
                    }
                }

            } else {
                DBAccess.SaveBigTextToDB(vals, ptable, "OID", String.valueOf(this.getOID()), this.getKeyOfEn());
            }
            String atparas = DBAccess.RunSQLReturnStringIsNull("SELECT AtPara FROM " + ptable + " WHERE OID=" + String.valueOf(this.getOID()), "");

            //标记该字段已经完成.
            if (atparas.contains("," + this.getKeyOfEn() + ",") == false) {
                AtPara ap = new AtPara(atparas);
                String val = ap.GetValStrByKey("ChapterFrmSaveFields");

                if (vals.length() == 0) {
                    val = val.replace("," + this.getKeyOfEn() + ",", "");
                } else {
                    val += "," + this.getKeyOfEn() + ",";
                }

                ap.SetVal("ChapterFrmSaveFields", val);

                String strs = ap.GenerAtParaStrs();
                int i = DBAccess.RunSQL("UPDATE " + ptable + " SET AtPara='" + strs + "' WHERE OID=" + this.getOID());
                if (i == 0) {
                    return "err@保存章节字段出现错误，行数据OID=" + this.getOID() + ",不存在.";
                }

                if (this.getVals().length() == 0) {
                    return "0"; //去掉star.
                }
                return "1";
            } else {
                //如果存在，并且值为null. qudiao.
                if (Objects.equals(vals, "")) {
                    atparas = atparas.replace("," + this.getKeyOfEn() + ",", "");
                    int i = DBAccess.RunSQL("UPDATE " + ptable + " SET AtPara='" + atparas + "' WHERE OID=" + this.getOID());
                    if (i == 0) {
                        return "err@保存章节字段出现错误，行数据OID=" + this.getOID() + ",不存在.";
                    }
                    return "0";
                }
            }
            return "1"; // 增加star.
        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("ta too long for column") == true || ex.getMessage().contains("太长") == true) {
                DBAccess.DropTableColumn(this.getFrmID(), this.getKeyOfEn());
                DBAccess.SaveBigTextToDB(this.getVals(), this.getFrmID(), "OID", String.valueOf(this.getOID()), this.getKeyOfEn());
                return "保存成功.";
            }
            return ex.getMessage();
        }
    }

    public final String ChapterFrm_SaveAttr() throws Exception {
        //获取表单数据
        Hashtable ht = bp.pub.PubClass.GetMainTableHT();
        MapAttrs attrs = new MapAttrs();
        attrs.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.GroupID, this.GetRequestValInt("GroupID"), null);

        GEEntity ge = new GEEntity(this.getFrmID(), this.getPKVal());
        for (MapAttr attr : attrs.ToJavaList()) {
            String val = ht.get(attr.getKeyOfEn()) == null ? "" : ht.get(attr.getKeyOfEn()).toString();
            ge.SetValByKey(attr.getKeyOfEn(), val);
        }
        ge.Update();
        return "保存成功";
    }

    /**
     * 根据版本号获取表单的历史数据
     *
     * @return
     */
    public final String ChartFrm_GetBigTextByVer() throws Exception {
        String dbstr = SystemConfig.getAppCenterDBVarStr();
        String sql = "SELECT MyPK FROM Sys_FrmDBVer WHERE FrmID=" + dbstr + "FrmID AND RefPKVal=" + dbstr + "RefPKVal AND Ver=" + dbstr + "Ver";
        Paras ps = new Paras();
        ps.SQL = sql;
        ps.Add("FrmID", this.getFrmID(), false);
        ps.Add("RefPKVal", this.getOID());
        ps.Add("Ver", GetRequestVal("DBVer"), false);
        String mypk = DBAccess.RunSQLReturnStringIsNull(ps, "");
        if (DataType.IsNullOrEmpty(mypk) == true) {
            return "err@获取表单数据版本失败";
        }
        return DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", mypk, "FrmDB");
    }

    public final String ChartFrm_GetDtlDataByVer() throws Exception {
        return DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", this.getMyPK(), "FrmDtlDB");
    }

    ///#endregion 章节表单.


}
