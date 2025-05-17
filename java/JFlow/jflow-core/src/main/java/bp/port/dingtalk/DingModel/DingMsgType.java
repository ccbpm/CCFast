package bp.port.dingtalk.DingModel;

/**
 * 钉钉消息类型
 */
public enum DingMsgType {
    /**
     * 文本消息
     */
    text,
    /**
     * 声音，vido
     */
    voice,
    /**
     * 图片消息
     */
    image,
    /**
     * 文件消息
     */
    file,
    /**
     * 超链接消息
     */
    link,
    /**
     * OA消息
     */
    OA;

    public static final int SIZE = java.lang.Integer.SIZE;

    public int getValue() {
        return this.ordinal();
    }

    public static DingMsgType forValue(int value) {
        return values()[value];
    }
}
