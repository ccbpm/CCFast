package bp.port.dingtalk.DingModel;

import java.util.*;

/**
 * 获取部门列表
 */
public class DepartMentList {
    /**
     * 返回码
     */
    private String errcode;

    public final String getErrcode() {
        return errcode;
    }

    public final void setErrcode(String value) {
        errcode = value;
    }

    /**
     * 对返回码的文本描述内容
     */
    private String errmsg;

    public final String getErrmsg() {
        return errmsg;
    }

    public final void setErrmsg(String value) {
        errmsg = value;
    }

    /**
     * 部门列表数据。以部门的order字段从小到大排列
     */
    private ArrayList<DepartMentDetailInfo> department;

    public final ArrayList<DepartMentDetailInfo> getDepartment() {
        return department;
    }

    public final void setDepartment(ArrayList<DepartMentDetailInfo> value) {
        department = value;
    }
}
