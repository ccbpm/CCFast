package bp.da;

import java.util.*;

/** 
 字段s
*/
public class KeyVals extends ArrayList<KeyVal>
{

	private static final long serialVersionUID = 1L;

	public final void Add(String _key, String _value, String _type) throws Exception
	{
		KeyVal tempVar = new KeyVal();
		tempVar.setkey(_key);
		tempVar.setvalue(_value);
		tempVar.settype(_type);
		this.add(tempVar);
	}
}