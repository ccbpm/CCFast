package bp.en;

import bp.da.*;
import bp.sys.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.math.*;

/**
 属性集合
*/
public class Attrs extends ArrayList<Attr> implements Serializable
{

	private static final long serialVersionUID = 1L;

	public String currGroupAttrName = "基本信息";

	///关于属性的增加 String
	protected final void AddTBString(String key, String field, Object defaultVal, FieldType _FieldType, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith) {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppString);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIWidth(tbWith);
		attr.setUIIsReadonly(isReadonly);
		attr.setMaxLength(maxLength);
		attr.setMinLength(minLength);
		attr.setMyFieldType(_FieldType);
		this.Add(attr);
	}
	public final void AddTBString(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith)  {
		AddTBString(key, key, defaultVal, FieldType.Normal, desc, uiVisable, isReadonly, minLength, maxLength, tbWith);
	}
	public final void AddTBString(String key, String field, Object defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith) {
		AddTBString(key, field, defaultVal, FieldType.Normal, desc, uiVisable, isReadonly, minLength, maxLength, tbWith);
	}

	public final void AddTBStringDoc(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly) {
		AddTBStringDoc(key, key, defaultVal, desc, uiVisable, isReadonly, 0, 2000, 300, 300);
	}
	public final void AddTBStringDoc(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith, int rows)  {
		AddTBStringDoc(key, key, defaultVal, desc, uiVisable, isReadonly, minLength, maxLength, tbWith, rows);
	}
	public final void AddTBStringDoc(String key, String field, String defaultVal, String desc, boolean uiVisable, boolean isReadonly, int minLength, int maxLength, int tbWith, int rows) {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppString);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIWidth(300);
		attr.setUIIsReadonly(isReadonly);
		attr.setMaxLength(maxLength);
		attr.setMinLength(minLength);
		attr.setMyFieldType(FieldType.Normal);
		attr.setUIHeight(rows);
		this.Add(attr);
	}
	/**
	 增加附件

	 param fileDesc
	 * @
	*/
	public final void AddMyFile(String fileDesc) throws Exception {
		this.AddTBString(EntityNoMyFileAttr.MyFileName, null, fileDesc, false, false, 0, 100, 200);
		this.AddTBString(EntityNoMyFileAttr.MyFilePath, null, "MyFilePath", false, false, 0, 100, 200);
		this.AddTBString(EntityNoMyFileAttr.MyFileExt, null, "MyFileExt", false, false, 0, 10, 10);
		//  this.AddTBInt(EntityNoMyFileAttr.MyFileNum, 0, "MyFileNum", false, false);
		this.AddTBInt(EntityNoMyFileAttr.MyFileH, 0, "MyFileH", false, false);
		this.AddTBInt(EntityNoMyFileAttr.MyFileW, 0, "MyFileW", false, false);
		this.AddTBInt("MyFileSize", 0, "MyFileSize", false, false);

		//this.IsHaveFJ = true;
	}

		///  关于属性的增加 String


		///关于属性的增加 Int

	/**
	 增加一个普通的类型。

	 param key 键
	 param _Field 字段
	 param defaultVal 默认值
	 param desc 描述
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 * @
	*/
	public final void AddTBInt(String key, String _Field, int defaultVal, String desc, boolean uiVisable, boolean isReadonly) {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(_Field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppInt);
		attr.setMyFieldType(FieldType.Normal);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIIsReadonly(isReadonly);
		this.Add(attr);
	}
	/**
	 增加一个普通的类型。字段值与属性相同。

	 param key 键
	 param defaultVal 默认值
	 param desc 描述
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 * @
	*/
	public final void AddTBInt(String key, int defaultVal, String desc, boolean uiVisable, boolean isReadonly) {
		this.AddTBInt(key, key, defaultVal, desc, uiVisable, isReadonly);
	}
	public final void AddBoolen(String key, boolean defaultVal, String desc) {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(key);

		if (defaultVal)
		{
			attr.setDefaultVal(1);
		}
		else
		{
			attr.setDefaultVal(0);
		}

		attr.setMyDataType(DataType.AppBoolean);
		attr.setDesc(desc);
		attr.setUIContralType(UIContralType.CheckBok);
		attr.setUIIsReadonly(true);
		attr.setUIVisible(true);
		this.Add(attr);
	}

		///  关于属性的增加 Int


		///关于属性的增加 Float类型
	public final void AddTBFloat(String key, String _Field, float defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(_Field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppFloat);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIIsReadonly(isReadonly);
		this.Add(attr);
	}
	public final void AddTBFloat(String key, float defaultVal, String desc, boolean uiVisable, boolean isReadonly) {
		this.AddTBFloat(key, key, defaultVal, desc, uiVisable, isReadonly);
	}

		///  关于属性的增加 Float


		///Decimal类型
	public final void AddTBDecimal(String key, String _Field, BigDecimal defaultVal, String desc, boolean uiVisable, boolean isReadonly) {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(_Field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppDouble);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIIsReadonly(isReadonly);
		this.Add(attr);
	}
	public final void AddTBDecimal(String key, BigDecimal defaultVal, String desc, boolean uiVisable, boolean isReadonly) {
		this.AddTBDecimal(key, key, defaultVal, desc, uiVisable, isReadonly);
	}

		///


		///日期
	public final void AddTBDate(String key, String field, String defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppDate);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIIsReadonly(isReadonly);
		attr.setMaxLength(20);
		this.Add(attr);
	}

	public final void AddTBDate(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		this.AddTBDate(key, key, defaultVal, desc, uiVisable, isReadonly);
	}
	/**
	 增加日期类型的控健(默认日期是当前日期)

	 param key key
	 param desc desc
	 param uiVisable uiVisable
	 param isReadonly isReadonly
	 * @
	*/
	public final void AddTBDate(String key, String desc, boolean uiVisable, boolean isReadonly) {
		this.AddTBDate(key, key, DataType.dateToStr(new Date(), DataType.getSysDateFormat()), desc, uiVisable, isReadonly);
	}



	///日期时间类型。
	/**
	 增加日期类型的控健

	 param key 健值
	 param defaultVal 默认值
	 param desc 描述
	 param uiVisable 是不是可见
	 param isReadonly 是不是只读
	 * @
	*/
	public final void AddTBDateTime(String key, String field, String defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppDateTime);
		attr.setDesc(desc);
		attr.setUIVisible(uiVisable);
		attr.setUIIsReadonly(isReadonly);
		attr.setMaxLength(30);
		attr.setMinLength(0);
		attr.setUIWidth(100);
		this.Add(attr);
	}
	public final void AddTBDateTime(String key, String defaultVal, String desc, boolean uiVisable, boolean isReadonly)  {
		this.AddTBDateTime(key, key, defaultVal, desc, uiVisable, isReadonly);
	}
	public final void AddTBDateTime(String key, String desc, boolean uiVisable, boolean isReadonly)  {
		this.AddTBDateTime(key, key, DataType.dateToStr(new Date(), DataType.getSysDateTimeFormat()), desc, uiVisable, isReadonly);
	}


		///于帮定自定义,枚举类型有关系的操作。
	public final void AddDDLSysEnum(String key, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, String sysEnumKey)  {
		this.AddDDLSysEnum(key, key, defaultVal, desc, isUIVisable, isUIEnable, sysEnumKey, null);
	}
	/**
	 /

	 param key
	 param field
	 param defaultVal
	 param desc
	 param isUIVisable
	 param isUIEnable
	 param sysEnumKey
	 * @
	*/
	public final void AddDDLSysEnum(String key, String field, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, String sysEnumKey)  {
		this.AddDDLSysEnum(key, field, defaultVal, desc, isUIVisable, isUIEnable, sysEnumKey, null);
	}
	/**
	 自定义枚举类型

	 param key 键
	 param field 字段
	 param defaultVal 默认
	 param desc 描述
	 param sysEnumKey Key
	 * @
	*/
	public final void AddDDLSysEnum(String key, String field, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, String sysEnumKey, String cfgVal)  {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppInt);
		attr.setMyFieldType(FieldType.Enum);
		attr.setDesc(desc);
		attr.setUIContralType(UIContralType.DDL);
		attr.setUIBindKey(sysEnumKey);
		attr.UITag = cfgVal;
		attr.setUIVisible(isUIVisable);
		attr.setUIIsReadonly(!isUIEnable);
		this.Add(attr);
	}
	/**
	 自定义枚举类型

	 param key 键
	 param defaultVal 默认
	 param desc 描述
	 param sysEnumKey Key
	 * @
	*/
	public final void AddDDLSysEnum(String key, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable, String sysEnumKey, String cfgVals){
		AddDDLSysEnum(key, key, defaultVal, desc, isUIVisable, isUIEnable, sysEnumKey, cfgVals);
	}
	public final void AddDDLSysEnum(String key, int defaultVal, String desc, boolean isUIVisable, boolean isUIEnable)  {
		AddDDLSysEnum(key, key, defaultVal, desc, isUIVisable, isUIEnable, key);
	}

		///


		///entities
	/**
	 于实体有关系的操作。

	 param key 健值
	 param field 字段
	 param defaultVal 默认值
	 param dataType DataType类型
	 param desc 描述
	 param ens 实体集合
	 param refKey 关联的建
	 param refText 关联的Text
	 * @
	*/
	public final void AddDDLEntities(String key, String field, Object defaultVal, int dataType, FieldType _fildType, String desc, Entities ens, String refKey, String refText, boolean uiIsEnable) {
		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(field);
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(dataType);
		attr.setMyFieldType(_fildType);

		attr.setDesc(desc);
		attr.setUIContralType(UIContralType.DDL);
		attr.setUIBindKey(ens.toString());
		//attr.getUIBindKey()OfEn = ens.getNewEntity().ToString();
		attr.setHisFKEns(ens);

		attr.setHisFKEns(ens);
		attr.setUIRefKeyText(refText);
		attr.setUIRefKeyValue(refKey);
		attr.setUIIsReadonly(uiIsEnable);
		this.Add(attr, true, false);
	}


		///DDLSQL

	public final void AddDDLSQL(String key, Object defaultVal, String desc, String sql) throws Exception {
		AddDDLSQL(key, defaultVal, desc, sql, true);
	}


	public final void AddDDLSQL(String key, Object defaultVal, String desc, String sql, boolean uiIsEnable) {
		if (defaultVal == null)
		{
			defaultVal = "";
		}

		Attr attr = new Attr();
		attr.setKey(key);
		attr.setField(key);

		if (Integer.class == defaultVal.getClass())
		{
			attr.setDefaultVal(defaultVal);
			attr.setMyDataType(DataType.AppInt);
		}
		else
		{
			attr.setDefaultVal(defaultVal);
			attr.setMyDataType(DataType.AppString);
		}

		attr.setMyFieldType(FieldType.Normal);
		attr.setMaxLength(50);

		attr.setDesc(desc);
		attr.setUIContralType(UIContralType.DDL);

		attr.setUIBindKey(sql);
		attr.setUIIsReadonly(!uiIsEnable);
		this.Add(attr);


		//他的名称列.
		attr = new Attr();
		attr.setKey(key + "Text");
		attr.setField(key + "Text");
		attr.setDefaultVal(defaultVal);
		attr.setMyDataType(DataType.AppString);
		attr.setMyFieldType(FieldType.RefText);
		attr.setMaxLength(200); //最大长度 @李国文
		attr.setDesc(desc);
		attr.setUIContralType(UIContralType.TB);
		//	attr.setUIBindKey( sql;
		attr.setUIIsReadonly(true);
		attr.setUIVisible(false);
		this.Add(attr);
	}

		/// DDLSQL

	public final void AddDDLEntities(String key, String field, Object defaultVal, int dataType, String desc, Entities ens, String refKey, String refText, boolean uiIsEnable)  {
		AddDDLEntities(key, field, defaultVal, dataType, FieldType.FK, desc, ens, refKey, refText, uiIsEnable);
	}
	/**
	 于实体有关系的操作。字段与属性名称相同。

	 param key 健值
	 param defaultVal 默认值
	 param dataType DataType类型
	 param desc 描述
	 param ens 实体集合
	 param refKey 关联的建
	 param refText 关联的Text
	 * @
	*/
	public final void AddDDLEntities(String key, Object defaultVal, int dataType, String desc, Entities ens, String refKey, String refText, boolean uiIsEnable) throws Exception {
		AddDDLEntities(key, key, defaultVal, dataType, desc, ens, refKey, refText, uiIsEnable);
	}

		///


		///entityNoName
	public final void AddDDLEntities(String key, Object defaultVal, String desc, EntitiesNoName ens, boolean uiIsEnable) {
		this.AddDDLEntities(key, key, defaultVal, DataType.AppString, desc, ens, "No", "Name", uiIsEnable);
	}
	public final void AddDDLEntities(String key, String field, Object defaultVal, String desc, EntitiesNoName ens, boolean uiIsEnable) {
		this.AddDDLEntities(key, field, defaultVal, DataType.AppString, desc, ens, "No", "Name", uiIsEnable);
	}

		///


		///EntitiesSimpleTree
	public final void AddDDLEntities(String key, Object defaultVal, String desc, EntitiesTree ens, boolean uiIsEnable)  {
		this.AddDDLEntities(key, key, defaultVal, DataType.AppString, desc, ens, "No", "Name", uiIsEnable);
	}

		///


		///EntitiesOIDName
	public final void AddDDLEntities(String key, Object defaultVal, String desc, EntitiesOIDName ens, boolean uiIsEnable)  {
		this.AddDDLEntities(key, key, defaultVal, DataType.AppInt, desc, ens, "OID", "Name", uiIsEnable);
	}
	public final void AddDDLEntities(String key, String field, Object defaultVal, String desc, EntitiesOIDName ens, boolean uiIsEnable)  {
		this.AddDDLEntities(key, field, defaultVal, DataType.AppInt, desc, ens, "OID", "Name", uiIsEnable);
	}


	public final Attrs Clone() throws Exception {
		Attrs attrs = new Attrs();
		for (Attr attr : this)
		{
			attrs.Add(attr);
		}
		return attrs;
	}
	/**
	 下一个Attr 是否是 Doc 类型.

	 param CurrentKey
	 @return
	*/
	public final Attr NextAttr(String CurrentKey)  {
		int i = this.GetIndexByKey(CurrentKey);

		if (this.size() > i)
		{
			return null;
		}

		return this.get(i + 1) instanceof Attr ? (Attr)this.get(i + 1) : null;
	}
	public final Attr PrvAttr(String CurrentKey)  {
		int i = this.GetIndexByKey(CurrentKey);

		if (this.size() < i)
		{
			return null;
		}

		return this.get(i - 1) instanceof Attr ? (Attr)this.get(i - 1) : null;
	}
	/**
	 是否包含属性key。

	 param key
	 @return
	*/
	public final boolean contains(String key)  {
		for (Attr attr : this)
		{
			if (attr.getKey().equals(key))
			{
				return true;
			}
		}
		return false;
	}
	public final boolean ContainsUpper(String key)  {
		for (Attr attr : this)
		{
			if (attr.getKey().toUpperCase().equals(key.toUpperCase()))
			{
				return true;
			}
		}
		return false;
	}
	/**
	 物理字段Num
	*/
	public final int getConutOfPhysicsFields()
	{
		int i = 0;
		for (Attr attr : this)
		{
			if (attr.getMyFieldType() != FieldType.RefText)
			{
				i++;
			}
		}
		return i;
	}


	/**
	 通过Key ， 取出他的Index.

	 param key Key
	 @return index
	*/
	public final int GetIndexByKey(String key)  {
		for (int i = 0 ; i < this.size() ; i++)
		{
			if (this.get(i).getKey().equals(key))
			{
				return i;
			}
		}
		return -1;
	}


	public final Attr GetAttrByKey(String key)  {
		for (Attr item : this)
		{
			if (item.getKey().equals(key))
			{
				return item;
			}
		}
		return null;
	}
	public final Attr GetAttrByKeyOfEn(String Field)  {
		for (Attr item : this)
		{
			if (DataType.IsNullOrEmpty(item.getField())==false && item.getField().equals(Field))
			{
				return item;
			}
		}
		return null;
	}
	/**
	 属性集合

	 param cfgKeys 约定的字段格式
	 * @
	*/
	public Attrs(String cfgKeys) throws Exception {

		/**
		 *  根据约定的格式的字符串生成集合.
			 @Key=MyStringField;Name=我的中文字段;Type=String;DefVal=默认值;AppType=Normal;IsLine=1
			 @Key=MyIntField;Name=我的数字字段;Type=Int;DefVal=12;AppType=Normal;IsLine=false
			 @Key=MyFloatField;Name=我的浮点字段;Type=Float;DefVal=12.0;AppType=Normal;IsLine=false
			 @Key=MyEnumField;Name=我的枚举字段;Type=Int;DefVal=0;AppType=Enum;IsLine=false;BindKey=[0=Yes,1=No,2=Unhnow]
			 @Key=MyFKField;Name=我的外键字段;Type=String;DefVal=01;AppType=FK;IsLine=false;BindKey=bp.port.Depts
		 * */

		String[] strs = cfgKeys.split("[@]", -1);
		for (String str : strs)
		{
			AtPara ap = new AtPara(str.replace(";", "@"));

			FieldTypeS ft = FieldTypeS.forValue(ap.GetValIntByKey("AppType"));
			switch (ft)
			{
				case Enum:
					this.AddDDLSysEnum(ap.GetValStrByKey("Key"), ap.GetValStrByKey("Key"), ap.GetValIntByKey("DefVal"), ap.GetValStrByKey("Name"), true, true, ap.GetValStrByKey("Key"), "@" + ap.GetValStrByKey("BindKey").replace(",","@"));
					break;
				case FK:
					EntitiesNoName ens = (EntitiesNoName)bp.en.ClassFactory.GetEns(ap.GetValStrByKey("BindKey"));
					this.AddDDLEntities(ap.GetValStrByKey("Key"), ap.GetValStrByKey("DefVal"), ap.GetValStrByKey("Name"), ens, true);
					break;
				default:
					switch (ap.GetValStrByKey("Type"))
					{
						case "String":
							this.AddTBString(ap.GetValStrByKey("Key"), ap.GetValStrByKey("DefVal"), ap.GetValStrByKey("Name"), true, false, 0, 1000, 500);
							break;
						case "Int":
							this.AddTBInt(ap.GetValStrByKey("Key"), ap.GetValIntByKey("DefVal"), ap.GetValStrByKey("Name"), true, false);
							break;
						case "Float":
							this.AddTBFloat(ap.GetValStrByKey("Key"), ap.GetValFloatByKey("DefVal"), ap.GetValStrByKey("Name"), true, false);
							break;
						default:
							break;
					}
					break;
			}
		}
	}
	/**
	 属性集合
	*/
	public Attrs()
	{
	}

	/**
	 转换为mapattrs
	 * @
	*/
	public final MapAttrs ToMapAttrs() throws Exception {
		MapAttrs mapAttrs = new MapAttrs();
		for (Attr item : this)
		{
			if (item.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}

			MapAttr mattr = new MapAttr();
			mattr.setKeyOfEn(item.getKey());
			mattr.setName(item.getDesc());
			mattr.setMyDataType(item.getMyDataType());
			mattr.setUIContralType(item.getUIContralType());
			mattr.setUIBindKey(item.getUIBindKey());

				//@于庆海，这里需要翻译.
			mattr.setUIWidth(item.getUIWidthInt());
			mattr.setUIHeightInt(item.getUIHeight());

			mattr.setMaxLen(item.getMaxLength());
			mattr.setMinLen(item.getMinLength());
			mattr.setUIVisible(item.getUIVisible());
			mattr.setDefValReal(item.getDefaultValOfReal());
			mattr.setDefValType(item.getDefValType());

			mattr.setUIIsEnable(!item.getUIIsReadonly());

			if (item.getItIsSupperText() == 1)
				mattr.setTextModel(3);
			if (item.getUIHeight() > 10)
			{
				if (item.UIIsLine == true)
					mattr.setColSpan(4);
				else
					mattr.setColSpan(3);
			}
			else
			{
				if (item.UIIsLine == true)
					mattr.setColSpan(3);
			}

				//帮助url.
			mattr.SetPara("HelpUrl", item.HelperUrl);
			mattr.setUIRefKeyText(item.getUIRefKeyText());
			mattr.setUIRefKey(item.getUIRefKeyValue());


			if (item.getMyFieldType() == FieldType.Enum)
			{
				mattr.setLGType(FieldTypeS.Enum);
			}

			if (item.getMyFieldType() == FieldType.FK)
			{
				mattr.setLGType(FieldTypeS.FK);
			}

			mapAttrs.AddEntity(mattr);
		}

		return mapAttrs;
	}
	public final void Add(Attr attr)  {
		Add(attr,false);
	}
	public void Add(Attr attr, boolean isClearGroupName)  {
		if (isClearGroupName == false)
			attr.GroupName = this.currGroupAttrName;

		if (attr.getField() == null || attr.getField().equals(""))
		{
			attr.setField(attr.getKey());
			// throw new Exception("@属性设置错误：您不能设置 key='" + attr.getKey() + "', " + attr.getDesc() + ",得字段值为空");
		}
		this.Add(attr, true, false, isClearGroupName);
	}
	/**
	 加入一个属性。

	 param attr attr
	 param isAddHisRefText isAddHisRefText
	 * @
	*/
	public final void Add(Attr attr, boolean isAddHisRefText, boolean isAddHisRefName) {
		Add(attr,isAddHisRefText,isAddHisRefName,false);
	}
	public final void Add(Attr attr, boolean isAddHisRefText, boolean isAddHisRefName, boolean isClearGroupName)  {
		for (Attr myattr : this)
		{
			if (myattr.getKey().equals(attr.getKey()))
			{
				return;
			}
		}

		if (isClearGroupName==false)
			attr.GroupName = this.currGroupAttrName;

		this.add(attr);

		if (isAddHisRefText)
		{
			this.AddRefAttrText(attr);
		}

		if (isAddHisRefName)
		{
			this.AddRefAttrName(attr);
		}
	}
	private void AddRefAttrText(Attr attr)  {
		if (attr.getMyFieldType() == FieldType.Enum && attr.getMyDataType() == DataType.AppString && attr.getUIContralType() == UIContralType.CheckBok)
		{
			return;
		}
		if (attr.getMyFieldType() == FieldType.FK || attr.getMyFieldType() == FieldType.Enum || attr.getMyFieldType() == FieldType.PKEnum || attr.getMyFieldType() == FieldType.PKFK)
		{

			Attr myattr = new Attr();
			myattr.GroupName = this.currGroupAttrName;
			myattr.setMyFieldType(FieldType.RefText);
			myattr.setMyDataType(DataType.AppString);
			myattr.setUIContralType(UIContralType.TB);
			myattr.setUIWidth(attr.getUIWidth() * 2);
			myattr.setKey(attr.getKey() + "Text");

			myattr.setUIIsReadonly(true);
			myattr.setUIBindKey(attr.getUIBindKey());
			myattr.setHisFKEns(attr.getHisFKEns());

			String desc ="名称";
			myattr.setDesc(desc);
			if (desc.indexOf("编号") >= 0)
			{
				myattr.setDesc(attr.getDesc().replace("编号","名称"));
			}
			else
			{
				myattr.setDesc(attr.getDesc() + "名称");
			}

			if (attr.getUIContralType() == UIContralType.DDL)
			{
				myattr.setUIVisible(false);
			}

			this.add(myattr);

		}
	}
	private void AddRefAttrName(Attr attr)
	{
		if (attr.getMyFieldType() == FieldType.FK || attr.getMyFieldType() == FieldType.Enum || attr.getMyFieldType() == FieldType.PKEnum || attr.getMyFieldType() == FieldType.PKFK)
		{

			Attr myattr = new Attr();
			myattr.GroupName = this.currGroupAttrName;
			myattr.setMyFieldType(FieldType.Normal);
			myattr.setMyDataType(DataType.AppString);
			myattr.setUIContralType(UIContralType.TB);
			myattr.setUIWidth(attr.getUIWidth() * 2);

			myattr.setKey(attr.getKey() + "Name");
			myattr.setField(attr.getKey() + "Name");

			myattr.setMaxLength(200);
			myattr.setMinLength(0);

			myattr.setUIVisible(false);
			myattr.setUIIsReadonly(true);

			myattr.setDesc("Name");
			myattr.setDesc(myattr.getDesc());
			this.add(myattr);
		}
	}


	public static ArrayList<Attr> convertAttrs(Object obj)
	{
		return (ArrayList<Attr>) obj;
	}
	public List<Attr> ToJavaList()
	{
		return (List<Attr>)(Object)(Object)this;
	}
}
