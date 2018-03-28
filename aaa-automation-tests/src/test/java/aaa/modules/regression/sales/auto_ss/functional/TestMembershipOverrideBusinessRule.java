package aaa.modules.regression.sales.auto_ss.functional;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;

import aaa.admin.modules.product.productfactory.policy.defaulttabs.RulesTab;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import org.openqa.selenium.By;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMembershipOverrideBusinessRule  extends AutoSSBaseTest {
    /**
     * @author Robert Boles
     * @name Test Membership Override Pay Plan - PAS-10845 - Scenario 1 - Membership = Membership Override
     * @scenario
     * Precondition: Agent is expected to have the Membership override privilege.
     * 1. Create Customer.
     * 2. Create Auto SS Policy with Membership "Membership Override"
     * 3. Select Override Prefilled Current Carrier and populate information.
     * 4. Navigate to Premium & Coverages tab and select Eleven pay - Standard
     * 8. Bind Quote ---> quote will bind
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10845")

    public void pas10845_Validate_Membership_Override_BusinessRule_MSOverriden() {

        TestData testData = getPolicyTD();
        TestData tdSpecific = getTestSpecificTD("AAAProductOwned_Override").resolveLinks();
        TestData currentCarrierSection = getTestSpecificTD("CurrentCarrierInformation_BR").resolveLinks();
        TestData pNcTab = getTestSpecificTD("PremiumAndCoveragesTab").resolveLinks();

        testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
                tdSpecific);

        testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()),
                currentCarrierSection);

        testData.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
                AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Eleven Pay - Standard");

        mainApp().open();
        createCustomerIndividual();
        createPolicy(testData);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }


    /**
     * @author Robert Boles
     * @name Test Membership Override Pay Plan - PAS-10845 - Scenario 2 - Membership Pending
     * @scenario
     * Precondition: Agent is expected to have the Membership override privilege.
     * 1. Create Customer.
     * 2. Create Auto SS Policy with Membership "Membership Pending"
     * 3. Select Override Prefilled Current Carrier and populate information.
     * 4. Navigate to Premium & Coverages tab and select Eleven pay - Standard
     * 8. Bind Quote ---> quote will bind
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10845")
    public void pas10845_Validate_Membership_Override_BusinessRule_MSPending() {

        TestData testData = getPolicyTD();
        TestData tdSpecificProductOwned = getTestSpecificTD("AAAProductOwned_Pend").resolveLinks();
        TestData tdCurrentCarrierSection = getTestSpecificTD("CurrentCarrierInformation_BR").resolveLinks();
        TestData tdPremium = getTestSpecificTD("PremiumAndCoveragesTab").resolveLinks();

        testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()), tdSpecificProductOwned)
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()),tdCurrentCarrierSection)
                .adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
                AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Eleven Pay - Standard");

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();

        policy.getDefaultView().fillUpTo(testData, DocumentsAndBindTab.class, true);
        new DocumentsAndBindTab().submitTab();
        verifyErrorAndExit();
    }

    /**
     * @author Robert Boles
     * @name Test Membership Override Pay Plan - PAS-10845 - Scenario 3 - Membership = Yes
     * @scenario
     * Precondition: Agent is expected to have the Membership override privilege.
     * 1. Create Customer.
     * 2. Create Auto SS Policy with Membership "Yes" - add membership information for active membership
     * 3. Select Override Prefilled Current Carrier and populate information.
     * 4. Navigate to Premium & Coverages tab and select Eleven pay - Standard
     * 8. Bind Quote ---> quote will bind
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10845")
    public void pas10845_Validate_Membership_Override_BusinessRule_MSYes() {

        TestData testData = getPolicyTD();
        TestData tdSpecificProductOwned = getTestSpecificTD("AAAProductOwned_Yes").resolveLinks();
        TestData tdCurrentCarrierSection = getTestSpecificTD("CurrentCarrierInformation_BR").resolveLinks();
        TestData tdPremium = getTestSpecificTD("PremiumAndCoveragesTab").resolveLinks();

        testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()), tdSpecificProductOwned)
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                        AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()),
                        tdCurrentCarrierSection)
                .adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
                        AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Eleven Pay - Standard");

        mainApp().open();
        createCustomerIndividual();
        createPolicy(testData);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    /**
     * @author Robert Boles
     * @name Test Membership Override Pay Plan - PAS-10845  -Scenario 4 - Membership = No
     * @scenario
     * Precondition: Agent is expected to have the Membership override privilege.
     * 1. Create Customer.
     * 2. Create Auto SS Policy with Membership "No"
     * 3. Select Override Prefilled Current Carrier and populate information.
     * 4. Navigate to Premium & Coverages tab and select Eleven pay - Standard
     * 8. Bind Quote ---> Rule should fire -
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10845")
    public void pas10845_Validate_Membership_Override_BusinessRule_MSNo() {
        TestData testData = getPolicyTD();
        TestData tdSpecificProductOwned = getTestSpecificTD("AAAProductOwned_No").resolveLinks();
        TestData tdCurrentCarrierSection = getTestSpecificTD("CurrentCarrierInformation_BR").resolveLinks();
        TestData tdPremium = getTestSpecificTD("PremiumAndCoveragesTab").resolveLinks();

        testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()), tdSpecificProductOwned)
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                        AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()),
                        tdCurrentCarrierSection)
                .adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
                        AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Eleven Pay - Standard");

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();

        policy.getDefaultView().fillUpTo(testData, DocumentsAndBindTab.class, true);
        new DocumentsAndBindTab().submitTab();
        verifyErrorAndExit();
    }

    private void verifyErrorAndExit() {
        ErrorTab errorTab = new ErrorTab();
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_200127);
        mainApp().close();
    }
}