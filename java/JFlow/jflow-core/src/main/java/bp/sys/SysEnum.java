package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import bp.difference.*;
import java.util.*;

/** 
 SysEnum
*/
public class SysEnum extends EntityMyPK
{
	/** 
	 得到一个String By LabKey.
	 
	 @param EnumKey
	 @param intKey
	 @return 
	*/
	public static String GetLabByPK(String EnumKey, int intKey) throws Exception {
		SysEnum en = new SysEnum(EnumKey, intKey);
		return en.getLab();
	}


		///#region 实现基本的方法
	public final String getOrgNo()  {
		return this.GetValStringByKey(SysEnumAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(SysEnumAttr.OrgNo, value);
	}

	/** 
	 标签
	*/
	public final String getLab()  {
		return this.GetValStringByKey(SysEnumAttr.Lab);
	}
	public final void setLab(String value){
		this.SetValByKey(SysEnumAttr.Lab, value);
	}
	/** 
	 标签
	*/
	public final String getLang()  {
		return this.GetValStringByKey(SysEnumAttr.Lang);
	}
	public final void setLang(String value){
		this.SetValByKey(SysEnumAttr.Lang, value);
	}
	/** 
	 Int val
	*/
	public final int getIntKey()  {
		return this.GetValIntByKey(SysEnumAttr.IntKey);
	}
	public final void setIntKey(int value){
		this.SetValByKey(SysEnumAttr.IntKey, value);
	}
	/** 
	 EnumKey
	*/
	public final String getEnumKey()  {
		return this.GetValStringByKey(SysEnumAttr.EnumKey);
	}
	public final void setEnumKey(String value){
		this.SetValByKey(SysEnumAttr.EnumKey, value);
	}
	/** 
	 StrKey
	*/
	public final String getStrKey()  {
		return this.GetValStringByKey(SysEnumAttr.StrKey);
	}
	public final void setStrKey(String value){
		this.SetValByKey(SysEnumAttr.StrKey, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 SysEnum
	*/
	public SysEnum()
	{
	}
	/** 
	 税务编号
	 
	 @param enumKey 编号
	*/
	public SysEnum(String enumKey, int val) throws Exception {
		this.setEnumKey(enumKey);
		this.setLang(bp.web.WebUser.getSysLang());
		this.setIntKey(val);
		this.setMyPK(this.getEnumKey() + "_" + this.getLang() + "_" + this.getIntKey());
		int i = this.RetrieveFromDBSources();
		if (i == 0)
		{
			i = this.Retrieve(SysEnumAttr.EnumKey, enumKey, SysEnumAttr.Lang, bp.web.WebUser.getSysLang(), SysEnumAttr.IntKey, this.getIntKey());
			SysEnums ses = new SysEnums();
			ses.Full(enumKey);
			if (i == 0)
			{
				//尝试注册系统的枚举的配置.
				bp.sys.SysEnums myee = new SysEnums(enumKey);
				throw new RuntimeException("@ EnumKey=" + getEnumKey() + " Val=" + val + " Lang=" + bp.web.WebUser.getSysLang() + " ...Error");
			}
		}
	}
	public SysEnum(String enumKey, String Lang, int val) throws Exception {
		this.setEnumKey(enumKey);
		this.setLang(Lang);
		this.setIntKey(val);
		this.setMyPK(this.getEnumKey() + "_" + this.getLang() + "_" + this.getIntKey());
		int i = this.RetrieveFromDBSources();
		if (i == 0)
		{
			i = this.Retrieve(SysEnumAttr.EnumKey, enumKey, SysEnumAttr.Lang, Lang, SysEnumAttr.IntKey, this.getIntKey());

			SysEnums ses = new SysEnums();
			ses.Full(enumKey);

			if (i == 0)
			{
				throw new RuntimeException("@ EnumKey=" + enumKey + " Val=" + val + " Lang=" + Lang + " Error");
			}
		}
	}
	/** 
	 Map
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map(bp.sys.base.Glo.SysEnum(), "枚举数据");

		/*
		* 为了能够支持 cloud 我们做了如下变更.
		* 1. 增加了OrgNo 字段.
		* 2. 如果是单机版用户,原来的业务逻辑不变化. MyPK= EnumKey+"_"+IntKey+'_'+Lang 
		* 3. 如果是SAAS模式, MyPK= EnumKey+"_"+IntKey+'_'+Lang +"_"+OrgNo 
		*/

		map.AddMyPK();
		map.AddTBString(SysEnumAttr.Lab, null, "标签", true, false, 1, 300, 8);

		//不管是那个模式  就是短号. 
		map.AddTBString(SysEnumAttr.EnumKey, null, "EnumKey", true, false, 1, 100, 8);
		map.AddTBInt(SysEnumAttr.IntKey, 0, "Val", true, false);
		map.AddTBString(SysEnumAttr.Lang, "CH", "语言", true, false, 0, 10, 8);

		map.AddTBString(SysEnumMainAttr.OrgNo, null, "OrgNo", true, false, 0, 50, 8);
		map.AddTBString(SysEnumAttr.StrKey, null, "StrKey", true, false, 1, 100, 8);
		//新增属性
		map.AddTBString(SysEnumAttr.RefPK, null, "RefPK", true, false, 0, 100, 8);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	public final void ResetPK() throws Exception {
		if (this.getLang() == null && Objects.equals(this.getLang(), ""))
		{
			this.setLang(bp.web.WebUser.getSysLang());
		}

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS || SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			this.setMyPK(this.getEnumKey() + "_" + this.getLang() + "_" + this.getIntKey() + "_" + bp.web.WebUser.getOrgNo()); //关联的主键.
		}

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			this.setMyPK(this.getEnumKey() + "_" + this.getLang() + "_" + this.getIntKey());
		}
	}

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		ResetPK();

		return super.beforeUpdateInsertAction();
	}

