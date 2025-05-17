package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;


/** 
 分组
*/
public class GroupField extends EntityOID
{

		///#region 权限控制
	/** 
	 权限控制.
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getNo().equals("admin") == true || bp.web.WebUser.getIsAdmin() )
		{
			uac.IsDelete = true;
			uac.IsInsert = false;
			uac.IsUpdate = true;
			return uac;
		}
		uac.Readonly();
		uac.IsView = false;
		return uac;
	}

		///#endregion 权限控制


		///#region 属性
	public boolean ItIsUse = false;
	/** 
	 表单ID
	*/
	public final String getFrmID()  {
		return this.GetValStrByKey(GroupFieldAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(GroupFieldAttr.FrmID, value);
	}
	public final String getEnName()  {
		return this.GetValStrByKey(GroupFieldAttr.FrmID);
	}
	public final void setEnName(String value){
		this.SetValByKey(GroupFieldAttr.FrmID, value);
	}
	/** 
	 标签
	*/
	public final String getLab()  {
		return this.GetValStrByKey(GroupFieldAttr.Lab);
	}
	public final void setLab(String value){
		this.SetValByKey(GroupFieldAttr.Lab, value);
	}
	/** 
	 顺序号
	*/
	public final int getIdx()  {
		return this.GetValIntByKey(GroupFieldAttr.Idx);
	}
	public final void setIdx(int value){
		this.SetValByKey(GroupFieldAttr.Idx, value);
	}
	/** 
	 控件类型
	*/
	public final String getCtrlType()  {
		return this.GetValStrByKey(GroupFieldAttr.CtrlType);
	}
	public final void setCtrlType(String value){
		this.SetValByKey(GroupFieldAttr.CtrlType, value);
	}
	/** 
	 控件ID
	*/
	public final String getCtrlID()
	{
		return this.GetValStrByKey(GroupFieldAttr.CtrlID);
	}
	public final void setCtrlID(String value){
		this.SetValByKey(GroupFieldAttr.CtrlID, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 GroupField
	*/
	public GroupField()
	{
	}
	public GroupField(int oid) throws Exception {
		super(oid);
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
		Map map = new Map("Sys_GroupField", "经典表单分组");


			///#region 字段.
		map.AddTBIntPKOID();
		map.AddTBString(GroupFieldAttr.Lab, null, "标签", true, false, 0, 500, 20, true);
		map.AddTBString(GroupFieldAttr.FrmID, null, "表单ID", true, true, 0, 200, 20);

		map.AddTBString(GroupFieldAttr.CtrlType, null, "控件类型", true, false, 0, 50, 20);
		map.SetHelperUrl("CtrlType", "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=8108065&doc_id=31094");

		//  map.SetHelperAlert(GroupFieldAttr.CtrlType, "对章节表单有效,类型有:ChapterFrmLinkFrm,Dtl,Ath,FWC");

		map.AddTBString(GroupFieldAttr.CtrlID, null, "控件ID", true, false, 0, 500, 20);

		map.AddBoolean(GroupFieldAttr.IsZDPC, false, "是否折叠(PC)", false, true);
		map.AddBoolean(GroupFieldAttr.IsZDMobile, false, "是否折叠(Mobile)", true, true);
		map.AddDDLSysEnum(GroupFieldAttr.ShowType, 0, "分组显示模式", true, true, GroupFieldAttr.ShowType, "@0=显示@1=PC折叠@2=隐藏");

		map.AddTBString("ParentOID", null, "父级", true, false, 0, 128, 20, false);
		map.SetHelperAlert("ParentOID", "对章节表单有效:章节表单的目录父子关系,默认为0,就是跟目录.");

		map.AddTBString("Icon", null, "图标", true, true, 0, 128, 20, true);
		map.AddTBInt(GroupFieldAttr.Idx, 99, "顺序号", true, false);
		map.AddTBString(MapAttrAttr.GUID, null, "GUID", true, true, 0, 128, 20, true);
		map.AddTBAtParas(3000);

		map.AddLang(); //增加多语言.

			///#endregion 字段.


			///#region 方法.
		RefMethod rm = new RefMethod();
		rm = new RefMethod();
		rm.Title = "删除隶属分组的字段";
		rm.Warning = "您确定要删除该分组下的所有字段吗？";
		rm.ClassMethodName = this.toString() + ".DoDelAllField";
		rm.refMethodType = RefMethodType.Func;
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "章节表单分组扩展";
		rm.ClassMethodName = this.toString() + ".DoSetGFType";
		//   rm.getHisAttrs().AddDDLSysEnum("Type", 0, "设置类型", true, true, "MyGFType", "@0=链接到其它表单@1=自定义URL");

		rm.getHisAttrs().AddTBInt("Type", 0, "设置类型：0链接到其它表单，1自定义URL", true, false);
		rm.getHisAttrs().AddTBString("val", null, "输入对应的值", true, false, 0, 1000, 1000);
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "调整字段顺序";
		rm.ClassMethodName = this.toString() + ".DoGroupFieldIdx";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		// map.AddRefMethod(rm);

			///#endregion 方法.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion



		///#region 方法.
	/** 
	 设置分组解析类型(对章节表单有效)
	 
	 @param type 类型
	 @param val 值
	 @return 执行结果
	*/
	public final String DoSetGFType(int type, String val) throws Exception {
		MapData md = new MapData(this.getFrmID());
		if (md.getHisFrmType() != FrmType.ChapterFrm)
		{
			return "err@该设置对章节表单有效.";
		}

		//链接到其他表单上
		if (type == 0)
		{
			md.setNo(val);
			if (md.RetrieveFromDBSources() == 0)
			{
				return "err@表单ID输入错误[" + val + "].";
			}

			this.setCtrlType("ChapterFrmLinkFrm");
			this.setCtrlID(val);
		}

		//如果是自定义url.
		if (type == 1)
		{
			this.setCtrlType("ChapterFrmSelfUrl");
			this.setCtrlID(val);
		}

		this.Update();
		return "执行成功.";
	}
	/** 
	 外部调用的
	 
	 @return 
	*/
	public final String AddGroup() throws Exception {
		this.InsertAsNew();
		return "执行成功.";
	}

	/** 
	 删除所有隶属该分组的字段.
	 
	 @return 
	*/
	public final String DoDelAllField() throws Exception {
		String attrs = "'OID','RDT','REC','RefPK','FID','BillNo','Title','Name','AtPara','BillState','FK_Dept','OrgNo','RDT','Starter','StarterName'";
		String sql = "DELETE FROM Sys_MapAttr WHERE FK_MapData='" + this.getFrmID() + "' AND GroupID=" + this.getOID() + " AND KeyOfEn NOT IN ("+ attrs + ")";
		int i = DBAccess.RunSQL(sql);
		return "删除字段{" + i + "}个，被删除成功.";
	}
	/** 
	 分组内的字段顺序调整
	 
	 @return 
	*/
	public final String DoGroupFieldIdx() {
		return "../../Admin/FoolFormDesigner/GroupFieldIdx.htm?FrmID=" + this.getFrmID() + "&GroupField=" + this.getOID();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		String sql = "UPDATE Sys_GroupField SET LAB='" + this.getLab() + "' WHERE OID=" + this.getOID();
		DBAccess.RunSQL(sql);
		return super.beforeUpdate();
	}
	public final String DoDown() throws Exception
	{
		this.DoOrderDown(GroupFieldAttr.FrmID, this.getFrmID(), GroupFieldAttr.Idx);
		return "执行成功";
	}
	public final String DoUp() throws Exception
	{
		this.DoOrderUp(GroupFieldAttr.FrmID, this.getFrmID(), GroupFieldAttr.Idx);
		return "执行成功";
	}
	@Override
	protected boolean beforeDelete() throws Exception {
		String sql = "SELECT Name,KeyOfEn FROM Sys_MapAttr WHERE GroupID=" + this.getOID();

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		String str = "";
		for (DataRow dr : dt.Rows)
		{
			str += " \t\n" + dr.getValue(0).toString() + " - " + dr.getValue(1).toString();
		}

		if (DataType.IsNullOrEmpty(str) == false)
		{
			str = "err@分组ID:" + this.getOID() + "删除分组错误:如下字段存在，您不能删除:" + str;
			str += "\t\n 您要删除这个分组，请按照如下操作。";
			str += "\t\n 1. 移除字段到其他分组里面去. ";
			str += "\t\n 2. 删除字段. ";
			str += "\t\n 3. 如果是隐藏字段，您可以在表单设计器中，表单属性点开隐藏字段,打开隐藏字段，并编辑所在分组. ";
			str += "\t\n +++++++++ 容器存在的字段 +++++++++++ ";
			str += "\t\n  " + str;

			throw new RuntimeException(str);
		}

		return super.beforeDelete();
	}
	@Override
	protected boolean beforeInsert() throws Exception
	{
		try
		{
			String sql = "SELECT MAX(Idx) FROM " + this.getEnMap().getPhysicsTable() + " WHERE FrmID='" + this.getFrmID() + "'";
			this.setIdx(DBAccess.RunSQLReturnValInt(sql, 0) + 1);
		}
		catch (java.lang.Exception e)
		{
			this.setIdx(1);
		}
		return super.beforeInsert();
	}
	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		if (this.getCtrlType().equals("Frame") == true)
		{
			MapFrame frame = new MapFrame(this.getCtrlID());
			if (this.getLab().equals(frame.getName()) == false)
			{
				frame.setName(this.getLab());
				frame.DirectUpdate();
			}
		}
		super.afterInsertUpdateAction();
	}

		///#endregion 方法.
}
