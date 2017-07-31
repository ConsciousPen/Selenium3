package aaa.helpers.http;

import java.io.IOException;

import aaa.helpers.http.impl.HttpAAARequestor;
import aaa.helpers.http.impl.HttpRequestor.HttpHeaders;

public class HttpLogout {

	private HttpLogout() {
	}

	public static void logoutAdmin(HttpAAARequestor httpRequestor) throws IOException {
		httpRequestor.sendGetRequest("/aaa-admin/admin/flow?_j_acegi_logout=_j_acegi_logout&_admin_app=true");
		httpRequestor.sendGetRequest(httpRequestor.getReponseHeader(HttpHeaders.LOCATION));
		httpRequestor.sendGetRequest(httpRequestor.getReponseHeader(HttpHeaders.LOCATION));
	}

	public static void logoutApp(HttpAAARequestor httpRequestor) throws IOException {
		httpRequestor.sendGetRequest("/aaa-admin/flow?_j_acegi_logout=_j_acegi_logout&_admin_app=false");
		httpRequestor.sendGetRequest(httpRequestor.getReponseHeader(HttpHeaders.LOCATION));
		httpRequestor.sendGetRequest(httpRequestor.getReponseHeader(HttpHeaders.LOCATION));
	}
}
