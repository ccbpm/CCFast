package bp.en;
/**
  控件类型
*/
public enum UIContralType
{
	/**
	 文本框
	*/
	TB(0),
	/**
	 下拉框
	*/
	DDL(1),
	/**
	 CheckBok
	*/
	CheckBok(2),
	/**
	 单选择按钮
	*/
	RadioBtn(3),
	/**
	 地图定位
	*/
	MapPin(4),
	/**
	 录音控件
	*/
	MicHot(5),
	/**
	 附件展示控件
	*/
	AthShow(6),
	/**
	 手机拍照控件
	*/
	MobilePhoto(7),
	/**
	 手写签名版
	*/
	HandWriting(8),
	/**
	 超链接
	*/
	HyperLink(9),
	/**
	 文本
	*/
	Lab(10),
	/**
	 图片
	*/
	FrmImg(11),
	/**
	 图片附件
	*/
	FrmImgAth(12),
	/**
	 身份证号
	*/
	IDCard(13),
	/**
	 签批组件
	*/
	SignCheck(14),
	/**
	 评论组件
	*/
	FlowBBS(15),
	/**
	 系统定位
	*/
	Fixed(16),
	/**
	 * 公文正文组件
	 */
	GovDocFile(110),
	/**
	 发文字号
	*/
	DocWord(17),
	/**
	 收文字号
	*/
	DocWordReceive(170),
	/**
	 按钮
	 */
	Btn(18),
	/**
	 流程进度图
	*/
	JobSchedule(50),
	/**
	 大块文本Html(说明性文字)
	*/
	BigText(60),
	/**
	 * 附件表格
	 */
	AthTable(600),
	/**
	 评分
	*/
	Score(101);



	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, UIContralType> mappings;
	private static java.util.HashMap<Integer, UIContralType> getMappings()  {
		if (mappings == null)
		{
			synchronized (UIContralType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, UIContralType>();
				}
			}
		}
		return mappings;
	}

	private UIContralType(int value)
	{intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static UIContralType forValue(int value)
	{
		return getMappings().get(value);
	}
}
