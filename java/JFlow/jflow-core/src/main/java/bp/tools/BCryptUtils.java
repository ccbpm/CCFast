package bp.tools;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptUtils {
    /**
     * 加密
     */
    public static String hashpw(String str) {
        return BCrypt.hashpw(str, BCrypt.gensalt(12));
    }

    /**
     * 校验
     */
    public static Boolean checkpw(String password, String hashpw) {
        try{
            return BCrypt.checkpw(password, hashpw);
        }catch (Exception e){
            return false;
        }
    }

    public static void main(String[] args) {
        String pwd="123";
        String de=hashpw("123");
         System.out.println("原密码："+pwd);
         System.out.println("加密后："+de);
        System.out.println("验证："+checkpw(pwd, de));
    }
}
