package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.sys.CCFormAPI;
import java.io.*;
import java.text.SimpleDateFormat;

/** 
 页面功能实体
*/
public class WF_Admin_FoolFormDesigner_StyletDfine extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_Admin_FoolFormDesigner_StyletDfine() throws Exception {

	}


		///#region GloValStyles.htm
	public final String GloValStyles_PinYin() throws Exception {
		String name = this.GetRequestVal("TB_Name");

		//表单No长度最大100，因有前缀CCFrm_，因此此处设置最大94，added by liuxc,2017-9-25
		String str = CCFormAPI.ParseStringToPinyinField(name, true, true, 94);

		GloVar en = new GloVar();
		en.setNo(str);
		if (en.RetrieveFromDBSources() == 0)
		{
			return str;
		}

		return "err@标签:" + str + "已经被使用.";
	}
	public final String GloValStyles_Init() throws Exception {
		String val = this.GetRequestVal("CSS");
		if (DataType.IsNullOrEmpty(val) == true)
		{
			return "";
		}

		GloVar en = new GloVar(val);

		//生成风格文件.
		String docs = GenerStyleDocs(en);

		//内容.
		docs = docs.replace(en.getNo(), "GloValsTemp");

		//要去掉.
		docs = docs.replace("!important", "");

		//保存一个临时文件,
		String path = bp.difference.SystemConfig.getPathOfDataUser() + "Style/GloVarsCSSTemp.css";
		DataType.SaveAsFile(path, docs);

		return "风格文件已经生成:" + path;
	}
	/** 
	 应用.
	 
	 @return 
	*/
	public final String GloValStyles_App() throws Exception {
		GloVars ens = new GloVars();
		ens.Retrieve(GloVarAttr.GroupKey, "CSS", null);

		String html = "";
		for (GloVar item : ens.ToJavaList())
		{
			//生成风格文件.
			html += GenerStyleDocs(item);
		}

		//保存一个临时文件,
		String path = bp.difference.SystemConfig.getPathOfDataUser() + "Style/GloVarsCSS.css";
		DataType.SaveAsFile(path, html);

		return "执行成功.";
	}

		///#endregion


		///#region Default.htm 风格设计页面..
	/** 
	 保存为模版.
	 
	 @return 
	*/
	public final String Default_SaveAsTemplate() throws Exception {
		try
		{
			GloVars ens = new GloVars();
			ens.Retrieve("GroupKey", "FoolFrmStyle", "Idx");

			String myName = this.GetRequestVal("TemplateName");

			if (DataType.IsNullOrEmpty(myName) == true)
			{
				myName = new SimpleDateFormat("MM月dd日HH时mm分ss秒").toString();
			}

			String path = bp.difference.SystemConfig.getPathOfDataUser() + "Style/TemplateFoolFrm/" + myName + ".xml";
			ens.SaveToXml(path);

			return "模版创建成功.";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/** 
	 模版选择
	 
	 @return 
	*/
	public final String Default_Template_Selected() throws Exception {
		return Default_Selected_Ext(false);
	}
	/** 
	 初始化表单风格
	 
	 @return 
	*/
	public final String Default_GenerGloVars() throws Exception {
		//获得标准的配置文件,用于比较缺少或者删除的标记.
		String path = bp.difference.SystemConfig.getPathOfWebApp() + "WF/Admin/FoolFormDesigner/StyletDfine/DefaultStyle.xml";

		DataSet ds = new DataSet();
		ds.readXml(path);
		DataTable dt = ds.Tables.get(0);

		GloVars ens = new GloVars();
		ens.Retrieve("GroupKey", "FoolFrmStyle", "Idx");


			///#region  检查是否有新增的标签,如果有就 insert 一个。
		int idx = 0;
		for (DataRow dr : dt.Rows)
		{
			idx++;
			String no = dr.getValue(0).toString();
			String name = dr.getValue(1).toString();
			String val = dr.getValue(2).toString();
			if (ens.contains(no) == false)
			{
				GloVar myen = new GloVar();
				myen.setNo(no);
				myen.setName(name);
				myen.setVal(val);
				myen.setGroupKey("FoolFrmStyle");
				myen.setIdx(idx);
				myen.Insert();
				ens.AddEntity(myen);
			}
		}

			///#endregion  检查是否有新增的标签,如果有就insert一个。


			///#region  检查是否有 多余 的标签,如果有就 Delete .
		boolean isChange = false;
		for (GloVar item : ens.ToJavaList())
		{
			boolean isHave = false;
			for (DataRow dr : dt.Rows)
			{
				String no = dr.getValue(0).toString();

				if (item.getNo().equals(no) == false)
				{
					continue;
				}

				isHave = true;
			}

			if (isHave == false)
			{
				item.Delete();
				isChange = true;
			}
		}

		//如果发生了变化,就重新查询.
		if (isChange == true)
		{
			ens.Retrieve("GroupKey", "FoolFrmStyle", "Idx");
		}

			///#endregion  检查是否有 多余 的标签,如果有就Delete 。

		Default_App_Ext(ens, false);

		return ens.ToJson("dt");
	}
	/** 
	 应用
	 
	 @return 
	*/
	public final String Default_App() throws Exception {
		String docs = "";

		//查询出来所有的.
		GloVars ens = new GloVars();
		ens.Retrieve("GroupKey", "FoolFrmStyle", "Idx");
		return Default_App_Ext(ens, true);
	}
	/** 
	 生成
	 
	 param ens
	 param isApp
	 @return 
	*/

	public final String Default_App_Ext(GloVars ens) throws Exception {
		return Default_App_Ext(ens, false);
	}

//ORIGINAL LINE: public String Default_App_Ext(GloVars ens, bool isApp = false)
	public final String Default_App_Ext(GloVars ens, boolean isApp) throws Exception {
		String docs = "";

		for (GloVar en : ens.ToJavaList())
		{
			docs += GenerStyleDocs(en);
		}

		//保存.
		if (isApp == true)
		{
			String pathDefault = bp.difference.SystemConfig.getPathOfDataUser() + "Style/FoolFrmStyle/Default.css";
			DataType.SaveAsFile(pathDefault, docs);
		}

		//保存一个临时文件,
		String path = bp.difference.SystemConfig.getPathOfDataUser() + "Style/FoolFrmStyle/DefaultPreview.css";
		DataType.SaveAsFile(path, docs);

		return "info@风格文件已经生成:" + path;
	}
	private String GenerStyleDocs(GloVar en) throws Exception {
		String docs = "";
		docs += "\t\n/* " + en.getName() + " */";

		docs += "\t\n." + en.getNo();
		docs += "\t\n{ ";

		AtPara ap = new AtPara(en.getVal());
		for (String item : ap.getHisHT().keySet())
		{
			//特殊标记.
			if (item.contains("_Temp") == true)
			{
				continue;
			}

			docs += "\t\n " + item + ":" + ap.GetValStrByKey(item).trim().replace(" ", "") + " !important;";
			//docs += "\t\n " + item + ":" + ap.GetValStrByKey(item).Trim().replace(" ", "") + " ;";
		}
		docs += "\t\n }";
		return docs;
	}

		///#endregion 风格设计页面..


		///#region Template.htm 模版页面.
	/** 
	 应用
	 
	 @return 
	*/
	public final String Template_App() throws Exception {
		String str = Default_Selected_Ext(true);
		return str;
	}
	/** 
	 删除文件.
	 
	 @return 
	*/
	public final String Default_Template_Delete() throws Exception {
		String path = bp.difference.SystemConfig.getPathOfDataUser() + "Style/TemplateFoolFrm/";
		(new File(path + this.getName())).delete();
		return "删除成功.";
	}
	/** 
	 初始化数据
	 
	 @return 
	*/
	public final String Default_GenerTemplate() throws Exception {
		String path = bp.difference.SystemConfig.getPathOfDataUser() + "Style/TemplateFoolFrm/";
		String[] fls = bp.tools.BaseFileUtils.getFiles(path);


		DataTable dt = new DataTable();
		dt.Columns.Add("No");
		dt.Columns.Add("Name");

		for (String item : fls)
		{
			File info = new File(item);

			DataRow dr = dt.NewRow();
			String name = info.getName();
			if (name.contains("Default") == true)
			{
				continue;
			}

			if (name.contains("Sys.xml") == true)
			{
				name = info.getName().replace("Sys.xml", "[系统]");
			}
			else
			{
				name = info.getName().replace(".xml", "[自定义]");
			}


			dr.setValue(1, name);

			dr.setValue(0, info.getName());
			dt.Rows.add(dr);
		}
		return bp.tools.Json.ToJson(dt);
	}

	/** 
	 模版选择.
	 
	 @return 
	*/
	public final String Default_Selected_Ext(boolean isApp) throws Exception {
		String filePath = bp.difference.SystemConfig.getPathOfDataUser() + "Style/TemplateFoolFrm/" + this.getName();

		DataSet ds = new DataSet();
		ds.readXml(filePath);

		DataTable dt = ds.Tables.get(0);

		GloVars ens = new GloVars();
		int idx = 0;
		for (DataRow dr : dt.Rows)
		{
			String key = dr.getValue("No").toString();
			String name = dr.getValue("Name").toString();
			String val = dr.getValue("Val").toString();

			idx++;
			GloVar en = new GloVar();
			en.setNo(key);
			en.setName(name);
			en.setGroupKey("FoolFrmStyle");
			en.setVal(val);
			en.setIdx(idx);
			ens.AddEntity(en);
		}
		ens.Delete("GroupKey", "FoolFrmStyle");
		for (GloVar en : ens.ToJavaList())
		{
			en.Insert();
		}

		//生成临时文件.  如果是 isApp==true ,就生成正式的风格文件.
		Default_App_Ext(ens, isApp);

		return "执行成功.";
	}

		///#endregion 模版页面.

}