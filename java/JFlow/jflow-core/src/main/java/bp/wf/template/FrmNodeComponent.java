package bp.wf.template;

import bp.da.*;
import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.wf.*;
import bp.wf.template.sflow.*;

/** 
 节点表单组件
*/
public class FrmNodeComponent extends Entity
{

		///#region 公共属性
	/** 
	 审核组件状态
	*/
	public final FrmWorkCheckSta getFWCSta() {
		return FrmWorkCheckSta.forValue(this.GetValIntByKey(NodeWorkCheckAttr.FWCSta, 0));
	}
	public final void setFWCSta(FrmWorkCheckSta value){
		this.SetValByKey(NodeWorkCheckAttr.FWCSta, value.getValue());
	}
	/** 
	 节点属性.
	*/
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

		///#endregion


		///#region 构造方法
	/** 
	 控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenAll();
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
	 节点表单组件
	*/
	public FrmNodeComponent()
	{
	}
	/** 
	 节点表单组件
	 @param mapData
	*/
	public FrmNodeComponent(String mapData) throws Exception {
		String mapdata = mapData.replace("ND", "");
		if (DataType.IsNumStr(mapdata) == false)
		{
			//  this.HisFrmNodeComponentSta = FrmNodeComponentSta.Disable;
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
	 节点表单组件
	 @param nodeID
	*/
	public FrmNodeComponent(int nodeID) throws Exception {
		this.setNodeID(nodeID);
		this.Retrieve();
	}
	/** 
	 EnMap
	*/
	@Override
	public Map getEnMap()  {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_Node", "节点表单组件");

		map.setDepositaryOfEntity(Depositary.None);
		map.setDepositaryOfMap(Depositary.Application);

		map.AddGroupAttr("基本属性", "");
		map.AddTBIntPK(NodeAttr.NodeID, 0, "节点ID", true, true);
		map.AddTBString(NodeAttr.Name, null, "节点名称", true, true, 0, 100, 10);

		map.AddGroupAttr("审核组件", "");
		//审核组件.
		NodeWorkCheck fwc = new NodeWorkCheck();
		map.AddAttrs(fwc.getEnMap().getAttrs(), false);

		//父子流程.
		map.AddGroupAttr("父子流程", "");
		FrmSubFlow subflow = new FrmSubFlow();
		map.AddAttrs(subflow.getEnMap().getAttrs(), false);

		//轨迹组件.
		map.AddGroupAttr("轨迹组件", "");
		FrmTrack track = new FrmTrack();
		map.AddAttrs(track.getEnMap().getAttrs(), false);

		//流转自定义组件.
		map.AddGroupAttr("流转自定义组件", "");
		FrmTransferCustom ftt = new FrmTransferCustom();
		map.AddAttrs(ftt.getEnMap().getAttrs(), false);

		this.set_enMap(map);
		return this.get_enMap();
	}

	/** 
	 初始化GroupField
	*/
	public final void InitGroupField() throws Exception {
		GroupField gf = new GroupField();


			///#region 审核组件.
		NodeWorkCheck fwc = new NodeWorkCheck(this.getNodeID());
		fwc.Copy(this);

		if (fwc.getHisFrmWorkCheckSta() == FrmWorkCheckSta.Disable)
		{
			gf.Delete(GroupFieldAttr.FrmID, this.getNo(), GroupFieldAttr.CtrlType, GroupCtrlType.FWC);
		}
		else
		{
			if (gf.IsExit(GroupFieldAttr.FrmID, this.getNo(), GroupFieldAttr.CtrlType, GroupCtrlType.FWC) == false)
			{
				gf = new GroupField();
				gf.setFrmID("ND" + this.getNodeID());
				gf.setCtrlType(GroupCtrlType.FWC);
				gf.setLab("审核组件");
				gf.setIdx(0);
				gf.Insert(); //插入.
			}
		}

			///#endregion 审核组件.


			///#region 父子流程组件.
		FrmSubFlow subflow = new FrmSubFlow(this.getNodeID());
		subflow.Copy(this);

		if (subflow.getHisFrmSubFlowSta() == FrmSubFlowSta.Disable)
		{
			gf.Delete(GroupFieldAttr.CtrlID, "SubFlow" + this.getNo());
		}
		else
		{
			if (gf.IsExit(GroupFieldAttr.CtrlID, "SubFlow" + this.getNo()) == false)
			{
				gf = new GroupField();
				gf.setFrmID("ND" + this.getNodeID());
				gf.setCtrlID("SubFlow" + this.getNo());
				gf.setCtrlType(GroupCtrlType.SubFlow);
				gf.setLab("父子流程组件");
				gf.setIdx(0);
				gf.Insert(); //插入.
			}
		}

			///#endregion 父子流程组件.


			///#region 处理轨迹组件.
		FrmTrack track = new FrmTrack(this.getNodeID());
		track.Copy(this);
		if (track.getFrmTrackSta() == FrmTrackSta.Disable)
		{
			gf.Delete(GroupFieldAttr.CtrlID, "FrmTrack" + this.getNo());
		}
		else
		{
			if (gf.IsExit(GroupFieldAttr.CtrlID, "FrmTrack" + this.getNo()) == false)
			{
				gf = new GroupField();
				gf.setFrmID("ND" + this.getNodeID());
				gf.setCtrlID("FrmTrack" + this.getNo());
				gf.setCtrlType(GroupCtrlType.Track);
				gf.setLab("轨迹");
				gf.setIdx(0);
				gf.Insert(); //插入.
			}
		}

			///#endregion 处理轨迹组件.



			///#region 流转自定义组件.
		FrmTransferCustom ftc = new FrmTransferCustom(this.getNodeID());
		ftc.Copy(this);

		if (ftc.getFTCSta() == FTCSta.Disable)
		{
			gf.Delete(GroupFieldAttr.CtrlID, "FrmFTC" + this.getNo());
		}
		else
		{
			if (gf.IsExit(GroupFieldAttr.CtrlID, "FrmFTC" + this.getNo()) == false)
			{
				gf = new GroupField();
				gf.setFrmID("ND" + this.getNodeID());
				gf.setCtrlID("FrmFTC" + this.getNo());
				gf.setCtrlType(GroupCtrlType.FTC);
				gf.setLab("流转自定义");
				gf.setIdx(0);
				gf.Insert(); //插入.
			}
		}

			///#endregion 流转自定义组件.

	}

	@Override
	protected boolean beforeUpdate() throws Exception
	{

		this.InitGroupField();

		return super.beforeUpdate();
	}

		///#endregion

	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		Node fl = new Node();
		fl.setNodeID(this.getNodeID());
		fl.RetrieveFromDBSources();
		fl.Update();

		super.afterInsertUpdateAction();
	}
}
