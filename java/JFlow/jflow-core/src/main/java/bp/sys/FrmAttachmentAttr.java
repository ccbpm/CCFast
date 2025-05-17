package bp.sys;
import bp.en.*;

/** 
 附件
*/
public class FrmAttachmentAttr extends EntityMyPKAttr
{
	/** 
	 Name
	*/
	public static final String Name = "Name";
	/** 
	 主表
	*/
	public static final String FK_MapData = "FK_MapData";
	/** 
	 运行模式
	*/
	public static final String AthRunModel = "AthRunModel";
	/** 
	 节点ID
	*/
	public static final String FK_Node = "FK_Node";
	/** 
	 高度
	*/
	public static final String H = "H";
	/** 
	 要求上传的格式
	*/
	public static final String Exts = "Exts";
	/** 
	 附件编号
	*/
	public static final String NoOfObj = "NoOfObj";
	/** 
	 是否可以上传
	*/
	public static final String IsUpload = "IsUpload";
	/** 
	 是否是合流汇总
	*/
	public static final String IsHeLiuHuiZong = "IsHeLiuHuiZong";
	/** 
	 是否汇总到合流节点上去？
	*/
	public static final String IsToHeLiuHZ = "IsToHeLiuHZ";
	/** 
	 是否增加
	*/
	public static final String IsNote = "IsNote";
	/** 
	 是否启用扩展列
	*/
	public static final String IsExpCol = "IsExpCol";
	/** 
	 是否显示标题列
	*/
	public static final String IsShowTitle = "IsShowTitle";
	/** 
	 是否可以下载
	*/
	public static final String IsDownload = "IsDownload";
	/** 
	 是否可以排序
	*/
	public static final String IsOrder11 = "IsOrder";
	/** 
	 数据存储方式
	*/
	public static final String AthSaveWay = "AthSaveWay";
	/** 
	 单附件模板使用规则
	*/
	public static final String AthSingleRole = "AthSingleRole";
	/** 
	 单附件编辑模式
	*/
	public static final String AthEditModel = "AthEditModel";
	/** 
	 是否排序？
	*/
	public static final String IsIdx = "IsIdx";
	/** 
	 是否要转换成html，方便在线浏览.
	*/
	public static final String IsTurn2Html = "IsTurn2Html";
	/** 
	 类别
	*/
	public static final String Sort = "Sort";
	/** 
	 上传类型
	*/
	public static final String UploadType = "UploadType";
	/** 
	 GroupID
	*/
	public static final String GroupID = "GroupID";
	/** RowIdx
	*/
	public static final String RowIdx = "RowIdx";
	/** 
	 
	 自动控制大小
	*/
	public static final String IsAutoSize = "IsAutoSize";
	/** 
	 GUID
	*/
	public static final String GUID = "GUID";
	/** 
	 数据控制方式(对父子流程有效果)
	*/
	public static final String CtrlWay = "CtrlWay";
	/** 
	 上传方式(对父子流程有效果)
	*/
	public static final String AthUploadWay = "AthUploadWay";
	/** 
	 文件展现方式
	*/
	public static final String FileShowWay = "FileShowWay";
	/** 
	 上传方式
	 0，批量上传。
	 1，单个上传。
	*/
	public static final String UploadCtrl = "UploadCtrl";
	/** 
	 上传校验
	 0=不校验.
	 1=不能为空.
	 2=每个类别下不能为空.
	*/
	public static final String UploadFileNumCheck = "UploadFileNumCheck";
	/** 
	 上传最小数量
	*/
	public static final String NumOfUpload = "NumOfUpload";
	/** 
	 上传最大数量
	*/
	public static final String TopNumOfUpload = "TopNumOfUpload";
	/** 
	 附件最大限制
	*/
	public static final String FileMaxSize = "FileMaxSize";
	/** 
	 是否可见？
	*/
	public static final String IsVisable = "IsVisable";
	/** 
	 附件类型 0 普通附件 1 图片附件
	*/
	public static final String FileType = "FileType";
	/** 
	 移动端图片附件上传的方式
	*/
	public static final String PicUploadType = "PicUploadType";
	/** 
	 是否启用模板？
	*/
	public static final String IsEnableTemplate = "IsEnableTemplate";
	/** 
	 附件删除方式
	*/
	public static final String DeleteWay = "DeleteWay";


		///#region 数据引用.
	/** 
	 数据引用
	*/
	public static final String DataRefNoOfObj = "DataRefNoOfObj";
	/***
	 * 在线编辑方式
	 */
	public static final String AthEditType = "AthEditType";
	/**
	 阅读规则
	*/
	public static final String ReadRole = "ReadRole";

		///#endregion 数据引用.


		///#region 快捷键.
	/** 
	 是否启用快捷键
	*/
	public static final String FastKeyIsEnable = "FastKeyIsEnable";
	/** 
	 快捷键生成规则
	*/
	public static final String FastKeyGenerRole = "FastKeyGenerRole";

		///#endregion
}
