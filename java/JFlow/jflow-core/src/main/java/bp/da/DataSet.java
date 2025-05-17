package bp.da;

import java.io.*;
import java.util.*;
import bp.difference.SystemConfig;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.core.io.ClassPathResource;
public class DataSet {

	private String name;

	public List<DataTable> Tables;

	public Hashtable<String, DataTable> hashTables;


	public DataSet() {
		if (Tables == null) {
			Tables = new ArrayList<DataTable>();
			hashTables = new Hashtable<String, DataTable>();
		}
	}

	public boolean removeTableByName(String tableName)
	{

		for (DataTable dtb : this.Tables)
		{
			if( tableName.equals(dtb.getTableName()))
			{
				this.Tables.remove(dtb);
				return true;

			}
		}
		return false;
	}
	public DataTable get(String tableName)
	{
		return GetTableByName(tableName);
	}

	public DataTable GetTableByName(String tableName)
	{
		for (DataTable dtb : this.Tables)
		{
			if( tableName.equals(dtb.getTableName()))
			{
				return dtb;

			}
		}

		return new DataTable();

	}

	/**
	 * 判断DataSet中是否包含指定的名称的DataTable数据
	 * @param tableName
	 * @return
	 */
	public boolean contains(String tableName){
		boolean isHave=false;
		for (DataTable dtb : this.Tables)
		{
			if( tableName.equals(dtb.getTableName()))
			{
				isHave = true;
				break;
			}
		}
		return isHave;
	}

	public DataSet(String name)  {
		if (Tables == null) {
			Tables = new ArrayList<DataTable>();
			hashTables = new Hashtable<String, DataTable>();
		}
	}

	/**
	 * DataSet 以xml形式写入文件
	 *
	 * param file
	 * @throws Exception
	 */
	public void WriteXml(String file)  {
		WriteXml(file, XmlWriteMode.IgnoreSchema, new DataSet("NewDataSet"));
	}

