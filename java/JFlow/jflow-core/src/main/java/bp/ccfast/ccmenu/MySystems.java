package bp.ccfast.ccmenu;

import bp.ccbill.template.Collection;
import bp.ccbill.template.Method;
import bp.sys.*;
import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.ccbill.template.*;
import bp.wf.*;
import bp.wf.template.*;
import bp.*;
import bp.ccfast.*;
import java.util.*;
import java.io.*;

/** 
 系统s
*/
public class MySystems extends EntitiesNoName
{

		///#region 构造
	/** 
	 系统s
	*/
	public MySystems()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MySystem();
	}
	@Override
	public int RetrieveAll() throws Exception {
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{

			int i = this.Retrieve(MySystemAttr.IsEnable, 1, "Idx");
			if (i != 0)
			{
				return i;
			}

			i = this.RetrieveAll("Idx");
			if (i != 0)
			{
				return i;
			}


				///#region 初始化菜单.

			String file = bp.difference.SystemConfig.getPathOfData() + "XML/AppFlowMenu.xml";
			DataSet ds = new DataSet();
			ds.readXml(file);

			//增加系统.
			for (DataRow dr : ds.GetTableByName("MySystem").Rows)
			{
				MySystem en = new MySystem();
				en.setNo(dr.getValue("No").toString());
				en.setName(dr.getValue("Name").toString());
				en.setIcon(dr.getValue("Icon").toString());
				en.setItIsEnable(true);
				en.Insert();
			}

			//增加模块.
			for (DataRow dr : ds.GetTableByName("Module").Rows)
			{
				Module en = new Module();
				en.setNo(dr.getValue("No").toString());
				en.setName(dr.getValue("Name").toString());
				en.setSystemNo(dr.getValue("SystemNo").toString());
				en.setIcon(dr.getValue("Icon").toString());

				// en.MenuCtrlWay = 1; 
				//en.IsEnable = true;
				en.Insert();
			}

			//增加连接.
			for (DataRow dr : ds.GetTableByName("Item").Rows)
			{
				Menu en = new Menu();
				en.setNo(dr.getValue("No").toString());
				en.setName(dr.getValue("Name").toString());
				//   en.SystemNo = dr["SystemNo").toString();
				en.setModuleNo(dr.getValue("ModuleNo").toString());
				en.setUrlExt(dr.getValue("Url").toString());
				en.setIcon(dr.getValue("Icon").toString());
				en.Insert();
			}


				///#endregion 初始化菜单.

			return RetrieveAll();
		}

		////集团模式下的角色体系: @0=每套组织都有自己的角色体系@1=所有的组织共享一套岗则体系.
		//if (bp.difference.SystemConfig.getGroupStationModel() == 1)
		//    return base.RetrieveAll("Idx");

		//按照orgNo查询.
		return this.Retrieve("OrgNo", bp.web.WebUser.getOrgNo(), "Idx");
	}

		///#endregion

	/** 
	 获得系统列表
	 
	 @return 
	*/
	public final String ImpSystem_Init() throws Exception {
		String path = bp.difference.SystemConfig.getPathOfWebApp() + "CCFast/SystemTemplete/";
		String[] strs = bp.tools.BaseFileUtils.getFiles(path);

		DataTable dt = new DataTable();
		dt.Columns.Add("No");
		dt.Columns.Add("Name");

		for (String str : strs)
		{
			File en = new File(str);
			DataRow dc = dt.NewRow();
			dc.setValue(String.valueOf(0), en.getName());
			dc.setValue(String.valueOf(1), en.getName());
			dt.Rows.add(dc);
		}
		return bp.tools.Json.ToJson(dt);
	}
	public final String DealGUIDNo(String no) throws Exception
	{
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			return no;
		}

		if (no.contains("_") == true)
		{
			no = no.substring(no.indexOf('_'));
		}

		return bp.web.WebUser.getOrgNo() + "_" + no;
	}
	/** 
	 导入系统
	 
	 @param name
	 @return 
	*/
	public final String ImpSystem_Imp(String name) throws Exception {
		String path = bp.difference.SystemConfig.getPathOfWebApp() + "CCFast/SystemTemplete/" + name;
		String pathOfMenus = path + "/Menus.xml";
		if ((new File(pathOfMenus)).isFile() == false)
		{
			return "err@系统错误，目录里缺少文件:" + pathOfMenus;
		}

		DataSet ds = new DataSet();
		ds.readXml(pathOfMenus);

		//创建系统.
		DataTable dt = ds.GetTableByName("MySystem");
		MySystem system = new MySystem();
		Row row = system.getRow();
		row.LoadDataTable(dt, dt.Rows.get(0));

		//旧的orgNo.
		String oldOrgNo = system.getOrgNo();

		system.setNo(this.DealGUIDNo(system.getNo()));
		if (system.getIsExits() == true)
		{
			return "err@系统:" + name + ",已经存在.您不能在导入.";
		}

		system.setOrgNo(bp.web.WebUser.getOrgNo());
		system.DirectInsert();

		//创建流程目录..
		FlowSort fs = new FlowSort();
		fs.setNo(system.getNo());
		fs.setName(system.getName());
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			fs.setParentNo("100");
		}
		else
		{
			fs.setOrgNo(bp.web.WebUser.getOrgNo());
			fs.setParentNo(system.getNo());
		}
		if (fs.getIsExits() == true)
		{
			fs.DirectUpdate();
		}
		else
		{
			fs.DirectInsert();
		}

		//创建model.
		dt = ds.GetTableByName("Modules");
		Modules modules = new Modules();

		for (DataRow dr : dt.Rows)
		{
			Module en = new Module();
			en.getRow().LoadDataTable(dt, dr);
			en.setOrgNo(bp.web.WebUser.getOrgNo());
			en.setSystemNo(system.getNo()); //重新赋值，有可能这个编号有变化。
			en.setNo(this.DealGUIDNo(en.getNo())); //修改编号格式，防止重复导入，在saas模式下，可以重复导入。
			en.DirectInsert();
			modules.AddEntity(en);
		}

		//创建menus.
		dt = ds.GetTableByName("Menus");

		for (DataRow dr : dt.Rows)
		{
			bp.ccfast.ccmenu.Menu en = new bp.ccfast.ccmenu.Menu();
			en.getRow().LoadDataTable(dt, dr);
			en.setOrgNo(bp.web.WebUser.getOrgNo());

			en.setSystemNo(system.getNo()); //重新赋值，有可能这个编号有变化。
									 //en.ModuleNo = "";

			int idx = en.getModuleNo().indexOf('_');
			if (idx > 0)
			{
				en.setModuleNo(en.getModuleNo().substring(idx));
			}

			Module myModule = null;

			//解决对应的模块编号变化的问题.
			for (Module item : modules.ToJavaList())
			{
				if (en.getModuleNo().contains(item.getNo()) == true)
				{
					en.setModuleNo(item.getNo());
					myModule = item;
					continue;
				}
			}

			//设置模块编号.
			en.setModuleNo(myModule.getNo());

			en.setNo(this.DealGUIDNo(en.getNo())); //修改编号格式，防止重复导入，在saas模式下，可以重复导入。

			switch (en.getMenuModel())
			{
				case "Dict":
					ImpSystem_Imp_Dict(en, path, system, myModule, oldOrgNo);
					break;
				case "DictTable":
					ImpSystem_Imp_DictTable(en, path);
					break;
				default:
					break;
			}
			en.DirectInsert();
		}

		return "执行成功.";
	}
	private void ImpSystem_Imp_DictTable(Menu en, String path) throws Exception {
		String frmID = en.getUrlExt();

		//导入表单.
		String file = path + "/" + frmID + ".xml";
		DataSet ds = new DataSet();
		ds.readXml(file);

		//创建用户自定义表
		DataTable dt = ds.GetTableByName("SFTable");
		SFTable sFTable = new SFTable();
		Row row = sFTable.getRow();
		row.LoadDataTable(dt, dt.Rows.get(0));
		sFTable.DirectInsert();

	}
	/** 
	 导入实体
	 
	 @param en
	 @param path
	*/
	private void ImpSystem_Imp_Dict(Menu en, String path, MySystem system, Module module_Keyword, String oldOrgNo) throws Exception {
		String frmID = en.getUrlExt();

		//导入表单.
		String file = path + "/" + frmID + ".xml";
		DataSet ds = new DataSet();
		ds.readXml(file);

		//旧的OrgNo 替换为新的orgNo.
		String realFrmID = en.getUrlExt();
		if (DataType.IsNullOrEmpty(oldOrgNo) == false)
		{
			realFrmID = frmID.replace(oldOrgNo, bp.web.WebUser.getOrgNo());
		}

		MapData.ImpMapData(realFrmID, ds);
		MapData md = new MapData(realFrmID);

		if (DataType.IsNullOrEmpty(oldOrgNo) == false)
		{
			md.setPTable(md.getPTable().replace(oldOrgNo, bp.web.WebUser.getOrgNo()));
		}

		md.Update();

		file = path + "/" + frmID + "_GroupMethods.xml";

		//导入单个实体的方法分组.
		ds = new DataSet();
		ds.readXml(file);
		DataTable dt = ds.GetTableByName("GroupMethods");
		for (DataRow dr : dt.Rows)
		{
			GroupMethod gm = new GroupMethod();
			gm.getRow().LoadDataTable(dt, dr);
			gm.setOrgNo(bp.web.WebUser.getOrgNo());
			gm.setFrmID(realFrmID);
			gm.setNo(DBAccess.GenerGUID(0, null, null));
			gm.DirectInsert();
		}

		dt = ds.GetTableByName("Methods");
		if (dt != null)
		{
			for (DataRow dr : dt.Rows)
			{
				Method myen = new Method();
				myen.getRow().LoadDataTable(dt, dr);

				myen.setFrmID(realFrmID);

				switch (myen.getMethodModel())
				{
					case "FlowEtc": //其他业务流程.
						myen.setFlowNo(ImpSystem_Imp_Dict_FlowEtc(myen.getFlowNo(), myen.getName(), path, system));
						break;
					case "FlowBaseData": //修改基础资料流程
						myen.setFlowNo(ImpSystem_Imp_Dict_FlowEtc(myen.getFlowNo(), myen.getName(), path, system));
						break;
					case "Func": //功能.
						break;
					default:
						break;
				}
				//    en.OrgNo = Web.WebUser.getOrgNo();
				myen.setNo(DBAccess.GenerGUID(0, null, null));
				myen.DirectInsert();
			}
		}

		//导入实体集合.
		file = path + "/" + frmID + "_Collections.xml";
		ds.readXml(file);
		dt = ds.GetTableByName("GroupMethods");
		for (DataRow dr : dt.Rows)
		{
			Collection myen = new Collection();
			myen.getRow().LoadDataTable(dt, dr);
			myen.setFrmID(realFrmID);

			switch (myen.getMethodModel())
			{
				case "FlowEntityBatchStart": //批量发起流程.
					ImpSystem_Imp_Dict_FlowEtc(myen.getFlowNo(), myen.getName(), path, system);
					break;
				case "FlowNewEntity": //新建流程
					ImpSystem_Imp_Dict_FlowEtc(myen.getFlowNo(), myen.getName(), path, system);
					break;
				case "Func": //功能.
					break;
				default:
					break;
			}
			myen.DirectInsert();
		}
	}
	/** 
	 导入流程.
	 @param tempFlowNo
	 @param path
	 @return 
	*/
	private String ImpSystem_Imp_Dict_FlowEtc(String tempFlowNo, String tempFlowName, String path, MySystem mysystem) throws Exception {
		//导入模式
		ImpFlowTempleteModel model = ImpFlowTempleteModel.AsNewFlow;

		path = path + "/" + tempFlowNo + "_Flow/" + tempFlowName + ".xml";
		//   if (model == ImpFlowTempleteModel.AsSpecFlowNo)
		//     flowNo = this.GetRequestVal("SpecFlowNo");

		//执行导入
		Flow flow = TemplateGlo.LoadFlowTemplate(mysystem.getNo(), path, model, null);
		flow.setFlowSortNo(mysystem.getNo());
		flow.DoCheck(); //要执行一次检查.

		return flow.getNo();


		//Hashtable ht = new Hashtable();
		//ht.Add("FK_Flow", flow.getNo()); //流程编号.
		//ht.Add("FlowName", flow.Name); //名字.
		//ht.Add("FK_FlowSort", flow.FlowSortNo); //类别.
		//ht.Add("Msg", "导入成功,流程编号为:" + flow.getNo() + "名称为:" + flow.Name);
		//return BP.Tools.Json.ToJson(ht);
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MySystem> ToJavaList()
	{
		return (java.util.List<MySystem>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MySystem> Tolist()
	{
		ArrayList<MySystem> list = new ArrayList<MySystem>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MySystem)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
