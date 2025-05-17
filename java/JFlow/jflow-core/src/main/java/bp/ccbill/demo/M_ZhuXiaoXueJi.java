package bp.ccbill.demo;

import bp.sys.*;
import bp.*;
import bp.ccbill.*;

/** 
 注销学籍 方法
*/
public class M_ZhuXiaoXueJi extends bp.en.Method
{
	public M_ZhuXiaoXueJi()
	{
		this.Title = "注销学籍.";
		this.Help = "执行学籍的注销的业务逻辑， 关于该学生的借书信息、食堂信息等资料都需要注销掉.";
		this.GroupName = "CCBill的是实体Demo";
	}
	@Override
	public Object Do() throws Exception {
		//0. 获得参数.
		long workid = this.GetValIntByKey("WorkID"); //实体主键.
		String frmID = this.GetValStrByKey("FrmID"); //实体主键.

		//1. 检查是否有食堂欠费。

		//2. 检查图书馆借书是否归还？

		//3. 执行注销.(以下是采用ccbpm的语法法.)
		GEEntity en = new GEEntity(frmID, workid);
		en.SetValByKey("XSZT", 3); //修改字段值，
		en.Update();

		return "学籍已经注销了。";
	}


		///#region 重写。
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
		if (bp.web.WebUser.getIsAdmin() == true)
		{
			return true;
		}
		return false;
	}

		///#endregion 重写。

}
