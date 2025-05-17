package bp.ccbill.template;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 二维码
*/
public class MethodQRCodes extends EntitiesNoName
{
	/** 
	 二维码
	*/
	public MethodQRCodes()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MethodQRCode();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MethodQRCode> ToJavaList()
	{
		return (java.util.List<MethodQRCode>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MethodQRCode> Tolist()
	{
		ArrayList<MethodQRCode> list = new ArrayList<MethodQRCode>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MethodQRCode)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
