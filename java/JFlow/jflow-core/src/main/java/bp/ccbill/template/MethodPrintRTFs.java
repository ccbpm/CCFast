package bp.ccbill.template;

import bp.en.EntitiesNoName;
import bp.en.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 打印RTFs
 */
public class MethodPrintRTFs extends EntitiesNoName {
    /**
     * 连接方法
     */
    public MethodPrintRTFs() {
    }

    /**
     * 得到它的 Entity
     */
    @Override
    public Entity getNewEntity() {
        return new MethodPrintRTF();
    }

    ///#region 为了适应自动翻译成java的需要,把实体转换成List.

    /**
     * 转化成 java list,C#不能调用.
     *
     * @return List
     */
    public final List<MethodLink> ToJavaList() {
        return (List<MethodLink>) (Object) this;
    }

    /**
     * 转化成list
     *
     * @return List
     */
    public final ArrayList<MethodPrintRTF> Tolist() {
        ArrayList<MethodPrintRTF> list = new ArrayList<MethodPrintRTF>();
        for (int i = 0; i < this.size(); i++) {
            list.add((MethodPrintRTF) this.get(i));
        }
        return list;
    }

    ///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
