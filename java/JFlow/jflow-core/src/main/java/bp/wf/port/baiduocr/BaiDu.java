package bp.wf.port.baiduocr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BaiDu {
    /**
     获取百度APIKey

     */
    public static String getBaiDu_APIKey()
    {
        return bp.difference.SystemConfig.getAPIKey();
    }
    ///// <summary>
    ///// 获取百度SecretKey
    ///// </summary>
    public static String getBaiDu_SecretKey()
    {
        return bp.difference.SystemConfig.getSecretKey();
    }
    /**
     获取百度云token

     @return
     */
    public static String getAccessToken() throws IOException {
        //百度云应用获取token
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + getBaiDu_APIKey()
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + getBaiDu_SecretKey();

        URL realUrl = new URL(getAccessTokenUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String result = "";
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }
        return result;
    }

}
