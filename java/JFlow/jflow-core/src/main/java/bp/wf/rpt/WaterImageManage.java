package bp.wf.rpt;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

/** 
 水印图片的操作管理 Design by Gary Gong From Demetersoft.com
*/
public class WaterImageManage
{
	/** 
	 生成一个新的水印图片制作实例
	*/
	public WaterImageManage()
	{
		//
		// TODO: Add constructor logic here
		//
	}

	/*
	* 
	* 使用说明：
	* 　建议先定义一个WaterImage实例
	* 　然后利用实例的属性，去匹配需要进行操作的参数
	* 　然后定义一个WaterImageManage实例
	* 　利用WaterImageManage实例进行DrawImage（），印图片水印
	* 　DrawWords（）印文字水印
	* 
	-*/

	/** 
	 在图片上添加水印文字
	 
	 @param targerImagePath 源图片文件(文件名，不包括路径)
	 @param words 需要添加到图片上的文字
	 @param srcImagePath 目标图片名称及全路径
	 @return 
	*/
	public final void DrawWords(String targerImagePath,String words,String srcImagePath)
	{
		Integer degree = -15;
		OutputStream os = null;
		try {
			Image srcImage = ImageIO.read(new File(srcImagePath));
			BufferedImage bufImage = new BufferedImage(srcImage.getWidth(null), srcImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
			// 得到画布对象
			Graphics2D graphics2D = bufImage.createGraphics();
			// 设置对线段的锯齿状边缘处理
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(srcImage.getScaledInstance(srcImage.getWidth(null), srcImage.getHeight(null), Image.SCALE_SMOOTH),
					0, 0, null);
			if (null != degree) {
				// 设置水印旋转角度及坐标
				graphics2D.rotate(Math.toRadians(degree), (double) bufImage.getWidth() / 2, (double) bufImage.getHeight() / 2);
			}
			// 透明度
			float alpha = 0.25f;
			graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			// 设置颜色和画笔粗细
			graphics2D.setColor(Color.gray);
			graphics2D.setStroke(new BasicStroke(10));
			graphics2D.setFont(new Font("SimSun", Font.ITALIC, 18));
			// 绘制图案或文字
			String cont = words;
			String dateStr = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
			int charWidth1 = 8;
			int charWidth2 = 8;
			int halfGap = 12;
			graphics2D.drawString(cont, (srcImage.getWidth(null) - cont.length() * charWidth1) / 2,
					(srcImage.getHeight(null) - (charWidth1 + halfGap)) / 2);
			graphics2D.drawString(dateStr, (srcImage.getWidth(null) - dateStr.length() * charWidth2) / 2,
					(srcImage.getHeight(null) + (charWidth2 + halfGap)) / 2);

			graphics2D.dispose();

			os = new FileOutputStream(targerImagePath);
			// 生成图片 (可设置 jpg或者png格式)
			ImageIO.write(bufImage, "png", os);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
