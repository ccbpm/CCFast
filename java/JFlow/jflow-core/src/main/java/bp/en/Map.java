package bp.en;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.sys.*;
import bp.web.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.*;

/**
 EnMap 的摘要说明。
 */
public class Map  implements Serializable
{
	private static final long serialVersionUID = 1L;
	///帮助.
	/**
	 增加帮助

	 param key 字段
	 param url
	 */
	public final void SetHelperUrl(String key, String url)  {
		if (SystemConfig.isDisHelp())
			return;
		Attr attr = this.GetAttrByKey(key);
		attr.HelperUrl = url;
	}
	/**
	 增加帮助

	 param key 字段
	 */
	public final void SetHelperBaidu(String key)  {
		if (SystemConfig.isDisHelp())
			return;
		Attr attr = this.GetAttrByKey(key);
		attr.HelperUrl = "http://www.baidu.com/s?word=ccflow " + attr.getDesc();
	}
	/**
	 增加帮助

	 param key 字段
	 param keyword 关键字
	 */
	public final void SetHelperBaidu(String key, String keyword)  {
		if (SystemConfig.isDisHelp())
			return;
		Attr attr = this.GetAttrByKey(key);
		attr.HelperUrl = "http://www.baidu.com/s?word=" + keyword;
	}
	/**
	 增加帮助

	 param key 字段
	 param context 连接
	 */
	public final void SetHelperAlert(String key, String context)  {
		if (SystemConfig.isDisHelp())
			return;
		context = context.replace("@", "＠");
		Attr attr = this.GetAttrByKey(key);
		attr.HelperUrl = "javascript:alert('" + context + "')";
	}

	/// 帮助.


	///与xml 文件操作有关系
	/**
	 xml 文件的位置
	 */
	public String XmlFile = null;

	/// 与xml 文件操作有关系
	public boolean ItIsJM = false;
	private boolean _IsAllowRepeatNo;
	public final boolean getIsAllowRepeatNo()
	{
		return _IsAllowRepeatNo;
	}
	public final void setIsAllowRepeatNo(boolean value)
	{
		_IsAllowRepeatNo = value;
	}




	///chuli
	/**
	 查询语句(为了避免过多的资源浪费,一次性生成多次使用)
	 */
	public String SelectSQL = null;
	/**
	 是否是简单的属性集合
	 这里是处理外键的问题，在系统的批量运行过程中太多的外键就会影响效率。
	 */
	public boolean IsSimpleAttrs = false;
	/**
	 设置为简单的
	 * @
	 */
	public final Attrs SetToSimple() throws Exception {
		Attrs attrs = new Attrs();
		for (Attr attr : this._attrs)
		{
			if (attr.getMyFieldType() == FieldType.PK || attr.getMyFieldType() == FieldType.PKEnum || attr.getMyFieldType() == FieldType.PKFK)
			{
				attrs.Add(new Attr(attr.getKey(), attr.getField(), attr.getDefaultVal(), attr.getMyDataType(), true, attr.getDesc()));
			}
			else
			{
				attrs.Add(new Attr(attr.getKey(), attr.getField(), attr.getDefaultVal(), attr.getMyDataType(), false, attr.getDesc()));
			}
		}
		return attrs;
	}

	///


	///关于缓存问题
	public String _FK_MapData = null;
	public final String getFK_MapData()
	{
		if (_FK_MapData == null)
		{
			return this.getPhysicsTable();
		}
		return _FK_MapData;
	}
	public final void setFK_MapData(String value)
	{
		_FK_MapData = value;
	}
	/**
	 存放位置
	 */
	private Depositary _DepositaryOfEntity = Depositary.None;
	/**
	 存放位置OfEntity
	 */
	public final Depositary getDepositaryOfEntity()
	{
		return _DepositaryOfEntity;
	}
	public final void setDepositaryOfEntity(Depositary value)
	{_DepositaryOfEntity = value;
	}
	/**

	 */
	private Depositary _DepositaryOfMap = Depositary.Application;
	/**
	 存放位置
	 */
	public final Depositary getDepositaryOfMap()
	{
		return _DepositaryOfMap;
	}
	public final void setDepositaryOfMap(Depositary value)
	{_DepositaryOfMap = value;
	}

	///


	///查询属性处理


	///非枚举值与外键条件查询
	private SearchNormals _SearchNormals = null;
	public final SearchNormals getSearchNormals()
	{
		if(this._SearchNormals == null)
		{
			this._SearchNormals = new SearchNormals();
		}
		return this._SearchNormals;
	}
	/**
	 得到全部的Attrs

	 @return
	 */
	public final Attrs GetChoseAttrs(Entity en) throws Exception {
		return CField.GetMyAttrs(en.GetNewEntities(), en.get_enMap());
	}
	public final Attrs GetChoseAttrs(Entities ens)
	{
		return CField.GetMyAttrs(ens, this);
	}

	///


	///关于枚举值与外键查找条件
	/**
	 查找的attrs
	 */
	private SearchFKEnums _SearchAttrs = null;
	/**
	 查找的attrs
	 */
	public final SearchFKEnums getSearchFKEnums()
	{
		if (this._SearchAttrs == null)
		{
			this._SearchAttrs = new SearchFKEnums();
		}
		return this._SearchAttrs;
	}

	public final void AddHidden(String refKey, String symbol, String val)
	{
		SearchNormal aos = new SearchNormal("K" + this.getSearchNormals().size(), refKey, refKey, symbol, val, 0, true);
		this.getSearchNormals().Add(aos);
	}
	/**
	 加入查找属性.必须是外键盘/枚举类型/boolen.

	 param key key
	 */

	public final void AddSearchAttr(String key)  {
		AddSearchAttr(key, 130);
	}

	public final void AddSearchAttr(String key, int width)  {
		Attr attr = this.GetAttrByKey(key);
		if (attr.getKey().equals("FK_Dept"))
		{
			this.getSearchFKEnums().Add(attr, false, null, width);
		}
		else
		{
			this.getSearchFKEnums().Add(attr, true, null, width);
		}
	}
	/**
	 加入查找属性.必须是外键盘/枚举类型/boolen.

	 param key 键值
	 param isShowSelectedAll 是否显示全部
	 param relationalDtlKey 级联子菜单字段
	 */
	public final void AddSearchAttr(String key, boolean isShowSelectedAll, String relationalDtlKey)  {
		Attr attr = this.GetAttrByKey(key);
		this.getSearchFKEnums().Add(attr, isShowSelectedAll, relationalDtlKey);
	}


	/**
	 取得字段

	 param key key
	 @return field name
	 */
	public final String GetFieldByKey(String key)  {
		return GetAttrByKey(key).getField();
	}
	/**
	 取得描述

	 param key key
	 @return val
	 */
	public final String GetDescByKey(String key)  {
		return GetAttrByKey(key).getDesc();
	}
	/**
	 通过一个key 得到它的属性值。

	 param key key
	 @return attr
	 */
	public final Attr GetAttrByKey(String key)  {
		for (Attr attr : this.getAttrs())
		{
			if (attr.getKey().toUpperCase().equals(key.toUpperCase()))
			{
				return attr;
			}
		}

		if (key == null)
		{
			throw new RuntimeException("@[" + this.getEnDesc() + "] 获取属性key 值不能为空.");
		}

		if (this.toString().contains("."))
		{
			throw new RuntimeException("@[" + this.toString() + "," + this.getEnDesc() + "," + this.getPhysicsTable() + "] 没有找到 key=[" + key + "]的属性，请检查Map文件。此问题出错的原因之一是，在设置系统中的一个实体的属性关联这个实体，你在给实体设置信息时没有按照规则书写reftext, refvalue。请核实。");
		}
		else
		{
			throw new RuntimeException("@[" + this.toString() + "," + this.getEnDesc() + "," + this.getPhysicsTable() + "] 没有找到 key=[" + key + "]的属性，请检查Sys_MapAttr表是否有该数据,用SQL执行: SELECT * FROM Sys_MapAttr WHERE FK_MapData='" + this.toString() + "' AND KeyOfEn='" + key + "' 是否可以查询到数据，如果没有可能该字段属性丢失。");
		}
	}
	/**
	 获得属性.

	 param key
	 @return
	 */
	public final Attr GetAttrByBindKey(String key) throws Exception {
		for (Attr attr : this.getAttrs())
		{
			if (attr.getUIBindKey().equals(key))
			{
				return attr;
			}
		}
		if (key == null)
		{
			throw new RuntimeException("@[" + this.getEnDesc() + "] 获取属性key 值不能为空.");
		}

		if (this.toString().contains("."))
		{
			throw new RuntimeException("@[" + this.toString() + "," + this.getEnDesc() + "," + this.toString() + "] 没有找到 key=[" + key + "]的属性，请检查Map文件。此问题出错的原因之一是，在设置系统中的一个实体的属性关联这个实体，你在给实体设置信息时没有按照规则书写reftext, refvalue。请核实。");
		}
		else
		{
			throw new RuntimeException("@[" + this.toString() + "," + this.getEnDesc() + "," + this.toString() + "] 没有找到 key=[" + key + "]的属性，请检查Sys_MapAttr表是否有该数据,用SQL执行: SELECT * FROM Sys_MapAttr WHERE FK_MapData='" + this.toString() + "' AND KeyOfEn='" + key + "' 是否可以查询到数据，如果没有可能该字段属性丢失。");
		}
	}
	/**
	 通过一个key 得到它的属性值。


	 @return attr
	 */
	public final Attr GetAttrByDesc(String desc)  {
		for (Attr attr : this.getAttrs())
		{
			if (attr.getDesc().equals(desc) || attr.getKey().equals(desc)== true)
			{
				return attr;
			}
		}
		if (desc == null)
		{
			throw new RuntimeException("@[" + this.toString() + "," + this.getEnDesc() + "] 获取属性 desc  值不能为空.");
		}

		throw new RuntimeException("@[" + this.toString() + "," + this.getEnDesc() + "] 没有找到 desc=[" + desc + "]的属性，请检查Map文件。此问题出错的原因之一是，在设置系统中的一个实体的属性关联这个实体，你在给实体设置信息时没有按照规则书写reftext, refvalue。请核实。");
	}

