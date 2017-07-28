package aaa.helpers.http.impl;

public final class HttpConstants {

	public static final String REGEX_SESSION_COOKIE_APP = "JSESSIONID(_APP)?=([^\\;]+)";
	public static final String REGEX_SESSION_COOKIE_ADMIN = "JSESSIONID(_ADMIN)?=([^\\;]+)";
	public static final String REGEX_SESSION_COOKIE_STUB = "JSESSIONID_STUB=([^\\;]+)";
	public static final String REGEX_SESSION_WINDOW_ID = "_windowId=(.*)";

	public static final String COOKIE_APP = "JSESSIONID_APP=";
	public static final String COOKIE_APP2 = "JSESSIONID";
	public static final String COOKIE_ADMIN = "JSESSIONID=";
	public static final String COOKIE_ADMIN2 = "JSESSIONID_ADMIN=";
	public static final String COOKIE_STUB = "JSESSIONID_STUB=";
	public static final String WINDOW_ID = "window.name=";
	public static final String URL_PROTOCOL = "http://";

	public static final String REGEX_OPEN_TOKEN = "id='token' value='([^']+)'";
	public static final String REGEX_VIEW_STATE = "id=\"javax.faces.ViewState\" value=\"([^\"]+)\"";

}
