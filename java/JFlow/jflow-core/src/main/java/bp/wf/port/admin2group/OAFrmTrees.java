package bp.wf.port.admin2group;

import bp.en.*;

import java.util.*;

/**
 * 组织管理员s
 */
public class OAFrmTrees extends EntitiesMyPK {

    ///#region 构造

    /**
     * 组织s
     */
    public OAFrmTrees() {
    }

    /**
     * 得到它的 Entity
     */
    @Override
    public Entity getNewEntity() {
        return new OAFrmTree();
    }

    ///#endregion


    ///#region 为了适应自动翻译成java的需要,把实体转换成List.

    /**
     * 转化成 java list,C#不能调用.
     *
     * @return List
     */
    public final java.util.List<OAFrmTree> ToJavaList() {
        return (java.util.List<OAFrmTree>) (Object) this;
    }

    /**
     * 转化成list
     *
     * @return List
     */
    public final ArrayList<OAFrmTree> Tolist() {
        ArrayList<OAFrmTree> list = new ArrayList<OAFrmTree>();
        for (int i = 0; i < this.size(); i++) {
            list.add((OAFrmTree) this.get(i));
        }
        return list;
    }

    ///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
