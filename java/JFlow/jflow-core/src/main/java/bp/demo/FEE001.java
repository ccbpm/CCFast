package bp.demo;

import bp.wf.FlowEventBase;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
 
public class FEE001 extends FlowEventBase
{
	public FEE001() throws Exception {
		super();
	}
	
	// 对ccflow事件的综合测试---- 一个类文件，可以注册多个流程模板.
	@Override
	public String getFlowMark()
	{
		return "205,109,232,";
	}
	
	
	// 返回制定格式的时间String
	public String getTime()
	{
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		
		return dateString;
	}
	
	// 文件是否存在
	public boolean isExit()
	{
		File file = new File("C:\\FLOWANDNODEEVENT.txt");
		
		if (!file.exists())
		{
			file.mkdir();
			return true;
		}
		return false;
	}
	
	// 写入日志
	public void writeFlie(String str)
	{
		
		try
		{
			String temp = "在：" + getTime() + ",触发了" + str;
			byte[] data = temp.getBytes("GB2312");
			
			FileOutputStream fo = new FileOutputStream(
					"D:\\FLOWANDNODEEVENT.txt", true);
			fo.write(data);
			fo.flush();
			fo.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// 重写节点运动事件.
	@Override
	public String SendWhen() {
		if (this.getHisNode().getNodeID() == 14401)
		{
			String getVal = this.GetValStr("getVal");
			
			writeFlie("节点编号为" + this.getHisNode().getNodeID() + "的SendWhen事件.\r\n"
					+ "获取表单字段getVal的值为：" + getVal + "\r\n");
			//throw new exc
		}
		return "";
	}
	
	@Override
	public String SendSuccess() throws Exception {
		 
		try {
			if (this.getHisNode().getNodeID() == 20501)
			{
				String getVal = this.GetValStr("getVal");
				writeFlie("节点编号为" + this.getHisNode().getNodeID()
						+ "的SendSuccess事件.\r\n" + "获取表单字段getVal的值为：" + getVal
						+ "\r\n");

			}
		}catch (Exception ex){
			throw  ex;
		}
		return super.SendSuccess();
	}
	/**
	 * 删除后
	 * 
	 * @return
	 */
	@Override
	public String AfterFlowDel()
	{
		return null;
	}
	
	/**
	 * 删除前
	 * 
	 * @return
	 */
	@Override
	public String BeforeFlowDel()
	{
		return null;
	}
	
	/**
	 * 结束后
	 * 
	 * @return
	 */
	@Override
	public String FlowOverAfter()
	{
		if (this.getHisNode().getFlowNo().equals("005"))
		{

		}
		//String sql
		return "写入成功....";
	}
	 
	/**
	 * 结束前
	 * 
	 * @return
	 */
	@Override
	public String FlowOverBefore()
	{
		return null;
	}
	
	// 重写事件，完成业务逻辑.
}
