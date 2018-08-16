package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name Propose Renewal Version after it was created with batch jobs
 * @scenario
 * 1. Create Customer
 * 2. Create Auto policy
 * 3. Move time Between R-45 and R-35 (specific to the defect)
 * 4. And run Renewal Jobs
 * 5. Create Manual Renewal Version and Propose it
 * 6. Check That Renewal Version Status is Proposed
 * @details
 */
@StateList(states = { Constants.States.MT })
public class TestMTRenewalVersionProposal extends AutoSSBaseTest {

    @Parameters({"state"})
	@Test(groups = { Groups.FUNCTIONAL, Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void pas17788_MTManualRenewalVersionProposal(@Optional("MT") String state) {

        // Create Policy
        openAppAndCreatePolicy();
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        // Move time Between R-45 and R-35 (specific to the defect) And run Renewal Jobs
        moveTimeAndRunRenewJobs(PolicySummaryPage.getExpirationDate().minusDays(39));
        // Search for the Policy
        searchForPolicy(policyNumber);
        // Initiate Renewal Version
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        // Calculate Premium and Navigate to propose
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        new PremiumAndCoveragesTab().calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
        // Check that Renewal Version is in Status Proposed
        PolicySummaryPage.buttonRenewals.click();
        new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
    }
}
