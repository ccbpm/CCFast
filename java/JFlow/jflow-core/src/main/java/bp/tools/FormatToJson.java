package bp.tools;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import bp.da.DBAccess;
import bp.da.DataColumn;
import bp.da.DataRow;
import bp.da.DataRowCollection;
import bp.da.DataSet;
import bp.da.DataTable;
import bp.da.DataType;

public class FormatToJson {

	public final boolean getIsReusable() {
		return false;
	}

	/**
	 * 将JSON解析成DataSet只限标准的JSON数据 例如：Json＝{t1:[{name:'数据name',type:'数据type'}]} 或 Json＝{t1:[{name:
	 * '数据name',type:'数据type'}],t2:[{id:'数据id',gx:'数据gx',val:'数据val'}]}
	 *
	 * param Json Json字符串
	 * @return DataSet
	 * @throws Exception
	 */
	public static DataSet JsonToDataSet(String json) throws Exception {

		DataSet ds = new DataSet();
		JSONObject jsonObject = JSONObject.fromObject(json);
		@SuppressWarnings("rawtypes")
		Iterator it = jsonObject.keys();
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			DataTable dt = new DataTable(key);
			Object value = jsonObject.get(key);
			if (value instanceof JSONArray) {
				if (!value.toString().equals("[{}]")) {
					List<Map<String, Object>> jsonObjList = (List<Map<String, Object>>) FormatToJson.toList((JSONArray) value);
					for (Map<String, Object> map : jsonObjList) {
						DataRow dr = dt.NewRow();// 解析行
						for (Map.Entry<String, Object> entry : map.entrySet()) {
							DataColumn dc = new DataColumn(entry.getKey(), null, null);
							dr.setValue_UL(dc, entry.getValue());
							//							System.out.println(entry.getKey() + ": " + entry.getValue());
							int type = 0;
							for (DataColumn dcc : dt.Columns) {
								if (dcc.ColumnName.equals(entry.getKey())) {
									type = 1;
									break;
								}
							}
							if (type == 1) {
								continue;
							}
							dt.Columns.Add(dc);
						}
						dt.Rows.add(dr);
					}
				}
				ds.Tables.add(dt);
				ds.hashTables.put(key, dt);
			}
		}
		return ds;

	}

	/**
	 * Json 字符串 转换为 DataTable数据集合
	 *
	 * param json
	 * @return
	 */
	public static DataTable ToDataTable(String json) {
		try {
			DataTable dataTable = new DataTable();
			List<Map<String, Object>> arrayList = toList(json);
			if (arrayList.size() > 0) {
				for (Map<String, Object> dictionary : arrayList) {
					if (dataTable.Columns.size() == 0) {
						for (Map.Entry<String, Object> item : dictionary.entrySet()) {
							dataTable.Columns.Add(item.getKey(), item.getValue().getClass());
						}
					}
					DataRow dataRow = dataTable.NewRow();
					for (Map.Entry<String, Object> item : dictionary.entrySet()) {
						dataRow.setValue(item.getKey(), item.getValue());
					}
					dataTable.Rows.add(dataRow);
				}
			}
			return dataTable;
		} catch (Exception e) {
			return null;
		}
	}

	public FormatToJson() {}

	/**
	 * List转成json
	 *
	 * <typeparam name="T"></typeparam>
	 *
	 * param jsonName
	 * param list
	 * @return
	 */
	public static <T> String ListToJson(java.util.List<T> list, String jsonName) {
		StringBuilder Json = new StringBuilder();
		if (DataType.IsNullOrEmpty(jsonName)) {
			jsonName = list.get(0).getClass().getName();
		}
		Json.append("{\"" + jsonName + "\":");
		JSONArray jsonArray = JSONArray.fromObject(list);
		Json.append(jsonArray.toString());
		Json.append("}");
		return Json.toString();
	}

	/**
	 * List转成json
	 *
	 * <typeparam name="T"></typeparam>
	 *
	 * param list
	 * @return
	 */
	public static <T> String ListToJson(java.util.List<T> list) {
		Object obj = list.get(0);
		return ListToJson(list, obj.getClass().getName());
	}

	// /**
	// 对象转换为Json字符串
	//
	// param jsonObject 对象
	// @return Json字符串
	// */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String ToJson(Object jsonObject) {
		try {
			Class cla = Class.forName(jsonObject.getClass().getName());

			String separate = "";
			StringBuilder build = new StringBuilder("{");
			StringBuffer methodname = new StringBuffer();

			// 获取java类的变量
			Field[] fields = cla.getDeclaredFields();
			for (Field temp : fields) {
				build.append(separate);
				build.append("\"");
				build.append(temp.getName());
				build.append("\":");

				methodname.append("get");
				methodname.append(temp.getName().substring(0, 1).toUpperCase());
				methodname.append(temp.getName().substring(1));
				// 获取java的get方法
				Method method = cla.getMethod(methodname.toString());
				Object objectValue = method.invoke(jsonObject);
				String value = "";
				if (null != objectValue) {
					if (objectValue instanceof Iterable) {
						value = ToJson((Iterable) objectValue);
					} else {
						value = "\"" + ToJson(objectValue.toString()) + "\"";
					}
				} else {
					value = "\"\"";
				}
				build.append(value);
				methodname.setLength(0);
				separate = ",";
			}
			build.append("}");
			return build.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 对象集合转换Json
	 *
	 * param array 集合对象
	 * @return Json字符串
	 */
	@SuppressWarnings("rawtypes")
	public static String ToJson(Iterable array) {
		String separate = "";
		StringBuilder build = new StringBuilder("[");
		for (Object item : array) {
			build.append(separate);
			build.append(ToJson(item));
			separate = ",";
		}
		build.append("]");
		return build.toString();
	}

	/**
	 * 普通集合转换Json
	 *
	 * param array 集合对象
	 * @return Json字符串
	 */
	@SuppressWarnings("rawtypes")
	public static String ToArrayString(Iterable array) throws Exception {
		String separate = "";
		StringBuilder build = new StringBuilder("[");
		for (Object item : array) {
			build.append(separate);
			build.append(ToJson(item.toString()));
			separate = ",";
		}
		build.append("]");
		return build.toString();
	}

	/**
	 * Datatable转换为Json
	 *
	 * param table Datatable对象
	 * @return Json字符串
	 */
	public static String ToJson(DataTable dt) {
		StringBuilder jsonString = new StringBuilder();

		if (dt.Rows.size() == 0) {
			jsonString.append("[{}]");
			return jsonString.toString();
		}

		jsonString.append("[");
		DataRowCollection drc = dt.Rows;
		for (int i = 0; i < drc.size(); i++) {
			jsonString.append("{");
			for (int j = 0; j < dt.Columns.size(); j++) {
				String strKey = dt.Columns.get(j).ColumnName;
				String strValue = String.valueOf(drc.get(i).get(j));
				Object type = dt.Columns.get(j).DataType;
				jsonString.append("\"" + strKey + "\":");
				strValue = StringFormat(strValue, type);
				if (j < dt.Columns.size() - 1) {
					jsonString.append(strValue + ",");
				} else {
					jsonString.append(strValue);
				}
			}
			jsonString.append("},");
		}
		jsonString.deleteCharAt(jsonString.length() - 1);
		jsonString.append("]");
		return jsonString.toString();
	}
	/**
	 * Datatable转换为Json
	 *
	 * param table Datatable对象
	 * @return Json字符串
	 */
	public static String ToJsonUpper(DataTable dt) {
		StringBuilder jsonString = new StringBuilder();

		if (dt.Rows.size() == 0) {
			jsonString.append("[{}]");
			return jsonString.toString();
		}

		jsonString.append("[");
		DataRowCollection drc = dt.Rows;
		for (int i = 0; i < drc.size(); i++) {
			jsonString.append("{");
			for (int j = 0; j < dt.Columns.size(); j++) {
				String strKey = dt.Columns.get(j).ColumnName;
				String strValue = String.valueOf(drc.get(i).get(j));
				Object type = dt.Columns.get(j).DataType;
				jsonString.append("\"" + strKey.toUpperCase() + "\":");
				strValue = StringFormat(strValue, type);
				if (j < dt.Columns.size() - 1) {
					jsonString.append(strValue + ",");
				} else {
					jsonString.append(strValue);
				}
			}
			jsonString.append("},");
		}
		jsonString.deleteCharAt(jsonString.length() - 1);
		jsonString.append("]");
		return jsonString.toString();
	}
	/**
	 * DataTable转成Json
	 *
	 * param jsonName
	 * param dt
	 * @return
	 */
	public static String ToJson(DataTable dt, String jsonName) {
		StringBuilder Json = new StringBuilder();
		if (DataType.IsNullOrEmpty(jsonName)) {
			jsonName = dt.TableName;
		}
		Json.append("{\"" + jsonName + "\":[");
		if (dt.Rows.size() > 0) {
			for (int i = 0; i < dt.Rows.size(); i++) {
				Json.append("{");
				for (int j = 0; j < dt.Columns.size(); j++) {
					Object type = dt.Rows.get(i).get(j).getClass();
					Json.append("\"" + dt.Columns.get(j).ColumnName.toString() + "\":" + StringFormat(String.valueOf(dt.Rows.get(i).get(j)), type));
					if (j < dt.Columns.size() - 1) {
						Json.append(",");
					}
				}
				Json.append("}");
				if (i < dt.Rows.size() - 1) {
					Json.append(",");
				}
			}
		}
		Json.append("]}");
		return Json.toString();
	}

	/**
	 * DataSet转换为Json
	 *
	 * param dataSet DataSet对象
	 * @return Json字符串
	 */
	public static String ToJson(DataSet dataSet) {
		StringBuffer jsonString = new StringBuffer("{");

		switch (DBAccess.getAppCenterDBType()) {
		case MSSQL:
			for (int j = 0; j < dataSet.Tables.size(); j++) {
				DataTable table = dataSet.getTables().get(j);
				jsonString.append("\"" + table.TableName.toUpperCase() + "\":");
				jsonString.append("[");
				if (table.Rows.size() > 0) {
					for (int k = 0; k < table.Rows.size(); k++) {
						jsonString.append("{");
						for (int i = 0; i < table.Columns.size(); i++) {
							DataColumn dc = table.Columns.get(i);
							jsonString.append("\"" + dc.ColumnName.toUpperCase() + "\":");
							if (dc.DataType.toString().contains("String")) {
								if (table.Rows.get(k).getValue(dc) != null) {
									if (table.Rows.get(k).getValue(dc).toString().indexOf("\\") != -1) {
										jsonString.append("\"" + table.Rows.get(k).getValue(dc).toString().replaceAll("\\\\", "\\\\\\\\") + "\"");
									} else if (table.Rows.get(k).getValue(dc).toString().indexOf("\"") != -1) {
										jsonString.append("\"" + table.Rows.get(k).getValue(dc).toString().replaceAll("\"", "'") + "\"");
									} else {
										jsonString.append("\"" + table.Rows.get(k).getValue(dc) + "\"");
									}
								} else {
									jsonString.append("\"\"");
								}
							} else {
								if (table.Rows.get(k).getValue(dc) == null) {
									jsonString.append(0);
								} else {
									jsonString.append(table.Rows.get(k).getValue(dc));
								}
							}
							jsonString.append(",");
						}
						jsonString.deleteCharAt(jsonString.length() - 1);
						jsonString.append("},");
					}
					jsonString.deleteCharAt(jsonString.length() - 1);
				} else {
					jsonString.append("{}");
				}
				jsonString.append("],");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("}");
			break;
		case Oracle:
		case KingBaseR3:
		case KingBaseR6:
		case DM:
		case GBASE8CByOracle:
			for (int j = 0; j < dataSet.Tables.size(); j++) {
				DataTable table = dataSet.getTables().get(j);
				jsonString.append("\"" + table.TableName.toUpperCase() + "\":");
				jsonString.append("[");
				if (table.Rows.size() > 0) {
					for (int k = 0; k < table.Rows.size(); k++) {
						jsonString.append("{");
						for (int i = 0; i < table.Columns.size(); i++) {
							DataColumn dc = table.Columns.get(i);
							jsonString.append("\"" + dc.ColumnName.toUpperCase() + "\":");
							if (dc.DataType.toString().contains("String")) {
								if (table.Rows.get(k).getValue(dc) != null) {
									if (table.Rows.get(k).getValue(dc).toString().indexOf("\\") != -1) {
										jsonString.append("\"" + table.Rows.get(k).getValue(dc).toString().replaceAll("\\\\", "\\\\\\\\") + "\"");
									} else if (table.Rows.get(k).getValue(dc).toString().indexOf("\"") != -1) {
										jsonString.append("\"" + table.Rows.get(k).getValue(dc).toString().replaceAll("\"", "'") + "\"");
									} else {
										jsonString.append("\"" + table.Rows.get(k).getValue(dc) + "\"");
									}
								} else {
									jsonString.append("\"\"");
								}
							} else {
								if (table.Rows.get(k).getValue(dc) == null) {
									jsonString.append(0);
								} else {
									jsonString.append(table.Rows.get(k).getValue(dc));
								}
							}
							jsonString.append(",");
						}
						jsonString.deleteCharAt(jsonString.length() - 1);
						jsonString.append("},");
					}
					jsonString.deleteCharAt(jsonString.length() - 1);
				} else {
					jsonString.append("{}");
				}
				jsonString.append("],");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("}");
			break;
		case MySQL:
		case GBASE8A:
		case GBASE8CByMySQL:
			for (int j = 0; j < dataSet.Tables.size(); j++) {
				DataTable table = dataSet.getTables().get(j);
				jsonString.append("\"" + table.TableName.toUpperCase() + "\":");
				jsonString.append("[");
				if (table.Rows.size() > 0) {
					for (int k = 0; k < table.Rows.size(); k++) {
						jsonString.append("{");
						for (int i = 0; i < table.Columns.size(); i++) {
							DataColumn dc = table.Columns.get(i);
							jsonString.append("\"" + dc.ColumnName.toUpperCase() + "\":");
							if (dc.DataType.toString().contains("String")) {
								if (table.Rows.get(k).getValue(dc) != null) {
									if (table.Rows.get(k).getValue(dc).toString().indexOf("\\") != -1) {
										jsonString.append("\"" + table.Rows.get(k).getValue(dc).toString().replaceAll("\\\\", "\\\\\\\\") + "\"");
									} else if (table.Rows.get(k).getValue(dc).toString().indexOf("\"") != -1) {
										jsonString.append("\"" + table.Rows.get(k).getValue(dc).toString().replaceAll("\"", "'") + "\"");
									} else {
										jsonString.append("\"" + table.Rows.get(k).getValue(dc) + "\"");
									}
								} else {
									jsonString.append("\"\"");
								}
							} else {
								if (table.Rows.get(k).getValue(dc) == null) {
									jsonString.append(0);
								} else {
									jsonString.append(table.Rows.get(k).getValue(dc));
								}
							}
							jsonString.append(",");
						}
						jsonString.deleteCharAt(jsonString.length() - 1);
						jsonString.append("},");
					}
					jsonString.deleteCharAt(jsonString.length() - 1);
				} else {
					jsonString.append("{}");
				}
				jsonString.append("],");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("}");
			break;
		default:
			throw new RuntimeException("发现未知的数据库连接类型！");
		}


		return jsonString.toString();
	}

	/**
	 DataSet转换为Json

	 param dataSet DataSet对象
	 @return Json字符串
	*/
	public static String ToJson1(DataSet dataSet) {
		String jsonString = "{";
		for (DataTable table : dataSet.Tables) {
			jsonString += "\"" + table.TableName.toUpperCase() + "\":" + ToJson(table) + ",";
		}
		jsonString = StringHelper.trimEnd(jsonString, ',');
		return jsonString + "}";
	}


	/**
	 * String转换为Json
	 *
	 * param value String对象
	 * @return Json字符串
	 */
	public static String ToJson(String value) throws Exception {
		if (DataType.IsNullOrEmpty(value)) {
			return "";
		}

		String temstr;
		temstr = value;
		temstr = temstr.replace("{", "｛").replace("}", "｝").replace(":", "：").replace(",", "，").replace("[", "【").replace("]", "】").replace(";", "；")
				.replace("\n", "<br/>").replace("\r", "");

		temstr = temstr.replace("\t", "   ");
		temstr = temstr.replace("'", "\'");
		temstr = temstr.replace("\\", "\\\\");
		temstr = temstr.replace("\"", "\"\"");
		return temstr;
	}

	/**
	 * 过滤特殊字符
	 *
	 * param s
	 * @return
	 */
	public static String String2Json(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.toCharArray()[i];

			switch (c) {
			case '\"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '/':
				sb.append("\\/");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 格式化字符型、日期型、布尔型
	 *
	 * param str
	 * param type
	 * @return
	 */
	private static String StringFormat(String str, Object type) {
		if (type == String.class) {
			str = String2Json(str);
			str = "\"" + str + "\"";
		} else if (type == Date.class) {
			str = "\"" + DataType.stringToDate(str) + "\"";
		} else if (type == Boolean.class) {
			str = str.toLowerCase();
		}

		if (str.length() == 0) {
			str = "\"\"";
		}

		return str;
	}

	@SuppressWarnings("rawtypes")
	public static HashMap<String, Object> toHashMap(Object json) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		JSONObject jsonObject = JSONObject.fromObject(json);
		Iterator it = jsonObject.keys();
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			Object value = jsonObject.get(key);
			data.put(key, value);
		}
		return data;
	}

	@SuppressWarnings("rawtypes")
	public static List<Map<String, Object>> toList(Object json) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		JSONArray jsonArray = JSONArray.fromObject(json);
		for (Object obj : jsonArray) {
			JSONObject jsonObject = (JSONObject) obj;
			Map<String, Object> map = new HashMap<String, Object>();
			Iterator it = jsonObject.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = jsonObject.get(key);
				map.put((String) key, value);
			}
			list.add(map);
		}
		return list;
	}

	public static Object ParseFromJson(String szJson) throws Exception
	{
		com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

		Object entiy = mapper.readValue(szJson, Object.class);

		return entiy;
	}


	/**
	 *测试方法
	 *
	 */
	//	public static void main(String[] args)
	//	{
	//		String json = "{\"Sys_GroupField\":["
	//				+ "{\"OID\":107,\"Lab\":\"03.主从表基础功能\",\"EnName\":\"Demo_03\",\"Idx\":1,\"GUID\":\"\"},"
	//				+ "{\"OID\":109,\"Lab\":\"填充从表\",\"EnName\":\"Demo_03\",\"Idx\":2,\"GUID\":\"\"}],"
	//				+ "\"Sys_Enum\":["
	//				+ "{\"MyPK\":\"_CH_0\",\"Lab\":\"\",\"EnumKey\":\"\",\"IntKey\":0,\"Lang\":\"CH\"},"
	//				+ "{\"MyPK\":\"FYLX_CH_0\",\"Lab\":\"汽车票\",\"EnumKey\":\"FYLX\",\"IntKey\":0,\"Lang\":\"CH\"},"
	//				+ "{\"MyPK\":\"FYLX_CH_1\",\"Lab\":\"打的票\",\"EnumKey\":\"FYLX\",\"IntKey\":1,\"Lang\":\"CH\"},"
	//				+ "{\"MyPK\":\"FYLX_CH_2\",\"Lab\":\"火车票\",\"EnumKey\":\"FYLX\",\"IntKey\":2,\"Lang\":\"CH\"},"
	//				+ "{\"MyPK\":\"FYLX_CH_3\",\"Lab\":\"飞机票\",\"EnumKey\":\"FYLX\",\"IntKey\":3,\"Lang\":\"CH\"},"
	//				+ "{\"MyPK\":\"FYLX_CH_4\",\"Lab\":\"其它\",\"EnumKey\":\"FYLX\",\"IntKey\":4,\"Lang\":\"CH\"}]}";
	//		JSONObject jsonObject = JSONObject.fromObject(json);
	//		@SuppressWarnings("rawtypes")
	//		Iterator it = jsonObject.keys();
	//		while (it.hasNext())
	//		{
	//
	//			String key = String.valueOf(it.next());
	//			System.out.println(key);
	//			Object value = jsonObject.get(key);
	//			if (value instanceof JSONArray)
	//			{
	//				List<Map<String, Object>> jsonObjList = (List<Map<String, Object>>) FormatToJson
	//						.toList((JSONArray) value);
	//				for (Map<String, Object> map : jsonObjList)
	//				{
	//
	//					for (Map.Entry<String, Object> entry : map.entrySet())
	//					{
	//						System.out.println(entry.getKey() + ": " + entry.getValue());
	//					}
	//				}
	//			} else if (value instanceof JSONObject)
	//			{
	//				HashMap<String, Object> jsonObj = (HashMap<String, Object>) FormatToJson
	//						.toHashMap((JSONObject) value);
	//				for (Map.Entry<String, Object> entry : jsonObj.entrySet())
	//				{
	//					System.out.println(entry.getKey() + ": " + entry.getValue());
	//				}
	//			} else
	//			{
	//				System.out.println(value);
	//			}
	//		}
	//	}
}
