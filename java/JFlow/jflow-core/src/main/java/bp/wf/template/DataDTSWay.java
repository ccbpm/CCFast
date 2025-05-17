package bp.wf.template;


/**
 * 数据同步方案
 */
public enum DataDTSWay {
    /**
     * 不执行同步
     */
    None,
    /**
     * 任何节点发送后都执行同步
     */
    AnyNode,
    /**
     * 流程结束时
     */
    WhenStopFlow,
    /**
     * 指定的节点发送后
     */
    AfterSpecifiedNode;

    public static final int SIZE = java.lang.Integer.SIZE;

    public int getValue() {
        return this.ordinal();
    }

    public static DataDTSWay forValue(int value) {
        return values()[value];
    }
}
