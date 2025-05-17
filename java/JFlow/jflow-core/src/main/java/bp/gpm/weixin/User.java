package bp.gpm.weixin;

import bp.da.*;
import bp.tools.*;
import bp.*;

import java.util.*;

/**
 * 简写的User
 */
public class User {
    private int errcode;

    public final int getErrCode() {
        return errcode;
    }

    public final void setErrCode(int value) {
        errcode = value;
    }

    private String errmsg;

    public final String getErrMsg() {
        return errmsg;
    }

    public final void setErrMsg(String value) {
        errmsg = value;
    }

    private String userid;

    public final String getUserId() {
        return userid;
    }

    public final void setUserId(String value) {
        userid = value;
    }
}
