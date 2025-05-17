using System;
using System.Data;
using System.Text;
using BP.DA;
using BP.Sys;
using BP.Web;
using BP.Difference;
using BP.En;
using NPOI.SS.Formula.Functions;
using System.Collections.Generic;
using ICSharpCode.SharpZipLib.BZip2;
using ICSharpCode.SharpZipLib.Zip;
using System.IO;

namespace BP.App
{
    /// <summary>
    /// 页面功能实体
    /// </summary>
    public class Handler_ERP : BP.WF.HttpHandler.DirectoryPageBase
    {
        /// <summary>
        /// 构造函数
        /// </summary>
        public Handler_ERP()
        {
        }
        public string ImpZipAD()
        {
            var files = HttpContextHelper.RequestFiles();
            string ext = ".zip";
            string fileName = System.IO.Path.GetFileName(files[0].FileName);
            if (fileName.Contains(".zip"))
                ext = ".zip";
            //设置文件名
            string zipFile = DateTime.Now.ToString("yyyyMMddHHmmssff") + ext;
            //文件存放路径
            zipFile = BP.Difference.SystemConfig.PathOfTemp + zipFile;
            HttpContextHelper.UploadFile(files[0], zipFile);

            //创建临时目录.
            string tempDir = BP.Difference.SystemConfig.PathOfTemp + "ZipAD/";
            if (System.IO.Directory.Exists(tempDir) == false)
                System.IO.Directory.CreateDirectory(tempDir);

            //解开压缩到文件, 到  tempDir ,没有实现.
             //(new FastZip()).ExtractZip(zipFile, tempDir, FastZip.Overwrite.Always);

            //获得excel文件名称.
            System.IO.DirectoryInfo dir = new System.IO.DirectoryInfo(tempDir);
            FileInfo[] fls = dir.GetFiles();
            DirectoryInfo[] dirs = dir.GetDirectories();
            if (fls.Length == 0)
                return "err@文件不符合约定的格式，没有找到根目录下的文件.";
            FileInfo file = fls[0]; //获得索引文件.excel.

            //文件存放路径.
            DataTable dt = BP.DA.DBLoad.ReadExcelFileToDataTable(file.FullName);
            foreach (DataRow dr in dt.Rows)
            {
                string? idx = dr["序号"].ToString();
                string? name = dr["名称"].ToString();

                // string? adName = dr["名称"].ToString();
                // 生成workid.
                Int64 workID = BP.WF.Dev2Interface.Node_CreateBlankWork("005");
                GEEntityOID en = new GEEntityOID("ND501", workID);
                //通用匹配赋值
                foreach (DataColumn dc in dt.Columns)
                    en.SetValByDesc(dc.ColumnName, dr[dc.ColumnName]);

                //个性化处理赋值.
                string? splx = dr["商品类型"].ToString();
                string splxNo= DBAccess.RunSQLReturnStringIsNull("SELET No FROM XS_XXX WHERE Name='"+splx+"'", splx);
                en.SetValByKey("SQL_ShangPinLeiXing", splxNo);
                en.SetValByKey("SQL_ShangPinLeiXingT", splx);

                en.Update();//更新到数据库.

                //获得附件.
                foreach (DirectoryInfo dirAD in dirs)
                {
                    if (dirAD.FullName.Equals(idx + ".") == true)
                    {
                        //获得该目录下的文件.
                        FileInfo[] adFils = dirAD.GetFiles();
                        foreach (FileInfo itemFileInfo in adFils)
                        {
                            //上传文件.
                            BP.WF.Dev2Interface.CCForm_AddAth(501, "005", workID, "ShiPinTuPian", "ND501", itemFileInfo.FullName, itemFileInfo.Name);
                        }
                    }
                    //设置待办.
                    BP.WF.Dev2Interface.Node_SetDraft2Todolist(workID);
                    //执行发送.
                    BP.WF.Dev2Interface.Node_SendWork("005", workID);
                }
            }
            return "info@导入了:" + dt.Rows.Count + "笔数据.";
        }
        public TSEntitiesNoName ImpADEntity(DataTable dt)
        {
            string cls = "TS.AD.XS";
            string msg = "";

            TSEntityNoName xxs = new TSEntityNoName(cls);

            //导入的数据存储到这里.
            TSEntitiesNoName ads = (TSEntitiesNoName)xxs.GetNewEntities;

            foreach (DataRow dr in dt.Rows)
            {
                TSEntityNoName xs = new TSEntityNoName(cls);
                xs.No = dr[0].ToString();
                if (xs.IsExits == true)
                {
                    msg += " err@编号:" + xs.No + "," + xs.Name + ",已经存在.";
                    continue;
                }

                xs.Name = dr[1].ToString();
                string? wfsc = dr["违法时长"] as string; //违法时长
                xs.SetValByKey("ShiChang", wfsc);

                string? splx = dr["商品类型"] as string; //违法时长
                string splxNo = DBAccess.RunSQLReturnString("SELECT No FROM AD_SPLX WHERE Name='" + splx?.Trim() + "'");
                if (splxNo == null)
                    splxNo = "001";
                xs.SetValByKey("SPLX", splxNo);

                string? mtmc = dr["媒体名称"] as string; //违法时长
                string mtmcNo = DBAccess.RunSQLReturnString("SELECT No FROM AD_MeiTi WHERE Name='" + mtmc?.Trim() + "'");
                if (mtmcNo == null)
                    mtmcNo = "3701001";
                xs.SetValByKey("MeiTi", mtmcNo);


                string? diQu = dr["地区"] as string; //违法时长
                string diQuNo = DBAccess.RunSQLReturnString("SELECT No FROM cn_diqu WHERE Name='" + diQu?.Trim() + "'");
                if (diQuNo == null)
                    diQuNo = "370100";
                xs.SetValByKey("DiQu", diQuNo);

                xs.SetValByKey("QuXian", diQuNo.Replace("00", "") + "01");
                xs.SetValByKey("XiangZhen", diQuNo.Replace("00", "") + "0101");
                xs.Insert();

                ads.AddEntity(xs);

                msg += "info@数据[" + xs.No + xs.Name + "]已经导入.";
            }
            return ads;

        }


