package bp.wf.port.admin2group;

import bp.en.*;

import java.util.*;

/**
 * 组织管理员s
 */
public class OAFlowSorts extends EntitiesMM {

    ///#region 构造

    /**
     * 组织s
     */
    public OAFlowSorts() {
    }

    /**
     * 得到它的 Entity
     */
    @Override
    public Entity getNewEntity() {
        return new OAFlowSort();
    }

    ///#endregion


    ///#region 为了适应自动翻译成java的需要,把实体转换成List.

    /**
     * 转化成 java list,C#不能调用.
     *
     * @return List
     */
    public final java.util.List<OAFlowSort> ToJavaList() {
        return (java.util.List<OAFlowSort>) (Object) this;
    }

    /**
     * 转化成list
     *
     * @return List
     */
    public final ArrayList<OAFlowSort> Tolist() {
        ArrayList<OAFlowSort> list = new ArrayList<OAFlowSort>();
        for (int i = 0; i < this.size(); i++) {
            list.add((OAFlowSort) this.get(i));
        }
        return list;
    }

    ///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
