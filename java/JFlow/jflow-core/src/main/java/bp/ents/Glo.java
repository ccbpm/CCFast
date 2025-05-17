package bp.ents;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import bp.sys.*;
import bp.tools.StringUtils;
import com.alibaba.fastjson2.JSONObject;


public class Glo
{
	/**
	 是否存在

	 @param classID
	*/
	public static boolean IsExitMap(String classID)
	{
		return Cache.IsExitMapTS(classID);
	}
	public static Map SetMap(String classID, String mapData) throws Exception {
		if (mapData == null)
		{
			throw new RuntimeException("err@classID=[" + classID + "]的mapData不能为空.");
		}
		JSONObject json = JSONObject.parseObject(mapData);

		Map myMap = new Map();


			///#region 主表信息.
		myMap.setPhysicsTable(json.get("PhysicsTable").toString());
		myMap.setEnDesc(json.get("EnDesc").toString());
		myMap.setCodeStruct(json.get("CodeStruct").toString());
		myMap.ParaFields = json.get("ParaFields").toString();
		if (DataType.IsNullOrEmpty(myMap.ParaFields) == true)
		{
			myMap.ParaFields = null; //参数字段.
		}
		myMap.FJSavePath = json.get("FJSavePath").toString();
			///#endregion 主表信息.


			///#region 字段集合
		String attrs = json.get("attrs").toString();
		DataTable dtAttrs = bp.tools.Json.ToDataTable(attrs);
		for (DataRow dr : dtAttrs.Rows)
		{
			int myDataType = Integer.parseInt(dr.getValue("MyDataType").toString());
			FieldType myFieldType = FieldType.forValue(Integer.parseInt(dr.getValue("MyFieldType").toString()));

			Attr attr = new Attr();
			attr.setKey(dr.getValue("Key").toString());
			attr.setDesc(dr.getValue("Desc").toString());
			attr.setField(dr.getValue("Field").toString());
			attr.HelperUrl = dr.getValue("HelperUrl").toString();

			attr.setUIBindKey( dr.getValue("UIBindKey").toString());
			attr.setUIRefKeyText(dr.getValue("UIRefKeyText").toString());
			attr.setUIRefKeyValue( dr.getValue("UIRefKeyValue").toString());

			attr.UITag = dr.getValue("UITag").toString(); //枚举字段.

			attr.setItIsSupperText( Integer.parseInt(dr.getValue("IsSupperText").toString()));
			//.最小长度
			attr.setMinLength( Integer.parseInt(dr.getValue("MinLength").toString()));
			attr.setMaxLength(Integer.parseInt(dr.getValue("MaxLength").toString())); //.最大长度
			attr.setUIWidth(Integer.parseInt(dr.getValue("UIWidth").toString())); //.宽度.

			attr.setDefaultVal(dr.getValue("_defaultVal").toString()); //.默认值.

			if (dr.getValue("UIIsLine").toString().equals("false"))
			{
				attr.UIIsLine =false;
			}
			else
			{
				attr.UIIsLine =true;
			}

			attr.HelperUrl = dr.getValue("HelperUrl").toString();
			attr.setMyDataType( myDataType); //类型.
			attr.setMyFieldType( myFieldType);
			attr.setUIContralType(UIContralType.forValue(Integer.parseInt(dr.getValue("UIContralType").toString())));
			myMap.AddAttr(attr);
		}

			///#endregion 字段集合


			///#region 查询条件.

		//日期
		myMap.DTSearchKey = json.get("DTSearchKey").toString();
		myMap.DTSearchLabel = json.get("DTSearchLabel").toString();
		myMap.DTSearchWay = DTSearchWay.forValue(Integer.parseInt(json.get("DTSearchWay").toString()));

		//数值类型的范围.
		myMap.SearchFieldsOfNum = json.get("SearchFieldsOfNum").toString();

		//字段查询条件
		String fields = json.get("searchFields")==null?null:json.get("searchFields").toString();
		if(fields!=null){
			DataTable dt = bp.tools.Json.ToDataTable(fields);
			String searchFields="";
			for (DataRow dr : dt.Rows)
			{
				searchFields+="@"+dr.getValue("label").toString()+"="+dr.getValue("searchKey").toString();
			}
			myMap.SearchFields=searchFields;
		}
		//查询条件 - 枚举外键的集合.
		String ass = json.get("searchFKEnums").toString();
		DataTable dtAss = bp.tools.Json.ToDataTable(ass);
		for (DataRow dr : dtAss.Rows)
		{
			myMap.AddSearchAttr(dr.getValue("AttrKey").toString());
		}

		//查询条件 - 枚举外键的集合.
		ass = json.get("searchNormals").toString();
		DataTable dtNor = bp.tools.Json.ToDataTable(ass);
		for (DataRow dr : dtNor.Rows)
		{
			SearchNormal sn = new SearchNormal();
			sn.setKey(dr.getValue("Key").toString()); //  key || '';
			sn.setLab(dr.getValue("Lab").toString()); //  lab || '';
			sn.setRefAttrKey(dr.getValue("RefAttrKey").toString()); //  refAttr || '';
			sn.setDefaultSymbol(dr.getValue("DefaultSymbol").toString()); //  DefaultSymbol || '';
			sn.setDefaultVal(dr.getValue("DefaultVal").toString()); // defaultValue || '';
			sn.setTBWidth(Integer.parseInt(dr.getValue("TBWidth").toString())); // tbwidth || 120;
			sn.setItIsHidden(Boolean.parseBoolean(dr.getValue("IsHidden").toString())); // !!isHidden;
			myMap.getSearchNormals().Add(sn);
		}

			///#endregion 查询条件.

		String rms = json.get("rms").toString();
		DataTable dtrms = bp.tools.Json.ToDataTable(rms);
		for(DataRow dr : dtrms.Rows)
		{
			if ((int)dr.getValue("RefMethodType") == 6)
			{
				String classId = dr.getValue("RefDtlClsID").toString();
				String refDtlClsId = dr.getValue("RefDtlClsID").toString();
				if(StringUtils.isEmpty(classId) || StringUtils.isEmpty(refDtlClsId)) {
					continue;
				}
				myMap.AddDtl(dr.getValue("RefDtlClsID").toString(), dr.getValue("RefDtlRefPK").toString(),
						dr.getValue("GroupName").toString());
			}
		}

		return myMap;

		//移除该en的缓存
//		Cache.ClearSQL(classID);

	}
	/**
	 获得map的方法

	 @param tsClassID
	 @return
	*/
	public static Map GenerMap(String tsClassID)
	{
		if (tsClassID == null)
			throw new RuntimeException("err@错误：tsClassID 不能为null. ");
		String caseTsClassID = tsClassID;
		Map map = Cache.GetMapTS(caseTsClassID);
		if (map != null)
			return map;
		throw new RuntimeException("err@没有找到 " + tsClassID + " 的map。");
	}

}
