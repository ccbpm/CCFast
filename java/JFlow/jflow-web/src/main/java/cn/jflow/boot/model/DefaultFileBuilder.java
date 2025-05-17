package cn.jflow.boot.model;

import cn.jflow.boot.model.ActionEnum;
import cn.jflow.boot.model.OnlyOfficeFile;
import lombok.Builder;
@Builder
public class DefaultFileBuilder {


        private String fileId;
        private String fileName;
        private String type;
        private String lang;
        private ActionEnum action;
        private String actionData;
        private Boolean isEnableDirectUrl;
        private OnlyOfficeFile onlyOfficeFile;
        private EditConfig.User user;

        public EditConfig.User getUser() {
                return user;
        }

        public void setUser(EditConfig.User user) {
                this.user = user;
        }

        public String getFileId() {
                return fileId;
        }

        public void setFileId(String fileId) {
                this.fileId = fileId;
        }

        public String getFileName() {
                return fileName;
        }

        public void setFileName(String fileName) {
                this.fileName = fileName;
        }

        public String getType() {
                return type;
        }

        public void setType(String type) {
                this.type = type;
        }

        public String getLang() {
                return lang;
        }

        public void setLang(String lang) {
                this.lang = lang;
        }

        public ActionEnum getAction() {
                return action;
        }

        public void setAction(ActionEnum action) {
                this.action = action;
        }

        public String getActionData() {
                return actionData;
        }

        public void setActionData(String actionData) {
                this.actionData = actionData;
        }

        public Boolean getEnableDirectUrl() {
                return isEnableDirectUrl;
        }

        public void setEnableDirectUrl(Boolean enableDirectUrl) {
                isEnableDirectUrl = enableDirectUrl;
        }

        public OnlyOfficeFile getOnlyOfficeFile() {
                return onlyOfficeFile;
        }

        public void setOnlyOfficeFile(OnlyOfficeFile onlyOfficeFile) {
                this.onlyOfficeFile = onlyOfficeFile;
        }
}
