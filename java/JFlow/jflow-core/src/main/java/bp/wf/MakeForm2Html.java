package bp.wf;

import bp.difference.SystemConfig;
import bp.sys.*;
import bp.da.*;
import bp.en.*;
import bp.tools.*;
import bp.web.*;
import bp.port.*;
import bp.wf.rpt.*;
import bp.wf.template.*;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class MakeForm2Html
{
	private static StringBuilder GenerHtmlOfFool(MapData mapData, String frmID, String workid, Entity en, String path, String flowNo, String FK_Node, NodeFormType formType) throws Exception
	{
		StringBuilder sb = new StringBuilder();

		//审核意见
		DataTable dt = new DataTable();
		String sql = "";
		if(flowNo != null) {
			sql = "SELECT NDFromT,Msg,RDT,EmpFromT,EmpFrom,NDFrom FROM ND" + Integer.parseInt(flowNo) + "Track WHERE WorkID=" + Long.parseLong(workid) + " AND ActionType=" + ActionType.WorkCheck.getValue() + "  ORDER BY RDT ";
			dt = DBAccess.RunSQLReturnTable(sql);
		}
		//字段集合.
		MapAttrs mapAttrs = new MapAttrs(frmID);
		//获取当前表单的联动项
		FrmRBs frmRBs = new FrmRBs(frmID);
		Attrs attrs = null;
		GroupFields gfs = null;
		if (formType == NodeFormType.FoolTruck && DataType.IsNullOrEmpty(FK_Node) == false)
		{
			Node nd = new Node(FK_Node);
			Work wk = nd.getHisWork();
			wk.setOID(Long.parseLong(workid));
			wk.RetrieveFromDBSources();

			// 求出来走过的表单集合
			sql = "SELECT NDFrom FROM ND" + Integer.parseInt(flowNo) + "Track A, WF_Node B ";
			sql += " WHERE A.NDFrom=B.NodeID  ";
			sql += "  AND (ActionType=" + ActionType.Forward.getValue() + " OR ActionType=" + ActionType.Start.getValue()  + "  OR ActionType=" + ActionType.Skip.getValue()+ ")  ";
			sql += "  AND B.FormType=" + NodeFormType.FoolTruck.getValue()  + " "; // 仅仅找累加表单.
			sql += "  AND NDFrom!=" + Integer.parseInt(FK_Node.replace("ND", "")) + " "; //排除当前的表单.


			sql += "  AND (A.WorkID=" + workid + ") ";
			sql += " ORDER BY A.RDT ";

			// 获得已经走过的节点IDs.
			DataTable dtNodeIDs = DBAccess.RunSQLReturnTable(sql);
			String frmIDs = "";
			if (dtNodeIDs.Rows.size() > 0)
			{
				//把所有的节点字段.
				for (DataRow dr : dtNodeIDs.Rows)
				{
					if (frmIDs.contains("ND" + dr.getValue(0).toString()) == true)
						continue;
					frmIDs += "'ND" + dr.getValue(0).toString() + "',";
				}
			}

			if (frmIDs.equals(""))
				frmIDs = "'" + mapData.getNo() + "'";
			else
				frmIDs = frmIDs.substring(0, frmIDs.length() - 1);

			GenerWorkFlow gwf = new GenerWorkFlow(workid);
			if (gwf.getIsOver()==true)
				frmIDs = frmIDs + ",'" + FK_Node + "'";
			gfs = new GroupFields();
			gfs.RetrieveIn(GroupFieldAttr.FrmID, "(" + frmIDs + ")");

			mapAttrs = new MapAttrs();
			mapAttrs.RetrieveIn(MapAttrAttr.FK_MapData, "(" + frmIDs + ")", "GroupID, Idx");
			frmRBs = new FrmRBs();
			frmRBs.RetrieveIn(FrmRBAttr.FrmID, "(" + frmIDs + ")");
		}
		else
		{
			gfs = new GroupFields(frmID);
			attrs = en.getEnMap().getAttrs();
		}

		String hideField = ",";
		String showField = ",";
		if (frmRBs.size() > 0)
		{
			for (MapAttr mapAttr : mapAttrs.ToJavaList())
			{
				if (mapAttr.GetParaBoolen("IsEnableJS") == true)
				{
					String val = en.GetValStrByKey(mapAttr.getKeyOfEn());
					FrmRB rb = (FrmRB)frmRBs.GetEntityByKey(mapAttr.getMyPK() + "_" + val);
					if (rb == null)
						continue;
					String cfgs = rb.getFieldsCfg();
					if (DataType.IsNullOrEmpty(cfgs) == true)
						continue;
					AtPara atPara = new AtPara(cfgs);
					//获取显示的字段
					for(String key : atPara.getHisHT().keySet())
					{
						int keyVal = atPara.GetValIntByKey(key);
						if (keyVal == 0)
							continue;
						if (keyVal == 3)
							hideField += key + ",";
						if (keyVal == 2 || keyVal == 4)
							showField += key + ",";
					}
				}
			}
		}

		//生成表头.
		String frmName = mapData.getName();
		sb.append(" <table style='width:950px;height:auto;border:1px solid #cacaca;' >");

		///#region 生成头部信息.
		sb.append("<tr>");

		sb.append("<td colspan=4 >");

		sb.append("<table border=0 style='width:950px;'>");

		sb.append("<tr  style='border:0px;' >");

		//二维码显示
		boolean IsHaveQrcode = true;
//		if (SystemConfig.GetValByKeyBoolen("IsShowQrCode", false) == false)
//			IsHaveQrcode = false;

		//判断当前文件是否存在图片
		boolean IsHaveImg = false;
		String IconPath = path + "/icon.png";
		if (new File(IconPath).exists()== true)
			IsHaveImg = false;
		if (IsHaveImg == true)
		{
			sb.append("<td>");
			sb.append("<img src='icon.png' style='height:100px;border:0px;' />");
			sb.append("</td>");
		}
		if (IsHaveImg == false && IsHaveQrcode == false)
			sb.append("<td style='text-align: center;' colspan=6>");
		else if ((IsHaveImg == true && IsHaveQrcode == false) || (IsHaveImg == false && IsHaveQrcode == true))
			sb.append("<td style='text-align: center;' colspan=5>");
		else
			sb.append("<td style='text-align: center;' colspan=4>");

		for (DataRow dr : dt.Rows){
			String step=dr.getValue("NDFromT").toString();
			if(step.equals("拒绝")||step.equals("终止流程实例")){
				IsHaveQrcode=false;
				break;
			}
		}

		if (IsHaveQrcode == true)
		{
//			sb.append("<td>");
//			sb.append(" <img src='QR.png' style='height:100px;'  />");
//			sb.append("</td>");
			sb.append("<h2><b>" + frmName + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src='icon.png' style='height:100px;border:0px;' /></b></h2>");
			sb.append("</td>");
		}
		else{
			sb.append("<h2><b>" + frmName + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></h2>");
			sb.append("</td>");
		}


		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td colspan=6>");
//		成都同步新创科技股份有限公司&nbsp;&nbsp;&nbsp;&nbsp;
		sb.append(" <b>创建时间："+en.GetValStrByKey("RDT")+"</b>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");

		sb.append("</td>");
		///#endregion 生成头部信息.

		if (DataType.IsNullOrEmpty(FK_Node) == false && DataType.IsNullOrEmpty(flowNo) == false)
		{
			Node nd = new Node(Integer.parseInt(FK_Node.replace("ND", "")));
			if (frmID.startsWith("ND") == true && nd.getFrmWorkCheckSta() != FrmWorkCheckSta.Disable)
			{
				Object tempVar = gfs.GetEntityByKey(GroupFieldAttr.CtrlType, "FWC");
				GroupField gf = (GroupField)((tempVar instanceof GroupField) ? tempVar : null);
				if (gf == null)
				{
					gf = new GroupField();
					gf.setOID(100);
					gf.setFrmID(nd.getNodeFrmID());
					gf.setCtrlType("FWC");
					gf.setCtrlID("FWCND" + nd.getNodeID());
					gf.setIdx(100);
					gf.setLab("审核信息");
					gfs.AddEntity(gf);
				}
			}

		}

		boolean isHaveFWC = false;
		for (GroupField gf : gfs.ToJavaList())
		{
			//输出标题.
			if ( ! gf.getCtrlType().equals("Ath") && gf.getCtrlType().equals("FWC") == false)
			{
				sb.append(" <tr>");
				sb.append("  <th colspan=4><b>" + gf.getLab() + "</b></th>");
				sb.append(" </tr>");
			}

			///#region 输出字段.
			if (gf.getCtrlID().equals("") && gf.getCtrlType().equals(""))
			{
				boolean isDropTR = true;
				String html = "";
				for (MapAttr attr : mapAttrs.ToJavaList())
				{
					//1.处理隐藏字段，如果是不可见并且是启用的就隐藏. 2.处理分组数据，非当前分组的数据不输出
					if (attr.getUIVisible() == false && showField.contains("," + attr.getKeyOfEn() + ",") == false)
						continue;
					if(hideField.contains("," + attr.getKeyOfEn() + ",") == true)
						continue;
					if(attr.getGroupID() != gf.getOID())
						continue;
					String text = "";

					switch (attr.getLGType())
					{
						case Normal: // 输出普通类型字段.
							if (attr.getMyDataType() == 1 && attr.getUIContralType() == UIContralType.DDL)
							{
								text = en.GetValStrByKey(attr.getKeyOfEn()+"Text");
								if (DataType.IsNullOrEmpty(text))
								{
									text = en.GetValStrByKey(attr.getKeyOfEn() + "T");
								}
							}
							else
							{
								//判断是不是图片签名
								if (attr.getItIsSigan() == true)
								{
									String SigantureNO = en.GetValStrByKey(attr.getKeyOfEn());
									String src = SystemConfig.getHostURL() + "/DataUser/Siganture/";
									text = "<img src='" + src + SigantureNO + ".jpg' title='" + SigantureNO + "' onerror='this.src=\"" + src + "Siganture.jpg\"' style='height:50px;'  alt='图片丢失' /> ";
								} else if (attr.getUIContralType() == UIContralType.SignCheck)//是不是签批字段
								{
									//获取当前节点的审核意见
									DataTable mydt = GetWorkcheckInfoByNodeIDs(dt, en.GetValStrByKey(attr.getKeyOfEn()));
									text = "<div style='min-height:17px;'>";
									text += "<table style='width:100%'><tbody>";
									for (DataRow dr : mydt.Rows)
									{
										text += "<tr><td style='border: 1px solid #D6DDE6;'>";
										text += "<div style='word-wrap: break-word;line-height:20px;padding:5px;padding-left:50px;'><font color='#999'>" + dr.getValue(1).toString() + "</font></div>";
										text += "<div style='text-align:right;padding-right:5px'>" + dr.getValue(3).toString() + "(" + dr.getValue(2).toString() + ")</div>";
										text += "</td></tr>";
									}
									text += "</tbody></table></div>";
								} else{
									text = en.GetValStrByKey(attr.getKeyOfEn()+"T");
									if (DataType.IsNullOrEmpty(text))
										text = en.GetValStrByKey(attr.getKeyOfEn());
								}


								String ctrlType = attr.getatPara().GetValStrByKey("CtrlType");
								//如果是关联流程
								if (!DataType.IsNullOrEmpty(ctrlType) && "FlowRefLink".equals(ctrlType))
								{
									text = en.GetValStrByKey(attr.getKeyOfEn());
									String str = text.split("[,]")[0];
									if(str.split("=").length>1)
										text = str.split("=")[1];

								}

								if (attr.getTextModel() == 3)
									text = text.replace("white-space: nowrap;", "");

								if (attr.getUIContralType() == UIContralType.AthShow)
								{
									FrmAttachmentDBs athDBsFrom = new FrmAttachmentDBs();

									int i=athDBsFrom.Retrieve(FrmAttachmentDBAttr.NoOfObj, attr.getKeyOfEn(), FrmAttachmentDBAttr.RefPKVal, workid);
									text = "附件("+i+"个)";
									//把附件拷贝指定的位置
									if (new File(path + "/pdf/").exists() == false)
									{
										new File(path + "/pdf/").mkdir();
									}
									FrmAttachment ath = new FrmAttachment();
									ath.setMyPK(attr.getMyPK());
									if (ath.RetrieveFromDBSources() != 0){
										for (FrmAttachmentDB item : athDBsFrom.ToJavaList())
										{
											String fileTo = path + "/pdf/" + item.getFileName();
											//加密信息
											boolean fileEncrypt = SystemConfig.isEnableAthEncrypt();
											boolean isEncrypt = item.GetParaBoolen("IsEncrypt");
											///#region 从ftp服务器上下载.
											if (ath.getAthSaveWay() == AthSaveWay.FTPServer)
											{
												try
												{
													if (new File(fileTo).exists() == true)
														new File(fileTo).delete();

													//把文件copy到,
													String file = item.GenerTempFile(ath.getAthSaveWay());

													String fileTempDecryPath = file;
													if (fileEncrypt == true && isEncrypt == true)
													{
														fileTempDecryPath = file + ".tmp";
														AesEncodeUtil.decryptFile(file, fileTempDecryPath);

													}
													Files.copy(Paths.get(fileTempDecryPath), Paths.get(fileTo), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

												}
												catch (RuntimeException ex)
												{
												}
											}
											///#endregion 从ftp服务器上下载.


											///#region 从iis服务器上下载.
											if (ath.getAthSaveWay() == AthSaveWay.IISServer)
											{
												try {

													String fileTempDecryPath = item.getFileFullName();
													if (fileEncrypt == true && isEncrypt == true) {
														fileTempDecryPath = item.getFileFullName() + ".tmp";
														AesEncodeUtil.decryptFile(item.getFileFullName(), fileTempDecryPath);

													}

													//把文件copy到,
													Files.copy(Paths.get(fileTempDecryPath), Paths.get(fileTo), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

												}
												catch (RuntimeException ex)
												{
												}
											}

										}

									}

								}
							}

							if(attr.getMyDataType() == DataType.AppDouble || attr.getMyDataType() == DataType.AppFloat ||
									attr.getMyDataType() == DataType.AppMoney){
									String defval = attr.getDefVal();
									if(defval ==""||defval==null||defval =="0")
										defval="0.00";
									String[] res = defval.split("[.]", -1);
									int leg = 0;
									if(res.length>1)
										leg = res[1].split("").length;
									text = en.GetValDecimalByKey(attr.getKeyOfEn(), leg).toString();
							}
							break;
						case Enum:
							if (attr.getUIContralType() == UIContralType.CheckBok)
							{
								String s = en.GetValStrByKey(attr.getKeyOfEn()) + ",";
								SysEnums enums = new SysEnums(attr.getUIBindKey());
								for (SysEnum se : enums.ToJavaList())
								{
									if (s.indexOf(se.getIntKey() + ",") != -1)
									{
										text += se.getLab() + " ";
									}
								}

							}
							else
							{
								text = en.GetValRefTextByKey(attr.getKeyOfEn());
							}
							break;
						case FK:
							text = en.GetValRefTextByKey(attr.getKeyOfEn());
							break;
						default:
							break;
					}

					if (attr.getItIsBigDoc())
					{
						//这几种字体生成 pdf都乱码
						text = text.replace("仿宋,", "宋体,");
						text = text.replace("仿宋;", "宋体;");
						text = text.replace("仿宋\"", "宋体\"");
						text = text.replace("黑体,", "宋体,");
						text = text.replace("黑体;", "宋体;");
						text = text.replace("黑体\"", "宋体\"");
						text = text.replace("楷体,", "宋体,");
						text = text.replace("楷体;", "宋体;");
						text = text.replace("楷体\"", "宋体\"");
						text = text.replace("隶书,", "宋体,");
						text = text.replace("隶书;", "宋体;");
						text = text.replace("隶书\"", "宋体\"");
					}

					if (attr.getMyDataType() == DataType.AppBoolean)
					{
						if (DataType.IsNullOrEmpty(text) || text.equals("0"))
							text = "否";//text = "[&#10005]";
						else
							text = "是";//text = "[&#10004]";
					}

					//线性展示并且colspan=3
					if (attr.getColSpan() == 3 || (attr.getColSpan() == 4 && attr.getUIHeightInt() < 30))
					{
						isDropTR = true;
						html += " <tr>";
						html += " <td  class='FoolFrmFieldCtrl' style='width:143px' >" + attr.getName() + "</td>";
						html += " <td  ColSpan=3 style='width:712.5px' class='FContext'>";
						html += text;
						html += " </td>";
						html += " </tr>";
						continue;
					}

					//线性展示并且colspan=4
					if (attr.getColSpan() == 4)
					{
						isDropTR = true;
						html += " <tr>";
						html += " <td ColSpan=4 class='FoolFrmFieldCtrl' >" + attr.getName() + "</td>";
						html += " </tr>";
						html += " <tr>";
						html += " <td ColSpan=4 class='FContext'>";
						html += text;
						html += " </td>";
						html += " </tr>";
						continue;
					}

					if (isDropTR == true)
					{
						html += " <tr>";
						html += " <td class='FoolFrmFieldCtrl' style='width:143px'>" + attr.getName() + "</td>";
						html += " <td class='FContext' style='width:332px'>";
						html += text;
						html += " </td>";
						isDropTR = !isDropTR;
						continue;
					}

					if (isDropTR == false)
					{
						html += " <td  class='FoolFrmFieldCtrl'style='width:143px'>" + attr.getName() + "</td>";
						html += " <td class='FContext' style='width:332px'>";
						html += text;
						html += " </td>";
						html += " </tr>";
						isDropTR = !isDropTR;
						continue;
					}
				}
				sb.append(html); //增加到里面.
				continue;
			}
			///#endregion 输出字段.

			///#region 如果是从表.
			if (gf.getCtrlType().equals("Dtl"))
			{
				if (DataType.IsNullOrEmpty(gf.getCtrlID()) == true)
				{
					continue;
				}
				MapDtl mapdtl = new MapDtl(gf.getCtrlID());
				// 如果是从表
				MapAttrs attrsOfDtls = null;
				try
				{
					attrsOfDtls = new MapAttrs(gf.getCtrlID());
				}
				catch (RuntimeException ex)
				{
				}
				int columNum = 0;
				for (MapAttr item : attrsOfDtls.ToJavaList())
				{
					if (item.getKeyOfEn().equals("OID"))
					{
						continue;
					}
					if (item.getUIVisible() == false)
					{
						continue;
					}
					columNum++;
				}
				int columWidth = (int)100 / columNum;
				///#region 输出标题.
				// 判断是否存在二级表头，三级表头
				String secondHeader = mapdtl.GetParaString("MultiTitle",""); //二级表头
				String gradeHeader = mapdtl.GetParaString("MultiTitle1",""); //三级表头
				boolean isHaveGradeHeader = DataType.IsNullOrEmpty(gradeHeader)==true?false:true;
				boolean isSecondHeader = DataType.IsNullOrEmpty(secondHeader)==true?false:true;
				sb.append("<tr><td valign=top colspan=4 >");
				sb.append("<table style='width:950px'>");
				//不存在二级表头，三级表头
				if(isHaveGradeHeader == false && isSecondHeader == false){
					sb.append("<tr style='page-break-before: always;page-break-after: always;page-break-inside: avoid;'>");
					for (MapAttr item : attrsOfDtls.ToJavaList())
					{
						if (item.getKeyOfEn().equals("OID"))
							continue;
						if (item.getUIVisible()== false)
							continue;

						sb.append("<th style='width:" + columWidth + "%;text-align:center;white-space: normal !important;'>" + item.getName() + "</th>");
					}
					sb.append("</tr>");
					//不存在三级表头
				}else if(isHaveGradeHeader == false && isSecondHeader == true){
					StringBuilder firstStr = new StringBuilder("<tr style='page-break-before: always;page-break-after: always;page-break-inside: avoid;'>");
					StringBuilder secondStr = new StringBuilder("<tr style='page-break-before: always;page-break-after: always;page-break-inside: avoid;'>");
					String userFields="";
					for (MapAttr item : attrsOfDtls.ToJavaList())
					{
						if (item.getKeyOfEn().equals("OID"))
							continue;
						if (item.getUIVisible()== false)
							continue;
						String[] fields = GetMulitFields(item.getKeyOfEn(),secondHeader);
						if(fields==null){
							firstStr.append("<th style='width:" + columWidth + "%;text-align:center;white-space: normal !important;' rowspan=2>" + item.getName() + "</th>");
						}else{
							if(userFields.indexOf(fields[0]+",") ==-1){
								firstStr.append("<th style='text-align:center;white-space: normal !important;' colspan="+(fields.length-1)+" rowspan=1>" + fields[0] + "</th>");
								userFields+=fields[0]+",";
							}
							secondStr.append("<th style='width:" + columWidth + "%;text-align:center;white-space: normal !important;' rowspan=1>" + item.getName() + "</th>");
						}
					}
					secondStr.append("</tr>");
					firstStr.append("</tr>");
					sb.append(firstStr.toString());
					sb.append(secondStr.toString());
				}else if(isHaveGradeHeader == true && isSecondHeader == true){
					StringBuilder firstStr = new StringBuilder("<tr style='page-break-before: always;page-break-after: always;page-break-inside: avoid;'>");
					StringBuilder secondStr = new StringBuilder("<tr style='page-break-before: always;page-break-after: always;page-break-inside: avoid;'>");
					StringBuilder threeStr = new StringBuilder("<tr style='page-break-before: always;page-break-after: always;page-break-inside: avoid;'>");
					String userSecondFields="";
					String userThreeFields="";
					for (MapAttr item : attrsOfDtls.ToJavaList())
					{
						if (item.getKeyOfEn().equals("OID"))
							continue;
						if (item.getUIVisible()== false)
							continue;
						String[] fields = GetMulitFields(item.getKeyOfEn(),gradeHeader);

						if(fields==null){
							String[] sfields = GetMulitFields(item.getKeyOfEn(),secondHeader);
							if(sfields==null){
								firstStr.append("<th style='width:" + columWidth + "%;text-align:center;white-space: normal !important;' rowspan=3>" + item.getName() + "</th>");
							}else{
								String[] tfields = GetMulitFields(sfields[0],gradeHeader);
								if(tfields == null){
									if(userSecondFields.indexOf(sfields[0]+",") ==-1){
										firstStr.append("<th style='text-align:center;white-space: normal !important;' colspan="+(sfields.length-1)+" rowspan=1>" + sfields[0] + "</th>");
										userSecondFields+=sfields[0]+",";
									}
									secondStr.append("<th style='width:" + columWidth + "%;text-align:center;white-space: normal !important;' rowspan=2>" + item.getName() + "</th>");

								}else{
									if(userThreeFields.indexOf(tfields[0]+",") ==-1){
										int colSpan = GetColSpan(tfields[0],tfields,secondHeader);
										firstStr.append("<th style='text-align:center;white-space: normal !important;' colspan="+colSpan+" rowspan=1>" + tfields[0] + "</th>");
										userThreeFields+=tfields[0]+",";
									}
									if(userSecondFields.indexOf(sfields[0]+",") ==-1){
										secondStr.append("<th style='text-align:center;white-space: normal !important;' colspan="+(sfields.length-1)+" rowspan=1>" + sfields[0] + "</th>");
										userSecondFields+=sfields[0]+",";
									}
									threeStr.append("<th style='width:" + columWidth + "%;text-align:center;white-space: normal !important;' >" + item.getName() + "</th>");

								}
							}
						}else{
							if(userThreeFields.indexOf(fields[0]+",") ==-1){
								int colSpan = GetColSpan(fields[0],fields,secondHeader);
								firstStr.append("<th style='text-align:center;white-space: normal !important;' colspan="+colSpan+" rowspan=1>" + fields[0] + "</th>");
								userThreeFields+=fields[0]+",";
							}
							secondStr.append("<th style='width:" + columWidth + "%;text-align:center;white-space: normal !important;' rowspan=2>" + item.getName() + "</th>");
						}
					}
					threeStr.append("</tr>");
					secondStr.append("</tr>");
					firstStr.append("</tr>");
					sb.append(firstStr.toString());
					sb.append(secondStr.toString());
					sb.append(threeStr.toString());
				}

				///#endregion 输出标题.


				///#region 输出数据.
				GEDtls dtls = new GEDtls(gf.getCtrlID());
				dtls.Retrieve(GEDtlAttr.RefPK, workid, "OID");
				for (GEDtl dtl : dtls.ToJavaList())
				{
					sb.append("<tr>");

					for (MapAttr item : attrsOfDtls.ToJavaList())
					{

						if (item.getKeyOfEn().equals("OID") || item.getUIVisible() == false)
						{
							continue;
						}
						String text = "";
						switch (item.getLGType())
						{
							case Normal: // 输出普通类型字段.
								if (item.getMyDataType() == 1 && item.getUIContralType() == UIContralType.DDL)
								{

									if (attrsOfDtls.contains("KeyOfEn",item.getKeyOfEn() + "Text") == true)
									{
										text = dtl.GetValRefTextByKey(item.getKeyOfEn());
									}
									if (DataType.IsNullOrEmpty(text))
									{
										if (attrsOfDtls.contains("KeyOfEn",item.getKeyOfEn() + "T") == true)
										{
											text = dtl.GetValStrByKey(item.getKeyOfEn() + "T");
										}
									}
								}
								else
								{

									text = dtl.GetValStrByKey(item.getKeyOfEn()+"T");
									if(DataType.IsNullOrEmpty(text))
										text = dtl.GetValStrByKey(item.getKeyOfEn());

									if (item.getTextModel() == 3)
									{
										text = text.replace("white-space: nowrap;", "");
									}
								}

								break;
							case Enum:
								if (item.getUIContralType() == UIContralType.CheckBok)
								{
									String s = en.GetValStrByKey(item.getKeyOfEn()) + ",";
									SysEnums enums = new SysEnums(item.getUIBindKey());
									for (SysEnum se : enums.ToJavaList())
									{
										if (s.indexOf(se.getIntKey() + ",") != -1)
										{
											text += se.getLab() + " ";
										}
									}

								}
								else
								{
									text = dtl.GetValRefTextByKey(item.getKeyOfEn());
								}
								break;
							case FK:
								text = dtl.GetValRefTextByKey(item.getKeyOfEn());
								break;
							default:
								break;
						}

						if (item.getUIContralType() == UIContralType.DDL)
						{
							sb.append("<td>" + text + "</td>");
							continue;
						}

						if (item.getMyDataType() == DataType.AppBoolean)
						{
							if (DataType.IsNullOrEmpty(text) || text.equals("0"))
								sb.append("<td>[&#10005]</td>");
							else
								sb.append("<td>[&#10004]</td>");
							continue;
						}

						if (item.getItIsNum())
						{
							sb.append("<td style='text-align:right' >" + text + "</td>");
							continue;
						}

						sb.append("<td>" + text + "</td>");
					}
					sb.append("</tr>");
				}
				///#endregion 输出数据.


				sb.append("</table>");

				sb.append(" </td>");
				sb.append(" </tr>");
			}
			///#endregion 如果是从表.

			///#region 如果是附件.
			if (gf.getCtrlType().equals("Ath"))
			{
				if (DataType.IsNullOrEmpty(gf.getCtrlID()) == true)
				{
					continue;
				}
				FrmAttachment ath = new FrmAttachment();
				ath.setMyPK(gf.getCtrlID());
				if (ath.RetrieveFromDBSources() == 0)
				{
					continue;
				}
				if (ath.getItIsVisable() == false)
				{
					continue;
				}

				sb.append(" <tr>");
				sb.append("  <th colspan=4><b>" + gf.getLab() + "</b></th>");
				sb.append(" </tr>");

				FrmAttachmentDBs athDBs = bp.wf.CCFormAPI.GenerFrmAttachmentDBs(ath, workid, ath.getMyPK(), DataType.IsNumStr(workid)?Long.parseLong(workid):0);

				if (ath.getUploadType() == AttachmentUploadType.Multi || ath.getUploadType() == AttachmentUploadType.Single )
				{
					sb.append("<tr><td valign=top colspan=4 >");
					sb.append("<ul>");

					//判断是否有这个目录.
					if (new File(path + "/pdf/").exists() == false)
					{
						new File(path + "/pdf/").mkdir();
					}

					for (FrmAttachmentDB item : athDBs.ToJavaList())
					{
						String fileTo = path + "/pdf/" + item.getFileName();
						//加密信息
						boolean fileEncrypt = SystemConfig.isEnableAthEncrypt();
						boolean isEncrypt = item.GetParaBoolen("IsEncrypt");
						///#region 从ftp服务器上下载.
						if (ath.getAthSaveWay() == AthSaveWay.FTPServer)
						{
							try
							{
								if (new File(fileTo).exists() == true)
									new File(fileTo).delete();

								//把文件copy到,
								String file = item.GenerTempFile(ath.getAthSaveWay());

								String fileTempDecryPath = file;
								if (fileEncrypt == true && isEncrypt == true)
								{
									fileTempDecryPath = file + ".tmp";
									AesEncodeUtil.decryptFile(file, fileTempDecryPath);

								}
								Files.copy(Paths.get(fileTempDecryPath), Paths.get(fileTo), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

								sb.append("<li><a href='" + SystemConfig.GetValByKey("HostURL", "") + "/DataUser/InstancePacketOfData/" + FK_Node + "/" + workid + "/" + "pdf/" + item.getFileName() + "'>" + item.getFileName() + "</a></li>");
							}
							catch (RuntimeException ex)
							{
								sb.append("<li>" + item.getFileName() + "(<font color=red>文件未从ftp下载成功{" + ex.getMessage() + "}</font>)</li>");
							}
						}
						///#endregion 从ftp服务器上下载.


						///#region 从iis服务器上下载.
						if (ath.getAthSaveWay() == AthSaveWay.IISServer)
						{
							try {

								String fileTempDecryPath = item.getFileFullName();
								if (fileEncrypt == true && isEncrypt == true) {
									fileTempDecryPath = item.getFileFullName() + ".tmp";
									AesEncodeUtil.decryptFile(item.getFileFullName(), fileTempDecryPath);

								}

								//把文件copy到,
								Files.copy(Paths.get(fileTempDecryPath), Paths.get(fileTo), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

								sb.append("<li><a href='" + SystemConfig.GetValByKey("HostURL", "") + "/DataUser/InstancePacketOfData/" + frmID + "/" + workid + "/" + "pdf/" + item.getFileName() + "'>" + item.getFileName() + "</a></li>");
							}
							catch (RuntimeException ex)
							{
								sb.append("<li>" + item.getFileName() + "(<font color=red>文件未从web下载成功{" + ex.getMessage() + "}</font>)</li>");
							}
						}

					}
					sb.append("</ul>");
					sb.append("</td></tr>");
				}

			}
			///#endregion 如果是附件.

			//如果是IFrame页面
			if (gf.getCtrlType().equals("Frame") && flowNo != null)
			{
				if (DataType.IsNullOrEmpty(gf.getCtrlID()) == true)
				{
					continue;
				}
				sb.append("<tr>");
				sb.append("  <td colspan='4' >");

				//根据GroupID获取对应的
				MapFrame frame = new MapFrame(gf.getCtrlID());
				//获取URL
				String url = frame.getURL();

				//替换URL的
				url = url.replace("@basePath", SystemConfig.getHostURLOfBS());
				//替换系统参数
				url = url.replace("@WebUser.No", WebUser.getNo());
				url = url.replace("@WebUser.Name;", WebUser.getName());
				url = url.replace("@WebUser.FK_DeptName;", WebUser.getDeptName());
				url = url.replace("@WebUser.FK_Dept;", WebUser.getDeptNo());
				url = url.replace("@WebUser.DeptNo;", WebUser.getDeptNo());
				//替换参数
				if (url.indexOf("?") > 0)
				{
					//获取url中的参数
					url = url.substring(url.indexOf('?'));
					String[] paramss = url.split("[&]", -1);
					for (String param : paramss)
					{
						if (DataType.IsNullOrEmpty(param) || param.indexOf("@") == -1)
						{
							continue;
						}
						String[] paramArr = param.split("[=]", -1);
						if (paramArr.length == 2 && paramArr[1].indexOf('@') == 0)
						{
							if (paramArr[1].indexOf("@WebUser.") == 0)
							{
								continue;
							}
							url = url.replace(paramArr[1], en.GetValStrByKey(paramArr[1].substring(1)));
						}
					}

				}
				sb.append("<iframe style='width:100%;height:auto;' ID='" + frame.getMyPK() + "'    src='" + url + "' frameborder=0  leftMargin='0'  topMargin='0' scrolling=auto></iframe></div>");
				sb.append("</td>");
				sb.append("</tr>");
			}
			if(gf.getCtrlType().equals("FWC") && flowNo!=null){
				isHaveFWC = true;
				if(dt.Rows.size()>0){
					sb.append(" <tr>");
					sb.append("  <th colspan=4><b>" + gf.getLab() + "</b></th>");
					sb.append(" </tr>");
				}
				NodeWorkCheck fwc = new NodeWorkCheck(frmID);


				String html = ""; // "<table style='width:100%;valign:middle;height:auto;' >";

				///#region 生成审核信息.

				//获得当前待办的人员,把当前审批的人员排除在外,不然就有默认同意的意见可以打印出来.
				sql = "SELECT FK_Emp, FK_Node FROM WF_GenerWorkerList WHERE IsPass!=1 AND WorkID=" + workid;
				DataTable dtOfTodo = DBAccess.RunSQLReturnTable(sql);

				for (DataRow dr : dt.Rows)
				{
					///#region 排除正在审批的人员.
					String nodeID = dr.getValue("NDFrom").toString();
					String empFrom = dr.getValue("EmpFrom").toString();
					if (dtOfTodo.Rows.size() != 0)
					{
						boolean isHave = false;
						for (DataRow mydr : dtOfTodo.Rows)
						{
							if (!mydr.getValue("FK_Node").toString().equals(nodeID))
							{
								continue;
							}

							if (!mydr.getValue("FK_Emp").toString().equals(empFrom))
							{
								continue;
							}
							isHave = true;
						}

						if (isHave == true)
						{
							continue;
						}
					}
					///#endregion 排除正在审批的人员.
					if(dr.getValue("NDFromT").equals("结束"))
						continue;

					html += "<tr>";
					html += " <td valign=middle class='FContext'>" + dr.getValue("NDFromT") + "</td>";

					String msg = dr.getValue("Msg").toString().replace("@.","");
					msg = dr.getValue("Msg").toString().replace(".","");

					msg += "<br>";
					msg += "<br>";

					String empStrs = "";
					int singType = fwc.getSigantureEnabel();
					if(singType == 0){
						empStrs = dr.getValue("EmpFromT").toString();
					}
					if (singType == 1)
					{
						String src = SystemConfig.getHostURL() + "/DataUser/Siganture/"+dr.getValue("EmpFrom")+ ".jpg" ;
						empStrs = "<img src='" + src + "' title='" + dr.getValue("EmpFromT")+ "' style='height:60px;'  alt='图片丢失' /> ";
					}

					if(dr.getValue("NDFromT").equals("抄送"))
						msg += "抄送人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();

					else if(dr.getValue("NDFromT").equals("提交申请"))
						msg += "发起人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();

					else if(dr.getValue("NDFromT").equals("评论"))
						msg += "评论人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();

					else if(dr.getValue("NDFromT").equals("流程结束"))
						msg += "处理人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();

					else if(dr.getValue("NDFromT").equals("拒绝"))
						msg += "处理人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();

					else if(dr.getValue("NDFromT").equals("终止流程实例"))
						msg += "处理人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();
					else
						msg += "审核人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();


					html += " <td colspan=3 valign=middle style='font-size:18px'>" + msg + "</td>";
					html += " </tr>";
				}
				///#endregion 生成审核信息.

				sb.append(" " + html);
			}
		}
		///#region 审核组件
		if (flowNo != null && isHaveFWC== false)
		{
			if(dt.Rows.size()>0){
				sb.append(" <tr>");
				sb.append("  <th colspan=4><b>审核记录</b></th>");
				sb.append(" </tr>");
			}
			NodeWorkCheck fwc = new NodeWorkCheck(frmID);


			String html = ""; // "<table style='width:100%;valign:middle;height:auto;' >";

			///#region 生成审核信息.

			//获得当前待办的人员,把当前审批的人员排除在外,不然就有默认同意的意见可以打印出来.
			sql = "SELECT FK_Emp, FK_Node FROM WF_GenerWorkerList WHERE IsPass!=1 AND WorkID=" + workid;
			DataTable dtOfTodo = DBAccess.RunSQLReturnTable(sql);

			for (DataRow dr : dt.Rows)
			{
				///#region 排除正在审批的人员.
				String nodeID = dr.getValue("NDFrom").toString();
				String empFrom = dr.getValue("EmpFrom").toString();
				if (dtOfTodo.Rows.size() != 0)
				{
					boolean isHave = false;
					for (DataRow mydr : dtOfTodo.Rows)
					{
						if (!mydr.getValue("FK_Node").toString().equals(nodeID))
						{
							continue;
						}

						if (!mydr.getValue("FK_Emp").toString().equals(empFrom))
						{
							continue;
						}
						isHave = true;
					}

					if (isHave == true)
					{
						continue;
					}
				}
				///#endregion 排除正在审批的人员.
				if(dr.getValue("NDFromT").equals("结束"))
					continue;

				html += "<tr>";
				html += " <td valign=middle class='FContext'>" + dr.getValue("NDFromT") + "</td>";

				String msg = dr.getValue("Msg").toString().replace("@.","");
				msg = dr.getValue("Msg").toString().replace(".","");

				msg += "<br>";
				msg += "<br>";

				String empStrs = "";
				int singType = fwc.getSigantureEnabel();
				if(singType == 0){
					empStrs = dr.getValue("EmpFromT").toString();
				}
				if (singType == 1)
				{
					String src = SystemConfig.getHostURL() + "/DataUser/Siganture/"+dr.getValue("EmpFrom")+ ".jpg" ;
						empStrs = "<img src='" + src + "' title='" + dr.getValue("EmpFromT")+ "' style='height:60px;'  alt='图片丢失' /> ";
				}

				if(dr.getValue("NDFromT").equals("抄送"))
					msg += "抄送人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();

				else if(dr.getValue("NDFromT").equals("提交申请"))
					msg += "发起人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();

				else if(dr.getValue("NDFromT").equals("评论"))
					msg += "评论人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();

				else if(dr.getValue("NDFromT").equals("流程结束"))
					msg += "处理人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();

				else if(dr.getValue("NDFromT").equals("拒绝"))
					msg += "处理人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();

				else if(dr.getValue("NDFromT").equals("终止流程实例"))
					msg += "处理人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();
				else
					msg += "审核人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();


				html += " <td colspan=3 valign=middle style='font-size:18px'>" + msg + "</td>";
				html += " </tr>";
			}
			///#endregion 生成审核信息.

			sb.append(" " + html);
		}
		sb.append("</table>");
		return sb;
	}

	private static String[] GetMulitFields(String keyOfEn, String multi){
		String[] fields = multi.split(";");
		for (int i = 0; i < fields.length; i++) {
			String str = fields[i];
			if (DataType.IsNullOrEmpty(str) == true)
				continue;
			if (str.indexOf(keyOfEn + ",") == -1)
				continue;
			return str.substring(0, str.length() - 1).split(",");
		}
		return null;
	}

	/**
	 * 获取表头占的列
	 * @param keyOfEn
	 * @param fields
	 * @param secondHeader
	 * @return
	 */

	private static int GetColSpan(String keyOfEn,String[] fields,String secondHeader){
		int count = 0;
		for (int i = 0; i < fields.length; i++) {
			String str = fields[i];
			if (DataType.IsNullOrEmpty(str) == true)
				continue;
			if (str.equals(keyOfEn) ==true)
				continue;
			if(secondHeader.indexOf(str+",")==-1){
				count++;
				continue;
			}
			String[] strs = GetMulitFields(str,secondHeader);
			if(strs!=null)
				count+=strs.length-1;
		}
		return count;
	}
	/**
	 这里做了一些不为空的判断，获得document的元素的时候.
	 注意同步.

	 @param mapData
	 @param frmID
	 @param workid
	 @param en
	 @param path
	 @param indexFile
	 @param flowNo
	 @param FK_Node
	 */
	private static void GenerHtmlOfDevelop(MapData mapData, String frmID, String workid, Entity en, String path, String indexFile, String flowNo, String FK_Node) throws Exception
	{
		String htmlString = DataType.ReadTextFile(indexFile);
		// 解析字符串
		Document doc = Jsoup.parse(htmlString);

		//字段集合.
		MapAttrs mapAttrs = new MapAttrs(frmID);

		//获取审核组件的信息
		String sql = "SELECT NDFromT,Msg,RDT,EmpFromT,EmpFrom,NDFrom FROM ND" + Integer.parseInt(flowNo) + "Track WHERE WorkID=" + Long.parseLong(workid) + " AND ActionType=" + ActionType.WorkCheck.getValue() + " ORDER BY RDT ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		Element el = null;
		for (MapAttr attr : mapAttrs.ToJavaList())
		{
			//处理隐藏字段，如果是不可见并且是启用的就隐藏.
			if (attr.getUIVisible() == false)
			{
				continue;
			}
			String text = en.GetValStrByKey(attr.getKeyOfEn());
			//外键或者外部数据源
			if ((attr.getLGType() == FieldTypeS.Normal && attr.getMyDataType() == DataType.AppString && attr.getUIContralType() == UIContralType.DDL) || (attr.getLGType() == FieldTypeS.FK && attr.getMyDataType() == DataType.AppString))
			{
				if (mapAttrs.contains(attr.getKeyOfEn() + "Text") == true)
				{
					text = en.GetValRefTextByKey(attr.getKeyOfEn());
				}
				if (DataType.IsNullOrEmpty(text))
				{
					if (mapAttrs.contains(attr.getKeyOfEn() + "T") == true)
					{
						text = en.GetValStrByKey(attr.getKeyOfEn() + "T");
					}
				}
				el = doc.getElementById("DDL_" + attr.getKeyOfEn());
				if (el == null)
				{
					continue;
				}


				Element element = new Element("span");
				element.text(text);
				el.after(element);
				el.remove();
				continue;
			}
			//枚举、枚举下拉框
			if (attr.getMyDataType() == DataType.AppInt && attr.getLGType() == FieldTypeS.Enum)
			{
				text = en.GetValStrByKey(attr.getKeyOfEn());
				//如果是下拉框
				if (attr.getUIContralType() == UIContralType.DDL)
				{
					text = en.GetValStringByKey(attr.getKeyOfEn() + "Text");
					el = doc.getElementById("DDL_" + attr.getKeyOfEn());
					if (el == null)
					{
						continue;
					}
					Element element = new Element("span");
					element.text(text);
					el.after(element);
					el.remove();
					continue;
				}
				Element rb = doc.getElementById("RB_" + attr.getKeyOfEn() + "_" + text);
				if (rb != null)
				{
					rb.attr("checked", "checked");
				}

				continue;
			}
			//枚举复选框
			if (attr.getMyDataType() == DataType.AppString && attr.getLGType() == FieldTypeS.Enum)
			{
				text = en.GetValStrByKey(attr.getKeyOfEn());
				String s = en.GetValStrByKey(attr.getKeyOfEn()) + ",";
				SysEnums enums = new SysEnums(attr.getUIBindKey());
				for (SysEnum se : enums.ToJavaList())
				{
					Element mycb = doc.getElementById("CB_" + attr.getKeyOfEn() + "_" + se.getIntKey());
					if (mycb == null)
					{
						continue;
					}

					if (s.indexOf(se.getIntKey() + ",") != -1)
					{
						mycb.attr("checked", "checked");
					}

					mycb.attr("disabled", "disabled");
				}

				continue;
			}

			if (attr.getMyDataType() == DataType.AppBoolean)
			{
				Element cb = doc.getElementById("CB_" + attr.getKeyOfEn());
				if (cb != null)
				{
					if (DataType.IsNullOrEmpty(text) || text.equals("0"))
					{
						doc.getElementById("CB_" + attr.getKeyOfEn()).attr("checked", "");
					}
					else
					{
						doc.getElementById("CB_" + attr.getKeyOfEn()).attr("checked", "checked");
					}

					doc.getElementById("CB_" + attr.getKeyOfEn()).attr("disabled", "disabled");
				}
				continue;

			}
			if (attr.getMyDataType() == DataType.AppString)
			{
				//签批字段
				if (attr.getUIContralType() == UIContralType.SignCheck)
				{
					el = doc.getElementById("TB_" + attr.getKeyOfEn());
					if (el == null)
					{
						continue;
					}

					DataTable mydt = GetWorkcheckInfoByNodeIDs(dt, text);
					if (mydt.Rows.size() == 0)
					{
						el.remove();
						continue;
					}
					String _html = "<div style='min-height:17px;'>";
					_html += "<table style='width:100%'><tbody>";
					for (DataRow dr : mydt.Rows)
					{
						_html += "<tr><td style='border: 1px solid #D6DDE6;'>";
						_html += "<div style='word-wrap: break-word;line-height:20px;padding:5px;padding-left:50px;'><font color='#999'>" + dr.getValue(1).toString() + "</font></div>";
						_html += "<div style='text-align:right;padding-right:5px'>" + dr.getValue(3).toString() + "(" + dr.getValue(2).toString() + ")</div>";
						_html += "</td></tr>";
					}
					_html += "</tbody></table></div>";

					el.after(_html);
					el.remove();
					continue;
				}
				//字段附件
				if (attr.getUIContralType() == UIContralType.AthShow)
				{
					continue;
				}
				//签名
				if (attr.getItIsSigan() == true)
				{
					continue;
				}

			}

			if (attr.getTextModel()==3 ||  attr.getTextModel()==2)
			{
				//这几种字体生成 pdf都乱码
				text = text.replace("仿宋,", "宋体,");
				text = text.replace("仿宋;", "宋体;");
				text = text.replace("仿宋\"", "宋体\"");
				text = text.replace("黑体,", "宋体,");
				text = text.replace("黑体;", "宋体;");
				text = text.replace("黑体\"", "宋体\"");
				text = text.replace("楷体,", "宋体,");
				text = text.replace("楷体;", "宋体;");
				text = text.replace("楷体\"", "宋体\"");
				text = text.replace("隶书,", "宋体,");
				text = text.replace("隶书;", "宋体;");
				text = text.replace("隶书\"", "宋体\"");

				Element tb1 = doc.getElementById("TB_" + attr.getKeyOfEn());
				if (tb1 != null)
				{
					if(attr.getTextModel()==2){
						doc.getElementById("TB_" + attr.getKeyOfEn()).text(text);
						doc.getElementById("TB_" + attr.getKeyOfEn()).attr("disabled", "disabled");
					}
					if(attr.getTextModel()==3){
						//获取text中的imge文件
						ArrayList<String> imgs = getImgStr(text);
						String toFile = path + "/pdf/";
						String sourdeFile="";
						for(String str :imgs){
							if(DataType.IsNullOrEmpty(str)==true || str.contains("/")==false)
								continue;
							//复制文件到指定位置
							toFile +=str.substring(str.lastIndexOf("/")+1);
							sourdeFile = SystemConfig. getPathOfWebApp()+str.substring(str.indexOf("DataUser"));
							Files.copy(Paths.get(sourdeFile), Paths.get(toFile), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
							text = text.replace(str,toFile);
						}
						tb1.after(text);
						tb1.remove();
					}
				}
				continue;
			}

			//如果是字符串
			Element tb = doc.getElementById("TB_" + attr.getKeyOfEn());
			if (tb != null)
			{
				Element element = new Element("span");
				element.text(text);
				tb.after(element);
				tb.remove();
				//tb.attr("value", text);
				//tb.attr("disabled", "disabled");
			}
		}
		//获取从表
		MapDtls dtls = new MapDtls(frmID);
		for (MapDtl dtl : dtls.ToJavaList())
		{
			if (dtl.GetValBooleanByKey("IsView") == false)
			{
				continue;
			}
			String html = GetDtlHtmlByID(dtl, workid, mapData.getFrmW());
			Elements els = doc.select("img[data-key='" + dtl.getNo() + "']");
			if (els == null || els.size()==0)
			{
				continue;
			}
			el = els.get(0);

			el.after(html);
			el.remove();

		}
		//获取附件
		FrmAttachments aths = new FrmAttachments(frmID);
		for (FrmAttachment ath : aths.ToJavaList())
		{
			if (ath.getItIsVisable() == false)
			{
				continue;
			}
			Elements els = doc.select("img[data-key='" + ath.getMyPK()+ "']");
			if (els == null || els.size()==0)
			{
				continue;
			}

			String html = GetAthHtmlByID(ath, workid, path);
			el = els.get(0);
			el.after(html);
			el.remove();
		}
		;
		DataType.WriteFile(indexFile,doc.html());

		return;
	}
	public static ArrayList<String> getImgStr(String htmlStr) {
		ArrayList<String> pics = new ArrayList<>();
		String img = "";
		Pattern p_image;
		java.util.regex.Matcher m_image;
		String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
		p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
		m_image = p_image.matcher(htmlStr);
		while (m_image.find()) {
			// 得到<img />数据
			img = m_image.group();
			// 匹配<img>中的src数据
			java.util.regex.Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
			while (m.find()) {
				pics.add(m.group(1));
			}
		}

		return pics;
	}
	private static DataTable GetWorkcheckInfoByNodeIDs(DataTable dt, String nodeId)
	{
		DataTable mydt = dt.clone();
		if (DataType.IsNullOrEmpty(nodeId) == true)
		{
			return mydt;
		}
		String[] nodeIds = nodeId.split("[,]", -1);
		for (int i = 0; i < nodeIds.length; i++)
		{
			if (DataType.IsNullOrEmpty(nodeIds[i]) == true)
			{
				continue;
			}
			//获取到值
			DataRow[] rows = dt.Select("NDFrom=" + nodeIds[i]);
			for (DataRow dr : rows)
			{
				DataRow myrow = mydt.NewRow();
				for(DataColumn col :dt.Columns)
					myrow.setValue(col.ColumnName,dr.getValue(col.ColumnName));
				mydt.Rows.add(myrow);

			}
		}
		return mydt;
	}

	private static String GetDtlHtmlByID(MapDtl dtl, String workid, float width)throws Exception
	{
		StringBuilder sb = new StringBuilder();
		MapAttrs attrsOfDtls = new MapAttrs(dtl.getNo());
		int columNum = 0;
		for (MapAttr item : attrsOfDtls.ToJavaList())
		{
			if (item.getKeyOfEn().equals("OID"))
			{
				continue;
			}
			if (item.getUIVisible() == false)
			{
				continue;
			}
			columNum++;
		}
		if (columNum == 0)
		{
			return "";
		}
		int columWidth = (int)100 / columNum;

		sb.append("<table style='width:100%' >");
		sb.append("<tr>");

		for (MapAttr item : attrsOfDtls.ToJavaList())
		{
			if (item.getKeyOfEn().equals("OID"))
			{
				continue;
			}
			if (item.getUIVisible() == false)
			{
				continue;
			}
			sb.append("<th class='DtlTh' style='width:" + columWidth + "%'>" + item.getName() + "</th>");
		}
		sb.append("</tr>");
		///#endregion 输出标题.


		///#region 输出数据.
		GEDtls gedtls = new GEDtls(dtl.getNo());
		gedtls.Retrieve(GEDtlAttr.RefPK, workid, "OID");
		for (GEDtl gedtl : gedtls.ToJavaList())
		{
			sb.append("<tr>");

			for (MapAttr attr : attrsOfDtls.ToJavaList())
			{
				//处理隐藏字段，如果是不可见并且是启用的就隐藏.
				if (attr.getKeyOfEn().equals("OID") || attr.getUIVisible() == false)
					continue;

				String text = "";

				switch (attr.getLGType())
				{
					case Normal: // 输出普通类型字段.
						if (attr.getMyDataType() == 1 && attr.getUIContralType() ==UIContralType.DDL)
						{

							if (attrsOfDtls.contains(attr.getKeyOfEn() + "Text") == true)
							{
								text = gedtl.GetValRefTextByKey(attr.getKeyOfEn());
							}
							if (DataType.IsNullOrEmpty(text))
							{
								if (attrsOfDtls.contains(attr.getKeyOfEn() + "T") == true)
								{
									text = gedtl.GetValStrByKey(attr.getKeyOfEn() + "T");
								}
							}
						}
						else
						{
							//判断是不是图片签名
							if (attr.getItIsSigan() == true)
							{
								String SigantureNO = gedtl.GetValStrByKey(attr.getKeyOfEn());
								String src = SystemConfig.getHostURL() + "/DataUser/Siganture/";
								text = "<img src='" + src + SigantureNO + ".jpg' title='" + SigantureNO + "' onerror='this.src=\"" + src + "Siganture.jpg\"' style='height:50px;'  alt='图片丢失' /> ";
							}
							else
							{
								text = gedtl.GetValStrByKey(attr.getKeyOfEn());
							}
							if (attr.getTextModel() == 3)
							{
								text = text.replace("white-space: nowrap;", "");
							}
						}

						break;
					case Enum:
						if (attr.getUIContralType() == UIContralType.CheckBok)
						{
							String s = gedtl.GetValStrByKey(attr.getKeyOfEn()) + ",";
							SysEnums enums = new SysEnums(attr.getUIBindKey());
							for (SysEnum se : enums.ToJavaList())
							{
								if (s.indexOf(se.getIntKey() + ",") != -1)
								{
									text += se.getLab() + " ";
								}
							}

						}
						else
						{
							text = gedtl.GetValRefTextByKey(attr.getKeyOfEn());
						}
						break;
					case FK:
						text = gedtl.GetValRefTextByKey(attr.getKeyOfEn());
						break;
					default:
						break;
				}

				if (attr.getItIsBigDoc())
				{
					//这几种字体生成 pdf都乱码
					text = text.replace("仿宋,", "宋体,");
					text = text.replace("仿宋;", "宋体;");
					text = text.replace("仿宋\"", "宋体\"");
					text = text.replace("黑体,", "宋体,");
					text = text.replace("黑体;", "宋体;");
					text = text.replace("黑体\"", "宋体\"");
					text = text.replace("楷体,", "宋体,");
					text = text.replace("楷体;", "宋体;");
					text = text.replace("楷体\"", "宋体\"");
					text = text.replace("隶书,", "宋体,");
					text = text.replace("隶书;", "宋体;");
					text = text.replace("隶书\"", "宋体\"");
				}

				if (attr.getMyDataType() == DataType.AppBoolean)
				{
					if (DataType.IsNullOrEmpty(text) || text.equals("0"))
					{
						text = "否";
					}
					else
					{
						text = "是";
					}
				}
				if (attr.getItIsNum())
				{
					sb.append("<td class='DtlTd' style='text-align:right;' >" + text + "</td>");
				}
				else
				{
					sb.append("<td class='DtlTd' >" + text + "</td>");
				}
			}

			sb.append("</tr>");
		}
		///#endregion 输出数据.


		sb.append("</table>");


		sb.append("</span>");
		return sb.toString();
	}

	private static String GetAthHtmlByID(FrmAttachment ath, String workid, String path) throws Exception {
		StringBuilder sb = new StringBuilder();

		if (ath.getUploadType() == AttachmentUploadType.Multi)
		{


			//判断是否有这个目录.
			if (new File(path + "/pdf/").exists() == false)
			{
				new File(path + "/pdf/").mkdir();
			}

			//文件加密
			boolean fileEncrypt = SystemConfig.isEnableAthEncrypt();
			FrmAttachmentDBs athDBs = bp.wf.CCFormAPI.GenerFrmAttachmentDBs(ath, workid, ath.getMyPK(), DataType.IsNumStr(workid)?Long.parseLong(workid):0);
			sb.append("<table id = 'ShowTable' class='table' style='width:100%'>");
			sb.append("<thead><tr style = 'border:0px;'>");
			sb.append("<th style='width:50px; border: 1px solid #ddd;padding:8px;background-color:white' nowrap='true'>序</th>");
			sb.append("<th style = 'min -width:200px; border: 1px solid #ddd;padding:8px;background-color:white' nowrap='true'>文件名</th>");
			sb.append("<th style = 'width:50px; border: 1px solid #ddd;padding:8px;background-color:white' nowrap='true'>大小KB</th>");
			sb.append("<th style = 'width:120px; border: 1px solid #ddd;padding:8px;background-color:white' nowrap='true'>上传时间</th>");
			sb.append("<th style = 'width:80px; border: 1px solid #ddd;padding:8px;background-color:white' nowrap='true'>上传人</th>");
			sb.append("</thead>");
			sb.append("<tbody>");
			int idx = 0;
			for (FrmAttachmentDB item : athDBs.ToJavaList())
			{
				idx++;
				sb.append("<tr>");
				sb.append("<td class='Idx'>" + idx + "</td>");
				//获取文件是否加密
				boolean isEncrypt = item.GetParaBoolen("IsEncrypt");
				if (ath.getAthSaveWay() == AthSaveWay.FTPServer)
				{
					try
					{
						String toFile = path + "/pdf/" + item.getFileName();
						if (new File(toFile).exists()== false)
						{
							//获取文件是否加密
							String file = item.GenerTempFile(ath.getAthSaveWay());
							String fileTempDecryPath = file;
							if (fileEncrypt == true && isEncrypt == true)
							{
								fileTempDecryPath = file + ".tmp";
								AesEncodeUtil.decryptFile(file, fileTempDecryPath);

							}
							Files.copy(Paths.get(fileTempDecryPath), Paths.get(toFile), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

						}

						sb.append("<td  title='" + item.getFileName() + "'>" + item.getFileName() + "</td>");
					}
					catch (Exception ex)
					{
						sb.append("<td>" + item.getFileName() + "(<font color=red>文件未从ftp下载成功{" + ex.getMessage() + "}</font>)</td>");
					}
				}

				if (ath.getAthSaveWay() == AthSaveWay.IISServer)
				{
					try
					{
						String toFile = path + "/pdf/" + item.getFileName();
						if (new File(toFile).exists() == false)
						{
							//把文件copy到,
							String fileTempDecryPath = item.getFileFullName();
							if (fileEncrypt == true && isEncrypt == true)
							{
								fileTempDecryPath = item.getFileFullName() + ".tmp";
								AesEncodeUtil.decryptFile(item.getFileFullName(), fileTempDecryPath);

							}

							//把文件copy到,
							Files.copy(Paths.get(fileTempDecryPath), Paths.get(path + "/pdf/" + item.getFileName()), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

						}
						sb.append("<td>" + item.getFileName() + "</td>");
					}
					catch (Exception ex)
					{
						sb.append("<td>" + item.getFileName() + "(<font color=red>文件未从ftp下载成功{" + ex.getMessage() + "}</font>)</td>");
					}
				}
				sb.append("<td>" + item.getFileSize() + "KB</td>");
				sb.append("<td>" + item.getRDT() + "</td>");
				sb.append("<td>" + item.getRecName() + "</td>");
				sb.append("</tr>");


			}
			sb.append("</tbody>");

			sb.append("</table>");
		}
		return sb.toString();
	}
	/**
	 单表单，多表单打印PDF

	 @param node 节点属性
	 @param workid 流程实例WorkID
	 @param flowNo 流程编号
	 @param pdfName 生成PDF的名称
	 @param filePath 生成PDF的路径
	 @return
	 @exception Exception
	 */
	public static String MakeCCFormToPDF(Node node, long workid, String flowNo, String pdfName, String filePath, String basePath) throws Exception {
		//根据节点信息获取表单方案

		MapData md = new MapData("ND" + node.getNodeID());
		String resultMsg = "";
		GenerWorkFlow gwf = null;
		// 处理路径
		if(StringUtils.isEmpty(basePath)) {
			basePath =  SystemConfig.GetValByKey("HostURL", "");
		}
		//获取主干流程信息
		if (flowNo != null)
		{
			gwf = new GenerWorkFlow(workid);
		}

		//存放信息地址
		String hostURL = SystemConfig.GetValByKey("HostURL", "");
		String path = SystemConfig.getPathOfInstancePacketOfData() + "ND" + node.getNodeID() + "/" + workid;
		String frmID = node.getNodeFrmID();

		//处理正确的文件名.
		if (DataType.IsNullOrEmpty(pdfName) == true)
		{
			if (DataType.IsNullOrEmpty(flowNo) == false)
			{
				pdfName = DBAccess.RunSQLReturnStringIsNull("SELECT Title FROM WF_GenerWorkFlow WHERE WorkID=" + workid, (new Long(workid)).toString());
			}
			else
			{
				pdfName = (new Long(workid)).toString();
			}
		}

		pdfName = DataType.PraseStringToFileName(pdfName);

		java.util.Hashtable ht = new java.util.Hashtable();
		///#region 单表单打印
		if (node.getHisFormType() == NodeFormType.FoolForm || node.getHisFormType() == NodeFormType.RefOneFrmTree || node.getHisFormType() == NodeFormType.FoolTruck || node.getHisFormType() == NodeFormType.Develop)
		{
			resultMsg = setPDFPath("ND" + node.getNodeID(), String.valueOf(workid), flowNo, gwf);
			if (resultMsg.indexOf("err@") != -1)
			{
				return resultMsg;
			}

			String billUrl = SystemConfig.getPathOfInstancePacketOfData() +"ND" + node.getNodeID() + "/" + workid + "/index.htm";

			resultMsg = MakeHtmlDocument(frmID, workid, flowNo, path, billUrl, "ND" + node.getNodeID());

			if (resultMsg.indexOf("err@") != -1)
			{
				return resultMsg;
			}

			ht.put("htm", SystemConfig.GetValByKey("HostURLOfBS", "../../DataUser") + "/InstancePacketOfData/" + "ND" + node.getNodeID() + "/" + workid + "/index.htm");
			//生成pdf文件
			String pdfPath = filePath;
			if (DataType.IsNullOrEmpty(pdfPath) == true)
				pdfPath = path + "/pdf";

			if ((new File(pdfPath)).isDirectory() == false) {
				(new File(pdfPath)).mkdirs();
			}

			String pdfFile = pdfPath + "/" + pdfName + ".pdf";
			String pdfFileExe ="";
			if(SystemConfig.isJarRun()==false){
				pdfFileExe = SystemConfig.getPathOfDataUser() + "ThirdpartySoftware/wkhtmltox/wkhtmltopdf.exe";
			}
			else{
				pdfFileExe = SystemConfig.getPhysicalPath() + "DataUser/ThirdpartySoftware/wkhtmltox/wkhtmltopdf.exe";
			}

			if(!new File(pdfFileExe).exists()){
				throw new RuntimeException("err@第三方插件wkhtmltopdf不存在");
			}

			try
			{
				Html2Pdf(pdfFileExe, billUrl, pdfFile);
				if (DataType.IsNullOrEmpty(filePath) == true)
					ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS", "../../DataUser/") + "InstancePacketOfData/" + "ND" + node.getNodeID() + "/" + workid + "/pdf/" + pdfName + ".pdf");
				else
					ht.put("pdf", pdfPath + "/" + DataType.PraseStringToUrlFileName(pdfName) + ".pdf");
			}
			catch (RuntimeException ex)
			{
				throw new RuntimeException("err@html转PDF错误:PDF的路径" + pdfPath + "可能抛的异常" + ex.getMessage());
			}

			//生成压缩文件
			String zipFile = path + "/" + pdfName + ".zip";

			File finfo = new File(zipFile);
			ZipFilePath = finfo.getPath(); //文件路径.
			File zipFileFile = new File(zipFile);
			try {
				while (zipFileFile.exists() == true) {
					zipFileFile.delete();
				}
				// 执行压缩.
				ZipCompress fz = new ZipCompress(zipFile, pdfPath);
				fz.zip();
				ht.put("zip", basePath + "/DataUser/InstancePacketOfData/" + "ND" + node.getNodeID() + "/" + workid + "/" +  pdfName + ".zip");
			} catch (Exception ex) {
				ht.put("zip","err@执行压缩出现错误:" + ex.getMessage() + ",路径tempPath:" + pdfPath + ",zipFile=" + finfo.getName());
			}

			if (zipFileFile.exists() == false)
				ht.put("zip","err@压缩文件未生成成功,请在点击一次.");
			//把所有的文件做成一个zip文件.

			return bp.tools.Json.ToJsonEntitiesNoNameMode(ht);
		}
		///#endregion 单表单打印
		///#region 多表单合并PDF打印
		if (node.getHisFormType() == NodeFormType.SheetTree)
		{
			String pdfPath = filePath;
			//生成pdf文件
			//生成pdf文件
			if (DataType.IsNullOrEmpty(pdfPath) == true)
			{
				pdfPath = path + "/pdf";
			}
			String pdfTempPath = path + "/pdfTemp";

			DataRow dr = null;
			resultMsg = setPDFPath("ND" + node.getNodeID(), String.valueOf(workid), flowNo, gwf);
			if (resultMsg.indexOf("err@") != -1)
			{
				return resultMsg;
			}

			//获取绑定的表单
			FrmNodes nds = new FrmNodes(node.getFlowNo(), node.getNodeID());
			for (FrmNode item : nds.ToJavaList())
			{
				//判断当前绑定的表单是否启用
				if (item.getFrmEnableRoleInt() == FrmEnableRole.Disable.getValue())
					continue;

				//判断 who is pk
				if (flowNo != null && item.getWhoIsPK() == WhoIsPK.PWorkID) //如果是父子流程
					workid = gwf.getPWorkID();
				//获取表单的信息执行打印
				String billUrl = SystemConfig.getPathOfInstancePacketOfData() +  "ND" + node.getNodeID() + "/" + workid + "/" + item.getFrmID() + "index.htm";
				resultMsg = MakeHtmlDocument(item.getFrmID(), workid, flowNo, path, billUrl, "ND" + node.getNodeID());

				if (resultMsg.indexOf("err@") != -1)
				{
					return resultMsg;
				}

				ht.put("htm_" + item.getFrmID(), SystemConfig.GetValByKey("HostURLOfBS", "../../DataUser/") + "/InstancePacketOfData/" + "ND" + node.getNodeID() + "/" + workid + "/" + item.getFrmID() + "index.htm");

				///#region 把所有的文件做成一个zip文件.
				if (new File(pdfTempPath).exists() == false)
				{
					new File(pdfTempPath).mkdir();
				}

				String pdfFormFile = pdfTempPath + "/" + item.getFrmID() + ".pdf";
				String pdfFileExe = SystemConfig.getPathOfDataUser() + "ThirdpartySoftware/wkhtmltox/wkhtmltopdf.exe";
				try
				{
					Html2Pdf(pdfFileExe, resultMsg, pdfFormFile);

				}
				catch (RuntimeException ex)
				{
					//有可能是因为文件路径的错误， 用补偿的方法在执行一次, 如果仍然失败，按照异常处理.
					Html2Pdf(pdfFileExe, resultMsg, pdfFormFile);
				}

			}

			//pdf合并
			String pdfFile = pdfPath + "/" + pdfName + ".pdf";
			//开始合并处理
			if (new File(pdfPath).exists() == false)
				new File(pdfPath).mkdirs();

			MergePDF(pdfTempPath, pdfFile); //合并pdf
			//合并完删除文件夹
			BaseFileUtils.deleteDirectory(pdfTempPath);
			if (DataType.IsNullOrEmpty(filePath) == true)
			{
				ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS", "../../DataUser/") + "InstancePacketOfData/" + frmID + "/" + workid + "/pdf/" + pdfName + ".pdf");
			}
			else
			{
				ht.put("pdf", pdfPath + "/" + pdfName + ".pdf");
			}
			//生成压缩文件
			String zipFile = pdfPath + "/../" + pdfName + ".zip";

			File finfo = new File(zipFile);
			ZipFilePath =finfo.getName();

			File zipFileFile = new File(zipFile);
			try {
				while (zipFileFile.exists() == true) {
					zipFileFile.delete();
				}
				// 执行压缩.
				ZipCompress fz = new ZipCompress(zipFile, pdfPath);
				fz.zip();
				ht.put("zip", basePath + "/DataUser/InstancePacketOfData/" + frmID + "/" + workid + "/" + pdfName + ".zip");
			} catch (Exception ex) {
				ht.put("zip","err@执行压缩出现错误:" + ex.getMessage() + ",路径tempPath:" + pdfPath + ",zipFile=" + finfo.getName());
			}

			if (zipFileFile.exists() == false)
				ht.put("zip","err@压缩文件未生成成功,请在点击一次.");

			return bp.tools.Json.ToJsonEntitiesNoNameMode(ht);
		}
		///#endregion 多表单合并PDF打印
		return "warning@不存在需要打印的表单";

	}

	public static boolean deleteContents(File dir) {
		File[] files = dir.listFiles();
		boolean success = true;
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					success &= deleteContents(file);
				}
				if (!file.delete()) {
					System.out.println("Failed to delete " + file);
					success = false;
				}
			}
		}
		return success;
	}

	/**
	 批量打印下载pdf zip包
	 //多表单合并PDF打印 未处理
	 @param workIds 流程实例WorkID
	 @param flowNo 流程编号
	 @return
	 @exception Exception
	 */
	public static String MakeBatchPDFZip(String workIds, String flowNo) throws Exception {

		String[]  workflowIds = workIds.split(",");
		java.util.Hashtable ht = new java.util.Hashtable();

		String zipFilePath = SystemConfig.getPathOfInstancePacketOfData() +  flowNo;
		if ((new File(zipFilePath)).isDirectory() == false) {
			(new File(zipFilePath)).mkdirs();
		}
		deleteContents(new File(zipFilePath));

		for(int i=0;i<workflowIds.length;i++)
		{
			Long workid = Long.parseLong(workflowIds[i]);
			GenerWorkFlow gwf = new GenerWorkFlow( workid );
			int nodeID = gwf.getNodeID();
			Node node = new Node(nodeID);
			MapData md = new MapData("ND" + node.getNodeID());
			String resultMsg = "";
			//存放信息地址
			String path = SystemConfig.getPathOfInstancePacketOfData() + "ND" + node.getNodeID() + "/" + workid;
			String frmID = node.getNodeFrmID();
			//处理正确的文件名.
			String 	pdfName = DBAccess.RunSQLReturnStringIsNull("SELECT Title FROM WF_GenerWorkFlow WHERE WorkID=" + workid, (new Long(workid)).toString());
			pdfName = DataType.PraseStringToFileName(pdfName);
			pdfName=gwf.getWorkID()+"_"+gwf.getBillNo()+"_"+pdfName;

			resultMsg = setPDFPath("ND" + node.getNodeID(), String.valueOf(workid), flowNo, gwf);
			if (resultMsg.indexOf("err@") != -1)
			{
				return resultMsg;
			}

			String billUrl = SystemConfig.getPathOfInstancePacketOfData() +"ND" + node.getNodeID() + "/" + workid + "/index.htm";

			resultMsg = MakeHtmlDocument(frmID, workid, flowNo, path, billUrl, "ND" + node.getNodeID());

			if (resultMsg.indexOf("err@") != -1)
			{
				return resultMsg;
			}

			//ht.put("htm", SystemConfig.GetValByKey("HostURLOfBS", "../../DataUser") + "/InstancePacketOfData/" + "ND" + node.getNodeID() + "/" + workid + "/index.htm");
			//生成pdf文件
			String pdfPath = "";
			if (DataType.IsNullOrEmpty(pdfPath) == true)
				pdfPath = path + "/pdf";
			if ((new File(pdfPath)).isDirectory() == false) {
				(new File(pdfPath)).mkdirs();
			}

			String pdfFile = pdfPath + "/" + pdfName + ".pdf";
			String pdfFileExe ="";
			if(SystemConfig.isJarRun()==false)
				pdfFileExe = SystemConfig.getPathOfDataUser() + "ThirdpartySoftware/wkhtmltox/wkhtmltopdf.exe";
			else
				pdfFileExe = SystemConfig.getPhysicalPath() + "DataUser/ThirdpartySoftware/wkhtmltox/wkhtmltopdf.exe";
			try
			{
				Html2Pdf(pdfFileExe, billUrl, pdfFile);
			//	ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS", "../../DataUser/") + "InstancePacketOfData/" + "ND" + node.getNodeID() + "/" + workid + "/pdf/" + pdfName + ".pdf");
				Path sourcePath = Paths.get(pdfFile);
				Path targetPath = Paths.get(zipFilePath);
				Files.copy(sourcePath,targetPath.resolve(sourcePath.getFileName()),StandardCopyOption.REPLACE_EXISTING);

			}
			catch (RuntimeException ex)
			{
				throw new RuntimeException("err@html转PDF错误:PDF的路径" + pdfPath + "可能抛的异常" + ex.getMessage());
			}

		}


		//生成压缩文件
		String zipFile = SystemConfig.getPathOfInstancePacketOfData() + flowNo + ".zip";

		File finfo = new File(zipFile);
		ZipFilePath = finfo.getPath(); //文件路径.
		File zipFileFile = new File(zipFile);
		try {
			while (zipFileFile.exists() == true) {
				zipFileFile.delete();
			}
			// 执行压缩.
			ZipCompress fz = new ZipCompress(zipFile, zipFilePath);
			fz.zip();
			ht.put("zip", SystemConfig.GetValByKey("HostURL","") + "DataUser/InstancePacketOfData/"  +  flowNo + ".zip");
		} catch (Exception ex) {
			ht.put("zip","err@执行压缩出现错误:" + ex.getMessage() + ",路径tempPath:" + zipFilePath + ",zipFile=" + finfo.getName());
		}

		if (zipFileFile.exists() == false)
			ht.put("zip","err@压缩文件未生成成功,请在点击一次.");
		//把所有的文件做成一个zip文件.

		return bp.tools.Json.ToJsonEntitiesNoNameMode(ht);

		//return "warning@不存在需要打印的表单";

	}

	/**
	 单据打印

	 @param frmId 表单ID
	 @param workid 数据ID
	 @param filePath PDF路径
	 @param pdfName PDF名称
	 @return
	 */
	public static String MakeBillToPDF(String frmId, long workid, String filePath, String pdfName, String basePath) throws Exception {
		String resultMsg = "";
		// 处理路径
		if(StringUtils.isEmpty(basePath)) {
			basePath =  SystemConfig.GetValByKey("HostURL", "");
		}
		//  获取单据的属性信息
		bp.ccbill.FrmBill bill = new bp.ccbill.FrmBill(frmId);

		//存放信息地址
		String path = SystemConfig.getPathOfInstancePacketOfData() + bill.getNo() + "/" + workid;

		if (DataType.IsNullOrEmpty(pdfName) == true)
		{
			pdfName = (new Long(workid)).toString();
		}

		pdfName = DataType.PraseStringToFileName(pdfName);

		java.util.Hashtable ht = new java.util.Hashtable();
		String pdfPath = filePath;
		//生成pdf文件
		if (DataType.IsNullOrEmpty(pdfPath) == true)
		{
			pdfPath = path + "/pdf";
		}
		DataRow dr = null;
		resultMsg = setPDFPath(frmId, String.valueOf(workid), null, null);
		if (resultMsg.indexOf("err@") != -1)
		{
			return resultMsg;
		}

		//获取表单的信息执行打印
		String billUrl = SystemConfig.getPathOfInstancePacketOfData() + bill.getNo() + "/" + workid + "/" + "index.htm";
		resultMsg = MakeHtmlDocument(bill.getNo(), workid, null, path, billUrl, frmId);

		if (resultMsg.indexOf("err@") != -1)
		{
			return resultMsg;
		}

		ht.put("htm", SystemConfig.GetValByKey("HostURLOfBS", "../../DataUser/") + "InstancePacketOfData/" + frmId + "/" + workid + "/" + "index.htm");

		///#region 把所有的文件做成一个zip文件.
		if (new File(pdfPath).exists()== false)
		{
			new File(pdfPath).mkdir();
		}

		String pdfFormFile = pdfPath + "/" + pdfName + ".pdf"; //生成的路径.
		String pdfFileExe = SystemConfig.getPathOfDataUser() + "ThirdpartySoftware/wkhtmltox/wkhtmltopdf.exe";
		try
		{
			Html2Pdf(pdfFileExe, resultMsg, pdfFormFile);
			if (DataType.IsNullOrEmpty(filePath) == true)
			{
				ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS", "../../DataUser/") + "InstancePacketOfData/" + frmId + "/" + workid + "/pdf/" + pdfName + ".pdf");
			}
			else
			{
				ht.put("pdf", pdfPath + "/" + pdfName + ".pdf");
			}
		}
		catch (RuntimeException ex)
		{

			pdfFormFile = pdfPath + "/" + pdfName + ".pdf";

			Html2Pdf(pdfFileExe, resultMsg, pdfFormFile);
			ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS", "") + "/InstancePacketOfData/" + frmId + "/" + workid + "/pdf/" + bill.getName() + ".pdf");
		}

		//生成压缩文件
		String zipFile = path + "/../" + pdfName + ".zip";

		File finfo = new File(zipFile);
		ZipFilePath = finfo.getPath(); //文件路径.
		File zipFileFile = new File(zipFile);
		try
		{
			while (zipFileFile.exists() == true) {
				zipFileFile.delete();
			}
			// 执行压缩.
			ZipCompress fz = new ZipCompress(zipFile, pdfPath);
			fz.zip();

			ht.put("zip",  basePath + "/DataUser/InstancePacketOfData/" + frmId + "/" + pdfName + ".zip");
		}
		catch (RuntimeException ex)
		{
			ht.put("zip", "err@生成zip文件遇到权限问题:" + ex.getMessage() + " @Path:" + pdfPath);
		}
		return bp.tools.Json.ToJsonEntitiesNoNameMode(ht);
	}

	/**
	 EntityNoName打印

	 @param frmId 表单ID
	 @param pkVal 实体主键值
	 @param filePath PDF路径
	 @param pdfName PDF名称
	 @return
	 */
	public static String MakeEntityNoNameToPDF(String frmId, String pkVal, String filePath, String pdfName, String basePath) throws Exception {
		String resultMsg = "";
		// 处理路径
		if(StringUtils.isEmpty(basePath)) {
			basePath =  SystemConfig.GetValByKey("HostURL", "");
		}
		//  获取单据的属性信息
		bp.ccbill.FrmBill bill = new bp.ccbill.FrmBill(frmId);

		//存放信息地址
		String path = SystemConfig.getPathOfInstancePacketOfData() + bill.getNo() + "/" + pkVal;

		if (DataType.IsNullOrEmpty(pdfName) == true)
		{
			pdfName = pkVal;
		}

		pdfName = DataType.PraseStringToFileName(pdfName);

		java.util.Hashtable ht = new java.util.Hashtable();
		String pdfPath = filePath;
		//生成pdf文件
		if (DataType.IsNullOrEmpty(pdfPath) == true)
		{
			pdfPath = path + "/pdf";
		}
		DataRow dr = null;
		resultMsg = setPDFPath(frmId, pkVal, null, null);
		if (resultMsg.indexOf("err@") != -1)
		{
			return resultMsg;
		}

		//获取表单的信息执行打印
		String billUrl = SystemConfig.getPathOfInstancePacketOfData() + bill.getNo() + "/" + pkVal + "/" + "index.htm";
		resultMsg = MakeHtmlNoNameDocument(bill.getNo(), pkVal, path, billUrl);

		if (resultMsg.indexOf("err@") != -1)
		{
			return resultMsg;
		}

		ht.put("htm", SystemConfig.GetValByKey("HostURLOfBS", "../../DataUser/") + "InstancePacketOfData/" + frmId + "/" + pkVal + "/" + "index.htm");

		///#region 把所有的文件做成一个zip文件.
		if (new File(pdfPath).exists()== false)
		{
			new File(pdfPath).mkdir();
		}

		String pdfFormFile = pdfPath + "/" + pdfName + ".pdf"; //生成的路径.
		String pdfFileExe = SystemConfig.getPathOfDataUser() + "ThirdpartySoftware/wkhtmltox/wkhtmltopdf.exe";
		try
		{
			Html2Pdf(pdfFileExe, resultMsg, pdfFormFile);
			if (DataType.IsNullOrEmpty(filePath) == true)
			{
				ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS", "../../DataUser/") + "InstancePacketOfData/" + frmId + "/" + pkVal + "/pdf/" + pdfName + ".pdf");
			}
			else
			{
				ht.put("pdf", pdfPath + "/" + pdfName + ".pdf");
			}
		}
		catch (RuntimeException ex)
		{

			pdfFormFile = pdfPath + "/" + pdfName + ".pdf";

			Html2Pdf(pdfFileExe, resultMsg, pdfFormFile);
			ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS", "") + "/InstancePacketOfData/" + frmId + "/" + pkVal + "/pdf/" + bill.getName() + ".pdf");
		}

		//生成压缩文件
		String zipFile = path + "/../" + pdfName + ".zip";

		File finfo = new File(zipFile);
		ZipFilePath = finfo.getPath(); //文件路径.
		File zipFileFile = new File(zipFile);
		try
		{
			while (zipFileFile.exists() == true) {
				zipFileFile.delete();
			}
			// 执行压缩.
			ZipCompress fz = new ZipCompress(zipFile, pdfPath);
			fz.zip();

			ht.put("zip",  basePath + "/DataUser/InstancePacketOfData/" + frmId + "/" + pdfName + ".zip");
		}
		catch (RuntimeException ex)
		{
			ht.put("zip", "err@生成zip文件遇到权限问题:" + ex.getMessage() + " @Path:" + pdfPath);
		}
		return bp.tools.Json.ToJsonEntitiesNoNameMode(ht);
	}
	public static String MakeHtmlNoNameDocument(String frmID, String pkVal, String path, String indexFile) throws Exception {
		try
		{
			GEEntityNoName en = new GEEntityNoName(frmID, pkVal);

			MapData mapData = new MapData(frmID);

			if (mapData.getHisFrmType() == FrmType.Develop)
			{
				String ddocs = DataType.ReadTextFile(SystemConfig.getPathOfInstancePacketOfData()+"Template/indexDevelop.htm");
				String htmlString = DBAccess.GetBigTextFromDB("Sys_MapData", "No", mapData.getNo(), "HtmlTemplateFile");


				htmlString = htmlString.replace("../../DataUser", SystemConfig.getHostURLOfBS() + "/DataUser");
				htmlString = htmlString.replace("../DataUser", SystemConfig.getHostURLOfBS() + "/DataUser");
				ddocs = ddocs.replace("@Docs", htmlString);

				ddocs = ddocs.replace("@Title", mapData.getName());

				DataType.WriteFile(indexFile, ddocs);
				GenerHtmlOfDevelop(mapData, mapData.getNo(), pkVal, en, path, indexFile, null, null);
				return indexFile;
			}


			if (mapData.getHisFrmType() == FrmType.ChapterFrm)
			{
				String ddocs = DataType.ReadTextFile(SystemConfig.getPathOfInstancePacketOfData()+"Template/indexChapterFrm.htm");
				String htmlString = DBAccess.GetBigTextFromDB("Sys_MapData", "No",mapData.getNo(), "HtmlTemplateFile");


				htmlString = htmlString.replace("../../DataUser", SystemConfig.getHostURLOfBS() + "/DataUser");
				htmlString = htmlString.replace("../DataUser", SystemConfig.getHostURLOfBS() + "/DataUser");
				ddocs = ddocs.replace("@Docs", htmlString);

				ddocs = ddocs.replace("@Title", mapData.getName());

				DataType.WriteFile(indexFile, ddocs);
				GenerHtmlOfDevelop(mapData,mapData.getNo(), pkVal, en, path, indexFile, null, null);
				return indexFile;
			}

			String rdt = "";
			if (en.getEnMap().getAttrs().contains("RDT"))
			{
				rdt = en.GetValStringByKey("RDT");
				if (rdt.length() > 10)
				{
					rdt = rdt.substring(0, 10);
				}
			}
			//先判断节点中水印的设置
			//判断是否打印水印
			boolean isPrintShuiYin = SystemConfig.GetValByKeyBoolen("IsPrintBackgroundWord", false);
			if (isPrintShuiYin == true)
			{
				String words = Glo.getPrintBackgroundWord();

				words = words.replace("@RDT", rdt);

				if (words.contains("@") == true)
				{
					words = Glo.DealExp(words, en);
				}

				String templateFilePathMy = SystemConfig.getPathOfInstancePacketOfData()+"Template/";
				paintWaterMarkPhoto(templateFilePathMy + "ShuiYin.png", words,  path + "/ShuiYin.png");
			}
			//生成 表单的 html.
			StringBuilder sb = new StringBuilder();
			//首先判断是否有约定的文件.
			String docs = "";
			String tempFile = SystemConfig.getPathOfInstancePacketOfData()+"Template/" +mapData.getNo() + ".htm";
			if ((new File(tempFile)).isFile() == false)
			{
				if (mapData.getHisFrmType() == FrmType.FoolForm)
				{
					docs = DataType.ReadTextFile(SystemConfig.getPathOfInstancePacketOfData()+"Template/indexFool.htm");
					sb = GenerHtmlOfFool(mapData, frmID, pkVal, en, path, null, null,null);
					docs = docs.replace("@Width", mapData.getFrmW() + "px");
				}
			}
			docs = docs.replace("@Docs", sb.toString());
			docs = docs.replace("@PrintDT", DataType.getCurrentDateByFormart("yyyy年MM月dd日 HH时mm分ss秒"));
			DataType.WriteFile(indexFile, docs);

			return indexFile;
		}
		catch (RuntimeException ex)
		{
			return "err@报表生成错误:" + ex.getStackTrace();
		}
	}


	public static String MakeFormToPDF(String frmId, String frmName, Node node, long workid, String flowNo, String fileNameFormat, boolean urlIsHostUrl, String basePath) throws Exception {

		String resultMsg = "";
		GenerWorkFlow gwf = null;

		// 处理路径
		if(StringUtils.isEmpty(basePath)) {
			basePath =  SystemConfig.GetValByKey("HostURL", "");
		}
		//获取主干流程信息
		if (flowNo != null)
		{
			gwf = new GenerWorkFlow(workid);
		}

		//存放信息地址
		String hostURL = SystemConfig.GetValByKey("HostURL", "");
		String path = SystemConfig.getPathOfInstancePacketOfData() + "ND" + node.getNodeID() + "/" + workid;

		//处理正确的文件名.
		if (fileNameFormat == null)
		{
			if (flowNo != null)
			{
				fileNameFormat = DBAccess.RunSQLReturnStringIsNull("SELECT Title FROM WF_GenerWorkFlow WHERE WorkID=" + workid, "" + (new Long(workid)).toString());
			}
			else
			{
				fileNameFormat = (new Long(workid)).toString();
			}
		}

		if (DataType.IsNullOrEmpty(fileNameFormat) == true)
		{
			fileNameFormat = (new Long(workid)).toString();
		}

		fileNameFormat = DataType.PraseStringToFileName(fileNameFormat);

		java.util.Hashtable ht = new java.util.Hashtable();

		//生成pdf文件
		String pdfPath = path + "/pdf";


		DataRow dr = null;
		resultMsg = setPDFPath("ND" + node.getNodeID(), String.valueOf(workid), flowNo, gwf);
		if (resultMsg.indexOf("err@") != -1)
		{
			return resultMsg;
		}

		//获取绑定的表单
		FrmNode frmNode = new FrmNode();
		frmNode.Retrieve(FrmNodeAttr.FK_Frm, frmId);

		//判断当前绑定的表单是否启用
		if (frmNode.getFrmEnableRoleInt() == FrmEnableRole.Disable.getValue())
		{
			return "warning@" + frmName + "没有被启用";
		}

		//判断 who is pk
		if (flowNo != null && frmNode.getWhoIsPK() == WhoIsPK.PWorkID) //如果是父子流程
		{
			workid = gwf.getPWorkID();
		}

		//获取表单的信息执行打印
		String billUrl = SystemConfig.getPathOfInstancePacketOfData() + "ND" + node.getNodeID() + "/" + workid + "/" + frmNode.getFrmID() + "index.htm";
		resultMsg = MakeHtmlDocument(frmNode.getFrmID(), workid, flowNo, path, billUrl, "ND" + node.getNodeID());

		if (resultMsg.indexOf("err@") != -1)
		{
			return resultMsg;
		}

		// ht.Add("htm", SystemConfig.GetValByKey("HostURLOfBS", "../../DataUser/") + "/InstancePacketOfData/" + "ND" + node.getNodeID() + "/" + workid + "/" + frmNode.getFrmID() + "index.htm");

		///#region 把所有的文件做成一个zip文件.
		if (new File(pdfPath).exists() == false)
		{
			new File(pdfPath).mkdir();
		}

		fileNameFormat = fileNameFormat.substring(0, fileNameFormat.length() - 1);
		String pdfFormFile = pdfPath + "/" + frmNode.getFrmID() + ".pdf";
		String pdfFileExe = SystemConfig.getPathOfDataUser() + "ThirdpartySoftware/wkhtmltox/wkhtmltopdf.exe";
		try
		{
			Html2Pdf(pdfFileExe, resultMsg, pdfFormFile);
			if (urlIsHostUrl == false)
			{
				ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS", "../../DataUser/") + "InstancePacketOfData/" + "ND" + node.getNodeID() + "/" + workid + "/pdf/" + frmNode.getFrmID() + ".pdf");
			}
			else
			{
				ht.put("pdf", SystemConfig.GetValByKey("HostURL", "") + "/DataUser/InstancePacketOfData/" + "ND" + node.getNodeID() + "/" + workid + "/pdf/" + frmNode.getFrmID() + ".pdf");
			}


		}
		catch (RuntimeException ex)
		{
			//有可能是因为文件路径的错误， 用补偿的方法在执行一次, 如果仍然失败，按照异常处理.
			fileNameFormat = DBAccess.GenerGUID();
			pdfFormFile = pdfPath + "/" + fileNameFormat + ".pdf";

			Html2Pdf(pdfFileExe, resultMsg, pdfFormFile);
			ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS", "") + "/InstancePacketOfData/" + "ND" + node.getNodeID() + "/" + workid + "/pdf/" + frmNode.getFrmID() + ".pdf");
		}

		return bp.tools.Json.ToJsonEntitiesNoNameMode(ht);


	}

	/**
	 读取合并的pdf文件名称

	 @param Directorypath 目录
	 @param outpath 导出的路径
	 */
	public static void MergePDF(String Directorypath, String outpath) throws Exception {
		PDFMergerUtility merger=new PDFMergerUtility();
		String[] fileInFolder=BaseFileUtils.getFiles(Directorypath);
		for(int i=0;i<fileInFolder.length;i++){
			merger.addSource(fileInFolder[i]);
		}
		merger.setDestinationFileName(outpath);
		merger.mergeDocuments();
	}


	//前期文件的准备
	private static String setPDFPath(String frmID, String workid, String flowNo, GenerWorkFlow gwf) throws Exception {
		// 准备目录文件.
		String path = "";
		try {

			path = SystemConfig.getPathOfInstancePacketOfData() + frmID + "/";
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "path:" + path);
			if ((new File(path)).isDirectory() == false) {
				(new File(path)).mkdirs();
			}

			path = SystemConfig.getPathOfInstancePacketOfData() + frmID + "/" + workid;
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "path2:" + path);
			if ((new File(path)).isDirectory() == false) {
				(new File(path)).mkdirs();
			}

			// 把模版文件copy过去.
			String templateFilePath = SystemConfig.getPathOfInstancePacketOfData() + "Template/";
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "templateFilePath:" + templateFilePath);
			File dir = new File(templateFilePath);
			if(!dir.exists()){
				return "err@模板文件不存在，请将源代码中的DataUser/InstacePacketOfData目录中的Template文件拷贝到服务器。";
			}
			dir.setWritable(true);
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "读取文件夹:" + templateFilePath);
			File[] finfos = dir.listFiles();
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "读取文件夹中的文件:" + finfos.length);
			if (finfos.length == 0) {
				return "err@不存在模板文件";
			}
			for (File fl : finfos) {
				if (fl.getName().contains("ShuiYin")) {
					continue;
				}

				if (fl.getName().contains("htm")) {
					continue;
				}
				bp.da.Log.DefaultLogWriteLine(LogType.Info, "文件夹中的文件:" + path + "/" + fl.getPath());
				if ((new File(path + "/" + fl.getPath())).isFile() == true) {
					(new File(path + "/" + fl.getPath())).delete();
				}
				bp.da.Log.DefaultLogWriteLine(LogType.Info, "templateFile:" + path + "/" + fl.getPath());
				bp.da.Log.DefaultLogWriteLine(LogType.Info, "templateFile1:" + Paths.get(fl.getPath()));
				bp.da.Log.DefaultLogWriteLine(LogType.Info, "templateFile2:" + Paths.get(path + "/" + fl.getName()));
				Files.copy(Paths.get(fl.getPath()), Paths.get(path + "/" + fl.getName()),
						StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
			}

		} catch (RuntimeException ex) {
			return "err@读写文件出现权限问题，请联系管理员解决。" + ex.getMessage();
		}

		String hostURL = SystemConfig.GetValByKey("HostURL", "");
		String billUrl = hostURL + "/DataUser/InstancePacketOfData/" + frmID + "/" + workid + "/index.htm";

		// begin生成二维码.
		if(SystemConfig.GetValByKeyBoolen("IsShowQrCode",false) == true){
			/*说明是图片文件.*/
			String qrUrl = hostURL + "/WF/WorkOpt/PrintDocQRGuide.htm?FrmID=" + frmID + "&WorkID=" + workid + "&FlowNo=" + flowNo;
			if (flowNo != null)
			{
				gwf = new GenerWorkFlow(workid);
				qrUrl = hostURL + "/WF/WorkOpt/PrintDocQRGuide.htm?AP=" + frmID + "$" + workid + "_" + flowNo + "_" + gwf.getNodeID() + "_" + gwf.getStarter() + "_" + gwf.getDeptNo();
			}

			//二维码的生成
			QrCodeUtil.createQrCode(qrUrl,path,"QR.png","png");
		}
		// end生成二维码.
		return "";
	}


	/**
	 zip文件路径.

	 */
	public static String ZipFilePath = "";

	public static String CCFlowAppPath = "/";



	public static String MakeHtmlDocument(String frmID, long workid, String flowNo, String path, String indexFile, String nodeID) throws Exception {
		try
		{
			GenerWorkFlow gwf = null;

			if ((new File(path)).isDirectory() == false) {
				setPDFPath(frmID,String.valueOf(workid),flowNo,gwf);
			}

			if (flowNo != null)
				gwf = new GenerWorkFlow(workid);

			///#region 定义变量做准备.
			//生成表单信息.
			MapData mapData = new MapData(frmID);

			if (mapData.getHisFrmType()== FrmType.Url)
			{
				String url = mapData.getUrlExt();

				//替换系统参数
				url = url.replace("@WebUser.No", WebUser.getNo());
				url = url.replace("@WebUser.Name;", WebUser.getName());
				url = url.replace("@WebUser.FK_DeptName;", WebUser.getDeptName());
				url = url.replace("@WebUser.FK_Dept;", WebUser.getDeptNo());
				url = url.replace("@WebUser.DeptNo;", WebUser.getDeptNo());
				//替换参数
				if (url.indexOf("?") > 0)
				{
					//获取url中的参数
					String urlParam = url.substring(url.indexOf('?'));
					String[] paramss = url.split("[&]", -1);
					for (String param : paramss)
					{
						if (DataType.IsNullOrEmpty(param) || param.indexOf("@") == -1)
							continue;
						String[] paramArr = param.split("[=]", -1);
						if (paramArr.length == 2 && paramArr[1].indexOf('@') == 0)
						{
							if (paramArr[1].indexOf("@WebUser.") == 0)
								continue;
							url = url.replace(paramArr[1], gwf.GetValStrByKey(paramArr[1].substring(1)));
						}
					}

				}
				url = url.replace("@basePath", SystemConfig.getHostURLOfBS());
				if (url.contains("http") == false)
				{
					url = SystemConfig.getHostURLOfBS() + url;
				}

				String str = "<iframe style='width:100%;height:auto;' ID='" + mapData.getNo() + "'    src='" + url + "' frameborder=0  leftMargin='0'  topMargin='0' scrolling=auto></iframe></div>";
				String docs1 = DataType.ReadTextFile(SystemConfig.getPathOfInstancePacketOfData()+"Template/indexUrl.htm");

				docs1 = docs1.replace("@Width", mapData.getFrmW() + "px");
				if (gwf != null)
				{
					docs1 = docs1.replace("@Title", gwf.getTitle());
				}
				DataType.WriteFile(indexFile, docs1);
				return indexFile;
			}

			if (mapData.getHisFrmType() == FrmType.Develop)
			{
				GEEntity enn = new GEEntity(frmID, workid);
				String ddocs = DataType.ReadTextFile(SystemConfig.getPathOfInstancePacketOfData()+"Template/indexDevelop.htm");
				String htmlString = DBAccess.GetBigTextFromDB("Sys_MapData", "No", mapData.getNo(), "HtmlTemplateFile");


				htmlString = htmlString.replace("../../DataUser", SystemConfig.getHostURLOfBS() + "/DataUser");
				htmlString = htmlString.replace("../DataUser", SystemConfig.getHostURLOfBS() + "/DataUser");
				ddocs = ddocs.replace("@Docs", htmlString);

				ddocs = ddocs.replace("@Title", mapData.getName());

				DataType.WriteFile(indexFile, ddocs);
				GenerHtmlOfDevelop(mapData, mapData.getNo(), String.valueOf(workid), enn, path, indexFile, flowNo, nodeID);
				return indexFile;
			}


			if (mapData.getHisFrmType() == FrmType.ChapterFrm)
			{
				GEEntity enn = new GEEntity(frmID, workid);
				String ddocs = DataType.ReadTextFile(SystemConfig.getPathOfInstancePacketOfData()+"Template/indexChapterFrm.htm");
				String htmlString = DBAccess.GetBigTextFromDB("Sys_MapData", "No",mapData.getNo(), "HtmlTemplateFile");


				htmlString = htmlString.replace("../../DataUser", SystemConfig.getHostURLOfBS() + "/DataUser");
				htmlString = htmlString.replace("../DataUser", SystemConfig.getHostURLOfBS() + "/DataUser");
				ddocs = ddocs.replace("@Docs", htmlString);

				ddocs = ddocs.replace("@Title", mapData.getName());

				DataType.WriteFile(indexFile, ddocs);
				GenerHtmlOfDevelop(mapData,mapData.getNo(), String.valueOf(workid), enn, path, indexFile, flowNo, nodeID);
				return indexFile;
			}


			//以下就是经典表单.

			GEEntity en = new GEEntity(frmID, workid);

			///#region 生成水文.

			String rdt = "";
			if (en.getEnMap().getAttrs().contains("RDT"))
			{
				rdt = en.GetValStringByKey("RDT");
				if (rdt.length() > 10)
				{
					rdt = rdt.substring(0, 10);
				}
			}
			//先判断节点中水印的设置
			//判断是否打印水印
			boolean isPrintShuiYin = SystemConfig.GetValByKeyBoolen("IsPrintBackgroundWord", false);
			Node nd = null;
			if (gwf != null)
			{
				nd = new Node(gwf.getNodeID());
			}
			if (isPrintShuiYin == true)
			{
				String words = "";
				if (nd.getNodeID() != 0)
				{
					words = nd.getShuiYinModle();
				}

				if (DataType.IsNullOrEmpty(words) == true)
				{
					words = Glo.getPrintBackgroundWord();
				}
				words = words.replace("@RDT", rdt);

				if (words.contains("@") == true)
				{
					words = Glo.DealExp(words, en);
				}

				String templateFilePathMy = SystemConfig.getPathOfInstancePacketOfData()+"Template/";
				paintWaterMarkPhoto(templateFilePathMy + "ShuiYin.png", words,  path + "/ShuiYin.png");
			}

			///#endregion

			//生成 表单的 html.
			StringBuilder sb = new StringBuilder();

			///#region 替换模版文件..
			//首先判断是否有约定的文件.
			String docs = "";
			String tempFile = SystemConfig.getPathOfInstancePacketOfData()+"Template/" +mapData.getNo() + ".htm";
			if ((new File(tempFile)).isFile() == false)
			{
				if (gwf != null)
				{

					if (nd.getHisFormType() == NodeFormType.Develop)
					{
						mapData.setHisFrmType(FrmType.Develop);
					}
					else if (nd.getHisFormType() == NodeFormType.FoolForm || nd.getHisFormType() == NodeFormType.FoolTruck)
					{
						mapData.setHisFrmType(FrmType.FoolForm);
					}
					else if (nd.getHisFormType() == NodeFormType.SelfForm)
					{
						mapData.setHisFrmType(FrmType.Url);
					}
				}

				if (mapData.getHisFrmType() == FrmType.FoolForm)
				{
					docs = DataType.ReadTextFile(SystemConfig.getPathOfInstancePacketOfData()+"Template/indexFool.htm");
					sb = GenerHtmlOfFool(mapData, frmID, String.valueOf(workid), en, path, flowNo, nodeID, nd == null ? null : nd.getHisFormType());
					docs = docs.replace("@Width", mapData.getFrmW() + "px");
				}
			}

			docs = docs.replace("@Docs", sb.toString());


			docs = docs.replace("@PrintDT", DataType.getCurrentDateByFormart("yyyy年MM月dd日 HH时mm分ss秒"));

			if (flowNo != null)
			{
				gwf = new GenerWorkFlow(workid);
				gwf.setWorkID(workid);
				gwf.RetrieveFromDBSources();

				docs = docs.replace("@Title", gwf.getTitle());



				//替换模版尾部的打印说明信息.
				String pathInfo = SystemConfig.getPathOfInstancePacketOfData()+"Template/EndInfo/" + flowNo + ".txt";
				if (new File(pathInfo).exists() == false)
				{
					pathInfo = SystemConfig.getPathOfInstancePacketOfData()+"Template/EndInfo/Default.txt";
				}

				//docs = docs.replace("@EndInfo", DataType.ReadTextFile(pathInfo));
			}

			DataType.WriteFile(indexFile, docs);

			return indexFile;
		}
		catch (RuntimeException ex)
		{
			return "err@报表生成错误:" + ex.getStackTrace();
		}
	}

	private static void paintWaterMarkPhoto(String targerImagePath,String words,String srcImagePath) {
		Integer degree = -15;
		OutputStream os = null;
		try {
			Image srcImage = ImageIO.read(new File(srcImagePath));
			BufferedImage bufImage = new BufferedImage(srcImage.getWidth(null), srcImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
			// 得到画布对象
			Graphics2D graphics2D = bufImage.createGraphics();
			// 设置对线段的锯齿状边缘处理
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(srcImage.getScaledInstance(srcImage.getWidth(null), srcImage.getHeight(null), Image.SCALE_SMOOTH),
					0, 0, null);
			if (null != degree) {
				// 设置水印旋转角度及坐标
				graphics2D.rotate(Math.toRadians(degree), (double) bufImage.getWidth() / 2, (double) bufImage.getHeight() / 2);
			}
			// 透明度
			float alpha = 0.25f;
			graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			// 设置颜色和画笔粗细
			graphics2D.setColor(Color.gray);
			graphics2D.setStroke(new BasicStroke(10));
			graphics2D.setFont(new Font("SimSun", Font.ITALIC, 18));
			// 绘制图案或文字
			String cont = words;
			String dateStr = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
			int charWidth1 = 8;
			int charWidth2 = 8;
			int halfGap = 12;
			graphics2D.drawString(cont, (srcImage.getWidth(null) - cont.length() * charWidth1) / 2,
					(srcImage.getHeight(null) - (charWidth1 + halfGap)) / 2);
			graphics2D.drawString(dateStr, (srcImage.getWidth(null) - dateStr.length() * charWidth2) / 2,
					(srcImage.getHeight(null) + (charWidth2 + halfGap)) / 2);

			graphics2D.dispose();

			os = new FileOutputStream(targerImagePath);
			// 生成图片 (可设置 jpg或者png格式)
			ImageIO.write(bufImage, "png", os);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean Html2Pdf(String pdfFileExe, String htmFile, String pdf) {
		bp.da.Log.DebugWriteInfo("@开始生成PDF" + pdfFileExe + "@pdf=" + pdf + "@htmFile=" + htmFile);
		StringBuilder cmd = new StringBuilder();
		File file = new File(pdf);
		File parent = file.getParentFile();
		//如果pdf保存路径不存在，则创建路径
		if (!parent.exists()) {
			parent.mkdirs();
		}
		if (System.getProperty("os.name").indexOf("Windows") == -1) {
			// 非windows 系统
			pdfFileExe = "/usr/local/bin/wkhtmltopdf ";
			Log.DebugWriteInfo("linux生成");
		}

		cmd.append(pdfFileExe);
		cmd.append(" -T 28mm -B 26mm -L 26mm -R 22mm ");
		cmd.append(htmFile);
		cmd.append(" --disable-external-links ");//不要链接到远程网页
		cmd.append(" --enable-local-file-access ");//允许访问本地资源文件（Linux必须加此项）
		cmd.append(" --footer-font-name SimSun ");//页码字体名称
		cmd.append(" --footer-font-size 10 ");//页码字体大小
		cmd.append(" --footer-right ——[page]—— ");//页码右对齐样式
		cmd.append(" --footer-spacing 10 ");//页脚和内容之间的间距，单位为 mm （默认 0）
		cmd.append(pdf);
		Log.DebugWriteInfo("wkhtmltopdf执行命令行：" + cmd);

		boolean result = true;
		try {
			Process proc = Runtime.getRuntime().exec(cmd.toString());
			HtmlToPdfInterceptor error = new HtmlToPdfInterceptor(proc.getErrorStream());
			HtmlToPdfInterceptor output = new HtmlToPdfInterceptor(proc.getInputStream());
			error.start();
			output.start();
			proc.waitFor();
		} catch (Exception e) {
			Log.DebugWriteInfo("Exception："+e.getMessage());
			result = false;
			e.printStackTrace();
		}
		Log.DebugWriteInfo("linux生成结果："+result);
		return result;
	}
	/**
	 签名

	 @param userNo
	 @return
	 */
	private static String SignPic(String userNo) throws Exception {

		if (DataType.IsNullOrEmpty(userNo)) {
			return "";
		}
		// 如果文件存在
		String path = SystemConfig.getPathOfDataUser() + "Siganture/" + userNo + ".jpg";

		if ((new File(path)).isFile() == false) {
			path = SystemConfig.getPathOfDataUser() + "Siganture/" + userNo + ".JPG";
			if ((new File(path)).isFile() == true) {
				return "<img src='" + path + "' style='border:0px;width:100px;height:30px;'/>";
			} else {
				Emp emp = new Emp(userNo);
				return emp.getName();
			}
		} else {
			return "<img src='" + path + "' style='border:0px;width:100px;height:30px;'/>";
		}

	}


}
