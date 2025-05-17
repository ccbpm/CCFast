package bp.wf;

import bp.difference.SystemConfig;
import bp.sys.CCBPMRunModel;
import bp.web.WebUser;

public class SQLManager {
    public static String GenerSQLByMark(String mark)
    {
        if (mark.equals("srcDepts"))
        {
            if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single) return " SELECT No, Name, ParentNo FROM Port_Dept Order by Idx ";
            return "SELECT No, Name, ParentNo FROM Port_Dept WHERE OrgNo = '"+ WebUser.getOrgNo()+"' Order by Idx ";
        }

        if (mark.equals("srcStations"))
        {
            if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single) return "SELECT No, Name, FK_StationType GroupNo FROM Port_Station Order By Idx ";
            else return "SELECT No,Name,FK_StationType GroupNo FROM Port_Station WHERE OrgNo = '@WebUser.OrgNo' Order By Idx ";
        }

        if (mark.equals("srcEmps"))
        {
            return " SELECT No,Name,FK_Dept GroupNo FROM Port_Emp Order By Idx ";
        }

        if (mark.equals("srcStationTypes"))
        {
            if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single) return "SELECT No, Name FROM Port_StationType Order by Idx ";
            else return "SELECT No,Name FROM Port_StationType WHERE OrgNo = '@WebUser.OrgNo' Order by Idx ";
        }

        throw new RuntimeException("没有判断的标记:" + mark);
    }
}
