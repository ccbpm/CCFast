package bp.sys;

import bp.da.*;
import bp.difference.handler.CommonUtils;
import bp.en.*;
import bp.tools.StringHelper;
import bp.web.*;
import bp.difference.*;
import bp.tools.*;
import net.sf.json.JSONObject;

import java.util.*;

/**
 事件
*/
public class FrmEvents extends EntitiesMyPK
{
	/**
	 执行事件

	 @param dotype 执行类型
	 @param en 数据实体
	 @return null 没有事件，其他为执行了事件。
	*/
	public final String DoEventNode(String dotype, Entity en) throws Exception {
		// return null; // 2019-08-27 取消节点事件 zl
		return DoEventNode(dotype, en, null);
	}
	/**
	 执行事件

	 @param dotype 执行类型
	 @param en 数据实体
	 @param atPara 参数
	 @return null 没有事件，其他为执行了事件。

	 不再使用节点事件 2019-08-27 zl
	 原调用点有两处：（1）FrmEvent.cs 中的DoEventNode()； （2）Flow.cs中的DoFlowEventEntity()方法中，3973行 fes.DoEventNode(doType, en, atPara)。
	 现在都已经取消调用

	*/
	public final String DoEventNode(String dotype, Entity en, String atPara) throws Exception {
		if (this.size()== 0)
		{
			return null;
		}
		String val = _DoEventNode(dotype, en, atPara);
		if (val != null)
		{
			val = val.trim();
		}

		if (DataType.IsNullOrEmpty(val))
		{
			return ""; // 说明有事件，执行成功了。
		}
		else
		{
			return val; // 没有事件.
		}
	}

