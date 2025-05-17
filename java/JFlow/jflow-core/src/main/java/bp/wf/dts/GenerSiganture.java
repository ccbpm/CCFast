package bp.wf.dts;

import bp.difference.SystemConfig;
import bp.port.*;
import bp.en.*;


import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;

import javax.imageio.ImageIO;

/**
 Method 的摘要说明
 */
public class GenerSiganture extends Method
{
	/**
	 不带有参数的方法
	 */
	public GenerSiganture() throws Exception
	{
		this.Title = "为没有设置数字签名的用户设置默认的数字签名";
		this.Help = "此功能需要用户对 " + SystemConfig.getPathOfDataUser() + "\\Siganture\\ 有读写权限，否则会执行失败。";
	}
	/**
	 设置执行变量

	 @return
	 */
	@Override
	public void Init()  {
		//this.Warning = "您确定要执行吗？";
		//HisAttrs.AddTBString("P1", null, "原密码", true, false, 0, 10, 10);
		//HisAttrs.AddTBString("P2", null, "新密码", true, false, 0, 10, 10);
		//HisAttrs.AddTBString("P3", null, "确认", true, false, 0, 10, 10);
	}
	/**
	 当前的操纵员是否可以执行这个方法
	 */
	@Override
	public boolean getIsCanDo()  {
		return true;
	}
	/**
	 执行

	 @return 返回执行结果
	 */
	@Override
	public Object Do() throws Exception
	{
		try
		{
			bp.port.Emps emps = new Emps();
			emps.RetrieveAllFromDBSource();
			String path = SystemConfig.getPathOfDataUser() + "/Siganture/T.jpg";
			String fontName = "宋体";
			String empOKs = "";
			String empErrs = "";
			for (Emp emp : emps.ToJavaList())
			{
				GenerIt(emp.getNo(),emp.getName());
			}
			return "执行成功...";
		}
		catch (RuntimeException ex)
		{
			return "执行失败，请确认对 " + SystemConfig.getPathOfDataUser() + "/Siganture/ 目录有访问权限？异常信息:" + ex.getMessage();
		}
	}

	public static void GenerIt(String empID, String empName) throws IOException {
		String dataUserPath =  SystemConfig.getPathOfDataUser();
		if(SystemConfig.isJarRun())
			dataUserPath = SystemConfig.getPhysicalPath()+"DataUser";
		String path = dataUserPath + "/Siganture/T.jpg";
		String pathMe = dataUserPath + "/Siganture/" + empID + ".jpg";
		if ((new File(pathMe)).isFile())
			return;

		Files.copy(Paths.get(dataUserPath + "/Siganture/Templete.jpg"), Paths.get(path), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

		paintWaterMarkPhoto(pathMe,empName,path);


		Files.copy(Paths.get(pathMe), Paths.get(dataUserPath + "/Siganture/" +empName + ".jpg"), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
	}
	private static void paintWaterMarkPhoto(String targerImagePath,String words,String srcImagePath) {
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

			// 透明度
			float alpha = 0.25f;
			graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			// 设置颜色和画笔粗细
			graphics2D.setColor(Color.gray);
			graphics2D.setStroke(new BasicStroke(10));
			graphics2D.setFont(new Font("宋体", Font.ITALIC, 15));
			// 绘制图案或文字
			String cont = words;
			int charWidth1 = 8;
			int charWidth2 = 8;
			int halfGap = 12;
			graphics2D.drawString(cont, (srcImage.getWidth(null) - cont.length() * charWidth1) / 2,
					(srcImage.getHeight(null) - (charWidth1 + halfGap)) / 2);

			graphics2D.dispose();

			os = new FileOutputStream(targerImagePath);
			// 生成图片 (可设置 jpg或者png格式)
			ImageIO.write(bufImage, "JPG", os);
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