        public string ImpAD()
        {
            var files = HttpContextHelper.RequestFiles();
            string ext = ".xls";
            string fileName = System.IO.Path.GetFileName(files[0].FileName);
            if (fileName.Contains(".xlsx"))
                ext = ".xlsx";

            //设置文件名
            string fileNewName = DateTime.Now.ToString("yyyyMMddHHmmssff") + ext;

            //文件存放路径
            string filePath = BP.Difference.SystemConfig.PathOfTemp + fileNewName;
            HttpContextHelper.UploadFile(files[0], filePath);

            DataTable dtDept = BP.DA.DBLoad.ReadExcelFileToDataTable(filePath);

            string cls = "TS.AD.XS";
            string msg = "";
            foreach (DataRow dr in dtDept.Rows)
            {
                TSEntityNoName xs = new TSEntityNoName(cls);
                xs.No = dr[0].ToString();
                if (xs.IsExits == true)
                {
                    msg += " err@编号:" + xs.No + "," + xs.Name + ",已经存在.";
                    continue;
                }

                xs.Name = dr[1].ToString();
                string? wfsc = dr["违法时长"] as string; //违法时长
                xs.SetValByKey("ShiChang", wfsc);

                string? splx = dr["商品类型"] as string; //违法时长
                string splxNo = DBAccess.RunSQLReturnString("SELECT No FROM AD_SPLX WHERE Name='" + splx?.Trim() + "'");
                if (splxNo == null)
                    splxNo = "001";
                xs.SetValByKey("SPLX", splxNo);

                string? mtmc = dr["媒体名称"] as string; //违法时长
                string mtmcNo = DBAccess.RunSQLReturnString("SELECT No FROM AD_MeiTi WHERE Name='" + mtmc?.Trim() + "'");
                if (mtmcNo == null)
                    mtmcNo = "3701001";
                xs.SetValByKey("MeiTi", mtmcNo);


                string? diQu = dr["地区"] as string; //违法时长
                string diQuNo = DBAccess.RunSQLReturnString("SELECT No FROM cn_diqu WHERE Name='" + diQu?.Trim() + "'");
                if (diQuNo == null)
                    diQuNo = "370100";
                xs.SetValByKey("DiQu", diQuNo);

                xs.SetValByKey("QuXian", diQuNo.Replace("00", "") + "01");
                xs.SetValByKey("XiangZhen", diQuNo.Replace("00", "") + "0101");
                xs.Insert();

                msg += "info@数据[" + xs.No + xs.Name + "]已经导入.";
            }
            return msg;
        }
    }


}
