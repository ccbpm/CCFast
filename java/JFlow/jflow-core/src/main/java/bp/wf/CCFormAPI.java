package bp.wf;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.difference.*;
import bp.wf.template.frm.*;
import bp.tools.*;
import bp.*;
import com.google.gson.JsonObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 表单引擎api
*/
public class CCFormAPI extends Dev2Interface
{
	/**
	 获得Pop的字段的值

	 @param fieldName
	 @param pk
	 @return 返回data, No=编号,Name=名称两个列.
	 @exception Exception
	*/
	public static DataTable GenerPopData2022(String pk, String fieldName)
	{
		//判断该字段是否启用了pop返回值？
		String dbVarStr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "SELECT  Tag1 AS VAL FROM Sys_FrmEleDB WHERE RefPKVal=" + dbVarStr + "RefPKVal AND EleID=" + dbVarStr + "EleID";
		ps.Add("RefPKVal", pk);
		ps.Add("EleID", fieldName);
		DataTable dtVals = DBAccess.RunSQLReturnTable(ps);

		DataTable dt = new DataTable();
		dt.Columns.Add("No");
		dt.Columns.Add("Name");

		String emps = "";
		//获取接受人并格式化接受人,
		if (dtVals.Rows.size() > 0)
		{
			for (DataRow dr : dtVals.Rows)
			{
				emps += dr.getValue(0).toString() + ",";
			}
		}

		if (emps.contains(",") && emps.contains(";"))
		{
			/*如果包含,; 例如 zhangsan,张三;lisi,李四;*/
			String[] myemps1 = emps.split("[;]", -1);
			for (String str : myemps1)
			{
				if (DataType.IsNullOrEmpty(str))
				{
					continue;
				}

				String[] ss = str.split("[,]", -1);
				DataRow dr = dt.NewRow();
				dr.setValue(0, ss[0]);
				dt.Rows.add(dr);
			}
			return dt;
		}

		emps = emps.replace(";", ",");
		emps = emps.replace("；", ",");
		emps = emps.replace("，", ",");
		emps = emps.replace("、", ",");
		emps = emps.replace("@", ",");

		// 把它加入接受人员列表中.
		String[] myemps = emps.split("[,]", -1);
		for (String s : myemps)
		{
			if (DataType.IsNullOrEmpty(s))
			{
				continue;
			}

			DataRow dr = dt.NewRow();
			dr.setValue(0, s);
			dt.Rows.add(dr);
		}
		return dt;
	}
	/**
	 仅获取表单数据

	 @param enName
	 @param pkval
	 @param atParas
	 @return
	*/

	private static DataSet GenerDBForVSTOExcelFrmModelOfEntity(String enName, Object pkval, String atParas) throws Exception {
		return GenerDBForVSTOExcelFrmModelOfEntity(enName, pkval, atParas, null);
	}

	private static DataSet GenerDBForVSTOExcelFrmModelOfEntity(String enName, Object pkval, String atParas, String specDtlFrmID) throws Exception {
		DataSet myds = new DataSet();


			///#region 主表

		Entity en = ClassFactory.GetEn(enName);
		en.setPKVal(pkval);

		// if (DataType.IsNullOrEmpty(pkval)==false)
		en.Retrieve();


		//设置外部传入的默认值.
		if (SystemConfig.isBSsystem() == true)
		{
			// 处理传递过来的参数。
			//2019-07-25 zyt改造
			for (String k : ContextHolderUtils.getRequest().getParameterMap().keySet())
			{
				en.SetValByKey(k, ContextHolderUtils.getRequest().getParameter(k));
			}
		}

		//主表数据放入集合.
		DataTable mainTable = en.ToDataTableField("Main");
		mainTable.setTableName("MainTable");
		myds.Tables.add(mainTable);


			///#region 主表 Sys_MapData
		String sql = "SELECT * FROM Sys_MapData WHERE 1=2 ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.setTableName("Sys_MapData");

		Map map = en.getEnMapInTime();
		DataRow dr = dt.NewRow();
		dr.setValue(MapDataAttr.No, enName);
		dr.setValue(MapDataAttr.Name, map.getEnDesc());
		dr.setValue(MapDataAttr.PTable, map.getPhysicsTable());
		dt.Rows.add(dr);
		myds.Tables.add(dt);

			///#endregion 主表 Sys_MapData


			///#region 主表 Sys_MapAttr
		sql = "SELECT * FROM Sys_MapAttr WHERE 1=2 ";
		dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Sys_MapAttr";
		for (Attr attr : map.getAttrs())
		{
			dr = dt.NewRow();
			dr.setValue(MapAttrAttr.MyPK, enName + "_" + attr.getKey());
			dr.setValue(MapAttrAttr.Name, attr.getDesc());

			dr.setValue(MapAttrAttr.MyDataType, attr.getMyDataType()); //数据类型.
			dr.setValue(MapAttrAttr.MinLen, attr.getMinLength()); //最小长度.
			dr.setValue(MapAttrAttr.MaxLen, attr.getMaxLength()); //最大长度.

			// 设置他的逻辑类型.
			dr.setValue(MapAttrAttr.LGType, 0); //逻辑类型.
			switch (attr.getMyFieldType())
			{
				case Enum:
					dr.setValue(MapAttrAttr.LGType, 1);
					dr.setValue(MapAttrAttr.UIBindKey, attr.getUIBindKey());

					//增加枚举字段.
					if (myds.contains(attr.getUIBindKey()) == false)
					{
						String mysql = "SELECT IntKey AS No, Lab as Name FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey='" + attr.getUIBindKey() + "' ORDER BY IntKey ";
						DataTable dtEnum = DBAccess.RunSQLReturnTable(mysql);
						dtEnum.TableName = attr.getUIBindKey();
						myds.Tables.add(dtEnum);
					}

					break;
				case FK:
					dr.setValue(MapAttrAttr.LGType, 2);

					Entities ens = attr.getHisFKEns();
					dr.setValue(MapAttrAttr.UIBindKey, ens.toString());

					//把外键字段也增加进去.
					if (myds.contains(ens.toString()) == false && attr.getUIIsReadonly() == false)
					{
						ens.RetrieveAll();
						DataTable mydt = ens.ToDataTableDescField();
						mydt.setTableName(ens.toString());
						myds.Tables.add(mydt);
					}
					break;
				default:
					break;
			}

			// 设置控件类型.
			dr.setValue(MapAttrAttr.UIContralType, attr.getUIContralType().getValue());
			dt.Rows.add(dr);
		}
		myds.Tables.add(dt);

			///#endregion 主表 Sys_MapAttr


			///#region //主表 Sys_MapExt 扩展属性
		////主表的配置信息.
		//sql = "SELECT * FROM Sys_MapExt WHERE 1=2";
		//dt = DBAccess.RunSQLReturnTable(sql);
		//dt.TableName = "Sys_MapExt";
		//myds.Tables.add(dt);

			///#endregion //主表 Sys_MapExt 扩展属性


			///#endregion


			///#region 从表
		for (EnDtl item : map.getDtls())
		{

				///#region  把从表的数据放入集合.

			Entities dtls = item.Ens;

			QueryObject qo = qo = new QueryObject(dtls);

			if (dtls.toString().contains("CYSheBeiUse") == true)
			{
				qo.addOrderBy("RDT"); //按照日期进行排序，不用也可以.
			}

			qo.AddWhere(item.RefKey, pkval);
			DataTable dtDtl = qo.DoQueryToTable();

			dtDtl.setTableName(item.getEnsName()); //修改明细表的名称.
			myds.Tables.add(dtDtl); //加入这个明细表.


				///#endregion 把从表的数据放入.


				///#region 从表 Sys_MapDtl (相当于mapdata)

			Entity dtl = dtls.getNewEntity();
			map = dtl.getEnMap();

			//明细表的 描述 .
			sql = "SELECT * FROM Sys_MapDtl WHERE 1=2";
			dt = DBAccess.RunSQLReturnTable(sql);
			dt.setTableName("Sys_MapDtl_For_" + item.getEnsName());

			dr = dt.NewRow();
			dr.setValue(MapDtlAttr.No, item.getEnsName());
			dr.setValue(MapDtlAttr.Name, item.getDesc());
			dr.setValue(MapDtlAttr.PTable, dtl.getEnMap().getPhysicsTable());
			dt.Rows.add(dr);
			myds.Tables.add(dt);


				///#endregion 从表 Sys_MapDtl (相当于mapdata)


				///#region 明细表 Sys_MapAttr
			sql = "SELECT * FROM Sys_MapAttr WHERE 1=2";
			dt = DBAccess.RunSQLReturnTable(sql);
			dt.setTableName("Sys_MapAttr_For_" + item.getEnsName());
			for (Attr attr : map.getAttrs())
			{
				dr = dt.NewRow();
				dr.setValue(MapAttrAttr.MyPK, enName + "_" + attr.getKey());
				dr.setValue(MapAttrAttr.Name, attr.getDesc());

				dr.setValue(MapAttrAttr.MyDataType, attr.getMyDataType()); //数据类型.
				dr.setValue(MapAttrAttr.MinLen, attr.getMinLength()); //最小长度.
				dr.setValue(MapAttrAttr.MaxLen, attr.getMaxLength()); //最大长度.

				// 设置他的逻辑类型.
				dr.setValue(MapAttrAttr.LGType, 0); //逻辑类型.
				switch (attr.getMyFieldType())
				{
					case Enum:
						dr.setValue(MapAttrAttr.LGType, 1);
						dr.setValue(MapAttrAttr.UIBindKey, attr.getUIBindKey());

						//增加枚举字段.
						if (myds.contains(attr.getUIBindKey()) == false)
						{
							String mysql = "SELECT IntKey AS No, Lab as Name FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey='" + attr.getUIBindKey() + "' ORDER BY IntKey ";
							DataTable dtEnum = DBAccess.RunSQLReturnTable(mysql);
							dtEnum.setTableName(attr.getUIBindKey());
							myds.Tables.add(dtEnum);
						}
						break;
					case FK:
						dr.setValue(MapAttrAttr.LGType, 2);

						Entities ens = attr.getHisFKEns();
						dr.setValue(MapAttrAttr.UIBindKey, ens.toString());

						//把外键字段也增加进去.
						if (myds.contains(ens.toString()) == false && attr.getUIIsReadonly() == false)
						{
							ens.RetrieveAll();
							DataTable mydt = ens.ToDataTableDescField("dt");
							mydt.setTableName(ens.toString());
							myds.Tables.add(mydt);
						}
						break;
					default:
						break;
				}

				// 设置控件类型.
				dr.setValue(MapAttrAttr.UIContralType, attr.getUIContralType().getValue());
				dt.Rows.add(dr);
			}
			myds.Tables.add(dt);

				///#endregion 明细表 Sys_MapAttr

		}

			///#endregion

		return myds;
	}
	/**
	 仅获取表单数据

	 @param frmID 表单ID
	 @param pkval 主键
	 @param atParas 参数
	 @return 数据
	*/

