package aaa.modules.regression.service.helper;

import static aaa.admin.modules.IAdmin.log;
import static org.assertj.core.api.Assertions.assertThat;
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
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.waiters.Waiters;

public class HelperCommon {

	private static final String RATING_URL_TEMPLATE = "http://"+ PropertyProvider.getProperty(CustomTestProperties.APP_HOST)+":9089/aaa-rating-engine-app/REST/ws/home-ca";
	private static final String RATING_SERVICE_TYPE = "/determineDiscountPercentage";

	private static String swaggerUiUrl = PropertyProvider.getProperty(CustomTestProperties.APP_HOST) + PropertyProvider.getProperty(CustomTestProperties.DXP_PORT) + PropertyProvider
			.getProperty(CustomTestProperties.APP_SWAGGER_URL_TEMPLATE);

	public static <T> RfiDocumentResponse[] executeRequestRfi(String policyNumber, String date) {
		String requestUrl = urlBuilderAdmin(PropertyProvider.getProperty(CustomTestProperties.ADMIN_DOCUMENTS_RFI_DOCUMENTS_ENDPOINT)) + policyNumber + "/" + date;
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
			String requestUrl = urlBuilderDxp(PropertyProvider.getProperty(CustomTestProperties.DXP_CONTACT_INFO_UPDATE_ENDPOINT)) + policyNumber;
			runJsonRequestPostDxp(requestUrl, request);
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



	static ValidateEndorsementResponse executeEndorsementsValidate(String policyNumber, String endorsementDate) {
		EndorsementsValidateRequest request = new EndorsementsValidateRequest();
		request.policyNumber = policyNumber;
		request.endorsementDate = endorsementDate;
		String requestUrl = urlBuilderDxp(PropertyProvider.getProperty(CustomTestProperties.DXP_ENDORSEMENTS_VALIDATE_ENDPOINT));
		ValidateEndorsementResponse validateEndorsementResponse = runJsonRequestPostDxp(requestUrl, request, ValidateEndorsementResponse.class);
		return validateEndorsementResponse;
	}

	private static String urlBuilderDxp(String endpointUrlPart) {
		return PropertyProvider.getProperty(CustomTestProperties.DXP_PROTOCOL) + PropertyProvider.getProperty(CustomTestProperties.APP_HOST).replace(PropertyProvider.getProperty(CustomTestProperties.DOMAIN_NAME), "") + PropertyProvider.getProperty(CustomTestProperties.DXP_PORT) + endpointUrlPart;
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

	private static String urlBuilderAdmin(String endpointUrlPart) {
		return "http://" + PropertyProvider.getProperty(CustomTestProperties.APP_HOST) + PropertyProvider.getProperty(CustomTestProperties.ADMIN_PORT) + endpointUrlPart;
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
			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
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
