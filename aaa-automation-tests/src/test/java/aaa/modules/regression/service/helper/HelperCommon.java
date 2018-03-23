package aaa.modules.regression.service.helper;

import static aaa.admin.modules.IAdmin.log;
import static org.assertj.core.api.Assertions.assertThat;
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
import org.openqa.selenium.WebDriver;
import com.exigen.ipb.etcsa.base.app.Application;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import aaa.helpers.config.CustomTestProperties;
import aaa.main.modules.swaggerui.SwaggerUiTab;
import aaa.modules.regression.service.helper.dtoAdmin.RfiDocumentResponse;
import aaa.modules.regression.service.helper.dtoDxp.*;
import aaa.modules.regression.service.helper.dtoRating.DiscountPercentageRuntimeContext;
import aaa.modules.regression.service.helper.dtoRating.DiscountRetrieveFullRequest;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.waiters.Waiters;

public class HelperCommon {
	private static String swaggerUiUrl = PropertyProvider.getProperty(CustomTestProperties.APP_HOST) + PropertyProvider.getProperty(CustomTestProperties.DXP_PORT) + PropertyProvider
			.getProperty(CustomTestProperties.APP_SWAGGER_URL_TEMPLATE);

	private static final String ADMIN_DOCUMENTS_RFI_DOCUMENTS_ENDPOINT = "/aaa-admin/services/aaa-policy-rs/v1/documents/rfi-documents/";
	private static final String DXP_CONTACT_INFO_UPDATE_ENDPOINT = "/api/v1/policies/%s/contact-info";
	private static final String DXP_ENDORSEMENTS_VALIDATE_ENDPOINT = "/api/v1/policies/%s/start-endorsement-info";
	private static final String DXP_VIN_VALIDATE_ENDPOINT = "/api/v1/policies/%s/vehicles/%s/vin-info";
	private static final String DXP_ENDORSEMENT_START_ENDPOINT = "/api/v1/policies/%s/endorsement";
	private static final String DXP_VIEW_VEHICLES_ENDPOINT = "/api/v1/policies/%s/vehicles";
	private static final String DXP_ADD_VEHICLE_ENDPOINT = "/api/v1/policies/%s/endorsement/vehicles";
	private static final String DXP_LOOKUP_NAME_ENDPOINT = "/api/v1/lookups/%s?productCd=%s&riskStateCd=%s";
	private static final String GET_RATING_ENDPOINT = "SELECT value FROM PROPERTYCONFIGURERENTITY \n"
			+ "WHERE VALUE LIKE '%aaa-rating-engine-app%'\n"
			+ "and propertyname = 'aaaCaHomeRulesClientProxyFactoryBean.address'";
	private static final String RATING_URL_TEMPLATE = DBService.get().getValue(GET_RATING_ENDPOINT).get();
	private static final String RATING_SERVICE_TYPE = "/determineDiscountPercentage";
	private static final String DXP_LOCK_UNLOCK_SERVICES = "/api/v1/policies/%s/lock";

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
		return runJsonRequestGetDxp(requestUrl, ValidateEndorsementResponse.class);
	}

	static AAAVehicleVinInfoRestResponseWrapper executeVinValidate(String policyNumber, String vin, String endorsementDate) {
		String requestUrl = urlBuilderDxp(String.format(DXP_VIN_VALIDATE_ENDPOINT, policyNumber, vin));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		return runJsonRequestGetDxp(requestUrl, AAAVehicleVinInfoRestResponseWrapper.class);
	}

	static ErrorResponseDto validateEndorsementResponseError(String policyNumber, String endorsementDate) {
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

	static HashMap <String, String> executeLookupValidate(String lookupName, String productCd, String riskStateCd, String effectiveDate) {
		String requestUrl = urlBuilderDxp(String.format(DXP_LOOKUP_NAME_ENDPOINT, lookupName, productCd, riskStateCd));
		if (effectiveDate != null) {
			requestUrl = requestUrl + "&effectiveDate=" + effectiveDate;
		}
		return runJsonRequestGetDxp(requestUrl, HashMap.class );
	}

	private void authentication() {
		WebDriver driver = BrowserController.get().driver();
		driver.switchTo().alert();
		//Selenium-WebDriver Java Code for entering Username & Password as below:
		driver.findElement(By.id("userID")).sendKeys("admin");
		driver.findElement(By.id("password")).sendKeys("admin");
		driver.switchTo().alert().accept();
		driver.switchTo().defaultContent();
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

	private static String runJsonRequestPostDxp(String url, RestBodyRequest request) {
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
			if (response.getStatus() != Response.Status.OK.getStatusCode() && response.getStatus() != 422) {
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

	static void executeDiscountPercentageRetrieveRequest(String lob, String usState, String coverageCd, String expectedValue) {
		DiscountRetrieveFullRequest request = new DiscountRetrieveFullRequest();
		request.runtimeContext = new DiscountPercentageRuntimeContext();
		request.runtimeContext.currentDate = 1517382000000L;
		request.runtimeContext.lob = lob;
		request.runtimeContext.usState = usState;
		request.discountCd = "MEMDIS";
		request.coverageCd = coverageCd;
		request.policyType = lob;
		String requestUrl = RATING_URL_TEMPLATE + RATING_SERVICE_TYPE;
		String discountPercentageValue = runJsonRequestPostDxp(requestUrl, request);
		assertThat(discountPercentageValue).isEqualTo(expectedValue);
	}
}
