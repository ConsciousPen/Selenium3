package aaa.modules.regression.service.helper;

public class RestRequestInfo<T> {

	String url;
	String sessionId;
	RestBodyRequest bodyRequest;
	Class<T> responseType;
	int status;
}
