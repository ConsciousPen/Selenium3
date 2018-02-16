package aaa.modules.regression.service.helper.dtoAdmin;

import aaa.modules.regression.service.helper.RestBodyRequest;

public class RfiDocumentResponse implements RestBodyRequest {
    public String documentName;
    public String status;
    public String documentId;
    public String documentType;
    public String parentOid;
    public String parent;
}