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
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;

public class HelperCommon {
	private static final String ADMIN_DOCUMENTS_RFI_DOCUMENTS_ENDPOINT = "/aaa-admin/services/aaa-policy-rs/v1/documents/rfi-documents/";
	private static final String DXP_CONTACT_INFO_UPDATE_ENDPOINT = "/api/v1/policies/%s/contact-info";
	private static final String DXP_ENDORSEMENTS_VALIDATE_ENDPOINT = "/api/v1/policies/%s/start-endorsement-info";
	private static final String DXP_VIN_VALIDATE_ENDPOINT = "/api/v1/policies/%s/vehicles/%s/vin-info";
	private static final String DXP_ENDORSEMENT_START_ENDPOINT = "/api/v1/policies/%s/endorsement";
	private static final String DXP_VIEW_ENDORSEMENT_VEHICLES_ENDPOINT = "/api/v1/policies/%s/endorsement/vehicles";
	private static final String DXP_VIEW_VEHICLES_ENDPOINT = "/api/v1/policies/%s/vehicles";
	private static final String DXP_VIEW_RENEWAL_ENDPOINT = "/api/v1/policies/%s/renewal";
	private static final String DXP_VIEW_POLICY_ENDPOINT = "/api/v1/policies/%s";
	private static final String DXP_ADD_VEHICLE_ENDPOINT = "/api/v1/policies/%s/endorsement/vehicles";
	private static final String DXP_LOOKUP_NAME_ENDPOINT = "/api/v1/lookups/%s?productCd=%s&riskStateCd=%s";
	private static final String DXP_LOCK_UNLOCK_SERVICES = "/api/v1/policies/%s/lock";
	private static final String DXP_UPDATE_VEHICLE_ENDPOINT="/api/v1/policies/%s/endorsement/vehicles/%s";

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
		String requestUrl = urlBuilderDxp(String.format(DXP_ENDORSEMENTS_VALIDATE_ENDPOINT, policyNumber));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		return runJsonRequestGetDxp(requestUrl, ErrorResponseDto.class);
	}

	static PolicyLockUnlockDto executePolicyLockService(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_LOCK_UNLOCK_SERVICES, policyNumber));
		return runJsonRequestPostDxp(requestUrl, null, PolicyLockUnlockDto.class, status);
	}

	static PolicyLockUnlockDto executePolicyUnlockService(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_LOCK_UNLOCK_SERVICES, policyNumber));
		return runJsonRequestDeleteDxp(requestUrl, PolicyLockUnlockDto.class, status);
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
		return runJsonRequestPostDxp(requestUrl, request, Vehicle.class);
	}

	static Vehicle[] pendedEndorsementValidateVehicleInfo(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_VIEW_ENDORSEMENT_VEHICLES_ENDPOINT, policyNumber));
		return runJsonRequestGetDxp(requestUrl, Vehicle[].class);
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


	protected static String runJsonRequestPostDxp(String url, RestBodyRequest request) {
		return runJsonRequestPostDxp(url, request, String.class);
	}

	private static <T> T runJsonRequestPostDxp(String url, RestBodyRequest request, Class<T> responseType) {
		return runJsonRequestPostDxp(url, request, responseType, Response.Status.OK.getStatusCode());
	}

	public static <T> T runJsonRequestPostDxp(String url, RestBodyRequest request, Class<T> responseType, int status) {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(url);

			response = target
					.request()
					.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encode("admin:admin".getBytes()))
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.post(Entity.json(request));
			T responseObj = response.readEntity(responseType);
			log.info(response.toString());
			if (response.getStatus() != status) {
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
		return runJsonRequestDeleteDxp(url, responseType, Response.Status.OK.getStatusCode());
	}

	private static <T> T runJsonRequestDeleteDxp(String url, Class<T> responseType, int status) {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(url);

			response = target
					.request()
					.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encode("admin:admin".getBytes()))
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.delete();
			T responseObj = response.readEntity(responseType);
			log.info(response.toString());
			if (response.getStatus() != status) {
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

	private static <T> T runJsonRequestGetDxp(String url, Class<T> responseType) {
		return runJsonRequestGetDxp(url, responseType, 200);
	}

	private static <T> T runJsonRequestGetDxp(String url, Class<T> responseType, int status) {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(url);

			response = target
					.request()
					.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encode("admin:admin".getBytes()))
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.get();
			T result = response.readEntity(responseType);
			log.info(response.toString());
				if (response.getStatus() != status) {
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

}
