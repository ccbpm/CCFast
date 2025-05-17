package bp.wf.admin;

import bp.en.*;
import bp.en.Map;
import bp.web.*;
import bp.sys.*;


/** 
 流程
*/
public class Frm extends EntityNoName
{

		///#region 属性.
	/** 
	 存储表
	*/
	public final String getPTable()  {
		return this.GetValStringByKey(FrmAttr.PTable);
	}
	public final void setPTable(String value){
		this.SetValByKey(FrmAttr.PTable, value);
	}

		///#endregion 属性.


		///#region 构造方法
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (WebUser.getIsAdmin() == false)
		{
			throw new RuntimeException("err@管理员登录用户信息丢失,当前会话[" + WebUser.getNo() + "," + WebUser.getName() + "]");
		}
		uac.IsUpdate = true;
		uac.IsDelete = false;
		uac.IsInsert = false;
		return uac;
	}
	/** 
	 流程
	*/
	public Frm()
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


		Map map = new Map("Sys_MapData", "经典表单属性");


			///#region 基本属性.
		map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, true, 1, 190, 20);
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			map.AddTBString(MapDataAttr.PTable, null, "存储表", false, false, 0, 100, 20);
		}
		else
		{
			map.AddTBString(MapDataAttr.PTable, null, "存储表", true, false, 0, 100, 20);
			String msg = "提示:";
			msg += "\t\n1. 该表单把数据存储到那个表里.";
			msg += "\t\n2. 该表必须有一个int64未的OID列作为主键..";
			msg += "\t\n3. 如果指定了一个不存在的表,系统就会自动创建上.";
			map.SetHelperAlert(MapDataAttr.PTable, msg);
		}

		map.AddTBString(MapDataAttr.Name, null, "名称", true, false, 0, 500, 20, true);
		map.AddTBInt(MapDataAttr.TableCol, 0, "显示列数", false, false);
		map.AddTBInt(MapDataAttr.FrmW, 900, "表单宽度", true, false);

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
		}
		else
		{
			map.AddTBString(MapDataAttr.DBSrc, null, "数据源", false, false, 0, 500, 20);
			map.AddDDLEntities(MapDataAttr.FK_FormTree, "01", "目录", new bp.wf.template.SysFormTrees(), true);
		}

		//表单的运行类型.
		map.AddDDLSysEnum(MapDataAttr.FrmType, FrmType.FoolForm.getValue(), "表单类型", true, true, MapDataAttr.FrmType);

		//表单解析 0 普通 1 页签展示
		map.AddDDLSysEnum(MapDataAttr.FrmShowType, 0, "表单展示方式", true, true, "表单展示方式", "@0=普通方式@1=页签方式");


		map.AddTBString(MapDataAttr.Icon, "icon-doc", "图标", true, false, 0, 100, 100);

		map.AddBoolean("IsEnableJs", false, "是否启用自定义js函数？", true, true, true);

			///#endregion 基本属性.


			///#region 设计者信息.
		map.AddTBString(MapDataAttr.Designer, null, "设计者", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerContact, null, "联系方式", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerUnit, null, "单位", true, false, 0, 500, 20, true);
		map.AddTBString(MapDataAttr.GUID, null, "GUID", true, true, 0, 128, 20, false);
		map.AddTBString(MapDataAttr.Ver, null, "版本号", true, true, 0, 30, 20);
		map.AddTBString(MapDataAttr.Note, null, "备注", true, false, 0, 400, 100, true);
		//增加参数字段.
		map.AddTBAtParas(4000);
		map.AddTBInt(MapDataAttr.Idx, 100, "顺序号", false, false);

			///#endregion 设计者信息.

		map.AddSearchAttr(MapDataAttr.FK_FormTree, 130);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
