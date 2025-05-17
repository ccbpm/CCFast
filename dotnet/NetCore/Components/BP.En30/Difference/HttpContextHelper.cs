using Microsoft.AspNetCore.Http;
using System.Text;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization.Formatters.Binary;
using System.IO;
using System.Web;
using System.Text.RegularExpressions;
using BP.DA;

namespace BP.Difference
{
    /// <summary>
    /// HttpContext辅助工具类。
    /// 用于获取当前的HttpContex，包括Session、Cookie等信息。
    /// </summary>
    public static class HttpContextHelper
    {
        public static string GetNameByIdx(int idx)
        {
            IFormFile file = HttpContextHelper.RequestFiles(idx);
            return file.FileName;
        }

        public static IHttpContextAccessor _httpContextAccessor;

        /// <summary>
        /// 获取当前的HttpContext
        /// </summary>
        public static HttpContext Current
        {
            get
            {
                return _httpContextAccessor.HttpContext;
            }
        }
        public static void SetHttpContextAccessor(Microsoft.AspNetCore.Http.HttpContext ctx)
        {
            // _httpContextAccessor.HttpContext = ctx;
            
        }

        /// <summary>
        /// 获取当前的 Session
        /// </summary>
        public static ISession Session
        {
            get
            {
                return Current.Session;
            }
        }

        public static string SessionID
        {
            get
            {
                return Session.Id;
            }
        }

        /// <summary>
        /// 获取当前的 Request
        /// </summary>
        public static HttpRequest Request
        {
            get
            {
                return Current.Request;
            }
        }

        /// <summary>
        /// 获取当前的 Response
        /// </summary>
        public static HttpResponse Response
        {
            get
            {
                return Current.Response;
            }
        }
      
        /// <summary>
        /// 在Startup.Config()中调用此方法，通过DI获取并传入HttpContextAccessor。
        /// <![CDATA[ 写法：using BerryInfo.Web; app.UseStaticHttpContext();]]> 
        /// </summary>
        /// <param name="httpContextAccessor"></param>
        public static void Configure(IHttpContextAccessor httpContextAccessor)
        {
            HttpContextHelper._httpContextAccessor = httpContextAccessor;
        }

        public static void ResponseWrite(string content)
        {
            Response.WriteAsync(content, Encoding.UTF8).Wait();
        }

        public static void ResponseWriteString(string content, Encoding encoding)
        {
            Response.ContentType = "text/html";
            using (StreamWriter sw = new StreamWriter(Response.Body, encoding))
            {
                sw.Write(content);
            }
        }

        public static void ResponseWriteJson(string json, Encoding encoding)
        {
            Response.ContentType = "application/json";
            using (StreamWriter sw = new StreamWriter(Response.Body, encoding))
            {
                sw.Write(json);
            }
        }

        public static void ResponseWriteScript(string script, Encoding encoding)
        {
            Response.ContentType = "application/javascript";
            using (StreamWriter sw = new StreamWriter(Response.Body, encoding))
            {
                sw.Write(script);
            }
        }

        /// <summary>
        /// 向Response中写入文件数据
        /// </summary>
        /// <param name="fileData">文件数据，字节流</param>
        /// <param name="fileName">客户端显示的文件名</param>
        public static void ResponseWriteFile(byte[] fileData, string fileName, string contentType = "application/octet-stream")
        {
            Response.ContentType = String.Format("{0};charset=utf8", contentType);

            // 在Response的Header中设置下载文件的文件名，这样客户端浏览器才能正确显示下载的文件名
            // 注意这里要用HttpUtility.UrlEncode编码文件名，否则有些浏览器可能会显示乱码文件名
            var contentDisposition = "attachment;" + "filename=" + HttpUtility.UrlEncode(fileName, Encoding.UTF8);
            Response.Headers.Add("Content-Disposition", contentDisposition);
            Response.Headers.Add("Code", "OK");
            // 在Response的Header中设置下载文件的大小，这样客户端浏览器才能正确显示下载的进度
            Response.ContentLength = fileData.Length;

            Response.Body.Write(fileData);
            Response.Body.Flush();
        }

        /// <summary>
        /// 将字节写入响应流中
        /// </summary>
        /// <param name="bytes"></param>
        /// <param name="fileName"></param>
        public static void DownLoadFileByBytes(byte[] bytes, String fileName)
        {
            // 清除缓存区流中的所有内容输出
            Response.Clear();
            // 设置缓冲输出为true,后台编辑的文件写到内存流中了
            Response.Headers.Add("Content-Disposition", "attachment; filename=" + HttpUtility.UrlEncode(fileName));
            Response.Headers.Add("Content-Length", "" + bytes.Length);
            Response.ContentType = "application/octet-stream;charset=UTF-8";
            Response.Headers.Add("Code", "OK");
            // 在Response的Header中设置下载文件的大小，这样客户端浏览器才能正确显示下载的进度
            Response.ContentLength = bytes.Length;
            Response.Body.Write(bytes);
            Response.Body.Flush();
        }

