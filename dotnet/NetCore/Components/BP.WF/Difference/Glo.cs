using System;
using System.Collections.Generic;
using System.ServiceModel;
using System.Security.Cryptography;
using System.Text;

namespace BP.WF.Difference
{
    public class Glo
    {
        public static string Sha1Signature(string str)
        {

            // BY 20210720 Core中不存在System.Web HashPasswordForStoringInConfigFile 已过时，使用替代方法
            //string s = System.Web.Security.FormsAuthentication.HashPasswordForStoringInConfigFile(str, "SHA1").ToString();
            //string s = BP.WF.Difference.CCMobile_CCForm.GetSHA1(str);
            //return s.ToLower();

            //两种验证都可以尝试
            //1
            using (var md5 = MD5.Create())
            {
                var s = md5.ComputeHash(Encoding.UTF8.GetBytes(str));
                var strResult = BitConverter.ToString(s);
                return strResult.Replace("-", "").ToLower();
            }

            //2
            //SHA1 sha = SHA1.Create();
            //byte[] bytResult = sha.ComputeHash(System.Text.Encoding.UTF8.GetBytes(str));
            //return BitConverter.ToString(bytResult).Replace("-", "");

        }
    }

}
