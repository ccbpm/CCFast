package bp.ccbill;

public enum DictState {
    /**
     * 空白
     */
    None(0),
    /**
     * 草稿
     */
    Draft(1),
    /**
     * 编辑中
     */
    Editing(2),
    /**
     * 归档
     */
    Filing(3),
    /**
     * 删除作废
     */
    Delete(4);

    public static final int SIZE = Integer.SIZE;

    private int intValue;
    private static java.util.HashMap<Integer, DictState> mappings;

    private static java.util.HashMap<Integer, DictState> getMappings() {
        if (mappings == null) {
            synchronized (DictState.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<Integer, DictState>();
                }
            }
        }
        return mappings;
    }

    private DictState(int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static DictState forValue(int value) {
        return getMappings().get(value);
    }
}
