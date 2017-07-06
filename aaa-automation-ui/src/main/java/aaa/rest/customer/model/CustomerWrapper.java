package aaa.rest.customer.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.wrapper.AbstractModelWrapper;
import aaa.rest.wrapper.ModelWrapper;
import toolkit.datax.TestData;

@JsonIgnoreProperties(ignoreUnknown = true)
@ModelWrapper(modelClass = Customer.class)
public class CustomerWrapper extends AbstractModelWrapper {

    public CustomerWrapper() {
    }

    public CustomerWrapper(TestData testData) {
        this.errorCode = testData.getValue("errorCode");
        this.message = testData.getValue("message");
    }

}
