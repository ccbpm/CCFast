package bp.da;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.server.ExportException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Pattern;

import bp.difference.SystemConfig;
import bp.tools.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import net.sourceforge.jtds.jdbc.DateTime;
import org.apache.commons.io.FileUtils;
import bp.wf.Glo;
import bp.web.*;
import org.springframework.core.io.ClassPathResource;

public class DataType {


    /**
     获得两个日期之间的天数

     @param dtoffrom
     @param dtofto
     @return
     */
    public static float GeTimeLimits(String dtoffrom, String dtofto)
    {
        if (dtoffrom == null || dtoffrom.length() <= 7)
        {
            return 0;
        }

        if (dtofto == null || dtofto.length() <= 7)
        {
            return 0;
        }

        Date dtfrom = DataType.ParseSysDate2DateTime(dtoffrom);
        Date dtto = DataType.ParseSysDate2DateTime(dtofto);
        long diff = dtto.getTime() - dtfrom.getTime();
        float day = (float)diff / (1000 * 60 * 60 * 24);
        day = Float.parseFloat(String.format("%.2f", day));
        return day;
    }
    /**
     获得两个时间的参数

     @param dtoffrom 时间从
     @return
     */
    public static float GeTimeLimits(String dtoffrom)
    {
        return GeTimeLimits(dtoffrom, DataType.getCurrentDateTime());
    }
    public static long GetSpanMinute(String fromdatetim, String toDateTime)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime1 = LocalDateTime.parse(fromdatetim, formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(toDateTime, formatter);

        Duration duration = Duration.between(dateTime1, dateTime2);
        return duration.toMinutes();
    }


    public static boolean IsNullOrEmpty(Object object) {
        return object == null || object.equals("") || object.equals("null");
    }


    public static final String RegEx_replace_FirstXZ = "^(_|[0-9])+";
    public static final String RegEx_replace_OnlySZX = "[\\u4e00-\\u9fa5]|[^0-9a-zA-Z_]";
    public static final String RegEx_replace_OnlyHSZX = "[^0-9a-zA-Z_\\u4e00-\\u9fa5]";

    // / <summary>
    // / 获取两个时间之间的字符串表示形式，如：1天2时34分
    // / <para>added by liuxc,2014-12-4</para>
    // / </summary>
    // / <param name="t1">开始时间</param>
    // / <param name="t2">结束时间</param>
    // / <returns>返回：x天x时x分</returns>
    public static String GetSpanTime(Date t1, Date t2) {
        // 毫秒ms
        long diff = t2.getTime() - t1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        // System.out.print("两个时间相差：");
        // System.out.print(diffDays + " 天, ");
        // System.out.print(diffHours + " 小时, ");
        // System.out.print(diffMinutes + " 分钟, ");
        // System.out.print(diffSeconds + " 秒.");

        String spanStr = "";
        if (diffDays > 0)
            spanStr += diffDays + "天";

        if (diffHours > 0)
            spanStr += diffHours + "时";

        if (diffMinutes > 0)
            spanStr += diffMinutes + "分";

        if (spanStr.length() == 0)
            spanStr = "0分";

        return spanStr;
    }

    /**
     * 比较两个字符串是否有交集
     * <p>
     * param ids1
     * param ids2
     *
     * @return
     */
    public static boolean IsHaveIt(String ids1, String ids2) {
        if (DataType.IsNullOrEmpty(ids1) == true) {
            return false;
        }
        if (DataType.IsNullOrEmpty(ids2) == true) {
            return false;
        }

        String[] str1s = ids1.split("[,]", -1);
        String[] str2s = ids2.split("[,]", -1);

        for (String str1 : str1s) {
            if (str1.equals("") || str1 == null) {
                continue;
            }

            for (String str2 : str2s) {
                if (str2.equals("") || str2 == null) {
                    continue;
                }

                if (str2.equals(str1) == true) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getDatePoor(Date endDate, Date nowDate) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        long sec = diff % nd % nh % nm / ns;
        String spanStr = "";
        if (day > 0)
            spanStr += day + "天";

        if (hour > 0)
            spanStr += hour + "时";

        if (min > 0)
            spanStr += min + "分";
        if (sec > 0)
            spanStr += sec + "秒";
        if (spanStr.length() == 0)
            spanStr = "0分";

        return spanStr;
    }

    public static Date WeekOfMonday(java.util.Date dt) {
        Calendar monday = Calendar.getInstance();
        monday.setTime(dt);
        int FIRST_DAY_OF_WEEK = Calendar.MONDAY;
        monday.setFirstDayOfWeek(FIRST_DAY_OF_WEEK);
        monday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return monday.getTime();
    }

    public static Date WeekOfSunday(java.util.Date dt) {
        Calendar cal = Calendar.getInstance();
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        cal.add(Calendar.DATE, 6);
        return cal.getTime();
    }

    /**
     * 去掉周六日
     * <p>
     * param dt
     * param days
     *
     * @return
     */
    public static Date AddDays(java.util.Date dt, int days) {
        if (dt == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int mod = days % 5;
        int other = days / 5 * 7;
        for (int i = 0; i < mod; ) {
            cal.add(Calendar.DATE, 1);
            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                case Calendar.SATURDAY:
                    break;
                default:
                    i++;
                    break;
            }
        }
        if (other > 0)
            cal.add(Calendar.DATE, other);
        return cal.getTime();
    }

    /**
     * 增加日期去掉周末节假日
     * <p>
     * param dt
     * 日期
     * param days
     * 增加的天数
     * param tway
     *
     * @return
     */
    public static Date AddDays(String dt, int days, TWay tway) {
        return AddDays(DataType.ParseSysDate2DateTime(dt), days, tway);
    }

    public static Date AddDays(java.util.Date dt, int days, TWay tway) {
        if (dt == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        if (tway == TWay.AllDays) {
            cal.add(Calendar.DAY_OF_MONTH, days);
            return cal.getTime();
        }
        int mod = days % 5;
        int other = days / 5 * 7;
        for (int i = 0; i < mod; ) {
            cal.add(Calendar.DATE, 1);
            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                case Calendar.SATURDAY:
                    break;
                default:
                    i++;
                    break;
            }
        }
        if (other > 0)
            cal.add(Calendar.DATE, other);
        return cal.getTime();
    }

    public static Date AddDays(String sysdt, int days) {
        Date dt = DataType.ParseSysDate2DateTime(sysdt);
        return AddDays(dt, days);
    }

    /**
     * 取指定日期是一年中的第几周
     * <p>
     * param dtime
     * 给定的日期
     *
     * @return 数字 一年中的第几周
     */
    public static int WeekOfYear(java.util.Date dtime) {
        Calendar c = Calendar.getInstance();
        c.setTime(dtime);
        int week_of_year = c.get(Calendar.WEEK_OF_YEAR);
        return week_of_year - 1;

    }

    public static String TurnToJiDuByDataTime(String dt) {
        if (dt.length() <= 6) {
            throw new RuntimeException("@要转化季度的日期格式不正确:" + dt);
        }
        String yf = dt.substring(5, 7);

        // String member and was converted to Java 'if-else' logic:
        // switch (yf)
        if (yf.equals("01") || yf.equals("02") || yf.equals("03")) {
            return dt.substring(0, 4) + "-03";
        } else if (yf.equals("04") || yf.equals("05") || yf.equals("06")) {
            return dt.substring(0, 4) + "-06";
        } else if (yf.equals("07") || yf.equals("08") || yf.equals("09")) {
            return dt.substring(0, 4) + "-09";
        } else if (yf.equals("10") || yf.equals("11") || yf.equals("12")) {
            return dt.substring(0, 4) + "-12";
        } else {
        }
        return null;
    }

    // Datatable转换为Json

    /**
     * Datatable转换为Json
     * <p>
     * param dt
     * Datatable对象
     *
     * @return Json字符串
     */
    public static String ToJson(DataTable dt) {
        StringBuilder jsonString = new StringBuilder();
        jsonString.append("[");
        DataRowCollection drc = dt.Rows;
        for (int i = 0; i < drc.size(); i++) {
            jsonString.append("{");
            for (int j = 0; j < dt.Columns.size(); j++) {
                String strKey = dt.Columns.get(j).ColumnName;
                // *小周鹏修改-2014/11/11----------------------------START*
                // BillNoFormat对应value:{YYYY}-{MM}-{dd}-{LSH4} Format时会产生异常。
                if (strKey.equals("BillNoFormat")) {
                    continue;
                }
                // *小周鹏修改-2014/11/11----------------------------END*
                String strValue = drc.get(i).getValue(j).toString();
                // java.lang.Class type = dt.Columns.get(j).DataType;
                jsonString.append("\"" + strKey + "\":");

                // strValue = String.format(strValue, type);
                if (j < dt.Columns.size() - 1) {
                    jsonString.append("\"" + strValue + "\",");
                } else {
                    jsonString.append("\"" + strValue + "\"");
                }
            }
            jsonString.append("},");
        }
        jsonString.deleteCharAt(jsonString.length() - 1);
        jsonString.append("]");
        return jsonString.toString();
    }

    /**
     * DataTable转换为Json
     *
     * @throws Exception
     */
    public static String ToJson(DataTable dt, String jsonName) throws Exception {
        String jsonString = "";
        if (DataType.IsNullOrEmpty(jsonName)) {
            jsonName = dt.TableName;
        }
        jsonString += "{\"" + jsonName + "\":" + Json.ToJson(dt) + "}";
        return jsonString;

    }

    /**
     * 转化成Json. 成为 key value 模式.
     * <p>
     * param ht
     * 要转换的ht
     *
     * @return key value 模式的json
     * @throws Exception
     */
    public static String ToJsonEntityModel(Hashtable ht) throws Exception {
        return bp.tools.Json.ToJsonEntityModel(ht);
    }

    /**
     * 转换成MB
     * <p>
     * param val
     *
     * @return
     */
    public static float PraseToMB(long val) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            return Float.parseFloat(df.format(val / (float)1048576));
        } catch (java.lang.Exception e) {
            return 0;
        }
    }

