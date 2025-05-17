package cn.jflow.boot.controller;

import bp.App.ShuCai.*;
import bp.da.*;
import bp.difference.SystemConfig;
import bp.difference.handler.HttpHandlerBase;
import bp.en.*;
import bp.port.*;
import bp.port.Glo;
import bp.sys.*;
import bp.web.WebUser;
import bp.wf.*;
import bp.wf.httphandler.WF_Comm;
import bp.wf.template.NodeAttr;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.bytecode.stackmap.BasicBlock;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Api(tags="海南图斑信息相关接口")
@RequestMapping(value = "/WF/FRECKLE")
public class FreckleController extends HttpHandlerBase {

    public static  String FileExts="jpg,jpeg,png";
    public static Object Return_Info(int code, String msg, Object data)
    {
        Hashtable ht = new Hashtable();
        ht.put("code", code);
        ht.put("msg", msg);
        ht.put("detailinfo", data);
        return ht;
    }
    @GetMapping(value = "/SplitFreckle")
    @ApiOperation("拆分蔬菜基本图斑")
    @ResponseBody
    public Object SplitFreckle(String no,int splitCount,String geometry, String token) {
        try {
            //判断参数是否为空
            if (DataType.IsNullOrEmpty(no) || splitCount <= 0 || DataType.IsNullOrEmpty(token)) {
                return Return_Info(500, "参数不能为空", "");
            }
            Dev2Interface.Port_LoginByToken(token);

            boolean ishave=false;
            //首先判断当前人员是否有权限发起
            String sql1 = "SELECT A.FK_Flow FROM WF_Node A, WF_NodeStation B, Port_DeptEmpStation C";
            sql1 += " WHERE  A.NodePosType=0  AND A.NodeID=B.FK_Node AND B.FK_Station=C.FK_Station ";
            sql1 += "  AND (A.DeliveryWay=0 OR A.DeliveryWay=14) ";
            sql1 += "  AND C.FK_Emp='" + WebUser.getNo() + "'";
            DataTable dt=DBAccess.RunSQLReturnTable(sql1);
            for(DataRow dr:dt.Rows){
                String  flowNo= String.valueOf(dr.getValue("FK_Flow"));
                if("010".equals(flowNo)){
                    ishave=true;
                }
            }

            if (!ishave) {
                return Return_Info(500, "非市县填报员角色不能核查", "");
            }

            BasicInfo basicInfo=new BasicInfo();
            int count=basicInfo.Retrieve("No",no);
            if(count!=1){
                return Return_Info(500, "此摸排基本数据编号数据重复或不存在", "");
            }
            if(basicInfo.getISHC()==1){
                return Return_Info(500, "此摸排基本数据编号不允许重复发起核查", "");
            }

            HeChaQiShus heChaQiShus=new HeChaQiShus();
            heChaQiShus.RetrieveAll();
            ArrayList<HeChaQiShu> arrayList=heChaQiShus.Tolist();

            String sql="SELECT COALESCE(( MAX(batchnum) ),0) as maxbatchnum  FROM ND10Rpt where SCJDTBBH='"+no+"'";
            String maxBachNumStr=DBAccess.RunSQLReturnString(sql);
            int maxBachNum=Integer.parseInt(maxBachNumStr)+1;
            long oid=0;
            for(int i=0;i<splitCount;i++){

                Hashtable ht=new Hashtable();
                String sqlObjectId="SELECT COALESCE(( MAX(objectid) ),0) as objectid  FROM nd10rpt ";
                int maxObjectId=DBAccess.RunSQLReturnValInt(sqlObjectId)+1;
                ht.put(ND10RptAttr.ObjectId.toLowerCase(),maxObjectId);

                long workid = Dev2Interface.Node_CreateBlankWork("010", ht);
                oid=workid;
                //设置为草稿状态
                Dev2Interface.Node_SetDraft(workid);
                ND10Rpt nd10Rpt=new ND10Rpt();
                nd10Rpt.setOID(workid);
                nd10Rpt.Retrieve();
                nd10Rpt.setSCJDTBBH(no);
                nd10Rpt.setBatchNum(maxBachNum+i);
                nd10Rpt.setJDBH(basicInfo.getJDBH());
                nd10Rpt.setJDMC(basicInfo.getJDMC());
                nd10Rpt.setJDTBMJ(basicInfo.getJDMJ());
                nd10Rpt.setHCTBMJ(basicInfo.getTXMJ());
                nd10Rpt.setHCTBBH(no+"_"+String.format("%04d", nd10Rpt.getBatchNum()));
                nd10Rpt.setSQL_HeChaQiShu(basicInfo.getHCQS());
                Optional<HeChaQiShu> optional=arrayList.stream().filter(m->{
                   return m.GetValStrByKey("No").equals(basicInfo.getHCQS());
                }).findFirst();
                optional.ifPresent(m->{
                    nd10Rpt.setSQL_HeChaQiShuT(m.getName());
                });

                nd10Rpt.setGEOMETRY(geometry);
                nd10Rpt.setSQL_ShiQuXian(basicInfo.getQuXianNo());
                nd10Rpt.setSQL_ShiQuXianT(basicInfo.getQuXianNoT());
                nd10Rpt.setSQL_XiangZhen(basicInfo.getXiangZhenNo());
                nd10Rpt.setSQL_XiangZhenT(basicInfo.getXiangZhenNoT());
                nd10Rpt.setSHuoShuSX(basicInfo.getSHuoShuSX());
                nd10Rpt.setDKSta(0);

                //更新图形服务字段
                nd10Rpt.Update();
                if(DataType.IsNullOrEmpty(geometry)){
                    String sqlShape="UPDATE nd10rpt SET nd10rpt.shape = gis_basicinfo.shape FROM gis_basicinfo WHERE  " +
                            "nd10rpt.scjdtbbh=gis_basicinfo.no and  gis_basicinfo.no='"+no+"' and nd10rpt.oid = '"+oid+"';";
                    String updateSJYXBS="UPDATE  nd10rpt SET sjyxbs='1' where oid='"+oid+"';";
                    DBAccess.RunSQL(sqlShape+updateSJYXBS);
                }else{
                }
            }
            //核查中
            basicInfo.setISHC(1);
            basicInfo.Update();
            return Return_Info(200, "成功",oid);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Return_Info(500, ex.getMessage(), "");
        }
    }

