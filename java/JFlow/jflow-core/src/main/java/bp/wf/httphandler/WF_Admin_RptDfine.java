package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.difference.*;
import bp.*;
import bp.wf.*;
import bp.wf.rpt.RptDfine;

import java.io.IOException;
import java.util.*;

/** 
 页面功能实体
*/
public class WF_Admin_RptDfine extends bp.difference.handler.DirectoryPageBase
{


	/** 
	 构造函数
	*/
	public WF_Admin_RptDfine()
	{
	}


		///#region 执行父类的重写方法.
	/** 
	 默认执行的方法
	 
	 @return 
	*/
	@Override
	protected String DoDefaultMethod() throws IOException {
		String msg = "";
		try
		{
			switch (this.getDoType())
			{
				case "S3ColsLabel_Init": //顺序加载
					msg = this.S3ColsLabel_Init();
					break;
				case "S3ColsLabel_Save": //顺序保存
					msg = this.S3ColsLabel_Save();
					break;
				default:
					msg = "err@没有判断的执行类型：" + this.getDoType();
					break;
			}
			ContextHolderUtils.getResponse().getWriter().write(msg);
		}
		catch (Exception ex)
		{
			ContextHolderUtils.getResponse().getWriter().write("err@" + ex.getMessage());
		}

		//找不不到标记就抛出异常.
		throw new RuntimeException("@标记[" + this.getDoType() + "]，没有找到. @RowURL:" + ContextHolderUtils.getRequest().getRequestURI());
	}

		///#endregion 执行父类的重写方法.


