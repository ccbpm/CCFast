package bp.ccbill;

import bp.da.*;
import bp.sys.*;
import bp.ccbill.template.*;
import bp.difference.*;

/** 
 页面功能实体
*/
public class WF_CCBill_Admin extends bp.difference.handler.DirectoryPageBase
{

	/** 
	 工具栏按钮
	 
	 @return 
	*/
	public final String ToolbarSetting_Init() throws Exception {
		ToolbarBtns btns = new ToolbarBtns();
		int i = btns.Retrieve(GroupMethodAttr.FrmID, this.getFrmID(), "Idx");
		if (i == 0)
		{
			FrmBill bill = new FrmBill(this.getFrmID());
			ToolbarBtn btn = new ToolbarBtn();
			if (bill.getEntityType() != EntityType.DBList)
			{
				btn.setFrmID(this.getFrmID());
				btn.setBtnID("New");
				btn.setBtnLab("新建");
				btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
				btn.SetValByKey("Idx", 0);
				btn.Insert();


				btn = new ToolbarBtn();
				btn.setFrmID(this.getFrmID());
				btn.setBtnID("Save");
				btn.setBtnLab("保存");
				btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
				btn.SetValByKey("Idx", 1);
				btn.Insert();


				if (bill.getEntityType() == EntityType.FrmBill)
				{
					//单据增加提交的功能
					btn = new ToolbarBtn();
					btn.setFrmID(this.getFrmID());
					btn.setBtnID("Submit");
					btn.setBtnLab("提交");
					btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
					btn.SetValByKey("Idx", 1);
					btn.Insert();
				}

				btn = new ToolbarBtn();
				btn.setFrmID(this.getFrmID());
				btn.setBtnID("Delete");
				btn.setBtnLab("删除");
				btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
				btn.SetValByKey("Idx", 2);
				btn.Insert();
			}


			btn = new ToolbarBtn();
			btn.setFrmID(this.getFrmID());
			btn.setBtnID("PrintHtml");
			btn.setBtnLab("打印Html");
			btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
			btn.setItIsEnable(false);
			btn.SetValByKey("Idx", 3);
			btn.Insert();

			btn = new ToolbarBtn();
			btn.setFrmID(this.getFrmID());
			btn.setBtnID("PrintPDF");
			btn.setBtnLab("打印PDF");
			btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
			btn.setItIsEnable(false);
			btn.SetValByKey("Idx", 4);
			btn.Insert();

			btn = new ToolbarBtn();
			btn.setFrmID(this.getFrmID());
			btn.setBtnID("PrintRTF");
			btn.setBtnLab("打印RTF");
			btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
			btn.setItIsEnable(false);
			btn.SetValByKey("Idx", 5);
			btn.Insert();

			btn = new ToolbarBtn();
			btn.setFrmID(this.getFrmID());
			btn.setBtnID("PrintCCWord");
			btn.setBtnLab("打印CCWord");
			btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
			btn.setItIsEnable(false);
			btn.SetValByKey("Idx", 6);
			btn.Insert();

			btn = new ToolbarBtn();
			btn.setFrmID(this.getFrmID());
			btn.setBtnID("ExpZip");
			btn.setBtnLab("导出Zip包");
			btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
			btn.setItIsEnable(false);
			btn.SetValByKey("Idx", 7);
			btn.Insert();

			btns.Retrieve(GroupMethodAttr.FrmID, this.getFrmID(), "Idx");
		}
		return btns.ToJson("dt");
	}
	/** 
	 实体、单据工具栏操作按钮的顺序移动
	 
	 @return 
	*/
	public final String ToolbarSetting_Mover()
	{
		String[] ens = this.GetRequestVal("MyPKs").split("[,]", -1);
		for (int i = 0; i < ens.length; i++)
		{
			String enMyPK = ens[i];
			if (DataType.IsNullOrEmpty(enMyPK) == true)
			{
				continue;
			}
			String sql = "UPDATE Frm_ToolbarBtn SET Idx=" + i + " WHERE MyPK='" + enMyPK + "'";
			DBAccess.RunSQL(sql);
		}
		return "顺序移动成功..";
	}



