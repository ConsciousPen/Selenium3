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
import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.base.app.Application;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import aaa.helpers.config.CustomTestProperties;
import aaa.main.modules.swaggerui.SwaggerUiTab;
import aaa.modules.regression.service.helper.dtoAdmin.RfiDocumentResponse;
import aaa.modules.regression.service.helper.dtoDxp.*;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.waiters.Waiters;

public class HelperCommon {
	private static String swaggerUiUrl = PropertyProvider.getProperty(CustomTestProperties.APP_HOST) + PropertyProvider.getProperty(CustomTestProperties.DXP_PORT) + PropertyProvider
			.getProperty(CustomTestProperties.APP_SWAGGER_URL_TEMPLATE);

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
	private static final String RATING_URL_TEMPLATE = "http://"+ PropertyProvider.getProperty(CustomTestProperties.APP_HOST)+":9089/aaa-rating-engine-app/REST/ws/home-ca";
	private static final String RATING_SERVICE_TYPE = "/determineDiscountPercentage";
	private static final String DXP_LOCK_UNLOCK_SERVICES = "/api/v1/policies/%s/lock";

	private static String urlBuilderDxp(String endpointUrlPart) {
		return PropertyProvider.getProperty(CustomTestProperties.DXP_PROTOCOL) + PropertyProvider.getProperty(CustomTestProperties.APP_HOST).replace(PropertyProvider.getProperty(CustomTestProperties.DOMAIN_NAME), "") + PropertyProvider.getProperty(CustomTestProperties.DXP_PORT) + endpointUrlPart;
	}

	private static String urlBuilderAdmin(String endpointUrlPart) {
		return "http://" + PropertyProvider.getProperty(CustomTestProperties.APP_HOST) + PropertyProvider.getProperty(CustomTestProperties.ADMIN_PORT) + endpointUrlPart;
	}

	public static <T> RfiDocumentResponse[] executeRequestRfi(String policyNumber, String date) {
		String requestUrl = urlBuilderAdmin(ADMIN_DOCUMENTS_RFI_DOCUMENTS_ENDPOINT) + policyNumber + "/" + date;
		RfiDocumentResponse[] result = runJsonRequestGetAdmin(requestUrl, RfiDocumentResponse[].class);
		return result;
	}

	static void executeContactInfoRequest(String policyNumber, String emailAddressChanged, String authorizedBy) {
		if (Boolean.parseBoolean(PropertyProvider.getProperty(CustomTestProperties.USE_SWAGGER))) {
			emailUpdateSwaggerUi(policyNumber, emailAddressChanged, authorizedBy);
		} else {
			UpdateContactInfoRequest request = new UpdateContactInfoRequest();
			request.email = emailAddressChanged;
			request.authorizedBy = authorizedBy;
			String requestUrl = urlBuilderDxp(String.format(DXP_CONTACT_INFO_UPDATE_ENDPOINT, policyNumber));
			runJsonRequestPostDxp(requestUrl, request);
		}
	}

	static ValidateEndorsementResponse executeEndorsementsValidate(String policyNumber, String endorsementDate) {
		String requestUrl = urlBuilderDxp(String.format(DXP_ENDORSEMENTS_VALIDATE_ENDPOINT, policyNumber));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		ValidateEndorsementResponse validateEndorsementResponse = runJsonRequestGetDxp(requestUrl, ValidateEndorsementResponse.class);
		return validateEndorsementResponse;
	}

	static AAAVehicleVinInfoRestResponseWrapper executeVinValidate(String policyNumber, String vin, String endorsementDate) {
		String requestUrl = urlBuilderDxp(String.format(DXP_VIN_VALIDATE_ENDPOINT, policyNumber, vin));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		AAAVehicleVinInfoRestResponseWrapper validateVinResponse = runJsonRequestGetDxp(requestUrl, AAAVehicleVinInfoRestResponseWrapper.class);
		return validateVinResponse;
	}

