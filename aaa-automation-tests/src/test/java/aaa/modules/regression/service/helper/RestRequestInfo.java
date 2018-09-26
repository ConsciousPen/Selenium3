package aaa.modules.regression.service.helper;

import javax.ws.rs.core.Response;

public class RestRequestInfo<T> {

	public String url;
	public String sessionId;
	public Object bodyRequest;
	public Class<T> responseType;
	public int status = Response.Status.OK.getStatusCode();
}
