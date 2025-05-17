package bp.ccbill.template;

import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 台账子流程
*/
public class DictFlow extends EntityMyPK
{

		///#region 基本属性
	/** 
	 表单ID
	*/
	public final String getFrmID() {
		return this.GetValStringByKey(DictFlowAttr.FrmID);
	}
	public final void setFrmID(String value)  {
		this.SetValByKey(DictFlowAttr.FrmID, value);
	}

	/** 
	 方法名
	*/
	public final String getFlowNo() {
		return this.GetValStringByKey(DictFlowAttr.FlowNo);
	}
	public final void setFlowNo(String value)  {
		this.SetValByKey(DictFlowAttr.FlowNo, value);
	}
	/** 
	 显示标签
	*/
	public final String getLabel() {
		return this.GetValStringByKey(DictFlowAttr.Label);
	}
	public final void setLabel(String value)  {
		this.SetValByKey(DictFlowAttr.Label, value);
	}
	/** 
	 是否显示在表格右边
	*/
	public final int getItIsShowListRight() {
		return this.GetValIntByKey(DictFlowAttr.IsShowListRight);
	}
	public final void setItIsShowListRight(int value)  {
		this.SetValByKey(DictFlowAttr.IsShowListRight, value);
	}
	/** 
	 顺序号
	*/
	public final int getIdx() {
		return this.GetValIntByKey(DictFlowAttr.Idx);
	}
	public final void setIdx(int value)  {
		this.SetValByKey(DictFlowAttr.Idx, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 台账子流程
	*/
	public DictFlow()
	{
	}
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Frm_DictFlow", "台账子流程");

		map.AddMyPK(true);

		map.AddTBString(DictFlowAttr.FrmID, null, "表单ID", true, false, 0, 300, 10);
		map.AddTBString(DictFlowAttr.FlowNo, null, "流程编号", true, false, 0, 20, 10);
		map.AddTBString(DictFlowAttr.Label, null, "功能标签", true, false, 0, 20, 10);
		map.AddTBInt(DictFlowAttr.IsShowListRight, 0, "是否显示在列表右边", true, false);

		map.AddTBInt(DictFlowAttr.Idx, 0, "Idx", true, false);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 移动.
	public final void DoUp() throws Exception {
		this.DoOrderUp(DictFlowAttr.FrmID, this.getFrmID(), DictFlowAttr.Idx);
	}
	public final void DoDown() throws Exception
	{
		this.DoOrderDown(DictFlowAttr.FrmID, this.getFrmID(), DictFlowAttr.Idx);
	}

		///#endregion 移动.

}
