package bp.ccbill;

import bp.en.EntityNoName;
import bp.en.Map;
import bp.en.UAC;
import bp.sys.MapDataAttr;
import bp.sys.SFDBSrcs;

/** 
 数据源实体
*/
public class DBListDBSrc extends EntityNoName
{

		///#region 权限控制.
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForAppAdmin();
		uac.IsDelete = false;
		uac.IsInsert = false;
		return uac;
	}
		///#endregion 权限控制.
		///#region 属性
		///#endregion
		///#region 构造方法
	/** 
	 数据源实体
	*/
	public DBListDBSrc()
	{
	}
	/** 
	 数据源实体
	 
	 @param no 映射编号
	*/
	public DBListDBSrc(String no) throws Exception
	{
		super(no);
	}
	public final int getDBType() {
		return this.GetValIntByKey(MapDataAttr.DBType);
	}
	/**
	 * EnMap
	 */
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_MapData", "数据源实体");

		map.setCodeStruct( "4");

			///#region 基本属性.
		map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, true, 1, 190, 20);
		map.AddTBString(MapDataAttr.Name, null, "名称", true, true, 1, 190, 20);

			///#endregion 基本属性.
			///#region 数据源.
		map.AddDDLSysEnum(MapDataAttr.DBType, 0, "数据源类型", true, true, "DBListDBType", "@0=数据库查询SQL@1=执行Url返回Json@2=执行存储过程");
		map.AddDDLEntities(MapDataAttr.DBSrc, null, "数据源", new SFDBSrcs(), true);
		map.SetHelperAlert(MapDataAttr.DBSrc, "您可以在系统管理中新建SQL数据源.");
			///#endregion 数据源.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		DBList db = new DBList(this.getNo());
		if (db.getDBType() != this.getDBType())
		{
			db.setExpEn("");
			db.setExpList("");
			db.setExpCount("");
			db.Update();
		}
		return super.beforeUpdate();
	}
}
