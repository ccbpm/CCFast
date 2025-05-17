package bp.en;
import bp.da.*;
import bp.difference.SystemConfig;

/*
简介：负责存取数据的类
创建时间：2002-10
最后修改时间：2002-10
*/



public class EntityDBAccess
{

		///对实体的基本操作
	/**
	 删除

	 param en
	 @return
	 * @throws Exception
	*/
	public static int Delete(Entity en) throws Exception
	{
		if (en.getEnMap().getEnType() == EnType.View)
		{
			return 0;
		}

		switch (en.getEnMap().getEnDBUrl().getDBUrlType())
		{
			case AppCenterDSN :
				return DBAccess.RunSQL(en.getSQLCache().Delete, SqlBuilder.GenerParasPK(en));
			default :
				throw new RuntimeException("@没有设置类型。");
		}
	}
	/**
	 更新

	 param en 产生要更新的语句
	 param keys 要更新的属性(null,认为更新全部)
	 @return sql
	 * @throws Exception
	*/
	public static int Update(Entity en, String[] keys) throws Exception
	{
		if (en.getEnMap().getEnType() == EnType.View)
		{
			return 0;
		}

		bp.da.Paras paras = SqlBuilder.GenerParas(en, keys);
		String sql = en.getSQLCache().GetUpdateSQL(en, keys);
		try
		{
			switch (en.getEnMap().getEnDBUrl().getDBUrlType())
			{
				case AppCenterDSN:
					switch (SystemConfig.getAppCenterDBType())
					{
						case MSSQL:
						case Oracle:
						case DM:
						case KingBaseR3:
						case KingBaseR6:
						case MySQL:
						case PostgreSQL:
						case HGDB:
						case GBASE8CByOracle:
						case GBASE8CByMySQL:
						case GBASE8A:
							return DBAccess.RunSQL(sql, paras);
						case Informix:
							return DBAccess.RunSQL(en.getSQLCache().GetUpdateSQL(en, keys), SqlBuilder.GenerParas_Update_Informix(en, keys));
						case Access:
							return DBAccess.RunSQL(SqlBuilder.UpdateOfMSAccess(en, keys));
						default:
							//return DBAccess.RunSQL(en.SQLCache.GetUpdateSQL(en, keys),
							//    SqlBuilder.GenerParas(en, keys));
							if (keys != null)
							{
								Paras ps = new Paras();
								Paras myps = SqlBuilder.GenerParas(en, keys);
								for (Para p : myps)
								{
									for (String s : keys)
									{
										if (p.ParaName.equals(s))
										{
											ps.Add(p);
											break;
										}
									}
								}
								return DBAccess.RunSQL(en.getSQLCache().GetUpdateSQL(en, keys), ps);
							}
							else
							{
								return DBAccess.RunSQL(en.getSQLCache().GetUpdateSQL(en, keys), SqlBuilder.GenerParas(en, keys));
							}

					}

				default:
					throw new RuntimeException("@没有设置类型。");
			}
		}
		catch (RuntimeException ex)
		{
			if (SystemConfig.isDebug())
			{
				en.CheckPhysicsTable();
			}
			throw ex;
		}
	}


