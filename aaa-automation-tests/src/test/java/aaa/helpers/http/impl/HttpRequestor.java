package aaa.helpers.http.impl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import toolkit.utils.logging.CustomLogger;

public class HttpRequestor {

	private boolean allowRedirects = false;
	private Map<String, String> headers = new HashMap<String, String>();
	private String params;
	private String method;
	private byte[] response;
	private URL url;
	private HttpURLConnection connection;

	protected static Logger log = CustomLogger.getInstance();

	public void setUrl(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	public URL getUrl() {
		return url;
	}

	public void setMethod(HttpMethods method) {
		this.method = method.get();
	}

	public void setHeader(HttpHeaders name, String value) {
		this.headers.put(name.get(), value);
	}

	public void setHeaders(Map<HttpHeaders, String> headers) {
		Iterator<Map.Entry<HttpHeaders, String>> iterator = headers.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<HttpHeaders, String> header = iterator.next();
			this.headers.put(header.getKey().get(), header.getValue());
		}
	}

	public void removeHeader(HttpHeaders name) {
		headers.remove(name.get());
	}

	public void removeAllHeaders() {
		headers.clear();
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getParams() {
		return params;
	}

	public void removeParams() {
		this.params = "";
	}

	public void setAllowRedirects(boolean allowRedirects) {
		this.allowRedirects = allowRedirects;
	}

	public void sendRequest() throws IOException {
		connection = (HttpURLConnection) url.openConnection();
		connection.setInstanceFollowRedirects(allowRedirects);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod(method);

		Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, String> header = iterator.next();
			connection.setRequestProperty(header.getKey(), header.getValue());
		}
		if (!StringUtils.isEmpty(params)) {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
			outputStreamWriter.write(params);
			outputStreamWriter.flush();
			outputStreamWriter.close();
		}
		response = IOUtils.toByteArray(connection.getInputStream());
	}

	public String getResponse() {
		return new String(response).replace(System.getProperty("line.separator"), "");
	}

	public String getResponseOriginal() {
		return new String(response);
	}
	
	public byte[] getResponseAsBytes() {
        return response;
    }

	public String getReponseHeader(HttpHeaders name) {
		return connection.getHeaderField(name.get());
	}

	public enum HttpMethods {
		POST("POST"),
		GET("GET");

		String id;
		private HttpMethods(String id) {
			this.id = id;
		}

		public String get() {
			return this.id;
		}
	}

	public enum HttpHeaders {
		COOKIE("Cookie"),
		SET_COOKIE("Set-Cookie"),
		HOST("Host"),
		REFERER("Referer"),
		ACCEPT("Accept"),
		ACCEPT_ENCODING("Accept-Encoding"),
		ACCEPT_LANGUAGE("Accept-Language"),
		CONNECTION("Connection"),
		USER_AGENT("User-Agent"),
		CONTENT_TYPE("Content-Type"),
		CONTENT_LENGTH("Content-Length"),
		LOCATION("Location"),
		DNT("DNT");

		String id;
		private HttpHeaders(String id) {
			this.id = id;
		}

		public String get() {
			return this.id;
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.method + " " + this.url);
		stringBuilder.append(this.headers.toString());
		stringBuilder.append(params);
		return stringBuilder.toString();
	}
}