    @GetMapping(value = "/FreckleWorkShift")
    @ApiOperation("核查图斑工作自动移交")
    @ResponseBody
    public Object FreckleWorkShift(long oid, String token) {

        try {
            if (DataType.IsNullOrEmpty(token)) {
                return Return_Info(500, "参数不能为空", "");
            }
            String returnMsg = "";
            Dev2Interface.Port_LoginByToken(token);
            String toEmp=WebUser.getNo();

            String currentDeptStationEmpsSql="SELECT  fk_emp as No,name as Name  FROM " +
                    "(select dc.no,dc.city_code,des.fk_emp,emp.name  from  gk_deptcity dc  INNER  JOIN  " +
                    "port_deptempstation des on  dc.no=des.fk_dept INNER JOIN port_emp emp on emp.no=des.fk_emp and " +
                    "des.fk_station='3032-414103-106912') as newT WHERE newT.city_code=(select city_code from " +
                    "gk_deptcity where no='"+WebUser.getDeptNo()+"')";
            DataTable dt=DBAccess.RunSQLReturnTable(currentDeptStationEmpsSql);
            if(dt.Rows.size()==0){
                returnMsg="未找到相关移交人员";
            }

            boolean isEmpHave=false;
            for (DataRow dr : dt.Rows)
            {
                String empNo = dr.getValue("No").toString();
                if(toEmp.equals(empNo)){
                    isEmpHave=true;
                }
            }
            if(!isEmpHave){
                return Return_Info(200, returnMsg, "");
            }

            GenerWorkFlow generWorkFlow = new GenerWorkFlow();
            generWorkFlow.Retrieve(GenerWorkFlowAttr.WorkID, oid);

            if (generWorkFlow.getNodeID() == 1001 && !generWorkFlow.getTodoEmps().contains(toEmp+",")) {
                returnMsg = Dev2Interface.Node_Shift(generWorkFlow.getStarter(), oid, toEmp, "自动移交");
                String sql="UPDATE wf_generworkflow set Starter='%s',StarterName='%s' where workid='%s';";

                String deleteSql="DELETE from ND10Track where WorkID='"+oid+"' and ndfrom='1001' and ACTIONTYPE=3;";
                DBAccess.RunSQL(deleteSql+String.format(sql,toEmp,WebUser.getName(),oid));
            }
            if(returnMsg.contains("err")){
                return Return_Info(500, returnMsg, "");
            }else {

                return Return_Info(200, returnMsg, "");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return Return_Info(500, ex.getMessage(), "");
        }
    }

    @GetMapping(value = "GetFreckleBasicInfo")
    @ApiOperation("获取摸排基本信息")
    @ResponseBody
    public Object GetFreckleBasicInfo(String no, String token) {
        try {
            //判断参数是否为空
            if (DataType.IsNullOrEmpty(no) || DataType.IsNullOrEmpty(token)) {
                return Return_Info(500, "参数不能为空", "");
            }
            Dev2Interface.Port_LoginByToken(token);
            BasicInfo basicInfo = new BasicInfo();
            int count = basicInfo.Retrieve("No", no);
            if (count != 1) {
                return Return_Info(500, "此摸排基本信息编号数据重复或不存在", "");
            }

            DataTable dataTable = basicInfo.ToDataTableField();

            ArrayList hashtableArrayList=TableInfoArrayList(basicInfo);

            ArrayList detailIntoList = new ArrayList<>();
            Hashtable hashtable = DetailInFoHT("jbxx", "基本信息", "0", hashtableArrayList);
            detailIntoList.add(hashtable);

            return Return_Info(200, "成功", detailIntoList);

        } catch (Exception ex) {
            ex.printStackTrace();
            return Return_Info(500, ex.getMessage(), "");
        }
    }

    @GetMapping(value = "GetFreckleCheckInfo")
    @ApiOperation("获取摸排核查信息")
    @ResponseBody
    public Object GetFreckleCheckInfo(String hctbbh, String token) throws Exception {

        try {


            //判断参数是否为空
            if (DataType.IsNullOrEmpty(hctbbh) || DataType.IsNullOrEmpty(token)) {
                return Return_Info(500, "参数不能为空", "");
            }

            ArrayList detailIntoList = new ArrayList<>();

            //表单中联动控件不显示的附件字段
            ArrayList<String> athKeyList = new ArrayList();

            //附件tableinfo
            ArrayList<Hashtable> hashtableAthArrayList = new ArrayList<>();

            Dev2Interface.Port_LoginByToken(token);

            ND10Rpt nd10Rpt = new ND10Rpt();
            int count = nd10Rpt.Retrieve(ND10RptAttr.HCTBBH, hctbbh);
            if (count != 1) {
                return Return_Info(500, "此核查图斑编号数据不存在", "");
            }

            long oid = nd10Rpt.getOID();

            BasicInfo basicInfo = new BasicInfo();
            int countJB = basicInfo.Retrieve("No", nd10Rpt.getSCJDTBBH());
            //摸排基本信息
            if (countJB == 1) {
                ArrayList arrayListJB = TableInfoArrayList(basicInfo);
                Hashtable hashtableJB = DetailInFoHT("jbxx", "基本信息", "0", arrayListJB);
                detailIntoList.add(hashtableJB);
            }

            //摸排核查信息
            ArrayList hashtableArrayList = TableInfoArrayList(nd10Rpt);
            Hashtable hashtableHC = DetailInFoHT("hcxx", "核查信息", "0", hashtableArrayList);
            detailIntoList.add(hashtableHC);

            //相关图片信息
            GenerWorkFlow gwf = new GenerWorkFlow();
            int gwfCount = gwf.Retrieve(GenerWorkFlowAttr.WorkID, oid);
            if (gwfCount == 1) {

                //查询表单数据
                Flow flow = new Flow(gwf.getFlowNo());
                String sql = "select  *  from " + flow.getPTable() + " where oid=" + gwf.getWorkID();
                DataTable dataTableFrm = DBAccess.RunSQLReturnTable(sql);

                Node node = new Node();
                int countNode = node.Retrieve(NodeAttr.NodeID, gwf.getNodeID());
                if (countNode == 1 && dataTableFrm.Rows.size() > 0) {
                    MapExts mapExts = new MapExts();
                    int countMapExt = mapExts.Retrieve(MapAttrAttr.FK_MapData, node.getNodeFrmID(), MapExtAttr.ExtType, "RBAction", MapExtAttr.DoWay, "1");
                    if (countMapExt > 0) {
                        for (MapExt mapExt : mapExts.ToJavaList()) {

                            Object keyValue = dataTableFrm.Rows.get(0).getValue(mapExt.getAttrOfOper());
                            if (DataType.IsNullOrEmpty(keyValue)) {
                                continue;
                            }
                            //查询是所有的联动控件
                            FrmRBs frmRBs = new FrmRBs();
                            int countFrmRB = frmRBs.Retrieve(MapAttrAttr.FK_MapData, node.getNodeFrmID(), FrmRBAttr.KeyOfEn, mapExt.getAttrOfOper(), FrmRBAttr.IntKey, keyValue);
                            if (countFrmRB > 0) {
                                for (FrmRB frmRB : frmRBs.ToJavaList()) {
                                    String[] strs = frmRB.getFieldsCfg().split("@");
                                    for (String str : strs) {
                                        if (DataType.IsNullOrEmpty(str)) {
                                            continue;
                                        }
                                        String[] strsField = str.split("=");
                                        //4代表不可见
                                        if (strsField[1].equals("4")) {
                                            athKeyList.add(strsField[0]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                List<String> list = athKeyList.stream().distinct().collect(Collectors.toList());

                //查询附件db信息
                FrmAttachmentDBs frmAttachmentDBs = new FrmAttachmentDBs();
                QueryObject qo = new QueryObject(frmAttachmentDBs);
                qo.AddWhere(FrmAttachmentDBAttr.FK_MapData, node.getNodeFrmID());
                qo.addAnd();
                qo.setSQL("(" + FrmAttachmentDBAttr.RefPKVal + " = '" + gwf.getWorkID() + "' )");
                if (list.stream().count() > 0) {
                    String sqlInWhere = Glo.GenerWhereInSQL(String.join(",", list));
                    qo.addAnd();
                    qo.AddWhere(FrmAttachmentDBAttr.FK_FrmAttachment, " NOT IN ", "(" + sqlInWhere + ")");
                }
                qo.DoQuery();
                for (FrmAttachmentDB frmAttachmentDB : frmAttachmentDBs.ToJavaList()) {
                    if (FileExts.contains(frmAttachmentDB.getFileExts().toLowerCase())) {
                        //经度
                        String Longitude = frmAttachmentDB.getatPara().GetValStrByKey("Longitude");
                        //纬度
                        String Latitude = frmAttachmentDB.getatPara().GetValStrByKey("Latitude");
                        Hashtable hashtable = new Hashtable();
                        hashtable = TableInfoHT(frmAttachmentDB.getFileName(), frmAttachmentDB.getFileFullName(), Longitude, Latitude, "", "", "", "");
                        hashtableAthArrayList.add(hashtable);
                    }
                }
                Hashtable hashtableZP = DetailInFoHT("zpxx", "照片信息", "1", hashtableAthArrayList);
                detailIntoList.add(hashtableZP);
            }
            return Return_Info(200, "成功", detailIntoList);
        } catch (Exception exception) {
            exception.printStackTrace();
            return Return_Info(500, exception.getMessage(), "");
        }
    }

    @PostMapping("/ImpEntityData_Done")
    @ApiOperation("导入摸排基本信息数据")
    @ResponseBody
    public Object ImpEntityData_Done(@RequestParam("file") MultipartFile file) throws Exception {

        if (DataType.IsNullOrEmpty(file)) {
            return Return_Info(500, "参数不能为空", "");
        }
        //Dev2Interface.Port_LoginByToken(token);
        //文件存放路径
        String filePath = SystemConfig.getPathOfTemp() + "/" + DBAccess.GenerGUID() + ".xls";

        File fileFullName = new File(filePath);

        DataType.getFileByBytes(file.getBytes(), filePath);

        //从excel里面获得数据表.
        DataTable dt = DBLoad.ReadExcelFileToDataTable(filePath, 0);
        if (fileFullName.exists() == true)
            fileFullName.delete();

        // 检查数据是否完整.
//        string errInfo="";
//        for (DataRow dr : dt.Rows) {
//            String no = String.valueOf(dr.getValue("图斑tr编号"));
//        }
//
//        if (DataType.IsNullOrEmpty(errInfo)==false)
//            return "err@数据不完整："+errInfo;

        //删除文件
        try {


            StringBuilder stringBuilder=new StringBuilder("err@");
            BasicInfo basicInfo = new BasicInfo();
            String sql="SELECT COALESCE(( MAX(objectid) ),0) as objectid  FROM gis_basicinfo ";
            int maxObjectId=DBAccess.RunSQLReturnValInt(sql)+1;

            int num=1;
            for (DataRow dr : dt.Rows) {
                try{
                    num++;
                    String no = String.valueOf(dr.getValue("图斑编号"));
                    basicInfo.setNo(no);
                    if(DataType.IsNullOrEmpty(no)){
                        stringBuilder.append("第"+num+"行:"+"图斑编号不能为空");
                        continue;
                    }
                    basicInfo.setTBBH(no);
                    int count = basicInfo.Retrieve(EntityNoAttr.No, no);

                    String hcqs = String.valueOf(dr.getValue("核查期数"));
                    basicInfo.setHCQS(hcqs);
                    basicInfo.setISHC(0);
                    basicInfo.SetValByKey(BasicInfoAttr.ISHCT,"否");
                    basicInfo.setDKSta(0);
                    basicInfo.SetValByKey(BasicInfoAttr.DKStaT,"未提交");

                    String shuoshuSX = String.valueOf(dr.getValue("所属市县"));

                    String quXianNo = String.valueOf(dr.getValue("行政区代码"));
                    basicInfo.SetValByKey(BasicInfoAttr.QuXianNo, quXianNo);
                    basicInfo.SetValByKey(BasicInfoAttr.SHuoShuSX,shuoshuSX);
                    if(DataType.IsNullOrEmpty(quXianNo)){
                        stringBuilder.append("第"+num+"行:"+"行政区代码不允许为空");
                        continue;
                    }

                    //0 省  1 市县 2 区 3 乡镇  citylevel
                    if(DataType.IsNullOrEmpty(quXianNo)){
                        stringBuilder.append("第"+num+"行:"+"查询不到此行政区代码数据");
                        continue;
                    }
                    String quXianNoT = String.valueOf(dr.getValue("行政区名称"));
                    basicInfo.SetValByKey(BasicInfoAttr.QuXianNoT, quXianNoT);

                    //String DBAccess.RunSQLReturnString("select  * from  sys_city where city_code ='460108'");
                    String xiangZhenNo = String.valueOf(dr.getValue("乡镇代码"));
                    basicInfo.SetValByKey(BasicInfoAttr.XiangZhenNo, xiangZhenNo);
                    if(DataType.IsNullOrEmpty(xiangZhenNo)){
                        stringBuilder.append("第"+num+"行:"+"乡镇编码不允许为空");
                        continue;
                    }

                    String xiangZhenNoT = String.valueOf(dr.getValue("乡镇名称"));
                    basicInfo.SetValByKey(BasicInfoAttr.XiangZhenNoT, xiangZhenNoT);

                    String sjly = String.valueOf(dr.getValue("数据来源"));
                    basicInfo.SetValByKey(BasicInfoAttr.SJLY, sjly);

                    String zzzl = String.valueOf(dr.getValue("种植种类"));
                    basicInfo.SetValByKey(BasicInfoAttr.ZZZL, zzzl);

                    String txmj = String.valueOf(dr.getValue("图形面积"));
                    basicInfo.SetValByKey(BasicInfoAttr.TXMJ, txmj);

                    float jdmj =toFloatInternal(String.valueOf(dr.getValue("基地面积")));
                    basicInfo.SetValByKey(BasicInfoAttr.JDMJ, jdmj);

                    float zzmjF =toFloatInternal(String.valueOf(dr.getValue("种植面积")));
                    basicInfo.SetValByKey(BasicInfoAttr.ZHMJ, zzmjF);

                    String jdmc = String.valueOf(dr.getValue("基地名称"));
                    basicInfo.SetValByKey(BasicInfoAttr.JDMC, jdmc);

                    String jdbh = String.valueOf(dr.getValue("基地编号"));
                    basicInfo.SetValByKey(BasicInfoAttr.JDBH, jdbh);

                    String jdfzr = String.valueOf(dr.getValue("基地负责人"));
                    basicInfo.SetValByKey(BasicInfoAttr.JDFZE, jdfzr);

                    String lxfs = String.valueOf(dr.getValue("联系方式"));
                    basicInfo.SetValByKey(BasicInfoAttr.LXFS, lxfs);

                    String jdzb = String.valueOf(dr.getValue("基地坐标"));
                    basicInfo.SetValByKey(BasicInfoAttr.JDZB, jdzb);

                    String tdsyqrjdh = String.valueOf(dr.getValue("土地所有权人及电话"));
                    basicInfo.SetValByKey(BasicInfoAttr.TDWYQRJDH, tdsyqrjdh);

                    String zgqrjdh = String.valueOf(dr.getValue("资格权人及电话"));
                    basicInfo.SetValByKey(BasicInfoAttr.ZGQRJDH, zgqrjdh);

                    String jyqrjdh = String.valueOf(dr.getValue("经营权人及电话"));
                    basicInfo.SetValByKey(BasicInfoAttr.JYQRJDH, jyqrjdh);

                    String zzrjdh = String.valueOf(dr.getValue("种植人及电话"));
                    basicInfo.SetValByKey(BasicInfoAttr.ZZRJDH, zzrjdh);

                    float cljh =toFloatInternal(String.valueOf(dr.getValue("产量计划")));
                    basicInfo.SetValByKey(BasicInfoAttr.CLJH, cljh);


                    if (count > 0) {
                        basicInfo.Update();
                    } else {
                        basicInfo.setOBJECTID(maxObjectId);
                        maxObjectId++;
                        basicInfo.Insert();
                    }
                }catch (Exception ex){
                    stringBuilder.append("第"+num+"行:"+ex.getMessage());
                }
                }
            if(stringBuilder.toString().length()>4){
                return Return_Info(200, "失败",stringBuilder.toString());
            }
            return Return_Info(200, "成功","");
        }catch (Exception ex){
            ex.printStackTrace();
            return Return_Info(500, ex.getMessage(), "");
        }

    }

    @GetMapping(value = "checkEmpStation")
    @ApiOperation("判断用户是否属于指定角色")
    @ResponseBody
    /***
     * 判断某个用户是否是指定的角色
     * @param emp
     * @param stationName
     * @param token
     * @return
     * @throws Exception
     */
    public Object checkEmpStation(String stationName, String token) throws Exception {

        try {

            if (DataType.IsNullOrEmpty(stationName) || DataType.IsNullOrEmpty(token)) {
                return Return_Info(500, "参数不能为空", "");
            }

            Dev2Interface.Port_LoginByToken(token);

            String sql = "select  s.No,s.Name  from  port_deptempstation des INNER JOIN    " +
                    "port_station s on des.fk_station=s.no and des.fk_emp='"+WebUser.getNo()+"' and s.no='3032-414103-106912'";
            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            boolean ishave = false;
            for (DataRow dr : dt.Rows) {
                String name = dr.getValue("Name").toString();
                if (!DataType.IsNullOrEmpty(name)&&name.contains(stationName)) {
                    ishave = true;
                }
            }
            return Return_Info(200, "成功", ishave);
        } catch (Exception ex) {
            return Return_Info(500, ex.getMessage(), "");
        }
    }

    /**
     *
     * @param oid
     * @param token
     * @return
     * @throws Exception
     */
    @GetMapping(value = "deletFlowInstanceData")
    @ApiOperation("删除流程实例数据")
    @ResponseBody
    public Object deletFlowInstanceData(long oid, String token) throws Exception {

        try {

            if (DataType.IsNullOrEmpty(token)) {
                return Return_Info(500, "参数不能为空", "");
            }

            Dev2Interface.Port_LoginByToken(token);

            ND10Rpt nd10Rpt = new ND10Rpt();
            nd10Rpt.setOID(oid);
            nd10Rpt.Retrieve();

            if (nd10Rpt.getDKSta() == 0) {
                Dev2Interface.Flow_DoDeleteFlowByReal(oid);
                String sql = "UPDATE gis_basicinfo set ishc=0 where no= '" + nd10Rpt.getSCJDTBBH() + "'";
                DBAccess.RunSQL(sql);
            }else{
                return Return_Info(500, "未提交状态下才能删除", "");
            }

            return Return_Info(200, "删除成功", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Return_Info(500, ex.getMessage(), "");
        }
    }

    /***
     * 同步数据
     * @param param
     * @return
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/synchronizeUserDataInfo")
    public Object synchronizeUserDataInfo(@RequestBody JSONObject param, String token) {

        try {

            if (DataType.IsNullOrEmpty(token)) {
                return Return_Info(500, "参数不能为空", "");
            }
            Dev2Interface.Port_LoginByToken(token);
            Log.DefaultLogWriteLine(LogType.Info, "海口用户角色权限数据维护接口参数：" + JSON.toJSONString(param));

            Map<String, Object> pmap = parseJSON2Map(param);
            System.out.println(param.toString());

            //处理部门
            deptsDeal(pmap);
            //处理用户
            usersDeal(pmap);

            //处理角色
            rolesDeal(pmap);

            //处理用户角色部门
            user_roles(pmap);

        } catch (Exception ex) {
            ex.printStackTrace();
            bp.da.Log.DebugWriteError(ex);
            return Return_Info(500, ex.getMessage(), "");
        }
        return Return_Info(200, "成功", "");

    }
    private void deptsDeal(Map<String, Object> pmap) throws Exception {

            if(!pmap.containsKey("depts") || !(pmap.get("depts") instanceof List)){
                return;
            }
            List<Map<String, Object>>list= (List<Map<String, Object>>) pmap.get("depts");
            for(Map<String, Object>m:list) {

                String dept_id = m.get("dept_id").toString();
                String del_flag = m.get("del_flag").toString();
                String parent_id = m.get("parent_id").toString();
                String dept_name = m.get("dept_name").toString();
                Dept dept = new Dept();
                dept.setNo(dept_id);
                //删除
                if (del_flag.equals("2")) {
                    dept.Delete();
                    //处理GK_DeptCity
                    Paras ps = new Paras();
                    ps.SQL = "update GK_DeptCity set del_flag=2  WHERE no=" + SystemConfig.getAppCenterDBVarStr() + "dept_id";
                    ps.Add("dept_id", dept_id);
                    bp.da.DBAccess.RunSQL(ps);
                    //处理部门用户表
                    DeptEmp deptEmp = new DeptEmp();
                    deptEmp.Delete("FK_Dept", dept_id);
                    //处理部门用户角色表
                    DeptEmpStation deptEmpStation = new DeptEmpStation();
                    deptEmpStation.Delete("FK_Dept", dept_id);

                } else {
                    int i = dept.RetrieveFromDBSources();
                    dept.setParentNo(parent_id);
                    dept.setName(dept_name);
                    //插入
                    if (i == 0) {
                        dept.Insert();
//                        Paras ps = new Paras();
//                        ps.SQL = "insert into GK_DeptCity(no,name,parentno,province_code,city_code,street_code,city_level,del_flag)" +
//                                " values(" +
//                                SystemConfig.getAppCenterDBVarStr() + "no," + SystemConfig.getAppCenterDBVarStr() + "name," +
//                                SystemConfig.getAppCenterDBVarStr() + "parentno," + SystemConfig.getAppCenterDBVarStr() + "province_code," +
//                                SystemConfig.getAppCenterDBVarStr() + "city_code," + SystemConfig.getAppCenterDBVarStr() + "street_code," +
//                                SystemConfig.getAppCenterDBVarStr() + "city_level," + SystemConfig.getAppCenterDBVarStr() + "del_flag" +
//                                ")";
//                        ps.Add("no", dept_id);
//                        ps.Add("name", dept_name);
//                        ps.Add("parentno", parent_id);
//                        ps.Add("del_flag", 0);
//                        bp.da.DBAccess.RunSQL(ps);
                    } else {
                        dept.Update();
                        //Paras ps = new Paras();
//                        ps.SQL = "update GK_DeptCity set " +
//                                "name=" + SystemConfig.getAppCenterDBVarStr() + "name," +
//                                "parentno=" + SystemConfig.getAppCenterDBVarStr() + "parentno,province_code=" + SystemConfig.getAppCenterDBVarStr() + "province_code," +
//                                "city_code=" + SystemConfig.getAppCenterDBVarStr() + "city_code,street_code=" + SystemConfig.getAppCenterDBVarStr() + "street_code" +
//                                "  WHERE no=" + SystemConfig.getAppCenterDBVarStr() + "dept_id";
//                        ps.Add("name", dept_name);
//                        ps.Add("parentno", parent_id);
//                        ps.Add("dept_id", dept_id);
                        //bp.da.DBAccess.RunSQL(ps);
                    }
                }

            }
    }
    private void usersDeal(Map<String, Object> pmap) throws Exception {

        if (!pmap.containsKey("users") || !(pmap.get("users") instanceof List)) {
            return;
        }
        List<Map<String, Object>> list = (List<Map<String, Object>>) pmap.get("users");
        for (Map<String, Object> m : list) {

            String user_id = m.get("user_id").toString();
            String dept_id = m.get("dept_id").toString();
            String user_name = m.get("user_name").toString();
            String email = m.get("email").toString();
            String del_flag = m.get("del_flag").toString();
            Emp emp = new Emp();
            emp.setNo(user_id);
            //删除
            if (del_flag.equals("2")) {
                emp.Delete();
                //处理部门用户表
                DeptEmp deptEmp = new DeptEmp();
                deptEmp.Delete("FK_Emp", user_id);
                //处理部门用户角色表
                DeptEmpStation deptEmpStation = new DeptEmpStation();
                deptEmpStation.Delete("FK_Emp", user_id);
            } else {
                int i = emp.RetrieveFromDBSources();
                emp.setName(user_name);
                emp.setDeptNo(dept_id);
                emp.setEmail(email);
                Dept dept = new Dept();
                dept.setNo(dept_id);
                int j = dept.RetrieveFromDBSources();
                //用户部门不存在
                if (j == 0) {
                    throw new RuntimeException("部门:" + dept_id + "不存在");
                }
                //插入
                if (i == 0) {
                    emp.Insert();
                    //处理部门用户表
                    DeptEmp deptEmp = new DeptEmp();
                    deptEmp.setDeptName(dept.getName());
                    deptEmp.setDeptNo(dept.getNo());
                    deptEmp.setEmpNo(user_id);
                    deptEmp.setMyPK(dept.getNo() + "_" + user_id);
                    deptEmp.Insert();
                } else {
                    emp.Update();
                }


            }
        }
    }

    private void rolesDeal(Map<String, Object> pmap) throws Exception {
        if (!pmap.containsKey("roles") || !(pmap.get("roles") instanceof List)) {
            return;
        }
        List<Map<String, Object>> list = (List<Map<String, Object>>) pmap.get("roles");
        for (Map<String, Object> m : list) {
            String role_id = m.get("role_id").toString();
            String role_name = m.get("role_name").toString();
            String role_level = m.get("role_level").toString();
            String del_flag = m.get("del_flag").toString();
            Station station = new Station();
            station.setNo(role_id);
            //删除
            if (del_flag.equals("2")) {
                station.Delete();
                //处理部门用户角色表
                DeptEmpStation deptEmpStation = new DeptEmpStation();
                deptEmpStation.Delete("FK_Station", role_id);
            } else {
                int i = station.RetrieveFromDBSources();
                station.setName(role_name);
                station.setFKStationType(role_level);
                //插入
                if (i == 0) {
                    station.Insert();
                } else {
                    station.Update();
                }
            }
        }
    }

    //用户角色数据处理
    private void user_roles(Map<String, Object> pmap) throws Exception {

        if (!pmap.containsKey("user_roles") || !(pmap.get("user_roles") instanceof List)) {
            return;
        }
        List<Map<String, Object>> list = (List<Map<String, Object>>) pmap.get("user_roles");
        //判断用户，角色是否存在
        for (Map<String, Object> m : list) {
            String user_id = m.get("user_id").toString();
            String role_id = m.get("role_id").toString();
            String del_flag = m.get("del_flag").toString();
            if (del_flag.equals("2")) {
                continue;
            }
            Emp emp = new Emp();
            emp.setNo(user_id);
            int i = emp.RetrieveFromDBSources();
            //用户不存在
            if (i == 0) {
                throw new Exception("用户:" + user_id + "不存在");
            }
            Station station = new Station();
            station.setNo(role_id);
            i = station.RetrieveFromDBSources();
            //角色不存在
            if (i == 0) {
                throw new Exception("角色:" + role_id + "不存在");
            }
        }
        //按人员分组
        Map<String, List<Map<String, Object>>> emap = list.stream().collect(Collectors.groupingBy(i -> i.get("user_id").toString()));
        //删除人员的角色部门数据
        for (String key : emap.keySet()) {
            DeptEmpStation deptEmpStation = new DeptEmpStation();
            deptEmpStation.Delete("FK_Emp", key);

            DeptEmp deptEmp = new DeptEmp();
            deptEmp.Delete("FK_Emp", key);
        }
        for (Map<String, Object> m : list) {
            String user_id = m.get("user_id").toString();
            String role_id = m.get("role_id").toString();
            String del_flag = m.get("del_flag").toString();
            if (del_flag.equals("2")) {
                continue;
            }
            Emp emp = new Emp();
            emp.setNo(user_id);
            emp.RetrieveFromDBSources();

            Dept dept = new Dept();
            dept.setNo(emp.getDeptNo());
            dept.RetrieveFromDBSources();

            Station station = new Station();
            station.setNo(role_id);
            station.RetrieveFromDBSources();

            //插入数据
            DeptEmpStation deptEmpStation = new DeptEmpStation();
            deptEmpStation.setEmpNo(user_id);
            deptEmpStation.setStationNo(role_id);
            deptEmpStation.setDeptNo(emp.getDeptNo());
            deptEmpStation.setMyPK(emp.getDeptNo() + "_" + user_id + "_" + role_id);
            deptEmpStation.Insert();

            //部门用户表中角色修改
            DeptEmp deptEmp = new DeptEmp();
            deptEmp.setDeptNo(emp.getDeptNo());
            deptEmp.setEmpNo(emp.getNo());
            deptEmp.setDeptName(dept.getName());

            deptEmp.setStationNo(role_id);
            deptEmp.setStationNoT(station.getName());
            deptEmp.setMyPK(emp.getDeptNo() + "_" + user_id);
            deptEmp.Insert();
        }


    }
    private boolean fieldChecks(Map<String, Object> pmap){
        try{
            StringBuilder fields=new StringBuilder("");
            //部门字段
            List<String>depts=Arrays.asList("dept_id","parent_id","dept_name","province_code","city_code","street_code","city_level","del_flag");
            //用户字段
            List<String>users=Arrays.asList("user_id","dept_id","user_name","email","del_flag");
            //角色字段
            List<String>roles=Arrays.asList("role_id","role_name","role_level","del_flag");
            //用户角色字段
            List<String>user_roles=Arrays.asList("user_id","role_id","del_flag");

            if(pmap.containsKey("depts") && pmap.get("depts") instanceof List){
                List<Map<String, Object>>list= (List<Map<String, Object>>) pmap.get("depts");
                fieldCheck(depts,list,fields);
            }
            if(!fields.toString().equals("")){
                //returnMap(map,0,"0",fields.toString());
                return false;
            }
            if(pmap.containsKey("users") && pmap.get("users") instanceof List){
                List<Map<String, Object>>list= (List<Map<String, Object>>) pmap.get("users");
                fieldCheck(users,list,fields);
            }
            if(!fields.toString().equals("")){
                //returnMap(map,0,"0",fields.toString());
                return false;
            }
            if(pmap.containsKey("roles") && pmap.get("roles") instanceof List){
                List<Map<String, Object>>list= (List<Map<String, Object>>) pmap.get("roles");
                fieldCheck(roles,list,fields);
            }
            if(!fields.toString().equals("")){
                //returnMap(map,0,"0",fields.toString());
                return false;
            }
            if(pmap.containsKey("user_roles") && pmap.get("user_roles") instanceof List){
                List<Map<String, Object>>list= (List<Map<String, Object>>) pmap.get("user_roles");
                fieldCheck(user_roles,list,fields);
            }
            if(!fields.toString().equals("")){
                //returnMap(map,0,"0",fields.toString());
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }
    private void user_roles(Map<String, Object> pmap,Map<String, Object> map) throws Exception {
        try{
            if(!pmap.containsKey("user_roles") || !(pmap.get("user_roles") instanceof List)){
                return;
            }
            List<Map<String, Object>>list= (List<Map<String, Object>>) pmap.get("user_roles");
            //判断用户，角色是否存在
            for(Map<String, Object>m:list) {
                try {
                    String user_id = m.get("user_id").toString();
                    String role_id = m.get("role_id").toString();
                    String del_flag = m.get("del_flag").toString();
                    if(del_flag.equals("2")){
                        continue;
                    }
                    Emp emp = new Emp();
                    emp.setNo(user_id);
                    int i=emp.RetrieveFromDBSources();
                    //用户不存在
                    if(i==0){
                       throw  new RuntimeException("用户:"+user_id+"不存在");
                    }
                    Station station = new Station();
                    station.setNo(role_id);
                    i=station.RetrieveFromDBSources();
                    //角色不存在
                    if(i==0){
                        throw new RuntimeException("角色:"+role_id+"不存在");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            //按人员分组
            Map<String, List<Map<String, Object>>> emap=list.stream().collect(Collectors.groupingBy(i->i.get("user_id").toString()));
            //删除人员的角色部门数据
            for (String key : emap.keySet()) {
                DeptEmpStation deptEmpStation=new DeptEmpStation();
                deptEmpStation.Delete("FK_Emp",key);
            }
            for(Map<String, Object>m:list) {
                try {
                    String user_id = m.get("user_id").toString();
                    String role_id = m.get("role_id").toString();
                    String del_flag = m.get("del_flag").toString();
                    if(del_flag.equals("2")){
                        continue;
                    }
                    Emp emp = new Emp();
                    emp.setNo(user_id);
                    emp.RetrieveFromDBSources();

                    Station station = new Station();
                    station.setNo(role_id);
                    station.RetrieveFromDBSources();

                    //插入数据
                    DeptEmpStation deptEmpStation=new DeptEmpStation();
                    deptEmpStation.setEmpNo(user_id);
                    deptEmpStation.setStationNo(role_id);
                    deptEmpStation.setDeptNo(emp.getDeptNo());
                    deptEmpStation.setMyPK(emp.getDeptNo()+"_"+user_id+"_"+role_id);
                    deptEmpStation.Insert();

                    //部门用户表中角色修改
                    DeptEmp deptEmp=new DeptEmp();
                    deptEmp.setStationNo(role_id);
                    deptEmp.setStationNoT(station.getName());
                    deptEmp.setMyPK(emp.getDeptNo()+"_"+user_id);
                    deptEmp.Update();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }catch (Exception e){
            throw  e;
        }
    }
    private void fieldCheck(List<String>fields,List<Map<String, Object>>list,StringBuilder losefields){
        list.stream().forEach(i->{
            fields.forEach(j->{
                boolean ischeck=false;
                for (String key : i.keySet()) {
                    if(j.equals(key)){
                        ischeck=true;
                        break;
                    }
                }
                if(!ischeck && !losefields.toString().contains(j)){
                    losefields.append(",").append(j);
                }

            });
            if(!losefields.toString().equals("")){
                return;
            }
        });
    }
    private  Map<String, Object> parseJSON2Map(JSONObject json) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        // 最外层解析
        for (Object k : json.keySet()) {
            Object v = json.get(k);
            // 如果内层还是json数组的话，继续解析
            if (v instanceof JSONArray) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                Iterator<JSONObject> it = ((JSONArray) v).iterator();
                while (it.hasNext()) {
                    JSONObject json2 = it.next();
                    list.add(parseJSON2Map(json2));
                }
                map.put(k.toString(), list);
            } else if (v instanceof JSONObject) {
                // 如果内层是json对象的话，继续解析
                map.put(k.toString(), parseJSON2Map((JSONObject) v));
            } else {
                // 如果内层是普通对象的话，直接放入map中
                map.put(k.toString(), v);
            }
        }
        return map;
    }


    @GetMapping(value = "executeSql")
    @ApiOperation("执行sql语句")
    @ResponseBody
    public Object executeSql(String sql, String token) throws Exception {

       try {
           Dev2Interface.Port_LoginByToken(token);
           DBAccess.RunSQLReturnString(sql);
           return Return_Info(200, "成功", "");
       }catch (Exception ex){
           return Return_Info(500, ex.getMessage(), "");
       }

    }
  public  float toFloatInternal(String str){
      if (str == null || str.trim().isEmpty()) {
          return 0.0f;
      }
      try {
          return Float.parseFloat(str);
      } catch (NumberFormatException e) {
          return 0.0f;
      }
  }
    public  Hashtable  TableInfoHT(String colName, String colContent, String longitude, String latitude, String azimuth, String distance
    , String imageID, String imageBUUID){
        String url="https://172.19.144.11:9099/bpm-api/";
        String filePath=colContent.substring(colContent.indexOf("DataUser"));
        Hashtable hashtable=new Hashtable();
        hashtable.put("colName",colName);
        hashtable.put("colContent",url+filePath);
        hashtable.put("longitude",longitude);
        hashtable.put("latitude",latitude);
        hashtable.put("azimuth",azimuth);
        hashtable.put("distance",distance);
        hashtable.put("imageID",imageID);
        hashtable.put("imageBUUID",imageBUUID);
        return hashtable;
    }

    public  Hashtable  TableInfoHT(String colName, Object colContent){
        Hashtable hashtable=new Hashtable();
        hashtable.put("colName",colName);
        hashtable.put("colContent",colContent);
        hashtable.put("longitude","");
        hashtable.put("latitude","");
        hashtable.put("azimuth","");
        hashtable.put("distance","");
        hashtable.put("imageID","");
        hashtable.put("imageBUUID","");
        return hashtable;
    }

    public  Hashtable DetailInFoHT(String tabid,String tabName,String tabType,Object hashtableArrayList){
        Hashtable hashtable=new Hashtable();
        hashtable.put("tabid",tabid);
        hashtable.put("tabName",tabName);
        hashtable.put("tabType",tabType);
        hashtable.put("tabinfo",hashtableArrayList);
        return hashtable;
    }

    public ArrayList TableInfoArrayList(Entity entity) {
        ArrayList<Hashtable> hashtableArrayList = new ArrayList<>();
        for (Attr attr : entity.getEnMap().getAttrs()) {

            if(attr.getKey().equals(BasicInfoAttr.OBJECTID)||attr.getKey().equals(BasicInfoAttr.ISHC)||attr.getKey().equals(BasicInfoAttr.QuXianNo)
               ||attr.getKey().equals(BasicInfoAttr.XiangZhenNo)||attr.getKey().equals(BasicInfoAttr.SHuoShuSX)||attr.getKey().equals(EntityOIDAttr.OID)
               ||attr.getKey().equals(ND10RptAttr.ObjectId)||attr.getKey().equals(ND10RptAttr.DKSta)||attr.getKey().equals(ND10RptAttr.BatchNum)
               ||attr.getKey().equals(ND10RptAttr.SQL_HeChaQiShu)||attr.getKey().equals(ND10RptAttr.SQL_ShiQuXian)||attr.getKey().equals(ND10RptAttr.SQL_XiangZhen)){
                continue;
            }
            Hashtable hashtable = new Hashtable();
            if (attr.getMyDataType() == DataType.AppBoolean) {
                if (entity.GetValIntByKey(attr.getKey()) == 1) {
                    hashtable = TableInfoHT(attr.getDesc(), "是");
                } else {
                    hashtable = TableInfoHT(attr.getDesc(), "否");
                }
                continue;
            }
            /*如果是外键 就要去掉左右空格。
             *  */
            if (attr.getMyFieldType() == FieldType.FK || attr.getMyFieldType() == FieldType.PKFK) {
                hashtable = TableInfoHT(attr.getDesc(), entity.GetValByKey(attr.getKey()).toString().trim());
            } else {
                Object obj = entity.GetValByKey(attr.getKey());
                if (obj == null && attr.getItIsNum()) {
                    hashtable = TableInfoHT(attr.getDesc(), "0");
                    continue;
                }

                if (attr.getItIsNum() == true && DataType.IsNumStr(obj.toString()) == false) {
                    hashtable = TableInfoHT(attr.getDesc(), "0");
                } else {
                    hashtable = TableInfoHT(attr.getDesc(), obj);
                }
            }
            hashtableArrayList.add(hashtable);
        }
        return hashtableArrayList;
    }

    @Override
    public Class getCtrlType() {
        return null;
    }
}
