package bp.wf.httphandler;

import bp.sys.*;
import bp.da.*;
import bp.wf.xml.*;
import bp.*;
import bp.wf.*;
import java.util.*;

public class WF_Admin_CCFormDesigner_FrmEvent extends bp.difference.handler.DirectoryPageBase
{

	/** 
	 构造函数
	*/
	public WF_Admin_CCFormDesigner_FrmEvent()
	{
	}


		///#region 事件基类.
	/** 
	 事件类型
	*/
	public final String getShowType()
	{
		if (this.getNodeID() != 0)
		{
			return "Node";
		}

		if (this.getNodeID() == 0 && DataType.IsNullOrEmpty(this.getFlowNo()) == false && this.getFlowNo().length() >= 3)
		{
			return "Flow";
		}

		if (this.getNodeID() == 0 && DataType.IsNullOrEmpty(this.getFrmID()) == false)
		{
			return "Frm";
		}

		return "Node";
	}

	/** 
	 获得该节点下已经绑定该类型的实体.
	 
	 @return 
	*/
	public final String ActionDtl_Init() throws Exception {
		DataSet ds = new DataSet();

		//事件实体.
		FrmEvents ndevs = new FrmEvents();
		ndevs.Retrieve(FrmEventAttr.FrmID, this.getFrmID(), null);

		DataTable dt = ndevs.ToDataTableField("FrmEvents");
		ds.Tables.add(dt);

		//业务单元集合.
		DataTable dtBuess = new DataTable();
		dtBuess.Columns.Add("No", String.class);
		dtBuess.Columns.Add("Name", String.class);
		dtBuess.TableName = "BuessUnits";
		ArrayList<BuessUnitBase> al = bp.en.ClassFactory.GetObjects("BP.Sys.BuessUnitBase");
		for (BuessUnitBase en : al)
		{
			DataRow dr = dtBuess.NewRow();
			dr.setValue("No", en.toString());
			dr.setValue("Name", en.getTitle());
			dtBuess.Rows.add(dr);
		}

		ds.Tables.add(dtBuess);

		return bp.tools.Json.ToJson(ds);
	}
	/** 
	 执行删除
	 
	 @return 
	*/
	public final String ActionDtl_Delete() throws Exception {
		//事件实体.
		FrmEvent en = new FrmEvent();
		en.setMyPK(this.getMyPK());
		en.Delete();
		return "删除成功.";
	}
	public final String ActionDtl_Save() throws Exception {
		//事件实体.
		FrmEvent en = new FrmEvent();

		en.setNodeID(this.getNodeID());
		en.setEventNo(this.GetRequestVal("FK_Event")); //事件类型.
		en.setHisDoTypeInt(this.GetValIntFromFrmByKey("EventDoType")); //执行类型.
		en.setMyPK(this.getNodeID() + "_" + en.getEventNo() + "_" + en.getHisDoTypeInt()); //组合主键.
		en.RetrieveFromDBSources();

		en.setMsgOKString(this.GetValFromFrmByKey("MsgOK")); //成功的消息.
		en.setMsgErrorString(this.GetValFromFrmByKey("MsgError")); //失败的消息.

		//执行内容.
		if (en.getHisDoType() == EventDoType.BuessUnit)
		{
			en.setDoDoc(this.GetValFromFrmByKey("DDL_Doc"));
		}
		else
		{
			en.setDoDoc(this.GetValFromFrmByKey("TB_Doc"));
		}

		en.Save();

		return "保存成功.";
	}

		///#endregion 事件基类.

}
