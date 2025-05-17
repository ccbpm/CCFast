package cn.jflow.boot.controller;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.port.*;
import bp.tools.Encodes;
import bp.tools.MD5Utill;
import bp.web.WebUser;
import bp.wf.*;
import bp.wf.port.WFEmp;
import bp.wf.template.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 海南国控蔬菜项目
 */
@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/HN_BPM")
public class HNController {

    @PostMapping("/sendSMS")
    public Map<String, Object> sendSMS(@RequestBody com.alibaba.fastjson.JSONObject param){
        Map<String, Object> map=new HashMap<>();
        try {
            String token = param.getString("token");
//            GenerWorkFlows generWorkFlows=new GenerWorkFlows();
//            generWorkFlows.Retrieve(GenerWorkFlowAttr.BillNo,bah);
//            if(generWorkFlows.size()==0||generWorkFlows.size()>1){
//                map.put("code",500);
//                map.put("msg","执行错误，备案号："+bah+"不存在");
//                return  map;
//            }
//            else{
//                GenerWorkFlow gwf=(GenerWorkFlow)generWorkFlows.get(0);
//                bp.wf.Dev2Interface.Port_Login("YTHPT");
//                SendReturnObjs objs=bp.wf.Dev2Interface.Node_SendWork(gwf.getFlowNo(),gwf.getWorkID());
//                String sendMsg= objs.getMsgOfText();
//                bp.da.Log.DefaultLogWriteLine(LogType.Info, "接收一体化参数后发送：" + sendMsg);
//
//                map.put("code",200);
//                map.put("msg","执行成功");
//                return  map;
//            }
            return  map;
        }
        catch (Exception ex){
            Log.DefaultLogWriteLine(LogType.Info, "接收一体化参数后发送异常：" + ex.getMessage());
            map.put("code",500);
            map.put("msg","调用错误："+ex.getMessage());
            return  map;
        }
    }
   //接收一体化推送数据
    @PostMapping("/sendNext")
    public Map<String, Object> sendNext( @RequestBody JSONObject param){
        Map<String, Object> map=new HashMap<>();
        try {
            Boolean isTrue=checkSign(param);
            //签名判断
            if(!isTrue){
                map.put("code", 500);
                map.put("token",0);
                map.put("message","签名验证失败.");
                return map;
            }
            String bah = param.getString("BAH");
            GenerWorkFlows generWorkFlows=new GenerWorkFlows();
            generWorkFlows.Retrieve(GenerWorkFlowAttr.BillNo,bah);
            if(generWorkFlows.size()==0||generWorkFlows.size()>1){
                map.put("code",500);
                map.put("msg","执行错误，备案号："+bah+"不存在");
                return  map;
            }
            else{
                GenerWorkFlow gwf=(GenerWorkFlow)generWorkFlows.get(0);
                Dev2Interface.Port_Login("YTHPT");
                SendReturnObjs objs= Dev2Interface.Node_SendWork(gwf.getFlowNo(),gwf.getWorkID());
                String sendMsg= objs.getMsgOfText();
                Log.DefaultLogWriteLine(LogType.Info, "接收一体化参数后发送：" + sendMsg);

                map.put("code",200);
                map.put("msg","执行成功");
                return  map;
            }
        }
        catch (Exception ex){
            Log.DefaultLogWriteLine(LogType.Info, "接收一体化参数后发送异常：" + ex.getMessage());
            map.put("code",500);
            map.put("msg","调用错误："+ex.getMessage());
            return  map;
        }
    }
    /**
     * 退回，海南一体化
     *
     * @return 退回信息
     * @throws Exception
     */