	/**
	 * DataSet 以xml形式写入文件
	 *
	 * param path
	 * param mode
	 *            暂不支持DiffGram格式
	 * @throws Exception
	 */
	public void WriteXml(String path, XmlWriteMode mode, DataSet ds) {
		StringBuilder str = new StringBuilder("<?xml version=\"1.0\" standalone=\"yes\"?>");
		str.append("<NewDataSet>");
		// 输出表架构
		for (int i = 0; i < ds.Tables.size(); i++) {
			DataTable dt = ds.Tables.get(i);
			for (int k = 0; k < dt.Rows.size(); k++) {
				str.append("<");
				str.append(dt.getTableName());
				str.append(">");
				for (int j = 0; j < dt.Columns.size(); j++) {
					DataColumn dc = dt.Columns.get(j);

					str.append("<");
					str.append(dc.ColumnName);
					str.append(">");
					try {
						Object value = dt.Rows.get(k).getValue(dc);
						if (value.toString().contains(">") || value.toString().contains("<") || value.toString().contains("&")
								|| value.toString().contains("'") || value.toString().contains("\"")) {
							value = value.toString().replace(">", "&gt;");
							value = value.toString().replace("<", "&lt;");
							value = value.toString().replace("&", "&amp;");
							value = value.toString().replace("'", "&apos;");
							value = value.toString().replace("\"", "&quot;");
						}
						str.append(value);
					} catch (Exception e) {

					}
					str.append("</");
					str.append(dc.ColumnName);
					str.append(">");

				}
				str.append("</");
				str.append(dt.getTableName());
				str.append(">");
			}
		}
		// }
		str.append("</NewDataSet>");
		String temp = str.toString();
		int pathType=0;
		if(path.startsWith("resources")==true)
			pathType = 1;
		if(SystemConfig.isJarRun() && pathType==1){

		}
		// 写入文件
		File file = new File(path);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
			BufferedWriter br = new BufferedWriter(osw);
			br.write(temp.toString());
			fos.flush();
			br.close();
			fos.close();
			osw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String formatXml(String str) throws Exception {
		Document document = null;
		document = DocumentHelper.parseText(str);
		// 格式化输出格式
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("gbk");
		StringWriter writer = new StringWriter();
		// 格式化输出流
		XMLWriter xmlWriter = new XMLWriter(writer, format);
		// 将document写入到输出流
		xmlWriter.write(document);
		xmlWriter.close();

		return writer.toString();
	}

	public void readXmls(String xmlPath) throws Exception {
		if (DataType.IsNullOrEmpty(xmlPath)) {
			return;
		}

		SAXReader reader = new SAXReader();

		File file = new File(xmlPath);
		// DataSet ds=new DataSet();
		if (file.exists()) {
			Document document = reader.read(file);// 读取XML文件
			Element root = document.getRootElement();// 得到根节点
			for (Iterator i = root.elementIterator(); i.hasNext();) {
				Element e = (Element) i.next();
				boolean type = false;
				for (int k = 0; k < this.Tables.size(); k++) {
					if (this.Tables.get(k).TableName.equals(e.getName())) {
						DataTable dt = this.Tables.get(k);
						DataRow dr = dt.NewRow();
						DataColumn dc = null;
						for (Iterator j = e.elementIterator(); j.hasNext();) {
							Element cn = (Element) j.next();
							dc = new DataColumn(cn.getName());
							dr.setValue(dc, cn.getText());
						}
						dt.Columns.Add(dc);
						dt.Rows.add(dr);
						type = true;
						break;
					}
				}
				if (type) {
					continue;
				}
				DataTable dt = new DataTable(e.getName());
				DataRow dr = dt.NewRow();
				DataColumn dc = null;
				for (Iterator j = e.elementIterator(); j.hasNext();) {
					Element cn = (Element) j.next();
					dc = new DataColumn(cn.getName());
					dt.Columns.Add(dc);
					dr.setValue(dc, cn.getText());
				}
				dt.Rows.add(dr);
				this.Tables.add(dt);
				this.hashTables.put(e.getName(), dt);
			}
		}
	}

	public void readXmlm(String xml) {
		if (DataType.IsNullOrEmpty(xml))
			return;
		try {
			xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n <NewDataSets>" + xml + "</NewDataSets>";
			// 创建xml解析对象
			SAXReader reader = new SAXReader();
			// 定义一个文档
			Document document = null;
			// 将字符串转换为
			document = reader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			@SuppressWarnings("unchecked")
			List<Element> elements = document.selectNodes("//NewDataSets/NewDataSet");

			int i = 0;
			DataTable oratb = new DataTable();
			for (Element element : elements) {
				DataRow dr = oratb.NewRow();
				@SuppressWarnings("unchecked")
				Iterator<Element> iter = element.elementIterator();
				int j = 0;
				while (iter.hasNext()) {
					Element itemEle = (Element) iter.next();
					if (i == 0) {
						oratb.Columns.Add(itemEle.getName());
					}
					dr.setValue(j, itemEle.getTextTrim());
					j++;
				}
				oratb.Rows.add(dr);
				i++;
			}
			this.Tables.add(oratb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) throws Exception {
		this.setName( name);
	}

	public List<DataTable> getTables() {
		return Tables;
	}

	public void setTables(List<DataTable> tables) {
		Tables = tables;
	}

	public Hashtable<String, DataTable> getHashTabless() {
		return hashTables;
	}

	public void setHashTables(Hashtable<String, DataTable> hashTables) {
		this.hashTables = hashTables;
	}

	/**
	 * 获取文件的XML文件内容。
	 * param path xml文件路径
	 * @return xml文件内容
	 */
	public String xmlToString(String path) throws Exception {
		String line = null;
		StringBuffer strBuffer = new StringBuffer();
		int pathType =0;
		if((path.indexOf("DataUser/")!=-1 &&path.indexOf("DataUser/Siganture/")==-1 && path.indexOf("DataUser/UploadFile/")==-1 &&
		 path.indexOf("DataUser/FlowDesc/")==-1 && path.indexOf("DataUser/Temp/")==-1 )|| path.indexOf("WF/")!=-1)
			pathType =1;
		try {
			String encoding = "UTF-8"; // 字符编码
			if(SystemConfig.isJarRun() && pathType==1){
				ClassPathResource classPathResource = new ClassPathResource(path);
				InputStream inputStream = classPathResource.getInputStream();
				StringBuilder stringBuilder = new StringBuilder();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, encoding));
				StringBuffer buffer = new StringBuffer();
				while ((line = bufferedReader.readLine()) != null){
					buffer.append(line + "\n");
				}
				bufferedReader.close();
				return buffer.toString();
			}else{
				File file = new File(path);
				if (file.isFile() && file.exists()) {

					InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
					BufferedReader bufferedReader = new BufferedReader(read);
					while ((line = bufferedReader.readLine()) != null) {
						strBuffer.append(line + "\n");
					}
					read.close();
				} else {
					throw new Exception("找不到指定的文件"+path+",请联系管理员查看环境配置是否正确，jflow.properties中的配置是否正确");
				}
			}

		} catch (Exception e) {
			throw new Exception("读取文件内容操作出错！" + e.getMessage());
		}
		return strBuffer.toString();
	}

	/**
	 * 读取XML文件
	 * param xmlpath xml文件路径
	 * @author ThinkGem
	 */
	@SuppressWarnings("rawtypes")
	public void readXml(String xmlpath) {
		if (DataType.IsNullOrEmpty(xmlpath)){
			return;
		}
		try {
			String xml = xmlToString(xmlpath);
			Document document = new SAXReader().read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			Element element = document.getRootElement();

			// 遍历 DataTable
			for (Iterator iterator = element.elementIterator(); iterator.hasNext();) {
				Element el = (Element) iterator.next();
//				System.out.println(" ===================== " + el.getName());

				// 如果没有获取到DataTable则新建一个
				DataTable dt = hashTables.get(el.getName());
				if (dt == null){
					dt = new DataTable(el.getName());
					hashTables.put(el.getName(), dt);
					Tables.add(dt);
				}

				// 新增一行数据
				DataRow dr = dt.NewRow();
				dt.Rows.add(dr);

				// 遍历该DataTable的属性
				for (Iterator it = el.attributeIterator(); it.hasNext();) {
					Attribute at = (Attribute)it.next();
//					System.out.println(" ======= " + at.getName());
					//sunxd 修改
					//由于"at.getName().toLowerCase()"语句导至  isContains 方法判断永远不成立，会给TABLE插入很多重复列
					//将at.getName().toLowerCase() 修改为  at.getName()
					if (!iscontains(dt.Columns.subList(0, dt.Columns.size()), at.getName())) {
						dt.Columns.Add(at.getName());
					}
					dr.setValue(at.getName(), at.getValue());
				}

				// 遍历该DataTable的子元素
				for (Iterator it = el.elementIterator(); it.hasNext();) {
					Element at = (Element) it.next();

					if (!iscontains(dt.Columns.subList(0, dt.Columns.size()), at.getName())) {
						dt.Columns.Add(at.getName());
					}
					String value = at.getText();
					try {
						//导出模板进行了转义，现在进行反转 dgq 2018-7-5
						if (value.toString().contains("&gt;") || value.toString().contains("&lt;")
								|| value.toString().contains("&amp;") || value.toString().contains("&apos;")
								|| value.toString().contains("&quot;")) {
							value = value.toString().replace("&amp;", "&");
							value = value.toString().replace("&gt;",">");
							value = value.toString().replace("&lt;","<");
							value = value.toString().replace("&apos;","'");
							value = value.toString().replace("&quot;","\"");
						}
					} catch (Exception e) {

					}
					dr.setValue(at.getName(), value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean iscontains(List<DataColumn> dcList, String column) {
		for (DataColumn dc : dcList) {
			if (dc.ColumnName.equals(column)) {
				return true;
			}
		}
		return false;
	}

	public static String ConvertDataSetToXml(DataSet dataSet) {
		if (dataSet != null) {
			String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			str += "<xs:schema id=\"NewDataSet\" xmlns=\"\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:msdata=\"urn:schemas-microsoft-com:xml-msdata\">";

			str += "<xs:element name=\"NewDataSet\" msdata:IsDataSet=\"true\" msdata:UseCurrentLocale=\"true\">";
			str += "<xs:complexType><xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">";
			// 循环每一个表的列

			for (int n = 0; n < dataSet.getTables().size(); n++) {
				str += "<xs:element name=\"" + dataSet.getTables().get(n).TableName + "\">";
				str += "<xs:complexType><xs:sequence>";
				DataTable table = dataSet.getTables().get(n);
				for (DataColumn col : table.Columns) {
					str += "<xs:element name=\"" + col.ColumnName;
					str += "\" type=\"";
					try {
						col.DataType.toString();
						str += IsType(col.DataType.toString());
					} catch (Exception e) {
						str += "xs:string";
					}
					str += "\" minOccurs=\"0\" />";
				}
				str += "</xs:sequence></xs:complexType></xs:element>";
			}
			str += "</xs:choice></xs:complexType></xs:element><_NewDataSet>";
			for (int i = 0; i < dataSet.getTables().size(); i++) {
				DataTable dt = dataSet.getTables().get(i);
				DataTable table = dataSet.getTables().get(i);
				for (int j = 0; j < table.Rows.size(); j++) {
					str += "<_" + i + ">";
					DataRow row = table.Rows.get(j);
					for (int a = 0; a < row.columns.size(); a++) {
						DataColumn col = table.Columns.get(a);
						str += "<_" + a + ">";
						if (row.getValue(col) == null) {
							str += "";
						} else {
							if (col.ColumnName.equals("icon")) {
								if (row.getValue(col).equals("")) {
									str += "审核";
								} else if (row.getValue(col).equals("Default")) {
									str += "审核";
								} else {
									str += row.getValue(col);
								}
							} else {
								str += row.getValue(col);
							}
						}
						str += "</_" + a + ">";

					}
					str += "</_" + i + ">";
				}
			}
			str += "</_NewDataSet>";
			str += "</xs:schema>";
			return str;
		}
		return null;
	}

	public static String IsType(String name) throws Exception {
		if (name.equals("class java.lang.Integer")) {
			name = "xs:int";
		}
		if (name.equals("class java.lang.Long")) {
			name = "xs:long";
		}
		if (name.equals("class java.lang.Float")) {
			name = "xs:float";
		}
		if (name.equals("class java.lang.Double")) {
			name = "xs:double";
		}
		if (name.equals("class java.lang.String")) {
			name = "xs:string";
		}
		if (name.equals("class java.math.BigDecimal")) {
			name = "xs:int";
		}
		return name;
	}


	public static void main(String[] args) throws Exception {

	}
}