	public static int Retrieve(Entity en, String sql, Paras paras) throws Exception
	{
		DataTable dt;
		switch (en.getEnMap().getEnDBUrl().getDBUrlType())
		{
			case AppCenterDSN:
				dt = DBAccess.RunSQLReturnTable(sql, paras);
				break;

			default:
				throw new RuntimeException("@没有设置DB类型。");
		}

		if (dt.Rows.size() == 0)
		{
			return 0;
		}
		Attrs attrs = en.getEnMap().getAttrs();
		EntityDBAccess.fullDate(dt, en, attrs);
		int num = dt.Rows.size();
		dt.Clear();
		return num;
	}
	/**
	 查询

	 param en 实体
	 param sql 组织的查询语句
	 @return
	*/
	public static int Retrieve(Entity en, String sql)throws Exception
	{
		try
		{
			DataTable dt = new DataTable();
			switch (en.getEnMap().getEnDBUrl().getDBUrlType())
			{
				case AppCenterDSN:
					dt = DBAccess.RunSQLReturnTable(sql);
					break;

				default:
					throw new RuntimeException("@没有设置DB类型。");
			}

			if (dt.Rows.size() == 0)
			{
				return 0;
			}
			Attrs attrs = en.getEnMap().getAttrs();
			EntityDBAccess.fullDate(dt, en, attrs);
			int i = dt.Rows.size();
			return i;
		}
		catch (RuntimeException ex)
		{
			throw ex;
		}
	}
	private static void fullDate(DataTable dt, Entity en, Attrs attrs)throws Exception
	{
		//判断是否加密.
		if (en.getEnMap().ItIsJM == false)
		{
			String parafields = en.getEnMap().ParaFields;
			if (parafields == null)
			{
				for (Attr attr : attrs)
				{
					en.getRow().SetValByKey(attr.getKey(), dt.Rows.get(0).getValue(attr.getKey()));
				}
				return;
			}

			if (dt.Columns.contains("AtPara") == false)
				throw new Exception("err@实体类[" + en.getEnMap().getEnDesc() + "_" + en.toString() + "] 缺少AtPara字段.");

			AtPara ap = new AtPara(dt.Rows.get(0).getValue("AtPara").toString());
			parafields = "," + en.getEnMap().ParaFields+",";
			for (Attr attr : attrs)
			{
				//如果是参数字段.
				if (parafields.contains("," + attr.getKey() + ",") == true)
				{
					en.getRow().SetValByKey(attr.getKey(), ap.GetValStrByKey(attr.getKey()));
					continue;
				}

				//判断枚举字段是否是参数字段.
				if (attr.getItIsRefAttr() == true && dt.Columns.contains(attr.getKey()) == false)
				{
					String key = attr.getKey().replace("Text", "");
					Attr enumAttr = attrs.GetAttrByKey(key);

					AtPara apcfg = new AtPara(enumAttr.UITag);
					String enumVal = en.getRow().get(key).toString();
					en.getRow().SetValByKey(attr.getKey(), apcfg.GetValStrByKey(enumVal));
					continue;
				}

				en.getRow().SetValByKey(attr.getKey(), dt.Rows.get(0).getValue(attr.getKey()));
			}
			return;
		}

		//执行解密.
		for (Attr attr : attrs)
		{
			Object val = dt.Rows.get(0).getValue(attr.getKey());

			if (attr.getItIsPK() == false && attr.getMyDataType() == DataType.AppString)
				val = val;

			en.getRow().SetValByKey(attr.getKey(), val);
		}
	}
	public static int Retrieve(Entities ens, String sql)throws Exception
	{
		try
		{
			DataTable dt = new DataTable();
			switch (ens.getNewEntity().getEnMap().getEnDBUrl().getDBUrlType())
			{
				case AppCenterDSN:
					dt = DBAccess.RunSQLReturnTable(sql);
					break;

				default:
					throw new RuntimeException("@没有设置DB类型。");
			}

			if (dt.Rows.size() == 0)
			{
				return 0;
			}

			Map enMap = ens.getNewEntity().getEnMap();
			Attrs attrs = enMap.getAttrs();

			//Entity  en1 = ens.getNewEntity();
			for (DataRow dr : dt.Rows)
			{
				Entity en = ens.getNewEntity();
				//Entity  en = en1.CreateInstance();
				for (Attr attr : attrs)
				{
					en.getRow().SetValByKey(attr.getKey(), dr.getValue(attr.getKey()));
				}
				ens.AddEntity(en);
			}
			int i = dt.Rows.size();

			return i;
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("@在[" + ens.getNewEntity().getEnDesc() + "]查询时出现错误:" + ex.getMessage());
		}
	}
	public static int Retrieve(Entities ens, String sql, Paras paras, String[] fullAttrs)throws Exception
	{
		DataTable dt = null;
		switch (ens.getNewEntity().getEnMap().getEnDBUrl().getDBUrlType())
		{
			case AppCenterDSN:
				dt = DBAccess.RunSQLReturnTable(sql, paras);
				break;

			default:
				throw new RuntimeException("@没有设置DB类型。");
		}

		if (dt.Rows.size() == 0)
		{
			return 0;
		}
		ens.clear();
		//设置查询.
		QueryObject.InitEntitiesByDataTable(ens, dt, fullAttrs);

		int i = dt.Rows.size();
		return i;
	}
}
