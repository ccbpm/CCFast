package bp.ccfast.third.goview;


public class SysFileVo {
	
    private String id;
    private String fileName;
    private String fileSize;
    private String createTime;
    
    /**
     * 相对路径
     */
    private String relativePath;
    
    /**
     * 虚拟路径key
     */
    private String virtualKey;
    
    /**
     * 请求url
     */
    private String fileurl;
    
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getRelativePath() {
		return relativePath;
	}
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	public String getVirtualKey() {
		return virtualKey;
	}
	public void setVirtualKey(String virtualKey) {
		this.virtualKey = virtualKey;
	}
	public String getFileurl() {
		return fileurl;
	}
	public void setFileurl(String fileurl) {
		this.fileurl = fileurl;
	}
	
}
