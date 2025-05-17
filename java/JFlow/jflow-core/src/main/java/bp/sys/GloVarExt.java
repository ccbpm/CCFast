package bp.sys;

import bp.en.*;
import bp.en.Map;
import java.math.*;

/** 
 全局变量
*/
public class GloVarExt extends EntityNoName
{

		///#region 属性
	public final Object getValOfObject()  {
		return this.GetValByKey(GloVarAttr.Val);
	}
	public final void setValOfObject(Object value){
		this.SetValByKey(GloVarAttr.Val, value);
	}
	public final String getVal()  {
		return this.GetValStringByKey(GloVarAttr.Val);
	}
	public final void setVal(String value){
		this.SetValByKey(GloVarAttr.Val, value);
	}
	public final float getValOfFloat()
	{
		try
		{
			return this.GetValFloatByKey(GloVarAttr.Val);
		}
		catch (java.lang.Exception e)
		{
			return 0;
		}
	}
	public final void setValOfFloat(float value){
		this.SetValByKey(GloVarAttr.Val, value);
	}
	public final int getValOfInt()
	{
		try
		{
			return this.GetValIntByKey(GloVarAttr.Val);
		}
		catch (RuntimeException ex)
		{
			return 0;
		}
	}
	public final void setValOfInt(int value){
		this.SetValByKey(GloVarAttr.Val, value);
	}
	public final BigDecimal getValOfDecimal()
	{
		try
		{
			return this.GetValDecimalByKey(GloVarAttr.Val,0);
		}
		catch (java.lang.Exception e)
		{
			return new BigDecimal(0);
		}
	}
	public final void setValOfDecimal(BigDecimal value){
		this.SetValByKey(GloVarAttr.Val, value);
	}
	public final boolean getValOfBoolen()  {
		return this.GetValBooleanByKey(GloVarAttr.Val);
	}
	public final void setValOfBoolen(boolean value){
		this.SetValByKey(GloVarAttr.Val, value);
	}
	/** 
	 note
	*/
	public final String getNote()  {
		return this.GetValStringByKey(GloVarAttr.Note);
	}
	public final void setNote(String value){
		this.SetValByKey(GloVarAttr.Note, value);
	}
	/** 
	 分组值
	*/
	public final String getGroupKey()  {
		return this.GetValStringByKey(GloVarAttr.GroupKey);
	}
	public final void setGroupKey(String value){
		this.SetValByKey(GloVarAttr.GroupKey, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 全局变量
	*/
	public GloVarExt()
	{
	}
	/** 
	 全局变量
	 
	 @param no
	*/
	public GloVarExt(String no) throws Exception  {
		this.setNo(no);
		this.Retrieve();
	}
	/** 
	 全局变量s
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_GloVar", "全局变量");


		map.AddTBStringPK(GloVarAttr.No, null, "键", true, false, 1, 50, 20);
		map.AddTBString(GloVarAttr.Name, null, "名称", true, false, 0, 120, 20);
		map.AddTBString(GloVarAttr.Val, null, "值/表达式", true, false, 0, 2000, 20, true);
		map.AddTBString(GloVarAttr.GroupKey, null, "分组值", false, false, 0, 120, 20, false);
		map.AddTBStringDoc(GloVarAttr.Note, null, "备注", true, false, true);
		map.AddTBInt(GloVarAttr.Idx, 0, "顺序号", true, true);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
