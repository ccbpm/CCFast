package bp.sys;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.web.*;

/**
 实体版本号
*/
public class EnVer extends EntityMyPK
{

		///#region 方法.
	/**
	 创建新版本

	 @return
	*/
	public static String NewVer(Entity myen) throws Exception {
		String frmID = myen.toString();
		String frmName = myen.getEnDesc();
		String pkval = myen.getPKVal().toString();
		// 获得最大的版本号.
		int maxVer = DBAccess.RunSQLReturnValInt("SELECT MAX(EnVer) as Num FROM Sys_EnVer WHERE  FrmID='" + frmID + "' AND EnPKValue='" + pkval + "'", 0);
		//最大版本号>1,存在历史版本，获取上一个版本的数据
		String oldStr = "";
		EnVer oldVer = null;
		if (maxVer > 0)
		{
			String mypk = frmID + "_" + pkval + "_" + (maxVer);
			oldVer = new EnVer();
			oldVer.setMyPK(mypk);
			if (oldVer.RetrieveFromDBSources() != 0)
			{
				oldStr = DBAccess.GetBigTextFromDB("Sys_EnVer", "MyPK", mypk, EnVerAttr.DBJSON);
			}
			if (oldStr.equals(myen.ToJson()))
			{
				return "";
			}
		}
		//创建实体.
		EnVer ev = new EnVer();

		ev.setRecNo(WebUser.getNo());
		ev.setRecName(WebUser.getName());
		ev.setRDT(DataType.getCurrentDateTime());
		ev.setFrmID(frmID);
		ev.setEnPKValue(pkval);
		ev.setMyNote("");
		ev.setName(frmName);


		ev.setVer(maxVer + 1); //设置版本号.
		ev.setMyPK(ev.getFrmID() + "_" + ev.getEnPKValue() + "_" + ev.getVer());
		ev.Insert(); //执行插入.
		//存入EnVerDtl表中
		MapAttrs mapAttrs = new MapAttrs(frmID);
		EnVerDtl enVerDtl = null;
		for(MapAttr mapAttr : mapAttrs.ToJavaList())
		{
			if (mapAttr.getUIIsEnable() == false)
				continue;
			enVerDtl = new EnVerDtl();
			enVerDtl.setRefPK(ev.getMyPK());
			enVerDtl.setEnPKValue(pkval);
			enVerDtl.setFrmID(ev.getFrmID());
			enVerDtl.setAttrKey(mapAttr.getKeyOfEn());
			enVerDtl.setAttrName(mapAttr.getName());
			enVerDtl.setBindKey(mapAttr.getUIBindKey());
			enVerDtl.setLGType(mapAttr.getLGType().getValue());
			enVerDtl.setMyVal(myen.GetValByKey(mapAttr.getKeyOfEn())!=null?myen.GetValByKey(mapAttr.getKeyOfEn()).toString():"");
			//enVerDtl.MyPK = ev.MyPK + "_" + mapAttr.KeyOfEn;
			enVerDtl.Insert();

		}
		//存储数据JSON
		DBAccess.SaveBigTextToDB(myen.ToJson(), "Sys_EnVer", "MyPK", ev.getMyPK(), EnVerAttr.DBJSON);

		return "版本创建成功.";
	}

		///#endregion 方法.


