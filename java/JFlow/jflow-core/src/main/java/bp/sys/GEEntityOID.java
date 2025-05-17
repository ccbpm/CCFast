package bp.sys;

import bp.da.DBAccess;
import bp.en.Entities;
import bp.en.Entity;
import bp.en.Map;
import bp.en.SqlBuilder;

import java.util.ArrayList;
import java.util.Objects;

/** 
 通用OID实体
*/
public class GEEntityOID extends Entity
{

	public final long getOID()  {
		return (long) this.GetValInt64ByKey("OID");
	}
	public final void setOID(long value)  {
		this.SetValByKey("OID", value);
	}
	/// <summary>
	/// 主键值
	/// </summary>
	public final String getPK()  {
		return "OID";
	}
	/// <summary>
	///  主键字段
	/// </summary>
	public final String getPKField()  {
		return "OID";
	}

	/**
	 转化为类.
	 @return
	 */
	@Override
	public String toString()
	{
		return this.FrmID;
	}
	@Override
	public String getClassID()
	{
		return this.FrmID;
	}
	/**
	 主键
	*/
	public String FrmID = null;
	/**
	 通用OID实体
	*/
	public GEEntityOID()
	{
	}
	/**
	 通用OID实体

	 @param fk_mapdata 节点ID
	*/
	public GEEntityOID(String fk_mapdata)
	{
		this.FrmID =fk_mapdata;
		this.set_enMap(null);
	}
	/**


	 @param frmID
	 @param pk
	*/
	public GEEntityOID(String frmID, Object pk) throws Exception {
		this.FrmID =frmID;
		this.setPKVal(pk);
		this.set_enMap(null);
		this.Retrieve();
	}

		///#endregion


		///#region 构造映射.
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		if (this.FrmID == null)
		{
			throw new RuntimeException("没有给[" + this.FrmID + "]值，您不能获取它的Map。");
		}
		try{
			this.set_enMap(MapData.GenerHisMap(this.FrmID));
		}catch(Exception ex){

		}