		///#region 方法的操作.
	/** 
	 方法的初始化
	 
	 @return 
	*/
	public final String Method_Init() throws Exception {
		GroupMethod gnn = new GroupMethod();

		GroupMethods gms = new GroupMethods();
		int i = gms.Retrieve(GroupMethodAttr.FrmID, this.getFrmID(), "Idx");
		if (i == 0)
		{
			GroupMethod gm = new GroupMethod();
			gm.setFrmID(this.getFrmID());
			gm.setName("相关操作");
			gm.setMethodType("Home");
			gm.setIcon("icon-home");
			gm.Insert();
			gms.Retrieve(GroupMethodAttr.FrmID, this.getFrmID(), "Idx");
		}

		DataTable dtGroups = gms.ToDataTableField("Groups");
		dtGroups.setTableName("Groups");

		Methods methods = new Methods();
	 //   methods.Retrieve(MethodAttr.FrmID, this.FrmID, MethodAttr.IsEnable, 1, "Idx");
		methods.Retrieve(MethodAttr.FrmID, this.getFrmID(), "Idx");

		DataTable dtMethods = methods.ToDataTableField("Methods");
		dtMethods.setTableName("Methods");

		DataSet ds = new DataSet();
		ds.Tables.add(dtGroups);
		ds.Tables.add(dtMethods);

		return bp.tools.Json.ToJson(ds);

	}
	/** 
	 移动分组
	 
	 @return 
	*/
	public final String Method_MoverGroup()
	{
		String[] ens = this.GetRequestVal("GroupIDs").split("[,]", -1);
		String frmID = this.getFrmID();
		for (int i = 0; i < ens.length; i++)
		{
			String en = ens[i];

			String sql = "UPDATE Frm_GroupMethod SET Idx=" + i + " WHERE No='" + en + "' AND FrmID='" + frmID + "'";
			DBAccess.RunSQL(sql);
		}
		return "目录移动成功..";
	}
	/** 
	 移动方法.
	 
	 @return 
	*/
	public final String Method_MoverMethod()
	{
		String sortNo = this.GetRequestVal("GroupID");

		String[] ens = this.GetRequestVal("MethodIDs").split("[,]", -1);
		for (int i = 0; i < ens.length; i++)
		{
			String enNo = ens[i];
			String sql = "UPDATE Frm_Method SET GroupID ='" + sortNo + "',Idx=" + i + " WHERE No='" + enNo + "'";
			DBAccess.RunSQL(sql);
		}
		return "方法顺序移动成功..";
	}

		///#endregion 方法的操作.

	/** 
	 构造函数
	*/
	public WF_CCBill_Admin()
	{

	}
	/** 
	 列表集合初始化
	 
	 @return 
	*/
	public final String Collection_Init() throws Exception {
		Collections collections = new Collections();
		int i = collections.Retrieve(GroupMethodAttr.FrmID, this.getFrmID(), "Idx");
		if (i == 0)
		{
			FrmBill bill = new FrmBill(this.getFrmID());
			//查询
			Collection collection = new Collection();
			collection.setFrmID(this.getFrmID());
			collection.setMethodID("Search");
			collection.setName("查询");
			collection.setMethodModel("Search");
			collection.setMark("Search");
			collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
			collection.SetValByKey("Idx", 0);
			collection.Insert();

			if (bill.getEntityType() != EntityType.DBList)
			{
				//新建
				collection = new Collection();
				collection.setFrmID(this.getFrmID());
				collection.setMethodID("New");
				collection.setName("新建");
				collection.setMethodModel("New");
				collection.setMark("New");
				collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
				collection.SetValByKey("Idx", 1);
				collection.Insert();

				//删除
				collection = new Collection();
				collection.setFrmID(this.getFrmID());
				collection.setMethodID("Delete");
				collection.setName("删除");
				collection.setMethodModel("Delete");
				collection.setMark("Delete");
				collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
				collection.SetValByKey("Idx", 2);
				collection.Insert();

				//导入
				collection = new Collection();
				collection.setFrmID(this.getFrmID());
				collection.setMethodID("ImpExcel");
				collection.setName("导入Excel");
				collection.setMethodModel("ImpExcel");
				collection.setMark("ImpExcel");
				collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
				collection.SetValByKey("Idx", 5);
				collection.Insert();
			}


			collection = new Collection();
			collection.setFrmID(this.getFrmID());
			collection.setMethodID("Group");
			collection.setName("分析");
			collection.setMethodModel("Group");
			collection.setMark("Group");
			collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
			collection.SetValByKey("Idx", 3);
			collection.SetValByKey("IsEnable", false);
			collection.Insert();

			//导出
			collection = new Collection();
			collection.setFrmID(this.getFrmID());
			collection.setMethodID("ExpExcel");
			collection.setName("导出Excel");
			collection.setMethodModel("ExpExcel");
			collection.setMark("ExpExcel");
			collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
			collection.SetValByKey("Idx", 4);
			collection.Insert();


			collections.Retrieve(GroupMethodAttr.FrmID, this.getFrmID(), "Idx");
		}
		return collections.ToJson("dt");
	}

	/** 
	 集合方法的移动.
	 
	 @return 
	*/
	public final String Collection_Mover()
	{
		String[] ens = this.GetRequestVal("MyPKs").split("[,]", -1);
		for (int i = 0; i < ens.length; i++)
		{
			String enNo = ens[i];
			if (DataType.IsNullOrEmpty(enNo) == true)
			{
				continue;
			}
			String sql = "UPDATE Frm_Collection SET Idx=" + i + " WHERE No='" + enNo + "'";
			DBAccess.RunSQL(sql);
		}
		return "顺序移动成功..";
	}


		///#region 执行父类的重写方法.
	/** 
	 默认执行的方法
	 
	 @return 
	*/
	@Override
	protected String DoDefaultMethod()
	{
		switch (this.getDoType())
		{
			case "DtlFieldUp": //字段上移
				return "执行成功.";
			default:
				break;
		}

		//找不不到标记就抛出异常.
		throw new RuntimeException("@标记[" + this.getDoType() + "]DoMethod=[" + this.GetRequestVal("DoMethod") + "]，没有找到. @RowURL:" + ContextHolderUtils.getRequest().getRequestURI());
	}

		///#endregion 执行父类的重写方法.
}