    @CrossOrigin(origins = "*")
    @PostMapping("/returnWorkYTH")
    public Map<String, Object> returnWorkYTH(@RequestBody JSONObject param){
        Map<String, Object> map=new HashMap<>();
        try {
            //帐号
            String userNo = param.get("userNo").toString();
            //时间戳
            Long timestamp=Long.parseLong(param.get("timestamp").toString());
            //签名
            String sign=param.get("sign").toString();
            Boolean isTrue=checkSign(param);
            //签名判断
            if(!isTrue){
                map.put("code", 500);
                map.put("token",0);
                map.put("message","签名验证失败.");
                return map;
            }
            String bah = param.getString("BAH");
            GenerWorkFlows generWorkFlows=new GenerWorkFlows();
            generWorkFlows.Retrieve(GenerWorkFlowAttr.BillNo,bah);
            if(generWorkFlows.size()==0||generWorkFlows.size()>1){
                map.put("code",500);
                map.put("msg","执行错误，备案号："+bah+"不存在");
                return  map;
            }
            //声明参数类
            Paras ps = new Paras();
            //设置字符
            String dbstr = SystemConfig.getAppCenterDBVarStr();
            JSONObject res = new JSONObject();
            //退回信息
            String msg = param.get("returnMsg").toString();
            GenerWorkFlow gwf=(GenerWorkFlow)generWorkFlows.get(0);
            Dev2Interface.Port_Login("YTHPT");

            try {
                //执行退回
                String returnMsg = Dev2Interface.Node_ReturnWork(gwf.getWorkID(), 0, msg,false);
                Log.DefaultLogWriteLine(LogType.Info, "退回成功:" + bah);
                //执行退出
                Dev2Interface.Port_SigOut();
                map.put("code",200);
                map.put("msg",returnMsg);
                return  map;

            } catch (Exception ex) {
                //执行退出
                Dev2Interface.Port_SigOut();
                Log.DefaultLogWriteLine(LogType.Info, "退回失败:" + ex.getMessage().toString());
                map.put("code",500);
                map.put("msg",ex.getMessage());
                return  map;
            }
        }
        catch (Exception ex){
            map.put("code",500);
            map.put("msg",ex.getMessage());
            return  map;
        }
    }
    /**
     * 获取审批轨迹，海南一体化
     *
     * @return 退回信息
     * @throws Exception
     */

    @CrossOrigin(origins = "*")
    @PostMapping("/getTrackInfo")
    public Map<String,Object> getTrackInfo(@RequestBody JSONObject param){
        Map<String, Object> map=new HashMap<>();
        try {
            Boolean isTrue=checkSign(param);
            //签名判断
            if(!isTrue){
                map.put("code", 500);
                map.put("token",0);
                map.put("message","签名验证失败.");
                return map;
            }
            String bah = param.getString("BAH");
            GenerWorkFlows generWorkFlows=new GenerWorkFlows();
            generWorkFlows.Retrieve(GenerWorkFlowAttr.BillNo,bah);
            if(generWorkFlows.size()==0||generWorkFlows.size()>1){
                map.put("code",500);
                map.put("msg","执行错误，备案号："+bah+"不存在");
                return  map;
            }
            GenerWorkFlow gwf=(GenerWorkFlow)generWorkFlows.get(0);
            Dev2Interface.Port_Login("YTHPT");
            //获取干流程和子线程中的Track信息
            DataTable dt = Dev2Interface.DB_GenerTrackTable(gwf.getFlowNo(), gwf.getWorkID(), gwf.getPFID(), false);
            map.put("code",200);
            map.put("msg","执行成功");
            map.put("result",bp.tools.Json.ToJson(dt));
            //执行退出
            Dev2Interface.Port_SigOut();
            return map;
        }
        catch (Exception ex){
            try {
                //执行退出
                Dev2Interface.Port_SigOut();
            }
            catch (Exception e){}
            map.put("code",500);
            map.put("msg","执行异常："+ex.getMessage().toString());
            return map;
        }
    }
    /**
     * 发送短信通知，海南一体化
     *
     * @return 退回信息
     * @throws Exception
     */

