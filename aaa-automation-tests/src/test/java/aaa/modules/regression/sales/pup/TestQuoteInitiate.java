package aaa.modules.regression.sales.pup;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
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
	@TestInfo(component = ComponentConstant.Sales.PUP )
    public void testQuoteInitiate(@Optional("") String state) {
        mainApp().open();

        createCustomerIndividual();

        CustomerSummaryPage.buttonAddQuote.click();
        QuoteSummaryPage.comboBoxProduct.setValue(PolicyType.PUP.getName());
        assertThat(QuoteSummaryPage.buttonAddNewQuote).isEnabled();
        QuoteSummaryPage.buttonAddNewQuote.click();
        assertThat(new PrefillTab().getAssetList()).isPresent();
        PrefillTab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.labelPolicyNumber).isPresent();

        log.info("Initiated Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
    }
}
