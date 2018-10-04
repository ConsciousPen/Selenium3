package aaa.helpers.rest;

import static aaa.admin.modules.IAdmin.log;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.xerces.impl.dv.util.Base64;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jna.platform.win32.Guid;
import aaa.config.CsaaTestProperties;
import aaa.helpers.rest.dtoDxp.ApplicationContext;
import aaa.helpers.rest.dtoDxp.GetOAuth2TokenRequest;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;

public class JsonClient {

	private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();
	private static final ObjectMapper PRETTY_PRINT_OBJECT_MAPPER = new ObjectMapper();
	static {
		PRETTY_PRINT_OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
	}
	private static final String APPLICATION_CONTEXT_HEADER = "X-ApplicationContext";

	/**
	 * Generic request method implementation for specific request and method type.
	 * @param request - rest request to send.
	 * @param requestType - request method to use.
	 * @param <T> - response body class type.
	 * @return response instance of specific class.
	 */
	public static <T> T sendJsonRequest(RestRequestInfo<T> request, RequestMethod requestType) {
		Client client = null;
		Response response = null;
		try {
			log.info("Request: " + asJson(request));
			if (RequestMethod.DELETE == requestType) {
				ClientConfig config = new ClientConfig();
				config.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
				client = ClientBuilder.newClient(config).register(JacksonJsonProvider.class);
			} else {
				client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			}
			if(requestType == RequestMethod.PATCH) {
				client = client.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
			}

			Invocation.Builder jsonRequest = createJsonRequest(client, request.url, request.sessionId);

			String methodName = requestType.name();
			if (request.bodyRequest != null) {
				response = jsonRequest.method(methodName, Entity.json(request.bodyRequest));
			} else {
				response = jsonRequest.method(methodName);
			}
			T result = readBufferedEntity(response, request.responseType);
			log.info(response.toString());
			if (response.getStatus() != request.status) {
				//handle error
				throw new IstfException(methodName + " json request failed");
			}
			log.info("Response: " + asJson(result));
			return result;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}
	/**
	 * todo
	 */
	//WORKING
	public static <T> T runJsonRequestPatch(String url, RestBodyRequest request, Class<T> responseType, int status) {
		Client client = null;
		Response response = null;
		log.info("Request: " + asJson(request));
		try {
			client = ClientBuilder.newClient().property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true).register(JacksonJsonProvider.class);

			String token = getBearerToken();

			response = client.target(url)
					.request()
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.method("PATCH", Entity.json(request));
			response.bufferEntity();
			T responseObj = response.readEntity(responseType);
			log.info(response.toString());
			if (response.getStatus() != status) {
				//handle error
				throw new IstfException("PATCH json response failed");
			}
			return responseObj;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	public static <T> T runJsonRequestPutDxp(String url, RestBodyRequest bodyRequest, Class<T> responseType) {
		return runJsonRequestPutDxp(url, bodyRequest, responseType, Response.Status.OK.getStatusCode());
	}

	/**
	 *  Migrate to {@link #sendJsonRequest(RestRequestInfo, RequestMethod)}
	 */
	@Deprecated
	public static <T> T runJsonRequestPutDxp(String url, RestBodyRequest request, Class<T> responseType, int status) {
		Client client = null;
		Response response = null;
		log.info("Request: " + asJson(request));
		try {
			client = ClientBuilder.newClient().property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true).register(JacksonJsonProvider.class);

			String token = getBearerToken();

			response = client.target(url)
					.request()
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.method("PUT", Entity.json(request));
			T responseObj = readBufferedEntity(response, responseType);
			log.info(response.toString());
			if (response.getStatus() != status) {
				//handle error
				throw new IstfException("PATCH json response failed");
			}
			return responseObj;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	public static <T> T runJsonRequestGetAdmin(String url, Class<T> returnClazz) {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(url);

			response = target
					.request()
					.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encode("qa:qa".getBytes()))
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.get();
			T result = response.readEntity(returnClazz);
			log.info(response.toString());
			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				//handle error
				throw new IstfException("GET json request failed");
			}
			return result;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	/**
	 *  Migrate to {@link #sendJsonRequest(RestRequestInfo, RequestMethod)}
	 */
	@Deprecated
	public static <T> T runJsonRequestPostAdmin(String url, RestBodyRequest request, Class<T> responseType, int status) {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(url);

			response = target
					.request()
					.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encode("qa:qa".getBytes()))
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.method("POST", Entity.json(request));
			T responseObj = readBufferedEntity(response, responseType);
			log.info(response.toString());
			if (response.getStatus() != status) {
				//handle error
				throw new IstfException("POST?PATCH? json response failed");
			}
			return responseObj;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	/**
	 * Buffers method body and try to read response of expected type. If {@link ProcessingException} is thrown method
	 * attempts to parse and log body as string and rethrown the exception.
	 * Exception will not be caught if not expected nor error response body is parsed.
	 * @param response service response to read from
	 * @param responseType expected response class
	 * @param <T> - expected response type
	 * @return response instance of specific class
	 */
	private static <T> T readBufferedEntity(Response response, Class<T> responseType) {
		if (response.bufferEntity()) {
			try {
				return response.readEntity(responseType);
			} catch (ProcessingException e) {
				log.error("Actual response: " + System.lineSeparator() + asJson(response.readEntity(String.class)));
			}
		}
		return response.readEntity(responseType);
	}

	private static String asJson(Object object) {
		try {
			return PRETTY_PRINT_OBJECT_MAPPER.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("Failed to parse request/response as json", e);
			return null;
		}
	}

	private static Invocation.Builder createJsonRequest(Client client, String url, String sessionId) {
		Invocation.Builder builder = client.target(url).request().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		if (BooleanUtils.toBoolean(PropertyProvider.getProperty(CsaaTestProperties.OAUTH2_ENABLED))) {
			String token = getBearerToken();
			if (StringUtils.isNotEmpty(token)) {
				builder = builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			}
		}
		return builder.header(APPLICATION_CONTEXT_HEADER, createApplicationContext(sessionId));
	}

	private static String createApplicationContext(String sessionId) {
		try {
			ApplicationContext applicationContext = new ApplicationContext();
			applicationContext.address = "AutomationTest";
			applicationContext.application = "MyPolicy";
			applicationContext.correlationId = Guid.GUID.newGuid().toString();
			applicationContext.sessionId = sessionId;
			return DEFAULT_OBJECT_MAPPER.writeValueAsString(applicationContext);
		} catch (JsonProcessingException e) {
			throw new IstfException("Failed to create application context");
		}
	}

	private static String getBearerToken() {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(PropertyProvider.getProperty(CsaaTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE) + PropertyProvider.getProperty(CsaaTestProperties.PING_HOST));
			response = target
					.request()
					.header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED)
					.post(Entity.json(GetOAuth2TokenRequest.create().asUrlEncoded()));

			Map result = response.readEntity(HashMap.class);

			return result.get("access_token").toString();
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}
}
