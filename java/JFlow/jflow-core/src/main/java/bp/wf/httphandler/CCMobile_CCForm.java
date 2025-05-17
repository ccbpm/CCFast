package bp.wf.httphandler;

import bp.da.*;
import bp.difference.handler.CommonFileUtils;
import bp.sys.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.wf.CCFormAPI;
import bp.wf.template.*;
import bp.tools.*;
import bp.difference.*;
import bp.wf.*;
import net.sf.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.time.*;
/**
 表单
*/
public class CCMobile_CCForm extends bp.difference.handler.DirectoryPageBase
{

	/**
	 构造函数
	*/
	public CCMobile_CCForm() throws Exception {
		WebUser.setSheBei("Mobile");
	}
	public final String HandlerMapExt() throws Exception {
		WF_CCForm en = new WF_CCForm();
		return en.HandlerMapExt();
	}

	public final String AttachmentUpload_Down() throws Exception {
		WF_CCForm ccform = new WF_CCForm();
		return ccform.AttachmentUpload_Down();
	}
	/**
	 表单初始化.

	 @return
	*/
	public final String Frm_Init() throws Exception {
		WF_CCForm ccform = new WF_CCForm();
		return ccform.Frm_Init();
	}

	public final String FrmGener_Init() throws Exception {
		WF_CCForm ccform = new WF_CCForm();
		return ccform.FrmGener_Init();
	}

	public final String Dtl_Init() throws Exception {
		WF_CCForm ccform = new WF_CCForm();
		return ccform.Dtl_Init();
	}

	//保存从表数据
	public final String Dtl_SaveRow() throws Exception {

			///#region  查询出来从表数据.
		GEDtls dtls = new GEDtls(this.getEnsName());
		bp.en.Entity tempVar = dtls.getNewEntity();
		GEDtl dtl = tempVar instanceof GEDtl ? (GEDtl)tempVar : null;
		dtls.Retrieve("RefPK", this.GetRequestVal("RefPKVal"), null);
		MapDtl mdtl = new MapDtl(this.getEnsName());
		Map map = dtl.getEnMap();
		for (GEDtl item : dtls.ToJavaList())
		{
			String pkval = item.GetValStringByKey(dtl.getPK());
			for (Attr attr : map.getAttrs())
			{
				if (attr.getItIsRefAttr()  == true)
				{
					continue;
				}

				if (attr.getMyDataType() == DataType.AppDateTime || attr.getMyDataType() == DataType.AppDate)
				{
					if (attr.getUIIsReadonly() == true)
					{
						continue;
					}

					String val = this.GetValFromFrmByKey("TB_" + attr.getKey() + "_" + pkval, null);
					item.SetValByKey(attr.getKey(), URLDecoder.decode(val, "UTF-8"));
					continue;
				}


				if (attr.getUIContralType() == UIContralType.TB)
				{

					String val = this.GetValFromFrmByKey("TB_" + attr.getKey() + "_" + pkval, null);
					item.SetValByKey(attr.getKey(), URLDecoder.decode(val, "UTF-8"));
					continue;
				}

				if (attr.getUIContralType() == UIContralType.DDL)
				{
					String val = this.GetValFromFrmByKey("DDL_" + attr.getKey() + "_" + pkval);
					item.SetValByKey(attr.getKey(), URLDecoder.decode(val, "UTF-8"));
					continue;
				}

				if (attr.getUIContralType() == UIContralType.CheckBok)
				{
					String val = this.GetValFromFrmByKey("CB_" + attr.getKey() + "_" + pkval, "-1");
					if (Objects.equals(val, "0"))
					{
						item.SetValByKey(attr.getKey(), 0);
					}
					else
					{
						item.SetValByKey(attr.getKey(), 1);
					}
					continue;
				}
			}
			item.SetValByKey("OID",pkval);
			//关联主赋值.
			item.setRefPK(this.getRefPKVal());
			switch (mdtl.getDtlOpenType())
			{
				case ForEmp: // 按人员来控制.
					item.setRefPK(this.getRefPKVal());
					break;
				case ForWorkID: // 按工作ID来控制
					item.setRefPK(this.getRefPKVal());
					item.setFID(this.getFID());
					break;
				case ForFID: // 按流程ID来控制.
					item.setRefPK(this.getRefPKVal());
					item.setFID(this.getFID());
					break;
			}
			item.setRec(WebUser.getNo());
			item.Update(); //执行更新.
		}
		return "保存成功.";

			///#endregion  查询出来从表数据.

	}

