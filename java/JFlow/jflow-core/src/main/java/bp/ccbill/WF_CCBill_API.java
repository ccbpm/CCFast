package bp.ccbill;

import bp.da.*;
import bp.sys.*;
import bp.web.*;
import bp.ccbill.template.*;
import bp.ccfast.ccmenu.*;

/** 
 页面功能实体
*/
public class WF_CCBill_API extends bp.difference.handler.DirectoryPageBase
{

		///#region 构造方法.
	/** 
	 构造函数
	*/
	public WF_CCBill_API()
	{
	}

		///#endregion 构造方法.


		///#region 常用参数.
	/** 
	 目录树编号
	*/
	public final String getTreeNo()
	{
		return this.GetRequestVal("TreeNo");
	}

		///#endregion 常用参数.



		///#region 前台的操作 api
	/** 
	 获得可以操作的单据列表
	 
	 @return 
	*/
	public final String CCFrom_GenerFrmListOfCanOption()
	{
		String sql = "";
		String userNo = GetRequestVal("UserNo");
		if (DataType.IsNullOrEmpty(userNo) == true)
		{
			userNo = WebUser.getUserID();
		}

		String powerSQL = "";

		if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL)
		{
			powerSQL = "SELECT FrmID," + "(CASE WHEN IsEnableAll=1 THEN true " + "ELSE(CASE WHEN IsEnableUser=1 AND INSTR(IDOfUsers,'," + userNo + ",')>0 THEN true " + "ELSE(CASE WHEN IsEnableStation=1 AND (SELECT COUNT(*) From Port_DeptEmpStation D,Port_Emp E WHERE D.FK_Emp = E.No AND E.No='" + userNo + "' AND INSTR(IDOfStations,D.FK_Station))>0 THEN true " + "ELSE(CASE WHEN IsEnableDept=1 AND (SELECT COUNT(*) From Port_DeptEmp D,Port_Emp E WHERE D.FK_Emp = E.No AND E.No='" + userNo + "' AND INSTR(IDOfDepts,D.FK_Dept))>0 THEN true " + "ELSE false END)" + "END)" + "END)" + "END) AS IsView   FROM Frm_CtrlModel WHERE CtrlObj='BtnSearch'";

			sql = "SELECT No,Name,EntityType,FrmType,PTable FROM Sys_MapData M, (" + powerSQL + ") AS B WHERE M.No=B.FrmID AND (M.EntityType=1 OR M.EntityType=2) AND B.IsView=1 ORDER BY M.IDX ";

		}
		else
		{
			powerSQL = "SELECT FrmID," + "(CASE WHEN IsEnableAll=1 THEN true " + "ELSE(CASE WHEN IsEnableUser=1 AND 1=1 THEN true " + "ELSE(CASE WHEN IsEnableStation=1 AND (SELECT COUNT(*) From Port_DeptEmpStation D,Port_Emp E WHERE D.FK_Emp = E.No AND E.No='" + userNo + "' AND  1=1 THEN true " + "ELSE(CASE WHEN IsEnableDept=1 AND (SELECT COUNT(*) From Port_DeptEmp D,Port_Emp E WHERE D.FK_Emp = E.No AND E.No='" + userNo + "' AND  1=1 THEN true " + "ELSE false END)" + "END)" + "END)" + "END) AS IsView   FROM Frm_CtrlModel WHERE CtrlObj='BtnSearch'";

			sql = "SELECT No,Name,EntityType,FrmType,PTable FROM Sys_MapData M  WHERE  (M.EntityType=1 OR M.EntityType=2)   ORDER BY M.IDX ";

		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.Columns.get(0).ColumnName = "No";
		dt.Columns.get(1).ColumnName = "Name";
		dt.Columns.get(2).ColumnName = "EntityType";
		dt.Columns.get(3).ColumnName = "FrmType";
		dt.Columns.get(4).ColumnName = "PTable";


		return bp.tools.Json.ToJson(dt);
	}
	/** 
	 根据单据编号创建或者更新实体信息.
	 
	 @return 返回url：打开该实体的url.
	*/
	public final String CCFrom_NewFrmBillAsSpecBillNo() throws Exception {

		String billNo = this.GetRequestVal("BillNo");
		String title = this.GetRequestVal("Title");
		String paras = this.GetRequestVal("Paras");

		if (DataType.IsNullOrEmpty(paras) == true)
		{
			paras = "";
		}
		AtPara ap = new AtPara(paras);

		GEEntityOID en = new GEEntityOID(this.getFrmID());
		int i = en.Retrieve("BillNo", billNo);
		if (i == 0)
		{
			long workid = bp.ccbill.Dev2Interface.CreateBlankBillID(this.getFrmID(), WebUser.getNo(), ap.getHisHT(), billNo);
			en = new GEEntityOID(this.getFrmID(), workid);
			if (DataType.IsNullOrEmpty(paras) == false)
			{
				en.Copy(ap.getHisHT());
				en.Update();
			}
			if (DataType.IsNullOrEmpty(title) == false)
			{
				en.SetValByKey("Title", title);
				en.Update();
			}
			return "url@../../WF/CCBill/MyBill.htm?FrmID=" + this.getFrmID() + "&OID=" + workid;
		}
		else
		{
			if (DataType.IsNullOrEmpty(paras) == false)
			{
				en.Copy(ap.getHisHT());
				en.Update();
			}

			if (DataType.IsNullOrEmpty(title) == false && en.GetValStrByKey("Title").equals(title) == false)
			{
				en.SetValByKey("Title", title);
				en.Update();
			}
		}
		return "url@../../WF/CCBill/MyBill.htm?FrmID=" + this.getFrmID() + "&OID=" + en.getOID();
	}
	/** 
	 获得指定的目录下可以操作的单据列表
	 
	 @return 
	*/
	public final String CCFrom_GenerFrmListOfCanOptionBySpecTreeNo()
	{

		String treeNo = this.GetRequestVal("TreeNo");
		return null;
	}
	/** 
	 获得一个表单的操作权限
	 
	 @return 
	*/
	public final ToolbarBtns CCFrom_FrmPower() throws Exception {
		//获取该表单所有操作按钮的权限
		ToolbarBtns btns = new ToolbarBtns();
		btns.Retrieve(ToolbarBtnAttr.FrmID, this.getFrmID(), "Idx");
		boolean isReadonly = this.GetRequestValBoolen("IsReadonly");
		if (btns.size() == 0)
		{
			MapData md = new MapData(this.getFrmID());
			//表单的工具栏权限
			ToolbarBtn btn = new ToolbarBtn();
			if (md.getEntityType() != EntityType.DBList && isReadonly == false)
			{
				btn.setFrmID(md.getNo());
				btn.setBtnID("New");
				btn.setBtnLab("新建");
				btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
				btn.SetValByKey("Idx", 0);
				btn.Insert();


				btn = new ToolbarBtn();
				btn.setFrmID(md.getNo());
				btn.setBtnID("Save");
				btn.setBtnLab("保存");
				btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
				btn.SetValByKey("Idx", 1);
				btn.Insert();

				if (md.getEntityType() == EntityType.FrmBill)
				{
					//单据增加提交的功能
					btn = new ToolbarBtn();
					btn.setFrmID(md.getNo());
					btn.setBtnID("Submit");
					btn.setBtnLab("提交");
					btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
					btn.SetValByKey("Idx", 1);
					btn.Insert();
				}

				btn = new ToolbarBtn();
				btn.setFrmID(md.getNo());
				btn.setBtnID("Delete");
				btn.setBtnLab("删除");
				btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
				btn.SetValByKey("Idx", 2);
				btn.Insert();
			}


			btn = new ToolbarBtn();
			btn.setFrmID(md.getNo());
			btn.setBtnID("PrintHtml");
			btn.setBtnLab("打印Html");
			btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
			btn.setItIsEnable(false);
			btn.SetValByKey("Idx", 3);
			btn.Insert();

			btn = new ToolbarBtn();
			btn.setFrmID(md.getNo());
			btn.setBtnID("PrintPDF");
			btn.setBtnLab("打印PDF");
			btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
			btn.setItIsEnable(false);
			btn.SetValByKey("Idx", 4);
			btn.Insert();

			btn = new ToolbarBtn();
			btn.setFrmID(md.getNo());
			btn.setBtnID("PrintRTF");
			btn.setBtnLab("打印RTF");
			btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
			btn.setItIsEnable(false);
			btn.SetValByKey("Idx", 5);
			btn.Insert();

			btn = new ToolbarBtn();
			btn.setFrmID(md.getNo());
			btn.setBtnID("PrintCCWord");
			btn.setBtnLab("打印CCWord");
			btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
			btn.setItIsEnable(false);
			btn.SetValByKey("Idx", 6);
			btn.Insert();

			btn = new ToolbarBtn();
			btn.setFrmID(md.getNo());
			btn.setBtnID("ExpZip");
			btn.setBtnLab("导出Zip包");
			btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
			btn.setItIsEnable(false);
			btn.SetValByKey("Idx", 7);
			btn.Insert();

			btns.Retrieve(ToolbarBtnAttr.FrmID, this.getFrmID(), ToolbarBtnAttr.IsEnable, 1, "Idx");
			return btns;
		}

		//获取针对按钮设置的操作权限
		PowerCenters pcs = new PowerCenters();
		pcs.Retrieve(PowerCenterAttr.CtrlObj, this.getFrmID(), PowerCenterAttr.CtrlGroup, "FrmBtn", null);

		String mydepts = "" + WebUser.getDeptNo() + ","; //我的部门.
		String mystas = ""; //我的角色.

		DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.getUserID() + "'");
		for (DataRow dr : mydeptsDT.Rows)
		{
			mydepts += dr.getValue(0).toString() + ",";
			mystas += dr.getValue(1).toString() + ",";
		}