	///


	///计算属性
	/**
	 取道最大的TB宽度。
	 */
	private int _MaxTBLength = 0;
	/**
	 最大的TB宽度。
	 */
	public final float getMaxTBLength()
	{
		if (_MaxTBLength == 0)
		{
			for (Attr attr : this.getAttrs())
			{
				if (attr.getUIWidth() > _MaxTBLength)
				{
					_MaxTBLength = (int)attr.getUIWidth();
				}
			}
		}
		return _MaxTBLength;
	}
	/**
	 物理键盘集合
	 */
	private Attrs _HisPhysicsAttrs = null;
	/**
	 物理键盘集合
	 * @throws Exception
	 */
	public final Attrs getHisPhysicsAttrs() {
		if (_HisPhysicsAttrs == null)
		{
			_HisPhysicsAttrs = new Attrs();
			for (Attr attr : this.getAttrs())
			{
				if (attr.getMyFieldType() == FieldType.NormalVirtual || attr.getMyFieldType() == FieldType.RefText)
				{
					continue;
				}
				_HisPhysicsAttrs.Add(attr, false, this.IsAddRefName);
			}
		}
		return _HisPhysicsAttrs;
	}
	/**
	 他的外键集合
	 */
	private Attrs _HisFKAttrs = null;
	/**
	 他的外键集合
	 * @throws Exception
	 */
	public final Attrs getHisFKAttrs() throws Exception
	{
		if (_HisFKAttrs == null)
		{
			_HisFKAttrs = new Attrs();
			for (Attr attr : this.getAttrs())
			{
				if (attr.getMyFieldType() == FieldType.FK || attr.getMyFieldType() == FieldType.PKFK)
				{
					_HisFKAttrs.Add(attr, false, false);
				}
			}
		}
		return _HisFKAttrs;
	}
	public BPEntityAthType HisBPEntityAthType = BPEntityAthType.None;
	/**
	 附件存储位置
	 */
	public String FJSavePath = null;
	/**
	 移动到显示方式
	 */
	public String TitleExt = null;
	/**
	 是否加入相关联的名称
	 AttrKey -  AttrKeyName
	 */
	public boolean IsAddRefName = false;
	/**
	 他的外键Enum集合
	 */
	private Attrs _HisEnumAttrs = null;
	/**
	 他的外键Enum集合
	 * @throws Exception
	 */
	public final Attrs getHisEnumAttrs() throws Exception
	{
		if (_HisEnumAttrs == null)
		{
			_HisEnumAttrs = new Attrs();
			for (Attr attr : this.getAttrs())
			{
				if (attr.getMyFieldType() == FieldType.Enum || attr.getMyFieldType() == FieldType.PKEnum)
				{
					_HisEnumAttrs.Add(attr, true, false);
				}
			}
		}
		return _HisEnumAttrs;
	}
	/**
	 他的外键EnumandPk集合
	 */
	private Attrs _HisFKEnumAttrs = null;
	/**
	 他的外键EnumandPk集合
	 * @throws Exception
	 */
	public final Attrs getHisFKEnumAttrs() throws Exception
	{
		if (_HisFKEnumAttrs == null)
		{
			_HisFKEnumAttrs = new Attrs();
			for (Attr attr : this.getAttrs())
			{
				if (attr.getMyFieldType() == FieldType.Enum || attr.getMyFieldType() == FieldType.PKEnum || attr.getMyFieldType() == FieldType.FK || attr.getMyFieldType() == FieldType.PKFK)
				{
					_HisFKEnumAttrs.Add(attr);
				}
			}
		}
		return _HisFKEnumAttrs;
	}

	///


	///他的实体配置信息
	private Attrs _HisCfgAttrs = null;
	public final Attrs getHisCfgAttrs() throws Exception {
		if (this._HisCfgAttrs == null)
		{
			this._HisCfgAttrs = new Attrs();
			if (WebUser.getNo().equals("admin") == true)
			{

				this._HisCfgAttrs.AddDDLSysEnum("UIRowStyleGlo", 2, "表格数据行风格(应用全局)", true, false, "UIRowStyleGlo", "@0=无风格@1=交替风格@2=鼠标移动@3=交替并鼠标移动");

				this._HisCfgAttrs.AddBoolen("IsEnableDouclickGlo", true, "是否启动双击打开(应用全局)");

				this._HisCfgAttrs.AddBoolen("IsEnableFocusField", true, "是否启用焦点字段");
				this._HisCfgAttrs.AddTBString("FocusField", null, "焦点字段(用于显示点击打开的列", true, false, 0, 20, 20);
				this._HisCfgAttrs.AddBoolen("IsEnableRefFunc", true, "是否启用相关功能列");
				this._HisCfgAttrs.AddBoolen("IsEnableOpenICON", true, "是否启用打开图标");
				this._HisCfgAttrs.AddDDLSysEnum("MoveToShowWay", 0, "移动到显示方式", true, false, "MoveToShowWay", "@0=不显示@1=下拉列表@2=平铺");
				this._HisCfgAttrs.AddTBString("MoveTo", null, "移动到字段", true, false, 0, 20, 20);
				this._HisCfgAttrs.AddTBInt("WinCardW", 820, "弹出窗口宽度", true, false);
				this._HisCfgAttrs.AddTBInt("WinCardH", 480, "弹出窗口高度", true, false);
				this._HisCfgAttrs.AddDDLSysEnum("EditerType", 0, "大块文本编辑器", true, false, "EditerType", "@0=无@1=sina编辑器@2=FKCEditer@3=KindEditor@4=UEditor");
				this._HisCfgAttrs.AddTBString("ShowColumns", "", "选择列", false, false, 0, 1000, 100); //added by liuxc,2015-8-7,增加选择列存储字段
				//  this._HisCfgAttrs.AddDDLSysEnum("UIRowStyleGlo", 2, "表格数据行风格(应用全局)", true, false, "UIRowStyleGlo", "@0=无风格@1=交替风格@2=鼠标移动@3=交替并鼠标移动");
			}
		}
		return _HisCfgAttrs;
	}

	///


	///他的关连信息.
	private Attrs _HisRefAttrs = null;
	public final Attrs getHisRefAttrs() throws Exception
	{
		if (this._HisRefAttrs == null)
		{
			this._HisRefAttrs = new Attrs();

			for (Attr attr : this.getAttrs())
			{
				if (attr.getMyFieldType() == FieldType.FK || attr.getMyFieldType() == FieldType.PKFK)
				{
					_HisRefAttrs.Add(attr);
				}
			}
		}
		return _HisRefAttrs;
	}

	/**
	 增加一个相关功能

	 param title 标题
	 param classMethodName 连接
	 param icon 图标
	 param tooltip 提示信息
	 param target 连接到
	 param width 宽度
	 param height 高度
	 */
	public final void AddRefMethod(String title, String classMethodName, Attrs attrs, String warning, String icon, String tooltip, String target, int width, int height) throws Exception {
		RefMethod func = new RefMethod();
		func.Title = title;
		func.Warning = warning;
		func.ClassMethodName = classMethodName;
		func.Icon = icon;
		func.ToolTip = tooltip;
		func.Width = width;
		func.Height = height;
		func.setHisAttrs(attrs);
		this.getHisRefMethods().Add(func);
	}
	public final void AddRefMethodOpen()
	{
		RefMethod func = new RefMethod();
		func.Title = "打开";
		func.ClassMethodName = this.toString() + ".DoOpenCard";
		func.Icon = SystemConfig.getCCFlowWebPath() + "WF/Img/Btn/Edit.gif";
		this.getHisRefMethods().Add(func);
	}
	/**
	 增加


	 */
	public final void AddRefMethod(RefMethod rm)  {
		rm.GroupName = this.currGroupMethodName;
		this.getHisRefMethods().Add(rm);
	}

	/**
	 相关功能s
	 */
	private RefMethods _RefMethods = null;
	/**
	 相关功能
	 */
	public final RefMethods getHisRefMethods()
	{
		if (this._RefMethods == null)
		{
			_RefMethods = new RefMethods();
		}
		return _RefMethods;
	}
	/**
	 明细s
	 */
	private EnDtls _Dtls = null;
	/**
	 他的明细
	 */
	public final EnDtls getDtls()
	{
		if (this._Dtls == null)
		{
			_Dtls = new EnDtls();
		}

		return _Dtls;
	}
	public final void setDtls(EnDtls value){
		_Dtls = value;
	}
	/**
	 所有的明细
	 */
	private EnDtls _DtlsAll = null;
	/**
	 所有的明细
	 * @
	 */
	public final EnDtls getDtlsAll() throws Exception {
		if (this._DtlsAll == null)
		{
			_DtlsAll = this.getDtls();

			// 加入他的多选。
			for (AttrOfOneVSM en : this.getAttrsOfOneVSM())
			{
				EnDtl dtl = new EnDtl();
				dtl.Ens=en.getEnsOfMM();
				dtl.RefKey=en.getAttrOfOneInMM();
				//dtl.Desc =en.Desc;
				//dtl.Desc = en.Desc ;
				_DtlsAll.Add(dtl);
			}

		}
		return _DtlsAll;
	}


