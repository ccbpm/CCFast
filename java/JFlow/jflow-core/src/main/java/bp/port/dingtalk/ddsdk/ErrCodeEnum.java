package bp.port.dingtalk.ddsdk;


public enum ErrCodeEnum {
    OK(0),
    VoildAccessToken(40014),
    /**
     * 未知
     */
    Unknown(Integer.MAX_VALUE);

    public static final int SIZE = java.lang.Integer.SIZE;

    private int intValue;
    private static java.util.HashMap<Integer, ErrCodeEnum> mappings;

    private static java.util.HashMap<Integer, ErrCodeEnum> getMappings() {
        if (mappings == null) {
            synchronized (ErrCodeEnum.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<Integer, ErrCodeEnum>();
                }
            }
        }
        return mappings;
    }

    private ErrCodeEnum(int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static ErrCodeEnum forValue(int value) {
        return getMappings().get(value);
    }
}
