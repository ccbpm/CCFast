package bp.sys.base;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.web.*;
import bp.*;
import bp.sys.*;
import java.time.*;
import java.util.Date;

/** 
 事件基类
*/
public abstract class EventBase
{

		///#region 属性.
	public Entity HisEn = null;
	private Row _SysPara = null;
	/** 
	 参数
	*/
	public final Row getSysPara()
	{
		if (_SysPara == null)
		{
			_SysPara = new Row();
		}
		return _SysPara;
	}
	public final void setSysPara(Row value)
	{
		_SysPara = value;
	}
	/** 
	 成功信息
	*/
	public String SucessInfo = null;
	private String _title = null;
	/** 
	 标题
	*/
	public final String getTitle()
	{
		if (_title == null)
		{
			_title = "未命名";
		}
		return _title;
	}
	public final void setTitle(String value)
	{
		_title = value;
	}

		///#endregion 属性.


		///#region 系统参数
	/** 
	 表单ID
	*/
	public final String getFKMapdata()  {
		return this.GetValStr("FK_MapData");
	}
	/** 
	 事件类型
	*/
	public final String getEventSource()  {
		return this.GetValStr("EventSource");
	}


		///#endregion


		///#region 常用属性.
	/** 
	 工作ID
	*/
	public final int getOID()  {
		return this.GetValInt("OID");
	}
	/** 
	 工作ID
	*/
	public final long getWorkID() throws Exception {
		if (this.getOID() == 0)
		{
			return this.GetValInt64("WorkID"); //有可能开始节点的WorkID=0
		}
		return this.getOID();
	}
	/** 
	 FID
	*/
	public final long getFID()  {
		return this.GetValInt64("FID");
	}
	/** 
	 流程编号
	*/
	public final String getFlowNo()  {
		return this.GetValStr("FK_Flow");
	}
	/** 
	 节点编号
	*/
	public final int getNodeID()
	{
		try
		{
			return this.GetValInt("FK_Node");
		}
		catch (java.lang.Exception e)
		{
			return 0;
		}
	}
	/** 
	 传过来的WorkIDs集合，子流程.
	*/
	public final String getWorkIDs()  {
		return this.GetValStr("WorkIDs");
	}
	/** 
	 编号集合s
	*/
	public final String getNos()  {
		return this.GetValStr("Nos");
	}

		///#endregion 常用属性.


		///#region 获取参数方法
	public final Date GetValDateTime(String key)
	{
		String str = this.getSysPara().GetValByKey(key).toString();
		return DataType.ParseSysDateTime2DateTime(str);
	}
	/** 
	 获取字符串参数
	 
	 @param key key
	 @return 如果为Nul,或者不存在就抛出异常
	*/
	public final String GetValStr(String key)
	{
		return this.getSysPara().GetValByKey(key).toString();
	}
	/** 
	 获取Int64的数值
	 
	 @param key 键值
	 @return 如果为Nul,或者不存在就抛出异常
	*/
	public final long GetValInt64(String key)
	{
		return Long.parseLong(this.GetValStr(key));
	}
	/** 
	 获取int的数值
	 
	 @param key 键值
	 @return 如果为Nul,或者不存在就抛出异常
	*/
	public final int GetValInt(String key)
	{
		return Integer.parseInt(this.GetValStr(key));
	}

		///#endregion 获取参数方法

	/** 
	 事件基类
	*/
	public EventBase()
	{
	}
	/** 
	 执行事件
	 1，如果遇到错误就抛出异常信息，前台界面就会提示错误并不向下执行。
	 2，执行成功，把执行的结果赋给SucessInfo变量，如果不需要提示就赋值为空或者为null。
	 3，所有的参数都可以从  this.SysPara.GetValByKey中获取。
	*/
	public abstract void Do();
	/** 
	 获得最后一个action的ID.
	 
	 @return 
	*/
	public final String GetLastActionTrackID() throws Exception {
		String sql = "SELECT  MyPK FROM ND" + Integer.parseInt(this.getFlowNo()) + "Track WHERE WorkID=" + this.getWorkID() + " AND NDFrom=" + this.getNodeID() + " ORDER BY RDT ";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			return null;
		}
		return dt.Rows.get(0).getValue(0).toString();
	}
}