	/** 
	 枚举类型新增保存后在Frm_RB中增加新的枚举值
	*/
	@Override
	protected void afterInsert() throws Exception
	{
		//获取引用枚举的表单
		String sql = " select  distinct(FK_MapData)from Sys_FrmRB where EnumKey='" + this.getEnumKey() + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			super.afterInsert();
			return;
		}

		for (DataRow dr : dt.Rows)
		{
			if (DataType.IsNullOrEmpty(dr.getValue(0).toString()))
			{
				continue;
			}
			String fk_mapdata = dr.getValue(0).toString();
			String mypk = fk_mapdata + "_" + this.getEnumKey() + "_" + this.getIntKey();
			FrmRB frmrb = new FrmRB();
			if (frmrb.IsExit("MyPK", mypk) == true)
			{
				frmrb.setLab(this.getLab());
				frmrb.Update();
				continue;
			}
			//获取mapAttr 
			MapAttr mapAttr = new MapAttr();
			mapAttr.setMyPK(fk_mapdata + "_" + this.getEnumKey());
			int i = mapAttr.RetrieveFromDBSources();
			if (i != 0)
			{

				int RBShowModel = mapAttr.GetParaInt("RBShowModel");
				FrmRB frmrb1 = new FrmRB();
				frmrb1.setMyPK(fk_mapdata + "_" + this.getEnumKey() + "_" + this.getIntKey());

				frmrb.setFrmID(fk_mapdata);
				frmrb.setKeyOfEn(this.getEnumKey());
				frmrb.setEnumKey(this.getEnumKey());
				frmrb.setLab(this.getLab());
				if(DataType.IsNullOrEmpty(this.getStrKey())==false)
					frmrb.setIntKey(this.getStrKey());
				else
					frmrb.setIntKey(String.valueOf(this.getIntKey()));
				frmrb.Insert();
			}

		}

		super.afterInsert();
	}
}
