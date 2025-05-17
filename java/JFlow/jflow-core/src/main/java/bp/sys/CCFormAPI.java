package bp.sys;

import bp.difference.StringHelper;
import bp.en.*;
import bp.da.*;
import bp.tools.JflowThreadPoolTaskExecutor;
import bp.tools.Json;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 表单API
*/
public class CCFormAPI
{

	///region 智能判断字段类型.
	//3-float  2-int 4-boolen
	public static String fieldNameKeys = "[{\"regName\":\"地点|场所|位置\",\"dataType\":\"1\"}," +
			"{\"regName\":\"费用|费|计|金额|额|值|价|金\",\"dataType\":\"8\"}," +
			"{\"regName\":\"日期\",\"dataType\":\"6\"}," +
			"{\"regName\":\"时间\",\"dataType\":\"7\"}," +
			"{\"regName\":\"长|宽|高|公斤|斤|率|重|公里|里程|千克\",\"dataType\":\"3\"}," +
			"{\"regName\":\"个|量|数|台|张|分数|分|只|件|把|张|条|双|瓶|盒|袋|堆|群|串|溜|阵|批|幅|截|缕|抹|丝|毫|点|滴|片|粒|颗|瓣|丛|簇\",\"dataType\":\"2\"}," +
			"{\"regName\":\"否|是|是否\",\"dataType\":\"4\"}]";

	// //后置提示endLab图标let icon提示tip;
	public static String fieldEndLabs = "[{\"unitEndLabName\":\"KG\",\"regName\":\"体重|重量|重\",\"icon\":\"icon-user\",\"tip\":\"\"}," +
			"{\"unitEndLabName\":\"CM\",\"regName\":\"身高|身长|尺寸|长|宽|高\",\"icon\":\"\",\"tip\":\"\"}," +
			"{\"unitEndLabName\":\"公里\",\"regName\":\"距离|里程|路程\",\"icon\":\"\",\"tip\":\"请输入公里数\"}," +
			"{\"unitEndLabName\":\"周岁\",\"regName\":\"龄|年纪\",\"icon\":\"\",\"tip\":\"请输入年龄\"}," +
			"{\"unitEndLabName\":\"码\",\"regName\":\"速度|布长|鞋码\",\"icon\":\"\",\"tip\":\"\"}," +
			"{\"unitEndLabName\":\"ML\",\"regName\":\"容积\",\"icon\":\"\",\"tip\":\"\"}," +
			"{\"unitEndLabName\":\"%\",\"regName\":\"率\",\"icon\":\"\",\"tip\":\"\"}," +
			"{\"unitEndLabName\":\"\",\"regName\":\"备注\",\"icon\":\"icon-book-open\",\"tip\":\"\"}," +
			"{\"unitEndLabName\":\"\",\"regName\":\"邮件|email|电子邮件\",\"icon\":\"icon-envelope\",\"tip\":\"\"}," +
			"{\"unitEndLabName\":\"\",\"regName\":\"电话|手机|联系方式\",\"icon\":\"icon-envelope\",\"tip\":\"\"}," +
			"{\"unitEndLabName\":\"\",\"regName\":\"网站|域名|链接|主页|博客\",\"icon\":\"icon-link\",\"tip\":\"http://ccflow.org\"}]";
	/**
	 * 根据中文名称返回字段类型
	 * @param arrayListName
	 * @return
	 */
	public static String GenerDataTypeByCHName(List<String> arrayListName) throws Exception {

		MapAttrs mapAttrs = new MapAttrs();
		String result = String.join(",", arrayListName);
		DataTable dt = new DataTable();
		if (!DataType.IsNullOrEmpty(result)) {
			QueryObject qo = new QueryObject(mapAttrs);
			qo.AddWhereIn("Name", "(" + bp.port.Glo.GenerWhereInSQL(String.join(",", result)) + ")");
			dt = qo.DoQueryToTable();
		}

		DataTable dtFieldNameKeys = Json.ToDataTable(fieldNameKeys);
		List<Hashtable> list = new ArrayList<>();
		try
		{
			for (int i = 0; i < arrayListName.size(); i++)
			{
				String name = arrayListName.get(i);

				if (DataType.IsNullOrEmpty(name)) {
					continue;
				}
				DataRow[] datas = dt.Select("Name='" + name + "'");
				if (datas.length > 0)
				{
					Hashtable hashtable = new Hashtable();
					hashtable.put("name", name);
					hashtable.put("dataType", datas[0].getValue("MyDataType").toString());
					list.add(hashtable);
				}
				else {
					for (DataRow drKey : dtFieldNameKeys.Rows)
					{
						String regex = "";
						try
						{
							regex = drKey.getValue("regName").toString();
						}
						catch (Exception ex)
						{
							Log.DebugWriteError(ex);
						}
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(arrayListName.get(i));
						if (matcher.find())
						{
							Hashtable<String, Object> hashtable = new Hashtable<>();
							hashtable.put("name", name);
							hashtable.put("dataType", drKey.getValue("dataType"));
							list.add(hashtable);
							break;
						}
					}
				}

			}
		}
		catch (Exception ex)
		{
			Log.DebugWriteError(ex);
			return Json.ToJson(list);
		}
		return Json.ToJson(list);
	}

	/**
	 * 根据中文名称返回后缀提示
	 * @param arrayListName
	 * @return
	 */
	public static String GenerFildEndInfoByCHName(ArrayList<String> arrayListName) throws Exception {

		DataTable dtFieldEndLabs = Json.ToDataTable(fieldEndLabs);
		List<Hashtable<String, Object>> list = new ArrayList<>();
		try
		{
			for (int i = 0; i < arrayListName.size(); i++)
			{
				String name = arrayListName.get(i);

				for (DataRow dr : dtFieldEndLabs.Rows)
				{
					String regex = "";
					try
					{
						regex = dr.getValue("regName").toString();
					}
					catch (Exception ex)
					{
						Log.DebugWriteError(ex);
					}
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(arrayListName.get(i));
					if (matcher.find())
					{
						Hashtable<String, Object> hashtable = new Hashtable<>();
						hashtable.put("name", name);
						hashtable.put("unitEndLabName", dr.getValue("unitEndLabName"));
						hashtable.put("icon", dr.getValue("icon"));
						hashtable.put("tip", dr.getValue("tip"));
						list.add(hashtable);
						break;
					}
				}
			}
		}
		catch (Exception ex)
		{
			Log.DebugWriteError(ex);
			return Json.ToJson(list);
		}

		return Json.ToJson(list);
	}
	//#endregion 智能判断字段类型.

