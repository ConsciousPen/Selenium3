package aaa.helpers.performance.customer;

import java.util.ArrayList;
import java.util.List;

public class PerfCustomerHolder {
    private String customerNumber;
    private String taskId;
    private List<String> noteIds = new ArrayList<>();

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<String> getNoteIds() {
        return noteIds;
    }

    public void setNoteIds(List<String> noteIds) {
        this.noteIds = noteIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PerfCustomerHolder that = (PerfCustomerHolder) o;

        if (customerNumber != null ? !customerNumber.equals(that.customerNumber) : that.customerNumber != null)
            return false;
        if (taskId != null ? !taskId.equals(that.taskId) : that.taskId != null) return false;
        return noteIds != null ? noteIds.equals(that.noteIds) : that.noteIds == null;
    }

    @Override
    public int hashCode() {
        int result = customerNumber != null ? customerNumber.hashCode() : 0;
        result = 31 * result + (taskId != null ? taskId.hashCode() : 0);
        result = 31 * result + (noteIds != null ? noteIds.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PerfCustomerHolder{" +
                "customerNumber='" + customerNumber + '\'' +
                ", taskId='" + taskId + '\'' +
                ", noteIds=" + noteIds +
                '}';
    }
}
