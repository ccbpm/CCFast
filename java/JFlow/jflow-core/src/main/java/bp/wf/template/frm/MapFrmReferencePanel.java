package bp.wf.template.frm;

import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 Word表单属性
*/
public class MapFrmReferencePanel extends EntityNoName
{

		///#region 文件模版属性.
	/** 
	 模版版本号
	*/
	public final String getRefTitle()  {
		return this.GetValStringByKey(MapFrmReferencePanelAttr.RefTitle);
	}
	public final void setRefTitle(String value){
		this.SetValByKey(MapFrmReferencePanelAttr.RefTitle, value);
	}

		///#endregion 文件模版属性.



		///#region 权限控制.
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getNo().equals("admin") == true)
		{
			uac.IsDelete = false;
			uac.IsUpdate = true;
			return uac;
		}
		uac.Readonly();
		return uac;
	}

		///#endregion 权限控制.


		///#region 构造方法
	/** 
	 Word表单属性
	*/
	public MapFrmReferencePanel()
	{
	}
	/** 
	 Word表单属性
	 
	 @param no 表单ID
	*/
	public MapFrmReferencePanel(String no) throws Exception
	{
		super(no);
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
		Map map = new Map("Sys_MapData", "参考面板");



			///#region 基本属性.
		map.AddTBStringPK(MapFrmReferencePanelAttr.No, null, "表单编号", true, true, 1, 190, 20);

		map.AddDDLSysEnum(MapFrmReferencePanelAttr.RefWorkModel, 0, "工作模式", true, true, MapFrmReferencePanelAttr.RefWorkModel, "@0=禁用@1=静态Html脚本@2=静态框架Url@3=动态Url@4=动态Html脚本");

		map.AddTBString(MapFrmReferencePanelAttr.RefBlurField, null, "失去焦点字段", true, false, 0, 100, 20, true);
		map.SetHelperAlert(MapFrmReferencePanelAttr.RefBlurField, "配置表单字段名字，对【动态url】有效.");

		map.AddTBString(MapFrmReferencePanelAttr.RefUrl, null, "连接", true, false, 0, 100, 20, true);
		map.AddTBStringDoc(MapFrmReferencePanelAttr.RefHtml, null, "静态Html脚本", true, false, true, 10);


			///#endregion 基本属性.


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


}
