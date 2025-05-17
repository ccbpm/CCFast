package bp.App.ShuCai;

import bp.en.EntitiesNoName;
import bp.en.Entity;

import java.util.ArrayList;

public class HeChaQiShus extends EntitiesNoName {
    ///#region 构造
    /**
     映射基础s
     */
    public HeChaQiShus()
    {
    }
    /**
     得到它的 Entity
     */
    @Override
    public Entity getNewEntity()
    {
        return new HeChaQiShu();
    }

    ///#endregion


    ///#region 为了适应自动翻译成java的需要,把实体转换成List.
    /**
     转化成 java list,C#不能调用.

     @return List
     */
    public final java.util.List<HeChaQiShu> ToJavaList()
    {
        return (java.util.List<HeChaQiShu>)(Object)this;
    }
    /**
     转化成list

     @return List
     */
    public final ArrayList<HeChaQiShu> Tolist()
    {
        ArrayList<HeChaQiShu> list = new ArrayList<HeChaQiShu>();
        for (int i = 0; i < this.size(); i++)
        {
            list.add((HeChaQiShu)this.get(i));
        }
        return list;
    }
}
