package bp.tools;
import java.io.File;
import java.io.FilenameFilter;

public class ImageFilter implements FilenameFilter
{
	
	public boolean isGif(String file)
	{
		if (file.toLowerCase().endsWith(".gif"))
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	public boolean isJpg(String file)
	{
		if (file.toLowerCase().endsWith(".jpg"))
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	public boolean isPng(String file)
	{
		if (file.toLowerCase().endsWith(".png"))
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	@Override
	public boolean accept(File dir, String fname)
	{
		return (isGif(fname) || isJpg(fname) || isPng(fname));
	}
}
