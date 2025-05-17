package bp.ccfast.portal;

import bp.da.*;
import bp.en.*;
import bp.en.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** 
 信息块
*/
public class WindowTemplate extends EntityNoName
{

		///#region 权限控制.
	/** 
	 控制权限
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getIsAdmin() == true)
		{
			uac.OpenAll();
		}
		else
		{
			uac.IsView = false;
		}
		return uac;
	}

		///#endregion 权限控制.


		///#region 属性
	/** 
	 窗口模式
	*/
	public final String getWinDocModel() {
		return
				this.GetValStringByKey(WindowTemplateAttr.WinDocModel);
	}
	public final void setWinDocModel(String value)  {
		this.SetValByKey(WindowTemplateAttr.WinDocModel, value);
	}

	/** 
	 更多的URL
	*/
	public final String getMoreUrl()  {
		return this.GetValStrByKey(WindowTemplateAttr.MoreUrl);
	}
	public final void setMoreUrl(String value)  {
		this.SetValByKey(WindowTemplateAttr.MoreUrl, value);
	}
	/** 
	 更多标签
	*/
	public final String getMoreLab()  {
		return this.GetValStrByKey(WindowTemplateAttr.MoreLab);
	}
	public final void setMoreLab(String value)  {
		this.SetValByKey(WindowTemplateAttr.MoreLab, value);
	}
	public final int getPopW() {
		return this.GetValIntByKey(WindowTemplateAttr.PopW);
	}
	public final void setPopW(int value)  {
		this.SetValByKey(WindowTemplateAttr.PopW, value);
	}
	public final int getPopH() {
		return this.GetValIntByKey(WindowTemplateAttr.PopH);
	}
	public final void setPopH(int value)  {
		this.SetValByKey(WindowTemplateAttr.PopH, value);
	}
	public final int getColSpan() {
		return this.GetValIntByKey(WindowTemplateAttr.ColSpan);
	}
	public final void setColSpan(int value)  {
		this.SetValByKey(WindowTemplateAttr.ColSpan, value);
	}
	public final int getMoreLinkModel() {
		return this.GetValIntByKey(WindowTemplateAttr.MoreLinkModel);
	}
	public final void setMoreLinkModel(int value)  {
		this.SetValByKey(WindowTemplateAttr.MoreLinkModel, value);
	}
	/** 
	 标题
	*/

	/** 
	 用户是否可以删除
	*/
	public final boolean getItIsDel() {
		return this.GetValBooleanByKey(WindowTemplateAttr.IsDel);
	}
	public final void setItIsDel(boolean value)  {
		this.SetValByKey(WindowTemplateAttr.IsDel, value);
	}
	/** 
	 是否禁用?
	*/
	public final boolean getItIsEnable() {
		return this.GetValBooleanByKey(WindowTemplateAttr.IsEnable);
	}
	public final void setItIsEnable(boolean value)  {
		this.SetValByKey(WindowTemplateAttr.IsEnable, value);
	}

	/** 
	 打开方式
	*/
	public final int getOpenWay() {
		return this.GetValIntByKey(WindowTemplateAttr.OpenWay);
	}
	public final void setOpenWay(int value)  {
		this.SetValByKey(WindowTemplateAttr.OpenWay, value);
	}
	/** 
	 顺序号
	*/
	public final int getIdx() {
		return this.GetValIntByKey(WindowTemplateAttr.Idx);
	}
	public final void setIdx(int value)  {
		this.SetValByKey(WindowTemplateAttr.Idx, value);
	}
	public final String getDocGenerRDT()  {
		return this.GetValStrByKey(WindowTemplateAttr.DocGenerRDT);
	}
	public final void setDocGenerRDT(String value)  {
		this.SetValByKey(WindowTemplateAttr.DocGenerRDT, value);
	}
	public final String getPageID()  {
		return this.GetValStrByKey(WindowTemplateAttr.PageID);
	}
	public final void setPageID(String value)  {
		this.SetValByKey(WindowTemplateAttr.PageID, value);
	}
	public final String getDocs()  {
		return this.GetValStrByKey(WindowTemplateAttr.Docs);
	}
	public final void setDocs(String value)  {
		this.SetValByKey(WindowTemplateAttr.Docs, value);
	}
	/** 
	 获取参数化的字符串
	 
	 @param stringInput
	 @param dr
	 @return 
	*/
	private String GetParameteredString(String stringInput, DataRow dr)
	{
		String regE = "@[a-zA-Z]([\\w-]*[a-zA-Z0-9])?"; //字母开始，字母+数字结尾，字母+数字+下划线+中划线中间
		//String regE = "@[\\w-]+";                               //字母+数字+下划线+中划线
		//MatchCollection mc = Regex.Matches(stringInput, regE, RegexOptions.IgnoreCase);
		Pattern pattern = Pattern.compile(regE);
		Matcher matcher = pattern.matcher(stringInput);
		while (matcher.find())
		{
			String v = matcher.group();
			String f = matcher.group().substring(1);
			stringInput = stringInput.replace(v, String.format("%1$s", dr.getValue(f).toString()));
		}

		return stringInput;
	}

		///#endregion


