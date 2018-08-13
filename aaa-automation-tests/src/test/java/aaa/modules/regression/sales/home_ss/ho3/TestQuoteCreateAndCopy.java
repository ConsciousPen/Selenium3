package aaa.modules.regression.sales.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
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
public class TestQuoteCreateAndCopy extends HomeSSHO3BaseTest {
	
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
    public void testPolicyCreation(@Optional("") String state) {
        mainApp().open();

        createCustomerIndividual();
        createQuote();
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        log.info("TEST: Created Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        policy.copyQuote().perform(getPolicyTD("CopyFromQuote", "TestData"));
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
        log.info("TEST: Copied Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        policy.dataGather().start();
        policy.getDefaultView().fill(getTestSpecificTD("TestData"));
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        log.info("TEST: Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
	}

}
