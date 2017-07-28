package aaa.helpers.http.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aaa.helpers.http.impl.HttpConstants;
import aaa.helpers.http.impl.HttpHelper;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.exceptions.IstfException;

public class HttpHelper {

	private static String host = PropertyProvider.getProperty(TestProperties.APP_HOST);
	private static String euTemplate = PropertyProvider.getProperty(TestProperties.EU_URL_TEMPLATE);
	private static String adTemplate = PropertyProvider.getProperty(TestProperties.AD_URL_TEMPLATE);

	private HttpHelper() {
	}

	public static String find(String string, String regex) throws IOException {
		return HttpHelper.find(string, regex, 1);
	}

	public static String find(String string, String regex, int index) throws IOException {
		Pattern r = Pattern.compile(regex);
		Matcher m = r.matcher(string);

		if (!m.find()) {
			throw new IOException("Cannot extract regex (" + regex + ") from string");
		}
		return m.group(index);
	}

	// URL helpers

	public static String getEuDomain() {
		return getEuUrl().getHost() + ":" + getEuUrl().getPort();
	}

	public static String getAdDomain() {
		return getAdUrl().getHost() + ":" + getAdUrl().getPort();
	}

	public static String getAdPort() {
		return getAdUrl().getPort() + "";
	}

	public static String getEuPort() {
		return getEuUrl().getPort() + "";
	}

	public static String getHost() {
		return host;
	}

	private static URL getEuUrl() {
		URL url;
		try {
			url = new URL(HttpConstants.URL_PROTOCOL + host + euTemplate);
		} catch (MalformedURLException e) {
			throw new IstfException(e);
	}
		return url;
	}

	public static String getEuLoginUrl() {
		return getEuUrl().toString();
}

	private static URL getAdUrl() {
		URL url;
		try {
			url = new URL(HttpConstants.URL_PROTOCOL + host + adTemplate);
		} catch (MalformedURLException e) {
			throw new IstfException(e);
		}
		return url;
	}

	public static String getAdLoginUrl() {
		return getAdUrl().toString();
	}

	public static String getEuPath() {
		return getEuUrl().getPath();
	}

	public static String getAdPath() {
		return getAdUrl().getPath();
	}

}
