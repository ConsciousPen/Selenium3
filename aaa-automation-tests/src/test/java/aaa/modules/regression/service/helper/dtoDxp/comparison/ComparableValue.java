package aaa.modules.regression.service.helper.dtoDxp.comparison;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ComparableValue {

    @JsonUnwrapped
    public String value;
    public String newValue;
    public String oldValue;
}
