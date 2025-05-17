package bp.sys;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.difference.*;
import net.sf.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 扩展
*/
public class MapExt extends EntityMyPK
{

		///#region 关于 Pop at 参数
	/**
	 转化JSON

	 @return
	*/
	public final String PopValToJson() {
		return bp.tools.Json.ToJsonEntityModel(this.PopValToHashtable());
	}
	public final Hashtable PopValToHashtable()  {

		//创建一个ht, 然后把他转化成json返回出去。
		Hashtable ht = new Hashtable();

		switch (this.getPopValWorkModel())
		{
			case SelfUrl:
				ht.put("URL", this.getPopValUrl());
				break;
			case TableOnly:
				ht.put("EntitySQL", this.getPopValEntitySQL());
				break;
			case TablePage:
				ht.put("PopValTablePageSQL", this.getPopValTablePageSQL());
				ht.put("PopValTablePageSQLCount", this.getPopValTablePageSQLCount());
				break;
			case Group:
				ht.put("GroupSQL", this.getTag1());
				ht.put("EntitySQL", this.getPopValEntitySQL());
				break;
			case Tree:
				ht.put("TreeSQL", this.getPopValTreeSQL());
				ht.put("TreeParentNo", this.getPopValTreeParentNo());
				break;
			case TreeDouble:
				ht.put("DoubleTreeSQL", this.getPopValTreeSQL());
				ht.put("DoubleTreeParentNo", this.getPopValTreeParentNo());
				ht.put("DoubleTreeEntitySQL", this.getPopValDoubleTreeEntitySQL());
				break;
			default:
				break;
		}

		ht.put(MapExtAttr.W, this.getW());
		ht.put(MapExtAttr.H, this.getH());

		ht.put("PopValWorkModel", this.getPopValWorkModel().toString()); //工作模式.
		ht.put("PopValSelectModel", this.getPopValSelectModel().toString()); //单选，多选.

		ht.put("PopValFormat", this.getPopValFormat().toString()); //返回值格式.
		ht.put("PopValTitle", this.getPopValTitle()); //窗口标题.
		ht.put("PopValColNames", this.getPopValColNames()); //列名 @No=编号@Name=名称@Addr=地址.
		ht.put("PopValSearchTip", this.getPopValSearchTip()); //搜索提示..

		//查询条件.
		ht.put("PopValSearchCond", this.getPopValSearchCond()); //查询条件..


		//转化为Json.
		return ht;
	}
	/**
	 连接
	*/
	public final String getPopValUrl() {
		return this.getDoc();
	}
	public final void setPopValUrl(String value) throws Exception {
		this.setDoc(value);
	}
	/**
	 实体SQL
	*/
	public final String getPopValEntitySQL() {
		return this.getTag2();
	}
	public final void setPopValEntitySQL(String value) throws Exception {
		this.setTag2(value);
	}
	/**
	 分组SQL
	*/
	public final String getPopValGroupSQL() {
		return this.getTag1();
	}
	public final void setPopValGroupSQL(String value) throws Exception {
		this.setTag1(value);
	}
	/**
	 分页SQL带有关键字
	*/
	public final String getPopValTablePageSQL() {
		return this.getTag();
	}
	public final void setPopValTablePageSQL(String value) throws Exception {
		this.setTag(value);
	}
	/**
	 分页SQL获取总行数
	*/
	public final String getPopValTablePageSQLCount() {
		return this.getTag1();
	}
	public final void setPopValTablePageSQLCount(String value) throws Exception {
		this.setTag1(value);
	}
	/**
	 标题
	*/
	public final String getPopValTitle() {
		return this.GetParaString("PopValTitle");
	}
	public final void setPopValTitle(String value)  {
		this.SetPara("PopValTitle", value);
	}

	public final String getPopValTreeSQL() {
		return this.getPopValEntitySQL();
	}
	public final void setPopValTreeSQL(String value) throws Exception {
		this.setPopValEntitySQL(value);
	}
	/**
	 根目录
	*/
	public final String getPopValTreeParentNo() {
		return this.GetParaString("PopValTreeParentNo");
	}
	public final void setPopValTreeParentNo(String value)  {
		this.SetPara("PopValTreeParentNo", value);
	}
	/**
	 Pop 返回值的格式.
	*/
	public final PopValFormat getPopValFormat() {
		return PopValFormat.forValue(this.GetParaInt("PopValFormat"));
	}
	public final void setPopValFormat(PopValFormat value)  {
		this.SetPara("PopValFormat", value.getValue());
	}
	/**
	 双实体树的实体
	*/
	public final String getPopValDoubleTreeEntitySQL() {
		return this.getTag1();
	}
	public final void setPopValDoubleTreeEntitySQL(String value) throws Exception {
		this.setTag1(value);
	}
	/**
	 pop 选择方式
	 0,多选,1=单选.
	*/
	public final PopValSelectModel getPopValSelectModel() {
		return PopValSelectModel.forValue(this.GetParaInt("PopValSelectModel"));
	}
	public final void setPopValSelectModel(PopValSelectModel value)  {
		this.SetPara("PopValSelectModel", value.getValue());
	}
	/**
	 PopVal工作模式
	*/
	public final PopValWorkModel getPopValWorkModel() {
		return PopValWorkModel.forValue(this.GetParaInt("PopValWorkModel"));
	}
	public final void setPopValWorkModel(PopValWorkModel value)  {
		this.SetPara("PopValWorkModel", value.getValue());
	}
	/**
	 开窗的列中文名称.
	*/
	public final String getPopValColNames() {
		return this.getTag3();
	}
	public final void setPopValColNames(String value) throws Exception {
		this.setTag3(value);
	}
	/**
	 查询条件
	*/
	public final String getPopValSearchCond() {
		return this.getTag4();
	}
	public final void setPopValSearchCond(String value) throws Exception {
		this.setTag4(value);
	}
	/**
	 搜索提示关键字
	*/
	public final String getPopValSearchTip() {
		return this.GetParaString("PopValSearchTip", "请输入关键字");
	}
	public final void setPopValSearchTip(String value)  {
		this.SetPara("PopValSearchTip", value);
	}
	/**
	 数据源
	*/
	public final String getDBSrcNo()  {
		return this.GetValStrByKey(MapExtAttr.FK_DBSrc);
	}
	public final void setDBSrcNo(String value){
		this.SetValByKey(MapExtAttr.FK_DBSrc, value);
	}

		///#endregion


