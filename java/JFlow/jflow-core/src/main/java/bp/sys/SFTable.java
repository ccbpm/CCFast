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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;

/**
 用户自定义表
*/
public class SFTable extends EntityNoName
{

		///#region 数据源属性.
	/**
	 判断是否存在 @honyan.
	*/
	@Override
	public boolean getIsExits() throws Exception {
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			return super.getIsExits();
		}

		this.setNo(WebUser.getOrgNo() + "_" + this.getNo());
		return super.getIsExits();
	}


	/**
	 判断json 是否符合要求

	 @param jsonItem
	 @param fieldNo
	 @param fieldName
	 @param fieldParentNo
	 @exception Exception
	*/
	private void checkJsonField(JSONObject jsonItem, String fieldNo, String fieldName, String fieldParentNo) throws Exception {
		// 判断是否为数组嵌套
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(jsonItem.toString());
		if (jsonNode.getNodeType() != JsonNodeType.OBJECT)
		{
			throw new RuntimeException("API返回格式错误，不为标准json数组格式");
		}
		// 判断是否包含所匹配的项
		if (!DataType.IsNullOrEmpty(fieldNo) && !jsonItem.containsKey(fieldNo))
		{
			throw new RuntimeException("字典[" + this.getNo() + "]API不包含定义的No列：" + fieldNo);
		}
		if (!DataType.IsNullOrEmpty(fieldName) && !jsonItem.containsKey(fieldName))
		{
			throw new RuntimeException("字典[" + this.getNo() + "]API不包含定义的Name列：" + fieldName);
		}

		if (this.getCodeStruct() == CodeStruct.Tree)
		{
			if (!DataType.IsNullOrEmpty(fieldParentNo) && !jsonItem.containsKey(fieldParentNo))
			{
				throw new RuntimeException("字典[" + this.getNo() + "]API不包含定义的ParentNo列：" + fieldParentNo);
			}
		}
	}
	/**
	 过滤出新的json对象

	 @param source
	 @param fieldNo
	 @param fieldName
	 @param fieldParentNo
	 @return
	*/
	private JSONObject getJSONByTargetName(JSONObject source, String fieldNo, String fieldName, String fieldParentNo) throws Exception {
		JSONObject nObj = new JSONObject();
		if (!DataType.IsNullOrEmpty(fieldNo))
		{
			nObj.put("No", source.get(fieldNo));
		}

		if (!DataType.IsNullOrEmpty(fieldName))
		{
			nObj.put("Name", source.get(fieldName));
		}
		if (this.getCodeStruct() == CodeStruct.Tree)
		{
			if (!DataType.IsNullOrEmpty(fieldParentNo))
			{
				nObj.put("ParentNo", source.get(fieldParentNo));
			}
		}
		return nObj;
	}

	/**
	 获得外部数据表
	*/

	public final DataTable GenerHisDataTable() throws Exception {
		return GenerHisDataTable(null);
	}
	public final DataTable GenerHisDataTable(Hashtable ht) throws Exception {
		//创建数据源.
		SFDBSrc src = new SFDBSrc(this.getSFDBSrcNo());


			///#region BP类
		if (Objects.equals(this.getSrcType(), bp.sys.DictSrcType.BPClass))
		{
			Entities ens = ClassFactory.GetEns(this.getNo());
			return ens.RetrieveAllToTable();
		}

			///#endregion


			///#region  WebServices
		// this.SrcType == BP.Sys.SrcType.WebServices，by liuxc
		//暂只考虑No,Name结构的数据源，2015.10.04，added by liuxc
		if (Objects.equals(this.getSrcType(), DictSrcType.WebServices))
		{
			String[] td = this.getTableDesc().split("[,]", -1); //接口名称,返回类型
			String[] ps = (this.getSelectStatement() != null ? this.getSelectStatement() : "").split("[&]", -1);
			ArrayList args = new ArrayList();
			String[] pa = null;

			for (String p : ps)
			{
				if (DataType.IsNullOrEmpty(p))
				{
					continue;
				}

				pa = p.split("[=]", -1);
				if (pa.length != 2)
				{
					continue;
				}
				//此处要 SL 中显示表单时，会有问题
				try
				{
					if (pa[1].contains("@WebUser.No"))
					{
						pa[1] = pa[1].replace("@WebUser.No", WebUser.getNo());
					}
					if (pa[1].contains("@WebUser.Name"))
					{
						pa[1] = pa[1].replace("@WebUser.Name", WebUser.getName());
					}
					if (pa[1].contains("@WebUser.FK_DeptName"))
					{
						pa[1] = pa[1].replace("@WebUser.FK_DeptName", WebUser.getDeptName());
					}
					if (pa[1].contains("@WebUser.FK_Dept"))
					{
						pa[1] = pa[1].replace("@WebUser.FK_Dept", WebUser.getDeptNo());
					}
					if (pa[1].contains("@WebUser.DeptNo"))
					{
						pa[1] = pa[1].replace("@WebUser.DeptNo", WebUser.getDeptNo());
					}
				}
				catch (java.lang.Exception e)
				{
				}

				if (pa[1].contains("@WorkID"))
				{
					String tempVar = ContextHolderUtils.getRequest().getParameter("WorkID");
					pa[1] = pa[1].replace("@WorkID", tempVar != null ? tempVar : "");
				}
				if (pa[1].contains("@NodeID"))
				{
					String tempVar2 = ContextHolderUtils.getRequest().getParameter("NodeID");
					pa[1] = pa[1].replace("@NodeID", tempVar2 != null ? tempVar2 : "");
				}
				if (pa[1].contains("@FK_Node"))
				{
					String tempVar3 = ContextHolderUtils.getRequest().getParameter("FK_Node");
					pa[1] = pa[1].replace("@FK_Node", tempVar3 != null ? tempVar3 : "");
				}
				if (pa[1].contains("@FK_Flow"))
				{
					String tempVar4 = ContextHolderUtils.getRequest().getParameter("FK_Flow");
					pa[1] = pa[1].replace("@FK_Flow", tempVar4 != null ? tempVar4 : "");
				}
				if (pa[1].contains("@FID"))
				{
					String tempVar5 = ContextHolderUtils.getRequest().getParameter("FID");
					pa[1] = pa[1].replace("@FID", tempVar5 != null ? tempVar5 : "");
				}

				args.add(pa[1]);
			}

			Object result = InvokeWebService(src.getIP(), td[0], args.toArray(new Object[0]));

			if (DataType.IsNullOrEmpty(result instanceof String ? (String)result : null) == true)
			{
				throw new RuntimeException("err@获得结果错误.");
			}

			switch (td[1])
			{
				case "DataSet":
					return result == null ? new DataTable() : (result instanceof DataSet ? (DataSet)result : null).Tables.get(0);
				case "DataTable":
					return result instanceof DataTable ? (DataTable)result : null;
				case "Json":
					return bp.tools.Json.ToDataTable(result instanceof String ? (String)result : null);
				case "Xml":
					if (result == null || (DataType.IsNullOrEmpty(result.toString())))
					{
						throw new RuntimeException("@返回的XML格式字符串不正确。");
					}
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();

					// 解析XML文件
					Document doc = builder.parse(result.toString());

					Element root = doc.getDocumentElement();
					NodeList nodeList = root.getChildNodes();
					if (nodeList.getLength()< 2)
					{
						root = (Element)nodeList.item(0);
					}
					else
					{
						root = (Element)nodeList.item(1);
					}

					DataTable dt = new DataTable();
					dt.Columns.Add("No", String.class);
					dt.Columns.Add("Name", String.class);
					for (int i = 0; i < nodeList.getLength(); i++)
					{
						Node node = nodeList.item(i);
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							Element element = (Element) node;
							DataRow dr = dt.NewRow();
							dr.setValue("No", element.getNodeName());
							dr.setValue("Name", element.getTextContent());
							dt.Rows.add(dr);
						}

					}
					return dt;
				default:
					throw new RuntimeException("@不支持的返回类型" + td[1]);
			}
		}

			///#endregion


			///#region WebApi接口
		if (Objects.equals(this.getSrcType(), DictSrcType.WebApi))
		{
			String postData = bp.en.OverrideFile.Data_WebApi(ht, this.GetValStringByKey("RequestMethod"), this.getSFDBSrcNo(), this.getSelectStatement(), this.GetValStringByKey("PostDoc"), this.GetValStringByKey("Remark"));

			// 需要替换的参数
			String fieldNo = this.GetValStringByKey("FieldNo");
			if (DataType.IsNullOrEmpty(fieldNo))
			{
				fieldNo = "No";
			}

			String fieldName = this.GetValStringByKey("FieldName");
			if (DataType.IsNullOrEmpty(fieldName))
			{
				fieldName = "Name";
			}

			String fieldParentNo = this.GetValStringByKey("FieldParentNo");
			if (DataType.IsNullOrEmpty(fieldParentNo))
			{
				fieldParentNo = "ParentNo";
			}

			// 根节点
			String jsonNode = this.GetValStringByKey("JsonNode");
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jToken = objectMapper.readTree(postData);

			//JToken jToken = JToken.Parse(postData);
			// 如果是JSON数组
			if (jToken.getNodeType() == JsonNodeType.ARRAY)
			{
				// 新的对象，用来删除原对象无用字段
				JSONArray newJsonArr = new JSONArray();
				JSONArray arr = JSONArray.fromObject(postData);
				if (!arr.isEmpty())
				{
					JSONObject firstItem = arr.getJSONObject(0);
					checkJsonField(firstItem, fieldNo, fieldName, fieldParentNo);
				}
				for (int i = 0; i < arr.size(); i++){
					// 获取当前元素
					JSONObject obj = arr.getJSONObject(i);
					newJsonArr.add(getJSONByTargetName(obj, fieldNo, fieldName, fieldParentNo));
				}
				return bp.tools.Json.ConvertToDataTable(newJsonArr);
			}
			// 如果是JSON对象
			if (jToken.getNodeType() == JsonNodeType.OBJECT)
			{
				JSONObject jsonItem =JSONObject.fromObject(postData);
				// 判断是不是有根节点
				// 如果有
				if (!DataType.IsNullOrEmpty(jsonNode) && jsonItem.containsKey(jsonNode))
				{
					// 新的对象，用来删除原对象无用字段
					JSONArray newJsonArr = new JSONArray();
					ObjectMapper objectMapper1= new ObjectMapper();
					JsonNode jToken1 = objectMapper.readTree(jsonItem.getString(jsonNode));
					// 判断当前是不是数组或者
					if (jToken1.getNodeType() == JsonNodeType.ARRAY)
					{
						JSONArray arr = JSONArray.fromObject(jsonItem.getString(jsonNode));
						if (!arr.isEmpty())
						{
							JSONObject firstItem = arr.getJSONObject(0);
							checkJsonField(firstItem, fieldNo, fieldName, fieldParentNo);
						}

						for (int i = 0; i < arr.size(); i++){
							JSONObject obj = arr.getJSONObject(i);
							newJsonArr.add(getJSONByTargetName(obj, fieldNo, fieldName, fieldParentNo));
						}
						return bp.tools.Json.ToDataTable(newJsonArr.toString());
					}
					if (jToken1.getNodeType() == JsonNodeType.OBJECT)
					{
						JSONObject targetObj = JSONObject.fromObject(jsonItem.getString(jsonNode));
						checkJsonField(targetObj, fieldNo, fieldName, fieldParentNo);
						JSONObject itemOfRootNode = getJSONByTargetName(targetObj, fieldNo, fieldName, fieldParentNo);
						return bp.tools.Json.ToDataTable(itemOfRootNode.toString());
					}
					throw new RuntimeException("指定的RootNode下不是JSON数组 或 JSON对象");

				}
				// 如果没有配置rootNode，检查他本身有没有所需属性
				JSONObject currObj = JSONObject.fromObject(postData);
				checkJsonField(currObj, fieldNo, fieldName, fieldParentNo);
				JSONObject itemOfResponse = getJSONByTargetName(currObj, fieldNo, fieldName, fieldParentNo);
				return bp.tools.Json.ToDataTable(itemOfResponse.toString());
			}
			throw new RuntimeException("Web_API 没有正确返回JSON字符串");

				///#endregion
		}

			///#endregion WebApi接口


			///#region SQL查询.外键表/视图，edited by liuxc,2016-12-29
		if (Objects.equals(this.getSrcType(), DictSrcType.TableOrView))
		{
			String sql = "SELECT " + this.getColumnValue() + " No, " + this.getColumnText() + " Name";
			if (this.getCodeStruct() == bp.sys.CodeStruct.Tree)
			{
				sql += ", " + this.getParentValue() + " ParentNo";
			}

			sql += " FROM " + this.getSrcTable();
			DataTable dt = src.RunSQLReturnTable(sql);

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
		}

			///#endregion SQL查询.外键表/视图，edited by liuxc,2016-12-29


			///#region 动态SQL，edited by liuxc,2016-12-29
		if (Objects.equals(this.getSrcType(), DictSrcType.SQL))
		{
			String runObj = this.getSelectStatement();

			if (DataType.IsNullOrEmpty(runObj))
			{
				throw new RuntimeException("@外键类型SQL配置错误," + this.getNo() + " " + this.getName() + " 是一个(SQL)类型(" + this.GetValStrByKey("SrcType") + ")，但是没有配置sql.");
			}

			if (runObj == null)
			{
				runObj = "";
			}

			runObj = bp.difference.Glo.DealExp(runObj, ht);
			if (runObj.contains("@") == true)
			{
				String remark=this.GetValStringByKey("Remark");
				if(DataType.IsNullOrEmpty(remark)==false){
					AtPara atpara = new AtPara(remark);
					for(String key : atpara.getHisHT().keySet())
					{
						String alias = atpara.GetValStrByKey(key);
						String[] strs = alias.split(",");
						for(String str: strs)
						{
							if (DataType.IsNullOrEmpty(str) == true)
								continue;
							runObj = runObj.replace("@" + key, ht.get(key).toString());
						}
					}
				}

				if (runObj.contains("@") == true)
					throw new RuntimeException("@外键类型SQL错误," + runObj + "部分查询条件没有被替换.");
			}

			DataTable dt = null;
			try
			{
				dt = src.RunSQLReturnTable(runObj);
			}
			catch (RuntimeException ex)
			{
				throw new RuntimeException("err@获得SFTable(" + this.getNo() + "," + this.getName() + ")出现错误:SQL[" + runObj + "],数据库异常信息:" + ex.getMessage());
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
		}

			///#endregion


			///#region 自定义表.
		//if (this.SrcType == DictSrcType.CreateTable)
		//{
		//    String sql = "SELECT No, Name FROM " + this.No;
		//    DataTable dt = src.RunSQLReturnTable(sql);
		//    if (SystemConfig.AppCenterDBFieldCaseModel != FieldCaseModel.None)
		//    {
		//        dt.Columns.get(0).ColumnName = "No";
		//        dt.Columns.get(1).ColumnName = "Name";
		//        if (dt.Columns.size()== 3)
		//            dt.Columns.get(2).ColumnName = "ParentNo";
		//    }
		//    return dt;
		//}


			///#endregion


			///#region 内置字典表.
		if (Objects.equals(this.getSrcType(), DictSrcType.SysDict) || Objects.equals(this.getSrcType(), DictSrcType.CreateTable))
		{
			String sql = "";
			if (this.getCodeStruct() == CodeStruct.NoName)
			{
				sql = "SELECT BH AS No, Name FROM Sys_SFTableDtl WHERE FK_SFTable='" + this.getNo() + "'";
			}
			else
			{
				sql = "SELECT BH AS No, Name,ParentNo FROM Sys_SFTableDtl WHERE FK_SFTable='" + this.getNo() + "'";
			}
			DataTable dt = src.RunSQLReturnTable(sql);

			if (dt != null && dt.Rows.size() == 0)
			{
				if (this.getCodeStruct() == CodeStruct.NoName)
				{
					SFTableDtl dtl = new SFTableDtl();
					dtl.setBH("001");
					dtl.setName("Name 001");
					dtl.setMyPK(this.getNo() + "_" + dtl.getBH());
					dtl.setFKSFTable(this.getNo());
					dtl.Insert();

					dtl = new SFTableDtl();
					dtl.setBH("002");
					dtl.setName("Name 002");
					dtl.setMyPK(this.getNo() + "_" + dtl.getBH());
					dtl.setFKSFTable(this.getNo());
					dtl.Insert();

					dtl = new SFTableDtl();
					dtl.setBH("003");
					dtl.setName("Name 002");
					dtl.setMyPK(this.getNo() + "_" + dtl.getBH());
					dtl.setFKSFTable(this.getNo());
					dtl.Insert();
				}
				//如果是tree.
				if (this.getCodeStruct() == CodeStruct.Tree)
				{
					SFTableDtl dtl = new SFTableDtl();
					dtl.setBH("001");
					dtl.setName("根目录");
					dtl.setMyPK(this.getNo() + "_" + dtl.getBH());
					dtl.setFKSFTable(this.getNo());
					dtl.setParentNo("0"); // root 树结构编号.
					dtl.Insert();

					dtl = new SFTableDtl();
					dtl.setBH("002");
					dtl.setName("Node 001");
					dtl.setMyPK(this.getNo() + "_" + dtl.getBH());
					dtl.setFKSFTable(this.getNo());
					dtl.setParentNo("001");
					dtl.Insert();

					dtl = new SFTableDtl();
					dtl.setBH("003");
					dtl.setName("Node 002");
					dtl.setMyPK(this.getNo() + "_" + dtl.getBH());
					dtl.setFKSFTable(this.getNo());
					dtl.setParentNo("001");
					dtl.Insert();
				}
				dt = src.RunSQLReturnTable(sql);
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
		}

			///#endregion
		throw new RuntimeException("err@没有判断的类型." + this.getSrcTable());
	}
	public String SaveData(String no, String name, String fk_SFTable) throws Exception {
		return InsertData(no, name, fk_SFTable);
	}
	/**
	 修改外键数据

	 @return
	*/
	public final void UpdateData(String no, String name, String FK_SFTable) throws Exception {
		String sql = "";
		if (Objects.equals(this.getSrcType(), DictSrcType.SysDict))
		{
			sql = "UPDATE Sys_SFTableDtl SET Name = '" + name + "' WHERE MyPK='" + FK_SFTable + "_" + no + "'";
		}
		else
		{
			sql = "UPDATE " + FK_SFTable + " SET Name = '" + name + "' WHERE No = '" + no + "'";
		}
		DBAccess.RunSQL(sql);
	}
	/**
	 新增外键数据

	 @return
	*/
	public final String InsertData(String no, String name, String fk_SFTable) throws Exception {

		if (Objects.equals(this.getSrcType(), bp.sys.DictSrcType.SysDict))
		{
			String mypk = fk_SFTable + "_" + no;
			SFTableDtl dtl = new SFTableDtl();
			dtl.setMyPK(mypk);
			if (dtl.RetrieveFromDBSources() == 0)
			{
				dtl.setBH(no);
				dtl.setName(name);
				dtl.setFKSFTable(fk_SFTable);
				dtl.Insert();//插入.
			}
			else
			{
				dtl.setBH(no);
				dtl.setName(name);
				dtl.setFKSFTable(fk_SFTable);
				dtl.Update();//插入.
			}
			return "执行成功.";
			//  sql = "insert into  Sys_SFTableDtl(MyPK,FK_SFTable,BH,Name) values('" + FK_SFTable + "_" + No + "','" + FK_SFTable + "','" + No + "','" + Name + "')";
		}
		else
		{
			String sql = "SELECT No,Name FROM " + fk_SFTable + " WHERE No='" + no + "'";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
				sql = "insert into  " + fk_SFTable + "(No,Name) values('" + this.getNo() + "','" + this.getName() + "')";
			else
				sql = "UPDATE " + fk_SFTable + " SET Name = '" + this.getName() + "' WHERE No = '" + this.getNo() + "'";
			DBAccess.RunSQL(sql);
			return "执行成功.";

		}
	}

	/**
	 修改了名字.

	 @return
	*/
	public final String GenerJson() throws Exception {
		return bp.tools.Json.ToJson(this.GenerHisDataTable());
	}

	/**
	 自动生成编号

	 @return
	*/
	public final String GenerSFTableNewNo() throws Exception {
		String table = this.getSrcTable();
		NoGenerModel NoGenerModel = this.getNoGenerModel();
		if (NoGenerModel == NoGenerModel.ByGUID) //编号按guid生成
		{
			String guid = DBAccess.GenerGUID();
			return guid;
		}
		else if (NoGenerModel == NoGenerModel.ByLSH) //编号按流水号生成
		{
			if (Objects.equals(this.getSrcType(), DictSrcType.SysDict)) //如果是按系统字典表
			{
				try
				{
					String sql = null;
					String field = "BH";
					switch (this.getEnMap().getEnDBUrl().getDBType())
					{
						case MSSQL:
							sql = "SELECT CONVERT(INT, MAX(CAST(" + field + " as int)) )+1 AS No FROM Sys_SFTableDtl where FK_SFTable='" + table + "'";
							break;
						case PostgreSQL:
						case UX:
						case HGDB:
							sql = "SELECT to_number( MAX(" + field + ") ,'99999999')+1   FROM Sys_SFTableDtl where FK_SFTable='" + table + "'";
							break;
						case Oracle:
						case DM:
						case KingBaseR3:
						case KingBaseR6:
						case GBASE8CByOracle:
							sql = "SELECT MAX(" + field + ") +1 AS No FROM Sys_SFTableDtl where FK_SFTable='" + table + "'";
							break;
						case MySQL:
						case GBASE8A:
						case GBASE8CByMySQL:
							sql = "SELECT CONVERT(MAX(CAST(" + field + " AS SIGNED INTEGER)),SIGNED) +1 AS No FROM Sys_SFTableDtl where FK_SFTable='" + table + "'";
							break;
						case Informix:
							sql = "SELECT MAX(" + field + ") +1 AS No FROM Sys_SFTableDtl where FK_SFTable='" + table + "'";
							break;
						case Access:
							sql = "SELECT MAX( [" + field + "]) +1 AS  No FROM Sys_SFTableDtl where FK_SFTable='" + table + "'";
							break;
						default:
							throw new RuntimeException("error");
					}
					String str = String.valueOf(DBAccess.RunSQLReturnValInt(sql, 1));
					if (Objects.equals(str, "0") || Objects.equals(str, ""))
					{
						str = "1";
					}
					return StringHelper.padLeft(str, 3, '0');
				}
				catch (RuntimeException e)
				{
					return "";
				}
			}

			try
			{
				String sql = null;
				String field = "No";
				switch (this.getEnMap().getEnDBUrl().getDBType())
				{
					case MSSQL:
						sql = "SELECT CONVERT(INT, MAX(CAST(" + field + " as int)) )+1 AS No FROM " + table;
						break;
					case PostgreSQL:
					case UX:
					case HGDB:
						sql = "SELECT to_number( MAX(" + field + ") ,'99999999')+1   FROM " + table;
						break;
					case Oracle:
					case DM:
					case KingBaseR3:
					case KingBaseR6:
					case GBASE8CByOracle:
						sql = "SELECT MAX(" + field + ") +1 AS No FROM " + table;
						break;
					case MySQL:
					case GBASE8CByMySQL:
					case GBASE8A:
						sql = "SELECT CONVERT(MAX(CAST(" + field + " AS SIGNED INTEGER)),SIGNED) +1 AS No FROM " + table;
						break;
					case Informix:
						sql = "SELECT MAX(" + field + ") +1 AS No FROM " + table;
						break;
					case Access:
						sql = "SELECT MAX( [" + field + "]) +1 AS  No FROM " + table;
						break;
					default:
						throw new RuntimeException("error");
				}
				String str = String.valueOf(DBAccess.RunSQLReturnValInt(sql, 1));
				if (Objects.equals(str, "0") || Objects.equals(str, ""))
				{
					str = "1";
				}
				return StringHelper.padLeft(str, 3, '0');
			}
			catch (RuntimeException e2)
			{
				return "";
			}
		}
		else //其他的生成编号默认为空
		{
			return "";
		}
	}
	/**
	 实例化 WebServices

	 @param url WebServices地址
	 @param methodname 调用的方法
	 @param args 把webservices里需要的参数按顺序放到这个object[]里
	*/
	public final Object InvokeWebService(String url, String methodname, Object[] args)
	{
		return null;
	}


		///#region 链接到其他系统获取数据的属性
	/**
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStringByKey(SFTableAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(SFTableAttr.OrgNo, value);
	}
	/**
	 数据源
	*/
	public final String getSFDBSrcNo()  {
		return this.GetValStringByKey(SFTableAttr.FK_SFDBSrc);
	}
	public final void setSFDBSrcNo(String value){
		this.SetValByKey(SFTableAttr.FK_SFDBSrc, value);
	}
	public final String getSFDBSrcT()  {
		return this.GetValRefTextByKey(SFTableAttr.FK_SFDBSrc);
	}
	/**
	 数据缓存时间
	*/
	public final String getRootVal()  {
		return this.GetValStringByKey(SFTableAttr.RootVal);
	}
	public final void setRootVal(String value){
		this.SetValByKey(SFTableAttr.RootVal, value);
	}
	/**
	 同步间隔
	*/
	public final int getCacheMinute()  {
		return this.GetValIntByKey(SFTableAttr.CacheMinute);
	}
	public final void setCacheMinute(int value){
		this.SetValByKey(SFTableAttr.CacheMinute, value);
	}

	/**
	 物理表名称
	*/
	public final String getSrcTable() throws Exception {
		String str = this.GetValStringByKey(SFTableAttr.SrcTable);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			return this.getNo();
		}
		return str;
	}
	public final void setSrcTable(String value){
		this.SetValByKey(SFTableAttr.SrcTable, value);
	}
	/**
	 值/主键字段名
	*/
	public final String getColumnValue()  {
		return this.GetValStringByKey(SFTableAttr.ColumnValue);
	}
	public final void setColumnValue(String value){
		this.SetValByKey(SFTableAttr.ColumnValue, value);
	}
	/**
	 显示字段/显示字段名
	*/
	public final String getColumnText()  {
		return this.GetValStringByKey(SFTableAttr.ColumnText);
	}
	public final void setColumnText(String value){
		this.SetValByKey(SFTableAttr.ColumnText, value);
	}
	/**
	 父结点字段名
	*/
	public final String getParentValue()  {
		return this.GetValStringByKey(SFTableAttr.ParentValue);
	}
	public final void setParentValue(String value){
		this.SetValByKey(SFTableAttr.ParentValue, value);
	}
	/**
	 查询语句
	*/
	public final String getSelectStatement()  {
		return this.GetValStringByKey(SFTableAttr.SelectStatement);
	}
	public final void setSelectStatement(String value){
		this.SetValByKey(SFTableAttr.SelectStatement, value);
	}
	/**
	 加入日期
	*/
	public final String getRDT()  {
		return this.GetValStringByKey(SFTableAttr.RDT);
	}
	public final void setRDT(String value){
		this.SetValByKey(SFTableAttr.RDT, value);
	}

		///#endregion


		///#region 属性
	/**
	 是否是类
	*/
	public final boolean getItIsClass() throws Exception {
		if (this.getNo().contains("."))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 是否是树形实体?
	*/
	public final boolean getItIsTree() throws Exception {
		if (this.getCodeStruct() == bp.sys.CodeStruct.NoName)
		{
			return false;
		}
		return true;
	}
	/**
	 数据源类型
	*/
	public final String getSrcType() throws Exception {
		if (this.getNo().toUpperCase().contains("BP.") == true)
		{
			return DictSrcType.BPClass;
		}
		else
		{
			String src = this.GetValStringByKey(SFTableAttr.DictSrcType);
			if (src.equals(bp.sys.DictSrcType.BPClass))
			{
				return bp.sys.DictSrcType.CreateTable;
			}
			return src;
		}
	}
	public final void setSrcType(String value){
		this.SetValByKey(SFTableAttr.DictSrcType, value);
	}
	/**
	 数据源类型名称
	*/
	public final String getSrcTypeText() throws Exception {
		switch (this.getSrcType())
		{
			case DictSrcType.TableOrView:
				if (this.getItIsClass())
				{
					return "<img src='/WF/Img/Class.png' width='16px' broder='0' />实体类";
				}
				else
				{
					return "<img src='/WF/Img/Table.gif' width='16px' broder='0' />表/视图";
				}
			case DictSrcType.SQL:
				return "<img src='/WF/Img/SQL.png' width='16px' broder='0' />SQL表达式";
			case DictSrcType.WebServices:
				return "<img src='/WF/Img/WebServices.gif' width='16px' broder='0' />WebServices";
			case DictSrcType.WebApi:
				return "WebApi接口";
			default:
				return "";
		}
	}
	/**
	 字典表类型
	 <p>0：NoName类型</p>
	 <p>1：NoNameTree类型</p>
	 <p>2：NoName行政区划类型</p>
	*/
	public final CodeStruct getCodeStruct() {
		return CodeStruct.forValue(this.GetValIntByKey(SFTableAttr.CodeStruct));
	}
	public final void setCodeStruct(CodeStruct value){
		this.SetValByKey(SFTableAttr.CodeStruct, value.getValue());
	}
	/**
	编号生成规则
	*/
	public final NoGenerModel getNoGenerModel() {
		return NoGenerModel.forValue(this.GetValIntByKey(SFTableAttr.NoGenerModel));
	}
	public final void setNoGenerModel(NoGenerModel value){
		this.SetValByKey(SFTableAttr.NoGenerModel, value.getValue());
	}
	/**
	 编码类型
	*/
	public final String getCodeStructT()  {
		return this.GetValRefTextByKey(SFTableAttr.CodeStruct);
	}
	/**
	 值
	*/
	public final String getFKVal()  {
		return this.GetValStringByKey(SFTableAttr.FK_Val);
	}
	public final void setFKVal(String value){
		this.SetValByKey(SFTableAttr.FK_Val, value);
	}
	/**
	 描述
	*/
	public final String getTableDesc()  {
		return this.GetValStringByKey(SFTableAttr.TableDesc);
	}
	public final void setTableDesc(String value){
		this.SetValByKey(SFTableAttr.TableDesc, value);
	}
	/**
	 默认值
	*/
	public final String getDefVal()  {
		return this.GetValStringByKey(SFTableAttr.DefVal);
	}
	public final void setDefVal(String value){
		this.SetValByKey(SFTableAttr.DefVal, value);
	}
	public final EntitiesNoName getHisEns() throws Exception {
		if (this.getItIsClass())
		{
			EntitiesNoName ens = (EntitiesNoName)ClassFactory.GetEns(this.getNo());
			ens.RetrieveAll();
			return ens;
		}

		GENoNames ges = new GENoNames(this.getNo(), this.getName());
		ges.RetrieveAll();
		return ges;
	}

		///#endregion


		///#region 构造方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		//   uac.OpenForSysAdmin();
		uac.Readonly();
		return uac;
	}
	/**
	 用户自定义表
	*/
	public SFTable()
	{
	}
	public SFTable(String mypk) throws Exception {
		this.setNo(mypk);
		try
		{
			this.Retrieve();
		}
		catch (RuntimeException ex)
		{
			switch (this.getNo())
			{
				case "BP.Pub.NYs":
					this.setName("年月");
					this.setFKVal("FK_NY");
					this.Insert();
					break;
				case "BP.Pub.YFs":
					this.setName("月");
					this.setFKVal("FK_YF");
					this.Insert();
					break;
				case "BP.Pub.Days":
					this.setName("天");
					this.setFKVal("FK_Day");
					this.Insert();
					break;
				case "BP.Pub.NDs":
					this.setName("年");
					this.setFKVal("FK_ND");
					this.Insert();
					break;
				default:
					throw new RuntimeException(ex.getMessage());
			}
		}
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
		Map map = new Map("Sys_SFTable", "字典表");


		map.AddTBStringPK(SFTableAttr.No, null, "表英文名称", true, false, 1, 200, 20);
		map.AddTBString(SFTableAttr.Name, null, "表中文名称", true, false, 0, 200, 20);

		map.AddDDLStringEnum(SFTableAttr.DictSrcType, "BPClass", "数据表类型", SFTableAttr.DictSrcType, false);
		//map.AddDDLSysEnum(SFTableAttr.DictSrcType, 0, "数据表类型", true, false, SFTableAttr.DictSrcType);

		map.AddDDLSysEnum(SFTableAttr.CodeStruct, 0, "字典表类型", true, false, SFTableAttr.CodeStruct, "@0=编号名称类型@1=树结构类型");
		map.AddTBString(SFTableAttr.RootVal, null, "根节点值", false, false, 0, 200, 20);

		map.AddTBString(SFTableAttr.FK_Val, null, "默认创建的字段名", true, false, 0, 200, 20);
		map.AddTBString(SFTableAttr.TableDesc, null, "表描述", true, false, 0, 200, 20);
		map.AddTBString(SFTableAttr.DefVal, null, "默认值", true, false, 0, 200, 20);
		map.AddDDLSysEnum(SFTableAttr.NoGenerModel, 1, "编号生成规则", true, true, SFTableAttr.NoGenerModel, "@0=自定义@1=流水号@2=标签的全拼@3=标签的简拼@4=按GUID生成");
		//数据源.
		map.AddDDLEntities(SFTableAttr.FK_SFDBSrc, "local", "数据源", new bp.sys.SFDBSrcs(), true);

		map.AddTBString(SFTableAttr.SrcTable, null, "数据源表", false, false, 0, 200, 20);
		map.AddTBString(SFTableAttr.ColumnValue, null, "显示的值(编号列)", false, false, 0, 200, 20);
		map.AddTBString(SFTableAttr.ColumnText, null, "显示的文字(名称列)", false, false, 0, 200, 20);
		map.AddTBString(SFTableAttr.ParentValue, null, "父级值(父级列)", false, false, 0, 200, 20);
		map.AddTBString(SFTableAttr.SelectStatement, null, "查询语句", true, false, 0, 1000, 600, true);

		map.AddTBString("PostDoc", null, "Post内容", true, false, 0, 1000, 600, true);
		map.AddTBString("Remark", null, "参数别名", true, false, 0, 1000, 600, true);
		map.AddTBString("RequestMethod", "Get", "RequestMethod", true, false, 0, 100, 600, true);
		map.AddTBString("FieldNo", "", "FieldNo", true, false, 0, 200, 600, true);
		map.AddTBString("FieldName", "", "FieldName", true, false, 0, 200, 600, true);
		map.AddTBString("FieldParentNo", "", "FieldParentNo", true, false, 0, 200, 600, true);
		map.AddTBString("JsonNode", "", "根节点", true, false, 0, 200, 600, true);

		//是否有参数
		//map.AddTBInt("IsPara", 0, "IsPara", false, false);
		map.AddTBDateTime(SFTableAttr.RDT, null, "加入日期", false, false);
		map.AddTBString(SFTableAttr.OrgNo, null, "组织编号", false, false, 0, 100, 20);
		map.AddTBString(SFTableAttr.AtPara, null, "AtPara", false, false, 0, 50, 20);

		//查找.
		map.AddSearchAttr(SFTableAttr.FK_SFDBSrc);

		RefMethod rm = new RefMethod();
		rm.Title = "查看数据";
		rm.ClassMethodName = this.toString() + ".DoEdit";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ItIsForEns = false;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "修改属性";
		rm.ClassMethodName = this.toString() + ".DoAttr";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ItIsForEns = true;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "新建字典";
		rm.ClassMethodName = this.toString() + ".DoNew";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ItIsForEns = true;
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "创建Table向导";
		//rm.ClassMethodName = this.ToString() + ".DoGuide";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.ItIsForEns = false;
		//map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "数据源管理";
		//rm.ClassMethodName = this.ToString() + ".DoMangDBSrc";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.ItIsForEns = false;
		//map.AddRefMethod(rm);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 映射方法.
	public final String DoAttr() throws Exception {
		String projectName = ContextHolderUtils.getRequest().getServletPath();
		if (projectName.equals("WF/"))
		{
			projectName = "";
		}
		else
		{
			projectName = "/" + projectName;
		}
		return SystemConfig.getHostURLOfBS() + projectName + "/WF/Comm/EnOnly.htm?EnName=BP.Sys.SFTable&No=" + this.getNo();
	}
	public final String DoNew()
	{
		String projectName = ContextHolderUtils.getRequest().getServletPath();
		if (projectName.equals("WF/"))
		{
			projectName = "";
		}
		else
		{
			projectName = "/" + projectName;
		}
		return SystemConfig.getHostURLOfBS() + projectName + "/WF/Admin/FoolFormDesigner/SFTable/Default.htm?DoType=New&FromApp=SL&s=0.3256071044807922";
	}
	/**
	 数据源管理

	 @return
	*/
	public final String DoMangDBSrc()
	{
		return "../../Comm/Sys/SFDBSrcNewGuide.htm";
	}
	/**
	 创建表向导

	 @return
	*/
	public final String DoGuide()
	{
		return "../../Admin/FoolFormDesigner/CreateSFGuide.htm";
	}
	/**
	 编辑数据

	 @return
	*/
	public final String DoEdit() throws Exception {
		if (this.getItIsClass())
		{

			return "../../Comm/Ens.htm?EnsName=" + this.getNo();
		}
		else
		{
			if (this.GetValIntByKey(SFTableAttr.CodeStruct) == 0)
			{
				return "../../Admin/FoolFormDesigner/SFTableEditData.htm?FK_SFTable=" + this.getNo();
			}
			else
			{
				return "../../Admin/FoolFormDesigner/SFTableEditDataTree.htm?FK_SFTable=" + this.getNo();
			}
		}
	}

		///#endregion



		///#region 重写方法.
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
	@Override
	protected boolean beforeInsert() throws Exception
	{

		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.setOrgNo(WebUser.getOrgNo());
			this.setNo(this.getOrgNo() + "_" + this.getNo());
		}
		//利用这个时间串进行排序.
		this.setRDT(DataType.getCurrentDateTime());


			///#region 如果是本地类.
		if (Objects.equals(this.getSrcType(), bp.sys.DictSrcType.BPClass))
		{
			Entities ens = ClassFactory.GetEns(this.getNo());
			Entity en = ens.getNewEntity();
			this.setName(en.getEnDesc());

			//检查是否是树结构.
			if (en.getItIsTreeEntity() == true)
			{
				this.setCodeStruct(bp.sys.CodeStruct.Tree);
			}
			else
			{
				this.setCodeStruct(bp.sys.CodeStruct.NoName);
			}
		}

			///#endregion 如果是本地类.


			///#region 本地类，物理表..
		if (Objects.equals(this.getSrcType(), bp.sys.DictSrcType.CreateTable))
		{
			if (DBAccess.IsExitsObject(this.getNo()) == true)
			{
				return super.beforeInsert();
			}

			String sql = "";
			if (this.getCodeStruct() == bp.sys.CodeStruct.NoName || this.getCodeStruct() == bp.sys.CodeStruct.GradeNoName)
			{
				sql = "CREATE TABLE " + this.getNo() + " (";
				sql += "No varchar(30) NOT NULL,";
				sql += "Name varchar(3900) NULL";
				sql += ")";
			}

			if (this.getCodeStruct() == bp.sys.CodeStruct.Tree)
			{
				sql = "CREATE TABLE " + this.getNo() + " (";
				sql += "No varchar(30) NOT NULL,";
				sql += "Name varchar(3900)  NULL,";
				sql += "ParentNo varchar(3900)  NULL";
				sql += ")";
			}

			this.RunSQL(sql);

			//初始化数据.
			this.InitDataTable();
		}

			///#endregion 如果是本地类.

		return super.beforeInsert();
	}
	@Override
	protected void afterInsert() throws Exception
	{
		try
		{
			if (Objects.equals(this.getSrcType(), DictSrcType.TableOrView))
			{
				//暂时这样处理
				String sql = "CREATE VIEW " + this.getNo() + " (";
				sql += "[No],";
				sql += "[Name]";
				sql += (this.getCodeStruct() == bp.sys.CodeStruct.Tree ? ",[ParentNo])" : ")");
				sql += " AS ";
				sql += "SELECT " + this.getColumnValue() + " No," + this.getColumnText() + " Name" + (this.getCodeStruct() == bp.sys.CodeStruct.Tree ? ("," + this.getParentValue() + " ParentNo") : "") + " FROM " + this.getSrcTable() + (DataType.IsNullOrEmpty(this.getSelectStatement()) ? "" : (" WHERE " + this.getSelectStatement()));

				if (SystemConfig.getAppCenterDBType() == DBType.MySQL)
				{
					sql = sql.replace("[", "`").replace("]", "`");
				}
				else
				{
					sql = sql.replace("[", "").replace("]", "");
				}
				this.RunSQL(sql);
			}
		}
		catch (RuntimeException ex)
		{
			//创建视图失败时，删除此记录，并提示错误
			this.DirectDelete();
			throw ex;
		}
		super.afterInsert();
	}

		///#endregion 重写方法.


		///#region 执行方法.
	public final String GenerDataOfJsonFromWebApi(String paras) throws Exception {
		int isPara = this.GetValIntByKey("IsPara");
		//if (isPara == 0)
		//    return "err@无参字典，不能调用这个方法.";
		if (isPara == 1 && paras.contains("=") == false)
		{
			paras = "@Key=" + paras;
		}
		if (isPara == 2 && paras.contains("=") == false)
		{
			return "err@多个参字典,正确的格式为:@para1=val1@para2=val2.";
		}
		//把参数转化为 ht.
		SFParas ens = new SFParas();
		ens.Retrieve("RefPKVal", this.getNo());

		//获得ht.
		Hashtable ht = SFTable.GenerHT(paras, ens);
		DataTable dt = this.GenerHisDataTable(ht);
		return bp.tools.Json.ToJson(dt);
	}
	/**
	 根据参数获得json.

	 @param paras
	 @return
	*/
	public final String GenerJsonByPara(String paras) throws Exception {
		int isPara = this.GetValIntByKey("IsPara");
		//if (isPara == 0)
		//    return "err@无参字典，不能调用这个方法.";
		if (isPara == 1 && paras.contains("=") == false)
		{
			paras = "@Key=" + paras;
		}
		if (isPara == 2 && paras.contains("=") == false)
		{
			return "err@多个参字典,正确的格式为:@para1=val1@para2=val2.";
		}

		//把参数转化为 ht.
		SFParas ens = new SFParas();
		ens.Retrieve("RefPKVal", this.getNo());

		//获得ht.
		Hashtable ht = SFTable.GenerHT(paras, ens);
		try
		{
			DataTable dt = this.GenerHisDataTable(ht);
			String json = bp.tools.Json.ToJson(dt);
			return json;
		}
		catch (RuntimeException ex)
		{
			return ex.getMessage();
		}
	}

	public String TS_YuanShi_Data_WebApi_Test() throws Exception {
		SFDBSrc mysrc = new SFDBSrc(this.getSFDBSrcNo());
		String tsUrl = this.GetParaString("Test1");
		//string apiUrl = this.SelectStatement;
		String requestMethod = this.GetValStringByKey("RequestMethod");
		if (requestMethod.toUpperCase().equals("GET") == true)
		{
			tsUrl = mysrc.getConnString() + tsUrl;
			return bp.tools.HttpClientUtil.doGet(tsUrl);
		}
		if (tsUrl.startsWith("http") == false)
			tsUrl = mysrc.getConnString() + tsUrl;
		String paramData = this.GetParaString("Test2");
		if (DataType.IsNullOrEmpty(paramData))
			paramData = "";
		paramData = paramData.replace("~~", "\"");
		paramData = paramData.replace("~", "\"");
		return bp.tools.HttpClientUtil.doPostJson(tsUrl, paramData);

	}
	public String TS_YuanShi_Data_WebApi_CodeStruct_Test() throws Exception {
		String fieldNo = this.GetValStringByKey("FieldNo");
		if (DataType.IsNullOrEmpty(fieldNo))
			fieldNo = "No";

		String fieldName = this.GetValStringByKey("FieldName");
		if (DataType.IsNullOrEmpty(fieldName))
			fieldName = "Name";

		String fieldParentNo = this.GetValStringByKey("FieldParentNo");
		if (DataType.IsNullOrEmpty(fieldParentNo))
			fieldParentNo = "ParentNo";

		// 根节点
		String jsonNode = this.GetValStringByKey("JsonNode");
		String postData = this.TS_YuanShi_Data_WebApi_Test();
		// 根节点
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jToken = objectMapper.readTree(postData);

		//JToken jToken = JToken.Parse(postData);
		// 如果是JSON数组
		if (jToken.getNodeType() == JsonNodeType.ARRAY)
		{
			// 新的对象，用来删除原对象无用字段
			JSONArray newJsonArr = new JSONArray();
			JSONArray arr = JSONArray.fromObject(postData);
			if (!arr.isEmpty())
			{
				JSONObject firstItem = arr.getJSONObject(0);
				checkJsonField(firstItem, fieldNo, fieldName, fieldParentNo);
			}
			for (int i = 0; i < arr.size(); i++){
				// 获取当前元素
				JSONObject obj = arr.getJSONObject(i);
				newJsonArr.add(getJSONByTargetName(obj, fieldNo, fieldName, fieldParentNo));
			}
			return bp.tools.Json.ToJson(bp.tools.Json.ConvertToDataTable(newJsonArr));
		}
		// 如果是JSON对象
		if (jToken.getNodeType() == JsonNodeType.OBJECT)
		{
			JSONObject jsonItem =JSONObject.fromObject(postData);
			// 判断是不是有根节点
			// 如果有
			if (!DataType.IsNullOrEmpty(jsonNode) && jsonItem.containsKey(jsonNode))
			{
				// 新的对象，用来删除原对象无用字段
				JSONArray newJsonArr = new JSONArray();
				ObjectMapper objectMapper1= new ObjectMapper();
				JsonNode jToken1 = objectMapper.readTree(jsonItem.getString(jsonNode));
				// 判断当前是不是数组或者
				if (jToken1.getNodeType() == JsonNodeType.ARRAY)
				{
					JSONArray arr = JSONArray.fromObject(jsonItem.getString(jsonNode));
					if (!arr.isEmpty())
					{
						JSONObject firstItem = arr.getJSONObject(0);
						checkJsonField(firstItem, fieldNo, fieldName, fieldParentNo);
					}

					for (int i = 0; i < arr.size(); i++){
						JSONObject obj = arr.getJSONObject(i);
						newJsonArr.add(getJSONByTargetName(obj, fieldNo, fieldName, fieldParentNo));
					}
					return bp.tools.Json.ToJson(bp.tools.Json.ToDataTable(newJsonArr.toString()));
				}
				if (jToken1.getNodeType() == JsonNodeType.OBJECT)
				{
					JSONObject targetObj = JSONObject.fromObject(jsonItem.getString(jsonNode));
					checkJsonField(targetObj, fieldNo, fieldName, fieldParentNo);
					JSONObject itemOfRootNode = getJSONByTargetName(targetObj, fieldNo, fieldName, fieldParentNo);
					return bp.tools.Json.ToJson(bp.tools.Json.ToDataTable(itemOfRootNode.toString()));
				}
				throw new RuntimeException("指定的RootNode下不是JSON数组 或 JSON对象");

			}
			// 如果没有配置rootNode，检查他本身有没有所需属性
			JSONObject currObj = JSONObject.fromObject(postData);
			checkJsonField(currObj, fieldNo, fieldName, fieldParentNo);
			JSONObject itemOfResponse = getJSONByTargetName(currObj, fieldNo, fieldName, fieldParentNo);
			return bp.tools.Json.ToJson(bp.tools.Json.ToDataTable(itemOfResponse.toString()));
		}

		return "";
	}
	/**
	 获得原始的数据

	 @return
	*/
	public final String TS_YuanShi_Data_WebApi() throws Exception {
		return bp.en.OverrideFile.Data_WebApi(null, this.GetValStringByKey("RequestMethod"), this.getSFDBSrcNo(), this.getSelectStatement(), this.GetValStringByKey("PostDoc"), this.GetValStringByKey("Remark"));
	}
	/**
	 获得原始的数据

	 @param paras
	 @return
	*/
	public final String TS_YuanShi_Data_WebApi_Para(String paras) throws Exception {
		//把参数转化为ht.
		Hashtable ht = DataType.ParseParasToHT(paras);

		//获取参数.
		SFParas ens = new SFParas();
		ens.Retrieve("RefPKVal", this.getNo());
		//遍历它.
		for (SFPara item : ens.ToJavaList())
		{
			//内部参数.
			if (item.getItIsSys() == 0)
			{
				if (ht.containsKey(item.getParaKey()) == false)
				{
					ht.put(item.getParaKey(), item.getExpVal());
				}
				continue;
			}

			//如果是外部参数.
			if (ht.containsKey(item.getParaKey()) == true)
			{
				continue;
			}

			String key = "";
			if (ht.containsKey("Key") == true)
			{
				key = ht.get("Key").toString();
			}
			ht.put(item.getParaKey(), key);
		}
		return bp.en.OverrideFile.Data_WebApi(ht, this.GetValStringByKey("RequestMethod"), this.getSFDBSrcNo(), this.getSelectStatement(), this.GetValStringByKey("PostDoc"), this.GetValStringByKey("Remark"));
	}
	/**
	 返回json.

	 @return
	*/
	public final String GenerDataOfJson() throws Exception {
		DataTable dt = this.GenerHisDataTable();
		String json = bp.tools.Json.ToJson(dt);
		return json;
	}
	/**
	 初始化数据.
	*/
	public final void InitDataTable() throws Exception {
		DataTable dt = this.GenerHisDataTable();

		String sql = "";
		if (dt.Rows.size() == 0)
		{
			/*初始化数据.*/
			if (this.getCodeStruct() == bp.sys.CodeStruct.Tree)
			{
				sql = "INSERT INTO " + this.getSrcTable() + " (No,Name,ParentNo) VALUES('1','" + this.getName() + "','0') ";
				this.RunSQL(sql);

				for (int i = 1; i < 4; i++)
				{
					String no = String.valueOf(i);
					no = StringHelper.padLeft(no, 3, '0');

					sql = "INSERT INTO " + this.getSrcTable() + " (No,Name,ParentNo) VALUES('" + no + "','Item" + no + "','1') ";
					this.RunSQL(sql);
				}
			}

			if (this.getCodeStruct() == bp.sys.CodeStruct.NoName)
			{
				for (int i = 1; i < 4; i++)
				{
					String no = String.valueOf(i);
					no = StringHelper.padLeft(no, 3, '0');
					sql = "INSERT INTO " + this.getSrcTable() + " (No,Name) VALUES('" + no + "','Item" + no + "') ";
					this.RunSQL(sql);
				}
			}
		}
	}

		///#endregion 执行方法.

	/**
	 获得数据

	 @param paras @Key=xxx@xxx=xxx
	 @return
	*/
	public final String Vue3_GenerJsonByParas(String paras) throws Exception {
		//获取参数,.
		SFParas ens = new SFParas();
		ens.Retrieve("RefPK", this.getNo());

		//通过公共的方法生成参数.
		Hashtable ht = SFTable.GenerHT(paras, ens);

		DataTable dt = this.GenerHisDataTable(ht);
		return bp.tools.Json.ToJson(dt);
	}

	public static Hashtable GenerHT(String paras, SFParas ens) throws Exception {
		//把参数转化为ht.
		Hashtable ht = DataType.ParseParasToHT(paras);

		//遍历它.
		for (SFPara item : ens.ToJavaList())
		{
			//内部参数.
			if (item.getItIsSys() == 0)
			{
				if (ht.containsKey(item.getParaKey()) == false)
				{
					ht.put(item.getParaKey(), item.getExpVal());
				}
				continue;
			}

			//如果是外部参数.
			if (ht.containsKey(item.getParaKey()) == true)
			{
				continue;
			}

			String key = "";
			if (ht.containsKey("Key") == true)
			{
				key = ht.get("Key").toString();
			}
			ht.put(item.getParaKey(), key);
		}
		return ht;
	}
}
