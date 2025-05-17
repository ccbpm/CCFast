package bp.wf;

import bp.da.*;

/** 
 审核工作节点
*/
public class WorkCheck
{
	/** 
	 工作ID
	*/
	public long WorkID = 0;
	public long FID = 0;
	/** 
	 节点ID
	*/
	public int NodeID = 0;
	/**
	 * 时序模式 0 倒序 1正序
	 */
	public int FWCTimeModel = 0;
	/** 
	 流程编号
	*/
	public String FlowNo = null;
	public WorkCheck(String flowNo, int nodeID, long workid, long fid,int fwcTimeModel)
	{
		this.FlowNo= flowNo;
		this.NodeID = nodeID;
		this.WorkID = workid;
		this.FID = fid;
		this.FWCTimeModel = fwcTimeModel;
	}
	/** 
	 获取主键32位
	 
	 @return 
	*/
	public final int GetMyPK32()
	{
		try
		{
			int newPK = Integer.parseInt(String.valueOf(this.WorkID)) + this.NodeID + Integer.parseInt(this.FlowNo);
			String myPk = "";
			String sql = "SELECT TOP 1 RDT FROM WF_GenerWorkerlist WHERE WorkID=%d AND FK_Node=%d AND FK_Flow='%s' ORDER BY RDT DESC";
			DataTable dt = DBAccess.RunSQLReturnTable(String.format(sql, this.WorkID, this.NodeID, this.FlowNo));
			if (dt != null && dt.Rows.size() > 0)
			{
				myPk = dt.Rows.get(0).getValue("RDT").toString();
				myPk = myPk.replace("-", "").replace(":", "").replace(" ", "");
				myPk = myPk.substring(4);
				newPK = Integer.parseInt(String.valueOf(this.WorkID)) + this.NodeID + Integer.parseInt(this.FlowNo) + Integer.parseInt(myPk);
			}
			return newPK;
		}
		catch (RuntimeException ex)
		{
			return 0;
		}
	}
	/** 
	 获取主键
	 
	 @return 
	*/
	public final long GetMyPK()
	{
		try
		{
			long newPK = Long.parseLong(String.valueOf(this.WorkID)) + this.NodeID + Long.parseLong(this.FlowNo);
			String myPk = "";
			String sql = "SELECT TOP 1 RDT FROM WF_GenerWorkerlist WHERE WorkID=%d AND FK_Node=%d AND FK_Flow='%s' ORDER BY RDT DESC";


			DataTable dt = DBAccess.RunSQLReturnTable(String.format(sql, this.WorkID, this.NodeID, this.FlowNo));
			if (dt != null && dt.Rows.size() > 0)
			{
				myPk = dt.Rows.get(0).getValue("RDT").toString();
				myPk = myPk.replace("-", "").replace(":", "").replace(" ", "");
				myPk = myPk.substring(2);
				newPK = Long.parseLong(String.valueOf(this.WorkID)) + this.NodeID + Long.parseLong(this.FlowNo) + Long.parseLong(myPk);
			}
			return newPK;
		}
		catch (RuntimeException ex)
		{
			return 0;
		}
	}
	public final Tracks getHisWorkChecks() throws Exception {
		if (_HisWorkChecks == null)
		{
			_HisWorkChecks = new Tracks();
			bp.en.QueryObject qo = new bp.en.QueryObject(_HisWorkChecks);

			if (this.FID != 0)
			{
				qo.AddWhere(TrackAttr.WorkID, this.FID);
				qo.addOr();
				//qo.AddWhere(TrackAttr.WorkID, this.WorkID);
				qo.addSQL(TrackAttr.WorkID + "=" + this.WorkID);
			}
			else
			{
				qo.AddWhere(TrackAttr.WorkID, this.WorkID);

				if (this.WorkID != 0)
				{
					qo.addOr();
					qo.AddWhere(TrackAttr.FID, this.WorkID);
				}
			}
			// 如果存在旧数据，还是按照时间排序，否则按照主键排序
			String tableName = "ND" + Integer.parseInt(this.FlowNo) + "Track";
			String strPK = DBAccess.RunSQLReturnString("SELECT MIN(MyPK) FROM " + tableName + " WHERE WorkID = " + this.WorkID);
			if(this.FWCTimeModel == 0)
				if(strPK.length() < 18) qo.addOrderByDesc(TrackAttr.RDT);
				else qo.addOrderByDesc(TrackAttr.MyPK);
			if(this.FWCTimeModel == 1)
				if(strPK.length() < 18) qo.addOrderBy(TrackAttr.RDT);
				else qo.addOrderBy(TrackAttr.MyPK);
			String sql = qo.getSQL();
			sql = sql.replace("WF_Track", tableName);
			DataTable dt = null;

			//修复track 表.
			try
			{
				dt = DBAccess.RunSQLReturnTable(sql, qo.getMyParas());
			}
			catch (RuntimeException ex)
			{
				Track.CreateOrRepairTrackTable(this.FlowNo);
				dt = DBAccess.RunSQLReturnTable(sql, qo.getMyParas());
			}

			//dt.DefaultView.setSort("RDT desc");

			//放入到track里面.
			bp.en.QueryObject.InitEntitiesByDataTable(_HisWorkChecks, dt, null);
		}
		return _HisWorkChecks;
	}
	private Tracks _HisWorkChecks = null;
}
