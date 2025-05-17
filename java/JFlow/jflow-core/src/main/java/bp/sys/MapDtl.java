package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import bp.sys.base.FormEventBaseDtl;

import java.util.*;

/**
 明细
*/
public class MapDtl extends EntityNoName
{

		///#region 导入导出属性.
	/**
	 关联主键
	*/
	public final String getRefPK() throws Exception {
		String str = this.GetValStrByKey(MapDtlAttr.RefPK);
		if (DataType.IsNullOrEmpty(str))
		{
			return "RefPK";
		}
		return str;
	}
	public final void setRefPK(String value){
		this.SetValByKey(MapDtlAttr.RefPK, value);
	}
	/**
	 Rowid
	*/
	public final int getRowIdx()  {
		return this.GetValIntByKey(MapDtlAttr.RowIdx);
	}
	public final void setRowIdx(int value){
		this.SetValByKey(MapDtlAttr.RowIdx, value);
	}
	/**
	 是否可以导入
	*/
	public final boolean getItIsImp()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsImp);
	}
	public final void setItIsImp(boolean value){
		this.SetValByKey(MapDtlAttr.IsImp, value);
	}
	/**
	 是否可以导出
	*/
	public final boolean getItIsExp()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsExp);
	}
	public final void setItIsExp(boolean value){
		this.SetValByKey(MapDtlAttr.IsExp, value);
	}
	/**
	 执行的类
	*/
	public final String getFEBD()  {
		return this.GetValStringByKey(MapDtlAttr.FEBD);
	}
	public final void setFEBD(String value){
		this.SetValByKey(MapDtlAttr.FEBD, value);
	}
	/**
	 导入模式
	*/
	public final int getImpModel()  {
		return this.GetValIntByKey(MapDtlAttr.ImpModel);
	}
	public final void setImpModel(int value){
		this.SetValByKey(MapDtlAttr.ImpModel, value);
	}
	/**
	 查询sql
	*/
	public final String getImpSQLInit()  {
		return this.GetValStringByKey(MapDtlAttr.ImpSQLInit).replace("~", "'");
	}
	public final void setImpSQLInit(String value){
		this.SetValByKey(MapDtlAttr.ImpSQLInit, value);
	}
	/**
	 搜索sql
	*/
	public final String getImpSQLSearch()  {
		return this.GetValStringByKey(MapDtlAttr.ImpSQLSearch).replace("~", "'");
	}
	public final void setImpSQLSearch(String value){
		this.SetValByKey(MapDtlAttr.ImpSQLSearch, value);
	}
	/**
	 填充数据
	*/
	public final String getImpSQLFullOneRow()  {
		return this.GetValStringByKey(MapDtlAttr.ImpSQLFullOneRow).replace("~", "'");
	}
	public final void setImpSQLFullOneRow(String value){
		this.SetValByKey(MapDtlAttr.ImpSQLFullOneRow, value);
	}

		///#endregion


		///#region 基本设置
	/**
	 工作模式
	*/
	public final DtlModel getDtlModel() {
		return DtlModel.forValue(this.GetValIntByKey(MapDtlAttr.Model));
	}
	public final void setDtlModel(DtlModel value){
		this.SetValByKey(MapDtlAttr.Model, value.getValue());
	}
	/**
	 是否启用行锁定.
	*/
	public final boolean getItIsRowLock() {
		return this.GetParaBoolen(MapDtlAttr.IsRowLock, false);
	}
	public final void setItIsRowLock(boolean value)  {
		this.SetPara(MapDtlAttr.IsRowLock, value);
	}

		///#endregion 基本设置


		///#region 参数属性
	/**
	 记录增加模式
	*/
	public final DtlAddRecModel getDtlAddRecModel() {
		return DtlAddRecModel.forValue(this.GetParaInt(MapDtlAttr.DtlAddRecModel));
	}
	public final void setDtlAddRecModel(DtlAddRecModel value)  {
		this.SetPara(MapDtlAttr.DtlAddRecModel, value.getValue());
	}
	/**
	 保存方式
	*/
	public final DtlSaveModel getDtlSaveModel() {
		return DtlSaveModel.forValue(this.GetParaInt(MapDtlAttr.DtlSaveModel));
	}
	public final void setDtlSaveModel(DtlSaveModel value)  {
		this.SetPara(MapDtlAttr.DtlSaveModel, value.getValue());
	}
	/**
	 是否启用Link,在记录的右边.
	*/
	public final boolean getItIsEnableLink() {
		return this.GetParaBoolen(MapDtlAttr.IsEnableLink, false);
	}
	public final void setItIsEnableLink(boolean value)  {
		this.SetPara(MapDtlAttr.IsEnableLink, value);
	}
	public final String getLinkLabel() throws Exception {
		String s = this.GetParaString(MapDtlAttr.LinkLabel);
		if (DataType.IsNullOrEmpty(s))
		{
			return "详细";
		}
		return s;
	}
	public final void setLinkLabel(String value)  {
		this.SetPara(MapDtlAttr.LinkLabel, value);
	}
	public final String getLinkUrl() throws Exception {
		String s = this.GetValStrByKey(MapDtlAttr.LinkUrl);
		if (DataType.IsNullOrEmpty(s))
		{
			return "http://ccport.org";
		}
		return s;
	}
	public final void setLinkUrl(String value){
		this.SetValByKey(MapDtlAttr.LinkUrl, value);
		//String val = value;
		//val = val.replace("@", "*");
		//this.SetPara(MapDtlAttr.LinkUrl, val);
	}
	public final String getLinkTarget() throws Exception {
		String s = this.GetParaString(MapDtlAttr.LinkTarget);
		if (DataType.IsNullOrEmpty(s))
		{
			return "_blank";
		}
		return s;
	}
	public final void setLinkTarget(String value)  {
		this.SetPara(MapDtlAttr.LinkTarget, value);
	}
	/**
	 子线程处理人字段(用于分流节点的明细表分配子线程任务).
	*/
	public final String getSubThreadWorker() throws Exception {
		String s = this.GetParaString(MapDtlAttr.SubThreadWorker);
		if (DataType.IsNullOrEmpty(s))
		{
			return "";
		}
		return s;
	}
	public final void setSubThreadWorker(String value)  {
		this.SetPara(MapDtlAttr.SubThreadWorker, value);
	}
	/**
	 子线程分组字段(用于分流节点的明细表分配子线程任务)
	*/
	public final String getSubThreadGroupMark() throws Exception {
		String s = this.GetParaString(MapDtlAttr.SubThreadGroupMark);
		if (DataType.IsNullOrEmpty(s))
		{
			return "";
		}
		return s;
	}
	public final void setSubThreadGroupMark(String value)  {
		this.SetPara(MapDtlAttr.SubThreadGroupMark, value);
	}
	/**
	 节点ID
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(MapDtlAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(MapDtlAttr.FK_Node, value);
	}

	public final String getInitDBAttrs()  {
		return this.GetValStringByKey(MapDtlAttr.InitDBAttrs);
	}

		///#endregion 参数属性


		///#region 外键属性
	/**
	 框架
	*/
	public final MapFrames getMapFrames() throws Exception {
		Object tempVar = this.GetRefObject("MapFrames");
		MapFrames obj = tempVar instanceof MapFrames ? (MapFrames)tempVar : null;
		if (obj == null)
		{
			obj = new MapFrames(this.getNo());
			this.SetRefObject("MapFrames", obj);
		}
		return obj;
	}
	/**
	 逻辑扩展
	*/
	public final MapExts getMapExts() throws Exception {
		Object tempVar = this.GetRefObject("MapExts");
		MapExts obj = tempVar instanceof MapExts ? (MapExts)tempVar : null;
		if (obj == null)
		{
			obj = new MapExts(this.getNo());
			this.SetRefObject("MapExts", obj);
		}
		return obj;
	}
	/**
	 事件:
	 1.该事件与Node,Flow,MapDtl,MapData一样的算法.
	 2.如果一个业务逻辑有变化，其他的也要变化.
	*/
	public final FrmEvents getFrmEvents() throws Exception {
		//判断内存是否有？.
		Object tempVar = this.GetRefObject("FrmEvents");
		FrmEvents objs = tempVar instanceof FrmEvents ? (FrmEvents)tempVar : null;
		if (objs != null)
		{
			return objs; //如果缓存有值，就直接返回.
		}

		int count = this.GetParaInt("FrmEventsNum", -1);
		if (count == -1)
		{
			objs = new FrmEvents();
			objs.Retrieve(FrmEventAttr.FrmID, this.getNo());

			this.SetPara("FrmEventsNum", objs.size()); //设置他的数量.
			this.DirectUpdate();
			this.SetRefObject("FrmEvents", objs);
			return objs;
		}

		if (count == 0)
		{
			objs = new FrmEvents();
			this.SetRefObject("FrmEvents", objs);
			return objs;
		}
		else
		{
			objs = new FrmEvents();
			objs.Retrieve(FrmEventAttr.FrmID, this.getNo());
			this.SetPara("FrmEventsNum", objs.size()); //设置他的数量.
			this.SetRefObject("FrmEvents", objs);
		}
		return objs;
	}
	/**
	 从表
	*/
	public final MapDtls getMapDtls() throws Exception {
		Object tempVar = this.GetRefObject("MapDtls");
		MapDtls obj = tempVar instanceof MapDtls ? (MapDtls)tempVar : null;
		if (obj == null)
		{
			obj = new MapDtls(this.getNo());
			this.SetRefObject("MapDtls", obj);
		}
		return obj;
	}

	/**
	 附件
	*/
	public final FrmAttachments getFrmAttachments() throws Exception {
		Object tempVar = this.GetRefObject("FrmAttachments");
		FrmAttachments obj = tempVar instanceof FrmAttachments ? (FrmAttachments)tempVar : null;
		if (obj == null)
		{
			obj = new FrmAttachments(this.getNo());
			this.SetRefObject("FrmAttachments", obj);
		}
		return obj;
	}
	/**
	 图片附件
	*/
	public final FrmImgAths getFrmImgAths() throws Exception {
		Object tempVar = this.GetRefObject("FrmImgAths");
		FrmImgAths obj = tempVar instanceof FrmImgAths ? (FrmImgAths)tempVar : null;
		if (obj == null)
		{
			obj = new FrmImgAths(this.getNo());
			this.SetRefObject("FrmImgAths", obj);
		}
		return obj;
	}
	/**
	 单选按钮
	*/
	public final FrmRBs getFrmRBs() throws Exception {
		Object tempVar = this.GetRefObject("FrmRBs");
		FrmRBs obj = tempVar instanceof FrmRBs ? (FrmRBs)tempVar : null;
		if (obj == null)
		{
			obj = new FrmRBs(this.getNo());
			this.SetRefObject("FrmRBs", obj);
		}
		return obj;
	}
	/**
	 属性
	*/
	public final MapAttrs getMapAttrs() throws Exception {
		Object tempVar = this.GetRefObject("MapAttrs");
		MapAttrs obj = tempVar instanceof MapAttrs ? (MapAttrs)tempVar : null;
		if (obj == null)
		{
			obj = new MapAttrs(this.getNo());
			this.SetRefObject("MapAttrs", obj);
		}
		return obj;
	}

		///#endregion


		///#region 属性
	public GEDtls HisGEDtls_temp = null;
	public final EditModel getHisEditModel() {
		return EditModel.forValue(this.GetValIntByKey(MapDtlAttr.EditModel));
	}
	public final void setHisEditModel(EditModel value){
		this.SetValByKey(MapDtlAttr.EditModel, value.getValue());
	}
	/**

	*/
	public final WhenOverSize getHisWhenOverSize() {
		return WhenOverSize.forValue(this.GetValIntByKey(MapDtlAttr.WhenOverSize));
	}
	public final void setHisWhenOverSize(WhenOverSize value){
		this.SetValByKey(MapDtlAttr.WhenOverSize, value.getValue());
	}
	/**
	 是否显示数量
	*/
	public final boolean getItIsShowSum()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsShowSum);
	}
	public final void setItIsShowSum(boolean value){
		this.SetValByKey(MapDtlAttr.IsShowSum, value);
	}
	/**
	 是否显示Idx
	*/
	public final boolean getItIsShowIdx()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsShowIdx);
	}
	public final void setItIsShowIdx(boolean value){
		this.SetValByKey(MapDtlAttr.IsShowIdx, value);
	}
	/**
	 是否显示标题
	*/
	public final boolean getItIsShowTitle()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsShowTitle);
	}
	public final void setItIsShowTitle(boolean value){
		this.SetValByKey(MapDtlAttr.IsShowTitle, value);
	}
	/**
	 是否是合流汇总数据
	*/
	public final boolean getItIsHLDtl()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsHLDtl);
	}
	public final void setItIsHLDtl(boolean value){
		this.SetValByKey(MapDtlAttr.IsHLDtl, value);
	}
	/**
	 是否是分流
	*/
	public final boolean getItIsFLDtl() {
		return this.GetParaBoolen(MapDtlAttr.IsFLDtl);
	}
	public final void setItIsFLDtl(boolean value)  {
		this.SetPara(MapDtlAttr.IsFLDtl, value);
	}
	/**
	 是否只读？
	*/
	public final boolean getItIsReadonly() throws Exception {

		if (this.getItIsInsert() == false && this.getItIsUpdate() == false && this.getItIsDelete() == false)
		{
			return true;
		}

		return this.GetValBooleanByKey(MapDtlAttr.IsReadonly);
	}
	public final void setItIsReadonly(boolean value){
		this.SetValByKey(MapDtlAttr.IsReadonly, value);
	}
	/**
	 是否可以删除？
	*/
	public final boolean getItIsDelete()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsDelete);
	}
	public final void setItIsDelete(boolean value){
		this.SetValByKey(MapDtlAttr.IsDelete, value);
	}
	/**
	 是否可以新增？
	*/
	public final boolean getItIsInsert()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsInsert);
	}
	public final void setItIsInsert(boolean value){
		this.SetValByKey(MapDtlAttr.IsInsert, value);
	}
	/**
	 是否可见
	*/
	public final boolean getItIsView()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsView);
	}
	public final void setItIsView(boolean value){
		this.SetValByKey(MapDtlAttr.IsView, value);
	}
	/**
	 是否可以更新？
	*/
	public final boolean getItIsUpdate()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsUpdate);
	}
	public final void setItIsUpdate(boolean value){
		this.SetValByKey(MapDtlAttr.IsUpdate, value);
	}
	/**
	 是否启用多附件
	*/
	public final boolean getItIsEnableAthM()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsEnableAthM);
	}
	public final void setItIsEnableAthM(boolean value){
		this.SetValByKey(MapDtlAttr.IsEnableAthM, value);
	}
	/**
	 是否启用分组字段
	*/
	public final boolean getItIsEnableGroupField()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsEnableGroupField);
	}
	public final void setItIsEnableGroupField(boolean value){
		this.SetValByKey(MapDtlAttr.IsEnableGroupField, value);
	}
	/**
	 是否起用审核连接
	*/
	public final boolean getItIsEnablePass()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsEnablePass);
	}
	public final void setItIsEnablePass(boolean value){
		this.SetValByKey(MapDtlAttr.IsEnablePass, value);
	}

	/**
	 是否copy数据？
	*/
	public final boolean getItIsCopyNDData()  {
		return this.GetValBooleanByKey(MapDtlAttr.IsCopyNDData);
	}
	public final void setItIsCopyNDData(boolean value){
		this.SetValByKey(MapDtlAttr.IsCopyNDData, value);
	}


	public boolean ItIsUse = false;
	/**
	 是否检查人员的权限
	*/
	public final DtlOpenType getDtlOpenType() {
		return DtlOpenType.forValue(this.GetValIntByKey(MapDtlAttr.DtlOpenType));
	}
	public final void setDtlOpenType(DtlOpenType value){
		this.SetValByKey(MapDtlAttr.DtlOpenType, value.getValue());
	}
	/**
	 分组字段
	*/
	public final String getGroupField()  {
		return this.GetValStrByKey(MapDtlAttr.GroupField);
	}
	public final void setGroupField(String value){
		this.SetValByKey(MapDtlAttr.GroupField, value);
	}
	/**
	 表单ID
	*/
	public final String getFrmID()  {
		return this.GetValStrByKey(MapDtlAttr.FK_MapData);
	}
	public final void setFrmID(String value){
		this.SetValByKey(MapDtlAttr.FK_MapData, value);
	}
	/**
	 从表的模式
	*/
	public final int getPTableModel() {
		return this.GetParaInt("PTableModel");
	}
	public final void setPTableModel(int value)  {
		this.SetPara("PTableModel", value);
	}
	/**
	 数量
	*/
	public final int getRowsOfList() throws Exception {
		//如果不可以插入，就让其返回0.
		if (this.getItIsInsert() == false)
		{
			return 0;
		}

		return this.GetValIntByKey(MapDtlAttr.RowsOfList);
	}
	public final void setRowsOfList(int value){
		this.SetValByKey(MapDtlAttr.RowsOfList, value);
	}
	/**
	 物理表
	*/
	public final String getPTable() throws Exception {
		String s = this.GetValStrByKey(MapDtlAttr.PTable);
		if (DataType.IsNullOrEmpty(s) == true)
		{

			s = this.getNo();
			if (Objects.equals(s.substring(0, 1), "0"))
			{
				return "T" + this.getNo();
			}
			else
			{
				return s;
			}
		}
		else
		{
			if (Objects.equals(s.substring(0, 1), "0"))
			{
				return "T" + this.getNo();
			}
			else
			{
				return s;
			}
		}
	}
	public final void setPTable(String value){
		this.SetValByKey(MapDtlAttr.PTable, value);
	}
	/**
	 过滤的SQL表达式.
	*/
	public final String getFilterSQLExp() throws Exception {
		String s = this.GetValStrByKey(MapDtlAttr.FilterSQLExp);
		if (DataType.IsNullOrEmpty(s) == true)
		{
			return "";
		}
		s = s.replace("~", "'");
		return s.trim();
	}
	public final void setFilterSQLExp(String value){
		this.SetValByKey(MapDtlAttr.FilterSQLExp, value);
	}
	/**
	 排序字段
	*/
	public final String getOrderBySQLExp() throws Exception {
		String s = this.GetValStrByKey(MapDtlAttr.OrderBySQLExp);
		if (DataType.IsNullOrEmpty(s) == true)
		{
			return "";
		}
		s = s.replace("~", "'");
		return s.trim();
	}
	public final void setOrderBySQLExp(String value){
		this.SetValByKey(MapDtlAttr.OrderBySQLExp, value);
	}
	/**
	 多表头
	*/
	//public String MTR
	//{
	//    get
	//    {
	//        String s = this.GetValStrByKey(MapDtlAttr.MTR);
	//        s = s.replace("《", "<");
	//        s = s.replace("》", ">");
	//        s = s.replace("‘", "'");
	//        return s;
	//    }
	//    set
	//    {
	//        String s = value;
	//        s = s.replace("<", "《");
	//        s = s.replace(">", "》");
	//        s = s.replace("'", "‘");
	//        this.SetValByKey(MapDtlAttr.MTR, value);
	//    }
	//}
	/**
	 别名
	*/
	public final String getAlias()  {
		return this.GetValStrByKey(MapDtlAttr.Alias);
	}
	public final void setAlias(String value){
		this.SetValByKey(MapDtlAttr.Alias, value);
	}

		///#endregion


		///#region 构造方法
	public final Map GenerMap() throws Exception {
		boolean isdebug = bp.difference.SystemConfig.isDebug();

		if (isdebug == false)
		{
			Map m = Cache.GetMap(this.getNo());
			if (m != null)
			{
				return m;
			}
		}

		MapAttrs mapAttrs = this.getMapAttrs();
		Map map = new Map(this.getPTable(), this.getName());

		Attrs attrs = new Attrs();
		for (MapAttr mapAttr : mapAttrs.ToJavaList())
		{
			map.AddAttr(mapAttr.getHisAttr());
		}

		Cache.SetMap(this.getNo(), map);
		return map;
	}
	public final GEDtl getHisGEDtl() throws Exception {
		GEDtl dtl = new GEDtl(this.getNo());
		return dtl;
	}
	public final GEEntity GenerGEMainEntity(String mainPK) throws Exception {
		GEEntity en = new GEEntity(this.getFrmID(), mainPK);
		return en;
	}
	/**
	 明细
	*/
	public MapDtl()
	{
	}
	public MapDtl(String no) throws Exception  {
		this.setNo(no);
		//   this.IsReadonly = 2;
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

		Map map = new Map("Sys_MapDtl", "明细");
		map.IndexField = MapDtlAttr.FK_MapData;

		map.AddTBStringPK(MapDtlAttr.No, null, "编号", true, false, 1, 100, 20);
		map.AddTBString(MapDtlAttr.Name, null, "描述", true, false, 1, 200, 20);
		map.AddTBString(MapDtlAttr.Alias, null, "别名", true, false, 1, 200, 20);
		map.AddTBString(MapDtlAttr.FK_MapData, null, "主表", true, false, 0, 100, 20);
		map.AddTBString(MapDtlAttr.PTable, null, "物理表", true, false, 0, 200, 20);

		map.AddTBString(MapDtlAttr.GroupField, null, "分组字段", true, false, 0, 300, 20);
		map.AddTBString(MapDtlAttr.RefPK, null, "关联的主键", true, false, 0, 100, 20);

		// 为明细表初始化事件类.
		map.AddTBString(MapDtlAttr.FEBD, null, "映射的事件实体类", true, false, 0, 100, 20);

		// @0=普通@1=固定行
		map.AddTBInt(MapDtlAttr.Model, 0, "工作模式", false, false);
		//map.AddDDLSysEnum(MapDtlAttr.Model, 0, "工作模式", true, true,
		//MapDtlAttr.Model, "@0=普通@1=固定行");

		map.AddTBInt(MapDtlAttr.DtlVer, 0, "使用版本", false, false);
		// map.AddDDLSysEnum(MapDtlAttr.DtlVer, 0, "使用版本", true, true, MapDtlAttr.DtlVer, "@0=2017传统版@1=2019EasyUI版本");


		map.AddTBInt(MapDtlAttr.RowsOfList, 0, "初始化行数", false, false);

		map.AddBoolean(MapDtlAttr.IsEnableGroupField, false, "是否启用分组字段", false, false);

		map.AddBoolean(MapDtlAttr.IsShowSum, true, "是否显示合计？", false, false);
		map.AddBoolean(MapDtlAttr.IsShowIdx, true, "是否显示序号？", false, false);
		map.AddBoolean(MapDtlAttr.IsCopyNDData, true, "是否允许Copy数据", false, false);
		map.AddBoolean(MapDtlAttr.IsHLDtl, false, "是否是合流汇总", false, false);

		map.AddBoolean(MapDtlAttr.IsReadonly, false, "是否只读？", false, false);
		map.AddBoolean(MapDtlAttr.IsShowTitle, true, "是否显示标题？", false, false);
		map.AddBoolean(MapDtlAttr.IsView, true, "是否可见", false, false);

		map.AddBoolean(MapDtlAttr.IsInsert, true, "是否可以插入行？", false, false);
		map.AddBoolean(MapDtlAttr.IsDelete, true, "是否可以删除行", false, false);
		map.AddBoolean(MapDtlAttr.IsUpdate, true, "是否可以更新？", false, false);
		map.AddBoolean(MapDtlAttr.IsBatchUpdate, true, "是否可以批量更新？", false, false);
		map.AddBoolean("IsCopyThisData", false, "是否可以复制行数据？", false, false);
		map.AddBoolean(MapDtlAttr.IsEnablePass, false, "是否启用通过审核功能?", false, false);
		map.AddBoolean(MapDtlAttr.IsEnableAthM, false, "是否启用多附件", false, false);

		map.AddBoolean(MapDtlAttr.IsCopyFirstData, false, "是否复制第一行数据？", false, false);
		map.AddTBString(MapDtlAttr.InitDBAttrs, null, "行初始化字段", true, false, 0, 40, 20, false);
		// 超出行数
		map.AddTBInt(MapDtlAttr.WhenOverSize, 0, "列表数据显示格式", false, false);

		//数据开放类型 .
		map.AddTBInt(MapDtlAttr.DtlOpenType, 1, "数据开放类型", false, false);

		map.AddTBInt(MapDtlAttr.ListShowModel, 0, "列表数据显示格式", false, false);
		map.AddTBInt(MapDtlAttr.EditModel, 0, "行数据显示格式", false, false);
		map.AddTBString(MapDtlAttr.UrlDtl, null, "自定义Url", true, false, 0, 200, 20, true);

		map.AddTBString(MapDtlAttr.ColAutoExp, null, "列字段计算", true, false, 0, 200, 20, true);
		map.AddTBInt(MapDtlAttr.MobileShowModel, 0, "移动端数据显示格式", false, false);
		map.AddTBString(MapDtlAttr.MobileShowField, null, "移动端列表显示字段", true, false, 0, 100, 20);

		map.AddTBFloat(MapDtlAttr.H, 150, "高度", true, false);

		map.AddTBFloat(MapDtlAttr.FrmW, 900, "表单宽度", true, true);
		map.AddTBFloat(MapDtlAttr.FrmH, 1200, "表单高度", true, true);
		map.AddTBInt(MapDtlAttr.NumOfDtl, 0, "最小从表集合", true, false);
		//MTR 多表头列.
		//map.AddTBString(MapDtlAttr.MTR, null, "多表头列", true, false, 0, 3000, 20);

			///#region 超链接.
		map.AddBoolean(MapDtlAttr.IsEnableLink, false, "是否启用超链接", true, true);
		map.AddTBString(MapDtlAttr.LinkLabel, "", "超连接标签", true, false, 0, 50, 100);
		map.AddTBString(MapDtlAttr.LinkTarget, null, "连接目标", true, false, 0, 10, 100);
		map.AddTBString(MapDtlAttr.LinkUrl, null, "连接URL", true, false, 0, 200, 200, true);

		map.AddBoolean(MapDtlAttr.IsEnableLink2, false, "是否启用超链接", true, true);
		map.AddTBString(MapDtlAttr.LinkLabel2, "", "超连接标签", true, false, 0, 50, 100);
		map.AddTBString(MapDtlAttr.LinkTarget2, null, "连接目标", true, false, 0, 10, 100);
		map.AddTBString(MapDtlAttr.LinkUrl2, null, "连接URL", true, false, 0, 200, 200, true);

		///#endregion 超链接.

		//SQL过滤表达式.
		map.AddTBString(MapDtlAttr.FilterSQLExp, null, "过滤SQL表达式", true, false, 0, 70, 20, true);
		map.AddTBString(MapDtlAttr.OrderBySQLExp, null, "排序字段", true, false, 0, 70, 20, true);

		//add 2014-02-21.
		map.AddTBInt(MapDtlAttr.FK_Node, 0, "节点(用户独立表单权限控制)", false, false);

		//要显示的列.
		map.AddTBString(MapDtlAttr.ShowCols, null, "显示的列", true, false, 0, 500, 20, true);
		map.SetHelperAlert(MapDtlAttr.ShowCols, "默认为空,全部显示,如果配置了就按照配置的计算,格式为:field1,field2");

		map.AddTBString("Btns", null, "头部按钮", true, false, 0, 200, 20, true);

			///#region 导入导出填充.
		// 2014-07-17 for xinchang bank.
		map.AddBoolean(MapDtlAttr.IsExp, true, "IsExp", false, false);
		map.AddTBInt(MapDtlAttr.ImpModel, 0, "导入规则", false, false);

		// map.AddBoolean(MapDtlAttr.IsImp, true, "IsImp", false, false);
		// map.AddBoolean(MapDtlAttr.IsEnableSelectImp, false, "是否启用选择数据导入?", false, false);
		map.AddTBString(MapDtlAttr.ImpSQLSearch, null, "查询SQL", true, false, 0, 500, 20);
		map.AddTBString(MapDtlAttr.ImpSQLInit, null, "初始化SQL", true, false, 0, 500, 20);
		map.AddTBString(MapDtlAttr.ImpSQLFullOneRow, null, "数据填充SQL", true, false, 0, 500, 20);
		map.AddTBString(MapDtlAttr.ImpSQLNames, null, "字段中文名", true, false, 0, 900, 20);
		map.AddBoolean(MapDtlAttr.IsImp, false, "IsImp", true, true);

			///#endregion 导入导出填充.

		map.AddTBString(MapDtlAttr.GUID, null, "GUID", false, false, 0, 128, 20);

		//参数.
		map.AddTBAtParas(300);

		this.set_enMap(map);
		return this.get_enMap();
	}


		///#region 基本属性.


	public final float getH()  {
		return this.GetValFloatByKey(MapDtlAttr.H);
	}
	public final void setH(float value){
		this.SetValByKey(MapDtlAttr.H, value);
	}
	public final float getFrmW()  {
		return this.GetValFloatByKey(MapDtlAttr.FrmW);
	}
	public final float getFrmH()  {
		return this.GetValFloatByKey(MapDtlAttr.FrmH);
	}

		///#endregion 基本属性.

	/**
	 获取个数

	 @param workID
	 @return
	*/
	public final int GetCountByFK(int workID) throws Exception {
		return DBAccess.RunSQLReturnValInt("select COUNT(OID) from " + this.getPTable() + " WHERE WorkID=" + workID);
	}
	public final int GetCountByFK(String field, String val) throws Exception {
		return DBAccess.RunSQLReturnValInt("select COUNT(OID) from " + this.getPTable() + " WHERE " + field + "='" + val + "'");
	}
	public final int GetCountByFK(String field, long val) throws Exception {
		return DBAccess.RunSQLReturnValInt("select COUNT(OID) from " + this.getPTable() + " WHERE " + field + "=" + val);
	}
	public final int GetCountByFK(String f1, long val1, String f2, String val2) throws Exception {
		return DBAccess.RunSQLReturnValInt("SELECT COUNT(OID) from " + this.getPTable() + " WHERE " + f1 + "=" + val1 + " AND " + f2 + "='" + val2 + "'");
	}

		///#endregion

	public final void IntMapAttrs() throws Exception {
		bp.sys.MapData md = new bp.sys.MapData();
		md.setNo(this.getNo());
		if (md.RetrieveFromDBSources() == 0)
		{
			md.setName(this.getName());
		  //  md.setPTable(this.PTable;
			md.DirectInsert();
		}

		MapAttrs attrs = new MapAttrs(this.getNo());
		bp.sys.MapAttr attr = new bp.sys.MapAttr();
		if (attrs.contains(MapAttrAttr.KeyOfEn, "OID") == false)
		{
			attr = new bp.sys.MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.Readonly);

			attr.setKeyOfEn("OID");
			attr.setName("主键");
			attr.setMyDataType(DataType.AppInt);

			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setDefVal("0");
			attr.Insert();
		}

		if (attrs.contains(MapAttrAttr.KeyOfEn, "RefPK") == false)
		{
			attr = new bp.sys.MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.Readonly);

			attr.setKeyOfEn("RefPK");
			attr.setName("关联ID");
			attr.setMyDataType(DataType.AppString);

			attr.setMyDataType(DataType.AppString);

			attr.setMyDataType(DataType.AppString);

			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setDefVal("0");
			attr.Insert();
		}

		if (attrs.contains(MapAttrAttr.KeyOfEn, "FID") == false)
		{
			attr = new bp.sys.MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.Readonly);

			attr.setKeyOfEn("FID");
			attr.setName("FID");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setDefVal("0");
			attr.Insert();
		}

		if (attrs.contains(MapAttrAttr.KeyOfEn, "RDT") == false)
		{
			attr = new bp.sys.MapAttr();
			attr.setFrmID(this.getNo());
			attr.setHisEditType(EditType.UnDel);

			attr.setKeyOfEn("RDT");
			attr.setName("记录时间");
			attr.setMyDataType(DataType.AppDateTime);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setTag("1");
			attr.Insert();
		}

		if (attrs.contains(MapAttrAttr.KeyOfEn, "Rec") == false)
		{
			attr = new bp.sys.MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.Readonly);

			attr.setKeyOfEn("Rec");
			attr.setName("记录人");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMaxLen(20);
			attr.setMinLen(0);
			attr.setDefVal("@WebUser.No");
			attr.setTag("@WebUser.No");
			attr.Insert();
		}
		if (attrs.contains(MapAttrAttr.KeyOfEn, "Idx") == false)
		{
			attr = new bp.sys.MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.Readonly);

			attr.setKeyOfEn("Idx");
			attr.setName("Idx");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setDefVal("0");
			attr.Insert();
		}

	}
	private void InitExtMembers() throws Exception {
		/* 如果启用了多附件*/
		if (this.getItIsEnableAthM() == true)
		{
			bp.sys.FrmAttachment athDesc = new bp.sys.FrmAttachment();
			athDesc.setMyPK(this.getNo() + "_AthMDtl");
			if (athDesc.RetrieveFromDBSources() == 0)
			{
				athDesc.setFrmID(this.getNo());
				athDesc.setNoOfObj("AthMDtl");
				athDesc.setName(this.getName());
				athDesc.DirectInsert();
				//增加分组
				GroupField group = new GroupField();
				group.setLab(athDesc.getName());
				group.setFrmID(this.getNo());
				group.setCtrlType("Ath");
				group.setCtrlID(athDesc.getMyPK());
				group.setIdx(10);
				group.Insert();
			}
		}

	}

	public final String ChangeMapAttrIdx(String mypkArr)
	{
		String[] mypks = mypkArr.split("[,]", -1);
		for (int i = 0; i < mypks.length; i++)
		{
			String mypk = mypks[i];
			if (mypk == null || Objects.equals(mypk, ""))
			{
				continue;
			}

			String sql = "UPDATE Sys_MapAttr SET Idx=" + i + " WHERE MyPK='" + mypk + "' ";
			DBAccess.RunSQL(sql);
		}
		return "移动成功..";
	}

	@Override
	protected boolean beforeInsert() throws Exception
	{
		//在属性实体集合插入前，clear父实体的缓存.
		bp.sys.base.Glo.ClearMapDataAutoNum(this.getFrmID());


		GroupField gf = new GroupField();
		if (gf.IsExit(GroupFieldAttr.CtrlID, this.getNo()) == false)
		{
			gf.setFrmID(this.getFrmID());
			gf.setCtrlID(this.getNo());
			gf.setCtrlType("Dtl");
			gf.setLab(this.getName());
			gf.setIdx(0);
			gf.Insert(); //插入.
			this.setGroupField(String.valueOf(gf.getOID()));
		}

		if (DataType.IsNullOrEmpty(this.getPTable()) == true && this.getNo().contains("ND") == true)
		{
			if (this.getNo().contains("01Dtl") == false)
			{
				String ptable = this.getNo().substring(0, this.getNo().indexOf("01Dtl")) + this.getNo().substring(this.getNo().indexOf("01Dtl"));
				this.setPTable(ptable);
			}
		}


		return super.beforeInsert();
	}
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		this.InitExtMembers();

		bp.sys.MapData md = new bp.sys.MapData();
		md.setNo(this.getNo());
		if (md.RetrieveFromDBSources() == 0)
		{
			md.setName(this.getName());
			md.Insert();
		}

		if (this.getItIsRowLock() == true)
		{
			/*检查是否启用了行锁定.*/
			MapAttrs attrs = new MapAttrs(this.getNo());
			if (attrs.contains(MapAttrAttr.KeyOfEn, "IsRowLock") == false)
			{
				throw new RuntimeException("您启用了从表单(" + this.getName() + ")行数据锁定功能，但是该从表里没IsRowLock字段，请参考帮助文档。");
			}
		}

		if (this.getItIsEnablePass())
		{
			/*判断是否有IsPass 字段。*/
			MapAttrs attrs = new MapAttrs(this.getNo());
			if (attrs.contains(MapAttrAttr.KeyOfEn, "IsPass") == false)
			{
				throw new RuntimeException("您启用了从表单(" + this.getName() + ")条数据审核选项，但是该从表里没IsPass字段，请参考帮助文档。");
			}
		}
		return super.beforeUpdateInsertAction();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		if (this.getNo().equals(this.getFrmID()) == true)
		{
			throw new RuntimeException("err@从表的No不能与FK_MapData字段相等." + this.getNo());
		}
		int listShowModel = this.GetValIntByKey(MapDtlAttr.ListShowModel);
		//判断二维、三维是否增加了IsEnable的字段
		boolean isExist = false;
		MapAttrs mattrs = new MapAttrs(this.getNo());
		boolean isHaveEnable = false;
		for (MapAttr attr : mattrs.ToJavaList())
		{
			if (attr.getKeyOfEn().equals("IsEnable"))
				isExist = true;
			if (attr.getUIIsEnable() && attr.getUIContralType() == UIContralType.TB)
				isHaveEnable = true;
		}
		if(isExist == false && (listShowModel == 3 || listShowModel == 4 || listShowModel == 5))
		{
			MapAttr attr = (MapAttr)mattrs.get(0);
			attr.setMyPK(attr.getFrmID() + "_IsEnable");
			attr.setKeyOfEn("IsEnable");
			attr.setName("是否可用");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIContralType(UIContralType.CheckBok);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.Insert();
		}
		this.InitExtMembers();

		//更新MapData中的名称
		bp.sys.MapData md = new bp.sys.MapData();
		md.setNo(this.getNo());

		//获得事件实体.
		FormEventBaseDtl febd = bp.sys.base.Glo.GetFormDtlEventBaseByEnName(this.getNo());
		if (febd == null)
		{
			this.setFEBD("");
		}
		else
		{
			this.setFEBD(febd.toString());
		}

		if (this.getPTable().length() == 0)
		{
			this.setPTable(this.getNo());
		}

		if (md.RetrieveFromDBSources() == 1)
		{
			md.setName(this.getName());
			md.setPTable(this.getPTable());
			//避免在表单库中显示
			md.setFormTreeNo("");
			md.Update(); //需要更新到缓存.
		}

		return super.beforeUpdate();
	}
	@Override
	protected boolean beforeDelete() throws Exception
	{
		String sql = "";
		sql += "@DELETE FROM Sys_FrmImg WHERE FK_MapData='" + this.getNo() + "'";
		sql += "@DELETE FROM Sys_FrmImgAth WHERE FK_MapData='" + this.getNo() + "'";
		sql += "@DELETE FROM Sys_FrmRB WHERE FK_MapData='" + this.getNo() + "'";
		sql += "@DELETE FROM Sys_FrmAttachment WHERE FK_MapData='" + this.getNo() + "'";
		sql += "@DELETE FROM Sys_MapFrame WHERE FK_MapData='" + this.getNo() + "'";

		if (this.getNo().contains("BP.") == false)
		{
			sql += "@DELETE FROM Sys_MapExt WHERE FK_MapData='" + this.getNo() + "'";
		}

		sql += "@DELETE FROM Sys_MapAttr WHERE FK_MapData='" + this.getNo() + "'";
		sql += "@DELETE FROM Sys_MapData WHERE No='" + this.getNo() + "'";
		sql += "@DELETE FROM Sys_GroupField WHERE FrmID='" + this.getNo() + "'";
		sql += "@DELETE FROM Sys_GroupField WHERE CtrlID='" + this.getNo() + "'";
		DBAccess.RunSQLs(sql);

		if (DBAccess.IsExitsObject(this.getPTable()) && this.getPTable().indexOf("ND") == 0)
		{
			//如果其他表单引用了该表，就不能删除它.
			sql = "SELECT COUNT(No) AS NUM  FROM Sys_MapData WHERE PTable='" + this.getPTable() + "' OR ( PTable='' AND No='" + this.getPTable() + "')";
			int i = DBAccess.RunSQLReturnValInt(sql, 0);

			sql = "SELECT COUNT(No) AS NUM  FROM Sys_MapDtl WHERE PTable='" + this.getPTable() + "' OR ( PTable='' AND No='" + this.getPTable() + "')";
			i += DBAccess.RunSQLReturnValInt(sql, 0);
			if (i >= 1)
			{
				/* 说明有多个表单在引用.就不删除物理*/
			}
			else
			{
				// edit by zhoupeng 误删已经有数据的表.
				if (DBAccess.RunSQLReturnValInt("SELECT COUNT(*) FROM " + this.getPTable() + " WHERE 1=1 ") == 0)
				{
					DBAccess.RunSQL("DROP TABLE " + this.getPTable());
				}
			}
		}

		//执行清空缓存到的AutoNum.
		MapData md = new MapData(this.getFrmID());
		md.ClearAutoNumCache(true); //更新缓存.
		return super.beforeDelete();
	}
}
