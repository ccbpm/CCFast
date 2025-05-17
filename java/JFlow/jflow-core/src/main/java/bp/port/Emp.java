package bp.port;

import bp.da.*;
import bp.difference.*;
import bp.en.*;
import bp.en.Map;
import bp.en.Map;
import bp.sys.*;
import bp.sys.base.Glo;
import bp.tools.*;
import bp.web.*;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Emp 的摘要说明。
 */
public class Emp extends EntityNoName {

    ///#region 扩展属性
    public final String getPinYin() throws Exception {
        return this.GetValStringByKey(EmpAttr.PinYin);
    }

    public final void setPinYin(String value) throws Exception {
        this.SetValByKey(EmpAttr.PinYin, value);
    }

    /**
     * 用户ID:SAAS模式下UserID是可以重复的.
     */
    public final String getUserID() throws Exception {
        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS) {
            return this.GetValStringByKey(EmpAttr.UserID);
        }

        return this.GetValStringByKey(EmpAttr.No);
    }
    public final String getAppUserID() throws Exception {
        return this.GetValStringByKey(EmpAttr.UserID);
    }

    public final void setUserID(String value) throws Exception {
        this.SetValByKey(EmpAttr.UserID, value);

        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS) {
            if (value.startsWith(WebUser.getOrgNo() + "_") == true) {
                this.SetValByKey(EmpAttr.UserID, value.replace(WebUser.getOrgNo() + "_", ""));
                this.SetValByKey(EmpAttr.No, value);
            } else {
                this.SetValByKey(EmpAttr.No, WebUser.getOrgNo() + "_" + value);
            }
        } else {
            this.SetValByKey(EmpAttr.No, value);
        }
    }

    /**
     * 组织编号
     */
    public final String getOrgNo() throws Exception {
        return this.GetValStringByKey(EmpAttr.OrgNo);
    }

    public final void setOrgNo(String value) throws Exception {
        this.SetValByKey(EmpAttr.OrgNo, value);
    }

    /**
     * 部门编号
     */
    public final String getDeptNo() throws Exception {
        return this.GetValStrByKey(EmpAttr.FK_Dept);
    }

    public final void setDeptNo(String value) throws Exception {
        this.SetValByKey(EmpAttr.FK_Dept, value);
    }

    /**
     * 部门编号
     */
    public final String getDeptText() throws Exception {
        return this.GetValRefTextByKey(EmpAttr.FK_Dept);
    }

    /**
     * 密码
     */
    public final String getPass() throws Exception {
        return DBAccess.RunSQLReturnStringIsNull("SELECT Pass FROM Port_Emp WHERE No='" + this.getNo() + "'", null);
    }

    public final void setPass(String value) throws Exception {
        DBAccess.RunSQL("UPDATE Port_Emp SET Pass='" + value + "' WHERE No='" + this.getNo() + "'");
    }

    public final String getToken() throws Exception {
        if (DBAccess.IsExitsTableCol("Port_Emp", "Token") == false) {
            //@qfl补充上增加密码列 长度 50.
        }
        return DBAccess.RunSQLReturnStringIsNull("SELECT Token FROM Port_Emp WHERE No='" + this.getNo() + "'", "123");
    }

    public final void setToken(String value) throws Exception {
        DBAccess.RunSQL("UPDATE Port_Emp SET Token='" + value + "' WHERE No='" + this.getNo() + "'");
    }

    public final String getTel() throws Exception {
        return this.GetValStrByKey(EmpAttr.Tel);
    }

    public final void setTel(String value) throws Exception {
        this.SetValByKey(EmpAttr.Tel, value);
    }

    public final String getEmail() throws Exception {
        return this.GetValStrByKey(EmpAttr.Email);
    }

    public final void setEmail(String value) throws Exception {
        this.SetValByKey(EmpAttr.Email, value);
    }

    ///#endregion


    ///#region 公共方法

    /**
     * 权限管理.
     */
    @Override
    public UAC getHisUAC() {
        UAC uac = new UAC();
        uac.OpenForAppAdmin();
        return uac;
    }

    /**
     * 检查密码(可以重写此方法)
     *
     * @param pass 密码
     * @return 是否匹配成功
     */
    public final boolean CheckPass(String pass) throws Exception {
        String type = SystemConfig.getIsPasswordEncryptionType();
        String key = SystemConfig.getIsEncryptionKey();
        String sqlPass = this.getPass();
        pass= AesEncodeUtil.decryptAES(pass, key);
        //BCrypt加密类型
        if(type.equals("2")){
            return BCryptUtils.checkpw(pass,sqlPass);
        }
        //MD5加密类型
        if(type.equals("1")){
            pass= MD5Utill.MD5Encode(pass, "UTF8");
        }
        if (sqlPass.equals(pass) == true) {
            return true;
        }
        return false;
    }
    /**
     * 密码强度:0  无    1：8-20位字符,必须包含大小写字母、数字。在密码中不得包含用户名
     */
    public final boolean isCharacterAndNumber(String password) {
        String type=SystemConfig.getPassWordComposeType();
        //8-20位字符,必须包含大小写字母、数字。在密码中不得包含用户名
        if(type.equals("1")){
            String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,20}$";
            if(password.contains(getNo()) || !Pattern.matches(pattern, password)){
                return  false;
            }
        }
        return true;
    }
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: private static byte[] Keys = { 0x12, 0xCD, 0x3F, 0x34, 0x78, 0x90, 0x56, 0x7B };
    private static byte[] Keys = {0x12, (byte) 0xCD, 0x3F, 0x34, 0x78, (byte) 0x90, 0x56, 0x7B};

    /**
     加密字符串

     @param pass
     @return
     */
	/*public static String EncryptPass(String pass)
	{
		DESCryptoServiceProvider descsp = new DESCryptoServiceProvider(); //实例化加/解密类对象

//ORIGINAL LINE: byte[] data = Encoding.Unicode.GetBytes(pass);
		byte[] data = pass.getBytes(java.nio.charset.StandardCharsets.UTF_16LE); //定义字节数组，用来存储要加密的字符串
		ByteArrayOutputStream MStream = new ByteArrayOutputStream(); //实例化内存流对象
		//使用内存流实例化加密流对象
		CryptoStream CStream = new CryptoStream(MStream, descsp.CreateEncryptor(Keys, Keys), CryptoStreamMode.Write);
		CStream.Write(data, 0, data.length); //向加密流中写入数据
		CStream.FlushFinalBlock(); //释放加密流
		return Convert.ToBase64String(MStream.ToArray()); //返回加密后的字符串
	}*/

    /**
     解密字符串

     @param pass
     @return
     */
	/*public static String DecryptPass(String pass)
	{
		DESCryptoServiceProvider descsp = new DESCryptoServiceProvider(); //实例化加/解密类对象
//ORIGINAL LINE: byte[] data = Convert.FromBase64String(pass);
		byte[] data = Convert.FromBase64String(pass); //定义字节数组，用来存储要解密的字符串
		ByteArrayOutputStream MStream = new ByteArrayOutputStream(); //实例化内存流对象
		//使用内存流实例化解密流对象       
		CryptoStream CStream = new CryptoStream(MStream, descsp.CreateDecryptor(Keys, Keys), CryptoStreamMode.Write);
		CStream.Write(data, 0, data.length); //向解密流中写入数据
		CStream.FlushFinalBlock(); //释放解密流
		return Encoding.Unicode.GetString(MStream.ToArray()); //返回解密后的字符串
	}*/

    ///#endregion 公共方法


    ///#region 构造函数

    /**
     * 操作员
     */
    public Emp() {
    }

    /**
     * 操作员
     *
     * @param userID 编号
     */
    public Emp(String userID) throws Exception {
        if (userID == null || userID.length() == 0) {
            throw new RuntimeException("@要查询的操作员编号为空。");
        }

        userID = userID.trim();
        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS) {
            if (userID.equals("admin") == true) {
                this.SetValByKey("No", userID);
            } else {
                if(userID.startsWith(WebUser.getOrgNo()+"_"))
                    this.SetValByKey("No", userID);
                else
                    this.SetValByKey("No", WebUser.getOrgNo() + "_" + userID);
            }
        } else {
            this.SetValByKey("No", userID);
        }
        this.Retrieve();
    }

    /**
     * 重写基类方法
     */
    @Override
    public Map getEnMap() {
        if (this.get_enMap() != null) {
            return this.get_enMap();
        }
        Map map = new Map("Port_Emp", "用户");

        map.setEnDBUrl(new DBUrl(DBUrlType.AppCenterDSN)); //要连接的数据源（表示要连接到的那个系统数据库）。
        map.IndexField = EmpAttr.FK_Dept;

        /* 关于字段属性的增加 .. */
        //map.IsEnableVer = true;
        map.AddTBStringPK(EmpAttr.No, null, "编号", true, false, 1, 50, 30);
        //如果是集团模式或者是SAAS模式.
//        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS) {
//
//        }
        map.AddTBString(EmpAttr.UserID, null, "用户ID", true, false, 0, 50, 30);
        map.AddTBString(EmpAttr.Name, null, "名称", true, false, 0, 200, 30);
        map.AddTBString(EmpAttr.PinYin, null, "拼音", false, false, 0, 200, 30);
        map.AddDDLEntities(EmpAttr.FK_Dept, null, "部门", new bp.port.Depts(), false);
        map.AddTBString(EmpAttr.Tel, null, "手机号", false, false, 0, 36, 36);
        map.AddTBString(EmpAttr.Email, null, "邮箱", false, false, 0, 36, 36);
        map.AddTBString(EmpAttr.Leader, null, "直属部门领导", false, false, 0, 20, 130);
        map.SetHelperAlert(EmpAttr.Leader, "这里是领导的登录帐号，不是中文名字，用于流程的接受人规则中。");
        map.AddTBString(EmpAttr.LeaderName, null, "领导名", true, true, 0, 20, 130);
        map.AddTBString(EmpAttr.OrgNo, null, "组织编号", true, true, 0, 50, 50);
        map.AddTBInt(EmpAttr.EmpSta, 0, "状态(0=启用，1=禁用)", false, false);
        //if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
        //   map.AddBoolean(EmpAttr.IsOfficer, false, "是否是联络员", true, true);
        map.AddTBInt(EmpAttr.Idx, 0, "Idx", false, false);

        ///#endregion 字段

        ///#region 查询条件.
        map.AddSearchAttr(EmpAttr.FK_Dept);

        if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single) {
            map.AddHidden("OrgNo", "=", "@WebUser.OrgNo");
        }

        ///#endregion 查询条件.


        ///#region 方法.
        RefMethod rm = new RefMethod();
        rm.Title = "设置图片签名";
        rm.ClassMethodName = this.toString() + ".DoSinger";
        rm.refMethodType = RefMethodType.RightFrameOpen;
        map.AddRefMethod(rm);

        rm = new RefMethod();
        rm.Title = "部门角色";
        rm.ClassMethodName = this.toString() + ".DoEmpDepts";
        rm.refMethodType = RefMethodType.RightFrameOpen;
        map.AddRefMethod(rm);

        //节点绑定部门. 节点绑定部门.
        map.getAttrsOfOneVSM().AddBranches(new DeptEmps(), new bp.port.Depts(), bp.port.DeptEmpAttr.FK_Emp, bp.port.DeptEmpAttr.FK_Dept, "部门维护", EmpAttr.Name, EmpAttr.No, "@WebUser.DeptNo");


        rm = new RefMethod();
        rm.Title = "修改密码";
        rm.ClassMethodName = this.toString() + ".DoResetpassword";
        try{
            rm.getHisAttrs().AddTBString("pass1", null, "输入密码", true, false, 0, 100, 100);
            rm.getHisAttrs().AddTBString("pass2", null, "再次输入", true, false, 0, 100, 100);
        }catch(Exception e){

        }
        map.AddRefMethod(rm);

        rm = new RefMethod();
        rm.Title = "修改主部门";
        rm.ClassMethodName = this.toString() + ".DoEditMainDept";
        rm.RefAttrKey = EmpAttr.FK_Dept;
        rm.refMethodType = RefMethodType.LinkModel;
        map.AddRefMethod(rm);

        rm = new RefMethod();
        rm.Title = "设置部门直属领导";
        rm.ClassMethodName = this.toString() + ".DoEditLeader";
        rm.RefAttrKey = EmpAttr.LeaderName;
        rm.refMethodType = RefMethodType.LinkModel;
        map.AddRefMethod(rm);

        //平铺模式.
        map.getAttrsOfOneVSM().AddGroupPanelModel(new bp.port.TeamEmps(), new bp.port.Teams(), bp.port.TeamEmpAttr.FK_Emp, bp.port.TeamEmpAttr.FK_Team, "标签", TeamAttr.FK_TeamType);


        ///#endregion 方法.


        this.set_enMap(map);
        return this.get_enMap();
    }


    ///#region 方法执行.
    public final String DoEditMainDept() throws Exception {
        return "../../../GPM/EmpDeptMainDept.htm?FK_Emp=" + this.getNo() + "&FK_Dept=" + this.getDeptNo();
    }

    public final String DoEditLeader() throws Exception {
        return "../../../GPM/EmpLeader.htm?FK_Emp=" + this.getNo() + "&FK_Dept=" + this.getDeptNo();
    }

    public final String DoEmpDepts() throws Exception {
        return "../../../GPM/EmpDepts.htm?FK_Emp=" + this.getNo();
    }

    public final String DoSinger() throws Exception {
        //路径
        return "../../../GPM/Siganture.htm?EmpNo=" + this.getNo();
    }

    ///#endregion 方法执行.

    @Override
    protected boolean beforeInsert() throws Exception {

        //设置orgNo.
        if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single) {
            Dept dept = new Dept();
            dept.setNo(this.getNo());
            dept.RetrieveFromDBSources();
            if(DataType.IsNullOrEmpty(this.getOrgNo())){
                this.setOrgNo(dept.getOrgNo());
            }

        }

        return super.beforeInsert();
    }

    @Override
    protected void afterInsert() throws Exception {
        String pass = this.getPass();
        if (DataType.IsNullOrEmpty(pass) == true)
            pass = SystemConfig.getUserDefaultPass();

        if (SystemConfig.getIsEnablePasswordEncryption() == true)
            pass = bp.tools.MD5Utill.MD5Encode(pass, "UTF8");
        this.setPass(pass); //设置密码.

        super.afterInsert();
    }

    @Override
    protected boolean beforeUpdateInsertAction() throws Exception {
        //设置OrgNo.
        if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single) {
            //不是超级管理员.
            if (WebUser.getNo().equals("admin") == false) {
                if(DataType.IsNullOrEmpty(this.getOrgNo())){
                    this.setOrgNo(WebUser.getOrgNo());
                }

            }

            if (WebUser.getNo().equals("admin") == true) {
                Dept dept = new Dept(this.getDeptNo());
                if(DataType.IsNullOrEmpty(this.getOrgNo())){
                    this.setOrgNo(dept.getOrgNo());
                }

            }
        }

        //增加拼音，以方便查找.
        if (DataType.IsNullOrEmpty(this.getName()) == true) {
            throw new RuntimeException("err@名称不能为空.");
        }

        if (WebUser.getIsAdmin() == false && this.getNo().equals(WebUser.getNo()) == false) {
         //   throw new RuntimeException("err@非管理员无法操作.");
        }

        if (DataType.IsNullOrEmpty(this.getEmail()) == false) {
            //邮箱格式

            Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
            Matcher matcher = pattern.matcher(this.getEmail());
            if (matcher.matches() == false) {
                throw new RuntimeException("err@邮箱格式不正确,无效的邮箱."+this.getEmail());
            }
        }

        String pinyinQP = DataType.ParseStringToPinyin(this.getName()).toLowerCase();
        String pinyinJX = DataType.ParseStringToPinyinJianXie(this.getName()).toLowerCase();
        this.setPinYin("," + pinyinQP + "," + pinyinJX + ",");

        //处理角色信息.
        DeptEmpStations des = new DeptEmpStations();
        des.Retrieve(DeptEmpStationAttr.FK_Emp, this.getNo());

        String depts = "";
        String stas = "";

        for (DeptEmpStation item : des.ToJavaList()) {
            Dept dept = new Dept();
            dept.setNo(item.getDeptNo());
            if (dept.RetrieveFromDBSources() == 0) {
                item.Delete();
                continue;
            }

            //给拼音重新定义值,让其加上部门的信息.
            this.setPinYin(this.getPinYin() + pinyinJX + "/" + DataType.ParseStringToPinyinJianXie(dept.getName()).toLowerCase() + ",");

            Station sta = new Station();
            sta.setNo(item.getStationNo());
            if (sta.RetrieveFromDBSources() == 0) {
                item.Delete();
                continue;
            }

            stas += "@" + dept.getNameOfPath() + "|" + sta.getName();
            depts += "@" + dept.getNameOfPath();
        }

        //记录日志.
        Glo.WriteUserLog("新建/修改人员:" + this.ToJson(), "组织数据操作");
        return super.beforeUpdateInsertAction();
    }

    @Override
    protected boolean beforeDelete() throws Exception {
        if (this.getNo().toLowerCase().equals("admin") == true) {
            throw new RuntimeException("err@管理员账号不能删除.");
        }
        if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single) {
            //如果是组织的主要管理员时不能删除
            String sql = "SELECT COUNT(*) From Port_Org WHERE No='" + WebUser.getOrgNo() + "' AND Adminer='" + this.getNo() + "'";
            if (DBAccess.RunSQLReturnValInt(sql) == 1) {
                throw new RuntimeException("err@组织" + WebUser.getOrgName() + "的主要管理员账号不能删除.");
            }
        }

        bp.sys.base.Glo.WriteUserLog("删除人员:" + this.ToJson(), "组织数据操作");
        return super.beforeDelete();
    }

    /**
     * 保存后修改WF_Emp中的邮箱
     */
    @Override
    protected void afterInsertUpdateAction() throws Exception {
        String sql = "Select Count(*) From WF_Emp Where No='" + this.getNo() + "'";
        int count = DBAccess.RunSQLReturnValInt(sql);
        if (count == 0) {
            sql = "INSERT INTO WF_Emp (No,Name) VALUES('" + this.getNo() + "','" + this.getName() + "')";
            DBAccess.RunSQL(sql);
        }

        DeptEmp deptEmp = new DeptEmp();
        deptEmp.setDeptNo(this.getDeptNo());
        deptEmp.setEmpNo(this.getNo());
        deptEmp.setMyPK(this.getDeptNo() + "_" + this.getNo());
        if (deptEmp.IsExit("MyPK", deptEmp.getMyPK()) == false) {
            deptEmp.Insert();
        }

        super.afterInsertUpdateAction();
    }

    /**
     * 删除之后要做的事情
     */
    @Override
    protected void afterDelete() throws Exception {
        DeptEmps des = new DeptEmps();
        des.Delete(DeptEmpAttr.FK_Emp, this.getNo());

        DeptEmpStations stas = new DeptEmpStations();
        stas.Delete(DeptEmpAttr.FK_Emp, this.getNo());


        super.afterDelete();
    }

    public static String GenerPinYin(String no, String name) throws Exception {
        //增加拼音，以方便查找.
        String pinyinQP = DataType.ParseStringToPinyin(name).toLowerCase();
        String pinyinJX = DataType.ParseStringToPinyinJianXie(name).toLowerCase();
        String py = "," + pinyinQP + "," + pinyinJX + ",";

        //处理角色信息.
        DeptEmpStations des = new DeptEmpStations();
        des.Retrieve(DeptEmpStationAttr.FK_Emp, no);

        String depts = "";
        String stas = "";

        for (DeptEmpStation item : des.ToJavaList()) {
            Dept dept = new Dept();
            dept.setNo(item.getDeptNo());
            if (dept.RetrieveFromDBSources() == 0) {
                item.Delete();
                continue;
            }

            //给拼音重新定义值,让其加上部门的信息.
            py = py + pinyinJX + "/" + DataType.ParseStringToPinyinJianXie(dept.getName()).toLowerCase() + ",";

            bp.port.Station sta = new bp.port.Station();
            sta.setNo(item.getStationNo());
            if (sta.RetrieveFromDBSources() == 0) {
                item.Delete();
                continue;
            }

            stas += "@" + dept.getNameOfPath() + "|" + sta.getName();
            depts += "@" + dept.getNameOfPath();
        }

        return py;
    }

    /**
     * 向上移动
     */
    public final void DoUp() throws Exception {
        this.DoOrderUp(EmpAttr.FK_Dept, this.getDeptNo(), EmpAttr.Idx);
    }

    /**
     * 向下移动
     */
    public final void DoDown() throws Exception {
        this.DoOrderDown(EmpAttr.FK_Dept, this.getDeptNo(), EmpAttr.Idx);
    }

    public final String DoResetpassword(String pass1, String pass2) throws Exception {

        if (pass1.equals(pass2) == false) {
            return "两次密码不一致";
        }
        if (pass1.toLowerCase().contains("or") == true || pass1.toLowerCase().contains(" ") == true) {
            return "密码格式错误.";
        }

        if (SystemConfig.getIsEnablePasswordEncryption() == true) {
            pass1 = bp.tools.MD5Utill.MD5Encode(pass1, "UTF8");
        }

        if (WebUser.getIsAdmin() == false) {
            if (this.getUserID().equals(WebUser.getNo()) == false) {
                return "err@您不能修改别人的密码.";
            }
        }

        if (this.getOrgNo().equals(WebUser.getOrgNo()) == false) {
            return "err@您不能修改别的组织密码.";
        }

        this.setPass(pass1);

        return "密码设置成功";
    }

    /**
     * 获取集合
     */
    @Override
    public Entities GetNewEntities() {
        return new Emps();
    }

    ///#endregion 构造函数


    ///#region 方法测试代码.

    /**
     * 禁用、启用用户
     *
     * @return
     */
    public final String DoUnEnable() throws Exception {
        String userNo = this.getNo();
        if (userNo.equals("admin")) {
            throw new RuntimeException("err@管理员账号不能禁用.");
        }
        String sql = "";
        if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single) {
            //如果是组织的主要管理员时不能删除
            sql = "SELECT COUNT(*) From Port_Org WHERE No='" + WebUser.getOrgNo() + "' AND Adminer='" + this.getNo() + "'";
            if (DBAccess.RunSQLReturnValInt(sql) == 1) {
                throw new RuntimeException("err@组织" + WebUser.getOrgName() + "的主要管理员账号不能禁用.");
            }
        }
        //判断当前人员是否有待办
        String wfSql = "";
        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS) {
            wfSql = " AND OrgNo='" + WebUser.getOrgNo() + "'";
            userNo = this.getUserID();
        }

        /*不是授权状态*/
        if (SystemConfig.GetValByKeyBoolen("IsEnableTaskPool", false) == true) {
            sql = "SELECT count(WorkID) as Num FROM WF_EmpWorks WHERE FK_Emp='" + userNo + "' AND TaskSta!=1 " + wfSql;
        } else {
            sql = "SELECT count(WorkID) as Num FROM WF_EmpWorks WHERE  FK_Emp='" + userNo + "' " + wfSql;
        }

        int count = DBAccess.RunSQLReturnValInt(sql);
        if (count != 0) {
            return this.getName() + "还存在待办，不能禁用该用户";
        }
        sql = "UPDATE WF_Emp SET UseSta=0 WHERE No='" + this.getNo() + "'";
        DBAccess.RunSQL(sql);
        //this.Delete();
        this.SetValByKey("EmpSta", 1);
        this.Update();
        return this.getName() + "已禁用";

    }

    /**
     * 密码重置（根据用户配置的默认密码进行重置）
     *
     * @return
     */
    public final String ResetPass() throws Exception {
        //用户默认密码
        String defaultPass = SystemConfig.getUserDefaultPass();
        //加密类型
        String type = SystemConfig.getIsPasswordEncryptionType();
        String encodPass = defaultPass;
        //BCrypt加密类型
        if(type.equals("2")){
            encodPass = BCryptUtils.hashpw(defaultPass);
        }
        //MD5加密类型
        if(type.equals("1")){
            encodPass = MD5Utill.MD5Encode(defaultPass, "UTF8");
        }
        this.setPass(encodPass);
        return "执行成功.";
    }

    /**
     * ChangePass
     *
     * @param oldpass
     * @param pass1
     * @param pass2
     * @return
     */
    public final String ChangePass(String oldpass, String pass1, String pass2) throws Exception {
        if (this.CheckPass(oldpass) == false)
            return "err@旧密码错误.";

        if (pass1.equals(pass2) == false)
            return "err@两次密码不一致.";

        String type= SystemConfig.getIsPasswordEncryptionType();
        String key= SystemConfig.getIsEncryptionKey();
        pass1 = AesEncodeUtil.decryptAES(pass1, key);
        if(!isCharacterAndNumber(pass1)){
            return "err@新密码必须含大小写字母及数字;不得含用户名;长度限制8-20个字符";
        }
        //BCrypt加密类型
        if(type.equals("2")){
            pass1 = BCryptUtils.hashpw(pass1);
        }
        //MD5加密类型
        if(type.equals("1")){
            pass1= MD5Utill.MD5Encode(pass1, "UTF8");
        }
        DBAccess.RunSQL("UPDATE Port_Emp SET Pass='"+ pass1 + "' WHERE No='"+this.getNo()+"'");
        WebUser.setIsFirstLogin("0");
        return "执行成功.";
    }


    ///#endregion 方法测试代码.

    /**
     * 是否包含指定的角色编号
     *
     * @param stationNo 指定的角色编号
     * @return
     */
    public final boolean HaveStation(String stationNo) throws Exception {
        String sql = "SELECT COUNT(FK_Emp) AS Num FROM Port_DeptEmpStation WHERE FK_Emp='" + this.getNo() + "' AND FK_Station='" + stationNo + "'";
        if (DBAccess.RunSQLReturnValInt(sql) == 0) {
            return false;
        }
        return true;
    }

}
