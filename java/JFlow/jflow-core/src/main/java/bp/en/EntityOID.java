package bp.en;

import bp.da.*;
import bp.difference.SystemConfig;

/**
 OID实体,只有一个实体这个实体只有一个主键属性。
*/
public abstract class EntityOID extends Entity
{
	private static final long serialVersionUID = 1L;
	///属性
	/**
	 是否是自动增长列
	*/
	public boolean getIsInnKey()
	{
		return false;
	}
	/**
	 主键
	*/
	@Override
	public String getPK()
	{
		return "OID";
	}
	/**
	 OID, 如果是空的就返回 0 .
	*/
	public long getOID()
	{
		try
		{
			return this.GetValInt64ByKey(EntityOIDAttr.OID);
		}
		catch (java.lang.Exception e)
		{
			return 0;
		}
	}
	public  void setOID(long value)  {
		this.SetValByKey(EntityOIDAttr.OID, value);
	}

		///


		///构造函数
	/**
	 构造一个空实例
	*/
	protected EntityOID()
	{
	}
	/**
	 根据OID构造实体

	 param oid oid
	 * @throws Exception
	*/
	protected EntityOID(int oid) throws Exception {
		this.SetValByKey(EntityOIDAttr.OID, oid);
		this.Retrieve();
	}

		///


