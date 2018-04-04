package aaa.modules.regression.service.helper;

import static aaa.admin.modules.IAdmin.log;
import java.util.HashMap;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.xerces.impl.dv.util.Base64;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import aaa.helpers.config.CustomTestProperties;
import aaa.modules.regression.service.helper.dtoAdmin.RfiDocumentResponse;
import aaa.modules.regression.service.helper.dtoDxp.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.platform.win32.Guid;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;

import javax.ws.rs.client.*;
import java.util.Map;

public class HelperCommon {
	private static final String ADMIN_DOCUMENTS_RFI_DOCUMENTS_ENDPOINT = "/aaa-admin/services/aaa-policy-rs/v1/documents/rfi-documents/";
	private static final String DXP_CONTACT_INFO_UPDATE_ENDPOINT = "/api/v1/policies/%s/contact-info";
	private static final String DXP_ENDORSEMENTS_VALIDATE_ENDPOINT = "/api/v1/policies/%s/start-endorsement-info";
	private static final String DXP_VIN_VALIDATE_ENDPOINT = "/api/v1/policies/%s/vin-info/%s";
	private static final String DXP_ENDORSEMENT_START_ENDPOINT = "/api/v1/policies/%s/endorsement";
	private static final String DXP_VIEW_ENDORSEMENT_VEHICLES_ENDPOINT = "/api/v1/policies/%s/endorsement/vehicles";
	private static final String DXP_VIEW_VEHICLES_ENDPOINT = "/api/v1/policies/%s/vehicles";
	private static final String DXP_VIEW_RENEWAL_ENDPOINT = "/api/v1/policies/%s/renewal";
	private static final String DXP_VIEW_POLICY_ENDPOINT = "/api/v1/policies/%s";
	private static final String DXP_ADD_VEHICLE_ENDPOINT = "/api/v1/policies/%s/endorsement/vehicles";
	private static final String DXP_LOOKUP_NAME_ENDPOINT = "/api/v1/lookups/%s?productCd=%s&riskStateCd=%s";
	private static final String DXP_LOCK_UNLOCK_SERVICES = "/api/v1/policies/%s/lock";
	private static final String DXP_UPDATE_VEHICLE_ENDPOINT="/api/v1/policies/%s/endorsement/vehicles/%s";
	private static final String DXP_ENDORSEMENT_BIND_ENDPOINT = "/api/v1/policies/%s/endorsement/bind";
	private static final String DXP_ENDORSEMENT_RATE_ENDPOINT = "/api/v1/policies/%s/endorsement/rate";
	private static final String DXP_VIEW_ENDORSEMENT_DRIVER_ASSIGNMENT="/api/v1/policies/%s/endorsement/assignments";
	private static final String DXP_VIEW_PREMIUM_POLICY ="/api/v1/policies/%s/premiums";
	private static final String DXP_VIEW_PREMIUM_ENDORSEMENT="/api/v1/policies/%s/endorsement/premiums";
	private static final String APPLICATION_CONTEXT_HEADER = "X-ApplicationContext";
	private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();

	private static String urlBuilderDxp(String endpointUrlPart) {
		if (Boolean.valueOf(PropertyProvider.getProperty(CustomTestProperties.SCRUM_ENVS_SSH)).equals(true)) {
			return PropertyProvider.getProperty(CustomTestProperties.DXP_PROTOCOL) + PropertyProvider.getProperty(CustomTestProperties.APP_HOST).replace(PropertyProvider.getProperty(CustomTestProperties.DOMAIN_NAME), "") + PropertyProvider.getProperty(CustomTestProperties.DXP_PORT) + endpointUrlPart;
		}
		return PropertyProvider.getProperty(CustomTestProperties.DOMAIN_NAME) + endpointUrlPart;
	}

	private static String urlBuilderAdmin(String endpointUrlPart) {
		return "http://" + PropertyProvider.getProperty(CustomTestProperties.APP_HOST) + PropertyProvider.getProperty(CustomTestProperties.ADMIN_PORT) + endpointUrlPart;
	}

