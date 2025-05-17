package bp.sys;

import bp.en.*;
import bp.en.Map;

/** 
 单选框
*/
public class FrmRB extends EntityMyPK
{

		///#region 属性
	/** 
	 提示
	*/
	public final String getTip()  {
		return this.GetValStringByKey(FrmRBAttr.Tip);
	}
	public final void setTip(String value){
		this.SetValByKey(FrmRBAttr.Tip, value);
	}

	/** 
	 字段设置值
	*/
	public final String getSetVal()  {
		return this.GetValStringByKey(FrmRBAttr.SetVal);
	}
	public final void setVal(String value){
		this.SetValByKey(FrmRBAttr.SetVal, value);
	}
	/** 
	 要执行的脚本
	*/
	public final String getScript()  {
		return this.GetValStringByKey(FrmRBAttr.Script);
	}
	public final void setScript(String value){
		this.SetValByKey(FrmRBAttr.Script, value);
	}

	/** 
	 字段-配置信息
	*/
	public final String getFieldsCfg()  {
		return this.GetValStringByKey(FrmRBAttr.FieldsCfg);
	}
	public final void setFieldsCfg(String value){
		this.SetValByKey(FrmRBAttr.FieldsCfg, value);
	}
	public final String getLab()  {
		return this.GetValStringByKey(FrmRBAttr.Lab);
	}

	public final void setLab(String val){
		this.SetValByKey(FrmRBAttr.Lab, val);
	}
	public final String getKeyOfEn()  {
		return this.GetValStringByKey(FrmRBAttr.KeyOfEn);
	}
	public final void setKeyOfEn(String value){
		this.SetValByKey(FrmRBAttr.KeyOfEn, value);
	}
	public final String getIntKey()  {
		return this.GetValStringByKey(FrmRBAttr.IntKey);
	}
	public final void setIntKey(String val){
		this.SetValByKey(FrmRBAttr.IntKey, val);
	}

	public final String getFrmID()  {
		return this.GetValStrByKey(FrmRBAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(FrmRBAttr.FrmID, value);
	}

	public final String getEnumKey()  {
		return this.GetValStrByKey(FrmRBAttr.EnumKey);
	}
	public final void setEnumKey(String val){
		this.SetValByKey(FrmRBAttr.EnumKey, val);
	}
	public final int getFontSize()
	{
		return this.GetParaInt(FrmRBAttr.FontSize, 12);
	}
	public final void setFontSize(int value)
	{
		this.SetPara(FrmRBAttr.FontSize, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 单选框
	*/
	public FrmRB()
	{
	}
	public FrmRB(String mypk) throws Exception {
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
		Map map = new Map("Sys_FrmRB", "单选框");
		map.IndexField = FrmRBAttr.FrmID;

		map.AddMyPK();
		map.AddTBString(FrmRBAttr.FrmID, "", "表单ID", true, false, 0, 200, 20);
		map.AddTBString(FrmRBAttr.KeyOfEn, "", "字段", true, false, 0, 200, 20);
		map.AddTBString(FrmRBAttr.EnumKey, "", "枚举值", true, false, 0, 30, 20);
		map.AddTBString(FrmRBAttr.Lab, "", "标签", true, false, 0, 500, 20);
		map.AddTBString(FrmRBAttr.IntKey, "0", "IntKey", true, false, 0, 500, 20);
		map.AddTBInt(MapAttrAttr.UIIsEnable, 0, "是否启用", true, false);

		//要执行的脚本.
		map.AddTBString(FrmRBAttr.Script, "", "要执行的脚本", true, false, 0, 4000, 20);
		map.AddTBString(FrmRBAttr.FieldsCfg, "", "配置信息@FieldName=Sta", true, false, 0, 4000, 20);
		map.AddTBString(FrmRBAttr.SetVal, "", "设置的值", true, false, 0, 200, 20);
		map.AddTBString(FrmRBAttr.Tip, "", "选择后提示的信息", true, false, 0, 1000, 20);

		map.AddTBAtParas(500);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(this.getFrmID() + "_" + this.getKeyOfEn() + "_" + this.getIntKey());
		return super.beforeInsert();
	}

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		this.setMyPK(this.getFrmID() + "_" + this.getKeyOfEn() + "_" + this.getIntKey());
		return super.beforeUpdateInsertAction();
	}
}
