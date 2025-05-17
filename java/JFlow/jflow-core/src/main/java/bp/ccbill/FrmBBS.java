package bp.ccbill;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import java.util.*;

/** 
 评论组件
*/
public class FrmBBS extends EntityNoName
{

		///#region 字段属性.
	/** 
	 参数数据.
	*/
	public final String getDocs() {
		return this.GetValStringByKey(FrmBBSAttr.Docs);
	}
	public final void setDocs(String value)  {
		this.SetValByKey(FrmBBSAttr.Docs, value);
	}
	/** 
	 表单ID
	*/
	public final String getFrmID() {
		return this.GetValStringByKey(FrmBBSAttr.FrmID);
	}
	public final void setFrmID(String value)  {
		this.SetValByKey(FrmBBSAttr.FrmID, value);
	}
	/** 
	 表单名称
	*/
	public final String getFrmName() {
		return this.GetValStringByKey(FrmBBSAttr.FrmName);
	}
	public final void setFrmName(String value)  {
		this.SetValByKey(FrmBBSAttr.FrmName, value);
	}
	/** 
	 记录日期
	*/
	public final String getRDT() {
		return this.GetValStringByKey(FrmBBSAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(FrmBBSAttr.RDT, value);
	}

	/** 
	 工作ID
	*/
	public final long getWorkID() {
		return this.GetValInt64ByKey(FrmBBSAttr.WorkID);
	}
	public final void setWorkID(long value)  {
		this.SetValByKey(FrmBBSAttr.WorkID, value);
	}

	/** 
	 活动名称
	*/
	public final String getActionTypeText() {
		return this.GetValStringByKey(FrmBBSAttr.ActionTypeText);
	}
	public final void setActionTypeText(String value)  {
		this.SetValByKey(FrmBBSAttr.ActionTypeText, value);
	}
	/** 
	 记录人
	*/
	public final String getRec() {
		return this.GetValStringByKey(FrmBBSAttr.Rec);
	}
	public final void setRec(String value)  {
		this.SetValByKey(FrmBBSAttr.Rec, value);
	}
	/** 
	 记录人名字
	*/
	public final String getRecName() {
		return this.GetValStringByKey(FrmBBSAttr.RecName);
	}
	public final void setRecName(String value)  {
		this.SetValByKey(FrmBBSAttr.RecName, value);
	}
	/** 
	 消息
	*/
	public final String getMsg() {
		return this.GetValStringByKey(FrmBBSAttr.Msg);
	}
	public final void setMsg(String value)  {
		this.SetValByKey(FrmBBSAttr.Msg, value);
	}
	/** 
	 消息
	*/
	public final String getMsgHtml() throws Exception {
		return this.GetValHtmlStringByKey(FrmBBSAttr.Msg);
	}

		///#endregion attrs


		///#region 流程属性.
	public final String getDeptNo() {
		return this.GetValStringByKey(FrmBBSAttr.DeptNo);
	}
	public final void setDeptNo(String value)  {
		this.SetValByKey(FrmBBSAttr.DeptNo, value);
	}
	public final String getDeptName() {
		return this.GetValStringByKey(FrmBBSAttr.DeptName);
	}
	public final void setDeptName(String value)  {
		this.SetValByKey(FrmBBSAttr.DeptName, value);
	}

		///#endregion 流程属性.


		///#region 构造.
	/** 
	 表单评论组件表
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Frm_BBS", "表单评论组件表");


			///#region 基本字段.

		map.AddTBStringPK(FrmBBSAttr.No, null, "No", true, false, 0, 50, 200);

		map.AddTBString(FrmBBSAttr.Name, null, "标题", true, false, 0, 4000, 200);
		map.AddTBString(FrmBBSAttr.ParentNo, null, "父节点", true, false, 0, 50, 200);
		map.AddTBString(FrmBBSAttr.WorkID, null, "工作ID/OID", true, false, 0, 50, 200);

		map.AddTBString(FrmBBSAttr.Docs, null, "内容", true, false, 0, 50, 200);

		//map.AddTBInt(FrmBBSAttr.ActionType, 0, "类型", true, false);
		// map.AddTBString(FrmBBSAttr.ActionTypeText, null, "类型(名称)", true, false, 0, 30, 100);

		map.AddTBString(FrmBBSAttr.Rec, null, "记录人", true, false, 0, 200, 100);
		map.AddTBString(FrmBBSAttr.RecName, null, "名称", true, false, 0, 200, 100);
		map.AddTBDateTime(FrmBBSAttr.RDT, null, "记录日期时间", true, false);

		map.AddTBString(FrmBBSAttr.DeptNo, null, "部门编号", true, false, 0, 200, 100);
		map.AddTBString(FrmBBSAttr.DeptName, null, "名称", true, false, 0, 200, 100);

			///#endregion 基本字段


		map.AddTBString(FrmBBSAttr.FrmID, null, "表单ID", true, false, 0, 50, 200);
		map.AddTBString(FrmBBSAttr.FrmName, null, "表单名称(可以为空)", true, false, 0, 200, 200);
		map.AddMyFile(null, null, null);
		this.set_enMap(map);
		return this.get_enMap();
	}
	/** 
	 评论组件
	*/
	public FrmBBS()
	{
	}
	public FrmBBS(String no) throws Exception
	{
		this.setNo(no);
		this.Retrieve();
	}

		///#endregion 构造.

	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setNo(DBAccess.GenerGUID(0, null, null));

		this.SetValByKey(FrmBBSAttr.Rec, bp.web.WebUser.getNo());
		this.SetValByKey(FrmBBSAttr.RecName, bp.web.WebUser.getName());
		this.SetValByKey(FrmBBSAttr.RDT, DataType.getCurrentDateTime());

		this.SetValByKey(FrmBBSAttr.DeptNo, bp.web.WebUser.getDeptNo());
		this.SetValByKey(FrmBBSAttr.DeptName, bp.web.WebUser.getDeptName());

		return super.beforeInsert();
	}
	@Override
	protected void afterDelete() throws Exception
	{
		FrmBBSs ens = new FrmBBSs();
		ens.Delete(FrmBBSAttr.ParentNo, this.getNo());
		super.afterDelete();
	}
}
