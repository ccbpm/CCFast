package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import bp.web.*;
/** 
 常用语
*/
public class FastInput extends EntityMyPK
{

		///#region 基本属性
	/** 
	 表单ID
	*/
	public final String getEnsName()  {
		return this.GetValStringByKey(FastInputAttr.EnsName);
	}
	public final void setEnsName(String value){
		this.SetValByKey(FastInputAttr.EnsName, value);
	}
	/** 
	 属性
	*/
	public final String getAttrKey()  {
		return this.GetValStringByKey(FastInputAttr.AttrKey);
	}
	public final void setAttrKey(String value){
		this.SetValByKey(FastInputAttr.AttrKey, value);
	}
	/** 
	 配置的变量
	*/
	public final String getCfgKey()
	{
		return "CYY";
	}
	public final void setCfgKey(String value){
		this.SetValByKey(FastInputAttr.CfgKey, value);
	}
	/** 
	 人员
	*/
	public final String getEmpNo()  {
		return this.GetValStringByKey(FastInputAttr.FK_Emp);
	}
	public final void setEmpNo(String value){
		this.SetValByKey(FastInputAttr.FK_Emp, value);
	}
	public final String getVals()  {
		return this.GetValStringByKey(FastInputAttr.Vals);
	}
	public final void setVals(String value){
		this.SetValByKey(FastInputAttr.Vals, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 常用语
	*/
	public FastInput()
	{
	}
	/** 
	 常用语
	 
	 @param mypk
	*/
	public FastInput(String mypk) throws Exception {
		super(mypk);
	}
	/** 
	 更新前做的事情
	 
	 @return 
	*/

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			this.setMyPK(DBAccess.GenerGUID());
		}

		return super.beforeUpdateInsertAction();
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_UserRegedit", "常用语");

		/*
		 * 常用语分为两个模式: 流程的常用语，与表单字段的常用语. 
		 * 这两个模式都存储在同一个表里.
		 * 
		 * 流程的常用语存储格式为: 
		 *  CfgKey=Flow,  EnsName=Flow,  AttrKey=WorkCheck,FlowBBS,WorkReturn 三种类型.
		 *  
		 * 表单的常用语为存储格式为:
		 *  CfgKey=Frm,  EnsName=myformID, AttrKey=myFieldName, 
		 * 
		 */

		//该表单对应的表单ID ， 
		//CfgKey=Flow, EnsName=Flow 是流程的常用语.   Filed

		map.AddMyPK();
		map.AddTBString(FastInputAttr.CfgKey, null, "类型Flow,Frm", true, false, 0, 20, 20);

		map.AddTBString(FastInputAttr.EnsName, null, "表单ID", true, false, 0, 100, 4);
		map.AddTBString(FastInputAttr.AttrKey, null, "字段", true, false, 0, 100, 4);
		map.AddTBString(FastInputAttr.FK_Emp, null, "人员", true, false, 0, 100, 4);

		map.AddTBString(FastInputAttr.Vals, null, "值", true, false, 0, 500, 500);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/** 
	 上移
	 
	 @return 
	*/
	public final String DoUp() throws Exception
	{
		this.DoOrderUp(FastInputAttr.CfgKey, "CYY", FastInputAttr.EnsName, this.getEnsName(), FastInputAttr.AttrKey, this.getAttrKey(), FastInputAttr.FK_Emp, WebUser.getNo(), "Idx");

		return "移动成功.";
	}
	/** 
	 下移
	 
	 @return 
	*/
	public final String DoDown() throws Exception
	{
		this.DoOrderDown(FastInputAttr.CfgKey, "CYY", FastInputAttr.EnsName, this.getEnsName(), FastInputAttr.AttrKey, this.getAttrKey(), FastInputAttr.FK_Emp, WebUser.getNo(), "Idx");
		return "移动成功.";
	}
}
