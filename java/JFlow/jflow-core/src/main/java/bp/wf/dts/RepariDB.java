package bp.wf.dts;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;

/** 
 修复数据库 的摘要说明
*/
public class RepariDB extends Method
{
	/** 
	 不带有参数的方法
	*/
	public RepariDB()
	{
		this.Title = "修复数据库";
		this.Help = "把最新的版本的与当前的数据表结构，做一个自动修复, 修复内容：缺少列，缺少列注释，列注释不完整或者有变化。";
		this.Help += "<br>因为表单设计器的错误，丢失了字段，通过它也可以自动修复。";
	}
	/** 
	 设置执行变量
	 
	 @return 
	*/
	@Override
	public void Init()
	{
		//this.Warning = "您确定要执行吗？";
		//HisAttrs.AddTBString("P1", null, "原密码", true, false, 0, 10, 10);
		//HisAttrs.AddTBString("P2", null, "新密码", true, false, 0, 10, 10);
		//HisAttrs.AddTBString("P3", null, "确认", true, false, 0, 10, 10);
	}
	/** 
	 当前的操纵员是否可以执行这个方法
	*/
	@Override
	public boolean getIsCanDo()
	{
		return true;
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	*/
	@Override
	public Object Do() throws Exception {
		String rpt = bp.pub.PubClass.DBRpt(DBCheckLevel.High);


		//// 手动升级. 2011-07-08 补充节点字段分组.
		//String sql = "DELETE FROM Sys_EnCfg WHERE No='BP.WF.Template.NodeSheet'";
		//DBAccess.RunSQL(sql);

		//sql = "INSERT INTO Sys_EnCfg(No,GroupTitle) VALUES ('BP.WF.Template.NodeSheet','NodeID=基本配置@WarningHour=考核属性@SendLab=功能按钮标签与状态')";
		//DBAccess.RunSQL(sql);

		// 修复因bug丢失的字段.
		MapDatas mds = new MapDatas();
		mds.RetrieveAll();
		for (MapData md : mds.ToJavaList())
		{
			String nodeid =md.getNo().replace("ND","");
			try
			{
				Node nd = new Node(Integer.parseInt(nodeid));
				nd.RepareMap(nd.getHisFlow());
				continue;
			}
			catch (RuntimeException ex)
			{
			}

			MapAttr attr = new MapAttr();
			if (attr.IsExit(MapAttrAttr.KeyOfEn, "OID", MapAttrAttr.FK_MapData, md.getNo()) == false)
			{
				attr.setFrmID(md.getNo());
				attr.setKeyOfEn("OID");
				attr.setName("OID");
				attr.setMyDataType(DataType.AppInt);
				attr.setUIContralType(UIContralType.TB);
				attr.setLGType(FieldTypeS.Normal);
				attr.setUIVisible(false);
				attr.setUIIsEnable(false);
				attr.setDefVal("0");
				attr.setHisEditType(EditType.Readonly);
				attr.Insert();
			}
		}
		return "执行成功...";
	}
}
