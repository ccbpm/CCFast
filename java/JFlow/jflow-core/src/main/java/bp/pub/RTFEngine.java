package bp.pub;

import bp.en.*; import bp.en.Map;
import bp.da.*;
import bp.port.*;
import bp.sys.*;
import bp.tools.ConvertTools;
import bp.tools.StringHelper;
import bp.difference.*;
import bp.wf.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.math.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 WebRtfReport 的摘要说明。
*/
public class RTFEngine
{

		///#region 数据实体
	private Entities _HisEns = null;
	public final Entities getHisEns()
	{
		if (_HisEns == null)
		{
			_HisEns = new Emps();
		}

		return _HisEns;
	}

		///#endregion 数据实体

		///#region 模板数据
	public String _rtfStr = "";
	///#endregion 模板数据
	///#region 数据明细实体

	public final String GetCode(String str) {
		if (StringHelper.isNullOrEmpty(str)) {
			return "";
		}

		String rtn = "";
		byte[] rr = null;
		try {
			rr = str.getBytes("gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		for (byte b : rr) {
			if (b > 122) {
				rtn += "\\'" + String.format("%x", b);
			} else if(b < 0) {
				rtn += "\\'" + String.format("%02x", b);
			} else {
				rtn += (char) b;
			}
		}
		return rtn.replace("\n", " \\par ");
	}
	//明细表数据
	private ArrayList _EnsDataDtls = null;
	public final ArrayList getEnsDataDtls()
	{
		if (_EnsDataDtls == null)
		{
			_EnsDataDtls = new ArrayList();
		}
		return _EnsDataDtls;
	}
	//多附件数据
	private Hashtable _EnsDataAths = null;
	public final Hashtable getEnsDataAths()
	{
		if (_EnsDataAths == null)
		{
			_EnsDataAths = new Hashtable();
		}
		return _EnsDataAths;
	}


		///#endregion 数据明细实体

	/**
	 增加一个数据实体

	 @param en
	*/
	public final void AddEn(Entity en) throws Exception {
		this.getHisEns().AddEntity(en);
	}
	/**
	 增加一个Ens
	 @param dtlEns
	*/
	public final void AddDtlEns(Entities dtlEns)
	{
		this.getEnsDataDtls().add(dtlEns);
	}
	public String CyclostyleFilePath = "";
	public String TempFilePath = "";


		///#region 获取特殊要处理的流程节点信息.
	public final String GetValueByKeyOfCheckNode(String[] strs) throws Exception {
		for (Entity en : this.getHisEns())
		{
			String val = en.GetValStringByKey(strs[2]);
			switch (strs.length)
			{
				case 1:
				case 2:
					throw new RuntimeException("step1参数设置错误" + strs.toString());
				case 3: // S.9001002.Rec
					return val;
				case 4: // S.9001002.RDT.Year
					switch (strs[3])
					{
						case "Text":
							if (Objects.equals(val, "0"))
							{
								return "否";
							}
							else
							{
								return "是";
							}
						case "YesNo":
							if (Objects.equals(val, "1"))
							{
								return "[√]";
							}
							else
							{
								return "[×]";
							}
						case "Year":
							return val.substring(0, 4);
						case "Month":
							return val.substring(5, 7);
						case "Day":
							return val.substring(8, 10);
						case "NYR":
							return DataType.getDateByFormart(DataType.ParseSysDate2DateTime(val),"yyyy年MM月dd日");
						case "RMB":
							DecimalFormat fnum = new DecimalFormat("##0.00");
							return fnum.format(val);
						case "RMBDX":
							return DataType.ParseFloatToCache(Float.parseFloat(val));
						default:
							throw new RuntimeException("step2参数设置错误" + strs);
					}
				default:
					throw new RuntimeException("step3参数设置错误" + strs);
			}
		}
		throw new RuntimeException("step4参数设置错误" + strs);
	}
	public Entity HisGEEntity = null;
	/**
	 获取ICON图片的数据。

	 @param key
	 @return
	*/
	public final String GetValueImgStrs(String key) throws Exception {
		key = key.replace(" ", "");
		key = key.replace("\r\n", "");
		String web_path = SystemConfig.getPathOfWebApp();
		// 说明是图片文件.
		String path = key.replace("OID.Img@AppPath", web_path.substring(0, web_path.length() - 1)).replace("\\\\",
				"\\");
		// 定义rtf中图片字符串
		StringBuilder pict = new StringBuilder();
		// 获取要插入的图片
		// Image img = Image.FromFile(path);
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 将要插入的图片转换为16进制字符串
		String imgHexString;


		imgHexString = ImageTo16String(path);

		// 生成rtf中图片字符串
		pict.append("\n");
		pict.append("{\\pict");
		pict.append("\\jpegblip");
		pict.append("\\picscalex100");
		pict.append("\\picscaley100");
		pict.append("\\picwgoal" + image.getWidth() * 15);
		pict.append("\\pichgoal" + image.getHeight() * 15);
		pict.append(imgHexString + "}");
		pict.append("\n");
		return pict.toString();
	}

	/**
	 * 图片转换
	 *
	 * param image_path
	 * @return
	 * @throws IOException
	 */
	public static String ImageTo16String(String image_path){
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		try {
			StringBuilder imgs = new StringBuilder();
			fis = new FileInputStream(image_path);
			bos = new ByteArrayOutputStream();

			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = fis.read(buff)) != -1) {
				bos.write(buff, 0, len);
			}
			// 得到图片的字节数组
			byte[] result = bos.toByteArray();
			for (int i = 0; i < result.length; i++) {
				if ((i % 32) == 0) {
					imgs.append("\n");
				}
				byte num2 = result[i];
				int num3 = (num2 & 240) >> 4;
				int num4 = num2 & 15;
				imgs.append("0123456789abcdef".substring(num3, num3 + 1));
				imgs.append("0123456789abcdef".substring(num4, num4 + 1));
			}
			return imgs.toString();
			// 字节数组转成十六进制
		} catch (IOException e) {
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return "";
	}
	/**
	 输入轨迹表.
	 @return
	*/
	public StringBuilder GetFlowTrackTable(StringBuilder str) throws Exception {
		DataTable dt = null;
		GenerWorkerLists gwls = null;
		try {
			long workID = this.HisGEEntity.GetValInt64ByKey("OID");
			// 获取人员信息
			GenerWorkFlow gwf = new GenerWorkFlow();
			gwls = new GenerWorkerLists();
			gwf.setWorkID(workID);
			int count = gwf.RetrieveFromDBSources();
			if(count == 0) return str;
			//获取轨迹信息
			dt=bp.wf.Dev2Interface.DB_GenerTrackTable(gwf.getFlowNo(),workID, gwf.getFID());


			if (gwf.getWFState() != WFState.CompleteEnd && gwf.getWFState() != WFState.CompleteByNode) {

				gwls.Retrieve(GenerWorkerListAttr.WorkID,workID, GenerWorkerListAttr.Idx);

				// warning 补偿式的更新. 做特殊的判断，当会签过了以后仍然能够看isPass=90的错误数据.
				for (GenerWorkerList item : gwls.ToJavaList()) {
					if (item.getPassInt() == 90 && gwf.getNodeID() != item.getNodeID()) {
						item.setPassInt(0);
						item.Update();
					}
				}
			}

			String shortName = "Track";

			int pos_rowKey = str.indexOf(shortName);
			int end_rowKey = str.lastIndexOf(shortName);
			int row_start = -1, row_end = -1;
			if (pos_rowKey != -1) {
				row_start = str.substring(0, pos_rowKey).lastIndexOf("\\row");
				// 获取从表表名出现的最后的位置
				// int end_rowKey = str.lastIndexOf(shortName);
				// 获取row的位置
				row_end = str.substring(end_rowKey).indexOf("\\row");
			}

			if (row_start != -1 && row_end != -1) {
				String row = str.substring(row_start, (end_rowKey) + row_end);
				str = new StringBuilder(str.toString().replace(row, ""));

				int i = dt.Rows.size();
				//增加等待审核的人员, 在所有的人员循环以后.
				Entity gwl = new GenerWorkerLists().getNewEntity();
				for (int ii = 0; ii < gwls.size(); ii++) {

					gwl = gwls.get(ii);
					if (gwl.GetValStringByKey("IsPass").equals("1"))
						continue;

					Object tempVar = row;
					String rowData = (String) ((tempVar instanceof String) ? tempVar : null);
					//替换序号
					rowData = rowData.replace("<" + shortName + "." +"Idx>", i+1+"");
					rowData = rowData.replace("<" + shortName + "." +"NDFromT>", gwl.GetValStringByKey("FK_NodeText").toString());

					if (gwl.GetValStringByKey("IsRead").equals("1")) {
						rowData = rowData.replace("<" + shortName + "." +"Action>", "已阅读");
					} else {
						rowData = rowData.replace("<" + shortName + "." +"Action>", "尚未阅读");
					}
					rowData = rowData.replace("<" + shortName + "." +"ActionTypeText>", "等待审批");
					rowData = rowData.replace("<" + shortName + "." +"EmpFromT>", gwl.GetValStringByKey("FK_EmpText").toString());
					rowData = rowData.replace("<" + shortName + "." +"StartTime>", gwl.GetValStringByKey("RDT").toString());
					rowData = rowData.replace("<" + shortName + "." +"EndTime>", "");
					rowData = rowData.replace("<" + shortName + "." +"PassTime>", "");
					str = str.insert(row_start, rowData);
				}

				//增加已审核人员
				while (i > 0)
				{
					i--;
					DataRow dr = dt.Rows.get(i);
					if (String.valueOf(ActionType.FlowBBS.getValue()).equals(dr.getValue("actiontype").toString()))
						continue;
					if (String.valueOf(ActionType.WorkCheck.getValue()).equals(dr.getValue("actiontype").toString()))
						continue;
					Object tempVar = row;
					String rowData = (String) ((tempVar instanceof String) ? tempVar : null);
					//替换序号
					rowData = rowData.replace("<" + shortName + "." +"Idx>", String.valueOf(i + 1));
					rowData = rowData.replace("<" + shortName + "." +"NDFromT>", dr.getValue("ndfromt").toString());
					rowData = rowData.replace("<" + shortName + "." +"Action>", "已处理");
					rowData = rowData.replace("<" + shortName + "." +"ActionTypeText>", dr.getValue("actiontypetext").toString());
					rowData = rowData.replace("<" + shortName + "." +"EmpFromT>", dr.getValue("empfromt").toString());
					String startTime = "";
					String endTime = "";
					String passTime = "";
					//获取轨迹中上一个节点的时间
					if (i == 0) {
						startTime = dr.getValue("rdt").toString();
						endTime = dr.getValue("rdt").toString();
					} else {
						//上一节点的到达时间就是本节点的开始时间
						startTime= dt.Rows.get(i - 1).getValue("rdt").toString();
						endTime = dt.Rows.get(i).getValue("rdt").toString();
					}
					//求得历时时间差
					SimpleDateFormat sf = new SimpleDateFormat(DataType.getSysDateTimessFormat());
					passTime = DataType.getDatePoor(sf.parse(endTime), sf.parse(startTime));

					rowData = rowData.replace("<" + shortName + "." +"StartTime>", startTime);
					rowData = rowData.replace("<" + shortName + "." +"EndTime>", endTime);
					rowData = rowData.replace("<" + shortName + "." +"PassTime>", passTime);

					str = str.insert(row_start, rowData);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  str;
	}
	/**
	 获取ICON图片的数据。
	 @param billUrl
	 @return
	*/
	public final String GetValueImgStrsOfQR(String billUrl)
	{
		/*说明是图片文件.*/
		String path = SystemConfig.getPathOfTemp() + DBAccess.GenerGUID() + ".png"; // key.replace("OID.Img@AppPath", bp.difference.SystemConfig.getPathOfWebApp());


		///#region 生成二维码.
		bp.tools.QrCodeUtil.createQrCode(path,SystemConfig.getPathOfTemp(),DBAccess.GenerGUID()+".png","png");
		///#endregion

		//定义rtf中图片字符串
		StringBuilder pict = new StringBuilder();
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 将要插入的图片转换为16进制字符串
		String imgHexString;
		imgHexString = ImageTo16String(path);
		//生成rtf中图片字符串
		pict.append("\r\n");
		pict.append("{\\pict");
		pict.append("\\jpegblip");
		pict.append("\\picscalex100");
		pict.append("\\picscaley100");
		pict.append("\\picwgoal" + image.getWidth() * 15);
		pict.append("\\pichgoal" + image.getHeight() * 15);
		pict.append(imgHexString + "}");
		pict.append("\r\n");
		return pict.toString();
	}
	/**
	 获取M2M数据并输出

	 @param key
	 @return
	*/
	public final String GetValueM2MStrs(String key)
	{
		return "";
	}
	/**
	 获取写字版的数据

	 @param key
	 @return
	*/
	public final String GetValueBPPaintStrs(String key) {
		key = key.replace(" ", "");
		key = key.replace("\r\n", "");

		String[] strs = key.split("[.]", -1);
		String filePath = "";
		try {
			filePath = DBAccess.RunSQLReturnString("SELECT Tag2 From Sys_FrmEleDB WHERE RefPKVal="
					+ this.HisGEEntity.getPKVal() + " AND EleID='" + strs[2].trim() + "'");
			if (filePath == null) {
				return "";
			}
		} catch (java.lang.Exception e) {
			return "";
		}

		// 定义rtf中图片字符串
		StringBuilder pict = new StringBuilder();
		// 获取要插入的图片
		// System.Drawing.Image img = System.Drawing.Image.FromFile(filePath);

		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 将要插入的图片转换为16进制字符串
		String imgHexString;
		filePath = filePath.toLowerCase();
		imgHexString = ImageTo16String(filePath);
		// 生成rtf中图片字符串
		pict.append("\n");
		pict.append("{\\pict");
		pict.append("\\jpegblip");
		pict.append("\\picscalex100");
		pict.append("\\picscaley100");
		pict.append("\\picwgoal" + image.getWidth() * 15);
		pict.append("\\pichgoal" + image.getHeight() * 15);
		pict.append(imgHexString + "}");
		pict.append("\n");
		return pict.toString();
	}
	/**
	 获取类名+@+字段格式的数据.
	 比如：
	 Demo_Inc@ABC
	 Emp@Name
	 @param key
	 @return
	*/
	public final String GetValueByAtKey(String key) throws Exception {
		for (Entity en : Entities.convertEntities(this.getHisEns())) {
			String enKey = en.toString();

			// 有可能是 bp.port.Emp
			if (enKey.contains(".")) {
				enKey = en.getClass().getName();
			}

			// 如果不包含.
			if (key.toString().contains(enKey + "@") == false) {
				continue;
			}

			// 如果不包含 . 就说明，不需要转意。
			if (key.toString().contains(".") == false) {
				return en.GetValStringByKey(key.substring(key.toString().indexOf('@') + 1));
			}

			// 把实体名去掉
			key = key.replace(enKey + "@", "");
			// 把数据破开.
			String[] strs = key.split("[.]", -1);
			if (strs.length == 2) {
				if (strs[1].trim().equals("ImgAth")) {
					String path1 = SystemConfig.getPathOfDataUser() + "\\ImgAth\\Data\\" + strs[0].trim() + "_"
							+ en.getPKVal() + ".png";
					// 定义rtf中图片字符串.
					StringBuilder mypict = new StringBuilder();
					// 获取要插入的图片
					// System.Drawing.Image imgAth =
					// System.Drawing.Image.FromFile(path1);
					BufferedImage image = null;
					try {
						image = ImageIO.read(new File(path1));
					} catch (IOException e) {
						e.printStackTrace();
					}

					// 将要插入的图片转换为16进制字符串
					// String imgHexStringImgAth = GetImgHexString(imgAth,
					// System.Drawing.Imaging.ImageFormat.Jpeg);
					String imgHexStringImgAth = ImageTo16String(path1);
					// 生成rtf中图片字符串
					mypict.append("\n");
					mypict.append("{\\pict");
					mypict.append("\\jpegblip");
					mypict.append("\\picscalex100");
					mypict.append("\\picscaley100");
					mypict.append("\\picwgoal" + image.getWidth() * 15);
					mypict.append("\\pichgoal" + image.getHeight() * 15);
					mypict.append(imgHexStringImgAth + "}");
					mypict.append("\n");
					return mypict.toString();
				}

				String val = en.GetValStringByKey(strs[0].trim());
				if (strs[1].trim().equals("Text")) {
					if (val.equals("0")) {
						return "否";
					} else {
						return "是";
					}
				} else if (strs[1].trim().equals("Year")) {
					return val.substring(0, 4);
				} else if (strs[1].trim().equals("Month")) {
					return val.substring(5, 7);
				} else if (strs[1].trim().equals("Day")) {
					return val.substring(8, 10);
				} else if (strs[1].trim().equals("NYR")) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
					return format.format(DataType.ParseSysDate2DateTime(val));
				} else if (strs[1].trim().equals("RMB")) {
					return new DecimalFormat("##0.00").format(Float.parseFloat(val));
				} else if (strs[1].trim().equals("RMBDX")) {
					return DataType.ParseFloatToCache(Float.parseFloat(val));
				} else if (strs[1].trim().equals("ImgAth")) {
					String path1 = SystemConfig.getPathOfDataUser() + "\\ImgAth\\Data\\" + strs[0].trim() + "_"
							+ this.HisGEEntity.getPKVal() + ".png";

					// 定义rtf中图片字符串.
					StringBuilder mypict = new StringBuilder();
					// 获取要插入的图片
					// System.Drawing.Image imgAth =
					// System.Drawing.Image.FromFile(path1);
					BufferedImage image = null;
					try {
						image = ImageIO.read(new File(path1));
					} catch (IOException e) {
						e.printStackTrace();
					}

					// 将要插入的图片转换为16进制字符串
					// String imgHexStringImgAth = GetImgHexString(imgAth,
					// System.Drawing.Imaging.ImageFormat.Jpeg);
					String imgHexStringImgAth = ImageTo16String(path1);
					// 生成rtf中图片字符串
					mypict.append("\n");
					mypict.append("{\\pict");
					mypict.append("\\jpegblip");
					mypict.append("\\picscalex100");
					mypict.append("\\picscaley100");
					mypict.append("\\picwgoal" + image.getWidth() * 15);
					mypict.append("\\pichgoal" + image.getHeight() * 15);
					mypict.append(imgHexStringImgAth + "}");
					mypict.append("\n");
					return mypict.toString();
				} else if (strs[1].trim().equals("Siganture")) {
					String path = SystemConfig.getPathOfDataUser() + "/Siganture/" + val + ".jpg";
					// 定义rtf中图片字符串.
					StringBuilder pict = new StringBuilder();
					// 获取要插入的图片
					// System.Drawing.Image imgAth =
					// System.Drawing.Image.FromFile(path1);
					BufferedImage image = null;
					try {
						image = ImageIO.read(new File(path));
					} catch (IOException e) {
						e.printStackTrace();
					}

					// 将要插入的图片转换为16进制字符串
					// String imgHexString = GetImgHexString(imgAth,
					// System.Drawing.Imaging.ImageFormat.Jpeg);
					String imgHexString = ImageTo16String(path);
					// 生成rtf中图片字符串
					pict.append("\n");
					pict.append("{\\pict");
					pict.append("\\jpegblip");
					pict.append("\\picscalex100");
					pict.append("\\picscaley100");
					pict.append("\\picwgoal" + image.getWidth() * 15);
					pict.append("\\pichgoal" + image.getHeight() * 15);
					pict.append(imgHexString + "}");
					pict.append("\n");
					return pict.toString();
					// 替换rtf模板文件中的签名图片标识为图片字符串
					// str = str.replace(imgMark, pict.ToString());
				} else {
					throw new RuntimeException("参数设置错误，特殊方式取值错误：" + key);
				}
			}
		} // 实体循环。
		throw new RuntimeException("参数设置错误 GetValueByKey ：" + key);
	}

	/**
	 获得所所有的审核人员信息.

	 @return
	*/
	public final String GetValueCheckWorks() throws Exception {
		String html = "";

		//获得当前待办的人员,把当前审批的人员排除在外,不然就有默认同意的意见可以打印出来.
		String sql = "SELECT FK_Emp, FK_Node FROM WF_GenerWorkerlist WHERE IsPass!=1 AND WorkID=" + this.HisGEEntity.getPKVal();
		DataTable dtOfTodo = DBAccess.RunSQLReturnTable(sql);

		for (DataRow dr : dtTrack.Rows)
		{

				///#region 排除正在审批的人员.
			String nodeID = dr.getValue("NDFrom").toString();
			String empFrom = dr.getValue("EmpFrom").toString();
			if (dtOfTodo.Rows.size() != 0)
			{
				boolean isHave = false;
				for (DataRow mydr : dtOfTodo.Rows)
				{
					if (!Objects.equals(mydr.getValue("FK_Node").toString(), nodeID))
					{
						continue;
					}

					if (!Objects.equals(mydr.getValue("FK_Emp").toString(), empFrom))
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


			html += "<tr>";
			html += " <td valign=middle >" + dr.getValue("NDFromT") + "</td>";

			String msg = dr.getValue("Msg").toString();

			msg += "<br>";
			msg += "<br>";

			String empStrs = "";
			if (dtTrack == null)
			{
				empStrs = dr.getValue("EmpFromT").toString();
			}
			else
			{
				String singType = "0";
				for (DataRow drTrack : dtTrack.Rows)
				{
					if (Objects.equals(drTrack.getValue("No").toString(), dr.getValue("EmpFrom").toString()))
					{
						singType = drTrack.getValue("SignType").toString();
						break;
					}
				}

				if (Objects.equals(singType, "0") || Objects.equals(singType, "2"))
				{
					empStrs = dr.getValue("EmpFromT").toString();
				}


				if (Objects.equals(singType, "1"))
				{
					empStrs = "<img src='../../../../../DataUser/Siganture/" + dr.getValue("EmpFrom") + ".jpg' title='" + dr.getValue("EmpFromT") + "' style='height:60px;' border=0 onerror=\"src='../../../../../DataUser/Siganture/UnName.jpg'\" /> " + dr.getValue("EmpFromT");
				}

			}

			msg += "审核人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();

			html += " <td colspan=3 valign=middle >" + msg + "</td>";
			html += " </tr>";
		}

		return html;
	}
	/**
	 获得审核组件的信息.

	 @param key
	 @return
	*/
	public final String GetValueCheckWorkByKey(String key)
	{
		key = key.replace(" ", "");
		key = key.replace("\r\n", "");

		String[] strs = key.split("[.]", -1);
		if (strs.length == 3)
		{
			/*
			 *  是一个节点一个审核人的模式. <WorkCheck.RDT.101>
			 */
			if (dtTrack == null)
			{
				throw new RuntimeException("@您设置了获取审核组件里的规则，但是你没有给审核组件数据源dtTrack赋值。");
			}

			String nodeID = strs[2];
			for (DataRow dr : dtTrack.Rows)
			{
				if (!Objects.equals(dr.getValue("NDFrom").toString(), nodeID))
				{
					continue;
				}

				switch (strs[1])
				{
					case "RDT":
						return dr.getValue("RDT").toString(); //审核日期.
					case "RDT-NYR":
						String rdt = dr.getValue("RDT").toString(); //审核日期.
						LocalDateTime dt = LocalDateTime.parse(rdt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
						return dt.getYear() + "\\'c4\\'ea" + dt.getMonthValue() + "\\'d4\\'c2" + dt.getDayOfMonth() + "\\'c8\\'d5";
					case "Rec":
						return dr.getValue("EmpFrom").toString(); //记录人.
					case "RecName":
						String recName = dr.getValue("EmpFromT").toString(); //审核人.
						recName = this.GetCode(recName);
						return recName;
					case "Msg":
					case "Note":
						String text = dr.getValue("Msg").toString();
						text = text.replace("\\", "\\\\");
						text = this.GetCode(text);
						return text;

					//return System.Text.Encoder  //审核信息.
					default:
						break;
				}
			}
		}

		return "无";
	}

	private String GetValueCheckWorkByKey(DataRow row, String key)
	{
		key = key.replace(" ", "");
		key = key.replace("\r\n", "");

		switch (key)
		{
			case "RDT":
				return row.getValue("RDT").toString(); //审核日期.
			case "RDT-NYR":
				String rdt = row.getValue("RDT").toString(); //审核日期.
				LocalDateTime dt = LocalDateTime.parse(rdt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				return dt.getYear() + "\\'c4\\'ea" + dt.getMonthValue() + "\\'d4\\'c2" + dt.getDayOfMonth() + "\\'c8\\'d5";
			case "Rec":
				return row.getValue("EmpFrom").toString(); //记录人.
			case "RecName":
				String recName = row.getValue("EmpFromT").toString(); //审核人.
				recName = this.GetCode(recName);
				return recName;
			case "Msg":
			case "Note":
				String text = row.getValue("Msg").toString();
				text = text.replace("\\", "\\\\");
				text = this.GetCode(text);
				return text;
			case "Siganture":
				String empNo = row.getValue("EmpFrom").toString(); //记录人.
				return empNo; //审核人的签名.
			case "WriteDB":
				return row.getValue("WriteDB").toString();
			default:
				return row.getValue(key) instanceof String ? (String)row.getValue(key) : null;
		}
	}
	/**
	 审核节点的表示方法是 节点ID.Attr.

	 @param key
	 @return
	*/
	public final String GetValueByKey(String key) throws Exception {
		key = key.replace(" ", "");
		key = key.replace("\r\n", "");

		//获取参数代码.
		if (key.contains("@"))
		{
			return GetValueByAtKey(key);
		}

		String[] strs = key.split("[.]", -1);

		// 如果不包含 . 就说明他是从Rpt中取数据。
		//if (this.HisGEEntity != null && key.contains("ND") == false)
		if (this.HisGEEntity != null)
		{
			if (strs.length == 1)
			{
				return this.HisGEEntity.GetValStringByKey(key);
			}

			if (Objects.equals(strs[1].trim(), "Checkboxs"))
			{
				//获取复选框多选的值
				String content = this.HisGEEntity.GetValStringByKey(strs[0]);
				//转换成文本
				Attr attr = this.HisGEEntity.getEnMap().getAttrs().GetAttrByKeyOfEn(strs[0]);
				if (DataType.IsNullOrEmpty(attr.getUIBindKey()) == true)
				{
					return content;
				}
				SysEnums enums = new SysEnums(attr.getUIBindKey());
				String str = "";
				for (SysEnum en : enums.ToJavaList())
				{
					if ((content + ",").contains(en.getIntKey() + ",") == true)
					{
						str += en.getLab() + ",";
					}
				}
				if (!Objects.equals(str, ""))
				{
					str = str.substring(0, str.length() - 1);
				}
				return str;
			}
			if (Objects.equals(strs[1].trim(), "Editor"))
			{
				//获取富文本的内容
				String content = this.HisGEEntity.GetValStringByKey(strs[0]);
				content = content.replace("img+", "img ");
				String contentHtml = "<html><head></head><body>" + content + "</body></html>";
				Pattern pattern = Pattern.compile("<[^>]+>");
				Matcher matcher = pattern.matcher(contentHtml);
				String StrNohtml = matcher.replaceAll("");
				pattern = Pattern.compile("&[^;]+;");
				matcher = pattern.matcher(StrNohtml);
				StrNohtml = matcher.replaceAll("");
				return this.GetCode(StrNohtml);

			}

			if (strs[1].trim().equals("ImgAth")) {
				String path1 = SystemConfig.getPathOfDataUser() + "\\ImgAth\\Data\\" + strs[0].trim() + "_"
						+ this.HisGEEntity.getPKVal() + ".png";

				// 定义rtf中图片字符串.
				StringBuilder mypict = new StringBuilder();
				// 获取要插入的图片
				// System.Drawing.Image imgAth =
				// System.Drawing.Image.FromFile(path1);
				BufferedImage image = null;
				try {
					image = ImageIO.read(new File(path1));
				} catch (IOException e) {
					e.printStackTrace();
				}

				// 将要插入的图片转换为16进制字符串
				// String imgHexStringImgAth = GetImgHexString(imgAth,
				// System.Drawing.Imaging.ImageFormat.Jpeg);
				String imgHexStringImgAth = ImageTo16String(path1);
				// 生成rtf中图片字符串
				mypict.append("\n");
				mypict.append("{\\pict");
				mypict.append("\\jpegblip");
				mypict.append("\\picscalex100");
				mypict.append("\\picscaley100");
				mypict.append("\\picwgoal" + image.getWidth() * 15);
				mypict.append("\\pichgoal" + image.getHeight() * 15);
				mypict.append(imgHexStringImgAth + "}");
				mypict.append("\n");
				return mypict.toString();
			}


			if (strs[1].trim().equals("BPPaint")) {
				String path1 = DBAccess.RunSQLReturnString("SELECT  Tag2 FROM Sys_FrmEleDB WHERE REFPKVAL="
						+ this.HisGEEntity.getPKVal() + " AND EleID='" + strs[0].trim() + "'");
				// string path1 = SystemConfig.getPathOfDataUser() +
				// "\\BPPaint\\" + this.HisGEEntity.ToString().Trim() + "\\" +
				// this.HisGEEntity.PKVal + ".png";
				// 定义rtf中图片字符串.
				StringBuilder mypict = new StringBuilder();
				// 获取要插入的图片
				// System.Drawing.Image myBPPaint =
				// System.Drawing.Image.FromFile(path1);
				BufferedImage image = null;
				try {
					image = ImageIO.read(new File(path1));
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 将要插入的图片转换为16进制字符串
				// String imgHexStringImgAth = GetImgHexString(myBPPaint,
				// System.Drawing.Imaging.ImageFormat.Jpeg);
				String imgHexStringImgAth = ImageTo16String(path1);
				// 生成rtf中图片字符串
				mypict.append("\n");
				mypict.append("{\\pict");
				mypict.append("\\jpegblip");
				mypict.append("\\picscalex100");
				mypict.append("\\picscaley100");
				mypict.append("\\picwgoal" + image.getWidth() * 15);
				mypict.append("\\pichgoal" + image.getHeight() * 15);
				mypict.append(imgHexStringImgAth + "}");
				mypict.append("\n");
				return mypict.toString();
			}

			//根据枚举值返回选中符号
			if (strs[1].contains("-EnumYes") == true)
			{
				String relVal = this.HisGEEntity.GetValStringByKey(strs[0]);
				String[] checkVal = strs[1].split("[-]", -1);
				if (checkVal.length == 1)
				{
					return relVal;
				}
				if (relVal.equals(checkVal[0]))
				{
					return "[√]";
				}
				else
				{
					return "[×]";
				}
			}

			if (strs.length == 2)
			{
				String val = this.HisGEEntity.GetValStringByKey(strs[0].trim());
				switch (strs[1].trim())
				{

					case "Text":
						if (Objects.equals(val, "0"))
						{
							return "否";
						}
						else
						{
							return "是";
						}
					case "Year":
						return val.substring(0, 4);
					case "Month":
						return val.substring(5, 7);
					case "Day":
						return val.substring(8, 10);
					case "NYR":
						return DataType.getDateByFormart(DataType.ParseSysDate2DateTime(val),"yyyy年MM月dd日");
					case "RMB":
						return DataType.IsNullOrEmpty(val) ? "" : new DecimalFormat("##0.00").format(Float.parseFloat(val)).toString();
					case "RMBDX":
						return this.GetCode(DataType.ParseFloatToCache(Float.parseFloat(val)));
					case "Siganture":
						String path = SystemConfig.getPathOfDataUser() + "/Siganture/" + val + ".jpg";
						// 定义rtf中图片字符串
						StringBuilder pict = new StringBuilder();
						// 获取要插入的图片
						// System.Drawing.Image img =
						// System.Drawing.Image.FromFile(path);
						BufferedImage image = null;
						try {
							image = ImageIO.read(new File(path));
						} catch (IOException e) {
							e.printStackTrace();
						}

						// 将要插入的图片转换为16进制字符串
						// String imgHexStringImgAth = GetImgHexString(imgAth,
						// System.Drawing.Imaging.ImageFormat.Jpeg);
						String imgHexStringImgAth = ImageTo16String(path);
						// 生成rtf中图片字符串
						pict.append("\n");
						pict.append("{\\pict");
						pict.append("\\jpegblip");
						pict.append("\\picscalex100");
						pict.append("\\picscaley100");
						pict.append("\\picwgoal" + image.getWidth() * 15);
						pict.append("\\pichgoal" + image.getHeight() * 15);
						pict.append(imgHexStringImgAth + "}");
						pict.append("\n");
						return pict.toString();
					case "BoolenText":
						if (Objects.equals(val, "0"))
							return "否";
						return "是";

					case "Boolen":
						if (Objects.equals(val, "1"))
							return "[√]";
						return "[×]";
					case "YesNo":
						if (Objects.equals(val, "1"))
							return "[√]";
						return "[×]";

					case "Yes":
						if (Objects.equals(val, "1"))
							return "[√]";
						return "[×]";
					case "No":
						if (Objects.equals(val, "0"))
							return "[√]";
						return "[×]";

					default:
						throw new RuntimeException("参数设置错误，特殊方式取值错误：" + key);
				}
			}
			else
			{
				throw new RuntimeException("参数设置错误，特殊方式取值错误：" + key);
			}
		}

		for (Entity en : this.getHisEns())
		{
			String enKey = en.toString();
			if (enKey.contains("."))
			{
				enKey = en.getClass().getSimpleName();
			}
			if (key.contains(en.toString() + ".") == false)
			{
				continue;
			}

			/*说明就在这个字段内*/
			if (strs.length == 1)
			{
				throw new RuntimeException("参数设置错误，strs.length=1 。" + key);
			}

			if (strs.length == 2)
			{
				return en.GetValStringByKey(strs[1].trim());
			}

			if (strs.length == 3)
			{
				if (Objects.equals(strs[2].trim(), "ImgAth"))
				{
					String path1 = SystemConfig.getPathOfDataUser() + "ImgAth/Data/" + strs[1].trim() + "_" + en.getPKVal() + ".png";
					//定义rtf中图片字符串.
					StringBuilder mypict = new StringBuilder();
					BufferedImage image = null;
					try {
						image = ImageIO.read(new File(path1));
					} catch (IOException e) {
						e.printStackTrace();
					}

					//将要插入的图片转换为16进制字符串
					String imgHexStringImgAth = ImageTo16String(path1);
					//生成rtf中图片字符串
					mypict.append("\r\n");
					mypict.append("{\\pict");
					mypict.append("\\jpegblip");
					mypict.append("\\picscalex100");
					mypict.append("\\picscaley100");
					mypict.append("\\picwgoal" + image.getWidth() * 15);
					mypict.append("\\pichgoal" + image.getHeight() * 15);
					mypict.append(imgHexStringImgAth + "}");
					mypict.append("\r\n");
					return mypict.toString();
				}


				String val = en.GetValStringByKey(strs[1].trim());
				switch (strs[2].trim())
				{
					case "Text":
						if (Objects.equals(val, "0"))
						{
							return "否";
						}
						else
						{
							return "是";
						}
					case "Year":
						return val.substring(0, 4);
					case "Month":
						return val.substring(5, 7);
					case "Day":
						return val.substring(8, 10);
					case "NYR":
						return DataType.getDateByFormart(DataType.ParseSysDate2DateTime(val),"yyyy年MM月dd日");
					case "RMB":
						return new DecimalFormat("##0.00").format(Float.parseFloat(val));
					case "RMBDX":
						return DataType.ParseFloatToCache(Float.parseFloat(val));
					case "ImgAth":
						String path1 = SystemConfig.getPathOfDataUser() + "ImgAth/Data/" + strs[0].trim() + "_" + this.HisGEEntity.getPKVal() + ".png";

						// 定义rtf中图片字符串
						StringBuilder mypict = new StringBuilder();
						// 获取要插入的图片
						// System.Drawing.Image img = System.Drawing.Image.FromFile(filePath);

						BufferedImage image = null;
						try {
							image = ImageIO.read(new File(path1));
						} catch (IOException e) {
							e.printStackTrace();
						}
						// 将要插入的图片转换为16进制字符串
						String imgHexString;
						path1 = path1.toLowerCase();
						imgHexString = ImageTo16String(path1);
						//生成rtf中图片字符串
						mypict.append("\r\n");
						mypict.append("{\\pict");
						mypict.append("\\jpegblip");
						mypict.append("\\picscalex100");
						mypict.append("\\picscaley100");
						mypict.append("\\picwgoal" + image.getWidth() * 15);
						mypict.append("\\pichgoal" + image.getHeight() * 15);
						mypict.append(imgHexString + "}");
						mypict.append("\r\n");
						return mypict.toString();
					case "Siganture":
						String path = SystemConfig.getPathOfDataUser() + "Siganture/" + val + ".jpg";
						//定义rtf中图片字符串.
						StringBuilder pict = new StringBuilder();
						// 获取要插入的图片
						// System.Drawing.Image img = System.Drawing.Image.FromFile(filePath);

						BufferedImage image1 = null;
						try {
							image1 = ImageIO.read(new File(path));
						} catch (IOException e) {
							e.printStackTrace();
						}
						// 将要插入的图片转换为16进制字符串
						String imgHexString1;
						path = path.toLowerCase();
						imgHexString1 = ImageTo16String(path);
						//生成rtf中图片字符串
						pict.append("\r\n");
						pict.append("{\\pict");
						pict.append("\\jpegblip");
						pict.append("\\picscalex100");
						pict.append("\\picscaley100");
						pict.append("\\picwgoal" + image1.getWidth() * 15);
						pict.append("\\pichgoal" + image1.getHeight() * 15);
						pict.append(imgHexString1 + "}");
						pict.append("\r\n");
						return pict.toString();
					//替换rtf模板文件中的签名图片标识为图片字符串
					// str = str.replace(imgMark, pict.ToString());
					default:
						throw new RuntimeException("参数设置错误，特殊方式取值错误：" + key);
				}
			}
		}

		throw new RuntimeException("参数设置错误 GetValueByKey ：" + key);
	}

		///#endregion


		///#region 生成单据
	/**
	 生成单据

	 @param cfile 模板文件
	*/
	public final void MakeDoc(String cfile) throws Exception {
		String file = PubClass.GenerTempFileName("doc");
		this.MakeDoc(cfile, SystemConfig.getPathOfTemp(), file);
	}
	public String ensStrs = "";
	/**
	 轨迹表（用于输出打印审核轨迹,审核信息.）
	*/
	public DataTable dtTrack = null;
	public DataTable wks = null;
	public DataTable subFlows = null;

	/**
	 单据生成

	 @param templateRtfFile 模板文件
	 @param path 生成路径
	 @param file 生成文件
	*/

	public final void MakeDoc(String templateRtfFile, String path, String file) throws Exception {
		MakeDoc(templateRtfFile, path, file, null);
	}

	public final void MakeDoc(String templateRtfFile, String path, String file, String billUrl) throws Exception {
		templateRtfFile = templateRtfFile.replace(".rtf.rtf", ".rtf");

		if ((new File(path)).isDirectory() == false)
		{
			(new File(path)).mkdirs();
		}
		if(DataType.IsNullOrEmpty(this._rtfStr))
			this._rtfStr =Cache.GetBillStr(templateRtfFile, false).substring(0);
		StringBuilder str = new StringBuilder(this._rtfStr);

		if (this.getHisEns().size()== 0)
		{
			if (this.HisGEEntity == null)
			{
				throw new RuntimeException("@您没有为报表设置数据源...");
			}
		}

		this.ensStrs = "";
		if (this.getHisEns().size()!= 0)
		{
			for (Entity en : this.getHisEns())
			{
				ensStrs += en.toString();
			}
		}
		else
		{
			ensStrs = this.HisGEEntity.toString();
		}

		String error = "";
		String[] paras = null;
		if (this.HisGEEntity != null)
		{
			paras = Cache.GetBillParas(templateRtfFile, ensStrs, this.HisGEEntity);
		}
		else
		{
			paras = Cache.GetBillParas(templateRtfFile, ensStrs, this.getHisEns());
		}

		this.TempFilePath = path + file;
		try
		{
			String key = "";
			String ss = "";


				///#region 替换主表标记
			for (String para : paras)
			{
				if (DataType.IsNullOrEmpty(para))
				{
					continue;
				}

				//如果包含,时间表.
				if (para.contains("FlowTrackTable") == true && dtTrack != null)
				{
					str = new StringBuilder(str.toString().replace("<FlowTrackTable>", this.GetFlowTrackTable(str)));
					continue;

				}
				try
				{
					if (para.contains("Editor"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueByKey(para)));
					}
					else if (para.contains("ImgAth"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueByKey(para)));
					}
					else if (para.contains("Siganture"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueByKey(para)));
					}
					else if (para.contains("Img@AppPath"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueImgStrs(para)));
					}
					else if (para.contains("Img@QR"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueImgStrsOfQR(billUrl)));
					}
					else if (para.contains(".BPPaint"))
					{
						str =new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueBPPaintStrs(para)));
					}
					else if (para.contains(".M2M"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueM2MStrs(para)));
					}
					else if (para.contains(".RMBDX"))
					{
						str =new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueByKey(para)));
					}
					else if (para.contains(".RMB"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueByKey(para)));
					}
					else if (para.contains(".Boolen"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueByKey(para)));
					}
					else if (para.contains(".BoolenText"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueByKey(para)));
					}
					else if (para.contains(".NYR"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetCode(this.GetValueByKey(para))));
					}
					else if (para.contains(".Year"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueByKey(para)));
					}
					else if (para.contains(".Month"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueByKey(para)));
					}
					else if (para.contains(".Day"))
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueByKey(para)));
					}
					else if (para.contains(".Yes") == true)
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetCode(this.GetValueByKey(para))));
					}
					else if (para.contains("-EnumYes") == true)
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetCode(this.GetValueByKey(para))));
					}
					else if (para.contains(".Checkboxs") == true)
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetCode(this.GetValueByKey(para))));
					}
					else if ((para.contains("WorkCheck.RDT") == true && para.contains("WorkCheck.RDT.") == false) || (para.contains("WorkCheck.Rec") == true && para.contains("WorkCheck.Rec.") == false) || (para.contains("WorkCheck.RecName") == true && para.contains("WorkCheck.RecName.") == false) || (para.contains("WorkCheck.Note") == true && para.contains("WorkCheck.Note.") == false)) // 审核组件的审核日期.
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueCheckWorkByKey(para)));
					}
					else if (para.contains("WorkChecks") == true) //为烟台增加审核人员的信息,把所有的审核人员信息都输入到这里.
					{
						str = new StringBuilder(str.toString().replace("<" + para + ">", this.GetValueCheckWorks()));
					}
					else if (para.contains(".") == true || para.equals("IDX"))
					{
						continue; //有可能是明细表数据.
					}
					else
					{
						String val = this.GetValueByKey(para);
						val = val.replace("\\", "\\\\");
						val = this.GetCode(val);
						str = new StringBuilder(str.toString().replace("<" + para + ">", val));
					}
				}
				catch (RuntimeException ex)
				{
					error += "@替换主表标记取参数[" + para + "]出现错误：有以下情况导致此错误;1你用Text取值时间，此属性不是外键。2,类无此属性。3,该字段是明细表字段但是丢失了明细表标记.<br>更详细的信息：<br>" + ex.getMessage();
					if (SystemConfig.isDebug())
					{
						throw new RuntimeException(error);
					}
					Log.DebugWriteError(error);
				}
			}
			///#endregion 替换主表标记
			///#region 从表
			String shortName = "";
			ArrayList<Entities> al = this.getEnsDataDtls();

			for (int K = 0; K < 2; K++)
			{
				for (Entities dtls : al)
				{
					Entity dtl = dtls.getNewEntity();
					String dtlEnName = dtl.toString();
					shortName = dtlEnName.substring(dtlEnName.lastIndexOf(".") + 1);

					if (str.indexOf(shortName) == -1)
					{
						continue;
					}

					int pos_rowKey = str.indexOf("<" + shortName + ".") + 1;
					int row_start = -1, row_end = -1;
					if (pos_rowKey != -1)
					{
						row_start = str.substring(0, pos_rowKey).lastIndexOf("\\nestrow");
						if (row_start == -1)
						{
							row_start = str.substring(0, pos_rowKey).lastIndexOf("\\row");
						}


						row_end = str.substring(pos_rowKey).indexOf("\\nestrow");
						if (row_end == -1)
						{
							row_end = str.substring(pos_rowKey).indexOf("\\row");
						}

					}

					if (row_start == -1 || row_end == -1)
					{
						continue; //如果没有发现标记.
					}

					//分页是否显示表头
					boolean  dtlPageIsShowHeader  = SystemConfig.GetValByKeyBoolen("DtlPageIsShowHeader",false);
					String headerStr = "";
					if(dtlPageIsShowHeader == true){
						int h_pos_rowKey = str.indexOf("<Dtl." + shortName + ">") + 1;
						int h_start = -1;
						if (h_pos_rowKey != -1)
						{
							h_start = str.substring(0, h_pos_rowKey).lastIndexOf("\\nestrow");
							if (h_start == -1)
							{
								h_start = str.substring(0, h_pos_rowKey).lastIndexOf("\\row");
							}
							if (h_start == -1)
							{
								h_start = str.substring(0, h_pos_rowKey).lastIndexOf("}\\trowd ");
							}
						}

						String subStr = str.substring(0,row_start);
						int h_end = row_start;
						/*int h_start = subStr.lastIndexOf("\\nestrow");
						if (h_start == -1)
						{
							h_start = subStr.lastIndexOf("\\row");
						}*/
						if(h_start!=-1){
							headerStr =  str.substring(h_start, h_end);
							headerStr = headerStr.replace("<Dtl." + shortName + ">", "");
						}
					}

					//获得row的数据.
					String row = str.substring(row_start, (pos_rowKey) + row_end);
					str = new StringBuilder(str.toString().replace(row, ""));

					Map map = dtls.getNewEntity().getEnMap();
					//int i = dtls.size();
					int ortherCount = str.toString().substring(0,row_start).split("\\row").length+str.toString().substring(0,row_start).split("\\trowd").length;
					int dtlIndex = 0;
					int index = 0;
					for(int i=0;i<dtls.size();i++)
					{
						//i--;
						index++;
						String rowData = row;
						dtl = dtls.get(i);
						//替换序号
						int rowIdx = i + 1;
						rowData = rowData.replace("<IDX>", String.valueOf(rowIdx));

						for (Attr attr : map.getAttrs())
						{
							switch (attr.getMyDataType())
							{
								case DataType.AppDouble:
								case DataType.AppFloat:
									rowData = rowData.replace("<" + shortName + "." + attr.getKey() + ">", dtl.GetValStringByKey(attr.getKey()));
									break;
								case DataType.AppMoney:
									rowData = rowData.replace("<" + shortName + "." + attr.getKey() + ">", dtl.GetValDecimalByKey(attr.getKey(),2).toString());
									break;
								case DataType.AppInt:

									if (attr.getMyDataType() == DataType.AppBoolean)
									{
										rowData = rowData.replace("<" + shortName + "." + attr.getKey() + ">", dtl.GetValStrByKey(attr.getKey()));
										int v = dtl.GetValIntByKey(attr.getKey());
										if (v == 1)
										{
											rowData = rowData.replace("<" + shortName + "." + attr.getKey() + "Text>", "是");
										}
										else
										{
											rowData = rowData.replace("<" + shortName + "." + attr.getKey() + "Text>", "否");
										}
									}
									else
									{
										if (attr.getItIsEnum())
										{
											rowData = rowData.replace("<" + shortName + "." + attr.getKey() + "Text>", GetCode(dtl.GetValRefTextByKey(attr.getKey())));
										}
										else
										{
											rowData = rowData.replace("<" + shortName + "." + attr.getKey() + ">", dtl.GetValStrByKey(attr.getKey()));
										}
									}
									break;
								default:
									rowData = rowData.replace("<" + shortName + "." + attr.getKey() + ">", GetCode(dtl.GetValStrByKey(attr.getKey())));
									break;
							}
						}
						//判断有几行
						int count = ortherCount +dtlIndex;
						if(count==15){
							dtlIndex=0;
							String buff = "\\row\n\\pard\n";
							if(i==dtls.size()-1)
								str = str.insert(row_start, rowData);
							else
								str = str.insert(row_start, rowData+buff+headerStr);
							row_start+=rowData.length()+buff.length()+headerStr.length();
						}
						else{
							str = str.insert(row_start, rowData);
							row_start+=rowData.length();
							dtlIndex++;
						}

					}
					str = new StringBuilder(str.toString().replace("<Dtl." + shortName + ">", ""));
				}

			}
			///#endregion 从表
			///#region 明细 合计信息。
			al = this.getEnsDataDtls();
			for (Entities dtls : al)
			{
				Entity dtl = dtls.getNewEntity();
				String dtlEnName = dtl.toString();
				shortName = dtlEnName.substring(dtlEnName.lastIndexOf(".") + 1);
				//shortName = dtls.ToString().Substring(dtls.ToString().LastIndexOf(".") + 1);
				Map map = dtl.getEnMap();
				for (Attr attr : map.getAttrs())
				{
					switch (attr.getMyDataType())
					{
						case DataType.AppDouble:
						case DataType.AppFloat:
						case DataType.AppMoney:
						case DataType.AppRate:
							key = "<" + shortName + "." + attr.getKey() + ".SUM>";
							if (str.indexOf(key) != -1) {
								str = new StringBuilder(str.toString().replace(key,
										(new Float(dtls.GetSumFloatByKey(attr.getKey()))).toString()));
							}

							key = "<" + shortName + "." + attr.getKey() + ".SUM.RMB>";
							if (str.indexOf(key) != -1) {
								String value = new DecimalFormat("##0.00")
										.format(new Float(dtls.GetSumFloatByKey(attr.getKey())));
								str = new StringBuilder(str.toString().replace(key, value));
								// str = str.replace(key, (new
								// Float(dtls.GetSumFloatByKey(attr.getKey()))).ToString("0.00"));
							}

							key = "<" + shortName + "." + attr.getKey() + ".SUM.RMBDX>";
							if (str.indexOf(key) != -1) {
								str = new StringBuilder(str.toString().replace(key,
										GetCode(DataType.ParseFloatToCache(dtls.GetSumFloatByKey(attr.getKey())))));
							}
							break;
						case DataType.AppInt:
							key = "<" + shortName + "." + attr.getKey() + ".SUM>";
							if (str.indexOf(key) != -1) {
								str = new StringBuilder(str.toString().replace(key,
										(new Integer(dtls.GetSumIntByKey(attr.getKey()))).toString()));
							}
							break;
						default:
							break;
					}
				}
			}

				///#endregion 从表合计


				///#region 审核组件组合信息，added by liuxc,2016-12-16

			//节点单个审核人
			// 根据track表获取审核的节点
			if (dtTrack != null && str.toString().contains("<WorkCheckBegin>") == false
					&& str.toString().contains("<WorkCheckEnd>") == false && SystemConfig.getWorkCheckShow() == 0) {
				if(this.wks !=null && this.wks.Rows.size() !=0) {
					for (DataRow nd : wks.Rows) // 此处的22是ActionType.WorkCheck的值，此枚举位于BP.WF项目中，此处暂写死此值
					{
						int nodeID = nd.getValue(0) != null ? Integer.parseInt(nd.getValue(0).toString()) : 0;
						//判断是否存在一个节点多个签批意见，需要循环显示，第一个意见替换，其余的节点追加在后面
						boolean isHaveNote = str.toString().contains("WorkCheck.Note." + nodeID);
						boolean isHaveRec = str.toString().contains("WorkCheck.Rec." + nodeID);
						boolean isHaveRecName = str.toString().contains("WorkCheck.RecName." + nodeID);
						boolean isHaveRDT = str.toString().contains("WorkCheck.RDT." + nodeID);
						boolean isHaveWriteDB = str.toString().contains("WorkCheck.WriteDB." + nodeID);
						boolean isHaveSiganture = str.toString().contains("WorkCheck.Siganture." + nodeID);
						if (isHaveNote == false && isHaveRec == false && isHaveRecName == false && isHaveRDT == false && isHaveWriteDB == false && isHaveSiganture ==false)
							continue;
						//把track信息分组
						DataRow[] tracks = tracks = dtTrack.Select("NDFrom=" + nodeID);
						if (tracks.length == 0)
						{ //该节点没有签名，替换签名的内容
							String wkKey = "<WorkCheck.Note." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.Rec." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.RecName." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.RDT." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.RDT-NYR." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.Siganture." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.WriteDB." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							continue;
						}
						String workCheckStr = "";
						int idx = 0;
						for (DataRow row : tracks)
						{
							int acType = Integer.parseInt(row.getValue("ActionType").toString());
							if (acType != 22)
								continue;
							String empNo = row.getValue("EmpFrom") == null ? "" : row.getValue("EmpFrom").toString();

							if (isHaveNote)
								workCheckStr += this.GetValueCheckWorkByKey(row, "Msg") + "\\par";
							if (isHaveRec)
								workCheckStr += this.GetValueCheckWorkByKey(row, "EmpFrom") + "\\par";
							if (isHaveRecName)
								workCheckStr += this.GetValueCheckWorkByKey(row, "EmpFromT") + "\\par";
							if (isHaveRDT)
								workCheckStr += this.GetValueCheckWorkByKey(row, "RDT") + "\\par";
							if (isHaveWriteDB) {
								String wkVal = this.GetValueCheckWorkByKey(row, "WriteDB");
								wkVal = wkVal.replace("data:image/png;base64,", "");
								// 得到图片的字节数组
								Base64.Decoder decoder = Base64.getDecoder();
								byte[] result = decoder.decode(wkVal);
								ByteArrayInputStream in = new ByteArrayInputStream(result);    //将b作为输入流；

								BufferedImage image = ImageIO.read((InputStream) in);
								if(image!=null) {
									int width = image.getWidth();
									int height = image.getHeight();
									StringBuilder imgs = new StringBuilder();
									for (int i = 0; i < result.length; i++) {
										if ((i % 32) == 0) {
											imgs.append("\n");
										}
										byte num2 = result[i];
										int num3 = (num2 & 240) >> 4;
										int num4 = num2 & 15;
										imgs.append("0123456789abcdef".substring(num3, num3 + 1));
										imgs.append("0123456789abcdef".substring(num4, num4 + 1));
									}
									wkVal = imgs.toString();
									//转换
									//定义rtf中图片字符串.
									StringBuilder mypict = new StringBuilder();
									mypict.append("\n");
									mypict.append("{\\pict");
									mypict.append("\\jpegblip");
									mypict.append("\\picscalex100");
									mypict.append("\\picscaley100");
									mypict.append("\\picwgoal" + width * 3);
									mypict.append("\\pichgoal" + height * 3);
									mypict.append(wkVal + "}");
									mypict.append("\n");
									workCheckStr += mypict.toString() + "\\par";
								}
							}
							if (isHaveSiganture) {
								String val = this.GetValueCheckWorkByKey(row, "EmpFrom");
								String SiganturePath = SystemConfig.getPathOfDataUser() + "/Siganture/" + val + ".jpg";
								// 定义rtf中图片字符串
								StringBuilder pict = new StringBuilder();
								// 获取要插入的图片
								// System.Drawing.Image img =
								// System.Drawing.Image.FromFile(path);
								BufferedImage image = null;
								try {
									image = ImageIO.read(new File(SiganturePath));
								} catch (IOException e) {
									e.printStackTrace();
								}

								// 将要插入的图片转换为16进制字符串
								// String imgHexStringImgAth = GetImgHexString(imgAth,
								// System.Drawing.Imaging.ImageFormat.Jpeg);
								String imgHexStringImgAth = ImageTo16String(SiganturePath);
								// 生成rtf中图片字符串
								pict.append("\n");
								pict.append("{\\pict");
								pict.append("\\jpegblip");
								pict.append("\\picscalex100");
								pict.append("\\picscaley100");
								pict.append("\\picwgoal" + image.getWidth() * 15);
								pict.append("\\pichgoal" + image.getHeight() * 15);
								pict.append(imgHexStringImgAth + "}");
								pict.append("\n");

								//workCheckStr += pict.toString() + "\\par";
								workCheckStr += pict.toString() ;

							}
							idx++;
						}
						if (workCheckStr.equals(" "))
						{
							String wkKey = "<WorkCheck.Note." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.Rec." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.RecName." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.RDT." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.RDT-NYR." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.Siganture." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.WriteDB." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							continue;
						}
						if (isHaveNote == true || isHaveRec == true || isHaveRecName == true || isHaveRDT == true || isHaveWriteDB == true || isHaveSiganture == true)
						{
							boolean isHaveChange = false;
							if (isHaveNote == true)
							{
								str = new StringBuilder(str.toString().replace("<WorkCheck.Note." + nodeID + ">", workCheckStr));
								isHaveChange = true;
							}

							if (isHaveRec == true && isHaveChange == false)
							{
								str = new StringBuilder(str.toString().replace("<WorkCheck.Rec." + nodeID + ">", workCheckStr));
								isHaveChange = true;
							}

							if (isHaveRecName == true && isHaveChange == false)
							{
								str = new StringBuilder(str.toString().replace("<WorkCheck.RecName." + nodeID + ">", workCheckStr));
								isHaveChange = true;
							}

							if (isHaveRDT == true && isHaveChange == false)
							{
								str = new StringBuilder(str.toString().replace("<WorkCheck.RDT." + nodeID + ">", workCheckStr));
								isHaveChange = true;
							}

							if (isHaveWriteDB == true && isHaveChange == false)
							{
								str = new StringBuilder(str.toString().replace("<WorkCheck.WriteDB." + nodeID + ">", workCheckStr));
								isHaveChange = true;
							}
							if (isHaveSiganture == true && isHaveChange == false)
							{
								str = new StringBuilder(str.toString().replace("<WorkCheck.Siganture." + nodeID + ">", workCheckStr));
								isHaveChange = true;
							}

							String wkKey = "<WorkCheck.Note." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.Rec." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.RecName." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.RDT." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.RDT-NYR." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.Siganture." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));
							wkKey = "<WorkCheck.WriteDB." + nodeID + ">";
							str = new StringBuilder(str.toString().replace(wkKey, ""));

						}

//
//						int acType = Integer.parseInt(row.getValue("ActionType").toString());
//						if (acType != 22)
//							continue;
//						str = new StringBuilder(str.toString().replace("<WorkCheck.Msg." + row.getValue("NDFrom") + ">",
//								this.GetValueCheckWorkByKey(row, "Msg")));
//						str = new StringBuilder(str.toString().replace("<WorkCheck.Rec." + row.getValue("NDFrom") + ">",
//								this.GetValueCheckWorkByKey(row, "EmpFromT")));
//						str = new StringBuilder(str.toString().replace("<WorkCheck.RDT." + row.getValue("NDFrom") + ">",
//								this.GetValueCheckWorkByKey(row, "RDT")));

					}
				}
				str = SingleWorkCheckByRec(str,dtTrack);
			}
			//节点单个审核人——相关信息按配置标签输出
			if (dtTrack != null && str.toString().contains("<WorkCheckBegin>") == false && str.toString().contains("<WorkCheckEnd>") == false&& SystemConfig.getWorkCheckShow() == 1)
			{
				for (DataRow row : dtTrack.Rows) //此处的22是ActionType.WorkCheck的值，此枚举位于BP.WF项目中，此处暂写死此值
				{
					int acType = Integer.parseInt(row.getValue("ActionType").toString());
					if (acType != 22)
						continue;
					//节点从.
					String empNo = row.getValue("EmpFrom") == null ? "" : row.getValue("EmpFrom").toString();
					String nodeID = row.getValue("NDFrom").toString();
					String wkKey = "<WorkCheck.Note." + nodeID + ">";
					String wkVal = this.GetValueCheckWorkByKey(row, "Msg");
					str = new StringBuilder(str.toString().replace(wkKey, wkVal));

					wkKey = "<WorkCheck.Rec." + nodeID + ">";
					wkVal = this.GetValueCheckWorkByKey(row, "EmpFrom");
					str = new StringBuilder(str.toString().replace(wkKey, wkVal));

					wkKey = "<WorkCheck.RecName." + nodeID + ">";
					wkVal = this.GetValueCheckWorkByKey(row, "EmpFromT");
					str = new StringBuilder(str.toString().replace(wkKey, wkVal));


					wkKey = "<WorkCheck.RDT." + nodeID + ">";
					wkVal = this.GetValueCheckWorkByKey(row, "RDT");
					str = new StringBuilder(str.toString().replace(wkKey, wkVal));

					wkKey = "<WorkCheck.RDT-NYR." + nodeID + ">";
					wkVal = this.GetValueCheckWorkByKey(row, "RDT-NYR");
					str = new StringBuilder(str.toString().replace(wkKey, wkVal));

					//审核人的签名. 2020.11.28 by zhoupeng
					wkKey = "<WorkCheck.Siganture." + nodeID + ">";
					if (str.toString().contains(wkKey) == true)
					{
						wkVal = this.GetValueCheckWorkByKey(row, "EmpFrom");
						String filePath =  bp.difference.SystemConfig.getPathOfDataUser() + "Siganture/" + wkVal + ".jpg";
						//定义rtf中图片字符串.
						StringBuilder mypict = new StringBuilder();
						//获取要插入的图片
						BufferedImage image = null;
						try {
							image = ImageIO.read(new File(filePath));
						} catch (IOException e) {
							e.printStackTrace();
						}
						if(image!=null){
							//将要插入的图片转换为16进制字符串
							String imgHexString = ImageTo16String(filePath);
							//生成rtf中图片字符串
							mypict.append("\n");
							mypict.append("{\\pict");
							mypict.append("\\jpegblip");
							mypict.append("\\picscalex100");
							mypict.append("\\picscaley100");
							mypict.append("\\picwgoal" + image.getWidth() * 15);
							mypict.append("\\pichgoal" + image.getHeight() * 15);
							mypict.append(imgHexString + "}");
							mypict.append("\n");
							str = new StringBuilder(str.toString().replace(wkKey,  mypict.toString()));
						}else{
							str = new StringBuilder(str.toString().replace(wkKey,  ""));
						}
					}

					//审核人的手写签名. 2020.11.28 by zhoupeng
					wkKey = "<WorkCheck.WriteDB." + nodeID + ">";
					if (str.toString().contains(wkKey) == true)
					{
						wkVal = this.GetValueCheckWorkByKey(row, "WriteDB");
						wkVal = wkVal.replace("data:image/png;base64,", "");
						// 得到图片的字节数组
						Base64.Decoder decoder = Base64.getDecoder();
						byte[] result = decoder.decode(wkVal);
						ByteArrayInputStream in = new ByteArrayInputStream(result);    //将b作为输入流；

						BufferedImage image = ImageIO.read((InputStream) in);
						if(image!=null) {
							int width = image.getWidth();
							int height = image.getHeight();
							StringBuilder imgs = new StringBuilder();
							for (int i = 0; i < result.length; i++) {
								if ((i % 32) == 0) {
									imgs.append("\n");
								}
								byte num2 = result[i];
								int num3 = (num2 & 240) >> 4;
								int num4 = num2 & 15;
								imgs.append("0123456789abcdef".substring(num3, num3 + 1));
								imgs.append("0123456789abcdef".substring(num4, num4 + 1));
							}
							wkVal = imgs.toString();
							//转换
							//定义rtf中图片字符串.
							StringBuilder mypict = new StringBuilder();
							mypict.append("\n");
							mypict.append("{\\pict");
							mypict.append("\\jpegblip");
							mypict.append("\\picscalex100");
							mypict.append("\\picscaley100");
							mypict.append("\\picwgoal" + width * 3);
							mypict.append("\\pichgoal" + height * 3);
							mypict.append(wkVal + "}");
							mypict.append("\n");
							str = new StringBuilder(str.toString().replace(wkKey,  mypict.toString()));
						}else{
							str = new StringBuilder(str.toString().replace(wkKey,  ""));
						}

					}
				}
				str = SingleWorkCheckByRec(str,dtTrack);
			}

				// 轨迹信息
			str = GetFlowTrackTable(str);
			// 多附件

			for (Object athObjEnsName : this.getEnsDataAths().keySet()) {
				String athName = "Ath." + athObjEnsName.toString();
				String athFilesName = "";
				if (str.indexOf(athName) == -1)
					continue;

				FrmAttachmentDBs athDbs = (FrmAttachmentDBs) this.getEnsDataAths().get(athObjEnsName);
				if (athDbs == null)
					continue;
				for (FrmAttachmentDB athDb : athDbs.ToJavaList()) {
					if (athFilesName.length() > 0)
						athFilesName += " ， ";

					athFilesName += athDb.getFileName();
				}
				str = new StringBuilder(str.toString().replace("<" + athName + ">", this.GetCode(athFilesName)));
			}
             //2024.2.4注释掉 ，下边还有文件生成，dsm
