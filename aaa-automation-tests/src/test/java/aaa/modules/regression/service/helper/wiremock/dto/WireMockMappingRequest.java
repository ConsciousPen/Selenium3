package aaa.modules.regression.service.helper.wiremock.dto;

import aaa.modules.regression.service.helper.RestBodyRequest;

public class WireMockMappingRequest implements RestBodyRequest{

    public String id;
    public MappingRequest request;
    public MappingResponse response;
    public int priority;
}
