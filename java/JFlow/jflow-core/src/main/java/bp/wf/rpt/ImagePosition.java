package bp.wf.rpt;


/**
 * 图片位置
 * <p>
 * 使用说明：
 * 　建议先定义一个WaterImage实例
 * 　然后利用实例的属性，去匹配需要进行操作的参数
 * 　然后定义一个WaterImageManage实例
 * 　利用WaterImageManage实例进行DrawImage（），印图片水印
 * 　DrawWords（）印文字水印
 */
public enum ImagePosition {
    LeftTop, //左上
    LeftBottom, //左下
    RightTop, //右上
    RigthBottom, //右下
    TopMiddle, //顶部居中
    BottomMiddle, //底部居中
    Center; //中心

    public static final int SIZE = java.lang.Integer.SIZE;

    public int getValue() {
        return this.ordinal();
    }

    public static ImagePosition forValue(int value) {
        return values()[value];
    }
}
