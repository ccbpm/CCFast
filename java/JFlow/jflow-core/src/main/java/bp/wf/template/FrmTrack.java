package bp.wf.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
/** 
 轨迹图标组件
*/
public class FrmTrack extends Entity
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
	public final FrmTrackSta getFrmTrackSta() {
		return FrmTrackSta.forValue(this.GetValIntByKey(FrmTrackAttr.FrmTrackSta));
	}
	public final void setFrmTrackSta(FrmTrackSta value){
		this.SetValByKey(FrmTrackAttr.FrmTrackSta, value.getValue());
	}

	/** 
	 H
	*/
	public final float getFrmTrackH()  {
		return this.GetValFloatByKey(FrmTrackAttr.FrmTrack_H);
	}
	public final void setFrmTrackH(float value){
		this.SetValByKey(FrmTrackAttr.FrmTrack_H, value);
	}
	public final String getFrmTrackHstr() throws Exception {
		if (this.getFrmTrackH() == 0)
		{
			return "100%";
		}
		return this.getFrmTrackH() + "px";
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
	public final String getFrmTrackLab()  {
		return this.GetValStrByKey(FrmTrackAttr.FrmTrackLab);
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
	 轨迹图标组件
	*/
	public FrmTrack()
	{
	}
	/** 
	 轨迹图标组件
	 @param mapData
	*/
	public FrmTrack(String mapData) throws Exception {
		if (mapData.contains("ND") == false)
		{
			this.setFrmTrackSta(FrmTrackSta.Disable);
			return;
		}

		String mapdata = mapData.replace("ND", "");
		if (DataType.IsNumStr(mapdata) == false)
		{
			this.setFrmTrackSta(FrmTrackSta.Disable);
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
	 轨迹图标组件
	 @param nodeID
	*/
	public FrmTrack(int nodeID) throws Exception {
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

		Map map = new Map("WF_Node", "轨迹图标组件");

		map.AddGroupAttr("轨迹组件", "");
		map.AddTBIntPK(NodeAttr.NodeID, 0, "节点ID", true, true);
		map.AddTBString(NodeAttr.Name, null, "节点名称", true, true, 0, 100, 10);
		map.AddTBString(FrmTrackAttr.FrmTrackLab, "轨迹", "显示标签", true, false, 0, 200, 10, false);


			///#region 此处变更了 NodeSheet类中的，map 描述该部分也要变更.

		map.AddDDLSysEnum(FrmTrackAttr.FrmTrackSta, FrmTrackSta.Disable.getValue(), "组件状态", true, true, FrmTrackAttr.FrmTrackSta, "@0=禁用@1=显示轨迹图@2=显示轨迹表");

		map.AddTBFloat(FrmTrackAttr.FrmTrack_H, 300, "高度", true, false);


			///#endregion 此处变更了 NodeSheet类中的，map 描述该部分也要变更.

		this.set_enMap(map);
		return this.get_enMap();
	}

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		return super.beforeUpdateInsertAction();
	}

		///#endregion
}
