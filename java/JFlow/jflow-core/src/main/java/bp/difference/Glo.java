package bp.difference;

import bp.da.AtPara;
import bp.da.DataType;

import java.util.Hashtable;

public class Glo {

    public static String DealSQLStringEnumFormat(String cfgString) {
        //把这个string,转化SQL. @tuanyuan=团员@dangyuan=党员
        AtPara ap = new AtPara(cfgString);
        String sql = "";
        for(String item : ap.getHisHT().keySet())
        {
            sql += " SELECT '" + item + "' as No, '" + ap.GetValStrByKey(item) + "' as Name FROM Port_Emp WHERE No = 'admin' UNION ";
        }
        sql = sql.substring(0, sql.length() - 6);
        return sql;
    }

    /**
     * 获取的是本机IP
     *
     * @return
     * @throws Exception
     */
    public static String getIP() {
        return bp.sys.base.Glo.getRequest().getLocalAddr();
    }

    public static String getRemotIP() {
        return bp.sys.base.Glo.getRequest().getRemoteAddr();
    }
    public static String DealExp(String exp, Hashtable ht)
    {
        if (exp == null)
            exp ="";

        exp = exp.replace("~", "'");
        exp = exp.replace("/#", "+"); //为什么？
        exp = exp.replace("/$", "-"); //为什么？
        if (exp.contains("@WebUser.No"))
            exp = exp.replace("@WebUser.No", bp.web.WebUser.getNo());

        if (exp.contains("@WebUser.Name"))
            exp = exp.replace("@WebUser.Name", bp.web.WebUser.getName());

        if (exp.contains("@WebUser.FK_DeptName"))
            exp = exp.replace("@WebUser.FK_DeptName", bp.web.WebUser.getDeptName());

        if (exp.contains("@WebUser.FK_Dept"))
            exp = exp.replace("@WebUser.FK_Dept", bp.web.WebUser.getDeptNo());

        if (exp.contains("@WebUser.DeptNo"))
            exp = exp.replace("@WebUser.DeptNo", bp.web.WebUser.getDeptNo());

        if (exp.contains("@") == true && ht != null)
        {
            for(Object key : ht.keySet())
            {
                if(key == null)
                    continue;
                //值为空或者null不替换
                if (ht.get(key) == null || ht.get(key).toString().equals("") == true)
                    continue;

                if (exp.contains("@" + key))
                    exp = exp.replace("@" + key, ht.get(key).toString());

                //不包含@则返回SQL语句
                if (exp.contains("@") == false)
                    break;
            }
        }

        if (exp.contains("@") && bp.difference.SystemConfig.isBSsystem() == true)
        {
            /*如果是bs*/
            for(String key : ContextHolderUtils.getRequest().getParameterMap().keySet())
            {
                if (DataType.IsNullOrEmpty(key))
                    continue;
                exp = exp.replace("@" + key, ContextHolderUtils.getRequest().getParameter(key));
            }
        }
        return exp;
    }
}
