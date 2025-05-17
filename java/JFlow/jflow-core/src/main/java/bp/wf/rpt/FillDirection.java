package bp.wf.rpt;

public enum FillDirection {
    Vertical(1),

    Horizontal(2);

    public static final int SIZE = java.lang.Integer.SIZE;

    private int intValue;
    private static java.util.HashMap<Integer, FillDirection> mappings;

    private static java.util.HashMap<Integer, FillDirection> getMappings() {
        if (mappings == null) {
            synchronized (FillDirection.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<Integer, FillDirection>();
                }
            }
        }
        return mappings;
    }

    private FillDirection(int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static FillDirection forValue(int value) {
        return getMappings().get(value);
    }
}