	public static DataSet GenerDBForVSTOExcelFrmModel(String frmID, Object pkval, String atParas) throws Exception {
		return GenerDBForVSTOExcelFrmModel(frmID, pkval, atParas, null);
	}

	public static DataSet GenerDBForVSTOExcelFrmModel(String frmID, Object pkval, String atParas, String specDtlFrmID) throws Exception {
		//如果是一个实体类.
		if (frmID.toUpperCase().contains("BP."))
		{
			// 执行map同步.
			Entities ens = ClassFactory.GetEns(frmID + "s");
			Entity myen = ens.getNewEntity();
			myen.DTSMapToSys_MapData(null);
			return GenerDBForVSTOExcelFrmModelOfEntity(frmID, pkval, atParas, specDtlFrmID = null);
		}

		//数据容器,就是要返回的对象.
		DataSet myds = new DataSet();

		//映射实体.
		MapData md = new MapData(frmID);

		Map map = md.GenerHisMap();

		Entity en = null;
		if (map.getAttrs().contains("OID") == true)
		{
			//实体.
			GEEntityOID wk = new GEEntityOID(frmID);
			wk.setOID(Integer.parseInt(pkval.toString()));
			if (wk.RetrieveFromDBSources() == 0)
			{
				wk.Insert();
			}

			ExecEvent.DoFrm(md, EventListFrm.FrmLoadBefore, wk, null);

			en = wk;
		}

		if (map.getAttrs().contains("MyPK") == true)
		{
			//实体.
			GEEntityMyPK wk = new GEEntityMyPK(frmID);
			wk.setMyPK(pkval.toString());
			if (wk.RetrieveFromDBSources() == 0)
			{
				wk.Insert();
			}
			ExecEvent.DoFrm(md, EventListFrm.FrmLoadBefore, wk, null);
			en = wk;
		}

		//加载事件.

		//把参数放入到 En 的 Row 里面。
		if (DataType.IsNullOrEmpty(atParas) == false)
		{
			AtPara ap = new AtPara(atParas);
			for (String key : ap.getHisHT().keySet())
			{
				switch (key)
				{
					case "FrmID":
					case "FK_MapData":
						continue;
					default:
						break;
				}

				if (en.getRow().containsKey(key) == true) //有就该变.
				{
					en.getRow().SetValByKey(key, ap.GetValStrByKey(key));
				}
				else
				{
					en.getRow().put(key, ap.GetValStrByKey(key)); //增加他.
				}
			}
		}

		//属性.
		MapExt me = null;
		DataTable dtMapAttr = null;
		MapExts mes = null;


			///#region 表单模版信息.（含主、从表的，以及从表的枚举/外键相关数据）.
		//增加表单字段描述.
		String sql = "SELECT * FROM Sys_MapData WHERE No='" + frmID + "' ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Sys_MapData";
		myds.Tables.add(dt);

		//增加表单字段描述.
		sql = "SELECT * FROM Sys_MapAttr WHERE FK_MapData='" + frmID + "' ";
		dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Sys_MapAttr";
		myds.Tables.add(dt);

		//增加从表信息.
		sql = "SELECT * FROM Sys_MapDtl WHERE FK_MapData='" + frmID + "' ";
		dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Sys_MapDtl";
		myds.Tables.add(dt);


		//主表的配置信息.
		sql = "SELECT * FROM Sys_MapExt WHERE FK_MapData='" + frmID + "'";
		dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Sys_MapExt";
		myds.Tables.add(dt);


			///#region 加载 从表表单模版信息.（含 从表的枚举/外键相关数据）
		for (MapDtl item : md.getMapDtls().ToJavaList())
		{

				///#region 返回指定的明细表的数据.
			if (DataType.IsNullOrEmpty(specDtlFrmID) == true)
			{
			}
			else
			{
				if (!Objects.equals(item.getNo(), specDtlFrmID))
				{
					continue;
				}
			}

				///#endregion 返回指定的明细表的数据.

			//明细表的主表描述
			sql = "SELECT * FROM Sys_MapDtl WHERE No='" + item.getNo() + "'";
			dt = DBAccess.RunSQLReturnTable(sql);
			dt.TableName = "Sys_MapDtl_For_" + ((item.getAlias() == null || item.getAlias().isEmpty() ? item.getNo() : item.getAlias()));
			myds.Tables.add(dt);

			//明细表的表单描述
			sql = "SELECT * FROM Sys_MapAttr WHERE FK_MapData='" + item.getNo() + "'";
			dtMapAttr = DBAccess.RunSQLReturnTable(sql);
			dtMapAttr.TableName = "Sys_MapAttr_For_" + ((item.getAlias() == null || item.getAlias().isEmpty() ? item.getNo() : item.getAlias()));
			myds.Tables.add(dtMapAttr);

			//明细表的配置信息.
			sql = "SELECT * FROM Sys_MapExt WHERE FK_MapData='" + item.getNo() + "'";
			dt = DBAccess.RunSQLReturnTable(sql);
			dt.TableName = "Sys_MapExt_For_" + ((item.getAlias() == null || item.getAlias().isEmpty() ? item.getNo() : item.getAlias()));
			myds.Tables.add(dt);


				///#region 从表的 外键表/枚举
			mes = new MapExts(item.getNo());
			for (DataRow dr : dtMapAttr.Rows)
			{
				String lgType = dr.getValue("LGType").toString();
				//不是枚举/外键字段
				if (lgType.equals("0"))
				{
					continue;
				}

				String uiBindKey = dr.getValue("UIBindKey").toString();
				String mypk = dr.getValue("MyPK").toString();


					///#region 枚举字段
				if (lgType.equals("1"))
				{
					// 如果是枚举值, 判断是否存在.
					if (myds.contains(uiBindKey) == true)
					{
						continue;
					}

					String mysql = "SELECT IntKey AS No, Lab as Name FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey='" + uiBindKey + "' ORDER BY IntKey ";
					DataTable dtEnum = DBAccess.RunSQLReturnTable(mysql);
					dtEnum.TableName = uiBindKey;
					myds.Tables.add(dtEnum);
					continue;
				}

					///#endregion

				String UIIsEnable = dr.getValue("UIIsEnable").toString();
				if (UIIsEnable.equals("0")) //字段未启用
				{
					continue;
				}


					///#region 外键字段
				// 检查是否有下拉框自动填充。
				String keyOfEn = dr.getValue("KeyOfEn").toString();


					///#region 处理下拉框数据范围. for 小杨.
				Object tempVar = mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL, MapExtAttr.AttrOfOper, keyOfEn);
				me = tempVar instanceof MapExt ? (MapExt)tempVar : null;
				if (me != null) //有范围限制时
				{
					Object tempVar2 = me.getDoc();
					String fullSQL = tempVar2 instanceof String ? (String)tempVar2 : null;
					fullSQL = fullSQL.replace("~", ",");
					fullSQL = bp.wf.Glo.DealExp(fullSQL, en, null);

					dt = DBAccess.RunSQLReturnTable(fullSQL);

					dt.setTableName(mypk);
					myds.Tables.add(dt);
					continue;
				}

					///#endregion 处理下拉框数据范围.
				else //无范围限制时
				{
					// 判断是否存在.
					if (myds.contains(uiBindKey) == true)
					{
						continue;
					}

					myds.Tables.add(bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null));
				}

					///#endregion 外键字段
			}