		///#region 创建修改字段.
	/**
	 创建通用组件入口

	 @param fk_mapdata 表单ID
	 @param ctrlType 控件类型
	 @param no 编号
	 @param name 名称
	 @param x 位置x
	 @param y 位置y
	*/
	public static void CreatePublicNoNameCtrl(String fk_mapdata, String ctrlType, String no, String name, float x, float y) throws Exception {
		switch (ctrlType)
		{
			case "Dtl":
				CreateOrSaveDtl(fk_mapdata, no, name);
				break;
			case "AthMulti":
				CreateOrSaveAthMulti(fk_mapdata, no, name);
				break;
			case "AthSingle":
				CreateOrSaveAthSingle(fk_mapdata, no, name, x, y);
				break;
			case "AthImg":
				CreateOrSaveAthImg(fk_mapdata, no, name, x, y);
				break;
			case "iFrame": //框架.
				MapFrame mapFrame = new MapFrame();
				mapFrame.setMyPK(fk_mapdata + "_" + no);
				if (mapFrame.RetrieveFromDBSources() != 0)
				{
					throw new RuntimeException("@创建失败，已经有同名元素[" + no + "]的控件.");
				}
				mapFrame.setFrmID(fk_mapdata);
				mapFrame.setEleType("iFrame");
				mapFrame.setName(name);
				mapFrame.setFrmID(no);
				mapFrame.setURL("http://ccbpm.cn");

				mapFrame.setW(400);
				mapFrame.setH(600);
				mapFrame.Insert();
				break;

			case "HandSiganture": //签字版
				//检查是否可以创建字段?
				MapData md = new MapData(fk_mapdata);
				md.CheckPTableSaveModel(no);

				MapAttr ma = new MapAttr();
				ma.setFrmID(fk_mapdata);
				ma.setKeyOfEn(no);
				ma.setName(name);
				ma.setMyDataType(DataType.AppString);
				ma.setUIContralType(UIContralType.HandWriting);

				//frmID设置字段所属的分组
				GroupField groupField = new GroupField();
				groupField.Retrieve(GroupFieldAttr.FrmID, fk_mapdata, GroupFieldAttr.CtrlType, "");
				ma.setGroupID(groupField.getOID());
				ma.Insert();
				break;
			default:
				throw new RuntimeException("@没有判断的存储控件:" + ctrlType + ",存储该控件前,需要做判断.");
		}
	}
	/**
	 创建/修改-图片附件

	 @param fk_mapdata 表单ID
	 @param no 明细表编号
	 @param name 名称
	 @param x 位置x
	 @param y 位置y
	*/
	public static void CreateOrSaveAthImg(String fk_mapdata, String no, String name, float x, float y) throws Exception {
		no = no.trim();
		name = name.trim();

		FrmImgAth ath = new FrmImgAth();
		ath.setFrmID(fk_mapdata);
		ath.setCtrlID(no);
		ath.setMyPK(fk_mapdata + "_" + no);

		//ath.X = x;
		//ath.Y = y;
		ath.Insert();
	}
	/**
	 创建/修改-多附件

	 @param fk_mapdata 表单ID
	 @param no 明细表编号
	 @param name 名称
	 @param x 位置x
	 @param y 位置y
	*/
	public static void CreateOrSaveAthSingle(String fk_mapdata, String no, String name, float x, float y) throws Exception {
		FrmAttachment ath = new FrmAttachment();
		ath.setFrmID(fk_mapdata);
		ath.setNoOfObj(no);

		ath.setMyPK(ath.getFrmID() + "_" + ath.getNoOfObj());
		ath.RetrieveFromDBSources();
		ath.setUploadType(AttachmentUploadType.Single);
		ath.setName(name);
		ath.Save();
	}
	/**
	 创建/修改-多附件

	 @param fk_mapdata 表单ID
	 @param no 明细表编号
	 @param name 名称
	*/
	public static void CreateOrSaveAthMulti(String fk_mapdata, String no, String name) throws Exception {
		FrmAttachment ath = new FrmAttachment();
		ath.setFrmID(fk_mapdata);
		ath.setNoOfObj(no);
		ath.setMyPK(ath.getFrmID() + "_" + ath.getNoOfObj());
		int i = ath.RetrieveFromDBSources();

		if (i == 0)
		{
			if (fk_mapdata.contains("ND") == true)
			{
				ath.setHisCtrlWay(AthCtrlWay.WorkID);
			}
		}

		ath.setUploadType(AttachmentUploadType.Multi);
		ath.setName(name);
		//默认在移动端显示
		ath.SetPara("IsShowMobile", 1);
		ath.Save();
	}
	/**
	 创建/修改一个明细表

	 @param fk_mapdata 表单ID
	 @param dtlNo 明细表编号
	 @param dtlName 名称
	*/
	public static void CreateOrSaveDtl(String fk_mapdata, String dtlNo, String dtlName) throws Exception {
		MapDtl dtl = new MapDtl();
		dtl.setNo(dtlNo);

		if (dtl.RetrieveFromDBSources() == 0)
		{
			if (dtlName == null)
			{
				dtlName = dtlNo;
			}

			//把他的模式复制过来.
			MapData md = new MapData(fk_mapdata);
			dtl.setPTableModel(md.getPTableModel());

		}

		dtl.setName(dtlName);
		dtl.setFrmID(fk_mapdata);

		dtl.Save();

		//初始化他的map.
		dtl.IntMapAttrs();
	}
	/**
	 创建一个外部数据字段

	 @param fk_mapdata 表单ID
	 @param fieldName 字段名
	 @param fieldDesc 字段中文名
	 @param fk_SFTable 外键表
	 @param x 位置
	 @param y 位置
	*/

	public static void SaveFieldSFTable(String fk_mapdata, String fieldName, String fieldDesc, String fk_SFTable, float x, float y) throws Exception {
		SaveFieldSFTable(fk_mapdata, fieldName, fieldDesc, fk_SFTable, x, y, 1);
	}

	public static void SaveFieldSFTable(String fk_mapdata, String fieldName, String fieldDesc, String fk_SFTable, float x, float y, int colSpan) throws Exception {
		//检查是否可以创建字段?
		MapData md = new MapData();
		md.setNo(fk_mapdata);
		if (md.RetrieveFromDBSources() == 1)
		{
			md.CheckPTableSaveModel(fieldName);
		}

		//外键字段表.
		SFTable sf = new SFTable(fk_SFTable);

		if (DataType.IsNullOrEmpty(fieldDesc) == true)
		{
			fieldDesc = sf.getName();
		}

		MapAttr attr = new MapAttr();
		attr.setMyPK(fk_mapdata + "_" + fieldName);
		attr.RetrieveFromDBSources();

		//基本属性赋值.
		attr.setFrmID(fk_mapdata);
		attr.setKeyOfEn(fieldName);
		attr.setName(fieldDesc);
		attr.setMyDataType(DataType.AppString);

		attr.setUIContralType(UIContralType.DDL);
		attr.setUIBindKey(fk_SFTable); //绑定信息.
		//如果绑定的外键是树形结构的，在AtPara中增加标识
		if (sf.getCodeStruct() == CodeStruct.Tree)
		{
			attr.SetPara("CodeStruct", 1);
		}
		if (DataType.IsNullOrEmpty(sf.getRootVal()) == false)
		{
			attr.SetPara("ParentNo", sf.getRootVal());
		}


		//根据外键表的类型不同，设置它的LGType.
		switch (sf.getSrcType())
		{
			case DictSrcType.CreateTable:
			case DictSrcType.TableOrView:
			case DictSrcType.BPClass:
				attr.setLGType(FieldTypeS.FK);
				break;
			case DictSrcType.SQL: //是sql模式.
			default:
				attr.setLGType(FieldTypeS.Normal);
				break;
		}

		//frmID设置字段所属的分组
		GroupField groupField = new GroupField();
		groupField.Retrieve(GroupFieldAttr.FrmID, fk_mapdata, GroupFieldAttr.CtrlType, "");
		attr.setGroupID(groupField.getOID());
		if (attr.RetrieveFromDBSources() == 0)
		{
			attr.Insert();
		}
		else
		{
			attr.Update();
		}

		//如果是普通的字段, 这个属于外部数据类型,或者webservices类型. sql 语句类型.
		if (attr.getLGType() == FieldTypeS.Normal)
		{
			MapAttr attrH = new MapAttr();
			attrH.Copy(attr);

			attrH.SetValByKey(MapAttrAttr.UIBindKey, "");
			attrH.SetPara("CodeStruct", "");
			attrH.SetPara("ParentNo", "");
			attrH.SetValByKey(MapAttrAttr.KeyOfEn, attr.getKeyOfEn() + "T");
			attrH.SetValByKey(MapAttrAttr.Name, attr.getName());
			attrH.SetValByKey(MapAttrAttr.UIContralType, UIContralType.TB.getValue());
			attrH.SetValByKey(MapAttrAttr.MinLen, 0);
			attrH.SetValByKey(MapAttrAttr.MaxLen, 500);
			attrH.SetValByKey(MapAttrAttr.MyDataType, DataType.AppString);
			attrH.SetValByKey(MapAttrAttr.UIVisible, false);
			attrH.SetValByKey(MapAttrAttr.UIIsEnable, false);
			attrH.SetValByKey(MapAttrAttr.MyPK, attrH.getFrmID() + "_" + attrH.getKeyOfEn());

			if (attrH.RetrieveFromDBSources() == 0)
			{
				attrH.Insert();
			}
			else
			{
				attrH.Update();
			}
		}
	}
	/**
	 保存枚举字段
	 @param fk_mapdata 表单ID
	 @param fieldName 字段名
	 @param fieldDesc 字段描述
	 @param enumKey 枚举值
	 @param ctrlType 显示的控件类型
	 @param x 位置x
	 @param y 位置y
	*/

