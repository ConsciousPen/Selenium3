package aaa.rest.platform.notes.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.wrapper.AbstractModelWrapper;
import aaa.rest.wrapper.ModelWrapper;
import toolkit.datax.TestData;

@JsonIgnoreProperties(ignoreUnknown = true)
@ModelWrapper(modelClass = NotesResponse.class)
public class NotesResponseWrapper extends AbstractModelWrapper {

    public NotesResponseWrapper() {
    }

    public NotesResponseWrapper(TestData testData) {
        this.errorCode = testData.getValue("errorCode");
        this.message = testData.getValue("message");
    }

    public NotesResponseWrapper(List<TestData> testDataList) {
        for (TestData testData : testDataList) {
            getModels().add(new NotesResponse(testData));
        }
    }

}
