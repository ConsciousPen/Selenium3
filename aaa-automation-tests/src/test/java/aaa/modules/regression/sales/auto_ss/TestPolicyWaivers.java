package aaa.modules.regression.sales.auto_ss;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestPolicyWaivers extends AutoSSBaseTest {
    protected TestData tdPolicy;
    private String origPolicyNum;
    private String policyNum;

    /**
     * @author Rob Boles
     * @name Test Same Day Waiver in Endorsement
     * @scenario 1. Add minor violation - Disregard Traffic Device or Sign - set the occurrence date to policy effective date
     * 2. Add second minor violation - Failure to Yield Right-of-Way - set the occurrence date to policy effective date
     * 3. Create 2nd driver and repeat steps 1 and 2
     * 4. Calculate premium - validate 2nd same day minor violation is waved for both drivers
     * 5. Bind policy
     * 6. Initiate mid-term endorsement
     * 7. Navigate to Driver tab and note the details of the violations (1st minor should have 3 points and 2nd minor should be waived, both same day)
     * 8. Navigate to PnC tab and calculate premium
     * 9. Navigate back to Driver tab
     * 10. Validate: The points will be retained for the 1st minor and the 2nd minor will remain waived with 0 points and no premium change noted.
     * @details
     */
    @Parameters({"state"})
    @StateList(statesExcept = {Constants.States.CA})
    @Test(groups = {Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testPolicyWaiversAutoSS(@Optional("") String state) {
        TestData td_quote = getPolicyTD();

        mainApp().open();
        createCustomerIndividual();
        //TODO: adjust td_quote to add minor violations
        origPolicyNum = createPolicy();
        policyNum = createPolicyAndVerifyWaiverStatus(td_quote);
        verifyWaiverStatusOnEndorsement(policyNum);
        //assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        //Assertions.assertThat(PolicySummaryPage.getExpirationDate()).isEqualTo(PolicySummaryPage.getEffectiveDate().plusYears(1));
    }

    private String createPolicyAndVerifyWaiverStatus(TestData td_quote) {
        SearchPage.openPolicy(origPolicyNum);


        policy.policyCopy().perform(td_quote);
        policy.dataGather().start();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        new GeneralTab().fillTab(td_quote);
        new GeneralTab().submitTab();

        new DriverTab().fillTab(td_quote);
        new DriverTab().submitTab();

        new RatingDetailReportsTab().fillTab(td_quote).submitTab();
        //TODO: Fix return type to return something of value or add assertions to check driver tab
        return PolicySummaryPage.labelPolicyNumber.getValue();
    }

    private void verifyWaiverStatusOnEndorsement(String policyNum) {
        SearchPage.openPolicy(policyNum);
        policy.endorse();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        //TODO: On driver tab in endo - scrape table values for 2 minor violations
        //new DriverTab().fillTab(td).submitTab();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        new PremiumAndCoveragesTab().calculatePremium();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        //TODO: on driver tab validate the waiver did not drop

    }
}

