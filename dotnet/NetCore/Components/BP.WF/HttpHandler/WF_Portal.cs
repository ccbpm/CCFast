using System;
using System.Data;
using System.Text;
using System.Web;
using BP.DA;
using BP.Sys;
using BP.Web;
using BP.Port;
using BP.CCFast.CCMenu;
using System.Collections;
using BP.WF.Port.Admin2Group;
using BP.Tools;
using System.Security.Cryptography;
using BP.CCFast.Portal;
using BP.Difference;
using BP.WF.XML;
using ICSharpCode.SharpZipLib.Zip;
using BP.Port;
using BP.WF.Port;
using BP.WF.Template;
using BP.Port.WeiXin.Msg;
using BP.Cloud;
using BP.En;
using static iTextSharp.text.pdf.AcroFields;
using static Google.Protobuf.WellKnownTypes.Field;
using Spire.License;

namespace BP.WF.HttpHandler
{
    /// <summary>
    /// 页面功能实体
    /// </summary>
    public class WF_Portal : DirectoryPageBase
    {
        public string PageID
        {
            get
            {
                string pageID = this.GetRequestVal("PageID");
                if (DataType.IsNullOrEmpty(pageID) == true)
                    pageID = "Home";

                return pageID;
            }
        }
        /// <summary>
        /// 用于切换部门登陆,去掉了切换组织概念.
        /// </summary>
        /// <returns></returns>
        public string My_Depts()
        {
            BP.Port.Emp myemp = new BP.Port.Emp(WebUser.UserID);

            DeptEmp de = new DeptEmp();
            de.setMyPK(myemp.DeptNo + "_" + myemp.No);
            int i = de.RetrieveFromDBSources();
            if (i == 0)
            {
                de.SetValByKey("FK_Emp", myemp.No);
                de.SetValByKey("FK_Dept", myemp.DeptNo);
                de.SetValByKey("DeptName", myemp.DeptText);
                if (SystemConfig.CCBPMRunModel != CCBPMRunModel.Single)
                    de.SetValByKey("OrgNo", WebUser.OrgNo);
                de.Insert();
            }

            if (SystemConfig.CCBPMRunModel == CCBPMRunModel.GroupInc)
            {
                if (WebUser.No.Equals("admin") == true)
                {
                    // 为深圳单独处理的需求，检查是否有管理员.
                    BP.WF.Port.Admin2Group.Orgs ens = new Port.Admin2Group.Orgs();
                    ens.RetrieveAll();
                    foreach (BP.WF.Port.Admin2Group.Org item in ens)
                    {
                        de = new DeptEmp();
                        de.MyPK = item.No + "_" + item.Adminer;
                        if (de.RetrieveFromDBSources() == 0)
                        {
                            de.DeptNo = item.No;
                            de.DeptName = item.Name;
                            de.EmpNo = item.Adminer;
                            de.Insert();
                        }
                    }
                }
            }

            string sql = "";
            if (SystemConfig.CCBPMRunModel == CCBPMRunModel.Single)
                sql = "SELECT a.No,a.Name FROM Port_Dept a,  Port_DeptEmp b WHERE a.No=b.FK_Dept AND b.FK_Emp ='" + WebUser.No + "' ";
            else
            {
                //更新组织结构.
                // if (BP.Web.WebUser.IsAdmin == true)
                //   DBAccess.RunSQL("update port_deptemp d, port_dept p set d.OrgNo = p.OrgNo  where d.FK_Dept=p.no");

                sql = "SELECT a.No,a.Name, c.No AS OrgNo,c.Name AS OrgName  FROM Port_Dept a, Port_DeptEmp b, Port_Org c WHERE a.No=b.FK_Dept AND c.No=a.OrgNo AND b.FK_Emp ='" + WebUser.No + "' ";
            }



            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            if (SystemConfig.AppCenterDBFieldCaseModel != FieldCaseModel.None)
            {
                if (CCBPMRunModel.Single == SystemConfig.CCBPMRunModel)
                {
                    dt.Columns[0].ColumnName = "No";
                    dt.Columns[1].ColumnName = "Name";
                }
                else
                {
                    dt.Columns[0].ColumnName = "No";
                    dt.Columns[1].ColumnName = "Name";
                    dt.Columns[2].ColumnName = "OrgNo";
                    dt.Columns[3].ColumnName = "OrgName";
                }
            }

            // 增加当前登陆部门的标识.
            dt.Columns.Add("Note");
            foreach (DataRow dr in dt.Rows)
            {
                string deptNo = dr[0].ToString();
                if (BP.Web.WebUser.DeptNo.Equals(deptNo) == true)
                    dr["Note"] = "当前登陆部门";
                else
                    dr["Note"] = "无";
            }

            return BP.Tools.Json.ToJson(dt);
        }
        public string Change_Dept()
        {
            string deptNo = this.RefNo;
            string token = WebUser.Token;

            if (DBAccess.IsView("Port_Emp") == true)
            {
                // 初始化实例.
                BP.Port.Emp emp = new BP.Port.Emp(BP.Web.WebUser.No);

                BP.Port.Dept dept = new BP.Port.Dept(deptNo);
                emp.SetValByKey("FK_Dept", dept.No);
                emp.SetValByKey("OrgNo", dept.OrgNo);

                // 登陆.
                BP.Web.WebUser.SignInOfGener(emp, "CH", false, false);
            }
            else
            {
                // 换掉主部门.
                BP.Port.Emp emp = BP.Web.WebUser.ChangeMainDept(WebUser.No, deptNo);

                // 初始化实例.
                if (emp == null)
                    emp = new BP.Port.Emp(BP.Web.WebUser.No);

                // 登陆.
                BP.Web.WebUser.SignInOfGener(emp, "CH", false, false);
            }

            WebUser.Token = token;


            return "切换成功.";

        }
        /// <summary>
        /// 初始化
        /// </summary>
        /// <returns></returns>
        public string Home_Init()
        {
            BP.CCFast.Portal.WindowTemplates ens = new BP.CCFast.Portal.WindowTemplates();
            ens.Retrieve(WindowTemplateAttr.PageID, this.PageID, "Idx");
            if (ens.Count == 0 && this.PageID.Equals("Home") == true)
            {
                ens.InitHomePageData(); //初始化数据.
                ens.Retrieve(WindowTemplateAttr.PageID, this.PageID, "Idx");
            }

            //初始化数据.
            ens.InitDocs();

            DataTable dt = ens.ToDataTableField();
            dt.TableName = "WindowTemplates";

            return BP.Tools.Json.ToJson(dt);
        }


