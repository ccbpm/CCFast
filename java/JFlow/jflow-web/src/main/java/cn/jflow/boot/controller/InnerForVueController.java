package cn.jflow.boot.controller;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.difference.handler.HttpHandlerBase;
import bp.sys.CCBPMRunModel;
import bp.tools.Json;
import bp.web.WebUser;
import bp.wf.Dev2Interface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Hashtable;

@RestController
@Api(tags="内部仅为vue调用")
@RequestMapping(value = "/WF/InnerForVue")
public class InnerForVueController extends HttpHandlerBase {
    public static Object Return_Info(int code, String msg, Object data)
    {
        Hashtable ht = new Hashtable();
        ht.put("code", code);
        ht.put("message", msg);
        ht.put("msg", msg);
        ht.put("data", data);
        return ht;
    }
    @GetMapping(value = "/Search_Emps")
    @ApiOperation("根据关键字查询人员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "keyword", value = "关键字查询", paramType = "query", required = true)
    })
    public Object Search_Emps(String Token, String keyword) throws Exception {
        //根据token登录
        Dev2Interface.Port_LoginByToken(Token);

        String sqlTemplate = "";
        String execSql = "";
        if (DataType.IsNullOrEmpty(keyword))
            return Return_Info(500,"err@请输入关键字","");
        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
        {
            sqlTemplate = "SELECT A.No,A.Name,B.Name AS DeptName FROM Port_Emp A,Port_Dept B WHERE A.FK_Dept=B.No AND ( A.No LIKE '%@Key%' OR A.Name LIKE '%@Key%' OR PinYin LIKE  '%@Key%') AND A.EmpSta = 0 Order By A.Idx  ";
            execSql = sqlTemplate.replace("@Key", keyword);
        }
        else
        {
            sqlTemplate = "SELECT A.No,A.Name,B.Name AS DeptName FROM Port_Emp A,Port_Dept B WHERE A.FK_Dept=B.No AND ( A.No LIKE '%@Key%' OR A.Name LIKE '%@Key%' OR PinYin LIKE  '%@Key%')  AND A.OrgNo='@WebUser.OrgNo' AND A.EmpSta = 0 Order By A.Idx ";
            execSql = sqlTemplate.replace("@Key", keyword);
            execSql = execSql.replace("@WebUser.OrgNo", WebUser.getOrgNo());
        }
        if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
            execSql = execSql.replace("SELECT","SELECT Top 20");
        else if (SystemConfig.getAppCenterDBType() == DBType.Oracle
                || SystemConfig.getAppCenterDBType() == DBType.DM
                || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3
                || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6
                || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
            execSql = "SELECT  * FROM("+execSql+")  WHERE ROWNUM =1";
        else
            execSql+= " LIMIT 20";

        DataTable dt = DBAccess.RunSQLReturnTable(execSql);

        if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
        {
            dt.Columns.get("NO").ColumnName = "No";
            dt.Columns.get("NAME").ColumnName = "Name";
            dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
        }

        if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
        {
            dt.Columns.get("no").ColumnName = "No";
            dt.Columns.get("name").ColumnName = "Name";
            dt.Columns.get("deptname").ColumnName = "DeptName";
        }
        return Return_Info(200,"根据关键字查询人员信息成功", Json.ToJson(dt));
    }

    @Override
    public Class getCtrlType() {
        return null;
    }
}
