package bp.port.dingtalk.ddsdk;


import bp.da.DataType;
import com.google.gson.Gson;

/**
 * 分析器
 */
public class Analyze {
    ///#region Get Function

    /**
     * 发起GET请求
     *
     * <typeparam name="T"></typeparam>
     *
     * @param requestUrl
     * @return
     */
    public static <T extends ResultPackage> T Get(String requestUrl, Class<T> clazz) {
        String resultJson = RequestHelper.Get(requestUrl);
        return AnalyzeResult(resultJson, clazz);
    }
    ///#endregion

    ///#region Post Function

    /**
     * 发起POST请求
     *
     * <typeparam name="T"></typeparam>
     *
     * @param requestUrl
     * @param requestParamOfJsonStr
     * @return
     */
    public static <T extends ResultPackage> T Post(String requestUrl, String requestParamOfJsonStr, Class<T> clazz) {
        String resultJson = RequestHelper.Post(requestUrl, requestParamOfJsonStr);
        return AnalyzeResult(resultJson, clazz);
    }
    ///#endregion

    /**
     * 下载文件
     *
     * @param downLoadUrl
     * @param saveFullName
     * @return
     */
    public static boolean HttpDownLoadFile(String downLoadUrl, String saveFullName) {
        return RequestHelper.HttpDownLoadFile(downLoadUrl, saveFullName);
    }

    ///#region AnalyzeResult

    /**
     * 分析结果
     * @param resultJson
     * @return
     */
    private static <T extends ResultPackage> T AnalyzeResult(String resultJson, Class<T> clazz) {
        Gson gson = new Gson();
        T result = null;
        if (!DataType.IsNullOrEmpty(resultJson)) {
            result = gson.fromJson(resultJson, clazz);
            if (result != null && result.ItIsOK()) {
                result.setJson(resultJson);
            }
        }
        return result;
    }
    ///#endregion
}
