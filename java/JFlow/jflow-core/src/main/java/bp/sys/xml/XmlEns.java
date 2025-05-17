package bp.sys.xml;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.en.*;
import java.util.*;
import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 XmlEn 的摘要说明。
 */
public abstract class XmlEns extends ArrayList<Object>
{
	public final int LoadXmlFile(String file) throws Exception {
		return LoadXmlFile(file, this.getTableName());
	}
	public final int LoadXmlFile(String file, String table) throws Exception {
		DataSet ds = new DataSet();
		ds.readXml(file);
		DataTable dt = ds.GetTableByName(table);
		for (DataRow dr : dt.Rows)
		{
			XmlEn en = this.getNewEntity();
			en.setRow(new Row());
			en.getRow().LoadDataTable(dt, dr);
			this.Add(en);
		}
		return dt.Rows.size();
	}
	public final boolean Contine(String key, String val) throws Exception {
		for (XmlEn en : this.ToJavaListXmlEns())
		{
			if (en.GetValStringByKey(key).equals(val))
			{
				return true;
			}
		}
		return false;
	}


	///#region 构造
	/**
	 构造
	 */
	public XmlEns()
	{

	}

	///#endregion 构造


	///#region 查询方法
	public final String getTname() throws Exception {
		String tname = this.getFile().replace(".TXT", "").replace(".txt", "");
		tname = tname.substring(tname.lastIndexOf("/") + 1) + this.getTableName() + "_X";
		return tname;
	}

