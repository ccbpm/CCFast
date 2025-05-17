/**
 * Project Name:app-engine-tools
 * File Name:SftpUtil.java
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
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * SFTP工具类
 * 
 * @author fanchengliang
 */
public class SftpUtil {
	private final Logger logger = LoggerFactory.getLogger(SftpUtil.class);

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
	private int port = 22;

	/**
	 * 用户名
	 */
	private String user = "";

	/**
	 * 密码
	 */
	private String pass = "";

	/**
	 * SFTP客户端基类
	 */
	private ChannelSftp sftp = null;

	/**
	 * 是否关闭链接
	 */
	private boolean closeConnection = false;

	/**
	 * 工作路径
	 */
	private String workingDirectory = "/";

	/**
	 * SFTP默认控制编码
	 */
	private String controlEncoding = "GB2312";

	/**
	 * 连接超时时间
	 */
	private int timeout = 3000;

	/**
	 * 默认构造函数
	 */
	public SftpUtil() {

	}

	/**
	 * 初始化连接参数
	 * 
	 * 
	 */
	public SftpUtil(String ip, int port, String user, String pass) {
		// 获取SFTP连接信息
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.pass = pass;
	}

	/**
	 * 建立一个SFTP链接
	 */
	private void openConnection() {

		if (this.sftp == null) {
			sftp = new ChannelSftp();
		}
		if (this.sftp.isConnected()) {
			return;
		}
		JSch jsch = new JSch();
		try {
			Session sshSession = jsch.getSession(user, ip, port);
			logger.info("Session创建");
			sshSession.setPassword(pass);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			// 设置超时时间为
			if (this.timeout <= 0) {
				sshSession.connect();
			} else {
				sshSession.connect(this.timeout);
			}
			logger.info("SFTP Server:{},Port{},MSG:{}", this.ip, String.valueOf(this.port), "SFTP登录成功");
			Channel channel = sshSession.openChannel("sftp");
			if (this.timeout <= 0) {
				channel.connect();
			} else {
				channel.connect(this.timeout);
			}
			sftp = (ChannelSftp) channel;
			sftp.setFilenameEncoding(controlEncoding);
		} catch (Exception e) {
			logger.error("{}", e.toString());
		}
	}

	/**
	 * 释放一个SFTP链接
	 */
	public void releaseConnection() {
		if (null != sftp) {
			sftp.disconnect();
			try {
				if (null != sftp.getSession()) {
					sftp.getSession().disconnect();
				}
			} catch (JSchException e) {
				logger.error("{}", e.toString());
			}
		}
	}

