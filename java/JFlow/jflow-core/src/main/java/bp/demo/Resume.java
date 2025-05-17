package bp.demo;

import bp.en.EntityOID;
import bp.en.Map;
import bp.en.UAC;
import bp.web.WebUser;

/**
 * 简历
 */
public class Resume extends EntityOID
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 12525225252L;
	
	/**
	 * 年月
	 */
	public final String getNianYue() {
		return this.GetValStringByKey(ResumeAttr.NianYue);
	}
	
	public final void setNianYue(String value)  {
		this.SetValByKey(ResumeAttr.NianYue, value);
	}
	
	/**
	 * 人员
	 */
	public final String getRefPK() {
		return this.GetValStringByKey(ResumeAttr.RefPK);
	}
	
	public final void setRefPK(String value)  {
		this.SetValByKey(ResumeAttr.RefPK, value);
	}
	
	/**
	 * 工作单位
	 */
	public final String getGongZuoDanWei() {
		return this.GetValStringByKey(ResumeAttr.GongZuoDanWei);
	}
	
	public final void setGongZuoDanWei(String value)  {
		this.SetValByKey(ResumeAttr.GongZuoDanWei, value);
	}
	
	/**
	 * 证明人
	 */
	public final String getZhengMingRen() {
		return this.GetValStringByKey(ResumeAttr.ZhengMingRen);
	}
	
	public final void setZhengMingRen(String value)  {
		this.SetValByKey(ResumeAttr.ZhengMingRen, value);
	}
	
	/**
	 * 备注
	 */
	public final String getBeiZhu() {
		return this.GetValStringByKey(ResumeAttr.BeiZhu);
	}
	
	public final void setBeiZhu(String value)  {
		this.SetValByKey(ResumeAttr.BeiZhu, value);
	}
	/**
	 实体的权限控制
	 */
	@Override
	public UAC getHisUAC()  {
		UAC uac = new UAC();
		if (WebUser.getDeptNo().contains("01,02,03")) {
			//如果是部门编号为01,02,03的人员，都可以编辑
			uac.IsDelete = true;
			uac.IsUpdate = true;
			uac.IsInsert = true;
			uac.IsView = true;
		}else{
			//否则只能只读
			uac.IsView = true;
		}
		return uac;
	}
	/**
	 * 构造函数
	 */
	public Resume()
	{
	}
	
	public Resume(int oid) throws Exception
	{
		super(oid);
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
		Map map = new Map("Demo_Resume");
		map.setEnDesc("简历");
		
		map.AddTBIntPKOID();
		
		map.AddTBString(ResumeAttr.RefPK, null, "人员", false, false, 0, 200, 10);
		map.AddTBString(ResumeAttr.NianYue, null, "年月", true, false, 0, 200, 50);
		map.AddTBString(ResumeAttr.GongZuoDanWei, null, "工作单位", true, false, 0,
				200, 70);
		map.AddTBString(ResumeAttr.ZhengMingRen, "", "证明人", true, false, 0,
				200, 50);
		map.AddTBString(ResumeAttr.BeiZhu, null, "备注", true, false, 0, 200, 150);
		
		map.AddTBString("QT", null, "其他", true, false, 0, 200, 150);
		
		this.set_enMap(map);
		return this.get_enMap();
	}
}