	//多附件上传方法
	public final String UploadIOSAttach() throws Exception {

		String uploadFileM = ""; //上传附件数据的MyPK,用逗号分开
		String pkVal = this.GetRequestVal("PKVal");
		String attachPk = this.GetRequestVal("FK_FrmAttachment");
		String sort = this.GetRequestVal("Sort");
		String fileSoruce = this.GetRequestVal("fileSource");
		String fileName = this.GetRequestVal("fileName");
		String ext = this.GetRequestVal("Ext");

		// 多附件描述.
		FrmAttachment athDesc = new FrmAttachment(attachPk);
		MapData mapData = new MapData(athDesc.getFrmID());
		String msg = "";
		//求出来实体记录，方便执行事件.
		GEEntity en = new GEEntity(athDesc.getFrmID());
		en.setPKVal(pkVal);
		if (en.RetrieveFromDBSources() == 0)
		{
			en.setPKVal(this.getFID());
			if (en.RetrieveFromDBSources() == 0)
			{
				en.setPKVal(this.getPWorkID());
				en.RetrieveFromDBSources();
			}
		}

		//求主键. 如果该表单挂接到流程上.
		if (this.getNodeID() != 0)
		{
			//判断表单方案。
			FrmNode fn = new FrmNode(this.getNodeID(), this.getFrmID());
			if (fn.getFrmSln() == FrmSln.Self)
			{
				FrmAttachment myathDesc = new FrmAttachment();
				myathDesc.setMyPK(attachPk + "_" + this.getNodeID());
				if (myathDesc.RetrieveFromDBSources() != 0)
				{
					athDesc.setHisCtrlWay(myathDesc.getHisCtrlWay());
				}
			}
			pkVal = Dev2Interface.GetAthRefPKVal(this.getWorkID(), this.getPWorkID(), this.getFID(), this.getNodeID(), this.getFrmID(), athDesc);
		}

		//获取上传文件是否需要加密
		boolean fileEncrypt = SystemConfig.isEnableAthEncrypt();


			///#region 文件上传的iis服务器上 or db数据库里.
		if (athDesc.getAthSaveWay() == AthSaveWay.IISServer || athDesc.getAthSaveWay() == AthSaveWay.DB)
		{
			String savePath = athDesc.getSaveTo();
			if (savePath.contains("@") == true || savePath.contains("*") == true)
			{
				/*如果有变量*/
				savePath = savePath.replace("*", "@");

				if (savePath.contains("@") && this.getNodeID() != 0)
				{
					/*如果包含 @ */
					Flow flow = new Flow(this.getFlowNo());
					GERpt myen = flow.getHisGERpt();
					myen.setOID(this.getWorkID());
					myen.RetrieveFromDBSources();
					savePath = bp.wf.Glo.DealExp(savePath, myen, null);
				}
				if (savePath.contains("@") == true)
				{
					throw new RuntimeException("@路径配置错误,变量没有被正确的替换下来." + savePath);
				}
			}
			else
			{
				savePath = athDesc.getSaveTo() + "/" + pkVal;
			}

			//替换关键的字串.
			savePath = savePath.replace("\\\\", "/");
			try
			{
				if (savePath.contains(SystemConfig.getPathOfWebApp()) == false)
				{
					savePath = SystemConfig.getPathOfWebApp() + savePath;
				}
			}
			catch (RuntimeException ex)
			{
				savePath = SystemConfig.getPathOfDataUser() + "UploadFile/" + mapData.getNo() + "/";
				//return "err@获取路径错误" + ex.Message + ",配置的路径是:" + savePath + ",您需要在附件属性上修改该附件的存储路径.";
			}

			try
			{
				if ((new File(savePath)).isDirectory() == false)
				{
					(new File(savePath)).mkdirs();
				}
			}
			catch (RuntimeException ex)
			{
				throw new RuntimeException("err@创建路径出现错误，可能是没有权限或者路径配置有问题:" + savePath + "@异常信息:" + ex.getMessage());
			}


			String guid = DBAccess.GenerGUID(0, null, null);



			String realSaveTo = savePath + "/" + guid + "." + fileName;

			realSaveTo = realSaveTo.replace("~", "-");
			realSaveTo = realSaveTo.replace("'", "-");
			realSaveTo = realSaveTo.replace("*", "-");

			if (fileEncrypt == true)
			{
				String strtmp = realSaveTo + ".tmp";
				Base64StrToImage(fileSoruce, strtmp);
				AesEncodeUtil.encryptFile(strtmp, strtmp.replace(".tmp", ""));
				(new File(strtmp)).delete(); //删除临时文件
			}
			else
			{
				//文件保存的路径
				Base64StrToImage(fileSoruce, realSaveTo);
			}

			//执行附件上传前事件，added by liuxc,2017-7-15
			msg = ExecEvent.DoFrm(mapData, EventListFrm.AthUploadeBefore, en, "@FK_FrmAttachment=" + athDesc.getMyPK() + "@FileFullName=" + realSaveTo);
			if (!DataType.IsNullOrEmpty(msg))
			{
				bp.sys.base.Glo.WriteLineError("@AthUploadeBefore事件返回信息，文件：" + fileName + "，" + msg);
				(new File(realSaveTo)).delete();

			}

			File info = new File(realSaveTo);
			FrmAttachmentDB dbUpload = new FrmAttachmentDB();
			dbUpload.setMyPK(guid);
			dbUpload.setNodeID(this.getNodeID());
			dbUpload.setSort(sort);
			dbUpload.setFrmID(athDesc.getFrmID());
			dbUpload.setFKFrmAttachment(attachPk);
			dbUpload.setFileExts(info.getName().substring(fileName.lastIndexOf(".")));
			dbUpload.setFID(this.getFID());
			if (fileEncrypt == true)
			{
				dbUpload.SetPara("IsEncrypt", 1);
			}
			dbUpload.setFileFullName(realSaveTo);
			dbUpload.setFileName(fileName);
			dbUpload.setFileSize((float)info.length());
			dbUpload.setRDT(DataType.getCurrentDateTimess());
			dbUpload.setRec(WebUser.getNo());
			dbUpload.setRecName(WebUser.getName());
			dbUpload.setDeptNo(WebUser.getDeptNo());
			dbUpload.setDeptName(WebUser.getDeptName());
			dbUpload.setRefPKVal( pkVal);
			dbUpload.setFID(this.getFID());

			dbUpload.setUploadGUID(guid);
			dbUpload.Insert();
			//执行附件上传后事件，added by liuxc,2017-7-15
			msg = ExecEvent.DoFrm(mapData, EventListFrm.AthUploadeAfter, en, "@FK_FrmAttachment=" + dbUpload.getFKFrmAttachment() + "@FK_FrmAttachmentDB=" + dbUpload.getMyPK() + "@FileFullName=" + dbUpload.getFileFullName());
			if (!DataType.IsNullOrEmpty(msg))
			{
				bp.sys.base.Glo.WriteLineError("@AthUploadeAfter事件返回信息，文件：" + dbUpload.getFileName() + "，" + msg);
			}
		}

			///#endregion 文件上传的iis服务器上 or db数据库里.


			///#region 保存到数据库 / FTP服务器上 / OSS服务器上.
		if (athDesc.getAthSaveWay() == AthSaveWay.FTPServer || athDesc.getAthSaveWay() == AthSaveWay.OSS)
		{
			String guid = DBAccess.GenerGUID(0, null, null);

			//把文件临时保存到一个位置.
			String temp = SystemConfig.getPathOfTemp() + guid + ".tmp";

			if (fileEncrypt == true)
			{
				String strtmp = SystemConfig.getPathOfTemp() + guid + "_Desc" + ".tmp";
				Base64StrToImage(fileSoruce, strtmp);
				AesEncodeUtil.encryptFile(strtmp, temp); //加密
				(new File(strtmp)).delete(); //删除临时文件
			}
			else
			{
				//文件保存的路径
				Base64StrToImage(fileSoruce, temp);
			}

			//执行附件上传前事件，added by liuxc,2017-7-15
			msg = ExecEvent.DoFrm(mapData, EventListFrm.AthUploadeBefore, en, "@FK_FrmAttachment=" + athDesc.getMyPK() + "@FileFullName=" + temp);
			if (DataType.IsNullOrEmpty(msg) == false)
			{
				bp.sys.base.Glo.WriteLineError("@AthUploadeBefore事件返回信息，文件：" + fileName + "，" + msg);
				(new File(temp)).delete();

				throw new RuntimeException("err@上传附件错误：" + msg);
			}

			File info = new File(temp);
			FrmAttachmentDB dbUpload = new FrmAttachmentDB();
			dbUpload.setMyPK(DBAccess.GenerGUID(0, null, null));
			dbUpload.setSort(sort);
			dbUpload.setNodeID(this.getNodeID());
			dbUpload.setFrmID(athDesc.getFrmID());
			dbUpload.setFKFrmAttachment(athDesc.getMyPK());
			dbUpload.setFID(this.getFID()); //流程id.
			if (fileEncrypt == true)
			{
				dbUpload.SetPara("IsEncrypt", 1);
			}

			dbUpload.setRefPKVal( pkVal.toString());
			dbUpload.setFrmID(athDesc.getFrmID());
			dbUpload.setFKFrmAttachment(athDesc.getMyPK());
			dbUpload.setFileName(fileName);
			dbUpload.setFileSize((float)info.length());
			dbUpload.setRDT(DataType.getCurrentDateTimess());
			dbUpload.setRec(WebUser.getNo());
			dbUpload.setRecName(WebUser.getName());
			dbUpload.setDeptNo(WebUser.getDeptNo());
			dbUpload.setDeptName(WebUser.getDeptName());

			dbUpload.setUploadGUID(guid);


			if (athDesc.getAthSaveWay() == AthSaveWay.FTPServer)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
				String ny = sdf.format(new Date());
				String workDir = ny + "/" + athDesc.getFrmID() + "/";
				FtpUtil ftpUtil = bp.wf.Glo.getFtpUtil();
				ftpUtil.changeWorkingDirectory(workDir,true);
				// 把文件放在FTP服务器上去.
				boolean isOK=ftpUtil.uploadFile( guid + "." + dbUpload.getFileExts(),temp);
				ftpUtil.releaseConnection();
				//设置路径.
				dbUpload.setFileFullName(ny + "//" + athDesc.getFrmID() + "//" + guid + "." + dbUpload.getFileExts());
				dbUpload.Insert();
				(new File(temp)).delete();
			}
			// 文件上传到OSS服务器上
			if(athDesc.getAthSaveWay() == AthSaveWay.OSS)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
				String ny = sdf.format(new Date());
				String fName = ny + File.separator + athDesc.getFrmID() + File.separator + guid + "." + dbUpload.getFileExts();
				//调用OSS工具类上传
				OSSUploadFileUtils.uploadFile(fName, Files.newInputStream(new File(temp).toPath()));
				//设置路径.
				dbUpload.setFileFullName(fName);
				dbUpload.Insert();
				(new File(temp)).delete();
			}
			//执行附件上传后事件，added by liuxc,2017-7-15
			msg = ExecEvent.DoFrm(mapData, EventListFrm.AthUploadeAfter, en, "@FK_FrmAttachment=" + dbUpload.getFKFrmAttachment() + "@FK_FrmAttachmentDB=" + dbUpload.getMyPK() + "@FileFullName=" + temp);
			if (DataType.IsNullOrEmpty(msg) == false)
			{
				bp.sys.base.Glo.WriteLineError("@AthUploadeAfter事件返回信息，文件：" + dbUpload.getFileName() + "，" + msg);
			}

		}

			///#endregion 保存到数据库.
		return "上传成功";
	}


	public final void Base64StrToImage(String base64Str, String savePath)
	{
		try{
			base64Str = base64Str.replace(" ", "+");
			String[] str = base64Str.split(",");  //base64Str为base64完整的字符串，先处理一下得到我们所需要的字符串
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] imageBytes = decoder.decode(str[1]);
			InputStream in = new ByteArrayInputStream(imageBytes);
			byte[] b = new byte[1024];
			int nRead = 0;

			OutputStream o = new FileOutputStream(savePath);

			while ((nRead = in.read(b)) != -1) {
				o.write(b, 0, nRead);
			}

			o.flush();
			o.close();
			in.close();
		}catch(Exception e){
			throw new RuntimeException("err@IOS上传附件失败");
		}
	}

	/**
	 获取百度云token

	 @return
	*/
	public final String getAccessToken()  {
		String ak = SystemConfig.getAPIKey();
		String sk = SystemConfig.getSecretKey();

		// 获取token地址
		String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
		String getAccessTokenUrl = authHost
				// 1. grant_type为固定参数
				+ "grant_type=client_credentials"
				// 2. 官网获取的 API Key
				+ "&client_id=" + ak
				// 3. 官网获取的 Secret Key
				+ "&client_secret=" + sk;
		try {
			URL realUrl = new URL(getAccessTokenUrl);
			// 打开和URL之间的连接
			HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			// 定义 BufferedReader输入流来读取URL的响应
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String result = "";
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			return result;
		} catch (Exception e) {
			System.err.printf("获取token失败！");
			e.printStackTrace(System.err);
		}
		return null;
	}

	public final String GetAccessTokenImgs() throws Exception {
		//获取 AccessToken
		return bp.gpm.weixin.WeiXinEntity.getAccessToken();
	}

	/**
	 下载微信服务器图片，上传到应用服务器
	*/
	public final String MyFlowGener_SaveUploadeImg()
	{
		try
		{
			String media_id = this.GetRequestVal("IDs");
			String athMyPK = this.GetRequestVal("AthMyPK"); //图片组件.

			FrmAttachment athDesc = new FrmAttachment(athMyPK);

			if (DataType.IsNullOrEmpty(media_id))
			{
				return "media_id为空";
			}
			String accessToken = GetAccessTokenImgs();
			String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=%1$s&media_id=%2$s", accessToken, media_id);
			URL httpUrl = new URL(url);
			HttpURLConnection  conn = (HttpURLConnection) httpUrl.openConnection();
			conn.setRequestMethod("POST");
			conn.connect();
			BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
			String content_disposition = conn.getHeaderField("content-disposition");
			//微信服务器生成的文件名称
			String fileName ="";
			String[] content_arr = content_disposition.split(";");
			if(content_arr.length  == 2){
				String tmp = content_arr[1];
				int index = tmp.indexOf("\"");
				fileName =tmp.substring(index+1, tmp.length()-1);
			}
			//生成不同文件名称
			File file = new File(SystemConfig.getPathOfTemp() +fileName);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			byte[] buf = new byte[2048];
			int length = bis.read(buf);
			while(length != -1){
				bos.write(buf, 0, length);
				length = bis.read(buf);
			}
			bos.close();
			bis.close();
			Log.DebugWriteError(this.getNodeID() + ":" + this.getFlowNo() + ":" + this.getWorkID() + ":" + athDesc.getNoOfObj() + ":" + athDesc.getFrmID() + ":" + SystemConfig.getPathOfTemp() + fileName + ":" + fileName);
			CCFormAPI.CCForm_AddAth(this.getNodeID(),this.getFlowNo(), this.getWorkID(), athMyPK, athDesc.getFrmID(), SystemConfig.getPathOfTemp() + fileName, fileName);

			return "执行成功";
		}
		catch (Exception ex)
		{
			String msg = "err@GetMedia:" + ex.getMessage() + " -- " + ex.getStackTrace();
			Log.DebugWriteError(msg);
			return msg;

		}
	}
	/**
	 * BufferedImage转Byte数组
	 * @param bitmap
	 * @param fileExtension (jpg,png,webp...)
	 * @return
	 * @throws IOException
	 */
	public static byte[] BitmapToBytes(BufferedImage bitmap, String fileExtension) throws IOException {
		ByteArrayOutputStream baos = null;
		try
		{
			// 获取图像文件的后缀名
			//String fileName = bitmap.getName();
			//String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
			// 将图像转换为字节数组
			baos = new ByteArrayOutputStream();
			ImageIO.write(bitmap, fileExtension, baos);
			byte[] imageBytes = baos.toByteArray();

			return imageBytes;
		}
		catch (NullPointerException ex)
		{
			throw ex;
		}
		finally
		{
			baos.close();
		}
	}
	/**
	 调用企业号获取地理位置

	 @return
	*/
	public final String GetWXConfigSetting() throws Exception {
		String htmlPage = this.GetRequestVal("htmlPage");
		Hashtable ht = new Hashtable();

		//生成签名的时间戳
		String timestamp = DataType.getCurrentDateByFormart("yyyyMMDDHHddss");
		//生成签名的随机串
		String nonceStr = DBAccess.GenerGUID(0, null, null);
		//企业号jsapi_ticket
		String jsapi_ticket = "";
		String url1 = htmlPage;
		//获取 AccessToken
		String CorpID = this.GetRequestVal("CorpID");
		if(DataType.IsNullOrEmpty(CorpID) == false)
			SystemConfig.getAppSettings().put("CorpID",CorpID);
		String CorpSecret = this.GetRequestVal("CorpSecret");
		if(DataType.IsNullOrEmpty(CorpSecret) == false)
			SystemConfig.getAppSettings().put("AppSecret",CorpSecret);
		String accessToken = bp.gpm.weixin.WeiXinEntity.getAccessToken();

		String url = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=" + accessToken;
		String str = DataType.ReadURLContext(url, 9999);
		JSONObject jd = JSONObject.fromObject(str);
		if(jd.getString("errcode").equals("0")){
			jsapi_ticket = jd.getString("ticket");
		}else
		{
			return "err:@获取jsapi_ticket失败+accessToken=" + accessToken;
		}

		ht.put("timestamp", timestamp);
		ht.put("nonceStr", nonceStr);
		//企业微信的corpID
		ht.put("AppID", SystemConfig.getWX_CorpID());

		//生成签名算法
		String str1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url1 + "";
		String Signature = bp.wf.difference.Glo.Sha1Signature(str1);
		ht.put("signature", Signature);

		return Json.ToJson(ht);
	}


	public String GetIDCardInfo() throws Exception
	{
		String token = getAccessToken();
		JSONObject jd = JSONObject.fromObject(token);

		String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";
		// 本地文件路径
		// 图片的base64编码
		long filesSize = CommonFileUtils.getFilesSize(getRequest(), "file");
		if (filesSize == 0) {
			return "err@请选择要上传的身份证件。";
		}

		InputStream stream = CommonFileUtils.getInputStream(getRequest(), "file");//new MemoryStream();
		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		Base64.Encoder encoder = Base64.getEncoder();
		String imgParam = encoder.encodeToString(bytes);
		stream.close();

		String param = "id_card_side=" + "front" + "&image=" + imgParam;

		// 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
		Hashtable<String,String> map = new Hashtable<String,String>();
		map.put("access_token",jd.getString("access_token"));
		map.put("id_card_side","front");
		map.put("image",imgParam);

		String result =HttpClientUtil.doPost(url, map, "Content-Type","application/x-www-form-urlencoded");
		System.out.println(result);
		return result;

	}
	/**
	 获取微信公众号的签名

	 @return
	*/
	public final String GetWXGZHConfigSetting() throws Exception {
		String htmlPage = this.GetRequestVal("htmlPage");
		Hashtable ht = new Hashtable();

		//生成签名的时间戳
		String timestamp = DataType.getCurrentDateByFormart("yyyyMMDDHHddss");
		//生成签名的随机串
		String nonceStr = DBAccess.GenerGUID(0, null, null);
		//企业号jsapi_ticket
		String jsapi_ticket = "";
		String url1 = htmlPage;
		//获取 AccessToken
		bp.wf.weixin.gzh.WeiXinGZHModel.AccessToken accessToken = bp.wf.weixin.WeiXinGZHEntity.getAccessToken();

		if (!Objects.equals(accessToken.getErrcode(), "0") && DataType.IsNullOrEmpty(accessToken.getErrcode()) == false)
		{
			return "err@获取网页授权失败，errcode：" + accessToken.getErrcode();
		}
		String token = accessToken.getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + token + "&type=jsapi";

		String str = PubGlo.HttpGet(url);

		//权限签名算法
		bp.gpm.weixin.Ticket ticket = new bp.gpm.weixin.Ticket();
		ticket = (bp.gpm.weixin.Ticket) FormatToJson.ParseFromJson(str);

		if (Objects.equals(ticket.getErrcode(), "0"))
		{
			jsapi_ticket = ticket.getTicket();
		}
		else
		{
			return "err@获取jsapi_ticket失败+accessToken=" + token;
		}

		ht.put("timestamp", timestamp);
		ht.put("nonceStr", nonceStr);
		//企业微信的corpID
		ht.put("AppID", SystemConfig.getWXGZH_Appid());

		//生成签名算法
		String str1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url1 + "";
		String Signature = bp.wf.difference.Glo.Sha1Signature(str1);
		ht.put("signature", Signature);

		return Json.ToJson(ht);
	}

	public final String WXGZH_AthUpload() throws Exception {

		// 多附件描述.
		String pkVal = this.GetRequestVal("PKVal");
		String attachPk = this.GetRequestVal("AttachPK");

		FrmAttachment athDesc = new FrmAttachment(attachPk);
		MapData mapData = new MapData(athDesc.getFrmID());
		String msg = "";
		//求出来实体记录，方便执行事件.
		GEEntity en = new GEEntity(athDesc.getFrmID());
		en.setPKVal(pkVal);
		if (en.RetrieveFromDBSources() == 0)
		{
			en.setPKVal(this.getFID());
			if (en.RetrieveFromDBSources() == 0)
			{
				en.setPKVal(this.getPWorkID());
				en.RetrieveFromDBSources();
			}
		}

		//求主键. 如果该表单挂接到流程上.
		if (this.getNodeID() != 0 && athDesc.getNoOfObj().contains("AthMDtl") == false)
		{
			//判断表单方案。
			FrmNode fn = new FrmNode(this.getNodeID(), this.getFrmID());
			if (fn.getFrmSln() == FrmSln.Self)
			{
				FrmAttachment myathDesc = new FrmAttachment();
				myathDesc.setMyPK(attachPk + "_" + this.getNodeID());
				if (myathDesc.RetrieveFromDBSources() != 0)
				{
					athDesc.setHisCtrlWay(myathDesc.getHisCtrlWay());
				}
			}
			pkVal = Dev2Interface.GetAthRefPKVal(this.getWorkID(), this.getPWorkID(), this.getFID(), this.getNodeID(), this.getFrmID(), athDesc);
		}
		String savePath = "";
		String fileName = DBAccess.GenerGUID(0, null, null);
		if (athDesc.getAthSaveWay() == AthSaveWay.IISServer)
		{
			savePath = athDesc.getSaveTo() + "/" + pkVal;
			if ((new File(savePath)).isDirectory() == false)
			{
				(new File(savePath)).mkdirs();
			}
			savePath = savePath + "/" + fileName + ".png";
		}
		if (athDesc.getAthSaveWay() == AthSaveWay.FTPServer || athDesc.getAthSaveWay() == AthSaveWay.OSS)
		{
			savePath = SystemConfig.getPathOfTemp() + fileName + ".tmp";
		}
		File file =  new File(savePath);
		CommonFileUtils.upload(getRequest(), "file", file);
		String ny = DataType.getCurrentDateByFormart("yyyy_MM");

		if (athDesc.getAthSaveWay() == AthSaveWay.FTPServer)
		{
			String workDir = ny + "/" + athDesc.getFrmID() + "/";
			FtpUtil ftpUtil = bp.wf.Glo.getFtpUtil();
			ftpUtil.changeWorkingDirectory(workDir,true);
			// 把文件放在FTP服务器上去.
			ftpUtil.uploadFile( fileName + ".png",savePath);
			ftpUtil.releaseConnection();
		}

		//文件上传到OSS服务器上
		if(athDesc.getAthSaveWay() == AthSaveWay.OSS)
		{
			String fName = ny + File.separator + athDesc.getFrmID() + File.separator + fileName + ".png";
			//调用OSS工具类上传
			OSSUploadFileUtils.uploadFile(fName, Files.newInputStream(new File(savePath).toPath()));
		}
		File info = new File(savePath);
		FrmAttachmentDB dbUpload = new FrmAttachmentDB();
		dbUpload.setMyPK(DBAccess.GenerGUID(0, null, null));
		dbUpload.setSort("");
		dbUpload.setNodeID(this.getNodeID());
		dbUpload.setFrmID(athDesc.getFrmID());
		dbUpload.setFKFrmAttachment(athDesc.getMyPK());
		dbUpload.setFID(this.getFID()); //流程id.

		dbUpload.setRefPKVal( pkVal.toString());
		dbUpload.setFrmID(athDesc.getFrmID());
		dbUpload.setFKFrmAttachment(athDesc.getMyPK());
		dbUpload.setFileName(fileName + ".png");
		dbUpload.setFileSize((float)info.length());
		dbUpload.setRDT(DataType.getCurrentDateTimess());
		dbUpload.setRec(WebUser.getNo());
		dbUpload.setRecName(WebUser.getName());
		dbUpload.setDeptNo(WebUser.getDeptNo());
		dbUpload.setDeptName(WebUser.getDeptName());
		dbUpload.setFileExts("png");
		if (athDesc.getAthSaveWay() == AthSaveWay.IISServer)
		{
			//文件方式保存
			dbUpload.setFileFullName(savePath);
		}

		if (athDesc.getAthSaveWay() == AthSaveWay.FTPServer)
		{
			dbUpload.setFileFullName(ny + "//" + athDesc.getFrmID() + "//" + fileName + ".png");
		}
		if (athDesc.getAthSaveWay() == AthSaveWay.OSS)
		{
			dbUpload.setFileFullName(ny + File.separator + athDesc.getFrmID() + File.separator + fileName + ".png");
		}
		dbUpload.Insert();

		return "上传成功";
	}
}
