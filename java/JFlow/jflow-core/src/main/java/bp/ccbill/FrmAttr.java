package bp.ccbill;

import bp.*;

/** 
 实体表单 - Attr
*/
public class FrmAttr extends bp.en.EntityOIDNameAttr
{

		///#region 基本属性
	/** 
	 工作模式
	*/
	public static final String FrmDictWorkModel = "FrmDictWorkModel";
	/** 
	 实体类型
	*/
	public static final String EntityType = "EntityType";
	/** 
	 展示模式
	*/
	public static final String EntityShowModel = "EntityShowModel";
	/** 
	 单据编号生成规则
	*/
	public static final String BillNoFormat = "BillNoFormat";
	/** 
	 单据编号生成规则
	*/
	public static final String TitleRole = "TitleRole";
	/** 
	 排序字段
	*/
	public static final String SortColumns = "SortColumns";
	/** 
	 字段颜色设置
	*/
	public static final String ColorSet = "ColorSet";
	/** 
	 按照指定字段的颜色显示表格行的颜色
	*/
	public static final String RowColorSet = "RowColorSet";
	/** 
	 字段求和求平均设置
	*/
	public static final String FieldSet = "FieldSet";
	/**
	 表格内容展示类型
	 */
	public static final String TableStyle = "TableStyle";
	/**
	 关联单据
	*/
	public static final String RefBill = "RefBill";

		///#endregion


		///#region 隐藏属性.
	/** 
	 要显示的列
	*/
	public static final String ShowCols = "ShowCols";

		///#endregion 隐藏属性


		///#region 按钮信息.
	/** 
	 按钮New标签
	*/
	public static final String BtnNewLable = "BtnNewLable";
	/** 
	 按钮New启用规则
	*/
	public static final String BtnNewModel = "BtnNewModel";
	/** 
	 按钮Save标签
	*/
	public static final String BtnSaveLable = "BtnSaveLable";
	/** 
	 按钮save启用规则
	*/
	public static final String BtnSaveEnable = "BtnSaveEnable";

	public static final String BtnSubmitLable = "BtnSubmitLable";
	public static final String BtnSubmitEnable = "BtnSubmitEnable";


	/** 
	 保存andclose
	*/
	public static final String BtnSaveAndCloseLable = "BtnSaveAndCloseLable";
	/** 
	 保存并关闭.
	*/
	public static final String BtnSaveAndCloseEnable = "BtnSaveAndCloseEnable";

	/** 
	 按钮del标签
	*/
	public static final String BtnDelLable = "BtnDelLable";
	/** 
	 数据版本
	*/
	public static final String BtnDataVer = "BtnDataVer";
	/** 
	 按钮del启用规则
	*/
	public static final String BtnDelEnable = "BtnDelEnable";
	/** 
	 按钮del标签
	*/
	public static final String BtnStartFlowLable = "BtnStartFlowLable";
	/** 
	 按钮del启用规则
	*/
	public static final String BtnStartFlowEnable = "BtnStartFlowEnable";
	/** 
	 查询
	*/
	public static final String BtnSearchLabel = "BtnSearchLabel";
	/** 
	 查询
	*/
	public static final String BtnSearchEnable = "BtnSearchEnable";
	/** 
	 分析
	*/
	public static final String BtnGroupLabel = "BtnGroupLabel";
	/** 
	 分析
	*/
	public static final String BtnGroupEnable = "BtnGroupEnable";

		///#endregion


		///#region 打印
	public static final String BtnPrintHtml = "BtnPrintHtml";
	public static final String BtnPrintHtmlEnable = "BtnPrintHtmlEnable";

	public static final String BtnPrintPDF = "BtnPrintPDF";
	public static final String BtnPrintPDFEnable = "BtnPrintPDFEnable";

	public static final String BtnPrintRTF = "BtnPrintRTF";
	public static final String BtnPrintRTFEnable = "BtnPrintRTFEnable";

	public static final String BtnPrintCCWord = "BtnPrintCCWord";
	public static final String BtnPrintCCWordEnable = "BtnPrintCCWordEnable";

		///#endregion


		///#region 按钮.
	/** 
	 导出zip文件
	*/
	public static final String BtnExpZip = "BtnExpZip";
	/** 
	 是否可以启用?
	*/
	public static final String BtnExpZipEnable = "BtnExpZipEnable";
	/** 
	 关联单据
	*/
	public static final String BtnRefBill = "BtnRefBill";
	/** 
	 关联单据是否可用
	*/
	public static final String RefBillRole = "RefBillRole";

		///#endregion 按钮.


		///#region 集合的操作.
	/** 
	 导入Excel
	*/
	public static final String BtnImpExcel = "BtnImpExcel";
	/** 
	 是否启用导入
	*/
	public static final String BtnImpExcelEnable = "BtnImpExcelEnable";
	/** 
	 导出Excel
	*/
	public static final String BtnExpExcel = "BtnExpExcel";
	/** 
	 导出excel
	*/
	public static final String BtnExpExcelEnable = "BtnExpExcelEnable";

		///#endregion 集合的操作.

	/** 
	 行打开模式
	*/
	public static final String RowOpenModel = "RowOpenModel";
	public static final String PopHeight = "PopHeight";
	public static final String PopWidth = "PopWidth";
	public static final String Tag0 = "Tag0";
	public static final String Tag1 = "Tag1";
	public static final String Tag2 = "Tag2";
	/** 
	 实体编辑模式
	*/
	public static final String EntityEditModel = "EntityEditModel";

	public static final String SearchDictOpenType = "SearchDictOpenType";

}