		///#region 属性
	public final String getDBJSON()  {
		return this.GetValStrByKey(EnVerAttr.DBJSON);
	}
	public final void setDBJSON(String value){
		this.SetValByKey(EnVerAttr.DBJSON, value);
	}
	/**
	 实体类名称
	*/
	public final String getName()  {
		return this.GetValStrByKey(EnVerAttr.Name);
	}
	public final void setName(String value){
		this.SetValByKey(EnVerAttr.Name, value);
	}
	public final String getEnPKValue()  {
		return this.GetValStrByKey(EnVerAttr.EnPKValue);
	}
	public final void setEnPKValue(String value){
		this.SetValByKey(EnVerAttr.EnPKValue, value);
	}
	/**
	 版本号
	*/
	public final int getVer()  {
		return this.GetValIntByKey(EnVerAttr.EnVer);
	}
	public final void setVer(int value){
		this.SetValByKey(EnVerAttr.EnVer, value);
	}
	/**
	 修改人
	*/
	public final String getRecNo()  {
		return this.GetValStrByKey(EnVerAttr.RecNo);
	}
	public final void setRecNo(String value){
		this.SetValByKey(EnVerAttr.RecNo, value);
	}
	public final String getRecName()  {
		return this.GetValStrByKey(EnVerAttr.RecName);
	}
	public final void setRecName(String value){
		this.SetValByKey(EnVerAttr.RecName, value);
	}
	public final String getMyNote()  {
		return this.GetValStrByKey(EnVerAttr.MyNote);
	}
	public final void setMyNote(String value){
		this.SetValByKey(EnVerAttr.MyNote, value);
	}
	public final String getFrmID()  {
		return this.GetValStrByKey(EnVerAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(EnVerAttr.FrmID, value);
	}
	/**
	 修改日期
	*/
	public final String getRDT()  {
		return this.GetValStrByKey(EnVerAttr.RDT);
	}
	public final void setRDT(String value){
		this.SetValByKey(EnVerAttr.RDT, value);
	}

	/**
	 主键值
	*/
	public final String getPKValue()  {
		return this.GetValStrByKey(EnVerAttr.PKValue);
	}
	public final void setPKValue(String value){
		this.SetValByKey(EnVerAttr.PKValue, value);
	}

		///#endregion


		///#region 构造函数
	/**
	 实体版本号
	*/
	public EnVer()
	{
	}
	public EnVer(String mypk) throws Exception {
		this.setMyPK(mypk);
		this.Retrieve();
	}


		///#endregion


		///#region 重写方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		uac.IsDelete = false;
		uac.IsInsert = false;
		uac.IsUpdate = false;
		return uac;
	}
	/**
	 Map
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_EnVer", "实体版本号");
		map.setEnDBUrl(new DBUrl(DBUrlType.AppCenterDSN)); //连接到的那个数据库上. (默认的是: AppCenterDSN )

		map.AddMyPK();

		map.AddTBString(EnVerAttr.FrmID, null, "实体类", true, true, 1, 50, 20, true);
		map.AddTBString(EnVerAttr.Name, null, "实体名", true, true, 0, 100, 30, true);

		map.AddTBString(EnVerAttr.EnPKValue, null, "主键值", true, true, 0, 40, 100);
		map.AddTBInt(EnVerAttr.EnVer, 0, "版本号", true, true);

		map.AddTBString(EnVerAttr.RecNo, null, "修改人账号", true, true, 0, 100, 30);
		map.AddTBString(EnVerAttr.RecName, null, "修改人名称", true, true, 0, 100, 30);

		//需要存储二进制文件.
		//map.AddTBString(EnVerAttr.DBJSON, null, "数据JSON", true, true, 0, 4000, 30);

		map.AddTBString(EnVerAttr.MyNote, null, "备注", true, true, 0, 100, 30);

		map.AddTBDateTime(EnVerAttr.RDT, null, "创建日期", true, true);

		RefMethod rm = new RefMethod();
		rm.Title = "快照";
		rm.ClassMethodName = this.toString() + ".ShowVer";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		//数据.
		map.AddDtl(new EnVerDtls(), EnVerAttr.RefPK, null, DtlEditerModel.DtlSearch, null);

		map.DTSearchKey = "RDT";
		map.DTSearchWay = DTSearchWay.ByDateTime;
		map.DTSearchLabel = "日期";

		this.set_enMap(map);

		return this.get_enMap();
	}

		///#endregion

	public final String ShowVer() {
		return "/WF/CCBill/OptComponents/DataVer.htm?FrmID=" + this.getFrmID() + "&WorkID=" + this.getEnPKValue();
	}

	@Override
	protected boolean beforeInsert() throws Exception
	{
		return super.beforeInsert();
	}
	@Override
	protected void afterDelete() throws Exception
	{
		//删除数据.
		EnVerDtls dtls = new EnVerDtls();
		dtls.Delete(EnVerDtlAttr.RefPK, this.getMyPK());
		super.afterDelete();
	}
}
