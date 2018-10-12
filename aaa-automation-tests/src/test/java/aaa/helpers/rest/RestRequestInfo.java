package aaa.helpers.rest;

import javax.ws.rs.core.Response;

public class RestRequestInfo<T> {

	public String url;
	public String sessionId;
	public Object bodyRequest;
	public Class <T> responseType;
	public int status = Response.Status.OK.getStatusCode();


	public RestRequestInfo<T> build()
	{
		RestRequestInfo<T> result = new RestRequestInfo<>();
		result.setUrl(url);
		result.setSessionId(sessionId);
		result.setBodyRequest(bodyRequest);
		result.setResponseType(responseType);
		result.setStatus(status);
		return result;
	}

	public RestRequestInfo<T> setUrl(String url) {
		this.url = url;
		return this;
	}

	public RestRequestInfo<T> setSessionId(String sessionId) {
		this.sessionId = sessionId;
		return this;
	}

	public RestRequestInfo<T> setStatus(int status) {
		this.status = status;
		return this;
	}

	public RestRequestInfo<T> setResponseType(Class<T> responseType) {
		this.responseType = responseType;
		return this;
	}

	public RestRequestInfo<T> setBodyRequest(Object bodyRequest) {
		this.bodyRequest = bodyRequest;
		return this;
	}

	public String getSessionId() {
		return sessionId;
	}

	public Object getBodyRequest() {
		return bodyRequest;
	}


	public Class<T> getResponseType() {
		return responseType;
	}

	public int getStatus() {
		return status;
	}

	public String getUrl() {
		return url;
	}

}