		return this.get_enMap();
	}
	/** 
	 GEEntitys
	*/
	@Override
	public Entities GetNewEntities()
	{
		if (this.FrmID == null)
		{
			return new GEEntityOIDs();
		}
		return new GEEntityOIDs(this.FrmID);
	}

	/// <summary>
	/// 从另外的一个实体来copy数据.
	/// </summary>
	/// <param name="en"></param>
	public final void CopyFromFrm(GEEntityOID en) throws Exception {
		//先求出来旧的OID.
		long oldOID = this.getOID();

		//复制主表数据.
		this.Copy(en);
		this.Save();
		this.setOID(oldOID);

		//复制从表数据.
		MapDtls dtls = new MapDtls(this.FrmID);

		//被copy的明细集合.
		MapDtls dtlsFrom = new MapDtls(en.FrmID);

		if (dtls.size() != dtlsFrom.size())
			throw new Exception("@复制的两个表单从表不一致...");

		//序号.
		int i = 0;
		for (MapDtl dtl : dtls.ToJavaList())
		{
			//删除旧的数据.
			DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPK='" + this.getOID() + "'");

			//求对应的Idx的，从表配置.
			MapDtl dtlFrom = dtlsFrom.get(i) instanceof MapDtl ? (MapDtl)dtlsFrom.get(i) : null;
			GEDtls ensDtlFrom = new GEDtls(dtlFrom.getNo());
			ensDtlFrom.Retrieve(GEDtlAttr.RefPK, oldOID);

			//创建一个实体.
			GEDtl dtlEnBlank = new GEDtl(dtl.getNo());

			// 遍历数据,执行copy.
			for (GEDtl enDtlFrom : ensDtlFrom.ToJavaList())
			{
				dtlEnBlank.Copy(enDtlFrom);
				dtlEnBlank.setRefPK(String.valueOf(this.getOID()));
				dtlEnBlank.SaveAsNew();
			}
			i++;
		}

		//复制附件数据.
		FrmAttachments aths = new FrmAttachments(this.FrmID);
		FrmAttachments athsFrom = new FrmAttachments(en.FrmID);
		for (FrmAttachment ath : aths.ToJavaList())
		{
			//删除数据,防止copy重复
			DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE FK_MapData='" + this.FrmID + "' AND RefPKVal='" + this.getOID() + "'");

			for (FrmAttachment athFrom : athsFrom.ToJavaList())
			{
				if (athFrom.getNoOfObj() != ath.getNoOfObj())
					continue;

				FrmAttachmentDBs athDBsFrom = new FrmAttachmentDBs();
				athDBsFrom.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, athFrom.getMyPK(), FrmAttachmentDBAttr.RefPKVal, en.getOID());
				for (FrmAttachmentDB athDBFrom : athDBsFrom.ToJavaList())
				{
					athDBFrom.setMyPK(DBAccess.GenerGUID());
					athDBFrom.setFrmID(this.FrmID);
					athDBFrom.setFKFrmAttachment(ath.getMyPK());
					athDBFrom.setRefPKVal(String.valueOf(this.getOID()));
					athDBFrom.Insert();
				}
			}
		}
	}

	/// <summary>
	/// 把当前实体的数据copy到指定的主键数据表里.
	/// </summary>
	/// <param name="oid">指定的主键</param>
	public final void CopyToOID(long oid) throws Exception {

		//先求出来旧的OID.
		Object oidOID = this.getOID();
		this.setOID(oid);
		this.Save();

		//复制从表数据.
		MapDtls dtls = new MapDtls(this.FrmID);
		for (MapDtl dtl : dtls.ToJavaList())
		{
			//删除旧的数据.
			DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPK='" + this.getOID() + "'");

			GEDtls ensDtl = new GEDtls(dtl.getNo());

			//   var typeVal = BP.Sys.Base.Glo.GenerRealType( ensDtl.GetNewEntity.EnMap.Attrs, GEDtlAttr.RefPK, this.OID);

			ensDtl.Retrieve(GEDtlAttr.RefPK, oidOID.toString());

			for (GEDtl enDtl : ensDtl.ToJavaList())
			{
				enDtl.setRefPK(String.valueOf(this.getOID()));
				enDtl.InsertAsNew();
			}
		}

		//复制附件数据.
		FrmAttachments aths = new FrmAttachments(this.FrmID);
		for (FrmAttachment ath : aths.ToJavaList())
		{
			//删除可能存在的新oid数据。
			DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE FK_MapData='" + this.FrmID + "' AND RefPKVal='" + this.getOID() + "'");

			//找出旧数据.
			FrmAttachmentDBs athDBs = new FrmAttachmentDBs(this.FrmID, oidOID.toString());
			for (FrmAttachmentDB athDB : athDBs.ToJavaList())
			{
				FrmAttachmentDB athDB_N = new FrmAttachmentDB();
				athDB_N.Copy(athDB);

				athDB_N.setFrmID(this.FrmID);
				athDB_N.setRefPKVal(String.valueOf(this.getOID()));

				if (athDB_N.getHisAttachmentUploadType() == AttachmentUploadType.Single)
				{
					/*如果是单附件.*/
					athDB_N.setMyPK(athDB_N.getFKFrmAttachment() + "_" + this.getOID());
					if (athDB_N.getIsExits() == true)
						continue;  /*说明上一个节点或者子线程已经copy过了, 但是还有子线程向合流点传递数据的可能，所以不能用break.*/

					athDB_N.Insert();
				}
				else
				{
					athDB_N.setMyPK(DBAccess.GenerGUID());
					athDB_N.Insert();
				}
			}
		}
	}

	private ArrayList _Dtls = null;
	public final ArrayList getDtls()
	{
		if (_Dtls == null)
		{
			_Dtls = new ArrayList();
		}
		return _Dtls;
	}
	protected String getSerialKey()
	{
		return "OID";
	}

	/** 
	 作为一个新的实体保存。
	*/
	public final void SaveAsNew() throws Exception {
		try
		{
			String oidOID = String.valueOf(this.getOID());
			this.SetValByKey(this.getPK(),DBAccess.GenerOIDByKey32(this.getSerialKey()));
			this.RunSQL(SqlBuilder.Insert(this));

			String ptable = this.getEnMap().getPhysicsTable();

			//判断有没有DBFile文件.
			if (DBAccess.IsExitsTableCol(ptable, "DBFile") == true)
			{
				//获取旧数据bye.
				byte[] val = DBAccess.GetByteFromDB(ptable, "OID", String.valueOf(oidOID), "DBFile");

				//保存到新记录里面.
				DBAccess.SaveBytesToDB(val, ptable, "OID", String.valueOf(this.getOID()), "DBFile");
			}
		}
		catch (Exception ex)
		{
			this.CheckPhysicsTable();
			throw ex;
		}
	}
	/** 
	 按照指定的OID Insert.
	*/
	public final void InsertAsOID(int oid) throws Exception {
		this.SetValByKey("OID", oid);
		try
		{
			this.RunSQL(SqlBuilder.Insert(this));
		}
		catch (RuntimeException ex)
		{
			this.CheckPhysicsTable();
			throw ex;
		}
	}
	public final void InsertAsOID(long oid) throws Exception {
		try
		{
			//先设置一个标记值，为的是不让其在[beforeInsert]产生oid.
			this.SetValByKey("OID", -999);

			//调用方法.
			this.beforeInsert();

			//设置主键.
			this.SetValByKey("OID", oid);

			this.RunSQL(SqlBuilder.Insert(this));

			this.afterInsert();
		}
		catch (Exception ex)
		{
			this.CheckPhysicsTable();
			throw ex;
		}
	}
	/**
	 按照指定的OID 保存
	 
	 @param oid
	*/
	public final void SaveAsOID(long oid) throws Exception {
		this.SetValByKey("OID", oid);
		if (this.Update() == 0)
		{
			this.InsertAsOID(oid);
		}
	}

}
