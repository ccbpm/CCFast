package bp.wf;

import bp.sys.*;
import bp.en.*;
/** 
 单据业务流程
*/
public class CCBill_FlowEvent
{
	/** 
	 流程结束的时候要处理的事件
	 
	 @param wn
	 @param paras
	*/
	public static void DealFlowOver(WorkNode wn, String paras) throws Exception {

			///#region 如果是修改基础资料流程。
		/**如果是修改基础资料流程.
		*/
		if (wn.getHisGenerWorkFlow().GetParaBoolen("FlowBaseData", false) == true)
		{
			long dictWorkID = wn.getHisGenerWorkFlow().getPWorkID();
			String dictFrmID = wn.getHisGenerWorkFlow().getPFlowNo();

			String flowFrmID = "ND" + Integer.parseInt(wn.getHisFlow().getNo() + "01");

			// MapData md = new MapData(flowFrmID);

			//同步主表数据.
			Row row = wn.rptGe.getRow();

			//创建实体.
			GEEntityOID geEn = new GEEntityOID(dictFrmID, dictWorkID);
			for (String key : row.keySet())
			{
				if (key.indexOf("bak") == 0)
				{
					continue;
				}

				if (bp.wf.Glo.getFlowFields().contains("," + key + ",") == true)
				{
					continue;
				}

				//设置值.
				geEn.SetValByKey(key, row.GetValByKey(key));
			}

			geEn.setOID(dictWorkID);
			geEn.Update(); //更新.

			//修改实体信息.
			bp.ccbill.Dev2Interface.WriteTrack(dictFrmID, String.valueOf(wn.getWorkID()), bp.ccbill.FrmActionType.FlowBaseData, "流程修改实体数据", geEn.ToJson(true), wn.getHisFlow().getNo(), wn.getHisFlow().getName(), wn.getHisNode().getNodeID(), wn.getWorkID());

			//更新从表.
			//  MapDtls dtls = new MapDtls(flowFrmID);
		}

			///#endregion 如果是修改基础资料流程。


			///#region 如果是新建实体流程.
		if (wn.getHisGenerWorkFlow().GetParaBoolen("FlowNewEntity", false) == true)
		{

			String menuNo = wn.getHisGenerWorkFlow().GetParaString("MenuNo");

			bp.ccbill.template.MethodFlowNewEntity menu = new bp.ccbill.template.MethodFlowNewEntity(menuNo);

			//创建工作，并copy数据过去.
			Row row = wn.rptGe.getRow();
			bp.ccbill.Dev2Interface.SaveDictWork(menu.getFrmID(), wn.getWorkID(), row);

			//替换实体名字.
			if (row.containsKey("DictName") == true)
			{
				String dictName = row.get("DictName").toString();
				GEEntity ge = new GEEntity(menu.getFrmID(), wn.getWorkID());
				ge.SetValByKey("Title", dictName);
				ge.Update();
			}

			//写入日志.
			String myparas = "";
			myparas += "@PWorkID=" + wn.getWorkID();
			myparas += "@PFlowNo=" + wn.getHisFlow().getNo();
			myparas += "@PNodeID=" + wn.getHisNode().getNodeID();
			bp.ccbill.Dev2Interface.WriteTrack(menu.getFrmID(), String.valueOf(wn.getWorkID()), bp.ccbill.FrmActionType.StartRegFlow, "流程创建实体", myparas, wn.getHisFlow().getNo(), wn.getHisFlow().getName(), wn.getHisNode().getNodeID(), wn.getWorkID());
		}

			///#endregion 如果是新建实体流程.

	}

	public static void DoFlow(String flowMark, WorkNode wn, String paras) throws Exception {
		/**流程结束之前.
		*/
		if (flowMark.equals(EventListFlow.FlowOverBefore) == true)
		{
			DealFlowOver(wn, paras);
		}
	}

}
