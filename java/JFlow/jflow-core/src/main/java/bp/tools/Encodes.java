/**
 * Project Name:app-engine
 * File Name:Encodes.java
 * Package com.bwda.engine.common.utils
 * Date:2017年7月18日
 * Copyright (c) 2017, 江苏保旺达软件有限公司 All Rights Reserved.
 * @author fanchengliang
 *
*/
package bp.tools;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringEscapeUtils;
/**
 * 封装各种格式的编码解码工具类. 1.Commons-Codec的 hex/base64 编码 2.自制的base62 编码
 * 3.Commons-Lang的xml/html escape 4.JDK提供的URLEncoder
 * 
 * @author fanchengliang
 */
public class Encodes {

	/** 编码格式 */
	private static final String DEFAULT_URL_ENCODING = "UTF-8";
	/** BASE62字节数组 */
	private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

	/**
	 * Hex编码.
	 * 
	 * param input
	 *            输入
	 * @return 解码结果
	 */
	public static String encodeHex(byte[] input) {
		return new String(Hex.encodeHex(input));
	}

	/**
	 * Hex解码.
	 * 
	 * param input
	 *            输入
	 * @return 解码结果
	 * @throws DecoderException
	 */
	public static byte[] decodeHex(String input) throws DecoderException {
		return Hex.decodeHex(input.toCharArray());

	}

	/**
	 * Base64编码.
	 * 
	 * param input
	 *            输入
	 * @return 编码结果
	 */
	public static String encodeBase64(byte[] input) {
		try {
			return new String(Base64.encodeBase64(input), DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * Base64编码.
	 * 
	 * param input
	 *            输入
	 * @return 编码结果
	 */
	public static String encodeBase64(String input) {
		try {
			return new String(Base64.encodeBase64(input.getBytes(DEFAULT_URL_ENCODING)), DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * Base64解码.
	 * 
	 * param input
	 *            输入
	 * @return 解码结果
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] decodeBase64(String input) throws UnsupportedEncodingException {

		return Base64.decodeBase64(input.getBytes(DEFAULT_URL_ENCODING));

	}

	/**
	 * Base64解码.
	 * 
	 * param input
	 *            输入
	 * @return 解码结果
	 */
	public static String decodeBase64String(String input) {
		try {
			return new String(Base64.decodeBase64(input.getBytes(DEFAULT_URL_ENCODING)), DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * Base62编码。
	 * 
	 * param input
	 *            输入
	 * @return 编码结果
	 */
	public static String encodeBase62(byte[] input) {
		char[] chars = new char[input.length];
		for (int i = 0; i < input.length; i++) {
			chars[i] = BASE62[((input[i] & 0xFF) % BASE62.length)];
		}
		return new String(chars);
	}

	/**
	 * Html 转码.
	 * 
	 * param html
	 *            待转码字符串
	 * @return 转码结果
	 */
	public static String escapeHtml(String html) {
		return StringEscapeUtils.escapeHtml(html);
	}

	/**
	 * Html 解码.
	 * 
	 * param
	 *
	 * @return 解码结果
	 */
	public static String unescapeHtml(String htmlEscaped) {
		return StringEscapeUtils.unescapeHtml(htmlEscaped);
	}

	/**
	 * Xml 转码.
	 * 
	 * param xml
	 *            待转码字符串
	 * @return 转码结果
	 */
	public static String escapeXml(String xml) {
		return StringEscapeUtils.escapeXml(xml);
	}

	/**
	 * Xml 解码.
	 * 
	 * param xmlEscaped
	 *            待解码字符串
	 * @return 解码结果
	 */
	public static String unescapeXml(String xmlEscaped) {
		return StringEscapeUtils.unescapeXml(xmlEscaped);
	}

	/**
	 * URL 编码, Encode默认为UTF-8.
	 * 
	 * param part
	 *            字符串
	 * @return 编码结果
	 * @throws UnsupportedEncodingException
	 */
	public static String urlEncode(String part) throws UnsupportedEncodingException {

		return URLEncoder.encode(part, DEFAULT_URL_ENCODING);

	}

	/**
	 * URL 解码, Encode默认为UTF-8.
	 * 
	 * param part
	 *            字符串
	 * @return 解码结果
	 * @throws UnsupportedEncodingException
	 */
	public static String urlDecode(String part) throws UnsupportedEncodingException {

		return URLDecoder.decode(part, DEFAULT_URL_ENCODING);

	}
}
