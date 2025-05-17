package bp.port.dingtalk.ddsdk;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * 时间戳
 */
public class TimeStamp {
    /**
     * 将当前UTC时间转换为自1970年1月1日以来的秒数（Java中的时间戳通常是毫秒）
     *
     * @return
     */
    public static long Now() {
        return Instant.now().getEpochSecond();
    }

    /**
     * 将时间戳（秒）转换回日期时间（UTC）
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime ToDateTime(long timestamp) {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    /**
     * 将时间戳（秒）转换为默认的日期时间字符串
     *
     * @param timestamp
     * @return
     */
    public static String ToDateTimeString(long timestamp) {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime().toString();
    }

    /**
     * 将时间戳（秒）转换为指定格式的日期时间字符串
     *
     * @param timestamp
     * @param format
     * @return
     */
    public static String ToDateTimeString(long timestamp, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault());
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).format(formatter);
    }
}
