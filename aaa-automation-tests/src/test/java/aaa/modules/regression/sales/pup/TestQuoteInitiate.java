package aaa.modules.regression.sales.pup;

import static toolkit.verification.CustomAssertions.assertThat;
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
 * <b> Test Initiate Umbrella Quote </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Initiated Umbrella Quote
 * <p> 3. Verify quote status is 'Data Gathering' and policy number is present
 *
 */
public class TestQuoteInitiate extends PersonalUmbrellaBaseTest {

	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testQuoteInitiate(@Optional("") String state) {
		mainApp().open();

		createCustomerIndividual();

		CustomerSummaryPage.buttonAddQuote.click();
		QuoteSummaryPage qsp = new QuoteSummaryPage();
		assertThat(qsp.buttonAddNewQuote).isEnabled();
		qsp.initiateQuote(getPolicyType());
		PrefillTab.buttonSaveAndExit.click();
		assertThat(PolicySummaryPage.labelPolicyNumber).isPresent();

		log.info("Initiated Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
	}
}
