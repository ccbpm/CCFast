package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;

/** 
 报表模板
*/
public class RptTemplate extends Entity
{

		///#region 基本属性
	/** 
	 集合类名称
	*/
	public final String getEnsName()  {
		return this.GetValStringByKey(RptTemplateAttr.EnsName);
	}
	public final void setEnsName(String value){
		this.SetValByKey(RptTemplateAttr.EnsName, value);
	}
	public final String getFKEmp()  {
		return this.GetValStringByKey(RptTemplateAttr.FK_Emp);
	}
	public final void setFKEmp(String value){
		this.SetValByKey(RptTemplateAttr.FK_Emp, value);
	}
	/** 
	 描述
	*/
	public final String getMyPK()  {
		return this.GetValStringByKey(RptTemplateAttr.MyPK);
	}
	public final void setMyPK(String value){
		this.SetValByKey(RptTemplateAttr.MyPK, value);
	}
	/** 
	 D1
	*/
	public final String getD1()  {
		return this.GetValStringByKey(RptTemplateAttr.D1);
	}
	public final void setD1(String value){
		this.SetValByKey(RptTemplateAttr.D1, value);
	}
	/** 
	 D2
	*/
	public final String getD2()  {
		return this.GetValStringByKey(RptTemplateAttr.D2);
	}
	public final void setD2(String value){
		this.SetValByKey(RptTemplateAttr.D2, value);
	}
	/** 
	 D3
	*/
	public final String getD3()  {
		return this.GetValStringByKey(RptTemplateAttr.D3);
	}
	public final void setD3(String value){
		this.SetValByKey(RptTemplateAttr.D3, value);
	}
	public final String getAlObjsText()  {
		return this.GetValStringByKey(RptTemplateAttr.AlObjs);
	}
	/** 
	 分析的对象
	 数据格式 @分析对象1@分析对象2@分析对象3@
	*/
	public final String getAlObjs()  {
		return this.GetValStringByKey(RptTemplateAttr.AlObjs);
	}
	public final void setAlObjs(String value){
		this.SetValByKey(RptTemplateAttr.AlObjs, value);
	}
	public final int getHeight()  {
		return this.GetValIntByKey(RptTemplateAttr.Height);
	}
	public final void setHeight(int value){
		this.SetValByKey(RptTemplateAttr.Height, value);
	}
	public final int getWidth()  {
		return this.GetValIntByKey(RptTemplateAttr.Width);
	}
	public final void setWidth(int value){
		this.SetValByKey(RptTemplateAttr.Width, value);
	}
	/** 
	 是否显示大合计
	*/
	public final boolean isSumBig()  {
		return this.GetValBooleanByKey(RptTemplateAttr.IsSumBig);
	}
	public final void setSumBig(boolean value){
		this.SetValByKey(RptTemplateAttr.IsSumBig, value);
	}
	/** 
	 小合计
	*/
	public final boolean isSumLittle()  {
		return this.GetValBooleanByKey(RptTemplateAttr.IsSumLittle);
	}
	public final void setSumLittle(boolean value){
		this.SetValByKey(RptTemplateAttr.IsSumLittle, value);
	}
	/** 
	 是否现实右合计。
	*/
	public final boolean isSumRight()  {
		return this.GetValBooleanByKey(RptTemplateAttr.IsSumRight);
	}
	public final void setSumRight(boolean value){
		this.SetValByKey(RptTemplateAttr.IsSumRight, value);
	}
	public final PercentModel getPercentModel() {
		return PercentModel.forValue(this.GetValIntByKey(RptTemplateAttr.PercentModel));
	}
	public final void setPercentModel(PercentModel value){
		this.SetValByKey(RptTemplateAttr.PercentModel, value.getValue());
	}

		///#endregion


		///#region 构造方法

	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsUpdate = true;
		uac.IsView = true;
		return super.getHisUAC();
	}
	/** 
	 
	*/
	public RptTemplate()
	{
	}
	/** 
	 类
	 
	 @param ensName
	*/
	public RptTemplate(String ensName) throws Exception {
		this.setEnsName(ensName);
		this.setFKEmp(bp.web.WebUser.getNo());
		this.setMyPK(bp.web.WebUser.getNo() + "@" + getEnsName());
		try
		{
			this.Retrieve();
		}
		catch (java.lang.Exception e)
		{
			this.Insert();
		}
	}

	/** 
	 报表模板
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Sys_RptTemplate", "报表模板");
		map.setDepositaryOfEntity(Depositary.Application);
		map.setEnType(EnType.Sys);

		map.AddMyPK();
		map.AddTBString(RptTemplateAttr.EnsName, null, "类名", false, false, 0, 500, 20);
		map.AddTBString(RptTemplateAttr.FK_Emp, null, "操作员", true, false, 0, 100, 20);

		map.AddTBString(RptTemplateAttr.D1, null, "D1", false, true, 0, 90, 10);
		map.AddTBString(RptTemplateAttr.D2, null, "D2", false, true, 0, 90, 10);
		map.AddTBString(RptTemplateAttr.D3, null, "D3", false, true, 0, 90, 10);

		map.AddTBString(RptTemplateAttr.AlObjs, null, "要分析的对象", false, true, 0, 90, 10);

		map.AddTBInt(RptTemplateAttr.Height, 600, "Height", false, true);
		map.AddTBInt(RptTemplateAttr.Width, 800, "Width", false, true);

		map.AddBoolean(RptTemplateAttr.IsSumBig, false, "是否显示大合计", false, true);
		map.AddBoolean(RptTemplateAttr.IsSumLittle, false, "是否显示小合计", false, true);
		map.AddBoolean(RptTemplateAttr.IsSumRight, false, "是否显示右合计", false, true);

		map.AddTBInt(RptTemplateAttr.PercentModel, 0, "比率显示方式", false, true);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
