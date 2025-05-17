package bp.wf.dts;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;

/** 
 Method 的摘要说明
*/
public class DeletTempFiles extends Method
{
	/** 
	 不带有参数的方法
	*/
	public DeletTempFiles()
	{
		this.Title = "删除产生的临时文件";
		this.Help = "上传、下载、导入导出流程模版表单模版的临时文件删除.";
		this.Icon = "<img src='/WF/Img/Btn/Delte.gif'  border=0 />";
		this.GroupName = "系统维护";

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
		return true;
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	*/
	@Override
	public Object Do()
	{

		try
		{
			Glo.DeleteTempFiles();
		}
		catch (RuntimeException ex)
		{
			bp.da.Log.DebugWriteError("删除临时文件错误:" + ex.getMessage());
			return "err@" + ex.getMessage();
		}
		return "删除成功.";
	}
}
