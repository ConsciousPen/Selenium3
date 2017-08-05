package aaa.rest.platform.notes.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import aaa.rest.IModel;
import toolkit.datax.TestData;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotesResponse implements IModel {

    private String errorCode = "";
    private String message = "";
    private String field = "";
    private String stackTrace = "";
    private String[] errors;
    private String id = "";
    private String category = "";
    private boolean confidential;
    private String title = "";
    private String description = "";
    private String entityType = "";
    private String entityRefNo = "";
    private String performerId = "";
    private String created = "";
    private String updated = "";
    private String userNoteAdditionalInfo = "";
    private String userNoteName = "";

    public NotesResponse() {
    }

    public NotesResponse(TestData testData) {
        this.category = testData.getValue("category");
        this.confidential = Boolean.parseBoolean(testData.getValue("confidential"));
        this.description = testData.getValue("description");
        this.entityType = testData.getValue("entityType");
        this.errorCode = testData.getValue("errorCode");
        this.message = testData.getValue("message");
        this.performerId = testData.getValue("performerId");
        this.title = testData.getValue("title");
        this.userNoteName = testData.getValue("userNoteName");
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isConfidential() {
        return confidential;
    }

    public void setConfidential(boolean confidential) {
        this.confidential = confidential;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityRefNo() {
        return entityRefNo;
    }

    public void setEntityRefNo(String entityRefNo) {
        this.entityRefNo = entityRefNo;
    }

    public String getPerformerId() {
        return performerId;
    }

    public void setPerformerId(String performerId) {
        this.performerId = performerId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUserNoteAdditionalInfo() {
        return userNoteAdditionalInfo;
    }

    public void setUserNoteAdditionalInfo(String userNoteAdditionalInfo) {
        this.userNoteAdditionalInfo = userNoteAdditionalInfo;
    }

    public String getUserNoteName() {
        return userNoteName;
    }

    public String getField() {
        return field;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }

    public void setUserNoteName(String userNoteName) {
        this.userNoteName = userNoteName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NotesResponse that = (NotesResponse) o;

        if (confidential != that.confidential)
            return false;
        if (errorCode != null ? !errorCode.equals(that.errorCode) : that.errorCode != null)
            return false;
        if (message != null ? !message.equals(that.message) : that.message != null)
            return false;
        if (category != null ? !category.equals(that.category) : that.category != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null)
            return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (entityType != null ? !entityType.equals(that.entityType) : that.entityType != null)
            return false;
        if (performerId != null ? !performerId.equals(that.performerId) : that.performerId != null)
            return false;
        return userNoteName != null ? userNoteName.equals(that.userNoteName) : that.userNoteName == null;
    }

    @Override
    public int hashCode() {
        int result = errorCode != null ? errorCode.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (confidential ? 1 : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (entityType != null ? entityType.hashCode() : 0);
        result = 31 * result + (performerId != null ? performerId.hashCode() : 0);
        result = 31 * result + (userNoteName != null ? userNoteName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NotesResponse{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", category='" + category + '\'' +
                ", confidential=" + confidential +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", entityType='" + entityType + '\'' +
                ", performerId='" + performerId + '\'' +
                ", userNoteName='" + userNoteName + '\'' +
                '}';
    }

    public static class Builder {
        private NotesResponse response = new NotesResponse();

        public Builder setErrorCode(String errorCode) {
            this.response.setErrorCode(errorCode);
            return this;
        }

        public Builder setMessage(String message) {
            this.response.setMessage(message);
            return this;
        }

        public Builder setCategory(String category) {
            this.response.setCategory(category);
            return this;
        }

        public Builder setTitle(String title) {
            this.response.setTitle(title);
            return this;
        }

        public Builder setDescription(String description) {
            this.response.setDescription(description);
            return this;
        }

        public Builder setEntityType(String entityType) {
            this.response.setEntityType(entityType);
            return this;
        }

        public Builder setPerformerId(String performerId) {
            this.response.setPerformerId(performerId);
            return this;
        }

        public Builder setUserNoteName(String userNoteName) {
            this.response.setUserNoteName(userNoteName);
            return this;
        }

        public Builder setConfidential(boolean confidential) {
            this.response.setConfidential(confidential);
            return this;
        }

        public NotesResponse build() {
            return this.response;
        }
    }
}
