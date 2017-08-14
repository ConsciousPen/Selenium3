package aaa.modules.regression.sales.pup;

import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
/**
 * @author Lina Li
 * @name Test Create and Copy PUP SS Quote
 * @scenario
 * 1. Create Customer
 * 2. Create PUP SS Quote
 * 3. Verify Quote exist and had "Premium Calculated" status
 * 4. Select "Copy From Quote" action 
 * 5. Verify that status of the copied Quote is "Gathering Info"
 */

public class TestCreateAndCopyQuote extends PersonalUmbrellaBaseTest {

	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testCreateAndCopyQuote() {
		mainApp().open();
		createCustomerIndividual();

		createQuote();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Initial Quote policyNumber " + policyNumber);

		policy.copyQuote().perform(getPolicyTD("CopyFromQuote", "TestData"));
		String policyNumberCopied = PolicySummaryPage.labelPolicyNumber.getValue();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
		log.info("Copied Quote policyNumber " + policyNumberCopied);
	}

}
