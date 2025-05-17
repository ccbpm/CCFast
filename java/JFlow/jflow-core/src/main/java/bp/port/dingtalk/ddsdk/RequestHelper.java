package bp.port.dingtalk.ddsdk;

import bp.da.Log;
import bp.tools.HttpClientUtil;

/**
 * 请求协助类
 */
public class RequestHelper {
    ///#region Get

    /**
     * 执行基本的命令方法,以Get方式
     *
     * @param apiurl
     * @return
     */
    public static String Get(String apiurl) {
        return HttpClientUtil.doGet(apiurl);
    }
    ///#endregion

    ///#region Post

    /**
     * 以Post方式提交命令
     */
    public static String Post(String apiurl, String jsonString) {
        return HttpClientUtil.doPostJson(apiurl, jsonString);
    }
    ///#endregion

    /**
     * 下载文件
     *
     * @param downLoadUrl  下载地址
     * @param saveFullName 保存路径
     * @return
     */
    public static boolean HttpDownLoadFile(String downLoadUrl, String saveFullName) {
        boolean flagDown = false;
        try {
            HttpClientUtil.HttpDownloadFile(downLoadUrl, saveFullName);
            flagDown = true;
        } catch (RuntimeException ex) {
            Log.DebugWriteError(ex.getMessage());
        }
        return flagDown;
    }
}
