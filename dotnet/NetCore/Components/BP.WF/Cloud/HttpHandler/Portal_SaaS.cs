using System;
using System.Collections.Generic;
using System.Data;
using System.Text;
using System.Web;
using BP.DA;
using BP.Difference;
using BP.Sys;
using BP.Web;
using BP.Port;
using BP.En;
using BP.WF;
using BP.WF.Template;
using System.Collections;
using BP.WF.Port;

namespace BP.Cloud.HttpHandler
{
    /// <summary>
    /// 页面功能实体
    /// </summary>
    public class Portal_SaaS : BP.WF.HttpHandler.DirectoryPageBase
    {
        /// <summary>
        /// 构造函数
        /// </summary>
        public Portal_SaaS()
        {
        }
     
        /// <summary>
        /// 获取组织
        /// </summary>
        /// <returns></returns>
        public string SelectOneOrg_Init() 
        {
            BP.Cloud.Orgs orgs = new BP.Cloud.Orgs();
            orgs.RetrieveAll();
            DataTable dt = orgs.ToDataTableField("Orgs");
            return BP.Tools.Json.ToJson(dt);
        }

        /// <summary>
        /// JFlow要用到，根据no获取对应的组织数据
        /// </summary>
        /// <returns></returns>
        public string GetOrgByNo()
        {
            String no = this.GetRequestVal("OrgNo");
            BP.Cloud.Org org = new Org();
            org.No = no;
            if (org.RetrieveFromDBSources() == 0)
            {
                return "err@组织不存在.";
            }
            return org.ToJson();
        }