	public static RfiDocumentResponse[] executeRequestRfi(String policyNumber, String date) {
		String requestUrl = urlBuilderAdmin(ADMIN_DOCUMENTS_RFI_DOCUMENTS_ENDPOINT) + policyNumber + "/" + date;
		return runJsonRequestGetAdmin(requestUrl, RfiDocumentResponse[].class);
	}

	static void executeContactInfoRequest(String policyNumber, String emailAddressChanged, String authorizedBy) {
		UpdateContactInfoRequest request = new UpdateContactInfoRequest();
		request.email = emailAddressChanged;
		request.authorizedBy = authorizedBy;
		String requestUrl = urlBuilderDxp(String.format(DXP_CONTACT_INFO_UPDATE_ENDPOINT, policyNumber));
		runJsonRequestPostDxp(requestUrl, request);
	}

	static ValidateEndorsementResponse executeEndorsementsValidate(String policyNumber, String endorsementDate) {
		String requestUrl = urlBuilderDxp(String.format(DXP_ENDORSEMENTS_VALIDATE_ENDPOINT, policyNumber));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		return runJsonRequestGetDxp(requestUrl, ValidateEndorsementResponse.class);
	}

	static ValidateEndorsementResponse executeEndorsementsValidate(String policyNumber, String endorsementDate, String sessionId) {
		final RestRequestInfo<ValidateEndorsementResponse> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.responseType = ValidateEndorsementResponse.class;
		restRequestInfo.sessionId = sessionId;
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_ENDORSEMENTS_VALIDATE_ENDPOINT, policyNumber));
		if (endorsementDate != null) {
			restRequestInfo.url = restRequestInfo.url + "?endorsementDate=" + endorsementDate;
		}
		return runJsonRequestGetDxp(restRequestInfo);
	}

	static Vehicle updateVehicle(String policyNumber, String oid, VehicleUpdateDto request) {

		String requestUrl = urlBuilderDxp(String.format(DXP_UPDATE_VEHICLE_ENDPOINT, policyNumber, oid));
		return runJsonRequestPostDxp(requestUrl, request, Vehicle.class);
	}


	static AAAVehicleVinInfoRestResponseWrapper executeVinValidate(String policyNumber, String vin, String endorsementDate) {
		String requestUrl = urlBuilderDxp(String.format(DXP_VIN_VALIDATE_ENDPOINT, policyNumber, vin));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		return runJsonRequestGetDxp(requestUrl, AAAVehicleVinInfoRestResponseWrapper.class);
	}

	static ErrorResponseDto validateEndorsementResponseError(String policyNumber, String endorsementDate, int status) {
		final RestRequestInfo<ErrorResponseDto> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_ENDORSEMENTS_VALIDATE_ENDPOINT, policyNumber));
		restRequestInfo.status = status;
		restRequestInfo.responseType = ErrorResponseDto.class;
		if (endorsementDate != null) {
			restRequestInfo.url = restRequestInfo.url + "?endorsementDate=" + endorsementDate;
		}
		return runJsonRequestGetDxp(restRequestInfo);
	}

	static PolicyLockUnlockDto executePolicyLockService(String policyNumber, int status, String sessionId) {
		final RestRequestInfo<PolicyLockUnlockDto> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_LOCK_UNLOCK_SERVICES, policyNumber));
		restRequestInfo.responseType = PolicyLockUnlockDto.class;
		restRequestInfo.status = status;
		restRequestInfo.sessionId = sessionId;
		return runJsonRequestPostDxp(restRequestInfo);
	}

	static PolicyLockUnlockDto executePolicyUnlockService(String policyNumber, int status, String sessionId) {
		final RestRequestInfo<PolicyLockUnlockDto> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_LOCK_UNLOCK_SERVICES, policyNumber));
		restRequestInfo.responseType = PolicyLockUnlockDto.class;
		restRequestInfo.status = status;
		restRequestInfo.sessionId = sessionId;
		return runJsonRequestDeleteDxp(restRequestInfo);
	}

	static Vehicle[] executeVehicleInfoValidate(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_VIEW_VEHICLES_ENDPOINT, policyNumber));
		return runJsonRequestGetDxp(requestUrl, Vehicle[].class);
	}

	static Vehicle executeVehicleAddVehicle(String policyNumber, String purchaseDate, String vin) {
		String requestUrl = urlBuilderDxp(String.format(DXP_ADD_VEHICLE_ENDPOINT, policyNumber));
		Vehicle request = new Vehicle();
		request.purchaseDate = purchaseDate;
		request.vehIdentificationNo = vin;
		return runJsonRequestPostDxp(requestUrl, request, Vehicle.class, 201);
	}

	static Vehicle[] pendedEndorsementValidateVehicleInfo(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_VIEW_ENDORSEMENT_VEHICLES_ENDPOINT, policyNumber));
		return runJsonRequestGetDxp(requestUrl, Vehicle[].class);
	}

	static DriverAssignmentDto[] pendedEndorsementDriverAssignmentInfo(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_VIEW_ENDORSEMENT_DRIVER_ASSIGNMENT, policyNumber));
		return runJsonRequestGetDxp(requestUrl, DriverAssignmentDto[].class);
	}

	static PolicyPremiumInfo[] viewPremiumInfo(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_VIEW_PREMIUM_POLICY, policyNumber));
		return runJsonRequestGetDxp(requestUrl, PolicyPremiumInfo[].class);
	}

	static PolicyPremiumInfo[] viewPremiumInfoPendedEndorsementResponse(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_VIEW_PREMIUM_ENDORSEMENT, policyNumber));
		return runJsonRequestGetDxp(requestUrl, PolicyPremiumInfo[].class);
	}

	static AAAEndorseResponse executeEndorseStart(String policyNumber, String endorsementDate) {
		AAAEndorseRequest request = new AAAEndorseRequest();
		request.endorsementDate = endorsementDate;
		request.endorsementReason = "OTHPB";
		request.endorsementReasonOther = "Some reason why endorsement was done";
		String requestUrl = urlBuilderDxp(String.format(DXP_ENDORSEMENT_START_ENDPOINT, policyNumber));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		return runJsonRequestPostDxp(requestUrl, request, AAAEndorseResponse.class, Response.Status.CREATED.getStatusCode());
	}

	@SuppressWarnings("unchecked")
	static PolicySummary executeViewPolicyRenewalSummary(String policyNumber, String term, int code) {
		String endPoint;
		if ("policy".equals(term)) {
			endPoint = DXP_VIEW_POLICY_ENDPOINT;
		} else {
			endPoint = DXP_VIEW_RENEWAL_ENDPOINT;
		}
		String requestUrl = urlBuilderDxp(String.format(endPoint, policyNumber));
		return runJsonRequestGetDxp(requestUrl, PolicySummary.class, code);
	}

	@SuppressWarnings("unchecked")
	static HashMap<String, String> executeLookupValidate(String lookupName, String productCd, String riskStateCd, String effectiveDate) {
		String requestUrl = urlBuilderDxp(String.format(DXP_LOOKUP_NAME_ENDPOINT, lookupName, productCd, riskStateCd));
		if (effectiveDate != null) {
			requestUrl = requestUrl + "&effectiveDate=" + effectiveDate;
		}
		return runJsonRequestGetDxp(requestUrl, HashMap.class);
	}

	static String executeEndorsementBind(String policyNumber, String authorizedBy, int status) {
		AAABindEndorsementRequestDTO request = new AAABindEndorsementRequestDTO();
		request.authorizedBy = authorizedBy;
		String requestUrl = urlBuilderDxp(String.format(DXP_ENDORSEMENT_BIND_ENDPOINT, policyNumber));
		return runJsonRequestPostDxp(requestUrl, request, String.class, status);
	}

	static PolicyPremiumInfo[] executeEndorsementRate(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_ENDORSEMENT_RATE_ENDPOINT, policyNumber));
		return runJsonRequestPostDxp(requestUrl, null, PolicyPremiumInfo[].class, status);
	}

	protected static String runJsonRequestPostDxp(String url, RestBodyRequest bodyRequest) {
		return runJsonRequestPostDxp(url, bodyRequest, String.class);
	}

	private static <T> T runJsonRequestPostDxp(String url, RestBodyRequest bodyRequest, Class<T> responseType) {
		return runJsonRequestPostDxp(url, bodyRequest, responseType, Response.Status.OK.getStatusCode());
	}

	public static <T> T runJsonRequestPostDxp(String url, RestBodyRequest bodyRequest, Class<T> responseType, int status) {
		final RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = url;
		restRequestInfo.bodyRequest = bodyRequest;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status =  status;
		return runJsonRequestPostDxp(restRequestInfo);
	}

	public static <T> T runJsonRequestPostDxp(RestRequestInfo<T> request) {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			response = createJsonRequest(client, request.url, request.sessionId).post(Entity.json(request.bodyRequest));
			T responseObj = response.readEntity(request.responseType);
			log.info(response.toString());
			if (response.getStatus() != request.status) {
				//handle error
				throw new IstfException(response.readEntity(String.class));
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

	public static <T> T runJsonRequestDeleteDxp(String url, Class<T> responseType) {
		return runJsonRequestDeleteDxp(url, responseType,Response.Status.OK.getStatusCode());
	}

	public static <T> T runJsonRequestDeleteDxp(String url, Class<T> responseType, int status) {
		final RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = url;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestDeleteDxp(restRequestInfo);
	}

	private static <T> T runJsonRequestDeleteDxp(RestRequestInfo<T> request) {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			response = createJsonRequest(client, request.url, request.sessionId).delete();
			T responseObj = response.readEntity(request.responseType);
			log.info(response.toString());
			if (response.getStatus() != request.status) {
				//handle error
				throw new IstfException(response.readEntity(String.class));
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

	public static <T> T runJsonRequestGetDxp(String url, Class<T> responseType) {
		return runJsonRequestGetDxp(url, responseType, 200);
	}

	public static <T> T runJsonRequestGetDxp(String url, Class<T> responseType, int status){
		final RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = url;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestGetDxp(restRequestInfo);
	}

	private static <T> T runJsonRequestGetDxp(RestRequestInfo<T> request) {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			response = createJsonRequest(client, request.url, request.sessionId).get();
			T result = response.readEntity(request.responseType);
			log.info(response.toString());
			if (response.getStatus() != request.status) {
				//handle error
				throw new IstfException(response.readEntity(String.class));
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

	private static <T> T runJsonRequestGetAdmin(String url, Class<T> returnClazz) {
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
				throw new IstfException(response.readEntity(String.class));
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

	private static Invocation.Builder createJsonRequest(Client client, String url, String sessionId) {
		Invocation.Builder builder = client.target(url).request().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		if(BooleanUtils.toBoolean(PropertyProvider.getProperty(CustomTestProperties.OAUTH2_ENABLED))) {
			final String token = getBearerToken();
			if(StringUtils.isNotEmpty(token)) {
				builder = builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			}
		}
		return builder.header(APPLICATION_CONTEXT_HEADER, createApplicationContext(sessionId));
	}

	private static String getBearerToken() {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(PropertyProvider.getProperty(CustomTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE)
					+ PropertyProvider.getProperty(CustomTestProperties.PING_HOST));
			response = target
					.request()
					.header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED)
					.post(Entity.json(GetOAuth2TokenRequest.create().asUrlEncoded()));
			final Map result = response.readEntity(Map.class);
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

	private static String createApplicationContext(String sessionId) {
		try {
			final ApplicationContext applicationContext = new ApplicationContext();
			applicationContext.address = "AutomationTest";
			applicationContext.application = "AutomationTest";
			applicationContext.correlationId = Guid.GUID.newGuid().toString();
			applicationContext.subSystem = "AutomationTest";
			applicationContext.transactionType = "AutomationTest";
			applicationContext.userId = "MyPolicy";
			applicationContext.sessionId = sessionId;
			return DEFAULT_OBJECT_MAPPER.writeValueAsString(applicationContext);
		} catch (JsonProcessingException e) {
			throw new IstfException("Failed to create application context");
		}
	}
}
