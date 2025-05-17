package bp.tools;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

import bp.da.Log;
import bp.da.LogType;
import bp.sys.SFDBSrc;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import bp.da.DataType;

public class HttpClientUtil {
	private static HttpClientContext context = HttpClientContext.create();  
	 private static RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(120000).setSocketTimeout(60000)  
	            .setConnectionRequestTimeout(60000).setCookieSpec(CookieSpecs.STANDARD).  
	                    setExpectContinueEnabled(true).  
	                    setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST)).  
	                    setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();  
	 
    public static String doGet(String url, String data,String header,String context1) {  
        CookieStore cookieStore = new BasicCookieStore();  
        CloseableHttpClient httpClient = HttpClientBuilder.create().  
                setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy()).  
                setRedirectStrategy(new DefaultRedirectStrategy()).  
                setDefaultCookieStore(cookieStore).  
                setDefaultRequestConfig(requestConfig).build();  
        String resultString = "";
        HttpGet httpGet = new HttpGet(url);  
        if(DataType.IsNullOrEmpty(context1)== false)
        	httpGet.setHeader(header,context1); 
        CloseableHttpResponse response = null;  
        try {  
            response = httpClient.execute(httpGet, context);  
            resultString = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
        }catch (Exception e){
			Log.DebugWriteError(e.getMessage());
		}
        return resultString;
    }
	public static String doGetSSL(String url, Map<String, String> headerMap,Map<String, String> param) {

		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建Httpclient对象
			CloseableHttpClient httpclient = new HttpIgnoreCertification();
			// 创建uri
			URIBuilder builder = new URIBuilder(url);

			URI uri = builder.build();
			if (param != null) {
				for (Object key : param.keySet()) {
					builder.addParameter(key.toString(), param.get(key));
				}
			}

			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);
			//创建Header参数列表
			if(headerMap!=null) {
				for (Object key : headerMap.keySet()) {
					httpGet.setHeader(key.toString(),headerMap.get(key));

				}
			}

			// 执行请求
			response = httpclient.execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				resultString = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
			}
			if(response.getStatusLine().getStatusCode()==302){
				Header header = response.getFirstHeader("location"); // 跳转的目标地址是在 HTTP-HEAD 中的


				httpGet = new HttpGet(header.getValue());
				httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");


				response = httpclient.execute(httpGet);
				int  code = response.getStatusLine().getStatusCode();
				System.out.println("login" + EntityUtils.toString(response.getEntity(), Consts.UTF_8));


			}


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				//httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}
	public static String doGet1(String url, Map<String, String> param) {
		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpConnectionManager.getHttpClient();
		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			if (param != null) {
				for (Object key : param.keySet()) {
					builder.addParameter(key.toString(), param.get(key));
				}
			}
			URI uri = builder.build();

			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);

			// 执行请求
			response = httpclient.execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				resultString = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
			}
			if(response.getStatusLine().getStatusCode()==302){  
				Header header = response.getFirstHeader("location"); // 跳转的目标地址是在 HTTP-HEAD 中的

	 
				httpGet = new HttpGet(header.getValue());
				httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
	            
	 
	            response = httpclient.execute(httpGet);
	            int  code = response.getStatusLine().getStatusCode();
	            System.out.println("login" + EntityUtils.toString(response.getEntity(), Consts.UTF_8));


			}  
		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	public static String doGet(String url) {
		String resultString  = doGet(url, null,null,null);
		  System.out.println(resultString);  
		  return resultString;
	}

	public static String doPostSSL(String url, String Json,Map<String,String>headerParam) {
		// 创建Httpclient对象
		HttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			HttpClient httpClient = new HttpIgnoreCertification();

			httpPost.addHeader("Content-Type", "application/json");
			//创建Header参数列表
			if(headerParam!=null) {
				for (Object key : headerParam.keySet()) {
					httpPost.setHeader(key.toString(),headerParam.get(key));

				}
			}
			// 创建body参数列表
			if (Json != null) {
				StringEntity s = new StringEntity(Json,"utf-8");
				//s.setContentEncoding("utf-8");
				s.setContentType("application/json;charset=UTF-8"); //发送json数据需要设置contentType  
				s.setContentEncoding(new BasicHeader("Content-Type", "application/json;charset=UTF-8"));
				httpPost.setEntity(s);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			if(response.getStatusLine().getStatusCode()==302){
				return "";
			}

			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultString;
	}
	public static String doPost(String url, Map<String, String> param,String header,String context) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpConnectionManager.getHttpClient();	
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			
			if(DataType.IsNullOrEmpty(context)== false)
				httpPost.setHeader(header,context); 
			// 创建参数列表
			if (param != null) {
				List<NameValuePair> paramList = new ArrayList<>();
				for (Object key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key.toString(), param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, Consts.UTF_8);

				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			if(response.getStatusLine().getStatusCode()==302){  
				return "";
			}  
		
			resultString = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "调用HTTPClient出错："+ e.getMessage());
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	/**
	 * http连接
	 * param url
	 * param param body 参数
	 * param headerParam 头部参数
	 * @return
	 */
	public static String doPost(String url, String Json,Map<String,String> headerParam) throws Exception {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpConnectionManager.getHttpClient();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			URI uri = new URI(url);
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(uri);

			//创建Header参数列表
			if(headerParam!=null) {
				for (Object key : headerParam.keySet()) {
					httpPost.setHeader(key.toString(),headerParam.get(key));

				}
			}
			// 创建body参数列表
			if (Json != null) {
				StringEntity s = new StringEntity(Json, Consts.UTF_8);
				s.setContentEncoding("utf-8");
				s.setContentType("application/json"); //发送json数据需要设置contentType  
				httpPost.setEntity(s);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			if(response.getStatusLine().getStatusCode()==302){
				return "";
			}

			resultString = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	public static String doPost(String url) {
		return doPost(url, null,null,null);
	}

	/**
	 * 请求的参数类型为json
	 *
	 * param url
	 * param json
	 * @return {username:"",pass:""} 1
	 */
	public static String doPostJson(String url, String json) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpConnectionManager.getHttpClient();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	public static void HttpDownloadFile(String urlPath,String toPath) {

		InputStream inputStream = getInputStream(urlPath);
		byte[] data = new byte[1024];
		int len = 0;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(toPath);
			while ((len = inputStream.read(data)) != -1) {
				fileOutputStream.write(data, 0, len);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}


	// 从服务器获得一个输入流(本例是指从服务器获得一个image输入流)
	public static InputStream getInputStream(String urlPath) {
		InputStream inputStream = null;
		HttpURLConnection httpURLConnection = null;

		try {
			URL url = new URL(urlPath);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			// 设置网络连接超时时间
			httpURLConnection.setConnectTimeout(3000);
			// 设置应用程序要从网络连接读取数据
			httpURLConnection.setDoInput(true);

			httpURLConnection.setRequestMethod("GET");
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode == 200) {
				// 从服务器返回一个输入流
				inputStream = httpURLConnection.getInputStream();

			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return inputStream;

	}

	public static String HttpPostConnect_Data(String dbSrNo, String exp, Hashtable ht, String requestMethod) throws Exception {  //返回值
		//用户输入的webAPI地址
		String apiUrl = exp;
		if (apiUrl.contains("@WebApiHost"))//可以替换配置文件中配置的webapi地址
			apiUrl = apiUrl.replace("@WebApiHost",bp.difference.SystemConfig.GetValByKey("WebApiHost",""));

		SFDBSrc mysrc = new SFDBSrc(dbSrNo);
		//拼接路径.
		apiUrl = mysrc.getConnString() + apiUrl;

        //#region 解析路径变量 /{xxx}/{yyy} ? xxx=xxx
		if (apiUrl.contains("{") == true)
		{
			if (ht != null)
			{
				for(Object key : ht.keySet())
				{
					apiUrl = apiUrl.replace("{" + key + "}", ht.get(key).toString());
				}
			}
		}
        //#endregion

		String[] strs = apiUrl.split("?");
		//api接口地址
		String apiHost = strs[0];
		//api参数
		String apiParams = "";
		if (strs.length == 2)
			apiParams = strs[1];
		return doPostJson(apiHost, apiParams);
	}

	public static void main(String[] args) {
		String eurl="http://59.212.37.97:85/encrypt/decryptVal";
		String durl="http://59.212.37.97:85/encrypt/encryptVal";
		Map<String, String> param=new HashMap<>();
//		param.put("val","13225315328");
		param.put("val","AdzygP/rjzIf98CS+DWuyw==");

        String r=doPost(eurl,param,"AppNo","gjpt");
//		String r=doGet1("http://172.25.116.26:9009", param);
		System.out.println(r);
	}
}