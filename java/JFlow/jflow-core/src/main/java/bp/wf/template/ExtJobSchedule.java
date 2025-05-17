package bp.wf.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.sys.frmui.*;


/** 
 流程进度图
*/
public class ExtJobSchedule extends EntityMyPK
{

		///#region 属性
	/** 
	 目标
	*/
	public final String getTarget()  {
		return this.GetValStringByKey(MapAttrAttr.Tag1);
	}
	public final void setTarget(String value){
		this.SetValByKey(MapAttrAttr.Tag1, value);
	}
	/** 
	 URL
	*/
	public final String getURL()  {
		return this.GetValStringByKey(MapAttrAttr.Tag2).replace("#", "@");
	}
	public final void setURL(String value){
		this.SetValByKey(MapAttrAttr.Tag2, value);
	}
	/** 
	 FK_MapData
	*/
	public final String getFrmID()  {
		return this.GetValStrByKey(MapAttrAttr.FK_MapData);
	}
	public final void setFrmID(String value){
		this.SetValByKey(MapAttrAttr.FK_MapData, value);
	}
	/** 
	 Text
	*/
	public final String getName()  {
		return this.GetValStrByKey(MapAttrAttr.Name);
	}
	public final void setName(String value){
		this.SetValByKey(MapAttrAttr.Name, value);
	}

		///#endregion


		///#region 构造方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.Readonly();
		if (bp.web.WebUser.getNo().equals("admin") == true)
		{
			uac.IsUpdate = true;
			uac.IsDelete = true;
		}
		return uac;
	}
	/** 
	 流程进度图
	*/
	public ExtJobSchedule()
	{
	}
	/** 
	 流程进度图
	 
	 @param mypk
	*/
	public ExtJobSchedule(String mypk) throws Exception
	{
		this.setMyPK(mypk);
		this.Retrieve();
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
		Map map = new Map("Sys_MapAttr", "流程进度图");
		map.setDepositaryOfEntity(Depositary.Application);
		map.setDepositaryOfMap(Depositary.Application);



			///#region 通用的属性.
		map.AddMyPK(true);
		map.AddTBString(MapAttrAttr.FK_MapData, null, "表单ID", true, true, 1, 100, 20);
		map.AddTBString(MapAttrAttr.KeyOfEn, null, "字段", true, true, 1, 100, 20);
		map.AddTBInt(MapAttrAttr.GroupID, 0, "显示的分组", false, true);
//		map.AddDDLSQL(MapAttrAttr.GroupID, "0", "显示的分组", MapAttrString.getSQLOfGroupAttr(), true);

		//map.AddDDLSysEnum(MapAttrAttr.LabelColSpan, 1, "文本单元格数量", true, true, "ColSpanAttrString",
		//    "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格");
		//map.AddTBInt(MapAttrAttr.RowSpan, 1, "行数", true, false);

		map.AddTBFloat(MapAttrAttr.UIHeight, 1, "高度", true, false);
		map.AddTBFloat(MapAttrAttr.UIWidth, 1, "宽度", true, false);

		map.AddTBString(MapAttrAttr.Name, null, "名称", true, false, 0, 500, 20, true);

			///#endregion 通用的属性.


			///#region 个性化属性.
	   // map.AddTBString(MapAttrAttr.Tag1, "_blank", "连接目标(_blank,_parent,_self)", true, false, 0, 20, 20);
	   // map.AddTBString(MapAttrAttr.Tag2, null, "URL", true, false, 0, 500, 20, true);

			///#endregion 个性化属性.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
