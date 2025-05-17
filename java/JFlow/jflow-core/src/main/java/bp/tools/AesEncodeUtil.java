package bp.tools;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 文件AES加密
 *
 * @author Administrator
 * @date 2018年4月28日
 */
public class AesEncodeUtil {
    private static Logger logger = LoggerFactory.getLogger(En3Des.class);
    
    // 初始向量
    public static final String VIPARA = "6#dPz>3F6#dPz>3F"; // AES 为16bytes. DES

    // 编码方式
    public static final String bm = "UTF-8";

    // 私钥
    private static final String ASE_KEY = "6#dPz>3F6#dPz>3F"; // AES固定格式为128/192/256
    private static final String ALGORITHM = "AES";

    //属性文件值开头标志（证明已加密）
    private static final String envTag="#CC#";
    /**
     * 加密
     * 
     * param
     * @return
     */
    public static byte[] encryptAES(byte[] input, int inputOffset, int inputLength) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            SecretKeySpec key = new SecretKeySpec(ASE_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(input, inputOffset, inputLength);
            return encryptedData;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static byte[] decryptAES(byte[] input, int inputOffset, int inputLength) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            SecretKeySpec key = new SecretKeySpec(ASE_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte[] decryptedData = cipher.doFinal(input, inputOffset, inputLength);
            return decryptedData;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static String encryptAESHex(String cleartext) {
        byte[] bEnc = null;
        try {
            bEnc = encryptAES(cleartext.getBytes(bm), 0, cleartext.length());
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return bEnc == null ? "" : bytesToHexString(bEnc);
    }

    public static String decryptAESHex(String encrypted) {
        try {
            byte[] byteMi = En3Des.hexStringToBytes(encrypted);
            byte[] bDec = decryptAES(byteMi, 0, byteMi.length);
            return bDec == null ? "" : new String(bDec, bm);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "";
        }
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 
     * param strSrcFile 原始文件
     * param strDestFile 加密后的文件
     * @throws Exception
     */
    // 加密文件
    public static  void encryptFile(String strSrcFile, String strDestFile) throws Exception {
        try {       	
            File file = new File(strSrcFile);
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            FileOutputStream fos = new FileOutputStream(strDestFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int c = 0;
            int length = 4 * 1024;
            byte[] bytes = new byte[length];
            while ((c = bis.read(bytes)) != -1) {
                byte bEnc[] = encryptAES(bytes, 0, c);
                bos.write(bEnc, 0, bEnc.length);
            }
            bos.flush();
            bos.close();
            fis.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    // 解密文件
    public static void decryptFile(String strSrcFile, String strDestFile) {
        try {
            File file = new File(strSrcFile);
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            FileOutputStream fos = new FileOutputStream(strDestFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            int c = 0;
            int length = 4 * 1024 + 16;
            byte[] bytes = new byte[length];
            while ((c = bis.read(bytes)) != -1) {
                byte bDec[] = decryptAES(bytes, 0, c);
                bos.write(bDec, 0, bDec.length);
            }
            bos.flush();
            bos.close();
            fis.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    //属性文件value解密处理
   public static  String  decEnvValue(String v){
    if(v.toString().startsWith(envTag)){
        v= AesEncodeUtil.decryptAESHex(v.toString().replace(envTag,""));
    }
    return v;
}
    /**
     * 测试
     * 
     * param args
     * @throws Exception
     */
    public static void main1(String[] args) throws Exception {
//        String strIn = "1234567812345678";
//        byte[] bEnc = encryptAES(strIn.getBytes(), 0, strIn.length());
//        System.out.println(strIn.length());
//        System.out.println(bEnc.length);
//        String strEn = encryptAESHex("123456");
//
//        System.out.println(strEn);
//
//        System.out.println(decryptAESHex(strEn));

       // encryptFile("D:\\终端准入版本说明书.docx", "D:\\终端准入版本说明书1.docx");

       // decryptFile("I:\\apache-tomcat-7.0.6\\webapps\\jflow-web\\DataUser\\UploadFile\\ND22501\\450\\0fe8d1c553294df39142feb685e58633.URL.txt", "I:\\apache-tomcat-7.0.6\\webapps\\jflow-web\\DataUser\\UploadFile\\ND22501\\450\\0fe8d1c553294df39142feb685e58633.URL.txt.tmp");
    }
    public static String encryptAES(String data,String key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decryptAES(String encryptedData,String key) throws Exception {
        encryptedData=encryptedData.replace(" ","+");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(original);
    }
    public static void main(String[] args) {
        try{
            t2();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void t2() throws Exception {
        String key="lCCqkL1BfSwt2M@5";
        System.out.println("秘钥："+key);
        String pwd="123";
        String estr= encryptAES(pwd,key);
        System.out.println("加密后："+estr);
        String dstr= decryptAES(estr,key);
        System.out.println("解密后："+dstr);
    }
    public static void t1() throws Exception {
        String a="123";
        System.out.println("加密前："+a);
        String ea=encryptAESHex(a);
        System.out.println("加密后："+ea);
        String da=decryptAESHex(ea);
        System.out.println("解密后："+da);
    }
}