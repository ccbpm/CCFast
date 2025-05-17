package bp.port.dingtalk.DingModel;

import java.util.List;

/**
 * 角色返回具体信息
 *
 * @author: scott
 * @date: 2024年07月26日 15:57
 */
public class DingRolePageVo {
    private Boolean hasMore;
    private List<DingRoleGroupList> list;

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<DingRoleGroupList> getList() {
        return list;
    }

    public void setList(List<DingRoleGroupList> list) {
        this.list = list;
    }
}
