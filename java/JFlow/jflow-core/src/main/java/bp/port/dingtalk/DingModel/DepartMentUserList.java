package bp.port.dingtalk.DingModel;

import java.util.List;

/**
 * 部门人员列表
 *
 * @author: scott
 * @date: 2024年07月26日 15:26
 */
public class DepartMentUserList {
    //返回码
    private String errcode;
    //对返回码的文本描述内容
    private String errmsg;
    //在分页查询时返回，代表是否还有下一页更多数据
    private String hasMore;
    private List<DepartMentUserInfo> userlist;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getHasMore() {
        return hasMore;
    }

    public void setHasMore(String hasMore) {
        this.hasMore = hasMore;
    }

    public List<DepartMentUserInfo> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<DepartMentUserInfo> userlist) {
        this.userlist = userlist;
    }
}
