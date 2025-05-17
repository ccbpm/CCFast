package bp.port.dingtalk.DingModel;

import java.util.List;

/**
 * 获取指定角色的员工列表的返回值
 *
 * @author: scott
 * @date: 2024年07月26日 16:02
 */
public class DingRoleUserPageVo {
    private String hasMore;
    private List<DingRoleUserList> list;

    public String getHasMore() {
        return hasMore;
    }

    public void setHasMore(String hasMore) {
        this.hasMore = hasMore;
    }

    public List<DingRoleUserList> getList() {
        return list;
    }

    public void setList(List<DingRoleUserList> list) {
        this.list = list;
    }
}
