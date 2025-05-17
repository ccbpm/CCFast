package bp.ccbill;

import bp.da.*;
import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.tools.DateUtils;
import bp.wf.*;
import bp.wf.httphandler.*;
import bp.*;
import java.util.*;
import java.time.*;

/** 
 页面功能实体
*/
public class WF_CCBill_Opt extends bp.difference.handler.DirectoryPageBase
{

		///#region 构造方法.
	/** 
	 构造函数
	*/
	public WF_CCBill_Opt()
	{
	}

		///#endregion 构造方法.


		///#region 关联单据.
	/** 
	 设置父子关系.
	 
	 @return 
	*/
	public final String RefBill_Done() throws Exception {
		try
		{
			String frmID = this.GetRequestVal("FrmID");
			long workID = this.GetRequestValInt64("WorkID");
			GERpt rpt = new GERpt(frmID, workID);

			String pFrmID = this.GetRequestVal("PFrmID");
			long pWorkID = this.GetRequestValInt64("PWorkID");

			//把数据copy到当前的子表单里.
			GERpt rptP = new GERpt(pFrmID, pWorkID);
			rpt.Copy(rptP);
			rpt.setPWorkID(pWorkID);
			rpt.SetValByKey("PFrmID", pFrmID);
			rpt.Update();

			//更新控制表,设置父子关系.
			GenerBill gbill = new GenerBill(workID);
			gbill.setPFrmID(pFrmID);
			gbill.setPWorkID(pWorkID);
			gbill.Update();
			return "执行成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/** 
	 单据初始化
	 
	 @return 
	*/
	public final String RefBill_Init() throws Exception {
		DataSet ds = new DataSet();


			///#region 查询显示的列
		MapAttrs mattrs = new MapAttrs();
		mattrs.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.Idx);

		DataRow row = null;
		DataTable dt = new DataTable("Attrs");
		dt.Columns.Add("KeyOfEn", String.class);
		dt.Columns.Add("Name", String.class);
		dt.Columns.Add("Width", Integer.class);
		dt.Columns.Add("UIContralType", Integer.class);
		dt.Columns.Add("LGType", Integer.class);

		//设置标题、单据号位于开始位置


		for (MapAttr attr : mattrs.ToJavaList())
		{
			String searchVisable = attr.getatPara().GetValStrByKey("SearchVisable");
			if (Objects.equals(searchVisable, "0"))
			{
				continue;
			}
			if (attr.getUIVisible() == false)
			{
				continue;
			}
			row = dt.NewRow();
			row.setValue("KeyOfEn", attr.getKeyOfEn());
			row.setValue("Name", attr.getName());
			row.setValue("Width", attr.getUIWidthInt());
			row.setValue("UIContralType", attr.getUIContralType().getValue());
			row.setValue("LGType", attr.getLGType().getValue());
			dt.Rows.add(row);
		}
		ds.Tables.add(dt);

			///#endregion 查询显示的列


			///#region 查询语句

		MapData md = new MapData(this.getFrmID());

		GEEntitys rpts = new GEEntitys(this.getFrmID());

		Attrs attrs = rpts.getNewEntity().getEnMap().getAttrs();

		QueryObject qo = new QueryObject(rpts);


			///#region 关键字字段.
		String keyWord = this.GetRequestVal("SearchKey");

		if (DataType.IsNullOrEmpty(keyWord) == false && keyWord.length() >= 1)
		{
			qo.addLeftBracket();
			if (Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "?"))
			{
				qo.AddWhere("Title", " LIKE ", bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : (" '%'+" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
			}
			else
			{
				qo.AddWhere("Title", " LIKE ", " '%'||" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
			}
			qo.addOr();
			if (Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "?"))
			{
				qo.AddWhere("BillNo", " LIKE ", bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL ? ("CONCAT('%'," + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : ("'%'+" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
			}
			else
			{
				qo.AddWhere("BillNo", " LIKE ", "'%'||" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
			}

			qo.getMyParas().Add("SKey", keyWord, false);
			qo.addRightBracket();

		}
		else
		{
			qo.AddHD();
		}

			///#endregion 关键字段查询


			///#region 时间段的查询
		String dtFrom = this.GetRequestVal("DTFrom");
		String dtTo = this.GetRequestVal("DTTo");
		if (DataType.IsNullOrEmpty(dtFrom) == false)
		{

			//取前一天的24：00
			if (dtFrom.trim().length() == 10) //2017-09-30
			{
				dtFrom += " 00:00:00";
			}
			if (dtFrom.trim().length() == 16) //2017-09-30 00:00
			{
				dtFrom += ":00";
			}

			dtFrom = DateUtils.addDay(DateUtils.parse(dtFrom, "yyyy-MM-dd"), -1) + " 24:00";

			if (dtTo.trim().length() < 11 || dtTo.trim().indexOf(' ') == -1)
			{
				dtTo += " 24:00";
			}

			qo.addAnd();
			qo.addLeftBracket();
			qo.setSQL( " RDT>= '" + dtFrom + "'");
			qo.addAnd();
			qo.setSQL("RDT <= '" + dtTo + "'");
			qo.addRightBracket();
		}

			///#endregion 时间段的查询

		qo.DoQuery("OID", this.getPageSize(), this.getPageIdx());


			///#endregion

		DataTable mydt = rpts.ToDataTableField("dt");
		mydt.setTableName("DT");

		ds.Tables.add(mydt); //把数据加入里面.

		return bp.tools.Json.ToJson(ds);
	}

		///#endregion 关联单据.
}
