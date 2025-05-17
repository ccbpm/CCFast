package bp.wf.httphandler;

import bp.difference.handler.CommonFileUtils;
import bp.sys.*;
import bp.da.*;
import bp.ccbill.*;
import bp.difference.*;
import bp.*;
import bp.sys.CCFormAPI;
import bp.tools.BaseFileUtils;
import bp.wf.*;
import bp.wf.Dev2Interface;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;

public class WF_Admin_DevelopDesigner extends bp.difference.handler.DirectoryPageBase
{

		///#region 执行父类的重写方法.
	/** 
	 构造函数
	*/
	public WF_Admin_DevelopDesigner()
	{

	}
	/** 
	 表单初始化
	 
	 @return 
	*/
	public final String Designer_Init() throws Exception {
		//获取htmlfrom 信息.
		String htmlCode = DBAccess.GetBigTextFromDB("Sys_MapData", "No", this.getFrmID(), "HtmlTemplateFile");

		if (DataType.IsNullOrEmpty(htmlCode) == true)
		{
			htmlCode = "<h3>请插入表单模板.</h3>";
		}

		//把数据同步到DataUser/CCForm/HtmlTemplateFile/文件夹下
		String filePath = SystemConfig.getPathOfDataUser() + "CCForm/HtmlTemplateFile/";
		if ((new File(filePath)).isDirectory() == false)
		{
			(new File(filePath)).mkdirs();
		}
		filePath = filePath + this.getFrmID() + ".htm";

		//写入到html 中
		DataType.WriteFile(filePath, htmlCode);
		return htmlCode;
	}
	/** 
	 保存表单
	 
	 @return 
	*/
	public final String SaveForm() throws Exception {
		//获取html代码
		String htmlCode = this.GetRequestVal("HtmlCode");
		if (DataType.IsNullOrEmpty(htmlCode) == true)
		{
			return "err@表单内容不能为空.";
		}

		if (htmlCode.contains("err@") == true)
		{
			return "err@错误" + htmlCode;
		}

		//htmlCode = HttpUtility.UrlDecode(htmlCode, Encoding.UTF8);

		return Dev2Interface.SaveDevelopForm(htmlCode, this.getFrmID());

	}

		///#endregion


