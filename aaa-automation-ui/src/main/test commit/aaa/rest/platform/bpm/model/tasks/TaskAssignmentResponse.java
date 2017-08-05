package aaa.rest.platform.bpm.model.tasks;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.IModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskAssignmentResponse implements IModel {

    private List<String> messageList = new ArrayList<>();
    private String contextDescription;
    private String successful;
    private String messagesAsString;
    private String status;

    public List<String> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<String> messageList) {
        this.messageList = messageList;
    }

    public String getContextDescription() {
        return contextDescription;
    }

    public void setContextDescription(String contextDescription) {
        this.contextDescription = contextDescription;
    }

    public String getSuccessful() {
        return successful;
    }

    public void setSuccessful(String successful) {
        this.successful = successful;
    }

    public String getMessagesAsString() {
        return messagesAsString;
    }

    public void setMessagesAsString(String messagesAsString) {
        this.messagesAsString = messagesAsString;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TaskAssignmentResponse that = (TaskAssignmentResponse) o;

        if (messageList != null ? !messageList.equals(that.messageList) : that.messageList != null)
            return false;
        if (contextDescription != null ? !contextDescription.equals(that.contextDescription) : that.contextDescription != null)
            return false;
        if (successful != null ? !successful.equals(that.successful) : that.successful != null)
            return false;
        if (messagesAsString != null ? !messagesAsString.equals(that.messagesAsString) : that.messagesAsString != null)
            return false;
        return status != null ? status.equals(that.status) : that.status == null;
    }

    @Override public int hashCode() {
        int result = messageList != null ? messageList.hashCode() : 0;
        result = 31 * result + (contextDescription != null ? contextDescription.hashCode() : 0);
        result = 31 * result + (successful != null ? successful.hashCode() : 0);
        result = 31 * result + (messagesAsString != null ? messagesAsString.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "TaskAssignmentResponse{" +
                "messageList=" + messageList +
                ", contextDescription='" + contextDescription + '\'' +
                ", successful='" + successful + '\'' +
                ", messagesAsString='" + messagesAsString + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