		ToolbarBtns newBtns = new ToolbarBtns();
		String empIds = "";
		for (ToolbarBtn btn : btns.ToJavaList())
		{
			if (btn.getItIsEnable() == false)
			{
				continue;
			}
			if (isReadonly == true && (btn.getBtnID().equals("New") || btn.getBtnID().equals("Save") || btn.getBtnID().equals("Submit") || btn.getBtnID().equals("Delete")))
			{
				continue;
			}
			//找到关于系统的控制权限集合.
			bp.en.Entities tempVar = pcs.GetEntitiesByKey(PowerCenterAttr.CtrlPKVal, btn.getMyPK());
			PowerCenters mypcs = tempVar instanceof PowerCenters ? (PowerCenters)tempVar : null;
			//如果没有权限控制的描述，就默认有权限.
			if (mypcs == null)
			{
				newBtns.AddEntity(btn);
				continue;
			}

			//控制遍历权限.
			for (PowerCenter pc : mypcs.ToJavaList())
			{
				if (pc.getCtrlModel().equals("Anyone") == true)
				{
					newBtns.AddEntity(btn);
					break;
				}
				if (pc.getCtrlModel().equals("Adminer") == true && WebUser.getNo().equals("admin") == true)
				{
					newBtns.AddEntity(btn);
					break;
				}

				if (pc.getCtrlModel().equals("AdminerAndAdmin2") == true && WebUser.getIsAdmin() == true)
				{
					newBtns.AddEntity(btn);
					break;
				}
				empIds = "," + pc.getIDs() + ",";
				if (pc.getCtrlModel().equals("Emps") == true && empIds.contains("," + WebUser.getNo() + ",") == true)
				{
					newBtns.AddEntity(btn);
					break;
				}

				//是否包含部门？
				if (pc.getCtrlModel().equals("Depts") == true && DataType.IsHaveIt(pc.getIDs(), mydepts) == true)
				{
					newBtns.AddEntity(btn);
					break;
				}

				//是否包含角色？
				if (pc.getCtrlModel().equals("Stations") == true && DataType.IsHaveIt(pc.getIDs(), mystas) == true)
				{
					newBtns.AddEntity(btn);
					break;
				}

				//SQL？
				if (pc.getCtrlModel().equals("SQL") == true)
				{
					String sql = bp.wf.Glo.DealExp(pc.getIDs(), null, "");
					if (DBAccess.RunSQLReturnValFloat(sql) > 0)
					{
						newBtns.AddEntity(btn);
					}
					break;
				}
			}
		}
		return newBtns;
	}
		/** 
		 获取单据，实体按钮权限集合
		 
		 @return 
		*/
	public final String CCFrom_ToolBar_Init() throws Exception {
		//获取实体单据的权限
		ToolbarBtns btns = CCFrom_FrmPower();
		return bp.tools.Json.ToJson(btns.ToDataTableField("Frm_ToolbarBtn"));

	}

	/** 
	 删除实体根据BillNo
	 
	 @return 
	*/
	public final String CCFrom_DeleteFrmEntityByBillNo() throws Exception {

		GEEntity en = new GEEntity(this.getFrmID());
		int i = en.Retrieve("BillNo", this.GetRequestVal("BillNo"));
		if (i == 0)
		{
			return "err@单据编号为" + this.GetRequestVal("BillNo") + "的数据不存在.";
		}

		en.Delete();
		return "删除成功";
	}
	/** 
	 删除实体根据 OID
	 
	 @return 
	*/
	public final String CCFrom_DeleteFrmEntityByOID() throws Exception {
		GEEntity en = new GEEntity(this.getFrmID(), this.getOID());
		en.Delete();
		return "删除成功";
	}

		///#endregion 前台的操作 api.



		///#region 后台操作api.
	/** 
	 获得所有的单据、表单
	 
	 @return 
	*/
	public final String CCBillAdmin_Admin_GenerAllBills()
	{
		String sql = "";
		sql = "SELECT No,Name,EntityType,FrmType,PTable FROM Sys_MapData WHERE (EntityType=1 OR EntityType=2) ORDER BY IDX ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.Columns.get(0).ColumnName = "No";
		dt.Columns.get(1).ColumnName = "Name";
		dt.Columns.get(2).ColumnName = "EntityType";
		dt.Columns.get(3).ColumnName = "FrmType";
		dt.Columns.get(4).ColumnName = "PTable";


		return bp.tools.Json.ToJson(dt);
	}

		///#endregion

}
