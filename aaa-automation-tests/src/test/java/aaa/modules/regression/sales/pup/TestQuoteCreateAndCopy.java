package aaa.modules.regression.sales.pup;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test Create and Copy PUP SS Quote
 * @scenario
 * 1. Create Customer
 * 2. Create PUP SS Quote
 * 3. Verify Quote exist and had "Premium Calculated" status
 * 4. Select "Copy From Quote" action 
 * 5. Verify that status of the copied Quote is "Gathering Info"
 * 6. Select "Data Gather" action and click Go button.
 * 7. Fill all mandatory fields and issue quote.
 * 8. Verify new policy is in Active satus.
 */

public class TestQuoteCreateAndCopy extends PersonalUmbrellaBaseTest {
	
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP)
    public void testPolicyCreation() {
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
