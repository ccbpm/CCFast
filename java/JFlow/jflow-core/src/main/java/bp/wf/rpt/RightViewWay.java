package bp.wf.rpt;

/**
 * 报表查看权限控制方式
 */
public enum RightViewWay {
    /**
     * 任何人都可以查看
     */
    AnyOne,
    /**
     * 按照组织结构权限
     */
    ByPort,
    /**
     * 按照SQL控制
     */
    BySQL;

    public static final int SIZE = java.lang.Integer.SIZE;

    public int getValue() {
        return this.ordinal();
    }

    public static RightViewWay forValue(int value) {
        return values()[value];
    }
}
