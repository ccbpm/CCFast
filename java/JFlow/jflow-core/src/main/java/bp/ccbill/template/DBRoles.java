package bp.ccbill.template;

import bp.en.*;

import java.util.*;

/**
 * 台账子流程
 */
public class DBRoles extends EntitiesMyPK {
    /**
     * 台账子流程
     */
    public DBRoles() {
    }

    /**
     * 得到它的 Entity
     */
    @Override
    public Entity getNewEntity() {
        return new DBRole();
    }

    ///#region 为了适应自动翻译成java的需要,把实体转换成List.

    /**
     * 转化成 java list,C#不能调用.
     *
     * @return List
     */
    public final java.util.List<DBRole> ToJavaList() {
        return (java.util.List<DBRole>) (Object) this;
    }

    /**
     * 转化成list
     *
     * @return List
     */
    public final ArrayList<DBRole> Tolist() {
        ArrayList<DBRole> list = new ArrayList<DBRole>();
        for (int i = 0; i < this.size(); i++) {
            list.add((DBRole) this.get(i));
        }
        return list;
    }

    ///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