		///#region 属性
	public final String getExtDesc() throws Exception {
		String dec = "";
		switch (this.getExtType())
		{
			case MapExtXmlList.ActiveDDL:
				dec += "字段" + this.getAttrOfOper();
				break;
			case MapExtXmlList.TBFullCtrl:
				dec += this.getAttrOfOper();
				break;
			case MapExtXmlList.DDLFullCtrl:
				dec += "" + this.getAttrOfOper();
				break;
			case MapExtXmlList.InputCheck:
				dec += "字段：" + this.getAttrOfOper() + " 检查内容：" + this.getTag1();
				break;
			case MapExtXmlList.PopVal:
				dec += "字段：" + this.getAttrOfOper() + " Url：" + this.getTag();
				break;
			default:
				break;
		}
		return dec;
	}
	/**
	 是否自适应大小
	*/
	public final boolean getItIsAutoSize()  {
		return this.GetValBooleanByKey(MapExtAttr.IsAutoSize);
	}
	public final void setItIsAutoSize(boolean value){
		this.SetValByKey(MapExtAttr.IsAutoSize, value);
	}
	/**
	 数据格式
	*/
	public final String getDBType()  {
		return this.GetValStrByKey(MapExtAttr.DBType);
	}
	public final void setDBType(String value){
		this.SetValByKey(MapExtAttr.DBType, value);
	}
	public final String getAtPara()  {
		return this.GetValStrByKey(MapExtAttr.AtPara);
	}
	public final void setAtPara(String value){
		this.SetValByKey(MapExtAttr.AtPara, value);
	}

	public final String getExtModel()  {
		return this.GetValStrByKey(MapExtAttr.ExtModel);
	}
	public final void setExtModel(String value){
		this.SetValByKey(MapExtAttr.ExtModel, value);
	}
	public final String getExtType()  {
		return this.GetValStrByKey(MapExtAttr.ExtType);
	}
	public final void setExtType(String value){
		this.SetValByKey(MapExtAttr.ExtType, value);
	}
	public final String getDoWay()  {
		return this.GetValStrByKey(MapExtAttr.DoWay);
	}
	public final void setDoWay(String value){
		this.SetValByKey(MapExtAttr.DoWay, value);
	}
	/**
	 操作的attrs
	*/
	public final String getAttrOfOper()  {
		return this.GetValStrByKey(MapExtAttr.AttrOfOper);
	}
	public final void setAttrOfOper(String value){
		this.SetValByKey(MapExtAttr.AttrOfOper, value);
	}
	/**
	 激活的attrs
	*/
	public final String getAttrsOfActive() throws Exception {
		//  return this.GetValStrByKey(MapExtAttr.AttrsOfActive).replace("~", "'");
		return this.GetValStrByKey(MapExtAttr.AttrsOfActive);
	}
	public final void setAttrsOfActive(String value){
		this.SetValByKey(MapExtAttr.AttrsOfActive, value);
	}
	public final String getFrmID()  {
		return this.GetValStrByKey(MapExtAttr.FK_MapData);
	}
	public final void setFrmID(String value){
		this.SetValByKey(MapExtAttr.FK_MapData, value);
	}
	public final void setFK_MapData(String val){
		this.SetValByKey(MapExtAttr.FK_MapData, val);

	}
	/**
	 Doc
	*/
	public final String getDoc()  {
		String str = this.GetValStrByKey("Doc");
		str = str.replace("~~", "\"");
		str = str.replace("~", "'");
		return str;
	}
	public final void setDoc(String value)  {
		String str = value.replace("'", "~");
		this.SetValByKey("Doc", str);
	}

	/**
	  处理自动填充SQL

	 @param htMainEn
	 @param htDtlEn
	 @return
	*/
	public final String AutoFullDLL_SQL_ForDtl(Hashtable htMainEn, Hashtable htDtlEn) throws Exception {
		String fullSQL = this.getDoc().replace("@WebUser.No", WebUser.getNo());
		fullSQL = fullSQL.replace("@WebUser.Name", WebUser.getName());
		fullSQL = fullSQL.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
		fullSQL = fullSQL.replace("@WebUser.DeptNo", WebUser.getDeptNo());
		fullSQL = fullSQL.replace("@WebUser.FK_DeptName", WebUser.getDeptName());

		if (fullSQL.contains("@"))
		{
			for (Object key : htDtlEn.keySet())
			{
				if (fullSQL.contains("@") == false)
				{
					break;
				}
				if (fullSQL.contains("@" + key + ";") == true)
				{
					fullSQL = fullSQL.replace("@" + key + ";", htDtlEn.get(key) instanceof String ? (String)htDtlEn.get(key) : null);
				}

				if (fullSQL.contains("@" + key) == true)
				{
					fullSQL = fullSQL.replace("@" + key, htDtlEn.get(key) instanceof String ? (String)htDtlEn.get(key) : null);
				}
			}
		}

		if (fullSQL.contains("@"))
		{
			for (Object key : htMainEn.keySet())
			{
				if (fullSQL.contains("@") == false)
				{
					break;
				}

				if (fullSQL.contains("@" + key + ";") == true)
				{
					fullSQL = fullSQL.replace("@" + key + ";", htMainEn.get(key) instanceof String ? (String)htMainEn.get(key) : null);
				}

				if (fullSQL.contains("@" + key) == true)
				{
					fullSQL = fullSQL.replace("@" + key, htMainEn.get(key) instanceof String ? (String)htMainEn.get(key) : null);
				}
			}
		}
		return fullSQL;
	}

	public final String getTagOfSQLAutoFullTB() throws Exception {
		if (DataType.IsNullOrEmpty(this.getTag()))
		{
			return this.getDocOfSQLDeal();
		}

		String sql = this.getTag();
		sql = sql.replace("@WebUser.No", WebUser.getNo());
		sql = sql.replace("@WebUser.Name", WebUser.getName());
		sql = sql.replace("@WebUser.FK_DeptNameOfFull", WebUser.getDeptNameOfFull());
		sql = sql.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
		sql = sql.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
		sql = sql.replace("@WebUser.DeptNo", WebUser.getDeptNo());
		return sql;
	}

	public final String getDocOfSQLDeal() throws Exception {
		String sql = this.getDoc();
		sql = sql.replace("@WebUser.No", WebUser.getNo());
		sql = sql.replace("@WebUser.Name", WebUser.getName());
		sql = sql.replace("@WebUser.FK_DeptNameOfFull", WebUser.getDeptNameOfFull());
		sql = sql.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
		sql = sql.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
		sql = sql.replace("@WebUser.DeptNo", WebUser.getDeptNo());
		return sql;
	}
	public final String getTag()  {
		String s = this.GetValStrByKey("Tag1");
		s = s.replace("~~", "\"");
		s = s.replace("~", "'");
		s = s.replace("\\\\", "/");
		s = s.replace("\\\\", "/");

		s = s.replace("CCFlow/Data/", "CCFlow/WF/Data/");

		return s;
	}
	public final void setTag(String value){
		this.SetValByKey("Tag", value);
	}
	public final String getTag1()  {
		String str = this.GetValStrByKey("Tag1");
		str = str.replace("~~", "\"");
		str = str.replace("~", "'");
		str = str.replace("‘", "'");
		str = str.replace("’", "'");
		return str;
	}
	public final void setTag1(String value){
		this.SetValByKey("Tag1", value);
	}
	public final String getTag2()  {
		String str = this.GetValStrByKey("Tag2");
		str = str.replace("~~", "\"");
		str = str.replace("~", "'");
		str = str.replace("‘", "'");
		str = str.replace("’", "'");
		return str;
	}
	public final void setTag2(String value){
		this.SetValByKey("Tag2", value);
	}
	public final String getTag3()  {
		String str = this.GetValStrByKey("Tag3");
		str = str.replace("~~", "\"");
		str = str.replace("~", "'");
		str = str.replace("‘", "'");
		str = str.replace("’", "'");
		return str;
	}
	public final void setTag3(String value){
		this.SetValByKey("Tag3", value);
	}
	public final String getTag4()  {
		String str = this.GetValStrByKey("Tag4");
		str = str.replace("~~", "\"");
		str = str.replace("~", "'");
		str = str.replace("‘", "'");
		str = str.replace("’", "'");
		return str;
	}
	public final void setTag4(String value){
		this.SetValByKey("Tag4", value);
	}

