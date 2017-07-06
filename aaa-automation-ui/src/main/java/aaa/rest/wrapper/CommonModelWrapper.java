package aaa.rest.wrapper;

import aaa.rest.IModel;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import toolkit.datax.TestData;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonModelWrapper implements IModel {

    protected String errorCode;
    protected String message;
    protected List<IModel> models = new ArrayList<>();
    protected List<RestError> errors = new ArrayList<>();

    public CommonModelWrapper() {
    }

    public CommonModelWrapper(TestData testData) {
        this.message = testData.getValue("message");
        this.errorCode = testData.getValue("errorCode");
        if (!testData.getTestDataList("errors").isEmpty()) {
            for (TestData data : testData.getTestDataList("errors")) {
                this.errors.add(new RestError(data));
            }
        }
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

    public List<IModel> getModels() {
        return models;
    }

    public void setModels(List<IModel> models) {
        this.models = models;
    }

    public List<RestError> getErrors() {
        return errors;
    }

    public void setErrors(List<RestError> errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CommonModelWrapper that = (CommonModelWrapper) o;

        if (errorCode != null ? !errorCode.equals(that.errorCode) : that.errorCode != null)
            return false;
        if (message != null ? !message.trim().equals(that.message.trim()) : that.message != null)
            return false;
        if (models != null ? !models.containsAll(that.models) : that.models != null)
            return false;
        return errors != null ? errors.containsAll(that.errors) : that.errors == null;
    }

    @Override
    public int hashCode() {
        int result = errorCode != null ? errorCode.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (models != null ? models.hashCode() : 0);
        result = 31 * result + (errors != null ? errors.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CommonModelWrapper{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", models=" + models +
                ", errors=" + errors +
                '}';
    }
}
