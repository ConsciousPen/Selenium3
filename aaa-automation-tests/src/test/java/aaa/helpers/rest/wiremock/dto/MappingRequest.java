package aaa.helpers.rest.wiremock.dto;

import java.util.List;

public class MappingRequest {

    public String urlPath;
    public String method;
    public List<BodyPattern> bodyPatterns;
}