        public string Home_DoMove()
        {
            string[] mypks = this.MyPK.Split(',');
            for (int i = 0; i < mypks.Length; i++)
            {
                string str = mypks[i];
                if (str == null || str == "")
                    continue;

                string sql = "UPDATE GPM_WindowTemplate SET Idx=" + i + " WHERE No='" + str + "' AND PageID='" + this.PageID + "' ";
                DBAccess.RunSQL(sql);
            }
            return "移动成功..";
        }
        /// <summary>
        /// 构造函数
        /// </summary>
        public WF_Portal()
        {
        }
        public string CheckEncryptEnable()
        {
            if (SystemConfig.isEnablePasswordEncryption == true)
                return "1";
            return "0";
        }
        /// <summary>
        /// 系统信息
        /// </summary>
        /// <returns></returns>
        public string Login_InitInfo()
        {
            Hashtable ht = new Hashtable();
            ht.Add("SysNo", BP.Difference.SystemConfig.SysNo);
            ht.Add("SysName", BP.Difference.SystemConfig.SysName);
            ht.Add("OSModel", (int)SystemConfig.CCBPMRunModel);

            // 0=内网模式, 1=运营模式.
            ht.Add("SaaSModel", SystemConfig.GetValByKey("SaaSModel", "0"));

            return BP.Tools.Json.ToJson(ht);
        }
        /// <summary>
        /// 初始化登录界面.
        /// </summary>
        /// <returns></returns>
        public string Login_Init()
        {
            /*DTS.GenerSKeyWords gsw = new DTS.GenerSKeyWords();
            gsw.Do();*/
            //判断是否已经安装数据库，是否需要更新
            if (CheckIsDBInstall() == true)
                return "url@/WF/Admin/DBInstall.htm";

            #region 如果是saas模式.
            if (SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
            {
                if (DataType.IsNullOrEmpty(this.GetRequestVal("OrgNo")) == true)
                    return "url@/Portal/SaaS/SelectOneOrg.htm";
                else
                    return "url@/Portal/SaaS/Login.htm?OrgNo=" + this.OrgNo;
            }
            #endregion 如果是saas模式.


            string doType = GetRequestVal("LoginType");
            if (DataType.IsNullOrEmpty(doType) == false && doType.Equals("Out") == true)
            {
                //清空cookie
                WebUser.Exit();
                return "成功退出.";
            }

            //是否需要自动登录。 这里都把cookeis的数据获取来了.
            string userNo = this.GetRequestVal("UserNo");
            string sid = this.GetRequestVal("Token");

            if (String.IsNullOrEmpty(sid) == false && String.IsNullOrEmpty(userNo) == false)
            {
                //调用登录方法.
                BP.WF.Dev2Interface.Port_Login(this.UserNo, this.SID);
                return "url@Apps.htm?UserNo=" + this.UserNo + "&Token=" + SID;

            }

            Hashtable ht = new Hashtable();
            ht.Add("SysName", BP.Difference.SystemConfig.SysName);
            ht.Add("SysNo", BP.Difference.SystemConfig.SysNo);
            ht.Add("ServiceTel", BP.Difference.SystemConfig.ServiceTel);
            ht.Add("CustomerName", BP.Difference.SystemConfig.CustomerName);
            if (WebUser.NoOfRel == null)
            {
                ht.Add("UserNo", "");
                ht.Add("UserName", "");
            }
            else
            {
                ht.Add("UserNo", WebUser.No);

                string name = WebUser.Name;

                if (DataType.IsNullOrEmpty(name) == true)
                    ht.Add("UserName", WebUser.No);
                else
                    ht.Add("UserName", name);
            }

            return BP.Tools.Json.ToJsonEntityModel(ht);
        }
        public string Login_VerifyState()
        {
            if (!DataType.IsNullOrEmpty(HttpContextHelper.RequestCookieGet(this.ToString() + "_Login_Error", "CCS")))
            {
                return "err@" + Login_VerifyCode();
            }

            return "无需验证";
        }

        public string Login_VerifyCode()
        {
            string userNo = this.GetRequestVal("TB_No");
            return Verify.DrawImage(5, this.ToString(), "Login_Error", "VerifyCode", userNo);
        }
        private bool IsCheckCode = true;

        // 记录失败次数
        private static Hashtable failRecord = new Hashtable();
        // 记录锁定用户
        private static Hashtable lockTable = new Hashtable();

        private Int64 getTimeStamp()
        {
            TimeSpan ts = DateTime.Now - new DateTime(1970, 1, 1, 0, 0, 0, 0);
            return Convert.ToInt64(ts.TotalSeconds);
        }

        // 判断用户是否被锁定
        private bool isBeenLock(String userNo)
        {
            // 如果包含此用户，判断是否到锁定结束时间。
            if (lockTable.ContainsKey(userNo))
            {
                // 如果正被锁定
                if (this.getTimeStamp() < (Int64)lockTable[userNo])
                {
                    return true;
                }
                else
                {
                    // 超时解锁用户
                    lockTable.Remove(userNo);
                    failRecord.Remove(userNo);
                    return false;
                }
            }
            return false;
        }

        private void handleLoginFail(string userNo)
        {
            // 没有记录则新增
            if (!failRecord.ContainsKey(userNo))
            {
                int failCount = 1;
                failRecord.Add(userNo, failCount);

            }
            else
            {
                try
                {
                    int failCount = Convert.ToInt32(failRecord[userNo]);
                    failCount++;
                    failRecord[userNo] = failCount;
                    if (failCount >= 3 && !lockTable.ContainsKey(userNo))
                    {
                        lockTable.Add(userNo, this.getTimeStamp() + Convert.ToInt64(BP.Difference.SystemConfig.UserLockTimeSeconds));
                    }
                }
                catch
                {
                    failRecord[userNo] = 1;
                }
            }
        }

        public string Login_Submit()
        {
            try
            {
                string gotoSystem = this.GetRequestVal("DDL_System");
                if (DataType.IsNullOrEmpty(gotoSystem) == true)
                    gotoSystem = "";

                //是不是中间件.
                string val = this.GetRequestVal("IsZZJ");
                if (DataType.IsNullOrEmpty(val) == true)
                    val = "0";
                if (val.Equals("1") == true)
                    gotoSystem = "CCFlow";

                string userNo = this.GetRequestVal("TB_No");
                if (this.isBeenLock(userNo))
                {
                    return "err@账号已被锁定";
                }
                if (userNo == null)
                    userNo = this.GetRequestVal("TB_UserNo");

                userNo = userNo.Trim();

                #region 先校验用户名也密码.

                string pass = this.GetRequestVal("TB_PW");
                if (pass == null)
                    pass = this.GetRequestVal("TB_Pass");

                pass = pass.Trim();
                BP.Port.Emp emp = new BP.Port.Emp();
                emp.UserID = userNo;
                //是否存在用户
                bool isExist = emp.RetrieveFromDBSources() == 0 ? false : true;
                if (isExist == false && DBAccess.IsExitsTableCol("Port_Emp", "NikeName") == true)
                {
                    /*如果包含昵称列,就检查昵称是否存在.*/
                    Paras ps = new Paras();
                    ps.SQL = "SELECT No FROM Port_Emp WHERE NikeName=" + BP.Difference.SystemConfig.AppCenterDBVarStr + "NikeName";
                    ps.Add("NikeName", userNo);
                    string no = DBAccess.RunSQLReturnStringIsNull(ps, null);
                    if (DataType.IsNullOrEmpty(no) == false)
                    {
                        emp.No = no;
                        if (emp.RetrieveFromDBSources() != 0)
                            isExist = true;
                    }
                }
                if (isExist == false && DBAccess.IsExitsTableCol("Port_Emp", "Tel") == true)
                {
                    /*如果包含Name列,就检查Name是否存在.*/
                    Paras ps = new Paras();
                    ps.SQL = "SELECT No FROM Port_Emp WHERE Tel=" + BP.Difference.SystemConfig.AppCenterDBVarStr + "Tel";
                    ps.Add("Tel", userNo);
                    string no = DBAccess.RunSQLReturnStringIsNull(ps, null);
                    if (DataType.IsNullOrEmpty(no) == false)
                    {
                        emp.No = no;
                        if (emp.RetrieveFromDBSources() != 0)
                            isExist = true;
                    }
                }
                if (isExist == false && DBAccess.IsExitsTableCol("Port_Emp", "Email") == true)
                {
                    /*如果包含Name列,就检查Name是否存在.*/
                    Paras ps = new Paras();
                    ps.SQL = "SELECT No FROM Port_Emp WHERE Email=" + BP.Difference.SystemConfig.AppCenterDBVarStr + "Email";
                    ps.Add("Email", userNo);
                    string no = DBAccess.RunSQLReturnStringIsNull(ps, null);
                    if (DataType.IsNullOrEmpty(no) == false)
                    {
                        emp.No = no;
                        if (emp.RetrieveFromDBSources() != 0)
                            isExist = true;
                    }
                }
                if (isExist == false)
                {
                    this.handleLoginFail(userNo);
                    return "err@用户名或密码错误.";
                }

                #region 校验验证码.
                //WFEmp wfEmp = new WFEmp();
                //wfEmp.No = emp.UserID;
                //if (wfEmp.RetrieveFromDBSources() == 0)
                //{
                //    wfEmp.Name = emp.Name;
                //    wfEmp.DeptNo = emp.DeptNo;
                //    wfEmp.Insert();
                //}
                //string code = wfEmp.GetParaString("VerifyCode");

                //if (DataType.IsNullOrEmpty(code) == false)
                //{
                //    string strMd5 = this.GetRequestVal("VerifyCode");
                //    if (DataType.IsNullOrEmpty(strMd5)==true)
                //        strMd5 = "";
                //    else
                //        strMd5 = Convert.ToBase64String(MD5.Create().ComputeHash(Encoding.UTF8.GetBytes(strMd5))).Replace("+", "%2B");

                //    if (code.Equals(strMd5) == false)
                //        return "err@验证码错误.";

                //    //清空验证信息
                //    wfEmp.SetPara("VerifyCode", "");
                //    wfEmp.Update();

                //    //var ccsCks = HttpContext.Current.Request.Cookies["CCS"];
                //    //if (ccsCks != null)
                //    //{
                //    //    ccsCks.Expires = DateTime.Today.AddDays(-1);
                //    //    HttpContextHelper.Response.Cookies.Add(ccsCks);
                //    //    HttpContextHelper.Request.Cookies.Remove("CCS");
                //    //}

                //}
                #endregion 校验验证码.

                if (emp.CheckPass(pass) == false)
                {
                    this.handleLoginFail(userNo);
                    return "err@用户名或密码错误.";
                }


                #endregion 先校验用户名也密码.

                if (DataType.IsNullOrEmpty(userNo) == false && userNo.Equals("admin"))
                {
                    try
                    {
                        // 执行升级
                        BP.WF.Glo.UpdataCCFlowVer();
                    }
                    catch (Exception ex)
                    {
                        BP.WF.Glo.UpdataCCFlowVer();
                        string msg = "err@升级失败(ccbpm有自动修复功能,您可以刷新一下系统会自动创建字段,刷新多次扔解决不了问题,请反馈给我们)";
                        msg += "@系统信息:" + ex.Message;
                        return msg;
                    }
                }
                string token = "";
                if (Glo.CCBPMRunModel == CCBPMRunModel.Single)
                {
                    BP.WF.Dev2Interface.Port_Login(emp.UserID);
                    //调用登录方法.
                    if (DBAccess.IsExitsTableCol("Port_Emp", "EmpSta") == true)
                    {
                        string sql = "SELECT EmpSta FROM Port_Emp WHERE No='" + emp.No + "'";
                        if (DBAccess.RunSQLReturnValInt(sql, 0) == 1)
                            return "err@该用户已经被禁用.";
                    }
                    token = BP.WF.Dev2Interface.Port_GenerToken("PC");

                    if (gotoSystem.Equals("CCFlow") == true)
                        return "url@/WF/AppClassic/Home.htm?Token=" + token + "&UserNo=" + emp.UserID;
                    else
                        return "url@Default.htm?Token=" + token + "&UserNo=" + emp.UserID;
                }

                //设置他的组织，信息.
                WebUser.No = emp.UserID; //登录帐号.
                WebUser.DeptNo = emp.DeptNo;
                WebUser.DeptName = emp.DeptText;

                //执行登录.
                BP.WF.Dev2Interface.Port_Login(emp.UserID, emp.OrgNo);
                token = BP.WF.Dev2Interface.Port_GenerToken("PC");
                //HttpContextHelper.RedisUtils.Set("WebUser_" + token, token);

                return "url@Default.htm?Token=" + token + "&UserNo=" + emp.UserID + "&OrgNo=" + emp.OrgNo;
                //return "url@SelectOneOrg.htm?Token=" + token + "&UserNo=" + emp.UserID + "&OrgNo=" + emp.OrgNo;
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }
        /// <summary>
        /// 登录.
        /// </summary>
        /// <returns></returns>
        public string Login_SubmitBak()
        {
            try
            {
                string userNo = this.GetRequestVal("TB_No");
                if (userNo == null)
                    userNo = this.GetRequestVal("TB_UserNo");

                userNo = userNo.Trim();

                //if (IsCheckCode == true)
                //{
                //    string verifyCode = this.GetRequestVal("VerifyCode");
                //    string atParaStr = DBAccess.RunSQLReturnString("select AtPara from wf_emp where no='" + userNo + "'");

                //    AtPara atPara = new AtPara(atParaStr);

                //    string checkVerifyCode = atPara.GetValStrByKey(this.ToString() + "_VerifyCode");// HttpUtility.UrlDecode(HttpContextHelper.RequestCookieGet(this.ToString() + "_VerifyCode", "CCS"));
                //    string strMd5 = string.IsNullOrEmpty(verifyCode) ? "" : Convert.ToBase64String(MD5.Create().ComputeHash(Encoding.UTF8.GetBytes(verifyCode)));

                //    //string login_Error = atPara.GetValStrByKey(this.ToString() + "_Login_Error"); //HttpContextHelper.RequestCookieGet(this.ToString() + "_Login_Error", "CCS");

                //    //if (string.IsNullOrEmpty(login_Error) == true && string.IsNullOrEmpty(verifyCode) == false)
                //    //    return "err@错误的验证状态.";

                //    if (string.IsNullOrEmpty(checkVerifyCode) == false && checkVerifyCode != strMd5)
                //        return "err@验证码错误.";

                //    var ccsCks = HttpContext.Current.Request.Cookies["CCS"];
                //    if (ccsCks != null)
                //    {
                //        ccsCks.Expires = DateTime.Today.AddDays(-1);
                //        HttpContextHelper.Response.Cookies.Add(ccsCks);
                //        HttpContextHelper.Request.Cookies.Remove("CCS");
                //    }
                //}



                string pass = this.GetRequestVal("TB_PW");
                if (pass == null)
                    pass = this.GetRequestVal("TB_Pass");

                pass = pass.Trim();
                //pass = HttpUtility.UrlDecode(pass,Encoding.UTF8);

                BP.Port.Emp emp = new BP.Port.Emp();
                emp.UserID = userNo;
                if (emp.RetrieveFromDBSources() == 0)
                {
                    if (DBAccess.IsExitsTableCol("Port_Emp", "NikeName") == true)
                    {
                        /*如果包含昵称列,就检查昵称是否存在.*/
                        Paras ps = new Paras();
                        ps.SQL = "SELECT No FROM Port_Emp WHERE NikeName=" + BP.Difference.SystemConfig.AppCenterDBVarStr + "NikeName";
                        ps.Add("NikeName", userNo);
                        string no = DBAccess.RunSQLReturnStringIsNull(ps, null);
                        if (no == null)
                        {
                            return "err@用户名或者密码错误.";
                            //HttpContextHelper.AddCookie("CCS", this.ToString() + "_Login_Error", this.ToString() + "_Login_Error");
                        }

                        emp.No = no;
                        int i = emp.RetrieveFromDBSources();
                        if (i == 0)
                        {
                            //HttpContextHelper.AddCookie("CCS", this.ToString() + "_Login_Error", this.ToString() + "_Login_Error");
                            return "err@用户名或者密码错误.";
                        }
                    }

                    if (DBAccess.IsExitsTableCol("Port_Emp", "Tel") == true)
                    {
                        /*如果包含Name列,就检查Name是否存在.*/
                        Paras ps = new Paras();
                        ps.SQL = "SELECT No FROM Port_Emp WHERE Tel=" + BP.Difference.SystemConfig.AppCenterDBVarStr + "Tel";
                        ps.Add("Tel", userNo);
                        string no = DBAccess.RunSQLReturnStringIsNull(ps, null);
                        if (no == null)
                        {
                            //HttpContextHelper.AddCookie("CCS", this.ToString() + "_Login_Error", this.ToString() + "_Login_Error");
                            return "err@用户名或者密码错误.";
                        }

                        emp.No = no;
                        int i = emp.RetrieveFromDBSources();
                        if (i == 0)
                        {
                            //HttpContextHelper.AddCookie("CCS", this.ToString() + "_Login_Error", this.ToString() + "_Login_Error");
                            return "err@用户名或者密码错误.";
                        }
                    }
                    else
                    {
                        //HttpContextHelper.AddCookie("CCS", this.ToString() + "_Login_Error", this.ToString() + "_Login_Error");
                        return "err@用户名或者密码错误.";
                    }
                }

                if (emp.CheckPass(pass) == false)
                {
                    //HttpContextHelper.AddCookie("CCS", this.ToString() + "_Login_Error", this.ToString() + "_Login_Error");
                    return "err@用户名或者密码错误.";
                }

                //清空登录错误的信息
                string str = DBAccess.RunSQLReturnString("select AtPara from wf_emp where no='" + userNo + "'");

                AtPara ap = new AtPara(str);
                ap.SetVal(this.ToString() + "_VerifyCode", "");
                //ap.SetVal(this.ToString() + "_Login_Error", "");
                DBAccess.RunSQL("update wf_emp set atPara='" + ap.GenerAtParaStrs() + "' where no='" + userNo + "'");



                if (DataType.IsNullOrEmpty(userNo) == false && userNo.Equals("admin"))
                {
                    try
                    {
                        // 执行升级
                        BP.WF.Glo.UpdataCCFlowVer();
                    }
                    catch (Exception ex)
                    {
                        BP.WF.Glo.UpdataCCFlowVer();
                        string msg = "err@升级失败(ccbpm有自动修复功能,您可以刷新一下系统会自动创建字段,刷新多次扔解决不了问题,请反馈给我们)";
                        msg += "@系统信息:" + ex.Message;
                        return msg;
                    }
                }

                if (Glo.CCBPMRunModel == CCBPMRunModel.Single)
                {
                    BP.WF.Dev2Interface.Port_Login(emp.UserID);
                    //调用登录方法.
                    if (DBAccess.IsExitsTableCol("Port_Emp", "EmpSta") == true)
                    {
                        string sql = "SELECT EmpSta FROM Port_Emp WHERE No='" + emp.No + "'";
                        if (DBAccess.RunSQLReturnValInt(sql, 1) == 1)
                            return "err@该用户已经被禁用.";
                    }
                    return "url@Default.htm?Token=" + BP.WF.Dev2Interface.Port_GenerToken("PC") + "&UserNo=" + emp.UserID;
                }

                //获得当前管理员管理的组织数量.
                OrgAdminers adminers = null;

                //查询他管理多少组织.
                adminers = new OrgAdminers();
                adminers.Retrieve(OrgAdminerAttr.FK_Emp, emp.UserID);
                if (adminers.Count == 0)
                {
                    BP.WF.Port.Admin2Group.Orgs orgs = new BP.WF.Port.Admin2Group.Orgs();
                    int i = orgs.Retrieve("Adminer", this.GetRequestVal("TB_No"));
                    if (i == 0)
                    {
                        //调用登录方法.
                        BP.WF.Dev2Interface.Port_Login(emp.UserID, emp.OrgNo);
                        return "url@Default.htm?Token=" + BP.WF.Dev2Interface.Port_GenerToken("PC") + "&UserNo=" + emp.UserID + "&OrgNo=" + emp.OrgNo;
                    }

                    foreach (BP.WF.Port.Admin2Group.Org org in orgs)
                    {
                        OrgAdminer oa = new OrgAdminer();
                        oa.EmpNo = WebUser.No;
                        oa.OrgNo = org.No;
                        oa.Save();
                    }
                    adminers.Retrieve(OrgAdminerAttr.FK_Emp, emp.UserID);
                }

                //设置他的组织，信息.
                WebUser.No = emp.UserID; //登录帐号.
                WebUser.DeptNo = emp.DeptNo;
                WebUser.DeptName = emp.DeptText;

                //执行登录.
                BP.WF.Dev2Interface.Port_Login(emp.UserID, emp.OrgNo);

                string token = BP.WF.Dev2Interface.Port_GenerToken("PC");

                //判断是否是多个组织的情况.
                if (adminers.Count == 1)
                    return "url@Default.htm?Token=" + token + "&UserNo=" + emp.UserID + "&OrgNo=" + emp.OrgNo;

                //return "url@Default.htm?Token=" + token + "&UserNo=" + emp.UserID + "&OrgNo=" + emp.OrgNo;


                return "url@SelectOneOrg.htm?Token=" + token + "&UserNo=" + emp.UserID + "&OrgNo=" + emp.OrgNo;
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }
        public string Login_SubmitVue3()
        {
            this.IsCheckCode = false;
            return Login_Submit();
        }
        private bool CheckIsDBInstall()
        {
            //检查数据库连接.
            try
            {
                DBAccess.TestIsConnection();
            }
            catch (Exception ex)
            {
                throw new Exception("err@异常信息:" + ex.Message);
            }

            //检查是否缺少Port_Emp 表，如果没有就是没有安装.
            if (DBAccess.IsExitsObject("Port_Emp") == false && DBAccess.IsExitsObject("WF_Flow") == false)
                return true;

            //如果没有流程表，就执行安装.
            if (DBAccess.IsExitsObject("WF_Flow") == false)
                return true;
            return false;
        }

        #region Frm.htm 表单.
        /// <summary>
        /// 表单树.
        /// </summary>
        /// <returns></returns>
        public string Frms_InitSort()
        {

            //获得数量.
            string sqlWhere = "";
            string sql = "";
            //集团模式且一个部门下维护一套角色体系
            if (SystemConfig.CCBPMRunModel == CCBPMRunModel.GroupInc)
            {
                //如果当前管理员登录的部门是主部门
                Paras ps = new Paras();
                BP.Port.Emp emp = new BP.Port.Emp(WebUser.No);
                DataTable dt = null;
                if (emp.DeptNo.Equals(WebUser.DeptNo) == false)
                {
                    sql = "SELECT No,Name,ParentNo From Sys_FormTree WHERE No='" + WebUser.DeptNo + "' Order By Idx";
                    dt = DBAccess.RunSQLReturnTable(sql);
                    if (dt.Rows.Count == 0)
                    {
                        //根据这个部门编号生成一个流程类别
                        BP.WF.Template.SysFormTree formTree = new Template.SysFormTree();
                        formTree.No = WebUser.DeptNo;
                        formTree.ParentNo = WebUser.OrgNo;
                        formTree.Name = WebUser.DeptName;
                        formTree.OrgNo = WebUser.OrgNo;
                        formTree.DirectInsert();
                    }
                }
                sql = "SELECT No,Name,ParentNo From Sys_FormTree WHERE OrgNo='" + WebUser.OrgNo + "'  Order By Idx ";
                //if (WebUser.No.Equals("admin") == true)
                //   sql = "SELECT No,Name,ParentNo From Sys_FormTree Order By Idx";
                ps.SQL = sql;
                dt = DBAccess.RunSQLReturnTable(ps);
                return BP.Tools.Json.ToJson(dt);
            }
            if (BP.Difference.SystemConfig.CCBPMRunModel != CCBPMRunModel.Single)
            {
                sqlWhere = "   OrgNo='" + BP.Web.WebUser.OrgNo + "' AND No!='" + WebUser.OrgNo + "'";
                if (SystemConfig.CCBPMRunModel == CCBPMRunModel.GroupInc && SystemConfig.GroupStationModel == 2)
                {
                    BP.WF.Port.AdminGroup.Org org = new BP.WF.Port.AdminGroup.Org(WebUser.OrgNo);
                    if (WebUser.No.Equals(org.Adminer) == false)
                        sqlWhere += " AND No IN(SELECT FrmTreeNo From Port_OrgAdminerFrmTree Where OrgNo='" + BP.Web.WebUser.OrgNo + "' AND FK_Emp='" + WebUser.No + "')";
                }
            }
            else
                sqlWhere = "   No!='100' ";


            //求内容.
            sql = "SELECT No as \"No\",Name as \"Name\" FROM Sys_FormTree WHERE  " + sqlWhere + " ORDER BY Idx ";
            if (SystemConfig.CCBPMRunModel == CCBPMRunModel.Single)
            {
                GloVar gloVar = new GloVar();
                gloVar.No = WebUser.DeptNo + "_" + WebUser.No + "_Adminer";
                if (gloVar.RetrieveFromDBSources() != 0)
                {
                    sql = "SELECT No as \"No\",Name as \"Name\" FROM Sys_FormTree WHERE  No='" + WebUser.DeptNo + "' OR ParentNo='" + WebUser.DeptNo + "' ORDER BY Idx ";

                }
            }
            DataTable dtSort = DBAccess.RunSQLReturnTable(sql);
            if (BP.Difference.SystemConfig.AppCenterDBFieldCaseModel != FieldCaseModel.None)
            {
                dtSort.Columns[0].ColumnName = "No";
                dtSort.Columns[1].ColumnName = "Name";
                //dtSort.Columns[2].ColumnName = "WFSta2";
                //dtSort.Columns[3].ColumnName = "WFSta3";
                //dtSort.Columns[4].ColumnName = "WFSta5";
            }
            return BP.Tools.Json.ToJson(dtSort);
        }
        /// <summary>
        /// 表单
        /// </summary>
        /// <returns></returns>
        public string Frms_Init()
        {
            //获得流程实例的数量.
            string sqlWhere = "";
            string sql = "";
            if (BP.Difference.SystemConfig.CCBPMRunModel != CCBPMRunModel.Single)
                sqlWhere = " AND OrgNo='" + BP.Web.WebUser.OrgNo + "'";


            //求流程内容.
            sql = "SELECT No as \"No\",Name as \"Name\",FrmType,FK_FormTree,PTable,DBSrc,Icon,EntityType,Ver FROM Sys_MapData WHERE 1=1 " + sqlWhere + " ORDER BY Idx ";
            DataTable dtFlow = null;
            try
            {
                dtFlow = DBAccess.RunSQLReturnTable(sql);
            }
            catch (Exception ex)
            {
                MapData md = new MapData();
                md.CheckPhysicsTable();
                dtFlow = DBAccess.RunSQLReturnTable(sql);
            }

            if (BP.Difference.SystemConfig.AppCenterDBFieldCaseModel != FieldCaseModel.None)
            {
                dtFlow.Columns[0].ColumnName = "No";
                dtFlow.Columns[1].ColumnName = "Name";
                dtFlow.Columns[2].ColumnName = "FrmType";
                dtFlow.Columns[3].ColumnName = "FK_FormTree";
                dtFlow.Columns[4].ColumnName = "PTable";
                dtFlow.Columns[5].ColumnName = "DBSrc";
                dtFlow.Columns[6].ColumnName = "Icon";
                dtFlow.Columns[7].ColumnName = "EntityType";
                dtFlow.Columns[8].ColumnName = "Ver";
                //dtFlow.Columns[2].ColumnName = "WorkModel";
                //dtFlow.Columns[3].ColumnName = "AtPara";
                //dtFlow.Columns[4].ColumnName = "FK_FlowSort";
                //dtFlow.Columns[5].ColumnName = "WFSta2";
                //dtFlow.Columns[6].ColumnName = "WFSta3";
                //dtFlow.Columns[7].ColumnName = "WFSta5";
            }
            return BP.Tools.Json.ToJson(dtFlow);
        }
        /// <summary>
        /// 流程移动.
        /// </summary>
        /// <returns></returns>
        public string Frms_Move()
        {
            string sortNo = this.GetRequestVal("SortNo");
            string[] flowNos = this.GetRequestVal("EnNos").Split(',');
            for (int i = 0; i < flowNos.Length; i++)
            {
                string flowNo = flowNos[i];

                string sql = "UPDATE Sys_MapData SET FK_FormTree ='" + sortNo + "',Idx=" + i + " WHERE No='" + flowNo + "'";
                DBAccess.RunSQL(sql);
            }
            return "表单顺序移动成功..";
        }
        public string Frms_MoveSort()
        {
            string[] ens = this.GetRequestVal("SortNos").Split(',');

            SysFormTree ft = new SysFormTree();

            string table = ft.EnMap.PhysicsTable;

            for (int i = 0; i < ens.Length; i++)
            {
                string en = ens[i];

                string sql = "UPDATE " + table + " SET Idx=" + i + " WHERE No='" + en + "'";
                DBAccess.RunSQL(sql);
            }
            return "目录移动成功..";
        }

        #endregion Frm.htm 表单.

        #region 流程树.
        /// <summary>
        /// 初始化
        /// </summary>
        /// <returns></returns>
        public string FlowTree_InitSort()
        {
            //   if (SystemConfig.CCBPMRunModel==)
            return "";
        }
        #endregion 流程树.


        #region Flows.htm 流程.
        public string Flows_Init()
        {
            //获得流程实例的数量.
            string sqlWhere = "";
            string sql = "";
            if (SystemConfig.CCBPMRunModel != CCBPMRunModel.Single)
                sqlWhere = " AND OrgNo='" + BP.Web.WebUser.OrgNo + "'";

            //求流程数量.
            sql = "SELECT FK_Flow,WFState, COUNT(*) AS Num FROM WF_GenerWorkFlow WHERE 1=1 " + sqlWhere + " GROUP BY FK_Flow, WFState ";
            DataTable dt = DBAccess.RunSQLReturnTable(sql);

            //求流程内容.
            sql = "SELECT No as \"No\",Name as \"Name\",WorkModel, FK_FlowSort, 0 as WFSta2, 0 as WFSta3, 0 as WFSta5, Ver FROM WF_Flow WHERE 1=1 " + sqlWhere + " ORDER BY Idx ";
            DataTable dtFlow = DBAccess.RunSQLReturnTable(sql);
            if (BP.Difference.SystemConfig.AppCenterDBFieldCaseModel != FieldCaseModel.None)
            {
                dtFlow.Columns[0].ColumnName = "No";
                dtFlow.Columns[1].ColumnName = "Name";
                dtFlow.Columns[2].ColumnName = "WorkModel";
                //dtFlow.Columns[3].ColumnName = "AtPara";
                dtFlow.Columns[3].ColumnName = "FK_FlowSort";
                dtFlow.Columns[4].ColumnName = "WFSta2";
                dtFlow.Columns[5].ColumnName = "WFSta3";
                dtFlow.Columns[6].ColumnName = "WFSta5";
                dtFlow.Columns[7].ColumnName = "Ver";
            }

            // 给状态赋值.
            foreach (DataRow dr in dtFlow.Rows)
            {
                string flowNo = dr[0] as string;
                foreach (DataRow mydr in dt.Rows)
                {
                    string fk_flow = mydr[0].ToString();
                    if (fk_flow.Equals(flowNo) == false)
                        continue;

                    int wfstate = int.Parse(mydr[1].ToString());
                    int Num = int.Parse(mydr[2].ToString());
                    if (wfstate == 2)
                        dr["WFSta2"] = Num;
                    if (wfstate == 3)
                        dr["WFSta3"] = Num;
                    if (wfstate == 5)
                        dr["WFSta5"] = Num;
                    break;
                }
            }
            return BP.Tools.Json.ToJson(dtFlow);
        }
        /// <summary>
        /// 流程移动.
        /// </summary>
        /// <returns></returns>
        public string Flows_Move()
        {
            string sourceSortNo = this.GetRequestVal("SourceSortNo");
            string sourceFlowNos = this.GetRequestVal("SourceFlowNos");
            string toSortNo = this.GetRequestVal("ToSortNo");
            string toFlowNos = this.GetRequestVal("ToFlowNos");
            string[] flowNos = sourceFlowNos.Split(',');
            for (int i = 0; i < flowNos.Length; i++)
            {
                string flowNo = flowNos[i];

                string sql = "UPDATE WF_Flow SET FK_FlowSort ='" + sourceSortNo + "',Idx=" + i + " WHERE No='" + flowNo + "'";
                DBAccess.RunSQL(sql);
            }
            //如果是在同一个流程类别中拖动流程顺序
            if (sourceSortNo.Equals(toSortNo) == true)
                return "流程顺序移动成功..";
            flowNos = toFlowNos.Split(',');
            for (int i = 0; i < flowNos.Length; i++)
            {
                string flowNo = flowNos[i];

                string sql = "UPDATE WF_Flow SET FK_FlowSort ='" + toSortNo + "',Idx=" + i + " WHERE No='" + flowNo + "'";
                DBAccess.RunSQL(sql);
            }
            return "流程顺序移动成功..";
        }
        public string Flows_MoveSort()
        {
            string[] ens = this.GetRequestVal("SortNos").Split(',');
            for (int i = 0; i < ens.Length; i++)
            {
                string en = ens[i];

                string sql = "UPDATE WF_FlowSort SET Idx=" + i + " WHERE No='" + en + "'";
                DBAccess.RunSQL(sql);
            }
            return "目录移动成功..";
        }
        #endregion 流程.

        #region 消息.

        /// <summary>
        /// 消息初始化
        /// </summary>
        /// <returns></returns>
        public string Message_Init()
        {
            //获得消息.
            string sql = "SELECT a.MyPK, a.EmailTitle,a.EmailDoc,a.EmailSta, a.RDT,a.Sender, a.AtPara,a.MsgType,a.IsRead,b.Name FROM Sys_SMS a, port_emp b WHERE SendTo='" + WebUser.No + "' and a.sender = b.No ORDER BY IsRead ";
            DataTable infos = DBAccess.RunSQLReturnTable(sql);
            infos.TableName = "Messages";
            if (SystemConfig.AppCenterDBFieldCaseModel == FieldCaseModel.Lowercase)
            {
                infos.Columns["mypk"].ColumnName = "MyPK";
                infos.Columns["emailtitle"].ColumnName = "EmailTitle";
                infos.Columns["emaildoc"].ColumnName = "EmailDoc";
                infos.Columns["emailsta"].ColumnName = "EmailSta";
                infos.Columns["rdt"].ColumnName = "RDT";
                infos.Columns["sender"].ColumnName = "Sender";
                infos.Columns["atpara"].ColumnName = "AtPara";
                infos.Columns["msgtype"].ColumnName = "MsgType";
                infos.Columns["isread"].ColumnName = "IsRead";
                infos.Columns["name"].ColumnName = "Name";
            }
            //@hongyan 更新
            if (SystemConfig.AppCenterDBFieldCaseModel == FieldCaseModel.UpperCase)
            {
                infos.Columns["MYPK"].ColumnName = "MyPK";
                infos.Columns["EMAILTITLE"].ColumnName = "EmailTitle";
                infos.Columns["EMAILDOC"].ColumnName = "EmailDoc";
                infos.Columns["EMAILSTA"].ColumnName = "EmailSta";
                infos.Columns["RDT"].ColumnName = "RDT";
                infos.Columns["SENDER"].ColumnName = "Sender";
                infos.Columns["ATPARA"].ColumnName = "AtPara";
                infos.Columns["MSGTYPE"].ColumnName = "MsgType";
                infos.Columns["ISREAD"].ColumnName = "IsRead";
                infos.Columns["NAME"].ColumnName = "Name";
            }

            //返回信息.
            return BP.Tools.Json.ToJson(infos);
        }
        /// <summary>
        /// 发送邮件
        /// </summary>
        /// <returns></returns>
        public string SendEmail()
        {
            try
            {
                string doc = this.GetRequestVal("Doc");
                string emailTos = this.GetRequestVal("Addrs");
                string title = this.GetRequestVal("Title");
                if (emailTos.Contains(";"))
                {
                    string[] emailTs = emailTos.Split(';');
                    foreach (string item in emailTs)
                    {
                        if (DataType.IsNullOrEmpty(item))
                            continue;
                        SMS.SendEmailNowAsync(item, title, doc);
                    }
                }
                else
                {
                    SMS.SendEmailNowAsync(emailTos, title, doc);
                }


                return "发送成功";
            }
            catch (Exception ex)
            {
                return "@err:" + ex.Message;
            }
        }
        /// <summary>
        /// 给企业微信人员发送文本消息
        /// </summary>
        /// <returns></returns>
        public string SendMsgToWeiXin()
        {
            string doc = this.GetRequestVal("Doc");
            string ToUsers = this.GetRequestVal("ToUsers");
            string title = this.GetRequestVal("Title");
            try
            {
                //微信企业号ID
                string agentId = BP.Difference.SystemConfig.WX_AgentID ?? null;
                if (agentId != null)
                {
                    //申请权限，获取token
                    string accessToken = BP.Port.WeiXin.WeiXinEntity.getAccessToken();//获取 AccessToken

                    if (ToUsers.Contains(";"))
                    {
                        string[] toUsers = ToUsers.Split(';');
                        foreach (string item in toUsers)
                        {
                            if (DataType.IsNullOrEmpty(item))
                                continue;
                            MsgText msgText = new MsgText();
                            msgText.content = doc;
                            msgText.touser = item;
                            msgText.Access_Token = accessToken;
                            msgText.agentid = BP.Difference.SystemConfig.WX_AgentID;
                            msgText.safe = "0";

                            //执行发送
                            BP.Port.WeiXin.Glo.PostMsgOfText(msgText);
                        }
                    }
                    else
                    {
                        MsgText msgText = new MsgText();
                        msgText.content = doc;
                        msgText.touser = ToUsers;
                        msgText.Access_Token = accessToken;
                        msgText.agentid = BP.Difference.SystemConfig.WX_AgentID;
                        msgText.safe = "0";

                        //执行发送
                        BP.Port.WeiXin.Glo.PostMsgOfText(msgText);
                    }
                }
                return "发送成功";
            }
            catch (Exception e)
            {

                BP.DA.Log.DebugWriteError(e.ToString());
                BP.DA.Log.DebugWriteError(e.StackTrace);
                BP.DA.Log.DebugWriteError(e.Message);
                BP.DA.Log.DebugWriteError(e.InnerException);
                return "@error:发送失败:" + e.Message;
            }

        }
        #endregion 消息.
        #region 通知公告.

        /// <summary>
        /// 消息初始化
        /// </summary>
        /// <returns></returns>
        public string Info_Init()
        {

            //获得消息.
            string sql = "SELECT No, Name,Docs,InfoPRI, InfoSta,RecName, RelerName,RelDeptName,RDT FROM OA_Info WHERE InfoSta=0 ORDER BY RDT ";
            DataTable infos = DBAccess.RunSQLReturnTable(sql);
            infos.TableName = "Infos";

            DataSet ds = new DataSet();
            ds.Tables.Add(infos);


            //返回信息.
            return BP.Tools.Json.ToJson(ds);
        }
        #endregion 通知公告


        #region   加载菜单 .

        /// <summary>
        /// 获得菜单:权限.
        /// </summary>
        /// <returns></returns>
        public string Default_Init_GroupInc_Menums()
        {
            #region 0.权限准备.
            //系统.
            MySystems systems = new MySystems();
            systems.Retrieve("OrgNo", BP.Web.WebUser.OrgNo, "Idx");

            //模块.
            Modules modules = new Modules();
           // modules.Retrieve("OrgNo", BP.Web.WebUser.OrgNo, "Idx");
            modules.RetrieveAll("Idx"); // ("OrgNo", BP.Web.WebUser.OrgNo, "Idx");

            //菜单.
            Menus menus = new Menus();
            menus.RetrieveAll("Idx"); // ("OrgNo", BP.Web.WebUser.OrgNo, "Idx");
           // menus.Retrieve("OrgNo", BP.Web.WebUser.OrgNo, "Idx");
            //定义容器
            MySystems systemsCopy = new MySystems();
            Modules modulesCopy = new Modules();
            Menus menusCopy = new Menus();

            //权限中心.
            BP.CCFast.CCMenu.PowerCenters pcs = new BP.CCFast.CCMenu.PowerCenters();
            pcs.RetrieveIn("CtrlObj", "'System','Module','Menu'");

            //求出来当前登录人员的身份信息.
            string mydepts = "" + WebUser.DeptNo + ","; //我的部门.
            string mystas = ""; //我的角色.
            DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.UserID + "'");
            foreach (DataRow dr in mydeptsDT.Rows)
            {
                mydepts += dr[0].ToString() + ",";
                mystas += dr[1].ToString() + ",";
            }
            #endregion 0.权限准备.

            #region 1.处理本组织:系统..
            //首先解决系统的权限.
            string ids = ""; //求本组织的数据.
            foreach (MySystem item in systems)
            {
                //如果被禁用了.
                if (item.ItIsEnable == false) continue;

                //找到关于系统的控制权限集合.
                PowerCenters mypcs = pcs.GetEntitiesByKey(PowerCenterAttr.CtrlPKVal, item.No) as PowerCenters;
                //如果没有权限控制的描述，就默认有权限.
                if (mypcs == null)
                {
                    systemsCopy.AddEntity(item);
                    continue;
                }

                //控制遍历权限.
                foreach (PowerCenter pc in mypcs)
                {
                    if (pc.CtrlModel.Equals("AnyOrgs") == true || pc.CtrlModel.Equals("Anyone") == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    if (pc.CtrlModel.Equals("Adminer") == true && BP.Web.WebUser.No.Equals("admin") == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }

                    if (pc.CtrlModel.Equals("AdminerAndAdmin2") == true && BP.Web.WebUser.IsAdmin == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    ids = "," + pc.IDs + ",";
                    if (pc.CtrlModel.Equals("Emps") == true && ids.Contains("," + BP.Web.WebUser.No + ",") == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    //是否包含部门？
                    if (pc.CtrlModel.Equals("Depts") == true && BP.DA.DataType.IsHaveIt(pc.IDs, mydepts) == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    //是否包含角色？
                    if (pc.CtrlModel.Equals("Stations") == true && BP.DA.DataType.IsHaveIt(pc.IDs, mystas) == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    //SQL？
                    if (pc.CtrlModel.Equals("SQL") == true)
                    {
                        string sql = BP.WF.Glo.DealExp(pc.IDs, null, "");
                        if (DBAccess.RunSQLReturnValFloat(sql) > 0)
                        {
                            systemsCopy.AddEntity(item);
                        }
                        break;
                    }
                }
            }
            #endregion 1.处理本组织的数据.

            #region 2.处理共享组织:系统.
            MySystems systemOrgs = new MySystems();
            systemOrgs.Retrieve("ShareOrgSln", 1, "Idx");
            foreach (MySystem systemOrg in systemOrgs)
            {
                if (systemOrg.OrgNo.Equals(WebUser.OrgNo)) continue; //排除自己的组织.

                //按照权限处理.
                foreach (PowerCenter pc in pcs)
                {
                    if (pc.CtrlPKVal.Equals(systemOrg.No) == false)
                        continue;

                    //管理员+二级管理员..
                    if (pc.CtrlModel.Equals("Adminer") == true && WebUser.No.Equals("admin") == true)
                    {
                        systemsCopy.AddEntity(systemOrg);
                        break;
                    }
                    if (pc.CtrlModel.Equals("AdminerAndAmin2") == true && WebUser.IsAdmin == true)
                    {
                        systemsCopy.AddEntity(systemOrg);
                        break;
                    }

                    //任何组织,任何人.
                    if (pc.CtrlModel.Equals("AnyOrgs") == true || pc.CtrlModel.Equals("AnyOne") == true)
                    {
                        systemsCopy.AddEntity(systemOrg);
                        break;
                    }
                    //指定组织.
                    ids = "," + pc.IDs + ",";
                    if (pc.CtrlModel.Equals("SpecOrgs") == true && ids.Contains("," + BP.Web.WebUser.OrgNo + ",") == true)
                    {
                        systemsCopy.AddEntity(systemOrg);
                        break;
                    }

                    //指定的部门s
                    if (pc.CtrlModel.Equals("SpecOrgDepts") == true && BP.DA.DataType.IsHaveIt(pc.IDs, mydepts) == true)
                    {
                        systemsCopy.AddEntity(systemOrg);
                        break;
                    }
                    //是否包含角色？
                    if (pc.CtrlModel.Equals("SpecOrgStations") == true && BP.DA.DataType.IsHaveIt(pc.IDs, mystas) == true)
                    {
                        systemsCopy.AddEntity(systemOrg);
                        break;
                    }
                }
            }
            #endregion  2.处理共享组织的数据.

            #region 3.根据系统求出模块权限.
            foreach (MySystem item in systemsCopy)
            {
                foreach (Module module in modules)
                {
                    //如果被禁用了.
                    if (module.ItIsEnable == false) continue;
                    if (module.SystemNo.Equals(item.No) == false) continue;

                    //找到关于系统的控制权限集合.
                    PowerCenters mypcs = pcs.GetEntitiesByKey(PowerCenterAttr.CtrlPKVal, module.No) as PowerCenters;
                    //如果没有权限控制的描述，就默认有权限.
                    if (mypcs == null)
                    {
                        modulesCopy.AddEntity(module);
                        continue;
                    }

                    //控制遍历权限.
                    foreach (PowerCenter pc in mypcs)
                    {
                        if (pc.CtrlPKVal.Equals(module.No) == false)
                            continue;

                        //任何组织,任何人.
                        if (pc.CtrlModel.Equals("AnyOrgs") == true || pc.CtrlModel.Equals("Anyone") == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }
                        if (pc.CtrlModel.Equals("Adminer") == true && BP.Web.WebUser.No.Equals("admin") == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        if (pc.CtrlModel.Equals("AdminerAndAdmin2") == true && BP.Web.WebUser.IsAdmin == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        ids = "," + pc.IDs + ",";
                        if (pc.CtrlModel.Equals("Emps") == true && ids.Contains("," + BP.Web.WebUser.No + ",") == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        //是否包含部门？
                        if (pc.CtrlModel.Equals("Depts") == true && this.IsHaveIt(pc.IDs, mydepts) == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        //是否包含角色？
                        if (pc.CtrlModel.Equals("Stations") == true && this.IsHaveIt(pc.IDs, mystas) == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }
                        if (pc.CtrlModel.Equals("SQL") == true)
                        {
                            string sql = BP.WF.Glo.DealExp(pc.IDs, null, "");
                            if (DBAccess.RunSQLReturnValFloat(sql) > 0)
                            {
                                modulesCopy.AddEntity(module);
                            }
                            break;
                        }

                        //指定组织.
                        ids = "," + pc.IDs + ",";
                        if (pc.CtrlModel.Equals("SpecOrgs") == true && ids.Contains("," + BP.Web.WebUser.OrgNo + ",") == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        //指定的部门s
                        if (pc.CtrlModel.Equals("SpecOrgDepts") == true && BP.DA.DataType.IsHaveIt(pc.IDs, mydepts) == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }
                        //是否包含角色？
                        if (pc.CtrlModel.Equals("SpecOrgStations") == true && BP.DA.DataType.IsHaveIt(pc.IDs, mystas) == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }
                    }
                }
            }
            #endregion  3. 根据系统求出模块权限.

            #region 4.求出菜单权限.
            foreach (Module item in modulesCopy)
            {
                foreach (Menu menu in menus)
                {
                    //如果被禁用了.
                    if (menu.ItIsEnable == false) continue;
                    if (menu.ModuleNo.Equals(item.No) == false) continue;

                    //找到关于系统的控制权限集合.
                    PowerCenters mypcs = pcs.GetEntitiesByKey(PowerCenterAttr.CtrlPKVal, menu.No) as PowerCenters;
                    //如果没有权限控制的描述，就默认有权限.
                    if (mypcs == null)
                    {
                        menusCopy.AddEntity(menu);
                        continue;
                    }

                    //控制遍历权限.
                    foreach (PowerCenter pc in mypcs)
                    {
                        if (pc.CtrlModel.Equals("Anyone") == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }
                        if (pc.CtrlModel.Equals("Adminer") == true && BP.Web.WebUser.No.Equals("admin") == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        if (pc.CtrlModel.Equals("AdminerAndAdmin2") == true && BP.Web.WebUser.IsAdmin == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        ids = "," + pc.IDs + ",";
                        if (pc.CtrlModel.Equals("Emps") == true && ids.Contains("," + BP.Web.WebUser.No + ",") == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        //是否包含部门？
                        if (pc.CtrlModel.Equals("Depts") == true && this.IsHaveIt(pc.IDs, mydepts) == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        //是否包含角色？
                        if (pc.CtrlModel.Equals("Stations") == true && this.IsHaveIt(pc.IDs, mystas) == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        //按照SQL语句
                        if (pc.CtrlModel.Equals("SQL") == true)
                        {
                            string sql = pc.IDs;
                            if (DataType.IsNullOrEmpty(sql) == true)
                            {
                                menusCopy.AddEntity(menu);
                                break;
                            }
                            sql = BP.WF.Glo.DealExp(sql, null);
                            if (DBAccess.RunSQLReturnValInt(sql, 0) > 0)
                            {
                                menusCopy.AddEntity(menu);
                                break;
                            }
                        }
                    }
                }
            }
            #endregion 4.求出菜单权限.

            #region 5. 组装数据.
            DataSet ds = new DataSet();
            DataTable dtSystem = systemsCopy.ToDataTableField("System");
            dtSystem.Columns.Add("IsOpen");

            //给第1个系统，第1个模块设置打开状态.
            DataTable dtModule = modulesCopy.ToDataTableField("Module");
            dtModule.Columns.Add("IsOpen");
            if (dtSystem.Rows.Count > 0)
            {
                dtSystem.Rows[0]["IsOpen"] = "true";
                string systemNo = dtSystem.Rows[0]["No"].ToString();
                foreach (DataRow dr in dtModule.Rows)
                {
                    if (dr["SystemNo"].ToString().Equals(systemNo) == false)
                        continue;

                    dr["IsOpen"] = "true";
                    break;
                }
            }
            ds.Tables.Add(dtSystem);
            ds.Tables.Add(dtModule);
            DataTable dtMenu = menusCopy.ToDataTableField("Menu");
            dtMenu.Columns["UrlExt"].ColumnName = "Url";
            ds.Tables.Add(dtMenu);

            string pkval = BP.Web.WebUser.No + "_Menus";
            string json = BP.Tools.Json.ToJson(ds);
            DBAccess.SaveBigTextToDB(json, "Sys_UserRegedit", "MyPK", pkval, "BigDocs");
            try
            {
                DBAccess.RunSQL("UPDATE Sys_UserRegedit SET FK_Emp='" + BP.Web.WebUser.No + "', CfgKey='Menus',OrgNo='" + BP.Web.WebUser.OrgNo + "' WHERE MyPK='" + pkval + "'");
            }
            catch
            {
                UserRegedit ur = new UserRegedit();
                ur.CheckPhysicsTable();
            }
            #endregion 5. 组装数据.

            return json;
        }
        /// <summary>
        /// 比较两个字符串是否有交集
        /// </summary>
        /// <param name="ids1"></param>
        /// <param name="ids2"></param>
        /// <returns></returns>
        public bool IsHaveIt(string ids1, string ids2)
        {
            if (DataType.IsNullOrEmpty(ids1) == true)
                return false;
            if (DataType.IsNullOrEmpty(ids2) == true)
                return false;

            string[] str1s = ids1.Split(',');
            string[] str2s = ids2.Split(',');

            foreach (string str1 in str1s)
            {
                if (str1 == "" || str1 == null)
                    continue;

                foreach (string str2 in str2s)
                {
                    if (str2 == "" || str2 == null)
                        continue;

                    if (str2.Equals(str1) == true)
                        return true;
                }
            }
            return false;
        }
        public string Default_LogOut()
        {

            string orgNo = WebUser.OrgNo;
            BP.Web.WebUser.Exit();

            if (BP.Difference.SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
            {
                return "/Portal/SaaS/Login.htm?OrgNo=" + orgNo;
            }

            return "./Login.htm?DoType=Logout&SystemNo=CCFast";
        }
        /// <summary>
        /// 获得门户信息.
        /// </summary>
        /// <returns></returns>
        public string SystemPortal_GenerPages()
        {
            //系统表.
            MySystems systems = new MySystems();
            systems.Retrieve("SystemType", 1);
            return systems.ToJson();
        }
        /// <summary>
        /// 门户初始化数据
        /// </summary>
        /// <returns>返回数量信息</returns>
        public string SystemPortal_Init()
        {
            DataTable dt = new DataTable();
            dt.Columns.Add("No");
            dt.Columns.Add("Name");
            dt.Columns.Add("Num");

            DataRow dr = dt.NewRow();
            dr["No"] = "GL_Todolist";
            dr["Name"] = "待办";
            dr["Num"] = BP.WF.Dev2Interface.Todolist_EmpWorks;
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr["No"] = "GL_Complete";
            dr["Name"] = "已完成";
            dr["Num"] = BP.WF.Dev2Interface.Todolist_Complete;
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr["No"] = "GL_Draft";
            dr["Name"] = "草稿";
            dr["Num"] = BP.WF.Dev2Interface.Todolist_Draft;
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr["No"] = "GL_Todolist";
            dr["Name"] = "退回";
            dr["Num"] = BP.WF.Dev2Interface.Todolist_ReturnNum;
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr["No"] = "GL_CC";
            dr["Name"] = "抄送";
            dr["Num"] = BP.WF.Dev2Interface.Todolist_CCWorks;
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr["No"] = "GL_Todolist";
            dr["Name"] = "未读";
            dr["Num"] = BP.WF.Dev2Interface.Todolist_UnRead;
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            //dr["No"] = "GL_Timeout";
            dr["No"] = "GL_Todolist";
            dr["Name"] = "逾期";
            dr["Num"] = BP.WF.Dev2Interface.Todolist_OverWorkNum;
            dt.Rows.Add(dr);

            //Hashtable ht = new Hashtable();
            //ht.Add("Todolist_HungupNum", BP.WF.Dev2Interface.Todolist_HungupNum); //挂起.
            //ht.Add("Todolist_Draft", BP.WF.Dev2Interface.Todolist_Draft); //草稿.
            //ht.Add("Todolist_EmpWorks", BP.WF.Dev2Interface.Todolist_EmpWorks); //待办数
            //ht.Add("Todolist_CCWorks", BP.WF.Dev2Interface.Todolist_CCWorks); //抄送
            //ht.Add("Todolist_OverWorkNum", BP.WF.Dev2Interface.Todolist_OverWorkNum); //逾期
            //ht.Add("Todolist_UnRead", BP.WF.Dev2Interface.Todolist_UnRead); //未阅
            return BP.Tools.Json.ToJson(dt);
        }

        /// <summary>
        /// 获得指定门户信息.
        /// </summary>
        /// <returns></returns>
        public string SystemPortal_getPages()
        {
            //系统表.
            string pageId = GetRequestVal("pageId");
            MySystem system = new MySystem();
            system.Retrieve("No", pageId);
            return system.ToJson();
        }

        /// <summary>
        /// 获得指定门户导航栏气泡显示.
        /// </summary>
        /// <returns></returns>
        public string SystemPortal_getTodolist()
        {
            DataTable dt = new DataTable();

            //系统表.
            string pageId = GetRequestVal("pageId");
            MySystems systems = new MySystems();
            systems.Retrieve("No", pageId);
            if (systems.Count > 0)
            {
                int Todolist_EmpWorks = systems[0].GetValIntByKey("Todolist_EmpWorks");
                int Todolist_Draft = systems[0].GetValIntByKey("Todolist_Draft");
                int Todolist_Complete = systems[0].GetValIntByKey("Todolist_Complete");
                int Todolist_ReturnNum = systems[0].GetValIntByKey("Todolist_ReturnNum");
                int Todolist_CCWorks = systems[0].GetValIntByKey("Todolist_CCWorks");
                int Todolist_OverWorkNum = systems[0].GetValIntByKey("Todolist_OverWorkNum");
                int Todolist_UnRead = systems[0].GetValIntByKey("Todolist_UnRead");
                int Todolist_HungupNum = systems[0].GetValIntByKey("Todolist_HungupNum");


                dt.Columns.Add("No");
                dt.Columns.Add("Name");
                dt.Columns.Add("Num");
                DataRow dr = dt.NewRow();
                if (Todolist_EmpWorks == 1)
                {
                    dr = dt.NewRow();
                    dr["No"] = "GL_Todolist";
                    dr["Name"] = "待办";
                    dr["Num"] = BP.WF.Dev2Interface.Todolist_EmpWorks;
                    dt.Rows.Add(dr);
                }

                if (Todolist_Draft == 1)
                {
                    dr = dt.NewRow();
                    dr["No"] = "GL_Complete";
                    dr["Name"] = "已完成";
                    dr["Num"] = BP.WF.Dev2Interface.Todolist_Complete;
                    dt.Rows.Add(dr);
                }
                if (Todolist_Complete == 1)
                {
                    dr = dt.NewRow();
                    dr["No"] = "GL_Draft";
                    dr["Name"] = "草稿";
                    dr["Num"] = BP.WF.Dev2Interface.Todolist_Draft;
                    dt.Rows.Add(dr);
                }

                if (Todolist_ReturnNum == 1)
                {
                    dr = dt.NewRow();
                    dr["No"] = "GL_Todolist";
                    dr["Name"] = "退回";
                    dr["Num"] = BP.WF.Dev2Interface.Todolist_ReturnNum;
                    dt.Rows.Add(dr);
                }
                if (Todolist_CCWorks == 1)
                {
                    dr = dt.NewRow();
                    dr["No"] = "GL_CC";
                    dr["Name"] = "抄送";
                    dr["Num"] = BP.WF.Dev2Interface.Todolist_CCWorks;
                    dt.Rows.Add(dr);
                }

                if (Todolist_OverWorkNum == 1)
                {
                    dr = dt.NewRow();
                    dr["No"] = "GL_Todolist";
                    dr["Name"] = "逾期";
                    dr["Num"] = BP.WF.Dev2Interface.Todolist_OverWorkNum;
                    dt.Rows.Add(dr);
                }
                if (Todolist_UnRead == 1)
                {
                    dr = dt.NewRow();
                    dr["No"] = "GL_Todolist";
                    dr["Name"] = "未读";
                    dr["Num"] = BP.WF.Dev2Interface.Todolist_UnRead;
                    dt.Rows.Add(dr);
                }

                if (Todolist_HungupNum == 1)
                {
                    dr = dt.NewRow();
                    dr["No"] = "GL_Todolist";
                    dr["Name"] = "挂起";
                    dr["Num"] = BP.WF.Dev2Interface.Todolist_OverWorkNum;
                    dt.Rows.Add(dr);
                }
            }
            return BP.Tools.Json.ToJson(dt);
        }
        /// <summary>
        /// 返回构造的JSON.
        /// </summary>
        /// <returns></returns>
        public string Default_Init()
        {
            string isVue3 = this.GetRequestVal("isVue3");
            //如果是admin. 
            if (BP.Web.WebUser.No.ToLower().Equals("admin") == true && this.ItIsMobile == false)
                return Default_Init_Admin_Menums(isVue3);

            if (SystemConfig.CCBPMRunModel == CCBPMRunModel.GroupInc)
                return Default_Init_GroupInc_Menums();

            if (SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                return Default_Init_SaaS_Menums();

            return Default_Init_Single_Menums();
        }
        public string Default_Init_Single_Menums()
        {
            #region 0.权限准备.
            //系统.
            MySystems systems = new MySystems();
            systems.RetrieveAll();

            //模块.
            Modules modules = new Modules();
            modules.RetrieveAll("Idx");  
            //菜单.
            Menus menus = new Menus();
            menus.RetrieveAll("Idx");  

            //定义容器
            MySystems systemsCopy = new MySystems();
            Modules modulesCopy = new Modules();
            Menus menusCopy = new Menus();

            //权限中心.
            BP.CCFast.CCMenu.PowerCenters pcs = new BP.CCFast.CCMenu.PowerCenters();
            pcs.RetrieveIn("CtrlObj", "'System','Module','Menu'");

            //求出来当前登录人员的身份信息.
            string mydepts = "" + WebUser.DeptNo + ","; //我的部门.
            string mystas = ""; //我的角色.
            DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.UserID + "'");
            foreach (DataRow dr in mydeptsDT.Rows)
            {
                mydepts += dr[0].ToString() + ",";
                mystas += dr[1].ToString() + ",";
            }
            #endregion 0.权限准备.

            #region 1.处理系统权限.
            //首先解决系统的权限.
            string ids = ""; //求本组织的数据.
            foreach (MySystem item in systems)
            {
                //如果被禁用了.
                if (item.ItIsEnable == false) continue;

                //找到关于系统的控制权限集合.
                PowerCenters mypcs = pcs.GetEntitiesByKey(PowerCenterAttr.CtrlPKVal, item.No) as PowerCenters;
                //如果没有权限控制的描述，就默认有权限.
                if (mypcs == null)
                {
                    systemsCopy.AddEntity(item);
                    continue;
                }

                //控制遍历权限.
                foreach (PowerCenter pc in mypcs)
                {
                    if (pc.CtrlModel.Equals("Anyone") == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    if (pc.CtrlModel.Equals("Adminer") == true && BP.Web.WebUser.No.Equals("admin") == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }

                    if (pc.CtrlModel.Equals("AdminerAndAdmin2") == true && BP.Web.WebUser.IsAdmin == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    ids = "," + pc.IDs + ",";
                    if (pc.CtrlModel.Equals("Emps") == true && ids.Contains("," + BP.Web.WebUser.No + ",") == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    //是否包含部门？
                    if (pc.CtrlModel.Equals("Depts") == true && BP.DA.DataType.IsHaveIt(pc.IDs, mydepts) == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    //是否包含角色？
                    if (pc.CtrlModel.Equals("Stations") == true && BP.DA.DataType.IsHaveIt(pc.IDs, mystas) == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    //SQL？
                    if (pc.CtrlModel.Equals("SQL") == true)
                    {
                        string sql = BP.WF.Glo.DealExp(pc.IDs, null, "");
                        if (DBAccess.RunSQLReturnValFloat(sql) > 0)
                        {
                            systemsCopy.AddEntity(item);
                        }
                        break;
                    }
                }
            }
            #endregion 1.处理系统权限.

            #region 2.根据系统求出模块权限.
            foreach (MySystem item in systemsCopy)
            {
                foreach (Module module in modules)
                {
                    //如果被禁用了.
                    if (module.ItIsEnable == false) continue;
                    if (module.SystemNo.Equals(item.No) == false) continue;

                    //找到关于系统的控制权限集合.
                    PowerCenters mypcs = pcs.GetEntitiesByKey(PowerCenterAttr.CtrlPKVal, module.No) as PowerCenters;
                    //如果没有权限控制的描述，就默认有权限.
                    if (mypcs == null)
                    {
                        modulesCopy.AddEntity(module);
                        continue;
                    }

                    //控制遍历权限.
                    foreach (PowerCenter pc in mypcs)
                    {
                        if (pc.CtrlPKVal.Equals(module.No) == false)
                            continue;

                        if (pc.CtrlModel.Equals("Anyone") == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }
                        if (pc.CtrlModel.Equals("Adminer") == true && BP.Web.WebUser.No.Equals("admin") == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        if (pc.CtrlModel.Equals("AdminerAndAdmin2") == true && BP.Web.WebUser.IsAdmin == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        ids = "," + pc.IDs + ",";
                        if (pc.CtrlModel.Equals("Emps") == true && ids.Contains("," + BP.Web.WebUser.No + ",") == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        //是否包含部门？
                        if (pc.CtrlModel.Equals("Depts") == true && this.IsHaveIt(pc.IDs, mydepts) == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        //是否包含角色？
                        if (pc.CtrlModel.Equals("Stations") == true && this.IsHaveIt(pc.IDs, mystas) == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }
                        if (pc.CtrlModel.Equals("SQL") == true)
                        {
                            string sql = BP.WF.Glo.DealExp(pc.IDs, null, "");
                            if (DBAccess.RunSQLReturnValFloat(sql) > 0)
                            {
                                modulesCopy.AddEntity(module);
                            }
                            break;
                        }
                    }
                }
            }
            #endregion  2. 根据系统求出模块权限.

            #region 3.求出菜单权限.
            foreach (Module item in modulesCopy)
            {
                foreach (Menu menu in menus)
                {
                    //如果被禁用了.
                    if (menu.ItIsEnable == false) continue;
                    if (menu.ModuleNo.Equals(item.No) == false) continue;

                    //找到关于系统的控制权限集合.
                    PowerCenters mypcs = pcs.GetEntitiesByKey(PowerCenterAttr.CtrlPKVal, menu.No) as PowerCenters;
                    //如果没有权限控制的描述，就默认有权限.
                    if (mypcs == null)
                    {
                        menusCopy.AddEntity(menu);
                        continue;
                    }

                    //控制遍历权限.
                    foreach (PowerCenter pc in mypcs)
                    {
                        if (pc.CtrlModel.Equals("Anyone") == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }
                        if (pc.CtrlModel.Equals("Adminer") == true && BP.Web.WebUser.No.Equals("admin") == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        if (pc.CtrlModel.Equals("AdminerAndAdmin2") == true && BP.Web.WebUser.IsAdmin == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        ids = "," + pc.IDs + ",";
                        if (pc.CtrlModel.Equals("Emps") == true && ids.Contains("," + BP.Web.WebUser.No + ",") == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        //是否包含部门？
                        if (pc.CtrlModel.Equals("Depts") == true && this.IsHaveIt(pc.IDs, mydepts) == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        //是否包含角色？
                        if (pc.CtrlModel.Equals("Stations") == true && this.IsHaveIt(pc.IDs, mystas) == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        //按照SQL语句
                        if (pc.CtrlModel.Equals("SQL") == true)
                        {
                            string sql = pc.IDs;
                            if (DataType.IsNullOrEmpty(sql) == true)
                            {
                                menusCopy.AddEntity(menu);
                                break;
                            }
                            sql = BP.WF.Glo.DealExp(sql, null);
                            if (DBAccess.RunSQLReturnValInt(sql, 0) > 0)
                            {
                                menusCopy.AddEntity(menu);
                                break;
                            }
                        }
                    }
                }
            }
            #endregion 3.求出菜单权限.

            #region 4.组装数据.
            DataSet ds = new DataSet();
            DataTable dtSystem = systemsCopy.ToDataTableField("System");
            dtSystem.Columns.Add("IsOpen");

            //给第1个系统，第1个模块设置打开状态.
            DataTable dtModule = modulesCopy.ToDataTableField("Module");
            dtModule.Columns.Add("IsOpen");
            if (dtSystem.Rows.Count > 0)
            {
                dtSystem.Rows[0]["IsOpen"] = "true";
                string systemNo = dtSystem.Rows[0]["No"].ToString();
                foreach (DataRow dr in dtModule.Rows)
                {
                    if (dr["SystemNo"].ToString().Equals(systemNo) == false)
                        continue;

                    dr["IsOpen"] = "true";
                    break;
                }
            }
            ds.Tables.Add(dtSystem);
            ds.Tables.Add(dtModule);
            DataTable dtMenu = menusCopy.ToDataTableField("Menu");
            dtMenu.Columns["UrlExt"].ColumnName = "Url";
            ds.Tables.Add(dtMenu);

            string pkval = BP.Web.WebUser.No + "_Menus";
            string json = BP.Tools.Json.ToJson(ds);
            DBAccess.SaveBigTextToDB(json, "Sys_UserRegedit", "MyPK", pkval, "BigDocs");
            try
            {
                DBAccess.RunSQL("UPDATE Sys_UserRegedit SET FK_Emp='" + BP.Web.WebUser.No + "', CfgKey='Menus',OrgNo='" + BP.Web.WebUser.OrgNo + "' WHERE MyPK='" + pkval + "'");
            }
            catch
            {
                UserRegedit ur = new UserRegedit();
                ur.CheckPhysicsTable();
            }
            #endregion 4.组装数据.

            return json;
        }
        public string Default_Init_SaaS_Menums()
        {
            #region 0.权限准备.
            //系统.
            MySystems systems = new MySystems();
            systems.Retrieve("OrgNo",WebUser.OrgNo,"Idx");

            //模块.
            Modules modules = new Modules();
            modules.Retrieve("OrgNo", WebUser.OrgNo, "Idx");

            //菜单.
            Menus menus = new Menus();
            menus.Retrieve("OrgNo", WebUser.OrgNo, "Idx");

            //定义容器
            MySystems systemsCopy = new MySystems();
            Modules modulesCopy = new Modules();
            Menus menusCopy = new Menus();

            //权限中心.
            BP.CCFast.CCMenu.PowerCenters pcs = new BP.CCFast.CCMenu.PowerCenters();
            QueryObject qo = new QueryObject(pcs);
            qo.AddWhere("OrgNo", WebUser.OrgNo);
            qo.addAnd();
            qo.AddWhereIn("CtrlObj", "('System','Module','Menus')");
            qo.addOrderBy("Idx");
            qo.DoQuery();

            //求出来当前登录人员的身份信息.
            string mydepts = "" + WebUser.DeptNo + ","; //我的部门.
            string mystas = ""; //我的角色.
            DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.UserID + "'");
            foreach (DataRow dr in mydeptsDT.Rows)
            {
                mydepts += dr[0].ToString() + ",";
                mystas += dr[1].ToString() + ",";
            }
            #endregion 0.权限准备.

            #region 1.处理系统权限.
            //首先解决系统的权限.
            string ids = ""; //求本组织的数据.
            foreach (MySystem item in systems)
            {
                //如果被禁用了.
                if (item.ItIsEnable == false) continue;

                //找到关于系统的控制权限集合.
                PowerCenters mypcs = pcs.GetEntitiesByKey(PowerCenterAttr.CtrlPKVal, item.No) as PowerCenters;
                //如果没有权限控制的描述，就默认有权限.
                if (mypcs == null)
                {
                    systemsCopy.AddEntity(item);
                    continue;
                }

                //控制遍历权限.
                foreach (PowerCenter pc in mypcs)
                {
                    if (pc.CtrlModel.Equals("Anyone") == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    if (pc.CtrlModel.Equals("Adminer") == true && BP.Web.WebUser.No.Equals("admin") == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }

                    if (pc.CtrlModel.Equals("AdminerAndAdmin2") == true && BP.Web.WebUser.IsAdmin == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    ids = "," + pc.IDs + ",";
                    if (pc.CtrlModel.Equals("Emps") == true && ids.Contains("," + BP.Web.WebUser.No + ",") == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    //是否包含部门？
                    if (pc.CtrlModel.Equals("Depts") == true && BP.DA.DataType.IsHaveIt(pc.IDs, mydepts) == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    //是否包含角色？
                    if (pc.CtrlModel.Equals("Stations") == true && BP.DA.DataType.IsHaveIt(pc.IDs, mystas) == true)
                    {
                        systemsCopy.AddEntity(item);
                        break;
                    }
                    //SQL？
                    if (pc.CtrlModel.Equals("SQL") == true)
                    {
                        string sql = BP.WF.Glo.DealExp(pc.IDs, null, "");
                        if (DBAccess.RunSQLReturnValFloat(sql) > 0)
                        {
                            systemsCopy.AddEntity(item);
                        }
                        break;
                    }
                }
            }
            #endregion 1.处理系统权限.

            #region 2.根据系统求出模块权限.
            foreach (MySystem item in systemsCopy)
            {
                foreach (Module module in modules)
                {
                    //如果被禁用了.
                    if (module.ItIsEnable == false) continue;
                    if (module.SystemNo.Equals(item.No) == false) continue;

                    //找到关于系统的控制权限集合.
                    PowerCenters mypcs = pcs.GetEntitiesByKey(PowerCenterAttr.CtrlPKVal, module.No) as PowerCenters;
                    //如果没有权限控制的描述，就默认有权限.
                    if (mypcs == null)
                    {
                        modulesCopy.AddEntity(module);
                        continue;
                    }

                    //控制遍历权限.
                    foreach (PowerCenter pc in mypcs)
                    {
                        if (pc.CtrlPKVal.Equals(module.No) == false)
                            continue;

                        if (pc.CtrlModel.Equals("Anyone") == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }
                        if (pc.CtrlModel.Equals("Adminer") == true && BP.Web.WebUser.No.Equals("admin") == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        if (pc.CtrlModel.Equals("AdminerAndAdmin2") == true && BP.Web.WebUser.IsAdmin == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        ids = "," + pc.IDs + ",";
                        if (pc.CtrlModel.Equals("Emps") == true && ids.Contains("," + BP.Web.WebUser.No + ",") == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        //是否包含部门？
                        if (pc.CtrlModel.Equals("Depts") == true && this.IsHaveIt(pc.IDs, mydepts) == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }

                        //是否包含角色？
                        if (pc.CtrlModel.Equals("Stations") == true && this.IsHaveIt(pc.IDs, mystas) == true)
                        {
                            modulesCopy.AddEntity(module);
                            break;
                        }
                        if (pc.CtrlModel.Equals("SQL") == true)
                        {
                            string sql = BP.WF.Glo.DealExp(pc.IDs, null, "");
                            if (DBAccess.RunSQLReturnValFloat(sql) > 0)
                            {
                                modulesCopy.AddEntity(module);
                            }
                            break;
                        }
                    }
                }
            }
            #endregion  2. 根据系统求出模块权限.

            #region 3.求出菜单权限.
            foreach (Module item in modulesCopy)
            {
                foreach (Menu menu in menus)
                {
                    //如果被禁用了.
                    if (menu.ItIsEnable == false) continue;
                    if (menu.ModuleNo.Equals(item.No) == false) continue;

                    //找到关于系统的控制权限集合.
                    PowerCenters mypcs = pcs.GetEntitiesByKey(PowerCenterAttr.CtrlPKVal, menu.No) as PowerCenters;
                    //如果没有权限控制的描述，就默认有权限.
                    if (mypcs == null)
                    {
                        menusCopy.AddEntity(menu);
                        continue;
                    }

                    //控制遍历权限.
                    foreach (PowerCenter pc in mypcs)
                    {
                        if (pc.CtrlModel.Equals("Anyone") == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }
                        if (pc.CtrlModel.Equals("Adminer") == true && BP.Web.WebUser.No.Equals("admin") == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        if (pc.CtrlModel.Equals("AdminerAndAdmin2") == true && BP.Web.WebUser.IsAdmin == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        ids = "," + pc.IDs + ",";
                        if (pc.CtrlModel.Equals("Emps") == true && ids.Contains("," + BP.Web.WebUser.No + ",") == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        //是否包含部门？
                        if (pc.CtrlModel.Equals("Depts") == true && this.IsHaveIt(pc.IDs, mydepts) == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        //是否包含角色？
                        if (pc.CtrlModel.Equals("Stations") == true && this.IsHaveIt(pc.IDs, mystas) == true)
                        {
                            menusCopy.AddEntity(menu);
                            break;
                        }

                        //按照SQL语句
                        if (pc.CtrlModel.Equals("SQL") == true)
                        {
                            string sql = pc.IDs;
                            if (DataType.IsNullOrEmpty(sql) == true)
                            {
                                menusCopy.AddEntity(menu);
                                break;
                            }
                            sql = BP.WF.Glo.DealExp(sql, null);
                            if (DBAccess.RunSQLReturnValInt(sql, 0) > 0)
                            {
                                menusCopy.AddEntity(menu);
                                break;
                            }
                        }
                    }
                }
            }
            #endregion 3.求出菜单权限.

            #region 4.组装数据.
            DataSet ds = new DataSet();
            DataTable dtSystem = systemsCopy.ToDataTableField("System");
            dtSystem.Columns.Add("IsOpen");

            //给第1个系统，第1个模块设置打开状态.
            DataTable dtModule = modulesCopy.ToDataTableField("Module");
            dtModule.Columns.Add("IsOpen");
            if (dtSystem.Rows.Count > 0)
            {
                dtSystem.Rows[0]["IsOpen"] = "true";
                string systemNo = dtSystem.Rows[0]["No"].ToString();
                foreach (DataRow dr in dtModule.Rows)
                {
                    if (dr["SystemNo"].ToString().Equals(systemNo) == false)
                        continue;

                    dr["IsOpen"] = "true";
                    break;
                }
            }
            ds.Tables.Add(dtSystem);
            ds.Tables.Add(dtModule);
            DataTable dtMenu = menusCopy.ToDataTableField("Menu");
            dtMenu.Columns["UrlExt"].ColumnName = "Url";
            ds.Tables.Add(dtMenu);

            string pkval = BP.Web.WebUser.No +WebUser.OrgNo+ "_Menus";
            string json = BP.Tools.Json.ToJson(ds);
            DBAccess.SaveBigTextToDB(json, "Sys_UserRegedit", "MyPK", pkval, "BigDocs");
            try
            {
                DBAccess.RunSQL("UPDATE Sys_UserRegedit SET FK_Emp='" + BP.Web.WebUser.No + "', CfgKey='Menus',OrgNo='" + BP.Web.WebUser.OrgNo + "' WHERE MyPK='" + pkval + "'");
            }
            catch
            {
                UserRegedit ur = new UserRegedit();
                ur.CheckPhysicsTable();
            }
            #endregion 4.组装数据.
            return json;
        }
        public string Default_Init_Admin_Menums(string isVue3)
        {
            DataSet myds = new DataSet();

            #region 构造数据容器.
            //系统表.
            MySystems systems = new MySystems();
            systems.RetrieveAll();
            DataTable dtSys = systems.ToDataTableField("System");
            dtSys.Columns.Add("IsOpen");

            //模块.
            Modules modules = new Modules();
            modules.RetrieveAll();
            DataTable dtModule = modules.ToDataTableField("Module");
            dtModule.Columns.Add("IsOpen");

            //菜单.
            Menus menus = new Menus();
            menus.RetrieveAll();
            DataTable dtMenu = menus.ToDataTableField("Menu");
            dtMenu.Columns["UrlExt"].ColumnName = "Url";
            #endregion 构造数据容器.

            #region 把数据加入里面去.
            myds.Tables.Add(dtSys);
            myds.Tables.Add(dtModule);
            myds.Tables.Add(dtMenu);
            #endregion 把数据加入里面去.

            #region 如果是admin.
            if (BP.Web.WebUser.IsAdmin == true && this.ItIsMobile == false && SystemConfig.CCBPMRunModel != CCBPMRunModel.SAAS)
            {
                if (isVue3 == null)
                {
                    #region 增加默认的系统.
                    DataRow dr = dtSys.NewRow();
                    dr["No"] = "Flows";
                    dr["Name"] = "流程设计";
                    dr["Icon"] = "";
                    dtSys.Rows.Add(dr);

                    dr = dtSys.NewRow();
                    dr["No"] = "Frms";
                    dr["Name"] = "表单设计";
                    dr["Icon"] = "";
                    dtSys.Rows.Add(dr);

                    dr = dtSys.NewRow();
                    dr["No"] = "System";
                    dr["Name"] = "系统管理";
                    dr["Icon"] = "";
                    dtSys.Rows.Add(dr);
                    #endregion 增加默认的系统.
                }

                string sqlWhere = "";
                if (BP.Difference.SystemConfig.CCBPMRunModel != CCBPMRunModel.Single)
                    sqlWhere = " AND OrgNo='" + BP.Web.WebUser.OrgNo + "'";

                DataSet dsAdminMenus = new DataSet();
                AdminMenus mymenus = new AdminMenus();
                dsAdminMenus.ReadXml(mymenus.File);

                //增加模块.
                DataTable dtGroup = dsAdminMenus.Tables["Group"];
                foreach (DataRow dtRow in dtGroup.Rows)
                {
                    DataRow drModel = dtModule.NewRow();
                    drModel["No"] = dtRow["No"];
                    drModel["Name"] = dtRow["Name"];
                    drModel["SystemNo"] = "System";
                    drModel["Icon"] = dtRow["Icon"];
                    dtModule.Rows.Add(drModel);
                }

                //增加菜单.
                DataTable dtItem = dsAdminMenus.Tables["Item"];
                foreach (DataRow dtRow in dtItem.Rows)
                {
                    DataRow drMenu = dtMenu.NewRow();
                    drMenu["No"] = dtRow["No"];
                    drMenu["Name"] = dtRow["Name"];
                    drMenu["ModuleNo"] = dtRow["GroupNo"];
                    drMenu["Url"] = dtRow["Url"];
                    drMenu["Icon"] = dtRow["Icon"];
                    drMenu["SystemNo"] = "System";
                    dtMenu.Rows.Add(drMenu);
                }
            }
            #endregion 如果是admin.

            //myds.WriteXml("c:/11.xml");
            #region 让第一个系统的第1个模块的默认打开的.
            if (myds.Tables["System"].Rows.Count != 0)
            {
                //让第一个打开.
                myds.Tables["System"].Rows[0]["IsOpen"] = "true";
                string systemNo = myds.Tables["System"].Rows[0]["No"].ToString();
                foreach (DataRow dr in myds.Tables["Module"].Rows)
                {
                    if (dr["SystemNo"].ToString().Equals(systemNo) == false)
                        continue;

                    dr["IsOpen"] = "true";
                    break;
                }
            }
            #endregion 让第一个系统的第1个模块的第一个菜单打开.

            return BP.Tools.Json.ToJson(myds);
        }
        #endregion   加载菜单.


        /// <summary>
        /// 生成页面
        /// </summary>
        /// <returns></returns>
        public string LoginGenerQRCodeMobile_Init()
        {
            string url = BP.Difference.SystemConfig.HostURL + "/FastMobilePortal/Login.htm";
            return url;
        }

        #region 按照流程类别批量导出流程模板
        /// <summary>
        /// 批量导出流程模板
        /// </summary>
        /// <returns></returns>
        public string Flow_BatchExpFlowTemplate()
        {
            string flowSort = this.GetRequestVal("FK_Sort");
            string flowSortName = this.GetRequestVal("FlowSortName");
            if (DataType.IsNullOrEmpty("flowSort") == true)
                return "err@流程的类别不能为空";
            //根据流程类别获取改类别下的所有流程
            Flows flows = new Flows(flowSort);
            //在临时文件中指定一个目录
            string path = BP.Difference.SystemConfig.PathOfTemp + flowSortName + "/";
            if (System.IO.Directory.Exists(path) == true)
                System.IO.Directory.Delete(path, true);
            else
                System.IO.Directory.CreateDirectory(path);
            foreach (Flow flow in flows)
            {
                flow.DoExpFlowXmlTemplete(path);
            }
            //生成压缩包文件
            string zipFile = BP.Difference.SystemConfig.PathOfTemp + flowSortName + ".zip";
            try
            {
                while (System.IO.File.Exists(zipFile) == true)
                {
                    System.IO.File.Delete(zipFile);
                }
                //执行压缩.
                FastZip fz = new FastZip();
                fz.CreateZip(zipFile, path, true, "");
                //删除临时文件夹
                System.IO.Directory.Delete(path, true);
            }
            catch (Exception ex)
            {
                return "err@执行压缩出现错误:" + ex.Message + ",路径tempPath:" + path + ",zipFile=" + zipFile;
            }
            return "url@DataUser/Temp/" + flowSortName + ".zip";
        }
        #endregion 按照流程类别批量导出流程模板

        #region 按照表单类别批量导出表单模板
        /// <summary>
        /// 批量导出表单模板
        /// </summary>
        /// <returns></returns>
        public string Form_BatchExpFrmTemplate()
        {
            string frmTree = this.GetRequestVal("FK_FrmTree");
            string frmTreeName = this.GetRequestVal("FrmTreeName");
            if (DataType.IsNullOrEmpty("frmTree") == true)
                return "err@表单的类别不能为空";
            //根据流程类别获取改类别下的所有流程
            MapDatas mds = new MapDatas();
            mds.Retrieve(MapDataAttr.FK_FormTree, frmTree, MapDataAttr.Idx);
            //在临时文件中指定一个目录
            string path = BP.Difference.SystemConfig.PathOfTemp + frmTreeName + "/";
            if (System.IO.Directory.Exists(path) == true)
                System.IO.Directory.Delete(path, true);
            else
                System.IO.Directory.CreateDirectory(path);
            foreach (MapData md in mds)
            {
                DataSet ds = BP.Sys.CCFormAPI.GenerHisDataSet_AllEleInfo(md.No);

                string file = path + md.Name + ".xml";
                ds.WriteXml(file);
            }
            //生成压缩包文件
            string zipFile = BP.Difference.SystemConfig.PathOfTemp + frmTreeName + ".zip";
            try
            {
                while (System.IO.File.Exists(zipFile) == true)
                {
                    System.IO.File.Delete(zipFile);
                }
                //执行压缩.
                FastZip fz = new FastZip();
                fz.CreateZip(zipFile, path, true, "");
                //删除临时文件夹
                System.IO.Directory.Delete(path, true);
            }
            catch (Exception ex)
            {
                return "err@执行压缩出现错误:" + ex.Message + ",路径tempPath:" + path + ",zipFile=" + zipFile;
            }
            return "url@DataUser/Temp/" + frmTreeName + ".zip";
        }
        #endregion 按照表单类别批量导出表单模板
    }
}
