package bp.ccbill.template;

import bp.da.*;
import bp.en.Map;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 功能执行
*/
public class CollectionFunc extends EntityNoName
{

		///#region 基本属性
	/** 
	 表单ID
	*/
	public final String getFrmID() {
		return this.GetValStringByKey(CollectionAttr.FrmID);
	}
	public final void setFrmID(String value)  {
		this.SetValByKey(CollectionAttr.FrmID, value);
	}
	/** 
	 方法ID
	*/
	public final String getMethodID() {
		return this.GetValStringByKey(CollectionAttr.MethodID);
	}
	public final void setMethodID(String value)  {
		this.SetValByKey(CollectionAttr.MethodID, value);
	}

	public final String getMsgErr() {
		return this.GetValStringByKey(CollectionAttr.MsgErr);
	}
	public final void setMsgErr(String value)  {
		this.SetValByKey(CollectionAttr.MsgErr, value);
	}
	public final String getMsgSuccess() {
		return this.GetValStringByKey(CollectionAttr.MsgSuccess);
	}
	public final void setMsgSuccess(String value)  {
		this.SetValByKey(CollectionAttr.MsgSuccess, value);
	}
	public final String getTag1() {
		return this.GetValStringByKey(CollectionAttr.Tag1);
	}
	public final void setTag1(String value)  {
		this.SetValByKey(CollectionAttr.Tag1, value);
	}

	/** 
	 方法类型
	*/
	public final RefMethodType getRefMethodType() {
		return RefMethodType.forValue(this.GetValIntByKey(MethodAttr.RefMethodType));
	}
	public final void setRefMethodType(RefMethodType value)  {
		this.SetValByKey(MethodAttr.RefMethodType, value.getValue());
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
	 功能执行
	*/
	public CollectionFunc()
	{
	}
	public CollectionFunc(String mypk) throws Exception {
		this.setNo(mypk);
		this.Retrieve();
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

		Map map = new Map("Frm_Collection", "功能方法");


		//主键.
		map.AddTBStringPK(MethodAttr.No, null, "编号", true, true, 0, 50, 10);
		map.AddTBString(MethodAttr.Name, null, "方法名", true, false, 0, 300, 10);
		map.AddTBString(MethodAttr.MethodID, null, "方法ID", true, true, 0, 300, 10);
		map.AddTBString(MethodAttr.GroupID, null, "分组ID", true, true, 0, 50, 10);

		//功能标记.
		map.AddTBString(MethodAttr.MethodModel, null, "方法模式", true, true, 0, 300, 10);
		map.AddTBString(MethodAttr.Tag1, null, "Tag1", true, true, 0, 300, 10);
		map.AddTBString(MethodAttr.FrmID, null, "表单ID", true, true, 0, 300, 10);

		map.AddTBString(MethodAttr.Icon, null, "图标", true, false, 0, 50, 10, true);

		map.AddTBString(MethodAttr.Mark, null, "功能说明", true, false, 0, 900, 10, true);
		map.SetHelperAlert(MethodAttr.Mark, "对于该功能的描述.");



		map.AddTBString(MethodAttr.WarningMsg, null, "功能执行警告信息", true, false, 0, 300, 10, true);
		//map.AddDDLSysEnum(MethodAttr.ShowModel, 0, "显示方式", true, true, MethodAttr.ShowModel,
		//  "@0=按钮@1=超链接");

		map.AddDDLSysEnum(MethodAttr.MethodDocTypeOfFunc, 0, "内容类型", true, false, "MethodDocTypeOfFunc", "@0=SQL@1=URL@2=JavaScript@3=业务单元");

		map.AddTBString(MethodAttr.MsgSuccess, null, "成功提示信息", true, false, 0, 300, 10, true);
		map.AddTBString(MethodAttr.MsgErr, null, "失败提示信息", true, false, 0, 300, 10, true);



		RefMethod rm = new RefMethod();

		rm.Title = "方法内容"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoDocs";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		//rm.GroupName = "开发接口";
		map.AddRefMethod(rm);


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 执行方法.
	/** 
	 方法参数
	 
	 @return 
	*/
	public final String DoParas() throws Exception {

		return "../../CCBill/Admin/MethodParas.htm?No=" + this.getMethodID();
	}
	/** 
	 方法内容
	 
	 @return 
	*/
	public final String DoDocs() {
		return "../../CCBill/Admin/MethodDoc/Default.htm?No=" + this.getNo();
	}
	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getNo()) == true)
		{
			this.setNo(DBAccess.GenerGUID(0, null, null));
		}
		return super.beforeInsert();
	}

		///#endregion 执行方法.

}
