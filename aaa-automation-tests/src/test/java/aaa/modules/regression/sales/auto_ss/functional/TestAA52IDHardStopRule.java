package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.ID)
public class TestAA52IDHardStopRule extends AutoSSBaseTest {

    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    private ErrorTab errorTab = new ErrorTab();

    /**
     * @author Sreekanth Kopparapu
     * @name ID Auto New Hard Stop Rule when AA52ID doc is not Signed after UM/UIM Coverage is opted for New Business
     * @scenario 1. Create Customer
     * 2. Initiate Auto SS ID Quote
     * 3. Navigate to P&C Page and ensure the UM/UIM coverages are selected
     * 4. Navigate to Documents&Bind tab to validate the UM and UIM Disclosure Statement and Rejection Of Coverage field
     * 5. default value for UM and UIM Disclosure Statement and Rejection Of Coverage = Not Signed
     * 7. Override Rule - with message " "A signed Uninsured motorist coverage selection form must be received prior to issuing this transaction" is displayed
     * 8. Override the rule and is able to Bind the policy
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-17818")
    public void pas17818_testDocHardStopAA52IDBehaviorNB(@Optional("ID") String state) {

        // Initiate Policy, calculate premium with UM/UIM coverages, Documents and Bind tab - UM and UIM coverage field
        createQuoteAndFillUpTo(DocumentsAndBindTab.class);

        documentsAndBindTab.getRequiredToBindAssetList()
                .getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_UNDERINSURED_DISCLOSURE_STATEMENT_AND_REJECTION_OF_COVERAGE).setValue("Not Signed");
        overrideErrorsAndSubmitTab(ErrorEnum.Errors.ERROR_AAA_200306);
        new PurchaseTab().fillTab(getPolicyTD()).submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).isPresent();
    }

    /**
     * @author Sreekanth Kopparapu
     * @name ID Auto New Hard Stop Rule when AA52ID doc is not Signed after UM/UIM Coverage is opted for NB, Endorsement
     * @scenario 1. Create Customer
     * 2. Initiate Auto SS ID Quote
     * 3. Navigate to P&C Page and ensure the UM/UIM coverages are Rejected
     * 4. Navigate to Documents&Bind tab to validate the UM and UIM Disclosure Statement and Rejection Of Coverage field
     * 5. default value for UM and UIM Disclosure Statement and Rejection Of Coverage = Not Signed
     * 7. Override Rule - with message " "A signed Uninsured motorist coverage Rejection form must be received prior to issuing this transaction" is displyed
     * 8. Override the rule and Bind the policy
     * 9. Perform an endorsement
     * 10. P&C Page - opt for UM/UIM coverages and Calculate the Premium
     * 11. Documents & Bind page - "UM and UIM Disclosure Statement and Rejection Of Coverage" field with "Not Signed" option selected
     * 12. When trying to bind the policy - Hard Stop Rule is displayed "A signed Uninsured motorist coverage selection form must be received prior to issuing this transaction" "
     * 13. Override the rule and is able to Bind the policy
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-17818")
    public void pas17818_testDocHardStopAA52IDBehaviorEndorsement(@Optional("ID") String state) {
        checkRenewalAndEndorsement(false);
    }

    /**
     * @author Sreekanth Kopparapu
     * @name ID Auto New Hard Stop Rule when AA52ID doc is not Signed after UM/UIM Coverage is opted for Renewal assuming @NB UM/UIM are not opted
     * @scenario 1. Create Customer
     * 2. Initiate Auto SS ID Quote
     * 3. Navigate to P&C Page and ensure the UM/UIM coverages are Rejected
     * 4. Navigate to Documents&Bind tab to validate the UM and UIM Disclosure Statement and Rejection Of Coverage field
     * 5. default value for UM and UIM Disclosure Statement and Rejection Of Coverage = Not Signed
     * 7. Override Rule - with message " "A signed Uninsured motorist coverage Rejection form must be received prior to issuing this transaction" is displyed
     * 8. Override the rule and is able to Bind the policy
     * 9. Perform an Renewal in DataGather mode
     * 10. P&C Page - opt for UM/UIM coverages and Calculate the Premium
     * 11. Documents & Bind page - UM and UIM Coverage Rejection field with Not Signed option selected
     * 12. Save&Exit the policy - Hard Stop Rule is displayed "A signed Uninsured motorist coverage selection form must be received prior to issuing this transaction" "
     * 13. Override the rule and is able to Save & Exit the policy
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-17818")
    public void pas17818_testDocHardStopAA52IDBehaviorRenewal(@Optional("ID") String state) {
        checkRenewalAndEndorsement(true);
    }

    /**
     * @author Sreekanth Kopparapu
     * @name ID Auto New Hard Stop Rule when AA52ID doc is not Signed after UM/UIM Coverage is opted for Renewal assuming @NB UM/UIM are not opted
     * @scenario 1. Create Customer
     * 2. Initiate Auto SS ID Conversion - Initiate Manual Conversion and fill all mandatory fileds in P&CPage
     * 3. Navigate to P&C Page and ensure the UM/UIM coverages are selected
     * 4. Navigate to Documents&Bind tab to validate the UM and UIM Disclosure Statement and Rejection Of Coverage field
     * 5. default value for UM and UIM Disclosure Statement and Rejection Of Coverage = Not Signed
     * 7. Override Rule - with message " "A signed Uninsured motorist coverage Rejection form must be received prior to issuing this transaction" is displyed
     * 8. Override the rule and is able to Propose the policy
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-17818")
    public void pas17818_testDocHardStopAA52IDBehaviorConversion(@Optional("ID") String state) {

        //Initiate manual conversion policy
        mainApp().open();
        createCustomerIndividual();
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        policy.getDefaultView().fillUpTo(getConversionPolicyDefaultTD(), DocumentsAndBindTab.class, true);

        documentsAndBindTab.getRequiredToBindAssetList()
                .getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_UNDERINSURED_DISCLOSURE_STATEMENT_AND_REJECTION_OF_COVERAGE).setValue("Not Signed");
        overrideErrorsAndSubmitTab(ErrorEnum.Errors.ERROR_AAA_200306);
        PolicySummaryPage.buttonBackFromRenewals.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).isPresent();

    }

    private void checkRenewalAndEndorsement(Boolean isRenewal){
        //Index 0 is nothing but the UM/UIM coverages are rejected [No Coverage]
        TestData td = getPolicyTD()
                .adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY.getLabel()), "index=0")
                .adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel()), "index=0");

        // Initiate Policy, @ Endorsement - calculate premium with UM/UIM coverages, Documents and Bind tab - UM and UIM coverage field
		openAppAndCreatePolicy(td);
        assertThat(PolicySummaryPage.labelPolicyStatus).isPresent();

        if(isRenewal){
            policy.renew().perform();
        }
        else {
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        }

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY).setValueByIndex(1);
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY).setValueByIndex(1);
        premiumAndCoveragesTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.getRequiredToBindAssetList()
                .getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_UNDERINSURED_DISCLOSURE_STATEMENT_AND_REJECTION_OF_COVERAGE).setValue("Not Signed");
        overrideErrorsAndSubmitTab(ErrorEnum.Errors.ERROR_AAA_200306);
        assertThat(PolicySummaryPage.labelPolicyStatus).isPresent();
    }

    private void overrideErrorsAndSubmitTab(ErrorEnum.Errors error) {

        documentsAndBindTab.submitTab();
        errorTab.overrideErrors(error);
        errorTab.override();
        documentsAndBindTab.submitTab();

    }
}