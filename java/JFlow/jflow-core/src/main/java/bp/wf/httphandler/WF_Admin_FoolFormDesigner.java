package bp.wf.httphandler;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.CCFormAPI;
import bp.sys.frmui.MapAttrString;
import bp.tools.StringUtils;
import bp.web.*;
import bp.sys.*;
import bp.wf.template.*;
import bp.wf.template.frm.*;
import bp.difference.*;
import bp.*;
import bp.wf.*;

import java.io.IOException;
import java.util.*;

/** 
 处理页面的业务逻辑
*/
public class WF_Admin_FoolFormDesigner extends bp.difference.handler.DirectoryPageBase
{

		///#region 表单设计器.
	public final String Designer_Move() throws Exception {
		//当前的MyPK.
		String mypk = this.getMyPK(); //当前的主键.

		//获得当前分组下的字段集合.
		String mypks = this.GetRequestVal("MyPKs"); //字段集合.
		int groupID = this.GetRequestValInt("GroupID");

		GroupField gf = new GroupField(groupID);
		if (gf.getCtrlType().equals("") == false)
		{
			return "err@你不能移动到【" + gf.getLab() + "】它是非字段分组，您不能移动。";
		}
		String sql = "";
		String[] strs = mypks.split("[,]", -1);
		for (int i = 0; i < strs.length; i++)
		{
			String str = strs[i];
			if (str.equals(mypk))
			{
				sql = "UPDATE Sys_MapAttr SET GroupID=" + groupID + ", Idx=" + i + " WHERE MyPK='" + str + "'";
			}
			else
			{
				sql = "UPDATE Sys_MapAttr SET Idx=" + i + " WHERE MyPK='" + str + "'";
			}
			DBAccess.RunSQL(sql);
		}
		return "表单顺序移动成功..";
	}
	/** 
	 字段分组移动.
	 
	 @return 
	*/
	public final String DesignerVue_GF_Move()
	{
		String[] strs = this.GetRequestVal("Vals").split("[,]", -1);
		for (int i = 0; i < strs.length; i++)
		{
			String str = strs[i];
			if (DataType.IsNullOrEmpty(str))
			{
				continue;
			}

			String sql = "UPDATE Sys_GroupField SET Idx=" + i + " WHERE OID=" + str;
			DBAccess.RunSQL(sql);
		}
		return "分组顺序移动成功...";
	}
	/** 
	  自动创建字段接口
	 
	 @return 
	*/
	public final String DesignerVue_CreateField() throws Exception {
		//获得类型.
		int dataType = Integer.parseInt(this.GetRequestVal("DataType"));

		int fIdx = Integer.parseInt(this.GetRequestVal("Idx"));

		MapAttr attr = new MapAttr();

		int idx = 0;
		while (true)
		{
			idx++;

			String field = "F" + String.valueOf(idx);

			attr.setMyPK(this.getFrmID() + "_" + field);
			if (attr.getIsExits())
			{
				continue;
			}

			//   if (attr.IsExit("KeyOfEn", field, "FK_MapData", this.FrmID, "DataType", dataType) == true)
			//     continue;

			if (dataType == DataType.AppString)
			{
				attr.setKeyOfEn(field);
				attr.setName("文字" + field);
				attr.setMyDataType(dataType);
				attr.setMaxLen(50);
				attr.setMinLen(0);
				attr.setIdx(fIdx);
				attr.Insert();
				return attr.ToJson(true);
			}

			if (dataType == DataType.AppInt)
			{
				attr.setKeyOfEn(field);
				attr.setName("Int数值" + field);
				attr.setMyDataType(dataType);
				attr.setIdx(fIdx);
				attr.Insert();
				return attr.ToJson(true);
			}

			if (dataType == DataType.AppBoolean)
			{
				attr.setKeyOfEn(field);
				attr.setName("开关类型：" + field);
				attr.setMyDataType(dataType);
				attr.setIdx(fIdx);
				attr.Insert();
				return attr.ToJson(true);
			}

			if (dataType == DataType.AppMoney)
			{
				attr.setKeyOfEn(field);
				attr.setName("金额类型：" + field);
				attr.setMyDataType(dataType);
				attr.setIdx(fIdx);
				attr.Insert();
				return attr.ToJson(true);
			}

			if (dataType == DataType.AppFloat)
			{
				attr.setKeyOfEn(field);
				attr.setName("Float数值" + field);
				attr.setMyDataType(dataType);
				attr.setIdx(fIdx);
				attr.Insert();
				return attr.ToJson(true);
			}

			if (dataType == DataType.AppDouble)
			{
				attr.setKeyOfEn(field);
				attr.setName("Double数值" + field);
				attr.setMyDataType(dataType);
				attr.setIdx(fIdx);
				attr.Insert();
				return attr.ToJson(true);
			}

			if (dataType == DataType.AppDate)
			{
				attr.setKeyOfEn(field);
				attr.setName("Date" + field);
				attr.setMyDataType(dataType);
				attr.setIdx(fIdx);
				attr.Insert();
				return attr.ToJson(true);
			}
			if (dataType == DataType.AppDateTime)
			{
				attr.setKeyOfEn(field);
				attr.setName("DateTime" + field);
				attr.setMyDataType(dataType);
				attr.setIdx(fIdx);
				attr.Insert();
				return attr.ToJson(true);
			}

			return "err@参数类型错误:dataType:" + dataType + ",没有判断.";
		}

	}
	/** 
	 删除字段
	 
	 @return 
	*/
	public final String DesignerVue_DeleteField() throws Exception {
		MapAttr ma = new MapAttr(this.getMyPK());
		ma.Delete();
		return "删除成功.";
	}

	/** 
	 是不是第一次进来.
	*/
	public final boolean getItIsFirst()
	{
		if (this.GetRequestVal("IsFirst") == null || Objects.equals(this.GetRequestVal("IsFirst"), "") || Objects.equals(this.GetRequestVal("IsFirst"), "null"))
		{
			return false;
		}
		return true;
	}
	/** 
	 检查表单
	 
	 @return 
	*/
	public final String Designer_CheckFrm() throws Exception {

			///#region  检查完整性.
		if (this.getFrmID().toUpperCase().contains("BP.") == true)
		{
			/*如果是类的实体.*/
			Entities ens = ClassFactory.GetEns(this.getFrmID());
			Entity en = ens.getNewEntity();

			MapData mymd = new MapData();
			mymd.setNo(this.getFrmID());
			mymd.ClearCache(); //清除缓存。

			int i = mymd.RetrieveFromDBSources();
			if (i == 0)
			{
				en.DTSMapToSys_MapData(this.getFrmID()); //调度数据到
			}

			mymd.RetrieveFromDBSources();
			mymd.setHisFrmType(FrmType.FoolForm);
			mymd.Update();
		}

			///#endregion

	 //   MapFrmFool cols = new MapFrmFool(this.FrmID);
	  //  cols.DoCheckFixFrmForUpdateVer();
		return "url@Designer.htm?FK_MapData=" + this.getFrmID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID();
	}
	/** 
	  设计器初始化.
	 
	 @return 
	*/
	public final String Designer_Init() throws Exception {
		//删除垃圾数据.
		DBAccess.RunSQL("DELETE FROM Sys_GroupField where oid not in (select groupID from Sys_MapAttr ) and ctrlType='' and lab=''");

		DataSet ds = new DataSet();
		//如果是第一次进入，就执行旧版本的升级检查.
		if (this.getItIsFirst() == true)
		{
			return Designer_CheckFrm();
		}

		//把表单属性放入里面去.
		MapData md = new MapData();
		md.setNo(this.getFrmID());
		int i = md.RetrieveFromDBSources();
		MapDtl mapDtl = new MapDtl();
		mapDtl.setNo(this.getFrmID());
		int count = mapDtl.RetrieveFromDBSources();
		boolean isDtlMapData = count == 0 ? false : true;
		if(i == 0){
			if(count == 1){
				md.setName(mapDtl.getName());
				md.setFormEventEntity("");
				md.setEnPK("");
				md.Insert();
			}
		}
		md.RepairMap(isDtlMapData);
		//清缓存
		md.ClearCache();
		ds.Tables.add(md.ToDataTableField("Sys_MapData").copy());

		// 字段属性.
		MapAttrs mattrs = new MapAttrs(this.getFrmID());
		for (MapAttr item : mattrs.ToJavaList())
		{
			item.setDefVal(item.getDefValReal());
		}

		ds.Tables.add(mattrs.ToDataTableField("Sys_MapAttr"));

		MapDtls dtls = new MapDtls();
		dtls.Retrieve(MapDtlAttr.FK_MapData, this.getFrmID(), MapDtlAttr.FK_Node, 0, null);
		ds.Tables.add(dtls.ToDataTableField("Sys_MapDtl"));

		GroupFields gfs = new GroupFields(this.getFrmID());
		// 检查组件的分组是否完整?
		for (GroupField item : gfs.ToJavaList())
		{
			boolean isHave = false;
			if (item.getCtrlType() == null)
			{
				item.setCtrlType("");
			}
			if (Objects.equals(item.getCtrlType(), "Dtl"))
			{
				for (MapDtl dtl : dtls.ToJavaList())
				{
					if (Objects.equals(dtl.getNo(), item.getCtrlID()))
					{
						isHave = true;
						break;
					}
				}
				//分组不存在了，就删除掉他.
				if (isHave == false)
				{
					item.Delete();
				}
			}
		}
		ds.Tables.add(gfs.ToDataTableField("Sys_GroupField"));



		MapFrames frms = new MapFrames(this.getFrmID());
		ds.Tables.add(frms.ToDataTableField("Sys_MapFrame"));

		//附件表.
		FrmAttachments aths = new FrmAttachments(this.getFrmID());
		ds.Tables.add(aths.ToDataTableField("Sys_FrmAttachment"));

		//加入扩展属性.
		MapExts MapExts = new MapExts(this.getFrmID());
		ds.Tables.add(MapExts.ToDataTableField("Sys_MapExt"));



		if (this.getFrmID().indexOf("ND") == 0)
		{
			String nodeStr = this.getFrmID().replace("ND", "");
			if (DataType.IsNumStr(nodeStr) == true)
			{
				FrmNodeComponent fnc = new FrmNodeComponent(Integer.parseInt(nodeStr));
				//   var f = fnc.GetValFloatByKey("FWC_H");
				ds.Tables.add(fnc.ToDataTableField("WF_Node").copy());
			}
		}

		//把dataet转化成json 对象.
		return bp.tools.Json.ToJson(ds);
	}

		///#endregion

	public final String Designer_AthNew() throws Exception {
		FrmAttachment ath = new FrmAttachment();
		ath.setFrmID(this.getFrmID());
		ath.setNoOfObj(this.GetRequestVal("AthNo"));
		ath.setMyPK(ath.getFrmID() + "_" + ath.getNoOfObj());
		if (ath.RetrieveFromDBSources() == 1)
		{
			return "err@附件ID:" + ath.getNoOfObj() + "已经存在.";
		}
		CCFormAPI.CreateOrSaveAthMulti(this.getFrmID(), this.GetRequestVal("AthNo"), "我的附件");
		return ath.getMyPK();
	}

