package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.difference.*;
import bp.*;
import bp.wf.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 初始化函数
*/
public class WF_Admin_FoolFormDesigner_MapExt extends bp.difference.handler.DirectoryPageBase
{

		///#region 执行父类的重写方法.
	/**
	 默认执行的方法

	 @return
	*/
	@Override
	protected String DoDefaultMethod()
	{
		switch (this.getDoType())
		{
			case "DtlFieldUp": //字段上移
				return "执行成功.";
			default:
				break;
		}

		//找不不到标记就抛出异常.
		throw new RuntimeException("@标记[" + this.getDoType() + "]，没有找到.");
	}

	/**
	 构造函数
	*/
	public WF_Admin_FoolFormDesigner_MapExt()
	{
	}

		///#endregion 执行父类的重写方法.


		///#region AutoFullDtlField 自动计算 a*b  功能界面 .
	/**
	 保存(自动计算: @单价*@数量 模式.)

	 @return
	*/
	public final String AutoFullDtlField_Save() throws Exception {
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.AutoFullDtlField, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

		me.setFrmID(this.getFrmID());
		me.setAttrOfOper(this.getKeyOfEn());
		me.setDoc(this.GetValFromFrmByKey("DDL_Dtl") + "." + this.GetValFromFrmByKey("DDL_Field") + "." + this.GetValFromFrmByKey("DDL_JSFS")); //要执行的表达式.

		me.setExtType(MapExtXmlList.AutoFullDtlField);

		me.setTag1(this.GetValFromFrmByKey("TB_Tag1"));
		me.setTag2(this.GetValFromFrmByKey("TB_Tag2"));

		String Tag = "0";
		try
		{
			Tag = this.GetValFromFrmByKey("CB_Tag");
			if (Objects.equals(Tag, "on"))
			{
				Tag = "1";
			}
		}
		catch (RuntimeException e)
		{
			Tag = "0";
		}


		me.setTag(Tag);

		String Tag3 = "0";
		try
		{
			Tag3 = this.GetValFromFrmByKey("CB_Tag3");
			if (Objects.equals(Tag3, "on"))
			{
				Tag3 = "1";
			}
		}
		catch (RuntimeException e)
		{
			Tag3 = "0";
		}
		me.setTag3(Tag3);

		me.setTag4(this.GetValFromFrmByKey("DDL_Fileds"));

		//执行保存.
		me.setMyPK(MapExtXmlList.AutoFullDtlField + "_" + me.getFrmID() + "_" + me.getAttrOfOper());
		if (me.Update() == 0)
		{
			me.Insert();
		}

		return "保存成功.";
	}
	public final String AutoFullDtlField_Delete() throws Exception {
		MapExt me = new MapExt();
		me.Delete(MapExtAttr.ExtType, MapExtXmlList.AutoFullDtlField, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

		return "删除成功.";
	}
	public final String AutoFullDtlField_Init() throws Exception {
		DataSet ds = new DataSet();

		// 加载mapext 数据.
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.AutoFullDtlField, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());
		if (i == 0)
		{
			me.setFrmID(this.getFrmID());
			me.setAttrOfOper(this.getKeyOfEn());
			me.setDBSrcNo("local");
		}

		if (Objects.equals(me.getDBSrcNo(), ""))
		{
			me.setDBSrcNo("local");
		}

		//去掉 ' 号.
		me.SetValByKey("Doc", me.getDoc());

		DataTable dt = me.ToDataTableField("Main");
		dt.TableName = "Sys_MapExt";
		ds.Tables.add(dt);

		//把从表放入里面.
		MapDtls dtls = new MapDtls();
		dtls.Retrieve(MapDtlAttr.FK_MapData, this.getFrmID(), MapDtlAttr.FK_Node, 0, null);
		ds.Tables.add(dtls.ToDataTableField("Dtls"));

		//把从表的字段放入.
		for (MapDtl dtl : dtls.ToJavaList())
		{
			String sql = "SELECT KeyOfEn as \"No\",Name as \"Name\" FROM Sys_MapAttr WHERE FK_MapData='" + dtl.getNo() + "' AND (MyDataType=2 OR MyDataType=3 OR MyDataType=5 OR MyDataType=8)  ";
			sql += " AND KeyOfEn !='OID' AND KeyOfEn!='FID' AND KeyOfEn!='RefPK' ";

			//把从表增加里面去.
			DataTable mydt = DBAccess.RunSQLReturnTable(sql);
			mydt.TableName = dtl.getNo();
			ds.Tables.add(mydt);
		}

		//把主表的字段放入
		String mainsql = "SELECT KeyOfEn as \"No\",Name as \"Name\" FROM Sys_MapAttr WHERE FK_MapData='" + this.getFrmID() + "' AND MyDataType=1 AND UIIsEnable = 0 ";
		mainsql += " AND KeyOfEn !='OID' AND KeyOfEn!='FID' AND KeyOfEn!='WorkID' AND KeyOfEn!='NodeID' AND KeyOfEn!='RefPK'  AND KeyOfEn!='RDT' AND KeyOfEn!='Rec' ";

		//把从表增加里面去.
		DataTable maindt = DBAccess.RunSQLReturnTable(mainsql);
		maindt.TableName = "main_Attr";
		ds.Tables.add(maindt);

		return bp.tools.Json.ToJson(ds);
	}

		///#endregion AutoFullDtlField  功能界面.


		///#region AutoFull 自动计算 a*b  功能界面 .
	/**
	 保存(自动计算: @单价*@数量 模式.)

	 @return
	*/
	public final String AutoFull_Save() throws Exception {
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.AutoFull, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

		me.setFrmID(this.getFrmID());
		me.setAttrOfOper(this.getKeyOfEn());
		me.setDoc(this.GetValFromFrmByKey("TB_Doc")); //要执行的表达式.

		me.setExtType(MapExtXmlList.AutoFull);

		//执行保存.
		me.setMyPK(MapExtXmlList.AutoFull + "_" + me.getFrmID() + "_" + me.getAttrOfOper());
		if (me.Update() == 0)
		{
			me.Insert();
		}

		return "保存成功.";
	}

	public final String AutoFull_Init() throws Exception {
		DataSet ds = new DataSet();

		// 加载mapext 数据.
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.AutoFull, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());
		if (i == 0)
		{
			me.setFrmID(this.getFrmID());
			me.setAttrOfOper(this.getKeyOfEn());
			me.setDBSrcNo("local");
		}

		if (Objects.equals(me.getDBSrcNo(), ""))
		{
			me.setDBSrcNo("local");
		}

