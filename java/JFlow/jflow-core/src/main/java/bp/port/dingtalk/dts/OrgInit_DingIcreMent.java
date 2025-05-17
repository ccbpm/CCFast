package bp.port.dingtalk.dts;

import bp.da.DataType;
import bp.en.*;
import bp.port.Emp;
import bp.port.dingtalk.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** 
 钉钉组织结构增量同步
*/
public class OrgInit_DingIcreMent extends Method
{
	/** 
	 钉钉组织结构增量同步
	*/
	public OrgInit_DingIcreMent()
	{
		this.Title = "同步增量钉钉通讯录到CCGPM";
		this.Help = "增量同步钉钉通讯录,需要时间比较长，请耐心等待。<br> 钉钉相关配置写入Web.config，配置正确才可以被执行";
	}
	/** 
	 设置执行变量
	 
	 @return 
	*/
	@Override
	public void Init()
	{
	}
	/** 
	 当前的操纵员是否可以执行这个方法
	*/
	@Override
	public boolean getIsCanDo()
	{
		if (bp.wf.Glo.isEnableDingDing() == true)
		{
			return true;
		}
		return false;
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	*/
	@Override
	public Object Do() throws Exception {
		Emp emp = new bp.port.Emp();
		emp.CheckPhysicsTable();

		DingDing ding = new DingDing();
		String result = ding.AnsyIncrementOrgToGPM();
		if (DataType.IsNullOrEmpty(result))
		{
			return "执行成功,没有发生变化...";
		}
		else if (result.contains("钉钉获取部门出错"))
		{
			return result;
		}
		else if (result.length() > 0)
		{
			String webPath = "Log/Ding_GPM.log";
			String savePath = bp.difference.SystemConfig.getPathOfDataUser() + webPath;
			File logFile = new File(savePath);
			// 如果目录不存在，则创建目录（注意：这里假设Log是目录，且需要创建）
			File dir = logFile.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs(); // 使用mkdirs()来创建多级目录
			}
			// 将result写入logFile(使用try-with-resources语句来自动关闭资源)
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) { // 第二个参数true表示追加模式
				writer.write(result);
				writer.newLine(); // 如果需要，在内容后添加新行
			} catch (IOException e) {
				e.printStackTrace();
			}
			bp.da.Log.DebugWriteInfo(result);
			return "执行成功<a href=\"/DataUser/" + webPath + "\" target='_blank'>下载日志</a>";
		}
		else
		{
			return "执行失败...";
		}
	}
}
