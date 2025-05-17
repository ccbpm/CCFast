package bp.wf.rpt;

import bp.*;
import bp.wf.*;
import java.util.*;
import java.io.*;

/** 
 装载水印图片的相关信息
*/
public class WaterImage
{
	/** 
	 水文
	*/
	public WaterImage()
	{
	}

	private String m_sourcePicture;
	/** 
	 源图片地址名字(带后缀)
	*/

	public final String getSourcePicture()
	{
		return m_sourcePicture;
	}
	public final void setSourcePicture(String value)
	{
		m_sourcePicture = value;
	}

	private String m_waterImager;
	/** 
	 水印图片名字(带后缀)
	*/
	public final String getWaterPicture()
	{
		return m_waterImager;
	}
	public final void setWaterPicture(String value)
	{
		m_waterImager = value;
	}

	private float m_alpha;
	/** 
	 水印图片文字的透明度
	*/
	public final float getAlpha()
	{
		return m_alpha;
	}
	public final void setAlpha(float value)
	{
		m_alpha = value;
	}

	private ImagePosition m_postition = ImagePosition.values()[0];
	/** 
	 水印图片或文字在图片中的位置
	*/
	public final ImagePosition getPosition()
	{
		return m_postition;
	}
	public final void setPosition(ImagePosition value)
	{
		m_postition = value;
	}

	private String m_words;
	/** 
	 水印文字的内容
	*/
	public final String getWords()
	{
		return m_words;
	}
	public final void setWords(String value)
	{
		m_words = value;
	}

}
