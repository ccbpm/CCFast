package Controller;

import bp.da.DBAccess;
import bp.da.DataRow;
import bp.da.DataTable;
import bp.port.*;
import bp.tools.Json;
import bp.wf.port.Dept;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Hashtable;

@RestController
@RequestMapping(value = "/PortOrg")
@CrossOrigin(origins = "*")
public class PortOrgController {
    /**
     * 报文接入
     * @param resbody
     */
    @RequestMapping(value = "/Port_Save")
    public Object Port_Save(@RequestBody String resbody)  {

        String from ="";
        String msgType ="";
        String msgId = "";
        String returnInFo="";
        try {
            //解析json数据
            JSONObject jsonData = JSONObject.fromObject(JSONObject.fromObject(resbody).get("data"));
            from = (String) jsonData.get("from");
            // 目标应用
            String to = (String) jsonData.get("to");
            // 创建时间戳
            String createTime = (String) jsonData.get("createTime");
            // 消息类型
            msgType = (String) jsonData.get("msgType");
            // 信息
            Object content = (Object) jsonData.get("content");
            //消息id,64位整型
            msgId =  (String)jsonData.get("msgId");
            // 消息类型，2000--代表组织架构，2001代表账户信息,2002--代表角色数据，2003--代表人员角色组织关联数据
            if ("2000".equals(msgType)) {
                // 解析组织信息存储
                returnInFo = portOrg_Save(content);
            } else if ("2001".equals(msgType)) {
                // 解析账户信息存储
                returnInFo = portEmp_Save(content);
            } else if ("2002".equals(msgType)) {
                //解析角色数据
                returnInFo = station_Save(content);
            } else {
                //人员角色组织关联数据
                returnInFo = empStationDept_Save(content);
            }
        }catch(Exception ex ){
            ex.printStackTrace();
            return Return_Info(from, (int)new Date().getTime(), msgType, ex.getMessage(), msgId,"1");
        }
        return  Return_Info(from, (int)new Date().getTime(), msgType,returnInFo,msgId,"0");
    }

    public String portOrg_Save(Object content) throws Exception {
        //解析content json数据
        JSONObject jsoncontent = ((JSONObject) ((JSONArray) content).get(0));
        //唯一标识，作为处理结果ID返回
        String id = (String) jsoncontent.get("id");
        //机构编码
        String orgCode = (String) jsoncontent.get("orgCode");
        //"HY0106",--父级ID（必填*）
        String parentId = (String) jsoncontent.get("parentId");
        //"HR" "来源系统 ： HR、UC"
        String datasource = (String) jsoncontent.get("datasource");
        //"财务管理部",--组织机构名称（本节点）（必填*）
        String orgName = (String) jsoncontent.get("orgName");

        // 首先要判断datasource数据来源这个是否为 "UC" 只有是 "UC" 才需要继续进行，否则直接返回接口推送成功的逻辑。
        if(!"UC".equals(datasource)){
            return "datasource数据来源非UC，直接返回。";
        }
        /**插入部门数据Start**/
        Dept dept = new Dept();

        //判断 parentId 字段为 NULL 时，代表是整个组织架构的根节点，这条数据的 orgCode 是 "ROOT" ,
        // 由于bpm中根节点设置的编码是 "100"，所以需要你这边将推过来的这个根节点数据的orgCode 和 parentId 从"ROOT" NULL 做个 "100" "0"的数据编码和父级编码的转换入库
        //根目录节点
        if ("NULL".equals(parentId) && "ROOT".equals(orgCode))
            dept.setNo("100");
        else
            dept.setNo(orgCode);
        //判断是否增量
        if(dept.RetrieveFromDBSources() == 0){
            //增量
            dept.setName(orgName);
            //全部作为根目录的部门插入，后续进入系统设定独立组织数据
            dept.setOrgNo("100");
            //是否为根目录
            if ("NULL".equals(parentId) && "ROOT".equals(orgCode)) {
                dept.setParentNo("0");
            }else if("ROOT".equals(parentId)) {
                //是否为独立组织
                dept.setParentNo("100");
            }else {
                dept.setParentNo(parentId);
            }
            dept.Insert();
        } else {
            //修改
            dept.setName(orgName);
            //修改时，赋值之前的orgNo，可能已经被设为独立组织
            dept.setOrgNo(dept.getOrgNo());
            //是否为根目录
            if ("NULL".equals(parentId) && "ROOT".equals(orgCode)) {
                dept.setParentNo("0");
            }else if("ROOT".equals(parentId)) {
                //是否为独立组织
                dept.setParentNo("100");
            }else {
                dept.setParentNo(parentId);
            }

            dept.Update();
        }
        /**插入部门数据End**/

        return id;
    }

