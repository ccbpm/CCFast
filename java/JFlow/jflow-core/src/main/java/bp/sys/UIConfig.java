package bp.sys;

import bp.da.*;
import bp.en.*;

/** 
 UI的设置.Search. Card, Group信息.
*/
public class UIConfig
{
	public Entity HisEn;
	public AtPara HisAP;
	public UIConfig()
	{
	}
	/** 
	 UI的设置.Search. Card, Group信息.
	 
	 @param en
	*/
	public UIConfig(Entity en) throws Exception {
		this.HisEn = en;
		EnCfg cfg = new EnCfg(en.toString());
		String paraStr = cfg.getUI();
		if (DataType.IsNullOrEmpty(paraStr) == true)
		{
			paraStr = "@UIRowStyleGlo=0@IsEnableDouclickGlo=1@IsEnableRefFunc=1@IsEnableFocusField=1@IsEnableOpenICON=1@FocusField=''@WinCardH=600@@WinCardW=800@ShowColumns=";
		}
		HisAP = new AtPara(paraStr);
	}
	/** 
	 获取显示列数组，中间用,隔开
	*/
	public final String[] getShowColumns() {
		String colstr = this.HisAP.GetValStrByKey("ShowColumns");

		if (DataType.IsNullOrEmpty(colstr))
		{
			return new String[0];
		}

		return colstr.split("[,]+");
	}



	///#region 移动.
	/** 
	 移动到方式.
	*/
	public final MoveToShowWay getMoveToShowWay()
	{
		return MoveToShowWay.forValue(this.HisAP.GetValIntByKey("MoveToShowWay"));
	}


	/** 
	 移动到字段
	*/
	public final String getMoveTo()
	{
		String s = this.HisAP.GetValStrByKey("MoveTo");
		return s;
	}

		///#endregion 移动.

	/** 
	 风格类型
	*/
	public final int getUIRowStyleGlo()
	{
		return this.HisAP.GetValIntByKey("UIRowStyleGlo");
	}
	/** 
	 是否启用双击打开？
	*/
	public final boolean getItIsEnableDouclickGlo()
	{
		return this.HisAP.GetValBoolenByKey("IsEnableDouclickGlo");
	}
	/** 
	 是否显示相关功能?
	*/
	public final boolean getItIsEnableRefFunc()
	{
		return this.HisAP.GetValBoolenByKey("IsEnableRefFunc");
	}
	/** 
	 是否启用焦点字段
	*/
	public final boolean getItIsEnableFocusField()
	{
		return this.HisAP.GetValBoolenByKey("IsEnableFocusField");
	}
	/** 
	 是否打开ICON
	*/
	public final boolean getItIsEnableOpenICON()
	{
		return this.HisAP.GetValBoolenByKey("IsEnableOpenICON");
	}
	/** 
	 焦点字段
	*/
	public final String getFocusField() throws Exception {
		String s = this.HisAP.GetValStrByKey("FocusField");
		if (DataType.IsNullOrEmpty(s))
		{
			if (this.HisEn.getEnMap().getAttrs().contains("Name"))
			{
				return "Name";
			}
			if (this.HisEn.getEnMap().getAttrs().contains("Title"))
			{
				return "Title";
			}
		}
		return s;
	}
	public final int getWinCardW()
	{
		return this.HisAP.GetValIntByKey("WinCardW");
	}
	public final int getWinCardH()
	{
		return this.HisAP.GetValIntByKey("WinCardH");
	}
	/** 
	 保存
	 
	 @return 
	*/
	public final int Save() throws Exception {
		EnCfg cfg = new EnCfg(this.HisEn.toString());
		cfg.setUI(this.HisAP.GenerAtParaStrs());
		return cfg.Save();
	}
}
