package bp.wf.httphandler;

import bp.difference.*;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

public class HttpHandlerGlo
{

		///#region 转化格式  chen
	public static void DownloadFile(String filepath, String tempName) throws IOException {

		ContextHolderUtils.ResponseWriteFile(filepath, tempName, "application/octet-stream");
	}

	/**
	 * 下载文件通过bytes
	 * @param response
	 * @param bytes
	 * @throws IOException
	 */
	public  static void DownLoadFileByBytes(HttpServletResponse response,byte[] bytes,String fileName) throws IOException {

		response.reset();
		response.setHeader("Content-Disposition", "attachment; filename=" + 	URLEncoder.encode(fileName, "UTF-8"));
		response.setHeader("Content-Length", "" + bytes.length);
		response.setContentType("application/octet-stream;charset=UTF-8");
		IOUtils.write(bytes, response.getOutputStream());
	}

	/** 
	 从别的网站服务器上下载文件
	 @param filepath
	 @param tempName
	*/
	public static void DownloadHttpFile(String filepath, String tempName) throws IOException {
		ArrayList<Byte> byteList = new ArrayList<Byte>();

		//打开网络连接
		String filePth = filepath.replace("\\", "/").trim();
		if (filepath.indexOf("/") == 0)
		{
			filepath = StringHelper.remove(filepath, 1, filepath.length() - 1);
		}
		if (!SystemConfig.getAttachWebSite().trim().endsWith("/"))
		{
			filepath = SystemConfig.getAttachWebSite().trim() + "/" + filepath;
		}
		else
		{
			filepath = SystemConfig.getAttachWebSite().trim() + filepath;
		}
		java.net.URL url = new java.net.URL(filepath);
		// 打开连接
		URLConnection con = url.openConnection();
		// 输入流
		InputStream is = con.getInputStream();
		// 1K的数据缓冲
		byte[] bs = new byte[1024];
		// 读取到的数据长度
		int len;
		//判断指定目录是否存在，不存在则先创建目录
		File file = new File(tempName);
		if (!file.exists())
		file.mkdirs();
		//fileName如果不包含文件后缀，则需要加上后缀，如：fileName + ".jpg";fileName + ".txt";
		FileOutputStream os = new FileOutputStream(tempName, false);//false：覆盖文件,true:在原有文件后追加
		// 开始读取
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		is.close();
		os.close();
	}
	public static void OpenWordDoc(String filepath, String tempName) throws IOException {
		ContextHolderUtils.ResponseWriteFile(filepath, tempName, "application/ms-msword");
	}
	public static void OpenWordDocV2(String filepath, String tempName) throws IOException {
		ContextHolderUtils.ResponseWriteFile(filepath, tempName, "application/octet-stream");
	}

}
