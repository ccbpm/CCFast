package bp.tools;

import bp.sys.*;
import bp.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import java.io.*;

/** 
 字符串加解密
*/
public final class SecurityDES
{
	//默认密钥向量

//ORIGINAL LINE: private static byte[] IV = { 0x65, 0x88, 0x35, 0x71, 0x60, 0x1B, 0x2D, 0x7F };
	private static byte[] IV = {0x65, (byte)0x88, 0x35, 0x71, 0x60, 0x1B, 0x2D, 0x7F};
	private static String key = "ligy@163";
	/** 
	 DES加密字符串
	 
	 @return 加密成功返回加密后的字符串，失败返回源串
	*/
	public static String Encrypt(String toEncryptString)
	{
		return "请先完成翻译";
//		try
//		{
//			if (toEncryptString == null || toEncryptString.equals("") || toEncryptString.equals(""))
//			{
//				return "";
//			}
//
//			byte[] rgbKey = key.substring(0, 8).getBytes(java.nio.charset.StandardCharsets.UTF_8);
//
//			byte[] rgbIV = IV;
//
//			byte[] inputByteArray = toEncryptString.getBytes(java.nio.charset.StandardCharsets.UTF_8);
//			DESCryptoServiceProvider dCSP = new DESCryptoServiceProvider();
//			ByteArrayOutputStream mStream = new ByteArrayOutputStream();
//			CryptoStream cStream = new CryptoStream(mStream, dCSP.CreateEncryptor(rgbKey, rgbIV), CryptoStreamMode.Write);
//			cStream.Write(inputByteArray, 0, inputByteArray.length);
//			cStream.FlushFinalBlock();
//			return Convert.ToBase64String(mStream.ToArray());
//		}
//		catch (java.lang.Exception e)
//		{
//			return toEncryptString;
//		}
	}
}