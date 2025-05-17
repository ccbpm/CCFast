package bp.ccfast.third.goview;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * <p>
 * 
 * </p>
 *
 * @author fc
 * @since 2023-04-30
 */

public class GoviewProjectDataVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;

    private String projectId;

    private String createTime;

    private String createUserId;

    private String content;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

   
}
