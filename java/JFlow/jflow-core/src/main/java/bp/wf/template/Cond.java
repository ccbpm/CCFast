package bp.wf.template;

import bp.da.*;
import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.port.*;
import bp.difference.*;
import bp.*;
import bp.tools.Json;
import bp.web.WebUser;
import bp.wf.*;
import java.util.*;

/**
 条件
*/
public class Cond extends EntityMyPK
{

		///#region 身份.
	private WebUserCopy _webUserCopy = null;
	public final WebUserCopy getWebUser() throws Exception {
		if (_webUserCopy == null)
		{
			_webUserCopy = new WebUserCopy();
			_webUserCopy.LoadWebUser();
		}
		return _webUserCopy;
	}
	public final void setWebUser(WebUserCopy value)
	{
		_webUserCopy = value;
	}
	private GuestUserCopy _guestUserCopy = null;
	public final GuestUserCopy getGuestUser()
	{
		if (_guestUserCopy == null)
		{
			_guestUserCopy = new GuestUserCopy();
			_guestUserCopy.LoadWebUser();
		}
		return _guestUserCopy;
	}
	public final void setGuestUser(GuestUserCopy value)
	{
		_guestUserCopy = value;
	}
	private Node _node = null;
	public final void setNode(Node nd){
		_node = nd;
	}
	public final Node getNode() throws Exception {
		if (_node == null){
			Node nd = new Node(this.getNodeID());
			_node = nd;
			return _node;
		}

		return _node;
	}

		///#endregion 身份.


		///#region 参数属性.
	/**
	 指定人员方式
	*/
	public final SpecOperWay getSpecOperWay() {
		return SpecOperWay.forValue(this.GetValIntByKey(CondAttr.SpecOperWay, 0));
	}
	public final void setSpecOperWay(SpecOperWay value)  {
		this.SetValByKey(CondAttr.SpecOperWay, value.getValue());
	}
	/**
	 指定人员参数
	*/
	// Vue3 从Row中读写 @wanglu
	public final String getSpecOperPara() {
		return this.GetValStringByKey(CondAttr.SpecOperPara);
	}
	public final void setSpecOperPara(String value)  {
		this.SetValByKey(CondAttr.SpecOperPara, value);
	}
	// end
	/**
	 求指定的人员.
	*/
	public final String getSpecOper() throws Exception {
		int specOperWay  = this.GetValIntByKey(CondAttr.SpecOperWay,0);
		if (specOperWay == 0 || specOperWay == 1 || specOperWay==2)
		{
			return bp.web.WebUser.getNo();
		}


		if (specOperWay == SpecOperWay.SpecNodeOper.getValue())
		{
			String sql = "SELECT FK_Emp FROM WF_GenerWorkerlist WHERE FK_Node=" + this.getSpecOperPara() + " AND WorkID=" + this.getWorkID();
			String fk_emp = DBAccess.RunSQLReturnStringIsNull(sql, null);
			if (fk_emp == null)
			{
				throw new RuntimeException("@您在配置方向条件时错误，求指定的人员的时候，按照指定的节点[" + this.getSpecOperPara() + "]，作为处理人，但是该节点上没有人员。查询SQL:" + sql);
			}
			return fk_emp;
		}

		if (specOperWay == SpecOperWay.SpecSheetField.getValue())
		{
			if (this.en.getRow().containsKey(this.getSpecOperPara().replace("@", "").replace(this.en.getRptName()+"_","")) == false)
			{
				throw new RuntimeException("@您在配置方向条件时错误，求指定的人员的时候，按照指定的字段[" + this.getSpecOperPara() + "]作为处理人，但是该字段不存在。");
			}

			return this.en.GetValStringByKey(this.getSpecOperPara().replace("@", "").replace(this.en.getRptName()+"_",""));
		}

		if (specOperWay == SpecOperWay.CurrOper.getValue())
		{
			if (this.en.getRow().containsKey(this.getSpecOperPara().replace("@", "").replace(this.en.getRptName()+"_","")) == false)
			{
				throw new RuntimeException("@您在配置方向条件时错误，求指定的人员的时候，按照指定的字段[" + this.getSpecOperPara() + "]作为处理人，但是该字段不存在。");
			}

			return this.en.GetValStringByKey(this.getSpecOperPara().replace("@", ""));
		}

		if (specOperWay == SpecOperWay.SpenEmpNo.getValue())
		{
			if (DataType.IsNullOrEmpty(this.getSpecOperPara()) == false)
			{
				throw new RuntimeException("@您在配置方向条件时错误，求指定的人员的时候，按照指定的人员[" + this.getSpecOperPara() + "]作为处理人，但是人员参数没有设置。");
			}
			return this.getSpecOperPara();
		}

		return "";
	}

		///#endregion 参数属性.


		///#region 基本属性.
	public GERpt en = null;
	/**
	 数据来源
	*/
	public final ConnDataFrom getHisDataFrom() {
		return ConnDataFrom.forValue(this.GetValIntByKey(CondAttr.DataFrom));
	}
	public final void setHisDataFrom(ConnDataFrom value) throws Exception {
		switch (value)
		{
			case NodeForm:
				this.setDataFromText("节点表单");
				break;
			case StandAloneFrm:
				this.setDataFromText("独立表单");
				break;
			case CondOperator:
				this.setDataFromText("操作符");
				break;
			case Depts:
				this.setDataFromText("部门条件");
				break;
			case Stas:
				this.setDataFromText("角色条件");
				break;
			case SQL:
				this.setDataFromText("SQL条件");
				break;
			case SQLTemplate:
				this.setDataFromText("SQL模板条件");
				break;
			case Paras:
				this.setDataFromText("参数条件");
				break;
			case Url:
				this.setDataFromText("URL条件");
				break;
			case WebApi:
				this.setDataFromText("WebAPI条件");
				break;
			case WorkCheck:
				this.setDataFromText("审核组件立场");
				break;
			default:
				break;
		}

		this.SetValByKey(CondAttr.DataFrom, value.getValue());
	}
	public final String getRefPKVal()  {
		return this.GetValStringByKey(CondAttr.RefPKVal);
	}
	public final void setRefPKVal(String value){
		this.SetValByKey(CondAttr.RefPKVal, value);
	}
	public final String getDataFromText()  {
		return this.GetValStringByKey(CondAttr.DataFromText);
	}
	public final void setDataFromText(String value){
		this.SetValByKey(CondAttr.DataFromText, value);
	}
	/**
	 流程编号
	*/
	public final String getFlowNo()  {
		return this.GetValStringByKey(CondAttr.FK_Flow);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(CondAttr.FK_Flow, value);
	}
	/**
	 隶属流程编号，用于备份删除.
	*/
	public final String getRefFlowNo()  {
		return this.GetValStringByKey(CondAttr.RefFlowNo);
	}
	public final void setRefFlowNo(String value){
		this.SetValByKey(CondAttr.RefFlowNo, value);
	}
	/**
	 数据源
	*/
	public final String getDBSrcNo()  {
		return this.GetValStringByKey(CondAttr.FK_DBSrc);
	}
	public final void setDBSrcNo(String value){
		this.SetValByKey(CondAttr.FK_DBSrc, value);
	}
	/**
	 备注
	*/
	public final String getNote()  {
		return this.GetValStringByKey(CondAttr.Note);
	}
	public final void setNote(String value){
		this.SetValByKey(CondAttr.Note, value);
	}
	/**
	 条件类型(表单条件，角色条件，部门条件，开发者参数)
	*/
	public final CondType getCondType() {
		return CondType.forValue(this.GetValIntByKey(CondAttr.CondType));
	}
	public final void setCondType(CondType value){
		this.SetValByKey(CondAttr.CondType, value.getValue());
	}
	public final int getCondTypeInt()  {
		return this.GetValIntByKey(CondAttr.CondType);
	}
	public final void setCondTypeInt(int value){
		this.SetValByKey(CondAttr.CondType, value);
	}
	/**
	 节点ID
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(CondAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(CondAttr.FK_Node, value);
	}
	/**
	 对方向条件有效
	*/
	public final int getToNodeID()  {
		return this.GetValIntByKey(CondAttr.ToNodeID);
	}
	public final void setToNodeID(int value){
		this.SetValByKey(CondAttr.ToNodeID, value);
	}