	/**
	 构造涵数

	 param dburl 数据库连接
	 param physicsTable 物理table.
	 * @
	 */
	public Map(DBUrl dburl, String physicsTable) throws Exception {
		this.setEnDBUrl(dburl);
		this.setPhysicsTable(physicsTable);
	}
	/**
	 构造涵数

	 param physicsTable 物理table
	 * @
	 */
	public Map(String physicsTable) {
		this.setPhysicsTable(physicsTable);
	}
	/**
	 构造涵数

	 param physicsTable 表
	 param enDesc 中文描述
	 * @
	 */
	public Map(String physicsTable, String enDesc)  {
		this.setPhysicsTable(physicsTable);
		this._EnDesc = enDesc;
	}
	/**
	 构造涵数


	 param physicsTable 物理表
	 * @
	 */
	public Map(DBUrlType dburltype, String physicsTable)  {
		this.setEnDBUrl(new DBUrl(dburltype));
		this.setPhysicsTable(physicsTable);
	}
	/**
	 构造涵数
	 */
	public Map()  {
	}


	/**
	 多对多的关联
	 */
	private AttrsOfOneVSM _AttrsOfOneVSM = new AttrsOfOneVSM();
	/**
	 点对多的关联
	 */
	public final AttrsOfOneVSM getAttrsOfOneVSM()
	{
		if (this._AttrsOfOneVSM == null)
			this._AttrsOfOneVSM = new AttrsOfOneVSM();
		this._AttrsOfOneVSM.GroupName = this.currGroupMethodName;
		return this._AttrsOfOneVSM;
	}
	public final void setAttrsOfOneVSM(AttrsOfOneVSM value)
	{
		this._AttrsOfOneVSM = value;
	}
	/**
	 通过多实体的类名称取出他的OneVSM属性.

	 param ensOfMMclassName
	 @return
	 */
	public final AttrOfOneVSM GetAttrOfOneVSM(String ensOfMMclassName)  {
		for (AttrOfOneVSM attr : this.getAttrsOfOneVSM())
		{
			if (attr.getEnsOfMM().toString().equals(ensOfMMclassName))
			{
				return attr;
			}
		}
		throw new RuntimeException("error param:  " + ensOfMMclassName);
	}
	/**
	 文件类型
	 */
	private AdjunctType _AdjunctType = AdjunctType.None;
	/**
	 文件类型
	 */
	public final AdjunctType getAdjunctType()
	{
		return this._AdjunctType;
	}
	public final void setAdjunctType(AdjunctType value)
	{this._AdjunctType = value;
	}
	public String MoveTo = null;
	/**

	 */
	public String IndexField = null;
	/// <summary>
	/// 属性字段
	/// </summary>
	public String ParaFields = null;
	/**
	 实体描述
	 */
	private String _EnDesc = "";
	public final String getEnDesc()
	{
		return this._EnDesc;
	}
	public final void setEnDesc(String value)
	{
		this._EnDesc = value;
	}
	/**
	 是否版本管理 IsAllowRepeatName
	 */
	public boolean ItIsEnableVer = false;
	public boolean ItIsShowSearchKey = true;
	public boolean IsAllowRepeatName = true;
	/**
	 如果是null，就按照通用的查询关键字.
	 如果按照指定的格式查询按照如下格式配置.
	 @名称=No@名称=Name@件号=JianHao
	 */
	public String SearchFields = "";
	/**
	 查询的数值 @年龄=Age@薪水=XinShui
	 */
	public String SearchFieldsOfNum = "";
	/**
	 数值查询范围.
	 */
	public bp.sys.DTSearchWay DTSearchWay = bp.sys.DTSearchWay.None;
	public String DTSearchLabel = "日期从";
	public String DTSearchKey = "";
	/**
	 图片DefaultImageUrl
	 */
	public String Icon = "../Images/En/Default.gif";
	/**
	 实体类型
	 */
	private EnType _EnType = EnType.App;
	/**
	 实体类型 默认为0(用户应用).
	 */
	public final EnType getEnType()
	{
		return this._EnType;
	}
	public final void setEnType(EnType value)
	{this._EnType = value;
	}
	/**
	 为方便java转换设置

	 param val
	 */
	public final void Java_SetEnType11(EnType val)
	{
		this._EnType = val;
	}


	/// 生成属性根据xml.
	private String PKs = "";
	public final void GenerMap(String xml) throws Exception {
		DataSet ds = new DataSet("");
		ds.readXml(xml);
		for (DataTable dt : ds.Tables)
		{
			switch (dt.getTableName())
			{
				case "Base":
					this.DealDT_Base(dt);
					break;
				case "Attr":
					this.DealDT_Attr(dt);
					break;
				case "SearchAttr":
					this.DealDT_SearchAttr(dt);
					break;
				case "Dtl":
					this.DealDT_SearchAttr(dt);
					break;
				case "Dot2Dot":
					this.DealDT_Dot2Dot(dt);
					break;
				default:
					throw new RuntimeException("XML 配置信息错误，没有约定的标记:" + dt.TableName);
			}
		}
		// 检查配置的完整性。

	}

	private void DealDT_Base(DataTable dt) throws Exception {
		if (dt.Rows.size() != 1)
		{
			throw new RuntimeException("基础信息配置错误，不能多于或者少于1行记录。");
		}
		for (DataColumn dc : dt.Columns)
		{
			Object obj = dt.Rows.get(0).getValue(dc.ColumnName);

			if(DataType.IsNullOrEmpty(obj) == true)
				continue;

			String val = obj.toString();

			switch (dc.ColumnName)
			{
				case "EnDesc":
					this.setEnDesc(val);
					break;
				case "Table":
					this.setPhysicsTable(val);
					break;
				case "DBUrl":
					this.setEnDBUrl(new DBUrl(DataType.GetDBUrlByString(val)));
					break;
				case "ICON":
					this.Icon = val;
					break;
				case "CodeStruct":
					this.setCodeStruct(val);
					break;
				case "AdjunctType":
					break;
				case "EnType":
					switch (val) {
						case "Admin":
							this.setEnType(bp.en.EnType.Admin);
							break;
						case "App":
							this.setEnType(bp.en.EnType.App);
							break;
						case "Dot2Dot":
							this.setEnType(bp.en.EnType.Dot2Dot);
							break;
						case "Dtl":
							this.setEnType(bp.en.EnType.Dtl);
							break;
						case "Etc":
							this.setEnType(bp.en.EnType.Etc);
							break;
						case "PowerAble":
							this.setEnType(bp.en.EnType.PowerAble);
							break;
						case "Sys":
							this.setEnType(bp.en.EnType.Sys);
							break;
						case "View":
							this.setEnType(bp.en.EnType.View);
							break;
						case "XML":
							this.setEnType(bp.en.EnType.XML);
							break;
						default:
							throw new RuntimeException("没有约定的标记:EnType =  " + val);
					}
					break;
				case "DepositaryOfEntity":
					switch (val)
					{
						case "Application":
							this.setDepositaryOfEntity(Depositary.Application);
							break;
						case "None":
							this.setDepositaryOfEntity(Depositary.None);
							break;
						case "Session":
							this.setDepositaryOfEntity(Depositary.Application);
							break;
						default:
							throw new RuntimeException("没有约定的标记:DepositaryOfEntity=[" + val + "] 应该选择为,Application, None, Session ");
					}
					break;
				case "DepositaryOfMap":
					switch (val)
					{
						case "Application":
						case "Session":
							this.setDepositaryOfMap(Depositary.Application);
							break;
						case "None":
							this.setDepositaryOfMap(Depositary.None);
							break;
						default:
							throw new RuntimeException("没有约定的标记:DepositaryOfMap=[" + val + "] 应该选择为,Application, None, Session ");
					}
					break;
				case "PKs":
					this.PKs = val;
					break;
				default:
					throw new RuntimeException("基础信息中没有约定的标记:" + val);
			}
		}
	}
	private void DealDT_Attr(DataTable dt) throws Exception {
		for (DataRow dr : dt.Rows)
		{
			Attr attr = new Attr();
			for (DataColumn dc : dt.Columns)
			{
				String val = dr.getValue(dc.ColumnName).toString();
				switch (dc.ColumnName)
				{
					case "Key":
						attr.setKey(val);
						break;
					case "Field":
						attr.setField(val);
						break;
					case "DefVal":
						attr.setDefaultVal(val);
						break;
					case "DT":
						attr.setMyDataType(DataType.GetDataTypeByString(val));
						break;
					case "UIBindKey":
						attr.setUIBindKey(val);
						break;
					case "UIIsReadonly":
						if (val.equals("1") || val.toUpperCase().equals("TRUE"))
						{
							attr.setUIIsReadonly(true);
						}
						else
						{
							attr.setUIIsReadonly(false);
						}
						break;
					case "MinLen":
						attr.setMinLength(Integer.parseInt(val));
						break;
					case "MaxLen":
						attr.setMaxLength(Integer.parseInt(val));
						break;
					case "TBLen":
						attr.setUIWidth(Integer.parseInt(val));
						break;
					default:
						throw new RuntimeException("没有约定的标记:" + val);
				}
			}

			// 判断属性.
			if (attr.getUIBindKey() == null)
			{
				/* 说明没有设置外键或者枚举类型。*/
				//if (attr.getMyDataType()
			}
			else
			{
				if (attr.getUIBindKey().indexOf(".") != -1)
				{
					/*说明它是一个类。*/
					Entities ens = attr.getHisFKEns();
					EntitiesNoName ensNoName = ens instanceof EntitiesNoName ? (EntitiesNoName)ens : null;
					if (ensNoName == null)
					{
						/*没有转换成功的情况。*/
					}
					else
					{
						/*已经转换成功, 说明它是EntityNoName 类型。 */
						if (this.PKs.indexOf(attr.getKey()) != -1)
						{
							/* 如果是一个主键  */
							if (attr.getField().equals(""))
							{
								attr.setField(attr.getKey());
							}
							this.AddDDLEntitiesPK(attr.getKey(), attr.getField(), attr.getDefaultVal().toString(), attr.getDesc(), ensNoName, attr.getUIIsReadonly());
						}
						else
						{
							this.AddDDLEntities(attr.getKey(), attr.getField(), attr.getDefaultVal().toString(), attr.getDesc(), ensNoName, attr.getUIIsReadonly());
						}
					}

				}
				else
				{
				}

			}


		}
	}
	private void DealDT_SearchAttr(DataTable dt)
	{
	}
	private void DealDT_Dtl(DataTable dt)
	{
	}
	private void DealDT_Dot2Dot(DataTable dt)
	{
	}


