/**
 * Project Name:app-engine-tools
 * File Name:FtpUtil.java
 * Package com.bwda.engine.common.utils
 * Date:2017年12月27日
 * Copyright (c) 2017, 江苏保旺达软件有限公司 All Rights Reserved.
 * @author fanchengliang
*/
package bp.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;

/**
 * FTP工具类
 * 
 * @author fanchenglaing
 */
public final class FtpUtil {
    
    private final Logger logger = LoggerFactory.getLogger(FtpUtil.class);
    
    public enum EnumFtpUtil {
        /**
         * 创建目录
         */
        CreateDirectory,
        /**
         * 删除目录
         */
        RemoveDirectory
    }
    
    /**
     * FTP服务器地址
     */
    private String ip = null;
    
    /**
     * FTP服务器端口
     */
    private int port = 21;
    
    /**
     * 用户名
     */
    private String user = "";
    
    /**
     * 密码
     */
    private String pass = "";
    
    /**
     * FTP客户端基类
     */
    private FTPClient ftpClient = null;
    
    /**
     * 是否关闭链接
     */
    private boolean closeConnection = false;
    
    /**
     * 工作路径
     */
    private String workingDirectory = "/";
    
    /**
     * FTP默认控制编码
     */
    private String controlEncoding = "GB2312";
    
    /**
     * 是否使用SFTP
     */
    private boolean isSFTP = false;
    
    /**
     * SFTP
     */
    private SftpUtil sftpUtil = null;
    
    /**
     * 路径
     */
    private String ftpPath = "";
    
    /**
     * 连接超时时间
     */
    private int timeout = 3000;
    
    /**
     * 默认构造函数
     */
    public FtpUtil() {
        
    }
    
