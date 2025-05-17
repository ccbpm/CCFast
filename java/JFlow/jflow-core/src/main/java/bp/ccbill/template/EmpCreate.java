package bp.ccbill.template;

import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 单据可创建的人员
 表单ID的到人员有两部分组成.	 
 记录了从一个表单ID到其他的多个表单ID.
 也记录了到这个表单ID的其他的表单ID.
*/
public class EmpCreate extends EntityMM
{

		///#region 基本属性
	/** 
	表单ID
	*/
	public final int getFrmID() {
		return this.GetValIntByKey(EmpCreateAttr.FrmID);
	}
	public final void setFrmID(int value)  {
		this.SetValByKey(EmpCreateAttr.FrmID, value);
	}
	/** 
	 到人员
	*/
	public final String getEmpNo() {
		return this.GetValStringByKey(EmpCreateAttr.FK_Emp);
	}
	public final void setEmpNo(String value)  {
		this.SetValByKey(EmpCreateAttr.FK_Emp, value);
	}
	public final String getEmpNoT() {
		return this.GetValRefTextByKey(EmpCreateAttr.FK_Emp);
	}

		///#endregion


		///#region 构造方法
	/** 
	 单据可创建的人员
	*/
	public EmpCreate()
	{
	}
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Frm_EmpCreate", "单据可创建的人员");

		map.AddTBStringPK(EmpCreateAttr.FrmID,null,"表单",true,true,1,100,100);
		map.AddDDLEntitiesPK(EmpCreateAttr.FK_Emp, null, "人员", new bp.port.Emps(), true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
