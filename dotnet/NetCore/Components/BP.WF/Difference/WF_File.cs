using System;
using System.IO;
using System.Web;
using BP.Difference;
using Microsoft.AspNetCore.Http;


namespace BP.WF.Difference
{
    public class WF_File
    {
        public static void GetFileName(int idx)
        {
        }
        /// <summary>
        /// 文件上传
        /// </summary>
        /// <param name="filePath"></param>
        public static void UploadFile(string filePath)
        {
            //2023.8.21,解决文件文件目录不存在报错的异常，不存在时先创建 by oppein
            FileInfo saveFileInfo = new FileInfo(filePath);
            string saveDirectory = saveFileInfo.DirectoryName;
            if (!Directory.Exists(saveDirectory))
            {
                Directory.CreateDirectory(saveDirectory);
            }

            try
            {
                var filelist = HttpContextHelper.Current.Request.Form.Files;
                if (filelist == null || filelist.Count == 0)
                {
                    throw new NotImplementedException("没有上传文件");
                }
                IFormFile f = filelist[0];
                // 写入文件

                var stream = new FileStream(filePath, FileMode.Create);
                f.CopyTo(stream);
            }
            catch (Exception ex)
            {
                throw new NotImplementedException(ex.Message);
            }
        }
        /// <summary>
        /// 获取文件
        /// </summary>
        /// <returns></returns>
        public static IFormFile GetUploadFile(int index = 0)
        {
            try
            {
                var filelist = HttpContextHelper.Current.Request.Form.Files;
                if (filelist == null || filelist.Count == 0)
                {
                    throw new NotImplementedException("没有上传文件");
                }
                return filelist[index];
            }
            catch (Exception ex)
            {
                throw new NotImplementedException(ex.Message);
            }
        }
        public static IFormFileCollection GetUploadFiles()
        {
            try
            {
                var filelist = HttpContextHelper.Current.Request.Form.Files;
                if (filelist == null || filelist.Count == 0)
                {
                    throw new NotImplementedException("没有上传文件");
                }
                return filelist;
            }
            catch (Exception ex)
            {
                throw new NotImplementedException(ex.Message);
            }
        }
        public static void UploadFile(FormFile file, string filePath)
        {
            try
            {
                // 写入文件
                var stream = new FileStream(filePath, FileMode.Create);
                file.CopyTo(stream);
            }
            catch (Exception ex)
            {
                throw new NotImplementedException(ex.Message);
            }
        }
        public static long GetFileLength(FormFile file)
        {
            try
            {
                return file.Length;
            }
            catch (Exception ex)
            {
                throw new NotImplementedException(ex.Message);
            }
        }
    }
}