	/**
	 生成字串的字段的长度。
	 *//*
	private int _GenerNoLength = 0;
	public final int getGenerNoLength()
	{
		if (this._GenerNoLength == 0)
		{
			return 3;
			//throw new RuntimeException("@没有指定生成字串的字段长度。");
		}
		return this._GenerNoLength;
	}
	public final void setGenerNoLength(int value)
	{
		this._GenerNoLength = value;
	}*/
	/**
	 编码结构
	 例如： 0， 2322;
	 */
	private String _CodeStruct = "2";
	/**
	 编码的结构
	 */
	public final String getCodeStruct()
	{
		return this._CodeStruct;
	}
	public final void setCodeStruct(String value)
	{
		String str = value;
		if (str.contains("{LSH4}"))
			str = "4";
		if (str.contains("{LSH3}"))
			str = "3";
		if (str.contains("{LSH2}"))
			str = "2";
		this._CodeStruct = str;
		this.setItIsAutoGenerNo(true);
	}
	/**
	 设置编码规则，为方便java转换使用.

	 param val
	 * @
	 */
	public final void Java_SetCodeStruct(String val)
	{
		setCodeStruct(val);
	}
	/**
	 编号的总长度。
	 */
	public final int getCodeLength()
	{
		int i = 0;
		if (getCodeStruct().length() == 0)
		{
			i = Integer.parseInt(this.getCodeStruct());
		}
		else
		{
			char[] s = this.getCodeStruct().toCharArray();
			for (char c : s)
			{
				i = i + Integer.parseInt(String.valueOf(c));
			}
		}

		return i;
	}
	/**
	 是否允许重复的名称(默认不允许重复。)
	 */
	private boolean _IsAllowRepeatName = true;
	/**
	 是否允许重复的名称.
	 在insert，update 前检查。
	 */
	public final boolean getIsAllowRepeatName()
	{
		return _IsAllowRepeatName;
	}
	public final void setIsAllowRepeatName(boolean value)
	{
		_IsAllowRepeatName = value;
	}
	/**
	 是否自动编号
	 */
	private boolean _IsAutoGenerNo = false;
	/**
	 是否自动编号.
	 */
	public final boolean getItIsAutoGenerNo()
	{
		return true;//_IsAutoGenerNo;
	}
	public final void setItIsAutoGenerNo(boolean value)
	{
		_IsAutoGenerNo = value;
	}
	/**
	 是否检查编号长度。（默认的false）
	 */
	private boolean _IsCheckNoLength = false;
	/**
	 是否检查编号长度.
	 在insert 前检查。
	 */
	public final boolean getIsCheckNoLength()
	{
		return _IsCheckNoLength;
	}
	public final void setIsCheckNoLength(boolean value)
	{
		_IsCheckNoLength = value;
	}


	private DBUrl _EnDBUrl = null;
	/**
	 数据库连接
	 */
	public final DBUrl getEnDBUrl()
	{
		if (this._EnDBUrl == null)
		{
			_EnDBUrl = new DBUrl();
		}
		return this._EnDBUrl;
	}
	public final void setEnDBUrl(DBUrl value)
	{this._EnDBUrl = value;
	}
	private String _PhysicsTable = null;
	/**
	 是否是视图
	 * @
	 */
	@JsonIgnore
	public final boolean getIsView() {
		return DBAccess.IsView(this.getPhysicsTableExt(), this.getEnDBUrl().getDBType());
	}

	public final String getPhysicsTableExt()
	{
		if(DataType.IsNullOrEmpty(this.getPhysicsTable()))
			return this.getPhysicsTable();
		if (this.getPhysicsTable().indexOf(".") != -1)
		{
			String[] str = this.getPhysicsTable().split("[.]", -1);
			return str[1];
		}
		else
		{
			return this.getPhysicsTable();
		}
	}
	/**
	 物理表名称

	 @return Table name
	 */
	public final String getPhysicsTable()
	{
		return this._PhysicsTable;

	}
	public final void setPhysicsTable(String value)
	{
		// 因为组成的select 语句放入了内存,修改它的时间也要修改内存的数据。

		Cache.RemoveObj(this.toString() + "SQL", Depositary.Application);
		Cache.RemoveObj("MapOf" + this.toString(), this.getDepositaryOfMap()); // RemoveObj

		this._PhysicsTable = value;
	}


	private Attrs _attrs = null;
	public final Attrs getAttrs()
	{
		if (this._attrs == null)
		{
			this._attrs = new Attrs();
		}
		return this._attrs;
	}
	public final void setAttrs(Attrs value) throws Exception {
		if (this._attrs == null) {
			this._attrs = new Attrs();
		}

		Attrs myattrs = value;
		for (Attr item : myattrs) {
			this._attrs.Add(item);
		}
	}


	public final void AddBoolean(String key, boolean defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, boolean isLine, String helpUrl)  {
		AddBoolean(key, key, defaultVal, desc, isUIVisable, isUIEnable, isLine, null);
	}
	public final void AddBoolean(String key, String field, boolean defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, boolean isLine)  {
		AddBoolean(key, field, defaultVal, desc, isUIVisable, isUIEnable, isLine, null);
	}
	/**
	 增加与boolen 有关系的操作.

	 param key key
	 param field field
	 param defaultVal defaultVal
	 param desc desc
	 param isUIEnable isUIEnable
	 param isUIVisable isUIVisable
	 * @
	 */
	public final void AddBoolean(String key, String field, boolean defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, boolean isLine, String helpUrl) {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.HelperUrl = helpUrl;

		if (defaultVal)
		{
			attr.setDefaultVal(1);
		}
		else
		{
			attr.setDefaultVal(0);
		}

		attr.setMyDataType(DataType.AppBoolean);
		attr.setDesc(desc);
		attr.setUIContralType(UIContralType.CheckBok);

		attr.setUIIsReadonly(isUIEnable?false:true);

		attr.setUIVisible(isUIVisable);
		attr.UIIsLine=isLine;
		this.getAttrs().Add(attr);
	}
	/**
	 增加与boolen 有关系的操作.

	 param key key

	 param defaultVal defaultVal
	 param desc desc
	 param isUIEnable isUIEnable
	 param isUIVisable isUIVisable
	 * @
	 */
	public final void AddBoolean(String key, boolean defaultVal, String desc, boolean isUIVisable, boolean isUIEnable)  {
		AddBoolean(key, key, defaultVal, desc, isUIVisable, isUIEnable, false);
	}

	/**
	 增加与boolen 有关系的操作.

	 param key key

	 param defaultVal defaultVal
	 param desc desc
	 param isUIEnable isUIEnable
	 param isUIVisable isUIVisable
	 * @
	 */
	public final void AddBoolean(String key, boolean defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, boolean isLine) {
		AddBoolean(key, key, defaultVal, desc, isUIVisable, isUIEnable, isLine);
	}





