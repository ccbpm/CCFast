package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;


/** 
 列选择
*/
public class CField extends EntityMyPK
{

		///#region 基本属性
	/** 
	 列选择
	*/
	public final String getAttrs()  {
		return this.GetValStringByKey(CFieldAttr.Attrs);
	}
	public final void setAttrs(String value){
		this.SetValByKey(CFieldAttr.Attrs, value);
	}
	/** 
	 操作员ID
	*/
	public final String getEmpNo()  {
		return this.GetValStringByKey(CFieldAttr.EmpNo);
	}
	public final void setEmpNo(String value){
		this.SetValByKey(CFieldAttr.EmpNo, value);
	}
	/** 
	 属性
	*/
	public final String getEnsName()  {
		return this.GetValStringByKey(CFieldAttr.EnsName);
	}
	public final void setEnsName(String value){
		this.SetValByKey(CFieldAttr.EnsName, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 列选择
	*/
	public CField()
	{
	}
	/** 
	 map
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Sys_UserRegedit", "列选择");

		map.AddMyPK();
		map.AddTBString(CFieldAttr.EnsName, null, "实体类名称", false, true, 0, 100, 10);
		map.AddTBString(CFieldAttr.EmpNo, null, "工作人员", false, true, 0, 100, 10);
		map.AddTBStringDoc(CFieldAttr.Attrs, null, "属性s", true, false);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			this.setMyPK(this.getEnsName() + "_" + this.getEmpNo());
		}
		return super.beforeUpdateInsertAction();
	}

	public static Attrs GetMyAttrs(Entities ens, Map map)  {
		String vals = bp.difference.SystemConfig.GetConfigXmlEns("ListAttrs", ens.toString());
		if (vals == null)
		{
			return map.getAttrs();
		}
		Attrs attrs = new Attrs();
		for (Attr attr : map.getAttrs())
		{
			if (vals.contains("," + attr.getKey() + ","))
			{
				attrs.Add(attr);
			}
		}
		return attrs;
	}
}
