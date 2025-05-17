package bp.en;


import java.io.Serializable;

/**
 属性
*/
public class AttrFile implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String FileNo = null;
	public String FileName = null;
	public AttrFile(String fileno, String filename)
	{
		this.FileNo = fileno;
		this.FileName = filename;
	}
	public AttrFile()throws Exception
	{
	}
}
