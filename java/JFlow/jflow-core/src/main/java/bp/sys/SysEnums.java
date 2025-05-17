package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.difference.*;
import java.util.*;

/** 
 纳税人集合 
*/
public class SysEnums extends Entities
{
	/** 
	 此枚举类型的个数
	*/
	public int Num = -1;
	public final String ToDesc()
	{
		String strs = "";
		for (SysEnum se : this.ToJavaList())
		{
			strs += se.getIntKey() + " " + se.getLab() + ";";
		}
		return strs;
	}
	public final String GenerCaseWhenForOracle(String enName, String mTable, String key, String field, String enumKey, String def) throws Exception {
		String sql = (String)Cache.GetObjFormApplication("ESQL" + enName + mTable + key + "_" + enumKey, null);
		// String sql = "";
		if (sql != null)
		{
			return sql;
		}

		sql = " SELECT EnumType,CfgVal FROM Sys_EnumMain WHERE No='" + enumKey + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int enumType = 0;
		if (dt.Rows.size() != 0)
		{
			enumType = Integer.parseInt(dt.Rows.get(0).getValue(0).toString());
		}

		if (this.size()== 0)
		{
			if (dt.Rows.size() == 0)
			{
				throw new Exception("@枚举值（" + enumKey + "）已被删除，无法形成期望的SQL。");
			}
			RegIt(enumKey, dt.Rows.get(0).getValue(1).toString());
			new SysEnums(enumKey);

			if (this.size()== 0)
			{
				throw new RuntimeException("@枚举值" + enumKey + "已被删除。");
			}
		}

		if(enumType == 0 && DataType.IsNumStr(def))
		{
			sql = " CASE NVL(" + mTable + field + "," + Integer.parseInt(def) + ")";
		}
		else
		{
			sql = " CASE NVL(" + mTable + field + ",'" + def + "')";
		}
		for (SysEnum se1 : this.ToJavaList())
		{
			if(DataType.IsNullOrEmpty(se1.getStrKey()))
			{
				sql += " WHEN " + se1.getIntKey() + " THEN '" + se1.getLab() + "'";
			}
			else
			{
				sql += " WHEN '" + se1.getStrKey() + "' THEN '" + se1.getLab() + "'";
			}
		}

		SysEnum se = null;
		if (enumType == 0 && DataType.IsNumStr(def))
		{
			se = (SysEnum)this.GetEntityByKey(SysEnumAttr.IntKey, Integer.parseInt(def));
		}
		else if (enumType == 1 && DataType.IsNullOrEmpty(def) == false)
		{
			se = (SysEnum)this.GetEntityByKey(SysEnumAttr.StrKey, def);
		}
		if (se == null)
		{
			sql += " END \"" + key + "Text\"";
		}
		else
		{
			sql += " WHEN NULL THEN '" + se.getLab() + "' END \"" + key + "Text\"";
		}

		Cache.AddObj("ESQL" + enName + mTable + key + "_" + enumKey, Depositary.Application, sql);
		return sql;
	}

