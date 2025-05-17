package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import bp.web.*;
import bp.difference.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 用户自定义表
*/
public class SFSearch extends EntityNoName
{
	public final String getFKSFDBSrc()  {
		return this.GetValStringByKey("FK_SFDBSrc");
	}
	public final String getSelectStatement()  {
		return this.GetValStringByKey("SelectStatement");
	}


		///#region 数据源属性.
	/**
	 获得外部数据表
	*/

	public final DataTable GenerHisDataTable() throws Exception {
		return GenerHisDataTable(null);
	}

	public final DataTable GenerHisDataTable(Hashtable ht) throws Exception {
		//创建数据源.
		SFDBSrc src = new SFDBSrc(this.getFKSFDBSrc());
		DataTable dt = null;
			///#region WebApi接口
		if (src.getDBSrcType().equals("WebApi") == true)
		{
			//执行POST
			String postData = bp.en.OverrideFile.Data_WebApi(ht,this.GetValStringByKey("RequestMethod"), this.getFKSFDBSrc(), this.getSelectStatement(), this.GetValStringByKey("PostDoc"), this.GetValStringByKey("ParamAlia"));
			String jsonNode = this.GetValStringByKey("JsonNode"); //json的节点.
			ObjectMapper objectMapper = new ObjectMapper();

			// 解析JSON数据
			JsonNode jToken = objectMapper.readTree(postData);


			//JToken jToken = JToken.Parse(postData);

			// 如果是JSON数组
			if (jToken.getNodeType() == JsonNodeType.ARRAY)
			{
				JSONArray arr = JSONArray.fromObject(postData);
				if (arr.isEmpty())
				{
					dt = new DataTable();
				}
				else
				{
					dt = bp.tools.Json.ConvertToDataTable(arr);
				}
			}
			// 如果是JSON对象
			if (jToken.getNodeType() == JsonNodeType.OBJECT)
			{
				JSONObject jsonItem =JSONObject.fromObject(postData);
				if (!DataType.IsNullOrEmpty(jsonNode) && jsonItem.containsKey(jsonNode))
				{
					JSONArray newJsonArr = new JSONArray();
					ObjectMapper objectMapper1= new ObjectMapper();
					JsonNode jToken1 = objectMapper.readTree(jsonItem.getString(jsonNode));
					// 判断当前是不是数组或者
					if (jToken1.getNodeType() == JsonNodeType.ARRAY)
					{
						newJsonArr = JSONArray.fromObject(jsonItem.getString(jsonNode));
						return bp.tools.Json.ToDataTable(newJsonArr.toString());
					}
					if (jToken1.getNodeType() == JsonNodeType.OBJECT)
					{
						JSONObject targetObj =JSONObject.fromObject(jsonItem.getString(jsonNode));
						return bp.tools.Json.ToDataTable(targetObj.toString());
					}
					throw new RuntimeException("指定的RootNode下不是JSON数组 或 JSON对象");
				}

				dt = bp.tools.Json.ToDataTable(jsonItem.toString());
			}

			if (dt == null)
			{
				throw new RuntimeException("Web_API 没有正确返回JSON字符串");
			}
			return dt;
		}

			///#endregion WebApi接口


			///#region SQL接口
		String runObj = this.getSelectStatement();
		if (DataType.IsNullOrEmpty(runObj))
		{
			throw new RuntimeException("@外键类型SQL配置错误," + this.getNo() + " " + this.getName() + " 是一个(SQL)类型(" + this.GetValStrByKey("SrcType") + ")，但是没有配置sql.");
		}

		if (runObj == null)
		{
			runObj = "";
		}
		runObj = runObj.replace("~~", "\"");
		runObj = runObj.replace("~", "'");
		runObj = runObj.replace("/#", "+"); //为什么？
		runObj = runObj.replace("/$", "-"); //为什么？
		if (runObj.contains("@WebUser.No"))
		{
			runObj = runObj.replace("@WebUser.No", WebUser.getNo());
		}

		if (runObj.contains("@WebUser.Name"))
		{
			runObj = runObj.replace("@WebUser.Name", WebUser.getName());
		}

		if (runObj.contains("@WebUser.FK_DeptName"))
		{
			runObj = runObj.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
		}

		if (runObj.contains("@WebUser.FK_Dept"))
		{
			runObj = runObj.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
		}

		if (runObj.contains("@WebUser.DeptNo"))
			runObj = runObj.replace("@WebUser.DeptNo", WebUser.getDeptNo());

		if (runObj.contains("@") == true && ht != null)
		{
			for (Object key : ht.keySet())
			{
				//值为空或者null不替换
				if (ht.get(key) == null || ht.get(key).equals("") == true)
				{
					continue;
				}

				if (runObj.contains("@" + key))
				{
					runObj = runObj.replace("@" + key, ht.get(key).toString());
				}
				//不包含@则返回SQL语句
				if (runObj.contains("@") == false)
				{
					break;
				}
			}
		}
		if (runObj.contains("@") && SystemConfig.isBSsystem() == true)
		{
			/*如果是bs*/
			for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet())
			{
				if (DataType.IsNullOrEmpty(key))
				{
					continue;
				}
				runObj = runObj.replace("@" + key, ContextHolderUtils.getRequest().getParameter(key));
			}
		}
		if (runObj.contains("@") == true)
		{
			throw new RuntimeException("@外键类型SQL错误," + runObj + "部分查询条件没有被替换.");
		}
		try
		{
			dt = src.RunSQLReturnTable(runObj);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("err@获得SFSearch(" + this.getNo() + "," + this.getName() + ")出现错误:SQL[" + runObj + "],数据库异常信息:" + ex.getMessage());
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "No";
			dt.Columns.get(1).ColumnName = "Name";
			if (dt.Columns.size()== 3)
			{
				dt.Columns.get(2).ColumnName = "ParentNo";
			}
		}
		return dt;

