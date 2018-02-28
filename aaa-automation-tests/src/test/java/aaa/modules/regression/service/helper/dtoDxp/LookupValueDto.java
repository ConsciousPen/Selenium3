package aaa.modules.regression.service.helper.dtoDxp;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LookupValueDto  implements Serializable {
    @JsonProperty("CODE")
    private String code;
    @JsonProperty("DISPLAYVALUE")
    private String displayValue;
    @JsonProperty("S_OBJECT")
    private String serializedObject;

    public LookupValueDto() {
    }

    public LookupValueDto(String code, String displayValue) {
        this.code = code;
        this.displayValue = displayValue;
    }

    public LookupValueDto(String code, String displayValue, String serializedObject) {
        this.code = code;
        this.displayValue = displayValue;
        this.serializedObject = serializedObject;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getSerializedObject() {
        return serializedObject;
    }

    public void setSerializedObject(String serializedObject) {
        this.serializedObject = serializedObject;
    }
}