	///于帮定自定义,枚举类型有关系的操作。
	public final void AddDDLSysEnumPK(String key, String field, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, String sysEnumKey)  {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppInt);
		attr.setMyFieldType(FieldType.PKEnum);
		attr.setDesc(desc);
		attr.setUIContralType(UIContralType.DDL);
		attr.setUIBindKey(sysEnumKey);
		attr.setUIVisible(isUIVisable);
		attr.setUIIsReadonly(!isUIEnable);
		this.getAttrs().Add(attr);
	}
	public final void AddDDLSysEnum(String key, String field, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, String sysEnumKey, String cfgVal, boolean isLine) {
		AddDDLSysEnum(key, field, defaultVal, desc, isUIVisable, isUIEnable, sysEnumKey, cfgVal, isLine, null);
	}
	/**
	 自定义枚举类型

	 param key 键
	 param field 字段
	 param defaultVal 默认
	 param desc 描述
	 param sysEnumKey Key
	 * @
	 */
	public final void AddDDLSysEnum(String key, String field, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, String sysEnumKey, String cfgVal, boolean isLine, String helpUrl)  {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.HelperUrl = helpUrl;
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppInt);
		attr.setMyFieldType(FieldType.Enum);
		attr.setDesc(desc);
		attr.setUIContralType(UIContralType.DDL);
		attr.setUIBindKey(sysEnumKey);
		attr.UITag = cfgVal;
		attr.setUIVisible(isUIVisable);
		attr.setUIIsReadonly(!isUIEnable);
		attr.UIIsLine = isLine;
		this.getAttrs().Add(attr);
	}
	/**
	 自定义枚举类型

	 param key 键
	 param defaultVal 默认
	 param desc 描述
	 param sysEnumKey Key
	 * @
	 */
	public final void AddDDLSysEnum(String key, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, String sysEnumKey)  {
		AddDDLSysEnum(key, key, defaultVal, desc, isUIVisable, isUIEnable, sysEnumKey, null, false);
	}
	public final void AddDDLSysEnum(String key, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, String sysEnumKey, String cfgVal, boolean isLine) {
		AddDDLSysEnum(key, key, defaultVal, desc, isUIVisable, isUIEnable, sysEnumKey, cfgVal, isLine);
	}
	public final void AddDDLSysEnum(String key, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, String sysEnumKey, String cfgVal) {
		AddDDLSysEnum(key, key, defaultVal, desc, isUIVisable, isUIEnable, sysEnumKey, cfgVal, false);
	}
	public final void AddDDLSysEnum(String key, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable){
		AddDDLSysEnum(key, key, defaultVal, desc, isUIVisable, isUIEnable, key, null, false);
	}
	///
	///于帮定自定义,枚举类型有关系的操作。
	/**
	 自定义枚举类型

	 param key 键
	 param field 字段
	 param defaultVal 默认
	 param desc 描述
	 param sysEnumKey Key
	 * @
	 */
	public final void AddRadioBtnSysEnum(String key, String field, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, String sysEnumKey)  {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppInt);
		attr.setMyFieldType(FieldType.Enum);
		attr.setDesc(desc);
		attr.setUIContralType(UIContralType.RadioBtn);
		attr.setUIBindKey(sysEnumKey);
		attr.setUIVisible(isUIVisable);
		attr.setUIIsReadonly(!isUIEnable);
		this.getAttrs().Add(attr);
	}
	/**
	 自定义枚举类型

	 param key 键
	 param defaultVal 默认
	 param desc 描述
	 param sysEnumKey Key
	 * @
	 */
	public final void AddRadioBtnSysEnum(String key, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, String sysEnumKey) {
		AddDDLSysEnum(key, key, defaultVal, desc, isUIVisable, isUIEnable, sysEnumKey, null, false);
	}

	///



	///DDLSQL

	public final void AddDDLSQL(String key, Object defaultVal, String desc, String sql)  {
		AddDDLSQL(key, defaultVal, desc, sql, true);
	}


	public final void AddDDLSQL(String key, Object defaultVal, String desc, String sql, boolean uiIsEnable)  {
		if (defaultVal == null)
		{
			defaultVal = "";
		}

		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(key);

		if (Integer.class == defaultVal.getClass())
		{
			attr.setDefaultVal(defaultVal);
			attr.setMyDataType(DataType.AppInt);
		}
		else
		{
			attr.setDefaultVal(defaultVal);
			attr.setMyDataType(DataType.AppString);
		}

		attr.setMyFieldType(FieldType.Normal);
		attr.setMaxLength(50);

		attr.setDesc(desc);
		attr.setUIContralType(UIContralType.DDL);
		attr.setUIBindKey(sql);
		//attr.setHisFKEns(null);
		attr.setUIIsReadonly(!uiIsEnable);
		this.getAttrs().Add(attr);


		//他的名称列.
		attr = new Attr();
		attr.setKey(key + "Text");
		attr.setField(key + "Text");
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppString);
		attr.setMyFieldType(FieldType.Normal);
		attr.setMaxLength(200); //最大长度 @李国文
		attr.setDesc(desc);
		attr.setUIContralType(UIContralType.TB);
		//	attr.setUIBindKey( sql;
		attr.setUIIsReadonly(true);
		attr.setUIVisible(false);
		this.getAttrs().Add(attr);
	}

	/// DDLSQL



	///与实体由关系的操作。


	///entityNoName
	public final void AddDDLEntities(String key, String defaultVal, String desc, EntitiesTree ens, boolean uiIsEnable)  {
		this.AddDDLEntities(key, key, defaultVal, DataType.AppString, desc, ens, "No", "Name", uiIsEnable);
	}
	public final void AddDDLEntities(String key, String defaultVal, String desc, EntitiesNoName ens, boolean uiIsEnable) {
		this.AddDDLEntities(key, key, defaultVal, DataType.AppString, desc, ens, "No", "Name", uiIsEnable);
	}
	public final void AddDDLEntities(String key, String field, String defaultVal, String desc, EntitiesNoName ens, boolean uiIsEnable) {
		this.AddDDLEntities(key, field, defaultVal, DataType.AppString, desc, ens, "No", "Name", uiIsEnable);
	}

	///


	///EntitiesOIDName
	public final void AddDDLEntities(String key, int defaultVal, String desc, EntitiesOIDName ens, boolean uiIsEnable)  {
		this.AddDDLEntities(key, key, defaultVal, DataType.AppInt, desc, ens, "OID", "Name", uiIsEnable);
	}
	public final void AddDDLEntities(String key, String field, Object defaultVal, String desc, EntitiesOIDName ens, boolean uiIsEnable) {
		this.AddDDLEntities(key, field, defaultVal, DataType.AppInt, desc, ens, "OID", "Name", uiIsEnable);
	}

	///

	/**
	 于实体有关系的操作。

	 param key 健值
	 param field 字段
	 param defaultVal 默认值
	 param dataType DataType类型
	 param desc 描述
	 param ens 实体集合
	 param refKey 关联的建
	 param refText 关联的Text
	 * @throws Exception
	 */
	private void AddDDLEntities(String key, String field, Object defaultVal, int dataType, FieldType _fildType, String desc, Entities ens, String refKey, String refText, boolean uiIsEnable)  {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(dataType);
		attr.setMyFieldType(_fildType);
		attr.setMaxLength(50);

		attr.setDesc(desc);
		attr.setUIContralType(UIContralType.DDL);
		attr.setUIBindKey(ens.toString());
		// attr.getUIBindKey()OfEn = ens.getNewEntity().ToString();

		attr.setHisFKEns(ens);


		attr.setHisFKEns(ens);
		attr.setUIRefKeyText(refText);
		attr.setUIRefKeyValue(refKey);
		attr.setUIIsReadonly(!uiIsEnable);

		this.getAttrs().Add(attr, true, this.IsAddRefName);
	}
	public final void AddDDLEntities(String key, String field, Object defaultVal, int dataType, String desc, Entities ens, String refKey, String refText, boolean uiIsEnable) {
		AddDDLEntities(key, field, defaultVal, dataType, FieldType.FK, desc, ens, refKey, refText, uiIsEnable);
	}
	/**
	 于实体有关系的操作。字段与属性名称相同。

	 param key 健值

	 param defaultVal 默认值
	 param dataType DataType类型
	 param desc 描述
	 param ens 实体集合
	 param refKey 关联的建
	 param refText 关联的Text
	 * @throws Exception
	 */
	public final void AddDDLEntities(String key, Object defaultVal, int dataType, String desc, Entities ens, String refKey, String refText, boolean uiIsEnable) {
		AddDDLEntities(key, key, defaultVal, dataType, desc, ens, refKey, refText, uiIsEnable);
	}
	public final void AddDDLEntitiesPK(String key, Object defaultVal, int dataType, String desc, EntitiesTree ens, boolean uiIsEnable){
		AddDDLEntities(key, key, defaultVal, dataType, FieldType.PKFK, desc, ens, "No", "Name", uiIsEnable);
	}
	public final void AddDDLEntitiesPK(String key, Object defaultVal, int dataType, String desc, Entities ens, String refKey, String refText, boolean uiIsEnable){
		AddDDLEntities(key, key, defaultVal, dataType, FieldType.PKFK, desc, ens, refKey, refText, uiIsEnable);
	}
	public final void AddDDLEntitiesPK(String key, String field, Object defaultVal, int dataType, String desc, Entities ens, String refKey, String refText, boolean uiIsEnable)  {
		AddDDLEntities(key, field, defaultVal, dataType, FieldType.PKFK, desc, ens, refKey, refText, uiIsEnable);
	}


	///关于EntitiesNoName 有关系的操作。
	/**
	 关于EntitiesNoName 有关系的操作

	 param key
	 param field
	 param defaultVal
	 param desc
	 param ens
	 param uiIsEnable
	 * @
	 */
	public final void AddDDLEntitiesPK(String key, String field, String defaultVal, String desc, EntitiesTree ens, boolean uiIsEnable) {
		AddDDLEntities(key, field, (Object)defaultVal, DataType.AppString, FieldType.PKFK, desc, ens, "No", "Name", uiIsEnable);
	}
	public final void AddDDLEntitiesPK(String key, String field, String defaultVal, String desc, EntitiesNoName ens, boolean uiIsEnable) {
		AddDDLEntities(key, field, (Object)defaultVal, DataType.AppString, FieldType.PKFK, desc, ens, "No", "Name", uiIsEnable);
	}
	public final void AddDDLEntitiesPK(String key, String defaultVal, String desc, EntitiesNoName ens, boolean uiIsEnable) {
		AddDDLEntitiesPK(key, key, defaultVal, desc, ens, uiIsEnable);
	}
	public final void AddDDLEntitiesPK(String key, String defaultVal, String desc, EntitiesTree ens, boolean uiIsEnable)  {
		AddDDLEntitiesPK(key, key, defaultVal, desc, ens, uiIsEnable);
	}

	///


	///







	///


	///TB


	///String 有关系的操作。


	///关于
	protected final void AddTBString(String key, String field, Object defaultVal, FieldType _FieldType, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith, boolean isUILine)  {
		AddTBString(key, field, defaultVal, _FieldType, desc, uiVisable, isReadonly, minLength, maxLength, tbWith, isUILine, null);
	}
	protected final void AddTBString(String key, String field, Object defaultVal, FieldType _FieldType, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith, boolean isUILine, String helpUrl)  {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.HelperUrl = helpUrl;

		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppString);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIWidth(tbWith);
		attr.setUIIsReadonly(isReadonly);
		attr.setMaxLength(maxLength);
		attr.setMinLength(minLength);
		attr.setMyFieldType(_FieldType);
		attr.UIIsLine=isUILine;
		this.getAttrs().Add(attr);
	}

	///


	///公共的。
	/**
	 同步两个实体属性.
	 * @
	 */

	public final void AddAttrsFromMapData() throws Exception {
		AddAttrsFromMapData(null);
	}


	public final void AddAttrsFromMapData(String FK_MapData) throws Exception {
		if (DataType.IsNullOrEmpty(this.getFK_MapData()) && DataType.IsNullOrEmpty(FK_MapData))
		{
			throw new RuntimeException("@您没有为map的 FK_MapData 赋值.");
		}
		if (DataType.IsNullOrEmpty(FK_MapData) == false)
		{
			this.setFK_MapData(FK_MapData);
		}
		MapData md = null;
		md = new MapData();
		md.setNo(this.getFK_MapData());
		if (md.RetrieveFromDBSources() == 0)
		{
			md.setName(this.getFK_MapData());
			md.setPTable(this.getPhysicsTable());
			md.setEnPK(this.PKs);
			md.Insert();
			md.RepairMap();
		}
		md.Retrieve();
		MapAttrs attrs = new MapAttrs(this.getFK_MapData());

		/*把 手工编写的attr 放入 mapattrs里面去. */
		for (Attr attr : this.getAttrs())
		{
			if (attrs.contains(MapAttrAttr.KeyOfEn, attr.getKey()))
			{
				continue;
			}

			if (attr.getItIsRefAttr())
			{
				continue;
			}

			//把文件实体类的属性放入关系实体类中去。
			MapAttr mapattrN = attr.getToMapAttr();
			mapattrN.setFrmID(this.getFK_MapData());
			if (mapattrN.getUIHeight() == 0)
			{
				mapattrN.setUIHeight(23);
			}
			mapattrN.Insert();
			attrs.AddEntity(mapattrN);
		}

		//把关系实体类的属性放入文件实体类中去。
		for (MapAttr attr : attrs.ToJavaList())
		{
			if (this.getAttrs().contains(attr.getKeyOfEn()) == true)
			{
				continue;
			}
			this.AddAttr(attr.getHisAttr());
		}
	}
	public final void AddAttrs(Attrs attrs) throws Exception {
		AddAttrs(attrs,false);
	}
	public final void AddAttrs(Attrs attrs,boolean isClearGroupName) {
		for (Attr attr : attrs)
		{
			if (attr.getItIsRefAttr())
			{
				continue;
			}
			this.getAttrs().Add(attr,isClearGroupName);
		}
	}
	public final void AddAttr(Attr attr)  {
		this.getAttrs().Add(attr);
	}
	public final void AddAttr(String key, Object defaultVal, int dbtype, boolean isPk, String desc) throws Exception {
		if (isPk)
		{
			AddTBStringPK(key, key, desc, true, false, 0, 1000, 100);
		}
		else
		{
			AddTBString(key, key, defaultVal.toString(), FieldType.Normal, desc, true, false, 0, 1000, 100, false);
		}
	}
	/**
	 增加一个textbox 类型的属性。

	 param key 健值

	 param defaultVal 默认值

	 param desc 描述
	 param uiVisable 是不是可见
	 param uiVisable 是不是只读
	 param minLength 最小长度
	 param maxLength 最大长度
	 param tbWith 宽度
	 * @
	 */
	public final void AddTBString(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith)  {
		AddTBString(key, key, defaultVal, FieldType.Normal, desc, uiVisable, isReadonly, minLength, maxLength, tbWith, false);
	}
	public final void AddTBString(String key, String field, Object defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith)  {
		AddTBString(key, field, defaultVal, FieldType.Normal, desc, uiVisable, isReadonly, minLength, maxLength, tbWith, false);
	}
	public final void AddTBString(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith, boolean isUILine)  {
		AddTBString(key, key, defaultVal, FieldType.Normal, desc, uiVisable, isReadonly, minLength, maxLength, tbWith, isUILine);
	}
	public final void AddTBString(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith, boolean isUILine, String helpUrl)  {
		AddTBString(key, key, defaultVal, FieldType.Normal, desc, uiVisable, isReadonly, minLength, maxLength, tbWith, isUILine, helpUrl);
	}
	/**
	 附件集合
	 * @
	 */
	public final void AddMyFileS()  {
		this.AddTBInt(EntityNoMyFileAttr.MyFileNum, 0, "附件", false, false);
		this.HisBPEntityAthType = BPEntityAthType.Multi;
	}
	/**
	 附件集合

	 param desc
	 * @
	 */
	public final void AddMyFileS(String desc)  {
		this.AddTBInt(EntityNoMyFileAttr.MyFileNum, 0, desc, false, false);
		this.HisBPEntityAthType = BPEntityAthType.Multi;
	}

	public final void AddMyFile(String fileDesc)  {
		AddMyFile(fileDesc, null, null);
	}

	public final void AddMyFile() throws Exception {
		AddMyFile(null, null, null);
	}



	public final void AddMyFile(String fileDesc, String ext, String savePath)  {
		if (fileDesc == null)
		{
			fileDesc = "附件或图片";
		}

		this.AddTBString(EntityNoMyFileAttr.MyFileName, null, fileDesc, false, false, 0, 300, 200);
		this.AddTBString(EntityNoMyFileAttr.MyFilePath, null, "MyFilePath", false, false, 0, 300, 200);
		this.AddTBString(EntityNoMyFileAttr.MyFileExt, null, "MyFileExt", false, false, 0, 20, 10);
		this.AddTBString(EntityNoMyFileAttr.WebPath, null, "WebPath", false, false, 0, 300, 10);

		this.AddTBInt(EntityNoMyFileAttr.MyFileH, 0, "MyFileH", false, false);
		this.AddTBInt(EntityNoMyFileAttr.MyFileW, 0, "MyFileW", false, false);
		this.AddTBFloat("MyFileSize", 0, "MyFileSize", false, false);

		this.HisBPEntityAthType = BPEntityAthType.Single;
		this.FJSavePath = savePath;
	}
	private AttrFiles _HisAttrFiles = null;
	public final AttrFiles getHisAttrFiles()
	{
		if (_HisAttrFiles == null)
		{
			_HisAttrFiles = new AttrFiles();
		}
		return _HisAttrFiles;
	}
	/**
	 增加一个特定的附件,可以利用它增加多个？
	 比如：增加简历，增加论文。

	 param fileDesc
	 param fExt
	 * @
	 */
	public final void AddMyFile(String fileDesc, String fExt){
		getHisAttrFiles().Add(fExt, fileDesc);
		this.HisBPEntityAthType = BPEntityAthType.Single;

		AddMyFile(fileDesc, fExt, null);
	}

	//#region 字段分组方法.
	public String currGroupAttrName = "基本信息";
	public void AddGroupAttr(String groupName)
	{
		AddGroupAttr(groupName,"");
	}
	public void AddGroupAttr(String groupName,String icon)
	{
		this.currGroupAttrName = groupName;
		this.getAttrs().currGroupAttrName = groupName;
	}
    //#endregion 字段分组方法.

	//#region 方法分组.
	public String currGroupMethodName = "基本信息";
	public void AddGroupMethod(String groupName)
	{
		this.currGroupMethodName = groupName;
	}
   //#endregion 方法分组.

	//#region 属性.
	public void AddDDLStringEnum(String key, String defaultVal, String name, String cfgString, Boolean uiIsEnable) {
		AddDDLStringEnum(key,defaultVal,name,cfgString,uiIsEnable,"",false);
	}
	public void AddDDLStringEnum(String key, String defaultVal, String name, String cfgString, Boolean uiIsEnable, String helpDoc, Boolean isUILine){
		if(DataType.IsNullOrEmpty(helpDoc))	helpDoc = "";
		if(DataType.IsNullOrEmpty(isUILine)) isUILine = false;

		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(key);

		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppString);

		// 设置外部数据源类型字段.
		attr.setMyFieldType(FieldType.Normal);
		attr.setUIContralType(UIContralType.DDL);
		attr.setMaxLength(50);
		attr.setMinLength(0);
		attr.setDesc(name);
		//转化为sql.
		attr.setUIBindKey(bp.difference.Glo.DealSQLStringEnumFormat(cfgString));
		// alert(attr.getUIBindKey());

		attr.setUIIsReadonly(!uiIsEnable);
		attr.HelperUrl = helpDoc;
		attr.UIIsLine = isUILine;
		this.getAttrs().Add(attr);

		//他的名称列.
		Attr attr2 = new Attr();
		attr2.setKey(key + 'T');
		attr2.setField(key + 'T');
		attr2.setDefaultVal(defaultVal);
		attr2.setMyDataType(DataType.AppString);
		attr2.setMyFieldType(FieldType.Normal);
		attr2.setMaxLength(200);
		attr2.setDesc(name);
		attr2.setUIContralType(UIContralType.TB);
		attr2.HelperUrl = helpDoc;
		attr2.UIIsLine = !!isUILine;
		//	attr.setUIBindKey( sql;
		attr2.setUIIsReadonly(true);
		attr2.setUIVisible(false);
		this.getAttrs().Add(attr2);
	}
    //#endregion 枚举属性

	///增加大块文本输入
	public final void AddTBStringDoc()  {
		AddTBStringDoc("Doc", "Doc", null, "内容", true, false, 0, 4000, 10, true);
	}


	public final void AddTBStringDoc(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly, boolean isUILine){
		AddTBStringDoc(key, defaultVal, desc, uiVisable, isReadonly, isUILine, 10);
	}

	//ORIGINAL LINE: public void AddTBStringDoc(String key, String defaultVal, String desc, bool uiVisable, bool isReadonly, bool isUILine, int rows = 10)
	public final void AddTBStringDoc(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly, boolean isUILine, int rows)  {
		AddTBStringDoc(key, key, defaultVal, desc, uiVisable, isReadonly, 0, 4000, rows, isUILine);
	}

	public final void AddTBStringDoc(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		AddTBStringDoc(key, key, defaultVal, desc, uiVisable, isReadonly, 0, 4000, 300, false);
	}
	public final void AddTBStringDoc(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int rows){
		AddTBStringDoc(key, key, defaultVal, desc, uiVisable, isReadonly, minLength, maxLength, rows, false);
	}

	public final void AddTBStringDoc(String key, String field, String defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int rows, boolean isUILine) {
		AddTBStringDoc(key, field, defaultVal, desc, uiVisable, isReadonly, minLength, maxLength, rows, isUILine, false);
	}

	public final void AddTBStringDoc(String key, String field, String defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int rows, boolean isUILine, boolean isRichText)  {


		if (field == null)
		{
			field = key;
		}

		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppString);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIWidth(300);
		attr.setUIIsReadonly(isReadonly);
		attr.setMaxLength(4000);
		attr.setMinLength(minLength);
		attr.setMyFieldType(FieldType.Normal);
		attr.setUIHeight(rows);
		if (isRichText == true)
		{
			attr.setItIsSupperText(1); //是富文本. 都要解析为上下结构.
			isUILine = true; //必须是上下结构.
		}
		else
		{
			attr.setItIsSupperText(0); //不是富文本. 根据 isUILine 解析是否上下结构.
		}
		attr.UIIsLine=isUILine;
		this.getAttrs().Add(attr);
	}

	///


	/// PK
	public final void AddTBStringPK(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith) {
		this.PKs = key;
		AddTBString(key, key, defaultVal, FieldType.PK, desc, uiVisable, isReadonly, minLength, maxLength, tbWith, false);
	}
	public final void AddTBStringPK(String key, String field, Object defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith)  {
		this.PKs = key;
		AddTBString(key, field, defaultVal, FieldType.PK, desc, uiVisable, isReadonly, minLength, maxLength, tbWith, false);
	}

	///


	///PKNo


	///


	/// 外键于 Ens 有关系的操作。
	/**
	 外键于 Ens 有关系的操作。

	 param key 属性
	 param field 字段
	 param defaultVal 默认值
	 param desc 描述
	 param ens 实体
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 param minLength 最小长度
	 param maxLength 最大长度
	 param tbWith 宽度
	 * @
	 */
	public final void AddTBStringFKEns(String key, String field, String defaultVal, String desc, Entities ens, String refKey, String refText, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith) throws Exception {
		Attr attr = new Attr();
		attr.setKey(key);

		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppString);
		attr.setUIBindKey(ens.toString());
		attr.setHisFKEns(ens);
		// attr.getUIBindKey()OfEn = ens.getNewEntity().ToString();
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIWidth(tbWith);
		attr.setUIIsReadonly(isReadonly);
		attr.setMaxLength(maxLength);
		attr.setMinLength(minLength);
		attr.setUIRefKeyValue(refKey);
		attr.setUIRefKeyText(refText);
		attr.setMyFieldType(FieldType.FK);
		this.getAttrs().Add(attr);
	}
	/**
	 外键于 Ens 有关系的操作。

	 param key 属性
	 param defaultVal 默认值
	 param desc 描述
	 param ens 实体
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 param minLength 最小长度
	 param maxLength 最大长度
	 param tbWith 宽度
	 * @
	 */
	public final void AddTBStringFKEns(String key, String defaultVal, String desc, Entities ens, String refKey, String refText, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith) throws Exception {
		this.AddTBStringFKEns(key, key, defaultVal, desc, ens, refKey, refText, uiVisable, isReadonly, minLength, maxLength, tbWith);
	}

	///


	///于多值有关系的操作
	/**
	 于多值有关系的操作

	 param key
	 param field
	 param defaultVal
	 param desc
	 param ens
	 param uiVisable
	 param isReadonly
	 param minLength
	 param maxLength
	 param tbWith
	 * @
	 */
	public final void AddTBMultiValues(String key, String field, Object defaultVal, String desc, Entities ens, String refValue, String refText, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith) throws Exception {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppString);
		attr.setUIBindKey(ens.toString());
		attr.setHisFKEns(ens);

		// attr.getUIBindKey()OfEn = ens.getNewEntity().ToString();

		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIWidth(tbWith);
		attr.setUIIsReadonly(isReadonly);
		attr.setUIRefKeyText(refText);
		attr.setUIRefKeyValue(refValue);
		attr.setMaxLength(maxLength);
		attr.setMinLength(minLength);
		attr.setMyFieldType(FieldType.MultiValues);

		this.getAttrs().Add(attr);
	}

	///


	/// 主键于 Ens 有关系的操作。
	/**
	 外键于 Ens 有关系的操作。
	 主键

	 param key 属性
	 param field 字段
	 param defaultVal 默认值
	 param desc 描述
	 param ens 实体
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 param minLength 最小长度
	 param maxLength 最大长度
	 param tbWith 宽度
	 * @
	 */
	public final void AddTBStringPKEns(String key, String field, Object defaultVal, String desc, Entities ens, String refVal, String refText, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith) throws Exception {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppString);
		attr.setUIBindKey(ens.toString());
		attr.setHisFKEns(attr.getHisFKEns());
		//attr.getUIBindKey()OfEn = ens.getNewEntity().ToString();
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIWidth(tbWith);
		attr.setUIIsReadonly(isReadonly);

		attr.setUIRefKeyText(refText);
		attr.setUIRefKeyValue(refVal);

		attr.setMaxLength(maxLength);
		attr.setMinLength(minLength);
		attr.setMyFieldType(FieldType.PKFK);
		this.getAttrs().Add(attr);
	}
	/**
	 外键于 Ens 有关系的操作。

	 param key 属性
	 param defaultVal 默认值
	 param desc 描述
	 param ens 实体
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 param minLength 最小长度
	 param maxLength 最大长度
	 param tbWith 宽度
	 * @
	 */
	public final void AddTBStringPKEns(String key, String defaultVal, String desc, Entities ens, String refKey, String refText, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith) throws Exception {
		this.AddTBStringPKEns(key, key, defaultVal, desc, ens, refKey, refText, uiVisable, isReadonly, minLength, maxLength, tbWith);
	}

	///


	/// 主键于 DataHelpKey 有关系的操作。
	/**
	 外键于 DataHelpKey 有关系的操作, 用与自定义的右键帮助系统.

	 param key 属性
	 param field 字段
	 param defaultVal 默认值
	 param desc 描述
	 param DataHelpKey 在TB 里定义的右健帮助Key </param>
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 param minLength 最小长度
	 param maxLength 最大长度
	 param tbWith 宽度
	 * @
	 */
	public final void AddTBStringPKSelf(String key, String field, Object defaultVal, String desc, String DataHelpKey, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith) throws Exception {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppString);
		attr.setUIBindKey(DataHelpKey);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIWidth(tbWith);
		attr.setUIIsReadonly(isReadonly);
		attr.setMaxLength(maxLength);
		attr.setMinLength(minLength);
		attr.setMyFieldType(FieldType.PK);
		this.getAttrs().Add(attr);
	}
	/**
	 外键于 Ens 有关系的操作。用与自定义的右键帮助系统.

	 param key 属性
	 param defaultVal 默认值
	 param desc 描述
	 param DataHelpKey 在TB 里定义的右健帮助Key </param>
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 param minLength 最小长度
	 param maxLength 最大长度
	 param tbWith 宽度
	 * @
	 */
	public final void AddTBStringPKSelf(String key, Object defaultVal, String desc, String DataHelpKey, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith) throws Exception {
		this.AddTBStringPKSelf(key, key, defaultVal, desc, DataHelpKey, uiVisable, isReadonly, minLength, maxLength, tbWith);
	}

	///


	/// 外键于 DataHelpKey 有关系的操作。
	/**
	 外键于 DataHelpKey 有关系的操作。用与自定义的右键帮助系统.

	 param key 属性
	 param field 字段
	 param defaultVal 默认值
	 param desc 描述
	 param DataHelpKey 在TB 里定义的右健帮助Key </param>
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 param minLength 最小长度
	 param maxLength 最大长度
	 param tbWith 宽度
	 * @
	 */
	public final void AddTBStringFKSelf(String key, String field, Object defaultVal, String desc, String DataHelpKey, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith) throws Exception {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppString);
		attr.setUIBindKey(DataHelpKey);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIWidth(tbWith);
		attr.setUIIsReadonly(isReadonly);
		attr.setMaxLength(maxLength);
		attr.setMinLength(minLength);
		attr.setMyFieldType(FieldType.Normal);
		this.getAttrs().Add(attr);
	}
	/**
	 外键于 Ens 有关系的操作。用与 Ens 右键帮助系统.

	 param key 属性
	 param defaultVal 默认值
	 param desc 描述
	 param DataHelpKey 在TB 里定义的右健帮助Key </param>
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 param minLength 最小长度
	 param maxLength 最大长度
	 param tbWith 宽度
	 * @
	 */
	public final void AddTBStringFKSelf(String key, Object defaultVal, String desc, String DataHelpKey, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith) throws Exception {
		this.AddTBStringFKSelf(key, key, defaultVal, desc, DataHelpKey, uiVisable, isReadonly, minLength, maxLength, tbWith);
	}

	///


	/// 增加外键植
	public final void AddTBStringFKValue(String refKey, String key, String desc, boolean IsVisable, int with)
	{

	}
	///日期类型
	public final void AddTBDate(String key) throws Exception {
		switch (key)
		{
			case "RDT":
				AddTBDate("RDT", "记录日期", true, true);
				break;
			case "UDT":
				AddTBDate("UDT", "更新日期", true, true);
				break;
			default:
				AddTBDate(key, key, true, true);
				break;
		}
	}
	/**
	 增加日期类型的控健

	 param key 健值
	 param defaultVal 默认值
	 param desc 描述
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 * @
	 */
	public final void AddTBDate(String key, String field, String defaultVal, String desc, boolean uiVisable, boolean isReadonly) {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppDate);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIIsReadonly(isReadonly);
		attr.setMaxLength(50);
		this.getAttrs().Add(attr);
	}
	/**
	 增加日期类型的控健

	 param key key
	 param defaultVal defaultVal/如果你想用当天的信息,请选择后面的方法加入
	 param desc desc
	 param uiVisable uiVisable
	 param isReadonly isReadonly
	 * @
	 */
	public final void AddTBDate(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		AddTBDate(key, key, defaultVal, desc, uiVisable, isReadonly);
	}
	/**
	 增加日期类型的控健(默认日期是当前日期)

	 param key key
	 param desc desc
	 param uiVisable uiVisable
	 param isReadonly isReadonly
	 * @
	 */
	public final void AddTBDate(String key, String desc, boolean uiVisable, boolean isReadonly) {
		AddTBDate(key, key, DataType.getCurrentDateByFormart(DataType.getSysDateFormat()), desc, uiVisable, isReadonly);
	}
	///日期时间类型。
	/**
	 增加日期类型的控健

	 param key 健值
	 param defaultVal 默认值
	 param desc 描述
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 * @
	 */
	public final void AddTBDateTime(String key, String field, String defaultVal, String desc, boolean uiVisable, boolean isReadonly) {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppDateTime);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIIsReadonly(isReadonly);
		attr.setMaxLength(50);
		attr.setUIWidth(100);
		this.getAttrs().Add(attr);
	}
	public final void AddTBDateTime(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		this.AddTBDateTime(key, key, defaultVal, desc, uiVisable, isReadonly);
	}
	public final void AddTBDateTime(String key, String desc, boolean uiVisable, boolean isReadonly)  {
		this.AddTBDateTime(key, key, DataType.getCurrentDateByFormart(DataType.getSysDateTimeFormat()), desc, uiVisable, isReadonly);
	}
	///资金类型
	public final void AddTBMoney(String key, String field, float defaultVal, String desc, boolean uiVisable, boolean isReadonly) throws Exception {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppMoney);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIIsReadonly(isReadonly);
		this.getAttrs().Add(attr);
	}
	public final void AddTBMoney(String key, float defaultVal, String desc, boolean uiVisable, boolean isReadonly) throws Exception {
		this.AddTBMoney(key, key, defaultVal, desc, uiVisable, isReadonly);
	}
	/**
	 增加一个普通的类型。

	 param key 键
	 param _Field 字段
	 param defaultVal 默认值
	 param desc 描述
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 * @
	 */
	public final void AddTBInt(String key, String _Field, int defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(_Field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppInt);
		attr.setMyFieldType(FieldType.Normal);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIIsReadonly(isReadonly);
		this.getAttrs().Add(attr);
	}
	/**
	 增加一个普通的类型。字段值与属性相同。

	 param key 键
	 param defaultVal 默认值
	 param desc 描述
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 * @
	 */
	public final void AddTBInt(String key, int defaultVal, String desc, boolean uiVisable, boolean isReadonly){
		this.AddTBInt(key, key, defaultVal, desc, uiVisable, isReadonly);
	}
	/**
	 增加一个PK的类型。
	 param key 键
	 param _Field 字段
	 param defaultVal 默认值
	 param desc 描述
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 * @
	 */
	public final void AddTBIntPK(String key, String _Field, int defaultVal, String desc, boolean uiVisable, boolean isReadonly, boolean identityKey){
		this.PKs = key;
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(_Field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppInt);
		attr.setMyFieldType(FieldType.PK);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIIsReadonly(isReadonly);
		if (identityKey)
		{
			attr.setUIBindKey("1"); //特殊标记此值，让它可以自动生成自增长的列.
		}
		this.getAttrs().Add(attr);
	}
	/**
	 增加一个PK的类型。字段值与属性相同。
	 param _field 键
	 param desc 描述
	 * @
	 */
	public final void AddTBIntPKOID(String _field, String desc){
		this.AddTBIntPK("OID", _field, 0, "OID", false, true, false);
	}
	public final void AddTBIntPKOID()  {
		this.AddTBIntPKOID("OID", "OID");
	}

	public void AddLang()  {
		this.AddTBString("NameFT", null, "繁体", true, false, 0, 200, 150, false);
		this.AddTBString("NameEn", null, "英文", true, false, 0, 200, 150, false);
		this.AddTBString("NameJP", null, "日文", true, false, 0, 200, 150, false);
	}
	public final void AddTBAtParas() {
		this.AddTBString("AtPara", null, "AtPara", false, true, 0, 4000, 10);
	}
	/**
	 增加  AtParas字段.
	 param fieldLength
	 * @
	 */
	public final void AddTBAtParas(int fieldLength)  {
		this.AddTBString("AtPara", null, "AtPara", false, true, 0, fieldLength, 10);
	}
	/**
	 主键
	 * @
	 */

	public final void AddMyPK() {
		AddMyPK(true);
	}


	public final void AddMyPK(boolean uiVisable)  {
		this.PKs = "MyPK";
		this.AddTBStringPK("MyPK", null, "主键MyPK", uiVisable, true, 1, 150, 10);
	}
	/**
	 增加一个PK的类型。字段值与属性相同。
	 param key 键
	 param defaultVal 默认值
	 param desc 描述
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 * @
	 */
	public final void AddTBIntPK(String key, int defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		this.AddTBIntPK(key, key, defaultVal, desc, uiVisable, isReadonly, false);
	}

	public final void AddTBIntPK(String key, int defaultVal, String desc, boolean uiVisable, boolean isReadonly, boolean identityKey) {
		this.AddTBIntPK(key, key, defaultVal, desc, uiVisable, isReadonly, identityKey);
	}
	public final void AddTBIntMyNum() throws Exception {
		this.AddTBInt("MyNum", "MyNum", 1, "个数", true, true);
	}

	///Float类型
	public final void AddTBFloat(String key, String _Field, float defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(_Field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppFloat);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIIsReadonly(isReadonly);
		this.getAttrs().Add(attr);
	}
	public final void AddTBFloat(String key, float defaultVal, String desc, boolean uiVisable, boolean isReadonly) {
		this.AddTBFloat(key, key, defaultVal, desc, uiVisable, isReadonly);
	}

	///Decimal类型
	public final void AddTBDecimal(String key, String _Field, BigDecimal defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(_Field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppDouble);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIIsReadonly(isReadonly);
		this.getAttrs().Add(attr);
	}
	public final void AddTBDecimal(String key, BigDecimal defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		this.AddTBDecimal(key, key, defaultVal, desc, uiVisable, isReadonly);
	}

	/**
	 增加明细
	 @param ens 子类
	 @param refKey 关联的键值
	 @param groupName 分组名字
	 @param model 模式
	 */
	public final void AddDtl(Entities ens, String refKey, String groupName, DtlEditerModel model)
	{
		AddDtl(ens, refKey, groupName, model, null);
	}

	public final void AddDtl(Entities ens, String refKey, String groupName)
	{
		AddDtl(ens, refKey, groupName, DtlEditerModel.DtlBatch, null);
	}

	public final void AddDtl(Entities ens, String refKey)
	{
		AddDtl(ens, refKey, null, DtlEditerModel.DtlBatch, null);
	}

	public final void AddDtl(Entities ens, String refKey, String groupName, DtlEditerModel model, String icon)
	{
		EnDtl dtl = new EnDtl();
		dtl.Ens = ens;
		dtl.RefKey = refKey;
		dtl.GroupName = this.currGroupMethodName;
		dtl.DtlEditerModel = model;
		dtl.Icon = icon;
		this.getDtls().Add(dtl);
	}

	public final void AddDtl(String url, String refKey, String groupName, DtlEditerModel model, String icon)
	{
		AddDtl(url, refKey, groupName, model, icon, "");
	}

	public final void AddDtl(String url, String refKey, String groupName, DtlEditerModel model)
	{
		AddDtl(url, refKey, groupName, model, null, "");
	}

	public final void AddDtl(String url, String refKey, String groupName)
	{
		AddDtl(url, refKey, groupName, DtlEditerModel.DtlBatch, null, "");
	}

	public final void AddDtl(String url, String refKey)
	{
		AddDtl(url, refKey, null, DtlEditerModel.DtlBatch, null, "");
	}

	public final void AddDtl(String url, String refKey, String groupName, DtlEditerModel model, String icon, String desc)
	{
		EnDtl dtl = new EnDtl();
		dtl.UrlExt = url;
		dtl.RefKey=refKey;
		dtl.GroupName = this.currGroupMethodName;
		dtl.setDesc(desc);
		dtl.DtlEditerModel=model;
		dtl.Icon = icon;
		this.getDtls().Add(dtl);
	}
}
