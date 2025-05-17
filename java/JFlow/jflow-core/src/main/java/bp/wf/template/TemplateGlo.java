package bp.wf.template;

import bp.da.*;
import bp.sys.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.difference.*;
import bp.wf.Glo;
import bp.wf.template.sflow.*;
import bp.wf.template.frm.*;
import bp.*;
import bp.wf.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import static bp.tools.StringHelper.padLeft;

/**
 流程模版的操作
*/
public class TemplateGlo
{
	/**
	 装载流程模板

	 @param fk_flowSort 流程类别
	 @param path 流程名称
	 @return
	*/

	public static Flow LoadFlowTemplate(String fk_flowSort, String path, ImpFlowTempleteModel model, String SpecialFlowNo) throws Exception {
		return LoadFlowTemplate(fk_flowSort, path, model, SpecialFlowNo, null);
	}

	public static Flow LoadFlowTemplate(String fk_flowSort, String path, ImpFlowTempleteModel model) throws Exception {
		return LoadFlowTemplate(fk_flowSort, path, model, "", null);
	}

	public static Flow LoadFlowTemplate(String fk_flowSort, String path, ImpFlowTempleteModel model, String SpecialFlowNo, String flowName) throws Exception {
		File info = new File(path);
		DataSet ds = new DataSet();
		try
		{
			ds.readXml(path);
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("@导入流程路径:" + path + "出错：" + ex.getMessage());
		}


		if (ds.contains("WF_Flow") == false)
		{
			throw new RuntimeException("err@导入错误，非流程模版文件" + path + "。");
		}

		DataTable dtFlow = ds.GetTableByName("WF_Flow");
		Flow fl = new Flow();
		String oldFlowNo = dtFlow.Rows.get(0).getValue("No").toString();
		String oldFlowName = dtFlow.Rows.get(0).getValue("Name").toString();

		int oldFlowID = Integer.parseInt(oldFlowNo);
		int iOldFlowLength = String.valueOf(oldFlowID).length();
		String timeKey = DataType.dateToStr(new Date(),"yyMMddhhmmss");// LocalDateTime.now().toString("yyMMddhhmmss");


			///#region 根据不同的流程模式，设置生成不同的流程编号.
		switch (model)
		{
			case AsNewFlow: //做为一个新流程.
				fl.setNo(fl.GenerNewNo());
				fl.DoDelData();
				fl.DoDelete(); //删除可能存在的垃圾.
				fl.Insert();
				break;
			case AsTempleteFlowNo: //用流程模版中的编号
				fl.setNo(oldFlowNo);
				if (fl.getIsExits())
				{
					throw new RuntimeException("导入错误:流程模版(" + oldFlowName + ")中的编号(" + oldFlowNo + ")在系统中已经存在,流程名称为:" + dtFlow.Rows.get(0).getValue("name").toString());
				}
				else
				{
					fl.setNo(oldFlowNo);
					fl.DoDelData();
					fl.DoDelete(); //删除可能存在的垃圾.
					fl.Insert();

				}
				break;
			case OvrewaiteCurrFlowNo: //覆盖当前的流程.
				fl.setNo(oldFlowNo);
				fl.DoDelData();
				fl.DoDelete(); //删除可能存在的垃圾.
				fl.Insert();
				break;
			case AsSpecFlowNo:
				if (SpecialFlowNo.length() <= 0)
					throw new RuntimeException("@您是按照指定的流程编号导入的，但是您没有传入正确的流程编号。");

				fl.setNo(SpecialFlowNo.toString());
				fl.DoDelData();
				fl.DoDelete(); //删除可能存在的垃圾.
				fl.Insert();
				break;
			default:
				throw new RuntimeException("@没有判断");
		}

		///#endregion 根据不同的流程模式，设置生成不同的流程编号.
		int idx = 0;
		String infoErr = "";
		String infoTable = "";
		int flowID = Integer.parseInt(fl.getNo());


			///#region 处理流程表数据
		for (DataColumn dc : dtFlow.Columns)
		{
			String val = dtFlow.Rows.get(0).getValue(dc.ColumnName)==null? null: dtFlow.Rows.get(0).getValue(dc.ColumnName).toString();
			switch (dc.ColumnName.toLowerCase())
			{
				case "no":
				case "fk_flowsort":
					continue;
				case "name":
					break;
				case "atpara":
					if(DataType.IsNullOrEmpty(val)==false){
						AtPara atPara = new AtPara(val);
						String item = atPara.GetValStrByKey("DTSSpecNodes");
						if(DataType.IsNullOrEmpty(item) == false){
							String[] nodeIDs = item.split(",");
							String nodeIDStr = "";
							for(String nodeID : nodeIDs){
								//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
								if (nodeID.length() < iOldFlowLength)
									throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_Flow下DTSSpecNodes值错误:" + val);
								nodeIDStr += flowID + nodeID.substring(iOldFlowLength)+",";
							}
							if(nodeIDStr.endsWith(","))
								nodeIDStr = nodeIDStr.substring(0,nodeIDStr.length()-1);
							atPara.SetVal("DTSSpecNodes",nodeIDStr);
							val = atPara.GenerAtParaStrs();
						}

					}
					break;
				default:
					break;
			}
			fl.SetValByKey(dc.ColumnName, val);
		}
		fl.setFlowSortNo(fk_flowSort);
		if (DBAccess.IsExitsObject(fl.getPTable()) == true)
			fl.setPTable(null);

		//修改成当前登录人所在的组织
		fl.setOrgNo(WebUser.getOrgNo());
		fl.SetValByKey("Creater",WebUser.getNo());
		if (DataType.IsNullOrEmpty(flowName) == false)
			fl.setName(flowName);
		if(fl.getFlowDevModel() == FlowDevModel.JiJian){
			//修改FrmUrl的值
			fl.setFrmUrl("ND"+Integer.parseInt(fl.getNo())+"01");
		}
		fl.Update();

		//判断该流程是否是公文流程，存在BuessFields、FlowBuessType、FK_DocType=01
		Attrs attrs = fl.getEnMap().getAttrs();
		if (attrs.contains("FlowBuessType") == true)
		{
			DBAccess.RunSQL("UPDATE WF_Flow Set BuessFields='" + fl.GetParaString("BuessFields") + "', FlowBuessType=" + fl.GetParaInt("FlowBuessType", 0) + " ,FK_DocType='" + fl.GetParaString("FK_DocType") + "'");
		}


			///#endregion 处理流程表数据


			///#region 处理OID 插入重复的问题 Sys_GroupField, Sys_MapAttr.
		DataTable mydtGF = ds.GetTableByName("Sys_GroupField");
		DataTable myDTAttr = ds.GetTableByName("Sys_MapAttr");
		DataTable myDTAth = ds.GetTableByName("Sys_FrmAttachment");
		DataTable myDTDtl = ds.GetTableByName("Sys_MapDtl");
		DataTable myDFrm = ds.GetTableByName("Sys_MapFrame");

		for (DataRow dr : mydtGF.Rows)
		{
			bp.sys.GroupField gf = new bp.sys.GroupField();
			for (DataColumn dc : mydtGF.Columns)
			{
				String val = dr.getValue(dc.ColumnName)==null ? null:dr.getValue(dc.ColumnName).toString();
				if (val == null)
					continue;
				switch (dc.ColumnName.toLowerCase())
				{
					case "enname":
					case "keyofen":
					case "ctrlid": //升级经典表单的时候,新增加的字段 add by zhoupeng 2016.11.21
					case "frmid": //升级经典表单的时候,新增加的字段 add by zhoupeng 2016.11.21
						val = val.replace("ND" + oldFlowID, "ND" + flowID);
						break;
					default:
						break;
				}
				gf.SetValByKey(dc.ColumnName, val);
			}
			long oldID = gf.getOID();
			gf.setOID(DBAccess.GenerOID());
			gf.DirectInsert();
			dr.setValue("OID", gf.getOID()); //给他一个新的OID.

			// 属性。
			if (myDTAttr != null && myDTAttr.Columns.contains("GroupID"))
			{
				for (DataRow dr1 : myDTAttr.Rows)
				{
					if (dr1.getValue("GroupID") == null)
					{
						dr1.setValue("GroupID", 0);
					}

					if (dr1.getValue("GroupID").toString().equals(String.valueOf(oldID)))
					{
						dr1.setValue("GroupID", gf.getOID());
					}
				}
			}

			// 附件。
			if (myDTAth != null && myDTAth.Columns.contains("GroupID"))
			{
				for (DataRow dr1 : myDTAth.Rows)
				{
					if (dr1.getValue("GroupID") == null)
					{
						dr1.setValue("GroupID", 0);
					}

					if (dr1.getValue("GroupID").toString().equals(String.valueOf(oldID)))
					{
						dr1.setValue("GroupID", gf.getOID());
					}
				}
			}

			if (myDTDtl != null && myDTDtl.Columns.contains("GroupID"))
			{
				// 从表。
				for (DataRow dr1 : myDTDtl.Rows)
				{
					if (dr1.getValue("GroupID") == null)
					{
						dr1.setValue("GroupID", 0);
					}

					if (dr1.getValue("GroupID").toString().equals(String.valueOf(oldID)))
					{
						dr1.setValue("GroupID", gf.getOID());
					}
				}
			}

			// frm.
			if (myDFrm != null && myDFrm.Columns.contains("GroupID"))
			{
				for (DataRow dr1 : myDFrm.Rows)
				{
					if (dr1.getValue("GroupID") == null)
					{
						dr1.setValue("GroupID", 0);
					}

					if (dr1.getValue("GroupID").toString().equals(String.valueOf(oldID)))
					{
						dr1.setValue("GroupID", gf.getOID());
					}
				}
			}
		}

			///#endregion 处理OID 插入重复的问题。 Sys_GroupField ， Sys_MapAttr.

		int timeKeyIdx = 0;
		for (DataTable dt : ds.Tables)
		{
			timeKeyIdx++;
			timeKey = timeKey + String.valueOf(timeKeyIdx);

			infoTable = "@导入:" + dt.TableName + " 出现异常。";
			switch (dt.TableName)
			{
				case "WF_Flow": //模版文件。
				case "WF_SyncDataField":
					continue;
				case "WF_SyncData":
					DataTable fieldDt = ds.GetTableByName("WF_SyncDataField");
					SyncDataField syncDataField = null;
					for(DataRow dr:dt.Rows){
						SyncData syncData = new SyncData();
						for(DataColumn dc :dt.Columns){
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
								continue;
							if(dc.ColumnName.toLowerCase().equals("flowno"))
								val = fl.getNo();
							syncData.SetValByKey(dc.ColumnName, val);
						}
						String mypk = syncData.getMyPK();
						String newMyPK = DBAccess.GenerGUID(0, null, null);
						//判断是否有SyncDataField
						for(DataRow drr : fieldDt.Rows){
							String refPKVal = drr.getValue("RefPKVal").toString();
							if(refPKVal.equals(mypk)==false)
								continue;
							syncDataField = new SyncDataField();
							for(DataColumn dc :dt.Columns){
								String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
								if (val == null)
									continue;
								if(dc.ColumnName.toLowerCase().equals("refpkval"))
									val = mypk;
								if(dc.ColumnName.toLowerCase().equals("flowno"))
									val = fl.getNo();
								syncDataField.SetValByKey(dc.ColumnName, val);
							}
							syncDataField.setMyPK(syncDataField.getRefPKVal()+"_"+syncDataField.getAttrKey());
							syncDataField.Insert();
							continue;
						}
						syncData.setMyPK(newMyPK);
						syncData.Insert();
					}
					break;

				case "WF_NodeSubFlow": //延续子流程.
					for (DataRow dr : dt.Rows)
					{
						SubFlow yg = new SubFlow();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							switch (dc.ColumnName.toLowerCase())
							{
								case "tonodeid":
								case "fk_node":
								case "nodeid":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_NodeSubFlow下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								case "fk_flow":
									val = fl.getNo();
									break;
								default:
									val = val.replace("ND" + oldFlowID, "ND" + flowID);
									break;
							}
							yg.SetValByKey(dc.ColumnName, val);
						}
						yg.Insert();
					}
					continue;
				case "WF_FlowForm": //独立表单。 add 2013-12-03
					//foreach (DataRow dr in dt.Rows)
					//{
					//    FlowForm cd = new FlowForm();
					//    foreach (DataColumn dc in dt.Columns)
					//    {
					//        String val = dr[dc.ColumnName] as string;
					//        if (val == null)
					//            continue;
					//        switch (dc.ColumnName.ToLower())
					//        {
					//            case "fk_flow":
					//                val = fl.No;
					//                break;
					//            default:
					//                val = val.replace("ND" + oldFlowID, "ND" + flowID);
					//                break;
					//        }
					//        cd.SetValByKey(dc.ColumnName, val);
					//    }
					//    cd.Insert();
					//}
					break;
				case "WF_NodeForm": //节点表单权限。 2013-12-03
					for (DataRow dr : dt.Rows)
					{
						NodeToolbar cd = new NodeToolbar();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							switch (dc.ColumnName.toLowerCase())
							{
								case "tonodeid":
								case "fk_node":
								case "nodeid":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_NodeForm下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								case "fk_flow":
									val = fl.getNo();
									break;
								default:
									val = val.replace("ND" + oldFlowID, "ND" + flowID);
									break;
							}
							cd.SetValByKey(dc.ColumnName, val);
						}
						cd.Insert();
					}
					break;
				case "Sys_FrmSln": //表单字段权限。 2013-12-03
					for (DataRow dr : dt.Rows)
					{
						FrmField cd = new FrmField();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							switch (dc.ColumnName.toLowerCase())
							{
								case "tonodeid":
								case "fk_node":
								case "nodeid":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点Sys_FrmSln下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								case "fk_flow":
									val = fl.getNo();
									break;
								default:
									val = val.replace("ND" + oldFlowID, "ND" + flowID);
									break;
							}
							cd.SetValByKey(dc.ColumnName, val);
						}
						cd.Insert();
					}
					break;
				case "WF_NodeToolbar": //工具栏。
					for (DataRow dr : dt.Rows)
					{
						NodeToolbar cd = new NodeToolbar();
						for (DataColumn dc : dt.Columns)
						{
							String val =dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							switch (dc.ColumnName.toLowerCase())
							{
								case "tonodeid":
								case "fk_node":
								case "nodeid":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_NodeToolbar下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								case "fk_flow":
									val = fl.getNo();
									break;
								default:
									val = val.replace("ND" + oldFlowID, "ND" + flowID);
									break;
							}
							cd.SetValByKey(dc.ColumnName, val);
						}
						cd.setOID(DBAccess.GenerOID());
						cd.DirectInsert();
					}
					break;
				case "Sys_FrmPrintTemplate":
					for (DataRow dr : dt.Rows)
					{
						FrmPrintTemplate bt = new FrmPrintTemplate();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							switch (dc.ColumnName.toLowerCase())
							{
								case "fk_flow":
									val = String.valueOf(flowID);
									break;
								case "nodeid":
								case "fk_node":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点Sys_FrmPrintTemplate下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								default:
									break;
							}
							bt.SetValByKey(dc.ColumnName, val);
						}

