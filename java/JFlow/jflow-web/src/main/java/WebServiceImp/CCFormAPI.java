package WebServiceImp;

import javax.jws.WebMethod;
import javax.jws.WebService;


import bp.da.DataRow;
import bp.da.DataSet;
import bp.da.DataTable;
import bp.da.DataType;
import bp.en.Attr;
import bp.en.Attrs;
import bp.sys.GEDtlAttr;
import bp.sys.GEDtls;
import bp.sys.MapData;
import bp.sys.MapDtl;
import bp.sys.MapDtls;
import bp.web.WebUser;
import bp.wf.Dev2Interface;
import bp.wf.GenerWorkFlow;
import bp.wf.data.GERpt;
import WebService.CCFormAPII;

@WebService
public class CCFormAPI implements CCFormAPII{
	
	/** 
	 获得单据模版信息
	 @param userNo 用户编号
	 @param sid SID
	 @param workID 工作ID
	 @param billTemplateNo 单据模版编号
	 @param ds 返回的数据源
	 @param bytes 返回的字节
	 * @throws Exception 
	*/

	@Override
	@WebMethod
	public void GenerBillTemplate(String userNo, String sid, long workID, String billTemplateNo, DataSet ds,
			byte[] bytes) throws Exception {
		Dev2Interface.Port_Login(userNo);
		if (DataType.IsNullOrEmpty(userNo) == true)
		{
			userNo = WebUser.getNo();
		}

		Dev2Interface.Port_Login(userNo);
		GenerWorkFlow gwf = new GenerWorkFlow(workID);

		//是否可以查看该工作.
		boolean b = Dev2Interface.Flow_IsCanViewTruck(gwf.getFlowNo(), gwf.getWorkID(), userNo);
		if (b == false)
		{
			throw new RuntimeException("err@无权查看该流程.");
		}

		String frmID = "ND" + Integer.parseInt(gwf.getFlowNo()) + "Rpt";
		GERpt rpt = new GERpt("ND" + Integer.parseInt(gwf.getFlowNo()) + "Rpt", workID);
		DataTable dt = rpt.ToDataTableField( "Main");
		ds.Tables.add(dt);
		Attrs attrs = rpt.getEnMap().getAttrs();
			///#region 处理bool类型.
		for (Attr item : attrs)
		{
			if (item.getMyDataType() == DataType.AppBoolean)
			{
				dt.Columns.Add(item.getKey() + "Text", String.class);

				for (DataRow dr : dt.Rows)
				{
					String val = dr.getValue(item.getKey()).toString();
					if (val.equals("1"))
					{
						dr.setValue(item.getKey() + "Text","[√]");
					}
					else
					{
						dr.setValue(item.getKey() + "Text", "[×]");
					}
				}
			}
		}
			///#endregion 处理bool类型.

		//把从表数据加入里面去.
		MapDtls dtls = new MapDtls("ND" + gwf.getNodeID());
		for (MapDtl item : dtls.ToJavaList())
		{
			GEDtls dtlEns = new GEDtls(item.getNo());
			dtlEns.Retrieve(GEDtlAttr.RefPK, workID);

			DataTable dtDtl = dtlEns.ToDataTableField(item.getNo());
			ds.Tables.add(dtDtl);
		}

		return;

	}

	/** 
	 获得Word文件 - 未开发完成.
	 
	 @param userNo 用户编号
	 @param sid SID
	 @return 
	 * @throws Exception 
	*/
	@Override
	public void WordFileGener(String userNo, String sid, long workID, byte[] bytes) throws Exception {
		Dev2Interface.Port_Login(userNo);
		if (DataType.IsNullOrEmpty(userNo) == true)
		{
			userNo = WebUser.getNo();
		}


		Dev2Interface.Port_Login(userNo);
		GenerWorkFlow gwf = new GenerWorkFlow(workID);

		boolean b = Dev2Interface.Flow_IsCanViewTruck(gwf.getFlowNo(), gwf.getWorkID(),userNo);
		if (b == false)
		{
			throw new RuntimeException("err@无权查看该流程.");
		}


		String frmID = "ND" + Integer.parseInt(gwf.getFlowNo()) + "Rpt";

		MapData md = new MapData(frmID);

		//创建excel表单描述，让其保存到excel表单指定的字段里, 扩展多个表单映射同一张表.
		//MapFrmWord mfe = new MapFrmExcels(frmID);

		//返回文件模版.
		//md.WordGenerFile((new Long(workID)).toString(), bytes, mfe.DBSave);

		
	} 

}
