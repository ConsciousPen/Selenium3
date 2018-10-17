package aaa.helpers.rest.wiremock.dto;

import aaa.helpers.rest.RestBodyRequest;

public class WireMockMappingRequest implements RestBodyRequest{

    public String id;
    public MappingRequest request;
    public MappingResponse response;
    public int priority;
}