	/** 
	 生成随机的字段ID.
	 
	 @return 
	*/
	public final String GenerRandomFieldID()
	{
		String sql = "SELECT count(MyPK) as MyNum FROM Sys_MapAttr WHERE FK_MapData='" + this.getFrmID() + "'";
		int idx = DBAccess.RunSQLReturnValInt(sql);
		for (int i = idx; i < 999; i++)
		{
			String str = "F" + StringHelper.padLeft(String.valueOf(i), 3, '0');
			sql = "SELECT count(MyPK) as MyNum FROM Sys_MapAttr WHERE FK_MapData='" + this.getFrmID() + "' AND KeyOfEn='" + str + "'";
			int num= DBAccess.RunSQLReturnValInt(sql);
			if (num == 0)
			{
				return str;
			}
		}
		return "err@系统错误.";
	}

	/** 
	 初始化
	 
	 @return 
	*/
	public final String MapDefDtlFreeFrm_Init() throws Exception {
		String isFor = this.GetRequestVal("For");
		if (DataType.IsNullOrEmpty(isFor) == false)
		{
			return "sln@" + isFor;
		}

		if (this.getMapDtlNo().contains("_Ath") == true)
		{
			return "info@附件扩展";
		}
		if (this.getMapDtlNo().toUpperCase().contains("BP.WF.RETURNWORKS") == true)
		{
			return "info@退回字段扩展";
		}


		MapDtl dtl = new MapDtl();

		//如果传递来了节点信息, 就是说明了独立表单的节点方案处理, 现在就要做如下判断
		if (this.getNodeID() != 0)
		{
			dtl.setNo(this.getMapDtlNo() + "_" + this.getNodeID());

			if (dtl.RetrieveFromDBSources() == 0)
			{
				// 开始复制它的属性.
				MapAttrs mattrs = new MapAttrs(this.getMapDtlNo());
				MapDtl odtl = new MapDtl();
				odtl.setNo(this.getMapDtlNo());
				int i = odtl.RetrieveFromDBSources();
				if (i == 0)
				{
					return "info@字段列";
				}


				//存储表要与原明细表一致
				if (odtl.getPTable() == null || odtl.getPTable().isEmpty())
				{
					dtl.setPTable(odtl.getNo());
				}
				else
				{
					dtl.setPTable(odtl.getPTable());
				}

				//让其直接保存.
				dtl.setNo(this.getMapDtlNo() + "_" + this.getNodeID());
				dtl.setFrmID("Temp");
				dtl.DirectInsert(); //生成一个明细表属性的主表.

				//字段的分组也要一同复制
				HashMap<Integer, Long> groupids = new HashMap<Integer, Long>();

				//循环保存字段.
				int idx = 0;
				for (MapAttr item : mattrs.ToJavaList())
				{
					if (item.getGroupID() != 0)
					{
						if (groupids.containsKey(item.getGroupID()))
						{
							item.setGroupID(groupids.get(item.getGroupID()));
						}
						else
						{
							GroupField gf = new GroupField();
							gf.setOID(item.getGroupID());

							if (gf.RetrieveFromDBSources() == 0)
							{
								gf.setLab("默认分组");
							}

							gf.setEnName(dtl.getNo());
							gf.InsertAsNew();

							if (groupids.containsKey(item.getGroupID()) == false)
							{
								groupids.put(item.getGroupID(), gf.getOID());
							}

							item.setGroupID(gf.getOID());
						}
					}

					item.setFrmID(this.getMapDtlNo() + "_" + this.getNodeID());
					item.setMyPK(item.getFrmID() + "_" + item.getKeyOfEn());
					item.Save();
					idx++;
					item.setIdx(idx);
					item.DirectUpdate();
				}

				MapData md = new MapData();
				md.setNo("Temp");
				if (md.getIsExits())
				{
					md.setName("为权限方案设置的临时的数据");
					md.Insert();
				}
			}

			return "sln@" + dtl.getNo();
		}

		dtl.setNo(this.getMapDtlNo());
		if (dtl.RetrieveFromDBSources() == 0)
		{
			CCFormAPI.CreateOrSaveDtl(this.getFrmID(), this.getMapDtlNo(), this.getMapDtlNo());
		}
		else
		{
			CCFormAPI.CreateOrSaveDtl(this.getFrmID(), this.getMapDtlNo(), dtl.getName());
		}

		return "创建成功.";
	}


	/** 
	 构造函数
	*/
	public WF_Admin_FoolFormDesigner()
	{
	}
	/** 
	 转拼音
	 
	 @return 
	*/
	public final String ParseStringToPinyin()
	{
		String name = GetRequestVal("name");
		String flag = GetRequestVal("flag");
		//此处为字段中文转拼音，设置为最大20个字符，edited by liuxc,2017-9-25
		return CCFormAPI.ParseStringToPinyinField(name, Objects.equals(flag, "true"), true, 20);
	}
	/** 
	 增加一个枚举类型
	 
	 @return 
	*/
	public final String SysEnumList_SaveEnumField() throws Exception {
		String dtlKey = this.GetRequestVal("DtlKeyOfEn");
		MapAttr attr = new MapAttr();
		attr.setMyPK(this.getFrmID() + "_" + this.getKeyOfEn());
		if (attr.RetrieveFromDBSources() != 0)
		{
			return "err@字段名[" + this.getKeyOfEn() + "]已经存在.";
		}

		if (DataType.IsNullOrEmpty(dtlKey) == false)
		{
			attr = new MapAttr();
			attr.setMyPK(this.getFrmID() + "_" + dtlKey);
			if (attr.RetrieveFromDBSources() != 0)
			{
				return "err@字段名[" + dtlKey + "]已经存在.";
			}
		}
		attr.setMyPK(this.getFrmID() + "_" + this.getKeyOfEn());
		attr.setFrmID( this.getFrmID());
		attr.setKeyOfEn(this.getKeyOfEn());
		attr.setUIBindKey(this.GetRequestVal("EnumKey"));

		attr.setGroupID(this.GetRequestValInt("GroupFeid"));
		int uiContralType = this.GetRequestValInt("UIContralType");

		if (uiContralType != 0)
		{
			attr.setUIContralType(UIContralType.forValue(uiContralType));
			if (attr.getUIContralType() == UIContralType.RadioBtn)
			{
				attr.SetPara("RBShowModel", 3); //设置模式.
			}
		}
		else
		{
			attr.setUIContralType(UIContralType.DDL);
		}

		if (attr.getUIContralType() == UIContralType.CheckBok)
		{
			attr.setMyDataType(DataType.AppString);
		}
		else
		{
			attr.setMyDataType(DataType.AppInt);
		}
		attr.setLGType(FieldTypeS.Enum);


		SysEnumMain sem = new SysEnumMain();
		sem.setNo(attr.getUIBindKey());
		if (sem.RetrieveFromDBSources() != 0)
		{
			attr.setName(sem.getName());
		}
		else
		{
			int count = sem.Retrieve("EnumKey", attr.getUIBindKey(), "OrgNo", WebUser.getOrgNo());
			if (count != 0)
			{
				attr.setName(sem.getName());
			}
			else
			{
				attr.setName("枚举" + attr.getUIBindKey());
			}
		}


		//paras参数
		Paras ps = new Paras();
		ps.SQL = "SELECT OID FROM Sys_GroupField A WHERE A.FrmID=" + SystemConfig.getAppCenterDBVarStr() + "FrmID AND ( CtrlType='' OR CtrlType IS NULL ) ORDER BY OID DESC ";
		ps.Add("FrmID", this.getFrmID(), false);
		attr.setGroupID(DBAccess.RunSQLReturnValInt(ps, 0));
		attr.Insert();
		if (DataType.IsNullOrEmpty(dtlKey) == false)
		{
			attr.setMyPK(this.getFrmID() + "_" + dtlKey);
			attr.setKeyOfEn(dtlKey);
			String uiBindKey = sem.GetParaString("DtlEnumKey");
			attr.setUIBindKey(uiBindKey);
			attr.setName(sem.GetParaString("DtlName"));
			attr.Insert();
			//创建联动关系
			MapExt ext = new MapExt();
			String mypk = "ActiveDDL_" + this.getFrmID() + "_" + this.getKeyOfEn();
			ext.setMyPK(mypk);
			ext.setAttrsOfActive(dtlKey);
			ext.setDBType("0");
			ext.setDBSrcNo("local");
			ext.setDoc("Select IntKey as No, Lab as Name From " + bp.sys.base.Glo.SysEnum() + " Where EnumKey='" + uiBindKey + "' AND IntKey>=@Key*100 AND IntKey< (@Key*100/#100)");
			ext.setFrmID(this.getFrmID());
			ext.setAttrOfOper(this.getKeyOfEn());
			ext.setExtType("ActiveDDL");
			ext.Insert();
		}
		return this.getFrmID() + "_" + this.getKeyOfEn();
	}

	/** 
	 保存空白的分组.
	 
	 @return 
	*/
	public final String GroupField_SaveBlank() throws Exception {
		String no = this.GetValFromFrmByKey("TB_Blank_No");
		String name = this.GetValFromFrmByKey("TB_Blank_Name");

		GroupField gf = new GroupField();
		gf.setOID(this.GetRequestValInt("GroupField"));
		if (gf.getOID() != 0)
		{
			gf.Retrieve();
		}

		gf.setCtrlID(no);
		gf.setEnName(this.getFrmID());
		gf.setLab(name);
		gf.Save();
		return "保存成功.";
	}

