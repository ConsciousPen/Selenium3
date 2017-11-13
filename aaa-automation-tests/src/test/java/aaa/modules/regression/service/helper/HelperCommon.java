package aaa.modules.regression.service.helper;

import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.config.CustomTestProperties;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.swaggerui.SwaggerUiTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.base.app.Application;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.xerces.impl.dv.util.Base64;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.waiters.Waiters;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class HelperCommon extends BaseTest {
	private static String swaggerUiUrl = PropertyProvider.getProperty(CustomTestProperties.APP_HOST) + PropertyProvider.getProperty(CustomTestProperties.DXP_PORT) + PropertyProvider.getProperty(CustomTestProperties.APP_SWAGGER_URLTEMPLATE);


	private static String urlBuilder(String endpointUrlPart) {
        return "http://" + PropertyProvider.getProperty(CustomTestProperties.APP_HOST) + PropertyProvider.getProperty(CustomTestProperties.DXP_PORT) + endpointUrlPart;
	}

	private void emailUpdateSwaggerUi(String policyNumber, String emailAddress) {
		By customerV1EndorsementsPost = SwaggerUiTab.customerV1EndorsementsPost.getLocator();
		adminApp().open();
		Application.open(swaggerUiUrl);
		SwaggerUiTab swaggerUiTab = new SwaggerUiTab();

		Waiters.SLEEP(2000).go();
		SwaggerUiTab.customerV1Endorsements.click();
		SwaggerUiTab.customerV1EndorsementsPost.click();

		SwaggerUiTab.policyNumber.setValue(policyNumber);
		SwaggerUiTab.updateEmailRequest.setValue("{\"email\": \"" + emailAddress + "\"}");
		swaggerUiTab.clickButtonTryIt(customerV1EndorsementsPost);
		//TODO get rid of authentication popup, which cant be handled by Chrome of Firefox
		CustomAssert.assertEquals(swaggerUiTab.getResponseCodeValue(customerV1EndorsementsPost), "200");
		log.info(swaggerUiTab.getResponseBodyValue(customerV1EndorsementsPost));
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


	public void runJsonRequest(String url, RestBodyRequest request) {
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
			response.readEntity(String.class);
			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				//handle error
				throw new IstfException(response.readEntity(String.class));
			}
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	void executeRequest(String policyNumber, String emailAddressChanged) {
		if (Boolean.parseBoolean(PropertyProvider.getProperty(CustomTestProperties.USE_SWAGGER))) {
			emailUpdateSwaggerUi(policyNumber, emailAddressChanged);
		} else {
			UpdateEmailRequest request = new UpdateEmailRequest();
			request.email = emailAddressChanged;
			String requestUrl = urlBuilder(PropertyProvider.getProperty(CustomTestProperties.DXP_EMAIL_UPDATE_ENDPOINT)) + policyNumber;
			runJsonRequest(requestUrl, request);
		}
	}




	void emailUpdateTransactionHistoryCheck(String policyNumber) {
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.buttonTransactionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).verify.present();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").verify.value("Email Updated - Exte...");
		Tab.buttonCancel.click();
	}
}