	/**
	 * 设置登录后转到的目标文件夹
	 * 
	 * param strWorkingDirectory
	 *            工作目录路径
	 * param booAutoCreate
	 *            自动创建目录
	 * @return boolean true 成功 false 失败
	 */
	public final boolean changeWorkingDirectory(String strWorkingDirectory, boolean booAutoCreate) {
		boolean booResult = true;
		if (StringUtils.isNotBlank(strWorkingDirectory)) {
			strWorkingDirectory = strWorkingDirectory.replaceAll("/{2,}", "/");
			logger.info("切换工作目录={}", strWorkingDirectory);
			try {
				this.openConnection();
				if (null != sftp) {
					booResult = innerChangeDirectory("/bpm/"+strWorkingDirectory, booAutoCreate);
				}
			} catch (Exception e) {
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
	 * innerChangeDirectory:(切换工作目录内部方法).
	 * 
	 * param strWorkingDirectory
	 *            工作目录路径
	 * param booAutoCreate
	 *            自动创建目录
	 * @return boolean true 成功 false 失败
	 * @throws SftpException 
	 */
	private boolean innerChangeDirectory(String strWorkingDirectory, boolean booAutoCreate) throws SftpException{
		 boolean booResult;
		 this.openConnection();
        try {
            sftp.cd(strWorkingDirectory);
            booResult = true;
        }
        catch (SftpException e) {
            booResult = false;
            if (booAutoCreate) {
                String[] strArrayDirs = StringUtils.split(strWorkingDirectory, "/");
                if(strWorkingDirectory.startsWith("/")){
                    strArrayDirs[0]="/"+strArrayDirs[0];
                }
                for (String strDir : strArrayDirs) {
                    try {
						sftp.cd(strDir);
                    }
                    catch (Exception e1) {
                        sftp.mkdir(strDir);
						sftp.cd(strDir);
                    }

                }
                sftp.cd(strWorkingDirectory);
                booResult = true;
            }
        }
	    return booResult;
	}

	/**
	 * 上传文件,并按照strRemoteFile 文件名称修改上传之后的文件名
	 * 
	 * param strRemoteFile
	 *            远程文件名
	 * param strLocalFile
	 *            本地文件路径（含文件名）
	 * @return boolean true 上传成功 false 上传失败
	 * @throws Exception
	 */
	public final boolean uploadFile(String strRemoteFile, String strLocalFile) throws Exception {
		boolean booResult = false;
		// strRemoteFile="\\"+strRemoteFile;
		InputStream inputStream = null;
		try {
			// 获得文件输入流
			inputStream = new FileInputStream(strLocalFile);
			// 开始上传文件
			this.openConnection();
			if (this.sftp != null) {
				sftp.put(inputStream, strRemoteFile);
				inputStream.close();
				booResult = true;
				logger.info("FTP Server:{},工作目录:{},上传文件成功：{}", this.ip, this.workingDirectory, strRemoteFile);
			}
		} catch (Exception e) {
			logger.error("{}", e.toString());
			booResult = false;
			throw e;
		} finally {
			if (this.closeConnection) {
				this.releaseConnection();
			}
			inputStream = null;
		}
		return booResult;
	}

	/**
	 * 上传文件,并按照strRemoteFile 文件名称修改上传之后的文件名
	 * 
	 * param strRemoteFile
	 *            远程文件名
	 * param inputStreamLocal
	 *            本地文件输入流
	 * @return boolean true 上传成功 false 上传失败
	 */
	public final boolean uploadFile(String strRemoteFile, InputStream inputStreamLocal) {
		boolean booResult = true;
		try {
			// 开始上传文件
			this.openConnection();
			if (this.sftp != null) {
				sftp.put(inputStreamLocal, strRemoteFile);
				logger.info("FTP Server:{},工作目录:{},上传文件成功：{}", this.ip, this.workingDirectory, strRemoteFile);
			}
		} catch (Exception e) {
			logger.error("{}", e.toString());
			booResult = false;
		} finally {
			try {
				if (this.closeConnection) {
					this.releaseConnection();
				}
				if (null != inputStreamLocal) {
					inputStreamLocal.close();
				}
			} catch (Exception e2) {
				booResult = false;
			}
		}
		return booResult;
	}

	/**
	 * 上传文件,不修改上传之后的文件名
	 * 
	 * param strLocalFile
	 *            本地文件路径（含文件名）
	 * @return true 上传成功 false 上传失败
	 * @throws Exception
	 */
	public final boolean uploadFile(String strLocalFile) throws Exception {
		String strRemoteFile = StringUtils.substringAfterLast(strLocalFile.replaceAll("\\\\", "/"), "/");
		return this.uploadFile(strRemoteFile, strLocalFile);
	}

	/**
	 * 下载文件(并用strLocalFile命名)
	 * 
	 * param strRemoteFile
	 *            远程文件名
	 * param strLocalFile
	 *            本地文件路径（含文件名）
	 * @return boolean true 下载成功 false 下载失败
	 */
	public final boolean downloadFile(String strRemoteFile, String strLocalFile) {
		boolean booResult = false;
		File fileOut = new File(strLocalFile);
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fileOut);
			this.openConnection();
			if (this.sftp != null) {
				if (this.file(strRemoteFile) != null) {
					this.sftp.get(strRemoteFile, outputStream);
					outputStream.close();
					booResult = true;
					logger.info("FTP Server:{},工作目录:{},下载文件成功：{}", this.ip, this.workingDirectory, strRemoteFile);
				}
			}
		} catch (Exception e) {
			booResult = false;
			logger.error("{}", e.toString());
		} finally {
			if (this.closeConnection) {
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
	 * param strRemoteFile
	 *            远程文件名
	 * @return boolean true 下载成功 false 下载失败
	 */
	public final boolean downloadFile(String strRemoteFile) {
		return this.downloadFile(strRemoteFile, strRemoteFile);
	}

	/**
	 * 下载文件(不修改文件名)
	 * 
	 * param strRemoteFile
	 *            远程文件名
	 * @return 文件二进制流
	 */
	public final ByteArrayInputStream downloadFileAsStream(String strRemoteFile) {
		ByteArrayInputStream byteArrayInputStream = null;
		try {
			this.openConnection();
			if (this.sftp != null) {
				if (this.file(strRemoteFile) != null) {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					this.sftp.get(strRemoteFile, byteArrayOutputStream);
					byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
				}
			}
		} catch (Exception e) {
			logger.error("{}", e.toString());
		} finally {
			if (this.closeConnection) {
				this.releaseConnection();
			}
		}
		return byteArrayInputStream;
	}

	/**
	 * 根据指定的文件名称查询SFTP上面的文件
	 * 
	 * param strFileName
	 *            远程文件名
	 * @return SFTP上面的文件
	 */
	@SuppressWarnings("unchecked")
	public final LsEntry file(String strFileName) {
		LsEntry lsEntry = null;
		try {
			this.openConnection();
			Vector<LsEntry> vector = this.sftp.ls(strFileName);
			if (vector != null && !vector.isEmpty()) {
				lsEntry = (LsEntry) vector.get(0);
			}
		} catch (Exception e) {
			logger.error("{}", e.toString());
		}
		if (lsEntry == null) {
			logger.info("指定的FTP文件不存在，路径：{},文件名：{}。", this.workingDirectory, strFileName);
		}
		return lsEntry;
	}

	/**
	 * 文件列表 显示storePath下的文件夹和文件
	 * 
	 * @return Vector 文件和文件夹集合
	 */
	@SuppressWarnings("unchecked")
	public final Vector<LsEntry> fileList() {
		Vector<LsEntry> vector = null;
		try {
			this.openConnection();
			vector = this.sftp.ls(sftp.pwd());
			if (vector == null || vector.isEmpty()) {
				logger.info("当前登录用户：{},目录：{},下面文件夹和文件数目为空", this.user, this.workingDirectory);
			} else {
				logger.info("ftpFileArray!=null");
				for (LsEntry lsEntry : vector) {
					logger.info(lsEntry.getFilename());
				}
			}
		} catch (Exception e) {
			logger.error("{}", e.toString());
			vector = null;
		} finally {
			if (this.closeConnection) {
				this.releaseConnection();
			}
		}
		return vector;
	}

	/**
	 * 删除服务器端文件
	 * 
	 * param strFileName
	 *            远程文件名
	 * @return boolean 操作结果 true 成功 false 失败
	 * @throws SftpException
	 */
	public final boolean deleteFile(String strFileName) throws SftpException {

		if (strFileName.indexOf("/") != -1) {
 
			int start=strFileName.lastIndexOf('/') + 1;
			
			// 文件名称.
			String fileName = strFileName.substring(start);

			// 取文件目录》
			String dir = strFileName.substring(0, strFileName.lastIndexOf('/'));
 
			//设置当前目录.
			this.innerChangeDirectory(dir, false);

			strFileName = fileName;

		}

		boolean booResult = false;
		if (strFileName == null || strFileName.trim().length() <= 0) {
			logger.error("{}", "被删除文件名不能为空.");
		} else {
			try {
				if(sftp.isConnected()==false)
				this.openConnection();
				if (this.sftp != null) {
					sftp.rm(strFileName);
					booResult = true;
					logger.info("FTP Server:{},工作目录:{},删除文件成功：{}", this.ip, this.workingDirectory, strFileName);
				}
			} catch (Exception e) {
				booResult = false;
				logger.error("{}", e.toString());
			} finally {
				if (this.closeConnection) {
					this.releaseConnection();
				}
			}
		}
		return booResult;
	}

	/**
	 * 文件重命名
	 * 
	 * param oldName
	 *            原名称
	 * param newName
	 *            新名称
	 * @return boolean 操作结果 true 成功 false 失败
	 */
	public final boolean rename(String oldName, String newName) {
		boolean booResult = false;
		try {
			this.openConnection();
			if (this.sftp != null) {
				this.sftp.rename(oldName, newName);
				booResult = true;
				logger.info("FTP Server:{},工作目录:{},文件重命名成功：{}", this.ip, this.workingDirectory, newName);
			}
		} catch (Exception e) {
			booResult = false;
			logger.error("{}", e.toString());
		} finally {
			if (this.closeConnection) {
				this.releaseConnection();
			}
		}
		return booResult;
	}

	/**
	 * 创建或删除目录
	 * 
	 * param strWorkingDirectory
	 *            工作目录
	 * param enumFtpUtil
	 *            枚举目录操作
	 * @return boolean 操作结果 true 成功 false 失败
	 */
	public final boolean managerDirectory(String strWorkingDirectory, EnumFtpUtil enumFtpUtil) {
		boolean booResult = true;
		if (StringUtils.isNotBlank(strWorkingDirectory)) {
			strWorkingDirectory = strWorkingDirectory.replaceAll("\\\\", "/");
			try {
				this.openConnection();
				if (enumFtpUtil == EnumFtpUtil.CreateDirectory) {
					String[] strArrayPath = StringUtils.split(strWorkingDirectory, "/");
					for (String strPath : strArrayPath) {
						if (StringUtils.isNotBlank(strPath)) {
							this.changeWorkingDirectory(strWorkingDirectory, true);
						}
					}
					logger.info("FTP Server:{},工作目录:{},创建目录成功：{}", this.ip, this.workingDirectory, strWorkingDirectory);
				} else if (enumFtpUtil == EnumFtpUtil.RemoveDirectory) {
					booResult = this.removeAll(strWorkingDirectory);
					logger.info("FTP Server:{},工作目录:{},删除目录成功：{}", this.ip, this.workingDirectory, strWorkingDirectory);
				}
			} catch (Exception e) {
				booResult = false;
				logger.error("{}", e.toString());
			} finally {
				if (this.closeConnection) {
					this.releaseConnection();
				}
			}
		}
		return booResult;
	}

	/**
	 * 删除目录
	 *
	 * param folderDir
	 *            目录
	 * @return boolean 操作结果 true 成功 false 失败
	 */
	private boolean removeAll(String folderDir) {
		try {
			this.sftp.cd(folderDir);
			Vector<LsEntry> vector = this.fileList();
			if (vector != null && !vector.isEmpty()) {
				for (LsEntry lsEntry : vector) {
					if (lsEntry.getAttrs().isDir() && !StringUtils.equals(".", lsEntry.getFilename())
							&& !StringUtils.equals("..", lsEntry.getFilename())) {
						this.removeAll(folderDir + "/" + lsEntry.getFilename());
					} else {
						this.sftp.rm(folderDir + "/" + lsEntry.getFilename());
					}
				}
			}
			this.sftp.rmdir(folderDir);
		} catch (Exception e) {
			logger.error("{}", e.toString());
			return false;
		}
		return true;
	}

	/**
	 * 获得远程文件的文件头信息，前4个字节
	 * 
	 * param strRemoteFile
	 *            远程文件名
	 * @return byte[] 文件的文件头信息，前4个字节
	 */
	public final byte[] getFileHeader(String strRemoteFile) {
		byte[] byteArray = null;
		try {
			this.openConnection();
			if (this.sftp != null) {
				if (this.file(strRemoteFile) != null) {
					InputStream inputStream = this.sftp.get(strRemoteFile);
					byteArray = new byte[4];
					inputStream.read(byteArray);
				}
			}
		} catch (Exception e) {
			logger.error("{}", e.toString());
		} finally {
			if (this.closeConnection) {
				this.releaseConnection();
			}
		}
		return byteArray;
	}

	/**
	 * 在SFTP内部复制文件
	 * 
	 * param sourceFile
	 *            源文件名
	 * param targetFile
	 *            目标文件名
	 * @return boolean 操作结果 true 成功 false 失败
	 */
	public final boolean copyFile(String sourceFile, String targetFile) {
		boolean booResult = false;
		try {
			this.openConnection();
			if (this.sftp != null) {
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
						targetFile = StringUtils.substringBeforeLast(targetFile, ".").replaceAll("_\\d{10,}", "")
								+ strSubNewFileName;
						if (StringUtils.isNotBlank(strSuffix)) {
							targetFile += "." + strSuffix;
						}
					}
					booResult = this.uploadFile(targetFile, in);
				} else {
					logger.error("源文件不存在:{}", sourceFile);
				}
			}
		} catch (Exception e) {
			booResult = false;
			logger.error("{}", e.toString());
		} finally {
			if (this.closeConnection) {
				this.releaseConnection();
			}
		}
		return booResult;
	}

	/**
	 * 在SFTP内部移动文件
	 * 
	 * param sourceFile
	 *            源文件名
	 * param targetFile
	 *            目标文件名
	 * @return boolean 操作结果 true 成功 false 失败
	 */
	public final boolean moveFile(String sourceFile, String targetFile) {
		boolean booResult = false;
		try {
			this.openConnection();
			if (this.sftp != null) {
				booResult = this.copyFile(sourceFile, targetFile);
				if (booResult) {
					booResult = this.deleteFile(sourceFile);
				}
			}
		} catch (Exception e) {
			booResult = false;
			logger.error("{}", e.toString());
		} finally {
			if (this.closeConnection) {
				this.releaseConnection();
			}
		}
		return booResult;
	}

	/**
	 * 上传目录
	 *
	 * param sPath
	 *            上传源目录
	 * param dPath
	 *            上传目的目录
	 */
	public void upLoadDir(String sPath, String dPath) {

		try {
			this.openConnection();

			try {
				// 尝试进入目录
				this.sftp.cd(dPath);
			} catch (SftpException e) {
				// 新建目录
				this.sftp.mkdir(dPath);
				this.sftp.cd(dPath);
			}

			File file = new File(sPath);
			// 上传文件到当前目录
			uploadFile(file, this.sftp.pwd());

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
	 * param file
	 *            要上传的目录或文件
	 * param pwd
	 *            ftp当前目录
	 */
	private void uploadFile(File file, String pwd) {

		// 如果是目录
		if (file.isDirectory()) {
			// 列出文件
			File[] list = file.listFiles();
			String fileName = file.getName();

			// 创建目录
			try {
				this.sftp.cd(pwd);
				logger.info("正在创建目录:{}/{}", sftp.pwd(), fileName);
				this.sftp.mkdir(fileName);
				logger.info("成功创建目录:{}/{}", sftp.pwd(), fileName);
			} catch (Exception e) {
				logger.error("创建目录{}失败,{}", fileName, e.getMessage());
			}

			// 进入目录
			pwd = pwd + "/" + file.getName();
			try {
				this.sftp.cd(file.getName());
			} catch (SftpException e) {
				logger.error("进入目录{}失败", file.getName());
			}

			// 迭代处理：上传文件(目录)到当前目录
			for (int i = 0; i < list.length; i++) {
				uploadFile(list[i], pwd);
			}
		}
		// 是文件
		else {
			// 进入当前目录
			try {
				this.sftp.cd(pwd);
			} catch (SftpException e1) {
				logger.error("进入目录{}失败,{}", pwd, e1.getMessage());
			}

			logger.info("正在复制文件:{}", file.getAbsolutePath());

			InputStream instream = null;
			OutputStream outstream = null;

			try {
				// 输出流
				outstream = this.sftp.put(file.getName());
				// 输入流
				instream = new FileInputStream(file);

				byte b[] = new byte[1024];
				int n;
				try {
					// 写入流
					while ((n = instream.read(b)) != -1) {
						outstream.write(b, 0, n);
					}
				} catch (IOException e) {
					logger.error("上传文件{}失败,{}", file.getName(), e.getMessage());
				}
			} catch (SftpException e) {
				logger.error("上传文件{}失败,{}", file.getName(), e.getMessage());
			} catch (IOException e) {
				logger.error("上传文件{}失败,{}", file.getName(), e.getMessage());
			} finally {
				try {
					// 关闭流
					outstream.flush();
					outstream.close();
					instream.close();
				} catch (Exception e2) {
					logger.error(e2.getMessage());
				}
			}
		}
	}

	/**
	 * 下载目录
	 *
	 * param sPath
	 *            下载源目录
	 * param dPath
	 *            下载目的目录
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
			this.sftp.cd(sPath);

			Vector<LsEntry> vector = this.fileList();

			if (vector != null && !vector.isEmpty()) {
				for (LsEntry lsEntry : vector) {
					// 如果是目录
					if (lsEntry.getAttrs().isDir() && !StringUtils.equals(".", lsEntry.getFilename())
							&& !StringUtils.equals("..", lsEntry.getFilename())) {
						// 创建本地目录
						BaseFileUtils.createDirectory(dPath + lsEntry.getFilename());
						// 确保链接打开
						this.openConnection();
						this.sftp.cd(sPath + lsEntry.getFilename());
						// 递归调用下载目录
						downLoadDir(sPath + lsEntry.getFilename(), dPath + lsEntry.getFilename());
					}
					// 下载文件
					else {
						BaseFileUtils.createDirectory(dPath);
						downloadFile(sPath + lsEntry.getFilename(), dPath + lsEntry.getFilename());
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
	 * 获得：是否关闭SFTP链接，默认为否。
	 * 
	 * @return boolean 操作结果 true 成功 false 失败
	 */
	public final boolean isCloseConnection() {
		return closeConnection;
	}

	/**
	 * 设置：是否关闭SFTP链接，默认为否。
	 * 
	 * param closeConnection
	 *            是否关闭连接
	 */
	public final void setCloseConnection(boolean closeConnection) {
		this.closeConnection = closeConnection;
	}

	/**
	 * 获得默认控制编码,默认为GB2312，支持中文目录及中文文件名传输
	 * 
	 * @return 获得控制编码,
	 */
	public String getControlEncoding() {
		return controlEncoding;
	}

	/**
	 * 设置默认控制编码,默认为GB2312，支持中文目录及中文文件名传输
	 * 
	 * param controlEncoding
	 *            获得控制编码,
	 */
	public void setControlEncoding(String controlEncoding) {
		if (StringUtils.isNotBlank(controlEncoding)) {
			this.controlEncoding = controlEncoding;
		}
	}

	/**
	 * 设置：连接超时时间
	 * 
	 * param timeout
	 *            连接超时时间
	 */

	public final void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/*
	 * 测试代码 public static void main(String[] args) { SftpUtil util = new
	 * SftpUtil("192.168.2.129", 22, "pms", "1qaz2wsx");
	 * util.setCloseConnection(true);
	 * 
	 * try { util.upLoadDir("D:/test_sftp22", "/"); //
	 * util.downLoadDir("/版本计划/水印中心/", "D:/test_sftp/版本计划3/"); //
	 * util.downLoadDir("/test_ftp222", "D:/test_sftp22"); } catch (Exception e)
	 * { } }
	 */

}
