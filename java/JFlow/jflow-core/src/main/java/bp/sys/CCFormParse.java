package bp.sys;

/** 
 解析控件并保存.
*/
public class CCFormParse
{
	/** 
	 保存元素
	 
	 @param fk_mapdata 表单ID
	 @param eleType 元素类型
	 @param ctrlID 控件ID
	 @param x 位置
	 @param y 位置
	 @param h 高度
	 @param w 宽度
	*/
	public static void SaveMapFrame(String fk_mapdata, String eleType, String ctrlID, float x, float y, float h, float w) throws Exception {
		MapFrame en = new MapFrame();
		en.setMyPK(ctrlID);
		int i = en.RetrieveFromDBSources();
		en.setEleType(eleType);
		en.setFrmID(ctrlID);

		en.setW(w);
		en.setH(h);

		if (i == 0)
		{
			en.Insert();
		}
		else
		{
			en.Update();
		}
	}

	/** 
	 保存一个rb
	 
	 @param fk_mapdata 表单ID
	 @param ctrlID 控件ID
	 @param x 位置x
	 @param y 位置y
	*/
	public static String SaveFrmRadioButton(String fk_mapdata, String ctrlID, float x, float y) throws Exception {
		FrmRB en = new FrmRB();
		en.setMyPK(fk_mapdata + "_" + ctrlID);
		int i = en.RetrieveFromDBSources();
		if (i == 0)
		{
			return null;
		}

		en.setFrmID(fk_mapdata);

		en.Update();
		return en.getKeyOfEn();
	}
	/** 
	 保存图片
	 
	 @param fk_mapdata 表单ID
	 @param ctrlID 空间ID
	 @param x 位置x
	 @param y 位置y
	 @param h 高度h
	 @param w 宽度w
	*/
	public static void SaveAthImg(String fk_mapdata, String ctrlID, float x, float y, float h, float w) throws Exception {
		FrmImgAth en = new FrmImgAth();
		en.setMyPK(fk_mapdata + "_" + ctrlID);
		en.setFrmID(fk_mapdata);
		en.setCtrlID(ctrlID);
		en.RetrieveFromDBSources();

		//en.X = x;
		//en.Y = y;
		en.setW(w);
		en.setH(h);
		en.Update();
	}
	/** 
	 保存多附件
	 
	 @param fk_mapdata 表单ID
	 @param ctrlID 控件ID
	 @param x 位置x
	 @param y 位置y
	 @param h 高度
	 @param w 宽度
	*/
	public static void SaveAthMulti(String fk_mapdata, String ctrlID, float x, float y, float h, float w) throws Exception {
		FrmAttachment en = new FrmAttachment();
		en.setMyPK(fk_mapdata + "_" + ctrlID);
		en.setFrmID(fk_mapdata);
		en.setNoOfObj(ctrlID);
		en.RetrieveFromDBSources();

		en.setH(h);
		en.Update();
	}
	public static void SaveDtl(String fk_mapdata, String ctrlID, float x, float y, float h, float w) throws Exception {
		MapDtl dtl = new MapDtl();
		dtl.setNo(ctrlID);
		dtl.RetrieveFromDBSources();

		dtl.setFrmID(fk_mapdata);
		dtl.SetValByKey(MapAttrAttr.UIWidth, w);
		dtl.SetValByKey(MapAttrAttr.UIHeight, h);
		dtl.Update();
	}

}