	/** 
	 审核分组保存
	 
	 @return 
	*/
	public final String GroupField_Save() throws Exception {
		String lab = this.GetValFromFrmByKey("TB_Check_Name");
		if (lab.length() == 0)
		{
			return "err@审核角色不能为空";
		}

		String prx = this.GetValFromFrmByKey("TB_Check_No");
		if (prx.length() == 0)
		{
			prx = DataType.ParseStringToPinyin(lab);
		}

		MapAttr attr = new MapAttr();
		int i = attr.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.KeyOfEn, prx + "_Note");
		i += attr.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.KeyOfEn, prx + "_Checker");
		i += attr.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.KeyOfEn, prx + "_RDT");

		if (i > 0)
		{
			return "err@前缀已经使用：" + prx + " ， 请确认您是否增加了这个审核分组或者，请您更换其他的前缀。";
		}

		CCFormAPI.CreateCheckGroup(this.getFrmID(), lab, prx);

		return "保存成功";
	}

	/** 
	 保存分组
	 
	 @return 
	*/
	public final String GroupField_SaveCheck() throws Exception {
		String lab = this.GetRequestVal("TB_Check_Name");
		String prx = this.GetRequestVal("TB_Check_No");
		CCFormAPI.CreateCheckGroup(this.getFrmID(), lab, prx);
		return "创建成功...";
	}
	/** 
	 
	 删除分组
	 
	 @return 
	*/
	public final String GroupField_DeleteCheck() throws Exception {
		GroupField gf = new GroupField();
		gf.setOID(this.GetRequestValInt("GroupField"));
		gf.Delete();

		MapFrmFool md = new MapFrmFool(this.getFrmID());
		md.DoCheckFixFrmForUpdateVer();

		return "删除成功...";
	}

	/** 
	 
	 删除并删除该分组下的字段
	 
	 @return 
	*/
	public final String GroupField_DeleteAllCheck() throws Exception {
		MapAttrs mattrs = new MapAttrs();
		mattrs.Retrieve(MapAttrAttr.GroupID, this.GetRequestValInt("GroupField"), null);
		for (MapAttr attr : mattrs.ToJavaList())
		{
			if (attr.getHisEditType() != EditType.Edit)
			{
				continue;
			}
			if (Objects.equals(attr.getKeyOfEn(), "FID"))
			{
				continue;
			}

			attr.Delete();
		}

		GroupField gf = new GroupField();
		gf.setOID(this.GetRequestValInt("GroupField"));
		gf.Delete();

		return "删除并删除该分组下的字段成功...";
	}

	public final String ImpTableField_Step1() throws Exception {
		SFDBSrcs ens = new SFDBSrcs();
		ens.RetrieveAll();
		DataSet ds = new DataSet();
		ds.Tables.add(ens.ToDataTableField("SFDBSrcs"));
		return bp.tools.Json.ToJson(ds);
	}

	public final String getFKSFDBSrc()
	{
		return this.GetRequestVal("FK_SFDBSrc");
	}


	private String _STable = null;
	public final String getSTable() throws Exception {
		if (_STable == null)
		{
			//return this.GetRequestVal("FK_SFDBSrc");

			_STable = this.GetRequestVal("STable"); // context.Request.QueryString["STable");
			if (_STable == null || "".equals(_STable))
			{
				Entity en = ClassFactory.GetEn(this.getFrmID());
				if (en != null)
				{
					_STable = en.getEnMap().getPhysicsTable();
				}
				else
				{
					MapData md = new MapData(this.getFrmID());
					_STable = md.getPTable();
				}
			}
		}

		if (_STable == null)
		{
			_STable = "";
		}
		return _STable;
	}

	public final String ImpTableField_Step2() throws Exception {

		DataSet ds = new DataSet();

		SFDBSrc src = new SFDBSrc(this.getFKSFDBSrc());
		ds.Tables.add(src.ToDataTableField("SFDBSrc"));

		DataTable tables = src.GetTables(false);
		tables.TableName = "tables";
		ds.Tables.add(tables);

		DataTable tableColumns = src.GetColumns(this.getSTable());
		tableColumns.TableName = "columns";
		ds.Tables.add(tableColumns);

		MapAttrs attrs = new MapAttrs(this.getFrmID());
		ds.Tables.add(attrs.ToDataTableField("attrs"));
		DataTable dt = new DataTable();
		dt.TableName = "STable";
		dt.Columns.Add("STable");
		DataRow dr = dt.NewRow();
		dr.setValue("STable", this.getSTable());
		dt.Rows.add(dr);
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}
	/** 
	 列
	*/
	private ArrayList<String> sCols = null;
	/** 
	 列
	*/
	public final ArrayList<String> getSColumns()
	{
		if (sCols != null)
		{
			return sCols;
		}

		String tempVar = this.GetRequestVal("SColumns");
		String cols = tempVar != null ? tempVar : "";
		String[] arr = cols.split("[,]", -1);
		sCols = new ArrayList<String>();

		for (String s : arr)
		{
			if (s == null || s.isEmpty())
			{
				continue;
			}

			sCols.add(s);
		}

		return sCols;
	}
	/** 
	 导入步骤
	 
	 @return 
	*/
	public final String ImpTableField_Step3() throws Exception {
		DataSet ds = new DataSet();
		SFDBSrc src = new SFDBSrc(this.getFKSFDBSrc());
		DataTable tableColumns = src.GetColumns(this.getSTable());
		DataTable dt = tableColumns.clone();
		dt.TableName = "selectedColumns";
		for (DataRow dr : tableColumns.Rows)
		{
			if (this.getSColumns().contains(dr.getValue("No")))
			{
				dt.Rows.add(dr);
			}
		}
		ds.Tables.add(dt);
		SysEnums ens = new SysEnums(MapAttrAttr.MyDataType);
		ds.Tables.add(ens.ToDataTableField("MyDataType"));
		SysEnums ens1 = new SysEnums(MapAttrAttr.LGType);
		ds.Tables.add(ens1.ToDataTableField("LGType"));
		return bp.tools.Json.ToJson(ds);
	}
	/** 
	 保存字段
	 
	 @return 
	*/
	public final String ImpTableField_Save() throws Exception {
		MapData md = new MapData();
		md.setNo(this.getFrmID());
		md.RetrieveFromDBSources();

		String msg = md.getName() +"导入字段信息:" + this.getFrmID();
		boolean isLeft = true;
		// float maxEnd = md.MaxEnd;

		int iGroupID = 0;

		Paras ps = new Paras();
		ps.SQL = "SELECT OID FROM Sys_GroupField WHERE FrmID=" + SystemConfig.getAppCenterDBVarStr() + "FrmID and (CtrlType is null or CtrlType ='') ORDER BY OID DESC ";
		ps.Add("FrmID", this.getFrmID(), false);
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (dt != null && dt.Rows.size() > 0)
		{
			iGroupID = Integer.parseInt(dt.Rows.get(0).getValue(0).toString());
		}

		for (String name : ContextHolderUtils.getRequest().getParameterMap().keySet())
		{
			if (name.startsWith("HID_Idx_") == false)
			{
				continue;
			}

			String columnName = name.substring("HID_Idx_".length());

			MapAttr ma = new MapAttr();
			ma.setKeyOfEn(columnName);
			ma.setFrmID(this.getFrmID());
			ma.setMyPK(this.getFrmID() + "_" + ma.getKeyOfEn());
			if (ma.getIsExits())
			{
				msg += "\t\n字段:" + ma.getKeyOfEn() + " - " + ma.getName() +"已存在.";
				continue;
			}

			ma.setName(this.GetValFromFrmByKey("TB_Desc_" + columnName));
			if (DataType.IsNullOrEmpty(ma.getName()))
			{
				ma.setName(ma.getKeyOfEn());
			}

			ma.setMyDataType(this.GetValIntFromFrmByKey("DDL_DBType_" + columnName));

			//翻译过去.
			String len = this.GetValFromFrmByKey("TB_Len_" + columnName);
			if (len.equals("null") || DataType.IsNullOrEmpty(len) == true)
			{
				len = "20";
			}

			try
			{
				int mylen = Integer.parseInt(len);
				if (mylen > 4000)
				{
					mylen = 0;
					//ma.isu = true;
				}


				ma.setMaxLen(mylen);
			}
			catch (RuntimeException e)
			{
				ma.setMaxLen(0);
				//throw new Exception("err@转化为最大长度的时候错误:" + ma.getKeyOfEn() + " len:" + len);
			}

			ma.setUIBindKey(this.GetValFromFrmByKey("TB_BindKey_" + columnName));
			ma.setLGType(FieldTypeS.Normal);
			ma.setGroupID(iGroupID);

			//绑定了外键或者枚举.
			if (DataType.IsNullOrEmpty(ma.getUIBindKey()) == false)
			{
				SysEnums se = new SysEnums();
				se.Retrieve(SysEnumAttr.EnumKey, ma.getUIBindKey(), null);
				if (!se.isEmpty())
				{
					ma.setMyDataType(DataType.AppInt);
					ma.setLGType(FieldTypeS.Enum);
					ma.setUIContralType(UIContralType.DDL);
				}
				SFTable tb = new SFTable();
				tb.setNo(ma.getUIBindKey());
				if (tb.getIsExits())
				{
					ma.setMyDataType(DataType.AppString);
					ma.setLGType(FieldTypeS.FK);
					ma.setUIContralType(UIContralType.DDL);
				}
			}

			if (ma.getMyDataType() == DataType.AppBoolean)
			{
				ma.setUIContralType( UIContralType.CheckBok);
			}

			ma.Insert();

			msg += "\t\n字段:" + ma.getKeyOfEn() + " - " + ma.getName() +"加入成功.";


			isLeft = !isLeft;
		}

		//更新名称.
		DBAccess.RunSQL("UPDATE Sys_MapAttr SET Name=KeyOfEn WHERE Name=NULL OR Name='' ");
		return msg;
	}
	/** 
	 框架信息.
	 
	 @return 
	*/
	public final String MapFrame_Init() throws Exception {
		MapFrame mf = new MapFrame();
		mf.setFrmID(this.getFrmID());

		if (this.getMyPK() == null)
		{
			mf.setURL("http://ccbpm.cn");
			mf.setW(400);
			mf.setH(300);
			mf.setName("我的框架.");
			mf.setFrmID(this.getFrmID());
			mf.setMyPK(DBAccess.GenerGUID(0, null, null));
		}
		else
		{
			mf.setMyPK(this.getMyPK());
			mf.RetrieveFromDBSources();
		}
		return mf.ToJson(true);
	}
	/** 
	 框架信息保存.
	 
	 @return 
	*/
	public final String MapFrame_Save() throws Exception {
		MapFrame mf = new MapFrame();
		Object tempVar = bp.pub.PubClass.CopyFromRequestByPost(mf);
		mf = tempVar instanceof MapFrame ? (MapFrame)tempVar : null;
		mf.setFrmID(this.getFrmID());

		mf.Save(); //执行保存.
		return "保存成功..";
	}


		///#region SFList 外键表列表.
	/** 
	 删除
	 
	 @return 
	*/
	public final String SFList_Delete()
	{
		try
		{
			SFTable sf = new SFTable(this.getFK_SFTable());
			sf.Delete();
			return "删除成功...";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/** 
	 字典表列表.
	 
	 @return 
	*/
	public final String SFList_Init() throws Exception {
		DataSet ds = new DataSet();
		DataTable dt = null;
		SFTables ens = new SFTables();
		//获取关键字
		String Key = this.GetRequestVal("Key");
		//如果关键之不为空，直接查询
		if (Key == null || Key.isEmpty())
		{
			ens.RetrieveAll();
			dt = ens.ToDataTableField("SFTables");
			ds.Tables.add(dt);
		} //如果关键字为空，按条件查询
		else
		{
			QueryObject ob = new QueryObject(ens);
			// 查询条件示例：where No like '%Key%' or Name like '%Key%'
			ob.AddWhere(SFTableAttr.No, "like", "%" + Key + "%");
			ob.addOr();
			ob.AddWhere(SFTableAttr.Name, "like", "%" + Key + "%");
			//根据No 排序
			ob.addOrderBy(SFTableAttr.No);
			//返回对象为dt
			dt = ob.DoQueryToTable();
			dt.TableName = "SFTables";
			ds.Tables.add(dt);
		}

		int pTableModel = 0;
		if (this.GetRequestVal("PTableModel").equals("2"))
		{
			pTableModel = 2;
		}

		//获得ptableModel.
		if (pTableModel == 0)
		{
			MapDtl dtl = new MapDtl();
			dtl.setNo(this.getFrmID());
			if (dtl.RetrieveFromDBSources() == 1)
			{
				pTableModel = dtl.getPTableModel();
			}
			else
			{
				MapData md = new MapData();
				md.setNo(this.getFrmID());
				if (md.RetrieveFromDBSources() == 1)
				{
					pTableModel = md.getPTableModel();
				}
			}
		}

		if (pTableModel == 2)
		{
			DataTable mydt = MapData.GetFieldsOfPTableMode2(this.getFrmID());
			mydt.TableName = "Fields";
			ds.Tables.add(mydt);
		}

		return bp.tools.Json.ToJson(ds);
	}
	public final String SFList_SaveSFField() throws Exception {
		MapAttr attr = new MapAttr();
		attr.setMyPK(this.getFrmID() + "_" + this.getKeyOfEn());
		if (attr.RetrieveFromDBSources() != 0)
		{
			return "err@字段名[" + this.getKeyOfEn() + "]已经存在.";
		}

		CCFormAPI.SaveFieldSFTable(this.getFrmID(), this.getKeyOfEn(), null, this.GetRequestVal("SFTable"), 100, 100, 1);

		attr.Retrieve();
		Paras ps = new Paras();
		ps.SQL = "SELECT OID FROM Sys_GroupField A WHERE A.FrmID=" + SystemConfig.getAppCenterDBVarStr() + "FrmID AND (CtrlType='' OR CtrlType IS NULL) ORDER BY OID DESC ";
		ps.Add("FrmID", this.getFrmID(), false);
		attr.setGroupID(DBAccess.RunSQLReturnValInt(ps, 0));
		attr.Update();

		SFTable sf = new SFTable(attr.getUIBindKey());

		if (Objects.equals(sf.getSrcType(), DictSrcType.TableOrView) || Objects.equals(sf.getSrcType(), DictSrcType.BPClass) || Objects.equals(sf.getSrcType(), DictSrcType.CreateTable))
		{
			return "../../Comm/En.htm?EnName=BP.Sys.FrmUI.MapAttrSFTable&PKVal=" + attr.getMyPK();
		}
		else
		{
			return "../../Comm/En.htm?EnName=BP.Sys.FrmUI.MapAttrSFSQL&PKVal=" + attr.getMyPK();
		}
	}

		///#endregion 外键表列表.

	/** 
	 初始化表.
	 
	 @return 
	*/
	public final String EditTableField_Init() throws Exception {
		MapAttr attr = new MapAttr();
		attr.setKeyOfEn(this.getKeyOfEn());
		attr.setFrmID( this.getFrmID());

		if (DataType.IsNullOrEmpty(this.getMyPK()) == false)
		{
			attr.setMyPK(this.getMyPK());
			attr.RetrieveFromDBSources();
		}
		else
		{
			SFTable sf = new SFTable(this.getFK_SFTable());
			attr.setName(sf.getName());
			attr.setKeyOfEn(sf.getNo());
		}

		//第1次加载.
		attr.setUIContralType(UIContralType.DDL);

		attr.setFrmID( this.getFrmID());

		//字体大小.
		int size = attr.getParaFontSize();
		if (size == 0)
		{
			attr.setParaFontSize(12);
		}

		//横跨的列数.
		if (attr.getColSpan() == 0)
		{
			attr.setColSpan(1);
		}

		return attr.ToJson(true);
	}
	/** 
	 从表里选择字段.
	 
	 @return 
	*/
	public final String FieldTypeListChoseOneField_Init() throws Exception {
		String ptable = "";

		MapDtl dtl = new MapDtl();
		dtl.setNo(this.getFrmID());
		if (dtl.RetrieveFromDBSources() == 1)
		{
			ptable = dtl.getPTable();
		}
		else
		{
			MapData md = new MapData(this.getFrmID());
			ptable = md.getPTable();
		}

		//获得原始数据.
		DataTable dt = DBAccess.GetTableSchema(ptable);

		//创建样本.
		DataTable mydt = DBAccess.GetTableSchema(ptable);
		mydt.Rows.clear();

		//获得现有的列..
		MapAttrs attrs = new MapAttrs(this.getFrmID());

		String flowFiels = ",GUID,PRI,PrjNo,PrjName,PEmp,AtPara,FlowNote,WFSta,PNodeID,FK_FlowSort,FK_Flow,OID,FID,Title,WFState,CDT,FlowStarter,FlowStartRDT,FK_Dept,FK_NY,FlowDaySpan,FlowEmps,FlowEnder,FlowEnderRDT,FlowEndNode,PWorkID,PFlowNo,BillNo,ProjNo,";

		//排除已经存在的列.
		for (DataRow dr : dt.Rows)
		{
			String key = dr.getValue("FName").toString();
			if (attrs.contains(MapAttrAttr.KeyOfEn, key) == true)
			{
				continue;
			}

			if (flowFiels.contains("," + key + ",") == true)
			{
				continue;
			}

			DataRow mydr = mydt.NewRow();
			mydr.setValue("FName", dr.getValue("FName"));
			mydr.setValue("FType", dr.getValue("FType"));
			mydr.setValue("FLen", dr.getValue("FLen"));
			mydr.setValue("FDesc", dr.getValue("FDesc"));
			mydt.Rows.add(mydr);
		}

		mydt.TableName = "dt";
		return bp.tools.Json.ToJson(mydt);
	}
	public final String FieldTypeListChoseOneField_Save() throws Exception {
		int dataType = this.GetRequestValInt("DataType");
		String keyOfEn = this.GetRequestVal("KeyOfEn");
		String name = this.GetRequestVal("FDesc");
		String frmID = this.GetRequestVal("FK_MapData");

		MapAttr attr = new MapAttr();
		attr.setFrmID( frmID);
		attr.setKeyOfEn(keyOfEn);
		attr.setMyPK(attr.getFrmID() + "_" + keyOfEn);
		if (attr.IsExits())
		{
			return "err@该字段[" + keyOfEn + "]已经加入里面了.";
		}

		attr.setName(name);
		attr.setMyDataType(dataType);

		if (DataType.AppBoolean == dataType)
		{
			attr.setUIContralType(UIContralType.CheckBok);
		}
		else
		{
			attr.setUIContralType(UIContralType.TB);
		}

		//Paras ps = new Paras();
		//ps.SQL = "SELECT OID FROM Sys_GroupField A WHERE A.FrmID=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "FrmID AND  ( CtrlType='' OR CtrlType= NULL ) ";
		// ps.Add("FrmID", this.FrmID);
		String sql = "SELECT OID FROM Sys_GroupField A WHERE A.FrmID='" + this.getFrmID() + "' AND (CtrlType='' OR CtrlType= NULL) ";
		attr.setGroupID(DBAccess.RunSQLReturnValInt(sql, 0));
		attr.Insert();

		return "保存成功.";
	}
	/** 
	 字段选择.
	 
	 @return 
	*/
	public final String FieldTypeSelect_Create() throws Exception {
		String no = this.GetRequestVal("KeyOfEn");
		if (Objects.equals(no, "No"))
		{
			no = "No1";
		}

		String name = this.GetRequestVal("name");
		String newNo = DataType.ParseStringForNo(no, 0);
		//String newName = DataType.ParseStringForName(name, 0);
		String newName = name;
		int fType = Integer.parseInt(this.GetRequestVal("FType"));
		boolean isSupperText = this.GetRequestValBoolen("IsSupperText");

		MapAttrs attrs = new MapAttrs();
		int i = attrs.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.KeyOfEn, newNo, null);
		if (i != 0)
		{
			return "err@字段名：" + newNo + "已经存在.";
		}


			///#region 计算GroupID  需要翻译
		int iGroupID = this.getGroupField();
		try
		{
			Paras ps = new Paras();
			ps.SQL = "SELECT OID FROM Sys_GroupField WHERE FrmID=" + SystemConfig.getAppCenterDBVarStr() + "FrmID and (CtrlType is null or CtrlType ='') ORDER BY OID DESC ";
			ps.Add("FrmID", this.getFrmID(), false);
			DataTable dt = DBAccess.RunSQLReturnTable(ps);
			if (dt != null && dt.Rows.size() > 0)
			{
				iGroupID = Integer.parseInt(dt.Rows.get(0).getValue(0).toString());
			}
		}
		catch (RuntimeException ex)
		{

		}

			///#endregion

		try
		{
			MapData md = new MapData();
			md.setNo(this.getFrmID());
			if (md.RetrieveFromDBSources() != 0)
			{
				md.CheckPTableSaveModel(newNo);
			}
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}

		//求出选择的字段类型.
		MapAttr attr = new MapAttr();
		attr.setName(newName);
		attr.setKeyOfEn(newNo);
		attr.setFrmID( this.getFrmID());
		attr.setLGType(FieldTypeS.Normal);
		attr.setMyPK(this.getFrmID() + "_" + newNo);
		attr.setGroupID(iGroupID);
		attr.setMyDataType(fType);

		if (DataType.IsNullOrEmpty(this.GetRequestVal("FK_Flow")) == false)
		{
			attr.SetPara("FK_Flow", this.GetRequestVal("FK_Flow"));
		}

		int colspan = attr.getColSpan();
		attr.setParaFontSize(12);
		int rows = attr.getUIRows();

		if (attr.getMyDataType() == DataType.AppString)
		{

			UIContralType uiContralType = UIContralType.forValue(this.GetRequestValInt("UIContralType"));

			attr.setUIWidth(100);
			attr.setUIHeight(23);
			if (uiContralType == UIContralType.SignCheck || uiContralType == UIContralType.FlowBBS)
			{
				attr.setUIIsEnable(false);
				attr.setUIVisible(true);
			}
			else
			{
				attr.setUIVisible(true);
				attr.setUIIsEnable(true);
			}

			attr.setColSpan(1);
			attr.setMinLen(0);
			attr.setMaxLen(50);
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(uiContralType);
			attr.Insert();

			if (isSupperText == true)
			{
				MapAttrString attrString = new MapAttrString(attr.getMyPK());
				attrString.setItIsSupperText(true);
				attrString.Update();
			}
			return "url@../../Comm/En.htm?EnName=BP.Sys.FrmUI.MapAttrString&MyPK=" + attr.getMyPK();
		}

		if (attr.getMyDataType() == DataType.AppInt)
		{
			attr.setUIWidth(100);
			attr.setUIHeight(23);
			attr.setUIVisible(true);
			attr.setUIIsEnable(true);
			attr.setColSpan(1);
			attr.setMinLen(0);
			attr.setMaxLen(50);
			attr.setMyDataType(DataType.AppInt);
			attr.setUIContralType(UIContralType.TB);
			attr.setDefVal("0");
			attr.Insert();

			return "url@../../Comm/En.htm?EnName=BP.Sys.FrmUI.MapAttrNum&MyPK=" + attr.getMyPK() + "&FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + newNo + "&FType=" + attr.getMyDataType() + "&DoType=Edit&GroupField=" + this.getGroupField();
		}

		if (attr.getMyDataType() == DataType.AppMoney)
		{
			attr.setUIWidth(100);
			attr.setUIHeight(23);
			attr.setUIVisible(true);
			attr.setUIIsEnable(true);
			attr.setColSpan(1);
			attr.setMinLen(0);
			attr.setMaxLen(50);
			attr.setMyDataType(DataType.AppMoney);
			attr.setUIContralType(UIContralType.TB);
			attr.setDefVal("0.00");
			attr.Insert();
			return "url@../../Comm/En.htm?EnName=BP.Sys.FrmUI.MapAttrNum&MyPK=" + attr.getMyPK() + "&FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + newNo + "&FType=" + attr.getMyDataType() + "&DoType=Edit&GroupField=" + this.getGroupField();
		}

		if (attr.getMyDataType() == DataType.AppFloat)
		{
			attr.setUIWidth(100);
			attr.setUIHeight(23);
			attr.setUIVisible(true);
			attr.setUIIsEnable(true);
			attr.setColSpan(1);
			attr.setMinLen(0);
			attr.setMaxLen(50);
			attr.setMyDataType(DataType.AppFloat);
			attr.setUIContralType(UIContralType.TB);

			attr.setDefVal("0");
			attr.Insert();

			return "url@../../Comm/En.htm?EnName=BP.Sys.FrmUI.MapAttrNum&MyPK=" + attr.getMyPK() + "&FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + newNo + "&FType=" + attr.getMyDataType() + "&DoType=Edit&GroupField=" + this.getGroupField();
		}

		if (attr.getMyDataType() == DataType.AppDouble)
		{
			attr.setUIWidth(100);
			attr.setUIHeight(23);
			attr.setUIVisible(true);
			attr.setUIIsEnable(true);
			attr.setColSpan(1);
			attr.setMinLen(0);
			attr.setMaxLen(50);
			attr.setMyDataType(DataType.AppDouble);
			attr.setUIContralType(UIContralType.TB);
			attr.setDefVal("0");
			attr.Insert();

			return "url@../../Comm/En.htm?EnName=BP.Sys.FrmUI.MapAttrNum&MyPK=" + attr.getMyPK() + "&FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + newNo + "&FType=" + attr.getMyDataType() + "&DoType=Edit&GroupField=" + this.getGroupField();
		}

		if (attr.getMyDataType() == DataType.AppDate)
		{

			attr.setUIWidth(100);
			attr.setUIHeight(23);
			attr.setUIVisible(true);
			attr.setUIIsEnable(true);
			attr.setColSpan(1);
			attr.setMinLen(0);
			attr.setMaxLen(50);
			attr.setUIContralType(UIContralType.TB);
			attr.setMyDataType(DataType.AppDate);
			attr.Insert();

			bp.sys.frmui.MapAttrDT dt = new bp.sys.frmui.MapAttrDT();
			dt.setMyPK(attr.getMyPK());
			dt.RetrieveFromDBSources();
			dt.setFormat(0);
			dt.Update();


			return "url@../../Comm/En.htm?EnName=BP.Sys.FrmUI.MapAttrDT&MyPK=" + attr.getMyPK() + "&FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + newNo + "&FType=" + attr.getMyDataType() + "&DoType=Edit&GroupField=" + this.getGroupField();
		}

		if (attr.getMyDataType() == DataType.AppDateTime)
		{
			attr.setUIWidth(100);
			attr.setUIHeight(23);
			attr.setUIVisible(true);
			attr.setUIIsEnable(true);
			attr.setColSpan(1);
			attr.setMinLen(0);
			attr.setMaxLen(50);
			attr.setUIContralType(UIContralType.TB);
			attr.setMyDataType(DataType.AppDateTime);
			attr.Insert();

			bp.sys.frmui.MapAttrDT dt = new bp.sys.frmui.MapAttrDT();
			dt.setMyPK(attr.getMyPK());
			dt.RetrieveFromDBSources();
			dt.setFormat( 1);
			dt.Update();

			return "url@../../Comm/En.htm?EnName=BP.Sys.FrmUI.MapAttrDT&MyPK=" + attr.getMyPK() + "&FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + newNo + "&FType=" + attr.getMyDataType() + "&DoType=Edit&GroupField=" + this.getGroupField();
		}

		if (attr.getMyDataType() == DataType.AppBoolean)
		{
			attr.setUIWidth(100);
			attr.setUIHeight(23);
			attr.setUIVisible(true);
			attr.setUIIsEnable(true);
			attr.setColSpan(1);
			attr.setMinLen(0);
			attr.setMaxLen(50);
			attr.setUIContralType(UIContralType.CheckBok);
			attr.setMyDataType(DataType.AppBoolean);
			attr.setDefVal("0");
			attr.Insert();

			return "url@../../Comm/En.htm?EnName=BP.Sys.FrmUI.MapAttrBoolen&MyPK=" + attr.getMyPK() + "&FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + newNo + "&FType=" + attr.getMyDataType() + "&DoType=Edit&GroupField=" + this.getGroupField();
		}

		return "err@没有判断的字段数据类型." + attr.getMyDataTypeStr();
	}
	/** 
	 字段初始化数据.
	 
	 @return 
	*/
	public final String EditF_FieldInit() throws Exception {
		MapAttr attr = new MapAttr();
		attr.setKeyOfEn(this.getKeyOfEn());
		attr.setFrmID( this.getFrmID());

		if (DataType.IsNullOrEmpty(this.getMyPK()) == false)
		{
			attr.setMyPK(this.getMyPK());
			attr.RetrieveFromDBSources();
		}
		else
		{
			attr.setGroupID(this.getGroupField());
		}

		attr.setFrmID( this.getFrmID());

		//字体大小.
		int size = attr.getParaFontSize();
		if (size == 0)
		{
			attr.setParaFontSize(12);
		}


		//横跨的列数.
		if (attr.getColSpan() == 0)
		{
			attr.setColSpan(1);
		}

		return attr.ToJson(true);
	}
	public final String FieldInitEnum() throws Exception {
		MapAttr attr = new MapAttr();
		attr.setKeyOfEn(this.getKeyOfEn());
		attr.setFrmID( this.getFrmID());

		if (DataType.IsNullOrEmpty(this.getMyPK()) == false)
		{
			attr.setMyPK(this.getMyPK());
			attr.RetrieveFromDBSources();
		}
		else
		{
			SysEnumMain sem = new SysEnumMain(this.getEnumKey());
			attr.setName(sem.getName());
			attr.setKeyOfEn(sem.getNo());
			attr.setDefVal("0");
		}

		//第1次加载.
		if (attr.getUIContralType() == UIContralType.TB)
		{
			attr.setUIContralType(UIContralType.DDL);
		}

		attr.setFrmID( this.getFrmID());

		//字体大小.
		int size = attr.getParaFontSize();
		if (size == 0)
		{
			attr.setParaFontSize(12);
		}

		//横跨的列数.
		if (attr.getColSpan() == 0)
		{
			attr.setColSpan(1);
		}

		//int model = attr.getRBShowModel();
		//attr.RBShowModel = model;

		return attr.ToJson(true);
	}
	/** 
	 转化成json
	 
	 @return 
	*/
	public final String FieldInitGroupID() throws Exception {
		GroupFields gfs = new GroupFields(this.getFrmID());

		//转化成json输出.
		return gfs.ToJson("dt");
	}
	/** 
	 分组&枚举： 两个数据源.
	 
	 @return 
	*/
	public final String FieldInitGroupAndSysEnum() throws Exception {
		GroupFields gfs = new GroupFields(this.getFrmID());

		//分组值.
		DataSet ds = new DataSet();
		ds.Tables.add(gfs.ToDataTableField("Sys_GroupField"));

		//枚举值.
		String enumKey = this.getEnumKey();
		if (DataType.IsNullOrEmpty(enumKey) == true || enumKey.equals("null") == true)
		{
			MapAttr ma = new MapAttr(this.getMyPK());
			enumKey = ma.getUIBindKey();
		}

		SysEnums enums = new SysEnums(enumKey);
		ds.Tables.add(enums.ToDataTableField("Sys_Enum"));

		//转化成json输出.
		String json = bp.tools.Json.ToJson(ds);
		// DataType.WriteFile("c:\\FieldInitGroupAndSysEnum.json", json);
		return json;
	}

	/** 
	 保存枚举值.
	 
	 @return 
	*/
	public final String FieldSaveEnum()
	{
		try
		{
			//定义变量.
			if (this.getEnumKey() == null)
			{
				return "err@没有接收到EnumKey的值，无法进行保存操作。";
			}


			//@周朋 , 判断数据模式，创建的字段是否符合要求.
			MapData md = new MapData(this.getFrmID());
			md.CheckPTableSaveModel(this.getKeyOfEn());



			//赋值.
			MapAttr attr = new MapAttr();
			attr.setKeyOfEn(this.getKeyOfEn());
			attr.setFrmID( this.getFrmID());
			if (DataType.IsNullOrEmpty(this.getMyPK()) == false)
			{
				attr.setMyPK(this.getMyPK());
				attr.RetrieveFromDBSources();
			}
			else
			{
				/*判断字段是否存在？*/
				if (attr.IsExit(MapAttrAttr.KeyOfEn, this.getKeyOfEn(), MapAttrAttr.FK_MapData, this.getFrmID()) == true)
				{
					return "err@字段名:" + this.getKeyOfEn() + "已经存在.";
				}
			}

			attr.setKeyOfEn(this.getKeyOfEn());
			attr.setFrmID( this.getFrmID());
			attr.setLGType(FieldTypeS.Enum);
			attr.setUIBindKey(this.getEnumKey());
			attr.setMyDataType(DataType.AppInt);

			//控件类型.
			attr.setUIContralType(UIContralType.DDL);

			attr.setName(this.GetValFromFrmByKey("TB_Name"));
			attr.setKeyOfEn(this.GetValFromFrmByKey("TB_KeyOfEn"));
			attr.setColSpan(this.GetValIntFromFrmByKey("DDL_ColSpan"));
			if (attr.getColSpan() == 0)
			{
				attr.setColSpan(1);
			}

			//显示方式.
			attr.setRBShowModel(this.GetValIntFromFrmByKey("DDL_RBShowModel"));

			//控件类型.
			attr.setUIContralType(UIContralType.forValue(this.GetValIntFromFrmByKey("RB_CtrlType")));

			attr.setUIIsInput(this.GetValBoolenFromFrmByKey("CB_IsInput")); //是否是必填项.

			attr.setItIsEnableJS(this.GetValBoolenFromFrmByKey("CB_IsEnableJS")); //是否启用js设置？

			attr.setParaFontSize(this.GetValIntFromFrmByKey("TB_FontSize")); //字体大小.

			//默认值.
			attr.setDefVal(this.GetValFromFrmByKey("TB_DefVal"));

			try
			{
				//分组.
				if (this.GetValIntFromFrmByKey("DDL_GroupID") != 0)
				{
					attr.setGroupID(this.GetValIntFromFrmByKey("DDL_GroupID")); //在那个分组里？
				}
			}
			catch (java.lang.Exception e)
			{

			}

			//是否可用？所有类型的属性，都需要。
			int isEnable = this.GetValIntFromFrmByKey("RB_UIIsEnable");
			if (isEnable == 0)
			{
				attr.setUIIsEnable(false);
			}
			else
			{
				attr.setUIIsEnable(true);
			}

			//是否可见?
			int visable = this.GetValIntFromFrmByKey("RB_UIVisible");
			if (visable == 0)
			{
				attr.setUIVisible(false);
			}
			else
			{
				attr.setUIVisible(true);
			}

			attr.setMyPK(this.getFrmID() + "_" + this.getKeyOfEn());

			attr.Save();

			return "保存成功.";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/** 
	 保存外键表字段.
	 
	 @return 
	*/
	public final String EditTableField_Save()
	{
		try
		{
			//定义变量.
			if (this.getFK_SFTable() == null)
			{
				return "err@没有接收到FK_SFTable的值，无法进行保存操作。";
			}

			//赋值.
			MapAttr attr = new MapAttr();
			attr.setKeyOfEn(this.getKeyOfEn());
			attr.setFrmID( this.getFrmID());
			if (DataType.IsNullOrEmpty(this.getMyPK()) == false)
			{
				attr.setMyPK(this.getMyPK());
				attr.RetrieveFromDBSources();
			}
			else
			{
				/*判断字段是否存在？*/
				if (attr.IsExit(MapAttrAttr.KeyOfEn, this.getKeyOfEn(), MapAttrAttr.FK_MapData, this.getFrmID()) == true)
				{
					return "err@字段名:" + this.getKeyOfEn() + "已经存在.";
				}
			}

			attr.setKeyOfEn(this.getKeyOfEn());
			attr.setFrmID( this.getFrmID());
			attr.setLGType(FieldTypeS.FK);
			attr.setUIBindKey(this.getFK_SFTable());
			attr.setMyDataType(DataType.AppString);

			//控件类型.
			attr.setUIContralType(UIContralType.DDL);

			attr.setName(this.GetValFromFrmByKey("TB_Name"));
			attr.setKeyOfEn(this.GetValFromFrmByKey("TB_KeyOfEn"));
			attr.setColSpan(this.GetValIntFromFrmByKey("DDL_ColSpan"));
			if (attr.getColSpan() == 0)
			{
				attr.setColSpan(1);
			}

			attr.setUIIsInput(this.GetValBoolenFromFrmByKey("CB_IsInput")); //是否是必填项.

			attr.setParaFontSize(this.GetValIntFromFrmByKey("TB_FontSize")); //字体大小.

			//默认值.
			attr.setDefVal(this.GetValFromFrmByKey("TB_DefVal"));

			try
			{
				//分组.
				if (this.GetValIntFromFrmByKey("DDL_GroupID") != 0)
				{
					attr.setGroupID(this.GetValIntFromFrmByKey("DDL_GroupID")); //在那个分组里？
				}
			}
			catch (java.lang.Exception e)
			{

			}

			//是否可用？所有类型的属性，都需要。
			int isEnable = this.GetValIntFromFrmByKey("RB_UIIsEnable");
			if (isEnable == 0)
			{
				attr.setUIIsEnable(false);
			}
			else
			{
				attr.setUIIsEnable(true);
			}

			//是否可见?
			int visable = this.GetValIntFromFrmByKey("RB_UIVisible");
			if (visable == 0)
			{
				attr.setUIVisible(false);
			}
			else
			{
				attr.setUIVisible(true);
			}

			attr.setMyPK(this.getFrmID() + "_" + this.getKeyOfEn());
			attr.Save();

			return "保存成功.";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/** 
	 执行保存.
	 
	 @return 
	*/
	//public String EditF_Save()
	//{
	//    try
	//    {
	//        //定义变量.
	//        int fType = int.Parse(this.GetRequestVal("FType"));  //字段数据物理类型
	//        FieldTypeS lgType = (FieldTypeS)int.Parse(this.GetRequestVal("LGType")); //逻辑类型.
	//        String uiBindKey = this.GetRequestVal("UIBindKey");// context.Request.QueryString["UIBindKey");

	//        //赋值.
	//        MapAttr attr = new MapAttr();
	//        attr.setKeyOfEn(this.KeyOfEn);
	//        attr.setFrmID(this.FrmID;
	//        attr.setLGType(lgType; //逻辑类型.
	//        attr.setUIBindKey(uiBindKey; //绑定的枚举或者外键.
	//        attr.setMyDataType(fType; //物理类型.

	//        if (DataType.IsNullOrEmpty(this.MyPK) == false)
	//        {
	//            attr.setMyPK(this.MyPK);
	//            attr.RetrieveFromDBSources();
	//        }

	//        attr.setFrmID(this.FrmID;
	//        attr.setMyDataType(fType; //数据类型.
	//        attr.setName(this.GetValFromFrmByKey("TB_Name");

	//        attr.setKeyOfEn(this.GetValFromFrmByKey("TB_KeyOfEn"));
	//        attr.ColSpan = this.GetValIntFromFrmByKey("DDL_ColSpan");

	//        if (attr.getColSpan() == 0)
	//            attr.setColSpan(1);

	//        attr.setParaFontSize(this.GetValIntFromFrmByKey("TB_FontSize"); //字体大小.
	//        attr.Para_Tip = this.GetValFromFrmByKey("TB_Tip"); //操作提示.

	//        //默认值.
	//        attr.DefVal = this.GetValFromFrmByKey("TB_DefVal");


	//        //对于明细表就可能没有值.
	//        try
	//        {
	//            //分组.
	//            if (this.GetValIntFromFrmByKey("DDL_GroupID") != 0)
	//                attr.setGroupID(this.GetValIntFromFrmByKey("DDL_GroupID"); //在那个分组里？
	//        }
	//        catch
	//        {

	//        }


	//        //把必填项拿出来，所有字段都可以设置成必填项 杨玉慧
	//        attr.UIIsInput = this.GetValBoolenFromFrmByKey("CB_IsInput");   //是否是必填项.

	//        if (attr.getMyDataType() == DataType.AppString && lgType == FieldTypeS.Normal)
	//        {
	//            attr.IsRichText = this.GetValBoolenFromFrmByKey("CB_IsRichText"); //是否是富文本？
	//            attr.IsSupperText = this.GetValIntFromFrmByKey("CB_IsSupperText"); //是否是超大文本？


	//            //高度.
	//            attr.UIHeightInt = this.GetValIntFromFrmByKey("DDL_Rows") * 23;

	//            //最大最小长度.
	//            attr.setMaxLen(this.GetValIntFromFrmByKey("TB_MaxLen"));
	//            attr.setMinLen(this.GetValIntFromFrmByKey("TB_MinLen"));

	//            attr.setUIWidth(this.GetValIntFromFrmByKey("TB_UIWidth"); //宽度.
	//        }

	//        switch (attr.getMyDataType())
	//        {
	//            case DataType.AppInt:
	//            case DataType.AppFloat:
	//            case DataType.AppDouble:
	//            case DataType.AppMoney:
	//                attr.IsSum = this.GetValBoolenFromFrmByKey("CB_IsSum");
	//                break;
	//        }

	//        //获取宽度.
	//        try
	//        {
	//            attr.setUIWidth(this.GetValIntFromFrmByKey("TB_UIWidth"); //宽度.
	//        }
	//        catch
	//        {
	//        }


	//        //是否可用？所有类型的属性，都需要。
	//        int isEnable = this.GetValIntFromFrmByKey("RB_UIIsEnable");
	//        if (isEnable == 0)
	//            attr.setUIIsEnable(false);
	//        else
	//            attr.setUIIsEnable(true);

	//        //仅仅对普通类型的字段需要.
	//        if (lgType == FieldTypeS.Normal)
	//        {
	//            //是否可见?
	//            int visable = this.GetValIntFromFrmByKey("RB_UIVisible");
	//            if (visable == 0)
	//                attr.setUIVisible(false);
	//            else
	//                attr.setUIVisible(true);
	//        }

	//        attr.setMyPK(this.getFrmID() + "_" + this.KeyOfEn);
	//        attr.Save();

	//        return "保存成功.";
	//    }
	//    catch (Exception ex)
	//    {
	//        return ex.Message;
	//    }
	//}
	/** 
	 该方法有2处调用。
	 1，修改字段。
	 2，编辑属性。
	 
	 @return 
	*/
	public final String DtlInit() throws Exception {
		MapDtl dtl = new MapDtl();
		dtl.setNo(this.getMapDtlNo());
		if (dtl.RetrieveFromDBSources() == 0)
		{
			dtl.setFrmID(this.getFrmID());
			dtl.setName(this.getFrmID());
			dtl.Insert();
			dtl.IntMapAttrs();
		}

		if (this.getNodeID() != 0)
		{
			/* 如果传递来了节点信息, 就是说明了独立表单的节点方案处理, 现在就要做如下判断.
			 * 1, 如果已经有了.
			 */
			dtl.setNo(this.getMapDtlNo() + "_" + this.getNodeID());
			if (dtl.RetrieveFromDBSources() == 0)
			{

				// 开始复制它的属性.
				MapAttrs mattrs = new MapAttrs(this.getMapDtlNo());

				//让其直接保存.
				dtl.setNo(this.getMapDtlNo() + "_" + this.getNodeID());
				dtl.setFrmID("Temp");
				dtl.DirectInsert(); //生成一个明细表属性的主表.

				//循环保存字段.
				int idx = 0;
				for (MapAttr item : mattrs.ToJavaList())
				{
					item.setFrmID(this.getMapDtlNo() + "_" + this.getNodeID());
					item.setMyPK(item.getFrmID() + "_" + item.getKeyOfEn());
					item.Save();
					idx++;
					item.setIdx(idx);
					item.DirectUpdate();
				}

				MapData md = new MapData();
				md.setNo("Temp");
				if (md.getIsExits())
				{
					md.setName("为权限方案设置的临时的数据");
					md.Insert();
				}
			}
		}

		DataSet ds = new DataSet();
		DataTable dt = dtl.ToDataTableField("Main");
		ds.Tables.add(dt);

		//获得字段列表.
		MapAttrs attrsDtl = new MapAttrs(this.getMapDtlNo());
		DataTable dtAttrs = attrsDtl.ToDataTableField("Ens");
		ds.Tables.add(dtAttrs);

		//返回json配置信息.
		return bp.tools.Json.ToJson(ds);
	}
	/** 
	 执行保存.
	 
	 @return 
	*/
	public final String DtlSave()
	{
		try
		{
			//复制.
			MapDtl dtl = new MapDtl(this.getMapDtlNo());

			//从request对象里复制数据,到entity.
			bp.pub.PubClass.CopyFromRequest(dtl);

			dtl.Update();

			return "保存成功...";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/** 
	 下载表单.
	*/
	public final void DownTempFrm() throws IOException {
		String fileFullName = SystemConfig.getPathOfWebApp() + "Temp/" + this.getFrmID() + ".xml";

		ContextHolderUtils.ResponseWriteFile(fileFullName, this.getFrmID() + ".xml", "application/octet-stream");
	}


	public final boolean getItIsReusable()
	{
		return false;
	}


	public final boolean getItIsNodeSheet()
	{
		if (this.getFrmID().startsWith("ND") == true)
		{
			return true;
		}
		return false;
	}
	/** 
	 字段属性编辑 初始化
	 
	 @return 
	*/
	public final String Attachment_Init() throws Exception {
		FrmAttachment ath = new FrmAttachment();
		ath.setFrmID(this.getFrmID());
		ath.setNoOfObj(this.getAth());
		ath.setNodeID(this.getNodeID());
		if (this.getMyPK() == null)
		{
			if (this.getNodeID() == 0)
			{
				ath.setMyPK(this.getFrmID() + "_" + this.getAth());
			}
			else
			{
				ath.setMyPK(this.getFrmID() + "_" + this.getAth() + "_" + this.getNodeID());
			}
		}
		else
		{
			ath.setMyPK(this.getMyPK());

		}
		int i = ath.RetrieveFromDBSources();
		if (i == 0)
		{
			/*初始化默认值.*/
			ath.setNoOfObj("Ath1");
			ath.setName("我的附件");
			// ath.SaveTo =  bp.difference.SystemConfig.getPathOfDataUser() + "/UploadFile/" + this.getFrmID() + "/";
			ath.setH(40);
			ath.setExts("*.*");
		}

		if (i == 0 && this.getNodeID() != 0)
		{
			/*这里处理 独立表单解决方案, 如果有FK_Node 就说明该节点需要单独控制该附件的属性. */
			MapData mapData = new MapData();
			mapData.RetrieveByAttr(MapDataAttr.No, this.getFrmID());
			if (Objects.equals(mapData.getAppType(), "0"))
			{
				FrmAttachment souceAthMent = new FrmAttachment();
				// 查询出来原来的数据.
				int rowCount = souceAthMent.Retrieve(FrmAttachmentAttr.FK_MapData, this.getFrmID(), FrmAttachmentAttr.NoOfObj, this.getAth(), FrmAttachmentAttr.FK_Node, "0");
				if (rowCount > 0)
				{
					ath.Copy(souceAthMent);
				}
			}
			if (this.getNodeID() == 0)
			{
				ath.setMyPK(this.getFrmID() + "_" + this.getAth());
			}
			else
			{
				ath.setMyPK(this.getFrmID() + "_" + this.getAth() + "_" + this.getNodeID());
			}

			//插入一个新的.
			ath.setNodeID(this.getNodeID());
			ath.setFrmID(this.getFrmID());
			ath.setNoOfObj(this.getAth());
			//  ath.DirectInsert();
		}

		return ath.ToJson(true);
	}
	/** 
	 保存.
	 
	 @return 
	*/
	public final String Attachment_Save() throws Exception {
		FrmAttachment ath = new FrmAttachment();
		ath.setFrmID(this.getFrmID());
		ath.setNoOfObj(this.getAth());
		ath.setNodeID(this.getNodeID());
		ath.setMyPK(this.getFrmID() + "_" + this.getAth());

		int i = ath.RetrieveFromDBSources();
		Object tempVar = bp.pub.PubClass.CopyFromRequestByPost(ath);
		ath = tempVar instanceof FrmAttachment ? (FrmAttachment)tempVar : null;
		if (i == 0)
		{
			ath.Save(); //执行保存.
		}
		else
		{
			ath.Update();
		}
		return "保存成功..";
	}
	public final String Attachment_Delete() throws Exception {
		FrmAttachment ath = new FrmAttachment();
		ath.setMyPK(this.getMyPK());
		ath.Delete();
		return "删除成功.." + ath.getMyPK();
	}



		///#region sfGuide
	/** 
	 获取数据源字典表信息
	 
	 @return 
	*/
	public final String SFGuide_GetInfo() throws Exception {
		String sfno = this.GetRequestVal("sfno"); //context.Request.QueryString["sfno");

		if (sfno == null || sfno.isEmpty())
		{
			return "err@参数不正确";
		}

		SFTable sftable = new SFTable(sfno);
		DataTable dt = sftable.ToDataTableField("info");

		for (DataColumn col : dt.Columns)
		{
			col.ColumnName = col.ColumnName.toUpperCase();
		}

		return bp.tools.Json.ToJson(dt);
	}
	public final String SFGuide_SaveInfo() throws Exception {
		boolean IsNew = this.GetRequestValBoolen("IsNew");
		String sfno = this.GetRequestVal("No");
		String myname = this.GetRequestVal("Name");

		String srctype = this.GetRequestVal("SrcType");
		int codestruct = this.GetRequestValInt("CodeStruct");

		String defval = this.GetRequestVal("DefVal");
		String sfdbsrc = this.GetRequestVal("FK_SFDBSrc");
		String srctable = this.GetRequestVal("SrcTable");
		String columnvalue = this.GetRequestVal("ColumnValue");
		String columntext = this.GetRequestVal("ColumnText");

		String parentvalue = this.GetRequestVal("ParentValue");
		String tabledesc = this.GetRequestVal("TableDesc");
		String selectstatement = this.GetRequestVal("Selectstatement");

		//判断是否已经存在
		SFTable sftable = new SFTable();
		sftable.setNo(sfno);

		if (IsNew && sftable.RetrieveFromDBSources() > 0)
		{
			return "err@字典编号" + sfno + "已经存在，不允许重复。";
		}

		sftable.setName(myname);
		sftable.setSrcType(srctype);
		sftable.setCodeStruct(CodeStruct.forValue(codestruct));
		sftable.setDefVal(defval);
		sftable.setSFDBSrcNo(sfdbsrc);
		sftable.setSrcTable(srctable);
		sftable.setColumnValue(columnvalue);
		sftable.setColumnText(columntext);
		sftable.setParentValue(parentvalue);
		sftable.setTableDesc(tabledesc);
		sftable.setSelectStatement(selectstatement);

		switch (sftable.getSrcType())
		{
			case DictSrcType.BPClass:
				String[] nos = sftable.getNo().split("[.]", -1);
				sftable.setFKVal("FK_" + StringHelper.trimEnd(nos[nos.length - 1], 's'));
				sftable.setSFDBSrcNo("local");
				break;
			default:
				sftable.setFKVal("FK_" + sftable.getNo());
				break;
		}

		sftable.Save();
		return "保存成功！";
	}
	public final String SFGuide_Getmtds() throws Exception {
		String src = this.GetRequestVal("src"); //context.Request.QueryString["src");
		if (src == null || src.isEmpty())
		{
			return "err@系统中没有webservices类型的数据源，该类型的外键表不能创建，请维护数据源.";
		}

		SFDBSrc sr = new SFDBSrc(src);

		if (!Objects.equals(sr.getDBSrcType(), DBSrcType.WebServices))
		{
			return "err@数据源“" + sr.getName() +"”不是WebService数据源.";
		}

		//ArrayList<WSMethod> mtds = GetWebServiceMethods(sr);

		//return bp.tools.Json.ToJson(mtds);
		return null;
	}
	public final String SFGuide_GetCols() throws Exception {
		String src = this.GetRequestVal("src"); //context.Request.QueryString["src");
		String table = this.GetRequestVal("table"); //context.Request.QueryString["table");

		if (src == null || src.isEmpty())
		{
			throw new RuntimeException("err@参数不正确");
		}


		if (table == null || table.isEmpty())
		{
			return "[]";
		}

		SFDBSrc sr = new SFDBSrc(src);
		DataTable dt = sr.GetColumns(table);

		for (DataColumn col : dt.Columns)
		{
			col.ColumnName = col.ColumnName.toUpperCase();
		}

		for (DataRow r : dt.Rows)
		{
			if(SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase){
				r.setValue("NAME", r.getValue("NO") + (r.getValue("NAME") == null || "".equals(r.getValue("NAME")) || StringUtils.isEmpty(r.getValue("NAME").toString()) ? "" : String.format("[%1$s]", r.getValue("NAME"))));
			}else{
				r.setValue("Name", r.getValue("No") + (r.getValue("Name") == null || "".equals(r.getValue("Name")) || StringUtils.isEmpty(r.getValue("Name").toString()) ? "" : String.format("[%1$s]", r.getValue("Name"))));
			}		}

		return bp.tools.Json.ToJson(dt);
	}
	/** 
	 //获取表/视图列表
	 
	 @return 
	*/
	public final String SFGuide_GetTVs() throws Exception {
		String src = this.GetRequestVal("src"); // context.Request.QueryString["src");

		SFDBSrc sr = new SFDBSrc(src);
		DataTable dt = sr.GetTables(false);

		for (DataColumn col : dt.Columns)
		{
			col.ColumnName = col.ColumnName.toUpperCase();
		}

		return bp.tools.Json.ToJson(dt);
	}
	/** 
	 获得clsss列表.
	 
	 @return 
	*/
	public final String SFGuide_GetClass() throws Exception
	{
		String sfno = this.GetRequestVal("sfno"); // context.Request.QueryString["sfno"];
		String stru = this.GetRequestVal("struct"); //context.Request.QueryString["struct"];
		int st = 0;
		RefObject<Integer> tempRef_st = new RefObject<Integer>(st);
		boolean ntempVar = StringUtils.isEmpty(stru) || !TryParse(stru);
		st = tempRef_st.argvalue;
		if (ntempVar)
		{
			throw new RuntimeException("err@参数不正确.");
		}


		String error = "";
		ArrayList arr = null;
		SFTables sfs = new SFTables();
		Entities ens = null;
		SFTable sf = null;
		sfs.Retrieve(SFTableAttr.DictSrcType, DictSrcType.BPClass);

		switch (st)
		{
			case 0:
				arr = ClassFactory.GetObjects("bp.en.EntityNoName");
				break;
			case 1:
				arr = ClassFactory.GetObjects("bp.en.EntitySimpleTree");
				break;
			default:
				arr = new ArrayList();
				break;
		}

		StringBuilder s = new StringBuilder("[");
		for (Object en : arr)
		{
			try
			{
				if (en == null)
				{
					continue;
				}

				ens = ((Entity) en).GetNewEntities();
				if (ens == null)
				{
					continue;
				}

				Object tempVar = sfs.GetEntityByKey(ens.toString());
				sf = tempVar instanceof SFTable ? (SFTable)tempVar : null;

				if ((sf != null && !sfno.equals(sf.getNo())) || DataType.IsNullOrEmpty(ens.toString()))
				{
					continue;
				}

				s.append(String.format("{\"NO\":\"%1$s\",\"NAME\":\"%1$s[%2$s]\",\"DESC\":\"%2$s\"},", ens, ((Entity) en).getEnDesc()));
			}
			catch (java.lang.Exception e)
			{
				continue;
			}
		}
		return bp.tools.StringHelper.trimEnd(s.toString(), ',') + "]";
	}
	/**
	 * 转换数值是否成功
	 * @param st
	 * @return
	 */
	public final boolean TryParse(String st){
		int a = 0;
		try {
			a = Integer.parseInt(st);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/** 
	 获取数据源列表
	 
	 @return 
	*/
	public final String SFGuide_GetSrcs() throws Exception
	{

		String type = ContextHolderUtils.getRequest().getParameter("type");
		int itype = 0;
		boolean onlyWS = false;

		SFDBSrcs srcs = new SFDBSrcs();
		RefObject<Integer> tempRef_itype = new RefObject<Integer>(itype);
		boolean tempVar = !StringUtils.isEmpty(type) && TryParse(type);
		itype = tempRef_itype.argvalue;
		if (tempVar)
		{
			onlyWS = true;
			srcs.Retrieve(SFDBSrcAttr.DBSrcType, itype);
		}
		else
		{
			srcs.RetrieveAll();
		}

		DataTable dt = srcs.ToDataTableField();


		if (onlyWS == false)
		{
			java.util.ArrayList<DataRow> wsRows = new java.util.ArrayList<DataRow>();
			for (DataRow r : dt.Rows)
			{
				if (r.getValue("DBSRCTYPE").equals(DBSrcType.WebServices))
				{
					wsRows.add(r);
				}
			}

			for (DataRow r : wsRows)
			{
				dt.Rows.remove(r);
			}
		}
		return bp.tools.Json.ToJsonUpper(dt);
	}
	/** 
	
	 
	 @return 
	*/
	public final String FrmView_Init() throws Exception {
		String frmID = this.GetRequestVal("FrmID");

		MapData md = new MapData(frmID);

		//获得表单模版.
		DataSet myds = CCFormAPI.GenerHisDataSet(md.getNo(), null, null);



			///#region 把主从表数据放入里面.
		//.工作数据放里面去, 放进去前执行一次装载前填充事件.
		GEEntity wk = new GEEntity(getFrmID());

		DataTable mainTable = wk.ToDataTableField("MainTable");
		mainTable.TableName = "MainTable";
		myds.Tables.add(mainTable);

			///#endregion



			///#region 增加附件信息.
		FrmAttachments athDescs = new FrmAttachments();

		athDescs.Retrieve(FrmAttachmentAttr.FK_MapData, this.getFrmID(), null);
		if (!athDescs.isEmpty())
		{
			FrmAttachment athDesc = athDescs.get(0) instanceof FrmAttachment ? (FrmAttachment)athDescs.get(0) : null;

			//查询出来数据实体.
			FrmAttachmentDBs dbs = new FrmAttachmentDBs();
			if (athDesc.getHisCtrlWay() == AthCtrlWay.PWorkID)
			{
				Paras ps = new Paras();
				ps.SQL = "SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
				ps.Add("WorkID", this.getWorkID());
				String pWorkID = String.valueOf(DBAccess.RunSQLReturnValInt(ps, 0));
				if (pWorkID == null || Objects.equals(pWorkID, "0"))
				{
					pWorkID = String.valueOf(this.getWorkID());
				}

				if (athDesc.getAthUploadWay() == AthUploadWay.Inherit)
				{
					/* 继承模式 */
					QueryObject qo = new QueryObject(dbs);
					qo.AddWhere(FrmAttachmentDBAttr.RefPKVal, pWorkID);
					qo.addOr();
					qo.AddWhere(FrmAttachmentDBAttr.RefPKVal, String.valueOf(this.getWorkID()));
					qo.addOrderBy("RDT");
					qo.DoQuery();
				}

				if (athDesc.getAthUploadWay() == AthUploadWay.Interwork)
				{
					/*共享模式*/
					dbs.Retrieve(FrmAttachmentDBAttr.RefPKVal, pWorkID, null);
				}
			}
			else if (athDesc.getHisCtrlWay() == AthCtrlWay.WorkID)
			{
				/* 继承模式 */
				QueryObject qo = new QueryObject(dbs);
				qo.AddWhere(FrmAttachmentDBAttr.NoOfObj, athDesc.getNoOfObj());
				qo.addAnd();
				qo.AddWhere(FrmAttachmentDBAttr.RefPKVal, String.valueOf(this.getWorkID()));
				qo.addOrderBy("RDT");
				qo.DoQuery();
			}

			//增加一个数据源.
			myds.Tables.add(dbs.ToDataTableField("Sys_FrmAttachmentDB").copy());
		}

			///#endregion


			///#region 把外键表加入DataSet
		DataTable dtMapAttr = myds.GetTableByName("Sys_MapAttr");
		DataTable dt = new DataTable();
		MapExts mes = md.getMapExts();
		MapExt me = new MapExt();
		DataTable ddlTable = new DataTable();
		ddlTable.Columns.Add("No");
		for (DataRow dr : dtMapAttr.Rows)
		{
			String lgType = dr.getValue("LGType").toString();
			String uiBindKey = dr.getValue("UIBindKey").toString();

			if (DataType.IsNullOrEmpty(uiBindKey) == true)
			{
				continue; //为空就continue.
			}

			if (lgType.equals("1") == true)
			{
				continue; //枚举值就continue;
			}

			String uiIsEnable = dr.getValue("UIIsEnable").toString();
			if (uiIsEnable.equals("0") == true && lgType.equals("1") == true)
			{
				continue; //如果是外键，并且是不可以编辑的状态.
			}

			if (uiIsEnable.equals("1") == true && lgType.equals("0") == true)
			{
				continue; //如果是外部数据源，并且是不可以编辑的状态.
			}



			// 检查是否有下拉框自动填充。
			String keyOfEn = dr.getValue("KeyOfEn").toString();
			String fk_mapData = dr.getValue("FK_MapData").toString();


				///#region 处理下拉框数据范围. for 小杨.
			Object tempVar = mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL, MapExtAttr.AttrOfOper, keyOfEn);
			me = tempVar instanceof MapExt ? (MapExt)tempVar : null;
			if (me != null)
			{
				Object tempVar2 = me.getDoc();
				String fullSQL = tempVar2 instanceof String ? (String)tempVar2 : null;
				fullSQL = fullSQL.replace("~", ",");
				fullSQL = bp.wf.Glo.DealExp(fullSQL, wk, null);
				dt = DBAccess.RunSQLReturnTable(fullSQL);
				if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
				{
					String columnName = "";
					for (DataColumn col : dt.Columns)
					{
						columnName = col.ColumnName.toUpperCase();
						switch (columnName)
						{
							case "NO":
								col.ColumnName = "No";
								break;
							case "NAME":
								col.ColumnName = "Name";
								break;
						}
					}
				}
				//重构新表
				DataTable dt_FK_Dll = new DataTable();
				dt_FK_Dll.TableName = keyOfEn; //可能存在隐患，如果多个字段，绑定同一个表，就存在这样的问题.
				dt_FK_Dll.Columns.Add("No", String.class);
				dt_FK_Dll.Columns.Add("Name", String.class);
				for (DataRow dllRow : dt.Rows)
				{
					DataRow drDll = dt_FK_Dll.NewRow();
					drDll.setValue("No", dllRow.get("No"));
					drDll.setValue("Name", dllRow.get("Name"));
					dt_FK_Dll.Rows.add(drDll);
				}
				myds.Tables.add(dt_FK_Dll);
				continue;
			}

				///#endregion 处理下拉框数据范围.

			// 判断是否存在.
			if (myds.contains(uiBindKey) == true)
			{
				continue;
			}

			DataTable mydt = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null);
			if (mydt == null)
			{
				DataRow ddldr = ddlTable.NewRow();
				ddldr.setValue("No", uiBindKey);
				ddlTable.Rows.add(ddldr);
			}
			else
			{
				myds.Tables.add(mydt);
			}
		}
		ddlTable.TableName = "UIBindKey";
		myds.Tables.add(ddlTable);

			///#endregion End把外键表加入DataSet


			///#region 图片附件
		FrmImgAthDBs imgAthDBs = new FrmImgAthDBs(this.getFrmID(), String.valueOf(this.getWorkID()));
		if (imgAthDBs != null && !imgAthDBs.isEmpty())
		{
			DataTable dt_ImgAth = imgAthDBs.ToDataTableField("Sys_FrmImgAthDB");
			myds.Tables.add(dt_ImgAth);
		}

			///#endregion

		return bp.tools.Json.ToJson(myds);
	}

		///#endregion



		///#region  ImpTableFieldSelectBindKey 外键枚举
	/** 
	 初始化数据
	 
	 @return 
	*/
	public final String ImpTableFieldSelectBindKey_Init() throws Exception {
		DataSet ds = new DataSet();

		SysEnumMains ens = new SysEnumMains();
		ens.RetrieveAll();
		ds.Tables.add(ens.ToDataTableField("EnumMain"));

		SFTables tabs = new SFTables();
		tabs.RetrieveAll();
		ds.Tables.add(tabs.ToDataTableField("SFTables"));

		return bp.tools.Json.ToJson(ds);
	}

		///#endregion  ImpTableFieldSelectBindKey 外键枚举

	public final String MapDataVer_SetMainVer() throws Exception {
		MapDataVer mdVer = new MapDataVer(this.getMyPK());

			///#region 1.变更主版本
		String sql = "UPDATE Sys_MapDataVer SET IsRel=0 WHERE FrmID='" + mdVer.getFrmID() + "'";
		DBAccess.RunSQL(sql);

		mdVer.SetValByKey("IsRel", 1);
		mdVer.Update(); //更新.

			///#endregion 1.变更主版本


			///#region 2. 覆盖之前的主版本数据
		MapData mainData = new MapData(mdVer.getFrmID());
		String currVer = mainData.getNo() + "." + mainData.getVer2022();
		MapData md = new MapData();
		md.setNo(currVer);
		if (md.RetrieveFromDBSources() == 1)
		{
			md.Delete();
		}
		//把表单属性的FK_FormTree清空
		CCFormAPI.CopyFrm(mainData.getNo(), currVer, mainData.getName() +"(Ver" + currVer + ".0)", mainData.getFormTreeNo());
		md.setFormTreeNo("");
		md.setPTable(mainData.getPTable());
		md.Update();
		//修改从表的存储表
		MapDtls dtls = md.getMapDtls();
		for (MapDtl dtl : dtls.ToJavaList())
		{
			if (dtl.getPTable().equals(dtl.getNo()) == true)
			{
				dtl.setPTable(dtl.getPTable().replace(currVer, mainData.getNo()));
				dtl.Update();
				continue;
			}
		}
		//把当前表单对应数据改成当前的版本
		DBAccess.RunSQL("UPDATE " + md.getPTable() + " SET AtPara=CONCAT(AtPara,'@FrmVer=" + mainData.getVer2022() + "') WHERE AtPara NOT LIKE '%@FrmVer=%'");

			///#endregion 2. 覆盖之前的主版本数据



			///#region 3.把设置的表单数据拷贝到FrmID所在的表单上去
		//FrmID的表单只有不存在其他版本的时候才可以删除，修改版本
		sql = "UPDATE Sys_MapDataVer SET FrmID='" + mdVer.getMyPK() + "'WHERE FrmID='" + mdVer.getFrmID() + "'";
		DBAccess.RunSQL(sql);
		//绑定的表单，表单方案的还原
		FrmNodes frmNodes = new FrmNodes();
		frmNodes.Retrieve(FrmNodeAttr.FK_Frm, mainData.getNo(), null);

		FrmFields frmFields = new FrmFields();
		String whereFK_MapData = "'" + mainData.getNo() + "'";
		dtls = new MapDtls(mainData.getNo());
		for (MapDtl dtl : dtls.ToJavaList())
		{
			whereFK_MapData += ",'" + dtl.getNo() + "' ";

		}
		frmFields.RetrieveIn(FrmFieldAttr.FrmID, whereFK_MapData, null);
		mainData.Delete();
		//把表单属性的FK_FormTree清空
		CCFormAPI.CopyFrm(mainData.getNo() + "." + mdVer.getVer(), mainData.getNo(), mainData.getName(), mainData.getFormTreeNo());
		mainData.setFormTreeNo(mainData.getFormTreeNo());
		mainData.setPTable(mainData.getPTable());
		mainData.setVer2022(mdVer.getVer());
		mainData.Update();
		//修改从表的存储表
		dtls = mainData.getMapDtls();
		for (MapDtl dtl : dtls.ToJavaList())
		{
			if (dtl.getPTable().equals(dtl.getNo()) == true)
			{
				dtl.setPTable(dtl.getPTable().replace(mainData.getNo() + "." + mdVer.getVer(), mainData.getNo()));
				dtl.Update();
				continue;
			}
		}

		sql = "UPDATE Sys_MapDataVer SET FrmID='" + mainData.getNo() + "' WHERE FrmID='" + this.getMyPK() + "'";
		DBAccess.RunSQL(sql);
		//还原表单方案
		for (FrmNode frmNode : frmNodes.ToJavaList())
		{
			frmNode.DirectInsert();
		}
		for (FrmField frmField : frmFields.ToJavaList())
		{
			frmField.DirectInsert();
		}

			///#endregion 3.把设置的表单数据拷贝到FrmID所在的表单上去
		return "设置成功.";
	}

	public final String SysEnumList_SelectEnum()
	{
		String sql = "";
		String EnumName = this.GetRequestVal("EnumName");
	   if (Objects.equals(EnumName, ""))
	   {
		   sql = "SELECT EnumKey,No,Name,CfgVal,Lang FROM Sys_EnumMain";
	   }
	   else
	   {
		   sql = "SELECT EnumKey,No,Name,CfgVal,Lang FROM Sys_EnumMain WHERE (No like '%" + EnumName + "%') OR (Name like '%" + EnumName + "%')";
	   }
	   DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "EnumKey";
			dt.Columns.get(1).ColumnName = "No";
			dt.Columns.get(2).ColumnName = "Name";
			dt.Columns.get(3).ColumnName = "CfgVal";
			dt.Columns.get(4).ColumnName = "Lang";
		}
		return bp.tools.Json.ToJson(dt);
	}

	public final String SysEnumList_MapAttrs()
	{
		String sql = "SELECT A.FrmID,A.KeyOfEn,A.Name From Sys_MapAttr A,Sys_MapData B Where A.FrmID=B.No " + "AND A.UIBindKey='" + this.GetRequestVal("UIBindKey") + "' AND B.OrgNo='" + this.GetRequestVal("OrgNo") + "'";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "FK_MapData";
			dt.Columns.get(1).ColumnName = "KeyOfEn";
			dt.Columns.get(2).ColumnName = "Name";
		}
		return bp.tools.Json.ToJson(dt);
	}

}
