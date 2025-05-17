package bp.sys;
/** 
 表单事件类的常量
*/
public class EventListFrm
{
	/** 
	 表单载入前
	*/
	public static final String FrmLoadBefore = "FrmLoadBefore";
	/** 
	 表单载入后
	*/
	public static final String FrmLoadAfter = "FrmLoadAfter";
	/** 
	 表单保存前
	*/
	public static final String SaveBefore = "SaveBefore";
	/** 
	 表单保存后
	*/
	public static final String SaveAfter = "SaveAfter";
	/// <summary>
	/// 单据审核回滚
	/// </summary>
	public static final String Reback = "Reback";
	/// <summary>
	/// 启动审核
	/// </summary>
	public static final String CheckStart = "CheckStart";
	/// <summary>
	/// 撤销发送.
	/// </summary>
	public static final String UnSend = "UnSend";
	/// <summary>
	/// 归档前
	/// </summary>
	public static final String OverBefore = "OverBefore";
	/// <summary>
	/// 归档后
	/// </summary>
	public static final String OverAfter = "OverAfter";
	/// <summary>
	/// 审核结束
	/// </summary>
	public static final String CheckOver = "CheckOver";
	/** 
	 创建OID
	*/
	public static final String CreateOID = "CreateOID";
	/** 
	 附件上传前
	*/
	public static final String AthUploadeBefore = "AthUploadeBefore";
	/** 
	 上传后.
	*/
	public static final String AthUploadeAfter = "AthUploadeAfter";
	/** 
	 从表保存前
	*/
	public static final String DtlRowSaveBefore = "DtlRowSaveBefore";
	/** 
	 从表保存后
	*/
	public static final String DtlRowSaveAfter = "DtlRowSaveAfter";
	/** 
	 从表保存前
	*/
	public static final String DtlRowDelBefore = "DtlRowDelBefore";
	/** 
	 从表保存后
	*/
	public static final String DtlRowDelAfter = "DtlRowDelAfter";
}
