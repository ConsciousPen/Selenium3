package aaa.rest.wrapper;

import java.util.ArrayList;
import java.util.List;

import aaa.rest.IModel;

public abstract class AbstractModelWrapper implements IModel {

    protected String errorCode;
    protected String message;
    protected List<IModel> models = new ArrayList<>();
    protected List<RestError> errors = new ArrayList<>();

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

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AbstractModelWrapper that = (AbstractModelWrapper) o;

        if (errorCode != null ? !errorCode.equals(that.errorCode) : that.errorCode != null)
            return false;
        if (message != null ? !message.equals(that.message) : that.message != null)
            return false;
        if (models != null ? !models.containsAll(that.models) : that.models != null)
            return false;
        return errors != null ? errors.containsAll(that.errors) : that.errors == null;
    }

    @Override public int hashCode() {
        int result = errorCode != null ? errorCode.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (models != null ? models.hashCode() : 0);
        result = 31 * result + (errors != null ? errors.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "AbstractModelWrapper{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", models=" + models +
                ", errors=" + errors +
                '}';
    }
}
