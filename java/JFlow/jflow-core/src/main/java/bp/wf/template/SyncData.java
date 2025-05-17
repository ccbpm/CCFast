package bp.wf.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
/** 
 流程数据同步
*/
public class SyncData extends EntityMyPK
{

		///#region 属性
	/** 
	 流程编号
	*/
	public final String getFlowNo()  {
		return this.GetValStringByKey(SyncDataAttr.FlowNo);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(SyncDataAttr.FlowNo, value);
	}

	/**
	 表主键
	*/
	public final String getTablePKName()  {
		return this.GetValStringByKey(SyncDataAttr.TablePKName);
	}
	public final void setTablePKName(String value){
		this.SetValByKey(SyncDataAttr.TablePKName, value);
	}

	/**
	 * 主键类型
	 * @return
	 */
	public final String getTablePKType()  {
		return this.GetValStringByKey(SyncDataAttr.TablePKType);
	}
	public final void setTablePKType(String value){
		this.SetValByKey(SyncDataAttr.TablePKType, value);
	}
	/** 
	 表
	*/
	public final String getPTable()  {
		return this.GetValStringByKey(SyncDataAttr.PTable);
	}
	public final void setPTable(String value){
		this.SetValByKey(SyncDataAttr.PTable, value);
	}
	/** 
	 备注
	*/
	public final String getNote()  {
		return this.GetValStringByKey(SyncDataAttr.Note);
	}
	public final void setNote(String value){
		this.SetValByKey(SyncDataAttr.Note, value);
	}
	/** 
	 URL 
	*/
	public final String getAPIUrl()  {
		return this.GetValStringByKey(SyncDataAttr.APIUrl);
	}
	public final void setAPIUrl(String value){
		this.SetValByKey(SyncDataAttr.APIUrl, value);
	}
	/** 
	 数据源
	*/
	public final String getDBSrc()  {
		return this.GetValStringByKey(SyncDataAttr.DBSrc);
	}
	public final void setDBSrc(String value){
		this.SetValByKey(SyncDataAttr.DBSrc, value);
	}
	/** 
	 类型
	*/
	public final String getSyncType()  {
		return this.GetValStringByKey(SyncDataAttr.SyncType);
	}
	public final void setSyncType(String value){
		this.SetValByKey(SyncDataAttr.SyncType, value);
	}

	public final String getFrmID()  {
		return this.GetValStringByKey(SyncDataAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(SyncDataAttr.FrmID, value);
	}


		///#endregion


		///#region 构造函数
	/** 
	 SyncData
	*/
	public SyncData()
	{
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("WF_SyncData", "流程数据同步");

		map.AddMyPK(true);
		map.AddTBString(SyncDataAttr.FlowNo, null, "流程编号", false, false, 0, 10, 50, true);
		String val = "@DBSrc=按数据源同步@API=按API同步";
		map.AddDDLStringEnum(SyncDataAttr.SyncType, "DBSrc", "同步类型", val, true, "", false);
		map.AddTBStringDoc(SyncDataAttr.Note, null, "备注/说明", true, true, true, 10);

		map.AddTBString(SyncDataAttr.APIUrl, null, "API链接URL", false, false, 0, 300, 50, true);
		map.AddTBString(SyncDataAttr.DBSrc, null, "数据库链接URL", false, false, 0, 300, 50, true);
		map.AddTBString(SyncDataAttr.DBSrcT, null, "数据源", false, false, 0, 200, 50, true);

		map.AddTBString(SyncDataAttr.PTable, null, "数据表", true, true, 0, 100, 50);
		map.AddTBString(SyncDataAttr.PTableName, null, "表名", true, true, 0, 100, 50);

		map.AddTBString(SyncDataAttr.TablePKName, null, "主键", true, true, 0, 300, 50);
		map.AddTBString(SyncDataAttr.TablePKType, null, "主键类型", true, true, 0, 300, 50);

		map.AddTBString(SyncDataAttr.FrmID, null, "源表单ID", true, true, 0, 300, 50);
		map.AddTBString(SyncDataAttr.FrmName, null, "名称", true, true, 0, 300, 50);

		//map.AddTBString(SyncDataAttr.SQLTables, null, "查询表的集合SQL", false, false, 0, 100, 50);
		//map.AddTBString(SyncDataAttr.SQLFields, null, "查询表字段的SQL", false, false, 0, 100, 50);

		map.AddTBAtParas(4000);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 公共方法.
	/** 
	 同步数据库字段.
	 
	 @return 
	*/
	public final String Init_DtlFields() throws Exception {
		if (this.getSyncType().equals("DBSrc") == false)
		{
			return "当前数据源不是DBSrc类型,.";
		}

		if (DataType.IsNullOrEmpty(this.getPTable()) == false)
		{
			return "err@请输入table名称.";
		}

		SyncDataField fs = new SyncDataField();
		return "";
	}

		///#endregion 公共方法.

}
