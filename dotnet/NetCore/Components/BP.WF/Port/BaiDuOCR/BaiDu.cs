using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using BP.Tools;

namespace BP.WF.Port.BaiDuOCR
{
    public class BaiDu
    {
        /// <summary>
        /// 获取百度APIKey
        /// </summary>
        public static string BaiDu_APIKey
        {
            get
            {
                return BP.Difference.SystemConfig.BaiDu_APIKey;
            }
        }
        ///// <summary>
        ///// 获取百度SecretKey
        ///// </summary>
        public static string BaiDu_SecretKey
        {
            get
            {
                return BP.Difference.SystemConfig.BaiDu_SecretKey;
            }
        }
        /// <summary>
        /// 获取百度云token
        /// </summary>
        /// <returns></returns>
        public static string getAccessToken()
        {
            //百度云应用获取token
            string authHost = "https://aip.baidubce.com/oauth/2.0/token";
            HttpClient client = new HttpClient();
            List<KeyValuePair<string, string>> paraList = new List<KeyValuePair<string, string>>();
            paraList.Add(new KeyValuePair<string, string>("grant_type", "client_credentials"));
            paraList.Add(new KeyValuePair<string, string>("client_id", BaiDu_APIKey));
            paraList.Add(new KeyValuePair<string, string>("client_secret", BaiDu_SecretKey));

            HttpResponseMessage response = client.PostAsync(authHost, new FormUrlEncodedContent(paraList)).Result;
            string result = response.Content.ReadAsStringAsync().Result;
            Console.WriteLine(result);

            return result;
        }
        /// <summary>
        /// 根据ip地质获取访问者信息
        /// </summary>
        /// <param name="ip"></param>
        /// <returns></returns>
        public static BaiDuMap getMapLocation(string ip)
        {
            try
            {
                //调用百度地图api获取访问信息
                string url = "http://api.map.baidu.com/location/ip?ak=rgwS2tQzfT9dX21CvZkyTE2eQ1D0vDWh&ip=" + ip;
                string str = BP.DA.DataType.ReadURLContext(url, 5000, Encoding.UTF8);
                //增加日志，便于以后项目检查白名单
                BP.DA.Log.DebugWriteError("baiDuMap:" + str);
                //反序列化
                BaiDuMap baiDuMap = FormatToJson.ParseFromJson<BaiDuMap>(str);
                return baiDuMap;
            }
            catch (Exception ex) {
                BP.DA.Log.DebugWriteError("百度地图接口调用错误:" + ex.Message);
                return null;
            }
        }
    }
}
