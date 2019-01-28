package aaa.helpers.http.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.exigen.ipb.eisa.base.app.CSAAApplicationFactory;
import toolkit.exceptions.IstfException;

public class HttpHelper {

	private HttpHelper() {
	}

	public static int getAdPort() {
		return CSAAApplicationFactory.get().adminApp().getPort();
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

	public static int getEuPort() {
		return CSAAApplicationFactory.get().mainApp().getPort();
	}

	public static String getHost() {
		return CSAAApplicationFactory.get().mainApp().getHost();
	}

	public static String find(String string, String regex) throws IOException {
		return find(string, regex, 1);
	}

	private static URL getEuUrl() {
		URL url;
		try {
			url = new URL(CSAAApplicationFactory.get().mainApp().getUrl());
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
			url = new URL(CSAAApplicationFactory.get().adminApp().getUrl());
		} catch (MalformedURLException e) {
			throw new IstfException(e);
		}
		return url;
	}

	public static String getAdLoginUrl() {
		return getAdUrl().toString();
	}

}