		///#region 插入模版.
	/** 
	 获取开发者表单模板目录
	 
	 @return 
	*/
	public final String Template_Init() throws Exception {
		DataSet ds = new DataSet();
		String path = SystemConfig.getPathOfDataUser() + "Style/TemplateFoolDevelopDesigner/";
		if(SystemConfig.isJarRun())
			path = SystemConfig.getPhysicalPath()+"/DataUser/Style/TemplateFoolDevelopDesigner/";
		File dirFile = new File(path);
		File[] files = dirFile.listFiles(); //获取子文件夹
		//模版类型
		DataTable dt = new DataTable();
		dt.TableName = "dirs";
		dt.Columns.Add("No");
		dt.Columns.Add("Name");
		DataRow dr = dt.NewRow();
		//模版信息
		DataTable filesDt = new DataTable();
		filesDt.TableName = "temps";
		filesDt.Columns.Add("No");
		filesDt.Columns.Add("Name");
		filesDt.Columns.Add("Dir");
		DataRow tempdr = filesDt.NewRow();

		for (File item : files)
		{
			//模版分类
			dr = dt.NewRow();
			dr.setValue("No", item);
			dr.setValue("Name", item.getName());
			dt.Rows.add(dr);

			//获取模版
			File[] itemfiles = item.listFiles();
			if(itemfiles==null)
				continue;
			for (File temp : itemfiles)
			{
				if(temp.getName().endsWith(".htm")==false)
					continue;
				tempdr = filesDt.NewRow();
				tempdr.setValue("No", temp);
				tempdr.setValue("Name", temp.getName());
				tempdr.setValue("Dir", item.getName());
				filesDt.Rows.add(tempdr);
			}
		}

		ds.Tables.add(dt);
		ds.Tables.add(filesDt);

		return bp.tools.Json.ToJson(ds);
	}
	/** 
	 根据名称获取开发者表单文件内容
	 
	 @return 
	*/
	public final String Template_GenerHtml() throws Exception {
		String fileName = this.GetRequestVal("DevTempName");
		String fielDir = this.GetRequestVal("DevTempDir");
		String path = SystemConfig.getPathOfDataUser() + "Style/TemplateFoolDevelopDesigner/" + fielDir + "/";
		String filePath = path + fileName;
		String strHtml = DataType.ReadTextFile(filePath);
		return strHtml;
	}
	public final String Template_Imp() throws Exception {
		//File files = HttpContextHelper.RequestFiles(); //context.Request.Files;
		File xmlFile = null;
		String fileName = "";
		HttpServletRequest request = getRequest();
		String contentType = request.getContentType();
		if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
			//return "err@请选择要上传的流程模版。";


			//设置文件名
			//String fileNewName = (new File(files.get(0).FileName)).getName(); // DateTime.Now.ToString("yyyyMMddHHmmssff") + "_" + System.IO.Path.GetFileName(files[0].FileName);
			fileName = CommonFileUtils.getOriginalFilename(request, "file");
			//文件存放路径
			String filePath = SystemConfig.getPathOfDataUser() + "Style/TemplateFoolDevelopDesigner/" + "" + fileName;
			String savePath = SystemConfig.getPathOfDataUser() + "JSLibData" + "/" + fileName;
			xmlFile = new File(savePath);
			if (xmlFile.exists()) {
				xmlFile.delete();
			}

			try {
				CommonFileUtils.upload(request, "file", xmlFile);
			} catch (Exception e) {
				e.printStackTrace();
				return "err@执行失败";
			}
		}
		return "模板" + fileName + "导入成功";
	}


	///#endregion 插入模版.

	public final String Fields_Init() throws Exception {
		String html = DBAccess.GetBigTextFromDB("Sys_MapData", "No", this.getFrmID(), "HtmlTemplateFile");
		return html;
	}

	/** 
	 格式化html的文档.
	 
	 @return 
	*/
	public final String Designer_FormatHtml() throws Exception {
		String html = DBAccess.GetBigTextFromDB("Sys_MapData", "No", this.getFrmID(), "HtmlTemplateFile");

		return "替换成功.";
		//return html;
	}

	/** 
	 表单重置
	 
	 @return 
	*/
	public final String ResetFrm_Init()
	{
		//删除html
		String filePath = SystemConfig.getPathOfDataUser() + "CCForm/HtmlTemplateFile/" + this.getFrmID() + ".htm";
		if ((new File(filePath)).isFile() == true)
		{
			(new File(filePath)).delete();
		}

		//删除存储的html代码
		String sql = "UPDATE Sys_MapData SET HtmlTemplateFile='' WHERE No='" + this.getFrmID() + "'";
		DBAccess.RunSQL(sql);
		//删除MapAttr中的数据
		sql = "Delete Sys_MapAttr WHERE FK_MapData='" + this.getFrmID() + "'";
		DBAccess.RunSQL(sql);
		//删除MapExt中的数据
		sql = "Delete Sys_MapExt WHERE FK_MapData='" + this.getFrmID() + "'";
		DBAccess.RunSQL(sql);

		return "重置成功";
	}


		///#region 复制表单

	public final void DoCopyFrm() throws Exception {
		String fromFrmID = GetRequestVal("FromFrmID");
		String toFrmID = GetRequestVal("ToFrmID");
		String toFrmName = GetRequestVal("ToFrmName");

			///#region 原表单信息
		//表单信息
		MapData fromMap = new MapData(fromFrmID);
		//单据信息
		FrmBill fromBill = new FrmBill();
		fromBill.setNo(fromFrmID);
		int billCount = fromBill.RetrieveFromDBSources();
		//实体单据
		FrmDict fromDict = new FrmDict();
		fromDict.setNo(fromFrmID);
		int DictCount = fromDict.RetrieveFromDBSources();

			///#endregion 原表单信息


			///#region 复制表单
		MapData toMapData = new MapData();
		toMapData = fromMap;
		toMapData.setNo(toFrmID);
		toMapData.setName(toFrmName);
		toMapData.Insert();
		if (billCount != 0)
		{
			FrmBill toBill = new FrmBill();
			toBill = fromBill;
			toBill.setNo(toFrmID);
			toBill.setName(toFrmName);
			toBill.setEntityType(EntityType.FrmBill);
			toBill.Update();
		}
		if (DictCount != 0)
		{
			FrmDict toDict = new FrmDict();
			toDict = fromDict;
			toDict.setNo(toFrmID);
			toDict.setName(toFrmName);
			toDict.setEntityType(EntityType.FrmDict);
			toDict.Update();
		}

			///#endregion 复制表单

		MapData.ImpMapData(toFrmID, CCFormAPI.GenerHisDataSet_AllEleInfo(fromFrmID));

		//清空缓存
		toMapData.RepairMap();
		SystemConfig.DoClearCache();


	}

		///#endregion 复制表单

}
