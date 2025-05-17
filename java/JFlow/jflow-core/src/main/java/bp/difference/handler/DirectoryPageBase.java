package bp.difference.handler;

import bp.da.DataType;
import bp.da.Log;
import bp.difference.ContextHolderUtils;
import bp.sys.Glo;
import bp.web.WebUser;
import org.apache.http.protocol.HttpContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DirectoryPageBase {
	/**
	 * 获得Form数据.
	 *
	 * param key
	 *            key
	 * @return 返回值
	 */

	public final String GetValFromFrmByKey(String key, String isNullAsVal) {
		String val = getRequest().getParameter(key);
		if (val == null && key.contains("DDL_") == false) {
			val = this.getRequest().getParameter("DDL_" + key);
		}

		if (val == null && key.contains("TB_") == false) {
			val = getRequest().getParameter("TB" + key);
		}

		if (val == null && key.contains("CB_") == false) {
			val = getRequest().getParameter("CB_" + key);
		}

		if (val == null) {
			if (isNullAsVal != null)
				return isNullAsVal;
            return "";
			//throw new RuntimeException("@获取Form参数错误,参数集合不包含[" + key + "]");
		}

		val = val.replace("'", "~");
		return val;
	}

	/**
	 * 获得Form数据.
	 *
	 * param key
	 *            key
	 * @return 返回值
	 */
	public final String GetValFromFrmByKey(String key) {
		String val = getRequest().getParameter(key);
		if (val == null && key.toString().contains("DDL_") == false) {
			val = this.getRequest().getParameter("DDL_" + key);
		}

		if (val == null && key.toString().contains("TB_") == false) {
			val = getRequest().getParameter("TB_" + key);
		}

		if (val == null && key.toString().contains("CB_") == false) {
			val = getRequest().getParameter("CB_" + key);
		}

		if (val == null) {
			return "";
			//throw new RuntimeException("@获取Form参数错误,参数集合不包含[" + key + "]");
		}

		val = val.replace("'", "~");
		return val;
	}

	public float GetValFloatFromFrmByKey(String key) {
		String str = this.GetValFromFrmByKey(key);
		if (str == null || str == "")
			throw new RuntimeException("@参数:" + key + "没有取到值.");
		return Float.parseFloat(str);
	}

	public BigDecimal GetValDecimalFromFrmByKey(String key) {
		String str = this.GetValFromFrmByKey(key);
		if (str == null || str == "")
			throw new RuntimeException("@参数:" + key + "没有取到值.");
		return new BigDecimal(str);
	}

	public int getIndex() {
		String str = getRequest().getParameter("Index");
		if (str == null || str == "")
			return 1;
		return Integer.parseInt(str);
	}
	public String getDomainExt()throws Exception
	{
		String str = this.GetRequestVal("DomainExt");
		if (DataType.IsNullOrEmpty(str) == true)
			return null;
		return str;

	}
	/// <summary>
	/// 组织编号
	/// </summary>
	public String getOrgNo()throws Exception
	{

		String str = this.GetRequestVal("OrgNo");
		if (DataType.IsNullOrEmpty(str) == true)
			return WebUser.getOrgNo();
		return str;

	}
	public String getDoWhat()throws Exception
	{

		String str = this.GetRequestVal("DoWhat");
		if (str == null || str.equals("")|| str.equals("null"))
			return null;
		return str;

	}
	public String getUserNo(){
		String str = this.GetRequestVal("UserNo");
		if (str == null || str.equals("") || str.equals("null"))
			return null;
		return str;

	}

	// 简易缓存机制
	private static final ConcurrentHashMap<String, Method> MethodCache = new ConcurrentHashMap<>();

	/**
	 * 执行方法
	 *
	 * param myEn
	 *            对象名
	 * param methodName
	 *            方法
	 * @return 返回执行的结果，执行错误抛出异常
	 * @throws Exception
	 */
	public final String DoMethod(DirectoryPageBase myEn, String methodName) throws Exception {
		// 执行该方法.
		Object[] paras = null;
		Object tempVar = null;
		try {
			java.lang.reflect.Method mp = MethodCache.get(myEn.getClass() + "." +methodName);
			if(mp == null){
				Class<? extends bp.difference.handler.DirectoryPageBase> tp = myEn.getClass();
				mp = tp.getMethod(methodName);
				MethodCache.put(myEn.getClass() + "." +methodName, mp);
			}
			tempVar = mp.invoke(myEn, paras);
		} catch (InvocationTargetException e ) {
			e.printStackTrace();
			String msg = null;
			if(e.getTargetException()!=null && e.getTargetException().getCause()!=null){
				msg = e.getTargetException().toString();
				msg += e.getTargetException().getCause().getMessage();
			}
			if (msg==null && e.getCause() != null) {
				msg = e.getCause().getMessage();
			}
			if (msg == null)
				msg = e.getMessage();
			// 如果有url返回.
			if (msg != null && (msg.indexOf("url@") == 0 || msg.indexOf("info@") == 0 ||(msg.indexOf("err@")==0)))
				return msg;

			String myParas = getRequest().getQueryString();
			String errInfo = "err@页面类[" + myEn.toString() + ",方法[" + methodName + "]执行错误.";
			errInfo += "\t\n@参数:" + myParas;
			errInfo += "\t\n@Msg:" + msg;
			errInfo += "\t\n@后台堆栈信息:" + getLastCausedBy(e);
			Log.DebugWriteError("bp.wf.HttpHangerBase.DoMethod()" + errInfo);
			return errInfo;
		}
		return (String) ((tempVar instanceof String) ? tempVar : null); // 调用由此
																		// MethodInfo
																		// 实例反射的方法或构造函数。
	}

	private static final Pattern CAUSED_BY_PATTERN = Pattern.compile("Caused by: (.*?)\\s+at (.+?):(\\d+)");

	/**
	 * 获取堆栈信息，最后一个Caused By
	 * @param e
	 * @return 最终错误信息及行号
	 */
	public static String getLastCausedBy(Throwable e){
		// 构建一个包含异常消息和堆栈跟踪的字符串
		StringBuilder sb = new StringBuilder();
		sb.append("堆栈信息: ").append(e.getMessage()).append(System.lineSeparator());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(baos));
		String stackTrace = baos.toString();
		sb.append(stackTrace);

		Matcher matcher = CAUSED_BY_PATTERN.matcher(sb);
		String lastCausedBy = null;
		String lastFileName = null;
		String lastLineNumber = null;

		while (matcher.find()) {
			lastCausedBy = matcher.group(1).trim();
			lastFileName = matcher.group(2).trim();
			lastLineNumber = matcher.group(3);
		}

		// 如果没有找到匹配的Caused by，则返回null或适当的消息
		if (lastCausedBy == null) {
			return "No Caused by found";
		}

		// 返回最后一个Caused by和其行号（如果找到）
		return String.format("Last Caused by: %s in %s:%s)", lastCausedBy, lastFileName, lastLineNumber);
	}

	/**
	 * 执行默认的方法名称
	 *
	 * @return 返回执行的结果
	 * @throws Exception
	 */
	protected String DoDefaultMethod() throws Exception {

		if (this.getDoType().contains(">")==true)
			return "err@非法的脚本植入";

		return "@子类没有重写该[" + this.getDoType() + "]方法.";
	}

	public Hashtable ht = null;
	public final void AddPara(String key, String val)
	{
		if (ht == null)
		{
			ht = new Hashtable();
		}
		ht.put(key, val);
	}

	/**
	 * 公共方法获取值
	 * param param参数名
	 * @return
	 */
	public final String GetRequestVal(String param) {
		try{
			if (ht != null && ht.containsKey(param))
			{
				String myval = ht.get(param) instanceof String ? (String)ht.get(param) : null;
				if(myval == null)
					return null;
				try{
					return URLDecoder.decode(myval, "UTF-8");
				}catch(Exception e){
					return myval;
				}

			}
			String val = getRequest().getParameter(param);
			if (DataType.IsNullOrEmpty(val)) {
				val = getRequest().getParameter(param);
				if(val == null)
					return null;
			}
			try{
				if(val.contains("＇")){
					val=val.replaceAll("＇","'");
				}
				return URLDecoder.decode(val, "UTF-8");
			}catch(Exception e){
				return val;
			}
		}catch(Exception e){
			throw new RuntimeException("获取参数["+param+"]解码异常:"+e.getMessage());
		}
	}

	public Boolean GetRequestValBoolen(String key) {
		if (this.GetRequestValInt(key) == 0)
			return false;

		return true;
	}

	/**
	 * 公共方法获取值
	 *
	 * param param
	 *            参数名
	 * @return
	 */
	public final int GetRequestValInt(String param) {
		String str = GetRequestVal(param);
		if (str == null || str.equals("") || str.equals("null")) {
			return 0;
		}
		try {
			return Integer.parseInt(str);
		} catch (java.lang.Exception e) {
			return 0;
		}
	}

	/**
	 * 公共方法获取值
	 *
	 * param param
	 * @return
	 */
	public final long GetRequestValInt64(String param) {
		String str = GetRequestVal(param);
		if (str == null || str.equals("") || str.equals("null")) {
			return 0;
		}
		try {
			return Long.parseLong(str);
		} catch (java.lang.Exception e) {
			return 0;
		}
	}

	public final int GetRequestValChecked(String param)
	{
		String str = GetRequestVal(param);
		if (str == null || str.equals("") || str.equals("null") || str.equals("undefined"))
			return 0;
		return 1;
	}

	/**
	 * 数据
	 *
	 * param param
	 * @return
	 */
	public final float GetRequestValFloat(String param) {
		String str = GetRequestVal(param);
		if (str == null || str.equals("") || str.equals("null")) {
			return 0;
		}
		try {
			return Float.parseFloat(str);
		} catch (java.lang.Exception e) {
			return 0;
		}
	}

	/**
	 * 获得参数.
	 */
	public final String getRequestParas() {
		String urlExt = "";
		String rawUrl = this.getRequest().getQueryString();
		rawUrl = "&" + rawUrl.substring(rawUrl.indexOf('?') + 1);
		String[] paras = rawUrl.split("[&]", -1);
		for (String para : paras) {
			if (para == null || para.equals("") || para.contains("=") == false) {
				continue;
			}

			if (para.equals("1=1")) {
				continue;
			}

			urlExt += "&" + para;
		}
		return urlExt;
	}
	public final String getRequestParasOfAll() {
		String urlExt = "";
		String rawUrl = this.getRequest().getQueryString();
		rawUrl = "&" + rawUrl.substring(rawUrl.indexOf('?') + 1);
		String[] paras = rawUrl.split("[&]", -1);
		for (String para : paras) {

			if (para == null || para.equals("") || para.contains("=") == false) {
				continue;
			}

			if (para.equals("1=1")) {
				continue;
			}

			urlExt += "&" + para;
		}

		 Enumeration enu = getRequest().getParameterNames();
		while (enu.hasMoreElements())
		{

			String key = (String) enu.nextElement();
			 if (urlExt.contains(key+"=") == true)
              continue;

			 urlExt += "&" + key + "=" + getRequest().getParameter(key);
		}


		return urlExt;
	}

	/**
	 * 编号
	 */
	public final String getNo() {
		// String str = context.Request.QueryString["No");
		String str = getRequest().getParameter("No");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	public String getFK_Dept() {
		String str = getRequest().getParameter("FK_Dept");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}


	public String getName() throws Exception {
		String str = getRequest().getParameter("Name");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	/**
	 * 执行类型
	 */
	public final String getDoType() {
		// 获得执行的方法.
		String doType = "";

		doType = this.GetRequestVal("DoType");
		if (doType == null) {
			doType = this.GetRequestVal("Action");
		}

		if (doType == null) {
			doType = this.GetRequestVal("action");
		}

		if (doType == null) {
			doType = this.GetRequestVal("Method");
		}

		return doType;
	}

	public final String getEnsName()throws Exception {
		String str = this.GetRequestVal("EnsName");

		if (str == null || str.equals("") || str.equals("null")) {
			str = this.GetRequestVal("FK_MapData");
		}

		if (str == null || str.equals("") || str.equals("null")) {
			str = this.GetRequestVal("FrmID");
		}

		if (str == null || str.equals("") || str.equals("null")) {
			if (this.getEnName() == null)
                return null;
            return this.getEnName() + "s";
		}

		return str;
	}

	public final String getTreeEnsName()throws Exception {
		String str = this.GetRequestVal("TreeEnsName");

		if (str == null || str.equals("") || str.equals("null")) {
			if (this.getEnName() == null)
                return null;
            return this.getEnName() + "s";
		}

		return str;
	}
	/**
	 部门编号
	 */
	public final String getDeptNo()
	{
		String str = this.GetRequestVal("FK_Dept");
		if (str == null || str.equals("") || str.equals("null"))
		{
			return null;
		}
		return str;
	}
	public final String getMyPK() {
		String str = this.GetRequestVal("MyPK");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	public final String getEventNo() {
		String str = this.GetRequestVal("FK_Event");
		if (DataType.IsNullOrEmpty(str))
			str = this.GetRequestVal("EventNo");
		if (str == null || str.equals("") || str.equals("null"))
			return null;
		return str;
	}

	/**
	 * 字典表
	 */
	public String getFK_SFTable() {
		String str = this.GetRequestVal("FK_SFTable");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	public String getEnumKey() {
		String str = this.GetRequestVal("EnumKey");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	public final String getKeyOfEn() {
		String str = this.GetRequestVal("KeyOfEn");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;

	}

	/**
	 * FK_MapData
	 */
	public String getFK_MapData() {
		String str = this.GetRequestVal("FK_MapData");
		if (str == null || str.equals("") || str.equals("null")) {
			str = this.GetRequestVal("FrmID");
		}
		return str;

	}

	/**
	 * 扩展信息
	 */
	public final String getFK_MapExt() {
		String str = this.GetRequestVal("FK_MapExt");
		if (DataType.IsNullOrEmpty(str)) {
			str = this.GetRequestVal("MyPK");
			if (DataType.IsNullOrEmpty(str)) {
				return null;
			}
		}
		return str;
	}

	/**
	 * 流程编号
	 */
	public final String getFK_Flow() {
		String str = this.GetRequestVal("FK_Flow");
		if ( DataType.IsNullOrEmpty(str)) {
			return null;
		}
		return str;
	}
	public String getDevType() {
		String str = this.GetRequestVal("DevType");
		if ( DataType.IsNullOrEmpty(str)) {
			return null;
		}
		return str;
	}
	public final String getPFlowNo() {
		String str = this.GetRequestVal("PFlowNo");
		if ( DataType.IsNullOrEmpty(str)) {
			return null;
		}
		return str;
	}


	public final int getPNodeID() {
		String str = this.GetRequestVal("PNodeID");
		if (DataType.IsNullOrEmpty(str)) {
			return 0;
		}

		return Integer.parseInt(str);
	}

	public final long getPFID() {
		String str = getRequest().getParameter("PFID");
		if (str == null || str.equals("") || str.equals("null")) {
			return 0;
		}
		return Integer.parseInt(str);
	}

	/**
	 流程编号
	 */
	public final String getFlowNo()
	{
		String str = this.GetRequestVal("FK_Flow");
		if (DataType.IsNullOrEmpty(str))
			str = this.GetRequestVal("FlowNo");
		if (str == null || str.equals("") || str.equals("null"))
			return null;
		if(DataType.IsNullOrEmpty(str) == false)
			return bp.wf.Dev2Interface.Flow_TurnFlowMark2FlowNo(str);
		return str;
	}
	public String getMsg()
	{
		String str = this.GetRequestVal("Msg");
		if (DataType.IsNullOrEmpty(str) == true)
		{
			return null;
		}
		return str;
	}

	/**
	 人员编号
	 */
	public final String getEmpNo()
	{
		String str = this.GetRequestVal("FK_Emp");
		if (DataType.IsNullOrEmpty(str) == true)
		{
			return null;
		}
		return str;
	}

	public final int getGroupField() {
		String str = this.GetRequestVal("GroupField");
		if (DataType.IsNullOrEmpty(str)) {
			return 0;
		}

		return Integer.parseInt(str);
	}

	/**
	 * 节点ID
	 */
	public int getFK_Node() {
		int nodeID = this.GetRequestValInt("FK_Node");
		if (nodeID == 0)
			nodeID = this.GetRequestValInt("NodeID");
		return nodeID;
	}
	private int _nodeID = 0;
	/**
	 节点ID
	 */
	public final int getNodeID()
	{
		if (_nodeID != 0)
		{
			return _nodeID;
		}

		int nodeID = this.GetRequestValInt("FK_Node");
		if (nodeID == 0)
		{
			nodeID = this.GetRequestValInt("NodeID");
		}
		return nodeID;
	}
	public final void setNodeID(int value)
	{
		_nodeID = value;
	}
	public final int getToNodeID()
	{
		return this.GetRequestValInt("ToNodeID");
	}
	public final long getFID() {
		String str = getRequest().getParameter("FID");
		if (str == null || str.equals("") || str.equals("null")) {
			return 0;
		}
		return Integer.parseInt(str);
	}
	public String getWorkIDStr()
	{
		String val = this.GetRequestVal("WorkID");
		if (DataType.IsNullOrEmpty(val) == true)
			val = this.GetRequestVal("OID");
		if (DataType.IsNullOrEmpty(val) == true)
			val = this.GetRequestVal("PKVal");
		return val;

	}
	public long getWorkID() {
		if(_workID!=0)
			return _workID;
		String str = this.GetRequestVal("WorkID");
		if (str == null || str.equals("") || str.equals("null"))
			str = this.GetRequestVal("PKVal");

		if (str == null || str.equals("") || str.equals("null"))
			str = this.GetRequestVal("OID");

		// For: QDingShuKe
		if (str == null || str.equals("") || str.equals("null"))
			str = this.GetRequestVal("instanceId");

		if (str == null || str.equals("") || str.equals("null"))
			return 0;

		return Long.parseLong(str);
	}
	public void setWorkID(long value){
		_workID = value;
	}

	/**
	 * 框架ID
	 */
	public final String getFK_MapFrame() {
		// String str = context.Request.QueryString["FK_MapFrame");
		String str = getRequest().getParameter("FK_MapFrame");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	/**
	 * RefOID
	 */
	public final int getRefOID() {
		// String str = context.Request.QueryString["RefOID");
		String str = getRequest().getParameter("RefOID");

		if (str == null || str.equals("") || str.equals("null")) {
			// str = context.Request.QueryString["OID");
			str = getRequest().getParameter("OID");
		}

		if (str == null || str.equals("") || str.equals("null")) {
			return 0;
		}

		return Integer.parseInt(str);
	}

	/**
	 * 明细表
	 */

	public final String getFK_MapDtl() {
		String str = getRequest().getParameter("FK_MapDtl");
		if (str == null || str.equals("") || str.equals("null")) {
			str = getRequest().getParameter("EnsName");
		}
		return str;
	}

	/**
	 * 页面Index. /** 字段属性编号
	 */
	public final String getAth() {
		// String str = context.Request.QueryString["Ath");
		String str = getRequest().getParameter("Ath");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	public HttpContext context = null;

	/**
	 * 获得Int数据
	 *
	 * param key
	 * @return
	 */
	public final int GetValIntFromFrmByKey(String key) {
		String str = this.GetValFromFrmByKey(key);
		if (str == null || str.equals("") || str.equals("0")) {
			throw new RuntimeException("@参数:" + key + "没有取到值.");
		}
		return Integer.parseInt(str);
	}

	public final boolean GetValBoolenFromFrmByKey(String key) {
		String val = this.GetValFromFrmByKey(key, "0");
		if (val == "on" || val == "1")
			return true;
		if (val == null || val.equals("") || val.equals("0")) {
			return false;
		}
		return true;
	}

	public final String getRefPK() {
		// String str = this.context.Request.QueryString["RefPK");
		String str = getRequest().getParameter("RefPK");
		return str;
	}

	public final String getRefPKVal() {
		// String str = this.context.Request.QueryString["RefPKVal");
		String str = this.getRequest().getParameter("RefPKVal");
		if (str == null) {
			str = "0";
		}
		return str;
	}

	/*
	 * 表单ID
	 */
	public final String getFrmID() {
		String str = this.GetRequestVal("FrmID");
		if (str == null || str.equals("") || str.equals("null")) {
			return this.GetRequestVal("FK_MapData");
		}

		return str;
	}

	private long _workID=0;

	public long getPWorkID() {

		return this.GetRequestValInt("PWorkID");
	}

	public final long getCWorkID() {
		return this.GetRequestValInt("CWorkID");
	}

	/// #region 属性参数.
	/**
	 * @于庆海翻译.
	 *
	 */
	public String getPKVal() {
		String str = this.GetRequestVal("PKVal");

		if (DataType.IsNullOrEmpty(str) == true) {
			str = this.GetRequestVal("OID");
		}

		if (DataType.IsNullOrEmpty(str) == true) {
			str = this.GetRequestVal("No");
		}

		if (DataType.IsNullOrEmpty(str) == true) {
			str = this.GetRequestVal("MyPK");
		}
		if (DataType.IsNullOrEmpty(str) == true) {
			str = this.GetRequestVal("NodeID");
		}

		if (DataType.IsNullOrEmpty(str) == true) {
			str = this.GetRequestVal("WorkID");
		}

		if ("null".equals(str) == true)
			return null;

		return str;
	}

	/// 是否是移动？
	/// </summary>
	public boolean getItIsMobile() {
		String v = this.GetRequestVal("IsMobile");
		if (v != null && v == "1")
			return true;

		if (Glo.getRequest().getRequestURI().contains("/CCMobile/"))
			return true;

		return false;
	}
	public static HttpServletRequest getRequest() {
		return ContextHolderUtils.getRequest();
	}

	public HttpServletResponse getResponse() {
		return ContextHolderUtils.getResponse();
	}

	/**
	 * 获取请求的表单数据
	 * @return FormData
	 */
	public static Map<String, String> getFormData() {
		HttpServletRequest request = ContextHolderUtils.getRequest();
		Map<String, String> formData = new HashMap<>();
		Enumeration<String> parameterNames = request.getParameterNames();

		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			String paramValue = request.getParameter(paramName);
			formData.put(paramName, paramValue);
		}

		return formData;
	}

	public final int getIsWap() {
		if(ContextHolderUtils.getRequest().getParameter("IsWap")==null)
			return 0;
		if (ContextHolderUtils.getRequest().getParameter("IsWap").equals("1")) {
			return 1;
		}
		return 0;
	}

	public final String getSta() {
		String str = ContextHolderUtils.getRequest().getParameter("Sta");

		return str;
	}
	/**
	 明细表
	 */
	public final String getMapDtlNo()
	{
		String str = this.GetRequestVal("FK_MapDtl"); //context.Request.QueryString["FK_MapDtl"];
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = this.GetRequestVal("EnsName"); // context.Request.QueryString["EnsName"];
		}
		return str;
	}

	public int getPageIdx() {
		String str = ContextHolderUtils.getRequest().getParameter("PageIdx");
		if (str == null || str.equals("") || str.equals("null"))
			return 1;
		return Integer.parseInt(str);

	}

	public int getPageSize() {
		String str = ContextHolderUtils.getRequest().getParameter("PageSize");
		if (str == null || str.equals("") || str.equals("null"))
			return 10;
		return Integer.parseInt(str);

	}

	public final String getKey() {
		return ContextHolderUtils.getRequest().getParameter("Key");
	}

	public final String getActionType()throws Exception
	{
		String s = ContextHolderUtils.getRequest().getParameter("ActionType");
		if (s == null)
		{
			s = ContextHolderUtils.getRequest().getParameter("DoType");
		}

		return s;
	}

	public final long getOID()throws Exception
	{
		String str = this.GetRequestVal("RefOID"); // context.Request.QueryString["RefOID");
		if (DataType.IsNullOrEmpty(str) == true)
			str = this.GetRequestVal("OID");  //context.Request.QueryString["OID");

		if (DataType.IsNullOrEmpty(str) == true)
			str="0";

		return Long.parseLong(str);

	}

	public String getEnName()throws Exception
	{
		return GetRequestVal("EnName");
	}
	public String getRefNo()throws Exception
	{
		return GetRequestVal("RefNo");
	}

	public final String getFK_Emp()throws Exception
	{
		return GetRequestVal("FK_Emp");
	}
	public String getPageID()throws Exception
	{
		String pageID = this.GetRequestVal("PageID");
		 if (DataType.IsNullOrEmpty(pageID) == true)
		 {
			 pageID = "Home";
		 }

		return pageID;
	}

	public boolean getIsCC()throws Exception
	{
		String s = ContextHolderUtils.getRequest().getParameter("Paras");
		if (s == null)
		{
			return false;
		}

		if (s.contains("IsCC"))
		{
			return true;
		}
		return false;
	}


	public final String getTB_Doc()throws Exception
	{
		return GetRequestVal("TB_Doc");
	}

	public String getSID()throws Exception
	{
		return GetRequestVal("SID");
	}
	public String getVals()
	{
		String str = this.GetRequestVal("Vals");
		if (DataType.IsNullOrEmpty(str))
			return null;
		return str;
	}

}
