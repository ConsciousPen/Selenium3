package aaa.rest.platform.bpm.model.tasks;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.IModel;
import toolkit.datax.TestData;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TasksResponse implements IModel, Comparable<Object> {
    private String agencyCd;
    private String name;
    private String id;
    private Assignment assignment;

    public TasksResponse() {
    }

    public TasksResponse(TestData testData, Map<String, String> mapper) {
        this.agencyCd = testData.getValue("agencyCd");
        this.name = testData.getValue("name");
        this.id = mapper.get(testData.getValue("id"));
    }

    public TasksResponse(TestData testData) {
        this.agencyCd = testData.getValue("agencyCd");
        this.name = testData.getValue("name");
    }

    public String getAgencyCd() {
        return agencyCd;
    }

    public void setAgencyCd(String agencyCd) {
        this.agencyCd = agencyCd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    /**
     * Assignment does not include to equals because it do not need to check it in Task get/create tests
     * Assignment related checks will be done separately
     * @param o
     * @return
     */
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TasksResponse that = (TasksResponse) o;

        if (agencyCd != null ? !agencyCd.equals(that.agencyCd) : that.agencyCd != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override public int hashCode() {
        int result = agencyCd != null ? agencyCd.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "TasksResponse{" +
                "agencyCd='" + agencyCd + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    @Override public int compareTo(Object o) {
        int compareId = Integer.parseInt(((TasksResponse) o).getId());
        int currentId = Integer.parseInt(this.id);
        return currentId - compareId;
    }
}