	static ErrorResponseDto validateEndorsementResponseError(String policyNumber, String endorsementDate, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_ENDORSEMENTS_VALIDATE_ENDPOINT, policyNumber));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		ErrorResponseDto validateEndorsementResponseError = runJsonRequestGetDxp(requestUrl, ErrorResponseDto.class, status);
		return validateEndorsementResponseError;
	}

	static PolicyLockUnlockDto executePolicyLockService(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_LOCK_UNLOCK_SERVICES, policyNumber));
		PolicyLockUnlockDto validatePolicyLockStatus = runJsonRequestPostDxp(requestUrl, null, PolicyLockUnlockDto.class, status);
		return validatePolicyLockStatus;
	}

	static PolicyLockUnlockDto executePolicyUnlockService(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_LOCK_UNLOCK_SERVICES, policyNumber));
		PolicyLockUnlockDto validatePolicyUnlockStatus = runJsonRequestDeleteDxp(requestUrl, PolicyLockUnlockDto.class, status);
		return validatePolicyUnlockStatus;
	}

	static Vehicle[] executeVehicleInfoValidate(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_VIEW_VEHICLES_ENDPOINT, policyNumber));
		Vehicle[] validateVehicleResponse = runJsonRequestGetDxp(requestUrl, Vehicle[].class);
		return validateVehicleResponse;
	}

	static Vehicle executeVehicleAddVehicle(String policyNumber, String purchaseDate, String vin) {
		String requestUrl = urlBuilderDxp(String.format(DXP_ADD_VEHICLE_ENDPOINT, policyNumber));
		Vehicle request = new Vehicle();
		request.purchaseDate = purchaseDate;
		request.vehIdentificationNo = vin;
		Vehicle vehicle = runJsonRequestPostDxp(requestUrl, request, Vehicle.class, 201);
		return vehicle;
	}

	static Vehicle[] pendedEndorsementValidateVehicleInfo(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_VIEW_ENDORSEMENT_VEHICLES_ENDPOINT, policyNumber));
		Vehicle[] validateEndorsementVehicleResponse = runJsonRequestGetDxp(requestUrl, Vehicle[].class);
		return validateEndorsementVehicleResponse;
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
		AAAEndorseResponse aaaEndorseResponse = runJsonRequestPostDxp(requestUrl, request, AAAEndorseResponse.class, Response.Status.CREATED.getStatusCode());
		return aaaEndorseResponse;
	}

	static PolicySummary executeViewPolicyRenewalSummary(String policyNumber, String term, int code) {
		String endPoint;
		if ("policy".equals(term)) {
			endPoint = DXP_VIEW_POLICY_ENDPOINT;
		} else {
			endPoint = DXP_VIEW_RENEWAL_ENDPOINT;
		}
		String requestUrl = urlBuilderDxp(String.format(endPoint, policyNumber));
		PolicySummary policySummaryResponse = runJsonRequestGetDxp(requestUrl, PolicySummary.class, code);
		return policySummaryResponse;
	}

	static HashMap<String, String> executeLookupValidate(String lookupName, String productCd, String riskStateCd, String effectiveDate) {
		String requestUrl = urlBuilderDxp(String.format(DXP_LOOKUP_NAME_ENDPOINT, lookupName, productCd, riskStateCd));
		if (effectiveDate != null) {
			requestUrl = requestUrl + "&effectiveDate=" + effectiveDate;
		}
		HashMap <String, String> validateLookupResponse  = runJsonRequestGetDxp(requestUrl, HashMap.class );
		return validateLookupResponse;
	}

	private static void emailUpdateSwaggerUi(String policyNumber, String emailAddress, String authorizedBy) {
		By customerV1EndorsementsPost = SwaggerUiTab.policyV1EndorsementsPost.getLocator();
		Application.open(swaggerUiUrl);
		SwaggerUiTab swaggerUiTab = new SwaggerUiTab();

		Waiters.SLEEP(2000).go();
		SwaggerUiTab.policyV1Endorsements.click();
		SwaggerUiTab.policyV1EndorsementsPost.click();

		SwaggerUiTab.policyNumber.setValue(policyNumber);
		SwaggerUiTab.updateContactInfo.setValue(" { \"email\": \"" + emailAddress + "\",\n"
				+ "  \"authorizedBy\": \"" + authorizedBy + "\"}");
		swaggerUiTab.clickButtonTryIt(customerV1EndorsementsPost);
		//TODO get rid of authentication popup, which cant be handled by Chrome of Firefox
		CustomAssert.assertEquals(swaggerUiTab.getResponseCodeValue(customerV1EndorsementsPost), "200");
		swaggerUiTab.getResponseBodyValue(customerV1EndorsementsPost);
	}

	protected static String runJsonRequestPostDxp(String url, RestBodyRequest request) {
		return runJsonRequestPostDxp(url, request, String.class);
	}

	public static <T> T runJsonRequestPostDxp(String url, RestBodyRequest request, Class<T> responseType) {
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

	public static <T> T runJsonRequestDeleteDxp(String url, Class<T> responseType, int status) {
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
		T result = runJsonRequestGetDxp(url, responseType, 200);
		return result;
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
