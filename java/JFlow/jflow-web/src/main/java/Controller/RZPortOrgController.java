package Controller;

import bp.da.DBAccess;
import bp.da.DataRow;
import bp.da.DataTable;
import bp.da.DataType;
import bp.port.*;
import bp.tools.Json;
import bp.web.WebUser;
import bp.wf.Dev2Interface;
import bp.wf.port.Dept;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Hashtable;

@RestController
@RequestMapping(value = "/RZ_PortOrg")
@CrossOrigin(origins = "*")
public class RZPortOrgController {
    /**
     * 报文接入
     * @param resbody
     */
    @RequestMapping(value = "/Port_Save")
    public Object Port_Save(@RequestBody String resbody)  {

        String returnInFo="";
        try {
            //解析json数据
            JSONObject jsonData = JSONObject.fromObject(JSONObject.fromObject(resbody));
            //是否为物理删除标识：true--是;false--否;默认为false;当为true时，取orgCodeList的数据进行物理删除（慎用）；
            //当为false时，取dataList的数据有则修改，无则新增处理，dataList单次最多2000条数据
            //deleteFlag当为false时，入参不传，只有为true时传
            //判断是否传了deleteFlag
            if(jsonData.has("deleteFlag")){
                Boolean deleteFlag  = (Boolean) jsonData.getBoolean("deleteFlag");

                if(deleteFlag){
                    if(!jsonData.has("orgCodeList"))
                        return "orgCodeList为空，返回.";
                    // 要删除组织的dataCode集合
                    Object orgCodeList = (Object) jsonData.get("orgCodeList");
                    returnInFo = Port_Dept_Delete(orgCodeList);
                }else {
                    if(!jsonData.has("dataList"))
                        return "dataList为空，返回.";
                    // 组织结构数据集合
                    Object dataList = (Object) jsonData.get("dataList");
                    returnInFo = port_Save(dataList);
                }
            }else {
                if(!jsonData.has("dataList"))
                    return "dataList为空，返回.";
                // 组织结构数据集合
                Object dataList = (Object) jsonData.get("dataList");
                returnInFo = port_Save(dataList);
            }
        }catch(Exception ex ){
            ex.printStackTrace();
            return Return_Info("500",  ex.getMessage(),"");
        }
        return  Return_Info("200", returnInFo,"");
    }
    public static String Port_Dept_Delete(Object orgCodeList) throws Exception {
        String returnInfo = "";
        try
        {
            //解析orgCodeList json数据
            JSONArray jsonArray = JSONArray.fromObject(orgCodeList);
            if(jsonArray.size()>0){
                for(int i=0;i<jsonArray.size();i++){
                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    Integer no = (Integer)jsonArray.get(i);
                    //删除部门.
                    bp.port.Dept dept = new bp.port.Dept();
                    dept.setNo("RZ_"+no);
                    if(dept.RetrieveFromDBSources()>0){
                        dept.Delete();
                    }else {
                        returnInfo += "No="+dept.getNo()+"在port_dept表中不存在;";
                        continue;
                    }
                }
            }
            return returnInfo;
        }
        catch (Exception ex)
        {
            return "err@" + ex.getMessage();
        }
    }
    public String port_Save(Object dataList) throws Exception {
        try{
            //解析orgCodeList json数据
            JSONArray jsonArray = JSONArray.fromObject(dataList);
            if(jsonArray.size()>0){
                for(int i=0;i<jsonArray.size();i++){
                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    JSONObject dataObject = (JSONObject)jsonArray.get(i);
                    //新增或修改部门.
                    bp.port.Dept dept = new bp.port.Dept();
                    dept.setNo("RZ_"+dataObject.get("dataCode"));
                    if(dept.RetrieveFromDBSources()>0){
                        //修改
                        dept.setName((String) dataObject.get("orgName"));
                        //全部作为根目录的部门插入，后续进入系统设定独立组织数据
                        dept.setOrgNo(dept.getOrgNo());
                        //是否为根目录
                        if ("0".equals(String.valueOf(dataObject.get("parentCode")))) {
                            dept.setParentNo("100");
                        }else {
                            dept.setParentNo("RZ_" + String.valueOf(dataObject.get("parentCode")));
                        }
                        dept.Update();
                    }else {
                        //增量
                        dept.setName((String) dataObject.get("orgName"));
                        //全部作为根目录的部门插入，后续进入系统设定独立组织数据
                        dept.setOrgNo("100");
                        //是否为根目录
                        if ("0".equals(String.valueOf(dataObject.get("parentCode")))) {
                            dept.setParentNo("100");
                        }else {
                            dept.setParentNo("RZ_" + String.valueOf(dataObject.get("parentCode")));
                        }
                        dept.Insert();
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return "err@" + ex.getMessage();
        }
        return "1";
    }

    /**
     * 同步岗位信息报文接入
     * @param resbody
     */
    @RequestMapping(value = "/PortStation_Save")
    public Object PortStation_Save(@RequestBody String resbody)  {

        String returnInFo="";
        try {
            //解析json数据
            JSONObject jsonData = JSONObject.fromObject(JSONObject.fromObject(resbody));
            //是否为物理删除标识：true--是;false--否;默认为false;当为true时，取orgCodeList的数据进行物理删除（慎用）；
            //当为false时，取dataList的数据有则修改，无则新增处理，dataList单次最多2000条数据
            //deleteFlag当为false时，入参不传，只有为true时传
            //判断是否传了deleteFlag
            if(jsonData.has("deleteFlag")){
                Boolean deleteFlag  = (Boolean) jsonData.getBoolean("deleteFlag");

                if(deleteFlag){
                    if(!jsonData.has("roleCodeList"))
                        return "roleCodeList为空，返回.";
                    // 要删除组织的dataCode集合
                    Object roleCodeList = (Object) jsonData.get("roleCodeList");
                    returnInFo = PortStation_Delete(roleCodeList);
                }else {
                    if(!jsonData.has("dataList"))
                        return "dataList为空，返回.";
                    // 组织结构数据集合
                    Object dataList = (Object) jsonData.get("dataList");
                    returnInFo = PortStation_Save(dataList);
                }
            }else {
                if(!jsonData.has("dataList"))
                    return "dataList为空，返回.";
                // 组织结构数据集合
                Object dataList = (Object) jsonData.get("dataList");
                returnInFo = PortStation_Save(dataList);
            }
        }catch(Exception ex ){
            ex.printStackTrace();
            return Return_Info("500",  ex.getMessage(),"");
        }
        return  Return_Info("200", returnInFo,"");
    }

    public static String PortStation_Delete(Object orgCodeList) throws Exception {
        String returnInfo = "";
        try
        {
            //解析orgCodeList json数据
            JSONArray jsonArray = JSONArray.fromObject(orgCodeList);
            if(jsonArray.size()>0){
                for(int i=0;i<jsonArray.size();i++){
                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    Integer no = (Integer)jsonArray.get(i);
                    //删除岗位.
                    Station station = new Station();
                    station.setNo("RZ_"+no);
                    if(station.RetrieveFromDBSources()>0){
                        station.Delete();
                    }else {
                        returnInfo += "No="+station.getNo()+"在port_station表中不存在;";
                        continue;
                    }
                }
            }
            return returnInfo;
        }
        catch (Exception ex)
        {
            return "err@" + ex.getMessage();
        }
    }

    public String PortStation_Save(Object dataList) throws Exception {
        try{
            //解析orgCodeList json数据
            JSONArray jsonArray = JSONArray.fromObject(dataList);
            if(jsonArray.size()>0){
                for(int i=0;i<jsonArray.size();i++){
                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    JSONObject dataObject = (JSONObject)jsonArray.get(i);
                    //新增或修改部门.
                    Station station = new Station();
                    station.setNo("RZ_" + dataObject.get("roleCode"));
                    //判断是否增量
                    if(station.RetrieveFromDBSources() == 0){
                        //增量
                        station.setName((String) dataObject.get("roleName"));
                        station.setFKStationType("1");
                        //全部作为根目录的部门插入，后续进入系统设定独立组织数据
                        station.setOrgNo("100");
                        station.Insert();
                    } else {
                        station.setName((String) dataObject.get("roleName"));
                        station.setFKStationType("1");
                        //全部作为根目录的部门插入，后续进入系统设定独立组织数据
                        station.setOrgNo(station.getOrgNo());
                        station.Update();
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return "err@" + ex.getMessage();
        }
        return "1";
    }

    /**
     * 同步用户信息报文接入
     * @param resbody
     */
    @RequestMapping(value = "/PortEmp_Save")
    public Object portEmp_Save(@RequestBody Object resbody) throws Exception {
        String returnInFo = "";
        try {
            //解析json数据
            JSONObject jsonData = JSONObject.fromObject(JSONObject.fromObject(resbody));
            //是否为物理删除标识：true--是;false--否;默认为false;当为true时，取orgCodeList的数据进行物理删除（慎用）；
            //当为false时，取dataList的数据有则修改，无则新增处理，dataList单次最多2000条数据
            //deleteFlag当为false时，入参不传，只有为true时传
            //判断是否传了deleteFlag
            if (jsonData.has("deleteFlag")) {
                Boolean deleteFlag = (Boolean) jsonData.getBoolean("deleteFlag");

                if (deleteFlag) {
                    if (!jsonData.has("userAccountList"))
                        return "userAccountList为空，返回.";
                    // 要删除组织的dataCode集合
                    Object userAccountList = (Object) jsonData.get("userAccountList");
                    returnInFo = PortEmp_Delete(userAccountList);
                } else {
                    if (!jsonData.has("dataList"))
                        return "dataList为空，返回.";
                    // 组织结构数据集合
                    Object dataList = (Object) jsonData.get("dataList");
                    returnInFo = PortEmp_Save(dataList);
                }
            } else {
                if (!jsonData.has("dataList"))
                    return "dataList为空，返回.";
                // 组织结构数据集合
                Object dataList = (Object) jsonData.get("dataList");
                returnInFo = PortEmp_Save(dataList);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Return_Info("500", ex.getMessage(), "");
        }
        return  Return_Info("200", returnInFo,"");
    }
    public static String PortEmp_Delete(Object userAccountList) throws Exception {
        String returnInfo = "";
        try
        {
            //解析orgCodeList json数据
            JSONArray jsonArray = JSONArray.fromObject(userAccountList);
            if(jsonArray.size()>0){
                for(int i=0;i<jsonArray.size();i++){
                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    String no = String.valueOf(jsonArray.get(i));
                    //删除岗位.
                    Emp emp = new Emp();
                    emp.setNo(no);
                    if(emp.RetrieveFromDBSources()>0){
                        emp.Delete("No",no);
                        //同时删除port_deptemp,port_deptempstation
                        DeptEmp deptEmp = new DeptEmp();
                        deptEmp.Delete("FK_Emp",no);

                        DeptEmpStation deptEmpStation = new DeptEmpStation();
                        deptEmpStation.Delete("FK_Emp",no);
                    }else {
                        returnInfo += "No="+emp.getNo()+"在port_emp表中不存在;";
                        continue;
                    }
                }
            }
            return returnInfo;
        }
        catch (Exception ex)
        {
            return "err@" + ex.getMessage();
        }
    }
    public String PortEmp_Save(Object dataList){
        String deptMsg="";
        String stationMsg = "";
        String empMsg = "";
        try{
            //解析orgCodeList json数据
            JSONArray jsonArray = JSONArray.fromObject(dataList);
            if(jsonArray.size()>0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    JSONObject dataObject = (JSONObject) jsonArray.get(i);
                    //新增或修改人员.
                    /**插入人员数据Start**/
                    Emp emp = new Emp();
                    emp.setNo((String) dataObject.get("userAccount"));
                    //判断是否增量
                    if (emp.RetrieveFromDBSources() == 0) {
                        //增量
                        emp.setName((String) dataObject.get("userName"));
                        emp.setPass("123");
                        emp.setDeptNo("RZ_" + dataObject.get("orgCode"));
                        emp.setTel((String) dataObject.get("userMobile"));
                        emp.setEmail((String) dataObject.get("userEmail"));
                        //全部作为根目录的部门插入，后续进入系统设定独立组织数据
                        emp.setOrgNo("100");
                        emp.SetValByKey(EmpAttr.EmpSta,dataObject.getInt("useFlag"));
                        emp.Insert();
                    } else {
                        //增量
                        emp.setName((String) dataObject.get("userName"));
                        emp.setPass("123");
                        emp.setDeptNo("RZ_" + dataObject.get("orgCode"));
                        emp.setTel((String) dataObject.get("userMobile"));
                        emp.setEmail((String) dataObject.get("userEmail"));
                        //全部作为根目录的部门插入，后续进入系统设定独立组织数据
                        emp.setOrgNo(emp.getOrgNo());
                        emp.SetValByKey(EmpAttr.EmpSta,dataObject.getInt("useFlag"));
                        emp.Update();
                    }
                    //部门人员、部门人员岗位表中清空该人员的所有数据
                    String userCode = emp.getNo();
                    //如果有数据的情况下，需要先删除数据
                    DeptEmp deptEmp = new DeptEmp();
                    if(deptEmp.Retrieve("FK_Emp",userCode) > 0){
                        deptEmp.Delete("FK_Emp",userCode);
                    }
                    DeptEmpStation deptEmpStation =new DeptEmpStation();
                    if(deptEmpStation.Retrieve("FK_Emp",userCode) > 0){
                        deptEmpStation.Delete("FK_Emp",userCode);
                    }
                    /*插入人员表结束*/
                    /*插入部门人员、部门人员岗位表开始*/
                    //解析userRoleOrgVOList数据,该用户拥有的角色对象集合，其中角色对象有角色code对应的管理组织机构code集合
                    Object obj = dataObject.get("userRoleOrgVOList");
                    if(obj==null || DataType.IsNullOrEmpty(obj.toString())){
                        empMsg+="人员"+emp.getName()+"不存在部门角色";
                        continue;
                    }

                    JSONArray userRoleOrgVOList = dataObject.getJSONArray("userRoleOrgVOList");
                    Dept dept = null;
                    Station sta = null;
                    if (userRoleOrgVOList.size() > 0) {
                        for (int ii = 0; ii < userRoleOrgVOList.size(); ii++) {
                            // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                            JSONObject roledata = (JSONObject) userRoleOrgVOList.get(ii);
                            //岗位ID
                            String roleCode = String.valueOf(roledata.get("roleCode"));
                            sta = new Station();
                            sta.setNo("RZ_" + roleCode);
                            if(sta.RetrieveFromDBSources()==0){
                                stationMsg +="角色:"+roleCode+"在Port_Station中不存在,请根据编号查询添加到数据库";
                            }

                            //该角色对应管理的组织机构code集合
                            JSONArray orgCodeList = JSONArray.fromObject(roledata.get("orgCodeList"));;
                            if (orgCodeList.size() > 0) {
                                for (int j = 0; j < orgCodeList.size(); j++) {
                                    // 遍历 jsonarray 数组，获取组织ID
                                    String orgCode =String.valueOf(orgCodeList.get(j));
                                    String stationCode="RZ_" + roleCode;
                                    String orgCodeRz = "RZ_" + orgCode;
                                    dept = new Dept();
                                    dept.setNo(orgCodeRz);
                                    if(dept.RetrieveFromDBSources() == 0){
                                        deptMsg+="部门编号:"+orgCode+"在Port_Dept中不存在";
                                    }else{
                                        deptEmp.setDeptName(dept.getName());
                                    }

                                    deptEmp.setDeptNo(orgCodeRz);
                                    deptEmp.setEmpNo(userCode);
                                    deptEmp.setOrgNo(emp.getOrgNo());
                                    //deptEmp.setStationNo(stationCode);
                                    deptEmp.Save();

                                    deptEmpStation.setDeptNo(orgCodeRz);
                                    deptEmpStation.setEmpNo(userCode);
                                    deptEmpStation.setStationNo(stationCode);
                                    deptEmpStation.setOrgNo(emp.getOrgNo());
                                    deptEmpStation.Insert();

                                }
                            }
                            /*插入部门人员、部门人员岗位表结束*/
                        }
                    }

                    // 按照人员、部门查找岗位插入部门人员表
                    UpdateDeptEmpStaionNoS(emp.getNo());
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return "err@" + ex.getMessage();
        }
        if(DataType.IsNullOrEmpty(deptMsg)&& DataType.IsNullOrEmpty(stationMsg) && DataType.IsNullOrEmpty(empMsg))
            return "1";
        return empMsg+";"+deptMsg+";"+stationMsg;
    }
    public static void UpdateDeptEmpStaionNoS(String empNo) throws Exception {
        String sql ="select fk_emp,fk_dept,GROUP_CONCAT(distinct(fk_station)) as staions , GROUP_CONCAT(distinct(b.name)) as stationTs" +
                " from port_deptempstation,port_station b " +
                " where  fk_station =b.no and FK_Emp='"+empNo+"'" +
                " group by fk_emp,fk_dept";
        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        if(dt.Rows.size()>0){
            for(DataRow dr : dt.Rows){
                String deptNo = (String) dr.getValue("fk_dept");

                DeptEmp de = new DeptEmp();
                de.Retrieve(DeptEmpAttr.FK_Dept,deptNo,DeptEmpAttr.FK_Emp,empNo);
                de.setStationNo((String) dr.getValue("staions"));
                de.setStationNoT((String) dr.getValue("stationTs"));
                de.Update();
            }
        }

    }
    public static Object Return_Info(String code, String message,Object data)
    {

        Hashtable ht = new Hashtable();
        ht.put("code", code);
        ht.put("message", message);
        ht.put("data", data);

        return Json.ToJson(ht);
    }
}
