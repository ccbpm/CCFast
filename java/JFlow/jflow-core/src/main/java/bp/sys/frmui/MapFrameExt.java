package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;

/** 
 框架
*/
public class MapFrameExt extends EntityMyPK
{

		///#region 属性
	/** 
	 连接
	*/

	public final String getFrmID()  {
		return this.GetValStrByKey(MapFrameAttr.FK_MapData);
	}
	public final String getName()  {
		return this.GetValStrByKey(MapFrameAttr.Name);
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
		//if (bp.web.WebUser.getNo().equals("admin"))
		//{
		uac.IsUpdate = true;
		uac.IsDelete = true;
		uac.IsInsert = false;
		//}
		return uac;
	}
	/** 
	 框架
	*/
	public MapFrameExt()
	{

	}
	/** 
	 框架
	 
	 @param mypk
	*/
	public MapFrameExt(String mypk) throws Exception {
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

		map.AddDDLSysEnum(MapFrameAttr.UrlSrcType, 0, "URL来源", true, true, MapFrameAttr.UrlSrcType, "@0=自定义@1=地图@2=流程轨迹表@3=流程轨迹图");
		map.AddTBString(MapFrameAttr.FrameURL, null, "URL", true, false, 0, 3000, 20, true);

		map.AddTBString(MapFrameAttr.URL, null, "URL", false, false, 0, 3000, 20, true);
		//显示的分组.
		// map.AddDDLSQL(MapFrameAttr.FrmID, "0", "表单表单","SELECT No, Name FROM Sys_Mapdata  WHERE  FrmType=3 ", true);



		map.AddTBString(MapFrameAttr.W, null, "宽度", true, false, 0, 20, 20);
		map.AddTBString(MapFrameAttr.H, null, "高度", true, false, 0, 20, 20);

		map.AddBoolean(MapFrameAttr.IsAutoSize, true, "是否自动设置大小", false, false);

		map.AddTBString(MapFrameAttr.EleType, null, "类型", false, false, 0, 50, 20, true);

		map.AddTBString(MapFrameAttr.GUID, null, "GUID", false, false, 0, 128, 20);

		map.AddTBInt(MapAttrAttr.Idx, 0, "顺序号", true, false); //@李国文.


			///#region 执行的方法.
		RefMethod rm = new RefMethod();

		rm = new RefMethod();
		rm.Title = "预制";
		rm.ClassMethodName = this.toString() + ".DoFrameExt()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

			///#endregion 执行的方法.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 框架扩展.
	/** 
	 框架扩展
	 
	 @return 
	*/
	public final String DoFrameExt() {
		return "../../Admin/FoolFormDesigner/FrameExt/Default.htm?MyPK=" + this.getMyPK();
	}

		///#endregion 框架扩展.


	@Override
	protected void afterDelete() throws Exception
	{
		//删除分组信息.
		GroupField gf = new GroupField();
		gf.Delete(GroupFieldAttr.CtrlID, this.getMyPK());

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());
		super.afterDelete();
	}

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		//在属性实体集合插入前，clear父实体的缓存.
		bp.sys.base.Glo.ClearMapDataAutoNum(this.getFrmID());

		int val = this.GetValIntByKey(MapFrameAttr.UrlSrcType, 0);
		if (val == 1)
		{
			String sql = "SELECT Url FROM Sys_MapData WHERE No='" + this.getFrmID() + "'";
			String url = DBAccess.RunSQLReturnStringIsNull(sql, "");
			this.SetValByKey(MapFrameAttr.FrameURL, url);
			this.SetValByKey(MapFrameAttr.URL, url);
		}
		else
		{
			this.SetValByKey(MapFrameAttr.URL, this.GetValByKey(MapFrameAttr.FrameURL));
		}

		//更新group.
		GroupField gf = new GroupField();
		int i = gf.Retrieve(GroupFieldAttr.FrmID, this.getFrmID(), GroupFieldAttr.CtrlID, this.getMyPK());
		if (i == 1)
		{
			gf.setLab(this.getName());
			gf.Update();
		}

		return super.beforeUpdateInsertAction();
	}

	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		MapFrame mapframe = new MapFrame();
		mapframe.setMyPK(this.getMyPK());
		mapframe.RetrieveFromDBSources();
		mapframe.Update();

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());

		super.afterInsertUpdateAction();
	}
}
