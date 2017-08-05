package aaa.rest.partysearch.model;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.wrapper.AbstractModelWrapper;
import aaa.rest.wrapper.ModelWrapper;
import toolkit.datax.TestData;

@JsonIgnoreProperties(ignoreUnknown = true)
@ModelWrapper(modelClass = Party.class)
public class PartyWrapper extends AbstractModelWrapper {

    public PartyWrapper() {
    }

    public PartyWrapper(TestData testData) {
        this.errorCode = testData.getValue("errorCode");
        this.message = testData.getValue("message");
    }

    public PartyWrapper(List<TestData> testDataList, Map<String, String> mapper){
        for (TestData testData : testDataList) {
            getModels().add(new Party(testData, mapper));
        }
    }
}
