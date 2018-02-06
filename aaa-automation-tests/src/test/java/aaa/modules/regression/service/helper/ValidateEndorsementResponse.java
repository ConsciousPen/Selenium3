package aaa.modules.regression.service.helper;

import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ValidateEndorsementResponse {

    public List<ValidateEndorsementRuleSet> ruleSets;
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    public List<String> allowedEndorsements;
}
