package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;

/** 
 框架
*/
public class MapFrame extends EntityMyPK
{

		///#region 属性
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getNo().equals("admin") == true)
		{
			uac.IsDelete = true;
			uac.IsUpdate = true;
		}
		return uac;
	}
	/** 
	 是否自适应大小
	*/
	public final boolean getItIsAutoSize()  {
		return this.GetValBooleanByKey(MapFrameAttr.IsAutoSize);
	}
	public final void setItIsAutoSize(boolean value){
		this.SetValByKey(MapFrameAttr.IsAutoSize, value);
	}
	/** 
	 EleType
	*/
	public final String getEleType()  {
		return this.GetValStrByKey(MapFrameAttr.EleType);
	}
	public final void setEleType(String val){
		this.SetValByKey(MapFrameAttr.EleType, val);
	}
	/** 
	 连接
	*/
	public final String getURL() throws Exception {
		String s = this.GetValStrByKey(MapFrameAttr.FrameURL);
		if (DataType.IsNullOrEmpty(s))
		{
			return "http://ccbpm.cn";
		}
		return s;
	}
	public final void setURL(String value){
		this.SetValByKey(MapFrameAttr.FrameURL, value);
	}
	/** 
	 高度
	*/
	public final float getH()  {
		return this.GetValFloatByKey(MapFrameAttr.H);
	}
	public final void setH(float value){
		this.SetValByKey(MapFrameAttr.H, value);
	}
	/** 
	 宽度
	*/
	public final float getW()  {
		return this.GetValFloatByKey(MapFrameAttr.W);
	}
	public final void setW(float value){
		this.SetValByKey(MapFrameAttr.W, value);
	}
	public boolean ItIsUse = false;
	public final String getFrmID()  {
		return this.GetValStrByKey(MapFrameAttr.FK_MapData);
	}
	public final void setFrmID(String value){
		this.SetValByKey(MapFrameAttr.FK_MapData, value);
	}
	public final String getName()  {
		return this.GetValStrByKey(MapFrameAttr.Name);
	}
	public final void setName(String value){
		this.SetValByKey(MapFrameAttr.Name, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 框架
	*/
	public MapFrame()
	{
	}
	/** 
	 框架
	 
	 @param mypk
	*/
	public MapFrame(String mypk) throws Exception {
		this.setMyPK(mypk);
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
		Map map = new Map("Sys_MapFrame", "框架");

		map.AddMyPK();
		map.AddTBString(MapFrameAttr.FK_MapData, null, "表单ID", true, true, 0, 100, 20);
		map.AddTBString(MapFrameAttr.Name, null, "名称", true, false, 0, 200, 20, true);

		//@0=自定义@1=地图@2=流程轨迹表@3=流程轨迹图.
		map.AddTBInt(MapFrameAttr.UrlSrcType, 0, "URL来源", false, false);

		map.AddTBString(MapFrameAttr.FrameURL, null, "FrameURL", true, false, 0, 3000, 20, true);
		map.AddTBString(MapFrameAttr.URL, null, "URL", false, false, 0, 3000, 20, true);

		//map.AddDDLSysEnum(MapFrameAttr.UrlSrcType, 0, "URL来源", true, true, MapFrameAttr.UrlSrcType,
		//"@0=自定义@1=地图@2=流程轨迹表@3=流程轨迹图");

		map.AddTBString(MapFrameAttr.W, null, "宽度", true, false, 0, 20, 20);
		map.AddTBString(MapFrameAttr.H, null, "高度", true, false, 0, 20, 20);

		map.AddBoolean(MapFrameAttr.IsAutoSize, true, "是否自动设置大小", false, false);

		map.AddTBString(MapFrameAttr.EleType, null, "类型", false, false, 0, 50, 20, true);

	   // map.AddTBInt(MapFrameAttr.RowIdx, 99, "位置", false, false);
	   // map.AddTBInt(MapFrameAttr.GroupID, 0, "GroupID", false, false);

		map.AddTBString(MapFrameAttr.GUID, null, "GUID", false, false, 0, 128, 20);

		this.set_enMap(map);
		return this.get_enMap();
	}

	/** 
	 插入之后增加一个分组.
	*/
	@Override
	protected void afterInsert() throws Exception
	{
		GroupField gf = new GroupField();
		gf.setFrmID(this.getFrmID());
		gf.setCtrlID(this.getMyPK());
		gf.setCtrlType("Frame");
		gf.setLab(this.getName());
		gf.setIdx(0);
		gf.Insert(); //插入.


		super.afterInsert();
	}
	/** 
	 删除之后的操作
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		GroupField gf = new GroupField();
		gf.Delete(GroupFieldAttr.CtrlID, this.getMyPK());

		super.afterDelete();
	}
	@Override
	protected boolean beforeInsert() throws Exception
	{
		//在属性实体集合插入前，clear父实体的缓存.
		bp.sys.base.Glo.ClearMapDataAutoNum(this.getFrmID());

		return super.beforeInsert();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		GroupField gf = new GroupField();
		gf.Retrieve(GroupFieldAttr.CtrlID, this.getMyPK());
		gf.setLab(this.getName());
		gf.Update();

		return super.beforeUpdate();
	}

		///#endregion
}