//			str = new StringBuilder(str.toString().replace("<", ""));
//			str = new StringBuilder(str.toString().replace(">", ""));
//			try {
//				ConvertTools.streamWriteConvertGBK(str.toString(), TempFilePath);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

			///#region 多附件
			for (Object athObjEnsName : this.getEnsDataAths().keySet())
			{
				String athName = "Ath." + athObjEnsName;
				String athFilesName = "";
				if (str.indexOf(athName) == -1)
				{
					continue;
				}

				Object tempVar2 = this.getEnsDataAths().get(athObjEnsName);
				FrmAttachmentDBs athDbs = tempVar2 instanceof FrmAttachmentDBs ? (FrmAttachmentDBs)tempVar2 : null;
				if (athDbs == null)
				{
					continue;
				}

				if (str.indexOf("Ath." + athObjEnsName + ".ImgAth") != -1)
				{
					String wkKey = "<Ath." + athObjEnsName + ".ImgAth>";
					String athImgs = "";
					// 定义rtf中图片字符串
					StringBuilder mypict = new StringBuilder();
					for (FrmAttachmentDB athDb : athDbs.ToJavaList())
					{
						if (athFilesName.length() > 0)
						{
							athFilesName += " ， ";
						}
						int i = athDb.getFileFullName().lastIndexOf("UploadFile/");
						athDb.setFileFullName(athDb.getFileFullName().substring(i));
						String filePath = SystemConfig.getPathOfDataUser() + athDb.getFileFullName();

						BufferedImage imgAth = null;
						try {
							imgAth = ImageIO.read(new File(filePath));
						} catch (IOException e) {
							e.printStackTrace();
						}
						//imgAth.Dispose();
						//将要插入的图片转换为16进制字符串
						String imgHexStringImgAth = ImageTo16String(filePath);
						//生成rtf中图片字符串
						mypict.append("\r\n");
						mypict.append("{\\pict");
						mypict.append("\\jpegblip");
						mypict.append("\\picscalex100");
						mypict.append("\\picscaley100");
						mypict.append("\\picwgoal" + imgAth.getWidth() * 9);
						mypict.append("\\pichgoal" + imgAth.getHeight() * 9);
						mypict.append(imgHexStringImgAth + "}");
						mypict.append("\n");
						athImgs = mypict.toString();

					}
					str = new StringBuilder(str.toString().replace(wkKey, athImgs));
				}
				else
				{
					for (FrmAttachmentDB athDb : athDbs.ToJavaList())
					{
						if (athFilesName.length() > 0)
						{
							athFilesName += " ， ";
						}

						athFilesName += athDb.getFileName();
					}
				}

				str = new StringBuilder(str.toString().replace("<" + athName + ">", this.GetCode(athFilesName)));
			}
			try {
				//去掉未使用的图片表格附件
				for (String para : paras){
					if(para!=null && para.contains("Ath.") && para.contains(".ImgAth")){
						str = new StringBuilder(str.toString().replace("<" + para + ">", ""));
					}
				}
				str = new StringBuilder(str.toString().replace("<", ""));
				str = new StringBuilder(str.toString().replace(">", ""));

				/*String filePath =SystemConfig.getPathOfDataUser()+"/Temp/"+DBAccess.GenerGUID()+".docx";
				boolean flag = ConvertTools.streamWriteConvertGBK(str.toString(), filePath);
				InputStream inputStream = new FileInputStream(new File(filePath));
				//InputStream stream = new ByteArrayInputStream(str.toString().getBytes());
				XWPFDocument doc = new XWPFDocument(inputStream);
				XWPFDocument document = new XWPFDocument(inputStream);
				List<XWPFTable> tables = document.getTables();
				for(int i = 0; i<tables.size();i++){
					XWPFTable table = tables.get(i);
					setTableProperties(table);

				}

*/
				ConvertTools.streamWriteConvertGBK(str.toString(), TempFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch (RuntimeException ex)
		{
			String msg = "";
			if (SystemConfig.isDebug())
			{ // 异常可能与单据的配置有关系。
				try
				{
					this.CyclostyleFilePath = SystemConfig.getPathOfDataUser() + "CyclostyleFile/" + templateRtfFile;
					str = new StringBuilder(Cache.GetBillStr(templateRtfFile, false));
					msg = "@已经成功的执行修复线  RepairLineV2，您重新发送一次或者，退后重新在发送一次，是否可以解决此问题";
				}
				catch (RuntimeException ex1)
				{
					msg = "执行修复线失败.  RepairLineV2 " + ex1.getMessage();
				}
			}
			throw new RuntimeException("生成文档失败：单据名称[" + this.CyclostyleFilePath + "] 异常信息：" + ex.getMessage() + " @自动修复单据信息：" + msg);
		}
	}
	private static void setTableProperties(XWPFTable table) {
		CTTbl ctTable = table.getCTTbl();
		CTTblPr tblPr = ctTable.getTblPr();

		// 确保表格属性存在
		if (tblPr == null) {
			tblPr = ctTable.addNewTblPr();
		}
		// 获取表格的CTTblLayout对象
		CTTblLayoutType ctTblLayout = tblPr.getTblLayout();

		// 如果CTTblLayout对象不存在，则创建一个新的
		if (ctTblLayout == null) {
			ctTblLayout = tblPr.addNewTblLayout();
		}
		// 设置表格布局类型为“自动”
		ctTblLayout.setType(STTblLayoutType.AUTOFIT);
	}

	public static String convert(XWPFDocument doc) {
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < doc.getParagraphs().size(); i++) {
			text.append(doc.getParagraphs().get(i).getText());
		}
		return text.toString();
	}
	private StringBuilder SingleWorkCheckByRec(StringBuilder str,DataTable dtTrack) throws IOException {
		//输出指定节点指定人的审核信息
		String wkKey="";
		String nodeID="";
		String empNo ="";
		for (DataRow row : dtTrack.Rows){
			int acType = Integer.parseInt(row.getValue("ActionType").toString());
			if (acType != 22)
				continue;
			nodeID = row.getValue("NDFrom").toString();
			empNo = row.getValue("EmpFrom").toString();
			wkKey = "<WorkCheck.Msg." +nodeID+"."+empNo + ">";
			if (str.toString().contains(wkKey) == true){
				str = new StringBuilder(str.toString().replace(wkKey, this.GetValueCheckWorkByKey(row, "Msg")));
			}
			wkKey = "<WorkCheck.Note." + nodeID+"."+empNo + ">";
			if (str.toString().contains(wkKey) == true){
				str = new StringBuilder(str.toString().replace(wkKey, this.GetValueCheckWorkByKey(row, "Msg")));
			}
			wkKey = "<WorkCheck.Rec." + nodeID+"."+empNo + ">";
			if (str.toString().contains(wkKey) == true){
				str = new StringBuilder(str.toString().replace(wkKey, this.GetValueCheckWorkByKey(row, "EmpFromT")));
			}
			wkKey = "<WorkCheck.RDT." +nodeID+"."+empNo + ">";
			if (str.toString().contains(wkKey) == true){
				str = new StringBuilder(str.toString().replace(wkKey, this.GetValueCheckWorkByKey(row, "RDT")));
			}
			wkKey = "<WorkCheck.Siganture." +nodeID+"."+empNo + ">";
			if (str.toString().contains(wkKey) == true)
			{
				String filePath =  bp.difference.SystemConfig.getPathOfDataUser() + "Siganture/" + empNo + ".jpg";
				//定义rtf中图片字符串.
				StringBuilder mypict = new StringBuilder();
				//获取要插入的图片
				BufferedImage image = null;
				try {
					image = ImageIO.read(new File(filePath));
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(image!=null){
					//将要插入的图片转换为16进制字符串
					String imgHexString = ImageTo16String(filePath);
					//生成rtf中图片字符串
					mypict.append("\n");
					mypict.append("{\\pict");
					mypict.append("\\jpegblip");
					mypict.append("\\picscalex100");
					mypict.append("\\picscaley100");
					mypict.append("\\picwgoal" + image.getWidth() * 15);
					mypict.append("\\pichgoal" + image.getHeight() * 15);
					mypict.append(imgHexString + "}");
					mypict.append("\n");
					str = new StringBuilder(str.toString().replace(wkKey,  mypict.toString()));
				}else{
					str = new StringBuilder(str.toString().replace(wkKey,  ""));
				}

			}

			//审核人的手写签名. 2020.11.28 by zhoupeng
			wkKey = "<WorkCheck.WriteDB." +nodeID+"."+empNo + ">";
			if (str.toString().contains(wkKey) == true)
			{
				String wkVal = this.GetValueCheckWorkByKey(row, "WriteDB");
				wkVal = wkVal.replace("data:image/png;base64,", "");
				// 得到图片的字节数组
				Base64.Decoder decoder = Base64.getDecoder();
				byte[] result = decoder.decode(wkVal);
				ByteArrayInputStream in = new ByteArrayInputStream(result);    //将b作为输入流；

				BufferedImage image = ImageIO.read((InputStream) in);
				if(image!=null) {
					int width = image.getWidth();
					int height = image.getHeight();
					StringBuilder imgs = new StringBuilder();
					for (int i = 0; i < result.length; i++) {
						if ((i % 32) == 0) {
							imgs.append("\n");
						}
						byte num2 = result[i];
						int num3 = (num2 & 240) >> 4;
						int num4 = num2 & 15;
						imgs.append("0123456789abcdef".substring(num3, num3 + 1));
						imgs.append("0123456789abcdef".substring(num4, num4 + 1));
					}
					wkVal = imgs.toString();
					//转换
					//定义rtf中图片字符串.
					StringBuilder mypict = new StringBuilder();
					mypict.append("\n");
					mypict.append("{\\pict");
					mypict.append("\\jpegblip");
					mypict.append("\\picscalex100");
					mypict.append("\\picscaley100");
					mypict.append("\\picwgoal" + width * 3);
					mypict.append("\\pichgoal" + height * 3);
					mypict.append(wkVal + "}");
					mypict.append("\n");
					str = new StringBuilder(str.toString().replace(wkKey,  mypict.toString()));
				}else{
					str = new StringBuilder(str.toString().replace(wkKey,  ""));
				}

			}
		}
		return str;
	}
	private String WorkCheckReplace(String str, DataRow row, int nodeID)
	{
		String wkKey = "<WorkCheck.Note." + nodeID + ">";
		String wkVal;
		if (row != null && !"null".equals(this.GetValueCheckWorkByKey(row, "Msg")))
		{
			wkVal = this.GetValueCheckWorkByKey(row, "Msg");
		}
		else
		{
			wkVal = "";
		}
		str = str.replace(wkKey, wkVal);
		wkKey = "<WorkCheck.Rec." + nodeID + ">";
		if (row != null)
		{
			wkVal = this.GetValueCheckWorkByKey(row, "EmpFrom");
		}
		else
		{
			wkVal = "";
		}
		str = str.replace(wkKey, wkVal);
		wkKey = "<WorkCheck.RecName." + nodeID + ">";
		if (row != null)
		{
			wkVal = this.GetValueCheckWorkByKey(row, "EmpFromT");
		}
		else
		{
			wkVal = "";
		}
		str = str.replace(wkKey, wkVal);
		wkKey = "<WorkCheck.RDT." + nodeID + ">";
		if (row != null)
		{
			wkVal = this.GetValueCheckWorkByKey(row, "RDT");
		}
		else
		{
			wkVal = "";
		}
		str = str.replace(wkKey, wkVal);
		wkKey = "<WorkCheck.RDT-NYR." + nodeID + ">";
		wkKey = "<WorkCheck.Siganture." + nodeID + ">";
		if (str.indexOf(wkKey) != -1)
		{
			if (row != null)
			{
				wkVal = this.GetCode(this.GetValueCheckWorkByKey(row, "EmpFrom"));
			}
			else
			{
				wkVal = "";
			}
			String filePath = SystemConfig.getPathOfDataUser() + "/Siganture/" + wkVal + ".jpg";
			// 定义rtf中图片字符串
			StringBuilder mypict = new StringBuilder();
			// 获取要插入的图片
			// System.Drawing.Image img =
			// System.Drawing.Image.FromFile(path);
			//获取要插入的图片
			BufferedImage imgAth = null;
			try {
				imgAth = ImageIO.read(new File(filePath));
			} catch (IOException e) {
				e.printStackTrace();
			}

			//将要插入的图片转换为16进制字符串
			String imgHexStringImgAth =ImageTo16String(filePath);
			//生成rtf中图片字符串
			mypict.append("\r\n");
			mypict.append("{\\pict");
			mypict.append("\\jpegblip");
			mypict.append("\\picscalex100");
			mypict.append("\\picscaley100");
			mypict.append("\\picwgoal" + imgAth.getWidth() * 15);
			mypict.append("\\pichgoal" + imgAth.getHeight() * 15);
			mypict.append(imgHexStringImgAth + "}");
			mypict.append("\n");
			str = str.replace(wkKey, mypict.toString());
		}
		return str;
	}

		///#region 方法
	/**
	 RTFEngine
	*/
	public RTFEngine()
	{
		this._EnsDataDtls = null;
		this._HisEns = null;
	}

	public RTFEngine(String rtfFile) throws Exception {
		this._EnsDataDtls = null;
		this._HisEns = null;
		this._rtfStr = Cache.GetBillStr(rtfFile, false).substring(0);
	}

	/**
	 传入的是单个实体

	 @param en
	*/
	public RTFEngine(Entity en)
	{
		this._EnsDataDtls = null;
		this._HisEns = null;
		this.HisGEEntity = en;
	}

		///#endregion
}