				///#endregion 从表的 外键表/枚举

		}

			///#endregion 加载 从表表单模版信息.（含 从表的枚举/外键相关数据）


			///#endregion 表单模版信息.（含主、从表的，以及从表的枚举/外键相关数据）.


			///#region 主表数据
		if (SystemConfig.isBSsystem() == true)
		{
			// 处理传递过来的参数。
			for (String k : ContextHolderUtils.getRequest().getParameterMap().keySet())
			{
				en.SetValByKey(k, ContextHolderUtils.getRequest().getParameter(k));
			}
		}

		// 执行表单事件..
		String msg = ExecEvent.DoFrm(md, EventListFrm.FrmLoadBefore, en);
		if (DataType.IsNullOrEmpty(msg) == false)
		{
			throw new RuntimeException("err@错误:" + msg);
		}

		//重设默认值.
		en.ResetDefaultVal(null, null, 0);

		//执行装载填充.
		me = new MapExt();
		if (me.Retrieve(MapExtAttr.ExtType, MapExtXmlList.PageLoadFull, MapExtAttr.FK_MapData, frmID) == 1)
		{
			//执行通用的装载方法.
			MapAttrs attrs = new MapAttrs(frmID);
			MapDtls dtls = new MapDtls(frmID);
			Entity tempVar3 = DealPageLoadFull(en, me, attrs, dtls);
			en = tempVar3 instanceof GEEntity ? (GEEntity)tempVar3 : null;
		}

		//增加主表数据.
		DataTable mainTable = en.ToDataTableField(md.getNo());
		mainTable.TableName = "MainTable";
		myds.Tables.add(mainTable);


			///#endregion 主表数据


			///#region  从表数据
		for (MapDtl dtl : md.getMapDtls().ToJavaList())
		{

				///#region 返回指定的明细表的数据.
			if (DataType.IsNullOrEmpty(specDtlFrmID) == true)
			{
			}
			else
			{
				if (!Objects.equals(dtl.getNo(), specDtlFrmID))
				{
					continue;
				}
			}

				///#endregion 返回指定的明细表的数据.

			GEDtls dtls = new GEDtls(dtl.getNo());
			QueryObject qo = null;

			if (Objects.equals(dtl.getRefPK(), ""))
			{
				try
				{
					qo = new QueryObject(dtls);
					switch (dtl.getDtlOpenType())
					{
						case ForEmp: // 按人员来控制.
							qo.AddWhere(GEDtlAttr.RefPK, pkval);
							qo.addAnd();
							qo.AddWhere(GEDtlAttr.Rec, WebUser.getNo());
							break;
						case ForWorkID: // 按工作ID来控制
							qo.AddWhere(GEDtlAttr.RefPK, pkval);
							break;
						case ForPWorkID: // 按工作ID来控制
							qo.AddWhere(GEDtlAttr.RefPK, DBAccess.RunSQLReturnValInt("SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=" + pkval));
							break;
						case ForFID: // 按流程ID来控制.
							qo.AddWhere(GEDtlAttr.FID, pkval);
							break;
					}
				}
				catch (java.lang.Exception e)
				{
					dtls.getNewEntity().CheckPhysicsTable();
				}
			}
			else
			{
				qo = new QueryObject(dtls);
				qo.AddWhere(dtl.getRefPK(), pkval);
			}

			//条件过滤.
			if (DataType.IsNullOrEmpty(dtl.getFilterSQLExp()) == false)
			{
				String[] strs = dtl.getFilterSQLExp().split("[=]", -1);
				qo.addAnd();
				qo.AddWhere(strs[0], strs[1]);
			}

			//排序.
			if (DataType.IsNullOrEmpty(dtl.getOrderBySQLExp()) == false)
			{
				qo.addOrderBy(dtl.getOrderBySQLExp());
			}


			//从表
			DataTable dtDtl = qo.DoQueryToTable();

			// 为明细表设置默认值.
			MapAttrs mattrs = new MapAttrs(dtl.getNo());
			for (MapAttr attr : mattrs.ToJavaList())
			{
				//处理它的默认值.
				if (attr.getDefValReal().contains("@") == false)
				{
					continue;
				}

				for (DataRow dr : dtDtl.Rows)
				{
					dr.setValue(attr.getKeyOfEn(), attr.getDefVal());
				}
			}

			dtDtl.TableName = (dtl.getAlias() == null || dtl.getAlias().isEmpty() ? dtl.getNo() : dtl.getAlias()); //edited by liuxc,2017-10-10.如果有别名，则使用别名，没有则使用No
			myds.Tables.add(dtDtl); //加入这个明细表, 如果没有数据，xml体现为空.
		}

			///#endregion 从表数据


			///#region 主表的 外键表/枚举
		dtMapAttr = myds.GetTableByName("Sys_MapAttr");
		mes = md.getMapExts();
		for (DataRow dr : dtMapAttr.Rows)
		{
			String uiBindKey = dr.getValue("UIBindKey") instanceof String ? (String)dr.getValue("UIBindKey") : null;
			if (DataType.IsNullOrEmpty(uiBindKey) == true)
			{
				continue;
			}

			String myPK = dr.getValue("MyPK").toString();
			String lgType = dr.getValue("LGType").toString();

			if (lgType.equals("1"))
			{
				// 如果是枚举值, 判断是否存在.,
				if (myds.contains(uiBindKey) == true)
				{
					continue;
				}

				String mysql = "SELECT IntKey AS No, Lab as Name FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey='" + uiBindKey + "' ORDER BY IntKey ";
				DataTable dtEnum = DBAccess.RunSQLReturnTable(mysql);
				dtEnum.TableName = uiBindKey;
				myds.Tables.add(dtEnum);
				continue;
			}

			if (lgType.equals("2") == false)
			{
				continue;
			}

			String UIIsEnable = dr.getValue("UIIsEnable").toString();
			if (UIIsEnable.equals("0"))
			{
				continue;
			}

			// 检查是否有下拉框自动填充。
			String keyOfEn = dr.getValue("KeyOfEn").toString();
			String fk_mapData = dr.getValue("FK_MapData").toString();


				///#region 处理下拉框数据范围. for 小杨.
			Object tempVar4 = mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL, MapExtAttr.AttrOfOper, keyOfEn);
			me = tempVar4 instanceof MapExt ? (MapExt)tempVar4 : null;
			if (me != null)
			{
				Object tempVar5 = me.getDoc();
				String fullSQL = tempVar5 instanceof String ? (String)tempVar5 : null;
				fullSQL = fullSQL.replace("~", ",");
				fullSQL = bp.wf.Glo.DealExp(fullSQL, en, null);
				dt = DBAccess.RunSQLReturnTable(fullSQL);
				dt.TableName = myPK; //可能存在隐患，如果多个字段，绑定同一个表，就存在这样的问题.
				myds.Tables.add(dt);
				continue;
			}

				///#endregion 处理下拉框数据范围.

			dt = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null);
			dt.TableName = uiBindKey;
			myds.Tables.add(dt);
		}

			///#endregion 主表的 外键表/枚举

		String name = "";
		for (DataTable item : myds.Tables)
		{
			name += item.TableName + ",";
		}
		//返回生成的dataset.
		return myds;
	}
	/**
	 执行PageLoad装载数据

	 @param item
	 @param en
	 @param mattrs
	 @param dtls
	 @return
	*/

	public static Entity DealPageLoadFull(Entity en, MapExt item, MapAttrs mattrs, MapDtls dtls, boolean isSelf, int nodeID) throws Exception {
		return DealPageLoadFull(en, item, mattrs, dtls, isSelf, nodeID, 0);
	}

	public static Entity DealPageLoadFull(Entity en, MapExt item, MapAttrs mattrs, MapDtls dtls, boolean isSelf) throws Exception {
		return DealPageLoadFull(en, item, mattrs, dtls, isSelf, 0, 0);
	}

	public static Entity DealPageLoadFull(Entity en, MapExt item, MapAttrs mattrs, MapDtls dtls) throws Exception {
		return DealPageLoadFull(en, item, mattrs, dtls, false, 0, 0);
	}

	public static Entity DealPageLoadFull(Entity en, MapExt item, MapAttrs mattrs, MapDtls dtls, boolean isSelf, int nodeID, long workID) throws Exception {
		if (item == null)
		{
			return en;
		}

		DataTable dt = null;
		String sql = item.getDoc();
		/* 如果有填充主表的sql  */
		sql = bp.wf.Glo.DealExp(sql, en, null);
		String fk_dbSrc = item.getDBSrcNo();
		//填充方式，0=sql，1=url,2=CCFromRef.js , 3=webapi
		String doWay = item.getDBType();

		SFDBSrc sfdb = null;
		//如果是sql方式填充
		if (doWay.equals("0") || doWay.equals("None"))
		{
			if (DataType.IsNullOrEmpty(fk_dbSrc) == false && fk_dbSrc.equals("local") == false)
			{
				sfdb = new SFDBSrc(fk_dbSrc);
			}
			if (DataType.IsNullOrEmpty(sql) == false)
			{
				if (DataType.IsNullOrEmpty(sql) == false)
				{
					int num = sql.toUpperCase().split("WHERE").length - 1;
					if (num == 1)
					{
						String sqlext = sql.substring(0, sql.toUpperCase().indexOf("WHERE"));
						sqlext = sql.substring(sqlext.length() + 1);
						if (sqlext.contains("@"))
						{
							throw new RuntimeException("设置的sql有错误可能有没有替换的变量:" + sql);
						}
					}
					if (num > 1 && sql.contains("@"))
					{
						throw new RuntimeException("设置的sql有错误可能有没有替换的变量:" + sql);
					}
					if (sfdb != null)
					{
						dt = sfdb.RunSQLReturnTable(sql);
					}
					else
					{
						dt = DBAccess.RunSQLReturnTable(sql);
					}

					Attrs attrs = en.getEnMap().getAttrs();

					if (dt.Rows.size() == 1)
					{
						DataRow dr = dt.Rows.get(0);
						for (DataColumn dc : dt.Columns)
						{
							//去掉一些不需要copy的字段.
							switch (dc.ColumnName)
							{
								case WorkAttr.OID:
								case WorkAttr.FID:
								case WorkAttr.Rec:
								case WorkAttr.MD5:
								case GERptAttr.FlowEnder:
								case GERptAttr.FlowEnderRDT:
								case GERptAttr.AtPara:
								case GERptAttr.PFlowNo:
								case GERptAttr.PWorkID:
								case GERptAttr.PNodeID:
								case GERptAttr.BillNo:
								case GERptAttr.FlowDaySpan:
								case "RefPK":
								case WorkAttr.RecText:
									continue;
								default:
									break;
							}

							//如果不包含数据库.
							if (attrs.contains(dc.ColumnName) == false)
							{
								continue;
							}

							//开始赋值.
							if (DataType.IsNullOrEmpty(en.GetValStringByKey(dc.ColumnName)) || Objects.equals(en.GetValStringByKey(dc.ColumnName), "0") || en.GetValStringByKey(dc.ColumnName).contains("0.0"))
							{
								en.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
								continue;
							}

							//获取attr
							Entity entity = mattrs.GetEntityByKey("KeyOfEn", dc.ColumnName);
							if (entity != null)
							{
								MapAttr attr = (MapAttr)entity;
								if (attr.getLGType() == FieldTypeS.Enum && en.GetValStringByKey(dc.ColumnName).equals("-1"))
								{
									en.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
									continue;
								}
								continue;
							}

						}
					}
				}
			}
		}
		//如果是webapi方式填充
		else if (doWay.equals("3"))
		{
			//请求地址
			String apiUrl = sql;
			//设置请求头
			Hashtable headerMap = new Hashtable();

			//设置返回值格式
			headerMap.put("Content-Type", "application/json");
			//设置token，用于接口校验
			String token = WebUser.getToken();
			if (DataType.IsNullOrEmpty(token) == true || token.contains(" "))
				throw new RuntimeException("err@非法的Token.");
			if (token.length() <= 6)
				throw new Exception("err@非法的Token." + token);
			headerMap.put("Authorization", token);

			try
			{
				//post方式请求数据
				String postData = bp.tools.HttpClientUtil.doPost(apiUrl,null, headerMap);
				//数据序列化
				JSONObject jsonData = JSONObject.fromObject(postData);
				//code=200，表示请求成功，否则失败
				if (!jsonData.get("code").toString().equals("200"))
					return en;

				//获取返回的数据
				JSONObject data = JSONObject.fromObject(jsonData.getString("data"));
				//获取主表数据
				String mainTable = data.getString("mainTable");
				dt = Json.ToDataTable(mainTable);

				//获取全部附件数据
				JSONArray athsJSON = JSONArray.fromObject(jsonData.getString("aths"));
				for (int i = 0; i < athsJSON.size(); i++)
				{
					//获取附件
					JSONObject athDatas = athsJSON.getJSONObject(i);
					//获取附件组件ID
					String FK_FrmAttachment = athDatas.getString("attachmentid");
					//获取当前组件中的附件数据
					JSONArray athArryData =JSONArray.fromObject(athDatas.getString("attachmentdbs"));
					//填充附件数据
					for (int k = 0; k < athArryData.size(); k++)
					{
						JSONObject athData = athArryData.getJSONObject(k);
						//生成mypk主键值
						String guid = DBAccess.GenerGUID(0, null, null);
						FrmAttachment attachment = new FrmAttachment(FK_FrmAttachment);

						//是否要先删除掉原有附件？根据实际需求，再做调整
						//FrmAttachmentDBs attachmentDBs = new FrmAttachmentDBs();
						//attachmentDBs.Retrieve(FrmAttachmentDBAttr.RefPKVal, workID, FrmAttachmentDBAttr.FK_MapData, attachment.getFrmID());
						//attachmentDBs.Delete();

						//插入数据
						FrmAttachmentDB attachmentDB = new FrmAttachmentDB();
						attachmentDB.setMyPK(guid);
						attachmentDB.setFKFrmAttachment(FK_FrmAttachment);
						attachmentDB.setFrmID(attachment.getFrmID());
						attachmentDB.setRefPKVal( String.valueOf(workID));
						attachmentDB.setFID(0); //先默认为0
						attachmentDB.setRec(athData.getString("rec")); //执行人
						attachmentDB.setFileFullName(athData.getString("fileFullName")); //附件全路径
						attachmentDB.setFileName(athData.getString("fileName")); //附件名称
						attachmentDB.setFileExts(athData.getString("fileExts")); //文件类型
						attachmentDB.setSort(athData.getString("sort")); //附件类型
						attachmentDB.setDeptNo(athData.getString("fk_dept")); //上传人所在部门
						attachmentDB.setDeptName(athData.getString("fk_deptName")); //上传人所在部门名称
						attachmentDB.setRecName(athData.getString("recName")); //上传人名称
						attachmentDB.setRDT(athData.getString("rdt")); //上传时间
						attachmentDB.setUploadGUID(guid);
						attachment.Insert();
					}
				}

				//获取从表数据
				JSONArray dtlJSON = jsonData.getJSONArray("dtls");
				for (int i = 0; i < dtlJSON.size(); i++)
				{
					JSONObject dtlDatas = dtlJSON.getJSONObject(i);
					//获取从表编号
					String dtlNo = dtlDatas.getString("dtlNo");
					//定义map
					MapDtl dtl = new MapDtl(dtlNo);
					//插入之前判断
					GEDtls gedtls = null;
					try
					{
						gedtls = new GEDtls(dtl.getNo());
						if (dtl.getDtlOpenType() == DtlOpenType.ForFID)
						{
							if (gedtls.RetrieveByAttr(GEDtlAttr.RefPK, workID) > 0)
							{
								continue;
							}
						}
						else
						{
							//如果存在数据，默认先删除
							if (gedtls.RetrieveByAttr(GEDtlAttr.RefPK, en.getPKVal()) > 0)
							{
								gedtls.Delete(GEDtlAttr.RefPK, en.getPKVal());
							}
						}
					}
					catch (RuntimeException ex)
					{
						bp.en.Entity tempVar = gedtls.getNewEntity();
						(tempVar instanceof GEDtl ? (GEDtl)tempVar : null).CheckPhysicsTable();
					}
					//获取从表数据
					JSONArray dtlArryData = dtlDatas.getJSONArray("dtl");
					for (int k = 0; k < dtlArryData.size(); k++)
					{
						//获取一条数据
						JSONObject dtlData = dtlArryData.getJSONObject(k);
						//从表数据
						String dtlDataStr = dtlData.getString("dtlData");
						//从表附件数据
						JSONArray dtlAthData = dtlData.getJSONArray("dtlAths");
						//从表数据字符串，转换成datatable
						DataTable dtlDt = Json.ToDataTable(dtlDataStr);
						//执行数据插入
						for (DataRow dr : dtlDt.Rows)
						{
							bp.en.Entity tempVar2 = gedtls.getNewEntity();
							GEDtl gedtl = tempVar2 instanceof GEDtl ? (GEDtl)tempVar2 : null;
							for (DataColumn dc : dt.Columns)
							{
								gedtl.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
							}

							switch (dtl.getDtlOpenType())
							{
								case ForEmp: // 按人员来控制.
									gedtl.setRefPK(en.getPKVal().toString());
									gedtl.setFID(Long.parseLong(en.getPKVal().toString()));
									break;
								case ForWorkID: // 按工作ID来控制
									gedtl.setRefPK(en.getPKVal().toString());
									gedtl.setFID(Long.parseLong(en.getPKVal().toString()));
									break;
								case ForFID: // 按流程ID来控制.
									gedtl.setRefPK(String.valueOf(workID));
									gedtl.setFID(Long.parseLong(en.getPKVal().toString()));
									break;
							}
							gedtl.setRDT(DataType.getCurrentDateTime());
							gedtl.setRec(WebUser.getNo());
							gedtl.Insert();
						}
					}
				}
			}
			catch (RuntimeException ex)
			{
				throw new RuntimeException("接口请求失败,message:" + ex.getMessage().toString());
			}
		}
		if (DataType.IsNullOrEmpty(item.getTag1()) || item.getTag1().length() < 15)
		{
			return en;
		}

		// 填充从表.
		for (MapDtl dtl : dtls.ToJavaList())
		{
			//如果有数据，就不要填充了.

			String[] sqls = item.getTag1().split("[$]", -1);
			for (String mysql : sqls)
			{
				if (DataType.IsNullOrEmpty(mysql))
				{
					continue;
				}
				if (mysql.contains(dtl.getNo() + ":") == false)
				{
					continue;
				}
				if (mysql.equals(dtl.getNo() + ":") == true)
				{
					continue;
				}


					///#region 处理sql.
				sql = Glo.DealSQLExp(mysql.replace(dtl.getNo() + ":", "").toString(), en, null);

					///#endregion 处理sql.

				if (DataType.IsNullOrEmpty(sql))
				{
					continue;
				}

				int num = sql.toUpperCase().split("WHERE").length - 1;
				if (num == 1)
				{
					String sqlext = sql.substring(0, sql.toUpperCase().indexOf("WHERE"));
					sqlext = sql.substring(sqlext.length() + 1);
					if (sqlext.contains("@"))
					{
						throw new RuntimeException("设置的sql有错误可能有没有替换的变量:" + sql);
					}
				}
				if (num > 1 && sql.contains("@"))
				{
					throw new RuntimeException("设置的sql有错误可能有没有替换的变量:" + sql);
				}

				if (isSelf == true)
				{
					MapDtl mdtlSln = new MapDtl();
					mdtlSln.setNo(dtl.getNo() + "_" + nodeID);
					int result = mdtlSln.RetrieveFromDBSources();
					if (result != 0)
					{
						dtl.setDtlOpenType(mdtlSln.getDtlOpenType());
					}
				}



				GEDtls gedtls = null;

				try
				{
					gedtls = new GEDtls(dtl.getNo());
					if (dtl.getDtlOpenType() == DtlOpenType.ForFID)
					{
						if (gedtls.RetrieveByAttr(GEDtlAttr.RefPK, workID) > 0)
						{
							continue;
						}
					}
					else
					{
						if (gedtls.RetrieveByAttr(GEDtlAttr.RefPK, en.getPKVal()) > 0)
						{
							continue;
						}
					}


					//gedtls.Delete(GEDtlAttr.RefPK, en.getPKVal());
				}
				catch (RuntimeException ex)
				{
					bp.en.Entity tempVar3 = gedtls.getNewEntity();
					(tempVar3 instanceof GEDtl ? (GEDtl)tempVar3 : null).CheckPhysicsTable();
				}

				sql = sql.startsWith(dtl.getNo() + "=") ? sql.substring((dtl.getNo() + "=").length()) : sql;
				if (sfdb != null)
				{
					dt = sfdb.RunSQLReturnTable(sql);
				}
				else
				{
					dt = DBAccess.RunSQLReturnTable(sql);
				}

				for (DataRow dr : dt.Rows)
				{
					bp.en.Entity tempVar4 = gedtls.getNewEntity();
					GEDtl gedtl = tempVar4 instanceof GEDtl ? (GEDtl)tempVar4 : null;
					for (DataColumn dc : dt.Columns)
					{
						gedtl.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
					}

					switch (dtl.getDtlOpenType())
					{
						case ForEmp: // 按人员来控制.
							gedtl.setRefPK(en.getPKVal().toString());
							gedtl.setFID(Long.parseLong(en.getPKVal().toString()));
							break;
						case ForWorkID: // 按工作ID来控制
							gedtl.setRefPK(en.getPKVal().toString());
							gedtl.setFID(Long.parseLong(en.getPKVal().toString()));
							break;
						case ForFID: // 按流程ID来控制.
							gedtl.setRefPK(String.valueOf(workID));
							gedtl.setFID(Long.parseLong(en.getPKVal().toString()));
							break;
					}
					gedtl.setRDT(DataType.getCurrentDateTime());
					gedtl.setRec(WebUser.getNo());
					gedtl.Insert();
				}
			}
		}
		return en;
	}

	public static Entity DealPageLoadFullVue(Entity en, MapExts mapExts, MapAttrs mattrs) throws Exception {
		DataTable dt = null;
		String sql = "";
		String paras = "";
		for(String key : en.getRow().keySet())
		{
			paras += "@" + key + "=" + en.GetValByKey(key);
		}
		for(MapExt mapExt : mapExts.ToJavaList())
		{
			//填充主表
			if (mapExt.getExtModel().equals(MapExtXmlList.PageLoadFullMainTable))
			{
				mapExt.SetValByKey("Tag5",mapExt.getDoWay());
				String data = mapExt.GetFullData(paras, en.GetValStringByKey("OID"));
				if (DataType.IsNullOrEmpty(data) == true)
					throw new RuntimeException("主表填充失败:没有获取到数据");
				//data转换成JSON
				dt = bp.tools.Json.ToDataTable(data);
				if (dt.Rows.size() == 1)
				{
					DataRow dr = dt.Rows.get(0);
					for(DataColumn dc :dt.Columns)
					{
						//去掉一些不需要copy的字段.
						switch (dc.ColumnName)
						{
							case WorkAttr.OID:
							case WorkAttr.FID:
							case WorkAttr.Rec:
							case WorkAttr.MD5:
							case GERptAttr.FlowEnder:
							case GERptAttr.FlowEnderRDT:
							case GERptAttr.AtPara:
							case GERptAttr.PFlowNo:
							case GERptAttr.PWorkID:
							case GERptAttr.PNodeID:
							case GERptAttr.BillNo:
							case GERptAttr.FlowDaySpan:
							case "RefPK":
							case WorkAttr.RecText:
								continue;
							default:
								break;
						}

						//如果不包含数据库.
						if (mattrs.contains("KeyOfEn", dc.ColumnName) == false)
							continue;

						//开始赋值.
						if (DataType.IsNullOrEmpty(en.GetValStringByKey(dc.ColumnName)) || en.GetValStringByKey(dc.ColumnName) == "0" || en.GetValStringByKey(dc.ColumnName).contains("0.0"))
						{
							en.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
							continue;
						}

						//获取attr
						Entity entity = mattrs.GetEntityByKey("KeyOfEn", dc.ColumnName);
						if (entity != null)
						{
							MapAttr attr = (MapAttr)entity;
							if (attr.getLGType() == FieldTypeS.Enum && en.GetValStringByKey(dc.ColumnName).equals("-1"))
							{
								en.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
								continue;
							}
							continue;
						}

					}
				}

			}
			//填充从表
			if (mapExt.getExtModel().equals(MapExtXmlList.PageLoadFullDtl))
			{
				mapExt.SetValByKey("Tag5",mapExt.getDoWay());
				mapExt.GetFullDataDtl(paras, en.GetValStringByKey("OID"));
			}
			//填充下拉框
			if (mapExt.getExtModel().equals(MapExtXmlList.PageLoadFullDDL))
			{

			}
		}

		return en;
	}
	/**
	 获取上传附件集合信息

	 @param athDesc
	 @param pkval
	 @param FK_FrmAttachment
	 @param workid
	 @param fid
	 @param pworkid
	 @param isContantSelf
	 @param fk_node
	 @return
	*/

	public static bp.sys.FrmAttachmentDBs GenerFrmAttachmentDBs(FrmAttachment athDesc, String pkval, String FK_FrmAttachment, long workid, long fid, long pworkid, boolean isContantSelf, int fk_node) throws Exception {
		return GenerFrmAttachmentDBs(athDesc, pkval, FK_FrmAttachment, workid, fid, pworkid, isContantSelf, fk_node, null);
	}

	public static bp.sys.FrmAttachmentDBs GenerFrmAttachmentDBs(FrmAttachment athDesc, String pkval, String FK_FrmAttachment, long workid, long fid, long pworkid, boolean isContantSelf) throws Exception {
		return GenerFrmAttachmentDBs(athDesc, pkval, FK_FrmAttachment, workid, fid, pworkid, isContantSelf, 0, null);
	}

	public static bp.sys.FrmAttachmentDBs GenerFrmAttachmentDBs(FrmAttachment athDesc, String pkval, String FK_FrmAttachment, long workid, long fid, long pworkid) throws Exception {
		return GenerFrmAttachmentDBs(athDesc, pkval, FK_FrmAttachment, workid, fid, pworkid, true, 0, null);
	}

	public static bp.sys.FrmAttachmentDBs GenerFrmAttachmentDBs(FrmAttachment athDesc, String pkval, String FK_FrmAttachment, long workid, long fid) throws Exception {
		return GenerFrmAttachmentDBs(athDesc, pkval, FK_FrmAttachment, workid, fid, 0, true, 0, null);
	}

	public static bp.sys.FrmAttachmentDBs GenerFrmAttachmentDBs(FrmAttachment athDesc, String pkval, String FK_FrmAttachment, long workid) throws Exception {
		return GenerFrmAttachmentDBs(athDesc, pkval, FK_FrmAttachment, workid, 0, 0, true, 0, null);
	}

	public static bp.sys.FrmAttachmentDBs GenerFrmAttachmentDBs(FrmAttachment athDesc, String pkval, String FK_FrmAttachment) throws Exception {
		return GenerFrmAttachmentDBs(athDesc, pkval, FK_FrmAttachment, 0, 0, 0, true, 0, null);
	}

	public static FrmAttachmentDBs GenerFrmAttachmentDBs(FrmAttachment athDesc, String pkval, String FK_FrmAttachment, long workid, long fid, long pworkid, boolean isContantSelf, int fk_node, String fk_mapData) throws Exception {
		if (pkval == null)
		{
			pkval = "0"; //解决预览的时候的错误.
		}

		FrmAttachmentDBs dbs = new FrmAttachmentDBs();
		//查询使用的workId
		String ctrlWayId = "";
		if (FK_FrmAttachment.contains("AthMDtl") == true || athDesc.GetParaBoolen("IsDtlAth") == true || athDesc.getFrmID().startsWith("TS."))
		{
			ctrlWayId = pkval;
		}
		else
		{
			MapData mapData = new MapData(athDesc.getFrmID());
			if (fk_node == 0 && (mapData.getEntityType() == EntityType.FrmDict || mapData.getEntityType() == EntityType.FrmBill || mapData.getEntityType() == EntityType.FrmEntityNoName))
			{
				ctrlWayId = pkval;
			}
			else
			{
				ctrlWayId = bp.wf.Dev2Interface.GetAthRefPKVal(workid, pworkid, fid, fk_node, fk_mapData, athDesc);
			}
		}


		//如果是空的，就返回空数据结构. @lizhen.
		if (ctrlWayId.equals("0") == true)
		{
			return dbs;
		}

		QueryObject qo = new QueryObject(dbs);
		//从表附件
		if (FK_FrmAttachment.contains("AthMDtl") || athDesc.GetParaBoolen("IsDtlAth") == true || athDesc.getFrmID().startsWith("TS."))
		{
			/*如果是一个明细表的多附件，就直接按照传递过来的PK来查询.*/
			qo.AddWhere(FrmAttachmentDBAttr.RefPKVal, pkval);
			qo.addAnd();
			qo.AddWhere(FrmAttachmentDBAttr.NoOfObj, athDesc.getNoOfObj());
			qo.DoQuery();
			return dbs;
		}
		if (athDesc.getHisCtrlWay() == AthCtrlWay.MySelfOnly || athDesc.getHisCtrlWay() == AthCtrlWay.PK)
		{
			qo.AddWhere(FrmAttachmentDBAttr.RefPKVal, pkval);
			qo.addAnd();
			//流程处理
			if(FK_FrmAttachment.startsWith("ND")){
				qo.AddWhere(FrmAttachmentDBAttr.NoOfObj, FK_FrmAttachment.substring(FK_FrmAttachment.indexOf("_")+1));
			}else{ //低代码处理
				qo.AddWhere(FrmAttachmentDBAttr.FK_FrmAttachment, FK_FrmAttachment);
			}
			if (isContantSelf == false)
			{
				qo.addAnd();
				qo.AddWhere(FrmAttachmentDBAttr.Rec, "!=", WebUser.getNo());
			}
			qo.addOrderBy("Idx,RDT");
			qo.DoQuery();
			return dbs;
		}

		/* 继承模式 */
		if (athDesc.getAthUploadWay() == AthUploadWay.Interwork)
		{
			qo.AddWhere(FrmAttachmentDBAttr.RefPKVal, ctrlWayId);
		}
		else
		{
			qo.AddWhereIn(FrmAttachmentDBAttr.RefPKVal, "('" + ctrlWayId + "','" + pkval + "')");
		}

		qo.addAnd();
		qo.AddWhere(FrmAttachmentDBAttr.NoOfObj, athDesc.getNoOfObj());

		if (isContantSelf == false)
		{
			qo.addAnd();
			qo.AddWhere(FrmAttachmentDBAttr.Rec, "!=", WebUser.getNo());
		}
		qo.addOrderBy("Idx,RDT");
		qo.DoQuery();
		return dbs;
	}
	/**
	 获取从表数据，用于显示dtl.htm

	 @param frmID 表单ID
	 @param pkval 主键
	 @param atParas 参数
	 @return 数据
	*/
	public static DataSet GenerDBForCCFormDtl(String frmID, MapDtl dtl, String pkval, String atParas, String dtlRefPKVal, long fid) throws Exception {
		return GenerDBForCCFormDtl(frmID,dtl,pkval,atParas,dtlRefPKVal,fid,false,"");
	}
	public static DataSet GenerDBForCCFormDtl(String frmID, MapDtl dtl, String pkval, String atParas, String dtlRefPKVal, long fid,boolean ishistoryData, String trackID) throws Exception {
		//数据容器,就是要返回的对象.
		DataSet myds = new DataSet();

		//实体.
		Entity en = null;
		MapData mapData = new MapData(frmID);
		if (mapData.getEntityType() == EntityType.FrmEntityNoName)
		{
			en = new GEEntityNoName(frmID);
			en.SetValByKey("No", pkval);
			if (en.RetrieveFromDBSources() == 0)
			{
				en.SetValByKey("No", dtlRefPKVal);
				en.RetrieveFromDBSources();
			}
		}
		else
		{
			en = new GEEntity(frmID);
			en.SetValByKey("OID", Integer.parseInt(pkval));
			if (en.RetrieveFromDBSources() == 0)
			{
				en.SetValByKey("OID", Integer.parseInt(dtlRefPKVal));
				en.RetrieveFromDBSources();
			}
		}



		//把参数放入到 En 的 Row 里面。
		if (DataType.IsNullOrEmpty(atParas) == false)
		{
			AtPara ap = new AtPara(atParas);
			for (String key : ap.getHisHT().keySet())
			{
				try
				{
					if (en.getRow().containsKey(key) == true) //有就该变.
					{
						en.getRow().put(key, ap.GetValStrByKey(key));
					}
					else
					{
						en.getRow().put(key, ap.GetValStrByKey(key)); //增加他.
					}
				}
				catch (RuntimeException ex)
				{
					throw new RuntimeException(key);
				}
			}
		}
		if (SystemConfig.isBSsystem() == true)
		{
			// 处理传递过来的参数。
			for (String k : ContextHolderUtils.getRequest().getParameterMap().keySet())
			{
				en.SetValByKey(k, ContextHolderUtils.getRequest().getParameter(k));
			}
		}



			///#region 加载从表表单模版信息.

		DataTable Sys_MapDtl = dtl.ToDataTableField("Sys_MapDtl");
		myds.Tables.add(Sys_MapDtl);

		//明细表的表单描述
		MapAttrs attrs = dtl.getMapAttrs();
		DataTable Sys_MapAttr = attrs.ToDataTableField("Sys_MapAttr");
		myds.Tables.add(Sys_MapAttr);

		//明细表的配置信息.
		MapExts mes = dtl.getMapExts();
		DataTable Sys_MapExt = mes.ToDataTableField("Sys_MapExt");
		myds.Tables.add(Sys_MapExt);

		//启用附件，增加附件信息
		FrmAttachments attachments = new FrmAttachments();
		attachments.Retrieve(FrmAttachmentAttr.FK_MapData, dtl.getNo());
		DataTable Sys_FrmAttachment = attachments.ToDataTableField("Sys_FrmAttachment");
		myds.Tables.add(Sys_FrmAttachment);

			///#endregion 加载从表表单模版信息.


			///#region 把从表的- 外键表/枚举 加入 DataSet.

		MapExt me = null;
		DataTable ddlTable = new DataTable();
		ddlTable.Columns.Add("No");
		for (MapAttr attr : attrs.ToJavaList())
		{

			//没有绑定外键
			String uiBindKey = attr.getUIBindKey();
			if (DataType.IsNullOrEmpty(uiBindKey) == true)
			{
				continue;
			}


				///#region 枚举字段
			if (attr.getLGType() == FieldTypeS.Enum)
			{
				// 如果是枚举值, 判断是否存在.
				if (myds.contains(uiBindKey) == true)
				{
					continue;
				}
				//String mysql = "SELECT IntKey AS No, Lab as Name FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey='" + uiBindKey + "' ORDER BY IntKey ";

				String mysql = "";
				if (attr.getUIContralType() == UIContralType.CheckBok)
				{
					SysEnumMain main = new SysEnumMain(attr.getUIBindKey());
					if (main.getEnumType() == 1)
						mysql = "SELECT StrKey AS No, Lab as Name FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey='" + uiBindKey + "' ORDER BY IntKey ";
					else
						mysql = "SELECT IntKey AS No, Lab as Name FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey='" + uiBindKey + "' ORDER BY IntKey ";
				}
				else
				{
					if (attr.getMyDataType() == DataType.AppString)
						mysql = "SELECT StrKey AS No, Lab as Name FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey='" + uiBindKey + "' ORDER BY IntKey ";
					else
						mysql = "SELECT IntKey AS No, Lab as Name FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey='" + uiBindKey + "' ORDER BY IntKey ";
				}

				DataTable dtEnum = DBAccess.RunSQLReturnTable(mysql);
				dtEnum.TableName = uiBindKey;

				dtEnum.Columns.get(0).ColumnName = "No";
				dtEnum.Columns.get(1).ColumnName = "Name";

				myds.Tables.add(dtEnum);
				continue;
			}

				///#endregion

			// 检查是否有下拉框自动填充。
			String keyOfEn = attr.getKeyOfEn();


				///#region 处理下拉框数据范围. for 小杨.
			Object tempVar = mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL, MapExtAttr.AttrOfOper, keyOfEn);
			me = tempVar instanceof MapExt ? (MapExt)tempVar : null;
			if (me != null  && me.getDoWay().equals("0")==false  && myds.contains(uiBindKey) == false) //是否存在.
			{
				Object tempVar2 = me.getDoc();
				String fullSQL = tempVar2 instanceof String ? (String)tempVar2 : null;
				fullSQL = fullSQL.replace("~", "'");
				fullSQL = bp.wf.Glo.DealExp(fullSQL, en, null);

				if (DataType.IsNullOrEmpty(fullSQL) == true)
				{
					throw new RuntimeException("err@没有给AutoFullDLL配置SQL：MapExt：=" + me.getMyPK() + ",原始的配置SQL为:" + me.getDoc());
				}

				DataTable dt = DBAccess.RunSQLReturnTable(fullSQL);
				if (uiBindKey.toLowerCase().equals("blank"))
					dt.TableName = keyOfEn;
				else
					dt.TableName = uiBindKey;

				if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
				{
					if (dt.Columns.contains("NO") == true)
					{
						dt.Columns.get("NO").ColumnName = "No";
					}
					if (dt.Columns.contains("NAME") == true)
					{
						dt.Columns.get("NAME").ColumnName = "Name";
					}
					if (dt.Columns.contains("PARENTNO") == true)
					{
						dt.Columns.get("PARENTNO").ColumnName = "ParentNo";
					}
				}

				if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
				{
					if (dt.Columns.contains("no") == true)
					{
						dt.Columns.get("no").ColumnName = "No";
					}
					if (dt.Columns.contains("name") == true)
					{
						dt.Columns.get("name").ColumnName = "Name";
					}
					if (dt.Columns.contains("parentno") == true)
					{
						dt.Columns.get("parentno").ColumnName = "ParentNo";
					}
				}

				myds.Tables.add(dt);
				continue;
			}

				///#endregion 处理下拉框数据范围.


				///#region 外键字段

			// 判断是否存在.
			if (myds.contains(uiBindKey) == true)
			{
				continue;
			}

			// 获得数据.
			DataTable mydt = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey,en.getRow());
			if (uiBindKey.toLowerCase().equals("blank"))
				mydt.TableName = keyOfEn;
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

				///#endregion 外键字段
		}
		ddlTable.TableName = "UIBindKey";
		myds.Tables.add(ddlTable);

			///#endregion 把从表的- 外键表/枚举 加入 DataSet.


			///#region 把主表数据放入.

		//重设默认值.
		en.ResetDefaultVal(null, null, 0);


		//增加主表数据.
		DataTable mainTable = en.ToDataTableField(frmID);
		mainTable.TableName = "MainTable";
		myds.Tables.add(mainTable);

			///#endregion 把主表数据放入.


			///#region  把从表的数据放入.
		DataTable dtDtl = null;
		if(ishistoryData == false){
			dtDtl = GetDtlInfo(dtl, en, dtlRefPKVal);
		}else{
			//获取历史数据
			if(DataType.IsNullOrEmpty(trackID) == false){
				//根据TrackID从Sys_FrmDBVer表中获取历史数据
				FrmDBVer ver = new FrmDBVer();
				int i = ver.Retrieve(FrmDBVerAttr.TrackID,trackID);
				if(i==1){
					String dtlStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", ver.getMyPK(), "FrmDtlDB");
					DataSet dtlDS = bp.tools.Json.ToDataSet(dtlStr);
					dtDtl = dtlDS.GetTableByName(dtl.getNo());
				}
			}else{
				dtDtl = GetDtlInfo(dtl, en, dtlRefPKVal);
			}
		}

		//从表集合为空时填充从表的情况
		if (dtDtl.Rows.size() == 0)
		{
			GEDtl endtl = null;
			//1.行初始化字段，设置了改字段值时默认就添加枚举值集合的行数据，一般不再新增从表数据
			if (DataType.IsNullOrEmpty(dtl.getInitDBAttrs()) == false)
			{
				MapAttrs mapAttrs = dtl.getMapAttrs();
				String[] keys = dtl.getInitDBAttrs().split("[,]", -1);

				MapAttr attr = null;
				for (String keyOfEn : keys)
				{
					Entity ent = mapAttrs.GetEntityByKey(dtl.getNo() + "_" + keyOfEn);
					if (ent == null)
					{
						continue;
					}
					attr = ent instanceof MapAttr ? (MapAttr)ent : null;
					if (DataType.IsNullOrEmpty(attr.getUIBindKey()) == true)
					{
						continue;
					}
					DataTable dt = null;
					//枚举字段
					if (attr.getLGType() == FieldTypeS.Enum && attr.getMyDataType() == DataType.AppInt)
					{
						dt = myds.GetTableByName(attr.getUIBindKey());
					}
					//外键、外部数据源
					if ((attr.getLGType() == FieldTypeS.FK && attr.getMyDataType() == DataType.AppString) || (attr.getLGType() == FieldTypeS.Normal && attr.getMyDataType() == DataType.AppString && attr.getUIContralType() == UIContralType.DDL))
					{
						dt = myds.GetTableByName(attr.getUIBindKey());
					}
					if (dt == null)
					{
						continue;
					}
					for (DataRow dr : dt.Rows)
					{
						endtl = new GEDtl(dtl.getNo());
						endtl.ResetDefaultVal(null, null, 0);
						endtl.SetValByKey(keyOfEn, dr.getValue(0));
						if(mapAttrs.contains(dtl.getNo() + "_" + keyOfEn+"T"))
							endtl.SetValByKey(keyOfEn+"T",dr.getValue(1));
						endtl.setRefPK(dtlRefPKVal);
						endtl.setFID(fid);
						endtl.Insert();
					}

				}
			}
			//2.从表装载填充
			Object tempVar3 = mes.GetEntityByKey("ExtModel", MapExtXmlList.PageLoadFullDtl);
			me = tempVar3 instanceof MapExt ? (MapExt)tempVar3 : null;
			if (me != null && me.getDoWay().equals("None")==false && DataType.IsNullOrEmpty(me.getDoc()) == false)
			{
				String paras = "";
				bp.en.Row row = en.getRow();
				for (String key : row.keySet())
				{
					//值为空或者null不替换
					if (row.get(key) == null || row.get(key).equals("") == true || key.equals("OID"))
						continue;
					paras += "@"+key+"=" + row.GetValStrByKey(key);
				}
				me.SetValByKey("Tag5", me.getDoWay());
				me.SetValByKey("Tag1", dtl.getNo());
				me.GetFullDataDtl(paras, en.GetValStringByKey("OID"));
			}

		   dtDtl = GetDtlInfo(dtl, en, dtlRefPKVal);
		}



		// 为明细表设置默认值.
		MapAttrs mattrs = new MapAttrs(dtl.getNo());
		for (MapAttr attr : mattrs.ToJavaList())
		{
			if (attr.getUIContralType() == UIContralType.TB)
			{
				continue;
			}

			//处理它的默认值.
			if (attr.getDefValReal().contains("@") == false)
			{
				continue;
			}

			for (DataRow dr : dtDtl.Rows)
			{
				if (dr.getValue(attr.getKeyOfEn()) == null || DataType.IsNullOrEmpty(dr.getValue(attr.getKeyOfEn()).toString()) == true)
				{
					dr.setValue(attr.getKeyOfEn(), attr.getDefVal());
				}
			}
		}

		dtDtl.TableName = "DBDtl"; //修改明细表的名称.
		myds.Tables.add(dtDtl); //加入这个明细表, 如果没有数据，xml体现为空.

			///#endregion 把从表的数据放入.

		//放入一个空白的实体，用与获取默认值.
		GEDtl dtlBlank = new GEDtl(dtl.getNo());
		dtlBlank.ResetDefaultVal(null, null, 0);
		myds.Tables.add(dtlBlank.ToDataTableField("Blank"));

		return myds;
	}

	public static DataTable GetDtlInfo(MapDtl dtl, Entity en, String dtlRefPKVal) throws Exception {
		return GetDtlInfo(dtl, en, dtlRefPKVal, false);
	}

	public static DataTable GetDtlInfo(MapDtl dtl, Entity en, String dtlRefPKVal, boolean isReload) throws Exception {
		QueryObject qo = null;
		GEDtls dtls = new GEDtls(dtl.getNo());
		try
		{
			qo = new QueryObject(dtls);
			switch (dtl.getDtlOpenType())
			{
				case ForEmp: // 按人员来控制.
					qo.AddWhere(GEDtlAttr.RefPK, dtlRefPKVal);
					qo.addAnd();
					qo.AddWhere(GEDtlAttr.Rec, WebUser.getNo());
					break;
				case ForWorkID: // 按工作ID来控制
				case ForPWorkID:
					qo.AddWhere(GEDtlAttr.RefPK, dtlRefPKVal);

					break;
				case ForFID: // 按工作ID来控制
					qo.AddWhere(GEDtlAttr.FID, dtlRefPKVal);
					break;
				case ForWorkIDAndSpecEmpNo: // 按工作ID来控制
					qo.AddWhere(GEDtlAttr.RefPK, dtlRefPKVal);
					qo.addAnd();
					String attr = dtl.GetParaString("DtlOpenPara");
					if (DataType.IsNullOrEmpty(attr) == true)
						throw new Exception("err@当前数据显示规则按照ForWorkIDAndSpecEmpNo计算，但是您没有设置人员账号字段名称,");
					qo.AddWhere(attr, WebUser.getNo());
					break;
				default:
					qo.AddWhere(GEDtlAttr.RefPK, dtlRefPKVal);
					break;
			}

			//条件过滤.
			String exp = dtl.getFilterSQLExp();
			if (DataType.IsNullOrEmpty(exp) == false)
			{
				exp = bp.wf.Glo.DealExp(exp, en);
				exp = exp.replace("''", "'");

				if (exp.substring(0, 5).toLowerCase().contains("and") == false)
				{
					exp = " AND " + exp;
				}
				qo.setSQL(exp);
			}

			//排序.
			if (DataType.IsNullOrEmpty(dtl.getOrderBySQLExp()) == false)
			{
				qo.addOrderBy(dtl.getOrderBySQLExp());
			}
			else
			{
				//增加排序.
				qo.addOrderBy("Idx");
			}

			qo.DoQuery();
			//放入一个空白的实体，用与获取默认值.
			GEDtl dtlBlank = new GEDtl(dtl.getNo());
			dtlBlank.ResetDefaultVal();
			if ( dtl.getInitDBAttrs() !="" && dtl.getRowsOfList() != 0 && dtl.getItIsInsert() == true)
			{
				for (int i = 0; i < dtl.getRowsOfList(); i++)
				{
					GEDtl geDtl = new GEDtl(dtl.getNo());
					geDtl.Copy(dtlBlank);
					dtls.AddEntity(geDtl);
				}
			}

			return dtls.ToDataTableField("dt");
		}
		catch (Exception ex)
		{
			dtl.IntMapAttrs();
			dtls.getNewEntity().CheckPhysicsTable();
			CacheFrmTemplate.Remove(dtl.getNo());
			Cache.SetMap(dtl.getNo(), null);
			Cache.getSQL_Cache().remove(dtl.getNo());
			if (isReload == false)
			{
				return GetDtlInfo(dtl, en, dtlRefPKVal, true);
			}
			else
			{
				throw new RuntimeException("获取从表[" + dtl.getName() +"]失败,错误:" + ex.getMessage());
			}
		}

	}
	public static int GetDtlCount(MapDtl dtl, Entity en, String dtlRefPKVal, boolean isReload) throws Exception {
		QueryObject qo = null;
		GEDtls dtls = new GEDtls(dtl.getNo());
		try
		{
			qo = new QueryObject(dtls);
			switch (dtl.getDtlOpenType())
			{
				case ForEmp: // 按人员来控制.
					qo.AddWhere(GEDtlAttr.RefPK, dtlRefPKVal);
					qo.addAnd();
					qo.AddWhere(GEDtlAttr.Rec, WebUser.getNo());
					break;
				case ForWorkID: // 按工作ID来控制
				case ForPWorkID:
					qo.AddWhere(GEDtlAttr.RefPK, dtlRefPKVal);

					break;
				case ForFID: // 按工作ID来控制
					qo.AddWhere(GEDtlAttr.FID, dtlRefPKVal);
					break;
				case ForWorkIDAndSpecEmpNo: // 按工作ID来控制
					qo.AddWhere(GEDtlAttr.RefPK, dtlRefPKVal);
					qo.addAnd();
					String attr = dtl.GetParaString("DtlOpenPara");
					if (DataType.IsNullOrEmpty(attr) == true)
						throw new Exception("err@当前数据显示规则按照ForWorkIDAndSpecEmpNo计算，但是您没有设置人员账号字段名称,");
					qo.AddWhere(attr, WebUser.getNo());
					break;
				default:
					qo.AddWhere(GEDtlAttr.RefPK, dtlRefPKVal);
					break;
			}

			//条件过滤.
			String exp = dtl.getFilterSQLExp();
			if (DataType.IsNullOrEmpty(exp) == false)
			{
				exp = bp.wf.Glo.DealExp(exp, en);
				exp = exp.replace("''", "'");

				if (exp.substring(0, 5).toLowerCase().contains("and") == false)
				{
					exp = " AND " + exp;
				}
				qo.setSQL(exp);
			}


			return qo.GetCount();

		}
		catch (Exception ex)
		{
			dtl.IntMapAttrs();
			dtls.getNewEntity().CheckPhysicsTable();
			CacheFrmTemplate.Remove(dtl.getNo());
			Cache.SetMap(dtl.getNo(), null);
			Cache.getSQL_Cache().remove(dtl.getNo());
			if (isReload == false)
			{
				return GetDtlCount(dtl, en, dtlRefPKVal, true);
			}
			else
			{
				throw new RuntimeException("获取从表[" + dtl.getName() +"]失败,错误:" + ex.getMessage());
			}
		}

	}
}
