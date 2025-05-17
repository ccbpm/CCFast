using System;
using System.Collections;
using System.Data;
using BP.DA;
using BP.Sys;
using BP.En;
using BP.WF.Data;
using BP.Web;

namespace BP.WF.Template
{
    /// <summary>
    /// 条件数据源
    /// </summary>
    public enum ConnDataFrom
    {
        /// <summary>
        /// 表单数据
        /// </summary>
        NodeForm = 0,
        /// <summary>
        /// 独立表单
        /// </summary>
        StandAloneFrm = 1,
        /// <summary>
        /// 角色数据
        /// </summary>
        Stas = 2,
        /// <summary>
        /// Depts
        /// </summary>
        Depts = 3,
        /// <summary>
        /// 按sql计算.
        /// </summary>
        SQL = 4,
        /// <summary>
        /// 按sql模版计算.
        /// </summary>
        SQLTemplate = 5,
        /// <summary>
        /// 按参数
        /// </summary>
        Paras = 6,
        /// <summary>
        /// 按Url.
        /// </summary>
        Url = 7,
        /// <summary>
        /// 按WebApi返回值
        /// </summary>
        WebApi = 8,
        /// <summary>
        /// 按照审核组件立场
        /// </summary>
        WorkCheck = 9,
        /// <summary>
        /// 按部门负责人
        /// </summary>
        CondLeaderOfDept = 10,
        /// <summary>
        /// 按人员计算.
        /// </summary>
        CondEmp = 11,
        /// <summary>
        /// 操作符
        /// </summary>
        CondOperator = 100
    }
    /// <summary>
    /// 条件类型
    /// </summary>
    public enum CondType
    {
        /// <summary>
        /// 节点完成条件
        /// </summary>
        Node = 0,
        /// <summary>
        /// 流程完成条件
        /// </summary>
        Flow = 1,
        /// <summary>
        /// 方向条件
        /// </summary>
        Dir = 2,
        /// <summary>
        /// 启动子流程
        /// </summary>
        SubFlow = 3,
        /// <summary>
        /// 抄送规则条件.
        /// </summary>
        CCRole = 4
    }
    /// <summary>
    /// 指定操作员方式
    /// </summary>
    public enum SpecOperWay
    {
        /// <summary>
        /// 当前的人员
        /// </summary>
        CurrOper = 0,
        /// <summary>
        /// 指定节点人员
        /// </summary>
        SpecNodeOper = 10,
        /// <summary>
        /// 指定表单人员
        /// </summary>
        SpecSheetField = 22,
        /// <summary>
        /// 指定人员编号
        /// </summary>
        SpenEmpNo = 33
    }
}
