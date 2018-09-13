package aaa.helpers.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.http.impl.*;
import aaa.modules.BaseTest;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.exceptions.IstfException;

public class HttpLogin {

	private static final String PARAMS_FILENAME = "login.txt";
	protected static Logger log = LoggerFactory.getLogger(HttpLogin.class);
	private static String user = PropertyProvider.getProperty(TestProperties.APP_USER);
	private static String password = PropertyProvider.getProperty(TestProperties.APP_PASSWORD);

	private HttpLogin() {
	}

	public static HttpAAARequestor loginEu() throws IOException {
		return loginEu(user, password, BaseTest.getState());

	}

	public static HttpAAARequestor loginAd() throws IOException {
		try {
			return loginAd(user, password, BaseTest.getState());
		} catch (Exception e) {
			return loginAd(user, password, BaseTest.getState());
		}
	}

	private static HttpAAARequestor loginAd(String login, String password, String state) throws IOException {
		HttpAAARequestor httpRequestor = new HttpAAARequestor();
		httpRequestor.setDomain(HttpHelper.getAdDomain());

		/*System.setProperty("http.proxyHost", "localhost");
		System.setProperty("http.proxyPort", "8888");*/

		HttpQueryBuilder queryBuilder = new HttpQueryBuilder();
		try {
			queryBuilder.readParamsFile(PARAMS_FILENAME);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			httpRequestor.sendGetRequest(HttpHelper.getAdLoginUrl());
		} catch (IOException e) {
			throw new IstfException("HTTP ERROR: Impossible to open Login page or server is down. \n", e);
		}

		String sessionCookieAdmin = HttpHelper.find(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.SET_COOKIE), HttpConstants.REGEX_SESSION_COOKIE_ADMIN, 2);
		httpRequestor.setAdminSessionCookie(sessionCookieAdmin);

		httpRequestor.sendGetRequest("/aaa-admin/flow?_flowId=ipb-entry-flow&_parentWindowId=");
		String stubLocation = httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.LOCATION);
		httpRequestor.sendGetRequest(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.LOCATION));

		String sessionCookieStub = HttpHelper.find(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.SET_COOKIE), HttpConstants.REGEX_SESSION_COOKIE_STUB, 2);
		if (StringUtils.isEmpty(HttpHelper.find(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.SET_COOKIE), HttpConstants.REGEX_SESSION_COOKIE_STUB, 1))) {
			httpRequestor.setCookieStubHeaderName(HttpConstants.COOKIE_STUB2);
		}
		httpRequestor.setStubSessionCookie(sessionCookieStub);

		if (state == null) {
			state = "UT";
		}

		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("username", login);
		mapping.put("states", state);
		mapping.put("password", password);

		httpRequestor.setDomain(stubLocation);
		httpRequestor.sendPostRequest(stubLocation.split("\\?")[0] + "?", queryBuilder.buildQueryString(0, mapping));

		String token = HttpHelper.find(httpRequestor.getResponse(), HttpConstants.REGEX_OPEN_TOKEN);

		mapping = new HashMap<String, String>();
		mapping.put("username", login);
		mapping.put("opentoken", token);

		httpRequestor.setDomain(HttpHelper.getAdDomain());
		httpRequestor.sendPostRequest("/aaa-admin/do_auth", queryBuilder.buildQueryString(1, mapping));

		String location = httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.LOCATION);
		String sessionWindowId = "";
		int counter = 0;
		while (sessionWindowId.isEmpty() && counter < 3) {
			counter++;
			if (location.startsWith("flow")) {
				location = "/aaa-admin/" + location;
			}
			httpRequestor.sendGetRequest(location);
			sessionWindowId = HttpHelper.find(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.LOCATION), HttpConstants.REGEX_SESSION_WINDOW_ID);
		}
		httpRequestor.setSessionWindowId(sessionWindowId);
		httpRequestor.sendGetRequest(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.LOCATION));

		httpRequestor.sendGetRequest(String.format("/aaa-admin/admin/flow?_flowId=ipb-entry-flow&_parentWindowId=%s", sessionWindowId));
		httpRequestor.sendGetRequest(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.LOCATION));
		httpRequestor.sendGetRequest(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.LOCATION));

		return httpRequestor;
	}

	private static HttpAAARequestor loginEu(String login, String password, String state) throws IOException {
		HttpAAARequestor httpRequestor = new HttpAAARequestor();
		httpRequestor.setDomain(HttpHelper.getEuDomain());
		/*
		 * System.setProperty("http.proxyHost", "localhost");
		 * System.setProperty("http.proxyPort", "8888");
		 */

		HttpQueryBuilder queryBuilder = new HttpQueryBuilder();
		queryBuilder.readParamsFile(PARAMS_FILENAME);

		try {
			httpRequestor.sendGetRequest(HttpHelper.getEuLoginUrl());
		} catch (IOException e) {
			throw new IstfException("HTTP ERROR: Impossible to open Login page or server is down. \n", e);
		}

		String sessionCookieApp = HttpHelper.find(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.SET_COOKIE), HttpConstants.REGEX_SESSION_COOKIE_APP);
		httpRequestor.setAppSessionCookie(sessionCookieApp);

		httpRequestor.sendGetRequest("/aaa-app/flow?_flowId=ipb-entry-flow&_parentWindowId=");
		httpRequestor.sendGetRequest(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.LOCATION));

		String sessionCookieStub = HttpHelper.find(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.SET_COOKIE), HttpConstants.REGEX_SESSION_COOKIE_STUB);
		httpRequestor.setStubSessionCookie(sessionCookieStub);

		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("username", login);
		mapping.put("states", state);
		mapping.put("password", password);

		httpRequestor.sendPostRequest(HttpConstants.URL_PROTOCOL + HttpHelper.getHost() + ":9083/aaa-external-stub-services-app/authentification?", queryBuilder.buildQueryString(0, mapping));

		String token = HttpHelper.find(httpRequestor.getResponse(), HttpConstants.REGEX_OPEN_TOKEN);

		mapping = new HashMap<String, String>();
		mapping.put("username", login);
		mapping.put("opentoken", token);

		httpRequestor.sendPostRequest("/aaa-app/do_auth", queryBuilder.buildQueryString(1, mapping));

		httpRequestor.sendGetRequest(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.LOCATION));
		httpRequestor.sendGetRequest(httpRequestor.getReponseHeader(HttpRequestor.HttpHeaders.LOCATION));

		return httpRequestor;
	}
}
