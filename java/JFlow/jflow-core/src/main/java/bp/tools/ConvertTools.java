package bp.tools;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConvertTools
{
	
	public static boolean StreamWriteConvert(String content, String fileName)
			throws Exception
	{
		boolean flag = false;
		FileOutputStream o = null;
		try
		{
			o = new FileOutputStream(fileName);
			o.write(content.getBytes("US-ASCII"));
			o.close();
			flag = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	public static boolean streamWriteConvertGBK(String content, String fileName)
			throws Exception
	{
		boolean flag = false;
		FileOutputStream o = null;
		try
		{
			o = new FileOutputStream(fileName);
			o.write(content.getBytes("GB2312"));
			o.close();
			flag = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	public static String StreamReaderToStringConvert(String file, String encode)
			throws IOException
	{
		StringBuilder val = new StringBuilder();
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, encode);
		BufferedReader br = new BufferedReader(isr);
		
		String lin = "";
		while ((lin = br.readLine()) != null)
		{
			if (lin == null || lin.trim().equals("") )
			{
				continue;
			}
			val.append(lin);
		}
		br.close();
		isr.close();
		fis.close();
		return val.toString();
	}
	
	public static String getPorjectPath()throws Exception
	{
		String nowpath;
		String tempdir;
		nowpath = System.getProperty("user.dir");
		tempdir = nowpath.replace("bin", "webapps"); // 把bin 文件夹变到 webapps文件里面
		tempdir += "/";
		return tempdir;
	}
	
	public static String getSysPath()throws Exception
	{
		String path = Thread.currentThread().getContextClassLoader()
				.getResource("").toString();
		String temp = path.replaceFirst("file:/", "").replaceFirst(
				"WEB-INF/classes/", "");
		String separator = System.getProperty("file.separator");
		String resultPath = temp.replaceAll("/", separator + separator);
		return resultPath;
	}
	
	/**
	 * 转换 exception getStackTrace 为字符串
	 * 
	 * param elements
	 * @return
	 */
	public static String getStackTraceString(StackTraceElement[] elements)
	{
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < elements.length; i++)
		{
			StackTraceElement stackTraceElement = elements[i];
			builder.append(stackTraceElement.toString());
		}
		return builder.toString();
	}
	
	/**
	 * 补全
	 * 
	 * param oriStr
	 *            当前数据
	 * param len
	 *            小于位数
	 * param alexin
	 *            补全数
	 * @return
	 */
	public static String padLeft(String oriStr, int len, String alexin)
	{
		StringBuilder str = new StringBuilder();
		int strlen = oriStr.length();
		if (strlen < len)
		{
			for (int i = 0; i < len - strlen; i++)
			{
				str.append(alexin);
			}
		}
		str.append(oriStr + str.toString());
		return str.toString();
	}
}
