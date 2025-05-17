package bp.wf.rpt;

import bp.en.*;

import java.util.*;

/**
 * 报表设计s
 */
public class MapRpts extends EntitiesNoName {

    ///#region 构造

    /**
     * 报表设计s
     */
    public MapRpts() {
    }

    /**
     * 得到它的 Entity
     */
    @Override
    public Entity getNewEntity() {
        return new MapRpt();
    }

    ///#endregion


    ///#region 为了适应自动翻译成java的需要,把实体转换成List.

    /**
     * 转化成 java list,C#不能调用.
     *
     * @return List
     */
    public final java.util.List<MapRpt> ToJavaList() {
        return (java.util.List<MapRpt>) (Object) this;
    }

    /**
     * 转化成list
     *
     * @return List
     */
    public final ArrayList<MapRpt> Tolist() {
        ArrayList<MapRpt> list = new ArrayList<MapRpt>();
        for (int i = 0; i < this.size(); i++) {
            list.add((MapRpt) this.get(i));
        }
        return list;
    }

    ///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
