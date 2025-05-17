package bp.ccbill.template;

import bp.da.*;
import bp.en.Map;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.ccbill.*;

/** 
 方法单据
*/
public class MethodBill extends EntityNoName
{

		///#region 基本属性
	/** 
	 表单ID
	*/
	public final String getFrmID() {
		return this.GetValStringByKey(MethodAttr.FrmID);
	}
	public final void setFrmID(String value)  {
		this.SetValByKey(MethodAttr.FrmID, value);
	}
	/** 
	 方法ID
	*/
	public final String getMethodID() {
		return this.GetValStringByKey(MethodAttr.MethodID);
	}
	public final void setMethodID(String value)  {
		this.SetValByKey(MethodAttr.MethodID, value);
	}

	public final String getTag1() throws Exception {
		String tag1 = this.GetValStringByKey(MethodAttr.Tag1);
		return tag1;
	}



		///#endregion


		///#region 构造方法
	/** 
	 权限控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (WebUser.getIsAdmin())
		{
			uac.IsUpdate = true;
			return uac;
		}
		return super.getHisUAC();
	}
	/** 
	 方法单据
	*/
	public MethodBill()
	{
	}
	public MethodBill(String mypk) throws Exception {
		this.setNo(mypk);
		this.Retrieve();
	}
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Frm_Method", "实体单据");

		//主键.
		map.AddTBStringPK(MethodAttr.No, null, "编号", true, true, 0, 50, 10);
		map.AddTBString(MethodAttr.Name, null, "方法名", true, false, 0, 300, 10);
		map.AddTBString(MethodAttr.MethodID, null, "方法ID", true, true, 0, 300, 10);
		map.AddTBString(MethodAttr.GroupID, null, "分组ID", true, true, 0, 50, 10);

		//功能标记.
		map.AddTBString(MethodAttr.MethodModel, null, "方法模式", true, true, 0, 300, 10);
		map.AddTBString(MethodAttr.Tag1, null, "Tag1", true, true, 0, 300, 10);
		map.AddTBString(MethodAttr.Mark, null, "Mark", true, true, 0, 300, 10);


		map.AddTBString(MethodAttr.FrmID, null, "表单ID", true, true, 0, 300, 10);
		map.AddTBString(MethodAttr.FlowNo, null, "流程编号", true, true, 0, 10, 10);

		map.AddTBString(MethodAttr.Icon, null, "图标", true, false, 0, 50, 10, true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/** 
	 新建工作
	 
	 @param workid 实体的实例ID
	 @return 
	*/
	public final String CreateWorkID(long workid) throws Exception {
		//获得当前的实体.
		GEEntity ge = new GEEntity(this.getFrmID(), workid);

		//创建单据ID.
		long workID = Dev2Interface.CreateBlankBillID(this.getTag1(), null, ge.getRow(), this.getFrmID(), workid);
		return String.valueOf(workID);
	}


		///#region 执行方法.
	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getNo()) == true)
		{
			this.setNo(DBAccess.GenerGUID(0, null, null));
		}
		return super.beforeInsert();
	}

		///#endregion 执行方法.

}