			///#endregion SQL接口
	}

	private String Data_WebApi(Hashtable ht) throws Exception {
		//返回值
		//用户输入的webAPI地址
		String apiUrl = this.getSelectStatement();


			///#region 解析路径变量 /{xxx}/{yyy} ? xxx=xxx
		if (apiUrl.contains("{") == true)
		{
			if (ht != null)
			{
				for (Object key : ht.keySet())
				{
					apiUrl = apiUrl.replace("{" + key + "}", ht.get(key).toString());
				}
			}
		}
		if (apiUrl.startsWith("htt") == false)
		{
			SFDBSrc mysrc = new SFDBSrc(this.getFKSFDBSrc());
			apiUrl = mysrc.getConnString() + apiUrl;
		}

		//执行POST
		return bp.tools.HttpClientUtil.doPost(apiUrl);
	}

		///#endregion


		///#region 构造方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForAdmin();
		return uac;
	}
	/**
	 用户自定义表
	*/
	public SFSearch()
	{
	}
	public SFSearch(String no) throws Exception {
		this.setNo(no);
		this.Retrieve();
	}
	/**
	 EnMap
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Sys_SFSearch", "查询");

		map.AddTBStringPK(SFTableAttr.No, null, "表英文名称", true, false, 1, 200, 20);
		map.AddTBString(SFTableAttr.Name, null, "表中文名称", true, false, 0, 200, 20);

		map.AddDDLEntities(SFTableAttr.FK_SFDBSrc, "local", "数据源", new bp.sys.SFDBSrcs(), true);
		map.AddDDLStringEnum(SFTableAttr.DictSrcType, "SQL", "数据源类型", "@SQL=SQL@WebApi=WebApi", false);
		map.AddDDLSysEnum("ResultNum", 0, "返回行数", true, true, "ResultNum", "@0=多行@1=单行");
		map.AddTBString("ConnString", null, "ConnString", true, false, 0, 200, 110, true);
		map.AddTBString(SFTableAttr.SelectStatement, null, "表达式", true, false, 0, 1000, 600, true);
		map.AddTBString("ExpNote", null, "表达式说明", true, false, 0, 1000, 200, true);
		map.AddTBStringDoc("TestParas", null, "测试用例", true, false, true);

		map.AddTBStringDoc("ParamAlia", null, "参数别名", true, false, true);
		map.AddDDLStringEnum("RequestMethod", "Get", "请求模式", "@Get=Get@POST=POST", true);
		map.AddTBStringDoc("PostDoc", null, "POST内容", true, false, true);

		map.AddTBString("JsonNode", "", "Json根节点", true, false, 0, 200, 600, true);

		map.AddTBDateTime(SFTableAttr.RDT, null, "创建日期", false, false);
		map.AddTBString(SFTableAttr.OrgNo, null, "组织编号", false, false, 0, 100, 20);
		map.AddTBAtParas();
		//是否有参数
		//map.AddTBInt("IsPara", 0, "IsPara", false, false);
		//查找.
		map.AddSearchAttr(SFTableAttr.FK_SFDBSrc);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
	public final String TS_SFSearch_Test() throws Exception {
		SFDBSrc src = new SFDBSrc();
		src.setNo(this.getFKSFDBSrc());
		src.RetrieveFromDBSources();

		if (src.getDBSrcType().equals("WebApi") == true)
		{
			String tsUrl = this.GetParaString("Test1");
			//string apiUrl = this.SelectStatement;
			String requestMethod = this.GetValStringByKey("RequestMethod");
			if (requestMethod.toUpperCase().equals("GET") == true)
			{
				tsUrl = src.getConnString() + tsUrl;
				return  bp.tools.HttpClientUtil.doGet(tsUrl);
			}
			if (tsUrl.startsWith("http") == false)
				tsUrl = src.getConnString() + tsUrl;
			String paramData = this.GetParaString("Test2");
			if (DataType.IsNullOrEmpty(paramData))
				paramData = "";
			paramData = paramData.replace("~~", "\"");
			paramData = paramData.replace("~", "\"");
			return bp.tools.HttpClientUtil.doPostJson(tsUrl, paramData);
		}
		String testParas = this.GetValStringByKey("TestParas") ;
		testParas = testParas.replace("~","'");
		if(DataType.IsNullOrEmpty(testParas))
			return "请配置测试用例";
		DataTable dt = src.RunSQLReturnTable(testParas);
		return bp.tools.Json.ToJson(dt);
	}
	/**
	 通过webapi获得json数据.

	 @param paras 参数:@Key=val@Key2=Val2
	 @return json
	 @exception Exception
	*/
	public final String GenerDataOfJson(String paras) throws Exception {
		//把参数转化为 ht.
		SFParas ens = new SFParas();
		ens.Retrieve("RefPKVal", this.getNo());

		//获得ht.
		Hashtable ht = SFTable.GenerHT(paras, ens); // DataType.ParseParasToHT(paras);

		DataTable dt = this.GenerHisDataTable(ht);
		return bp.tools.Json.ToJson(dt);
	}
	/**
	 获得数据

	 @param paras 参数
	 @param slnNo 方案编号：也就是mapExt的MyPK
	 @return 转换的json
	*/
	public final String GenerDataOfJsonUesingSln(String paras, String slnNo) throws Exception {
		//把参数转化为 ht.
		SFParas ens = new SFParas();
		ens.Retrieve("RefPKVal", this.getNo());

		//获得ht.参数.
		Hashtable ht = SFTable.GenerHT(paras, ens); // DataType.ParseParasToHT(paras);
		//获得原始数据.
		DataTable dt = this.GenerHisDataTable(ht);

		//获得转换方案.
		SFColumnSlns colSlns = new SFColumnSlns();
		colSlns.Retrieve("RefPKVal", slnNo, "IsEnable", 1);

		for (SFColumnSln sln : colSlns.ToJavaList())
		{
			String attrKey = sln.getRow().get("AttrKey").toString();
			String toField = sln.getRow().get("ToField").toString();
			if (DataType.IsNullOrEmpty(toField) == true)
			{
				continue;
			}

			if (dt.Columns.contains(attrKey) == true)
			{
				dt.Columns.get(attrKey).ColumnName = toField; //修改列名.
			}
		}
		return bp.tools.Json.ToJson(dt);
	}
	/**
	 检查是否有依赖的引用？

	 @return
	*/
	public final String IsCanDelete() throws Exception {
		MapAttrs mattrs = new MapAttrs();
		mattrs.Retrieve(MapAttrAttr.UIBindKey, this.getNo());
		if (mattrs.size()!= 0)
		{
			String err = "";
			for (MapAttr item : mattrs.ToJavaList())
			{
				err += " @ " + item.getMyPK() + " " + item.getName();
			}
			return "err@如下实体字段在引用:" + err + "。您不能删除该表。";
		}
		return null;
	}
	@Override
	protected boolean beforeDelete() throws Exception
	{
		String delMsg = this.IsCanDelete();
		if (delMsg != null)
		{
			throw new RuntimeException(delMsg);
		}

		return super.beforeDelete();
	}
}
