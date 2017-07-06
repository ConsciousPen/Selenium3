package aaa.rest.platform.bpm.model.processinstance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ProcessInstanceInfoReply")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessInstanceInfoReply {

    private String actProcessInstanceId;
    private String processBusinessContextId;
    private String processStarted;

    public ProcessInstanceInfoReply() {
    }

    public String getActProcessInstanceId() {
        return actProcessInstanceId;
    }

    public void setActProcessInstanceId(String actProcessInstanceId) {
        this.actProcessInstanceId = actProcessInstanceId;
    }

    public String getProcessBusinessContextId() {
        return processBusinessContextId;
    }

    public void setProcessBusinessContextId(String processBusinessContextId) {
        this.processBusinessContextId = processBusinessContextId;
    }

    public String getProcessStarted() {
        return processStarted;
    }

    public void setProcessStarted(String processStarted) {
        this.processStarted = processStarted;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProcessInstanceInfoReply that = (ProcessInstanceInfoReply) o;

        if (actProcessInstanceId != null ? !actProcessInstanceId.equals(that.actProcessInstanceId) : that.actProcessInstanceId != null)
            return false;
        if (processBusinessContextId != null ? !processBusinessContextId.equals(that.processBusinessContextId) : that.processBusinessContextId != null)
            return false;
        return processStarted != null ? processStarted.equals(that.processStarted) : that.processStarted == null;
    }

    @Override public int hashCode() {
        int result = actProcessInstanceId != null ? actProcessInstanceId.hashCode() : 0;
        result = 31 * result + (processBusinessContextId != null ? processBusinessContextId.hashCode() : 0);
        result = 31 * result + (processStarted != null ? processStarted.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "ProcessInstanceInfoReply{" +
                "actProcessInstanceId='" + actProcessInstanceId + '\'' +
                ", processBusinessContextId='" + processBusinessContextId + '\'' +
                ", processStarted='" + processStarted + '\'' +
                '}';
    }
}