	private DataTable GetTableTxts(List<File> fis) throws Exception {
		Object tempVar = Cache.GetObj(this.getTname(), Depositary.Application);
		DataTable cdt = tempVar instanceof DataTable ? (DataTable)tempVar : null;
		if (cdt != null)
		{
			return cdt;
		}

		DataTable dt = new DataTable(this.getTableName());
		for (java.io.File fi : fis)
		{
			dt = GetTableTxt(dt, fi);
		}

		Cache.AddObj(this.getTname(), Depositary.Application, dt);
		return dt;
	}
	private DataTable GetTableTxt() throws Exception {
		Object tempVar = Cache.GetObj(this.getTname(), Depositary.Application);
		DataTable cdt = tempVar instanceof DataTable ? (DataTable)tempVar : null;
		if (cdt != null)
		{
			return cdt;
		}

		DataTable dt = new DataTable(this.getTableName());
		java.io.File fi = new java.io.File(this.getFile());
		dt = GetTableTxt(dt, fi);

		Cache.AddObj(this.getTname(), Depositary.Application, dt);
		return dt;
	}
	private DataTable GetTableTxt(DataTable dt, java.io.File file)
	{
		BufferedReader br = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		Hashtable<String, Object> ht = new Hashtable<String, Object>();
		try
		{
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "UTF-8");
			br = new BufferedReader(isr);
			String key = "";
			String val = "";
			String lin = "";
			while ((lin = br.readLine()) != null)
			{
				if (lin == null || lin.trim().equals("") )
				{
					continue;
				}
				if (lin.trim().indexOf("*") == 0)
				{
					// 遇到注释文件
					continue;
				}
				if (lin.indexOf("=") == 0)
				{
					// 约定的行记录, 开始以 = 开始就认为是一个新的记录。
					// 处理表结构。
					for (String ojbkey : ht.keySet())
					{
						if (!dt.Columns.contains(ojbkey))
						{
							dt.Columns
									.Add(new DataColumn(ojbkey, String.class));
						}
					}

					DataRow dr = dt.NewRow();
					for (String ojbkey : ht.keySet())
					{
						dr.put(ojbkey, ht.get(ojbkey));
						/*
						 * warning dr[ojbkey] = ht.get(ojbkey);
						 */
					}

					if (ht.keySet().size() > 1)
					{
						dt.Rows.add(dr);
					}

					ht.clear(); // clear hashtable.
					continue;
				}

				int idx = lin.indexOf("=");
				if (idx == -1)
				{
					throw new RuntimeException(this.getFile()
							+ "@不符合规则 key =val 的规则。");
				}

				key = lin.substring(0, idx);
				if (key.equals(""))
				{
					continue;
				}

				val = lin.substring(idx + 1);
				ht.put(key, val);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				br.close();
				isr.close();
				fis.close();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		return dt;
	}
	public final DataTable GetTable() throws Exception {
		Object tempVar = Cache.GetObj(this.getTname(), Depositary.Application);
		DataTable cdt = tempVar instanceof DataTable ? (DataTable)tempVar : null;
		if (cdt != null)
		{
			return cdt;
		}
		String filePath = this.getFile();
		if (filePath.toLowerCase().indexOf(".txt") > 0)
		{
			return this.GetTableTxt();
		}

		if (filePath.toLowerCase().indexOf(".xml") > 0)
		{
			DataSet ds1 = new DataSet();
			ds1.readXml(this.getFile());
			DataTable mdt = ds1.GetTableByName(this.getTableName());
			if (mdt == null)
			{
				mdt = new DataTable();
			}

			Cache.AddObj(this.getTname(), Depositary.Application, mdt);

			return ds1.GetTableByName(this.getTableName());
		}

		if(SystemConfig.isJarRun()){
			DataTable dt = new DataTable(this.getTableName());
			//读取jar包获取jar包中含有filePath的文件
			List<String> list = new ArrayList<String>();
			JarFile jFile = null;
			try {
				jFile = new JarFile(System.getProperty("java.class.path"));
				Enumeration<JarEntry> jarEntrys = jFile.entries();
				while (jarEntrys.hasMoreElements()) {
					JarEntry entry = jarEntrys.nextElement();
					String name = entry.getName();
					if(name.contains(filePath) && name.endsWith(filePath)==false) {
						list.add(name.replace("BOOT-INF/classes/",""));
					}
				}

				if(list.size()==0){
					return dt;
				}
				DataTable tempDT = new DataTable();
				for(String pathName :list){
					DataSet ds = new DataSet("myds");
					ds.readXml(pathName);
					if (dt.Columns.size() == 0)
					{
						try
						{
							dt = ds.GetTableByName(this.getTableName());
						} catch (RuntimeException ex)
						{
							throw new RuntimeException("可能是没有在" + pathName
									+ "文件中找到表:" + this.getTableName()
									+ " exception=" + ex.getMessage());
						}
						tempDT = dt;
						continue;
					}

					DataTable mydt = ds.GetTableByName(this.getTableName());
					if (mydt == null)
						throw new RuntimeException("无此表:" + this.getTableName());

					if (mydt.Rows.size() == 0)
						continue;

					for (DataRow mydr : mydt.Rows)
					{
						DataRow dr = dt.NewRow();
						for (DataColumn dc : tempDT.Columns)
						{
							if (dc.ColumnName.indexOf("_Id") != -1)
								continue;
							try
							{
								Object obj = mydr.getValue(dc.ColumnName);
								dr.put(dc.ColumnName, obj);
							} catch (RuntimeException ex)
							{
								throw new RuntimeException("xml 配置错误，多个文件中的属性不对称。"
										+ ex.getMessage());
							}
						}

						dt.Rows.add(dr);
					}
				}


			} catch (IOException e) {
				e.printStackTrace();
			}
			Cache.AddObj(this.getTname(), Depositary.Application, dt);
			return dt;
		}
		/* 说明这个是目录 */
		java.io.File di = new java.io.File(this.getFile());
		if (di.exists() == false)
		{
			throw new RuntimeException("文件不存在:" + this.getFile());
		}

		List<File> fis = getListFiles(this.getFile(), "xml", true);
		if (null == fis || fis.size() == 0)
		{
			fis = getListFiles(this.getFile(), "txt", true);
			return this.GetTableTxts(fis);
		}

		DataTable dt = new DataTable(this.getTableName());
		if (fis.size() == 0)
		{
			return dt;
		}

		DataTable tempDT = new DataTable();
		for (java.io.File fi : fis)
		{

			DataSet ds = new DataSet("myds");
			try
			{
				ds.readXml(this.getFile() + fi.getName());
			}
			catch (RuntimeException ex)
			{
				throw new RuntimeException("读取文件:" + fi.getName() + "错误。Exception=" + ex.getMessage());
			}
			try
			{
				//ds.
				if (dt.Columns.size() == 0)
				{
					/* 如果表还是空的，没有任何结构。*/
					try
					{
						dt = ds.GetTableByName(this.getTableName());
					}
					catch (RuntimeException ex)
					{
						throw new RuntimeException("可能是没有在" + fi.getName() + "文件中找到表:" + this.getTableName() + " exception=" + ex.getMessage());
					}
					tempDT = dt;
					continue;
				}

				DataTable mydt = ds.GetTableByName(this.getTableName());
				if (mydt == null)
				{
					throw new RuntimeException("无此表:" + this.getTableName());
				}

				if (mydt.Rows.size() == 0)
				{
					continue;
				}

				for (DataRow mydr : mydt.Rows)
				{
					//dt.ImportRow(mydr);
					DataRow dr = dt.NewRow();

					for (DataColumn dc : tempDT.Columns)
					{
						//String "sd";
						if (dc.ColumnName.indexOf("_Id") != -1)
						{
							continue;
						}

						try
						{
							Object obj = mydr.getValue(dc.ColumnName);
							dr.setValue(dc.ColumnName, obj);
						}
						catch (RuntimeException ex)
						{
							throw new RuntimeException("xml 配置错误，多个文件中的属性不对称。" + ex.getMessage());
						}
					}

					dt.Rows.add(dr);
				}
			}
			catch (RuntimeException ex)
			{
				throw new RuntimeException("获取数据出现错误:fileName=" + fi.getName() + " clasName=" + this.toString() + " MoreInfo=" + ex.getMessage());
			}
		}
		Cache.AddObj(this.getTname(), Depositary.Application, dt);
		return dt;
	}
	public int RetrieveAllFromDBSource() throws Exception {
		Cache.RemoveObj(this.getTname());
		return this.RetrieveAll();
	}
	/**
	 装载XML
	 */
	public int RetrieveAll() throws Exception
	{
		this.clear(); // 清所有的信息。
		Object tempVar = Cache.GetObj(this.toString(), Depositary.Application);
		XmlEns ens = tempVar instanceof XmlEns ? (XmlEns)tempVar : null;
		if (ens != null)
		{
			for (XmlEn en : ens.ToJavaListXmlEns())
			{
				this.Add(en);
			}
			return ens.size();
		}

		// 从内存中找。
		DataTable dt = this.GetTable();
		for (DataRow dr : dt.Rows)
		{
			XmlEn en = this.getNewEntity();
			en.setRow(new Row());
			en.getRow().LoadDataTable(dt, dr);
			this.Add(en);
		}

		Cache.AddObj(this.toString(), Depositary.Application, this);
		return dt.Rows.size();
	}
	public final void FullEnToCache(String pk) throws Exception {
		this.RetrieveAll();
		XmlEn myen = this.getNewEntity();
		for (XmlEn en : this.ToJavaListXmlEns())
		{
			Cache.AddObj(myen.toString() + en.GetValByKey(pk), Depositary.Application, en);
		}
	}
	/**
	 * param path
	 *            文件路径
	 * param suffix
	 *            后缀名, 为空则表示所有文件
	 * param isdepth
	 *            是否遍历子目录
	 * @return list
	 */
	private List<File> getListFiles(String path, String suffix, boolean isdepth)
	{
		List<File> lstFileNames = new ArrayList<File>();
		File file = new File(path);
		return listFile(lstFileNames, file, suffix, isdepth);
	}
	public final int RetrieveByLength(String key, int len) throws Exception {
		this.clear(); //清所有的信息
		DataTable dt = this.GetTable();
		int i = 0;
		for (DataRow dr : dt.Rows)
		{
			if (dr.getValue(key).toString().length() == len)
			{
				XmlEn en = this.getNewEntity();
				en.setRow(new Row());
				en.getRow().LoadDataTable(dt, dr);
				this.Add(en);
				i++;
			}
		}
		return i;
	}
	public final int Retrieve(String key, Object val) throws Exception {
		return RetrieveBy(key, val);
	}
	public final int Retrieve(String key, Object val, String key1, String val1) throws Exception {
		this.clear(); //清所有的信息
		DataTable dt = this.GetTable();
		if (dt == null)
		{
			throw new RuntimeException("@错误：类" + this.getNewEntity().toString() + " File= " + this.getFile() + " Table=" + this.getTableName() + " ，没有取到数据。");
		}

		int i = 0;
		try
		{
			for (DataRow dr : dt.Rows)
			{
				if (dr.getValue(key).toString().equals(val.toString()) && dr.getValue(key1).toString().equals(val1))
				{
					XmlEn en = this.getNewEntity();
					en.setRow(new Row());
					en.getRow().LoadDataTable(dt, dr);
					this.Add(en);
					i++;
				}
			}
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("@装载错误:file=" + this.getFile() + " xml:" + this.toString() + "Err:" + ex.getMessage());
		}
		return i;
	}
	private List<File> listFile(List<File> lstFileNames, File f, String suffix,
								boolean isdepth)
	{
		// 若是目录, 采用递归的方法遍历子目录
		if (f.isDirectory())
		{
			File[] t = f.listFiles();
			for (int i = 0; i < t.length; i++)
			{
				if (isdepth || t[i].isFile())
				{
					listFile(lstFileNames, t[i], suffix, isdepth);
				}
			}
		} else
		{
			String filePath = f.getAbsolutePath();
			if (!suffix.equals(""))
			{
				int begIndex = filePath.lastIndexOf("."); // 最后一个.(即后缀名前面的.)的索引
				String tempsuffix = "";

				if (begIndex != -1)
				{
					tempsuffix = filePath.substring(begIndex + 1,
							filePath.length());
					if (tempsuffix.equals(suffix))
					{
						lstFileNames.add(f);
					}
				}
			} else
			{
				lstFileNames.add(f);
			}
		}
		return lstFileNames;
	}

	/**
	 按照键值查询

	 param key 要查询的健
	 param val 值
	 @return 返回查询的个数
	 */
	public final int RetrieveBy(String key, Object val) throws Exception {
		if (val == null)
		{
			return 0;
		}

		this.clear(); //清所有的信息
		DataTable dt = this.GetTable();
		if (dt == null)
		{
			throw new RuntimeException("@错误：类" + this.getNewEntity().toString() + " File= " + this.getFile() + " Table=" + this.getTableName() + " ，没有取到数据。");
		}

		int i = 0;
		for (DataRow dr : dt.Rows)
		{
			if (dr.getValue(key).toString().equals(val.toString()))
			{
				XmlEn en = this.getNewEntity();
				en.setRow(new Row());
				en.getRow().LoadDataTable(dt, dr);
				this.Add(en);
				i++;
			}
		}
		return i;
	}

	public final int RetrieveBy(String key, Object val, String orderByAttr) throws Exception {
		DataTable dt = this.GetTable();

		this.clear(); //清所有的信息.
		int i = 0;
		for (DataRow dr : dt.Rows)
		{
			if (dr.getValue(key).toString().equals(val.toString()))
			{
				XmlEn en = this.getNewEntity();
				en.setRow(new Row());
				en.getRow().LoadDataTable(dt, dr);
				this.Add(en);
				i++;
			}
		}
		return i;
	}

	///#endregion


	///#region 公共方法
	public final XmlEn Find(String key, Object val) throws Exception {
		for (XmlEn en : this.ToJavaListXmlEns())
		{
			if (val.toString().equals(en.GetValStringByKey(key)))
			{
				return en;
			}
		}
		return null;

	}
	public final boolean IsExits(String key, Object val) throws Exception {
		for (XmlEn en : this.ToJavaListXmlEns())
		{
			if (val.toString().equals(en.GetValStringByKey(key)))
			{
				return true;
			}
		}
		return false;
	}

	///#endregion



	///#region  增加 便利访问
	public final XmlEn GetEnByKey(String key, String val) throws Exception {
		for (XmlEn en : this.ToJavaListXmlEns())
		{
			if (en.GetValStringByKey(key).equals(val))
			{
				return en;
			}
		}
		return null;

	}

	/**
	 获取数据
	 */
	public final XmlEn get(String key, String val) throws Exception {
		for (XmlEn en : this.ToJavaListXmlEns())
		{
			if (en.GetValStringByKey(key).equals(val))
			{
				return en;
			}
		}
		throw new RuntimeException("在[" + this.getTableName() + "," + this.getFile() + "," + this.toString() + "]没有找到key=" + key + ", val=" + val + "的实例。");
	}
	/**
	 增加一个xml en to Ens.

	 param entity
	 @return
	 */
	public int Add(XmlEn entity)
	{
		this.add(entity);
		return this.size();
	}
	public int Add(XmlEns ens) throws Exception {
		if (ens == null)
		{
			return 0;
		}

		for (XmlEn en : ens.ToJavaListXmlEns())
		{
			this.add(en);
		}
		return ens.size();
	}

	///#endregion


	///#region 与 entities 接口
	/**
	 把数据装入一个实例集合中（把xml数据装入物理表中）

	 param ens 实体集合
	 */
	public final void FillXmlDataIntoEntities(Entities ens) throws Exception {
		this.RetrieveAll(); // 查询出来全部的数据。
		Entity en1 = ens.getNewEntity();

		String[] pks = en1.getPKs();
		for (XmlEn xmlen : this.ToJavaListXmlEns())
		{

			Entity en = ens.getNewEntity();
			for (String pk : pks)
			{
				Object obj = xmlen.GetValByKey(pk);
				en.SetValByKey(pk, obj);
			}

			try
			{
				en.RetrieveFromDBSources();
			}
			catch (RuntimeException ex)
			{
				en.CheckPhysicsTable();
				Log.DebugWriteError(ex.getMessage());
			}


			for (String s : xmlen.getRow().keySet())
			{
				Object obj = xmlen.GetValByKey(s);
				if (obj == null)
				{
					continue;
				}
				if (obj.toString().equals(""))
				{
					continue;
				}
				if (obj.toString().equals("None"))
				{
					continue;
				}
				if (obj == null)
				{
					continue;
				}
				en.SetValByKey(s, obj);
			}
			en.Save();
		}
	}
	public final void FillXmlDataIntoEntities() throws Exception {
		if (this.getRefEns() == null)
		{
			return;
		}
		this.FillXmlDataIntoEntities(this.getRefEns());
	}

	///#endregion



	///#region 子类实现xml 信息的描述.
	public abstract XmlEn getNewEntity();
	/**
	 获取它所在的xml file 位置.
	 */
	public abstract String getFile() ;
	/**
	 物理表名称(可能一个xml文件中有n个Table.)
	 */
	public abstract String getTableName();
	public abstract Entities getRefEns();

	///#endregion

	public final DataTable ToDataTable() throws Exception {
		DataTable dt = new DataTable(this.getTableName());

		if (this.size() == 0)
		{
			return dt;
		}

		XmlEn en = this.get(0) instanceof XmlEn ? (XmlEn)this.get(0) : null;
		Row r = en.getRow();
		for (String key : r.keySet())
		{
			dt.Columns.Add(key, String.class);
		}

		for (XmlEn en1 : this.ToJavaListXmlEns())
		{
			DataRow dr = dt.NewRow();
			for (String key : r.keySet())
			{
				dr.setValue(key, en1.GetValStringByKey(key));
			}
			dt.Rows.add(dr);
		}

		return dt;
	}
	public List<XmlEn> ToJavaListXmlEns()throws Exception
	{
		return (List<XmlEn>)(Object)this;
	}
}