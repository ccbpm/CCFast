package bp.nbport;

import bp.da.*;
import bp.port.DeptEmpStation;
import bp.port.Station;
import bp.sys.CCBPMRunModel;
import bp.wf.Dev2Interface;
import net.sf.json.JSONObject;

public class OrgEmpCRUD {

    private String orgNo="1";
    private String orgName="浙江海港";
    public void dealOrgEmp(String message) throws Exception {
        JSONObject jsonData = JSONObject.fromObject(message);

        // 判断是否有顶层组织存在，没有就增加
        dealOrgAndAdminer();

        // Dev2Interface.Port_Login("admin","1");
        String type = (String) jsonData.get("type");

        if("ORG".equals(type)){
            //处理组织信息
            dealOrg(jsonData);
        }else if("user".equals(type)){
            //处理用户信息
            dealEmp(jsonData);
        }
    }
    public void dealOrgAndAdminer() throws Exception {
        bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org();
        org.setNo(orgNo);
        if (org.RetrieveFromDBSources() == 0){
            org.setName(orgName);
            org.setAdminer("admin");
            org.setAdminerName("管理员");
            org.Insert();
        }

        OrgAdminerForJC oa = new OrgAdminerForJC();
        oa.setMyPK(orgNo + "_" + "admin");
        if (oa.RetrieveFromDBSources() == 0){
            oa.setOrgNo(orgNo);
            oa.setEmpNo("admin");
            oa.setEmpName("管理员");
            oa.Insert();
        }

        //插入部门.
        bp.port.DeptEmp de = new bp.port.DeptEmp();
        de.setMyPK(orgNo + "_" + "admin");
        if (de.RetrieveFromDBSources() == 0) {
            de.setDeptNo(orgNo);
            de.setEmpNo("admin");
            de.setOrgNo(orgNo);
            de.setDeptName(orgName);
            de.Insert();
        }

        //增加管理员部门
        bp.port.Dept dept = new bp.port.Dept();
        dept.setNo(orgNo);
        if (dept.RetrieveFromDBSources() == 0)
        {
            dept.setName(orgName);
            dept.setParentNo("0");
            dept.setOrgNo(orgNo);
            dept.Insert();
        }
    }
    public void dealOrg(JSONObject jsonData) throws Exception {
        String action = (String) jsonData.get("action");
        if("add".equals(action) || "update".equals(action)){
            //增加部门.
            bp.nbport.DeptForJC dept = new bp.nbport.DeptForJC();
            dept.setNo(jsonData.get("orgCode").toString());
            if (dept.RetrieveFromDBSources() == 0)
            {
                dept.setName(jsonData.get("orgName").toString());
                dept.setParentNo(jsonData.get("parentCode").toString());
                dept.setOrgNo(orgNo);
                dept.Insert();
            }
            else
            {
                dept.setName(jsonData.get("orgName").toString());
                dept.setParentNo(jsonData.get("parentCode").toString());
                dept.setOrgNo(orgNo);
                dept.Update();
            }

            //更新可能存在的之前未同步的部门名称
            DataTable dt = DBAccess.RunSQLReturnTable("select * from port_DeptEmp where fk_dept ='"+dept.getNo()+"'");
            if(dt.Rows.size()>0) {
                for(DataRow dr:dt.Rows) {
                    bp.port.DeptEmp de = new bp.port.DeptEmp();
                    de.setMyPK(dr.getValue("MyPK").toString());
                    de.RetrieveFromDBSources();
                    de.setDeptName(dept.getName());
                    de.Update();
                }
            }
        }else if("delete".equals(action)){
            bp.port.Dept dept = new bp.port.Dept(jsonData.get("orgCode").toString());
            dept.Delete();
        }
    }

    public void dealEmp(JSONObject jsonData){
        String action = (String) jsonData.get("action");
        String userNo = jsonData.get("mobile").toString();
        String deptNo = jsonData.get("orgCode").toString();
        try {
            if ("add".equals(action) || "update".equals(action)) {
                bp.nbport.DeptForJC dept = new bp.nbport.DeptForJC();
                dept.setNo(deptNo);
                if (dept.RetrieveFromDBSources() == 0) {
                    dept.setName("未同步");
                    dept.setParentNo(orgNo);
                    dept.setOrgNo(orgNo);
                    dept.Insert();
                }

                //增加人员信息.
                bp.nbport.EmpForJC emp = new bp.nbport.EmpForJC();
                emp.setNo(userNo);
                if (emp.RetrieveFromDBSources() == 0) {
                    emp.setName(jsonData.get("name").toString());
                    emp.setDeptNo(deptNo);
                    emp.setOrgNo(orgNo);
                    emp.Insert();
                }

                emp.SetValByKey("tel", jsonData.get("mobile").toString());
                emp.setName(jsonData.get("name").toString());
                emp.setDeptNo(deptNo);
                emp.Update();

                DBAccess.RunSQL("DELETE FROM Port_DeptEmp WHERE FK_Emp='" + userNo + "' AND FK_Dept='"+deptNo+"' AND OrgNo='" + orgNo + "'");
                DBAccess.RunSQL("DELETE FROM Port_DeptEmpStation WHERE FK_Emp='" + userNo + "' AND FK_Dept='"+deptNo+"' AND OrgNo='" + orgNo + "'");

                //插入部门.
                bp.port.DeptEmp de = new bp.port.DeptEmp();
                de.setDeptNo(emp.getDeptNo());
                de.setEmpNo(userNo);
                de.setOrgNo(orgNo);
                de.setDeptName(dept.getName());
                de.setMyPK(de.getDeptNo() + "_" + userNo);

                Station st = new Station();
                //更新角色.
                String stationName = jsonData.get("postName").toString();
                DataTable dt = DBAccess.RunSQLReturnTable("select * from port_station where name ='"+stationName+"'");
                if(dt.Rows.size()>0) {
                    st.setNo(dt.Rows.get(0).getValue("No").toString());
                }else {
                    st.setName(stationName);
                    st.setNo(DBAccess.GenerGUID());
                    st.setFKStationType("3");
                    st.setOrgNo(orgNo);
                    st.DirectInsert();
                }
                //插入部门.
                DeptEmpStation des = new DeptEmpStation();
                des.setDeptNo(deptNo);
                des.setEmpNo(userNo);
                des.setStationNo(st.getNo());
                des.setOrgNo(orgNo);
                des.setMyPK(de.getDeptNo() + "_" + des.getEmpNo() + "_" + des.getStationNo());
                des.DirectInsert();

                de.setStationNo(st.getNo());
                de.setStationNoT(stationName);
                de.DirectInsert();

                String sql = "UPDATE Port_Emp SET OrgNo='" + orgNo + "' WHERE No='" + emp.getNo() + "'";
                DBAccess.RunSQL(sql);

            } else if ("delete".equals(action)) {
                DBAccess.RunSQL("DELETE FROM Port_Emp WHERE No='" + userNo + "' AND FK_Dept='"+deptNo+"' AND OrgNo='" + orgNo + "'");
                DBAccess.RunSQL("DELETE FROM Port_DeptEmp WHERE FK_Emp='" + userNo + "' AND FK_Dept='"+deptNo+"' AND OrgNo='" + orgNo + "'");
                DBAccess.RunSQL("DELETE FROM Port_DeptEmpStation WHERE FK_Emp='" + userNo + "' AND FK_Dept='"+deptNo+"' AND OrgNo='" + orgNo + "'");
            }
        }catch (RuntimeException ex)
        {
             ex.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
