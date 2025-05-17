package bp.tools;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 支持HMAC-SHA1消息签名 及 DES/AES对称加密的工具类.
 * 
 * 支持Hex与Base64两种编码方式.
 * 
 * @author fanchengliang
 */
public class Cryptos {

	/** 加密格式 AES */
	private static final String AES = "AES";

	/** 加密格式 AES_CBC */
	private static final String AES_CBC = "AES/CBC/PKCS5Padding";

	/** 加密格式 HMACSHA1 */
	private static final String HMACSHA1 = "HmacSHA1";

	/** 默认编码格式 */
	private static final String DEFAULT_URL_ENCODING = "UTF-8";

	/** 默认HMACSHA1密钥大小 */
	private static final int DEFAULT_HMACSHA1_KEYSIZE = 160;

	/** 默认AES密钥大小 */
	private static final int DEFAULT_AES_KEYSIZE = 128;

	/** 默认向量大小 */
	private static final int DEFAULT_IVSIZE = 16;

	/** 默认键值 */
	private static final byte[] DEFAULT_KEY = new byte[] { -97, 88, -94, 9, 70, -76, 126, 25, 0, 3, -20, 113, 108, 28,
			69, 125 };

	/** 随机数生成器 */
	private static SecureRandom random = new SecureRandom();