    @CrossOrigin(origins = "*")
    @PostMapping("/sendMsm")
    public Map<String,Object> sendMsm(@RequestBody JSONObject param){
        Map<String, Object> map=new HashMap<>();
        try {
            Boolean isTrue=checkSign(param);
            //签名判断
            if(!isTrue){
                map.put("code", 500);
                map.put("token",0);
                map.put("message","签名验证失败.");
                return map;
            }
            String bah = param.getString("BAH");
            String role = param.getString("ROLE");
            GenerWorkFlows generWorkFlows=new GenerWorkFlows();
            generWorkFlows.Retrieve(GenerWorkFlowAttr.BillNo,bah);
            if(generWorkFlows.size()==0||generWorkFlows.size()>1){
                map.put("code",500);
                map.put("msg","执行错误，备案号："+bah+"不存在");
                return  map;
            }
            GenerWorkFlow gwf=(GenerWorkFlow)generWorkFlows.get(0);
            Dev2Interface.Port_Login("YTHPT");
            //计算接收人，按照当前bpm中的节点计算，一体化所有环节在bpm中只有一个节点代表
            //如果是分管领导，直接获取下一步的处理人即可
            DataTable empDt=Dev2Interface.Node_GetNextStepEmpsByNodeID(gwf.getNodeID(),gwf.getWorkID());
            //如果是主要领导，获取下下步骤的处理人
            if(role.equals("sxld")){
                //先获取下一步的节点
                Directions toNodes=Dev2Interface.Node_GetNextStepNodesByNodeID(gwf.getNodeID());
                Direction direction=(Direction)toNodes.get(0);
                //在获取下下一步的节点
                Directions nextNodes=Dev2Interface.Node_GetNextStepNodesByNodeID(direction.getToNode());
                Direction nextNode=(Direction)nextNodes.get(0);
                empDt=Dev2Interface.Node_GetNextStepEmpsByNodeID(nextNode.getToNode(),gwf.getWorkID());
            }
            String emps="";
            for (DataRow row: empDt.Rows){
                if(DataType.IsNullOrEmpty(row.get("No")))
                    emps+=row.get("no")+",";
                else
                    emps+=row.get("No")+",";
            }
            emps=emps.substring(0,emps.length()-1);
            Dev2Interface.Port_SendMessage(emps,"尊敬的领导，您在一体化平台有条待办项需要您审核，请登录手机APP进行审核","待办事项审核","Todolist","","YTHPT","","",gwf.getWorkID());
            map.put("code",200);
            map.put("msg","执行成功");
            //执行退出
            Dev2Interface.Port_SigOut();
            return map;
        }
        catch (Exception ex){
            try {
                //执行退出
                Dev2Interface.Port_SigOut();
            }
            catch (Exception e){}
            map.put("code",500);
            map.put("msg","执行异常："+ex.getMessage());
            return map;
        }
    }
    /**
     *  海口用户角色权限数据维护接口
     *  业务：
     *   1.先判断签名是否正确
     *   2.判断属性字段是否缺失
     *   3.处理部门表数据
     *   4.处理用户表数据
     *   5.处理角色表数据
     *   6.处理用户角色表数据
     *     1）筛选出本次接口数据中用户角色对象数组中涉及的用户并删除其在用户角色表中的所有数据，再插入本此接口的数据
     *     2）del_flag=2时，不插入本条数据
     *
     * @return 退回信息
     * @throws Exception
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/permission_data")
    public Map<String,Object> hk_permission_data(@RequestBody JSONObject param){
        Map<String, Object> map=new HashMap<>();
        returnMap(map,1,null,null);
        Log.DefaultLogWriteLine(LogType.Info, "海口用户角色权限数据维护接口：");
        Log.DefaultLogWriteLine(LogType.Info, param.toString());
        try {
            Boolean isTrue=checkSign(param);
            //签名判断
            if(!isTrue){
                returnMap(map,0,"-1",null);
                Log.DefaultLogWriteLine(LogType.Info, "海口用户角色权限数据维护接口_back：");
                Log.DefaultLogWriteLine(LogType.Info, map.toString());
                return map;
            }
            Map<String, Object> pmap=parseJSON2Map(param);
            isTrue=fieldChecks(pmap,map);
            //字段缺失
            if(!isTrue){
                Log.DefaultLogWriteLine(LogType.Info, "海口用户角色权限数据维护接口_back：");
                Log.DefaultLogWriteLine(LogType.Info, map.toString());
                return map;
            }
            //部门数据处理
            isTrue=deptsDeal(pmap,map);
            if(!isTrue){
                Log.DefaultLogWriteLine(LogType.Info, "海口用户角色权限数据维护接口_back：");
                Log.DefaultLogWriteLine(LogType.Info, map.toString());
                return map;
            }
            //用户数据处理
            isTrue=usersDeal(pmap,map);
            if(!isTrue){
                Log.DefaultLogWriteLine(LogType.Info, "海口用户角色权限数据维护接口_back：");
                Log.DefaultLogWriteLine(LogType.Info, map.toString());
                return map;
            }
            //角色数据处理
            isTrue=rolesDeal(pmap,map);
            if(!isTrue){
                Log.DefaultLogWriteLine(LogType.Info, "海口用户角色权限数据维护接口_back：");
                Log.DefaultLogWriteLine(LogType.Info, map.toString());
                return map;
            }
            //用户角色数据处理
            isTrue=user_roles(pmap,map);
            if(!isTrue){
                Log.DefaultLogWriteLine(LogType.Info, "海口用户角色权限数据维护接口_back：");
                Log.DefaultLogWriteLine(LogType.Info, map.toString());
                return map;
            }
        }
        catch (Exception ex){
            returnMap(map,0,"9",ex.getMessage());
        }
        Log.DefaultLogWriteLine(LogType.Info, "海口用户角色权限数据维护接口_back：");
        Log.DefaultLogWriteLine(LogType.Info, map.toString());
        return map;
    }
    //部门数据处理
  private boolean deptsDeal(Map<String, Object> pmap,Map<String, Object> map){
      try{
          if(!pmap.containsKey("depts") || !(pmap.get("depts") instanceof List)){
              return true;
          }
          List<Map<String, Object>>list= (List<Map<String, Object>>) pmap.get("depts");
          for(Map<String, Object>m:list){
              try {
                  String dept_id = m.get("dept_id").toString();
                  String del_flag = m.get("del_flag").toString();
                  String parent_id = m.get("parent_id").toString();
                  String dept_name = m.get("dept_name").toString();
                  String province_code = m.get("province_code").toString();
                  String city_code = m.get("city_code").toString();
                  String street_code= m.get("street_code").toString();
                  String city_level = m.get("city_level").toString();
                  Dept dept = new Dept();
                  dept.setNo(dept_id);
                  //删除
                  if (del_flag.equals("2")) {
                      dept.Delete();
                      //处理GK_DeptCity
                      Paras ps = new Paras();
                      ps.SQL = "update GK_DeptCity set del_flag=2  WHERE no=" + SystemConfig.getAppCenterDBVarStr() + "dept_id";
                      ps.Add("dept_id",dept_id);
                      bp.da.DBAccess.RunSQL(ps);
                      //处理部门用户表
                      DeptEmp deptEmp=new DeptEmp();
                      deptEmp.Delete("FK_Dept",dept_id);
                      //处理部门用户角色表
                      DeptEmpStation deptEmpStation=new DeptEmpStation();
                      deptEmpStation.Delete("FK_Dept",dept_id);

                  } else {
                     int i= dept.RetrieveFromDBSources();
                     dept.setParentNo(parent_id);
                     dept.setName(dept_name);
                     //插入
                     if(i==0){
                         dept.Insert();
                         Paras ps = new Paras();
                         ps.SQL = "insert into GK_DeptCity(no,name,parentno,province_code,city_code,street_code,city_level,del_flag)" +
                                 " values(" +
                                  SystemConfig.getAppCenterDBVarStr() + "no," + SystemConfig.getAppCenterDBVarStr() + "name," +
                                  SystemConfig.getAppCenterDBVarStr() + "parentno," + SystemConfig.getAppCenterDBVarStr() + "province_code," +
                                  SystemConfig.getAppCenterDBVarStr() + "city_code," + SystemConfig.getAppCenterDBVarStr() + "street_code," +
                                  SystemConfig.getAppCenterDBVarStr() + "city_level," + SystemConfig.getAppCenterDBVarStr() + "del_flag" +
                                 ")";
                         ps.Add("no",dept_id);
                         ps.Add("name",dept_name);
                         ps.Add("parentno",parent_id);
                         ps.Add("province_code",province_code);
                         ps.Add("city_code",city_code);
                         ps.Add("street_code",street_code);
                         ps.Add("city_level",city_level);
                         ps.Add("del_flag",0);
                         bp.da.DBAccess.RunSQL(ps);
                     }else{
                         dept.Update();
                         Paras ps = new Paras();
                         ps.SQL = "update GK_DeptCity set " +
                                  "name=" + SystemConfig.getAppCenterDBVarStr() + "name," +
                                  "parentno=" + SystemConfig.getAppCenterDBVarStr() + "parentno,province_code=" + SystemConfig.getAppCenterDBVarStr() + "province_code," +
                                 "city_code=" + SystemConfig.getAppCenterDBVarStr() + "city_code,street_code=" + SystemConfig.getAppCenterDBVarStr() + "street_code" +
                                 "  WHERE no=" + SystemConfig.getAppCenterDBVarStr() + "dept_id";
                         ps.Add("name",dept_name);
                         ps.Add("parentno",parent_id);
                         ps.Add("province_code",province_code);
                         ps.Add("city_code",city_code);
                         ps.Add("street_code",street_code);
                         ps.Add("dept_id",dept_id);
                         bp.da.DBAccess.RunSQL(ps);
                     }
                  }
              }catch (Exception ex){
               ex.printStackTrace();
              }
          }
      }catch (Exception e){
          e.printStackTrace();
          return false;
      }
      return true;
  }
    //用户数据处理
    private boolean usersDeal(Map<String, Object> pmap,Map<String, Object> map){
        try{
            if(!pmap.containsKey("users") || !(pmap.get("users") instanceof List)){
                return true;
            }
            List<Map<String, Object>>list= (List<Map<String, Object>>) pmap.get("users");
            for(Map<String, Object>m:list){
                try {
                    String user_id = m.get("user_id").toString();
                    String dept_id = m.get("dept_id").toString();
                    String user_name = m.get("user_name").toString();
                    String email = m.get("email").toString();
                    String del_flag= m.get("del_flag").toString();
                    Emp emp = new Emp();
                    emp.setNo(user_id);
                    //删除
                    if (del_flag.equals("2")) {
                        emp.Delete();
                        //处理部门用户表
                        DeptEmp deptEmp=new DeptEmp();
                        deptEmp.Delete("FK_Emp",user_id);
                        //处理部门用户角色表
                        DeptEmpStation deptEmpStation=new DeptEmpStation();
                        deptEmpStation.Delete("FK_Emp",user_id);
                    } else {
                        int i = emp.RetrieveFromDBSources();
                        emp.setName(user_name);
                        emp.setDeptNo(dept_id);
                        emp.setEmail(email);
                        Dept dept = new Dept();
                        dept.setNo(dept_id);
                        int j= dept.RetrieveFromDBSources();
                        //用户部门不存在
                        if(j==0){
                            returnMap(map,0,"1",dept_id);
                            return false;
                        }
                        //插入
                        if (i == 0) {
                         emp.Insert();
                         //处理部门用户表
                         DeptEmp deptEmp=new DeptEmp();
                         deptEmp.setDeptName(dept.getName());
                         deptEmp.setDeptNo(dept.getNo());
                         deptEmp.setEmpNo(user_id);
                         deptEmp.setMyPK(dept.getNo()+"_"+user_id);
                         deptEmp.Insert();
                        }else{
                         emp.Update();
                        //处理部门用户表
                        DeptEmp deptEmp=new DeptEmp();
                        deptEmp.setMyPK(dept.getNo()+"_"+user_id);
                        int E=deptEmp.RetrieveFromDBSources();
                        deptEmp.setDeptName(dept.getName());
                        deptEmp.setDeptNo(dept.getNo());
                        deptEmp.setEmpNo(user_id);
                        //部门删除时，本删除了
                        if(E==0){
                            deptEmp.Insert();
                        }else{
                            deptEmp.Update();
                        }
                       }
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }
    //角色数据处理
    private boolean rolesDeal(Map<String, Object> pmap,Map<String, Object> map){
            if(!pmap.containsKey("roles") || !(pmap.get("roles") instanceof List)){
                return true;
            }
            List<Map<String, Object>>list= (List<Map<String, Object>>) pmap.get("roles");
            for(Map<String, Object>m:list) {
                try {
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

                } catch (Exception e) {
                    return false;
                }
            }
        return true;
    }
    //用户角色数据处理
    private boolean user_roles(Map<String, Object> pmap,Map<String, Object> map){
        try{
            if(!pmap.containsKey("user_roles") || !(pmap.get("user_roles") instanceof List)){
                return true;
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
                        returnMap(map,0,"2","user_id:"+user_id);
                        return false;
                    }
                    Station station = new Station();
                    station.setNo(role_id);
                    i=station.RetrieveFromDBSources();
                    //角色不存在
                    if(i==0){
                        returnMap(map,0,"2","role_id:"+role_id);
                        return false;
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
            return false;
        }
        return true;
    }
  //字段验证
  private boolean fieldChecks(Map<String, Object> pmap,Map<String, Object> map){
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
                returnMap(map,0,"0",fields.toString());
                return false;
            }
            if(pmap.containsKey("users") && pmap.get("users") instanceof List){
                List<Map<String, Object>>list= (List<Map<String, Object>>) pmap.get("users");
                fieldCheck(users,list,fields);
            }
            if(!fields.toString().equals("")){
                returnMap(map,0,"0",fields.toString());
                return false;
            }
            if(pmap.containsKey("roles") && pmap.get("roles") instanceof List){
                List<Map<String, Object>>list= (List<Map<String, Object>>) pmap.get("roles");
                fieldCheck(roles,list,fields);
            }
            if(!fields.toString().equals("")){
                returnMap(map,0,"0",fields.toString());
                return false;
            }
            if(pmap.containsKey("user_roles") && pmap.get("user_roles") instanceof List){
                List<Map<String, Object>>list= (List<Map<String, Object>>) pmap.get("user_roles");
                fieldCheck(user_roles,list,fields);
            }
            if(!fields.toString().equals("")){
                returnMap(map,0,"0",fields.toString());
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
  }
   private void fieldCheck(List<String>fields,List<Map<String, Object>>list,StringBuilder losefields){
       list.stream().forEach(i->{
           fields.forEach(j->{
               boolean ischeck=false;
               for (String key : i.keySet()) {
                   if(j.equals(key)){
                       ischeck=true;
                       continue;
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
    /**
     * 海南一体化，签名认证
     * @return
     */
    private  Boolean checkSign(JSONObject param){
        try {
            //帐号
            String userNo = param.get("userNo").toString();
            //时间戳
            Long timestamp = Long.parseLong(param.get("timestamp").toString());
            //签名
            String sign = param.get("sign").toString();
            //当前时间
            Long timesNow = System.currentTimeMillis();
            //获取当前分钟数
            Long mins = (timesNow - timestamp) / (1000 * 60);
            if (mins > 5) {
//                return false;
            }
            String signStr = userNo + "_" + timestamp;
            String signMD5 = MD5Utill.MD5Encode(signStr, "UTF-8");
            //签名判断
            if (!signMD5.equals(sign)) {
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
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
    private void  returnMap( Map<String, Object> map,int code,String result,String info){
        if(code==1){
            map.put("code", 1);
            map.put("msg","调用成功");
        }else{
            map.put("code", 0);
            map.put("msg","调用失败");
            map.put("result",result);
            map.put("info",info==null?"":info);
        }
    }

    public static void main(String[] args) {
        //当前时间
        Long timesNow = System.currentTimeMillis();
        String signStr = "Permission_data" + "_" + timesNow;
        String signMD5 = MD5Utill.MD5Encode(signStr, "UTF-8");
        System.out.println(timesNow);
        System.out.println(signMD5);
    }
}

