package bp.tools;

import java.util.ArrayList;

/**
 * Class1 的摘要说明。
 */
public class StringExpressionCalculate
{
	public StringExpressionCalculate()throws Exception
	{
	}
	
	/**
	 * 转换到decimal
	 * 
	 * param exp
	 * @return
	 */
	public final double TurnToDecimal(String exp)
	{
		String str = CalculateParenthesesExpression(exp);
		str = str.replace("E+", "");
		return Math.round(Double.parseDouble(str));
	}
	
	public final float TurnToFloat(String exp)
	{
		return Float.parseFloat(CalculateParenthesesExpression(exp));
	}
	
	/**
	 * 中序转换成后序表达式再计算 如：23+56/(102-100)*((36-24)/(8-6))
	 * 转换成：23|56|102|100|-|/|*|36|24|-|8|6|-|/|*|+" 以便利用栈的方式都进行计算。
	 */
	public final String CalculateParenthesesExpression(String Expression)
	{
		ArrayList<String> operatorList = new ArrayList<String>();
		String operator1;
		String ExpressionString = "";
		String operand3;
		Expression = Expression.replace(" ", "");
		Expression = Expression.replace("%", "");
		Expression = Expression.replace("％", "");
		
		while (Expression.length() > 0)
		{
			operand3 = "";
			// 取数字处理
			if (IsNumber(String.valueOf(Expression.charAt(0)))
					|| Expression.charAt(0) == '.')
			{
				while (IsNumber(String.valueOf(Expression.charAt(0)))
						|| Expression.charAt(0) == '.')
				{
					operand3 += String.valueOf(Expression.charAt(0));
					Expression = Expression.substring(1);
					if (Expression.equals(""))
					{
						break;
					}
				}
				ExpressionString += operand3 + "|";
			}
			
			// 取"C"处理
			if (Expression.length() > 0
					&& String.valueOf(Expression.charAt(0)).equals("("))
			{
				operatorList.add("(");
				Expression = Expression.substring(1);
			}
			
			// 取")"处理
			operand3 = "";
			if (Expression.length() > 0
					&& String.valueOf(Expression.charAt(0)).equals(")"))
			{
				do
				{
					
					if (!operatorList.get(operatorList.size() - 1).toString()
							.equals("("))
					{
						operand3 += operatorList.get(operatorList.size() - 1)
								.toString() + "|";
						operatorList.remove(operatorList.size() - 1);
					} else
					{
						operatorList.remove(operatorList.size() - 1);
						break;
					}
					
				} while (true);
				ExpressionString += operand3;
				Expression = Expression.substring(1);
			}
			
			// 取运算符号处理
			operand3 = "";
			if (Expression.length() > 0
					&& (String.valueOf(Expression.charAt(0)).equals("*")
							|| String.valueOf(Expression.charAt(0)).equals("/")
							|| String.valueOf(Expression.charAt(0)).equals("+") || String
							.valueOf(Expression.charAt(0)).equals("-")))
			{
				operator1 = String.valueOf(Expression.charAt(0));
				if (operatorList.size() > 0)
				{
					
					if (operatorList.get(operatorList.size() - 1).toString()
							.equals("(")
							|| verifyOperatorPriority(operator1, operatorList
									.get(operatorList.size() - 1).toString()))
					{
						operatorList.add(operator1);
					} else
					{
						operand3 += operatorList.get(operatorList.size() - 1)
								.toString() + "|";
						operatorList.remove(operatorList.size() - 1);
						operatorList.add(operator1);
						ExpressionString += operand3;
					}
				} else
				{
					operatorList.add(operator1);
				}
				Expression = Expression.substring(1);
			}
		}
		operand3 = "";
		while (operatorList.size() != 0)
		{
			operand3 += operatorList.get(operatorList.size() - 1).toString()
					+ "|";
			operatorList.remove(operatorList.size() - 1);
		}
		ExpressionString += operand3.substring(0, operand3.length() - 1);
		;
		return CalculateParenthesesExpressionEx(ExpressionString);
	}
	
	// 第二步:把转换成后序表达的式子计算
	// 23|56|102|100|-|/|*|36|24|-|8|6|-|/|*|+"
	private String CalculateParenthesesExpressionEx(String Expression)
	{
		// 定义两个栈
		ArrayList<String> operandList = new ArrayList<String>();
		float operand1;
		float operand2;
		String[] operand3;
		
		Expression = Expression.replace(" ", "");
		operand3 = Expression.split("|", -1);
		
		for (int i = 0; i < operand3.length; i++)
		{
			/*
			 * warning if (Character.IsNumber(operand3[i], 0)) {
			 */
			if (IsNumber(operand3[i]))
			{
				operandList.add(operand3[i].toString());
			} else
			{
				// 两个操作数退栈和一个操作符退栈计算
				operand2 = (float) Double.parseDouble(operandList
						.get(operandList.size() - 1));
				operandList.remove(operandList.size() - 1);
				operand1 = (float) Double.parseDouble(operandList
						.get(operandList.size() - 1));
				operandList.remove(operandList.size() - 1);
				operandList.add((new Float(calculate(operand1, operand2,
						operand3[i]))).toString());
			}
		}
		return operandList.get(0).toString();
	}
	
	// 判断两个运算符优先级别
	private boolean verifyOperatorPriority(String Operator1, String Operator2)
	{
		
		if (Operator1.equals("*") && Operator2.equals("+"))
		{
			return true;
		} else if (Operator1.equals("*") && Operator2.equals("-"))
		{
			return true;
		} else if (Operator1.equals("/") && Operator2.equals("+"))
		{
			return true;
		} else if (Operator1.equals("/") && Operator2.equals("-"))
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	// 计算
	private float calculate(float operand1, float operand2, String operator2)
	{
		if (operator2.equals("*"))
		{
			operand1 *= operand2;
		} else if (operator2.equals("/"))
		{
			operand1 /= operand2;
		} else if (operator2.equals("+"))
		{
			operand1 += operand2;
		} else if (operator2.equals("-"))
		{
			operand1 -= operand2;
		} else
		{
		}
		return operand1;
	}
	
	public static boolean IsNumber(String str)
	{
		if (str.matches("\\d *"))
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	/** 
	 去掉文件名中的无效字符,如 \ / : * ? " < > | 
	 param fileName 待处理的文件名
	 @return 处理后的文件名
	*/
	public static String ReplaceBadCharOfFileName(String fileName)
	{
		String str = fileName;
		str = str.replace("\\", "");
		str = str.replace("/", "");
		str = str.replace(":", "");
		str = str.replace("*", "");
		str = str.replace("?", "");
		str = str.replace("\"", "");
		str = str.replace("<", "");
		str = str.replace(">", "");
		str = str.replace("|", "");
		str = str.replace(" ", "");
		//前面的替换会产生空格,最后将其一并替换掉
		return str;
	}
}