		///override 方法
	@Override
	public int DirectInsert() throws Exception
	{
		if(this.getOID() == 0)
			this.setOID(DBAccess.GenerOID());
		return super.DirectInsert();
	}
	public final void InsertAsNew() throws Exception
	{
		this.setOID(0);
		this.Insert();
	}
	@Override
	public boolean getIsExits() throws Exception
	{
		if (this.getOID() == 0)
		{
			return false;
		}

		try
		{
				// 生成数据库判断语句。
			String selectSQL = "SELECT " + this.getPKField() + " FROM " + this.getEnMap().getPhysicsTable() + " WHERE OID=" + this.getHisDBVarStr() + "v";
			Paras ens = new Paras();
			ens.Add("v", this.getOID());

				// 从数据库里面查询，判断有没有。
			switch (this.getEnMap().getEnDBUrl().getDBUrlType())
			{
				case AppCenterDSN:
					return DBAccess.IsExits(selectSQL, ens);
				default:
					throw new RuntimeException("没有设计到。" + this.getEnMap().getEnDBUrl().getDBType());
			}
		}
		catch (RuntimeException ex)
		{
			this.CheckPhysicsTable();
			throw ex;
		}

			/* DEL BY PENG 2008-04-27
			// 生成数据库判断语句。
			String selectSQL="SELECT "+this.PKField + " FROM "+ this.getEnMap().getPhysicsTable() + " WHERE " ;
			switch(this.getEnMap().EnDBUrl.DBType )
			{
				case MSSQL:
					selectSQL +=SqlBuilder.GetKeyConditionOfMS(this);
					break;
				case Access:
					selectSQL +=SqlBuilder.GetKeyConditionOfOLE(this);
					break;
				case Oracle:
					selectSQL +=SqlBuilder.GetKeyConditionOfOracle(this);
					break;
				default:
					throw new Exception("没有设计到。"+this.getEnMap().EnDBUrl.DBType);
			}

			// 从数据库里面查询，判断有没有。
			switch(this.getEnMap().EnDBUrl.DBUrlType )
			{
				case DBUrlType.AppCenterDSN:
					return DBAccess.getIsExits()( selectSQL) ;
				case DBUrlType.DBAccessOfMSSQL:
					return DBAccessOfMSSQL.getIsExits()( selectSQL) ;
				case DBUrlType.DBAccessOfOLE:
					return DBAccessOfOLE.getIsExits()( selectSQL) ;
				case DBUrlType.DBAccessOfOracle:
					return DBAccessOfOracle.getIsExits()( selectSQL) ;
				default:
					throw new Exception("没有设计到。"+this.getEnMap().EnDBUrl.DBType);
			}
			*/
	}
	/**
	 删除之前的操作。

	 @return
	 * @throws Exception
	*/
	@Override
	protected boolean beforeDelete() throws Exception
	{
		if (super.beforeDelete() == false)
		{
			return false;
		}
		try
		{
			if (this.getOID() < 0)
			{
				throw new RuntimeException("@实体[" + this.getEnDesc() + "]没有被实例化，不能Delete().");
			}
			return true;
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("@[" + this.getEnDesc() + "].beforeDelete err:" + ex.getMessage());
		}
	}
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		return super.beforeUpdateInsertAction();
	}

	/**
	 beforeInsert 之前的操作。

	 @return
	 * @throws Exception
	*/
	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (this.getOID() == -999)
		{
			return super.beforeInsert();
		}

		if (this.getOID() > 0)
		{
			throw new RuntimeException("@[" + this.getEnDesc() + "], 实体已经被实例化 oid=[" + this.getOID() + "]，不能Insert.");
		}

		if (this.getIsInnKey())
		{
			this.setOID(-1);
		}
		else
		{
			this.setOID(DBAccess.GenerOID());
		}

		return super.beforeInsert();

	}
	/**
	 beforeUpdate

	 @return
	 * @throws Exception
	*/
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		if (super.beforeUpdate() == false)
		{
			return false;
		}


		return true;
	}
	protected String getSerialKey()throws Exception
	{
		return "OID";
	}


		///


		///public 方法
	/**
	 作为一个新的实体保存。
	 * @throws Exception
	*/
	public final void SaveAsNew() throws Exception
	{
		try
		{
			this.setOID(DBAccess.GenerOIDByKey32(this.getSerialKey()));
			this.RunSQL(SqlBuilder.Insert(this));
		}
		catch (RuntimeException ex)
		{
			this.CheckPhysicsTable();
			throw ex;
		}
	}
	/**
	 按照指定的OID Insert.
	 * @throws Exception
	*/
	public final void InsertAsOID(int oid) throws Exception {
		this.SetValByKey("OID", oid);
		try
		{
			switch (SystemConfig.getAppCenterDBType())
			{
				case MSSQL:
					this.RunSQL(this.getSQLCache().Insert, SqlBuilder.GenerParas(this, null));
				case Access:
					this.RunSQL(this.getSQLCache().Insert, SqlBuilder.GenerParas(this, null));
				case MySQL:
				case Informix:
				default:
					this.RunSQL(this.getSQLCache().Insert.replace("[", "").replace("]", ""), SqlBuilder.GenerParas(this, null));
			}
			//this.RunSQL(SqlBuilder.Insert(this));
		}
		catch (RuntimeException ex)
		{
			this.CheckPhysicsTable();
			throw ex;
		}
	}
	public final void InsertAsOID(long oid) throws Exception
	{
		try
		{
			//先设置一个标记值，为的是不让其在[beforeInsert]产生oid.
			this.SetValByKey("OID", -999);

			//调用方法.
			this.beforeInsert();

			//设置主键.
			this.SetValByKey("OID", oid);

			switch (SystemConfig.getAppCenterDBType())
			{
				case MSSQL:
				case Access:
					this.RunSQL(this.getSQLCache().Insert, SqlBuilder.GenerParas(this, null));
					break;
				default:
					this.RunSQL(this.getSQLCache().Insert.replace("[", "").replace("]", ""), SqlBuilder.GenerParas(this, null));
			}
			//this.RunSQL(SqlBuilder.Insert(this));
			this.afterInsert();
		}
		catch (RuntimeException ex)
		{
			this.CheckPhysicsTable();
			throw ex;
		}
	}
	/**
	 按照指定的OID 保存

	 param oid
	 * @throws Exception
	*/
	public final void SaveAsOID(long oid) throws Exception {
		this.SetValByKey("OID", oid);
		if (this.IsExits() == false)
		{
			this.InsertAsOID(oid);
			return;
		}
		this.Update();
	}

}
