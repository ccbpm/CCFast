package bp.tools;

import com.jcraft.jsch.jce.MD5;
import org.springframework.util.DigestUtils;

import java.util.Calendar;

public class Rand {
    /**
     * 生成随机数字
     *
     * param length 生成长度
     * @return
     */
    public static String Number(int length) throws InterruptedException {
        return Str(length, false);
    }

    /**
     * 生成随机数字
     *
     * param Length 生成长度
     * param Sleep  是否要在生成前将当前线程阻止以避免重复
     * @return
     */
    public static String Number(int Length, boolean Sleep) throws Exception {
        if (Sleep) {
            Thread.sleep(2);
        }
        String result = "";
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < Length; i++) {
            result += String.valueOf(random.nextInt(10));
        }
        return result;
    }

    /**
     * 生成随机字母与数字
     *
     * param Length 生成长度
     * @return
     */
    public static String Str(int Length)throws Exception {
        return Str(Length, false);
    }

    /**
     * 生成随机字母与数字
     *
     * param Length 生成长度
     * param Sleep  是否要在生成前将当前线程阻止以避免重复
     * @return
     */
    public static String Str(int Length, boolean Sleep) throws InterruptedException {
        if (Sleep) {
            Thread.sleep(2);
        }
        char[] Pattern = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        String result = "";
        int n = Pattern.length;
        java.util.Random random = new java.util.Random(~(int) Calendar.getInstance().getTimeInMillis());
        for (int i = 0; i < Length; i++) {
            int rnd = random.nextInt(n);
            result += Pattern[rnd];
        }
        return result;
    }


    /**
     * 生成随机纯字母随机数
     *
     * param Length 生成长度
     * @return
     */
    public static String Str_char(int Length) throws Exception{
        return Str_char(Length, false);
    }

    /**
     * 生成随机纯字母随机数
     *
     * param Length 生成长度
     * param Sleep  是否要在生成前将当前线程阻止以避免重复
     * @return
     */
    public static String Str_char(int Length, boolean Sleep) throws Exception{
        if (Sleep) {
            Thread.sleep(2);
        }
        char[] Pattern = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        String result = "";
        int n = Pattern.length;
        java.util.Random random = new java.util.Random(~(int) Calendar.getInstance().getTimeInMillis());
        for (int i = 0; i < Length; i++) {
            int rnd = random.nextInt(n);
            result += Pattern[rnd];
        }
        return result;
    }

    /**
     * MD5 16位加密
     *
     * param ConvertString
     * @return
     */

    public static String GetMd5Str(String ConvertString) {
        MD5 md5 = new MD5();
        String t2 =DigestUtils.md5DigestAsHex(ConvertString.getBytes());
        t2 = t2.replace("-", "");
        return t2;
    }
}
