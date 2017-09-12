package aaa.helpers.http.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import aaa.helpers.http.impl.HttpConstants;
import aaa.helpers.http.impl.HttpRequestor;

public class HttpAAARequestor extends HttpRequestor {

	private String domain;
	private String sessionCookieApp;
	private String sessionCookieAdmin;
	private String sessionCookieStub;
	private String sessionWindowId;
	private String cookieStubHeaderName = HttpConstants.COOKIE_STUB;

	public void sendPostRequest(String url, String params) throws IOException {
		if (url.startsWith(HttpConstants.URL_PROTOCOL)) {
			setUrl(url);
		} else {
			setUrl(buildUrl(url));
		}
		setMethod(HttpMethods.POST);
		setParams(params);
		setHeaders(getPostRequestHeaders());
		sendRequest();
	}

	public void sendGetRequest(String url) throws IOException {
		if (url.startsWith(HttpConstants.URL_PROTOCOL)) {
			setUrl(url);
		} else {
			setUrl(buildUrl(url));
		}
		setMethod(HttpMethods.GET);
		removeParams();
		setHeaders(getGetRequestHeaders());
		sendRequest();
	}

	public String getAppSessionCookie() {
		return sessionCookieApp;
	}

	public String getStubSessionCookie() {
		return sessionCookieStub;
	}

	public String getAdminSessionCookie() {
		return sessionCookieStub;
	}
	
	public String getSessionWindowId() {
		return sessionWindowId;
	}

	public void setAppSessionCookie(String sessionCookie) {
		this.sessionCookieApp = sessionCookie;
	}

	public void setAdminSessionCookie(String sessionCookie) {
		this.sessionCookieAdmin = sessionCookie;
	}

	public void setStubSessionCookie(String sessionCookie) {
		this.sessionCookieStub = sessionCookie;
	}

	public void setSessionWindowId(String sessionWindowid) {
		this.sessionWindowId = sessionWindowid;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setCookieStubHeaderName(String name) {
		cookieStubHeaderName = name;
	}

	public Map<HttpHeaders, String> getCommonRequestHeaders() {
		Map<HttpHeaders, String> headers = new HashMap<HttpHeaders, String>();
		headers.put(HttpHeaders.HOST, domain);
		headers.put(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:20.0) Gecko/20100101 Firefox/20.0");

		String cookies = "";
		if (StringUtils.isNotEmpty(sessionCookieApp)) {
			cookies = cookies + HttpConstants.COOKIE_APP + sessionCookieApp + ";";
			cookies = cookies + HttpConstants.COOKIE_APP2 + sessionCookieApp + ";";
		}

		if (StringUtils.isNotEmpty(sessionCookieAdmin)) {
			cookies = cookies + HttpConstants.COOKIE_ADMIN + sessionCookieAdmin + ";";
			cookies = cookies + HttpConstants.COOKIE_ADMIN2 + sessionCookieAdmin + ";";
		}

		if (StringUtils.isNotEmpty(sessionCookieStub)) {
			cookies = cookies + cookieStubHeaderName + sessionCookieStub + ";";
		}

		if (StringUtils.isNotEmpty(sessionWindowId)) {
			cookies = cookies + HttpConstants.WINDOW_ID + sessionWindowId + ";";
		}
		headers.put(HttpHeaders.COOKIE, cookies);
		return headers;
	}

	private Map<HttpHeaders, String> getPostRequestHeaders() {
		Map<HttpHeaders, String> headers = getCommonRequestHeaders();
		headers.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
		return headers;
	}

	private Map<HttpHeaders, String> getGetRequestHeaders() {
		return getCommonRequestHeaders();
	}

	private String buildUrl(String urlPath) {
		return HttpConstants.URL_PROTOCOL + domain + urlPath;
	}
}