		///#region 构造方法
	/** 
	 信息块
	*/
	public WindowTemplate()
	{
	}
	/** 
	 信息块
	 
	 @param no
	*/
	public WindowTemplate(String no) throws Exception
	{
		this.setNo(no);
		this.Retrieve();
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

		Map map = new Map("GPM_WindowTemplate", "信息块");
		map.setEnType(EnType.Sys);


			///#region 基本信息.
		map.AddTBStringPK(WindowTemplateAttr.No, null, "编号", true, true, 1, 40, 100);
		map.AddTBString(WindowTemplateAttr.Name, null, "标题", true, false, 0, 300, 20, true);

		map.AddTBInt(WindowTemplateAttr.ColSpan, 1, "占的列数", true, false);
		map.SetHelperAlert(WindowTemplateAttr.ColSpan, "画布按照4列划分布局，输入的输在在1=4之间.");

		map.AddTBString(WindowTemplateAttr.WinDocModel, null, "内容类型", true, false, 0, 300, 20, true);
		map.AddTBString(WindowTemplateAttr.Icon, null, "Icon", true, false, 0, 100, 20, true);
		map.AddTBString(WindowTemplateAttr.PageID, null, "页面ID", true, true, 0, 40, 20, false);

			///#endregion 基本信息.

		// map.AddDDLSysEnum(WindowTemplateAttr.ColSpan, 1, "占的列数", true, true, WindowTemplateAttr.ColSpan,
		//  "@1=1列@2=2列@3=覆盖新窗口");


			///#region 更多的信息定义.
		map.AddTBString(WindowTemplateAttr.MoreLab, null, "更多标签", false, false, 0, 300, 20, true);
		map.AddTBString(WindowTemplateAttr.MoreUrl, null, "更多链接", false, false, 0, 300, 20, true);
		map.AddDDLSysEnum(WindowTemplateAttr.MoreLinkModel, 0, "打开方式", false, false, WindowTemplateAttr.MoreLinkModel, "@0=新窗口@1=本窗口@2=覆盖新窗口");
		map.AddTBInt(WindowTemplateAttr.PopW, 500, "Pop宽度", false, true);
		map.AddTBInt(WindowTemplateAttr.PopH, 400, "Pop高度", false, true);

			///#endregion 更多的信息定义.


			///#region 内容定义.
		map.AddTBString(WindowTemplateAttr.MoreUrl, null, "更多链接", false, false, 0, 300, 20, true);
		map.AddTBStringDoc(WindowTemplateAttr.Docs, null, "内容表达式", true, false);

			///#endregion 内容定义.


			///#region 权限定义.
		// 0=Html , 1=SQL列表
		//  map.AddTBInt(WindowTemplateAttr.WinDocModel, 0, "内容类型", false, true);
		// map.AddTBString(WindowTemplateAttr.Docs, null, "内容", true, false, 0, 4000, 20);

			///#endregion 权限定义.


			///#region 其他
		map.AddTBInt(WindowTemplateAttr.Idx, 0, "默认的排序", false, false);
		map.AddBoolean(WindowTemplateAttr.IsDel, true, "用户是否可删除", false, false);
		map.AddBoolean(WindowTemplateAttr.IsEnable, false, "是否禁用?", false, false);
		map.AddTBString(WindowTemplateAttr.OrgNo, null, "OrgNo", false, false, 0, 50, 20);

			///#endregion 其他


			///#region 扇形图

		map.AddTBString(WindowTemplateAttr.LabOfFZ, null, "分子标签", true, false, 0, 100, 20);
		map.AddTBStringDoc(WindowTemplateAttr.SQLOfFZ, null, "分子表达式", true, false, true, 10);

		map.AddTBString(WindowTemplateAttr.LabOfFM, null, "分母标签", true, false, 0, 100, 20);
		map.AddTBStringDoc(WindowTemplateAttr.SQLOfFM, null, "分子表达式", true, false, true, 10);
		map.AddTBString(WindowTemplateAttr.LabOfRate, null, "率标签", true, false, 0, 100, 20);

			///#endregion 扇形图



			///#region 多图形展示.

		map.AddBoolean("IsPie", false, "饼图?", true, true);
		map.AddBoolean("IsLine", false, "折线图?", true, true);
		map.AddBoolean("IsZZT", false, "柱状图?", true, true);
		map.AddBoolean("IsRing", false, "显示环形图?", true, true);
		map.AddBoolean("IsRate", false, "百分比扇形图?", true, true);

		map.AddDDLSysEnum(WindowTemplateAttr.DefaultChart, 0, "默认显示图形", true, true, WindowTemplateAttr.DefaultChart, "@0=饼图@1=折线图@2=柱状图@3=显示环形图");


			///#endregion 多图形展示.



		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setNo(DBAccess.GenerGUID(0, null, null));
		if (DataType.IsNullOrEmpty(this.getPageID()) == true)
		{
			this.setPageID("Home");
		}
		return super.beforeInsert();
	}

	@Override
	protected void afterDelete() throws Exception
	{
		//删除它的实例.
		Windows ens = new Windows();
		ens.Delete(WindowAttr.WindowTemplateNo, this.getNo());

		super.afterDelete();
	}
}