        /// <summary>
        /// 向Response中写入文件
        /// </summary>
        /// <param name="filePath">文件的完整路径，含文件名</param>
        /// <param name="clientFileName">客户端显示的文件名。若为空，自动从filePath参数中提取文件名。</param>
        public static void ResponseWriteFile(string filePath, string clientFileName = null, string contentType = "application/octet-stream")
        {
            if (String.IsNullOrEmpty(clientFileName))
                clientFileName = Path.GetFileName(filePath);

            byte[] fileData = null;

            using (FileStream fs = new FileStream(filePath, FileMode.Open, FileAccess.Read))
            {
                fileData = new byte[fs.Length];

                fs.Read(fileData, 0, fileData.Length);
            }

            ResponseWriteFile(fileData, clientFileName, contentType);
        }

        public static void ResponseWriteHeader(string key, string stringvalues)
        {
            Response.Headers.Add(key, stringvalues);
        }

        public static void ResponseClear()
        {

        }

        public static void ResponseAddHeader(string name, string value)
        {
            if (value.Count(c => Convert.ToInt32(c) > 128) > 0)
                value = HttpUtility.UrlEncode(value, Encoding.UTF8);

            Response.Headers.Add(name, value);
        }

        /// <summary>
        /// 添加cookie
        /// </summary>
        /// <param name="cookieValues">Dictionary</param>
        /// <param name="expires"></param>
        /// <param name="cookieName">.net core 中无需传此参数，传了也会被忽略。.net framework中，此参数必填</param>
        public static void ResponseCookieAdd(Dictionary<string, string> cookieValues, DateTime? expires = null, string cookieName = null)
        {
            CookieOptions opt = new CookieOptions();

            if (expires != null)
                opt.Expires = new DateTimeOffset(expires.Value);

            foreach (var d in cookieValues)
                Response.Cookies.Append(d.Key, d.Value, opt);
        }

        /// <summary>
        /// 删除指定的键值的cookie。
        /// </summary>
        /// <param name="cookieKeys"></param>
        /// <param name="cookieName">.net core中无需此参数，传了也会被忽略。net framework中，此参数必填</param>
        public static void ResponseCookieDelete(IEnumerable<string> cookieKeys, string cookieName)
        {
            foreach (var key in cookieKeys)
                Response.Cookies.Delete(key);
        }

        /// <summary>
        /// 获取cookie
        /// </summary>
        /// <param name="key"></param>
        /// <param name="cookieName">.net core中无需此参数，传了也会被忽略。net framework中，此参数必填</param>
        /// <returns></returns>
        public static string RequestCookieGet(string key, string cookieName)
        {
            string cookieStr = Request.Cookies[cookieName];
            string result = string.Empty;
            if (!string.IsNullOrWhiteSpace(cookieStr))
            {
                string[] cookieArray = cookieStr.Split('&');

                foreach (var item in cookieArray)
                {
                    if (item.IndexOf(key) >= 0)
                    {
                        result = item.Replace(key + "=", "");
                        break;
                    }
                }
            }
            else
            {
                if(!DataType.IsNullOrEmpty(key))
                {
                    result = Request.Cookies[key];
                }
            }
            return result;
        }
        public static string GetFormParams(string key)
        {
            //return Request.Form[key];
            return RequestParams(key);
        }
        public static string RequestParams(string key)
        {
            try
            {
                if (Request.Query.ContainsKey(key))
                    return Request.Query[key];

                try // 张磊注：如果Content-Type不是：multipart/form-data 或 application/x-www-form-urlencoded，request.Form会引发异常 InvalidOperationException: Incorrect Content-Type
                {
                    if (Request.Form.ContainsKey(key))
                        return Request.Form[key];
                }
                catch { }

                //if (Request.Cookies.ContainsKey(key))
                //    return Request.Cookies[key]; 
            }
            catch { }

            return null;
        }

        public static List<string> RequestQueryStringKeys
        {
            get
            {
                List<string> keys = new List<string>();
                if (Request.Query.Keys.Count > 0)
                    keys.AddRange(Request.Query.Keys);
                return keys;
            }
        }

        public static List<string> RequestParamKeys
        {
            get
            {
                List<string> keys = new List<string>();
                try
                {
                    if (Request.Query.Keys.Count > 0)
                        keys.AddRange(Request.Query.Keys);

                    try // 张磊注：如果Content-Type不是：multipart/form-data 或 application/x-www-form-urlencoded，request.Form会引发异常 InvalidOperationException: Incorrect Content-Type
                    {
                        if (Request.Form.Keys.Count > 0)
                            keys.AddRange(Request.Form.Keys);
                    }
                    catch { }

                    //if (Request.Cookies.Keys.Count>0)
                    //    keys.AddRange(Request.Cookies.Keys); 
                }
                catch { }
                return keys;
            }
        }

        public static string RequestRawUrl
        {
            get
            {
                return Request.Path + Request.QueryString;
            }
        }

        public static string RequestUrlHost
        {
            get
            {
                return Request.Host.Host;
            }
        }

