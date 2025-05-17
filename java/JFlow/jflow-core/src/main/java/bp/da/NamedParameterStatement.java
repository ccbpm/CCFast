package bp.da;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class wraps around a {@link PreparedStatement} and allows the programmer
 * to set parameters by name instead of by index. This eliminates any confusion
 * as to which parameter index represents what. This also means that rearranging
 * the SQL statement or adding a parameter doesn't involve renumbering your
 * indices. Code such as this:
 *
 * <pre>
 * <code>
 * Connection conn = getConnection();
 * String sql = "select * from my_table where name=? or address=?";
 * PreparedStatement p = conn.prepareStatement(sql);
 * p.setString(1, "bob");
 * p.setString(2, "123");
 * ResultSet rs = p.executeQuery();
 * </code>
 * </pre>
 * 
 * Can be replaced with:
 *
 * <pre>
 * <code>
 * Connection conn = getConnection();
 * String sql = "select * from my_table where name=:name or address=:address";
 * NamedParameterStatement p = new NamedParameterStatement(conn, sql);
 * p.setString("name", "bob");
 * p.setString("address", "123");
 * ResultSet rs = p.executeQuery();
 * </code>
 * </pre>
 */
public class NamedParameterStatement extends PreparedStatementWrapper
{
	private static final HashMap<String, Map<String, List<Integer>>> nameIndexCache = new HashMap<String, Map<String, List<Integer>>>();
	private static final HashMap<String, String> parsedSqlCache = new HashMap<String, String>();
	
	private final String parsedSql;
	private final Map<String, List<Integer>> nameIndexMap;
	
	/**
	 * Creates a NamedParameterStatement. Wraps a call to c.
	 * {@link Connection#prepareStatement(java.lang.String) prepareStatement}.
	 * 
	 * param conn
	 *            the database connection
	 * param sql
	 *            the parameterized sql
	 * @throws Exception
	 *             if the statement could not be created
	 */
	public NamedParameterStatement(Connection conn, String sql)
			throws Exception
	{
		if (nameIndexCache.containsKey(sql))
		{
			nameIndexMap = nameIndexCache.get(sql);
			parsedSql = parsedSqlCache.get(sql);
		} else
		{
			nameIndexMap = new HashMap<String, List<Integer>>();
			parsedSql = parseNamedSql(sql, nameIndexMap);
			
			nameIndexCache.put(sql, nameIndexMap);
			parsedSqlCache.put(sql, parsedSql);
		}
		ps = conn.prepareStatement(parsedSql);
	}
	
	/**
	 * Returns the indexes for a parameter.
	 * 
	 * param name
	 *            parameter name
	 * @return parameter indexes
	 * @throws IllegalArgumentException
	 *             if the parameter does not exist
	 */
	private List<Integer> getIndexes(String name) throws Exception
	{
		List<Integer> indexes = nameIndexMap.get(name);
		if (indexes == null)
		{
			throw new IllegalArgumentException("Parameter not found: " + name);
		}
		return indexes;
	}
	
	/**
	 * Parses a sql with named parameters. The parameter-index mappings are put
	 * into the map, and the parsed sql is returned.
	 * 
	 * param sql
	 *            sql with named parameters
	 * @return the parsed sql
	 */
	private static String parseNamedSql(String sql,
			Map<String, List<Integer>> nameIndexMap)
	{
		int length = sql.length();
		StringBuffer parsedSql = new StringBuffer(length);
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;
		int index = 1;
		for (int i = 0; i < length; i++)
		{
			char c = sql.charAt(i);
			if (inSingleQuote)
			{
				if (c == '\'')
				{
					inSingleQuote = false;
				}
			} else if (inDoubleQuote)
			{
				if (c == '"')
				{
					inDoubleQuote = false;
				}
			} else
			{
				if (c == '\'')
				{
					inSingleQuote = true;
				} else if (c == '"')
				{
					inDoubleQuote = true;
				} else if (c == ':' && i + 1 < length
						&& Character.isJavaIdentifierStart(sql.charAt(i + 1)))
				{
					int j = i + 2;
					while (j < length
							&& Character.isJavaIdentifierPart(sql.charAt(j)))
					{
						j++;
					}
					String name = sql.substring(i + 1, j);
					c = '?'; // replace the parameter with a question mark
					i += name.length(); // skip past the end if the parameter
					List<Integer> indexList = nameIndexMap.get(name);
					if (indexList == null)
					{
						indexList = new LinkedList<Integer>();
						nameIndexMap.put(name, indexList);
					}
					indexList.add(index);
					index++;
				}
			}
			parsedSql.append(c);
		}
		
		return parsedSql.toString();
	}
	
