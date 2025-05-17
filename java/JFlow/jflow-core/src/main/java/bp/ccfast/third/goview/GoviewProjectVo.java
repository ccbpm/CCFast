package bp.ccfast.third.goview;

import java.io.Serializable;
import java.util.Date;


public class GoviewProjectVo implements Serializable {
    private static final long serialVersionUID = 1L;

	private String id;
	

	private String projectName;
	

	private Integer state;


	private String createTime;
	

	private String createUserId;
	

	private Integer isDelete;
	

	private String indexImage;
	

	private String remarks;
	
	private String content;
	
	private Integer isTemplate;
	
	private String  templateID;
	
	

	public String getTemplateID() {
		return templateID;
	}

	public void setTemplateID(String templateID) {
		this.templateID = templateID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id =  id;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName =  projectName;
	}

	
	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId =  createUserId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public String getIndexImage() {
		return indexImage;
	}

	public void setIndexImage(String indexImage) {
		this.indexImage =  indexImage;
	}
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks =  remarks;
	}
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getIsTemplate() {
		return isTemplate;
	}

	public void setIsTemplate(Integer isTemplate) {
		this.isTemplate = isTemplate;
	}

	
}