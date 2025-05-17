package bp.tools;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptUtil {
    //使用AES-128-CBC加密模式，key需要为16位,key和iv可以相同！
    private static String KEY = "1234567890123456";
    private static String IV = "1234567890123456";
    /**
     * 加密方法
     * @param data 要加密的数据
     * @param key 加密key
     * @param iv  加密iv
     * @return 加密的结果
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            //"算法/模式/补码方式"NoPadding PkcsPadding
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return new Base64().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String data, String key, String iv) throws Exception
    {
        try {
            byte[] encrypted1 = new Base64().decode(data);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);return originalString;
        } catch (Exception e) {
            e.printStackTrace();return null;
        }
    }
    /**
     * 使用默认的key和iv加密
     */
    public static String encrypt(String data) throws Exception
    {
        return encrypt(data, KEY, IV);
    }
    /** * 使用默认的key和iv解密 * @param data * @return * @throws Exception */
    public static String desEncrypt(String data) throws Exception
    {
        return desEncrypt(data, KEY, IV);
    }




}
