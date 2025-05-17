package bp.wf.port.admin2group;

import bp.en.*;

import java.util.*;

/**
 * 操作员s
 */
// </summary>
public class Users extends EntitiesNoName {

    ///#region 构造方法

    /**
     * 得到它的 Entity
     */
    @Override
    public Entity getNewEntity() {
        return new User();
    }

    /**
     * 操作员s
     */
    public Users() {
    }


    ///#endregion 构造方法


    ///#region 为了适应自动翻译成java的需要,把实体转换成List.

    /**
     * 转化成 java list,C#不能调用.
     *
     * @return List
     */
    public final java.util.List<User> ToJavaList() {
        return (java.util.List<User>) (Object) this;
    }

    /**
     * 转化成list
     *
     * @return List
     */
    public final ArrayList<User> Tolist() {
        ArrayList<User> list = new ArrayList<User>();
        for (int i = 0; i < this.size(); i++) {
            list.add((User) this.get(i));
        }
        return list;
    }

    ///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
