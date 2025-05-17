package cn.jflow.boot.Utils;

import cn.jflow.boot.model.DocumentType;

import java.util.Arrays;
import java.util.List;

public class FileUtils {

    private  static  List<String> extsDocument = Arrays.asList(
            "doc", "docx", "docm",
            "dot", "dotx", "dotm",
            "odt", "fodt", "ott", "rtf", "txt",
            "html", "htm", "mht", "xml",
            "pdf", "djvu", "fb2", "epub", "xps", "oform");

    private  static  List<String> extsSpreadsheet = Arrays.asList(
            "xls", "xlsx", "xlsm", "xlsb",
            "xlt", "xltx", "xltm",
            "ods", "fods", "ots", "csv");

    private static List<String> extsPresentation = Arrays.asList(
            "pps", "ppsx", "ppsm",
            "ppt", "pptx", "pptm",
            "pot", "potx", "potm",
            "odp", "fodp", "otp");
    public static  DocumentType getDocumentType(String suffix) {
        suffix = suffix.toLowerCase();
        if (extsDocument.contains(suffix)) {
            return DocumentType.word;
        }
        if (extsSpreadsheet.contains(suffix)) {
            return DocumentType.cell;
        }
        if (extsPresentation.contains(suffix)) {
            return DocumentType.slide;
        }
        return DocumentType.word;
    }
}
