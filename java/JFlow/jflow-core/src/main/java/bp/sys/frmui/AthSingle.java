package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.en.Map;
import bp.sys.*;
import java.util.*;

/** 
 字段单附件： 附件属性存储在 FrmAttachmentSingle
*/
public class AthSingle extends EntityMyPK
{

		///#region 属性
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
	 字段
	*/
	public final String getKeyOfEn()  {
		return this.GetValStringByKey(MapAttrAttr.KeyOfEn);
	}
	public final void setKeyOfEn(String value){
		this.SetValByKey(MapAttrAttr.KeyOfEn, value);
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
	 字段单附件
	*/
	public AthSingle()
	{
	}
	/** 
	 字段单附件
	 
	 @param mypk
	*/
	public AthSingle(String mypk) throws Exception {
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
			return this.get_enMap();
		Map map = new Map("Sys_MapAttr", "字段单附件");

		map.AddMyPK();
		map.AddTBString(MapAttrAttr.FK_MapData, null, "表单ID", true, true, 1, 100, 20);
		map.AddTBString(MapAttrAttr.KeyOfEn, null, "字段", true, true, 1, 100, 20);
		map.AddTBString(MapAttrAttr.Name, null, "名称", true, false, 0, 500, 20, true);
		map.AddDDLSQL(MapAttrAttr.GroupID, 0, "显示的分组", MapAttrString.getSQLOfGroupAttr(), true);
		map.AddDDLSysEnum(MapAttrAttr.LabelColSpan, 1, "单元格数量", true, true, "ColSpanAttrString", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格");
		map.AddTBInt(MapAttrAttr.RowSpan, 1, "行数", true, false);

		map.AddTBFloat(MapAttrAttr.UIHeight, 1, "高度", true, false);
		map.AddTBFloat(MapAttrAttr.UIWidth, 1, "宽度", true, false);

		this.set_enMap(map);
		return this.get_enMap();
	}
	/** 
	 删除后清缓存
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		//删除相对应的rpt表中的字段
		if (this.getFrmID().contains("ND") == true)
		{
			String fk_mapData = this.getFrmID().substring(0, this.getFrmID().length() - 2) + "Rpt";
			String sql = "DELETE FROM Sys_MapAttr WHERE FK_MapData='" + fk_mapData + "' AND KeyOfEn='" + this.getKeyOfEn() + "'";
			DBAccess.RunSQL(sql);

			//删除对应的附件属性.
			FrmAttachment ath = new FrmAttachment();
			ath.setMyPK(this.getFrmID() + "_" + this.getKeyOfEn());
			ath.Delete();
		}

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());
		super.afterDelete();
	}

		///#endregion
}
