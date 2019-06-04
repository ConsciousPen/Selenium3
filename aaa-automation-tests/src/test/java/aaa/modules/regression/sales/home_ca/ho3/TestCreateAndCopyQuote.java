package aaa.modules.regression.sales.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Dmitry Kozakevich
 * <b> Test Create and Copy HomeCA Quote </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Create HomeCA Quote
 * <p> 3. Verify Quote exist and had "Premium Calculated" status
 * <p> 4. Copy Quote
 * <p> 5. Verify that status of the copied Quote is "Gathering Info"
 */
public class TestCreateAndCopyQuote extends HomeCaHO3BaseTest {

    @Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void testCreateAndCopyQuote(@Optional("CA") String state) {
        mainApp().open();
        createCustomerIndividual();

        policy.createQuote(getPolicyTD("DataGather", "TestData"));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("Initial Quote policyNumber " + policyNumber);

        policy.copyQuote().perform(getPolicyTD("CopyFromQuote", "TestData"));
        String policyNumberCopied = PolicySummaryPage.labelPolicyNumber.getValue();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
        log.info("Copied Quote policyNumber " + policyNumberCopied);
    }
}
