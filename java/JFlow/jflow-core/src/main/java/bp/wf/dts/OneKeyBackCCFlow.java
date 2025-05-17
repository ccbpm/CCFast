package bp.wf.dts;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.sys.CCFormAPI;
import bp.wf.template.*;
import bp.*;
import bp.wf.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import java.time.*;

/** 
 Method 的摘要说明
*/
public class
OneKeyBackCCFlow extends Method
{
	/** 
	 不带有参数的方法
	*/
	public OneKeyBackCCFlow()
	{
		this.Title = "一键备份流程与表单。";
		this.Help = "把流程、表单、组织结构数据都生成xml文档备份到C:/CCFlowTemplete下面。";
		this.GroupName = "数据备份/恢复";

	}
	/** 
	 设置执行变量
	 
	 @return 
	*/
	@Override
	public void Init()
	{
		//this.Warning = "您确定要执行吗？";
		//HisAttrs.AddTBString("P1", null, "原密码", true, false, 0, 10, 10);
		//HisAttrs.AddTBString("P2", null, "新密码", true, false, 0, 10, 10);
		//HisAttrs.AddTBString("P3", null, "确认", true, false, 0, 10, 10);
	}
	/** 
	 当前的操纵员是否可以执行这个方法
	*/
	@Override
	public boolean getIsCanDo()
	{
		return true;
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	*/
	@Override
	public Object Do() throws Exception {
		String path = "C:/CCFlowTemplete" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy年MM月dd日HH时mm分ss秒"));
		if ((new File(path)).isDirectory() == false)
		{
			(new File(path)).mkdirs();
		}


			///#region 1.备份流程类别信息
		DataSet dsFlows = new DataSet();
		//WF_FlowSort
		DataTable dt = DBAccess.RunSQLReturnTable("SELECT * FROM WF_FlowSort");
		dt.TableName = "WF_FlowSort";
		dsFlows.Tables.add(dt);
		dsFlows.WriteXml(path + "/FlowTables.xml");

			///#endregion 备份流程类别信息.


			///#region 2.备份组织结构.
		DataSet dsPort = new DataSet();
		//emps
		dt = DBAccess.RunSQLReturnTable("SELECT * FROM Port_Emp");
		dt.TableName = "Port_Emp";
		dsPort.Tables.add(dt);

		//Port_Dept
		dt = DBAccess.RunSQLReturnTable("SELECT * FROM Port_Dept");
		dt.TableName = "Port_Dept";
		dsPort.Tables.add(dt);

		//Port_Station
		dt = DBAccess.RunSQLReturnTable("SELECT * FROM Port_Station");
		dt.TableName = "Port_Station";
		dsPort.Tables.add(dt);

		//Port_EmpStation
		dt = DBAccess.RunSQLReturnTable("SELECT * FROM Port_DeptEmpStation");
		dt.TableName = "Port_DeptEmpStation";
		dsPort.Tables.add(dt);


		dsPort.WriteXml(path + "/PortTables.xml");

			///#endregion 备份表单相关数据.


			///#region 3.备份系统数据
		DataSet dsSysTables = new DataSet();

		//Sys_EnumMain
		dt = DBAccess.RunSQLReturnTable("SELECT * FROM Sys_EnumMain");
		dt.TableName = "Sys_EnumMain";
		dsSysTables.Tables.add(dt);

		//Sys_Enum
		dt = DBAccess.RunSQLReturnTable("SELECT * FROM " + bp.sys.base.Glo.SysEnum());
		dt.TableName = "Sys_Enum";
		dsSysTables.Tables.add(dt);

		//Sys_FormTree
		dt = DBAccess.RunSQLReturnTable("SELECT * FROM Sys_FormTree");
		dt.TableName = "Sys_FormTree";
		dsSysTables.Tables.add(dt);
		dsSysTables.WriteXml(path + "/SysTables.xml");

			///#endregion 备份系统数据.


			///#region 4.备份表单相关数据.
		String pathOfTables = path + "/SFTables";
		(new File(pathOfTables)).mkdirs();
		SFTables tabs = new SFTables();
		tabs.RetrieveAll();
		for (SFTable item : tabs.ToJavaList())
		{
			if (item.getNo().contains("."))
			{
				continue;
			}

			if (!Objects.equals(item.getSrcType(), DictSrcType.CreateTable))
			{
				continue;
			}

			try
			{
				String sql = "SELECT * FROM " + item.getNo() + " ";
				DataSet ds = new DataSet();
				ds.Tables.add(DBAccess.RunSQLReturnTable(sql));
				ds.WriteXml(pathOfTables + "/" + item.getNo() + ".xml", XmlWriteMode.WriteSchema, ds);
			}
			catch (java.lang.Exception e)
			{

			}
		}

			///#endregion 备份表单相关数据.


			///#region 5.备份流程.
		Flows fls = new Flows();
		fls.RetrieveAllFromDBSource();
		for (Flow fl : fls.ToJavaList())
		{
			FlowSort fs = new FlowSort();
			fs.setNo(fl.getFlowSortNo());
			fs.RetrieveFromDBSources();

			String pathDir = path + "/Flow/" + fs.getNo() + "." + fs.getName() + "/";
			if ((new File(pathDir)).isDirectory() == false)
			{
				(new File(pathDir)).mkdirs();
			}

			fl.DoExpFlowXmlTemplete(pathDir);
		}

			///#endregion 备份流程.


			///#region 6.备份表单.
		MapDatas mds = new MapDatas();
		mds.RetrieveAllFromDBSource();
		for (MapData md : mds.ToJavaList())
		{
			if (md.getFormTreeNo().length() < 2)
			{
				continue;
			}

			SysFormTree fs = new SysFormTree();
			fs.setNo(md.getFormTreeNo());
			fs.RetrieveFromDBSources();

			String pathDir = path + "/Form/" + fs.getNo() + "." + fs.getName();
			if ((new File(pathDir)).isDirectory() == false)
			{
				(new File(pathDir)).mkdirs();
			}
			DataSet ds = CCFormAPI.GenerHisDataSet(md.getNo(), null, null);
			ds.WriteXml(pathDir + "/" + md.getName() +".xml", XmlWriteMode.WriteSchema, ds);
		}

			///#endregion 备份表单.

		return "执行成功,存放路径:" + path;
	}
}
