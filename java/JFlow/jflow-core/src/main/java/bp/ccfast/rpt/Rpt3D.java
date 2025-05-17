package bp.ccfast.rpt;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import bp.ccfast.ccmenu.*;

/** 
 三维报表
*/
public class Rpt3D extends EntityNoName
{

		///#region 属性
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getIsAdmin() == true)
		{
			uac.IsDelete = true;
			uac.IsUpdate = true;
			uac.IsInsert = false;
			return uac;
		}
		else
		{
			uac.Readonly();
		}
		return uac;
	}

		///#endregion


		///#region 构造方法
	/** 
	 三维报表
	*/
	public Rpt3D()
	{
	}
	@Override
	protected boolean beforeDelete() throws Exception
	{
		return super.beforeDelete();
	}
	/** 
	 EnMap
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("GPM_Menu", "三维报表"); // 类的基本属性.
		map.setEnType(EnType.Sys);

		map.AddTBStringPK(MenuAttr.No, null, "编号", false, false, 1, 90, 50);
		map.AddTBString(MenuAttr.Icon, null, "Icon", true, false, 0, 50, 50);
		map.AddTBString(MenuAttr.Name, null, "菜单名称", true, false, 0, 200, 200);

		map.AddTBString(MenuAttr.Title, null, "报表标题", true, false, 0, 200, 200,true);
		map.AddTBString(MenuAttr.Tag4, null, "分析项目名称", true, false, 0, 200, 200);

		map.AddDDLSysEnum(MenuAttr.ListModel, 0, "维度显示格式", true, true, "RptModel", "@0=左边@1=顶部");

		map.AddDDLSysEnum(MenuAttr.TagInt1, 0, "合计位置?", true, true, "Rpt3SumModel", "@0=不显示@1=底部@2=头部");

		map.AddTBStringDoc(MenuAttr.Tag0, null, "数据源SQL", true, false, true, 10);
		String msg = "编写说明";
		msg += "\t\n 1. 该数据源一般是一个分组统计语句, 比如： SELECT D1,D2,D3,SUM(XX) AS Num FROM MyTable WHERE 1=2 GROUP BY D1,D2,D3  ";
		msg += "\t\n 2. 对应的数据列分别是 如下数据源的列数据，列的顺序不要改变。 ";
		msg += "\t\n 3. 每个维度都是返回的No,Name两个列的数据。 ";
		msg += "\t\n 3，DEMO ";
		msg += "\t\n 数据源：SELECT FK_BanJi,XB,ZZMM, COUNT(*) as Num from Demo_Student GROUP BY FK_BanJi,XB,ZZMM";
		msg += "\t\n 维度1：SELECT No,Name FROM demo_banji ";
		msg += "\t\n 维度2：SELECT IntKey as No, Lab as Name FROM sys_enum WHERE EnumKey='XB' ";
		msg += "\t\n 维度3：SELECT IntKey as No, Lab as Name FROM sys_enum WHERE EnumKey='ZZMM'";

		map.SetHelperAlert(MenuAttr.Tag0, msg);

		map.AddTBStringDoc(MenuAttr.Tag1, null, "维度1SQL", true, false, true, 10);
		map.AddTBStringDoc(MenuAttr.Tag2, null, "维度2SQL", true, false, true, 10);
		map.AddTBStringDoc(MenuAttr.Tag3, null, "维度3SQL", true, false, true, 10);

		//从表明细.
		map.AddDtl(new SearchAttrs(), SearchAttrAttr.RefMenuNo);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
	/** 
	 初始化数据.
	 
	 @return 
	*/
	public final String Rpt3D_Init() throws Exception {
		DataSet ds = new DataSet();

		//维度1
		String tag0 = this.GetValStrByKey(MenuAttr.Tag0);
		String tag1 = this.GetValStrByKey(MenuAttr.Tag1);
		String tag2 = this.GetValStrByKey(MenuAttr.Tag2);
		String tag3 = this.GetValStrByKey(MenuAttr.Tag3);
		if (DataType.IsNullOrEmpty(tag0) == true || DataType.IsNullOrEmpty(tag1) == true || DataType.IsNullOrEmpty(tag2) == true || DataType.IsNullOrEmpty(tag3) == true)
		{
			return "err@请检查" + this.getName() + "的数据源,维度1,维度2,维度3的信息是否设置完整";
		}

		DataTable src = DBAccess.RunSQLReturnTable(bp.wf.Glo.DealExp(tag0, null, null));
		src.setTableName("Src");
		ds.Tables.add(src);

		//维度1
		DataTable dt1 = DBAccess.RunSQLReturnTable(bp.wf.Glo.DealExp(tag1, null, null));
		dt1.setTableName("D1");
		ds.Tables.add(dt1);

		DataTable dt2 = DBAccess.RunSQLReturnTable(bp.wf.Glo.DealExp(tag2, null, null));
		dt2.setTableName("D2");
		ds.Tables.add(dt2);

		DataTable D3 = DBAccess.RunSQLReturnTable(bp.wf.Glo.DealExp(tag3, null, null));
		D3.setTableName("D3");
		ds.Tables.add(D3);

		return bp.tools.Json.ToJson(ds);
	}

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		return super.beforeUpdateInsertAction();
	}
}