	public static void SaveFieldEnum(String fk_mapdata, String fieldName, String fieldDesc, String enumKey, UIContralType ctrlType, float x, float y) throws Exception {
		SaveFieldEnum(fk_mapdata, fieldName, fieldDesc, enumKey, ctrlType, x, y, 1);
	}

	public static void SaveFieldEnum(String fk_mapdata, String fieldName, String fieldDesc, String enumKey, UIContralType ctrlType, float x, float y, int colSpan) throws Exception {
		MapAttr ma = new MapAttr();
		ma.setFrmID(fk_mapdata);
		ma.setKeyOfEn(fieldName);

		//赋值主键。
		ma.setMyPK(ma.getFrmID() + "_" + ma.getKeyOfEn());

		//先查询赋值.
		ma.RetrieveFromDBSources();

		ma.setName(fieldDesc);
		ma.setMyDataType(DataType.AppInt);

		ma.setUIIsEnable(true);
		ma.setLGType(FieldTypeS.Enum);

		ma.setUIContralType(ctrlType);
		ma.setUIBindKey(enumKey);

		if (ma.getUIContralType() == UIContralType.RadioBtn)
		{
			SysEnums ses = new SysEnums(ma.getUIBindKey());
			int idx = 0;
			for (SysEnum item : ses.ToJavaList())
			{
				idx++;
				FrmRB rb = new FrmRB();
				rb.setFrmID(ma.getFrmID());
				rb.setKeyOfEn(ma.getKeyOfEn());
				if(DataType.IsNullOrEmpty(item.getStrKey())==false)
					rb.setIntKey(item.getStrKey());
				else
					rb.setIntKey(String.valueOf(item.getIntKey()));
				rb.setMyPK(rb.getFrmID() + "_" + rb.getKeyOfEn() + "_" + rb.getIntKey());
				rb.RetrieveFromDBSources();

				rb.setEnumKey(ma.getUIBindKey());
				rb.setLab(item.getLab());
				rb.Save();
			}
		}
		//frmID设置字段所属的分组
		GroupField groupField = new GroupField();
		groupField.Retrieve(GroupFieldAttr.FrmID, fk_mapdata, GroupFieldAttr.CtrlType, "");
		ma.setGroupID(groupField.getOID());

		ma.Save();
	}

	public static void NewImage(String frmID, String keyOfEn, String name, float x, float y) throws Exception {
		FrmImg img = new FrmImg();
		img.setMyPK(keyOfEn);
		img.setFrmID(frmID);
		img.setName(name);
		img.setItIsEdit(1);
		img.setHisImgAppType(ImgAppType.Img);
		img.Insert();
	}


	public static void NewField(String frmID, String field, String fieldDesc, int mydataType, float x, float y) throws Exception {
		NewField(frmID, field, fieldDesc, mydataType, x, y, 1);
	}

	public static void NewField(String frmID, String field, String fieldDesc, int mydataType, float x, float y, int colSpan) throws Exception {
		//检查是否可以创建字段?
		MapData md = new MapData(frmID);
		md.CheckPTableSaveModel(field);

		MapAttr ma = new MapAttr();
		ma.setFrmID(frmID);
		ma.setKeyOfEn(field);
		ma.setName(fieldDesc);
		ma.setMyDataType(mydataType);
		if (mydataType == 7)
		{
			ma.setItIsSupperText(1);
		}

		//frmID设置字段所属的分组
		GroupField groupField = new GroupField();
		groupField.Retrieve(GroupFieldAttr.FrmID, frmID, GroupFieldAttr.CtrlType, "");
		ma.setGroupID(groupField.getOID());

		ma.Insert();
	}

	public static void NewEnumField(String fk_mapdata, String field, String fieldDesc, String enumKey, UIContralType ctrlType) throws Exception {
		NewEnumField(fk_mapdata, field, fieldDesc, enumKey, ctrlType, 1);
	}

	public static void NewEnumField(String fk_mapdata, String field, String fieldDesc, String enumKey, UIContralType ctrlType, int colSpan) throws Exception {
		//检查是否可以创建字段?
		MapData md = new MapData(fk_mapdata);
		md.CheckPTableSaveModel(field);

		MapAttr ma = new MapAttr();
		ma.setFrmID(fk_mapdata);
		ma.setKeyOfEn(field);
		ma.setName(fieldDesc);
		ma.setMyDataType(DataType.AppInt);
		//ma.X = x;
		//ma.Y = y;
		ma.setUIIsEnable(true);
		ma.setLGType(FieldTypeS.Enum);
		ma.setUIContralType(ctrlType);
		ma.setUIBindKey(enumKey);

		//frmID设置字段所属的分组
		GroupField groupField = new GroupField();
		groupField.Retrieve(GroupFieldAttr.FrmID, fk_mapdata, GroupFieldAttr.CtrlType, "");
		ma.setGroupID(groupField.getOID());
		ma.Insert();

		if (ma.getUIContralType() != UIContralType.RadioBtn)
		{
			return;
		}

		//删除可能存在的数据.
		DBAccess.RunSQL("DELETE FROM Sys_FrmRB WHERE KeyOfEn='" + ma.getKeyOfEn() + "' AND FK_MapData='" + ma.getFrmID() + "'");

		SysEnums ses = new SysEnums(ma.getUIBindKey());
		int idx = 0;
		for (SysEnum item : ses.ToJavaList())
		{
			idx++;
			FrmRB rb = new FrmRB();
			rb.setFrmID(ma.getFrmID());
			rb.setKeyOfEn(ma.getKeyOfEn());
			rb.setEnumKey(ma.getUIBindKey());

			rb.setLab(item.getLab());
			if(DataType.IsNullOrEmpty(item.getStrKey())==false)
				rb.setIntKey(item.getStrKey());
			else
				rb.setIntKey(String.valueOf(item.getIntKey()));
			//rb.X = ma.X;
			////让其变化y值.
			//rb.Y = ma.Y + idx * 30;
			rb.Insert();
		}
	}
	/**
	 创建字段分组

	 @param frmID
	 @param gKey
	 @param gName
	 @return
	*/
	public static String NewCheckGroup(String frmID, String gKey, String gName) throws Exception {
		MapAttr attrN = new MapAttr();
		int i = attrN.Retrieve(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.KeyOfEn, gKey + "_Note");
		i += attrN.Retrieve(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.KeyOfEn, gKey + "_Checker");
		i += attrN.Retrieve(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.KeyOfEn, gKey + "_RDT");
		if (i > 0)
		{
			return "err@前缀已经使用：" + gKey + " ， 请确认您是否增加了这个审核分组或者，请您更换其他的前缀。";
		}

		GroupField gf = new GroupField();
		gf.setLab(gName);
		gf.setFrmID(frmID);
		gf.Insert();

		attrN = new MapAttr();
		attrN.SetValByKey(MapAttrAttr.FK_MapData, frmID);
		attrN.SetValByKey(MapAttrAttr.KeyOfEn, gKey + "_Note");
		attrN.SetValByKey(MapAttrAttr.Name, "审核意见");
		attrN.SetValByKey(MapAttrAttr.MyDataType, DataType.AppString);
		attrN.setUIContralType(UIContralType.TB);
		attrN.SetValByKey(MapAttrAttr.UIIsEnable, true);
		attrN.SetValByKey(MapAttrAttr.UIIsLine, false);
		//attrN.SetValByKey(MapAttrAttr.DefVal, "@WebUser.Name");
		attrN.SetValByKey(MapAttrAttr.GroupID, gf.getOID());
		attrN.SetValByKey(MapAttrAttr.MaxLen, 4000);
		attrN.SetValByKey(MapAttrAttr.UIHeight, 23 * 3);
		attrN.SetValByKey(MapAttrAttr.Idx, 1);
		attrN.Insert();


		attrN = new MapAttr();
		attrN.SetValByKey(MapAttrAttr.FK_MapData, frmID);
		attrN.SetValByKey(MapAttrAttr.KeyOfEn, gKey + "_Checker");
		attrN.SetValByKey(MapAttrAttr.Name, "审核人");
		attrN.SetValByKey(MapAttrAttr.MyDataType, DataType.AppString);
		attrN.setUIContralType(UIContralType.TB);
		attrN.SetValByKey(MapAttrAttr.UIIsEnable, true);
		attrN.SetValByKey(MapAttrAttr.UIIsLine, false);
		attrN.SetValByKey(MapAttrAttr.DefVal, "@WebUser.Name");
		attrN.SetValByKey(MapAttrAttr.GroupID, gf.getOID());
		attrN.SetValByKey(MapAttrAttr.IsSigan, true);
		attrN.SetValByKey(MapAttrAttr.Idx, 2);
		attrN.Insert();


		attrN = new MapAttr();
		attrN.SetValByKey(MapAttrAttr.FK_MapData, frmID);
		attrN.SetValByKey(MapAttrAttr.KeyOfEn, gKey + "_RDT");
		attrN.SetValByKey(MapAttrAttr.Name, "审核日期");
		attrN.SetValByKey(MapAttrAttr.MyDataType, DataType.AppDateTime);
		attrN.setUIContralType(UIContralType.TB);
		attrN.SetValByKey(MapAttrAttr.UIIsEnable, true);
		attrN.SetValByKey(MapAttrAttr.UIIsLine, false);
		attrN.SetValByKey(MapAttrAttr.DefVal, "@RDT");
		attrN.SetValByKey(MapAttrAttr.GroupID, gf.getOID());
		attrN.SetValByKey(MapAttrAttr.Idx, 3);
		attrN.Insert();

		/*
		 * 判断是否是节点设置的审核分组，如果是就为节点设置焦点字段。
		 */
		frmID = frmID.replace("ND", "");
		int nodeid = 0;
		try
		{
			nodeid = Integer.parseInt(frmID);
		}
		catch (java.lang.Exception e)
		{
			//转化不成功就是不是节点表单字段.
			return "error:只能节点表单才可以使用审核分组组件。";
		}
		return null;
	}

