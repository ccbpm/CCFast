package bp.wf.httphandler;

import bp.ccbill.FrmBill;
import bp.ccfast.ccmenu.Module;
import bp.ccfast.ccmenu.MySystem;
import bp.ccfast.ccmenu.MySystemAttr;
import bp.da.*;
import bp.da.DataTableConvertJson;
import bp.difference.StringHelper;
import bp.difference.handler.CommonFileUtils;
import bp.difference.handler.CommonUtils;
import bp.difference.handler.DirectoryPageBase;
import bp.en.*;
import bp.port.*;
import bp.sys.*;
import bp.sys.xml.ActiveAttrAttr;
import bp.sys.xml.ActiveAttrs;
import bp.sys.xml.EnumInfoXml;
import bp.sys.xml.SQLList;
import bp.tools.*;
import bp.web.GuestUser;
import bp.web.WebUser;
import bp.wf.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.io.*;
import java.math.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import bp.en.Map;
import bp.wf.Glo;
import bp.difference.*;
import bp.wf.template.FlowSort;
import bp.wf.template.FlowSortAttr;
import bp.wf.template.SysFormTree;
import com.aspose.words.FieldNext;
import net.sf.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 页面功能实体
*/
public class WF_Comm extends bp.difference.handler.DirectoryPageBase
{
		///#region 树的实体.
	/**
	 获得树的结构

	 @return
	*/
	public final String Tree_Init() throws Exception {
		Object tempVar = ClassFactory.GetEns(this.getEnsName());
		EntitiesTree ens = tempVar instanceof EntitiesTree ? (EntitiesTree)tempVar : null;
		if (ens == null)
		{
			return "err@该实体[" + this.getEnsName() + "]不是一个树形实体.";
		}
		//获取ParentNo
		ens.RetrieveAll(EntityTreeAttr.Idx);

		return bp.tools.Json.ToJson(ens.ToDataTableField("TreeTable"));
	}
	///#region 部门-人员关系.

	public final String Tree_MapBaseInfo() throws Exception {
		Object tempVar = ClassFactory.GetEns(this.getTreeEnsName());
		EntitiesTree enTrees = tempVar instanceof EntitiesTree ? (EntitiesTree)tempVar : null;
		bp.en.Entity tempVar2 = enTrees.getNewEntity();
		EntityTree enenTree = tempVar2 instanceof EntityTree ? (EntityTree)tempVar2 : null;
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		Entity en = ens.getNewEntity();
		Hashtable ht = new Hashtable();
		ht.put("TreeEnsDesc", enenTree.getEnDesc());
		ht.put("EnsDesc", en.getEnDesc());
		ht.put("EnPK", en.getPK());
		return bp.tools.Json.ToJson(ht);
	}

	/**
	 获得树的结构

	 @return
	*/
	public final String TreeEn_Init() throws Exception {
		Object tempVar = ClassFactory.GetEns(this.getTreeEnsName());
		EntitiesTree ens = tempVar instanceof EntitiesTree ? (EntitiesTree)tempVar : null;
		ens.RetrieveAll(EntityTreeAttr.Idx);
		return ens.ToJsonOfTree("0");
	}

	/**
	 获取树关联的集合

	 @return
	*/
	public final String TreeEmp_Init() throws Exception {
		DataSet ds = new DataSet();
		String RefPK = this.GetRequestVal("RefPK");
		String FK = this.GetRequestVal("FK");
		//获取关联的信息集合
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		ens.RetrieveByAttr(RefPK, FK);
		DataTable dt = ens.ToDataTableField("GridData");
		ds.Tables.add(dt);

		//获取实体对应的列明
		Entity en = ens.getNewEntity();
		Map map = en.getEnMapInTime();
		MapAttrs attrs = map.getAttrs().ToMapAttrs();
		//属性集合.
		DataTable dtAttrs = attrs.ToDataTableField("dt");
		dtAttrs.TableName = "Sys_MapAttrs";

		dt = new DataTable("Sys_MapAttr");
		dt.Columns.Add("field", String.class);
		dt.Columns.Add("title", String.class);
		dt.Columns.Add("Width", Integer.class);
		dt.Columns.Add("UIContralType", Integer.class);
		bp.da.DataRow row = null;
		for (MapAttr attr : attrs.ToJavaList())
		{
			if (attr.getUIVisible() == false)
			{
				continue;
			}

			if (Objects.equals(attr.getKeyOfEn(), this.getRefPK()))
			{
				continue;
			}

			row = dt.NewRow();
			row.setValue("field", attr.getKeyOfEn());
			row.setValue("title", attr.getName());
			row.setValue("Width", attr.getUIWidthInt() * 2);
			row.setValue("UIContralType", attr.getUIContralType().getValue());

			if (attr.getHisAttr().getItIsFKorEnum())
			{
				row.setValue("field", attr.getKeyOfEn() + "Text");
			}
			dt.Rows.add(row);
		}

		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}
		///#endregion 部门-人员关系
	/**
	 构造函数
	*/
	public WF_Comm()
	{
	}


	public final void setDeptNo(String value)
	{
		String val = value;
		if (Objects.equals(val, "all"))
		{
			return;
		}

		if (this.getDeptNo() == null)
		{
			this.setDeptNo(value);
			return;
		}
	}


//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
		///#region 统计分析组件.
	/**
	 初始化数据

	 @return
	*/
	public final String ContrastDtl_Init() throws Exception {
		//获得.
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		Entity en = ens.getNewEntity();
		Map map = en.getEnMapInTime();

		MapAttrs attrs = map.getAttrs().ToMapAttrs();

		//属性集合.
		DataTable dtAttrs = attrs.ToDataTableField("dt");
		dtAttrs.TableName = "Sys_MapAttrs";

		DataSet ds = new DataSet();
		ds.Tables.add(dtAttrs); //把描述加入.

		//增加分组的查询条件
		UserRegedit ur = new UserRegedit();
		ur.setMyPK(WebUser.getNo() + "_" + this.getEnsName() + "_SearchAttrs");
		ur.RetrieveFromDBSources();
		AtPara ap = new AtPara(ur.getVals());
		String vals = "";
		for(String str : ap.getHisHT().keySet())
		{
			String val = this.GetRequestVal(str);
			if (DataType.IsNullOrEmpty(val) == false)
			{
				vals += "@" + str + "=" + val;
			}
			else
			{
				vals += "@" + str + "=" + ap.getHisHT().get(str);
			}
		}
		ur.SetValByKey(UserRegeditAttr.Vals, vals);
		//查询结果
		QueryObject qo = Search_Data(ens, en, map, ur, "ContrastDtl");
		//获取配置信息
		EnCfg encfg = new EnCfg();
		encfg.setNo(this.getEnsName());
		encfg.RetrieveFromDBSources();

		//增加排序
		String orderBy = "";
		boolean isDesc = false;
		if (DataType.IsNullOrEmpty(ur.getOrderBy()) == false)
		{
			orderBy = ur.getOrderBy();
			isDesc = ur.getOrderWay().equals("desc") == true ? true : false;
		}

		if (DataType.IsNullOrEmpty(ur.getOrderBy()) == true && encfg != null)
		{
			orderBy = encfg.GetValStrByKey("OrderBy");
			if (orderBy.indexOf(",") != -1)
			{
				String[] str = orderBy.split("[,]", -1);
				orderBy = str[0];
			}
			isDesc = encfg.GetValBooleanByKey("IsDeSc");
		}

		if (DataType.IsNullOrEmpty(orderBy) == false)
		{
			try
			{
				if (isDesc)
				{
					qo.addOrderByDesc(orderBy);
				}
				else
				{
					qo.addOrderBy(orderBy);
				}
			}
			catch (RuntimeException ex)
			{
				encfg.SetValByKey("OrderBy", orderBy);
			}
		}


		qo.DoQuery();

		DataTable dt = ens.ToDataTableField("dt");
		dt.TableName = "Group_Dtls";
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}

	/**
	 执行导出

	 @return
	*/
	//public string GroupDtl_Exp()
	//{
	//    //获得.
	//    Entities ens = ClassFactory.GetEns(this.EnsName);
	//    Entity en = ens.getNewEntity();

	//    //查询结果
	//    QueryObject qo = new QueryObject(ens);
	//    string[] strs = HttpContextHelper.Request.Form.ToString().Split('&');
	//    foreach (string str in strs)
	//    {
	//        if (str.IndexOf("EnsName") != -1)
	//            continue;

	//        string[] mykey = str.Split('=');
	//        string key = mykey[0];

	//        if (key == "OID" || key == "MyPK")
	//            continue;

	//        if (key == "FK_Dept")
	//        {
	//            this.DeptNo = mykey[1];
	//            continue;
	//        }

	//        bool isExist = false;
	//        bool IsInt = false;
	//        bool IsDouble = false;
	//        bool IsFloat = false;
	//        bool IsMoney = false;
	//        foreach (Attr attr in en.getEnMap().getAttrs())
	//        {
	//            if (attr.getKey().Equals(key))
	//            {
	//                isExist = true;
	//                if (attr.getMyDataType() == DataType.AppInt)
	//                    IsInt = true;
	//                if (attr.getMyDataType() == DataType.AppDouble)
	//                    IsDouble = true;
	//                if (attr.getMyDataType() == DataType.AppFloat)
	//                    IsFloat = true;
	//                if (attr.getMyDataType() == DataType.AppMoney)
	//                    IsMoney = true;
	//                break;
	//            }
	//        }

	//        if (isExist == false)
	//            continue;

	//        if (mykey[1] == "mvals")
	//        {
	//            //如果用户多项选择了，就要找到它的选择项目.

	//            UserRegedit sUr = new UserRegedit();
	//            sUr.setMyPK(WebUser.getNo() + this.EnsName + "_SearchAttrs";
	//            sUr.RetrieveFromDBSources();

	//            /* 如果是多选值 */
	//            string cfgVal = sUr.MVals;
	//            AtPara ap = new AtPara(cfgVal);
	//            string instr = ap.GetValStrByKey(key);
	//            string val = "";
	//            if (instr == null || instr == "")
	//            {
	//                if (key == "FK_Dept" || key == "FK_Unit")
	//                {
	//                    if (key == "FK_Dept")
	//                        val = WebUser.DeptNo;
	//                }
	//                else
	//                {
	//                    continue;
	//                }
	//            }
	//            else
	//            {
	//                instr = instr.Replace("..", ".");
	//                instr = instr.Replace(".", "','");
	//                instr = instr.Substring(2);
	//                instr = instr.Substring(0, instr.Length - 2);
	//                qo.AddWhereIn(mykey[0], instr);
	//            }
	//        }
	//        else
	//        {
	//            if (IsInt == true && DataType.IsNullOrEmpty(mykey[1]) == false)
	//                qo.AddWhere(mykey[0], Int32.Parse(mykey[1]));
	//            else if (IsDouble == true && DataType.IsNullOrEmpty(mykey[1]) == false)
	//                qo.AddWhere(mykey[0], double.Parse(mykey[1]));
	//            else if (IsFloat == true && DataType.IsNullOrEmpty(mykey[1]) == false)
	//                qo.AddWhere(mykey[0], float.Parse(mykey[1]));
	//            else if (IsMoney == true && DataType.IsNullOrEmpty(mykey[1]) == false)
	//                qo.AddWhere(mykey[0], decimal.Parse(mykey[1]));
	//            else
	//                qo.AddWhere(mykey[0], mykey[1]);
	//        }
	//        qo.addAnd();
	//    }

	//    if (this.DeptNo != null && (this.GetRequestVal("FK_Emp") == null
	//        || this.GetRequestVal("FK_Emp") == "all"))
	//    {
	//        if (this.DeptNo.Length == 2)
	//        {
	//            qo.AddWhere("FK_Dept", " = ", "all");
	//            qo.addAnd();
	//        }
	//        else
	//        {
	//            if (this.DeptNo.Length == 8)
	//            {
	//                qo.AddWhere("FK_Dept", " = ", this.DeptNo);
	//            }
	//            else
	//            {
	//                qo.AddWhere("FK_Dept", " like ", this.DeptNo + "%");
	//            }

	//            qo.addAnd();
	//        }
	//    }

	//    qo.AddHD();

	//    DataTable dt = qo.DoQueryToTable();

	//    string filePath = ExportDGToExcel(dt, en, en.getEnDesc());


