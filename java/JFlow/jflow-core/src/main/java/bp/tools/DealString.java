package bp.tools;

import bp.da.DataType;

/**
 * 功能：字符串处理函数集
 */
public class DealString
{
	// 私有成员
	/**
	 * 输入字符串
	 */
	private String inputString = null;
	/**
	 * 输出字符串
	 */
	private String outString = null;
	/**
	 * 提示信息
	 */
	private String noteMessage = null;
	
	// 公共属性
	/**
	 * 输入字符串
	 */
	public final String getInputString()
	{
		return inputString;
	}
	
	public final void setInputString(String value)
	{
		inputString = value;
	}
	
	/**
	 * 输出字符串
	 */
	public final String getOutString()
	{
		return outString;
	}
	
	public final void setOutString(String value)
	{
		outString = value;
	}
	
	/**
	 * 提示信息
	 */
	public final String getNoteMessage()
	{
		return noteMessage;
	}
	
	public final void setNoteMessage(String value)
	{
		noteMessage = value;
	}
	
	// 构造函数
	public DealString()
	{
	}
	public static String trimEnd(String str,String endChart){
		if(DataType.IsNullOrEmpty(str)==true)
			return "";
		while(str.endsWith(endChart)){
			str = str.substring(0,str.length()-endChart.length());
		}
		return str;
	}
	// 公共方法
	public final void ConvertToChineseNum()throws Exception
	{
		String numList = "零壹贰叁肆伍陆柒捌玖";
		String rmbList = "分角元拾佰仟万拾佰仟亿拾佰仟万";
		double number = 0;
		String tempOutString = "";
		
		try
		{
			number = Double.parseDouble(this.inputString);
		} catch (java.lang.Exception e)
		{
			this.noteMessage = "传入参数非数字！";
			return;
		}
		
		if (number > 9999999999999.99)
		{
			this.noteMessage = "超出范围的人民币值";
		}
		
		// 将小数转化为整数字符串
		Long ff = (long) (number * 100);
		String tempNumberString = ff.toString();
		int tempNmberLength = tempNumberString.length();
		int i = 0;
		while (i < tempNmberLength)
		{
			int oneNumber = Integer.parseInt(tempNumberString.substring(i,
					i + 1));
			String oneNumberChar = numList.substring(oneNumber, oneNumber + 1);
			String oneNumberUnit = rmbList.substring(tempNmberLength - i - 1,
					tempNmberLength - i - 1 + 1);
			if (!oneNumberChar.equals("零"))
			{
				tempOutString += oneNumberChar + oneNumberUnit;
			} else
			{
				if (oneNumberUnit.equals("亿") || oneNumberUnit.equals("万")
						|| oneNumberUnit.equals("元")
						|| oneNumberUnit.equals("零"))
				{
					while (tempOutString.endsWith("零"))
					{
						tempOutString = tempOutString.substring(0,
								tempOutString.length() - 1);
					}
					
				}
				if (oneNumberUnit.equals("亿")
						|| (oneNumberUnit.equals("万") && !tempOutString
								.endsWith("亿")) || oneNumberUnit.equals("元"))
				{
					tempOutString += oneNumberUnit;
				} else
				{
					boolean tempEnd = tempOutString.endsWith("亿");
					boolean zeroEnd = tempOutString.endsWith("零");
					if (tempOutString.length() > 1)
					{
						boolean zeroStart = tempOutString.substring(
								tempOutString.length() - 2,
								tempOutString.length() - 2 + 2).startsWith("零");
						if (!zeroEnd && (zeroStart || !tempEnd))
						{
							tempOutString += oneNumberChar;
						}
					} else
					{
						if (!zeroEnd && !tempEnd)
						{
							tempOutString += oneNumberChar;
						}
					}
				}
			}
			i += 1;
		}
		
		while (tempOutString.endsWith("零"))
		{
			tempOutString = tempOutString.substring(0,
					tempOutString.length() - 1);
		}
		
		while (tempOutString.endsWith("元"))
		{
			tempOutString = tempOutString + "整";
		}
		
		this.outString = tempOutString;
		
	}
	
	//右补齐
	public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }
    /**
     * @功能 String左对齐
     */
    public static String padLeft(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }
	
	
}