		///#endregion 创建修改字段.


		///#region 模版操作.
	/**
	 创建一个审核分组

	 @param frmID 表单ID
	 @param groupName 分组名称
	 @param prx 前缀
	*/
	public static void CreateCheckGroup(String frmID, String groupName, String prx) throws Exception {
		GroupField gf = new GroupField();
		gf.setLab(groupName);
		gf.setFrmID(frmID);
		int i = gf.Retrieve(GroupFieldAttr.Lab, groupName, GroupFieldAttr.FrmID, frmID);
		if (i == 0)
		{
			gf.Insert();
		}

		MapAttr attr = new MapAttr();
		attr.setFrmID(frmID);
		attr.setKeyOfEn(prx + "_Note");
		attr.setName("审核意见"); // sta;  // this.ToE("CheckNote", "审核意见");
		attr.setMyDataType(DataType.AppString);
		attr.setUIContralType(UIContralType.TB);
		attr.setUIIsEnable(true);
		attr.setUIIsLine(true);
		attr.setMaxLen(4000);
		attr.SetValByKey(MapAttrAttr.ColSpan, 4);
		// attr.ColSpan = 4;
		attr.setGroupID(gf.getOID());
		attr.setUIHeight(23 * 3);
		attr.setIdx(1);
		attr.Insert();
		attr.Update("Idx", 1);


		attr = new MapAttr();
		attr.setFrmID(frmID);
		attr.setKeyOfEn(prx + "_Checker");
		attr.setName("审核人"); // "审核人";
		attr.setMyDataType(DataType.AppString);
		attr.setUIContralType(UIContralType.TB);
		attr.setMaxLen(100);
		attr.setMinLen(0);
		attr.setUIIsEnable(true);
		attr.setUIIsLine(false);
		attr.setDefVal("@WebUser.No");
		attr.setUIIsEnable(false);
		attr.setGroupID(gf.getOID());
		attr.setItIsSigan(true);
		attr.setIdx(2);
		attr.Insert();
		attr.Update("Idx", 2);

		attr = new MapAttr();
		attr.setFrmID(frmID);
		attr.setKeyOfEn(prx + "_RDT");
		attr.setName("审核日期"); // "审核日期";
		attr.setMyDataType(DataType.AppDateTime);
		attr.setUIContralType(UIContralType.TB);
		attr.setUIIsEnable(true);
		attr.setUIIsLine(false);
		attr.setDefVal("@RDT");
		attr.setUIIsEnable(false);
		attr.setGroupID(gf.getOID());
		attr.setIdx(3);
		attr.Insert();
		attr.Update("Idx", 3);
	}
	/**
	 创建表单

	 @param frmID 表单ID
	 @param frmName 表单名称
	 @param frmTreeID 表单类别编号（表单树ID）
	*/

	public static void CreateFrm(String frmID, String frmName, String frmTreeID) throws Exception {
		CreateFrm(frmID, frmName, frmTreeID, FrmType.FoolForm);
	}

	public static void CreateFrm(String frmID, String frmName, String frmTreeID, FrmType frmType) throws Exception {
		MapData md = new MapData();
		md.setNo(frmID);
		if (md.getIsExits() == true)
		{
			throw new RuntimeException("@表单ID为:" + frmID + " 已经存在.");
		}

		md.setName(frmName);
		md.setHisFrmType(frmType);
		md.Insert();
	}
	/**
	 一键设置元素只读

	 @param frmID 要设置的表单.
	*/
	public static void OneKeySetFrmEleReadonly(String frmID) throws Exception {
		String sql = "UPDATE Sys_MapAttr SET UIIsEnable=0 WHERE FK_MapData='" + frmID + "'";
		DBAccess.RunSQL(sql);

		MapDtls dtls = new MapDtls(frmID);
		for (MapDtl dtl : dtls.ToJavaList())
		{
			dtl.setItIsInsert(false);
			dtl.setItIsUpdate(false);
			dtl.setItIsDelete(false);
			dtl.Update();
		}

		FrmAttachments ens = new FrmAttachments(frmID);
		for (FrmAttachment en : ens.ToJavaList())
		{
			en.setItIsUpload(false);
			en.setDeleteWay(0);
			en.Update();

		}

	}

	public static void RepareCCForm(String frmID) throws Exception {
		MapData md = new MapData(frmID);
		RepareCCForm(md);
	}

