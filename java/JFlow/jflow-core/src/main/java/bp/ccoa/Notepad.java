package bp.ccoa;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;

/** 
 记事本
*/
public class Notepad extends EntityMyPK
{

		///#region 基本属性
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(NotepadAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(NotepadAttr.OrgNo, value);
	}
	/** 
	 记录人
	*/
	public final String getRec()  {
		return this.GetValStrByKey(NotepadAttr.Rec);
	}
	public final void setRec(String value)  {
		this.SetValByKey(NotepadAttr.Rec, value);
	}
	/** 
	 记录日期
	*/
	public final String getRDT()  {
		return this.GetValStrByKey(NotepadAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(NotepadAttr.RDT, value);
	}
	/** 
	 年月
	*/
	public final String getNianYue()  {
		return this.GetValStrByKey(NotepadAttr.NianYue);
	}
	public final void setNianYue(String value)  {
		this.SetValByKey(NotepadAttr.NianYue, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 权限控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (WebUser.getIsAdmin())
		{
			uac.IsUpdate = true;
			return uac;
		}
		return super.getHisUAC();
	}
	/** 
	 记事本
	*/
	public Notepad()
	{
	}
	public Notepad(String mypk) throws Exception
	{
		this.setMyPK(mypk);
		this.Retrieve();
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("OA_Notepad", "记事本");

		map.AddMyPK(true);
		map.AddTBString(NotepadAttr.Name, null, "标题", true, false, 0, 300, 10, true);

		map.AddTBStringDoc(NotepadAttr.Docs, null, "内容", true, false);

		map.AddTBString(NotepadAttr.OrgNo, null, "OrgNo", false, false, 0, 100, 10);
		map.AddTBString(NotepadAttr.Rec, null, "记录人", false, false, 0, 100, 10, true);
		map.AddTBDateTime(NotepadAttr.RDT, null, "记录时间", false, false);
		map.AddTBString(NotepadAttr.NianYue, null, "NianYue", false, false, 0, 10, 10);

		map.AddTBInt(NotepadAttr.IsStar, 0, "是否标星", false, false);



		//RefMethod rm = new RefMethod();
		//rm.Title = "方法参数"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoParas";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		////rm.GroupName = "开发接口";
		////  map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "方法内容"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoDocs";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		////rm.GroupName = "开发接口";
		//map.AddRefMethod(rm);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 执行方法.
	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(DBAccess.GenerGUID(0, null, null));
		this.setRec(WebUser.getNo());
		this.setRDT(DataType.getCurrentDateTime());

		this.setNianYue(DataType.getCurrentYearMonth());


		return super.beforeInsert();
	}

		///#endregion 执行方法.
}
