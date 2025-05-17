package bp.App.Handler;

import bp.da.Log;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import java.math.BigDecimal;

public class ExcelHelperTool {
    public static void SetFormulaVal(Workbook iwBook, String metaSheetName, String name, String val) {
        try {
            Cell iCell = GetICell(iwBook, metaSheetName, name, CellType.FORMULA);
            iCell.setCellFormula(val);
        } catch (RuntimeException ex) {
            Log.DebugWriteError(ex.getMessage());
        }
    }


    public static void SetNumericVal(Workbook iwBook, String metaSheetName, String name, double val) {
        try {
            Cell iCell = GetICell(iwBook, metaSheetName, name, CellType.NUMERIC);
            iCell.setCellValue(val);
        } catch (RuntimeException ex) {
            Log.DebugWriteError(ex.getMessage());
        }
    }

    public static void SetStringCellVal(Workbook iwBook, String metaSheetName, String name, String val) {
        try {
            Cell iCell = GetICell(iwBook, metaSheetName, name, CellType.STRING);
            iCell.setCellValue(val);
        } catch (RuntimeException ex) {
            Log.DebugWriteError(ex.getMessage());
        }
    }

    public static Cell GetICell(Workbook iwBook, String metaSheetName, String name, CellType cellType) {
        try {
            Sheet metaDataSheet = iwBook.getSheet(metaSheetName);

            Name iName = iwBook.getName(name);
            AreaReference arf = new AreaReference(iName.getRefersToFormula(), SpreadsheetVersion.EXCEL2007);
            //如果是合并的单元格，获取左上角的单元格进行赋值
            CellReference cr = arf.getFirstCell();
            Cell iCell = metaDataSheet.getRow(cr.getRow()).getCell(cr.getCol());

            return iCell;
        } catch (RuntimeException ex) {
            Log.DebugWriteError(ex.getMessage());
            return null;
        }
    }

    public static double DecimalToDouble(String val) {
        BigDecimal _val = new BigDecimal(val);
        return _val.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
