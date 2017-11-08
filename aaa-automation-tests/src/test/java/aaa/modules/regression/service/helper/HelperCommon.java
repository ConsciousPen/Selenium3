package aaa.modules.regression.service.helper;

import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.config.CustomTestProperties;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.swaggerui.SwaggerUiTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.base.app.Application;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import toolkit.config.PropertyProvider;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.waiters.Waiters;

public class HelperCommon extends BaseTest {
	private static String swaggerUiUrl = PropertyProvider.getProperty("app.host") + PropertyProvider.getProperty(CustomTestProperties.APP_SWAGGER_URLTEMPLATE);

	public void emailUpdateSwaggerUi(String policyNumber, String emailAddress) {
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

	public void emailUpdateTransactionHistoryCheck(String policyNumber) {
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.buttonTransactionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).verify.present();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").verify.value("Email Updated - Exte...");
		Tab.buttonCancel.click();
	}
}