    /**
     * 构造函数
     *
     * param ip 服务器地址
     * param port 服务器端口
     * param user 用户名
     * param pass 密码
     */
    public FtpUtil(String ip, int port, String user, String pass) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }
    
    /**
     * 建立一个FTP链接
     */
    public final String openConnection() {
        // 设置FTP链接对象
        if (this.ftpClient == null) {
            boolean booLogin = false;
            this.ftpClient = new FTPClient();
            if (this.timeout > 0) {
                ftpClient.setConnectTimeout(timeout);
            }
            this.ftpClient.setControlEncoding(controlEncoding);
            try {
                this.ftpClient.connect(this.ip, this.port);
                if (!FTPReply.isPositiveCompletion(this.ftpClient.getReplyCode())) {
                    this.ftpClient = null;
                    logger.info("FTP Server:{},Port{},MSG:{}", this.ip, String.valueOf(this.port), "服务器拒绝建立连接！");
                    return "err@服务器拒绝建立连接！";
                }
                else {
                    booLogin = this.ftpClient.login(this.user, this.pass);
                    if (!booLogin) {
                        this.ftpClient = null;
                        logger.info("FTP Server:{},Port{},MSG:{}", this.ip, String.valueOf(this.port), "检查用户名/密码是否正确");
                        return "err@检查用户名/密码是否正确";
                    }
                    else {
                        logger.info("FTP Server:{},Port{},MSG:{}", this.ip, String.valueOf(this.port), "FTP登录成功");
                        this.ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
                        this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        this.ftpClient.enterLocalPassiveMode();
                        return "FTP登录成功";
                    }
                }
            }
            catch (Exception e) {
                this.ftpClient = null;
                logger.error("{}", e.toString());
                return "err@FTP登录失败";
            }
        }
        return "FTP登录成功";
    }
    
    /**
     * 释放一个FTP链接
     */
    public final void releaseConnection() {
        if (!this.isSFTP) {
            if (this.ftpClient != null && this.ftpClient.isConnected()) {
                try {
                    this.ftpClient.disconnect();
                }
                catch (Exception e) {
                    logger.error("{}", e.toString());
                }
                finally {
                    logger.info("FTP Server:{},用户{}已退出！", this.ip, this.user);
                    this.ftpClient = null;
                }
            }
        }
        else {
            sftpUtil.releaseConnection();
            sftpUtil = null;
        }
    }
    
    /**
     * 设置登录后转到的目标文件夹
     * 
     * param strWorkingDirectory 工作目录路径
     * param booAutoCreate 自动创建目录
     * @return boolean true 成功  false 失败
     * @throws SftpException 
     */
    public final boolean changeWorkingDirectory(String strWorkingDirectory, boolean booAutoCreate) throws SftpException {
        boolean booResult = false;
        strWorkingDirectory = BaseFileUtils.formatSeparator(strWorkingDirectory);
        if (!this.isSFTP) {
            booResult = this.changeWorkingDirectoryToFtp(strWorkingDirectory, booAutoCreate);
        }
        else {
            sftpUtil.setControlEncoding(controlEncoding);
            booResult = sftpUtil.changeWorkingDirectory(strWorkingDirectory, booAutoCreate);
        }
        return booResult;
    }
    
    /**
     * 切换工作目录
     *
     * param strWorkingDirectory 工作目录路径
     * param booAutoCreate 自动创建目录
     * @return boolean true 成功  false 失败
     * @throws SftpException 
     */
    private final boolean changeWorkingDirectoryToFtp(String strWorkingDirectory, boolean booAutoCreate) throws SftpException {
        boolean booResult = false;
        if (StringUtils.isNotBlank(strWorkingDirectory)) {
            strWorkingDirectory = strWorkingDirectory.replaceAll("/{2,}", "/");
            logger.info("切换工作目录={}", strWorkingDirectory);
            try {
                this.openConnection();
                if (this.ftpClient != null) {
                    booResult = this.ftpClient.changeWorkingDirectory(strWorkingDirectory);
                    if (!booResult && booAutoCreate) {
                        booResult = this.managerDirectory(strWorkingDirectory, EnumFtpUtil.CreateDirectory);
                        booResult = this.ftpClient.changeWorkingDirectory(strWorkingDirectory);
                    }
                }
            }
            catch (IOException e) {
                booResult = false;
                logger.error("{}", e.toString());
            }
        }
        if (booResult) {
            this.workingDirectory = strWorkingDirectory;
        }
        logger.info("切换{},工作目录:{}", booResult ? "成功" : "失败", strWorkingDirectory);
        return booResult;
    }
    
    /**
     * 上传文件,并按照strRemoteFile 文件名称修改上传之后的文件名
     * 
     * param strRemoteFile 远程文件名
     * param strLocalFile 本地文件路径（含文件名）
     * @return boolean true 上传成功 false 上传失败
     * @throws Exception 
     */
    public final boolean uploadFile(String strRemoteFile, String strLocalFile) throws Exception {
        boolean booResult = false;
        strRemoteFile = BaseFileUtils.formatSeparator(strRemoteFile);
        if (!this.isSFTP) {
            booResult = this.uploadFileToFtp(strRemoteFile, strLocalFile);
        }
        else {
            booResult = sftpUtil.uploadFile(strRemoteFile, strLocalFile);
        }
        return booResult;
    }
    
    /**
     * 上传文件到FTP
     *
     * param strRemoteFile 远程文件名
     * param strLocalFile 本地文件路径（含文件名）
     * @return boolean true 上传成功 false 上传失败
     */
    private final boolean uploadFileToFtp(String strRemoteFile, String strLocalFile) {
        boolean booResult = false;
        InputStream inputStream = null;
        try {
            // 获得文件输入流
            inputStream = new FileInputStream(strLocalFile);
            // 开始上传文件
            this.openConnection();
            if (this.ftpClient != null) {
                booResult = this.ftpClient.storeFile(strRemoteFile, inputStream);
                inputStream.close();
                booResult = true;
                logger.info("FTP Server:{},工作目录:{},上传文件成功：{}", this.ip, this.workingDirectory, strRemoteFile);
            }
        }
        catch (Exception e) {
            logger.error("{}", e.toString());
            booResult = false;
        }
        finally {
            if (this.closeConnection == true) {
                this.releaseConnection();
            }
            inputStream = null;
        }
        return booResult;
    }
    
    /**
     * 上传文件,并按照strRemoteFile 文件名称修改上传之后的文件名
     * 
     * param strRemoteFile 远程文件名
     * param inputStreamLocal 本地文件输入流
     * @return boolean true 上传成功 false 上传失败
     */
    public final boolean uploadFile(String strRemoteFile, InputStream inputStreamLocal) {
        boolean booResult = false;
        strRemoteFile = BaseFileUtils.formatSeparator(strRemoteFile);
        if (!this.isSFTP) {
            booResult = this.uploadFileToFtp(strRemoteFile, inputStreamLocal);
        }
        else {
            booResult = sftpUtil.uploadFile(strRemoteFile, inputStreamLocal);
        }
        return booResult;
    }
    
    /**
     * 上传文件,并按照strRemoteFile 文件名称修改上传之后的文件名
     * 
     * param strRemoteFile 远程文件名
     * param inputStreamLocal 本地文件输入流
     * @return true 上传成功  false 上传失败
     */
    private final boolean uploadFileToFtp(String strRemoteFile, InputStream inputStreamLocal) {
        boolean booResult = true;
        try {
            // 开始上传文件
            this.openConnection();
            if (this.ftpClient != null) {
                booResult = this.ftpClient.storeFile(strRemoteFile, inputStreamLocal);
                logger.info("FTP Server:{},工作目录:{},上传文件成功：{}", this.ip, this.workingDirectory, strRemoteFile);
            }
        }
        catch (Exception e) {
            logger.error("{}", e.toString());
            booResult = false;
        }
        finally {
            if (this.closeConnection == true) {
                this.releaseConnection();
            }
            try {
                if (null != inputStreamLocal) {
                    inputStreamLocal.close();
                }
            }
            catch (IOException e) {
                booResult = false;
            }
        }
        return booResult;
    }
    
    /**
     * 上传文件,不修改上传之后的文件名
     * 
     * param strLocalFile 本地文件路径（含文件名）
     * @return true 上传成功 false 上传失败
     * @throws Exception 
     */
    public final boolean uploadFile(String strLocalFile) throws Exception {
        boolean booResult = false;
        if (!this.isSFTP) {
            booResult = this.uploadFileToFtp(strLocalFile);
        }
        else {
            booResult = sftpUtil.uploadFile(strLocalFile);
        }
        return booResult;
    }
    
    /**
     * 上传文件,不修改上传之后的文件名
     * 
     * param strLocalFile 本地文件路径（含文件名）
     * @return true 上传成功 false 上传失败
     * @throws Exception 
     */
    private final boolean uploadFileToFtp(String strLocalFile) throws Exception {
        String strRemoteFile = StringUtils.substringAfterLast(strLocalFile.replaceAll("\\\\", "/"), "/");
        return this.uploadFile(strRemoteFile, strLocalFile);
    }
    
    /**
     * 下载文件(并用strLocalFile命名)
     * 
     * param strRemoteFile 远程文件名
     * param strLocalFile 本地文件路径（含文件名）
     * @return boolean true 下载成功 false 下载失败
     */
    public final boolean downloadFile(String strRemoteFile, String strLocalFile) {
        boolean booResult = false;
        strRemoteFile = BaseFileUtils.formatSeparator(strRemoteFile);
        strLocalFile = BaseFileUtils.formatSeparator(strLocalFile);
        if (!this.isSFTP) {
            booResult = this.downloadFileToFtp(strRemoteFile, strLocalFile);
        }
        else {
            booResult = sftpUtil.downloadFile(strRemoteFile, strLocalFile);
        }
        return booResult;
    }
    
    /**
     * 下载文件(并用strLocalFile命名)
     * 
     * param strRemoteFile 远程文件名
     * param strLocalFile 本地文件路径（含文件名）
     * @return boolean true 下载成功 false 下载失败
     */
    private final boolean downloadFileToFtp(String strRemoteFile, String strLocalFile) {
        boolean booResult = false;
        File fileOut = new File(strLocalFile);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileOut);
            this.openConnection();
            if (this.ftpClient != null) {
                if (this.file(strRemoteFile) != null) {
                    booResult = this.ftpClient.retrieveFile(strRemoteFile, outputStream);
                    outputStream.close();
                    booResult = true;
                    logger.info("FTP Server:{},工作目录:{},下载文件成功：{}", this.ip, this.workingDirectory, strRemoteFile);
                }
            }
        }
        catch (Exception e) {
            booResult = false;
            logger.error("{}", e.toString());
        }
        finally {
            if (this.closeConnection == true) {
                this.releaseConnection();
            }
            fileOut = null;
            outputStream = null;
        }
        return booResult;
    }
    
    /**
     * 下载文件(不修改文件名)
     * 
     * param strRemoteFile 远程文件名
     * @return boolean true 下载成功 false 下载失败
     */
    public final boolean downloadFile(String strRemoteFile) {
        boolean booResult = false;
        strRemoteFile = BaseFileUtils.formatSeparator(strRemoteFile);
        if (!this.isSFTP) {
            booResult = this.downloadFileToFtp(strRemoteFile);
        }
        else {
            booResult = sftpUtil.downloadFile(strRemoteFile);
        }
        return booResult;
    }
    
    /**
     * 下载文件(不修改文件名)
     * 
     * param strRemoteFile 远程文件名
     * @return boolean true 下载成功 false 下载失败
     */
    private final boolean downloadFileToFtp(String strRemoteFile) {
        return this.downloadFile(strRemoteFile, strRemoteFile);
    }
    
    /**
     * 获得下载文件二进制流
     * 
     * param strRemoteFile 远程文件名
     * @return 文件二进制流
     */
    public final ByteArrayInputStream downloadFileAsStream(String strRemoteFile) {
        ByteArrayInputStream byteArrayInputStream = null;
        strRemoteFile = BaseFileUtils.formatSeparator(strRemoteFile);
        if (this.isSFTP == false) {
            byteArrayInputStream = this.downloadFileAsStreamToFtp(strRemoteFile);
        }
        else {
            byteArrayInputStream = sftpUtil.downloadFileAsStream(strRemoteFile);
        }
        return byteArrayInputStream;
    }
    
    /**
     * 获得下载文件二进制流
     * 
     * param strRemoteFile 远程文件名
     * @return 件二进制流
     */
    public final ByteArrayInputStream downloadFileAsStreamToFtp(String strRemoteFile) {
        ByteArrayInputStream byteArrayInputStream = null;
        strRemoteFile = BaseFileUtils.formatSeparator(strRemoteFile);
        try {
            this.openConnection();
            if (this.ftpClient != null) {
                if (this.file(strRemoteFile) != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    this.ftpClient.retrieveFile(strRemoteFile, byteArrayOutputStream);
                    byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                }
            }
        }
        catch (Exception e) {
            logger.error("{}", e.toString());
        }
        finally {
            if (this.closeConnection == true) {
                this.releaseConnection();
            }
        }
        return byteArrayInputStream;
    }
    
    /**
     * 文件列表 显示storePath下的文件夹和文件
     * 
     * @return FTPFile[] 文件数组
     */
    public final FTPFile[] fileList() {
        FTPFile[] ftpFileArray = null;
        if (this.isSFTP == false) {
            ftpFileArray = this.fileListToFtp();
        }
        else {
            Vector<LsEntry> vector = sftpUtil.fileList();
            if (null != vector && !vector.isEmpty()) {
                ftpFileArray = new FTPFile[vector.size()];
                FTPFile ftpFile = null;
                for (int i = 0; i < ftpFileArray.length; i++) {
                    ftpFile = new FTPFile();
                    this.cloneFile(ftpFile, vector.get(i));
                    ftpFileArray[i] = ftpFile;
                }
            }
        }
        if (ftpFileArray != null) {
            // 排序
            try {
                Arrays.sort(ftpFileArray, new Comparator<FTPFile>() {
                    @Override
                    public int compare(FTPFile file1, FTPFile file2) {
                        if (file1.getTimestamp().getTime().getTime() >= file2.getTimestamp().getTime().getTime()) {
                            return -1;
                        }
                        else {
                            return 1;
                        }
                    }
                });
            }
            catch (IllegalArgumentException e) {
                logger.error("{文件排序发生问题！}", e.toString());
            }
        }
        return ftpFileArray;
    }
    
    /**
     * 克隆文件属性
     *
     * param ftpFile ftp文件
     * param lsEntry 属性
     */
    private void cloneFile(FTPFile ftpFile, LsEntry lsEntry) {
        long date = 0;
        Calendar calendar = null;
        ftpFile.setName(lsEntry.getFilename());
        ftpFile.setSize(lsEntry.getAttrs().getSize());
        date = Long.valueOf(lsEntry.getAttrs().getATime());
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date * 1000);
        ftpFile.setTimestamp(calendar);
        if (lsEntry.getAttrs().isDir()) {
            ftpFile.setType(FTPFile.DIRECTORY_TYPE);
        }
        else {
            ftpFile.setType(FTPFile.FILE_TYPE);
        }
        
    }
    
    /**
     * 文件列表 显示storePath下的文件夹和文件
     * 
     * @return FTPFile[] 文件数组
     */
    private final FTPFile[] fileListToFtp() {
        FTPFile[] ftpFileArray = null;
        try {
            this.openConnection();
            ftpFileArray = this.ftpClient.listFiles();
            if (ftpFileArray == null || ftpFileArray.length == 0) {
                logger.info("当前登录用户：{},目录：{},下面文件夹和文件数目为空", this.user, this.workingDirectory);
            }
            else {
                logger.info("ftpFileArray!=null");
                for (FTPFile ftpFile : ftpFileArray) {
                    logger.info(ftpFile.getName());
                }
            }
        }
        catch (IOException e) {
            logger.error("{}", e.toString());
            ftpFileArray = null;
        }
        finally {
            if (this.closeConnection == true) {
                this.releaseConnection();
            }
        }
        return ftpFileArray;
    }
    
    /**
     * 根据指定的文件名称查询FTP上面的文件
     * 
     * param strFileName 文件名
     * @return FTP文件
     */
    public final FTPFile file(String strFileName) {
        FTPFile ftpFile = null;
        strFileName = BaseFileUtils.formatSeparator(strFileName);
        if (this.isSFTP == false) {
            ftpFile = this.fileToFtp(strFileName);
        }
        else {
            LsEntry lsEntry = sftpUtil.file(strFileName);
            if (lsEntry != null) {
                ftpFile = new FTPFile();
                this.cloneFile(ftpFile, lsEntry);
            }
        }
        return ftpFile;
    }
    
    /**
     * 根据指定的文件名称查询FTP上面的文件
     * 
     * param strFileName 文件名
     * @return FTP文件
     */
    private final FTPFile fileToFtp(String strFileName) {
        FTPFile ftpFile = null;
        FTPFile[] ftpFileArray = null;
        try {
            this.openConnection();
            ftpFileArray = this.ftpClient.listFiles(strFileName);
            if (ftpFileArray != null && ftpFileArray.length == 1) {
                ftpFile = ftpFileArray[0];
            }
            else {
                logger.info("指定的FTP文件不存在，路径：{},文件名：{}。", this.workingDirectory, strFileName);
            }
        }
        catch (IOException e) {
            logger.error("{}", e.toString());
            ftpFileArray = null;
        }
        return ftpFile;
    }
    
    /**
     * 删除服务器端文件
     * 
     * param strFileName 文件名
     * @return boolean 删除结果
     * @throws SftpException 
     */
    public final boolean deleteFile(String strFileName) throws SftpException {
        boolean booResult = false;
        strFileName = BaseFileUtils.formatSeparator(strFileName);
        if (!this.isSFTP) {
            booResult = this.deleteFileToFtp(strFileName);
        }
        else {
            booResult = sftpUtil.deleteFile(strFileName);
        }
        return booResult;
    }
    
    /**
     * 删除服务器端文件
     * 
     * param strFileName 文件名
     * @return boolean 删除结果
     */
    private final boolean deleteFileToFtp(String strFileName) {
        boolean booResult = false;
        if (strFileName == null || strFileName.trim().length() <= 0) {
            logger.error("{}", "被删除文件名不能为空.");
        }
        else {
            try {
                this.openConnection();
                if (this.ftpClient != null) {
                    booResult = this.ftpClient.deleteFile(strFileName);
                    logger.info("FTP Server:{},工作目录:{},删除文件成功：{}", this.ip, this.workingDirectory, strFileName);
                }
            }
            catch (IOException e) {
                booResult = false;
                logger.error("{}", e.toString());
            }
            finally {
                if (this.closeConnection == true) {
                    this.releaseConnection();
                }
            }
        }
        return booResult;
    }
    
    /**
     * 文件重命名
     * 
     * param oldName 原名称
     * param newName 新名称
     * @return boolean 操作结果 true 成功 false 失败
     */
    public final boolean rename(String oldName, String newName) {
        boolean booResult = false;
        oldName = BaseFileUtils.formatSeparator(oldName);
        newName = BaseFileUtils.formatSeparator(newName);
        if (!this.isSFTP) {
            booResult = this.renameToFtp(oldName, newName);
        }
        else {
            booResult = sftpUtil.rename(oldName, newName);
        }
        return booResult;
    }
    
    /**
     * 文件重命名
     * 
     * param oldName 原名称
     * param newName 新名称
     * @return boolean 操作结果 true 成功 false 失败
     */
    private final boolean renameToFtp(String oldName, String newName) {
        boolean booResult = false;
        try {
            this.openConnection();
            if (this.ftpClient != null) {
                booResult = ftpClient.rename(oldName, newName);
                logger.info("FTP Server:{},工作目录:{},文件重命名成功：{}", this.ip, this.workingDirectory, newName);
            }
        }
        catch (IOException e) {
            booResult = false;
            logger.error("{}", e.toString());
        }
        finally {
            if (this.closeConnection == true) {
                this.releaseConnection();
            }
        }
        return booResult;
    }
    
    /**
     * 创建或删除目录
     * 
     * param strWorkingDirectory 工作目录
     * param enumFtpUtil 操作类型
     * @return boolean 操作结果 true 成功 false 失败
     * @throws SftpException 
     */
    public final boolean managerDirectory(String strWorkingDirectory, EnumFtpUtil enumFtpUtil) throws SftpException {
        boolean booResult = false;
        strWorkingDirectory = BaseFileUtils.formatSeparator(strWorkingDirectory);
        if (this.isSFTP == false) {
            booResult = this.managerDirectoryToFtp(strWorkingDirectory, enumFtpUtil);
        }
        else {
            if (enumFtpUtil == EnumFtpUtil.CreateDirectory) {
                booResult = sftpUtil.managerDirectory(strWorkingDirectory, SftpUtil.EnumFtpUtil.CreateDirectory);
            }
            else {
                booResult = sftpUtil.managerDirectory(strWorkingDirectory, SftpUtil.EnumFtpUtil.RemoveDirectory);
            }
        }
        return booResult;
    }
    
    /**
     * 创建或删除目录
     * 
     * param strWorkingDirectory 工作目录
     * param enumFtpUtil 操作类型
     * @return boolean 操作结果 true 成功 false 失败
     * @throws SftpException 
     */
    private final boolean managerDirectoryToFtp(String strWorkingDirectory, EnumFtpUtil enumFtpUtil) throws SftpException {
        boolean booResult = true;
        if (StringUtils.isNotBlank(strWorkingDirectory)) {
            strWorkingDirectory = strWorkingDirectory.replaceAll("\\\\", "/");
            try {
                this.openConnection();
                if (enumFtpUtil == EnumFtpUtil.CreateDirectory) {
                    String[] strArrayPath = StringUtils.split(strWorkingDirectory, "/");
                    for (String strPath : strArrayPath) {
                        if (StringUtils.isNotBlank(strPath)) {
                            if (this.ftpClient.changeWorkingDirectory("./" + strPath) == false) {
                                this.ftpClient.makeDirectory(strPath);
                                this.ftpClient.changeWorkingDirectory("./" + strPath);
                            }
                        }
                    }
                    logger.info("FTP Server:{},工作目录:{},创建目录成功：{}", this.ip, this.workingDirectory, strWorkingDirectory);
                }
                else if (enumFtpUtil == EnumFtpUtil.RemoveDirectory) {
                    booResult = this.removeAll(strWorkingDirectory);
                    logger.info("FTP Server:{},工作目录:{},删除目录成功：{}", this.ip, this.workingDirectory, strWorkingDirectory);
                }
            }
            catch (IOException e) {
                booResult = false;
                logger.error("{}", e.toString());
            }
            finally {
                if (this.closeConnection == true) {
                    this.releaseConnection();
                }
            }
        }
        return booResult;
    }
    
    /**
     * 删除目录
     *
     * param folderDir 目录
      * @return boolean 操作结果 true 成功 false 失败
     * @throws SftpException 
     */
    private boolean removeAll(String folderDir) throws SftpException {
        try {
            FTPFile[] files = ftpClient.listFiles(folderDir);
            for (FTPFile f : files) {
                if (f.isDirectory() && !StringUtils.equals(".", f.getName()) && !StringUtils.equals("..", f.getName())) {
                    this.removeAll(folderDir + "/" + f.getName());
                    ftpClient.removeDirectory(folderDir + "/" + f.getName());
                }
                if (f.isFile()) {
                	
                    this.deleteFile(folderDir + "/" + f.getName());
                }
            }
            ftpClient.removeDirectory(folderDir);
        }
        catch (IOException e) {
            logger.error("{}", e.toString());
            return false;
        }
        return true;
    }
    
    /**
     * 获得远程文件的文件头信息，前4个字节
     * 
     * param strRemoteFile 远程文件名
     * @return boolean 操作结果 true 成功  false 失败
     */
    public final byte[] getFileHeader(String strRemoteFile) {
        byte[] byteArray = null;
        strRemoteFile = BaseFileUtils.formatSeparator(strRemoteFile);
        if (this.isSFTP == false) {
            byteArray = this.getFileHeaderToFtp(strRemoteFile);
        }
        else {
            byteArray = sftpUtil.getFileHeader(strRemoteFile);
        }
        return byteArray;
    }
    
    /**
     * 获得远程文件的文件头信息，前4个字节
     * 
     * param strRemoteFile 远程文件名
     * @return boolean 操作结果 true 成功  false 失败
     */
    private final byte[] getFileHeaderToFtp(String strRemoteFile) {
        byte[] byteArray = null;
        try {
            this.openConnection();
            if (this.ftpClient != null) {
                if (this.file(strRemoteFile) != null) {
                    InputStream inputStream = ftpClient.retrieveFileStream(strRemoteFile);
                    byteArray = new byte[4];
                    inputStream.read(byteArray);
                }
            }
        }
        catch (Exception e) {
            logger.error("{}", e.toString());
        }
        finally {
            if (this.closeConnection == true) {
                this.releaseConnection();
            }
        }
        return byteArray;
    }
    
    /**
     * 在FTP内部复制文件
     * 
     * param sourceFile 源文件名
     * param targetFile 目标文件名
     * @return boolean 操作结果 true 成功  false 失败
     */
    public final boolean copyFile(String sourceFile, String targetFile) {
        boolean booResult = false;
        sourceFile = BaseFileUtils.formatSeparator(sourceFile);
        targetFile = BaseFileUtils.formatSeparator(targetFile);
        if (!this.isSFTP) {
            booResult = this.copyFileToFtp(sourceFile, targetFile);
        }
        else {
            booResult = sftpUtil.copyFile(sourceFile, targetFile);
        }
        return booResult;
    }
    
    /**
     * 在FTP内部复制文件
     * 
     * param sourceFile 源文件名
     * param targetFile 目标文件名
     * @return boolean 操作结果 true 成功  false 失败
     */
    private final boolean copyFileToFtp(String sourceFile, String targetFile) {
        boolean booResult = false;
        try {
            this.openConnection();
            if (this.ftpClient != null) {
                String strDir = StringUtils.substringBeforeLast(sourceFile, "/");
                if (StringUtils.isNotBlank(strDir)) {
                    this.changeWorkingDirectory(strDir, false);
                }
                if (this.file(sourceFile) != null) {
                    InputStream in = this.downloadFileAsStream(sourceFile);
                    strDir = StringUtils.substringBeforeLast(targetFile, "/");
                    if (StringUtils.isNotBlank(strDir)) {
                        this.changeWorkingDirectory(strDir, false);
                    }
                    String strSuffix = StringUtils.substringAfterLast(targetFile, ".");
                    if (this.file(targetFile) != null) {
                        String strSubNewFileName = new SimpleDateFormat("_MMddHHmmss").format(new Date());
                        targetFile =
                            StringUtils.substringBeforeLast(targetFile, ".").replaceAll("_\\d{10,}", "")
                                + strSubNewFileName;
                        if (StringUtils.isNotBlank(strSuffix)) {
                            targetFile += "." + strSuffix;
                        }
                    }
                    booResult = this.uploadFile(targetFile, in);
                }
                else {
                    logger.error("源文件不存在:{}", sourceFile);
                }
            }
        }
        catch (Exception e) {
            booResult = false;
            logger.error("{}", e.toString());
        }
        finally {
            if (this.closeConnection == true) {
                this.releaseConnection();
            }
        }
        return booResult;
    }
    
    /**
     * 在FTP内部移动文件
     * 
     * param sourceFile 源文件名
     * param targetFile 目标文件名
     * @return boolean 操作结果 true 成功  false 失败
     */
    public final boolean moveFile(String sourceFile, String targetFile) {
        boolean booResult = false;
        sourceFile = BaseFileUtils.formatSeparator(sourceFile);
        targetFile = BaseFileUtils.formatSeparator(targetFile);
        if (!this.isSFTP) {
            booResult = this.moveFileToFtp(sourceFile, targetFile);
        }
        else {
            booResult = sftpUtil.moveFile(sourceFile, targetFile);
        }
        return booResult;
    }
    
    /**
     * 在FTP内部移动文件
     * 
     * param sourceFile 源文件名
     * param targetFile 目标文件名
     * @return boolean 操作结果 true 成功  false 失败
     */
    private final boolean moveFileToFtp(String sourceFile, String targetFile) {
        boolean booResult = false;
        try {
            this.openConnection();
            if (this.ftpClient != null) {
                booResult = this.copyFile(sourceFile, targetFile);
                if (booResult) {
                    booResult = this.deleteFile(sourceFile);
                }
            }
        }
        catch (Exception e) {
            booResult = false;
            logger.error("{}", e.toString());
        }
        finally {
            if (this.closeConnection == true) {
                this.releaseConnection();
            }
        }
        return booResult;
    }
    
    
    /**
     * 上传目录
     *
     * param sPath 上传源目录
     * param dPath 上传目的目录
     */
    public void upLoadDir(String sPath, String dPath) {
        
        if (!this.isSFTP) {
            this.upLoadDirToFtp(sPath, dPath);
        }
        else {
            sftpUtil.upLoadDir(sPath, dPath);
        }
    }
    
    /**
     * 上传目录
     *
     * param sPath 上传源目录
     * param dPath 上传目的目录
     */
    public void upLoadDirToFtp(String sPath, String dPath) {
        
        try {
            this.openConnection();
            
            // 尝试进入目录
            this.changeWorkingDirectoryToFtp(dPath, true);
            
            File file = new File(sPath);
            // 上传文件到当前目录
            uploadFile(file, this.workingDirectory);
            
        } catch (Exception e) {
            logger.error("上传目录失败:{}", e.getMessage());
        } finally {
            if (this.closeConnection) {
                this.releaseConnection();
            }
        }
    }
    
    /**
     * 上传文件(目录)到当前目录
     *
     * param file 要上传的目录或文件
     * param workingDirectory ftp当前目录
     * @throws SftpException 
     */
    private void uploadFile(File file, String workingDirectory) throws SftpException {
        
        // 如果是目录
        if (file.isDirectory()) {
            // 列出文件
            File[] list = file.listFiles();
            String fileName = file.getName();
            
            // 尝试进入目录
            this.changeWorkingDirectoryToFtp(workingDirectory, false);
            logger.info("正在创建目录:{}/{}", workingDirectory, fileName);
            this.managerDirectoryToFtp(fileName, EnumFtpUtil.CreateDirectory);
            logger.info("成功创建目录:{}/{}", workingDirectory, fileName);
            
            // 进入目录
            workingDirectory = workingDirectory + "/" + file.getName();
            this.changeWorkingDirectoryToFtp(fileName, false);
            
            // 迭代处理：上传文件(目录)到当前目录
            for (int i = 0; i < list.length; i++) {
                uploadFile(list[i], workingDirectory);
            }
        }
        // 是文件
        else {
            // 进入当前目录
            this.changeWorkingDirectoryToFtp(workingDirectory, false);
            
            logger.info("正在复制文件:{}", file.getAbsolutePath());
            
            InputStream inputStream = null;
            try {
                // 获得文件输入流
                inputStream = new FileInputStream(file);
                // 开始上传文件
                this.openConnection();
                if (this.ftpClient != null) {
                    this.ftpClient.storeFile(file.getName(), inputStream);
                    inputStream.close();
                    logger.info("FTP Server:{},工作目录:{},上传文件成功：{}", this.ip, this.workingDirectory, file.getName());
                }
            }
            catch (Exception e) {
                logger.error("{}", e.toString());
            }
            finally {
                if (this.closeConnection == true) {
                    this.releaseConnection();
                }
                inputStream = null;
            }
        }
    }
    
    /**
     * 下载目录
     *
     * param sPath 下载源目录
     * param dPath 下载目的目录
     */
    public void downLoadDir(String sPath, String dPath) {
        
        sPath = BaseFileUtils.formatSeparator(sPath);
        dPath = BaseFileUtils.formatSeparator(dPath);
        if (!sPath.endsWith(Constant4Comm.STR_SLASH)) {
            sPath = sPath + Constant4Comm.STR_SLASH;
        }
        if (!dPath.endsWith(Constant4Comm.STR_SLASH)) {
            dPath = dPath + Constant4Comm.STR_SLASH;
        }
        
        try {
            this.openConnection();
            this.changeWorkingDirectoryToFtp(sPath, false);
            
            FTPFile[] ftpFiles = this.fileList();
            
            if (ftpFiles != null && ftpFiles.length > 0) {
                for (FTPFile ftpFile : ftpFiles) {
                    // 如果是目录
                    if (ftpFile.isDirectory() && !StringUtils.equals(".", ftpFile.getName())
                        && !StringUtils.equals("..", ftpFile.getName())) {
                        // 创建本地目录
                        BaseFileUtils.createDirectory(dPath + ftpFile.getName());
                        // 确保链接打开
                        this.openConnection();
                        this.changeWorkingDirectoryToFtp(sPath + ftpFile.getName(), true);
                        // 递归调用下载目录
                        downLoadDir(sPath + ftpFile.getName(), dPath + ftpFile.getName());
                    }
                    // 下载文件
                    else {
                        BaseFileUtils.createDirectory(dPath);
                        downloadFile(sPath + ftpFile.getName(), dPath + ftpFile.getName());
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("下载目录失败:{}", e.getMessage());
        } finally {
            if (this.closeConnection) {
                this.releaseConnection();
            }
        }
    }
    
    /**
     * 取得ftp客户端
     *
     * @return ftp客户端
     */
    public final FTPClient getFtpClient() {
        return ftpClient;
    }
    
    /**
     * 获得：是否关闭FTP链接，默认为是。
     * 
     * @return boolean 操作结果 true 成功  false 失败
     */
    public final boolean isCloseConnection() {
        return closeConnection;
    }
    
    /**
     * 设置：是否关闭FTP链接，默认为是。
     * 
     * param closeConnection 是否关闭连接
     */
    public final void setCloseConnection(boolean closeConnection) {
        this.closeConnection = closeConnection;
    }
    
    /**
     * 获得默认控制编码,默认为GBK，支持中文目录及中文文件名传输
     * 
     * @return  获得控制编码,
     */
    public String getControlEncoding() {
        return controlEncoding;
    }
    
    /**
     * 设置默认控制编码,默认为GBK，支持中文目录及中文文件名传输
     * 
     * param controlEncoding 获得控制编码,
     */
    public void setControlEncoding(String controlEncoding) {
        if (StringUtils.isNotBlank(controlEncoding)) {
            this.controlEncoding = controlEncoding;
        }
    }
    
    /**
     * 是否使用SFTP,默认为false
     * 
     * @return true 是SFTP  fasle 否
     */
    public final boolean isSFTP() {
        return isSFTP;
    }
    
    /**
     * 是否使用SFTP,默认为false
     * 
     * param isSFTP true SFTP  fasle FTP
     */
    public final void setSFTP(boolean isSFTP) {
        this.isSFTP = isSFTP;
        this.sftpUtil = new SftpUtil(this.ip, this.port, this.user, this.pass);
        this.sftpUtil.setTimeout(this.timeout);
    }
    
    /**
     * 获得：路径
     * 
     * @return 路径
     */
    public final String getFtpPath() {
        return ftpPath;
    }
    
    /**
     * 设置：路径
     * 
     * param ftpPath 路径
     */
    
    public final void setFtpPath(String ftpPath) {
        if (ftpPath != null) {
            this.ftpPath = ftpPath;
        }
    }
    
    /**
     * 设置：连接超时时间
     * 
     * param timeout 连接超时时间
     */
    public final void setTimeout(int timeout) {
        this.timeout = timeout;
        if (this.sftpUtil != null) {
            this.sftpUtil.setTimeout(this.timeout);
        }
    }
    
    /*
     * 测试代码
    public static void main(String[] args) {
        long lonStart = System.currentTimeMillis();
        FtpUtil ftpUtil = new FtpUtil("192.168.2.129", 21, "pms", "1qaz2wsx");
        ftpUtil.setCloseConnection(false);
        try {
            ftpUtil.setControlEncoding("UTF-8");
            ftpUtil.upLoadDir("D:/test_sftp/aa", "");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            ftpUtil.releaseConnection();
            ftpUtil = null;
        }
        long lonEnd = System.currentTimeMillis();
        System.out.println(lonEnd - lonStart);
        
    }
    */
    
}


