package bp.App.ShuCai;

import bp.en.EntitiesNoName;
import bp.en.Entity;

import java.util.ArrayList;

public class XiangZhens extends EntitiesNoName {

    ///#region 构造
    /**
     映射基础s
     */
    public XiangZhens()
    {
    }
    /**
     得到它的 Entity
     */
    @Override
    public Entity getNewEntity()
    {
        return new XiangZhen();
    }

    ///#endregion


    ///#region 为了适应自动翻译成java的需要,把实体转换成List.
    /**
     转化成 java list,C#不能调用.

     @return List
     */
    public final java.util.List<XiangZhen> ToJavaList()
    {
        return (java.util.List<XiangZhen>)(Object)this;
    }
    /**
     转化成list

     @return List
     */
    public final ArrayList<XiangZhen> Tolist()
    {
        ArrayList<XiangZhen> list = new ArrayList<XiangZhen>();
        for (int i = 0; i < this.size(); i++)
        {
            list.add((XiangZhen)this.get(i));
        }
        return list;
    }
}
