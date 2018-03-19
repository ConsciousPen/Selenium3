package aaa.modules.regression.sales.pup;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author xiaolan ge
 * @name Test Initiate Umbrella Quote
 * @scenario
 * 1. Create Customer
 * 2. Initiated Umbrella Quote
 * 3. Verify quote status is 'Data Gathering' and policy number is present
 * @details
 */
public class TestQuoteInitiate extends PersonalUmbrellaBaseTest {

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testQuoteInitiate(@Optional("") String state) {
		mainApp().open();

		createCustomerIndividual();

		CustomerSummaryPage.buttonAddQuote.click();
		QuoteSummaryPage qsp = new QuoteSummaryPage();
		qsp.buttonAddNewQuote.verify.enabled();
		qsp.buttonAddNewQuote.click();
		qsp.broadLineOfBusiness.setValue(QuoteSummaryPage.PERSONAL_LINES);
		qsp.product.setValue(getPolicyType().getName());
		qsp.nextBtn.click();
		PrefillTab.buttonSaveAndExit.click();
		PolicySummaryPage.labelPolicyNumber.verify.present();

		log.info("Initiated Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
	}
}
