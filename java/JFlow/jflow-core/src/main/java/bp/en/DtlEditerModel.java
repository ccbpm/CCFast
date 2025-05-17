package bp.en;

import bp.wf.BlockModel;

/**
 * 从表的编辑模式
 */
public enum DtlEditerModel {

    //批量编辑
    DtlBatch(0),
    //查询编辑模式
    DtlSearch(1),

    //自定义URL
    DtlURL(2),

    //在EnOnly显示查询
    DtlBatchEnonly(3),

    //在EnOnly
    DtlSearchEnonly(4),

    //
    DtlURLEnonly(5);

    public static final int SIZE = java.lang.Integer.SIZE;

    private int intValue;
    private static java.util.HashMap<Integer, DtlEditerModel> mappings;

    private static java.util.HashMap<Integer, DtlEditerModel> getMappings() {
        if (mappings == null) {
            synchronized (BlockModel.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<Integer, DtlEditerModel>();
                }
            }
        }
        return mappings;
    }

    private DtlEditerModel(int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static DtlEditerModel forValue(int value) {
        return getMappings().get(value);
    }
}
