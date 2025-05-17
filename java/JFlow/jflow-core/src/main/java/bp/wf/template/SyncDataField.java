package bp.wf.template;

import bp.en.*; import bp.en.Map;
/** 
 流程数据同步
*/
public class SyncDataField extends EntityMyPK
{

		///#region 属性
	/** 
	 流程编号
	*/
	public final String getFlowNo()  {
		return this.GetValStringByKey(SyncDataFieldAttr.FlowNo);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(SyncDataFieldAttr.FlowNo, value);
	}

	/** 
	 字段
	*/
	public final String getToFieldKey()  {
		return this.GetValStringByKey(SyncDataFieldAttr.ToFieldKey);
	}
	public final void setToFieldKey(String value){
		this.SetValByKey(SyncDataFieldAttr.ToFieldKey, value);
	}
	/** 
	 表集合
	*/
	public final String getToFieldName()  {
		return this.GetValStringByKey(SyncDataFieldAttr.ToFieldName);
	}
	public final void setToFieldName(String value){
		this.SetValByKey(SyncDataFieldAttr.ToFieldName, value);
	}
	/** 
	 表主键
	*/
	public final String getRefPKVal()  {
		return this.GetValStringByKey(SyncDataFieldAttr.RefPKVal);
	}
	public final void setRefPKVal(String value){
		this.SetValByKey(SyncDataFieldAttr.RefPKVal, value);
	}
	/** 
	 表
	*/
	public final String getAttrKey()  {
		return this.GetValStringByKey(SyncDataFieldAttr.AttrKey);
	}
	public final void setAttrKey(String value){
		this.SetValByKey(SyncDataFieldAttr.AttrKey, value);
	}
	/** 
	 备注
	*/
	public final String getAttrName()  {
		return this.GetValStringByKey(SyncDataFieldAttr.AttrName);
	}
	public final void setAttrName(String value){
		this.SetValByKey(SyncDataFieldAttr.AttrName, value);
	}
	/** 
	 URL 
	*/
	public final String getAttrType()  {
		return this.GetValStringByKey(SyncDataFieldAttr.AttrType);
	}
	public final void setAttrType(String value){
		this.SetValByKey(SyncDataFieldAttr.AttrType, value);
	}
	/** 
	 数据源
	*/
	public final String getTurnFunc()  {
		return this.GetValStringByKey(SyncDataFieldAttr.TurnFunc);
	}
	public final void setTurnFunc(String value){
		this.SetValByKey(SyncDataFieldAttr.TurnFunc, value);
	}
	/** 
	 类型
	*/
	public final boolean getItIsSync()  {
		return this.GetValBooleanByKey(SyncDataFieldAttr.IsSync);
	}
	public final void setItIsSync(boolean value){
		this.SetValByKey(SyncDataFieldAttr.IsSync, value);
	}


		///#endregion


		///#region 构造函数
	/** 
	 SyncDataField
	*/
	public SyncDataField()
	{
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
		Map map = new Map("WF_SyncDataField", "流程数据同步");

		map.AddMyPK(true);
		map.AddTBString(SyncDataFieldAttr.FlowNo, null, "流程编号", false, false, 0, 10, 50, true);
		map.AddTBString(SyncDataFieldAttr.RefPKVal, null, "关键内容", false, false, 0, 50, 50, true);

		map.AddTBString(SyncDataFieldAttr.AttrKey, null, "业务字段", true, true, 0, 100, 50, false);
		map.AddTBString(SyncDataFieldAttr.AttrName, null, "字段名", true, true, 0, 100, 50, false);
		map.AddTBString(SyncDataFieldAttr.AttrType, null, "类型", true, true, 0, 100, 50, false);

		map.AddTBString(SyncDataFieldAttr.ToFieldKey, null, "同步到字段", true, true, 0, 100, 50, false);
		map.AddTBString(SyncDataFieldAttr.ToFieldName, null, "字段名", true, true, 0, 100, 50, false);
		map.AddTBString(SyncDataFieldAttr.ToFieldType, null, "类型", true, true, 0, 100, 50, false);

		map.AddBoolean(SyncDataFieldAttr.IsSync, false, "同步?", true, true);
		map.AddTBString(SyncDataFieldAttr.TurnFunc, null, "转换函数", true, false, 0, 50, 50);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

}
