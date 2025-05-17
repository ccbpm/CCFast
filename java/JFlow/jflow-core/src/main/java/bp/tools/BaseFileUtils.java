package bp.tools;

import bp.difference.SystemConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 文件操作工具类 实现文件的创建、删除、复制、压缩、解压以及目录的创建、删除、复制、压缩解压等功能
 *
 * @author fanchengliang
 */
public class BaseFileUtils extends org.apache.commons.io.FileUtils {

    private static Logger logger = LoggerFactory.getLogger(BaseFileUtils.class);

    /**
     * 复制单个文件，如果目标文件存在，则不覆盖
     *
     * param srcFileName 待复制的文件名
     * param descFileName 目标文件名
     * @return 如果复制成功，则返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String descFileName) {
        return BaseFileUtils.copyFileCover(srcFileName, descFileName, false);
    }

    /**
     * 复制单个文件
     *
     * param srcFileName 待复制的文件名
     * param descFileName 目标文件名
     * param coverlay 如果目标文件已存在，是否覆盖
     * @return 如果复制成功，则返回true，否则返回false
     */
    public static boolean copyFileCover(String srcFileName, String descFileName, boolean coverlay) {
        File srcFile = new File(srcFileName);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            logger.debug("复制文件失败，源文件 " + srcFileName + " 不存在!");
            return false;
        }
        // 判断源文件是否是合法的文件
        else if (!srcFile.isFile()) {
            logger.debug("复制文件失败，" + srcFileName + " 不是一个文件!");
            return false;
        }
        File descFile = new File(descFileName);
        // 判断目标文件是否存在
        if (descFile.exists()) {
            // 如果目标文件存在，并且允许覆盖
            if (coverlay) {
                logger.debug("目标文件已存在，准备删除!");
                if (!BaseFileUtils.delFile(descFileName)) {
                    logger.debug("删除目标文件 " + descFileName + " 失败!");
                    return false;
                }
            }
            else {
                logger.debug("复制文件失败，目标文件 " + descFileName + " 已存在!");
                return false;
            }
        }
        else {
            if (!descFile.getParentFile().exists()) {
                // 如果目标文件所在的目录不存在，则创建目录
                logger.debug("目标文件所在的目录不存在，创建目录!");
                // 创建目标文件所在的目录
                if (!descFile.getParentFile().mkdirs()) {
                    logger.debug("创建目标文件所在的目录失败!");
                    return false;
                }
            }
        }

