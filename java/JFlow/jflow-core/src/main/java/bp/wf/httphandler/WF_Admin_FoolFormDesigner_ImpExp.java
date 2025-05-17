package bp.wf.httphandler;

import bp.ccbill.template.GroupMethod;
import bp.ccbill.template.MethodFlowBaseData;
import bp.ccbill.template.MethodPrintRTF;
import bp.difference.handler.CommonFileUtils;
import bp.sys.*;
import bp.da.*;
import bp.en.*;
import bp.ccbill.*;
import bp.difference.*;
import bp.*;
import bp.sys.CCFormAPI;
import bp.wf.*;
import bp.wf.Glo;
import net.sf.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map;

public class WF_Admin_FoolFormDesigner_ImpExp extends bp.difference.handler.DirectoryPageBase
{
	/**
	 构造函数
	*/
	public WF_Admin_FoolFormDesigner_ImpExp()
	{
	}


		///#region 导出.
	/**
	下载

	 @return
	*/
	public final String Exp_DownFormTemplete() throws Exception {
		bp.wf.httphandler.WF_Admin_CCBPMDesigner en = new WF_Admin_CCBPMDesigner();
		return en.DownFormTemplete();
	}

		///#endregion


		///#region 导入
	/**
	 初始化 导入的界面 .

	 @return
	*/
	public final String Imp_Init() throws Exception {
		DataSet ds = new DataSet();

		String sql = "";
		DataTable dt;

		if (this.getFlowNo() != null)
		{
			//加入节点表单. 如果没有流程参数.

			Paras ps = new Paras();
			ps.SQL = "SELECT NodeID, Name  FROM WF_Node WHERE FK_Flow=" + SystemConfig.getAppCenterDBVarStr() + "FK_Flow ORDER BY NODEID ";
			ps.Add("FK_Flow", this.getFlowNo(), false);
			dt = DBAccess.RunSQLReturnTable(ps);

			dt.TableName = "WF_Node";

			if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
			{
				dt.Columns.get("NODEID").ColumnName = "NodeID";
				dt.Columns.get("NAME").ColumnName = "Name";
			}

			if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
			{
				dt.Columns.get("nodeid").ColumnName = "NodeID";
				dt.Columns.get("name").ColumnName = "Name";
			}


			ds.Tables.add(dt);
		}


			///#region 加入表单库目录.

		sql = "SELECT No,Name,ParentNo FROM Sys_FormTree ORDER BY  PARENTNO, IDX ";

		dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Sys_FormTree";

		dt.Columns.get(0).ColumnName = "No";
		dt.Columns.get(1).ColumnName = "Name";
		dt.Columns.get(2).ColumnName = "ParentNo";
		ds.Tables.add(dt);

		//加入表单
		sql = "SELECT A.No, A.Name, A.FK_FormTree  FROM Sys_MapData A, Sys_FormTree B WHERE A.FK_FormTree=B.No";
		dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Sys_MapData";
		ds.Tables.add(dt);

		dt.Columns.get(0).ColumnName = "No";
		dt.Columns.get(1).ColumnName = "Name";
		dt.Columns.get(2).ColumnName = "FK_FormTree";


			///#endregion 加入表单库目录.


			///#region 加入流程树目录.
		sql = "SELECT No,Name,ParentNo FROM WF_FlowSort ORDER BY  PARENTNO, IDX ";

		dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "WF_FlowSort";
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("NO").ColumnName = "No";
			dt.Columns.get("NAME").ColumnName = "Name";
			dt.Columns.get("PARENTNO").ColumnName = "ParentNo";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("no").ColumnName = "No";
			dt.Columns.get("name").ColumnName = "Name";
			dt.Columns.get("parentno").ColumnName = "ParentNo";
		}

		ds.Tables.add(dt);