	public final int getH()  {
		return this.GetValIntByKey(MapExtAttr.H);
	}
	public final void setH(int value){
		this.SetValByKey(MapExtAttr.H, value);
	}
	public final int getW()  {
		return this.GetValIntByKey(MapExtAttr.W);
	}
	public final void setW(int value){
		this.SetValByKey(MapExtAttr.W, value);
	}

		///#endregion


		///#region 构造方法
	/**
	 扩展
	*/
	public MapExt()
	{
	}
	/**
	 扩展

	 @param mypk
	*/
	public MapExt(String mypk) throws Exception
	{
		this.setMyPK(mypk);
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

		Map map = new Map("Sys_MapExt", "业务逻辑");

		map.IndexField = MapDtlAttr.FK_MapData;
		map.AddMyPK();
		map.AddTBString(MapExtAttr.RefPKVal, null, "RefPKVal", true, false, 0, 100, 20);
		map.AddTBString(MapExtAttr.FK_MapData, null, "表单ID", true, false, 0, 100, 20);
		map.AddTBString(MapExtAttr.ExtModel, null, "类型1", true, false, 0, 30, 20);
		map.AddTBString(MapExtAttr.ExtType, null, "类型2", true, false, 0, 30, 20);

		//修改类型.
		// map.AddTBInt(MapExtAttr.DoWay, 0, "执行方式", true, false);
		map.AddTBString(MapExtAttr.DoWay, null, "执行方式", true, false, 0, 50, 20);

		map.AddTBString(MapExtAttr.AttrOfOper, null, "操作的Attr", true, false, 0, 30, 20);
		map.AddTBString(MapExtAttr.AttrsOfActive, null, "激活的字段", true, false, 0, 900, 20);

		map.AddTBStringDoc();
		map.AddTBString(MapExtAttr.Tag, null, "Tag", true, false, 0, 2000, 20);
		map.AddTBString(MapExtAttr.Tag1, null, "Tag1", true, false, 0, 2000, 20);
		map.AddTBString(MapExtAttr.Tag2, null, "Tag2", true, false, 0, 2000, 20);
		map.AddTBString(MapExtAttr.Tag3, null, "Tag3", true, false, 0, 2000, 20);
		map.AddTBString(MapExtAttr.Tag4, null, "Tag4", true, false, 0, 2000, 20);
		map.AddTBString(MapExtAttr.Tag5, null, "Tag5", true, false, 0, 2000, 20);
		map.AddTBString(MapExtAttr.Tag6, null, "Tag5", true, false, 0, 2000, 20);

		map.AddTBInt(MapExtAttr.H, 500, "高度", false, false);
		map.AddTBInt(MapExtAttr.W, 400, "宽度", false, false);
		map.AddBoolean("IsLazy", false, "是否懒加载?", true, true, true);
		// 数据类型 @0=SQL@1=URLJSON@2=FunctionJSON.
		map.AddTBInt(MapExtAttr.DBType, 0, "数据类型", true, false);
		map.AddTBString(MapExtAttr.FK_DBSrc, "local", "数据源", true, false, 0, 100, 20);
		// map.AddBoolean("NodeCascade", true, "父子节点是否级联", true, true, true);
		// add by zhoupeng 2013-12-21 计算的优先级,用于js的计算.
		// 也可以用于 字段之间的计算 优先级.
		map.AddTBInt(MapExtAttr.PRI, 0, "PRI/顺序号", false, false);
		map.AddTBString(MapExtAttr.AtPara, null, "参数", true, false, 0, 3999, 20);

		map.AddTBString("RefPKVal", null, "RefPKVal", true, false, 0, 100, 20);

		map.AddTBString("TagT", null, "Tagt", true, false, 0, 200, 20);
		map.AddTBString("Tag1T", null, "Tag1", true, false, 0, 200, 20);
		map.AddBoolean("IsLoadFull", true, "加载页面时是否自动填充", false, false);

		this.set_enMap(map);
		return this.get_enMap();
	}
		///#endregion


		///#region 其他方法.
	/**
	 统一生成主键的规则.
	*/
	public final void InitPK() throws Exception {
		if (DataType.IsNullOrEmpty(this.getFrmID()) == true)
		{
			return;
		}
		if (DataType.IsNullOrEmpty(this.getMyPK()) == false)
		{
			return;
		}

		switch (this.getExtType())
		{
			case MapExtXmlList.FullData:
			case MapExtXmlList.FullDataDtl:
				break;
			case MapExtXmlList.ActiveDDL:
				this.setMyPK(MapExtXmlList.ActiveDDL + "_" + this.getFrmID() + "_" + this.getAttrOfOper());
				break;
			case MapExtXmlList.DDLFullCtrl:
				this.setMyPK(MapExtXmlList.DDLFullCtrl + "_" + this.getFrmID() + "_" + this.getAttrOfOper());
				break;
			case MapExtXmlList.PopVal:
				this.setMyPK(MapExtXmlList.PopVal + "_" + this.getFrmID() + "_" + this.getAttrOfOper());
				break;
			case MapExtXmlList.TBFullCtrl:
				this.setMyPK(MapExtXmlList.TBFullCtrl + "_" + this.getFrmID() + "_" + this.getAttrOfOper());
				break;
			case MapExtXmlList.PopFullCtrl:
				this.setMyPK(MapExtXmlList.PopFullCtrl + "_" + this.getFrmID() + "_" + this.getAttrOfOper());
				break;
			case MapExtXmlList.AutoFull:
				this.setMyPK(MapExtXmlList.AutoFull + "_" + this.getFrmID() + "_" + this.getAttrOfOper());
				break;
			case MapExtXmlList.AutoFullDLL:
				this.setMyPK(MapExtXmlList.AutoFullDLL + "_" + this.getFrmID() + "_" + this.getAttrOfOper());
				break;
			case MapExtXmlList.InputCheck:
				this.setMyPK(MapExtXmlList.InputCheck + "_" + this.getFrmID() + "_" + this.getAttrOfOper());
				break;
			case MapExtXmlList.PageLoadFull:
				this.setMyPK(MapExtXmlList.PageLoadFull + "_" + this.getFrmID());
				break;
			case MapExtXmlList.RegularExpression:
				this.setMyPK(MapExtXmlList.RegularExpression + "_" + this.getFrmID() + "_" + this.getAttrOfOper() + "_" + this.getTag());
				break;
			case MapExtXmlList.BindFunction:
				this.setMyPK(MapExtXmlList.BindFunction + "_" + this.getFrmID() + "_" + this.getAttrOfOper() + "_" + this.getTag());
				break;
			case MapExtXmlList.Link:
				this.setMyPK(MapExtXmlList.Link + "_" + this.getFrmID() + "_" + this.getAttrOfOper());
				break;
			default:
				//这里要去掉，不然组合组主键，会带来错误.
				if (DataType.IsNullOrEmpty(this.getAttrOfOper()) == true)
				{
					this.setMyPK(this.getExtType() + "_" + this.getFrmID());
				}
				else
				{
					this.setMyPK(this.getExtType() + "_" + this.getFrmID() + "_" + this.getAttrOfOper());
				}
				break;
		}
	}

	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (Objects.equals(this.getMyPK(), ""))
		{
			this.setMyPK(DBAccess.GenerGUID());
		}

