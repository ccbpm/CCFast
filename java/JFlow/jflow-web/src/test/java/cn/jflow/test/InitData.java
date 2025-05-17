package cn.jflow.test;

import bp.da.DBAccess;
import bp.wf.Glo;
import cn.jflow.boot.JFlowApplication;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


/**
 * 初始化数据表
 * @author
 * @version 2023-2-28
 */
@ActiveProfiles("test")
@SpringBootTest(classes= JFlowApplication.class)
public class InitData extends AbstractJUnit4SpringContextTests {

    @Test
    public void initData() throws Exception{
        bp.da.Log.DebugWriteInfo("数据库初始化开始。");
        if(CheckIsDBInstall()){
            Glo.IsCanInstall();
        }
        //运行ccflow的安装.
        Glo.DoInstallDataBase("CH", 2);
        //执行ccflow的升级。
        Glo.UpdataCCFlowVer();
        //加注释.
        bp.pub.PubClass.AddComment();

        if (DBAccess.IsExitsTableCol("Port_Emp", "EmpSta") == true)
            DBAccess.DropTableColumn("Port_Emp", "EmpSta");

        if (DBAccess.IsExitsObject("WF_Flow") == true)
            bp.da.Log.DebugWriteInfo("数据库初始化完成。");
    }

    /**
     * 是否需要数据库连接
     * @return
     * @throws Exception
     */
    private boolean CheckIsDBInstall() throws Exception {
        //检查数据库连接.
        try
        {
            DBAccess.TestIsConnection();
        }
        catch (Exception ex)
        {
            throw new Exception("err@异常信息:" + ex.getMessage());
        }

        //检查是否缺少Port_Emp 表，如果没有就是没有安装.
        if (DBAccess.IsExitsObject("Port_Emp") == false && DBAccess.IsExitsObject("WF_Flow") == false)
            return true;

        //如果没有流程表，就执行安装.
        if (DBAccess.IsExitsObject("WF_Flow") == false)
            return true;
        return false;
    }


}
