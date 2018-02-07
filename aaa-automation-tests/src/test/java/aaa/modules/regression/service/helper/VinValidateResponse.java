package aaa.modules.regression.service.helper;

import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class VinValidateResponse {

    public List<VinFields> vehicles;
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    public String validationMessage;
}