	/**
	 执行事件，事件标记是 EventList.

	 @param dotype 执行类型
	 @param en 数据实体
	 @param atPara 特殊的参数格式@key=value 方式.
	 @return
	*/
	private String _DoEventNode(String dotype, Entity en, String atPara) throws Exception {
		if (this.size()== 0)
		{
			return null;
		}

		bp.en.Entity tempVar = this.GetEntityByKey(FrmEventAttr.FK_Event, dotype);
		FrmEvent nev = tempVar instanceof FrmEvent ? (FrmEvent)tempVar : null;

		if (nev == null || nev.getHisDoType() == EventDoType.Disable)
		{
			return null;
		}


			///#region 执行的是业务单元.
		if (nev.getHisDoType() == EventDoType.BuessUnit)
		{
			/* 获得业务单元，开始执行他 */
			BuessUnitBase enBuesss = bp.sys.base.Glo.GetBuessUnitEntityByEnName(nev.getDoDoc());
			if (enBuesss==null)
				return null;
			enBuesss.WorkID = Long.parseLong(en.getPKVal().toString());
			return enBuesss.DoIt();
		}

			///#endregion 执行的是业务单元.

		String doc = nev.getDoDoc().trim();
		if ((doc == null || Objects.equals(doc, "")) && nev.getHisDoType() != EventDoType.SpecClass) //edited by liuxc,2016-01-16,执行DLL文件不需要判断doc为空
		{
			return null;
		}


			///#region 处理执行内容
		Attrs attrs = en.getEnMap().getAttrs();
		String MsgOK = "";
		String MsgErr = "";

		if (nev.getNodeID() != 0)
		{
			doc = doc.replace("@FK_Node", "" + nev.getNodeID());
			doc = doc.replace("@NodeID", "" + nev.getNodeID());
		}
		if (DataType.IsNullOrEmpty(nev.getFlowNo()) == false)
		{
			doc = doc.replace("@FlowNo", "" + nev.getFlowNo());
			doc = doc.replace("@FK_Flow", "" + nev.getFlowNo());
		}
		doc = doc.replace("~", "'");
		doc = doc.replace("@WebUser.No", WebUser.getNo());
		doc = doc.replace("@WebUser.Name", WebUser.getName());
		doc = doc.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
		doc = doc.replace("@WebUser.DeptNo", WebUser.getDeptNo());
		doc = doc.replace("@FK_Node", nev.getFrmID().replace("ND", ""));
		doc = doc.replace("@FrmID", nev.getFrmID());
		doc = doc.replace("@FK_MapData", nev.getFrmID());
		doc = doc.replace("@RDT", DataType.getCurrentDateTime());

		doc = doc.replace("@WorkID", en.GetValStrByKey("OID", "@WorkID"));
		doc = doc.replace("@WebUser.OrgNo", WebUser.getOrgNo());

		if (doc.contains("@") == true)
		{
			for (Attr attr : attrs)
			{
				if (doc.contains("@" + attr.getKey()) == false)
				{
					continue;
				}
				doc = doc.replace("@" + attr.getKey(), en.GetValStrByKey(attr.getKey()));
			}
		}
		//替换。
		if (DataType.IsNullOrEmpty(atPara) == false && doc.contains("@") == true)
		{
			AtPara ap = new AtPara(atPara);
			for (String key : ap.getHisHT().keySet())
			{
				doc = doc.replace("@" + key, ap.GetValStrByKey(key));
			}
		}

		//SDK表单上服务器地址,应用到使用ccflow的时候使用的是sdk表单,该表单会存储在其他的服务器上.
		Object servHost= SystemConfig.getAppSettings().get("SDKFromServHost");

		doc = doc.replace("@SDKFromServHost", servHost==null?"":servHost.toString());
		if (doc.contains("@") == true)
		{
			if (ContextHolderUtils.getRequest() != null)
			{
				/*如果是 bs 系统, 有可能参数来自于url ,就用url的参数替换它们 .*/
				//String url = BP.Sys.Base.Glo.Request.RawUrl;
				//2019-07-25 zyt改造
				String url = bp.sys.base.Glo.getRequest().getRemoteAddr();
				if (url.indexOf('?') != -1)
				{
					url = StringHelper.trimStart(url.substring(url.indexOf('?')), '?');
				}

				String[] paras = url.split("[&]", -1);
				for (String s : paras)
				{
					String[] mys = s.split("[=]", -1);

					if (doc.contains("@" + mys[0]) == false)
					{
						continue;
					}

					doc = doc.replace("@" + mys[0], mys[1]);
				}
			}
		}

		if (nev.getHisDoType() == EventDoType.URLOfSelf)
		{
			if (doc.contains("?") == false)
			{
				doc += "?1=2";
			}

			doc += "&UserNo=" + WebUser.getNo();
			doc += "&Token=" + WebUser.getToken();
			doc += "&FK_Dept=" + WebUser.getDeptNo();
			// doc += "&FK_Unit=" + WebUser.FK_Unit;
			doc += "&OID=" + en.getPKVal();

			if (SystemConfig.isBSsystem())
			{
				/*是bs系统，并且是url参数执行类型.*/
				//2019-07-25 zyt改造
				String url = bp.sys.base.Glo.getRequest().getRemoteAddr();
				if (url.indexOf('?') != -1)
				{
					url = StringHelper.trimStart(url.substring(url.indexOf('?')), '?');
				}

				String[] paras = url.split("[&]", -1);
				for (String s : paras)
				{
					String[] mys = s.split("[=]", -1);

					if (doc.contains(mys[0] + "="))
					{
						continue;
					}

					doc += "&" + s;
				}

				doc = doc.replace("&?", "&");
			}

			if (SystemConfig.isBSsystem() == false)
			{
				/*非bs模式下调用,比如在cs模式下调用它,它就取不到参数. */
			}

			if (doc.startsWith("http") == false)
			{
				/*如果没有绝对路径 */
				if (SystemConfig.isBSsystem())
				{
					/*在cs模式下自动获取*/
					//String host = BP.Sys.Base.Glo.Request.Url.Host;
					//2019-07-25 zyt改造
					String host = SystemConfig.getHostURL();
					if (doc.contains("@AppPath"))
					{
						doc = doc.replace("@AppPath", CommonUtils.getRequest().getRequestURL());
					}
					else
					{
						doc = "http://" + CommonUtils.getRequest().getRequestURL() + doc;
					}
				}

				if (SystemConfig.isBSsystem() == false)
				{
					/*在cs模式下它的baseurl 从web.config中获取.*/
					String cfgBaseUrl = SystemConfig.getHostURL();
					if (DataType.IsNullOrEmpty(cfgBaseUrl))
					{
						String err = "调用url失败:没有在web.config中配置BaseUrl,导致url事件不能被执行.";
						Log.DebugWriteError(err);
						throw new RuntimeException(err);
					}
					doc = cfgBaseUrl + doc;
				}
			}

			//增加上系统约定的参数.
			doc += "&EntityName=" + en.toString() + "&EntityPK=" + en.getPK() + "&EntityPKVal=" + en.getPKVal() + "&FK_Event=" + nev.getMyPK();
		}

			///#endregion 处理执行内容

		if (atPara != null && doc.contains("@") == true)
		{
			AtPara ap = new AtPara(atPara);
			for (String s : ap.getHisHT().keySet())
			{
				doc = doc.replace("@" + s, ap.GetValStrByKey(s));
			}
		}

		if (Objects.equals(dotype, EventListFrm.FrmLoadBefore))
		{
			String frmType = en.GetParaString("FrmType");
			if (DataType.IsNullOrEmpty(frmType) == true || frmType.equals("DBList") == false)
			{
				en.Retrieve(); //如果不执行，就会造成实体的数据与查询的数据不一致.
			}
		}

		switch (nev.getHisDoType())
		{
			case SP:
			case SQL:
				//SQLServer数据库中执行带参的存储过程
				try
				{
					if (nev.getHisDoType() == EventDoType.SP && doc.contains("=") == true && SystemConfig.getAppCenterDBType() == DBType.MSSQL)
					{
						RunSQL(doc);
						return nev.MsgOK(en);
					}
					if (DataType.IsNullOrEmpty(nev.getDBSrcNo()) == false && nev.getDBSrcNo().equals("local") == false)
					{
						SFDBSrc sfdb = new SFDBSrc(nev.getDBSrcNo());
						sfdb.RunSQLs(doc);

					}
					else
					{
						// 允许执行带有GO的sql.
						DBAccess.RunSQLs(doc);
					}
					return nev.MsgOK(en);
				}
				catch (RuntimeException ex)
				{
					throw new RuntimeException(nev.MsgError(en) + ",异常信息:" + ex.getMessage());
				}
			case URLOfSelf:
				Object tempVar2 = doc;
				String myURL = tempVar2 instanceof String ? (String)tempVar2 : null;
				if (myURL.contains("http") == false)
				{
					if (SystemConfig.isBSsystem())
					{
						if (myURL.contains("@AppPath"))
						{
							myURL = myURL.replace("@AppPath", CommonUtils.getRequest().getRequestURL());
						}
						else
						{
							myURL = CommonUtils.getRequest().getRequestURL() + myURL;
						}
					}
					else
					{
						String cfgBaseUrl = SystemConfig.getHostURL();
						if (DataType.IsNullOrEmpty(cfgBaseUrl))
						{
							String err = "调用url失败:没有在web.config中配置BaseUrl,导致url事件不能被执行.";
							Log.DebugWriteError(err);
							throw new RuntimeException(err);
						}
						myURL = cfgBaseUrl + myURL;
					}
				}
				myURL = myURL.replace("@SDKFromServHost", SystemConfig.GetValByKey("SDKFromServHost",""));

				if (myURL.contains("&FID=") == false && en.getRow().containsKey("FID") == true)
				{
					String str = en.getRow().get("FID").toString();
					myURL = myURL + "&FID=" + str;
				}

				if (myURL.contains("&FK_Flow=") == false && en.getRow().containsKey("FK_Flow") == true)
				{
					String str = en.getRow().get("FK_Flow").toString();
					myURL = myURL + "&FK_Flow=" + str;
				}

				if (myURL.contains("&WorkID=") == false)
				{
					String str = "";
					if (en.getRow().containsKey("WorkID") == true)
					{
						str = en.getRow().get("WorkID").toString();
						myURL = myURL + "&WorkID=" + str;
					}
					else if (en.getRow().containsKey("OID") == true)
					{
						str = en.getRow().get("OID").toString();
						myURL = myURL + "&WorkID=" + str;
					}
				}

				try
				{
					String text = DataType.ReadURLContext(myURL, 600000);
					if (text == null)
					{
						throw new RuntimeException("@流程设计错误，执行的url错误:" + myURL + ", 返回为null, 请检查url设置是否正确。提示：您可以copy出来此url放到浏览器里看看是否被正确执行。");
					}

					if (text != null && text.length() > 7 && text.substring(0, 7).toLowerCase().contains("err"))
					{
						throw new RuntimeException(text);
					}

					if (text == null || Objects.equals(text.trim(), ""))
					{
						return null;
					}
					return text;
				}
				catch (RuntimeException ex)
				{
					throw new RuntimeException("@" + nev.MsgError(en) + " Error:" + ex.getMessage());
				}
			case EventBase: //执行事件类.
				// 获取事件类.
				Object tempVar3 = doc;
				String evName = tempVar3 instanceof String ? (String)tempVar3 : null;
				bp.sys.base.EventBase ev = null;
				try
				{
					ev = ClassFactory.GetEventBase(evName);
				}
				catch (RuntimeException ex)
				{
					throw new RuntimeException("@事件名称:" + evName + "拼写错误，或者系统不存在。说明：事件所在的类库必须是以BP.开头，并且类库的BP.xxx.dll。");
				}

				if (ev == null)
				{
					throw new RuntimeException("@事件名称:" + evName + "拼写错误，或者系统不存在。说明：事件所在的类库必须是以BP.开头，并且类库的BP.xxx.dll。");
				}

				//开始执行.
				try
				{

						///#region 处理整理参数.
					Row r = en.getRow();
					try
					{
						//系统参数.
						r.put("FK_MapData", en.getClassID());
					}
					catch (java.lang.Exception e)
					{
						r.put("FK_MapData", en.getClassID());
					}

					try
					{
						r.put("EventSource", nev.getEventNo());
					}
					catch (java.lang.Exception e2)
					{
						r.put("EventSource", nev.getEventNo());
					}

					if (atPara != null)
					{
						AtPara ap = new AtPara(atPara);
						for (String s : ap.getHisHT().keySet())
						{
							try
							{
								r.put(s, ap.GetValStrByKey(s));
							}
							catch (java.lang.Exception e3)
							{
								r.put(s, ap.GetValStrByKey(s));
							}
						}
					}

					if (SystemConfig.isBSsystem() == true)
					{
						/*如果是bs系统, 就加入外部url的变量.*/
						//2019 - 07 - 25 zyt改造
						for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet())
						{
							String val = ContextHolderUtils.getRequest().getParameter(key);
							try
							{
								r.put(key, val);
							}
							catch (java.lang.Exception e4)
							{
								r.put(key, val);
							}
						}
					}

						///#endregion 处理整理参数.

					ev.setSysPara(r);
					ev.HisEn = en;
					ev.Do();
					String str = ev.SucessInfo;
					if (str.contains("err@") == true)
					{
						throw new RuntimeException(str);
					}
					return str;
				}
				catch (RuntimeException ex)
				{
					//增加判断，可自定义事件返回信息
					if(DataType.IsNullOrEmpty(ex.getMessage())) {
						throw new RuntimeException("@执行事件(" + ev.getTitle() + ")期间出现错误:" + ex.getMessage());
					}
					else{
						throw new RuntimeException("@"+ex.getMessage());
					}
				}
			case WebApi:
				try
				{
					String urlExt = doc; //url.
					urlExt = PubGlo.DealExp(urlExt, en);

					//增加其他的参数.
					if (atPara != null && doc.contains("@") == true)
					{
						AtPara ap = new AtPara(atPara);
						for (String s : ap.getHisHT().keySet())
						{
							urlExt = urlExt.replace("@" + s, ap.GetValStrByKey(s));
						}
					}
					if (urlExt.contains("@WebApiHost"))//可以替换配置文件中配置的webapi地址
						urlExt = urlExt.replace("@WebApiHost", bp.difference.SystemConfig.GetValByKey("WebApiHost",""));
					String urlUodel = nev.GetValStringByKey("PostModel"); //模式. Post,Get
					int paraMode = nev.GetValIntByKey("ParaMoel"); //参数模式. 0=自定义模式， 1=全量模式.
					String pdocs = nev.GetValStringByKey("ParaDocs"); //参数内容.  对自定义模式有效.
					String paraDTModel = nev.GetValStringByKey("ParaDTModel"); //参数内容.  对自定义模式有效.
					boolean isJson = false;
					if (paraDTModel.equals("1"))
					{
						isJson = true;
					}
					//全量参数模式.
					if (paraMode == 1)
					{
						Hashtable hashtableJson=new Hashtable();
						hashtableJson.put("MainTable",en.ToJson(false));
						//从表数据
						ArrayList<Entities> entitiesArrayList=en.GetDtlsDatasOfList();
						//多个从表的情况下
						for(Entities  entities : entitiesArrayList){
							GEDtls geDtls=(GEDtls)entities;
							Entity entity=geDtls.getNewEntity();
							GEDtl geDtl=(GEDtl) entity;
							QueryObject queryObject=new QueryObject(geDtls);
							queryObject.AddWhere(MapDtlAttr.RefPK,en.getPKVal());
							queryObject.DoQuery();
							hashtableJson.put(geDtl.MapDtlNo,geDtls.ToJson());
							if(geDtls.size()>0){
								//查找从表字段附件信息
								//一个从表多个数据
								for(Entity geDtlFrm :geDtls) {
									GEDtl geDtlFrmDB=(GEDtl) geDtlFrm;
									FrmAttachmentDBs frmAttachmentDBs=new FrmAttachmentDBs();
									QueryObject queryObject1Frm=new QueryObject(frmAttachmentDBs);
									queryObject1Frm.AddWhere(FrmAttachmentDBAttr.RefPKVal,geDtlFrm.getPKVal());
									queryObject1Frm.addAnd();
									queryObject1Frm.AddWhere(FrmAttachmentDBAttr.FK_MapData,geDtl.MapDtlNo);
									queryObject1Frm.DoQuery();
									hashtableJson.put(geDtl.MapDtlNo+"_Ath_"+geDtlFrm.getPKVal(),frmAttachmentDBs.ToJson());
								}
							}
					   }
						pdocs=bp.tools.Json.ToJson(hashtableJson);
					}
					else
					{
						pdocs = pdocs.replace("~~", "\"");
						pdocs = pdocs.replace("~", "\"");
						pdocs = PubGlo.DealExp(pdocs, en);

						//
						if (atPara != null && pdocs.contains("@") == true)
						{
							AtPara ap = new AtPara(atPara);
							for (String s : ap.getHisHT().keySet())
							{
								pdocs = pdocs.replace("@" + s, ap.GetValStrByKey(s));
							}
						}

						if (pdocs.contains("@") == true)
						{
							throw new RuntimeException("@_DoEvent参数不完整:" + pdocs);
						}
					}


					if (DataType.IsNullOrEmpty(pdocs) == true)
						pdocs = en.ToJson();

					//保障数据的安全问题.
					if (urlExt.contains("Token=") == false)
						urlExt += "&Token=" + WebUser.getToken() + "&UserNo=" + WebUser.getNo();

					//判断提交模式.
					String result = "";
					if (urlUodel.toLowerCase().equals("get") == true)
						result = DataType.ReadURLContextByGET(urlExt, 9000); //返回字符串.
					else
						result = bp.tools.HttpClientUtil.doPostJson(urlExt, pdocs);
					if (DataType.IsNullOrEmpty(result) == true)
					{
						throw new RuntimeException("@执行WebAPI[" + urlExt + "]没有返回结果值");
					}
					if (result.equals("0") == true)
						throw new Exception("@执行["+dotype+"]WebAPI[" + urlExt + "]链接异常...");
					//数据序列化
					JSONObject jsonData = JSONObject.fromObject(result);
					//code=200，表示请求成功，否则失败
					String msg = jsonData.get("msg") != null ? jsonData.get("msg").toString() : "";
					if (!jsonData.get("code").toString().equals("200"))
					{
						throw new RuntimeException("@执行WebAPI[" + urlExt + "]失败:" + msg);
					}
					return msg;
				}
				catch (RuntimeException ex)
				{
					throw new RuntimeException("@" + nev.MsgError(en) + " Error:" + ex.getMessage());
				}
			case SFProcedure:
				///#endregion 自定义存储过程
				String doDoc = nev.getDoDoc();
				SFProcedure pro = new SFProcedure();
				pro.setNo(doDoc);
				if(pro.RetrieveFromDBSources()!=0){
					Hashtable ht = en.getRow();
					if (atPara != null){
						AtPara ap = new AtPara(atPara);
						for (String s : ap.getHisHT().keySet())
						{
							if(ht.containsKey(s) == false)
								ht.put(s,ap.GetValStrByKey(s));
						}
					}
					return pro.Exec(ht);
				}
				throw new RuntimeException("@自定义过程[" + doDoc+"]信息丢失,请联系管理员");
			default:
				throw new RuntimeException("@no such way." + nev.getHisDoType().toString());
		}
	}
	/**
	 事件
	*/
	public FrmEvents()
	{
	}
	/**
	 事件

	 @param fk_MapData FK_MapData
	*/
	public FrmEvents(String fk_MapData) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(FrmEventAttr.FrmID, fk_MapData);
		qo.DoQuery();
	}
	/**
	 事件

	 @param nodeID 按节点ID查询
	*/
	public FrmEvents(int nodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(FrmEventAttr.FK_Node, nodeID);
		qo.DoQuery();
	}
	public FrmEvents(int nodeID, String fk_flow) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(FrmEventAttr.FK_Node, nodeID);
		qo.addOr();
		qo.addLeftBracket();
		qo.AddWhere(FrmEventAttr.FK_Flow, fk_flow);
		qo.addAnd();
		qo.AddWhere(FrmEventAttr.FK_Node, 0);
		qo.addRightBracket();
		qo.DoQuery();
	}

	/**
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmEvent();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/**
	 转化成 java list,C#不能调用.

	 @return List
	*/
	public final java.util.List<FrmEvent> ToJavaList()
	{
		return (java.util.List<FrmEvent>)(Object)this;
	}
	/**
	 转化成list

	 @return List
	*/
	public final ArrayList<FrmEvent> Tolist()
	{
		ArrayList<FrmEvent> list = new ArrayList<FrmEvent>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmEvent)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
