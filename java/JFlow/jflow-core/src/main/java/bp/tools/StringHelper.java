package bp.tools;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringHelper
{
	public static boolean isNullOrEmpty(String string)
	{
		return string == null || "".equals(string) || "null".equals(string) || string.length() == 0;
	}
	
	public static String join(String separator, String[] stringarray)
	{
		if (stringarray == null)
			return null;
		else
			return join(separator, stringarray, 0, stringarray.length);
	}
	
	public static String join(String separator, String[] stringarray,
			int startindex, int count)
	{
		String result = "";
		
		if (stringarray == null)
			return null;
		
		for (int index = startindex; index < stringarray.length
				&& index - startindex < count; index++)
		{
			if (separator != null && index > startindex)
				result += separator;
			
			if (stringarray[index] != null)
				result += stringarray[index];
		}
		
		return result;
	}
	
	public static String trimEnd(String string, Character... charsToTrim)
	{
		if (string == null || charsToTrim == null)
			return string;
		
		int lengthToKeep = string.length();
		for (int index = string.length() - 1; index >= 0; index--)
		{
			boolean removeChar = false;
			if (charsToTrim.length == 0)
			{
				if (Character.isWhitespace(string.charAt(index)))
				{
					lengthToKeep = index;
					removeChar = true;
				}
			} else
			{
				for (int trimCharIndex = 0; trimCharIndex < charsToTrim.length; trimCharIndex++)
				{
					if (string.charAt(index) == charsToTrim[trimCharIndex])
					{
						lengthToKeep = index;
						removeChar = true;
						break;
					}
				}
			}
			if (!removeChar)
				break;
		}
		return string.substring(0, lengthToKeep);
	}
	
	public static String trimStart(String string, Character... charsToTrim)
	{
		if (string == null || charsToTrim == null)
			return string;
		
		int startingIndex = 0;
		for (int index = 0; index < string.length(); index++)
		{
			boolean removeChar = false;
			if (charsToTrim.length == 0)
			{
				if (Character.isWhitespace(string.charAt(index)))
				{
					startingIndex = index + 1;
					removeChar = true;
				}
			} else
			{
				for (int trimCharIndex = 0; trimCharIndex < charsToTrim.length; trimCharIndex++)
				{
					if (string.charAt(index) == charsToTrim[trimCharIndex])
					{
						startingIndex = index + 1;
						removeChar = true;
						break;
					}
				}
			}
			if (!removeChar)
				break;
		}
		return string.substring(startingIndex);
	}
	
	public static String trim(String string, Character... charsToTrim)
	{
		return trimEnd(trimStart(string, charsToTrim), charsToTrim);
	}
	
	public static boolean stringsEqual(String s1, String s2)
	{
		if (s1 == null && s2 == null)
			return true;
		else
			return s1 != null && s1.equals(s2);
	}
	
	/**
	 * 判断是否是空字符串 null和"" null返回result,否则返回字符串
	 * 
	 * param s
	 * @return
	 */
	public static String isEmpty(String s, String result)
	{
		if (s != null && !s.equals("") && !s.equals("null"))
		{
			return s;
		}
		return result;
	}
	
	public static String stringFill(String source, int fillLength,
			char fillChar, boolean isLeftFill)
	{
		if (source == null || source.length() >= fillLength)
			return source;
		
		StringBuilder result = new StringBuilder(fillLength);
		int len = fillLength - source.length();
		if (isLeftFill)
		{
			for (; len > 0; len--)
			{
				result.append(fillChar);
			}
			result.append(source);
		} else
		{
			result.append(source);
			for (; len > 0; len--)
			{
				result.append(fillChar);
			}
		}
		return result.toString();
	}
	
	public static String stringFill2(String source, int fillLength,
			char fillChar, boolean isLeftFill)
	{
		if (source == null || source.length() >= fillLength)
			return source;
		
		char[] c = new char[fillLength];
		char[] s = source.toCharArray();
		int len = s.length;
		if (isLeftFill)
		{
			int fl = fillLength - len;
			for (int i = 0; i < fl; i++)
			{
				c[i] = fillChar;
			}
			System.arraycopy(s, 0, c, fl, len);
		} else
		{
			System.arraycopy(s, 0, c, 0, len);
			for (int i = len; i < fillLength; i++)
			{
				c[i] = fillChar;
			}
		}
		return String.valueOf(c);
	}
	
	/**
	 * 将字符串按指定的"分割符"分割成字符串数组(接受连续出现分割符部分) Take a String which is a delimited
	 * list and convert it to a String array.
	 * 
	 * param s
	 *            String
	 * param delim
	 *            delim (this will not be returned)
	 * @return an array of the tokens in the list
	 *         split("a;b;c;;")={'a','b','c','',''} split(" a;b;c; ; ")={'
	 *         a','b','c',' ',' '}
	 */
	public static String[] split(String s, String delim)
	{
		if (s == null)
		{
			return new String[0];
		}
		if (delim == null)
		{
			return new String[]
			{ s };
		}
		
		List l = new LinkedList();
		int pos = 0;
		int delPos = 0;
		while ((delPos = s.indexOf(delim, pos)) != -1)
		{
			l.add(s.substring(pos, delPos));
			pos = delPos + delim.length();
		}
		if (pos <= s.length())
		{
			// add rest of String
			l.add(s.substring(pos));
		}
		return (String[]) l.toArray(new String[l.size()]);
	}
	
	/**
	 * 将字符串按指定的"分割符"分割成数值数组
	 * 
	 * param s
	 * param delim
	 * @return
	 */
	public static int[] splitToIntArray(String s, String delim)
	{
		String[] stringValueArray = split(s, delim);
		int[] intValueArray = new int[stringValueArray.length];
		for (int i = 0; i < intValueArray.length; i++)
		{
			intValueArray[i] = Integer.parseInt(stringValueArray[i]);
		}
		return intValueArray;
	}
	
    final static char CHAR_SPACE = ' '; // 0x0020
	
	public static boolean isAllWhitespace(String str)
    {
        for (int i = 0, len = str.length(); i < len; ++i) {
            if (str.charAt(i) > CHAR_SPACE) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllWhitespace(char[] ch, int start, int len)
    {
        len += start;
        for (; start < len; ++start) {
            if (ch[start] > CHAR_SPACE) {
                return false;
            }
        }
        return true;
    }
    public static String substring(String string, int start, int length)
	{
		if (length < 0)
			throw new IndexOutOfBoundsException("Parameter length cannot be negative.");

		return string.substring(start, start + length);
	}
    
    /**
     * This method replaces the .NET static String method 'IsNullOrWhiteSpace'.
     * param string
     * @return
     */
  	public static boolean isNullOrWhiteSpace(String string)
  	{
  		if (string == null)
  			return true;

  		for (int index = 0; index < string.length(); index++)
  		{
  			if ( ! Character.isWhitespace(string.charAt(index)))
  				return false;
  		}

  		return true;
  	}
  	
  	/**
  	 * This method replaces the .NET String method 'remove' (1 parameter version).
  	 * param string
  	 * param start
  	 * @return
  	 */
  	public static String remove(String string, int start)
  	{
  		return string.substring(0, start);
  	}
  	
	/**
	 * This method replaces the .NET String method 'remove' (2 parameter version).
	 * param string
	 * param start
	 * param count
	 * @return
	 */
  	public static String remove(String string, int start, int count)
  	{
  		return string.substring(0, start) + string.substring(start + count);
  	}
  	
  	/**
  	 * This method replaces the .NET String method 'PadRight' (1 parameter version).
  	 * param string
  	 * param totalWidth
  	 * @return
  	 */
  	public static String padRight(String string, int totalWidth)
  	{
  		return padRight(string, totalWidth, ' ');
  	}

  	/**
  	 * This method replaces the .NET String method 'PadRight' (2 parameter version).
  	 * param string
  	 * param totalWidth
  	 * param paddingChar
  	 * @return
  	 */
  	public static String padRight(String string, int totalWidth, char paddingChar)
  	{
  		StringBuilder sb = new StringBuilder(string);

  		while (sb.length() < totalWidth)
  		{
  			sb.append(paddingChar);
  		}

  		return sb.toString();
  	}
  	
  	/**
  	 * This method replaces the .NET String method 'PadLeft' (1 parameter version).
  	 * param string
  	 * param totalWidth
  	 * @return
  	 */
  	public static String padLeft(String string, int totalWidth)
  	{
  		return padLeft(string, totalWidth, ' ');
  	}

  	/**
  	 * This method replaces the .NET String method 'PadLeft' (2 parameter version).
  	 * param string
  	 * param totalWidth
  	 * param paddingChar
  	 * @return
  	 */
  	public static String padLeft(String string, int totalWidth, char paddingChar)
  	{
  		StringBuilder sb = new StringBuilder("");

  		while (sb.length() + string.length() < totalWidth)
  		{
  			sb.append(paddingChar);
  		}

  		sb.append(string);
  		return sb.toString();
  	}
  	
  	/**
  	 * This method replaces the .NET String method 'LastIndexOf' (char version).
  	 * param string
  	 * param value
  	 * param startIndex
  	 * param count
  	 * @return
  	 */
  	public static int lastIndexOf(String string, char value, int startIndex, int count)
	{
		int leftMost = startIndex + 1 - count;
		int rightMost = startIndex + 1;
		String subString = string.substring(leftMost, rightMost);
		int lastIndexInSubString = subString.lastIndexOf(value);
		if (lastIndexInSubString < 0)
			return -1;
		else
			return lastIndexInSubString + leftMost;
	}

	/**
	 * This method replaces the .NET String method 'LastIndexOf' (String version).
	 * param string
	 * param value
	 * param startIndex
	 * param count
	 * @return
	 */
	public static int lastIndexOf(String string, String value, int startIndex, int count)
	{
		int leftMost = startIndex + 1 - count;
		int rightMost = startIndex + 1;
		String subString = string.substring(leftMost, rightMost);
		int lastIndexInSubString = subString.lastIndexOf(value);
		if (lastIndexInSubString < 0)
			return -1;
		else
			return lastIndexInSubString + leftMost;
	}

	/**
	 * This method replaces the .NET String method 'IndexOfAny' (1 parameter version).
	 * param string
	 * param anyOf
	 * @return
	 */
	public static int indexOfAny(String string, char[] anyOf)
	{
		int lowestIndex = -1;
		for (char c : anyOf)
		{
			int index = string.indexOf(c);
			if (index > -1)
			{
				if (lowestIndex == -1 || index < lowestIndex)
				{
					lowestIndex = index;

					if (index == 0)
						break;
				}
			}
		}

		return lowestIndex;
	}

	/**
	 * This method replaces the .NET String method 'IndexOfAny' (2 parameter version).
	 * param string
	 * param anyOf
	 * param startIndex
	 * @return
	 */
	public static int indexOfAny(String string, char[] anyOf, int startIndex)
	{
		int indexInSubString = indexOfAny(string.substring(startIndex), anyOf);
		if (indexInSubString == -1)
			return -1;
		else
			return indexInSubString + startIndex;
	}

	/**
	 * This method replaces the .NET String method 'IndexOfAny' (3 parameter version).
	 * param string
	 * param anyOf
	 * param startIndex
	 * param count
	 * @return
	 */
	public static int indexOfAny(String string, char[] anyOf, int startIndex, int count)
	{
		int endIndex = startIndex + count;
		int indexInSubString = indexOfAny(string.substring(startIndex, endIndex), anyOf);
		if (indexInSubString == -1)
			return -1;
		else
			return indexInSubString + startIndex;
	}

	/**
	 * This method replaces the .NET String method 'LastIndexOfAny' (1 parameter version).
	 * param string
	 * param anyOf
	 * @return
	 */
	public static int lastIndexOfAny(String string, char[] anyOf)
	{
		int highestIndex = -1;
		for (char c : anyOf)
		{
			int index = string.lastIndexOf(c);
			if (index > highestIndex)
			{
				highestIndex = index;

				if (index == string.length() - 1)
					break;
			}
		}

		return highestIndex;
	}

	/**
	 * This method replaces the .NET String method 'LastIndexOfAny' (2 parameter version).
	 * param string
	 * param anyOf
	 * param startIndex
	 * @return
	 */
	public static int lastIndexOfAny(String string, char[] anyOf, int startIndex)
	{
		String subString = string.substring(0, startIndex + 1);
		int lastIndexInSubString = lastIndexOfAny(subString, anyOf);
		if (lastIndexInSubString < 0)
			return -1;
		else
			return lastIndexInSubString;
	}

	/**
	 * This method replaces the .NET String method 'LastIndexOfAny' (3 parameter version).
	 * param string
	 * param anyOf
	 * param startIndex
	 * param count
	 * @return
	 */
	public static int lastIndexOfAny(String string, char[] anyOf, int startIndex, int count)
	{
		int leftMost = startIndex + 1 - count;
		int rightMost = startIndex + 1;
		String subString = string.substring(leftMost, rightMost);
		int lastIndexInSubString = lastIndexOfAny(subString, anyOf);
		if (lastIndexInSubString < 0)
			return -1;
		else
			return lastIndexInSubString + leftMost;
	}
	
	/**
	 * 校验字符串是否是数字
	 * 不能校验带-的数字
	 * 替换为// -?[0-9]+.?[0-9]+ 即可校验所有数字
	 * param str
	 * @return
	 */
	public static boolean isNumeric(String str){ 
	   Pattern pattern = Pattern.compile("[0-9]*"); 
	   Matcher isNum = pattern.matcher(str);
	   if( !isNum.matches() ){
	       return false; 
	   } 
	   return true; 
	}
}