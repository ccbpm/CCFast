package bp.wf.httphandler;

import bp.da.*;
import bp.difference.handler.CommonFileUtils;
import bp.sys.*;
import bp.sys.CCFormAPI;
import bp.tools.DateUtils;
import bp.tools.FtpUtil;
import bp.web.*;
import bp.difference.*;
import bp.wf.*;
import org.apache.commons.net.ftp.FTPFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/** 
 页面功能实体
*/
public class WF_Admin_Template extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_Admin_Template()
	{

	}
	/** 
	 导入本机模版 
	 负责人：lizhen.
	 
	 @return 
	*/
	public final String ImpFrmLocal_Done() throws Exception {

		/**表单类型.
		*/
		String frmSort = this.GetRequestVal("FrmSort");

		//创建临时文件.
		String temp = SystemConfig.getPathOfTemp() + "/" + DBAccess.GenerGUID() + ".xml";
		HttpServletRequest request = getRequest();

		try{
			CommonFileUtils.upload(request, "file",new File(temp));
		}catch(Exception e){
			e.printStackTrace();
			return "err@执行失败";
		}

		//获得数据类型.
		DataSet ds = new DataSet();
		ds.readXml(temp);

		MapData md = new MapData();

		//获得frmID.
		String frmID = null;


			///#region 检查模版是否正确.
		//检查模版是否正确.
		String errMsg = "";
		if (ds.contains("WF_Flow") == true)
		{
			return "err@此模板文件为流程模板。";
		}

		if (ds.contains("Sys_MapAttr") == false)
		{
			return "err@缺少表:Sys_MapAttr";
		}

		if (ds.contains("Sys_MapData") == false)
		{
			return "err@缺少表:Sys_MapData";
		}


		frmID = ds.GetTableByName("Sys_MapData").Rows.get(0).getValue("No").toString();

			///#endregion 检查模版是否正确.

		String impType = this.GetRequestVal("RB_ImpType");

		//执行导入.
		return ImpFrm(impType, frmID, md, ds, frmSort);
	}

	public final String ImpFrm(String impType, String frmID, MapData md, DataSet ds, String frmSort) throws Exception {
		//导入模式:按照模版的表单编号导入,如果该编号已经存在就提示错误
		if (Objects.equals(impType, "0"))
		{
			md.setNo(frmID);
			if (md.RetrieveFromDBSources() == 1)
			{
				return "err@该表单ID【" + frmID + "】已经存在数据库中,您不能导入.";
			}
			md = CCFormAPI.Template_LoadXmlTemplateAsNewFrm(ds, frmSort);
		}

		//导入模式:按照模版的表单编号导入,如果该编号已经存在就直接覆盖.
		if (Objects.equals(impType, "1"))
		{
			md.setNo(frmID);
			if (md.RetrieveFromDBSources() == 1)
			{
				md.Delete(); //直接删除.
			}
			md = CCFormAPI.Template_LoadXmlTemplateAsNewFrm(ds, frmSort); // MapData.ImpMapData(ds);
		}

		//导入模式:按照模版的表单编号导入,如果该编号已经存在就增加@WebUser.OrgNo(组织编号)导入.
		if (Objects.equals(impType, "2"))
		{
			md.setNo(frmID);
			if (md.RetrieveFromDBSources() == 1)
			{
				md.setNo(frmID + WebUser.getOrgNo());
				if (md.RetrieveFromDBSources() == 1)
				{
					return "err@表单编号为:" + md.getNo() + "已存在.";
				}
				frmID = frmID + "" + WebUser.getOrgNo();
				md.setNo(frmID);
			}
			md = CCFormAPI.Template_LoadXmlTemplateAsSpecFrmID(frmID, ds, frmSort); // MapData.ImpMapData(ds);
		}

		//导入模式:按照指定的模版ID导入.
		if (Objects.equals(impType, "3"))
		{
			frmID = this.GetRequestVal("TB_SpecFrmID");
			md.setNo(frmID);
			if (md.RetrieveFromDBSources() == 1)
			{
				return "err@您输入的表单编号为:" + md.getNo() + "已存在.";
			}
			md = CCFormAPI.Template_LoadXmlTemplateAsSpecFrmID(frmID, ds, frmSort); // MapData.ImpMapData(ds);
		}
		if (Objects.equals(impType, "3ftp"))
		{
			md.setNo(frmID);
			if (md.RetrieveFromDBSources() == 1)
			{
				return "err@您输入的表单编号为:" + md.getNo() + "已存在.";
			}
			md = CCFormAPI.Template_LoadXmlTemplateAsSpecFrmID(frmID, ds, frmSort); // MapData.ImpMapData(ds);
		}

		return "执行成功.";
	}

	/** 
	 初始化
	 
	 @return 
	*/
	public final String Flow_Init() throws Exception {
		String dirName = this.GetRequestVal("DirName");
		if (DataType.IsNullOrEmpty(dirName) == true)
		{
			dirName = "/Flow/";
		}
		if (dirName.indexOf("/Flow/") == -1)
		{
			dirName = "/Flow/" + dirName;
		}
		FtpUtil ftpUtil = bp.wf.Glo.getFtpUtil();
		ftpUtil.changeWorkingDirectory(dirName,true);
		DataSet ds = new DataSet();
		FTPFile[] fls;
		try
		{
			fls = ftpUtil.fileList();
		}
		catch (java.lang.Exception e)
		{
			return "err@该目录无文件";
		}
		DataTable dtDir = new DataTable();
		dtDir.TableName = "Dir";
		dtDir.Columns.Add("FileName", String.class);
		dtDir.Columns.Add("RDT", String.class);
		dtDir.Columns.Add("Path", String.class);
		ds.Tables.add(dtDir);

		//把文件加里面.
		DataTable dtFile = new DataTable();
		dtFile.TableName = "File";
		dtFile.Columns.Add("FileName", String.class);
		dtFile.Columns.Add("RDT", String.class);
		dtFile.Columns.Add("Path", String.class);
		for (FTPFile fl : fls)
		{

			switch (fl.getType())
			{
				case FTPFile.DIRECTORY_TYPE:
				{
					DataRow drDir = dtDir.NewRow();
					drDir.setValue(0, fl.getName());
					drDir.setValue(1, DateUtils.format(fl.getTimestamp().getTime(), "yyyy-MM-dd HH:mm"));
					drDir.setValue(2, dirName + "/" + fl.getName());
					dtDir.Rows.add(drDir);
					continue;
				}
				default:
					break;
			}

			DataRow dr = dtFile.NewRow();
			dr.setValue(0, fl.getName());
			dr.setValue(1, DateUtils.format(fl.getTimestamp().getTime(), "yyyy-MM-dd HH:mm"));
			dr.setValue(2, dirName + "/" + fl.getName());
			dtFile.Rows.add(dr);
		}
		ds.Tables.add(dtFile);
		return bp.tools.Json.ToJson(ds);
	}
	/** 
	 初始化表单模板
	 
	 @return 
	*/
	public final String Form_Init() throws Exception
	{
		String dirName = this.GetRequestVal("DirName");
		if (DataType.IsNullOrEmpty(dirName) == true)
		{
			dirName = "/Form/";
		}
		if (dirName.indexOf("/Form/") == -1)
		{
			dirName = "/Form/" + dirName;
		}
		FtpUtil ftpUtil = bp.wf.Glo.getFtpUtil();
		ftpUtil.changeWorkingDirectory(dirName,true);
		DataSet ds = new DataSet();
		FTPFile[] fls;
		try
		{
			fls = ftpUtil.fileList();
		}
		catch (java.lang.Exception e)
		{

			//System.Windows.Forms.MessageBox.Show("该目录无文件");
			return "err@该目录无文件";
		}

		DataTable dtDir = new DataTable();
		dtDir.TableName = "Dir";
		dtDir.Columns.Add("FileName", String.class);
		dtDir.Columns.Add("RDT", String.class);
		dtDir.Columns.Add("Path", String.class);
		ds.Tables.add(dtDir);

		//把文件加里面.
		DataTable dtFile = new DataTable();
		dtFile.TableName = "File";
		dtFile.Columns.Add("FileName", String.class);
		dtFile.Columns.Add("RDT", String.class);
		dtFile.Columns.Add("Path", String.class);
		for (FTPFile fl : fls)
		{

			switch (fl.getType())
			{
				case FTPFile.DIRECTORY_TYPE:
				{
					DataRow drDir = dtDir.NewRow();
					drDir.setValue(0, fl.getName());
					drDir.setValue(1, DateUtils.format(fl.getTimestamp().getTime(), "yyyy-MM-dd HH:mm"));
					drDir.setValue(2, dirName + "/" + fl.getName());
					dtDir.Rows.add(drDir);
					continue;
				}
				default:
					break;
			}

			DataRow dr = dtFile.NewRow();
			dr.setValue(0, fl.getName());
			dr.setValue(1, DateUtils.format(fl.getTimestamp().getTime(), "yyyy-MM-dd HH:mm"));
			dr.setValue(2, dirName + "/" + fl.getName());
			dtFile.Rows.add(dr);
		}
		ds.Tables.add(dtFile);
		return bp.tools.Json.ToJson(ds);
	}
	/** 
	 导入流程模板
	 
	 @return 
	*/
	public final String Flow_Imp() throws Exception {
		//构造返回数据.
		DataTable dtInfo = new DataTable();
		dtInfo.Columns.Add("Name"); //文件名.
		dtInfo.Columns.Add("Info"); //导入信息。
		dtInfo.Columns.Add("Result"); //执行结果.

		//获得下载的文件名.
		String fls = this.GetRequestVal("Files");
		String[] strs = fls.split("[;]", -1);

		String sortNo = GetRequestVal("SortNo"); //流程类别.
		String dirName = GetRequestVal("DirName"); //目录名称.
		if (DataType.IsNullOrEmpty(dirName) == true)
		{
			dirName = "/";
		}
		FtpUtil ftpUtil = bp.wf.Glo.getFtpUtil();
		String remotePath = ftpUtil.getFtpClient().printWorkingDirectory() + dirName;

		String err = "";
		for (String str : strs)
		{
			if (str.equals("") || str.indexOf(".xml") == -1)
			{
				continue;
			}


			///下载文件.
			//设置要到的路径.
			String tempfile = SystemConfig.getPathOfTemp() + str;
			boolean fs;
			try
			{
				//下载目录下.
				fs = ftpUtil.downloadFile("/Flow" + remotePath + "/" + str,tempfile);
			}
			catch (RuntimeException ex)
			{
				dtInfo = this.ImpAddInfo(dtInfo, str, ex.getMessage(), "失败.");
				continue;
			}

			if (fs == false)
			{
				dtInfo = this.ImpAddInfo(dtInfo, str, "模板未下载成", "失败.");
				continue;
			}

			/// 下载文件.


			///执行导入.
			Flow flow = new Flow();
			try
			{
				//执行导入.
				//flow = bp.wf.Flow.DoLoadFlowTemplate(sortNo, tempfile, ImpFlowTempleteModel.AsNewFlow);
				flow.DoCheck(); //要执行一次检查.

				dtInfo = this.ImpAddInfo(dtInfo, str, "执行成功:新流程编号:" + flow.getNo() + " - " + flow.getName(), "成功.");
			}
			catch (RuntimeException ex)
			{
				dtInfo = this.ImpAddInfo(dtInfo, str, ex.getMessage(), "导入失败.");
			}

			/// 执行导入.
		}

		return bp.tools.Json.ToJson(dtInfo);
	}
	public final DataTable ImpAddInfo(DataTable dtInfo, String fileName, String info, String result)
	{
		DataRow dr = dtInfo.NewRow();
		dr.setValue(0, fileName);
		dr.setValue(1, info);
		dr.setValue(2, result);
		dtInfo.Rows.add(dr);
		return dtInfo;
	}
	/** 
	 导入表单模板
	 
	 @return 
	*/
	public final String Form_Step1() throws Exception {
		//构造返回数据.
		DataTable dtInfo = new DataTable();
		dtInfo.Columns.Add("Name"); //文件名.
		dtInfo.Columns.Add("Info"); //导入信息.
		dtInfo.Columns.Add("Result"); //执行结果.

		//获得变量.
		String fls = this.GetRequestVal("Files");
		String[] strs = fls.split("[;]", -1);
		String sortNo = GetRequestVal("SortNo");
		String dirName = GetRequestVal("DirName");
		if (DataType.IsNullOrEmpty(dirName) == true)
		{
			dirName = "/";
		}

		FtpUtil ftpUtil = bp.wf.Glo.getFtpUtil();
		String remotePath = ftpUtil.getFtpClient().printWorkingDirectory() + dirName;

		MapData md = new MapData();
		/**遍历选择的文件.
		 */
		for (String str : strs)
		{
			if (str.equals("") || str.indexOf(".xml") == -1)
			{
				continue;
			}

			String[] def = str.split("[,]", -1);
			String fileName = def[0]; //文件名
			String model = def[1]; //模式. 3=按照指定的表单ID进行导入.
			String frmID = def[2]; //指定表单的ID.

			if (model.equals("3") && DataType.IsNullOrEmpty(frmID) == true)
			{
				dtInfo = this.ImpAddInfo(dtInfo, fileName, "您需要指定表单ID", "导入失败");
				continue;
			}

			//设置要到的路径.
			String tempfile = SystemConfig.getPathOfTemp() + fileName;

			//下载目录下
			boolean fs = ftpUtil.downloadFile("/Form" + remotePath + "/" + fileName,tempfile);
			if (fs == false)
			{
				dtInfo = this.ImpAddInfo(dtInfo, fileName, "文件下载失败", "导入失败");
				continue;
			}

			//读取文件.
			DataSet ds = new DataSet();
			ds.readXml(tempfile);

			if (ds.GetTableByName("Sys_MapData") == null)
			{
				dtInfo = this.ImpAddInfo(dtInfo, str, "模版不存在Sys_MapData表,非法的表单.", "导入失败");
				continue;
			}


			try
			{
				if (model.equals("3"))
				{
					model += "ftp";
				}
				String info = this.ImpFrm(model, frmID, md, ds, sortNo);

				if (info.contains("err@"))
				{
					dtInfo = this.ImpAddInfo(dtInfo, fileName, info, "导入失败");
				}
				else
				{
					dtInfo = this.ImpAddInfo(dtInfo, fileName, info, "导入成功");
				}
			}
			catch (RuntimeException ex)
			{
				dtInfo = this.ImpAddInfo(dtInfo, str, ex.getMessage(), "导入失败");
			}
		}

		//返回执行结果.
		return bp.tools.Json.ToJson(dtInfo);
	}

		///#endregion 界面方法.

}
