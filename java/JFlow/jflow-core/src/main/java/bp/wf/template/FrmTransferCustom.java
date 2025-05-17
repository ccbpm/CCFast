package bp.wf.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;


/** 
 流转自定义组件
*/
public class FrmTransferCustom extends Entity
{

		///#region 属性
	public final String getNo() {
		return "ND" + this.getNodeID();
	}
	public final void setNo(String value) throws Exception {
		String nodeID = value.replace("ND", "");
		this.setNodeID(Integer.parseInt(nodeID));
	}
	/** 
	 节点ID
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(NodeAttr.NodeID);
	}
	public final void setNodeID(int value){
		this.SetValByKey(NodeAttr.NodeID, value);
	}
	/** 
	 控件状态
	*/
	public final FTCSta getFTCSta() {
		return FTCSta.forValue(this.GetValIntByKey(FTCAttr.FTCSta));
	}
	public final void setFTCSta(FTCSta value){
		this.SetValByKey(FTCAttr.FTCSta, value.getValue());
	}
	/** 
	 工作模式
	*/
	public final int getFTCWorkModel()  {
		return this.GetValIntByKey(FTCAttr.FTCWorkModel);
	}
	public final void setFTCWorkModel(int value){
		this.SetValByKey(FTCAttr.FTCWorkModel, value);
	}

	/** 
	 H
	*/
	public final float getFtcH()  {
		return this.GetValFloatByKey(FTCAttr.FTC_H);
	}
	public final void setFtcH(float value){
		this.SetValByKey(FTCAttr.FTC_H, value);
	}
	public final String getFTCHstr() throws Exception {
		if (this.getFtcH() == 0)
		{
			return "100%";
		}
		return this.getFtcH() + "px";
	}
	/** 
	 节点名称.
	*/
	public final String getName()  {
		return this.GetValStringByKey("Name");
	}
	/** 
	 显示标签
	*/
	public final String getFTCLab()  {
		return this.GetValStrByKey(FTCAttr.FTCLab);
	}

		///#endregion


		///#region 构造方法
	/** 
	 控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		uac.IsDelete = false;
		uac.IsInsert = false;
		return uac;
	}
	/** 
	 重写主键
	*/
	@Override
	public String getPK()
	{
		return "NodeID";
	}
	/** 
	 流转自定义组件
	*/
	public FrmTransferCustom()
	{
	}
	/** 
	 流转自定义组件
	 
	 @param mapData
	*/
	public FrmTransferCustom(String mapData) throws Exception {
		if (mapData.contains("ND") == false)
		{
			this.setFTCSta(FTCSta.Disable);
			return;
		}

		String mapdata = mapData.replace("ND", "");
		if (DataType.IsNumStr(mapdata) == false)
		{
			this.setFTCSta(FTCSta.Disable);
			return;
		}

		try
		{
			this.setNodeID(Integer.parseInt(mapdata));
		}
		catch (java.lang.Exception e)
		{
			return;
		}
		this.Retrieve();
	}
	/** 
	 流转自定义组件
	 
	 @param nodeID
	*/
	public FrmTransferCustom(int nodeID) throws Exception {
		this.setNodeID(nodeID);
		this.Retrieve();
	}
	/** 
	 EnMap
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_Node", "流转自定义组件");


		map.AddGroupAttr("流转自定义", "");

		map.AddTBIntPK(NodeAttr.NodeID, 0, "节点ID", true, true);
		map.AddTBString(NodeAttr.Name, null, "节点名称", true, true, 0, 100, 10);
		map.AddTBString(FTCAttr.FTCLab, "流转自定义", "显示标签", true, false, 0, 50, 10, true);



			///#region 此处变更了 NodeSheet类中的，map 描述该部分也要变更.

		map.AddDDLSysEnum(FTCAttr.FTCSta, FTCSta.Disable.getValue(), "组件状态", true, true, FTCAttr.FTCSta, "@0=禁用@1=只读@2=可设置人员");

		map.AddDDLSysEnum(FTCAttr.FTCWorkModel,0, "工作模式", true, true, FTCAttr.FTCWorkModel, "@0=简洁模式@1=高级模式");


		map.AddTBFloat(FTCAttr.FTC_H, 300, "高度", true, false);


			///#endregion 此处变更了 NodeSheet类中的，map 描述该部分也要变更.

		this.set_enMap(map);
		return this.get_enMap();
	}



		///#endregion
}
