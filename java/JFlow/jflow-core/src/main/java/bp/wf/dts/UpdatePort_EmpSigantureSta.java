package bp.wf.dts;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.web.WebUser;
import bp.en.*;
import java.io.*;

/**
 升级ccflow6 要执行的调度
 */
public class UpdatePort_EmpSigantureSta extends Method
{
	/**
	 不带有参数的方法
	 */
	public UpdatePort_EmpSigantureSta()throws Exception
	{
		this.Title = "同步用户签名状态(如果是在/DataUser/Siganture 有图片签名，就设置当前人员为图片签名状态.)";
		this.Help = "执行更新Port_Emp数据表的SignType字段.";
	}
	/**
	 设置执行变量

	 @return
	 */
	@Override
	public void Init()  {
	}
	/**
	 当前的操纵员是否可以执行这个方法
	 * @throws Exception
	 */
	@Override
	public boolean getIsCanDo()
	{
		if (WebUser.getNo().equals("admin") == true)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 执行

	 @return 返回执行结果
	 */
	@Override
	public Object Do() throws Exception {
		String path = SystemConfig.getPathOfDataUser() + "Siganture";
		File file1 = new File(path);
		String[] files = file1.list();

		//清空设置为图片签名的记录.
		String sql = "UPDATE Port_Emp SET SignType=0 WHERE SignType=1";
		DBAccess.RunSQL(sql);

		//遍历文件名.
		for (String file : files)
		{
			String userName = file.substring(file.lastIndexOf('/') + 1);
			userName = userName.substring(0, userName.lastIndexOf('.'));

			sql = "UPDATE Port_Emp SET SignType=1 WHERE No='" + userName + "'";
			DBAccess.RunSQL(sql);
		}

		return "执行完成，[" + files.length + "]笔数据.";
	}
}