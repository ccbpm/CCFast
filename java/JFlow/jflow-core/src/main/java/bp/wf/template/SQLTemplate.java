package bp.wf.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;

/** 
 SQL模板
*/
public class SQLTemplate extends EntityNoName
{

		///#region  属性
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}
	/** 
	 打开的连接
	*/
	public final String getDocs() throws Exception {
		String s = this.GetValStrByKey(SQLTemplateAttr.Docs);
		if (DataType.IsNullOrEmpty(s) == true)
		{
			return this.getNo();
		}
		return s;
	}
	public final void setDocs(String value){
		this.SetValByKey(SQLTemplateAttr.Docs, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 SQL模板
	*/
	public SQLTemplate()
	{
	}
	public SQLTemplate(String no) throws Exception
	{
		super(no.replace("\n","").trim());
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
		Map map = new Map("WF_SQLTemplate", "SQL模板");
		map.setCodeStruct( "3");

		map.AddTBStringPK(SQLTemplateAttr.No, null, "编号", true, true, 3, 3, 3);
		map.AddDDLSysEnum(SQLTemplateAttr.SQLType, 0, "模版SQL类型", true, true, SQLTemplateAttr.SQLType, "@0=方向条件@1=接受人规则@2=下拉框数据过滤@3=级联下拉框@4=PopVal开窗返回值@5=人员选择器人员选择范围");

		map.AddDDLEntities("DBSrc", "local", "数据源", new SFDBSrcs(), true);

		map.AddTBString(SQLTemplateAttr.Name, null, "SQL说明", true, false, 0, 200, 20,true);
		map.AddTBStringDoc(SQLTemplateAttr.Docs, null, "SQL模版", true, false, true, 10);

		//查询条件.
		map.AddSearchAttr(SQLTemplateAttr.SQLType, 130);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
