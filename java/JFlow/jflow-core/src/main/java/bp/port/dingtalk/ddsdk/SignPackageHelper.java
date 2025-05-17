package bp.port.dingtalk.ddsdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.time.*;

public class SignPackageHelper {
    ///#region Sha1Hex
    public static String Sha1Hex(String value) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-1");
            byte[] data = algorithm.digest(value.getBytes("UTF-8"));
            StringBuilder sh1 = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                sh1.append(String.format("%02X", data[i]));
            }
            return sh1.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found.");
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported.");
        }
    }
    ///#endregion

    ///#region CreateNonceStr

    /**
     * 创建随机字符串
     *
     * @return
     */
    public static String CreateNonceStr() {
        int length = 16;
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String str = "";
        Random rad = new Random();
        for (int i = 0; i < length; i++) {
            str += chars.substring(rad.nextInt(chars.length() - 1), 1);
        }
        return str;
    }
    ///#endregion

    ///#region ConvertToUnixTimeStamp

    /**
     * 将DateTime时间格式转换为Unix时间戳格式
     *
     * @param time 时间
     * @return double
     */
    public static String ConvertToUnixTimeStamp(LocalDateTime time) {
//		TimeSpan ts = LocalDateTime.UtcNow - LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0);
//		return FloatingPointToInteger.ToInt64(ts.TotalSeconds).toString();
        // 将LocalDateTime转换为Instant，然后获取Unix时间戳
        return String.valueOf(time.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
    }
    ///#endregion
}
