package bp.da;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.*;

/**
 * DBLoad 的摘要说明。
 * 支持2007(包括)以上版本
 */
public class DBLoad
{
	static
	{
	}

	public static class XSSFDateUtil extends DateUtil {
		protected static int absoluteDay(Calendar cal, boolean use1904windowing) {
			return DateUtil.absoluteDay(cal, use1904windowing);
		}
	}

	public static int ImportTableInto(DataTable impTb, String intoTb,
									  String select, int clear)
	{
		int count = 0;

		return count;
	}


	public static String GenerFirstTableName(String fileName)
	{
		return GenerTableNameByIndex(fileName, 0);
	}

	public static String GenerTableNameByIndex(String fileName, int index)
	{
		String[] excelSheets = GenerTableNames(fileName);
		if (excelSheets != null && excelSheets.length >= index)
		{
			return excelSheets[index];
		}
		return null;
	}

	public static String[] GenerTableNames(String fileName)
	{
		try (Workbook workbook = WorkbookFactory.create(new FileInputStream(fileName))) {
			int numberOfSheets = workbook.getNumberOfSheets();
			String[] sheetNames = new String[numberOfSheets];
			for (int i = 0; i < numberOfSheets; i++) {
				String sheetName = workbook.getSheetName(i);
				System.out.println("Sheet Name: " + sheetName);
				sheetNames[i] = sheetName;
			}
			return sheetNames;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 得到Excel表中的值
	 *
	 * @param cell
	 *            Excel中的每一个格子
	 * @return Excel中每一个格子中的值
	 */
	private static String getValue(Cell cell){
		if (cell.getCellType() == CellType.BOOLEAN)
		{
			// 返回布尔类型的值
			return String.valueOf(cell.getBooleanCellValue());
		}
		if(cell.getCellType() == CellType.NUMERIC){
			// 获取单元格的样式值，即获取单元格格式对应的数值
			int style = cell.getCellStyle().getDataFormat();
			// 判断是否是日期格式
			if (XSSFDateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				// 对不同格式的日期类型做不同的输出，与单元格格式保持一致
				switch (style) {
					case 178:
						return new SimpleDateFormat("yyyy'年'M'月'd'日'").format(date);

					case 14:
						return new SimpleDateFormat("yyyy/MM/dd").format(date);

					case 179:
						return new SimpleDateFormat("yyyy/MM/dd HH:mm").format(date);

					case 181:
						return new SimpleDateFormat("yyyy/MM/dd HH:mm a ").format(date);

					case 22:
						return new SimpleDateFormat(" yyyy/MM/dd HH:mm:ss ").format(date);

					default:
						break;
				}
			} else {
				switch (style) {
					// 单元格格式为百分比，不格式化会直接以小数输出
					case 9:
						return  new DecimalFormat("0.00%").format(cell.getNumericCellValue());

					// DateUtil判断其不是日期格式，在这里也可以设置其输出类型
					case 57:
						return new SimpleDateFormat(" yyyy'年'MM'月' ").format(cell.getDateCellValue());

					default:
						cell.setCellType(CellType.STRING);
						return String.valueOf(cell.getStringCellValue());
				}
			}


		}
		//其余的格式设置成String，返回String
		cell.setCellType(CellType.STRING);
		// 返回字符串类型的值
		return String.valueOf(cell.getStringCellValue());
	}


	@SuppressWarnings("resource")
	public static DataTable ReadExcelFileToDataTable(InputStream is)throws Exception
	{
		DataTable Tb = new DataTable("Tb");
		Tb.Rows.clear();
		DataColumnCollection collection = new DataColumnCollection(Tb);
		Workbook wb = null;

		try
		{
			wb = WorkbookFactory.create(is);

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// 循环工作表Sheet , 目前支持一个
		// for (int i = 0; i < hssfWorkbook.getNumberOfSheets(); i++) {
		Sheet sheet = wb.getSheetAt(0);
		if (sheet == null)
		{
			return null;
		}
		// 循环行Row
		int row_size = sheet.getPhysicalNumberOfRows();
		for (int j = 0; j < row_size; j++)
		{
			Row row = sheet.getRow(j);
			if (row == null)
			{
				continue;
			}

			// 循环列Cell
			int call_num = row.getPhysicalNumberOfCells();
			// title
			if (0 == j)
			{
				for (int k = 0; k < call_num; k++)
				{
					Cell cell = row.getCell(k);
					if (null == cell)
					{
						continue;
					}
					DataColumn column = new DataColumn(getValue(cell));
					collection.Add(column);
				}
			} else
			{ // 内容
				DataRow dataRow = new DataRow(Tb);
				for (int k = 0; k < call_num; k++)
				{
					Cell cell = row.getCell(k);
					if (null == cell)
					{
						continue;
					}
					dataRow.setValue(collection.get(k), getValue(cell));
				}
				Tb.Rows.add(dataRow);
			}

		}
		Tb.Columns = collection;
		return Tb;
	}
	public static DataTable ReadExcelFileToDataTable(InputStream is, int sheetIndex)throws Exception
	{
		DataTable Tb = new DataTable("Tb");
		Tb.Rows.clear();
		DataColumnCollection collection = new DataColumnCollection(Tb);
		Workbook wb = null;

		try
		{
			wb = WorkbookFactory.create(is);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// 循环工作表Sheet , 目前支持一个
		// for (int i = 0; i < hssfWorkbook.getNumberOfSheets(); i++) {
		Sheet xssfSheet = wb.getSheetAt(sheetIndex);
		if (xssfSheet == null)
		{
			return null;
		}
		// 循环行Row
		int row_size = xssfSheet.getPhysicalNumberOfRows();
		int call_num=0;
		for (int j = 0; j < row_size; j++)
		{
			Row xssfRow = xssfSheet.getRow(j);
			if (xssfRow == null)
			{
				continue;
			}

			// title
			if (0 == j)
			{
				call_num = xssfRow.getPhysicalNumberOfCells();
				for (int k = 0; k < call_num; k++)
				{
					Cell xssfCell = xssfRow.getCell(k);
					String cellstr="";
					if (null != xssfCell)
					{
						cellstr=getValue(xssfCell);
					}
					DataColumn column = new DataColumn(cellstr.replace(" ","").replace("*",""));
					collection.Add(column);
				}
			} else
			{ // 内容
				DataRow dataRow = new DataRow(Tb);
				for (int k = 0; k < call_num; k++)
				{
					Cell xssfCell = xssfRow.getCell(k);
					String cellstr="";
					if (null != xssfCell)
					{
						cellstr=getValue(xssfCell);
					}
					dataRow.setValue(collection.get(k), cellstr);
					dataRow.columns = collection;
				}
				Tb.Rows.add(dataRow);
			}

		}
		Tb.Columns = collection;
		return Tb;
	}
	public static DataTable ReadExcelFileToDataTable(String fileFullName, int sheetIdx)
	{
		//String tableName = GenerTableNameByIndex(fileFullName, sheetIdx);
		return ReadExcelFileToDataTableBySQL(fileFullName, sheetIdx);
	}
	public static DataTable ReadExcelFileToDataTableBySQL(String filePath, int sheetIdx)
	{
		FileInputStream stream = null;
		DataTable dataTable = new DataTable();
		try
		{
			stream = new FileInputStream(filePath);
			dataTable = ReadExcelFileToDataTable(stream, sheetIdx);

		} catch (Exception e)
		{
			e.printStackTrace();
		}finally{
			if(stream!=null){
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return dataTable;
	}

	public static DataTable ReadExcelFileToDataTable(String filePath)throws Exception
	{
		FileInputStream stream = null;
		DataTable dataTable = null;
		try
		{
			stream = new FileInputStream(filePath);
			dataTable = ReadExcelFileToDataTable(stream);

		} catch (IOException e)
		{
			e.printStackTrace();
		}finally{
			if(stream!=null){
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return dataTable;

	}

	@SuppressWarnings("resource")
	public static DataTable GetTableByExt(InputStream is) throws IOException {
		DataTable Tb = new DataTable("Tb");
		Tb.Rows.clear();
		DataColumnCollection collection = new DataColumnCollection(Tb);


		/*try
		{
			XSSFWorkbook xssfWorkbook = null;
			xssfWorkbook = new XSSFWorkbook(is);
			// 循环工作表Sheet , 目前支持一个
			// for (int i = 0; i < hssfWorkbook.getNumberOfSheets(); i++) {
			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
			if (xssfSheet == null)
			{
				return null;
			}
			// 循环行Row
			int row_size = xssfSheet.getPhysicalNumberOfRows();
			for (int j = 0; j < row_size; j++)
			{
				XSSFRow xssfRow = xssfSheet.getRow(j);
				if (xssfRow == null)
				{
					continue;
				}

				// 循环列Cell
				int call_num = xssfRow.getPhysicalNumberOfCells();
				// title
				if (0 == j)
				{
					for (int k = 0; k < call_num; k++)
					{
						XSSFCell xssfCell = xssfRow.getCell(k);
						if (null == xssfCell)
						{
							continue;
						}
						DataColumn column = new DataColumn(getValue(xssfCell));
						collection.Add(column);
					}
				} else
				{ // 内容
					DataRow dataRow = new DataRow(Tb);
					for (int k = 0; k < call_num; k++)
					{
						XSSFCell xssfCell = xssfRow.getCell(k);
						if (null == xssfCell)
						{
							continue;
						}
						dataRow.setValue(collection.get(k), getValue(xssfCell));
					}
					Tb.Rows.add(dataRow);
				}

			}
			Tb.Columns = collection;
		} catch (IOException e)
		{*/
			Workbook wb = WorkbookFactory.create(is);
			// 循环工作表Sheet , 目前支持一个
			// for (int i = 0; i < hssfWorkbook.getNumberOfSheets(); i++) {
			Sheet sheet = wb.getSheetAt(0);
			if (sheet == null)
			{
				return null;
			}
			// 循环行Row
			int row_size = sheet.getPhysicalNumberOfRows();
			for (int j = 0; j < row_size; j++)
			{
				Row row = sheet.getRow(j);
				if (row == null)
				{
					continue;
				}

				// 循环列Cell
				int call_num = row.getPhysicalNumberOfCells();
				// title
				if (0 == j)
				{
					for (int k = 0; k < call_num; k++)
					{
						Cell cell = row.getCell(k);
						if (null == cell)
						{
							continue;
						}
						DataColumn column = new DataColumn(getValue(cell));
						collection.Add(column);
					}
				} else
				{ // 内容
					DataRow dataRow = new DataRow(Tb);
					for (int k = 0; k < call_num; k++)
					{
						Cell cell = row.getCell(k);
						if (null == cell)
						{
							continue;
						}
						dataRow.setValue(collection.get(k), getValue(cell));
					}
					Tb.Rows.add(dataRow);
				}

			}
			Tb.Columns = collection;
		//}


		return Tb;
	}

	/**
	 * @param filePath
	 * @return
	 */
	public static DataTable GetTableByExt(String filePath)
	{
		FileInputStream stream = null;
		DataTable dataTable = null;
		try
		{

			stream = new FileInputStream(filePath);
			dataTable = GetTableByExt(stream);

		} catch (IOException e)
		{
			e.printStackTrace();
		}finally{
			if(stream!=null){
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return dataTable;

	}
}