	/**
	 修复表单.

	 @param md
	*/
	public static void RepareCCForm(MapData md) throws Exception {

		if (md.getEntityType() == EntityType.FrmEntityNoName || md.getEntityType() == EntityType.EntityTree)
			return;
		String frmID = md.getNo();
		MapAttr attr = new MapAttr();
		if (attr.IsExit(MapAttrAttr.KeyOfEn, "OID", MapAttrAttr.FK_MapData, frmID) == false)
		{
			attr.SetValByKey(MapAttrAttr.FK_MapData, frmID);
			attr.SetValByKey(MapAttrAttr.KeyOfEn, "OID");
			attr.SetValByKey(MapAttrAttr.Name, "主键");
			attr.SetValByKey(MapAttrAttr.MyDataType, DataType.AppInt);
			attr.SetValByKey(MapAttrAttr.UIContralType, UIContralType.TB.getValue());
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.SetValByKey(MapAttrAttr.DefVal, "0");
			attr.SetValByKey(MapAttrAttr.EditType, EditType.Readonly.getValue());

			attr.Insert();
		}
		if (attr.IsExit(MapAttrAttr.KeyOfEn, "FID", MapAttrAttr.FK_MapData, frmID) == false)
		{
			attr.SetValByKey(MapAttrAttr.FK_MapData, frmID);
			attr.SetValByKey(MapAttrAttr.KeyOfEn, "FID");
			attr.SetValByKey(MapAttrAttr.Name, "干流程主键");
			attr.SetValByKey(MapAttrAttr.MyDataType, DataType.AppInt);
			attr.SetValByKey(MapAttrAttr.UIContralType, UIContralType.TB.getValue());
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.SetValByKey(MapAttrAttr.DefVal, "0");
			attr.SetValByKey(MapAttrAttr.EditType, EditType.Readonly.getValue());

			attr.Insert();
		}
		if (attr.IsExit(MapAttrAttr.KeyOfEn, "AtPara", MapAttrAttr.FK_MapData, frmID) == false)
		{
			attr.setFrmID(frmID);
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("AtPara");
			attr.setName("参数"); // 单据编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setMinLen(0);
			attr.setMaxLen(4000);
			attr.setIdx(-100);
			attr.Insert();
		}

	}


	/**
	 复制表单

	 @param srcFrmID 源表单ID
	 @param copyToFrmID copy到表单ID
	 @param copyFrmName 新实体表单名称
	*/
	public static String CopyFrm(String srcFrmID, String copyToFrmID, String copyFrmName, String fk_frmTree) throws Exception {
		MapData mymd = new MapData();
		mymd.setNo(copyToFrmID);
		if (mymd.RetrieveFromDBSources() == 1)
		{
			throw new RuntimeException("@目标表单ID:" + copyToFrmID + "已经存在，位于:" + mymd.getFormTreeText() + "目录下.");
		}

		//获得源文件信息.
		DataSet ds = GenerHisDataSet_AllEleInfo(srcFrmID);

		//导入表单文件.
		ImpFrmTemplate(copyToFrmID, ds, false);

		//复制模版文件.
		MapData mdCopyTo = new MapData(copyToFrmID);

		if (mdCopyTo.getHisFrmType() == FrmType.ExcelFrm)
		{
			/*如果是excel表单，那就需要复制excel文件.*/
			String srcFile = bp.difference.SystemConfig.getPathOfDataUser() + "FrmVSTOTemplate/" + srcFrmID + ".xls";
			String toFile = bp.difference.SystemConfig.getPathOfDataUser() + "FrmVSTOTemplate/" + copyToFrmID + ".xls";
			if ((new File(srcFile)).isFile() == true)
			{
				if ((new File(toFile)).isFile() == false)
				{
					Files.copy(Paths.get(srcFile), Paths.get(toFile), StandardCopyOption.COPY_ATTRIBUTES);
				}
			}

			srcFile = bp.difference.SystemConfig.getPathOfDataUser() + "FrmVSTOTemplate/" + srcFrmID + ".xlsx";
			toFile = bp.difference.SystemConfig.getPathOfDataUser() + "FrmVSTOTemplate/" + copyToFrmID + ".xlsx";
			if ((new File(srcFile)).isFile() == true)
			{
				if ((new File(toFile)).isFile() == false)
				{
					Files.copy(Paths.get(srcFile), Paths.get(toFile), StandardCopyOption.COPY_ATTRIBUTES);
				}
			}
		}

		mdCopyTo.Retrieve();

		mdCopyTo.setFormTreeNo(fk_frmTree);
		//  md.FK_FrmSort = fk_frmTree;
		mdCopyTo.setName(copyFrmName);
		mdCopyTo.Update();

		return "表单复制成功,您需要重新登录，或者刷新才能看到。";
	}
	/**
	 导入表单API

	 @param toFrmID 要导入的表单ID
	 @param fromds 数据源
	 @param isSetReadonly 是否把空间设置只读？
	*/
	public static void ImpFrmTemplate(String toFrmID, DataSet fromds, boolean isSetReadonly) throws Exception {
		MapData.ImpMapData(toFrmID, fromds);
	}
	/**
	 修改frm的事件

	 @param frmID
	*/
	public static void AfterFrmEditAction(String frmID) throws Exception {
		//清除缓存.
		CacheFrmTemplate.Remove(frmID);
		Cache.SetMap(frmID, null);

		MapData mapdata = new MapData();
		mapdata.setNo(frmID);
		mapdata.RetrieveFromDBSources();
		Cache2019.UpdateRow(mapdata.toString(), frmID, mapdata.getRow());
		GEEntity en = new GEEntity(frmID);
		en.setRow(null);
		en.setSQLCache(null);
		mapdata.CleanObject();
		return;
	}
	/**
	 获得表单信息.

	 @param frmID 表单
	 @return
	*/

	public static DataSet GenerHisDataSet(String frmID, String frmName) throws Exception {
		return GenerHisDataSet(frmID, frmName, null);
	}

	public static DataSet GenerHisDataSet(String frmID) throws Exception {
		return GenerHisDataSet(frmID, null, null);
	}

