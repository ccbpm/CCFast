package bp.tools;


import bp.da.DBAccess;
import org.apache.commons.codec.binary.Base64;
import bp.difference.ContextHolderUtils;
import bp.tools.Rand;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Verify
{

    /**
     随机码认证

     param code 生成认证长度
     */
    public static String DrawImage(int code, String sessionName, String errorSign, String codeSign, String userNo) throws Exception {
        String str = Rand.Number(5);

        //ContextHolderUtils.addCookie(sessionName, Rand.GetMd5Str(str));
        //HttpContext.Current.Session[sessionName] = str;

        String atPara = DBAccess.RunSQLReturnString("SELECT AtPara FROM WF_Emp WHERE No='" + userNo + "'");
        bp.da.AtPara ap = new bp.da.AtPara(atPara);
        ap.SetVal(codeSign, Encodes.encodeBase64(MD5Utill.MD5Encode(str, "UTF8").replace("+", "%2B")));

        DBAccess.RunSQL("UPDATE WF_Emp SET AtPara='" + ap.GenerAtParaStrs() + "' WHERE No='" + userNo + "'");

        return CreateImages(str);
    }
    /**
     /// 生成验证图片
     ///
     /// param checkCode 验证字符
     */
    private static String CreateImages(String checkCode) throws IOException {
        StringBuffer sb = new StringBuffer();
        int iwidth = 80;
        // 1.创建空白图片
        BufferedImage image = new BufferedImage(iwidth, 40, BufferedImage.TYPE_INT_RGB);
        // 2.获取图片画笔
        Graphics g =  image.getGraphics();
        // 3.设置画笔颜色
        g.setColor(Color.LIGHT_GRAY);
        // 4.绘制矩形背景
        g.fillRect(0, 0, iwidth, 40);

        // 5.画随机字符
        java.util.Random rand = new java.util.Random();

        //输出不同字体和颜色的验证码字符
        for (int i = 0; i < checkCode.length(); i++)
        {
            char c = checkCode.charAt(i);
            // 设置随机颜色
            g.setColor(getRandomColor());
            // 设置字体大小
            g.setFont(new Font(null, Font.BOLD + Font.ITALIC, 30));
            // 画字符
            int ii = 4;
            if ((i + 1) % 2 == 0)
            {
                ii = 2;
            }
            g.drawString(c + "", i * iwidth / checkCode.length(), 40 * 2 / 3);
            // 记录字符
            sb.append(c);

        }
        // 6.画干扰线
        for (int i = 0; i < checkCode.length(); i++) {
            // 设置随机颜色
            g.setColor(getRandomColor());
            // 随机画线
            g.drawLine(rand.nextInt(iwidth), rand.nextInt(40), rand.nextInt(iwidth), rand.nextInt(40));
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        Base64 base = new Base64();
        String base64 = base.encodeToString(stream.toByteArray());
        stream.close();
        return "data:image/png;base64," + base64;

    }

    /**
     * 随机取色
     */
    public static Color getRandomColor() {
        java.util.Random ran = new java.util.Random();
        return new Color(ran.nextInt(256), ran.nextInt(256), ran.nextInt(256));
    }

}