		InitEtcFieldForTSEntity();

		bp.sys.base.Glo.ClearMapDataAutoNum(this.getFrmID());

		return super.beforeInsert();
	}
	/**
	 根据主键初始化其的字段.
	*/
	private void InitEtcFieldForTSEntity() throws Exception {

		if (DataType.IsNullOrEmpty(this.getFrmID()) == true && this.getMyPK().contains("_") == true)
		{
			String[] strs = this.getMyPK().split("[_]", -1);
			//表单ID.
			this.setFrmID(strs[0]);

			//要操作的字段.
			if (DataType.IsNullOrEmpty(this.getAttrOfOper()) == true && strs.length > 2)
			{
				this.setExtType(strs[1]);
			}

			//设置模式.
			if (DataType.IsNullOrEmpty(this.getExtType()) == true && strs.length > 2)
			{
				this.setExtType(strs[2]);
			}
			if (DataType.IsNullOrEmpty(this.getExtModel()) == true && strs.length > 2)
			{
				this.setExtType(strs[2]);
			}
		}

	}

	@Override
	protected boolean beforeUpdate() throws Exception
	{
		this.InitPK();


			///#region 处理ts程序更新前的，补充填写其他的数据.
		if (this.getMyPK().contains("_") == true)
		{
			String[] strs = this.getMyPK().split("[_]", -1);
			//对应的字段包含_
			if (strs.length > 3)
			{
				if (DataType.IsNullOrEmpty(this.getFrmID()) == true)
				{
					this.setFrmID(strs[0]);
				}

				if (DataType.IsNullOrEmpty(this.getExtModel()) == true)
				{
					this.setExtModel(strs[strs.length - 1]);
				}
				if (DataType.IsNullOrEmpty(this.getAttrOfOper()) == true)
				{
					this.setAttrOfOper(this.getMyPK().replace(this.getFrmID() + "_", "").replace("_" + this.getExtModel(), ""));
				}
			}
			if (strs.length == 3)
			{
				if (DataType.IsNullOrEmpty(this.getFrmID()) == true)
				{
					this.setFrmID(strs[0]);
				}

				if (DataType.IsNullOrEmpty(this.getAttrOfOper()) == true)
				{
					this.setAttrOfOper(strs[1]);
				}

				if (DataType.IsNullOrEmpty(this.getExtModel()) == true)
				{
					this.setExtModel(strs[2]);
				}
			}
			if (strs.length == 2) //主表、从表的装载填充
			{
				if (DataType.IsNullOrEmpty(this.getFrmID()) == true)
				{
					this.setFrmID(strs[0]);
				}

				if (DataType.IsNullOrEmpty(this.getExtModel()) == true)
				{
					this.setExtModel(strs[1]);
				}
			}
		}

			///#endregion 处理ts程序更新前的，补充填写其他的数据.

		//根据主键初始化其的字段
		InitEtcFieldForTSEntity();

		switch (this.getExtType())
		{
			case MapExtXmlList.ActiveDDL:
			case MapExtXmlList.DDLFullCtrl:
			case MapExtXmlList.TBFullCtrl:
				//if (this.Doc.contains("@Key") == false)
				//    throw new Exception("@SQL表达式错误，您必须包含@Key ,这个关键字. ");
				break;
			case MapExtXmlList.AutoFullDLL:
				//if (this.Doc.length() <= 3)
				//    throw new Exception("@必须填写SQL表达式. ");
				break;
			case MapExtXmlList.AutoFull:
				//if (this.Doc.length() <= 3)
				//    throw new Exception("@必须填写表达式. 比如 @单价;*@数量; ");
				break;
			case MapExtXmlList.PopVal:
				break;
			default:
				break;
		}

		return super.beforeUpdate();
	}

	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		if (this.getExtType().equals("MultipleChoiceSmall") == true || this.getExtType().equals("SingleChoiceSmall") == true)
		{
			//给该字段增加一个KeyOfEnT
			String mypk = this.getFrmID() + "_" + this.getAttrOfOper() + "T";
			MapAttr attrH = new MapAttr();
			attrH.setMyPK(mypk);
			if (attrH.RetrieveFromDBSources() == 0)
			{
				MapAttr attr = new MapAttr(this.getFrmID() + "_" + this.getAttrOfOper());
				attrH.Copy(attr);
				attrH.setKeyOfEn(attr.getKeyOfEn() + "T");
				attrH.setName(attr.getName());
				attrH.setUIContralType(UIContralType.TB);
				attrH.setMinLen(0);
				attrH.setMaxLen(500);
				attrH.setMyDataType(DataType.AppString);
				attrH.setUIVisible(false);
				attrH.setUIIsEnable(true);
				attrH.setMyPK(attrH.getFrmID() + "_" + attrH.getKeyOfEn());
				attrH.Save();
				attr.SetPara("MultipleChoiceSmall", "1");
			}
		}
		super.afterInsertUpdateAction();
	}

		///#endregion

	/**
	 删除垃圾数据.
	*/
	public static void DeleteDB() throws Exception {
		MapExts exts = new MapExts();
		exts.RetrieveAll();
		return;
	}

	public final String GetFullData(String paras, String oid) throws Exception {
		String tag5 = this.GetValStringByKey(MapExtAttr.Tag5);
		String tag6 = this.GetValStringByKey(MapExtAttr.Tag6);
		if (DataType.IsNullOrEmpty(tag6))
			tag6 = this.getDoc();
		if (tag5.equals("SFTable") || tag5.equals("2"))
		{
			SFSearch sfs = new SFSearch(tag6);
			return sfs.GenerDataOfJsonUesingSln(paras, this.getMyPK());
		}
		if (tag5.equals("Self") || tag5.equals("1"))
		{
			String sql = DealExp(tag6, paras, null);
			SFDBSrc src = new SFDBSrc(this.getDBSrcNo());
			DataTable dt = src.RunSQLReturnTable(sql);
			if(dt.Rows.size() == 0){
				//如果没查到要填充的数据，添加空行数据（用于前台）
				DataRow dr = dt.NewRow();
				for (DataColumn dc : dt.Columns){
					dr.setValue(dc.ColumnName, "");
				}
				dt.Rows.AddRow(dr);
			}
			return bp.tools.Json.ToJson(dt);
		}
		return "";
	}
	public final String GetFullDataDtl(String paras, String oid) throws Exception {
		if (DataType.IsNullOrEmpty(this.getTag1()) == true)
		{
			return "err@关联填充的从表为空";
		}
		DataTable dt = null;
		if (this.getDoWay().equals("SFTable"))
		{
			SFSearch sfs = new SFSearch(this.getDoc());
			String json = sfs.GenerDataOfJsonUesingSln(paras, this.getMyPK());
			dt = bp.tools.Json.ToDataTable(json);
		}
		if (this.getDoWay().equals("Self"))
		{
			String sql = DealExp(this.getDoc(), paras, null);
			SFDBSrc src = new SFDBSrc(this.getDBSrcNo());
			dt = src.RunSQLReturnTable(sql);
		}
		if (dt != null)
		{
			//删除从表数据
			GEDtls dtls = new GEDtls(this.getTag1());
			dtls.Delete(GEDtlAttr.RefPK, oid);
			//结果值插入从表数据
			for (DataRow dr : dt.Rows)
			{
				bp.sys.GEDtl mydtl = new GEDtl(this.getTag1());
				dtls.AddEntity(mydtl);
				for (DataColumn dc : dt.Columns)
				{
					mydtl.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
				}

				mydtl.setRefPK(oid);
				if (mydtl.getOID() > 100)
				{
					mydtl.InsertAsOID(mydtl.getOID());
				}
				else
				{
					mydtl.setOID(0);
					mydtl.Insert();
				}
			}
		}
		return "";
	}

	/// <summary>
	/// 附件填充
	/// </summary>
	/// <param name="paras"></param>
	/// <param name="oid"></param>
	/// <returns></returns>
	public String GetFullDataAth(String paras, String oid) throws Exception {
		if (DataType.IsNullOrEmpty(this.getTag1()) == true)
			return "err@关联填充的附件为空";
		DataTable dt = null;
		if (this.getDoWay().equals("SFTable"))
		{
			SFSearch sfs = new SFSearch(this.getDoc());
			String json = sfs.GenerDataOfJsonUesingSln(paras, this.getMyPK());
			dt = bp.tools.Json.ToDataTable(json);
		}
		if (this.getDoWay().equals("Self"))
		{
			String sql = DealExp(this.getDoc(), paras, null);
			SFDBSrc src = new SFDBSrc(this.getDBSrcNo());
			dt = src.RunSQLReturnTable(sql);
		}
		if (dt != null)
		{
			//删除附件数据
			FrmAttachmentDBs athDBs = new FrmAttachmentDBs();
			athDBs.Delete(FrmAttachmentDBAttr.NoOfObj,this.getTag1(), FrmAttachmentDBAttr.RefPKVal, oid);
			//结果值插入从表数据
			FrmAttachmentDB athDB = null;
			for (DataRow dr : dt.Rows)
			{
				athDB = new FrmAttachmentDB();
				String fileName = dr.getValue(0).toString(); //文件名称. 我的附件.
				String fileUrl = dr.getValue(1).toString();  //
				String sort = dr.getValue(2).toString();  //文件类别


				athDB.setRefPKVal(oid);
				athDB.setMyPK(DBAccess.GenerGUID());
				athDB.setSort(sort);
				if(this.getFrmID().startsWith("ND"))
					athDB.setNodeID(Integer.parseInt(this.getFrmID().replace("ND","")));
				else
					athDB.setNodeID(0);
				athDB.setRefPKVal(oid);
				athDB.setFrmID(this.getFrmID());
				athDB.setFKFrmAttachment(this.getFrmID()+"_"+this.getTag1());
				athDB.setFileName(fileName);
				athDB.setFileExts(fileName.substring(fileName.lastIndexOf(".")));
				try
				{
					File info = new File(fileUrl);
					athDB.setFileSize((float)info.length());
				}
				catch(Exception ex)
				{
					throw new Exception("err@附件["+fileName+"]的路径["+ fileUrl + "]不能下载");
				}
				athDB.setFileFullName(fileUrl);
				athDB.setRDT(DataType.getCurrentDateTimess());
				athDB.setRec(bp.web.WebUser.getNo());
				athDB.setRecName(bp.web.WebUser.getName());
				athDB.setDeptNo(WebUser.getDeptNo());
				athDB.setDeptName(WebUser.getDeptName());
				athDB.Insert();
			}
		}
		return "";
	}

	/**
	 根据字段，参数返回查询数据的DataTable

	 @param field 字段名
	 @param paras 参数
	 @param sqlWhere 增加的查询条件的SQL
	 @return
	*/
	public final String GetDataTableByField(String field, String paras, String sqlWhere, String oid, String type) throws Exception {
		if (DataType.IsNullOrEmpty(field) == true)
		{
			return "err@" + this.getMyPK() + "中" + field + "传参不能为空";
		}

		String sql = this.GetValStringByKey(field); //获得SQL.
		if (DataType.IsNullOrEmpty(sql) == true)
		{
			return "err@【"+this.getAttrOfOper()+"】配置的扩展属性【"+this.getExtModel()+"】的字段" + field + "执行的SQL为空,或者没有配置字典.";
		}

		//判断是不是使用字典表中的数据
		if (sql.toLowerCase().contains("select") == false && sql.toLowerCase().contains("@") == false)
		{
			SFTable sf = new SFTable();
			sf.setNo(sql);
			if (sf.RetrieveFromDBSources() == 1)
			{
				return sf.GenerJsonByPara(paras);
			}
			else
			{
				throw new RuntimeException("err@字典[" + sql + "]不存在.");
			}
		}

		if (this.getDBType().equals("0") == false)
		{
			return "err@数据源类型不是按照SQL查询,DBType=" + this.getDBType();
		}

		if (DBAccess.IsExitsTableCol("Sys_MapExt", field) == false)
		{
			return "err@传的参数不正确,Field=" + field + "在Sys_MapExt表中不存在";
		}


		//如果是SQL字典.
		if (sql.toLowerCase().contains("SELECT") == false)
		{
			if (this.getDoWay().equals("Self"))
			{
				sql = DealExp(sql, paras, null);
				SFDBSrc dbSrc = new SFDBSrc(this.getDBSrcNo());
				return bp.tools.Json.ToJson(dbSrc.RunSQLReturnTable(sql));

			}

			SFTable dict = new SFTable();
			dict.setNo(sql);
			if (dict.RetrieveFromDBSources() == 1)
			{
				return dict.GenerJsonByPara(paras);
			}
		}

		//填充下拉框
		GEEntity en = null;
		try{
			if (DataType.IsNullOrEmpty(oid) == false && oid.contains("_") == false && type.contains("Dtl") == false)
			{
				if (oid.equals("0"))
				{
					en = new GEEntity(this.getFrmID());
				}
				else
				{
					en = new GEEntity(this.getFrmID(), Long.parseLong(oid));
				}
			}
		}
		catch (Exception ex){

		}

		if (Objects.equals(this.getExtType(), MapExtXmlList.FullData) && field.equals("Tag") == true)
		{
			String[] strs = sql.split("[$]", -1);
			DataSet ds = new DataSet();
			for (String str : strs)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}
				String[] ss = str.split("[:]", -1);
				if (ss.length == 2)
				{

					sql = DealExp(ss[1], paras, en);
					DataTable dtt = null;
					if (DataType.IsNullOrEmpty(this.getDBSrcNo()) == false && this.getDBSrcNo().equals("local") == false)
					{
						SFDBSrc sfdb = new SFDBSrc(this.getDBSrcNo());
						dtt = sfdb.RunSQLReturnTable(sql);
					}
					else
					{
						dtt = DBAccess.RunSQLReturnTable(sql);
					}
					if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
					{
						dtt.Columns.get("NO").ColumnName = "No";
						dtt.Columns.get("NAME").ColumnName = "Name";

						//判断是否存在PARENTNO列，避免转换失败
						if (dtt.Columns.contains("PARENTNO") == true)
						{
							dtt.Columns.get("PARENTNO").ColumnName = "ParentNo";
						}
					}

					if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
					{
						dtt.Columns.get("no").ColumnName = "No";
						dtt.Columns.get("name").ColumnName = "Name";

						//判断是否存在PARENTNO列，避免转换失败
						if (dtt.Columns.contains("parentno") == true)
						{
							dtt.Columns.get("parentno").ColumnName = "ParentNo";
						}
					}
					dtt.TableName = ss[0];
					ds.Tables.add(dtt);
				}
			}
			return bp.tools.Json.ToJson(ds);
		}

		if (DataType.IsNullOrEmpty(sqlWhere) == false)
		{
			if (sql.toLowerCase().indexOf("where") == -1)
			{
				sql += " WHERE 1=1 ";
			}

			sql += sqlWhere;
		}

		sql = DealExp(sql, paras, en);

		DataTable dt = null;
		if (DataType.IsNullOrEmpty(this.getDBSrcNo()) == false && this.getDBSrcNo().equals("local") == false)
		{
			SFDBSrc sfdb = new SFDBSrc(this.getDBSrcNo());
			dt = sfdb.RunSQLReturnTable(sql);
		}
		else
		{
			dt = DBAccess.RunSQLReturnTable(sql);
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			if (dt.Columns.contains("NO") == true)
			{
				dt.Columns.get("NO").ColumnName = "No";
			}
			if (dt.Columns.contains("NAME") == true)
			{
				dt.Columns.get("NAME").ColumnName = "Name";
			}

			//判断是否存在PARENTNO列，避免转换失败
			if (dt.Columns.contains("PARENTNO") == true)
			{
				dt.Columns.get("PARENTNO").ColumnName = "ParentNo";
			}
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			if (dt.Columns.contains("no") == true)
			{
				dt.Columns.get("no").ColumnName = "No";
			}
			if (dt.Columns.contains("name") == true)
			{
				dt.Columns.get("name").ColumnName = "Name";
			}

			//判断是否存在PARENTNO列，避免转换失败
			if (dt.Columns.contains("parentno") == true)
			{
				dt.Columns.get("parentno").ColumnName = "ParentNo";
			}
		}

		return bp.tools.Json.ToJson(dt);
	}



	public final String GetDataTableByTag1(String key, String paras, String oid) throws Exception {
		String sql = "";
		if (DataType.IsNullOrEmpty(this.getTag1()) == false)
		{
			String[] condition = this.getTag1().split("[$]", -1);
			for (String para : condition)
			{
				if (para.contains("Para=" + key + "#") == false)
				{
					continue;
				}
				if (para.contains("ListSQL=") == false)
				{
					continue;
				}
				sql = para.substring(para.indexOf("ListSQL=") + 8);
				break;
			}

		}

		if (DataType.IsNullOrEmpty(sql) == true)
		{
			return "err@TableSearch设置的查询条件字段" + key + "的SQL查询语句为空";
		}

		GEEntity en = null;
		if (sql.contains("@") == true){
			if (DataType.IsNullOrEmpty(oid) == false && DataType.IsNumStr(oid))
			{
				en = new GEEntity(this.getFrmID(), Long.parseLong(oid));
			}
			sql = DealExp(sql, paras, en);
		}


		if (sql.contains("@") == true)
		{
			return "err@执行的SQL中" + sql + " 有@符号没有被替换";
		}
		DataTable dt = null;
		if (DataType.IsNullOrEmpty(this.getDBSrcNo()) == false && this.getDBSrcNo().equals("local") == false)
		{
			SFDBSrc sfdb = new SFDBSrc(this.getDBSrcNo());
			dt = sfdb.RunSQLReturnTable(sql);
		}
		else
		{
			dt = DBAccess.RunSQLReturnTable(sql);
		}

		return bp.tools.Json.ToJson(dt);
	}
	/// <summary>
	/// 表格查询
	/// </summary>
	/// <param name="paras"></param>
	/// <returns></returns>
	public String GetDataTableByTableSimple(String paras, String field) throws Exception {

		String sql = this.getTag2(); //查询的条件
		if(field.equals("Tag1"))
			sql = this.getTag1();

		if (DataType.IsNullOrEmpty(paras) == false)
		{
			JSONObject json = JSONObject.fromObject(paras);
			for (Object item : json.keySet()) {
				if(sql.contains("@")==false)
					break;
				if (item == null)
					continue;
				String key = item.toString();
				String val = json.getString(key);
				if (DataType.IsNullOrEmpty(val))
					val = "";

				if (DataType.IsNullOrEmpty(this.getDBSrcNo()) == false && this.getDBSrcNo().equals("local") == false){
					val = URLEncoder.encode(val, StandardCharsets.UTF_8.toString());
				}
				sql = sql.replace("@" + key, val);
			}
		}
		sql = DealExp(sql, "", null);

		DataTable dt = null;
		if (DataType.IsNullOrEmpty(this.getDBSrcNo()) == false && this.getDBSrcNo().equals("local") == false)
		{
			SFDBSrc sfdb = new SFDBSrc(this.getDBSrcNo());
			dt = sfdb.RunSQLReturnTable(sql);
		}
		else
			dt = DBAccess.RunSQLReturnTable(sql);
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("NO").ColumnName = "No";
			dt.Columns.get("NAME").ColumnName = "Name";

			//判断是否存在PARENTNO列，避免转换失败
			if (dt.Columns.contains("PARENTNO") == true)
				dt.Columns.get("PARENTNO").ColumnName = "ParentNo";
		}

		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("no").ColumnName = "No";
			dt.Columns.get("name").ColumnName = "Name";

			//判断是否存在PARENTNO列，避免转换失败
			if (dt.Columns.contains("parentno") == true)
				dt.Columns.get("parentno").ColumnName = "ParentNo";
		}

		return bp.tools.Json.ToJson(dt);
	}

	/// <summary>
	/// 表格查询
	/// </summary>
	/// <param name="paras">查询参数</param>
	/// <param name="params">替换的参数</param>
	/// <returns></returns>
	public String GetDataTableByTableSearch(String paras,String params) throws Exception {
		String sql = this.getTag2(); //查询的条件
		int pageIdx = 0;
		int pageSize = this.GetParaInt("PageSize", 10);//默认一页显示十行
		//如果数据源不包含PageIdx
		boolean isHavePageIdx = sql.indexOf("PageIdx") == -1 ? false : true;

		if (sql.toLowerCase().indexOf("where") == -1)
			sql += " WHERE 1=1";
		if (DataType.IsNullOrEmpty(paras) == false)
		{
			JSONObject json = JSONObject.fromObject(paras);
			for (Object item : json.keySet())
			{
				if(item == null)
					continue;
				String key = item.toString();
				String val = json.getString(key);
				if(DataType.IsNullOrEmpty(val))
					val="";
				if(key.equals("PageSize") || key.equals("PageIdx"))
				{
					sql = sql.replace("@"+key, val);
					if(key.equals("PageIdx"))
						pageIdx =  Integer.parseInt(val);
					if (key.equals("PageSize"))
						pageSize = Integer.parseInt(val);
					continue;
				}
				if (key.equals("Key"))
				{
					sql = sql.replace("@Key", val);
					continue;
				}
				if (key.startsWith("DTFrom_"))
				{
					String myKey = key.replace("DTFrom_", "");
					if(DataType.IsNullOrEmpty(val) == true)
					{
						if(sql.contains("@"+ key) == true)
						{
							sql = sql.replace(myKey+">='@" + key + "'", "1=1");
							sql = sql.replace(myKey + ">'@" + key + "'", "1=1");
						}
					}
					else
					{
							sql = sql.replace("@"+ key, val);
					}
					continue;
				}
				if (key.startsWith("DTTo_"))
				{
					String myKey =key.replace("DTTo_", "");
					if (DataType.IsNullOrEmpty(val) == true)
					{
						if (sql.contains("@" + key) == true)
						{
							sql = sql.replace(myKey + "<='@" + key + "'", "1=1");
							sql = sql.replace(myKey + "<'@" + key + "'", "1=1");
						}
					}
					else
					{
						sql = sql.replace("@" + key, val);
					}
					continue;
				}
				//下拉框的解析
				if(val.equals(""))
				{
					sql = sql.replace(key + "=@" + key, "1=1");
					sql = sql.replace(key + "='@" + key + "'", "1=1");
				}
				if (val.equals("")==false)
				{
					sql = sql.replace("@" + key, val);
				}
			}
		}
		if (DataType.IsNullOrEmpty(params) == false) {
			JSONObject json = JSONObject.fromObject(params);
			for (Object item : json.keySet()) {
				if(sql.contains("@")==false)
					break;
				if (item == null)
					continue;
				String key = item.toString();
				String val = json.getString(key);
				if (DataType.IsNullOrEmpty(val))
					val = "";
				if (val.equals("") == false) {
					sql = sql.replace("@" + key, val);
				}
			}
		}
		sql = DealExp(sql, "", null);

		DataTable dt = null;
		if (DataType.IsNullOrEmpty(this.getDBSrcNo()) == false && this.getDBSrcNo().equals("local") == false)
		{
			SFDBSrc sfdb = new SFDBSrc(this.getDBSrcNo());
			int fromPage = (pageIdx - 1) * pageSize;
			if (sfdb.getHisDBType().equals(bp.sys.DBSrcType.MySQL))
			{
				sql = "SELECT * From(" + sql + ") A LIMIT " + fromPage + ", " + pageSize;
			}
			//不包含分页查询
			if(isHavePageIdx == false)
			{

				if (sfdb.getHisDBType().equals(bp.sys.DBSrcType.Oracle) || sfdb.getHisDBType().equals(bp.sys.DBSrcType.KingBaseR3) || sfdb.getHisDBType().equals(bp.sys.DBSrcType.KingBaseR6) || sfdb.getHisDBType().equals(DBSrcType.GBASE8CByOracle))
				{
					int beginIndex = (pageIdx - 1) * pageSize + 1;
					int endIndex = pageIdx * pageSize;
					sql = "SELECT * FROM ( SELECT A.*, ROWNUM RN " + "FROM ("+sql+") A WHERE ROWNUM <= " + endIndex + " ) WHERE RN >=" + beginIndex;
				}
			}
			dt = sfdb.RunSQLReturnTable(sql);
		}
		else{
			int fromPage = (pageIdx - 1) * pageSize;
			if (SystemConfig.getAppCenterDBType() ==DBType.MySQL || SystemConfig.getAppCenterDBType() ==DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() ==DBType.GBASE8A)
			{
				sql = "SELECT * From(" + sql + ") A LIMIT " + fromPage + ", " + pageSize;
			}
			//不包含分页查询
			if (isHavePageIdx == false)
			{

				if (SystemConfig.getAppCenterDBType() ==DBType.DM || SystemConfig.getAppCenterDBType() ==DBType.Oracle || SystemConfig.getAppCenterDBType() ==DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() ==DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() ==DBType.GBASE8CByOracle)
				{
					int beginIndex = (pageIdx - 1) * pageSize + 1;
					int endIndex = pageIdx * pageSize;
					sql = "SELECT * FROM ( SELECT A.*, ROWNUM RN " + "FROM (" + sql + ") A WHERE ROWNUM <= " + endIndex + " ) WHERE RN >=" + beginIndex;
				}
			}
			dt = DBAccess.RunSQLReturnTable(sql);
		}


		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("NO").ColumnName = "No";
			dt.Columns.get("NAME").ColumnName = "Name";

			//判断是否存在PARENTNO列，避免转换失败
			if (dt.Columns.contains("PARENTNO") == true)
				dt.Columns.get("PARENTNO").ColumnName = "ParentNo";
		}

		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("no").ColumnName = "No";
			dt.Columns.get("name").ColumnName = "Name";

			//判断是否存在PARENTNO列，避免转换失败
			if (dt.Columns.contains("parentno") == true)
				dt.Columns.get("parentno").ColumnName = "ParentNo";
		}
		dt.TableName = "SearchData";
		DataSet ds = new DataSet();
		ds.Tables.add(dt);

		sql = this.getTag3(); //查询的条件
		if(DataType.IsNullOrEmpty(sql)== true)
			return bp.tools.Json.ToJson(ds);
		if (sql.toLowerCase().indexOf("where") == -1)
			sql += " WHERE 1=1";
		if (DataType.IsNullOrEmpty(paras) == false)
		{
			JSONObject json = JSONObject.fromObject(paras);
			for (Object item : json.keySet())
			{
				if(item == null)
					continue;
				String key = item.toString();
				String val = json.getString(key);
				if(DataType.IsNullOrEmpty(val))
					val="";
				if (key.equals("PageSize") || key.equals("PageIdx"))
				{
					sql = sql.replace("@" + key, val);
					continue;
				}
				if (key.equals("Key"))
				{
					sql = sql.replace("@Key", val);
					continue;
				}
				if (key.startsWith("DTFrom_"))
				{
					String myKey = key.replace("DTFrom_", "");
					if (DataType.IsNullOrEmpty(val) == true)
					{
						if (sql.contains("@" + key) == true)
						{
							sql = sql.replace(myKey + ">='@" + key + "'", "1=1");
							sql = sql.replace(myKey + ">'@" + key + "'", "1=1");
						}
					}
					else
					{
						sql = sql.replace("@" + key, val);
					}
					continue;
				}
				if (key.startsWith("DTTo_"))
				{
					String myKey = key.replace("DTTo_", "");
					if (DataType.IsNullOrEmpty(val) == true)
					{
						if (sql.contains("@" + key) == true)
						{
							sql = sql.replace(myKey + "<='@" + key + "'", "1=1");
							sql = sql.replace(myKey + "<'@" + key + "'", "1=1");
						}
					}
					else
					{
						sql = sql.replace("@" + key, val);
					}
					continue;
				}
				//下拉框的解析
				if (val.equals(""))
				{
					sql = sql.replace(key + "=@" + key, "1=1");
					sql = sql.replace(key + "='@" + key + "'", "1=1");
				}
				if (val.equals("") == false)
				{
					sql = sql.replace("@" + key, val);
				}
			}
		}

		sql = DealExp(sql, "", null);
		int count = 0;
		if (DataType.IsNullOrEmpty(this.getDBSrcNo()) == false && this.getDBSrcNo().equals("local") == false)
		{
			SFDBSrc sfdb = new SFDBSrc(this.getDBSrcNo());
			count = sfdb.RunSQLReturnInt(sql,0);
		}
		else
			count = DBAccess.RunSQLReturnValInt(sql);
		DataTable dtCount = new DataTable("DTCout");
		dtCount.TableName = "DTCout";
		dtCount.Columns.Add("Count", Integer.class);
		DataRow dr = dtCount.NewRow();
		dr.setValue(0,count);
		dtCount.Rows.add(dr);
		ds.Tables.add(dtCount);
		return bp.tools.Json.ToJson(ds);
	}
	private String DealExp(String exp, String paras, Entity en) throws Exception {
		//替换字符
		exp = exp.replace("~~", "\"");
		exp = exp.replace("~", "'");

		if (exp.contains("@") == false)
		{
			return exp;
		}

		//首先替换加; 的。
		exp = exp.replace("@WebUser.No;", WebUser.getNo());
		exp = exp.replace("@WebUser.Name;", WebUser.getName());
		exp = exp.replace("@WebUser.FK_DeptNameOfFull;", WebUser.getDeptNameOfFull());
		exp = exp.replace("@WebUser.FK_DeptName;", WebUser.getDeptName());
		exp = exp.replace("@WebUser.FK_Dept;", WebUser.getDeptNo());
		exp = exp.replace("@WebUser.DeptNo;", WebUser.getDeptNo());
		exp = exp.replace("@WebUser.OrgNo;", WebUser.getOrgNo());
		exp = exp.replace("@WebUser.OrgName;", WebUser.getOrgName());


		// 替换没有 ; 的 .
		exp = exp.replace("@WebUser.No", WebUser.getNo());
		exp = exp.replace("@WebUser.Name", WebUser.getName());
		exp = exp.replace("@WebUser.FK_DeptNameOfFull", WebUser.getDeptNameOfFull());
		exp = exp.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
		exp = exp.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
		exp = exp.replace("@WebUser.DeptNo", WebUser.getDeptNo());
		exp = exp.replace("@WebUser.OrgNo", WebUser.getOrgNo());
		exp = exp.replace("@WebUser.OrgName", WebUser.getOrgName());

		if (exp.contains("@") == false)
		{
			return exp;
		}

		if (DataType.IsNullOrEmpty(paras) == false && paras.equals("undefined") == false)
		{
			if (paras.contains("@") == true)
			{
				String[] strs = paras.split("[@]", -1);
				for (String key : strs)
				{
					if (DataType.IsNullOrEmpty(key) == true)
					{
						continue;
					}
					String attrKeyOfEn = key.split("[=]", -1)[0];
					String val = key.split("[=]", -1).length == 1 ? "" : key.split("[=]", -1)[1];
					if(DataType.IsNullOrEmpty(val)==false)
						val = val.replace("~", "@");
					exp = exp.replace("@" + attrKeyOfEn, val);
					if (attrKeyOfEn.toLowerCase().equals("key"))
					{
						exp = exp.replace("@Key", val);
						exp = exp.replace("@key", val);
						exp = exp.replace("@KEY", val);
					}

					if (exp.contains("@") == false)
					{
						break;
					}

				}
			}
			else
			{
				exp = exp.replace("@Key", paras);
				exp = exp.replace("@key", paras);
				exp = exp.replace("@KEY", paras);
			}


		}

		if (exp.contains("@") == false)
		{
			return exp;
		}

		//增加对新规则的支持. @MyField; 格式.
		if (en != null)
		{
			Attrs attrs = en.getEnMap().getAttrs();
			Row row = en.getRow();
			//特殊判断.
			if (row.containsKey("OID") == true)
			{
				exp = exp.replace("@WorkID", row.get("OID").toString());
			}

			if (exp.contains("@") == false)
			{
				return exp;
			}

			for (String key : row.keySet())
			{
				//值为空或者null不替换
				if (row.get(key) == null || row.get(key).equals("") == true)
				{
					exp = exp.replace("@" + key, "");
				}
				if (exp.contains("@" + key))
				{
					exp = exp.replace("@" + key, row.get(key).toString());
				}

				//不包含@则返回SQL语句
				if (exp.contains("@") == false)
				{
					return exp;
				}
			}

		}

		if (exp.contains("@") && SystemConfig.isBSsystem() == true)
		{
			/*如果是bs*/
			for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet())
			{
				if (DataType.IsNullOrEmpty(key))
				{
					continue;
				}
				exp = exp.replace("@" + key, ContextHolderUtils.getRequest().getParameter(key));
			}

		}

		exp = exp.replace("~", "'");
		return exp;
	}

	/**
	 保存大块html文本

	 @return
	*/
	public final String SaveBigNoteHtmlText(String text) throws Exception {
		DBAccess.SaveBigTextToDB(text, "Sys_MapExt", "MyPK", this.getMyPK(), "HtmlText");
		return "保存成功！";
	}

	public final String ReadBigNoteHtmlText() throws Exception {
		String doc = DBAccess.GetBigTextFromDB("Sys_MapExt", "MyPK", this.getMyPK(), "HtmlText");
		return doc;
	}
}
