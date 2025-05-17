using System;

namespace BP.Sys
{
    /// <summary>
    /// 表单事件类的常量
    /// </summary>
    public class EventListFrm
    { 
        /// <summary>
        /// 表单载入前
        /// </summary>
        public const string FrmLoadBefore = "FrmLoadBefore";
        /// <summary>
        /// 表单载入后
        /// </summary>
        public const string FrmLoadAfter = "FrmLoadAfter";
        /// <summary>
        /// 表单保存前
        /// </summary>
        public const string SaveBefore = "SaveBefore";
        /// <summary>
        /// 表单保存后
        /// </summary>
        public const string SaveAfter = "SaveAfter";
        /// <summary>
        /// 创建OID
        /// </summary>
        public const string CreateOID = "CreateOID";
        /// <summary>
        /// 附件上传前
        /// </summary>
        public const string AthUploadeBefore = "AthUploadeBefore";
        /// <summary>
        /// 上传后.
        /// </summary>
        public const string AthUploadeAfter = "AthUploadeAfter";
        /// <summary>
        /// 从表保存前
        /// </summary>
        public const string DtlRowSaveBefore = "DtlRowSaveBefore";
        /// <summary>
        /// 从表保存后
        /// </summary>
        public const string DtlRowSaveAfter = "DtlRowSaveAfter";
        /// <summary>
        /// 从表保存前
        /// </summary>
        public const string DtlRowDelBefore = "DtlRowDelBefore";
        /// <summary>
        /// 从表保存后
        /// </summary>
        public const string DtlRowDelAfter = "DtlRowDelAfter";
        /// <summary>
        /// 审核结束
        /// </summary>
        public const string CheckOver = "CheckOver";
        /// <summary>
        /// 单据审核回滚
        /// </summary>
        public const string Reback = "Reback";
        /// <summary>
        /// 启动审核
        /// </summary>
        public const string CheckStart = "CheckStart";
        /// <summary>
        /// 撤销发送.
        /// </summary>
        public const string UnSend = "UnSend";
        /// <summary>
        /// 归档前
        /// </summary>
        public const string OverBefore = "OverBefore";
        /// <summary>
        /// 归档后
        /// </summary>
        public const string OverAfter = "OverAfter";
    }
}
