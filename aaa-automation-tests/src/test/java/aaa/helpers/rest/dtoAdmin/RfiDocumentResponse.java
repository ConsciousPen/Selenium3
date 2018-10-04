package aaa.helpers.rest.dtoAdmin;

import aaa.helpers.rest.RestBodyRequest;

public class RfiDocumentResponse implements RestBodyRequest {
    public String documentName;
    public String status;
    public String documentId;
    public String documentType;
    public String parentOid;
    public String parent;
}