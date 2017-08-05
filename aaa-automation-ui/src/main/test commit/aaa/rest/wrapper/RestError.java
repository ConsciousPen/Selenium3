package aaa.rest.wrapper;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import toolkit.datax.TestData;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestError {

    private String errorCode;
    private String message;
    private String field;

    public RestError() {
    }

    public RestError(String errorCode, String message, String field) {
        this.errorCode = errorCode;
        this.message = message;
        this.field = field;
    }

    public RestError(TestData testData) {
        this.errorCode = testData.getValue("errorCode");
        this.message = testData.getValue("message");
        this.field = testData.getValue("field");
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

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RestError error = (RestError) o;

        if (errorCode != null ? !errorCode.equals(error.errorCode) : error.errorCode != null)
            return false;
        if (message != null ? !message.equals(error.message) : error.message != null)
            return false;
        return field != null ? field.equals(error.field) : error.field == null;
    }

    @Override public int hashCode() {
        int result = errorCode != null ? errorCode.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (field != null ? field.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Error{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", field='" + field + '\'' +
                '}';
    }
}
