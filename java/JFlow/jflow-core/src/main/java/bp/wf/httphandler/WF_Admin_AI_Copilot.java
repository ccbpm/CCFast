package bp.wf.httphandler;


import bp.da.*;
import bp.sys.*;
import bp.web.WebUser;

/**
 * 页面功能实体
 */
public class WF_Admin_AI_Copilot extends bp.difference.handler.DirectoryPageBase {
    public final String getWords()
    {
        return this.GetRequestVal("Words");
    }
    /**
     发起指定的流程

     @return
     */
    public final String Menu_Start_SpecFlow()
    {
        String sql = "";
        if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
        {
            sql = "SELECT No,Name FROM WF_Flow WHERE Name LIKE '%" + this.getWords() + "%'";
        }
        else
        {
            sql = "SELECT No,Name FROM WF_Flow WHERE Name LIKE '%" + this.getWords() + "%' AND OrgNo='" + WebUser.getOrgNo() + "'";
        }
        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        return bp.tools.Json.ToJson(dt);
    }
    /**
     构造函数
     */
//C# TO JAVA CONVERTER WARNING: The following constructor is declared outside of its associated class:
//ORIGINAL LINE: public WF_Admin_AI_Copilot()
    public WF_Admin_AI_Copilot()
    {
    }
}