	public void setArray(String name, Array value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setArray(index, value);
		}
	}
	
	public void setAsciiStream(String name, InputStream value)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setAsciiStream(index, value);
		}
	}
	
	public void setAsciiStream(String name, InputStream value, int length)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setAsciiStream(index, value, length);
		}
	}
	
	public void setBigDecimal(String name, BigDecimal value)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setBigDecimal(index, value);
		}
	}
	
	public void setBinaryStream(String name, InputStream value)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setBinaryStream(index, value);
		}
	}
	
	public void setBinaryStream(String name, InputStream value, int length)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setBinaryStream(index, value, length);
		}
	}
	
	public void setBinaryStream(String name, InputStream value, long length)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setBinaryStream(index, value, length);
		}
	}
	
	public void setBlob(String name, Blob value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setBlob(index, value);
		}
	}
	
	public void setBlob(String name, InputStream value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setBlob(index, value);
		}
	}
	
	public void setBlob(String name, InputStream value, long length)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setBlob(index, value, length);
		}
	}
	
	public void setBoolean(String name, boolean value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setBoolean(index, value);
		}
	}
	
	public void setByte(String name, byte value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setByte(index, value);
		}
	}
	
	public void setBytes(String name, byte[] value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setBytes(index, value);
		}
	}
	
	public void setCharacterStream(String name, Reader value)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setCharacterStream(index, value);
		}
	}
	
	public void setCharacterStream(String name, Reader value, int length)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setCharacterStream(index, value, length);
		}
	}
	
	public void setCharacterStream(String name, Reader value, long length)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setCharacterStream(index, value, length);
		}
	}
	
	public void setClob(String name, Clob value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setClob(index, value);
		}
	}
	
	public void setClob(String name, Reader value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setClob(index, value);
		}
	}
	
	public void setClob(String name, Reader value, long length)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setClob(index, value, length);
		}
	}
	
	public void setDate(String name, Date value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setDate(index, value);
		}
	}
	
	public void setDate(String name, Date value, Calendar cal)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setDate(index, value, cal);
		}
	}
	
	public void setDouble(String name, double value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setDouble(index, value);
		}
	}
	
	public void setFloat(String name, float value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setFloat(index, value);
		}
	}
	
	public void setInt(String name, int value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setInt(index, value);
		}
	}
	
	public void setLong(String name, long value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setLong(index, value);
		}
	}
	
	public void setNCharacterStream(String name, Reader value)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setNCharacterStream(index, value);
		}
	}
	
	public void setNCharacterStream(String name, Reader value, long length)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setNCharacterStream(index, value, length);
		}
	}
	
	public void setNClob(String name, NClob value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setNClob(index, value);
		}
	}
	
	public void setNClob(String name, Reader value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setNClob(index, value);
		}
	}
	
	public void setNClob(String name, Reader value, long length)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setNClob(index, value, length);
		}
	}
	
	public void setNString(String name, String value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setNString(index, value);
		}
	}
	
	public void setNull(String name, int sqlType) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setNull(index, sqlType);
		}
	}
	
	public void setObject(String name, Object value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setObject(index, value);
		}
	}
	
	public void setObject(String name, Object value, int targetSqlType)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setObject(index, value, targetSqlType);
		}
	}
	
	public void setObject(String name, Object value, int targetSqlType,
			int scaleOrLength) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setObject(index, value, targetSqlType, scaleOrLength);
		}
	}
	
	public void setRef(String name, Ref value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setRef(index, value);
		}
	}
	
	public void setRowId(String name, RowId value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setRowId(index, value);
		}
	}
	
	public void setShort(String name, short value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setShort(index, value);
		}
	}
	
	public void setSQLXML(String name, SQLXML value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setSQLXML(index, value);
		}
	}
	
	public void setString(String name, String value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setString(index, value);
		}
	}
	
	public void setTime(String name, Time value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setTime(index, value);
		}
	}
	
	public void setTime(String name, Time value, Calendar cal)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setTime(index, value, cal);
		}
	}
	
	public void setTimestamp(String name, Timestamp value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setTimestamp(index, value);
		}
	}
	
	public void setTimestamp(String name, Timestamp value, Calendar cal)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setTimestamp(index, value, cal);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setUnicodeStream(String name, InputStream value, int length)
			throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setUnicodeStream(index, value, length);
		}
	}
	
	public void setURL(String name, URL value) throws Exception
	{
		for (Integer index : getIndexes(name))
		{
			ps.setURL(index, value);
		}
	}
}