	/**
	 * 使用HMAC-SHA1进行消息签名, 返回字节数组,长度为20字节.
	 * 
	 * param input
	 *            原始输入字符数组
	 * param key
	 *            HMAC-SHA1密钥
	 */
	public static byte[] hmacSha1(byte[] input, byte[] key) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, HMACSHA1);
			Mac mac = Mac.getInstance(HMACSHA1);
			mac.init(secretKey);
			return mac.doFinal(input);
		} catch (GeneralSecurityException e) {
			e.getMessage();
		}
		return key;
	}

	/**
	 * 校验HMAC-SHA1签名是否正确.
	 * 
	 * param expected
	 *            已存在的签名
	 * param input
	 *            原始输入字符串
	 * param key
	 *            密钥
	 */
	public static boolean isMacValid(byte[] expected, byte[] input, byte[] key) {
		byte[] actual = hmacSha1(input, key);
		return Arrays.equals(expected, actual);
	}

	/**
	 * 生成HMAC-SHA1密钥,返回字节数组,长度为160位(20字节). HMAC-SHA1算法对密钥无特殊要求,
	 * RFC2401建议最少长度为160位(20字节).
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] generateHmacSha1Key() throws NoSuchAlgorithmException {

		KeyGenerator keyGenerator = KeyGenerator.getInstance(HMACSHA1);
		keyGenerator.init(DEFAULT_HMACSHA1_KEYSIZE);
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey.getEncoded();

	}

	/**
	 * 使用AES加密原始字符串.
	 * 
	 * param input
	 *            原始输入字符数组
	 * @throws Exception
	 */
	public static String aesEncrypt(String input) throws Exception {
		try {
			return Encodes.encodeHex(aesEncrypt(input.getBytes(DEFAULT_URL_ENCODING), DEFAULT_KEY));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 使用AES加密原始字符串.
	 * 
	 * param input
	 *            原始输入字符数组
	 * param key
	 *            符合AES要求的密钥
	 * @throws Exception
	 */
	public static String aesEncrypt(String input, String key) throws Exception {
		try {
			return Encodes.encodeHex(aesEncrypt(input.getBytes(DEFAULT_URL_ENCODING), Encodes.decodeHex(key)));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 使用AES加密原始字符串.
	 * 
	 * param input
	 *            原始输入字符数组
	 * param key
	 *            符合AES要求的密钥
	 * @throws Exception
	 */
	public static byte[] aesEncrypt(byte[] input, byte[] key) throws Exception {
		return aes(input, key, Cipher.ENCRYPT_MODE);
	}

	/**
	 * 使用AES加密原始字符串.
	 * 
	 * param input
	 *            原始输入字符数组
	 * param key
	 *            符合AES要求的密钥
	 * param iv
	 *            初始向量
	 * @throws Exception
	 */
	public static byte[] aesEncrypt(byte[] input, byte[] key, byte[] iv) throws Exception {
		return aes(input, key, iv, Cipher.ENCRYPT_MODE);
	}

	/**
	 * 使用AES解密字符串, 返回原始字符串.
	 * 
	 * param input
	 *            Hex编码的加密字符串
	 * @throws Exception
	 */
	public static String aesDecrypt(String input) throws Exception {
		 
		return new String(aesDecrypt(Encodes.decodeHex(input), DEFAULT_KEY),
					DEFAULT_URL_ENCODING);
		 
	}

	/**
	 * 使用AES解密字符串, 返回原始字符串.
	 * 
	 * param input
	 *            Hex编码的加密字符串
	 * param key
	 *            符合AES要求的密钥
	 * @throws Exception
	 */
	public static String aesDecrypt(String input, String key) throws Exception {
		try {
			return new String(aesDecrypt(Encodes.decodeHex(input), Encodes.decodeHex(key)), DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 使用AES解密字符串, 返回原始字符串.
	 * 
	 * param input
	 *            Hex编码的加密字符串
	 * param key
	 *            符合AES要求的密钥
	 * @throws Exception
	 */
	public static byte[] aesDecrypt(byte[] input, byte[] key) throws Exception {
		return aes(input, key, Cipher.DECRYPT_MODE);
	}

	/**
	 * 使用AES解密字符串, 返回原始字符串.
	 * 
	 * param input
	 *            Hex编码的加密字符串
	 * param key
	 *            符合AES要求的密钥
	 * param iv
	 *            初始向量
	 * @throws Exception
	 */
	public static byte[] aesDecrypt(byte[] input, byte[] key, byte[] iv) throws Exception {
		return aes(input, key, iv, Cipher.DECRYPT_MODE);
	}

	/**
	 * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
	 * 
	 * param input
	 *            原始字节数组
	 * param key
	 *            符合AES要求的密钥
	 * param mode
	 *            Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
	 */
	private static byte[] aes(byte[] input, byte[] key, int mode) throws Exception {

		SecretKey secretKey = new SecretKeySpec(key, AES);
		Cipher cipher = Cipher.getInstance(AES);
		cipher.init(mode, secretKey);
		return cipher.doFinal(input);

	}

	/**
	 * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
	 * 
	 * param input
	 *            原始字节数组
	 * param key
	 *            符合AES要求的密钥
	 * param iv
	 *            初始向量
	 * param mode
	 *            Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 */
	private static byte[] aes(byte[] input, byte[] key, byte[] iv, int mode) throws Exception {

		SecretKey secretKey = new SecretKeySpec(key, AES);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance(AES_CBC);
		cipher.init(mode, secretKey, ivSpec);
		return cipher.doFinal(input);

	}

	/**
	 * 生成AES密钥
	 * 
	 * @return AES密钥
	 * @throws NoSuchAlgorithmException
	 */
	public static String generateAesKeyString() throws NoSuchAlgorithmException {
		return Encodes.encodeHex(generateAesKey(DEFAULT_AES_KEYSIZE));
	}

	/**
	 * 生成AES密钥,返回字节数组, 默认长度为128位(16字节).
	 * 
	 * @return AES密钥
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] generateAesKey() throws NoSuchAlgorithmException {
		return generateAesKey(DEFAULT_AES_KEYSIZE);
	}

	/**
	 * 生成AES密钥,可选长度为128,192,256位.
	 * 
	 * param keysize
	 *            密钥长度
	 * @return AES密钥
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] generateAesKey(int keysize) throws NoSuchAlgorithmException {

		KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
		keyGenerator.init(keysize);
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey.getEncoded();

	}

	/**
	 * 生成随机向量,默认大小为cipher.getBlockSize(), 16字节.
	 * 
	 * @return 随机向量
	 */
	public static byte[] generateIV() {
		byte[] bytes = new byte[DEFAULT_IVSIZE];
		random.nextBytes(bytes);
		return bytes;
	}
	
	 public static void main(String[] args) {
		 try {
			System.out.println(Cryptos.aesDecrypt("b9eb8b5b8f8258d5565396c52aaec729"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