	//    return filePath;
	//}
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
		///#endregion 统计分析组件.

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
		///#region Entity 公共类库.
	/**
	 实体类名
	*/
	public final String getEnName()
	{
		return this.GetRequestVal("EnName");
	}
	/**
	 获得实体

	 @return
	*/
	public final String Entity_Init() throws Exception {
		Entity en = ClassFactory.GetEn(this.getEnName());
		try
		{
			String pkval = this.getPKVal();

			if (en == null)
			{
				return "err@类" + this.getEnName() + "不存在,请检查是不是拼写错误";
			}
			if (DataType.IsNullOrEmpty(pkval) == true || pkval.equals("0") || pkval.equals("undefined"))
			{
				Map map = en.getEnMap();
				for (Attr attr : en.getEnMap().getAttrs())
				{
					en.SetValByKey(attr.getKey(), attr.getDefaultVal());
				}
				//设置默认的数据.
				en.ResetDefaultVal(null, null, 0);
			}
			else
			{
				en.setPKVal(pkval);
				en.Retrieve();
				//int i=en.RetrieveFromDBSources();
				//if (i == 0)
				//  return "err@实体:["+"]";
			}

			return en.ToJson(false);
		}
		catch (RuntimeException ex)
		{
			en.CheckPhysicsTable();
			return "err@" + ex.getMessage();
		}
	}
	/**
	 删除

	 @return
	*/
	public final String Entity_Delete()
	{
		try
		{
			Entity en = ClassFactory.GetEn(this.getEnName());
			if (en == null)
			{
				return "err@类" + this.getEnName() + "不存在,请检查是不是拼写错误";
			}
				///#region 首先判断参数删除.
			String key1 = this.GetRequestVal("Key1");
			String val1 = this.GetRequestVal("Val1");

			String key2 = this.GetRequestVal("Key2");
			String val2 = this.GetRequestVal("Val2");
			Attrs attrs = en.getEnMap().getAttrs();

			if (DataType.IsNullOrEmpty(key1) == false && key1.equals("undefined") == false)
			{
				int num = 0;
				if (DataType.IsNullOrEmpty(key2) == false && key2.equals("undefined") == false)
				{
					if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
					{
						num = en.Delete(key1, bp.sys.base.Glo.GenerRealType(attrs, key1, val1), key2, bp.sys.base.Glo.GenerRealType(attrs, key2, val2));
					}
					else
					{
						num = en.Delete(key1, val1, key2, val2);
					}
				}
				else
				{
					if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
					{
						num = en.Delete(key1, bp.sys.base.Glo.GenerRealType(attrs, key1, val1));
					}
					else
					{
						num = en.Delete(key1, val1);
					}
				}
				return String.valueOf(num);
			}
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
				///#endregion 首先判断参数删除.

			/* 不管是个主键，还是单个主键，都需要循环赋值。*/
			for (Attr attr : en.getEnMap().getAttrs())
			{
				en.SetValByKey(attr.getKey(), this.GetRequestVal(attr.getKey()));
			}

			if (en.getPKCount() != 1)
			{
				int i = en.RetrieveFromDBSources(); //查询出来再删除.
				return String.valueOf(en.Delete()); //返回影响行数.
			}
			else
			{
				String pkval = en.getPKVal().toString();
				if (DataType.IsNullOrEmpty(pkval) == true)
				{
					en.setPKVal(this.getPKVal());
				}

				int num = en.RetrieveFromDBSources();
				en.Delete();

				return "删除成功.";
				// int i = en.RetrieveFromDBSources(); //查询出来再删除.
				//return en.Delete().ToString(); //返回影响行数.
			}


			// int i = en.RetrieveFromDBSources(); //查询出来再删除.
			//return en.Delete().ToString(); //返回影响行数.
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 更新

	 @return
	*/
	public final String Entity_Update()
	{
		try
		{
			Entity en = ClassFactory.GetEn(this.getEnName());
			if (en == null)
			{
				return "err@类" + this.getEnName() + "不存在,请检查是不是拼写错误";
			}
			en.setPKVal(this.getPKVal());
			en.RetrieveFromDBSources();

			//遍历属性，循环赋值.
			for (Attr attr : en.getEnMap().getAttrs())
			{
				en.SetValByKey(attr.getKey(), this.GetRequestVal(attr.getKey()));
			}
			//对于字段附件需要更新附件秒速表的名称字段
			if(String.valueOf(en.GetValByKey(MapAttrAttr.UIContralType)).equals(String.valueOf(UIContralType.AthShow.getValue()))){
				FrmAttachment athDesc = new FrmAttachment();
				athDesc.setMyPK(this.GetRequestVal("MyPK"));
				int count=athDesc.RetrieveFromDBSources();
				if(count>0){
					athDesc.SetValByKey(FrmAttachmentAttr.Name,this.GetRequestVal(FrmAttachmentAttr.Name));
					athDesc.Update();
				}
			}

			//返回数据.
			//return en.ToJson(false);
			en.setPKVal(this.getPKVal());

			return String.valueOf(en.Update()); //返回影响行数.
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 从数据源查询.

	 @return
	*/
	public final String Entity_RetrieveFromDBSources()
	{
		try
		{
			Entity en = ClassFactory.GetEn(this.getEnName());
			if (en == null)
			{
				return "err@类" + this.getEnName() + "不存在,请检查是不是拼写错误";
			}
			en.setPKVal(this.getPKVal());
			int i = en.RetrieveFromDBSources();

			if (i == 0)
			{
				en.ResetDefaultVal(null, null, 0);
				en.setPKVal(this.getPKVal());
			}

			if (en.getRow().containsKey("RetrieveFromDBSources") == true)
			{
				en.getRow().SetValByKey("RetrieveFromDBSources", i);
			}
			else
			{
				en.getRow().put("RetrieveFromDBSources", i);
			}

			return en.ToJson(false);
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 从数据源查询.

	 @return
	*/
	public final String Entity_Retrieve()
	{
		try
		{
			Entity en = ClassFactory.GetEn(this.getEnName());
			if (en == null)
			{
				return "err@类" + this.getEnName() + "不存在,请检查是不是拼写错误";
			}

			en = en.CreateInstance();
			en.setPKVal(this.getPKVal());
			en.Retrieve();

			if (en.getRow().containsKey("Retrieve") == true)
			{
				en.getRow().SetValByKey("Retrieve", "1");
			}
			else
			{
				en.getRow().put("Retrieve", "1");
			}

			return en.ToJson(false);
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 是否存在

	 @return
	*/
	public final String Entity_IsExits()
	{
		try
		{
			Entity en = ClassFactory.GetEn(this.getEnName());
			if (en == null)
			{
				return "err@类" + this.getEnName() + "不存在,请检查是不是拼写错误";
			}

			en.setPKVal(this.getPKVal());
			boolean isExit = en.getIsExits();
			if (isExit == true)
			{
				return "1";
			}
			return "0";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 执行保存

	 @return 返回保存影响的行数
	*/
	public final String Entity_Save()
	{
		try
		{
			Entity en = ClassFactory.GetEn(this.getEnName());
			if (en == null)
			{
				return "err@实体类名错误[" + this.getEnName() + "].";
			}

			en.setPKVal(this.getPKVal());
			en.RetrieveFromDBSources();

			//遍历属性，循环赋值.
			for (Attr attr : en.getEnMap().getAttrs())
			{
				en.SetValByKey(attr.getKey(), this.GetRequestVal(attr.getKey()));
			}

			//保存参数属性.
			String frmParas = URLDecoder.decode(this.GetValFromFrmByKey("frmParas", ""),"UTF-8");
			if (DataType.IsNullOrEmpty(frmParas) == false)
			{
				AtPara ap = new AtPara(frmParas);
				for (String key : ap.getHisHT().keySet())
				{
					en.SetPara(key, ap.GetValStrByKey(key));
				}
			}

			return String.valueOf(en.Save());
		}
		catch (Exception ex)
		{
			return "err@保存错误:" + ex.getMessage();
		}
	}
	/**
	 执行插入.

	 @return
	*/
	public final String Entity_Insert()
	{
		try
		{
			Entity en = ClassFactory.GetEn(this.getEnName());

			//遍历属性，循环赋值.
			for (Attr attr : en.getEnMap().getAttrs())
			{
				en.SetValByKey(attr.getKey(), this.GetRequestVal(attr.getKey()));
			}

			//插入数据库.
			int i = en.Insert();
			if (i == 1)
			{
				en.Retrieve(); //执行查询.
			}

			//返回数据.
			return en.ToJson(false);
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	/**
	 执行插入.

	 @return
	*/
	public final String Entity_DirectInsert()
	{
		try
		{
			Entity en = ClassFactory.GetEn(this.getEnName());
			if (en == null)
			{
				return "err@类" + this.getEnName() + "不存在,请检查是不是拼写错误";
			}
			//遍历属性，循环赋值.
			for (Attr attr : en.getEnMap().getAttrs())
			{
				en.SetValByKey(attr.getKey(), this.GetRequestVal(attr.getKey()));
			}

			//插入数据库.
			int i = en.DirectInsert();
			if (i == 1)
			{
				en.Retrieve(); //执行查询.
			}

			//返回数据.
			return en.ToJson(false);
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	/**
	 查询

	 @return
	*/
	public final String Entity_DoMethodReturnString() throws Exception {
		//创建类实体.
		Entity en = ClassFactory.GetEn(this.getEnName());
		if (en == null)
			return "err@类" + this.getEnName() + "不存在,请检查是不是拼写错误";
		en.setPKVal(this.getPKVal());
		en.RetrieveFromDBSources();

		String methodName = this.GetRequestVal("MethodName");
		String paras = this.GetRequestVal("paras");
		if(DataType.IsNullOrEmpty(paras)){
			paras = this.GetRequestVal("Paras");
		}
		java.lang.Class tp = en.getClass();
		java.lang.reflect.Method mp = null;
		for (java.lang.reflect.Method m : tp.getMethods()) {
			//找出名称和参数个数相同的方法
			if (m.getName().equals(methodName)==true) {
				String[] str =new String[0];
				if (DataType.IsNullOrEmpty(paras) == false) {
					//前台传入的参数以 ～  进行分隔，如果以～ 结尾，则必须追加一个空参数（和前端对应）
					str = paras.split("\\[～\\]");
					if(paras.endsWith("[～]")){
						str = Arrays.copyOf(str, str.length + 1);
						str[str.length - 1] = "";
					}
				}
				Class[] paramTypes =m.getParameterTypes();
				if(paramTypes.length==str.length){
					mp = m;
					break;
				}
			}
		}
		if (mp == null)
		{
			return "err@没有找到类[" + this.getEnName() + "]方法[" + methodName + "].";
		}



		//执行该方法.
		Object[] myparas = new Object[0];

		if (DataType.IsNullOrEmpty(paras) == false)
		{
			String[] str = paras.split("\\[～\\]");


			int idx = 0;
			Class[] paramTypes =mp.getParameterTypes();
			myparas = new Object[paramTypes.length];

			for (Class paramInfo : paramTypes)
			{
				String val = "";
				if(idx<str.length){
					val = str[idx];
					myparas[idx] = str[idx];
				}
				try
				{
					if (paramInfo.getName().equals("float"))
						myparas[idx] = Float.parseFloat(val);
					if (paramInfo.getName().equals("double"))
						myparas[idx] = Double.parseDouble(val);
					if (paramInfo.getName().equals("int"))
						myparas[idx] = Integer.parseInt(val);
					if (paramInfo.getName().equals("long"))
						myparas[idx] = Long.parseLong(val);
					if (paramInfo.getName().equals("java.math.BigDecimal"))
						myparas[idx] = new BigDecimal(Double.parseDouble(val));
					if (paramInfo.getName().equals("boolean"))
					{
						if (str[idx].toLowerCase().equals("true") || str[idx].equals("1"))
							myparas[idx] = true;
						else
							myparas[idx] = false;
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException("err@类[" + this.getEnName() + "]方法[" + methodName + "]值" + str[idx] + "转换成" + paramInfo.getName()+ "失败");
				}

				idx++;
			}
		}
		Object tempVar = mp.invoke(en, myparas);
		String result = tempVar instanceof String ? (String)tempVar : null; //调用由此 MethodInfo 实例反射的方法或构造函数。
		return result;
	}
	///#region Entities 公共类库.
	/**
	 调用参数.
	*/
	public final String getParas()
	{
		return this.GetRequestVal("Paras");
	}
	/**
	 查询全部

	 @return
	*/
	public final String Entities_RetrieveAll() throws Exception {
		try
		{
			Entities ens = ClassFactory.GetEns(this.getEnsName());
			if (ens == null)
			{
				return "err@类" + this.getEnsName() + "不存在,请检查是不是拼写错误";
			}
			ens.RetrieveAll();
			return ens.ToJson("dt");
		}
		catch (Exception e)
		{
			return "err@[Entities_RetrieveAll][" + this.getEnsName() + "]类名错误，或者其他异常:" + e.getMessage();
		}
	}
	/**
	 获得实体集合s

	 @return
	*/
	public final String Entities_Init()
	{
		try
		{
			Entities ens = ClassFactory.GetEns(this.getEnsName());
			if (ens == null)
			{
				return "err@类" + this.getEnsName() + "不存在,请检查是不是拼写错误";
			}
			if (this.getParas() == null)
			{
				return "0";
			}
			return Entities_Init_Ext(ens, ens.getNewEntity(), this.getParas());
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	public final String Entities_Init_Ext(Entities ens, Entity en, String paras) throws Exception {

		QueryObject qo = new QueryObject(ens);
		String[] myparas = this.getParas().split("[@]", -1);

		Attrs attrs = en.getEnMap().getAttrs();

		int idx = 0;
		for (int i = 0; i < myparas.length; i++)
		{
			String para = myparas[i];
			if (DataType.IsNullOrEmpty(para) || para.contains("=") == false)
			{
				continue;
			}

			String[] strs = para.split("[=]", -1);
			String key = strs[0];
			String val = strs[1];
			val = val.replace("~","@");

			if (key.toLowerCase().equals("orderby") == true)
			{
				//多重排序
				if (val.indexOf(",") != -1)
				{
					String[] strs1 = val.split("[,]", -1);
					for (String str : strs1)
					{
						if (DataType.IsNullOrEmpty(str) == true)
						{
							continue;
						}
						if (str.toUpperCase().indexOf("DESC") != -1)
						{
							String str1 = str.replace("DESC", "").replace("desc", "");
							qo.addOrderByDesc(str1.trim());
						}
						else
						{
							if (str.toUpperCase().indexOf("ASC") != -1)
							{
								String str1 = str.replace("ASC", "").replace("asc", "");
								qo.addOrderBy(str1.trim());
							}
							else
							{
								qo.addOrderBy(str.trim());
							}
						}

					}
				}
				else
				{
					qo.addOrderBy(val);
				}

				continue;
			}

			if (attrs.contains(key) == false)
			{
				continue;
			}

			Attr attr= attrs.GetAttrByKey(key);
			if (attr == null)
				continue;
			Object valObj = val;

			if (SystemConfig.getAppCenterDBFieldIsParaDBType() == true)
			{
				valObj = bp.sys.base.Glo.GenerRealType(en.getEnMap().getAttrs(), key, val);
			}

			if (idx != 0)
				qo.addAnd();

			switch (attr.getMyDataType())
			{
				case DataType.AppBoolean:
				case DataType.AppInt:
					qo.AddWhere(key, Integer.parseInt(valObj.toString()));
					break;
				case DataType.AppMoney:
				case DataType.AppFloat:
				case DataType.AppDouble:
					qo.AddWhere(key, Float.parseFloat(valObj.toString()));
					break;
				default:
					qo.AddWhere(key,  valObj);
					break;
			}
			idx++;
		}
		try
		{
			qo.DoQuery();
		}
		catch (RuntimeException ex)
		{
			if (ex.getMessage().contains("exist"))
			{
				qo.DoQuery();
			}
		}
		return ens.ToJson("dt");
	}

	/**
	 获得实体集合s

	 @return
	*/
	public final String Entities_RetrieveCond()
	{
		try
		{
			Entities ens = ClassFactory.GetEns(this.getEnsName());
			if (ens == null)
			{
				return "err@类" + this.getEnsName() + "不存在,请检查是不是拼写错误";
			}
			if (this.getParas() == null)
			{
				return "0";
			}

			return Entities_RetrieveCond_Ext(ens, this.getParas());
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	public final String Entities_RetrieveCond_Ext(Entities ens, String paras) throws Exception {

		QueryObject qo = new QueryObject(ens);
		String[] myparas = paras.replace("[%]", "%").split("[@]", -1);

		Attrs attrs = ens.getNewEntity().getEnMap().getAttrs();

		int idx = 0;
		for (int i = 0; i < myparas.length; i++)
		{
			String para = myparas[i];
			if (DataType.IsNullOrEmpty(para))
			{
				continue;
			}

			String[] strs = para.split("[|]", -1);
			String key = strs[0];
			String oper = strs[1];
			String val = strs[2];

			if (key.toLowerCase().equals("orderby") == true)
			{
				qo.addOrderBy(val);
				continue;
			}

			//获得真实的数据类型.
			Object typeVal = val;
			if (SystemConfig.getAppCenterDBFieldIsParaDBType() == true)
			{
				typeVal = bp.sys.base.Glo.GenerRealType(attrs, key, val);
			}

			String[] keys = key.trim().split("[,]", -1);
			int count = 0;
			for (String str : keys)
			{
				count++;
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}
				if (idx == 0 && count == 1)
				{
					qo.AddWhere(str, oper, typeVal);
				}
				else
				{
					if (count != 1)
					{
						qo.addOr();
					}
					else
					{
						qo.addAnd();
					}
					qo.AddWhere(str, oper, typeVal);
				}

			}
			idx++;
		}

		qo.DoQuery();
		return ens.ToJson("dt");
	}
	/**
	 执行方法

	 @return
	*/
	public final String Entities_DoMethodReturnString() throws Exception {

		//创建类实体.
		Entities ens = ClassFactory.GetEns(this.getEnsName());

		String methodName = this.GetRequestVal("MethodName");
		if (ens == null)
		{
			return "err@没有找到实体类";
		}
		java.lang.Class tp = ens.getClass();
		java.lang.reflect.Method mp = null;
		for (java.lang.reflect.Method m : tp.getMethods()) {
			if (m.getName().equals(methodName)==true) {
				mp = m;
				break;
			}
		}
		if (mp == null)
		{
			return "err@没有找到类[" + this.getEnsName() + "]方法[" + methodName + "].";
		}

		String paras = this.GetRequestVal("paras");
		if ("un".equals(paras) == true || "undefined".equals(paras) == true)
		{
			paras = "";
		}

		//执行该方法.
		Object[] myparas = new Object[0];
		String atPara = GetRequestVal("atPara");

		if (DataType.IsNullOrEmpty(paras) == false)
		{
			String[] str = paras.split("[~]", -1);
			if (DataType.IsNullOrEmpty(atPara) == true)
			{
				myparas = new Object[str.length];
			}
			else
			{
				myparas = new Object[str.length + 1];
			}
			Class[] paramInfos =mp.getParameterTypes();
			int idx = 0;
			for (Class paramInfo : paramInfos)
			{
				myparas[idx] = str[idx];
				try
				{
					if (paramInfo.getSimpleName().equals("Single"))
					{
						myparas[idx] = Float.parseFloat(str[idx]);
					}
					if (paramInfo.getSimpleName().equals("Double"))
					{
						myparas[idx] = Double.parseDouble(str[idx]);
					}
					if (paramInfo.getSimpleName().equals("Int32"))
					{
						myparas[idx] = Integer.parseInt(str[idx]);
					}
					if (paramInfo.getSimpleName().equals("Int64"))
					{
						myparas[idx] = Long.parseLong(str[idx]);
					}
					if (paramInfo.getSimpleName().equals("Decimal"))
					{
						myparas[idx] = new BigDecimal(Double.parseDouble(str[idx]));
					}
					if (paramInfo.getSimpleName().equals("Boolean"))
					{
						if (str[idx].toLowerCase().equals("true") || str[idx].equals("1"))
						{
							myparas[idx] = true;
						}
						else
						{
							myparas[idx] = false;
						}
					}

				}
				catch (RuntimeException e)
				{
					throw new RuntimeException("err@类[" + this.getEnName() + "]方法[" + methodName + "]值" + str[idx] + "转换成" + paramInfo.getSimpleName() + "失败");
				}

				idx++;
			}
		}

		if (DataType.IsNullOrEmpty(atPara) == false)
		{
			if (myparas.length == 0)
			{
				myparas = new Object[1];
				myparas[0] = atPara;
			}
			else
			{
				myparas[myparas.length - 1] = atPara;
			}
		}
		Object tempVar = mp.invoke(ens, myparas);
		String result = tempVar instanceof String ? (String)tempVar : null; //调用由此 MethodInfo 实例反射的方法或构造函数。
		return result;

	}
	/**
	 初始化.

	 @return
	*/
	public final String Method_Init() throws Exception {
		String ensName = this.GetRequestVal("M");
		Method rm = ClassFactory.GetMethod(ensName);
		if (rm == null)
		{
			return "err@方法名错误或者该方法已经不存在" + ensName;
		}

		if (rm.getHisAttrs().size() == 0)
		{
			Hashtable ht = new Hashtable();
			ht.put("No", ensName);
			ht.put("Title", rm.Title);
			ht.put("Help", rm.Help);
			ht.put("Warning", rm.Warning == null ? "" : rm.Warning);
			return bp.tools.Json.ToJson(ht);
		}

		DataTable dt = new DataTable();

		//转化为集合.
		MapAttrs attrs = rm.getHisAttrs().ToMapAttrs();

		return "";
	}
	public final String Method_Done()
	{
		String ensName = this.GetRequestVal("M");
		Method rm = ClassFactory.GetMethod(ensName);
		// rm.Init();
		int mynum = 0;
		for (Attr attr : rm.getHisAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}
			mynum++;
		}
		int idx = 0;
		for (Attr attr : rm.getHisAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}
			if (attr.getUIVisible()== false)
			{
				continue;
			}
			try
			{
				switch (attr.getUIContralType())
				{
					case TB:
						switch (attr.getMyDataType())
						{
							case DataType.AppString:
							case DataType.AppDate:
							case DataType.AppDateTime:
								String str1 = this.GetValFromFrmByKey(attr.getKey());
								rm.SetValByKey(attr.getKey(), str1);
								break;
							case DataType.AppInt:
								int myInt = this.GetValIntFromFrmByKey(attr.getKey()); //int.Parse(this.UCEn1.GetTBByID("TB_" + attr.getKey()).Text);
								rm.getRow().SetValByKey(String.valueOf(idx), myInt);
								rm.SetValByKey(attr.getKey(), myInt);
								break;
							case DataType.AppFloat:
								float myFloat = this.GetValFloatFromFrmByKey(attr.getKey()); // float.Parse(this.UCEn1.GetTBByID("TB_" + attr.getKey()).Text);
								rm.SetValByKey(attr.getKey(), myFloat);
								break;
							case DataType.AppDouble:
							case DataType.AppMoney:
								BigDecimal myDoub = this.GetValDecimalFromFrmByKey(attr.getKey()); // decimal.Parse(this.UCEn1.GetTBByID("TB_" + attr.getKey()).Text);
								rm.SetValByKey(attr.getKey(), myDoub);
								break;
							case DataType.AppBoolean:
								boolean myBool = this.GetValBoolenFromFrmByKey(attr.getKey()); // decimal.Parse(this.UCEn1.GetTBByID("TB_" + attr.getKey()).Text);
								rm.SetValByKey(attr.getKey(), myBool);
								break;
							default:
								return "err@没有判断的字段数据类型．";
						}
						break;
					case DDL:
						try
						{
							String str = this.GetValFromFrmByKey(attr.getKey()); // decimal.Parse(this.UCEn1.GetTBByID("TB_" + attr.getKey()).Text);
							// string str = this.UCEn1.GetDDLByKey("DDL_" + attr.getKey()).SelectedItemStringVal;
							rm.SetValByKey(attr.getKey(), str);
						}
						catch (Exception e)
						{
							rm.SetValByKey(attr.getKey(), "");
						}
						break;
					case CheckBok:
						boolean myBoolval = this.GetValBoolenFromFrmByKey(attr.getKey()); // decimal.Parse(this.UCEn1.GetTBByID("TB_" + attr.getKey()).Text);
						rm.SetValByKey(attr.getKey(), myBoolval);
						break;
					default:
						break;
				}
				idx++;
			}
			catch (RuntimeException ex)
			{
				return "err@获得参数错误" + "attr=" + attr.getKey() + " attr = " + attr.getKey() + ex.getMessage();
			}
		}

		try
		{
			Object obj = rm.Do();
			if (obj != null)
			{
				return obj.toString();
			}
			else
			{
				return "err@执行完成没有返回信息.";
			}
		}
		catch (Exception ex)
		{
			return "err@执行错误:" + ex.getMessage();
		}
	}
	public final String MethodLink_Init()
	{
		ArrayList<Method> al = ClassFactory.GetObjects("bp.en.Method");
		int i = 1;
		String html = "";

		DataTable dt = new DataTable();
		dt.Columns.Add("Name", String.class);
		dt.Columns.Add("Title", String.class);
		dt.Columns.Add("GroupName", String.class);
		dt.Columns.Add("Icon", String.class);
		dt.Columns.Add("Note", String.class);

		DataRow dr;
		for (Method en : al)
		{
			if (en.getIsCanDo() == false || en.ItIsVisable == false)
			{
				continue;
			}

			dr = dt.NewRow();
			dr.setValue("Name", en.toString());
			dr.setValue("Title", en.Title);
			dr.setValue("GroupName", en.GroupName);
			dr.setValue("Icon", en.Icon);
			dr.setValue("Note", en.Help);
			dt.Rows.add(dr);

		}

		return bp.tools.Json.ToJson(dt);
	}
	/**
	 获得查询的基本信息.

	 @return
	*/
	public final String Search_MapBaseInfo() throws Exception {
		//获得
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		if (ens == null)
		{
			return "err@类名:" + this.getEnsName() + "错误";
		}

		Entity en = ens.getNewEntity();
		Map map = en.getEnMapInTime();

		Hashtable ht = new Hashtable();

		//把权限信息放入.
		UAC uac = en.getHisUAC();
		ht.put("IsUpdata", uac.IsUpdate);
		ht.put("IsInsert", uac.IsInsert);
		ht.put("IsDelete", uac.IsDelete);
		ht.put("IsView", uac.IsView);
		ht.put("IsExp", uac.IsExp); //是否可以导出?
		ht.put("IsImp", uac.IsImp); //是否可以导入?

		ht.put("EnDesc", en.getEnDesc()); //描述?
		ht.put("EnName", en.toString()); //类名?


		//把map信息放入
		ht.put("PhysicsTable", map.getPhysicsTable());
		ht.put("CodeStruct", map.getCodeStruct());
		//ht.Add("CodeLength", map.CodeLength);

		//查询条件.
		if (map.ItIsShowSearchKey == true)
		{
			ht.put("IsShowSearchKey", 1);
		}
		else
		{
			ht.put("IsShowSearchKey", 0);
		}

		ht.put("SearchFields", map.SearchFields);
		ht.put("SearchFieldsOfNum", map.SearchFieldsOfNum);

		//按日期查询.
		ht.put("DTSearchWay", map.DTSearchWay.getValue());
		ht.put("DTSearchLabel", map.DTSearchLabel);
		ht.put("DTSearchKey", map.DTSearchKey);

		//把实体类中的主键放在hashtable中
		ht.put("EntityPK", en.getPKField());

		///#region 把配置的信息增加里面去.
		//EnCfg cfg = new EnCfg();
		//cfg.No = this.EnsName;
		//if (cfg.RetrieveFromDBSources() == 0)
		//{
		//    cfg.Insert();
		//}
		//foreach (string key in cfg.Row.Keys)
		//{
		//    if (ht.containsKey(key) == true)
		//        continue;
		//    //设置值.
		//    ht.Add(key, cfg.GetValByKey(key));
		//}
		///#endregion 把配置的信息增加里面去.

		return bp.tools.Json.ToJson(ht);
	}
	/**
	 外键或者枚举的查询.

	 @return
	*/
	public final String Search_SearchAttrs() throws Exception {
		//获得
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		if (ens == null)
		{
			return "err@类名错误:" + this.getEnsName();
		}

		Entity en = ens.getNewEntity();
		Map map = ens.getNewEntity().getEnMapInTime();

		DataSet ds = new DataSet();

		//构造查询条件集合.
		DataTable dt = new DataTable();
		dt.Columns.Add("Field");
		dt.Columns.Add("Name");
		dt.Columns.Add("Width");
		dt.Columns.Add("UIContralType");
		dt.Columns.Add("IsTree");
		dt.TableName = "Attrs";
		SearchFKEnums attrs = map.getSearchFKEnums();
		Attr attr = null;
		for (SearchFKEnum item : attrs)
		{
			attr = item.HisAttr;
			DataRow dr = dt.NewRow();
			dr.setValue("Field", item.Key);
			dr.setValue("Name", item.HisAttr.getDesc());
			dr.setValue("Width", item.Width); //下拉框显示的宽度.
			dr.setValue("UIContralType", item.HisAttr.getUIContralType().getValue());
			if (attr.getItIsFK() && attr.getHisFKEn().getItIsTreeEntity() == true)
			{
				if (attr.getKey().equals("FK_Dept") && WebUser.getIsAdmin() == false)
				{
					dr.setValue("IsTree", 0);
				}
				else
				{
					dr.setValue("IsTree", 1);
				}
			}
			else
			{
				dr.setValue("IsTree", 0);
			}
			dt.Rows.add(dr);
		}
		ds.Tables.add(dt);

		//把外键枚举增加到里面.
		for (SearchFKEnum item : attrs)
		{
			attr = item.HisAttr;
			if (attr.getItIsEnum() == true)
			{
				SysEnums ses = new SysEnums(attr.getUIBindKey());
				DataTable dtEnum = ses.ToDataTableField("dt");
				dtEnum.TableName = item.Key;
				ds.Tables.add(dtEnum);
				continue;
			}

			if (attr.getItIsFK() == true)
			{
				Entities ensFK = attr.getHisFKEns();
				ensFK.RetrieveAll();

				DataTable dtEn = ensFK.ToDataTableField("dt");
				dtEn.TableName = item.Key;
				ds.Tables.add(dtEn);
				continue;
			}
			//绑定SQL的外键
			if (DataType.IsNullOrEmpty(attr.getUIBindKey()) == false && ds.Tables.contains(attr.getKey()) == false)
			{
				//获取SQL
				String sql = attr.getUIBindKey();
				if (attr.getUIBindKey().contains("SELECT") == false)
				{
					SFTable sf = new SFTable(attr.getUIBindKey());
					sql = sf.getSelectStatement();
				}

				sql = Glo.DealExp(sql, null, null);
				DataTable dtSQl = DBAccess.RunSQLReturnTable(sql);
				for (DataColumn col : dtSQl.Columns)
				{
					String colName = col.ColumnName.toLowerCase();
					switch (colName)
					{
						case "no":
							col.ColumnName = "No";
							break;
						case "name":
							col.ColumnName = "Name";
							break;
						case "parentno":
							col.ColumnName = "ParentNo";
							break;
						default:
							break;
					}
				}
				dtSQl.TableName = item.Key;
				ds.Tables.add(dtSQl);
			}

		}

		//获取查询条件的扩展属性
		MapExts exts = new MapExts(this.getEnsName());
		if (!exts.isEmpty())
		{
			ds.Tables.add(exts.ToDataTableField("Sys_MapExt"));
		}


		return bp.tools.Json.ToJson(ds);
	}
	/**
	 执行查询 - 初始化查找数据

	 @return
	*/
	public final String Search_SearchIt() throws Exception {
		//取出来查询条件.
		UserRegedit ur = new UserRegedit();
		ur.setRow(null);
		ur.setMyPK(WebUser.getNo() + "_" + this.getEnsName() + "_SearchAttrs");
		ur.RetrieveFromDBSources();

		DataSet ds = new DataSet();
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		Entity en = ens.getNewEntity();
		Map map = null;
		if (this.getEnsName().indexOf("TS.") == 0)
		{
			map = en.getEnMap();
		}
		else
		{
			map = en.getEnMapInTime();
		}


		MapAttrs attrs = new MapAttrs();

		MapData md = new MapData();
		md.setNo(this.getEnsName());
		int count = md.RetrieveFromDBSources();
		if (count == 0)
		{
			attrs = map.getAttrs().ToMapAttrs();
		}
		else
		{
			attrs.Retrieve(MapAttrAttr.FK_MapData, this.getEnsName(), MapAttrAttr.Idx);
		}

		//根据设置的显示列显示字段
		DataRow row = null;
		DataTable dtAttrs = new DataTable("Attrs");
		dtAttrs.Columns.Add("KeyOfEn", String.class);
		dtAttrs.Columns.Add("Name", String.class);
		dtAttrs.Columns.Add("Width", Integer.class);
		dtAttrs.Columns.Add("UIContralType", Integer.class);
		dtAttrs.Columns.Add("IsRichText", Integer.class);
		dtAttrs.Columns.Add("MyDataType", Integer.class);
		for (MapAttr attr : attrs.ToJavaList())
		{
			String searchVisable = attr.getatPara().GetValStrByKey("SearchVisable");
			if (Objects.equals(searchVisable, "0"))
			{
				continue;
			}
			if ((count != 0 && DataType.IsNullOrEmpty(searchVisable)) || (count == 0 && !attr.getUIVisible()))
			{
				continue;
			}
			row = dtAttrs.NewRow();
			row.setValue("KeyOfEn", attr.getKeyOfEn());
			row.setValue("Name", attr.getName());
			row.setValue("Width", attr.getUIWidthInt());
			row.setValue("UIContralType", attr.getUIContralType().getValue());
			row.setValue("IsRichText", attr.getTextModel() == 3 ? 1 : 0);
			row.setValue("MyDataType", attr.getMyDataType());
			dtAttrs.Rows.add(row);
		}

		ds.Tables.add(dtAttrs); //把描述加入.

		md.setName(map.getEnDesc());

		//附件类型.
		md.SetPara("BPEntityAthType", map.HisBPEntityAthType.getValue());

		//获取实体类的主键
		md.SetPara("PK", en.getPK());

		ds.Tables.add(md.ToDataTableField("Sys_MapData"));

		QueryObject qo = Search_Data(ens, en, map, ur, "CommSearch");

		//获取配置信息
		EnCfg encfg = new EnCfg();
		// 如果开启了收藏功能
//		if(encfg.GetValBooleanByKey("EnableFavorite")) {
//
//		}
		encfg.setNo(this.getEnsName());
		encfg.RetrieveFromDBSources();

		String fieldSet = encfg.getFieldSet();
		String oper = "";
		if (DataType.IsNullOrEmpty(fieldSet) == false)
		{
			String ptable = en.getEnMap().getPhysicsTable();
			DataTable dt = new DataTable("Search_HeJi");
			dt.Columns.Add("Field");
			dt.Columns.Add("Type");
			dt.Columns.Add("Value");
			DataRow dr;
			String[] strs = fieldSet.split("[@]", -1);
			for (String str : strs)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}
				String[] item = str.split("[=]", -1);
				if (item.length == 2)
				{
					if (item[1].contains(",") == true)
					{
						String[] ss = item[1].split("[,]", -1);
						for (String s : ss)
						{
							dr = dt.NewRow();
							dr.setValue("Field", ((MapAttr)attrs.GetEntityByKey("KeyOfEn", s)).getName());
							dr.setValue("Type", item[0]);
							dt.Rows.add(dr);

							oper += item[0] + "(" + ptable + "." + s + ")" + ",";
						}
					}
					else
					{
						dr = dt.NewRow();
						dr.setValue("Field", ((MapAttr)attrs.GetEntityByKey("KeyOfEn", item[1])).getName());
						dr.setValue("Type", item[0]);
						dt.Rows.add(dr);

						oper += item[0] + "(" + ptable + "." + item[1] + ")" + ",";
					}
				}
			}
			oper = oper.substring(0, oper.length() - 1);
			DataTable dd = qo.GetSumOrAvg(oper);

			for (int i = 0; i < dt.Rows.size(); i++)
			{
				DataRow ddr = dt.Rows.get(i);
				ddr.setValue("Value", dd.Rows.get(0).getValue(i));
			}
			ds.Tables.add(dt);
		}
		//增加排序
		String orderBy = "";
		boolean isDesc = false;
		if (!DataType.IsNullOrEmpty(ur.getOrderBy()))
		{
			orderBy = ur.getOrderBy();
			isDesc = ur.getOrderWay().equals("desc");
		}

		if (DataType.IsNullOrEmpty(ur.getOrderBy()))
		{
			orderBy = encfg.GetValStrByKey("OrderBy");
			if (orderBy.contains(","))
			{
				String[] str = orderBy.split("[,]", -1);
				orderBy = str[0];
			}
			isDesc = encfg.GetValBooleanByKey("IsDeSc");
		}

		if (!DataType.IsNullOrEmpty(orderBy))
		{
			try
			{
				if (isDesc)
				{
					qo.addOrderByDesc(orderBy);
				}
				else
				{
					qo.addOrderBy(orderBy);
				}
			}
			catch (RuntimeException ex)
			{
				encfg.SetValByKey("OrderBy", orderBy);
			}
		}


		///#endregion 获得查询数据.

		//获得行数.
		ur.SetPara("RecCount", qo.GetCount());
		ur.Save();

		qo.DoQuery(en.getPK(), this.getPageSize(), this.getPageIdx());

		DataTable mydt = ens.ToDataTableField("dt");
		mydt.TableName = "DT";

		ds.Tables.add(mydt); //把数据加入里面.

			///#region 获得方法的集合
		DataTable dtM = new DataTable("dtM");
		dtM.Columns.Add("No");
		dtM.Columns.Add("Title");
		dtM.Columns.Add("Tip");
		dtM.Columns.Add("Visable");

		dtM.Columns.Add("Url");
		dtM.Columns.Add("Target");
		dtM.Columns.Add("Warning");
		dtM.Columns.Add("RefMethodType");
		dtM.Columns.Add("GroupName");
		dtM.Columns.Add("W");
		dtM.Columns.Add("H");
		dtM.Columns.Add("Icon");
		dtM.Columns.Add("IsCanBatch");
		dtM.Columns.Add("RefAttrKey");
		dtM.Columns.Add("ClassMethodName");
		dtM.Columns.Add("IsShowForEnsCondtion");
		dtM.Columns.Add("IsHaveFuncPara");

		RefMethods rms = map.getHisRefMethods();
		for (RefMethod item : rms)
		{
			if (item.ItIsForEns == false)
			{
				continue;
			}

			if (item.Visable == false)
			{
				continue;
			}

			String myurl = "";

			myurl = "RefMethod.htm?Index=" + item.Index + "&EnName=" + en.toString() + "&EnsName=" + en.GetNewEntities().toString() + "&PKVal=";

			DataRow dr = dtM.NewRow();

			dr.setValue("No", item.Index);
			dr.setValue("Title", item.Title);
			dr.setValue("Tip", item.ToolTip);
			dr.setValue("Visable", item.Visable);
			dr.setValue("Warning", item.Warning);
			dr.setValue("RefMethodType", item.refMethodType.getValue());
			dr.setValue("RefAttrKey", item.RefAttrKey);
			dr.setValue("URL", myurl);
			dr.setValue("W", item.Width);
			dr.setValue("H", item.Height);
			dr.setValue("Icon", item.Icon);
			dr.setValue("IsCanBatch", item.ItIsCanBatch);
			dr.setValue("GroupName", item.GroupName);
			dr.setValue("ClassMethodName", item.ClassMethodName);
			dr.setValue("IsShowForEnsCondtion", item.IsShowForEnsCondtion);
			dr.setValue("IsHaveFuncPara", item.getHisAttrs().isEmpty() ? 0 : 1);

			dtM.Rows.add(dr); //增加到rows.
		}
		ds.Tables.add(dtM); //把数据加入里面.
			///#endregion

		return bp.tools.Json.ToJson(ds);
	}

	/**
	 * 新建系统
	 * @return
	 * @throws Exception
	 */
	public String createMySystem() throws Exception {

		int systemType = this.GetRequestValInt("systemType");
		String name = this.GetRequestVal("name");
		//计算角色
		DeptEmpStations deptEmpStations = new DeptEmpStations();
		deptEmpStations.Retrieve(DeptEmpStationAttr.FK_Emp, WebUser.getNo());
		List<DeptEmpStation> deptEmpStationList = deptEmpStations.ToJavaList();
		String strs = deptEmpStationList.stream().map(m -> m.getStationNo()).collect(Collectors.joining(","));

		MySystem mySystem = new MySystem();
		mySystem.setSystemType(systemType);
		mySystem.setName(name);
		mySystem.setNo(DBAccess.GenerGUID());
		mySystem.SetValByKey(MySystemAttr.FK_Stations, strs);
		mySystem.setIcon("icon-user");
		if (systemType == 0) {
			mySystem.SetPara("EnName", "TS.GPM.MySystem");

			if (systemType == 0) {
				//循环创建模块.
				for (int index = 0; index < 2; index++) {

					Module model = new Module();
					model.setNo(DBAccess.GenerGUID());
					model.setName("模块" + index);
					model.setIcon("icon-fire");
					model.setSystemNo(mySystem.getNo());
					model.Insert();
				}
			}
		}
		if (systemType == 1) {
			mySystem.SetPara("EnName", "TS.GPM.SystemPortal");
			mySystem.setIcon("icon-chart");
		}
		if (systemType == 2) {
			mySystem.SetPara("EnName", "TS.GPM.SystemSkip");
		}
		mySystem.Insert();
		return mySystem.getNo();
	}


	/**
	 * 设置单机版二级管理员
	 * @throws Exception
	 */
	public final String DoSetSecondAdminer() throws Exception {

		String stationIds=this.GetRequestVal("stationIds");
		String deptNo=this.GetRequestVal("deptNo");
		String empNo=this.GetRequestVal("empNo");
		if (DataType.IsNullOrEmpty(empNo)|| DataType.IsNullOrEmpty(stationIds)) {
			return "err@参数不能为空";
		}
		Emp emp=new Emp(empNo);
		String[] strs = stationIds.split(",");
		StationTypes stationTypes = new StationTypes();
		stationTypes.RetrieveAll();
		List<StationType> stationTypeList = stationTypes.ToJavaList();
		Optional<StationType> stationTypeOptional = stationTypeList.stream().filter(m -> m.GetValByKey(StationTypeAttr.Name).equals("系统管理员")).findFirst();

		Stations stations = new Stations();
		stations.RetrieveAll();
		List<Station> stationList = stations.ToJavaList();

		//计算出来系统管理员角色
		List<Station> stationListSystem = new ArrayList<>();
		if (stationTypeOptional.isPresent()) {
			StationType stationTypeGet = stationTypeOptional.get();
			stationListSystem = stationList.stream().filter(m -> m.getFKStationType().equals(stationTypeGet.getNo())).collect(Collectors.toList());
		}
		List<String> stationListSystemIds=stationListSystem.stream().map(m->m.getNo()).collect(Collectors.toList());
		DeptEmpStations deptEmpStations = new DeptEmpStations();
		deptEmpStations.Delete(DeptEmpAttr.FK_Dept, deptNo, DeptEmpAttr.FK_Emp, empNo);

		//先将此人管理员账号删除
		GloVar gloVar = new GloVar();
		gloVar.setNo(empNo + "_Adminer");
		gloVar.Delete();

		for (String stationNo : strs) {

			DeptEmpStation deptEmpStation = new DeptEmpStation();
			deptEmpStation.setDeptNo(deptNo);
			deptEmpStation.setEmpNo(empNo);
			deptEmpStation.setStationNo(stationNo);
			deptEmpStation.setOrgNo(WebUser.getOrgNo());
			deptEmpStation.setMyPK(deptNo+"_"+empNo+"_"+stationNo);

			if (stationListSystemIds.contains(stationNo)) {
				String stationName=null;
				Optional<Station> stationNameOptional = stationListSystem.stream().filter(m -> m.getNo().equals(stationNo)).findFirst();
				if(stationNameOptional.isPresent()){
					stationName=stationNameOptional.get().getName();
				}
				if (gloVar.RetrieveFromDBSources() == 0) {
					gloVar.setName(emp.getName());
					gloVar.setVal(empNo);
					gloVar.setNote(stationNo);
					gloVar.setGroupKey("Adminer");
					gloVar.Insert();

					///#region 检查流程树.
					FlowSort fs = new bp.wf.template.FlowSort();
					fs.setNo(stationNo);
					if (fs.RetrieveFromDBSources() == 0) {
						//获得根目录节点.
						FlowSort root = new FlowSort();
						int i = root.Retrieve(FlowSortAttr.ParentNo, "0");

						//设置流程树权限.
						fs.setNo(stationNo);
						fs.setName(stationName);
						fs.setParentNo(root.getNo());
						fs.setIdx(999);
						fs.DirectInsert();

						//创建下一级目录.
						bp.en.EntityTree tempVar = fs.DoCreateSubNode(null);
						FlowSort en = tempVar instanceof FlowSort ? (FlowSort) tempVar : null;
						en.setName("日常办公类");
						en.setDomainExt("");
						en.DirectUpdate();
					}

					///#endregion 检查流程树.

					///#region 检查表单树.
					//表单根目录.
					SysFormTree ftRoot = new SysFormTree();
					int val = ftRoot.Retrieve(FlowSortAttr.ParentNo, "0");
					if (val == 0) {
						val = ftRoot.Retrieve(FlowSortAttr.No, "100");
						if (val == 0) {
							ftRoot.setNo("100");
							ftRoot.setName("表单库");
							ftRoot.setParentNo("0");
							ftRoot.Insert();
						} else {
							ftRoot.setParentNo("0");
							ftRoot.setName("表单库");
							ftRoot.Update();
						}
					}

					//设置表单树权限.
					SysFormTree ft = new SysFormTree();
					ft.setNo(stationNo);
					if (ft.RetrieveFromDBSources() == 0) {
						ft.setName(stationName);
						ft.setParentNo(ftRoot.getNo());
						ft.setIdx(999);
						ft.DirectInsert();

						//创建两个目录.
						bp.en.EntityTree tempVar2 = ft.DoCreateSubNode(null);
						SysFormTree mySubFT = tempVar2 instanceof SysFormTree ? (SysFormTree) tempVar2 : null;
						mySubFT.setName("日常办公类");
						mySubFT.DirectUpdate();
					}
					///#endregion 检查表单树.
				}else{
					return "err@已设置为:"+stationName+"管理员";
				}
			}
			deptEmpStation.Insert();
		}

		return "设置成功";
	}

	/***
	 * 保存公文模板
	 * @return
	 * @throws Exception
	 */
	public synchronized String SaveGongWenWord() throws Exception {

		String token = this.GetRequestVal("token");
		String fkNodeId = this.GetRequestVal("fkNodeId");
		String workId = this.GetRequestVal("workId");
		HttpServletRequest request = ContextHolderUtils.getRequest();
		String contentType = request.getContentType();
		MultipartHttpServletRequest multipartRequest = null;
		if (contentType != null && contentType.indexOf("multipart/form-data") != -1)
			multipartRequest = CommonFileUtils.getMultipartHttpServletRequest(request);
		if (multipartRequest == null)
			return "err@请求出错";
		List<MultipartFile> items = multipartRequest.getFiles("file");
		if (items.size() < 0 || DataType.IsNullOrEmpty(token) || DataType.IsNullOrEmpty(fkNodeId)) {
			return "err@参数不能为空";
		}
		MultipartFile file=items.get(0);
		byte[] bytes = file.getBytes();

		Node nd = new Node(Integer.parseInt(fkNodeId));
		Work wk = nd.getHisWork();
		wk.setOID(Integer.parseInt(workId));
		wk.SaveFileToDB("DBFile",bytes);
		return "已成功保存到服务器";
	}
	/**
	 执行查询.这个方法也会被导出调用.

	 @return
	*/
	public final QueryObject Search_Data(Entities ens, Entity en, Map map, UserRegedit ur, String action) throws Exception {

		//获得关键字.
		AtPara ap = new AtPara(ur.getVals());


		//关键字.
		String keyWord = ur.getSearchKey();
		QueryObject qo = new QueryObject(ens);
		boolean isFirst = true; //是否第一次拼接SQL
		Attrs attrs = map.getAttrs();
			///#region 关键字字段.
		if (DataType.IsNullOrEmpty(map.SearchFields) == false)
		{
			String field = ""; //字段名
			String fieldValue = ""; //字段值
			int idx = 0;

			//获取查询的字段
			String[] searchFields = map.SearchFields.split("[@]", -1);
			for (String str : searchFields)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}

				//字段名
				field = str.split("[=]", -1)[1];
				if (DataType.IsNullOrEmpty(field) == true)
				{
					continue;
				}

				//字段名对应的字段值
				fieldValue = ur.GetParaString(field);
				if (DataType.IsNullOrEmpty(fieldValue) == true)
				{
					continue;
				}
				fieldValue = fieldValue.trim();
				fieldValue = fieldValue.replace(",", ";").replace(" ", ";");
				String[] fieldValues = fieldValue.split("[;]", -1);
				int valIdx = 0;
				idx++;
				for (String val : fieldValues)
				{
					valIdx++;

					if (idx == 1 && valIdx == 1)
					{
						isFirst = false;
						/* 第一次进来。 */
						qo.addLeftBracket();
						if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
						{
							qo.AddWhere(field, " LIKE ", SystemConfig.getAppCenterDBType( ) == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + field + valIdx + ",'%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + field + valIdx + "+'%'"));
						}
						else
						{
							qo.AddWhere(field, " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + field + valIdx + "||'%'");
						}
						qo.getMyParas().Add(field + valIdx, val, false);

						if (valIdx == fieldValues.length)
						{
							qo.addRightBracket();
						}

						continue;
					}
					if (valIdx == 1 && idx != 1)
					{
						qo.addAnd();
						qo.addLeftBracket();
					}
					else
					{
						qo.addOr();
					}

					if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
					{
						qo.AddWhere(field, " LIKE ", SystemConfig.getAppCenterDBType( ) == DBType.MySQL ? ("CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + field + valIdx  + ",'%')") : ("'%'+" + SystemConfig.getAppCenterDBVarStr() + field + valIdx + "+'%'"));
					}
					else
					{
						qo.AddWhere(field, " LIKE ", "'%'||" + SystemConfig.getAppCenterDBVarStr() + field + valIdx + "||'%'");
					}
					qo.getMyParas().Add(field + valIdx, val, false);

					if (valIdx == fieldValues.length)
					{
						qo.addRightBracket();
					}
				}
			}

		}
		else
		{

			if (en.getEnMap().ItIsShowSearchKey && DataType.IsNullOrEmpty(keyWord) == false && keyWord.length() >= 1)
			{
				Attr attrPK = new Attr();
				for (Attr attr : attrs)
				{
					if (attr.getItIsPK())
					{
						attrPK = attr;
						break;
					}
				}
				int i = 0;
				String enumKey = ","; //求出枚举值外键.
				//keyWord = keyWord.replace(",", ";").replace(" ", ";");
				String[] strVals = keyWord.split("[;]", -1);

//				if (strVals.length > 1)
//				{
//					//判断是否存在SKeWord
//					Attr keyAttr = attrs.GetAttrByKeyOfEn("SKeyWords");
//					if (keyAttr == null)
//					{
//						throw new RuntimeException("err@没有关键字SKeyWords不能按照多关键字查询");
//					}
//					for (String val : strVals)
//					{
//						i++;
//						if (i == 1)
//						{
//							isFirst = false;
//							/* 第一次进来。 */
//							qo.addLeftBracket();
//							if (Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "?"))
//							{
//								qo.AddWhere("SKeyWords", " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKeyWords" + i + ", '%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + "SKeyWords" + i + "+'%'"));
//							}
//							else
//							{
//								qo.AddWhere("SKeyWords", " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + "SKeyWords" + i + "|| '%'");
//							}
//
//							qo.getMyParas().Add("SKeyWords" + i, val, false);
//
//							continue;
//						}
//						qo.addAnd();
//
//						if (Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "?"))
//						{
//							qo.AddWhere("SKeyWords", " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? ("CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKeyWords" + i + ", '%')") : ("'%'+" + SystemConfig.getAppCenterDBVarStr() + "SKeyWords" + i + "+'%'"));
//						}
//						else
//						{
//							qo.AddWhere("SKeyWords", " LIKE ", "'%'||" + SystemConfig.getAppCenterDBVarStr() + "SKeyWords" + i + "|| '%'");
//						}
//
//						qo.getMyParas().Add("SKeyWords" + i, val, false);
//					}
//				}
//				else
//				{
					for (Attr attr : map.getAttrs())
					{
						switch (attr.getMyFieldType())
						{
							case Enum:
								enumKey = "," + attr.getKey() + "Text,";
								break;
							case FK:
								//enumKey = "," + attr.getKey() + "Text,";
								// case FieldType.PKFK:
								continue;
							default:
								break;
						}

						if (attr.getMyDataType() != DataType.AppString)
						{
							continue;
						}

						//排除枚举值关联refText.
						if (attr.getMyFieldType() == FieldType.RefText)
						{
							if (enumKey.contains("," + attr.getKey() + ",") == true)
							{
								continue;
							}
						}

						if (Objects.equals(attr.getKey(), "FK_Dept"))
						{
							continue;
						}
						int valIdx = 0;
						for (String val : strVals)
						{
							i++;
							valIdx++;
							if (i == 1)
							{
								isFirst = false;
								/* 第一次进来。 */
								qo.addLeftBracket();
								if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
								{
									qo.AddWhere(attr.getKey(), " LIKE ", SystemConfig.getAppCenterDBType( ) == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey" + valIdx + ", '%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey" + valIdx + "+'%'"));
								}
								else
								{
									qo.AddWhere(attr.getKey(), " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey" + valIdx + "|| '%'");
								}

								qo.getMyParas().Add("SKey" + valIdx, val, false);

								continue;
							}
							qo.addOr();

							if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
							{
								qo.AddWhere(attr.getKey(), " LIKE ", SystemConfig.getAppCenterDBType( ) == DBType.MySQL ? ("CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey" + valIdx + ", '%')") : ("'%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey" + valIdx + "+'%'"));
							}
							else
							{
								qo.AddWhere(attr.getKey(), " LIKE ", "'%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey" + valIdx + "|| '%'");
							}

							qo.getMyParas().Add("SKey" + valIdx, val, false);
						}

					}
				//}
				qo.addRightBracket();

			}

		}
			///#region 增加数值型字段的查询
		if (DataType.IsNullOrEmpty(map.SearchFieldsOfNum) == false)
		{
			String field = ""; //字段名
			String fieldValue = ""; //字段值
			int idx = 0;

			//获取查询的字段
			String[] searchFieldsOfNum = map.SearchFieldsOfNum.split("[@]", -1);
			for (String str : searchFieldsOfNum)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}

				//字段名
				field = str.split("[=]", -1)[1];
				if (DataType.IsNullOrEmpty(field) == true)
				{
					continue;
				}

				//字段名对应的字段值
				fieldValue = ur.GetParaString(field);
				if (DataType.IsNullOrEmpty(fieldValue) == true)
				{
					continue;
				}
				String[] strVals = fieldValue.split("[,]", -1);

				//判断是否是第一次进入
				if (isFirst == false)
				{
					qo.addAnd();
				}
				else
				{
					isFirst = false;
				}
				qo.addLeftBracket();
				if (DataType.IsNullOrEmpty(strVals[0]) == false)
				{

					if (DataType.IsNullOrEmpty(strVals[1]) == true)
					{
						qo.AddWhere(field, ">=", strVals[0]);
					}
					else
					{
						qo.AddWhere(field, ">=", strVals[0], field + "1");
						qo.addAnd();
						qo.AddWhere(field, "<=", strVals[1], field + "2");
					}

				}
				else
				{
					qo.AddWhere(field, "<=", strVals[1]);
				}

				qo.addRightBracket();

			}


		}
		///#endregion
		///#region 设置隐藏字段的过滤查询
		if(action.equals("CommSearch") ==false && action.equals("ContrastDtl") ==false && action.equals("GroupSearch") ==false) {
			FrmBill frmBill = new FrmBill(this.getFrmID());
			String hidenField = frmBill.GetParaString("HidenField");
			if (DataType.IsNullOrEmpty(hidenField) == false) {
				hidenField = hidenField.replace("_WebUser.No", WebUser.getNo());
				hidenField = hidenField.replace("_WebUser.Name", WebUser.getName());
				hidenField = hidenField.replace("_WebUser.FK_DeptName", WebUser.getDeptName());
				hidenField = hidenField.replace("_WebUser.FK_Dept", WebUser.getDeptNo());
				hidenField = hidenField.replace("_WebUser.OrgNo", WebUser.getOrgNo());
				if (isFirst == false) {
					qo.addAnd();
				} else {
					isFirst = false;
				}
				qo.addSQL(hidenField);
			}
		}
		///#endregion 设置隐藏字段的查询

		if (map.DTSearchWay != DTSearchWay.None
				&& DataType.IsNullOrEmpty(ur.getDTSearchKey()) == false
				&& DataType.IsNullOrEmpty(ur.getDTFrom()) == false
				&& DataType.IsNullOrEmpty(ur.getDTTo()) == false)
		{
			String dtFrom = ur.getDTFrom();
			String dtTo = ur.getDTTo();

			if (map.DTSearchWay == DTSearchWay.ByYearMonth || map.DTSearchWay == DTSearchWay.ByYear)
			{
				if (isFirst == false)
				{
					qo.addAnd();
				}
				else
				{
					isFirst = false;
				}
				qo.AddWhere(map.DTSearchKey, dtFrom);
			}


			if (DataType.IsNullOrEmpty(dtTo) == true)
			{
				dtTo = DataType.getCurrentDate();
			}


			//按日期查询
			if (map.DTSearchWay == DTSearchWay.ByDate)
			{
				if (isFirst == false)
				{
					qo.addAnd();
				}
				else
				{
					isFirst = false;
				}
				if (DataType.IsNullOrEmpty(dtFrom) == true)
				{
					qo.addLeftBracket();
					qo.setSQL(map.getPhysicsTable() + "." + ur.getDTSearchKey() + " <= '" + dtTo + "'");
					qo.addRightBracket();
				}
				else
				{

					qo.addLeftBracket();
					dtFrom += " 00:00";
					dtTo += " 23:59:59";
					qo.setSQL(map.getPhysicsTable() + "." + ur.getDTSearchKey() + " >= '" + dtFrom + "'");
					qo.addAnd();
					qo.setSQL(map.getPhysicsTable() + "." + ur.getDTSearchKey() + " <= '" + dtTo + "'");
					qo.addRightBracket();

				}
			}

			if (map.DTSearchWay == DTSearchWay.ByDateTime)
			{
				//取前一天的24：00
				if (dtFrom.trim().length() == 10) //2017-09-30
				{
					dtFrom += " 00:00:00";
				}
				if (dtFrom.trim().length() == 16) //2017-09-30 00:00
				{
					dtFrom += ":00";
				}

				dtFrom = DateUtils.addDay(DateUtils.parse(dtFrom, "yyyy-MM-dd"), -1) + " 24:00";
				if (dtTo.trim().length() < 11 || dtTo.trim().indexOf(' ') == -1)
				{
					dtTo += " 24:00";
				}
				if (isFirst == false)
				{
					qo.addAnd();
				}
				else
				{
					isFirst = false;
				}
				if (DataType.IsNullOrEmpty(dtFrom) == true)
				{
					qo.addLeftBracket();
					qo.setSQL(ur.getDTSearchKey() + " <= '" + dtTo + "'");
					qo.addRightBracket();
				}
				else
				{
					qo.addLeftBracket();
					qo.setSQL(ur.getDTSearchKey()+ " >= '" + dtFrom + "'");
					qo.addAnd();
					qo.setSQL(ur.getDTSearchKey() + " <= '" + dtTo + "'");
					qo.addRightBracket();
				}

			}
		}

		ArrayList<String> keys = new ArrayList<String>();
			///#region 普通属性
		String opkey = ""; // 操作符号。
		for (SearchNormal attr : en.getEnMap().getSearchNormals())
		{
			if (attr.getItIsHidden())
			{
				if (isFirst == false)
				{
					qo.addAnd();
				}
				else
				{
					isFirst = false;
				}
				qo.addLeftBracket();
				if (attr.getDefaultSymbol().equals("exp") == true)
				{
					qo.addSQL(Glo.DealExp(attr.getRefAttrKey(), null));
					qo.addRightBracket();
					continue;

				}
				//如果传参上有这个值的查询
				String val = this.GetRequestVal(attr.getRefAttrKey());
				if (DataType.IsNullOrEmpty(val) == false)
				{
					attr.setDefaultSymbol("=");
					attr.setDefaultVal( val);
				}

				//获得真实的数据类型.
				if (SystemConfig.getAppCenterDBFieldIsParaDBType() == true && (attr.getDefaultSymbol().equals("=") || attr.getDefaultSymbol().equals("!=")))
				{
					Object valType = bp.sys.base.Glo.GenerRealType(en.getEnMap().getAttrs(), attr.getRefAttrKey(), attr.getDefaultValRun());
					qo.AddWhere(attr.getRefAttrKey(), attr.getDefaultSymbol(), valType);
				}
				else
				{
					qo.AddWhere(attr.getRefAttrKey(), attr.getDefaultSymbol(), attr.getDefaultValRun());
				}
				qo.addRightBracket();
				if (keys.contains(attr.getRefAttrKey()) == false)
				{
					keys.add(attr.getRefAttrKey());
				}
				continue;
			}

			if (attr.getSymbolEnable() == true)
			{

				opkey = ap.GetValStrByKey("DDL_" + attr.getKey());
				if (Objects.equals(opkey, "all"))
				{
					continue;
				}
			}
			else
			{
				opkey = attr.getDefaultSymbol();
			}

			if (isFirst == false)
			{
				qo.addAnd();
			}
			else
			{
				isFirst = false;
			}
			qo.addLeftBracket();

			if (attr.getDefaultVal().length() >= 8)
			{
				String date = "2005-09-01";
				try
				{
					/* 就可能是年月日。 */
					String y = ap.GetValStrByKey("DDL_" + attr.getKey() + "_Year");
					String m = ap.GetValStrByKey("DDL_" + attr.getKey() + "_Month");
					String d = ap.GetValStrByKey("DDL_" + attr.getKey() + "_Day");
					date = y + "-" + m + "-" + d;

					if (Objects.equals(opkey, "<="))
					{
						Date dt = DateUtils.addDay(DataType.ParseSysDate2DateTime(date), 1);
						date = DateUtils.format(dt,"yyyy-MM-dd");
					}
				}
				catch (Exception e)
				{
				}

				qo.AddWhere(attr.getRefAttrKey(), opkey, date);
			}
			else
			{
				qo.AddWhere(attr.getRefAttrKey(), opkey, ap.GetValStrByKey("TB_" + attr.getKey()));
			}
			qo.addRightBracket();
			if (keys.contains(attr.getRefAttrKey()) == false)
			{
				keys.add(attr.getRefAttrKey());
			}
		}
			///#region 获得查询数据.
		for (String str : ap.getHisHT().keySet())
		{
			if (keys.contains(str) == false)
			{
				keys.add(str);
			}

			String val = ap.GetValStrByKey(str);
			if (DataType.IsNullOrEmpty(val) == true || val.equals("null") == true)
			{
				val = "all";
			}
			if (val.equals("all"))
			{
				continue;
			}

			if (isFirst == false)
			{
				qo.addAnd();
			}
			else
			{
				isFirst = false;
			}
			isFirst = false;
			qo.addLeftBracket();
			Attr attr = attrs.GetAttrByKeyOfEn(str);
			if (attr != null && attr.getItIsFK() && attr.getUIBindKey().contains(",TS.") == false && attr.getHisFKEn().getItIsTreeEntity() == true && !(attr.getKey().equals("FK_Dept") && WebUser.getIsAdmin() == false))
			{
				//需要获取当前数据选中的数据和子级(先阶段只处理部门信息)
				DataTable dt = null;
				try
				{
					dt = DBAccess.RunSQLReturnTable(Dev2Interface.GetDeptNoSQLByParentNo(val, attr.getHisFKEn().getEnMap().getPhysicsTable()));
				}
				catch (RuntimeException ex)
				{
					if (SystemConfig.getAppCenterDBType() == DBType.MySQL)
					{
						throw new RuntimeException("err@请在web.config中数据库连接配置中增加Allow User Variables=True;");
					}
					throw new RuntimeException(ex.getMessage());
				}
				if (dt.Rows.size() == 0)
				{
					qo.AddWhere(attr.getKey(), val);
				}
				else
				{
					qo.AddWhereIn(attr.getKey(), dt);
				}
				qo.addRightBracket();
				continue;
			}
			//多选
			if (val.indexOf(",") != -1)
			{
				if (attr.getItIsNum() == true)
				{
					qo.AddWhere(str, "IN", "(" + val + ")");
					qo.addRightBracket();
					continue;
				}
				val = "('" + val.replace(",", "','") + "')";
				qo.AddWhere(str, "IN", val);
				qo.addRightBracket();
				continue;
			}

			Object valType = bp.sys.base.Glo.GenerRealType(attrs, str, val);
			qo.AddWhere(str, valType);
			qo.addRightBracket();
		}

		for (Attr attr : map.getAttrs())
		{
			if(DataType.IsNullOrEmpty(attr.getField()))
				continue;
			String val = this.GetRequestVal(attr.getField());
			if (DataType.IsNullOrEmpty(val))
			{
				continue;
			}
			if (keys.contains(attr.getField()))
			{
				continue;
			}
			if (attr.getField().equals("Token"))
			{
				continue;
			}
			if (attr.getField().equals("No"))
			{
				continue;
			}

			switch (attr.getMyDataType())
			{
				case DataType.AppBoolean:
					if (isFirst == false)
					{
						qo.addAnd();
					}
					else
					{
						isFirst = false;
					}
					qo.addLeftBracket();
					qo.AddWhere(attr.getField(), Boolean.getBoolean(val));
					qo.addRightBracket();
					break;
				case DataType.AppDate:
				case DataType.AppDateTime:
				case DataType.AppString:
					if (isFirst == false)
					{
						qo.addAnd();
					}
					else
					{
						isFirst = false;
					}
					qo.addLeftBracket();
					qo.AddWhere(attr.getField(), val);
					qo.addRightBracket();
					break;
				case DataType.AppDouble:
				case DataType.AppFloat:
				case DataType.AppMoney:
					if (isFirst == false)
					{
						qo.addAnd();
					}
					else
					{
						isFirst = false;
					}
					qo.addLeftBracket();
					qo.AddWhere(attr.getField(), Double.parseDouble(val));
					qo.addRightBracket();
					break;
				case DataType.AppInt:
					if (Objects.equals(val, "all") || Objects.equals(val, "-1"))
					{
						continue;
					}
					if (isFirst == false)
					{
						qo.addAnd();
					}
					else
					{
						isFirst = false;
					}
					;
					qo.addLeftBracket();
					qo.AddWhere(attr.getField(), Integer.parseInt(val));
					qo.addRightBracket();
					break;
				default:
					break;
			}
			if (keys.contains(attr.getField()) == false)
			{
				keys.add(attr.getField());
			}
		}

		return qo;

	}
	private DataTable SearchDtl_Data(Entities ens, Entity en, String workId, String fid) throws Exception {
		//获得.
		Map map = en.getEnMapInTime();

		MapAttrs attrs = map.getAttrs().ToMapAttrs();

		QueryObject qo = new QueryObject(ens);

		qo.AddWhere("RefPK", "=", workId);
		return qo.DoQueryToTable();
	}

	public final String Search_GenerPageIdx() throws Exception {

		UserRegedit ur = new UserRegedit();
		ur.setMyPK(WebUser.getNo() + "_" + this.getEnsName() + "_SearchAttrs");
		ur.RetrieveFromDBSources();

		String url = "?EnsName=" + this.getEnsName();
		int pageSpan = 10;
		int recNum = ur.GetParaInt("RecCount", 0); //获得查询数量.
		int pageSize = 12;
		if (recNum <= pageSize)
		{
			return "1";
		}

		String html = "";
		html += "<ul class='pagination'>";

		String appPath = ""; // this.Request.ApplicationPath;
		int myidx = 0;
		if (getPageIdx() <= 1)
		{
			html += "<li><img style='vertical-align:middle' src='" + Glo.getCCFlowAppPath() + "WF/Img/Arr/LeftEnd.png' border=0/><img style='vertical-align:middle' src='" + Glo.getCCFlowAppPath() + "WF/Img/Arr/Left.png' border=0/></li>";
		}
		else
		{
			myidx = getPageIdx() - 1;
			//this.Add("<a href='" + url + "&PageIdx=1' >《-</a> <a href='" + url + "&PageIdx=" + myidx + "'>《-</a>");
			html += "<li><a href='" + url + "&PageIdx=1' ><img style='vertical-align:middle' src='" + Glo.getCCFlowAppPath() + "WF/Img/Arr/LeftEnd.png' border=0/></a><a href='" + url + "&PageIdx=" + myidx + "'><img style='vertical-align:middle' src='" + Glo.getCCFlowAppPath() + "WF/Img/Arr/Left.png' border=0/></a></li>";
		}


		//分页采用java默认方式分页，采用bigdecimal分页报错
		int pageNum = (recNum + pageSize - 1) / pageSize;// 页面个数。

		int from = getPageIdx() < 1 ? 0 : (getPageIdx() - 1) * pageSize + 1;// 从

		int to = getPageIdx() < 1 ? pageSize : getPageIdx() * pageSize;// 到

		for (int i = 1; i <= pageNum; i++)
		{
			if (i >= from && i <= to)
			{
				if (getPageIdx() == i)
				{
					html += "<li class='active' ><a href='#'><b>" + i + "</b></a></li>";
				}
				else
				{
					html += "<li><a href='" + url + "&PageIdx=" + i + "'>" + i + "</a></li>";
				}
			}
		}

		if (getPageIdx() != pageNum)
		{
			myidx = getPageIdx() + 1;
			html += "<li><a href='" + url + "&PageIdx=" + myidx + "'><img style='vertical-align:middle' src='" + Glo.getCCFlowAppPath() + "WF/Img/Arr/Right.png' border=0/></a>&nbsp;<a href='" + url + "&PageIdx=" + pageNum + "'><img style='vertical-align:middle' src='" + Glo.getCCFlowAppPath() + "WF/Img/Arr/RightEnd.png' border=0/></a> &nbsp;&nbsp;页数:" + getPageIdx() + "/" + pageNum + "&nbsp;&nbsp;总数:" + recNum + "</li>";
		}
		else
		{
			html += "<li><img style='vertical-align:middle' src='" + Glo.getCCFlowAppPath() + "WF/Img/Arr/Right.png' border=0/></li>";
			html += "<li><img style='vertical-align:middle' src='" + Glo.getCCFlowAppPath() + "WF/Img/Arr/RightEnd.png' border=0/>&nbsp;&nbsp;页数:" + getPageIdx() + "/" + pageNum + "&nbsp;&nbsp;总数:" + recNum + "</li>";
		}
		html += "</ul>";
		return html;
	}
	/**
	 执行导出

	 @return
	*/
	public final String Search_Exp() throws Exception {
		//取出来查询条件.
		UserRegedit ur = new UserRegedit();
		ur.setMyPK(WebUser.getNo() + "_" + this.getEnsName() + "_SearchAttrs");
		ur.RetrieveFromDBSources();
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		Entity en = ens.getNewEntity();
		QueryObject qo = Search_Data(ens, en, en.getEnMap(), ur, "CommSearch");
		EnCfg encfg = new EnCfg();
		encfg.setNo(this.getEnsName());
		//增加排序
		if (encfg.RetrieveFromDBSources() != 0)
		{
			String orderBy = encfg.GetValStrByKey("OrderBy");
			boolean isDesc = encfg.GetValBooleanByKey("IsDeSc");

			if (DataType.IsNullOrEmpty(orderBy) == false)
			{
				try
				{
					if (isDesc)
					{
						qo.addOrderByDesc(orderBy);
					}
					else
					{
						qo.addOrderBy(orderBy);
					}
				}
				catch (RuntimeException ex)
				{
					encfg.SetValByKey("OrderBy", orderBy);
				}
			}
		}
		if (encfg.RetrieveFromDBSources() != 0)
		{
			qo.addOrderBy(en.getPK());
		}
		qo.DoQuery();
		return bp.tools.Json.ToJson(ens.ToDataTableField("dt"));
		//string name = "数据导出";
		//MapAttrs mapAttrs = new MapAttrs();
		//Attrs attrs = null;
		//MapData md = new MapData();
		//md.setNo(this.EnsName;
		//int count = md.RetrieveFromDBSources();
		//if (count == 1)
		//{
		//    mapAttrs.Retrieve(MapAttrAttr.FK_MapData, this.EnsName, MapAttrAttr.Idx);
		//    attrs = new Attrs();
		//    Attr attr = null;
		//    foreach (MapAttr mapAttr in mapAttrs)
		//    {
		//        string searchVisable = mapAttr.atPara.GetValStrByKey("SearchVisable");
		//        if (searchVisable == "0")
		//            continue;
		//        if ((count != 0 && DataType.IsNullOrEmpty(searchVisable)) || (count == 0 && mapattr.getUIVisible()== false))
		//            continue;
		//        attr = mapAttr.getHisAttr();
		//        attr.UIVisible = true;
		//        attrs.Add(attr);

		//    }

		//}

		//string filename = name + "_" + DataType.CurrentDateTimeCNOfLong + "_" + WebUser.getName() + ".xls";
		//string filePath = BP.Tools.ExportExcelUtil.ExportDGToExcel(Search_Data(ens, en), en, name, attrs);

		//return filePath;
	}
	/**
	 从表执行导出

	 @return
	*/
	public final String SearchDtl_Exp() throws Exception {
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		Entity en = ens.getNewEntity();

		String workId = this.GetRequestVal("WorkId");
		String fid = this.GetRequestVal("FID");
		String name = "从表数据导出";
		String filename = name + "_" + DataType.getCurrentDateTimeCNOfLong() + "_" + WebUser.getName() + ".xls";
		String filePath = bp.tools.ExportExcelUtil.ExportDGToExcel(SearchDtl_Data(ens, en, workId, fid), en, name, null, null);

		return filePath;
	}
	///#region Refmethod.htm 相关功能.
	public final String Refmethod_Init() throws Exception {
		String ensName = this.getEnsName();
		int index = this.getIndex();
		Entities ens = ClassFactory.GetEns(ensName);
		Entity en = ens.getNewEntity();
		RefMethod rm = en.getEnMap().getHisRefMethods().get(index);

		String pk = this.getPKVal();
		if (pk == null)
		{
			pk = this.GetRequestVal(en.getPK());
		}

		if (pk == null)
		{
			pk = this.getPKVal();
		}

		if (pk == null)
		{
			return "err@错误pkval 没有值。";
		}

		en.setPKVal(pk);
		en.RetrieveFromDBSources();

		//获取主键集合
		String[] pks = pk.split("[,]", -1);

		///#region 处理无参数的方法.
		if (rm.getHisAttrs() == null || rm.getHisAttrs().isEmpty())
		{
			String infos = "";
			int count = 0;
			int sucCount = 0;
			int errCount = 0;
			if (pks.length == 1)
			{
				rm.HisEn = en;

				// 如果是link.
				if (rm.refMethodType == RefMethodType.LinkModel || rm.refMethodType == RefMethodType.LinkeWinOpen || rm.refMethodType == RefMethodType.RightFrameOpen)
				{
					Object tempVar = rm.Do(null);
					String url = tempVar instanceof String ? (String)tempVar : null;
					if (DataType.IsNullOrEmpty(url))
					{
						return "err@应该返回的url.";
					}
					return "url@" + url;
				}

				Object obj = rm.Do(null);
				if (obj == null)
				{
					return "close@info";
				}

				String result = obj.toString();
				if (result.indexOf("url@") != -1 || result.indexOf("err@") != -1)
				{
					return result;
				}

				result = "info@" + result;
				return result;
			}
			for (String mypk : pks)
			{
				if (DataType.IsNullOrEmpty(mypk) == true)
				{
					continue;
				}
				count++;
				en.setPKVal(mypk);
				en.RetrieveFromDBSources();
				rm.HisEn = en;

				// 如果是link.
				if (rm.refMethodType == RefMethodType.LinkModel || rm.refMethodType == RefMethodType.LinkeWinOpen || rm.refMethodType == RefMethodType.RightFrameOpen)
				{
					Object tempVar2 = rm.Do(null);
					String url = tempVar2 instanceof String ? (String)tempVar2 : null;
					if (DataType.IsNullOrEmpty(url))
					{
						infos += "err@应该返回的url.";
						break;
					}

					infos += "url@" + url;
					break;
				}

				Object obj = rm.Do(null);
				if (obj == null)
				{
					infos += "close@info";
					break;
				}

				String result = obj.toString();
				if (result.indexOf("url@") != -1)
				{
					infos += result;
					break;
				}
				if (result.indexOf("err@") != -1)
				{
					errCount++;
				}
				else
				{
					sucCount++;
				}
				result = result.replace("err@", "");
				infos += "close@" + result + "<br/>";
			}
			if (pk.indexOf(",") != -1)
			{
				infos = "一共选择" + count + "笔数据,其中[" + sucCount + "]执行成功,[" + errCount + "]执行失败.<br/>" + infos;
			}
			return infos;
		}
			///#endregion 处理无参数的方法.

		DataSet ds = new DataSet();

		//转化为json 返回到前台解析. 处理有参数的方法.
		Attrs attrs = rm.getHisAttrs();
		MapAttrs mapAttrs = rm.getHisAttrs().ToMapAttrs();

		//属性.
		DataTable attrDt = mapAttrs.ToDataTableField("Sys_MapAttrs");
		ds.Tables.add(attrDt);

			///#region 该方法的默认值.
		DataTable dtMain = new DataTable();
		dtMain.TableName = "MainTable";
		for (MapAttr attr : mapAttrs.ToJavaList())
		{
			dtMain.Columns.Add(attr.getKeyOfEn(), String.class);
		}

		DataRow mydrMain = dtMain.NewRow();
		for (MapAttr item : mapAttrs.ToJavaList())
		{
			String v = item.getDefValReal();
			if (v.indexOf('@') == -1)
			{
				if (en.getRow().containsKey(item.getKeyOfEn()) == true)
				{
					mydrMain.setValue(item.getKeyOfEn(), en.GetValByKey(item.getKeyOfEn()));
				}
				else
				{
					mydrMain.setValue(item.getKeyOfEn(), item.getDefValReal());
				}
			}

			//替换默认值的@的
			else
			{
				if (v.equals("@WebUser.No"))
				{
					mydrMain.setValue(item.getKeyOfEn(), WebUser.getNo());
				}
				else if (v.equals("@WebUser.getName()"))
				{
					mydrMain.setValue(item.getKeyOfEn(), WebUser.getName());
				}
				else if (v.equals("@WebUser.FK_Dept")  || v.equals("@WebUser.DeptNo"))
				{
					mydrMain.setValue(item.getKeyOfEn(), WebUser.getDeptNo());
				}
				else if (v.equals("@WebUser.FK_DeptName"))
				{
					mydrMain.setValue(item.getKeyOfEn(), WebUser.getDeptName());
				}
				else if (v.equals("@WebUser.FK_DeptNameOfFull") || v.equals("@WebUser.FK_DeptFullName"))
				{
					mydrMain.setValue(item.getKeyOfEn(), WebUser.getDeptNameOfFull());
				}
				else if (v.equals("@RDT"))
				{
					if (item.getMyDataType()== DataType.AppDate)
					{
						mydrMain.setValue(item.getKeyOfEn(), DataType.getCurrentDate());
					}
					if (item.getMyDataType()== DataType.AppDateTime)
					{
						mydrMain.setValue(item.getKeyOfEn(), DataType.getCurrentDateTime());
					}
				}
				else
				{
					//如果是EnsName中字段
					if (en.GetValByKey(v.replace("@", "")) != null)
					{
						mydrMain.setValue(item.getKeyOfEn(), en.GetValByKey(v.replace("@", "")).toString());
					}

				}
			}
		}
		dtMain.Rows.add(mydrMain);
		ds.Tables.add(dtMain);
		///#endregion 该方法的默认值.

		///#region 加入该方法的外键.
		for (DataRow dr : attrDt.Rows)
		{
			String lgType = dr.get("LGType").toString();
			if (lgType.equals("2") == false)
			{
				continue;
			}

			String UIIsEnable = dr.get("UIVisible").toString();
			if (Objects.equals(UIIsEnable, "0"))
			{
				continue;
			}

			String uiBindKey = dr.get("UIBindKey").toString();
			if (DataType.IsNullOrEmpty(uiBindKey) == true)
			{
				String myPK = dr.get("MyPK").toString();
				/*如果是空的*/
				//   throw new Exception("@属性字段数据不完整，流程:" + fl.No + fl.Name + ",节点:" + nd.NodeID + nd.Name + ",属性:" + myPK + ",的UIBindKey IsNull ");
			}

			// 检查是否有下拉框自动填充。
			String keyOfEn = dr.get("KeyOfEn").toString();
			String fk_mapData = dr.get("FK_MapData").toString();
			if (ds.Tables.contains(uiBindKey) == false)
			{
				ds.Tables.add(bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null));
			}

		}

		//加入sql模式的外键.
		for (Attr attr : attrs)
		{
			if (attr.getItIsRefAttr() == true)
			{
				continue;
			}

			if (DataType.IsNullOrEmpty(attr.getUIBindKey()) || attr.getUIBindKey().length() <= 10)
			{
				continue;
			}

			if (attr.getUIIsReadonly() == true)
			{
				continue;
			}

			if (attr.getUIBindKey().contains("SELECT") == true || attr.getUIBindKey().contains("select") == true)
			{
				/*是一个sql*/
				Object tempVar3 = attr.getUIBindKey();
				String sqlBindKey = tempVar3 instanceof String ? (String)tempVar3 : null;
				sqlBindKey = Glo.DealExp(sqlBindKey, en, null);

				DataTable dt1 = DBAccess.RunSQLReturnTable(sqlBindKey);
				dt1.TableName = attr.getKey();

				//@杜. 翻译当前部分.
				if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
				{
					dt1.Columns.get("NO").ColumnName = "No";
					dt1.Columns.get("NAME").ColumnName = "Name";
				}
				if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
				{
					dt1.Columns.get("no").ColumnName = "No";
					dt1.Columns.get("name").ColumnName = "Name";
				}

				if (ds.Tables.contains(attr.getKey()) == false)
				{
					ds.Tables.add(dt1);
				}

			}
		}
		///#region 加入该方法的枚举.
		DataTable dtEnum = new DataTable();
		dtEnum.Columns.Add("Lab", String.class);
		dtEnum.Columns.Add("EnumKey", String.class);
		dtEnum.Columns.Add("IntKey", String.class);
		dtEnum.TableName = "Sys_Enum";

		for (Attr item : attrs)
		{
			if (item.getItIsEnum() == false)
			{
				continue;
			}

			SysEnums ses = new SysEnums(item.getUIBindKey(), item.UITag);
			for (SysEnum se : ses.ToJavaList())
			{
				DataRow drEnum = dtEnum.NewRow();
				drEnum.setValue("Lab", se.getLab());
				drEnum.setValue("EnumKey", se.getEnumKey());
				drEnum.setValue("IntKey", se.getIntKey());
				dtEnum.Rows.add(drEnum);
			}

		}

		ds.Tables.add(dtEnum);
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
			///#endregion 加入该方法的枚举.

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
			///#region 增加该方法的信息
		DataTable dt = new DataTable();
		dt.TableName = "RM";
		dt.Columns.Add("Title", String.class);
		dt.Columns.Add("Warning", String.class);

		DataRow mydr = dt.NewRow();
		mydr.setValue("Title", rm.Title);
		mydr.setValue("Warning", rm.Warning);
		dt.Rows.add(mydr);
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
			///#endregion 增加该方法的信息

		//增加到里面.
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}

	public final String Ens_Init() throws Exception {
		//定义容器.
		DataSet ds = new DataSet();

		//查询出来从表数据.
		Entities dtls = ClassFactory.GetEns(this.getEnsName());
		dtls.RetrieveAll();
		Entity en = dtls.getNewEntity();
		//QueryObject qo = new QueryObject(dtls);
		//qo.addOrderBy(en.PK);
		//qo.DoQuery();
		ds.Tables.add(dtls.ToDataTableField("Ens"));

		//实体.
		Entity dtl = dtls.getNewEntity();
		//定义Sys_MapData.
		MapData md = new MapData();
		md.setNo(this.getEnName());
		md.setName(dtl.getEnDesc());

			///#region 加入权限信息.
		//把权限加入参数里面.
		if (dtl.getHisUAC().IsInsert)
		{
			md.SetPara("IsInsert", "1");
		}
		if (dtl.getHisUAC().IsUpdate)
		{
			md.SetPara("IsUpdate", "1");
		}
		if (dtl.getHisUAC().IsDelete)
		{
			md.SetPara("IsDelete", "1");
		}
		///#endregion 加入权限信息.

		///#region 判断主键是否为自增长
		if (en.getItIsNoEntity() == true && en.getEnMap().getItIsAutoGenerNo())
		{
			md.SetPara("IsNewRow", "0");
		}
		else
		{
			md.SetPara("IsNewRow", "1");
		}
			///#region 添加EN的主键
		md.SetPara("PK", en.getPK());
			///#endregion

		ds.Tables.add(md.ToDataTableField("Sys_MapData"));

			///#region 字段属性.
		MapAttrs attrs = dtl.getEnMap().getAttrs().ToMapAttrs();
		DataTable sys_MapAttrs = attrs.ToDataTableField("Sys_MapAttr");
		ds.Tables.add(sys_MapAttrs);
			///#region 把外键与枚举放入里面去.
		for (MapAttr mapAttr : attrs.ToJavaList())
		{
			String uiBindKey = mapAttr.getUIBindKey();

			if (DataType.IsNullOrEmpty(uiBindKey) == true || mapAttr.getUIIsEnable() == false)
			{
				continue;
			}

			// 判断是否存在.
			if (ds.Tables.contains(uiBindKey) == true)
			{
				continue;
			}
			if (uiBindKey.toUpperCase().trim().startsWith("SELECT") == true)
			{
				String sqlBindKey = Glo.DealExp(uiBindKey, en, null);

				DataTable dt = DBAccess.RunSQLReturnTable(sqlBindKey);
				dt.TableName = mapAttr.getKeyOfEn();
				if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
				{
					dt.Columns.get("NO").ColumnName = "No";
					dt.Columns.get("NAME").ColumnName = "Name";
				}
				if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
				{
					dt.Columns.get("no").ColumnName = "No";
					dt.Columns.get("name").ColumnName = "Name";
				}

				ds.Tables.add(dt);
				continue;
			}

			if (mapAttr.getLGType() != FieldTypeS.FK)
			{
				continue;
			}

			ds.Tables.add(bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null));
		}

		String enumKeys = "";
		for (Attr attr : dtl.getEnMap().getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.Enum)
			{
				enumKeys += "'" + attr.getUIBindKey() + "',";
			}
		}

		if (enumKeys.length() > 2)
		{
			enumKeys = enumKeys.substring(0, enumKeys.length() - 1);

			String sqlEnum = "SELECT * FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey IN (" + enumKeys + ")";
			DataTable dtEnum = DBAccess.RunSQLReturnTable(sqlEnum);

			dtEnum.TableName = "Sys_Enum";

			if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
			{
				dtEnum.Columns.get("MYPK").ColumnName = "MyPK";
				dtEnum.Columns.get("LAB").ColumnName = "Lab";
				dtEnum.Columns.get("ENUMKEY").ColumnName = "EnumKey";
				dtEnum.Columns.get("INTKEY").ColumnName = "IntKey";
				dtEnum.Columns.get("LANG").ColumnName = "Lang";
			}
			if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
			{
				dtEnum.Columns.get("mypk").ColumnName = "MyPK";
				dtEnum.Columns.get("lab").ColumnName = "Lab";
				dtEnum.Columns.get("enumkey").ColumnName = "EnumKey";
				dtEnum.Columns.get("intkey").ColumnName = "IntKey";
				dtEnum.Columns.get("lang").ColumnName = "Lang";
			}
			ds.Tables.add(dtEnum);
		}
		///#endregion 把外键与枚举放入里面去.
		return bp.tools.Json.ToJson(ds);
	}

	///#region 实体集合的保存.
	/**
	 实体集合的删除

	 @return
	*/
	public final String Entities_Delete() throws Exception {

		if (this.getParas() == null)
		{
			return "err@删除实体，参数不能为空";
		}
		String[] myparas = this.getParas().split("[@]", -1);

		Entities ens = ClassFactory.GetEns(this.getEnsName());
		if (ens == null)
		{
			return "err@类" + this.getEnsName() + "不存在,请检查是不是拼写错误";
		}
		return Entities_Delete_Ext(ens);
	}
	public final String Entities_Delete_Ext(Entities ens)
	{
		try
		{
			String[] myparas = this.getParas().split("[@]", -1);
			ArrayList<String[]> paras = new ArrayList<String[]>();
			int idx = 0;
			for (int i = 0; i < myparas.length; i++)
			{
				String para = myparas[i];
				if (DataType.IsNullOrEmpty(para) || para.contains("=") == false)
				{
					continue;
				}

				String[] strs = para.split("[=]", -1);
				paras.add(strs);
			}

			if (paras.size() == 1)
			{
				ens.Delete(paras.get(0)[0], paras.get(0)[1]);
			}

			if (paras.size() == 2)
			{
				ens.Delete(paras.get(0)[0], paras.get(0)[1], paras.get(1)[0], paras.get(1)[1]);
			}

			if (paras.size() == 3)
			{
				ens.Delete(paras.get(0)[0], paras.get(0)[1], paras.get(1)[0], paras.get(1)[1], paras.get(2)[0], paras.get(2)[1]);
			}

			if (paras.size() == 4)
			{
				ens.Delete(paras.get(0)[0], paras.get(0)[1], paras.get(1)[0], paras.get(1)[1], paras.get(2)[0], paras.get(2)[1], paras.get(3)[0], paras.get(3)[1]);
			}

			if (paras.size() > 4)
			{
				return "err@实体类的删除，条件不能大于4个";
			}

			return "删除成功";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 初始化

	 @return
	*/
	public final String Entities_Save()
	{
		try
		{
			///#region  查询出来s实体数据.
			Entities dtls = ClassFactory.GetEns(this.getEnsName());
			if (dtls == null)
			{
				return "err@类" + this.getEnsName() + "不存在,请检查是不是拼写错误";
			}

			dtls.RetrieveAll();
			Entity en = dtls.getNewEntity();

			Map map = en.getEnMap();
			for (Entity item : dtls)
			{
				String pkval = item.getPKVal().toString();

				for (Attr attr : map.getAttrs())
				{
					if (attr.getItIsRefAttr() == true)
					{
						continue;
					}
					if (attr.getUIVisible()== false || attr.getUIIsReadonly() == true)
					{
						continue;
					}
					String key = pkval + "_" + attr.getKey();
					if (attr.getMyDataType() == DataType.AppDateTime || attr.getMyDataType() == DataType.AppDate)
					{
						String val = this.GetValFromFrmByKey("TB_" + key, null);
						item.SetValByKey(attr.getKey(), val);
						continue;
					}


					if (attr.getUIContralType() == UIContralType.TB)
					{
						String val = this.GetValFromFrmByKey("TB_" + key, null);
						item.SetValByKey(attr.getKey(), val);
						continue;
					}

					if (attr.getUIContralType() == UIContralType.DDL)
					{
						String val = this.GetValFromFrmByKey("DDL_" + key);
						item.SetValByKey(attr.getKey(), val);
						continue;
					}

					if (attr.getUIContralType() == UIContralType.CheckBok && attr.getUIIsReadonly() == false)
					{
						String val = this.GetValFromFrmByKey("CB_" + key, "-1");
						if (Objects.equals(val, "-1"))
						{
							item.SetValByKey(attr.getKey(), 0);
						}
						else
						{
							item.SetValByKey(attr.getKey(), 1);
						}
						continue;
					}
				}

				item.Update(); //执行更新.
			}
				///#region 保存新加行.
			String strs = this.GetRequestVal("NewPKVals");
			//没有新增行
			if (this.GetRequestValBoolen("InsertFlag") == false || (en.getEnMap().getItIsAutoGenerNo() == true && DataType.IsNullOrEmpty(strs) == true))
			{
				return "更新成功.";
			}

			String valValue = "";
			String[] pkVals = strs.split("[,]", -1);
			for (String pkval : pkVals)
			{
				if (DataType.IsNullOrEmpty(pkval) == true)
				{
					continue;
				}
				for (Attr attr : map.getAttrs())
				{

					if (attr.getMyDataType() == DataType.AppDateTime || attr.getMyDataType() == DataType.AppDate)
					{
						if (attr.getUIIsReadonly() == false)
						{
							continue;
						}

						valValue = this.GetValFromFrmByKey("TB_" + pkval + "_" + attr.getKey(), null);
						en.SetValByKey(attr.getKey(), valValue);
						continue;
					}

					if (attr.getUIContralType() == UIContralType.TB && attr.getUIIsReadonly() == false)
					{
						valValue = this.GetValFromFrmByKey("TB_" + pkval + "_" + attr.getKey());
						en.SetValByKey(attr.getKey(), valValue);
						continue;
					}

					if (attr.getUIContralType() == UIContralType.DDL && attr.getUIIsReadonly() == false)
					{
						valValue = this.GetValFromFrmByKey("DDL_" + pkval + "_" + attr.getKey());
						en.SetValByKey(attr.getKey(), valValue);
						continue;
					}

					if (attr.getUIContralType() == UIContralType.CheckBok && attr.getUIIsReadonly() == false)
					{
						valValue = this.GetValFromFrmByKey("CB_" + pkval + "_" + attr.getKey(), "-1");
						if (Objects.equals(valValue, "-1"))
						{
							en.SetValByKey(attr.getKey(), 0);
						}
						else
						{
							en.SetValByKey(attr.getKey(), 1);
						}
						continue;
					}
				}

				if (en.getItIsNoEntity())
				{
					if (en.getEnMap().getItIsAutoGenerNo())
					{
						en.SetValByKey("No", en.GenerNewNoByKey("No", null));
					}
				}

				try
				{
					if (Objects.equals(en.getPKVal().toString(), "0"))
					{
					}
					else
					{
						en.Insert();
					}
				}
				catch (RuntimeException ex)
				{
					//异常处理..
					Log.DebugWriteError(ex.getMessage());
					return ex.getMessage();
				}

			}



//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
				///#endregion 保存新加行.

			return "保存成功.";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	///#region 获取批处理的方法.
	public final String Refmethod_BatchInt() throws Exception {
		String ensName = this.getEnsName();
		Entities ens = ClassFactory.GetEns(ensName);
		Entity en = ens.getNewEntity();
		RefMethods rms = en.getEnMap().getHisRefMethods();
		DataTable dt = new DataTable();
		dt.TableName = "RM";
		dt.Columns.Add("No");
		dt.Columns.Add("Title");
		dt.Columns.Add("Tip");
		dt.Columns.Add("Visable");

		dt.Columns.Add("Url");
		dt.Columns.Add("Target");
		dt.Columns.Add("Warning");
		dt.Columns.Add("RefMethodType");
		dt.Columns.Add("GroupName");
		dt.Columns.Add("W");
		dt.Columns.Add("H");
		dt.Columns.Add("Icon");
		dt.Columns.Add("IsCanBatch");
		dt.Columns.Add("RefAttrKey");
		dt.Columns.Add("IsHaveFuncPara");
		for (RefMethod item : rms)
		{
			if (item.ItIsCanBatch == false)
			{
				continue;
			}
			DataRow mydr = dt.NewRow();
			item.HisEn = en; // 增加上.
			String myurl = "";
			if (item.refMethodType != RefMethodType.Func)
			{
				Object tempVar = item.Do(null);
				myurl = tempVar instanceof String ? (String)tempVar : null;
				if (myurl == null)
				{
					continue;
				}
			}
			else
			{
				myurl = "../Comm/RefMethod.htm?Index=" + item.Index + "&EnName=" + en.toString() + "&EnsName=" + en.GetNewEntities().toString() + "&PKVal=" + this.getPKVal();
			}

			DataRow dr = dt.NewRow();

			dr.setValue("No", item.Index);
			dr.setValue("Title", item.Title);
			dr.setValue("Tip", item.ToolTip);
			dr.setValue("Visable", item.Visable);
			dr.setValue("Warning", item.Warning);

			dr.setValue("RefMethodType", item.refMethodType.getValue());
			dr.setValue("RefAttrKey", item.RefAttrKey);
			dr.setValue("URL", myurl);
			dr.setValue("W", item.Width);
			dr.setValue("H", item.Height);
			dr.setValue("Icon", item.Icon);
			dr.setValue("IsCanBatch", item.ItIsCanBatch);
			dr.setValue("GroupName", item.GroupName);
			dr.setValue("IsHaveFuncPara", item.getHisAttrs().isEmpty() ? 0 : 1);
			dt.Rows.add(dr);
		}

		return bp.tools.Json.ToJson(dt);
	}
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
		///#endregion

	public final String Refmethod_Done() throws Exception {
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		Entity en = ens.getNewEntity();
		String msg = "";

		String pk = this.getPKVal();

		if (pk.contains(",") == false)
		{
			/*批处理的方式.*/
			en.setPKVal(pk);

			en.RetrieveFromDBSources();
			msg = DoOneEntity(en, this.getIndex());
			if (msg == null)
			{
				return "close@info";
			}
			else if (msg.indexOf("@") != -1)
			{
				return msg;
			}
			else
			{
				return "info@" + msg;
			}
		}

		//如果是批处理.
		String[] pks = pk.split("[,]", -1);
		int count = 0;
		int sucCount = 0;
		int errCount = 0;
		for (String mypk : pks)
		{
			if (DataType.IsNullOrEmpty(mypk) == true)
			{
				continue;
			}
			count++;
			en.setPKVal(mypk);
			en.RetrieveFromDBSources();

			String s = DoOneEntity(en, this.getIndex());
			if (DataType.IsNullOrEmpty(s) == false)
			{
				if (s.indexOf("err@") != -1)
				{
					errCount++;
				}
				else
				{
					sucCount++;
				}
				if (en.getItIsNoEntity())
				{
					msg += "编号:" + en.GetValByKey("No") + ",名称:" + en.GetValByKey("Name") + ",执行结果:" + s + "<br/>";
				}
				else if (en.getItIsOIDEntity())
				{
					if (DataType.IsNullOrEmpty(en.GetValStringByKey("PrjNo")) == false)
					{
						msg += "编号:" + en.GetValStringByKey("PrjNo") + " 名称:" + en.GetValStringByKey("PrjName") + " 执行结果:" + s + "<br/>";
					}
					else
					{
						msg += "编号:" + en.GetValByKey("OID") + " 名称:" + en.GetValByKey("Title") + " 执行结果:" + s + "<br/>";
					}

				}
				else
				{
					msg += "主键:" + en.GetValStringByKey(en.getPK()) + s + "<br/>";
				}
			}

		}

		if (DataType.IsNullOrEmpty(msg) == true)
		{
			return "close@info";
		}
		if (pk.indexOf(",") != -1)
		{
			msg = "一共选择" + count + "笔数据,其中[" + sucCount + "]执行成功,[" + errCount + "]执行失败.<br/>" + msg;
		}
		return "info@" + msg;
	}
	public final String DoOneEntity(Entity en, int rmIdx) throws Exception {
		RefMethod rm = en.getEnMap().getHisRefMethods().get(rmIdx);
		rm.HisEn = en;
		int mynum = 0;
		for (Attr attr : rm.getHisAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}
			mynum++;
		}

		Object[] objs = new Object[mynum];

		int idx = 0;
		for (Attr attr : rm.getHisAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}

			switch (attr.getUIContralType())
			{
				case TB:
					switch (attr.getMyDataType())
					{
						case DataType.AppString:
						case DataType.AppDate:
						case DataType.AppDateTime:
							String str1 = this.GetValFromFrmByKey(attr.getKey());
							objs[idx] = str1;
							//attr.getDefaultVal()=str1;
							break;
						case DataType.AppInt:
							int myInt = this.GetValIntFromFrmByKey(attr.getKey());
							objs[idx] = myInt;
							//attr.getDefaultVal()=myInt;
							break;
						case DataType.AppFloat:
							float myFloat = this.GetValFloatFromFrmByKey(attr.getKey());
							objs[idx] = myFloat;
							//attr.getDefaultVal()=myFloat;
							break;
						case DataType.AppDouble:
						case DataType.AppMoney:
							BigDecimal myDoub = this.GetValDecimalFromFrmByKey(attr.getKey());
							objs[idx] = myDoub;
							//attr.getDefaultVal()=myDoub;
							break;
						case DataType.AppBoolean:
							objs[idx] = this.GetValBoolenFromFrmByKey(attr.getKey());
							attr.setDefaultVal( false);
							break;
						default:
							throw new RuntimeException("没有判断的字段 - 数据类型．");

					}
					break;
				case DDL:
					try
					{
						if (attr.getMyDataType() == DataType.AppString)
						{
							String str = this.GetValFromFrmByKey(attr.getKey());
							objs[idx] = str;
							attr.setDefaultVal( str);
						}
						else
						{
							int enumVal = this.GetValIntFromFrmByKey(attr.getKey());
							objs[idx] = enumVal;
							attr.setDefaultVal( enumVal);
						}

					}
					catch (Exception e)
					{
						objs[idx] = null;
					}
					break;
				case CheckBok:
					objs[idx] = this.GetValBoolenFromFrmByKey(attr.getKey());

					attr.setDefaultVal( objs[idx].toString());

					break;
				default:
					break;
			}
			idx++;
		}

		try
		{
			Object obj = rm.Do(objs);
			if (obj != null)
			{
				return obj.toString();
			}

			return null;
		}
		catch (Exception ex)
		{
			String msg = "";
			for (Object obj : objs)
			{
				msg += "@" + obj.toString();
			}
			String err = "@执行[" + this.getEnsName() + "][" + rm.ClassMethodName + "]期间出现错误：" + ex.getMessage() + " InnerException= " + ex.getCause() + "[参数为：]" + msg;
			return "<font color=red>" + err + "</font>";
		}
	}
	public final String SFTable() throws Exception {
		SFTable sftable = new SFTable(this.GetRequestVal("SFTable"));
		DataTable dt = sftable.GenerHisDataTable(null);
		return bp.tools.Json.ToJson(dt);
	}
	/**
	 获得一个实体的数据

	 @return
	*/
	public final String EnsData() throws Exception {
		Entities ens = ClassFactory.GetEns(this.getEnsName());

		String filter = this.GetRequestVal("Filter");

		if (filter == null || Objects.equals(filter, "") || filter.contains("=") == false)
		{
			ens.RetrieveAll();
		}
		else
		{
			QueryObject qo = new QueryObject(ens);
			String[] strs = filter.split("[=]", -1);
			qo.AddWhere(strs[0], strs[1]);
			qo.DoQuery();
		}
		return ens.ToJson("dt");
	}
	/**
	 执行一个SQL，然后返回一个列表.
	 用于gener.js 的公共方法.

	 @return
	*/
	public final String SQLList() throws Exception {
		String sqlKey = this.GetRequestVal("SQLKey"); //SQL的key.
		String paras = this.GetRequestVal("Paras"); //参数. 格式为 @para1=paraVal@para2=val2

		SQLList sqlXml = new SQLList(sqlKey);

		//获得SQL
		String sql = sqlXml.getSQL();
		String[] strs = paras.split("[@]", -1);
		for (String str : strs)
		{
			if (str == null || Objects.equals(str, ""))
			{
				continue;
			}

			//参数.
			String[] p = str.split("[=]", -1);
			sql = sql.replace("@" + p[0], p[1]);
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}
	public final String EnumList() throws Exception {
		SysEnums ses = new SysEnums();
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			ses.Retrieve(SysEnumAttr.EnumKey, this.getEnumKey(), SysEnumAttr.OrgNo, WebUser.getOrgNo(), null);
			if (ses.isEmpty())
			{
				EnumInfoXml xml = new EnumInfoXml(this.getEnumKey());
				ses.RegIt(this.getEnumKey(), xml.getVals());
			}
		}
		else
		{
			ses = new SysEnums(this.getEnumKey());
		}
		return ses.ToJson("dt");
	}
		///#region 执行方法.
	/**
	 执行方法

	 @param clsName 类名称
	 @param methodName 方法名称
	 @return 执行结果
	*/
	public final String Exec(String clsName, String methodName) throws Exception {
		return Exec(clsName, methodName, null);
	}

	public final String Exec(String clsName, String methodName, String paras) throws Exception {

		///#region 处理 HttpHandler 类.
		if (clsName.toUpperCase().contains(".HttpHa" +
				"ndler.") == true)
		{
			//创建类实体.
			String baseName = bp.sys.base.Glo.DealClassEntityName("bp.difference.handler.WebContralBase");

			Object tempVar = java.lang.Class.forName(baseName).newInstance();
			DirectoryPageBase ctrl = tempVar instanceof DirectoryPageBase ? (DirectoryPageBase)tempVar : null;
			try
			{
				//执行方法返回json.
				String data = ctrl.DoMethod(ctrl, methodName);
				return data;
			}
			catch (RuntimeException ex)
			{
				String parasStr = "";
				for (Object key : CommonUtils.getRequest().getParameterMap().keySet())
				{
					parasStr += "@" + key + "=" + ContextHolderUtils.getRequest().getParameter(String.valueOf(key));
				}
				return "err@" + ex.getMessage() + " 参数:" + parasStr;
			}
		}
		try
		{
			//创建类实体.
			Object tempVar2 = java.lang.Class.forName("bp.en.Entity").newInstance();
			Entity en = tempVar2 instanceof Entity ? (Entity)tempVar2 : null;
			en.setPKVal(this.getPKVal());
			en.RetrieveFromDBSources();

			java.lang.Class tp = en.getClass();
			java.lang.reflect.Method mp = null;
			for (java.lang.reflect.Method m : tp.getMethods()) {
				if (m.getName().equals(methodName)==true) {
					mp = m;
					break;
				}
			}
			if (mp == null)
			{
				return "err@没有找到类[" + clsName + "]方法[" + methodName + "].";
			}

			//执行该方法.
			Object[] myparas = null;
			Object tempVar3 = mp.invoke(this, myparas);
			String result = tempVar3 instanceof String ? (String)tempVar3 : null; //调用由此 MethodInfo 实例反射的方法或构造函数。
			return result;
		}
		catch (RuntimeException ex)
		{
			return "err@执行实体类的方法错误:" + ex.getMessage();
		}
	}
	/**
	 运行SQL

	 @return 返回影响行数
	*/
	public final String DBAccess_RunSQL() throws Exception {
		String sql = this.GetRequestVal("SQL");
		String dbSrc = this.GetRequestVal("DBSrc");
		sql = sql.replace("~~", "\"");
		sql = sql.replace("~", "'");
		sql = sql.replace("[%]", "%"); //防止URL编码
		sql = sql.replace("@WebUser.No", WebUser.getNo()); //替换变量.
		sql = sql.replace("@WebUser.Name", WebUser.getName()); //替换变量.
		sql = sql.replace("@WebUser.FK_Dept", WebUser.getDeptNo()); //替换变量.
		sql = sql.replace("@WebUser.DeptNo", WebUser.getDeptNo()); //替换变量.
		sql = sql.replace("@WebUser.DeptParentNo", WebUser.getDeptParentNo()); //替换变量.
		sql = sql.replace("@WebUser.OrgNo", WebUser.getOrgNo()); //替换变量.
		if (DataType.IsNullOrEmpty(dbSrc) == false && dbSrc.equals("local") == false)
		{
			SFDBSrc sfdb = new SFDBSrc(dbSrc);
			return String.valueOf(sfdb.RunSQL(sql));
		}

		return String.valueOf(DBAccess.RunSQL(sql));
	}
	/**
	 运行SQL返回DataTable

	 @return DataTable转换的json
	*/
	public final String DBAccess_RunSQLReturnTable() throws Exception{
		String sql = this.GetRequestVal("SQL");

        String aesKey = SystemConfig.getIsEncryptionKey();
        String uriComponent = AesEncodeUtil.decryptAES(sql, aesKey);
        sql = URLDecoder.decode(uriComponent, StandardCharsets.UTF_8.name());

		//判断是否是标记,没有空格.
		if (sql.contains(" ") == false)
			sql = bp.wf.SQLManager.GenerSQLByMark(sql);

		String dbSrc = this.GetRequestVal("DBSrc");
		sql = sql.replace("~", "'");
		sql = sql.replace("[%]", "%"); //防止URL编码

		sql = sql.replace("@WebUser.No", WebUser.getNo()); //替换变量.
		sql = sql.replace("@WebUser.Name", WebUser.getName()); //替换变量.
		sql = sql.replace("@WebUser.FK_Dept", WebUser.getDeptNo()); //替换变量.
		sql = sql.replace("@WebUser.DeptNo", WebUser.getDeptNo()); //替换变量.
		sql = sql.replace("@WebUser.DeptParentNo", WebUser.getDeptParentNo()); //替换变量.
		sql = sql.replace("@WebUser.OrgNo", WebUser.getOrgNo()); //替换变量.

		sql = sql.replace("/#", "+"); //为什么？
		sql = sql.replace("/$", "-"); //为什么？
		sql = sql.replace("‘", "'");
		sql = sql.replace("’", "'");

		if (Objects.equals(null, sql) || Objects.equals("", sql)) {
			return "err@查询sql为空";
		}
		DataTable dt = null;
		if (DataType.IsNullOrEmpty(dbSrc) == false && dbSrc.equals("local") == false) {
			SFDBSrc sfdb = new SFDBSrc(dbSrc);
			dt = sfdb.RunSQLReturnTable(sql);
		} else {
			dt = DBAccess.RunSQLReturnTable(sql);
		}

		//暂定
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase) {
			//获取SQL的字段
			//获取 from 的位置
			sql = sql.replace(" ", "");
			if(sql.startsWith("\n"))
				sql = sql.replace("\n","");
			int index = sql.toUpperCase().indexOf("FROM");
			int indexAs = 0;
			sql = sql.substring(6, index);
			String[] keys = sql.split("[,]", -1);
			for (String key : keys) {
				String realkey = key.replace("Case", "").replace("case", "").replace("CASE", "");
				indexAs = realkey.toUpperCase().indexOf("AS");
				if (indexAs != -1) {
					realkey = realkey.substring(indexAs + 2);
				}else{
					if(key.indexOf(".")!=-1){
						String[] strs = key.split("[.]");
						if(strs.length==2)
							realkey = strs[1];
						else
							realkey = key;
					}else{
						realkey = key;
					}
				}
				if (dt.Columns.get(realkey.toUpperCase()) != null) {
					dt.Columns.get(realkey.toUpperCase()).ColumnName = realkey;
				}
			}

		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase) {
			//获取SQL的字段
			//获取 from 的位置
			sql = sql.replace(" ", "");
			if(sql.startsWith("\n"))
				sql = sql.replace("\n","");
			int index = sql.toUpperCase().indexOf("FROM");
			int indexAs = 0;
			sql = sql.substring(6, index);
			String[] keys = sql.split("[,]", -1);
			for (String key : keys) {
				String realkey = key.replace("Case", "").replace("case", "").replace("CASE", "");
				indexAs = realkey.toUpperCase().indexOf("AS");
				if (indexAs != -1) {
					realkey = realkey.substring(indexAs + 2);
				}else{
					if(key.indexOf(".")!=-1){
						String[] strs = key.split("[.]");
						if(strs.length==2)
							realkey = strs[1];
						else
							realkey = key;
					}else{
						realkey = key;
					}

				}
				if (dt.Columns.get(realkey.toLowerCase()) != null) {
					dt.Columns.get(realkey.toLowerCase()).ColumnName = realkey;
				}
			}

		}

		return bp.tools.Json.ToJson(dt);
	}
	public final String RunUrlCrossReturnString()
	{
		String url = this.GetRequestVal("urlExt");
		String strs = DataType.ReadURLContext(url, 9999);
		return strs;
	}
	/**
	 通过接口返回JSON数据
	 @return
	*/
	public final String RunWebAPIReturnString() throws Exception
	{
		//设置请求头
		Hashtable headerMap = new Hashtable();

		//设置返回值格式
		headerMap.put("Content-Type", "application/json");

		//设置token，用于接口校验
		String token = WebUser.getToken();
		if (DataType.IsNullOrEmpty(token) == true || token.contains(" "))
			throw new RuntimeException("err@非法的Token.");
		if (token.length() <= 6)
			throw new Exception("err@非法的Token." + token);
		headerMap.put("Authorization", token);

		String url = this.GetRequestVal("url");
	   String postData = bp.tools.HttpClientUtil.doPost(url,null, headerMap);

		JSONObject res = JSONObject.fromObject(postData);
		if (res.get("code").toString().equals("200"))
		{
			return res.get("data").toString();
		}
		else
		{
			return "[]";
		}
	}
		///#endregion
	public ConcurrentHashMap<String, Object> ClsCache = new ConcurrentHashMap<>();
	//执行方法.
	public final String HttpHandler() throws Exception {
		String httpHandlerName = this.GetRequestVal("HttpHandlerName");
		httpHandlerName = httpHandlerName.replace("BP.WF.HttpHandler", "bp.wf.httphandler");
		httpHandlerName = httpHandlerName.replace("BP.CCBill", "bp.ccbill");
		httpHandlerName = httpHandlerName.replace("BP.Cloud.HttpHandler", "bp.cloud.httphandler");
		//httpHandlerName = httpHandlerName.replace("BP.IC","bp.ic");
		String methodName = this.GetRequestVal("DoMethod");
		Object tempVar = ClsCache.get(httpHandlerName);
		if (tempVar == null) {
			tempVar = ClassFactory.GetHandlerPage(httpHandlerName);
		}
		if (tempVar != null) {
			DirectoryPageBase en = tempVar instanceof DirectoryPageBase ? (DirectoryPageBase) tempVar : null;
			if (en == null) {
				return "err@页面处理类名[" + httpHandlerName + "],没有获取到，请检查拼写错误？";
			}
			ClsCache.putIfAbsent(httpHandlerName, en);
			en.context = this.context;
			en.setWorkID(0); //从缓存中获取时，WorkID的初始值有可能不为0
			return en.DoMethod(en, methodName);
		}
		Class<?> type = java.lang.Class.forName(httpHandlerName);
		Object tempVar2 = type.newInstance();
		DirectoryPageBase en = tempVar2 instanceof DirectoryPageBase ? (DirectoryPageBase) tempVar2 : null;
		ClsCache.putIfAbsent(httpHandlerName, en);
		if (en == null) {
			return "err@页面处理类名[" + httpHandlerName + "],没有获取到，请检查拼写错误？";
		}
		return en.DoMethod(en, methodName);
	}
	/**
	 当前登录人员信息
	 @return
	*/
	public final String GuestUser_Init()
	{
		Hashtable ht = new Hashtable();

		String userNo = GuestUser.getNo();
		if (DataType.IsNullOrEmpty(userNo) == true)
		{
			ht.put("No", "");
			ht.put("Name", "");
			return bp.tools.Json.ToJson(ht);
		}

		ht.put("No", GuestUser.getNo());
		ht.put("Name", GuestUser.getName());
		return bp.tools.Json.ToJson(ht);
	}

	/**
	 当前登录人员信息

	 @return
	*/
	public final String WebUser_Init() throws Exception {
		Hashtable ht = new Hashtable();
		String token = this.GetRequestVal("Token");
		if (DataType.IsNullOrEmpty(token) == false)
		{
			if (DataType.IsNullOrEmpty(WebUser.getToken()) == false && token.equals(WebUser.getToken()) == true)
			{

			}
			else
			{
				Dev2Interface.Port_LoginByToken(token);
			}
		}
		if (DataType.IsNullOrEmpty(token) == true)
		{
			String userNo = WebUser.getNo();
			if (DataType.IsNullOrEmpty(userNo) == true)
			{
				token = WebUser.getToken();
				if (DataType.IsNullOrEmpty(token) == true)
				{
					throw new RuntimeException("err@ 登录已过期，请重新登录!");
				}

				Dev2Interface.Port_LoginByToken(token);
			}
		}
		//需要同步.
		ht.put("No", WebUser.getNo());
		ht.put("Name", WebUser.getName());
		ht.put("FK_Dept", WebUser.getDeptNo());
		ht.put("FK_DeptName", WebUser.getDeptName());
		ht.put("FK_DeptNameOfFull", WebUser.getDeptNameOfFull());
		ht.put("CustomerNo", SystemConfig.getCustomerNo());
		ht.put("CustomerName", SystemConfig.getCustomerName());
		ht.put("IsAdmin", WebUser.getIsAdmin() == true ? 1 : 0);
		ht.put("Token", WebUser.getToken()); //token.

		ht.put("Tel", WebUser.getTel());
		ht.put("OrgNo", WebUser.getOrgNo());
		ht.put("OrgName", WebUser.getOrgName());
		ht.put("IsFirstLogin", WebUser.getIsFirstLogin());
		ht.put("Roles", WebUser.getRoles());
		ht.put("RootNo", WebUser.getRootNo());
		//检查是否是授权状态.
		if (WebUser.getIsAuthorize() == true)
		{
			ht.put("IsAuthorize", "1");
			ht.put("Auth", WebUser.getAuth());
			ht.put("AuthName", WebUser.getAuthName());
		}
		else
		{
			ht.put("IsAuthorize", "0");
		}

		//每次访问表很消耗资源.
		//Port.WFEmp emp = new BP.Port.WFEmp(WebUser.getNo());
		//ht.Add("Theme", emp.GetParaString("Theme"));


		//增加运行模式. add by zhoupeng 2020.03.10 适应saas模式.
		ht.put("CCBPMRunModel", SystemConfig.GetValByKey("CCBPMRunModel", "0"));

		return bp.tools.Json.ToJson(ht);
	}

	public final String WebUser_BackToAuthorize() throws Exception {
		Dev2Interface.Port_Login(WebUser.getAuth());
		return "登录成功";
	}
	///#region 分组统计.
	/**
	 获得分组统计的查询条件.

	 @return
	*/
	public final String Group_MapBaseInfo() throws Exception {
		//获得
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		if (ens == null)
		{
			return "err@类名:" + this.getEnsName() + "错误";
		}

		Entity en = ens.getNewEntity();
		Map map = ens.getNewEntity().getEnMapInTime();

		Hashtable ht = new Hashtable();

		//把权限信息放入.
		UAC uac = en.getHisUAC();
		if (this.GetRequestValBoolen("IsReadonly"))
		{
			ht.put("IsUpdata", false);

			ht.put("IsInsert", false);
			ht.put("IsDelete", false);
		}
		else
		{
			ht.put("IsUpdata", uac.IsUpdate);

			ht.put("IsInsert", uac.IsInsert);
			ht.put("IsDelete", uac.IsDelete);
			ht.put("IsView", uac.IsView);
		}

		ht.put("IsExp", uac.IsExp); //是否可以导出?
		ht.put("IsImp", uac.IsImp); //是否可以导入?

		ht.put("EnDesc", en.getEnDesc()); //描述?
		ht.put("EnName", en.toString()); //类名?

		MapData mapData = new MapData();
		mapData.setNo(this.getEnsName());

			///#region 查询条件
		//单据，实体单据
		if (mapData.RetrieveFromDBSources() != 0 && DataType.IsNullOrEmpty(mapData.getFormTreeNo()) == false)
		{
			//查询条件.
			ht.put("IsShowSearchKey", mapData.GetParaInt("IsSearchKey", 0));
			ht.put("SearchFields", mapData.GetParaString("StringSearchKeys"));

			//按日期查询.
			ht.put("DTSearchWay", mapData.GetParaInt("DTSearchWay", 0));
			ht.put("DTSearchLabel", mapData.GetParaString("DTSearchLabel"));

		}
		else
		{
			if (map.ItIsShowSearchKey == true)
			{
				ht.put("IsShowSearchKey", 1);
			}
			else
			{
				ht.put("IsShowSearchKey", 0);
			}

			ht.put("SearchFields", map.SearchFields);
			ht.put("SearchFieldsOfNum", map.SearchFieldsOfNum);

			//按日期查询.
			ht.put("DTSearchWay", map.DTSearchWay.getValue());
			ht.put("DTSearchLabel", map.DTSearchLabel);
			ht.put("DTSearchKey", map.DTSearchKey);
		}
			///#endregion  查询条件

		//把map信息放入
		ht.put("PhysicsTable", map.getPhysicsTable());
		ht.put("CodeStruct", map.getCodeStruct());
		// ht.Add("CodeLength", map.CodeLength);
		return bp.tools.Json.ToJson(ht);
	}

	/**
	 外键或者枚举的分组查询条件.

	 @return
	*/
	public final String Group_SearchAttrs() throws Exception {
		//获得
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		if (ens == null)
		{
			return "err@类名错误:" + this.getEnsName();
		}

		Entity en = ens.getNewEntity();
		Map map = ens.getNewEntity().getEnMapInTime();

		DataSet ds = new DataSet();

		//构造查询条件集合.
		DataTable dt = new DataTable();
		dt.Columns.Add("Field");
		dt.Columns.Add("Name");
		dt.Columns.Add("MyFieldType");
		dt.TableName = "Attrs";

		SearchFKEnums attrs = map.getSearchFKEnums();
		for (SearchFKEnum item : attrs)
		{
			DataRow dr = dt.NewRow();
			dr.setValue("Field", item.Key);
			dr.setValue("Name", item.HisAttr.getDesc());
			dr.setValue("MyFieldType", item.HisAttr.getMyFieldType());
			dt.Rows.add(dr);
		}
		ds.Tables.add(dt);

		//把外键枚举增加到里面.
		for (SearchFKEnum item : attrs)
		{
			Attr attr = item.HisAttr;
			if (attr.getItIsEnum() == true)
			{
				SysEnums ses = new SysEnums(attr.getUIBindKey());
				DataTable dtEnum = ses.ToDataTableField("dt");
				dtEnum.TableName = item.Key;
				ds.Tables.add(dtEnum);
				continue;
			}

			if (attr.getItIsFK() == true)
			{
				Entities ensFK = item.HisAttr.getHisFKEns();
				ensFK.RetrieveAll();

				DataTable dtEn = ensFK.ToDataTableField("dt");
				dtEn.TableName = item.Key;

				ds.Tables.add(dtEn);
			}
			//绑定SQL的外键
			if (DataType.IsNullOrEmpty(attr.getUIBindKey()) == false && ds.Tables.contains(attr.getKey()) == false)
			{
				String sql = attr.getUIBindKey();
				DataTable dtSQl = null;
				//说明是实体类绑定的外部数据源
				if (sql.toUpperCase().contains("SELECT") == true)
				{
					//sql数据
					sql = Glo.DealExp(attr.getUIBindKey(), null, null);
					dtSQl = DBAccess.RunSQLReturnTable(sql);
				}
				else
				{
					dtSQl = bp.pub.PubClass.GetDataTableByUIBineKey(attr.getUIBindKey(), null);
				}
				for (DataColumn col : dtSQl.Columns)
				{
					String colName = col.ColumnName.toLowerCase();
					switch (colName)
					{
						case "no":
							col.ColumnName = "No";
							break;
						case "name":
							col.ColumnName = "Name";
							break;
						case "parentno":
							col.ColumnName = "ParentNo";
							break;
						default:
							break;
					}
				}
				dtSQl.TableName = item.Key;
				ds.Tables.add(dtSQl);
			}


		}

		return bp.tools.Json.ToJson(ds);
	}

	/**
	获取分组的外键、枚举

	 @return
	*/
	public final String Group_ContentAttrs() throws Exception {
		//获得
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		if (ens == null)
		{
			return "err@类名错误:" + this.getEnsName();
		}

		Entity en = ens.getNewEntity();
		Map map = null;
		if (this.getEnsName().indexOf("TS.") == 0)
			map = en.getEnMap();
		else
			map = en.getEnMapInTime();

		//Map map = ens.getNewEntity().getEnMapInTime();
		Attrs attrs = map.getAttrs();
		DataTable dt = new DataTable();
		dt.Columns.Add("Field");
		dt.Columns.Add("Name");
		dt.Columns.Add("Checked");
		dt.TableName = "Attrs";

		//获取注册信心表
		UserRegedit ur = new UserRegedit(WebUser.getNo(), this.getEnsName() + "_Group");

		//判断是否已经选择分组
		boolean contentFlag = false;
		for (Attr attr : attrs)
		{
			if (attr.getItIsEnum() == true || attr.getMyDataType() == DataType.AppBoolean ||attr.getUIContralType() == UIContralType.DDL || attr.getUIContralType() == UIContralType.RadioBtn)
			{
				DataRow dr = dt.NewRow();
				dr.setValue("Field", attr.getKey());
				dr.setValue("Name", attr.getDesc());

				// 根据状态 设置信息.
				if (ur.getVals().indexOf(attr.getKey()) != -1)
				{
					dr.setValue("Checked", "true");
					contentFlag = true;
				}
				dt.Rows.add(dr);
			}

		}

		if (contentFlag == false && dt.Rows.size() != 0)
		{
			dt.Rows.get(0).setValue("Checked","true");
		}

		return bp.tools.Json.ToJson(dt);
	}

	public final String Group_Analysis() throws Exception {
		//获得
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		if (ens == null)
		{
			return "err@类名错误:" + this.getEnsName();
		}

		Entity en = ens.getNewEntity();
		Map map = null;
		if (this.getEnsName().indexOf("TS.") == 0)
			map = en.getEnMap();
		else
			map = en.getEnMapInTime();
		//Map map = ens.getNewEntity().getEnMapInTime();
		DataSet ds = new DataSet();


		//获取注册信息表
		UserRegedit ur = new UserRegedit(WebUser.getNo(), this.getEnsName() + "_Group");

		DataTable dt = new DataTable();
		dt.Columns.Add("Field");
		dt.Columns.Add("Name");
		dt.Columns.Add("Checked");

		dt.TableName = "Attrs";

		//默认手动添加一个求数量的分析项
		DataRow dtr = dt.NewRow();
		dtr.setValue("Field", "Group_Number");
		dtr.setValue("Name", "数量");
		dtr.setValue("Checked", "true");
		dt.Rows.add(dtr);

		DataTable ddlDt = new DataTable();
		ddlDt.TableName = "Group_Number";
		ddlDt.Columns.Add("No");
		ddlDt.Columns.Add("Name");
		ddlDt.Columns.Add("Selected");
		DataRow ddlDr = ddlDt.NewRow();
		ddlDr.setValue("No", "SUM");
		ddlDr.setValue("Name", "求和");
		ddlDr.setValue("Selected", "true");
		ddlDt.Rows.add(ddlDr);
		ddlDr = ddlDt.NewRow();
		ddlDr.setValue("No", "AVG");
		ddlDr.setValue("Name", "求平均");
		if (ur.getVals().indexOf("@Group_Number=AVG") != -1)
		{
			ddlDr.setValue("Selected", "true");
		}
		ddlDt.Rows.add(ddlDr);
		ds.Tables.add(ddlDt);

		for (Attr attr : map.getAttrs())
		{
			if (attr.getItIsPK() || attr.getItIsNum() == false)
			{
				continue;
			}
			if (attr.getUIContralType() != UIContralType.TB)
			{
				continue;
			}
			if (attr.getUIVisible()== false)
			{
				continue;
			}
			if (attr.getMyFieldType() == FieldType.FK)
			{
				continue;
			}
			if (attr.getMyFieldType() == FieldType.Enum)
			{
				continue;
			}
			if (attr.getMyDataType() == DataType.AppBoolean)
				continue;
			if (Objects.equals(attr.getKey(), "OID") || Objects.equals(attr.getKey(), "WorkID") || Objects.equals(attr.getKey(), "MID"))
			{
				continue;
			}



			dtr = dt.NewRow();
			dtr.setValue("Field", attr.getKey());
			dtr.setValue("Name", attr.getDesc());


			// 根据状态 设置信息.
			if (ur.getVals().indexOf(attr.getKey()) != -1)
			{
				dtr.setValue("Checked", "true");
			}

			dt.Rows.add(dtr);

			ddlDt = new DataTable();
			ddlDt.Columns.Add("No");
			ddlDt.Columns.Add("Name");
			ddlDt.Columns.Add("Selected");
			ddlDt.TableName = attr.getKey();

			ddlDr = ddlDt.NewRow();
			ddlDr.setValue("No", "SUM");
			ddlDr.setValue("Name", "求和");
			if (ur.getVals().indexOf("@" + attr.getKey() + "=SUM") != -1)
			{
				ddlDr.setValue("Selected", "true");
			}
			ddlDt.Rows.add(ddlDr);

			ddlDr = ddlDt.NewRow();
			ddlDr.setValue("No", "AVG");
			ddlDr.setValue("Name", "求平均");
			if (ur.getVals().indexOf("@" + attr.getKey() + "=AVG") != -1)
			{
				ddlDr.setValue("Selected", "true");
			}
			ddlDt.Rows.add(ddlDr);

			if (this.getItIsContainsNDYF())
			{
				ddlDr = ddlDt.NewRow();
				ddlDr.setValue("No", "AMOUNT");
				ddlDr.setValue("Name", "求累计");
				if (ur.getVals().indexOf("@" + attr.getKey() + "=AMOUNT") != -1)
				{
					ddlDr.setValue("Selected", "true");
				}
				ddlDt.Rows.add(ddlDr);
			}

			ds.Tables.add(ddlDt);


		}

		ds.Tables.add(dt);
		return bp.tools.Json.ToJson(ds);
	}

	public final String Group_Search() throws Exception {
		//获得
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		if (ens == null)
		{
			return "err@类名错误:" + this.getEnsName();
		}

		Entity en = ens.getNewEntity();
		//Map map = ens.getNewEntity().getEnMapInTime();
		Map map = null;
		if (this.getEnsName().indexOf("TS.") == 0)
			map = en.getEnMap();
		else
			map = en.getEnMapInTime();
		DataSet ds = new DataSet();

		//获取注册信息表
		UserRegedit ur = new UserRegedit(WebUser.getNo(), this.getEnsName() + "_Group");

		// 查询出来关于它的活动列配置.
		ActiveAttrs aas = new ActiveAttrs();
		aas.RetrieveBy(ActiveAttrAttr.For, this.getEnsName());

		ds = GroupSearchSet(ens, en, map, ur, ds, aas);
		if (ds == null)
		{
			return "info@<img src='../Img/Warning.gif' /><b><font color=red> 您没有选择显示内容/分析项目</font></b>";
		}

		////不显示合计列。
		/*string NoShowSum =  BP.Difference.SystemConfig.GetConfigXmlEns("NoShowSum", this.EnsName);
		DataTable showSum = new DataTable("NoShowSum");
		showSum.Columns.Add("NoShowSum");
		DataRow sumdr = showSum.NewRow();
		sumdr["NoShowSum"] = NoShowSum;
		showSum.Rows.add(sumdr);

		DataTable activeAttr = aas.ToDataTable();
		activeAttr.TableName = "ActiveAttr";
		ds.Tables.add(activeAttr);
		ds.Tables.add(showSum);*/

		return bp.tools.Json.ToJson(ds);
	}

	private DataSet GroupSearchSet(Entities ens, Entity en, Map map, UserRegedit ur, DataSet ds, ActiveAttrs aas) throws Exception {

		//查询条件
		//分组
		String Condition = ""; //处理特殊字段的条件问题。

		AtPara atPara = new AtPara(ur.getVals());
		//获取分组的条件
		String groupKey = atPara.GetValStrByKey("SelectedGroupKey");
		//分析项
		String analyKey = atPara.GetValStrByKey("StateNumKey");

		//设置显示的列
		Attrs mapAttrOfShows = new Attrs();

		//查询语句定义
		String sql = "";
		String selectSQL = "SELECT "; //select部分的组合
		String groupBySQL = " GROUP BY "; //分组的组合
		///#region 分组条件的整合
		if (DataType.IsNullOrEmpty(groupKey) == false)
		{
			boolean isSelected = false;
			String[] SelectedGroupKeys = groupKey.split("[,]", -1);
			for (String key : SelectedGroupKeys)
			{
				if (DataType.IsNullOrEmpty(key) == true)
				{
					continue;
				}
				Attr attr = map.GetAttrByKey(key);
				// 加入组里面。
				mapAttrOfShows.Add(map.GetAttrByKey(key), false);

				selectSQL += map.getPhysicsTable() + "." + key + " \"" + key + "\",";

				groupBySQL += map.getPhysicsTable() + "." + key + ",";

				if (attr.getMyFieldType() == FieldType.FK)
				{
					Map fkMap = attr.getHisFKEn().getEnMap();
					String refText = fkMap.getPhysicsTable() + "_" + attr.getKey() + "." + fkMap.GetFieldByKey(attr.getUIRefKeyText());
					selectSQL += refText + "  AS " + key + "Text" + ",";
					groupBySQL += refText + ",";
					continue;
				}

				if (attr.getMyFieldType() == FieldType.Enum || attr.getMyFieldType() == FieldType.PKEnum)
				{
					//增加枚举字段
					if (DataType.IsNullOrEmpty(attr.getUIBindKey()))
					{
						throw new RuntimeException("@" + en.toString() + " key=" + attr.getKey() + " UITag=" + attr.UITag + "");
					}

					SysEnums ses = new SysEnums(attr.getUIBindKey(), attr.UITag);
					selectSQL += ses.GenerCaseWhenForOracle(en.getEnMap().getPhysicsTable() + ".", attr.getKey(), attr.getField(), attr.getUIBindKey(), attr.getDefaultVal().toString().equals("") == true ? "0" : attr.getDefaultVal().toString()) + ",";
					continue;
				}
				if(attr.getMyDataType() == DataType.AppBoolean)
				{
					selectSQL += " CASE " + en.getEnMap().getPhysicsTable() + "." + attr.getKey();
					selectSQL += " WHEN 0 THEN '否'";
					selectSQL += " WHEN 1 THEN '是'";
					selectSQL += " END \""+ attr.getKey() + "T\",";
					continue;
				}

				//不是外键、枚举，就是外部数据源
				selectSQL += map.getPhysicsTable() + "." + key + "T" + " \"" + key + "T\",";
				groupBySQL += map.getPhysicsTable() + "." + key + "T,";
			}
		}
		///#endregion 分组条件的整合
		///#region 分析项的整合
		Attrs AttrsOfNum = new Attrs();
		Attrs attrs = map.getAttrs();
		String[] analyKeys = analyKey.split("[,]", -1);
		for (String key : analyKeys)
		{
			if (DataType.IsNullOrEmpty(key) == true)
			{
				continue;
			}
			String[] strs = key.split("[=]", -1);
			if (strs.length != 2)
			{
				continue;
			}

			//求数据的总和
			if (strs[0].equals("Group_Number"))
			{
				selectSQL += " count(*) \"" + strs[0] + "\",";
				mapAttrOfShows.Add(new Attr("Group_Number", "Group_Number", 1, DataType.AppInt, false, "数量(合计)"), false);
				AttrsOfNum.Add(new Attr("Group_Number", "Group_Number", 1, DataType.AppInt, false, "数量"), false);
				continue;
			}

			//判断分析项的数据类型
			Attr attr =attrs.GetAttrByKeyOfEn(strs[0]);
			AttrsOfNum.Add(attr, false);

			int dataType = attr.getMyDataType();
			switch (strs[1])
			{
				case "SUM":
					if (dataType == 2)
					{
						selectSQL += " SUM(" + map.getPhysicsTable() + "." + strs[0] + ") \"" + strs[0] + "\",";
					}
					else
					{
						if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX)
						{
							selectSQL += " round ( cast (SUM(" + map.getPhysicsTable() + "." + strs[0] + ") as  numeric), 4)  \"" + strs[0] + "\",";
						}
						else
						{
							selectSQL += " round ( SUM(" + map.getPhysicsTable() + "." + strs[0] + "), 4) \"" + strs[0] + "\",";
						}
					}
					if(attr.getDesc().contains("(合计)") == false)
						attr.setDesc(attr.getDesc() + "(合计)");

					break;
				case "AVG":
					if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX)
					{
						selectSQL += " round ( cast (AVG(" + map.getPhysicsTable() + "." + strs[0] + ") as  numeric), 4)  \"" + strs[0] + "\",";
					}
					else
					{
						selectSQL += " round (AVG(" + map.getPhysicsTable() + "." + strs[0] + "), 4)  \"" + strs[0] + "\",";
					}
					if(attr.getDesc().contains("(平均)") == false)
						attr.setDesc(attr.getDesc() + "(平均)");
					break;
				case "AMOUNT":
					if (dataType == 2)
					{
						selectSQL += " SUM(" + map.getPhysicsTable() + "." + strs[0] + ") \"" + strs[0] + "\",";
					}
					else
					{
						if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX)
						{
							selectSQL += " round ( cast (SUM(" + map.getPhysicsTable() + "." + strs[0] + ") as  numeric), 4)  \"" + strs[0] + "\",";
						}
						else
						{
							selectSQL += " round ( SUM(" + map.getPhysicsTable() + "." + strs[0] + "), 4) \"" + strs[0] + "\",";
						}
					}
					if(attr.getDesc().contains("(累计)") == false)
						attr.setDesc(attr.getDesc() + "(累计)");
					break;
				default:
					throw new RuntimeException("没有判断的情况.");
			}
			mapAttrOfShows.Add(attr, false);

		}
			///#endregion 分析项的整合
		if (DataType.IsNullOrEmpty(selectSQL) == true || selectSQL.equals("SELECT ") == true)
		{
			return null;
		}
		selectSQL = selectSQL.substring(0, selectSQL.length() - 1);


		//获取查询的注册表
		UserRegedit searchUr = new UserRegedit();
		searchUr.setMyPK(WebUser.getNo() + "_" + this.getEnsName() + "_SearchAttrs");
		searchUr.RetrieveFromDBSources();

		QueryObject qo = Search_Data(ens, en, map, searchUr, "GroupSearch");

		String whereSQL = " " + qo.getSQL().substring(qo.getSQL().indexOf("FROM "));


		String orderbySQL = "";
		String orderByKey = this.GetRequestVal("OrderBy");
		if (DataType.IsNullOrEmpty(orderByKey) == false && selectSQL.contains(orderByKey) == true)
		{
			orderbySQL = " ORDER BY" + orderByKey;
			String orderWay = this.GetRequestVal("OrderWay");
			if (DataType.IsNullOrEmpty(orderWay) == false && orderWay.equals("Up") == false)
			{
				orderbySQL += " DESC ";
			}

		}

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
			///#endregion OrderBy语句组合

		sql = selectSQL + whereSQL + groupBySQL.substring(0, groupBySQL.length() - 1) + orderbySQL;

		DataTable dt = DBAccess.RunSQLReturnTable(sql, qo.getMyParas());
		dt.TableName = "MainData";

		ds.Tables.add(dt);
		ds.Tables.add(mapAttrOfShows.ToMapAttrs().ToDataTableField("Sys_MapAttr"));
		ds.Tables.add(AttrsOfNum.ToMapAttrs().ToDataTableField("AttrsOfNum"));

		return ds;
	}
	public final String ParseExpToDecimal() throws Exception {
		String exp = this.GetRequestVal("Exp");

		BigDecimal d = DataType.ParseExpToDecimal(exp);
		return d.toString();
	}


	public final boolean getItIsContainsNDYF()
	{
		if (Objects.equals(this.GetValFromFrmByKey("IsContainsNDYF").toString().toUpperCase(), "TRUE"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
		///#region 常用词汇功能开始
	/**
	 常用词汇

	 @return
	*/
	public final String HelperWordsData() throws Exception {

		String FK_MapData = this.GetRequestVal("FK_MapData");
		String AttrKey = this.GetRequestVal("AttrKey");
		String lb = this.GetRequestVal("lb");

		//读取txt文件
		if (Objects.equals(lb, "readWords"))
		{
			return readTxt();
		}

		//读取其他常用词汇
		DataSet ds = new DataSet();
		//我的词汇
		if (Objects.equals(lb, "myWords"))
		{
			DefVals dvs = new DefVals();
			QueryObject qo = new QueryObject(dvs);
			qo.AddHD();

			qo.addAnd();
			qo.AddWhere(DefValAttr.FrmID, "=", FK_MapData);
			qo.addAnd();
			qo.AddWhere(DefValAttr.AttrKey, "=", AttrKey);
			qo.addAnd();
			qo.AddWhere(DefValAttr.EmpNo, "=", WebUser.getNo());
			qo.addAnd();
			qo.AddWhere(DefValAttr.LB, "=", "1");

			String pageNumber = GetRequestVal("pageNumber");
			int iPageNumber =StringHelper.isNullOrEmpty(pageNumber) ? 1 : Integer.parseInt(pageNumber);
			//每页多少行
			String pageSize = GetRequestVal("pageSize");
			int iPageSize = StringHelper.isNullOrEmpty(pageSize) ? 9999 : Integer.parseInt(pageSize);

			DataTable dt = new DataTable("DataCount");
			dt.Columns.Add("DataCount", Integer.class);
			DataRow dr = dt.NewRow();
			dr.setValue("DataCount", qo.GetCount());
			dt.Rows.add(dr);
			ds.Tables.add(dt);
			qo.DoQuery("MyPK", iPageSize, iPageNumber);
			ds.Tables.add(dvs.ToDataTableField("MainTable")); //把描述加入.
		}
		if (Objects.equals(lb, "hisWords"))
		{
			Node nd = new Node(this.getNodeID());
			String rptNo = "ND" + Integer.parseInt(this.getFlowNo()) + "Rpt";
			if (nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.RefOneFrmTree)
			{
				MapData mapData = new MapData(this.getFrmID());
				rptNo = mapData.getPTable();
			}


			GEEntitys ges = new GEEntitys(rptNo);
			QueryObject qo = new QueryObject(ges);
			String fk_emp = this.GetRequestVal("FK_Emp");
			qo.AddWhere(fk_emp, "=", WebUser.getNo());
			qo.addAnd();
			qo.AddWhere(AttrKey, "!=", "");
			String pageNumber = GetRequestVal("pageNumber");
			int iPageNumber = StringHelper.isNullOrEmpty(pageNumber) ? 1 : Integer.parseInt(pageNumber);
			//每页多少行
			String pageSize = GetRequestVal("pageSize");
			int iPageSize = StringHelper.isNullOrEmpty(pageSize) ? 9999 : Integer.parseInt(pageSize);

			DataTable dt = new DataTable("DataCount");
			dt.Columns.Add("DataCount", Integer.class);
			DataRow dr = dt.NewRow();
			dr.setValue("DataCount", qo.GetCount());
			dt.Rows.add(dr);
			ds.Tables.add(dt);

			qo.DoQuery("OID", iPageSize, iPageNumber);

			dt = ges.ToDataTableField("dt");
			DataTable newDt = new DataTable("MainTable");
			newDt.Columns.Add("CurValue");
			newDt.Columns.Add("MyPk");
			for (DataRow drs : dt.Rows)
			{
				if (DataType.IsNullOrEmpty(drs.get(AttrKey).toString()))
				{
					continue;
				}
				dr = newDt.NewRow();
				dr.setValue("CurValue", drs.get(AttrKey));
				dr.setValue("MyPK", drs.get("OID"));
				newDt.Rows.add(dr);
			}

			ds.Tables.add(newDt); //把描述加入.


		}

		return DataTableConvertJson.DataTable2Json(ds.GetTableByName("MainTable"), Integer.parseInt(ds.GetTableByName("DataCount").Rows.get(0).getValue(0).toString()));
	}

	/**
	 注意特殊字符的处理

	 @return
	*/
	private String readTxt() throws Exception {
		try
		{
			String path = SystemConfig.getPathOfDataUser() + "Fastenter/" + getFK_MapData() + "/" + GetRequestVal("AttrKey");
			if (!(new File(path)).isDirectory())
			{
				(new File(path)).mkdirs();
			}

			File[] folderArray = (new File(path)).listFiles();
			if (folderArray.length == 0)
			{
				return "";
			}

			String fileName;
			String[] strArray;

			String pageNumber = GetRequestVal("pageNumber");
			int iPageNumber = DataType.IsNullOrEmpty(pageNumber) ? 1 : Integer.parseInt(pageNumber);
			String pageSize = GetRequestVal("pageSize");
			int iPageSize = DataType.IsNullOrEmpty(pageSize) ? 9999 : Integer.parseInt(pageSize);

			DataSet ds = new DataSet();
			DataTable dt = new DataTable("MainTable");
			dt.Columns.Add("MyPk", String.class);
			dt.Columns.Add("TxtStr", String.class);
			dt.Columns.Add("CurValue", String.class);

			String liStr = "";
			int count = 0;
			int index = iPageSize * (iPageNumber - 1);
			for (File file : folderArray)
			{

				if (count >= index && count < iPageSize * iPageNumber)
				{
					dt.Rows.get(count).setValue("MyPk",DBAccess.GenerGUID(0, null, null));


					fileName = file.getName().replace("\"", "").replace("'", "");
					BufferedReader reader = null;
					String tempString = null;
					try {
						reader = new BufferedReader(new FileReader(file));
						int line = 1;
						// 一次读入一行，直到读入null为文件结束
						while ((tempString = reader.readLine()) != null) {
							tempString += tempString;
							line++;
						}
						reader.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					liStr += String.format("%s},", bp.tools.DataTableConvertJson.GetFilteredStrForJSON(fileName),
							bp.tools.DataTableConvertJson.GetFilteredStrForJSON(tempString));

					dt.Rows.get(count).setValue("CurValue", bp.tools.DataTableConvertJson.GetFilteredStrForJSON(fileName));
					dt.Rows.get(count).setValue("TxtStr", bp.tools.DataTableConvertJson.GetFilteredStrForJSON(tempString));
				}
				count += 1;
			}

			ds.Tables.add(dt);
			dt = new DataTable("DataCount");
			dt.Columns.Add("DataCount", Integer.class);
			DataRow dr = dt.NewRow();
			dr.setValue("DataCount", folderArray.length);
			dt.Rows.add(dr);
			ds.Tables.add(dt);
			return Json.ToJson(ds);
		}
		catch (RuntimeException e)
		{
			return "";
		}
	}
	///#region 前台SQL转移处理
	public final String RunSQL_Init() throws Exception {
		String sql = GetRequestVal("SQL");
		sql= AesEncodeUtil.decryptAES(sql, "lCCqkL1BfSwt2M@5");
		String dbSrc = this.GetRequestVal("DBSrc");
		DataTable dt = null;
		if (DataType.IsNullOrEmpty(dbSrc) == false && dbSrc.equals("local") == false)
		{
			SFDBSrc sfdb = new SFDBSrc(dbSrc);
			dt = sfdb.RunSQLReturnTable(sql);
		}
		else
		{
			dt = DBAccess.RunSQLReturnTable(sql);
		}
		return bp.tools.Json.ToJson(dt);
	}
	///#endregion

	// 您的应用ID
	private static String APP_KEY = "447d8b671ee948b8";
	// 您的应用密钥
	private static String APP_SECRET = "rF1HBr3QjtPD1gXVFfIAGKtDRF6Q2HuB";

	public final String ToLang() throws NoSuchAlgorithmException {
		// 添加请求参数
		java.util.Map<String, String[]> params = createRequestParams();
		// 添加鉴权相关参数
		AuthV3Util.addAuthParams(APP_KEY, APP_SECRET, params);
		// 请求api服务
		byte[] result = OkHttpUtil.doPost("https://openapi.youdao.com/api", null, params, "application/json");
		// 打印返回结果
		if (result != null) {
			return new String(result, StandardCharsets.UTF_8);
		}
		return "";
	}
	private java.util.Map<String, String[]> createRequestParams() {
		/*
		 * note: 将下列变量替换为需要请求的参数
		 * 取值参考文档: https://ai.youdao.com/DOCSIRMA/html/%E8%87%AA%E7%84%B6%E8%AF%AD%E8%A8%80%E7%BF%BB%E8%AF%91/API%E6%96%87%E6%A1%A3/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1-API%E6%96%87%E6%A1%A3.html
		 */
		String q = this.GetRequestVal("Txt");//待翻译文本
		String from = "zh-CHS";//源语言语种
		String to = this.GetRequestVal("ToLang");//目标语言语种
		String vocabId = "";//非必填项，用户指定的词典 out_id，目前支持英译中

		return new HashMap<String, String[]>() {{
			put("q", new String[]{q});
			put("from", new String[]{from});
			put("to", new String[]{to});
			put("vocabId", new String[]{vocabId});
		}};
	}
}