		//去掉 ' 号.
		me.SetValByKey("Doc", me.getDoc());

		DataTable dt = me.ToDataTableField("Main");
		dt.TableName = "Sys_MapExt";
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}

		///#endregion ActiveDDL 功能界面.


		///#region TBFullCtrl 功能界面 .
	/**
	 保存

	 @return
	*/
	public final String TBFullCtrl_Save() throws Exception {
		try
		{
			MapExt me = new MapExt();
			int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.TBFullCtrl, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

			me.setFrmID(this.getFrmID());
			me.setAttrOfOper(this.getKeyOfEn());
			me.setDBSrcNo(this.GetValFromFrmByKey("FK_DBSrc"));
			me.setDoc(this.GetValFromFrmByKey("TB_Doc")); //要执行的SQL.

			me.setExtType(MapExtXmlList.TBFullCtrl);

			//执行保存.
			me.InitPK();

			if (me.Update() == 0)
			{
				me.Insert();
			}

			return "保存成功.";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	public final String TBFullCtrl_Delete() throws Exception {
		MapExt me = new MapExt();
		me.Delete(MapExtAttr.ExtType, MapExtXmlList.TBFullCtrl, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

		return "删除成功.";
	}
	public final String TBFullCtrl_Init() throws Exception {
		DataSet ds = new DataSet();

		//加载数据源.
		SFDBSrcs srcs = new SFDBSrcs();
		srcs.RetrieveAll();
		DataTable dtSrc = srcs.ToDataTableField("dt");
		dtSrc.TableName = "Sys_SFDBSrc";
		ds.Tables.add(dtSrc);

		// 加载 mapext 数据.
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.TBFullCtrl, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

		if (i == 0)
		{
			me.setFrmID(this.getFrmID());
			me.setAttrOfOper(this.getKeyOfEn());
			me.setDBSrcNo("local");
		}

		//这个属性没有用.
		me.setW(i); //用于标记该数据是否保存?  从而不现实填充从表，填充下拉框.按钮是否可以用.
		if (Objects.equals(me.getDBSrcNo(), ""))
		{
			me.setDBSrcNo("local");
		}

		//去掉 ' 号.
		me.SetValByKey("Doc", me.getDoc());

		DataTable dt = me.ToDataTableField("Main");
		dt.TableName = "Sys_MapExt";
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}
	/**
	 填充从表

	 @return
	*/
	public final String TBFullCtrlDtl_Init() throws Exception {
		MapExt me = new MapExt(this.getMyPK());

		String[] strs = me.getTag1().split("[$]", -1);
		// 格式为: $ND101Dtl2:SQL.

		MapDtls dtls = new MapDtls();
		dtls.Retrieve(MapDtlAttr.FK_MapData, me.getFrmID(), null);
		for (String str : strs)
		{
			if (DataType.IsNullOrEmpty(str) || str.contains(":") == false)
			{
				continue;
			}

			String[] kvs = str.split("[:]", -1);
			String fk_mapdtl = kvs[0];
			String sql = kvs[1];

			for (MapDtl dtl : dtls.ToJavaList())
			{
				if (!Objects.equals(dtl.getNo(), fk_mapdtl))
				{
					continue;
				}
				//dtl.MTR = sql.Trim();
			}
		}

		for (MapDtl dtl : dtls.ToJavaList())
		{
			String cols = "";
			MapAttrs mattrs = new MapAttrs(dtl.getNo());
			for (MapAttr item : mattrs.ToJavaList())
			{
				if (Objects.equals(item.getKeyOfEn(), "OID") || Objects.equals(item.getKeyOfEn(), "RefPKVal") || Objects.equals(item.getKeyOfEn(), "RefPK"))
				{
					continue;
				}

				cols += item.getKeyOfEn() + ",";
			}
			dtl.setAlias(cols); //把ptable作为一个数据参数.
		}
		return dtls.ToJson("dt");
	}

	public final String TBFullCtrlDtl_Save() throws Exception {
		MapDtls dtls = new MapDtls(this.getFrmID());
		MapExt me = new MapExt(this.getMyPK());

		String str = "";
		for (MapDtl dtl : dtls.ToJavaList())
		{
			String sql = this.GetRequestVal("TB_" + dtl.getNo());
			sql = sql.trim();
			if (DataType.IsNullOrEmpty(sql) == true)
			{
				continue;
			}

			if (sql.contains("@Key") == false)
			{
				return "err@在配置从表:" + dtl.getNo() + " sql填写错误, 必须包含@Key列, @Key就是当前文本框输入的值. ";
			}

			str += "$" + dtl.getNo() + ":" + sql;
		}
		me.setTag1(str);
		me.Update();

		return "保存成功.";
	}

	public final String TBFullCtrlDDL_Init() throws Exception {
		MapExt myme = new MapExt();
		myme.setMyPK(this.getMyPK());
		myme.RetrieveFromDBSources();
		MapAttrs mattrs = new MapAttrs(myme.getFrmID());
		mattrs.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.UIIsEnable, 1, MapAttrAttr.UIContralType, UIContralType.DDL.getValue(), null);

		String[] strs = myme.getTag().split("[$]", -1);
		for (MapAttr attr : mattrs.ToJavaList())
		{
			for (String s : strs)
			{
				if (DataType.IsNullOrEmpty(s) == true)
				{
					continue;
				}
				if (s.contains(attr.getKeyOfEn() + ":") == false)
				{
					continue;
				}

				attr.setDefVal(s.replace(attr.getKeyOfEn() + ":","")); //使用这个字段作为对应设置的sql.
			}
		}

		return mattrs.ToJson("dt");
	}
	public final String TBFullCtrlDDL_Save() throws Exception {
		MapExt me = new MapExt();
		me.setMyPK(this.getMyPK());
		if (me.RetrieveFromDBSources() == 0)
		{
			me.setMyPK(this.getMyPK());
			me.setAttrOfOper(GetRequestVal("AttrOfOper"));
			me.setFrmID(this.getFrmID());
			me.setExtType(MapExtXmlList.FullData);
			//me.getDoWay() = this.GetRequestVal("DDL_" + attr.getKeyOfEn());
			me.Insert();
		}


		MapAttrs attrs = new MapAttrs(me.getFrmID());
		attrs.Retrieve(MapAttrAttr.FK_MapData, me.getFrmID(), MapAttrAttr.UIIsEnable, 1, MapAttrAttr.UIContralType, UIContralType.DDL.getValue(), null);
		MapAttr mapAttr = new MapAttr(this.getFrmID() + "_" + me.getAttrOfOper());
		String str = "";
		for (MapAttr attr : attrs.ToJavaList())
		{

			String sql = this.GetRequestVal("TB_" + attr.getKeyOfEn());
			if (DataType.IsNullOrEmpty(sql) == true)
			{
				continue;
			}
			sql = sql.trim();

			if (sql.contains("@Key") == false && mapAttr.getUIContralType().getValue() != 18)
			{
				return "err@在配置从表:" + attr.getKeyOfEn() + " sql填写错误, 必须包含@Key列, @Key就是当前文本框输入的值. ";
			}

			str += "$" + attr.getKeyOfEn() + ":" + sql;
		}
		me.setTag(str);
		me.setAttrOfOper(GetRequestVal("AttrOfOper"));
		me.Update();

		return "保存成功.";
	}

		///#endregion TBFullCtrl 功能界面.


		///#region AutoFullDLL 功能界面 .

	public final String AutoFullDLL_Init() throws Exception {
		DataSet ds = new DataSet();

		//加载数据源.
		SFDBSrcs srcs = new SFDBSrcs();
		srcs.RetrieveAll();
		DataTable dtSrc = srcs.ToDataTableField("dt");
		dtSrc.TableName = "Sys_SFDBSrc";
		ds.Tables.add(dtSrc);

		// 加载 mapext 数据.
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

		if (i == 0)
		{
			me.setFrmID(this.getFrmID());
			me.setAttrOfOper(this.getKeyOfEn());
			me.setDBSrcNo("local");
		}

		if (DataType.IsNullOrEmpty(me.getDBSrcNo()) == true)
		{
			me.setDBSrcNo("local");
		}

		//去掉 ' 号.
		me.SetValByKey("Doc", me.getDoc());

		DataTable dt = me.ToDataTableField("Main");
		dt.TableName = "Sys_MapExt";
		ds.Tables.add(dt);
		return bp.tools.Json.ToJson(ds);
	}

	/**
	 查询条件的自动填充

	 @return
	*/
	public final String AutoFullDLL_Init_SearchCond() throws Exception {
		DataSet ds = new DataSet();

		//加载数据源.
		SFDBSrcs srcs = new SFDBSrcs();
		srcs.RetrieveAll();
		DataTable dtSrc = srcs.ToDataTableField("dt");
		dtSrc.TableName = "Sys_SFDBSrc";
		ds.Tables.add(dtSrc);

		// 加载 mapext 数据.
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLLSearchCond, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

		if (i == 0)
		{
			me.setFrmID(this.getFrmID());
			me.setAttrOfOper(this.getKeyOfEn());
			me.setDBSrcNo("local");
		}

		if (DataType.IsNullOrEmpty(me.getDBSrcNo()) == true)
		{
			me.setDBSrcNo("local");
		}

		//去掉 ' 号.
		me.SetValByKey("Doc", me.getDoc());

		DataTable dt = me.ToDataTableField("Main");
		dt.TableName = "Sys_MapExt";
		ds.Tables.add(dt);
		return bp.tools.Json.ToJson(ds);
	}

		///#endregion AutoFullDLL 功能界面.


		///#region DDLFullCtrl 功能界面 .
	/**
	 保存

	 @return
	*/
	public final String DDLFullCtrl_Save() throws Exception {
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.DDLFullCtrl, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

		me.setFrmID(this.getFrmID());
		me.setAttrOfOper(this.getKeyOfEn());
		me.setDBSrcNo(this.GetValFromFrmByKey("FK_DBSrc"));
		me.setDoc(this.GetValFromFrmByKey("TB_Doc")); //要执行的SQL.

		me.setExtType(MapExtXmlList.DDLFullCtrl);

		//执行保存.
		me.InitPK();
		if (me.Update() == 0)
		{
			me.Insert();
		}

		return "保存成功.";
	}
	public final String DDLFullCtrl_Delete() throws Exception {
		MapExt me = new MapExt();
		me.Delete(MapExtAttr.ExtType, MapExtXmlList.DDLFullCtrl, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

		return "删除成功.";
	}
	public final String DDLFullCtrl_Init() throws Exception {
		DataSet ds = new DataSet();

		//加载数据源.
		SFDBSrcs srcs = new SFDBSrcs();
		srcs.RetrieveAll();
		DataTable dtSrc = srcs.ToDataTableField("dt");
		dtSrc.TableName = "Sys_SFDBSrc";
		ds.Tables.add(dtSrc);

		// 加载 mapext 数据.
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.DDLFullCtrl, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

		if (i == 0)
		{
			me.setFrmID(this.getFrmID());
			me.setAttrOfOper(this.getKeyOfEn());
			me.setDBSrcNo("local");
		}

		me.setW(i);

		if (DataType.IsNullOrEmpty(me.getDBSrcNo()))
		{
			me.setDBSrcNo("local");
		}

		//去掉 ' 号.
		me.SetValByKey("Doc", me.getDoc());

		DataTable dt = me.ToDataTableField("Main");
		dt.TableName = "Sys_MapExt";
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}

		///#endregion DDLFullCtrl 功能界面.


		///#region ActiveDDL 功能界面 .

	public final String ActiveDDL_Init() throws Exception {
		DataSet ds = new DataSet();

		//加载外键字段.
		Paras ps = new Paras();
		ps.SQL = "SELECT KeyOfEn AS No, Name FROM Sys_MapAttr WHERE UIContralType=1 AND FK_MapData=" + SystemConfig.getAppCenterDBVarStr() + "FK_MapData AND KeyOfEn!=" + SystemConfig.getAppCenterDBVarStr() + "KeyOfEn";
		ps.Add("FK_MapData", this.getFrmID(), false);
		ps.Add("KeyOfEn", this.getKeyOfEn(), false);
		//var sql = "SELECT KeyOfEn AS No, Name FROM Sys_MapAttr WHERE UIContralType=1 AND FK_MapData='" + this.getFrmID() + "' AND KeyOfEn!='" + this.getKeyOfEn() + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		dt.TableName = "Sys_MapAttr";

		dt.Columns.get(0).ColumnName = "No";
		dt.Columns.get(1).ColumnName = "Name";
		ds.Tables.add(dt);

		if (dt.Rows.size() == 0)
		{
			return "err@表单中没有要级联的下拉框.";
		}

		//加载数据源.
		SFDBSrcs srcs = new SFDBSrcs();
		srcs.RetrieveAll();
		DataTable dtSrc = srcs.ToDataTableField("dt");
		dtSrc.TableName = "Sys_SFDBSrc";
		ds.Tables.add(dtSrc);

		// 加载mapext 数据.
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.ActiveDDL, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());
		if (i == 0)
		{
			me.setFrmID(this.getFrmID());
			me.setAttrOfOper(this.getKeyOfEn());
			me.setDBSrcNo("local");
		}

		if (DataType.IsNullOrEmpty(me.getDBSrcNo()))
		{
			me.setDBSrcNo("local");
		}

		//去掉 ' 号.
		me.SetValByKey("Doc", me.getDoc());

		dt = me.ToDataTableField("Main");
		dt.TableName = "Sys_MapExt";
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}
	/**
	 查询条件

	 @return
	*/
	public final String ActiveDDL_Init_SearchCond() throws Exception {
		DataSet ds = new DataSet();

		//加载外键字段.
		Paras ps = new Paras();
		ps.SQL = "SELECT KeyOfEn AS No, Name FROM Sys_MapAttr WHERE UIContralType=1 AND FK_MapData=" + SystemConfig.getAppCenterDBVarStr() + "FK_MapData AND KeyOfEn!=" + SystemConfig.getAppCenterDBVarStr() + "KeyOfEn";
		ps.Add("FK_MapData", this.getFrmID(), false);
		ps.Add("KeyOfEn", this.getKeyOfEn(), false);
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		dt.TableName = "Sys_MapAttr";

		dt.Columns.get(0).ColumnName = "No";
		dt.Columns.get(1).ColumnName = "Name";
		ds.Tables.add(dt);

		if (dt.Rows.size() == 0)
		{
			return "err@表单中没有要级联的下拉框.";
		}

		//加载数据源.
		SFDBSrcs srcs = new SFDBSrcs();
		srcs.RetrieveAll();
		DataTable dtSrc = srcs.ToDataTableField("dt");
		dtSrc.TableName = "Sys_SFDBSrc";
		ds.Tables.add(dtSrc);

		// 加载mapext 数据.
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.ActiveDDLSearchCond, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());
		if (i == 0)
		{
			me.setFrmID(this.getFrmID());
			me.setAttrOfOper(this.getKeyOfEn());
			me.setDBSrcNo("local");
		}

		if (Objects.equals(me.getDBSrcNo(), ""))
		{
			me.setDBSrcNo("local");
		}

		//去掉 ' 号.
		me.SetValByKey("Doc", me.getDoc());

		dt = me.ToDataTableField("Main");
		dt.TableName = "Sys_MapExt";
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}

		///#endregion ActiveDDL 功能界面.


		///#region 配置自动计算日期天数
	/**
	 初始化

	 @return
	*/
	public final String LoadRDTClo_Init()
	{
		DataSet ds = new DataSet();
		String FK_MapData = GetRequestVal("FK_MapData");
		String KeyOfEn = GetRequestVal("KeyOfEn");
		String sql = "";

		sql = "SELECT KeyOfEn as No, Name FROM Sys_MapAttr WHERE (MyDataType='6' OR MyDataType='7') AND FK_MapData='" + FK_MapData + "'";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "No";
			dt.Columns.get(1).ColumnName = "Name";
		}

		return bp.tools.Json.ToJson(dt);
	}
	public final String LoadRDTClo_Save() throws Exception {
		String KeyOfEn = GetRequestVal("KeyOfEn");
		String StarRDT = GetRequestVal("DDL_StarRDT"); //开始日期
		String EndRDT = GetRequestVal("DDL_EndRDT"); //结束日期
		String RDTRadio = GetRequestVal("RDTRadio"); //是否包含节假日

		MapExt mapExt = new MapExt();
		mapExt.setMyPK("ReqDays_" + this.getFrmID() + "_" + KeyOfEn);
		if (mapExt.RetrieveFromDBSources() == 0)
		{
			mapExt.setFrmID(this.getFrmID());
			mapExt.setExtType("ReqDays");
			mapExt.setAttrOfOper(KeyOfEn);
			mapExt.setTag1(StarRDT);
			mapExt.setTag2(EndRDT);
			mapExt.setTag3(RDTRadio);
			mapExt.Insert();
		}
		else
		{
			mapExt.setFrmID(this.getFrmID());
			mapExt.setExtType("ReqDays");
			mapExt.setAttrOfOper(KeyOfEn);
			mapExt.setTag1(StarRDT);
			mapExt.setTag2( EndRDT);
			mapExt.setTag3( RDTRadio);
			mapExt.Update();
		}
		return "保存成功！！";
	}

		///#endregion

		///#region 单选按钮事件
	/**
	 返回信息。

	 @return
	*/
	public final String RadioBtns_Init() throws Exception {
		DataSet ds = new DataSet();

		//放入表单字段.
		MapAttrs attrs = new MapAttrs(this.getFrmID());
		ds.Tables.add(attrs.ToDataTableField("Sys_MapAttr"));

		//属性.
		MapAttr attr = new MapAttr();
		attr.setMyPK(this.getFrmID() + "_" + this.getKeyOfEn());
		attr.Retrieve();

		//加入从表组件
		MapDtls mapDtls = new MapDtls(this.getFrmID());
		ds.Tables.add(mapDtls.ToDataTableField("MapDtls"));

		//加入多附件组件
		FrmAttachments frmAttachments = new FrmAttachments(this.getFrmID());
		ds.Tables.add(frmAttachments.ToDataTableField("FrmAttachments"));

		//把分组加入里面.
		GroupFields gfs = new GroupFields(this.getFrmID());
		ds.Tables.add(gfs.ToDataTableField("Sys_GroupFields"));

		//获取外键值
		DataTable dt = bp.pub.PubClass.GetDataTableByUIBineKey(attr.getUIBindKey(), null);
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
					default:
						break;
				}
		   }
		}


		//字段值.
		FrmRBs rbs = new FrmRBs();
		rbs.Retrieve(FrmRBAttr.FrmID, this.getFrmID(), FrmRBAttr.KeyOfEn, this.getKeyOfEn() ,FrmRBAttr.IntKey);
		if (rbs.size() == 0)
		{
			//如果是枚举类型
			if (attr.getLGType() == FieldTypeS.Enum)
			{
				/*初始枚举值变化.
				 */
				FrmRB rb = new FrmRB();
				rb.setFrmID(this.getFrmID());
				rb.setKeyOfEn(this.getKeyOfEn());
				rb.setIntKey("-1");
				rb.setLab("--无(不选择)--");
				rb.setEnumKey(attr.getUIBindKey());
				rb.Insert(); //插入数据.

				SysEnums ses = new SysEnums(attr.getUIBindKey());
				for (SysEnum se : ses.ToJavaList())
				{
					rb = new FrmRB();
					rb.setFrmID(this.getFrmID());
					rb.setKeyOfEn(this.getKeyOfEn());
					if(DataType.IsNullOrEmpty(se.getStrKey())==false)
						rb.setIntKey(se.getStrKey());
					else
						rb.setIntKey(String.valueOf(se.getIntKey()));
					rb.setLab(se.getLab());
					rb.setEnumKey(attr.getUIBindKey());
					rb.Insert(); //插入数据.
				}
			}
			//如果是外键类型
			if (attr.getLGType() == FieldTypeS.FK)
			{
				for (DataRow row : dt.Rows)
				{
					FrmRB rb = new FrmRB();
					rb.setFrmID(this.getFrmID());
					rb.setKeyOfEn(this.getKeyOfEn());
					rb.setIntKey(row.get("No").toString());
					rb.setLab(row.get("Name").toString());
					rb.setEnumKey(attr.getUIBindKey());
					rb.Insert(); //插入数据.
				}
			}

			//如果是复选框
			if (attr.getMyDataType() == DataType.AppBoolean && attr.getUIContralType() == UIContralType.CheckBok)
			{
				FrmRB rb = new FrmRB();
				rb.setFrmID(this.getFrmID());
				rb.setKeyOfEn(this.getKeyOfEn());
				rb.setIntKey("0");
				rb.setLab("否");
				rb.setEnumKey(attr.getUIBindKey());
				rb.Insert(); //插入数据.

				rb = new FrmRB();
				rb.setFrmID(this.getFrmID());
				rb.setKeyOfEn(this.getKeyOfEn());
				rb.setIntKey("1");
				rb.setLab("是");
				rb.setEnumKey(attr.getUIBindKey());
				rb.Insert(); //插入数据.

			}

			rbs.Retrieve(FrmRBAttr.FrmID, this.getFrmID(), FrmRBAttr.KeyOfEn, this.getKeyOfEn(),FrmRBAttr.IntKey);
		}

		//枚举值的情况
		if (rbs.size() != 0)
		{
			if (attr.getLGType() == FieldTypeS.Enum)
			{
				SysEnums ses = new SysEnums(attr.getUIBindKey());
				String intKeys = "@-1@";

				for(SysEnum se : ses.ToJavaList())
				{
					FrmRB rb = null;
					String intKey = String.valueOf(se.getIntKey());
					if(DataType.IsNullOrEmpty(se.getStrKey())==false)
						intKey = se.getStrKey();
					intKeys+=intKey+"@";
					Entity en = rbs.GetEntityByKey(this.getFrmID() + "_" + this.getKeyOfEn() + "_" + intKey);
					if(en!=null)
						rb = (FrmRB)en;
					if(rb == null)
					{
						rb = new FrmRB();
						rb.setFrmID(this.getFrmID());
						rb.setKeyOfEn(this.getKeyOfEn());
						rb.setIntKey(intKey);
						rb.setLab(se.getLab());
						rb.setEnumKey(attr.getUIBindKey());
						rb.Insert(); //插入数据.

					}else{
						rb.setLab(se.getLab());
						rb.Update();
					}
				}
				//删除已经不存在的枚举值
				for(FrmRB frmrb : rbs.ToJavaList()){
					if(intKeys.contains("@"+ frmrb.getIntKey()+"@")== false)
						frmrb.Delete();
				}
				rbs.Retrieve(FrmRBAttr.FrmID, this.getFrmID(), FrmRBAttr.KeyOfEn, this.getKeyOfEn(), FrmRBAttr.IntKey);


			}
		}

		//加入单选按钮.
		ds.Tables.add(rbs.ToDataTableField("Sys_FrmRB"));
		return bp.tools.Json.ToJson(ds);
	}

	/**
	 复选框选择事件

	 @return
	*/
	public final String CheckBoxs_Init() throws Exception {
		DataSet ds = new DataSet();

		//放入表单字段.
		MapAttrs attrs = new MapAttrs(this.getFrmID());
		ds.Tables.add(attrs.ToDataTableField("Sys_MapAttr"));

		//属性.
		MapAttr attr = new MapAttr();
		attr.setMyPK(this.getFrmID() + "_" + this.getKeyOfEn());
		attr.Retrieve();

		//把分组加入里面.
		GroupFields gfs = new GroupFields(this.getFrmID());
		ds.Tables.add(gfs.ToDataTableField("Sys_GroupFields"));

		FrmRBs rbs = new FrmRBs();
		rbs.Retrieve(FrmRBAttr.FrmID, this.getFrmID(), FrmRBAttr.KeyOfEn, this.getKeyOfEn(), null);
		//加入单选按钮.
		ds.Tables.add(rbs.ToDataTableField("Sys_FrmRB"));


		return bp.tools.Json.ToJson(ds);
	}
	/**
	 执行保存

	 @return
	*/
	public final String RadioBtns_Save() throws Exception {
		//String json = context.Request.Form["data");
		//if (DataType.IsNullOrEmpty(json))
		String json = GetRequestVal("data");
		DataTable dt = null;

		try
		{
			dt = bp.tools.Json.ToDataTable(json);
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
			//  return json;
		}

		for (DataRow dr : dt.Rows)
		{
			FrmRB rb = new FrmRB();
			rb.setMyPK(dr.getValue("MyPK").toString());
			rb.Retrieve();

			rb.setScript(dr.getValue("Script").toString());
			rb.setFieldsCfg(dr.getValue("FieldsCfg").toString()); //格式为 @字段名1=1@字段名2=0
			rb.setTip(dr.getValue("Tip").toString()); //提示信息

			rb.setVal(dr.getValue("SetVal").toString()); //设置值.

			rb.DirectUpdate();
		}

		return "保存成功.";
	}

		///#endregion


		///#region xxx 界面
	/**
	 初始化正则表达式界面

	 @return
	*/
	public final String RegularExpression_Init() throws Exception {
		DataSet ds = new DataSet();

		MapExts mapExts = new MapExts();
		mapExts.Retrieve(MapExtAttr.AttrOfOper, this.getKeyOfEn(), MapExtAttr.FK_MapData, this.getFrmID(), null);
		ds.Tables.add(mapExts.ToDataTableField("Sys_MapExt"));

		bp.sys.xml.RegularExpressions res = new bp.sys.xml.RegularExpressions();
		res.Retrieve("ForCtrl", "TB");

		DataTable myDT = res.ToDataTable();
		myDT.TableName = "RE";
		ds.Tables.add(myDT);


		bp.sys.xml.RegularExpressionDtls dtls = new bp.sys.xml.RegularExpressionDtls();
		dtls.RetrieveAll();
		DataTable myDTDtls = dtls.ToDataTable();
		myDTDtls.TableName = "REDtl";
		ds.Tables.add(myDTDtls);

		return bp.tools.Json.ToJson(ds);
	}
	public final String RegularExpressionNum_Init() throws Exception {
		DataSet ds = new DataSet();

		MapExts mes = new MapExts();
		mes.Retrieve("AttrOfOper", this.getKeyOfEn(), "FK_MapData", this.getFrmID(), null);
		ds.Tables.add(mes.ToDataTableField("Sys_MapExt"));

		bp.sys.xml.RegularExpressions res = new bp.sys.xml.RegularExpressions();
		res.Retrieve("ForCtrl", "TBNum");

		DataTable myDT = res.ToDataTable();
		myDT.TableName = "RE";
		ds.Tables.add(myDT);


		bp.sys.xml.RegularExpressionDtls dtls = new bp.sys.xml.RegularExpressionDtls();
		dtls.RetrieveAll();
		DataTable myDTDtls = dtls.ToDataTable();
		myDTDtls.TableName = "REDtl";
		ds.Tables.add(myDTDtls);

		return bp.tools.Json.ToJson(ds);
	}
	private void RegularExpression_Save_Tag(String tagID) throws Exception {
		String val = this.GetValFromFrmByKey("TB_Doc_" + tagID);
		if (DataType.IsNullOrEmpty(val))
		{
			return;
		}

		MapExt me = new MapExt();
		me.setMyPK(MapExtXmlList.TBFullCtrl + "_" + this.getFrmID() + "_" + this.getKeyOfEn() + "_" + tagID);
		me.setFrmID(this.getFrmID());
		me.setAttrOfOper(this.getKeyOfEn());
		me.setExtType("RegularExpression");
		me.setTag(tagID);
		me.setDoc(val);
		me.setTag1(this.GetValFromFrmByKey("TB_Tag1_" + tagID));
		me.Save();
	}


	/**
	 执行 保存.

	 @return
	*/
	public final String RegularExpression_Save() throws Exception {
		//删除该字段的全部扩展设置.
		MapExt me = new MapExt();
		me.Delete(MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.ExtType, MapExtXmlList.RegularExpression, MapExtAttr.AttrOfOper, this.getKeyOfEn());

		//执行存盘.
		RegularExpression_Save_Tag("onblur");
		RegularExpression_Save_Tag("onchange");
		RegularExpression_Save_Tag("onclick");
		RegularExpression_Save_Tag("ondblclick");
		RegularExpression_Save_Tag("onkeypress");
		RegularExpression_Save_Tag("onkeyup");
		RegularExpression_Save_Tag("onsubmit");

		return "保存成功...";
	}


	private String no;
	private String name;
	private String fk_dept;
	private String oid;
	private String kvs;
	public final String DealSQL(String sql, String key)
	{
		sql = sql.replace("@Key", key);
		sql = sql.replace("@key", key);
		sql = sql.replace("@Val", key);
		sql = sql.replace("@val", key);

		sql = sql.replace("@WebUser.No", WebUser.getNo());
		sql = sql.replace("@WebUser.Name", WebUser.getName());
		sql = sql.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
		sql = sql.replace("@WebUser.DeptNo", WebUser.getDeptNo());

		if (oid != null)
		{
			sql = sql.replace("@OID", oid);
		}

		if (DataType.IsNullOrEmpty(kvs) == false && sql.contains("@") == true)
		{
			String[] strs = kvs.split("[~]", -1);
			for (String s : strs)
			{
				if (DataType.IsNullOrEmpty(s) || s.contains("=") == false)
				{
					continue;
				}
				String[] mykv = s.split("[=]", -1);
				sql = sql.replace("@" + mykv[0], mykv[1]);

				if (sql.contains("@") == false)
				{
					break;
				}
			}
		}
		return sql;
	}

	/**
	 返回

	 @return
	*/
	public final String PopVal_Init() throws Exception {
		MapExt ext = new MapExt();
		ext.setMyPK(this.getMyPK());
		if (ext.RetrieveFromDBSources() == 0)
		{
			// throw new Exception("err@主键=" + ext.getMyPK() + "的配置数据丢失");
			ext.setPopValSelectModel(PopValSelectModel.One);
			ext.setPopValWorkModel(PopValWorkModel.TableOnly);
		}

		// ext.SetValByKey
		return ext.PopValToJson();
	}
	/**
	 保存设置.

	 @return
	*/
	public final String PopVal_Save()
	{
		try
		{
			MapExt me = new MapExt();
			me.setMyPK(this.getFK_MapExt());
			me.setFrmID(this.getFrmID());
			me.setExtType("PopVal");
			me.setAttrOfOper(this.getKeyOfEn());
			me.RetrieveFromDBSources();

			String valWorkModel = this.GetValFromFrmByKey("Model");

			switch (valWorkModel)
			{
				case "None":
					me.setPopValWorkModel(PopValWorkModel.None);
					break;
				case "SelfUrl": //URL模式.
					me.setPopValWorkModel(PopValWorkModel.SelfUrl);
					me.setPopValUrl(this.GetValFromFrmByKey("TB_Url"));
					break;
				case "TableOnly": //表格模式.
					me.setPopValWorkModel(PopValWorkModel.TableOnly);
					me.setPopValEntitySQL(this.GetValFromFrmByKey("TB_Table_SQL"));
					break;
				case "TablePage": //分页模式.
					me.setPopValWorkModel(PopValWorkModel.TablePage);
					me.setPopValTablePageSQL(this.GetValFromFrmByKey("TB_TablePage_SQL"));
					me.setPopValTablePageSQLCount(this.GetValFromFrmByKey("TB_TablePage_SQLCount"));
					break;
				case "Group": //分组模式.
					me.setPopValWorkModel(PopValWorkModel.Group);

					me.setPopValGroupSQL(this.GetValFromFrmByKey("TB_GroupModel_Group"));
					me.setPopValEntitySQL(this.GetValFromFrmByKey("TB_GroupModel_Entity"));

					//me.PopValUrl = this.GetValFromFrmByKey("TB_Url");
					break;
				case "Tree": //单实体树.
					me.setPopValWorkModel(PopValWorkModel.Tree);
					me.setPopValTreeSQL(this.GetValFromFrmByKey("TB_TreeSQL"));
					me.setPopValTreeParentNo(this.GetValFromFrmByKey("TB_TreeParentNo"));
					break;
				case "TreeDouble": //双实体树.
					me.setPopValWorkModel(PopValWorkModel.TreeDouble);
					me.setPopValTreeSQL(this.GetValFromFrmByKey("TB_DoubleTreeSQL")); // 树SQL
					me.setPopValTreeParentNo(this.GetValFromFrmByKey("TB_DoubleTreeParentNo"));
					me.setPopValDoubleTreeEntitySQL(this.GetValFromFrmByKey("TB_DoubleTreeEntitySQL")); //实体SQL
					break;
				default:
					break;
			}

			//高级属性.
			me.setW(Integer.parseInt(this.GetValFromFrmByKey("TB_Width")));
			me.setH(Integer.parseInt(this.GetValFromFrmByKey("TB_Height")));
			me.setPopValColNames(this.GetValFromFrmByKey("TB_ColNames")); //中文列名的对应.
			me.setPopValTitle(this.GetValFromFrmByKey("TB_Title")); //标题.
			me.setPopValSearchTip(this.GetValFromFrmByKey("TB_PopValSearchTip")); //关键字提示.
			me.setPopValSearchCond(this.GetValFromFrmByKey("TB_PopValSearchCond")); //查询条件.

			//数据返回格式.
			String popValFormat = this.GetValFromFrmByKey("PopValFormat");
			switch (popValFormat)
			{
				case "OnlyNo":
					me.setPopValFormat(PopValFormat.OnlyNo);
					break;
				case "OnlyName":
					me.setPopValFormat(PopValFormat.OnlyName);
					break;
				case "NoName":
					me.setPopValFormat(PopValFormat.NoName);
					break;
				default:
					break;
			}

			//选择模式.
			String seleModel = this.GetValFromFrmByKey("PopValSelectModel");
			if (Objects.equals(seleModel, "One"))
			{
				me.setPopValSelectModel(PopValSelectModel.One);
			}
			else
			{
				me.setPopValSelectModel(PopValSelectModel.More);
			}

			me.Save();
			return "保存成功.";
		}
		catch (Exception ex)
		{
			return "@保存失败:" + ex.getMessage();
		}
	}

		///#endregion xxx 界面方法.


		///#region PopFullCtrl 功能界面 .
	/**
	 保存

	 @return
	*/
	public final String PopFullCtrl_Save() throws Exception {
		try
		{
			MapExt me = new MapExt();
			int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.PopFullCtrl, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

			me.setFrmID(this.getFrmID());
			me.setAttrOfOper(this.getKeyOfEn());
			me.setDBSrcNo(this.GetValFromFrmByKey("FK_DBSrc"));
			me.setDoc(this.GetValFromFrmByKey("TB_Doc")); //要执行的SQL.

			me.setExtType(MapExtXmlList.PopFullCtrl);

			//执行保存.
			me.InitPK();

			if (me.Update() == 0)
			{
				me.Insert();
			}

			return "保存成功.";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	public final String PopFullCtrl_Delete() throws Exception {
		MapExt me = new MapExt();
		me.Delete(MapExtAttr.ExtType, MapExtXmlList.PopFullCtrl, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

		return "删除成功.";
	}
	public final String PopFullCtrl_Init() throws Exception {
		DataSet ds = new DataSet();

		//加载数据源.
		SFDBSrcs srcs = new SFDBSrcs();
		srcs.RetrieveAll();
		DataTable dtSrc = srcs.ToDataTableField("dt");
		dtSrc.TableName = "Sys_SFDBSrc";
		ds.Tables.add(dtSrc);

		// 加载 mapext 数据.
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.PopFullCtrl, MapExtAttr.FK_MapData, this.getFrmID(), MapExtAttr.AttrOfOper, this.getKeyOfEn());

		if (i == 0)
		{
			me.setFrmID(this.getFrmID());
			me.setAttrOfOper(this.getKeyOfEn());
			me.setDBSrcNo("local");
		}

		//这个属性没有用.
		me.setW(i); //用于标记该数据是否保存?  从而不现实填充从表，填充下拉框.按钮是否可以用.
		if (me.getDBSrcNo().equals(""))
		{
			me.setDBSrcNo("local");
		}

		//去掉 ' 号.
		me.SetValByKey("Doc", me.getDoc());

		DataTable dt = me.ToDataTableField("Main");
		dt.TableName = "Sys_MapExt";
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}
	/**
	 填充从表

	 @return
	*/
	public final String PopFullCtrlDtl_Init() throws Exception {
		MapExt me = new MapExt(this.getMyPK());

		String[] strs = me.getTag1().split("[$]", -1);
		// 格式为: $ND101Dtl2:SQL.

		MapDtls dtls = new MapDtls();
		dtls.Retrieve(MapDtlAttr.FK_MapData, me.getFrmID(), null);
		for (String str : strs)
		{
			if (DataType.IsNullOrEmpty(str) || str.contains(":") == false)
			{
				continue;
			}

			String[] kvs = str.split("[:]", -1);
			String fk_mapdtl = kvs[0];
			String sql = kvs[1];

			for (MapDtl dtl : dtls.ToJavaList())
			{
				if (!Objects.equals(dtl.getNo(), fk_mapdtl))
				{
					continue;
				}
				//dtl.MTR = sql.Trim();//多表头去掉了
			}
		}

		for (MapDtl dtl : dtls.ToJavaList())
		{
			String cols = "";
			MapAttrs mattrs = new MapAttrs(dtl.getNo());
			for (MapAttr item : mattrs.ToJavaList())
			{
				if (Objects.equals(item.getKeyOfEn(), "OID") || Objects.equals(item.getKeyOfEn(), "RefPKVal") || Objects.equals(item.getKeyOfEn(), "RefPK"))
				{
					continue;
				}

				cols += item.getKeyOfEn() + ",";
			}
			dtl.setAlias(cols); //把ptable作为一个数据参数.
		}
		return dtls.ToJson("dt");
	}

	public final String PopFullCtrlDtl_Save() throws Exception {
		MapDtls dtls = new MapDtls(this.getFrmID());
		MapExt me = new MapExt(this.getMyPK());

		String str = "";
		for (MapDtl dtl : dtls.ToJavaList())
		{
			String sql = this.GetRequestVal("TB_" + dtl.getNo());
			sql = sql.trim();
			if (DataType.IsNullOrEmpty(sql) == true)
			{
				continue;
			}

			if (sql.contains("@Key") == false)
			{
				return "err@在配置从表:" + dtl.getNo() + " sql填写错误, 必须包含@Key列, @Key就是当前文本框输入的值. ";
			}

			str += "$" + dtl.getNo() + ":" + sql;
		}
		me.setTag1(str);
		me.Update();

		return "保存成功.";
	}

	public final String PopFullCtrlDDL_Init() throws Exception {
		MapExt myme = new MapExt(this.getMyPK());
		MapAttrs mattrs = new MapAttrs(myme.getFrmID());
		mattrs.Retrieve(MapAttrAttr.FK_MapData, myme.getFrmID(), MapAttrAttr.UIIsEnable, 1, MapAttrAttr.UIContralType, UIContralType.DDL.getValue(), null);

		String[] strs = myme.getTag().split("[$]", -1);
		for (MapAttr attr : mattrs.ToJavaList())
		{
			for (String s : strs)
			{
				if (s == null)
				{
					continue;
				}
				if (s.contains(attr.getKeyOfEn() + ":") == false)
				{
					continue;
				}

				String[] ss = s.split("[:]", -1);
				attr.setDefVal(ss[1]); //使用这个字段作为对应设置的sql.
			}
		}

		return mattrs.ToJson("dt");
	}
	public final String PopFullCtrlDDL_Save() throws Exception {
		MapExt myme = new MapExt(this.getMyPK());

		MapAttrs mattrs = new MapAttrs(myme.getFrmID());
		mattrs.Retrieve(MapAttrAttr.FK_MapData, myme.getFrmID(), MapAttrAttr.UIIsEnable, 1, MapAttrAttr.UIContralType, UIContralType.DDL.getValue(), null);

		MapExt me = new MapExt(this.getMyPK());

		String str = "";
		for (MapAttr attr : mattrs.ToJavaList())
		{

			String sql = this.GetRequestVal("TB_" + attr.getKeyOfEn());
			sql = sql.trim();
			if (DataType.IsNullOrEmpty(sql) == true)
			{
				continue;
			}

			if (sql.contains("@Key") == false)
			{
				return "err@在配置从表:" + attr.getKeyOfEn() + " sql填写错误, 必须包含@Key列, @Key就是当前文本框输入的值. ";
			}

			str += "$" + attr.getKeyOfEn() + ":" + sql;
		}
		me.setTag(str);
		me.Update();

		return "保存成功.";
	}

		///#endregion PopFullCtrl 功能界面.



		///#region 杨玉慧  表单设计--表单属性   JS编程
	public final String InitScript_Init()
	{
		try
		{
			//2019-07-26 zyt改造
			//String webPath = HttpRuntime.AppDomainExtAppPath.replace("\\", "/");
			String webPath = SystemConfig.getPathOfWebApp().replace("\\", "/");
			String filePath = webPath + "DataUser/JSLibData/" + this.getFrmID() + "_Self.js";
			String content = "";
			if (!new File(filePath).exists())
				content = "";
			else
				//content = File.ReadAllText(filePath);
				content = DataType.ReadTextFile(filePath);
			return content;
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	public final String InitScript_Save()
	{
		try
		{
			//2019-07-26 zyt改造
			//String webPath = HttpRuntime.AppDomainExtAppPath.replace("\\", "/");
			String webPath = SystemConfig.getPathOfWebApp().replace("\\", "/");
			String filePath = webPath + "DataUser/JSLibData/" + this.getFrmID() + "_Self.js";
			String content = ContextHolderUtils.getRequest().getParameter("JSDoc"); // this.context.Request.Params["JSDoc");

			//在应用程序当前目录下的File1.txt文件中追加文件内容，如果文件不存在就创建，默认编码
			//Files.writeString(filePath, content);
			DataType.WriteFile(filePath, content);
			return "保存成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}

	}

	public final String InitScript_Delete()
	{
		try
		{
			//2019-07-26 zyt改造
			//String webPath = HttpRuntime.AppDomainExtAppPath.replace("\\", "/");
			String webPath = SystemConfig.getPathOfWebApp().replace("\\", "/");
			String filePath = webPath + "DataUser/JSLibData/" + this.getFrmID() + "_Self.js";

			if ((new File(filePath)).isFile())
			{
				(new File(filePath)).delete();
			}

			return "删除成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}

		///#endregion

	//public String NRCMaterielDtlSave()
	//{
	//    String fk_Template = this.GetRequestVal("FK_Template");
	//    String workid = this.GetRequestVal("WorkId");
	//    String sql = "SELECT * FROM STARCO_TemplateNRCMaterielDtl WHERE FK_Template='" + fk_Template + "'";
	//    DataTable dt = new DataTable();
	//    dt = DBAccess.RunSQLReturnTable(sql);
	//    if (dt != null && dt.Rows.size() > 0)
	//    {
	//        //String sql1 = "SELECT * FROM ND105Dtl1 WHERE RefPK='" + workid + "'";
	//        //DataTable dt1 = new DataTable();
	//        //dt1 = DBAccess.RunSQLReturnTable(sql1);
	//        //if (dt1 != null && dt1.Rows.size() > 0)
	//        //{

	//        //}

	//        String delSql = "DELETE FROM ND105Dtl1 WHERE RefPK='" + workid + "'";
	//        DBAccess.RunSQLReturnString(delSql);

	//        for (int i = 0; i < dt.Rows.size(); i++)
	//        {
	//            GEDtl dtl = new GEDtl("ND105Dtl1");

	//            dtl.SetValByKey("MingChen", dt.Rows[i]["Name").toString());
	//            dtl.SetValByKey("JianHao", dt.Rows[i]["PartNumber").toString());
	//            dtl.SetValByKey("RefPK", dt.Rows[i]["Qty").toString());
	//            dtl.SetValByKey("ShuLiang", dt.Rows[i]["PCH").toString());
	//            dtl.SetValByKey("PiCiHao", dt.Rows[i]["Name").toString());
	//            dtl.SetValByKey("RDT", dt.Rows[i]["Name").toString());
	//            dtl.SetValByKey("Rec", dt.Rows[i]["Name").toString());

	//            String name = dt.Rows[i]["Name").toString();
	//            String jianHao = dt.Rows[i]["PartNumber").toString();
	//            String workId = workid;
	//            String shuLiang = dt.Rows[i]["Qty").toString();
	//            String piCiHao = dt.Rows[i]["PCH").toString();
	//            String rdt = DateTime.Now.ToString();
	//            String userNo = WebUser.getNo();

	//            String sql2 = "INSERT INTO ND105Dtl1(MingChen,JianHao,RefPK,ShuLiang,PiCiHao,RDT,Rec) VALUES('" + name + "','" + jianHao + "','" + workId + "','" + shuLiang + "','" + piCiHao + "','" + rdt + "','" + userNo + "')";
	//            String result = DBAccess.RunSQLReturnString(sql2);
	//        }

	//    }

	//    return "ok";
	//}

}
