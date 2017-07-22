package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.HomeSSPolicyActions;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Olga Reva
 * @name Test Create and copy quote
 * @scenario
 * 1. Find customer or create new if customer does not exist.
 * 2. Initiate new HSS quote creation.
 * 3. Fill all mandatory fields on all tabs, order report, calculate premium.
 * 4. Save & Exit quote. 
 * 5. Verify quote status is Premium Calculated. 
 * 6. Perform Copy from Quote action.
 * 7. Verify status of copied quote is Gathering Info.
 * 8. Perform Data Gathering action for copied quote.
 * 9. Fill all unfilled fields in copied quote, calculate premium, bind and purchase.
 * 10. Verify that policy is created and in Active status.
 * @details
 */
public class TestQuoteCreateAndCopy extends HomeSSBaseTest {
	
	@Test
    @TestInfo(component = "Quote.HomeSS")
    public void testPolicyCreation() {
        mainApp().open();

        createCustomerIndividual();
        createQuote();
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        log.info("TEST: Created Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        new HomeSSPolicyActions.CopyQuote().perform(tdPolicy.getTestData("CopyFromQuote", "TestData"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
        log.info("TEST: Copied Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        new HomeSSPolicyActions.DataGather().start();
        
        policy.getDefaultView().fill(getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData"));
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        log.info("TEST: Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
	}

}
