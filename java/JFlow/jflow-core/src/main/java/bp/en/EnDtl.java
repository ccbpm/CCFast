package bp.en;

import bp.da.*;

import java.io.Serializable;

/**
 EnDtl 的摘要说明。
 */
public class EnDtl  implements Serializable
{
	private static final long serialVersionUID = 1L;
	/**
	 明细
	 */
	public EnDtl()
	{
	}
	/**
	 编辑器模式 0=默认的DtlBatch.htm, 1=DtlSearch.htm
	 */
	public DtlEditerModel DtlEditerModel = bp.en.DtlEditerModel.DtlBatch;
	/**
	 类名称
	 */
	public final String getEnsName()
	{
		return this.Ens.toString();
	}
	/**
	 明细
	 */
	public Entities Ens = null;

	public Entities getEns() {
		if (Ens != null)
			return Ens;
		if (UrlExt != null && UrlExt.contains("TS."))
			Ens = ClassFactory.GetEns(UrlExt);
//		if (Ens == null) {
//			throw new RuntimeException("没有找到对应的Ens, class name = " + this.UrlExt + ",refKey = " + getRefKey());
//		}
		return Ens;
	}

	public void setEns(Entities ens) {
		Ens = ens;
	}

	public String UrlExt = null;
	/**
	 他关连的 key
	 */
	public String RefKey = null;

	public String getRefKey() {
		return RefKey;
	}

	public void setRefKey(String refKey) {
		RefKey = refKey;
	}

	private String _desc = "";
	/**
	 描述
	 */
	public final String getDesc() throws Exception {
		if (DataType.IsNullOrEmpty(_desc))
		{
			return this.Ens.getNewEntity().getEnDesc();
		}
		return this._desc;
	}
	public final void setDesc(String value)
	{
		this._desc = value;
	}
	/**
	 显示到分组
	 */
	public String GroupName = null;
	public String Icon = null;

}
