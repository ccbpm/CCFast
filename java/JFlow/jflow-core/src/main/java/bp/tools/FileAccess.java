package bp.tools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class FileAccess {

	public static boolean Move(File srcFile, String destPath) {
		// Destination directory
		File dir = new File(destPath);

		// Move file to new directory
		boolean success = srcFile.renameTo(new File(dir, srcFile.getName()));

		return success;
	}

	public static boolean Move(String srcFile, String destPath) {
		// File (or directory) to be moved
		File file = new File(srcFile);

		// Destination directory
		File dir = new File(destPath);

		// Move file to new directory
		boolean success = file.renameTo(new File(dir, file.getName()));

		return success;
	}

	public static void Copy(String oldPath, String newPath) {
		InputStream inStream = null;
		FileOutputStream fs = null;
		
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				inStream =  new FileInputStream(oldPath);
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					fs.write(buffer, 0, byteread);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(null != fs)
				{
					fs.close();
				}
				if(null != inStream)
				{
					inStream.close();
				} 
				}catch (IOException e2) {
					e2.printStackTrace();
			}
		}
	}

	public static void Copy(File oldfile, String newPath) {
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			int bytesum = 0;
			int byteread = 0;
			if (oldfile.exists()) {
				inStream = new FileInputStream(oldfile);
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					fs.write(buffer, 0, byteread);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(null != fs)
				{
					fs.close();
				}
				if(null != inStream)
				{
					inStream.close();
				} 
				}catch (IOException e2) {
					e2.printStackTrace();
			}
		}
	}
	
	public static void Copy(InputStream inStream, String newPath){
		FileOutputStream fs = null;
		try {
			int bytesum = 0;
			int byteread = 0;
			fs = new FileOutputStream(newPath);
			byte[] buffer = new byte[1444];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
//				System.out.println(bytesum);
				fs.write(buffer, 0, byteread);
			}
			fs.close();
		} catch (Exception e) {
//			System.out.println("error  ");
			e.printStackTrace();
		}finally{
			try {
				if(null != fs)
				{
					fs.close();
				}
				}catch (IOException e2) {
					e2.printStackTrace();
			}
		}
	}
	
	public static void copyFolder(File src, File dest) {
		InputStream in = null;
		OutputStream out = null;
		try {
			if (src.isDirectory()) {
				if (!dest.exists()) {
					dest.mkdirs();
				}
				String files[] = src.list();
				for (String file : files) {
					File srcFile = new File(src, file);
					File destFile = new File(dest, file);
					// 递归复制
					copyFolder(srcFile, destFile);
				}
			} else {
				in = new FileInputStream(src);
				out = new FileOutputStream(dest);

				byte[] buffer = new byte[1024];

				int length;

				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(null != out)
				{
					out.close();
				}
				if(null != in)
				{
					in.close();
				} 
				}catch (IOException e2) {
					e2.printStackTrace();
			}
		}
	}
	
	public static String readFileByBytes(String fileName){
		int len=0;
		String line=null;
        StringBuffer str=new StringBuffer();
        BufferedReader in= null;
        try {
            in= new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));  ;
            while( (line=in.readLine())!=null ){
                if(len != 0){// 处理换行符的问题
                    str.append("\r\n"+line);
                }else{
                    str.append(line);
                }
                len++;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
			try {
				if(null != in)
				{
					in.close();
				} 
				}catch (IOException e2) {
					e2.printStackTrace();
			}
		}
        return str.toString();
	}
	
	/*
	 * Java文件操作 获取文件扩展名
	 *
	 *  Created on: 2011-8-2
	 *      Author: blueeagle
	 */
	    public static String getExtensionName(String filename) { 
	        if ((filename != null) && (filename.length() > 0)) { 
	            int dot = filename.lastIndexOf('.'); 
	            if ((dot >-1) && (dot < (filename.length() - 1))) { 
	                return filename.substring(dot + 1); 
	            } 
	        } 
	        return filename; 
	    } 
	/*
	 * Java文件操作 获取不带扩展名的文件名
	 *
	 *  Created on: 2011-8-2
	 *      Author: blueeagle
	 */
	    public static String getFileNameNoEx(String filename) { 
	        if ((filename != null) && (filename.length() > 0)) { 
	            int dot = filename.lastIndexOf('.'); 
	            if ((dot >-1) && (dot < (filename.length()))) { 
	                return filename.substring(0, dot); 
	            } 
	        } 
	        return filename; 
	    }
	    /**
	     * 递归删除目录下的所有文件及子目录下所有文件
	     * param file 将要删除的文件目录
	     * @return boolean Returns "true" if all deletions were successful.
	     *                 If a deletion fails, the method stops attempting to
	     *                 delete and returns "false".
	     */
		public static boolean deletesFile(File file) {
			String[] files = file.list();
			if (files != null && files.length > 0) {
				for (String f : files) {
					boolean success = deletesFile(new File(file, f));
					if (!success) {
						return false;
					}
				}
			}
			return file.delete();
		}
		
		public static void findFiles(String baseDirName, String targetFileName, List fileList) throws Exception {   
	    	File baseDir = new File(baseDirName);		// 创建一个File对象
			if (!baseDir.exists() || !baseDir.isDirectory()) {	// 判断目录是否存在
				return;
			}
	        String tempName = null;   
	        //判断目录是否存在   
	        File tempFile;
	    	File[] files = baseDir.listFiles();
	    	for (int i = 0; i < files.length; i++) {
				tempFile = files[i];
				if(tempFile.isDirectory()){
					findFiles(tempFile.getAbsolutePath(), targetFileName, fileList);
				}else if(tempFile.isFile()){
					tempName = tempFile.getName();
					if(tempName.indexOf(targetFileName)!=-1){
						// 匹配成功，将文件名添加到结果集
						fileList.add(tempFile.getAbsoluteFile());
					}
				}
			}
	    }   

}