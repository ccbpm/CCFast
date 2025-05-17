package bp.ccbill.template;

import bp.da.*;
import bp.en.Map;
import bp.web.*;
import bp.en.*; import bp.en.Map;

/** 
 单实体流程查询
*/
public class MethodSingleDictGenerWorkFlow extends EntityNoName
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


		///#endregion


		///#region 构造方法
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
	 单实体流程查询
	*/
	public MethodSingleDictGenerWorkFlow()
	{
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

		Map map = new Map("Frm_Method", "单个实体流程查询");

		map.AddMyPK(true);

		map.AddTBString(MethodAttr.FrmID, null, "表单ID", true, true, 0, 300, 10);
		map.AddTBString(MethodAttr.MethodID, null, "方法ID", true, true, 0, 200, 10);
		map.AddTBString(MethodAttr.GroupID, null, "GroupID", true, true, 0, 200, 10);

		map.AddTBString(MethodAttr.Name, null, "方法名", true, false, 0, 300, 10);
		map.AddTBString(MethodAttr.Icon, null, "图标", true, false, 0, 50, 10, true);

		//功能标记.
		map.AddTBString(MethodAttr.MethodModel, null, "方法模式", true, false, 0, 300, 10);
		map.AddTBString(MethodAttr.Tag1, null, "Tag1", true, false, 0, 300, 10);
		map.AddTBString(MethodAttr.Mark, null, "Mark", true, false, 0, 300, 10);



			///#region 工具栏.
		map.AddBoolean(MethodAttr.IsMyBillToolBar, true, "是否显示在MyBill.htm工具栏上", true, true, true);
		map.AddBoolean(MethodAttr.IsMyBillToolExt, false, "是否显示在MyBill.htm工具栏右边的更多按钮里", true, true, true);
		map.AddBoolean(MethodAttr.IsSearchBar, false, "是否显示在Search.htm工具栏上(用于批处理)", true, true, true);

			///#endregion 工具栏.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getNo()) == true)
		{
			this.setNo(DBAccess.GenerGUID(0, null, null));
		}
		return super.beforeInsert();
	}

}