		//加入表单
		sql = "SELECT No, Name, FK_FlowSort  FROM WF_Flow ";
		dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "WF_Flow";
		ds.Tables.add(dt);
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("NO").ColumnName = "No";
			dt.Columns.get("NAME").ColumnName = "Name";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("no").ColumnName = "No";
			dt.Columns.get("name").ColumnName = "Name";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
		}

			///#endregion 加入流程树目录.


			///#region 数据源
		SFDBSrcs ens = new SFDBSrcs();
		ens.RetrieveAll();
		ds.Tables.add(ens.ToDataTableField("SFDBSrcs"));

			///#endregion

		//加入系统表.
		return bp.tools.Json.ToJson(ds);
	}

		///#endregion 如果是单据.

	public void Imp_ExcelFileExt(String frmID, String name, long groupID) throws Exception {
		if (name.equals("发起时间")
				|| name.equals("标题")
				|| name.equals("发起人工号")
				|| name.equals("耗时(时:分:秒)"))
		{
			return;
		}

		if (name.contains("附件") == true)
		{
			String fjID = bp.da.DataType.ParseStringToPinyin(name);
			bp.sys.CCFormAPI.CreateOrSaveAthMulti(frmID, fjID, name);
			return;
		}

		String myName = name;
		if (name.contains(".") == true)
		{
			myName = name.substring(0, name.indexOf("."));
		}

		MapAttr attr = new MapAttr();
		int i = attr.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), "Name", myName);
		if (i == 1)
			return;

		attr.SetValByKey("FK_MapData", frmID);
		attr.SetValByKey("Name", myName);
		attr.SetValByKey("MyDataType,", DataType.AppString);
		attr.SetValByKey("GroupID", groupID);
		attr.SetValByKey("KeyOfEn", bp.da.DataType.ParseStringToPinyin(myName));

		if (name.contains("时间") == true)
		{
			attr.SetValByKey("MyDataType", DataType.AppDateTime);
		}

		if (name.contains("日期") == true)
		{
			attr.SetValByKey("MyDataType", DataType.AppDate);
		}

		if (name.toLowerCase().contains(".int") == true)
		{
			attr.SetValByKey("MyDataType", DataType.AppInt);
		}

		if (name.toLowerCase().contains(".float") == true)
		{
			attr.SetValByKey("MyDataType", DataType.AppFloat);
		}

		if (name.toLowerCase().contains(".je") == true)
		{
			attr.SetValByKey("MyDataType", DataType.AppMoney);
		}
		attr.SetValByKey("MyPK", attr.getFrmID() + "_" + attr.getKeyOfEn());
		attr.Save();
	}
	public String Imp_ExcelFileds() throws Exception {
		String frmID = this.getFrmID();
		String model = this.GetRequestVal("Model");

		//创建临时文件.
		String temp = bp.difference.SystemConfig.getPathOfTemp() + DBAccess.GenerGUID() + ".xlsx";
		HttpServletRequest request = getRequest();
		try {
			CommonFileUtils.upload(request, "file", new File(temp));
		} catch (Exception e) {
			e.printStackTrace();
			return "err@执行失败";
		}

		//获得datatable.
		DataTable dt = DBLoad.ReadExcelFileToDataTable(temp, 0);

		DataTable mydt = new DataTable();
		mydt.Columns.Add("No");
		mydt.Columns.Add("Name");

		for (DataColumn dc : dt.Columns)
		{
			DataRow mydr = mydt.NewRow();
			mydr.setValue("No", dc.ColumnName);
			mydr.setValue("Name", dc.ColumnName);
			mydt.Rows.add(mydr);
		}
		return bp.tools.Json.ToJson(mydt);
	}

	/**
	 * 导入excel模板
	 * @return
	 */
	public String Imp_ExcelFileSaveIt()
	{
		try
		{
			HttpServletRequest request = getRequest();
			MultipartHttpServletRequest mrequest = CommonFileUtils.getMultipartHttpServletRequest(request);
			MultipartFile requestFile = mrequest.getFile("file");
			if (requestFile == null) {
				return "err@请上传导入的模板文件.";
			}
//			if (HttpContextHelper.RequestFilesCount == 0)
//				return "err@请上传导入的模板文件.";

			GroupFields gfs = new GroupFields();
			gfs.Retrieve("FrmID", this.getFrmID(), "Idx");
			long gfID = 0;
			if (gfs.size() == 0)
			{
				GroupField gf = new GroupField();
				gf.setFrmID(this.getFrmID());
				gf.setLab("基础信息");
				gf.Insert();
				gfID = gf.getOID();
			}
			else
			{
				for (GroupField item : gfs.ToJavaList())
				{
					if (DataType.IsNullOrEmpty(item.getCtrlType()) == true)
						gfID = item.getOID();
				}
			}

			String[] strs = this.GetRequestVal("Fields").split(",");

			String model = "0";
            ///#region 如果模式== 0 ，按照列名导入.
			if (model.equals("0") == true)
			{
				//便利列.
				for (String str : strs)
				{
					Imp_ExcelFileExt(this.getFrmID(), str, gfID);
				}
				return "执行成功.";
			}
            ///#endregion 如果模式== 0 ，按照列名导入.

            ///#region 如果模式== 1 ，按照内容导入.
			////便利行，获得每个列的字段名.
			//foreach (DataRow dr in dt.Rows)
			//{
			//    foreach (DataColumn dc in dt.Columns)
			//    {
			//        String name = dr[dc.ColumnName] as String;
			//        if (DataType.IsNullOrEmpty(name) == true)
			//            continue;

			//        Imp_ExcelFileExt(this.FrmID, name, gfID);
			//    }
			//}
            ///#endregion 如果模式== 0 ，按照内容导入.

			return "导入成功.";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 * 导入word模板
	 *   1.只有经典，章节2种表单
	 *   2.导入表单后再生成rtf模版
	 * @return
	 */
	public String Imp_WordFileSaveIt() {
		try {
			HttpServletRequest request = getRequest();
			MultipartHttpServletRequest mrequest = CommonFileUtils.getMultipartHttpServletRequest(request);
			MultipartFile requestFile = mrequest.getFile("file");
			if (requestFile == null) {
				return "err@请上传导入的模板文件.";
			}
			//创建临时文件.
			String temp = SystemConfig.getPathOfTemp() + "/" + DBAccess.GenerGUID() + ".docx";
			try {
				CommonFileUtils.upload(request, "file", new File(temp));
			} catch (Exception e) {
				e.printStackTrace();
				return "err@执行失败";
			}
			//表单类型
			String FrmModel = this.GetRequestVal("FrmModel");
			String FrmID = this.GetRequestVal("FrmID");
			//获取文件的名称
			String fullfileName =requestFile.getOriginalFilename();
			StringBuilder rtfAddr=new StringBuilder("");
			//获得数据类型.
			Glo glo = new Glo();
			List<Map<String, Object>> list = glo.execWord(temp, FrmModel,FrmID,rtfAddr);
			if (list == null || list.size() == 0) {
				return "word内容为空";
			}
			execWordForData(list,FrmID);
			exec2rftAfter(FrmID,rtfAddr,fullfileName);
			return "导入成功.";
		} catch (Exception ex) {
			ex.printStackTrace();
			return "err@" + ex.getMessage();
		}
	}

	/**
	 * rtf文件生成后的操作
	 *   1.创建实体（GroupMethod）
	 *   2.创建实体（Method）
	 *   3.更新实体(文件上传前后一块更新) （MethodPrintRTF）
	 */
	private void exec2rftAfter(String frmID,StringBuilder rtfAddr,String fullfileName) throws Exception {
		//获取GroupID
		GroupMethod gmEn = new GroupMethod();
		gmEn.setFrmID(frmID);
		if(gmEn.RetrieveFromDBSources() == 0){
			gmEn.SetValByKey("Name","相关功能");
			gmEn.SetValByKey("icon","icon-folder");
			gmEn.SetValByKey("Idx","100");
			gmEn.Insert();
		}
		String groupId = gmEn.getNo();

		//插入打印Method
		bp.ccbill.template.Method en = new bp.ccbill.template.Method();
		en.setPKVal(frmID + "_PrintRTF");
		if(en.RetrieveFromDBSources() == 0){
			en.SetValByKey("Name", "RTF报告");
			en.SetValByKey("GroupID", groupId);
			en.SetValByKey("MethodModel", "PrintRTF");
			en.SetValByKey("Tag1", "PrintRTF");
			en.SetValByKey("FrmID", frmID);
			en.SetValByKey("WhatAreYouTodo", 0);
			en.SetValByKey("WhatAreYouTodoText", "关闭提示窗口");
			en.SetValByKey("Icon", "icon-printer");
			en.SetValByKey("ShowModel", 0);
			en.SetValByKey("PopHeight", 0);
			en.SetValByKey("IsMyBillToolBar", 1);
			en.SetValByKey("IsMyBillToolExt", 0);
			en.SetValByKey("IsSearchBar", 0);
			en.SetValByKey("DTSDataWay", 0);
			en.SetValByKey("DTSDataWayText", "不同步");
			en.SetValByKey("DTSWhenFlowOver", 0);
			en.SetValByKey("DTSWhenNodeOver", 0);
			en.SetValByKey("AtPara", "@EnName=TS.CCBill.MethodPrintRTF");
			en.Insert();
		}

		//设置打印模版
		MethodPrintRTF en2 = new MethodPrintRTF();
		en2.setPKVal(frmID + "_PrintRTF");
		if(en2.RetrieveFromDBSources() != 0){
			en2.SetValByKey("MyFileName", fullfileName.replace(".docx",".rtf"));
			en2.SetValByKey("MyFilePath", rtfAddr.toString());
			en2.SetValByKey("MyFileExt", "rtf");
			en2.SetValByKey("WebPath",rtfAddr.toString().substring(rtfAddr.toString().indexOf("DataUser")-1));
			en2.Update();
		}

	}
	private  void execWordForData(List<Map<String, Object>> list ,String FrmID) throws Exception {
		int idx = 0;
		for (Map<String, Object> m : list) {
			//分组基础信息
			Map<String, Object> ctrlInfo = (Map<String, Object>) m.get("ctrlInfo");
			//分组类型
			String ctrlTYpe = (String) m.get("ctrlTYpe");
			//分组value
			List<Map<String, Object>> ctrlValue = (List<Map<String, Object>>) m.get("ctrlValue");

			GroupField gf = new GroupField();
			gf.setFrmID(FrmID);
			gf.setLab(ctrlInfo.get("name").toString());

			//主表字段
			if (ctrlTYpe.equals("pt")) {
				gf.setCtrlType("");
				gf.setIdx(idx);
				gf.DirectInsert();
				//插入值
				for (Map<String, Object> ptm : ctrlValue) {
					MapAttr ma = new MapAttr();
					ma.setFrmID(FrmID);
					ma.setName(ptm.get("name").toString());
					ma.setMyPK(FrmID + "_" + ptm.get("KeyOfEn").toString());
					ma.setKeyOfEn(ptm.get("KeyOfEn").toString());
					ma.setMyDataType(DataType.AppString);
					ma.setGroupID(gf.getOID());
					ma.setMaxLen(50);
					if (ma.getIsExits()==false)
						ma.Insert();
					else {
						ma.Update();
					}
				}
			} else if (ctrlTYpe.equals("Dtl")) { //从表
				String dtlNo=ctrlInfo.get("KeyOfEn").toString();
				//创建从表
				CCFormAPI.CreateOrSaveDtl(FrmID, dtlNo, ctrlInfo.get("name").toString());
				//插入值
				for (Map<String, Object> ptm : ctrlValue) {
					MapAttr ma = new MapAttr();
					ma.setFrmID(dtlNo);
					ma.setName(ptm.get("name").toString());
					ma.setMyPK(dtlNo + "_" + ptm.get("KeyOfEn").toString());
					ma.setKeyOfEn(ptm.get("KeyOfEn").toString());
					ma.setMyDataType(DataType.AppString);
					if (ma.getIsExits()==false)
						ma.Insert();
				}
			} else if (ctrlTYpe.equals("Ath")) { //表格字段附件
				String no=ctrlInfo.get("KeyOfEn").toString();
				CCFormAPI.CreateOrSaveAthMulti(FrmID, no, ctrlInfo.get("name").toString());
				FrmAttachment en=new FrmAttachment(FrmID+ "_" +no);
				en.SetValByKey("FileType",1);
				en.Update();
			}
			idx++;
		}
	}
		/**
	 * 从本机装载表单模版
 	 * @return
	 * @throws Exception
	 */
	public final String Imp_LoadFrmTempleteFromLocalFile() throws Exception {
		File xmlFile = null;
		String fileName = UUID.randomUUID().toString();
		try {
			xmlFile = File.createTempFile(fileName, ".xml");
		} catch (IOException e1) {
			xmlFile = new File(System.getProperty("java.io.tmpdir"), fileName + ".xml");
		}
		xmlFile.deleteOnExit();
		HttpServletRequest request = ContextHolderUtils.getRequest();
		try {
			CommonFileUtils.upload(request, "file", xmlFile);
		} catch (Exception e) {
			e.printStackTrace();
			return "err@执行失败";
		}

		try
		{
			String fk_mapData = this.getFrmID();
			DataSet ds = new DataSet();
			ds.readXml(xmlFile.getAbsolutePath());

			//执行装载.
			MapData.ImpMapData(fk_mapData, ds);
			if (this.getNodeID() != 0)
			{
				Node nd = new Node(this.getNodeID());
				nd.RepareMap(nd.getHisFlow());
			}
			//清空缓存
			MapData mymd = new MapData(fk_mapData);
			mymd.RepairMap();
			if (mymd.getHisEntityType() == EntityType.FrmBill.getValue())
			{
				FrmBill bill = new FrmBill(mymd.getNo());
				bill.setEntityType(EntityType.FrmBill);
				bill.setBillNoFormat("ccbpm{yyyy}-{MM}-{dd}-{LSH4}");

				//设置默认的查询条件.
				bill.SetPara("IsSearchKey", 1);
				bill.SetPara("DTSearchWay", 0);
				bill.Update();
				bill.CheckEnityTypeAttrsFor_Bill();
			}


				///#region 如果是实体 EnityNoName .
			if (mymd.getEntityType() == EntityType.FrmDict)
			{
				FrmDict entityDict = new FrmDict(mymd.getNo());
				entityDict.setBillNoFormat("3"); //编码格式.001,002,003.
				entityDict.setBtnNewModel(0);

				//设置默认的查询条件.
				entityDict.SetPara("IsSearchKey", 1);
				entityDict.SetPara("DTSearchWay", 0);

				entityDict.setEntityType(EntityType.FrmDict);

				entityDict.Update();
				entityDict.CheckEnityTypeAttrsFor_Dict();
			}
			if (mymd.getEntityType() == EntityType.FrmEntityNoName)
			{
				FrmEntityNoName entity = new FrmEntityNoName(mymd.getNo());
				entity.setBillNoFormat("4"); //编码格式.001,002,003.
				entity.setBtnNewModel(0);

				//设置默认的查询条件.
				entity.SetPara("IsSearchKey", 1);
				entity.SetPara("DTSearchWay", 0);

				entity.setEntityType(EntityType.FrmEntityNoName);

				entity.Update();
				entity.CheckEnityTypeAttrsFor_EntityNoName();
			}
			SystemConfig.DoClearCache();
			return "执行成功.";
		}
		catch (RuntimeException ex)
		{
			//第一次导入，可能因为没有字段，导致报错，系统会刷新一次，并修复字段
			//所以再执行一次导入
			try
			{
				String fk_mapData = this.getFrmID();

				//读取上传的XML 文件.
				DataSet ds = new DataSet();
				//ds.readXml(path);
				ds.readXml(xmlFile.getAbsolutePath()); //this.context.Request.Files[0].InputStream

				//执行装载.
				MapData.ImpMapData(fk_mapData, ds);

				if (this.getNodeID() != 0)
				{
					Node nd = new Node(this.getNodeID());
					nd.RepareMap(nd.getHisFlow());
				}
				//清空缓存
				MapData mymd = new MapData(fk_mapData);
				mymd.RepairMap();
				if (mymd.getHisEntityType() == EntityType.FrmBill.getValue())
				{
					FrmBill bill = new FrmBill(mymd.getNo());
					bill.setEntityType(EntityType.FrmBill);
					bill.setBillNoFormat("ccbpm{yyyy}-{MM}-{dd}-{LSH4}");

					//设置默认的查询条件.
					bill.SetPara("IsSearchKey", 1);
					bill.SetPara("DTSearchWay", 0);

					bill.Update();
					bill.CheckEnityTypeAttrsFor_Bill();
				}

					///#endregion 如果是单据.


					///#region 如果是实体 EnityNoName .
				if (mymd.getHisEntityType() == EntityType.FrmDict.getValue())
				{
					FrmDict entityDict = new FrmDict(mymd.getNo());
					entityDict.setBillNoFormat("3"); //编码格式.001,002,003.
					entityDict.setBtnNewModel(0);

					//设置默认的查询条件.
					entityDict.SetPara("IsSearchKey", 1);
					entityDict.SetPara("DTSearchWay", 0);

					entityDict.setEntityType(EntityType.FrmDict);

					entityDict.Update();
					entityDict.CheckEnityTypeAttrsFor_Dict();
				}
				if (mymd.getEntityType() == EntityType.FrmEntityNoName)
				{
					FrmEntityNoName entity = new FrmEntityNoName(mymd.getNo());
					entity.setBillNoFormat("4"); //编码格式.001,002,003.
					entity.setBtnNewModel(0);

					//设置默认的查询条件.
					entity.SetPara("IsSearchKey", 1);
					entity.SetPara("DTSearchWay", 0);

					entity.setEntityType(EntityType.FrmEntityNoName);

					entity.Update();
					entity.CheckEnityTypeAttrsFor_EntityNoName();
				}
				SystemConfig.DoClearCache();
				return "执行成功.";
			}
			catch (RuntimeException newex)
			{
				return "err@导入失败:" + newex.getMessage();
			}
		}
	}

	/**
	 从流程上copy表单
	 @徐彪来调用.

	 @return
	*/
	public final String Imp_CopyFromFlow() throws Exception {
		String ndfrm = "ND" + Integer.parseInt(this.getFlowNo()) + "01";
		return Imp_CopyFrm(null, ndfrm);
	}
	public final String Imp_FrmEnsName() throws Exception {

		MapData md = new MapData(this.getFrmID());

		Entity en = ClassFactory.GetEns(this.getEnsName()).getNewEntity();

		Attrs attrs = en.getEnMap().getAttrs();
		for (Attr item : attrs)
		{
			if (item.getItIsPK() == true)
			{
				continue;
			}

			MapAttr mapAttr = item.getToMapAttr();
			mapAttr.setMyPK(this.getFrmID() + "_" + item.getKey());
			mapAttr.setFrmID(this.getFrmID());
			mapAttr.Save();
		}

		return this.getEnsName() + "的属性导入[" + this.getFrmID() + "]成功.";
	}

	/**
	 从表单库导入
	 从节点导入

	 @return
	*/
	public final String Imp_FromsCopyFrm() throws Exception {
		return Imp_CopyFrm();
	}


	public final String Imp_CopyFrm(String toFrmID) throws Exception {
		return Imp_CopyFrm(toFrmID, null);
	}

	public final String Imp_CopyFrm() throws Exception {
		return Imp_CopyFrm(null, null);
	}

	public final String Imp_CopyFrm(String toFrmID, String fromFrmID) throws Exception {
		try
		{
			if (toFrmID == null)
			{
				toFrmID = this.getFrmID();
			}


			String fromMapData = fromFrmID;
			if (fromMapData == null)
			{
				fromMapData = this.GetRequestVal("FromFrmID");
			}

			boolean isClear = this.GetRequestValBoolen("IsClear");
			boolean isSetReadonly = this.GetRequestValBoolen("IsSetReadonly");

			//首先初始化本部门的.
			MapData mymd = new MapData(toFrmID);
			String frmSort = mymd.getFormTreeNo(); //表单类别,防止表单类别冲掉,导致表单树看不到他.


			MapData mdFrom = new MapData(fromMapData);
			DataSet ds = CCFormAPI.GenerHisDataSet_AllEleInfo(mdFrom.getNo());

			MapData.ImpMapData(toFrmID, ds);

			//设置为只读模式.
			if (isSetReadonly == true)
			{
				MapData.SetFrmIsReadonly(toFrmID);
			}

			//清空缓存
			mymd = new MapData(toFrmID);

			// 如果是节点表单，就要执行一次修复，以免漏掉应该有的系统字段。
			if (toFrmID.startsWith("ND") == true)
			{
				String fk_node = toFrmID.replace("ND", "");
				Node nd = new Node(Integer.parseInt(fk_node));
				nd.RepareMap(nd.getHisFlow());

				//设置节点ID.
				mymd.setName(nd.getName());
				mymd.setFormTreeNo("");
				mymd.Update();

				//如果包含ND，就保持附件的从表一致.
				if (fromMapData.indexOf("ND") == 0)
				{
					MapDtls dtls = new MapDtls(fromMapData);

				}


			}
			else
			{
				mymd.setFormTreeNo(frmSort);
				mymd.Update();

			}

			mymd.RepairMap();
			if (mymd.getHisEntityType() == EntityType.FrmBill.getValue())
			{
				FrmBill bill = new FrmBill(mymd.getNo());
				bill.setEntityType(EntityType.FrmBill);
				bill.setBillNoFormat("ccbpm{yyyy}-{MM}-{dd}-{LSH4}");

				//设置默认的查询条件.
				bill.SetPara("IsSearchKey", 1);
				bill.SetPara("DTSearchWay", 0);

				bill.Update();
				bill.CheckEnityTypeAttrsFor_Bill();
			}

				///#endregion 如果是单据.


				///#region 如果是实体 EnityNoName .
			if (mymd.getHisEntityType() == EntityType.FrmDict.getValue())
			{
				FrmDict entityDict = new FrmDict(mymd.getNo());
				entityDict.setBillNoFormat("3"); //编码格式.001,002,003.
				entityDict.setBtnNewModel(0);

				//设置默认的查询条件.
				entityDict.SetPara("IsSearchKey", 1);
				entityDict.SetPara("DTSearchWay", 0);

				entityDict.setEntityType(EntityType.FrmDict);

				entityDict.Update();
				entityDict.CheckEnityTypeAttrsFor_Dict();
			}
			if (mymd.getEntityType() == EntityType.FrmEntityNoName)
			{
				FrmEntityNoName entity = new FrmEntityNoName(mymd.getNo());
				entity.setBillNoFormat("4"); //编码格式.001,002,003.
				entity.setBtnNewModel(0);

				//设置默认的查询条件.
				entity.SetPara("IsSearchKey", 1);
				entity.SetPara("DTSearchWay", 0);

				entity.setEntityType(EntityType.FrmEntityNoName);

				entity.Update();
				entity.CheckEnityTypeAttrsFor_EntityNoName();
			}
			///#endregion

			///#region 尾部处理.
			FrmAttachments aths = new FrmAttachments();
			aths.Retrieve("FK_MapData", toFrmID);
			for (FrmAttachment item : aths.ToJavaList())
			{
				if (item.getUploadType() == AttachmentUploadType.Multi){
					continue;
				}
				DBAccess.RunSQL("DELETE FROM Sys_GroupField WHERE CtrlID='" + item.getMyPK() + "'");
			}
            ///#endregion 尾部处理.

			SystemConfig.DoClearCache();
			return "执行成功.";



		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}


		///#region 04.从外部数据源导入
	/**
	 选择一个数据源，进入步骤2

	 @return
	*/
	public final String Imp_Src_Step2_Init() throws Exception {
		SFDBSrc src = new SFDBSrc(this.GetRequestVal("FK_SFDBSrc"));

		//获取所有的表/视图
		DataTable dtTables = src.GetTables(false);

		return bp.tools.FormatToJson.ToJson(dtTables);
	}

	/**
	 获取表字段

	 @return
	*/
	public final String Imp_Src_Step2_GetColumns() throws Exception {
		DataSet ds = new DataSet();

		//01.当前节点表单已经存在的列
		MapAttrs attrs = new MapAttrs(this.getFrmID());
		ds.Tables.add(attrs.ToDataTableField("MapAttrs"));

		//02.数据源表中的列
		SFDBSrc src = new SFDBSrc(this.GetRequestVal("FK_SFDBSrc"));
		DataTable tableColumns = src.GetColumns(this.GetRequestVal("STable"));
		tableColumns.TableName = "TableColumns";
		ds.Tables.add(tableColumns);

		return bp.tools.Json.ToJson(ds);
	}

	public final String Imp_Src_Step3_Init() throws Exception {
		DataSet ds = new DataSet();

		String SColumns = this.GetRequestVal("SColumns");
		SFDBSrc src = new SFDBSrc(this.GetRequestVal("FK_SFDBSrc"));
		DataTable tableColumns = src.GetColumns(this.GetRequestVal("STable"));

		//01.添加列
		DataTable dt = tableColumns.clone();
		for (DataRow dr : tableColumns.Rows)
		{
			if (SColumns.contains(dr.getValue("no").toString()))
			{
				dt.Rows.add(dr);
			}
		}
		dt.TableName = "Columns";
		ds.Tables.add(dt);

		//02.添加枚举
		SysEnums ens = new SysEnums(MapAttrAttr.MyDataType);
		ds.Tables.add(ens.ToDataTableField("EnumsDataType"));
		ens = new SysEnums(MapAttrAttr.LGType);
		ds.Tables.add(ens.ToDataTableField("EnumsLGType"));

		return bp.tools.Json.ToJson(ds);

	}

	public final String Imp_Src_Step3_Save() throws Exception {

		String hidImpFields = this.GetRequestVal("hidImpFields");
		String[] fields = StringHelper.trimEnd(hidImpFields, ',').split("[,]", -1);

		MapData md = new MapData();
		md.setNo(this.getFrmID());
		md.RetrieveFromDBSources();


		String msg = "导入字段信息:";
		boolean isLeft = true;
		for (int i = 0; i < fields.length; i++)
		{
			String colname = fields[i];

			MapAttr ma = new MapAttr();
			ma.setKeyOfEn(colname);
			ma.setName(this.GetRequestVal("TB_Desc_" + colname));
			ma.setFrmID(this.getFrmID());
			ma.setMyDataType(Integer.parseInt(this.GetRequestVal("DDL_DBType_" + colname)));
			ma.setMaxLen(Integer.parseInt(this.GetRequestVal("TB_Len_" + colname)));
			ma.setUIBindKey(this.GetRequestVal("TB_BindKey_" + colname));
			ma.setMyPK(this.getFrmID() + "_" + ma.getKeyOfEn());
			ma.setLGType(FieldTypeS.Normal);

			if (!Objects.equals(ma.getUIBindKey(), ""))
			{
				SysEnums se = new SysEnums();
				se.Retrieve(SysEnumAttr.EnumKey, ma.getUIBindKey(), null);
				if (!se.isEmpty())
				{
					ma.setMyDataType(DataType.AppInt);
					ma.setLGType(FieldTypeS.Enum);
					ma.setUIContralType(UIContralType.DDL);
				}

				SFTable tb = new SFTable();
				tb.setNo(ma.getUIBindKey());
				if (tb.getIsExits() == true)
				{
					ma.setMyDataType(DataType.AppString);
					ma.setLGType(FieldTypeS.FK);
					ma.setUIContralType(UIContralType.DDL);
				}
			}

			if (ma.getMyDataType() == DataType.AppBoolean)
			{
				ma.setUIContralType(UIContralType.CheckBok);
			}
			if (ma.getIsExits())
			{
				continue;
			}
			ma.Insert();

			msg += "\t\n字段:" + ma.getKeyOfEn() + "" + ma.getName() +"加入成功.";

			isLeft = !isLeft;
		}
		return msg;

	}

		///#endregion


}