        public static string RequestApplicationPath
        {
            get
            {
                return Request.Path;
            }
        }

        public static string RequestUrlAuthority
        {
            get
            {
                return Request.Host.Host + ":" + Request.Host.Port;
            }
        }

        public static string RequestQueryString(string key)
        {
            return Request.Query[key];
        }
        public static int RequestFilesCount
        {
            get
            {
                return Current.Request.Form.Files.Count;
            }
        }
        public static long RequestFileLength(int key)
        {
            return Current.Request.Form.Files[key].Length;
        }
        public static long RequestFileLength(IFormFile file)
        {
            return file.Length;
        }
        public static IFormFile RequestFiles(int key)
        {
            return Current.Request.Form.Files[key];
        }
        public static IFormFileCollection RequestFiles()
        {
            return Current.Request.Form.Files;
        }
        public static Stream RequestFileStream(int key)
        {
            return Current.Request.Form.Files[key].OpenReadStream();
        }

        /// <summary>
        /// 文件上传
        /// </summary>
        /// <param name="filePath"></param>
        public static void UploadFile(string filePath)
        {
            try
            {
                IFormFileCollection filelist = RequestFiles();
                if (filelist == null || filelist.Count == 0)
                {
                    throw new NotImplementedException("没有上传文件");
                }
                var f = filelist[0];

                //// 写入文件
                //using (FileStream fs = System.IO.File.Create(filePath))
                //{
                //    f.CopyTo(fs);
                //    fs.Flush();
                //}

                //2023.8.21,重构调用重载方法 by oppein
                UploadFile(f, filePath);

            }
            catch (Exception ex)
            {
                throw new NotImplementedException(ex.Message);
            }
        }
        public static void UploadFile(IFormFile file, string filePath)
        {
            try
            {
                //2023.8.21,解决文件文件目录不存在报错的异常，不存在时先创建 by oppein
                FileInfo saveFileInfo = new FileInfo(filePath);
                string saveDirectory = saveFileInfo.DirectoryName;
                if (!Directory.Exists(saveDirectory))
                {
                    Directory.CreateDirectory(saveDirectory);
                }

                // 写入文件
                using (FileStream fs = System.IO.File.Create(filePath))
                {
                    file.CopyTo(fs);
                    fs.Flush();
                }
            }
            catch (Exception ex)
            {
                throw new NotImplementedException(ex.Message);
            }
        }

        public static string UrlDecode(string Url)
        {
            return HttpUtility.UrlDecode(Url);
        }

        public static void SessionClear()
        {
            Session.Clear();
        }

        public static string SessionGetString(string key)
        {
            return Session.GetString(key);
        }

        /// <summary>
        /// 将键值对添加到Session中
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="session"></param>
        /// <param name="key"></param>
        /// <param name="value"></param>
        public static void SessionSet<T>(string key, T value)
        {
            Session.SetString(key, value as string);// JsonConvert.SerializeObject(value));
        }

        /// <summary>
        /// 根据键，获取Session中值
        /// 注意：使用的JsonConvert进行的序列化，因此其中不包括类型信息。若子类型B的对象b，用其父类型A进行Get，那么会丢失子类型部分的数据。
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="session"></param>
        /// <param name="key"></param>
        /// <returns></returns>
        public static T SessionGet<T>(string key)
        {
            string json = Session.GetString(key);
            return json == null ? default(T) : (T)Convert.ChangeType(json, typeof(T));
                                  //JsonConvert.DeserializeObject<T>(json);
        }

        public static void SessionSet(string key, object value)
        {
            BinaryFormatter bf = new BinaryFormatter();
            MemoryStream ms = new MemoryStream();
            bf.Serialize(ms, value);
            Session.Set(key, ms.ToArray());
        }

        public static object SessionGet(string key)
        {
            BinaryFormatter bf = new BinaryFormatter();
            MemoryStream ms = new MemoryStream(Session.Get(key));
            return bf.Deserialize(ms);
        }

        public static string CurrentSessionID
        {
            get
            {
                return Session.Id;
            }
        }

        /// <summary>
        /// 需要在Startup.cs里面设置
        /// HttpContextHelper.PhysicalApplicationPath=env.ContentRootPath
        /// </summary>
        public static string PhysicalApplicationPath { get; set; }


        public static string RequestUserAgent
        {
            get
            {
                return Current.Request.Headers["User-Agent"];
            }
        }

        // regex from http://detectmobilebrowsers.com/
        private static readonly Regex b = new Regex(@"(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino", RegexOptions.IgnoreCase | RegexOptions.Multiline);
        private static readonly Regex v = new Regex(@"1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-", RegexOptions.IgnoreCase | RegexOptions.Multiline);

        public static bool RequestIsFromMobile
        {
            get
            {
                var userAgent = RequestUserAgent;
                if ((b.IsMatch(userAgent) || v.IsMatch(userAgent.Substring(0, 4))))
                {
                    return true;
                }

                return false;
            }
        }

        public static string RequestBrowser
        {
            get
            {
                return Request.Headers["User-Agent"];
            }
        }

    }
}
