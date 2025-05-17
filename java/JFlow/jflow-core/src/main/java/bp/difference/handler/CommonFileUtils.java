package bp.difference.handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
public class CommonFileUtils {
	/**
	 * 上传
	 * param request
	 */
	public static void upload(HttpServletRequest request,String fileName,File targetFile) throws Exception{
		String contentType = request.getContentType();
		if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
			MultipartHttpServletRequest mrequest = getMultipartHttpServletRequest(request);
			
		    Iterator ifiles =  mrequest.getFileNames();
		    while(ifiles.hasNext()){
		    	System.out.println(ifiles.next());
		    }
			
			MultipartFile multipartFile = mrequest.getFile(fileName);
			try {
				multipartFile.transferTo(targetFile);
			} catch (Exception e) {
				throw e;
			}
		}
	}

	/**
	 * 多文件上传根据idx获取单个文件
	 * @param request
	 * @param fileName
	 * @param targetFile
	 * @param idx 获取第几个文件
	 * @throws Exception
	 */
	public static void upload(HttpServletRequest request,String fileName,File targetFile, Integer idx) throws Exception{
		String contentType = request.getContentType();
		if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
			MultipartHttpServletRequest mrequest = getMultipartHttpServletRequest(request);

			Iterator ifiles =  mrequest.getFileNames();
			while(ifiles.hasNext()){
				System.out.println(ifiles.next());
			}

			List<MultipartFile> multipartFiles = mrequest.getFiles(fileName);
			try {
				multipartFiles.get(idx).transferTo(targetFile);
			} catch (Exception e) {
				throw e;
			}
		}
	}
	
	/**
	 * 获取原始的文件名
	 * param request
	 * @return
	 */
	public static String getOriginalFilename(HttpServletRequest request,String fileName){
		String contentType = request.getContentType();
		if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
			MultipartHttpServletRequest mrequest = getMultipartHttpServletRequest(request);
			MultipartFile multipartFile = mrequest.getFile(fileName);
			return multipartFile.getOriginalFilename();
		}else{
			return null;
		}
	}
	
	/**
	 * 获取上传的文件
	 * param request
	 * @return
	 */
	public static long getFilesSize(HttpServletRequest request,String fileName){
		String contentType = request.getContentType();
		if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
			MultipartHttpServletRequest mrequest = getMultipartHttpServletRequest(request);
			MultipartFile multipartFile = mrequest.getFile(fileName);
			return multipartFile.getSize();
		}else{
			return 0;
		}		
	}
	
	/**
	 * 获取文件对于的输入流
	 * param request
	 * param fileName
	 * @return
	 * @throws IOException 
	 */
	public static InputStream getInputStream(HttpServletRequest request,String fileName) throws IOException{
		String contentType = request.getContentType();
		if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
			MultipartHttpServletRequest mrequest = getMultipartHttpServletRequest(request);
			MultipartFile multipartFile = mrequest.getFile(fileName);
			return multipartFile.getInputStream();
		}else{
			return null;
		}		
	}
	
	/**
	 * 根据httpServletRequest获取MultipartHttpServletRequest
	 * param request
	 * @return 
	 */
	public static MultipartHttpServletRequest getMultipartHttpServletRequest(HttpServletRequest request) {
		MultipartHttpServletRequest mrequest = null;
		if(ServletFileUpload.isMultipartContent(request)) {
			if(request instanceof MultipartHttpServletRequest) {
				mrequest = (MultipartHttpServletRequest) request;
			}else{
				mrequest = new StandardMultipartHttpServletRequest(request);
			}
		}
		return mrequest;
	}
}