	public static DataSet GenerHisDataSet(String frmID, String frmName, MapData md) throws Exception {
		//首先从缓存获取数据.
		//DataSet dsFrm = CacheFrmTemplate.GetFrmDataSetModel(frmID);
//		if (dsFrm != null)
//		{
//			return dsFrm;
//		}
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		ThreadPoolTaskExecutor threadPoolTaskExecutor= JflowThreadPoolTaskExecutor.getInstance();

		DataSet ds = new DataSet();

		Future<Boolean> futureSys_MapData = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				MapData mapData=new MapData(frmID);

				if (DataType.IsNullOrEmpty(mapData.getName()) == true && frmName != null)
				{
					mapData.setName(frmName);
				}
				//加入主表信息.
				DataTable Sys_MapData = mapData.ToDataTableField("Sys_MapData");
				ds.Tables.add(Sys_MapData);
				return true;
			}
		});

		Future<Boolean> futureSys_GroupField = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				MapData mapData=new MapData(frmID);
				if (DataType.IsNullOrEmpty(mapData.getName()) == true && frmName != null)
				{
					mapData.setName(frmName);
				}
				//加入主表信息.
				DataTable Sys_GroupField = mapData.getGroupFields().ToDataTableField("Sys_GroupField");
				ds.Tables.add(Sys_GroupField);
				return true;
			}
		});



		Future<Boolean> futureSys_MapDtl = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				MapData mapData=new MapData(frmID);

				if (DataType.IsNullOrEmpty(mapData.getName()) == true && frmName != null)
				{
					mapData.setName(frmName);
				}
				//加入明细表.
				DataTable Sys_MapDtl = mapData.getOrigMapDtls().ToDataTableField("Sys_MapDtl");
				ds.Tables.add(Sys_MapDtl);
				return true;
			}
		});

		Future<Boolean> futureSys_Menu = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				MapData mapData=new MapData(frmID);

				if (DataType.IsNullOrEmpty(mapData.getName()) == true && frmName != null)
				{
					mapData.setName(frmName);
				}
				//加入枚举表.
				DataTable Sys_Menu = mapData.getSysEnums().ToDataTableField("Sys_Enum");
				ds.Tables.add(Sys_Menu);
				return true;
			}
		});

		Future<Boolean> futureSys_MapAttr = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				MapData mapData=new MapData(frmID);

				if (DataType.IsNullOrEmpty(mapData.getName()) == true && frmName != null)
				{
					mapData.setName(frmName);
				}

				//加入外键属性.
				DataTable Sys_MapAttr = mapData.getMapAttrs().ToDataTableField("Sys_MapAttr");
				ds.Tables.add(Sys_MapAttr);
				return true;
			}
		});


		Future<Boolean> futureSys_MapExt = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				MapData mapData=new MapData(frmID);

				if (DataType.IsNullOrEmpty(mapData.getName()) == true && frmName != null)
				{
					mapData.setName(frmName);
				}

				//加入扩展属性.
				DataTable Sys_MapExt = mapData.getMapExts().ToDataTableField("Sys_MapExt");
				ds.Tables.add(Sys_MapExt);
				return true;
			}
		});


		Future<Boolean> futureSys_FrmRB = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				MapData mapData=new MapData(frmID);

				if (DataType.IsNullOrEmpty(mapData.getName()) == true && frmName != null)
				{
					mapData.setName(frmName);
				}

				//加入扩展属性.
				//Sys_FrmRB.
				DataTable Sys_FrmRB = mapData.getFrmRBs().ToDataTableField("Sys_FrmRB");
				ds.Tables.add(Sys_FrmRB);
				return true;
			}
		});

		Future<Boolean> futureSys_FrmImg = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				MapData mapData=new MapData(frmID);

				if (DataType.IsNullOrEmpty(mapData.getName()) == true && frmName != null)
				{
					mapData.setName(frmName);
				}

				//img.
				DataTable Sys_FrmImg = mapData.getFrmImgs().ToDataTableField("Sys_FrmImg");
				ds.Tables.add(Sys_FrmImg);
				return true;
			}
		});

		Future<Boolean> futureSys_MapFrame = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				MapData mapData=new MapData(frmID);
				if (DataType.IsNullOrEmpty(mapData.getName()) == true && frmName != null)
				{
					mapData.setName(frmName);
				}

				//Sys_MapFrame.
				DataTable Sys_MapFrame = mapData.getMapFrames().ToDataTableField("Sys_MapFrame");
				ds.Tables.add(Sys_MapFrame);
				return true;
			}
		});


		Future<Boolean> futureSys_FrmAttachment = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				MapData mapData=new MapData(frmID);

				if (DataType.IsNullOrEmpty(mapData.getName()) == true && frmName != null)
				{
					mapData.setName(frmName);
				}

				//Sys_FrmAttachment.
				DataTable Sys_FrmAttachment = mapData.getFrmAttachments().ToDataTableField("Sys_FrmAttachment");
				ds.Tables.add(Sys_FrmAttachment);
				return true;
			}
		});

		Future<Boolean> futureSys_FrmImgAth = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				MapData mapData=new MapData(frmID);

				if (DataType.IsNullOrEmpty(mapData.getName()) == true && frmName != null)
				{
					mapData.setName(frmName);
				}
				//FrmImgAths. 上传图片附件.
				DataTable Sys_FrmImgAth = mapData.getFrmImgAths().ToDataTableField("Sys_FrmImgAth");
				ds.Tables.add(Sys_FrmImgAth);
				return true;
			}
		});

		futureSys_MapData.get();
		futureSys_GroupField.get();
		futureSys_MapDtl.get();
		futureSys_Menu.get();
		futureSys_MapAttr.get();
		futureSys_MapExt.get();
		futureSys_FrmRB.get();
		futureSys_FrmImg.get();
		futureSys_MapFrame.get();
		futureSys_FrmAttachment.get();
		futureSys_FrmImgAth.get();
		//放入缓存.
		//CacheFrmTemplate.Put(frmID, ds);

		return ds;
	}
	/**
	 获得表单字段信息字段.

	 @param fk_mapdata
	 @return
	*/
	public static DataSet GenerHisDataSet_AllEleInfo(String fk_mapdata) throws Exception {
		MapData md = new MapData(fk_mapdata);

		//求出 frmIDs
		String frmIDs = "'" + fk_mapdata + "'";
		MapDtls mdtls = new MapDtls(md.getNo());
		for (MapDtl item : mdtls.ToJavaList())
		{
			frmIDs += ",'" + item.getNo() + "'";
		}

		DataSet ds = new DataSet();

		//加入主表信息.
		DataTable Sys_MapData = md.ToDataTableField("Sys_MapData");

		//如果是开发者表单, 就把html信息放入到字段.
		if (md.getHisFrmType() == FrmType.Develop)
		{
			Sys_MapData.Columns.Add("HtmlTemplateFile", String.class);
			String text = DBAccess.GetBigTextFromDB("Sys_MapData", "No", md.getNo(), "HtmlTemplateFile");
			Sys_MapData.Rows.get(0).setValue("HtmlTemplateFile",text);
		}


		ds.Tables.add(Sys_MapData);

		//加入分组表.
		GroupFields gfs = new GroupFields();
		gfs.RetrieveIn(GroupFieldAttr.FrmID, frmIDs);
		DataTable Sys_GroupField = gfs.ToDataTableField("Sys_GroupField");
		ds.Tables.add(Sys_GroupField);

		//加入明细表.
		DataTable Sys_MapDtl = md.getOrigMapDtls().ToDataTableField("Sys_MapDtl");
		ds.Tables.add(Sys_MapDtl);

		//加入枚举表.
		SysEnums ses = new SysEnums();
		ses.RetrieveInSQL(SysEnumAttr.EnumKey, "SELECT UIBindKey FROM Sys_MapAttr WHERE FK_MapData IN (" + frmIDs + ") ");
		DataTable Sys_Menu = ses.ToDataTableField("Sys_Enum");
		ds.Tables.add(Sys_Menu);

		//加入字段属性.
		MapAttrs attrs = new MapAttrs();
		attrs.RetrieveIn(MapAttrAttr.FK_MapData, frmIDs);
		DataTable Sys_MapAttr = attrs.ToDataTableField("Sys_MapAttr");
		ds.Tables.add(Sys_MapAttr);

		//加入扩展属性.
		MapExts exts = new MapExts();
		exts.RetrieveIn(MapAttrAttr.FK_MapData, frmIDs);
		DataTable Sys_MapExt = exts.ToDataTableField("Sys_MapExt");
		if (exts.getIsExits("ExtType", "HtmlText") == true)
		{
			Sys_MapExt.Columns.Add("HtmlText", String.class);
			for (DataRow dr : Sys_MapExt.Rows)
			{
				if (dr.getValue("ExtType").equals("HtmlText") == true)
				{
					String text = DBAccess.GetBigTextFromDB("Sys_MapExt", "MyPK", dr.getValue("MyPK").toString(), "HtmlText");
					dr.setValue("HtmlText", text);
				}
			}

		}
		ds.Tables.add(Sys_MapExt);

		//img.
		//Sys_FrmLab.
		FrmImgs frmImgs = new FrmImgs();
		frmImgs.RetrieveIn(MapAttrAttr.FK_MapData, frmIDs);
		ds.Tables.add(frmImgs.ToDataTableField("Sys_FrmImg"));

		//Sys_FrmRB.
		DataTable Sys_FrmRB = md.getFrmRBs().ToDataTableField("Sys_FrmRB");
		ds.Tables.add(Sys_FrmRB);


		//Sys_MapFrame.
		DataTable Sys_MapFrame = md.getMapFrames().ToDataTableField("Sys_MapFrame");
		ds.Tables.add(Sys_MapFrame);

		//Sys_FrmAttachment.
		FrmAttachments aths = md.getFrmAttachments();
		//获取从表中的附件字段
		for (MapDtl dtl : md.getOrigMapDtls().ToJavaList())
		{
			FrmAttachments dtlAths = new FrmAttachments(dtl.getNo());
			aths.AddEntities(dtlAths);

		}
		DataTable Sys_FrmAttachment = aths.ToDataTableField("Sys_FrmAttachment");
		ds.Tables.add(Sys_FrmAttachment);

		//FrmImgAths. 上传图片附件.
		DataTable Sys_FrmImgAth = md.getFrmImgAths().ToDataTableField("Sys_FrmImgAth");
		ds.Tables.add(Sys_FrmImgAth);

		return ds;
	}
	/**
	 获得表单模版dataSet格式.

	 @param fk_mapdata 表单ID
	 @return DataSet
	*/

	public static DataSet GenerHisDataSet_AllEleInfo2017(String fk_mapdata) throws Exception {
		return GenerHisDataSet_AllEleInfo2017(fk_mapdata, false);
	}

	public static DataSet GenerHisDataSet_AllEleInfo2017(String fk_mapdata, boolean isCheckFrmType) throws Exception {
		MapData md = new MapData(fk_mapdata);

		//从表.
		String sql = "SELECT * FROM Sys_MapDtl WHERE FK_MapData ='{0}'";
		sql = String.format(sql, fk_mapdata);
		DataTable dtMapDtl = DBAccess.RunSQLReturnTable(sql);
		dtMapDtl.TableName = "Sys_MapDtl";

		String ids = String.format("'%1$s'", fk_mapdata);
		for (DataRow dr : dtMapDtl.Rows)
		{
			ids += ",'" + dr.getValue("No") + "'";
		}
		String sqls = "";
		ArrayList<String> listNames = new ArrayList<String>();

		// Sys_GroupField.
		listNames.add("Sys_GroupField");
		sql = "SELECT * FROM Sys_GroupField WHERE  FrmID IN (" + ids + ")";
		sqls += sql;

		// Sys_Enum
		listNames.add("Sys_Enum");
		sql = "@SELECT * FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey IN ( SELECT UIBindKey FROM Sys_MapAttr WHERE FK_MapData IN (" + ids + ") ) order By EnumKey,IntKey";
		sqls += sql;

		// 审核组件
		String nodeIDstr = fk_mapdata.replace("ND", "");
		if (DataType.IsNumStr(nodeIDstr))
		{
			// 审核组件状态:0 禁用;1 启用;2 只读;
			listNames.add("WF_Node");
			sql = "@SELECT * FROM WF_Node WHERE NodeID=" + nodeIDstr + " AND  ( FWCSta >0  OR SFSta >0 )";
			sqls += sql;
		}

		String where = " FK_MapData IN (" + ids + ")";

		// Sys_MapData.
		listNames.add("Sys_MapData");
		sql = "@SELECT * FROM Sys_MapData WHERE No='" + fk_mapdata + "'";
		sqls += sql;

		// Sys_MapAttr.
		listNames.add("Sys_MapAttr");

		sql = "@SELECT * FROM Sys_MapAttr WHERE " + where + " AND KeyOfEn NOT IN('WFState') ORDER BY FK_MapData, IDX  ";
		sqls += sql;


		// Sys_MapExt.
		listNames.add("Sys_MapExt");
		sql = "@SELECT * FROM Sys_MapExt WHERE " + where;
		sqls += sql;

		//if (isCheckFrmType == true && md.getHisFrmType() == FrmType.FreeFrm)
		//{
		// line.
	  //  listNames.Add("Sys_FrmLine");
	 //   sql = "@SELECT * FROM Sys_FrmLine WHERE " + where;
	   // sqls += sql;

		// link.
		listNames.add("Sys_FrmLink");
		sql = "@SELECT * FROM Sys_FrmLink WHERE " + where;
		sqls += sql;

		// btn.
		listNames.add("Sys_FrmBtn");
		sql = "@SELECT * FROM Sys_FrmBtn WHERE " + where;
		sqls += sql;

		// Sys_FrmImg.
		listNames.add("Sys_FrmImg");
		sql = "@SELECT * FROM Sys_FrmImg WHERE " + where;
		sqls += sql;

		// Sys_FrmLab.
		listNames.add("Sys_FrmLab");
		sql = "@SELECT * FROM Sys_FrmLab WHERE " + where;
		sqls += sql;
		//}

		// Sys_FrmRB.
		listNames.add("Sys_FrmRB");
		sql = "@SELECT * FROM Sys_FrmRB WHERE " + where;
		sqls += sql;

		// ele.
		listNames.add("Sys_FrmEle");
		sql = "@SELECT * FROM Sys_FrmEle WHERE " + where;
		sqls += sql;

		//Sys_MapFrame.
		listNames.add("Sys_MapFrame");
		sql = "@SELECT * FROM Sys_MapFrame WHERE " + where;
		sqls += sql;

		// Sys_FrmAttachment.
		listNames.add("Sys_FrmAttachment");
		/* 20150730 小周鹏修改 添加AtPara 参数 START */
		//sql = "@SELECT  MyPK,FK_MapData,UploadType,X,Y,W,H,NoOfObj,Name,Exts,SaveTo,IsUpload,IsDelete,IsDownload "
		// + " FROM Sys_FrmAttachment WHERE " + where + " AND FK_Node=0";
		sql = "@SELECT * " + " FROM Sys_FrmAttachment WHERE " + where + "";

		/* 20150730 小周鹏修改 添加AtPara 参数 END */
		sqls += sql;

		// Sys_FrmImgAth.
		listNames.add("Sys_FrmImgAth");

		sql = "@SELECT * FROM Sys_FrmImgAth WHERE " + where;
		sqls += sql;

		//// sqls.replace(";", ";" + Environment.NewLine);
		// DataSet ds = DA.DBAccess.RunSQLReturnDataSet(sqls);
		// if (ds != null && ds.Tables.size()== listNames.size())
		//     for (int i = 0; i < listNames.Count; i++)
		//     {
		//         ds.Tables[i].TableName = listNames[i];
		//     }

		String[] strs = sqls.split("[@]", -1);
		DataSet ds = new DataSet();

		if (strs != null && strs.length == listNames.size())
		{
			for (int i = 0; i < listNames.size(); i++)
			{
				String s = strs[i];
				if (DataType.IsNullOrEmpty(s))
				{
					continue;
				}
				DataTable dt = DBAccess.RunSQLReturnTable(s);
				dt.TableName = listNames.get(i);
				ds.Tables.add(dt);
			}
		}

		for (DataTable item : ds.Tables)
		{
			if (Objects.equals(item.TableName, "Sys_MapAttr") && item.Rows.size() == 0)
			{
				md.RepairMap();
			}
		}

		ds.Tables.add(dtMapDtl);
		return ds;
	}

		///#endregion 模版操作.


		///#region 模版操作 2020.
	/**
	 A:从一个表单导入另外一个表单模版:
	 1.向已经存在的表单上导入模版.
	 2.用于节点表单的导入,设计表单的时候，新建一个表单后在导入的情况.

	 @param frmID
	 @param specImpFrmID
	 @return
	*/
	public static MapData Template_ImpFromSpecFrmID(String frmID, String specImpFrmID)
	{
		return null;
	}

	/**
	 B:复制表单模版到指定的表单ID.
	 用于复制一个表单，到另外一个表单ID上去.用于表单树的上的表单Copy.

	 @param fromFrmID 要copy的表单ID
	 @param copyToFrmID copy到的表单ID
	 @param copyToFrmName 表单名称
	 @return
	*/
	public static MapData Template_CopyFrmToFrmIDAsNewFrm(String fromFrmID, String copyToFrmID, String copyToFrmName)
	{

		return null;
	}
	/**
	 C:导入模版xml文件..
	 导入一个已经存在的表单,如果这个表单ID已经存在就提示错误..

	 @param  ds 表单元素
	 @param  frmSort 表单类别
	 @return
	*/
	public static MapData Template_LoadXmlTemplateAsNewFrm(DataSet ds, String frmSort) throws Exception {
		MapData md = MapData.ImpMapData(ds);
		md.setOrgNo(DBAccess.RunSQLReturnString("SELECT OrgNo FROM sys_formtree WHERE NO='" + frmSort + "'"));
		md.setFormTreeNo(frmSort);
		md.Update();
		return md;
	}
	public static MapData Template_LoadXmlTemplateAsSpecFrmID(String newFrmID, DataSet ds, String frmSort) throws Exception {
		MapData md = MapData.ImpMapData(newFrmID, ds);
		md.setOrgNo(DBAccess.RunSQLReturnString("SELECT OrgNo FROM sys_formtree WHERE NO='" + frmSort + "'"));
		md.setFormTreeNo(frmSort);
		md.Update();
		return md;
	}

		///#endregion 模版操作.


		///#region 其他功能.
	/**
	 保存枚举

	 @param enumKey 键值对
	 @param enumLab 标签
	 @param cfg 配置 @0=xxx@1=yyyy@n=xxxxxc
	 @return
	*/

	public static String SaveEnum(String enumKey, String enumLab, String cfg, boolean isNew) throws Exception {
		return SaveEnum(enumKey, enumLab, cfg, isNew, "CH");
	}

	public static String SaveEnum(String enumKey, String enumLab, String cfg, boolean isNew, String lang) throws Exception {
		SysEnumMain sem = new SysEnumMain();
		sem.setNo(enumKey);
		int dataCount = sem.RetrieveFromDBSources();
		if (dataCount > 0 && isNew)
		{
			return "err@已存在枚举" + enumKey + ",请修改枚举名字";
		}

		if (dataCount == 0)
		{
			sem.setName(enumLab);
			sem.setCfgVal(cfg);
			sem.setLang(lang);
			sem.DirectInsert();
		}
		else
		{
			sem.setName(enumLab);
			sem.setCfgVal(cfg);
			sem.setLang(lang);
			sem.Update();
		}

		String[] strs = cfg.split("[@]", -1);
		for (String str : strs)
		{
			if (DataType.IsNullOrEmpty(str))
			{
				continue;
			}
			String[] kvs = str.split("[=]", -1);
			SysEnum se = new SysEnum();
			se.setEnumKey(enumKey);
			se.setLang(lang);
			se.setIntKey(Integer.parseInt(kvs[0]));
			//杨玉慧
			//解决当  枚举值含有 '='号时，保存不进去的方法
			String[] kvsValues = new String[kvs.length - 1];
			for (int i = 0; i < kvsValues.length; i++)
			{
				kvsValues[i] = kvs[i + 1];
			}
			se.setLab(StringHelper.join("=", kvsValues));
			se.setMyPK(se.getEnumKey() + "_" + se.getLang() + "_" + se.getIntKey());
			se.Save();
		}
		return "保存成功.";
	}
	/**
	 转拼音方法

	 @param name 字段中文名称
	 @param isQuanPin 是否转换全拼
	 @return 转化后的拼音，不成功则抛出异常.
	*/
	public static String ParseStringToPinyinField(String name, boolean isQuanPin)
	{
		if (DataType.IsNullOrEmpty(name) == true)
		{
			return "";
		}

		String s = "";
		try
		{
			if (isQuanPin == true)
			{
				s = DataType.ParseStringToPinyin(name);
				if (s.length() > 15)
				{
					s = DataType.ParseStringToPinyinJianXie(name);
				}
			}
			else
			{
				s = DataType.ParseStringToPinyinJianXie(name);
			}

			s = s.trim().replace(" ", "");
			s = s.trim().replace(" ", "");
			//常见符号
			s = s.replace(",", "").replace(".", "").replace("，", "").replace("。", "").replace("!", "");
			s = s.replace("*", "").replace("@", "").replace("#", "").replace("~", "").replace("|", "");
			s = s.replace("$", "").replace("%", "").replace("&", "").replace("（", "").replace("）", "").replace("【", "").replace("】", "");
			s = s.replace("(", "").replace(")", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace("/", "");
			if (s.length() > 0)
			{
				//去除开头数字
				String headStr = s.substring(0, 1);
				if (DataType.IsNumStr(headStr) == true)
				{
					s = "F" + s;
				}
			}
			//去掉空格，去掉点.
			s = s.replace(" ", "");
			s = s.replace(".", "");
			return s;
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException(ex.getMessage());
		}
	}
	/**
	 转拼音全拼/简写方法(若转换后以数字开头，则前面加F)
	 <p>added by liuxc,2017-9-25</p>

	 @param name 中文字符串
	 @param isQuanPin 是否转换全拼
	 @param removeSpecialSymbols 是否去除特殊符号，仅保留汉字、数字、字母、下划线
	 @param maxLen 转化后字符串最大长度，0为不限制
	 @return 转化后的拼音，不成功则抛出异常.
	*/
	public static String ParseStringToPinyinField(String name, boolean isQuanPin, boolean removeSpecialSymbols, int maxLen)
	{
		if (DataType.IsNullOrEmpty(name) == true)
		{
			return "";
		}

		String s = "";

		if (removeSpecialSymbols)
		{
			name = DataType.ParseStringForName(name, maxLen);
		}

		//单.
		//name = name.replace("单", "Dan");

		try
		{
			if (isQuanPin == true)
			{
				s = DataType.ParseStringToPinyin(name);
			}
			else
			{
				s = DataType.ParseStringToPinyinJianXie(name);
			}

			//如果全拼长度超过maxLen，则取前maxLen长度的字符
			if (maxLen > 0 && s.length() > maxLen)
			{
				s = s.substring(0, maxLen);
			}

			if (s.length() > 0)
			{
				//去除开头数字
				String headStr = s.substring(0, 1);
				if (DataType.IsNumStr(headStr) == true)
				{
					s = "F" + (s.length() > maxLen - 1 ? s.substring(0, maxLen - 1) : s);
				}
			}

			return s;
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException(ex.getMessage());
		}
	}
	/**
	 多音字转拼音

	 @param charT 单个汉字
	 @return 包含返回拼音，否则返回null
	*/
	public static String ChinaMulTonesToPinYin(String charT) throws Exception {
		try
		{
			ChMulToneXmls mulChs = new ChMulToneXmls();
			mulChs.RetrieveAll();
			for (ChMulToneXml en : mulChs.ToJavaList())
			{
				if (en.getNo().equals(charT))
				{
					return en.getName();
				}
			}
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException(ex.getMessage());
		}
		return null;
	}

	/**
	 获得外键表

	 @param pageNumber 第几页
	 @param pageSize 每页大小
	 @return json
	*/
	public static String DB_SFTableList(int pageNumber, int pageSize) throws Exception {
		//获得查询.
		SFTables sftables = new SFTables();
		QueryObject obj = new QueryObject(sftables);
		int RowCount = obj.GetCount();

		//查询
		obj.DoQuery(SysEnumMainAttr.No, pageSize, pageNumber);

		//转化成json.
		return bp.tools.Json.ToJson(sftables.ToDataTableField());
		// return BP.Tools.Entitis2Json.ConvertEntitis2GridJsonOnlyData(sftables, RowCount);
	}
	/**
	 获得隐藏字段集合.

	 @param fk_mapdata
	 @return
	*/
	public static String DB_Hiddenfielddata(String fk_mapdata) throws Exception {
		MapAttrs mapAttrs = new MapAttrs();
		QueryObject obj = new QueryObject(mapAttrs);
		obj.AddWhere(MapAttrAttr.FK_MapData, fk_mapdata);
		obj.addAnd();
		obj.AddWhere(MapAttrAttr.UIVisible, "0");
		obj.addAnd();
		obj.AddWhere(MapAttrAttr.EditType, "0");
		//查询
		obj.DoQuery();

		return mapAttrs.ToJson();
	}

		///#endregion 其他功能.

}
