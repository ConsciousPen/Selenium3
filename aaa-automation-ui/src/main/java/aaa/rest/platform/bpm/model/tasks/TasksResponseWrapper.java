package aaa.rest.platform.bpm.model.tasks;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.wrapper.AbstractModelWrapper;
import aaa.rest.wrapper.ModelWrapper;
import aaa.rest.wrapper.RestError;
import toolkit.datax.TestData;

@JsonIgnoreProperties(ignoreUnknown = true)
@ModelWrapper(modelClass = TasksResponse.class)
public class TasksResponseWrapper extends AbstractModelWrapper {

    public TasksResponseWrapper() {
    }

    public TasksResponseWrapper(List<TestData> testDataList, Map<String, String> mapper) {
        for (TestData testData : testDataList) {
            getModels().add(new TasksResponse(testData, mapper));
        }
    }

    public TasksResponseWrapper(TestData testData) {
        setMessage(testData.getValue("message"));
        setErrorCode(testData.getValue("errorCode"));
        if (testData.getTestDataList("errors").size() > 0) {
            for (TestData data : testData.getTestDataList("errors")) {
                getErrors().add(new RestError(data));
            }
        }
    }

}
