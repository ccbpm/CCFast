package bp.port;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import java.util.*;

/** 
 操作员 的摘要说明。
*/
public class EmpSetting extends EntityNoName
{

		///#region 构造函数
	/** 
	 操作员f
	*/
	public EmpSetting()
	{
	}
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getIsAdmin() == true)
		{
			uac.OpenAll();
		}
		else
		{
			uac.Readonly();
		}
		uac.IsInsert = false;
		uac.IsDelete = false;
		return uac;
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Port_Emp", "我的设置");
		map.setEnType(EnType.App);
		map.IndexField = EmpAttr.FK_Dept;


			///#region 字段
		/*关于字段属性的增加 */
		map.AddTBStringPK(EmpAttr.No, null, "手机号/ID", false, false, 1, 500, 90);
		map.AddTBString(EmpAttr.UserID, null, "登录ID", true, true, 0, 100, 10);
		map.AddTBString(EmpAttr.Name, null, "姓名", true, false, 0, 500, 130);
		map.AddDDLEntities(EmpAttr.FK_Dept, null, "当前部门", new bp.port.Depts(), false);

		//状态. 0=启用，1=禁用.
		//  map.AddTBInt(EmpAttr.EmpSta, 0, "EmpSta", false, false);
		map.AddTBString(EmpAttr.Leader, null, "部门领导", false, false, 0, 100, 10);
		map.AddTBString(EmpAttr.Tel, null, "电话", true, false, 0, 20, 130, true);
		map.AddTBString(EmpAttr.Email, null, "邮箱", true, false, 0, 100, 132, true);
		map.AddTBString(EmpAttr.PinYin, null, "拼音", false, false, 0, 1000, 132, false);
		map.AddTBString(EmpAttr.OrgNo, null, "OrgNo", true, true, 0, 500, 132, false);

			///#endregion 字段


			///#region 相关方法.
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

		////节点绑定部门. 节点绑定部门.
		//map.getAttrsOfOneVSM().AddBranches(new DeptEmps(), new bp.port.Depts(),
		//   bp.port.DeptEmpAttr.FK_Emp,
		//   bp.port.DeptEmpAttr.FK_Dept, "部门维护", EmpAttr.Name, EmpAttr.No, "@OrgNo");

		rm = new RefMethod();
		rm.Title = "修改密码";
		rm.ClassMethodName = this.toString() + ".DoResetpassword";
		try{
			rm.getHisAttrs().AddTBString("pass1", null, "输入密码", true, false, 0, 100, 100);
			rm.getHisAttrs().AddTBString("pass2", null, "再次输入", true, false, 0, 100, 100);
		}catch(Exception e){

		}

		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "设置部门直属领导";
		//rm.ClassMethodName = this.ToString() + ".DoEditLeader";
		//rm.RefAttrKey = "LeaderName";
		//rm.refMethodType = RefMethodType.LinkModel;
		//map.AddRefMethod(rm);

			///#endregion 相关方法.

		this.set_enMap(map);
		return this.get_enMap();
	}


		///#region 方法执行.
	public final String DoEditLeader() {
		return "../../../GPM/EmpLeader.htm?FK_Emp=" + this.getNo() + "&FK_Dept=" + this.GetValByKey("FK_Dept");
	}

	public final String DoEmpDepts() {
		return "/GPM/EmpDepts.htm?FK_Emp=" + this.getNo();
	}

	public final String DoSinger() throws Exception {
		//路径
		return "../../../GPM/Siganture.htm?EmpNo=" + this.getNo();
	}

		///#endregion 方法执行.


	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		//增加拼音，以方便查找.
		if (DataType.IsNullOrEmpty(this.getName()) == true)
		{
			throw new RuntimeException("err@名称不能为空.");
		}

		String pinyinQP = DataType.ParseStringToPinyin(this.getName()).toLowerCase();
		String pinyinJX = DataType.ParseStringToPinyinJianXie(this.getName()).toLowerCase();
		this.SetValByKey("PinYin", "," + pinyinQP + "," + pinyinJX + ",");
		return super.beforeUpdateInsertAction();
	}


	@Override
	protected boolean beforeDelete() throws Exception
	{
			throw new RuntimeException("err@您不能删除别人的数据.");
	}
	/** 
	 获取集合
	*/
	@Override
	public Entities GetNewEntities()
	{
		return new EmpSettings();
	}

		///#endregion 构造函数
}