        public string Login_Submit()
        {
            try
            {
                string orgNo = this.OrgNo;
                if(DataType.IsNullOrEmpty(orgNo) == true)
                    return "err@没有获取到OrgNo，请检查参数是否正确.";
                string userNo = this.GetRequestVal("TB_No");
                string pass = this.GetRequestVal("TB_PW");
                if (pass == null)
                    pass = this.GetRequestVal("TB_Pass");
                pass = pass.Trim();
                BP.Port.Emp emp = new BP.Port.Emp();
                emp.No = this.OrgNo + "_" + userNo;
                if (emp.RetrieveFromDBSources() == 0)
                    return "err@用户名["+userNo+"]不存在.";

                if (emp.CheckPass(pass) == false)
                    return "err@密码错误.";

                WFEmp wfemp=new WFEmp();
                wfemp.No = emp.OrgNo + "_" + userNo;
                if (emp.RetrieveFromDBSources() == 0)
                {
                    wfemp.Name = emp.Name;
                    wfemp.DeptNo = emp.DeptNo;
                    wfemp.OrgNo = emp.OrgNo;
                    wfemp.Insert();
                }

                //BP.Cloud.Emp emp2 = new BP.Cloud.Emp(emp.No);
                BP.WF.Dev2Interface.Port_Login(userNo,this.OrgNo);
                string token = BP.WF.Dev2Interface.Port_GenerToken();
                WebUser.Token = token;

                // 是否是组织管理员
                /*BP.WF.Port.Admin2Group.OrgAdminer orgAdminer = new BP.WF.Port.Admin2Group.OrgAdminer();
                orgAdminer.EmpNo = emp.No;*/

                string sql = "SELECT FK_Emp FROM Port_OrgAdminer WHERE FK_Emp='" + emp.No + "' AND OrgNo='" + emp.OrgNo + "'";
                if (DBAccess.RunSQLReturnTable(sql).Rows.Count != 0)
                {
                    #region 初始化 - 流程树.
                    BP.WF.Template.FlowSort fs = new BP.WF.Template.FlowSort();
                    fs.No = emp.DeptNo; //公司编号
                    if (fs.RetrieveFromDBSources() == 0)
                    {
                        fs.Name = emp.Name;
                        fs.OrgNo = emp.OrgNo;
                        fs.ParentNo = "100";
                        fs.DirectInsert();

                        fs.No = DBAccess.GenerGUID();
                        fs.ParentNo = emp.DeptNo; //帐号信息.
                        fs.Name = "日常办公";
                        fs.OrgNo = emp.OrgNo;
                        fs.DirectInsert();

                        fs.No = DBAccess.GenerGUID();
                        fs.ParentNo = emp.DeptNo; //帐号信息.
                        fs.Name = "财务类";
                        fs.OrgNo = emp.OrgNo;
                        fs.DirectInsert();

                        fs.No = DBAccess.GenerGUID();
                        fs.ParentNo = emp.DeptNo;
                        fs.Name = "人力资源类";
                        fs.OrgNo = emp.OrgNo;
                        fs.DirectInsert();
                        #endregion 开始流程树.

                    }

                    #region 表单树.
                    BP.WF.Template.SysFormTree ft = new BP.WF.Template.SysFormTree();
                    ft.No = emp.DeptNo; //公司编号
                    if (ft.RetrieveFromDBSources() == 0)
                    {
                        ft.Name = "表单树";
                        ft.OrgNo = emp.OrgNo;
                        ft.ParentNo = "100"; //这里固定死了必须是100.
                        ft.DirectInsert();

                        ft.No = DBAccess.GenerGUID();
                        ft.ParentNo = emp.DeptNo;
                        ft.Name = "日常办公";
                        ft.OrgNo = emp.OrgNo;
                        ft.DirectInsert();

                        ft.No = DBAccess.GenerGUID();
                        ft.ParentNo = emp.DeptNo;
                        ft.Name = "财务类";
                        ft.OrgNo = emp.OrgNo;
                        ft.DirectInsert();

                        ft.No = DBAccess.GenerGUID();
                        ft.ParentNo = emp.DeptNo;
                        ft.Name = "人力资源类";
                        ft.OrgNo = emp.OrgNo;
                        ft.DirectInsert();
                        #endregion 表单树.
                    }
                }

                return WebUser.ToJson();
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }
        public string Login_SubmitSaaSOption()
        {
            try
            {
                string userNo = this.GetRequestVal("TB_No");
                if (DataType.IsNullOrEmpty(userNo)==true)
                    return "err@账号不能为空.";

                if (userNo.ToLower().Equals("admin")==true)
                    return "err@请登录admin后台.";

                string pass = this.GetRequestVal("TB_PW");
                if (pass == null)
                    pass = this.GetRequestVal("TB_Pass");
                pass = pass.Trim();
                BP.Cloud.Emps emps = new BP.Cloud.Emps();
                if (emps.Retrieve(EmpAttr.UserID, userNo) == 0)
                    return "err@用户名[" + userNo + "]不存在.";

                BP.Cloud.Emp myemp = emps[0] as BP.Cloud.Emp;

                //检查密码
                BP.Port.Emp emp1 = new BP.Port.Emp();
                emp1.No = myemp.No;
                emp1.RetrieveFromDBSources();
                if (emp1.CheckPass(pass) == false)
                    return "err@密码错误.";

                //BP.Cloud.Emp emp2 = new BP.Cloud.Emp(emp.No);
                BP.WF.Dev2Interface.Port_Login(userNo, myemp.OrgNo);
                string token = BP.WF.Dev2Interface.Port_GenerToken();
                return WebUser.ToJson();
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }

        public string CheckEncryptEnable()
        {
            if (SystemConfig.isEnablePasswordEncryption == true)
                return "1";
            return "0";
        }
        #region 执行父类的重写方法.
        /// <summary>
        /// 默认执行的方法
        /// </summary>
        /// <returns></returns>
        protected override string DoDefaultMethod()
        {
            switch (this.DoType)
            {
                case "DtlFieldUp": //字段上移
                    return "执行成功.";
                default:
                    break;
            }

            //找不不到标记就抛出异常.
            throw new Exception("@标记[" + this.DoType + "]，没有找到. @RowURL:" + HttpContextHelper.RequestRawUrl);
        }
        #endregion 执行父类的重写方法.

        #region xxx 界面 .
        #endregion xxx 界面方法.

    }
}
