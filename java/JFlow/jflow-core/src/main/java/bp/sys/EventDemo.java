package bp.sys;
import bp.sys.base.*;

/** 
 事件Demo
*/
public abstract class EventDemo extends EventBase
{

		///#region 属性.

		///#endregion 属性.

	/** 
	 事件Demo
	*/
	public EventDemo()
	{
		this.setTitle("事件demo执行演示.");
	}
	/** 
	 执行事件
	 1，如果遇到错误就抛出异常信息，前台界面就会提示错误并不向下执行。
	 2，执行成功，把执行的结果赋给SucessInfo变量，如果不需要提示就赋值为空或者为null。
	 3，所有的参数都可以从  this.SysPara.GetValByKey中获取。
	*/
	@Override
	public void Do()
	{
		if (1 == 2)
		{
			throw new RuntimeException("@执行错误xxxxxx.");
		}

		//如果你要向用户提示执行成功的信息，就给他赋值，否则就不必赋值。
		this.SucessInfo = "执行成功提示.";
	}
}
