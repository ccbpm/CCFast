package cn.jflow.boot.Utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 动态代理实现不了，JDK自带的基于接口的方式不能写static类型的方法，使用静态代理试一下
 * <p>
 * 基于java.net方式的http工具类
 * * <dependency>
 * * <groupId>org.apache.httpcomponents</groupId>
 * * <artifactId>httpclient</artifactId>
 * * </dependency>
 * * <dependency>
 * * <groupId>org.apache.httpcomponents</groupId>
 * * <artifactId>httpmime</artifactId>
 * * </dependency>
 */
public class HttpUtil {
    private final static CloseableHttpClient httpClient;
    private final static PoolingHttpClientConnectionManager poolManager;
    private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    static {
        // 生成自定义的httpClient
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        SSLConnectionSocketFactory sslConnectionSocketFactory = getSslConnectionSocketFactory();
        RegistryBuilder<ConnectionSocketFactory> builder = registryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
        if (sslConnectionSocketFactory != null) {
            builder.register("https", sslConnectionSocketFactory);
        }
        poolManager = new PoolingHttpClientConnectionManager(builder.build());
        poolManager.setMaxTotal(50);
        poolManager.setDefaultMaxPerRoute(50);

        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(poolManager);
        // 生成默认配置
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .build();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);


        List<Header> defaultHeader = new ArrayList<>();
        defaultHeader.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36"));
        httpClientBuilder.setDefaultHeaders(defaultHeader);
        httpClient = httpClientBuilder.build();
    }

    private static SSLConnectionSocketFactory getSslConnectionSocketFactory() {
        try {
            return new SSLConnectionSocketFactory(SSLContexts.custom().loadTrustMaterial(null, (x509Certificates, s) -> true).build(), new NoopHostnameVerifier());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static JSONObject get(String url, ArrayList<Header> headers) {
        HttpGet httpGet = new HttpGet();
        return doExecute(httpGet, url, null, headers);
    }


    public static JSONObject post(String url, Map<String, String> params, ArrayList<Header> headers) {
        HttpPost httpPost = new HttpPost();
        if (!CollectionUtils.isEmpty(params)) {
            List<BasicNameValuePair> formParam = new ArrayList<>();
            params.forEach((k, v) -> formParam.add(new BasicNameValuePair(k, v)));
            UrlEncodedFormEntity from = new UrlEncodedFormEntity(formParam, Consts.UTF_8);
            from.setContentType(new BasicHeader("Content-Type", ContentType.create("application/x-www-form-urlencoded", StandardCharsets.UTF_8).getMimeType()));
            httpPost.setEntity(from);
        }
        return doExecute(httpPost, url, params, headers);
    }


    public static JSONObject postJson(String url, Map<String, String> params, ArrayList<Header> headers) {
        HttpPost httpPost = new HttpPost();
        if (!CollectionUtils.isEmpty(params)) {
            StringEntity entity = new StringEntity(JSON.toJSONString(params), StandardCharsets.UTF_8);
            entity.setContentType(new BasicHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType()));
            entity.setContentEncoding(StandardCharsets.UTF_8.name());
            httpPost.setEntity(entity);
        }
        return doExecute(httpPost, url, params, headers);
    }

    public static JSONObject postJsonString(String url, String json, ArrayList<Header> headers) {
        HttpPost httpPost = new HttpPost();
        if (!StringUtils.isEmpty(json)) {
            StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
            entity.setContentType(new BasicHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType()));
            entity.setContentEncoding(StandardCharsets.UTF_8.name());
            httpPost.setEntity(entity);
        }
        return doExecute(httpPost, url, JSON.parseObject(json, Map.class), headers);
    }
    public static byte[] downloadFile(String url, Map<String, String> headers) {
        CloseableHttpResponse response = null;
        try {
            url = URLDecoder.decode(url, StandardCharsets.UTF_8.name());
            HttpGet httpPost = new HttpGet(url);

            // 设置header
            if (!CollectionUtils.isEmpty(headers)) {
                headers.forEach(httpPost::setHeader);
            }
            response = httpClient.execute(httpPost);
            if (HttpStatus.OK.value() == response.getStatusLine().getStatusCode()) {
                logger.info("==========请求成功，请求地址: {}，请求状态信息: {}", url, response.getStatusLine());
                return EntityUtils.toByteArray(response.getEntity());
            }
            logger.error("==========请求失败，请求地址: {}，请求状态信息: {}", url, response.getStatusLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return new byte[]{};
    }


    private static JSONObject doExecute(HttpRequestBase httpRequest, String url, Map<String, String> params, ArrayList<Header> headers) {
        CloseableHttpResponse response = null;
        JSONObject result = new JSONObject();
        try {
            httpRequest.setURI(new URI(URLDecoder.decode(url, StandardCharsets.UTF_8.name())));
            if (!CollectionUtils.isEmpty(headers)) {
                headers.forEach(httpRequest::setHeader);
            }
            response = httpClient.execute(httpRequest);
            if (HttpStatus.OK.value() == response.getStatusLine().getStatusCode()) {
                logger.info("==========请求成功，请求地址: {}，请求状态信息: {}，请求入参: {}", url, response.getStatusLine(), params);
                result = JSON.parseObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
            } else {
                logger.error("==========请求失败，请求地址: {}，请求状态信息: {}，请求入参: {}", url, response.getStatusLine(), params);
            }
        } catch (Exception e) {
            logger.error("==========请求失败，请求地址: {}，请求状态信息: {}，请求入参: {}", url, response != null ? response.getStatusLine() : "", params);
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
