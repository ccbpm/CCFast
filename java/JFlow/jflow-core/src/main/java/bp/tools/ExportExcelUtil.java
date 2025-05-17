package bp.tools;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

import bp.da.*;
import bp.en.*;
import bp.sys.*;
import bp.web.*;

public class ExportExcelUtil
{
	private static final String tab = "\t"; //对应C#中的 Convert.ToChar(9)
	public static String ExportDGToExcel(DataSet ds, String title, String paras) throws Exception {
		DataTable dt = ds.GetTableByName("GroupSearch");
		DataTable AttrsOfNum = ds.GetTableByName("AttrsOfNum");
		DataTable AttrsOfGroup = ds.GetTableByName("AttrsOfGroup");

		title = title.trim();
		String filename = title + "Ep" + title + ".xls";
		String file = filename;
		boolean flag = true;
		String filepath = bp.difference.SystemConfig.getPathOfTemp();

		///#region 参数及变量设置

		if ((new File(filepath)).isDirectory() == false)
		{
			(new File(filepath)).mkdirs();
		}



		filename = filepath + filename;

		//filename = HttpUtility.UrlEncode(filename);

		FileOutputStream objFileStream = new FileOutputStream(filename);
		OutputStreamWriter objStreamWriter = new OutputStreamWriter(objFileStream, java.nio.charset.StandardCharsets.UTF_16LE);
		///#endregion

		///#region 生成导出文件
		try
		{
			
			objStreamWriter.write(tab + title + tab + System.lineSeparator());
			String strLine = "序号" + tab;
			//生成文件标题
			for (DataRow attr : AttrsOfGroup.Rows)
			{
				strLine = strLine + attr.get("Name") + tab;
			}
			for (DataRow attr : AttrsOfNum.Rows)
			{
				strLine = strLine + attr.get("Name") + tab;
			}

			objStreamWriter.write(strLine + System.lineSeparator());
			strLine = "";
			for (DataRow dr : dt.Rows)
			{
				strLine = strLine + dr.get("IDX") + tab;
				for (DataRow attr : AttrsOfGroup.Rows)
				{
					strLine = strLine + dr.get(attr.get("KeyOfEn") + "T") + tab;

				}
				for (DataRow attr : AttrsOfNum.Rows)
				{

					strLine = strLine + dr.get(attr.get("KeyOfEn").toString()) + tab;
				}

				objStreamWriter.write(strLine + System.lineSeparator());
				strLine = "";
			}

			strLine = "汇总" + tab;
			for (DataRow attr : AttrsOfGroup.Rows)
			{

				strLine = strLine + "" + tab;
			}

			for (DataRow attr : AttrsOfNum.Rows)
			{
				double d = 0;
				for (DataRow dtr : dt.Rows)
				{
					d += Double.parseDouble(dtr.get(attr.get("KeyOfEn").toString()).toString());
				}
				if (paras.contains(attr.get("KeyOfEn") + "=AVG"))
				{
					if (dt.Rows.size() != 0)
					{
						DecimalFormat df = new DecimalFormat("#.0000");
						d = Double.valueOf(df.format(d/dt.Rows.size()));
					}

				}

				if (Integer.parseInt(attr.get("MyDataType").toString()) == DataType.AppInt)
				{
					if (paras.contains(attr.get("KeyOfEn") + "=AVG"))
					{
						strLine = strLine + d + tab;
					}
					else
					{
						strLine = strLine + (int)d + tab;
					}
				}
				else
				{
					strLine = strLine + d + tab;
					;
				}

			}

			objStreamWriter.write(strLine + System.lineSeparator());
			strLine = "";
		}
		catch (java.lang.Exception e)
		{
			flag = false;
		}
		finally
		{
			objStreamWriter.close();
			objFileStream.close();
		}
		///#endregion

		///#region 删除掉旧的文件
		//DelExportedTempFile(filepath);
		///#endregion

		if (flag)
		{
			file = "/DataUser/Temp/" + file;

		}

		return file;
	}

	public static String ExportDGToExcel(DataTable dt, Entity en, String title, Attrs mapAttrs) throws Exception {
		return ExportDGToExcel(dt, en, title, mapAttrs, null);
	}

	public static String ExportDGToExcel(DataTable dt, Entity en, String title) throws Exception {
		return ExportDGToExcel(dt, en, title, null, null);
	}

