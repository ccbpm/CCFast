package bp.App.ShuCai;

import bp.en.EntitiesNoName;
import bp.en.Entity;

import java.util.ArrayList;

public class QuXians extends EntitiesNoName {

    ///#region 构造
    /**
     映射基础s
     */
    public QuXians()
    {
    }
    /**
     得到它的 Entity
     */
    @Override
    public Entity getNewEntity()
    {
        return new QuXian();
    }

    ///#endregion


    ///#region 为了适应自动翻译成java的需要,把实体转换成List.
    /**
     转化成 java list,C#不能调用.

     @return List
     */
    public final java.util.List<QuXian> ToJavaList()
    {
        return (java.util.List<QuXian>)(Object)this;
    }
    /**
     转化成list

     @return List
     */
    public final ArrayList<QuXian> Tolist()
    {
        ArrayList<QuXian> list = new ArrayList<QuXian>();
        for (int i = 0; i < this.size(); i++)
        {
            list.add((QuXian)this.get(i));
        }
        return list;
    }
}
