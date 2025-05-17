package bp.tools;

import bp.da.*;
import bp.difference.SystemConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.geom.Arc2D;
import java.awt.geom.Arc2D.Float;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class Json
{
	/**
	 * 对象转换为Json字符串
	 */
	public static String ToJson(Object jsonObject)
	{
		Gson gson = new GsonBuilder().registerTypeAdapterFactory(new TypeAdapterFactory() {

			public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
				Class<T> rawType = (Class<T>) typetoken.getRawType();
				if (rawType != String.class) {
					return null;
				}
				return (TypeAdapter<T>) new TypeAdapter<String>() {

					@Override
					public String read(JsonReader reader) throws IOException {
						if (reader.peek() == JsonToken.NULL) {
							reader.nextNull();
							return "";
						}
						return reader.nextString();
					}

					@Override
					public void write(JsonWriter writer, String value) throws IOException {
						if (value == null) {
							// writer.nullValue();
							writer.value("");
							return;
						}
						writer.value(value);
					}

				};
			}

		}).create();

		return gson.toJson(jsonObject);
	}

	/**
	 * 对象集合转换Json
	 */
	@SuppressWarnings("rawtypes")
	public static String ToJson(Iterable array)
	{ Gson gson = new GsonBuilder().registerTypeAdapterFactory(new TypeAdapterFactory() {

		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
			Class<T> rawType = (Class<T>) typetoken.getRawType();
			if (rawType != String.class) {
				return null;
			}
			return (TypeAdapter<T>) new TypeAdapter<String>() {

				@Override
				public String read(JsonReader reader) throws IOException {
					if (reader.peek() == JsonToken.NULL) {
						reader.nextNull();
						return "";
					}
					return reader.nextString();
				}

				@Override
				public void write(JsonWriter writer, String value) throws IOException {
					if (value == null) {
						// writer.nullValue();
						writer.value("");
						return;
					}
					writer.value(value);
				}

			};
		}

	}).create();

		String str= gson.toJson(array);

		return str;
	}

	/**
	 * 普通集合转换Json
	 */
	@SuppressWarnings("rawtypes")
	public static String ToArrayString(Iterable array)
	{
		JSONObject object = JSONObject.fromObject(array);
		return object.toString();
	}

	////一些三个注释掉的方法：尝试使用手写+gson的方式书写，但是方法不能覆盖所有object转json
	/**
	 * 对象转换为Json字符串
	 *//*
	public static String ToJson(Object jsonObject)
	{
		JSONObject object = JSONObject.fromObject(jsonObject);
		Gson gson =  new GsonBuilder().serializeNulls().create();
		System.out.println(object.toString());
		System.out.println(gson.toJson(jsonObject).replaceAll(":null",":\"\""));
		return gson.toJson(jsonObject).replaceAll(":null",":\"\"");
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
				if (null != objectValue && !"".equals(objectValue)) {
					if (objectValue instanceof Iterable) {
						value = ToJson((Iterable) objectValue);
					} else if(objectValue.toString().contains("BP.") || objectValue instanceof Class){
						//value = ToJson((Class) objectValue);
						Gson gson =  new GsonBuilder().serializeNulls().create();
						value = gson.toJson(value).replaceAll(":null",":\"\"");
					}
					else{
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

	*//**
 * 对象集合转换Json
 * param <E>
 *//*
	@SuppressWarnings("rawtypes")
	public static <E> String ToJson(Iterable<E> array)
	{
		JSONArray jsonArray = JSONArray.fromObject(array);
		return jsonArray.toString();
		//Gson gson =  new GsonBuilder().serializeNulls().create();
		//return gson.toJson(array).replaceAll(":null",":\"\"");
		String separate = "";
		StringBuilder build = new StringBuilder("[");
		for (Object item : array) {
			if(!StringUtils.isEmpty(item.getClass().getName()) && item.getClass().getName().contains("BP.")){
				build.append(separate);
				Gson gson =  new GsonBuilder().serializeNulls().create();
				build.append(gson.toJson(item).replaceAll(":null",":\"\""));
				separate = ",";
			}else{
				build.append(separate);
				build.append(ToJson(item.toString()));
				separate = ",";
			}

		}
		build.append("]");
		return build.toString();
	}

	*//**
 * 普通集合转换Json
 *//*
	@SuppressWarnings("rawtypes")
	public static String ToArrayString(Iterable array)
	{
		//JSONObject object = JSONObject.fromObject(jsonObject);
		//Gson gson = new Gson();
		//return gson.toJson(array);
		String separate = "";
		StringBuilder build = new StringBuilder("[");
		for (Object item : array) {
			build.append(separate);
			build.append(ToJson(item.toString()));
			separate = ",";
		}
		build.append("]");
		return build.toString();
	}*/

	/**
	 * 删除结尾字符
	 *
	 * param str
	 *            需要删除的字符
	 */
	private static String DeleteLast(String str)
	{
		if (str.length() > 1)
		{
			return str.substring(0, str.length() - 1);
		}
		return str;
	}


	/**
	 * 把Ht转换成Entity模式.
	 * param ht
	 * @return
	 */
	public static String ToJsonEntityModel(Hashtable ht)
	{
		String strs = "{";
		Enumeration enm = ht.keys();
		while(enm.hasMoreElements())
		{
			String key = (String)enm.nextElement();
			Object val = (Object)ht.get(key);
			if(val == null){
				strs += "\"" + key + "\":\"\",";
				continue;
			}
			if(val.getClass().equals(Integer.class)
					|| val.getClass().equals(Long.class)
					|| val.getClass().equals(Float.class)
					|| val.getClass().equals(Double.class)
					//@Todo 待验证 2023-02-15
					|| val.getClass().equals(BigDecimal.class)){
				strs += "\"" + key + "\":" + ht.get(key) + ",";
			}else{
				strs += "\"" + key + "\":\"" + ToJsonStr ( ht.get(key).toString() ) + "\",";
			}

		}

		strs += "\"OutEnd\":\"无效参数请忽略\"";
		strs += "}";

		return strs;

	}
	public static String ToJsonEntitiesNoNameModel(Hashtable ht) throws Exception {
		DataTable dt = new DataTable();
		dt.Columns.Add("No");
		dt.Columns.Add("Name");

		for (Object key : ht.keySet())
		{
			DataRow dr = dt.NewRow();
			dr.setValueStr("No", key+"");
			dr.setValueStr("Name",ht.get(key)+"");
			dt.Rows.add(dr);

		}

		return bp.tools.Json.DataTableToJson(dt, false);
	}

	/**
	 * Datatable转换为Json
	 *
	 * param table DataTable对象
	 */
	public static String ToJson(DataTable table)   {
		StringBuilder jsonString = new StringBuilder("[");
		//先给 oldName 给值.
		for (DataColumn column : table.Columns)
		{
			if (column.oldColumnName==null  )
				column.oldColumnName=column.ColumnName;
		}
		//定义线程大小,用多线程转换DataTable(row多时很耗时)
		ExecutorService executorService = Executors.newFixedThreadPool(5);

		// 使用lambda表达式提交任务
		List<Future<String>> futures = table.Rows.stream()
				.map(item -> executorService.submit(() -> dxc(table,item)))
				.collect(Collectors.toList());
		// 获取结果
		List<String> results = futures.stream()
				.map(future -> {
					try {
						return future.get(); // 阻塞获取每个任务的结果
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				})
				.collect(Collectors.toList());
		executorService.shutdown();
		try{
			executorService.awaitTermination(1, TimeUnit.DAYS);
		}catch (Exception e){
			e.printStackTrace();
		}
		results.stream().forEach(i->{jsonString.append(i);});
		return DeleteLast(jsonString.toString()) + "]";
	}
	//处理DataRow 转json
	private static String dxc(DataTable table,DataRow drc){
		StringBuilder jsonString = new StringBuilder("");
		jsonString.append("{");

		for (DataColumn column : table.Columns)
		{
			jsonString.append("\"" +  column.ColumnName + "\":");

			Object obj = drc.getValue(column.oldColumnName);

			if (DataType.IsNullOrEmpty(obj)==true)
			{
				obj = drc.getValue(column.ColumnName);	//解决构造tabele的问题.
				if (obj==null)
				{
					jsonString.append("\"\",");
					continue;
				}
			}
			if (column.DataType == Integer.class
					|| column.DataType == Long.class
					|| column.DataType == long.class
					|| column.DataType == Arc2D.Float.class
					|| column.DataType == java.lang.Float.class
					|| column.DataType == float.class
					|| column.DataType == Double.class
					|| column.DataType == double.class
			)
			{
				if(obj.equals(""))
					jsonString.append("\"\",");
				else

					//Long型测试 问题待定
					if( column.DataType == Long.class
							|| column.DataType == long.class)
						jsonString.append("\"" + obj.toString() + "\",");
					else
						jsonString.append("" + obj.toString() + ",");
				continue;
			}


			String str=obj.toString();
			if (str.equals("true") || str.equals("false"))
			{
				jsonString.append(str + ",");
				continue;
			}else{

				jsonString.append("\"" + ToJsonStr(str) + "\",");
				continue;
			}


		}
		jsonString = new StringBuilder(DeleteLast(jsonString.toString()) + "},");
		return jsonString.toString();
	}


	/**
	 * Datatable转换为Json  upper
	 *
	 * param table Datatable对象
	 */
	public static String ToJsonUpper(DataTable table)
	{
		StringBuilder jsonString = new StringBuilder("[");
		DataRowCollection drc = table.Rows;
		for (int i = 0; i < drc.size(); i++)
		{
			jsonString.append("{");
			for (DataColumn column : table.Columns)
			{
				jsonString.append(ToJson(column.ColumnName.toUpperCase()) + ":");
				Object obj = drc.get(i).getValue(column.ColumnName);
				if (column.DataType == java.util.Date.class
						|| column.DataType == String.class)
				{
					if (null != obj)
					{
						jsonString.append("\"" + ToJson(obj.toString()) + "\",");
					} else
					{
						jsonString.append("\"\",");
					}
				} else
				{
					if (null != obj)
					{
						jsonString.append(ToJson(obj.toString()) + ",");
					} else
					{
						jsonString.append(",");
					}
				}
			}
			jsonString = new StringBuilder(DeleteLast(jsonString.toString()) + "},");
		}
		return DeleteLast(jsonString.toString()) + "]";
	}

	/**
	 * DataSet转换为Json
	 *
	 * param dataSet DataSet对象
	 */
	public static String ToJson(DataSet dataSet)throws Exception
	{
		StringBuilder jsonString = new StringBuilder("{");
		for (DataTable table : dataSet.Tables)
		{
			if(null==table)
				continue;
			jsonString.append("\"" + table.TableName + "\":"
					+ ToJson(table) + ",");
		}

		return DeleteLast(jsonString.toString()) + "}";
	}

	/**
	 * String转换为Json
	 *
	 * param value
	 *            String对象
	 * @return Json字符串
	 */
	public static String ToJsonStr(String value)
	{
		if (DataType.IsNullOrEmpty(value))
			return "";

		StringBuffer sb = new StringBuffer();
		for (int i=0; i< value.length(); i++) {
			char c = value.charAt(i);
			switch (c){
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
			}
		}
		return sb.toString().trim();

	}

	/**
	 * 把一个json转化一个datatable
	 *
	 * param strJson 一个json字符串
	 * @return 序列化的datatable
	 */
	public static DataTable ToDataTable(String strJson) throws Exception {
		DataTable tb = new DataTable();
		DataRow row = null;
		DataColumn dc = null;
		String key = "";
		Object value  = null;
		//转换json格式
		JSONArray json = JSONArray.fromObject(strJson);
		tb = new DataTable();
		tb.TableName = "";
		int idx=0;
		for(int i = 0;i < json.size();i++) {
			JSONObject pjson = (JSONObject)json.get(i);
			@SuppressWarnings("unchecked")
			Iterator<String> iterator = pjson.keys();
			row = tb.NewRow();
			dc = new DataColumn();
			if(idx == 0){
				while(iterator.hasNext())
				{
					key = (String) iterator.next();
					dc.ColumnName = key;
					value = pjson.get(key);
					tb.Columns.Add(dc.ColumnName,value.getClass());
					row.setValue(key, value);
				}
			}
			idx++;
			while(iterator.hasNext())
			{
				key = (String) iterator.next();
				value = pjson.get(key);
				row.setValue(key, value);
			}
			tb.Rows.add(i, row);
		}
		return tb;
	}

	/**
	 * 转化成Json.
	 * param ht Hashtable
	 * param isNoNameFormat 是否编号名称格式
	 * @return
	 */
	public static String ToJson(Hashtable ht, boolean isNoNameFormat) throws Exception
	{
		if (isNoNameFormat)
		{
			/*如果是datatable 模式. */
			DataTable dt = new DataTable();
			dt.TableName = "HT"; //此表名不能修改.
			dt.Columns.Add(new DataColumn("No", String.class));
			dt.Columns.Add(new DataColumn("Name", String.class));
			for (Object key : ht.keySet())
			{
				if (key==null)
					continue;

				if (DataType.IsNullOrEmpty(key.toString()) ==true)
					continue;


				DataRow dr = dt.NewRow();
				dr.setValue("No", key);

				String v = (String)((ht.get(key) instanceof String) ? ht.get(key) : null);
				if (v == null)
				{
					v = "";
				}
				dr.setValue("Name", v);
				dt.Rows.add(dr);
			}
			return ToJson(dt);
		}

		String strs = "{";
		for (Object key : ht.keySet())
		{
			if(ht.get(key.toString())!=null)
				strs += "\"" + key.toString() + "\":\"" +  ToJsonStr(ht.get(key.toString()).toString()) + "\",";
			else
				strs += "\"" + key.toString() + "\":\"" +  ht.get(key.toString()) + "\",";
		}
		strs += "\"OutEnd\":\"1\"";
		strs += "}";


		return strs;
	}

	/**add by dgq**/
	public final static JSONObject GetObjectFromArrary_ByKeyValue(JSONArray jsOb,String Key, String value)throws Exception
	{if (jsOb.isArray() == true)
		{
			for (int i = 0, j = jsOb.size(); i < j; i++)
			{
				if (!jsOb.getJSONObject(i).containsKey(Key))
				{
					continue;
				}

				if (!jsOb.getJSONObject(i).isNullObject()&& jsOb.getJSONObject(i).get(Key).equals(value))
				{
					return jsOb.getJSONObject(i);
				}
			}
		}
		return null;
	}


	/**
	 Datatable转换为Json
	 param dt Datatable对象
	 @return isUpperColumn
	 */
	public static String DataTableToJson(DataTable dt, boolean isUpperColumn) throws Exception {
		StringBuilder jsonString = new StringBuilder();
		if (dt.Rows.size() == 0)
		{
			jsonString.append("[]");
			return jsonString.toString();
		}

		boolean isOracel=false;
		// 20210426大小写jhy
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
				/*SystemConfig.getAppCenterDBType() ==  DBType.Oracle
				||SystemConfig.getAppCenterDBType() == DBType.KingBaseR3
				||SystemConfig.getAppCenterDBType() == DBType.KingBaseR6*/
			isOracel=true;

		jsonString.append("[");
		DataRowCollection drc = dt.Rows;
		for (int i = 0; i < drc.size(); i++)
		{
			jsonString.append("{");
			for (int j = 0; j < dt.Columns.size(); j++)
			{
				String strKey = null;
				String strValue=null;

				if (isUpperColumn == true)
				{
					strKey = dt.Columns.get(j).ColumnName.toUpperCase();
					String strOld = dt.Columns.get(j).ColumnName;
					strValue = drc.get(i).get(strKey) == null ? (drc.get(i).get(strOld) == null ?"":drc.get(i).get(strOld).toString()) : drc.get(i).get(strKey).toString();
				}
				else
				{

					strKey = dt.Columns.get(j).ColumnName;

					if (isOracel==false)
						strValue = drc.get(i).get(strKey) == null ? "" : drc.get(i).get(strKey).toString();

					else
					{
						strValue = drc.get(i).get(strKey) == null ? "" : drc.get(i).get(strKey).toString();

						if (StringUtils.isEmpty(strValue))
							strValue=drc.get(i).get(strKey.toUpperCase()) == null ? "" : drc.get(i).get(strKey.toUpperCase()).toString();
					}

				}



				Object type = dt.Columns.get(j).getDataType();

				jsonString.append("\"" + strKey + "\":");
				strValue = StringFormat(strValue, type);

				if (j < dt.Columns.size() - 1)
				{
					jsonString.append( strValue + ",");
				}
				else
				{
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
	 格式化字符型、日期型、布尔型

	 param str
	 param type
	 @return
	 */
	private static String StringFormat(String str, Object type)
	{

		if (type == String.class)
		{
			str = String2Json(str);
			return "\"" + str + "\"";
		}


		if (type == java.util.Date.class)
		{
			return  "\"" + str + "\"";
		}

		if (type == Boolean.class)
		{
			return str.toLowerCase();
		}


		if (type == byte[].class)
		{
			//数字字段需转string后进行拼接 @于庆海 需要翻译
			return  "\"" + str + "\"";
		}


		if (str.length() == 0)
		{
			return  "\"\"";
		}

		return str;


		// return "\"" + str + "\"";


	}

	/**
	 过滤特殊字符

	 param s
	 @return
	 */
	private static String String2Json(String s)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.toCharArray()[i];

			switch (c)
			{
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
	 把一个json转化一个datatable 杨玉慧

	 param strJson 一个json字符串
	 @return 序列化的datatable
	 */
	public static DataTable ToDataTableOneRow(String strJson) throws Exception {
		////杨玉慧  写  用这个写
		if (strJson.trim().indexOf('[') != 0)
		{
			strJson = "[" + strJson + "]";
		}
		DataTable dtt = ToDataTable(strJson);

		return dtt;
	}

	/**
	 Datatable转换为Json

	 param dt Datatable对象
	 @return Json字符串
	 */
	public static String DataTableToJson(DataTable dt, boolean isUpperColumn, boolean isRowUper) throws Exception {
		StringBuilder jsonString = new StringBuilder();
		if (dt.Rows.size() == 0)
		{
			jsonString.append("[]");
			return jsonString.toString();
		}

		jsonString.append("[");
		DataRowCollection drc = dt.Rows;
		for (int i = 0; i < drc.size(); i++)
		{
			jsonString.append("{");
			for (int j = 0; j < dt.Columns.size(); j++)
			{
				String strKey = null;

				if (isUpperColumn == true)
				{
					strKey = dt.Columns.get(j).ColumnName.toUpperCase();
				}
				else
				{
					strKey = dt.Columns.get(j).ColumnName;
				}
				String strValue = "";
				if(!isRowUper){
					strValue = drc.get(i).get(dt.Columns.get(j).ColumnName) == null ? "" : drc.get(i).get(dt.Columns.get(j).ColumnName).toString();
				}else{
					strValue = drc.get(i).get(strKey) == null ? "" : drc.get(i).get(strKey).toString();
				}
				Object type = dt.Columns.get(j).getDataType();
				jsonString.append("\"" + strKey + "\":");
				strValue = StringFormat(strValue, type);
				if (j < dt.Columns.size() - 1)
				{
					jsonString.append(strValue + ",");
				}
				else
				{
					jsonString.append(strValue);
				}
			}
			jsonString.append("},");
		}
		jsonString.deleteCharAt(jsonString.length() - 1);
		jsonString.append("]");
		return jsonString.toString();
	}

	//appAce 传入大小写区分的cols，输出区分大消息的数据，当时oracle时，取值按照大写取值
	public static String DataTableToJson(DataTable dt, boolean isUpperColumn, boolean isRowUper,boolean appace) throws Exception {
		StringBuilder jsonString = new StringBuilder();
		if (dt.Rows.size() == 0)
		{
			jsonString.append("[]");
			return jsonString.toString();
		}

		jsonString.append("[");
		DataRowCollection drc = dt.Rows;
		for (int i = 0; i < drc.size(); i++)
		{
			jsonString.append("{");
			for (int j = 0; j < dt.Columns.size(); j++)
			{
				String strKey = null;

				if (isUpperColumn == true)
				{
					strKey = dt.Columns.get(j).ColumnName.toUpperCase();
				}
				else
				{
					strKey = dt.Columns.get(j).ColumnName;
				}
				String strValue = "";
				if(!isRowUper){
					if (SystemConfig.getAppCenterDBFieldCaseModel()== FieldCaseModel.UpperCase){//按照大写取值
						strValue = drc.get(i).get(strKey.toUpperCase()) == null ? "" : drc.get(i).get(strKey.toUpperCase()).toString();
					}else if(SystemConfig.getAppCenterDBFieldCaseModel()== FieldCaseModel.Lowercase){
						strValue = drc.get(i).get(strKey.toLowerCase()) == null ? "" : drc.get(i).get(strKey.toLowerCase()).toString();
					} else{
						strValue = drc.get(i).get(dt.Columns.get(j).ColumnName) == null ? "" : drc.get(i).get(dt.Columns.get(j).ColumnName).toString();
					}
				}else{
					strValue = drc.get(i).get(strKey) == null ? "" : drc.get(i).get(strKey).toString();
				}
				Object type = dt.Columns.get(j).getDataType();
				jsonString.append("\"" + strKey + "\":");
				strValue = StringFormat(strValue, type);
				if (j < dt.Columns.size() - 1)
				{
					jsonString.append(strValue + ",");
				}
				else
				{
					jsonString.append(strValue);
				}
			}
			jsonString.append("},");
		}
		jsonString.deleteCharAt(jsonString.length() - 1);
		jsonString.append("]");
		return jsonString.toString();
	}

	public static String ToJsonEntitiesNoNameMode(Hashtable ht) throws Exception {
		DataTable dt = new DataTable();
		dt.Columns.Add("No");
		dt.Columns.Add("Name");

		for (Object key : ht.keySet())
		{
			DataRow dr = dt.NewRow();
			dr.setValueStr("No", key+"");
			dr.setValueStr("Name",ht.get(key)+"");
			dt.Rows.add(dr);

		}

		return bp.tools.Json.DataTableToJson(dt, false);
	}

	public static String ToJson_object(Object table){
		return ToJson(table);
	}

	/// <summary>
	/// JSON字符串的转义
	/// </summary>
	/// <param name="jsonStr"></param>
	/// <returns></returns>
	private static String TranJsonStr(String jsonStr) {
		String strs = jsonStr;
		strs = strs.replace("\\", "\\\\");
		strs = strs.replace("\n", "\\n");
		strs = strs.replace("\b", "\\b");
		strs = strs.replace("\t", "\\t");
		strs = strs.replace("\f", "\\f");
		strs = strs.replace("\r", "\\r");
		strs = strs.replace("/", "\\/");
		strs = strs.replace("\"", "\"\"");
		strs = strs.replace("'", "\'");
		strs = strs.replace("\t", "   ");
		return strs;
	}
	/**
	 * Json转DataSet
	 * param json
	 * @return
	 */
	public static DataSet ToDataSet(String json) throws Exception {
		if(DataType.IsNullOrEmpty(json)==true)
			return null;
		JSONObject obj =  JSONObject.fromObject(json);
		DataSet ds = new DataSet();
		DataTable dt = null;
		DataRow row = null;
		DataColumn dc = null;
		String key = "";
		Object value  = "";
		//最外层的数据
		for (Object k : obj.keySet()){
			Object v = obj.get(k);
			//如果内层不是json数组，数据格式有错误，不能转成DataTable
			if(v instanceof JSONArray == false){
				Log.DebugWriteError("err@字符串["+json+"]转DataTable失败");
				throw new Exception("err@字符串转DataTable失败");
			}
			JSONArray array = (JSONArray)v;
			dt = new DataTable(k.toString());
			int idx=0;
			for(int i = 0;i < array.size();i++) {
				JSONObject pjson = (JSONObject)array.get(i);
				@SuppressWarnings("unchecked")
				Iterator<String> iterator = pjson.keys();
				row = dt.NewRow();
				dc = new DataColumn();
				if(idx == 0){
					while(iterator.hasNext())
					{
						key = (String) iterator.next();
						dc.ColumnName = key;
						value = pjson.get(key);
						dt.Columns.Add(dc.ColumnName,value.getClass());
						row.setValue(key, value);
					}
				}
				idx++;
				while(iterator.hasNext())
				{
					key = (String) iterator.next();
					value = pjson.get(key);
					row.setValue(key, value);
				}
				dt.Rows.add(i, row);
			}

			ds.Tables.add(dt);
		}
		return ds;
	}

	public static HashMap<String, String> JsonToHashMap(String JsonStrin){
		HashMap<String, String> data = new HashMap<String, String>();

		try{
			// 将json字符串转换成jsonObject
			JSONObject jsonObject = JSONObject.fromObject(JsonStrin);
			@SuppressWarnings("rawtypes")
			Iterator it = jsonObject.keys();
			// 遍历jsonObject数据，添加到Map对象
			while (it.hasNext())
			{
				String key = String.valueOf(it.next()).toString();
				String value = (String) jsonObject.get(key).toString();
				data.put(key, value);
			}
		}catch (Exception e) {
			e.printStackTrace();
			//JOptionPane.showMessageDialog(null,"ERROR:["+e+"]");
		}
		return data;
	}
	public static Hashtable<String, String> JsonToHashtable(String JsonStrin){
		HashMap<String, String> data =JsonToHashMap(JsonStrin);
		Hashtable<String, String> ht = new Hashtable<String, String>();

		ht.putAll(data);
		return ht;
	}
	public static DataTable ConvertToDataTable(JSONArray dataArr) throws Exception {
		if (dataArr == null || dataArr.size() <= 0)
			return null;
		DataTable dt = new DataTable();
		DataRow row = null;
		DataColumn dc = null;
		int idx=0;
		String key ="";
		Object value=null;
		for(int i = 0;i < dataArr.size();i++) {
			JSONObject pjson = (JSONObject)dataArr.get(i);
			@SuppressWarnings("unchecked")
			Iterator<String> iterator = pjson.keys();
			row = dt.NewRow();
			dc = new DataColumn();
			if(idx == 0){
				while(iterator.hasNext())
				{
					key = (String) iterator.next();
					dc.ColumnName = key;
					value = pjson.get(key);
					dt.Columns.Add(dc.ColumnName,value.getClass());
					row.setValue(key, value);
				}
			}
			idx++;
			while(iterator.hasNext())
			{
				key = (String) iterator.next();
				value = pjson.get(key);
				row.setValue(key, value);
			}
			dt.Rows.add(i, row);
		}
		return dt;
	}
}