package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.*;
import bp.sys.CCFormAPI;
import bp.wf.*;
import java.util.*;

/** 
 初始化函数
*/
public class WF_Admin_CCFormDesigner_DialogCtr extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_Admin_CCFormDesigner_DialogCtr()
	{
	}

	/** 
	 获取隐藏字段
	 
	 @return 
	*/
	public final String Hiddenfielddata() throws Exception {
		return CCFormAPI.DB_Hiddenfielddata(this.getFrmID());
	}
	/** 
	 
	 
	 @return 
	*/
	public final String PublicNoName_InitFieldVal()
	{
		String sql = "";
		Hashtable ht = new Hashtable();

		String ctrlType = this.GetRequestVal("CtrlType");
		int num = 1;
		Paras ps = new Paras();
		switch (ctrlType)
		{
			case "Dtl":
				ps.SQL = "SELECT COUNT(*) FROM Sys_MapDtl WHERE FK_MapData=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "FK_MapData";
				ps.Add("FK_MapData", this.getFrmID(), false);
				//sql = "SELECT COUNT(*) FROM Sys_MapDtl WHERE FK_MapData='" + this.getFrmID() + "'";
				num = DBAccess.RunSQLReturnValInt(ps) + 1;
				ht.put("No", this.getFrmID() + "Dtl" + num);
				ht.put("Name", "从表" + num);
				break;
			case "AthMulti":
				ps.SQL = "SELECT COUNT(*) FROM Sys_FrmAttachment WHERE FK_MapData=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "FK_MapData";
				ps.Add("FK_MapData", this.getFrmID(), false);
				//sql = "SELECT COUNT(*) FROM Sys_FrmAttachment WHERE FK_MapData='" + this.getFrmID() + "'";
				num = DBAccess.RunSQLReturnValInt(ps) + 1;
				ht.put("No", "AthMulti" + num);
				ht.put("Name", "多附件" + num);
				break;
			case "ImgAth":
				ps.SQL = "SELECT COUNT(*) FROM Sys_FrmImgAth WHERE FK_MapData=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "FK_MapData";
				ps.Add("FK_MapData", this.getFrmID(), false);
				//sql = "SELECT COUNT(*) FROM Sys_FrmImgAth WHERE FK_MapData='" + this.getFrmID() + "'";
				num = DBAccess.RunSQLReturnValInt(ps) + 1;
				ht.put("No", "ImgAth" + num);
				ht.put("Name", "图片附件" + num);
				break;
			case "AthSingle":
				ps.SQL = "SELECT COUNT(*) FROM Sys_FrmAttachment WHERE FK_MapData=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "FK_MapData";
				ps.Add("FK_MapData", this.getFrmID(), false);
				//sql = "SELECT COUNT(*) FROM Sys_FrmAttachment WHERE FK_MapData='" + this.getFrmID() + "'";
				num = DBAccess.RunSQLReturnValInt(ps) + 1;
				ht.put("No", "AthSingle" + num);
				ht.put("Name", "单附件" + num);
				break;
			case "AthImg":
				ps.SQL = "SELECT COUNT(*) FROM Sys_FrmImgAth WHERE FK_MapData=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "FK_MapData";
				ps.Add("FK_MapData", this.getFrmID(), false);
				//sql = "SELECT COUNT(*) FROM Sys_FrmImgAth WHERE FK_MapData='" + this.getFrmID() + "'";
				num = DBAccess.RunSQLReturnValInt(ps) + 1;
				ht.put("No", "AthImg" + num);
				ht.put("Name", "图片附件" + num);
				break;
			case "HandSiganture": //手写板.
				ps.SQL = "SELECT COUNT(*) FROM Sys_FrmEle WHERE FK_MapData=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "FK_MapData" + " AND EleType=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "EleType";
				ps.Add("FK_MapData", this.getFrmID(), false);
				ps.Add("EleType", ctrlType, false);
				//sql = "SELECT COUNT(*) FROM Sys_FrmEle WHERE FK_MapData='" + this.getFrmID() + "' AND EleType='"+ctrlType+"'";
				num = DBAccess.RunSQLReturnValInt(ps) + 1;
				ht.put("No", "HandSiganture" + num);
				ht.put("Name", "签字板" + num);
				break;
			case "iFrame": //框架
				ps.SQL = "SELECT COUNT(*) FROM Sys_FrmEle WHERE FK_MapData=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "FK_MapData" + " AND EleType=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "EleType";
				ps.Add("FK_MapData", this.getFrmID(), false);
				ps.Add("EleType", ctrlType, false);
				//sql = "SELECT COUNT(*) FROM Sys_FrmEle WHERE FK_MapData='" + this.getFrmID() + "' AND EleType='" + ctrlType + "'";
				num = DBAccess.RunSQLReturnValInt(ps) + 1;
				ht.put("No", "iFrame" + num);
				ht.put("Name", "框架" + num);
				break;
			case "Fieldset": //分组
				ps.SQL = "SELECT COUNT(*) FROM Sys_FrmEle WHERE FK_MapData=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "FK_MapData" + " AND EleType=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "EleType";
				ps.Add("FK_MapData", this.getFrmID(), false);
				ps.Add("EleType", ctrlType, false);
				//sql = "SELECT COUNT(*) FROM Sys_FrmEle WHERE FK_MapData='" + this.getFrmID() + "' AND EleType='" + ctrlType + "'";
				num = DBAccess.RunSQLReturnValInt(ps) + 1;
				ht.put("No", "Fieldset" + num);
				ht.put("Name", "分组" + num);
				break;
			default:
				ht.put("No", ctrlType + 1);
				ht.put("Name", ctrlType + 1);
				break;
		}

		return bp.tools.Json.ToJsonEntityModel(ht);
	}


		///#region 枚举界面.
	/** 
	 获得外键列表.
	 
	 @return 
	*/
	public final String FrmTable_GetSFTableList() throws Exception {
		//WF_Admin_FoolFormDesigner wf = new WF_Admin_FoolFormDesigner(this.context);
		SFTables ens = new SFTables();
		ens.RetrieveAll();
		return ens.ToJson("dt");
	}

		///#endregion 枚举界面.


		///#region 执行父类的重写方法.
	/** 
	 默认执行的方法
	 
	 @return 
	*/
	@Override
	protected String DoDefaultMethod()
	{
		//找不不到标记就抛出异常.
		throw new RuntimeException("@标记[" + this.getDoType() + "]，没有找到.");
	}

		///#endregion 执行父类的重写方法.



		///#region 功能界面 .
	/** 
	 转化拼音 @李国文.
	 
	 @return 返回转换后的拼音
	*/
	public final String FrmTextBox_ParseStringToPinyin()
	{
		String name = GetRequestVal("name");

		String flag = this.GetRequestVal("flag");
		flag = DataType.IsNullOrEmpty(flag) == true ? "true" : flag.toLowerCase();

		//此处配置最大长度为20，edited by liuxc,2017-9-25
		return CCFormAPI.ParseStringToPinyinField(name, Objects.equals(flag, "true"), true, 20);
	}

		///#endregion 功能界面方法.

	/** 
	 获得表单对应的物理表特定的数据类型字段
	 
	 @return 
	*/
	public final String FrmTextBoxChoseOneField_Init() throws Exception {
		DataTable mydt = MapData.GetFieldsOfPTableMode2(this.getFrmID());
		mydt.TableName = "dt";
		return bp.tools.Json.ToJson(mydt);
	}
}