    /**
     * 转换成KB
     * <p>
     * param val
     *
     * @return
     */
    public static float PraseToKB(long val) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            return Float.parseFloat(df.format(val / 1024f));
        } catch (java.lang.Exception e) {
            return 0;
        }
    }

    /**
     * 处理文件名称
     * <p>
     * param fileNameFormat
     * 文件格式
     *
     * @return返回合法的文件名
     */
    public static String PraseStringToFileName(String fileNameFormat) {
        char[] strs = "+#?*\"<>/;,-:%~".toCharArray();
        for (char c : strs)
            fileNameFormat = fileNameFormat.replace(String.valueOf(c), "_");

        strs = "：，。；？".toCharArray();
        for (char c : strs)
            fileNameFormat = fileNameFormat.replace(String.valueOf(c), "_");

        // 去掉空格.
        while (fileNameFormat.contains(" ") == true)
            fileNameFormat = fileNameFormat.replace(" ", "");

        // 替换特殊字符.
        fileNameFormat = fileNameFormat.replace("\t\n", "");

        // 处理合法的文件名.
        StringBuilder rBuilder = new StringBuilder(fileNameFormat);
        /*
         * 暂时不做替换 for (char rInvalidChar :Path.GetInvalidFileNameChars())
         * String.valueOf(rBuilder).replace(String.valueOf(rInvalidChar),"");
         */
        fileNameFormat = String.valueOf(rBuilder);

        fileNameFormat = fileNameFormat.replace("__", "_");
        fileNameFormat = fileNameFormat.replace("__", "_");
        fileNameFormat = fileNameFormat.replace("__", "_");
        fileNameFormat = fileNameFormat.replace("__", "_");
        fileNameFormat = fileNameFormat.replace("__", "_");
        fileNameFormat = fileNameFormat.replace("__", "_");
        fileNameFormat = fileNameFormat.replace("__", "_");
        fileNameFormat = fileNameFormat.replace("__", "_");
        fileNameFormat = fileNameFormat.replace(" ", "");
        fileNameFormat = fileNameFormat.replace(" ", "");
        fileNameFormat = fileNameFormat.replace(" ", "");
        fileNameFormat = fileNameFormat.replace(" ", "");
        fileNameFormat = fileNameFormat.replace(" ", "");
        fileNameFormat = fileNameFormat.replace(" ", "");
        fileNameFormat = fileNameFormat.replace(" ", "");
        fileNameFormat = fileNameFormat.replace(" ", "");

        if (fileNameFormat.length() > 240)
            fileNameFormat = fileNameFormat.substring(0, 240);

        return fileNameFormat;
    }

    /**
     * param strs
     * param isNumber
     *
     * @return
     */
    public static String PraseAtToInSql(String strs, boolean isNumber) {
        if (IsNullOrEmpty(strs) == true)
            return "''";

        strs = strs.replace("@", "','");
        strs = strs + "'";
        if (strs.length() > 2)
            strs = strs.substring(2);
        if (isNumber) {
            strs = strs.replace("'", "");
        }
        return strs;
    }

    /**
     * 把内容里面的东西处理成超连接。
     * <p>
     * param doc
     *
     * @return
     */
    public static String DealSuperLink(String doc) {
        if (doc == null) {
            return null;
        }
        return doc;
    }

    /**
     * 将文件转化为二进制
     * <p>
     * param fileName
     *
     * @return
     */
    public static byte[] ConvertFileToByte(String fileName) {
        File fs = new File(fileName);
        long fileSize = fs.length();
        FileInputStream fi = null;
        byte[] nowByte = new byte[(int) fileSize];
        try {
            int offset = 0;
            int numRead = 0;
            while (offset < nowByte.length && (numRead = fi.read(nowByte, offset, nowByte.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset != nowByte.length) {
                throw new IOException("Could not completely read file " + fs.getName());
            }
            fi.close();
            fi = new FileInputStream(fs);
        } catch (Exception e) {
            Log.DebugWriteError("DataType ConvertFileToByte()" + e);
        }

        return nowByte;
    }

    /**
     * 将绝对路径的文件转byte
     * @param fileFullPath
     * @return
     * @throws IOException
     */
    public static byte[] ConvertByteByFileFullPath(String fileFullPath) throws Exception {
      try{
          File fileInfo = new File(fileFullPath);
          FileInputStream inputStream  =  new FileInputStream(fileInfo);
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
          byte[] buffer = new byte[1024];
          int length;
          while ((length = inputStream.read(buffer)) > 0) {
              outputStream.write(buffer, 0, length);
          }
          inputStream.close();
          outputStream.close();
          return outputStream.toByteArray();
      }catch (Exception ex){
          throw  new Exception(ex.getMessage());
      }
    }

    /**
     *     //将Byte数组转换成文件
     * @param bytes
     * @param fileFullPath
     */
    public static void getFileByBytes(byte[] bytes,String fileFullPath) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {

            file = new File(fileFullPath);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            Log.DebugWriteError(e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    Log.DebugWriteError(e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.DebugWriteError(e);
                }
            }
        }
    }

    /**
     * 写文件
     * <p>
     * param file
     * 路径
     * param Doc
     * 内容
     */
    public static void WriteFile(String file, String Doc) {
        File fl = new File(file);
        if (fl.exists()) {
            fl.delete();
        }
        try {
            fl.createNewFile();
            FileUtils.write(fl, Doc.trim(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取URL内容
     * <p>
     * param url
     * 要读取的url
     * param timeOut
     * 超时时间
     * param timeOut
     * text code.
     *
     * @return 返回读取内容
     */
    public static String ReadURLContext(String url, int timeOut) {
        String doc = "";
        try {
            // xiaozhoupeng 20150106 update Start
            if (!url.contains("http")) {
                String temp_url = url.substring(1, url.length()).replace(".aspx", ".jsp");
                url = Glo.getCCFlowAppPath() + temp_url;
            }
            if(url.indexOf("?")!=-1){
                String[] strs = url.split("[?]");
                String urlExt = strs[0]+"?1=1";
                String para = strs[1];
                String[] paras = para.split("&");
                for(String str: paras){
                    String[] item = str.split("=");
                    if(item.length==1 || DataType.IsNullOrEmpty(item[1])==true)
                        urlExt+="&"+item[0]+"=";
                    else
                        urlExt+="&"+item[0]+"="+ URLEncoder.encode(item[1], "UTF-8");
                }
                url = urlExt;
            }
            // xiaozhoupeng 20150106 update End
            URL ul = new URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) ul.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setReadTimeout(timeOut);
            java.io.BufferedReader in = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                doc += line;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static String ReadURLContextByGET(String url, int timeOut) {
        String doc = "";
        try {
            // xiaozhoupeng 20150106 update Start
            if (!url.contains("http")) {
                String temp_url = url.substring(1, url.length()).replace(".aspx", ".jsp");
                url = Glo.getCCFlowAppPath() + temp_url;
            }
            // xiaozhoupeng 20150106 update End
            URL ul = new URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) ul.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setReadTimeout(timeOut);
            java.io.BufferedReader in = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                doc += line;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }
    /**
     * 读取文件
     * <p>
     * param filePath
     * 路径
     *
     * @return 内容
     * @throws IOException
     */
    public static String ReadTextFile(String filePath) {
        return ReadTextFile(filePath, "UTF-8");
    }

    /**
     * 读取文件
     * <p>
     * param filePath
     * 路径
     *
     * @return 内容
     * @throws IOException
     */
    public static String ReadTextFile(String filePath, String codeType) {
        //需要先判断文件是否是jar包中的文件

        BufferedReader in = null;
        Reader is = null;
        String line = "";
        String path = filePath.replace("\\","/");
        boolean isRunJar = path.indexOf("DataUser/") != -1 && (path.indexOf("DataUser/Siganture/") != -1 || path.indexOf("DataUser/UploadFile/") != -1 ||
                path.indexOf("DataUser/FlowDesc/") != -1 || path.indexOf("DataUser/Temp/") != -1
                || path.indexOf("DataUser/JSLibData/") != -1 || path.indexOf("DataUser/InstancePacketOfData/") != -1) ? false : true;
        try {
            if (SystemConfig.isJarRun() && isRunJar) {
                ClassPathResource classPathResource = new ClassPathResource(filePath);
                InputStream inputStream = classPathResource.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, codeType));
                StringBuffer buffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                bufferedReader.close();
                return buffer.toString();
            } else {
                is = new InputStreamReader(new FileInputStream(filePath), codeType);
                in = new BufferedReader(is);
                line = in.readLine();
                StringBuffer content = new StringBuffer();
                while (line != null) {
                    content.append(line).append("\n");
                    line = in.readLine();
                }
                return content.toString();
            }
        } catch (Exception e) {

            return null;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }

    }

    public static boolean SaveAsFile(String filePath, String doc) {
        File fl = new File(filePath);
        if (fl.exists()) {
            fl.delete();
        }
        try {
            fl.createNewFile();
            FileWriter fw = new FileWriter(fl.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(doc);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;

    }

    public static String ReadTextFile2Html(String file) {
        String doc = ReadTextFile(file);
        return DataType.ParseText2Html(doc);
    }

    /**
     * 判断是否全部是汉字
     * <p>
     * param htmlstr
     *
     * @return
     */
    public static boolean CheckIsChinese(String htmlstr) {
        char[] chs = htmlstr.toCharArray();
        for (char c : chs) {
            int i = (new Character(c)).toString().length();
            if (i == 1) {
                return false;
            }
        }
        return true;
    }

    public static String Html2Text(String htmlstr) {
        htmlstr = htmlstr.replace("<BR>", "\n");
        return htmlstr.replace("&nbsp;", " ");
    }

    public static String ByteToString(byte[] bye) {
        String s = "";
        for (byte b : bye) {
            s += (new Byte(b)).toString();
        }
        return s;
    }

    public static byte[] StringToByte(String s) {
        byte[] bs = new byte[s.length()];
        char[] cs = s.toCharArray();
        int i = 0;
        for (char c : cs) {
            bs[i] = (byte) c;
            i++;
        }
        return bs;
    }

    /**
     * 取道百分比
     * <p>
     * param a
     * param b
     *
     * @return
     */
    public static String GetPercent(java.math.BigDecimal a, java.math.BigDecimal b) {
        java.math.BigDecimal p = a.divide(b);
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(p);

    }

    public static String GetWeek(int weekidx) {
        switch (weekidx) {
            case 0:
                return "星期日";
            case 1:
                return "星期一";
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            default:
                throw new RuntimeException("error weekidx=" + weekidx);
        }
    }

    public static String GetABC(String abc) {
        if (abc.equals("A")) {
            return "B";
        } else if (abc.equals("B")) {
            return "C";
        } else if (abc.equals("C")) {
            return "D";
        } else if (abc.equals("D")) {
            return "E";
        } else if (abc.equals("E")) {
            return "F";
        } else if (abc.equals("F")) {
            return "G";
        } else if (abc.equals("G")) {
            return "H";
        } else if (abc.equals("H")) {
            return "I";
        } else if (abc.equals("I")) {
            return "J";
        } else if (abc.equals("J")) {
            return "K";
        } else if (abc.equals("K")) {
            return "L";
        } else if (abc.equals("L")) {
            return "M";
        } else if (abc.equals("M")) {
            return "N";
        } else if (abc.equals("N")) {
            return "O";
        } else if (abc.equals("Z")) {
            return "A";
        } else {
            throw new RuntimeException("abc error" + abc);
        }
    }

    /**
     * 返回 data1 - data2 的天数.
     * <p>
     * param fromday
     * param today
     *
     * @return 相隔的天数
     */
    public static int SpanDays(String fromday, String today) throws ParseException {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = fmt.parse(fromday);
        Date date2 = fmt.parse(today);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        int days = Integer.parseInt(String.valueOf(between_days));
        return days;
    }

    /**
     * 返回 QuarterFrom - QuarterTo 的季度.
     * <p>
     * param _APFrom
     * QuarterFrom
     * param _APTo
     * QuarterTo
     *
     * @return 相隔的季度
     */
    public static int SpanQuarter(String _APFrom, String _APTo) throws ParseException {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date fromdate = fmt.parse(_APFrom + "-01");
        Date todate = fmt.parse(_APTo + "-01");

        int i = 0;
        if (fromdate.compareTo(todate) > 0) {
            throw new RuntimeException("选择出错！起始时期" + _APFrom + "不能大于终止时期" + _APTo + "!");
        }

        while (fromdate.compareTo(todate) <= 0) {
            i++;

            Calendar cd = Calendar.getInstance();
            cd.setTime(fromdate);
            cd.add(Calendar.MONTH, +1);
            fromdate = cd.getTime();
        }

        int j = (i + 2) / 3;
        return j;
    }

    /**
     * 到现在的天数。
     * <p>
     * param data1
     *
     * @return
     */
    public static int SpanDays(String data1) throws ParseException {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date fromday = fmt.parse(data1);
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(fromday);
        long time1 = cal.getTimeInMillis();
        cal.setTime(today);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        int days = Integer.parseInt(String.valueOf(between_days));
        return days;
    }


    /**
     * 检查是否是一个字段或者表名称
     * <p>
     * param str
     * 要检查的字段或者表名称
     *
     * @return 是否合法
     */
    public static boolean CheckIsFieldOrTableName(String str) {
      try
        {
            String s = str.substring(0, 1);
            if (DataType.IsNumStr(s))
                return false;

            String chars = "~!@#$%^&*()_+`{}|:'<>?[];',./";
            if (chars.contains(s) == true)
                return false;

            if (DataType.CheckIsChinese(str) == true)
                return false;

            return true;
        }catch(Exception ex)
        {
            return false;
        }
    }

    public static String ParseText2Html(String val) {
        return val.replace("\n", "<BR>").replace("~", "'");
    }

    public static String ParseHtmlToText(String val) {
        if (val == null) {
            return val;
        }

        val = val.replace("&nbsp;", " ");
        val = val.replace("  ", " ");

        val = val.replace("</td>", "");
        val = val.replace("</TD>", "");

        val = val.replace("</tr>", "");
        val = val.replace("</TR>", "");

        val = val.replace("<tr>", "");
        val = val.replace("<TR>", "");

        val = val.replace("</font>", "");
        val = val.replace("</FONT>", "");

        val = val.replace("</table>", "");
        val = val.replace("</TABLE>", "");

        val = val.replace("<BR>", "\n\t");
        val = val.replace("<BR>", "\n\t");
        val = val.replace("&nbsp;", " ");

        val = val.replace("<BR><BR><BR><BR>", "<BR><BR>");
        val = val.replace("<BR><BR><BR><BR>", "<BR><BR>");
        val = val.replace("<BR><BR>", "<BR>");

        char[] chs = val.toCharArray();

        boolean isStartRec = false;
        String recStr = "";
        for (char c : chs) {
            if (c == '<') {
                recStr = "";
                isStartRec = true; // 开始记录
            }

            if (isStartRec) {
                recStr += (new Character(c)).toString();
            }

            if (c == '>') {
                isStartRec = false;

                if (recStr.equals("")) {
                    isStartRec = false;
                    continue;
                }

                // 开始分析这个标记内的东西。
                String market = recStr.toLowerCase();
                if (market.contains("<img")) {
                    // 这是一个图片标记
                    isStartRec = false;
                    recStr = "";
                    continue;
                } else {
                    val = val.replace(recStr, "");
                    isStartRec = false;
                    recStr = "";
                }
            }
        }

        val = val.replace("字体：大中小", "");
        val = val.replace("字体:大中小", "");

        val = val.replace("  ", " ");
        val = val.replace("\t", "");
        val = val.replace("\n", "");
        val = val.replace("\r", "");
        return val;
    }

    public static String PraseStringToUrlFileName(String fileName) {

        if (fileName.lastIndexOf('\\') == -1) {
            fileName = PraseStringToUrlFileNameExt(fileName, "%", "%25");
            fileName = PraseStringToUrlFileNameExt(fileName, "+", "%2B");
            fileName = PraseStringToUrlFileNameExt(fileName, " ", "%20");
            fileName = PraseStringToUrlFileNameExt(fileName, "/", "%2F");
            fileName = PraseStringToUrlFileNameExt(fileName, "?", "%3F");
            fileName = PraseStringToUrlFileNameExt(fileName, "#", "%23");
            fileName = PraseStringToUrlFileNameExt(fileName, "&", "%26");
            fileName = PraseStringToUrlFileNameExt(fileName, "=", "%3D");
            fileName = PraseStringToUrlFileNameExt(fileName, " ", "%20");
            return fileName;
        }

        String filePath = fileName.substring(0, fileName.lastIndexOf('/'));
        String fName = fileName.substring(fileName.lastIndexOf('/'));

        fName = PraseStringToUrlFileNameExt(fName, "%", "%25");
        fName = PraseStringToUrlFileNameExt(fName, "+", "%2B");
        fName = PraseStringToUrlFileNameExt(fName, " ", "%20");
        fName = PraseStringToUrlFileNameExt(fName, "/", "%2F");
        fName = PraseStringToUrlFileNameExt(fName, "?", "%3F");
        fName = PraseStringToUrlFileNameExt(fName, "#", "%23");
        fName = PraseStringToUrlFileNameExt(fName, "&", "%26");
        fName = PraseStringToUrlFileNameExt(fName, "=", "%3D");
        fName = PraseStringToUrlFileNameExt(fName, " ", "%20");
        return filePath + fName;
    }

    private static String PraseStringToUrlFileNameExt(String fileName, String val, String replVal) {
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        fileName = fileName.replace(val, replVal);
        return fileName;
    }

    /**
     * 将中文转化成拼音
     * <p>
     * param exp
     *
     * @return
     */
    public static String ParseStringToPinyin(String exp) {
        // exp = exp.trim();
        switch (exp) {
            case "电话":
                return "Tel";
            case "地址":
                return "Addr";
            case "年龄":
                return "Age";
            case "邮件":
                return "Email";
            case "单价":
                return "DanJia";
            case "金额":
                return "JinE";
            case "单据编号":
                return "BillNo";
            default:
                break;
        }

        // 特殊处理.
        exp = exp.replace("单价", "DanJia");
        exp = exp.replace("单件", "DanJian");
        exp = exp.replace("单个", "DanGe");

        exp = exp.trim();
        // exp = exp.replaceAll(" ", "");
        String pinYin = "", str = null;
        char[] chars = exp.toCharArray();
        for (char c : chars) {
            try {
                str = String.valueOf(c);
                str = chs2py.convert((new Character(c)).toString());
                pinYin += str.substring(0, 1).toUpperCase() + str.substring(1);

            } catch (java.lang.Exception e) {
                pinYin += c;
            }
        }
        return pinYin;
    }

    /**
     * 转化成拼音第一个字母大字
     * <p>
     * param str
     * 要转化的中文串
     *
     * @return 拼音
     */
    public static String ParseStringToPinyinWordFirst(String str) {
        try {
            String _Temp = "";
            for (int i = 0; i < str.length(); i++) {
                _Temp = _Temp + DataType.ParseStringToPinyin(str.substring(i, i + 1));
            }
            return _Temp;
        } catch (RuntimeException ex) {
            throw new RuntimeException("@错误：" + str + "，不能转换成拼音。");
        }
    }

    /**
     * 转化成拼音第一个字母大字
     * <p>
     * param str
     * 要转化的中文串
     *
     * @return 拼音
     */
    public static String ParseStringToPinyinJianXie(String str) {
        str = str.replaceAll(" ", "");
        try {
            String _Temp = "";
            for (int i = 0; i < str.length(); i++) {
                _Temp += DataType.ParseStringToPinyin(str.substring(i, i + 1)).substring(0, 1);
            }
            return _Temp;
        } catch (RuntimeException ex) {
            throw new RuntimeException("@错误：" + str + "，不能转换成拼音。");
        }
    }

    /**
     * 转化成 decimal
     * <p>
     * param exp
     *
     * @return
     */
    public static BigDecimal ParseExpToDecimal(String exp) throws Exception {
        BigDecimal bigDecimal = new BigDecimal(0);
        if (exp.trim().equals("")) {
            throw new RuntimeException("DataType.ParseExpToDecimal要转换的表达式为空。");
        }

        exp = exp.replace("+-", "-");
        exp = exp.replace("￥", "");
        exp = exp.replace("\n", "");
        exp = exp.replace("\t", "");

        exp = exp.replace("＋", "+");
        exp = exp.replace("－", "-");
        exp = exp.replace("＊", "*");
        exp = exp.replace("／", "/");
        exp = exp.replace("）", ")");
        exp = exp.replace("（", "(");

        exp = exp.replace(".00.00", "00");

        exp = exp.replace("--", "- -");

        if (exp.indexOf("@") != -1) {
            return bigDecimal;
        }

        String val = exp.substring(0, 1);
        if (val.equals("-")) {
            exp = exp.substring(1);
        }
        exp = exp.replace("*100%", "*1");

        try {
            BigDecimal bigDecimal1 = new BigDecimal(exp);
            return bigDecimal1;
        } catch (java.lang.Exception e) {
            Log.DebugWriteError("ParseExpToDecimal " + e.getMessage());
        }

        try {
            String searchKey = "SELECT  " + exp + " as Num  ";

            for (DBType g : DBType.values()) {
                switch (g) {
                    case MSSQL:
                        break;
                    case Oracle:
                    case KingBaseR3:
                    case KingBaseR6:
                    case DM:
                    case GBASE8CByOracle:
                        searchKey = "SELECT  " + exp + " NUM from DUAL ";
                        return DBAccess.RunSQLReturnValDecimal(searchKey, bigDecimal, 2);
                    default:
                        break;
                }
            }

        } catch (RuntimeException ex) {
            return bigDecimal;
        }

        exp = exp.replace("-0", "");

        try {
            StringExpressionCalculate sc = new StringExpressionCalculate();

            return new BigDecimal(sc.TurnToDecimal(exp));
        } catch (RuntimeException ex) {
            if (exp.indexOf("/") != -1) {
                return bigDecimal;
            }
            throw new RuntimeException("表达式(\"" + exp + "\")计算错误：" + ex.getMessage());
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public static String ParseFloatToCache(float money) throws Exception {
        if (money == 0) {
            return "零圆零角零分";
        }
        DealString d = new DealString();
        d.setInputString((new Float(money)).toString());
        d.ConvertToChineseNum();
        return d.getOutString();
    }

    public static String ParseFloatToRMB(float money) throws Exception {
        if (money == 0) {
            return "零圆零角零分";
        }
        DealString d = new DealString();
        d.setInputString((new Float(money)).toString());
        d.ConvertToChineseNum();
        return d.getOutString();
    }

    public static boolean IsMobile(String input) {
        if (input.length() == 11 && input.substring(0, 1).equals("1")) {
            return true;
        }

        if (input.length() < 11) {
            return false;
        }

        //电信手机号码正则
        String dianxin = "^1[3578][01379]\\d{8}$";
        Pattern regexDX = Pattern.compile(dianxin);
        //联通手机号码正则
        String liantong = "^1[34578][01256]\\d{8}";
        Pattern regexLT = Pattern.compile(dianxin);
        //移动手机号码正则
        String yidong = "^(1[012345678]\\d{8}|1[345678][012356789]\\d{8})$";
        Pattern regexYD = Pattern.compile(dianxin);
        if (regexDX.matcher(input.toCharArray().toString()).matches()
                || regexLT.matcher(input.toCharArray().toString()).matches()
                || regexYD.matcher(input.toCharArray().toString()).matches()) {
            return true;
        }
        return false;
    }

    /**
     * 得到一个日期,根据系统
     * <p>
     * param dataStr
     *
     * @return
     */
    public final Date Parse(String dataStr) {
        return new Date(java.util.Date.parse(dataStr));
    }

    /**
     * 系统定义的时间格式 yyyy-MM-dd .
     */
    public static String getSysDateFormat() {
        return "yyyy-MM-dd";
    }

    public static String SysDateFormat(Date dt) {

        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
        return matter.format(dt);
    }

    /**
     * 当前月份加上指定的月份
     * <p>
     * param month
     *
     * @return
     */
    public static String addMonths(int month) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, month);
        String date = format.format(calendar.getTime());
        return date;

    }

    /**
     * 当前的日期
     */
    public static String getCurrentDate() {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
        String date = matter.format(dt);
        return date;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentDateTime() {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = matter.format(dt);
        return date;
    }

    public static Date getDate() {
        return new Date();
    }

    public static String getCurrentTime() {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("hh:mm");
        String date = matter.format(dt);
        return date;
    }

    /**
     * 把日期对象转换成指定格式的字符串
     * <p>
     * param date
     * - 日期对象
     * param sFormat
     * - 日期格式@return String yyyy-MM-dd HH:mm:ss
     */
    public static String dateToStr(Date date, String sFormat) {
        if (null == date)
            return "";

        SimpleDateFormat df = new SimpleDateFormat(sFormat);
        return df.format(date);
    }

    public static Date stringToDate(String str) {
        if (null == str || str.equals(""))
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getCurrentTimeQuarter() {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("hh:mm");
        String date = matter.format(dt);
        return date;
    }

    /**
     * 给一个时间，返回一个刻种时间。
     * <p>
     * param time
     *
     * @return
     */
    public static String ParseTime2TimeQuarter(String time) {
        String hh = time.substring(0, 3);
        int mm = Integer.parseInt(time.substring(3, 5));
        if (mm == 0) {
            return hh + "00";
        }

        if (mm < 15) {
            return hh + "00";
        }
        if (mm >= 15 && mm < 30) {
            return hh + "15";
        }

        if (mm >= 30 && mm < 45) {
            return hh + "30";
        }

        if (mm >= 45 && mm < 60) {
            return hh + "45";
        }
        return time;
    }

    public static String getCurrentDay() {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("dd");
        String date = matter.format(dt);
        return date;

    }

    /**
     * 当前的会计期间
     */
    public static String getCurrentAP() {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM");
        String date = matter.format(dt);
        return date;
    }

    /**
     * 当前的会计期间
     */
    public static String getCurrentYear() {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("yyyy");
        String date = matter.format(dt);
        return date;
    }

    public static String getCurrentMonth() {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("MM");
        String date = matter.format(dt);
        return date;
    }

    /**
     * 当前的会计期间 yyyy-MM
     */
    public static String getCurrentYearMonth() {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM");
        String date = matter.format(dt);
        return date;
    }

    public static String GetJDByMM(String mm) {
        String jd = "01";

        if (mm.equals("01") || mm.equals("02") || mm.equals("03")) {
            jd = "01";
        } else if (mm.equals("04") || mm.equals("05") || mm.equals("06")) {
            jd = "04";
        } else if (mm.equals("07") || mm.equals("08") || mm.equals("09")) {
            jd = "07";
        } else if (mm.equals("10") || mm.equals("11") || mm.equals("12")) {
            jd = "10";
        } else {
            throw new RuntimeException("@不是有效的月份格式" + mm);
        }
        return jd;
    }

    /**
     * 当前的季度期间yyyy-MM
     */
    public static String getCurrentAPOfJD() {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("yyyy");
        String date = matter.format(dt);
        SimpleDateFormat matter1 = new SimpleDateFormat("MM");
        String date1 = matter1.format(dt);
        String date2 = GetJDByMM(date1);
        return date + "-" + date2;
    }

    /**
     * 当前的季度的前一个季度.
     */
    public static String getCurrentAPOfJDOfFrontFamily() {
        Calendar cd = Calendar.getInstance();
        cd.set(Calendar.DAY_OF_MONTH, -3);
        cd.add(Calendar.MONTH, -3);
        Date d = cd.getTime();
        SimpleDateFormat matter = new SimpleDateFormat("yyyy");
        String date = matter.format(d);
        SimpleDateFormat matter1 = new SimpleDateFormat("MM");
        String date1 = matter1.format(d);
        String date2 = GetJDByMM(date1);
        return date + "-" + date2;
    }

    /**
     * 取出当前月份的上一个月份
     */
    public static String getCurrentNYOfPrevious() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        String date = format.format(calendar.getTime());
        return date;
    }

    /**
     * 当前的日期时间
     */
    public static String getCurrentDataTime() {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = matter.format(dt);
        return date;
    }

    public static String getCurrentDateByFormart(String formart) {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat(formart);
        String date = matter.format(dt);
        return date;
    }

    public static String getDateByFormart(Date date, String formart) {
        SimpleDateFormat matter = new SimpleDateFormat(formart);
        String dateStr = matter.format(date);
        return dateStr;
    }

    public static String getCurrentDateTimeCNOfShort() {
        SimpleDateFormat matter = new SimpleDateFormat("yy年MM月dd日 HH时mm分");
        return matter.format(new Date());
    }

    public static String getCurrentDateTimeCNOfLong() {
        SimpleDateFormat matter = new SimpleDateFormat("yy年MM月dd日 HH时mm分ss秒");
        return matter.format(new Date());
    }

    public static String getCurrentDateCNOfShort() {
        SimpleDateFormat matter = new SimpleDateFormat("yy年MM月dd日");
        return matter.format(new Date());
    }

    public static String getCurrentDateCNOfLong() {
        SimpleDateFormat matter = new SimpleDateFormat("yyyy年MM月dd日");
        return matter.format(new Date());
    }

    /**
     * 当前的日期时间
     */
    public static String getCurrentDateTimeCN() {
        Date date = new Date();
        SimpleDateFormat matter = new SimpleDateFormat(DataType.getSysDateFormatCN());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return matter.format(date) + "，" + GetWeekName(dayOfWeek);
    }

    private static String GetWeekName(int weekDay) {
        if (Calendar.MONDAY == weekDay) {
            return "星期一";
        } else if (Calendar.TUESDAY == weekDay) {
            return "星期二";
        } else if (Calendar.WEDNESDAY == weekDay) {
            return "星期三";
        } else if (Calendar.THURSDAY == weekDay) {
            return "星期四";
        } else if (Calendar.FRIDAY == weekDay) {
            return "星期五";
        } else if (Calendar.SATURDAY == weekDay) {
            return "星期六";
        } else {
            return "星期日";
        }
    }

    public static void main(String[] args) {
        long ts =new Date().getTime() -new Date("2023-08-02 14:00:00").getTime();
        java.time.Duration.ofSeconds(ts).toHours();
        System.out.println( java.time.Duration.ofSeconds(ts).toHours());
    }

    /**
     * 当前的日期时间
     */
    public static String getCurrentDateTimess() {
        /**
         * @update 修改版本时间（java中不支持空格）
         * @author xiongwei
         */
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return matter.format(new Date());
    }

    public static String ParseSysDateTime2SysDate(String sysDateformat) {
        try {
            return sysDateformat.substring(0, 10);
        } catch (RuntimeException ex) {
            throw new RuntimeException("日期格式错误:" + sysDateformat + " errorMsg=" + ex.getMessage());
        }
    }

    /**
     * 把chichengsoft本系统日期格式转换为系统日期格式。
     * <p>
     * param sysDateformat
     * yyyy-MM-dd
     *
     * @return DateTime
     */
    public static Date ParseSysDate2DateTime(String sysDateformat) {
        if (sysDateformat == null || sysDateformat.trim().length() == 0) {
            return new Date();
        }

        try {
            if (sysDateformat.length() > 10) {
                return ParseSysDateTime2DateTime(sysDateformat);
            }

            sysDateformat = sysDateformat.trim();
            String[] strs = null;
            if (sysDateformat.indexOf("-") != -1) {
                strs = sysDateformat.split("[-]", -1);
            }

            if (sysDateformat.indexOf("/") != -1) {
                strs = sysDateformat.split("[/]", -1);
            }

            int year = Integer.parseInt(strs[0]);
            int month = Integer.parseInt(strs[1]);
            int day = Integer.parseInt(strs[2]);

            return new Date(year, month, day, 0, 0, 0);
        } catch (RuntimeException ex) {
            throw new RuntimeException("日期[" + sysDateformat + "]转换出现错误:" + ex.getMessage() + "无效的日期是格式。");
        }
    }

    /**
     * 2005-11-04 09:12
     * <p>
     * param sysDateformat
     *
     * @return
     * @throws ParseException
     */
    public static Date ParseSysDateTime2DateTime(String sysDateformat) {

        try {
            return org.apache.commons.lang.time.DateUtils.parseDate(sysDateformat,
                    new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", "yyyy/MM/dd",
                            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
                            "yyyy.MM.dd HH:mm", "yyyy.MM", "yyyy", "yyyy年MM月dd日", "yyyy年MM月dd日 HH时mm分ss秒",
                            "yyyy年MM月dd日 HH时mm分", "yyyy年MM月"});
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * param dtoffrom
     * param dtofto
     *
     * @return
     * @throws ParseException
     */
    public static int GetSpanDays(String dtoffrom, String dtofto) {

        if (dtoffrom == null || dtofto == null || dtofto.length() <= 7 || dtoffrom.length() <= 7)
            return 0;


        java.util.Date dtfrom = DataType.ParseSysDate2DateTime(dtoffrom);
        java.util.Date dtto = DataType.ParseSysDate2DateTime(dtofto);
        long diff = dtto.getTime() - dtfrom.getTime();
        long day = diff / (1000 * 60 * 60 * 24);
        int ds = (int) day;
        return ds;
    }


    public static int GetSpanHours(String fromdatetim, String toDateTime) throws ParseException {
        java.util.Date dtfrom = DataType.ParseSysDateTime2DateTime(fromdatetim);
        java.util.Date dtto = DataType.ParseSysDateTime2DateTime(toDateTime);
        long diff = dtto.getTime() - dtfrom.getTime();
        long day = diff / (24 * 60 * 60 * 1000);
        long hour = (diff / (60 * 60 * 1000) - day * 24);
        return (int)hour;
    }

    public static float GetSpanHours(Date fromdatetim, Date toDateTime) throws ParseException {
        long diff = toDateTime.getTime() - fromdatetim.getTime();
        float day = diff / (24 * 60 * 60 * 1000);
        float hour = (diff / (60 * 60 * 1000) - day * 24);
        hour = Float.parseFloat(String.format("%.2f", hour));
        return hour;
    }
    /**
     * 到现在的时间
     * <p>
     * param fromdatetim
     *
     * @return 分中数
     */
    public static int GetSpanMinute(String fromdatetim) throws ParseException {
        java.util.Date dtfrom = DataType.ParseSysDateTime2DateTime(fromdatetim);
        java.util.Date dtto = new java.util.Date();
        long diff = dtto.getTime() - dtfrom.getTime();
        long day = diff / (24 * 60 * 60 * 1000);
        long hour = (diff / (60 * 60 * 1000) - day * 24);
        long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        int mn = (int) min;
        int hu = (int) hour;
        return mn + hu * 60;
    }

    /**
     * 系统定义日期时间格式 yyyy-MM-dd hh:mm
     */
    public static String getSysDateTimeFormat() {
        return "yyyy-MM-dd HH:mm";
    }

    public static String SysDateTimeFormat(Date dt) {

        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
         return  matter.format(dt);

    }


    /**
     * 系统定义日期时间格式 yyyy-MM-dd hh:mm
     */
    public static String getSysDateTimessFormat() {
        return "yyyy-MM-dd HH:mm:ss";
    }

    public static String getSysDateFormatCN() {
        return "yyyy年MM月dd日";
    }

    public static String getSysDatetimeFormatCN() {
        return "yyyy年MM月dd日 HH时mm分";
    }

    public static DBUrlType GetDBUrlByString(String strDBUrl) {
        return DBUrlType.AppCenterDSN;
    }

    public static int GetDataTypeByString(String datatype) {
        if (datatype.equals("AppBoolean")) {
            return DataType.AppBoolean;
        } else if (datatype.equals("AppDate")) {
            return DataType.AppDate;
        } else if (datatype.equals("AppDateTime")) {
            return DataType.AppDateTime;
        } else if (datatype.equals("AppDouble")) {
            return DataType.AppDouble;
        } else if (datatype.equals("AppFloat")) {
            return DataType.AppFloat;
        } else if (datatype.equals("AppInt")) {
            return DataType.AppInt;
        } else if (datatype.equals("AppMoney")) {
            return DataType.AppMoney;
        } else if (datatype.equals("AppString")) {
            return DataType.AppString;
        } else {
            throw new RuntimeException("@没有此类型[" + DataType.AppString + "]");
        }
    }

    public static String GetDataTypeDese(int datatype) {
        if (WebUser.getSysLang().equals("CH")) {
            switch (datatype) {
                case DataType.AppBoolean:
                    return "布尔(Int)";
                case DataType.AppDate:
                    return "日期nvarchar";
                case DataType.AppDateTime:
                    return "日期时间nvarchar";
                case DataType.AppDouble:
                    return "双精度(double)";
                case DataType.AppFloat:
                    return "浮点(float)";
                case DataType.AppInt:
                    return "整型(int)";
                case DataType.AppMoney:
                    return "货币(float)";
                case DataType.AppString:
                    return "字符(nvarchar)";
                default:
                    throw new RuntimeException("@没有此类型");
            }
        }

        switch (datatype) {
            case DataType.AppBoolean:
                return "Boolen";
            case DataType.AppDate:
                return "Date";
            case DataType.AppDateTime:
                return "Datetime";
            case DataType.AppDouble:
                return "Double";
            case DataType.AppFloat:
                return "Float";
            case DataType.AppInt:
                return "Int";
            case DataType.AppMoney:
                return "Money";
            case DataType.AppString:
                return "Nvarchar";
            default:
                throw new RuntimeException("@没有此类型");
        }
    }

    /**
     * string
     */
    public static final int AppString = 1;
    /**
     * int
     */
    public static final int AppInt = 2;
    /**
     * float
     */
    public static final int AppFloat = 3;
    /**
     * AppBoolean
     */
    public static final int AppBoolean = 4;
    /**
     * AppDouble
     */
    public static final int AppDouble = 5;
    /**
     * AppDate
     */
    public static final int AppDate = 6;
    /**
     * AppDateTime
     */
    public static final int AppDateTime = 7;
    /**
     * AppMoney
     */
    public static final int AppMoney = 8;
    /**
     * 率百分比。
     */
    public static final int AppRate = 9;

    public static String StringToDateStr(String str) {
        // try
        // {
        // java.util.Date dt = new java.util.Date(java.util.Date.parse(str));
        // String year = dt.Year.toString();
        // String month = dt.Month.toString();
        // String day = dt.Day.toString();
        // return year + "-" + month.PadLeft(2, '0') + "-" + day.PadLeft(2,
        // '0');
        // //return str;
        // }
        // catch (RuntimeException ex)
        // {
        // throw ex;
        // }
        return "";
    }

    public static String GenerSpace(int spaceNum) {
        if (spaceNum <= 0) {
            return "";
        }

        String strs = "";
        while (spaceNum != 0) {
            strs += "&nbsp;&nbsp;";
            spaceNum--;
        }
        return strs;
    }

    /**
     * @return
     * @Title: getShangCurrentNYOfPrevious
     * @Description: 得到当前日期的上两个月
     * @author dongliang
     * @date 2016年8月12日
     */
    public static String getShangCurrentNYOfPrevious() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2);
        String date = format.format(calendar.getTime());
        return date;

    }

    /**
     * @return
     * @Title: getCurrentWeek
     * @Description: 获取当前星期是一年之中第几周
     * @author dongliang
     * @date 2016年8月12日
     */

    public static int getCurrentWeek() {

        Calendar c = Calendar.getInstance();
        int i = c.get(Calendar.WEEK_OF_YEAR);
        return i;

    }

    /**
     * 根据一个日期，获得该日期属于一年的第几周.
     * @param dataTimeStr
     * @return
     */
    public static int getCurrentWeekGetWeekByDay(String dataTimeStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ParseSysDate2DateTime(dataTimeStr));
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        return  weekOfYear;
    }

    public static String GenerBR(int spaceNum) {
        String strs = "";
        while (spaceNum != 0) {
            strs += "<BR>";
            spaceNum--;
        }
        return strs;
    }

    public static boolean IsImgExt(String ext) {
        ext = ext.replace(".", "").toLowerCase();
        if (ext.equals("jpg") || ext.equals("gif") || ext.equals("jepg") || ext.equals("bmp") || ext.equals("png")
                || ext.equals("tif") || ext.equals("gsp") || ext.equals("mov") || ext.equals("psd")
                || ext.equals("tiff") || ext.equals("wmf")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean IsVoideExt(String ext) {
        ext = ext.replace(".", "").toLowerCase();
        if (ext.equals("mp3") || ext.equals("mp4") || ext.equals("asf") || ext.equals("wma") || ext.equals("rm")
                || ext.equals("rmvb") || ext.equals("mpg") || ext.equals("wmv") || ext.equals("quicktime")
                || ext.equals("avi") || ext.equals("flv") || ext.equals("mpeg")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean IsNumStr(String str) {
        if (DataType.IsNullOrEmpty(str) == true)
            return false;

        Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
        return pattern.matcher(str).matches();
    }

    public static boolean IsQS(int num) {
        int ii = 0;
        for (int i = 0; i < 500; i++) {
            if (num == ii) {
                return false;
            }
            ii = ii + 2;
        }
        return true;
    }

    public static boolean StringToBoolean(String str) {
        if (str == null || str.equals("") || str.equals(",nbsp;")) {
            return false;
        }

        if (str.equals("0") || str.equals("1")) {
            if (str.equals("0")) {
                return false;
            } else {
                return true;
            }
        } else if (str.equals("true") || str.equals("false")) {
            if (str.equals("false")) {
                return false;
            } else {
                return true;
            }

        } else if (str.equals("是") || str.equals("否")) {
            if (str.equals("否")) {
                return false;
            } else {
                return true;
            }
        } else {
            throw new RuntimeException("@要转换的[" + str + "]不是bool 类型");
        }
    }

    /**
     * 转化为友好的日期
     * <p>
     * param sysDateformat
     * 日期
     *
     * @return
     */
    public static String ParseSysDate2DateTimeFriendly(String sysDateformat) {

        return DateStringFromNow(DataType.ParseSysDateTime2DateTime(sysDateformat));
    }

    public final String DateStringFromNow(String dt) {
        return DateStringFromNow(DataType.ParseSysDateTime2DateTime(dt));
    }

    public static String DateStringFromNow(Date dt) {
        Date date = new Date();
        long spanTotal = date.getTime() - dt.getTime();
        long spanSeconds = spanTotal / 1000;
        long spanMinutes = spanTotal / 1000 / 60;
        long spanHours = spanTotal / 1000 / 60 / 60;
        long spanDays = spanTotal / 1000 / 60 / 60 / 24;
        if (spanDays > 60) {
            return dt.toString();
        } else {
            if (spanDays > 30) {
                return "1个月前";
            } else {
                if (spanDays > 14) {
                    return "2周前";
                } else {
                    if (spanDays > 7) {
                        return "1周前";
                    } else {
                        if (spanDays > 1) {
                            return String.format("%1$s天前", (int) Math.floor(spanDays));
                        } else {
                            if (spanHours > 1) {
                                return String.format("%1$s小时前", (int) Math.floor(spanHours));
                            } else {
                                if (spanMinutes > 1) {
                                    return String.format("%1$s分钟前", (int) Math.floor(spanMinutes));
                                } else {
                                    if (spanSeconds >= 1) {
                                        return String.format("%1$s秒前", (int) Math.floor(spanSeconds));
                                    } else {
                                        return "1秒前";
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 将文本转换成可用做Name,Text的文本，文本中仅允许含有汉字、字母、数字、下划线
     * <p>
     * param nameStr
     * 待转换的文本
     * param maxLen
     * 文本最大长度，0为不限制，超过maxLen，截取前maxLen字符长度
     *
     * @return
     */
    public static String ParseStringForName(String nameStr, int maxLen) {
        if (DataType.IsNullOrEmpty(nameStr)) {
            return "";
        }

        String nStr = nameStr.replaceAll(RegEx_replace_OnlyHSZX, "");

        if (maxLen > 0 && nStr.length() > maxLen) {
            return nStr.substring(0, maxLen);
        }

        return nStr;
    }

    /**
     * 将文本转换成可用做No的文本，文本中仅允许含有字母、数字、下划线，且开头只能是字母
     * <p>
     * param noStr
     * 待转换的文本
     * param maxLen
     * 文本最大长度，0为不限制，超过maxLen，截取前maxLen字符长度
     *
     * @return
     */
    public static String ParseStringForNo(String noStr, int maxLen) {
        if (DataType.IsNullOrEmpty(noStr)) {
            return "";
        }

        String nStr = noStr.replaceAll(RegEx_replace_OnlySZX, "").replaceAll(RegEx_replace_FirstXZ, "");

        if (maxLen > 0 && nStr.length() > maxLen) {
            return nStr.substring(0, maxLen);
        }

        return nStr;
    }

    public static DataSet CXmlFileToDataSet(String xmlFilePath) {
        if (!IsNullOrEmpty(xmlFilePath)) {
            try {
                DataSet ds = new DataSet();
                ds.readXml(xmlFilePath);
                return ds;
            } catch (Exception e) {
                throw e;
            }

        }
        return null;
    }

    public static String DealFromatSQLWhereIn(String ids) {
        if (ids.contains("'") == true)
            return ids;

        String[] strs = ids.split(",");
        String sqlIn = "";
        for (String str : strs) {
            sqlIn += ",'" + str + "'";
        }
        return sqlIn.substring(1);
    }

    public static String filtrateSQL(String searchKey) {
        if (DataType.IsNullOrEmpty(searchKey)) {
            //throw new IllegalArgumentException("参数为空!");
            return "";
        }
        //由于MySQL中\为默认转义字符
        //处理参数中的特殊字符,\用\\转义成普通的\字符.

        String newKey = searchKey;
        newKey = newKey.replaceAll("\\\\", "\\\\\\\\");
        newKey = newKey.replaceAll("'", "\\\\'");
        newKey = newKey.replaceAll("_", "\\\\_");
        newKey = newKey.replaceAll("%", "\\\\%");
        newKey = newKey.replaceAll("\"", "\\\\\"");

        return newKey;
    }

    /**
     * 判断字符串是否为JSON格式
     * @param val
     * @return
     */
    public static boolean IsJson(String val){
        boolean isJson = false;
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(val);
            isJson = true;
        }catch (JsonProcessingException e){
        }
        return isJson;
    }

     /**
     * JSON符串转Hashtable
     * @param jsonString
     * @return
     */
    public static Hashtable ToHashtable(String jsonString){
        Hashtable<String, Object> hashtable = new Hashtable<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonString);
            hashtable = mapper.convertValue(jsonNode, Hashtable.class);
        }catch (JsonProcessingException e){
            e.printStackTrace();
            Log.DebugWriteError("err@转换JSON时出现异常." + e.getMessage());
        }
        return hashtable;
    }
    public static Hashtable ParseParasToHT(String paras)
    {
        Hashtable ht = new Hashtable();
        if (paras != null)
        {
            paras = paras.replace("；", ";");
            paras = paras.replace(" ", "");
            paras = paras.replace("@", ";");

            String[] args = paras.split(";");
            for(String arg : args)
            {
                String[] kv = arg.split("=");
                if (kv.length > 1)
                {
                    String key = kv[0];
                    if (ht.contains(key) == true)
                        continue;
                    ht.put(key, kv[1]);
                }
            }
        }
        return ht;
    }
    public static String HttpDownloadFile(String url, String path) throws Exception {
        URL httpUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
        conn.setConnectTimeout(5*1000);
        //conn.setRequestMethod("POST");
        //conn.connect();
        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
//        String content_disposition = conn.getHeaderField("content-disposition");
//        //微信服务器生成的文件名称
//        String fileName ="";
//        String[] content_arr = content_disposition.split(";");
//        if(content_arr.length  == 2){
//            String tmp = content_arr[1];
//            int index = tmp.indexOf("\"");
//            fileName =tmp.substring(index+1, tmp.length()-1);
//        }
        //生成不同文件名称
        File file = new File(path);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        byte[] buf = new byte[2048];
        int length = bis.read(buf);
        while(length != -1){
            bos.write(buf, 0, length);
            length = bis.read(buf);
        }
        bos.close();
        bis.close();
        return path;
    }
}
