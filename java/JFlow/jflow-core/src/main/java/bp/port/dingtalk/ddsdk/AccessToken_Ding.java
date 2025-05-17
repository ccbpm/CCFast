package bp.port.dingtalk.ddsdk;

import java.time.*;

/**
 * 访问票据
 */
public class AccessToken_Ding {
    /**
     * 票据的值
     */
    private static String Value;

    public static String getValue() {
        return Value;
    }

    public static void setValue(String value) {
        Value = value;
    }

    /**
     * 票据的开始时间
     */
    private static LocalDateTime Begin = LocalDateTime.MIN;

    public static LocalDateTime getBegin() {
        return Begin;
    }

    public static void setBegin(LocalDateTime value) {
        Begin = value;
    }
}