		///#region 报表设计器. - 第2步选择列.
	/** 
	 初始化方法
	 
	 @return 
	*/
	public final String S2ColsChose_Init() throws Exception {
		DataSet ds = new DataSet();
		String rptNo = this.GetRequestVal("RptNo");

		//所有的字段.
		String fk_mapdata = "ND" + Integer.parseInt(this.getFlowNo()) + "Rpt";
		MapAttrs mattrs = new MapAttrs(fk_mapdata);
		ds.Tables.add(mattrs.ToDataTableField("Sys_MapAttrOfAll"));

		//判断rptNo是否存在于mapdata中
		MapData md = new MapData();
		md.setNo(rptNo);
		if (md.RetrieveFromDBSources() == 0)
		{
			RptDfine rd = new RptDfine(this.getFlowNo());

			switch (rptNo.substring(fk_mapdata.length()))
			{
				case "My":
					rd.DoReset_MyStartFlow();
					break;
				case "MyDept":
					rd.DoReset_MyDeptFlow();
					break;
				case "MyJoin":
					rd.DoReset_MyJoinFlow();
					break;
				case "Adminer":
					rd.DoReset_AdminerFlow();
					break;
				default:
					throw new RuntimeException("@未涉及的rptMark类型");
			}

			md.RetrieveFromDBSources();
		}

		//选择的字段,就是报表的字段.
		MapAttrs mattrsOfRpt = new MapAttrs(rptNo);
		ds.Tables.add(mattrsOfRpt.ToDataTableField("Sys_MapAttrOfSelected"));

		//系统字段.
		MapAttrs mattrsOfSystem = new MapAttrs();
		String sysFields = bp.wf.Glo.getFlowFields();
		for (MapAttr item : mattrs.ToJavaList())
		{
			if (sysFields.contains(item.getKeyOfEn()))
			{
				mattrsOfSystem.AddEntity(item);
			}
		}
		ds.Tables.add(mattrsOfSystem.ToDataTableField("Sys_MapAttrOfSystem"));

		//返回.
		return bp.tools.Json.ToJson(ds);
	}
	/** 
	 选择列的保存.
	 
	 @return 
	*/
	public final String S2ColsChose_Save() throws Exception {
		//报表列表.
		String rptNo = this.GetRequestVal("RptNo");

		//保存的字段,从外面传递过来的值. 用逗号隔开的: 比如:  ,Name,Tel,Addr,
		String fields = "," + this.GetRequestVal("Fields") + ",";

		//增加上必要的字段.
		if (rptNo.contains("My") == true && fields.contains(",FlowEmps,") == false)
		{
			fields += "FlowEmps,";
		}

		if (rptNo.contains("MyDept") == true && fields.contains(",FK_Dept,") == false)
		{
			fields += "FK_Dept,";
		}

		//字段组合.
		fields += bp.wf.rpt.RptDfine.PublicFiels;

		//构造一个空的集合，如果有数据，就把旧数据删除掉.
		MapAttrs mrattrsOfRpt = new MapAttrs();
		mrattrsOfRpt.Delete(MapAttrAttr.FK_MapData, rptNo);

		//所有的字段.
		String fk_mapdata = "ND" + Integer.parseInt(this.getFlowNo()) + "Rpt";
		MapAttrs allAttrs = new MapAttrs(fk_mapdata);

		for (MapAttr attr : allAttrs.ToJavaList())
		{
			//如果包含了指定的字段，就执行插入操作.
			if (fields.contains("," + attr.getKeyOfEn() + ",") == false)
			{
				continue;
			}

			attr.setFrmID( rptNo);
			attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());


				///#region 判断特殊的字段.
			switch (attr.getKeyOfEn())
			{
				case GERptAttr.WFSta:
					attr.setUIBindKey("WFSta");
					attr.setUIContralType(UIContralType.DDL);
					attr.setLGType(FieldTypeS.Enum);
					attr.setUIVisible(false);
					attr.setDefVal("0");
					attr.setMaxLen(100);
					attr.setUIVisible(true);
					attr.Insert();
					continue;
				case GERptAttr.FK_Dept:
					attr.setUIBindKey("");
					//attr.setUIBindKey("bp.port.Depts";
					attr.setUIContralType(UIContralType.TB);
					attr.setLGType(FieldTypeS.Normal);
					attr.setUIVisible(false);
					attr.setDefVal("");
					attr.setMaxLen(100);
					attr.setUIVisible(false);
					attr.Insert();
					continue;
				case GERptAttr.FK_NY:
					attr.setUIBindKey("BP.Pub.NYs");
					attr.setUIContralType(UIContralType.DDL);
					attr.setLGType(FieldTypeS.FK);
					attr.setUIVisible(true);
					attr.setUIIsEnable(false);
					//attr.setGroupID(groupID;
					attr.Insert();
					continue;
				case GERptAttr.Title:
					attr.setUIWidth(120);
					attr.setUIVisible(true);
					attr.setIdx(0);
					attr.Insert();
					continue;
				case GERptAttr.FlowStarter:
					attr.setUIIsEnable(false);
					attr.setUIVisible(false);
					attr.setUIBindKey("");
					//attr.setUIBindKey("bp.port.Depts";
					attr.setUIContralType(UIContralType.TB);
					attr.setLGType(FieldTypeS.Normal);
					attr.Insert();
					continue;
				case GERptAttr.FlowEmps:
					attr.setUIIsEnable(false);
					attr.setUIVisible(false);
					attr.setUIBindKey("");
					//attr.setUIBindKey("bp.port.Depts";
					attr.setUIContralType(UIContralType.TB);
					attr.setLGType(FieldTypeS.Normal);
					attr.Insert();
					continue;
				case GERptAttr.WFState:
					attr.setUIIsEnable(false);
					attr.setUIVisible(false);
					attr.setUIBindKey("");
					//attr.setUIBindKey("bp.port.Depts";
					attr.setUIContralType(UIContralType.TB);
					attr.setLGType(FieldTypeS.Normal);
					attr.setMyDataType(DataType.AppInt);
					attr.Insert();
					continue;
				case GERptAttr.FlowEndNode:
					//attr.setLGType(FieldTypeS.FK);
					//attr.setUIBindKey("BP.WF.Template.NodeExts";
					//attr.setUIContralType(UIContralType.DDL);
					break;
				case "FK_Emp":
					break;
				default:
					break;
			}

				///#endregion


			attr.setUIVisible(true);

			//如果包含了指定的字段，就执行插入操作.
			if (fields.contains("," + attr.getKeyOfEn() + ",") == true)
			{
				attr.setFrmID( rptNo);
				attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
				attr.DirectInsert();
			}
		}
		MapData mapData = new MapData(rptNo);
		mapData.ClearCache();
		return "保存成功.";
	}

		///#endregion


		///#region 报表设计器. - 第3步设置列的顺序.
	/** 
	 初始化方法
	 
	 @return 
	*/
	public final String S3ColsLabel_Init() throws Exception {
		String rptNo = this.GetRequestVal("RptNo");

		//判断rptNo是否存在于mapdata中
		MapData md = new MapData();
		md.setNo(rptNo);
		if (md.RetrieveFromDBSources() == 0)
		{
			RptDfine rd = new RptDfine(this.getFlowNo());

			switch (rptNo.substring(("ND" + Integer.parseInt(this.getFlowNo()) + "Rpt").length()))
			{
				case "My":
					rd.DoReset_MyStartFlow();
					break;
				case "MyDept":
					rd.DoReset_MyDeptFlow();
					break;
				case "MyJoin":
					rd.DoReset_MyJoinFlow();
					break;
				case "Adminer":
					rd.DoReset_AdminerFlow();
					break;
				default:
					throw new RuntimeException("@未涉及的rptMark类型");
			}

			md.RetrieveFromDBSources();
		}

		//选择的字段,就是报表的字段.
		MapAttrs mattrsOfRpt = new MapAttrs();
		QueryObject qo = new QueryObject(mattrsOfRpt);
		qo.AddWhere(MapAttrAttr.FK_MapData, rptNo);
		qo.addOrderBy(MapAttrAttr.Idx);
		qo.DoQuery();

		mattrsOfRpt.RemoveEn(rptNo + "_OID");
		mattrsOfRpt.RemoveEn(rptNo + "_Title");

		return mattrsOfRpt.ToJson("dt");
	}
	/** 
	 保存列的顺序名称.
	 
	 @return 
	*/
	public final String S3ColsLabel_Save() throws Exception {
		String orders = this.GetRequestVal("Orders");
		//格式为  @KeyOfEn,Lable,idx  比如： @DianHua,电话,1@Addr,地址,2

		String rptNo = this.GetRequestVal("RptNo");

		DBAccess.RunSQL("UPDATE Sys_MapAttr SET Idx=10000 WHERE FK_MapData='" + rptNo + "'");

		String[] strs = orders.split("[@]", -1);
		//重新对table排序
		for (int i = 0; i < strs.length; i++)
		{
			String item = strs[i];

			if (DataType.IsNullOrEmpty(item) == true)
			{
				continue;
			}

			String[] vals = item.split("[,]", -1);

			String mypk = rptNo + "_" + vals[0];

			// 更新顺序.
			DBAccess.RunSQL("UPDATE Sys_MapAttr SET Idx=" + i + " WHERE No='" + mypk + "'");
		}

		//foreach (String item in strs)
		//{
		//    if (DataType.IsNullOrEmpty(item) == true)
		//        continue;

		//    string[] vals = item.Split(',');

		//    String mypk = rptNo + "_" + vals[0];

		//    MapAttr attr = new MapAttr();
		//    attr.setMyPK(mypk);
		//    attr.Retrieve();

		//    attr.setName(vals[1];
		//    attr.setIdx(int.Parse(vals[2]);

		//    attr.Update(); //执行更新.
		//}

		MapAttr myattr = new MapAttr();
		myattr.setMyPK(rptNo + "_OID");
		myattr.RetrieveFromDBSources();
		myattr.setIdx(200);
		myattr.setName("工作ID");
		myattr.Update();

		myattr = new MapAttr();
		myattr.setMyPK(rptNo + "_Title");
		myattr.RetrieveFromDBSources();
		myattr.setIdx(-1);
		myattr.setName("标题");
		myattr.Update();

		return "保存成功..";
	}

		///#endregion


		///#region 报表设计器 - 第4步骤.
	public final String S5SearchCond_Init() throws Exception {
		//报表编号.
		String rptNo = this.GetRequestVal("RptNo");

		//定义容器.
		DataSet ds = new DataSet();

		//判断rptNo是否存在于mapdata中
		MapData md = new MapData();
		md.setNo(rptNo);
		if (md.RetrieveFromDBSources() == 0)
		{
			RptDfine rd = new RptDfine(this.getFlowNo());

			switch (rptNo.substring(("ND" + Integer.parseInt(this.getFlowNo()) + "Rpt").length()))
			{
				case "My":
					rd.DoReset_MyStartFlow();
					break;
				case "MyDept":
					rd.DoReset_MyDeptFlow();
					break;
				case "MyJoin":
					rd.DoReset_MyJoinFlow();
					break;
				case "Adminer":
					rd.DoReset_AdminerFlow();
					break;
				default:
					throw new RuntimeException("@未涉及的rptMark类型");
			}

			md.RetrieveFromDBSources();
		}

		ds.Tables.add(md.ToDataTableField("Main"));

		//查询出来枚举与外键类型的字段集合.
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, rptNo, "Idx");
		ds.Tables.add(attrs.ToDataTableField("Sys_MapAttr"));


			///#region 检查是否有日期字段.
		boolean isHave = false;
		for (MapAttr mattr : attrs.ToJavaList())
		{
			if (mattr.getUIVisible() == false)
			{
				continue;
			}

			if (mattr.getMyDataType() == DataType.AppDate || mattr.getMyDataType() == DataType.AppDateTime)
			{
				isHave = true;
				break;
			}
		}

		if (isHave == true)
		{
			DataTable dt = new DataTable();
			MapAttrs dtAttrs = new MapAttrs();
			for (MapAttr mattr : attrs.ToJavaList())
			{
				if (mattr.getMyDataType() == DataType.AppDate || mattr.getMyDataType() == DataType.AppDateTime)
				{
					if (mattr.getUIVisible() == false)
					{
						continue;
					}
					dtAttrs.AddEntity(mattr);
				}
			}
			ds.Tables.add(dtAttrs.ToDataTableField("Sys_MapAttrOfDate"));
		}

			///#endregion

		//返回数据.
		return bp.tools.Json.ToJson(ds);
	}
	public final String getRptNo()
	{
		return this.GetRequestVal("RptNo");
	}
	/** 
	 查询条件保存.
	 
	 @return 
	*/
	public final String S5SearchCond_Save() throws Exception {
		MapData md = new MapData();
		md.setNo(this.getRptNo());
		md.RetrieveFromDBSources();

		//报表编号.
		String fields = this.GetRequestVal("Fields");
		md.setRptSearchKeys(fields + "*");

		String IsSearchKey = this.GetRequestVal("IsSearchKey");
		if (Objects.equals(IsSearchKey, "0"))
		{
			md.setItIsSearchKey(false);
		}
		else
		{
			md.setItIsSearchKey(true);
		}
		md.SetPara("StringSearchKeys", this.GetRequestVal("StringSearchKeys"));
		//查询方式.
		int DTSearchWay = this.GetRequestValInt("DTSearchWay");
		md.setDTSearchWay(bp.sys.DTSearchWay.forValue(DTSearchWay));

		//日期字段.
		String DTSearchKey = this.GetRequestVal("DTSearchKey");
		md.setDTSearchKey(DTSearchKey);

		//是否查询自己部门发起
		md.SetPara("IsSearchNextLeavel", this.GetRequestValBoolen("IsSearchNextLeavel"));
		md.Save();

		Cache.getMap_Cache().remove(this.getRptNo());
		return "保存成功.";
	}

		///#endregion
}