	public static String ExportDGToExcel(DataTable dt, Entity en, String title, Attrs mapAttrs, String filename) throws Exception {
		if (filename == null)
		{
			filename = title + "_" + DataType.getCurrentDateCNOfLong() + "_" + WebUser.getNo() + ".xls";
		}
		String file = filename;
		boolean flag = true;
		String filepath = bp.difference.SystemConfig.getPathOfTemp();

		///#region 参数及变量设置


		//如果导出目录没有建立，则建立.
		if ((new File(filepath)).isDirectory() == false)
		{
			(new File(filepath)).mkdirs();
		}

		filename = filepath + filename;

		if ((new File(filename)).isFile())
		{
			(new File(filename)).delete();
		}

		FileOutputStream objFileStream = new FileOutputStream(filename);
		OutputStreamWriter objStreamWriter = new OutputStreamWriter(objFileStream, java.nio.charset.StandardCharsets.UTF_16LE);
		///#endregion

		///#region 生成导出文件
		try
		{
			Attrs attrs = null;
			if (mapAttrs != null)
			{
				attrs = mapAttrs;
			}
			else
			{
				attrs = en.getEnMap().getAttrs();
			}

			Attrs selectedAttrs = null;
			bp.sys.UIConfig cfg = new UIConfig(en);

			if (cfg.getShowColumns().length == 0)
			{
				selectedAttrs = attrs;
			}
			else
			{
				selectedAttrs = new Attrs();

				for (Attr attr : attrs)
				{
					boolean contain = false;

					for (String col : cfg.getShowColumns())
					{
						if (Objects.equals(col, attr.getKey()))
						{
							contain = true;
							break;
						}
					}

					if (contain)
					{
						selectedAttrs.Add(attr);
					}
				}
			}

			objStreamWriter.write(System.lineSeparator());
			objStreamWriter.write(tab + title + tab + System.lineSeparator());
			objStreamWriter.write(System.lineSeparator());
			String strLine = "";

			//添加标签，解决数字在excel中变为科学计数法问题
			strLine = "<table cellspacing=\"0\" cellpadding=\"5\" rules=\"all\" border=\"1\"> ";
			strLine += "<tr>";
			//生成文件标题
			for (Attr attr : selectedAttrs)
			{
				if (attr.getKey().equals("OID"))
				{
					continue;
				}

				if (attr.getKey().equals("WorkID"))
				{
					continue;
				}

				if (attr.getKey().equals("MyNum"))
				{
					continue;
				}

				if (attr.getItIsFKorEnum())
				{
					continue;
				}

				if (attr.getUIVisible() == false && attr.getMyFieldType() != FieldType.RefText)
				{
					continue;
				}

				if (attr.getKey().equals("MyFilePath") || attr.getKey().equals("MyFileExt") || attr.getKey().equals("WebPath") || attr.getKey().equals("MyFileH") || attr.getKey().equals("MyFileW") || attr.getKey().equals("MyFileSize") || attr.getKey().equals("RefPK"))
				{
					continue;
				}

				if (attr.getMyFieldType() == FieldType.RefText)
				{
					strLine += "<td>" + attr.getDesc().replace("名称", "") + tab + "</td>";
				}
				else
				{
					strLine += "<td>" + attr.getDesc() + tab + "</td>";
				}
			}
			strLine += "</tr>";
			objStreamWriter.write(strLine + System.lineSeparator());
			strLine = "";

			for (DataRow dr : dt.Rows)
			{
				strLine = "</tr>";
				for (Attr attr : selectedAttrs)
				{
					if (attr.getItIsFKorEnum())
					{
						continue;
					}

					if (attr.getUIVisible() == false && attr.getMyFieldType() != FieldType.RefText)
					{
						continue;
					}

					if (attr.getKey().equals("OID"))
					{
						continue;
					}

					if (attr.getKey().equals("MyNum"))
					{
						continue;
					}

					if (attr.getKey().equals("WorkID") == true)
					{
						continue;
					}

					if (attr.getKey().equals("MyFilePath") || attr.getKey().equals("MyFileExt") || attr.getKey().equals("WebPath") || attr.getKey().equals("MyFileH") || attr.getKey().equals("MyFileW") || attr.getKey().equals("MyFileSize") || attr.getKey().equals("RefPK"))
					{
						continue;
					}
					if (dt.Columns.contains(attr.getKey()) == false)
					{
						continue;
					}


					if (attr.getMyDataType() == DataType.AppBoolean)
					{
						strLine += "<td>" + (dr.get(attr.getKey()).equals(1) ? "是" : "否") + tab + "</td>";
					}
					else
					{
						String text = "";
						if (attr.getItIsFKorEnum() || attr.getItIsFK())
						{
							text = dr.get(attr.getKey() + "Text").toString();
						}
						else if (dt.Columns.contains(attr.getKey() + "T") == true)
						{
							text = dr.get(attr.getKey() + "T").toString();
						}
						else
						{
							text = dr.get(attr.getKey()).toString();
						}

						if (attr.getKey().equals("FK_NY") && DataType.IsNullOrEmpty(text) == true)
						{
							text = dr.get(attr.getKey()).toString();
						}
						if (DataType.IsNullOrEmpty(text) == false && (text.contains("\n") == true || text.contains("\r") == true))
						{
							text = text.replace("\n", " ");
							text = text.replace("\r", " ");
						}
						strLine += "<td style=\"vnd.ms-excel.numberformat:@\">" + text + " " + tab + "</td>";
					}
				}
				strLine += "</tr>";
				objStreamWriter.write(strLine + System.lineSeparator());
				strLine = "";
			}


			objStreamWriter.write(System.lineSeparator());
			objStreamWriter.write(tab + " 制表人：" + tab + WebUser.getName() + tab + "日期：" + tab + DataType.getCurrentDateByFormart("MM/dd/yyyy") + System.lineSeparator());

		}
		catch (RuntimeException e)
		{
			flag = false;
			throw new RuntimeException("数据导出有问题," + e.getMessage());
		}
		finally
		{
			objStreamWriter.close();
			objFileStream.close();
		}
		///#endregion

		///#region 删除掉旧的文件
		//DelExportedTempFile(filepath);
		///#endregion

		if (flag)
		{
			file = "/DataUser/Temp/" + file;
			//this.Write_Javascript(" window.open('"+ Request.ApplicationPath + @"/Report/Exported/" + filename +"'); " );
			//this.Write_Javascript(" window.open('"+Request.ApplicationPath+"/Temp/" + file +"'); " );
		}

		return file;
	}



}

