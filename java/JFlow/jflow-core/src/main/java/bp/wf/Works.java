package bp.wf;

import bp.en.*;
import java.util.*;

/** 
 工作 集合
*/
public abstract class Works extends EntitiesOID
{
	/** 
	 信息采集基类
	*/
	public Works()
	{
	}
	public final List<Work> ToJavaList()
	{
		return (List<Work>)(Object)this;
	}

}
