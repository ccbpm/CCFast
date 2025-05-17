package bp.demo;

import bp.en.EntityOID;
import bp.en.Map;

public class QingJia extends EntityOID
{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 请假人部门名称
	 */
	public final String getQingJiaRenDeptName() {
		return this.GetValStringByKey(QingJiaAttr.QingJiaRenDeptName);
	}
	
	public final void setQingJiaRenDeptName(String value)  {
		this.SetValByKey(QingJiaAttr.QingJiaRenDeptName, value);
	}
	
	/**
	 * 请假人编号
	 */
	public final String getQingJiaRenNo() {
		return this.GetValStringByKey(QingJiaAttr.QingJiaRenNo);
	}
	
	public final void setQingJiaRenNo(String value)  {
		this.SetValByKey(QingJiaAttr.QingJiaRenNo, value);
	}
	
	/**
	 * 请假人名称
	 */
	public final String getQingJiaRenName() {
		return this.GetValStringByKey(QingJiaAttr.QingJiaRenName);
	}
	
	public final void setQingJiaRenName(String value)  {
		this.SetValByKey(QingJiaAttr.QingJiaRenName, value);
	}
	
	/**
	 * 请假人部门编号
	 */
	public final String getQingJiaRenDeptNo() {
		return this.GetValStringByKey(QingJiaAttr.QingJiaRenDeptNo);
	}
	
	public final void setQingJiaRenDeptNo(String value)  {
		this.SetValByKey(QingJiaAttr.QingJiaRenDeptNo, value);
	}
	
	/**
	 * 请假原因
	 */
	public final String getQingJiaYuanYin() {
		return this.GetValStringByKey(QingJiaAttr.QingJiaYuanYin);
	}
	
	public final void setQingJiaYuanYin(String value)  {
		this.SetValByKey(QingJiaAttr.QingJiaYuanYin, value);
	}
	
	/**
	 * 请假天数
	 */
	public final float getQingJiaTianShu() {
		return this.GetValIntByKey(QingJiaAttr.QingJiaTianShu);
	}
	
	public final void setQingJiaTianShu(float value)  {
		this.SetValByKey(QingJiaAttr.QingJiaTianShu, value);
	}
	
	/**
	 * 部门审批意见
	 */
	public final String getNoteBM() {
		return this.GetValStringByKey(QingJiaAttr.NoteBM);
	}
	
	public final void setNoteBM(String value)  {
		this.SetValByKey(QingJiaAttr.NoteBM, value);
	}
	
	/**
	 * 总经理意见
	 */
	public final String getNoteZJL() {
		return this.GetValStringByKey(QingJiaAttr.NoteZJL);
	}
	
	public final void setNoteZJL(String value)  {
		this.SetValByKey(QingJiaAttr.NoteZJL, value);
	}
	
	/**
	 * 人力资源意见
	 */
	public final String getNoteRL() {
		return this.GetValStringByKey(QingJiaAttr.NoteRL);
	}
	
	public final void setNoteRL(String value)  {
		this.SetValByKey(QingJiaAttr.NoteRL, value);
	}
	
	/**
	 * 请假
	 */
	public QingJia()
	{
	}
	
	/**
	 * 请假
	 * 
	 * @param oid
	 *            实体类
	 * @throws Exception 
	 */
	public QingJia(int oid) throws Exception
	{
		super(oid);
	}
	
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		
		Map map = new Map("Demo_QingJia");
		map.setEnDesc("请假");
		
		map.AddTBIntPKOID();
		map.AddTBString(QingJiaAttr.QingJiaRenNo, null, "请假人编号", false, false,
				0, 200, 10);
		map.AddTBString(QingJiaAttr.QingJiaRenName, null, "请假人名称", true, false,
				0, 200, 70);
		map.AddTBString(QingJiaAttr.QingJiaRenDeptNo, "", "请假人部门编号", true,
				false, 0, 200, 50);
		map.AddTBString(QingJiaAttr.QingJiaRenDeptName, null, "请假人部门名称", true,
				false, 0, 200, 50);
		map.AddTBString(QingJiaAttr.QingJiaYuanYin, null, "请假原因", true, false,
				0, 200, 150);
		map.AddTBFloat(QingJiaAttr.QingJiaTianShu, 0, "请假天数", true, false);
		
		// 审核信息.
		map.AddTBString(QingJiaAttr.NoteBM, null, "部门经理意见", true, false, 0,
				200, 150);
		map.AddTBString(QingJiaAttr.NoteZJL, null, "总经理意见", true, false, 0,
				200, 150);
		map.AddTBString(QingJiaAttr.NoteRL, null, "人力资源意见", true, false, 0,
				200, 150);
		
		this.set_enMap(map);
		return this.get_enMap();
	}
}