        // 准备复制文件
        // 读取的位数
        int readByte = 0;
        InputStream ins = null;
        OutputStream outs = null;
        try {
            // 打开源文件
            ins = new FileInputStream(srcFile);
            // 打开目标文件的输出流
            outs = new FileOutputStream(descFile);
            byte[] buf = new byte[1024];
            // 一次读取1024个字节，当readByte为-1时表示文件已经读取完毕
            while ((readByte = ins.read(buf)) != -1) {
                // 将读取的字节流写入到输出流
                outs.write(buf, 0, readByte);
            }
            logger.debug("复制单个文件 " + srcFileName + " 到" + descFileName + "成功!");
            return true;
        }
        catch (Exception e) {
            logger.debug("复制文件失败：" + e.getMessage());
            return false;
        }
        finally {
            // 关闭输入输出流，首先关闭输出流，然后再关闭输入流
            if (outs != null) {
                try {
                    outs.close();
                }
                catch (IOException oute) {
                    logger.error(oute.getMessage());
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                }
                catch (IOException ine) {
                    logger.error(ine.getMessage());
                }
            }
        }
    }

    /**
     * 复制整个目录的内容，如果目标目录存在，则不覆盖
     *
     * param srcDirName 源目录名
     * param descDirName 目标目录名
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectory(String srcDirName, String descDirName) {
        return BaseFileUtils.copyDirectoryCover(srcDirName, descDirName, false);
    }

    /**
     * 复制整个目录的内容
     *
     * param srcDirName 源目录名
     * param descDirName 目标目录名
     * param coverlay 如果目标目录存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectoryCover(String srcDirName, String descDirName, boolean coverlay) {
        File srcDir = new File(srcDirName);
        // 判断源目录是否存在
        if (!srcDir.exists()) {
            logger.debug("复制目录失败，源目录 " + srcDirName + " 不存在!");
            return false;
        }
        // 判断源目录是否是目录
        else if (!srcDir.isDirectory()) {
            logger.debug("复制目录失败，" + srcDirName + " 不是一个目录!");
            return false;
        }
        // 如果目标文件夹名不以文件分隔符结尾，自动添加文件分隔符
        String descDirNames = descDirName;
        if (!descDirNames.endsWith(File.separator)) {
            descDirNames = descDirNames + File.separator;
        }
        File descDir = new File(descDirNames);
        // 如果目标文件夹存在
        if (descDir.exists()) {
            if (coverlay) {
                // 允许覆盖目标目录
                logger.debug("目标目录已存在，准备删除!");
                if (!BaseFileUtils.delFile(descDirNames)) {
                    logger.debug("删除目录 " + descDirNames + " 失败!");
                    return false;
                }
            }
            else {
                logger.debug("目标目录复制失败，目标目录 " + descDirNames + " 已存在!");
                return false;
            }
        }
        else {
            // 创建目标目录
            logger.debug("目标目录不存在，准备创建!");
            if (!descDir.mkdirs()) {
                logger.debug("创建目标目录失败!");
                return false;
            }

        }

        boolean flag = true;
        // 列出源目录下的所有文件名和子目录名
        File[] files = srcDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 如果是一个单个文件，则直接复制
            if (files[i].isFile()) {
                flag = BaseFileUtils.copyFile(files[i].getAbsolutePath(), descDirName + files[i].getName());
                // 如果拷贝文件失败，则退出循环
                if (!flag) {
                    break;
                }
            }
            // 如果是子目录，则继续复制目录
            if (files[i].isDirectory()) {
                flag = BaseFileUtils.copyDirectory(files[i].getAbsolutePath(), descDirName + files[i].getName());
                // 如果拷贝目录失败，则退出循环
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            logger.debug("复制目录 " + srcDirName + " 到 " + descDirName + " 失败!");
            return false;
        }
        logger.debug("复制目录 " + srcDirName + " 到 " + descDirName + " 成功!");
        return true;

    }

    /**
     *
     * 删除文件，可以删除单个文件或文件夹
     *
     * param fileName 被删除的文件名
     * @return 如果删除成功，则返回true，否是返回false
     */
    public static boolean delFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            logger.debug(fileName + " 文件不存在!");
            return true;
        }
        else {
            if (file.isFile()) {
                return BaseFileUtils.deleteFile(fileName);
            }
            else {
                return BaseFileUtils.deleteDirectory(fileName);
            }
        }
    }

    /**
     *
     * 删除单个文件
     *
     * param fileName 被删除的文件名
     * @return 如果删除成功，则返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                logger.debug("删除文件 " + fileName + " 成功!");
                return true;
            }
            else {
                logger.debug("删除文件 " + fileName + " 失败!");
                return false;
            }
        }
        else {
            logger.debug(fileName + " 文件不存在!");
            return true;
        }
    }

    /**
     *
     * 删除目录及目录下的文件
     *
     * param dirName 被删除的目录所在的文件路径
     * @return 如果目录删除成功，则返回true，否则返回false
     */
    public static boolean deleteDirectory(String dirName) {
        String dirNames = dirName;
        if (!dirNames.endsWith(File.separator)) {
            dirNames = dirNames + File.separator;
        }
        File dirFile = new File(dirNames);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            logger.debug(dirNames + " 目录不存在!");
            return true;
        }
        boolean flag = true;
        // 列出全部文件及子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = BaseFileUtils.deleteFile(files[i].getAbsolutePath());
                // 如果删除文件失败，则退出循环
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = BaseFileUtils.deleteDirectory(files[i].getAbsolutePath());
                // 如果删除子目录失败，则退出循环
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            logger.debug("删除目录失败!");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            logger.debug("删除目录 " + dirName + " 成功!");
            return true;
        }
        else {
            logger.debug("删除目录 " + dirName + " 失败!");
            return false;
        }

    }

    /**
     * 创建单个文件
     *
     * param descFileName 文件名，包含路径
     * @return 如果创建成功，则返回true，否则返回false
     */
    public static boolean createFile(String descFileName) {
        File file = new File(descFileName);
        if (file.exists()) {
            logger.debug("文件 " + descFileName + " 已存在!");
            return false;
        }
        if (descFileName.endsWith(File.separator)) {
            logger.debug(descFileName + " 为目录，不能创建目录!");
            return false;
        }
        if (!file.getParentFile().exists()) {
            // 如果文件所在的目录不存在，则创建目录
            if (!file.getParentFile().mkdirs()) {
                logger.debug("创建文件所在的目录失败!");
                return false;
            }
        }

        // 创建文件
        try {
            if (file.createNewFile()) {
                logger.debug(descFileName + " 文件创建成功!");
                return true;
            }
            else {
                logger.debug(descFileName + " 文件创建失败!");
                return false;
            }
        }
        catch (Exception e) {
            logger.debug(descFileName + " 文件创建失败!");
            return false;
        }

    }

    /**
     * 创建目录
     *
     * param descDirName 目录名,包含路径
     * @return 如果创建成功，则返回true，否则返回false
     */
    public static boolean createDirectory(String descDirName) {
        String descDirNames = descDirName;
        if (!descDirNames.endsWith(File.separator)) {
            descDirNames = descDirNames + File.separator;
        }
        File descDir = new File(descDirNames);
        if (descDir.exists()) {
            logger.debug("目录 " + descDirNames + " 已存在!");
            return false;
        }
        // 创建目录
        if (descDir.mkdirs()) {
            logger.debug("目录 " + descDirNames + " 创建成功!");
            return true;
        }
        else {
            logger.debug("目录 " + descDirNames + " 创建失败!");
            return false;
        }

    }

    /**
     *
     * 写入文件. <br/>
     *
     * param fileName 文件名
     * param content 内容
     * param append 追加模式
     */
    public static void writeToFile(String fileName, String content, boolean append) {
        try {
            BaseFileUtils.write(new File(fileName), content, "utf-8", append);
            logger.debug("文件 " + fileName + " 写入成功!");
        }
        catch (IOException e) {
            logger.debug("文件 " + fileName + " 写入失败! " + e.getMessage());
        }
    }

    /**
     *
     * 写入文件. <br/>
     *
     * param fileName 文件名
     * param content 内容
     * param encoding 编码
     * param append 追加模式
     */
    public static void writeToFile(String fileName, String content, String encoding, boolean append) {
        try {
            BaseFileUtils.write(new File(fileName), content, encoding, append);
            logger.debug("文件 " + fileName + " 写入成功!");
        }
        catch (IOException e) {
            logger.debug("文件 " + fileName + " 写入失败! " + e.getMessage());
        }
    }

    /**
     * 压缩文件或目录
     *
     * param srcDirName 压缩的根目录
     * param fileName 根目录下的待压缩的文件名或文件夹名，其中*或""表示跟目录下的全部文件
     * param descFileName 目标zip文件
     */
    public static void zipFiles(String srcDirName, String fileName, String descFileName) {
        // 判断目录是否存在
        if (srcDirName == null) {
            logger.debug("文件压缩失败，目录 " + srcDirName + " 不存在!");
            return;
        }
        File fileDir = new File(srcDirName);
        if (!fileDir.exists() || !fileDir.isDirectory()) {
            logger.debug("文件压缩失败，目录 " + srcDirName + " 不存在!");
            return;
        }
        String dirPath = fileDir.getAbsolutePath();
        File descFile = new File(descFileName);
        try {
            ZipOutputStream zouts = new ZipOutputStream(new FileOutputStream(descFile));
            if (Constant4Comm.STR_STAR.equals(fileName) || "".equals(fileName)) {
                BaseFileUtils.zipDirectoryToZipFile(dirPath, fileDir, zouts);
            }
            else {
                File file = new File(fileDir, fileName);
                if (file.isFile()) {
                    BaseFileUtils.zipFilesToZipFile(dirPath, file, zouts);
                }
                else {
                    BaseFileUtils.zipDirectoryToZipFile(dirPath, file, zouts);
                }
            }
            zouts.close();
            logger.debug(descFileName + " 文件压缩成功!");
        }
        catch (Exception e) {
            logger.debug("文件压缩失败：" + e.getMessage());
        }

    }

    /**
     * 解压缩ZIP文件，将ZIP文件里的内容解压到descFileName目录下
     *
     * param zipFileName 需要解压的ZIP文件
     * param descFileName 目标文件
     */
    public static boolean unZipFiles(String zipFileName, String descFileName) {
        String descFileNames = descFileName;
        if (!descFileNames.endsWith(File.separator)) {
            descFileNames = descFileNames + File.separator;
        }
        try {
            // 根据ZIP文件创建ZipFile对象
            ZipFile zipFile = new ZipFile(zipFileName);
            ZipEntry entry = null;
            String entryName = null;
            String descFileDir = null;
            byte[] buf = new byte[4096];
            int readByte = 0;
            // 获取ZIP文件里所有的entry
            @SuppressWarnings("rawtypes")
            Enumeration enums = zipFile.getEntries();
            // 遍历所有entry
            while (enums.hasMoreElements()) {
                entry = (ZipEntry)enums.nextElement();
                // 获得entry的名字
                entryName = entry.getName();
                descFileDir = descFileNames + entryName;
                if (entry.isDirectory()) {
                    // 如果entry是一个目录，则创建目录
                    new File(descFileDir).mkdirs();
                    continue;
                }
                else {
                    // 如果entry是一个文件，则创建父目录
                    new File(descFileDir).getParentFile().mkdirs();
                }
                File file = new File(descFileDir);
                // 打开文件输出流
                OutputStream os = new FileOutputStream(file);
                // 从ZipFile对象中打开entry的输入流
                InputStream is = zipFile.getInputStream(entry);
                while ((readByte = is.read(buf)) != -1) {
                    os.write(buf, 0, readByte);
                }
                os.close();
                is.close();
            }
            zipFile.close();
            logger.debug("文件解压成功!");
            return true;
        }
        catch (Exception e) {
            logger.debug("文件解压失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 将目录压缩到ZIP输出流
     *
     * param dirPath 目录路径
     * param fileDir 文件信息
     * param zouts 输出流
     */
    public static void zipDirectoryToZipFile(String dirPath, File fileDir, ZipOutputStream zouts) {
        if (fileDir.isDirectory()) {
            File[] files = fileDir.listFiles();
            // 空的文件夹
            if (files.length == 0) {
                // 目录信息
                ZipEntry entry = new ZipEntry(getEntryName(dirPath, fileDir));
                try {
                    zouts.putNextEntry(entry);
                    zouts.closeEntry();
                }
                catch (Exception e) {
                    logger.error(e.getMessage());
                }
                return;
            }

            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    // 如果是文件，则调用文件压缩方法
                    BaseFileUtils.zipFilesToZipFile(dirPath, files[i], zouts);
                }
                else {
                    // 如果是目录，则递归调用
                    BaseFileUtils.zipDirectoryToZipFile(dirPath, files[i], zouts);
                }
            }
        }
    }

    /**
     * 将文件压缩到ZIP输出流
     *
     * param dirPath 目录路径
     * param file 文件
     * param zouts 输出流
     */
    public static void zipFilesToZipFile(String dirPath, File file, ZipOutputStream zouts) {
        FileInputStream fin = null;
        ZipEntry entry = null;
        // 创建复制缓冲区
        byte[] buf = new byte[4096];
        int readByte = 0;
        if (file.isFile()) {
            try {
                // 创建一个文件输入流
                fin = new FileInputStream(file);
                // 创建一个ZipEntry
                entry = new ZipEntry(getEntryName(dirPath, file));
                // 存储信息到压缩文件
                zouts.putNextEntry(entry);
                // 复制字节到压缩文件
                while ((readByte = fin.read(buf)) != -1) {
                    zouts.write(buf, 0, readByte);
                }
                zouts.closeEntry();
                fin.close();
            }
            catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 获取待压缩文件在ZIP文件中entry的名字，即相对于根目录的相对路径名
     *
     * param dirPath 目录名
     * param file entry文件名
     * @return 文件在ZIP文件中entry的名字
     */
    private static String getEntryName(String dirPath, File file) {
        String dirPaths = dirPath;
        if (!dirPaths.endsWith(File.separator)) {
            dirPaths = dirPaths + File.separator;
        }
        String filePath = file.getAbsolutePath();
        // 对于目录，必须在entry名字后面加上Constant4Comm.STR_SLASH，表示它将以目录项存储
        if (file.isDirectory()) {
            filePath += Constant4Comm.STR_SLASH;
        }
        int index = filePath.indexOf(dirPaths);

        return filePath.substring(index + dirPaths.length());
    }

    /**
     * 根据“文件名的后缀”获取文件内容类型（而非根据File.getContentType()读取的文件类型）
     *
     * param returnFileName 带验证的文件名
     * @return 返回文件类型
     */
    public static String getContentType(String returnFileName) {
        String contentType = "application/octet-stream";
        if (returnFileName.lastIndexOf(Constant4Comm.STR_DOT) < 0) {
            return contentType;
        }
        returnFileName = returnFileName.toLowerCase();
        returnFileName = returnFileName.substring(returnFileName.lastIndexOf(Constant4Comm.STR_DOT) + 1);
        if (Constant4File.SUFIX_HTML.equals(returnFileName) || Constant4File.SUFIX_HTM.equals(returnFileName)
            || Constant4File.SUFIX_SHTML.equals(returnFileName)) {
            contentType = "text/html";
        }
        else if (Constant4File.SUFIX_APK.equals(returnFileName)) {
            contentType = "application/vnd.android.package-archive";
        }
        else if (Constant4File.SUFIX_SIS.equals(returnFileName)) {
            contentType = "application/vnd.symbian.install";
        }
        else if (Constant4File.SUFIX_SISX.equals(returnFileName)) {
            contentType = "application/vnd.symbian.install";
        }
        else if (Constant4File.SUFIX_EXE.equals(returnFileName)) {
            contentType = "application/x-msdownload";
        }
        else if (Constant4File.SUFIX_MSI.equals(returnFileName)) {
            contentType = "application/x-msdownload";
        }
        else if (Constant4File.SUFIX_CSS.equals(returnFileName)) {
            contentType = "text/css";
        }
        else if (Constant4File.SUFIX_XML.equals(returnFileName)) {
            contentType = "text/xml";
        }
        else if (Constant4File.SUFIX_GIF.equals(returnFileName)) {
            contentType = "image/gif";
        }
        else if (Constant4File.SUFIX_JPEG.equals(returnFileName) || Constant4File.SUFIX_JPG.equals(returnFileName)) {
            contentType = "image/jpeg";
        }
        else if (Constant4File.SUFIX_JS.equals(returnFileName)) {
            contentType = "application/x-javascript";
        }
        else if (Constant4File.SUFIX_ATOM.equals(returnFileName)) {
            contentType = "application/atom+xml";
        }
        else if (Constant4File.SUFIX_RSS.equals(returnFileName)) {
            contentType = "application/rss+xml";
        }
        else if (Constant4File.SUFIX_MML.equals(returnFileName)) {
            contentType = "text/mathml";
        }
        else if (Constant4File.SUFIX_TXT.equals(returnFileName)) {
            contentType = "text/plain";
        }
        else if (Constant4File.SUFIX_JAD.equals(returnFileName)) {
            contentType = "text/vnd.sun.j2me.app-descriptor";
        }
        else if (Constant4File.SUFIX_WML.equals(returnFileName)) {
            contentType = "text/vnd.wap.wml";
        }
        else if (Constant4File.SUFIX_HTC.equals(returnFileName)) {
            contentType = "text/x-component";
        }
        else if (Constant4File.SUFIX_PNG.equals(returnFileName)) {
            contentType = "image/png";
        }
        else if (Constant4File.SUFIX_TIF.equals(returnFileName) || Constant4File.SUFIX_TIFF.equals(returnFileName)) {
            contentType = "image/tiff";
        }
        else if (Constant4File.SUFIX_WBMP.equals(returnFileName)) {
            contentType = "image/vnd.wap.wbmp";
        }
        else if (Constant4File.SUFIX_ICO.equals(returnFileName)) {
            contentType = "image/x-icon";
        }
        else if (Constant4File.SUFIX_JNG.equals(returnFileName)) {
            contentType = "image/x-jng";
        }
        else if (Constant4File.SUFIX_BMP.equals(returnFileName)) {
            contentType = "image/x-ms-bmp";
        }
        else if (Constant4File.SUFIX_SVG.equals(returnFileName)) {
            contentType = "image/svg+xml";
        }
        else if (Constant4File.SUFIX_JAR.equals(returnFileName) || Constant4File.SUFIX_VAR.equals(returnFileName)
            || Constant4File.SUFIX_EAR.equals(returnFileName)) {
            contentType = "application/java-archive";
        }
        else if (Constant4File.SUFIX_DOC.equals(returnFileName)) {
            contentType = "application/msword";
        }
        else if (Constant4File.SUFIX_PDF.equals(returnFileName)) {
            contentType = "application/pdf";
        }
        else if (Constant4File.SUFIX_RTF.equals(returnFileName)) {
            contentType = "application/rtf";
        }
        else if (Constant4File.SUFIX_XLS.equals(returnFileName)) {
            contentType = "application/vnd.ms-excel";
        }
        else if (Constant4File.SUFIX_PPT.equals(returnFileName)) {
            contentType = "application/vnd.ms-powerpoint";
        }
        else if (Constant4File.SUFIX_7Z.equals(returnFileName)) {
            contentType = "application/x-7z-compressed";
        }
        else if (Constant4File.SUFIX_RAR.equals(returnFileName)) {
            contentType = "application/x-rar-compressed";
        }
        else if (Constant4File.SUFIX_SWF.equals(returnFileName)) {
            contentType = "application/x-shockwave-flash";
        }
        else if (Constant4File.SUFIX_RPM.equals(returnFileName)) {
            contentType = "application/x-redhat-package-manager";
        }
        else if (Constant4File.SUFIX_DER.equals(returnFileName) || Constant4File.SUFIX_PEM.equals(returnFileName)
            || Constant4File.SUFIX_CRT.equals(returnFileName)) {
            contentType = "application/x-x509-ca-cert";
        }
        else if (Constant4File.SUFIX_XHTML.equals(returnFileName)) {
            contentType = "application/xhtml+xml";
        }
        else if (Constant4File.SUFIX_ZIP.equals(returnFileName)) {
            contentType = "application/zip";
        }
        else if (Constant4File.SUFIX_MID.equals(returnFileName) || Constant4File.SUFIX_MIDI.equals(returnFileName)
            || Constant4File.SUFIX_KAR.equals(returnFileName)) {
            contentType = "audio/midi";
        }
        else if (Constant4File.SUFIX_MP3.equals(returnFileName)) {
            contentType = "audio/mpeg";
        }
        else if (Constant4File.SUFIX_OGG.equals(returnFileName)) {
            contentType = "audio/ogg";
        }
        else if (Constant4File.SUFIX_M4A.equals(returnFileName)) {
            contentType = "audio/x-m4a";
        }
        else if (Constant4File.SUFIX_RA.equals(returnFileName)) {
            contentType = "audio/x-realaudio";
        }
        else if (Constant4File.SUFIX_3GPP.equals(returnFileName) || Constant4File.SUFIX_3GP.equals(returnFileName)) {
            contentType = "video/3gpp";
        }
        else if (Constant4File.SUFIX_MP4.equals(returnFileName)) {
            contentType = "video/mp4";
        }
        else if (Constant4File.SUFIX_MPEG.equals(returnFileName) || Constant4File.SUFIX_MPG.equals(returnFileName)) {
            contentType = "video/mpeg";
        }
        else if (Constant4File.SUFIX_MOV.equals(returnFileName)) {
            contentType = "video/quicktime";
        }
        else if (Constant4File.SUFIX_FLV.equals(returnFileName)) {
            contentType = "video/x-flv";
        }
        else if (Constant4File.SUFIX_M4V.equals(returnFileName)) {
            contentType = "video/x-m4v";
        }
        else if (Constant4File.SUFIX_MNG.equals(returnFileName)) {
            contentType = "video/x-mng";
        }
        else if (Constant4File.SUFIX_ASX.equals(returnFileName) || Constant4File.SUFIX_ASF.equals(returnFileName)) {
            contentType = "video/x-ms-asf";
        }
        else if (Constant4File.SUFIX_WMV.equals(returnFileName)) {
            contentType = "video/x-ms-wmv";
        }
        else if (Constant4File.SUFIX_AVI.equals(returnFileName)) {
            contentType = "video/x-msvideo";
        }
        return contentType;
    }

    /**
     * 向浏览器发送文件下载，支持断点续传
     *
     * param file 要下载的文件
     * param request 请求对象
     * param response 响应对象
     * @return 返回错误信息，无错误信息返回null
     */
    public static String downFile(File file, HttpServletRequest request, HttpServletResponse response) {
        return downFile(file, request, response, null);
    }

    /**
     * 向浏览器发送文件下载，支持断点续传
     *
     * param file 要下载的文件
     * param request 请求对象
     * param response 响应对象
     * param fileName 指定下载的文件名
     * @return 返回错误信息，无错误信息返回null
     */
    public static String downFile(File file, HttpServletRequest request, HttpServletResponse response,
        String fileName) {
        String error = null;
        if (file != null && file.exists()) {
            if (file.isFile()) {
                if (file.length() <= 0) {
                    error = "该文件是一个空文件。";
                }
                if (!file.canRead()) {
                    error = "该文件没有读取权限。";
                }
            }
            else {
                error = "该文件是一个文件夹。";
            }
        }
        else {
            error = "文件已丢失或不存在！";
        }
        if (error != null) {
            logger.debug("---------------" + file + " " + error);
            return error;
        }

        // 记录文件大小
        long fileLength = file.length();
        // 记录已下载文件大小
        long pastLength = 0;
        // 0：从头开始的全文下载；1：从某字节开始的下载（bytes=27000-）；2：从某字节开始到某字节结束的下载（bytes=27000-39000）
        int rangeSwitch = 0;
        // 记录客户端需要下载的字节段的最后一个字节偏移量（比如bytes=27000-39000，则这个值是为39000）
        long toLength = 0;
        // 客户端请求的字节总量
        long contentLength = 0;
        // 记录客户端传来的形如“bytes=27000-”或者“bytes=27000-39000”的内容
        String rangeBytes = "";
        // 负责读取数据
        RandomAccessFile raf = null;
        // 写出数据
        OutputStream os = null;
        // 缓冲
        OutputStream out = null;
        // 暂存容器
        byte b[] = new byte[1024];

        // 客户端请求的下载的文件块的开始字节
        if (request.getHeader(Constant4Comm.REQ_HEAD_RANGE) != null) {
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
            logger.debug("request.getHeader(\"Range\") = " + request.getHeader(Constant4Comm.REQ_HEAD_RANGE));
            rangeBytes = request.getHeader(Constant4Comm.REQ_HEAD_RANGE).replaceAll("bytes=", "");
            if (rangeBytes.indexOf(Constant4Comm.CHAR_MINUS) == rangeBytes.length() - 1) {
                rangeSwitch = 1;
                rangeBytes = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                pastLength = Long.parseLong(rangeBytes.trim());
                // 客户端请求的是 969998336 之后的字节
                contentLength = fileLength - pastLength;
            }
            else {
                rangeSwitch = 2;
                String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                String temp2 = rangeBytes.substring(rangeBytes.indexOf('-') + 1, rangeBytes.length());
                // bytes=1275856879-1275877358，从第 1275856879 个字节开始下载
                pastLength = Long.parseLong(temp0.trim());
                // bytes=1275856879-1275877358，到第 1275877358 个字节结束
                toLength = Long.parseLong(temp2);
                // 客户端请求的是 1275856879-1275877358 之间的字节
                contentLength = toLength - pastLength;
            }
        }
        else {
            // 从开始进行下载
            // 客户端要求全文下载
            contentLength = fileLength;
        }

        response.reset();

        if (pastLength != 0) {
            // 如果是第一次下,还没有断点续传,状态是默认的 200,无需显式设置;响应的格式是:HTTP/1.1 200 OK
            response.setHeader("Accept-Ranges", "bytes");
            // 不是从最开始下载, 响应的格式是: Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
            logger.debug("---------------不是从开始进行下载！服务器即将开始断点续传...");
            switch (rangeSwitch) {
                // 针对 bytes=27000- 的请求
                case 1: {
                    String contentRange = new StringBuffer("bytes ").append(new Long(pastLength).toString())
                        .append("-")
                        .append(new Long(fileLength - 1).toString())
                        .append(Constant4Comm.STR_SLASH)
                        .append(new Long(fileLength).toString())
                        .toString();
                    if(contentRange.contains(" ") || contentRange.contains("\t") || contentRange.contains("\r")|| contentRange.contains(":")|| contentRange.contains(";")|| contentRange.contains("="))
                        throw new RuntimeException("err@contentRange格式不合法:"+contentRange);
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                // 针对 bytes=27000-39000 的请求
                case 2: {
                    String contentRange = rangeBytes + Constant4Comm.STR_SLASH + new Long(fileLength).toString();
                    if(contentRange.contains(" ") || contentRange.contains("\t") || contentRange.contains("\r")|| contentRange.contains(":")|| contentRange.contains(";")|| contentRange.contains("="))
                        throw new RuntimeException("err@contentRange格式不合法:"+contentRange);
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                default: {
                    break;
                }
            }
        }
        else {
            // 是从开始下载
            logger.debug("---------------是从开始进行下载！");
        }

        try {
            response.addHeader("Content-Disposition",
                "attachment; filename=\""
                    + Encodes.urlEncode(StringUtils.isBlank(fileName) ? file.getName() : fileName) + "\"");
            // set the MIME type.
            response.setContentType(getContentType(file.getName()));
            response.addHeader("Content-Length", String.valueOf(contentLength));
            os = response.getOutputStream();
            out = new BufferedOutputStream(os);
            raf = new RandomAccessFile(file, "r");
            int maxLen = 1024;
            try {
                switch (rangeSwitch) {
                    // 普通下载，或者从头开始的下载 同1
                    case 0: {
                    }
                    // 针对 bytes=27000- 的请求
                    case 1: {
                        // 形如 bytes=969998336- 的客户端请求，跳过 969998336 个字节
                        raf.seek(pastLength);
                        int n = 0;
                        while ((n = raf.read(b, 0, maxLen)) != -1) {
                            out.write(b, 0, n);
                        }
                        break;
                    }
                    // 针对 bytes=27000-39000 的请求
                    case 2: {
                        // 形如 bytes=1275856879-1275877358 的客户端请求，找到第 1275856879 个字节
                        raf.seek(pastLength);
                        int n = 0;
                        // 记录已读字节数
                        long readLength = 0;
                        // 大部分字节在这里读取
                        while (readLength <= contentLength - maxLen) {
                            n = raf.read(b, 0, 1024);
                            readLength += 1024;
                            out.write(b, 0, n);
                        }
                        // 余下的不足 1024 个字节在这里读取
                        if (readLength <= contentLength) {
                            n = raf.read(b, 0, (int)(contentLength - readLength));
                            out.write(b, 0, n);
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
                out.flush();
                logger.debug("---------------下载完成！");
            }
            catch (IOException ie) {
                /**
                 * 在写数据的时候， 对于 ClientAbortException 之类的异常， 是因为客户端取消了下载，而服务器端继续向浏览器写入数据时， 抛出这个异常，这个是正常的。
                 * 尤其是对于迅雷这种吸血的客户端软件， 明明已经有一个线程在读取 bytes=1275856879-1275877358， 如果短时间内没有读取完毕，迅雷会再启第二个、第三个。。。线程来读取相同的字节段，
                 * 直到有一个线程读取完毕，迅雷会 KILL 掉其他正在下载同一字节段的线程， 强行中止字节读出，造成服务器抛 ClientAbortException。 所以，我们忽略这种异常
                 */
                logger.debug("提醒：向客户端传输时出现IO异常，但此异常是允许的，有可能客户端取消了下载，导致此异常，不用关心！");
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                }
                catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    /**
     * 修正路径，将 \\ 或 / 等替换为 File.separator
     *
     * param path 待修正的路径
     * @return 修正后的路径
     */
    public static String path(String path) {
        String p = StringUtils.replace(path, Constant4Comm.STR_BACK_SLASH, Constant4Comm.STR_SLASH);
        p = StringUtils.join(StringUtils.split(p, Constant4Comm.STR_SLASH), Constant4Comm.STR_SLASH);
        if (!StringUtils.startsWithAny(p, Constant4Comm.STR_SLASH) && StringUtils.startsWithAny(path, Constant4Comm.STR_BACK_SLASH, Constant4Comm.STR_SLASH)) {
            p += Constant4Comm.STR_SLASH;
        }
        if (!StringUtils.endsWithAny(p, Constant4Comm.STR_SLASH) && StringUtils.endsWithAny(path, Constant4Comm.STR_BACK_SLASH, Constant4Comm.STR_SLASH)) {
            p = p + Constant4Comm.STR_SLASH;
        }
        if (path != null && path.startsWith(Constant4Comm.STR_SLASH)) {
            // linux下路径
            p = Constant4Comm.STR_SLASH + p;
        }
        return p;
    }

    /**
     * 获目录下的文件列表
     *
     * param dir 搜索目录
     * param searchDirs 是否是搜索目录
     * @return 文件列表
     */
    public static List<String> findChildrenList(File dir, boolean searchDirs) {
        List<String> files = new ArrayList<String>();
        for (String subFiles : dir.list()) {
            File file = new File(dir + Constant4Comm.STR_SLASH + subFiles);
            boolean isDir = searchDirs && file.isDirectory();
            boolean isFile = !searchDirs && !file.isDirectory();
            if (isDir || isFile) {
                files.add(file.getName());
            }
        }
        return files;
    }

    /**
     * 获取文件扩展名(返回小写)
     *
     * param fileName 文件名
     * @return 例如：test.jpg 返回： jpg
     */
    public static String getFileExtension(String fileName) {
        if ((fileName == null) || (fileName.lastIndexOf(Constant4Comm.STR_DOT) == -1)
            || (fileName.lastIndexOf(Constant4Comm.STR_DOT) == fileName.length() - 1)) {
            return null;
        }
        return StringUtils.lowerCase(fileName.substring(fileName.lastIndexOf(Constant4Comm.STR_DOT) + 1));
    }

    /**
     * 获取文件名，不包含扩展名
     *
     * param fileName 文件名
     * @return 例如：d:\files\test.jpg 返回：d:\files\test
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if ((fileName == null) || (fileName.lastIndexOf(Constant4Comm.STR_DOT) == -1)) {
            return null;
        }
        return fileName.substring(0, fileName.lastIndexOf(Constant4Comm.STR_DOT));
    }

    /**
     * 格式化路径分隔符
     * param filePath 路径
     * @return  格式化路径
     */
    public static String formatSeparator(String filePath) {
        filePath = StringUtils.replacePattern(filePath, "\\\\{1,}", "/");
        filePath = StringUtils.replacePattern(filePath, "//{2,}", "/");
        return filePath;
    }

    /**
    * 获取指定文件夹下的所有文件列表
    * param folder
    * @return
    * @throws IOException
    */
    public static String[] getFiles(String folder) throws Exception {
        if(SystemConfig.isJarRun()){
            if(folder.endsWith("/")==false)
                folder +="/";
            Resource[] resources =  new PathMatchingResourcePatternResolver().getResources(ResourceUtils.CLASSPATH_URL_PREFIX+folder + "*.*");
            String[] retS=new String[resources.length];
            int i =0;
            for(Resource resource : resources){
                retS[i]=folder+resource.getFilename();
                i++;
            }
            return retS;
        }
         File _folder=new File(folder);
         String[] filesInFolder;
         if(_folder.isDirectory()){
             filesInFolder=_folder.list();
             String[] retS=new String[filesInFolder.length];
             for(int i=0;i<filesInFolder.length;i++){
                 retS[i]=folder+File.separator+filesInFolder[i];
             }
             return retS;
         }else{
             throw new IOException("路径不是文件夹！");
         }
     }

    public static String[] getFileNames(String folder) throws Exception {
        if(SystemConfig.isJarRun()){
            if(folder.endsWith("/")==false)
                folder +="/";
            Resource[] resources =  new PathMatchingResourcePatternResolver().getResources(ResourceUtils.CLASSPATH_URL_PREFIX+folder + "*.*");
            String[] retS=new String[resources.length];
            int i =0;
            for(Resource resource : resources){
                retS[i]=resource.getFilename();
                i++;
            }
            return retS;
        }
        File _folder=new File(folder);
        String[] filesInFolder;
        if(_folder.isDirectory()){
            filesInFolder=_folder.list();
            String[] retS=new String[filesInFolder.length];
            for(int i=0;i<filesInFolder.length;i++){
                retS[i]=filesInFolder[i];
            }
            return retS;
        }else{
            throw new IOException("路径不是文件夹！");
        }
    }


    /***
     * 获取指定目录下的所有的文件（不包括文件夹），采用了递归
     *
     * param obj
     * @return
     */
    public static ArrayList<File> getListFiles(Object obj,String fileType) {
        File directory = null;
        if (obj instanceof File) {
            directory = (File) obj;
        } else {
            directory = new File(obj.toString());
        }
        ArrayList<File> files = new ArrayList<File>();
        if (directory.isFile()) {
            //获取最后一个.的位置
            int lastIndexOf = directory.getName().lastIndexOf(".");
            //获取文件的后缀名 .jpg
            String suffix = directory.getName().substring(lastIndexOf);

            if(fileType.equals("*.*")==true
                || fileType.toLowerCase().equals("*"+suffix.toLowerCase())==true
                ||(fileType.equals("*.xls*")==true &&suffix.toLowerCase().startsWith("*.xls")==true ))
                files.add(directory);
            return files;
        } else if (directory.isDirectory()) {
            File[] fileArr = directory.listFiles();
            for (int i = 0; i < fileArr.length; i++) {
                File fileOne = fileArr[i];
                files.addAll(getListFiles(fileOne,fileType));
            }
        }
        return files;
    }

    /**
     * 获取目录下的子目录
     * param obj
     * @return
     */
    public static ArrayList<File>GetDirectories(Object obj) {
        ArrayList<File> files = new ArrayList<File>();
        File directory = null;
        if (obj instanceof File) {
            directory = (File) obj;
        } else {
            directory = new File(obj.toString());
        }

        if (directory.isDirectory()) {
            File[] fileArr = directory.listFiles();
            for (int i = 0; i < fileArr.length; i++) {
                File fileOne = fileArr[i];
                if(fileOne.isDirectory())
                    files.add(fileOne);
            }
        }
        return files;
    }

    public static ArrayList<String> GetDirectories(String basePath) throws Exception{
        basePath = "BOOT-INF/classes/"+basePath;
        ArrayList<String> files = new ArrayList<String>();
        ClassLoader classLoader = BaseFileUtils.class.getClassLoader();
        URL url = classLoader.getResource(basePath);
        String urlStr = url.toString();
        // 找到!/ 截断之前的字符串
        String jarPath = urlStr.substring(0, urlStr.indexOf("!/") + 2);
        URL jarURL = new URL(jarPath);
        JarURLConnection jarCon = (JarURLConnection) jarURL.openConnection();
        JarFile jarFile = jarCon.getJarFile();
        Enumeration<JarEntry> jarEntrys = jarFile.entries();
        while (jarEntrys.hasMoreElements()) {
            JarEntry entry = jarEntrys.nextElement();
            // 简单的判断路径，如果想做到像Spring，Ant-Style格式的路径匹配需要用到正则。
            String name = entry.getName();
            if (name.startsWith(basePath) && !name.equals(basePath)) {
                if (entry.isDirectory()) {
                    // 文件夹 迭代
                    files.add(name);
                }
            }
        }
        return files;
    }


}
