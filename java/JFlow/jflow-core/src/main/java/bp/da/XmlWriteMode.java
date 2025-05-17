package bp.da;

public enum XmlWriteMode
{
	/**
	 * 以 XML 数据形式写入 bp.da.DataSet 的当前内容，以关系结构作为内联 XSD 架构。如果
	 * bp.da.DataSet 只有架构而无数据，那么只写入内联架构。如果 bp.da.DataSet
	 * 没有当前架构，则不写入任何内容。
	 */
	WriteSchema,
	/**
	 * 以 XML 数据形式写入 bp.da.DataSet 的当前内容，不带 XSD 架构。如果无数据加载到
	 * bp.da.DataSet 中，则不写入任何内容。
	 */
	IgnoreSchema;
	/**
	 * 作为 DiffGram 写入整个 bp.da.DataSet，包括原始值和当前值。若要生成只包含已更改的值的 DiffGram，请调用
	 * bp.da.DataSet.GetChanges()，然后在返回的 bp.da.DataSet 上作为 DiffGram
	 * 调用 bp.da.DataSet.WriteXml(System.IO.Stream)。
	 */
	// DiffGram,
	
	public int getValue()
	{
		return this.ordinal();
	}
	
	public static XmlWriteMode forValue(int value) 
	{
		return values()[value];
	}
}