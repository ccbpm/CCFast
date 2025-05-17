using BP.En30.ccportal;
using NPOI.HSSF.UserModel;
using NPOI.SS.UserModel;
using NPOI.XSSF.UserModel;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Odbc;
using System.IO;
using System.Text;

namespace BP.Difference
{
    public class DA_DataType
    {
        public static PortalInterfaceSoapClient GetPortalInterfaceSoapClientInstance()
        {
            return new PortalInterfaceSoapClient(); 
        }
    }

    public class DA_DbLoad
    {
        public static string GenerFirstTableName(string fileName)
        {
            return GenerTableNameByIndex(fileName, 0);
        }
        public static string GenerTableNameByIndex(string fileName, int index)
        {
            String[] excelSheets = GenerTableNames(fileName);
            if (excelSheets != null)
                return excelSheets[index];

            if (excelSheets.Length < index)
                throw new Exception("err@table的索引号错误" + index + "最大索引号为:" + excelSheets.Length);

            return null;
        }
        public static string[] GenerTableNames(string fileName)
        {
            try
            {
                IWorkbook workbook;
                string fileExt = Path.GetExtension(fileName).ToLower();
                using (FileStream fs = new FileStream(fileName, FileMode.Open, FileAccess.Read))
                {
                    //XSSFWorkbook 适用XLSX格式，HSSFWorkbook 适用XLS格式
                    if (fileExt == ".xlsx") { workbook = new XSSFWorkbook(fs); } else if (fileExt == ".xls") { workbook = new HSSFWorkbook(fs); } else { workbook = null; }
                    if (workbook == null) { return null; }
                }
                String[] excelSheets = new String[workbook.NumberOfSheets];
                for (int i = 0; i < workbook.NumberOfSheets; i++)
                {
                    excelSheets[i] = workbook.GetSheetName(i);
                }
                return excelSheets;
            }
            catch (Exception ex)
            {
                throw new Exception("@获取table出错误：" + ex.Message);
            }
        }

