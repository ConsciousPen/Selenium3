package aaa.modules.regression.sales.home_ca.ho3;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * @author Dmitry Kozakevich
 * @name Test Create and Copy HomeCA Quote
 * @scenario
 * 1. Create Customer
 * 2. Create HomeCA Quote
 * 3. Verify Quote exist and had "Premium Calculated" status
 * 4. Copy Quote
 * 5. Verify that status of the copied Quote is "Gathering Info"
 */
public class TestCreateAndCopyQuote extends HomeCaHO3BaseTest {

    @Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void testCreateAndCopyQuote(@Optional("CA") String state) {
        mainApp().open();
        createCustomerIndividual();

        policy.createQuote(getPolicyTD("DataGather", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("Initial Quote policyNumber " + policyNumber);

        policy.copyQuote().perform(getPolicyTD("CopyFromQuote", "TestData"));
        String policyNumberCopied = PolicySummaryPage.labelPolicyNumber.getValue();
        assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.DATA_GATHERING);
        log.info("Copied Quote policyNumber " + policyNumberCopied);
    }
}