    public String portEmp_Save(Object content) throws Exception {
        //解析content json数据
        JSONObject jsoncontent = ((JSONObject) ((JSONArray) content).get(0));
        //唯一标识，作为处理结果ID返回
        String id = (String) jsoncontent.get("id");
        //0--代表启用;1--代表禁用
        String status = (String) jsoncontent.get("status");
        //系统账号
        String systemAccount = (String) jsoncontent.get("systemAccount");
        //用户名
        String userName = (String) jsoncontent.get("userName");
        //组织架构编码
        String departmentCode = (String) jsoncontent.get("departmentCode");
        //手机号码
        String phoneNumber = (String) jsoncontent.get("phoneNumber");
        //个人邮箱
        String email =(String) jsoncontent.get("email");
        //"HR" "来源系统 ： HR、UC"
        String datasource = (String) jsoncontent.get("datasource");
        // 首先要判断datasource数据来源这个是否为 "UC" 只有是 "UC" 才需要继续进行，否则直接返回接口推送成功的逻辑。
        if(!"UC".equals(datasource)){
            return "datasource数据来源非UC，直接返回。";
        }
        /**插入人员数据Start**/
        Emp emp = new Emp();
        emp.setNo(systemAccount);
        //判断是否增量
        if(emp.RetrieveFromDBSources() == 0){
            //增量
            emp.setName(userName);

            emp.setPass("123");
            emp.setDeptNo(departmentCode);
            emp.setTel(phoneNumber);
            emp.setEmail(email);
            //全部作为根目录的部门插入，后续进入系统设定独立组织数据
            emp.setOrgNo("100");
            emp.SetValByKey(EmpAttr.EmpSta,Integer.valueOf(status));
            emp.setDeptNo(departmentCode);
            emp.Insert();
        } else {
            //增量
            emp.setName(userName);

            emp.setPass("123");
            emp.setDeptNo(departmentCode);
            emp.setTel(phoneNumber);
            emp.setEmail(email);
            //全部作为根目录的部门插入，后续进入系统设定独立组织数据
            emp.setOrgNo(emp.getOrgNo());
            emp.SetValByKey(EmpAttr.EmpSta,Integer.valueOf(status));
            emp.setDeptNo(departmentCode);
            emp.Update();
        }
        /**插入人员数据End**/

        return id;
    }
    public String station_Save(Object content) throws Exception {
        //解析content json数据
        JSONObject jsoncontent = ((JSONObject) ((JSONArray) content).get(0));
        //唯一标识，作为处理结果ID返回
        String id = (String) jsoncontent.get("id");
        //角色名称
        String roleName = (String) jsoncontent.get("roleName");
        //"HR" "来源系统 ： HR、UC"
        String datasource = (String) jsoncontent.get("datasource");
        // 首先要判断datasource数据来源这个是否为 "UC" 只有是 "UC" 才需要继续进行，否则直接返回接口推送成功的逻辑。
        if(!"UC".equals(datasource)){
            return "datasource数据来源非UC，直接返回。";
        }
        /**插入岗位数据Start**/
        Station station = new Station();
        station.setNo(id);
        //判断是否增量
        if(station.RetrieveFromDBSources() == 0){
            //增量
            station.setName(roleName);
            station.setFKStationType("1");
            //全部作为根目录的部门插入，后续进入系统设定独立组织数据
            station.setOrgNo("100");
            station.Insert();
        } else {
            station.setName(roleName);
            station.setFKStationType("1");
            //全部作为根目录的部门插入，后续进入系统设定独立组织数据
            station.setOrgNo(station.getOrgNo());
            station.Update();
        }
        /**插入岗位数据End**/

        return id;
    }
    public String empStationDept_Save(Object content) throws Exception {
        //解析content json数据
        JSONObject jsoncontent = ((JSONObject) ((JSONArray) content).get(0));
        //用户编码
        String userCode = (String) jsoncontent.get("userCode");
        //来源系统 ： HR、UC
        String datasource = (String) jsoncontent.get("datasource");

        // 首先要判断datasource数据来源这个是否为 "UC" 只有是 "UC" 才需要继续进行，否则直接返回接口推送成功的逻辑。
        if(!"UC".equals(datasource)){
            return "datasource数据来源非UC，直接返回。";
        }

        //获取人员所属的orgNo，赋值给人员部门、人员部门岗位
        Emp emp = new Emp(userCode);

        //角色-组织关联关系
        JSONArray roleOrgList = (JSONArray) jsoncontent.get("roleOrgList");
        if(roleOrgList.size()>0) {
            //如果有数据的情况下，需要先删除数据
            DeptEmp deptEmp = new DeptEmp();
            if(deptEmp.Retrieve("FK_Emp",userCode) > 0){
                deptEmp.Delete("FK_Emp",userCode);
            }
            DeptEmpStation deptEmpStation =new DeptEmpStation();
            if(deptEmpStation.Retrieve("FK_Emp",userCode) > 0){
                deptEmpStation.Delete("FK_Emp",userCode);
            }

            for (int i = 0; i < roleOrgList.size(); i++) {
                // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                JSONObject job = roleOrgList.getJSONObject(i);
                // 得到 每个对象中的属性值
                String roleId = (String)job.get("roleId");
                String roleName = (String)job.get("roleName");
                String orgCode = (String)job.get("orgCode");
                String orgName = (String)job.get("orgName");

                deptEmp = new DeptEmp();
                // 插入部门人员数据
                deptEmp.setPKVal(orgCode+"_"+userCode);
                deptEmp.setDeptNo(orgCode);
                deptEmp.setEmpNo(userCode);
                deptEmp.setOrgNo(emp.getOrgNo());
                //deptEmp.setStationNo(roleId);
                deptEmp.Insert();

                deptEmpStation =new DeptEmpStation();
                // 插入部门人员岗位数据
                deptEmpStation.setPKVal(orgCode+"_"+userCode+"_"+roleId);
                deptEmpStation.setDeptNo(orgCode);
                deptEmpStation.setEmpNo(userCode);
                deptEmpStation.setStationNo(roleId);
                deptEmpStation.setOrgNo(emp.getOrgNo());
                deptEmpStation.Insert();
            }
        }
        //按照人员、部门查找岗位插入部门人员表
        UpdateDeptEmpStaionNoS(userCode);
        return userCode;
    }
    public static void UpdateDeptEmpStaionNoS(String empNo) throws Exception {
        String sql = "select fk_emp,fk_dept,GROUP_CONCAT(distinct(fk_station)) as staions , GROUP_CONCAT(distinct(b.name)) as stationTs" +
                "from port_deptempstation,port_station b " +
                "where  fk_station =b.no and FK_Emp='" + empNo + "'" +
                "group by fk_emp,fk_dept";
        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        if (dt.Rows.size() > 0) {
            for (DataRow dr : dt.Rows) {
                String deptNo = (String) dr.getValue("fk_dept");

                DeptEmp de = new DeptEmp();
                de.Retrieve(DeptEmpAttr.FK_Dept, deptNo, DeptEmpAttr.FK_Emp, empNo);
                de.setStationNo((String) dr.getValue("staions"));
                de.setStationNoT((String) dr.getValue("stationTs"));
                de.Update();
            }
        }
    }
    public static Object Return_Info(String from, int createTime, String msgType,String content,String msgId,String funcflag)
    {

        Hashtable ht = new Hashtable();
        ht.put("from", from);
        ht.put("createTime", createTime);
        ht.put("msgType", msgType);
        ht.put("content", content);
        ht.put("msgId", msgId);
        ht.put("funcflag",funcflag);

        String htjson = Json.ToJson(ht);

        JSONObject jo = new JSONObject();
        jo.element("data",htjson);

        return jo;
    }
}