		///#endregion


		///#region 重写.
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getRefFlowNo()) == true)
		{
			if (this.getCondType() == CondType.Dir || this.getCondType() == CondType.Node || this.getCondType() == CondType.SubFlow)
			{
				Node nd = new Node(this.getNodeID());
				this.setRefFlowNo(nd.getFlowNo());
			}

			if (this.getCondType() == CondType.Flow)
			{
				this.setRefFlowNo(this.getFlowNo());
				if (DataType.IsNullOrEmpty(this.getRefFlowNo()) == true)
				{
					throw new RuntimeException("err@流程完成条件设置错误，没有给FlowNo赋值。");
				}
			}

			//for vue版本数据格式.增加一个主从表的标记字段.
			if (this.getCondType() == CondType.Dir)
			{
				this.setRefPKVal(this.getFlowNo() + '_' + this.getNodeID() + '_' + this.getToNodeID());
			}
		}
		return super.beforeUpdateInsertAction();
	}
	@Override
	protected boolean beforeInsert() throws Exception
	{
		//设置他的主键。
		this.setMyPK(DBAccess.GenerGUID(0, null, null));
		return super.beforeInsert();
	}
	/**
	 清除数据缓存
	*/
	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		Flow flow = new Flow(this.getFlowNo());
		flow.ClearAutoNumCache(true);
		super.afterInsertUpdateAction();
	}
	@Override
	protected void afterDelete() throws Exception
	{
		Log.DebugWriteInfo("用户[" + WebUser.getNo() + "]，执行删除方向条件，数据=" + bp.tools.Json.ToJson(this.ToDataTableField()));
		Flow flow = new Flow(this.getFlowNo());
		flow.ClearAutoNumCache(true);
		super.afterDelete();
	}

		///#endregion 重写.


		///#region 实现基本的方方法
	/**
	 属性
	*/
	public final String getAttrNo()  {
		return this.GetValStringByKey(CondAttr.FK_Attr);
	}
	public final void setAttrNo(String value) throws Exception {
		if (value == null)
		{
			throw new RuntimeException("FK_Attr不能设置为null");
		}

		value = value.trim();

		this.SetValByKey(CondAttr.FK_Attr, value);

		MapAttr attr = new MapAttr(value);

		if (attr.getLGType() == FieldTypeS.Enum)
		{
			/*是一个枚举类型的*/
			SysEnum se = new SysEnum(attr.getUIBindKey(), this.getOperatorValueInt());
			this.setOperatorValueT(se.getLab());
		}

		this.SetValByKey(CondAttr.AttrKey, attr.getKeyOfEn());
		this.SetValByKey(CondAttr.AttrName, attr.getName());
	}
	/**
	 要运算的实体属性
	*/
	public final String getAttrKey()  {
		return this.GetValStringByKey(CondAttr.AttrKey);
	}
	public final void setAttrKey(String value){
		this.SetValByKey(CondAttr.AttrKey, value);
	}
	/**
	 属性名称
	*/
	public final String getAttrName()  {
		return this.GetValStringByKey(CondAttr.AttrName);
	}
	public final void setAttrName(String value){
		this.SetValByKey(CondAttr.AttrName, value);
	}
	/**
	 Idx
	*/
	public final int getIdx()  {
		return this.GetValIntByKey(CondAttr.Idx);
	}
	public final void setIdx(int value){
		this.SetValByKey(CondAttr.Idx, value);
	}
	/**
	 计算方向
	*/
	public final boolean getJSFX()  {
		return this.GetValBooleanByKey(CondAttr.JSFX);
	}
	/**
	 操作的值
	*/
	public final String getOperatorValueT()  {
		return this.GetValStringByKey(CondAttr.OperatorValueT);
	}
	public final void setOperatorValueT(String value){
		this.SetValByKey(CondAttr.OperatorValueT, value);
	}
	/**
	 运算符号
	*/
	public final String getOperatorNo() throws Exception {
		String s = this.GetValStringByKey(CondAttr.FK_Operator);
		if (s == null || Objects.equals(s, ""))
		{
			return "=";
		}
		return s;
	}
	public final void setOperatorNo(String value) throws Exception {
		String val = "";

		switch (value)
		{
			case "dengyu":
				val = "=";
				break;
			case "dayu":
				val = ">";
				break;
			case "dayudengyu":
				val = ">=";
				break;
			case "xiaoyu":
				val = "<";
				break;
			case "xiaoyudengyu":
				val = "<=";
				break;
			case "budengyu":
				val = "!=";
				break;
			case "like":
				val = " LIKE ";
				break;
			default:
				break;
		}

		this.SetValByKey(CondAttr.FK_Operator, val);
	}
	/**
	 运算值
	*/
	public final Object getOperatorValue() throws Exception {
		String s = this.GetValStringByKey(CondAttr.OperatorValue);
		s = s.replace("~", "'");

		if (s.contains("@") == true)
		{
			if (s.equals("@WebUser.No") == true)
			{
				return getWebUser().No;
			}
			if (s.equals("@WebUser.Name") == true)
			{
				return getWebUser().Name;
			}
			if (s.equals("@WebUser.FK_Dept") == true || s.equals("@WebUser.DeptNo") == true)
			{
				return getWebUser().DeptNo;
			}
			if (s.equals("@WebUser.FK_DeptName") == true)
			{
				return getWebUser().DeptName;
			}
		}
		return s;
	}
	public final void setOperatorValue(Object value){
		this.SetValByKey(CondAttr.OperatorValue, value instanceof String ? (String)value : null);
	}
	/**
	 操作值Str
	*/
	public final String getOperatorValueStr() throws Exception {
		String sql = this.GetValStringByKey(CondAttr.OperatorValue);
		sql = sql.replace("~", "'");
		return sql;
	}
	/**
	 操作值int
	*/
	public final int getOperatorValueInt()  {
		return this.GetValIntByKey(CondAttr.OperatorValue);
	}
	/**
	 操作值boolen
	*/
	public final boolean getOperatorValueBool()  {
		return this.GetValBooleanByKey(CondAttr.OperatorValue);
	}
	private long _FID = 0;
	public final long getFID()
	{
		return _FID;
	}
	public final void setFID(long value)
	{
		_FID = value;
	}

	/**
	 workid
	*/
	private long _WorkID = 0;
	public final long getWorkID()
	{
		return _WorkID;
	}
	public final void setWorkID(long value)
	{
		_WorkID = value;
	}
	/**
	 条件消息
	*/
	public String MsgOfCond = "";
	/**
	 上移

	 @param fk_node 节点ID
	*/
	public final void DoUp(int fk_node) throws Exception {
		int condtypeInt = this.getCondType().getValue();
		this.DoOrderUp(CondAttr.FK_Node, String.valueOf(fk_node), CondAttr.CondType, String.valueOf(condtypeInt), CondAttr.Idx);
	}
	/**
	 下移

	 @param fk_node 节点ID
	*/
	public final void DoDown(int fk_node) throws Exception {
		int condtypeInt = this.getCondType().getValue();
		this.DoOrderDown(CondAttr.FK_Node, String.valueOf(fk_node), CondAttr.CondType, String.valueOf(condtypeInt), CondAttr.Idx);
	}
	/**
	 方向条件-下移
	*/
	public final void DoDown2020Cond() throws Exception {
		if (this.getCondType() == CondType.Dir)
		{
			this.DoOrderDown(CondAttr.FK_Node, this.getNodeID(), CondAttr.ToNodeID, this.getToNodeID(), CondAttr.CondType, CondType.Dir.getValue(), CondAttr.Idx);
		}

		if (this.getCondType() == CondType.Flow)
		{
			this.DoOrderDown(CondAttr.FK_Node, this.getNodeID(), CondAttr.CondType, CondType.Flow.getValue(), CondAttr.Idx);
		}

		if (this.getCondType() == CondType.SubFlow)
		{
			this.DoOrderDown(CondAttr.FK_Node, this.getNodeID(), CondAttr.CondType, CondType.SubFlow.getValue(), CondAttr.Idx);
		}

		if (this.getCondType() == CondType.Node)
		{
			this.DoOrderDown(CondAttr.FK_Node, this.getNodeID(), CondAttr.CondType, CondType.Node.getValue(), CondAttr.Idx);
		}
	}
	/**
	 方向条件-上移
	*/
	public final void DoUp2020Cond() throws Exception {
		if (this.getCondType() == CondType.Dir)
		{
			this.DoOrderUp(CondAttr.FK_Node, this.getNodeID(), CondAttr.ToNodeID, this.getToNodeID(), CondAttr.CondType, CondType.Dir.getValue(), CondAttr.Idx);
		}

		if (this.getCondType() == CondType.Flow)
		{
			this.DoOrderUp(CondAttr.FK_Node, this.getNodeID(), CondAttr.CondType, CondType.Flow.getValue(), CondAttr.Idx);
		}

		if (this.getCondType() == CondType.SubFlow)
		{
			this.DoOrderUp(CondAttr.FK_Node, this.getNodeID(), CondAttr.CondType, CondType.SubFlow.getValue(), CondAttr.Idx);
		}

		if (this.getCondType() == CondType.Node)
		{
			this.DoOrderUp(CondAttr.FK_Node, this.getNodeID(), CondAttr.CondType, CondType.Node.getValue(), CondAttr.Idx);
		}
	}

		///#endregion


		///#region 构造方法
	/**
	 条件
	*/
	public Cond()
	{
	}
	/**
	 条件

	 @param mypk
	*/
	public Cond(String mypk) throws Exception
	{
		this.setMyPK(mypk);
		this.Retrieve();
	}

		///#endregion


		///#region 公共方法
	/**
	 这个条件能不能通过
	*/
	public boolean isPassed() throws Exception {
		Node nd = new Node(this.getNodeID());
		if (this.en == null)
		{

				///#region 实体不存在则进行重新初始化
			GERpt en = nd.getHisFlow().getHisGERpt();
			try
			{
				en.SetValByKey("OID", this.getWorkID());
				en.Retrieve();
				en.ResetDefaultVal();
				this.en = en;
			}
			catch (RuntimeException ex)
			{
				//this.Delete();
				return false;
				//throw new Exception("@在取得判断条件实体[" + nd.getEnDesc() + "], 出现错误:" + ex.Message + "@错误原因是定义流程的判断条件出现错误,可能是你选择的判断条件工作类是当前工作节点的下一步工作造成,取不到该实体的实例.");
			}

				///#endregion
		}
		if (this.getHisDataFrom()  == ConnDataFrom.CondEmp)
		{
			// region 按人员控制
			String strs ="," + this.getOperatorValue().toString() + ",";
			if (strs.contains("," + WebUser.getNo() + ",") == true)
			{
				//处理计算方向.
				if (this.getJSFX() == false)
					return true;
				else
					return false;
			}else{
				//处理计算方向.
				if (this.getJSFX() == true)
					return true;
				else
					return false;

			}

		}
		if (this.getHisDataFrom() == ConnDataFrom.Stas)
		{

				///#region 按角色控制
			String strs = "," + this.getOperatorValue().toString() + ",";
			strs += "," + this.getOperatorValueT().toString() + ",";
			strs = strs.replace("@", ",");

			String strs1 = "";

			DeptEmpStations sts = GetCondShengFenStations();
			for (DeptEmpStation st : sts.ToJavaList())
			{
				if (strs.contains("," + st.getStationNo() + ",") == true)
				{
					this.MsgOfCond = "@以角色判断方向，条件为true：角色集合" + strs + "，操作员(" + bp.web.WebUser.getNo() + ")角色:" + st.getStationNo() + st.getStationT();

					//处理计算方向.
					if (this.getJSFX() == false)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				strs1 += st.getStationNo() + "-" + st.getStationT();
			}

			this.MsgOfCond = "@以角色判断方向，条件为false：角色集合" + strs + "，操作员(" + bp.web.WebUser.getNo() + ")角色:" + strs1;

			//处理计算方向.
			if (this.getJSFX() == false)
			{
				return false;
			}
			else
			{
				return true;
			}

				///#endregion
		}

		if (this.getHisDataFrom() == ConnDataFrom.Depts)
		{

				///#region 按部门控制
			String strs = "," + this.getOperatorValue().toString() + ",";
			strs = strs.replace("@", ",");

			// 需要递归计算.
			String subDeptStr = "";
			if (this.getItIsSubDept())
			{
				String[] splitStrs = strs.split(",");
				for (String str : splitStrs)
				{
					if(DataType.IsNullOrEmpty(str))
						continue;
					subDeptStr = GenerDeptNosString(str, subDeptStr);
				}
			}
			strs += subDeptStr + ",";

			//计算出来当前人员的所有部门.
			DeptEmps sts = GetCondShengFenDepts();
			String strs1 = "";
			for (DeptEmp st : sts.ToJavaList())
			{
				if (strs.contains("," + st.getDeptNo() + ",") == true)
				{
					this.MsgOfCond = "@以角色判断方向，条件为true：部门集合" + strs + "，操作员(" + bp.web.WebUser.getNo() + ")部门:" + st.getDeptNo();

					//处理计算方向.
					if (this.getJSFX() == false)
					{
						return true;
					}
					else
					{
						return false;
					}
				}

				strs1 += st.getDeptNo();
			}

			this.MsgOfCond = "@以部门判断方向，条件为false：部门集合" + strs + "，操作员(" + bp.web.WebUser.getNo() + ")部门:" + strs1;

			//处理计算方向.
			if (this.getJSFX() == false)
			{
				return false;
			}
			else
			{
				return true;
			}

				///#endregion
		}

		if (this.getHisDataFrom() == ConnDataFrom.SQL)
		{

				///#region 按SQL 计算
			//this.MsgOfCond = "@以表单值判断方向，值 " + en.getEnDesc() + "." + this.AttrKey + " (" + en.GetValStringByKey(this.AttrKey) + ") 操作符:(" + this.OperatorNo + ") 判断值:(" + this.OperatorValue.ToString() + ")";
			String sql = this.getOperatorValueStr();
			sql = sql.replace("~", "'");
			sql = sql.replace("@WebUser.No", bp.web.WebUser.getNo());
			sql = sql.replace("@WebUser.Name", bp.web.WebUser.getName());
			sql = sql.replace("@WebUser.FK_DeptName", bp.web.WebUser.getDeptName());
			sql = sql.replace("@WebUser.FK_Dept", bp.web.WebUser.getDeptNo());
			sql = sql.replace("@WebUser.DeptNo", bp.web.WebUser.getDeptNo());

			sql = sql.replace("@WebUser.OrgNo", bp.web.WebUser.getOrgNo());
			//获取参数值
			//System.Collections.Specialized.NameValueCollection urlParams = ContextHolderUtils.getRequest().getParameter; // System.Web.HttpContext.Current.Request.Form;
			for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet())
			{
				//循环使用数组
				if (DataType.IsNullOrEmpty(key) == false && sql.contains(key) == true)
				{
					sql = sql.replace("@" + key, ContextHolderUtils.getRequest().getParameter(key));
				}
				//sql = sql.replace("@" + key, urlParams[key]);
			}

			if (en.getItIsOIDEntity() == true)
			{
				sql = sql.replace("@WorkID", en.GetValStrByKey("OID"));
				sql = sql.replace("@OID", en.GetValStrByKey("OID"));
			}

			if (sql.contains("@") == true)
			{
				/* 如果包含 @ */
				for (Attr attr : this.en.getEnMap().getAttrs())
				{
					sql = sql.replace("@" + attr.getKey(), en.GetValStrByKey(attr.getKey()));
				}
			}

			int result = 0;
			//如果是本地数据源
			if (DataType.IsNullOrEmpty(this.getDBSrcNo()) == true || this.getDBSrcNo().equals("local"))
			{
				result = DBAccess.RunSQLReturnValInt(sql, -1);
			}
			else
			{
				SFDBSrc dbSrc = new SFDBSrc(this.getDBSrcNo());
				result = dbSrc.RunSQLReturnInt(sql, 0);
			}
			if (result <= 0)
			{
				return false;
			}

			return true;

				///#endregion 按SQL 计算
		}

		if (this.getHisDataFrom() == ConnDataFrom.SQLTemplate)
		{

				///#region 按SQLTemplate 计算
			//this.MsgOfCond = "@以表单值判断方向，值 " + en.getEnDesc() + "." + this.AttrKey + " (" + en.GetValStringByKey(this.AttrKey) + ") 操作符:(" + this.OperatorNo + ") 判断值:(" + this.OperatorValue.ToString() + ")";
			String fk_sqlTemplate = this.getOperatorValueStr();
			SQLTemplate sqltemplate = new SQLTemplate();
			sqltemplate.setNo(fk_sqlTemplate);
			if (sqltemplate.RetrieveFromDBSources() == 0)
			{
				throw new RuntimeException("@配置的SQLTemplate编号为[" + sqltemplate + "]被删除了,判断条件丢失.");
			}

			String sql = sqltemplate.getDocs();
			sql = sql.replace("~", "'");
			sql = sql.replace("@WebUser.No", bp.web.WebUser.getNo());
			sql = sql.replace("@WebUser.Name", bp.web.WebUser.getName());
			sql = sql.replace("@WebUser.FK_Dept", bp.web.WebUser.getDeptNo());
			sql = sql.replace("@WebUser.DeptNo", bp.web.WebUser.getDeptNo());
			if (en.getItIsOIDEntity() == true)
			{
				sql = sql.replace("@WorkID", en.GetValStrByKey("OID"));
				sql = sql.replace("@OID", en.GetValStrByKey("OID"));
			}

			if (sql.contains("@") == true)
			{
				/* 如果包含 @ */
				for (Attr attr : this.en.getEnMap().getAttrs())
				{
					sql = sql.replace("@" + attr.getKey(), en.GetValStrByKey(attr.getKey()));
				}
			}

			int result = 0;
			//如果是本地数据源
			if (DataType.IsNullOrEmpty(this.getDBSrcNo()) == true || Objects.equals(this.getDBSrcNo(), "local"))
			{
				result = DBAccess.RunSQLReturnValInt(sql, -1);
			}
			else
			{
				SFDBSrc dbSrc = new SFDBSrc(this.getDBSrcNo());
				result = dbSrc.RunSQLReturnInt(sql, 0);
			}
			if (result <= 0)
			{
				return false;
			}

			if (result >= 1)
			{
				return true;
			}

			throw new RuntimeException("@您设置的sql返回值，不符合农芯BPM的要求，必须是0或大于等于1。");

				///#endregion
		}
		if (this.getHisDataFrom() == ConnDataFrom.CondLeaderOfDept) {
			//按部门控制
			String strs = "," + this.getOperatorValue().toString() + ",";
			strs = strs.replace("@", "");
			//需递归计算
			String subDeptStr = "";
			if (this.getItIsSubDept()) {
				for (String str : strs.split(",")) {
					subDeptStr = str+GenerDeptNosString(str, subDeptStr);
				}
			}
			strs += subDeptStr + ",";
			String sql = "SELECT Leader FROM Port_Dept WHERE No IN (" + bp.port.Glo.GenerWhereInSQL(strs) + ")";
			DataTable dtLeader = DBAccess.RunSQLReturnTable(sql);
			String empNosOfLeader = ",";
			for (DataRow dr : dtLeader.Rows) {
				empNosOfLeader += dr.getValue(0) + ",";
			}

			//计算出来当前节点人员的所有部门
			bp.port.DeptEmps sts = GetCondShengFenDepts();
			for (bp.port.DeptEmp st : sts.ToJavaList()) {
				if (empNosOfLeader.contains("," + st.getEmpNo() + ",") == true) {
					this.MsgOfCond = "@判断部门领导，条件为true: 部门集合" + strs + ",操作员(" + bp.web.WebUser.getNo() + "),部门：" + st.getDeptNo();
					//处理计算方向
					if (this.getJSFX() == false)
						return true;
					else
						return false;
				}
			}
			this.MsgOfCond = "@判断部门领导，条件为false:部门集合" + strs + ",操作员(" + bp.web.WebUser.getNo() + ")";
			//计算方向B
			if(this.getJSFX() == false)
				return false;
			else
				return true;
		}
		if (this.getHisDataFrom() == ConnDataFrom.Url)
		{

				///#region URL 参数计算
			String url = this.getOperatorValueStr();
			if (url.contains("?") == false)
			{
				url = url + "?1=2";
			}

			url = url.replace("@SDKFromServHost", SystemConfig.GetValByKey("SDKFromServHost",""));
			url = bp.wf.Glo.DealExp(url, this.en, "");


				///#region 加入必要的参数.
			if (url.contains("&FlowNo") == false)
			{
				url += "&FlowNo=" + this.getFlowNo();
			}
			if (url.contains("&NodeID") == false)
			{
				url += "&NodeID=" + this.getNodeID();
			}

			if (url.contains("&WorkID") == false)
			{
				url += "&WorkID=" + this.getWorkID();
			}

			if (url.contains("&FID") == false)
			{
				url += "&FID=" + this.getFID();
			}

			if (url.contains("&Token") == false)
			{
				url += "&Token=" + bp.web.WebUser.getToken();
			}

			if (url.contains("&UserNo") == false)
			{
				url += "&UserNo=" + bp.web.WebUser.getNo();
			}



				///#endregion 加入必要的参数.


				///#region 对url进行处理.
			if (SystemConfig.isBSsystem())
			{
				/*是bs系统，并且是url参数执行类型.*/
				String myurl = ContextHolderUtils.getRequest().getRequestURI(); // BP.Sys.Base.Glo.Request.RawUrl;
				if (myurl.indexOf('?') != -1)
				{
					myurl = myurl.substring(myurl.indexOf('?'));
				}

				myurl = myurl.replace("?", "&");
				String[] paras = myurl.split("&");
				for (String s : paras)
				{
					String[] strs = s.split("=");

					//如果已经有了这个参数.
					if (url.contains(strs[0] + "=") == true)
					{
						continue;
					}

					if (url.contains(s))
					{
						continue;
					}
					url += "&" + s;
				}
				url = url.replace("&?", "&");
			}

			//替换特殊的变量.
			url = url.replace("&?", "&");

			if (SystemConfig.isBSsystem() == false)
			{
				/*非bs模式下调用,比如在cs模式下调用它,它就取不到参数. */
			}


			if (url.contains("http") == false)
			{
				/*如果没有绝对路径 */
				if (SystemConfig.isBSsystem())
				{
					/*在cs模式下自动获取*/
					String host = SystemConfig.getHostURL(); //BP.Sys.Base.Glo.Request.Url.Host;
					if (url.contains("@AppPath"))
					{
						url = url.replace("@AppPath", "http://" + host + bp.sys.Glo.getRequest().getRequestURI()); //BP.Sys.Base.Glo.Request.ApplicationPath
					}
					else //BP.Sys.Base.Glo.Request.Url.Authority
					{
						url = "http://" + bp.sys.Glo.getRequest().getRequestURI() + url;
					}
				}

				if (SystemConfig.isBSsystem() == false)
				{
					/*在cs模式下它的baseurl 从web.config中获取.*/
					String cfgBaseUrl = SystemConfig.GetValByKey("HostURL","");
					if (DataType.IsNullOrEmpty(cfgBaseUrl))
					{
						String err = "调用url失败:没有在web.config中配置BaseUrl,导致url事件不能被执行.";
						Log.DebugWriteError(err);
						throw new RuntimeException(err);
					}
					url = cfgBaseUrl + url;
				}
			}

				///#endregion 对url进行处理.


				///#region 求url的值
			try
			{
				url = url.replace("'", "");
				// url = url.replace("//", "/");
				// url = url.replace("//", "/");
				String text = DataType.ReadURLContext(url, 8000);
				if (text == null)
				{
					//throw new Exception("@流程设计的方向条件错误，执行的URL错误:" + url + ", 返回为null, 请检查设置是否正确。");
					return false;
				}

				if (DataType.IsNullOrEmpty(text) == true)
				{
					// throw new Exception("@错误，没有接收到返回值.");
					return false;
				}

				if (DataType.IsNumStr(text) == false)
				{
					//throw new Exception("@错误，不符合约定的格式，必须是数字类型。");
					return false;
				}
				try
				{
					float f = Float.parseFloat(text);
					if (f > 0)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				catch (RuntimeException ex)
				{
					return false;
				}
			}
			catch (RuntimeException ex)
			{
				throw new RuntimeException("@判断url方向出现错误:" + ex.getMessage() + ",执行url错误:" + url);
			}

				///#endregion 对url进行处理.


				///#endregion
		}

		if (this.getHisDataFrom() == ConnDataFrom.WebApi)
		{

				///#region WebApi接口
			//返回值
			String postData = "";
			String apiUrl = this.getOperatorValueStr();
			if (apiUrl.contains("@WebApiHost")) //可以替换配置文件中配置的webapi地址
			{
				apiUrl = apiUrl.replace("@WebApiHost", SystemConfig.GetValByKey("WebApiHost",""));
			}

			//如果有参数
			if (apiUrl.contains("?"))
			{
				//api接口地址
				String apiHost = apiUrl.split("\\?")[0];
				//api参数
				String apiParams = apiUrl.split("\\?")[1];
				//参数替换
				apiParams = bp.wf.Glo.DealExp(apiParams, this.en);
				//执行POST
				postData = bp.tools.PubGlo.HttpPostConnect(apiHost, apiParams,"POST",true);

				if (Objects.equals(postData, "true"))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			else
			{ //如果没有参数，执行GET
				postData = bp.tools.PubGlo.HttpGet(apiUrl);
				if (Objects.equals(postData, "true"))
				{
					return true;
				}
				else
				{
					return false;
				}
			}

				///#endregion WebApi接口
		}


			///#region 审核组件的立场
		if (this.getHisDataFrom() == ConnDataFrom.WorkCheck)
		{
			//获取当前节点的审核组件信息
			String tag = Dev2Interface.GetCheckTag(this.getFlowNo(), this.getWorkID(), this.getNodeID(), getWebUser().getNo());
			if (tag.contains("@FWCView=" + this.getOperatorValue()) == true)
			{
				return true;
			}
			return false;
		}

			///#endregion 审核组件的立场

		if (this.getHisDataFrom() == ConnDataFrom.Paras)
		{
			Hashtable ht = en.getRow();
			return bp.wf.Glo.CondExpPara(this.getOperatorValueStr(), ht, en.getOID());
		}

		//从节点表单里判断.
		if (this.getHisDataFrom() == ConnDataFrom.NodeForm)
		{
			if (en.getEnMap().getAttrs().contains(this.getAttrKey()) == false)
			{
				throw new RuntimeException("err@判断条件方向出现错误：实体：" + nd.getEnDesc() + " 属性" + this.getAttrKey() + "已经被删除方向条件判断失败.");
			}

			this.MsgOfCond = "@以表单值判断方向，值 " + en.getEnDesc() + "." + this.getAttrKey() + " (" + en.GetValStringByKey(this.getAttrKey()) + ") 操作符:(" + this.getOperatorNo() + ") 判断值:(" + this.getOperatorValue().toString() + ")";
			return CheckIsPass(en);
		}

		//从独立表单里判断.
		if (this.getHisDataFrom() == ConnDataFrom.StandAloneFrm)
		{
			MapAttr attr = new MapAttr(this.getAttrNo());
			attr.setMyPK(this.getAttrNo());
			if (attr.RetrieveFromDBSources() == 0)
			{
				throw new RuntimeException("err@到达【" + this.getToNodeID() + "】方向条件设置错误,原来做方向条件的字段:" + this.getAttrNo() + ",已经不存在了.");
			}

			GEEntity myen = new GEEntity(attr.getFrmID(), en.getOID());
			return CheckIsPass(myen);
		}
		return false;
	}
	private DeptEmps GetCondShengFenDepts() throws Exception {
		int specOperWay = this.GetValIntByKey(CondAttr.SpecOperWay,0);
		String specOperPara = this.getSpecOperPara();
		DeptEmps des = new DeptEmps();
		String userNo = this.getSpecOper();
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS && this.getSpecOper().startsWith(getWebUser().OrgNo) == false)
			userNo = getWebUser().OrgNo + "_" + this.getSpecOper();
		GenerWorkerList gwl = new GenerWorkerList();
		int i = 0;
		switch(specOperWay){
			case 0://发送人登录部门
				des.Retrieve(DeptEmpAttr.FK_Dept, WebUser.getDeptNo(),DeptEmpAttr.FK_Emp,userNo);
				break;
			case 1: //发送人的所有部门
			case 33: //指定操作员编号
				des.Retrieve("FK_Emp", userNo);
				break;
			case 2: //发送人使用的部门
			case 3: //发送人使用部门的父级
				int nodeID = this.getNode().getNodeID();
				i = gwl.Retrieve(GenerWorkerListAttr.WorkID,this.getWorkID(),GenerWorkerListAttr.FK_Node,nodeID,GenerWorkerListAttr.FK_Emp,this.getSpecOper());
				if(i == 0)
					throw new RuntimeException("err@不可能出现的错误");
				if(specOperWay == 2)
					des.Retrieve(DeptEmpAttr.FK_Emp, userNo,DeptEmpAttr.FK_Dept,gwl.getDeptNo());
				if(specOperWay == 3){
					Dept dept = new Dept();
					dept.setNo(gwl.getDeptNo());
					if(dept.Retrieve() ==1 )
						des.Retrieve(DeptEmpAttr.FK_Emp, userNo,DeptEmpAttr.FK_Dept,dept.getNo());
				}

				break;

			case 10://指定节点提交人的使用部门
			case 11: //指定节点提交人的所有部门
			case 12: //指定节点提交人的主部门
			case 13:
				if(DataType.IsNullOrEmpty(specOperPara) == true)
					throw new RuntimeException("err@方向条件，人员身份按指定节点提交人计算时请选择节点");
				GenerWorkerLists gwls = new GenerWorkerLists();
				i = gwls.Retrieve(GenerWorkerListAttr.WorkID,this.getWorkID(),GenerWorkerListAttr.FK_Node,specOperPara);
				if(i == 0)
					throw new RuntimeException("err@不可能出现的错误");
				gwl = (GenerWorkerList) gwls.get(0);
				String empNo = gwl.getEmpNo();
				if(SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS){
					empNo = getWebUser().OrgNo + "_" + gwl.getEmpNo();
				}
				if(specOperWay == 10) {
					des.Retrieve("FK_Emp", empNo, "FK_Dept", gwl.getDeptNo());
				}
				if(specOperWay == 11) {
					des.Retrieve("FK_Emp", empNo);
				}
				if(specOperWay == 13){
					Emp emp = new Emp(empNo);
					des.Retrieve("FK_Emp", empNo,"FK_Dept",emp.getDeptNo());
				}
				break;
			case 20: //字段(参数)人员的主部门
			case 21: //字段(参数)人员的所有部门
			case 22: //字段(参数)就是部门编号
				if(DataType.IsNullOrEmpty(specOperPara) == true)
					throw new RuntimeException("err@方向条件，人员身份按表单字段人员计算时请选择表单字段");
				String val = this.en.GetValStringByKey(specOperPara.replace(this.en.getRptName()+"_",""));
				if(DataType.IsNullOrEmpty(val))
					throw new RuntimeException("err@方向条件，人员身份按表单字段人员计算时字段["+specOperPara+"]的值为空");
				if(specOperWay == 20){
					Emp emp = new Emp();
					emp.setUserID(val);
					if(emp.RetrieveFromDBSources()==0)
						throw new RuntimeException("err@方向条件，人员身份按表单字段人员计算时未找到人员编号=["+val+"]");
					des.Retrieve("FK_Emp", val,"FK_Dept",emp.getDeptNo());
				}
				if(specOperWay == 21){
					des.Retrieve("FK_Emp", val);
				}
				if(specOperWay == 22)
					des.Retrieve("FK_Dept", val);
				break;
			case 23: //系统(参数)就是部门编号
				GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
				String val1 = gwf.GetParaString(specOperPara);
				if(DataType.IsNullOrEmpty(val1))
					throw new RuntimeException("err@方向条件，人员身份按表单字段人员计算时字段["+specOperPara+"]的值为空");
				des.Retrieve("FK_Dept", val1);
				break;
			default:
				throw new RuntimeException("err@部门人员身份["+specOperWay+"]未解析");
		}
		return des;
	}
	/**
	 * 岗位人员身份
	 * @return
	 * @throws Exception
	 */
	private DeptEmpStations GetCondShengFenStations() throws Exception {
		int specOperWay = this.GetValIntByKey(CondAttr.SpecOperWay);
		String specOperPara = this.getSpecOperPara();
		DeptEmpStations sts = new DeptEmpStations();
		String userNo = this.getSpecOper();
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS && this.getSpecOper().startsWith(getWebUser().OrgNo) == false)
			userNo = getWebUser().OrgNo + "_" + this.getSpecOper();
		GenerWorkerList gwl = new GenerWorkerList();
		int i = 0;
		switch(specOperWay){
			case 0://发送人登录部门下所有岗位
			case 2: //发送人选择部门+岗位
				int nodeID = this.getNode().getNodeID();
				String deptNo="";
				String staNo1="";
				i = gwl.Retrieve(GenerWorkerListAttr.WorkID,this.getWorkID(),GenerWorkerListAttr.FK_Node,nodeID,GenerWorkerListAttr.FK_Emp,this.getSpecOper());
				if(i == 0){
					//有可能是审核组件计算未来处理人
					SelectAccpers acpers = new SelectAccpers();
					i = acpers.Retrieve(SelectAccperAttr.WorkID,this.getWorkID(),SelectAccperAttr.FK_Node,nodeID);
					if(i==0)
						throw new RuntimeException("err@不可能出现的错误");
					SelectAccper acper = (SelectAccper)acpers.get(0);
					deptNo = acper.getDeptNo();

				}else{
					deptNo = gwl.getDeptNo();
					staNo1 = gwl.GetValStrByKey("StaNo");
				}

				if(specOperWay == 0)
					sts.Retrieve("FK_Emp", userNo,"FK_Dept",deptNo);
				if(specOperWay == 2)
					sts.Retrieve("FK_Emp", userNo,"FK_Station",staNo1);
				break;
			case 1: //发送人的所有部门的岗位
				sts.Retrieve("FK_Emp", userNo);
				break;
			case 10://指定节点提交人的使用部门下所有岗位
			case 11: //指定节点提交人的所有部门的岗位
			case 24: //指定节点提交人的使用部门所使用的岗位
				if(DataType.IsNullOrEmpty(specOperPara) == true)
					throw new RuntimeException("err@方向条件，人员身份按指定节点提交人计算时请选择节点");
				GenerWorkerLists gwls = new GenerWorkerLists();
				i = gwls.Retrieve(GenerWorkerListAttr.WorkID,this.getWorkID(),GenerWorkerListAttr.FK_Node,specOperPara);
				if(i == 0)
					sts.Retrieve("FK_Emp", gwl.getEmpNo(),"FK_Dept",WebUser.getDeptNo());
				//	throw new RuntimeException("err@不可能出现的错误");
				gwl = (GenerWorkerList) gwls.get(0);
				if(specOperWay == 10)
					sts.Retrieve("FK_Emp", gwl.getEmpNo(),"FK_Dept",gwl.getDeptNo());
				if(specOperWay == 11)
					sts.Retrieve("FK_Emp", gwl.getEmpNo());
				if (specOperWay == 24)
				{
					String dbStr = bp.difference.SystemConfig.getAppCenterDBVarStr();
					Paras ps = new Paras();
					ps.SQL = "SELECT NodeData,RDT FROM ND" + Integer.parseInt(this.getFlowNo())+ "Track WHERE ActionType=" + dbStr + "ActionType AND NDFrom=" + dbStr + "NDFrom AND WorkID=" + dbStr + "WorkID ORDER BY RDT DESC";
					ps.Add("ActionType", 1);
					ps.Add("NDFrom", specOperPara);
					ps.Add("WorkID", this.getWorkID());
					DataTable dt = DBAccess.RunSQLReturnTable(ps);
					if(dt.Rows.size() > 0){
						String[] splitNodeData = dt.Rows.get(0).getValue("NodeData").toString().split("@");
						String staNo = "";
						for (String str : splitNodeData)
						{
							if (str.contains("StaNo="))
							{
								staNo = str.split("=")[1];
								break;
							}
						}
						if(DataType.IsNullOrEmpty(staNo)){
							//没有找到设置的岗位数据
							throw new RuntimeException("err@没有找到设置的岗位数据。SQL=" + ps.getSQLNoPara() + ", 数据库=" + Json.ToJson(dt));
						}
						sts.Retrieve("FK_Emp", gwl.getEmpNo(), "FK_Dept", gwl.getDeptNo(), "FK_Station", staNo);
					}
				}

				break;
			case 20: //字段(参数)人员的主部门下所有的岗位
			case 21: //字段(参数)人员的所有部门的岗位
			case 22: //字段(参数)就是岗位编号
				if(DataType.IsNullOrEmpty(specOperPara))
					throw new RuntimeException("err@方向条件，人员身份按表单字段人员计算时请选择表单字段");
				String val = this.en.GetValStringByKey(specOperPara);
				if(DataType.IsNullOrEmpty(val))
					throw new RuntimeException("err@方向条件，人员身份按表单字段人员计算时字段["+specOperPara+"]的值为空");
				if(specOperWay == 20){
					Emp emp = new Emp();
					emp.setUserID(val);
					if(emp.RetrieveFromDBSources()==0)
						throw new RuntimeException("err@方向条件，人员身份按表单字段人员计算时未找到人员编号=["+val+"]");
					sts.Retrieve("FK_Emp", val,"FK_Dept",emp.getDeptNo());
				}
				if(specOperWay == 21){
					sts.Retrieve("FK_Emp", val);
				}
				if(specOperWay == 22)
					sts.Retrieve("FK_Station", val);
				break;
			default:
				throw new RuntimeException("err@岗位人员身份["+specOperWay+"]未解析");
		}
		return sts;
	}
	private boolean CheckIsPass(Entity en) throws Exception {
		MapAttr attr = new MapAttr(this.getAttrNo());
		attr.setMyPK(this.getAttrNo());
		if (attr.RetrieveFromDBSources() == 0)
		{
			throw new RuntimeException("err@到达【" + this.getToNodeID() + "】方向条件设置错误,原来做方向条件的字段:" + this.getAttrNo() + ",已经不存在了.");
		}

		try
		{
			switch (this.getOperatorNo().trim().toLowerCase())
			{
				case "<>":
				case "!=":
				case "budingyu":
				case "budengyu": //不等于.
					if (attr.getItIsNum() == true)
					{
						if (en.GetValDoubleByKey(this.getAttrKey()) != Double.parseDouble(this.getOperatorValue().toString()))
							return true;
						else
							return false;
					}
					if (en.GetValStringByKey(this.getAttrKey()).equals(this.getOperatorValue().toString()) == false)
						return true;
					else
						return false;
				case "=": // 如果是 =
				case "dengyu":
					if (attr.getItIsNum() == true)
					{
						if (en.GetValDoubleByKey(this.getAttrKey()) == Double.parseDouble(this.getOperatorValue().toString()))
							return true;
						else
							return false;
					}
					if (en.GetValStringByKey(this.getAttrKey()).equals(this.getOperatorValue().toString().replace("\"", "")) == true)
						return true;
					else
						return false;
				case ">":
				case "dayu":
					if (en.GetValDoubleByKey(this.getAttrKey()) > Double.parseDouble(this.getOperatorValue().toString()))
						return true;
					else
						return false;

				//if (en.GetValDoubleByKey(this.AttrKey) > Double.Parse(this.OperatorValue.ToString()))
				//    return true;
				//else
				//    return false;
				case ">=":
				case "dayudengyu":
					if (en.GetValDoubleByKey(this.getAttrKey()) >= Double.parseDouble(this.getOperatorValue().toString()))
						return true;
					else
						return false;
				case "<":
				case "xiaoyu":
					if (en.GetValDoubleByKey(this.getAttrKey()) < Double.parseDouble(this.getOperatorValue().toString()))
						return true;
					else
						return false;
				//if (en.GetValDoubleByKey(this.AttrKey) < Double.Parse(this.OperatorValue.ToString()))
				//    return true;
				//else
				//    return false;
				case "<=":
				case "xiaoyudengyu":
					if (en.GetValDoubleByKey(this.getAttrKey()) <= Double.parseDouble(this.getOperatorValue().toString()))
						return true;
					else
						return false;
				case "like":
				case "baohan":
					if (en.GetValStringByKey(this.getAttrKey()).indexOf(this.getOperatorValue().toString()) == -1)
						return false;
					else
						return true;
				case "notlike":
					if (en.GetValStringByKey(this.getAttrKey()).indexOf(this.getOperatorValue().toString()) == -1)
						return true;
					else
						return false;
				case "in":
					String value = "," + this.getOperatorValue() + ",";
					if (value.contains("," + en.GetValStringByKey(this.getAttrKey()) + ","))
						return true;
					return false;
				case "notin":
					String val = "," + this.getOperatorValue() + ",";
					if (val.contains("," + en.GetValStringByKey(this.getAttrKey()) + ",")==false)
						return true;
					return false;
				default:
					throw new RuntimeException("@没有找到操作符号(" + this.getOperatorNo().trim().toLowerCase() + ").");
			}
		}
		catch (RuntimeException ex)
		{
			Node nd23 = new Node(this.getNodeID());
			throw new RuntimeException("@判断条件:Node=[" + this.getNodeID() + "," + nd23.getEnDesc() + "], 出现错误。@" + ex.getMessage() + "。有可能您设置了非法的条件判断方式。");
		}
	}
	/**
	 属性
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("WF_Cond", "条件");

		map.AddMyPK(true);

		//for vue版本数据主从格式. 2022.9.28.
		map.AddTBString(CondAttr.RefPKVal, null, "关联主键", true, true, 0, 40, 20);

		//为流程的模板导出.
		map.AddTBString(CondAttr.RefFlowNo, null, "流程编号", true, true, 0, 5, 20);

		//用于整体流程的删除，导入，导出.
		map.AddTBString(CondAttr.RefFlowNo, null, "流程编号", true, true, 0, 5, 20);
		map.AddTBInt(CondAttr.FK_Node, 0, "节点ID", true, true);

		//@0=节点完成条件@1=流程条件@2=方向条件@3=启动子流程.
		map.AddTBInt(CondAttr.CondType, 0, "条件类型", true, true);
		map.AddTBString(CondAttr.FK_Flow, null, "流程", true, true, 0, 4, 20);

		//对于启动子流程规则有效. *************************************
		map.AddTBString(CondAttr.SubFlowNo, null, "子流程编号", true, true, 0, 5, 20);

		//对方向条件有效. *************************************
		map.AddTBInt(CondAttr.ToNodeID, 0, "ToNodeID（对方向条件有效）", true, true);

		//条件字段. *************************************
		//@0=NodeForm表单数据,1=StandAloneFrm独立表单,2=Stas角色数据,3=Depts,4=按sql计算.
		//5,按sql模版计算.6,按参数,7=按Url @=100条件表达式.
		map.AddTBInt(CondAttr.DataFrom, 0, "条件数据来源", true, true);
		map.AddTBString(CondAttr.DataFromText, null, "条件数据来源T", true, true, 0, 80, 100);
		map.AddTBString(CondAttr.FK_Attr, null, "属性", true, true, 0, 80, 20);
		map.AddTBString(CondAttr.AttrKey, null, "属性键", true, true, 0, 60, 20);
		map.AddTBString(CondAttr.AttrName, null, "中文名称", true, true, 0, 500, 20);
		map.AddTBString(CondAttr.FK_Operator, "=", "运算符号", true, true, 0, 60, 20);
		map.AddTBString(CondAttr.OperatorValue, "", "要运算的值", true, true, 0, 4000, 20);
		map.AddTBString(CondAttr.OperatorValueT, "", "要运算的值T", true, true, 0, 4000, 20);
		map.AddTBString(CondAttr.Note, null, "备注", true, true, 0, 500, 20);
		map.AddTBString(CondAttr.FK_DBSrc, "local", "SQL的数据来源", false, true, 0, 50, 20);
		//参数 for wangrui add 2015.10.6. 条件为station,depts模式的时候，需要指定人员。
		map.AddTBAtParas(2000);
		map.AddTBInt(CondAttr.Idx, 0, "优先级", true, true);
		map.AddTBInt(CondAttr.JSFX, 0, "计算方向", true, true);

		map.AddTBString(CondAttr.FrmID, "", "表单ID", true, true, 0, 100, 20);
		map.AddTBString(CondAttr.FrmName, "", "表单名称", true, true, 0, 100, 20);

		map.AddTBInt(CondAttr.SpecOperWay, 0, "指定人员方式", false, true);
		// map.AddTBString(CondAttr.SpecOperWay, null, "指定人员方式", true, true, 0, 10, 20);
		map.AddTBString(CondAttr.SpecOperPara, null, " 指定人员方式的参数", false, true, 0, 50, 20);

		//用到了UIBindKey的存储.
		map.AddTBString(CondAttr.Tag1, null, "Tag1", true, true, 0, 100, 20);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	private String GenerDeptNosString(String deptNo, String deptNos) throws Exception {
		Depts ens = new Depts();
		ens.Retrieve(EntityTreeAttr.ParentNo, deptNo, null);

		for (Dept en : ens.ToJavaList())
		{
			deptNos += "," + en.getNo();
			deptNos = GenerDeptNosString(en.getNo(), deptNos);
		}
		return deptNos;
	}



	/**
	 是否递归子部门 - 对部门条件计算有效.
	*/
	public final boolean getItIsSubDept() throws Exception {
		String val = this.GetValStringByKey("Tag1");
		if (DataType.IsNullOrEmpty(val) == true || val.equals("0"))
		{
			return false;
		}
		return true;
	}


}
