package aaa.helpers.rest.dtoDxp;

import java.util.List;
import aaa.helpers.rest.RestBodyRequest;

public class AAABindEndorsementRequestDTO implements RestBodyRequest {
    public String authorizedBy;
    public List<String> documentsSigned;
}