        public static DataTable ReadExcelFileToDataTableBySQL(string filePath, string tableName)
        {
            string sql = "SELECT * FROM [" + tableName + "]";
            DataTable dt = new DataTable("dt");

            string typ = System.IO.Path.GetExtension(filePath).ToLower();
            string strConn;
            switch (typ.ToLower())
            {
                case ".xls":
                case ".xlsx":
                    try
                    {
                        return ExcelToTable(filePath, tableName);
                    }
                    catch(Exception ex)
                    {
                        throw new Exception("@读取EXCEL数据失败！" + ex.Message);
                    }
                case ".dbf":
                    strConn = "Driver={Microsoft dBASE Driver (*.DBF)};DBQ=" + System.IO.Path.GetDirectoryName(filePath) + "\\"; //+FilePath;//
                    OdbcConnection conn1 = new OdbcConnection(strConn);
                    OdbcDataAdapter ada1 = new OdbcDataAdapter(sql, conn1);
                    conn1.Open();
                    try
                    {
                        ada1.Fill(dt);
                    }
                    catch//(System.Exception ex)
                    {
                        try
                        {
                            int sel = ada1.SelectCommand.CommandText.ToLower().IndexOf("select") + 6;
                            int from = ada1.SelectCommand.CommandText.ToLower().IndexOf("from");
                            ada1.SelectCommand.CommandText = ada1.SelectCommand.CommandText.Remove(sel, from - sel);
                            ada1.SelectCommand.CommandText = ada1.SelectCommand.CommandText.Insert(sel, " top 10 * ");
                            ada1.Fill(dt);
                            dt.TableName = "error";
                        }
                        catch (System.Exception ex)
                        {
                            conn1.Close();
                            throw new Exception("@读取DBF数据失败！" + ex.Message + " SQL:" + sql);
                        }
                    }
                    conn1.Close();
                    return dt;
                default:
                    break;
            }
            return dt;
        }
        /// <summary>
        /// Excel导入成Datable,其中sheetName为excel中sheet名，如果sheet名不存在，则读取第1个工作表的数据
        /// </summary>
        /// <param name="file">导入路径(包含文件名与扩展名)</param>
        /// <param name="sheetName">excel工作表的名称。若不指定，则默认读取第1个工作表</param>
        /// <param name="beginRowIndex">起始行号</param>
        /// <returns>DataTable</returns>
        public static DataTable ExcelToTable(string file, string sheetName = null, int beginRowIndex = 0)
        {
            DataTable dt = new DataTable();
            IWorkbook workbook;
            string fileExt = Path.GetExtension(file).ToLower();
            using (FileStream fs = new FileStream(file, FileMode.Open, FileAccess.Read))
            {
                //XSSFWorkbook 适用XLSX格式，HSSFWorkbook 适用XLS格式
                if (fileExt == ".xlsx") { workbook = new XSSFWorkbook(fs); } else if (fileExt == ".xls") { workbook = new HSSFWorkbook(fs); } else { workbook = null; }
                if (workbook == null) { return null; }
                ISheet sheet = null;

                // 2019-01-19 zl 简化代码逻辑
                sheet = String.IsNullOrEmpty(sheetName) ? workbook.GetSheetAt(0) : workbook.GetSheet(sheetName);
                dt.TableName = sheet.SheetName;
                
                //表头  
                IRow header = sheet.GetRow(sheet.FirstRowNum + beginRowIndex);
                List<int> columns = new List<int>();
                for (int i = 0; i < header.LastCellNum; i++)
                {
                    object obj = GetValueType(header.GetCell(i));
                    if (obj == null || obj.ToString() == string.Empty)
                    {
                        dt.Columns.Add(new DataColumn("Columns" + i.ToString()));
                    }
                    else
                        dt.Columns.Add(new DataColumn(obj.ToString()));
                    columns.Add(i);
                }
                //数据  
                for (int i = sheet.FirstRowNum + 1 + beginRowIndex; i <= sheet.LastRowNum; i++)
                {
                    DataRow dr = dt.NewRow();
                    bool hasValue = false;
                    foreach (int j in columns)
                    {
                        if (sheet.GetRow(i) != null)
                        {
                            dr[j] = GetValueType(sheet.GetRow(i).GetCell(j));
                            if (dr[j] != null && dr[j].ToString() != string.Empty)
                            {
                                hasValue = true;
                            }
                        }
                    }
                    if (hasValue)
                    {
                        dt.Rows.Add(dr);
                    }
                }
            }
            return dt;
        }

        /// <summary>
        /// 获取单元格类型
        /// </summary>
        /// <param name="cell"></param>
        /// <returns></returns>
        private static object GetValueType(ICell cell)
        {
            if (cell == null)
                return null;
            switch (cell.CellType)
            {
                case CellType.Boolean:
                    return cell.BooleanCellValue;
                // 2019-01-19 NPOI中数字和日期都是NUMERIC类型的
                case CellType.Numeric:
                    if (DateUtil.IsCellDateFormatted(cell)) // 日期格式
                        return cell.DateCellValue;
                    else
                        return cell.NumericCellValue;
                case CellType.String:
                    return cell.StringCellValue;
                case CellType.Error:
                    return cell.ErrorCellValue;
                case CellType.Formula:
                    return "=" + cell.CellFormula;
                case CellType.Blank:
                default:
                    return null;
            }
        }
    }
}
namespace BP.En30.ccportal
{
    public class PortalInterfaceSoapClient
    {
        public int CheckUserNoPassWord(string userNo, string password)
        {
            throw new NotImplementedException(".net core版本不支持");
        }
        public System.Data.DataTable GetDept(string deptNo)
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GetDepts()
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GetDeptsByParentNo(string parentDeptNo)
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GetStations()
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GetStation(string stationNo)
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GetEmps()
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GetEmpsByDeptNo(string deptNo)
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GetEmp(string no)
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GetDeptEmp()
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GetEmpHisDepts(string empNo)
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GetEmpHisStations(string empNo)
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GetDeptEmpStations()
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GenerEmpsByStations(string stationNos)
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GenerEmpsByDepts(string deptNos)
        {
            throw new NotImplementedException(".net core版本不支持");
        }

        public System.Data.DataTable GenerEmpsBySpecDeptAndStats(string deptNo, string stations)
        {
            throw new NotImplementedException(".net core版本不支持");
        }
    }
}

