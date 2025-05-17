package bp.sys;

import bp.ccbill.FrmBillAttr;
import bp.ccbill.FrmEntityNoNameAttr;
import bp.da.DBAccess;
import bp.da.DataType;
import bp.en.*;
import bp.sys.*;
import bp.wf.GERptAttr;
import bp.wf.template.SysFormTrees;

import java.util.ArrayList;
import java.util.Objects;

/** 
 单据属性
*/
public class GEEntityNoName extends EntityNoName
{

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
	/// <summary>
	/// 主键
	/// </summary>
	public String FrmID = null;

		///#region 构造方法
	/**
	 单据属性
	*/
	public GEEntityNoName()
	{
	}
	/// <summary>
	/// 通用OID实体
	/// </summary>
	/// <param name="nodeid">节点ID</param>
	public GEEntityNoName(String fk_mapdata)
	{
		this.FrmID =fk_mapdata;
		this.set_enMap(null);
	}
	/**
	 @param frmID
	 @param pk
	 */
	public GEEntityNoName(String frmID, Object pk) throws Exception {
		this.FrmID =frmID;
		this.setPKVal(pk);
		this.set_enMap(null);
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

		if (this.FrmID == null)
		{
			throw new RuntimeException("没有给[" + this.FrmID + "]值，您不能获取它的Map。");
		}
		try{
			this.set_enMap(bp.sys.MapData.GenerHisMap(this.FrmID));
		}catch(Exception ex){

		}
		return this.get_enMap();
	}

	/**
	 GEEntityNoNames
	 */
	@Override
	public Entities GetNewEntities()
	{
		if (this.FrmID == null)
		{
			return new GEEntityNoNames();
		}
		return new GEEntityNoNames(this.FrmID);
	}

	/**
	 从另外的一个实体来copy数据.
	 @param en
	 */
	public final void CopyFromFrm(GEEntityNoName en) throws Exception {
		//先求出来旧的OID.
		String oldNo = this.getNo();

		//复制主表数据.
		this.Copy(en);
		this.Save();
		this.SetValByKey("No", oldNo);

		//复制从表数据.
		MapDtls dtls = new MapDtls(this.FrmID);

		//被copy的明细集合.
		MapDtls dtlsFrom = new MapDtls(en.FrmID);

		if (dtls.size() != dtlsFrom.size())
		{
			throw new RuntimeException("@复制的两个表单从表不一致...");
		}

		//序号.
		int i = 0;
		for (MapDtl dtl : dtls.ToJavaList())
		{
			//删除旧的数据.
			DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPK='" + oldNo + "'");

			//求对应的Idx的，从表配置.
			MapDtl dtlFrom = dtlsFrom.get(i) instanceof MapDtl ? (MapDtl)dtlsFrom.get(i) : null;
			GEDtls ensDtlFrom = new GEDtls(dtlFrom.getNo());
			ensDtlFrom.Retrieve(GEDtlAttr.RefPK, oldNo);

			//创建一个实体.
			GEDtl dtlEnBlank = new GEDtl(dtl.getNo());

			// 遍历数据,执行copy.
			for (GEDtl enDtlFrom : ensDtlFrom.ToJavaList())
			{
				dtlEnBlank.Copy(enDtlFrom);
				dtlEnBlank.setRefPK(this.getNo().toString());
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
			DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE FK_MapData='" + this.FrmID + "' AND RefPKVal='" + this.getNo() + "'");

			for (FrmAttachment athFrom : athsFrom.ToJavaList())
			{
				if (!Objects.equals(athFrom.getNoOfObj(), ath.getNoOfObj()))
				{
					continue;
				}

				FrmAttachmentDBs athDBsFrom = new FrmAttachmentDBs();
				athDBsFrom.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, athFrom.getMyPK(), FrmAttachmentDBAttr.RefPKVal, String.valueOf(en.getNo()));
				for (FrmAttachmentDB athDBFrom : athDBsFrom.ToJavaList())
				{
					athDBFrom.setMyPK(DBAccess.GenerGUID());
					athDBFrom.setFrmID(this.FrmID);
					athDBFrom.setFKFrmAttachment(ath.getMyPK());
					athDBFrom.setRefPKVal(String.valueOf(this.getNo()));
					athDBFrom.Insert();
				}

			}
		}
	}

	/**
	 把当前实体的数据copy到指定的主键数据表里.

	 @param oid 指定的主键
	 */
	public final void CopyToNo(String oid) throws Exception {
		//实例化历史数据表单entity.
		String oidOID = this.getNo();
		this.setNo(oid);
		this.Save();

		//复制从表数据.
		MapDtls dtls = new MapDtls(this.FrmID);
		for (MapDtl dtl : dtls.ToJavaList())
		{
			//删除旧的数据.
			DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPK='" + this.getNo() + "'");

			GEDtls ensDtl = new GEDtls(dtl.getNo());

			//   var typeVal = BP.Sys.Base.Glo.GenerRealType( ensDtl.getNewEntity().getEnMap().Attrs, GEDtlAttr.RefPK, this.OID);

			ensDtl.Retrieve(GEDtlAttr.RefPK, String.valueOf(oidOID));

			for (GEDtl enDtl : ensDtl.ToJavaList())
			{
				enDtl.setRefPK(String.valueOf(this.getNo()));
				enDtl.InsertAsNew();
			}
		}

		//复制附件数据.
		FrmAttachments aths = new FrmAttachments(this.FrmID);
		for (FrmAttachment ath : aths.ToJavaList())
		{
			//删除可能存在的新oid数据。
			DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE FK_MapData='" + this.FrmID + "' AND RefPKVal='" + this.getNo() + "'");

			//找出旧数据.
			FrmAttachmentDBs athDBs = new FrmAttachmentDBs(this.FrmID, String.valueOf(oidOID));
			for (FrmAttachmentDB athDB : athDBs.ToJavaList())
			{
				FrmAttachmentDB athDB_N = new FrmAttachmentDB();
				athDB_N.Copy(athDB);

				athDB_N.setFrmID(this.FrmID);
				athDB_N.setRefPKVal(String.valueOf(this.getNo()));

				if (athDB_N.getHisAttachmentUploadType() == AttachmentUploadType.Single)
				{
					/*如果是单附件.*/
					athDB_N.setMyPK(athDB_N.getFKFrmAttachment() + "_" + this.getNo());
					if (athDB_N.getIsExits() == true)
					{
						continue; //说明上一个节点或者子线程已经copy过了, 但是还有子线程向合流点传递数据的可能，所以不能用break.
					}

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
		return "No";
	}
	/**
	 作为一个新的实体保存。
	 */
	public final void SaveAsNew() throws Exception {
		try
		{
			String oidOID = this.getNo();
			this.setNo(this.GenerNewNoByKey("No"));
			this.RunSQL(SqlBuilder.Insert(this));

			String ptable = this.getEnMap().getPhysicsTable();

			//判断有没有DBFile文件.
			if (DBAccess.IsExitsTableCol(ptable, "DBFile") == true)
			{
				//获取旧数据bye.
				byte[] val = DBAccess.GetByteFromDB(ptable, "OID", String.valueOf(oidOID), "DBFile");

				//保存到新记录里面.
				DBAccess.SaveBytesToDB(val, ptable, "OID", String.valueOf(this.getNo()), "DBFile");
			}
		}
		catch (Exception ex)
		{
			this.CheckPhysicsTable();
			throw ex;
		}
	}

}
