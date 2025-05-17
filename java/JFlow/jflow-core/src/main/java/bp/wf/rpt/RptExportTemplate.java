package bp.wf.rpt;

import java.util.*;
import java.time.*;

/** 
 报表导出模板
*/
public class RptExportTemplate
{
	/** 
	 模板最后修改时间
	*/
	private LocalDateTime LastModify = LocalDateTime.MIN;
	public final LocalDateTime getLastModify()
	{
		return LastModify;
	}
	public final void setLastModify(LocalDateTime value)
	{
		LastModify = value;
	}

	/** 
	 导出填充方向
	*/
	private FillDirection Direction = FillDirection.values()[0];
	public final FillDirection getDirection()
	{
		return Direction;
	}
	public final void setDirection(FillDirection value)
	{
		Direction = value;
	}

	/** 
	 导出开始填充的行/列号
	*/
	private int BeginIdx;
	public final int getBeginIdx()
	{
		return BeginIdx;
	}
	public final void setBeginIdx(int value)
	{
		BeginIdx = value;
	}

	/** 
	 字段与单元格绑定信息集合
	*/
	private ArrayList<RptExportTemplateCell> Cells;
	public final ArrayList<RptExportTemplateCell> getCells()
	{
		return Cells;
	}
	public final void setCells(ArrayList<RptExportTemplateCell> value)
	{
		Cells = value;
	}

	/** 
	 是否有单元格绑定了指定的表单中的字段
	 
	 @param fk_mapdata 表单对应FK_MapData
	 @return 
	*/
	public final boolean HaveCellInMapData(String fk_mapdata)
	{
		for (RptExportTemplateCell cell : getCells())
		{
			if (Objects.equals(cell.getFrmID(), fk_mapdata))
			{
				return true;
			}
		}

		return false;
	}

	public final RptExportTemplateCell GetBeginHeaderCell(FillDirection direction)
	{
		if (getCells() == null || getCells().isEmpty())
		{
			return null;
		}

		RptExportTemplateCell cell = getCells().get(0);

		if (direction == FillDirection.Vertical)
		{
			for (int i = 1;i < getCells().size();i++)
			{
				if (getCells().get(i).getColumnIdx() < cell.getColumnIdx())
				{
					cell = getCells().get(i);
				}
			}

			return cell;
		}

		for (int i = 1;i < getCells().size();i++)
		{
			if (getCells().get(i).getRowIdx() < cell.getRowIdx())
			{
				cell = getCells().get(i);
			}
		}

		return cell;
	}


	/** 
	 获取定义的填充明细表NO
	 
	 @return 
	*/
	public final String GetDtl()
	{
		for (RptExportTemplateCell cell : getCells())
		{
			if (!(cell.getDtlKeyOfEn() == null || cell.getDtlKeyOfEn().isEmpty()))
			{
				return cell.getFKDtlMapData();
			}
		}

		return null;
	}


}
