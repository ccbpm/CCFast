package bp.sys;

import bp.en.*; import bp.en.Map;

/** 
 按钮
*/
public class FrmBtn extends EntityMyPK
{

		///#region 属性
	/** 
	 所在的分组
	*/
	public final int getGroupID()  {
		return this.GetValIntByKey(FrmBtnAttr.GroupID);
	}
	public final void setGroupID(long value){
		this.SetValByKey(FrmBtnAttr.GroupID, value);
	}
	public final String getMsgOK()  {
		return this.GetValStringByKey(FrmBtnAttr.MsgOK);
	}
	public final void setMsgOK(String value){
		this.SetValByKey(FrmBtnAttr.MsgOK, value);
	}
	public final String getMsgErr()  {
		return this.GetValStringByKey(FrmBtnAttr.MsgErr);
	}
	public final void setMsgErr(String value){
		this.SetValByKey(FrmBtnAttr.MsgErr, value);
	}
	/** 
	 EventContext
	*/
	public final String getEventContext()  {
		return this.GetValStringByKey(FrmBtnAttr.EventContext).replace("#", "@");
		//return this.GetValStringByKey(FrmBtnAttr.EventContext);
	}
	public final void setEventContext(String value){
		this.SetValByKey(FrmBtnAttr.EventContext, value);
	}

	public final String getUACContext()  {
		return this.GetValStringByKey(FrmBtnAttr.UACContext);
	}
	public final void setUACContext(String value){
		this.SetValByKey(FrmBtnAttr.UACContext, value);
	}
	/** 
	 IsEnable
	*/
	public final boolean getItIsEnable()  {
		return this.GetValBooleanByKey(FrmBtnAttr.IsEnable);
	}
	public final void setItIsEnable(boolean value){
		this.SetValByKey(FrmBtnAttr.IsEnable, value);
	}
	/** 
	 Y
	*/
	public final float getY()  {
		return this.GetValFloatByKey(FrmBtnAttr.Y);
	}
	public final void setY(float value){
		this.SetValByKey(FrmBtnAttr.Y, value);
	}
	/** 
	 X
	*/
	public final float getX()  {
		return this.GetValFloatByKey(FrmBtnAttr.X);
	}
	public final void setX(float value){
		this.SetValByKey(FrmBtnAttr.X, value);
	}
	public final BtnEventType getHisBtnEventType() {
		return BtnEventType.forValue(this.GetValIntByKey(FrmBtnAttr.EventType));
	}
	/** 
	 BtnType
	*/
	public final int getEventType()  {
		return this.GetValIntByKey(FrmBtnAttr.EventType);
	}
	public final void setEventType(int value){
		this.SetValByKey(FrmBtnAttr.EventType, value);
	}
	/** 
	 FK_MapData
	*/
	public final String getFrmID()  {
		return this.GetValStrByKey(FrmBtnAttr.FK_MapData);
	}
	public final void setFrmID(String value){
		this.SetValByKey(FrmBtnAttr.FK_MapData, value);
	}
	/** 
	 Text
	*/
	public final String getLab()  {
		return this.GetValStrByKey(FrmBtnAttr.Lab);
	}
	public final void setLab(String value){
		this.SetValByKey(FrmBtnAttr.Lab, value);
	}
	public final String getTextHtml() throws Exception {
		//if (this.EventType)
		//    return "<b>" + this.GetValStrByKey(FrmBtnAttr.Text).replace("@","<br>") + "</b>";
		//else
			return this.GetValStrByKey(FrmBtnAttr.Lab).replace("@", "<br>");
	}

		///#endregion


		///#region 构造方法
	/** 
	 按钮
	*/
	public FrmBtn()
	{
	}
	/** 
	 按钮
	 
	 @param mypk
	*/
	public FrmBtn(String mypk) throws Exception {
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

		Map map = new Map("Sys_FrmBtn", "按钮");
		map.IndexField = FrmBtnAttr.FK_MapData;

		map.AddMyPK();
		map.AddTBString(FrmBtnAttr.FK_MapData, null, "表单ID", true, false, 1, 100, 20);
		map.AddTBString(FrmBtnAttr.Lab, null, "标签", true, false, 0, 3900, 20);

		map.AddTBFloat(FrmBtnAttr.X, 5, "X", true, false);
		map.AddTBFloat(FrmBtnAttr.Y, 5, "Y", false, false);

		map.AddTBInt(FrmBtnAttr.IsView, 0, "是否可见", false, false);
		map.AddTBInt(FrmBtnAttr.IsEnable, 0, "是否起用", false, false);

		//map.AddTBInt(FrmBtnAttr.BtnType, 0, "类型", false, false);

		map.AddTBInt(FrmBtnAttr.UAC, 0, "控制类型", false, false);
		map.AddTBString(FrmBtnAttr.UACContext, null, "控制内容", true, false, 0, 3900, 20);

		map.AddTBInt(FrmBtnAttr.EventType, 0, "事件类型", false, false);
		map.AddTBString(FrmBtnAttr.EventContext, null, "事件内容", true, false, 0, 3900, 20);

		map.AddTBString(FrmBtnAttr.MsgOK, null, "运行成功提示", true, false, 0, 500, 20);
		map.AddTBString(FrmBtnAttr.MsgErr, null, "运行失败提示", true, false, 0, 500, 20);

		map.AddTBString(FrmBtnAttr.BtnID, null, "按钮ID", true, false, 0, 128, 20);

		map.AddTBInt(FrmBtnAttr.GroupID, 0, "所在分组", false, false);


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