	public final String GenerCaseWhenForOracle(String mTable, String key, String field, String enumKey, String def) throws Exception {
		String sql = "";
		sql = " SELECT EnumType,CfgVal From Sys_EnumMain WHERE No='" + enumKey + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int enumType = 0;
		if (dt.Rows.size() != 0)
		{
			String val = dt.Rows.get(0).getValue(0).toString();
			if (val == null)
			{
				val = "0";
			}
			enumType = Integer.parseInt(val); //@lyc.
		}
		if (this.size()== 0)
		{
			if(dt.Rows.size() == 0)
			{
				throw new Exception("@枚举值（" + enumKey + "）已被删除，无法形成期望的SQL。");
			}
			RegIt(enumKey, dt.Rows.get(0).getValue(1).toString());
			new SysEnums(enumKey);

			if (this.size()== 0)
			{
				throw new RuntimeException("@枚举值（" + enumKey + "）已被删除，无法形成期望的SQL。");
			}
		}



		sql = " CASE " + mTable + field;
		for (SysEnum se1 : this.ToJavaList())
		{
			if(DataType.IsNullOrEmpty(se1.getStrKey()))
			{
				sql += " WHEN " + se1.getIntKey() + " THEN '" + se1.getLab() + "'";
			}
			else
			{
				sql += " WHEN '" + se1.getStrKey() + "' THEN '" + se1.getLab() + "'";
			}
		}

		SysEnum se = null;
		if (enumType == 0 && DataType.IsNumStr(def))
		{
			se = (SysEnum)this.GetEntityByKey(SysEnumAttr.IntKey, Integer.parseInt(def));
		}
		else if (enumType == 1 && DataType.IsNullOrEmpty(def) == false)
		{
			se = (SysEnum)this.GetEntityByKey(SysEnumAttr.StrKey, def);
		}
		if (se == null)
		{
			sql += " END \"" + key + "Text\"";
		}
		else
		{
			sql += " WHEN NULL THEN '" + se.getLab() + "' END \"" + key + "Text\"";
		}

		// Cache.AddObj("ESQL" + enName + key + "_" + enumKey, Depositary.Application, sql);
		return sql;
	}
	public final void LoadIt(String enumKey) throws Exception {
		if (this.Full(enumKey) == true)
		{
			return;
		}

		try
		{
			bp.sys.xml.EnumInfoXml xml = new bp.sys.xml.EnumInfoXml(enumKey);
			this.RegIt(enumKey, xml.getVals());
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("@你没有预制[" + enumKey + "]枚举值。@在修复枚举值出现错误:" + ex.getMessage());
		}
	}
	/** 
	 把所有的枚举注册一遍.
	*/
	public static void RegAll() throws Exception {
		bp.sys.xml.EnumInfoXmls xmls = new bp.sys.xml.EnumInfoXmls();
		xmls.RetrieveAll();
		SysEnums ses = new SysEnums();
		for (bp.sys.xml.EnumInfoXml xml : xmls.ToJavaList())
		{
			ses.RegIt(xml.getKey(), xml.getVals());
		}
	}
	/** 
	 SysEnums
	 
	 @param enumKey
	*/
	public SysEnums(String enumKey) throws Exception {
		this.LoadIt(enumKey);
	}
	public SysEnums(String enumKey, String vals) throws Exception {
		if (DataType.IsNullOrEmpty(vals) == true)
		{
			this.LoadIt(enumKey);
			return;
		}

		if (this.Full(enumKey) == false)
		{
			this.RegIt(enumKey, vals);
		}
	}
	public final void RegIt(String EnumKey, String vals) throws Exception {
		try
		{
			if (DataType.IsNullOrEmpty(vals))
			{
				throw new Exception("err@EnumKey="+ EnumKey + "的错误vals是空值.");
			}

			vals = vals.replace(" ", "");
			vals = vals.replace(" ", "");
			vals = vals.replace(" ", "");
			vals = vals.replace("\n", "");

			String[] strs = vals.split("@");
			SysEnums ens = new SysEnums();
			ens.Delete(SysEnumAttr.EnumKey, EnumKey);
			this.clear();

			for (String s : strs)
			{
				if (DataType.IsNullOrEmpty(s) == true)
					continue;
				String[] vk = s.split("=");
				SysEnum se = new SysEnum();
				se.setIntKey(Integer.parseInt(vk[0]));
				//杨玉慧
				//解决当  枚举值含有 '='号时，保存不进去的方法
				String[] kvsValues = new String[vk.length - 1];
				for (int i = 0; i < kvsValues.length; i++)
				{
					kvsValues[i] = vk[i + 1];
				}
				se.setLab(StringHelper.join("=", kvsValues));
				se.setEnumKey(EnumKey);
				se.setLang(bp.web.WebUser.getSysLang());
				se.setMyPK(EnumKey + "_" + se.getLang() + "_" + se.getIntKey());
				se.DirectInsert();
				this.AddEntity(se);
			}
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException(ex.getMessage() + " - " + vals);
		}
		//  this.Full(EnumKey);
	}
	public final boolean Full(String enumKey) throws Exception {
		Entities ens = (Entities)Cache.GetObjFormApplication("EnumOf" + enumKey + bp.web.WebUser.getSysLang(), null);
		if (ens != null)
		{
			this.AddEntities(ens);
			return true;
		}

		QueryObject qo = new QueryObject(this);

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			//如果是自定义的枚举.
			if (enumKey.contains(bp.web.WebUser.getOrgNo() + "_"))
			{
				String mykey = enumKey.replace(bp.web.WebUser.getOrgNo() + "_", "");

				/* 看看xml配置里面是否有?*/
				qo.AddWhere(SysEnumAttr.OrgNo, bp.web.WebUser.getOrgNo());
				qo.addAnd();
				qo.AddWhere(SysEnumAttr.EnumKey, mykey);
				qo.addOrderBy(SysEnumAttr.IntKey);
				if (qo.DoQuery() == 0)
				{
					return false;
				}

				Cache.AddObj("EnumOf" + enumKey + bp.web.WebUser.getSysLang(), Depositary.Application, this);
				return true;
			}

			qo.AddWhere(SysEnumAttr.EnumKey, enumKey);
			qo.addOrderBy(SysEnumAttr.IntKey);
			if (qo.DoQuery() == 0)
			{
				return false;
			}

			Cache.AddObj("EnumOf" + enumKey + bp.web.WebUser.getSysLang(), Depositary.Application, this);
			return true;
		}


