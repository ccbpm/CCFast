package bp.wf.httphandler;

import bp.da.*;
import bp.difference.handler.CommonFileUtils;
import bp.sys.*;
import bp.tools.Encodes;
import bp.web.*;
import bp.port.*;
import bp.difference.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;

/** 
 页面功能实体
*/
public class WF_Setting extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 清楚缓存
	 
	 @return 
	*/
	public final String Default_ClearCache()
	{
		DBAccess.RunSQL("DELETE FROM Sys_UserRegedit WHERE FK_Emp='" + WebUser.getNo() + "' AND OrgNo='" + WebUser.getOrgNo() + "'");
		return "执行成功，请刷新菜单或者重新进入看看菜单权限是否有变化。";
	}
	public final String UpdateEmpNo() throws Exception {
		Emp emp = new Emp(WebUser.getNo());
		emp.setEmail(this.GetRequestVal("Email"));
		emp.setTel(this.GetRequestVal("Tel"));
		emp.setName(this.GetRequestVal("Name"));
		emp.Update();
		return "修改成功.";
	}
	/** 
	 构造函数
	*/
	public WF_Setting()
	{
	}


		///#region 执行父类的重写方法.
	/** 
	 默认执行的方法
	 
	 @return 
	*/
	@Override
	protected String DoDefaultMethod()
	{
		switch (this.getDoType())
		{
			case "DtlFieldUp": //字段上移
				return "执行成功.";
			default:
				break;
		}

		//找不不到标记就抛出异常.
		throw new RuntimeException("@标记[" + this.getDoType() + "]，没有找到. @RowURL:" + ContextHolderUtils.getRequest().getRequestURI());
	}

		///#endregion 执行父类的重写方法.


	public final String Default_Init() throws Exception {
		Hashtable ht = new Hashtable();
		ht.put("UserNo", WebUser.getNo());
		ht.put("UserName", WebUser.getName());

		Emp emp = new Emp();
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			emp.setNo(WebUser.getOrgNo() + "_" + WebUser.getNo());
		}
		else
		{
			emp.setNo(WebUser.getNo());
		}
		emp.setUserID(WebUser.getNo());
		emp.Retrieve();

		//部门名称.
		ht.put("DeptName", emp.getDeptText());


		DeptEmpStations des = new DeptEmpStations();
		des.Retrieve(DeptEmpStationAttr.FK_Emp, emp.getUserID(), null);

		String depts = "";
		String stas = "";

		for (DeptEmpStation item : des.ToJavaList())
		{
			Dept dept = new Dept();
			dept.setNo(item.getDeptNo());
			int count = dept.RetrieveFromDBSources();
			if (count != 0)
			{
				depts += dept.getName() +"、";
			}


			if (DataType.IsNullOrEmpty(item.getStationNo()) == true)
			{
				continue;
			}

			if (DataType.IsNullOrEmpty(item.getDeptNo()) == true)
			{
				continue;
			}

			Station sta = new Station();
			sta.setNo(item.getStationNo());
			count = sta.RetrieveFromDBSources();
			if (count != 0)
			{
				stas += sta.getName() +"、";
			}
		}

		ht.put("Depts", depts);
		ht.put("Stations", stas);

		bp.wf.port.WFEmp wfemp = new bp.wf.port.WFEmp(WebUser.getUserID());
		ht.put("Tel", emp.getTel());
		ht.put("Email", emp.getEmail());

		return bp.tools.Json.ToJson(ht);
	}
	/** 
	 初始化
	 
	 @return json数据
	*/
	public final String Author_Init() throws Exception {
		bp.wf.port.WFEmp emp = new bp.wf.port.WFEmp(WebUser.getNo());
		Hashtable ht = emp.getRow();
		ht.remove(bp.wf.port.WFEmpAttr.StartFlows); //移除这一列不然无法形成json.
		return emp.ToJson(true);
	}


		///#region 图片签名.
	public final String Siganture_Init() throws Exception {
		if (WebUser.getNoOfRel() == null)
		{
			return "err@登录信息丢失";
		}

		//首先判断是否存在，如果不存在就生成一个.
		bp.wf.dts.GenerSiganture.GenerIt(WebUser.getNo(), WebUser.getName());


		Hashtable ht = new Hashtable();
		ht.put("No", WebUser.getNo());
		ht.put("Name", WebUser.getName());
		ht.put("FK_Dept", WebUser.getDeptNo());
		ht.put("FK_DeptName", WebUser.getDeptName());
		return bp.tools.Json.ToJson(ht);
	}
	public final String Siganture_Save() throws Exception {

		try {
			String empNo = this.GetRequestVal("EmpNo");
			if (DataType.IsNullOrEmpty(empNo) == true)
				empNo = WebUser.getNo();
			HttpServletRequest request = getRequest();
			String contentType = request.getContentType();
			if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
				String tempFilePath = SystemConfig.getPathOfWebApp() + "DataUser/Siganture/" + empNo + ".jpg";
				if (SystemConfig.isJarRun())
					tempFilePath = SystemConfig.getPhysicalPath()+"DataUser/Siganture/" + empNo + ".jpg";

				File tempFile = new File(tempFilePath);
				if (tempFile.exists()) {
					tempFile.delete();
				}
				try{
					CommonFileUtils.upload(request, "file", tempFile);
				}catch(Exception e){
					CommonFileUtils.upload(request, "File_Upload", tempFile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "err@执行失败";
		}
		return "上传成功！";
	}

		///#endregion 图片签名.


		///#region 头像.
		public final String HeadPic_Save()
		{
			try {
				String empNo = this.GetRequestVal("EmpNo");
				if (DataType.IsNullOrEmpty(empNo) == true)
					empNo = WebUser.getNo();
				HttpServletRequest request = getRequest();
				String contentType = request.getContentType();
				if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
					String tempFilePath = SystemConfig.getPathOfDataUser() + "UserIcon/" + empNo + ".png";
					File tempFile = new File(tempFilePath);
					if (tempFile.exists()) {
						tempFile.delete();
					}
					try{
						CommonFileUtils.upload(request, "file", tempFile);
					}catch(Exception e){
						CommonFileUtils.upload(request, "File_Upload", tempFile);
					}

				}
			} catch (Exception e) {

				e.printStackTrace();
				return "err@执行失败";
			}

			return "上传成功！";
		}

		///#endregion 头像.

	///#region 保存写字板签名.
	public final String ImageBody_Save() throws Exception {

		try {
			String empNo = this.GetRequestVal("EmpNo");
			if (DataType.IsNullOrEmpty(empNo) == true)
				empNo = WebUser.getNo();

			String tempFilePath = SystemConfig.getPathOfWebApp() + "DataUser/Siganture/" + empNo + ".jpg";
			if (SystemConfig.isJarRun())
				tempFilePath = SystemConfig.getPhysicalPath()+"DataUser/Siganture/" + empNo + ".jpg";

			File tempFile = new File(tempFilePath);
			if (tempFile.exists()) {
				tempFile.delete();
			}

			String imgData = this.GetValFromFrmByKey("imageData");
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] imgByte = decoder.decode(imgData);
			InputStream in = new ByteArrayInputStream(imgByte);
			byte[] b = new byte[1024];
			int nRead = 0;

			OutputStream o = new FileOutputStream(tempFile);

			while ((nRead = in.read(b)) != -1) {
				o.write(b, 0, nRead);
			}

			o.flush();
			o.close();

			in.close();

		} catch (Exception e) {
			e.printStackTrace();
			return "err@执行失败";
		}
		return "保存写字板签名成功！";
	}
	///#endregion 保存写字板签名.

		///#region 切换部门.
	/** 
	 初始化切换部门.
	 
	 @return 
	*/
	public final String ChangeDept_Init()
	{

		String sql = "";
		//如果是集团版.
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			sql = "SELECT a.No, a.Name, A.NameOfPath, '0' AS  CurrentDept, A.OrgNo, '' as OrgName FROM Port_Dept A, Port_DeptEmp B WHERE A.No=B.FK_Dept AND B.FK_Emp='" + WebUser.getNo() + "'";
		}
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql = "SELECT a.No, a.Name, A.NameOfPath, '0' AS  CurrentDept  FROM Port_Dept A, Port_DeptEmp B WHERE A.No=B.FK_Dept AND B.FK_Emp='" + WebUser.getNo() + "'";
		}
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			sql = "SELECT a.No, a.Name, A.NameOfPath, '0' AS  CurrentDept, A.OrgNo, '' as OrgName  FROM Port_Dept A, Port_DeptEmp B,Port_Emp C WHERE A.No=B.FK_Dept AND C.FK_Dept=A.No AND C.No=B.FK_Emp AND C.UserID='" + WebUser.getNo() + "'";
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		if (dt.Rows.size() == 0)
		{
			sql = "SELECT a.No,a.Name,B.NameOfPath, '1' as CurrentDept ,  B.OrgNo, '' as OrgName FROM ";
			sql += " Port_Emp A, Port_Dept B WHERE A.FK_Dept=B.No  AND A.No='" + WebUser.getNo() + "'";
			dt = DBAccess.RunSQLReturnTable(sql);
		}

		dt.Columns.get(0).ColumnName = "No";
		dt.Columns.get(1).ColumnName = "Name";
		dt.Columns.get(2).ColumnName = "NameOfPath";
		dt.Columns.get(3).ColumnName = "CurrentDept";

		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			dt.Columns.get(4).ColumnName = "OrgNo";
			dt.Columns.get(5).ColumnName = "OrgName";

			//设置组织名字.
			for (DataRow dr : dt.Rows)
			{
				String orgNo = dr.getValue(4).toString();
				dr.setValue(5, DBAccess.RunSQLReturnVal("SELECT Name FROM Port_Org WHERE No='" + orgNo + "'", null));
			}
		}

		//设置当前的部门.
		for (DataRow dr : dt.Rows)
		{
			if (dr.getValue("No").toString().equals(WebUser.getDeptNo()) == true)
			{
				dr.setValue("CurrentDept", "1");
			}

			if (DataType.IsNullOrEmpty(dr.getValue("NameOfPath").toString()) == true)
			{
				dr.setValue("NameOfPath", dr.getValue("Name"));
			}
		}
		return bp.tools.Json.ToJson(dt);
	}
	/** 
	 提交选择的部门。
	 
	 @return 
	*/
	public final String ChangeDept_Submit() throws Exception {
		String deptNo = this.GetRequestVal("DeptNo");

		Dept dept = new Dept(deptNo);

		// @honygan.
		DBAccess.RunSQL("UPDATE Port_Emp SET OrgNo='" + dept.getOrgNo() + "', FK_Dept='" + dept.getNo() + "' WHERE No='" + WebUser.getNo() + "'");

		WebUser.setDeptNo(dept.getNo());
		WebUser.setDeptName(dept.getName());
		WebUser.setDeptNameOfFull(dept.getNameOfPath());
		WebUser.setOrgNo(dept.getOrgNo());

		bp.wf.port.WFEmp emp = new bp.wf.port.WFEmp(WebUser.getNo());
		emp.setStartFlows("");
		emp.Update();
		//去掉切换主部门
		/* try
		 {
		     String sql = "";

		     if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		         sql = "UPDATE Port_Emp SET fk_dept='" + deptNo + "' WHERE UserID='" + WebUser.getNo() + "' AND OrgNo='" + WebUser.getOrgNo() + "'";
		     else
		         sql = "UPDATE Port_Emp SET fk_dept='" + deptNo + "' WHERE No='" + WebUser.getNo() + "'";
		     DBAccess.RunSQL(sql);
		     BP.WF.Dev2Interface.Port_Login(WebUser.getNo());
		 }
		 catch (Exception ex)
		 {

		 }*/

		return "@执行成功,已经切换到｛" + WebUser.getDeptName() + "｝部门上。";
	}

		///#endregion

	public final String UserIcon_Init()
	{
		return "";
	}

	public final String UserIcon_Save()
	{
		return "";
	}



		///#region 修改密码.
	public final String ChangePassword_Init()
	{
		if (DBAccess.IsView("Port_Emp", SystemConfig.getAppCenterDBType()) == true)
		{
			return "err@当前是组织结构集成模式，您不能修改密码，请在被集成的系统修改密码。";
		}

		return "";
	}
	/** 
	 修改密码 .
	 
	 @return 
	*/
	public final String ChangePassword_Submit() throws Exception {
		String oldPass = this.GetRequestVal("TB_PW");
		String pass = this.GetRequestVal("Pass");

		Emp emp = new Emp(WebUser.getNo());
		if (emp.CheckPass(oldPass) == false)
		{
			return "err@旧密码错误.";
		}

		if (SystemConfig.getIsEnablePasswordEncryption() == true)
		{
			pass = Encodes.encodeBase64(pass);
		}
		emp.setPass(pass);
		emp.Update();

		return "密码修改成功...";
	}

		///#endregion 修改密码.


	public final String SetUserTheme() throws Exception {
		String theme = this.GetRequestVal("Theme");
		bp.wf.port.WFEmp emp = new bp.wf.port.WFEmp(WebUser.getNo());
		emp.SetPara("Theme", theme);
		emp.Update();

		return "设置成功";
	}

}
