package bp.ccbill.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 分组
*/
public class GroupMethod extends EntityNoName
{

		///#region 权限控制
	/** 
	 权限控制.
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getNo().equals("admin") == true || bp.web.WebUser.getIsAdmin())
		{
			uac.IsDelete = true;
			uac.IsInsert = false;
			uac.IsUpdate = true;
			return uac;
		}
		uac.Readonly();
		uac.IsView = false;
		return uac;
	}

		///#endregion 权限控制


		///#region 属性
	/** 
	 表单ID
	*/
	public final String getFrmID()  {
		return this.GetValStrByKey(GroupMethodAttr.FrmID);
	}
	public final void setFrmID(String value)  {
		this.SetValByKey(GroupMethodAttr.FrmID, value);
	}
	public final String getOrgNo()  {
		return this.GetValStrByKey(GroupMethodAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(GroupMethodAttr.OrgNo, value);
	}
	public final String getMethodType()  {
		return this.GetValStrByKey(GroupMethodAttr.MethodType);
	}
	public final void setMethodType(String value)  {
		this.SetValByKey(GroupMethodAttr.MethodType, value);
	}
	/** 
	 顺序号
	*/
	public final int getIdx() {
		return this.GetValIntByKey(GroupMethodAttr.Idx);
	}
	public final void setIdx(int value)  {
		this.SetValByKey(GroupMethodAttr.Idx, value);
	}
	/** 
	 图标
	*/
	public final String getIcon()  {
		return this.GetValStrByKey(GroupMethodAttr.Icon);
	}
	public final void setIcon(String value)  {
		this.SetValByKey(GroupMethodAttr.Icon, value);
	}
	public final String getMethodID()  {
		return this.GetValStrByKey(GroupMethodAttr.MethodID);
	}
	public final void setMethodID(String value)  {
		this.SetValByKey(GroupMethodAttr.MethodID, value);
	}


		///#endregion


		///#region 构造方法
	/** 
	 GroupMethod
	*/
	public GroupMethod()
	{
	}
	/**
	 * EnMap
	 */
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Frm_GroupMethod", "方法分组");


			///#region 字段.
		map.AddTBStringPK(GroupMethodAttr.No, null, "编号", true, true, 0, 150, 20);
		map.AddTBString(GroupMethodAttr.FrmID, null, "表单ID", true, true, 0, 200, 20);

		map.AddTBString(GroupMethodAttr.Name, null, "标签", true, false, 0, 500, 20, true);
		map.AddTBString(GroupMethodAttr.Icon, null, "Icon", true, true, 0, 200, 20, true);

		map.AddTBString(GroupMethodAttr.OrgNo, null, "OrgNo", true, true, 0, 40, 20, true);

		map.AddTBInt(GroupMethodAttr.Idx, 0, "顺序号", true, false);
		map.AddTBAtParas(3000);

			///#endregion 字段.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 方法.
	@Override
	protected boolean beforeDelete() throws Exception
	{
		Methods ens = new Methods();
		ens.Retrieve(MethodAttr.GroupID, this.getNo(), null);
		if (ens.size() != 0)
		{
			throw new RuntimeException("err@该目录下有数据，您不能删除目录。");
		}

		return super.beforeDelete();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		return super.beforeUpdate();
	}
	public final String DoDown() throws Exception
	{
		this.DoOrderDown(GroupMethodAttr.FrmID, this.getFrmID(), GroupMethodAttr.Idx);
		return "执行成功";
	}
	public final String DoUp() throws Exception
	{
		this.DoOrderUp(GroupMethodAttr.FrmID, this.getFrmID(), GroupMethodAttr.Idx);
		return "执行成功";
	}
	@Override
	protected boolean beforeInsert()  throws Exception
	{
		//设置主键.
		if (DataType.IsNullOrEmpty(this.getNo()) == true)
		{
			this.setNo(DBAccess.GenerGUID(0, null, null));
		}

		this.setOrgNo(bp.web.WebUser.getOrgNo());

		return super.beforeInsert();
	}

		///#endregion 方法.
}
