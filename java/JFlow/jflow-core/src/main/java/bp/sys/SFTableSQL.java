package bp.sys;

import bp.en.*;
import bp.en.Map;

/** 
 用户自定义表
*/
public class SFTableSQL extends EntityNoName
{


		///#region 构造方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		uac.IsInsert = false;
		return uac;
	}
	/** 
	 用户自定义表
	*/
	public SFTableSQL()
	{
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
		Map map = new Map("Sys_SFTable", "字典表SQL");


		map.AddTBStringPK(SFTableAttr.No, null, "表英文名称", true, false, 1, 200, 20);
		map.AddTBString(SFTableAttr.Name, null, "表中文名称", true, false, 0, 200, 20);
		map.AddDDLSysEnum(SFTableAttr.CodeStruct, 0, "字典表类型", true, false, SFTableAttr.CodeStruct, "@0=编号名称类型@1=树结构类型");
		map.AddDDLStringEnum(SFTableAttr.DictSrcType, "SQL", "数据表类型", SFTableAttr.DictSrcType, false);

		map.AddTBString(SFTableAttr.FK_Val, null, "默认创建的字段名", true, false, 0, 200, 20);
		map.AddTBString(SFTableAttr.TableDesc, null, "表描述", true, false, 0, 200, 20);
		map.AddTBString(SFTableAttr.DefVal, null, "默认值", true, false, 0, 200, 20);

		//数据源.
		map.AddDDLEntities(SFTableAttr.FK_SFDBSrc, "local", "数据源", new bp.sys.SFDBSrcs(), true);

		//map.AddTBString(SFTableAttr.ColumnValue, null, "显示的值(编号列)", true, false, 0, 200, 20);
		//map.AddTBString(SFTableAttr.ColumnText, null, "显示的文字(名称列)", true, false, 0, 200, 20);
		map.AddTBString(SFTableAttr.ParentValue, null, "Root节点的值(对树结构有效)", true, false, 0, 200, 20);

		map.AddTBStringDoc(SFTableAttr.SelectStatement, null, "查询语句", true, false);
		map.AddTBDateTime(SFTableAttr.RDT, null, "加入日期", false, false);

		RefMethod rm = new RefMethod();
		rm.Title = "查看数据";
		rm.ClassMethodName = this.toString() + ".DoEdit";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ItIsForEns = false;
		map.AddRefMethod(rm);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/** 
	 编辑数据
	 
	 @return 
	*/
	public final String DoEdit() {
		return "../../Admin/FoolFormDesigner/SFTableEditData.htm?FK_SFTable=" + this.getNo() + "&&QueryType=Sql";
	}
	/** 
	 删除之前要做的工作
	 
	 @return 
	*/
	@Override
	protected boolean beforeDelete() throws Exception
	{
		MapAttrs mattrs = new MapAttrs();
		mattrs.Retrieve(MapAttrAttr.UIBindKey, this.getNo());
		if (mattrs.size()!= 0)
		{
			String err = "";
			for (MapAttr item : mattrs.ToJavaList())
			{
				err += " @ " + item.getMyPK() + " " + item.getName();
			}
			throw new RuntimeException("@如下实体字段在引用:" + err + "。您不能删除该表。");
		}
		return super.beforeDelete();
	}
}
