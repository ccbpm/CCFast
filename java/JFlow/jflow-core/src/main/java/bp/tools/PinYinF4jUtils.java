package bp.tools;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinF4jUtils
{
	/**
	 * 将汉字转换为全拼
	 * 
	 * param src
	 * @return String
	 */
	public static String getPinYin(String src)
	{
		char[] t1 = null;
		t1 = src.toCharArray();
		// System.out.println(t1.length);
		String[] t2 = new String[t1.length];
		// System.out.println(t2.length);
		// 设置汉字拼音输出的格式
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try
		{
			for (int i = 0; i < t0; i++)
			{
				// 判断能否为汉字字符
				// System.out.println(t1[i]);
				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+"))
				{
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
					t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
				} else
				{
					// 如果不是汉字字符，间接取出字符并连接到字符串t4后
					t4 += Character.toString(t1[i]);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e)
		{
			e.printStackTrace();
		}
		return t4;
	}
	
	/**
	 * 提取每个汉字的首字母
	 * 
	 * param str
	 * @return String
	 */
	public static String getPinYinHeadChar(String str)
	{
		String convert = "";
		for (int j = 0; j < str.length(); j++)
		{
			char word = str.charAt(j);
			// 提取汉字的首字母
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null)
			{
				convert += pinyinArray[0].charAt(0);
			} else
			{
				convert += word;
			}
		}
		return convert;
	}
	
	/**
	 * 将字符串转换成ASCII码
	 * 
	 * param cnStr
	 * @return String
	 */
	public static String getCnASCII(String cnStr)
	{
		StringBuffer strBuf = new StringBuffer();
		// 将字符串转换成字节序列
		byte[] bGBK = cnStr.getBytes();
		for (int i = 0; i < bGBK.length; i++)
		{
			// System.out.println(Integer.toHexString(bGBK[i] & 0xff));
			// 将每个字符转换成ASCII码
			strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
		}
		return strBuf.toString();
	}
	
	/**
	 * 获取字符串内的所有汉字的汉语拼音并大写每个字的首字母
	 * 
	 * param chinese
	 * @return
	 */
	public static String spell(String chinese)
	{
		if (chinese == null)
		{
			return null;
		}
		if (chinese.getBytes().length == chinese.length())
		{
			return getPinYin(chinese);
		}
		
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 小写
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不标声调
		format.setVCharType(HanyuPinyinVCharType.WITH_V);// u:的声母替换为v
		try
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < chinese.length(); i++)
			{
				String[] array = PinyinHelper.toHanyuPinyinStringArray(
						chinese.charAt(i), format);
				if (array == null || array.length == 0)
				{
					continue;
				}
				String s = array[0];// 不管多音字,只取第一个
				char c = s.charAt(0);// 大写第一个字母
				String pinyin = String.valueOf(c).toUpperCase()
						.concat(s.substring(1));
				sb.append(pinyin);
			}
			return sb.toString();
		} catch (BadHanyuPinyinOutputFormatCombination e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
//	public static void main(String[] args)
//	{
//		String cnStr = "好人";
//		System.out.println(spell(cnStr));
//	}
}
