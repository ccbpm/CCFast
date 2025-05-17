package bp.sys;

import bp.da.*;
import bp.en.*; import bp.en.Map;


/** 
 默认值
*/
public class DefVal extends EntityMyPK
{

		///#region 基本属性
	/** 
	 父节点编号
	*/
	public final String getParentNo()  {
		return this.GetValStringByKey(DefValAttr.ParentNo);
	}
	public final void setParentNo(String value){
		this.SetValByKey(DefValAttr.ParentNo, value);
	}
	/** 
	 是否父节点
	*/
	public final String isParent()  {
		return this.GetValStringByKey(DefValAttr.IsParent);
	}
	public final void setParent(String value){
		this.SetValByKey(DefValAttr.IsParent, value);
	}
	/** 
	 词汇类别
	*/
	public final String getWordsSort()  {
		return this.GetValStringByKey(DefValAttr.WordsSort);
	}
	public final void setWordsSort(String value){
		this.SetValByKey(DefValAttr.WordsSort, value);
	}
	/** 
	 节点编号
	*/
	public final String getFrmID()  {
		return this.GetValStringByKey(DefValAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(DefValAttr.FrmID, value);
	}
	/** 
	 节点对应字段
	*/
	public final String getAttrKey()  {
		return this.GetValStringByKey(DefValAttr.AttrKey);
	}
	public final void setAttrKey(String value){
		this.SetValByKey(DefValAttr.AttrKey, value);
	}
	/** 
	 是否历史词汇
	*/
	public final String getLB()  {
		return this.GetValStringByKey(DefValAttr.LB);
	}
	public final void setLB(String value){
		this.SetValByKey(DefValAttr.LB, value);
	}
	/** 
	 人员编号
	*/
	public final String getEmpNo()  {
		return this.GetValStringByKey(DefValAttr.EmpNo);
	}
	public final void setEmpNo(String value){
		this.SetValByKey(DefValAttr.EmpNo, value);
	}
	/** 
	 节点文本
	*/
	public final String getCurValue()  {
		return this.GetValStringByKey(DefValAttr.CurValue);
	}
	public final void setCurValue(String value){
		this.SetValByKey(DefValAttr.CurValue, value);
	}

		///#endregion


		///#region 构造方法

	/** 
	 默认值
	*/
	public DefVal()
	{
	}
	/** 
	 map
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_UserRegedit", "选择词汇");

		map.setCodeStruct("2");

		//设置PK.
		map.AddMyPK();

		//秦2015-1-10   根据公司需求改动   以下是源码
		//map.AddTBStringPK(DefValAttr.No, null, "编号", true, true, 1, 50, 20);
		//map.AddTBString(DefValAttr.EnsName, null, "类名称", false, true, 0, 100, 10);
		//map.AddTBString(DefValAttr.EnsDesc, null, "类描述", false, true, 0, 100, 10);
		//map.AddTBString(DefValAttr.AttrKey, null, "属性", false, true, 0, 100, 10);
		//map.AddTBString(DefValAttr.AttrDesc, null, "属性描述", false, false, 0, 100, 10);
		//map.AddTBString(DefValAttr.FK_Emp, null, "人员", false, true, 0, 100, 10);
		//map.AddTBString(DefValAttr.Val, null, "值", true, false, 0, 1000, 10);
		//map.AddTBString(DefValAttr.ParentNo, null, "父节点编号", false, false, 0, 50, 20);
		//map.AddTBInt(DefValAttr.IsParent, 0, "是否父节点", false, false);
		//map.AddTBString(DefValAttr.HistoryWords, null, "历史词汇", false, false, 0, 2000, 20);

		map.AddTBString(DefValAttr.FrmID, null, "实体", false, false, 0, 100, 20);
		map.AddTBString(DefValAttr.EmpNo, null, "人员", false, true, 0, 100, 10);
		map.AddTBString(DefValAttr.AttrKey, null, "节点对应字段", false, false, 0, 50, 20);

		//map.AddTBInt(DefValAttr.WordsSort, 0, "词汇类型", false, false);//1,2,3... 退回-移交-表单...(暂时)
		map.AddTBInt(DefValAttr.LB, 0, "类别", false, false); //我的，历史,系统，
		map.AddTBString(DefValAttr.CurValue, null, "文本", false, true, 0, 4000, 10);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		//设置主键.
		this.setMyPK(DBAccess.GenerGUID());
		return super.beforeInsert();
	}
}