		qo.AddWhere(SysEnumAttr.EnumKey, enumKey);
		qo.addAnd();
		qo.AddWhere(SysEnumAttr.Lang, bp.web.WebUser.getSysLang());
		qo.addOrderBy(SysEnumAttr.IntKey);
		if (qo.DoQuery() == 0)
		{
			/* 看看xml配置里面是否有?*/
			return false;
		}

		Cache.AddObj("EnumOf" + enumKey + bp.web.WebUser.getSysLang(), Depositary.Application, this);
		return true;
	}
	///// <summary>
	///// DBSimpleNoNames
	///// </summary>
	///// <returns></returns>
	//public DBSimpleNoNames ToEntitiesNoName()
	//{
	//    DBSimpleNoNames ens = new DBSimpleNoNames();
	//    foreach (SysEnum en in this)
	//    {
	//        ens.AddByNoName(en.IntKey.ToString(), en.Lab);
	//    }
	//    return ens;
	//}
	/** 
	 
	 
	 @param key
	 @param val
	 @return 
	*/
	public final int Delete(String key, Object val) throws Exception {
		try
		{
			Entity en = this.getNewEntity();
			Paras ps = new Paras();

			ps.SQL = "DELETE FROM " + en.getEnMap().getPhysicsTable() + " WHERE " + key + "=" + en.getHisDBVarStr() + "p";
			ps.Add("p", val);
			return en.RunSQL(ps);
		}
		catch (java.lang.Exception e)
		{
			Entity en = this.getNewEntity();
			en.CheckPhysicsTable();

			Paras ps = new Paras();
			ps.SQL = "DELETE FROM " + en.getEnMap().getPhysicsTable() + " WHERE " + key + "=" + en.getHisDBVarStr() + "p";
			ps.Add("p", val);
			return en.RunSQL(ps);
		}
	}
	/** 
	 SysEnums
	*/
	public SysEnums()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SysEnum();
	}
	/** 
	 通过int 得到Lab
	 
	 @param val val
	 @return String val
	*/
	public final String GetLabByVal(int val)
	{
		for (SysEnum en : this.ToJavaList())
		{
			if (en.getIntKey() == val)
			{
				return en.getLab();
			}
		}
		return null;
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SysEnum> ToJavaList()
	{
		return (java.util.List<SysEnum>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SysEnum> Tolist()
	{
		ArrayList<SysEnum> list = new ArrayList<SysEnum>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SysEnum)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