						bt.setMyPK(DBAccess.GenerGUID(0, null, null));

						try
						{
							//Files.copy(Paths.get(info.getParent() + "/" + bt.getMyPK() + ".rtf"), Paths.get(SystemConfig.getPathOfWebApp() + "/DataUser/CyclostyleFile/" + bt.getMyPK() + ".rtf"), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
						}
						catch (RuntimeException ex)
						{
							// infoErr += "@恢复单据模板时出现错误：" + ex.Message + ",有可能是您在复制流程模板时没有复制同目录下的单据模板文件。";
						}
						bt.Insert();
					}
					break;
				case "WF_FrmNode": //Conds.xml。
					DBAccess.RunSQL("DELETE FROM WF_FrmNode WHERE FK_Flow='" + fl.getNo() + "'");
					for (DataRow dr : dt.Rows)
					{
						FrmNode fn = new FrmNode();
						fn.setFlowNo(fl.getNo());
						for (DataColumn dc : dt.Columns)
						{
							String val =dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							switch (dc.ColumnName.toLowerCase())
							{
								case "fk_node":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_FrmNode下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								case "fk_flow":
									val = fl.getNo();
									break;
								case "fk_frm":
									if(fl.getFlowDevModel() == FlowDevModel.JiJian)
										val = "ND"+Integer.parseInt(fl.getNo())+"01";
									break;
								default:
									break;
							}
							fn.SetValByKey(dc.ColumnName, val);
						}
						// 开始插入。
						if (fn.getFrmID().equals(""))
							continue;
						fn.setMyPK(fn.getFrmID() + "_" + fn.getNodeID()+"_"+fl.getNo());
						try{
							fn.Insert();
						}catch(Exception e){

						}

					}
					break;
				case "WF_Cond": //Conds.xml。
					for (DataRow dr : dt.Rows)
					{
						Cond cd = new Cond();
						cd.setFlowNo(fl.getNo());
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}

							switch (dc.ColumnName.toLowerCase())
							{
								case "tonodeid":
								case "fk_node":
								case "nodeid":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_Cond下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								case "fk_flow":
									val = fl.getNo();
									break;
								default:
									val = val.replace("ND" + oldFlowID, "ND" + flowID);
									break;
							}
							cd.SetValByKey(dc.ColumnName, val);
						}
						if(cd.getCondType() !=CondType.Flow){
							cd.setRefPKVal(fl.getNo()+"_"+cd.getNodeID()+"_"+cd.getToNodeID());
						}
						if (cd.getCondType() == CondType.Flow)
							cd.setRefPKVal(String.valueOf(cd.getNodeID()));
						cd.setFlowNo(fl.getNo());
						cd.setRefFlowNo(fl.getNo());
						int specOperWay = cd.GetValIntByKey("SpecOperWay");
						if(specOperWay == 10 || specOperWay == 11 ||specOperWay==24){
							cd.SetValByKey(CondAttr.SpecOperPara,flowID + cd.getSpecOperPara().substring(iOldFlowLength));
						}
						cd.setMyPK(DBAccess.GenerGUID(0, null, null));
						cd.DirectInsert();
					}
					break;
				case "WF_NodeReturn": //可退回的节点。
					for (DataRow dr : dt.Rows)
					{
						NodeReturn cd = new NodeReturn();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							switch (dc.ColumnName.toLowerCase())
							{
								case "fk_node":
								case "returnto":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_NodeReturn下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								default:
									break;
							}
							cd.SetValByKey(dc.ColumnName, val);
						}

						//开始插入。
						try
						{
							cd.Insert();
						}
						catch (java.lang.Exception e)
						{
							cd.Update();
						}
					}
					break;
				case "WF_Direction": //方向。
					for (DataRow dr : dt.Rows)
					{
						Direction dir = new Direction();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							switch (dc.ColumnName.toLowerCase())
							{
								case "node":
								case "tonode":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_Direction下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								default:
									break;
							}
							dir.SetValByKey(dc.ColumnName, val);
						}
						dir.setFlowNo(fl.getNo());
						dir.Insert();
					}
					break;
				case "WF_LabNote": //LabNotes.xml。
					idx = 0;
					for (DataRow dr : dt.Rows)
					{
						LabNote ln = new LabNote();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							ln.SetValByKey(dc.ColumnName, val);
						}
						idx++;
						ln.setFlowNo(fl.getNo());
						ln.setMyPK(DBAccess.GenerGUID(0, null, null));
						ln.DirectInsert();
					}
					break;
				case "WF_NodeDept": //FAppSets.xml。
					for (DataRow dr : dt.Rows)
					{
						NodeDept dp = new NodeDept();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							switch (dc.ColumnName.toLowerCase())
							{
								case "fk_node":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_NodeDept下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								default:
									break;
							}
							dp.SetValByKey(dc.ColumnName, val);
						}
						try
						{
							//如果部门不属于本组织的，就要删除.
							if (bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.Single)
							{
								bp.wf.port.admin2group.Dept dept = new bp.wf.port.admin2group.Dept(dp.getDeptNo());
								if (dept.getOrgNo().equals(WebUser.getOrgNo()) == false)
								{
									continue;
								}
							}
							dp.Insert();
						}
						catch (RuntimeException ex)
						{
						}
					}
					break;
				case "WF_TemplateNode":
					for (DataRow dr : dt.Rows)
					{
						bp.wf.template.TemplateNode nd = new bp.wf.template.TemplateNode();
						nd.SetValByKey(NodeAttr.NodeID,Integer.parseInt(flowID + dr.getValue(NodeAttr.NodeID).toString().substring(iOldFlowLength)));
						nd.RetrieveFromDBSources();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							switch (dc.ColumnName.toLowerCase())
							{
								case "nodeid":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_Node下nodeid值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								case "fk_flow":
								case "fk_flowsort":
									continue;
								case "showsheets":
								case "histonds":
								case "groupstands": //去除不必要的替换
									String key = "@" + flowID;
									val = val.replace(key, "@");
									break;
								default:
									break;
							}
							nd.SetValByKey(dc.ColumnName, val);
						}
						nd.SetValByKey(NodeAttr.FK_Flow,fl.getNo());
						nd.DirectInsert();
					}
					break;
				case "WF_NodeWorkCheck":
					for (DataRow dr : dt.Rows)
					{
						bp.wf.template.NodeWorkCheck nd = new bp.wf.template.NodeWorkCheck();
						nd.SetValByKey(NodeAttr.NodeID,Integer.parseInt(flowID + dr.getValue(NodeAttr.NodeID).toString().substring(iOldFlowLength)));
						int i=nd.RetrieveFromDBSources();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							switch (dc.ColumnName.toLowerCase())
							{
								case "nodeid":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_Node下nodeid值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								case "fk_flow":
								case "fk_flowsort":
									continue;
								case "showsheets":
								case "histonds":
								case "groupstands": //去除不必要的替换
									String key = "@" + flowID;
									val = val.replace(key, "@");
									break;
								default:
									break;
							}
							nd.SetValByKey(dc.ColumnName, val);
						}
						nd.SetValByKey(NodeAttr.FK_Flow,fl.getNo());
						if(i==0)
							nd.DirectInsert();
						else
							nd.DirectUpdate();
					}
					break;
				case "WF_Node": //导入节点信息.
					for (DataRow dr : dt.Rows)
					{
						bp.wf.template.NodeExt nd = new bp.wf.template.NodeExt();
						bp.wf.template.NodeWorkCheck fwc = new NodeWorkCheck();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}

							switch (dc.ColumnName.toLowerCase())
							{
								case "nodeid":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_Node下nodeid值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								case "fk_flow":
								case "fk_flowsort":
									continue;
								case "showsheets":
								case "histonds":
								case "groupstands": //去除不必要的替换
									String key = "@" + flowID;
									val = val.replace(key, "@");
									break;
								default:
									break;
							}
							nd.SetValByKey(dc.ColumnName, val);
							fwc.SetValByKey(dc.ColumnName, val);
						}

						nd.setFlowNo(fl.getNo());
						nd.setFlowName(fl.getName());
						try
						{

							if (nd.getEnMap().getAttrs().contains("OfficePrintEnable"))
							{
								if (Objects.equals(nd.GetValStringByKey("OfficePrintEnable"), "打印"))
								{
									nd.SetValByKey("OfficePrintEnable", 0);
								}
							}

							try
							{
								if(nd.RetrieveFromDBSources() == 0)
								{
									nd.DirectInsert();
								}
								else
								{
									nd.DirectUpdate();
								}
							}
							catch (RuntimeException ex)
							{
								// var i2 = 11;
							}

							//把抄送的信息也导入里面去.
							fwc.setFWCVer(1); //设置为2019版本. 2018版是1个节点1个人,仅仅显示1个意见.
							fwc.DirectUpdate();
							DBAccess.RunSQL("DELETE FROM Sys_MapAttr WHERE FK_MapData='ND" + nd.getNodeID() + "'");
						}
						catch (RuntimeException ex)
						{
							fwc.CheckPhysicsTable();

							throw new RuntimeException("@导入节点:FlowName:" + nd.getFlowName() + " nodeID: " + nd.getNodeID() + " , " + nd.getName() + " 错误:" + ex.getMessage());
						}

					}

					// 执行update 触发其他的业务逻辑。
					for (DataRow dr : dt.Rows)
					{
						Node nd = new Node();
						nd.setNodeID(Integer.parseInt(dr.getValue(NodeAttr.NodeID).toString()));
						nd.RetrieveFromDBSources();
						Node newNode = new Node();
						newNode.Copy(nd);
						newNode.setFlowNo(fl.getNo());
						//获取表单类别
						String formType = dr.getValue(NodeAttr.FormType).toString();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							switch (dc.ColumnName.toLowerCase())
							{
								case "nodefrmid":
									//绑定表单库的表单11不需要替换表单编号
									if (formType.equals("11") == false)
									{
										int iFormTypeLength = iOldFlowLength + 2;
										if (val.length() > iFormTypeLength)
										{
											val = "ND" + flowID + val.substring(iFormTypeLength);
										}
									}
									break;
								case "nodeid":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_Node下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								case "fk_flow":
								case "fk_flowsort":
									continue;
								case "showsheets":
								case "histonds":
								case "groupstands": //修复替换
									String key = "@" + flowID;
									val = val.replace(key, "@");
									break;
								default:
									break;
							}
							newNode.SetValByKey(dc.ColumnName, val);
						}
						newNode.setFlowNo(fl.getNo());
						newNode.setFlowName(fl.getName());
						newNode.DirectUpdate();
					}

					//更新 GroupStaNDs . HisToND
					String sql = "UPDATE WF_Node SET GroupStaNDs=Replace(GroupStaNDs,'@" + Integer.parseInt(oldFlowNo) + "','@" + Integer.parseInt(fl.getNo()) + "'), HisToNDs=Replace(HisToNDs,'@" + Integer.parseInt(oldFlowNo) + "','@" + Integer.parseInt(fl.getNo()) + "') WHERE FK_Flow='" + fl.getNo() + "'";
					DBAccess.RunSQL(sql);
					break;
				case "WF_NodeExt":
					for (DataRow dr : dt.Rows)
					{
						bp.wf.template.NodeExt nd = new bp.wf.template.NodeExt();
						nd.setNodeID(Integer.parseInt(flowID + dr.getValue(NodeAttr.NodeID).toString().substring(iOldFlowLength)));
						nd.RetrieveFromDBSources();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							switch (dc.ColumnName.toLowerCase())
							{
								case "nodeid":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_Node下nodeid值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								case "fk_flow":
								case "fk_flowsort":
									continue;
								case "showsheets":
								case "histonds":
								case "groupstands": //去除不必要的替换
									String key = "@" + flowID;
									val = val.replace(key, "@");
									break;
								default:
									break;
							}
							nd.SetValByKey(dc.ColumnName, val);
						}
						nd.setFlowNo(fl.getNo());
						nd.DirectUpdate();
					}
					break;
				case "WF_Selector":
					for (DataRow dr : dt.Rows)
					{
						Selector selector = new Selector();
						for (DataColumn dc : dt.Columns)
						{

							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}

							if (dc.ColumnName.toLowerCase().equals("nodeid"))
							{
								if (val.length() < iOldFlowLength)
								{
									// 节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
									throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_Node下FK_Node值错误:" + val);
								}
								val = flowID + val.substring(iOldFlowLength);
							}

							selector.SetValByKey(dc.ColumnName, val);
						}
						selector.DirectUpdate();
					}
					break;
				case "WF_NodeStation": //FAppSets.xml。
					DBAccess.RunSQL("DELETE FROM WF_NodeStation WHERE FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + fl.getNo() + "')");
					for (DataRow dr : dt.Rows)
					{
						NodeStation ns = new NodeStation();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}

							switch (dc.ColumnName.toLowerCase())
							{
								case "fk_node":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_NodeStation下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								default:
									break;
							}
							ns.SetValByKey(dc.ColumnName, val);
						}
						ns.Insert();
					}
					break;
				case "Sys_Enum": //RptEmps.xml。
				case "Sys_Enums":
					for (DataRow dr : dt.Rows)
					{
						SysEnum se = new bp.sys.SysEnum();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							switch (dc.ColumnName.toLowerCase())
							{
								case "fk_node":
									break;
								default:
									break;
							}
							se.SetValByKey(dc.ColumnName, val);
						}
						// se.setMyPK(se.EnumKey + "_" + se.Lang + "_" + se.getIntKey();

						//设置orgNo.
						se.setOrgNo(WebUser.getOrgNo());
						se.ResetPK();

						if (se.IsExits())
						{
							continue;
						}
						se.Insert();
					}
					break;
				case "Sys_EnumMain": //RptEmps.xml。
					for (DataRow dr : dt.Rows)
					{
						SysEnumMain sem = new SysEnumMain();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) ==null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							sem.SetValByKey(dc.ColumnName, val);
						}
						if (sem.IsExits())
						{
							continue;
						}
						sem.Insert();
					}
					break;
				case "Sys_MapAttr": //RptEmps.xml。
					for (DataRow dr : dt.Rows)
					{
						MapAttr ma = new MapAttr();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName)==null ? null: dr.getValue(dc.ColumnName).toString();
							switch (dc.ColumnName.toLowerCase())
							{
								case "fk_mapdata":
								case "keyofen":
								case "autofulldoc":
									if (val == null)
									{
										continue;
									}
									val = val.replace("ND" + oldFlowID, "ND" + flowID);
									break;
								default:
									break;
							}
							ma.SetValByKey(dc.ColumnName, val);
						}
						boolean b = ma.IsExit(MapAttrAttr.FK_MapData, ma.getFrmID(), MapAttrAttr.KeyOfEn, ma.getKeyOfEn());

						ma.setMyPK(ma.getFrmID() + "_" + ma.getKeyOfEn());
						if (b == true)
							ma.DirectUpdate();
						else
							ma.DirectInsert();
					}
					break;
				case "Sys_MapData": //RptEmps.xml。
					for (DataRow dr : dt.Rows)
					{
						MapData md = new bp.sys.MapData();
						String htmlCode = "";
						for (DataColumn dc : dt.Columns)
						{
							if (Objects.equals(dc.ColumnName, "HtmlTemplateFile"))
							{
								htmlCode = dr.getValue(dc.ColumnName) == null ? null:dr.getValue(dc.ColumnName).toString();
								continue;
							}

							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}

							val = val.replace("ND" + oldFlowID, "ND" + Integer.parseInt(fl.getNo()));
							md.SetValByKey(dc.ColumnName, val);
						}
						md.Save();

						//如果是开发者表单，赋值HtmlTemplateFile数据库的值并保存到DataUser下
						if (md.getHisFrmType() == FrmType.Develop)
						{
							//String htmlCode = DBAccess.GetBigTextFromDB("Sys_MapData", "No", "ND" + oldFlowID, "HtmlTemplateFile");
							if (DataType.IsNullOrEmpty(htmlCode) == false)
							{
								htmlCode = htmlCode.replace("ND" + oldFlowID, "ND" + Integer.parseInt(fl.getNo()));
								//保存到数据库，存储html文件
								//保存到DataUser/CCForm/HtmlTemplateFile/文件夹下
								String filePath = SystemConfig.getPathOfDataUser() + "CCForm/HtmlTemplateFile/";
								if ((new File(filePath)).isDirectory() == false)
								{
									(new File(filePath)).mkdirs();
								}
								filePath = filePath + md.getNo() + ".htm";
								//写入到html 中
								DataType.WriteFile(filePath, htmlCode);
								// HtmlTemplateFile 保存到数据库中
								DBAccess.SaveBigTextToDB(htmlCode, "Sys_MapData", "No", md.getNo(), "HtmlTemplateFile");
							}
							else
							{
								//如果htmlCode是空的需要删除当前节点的html文件
								String filePath = SystemConfig.getPathOfDataUser() + "CCForm/HtmlTemplateFile/" + md.getNo() + ".htm";
								if ((new File(filePath)).isFile() == true)
								{
									(new File(filePath)).delete();
								}
								DBAccess.SaveBigTextToDB("", "Sys_MapData", "No", md.getNo(), "HtmlTemplateFile");
							}
						}
					}

					break;
				case "Sys_MapDtl": //RptEmps.xml。
					for (DataRow dr : dt.Rows)
					{
						MapDtl md = new bp.sys.MapDtl();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName)==null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}

							val = val.replace("ND" + oldFlowID, "ND" + flowID);
							md.SetValByKey(dc.ColumnName, val);
						}
						md.Save();
						md.IntMapAttrs(); //初始化他的字段属性.
					}
					break;
				case "Sys_MapExt":
					for (DataRow dr : dt.Rows)
					{
						MapExt md = new bp.sys.MapExt();
						String text = "";
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							if (dc.ColumnName.equals("HtmlText") == true)
							{
								text = dr.getValue(dc.ColumnName) == null ? "": dr.getValue(dc.ColumnName).toString();
								continue;
							}

							val = val.replace("ND" + oldFlowID, "ND" + flowID);
							md.SetValByKey(dc.ColumnName, val);
						}

						//调整他的PK.
						//md.InitPK();
						md.Save(); //执行保存.
						if (md.getExtType().equals("HtmlText") == true && DataType.IsNullOrEmpty(text) == false)
						{
							md.SaveBigNoteHtmlText(text);
						}
					}
					break;
				case "Sys_FrmImg":
					idx = 0;
					timeKey = DBAccess.GenerGUID(); // LocalDateTime.now().toString("yyyyMMddHHmmss");
					for (DataRow dr : dt.Rows)
					{
						idx++;
						FrmImg en = new FrmImg();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							val = val.replace("ND" + oldFlowID, "ND" + flowID);
							en.SetValByKey(dc.ColumnName, val);
						}

						//设置主键.
						en.setMyPK(en.getFrmID() + "_" + en.getKeyOfEn());
						en.Save(); //执行保存.
					}
					break;
				case "Sys_FrmImgAth": //图片附件.
					idx = 0;
					timeKey = DBAccess.GenerGUID(); // LocalDateTime.now().toString("yyyyMMddHHmmss");
					for (DataRow dr : dt.Rows)
					{
						idx++;
						FrmImgAth en = new FrmImgAth();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}
							val = val.replace("ND" + oldFlowID, "ND" + flowID);
							en.SetValByKey(dc.ColumnName, val);
						}
						en.setMyPK(en.getFrmID() + "_" + en.getCtrlID());
						en.Save();
						//  en.setMyPK(Guid.NewGuid().ToString());
					}
					break;
				case "Sys_FrmAttachment":
					idx = 0;
					for (DataRow dr : dt.Rows)
					{
						idx++;
						FrmAttachment en = new FrmAttachment();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}

							val = val.replace("ND" + oldFlowID, "ND" + flowID);
							en.SetValByKey(dc.ColumnName, val);
						}

						en.setMyPK(en.getFrmID() + "_" + en.getNoOfObj());
						en.Save();
					}
					break;
				case "Sys_FrmEvent": //事件.
					idx = 0;
					for (DataRow dr : dt.Rows)
					{
						idx++;
						FrmEvent en = new FrmEvent();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}

							switch (dc.ColumnName.toLowerCase())
							{
								case "fk_node":
									if (val.equals("0") == false)
									{
										if (val.length() < iOldFlowLength)
										{
											//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
											throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点Sys_FrmEvent下FK_Node值错误:" + val);
										}
										val = flowID + val.substring(iOldFlowLength);
									}
									break;
								case "fk_flow":
									val = fl.getNo();
									break;
								default:
									val = val.replace("ND" + oldFlowID, "ND" + flowID);
									break;
							}

							en.SetValByKey(dc.ColumnName, val);
						}

						//解决保存错误问题.
						en.setMyPK(DBAccess.GenerGUID(0, null, null));
						en.Insert();
					}
					break;
				case "Sys_FrmRB": //Sys_FrmRB.
					idx = 0;
					for (DataRow dr : dt.Rows)
					{
						idx++;
						FrmRB en = new FrmRB();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}

							val = val.replace("ND" + oldFlowID, "ND" + flowID);
							en.SetValByKey(dc.ColumnName, val);
						}
						en.Insert();
					}
					break;
				case "Sys_MapFrame":
					idx = 0;
					for (DataRow dr : dt.Rows)
					{
						idx++;
						MapFrame en = new MapFrame();
						for (DataColumn dc : dt.Columns)
						{
							Object val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}


							en.SetValByKey(dc.ColumnName, val.toString().replace("ND" + oldFlowID, "ND" + flowID));
						}
						en.DirectInsert();
					}
					break;
				case "WF_NodeEmp": //FAppSets.xml。
					for (DataRow dr : dt.Rows)
					{
						NodeEmp ne = new NodeEmp();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}

							switch (dc.ColumnName.toLowerCase())
							{
								case "fk_node":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_NodeEmp下FK_Node值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								default:
									break;
							}
							ne.SetValByKey(dc.ColumnName, val);
						}
						ne.Insert();
					}
					break;
				case "WF_CCRole": //FAppSets.xml。
					for (DataRow dr : dt.Rows)
					{
						CCRole ccRole = new CCRole();
						for (DataColumn dc : dt.Columns)
						{
							String val = dr.getValue(dc.ColumnName) == null ? null: dr.getValue(dc.ColumnName).toString();
							if (val == null)
							{
								continue;
							}

							switch (dc.ColumnName.toLowerCase())
							{
								case "nodeid":
									if (val.length() < iOldFlowLength)
									{
										//节点编号长度小于流程编号长度则为异常数据，异常数据不进行处理
										throw new RuntimeException("@导入模板名称：" + oldFlowName + "；节点WF_CCRole下NodeID值错误:" + val);
									}
									val = flowID + val.substring(iOldFlowLength);
									break;
								default:
									break;
							}
							ccRole.SetValByKey(dc.ColumnName, val);
						}
						ccRole.setMyPK(DBAccess.GenerGUID());
						ccRole.Insert();
					}
					break;
				case "Sys_GroupField":
					//foreach (DataRow dr in dt.Rows)
					//{
					//    GroupField gf = new Sys.GroupField();
					//    foreach (DataColumn dc in dt.Columns)
					//    {
					//        String val = dr[dc.ColumnName] as string;
					//        if (val == null)
					//            continue;
					//        switch (dc.ColumnName.ToLower())
					//        {
					//            case "enname":
					//            case "keyofen":
					//            case "ctrlid": //升级经典表单的时候,新增加的字段 add by zhoupeng 2016.11.21
					//            case "frmid": //升级经典表单的时候,新增加的字段 add by zhoupeng 2016.11.21
					//                val = val.replace("ND" + oldFlowID, "ND" + flowID);
					//                break;
					//            default:
					//                break;
					//        }
					//        gf.SetValByKey(dc.ColumnName, val);
					//    }
					//    gf.InsertAsOID(gf.getOID());
					//}
					break;

				default:
					// infoErr += "Error:" + dt.TableName;
					break;
					//    throw new Exception("@unhandle named " + dt.TableName);
			}
		}


			///#region 处理数据完整性。


		//DBAccess.RunSQL("UPDATE WF_Cond SET FK_Node=NodeID WHERE FK_Node=0");
		//DBAccess.RunSQL("UPDATE WF_Cond SET ToNodeID=NodeID WHERE ToNodeID=0");
		// DBAccess.RunSQL("DELETE FROM WF_Cond WHERE NodeID NOT IN (SELECT NodeID FROM WF_Node)");
		//  DBAccess.RunSQL("DELETE FROM WF_Cond WHERE ToNodeID NOT IN (SELECT NodeID FROM WF_Node) ");
		// DBAccess.RunSQL("DELETE FROM WF_Cond WHERE FK_Node NOT IN (SELECT NodeID FROM WF_Node) AND FK_Node > 0");

		//处理分组错误.
		Nodes nds = new Nodes(fl.getNo());
		for (Node nd : nds.ToJavaList())
		{
			if(nd.getHisNodeType() == NodeType.UserNode){
				MapFrmFool cols = new MapFrmFool("ND" + nd.getNodeID());
				cols.DoCheckFixFrmForUpdateVer();
			}
		}

			///#endregion

		//处理OrgNo 的导入问题.
		if (bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			fl.RetrieveFromDBSources();
			fl.setOrgNo(WebUser.getOrgNo());
			fl.DirectUpdate();
		}


		if (Objects.equals(infoErr, ""))
		{
			infoTable = "";
			//写入日志.
			bp.sys.base.Glo.WriteUserLog("导入流程模板：" + fl.getName() + " - " + fl.getNo(), "通用操作");

			//创建track.
			Track.CreateOrRepairTrackTable(fl.getNo());
			return fl; // "完全成功。";
		}

		infoErr = "@执行期间出现如下非致命的错误：\t\r" + infoErr + "@ " + infoTable;
		throw new RuntimeException(infoErr);
	}

	public static Node NewEtcNode(String flowNo, int x, int y, NodeType type) throws Exception {
		Flow flow = new Flow(flowNo);

		Node nd = new Node();
		int idx = DBAccess.RunSQLReturnValInt("SELECT COUNT(NodeID) FROM WF_Node WHERE FK_Flow='" + flowNo + "'", 0);
		if (idx == 0)
		{
			idx++;
		}

		int nodeID = 0;
		//设置节点ID.
		while (true)
		{
			String strID = flowNo + StringHelper.padLeft(String.valueOf(idx), 2, '0');
			nd.setNodeID(Integer.parseInt(strID));
			if (nd.IsExits() == false)
			{
				break;
			}
			idx++;
		}

		if (nd.getNodeID()> Integer.parseInt(flowNo + "99"))
		{
			throw new RuntimeException("流程最大节点编号不可以超过100");
		}

		if (type == NodeType.RouteNode)
		{
			nd.setName("条件" + nd.getNodeID());
		}

		if (type == NodeType.CCNode)
		{
			nd.setName("抄送" + nd.getNodeID());
		}

		if (type == NodeType.SubFlowNode)
		{
			nd.setName("子流程节点" + nd.getNodeID());
		}

		nd.setFlowNo(flowNo);
		nd.setHisNodeType(type); //抄送节点.
		nd.setX(x);
		nd.setY(y);
		nd.Insert();
		return nd;
	}


	public static Node NewNode(String flowNo, int x, int y, String icon, int runModel) throws Exception {
		return NewNode(flowNo, x, y, icon, runModel, 0);
	}

	public static Node NewNode(String flowNo, int x, int y, String icon) throws Exception {
		return NewNode(flowNo, x, y, icon, 0, 0);
	}

	public static Node NewNode(String flowNo, int x, int y) throws Exception {
		return NewNode(flowNo, x, y, null, 0, 0);
	}

	public static Node NewNode(String flowNo, int x, int y, String icon, int runModel, int nodeType) throws Exception {
		Flow flow = new Flow(flowNo);
		NodeSimples nds = new NodeSimples();
		nds.Retrieve("FK_Flow", flowNo,"Step");

		Node nd = new Node();
		nd.setFlowNo(flowNo);
		int nodeID = 0;
		int idx = 2; //从第2个开始.
		//设置节点ID.
		while (true)
		{
			String strID = "";
			if (String.valueOf(idx).length() == 3 || String.valueOf(idx).length() == 2)
			{
				strID = flowNo + String.valueOf(idx);
				nd.setNodeID(Integer.valueOf(strID));
			}
			else
			{
				strID = flowNo + padLeft(String.valueOf(idx),2, '0');
				nd.setNodeID(Integer.valueOf((strID)));
			}
			if (nds.contains("NodeID", nd.getNodeID()) == false)
				break;
			idx++;
		}

		nodeID = nd.getNodeID();

		//增加了两个默认值值 . 2016.11.15. 目的是让创建的节点，就可以使用.
		nd.setCondModel(DirCondModel.ByDDLSelected); //默认的发送方向.
		nd.setHisDeliveryWay(DeliveryWay.BySelected); //上一步发送人来选择.
		nd.setFormType(NodeFormType.FoolForm); //设置为经典表单.
		nd.setMark(String.valueOf(nodeID)); //类别
		//如果是极简模式.
		if (flow.getFlowDevModel() == FlowDevModel.JiJian)
		{
			nd.setFlowNo(flowNo);
			nd.setFormType(NodeFormType.FoolForm); //设置为经典表单.
			nd.setNodeFrmID( "ND" + Integer.parseInt(flow.getNo()) + "01");
			nd.setFrmWorkCheckSta(FrmWorkCheckSta.Enable);
			nd.DirectUpdate();
			FrmNode fn = new FrmNode();
			fn.setFrmID(nd.getNodeFrmID());
			fn.setEnableFWC(FrmWorkCheckSta.Enable);
			fn.setNodeID(nd.getNodeID());
			fn.setFlowNo(flowNo);
			fn.setFrmSln(FrmSln.Readonly);
			fn.setMyPK(fn.getFrmID() + "_" + fn.getNodeID() + "_" + fn.getFlowNo());
			//执行保存.
			fn.Save();
			//MapData md = new MapData();
			//nd.setNo(nd.getNodeFrmID();
			//md.setHisFrmType(FrmType.FoolForm);
			//md.Update();
		}

		//如果是累加.
		if (flow.getFlowDevModel() == FlowDevModel.FoolTruck)
		{
			nd.setFormType(NodeFormType.FoolTruck); //设置为经典表单.
			nd.setNodeFrmID( "ND" + nodeID);
			nd.setFrmWorkCheckSta (FrmWorkCheckSta.Disable);
			nd.DirectUpdate();
		}

		//如果是绑定表单库的表单
		if (flow.getFlowDevModel() == FlowDevModel.RefOneFrmTree)
		{
			nd.setFormType(NodeFormType.RefOneFrmTree); //设置为经典表单.
			nd.setNodeFrmID( flow.getFrmUrl());
			nd.setFrmWorkCheckSta(FrmWorkCheckSta.Enable);
			nd.DirectUpdate();
			FrmNode fn = new FrmNode();
			fn.setFrmID(nd.getNodeFrmID());
			fn.setEnableFWC(FrmWorkCheckSta.Enable);
			fn.setNodeID(nd.getNodeID());
			fn.setFlowNo(flowNo);
			fn.setFrmSln(FrmSln.Readonly);
			fn.setMyPK(fn.getFrmID() + "_" + fn.getNodeID() + "_" + fn.getFlowNo());
			//执行保存.
			fn.Save();

		}
		//如果是Self类型的表单的类型
		if (flow.getFlowDevModel() == FlowDevModel.SDKFrmSelfPK || flow.getFlowDevModel() == FlowDevModel.SDKFrmWorkID)
		{
			nd.setHisFormType(NodeFormType.SDKForm);
			nd.setFormUrl (flow.getFrmUrl());
			nd.DirectUpdate();
		}

		//如果是Self类型的表单的类型
		if (flow.getFlowDevModel() == FlowDevModel.SelfFrm)
		{
			nd.setHisFormType (NodeFormType.SelfForm);
			nd.setFormUrl (flow.getFrmUrl());
			nd.DirectUpdate();
		}
		//如果是绑定表单库的表单
		if (flow.getFlowDevModel() == FlowDevModel.EntityTS)
		{
			nd.setFormType(NodeFormType.EntityTS); //设置为经典表单.
			nd.setFormUrl( flow.getFrmUrl());
			nd.setFrmWorkCheckSta(FrmWorkCheckSta.Enable);
			nd.DirectUpdate();
			FrmNode fn = new FrmNode();
			fn.setFrmID(flow.getFrmUrl());
			fn.setEnableFWC(FrmWorkCheckSta.Enable);
			fn.setNodeID(nd.getNodeID());
			fn.setFlowNo(flowNo);
			fn.setFrmSln(FrmSln.Readonly);
			fn.setMyPK(fn.getFrmID() + "_" + fn.getNodeID() + "_" + fn.getFlowNo());
			//执行保存.
			fn.Save();

		}
		nd.setFlowNo(flowNo);
		nd.Insert();

		//为创建节点设置默认值   部分方法
		String file = SystemConfig.getPathOfDataUser() + "XML/DefaultNewNodeAttr.xml";
		DataSet ds = new DataSet();

		if (1 == 2 && (new File(file)).isFile() == true)
		{
			ds.readXml(file);

			NodeExt ndExt = new NodeExt(nd.getNodeID());
			DataTable dt = ds.Tables.get(0);
			for (DataColumn dc : dt.Columns)
			{
				nd.SetValByKey(dc.ColumnName, dt.Rows.get(0).getValue(dc.ColumnName));
				ndExt.SetValByKey(dc.ColumnName, dt.Rows.get(0).getValue(dc.ColumnName));
			}

			ndExt.setFlowNo(flowNo);
			ndExt.setNodeID(nodeID);
			ndExt.DirectUpdate();
		}
		nd.setFWCVer(1); //设置为2019版本. 2018版是1个节点1个人,仅仅显示1个意见.
		nd.setNodeID(nodeID);
		nd.setHisDeliveryWay(DeliveryWay.BySelected);
		nd.setX(x);
		nd.setY(y);
		nd.setIcon(icon);
		nd.setStep(idx);

		//节点类型.
		nd.setHisNodeWorkType(NodeWorkType.Work);
		nd.setName("New Node " + idx);
		nd.setHisNodePosType(NodePosType.Mid);
		nd.setFlowNo(flow.getNo());
		nd.setFlowName(flow.getName());

		//设置审核意见的默认值.
		nd.SetValByKey(NodeWorkCheckAttr.FWCDefInfo, Glo.getDefValWFNodeFWCDefInfo());

		//设置节点运行模式.
		nd.setHisRunModel(RunModel.forValue(runModel));
		//设置节点类型
		nd.setHisNodeType(NodeType.forValue(nodeType));


		nd.Update(); //执行更新.
		nd.CreateMap();

		//通用的人员选择器.
		bp.wf.template.Selector select = new Selector(nd.getNodeID());
		select.setSelectorModel(SelectorModel.GenerUserSelecter);
		select.Update();

		//设置默认值。
		int state = 0;
		if (flow.getFlowDevModel() == FlowDevModel.JiJian)
		{
			state = 1;
		}

		//设置审核组件的高度.
		DBAccess.RunSQL("UPDATE WF_Node SET FWC_H=300,FTC_H=300," + NodeAttr.FWCSta + "=" + state + " WHERE NodeID='" + nd.getNodeID() + "'");

		//创建默认的推送消息.
		CreatePushMsg(nd);

		//写入日志.
		bp.sys.base.Glo.WriteUserLog("创建节点：" + nd.getName() + " - " + nd.getNodeID(), "通用操作");
		return nd;
	}
	private static void CreatePushMsg(Node nd) throws Exception {
		// 创建发送短消息,为默认的消息.
		bp.wf.template.PushMsg pm = new bp.wf.template.PushMsg();
		pm.Delete(PushMsgAttr.NodeID, nd.getNodeID()); //删除已经存在的.

		//设置节点发送成功消息提醒.
		pm.setEventNo(EventListNode.SendSuccess);
		pm.setNodeID(nd.getNodeID());
		pm.setFlowNo(nd.getFlowNo());
		pm.setPushWayNo("TodoEmps");
		pm.setSMSPushModel("Email");
		pm.setMyPK(DBAccess.GenerGUID(0, null, null));
		pm.Insert();

		//设置退回消息提醒.
		pm.setEventNo(EventListNode.ReturnAfter);
		pm.setNodeID(nd.getNodeID());
		pm.setFlowNo(nd.getFlowNo());
		pm.setPushWayNo("TodoEmps");
		pm.setMyPK(DBAccess.GenerGUID(0, null, null));
		pm.Insert();

		//设置撤销消息提醒.
		pm.setEventNo(EventListNode.UndoneAfter);
		pm.setNodeID(nd.getNodeID());
		pm.setFlowNo(nd.getFlowNo());
		pm.setPushWayNo("TodoEmps");
		pm.setMyPK(DBAccess.GenerGUID(0, null, null));
		pm.Insert();
	}

	/**
	 检查当前人员是否有修改该流程模版的权限.

	 @param flowNo
	 @return
	*/
	public static boolean CheckPower(String flowNo)
	{
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.GroupInc)
			return true;
		return true;
	}
	public static Document readXml(String filePath) throws Exception {
		File xmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();
		return doc;
	}

	/**
	 创建流程.
	 @param flowSort
	 @param filePath
	 @return
	 @exception Exception
	*/
	public static Flow NewFlowByBPMN(String flowSort, String filePath) throws Exception {
		Document doc = readXml(filePath);
		// #region 0. 读取文件.
		//读取流程属性
		NodeList dtFlow = doc.getElementsByTagName("process");
		//读取节点
		NodeList dtNode = doc.getElementsByTagName("userTask");
		// 读取结束节点
		NodeList dtEndNode = doc.getElementsByTagName("endEvent");
		//将结束节点放入节点中
		List<NamedNodeMap> dtList = new ArrayList<>();
		for (int i = 0; i < dtNode.getLength(); i++)
		{
			NamedNodeMap dr = dtNode.item(i).getAttributes();
			dtList.add(dr);
		}
		for (int i = 0; i < dtEndNode.getLength(); i++)
		{
			NamedNodeMap dr = dtEndNode.item(i).getAttributes();
			dtList.add(dr);
		}
		//读取网关节点
		NodeList dtGateway = doc.getElementsByTagName("exclusiveGateway");
		//读取方法条件
		NodeList dtDirs = doc.getElementsByTagName("sequenceFlow");
		//图形数据
		NodeList dtShapes = doc.getElementsByTagName("bpmndi:BPMNShape");

		NamedNodeMap drFlow = dtFlow.item(0).getAttributes();
		String flowName = drFlow.getNamedItem("name").getTextContent().toString(); //获得流程名称
		String flowMark = drFlow.getNamedItem("id").getTextContent().toString(); //获得流程标记.
		Flow fl = new Flow();
		fl.SetValByKey("FlowMark", flowMark);
		if (fl.RetrieveFromDBSources() == 1)
			throw new Exception("err@该流程[" + flowMark + "]已经导入,如果您要导入，请您修改模板的流程标记.");
		//#endregion 读取文件.

		// #region 1. 创建空白的模板做准备..
		String sortNo = "001"; //放入的流程目录.
		String flowNo = bp.wf.template.TemplateGlo.NewFlowTemplate(sortNo, flowName, DataStoreModel.ByCCFlow, null, null);

		fl.SetValByKey("No", flowNo);
		fl.RetrieveFromDBSources();
		fl.SetValByKey("FlowMark", flowMark);//更新标记.
		fl.SetValByKey("FK_FlowSort", flowSort);//更新流程目录
		fl.Update();

		//删除第2个节点信息.
		Node nd = new Node();
		nd.SetValByKey("NodeID", Integer.parseInt(flowNo + "02"));
		nd.Delete(); //删除节点.
		bp.wf.template.Directions dir = new Directions();
		dir.Delete(DirectionAttr.FK_Flow, flowNo);
		ConcurrentHashMap<String, Integer> relations = new ConcurrentHashMap<>(); // 保存节点id 对应 关系
		ConcurrentHashMap<Integer, String> nodeUserTaskID = new ConcurrentHashMap<>(); // 保存节点id与userTaskID 对应 关系
		ConcurrentLinkedDeque<Node> flowNodes = new ConcurrentLinkedDeque<>();
		for (int i = 0; i < dtList.size(); i++)
		{
			NamedNodeMap dr = dtList.get(i);

			// #region 获得节点信息.
			String userTaskID = dr.getNamedItem("id").getTextContent();
			if (i == 0)
				nd = new Node(Integer.parseInt(flowNo + "01")); //开始节点.
			else
				nd = TemplateGlo.NewNode(flowNo, 100, 100);

			// 找到图形信息
			boolean hasPostion = false;
			for (int j = 0; j < dtShapes.getLength(); j++)
			{
				org.w3c.dom.Node node = dtShapes.item(j);
				String bpmnElement = node.getAttributes().getNamedItem("bpmnElement").getTextContent();
				// 找到坐标
				if (bpmnElement.equals(userTaskID))
				{
					if(DataType.IsNullOrEmpty(dtShapes.item(j).getFirstChild()))
					{
						String err = "err@解析BPMN出现异常数据,节点" + node.toString() + "bpmnElement = " + bpmnElement + "没有找到坐标数据";
						Log.DebugWriteError(err);
						continue;
					}
					NamedNodeMap attrs = node.getFirstChild().getNextSibling().getAttributes();
					nd.SetValByKey("X", (int)Float.parseFloat(attrs.getNamedItem("x").getTextContent()));
					nd.SetValByKey("Y", (int)Float.parseFloat(attrs.getNamedItem("y").getTextContent()));
					hasPostion = true;
					break;
				}
			}
			if (!hasPostion)
			{
				nd.SetValByKey("X", 100);
				nd.SetValByKey("Y", 100);
			}
			List<String> lines = new ArrayList<>();
			// 找到所有以他为起点的连接线
			for (int k = 0; k < dtDirs.getLength(); k++)
			{
				org.w3c.dom.Node node = dtDirs.item(k);
				String sourceRef = node.getAttributes().getNamedItem("sourceRef").getTextContent();
				if (sourceRef.equals(userTaskID))
				{
					String targetRef = node.getAttributes().getNamedItem("targetRef").getTextContent();
					lines.add(targetRef);
				}
			}
			nd.SetValByKey("Name", dr.getNamedItem("name").getTextContent());
			nd.SetValByKey("Mark", StringHelper.join(",", lines.toArray(new String[0])));
			nd.Update(); //更新节点信息.

			relations.put(userTaskID, nd.getNodeID());  // 保存关系
			nodeUserTaskID.put(nd.getNodeID(), userTaskID);  // 保存关系
			flowNodes.add(nd);  // 保存节点
			//#endregion 获得节点信息.

		}
		// #endregion 2. 生成节点.

		//#region 3. 生成网关节点
		for (int i = 0; i < dtGateway.getLength(); i++)
		{
			NamedNodeMap dr = dtGateway.item(i).getAttributes();
			String gatewayID = dr.getNamedItem("id").getTextContent();
			//创建网关节点
			nd = TemplateGlo.NewNode(flowNo, 100, 100, null, RunModel.Ordinary.getValue(), NodeType.RouteNode.getValue());
			// 找到图形信息
			boolean hasPostion = false;
			for (int j = 0; j < dtShapes.getLength(); j++)
			{
				org.w3c.dom.Node node = dtShapes.item(j);
				String bpmnElement = node.getAttributes().getNamedItem("bpmnElement").getTextContent();
				// 找到坐标
				if (bpmnElement.equals(gatewayID))
				{
					if(DataType.IsNullOrEmpty(dtShapes.item(j).getFirstChild()))
					{
						String err = "err@解析BPMN出现异常数据,节点" + node.toString() + "bpmnElement = " + bpmnElement + "没有找到坐标数据";
						Log.DebugWriteError(err);
						continue;
					}
					NamedNodeMap attrs = node.getFirstChild().getNextSibling().getAttributes();
					nd.SetValByKey("X", (int)Float.parseFloat(attrs.getNamedItem("x").getTextContent()));
					nd.SetValByKey("Y", (int)Float.parseFloat(attrs.getNamedItem("y").getTextContent()));
					hasPostion = true;
					break;
				}
			}
			if (!hasPostion)
			{
				nd.SetValByKey("X", 100);
				nd.SetValByKey("Y", 100);
			}
			//保存连接线
			List<String> lines = new ArrayList<>();
			//保存连接线的描述
			List<String> docs = new ArrayList<>();
			// 找到所有以他为起点的连接线
			for (int k = 0; k < dtDirs.getLength(); k++)
			{
				org.w3c.dom.Node node = dtDirs.item(k);
				String sourceRef = node.getAttributes().getNamedItem("sourceRef").getTextContent();
				if (sourceRef.equals(gatewayID))
				{
					String targetRef = node.getAttributes().getNamedItem("targetRef").getTextContent();
					lines.add(targetRef);
					String name = node.getAttributes().getNamedItem("name") == null ? "" : node.getAttributes().getNamedItem("name").getTextContent();
					docs.add(name);
				}
			}
			nd.Update(); //更新节点信息.
			nd.SetValByKey("Name", dr.getNamedItem("name").getTextContent());
			nd.SetValByKey("Mark", StringHelper.join(",", lines.toArray(new String[0])));
			nd.SetValByKey("Doc", StringHelper.join(",", docs.toArray(new String[0])));
			nd.Update(); //更新节点信息.

			relations.put(gatewayID, nd.getNodeID());  // 保存关系
			nodeUserTaskID.put(nd.getNodeID(), gatewayID);  // 保存关系
			flowNodes.add(nd);  // 保存节点
		}
		//#endregion 3. 生成网关节点

		// #region 4. 生成链接线.
		// 插入连接线，经过上面的流程后才知道对应关系
		for(Node node : flowNodes)
		{
			try
			{
				String[] toUserTasks = node.GetValStrByKey("Mark").split(",");
				node.SetValByKey("Mark", nodeUserTaskID.get(node.getNodeID()));
				String[] nodeDoc = new String[0];
				//判断如果是路由节点
				if (node.getHisNodeType() == NodeType.RouteNode)
				{
					nodeDoc = node.GetValStrByKey("Doc").split(",");
					node.SetValByKey("Doc", "");//清空临时保存的描述
				}
				node.Update();
				for (int i = 0; i< toUserTasks.length; i++)
				{
					String toUserTask = toUserTasks[i];
					int toNodeId = relations.get(toUserTask);
					//生成方向.
					Direction mydir = new Direction();
					mydir.SetValByKey("FK_Flow", flowNo);
					mydir.SetValByKey("Node", node.getNodeID());
					mydir.SetValByKey("ToNode", toNodeId);
					//判断如果有描述就添加
					if(nodeDoc.length > 0)
					{
						mydir.SetValByKey("Des", nodeDoc[i]);
					}
					mydir.Insert(); //自动生成主键.
				}
			}
			catch (Exception ex)
			{
				Log.DebugWriteError("解析BPMN-创建连接线失败：nodeId=" + node.getNodeID() + ", Mark = " + node.GetValStrByKey("Mark"));
			}

		}
		// #endregion 4. 生成链接线.

		return fl;
	}
	/**
	 创建一个流程模版

	 @param flowSort 流程类别
	 @param flowName 名称
	 @param dsm 存储方式
	 @param ptable 物理量
	 @param flowMark 标记
	 @return 创建的流程编号
	*/
	public static String NewFlowTemplate(String flowSort, String flowName, bp.wf.template.DataStoreModel dsm, String ptable, String flowMark) throws Exception {
		//定义一个变量.
		Flow flow = new Flow();
		try
		{
			//检查参数的完整性.
			if (DataType.IsNullOrEmpty(ptable) == false && ptable.length() >= 1)
			{
				String c = ptable.substring(0, 1);
				if (DataType.IsNumStr(c) == true)
				{
					throw new RuntimeException("@非法的流程数据表(" + ptable + "),它会导致ccflow不能创建该表.");
				}
			}

			flow.SetValByKey("PTable",  ptable);
			flow.setPTable(ptable);
			flow.setFlowSortNo(flowSort);
			flow.setFlowMark(flowMark);

			if (DataType.IsNullOrEmpty(flowMark) == false)
			{
				if (flow.IsExit(FlowAttr.FlowMark, flowMark))
				{
					throw new RuntimeException("@该流程标示:" + flowMark + "已经存在于系统中.");
				}
			}

			/*给初始值*/
			//this.Paras = "@StartNodeX=10@StartNodeY=15@EndNodeX=40@EndNodeY=10";
			flow.setParas( "@StartNodeX=200@StartNodeY=50@EndNodeX=200@EndNodeY=350");

			flow.setNo(flow.GenerNewNoByKey(FlowAttr.No, null));
			flow.setName(flowName);
			if ((flow.getName() == null || flow.getName().isEmpty()))
			{
				flow.setName("新建流程" + flow.getNo()); //新建流程.
			}

			//if (flow.getIsExits( true)
			//    throw new Exception("err@系统出现自动生成的流程编号重复.");

			if (bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.Single)
			{
				flow.setOrgNo(WebUser.getOrgNo()); //隶属组织
			}

			flow.setPTable("ND" + Integer.parseInt(flow.getNo()) + "Rpt");

			// 设置创建人，创建日期.
			flow.SetValByKey(FlowAttr.CreateDate, DataType.getCurrentDateTime());
			flow.SetValByKey(FlowAttr.Creater, WebUser.getNo());
			// flow.SetValByKey("Icon", "icon-people");
			flow.SetValByKey("ICON", "icon-people"); //图标.

			//flow.TitleRole
			flow.Insert();

			////如果是集团模式下.
			//if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
			//{
			//    // 记录创建人.
			//    FlowExt fe = new FlowExt(flow.getNo());
			//    fe.DesignerNo = bp.web.WebUser.getNo();
			//    fe.DesignerName = bp.web.WebUser.getName();
			//    fe.DesignTime = DataType.getCurrentDateTime();
			//    fe.DirectUpdate();
			//}

			Node nd = new Node();
			nd.setNodeID(Integer.parseInt(flow.getNo() + "01"));
			nd.setName("Start Node"); //  "开始节点";
			nd.setStep(1);
			nd.setFlowNo(flow.getNo());
			nd.setFlowName(flow.getName());
			nd.setHisNodePosType(NodePosType.Start);
			nd.setHisNodeWorkType(NodeWorkType.StartWork);
			nd.setX(200);
			nd.setY(150);
			nd.setNodePosType(NodePosType.Start);
			nd.setHisReturnRole(ReturnRole.CanNotReturn); //不能退回.
			nd.setIcon("前台");
			nd.setMark(flow.getNo() + "01");
			//增加了两个默认值值 . 2016.11.15. 目的是让创建的节点，就可以使用.
			nd.setCondModel(DirCondModel.ByDDLSelected); //默认的发送方向.
			nd.setHisDeliveryWay(DeliveryWay.BySelected); //上一步发送人来选择.
			nd.setFormType(NodeFormType.FoolForm); //设置为经典表单.

			//如果是集团模式.
			if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
			{
				if (DataType.IsNullOrEmpty(WebUser.getOrgNo()) == true)
				{
					throw new RuntimeException("err@登录信息丢失了组织信息,请重新登录.");
				}

				nd.setHisDeliveryWay(DeliveryWay.BySelectedOrgs);

				//把本组织加入进去.
				FlowOrg fo = new FlowOrg();
				String mypk = WebUser.getOrgNo() + "_" + nd.getFlowNo();
				fo.Delete("MyPK", mypk);
				fo.SetValByKey("MyPK", mypk);
				fo.setFlowNo(nd.getFlowNo());
				fo.setOrgNo(WebUser.getOrgNo());
				fo.Insert();
			}

			nd.Insert();
			nd.CreateMap();

			//为开始节点增加一个删除按钮.
			String sql = "UPDATE WF_Node SET DelEnable=1 WHERE NodeID=" + nd.getNodeID();
			DBAccess.RunSQL(sql);

			//nd.getHisWork().CheckPhysicsTable();  去掉，检查的时候会执行.
			CreatePushMsg(nd);

			//通用的人员选择器.
			bp.wf.template.Selector select = new Selector(nd.getNodeID());
			select.setSelectorModel(SelectorModel.GenerUserSelecter);
			select.Update();

			nd = new Node();

			//为创建节点设置默认值
			String fileNewNode = SystemConfig.getPathOfDataUser() + "XML/DefaultNewNodeAttr.xml";
			if ((new File(fileNewNode)).isFile() == true && 1 == 2)
			{
				DataSet myds = new DataSet();
				myds.readXml(fileNewNode);
				DataTable dt = myds.Tables.get(0);
				for (DataColumn dc : dt.Columns)
				{
					nd.SetValByKey(dc.ColumnName, dt.Rows.get(0).getValue(dc.ColumnName));
				}
			}
			else
			{
				nd.setHisNodePosType(NodePosType.Mid);
				nd.setHisNodeWorkType(NodeWorkType.Work);
				nd.setX(200);
				nd.setY(250);
				nd.setIcon("审核");
				nd.setNodePosType(NodePosType.End);

				//增加了两个默认值值 . 2016.11.15. 目的是让创建的节点，就可以使用.
				nd.setCondModel(DirCondModel.ByDDLSelected); //默认的发送方向.
				nd.setHisDeliveryWay(DeliveryWay.BySelected); //上一步发送人来选择.
				nd.setFormType(NodeFormType.FoolForm); //设置为经典表单.
			}

			nd.setNodeID(Integer.parseInt(flow.getNo() + "02"));
			nd.setName("Node 2"); // "结束节点";
			nd.setStep(2);
			nd.setFlowNo(flow.getNo());
			nd.setFlowName(flow.getName());
			nd.setHisDeliveryWay(DeliveryWay.BySelected); //上一步发送人来选择.
			nd.setFormType(NodeFormType.FoolForm); //设置为经典表单.
			nd.setCondModel(DirCondModel.ByDDLSelected);

			nd.setX(200);
			nd.setY(250);

			//设置审核意见的默认值.
			nd.SetValByKey(NodeWorkCheckAttr.FWCDefInfo, Glo.getDefValWFNodeFWCDefInfo());
			nd.setMark(flow.getNo() + "02");
			nd.Insert();
			nd.CreateMap();
			//nd.getHisWork().CheckPhysicsTable(); //去掉，检查的时候会执行.
			CreatePushMsg(nd);

			//通用的人员选择器.
			select = new Selector(nd.getNodeID());
			select.setSelectorModel(SelectorModel.GenerUserSelecter);
			select.Update();

			MapData md = new MapData();
			md.setNo("ND" + Integer.parseInt(flow.getNo()) + "Rpt");
			md.setName(flow.getName());
			md.Save();

			// 装载模版.
			String file = SystemConfig.getPathOfDataUser() + "XML/TempleteSheetOfStartNode.xml";
			if ((new File(file)).isFile() == true && 1 == 2)
			{
				//throw new Exception("@开始节点表单模版丢失" + file);
				/*如果存在开始节点表单模版*/
				DataSet ds = new DataSet();
				ds.readXml(file);

				String nodeID = "ND" + Integer.parseInt(flow.getNo() + "01");
				MapData.ImpMapData(nodeID, ds);
			}

			//加载默认字段.
			if (1 == 1)
			{
				String frmID = "ND" + Integer.parseInt(flow.getNo() + "01");
				//创建表单ID。
				int groupID = DBAccess.RunSQLReturnValInt("SELECT MAX(GroupID) FROM Sys_MapAttr WHERE FK_MapData='" + frmID + "'", 0);
				if (groupID == 0)
				{
					GroupField gf = new GroupField();
					gf.setLab("基本信息");
					gf.setFrmID(frmID);
					gf.Insert();
					groupID = (int) gf.getOID();
				}

				MapAttr attr = new MapAttr();
				attr.setMyPK(frmID + "_SQR");
				attr.setName("申请人");
				attr.setKeyOfEn("SQR");
				attr.setDefValReal("@WebUser.Name");
				attr.setUIVisible(true);
				attr.setUIIsEnable(false);
				attr.setFrmID(frmID);
				attr.setColSpan(1);
				attr.setGroupID(groupID);
				attr.Insert();

				attr = new MapAttr();
				attr.setMyPK(frmID + "_SQDT");
				attr.setName("申请日期");
				attr.setKeyOfEn("SQRQ");
				attr.setDefValReal("@RDT");
				attr.setUIVisible(true);
				attr.setUIIsEnable(false);
				attr.setFrmID(frmID);
				attr.setMyDataType(DataType.AppDateTime);
				attr.setColSpan(1);
				attr.setGroupID(groupID);
				attr.Insert();

				attr = new MapAttr();
				attr.setMyPK(frmID + "_SQDept");
				attr.setName("申请人部门");
				attr.setKeyOfEn("SQDept");
				attr.setDefValReal("@WebUser.FK_DeptName");
				attr.setUIVisible(true);
				attr.setUIIsEnable(false);
				attr.setFrmID(frmID);
				attr.setColSpan(3);
				attr.setGroupID(groupID);
				attr.Insert();
			}

			//创建track.
			Track.CreateOrRepairTrackTable(flow.getNo());

		}
		catch (RuntimeException ex)
		{
			/**删除垃圾数据.
			*/
			flow.DoDelete();
			//提示错误.
			throw new RuntimeException("err@创建流程错误:" + ex.getMessage());
		}

		//FlowExt flowExt = new FlowExt(flow.getNo());
		//flowExt.DesignerNo = bp.web.WebUser.getNo();
		//flowExt.DesignerName = bp.web.WebUser.getName();
		//flowExt.DesignTime = DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss");
		//flowExt.DirectSave();

		//创建连线
		Direction drToNode = new Direction();
		drToNode.setFlowNo(flow.getNo());
		drToNode.setNode(Integer.parseInt(Integer.parseInt(flow.getNo()) + "01"));
		drToNode.setToNode(Integer.parseInt(Integer.parseInt(flow.getNo()) + "02"));
		drToNode.Save();

		//增加方向.
		Node mynd = new Node(drToNode.getNode());
		mynd.setHisToNDs( String.valueOf(drToNode.getToNode()));
		mynd.Update();

		//设置流程的默认值.
		for (String key : SystemConfig.getAppSettings().keySet())
		{
			if (key.contains("NewFlowDefVal") == false)
			{
				continue;
			}

			String val = (String) SystemConfig.getAppSettings().get(key);
			//设置值.
			flow.SetValByKey(key.replace("NewFlowDefVal_", ""), val);
		}
		//执行一次流程检查, 为了节省效率，把检查去掉了.
		flow.DoCheck();

		//写入日志.
		bp.sys.base.Glo.WriteUserLog("创建流程：" + flow.getName() + " - " + flow.getNo(), "通用操作");

		return flow.getNo();
	}
	/**
	 删除节点.

	 @param nodeid
	*/
	public static void DeleteNode(int nodeid) throws Exception {
		Node nd = new Node(nodeid);
		nd.Delete();
	}
}
