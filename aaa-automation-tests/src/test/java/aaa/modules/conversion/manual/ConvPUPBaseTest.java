package aaa.modules.conversion.manual;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.pup.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;

/**
 * @author Josh Carpenter
 * This class houses methods specific to PUP conversion policies and their corresponding test data requirements.
 */
public class ConvPUPBaseTest extends PersonalUmbrellaBaseTest implements ManualConversionHelper {

    private ErrorTab errorTab = policy.getDefaultView().getTab(ErrorTab.class);
    private PurchaseTab purchaseTab = policy.getDefaultView().getTab(PurchaseTab.class);
    private BindTab bindTab = policy.getDefaultView().getTab(BindTab.class);
    private ClaimsTab claimsTab = policy.getDefaultView().getTab(ClaimsTab.class);

    @Override
    protected TestData getManualConversionInitiationTd() {
        TestData td = super.getManualConversionInitiationTd();
        return modifyInitiationTd(td, tdCustomerIndividual, getState())
                .adjust(TestData.makeKeyPath(InitiateRenewalEntryActionTab.class.getSimpleName(),
                        CustomerMetaData.InitiateRenewalEntryActionTab.PRODUCT_NAME.getLabel()), getPolicyType().getName());
    }

    protected TestData getManualConversionInitiationTd35() {
        TestData td = super.getManualConversionInitiationTd();
        return modifyInitiationTd(td, tdCustomerIndividual, getState())
                .adjust(TestData.makeKeyPath(InitiateRenewalEntryActionTab.class.getSimpleName(),
                        CustomerMetaData.InitiateRenewalEntryActionTab.PRODUCT_NAME.getLabel()), getPolicyType().getName())
                .adjust(TestData.makeKeyPath(InitiateRenewalEntryActionTab.class.getSimpleName(),
                        CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_EFFECTIVE_DATE.getLabel()), "$<today+30d:MM/dd/yyyy>");
    }

    @Override
    protected TestData getConversionPolicyDefaultTD(){
        TestData td = super.getConversionPolicyDefaultTD();
        return modifyConversionPolicyDefaultTD(td, getPolicyType());
    }

    protected TestData getPupConversionTdNoPolicyCreation() {
        return modifyConversionPolicyDefaultTD(getPolicyTD(), getPolicyType());
    }

    /**
     * Fills a PUP conversion policy and overrides the accompanying error on the Claims Tab directly
     * @param td Test data used to fill the PUP policy
     * @param errors the specific error(s) from ErrorEnum.Errors class to be verified
     */
    protected void fillPupOverrideRuleOnClaimsTab(TestData td, ErrorEnum.Errors... errors) {
        policy.getDefaultView().fillUpTo(td, ClaimsTab.class, true);
        claimsTab.submitTab();
        claimsTab.overrideRules();
        verifyErrorsAndOverride(errors);
        claimsTab.submitTab();
        NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
        policy.getDefaultView().fillFromTo(td, PremiumAndCoveragesQuoteTab.class, BindTab.class, true);
        bindTab.submitTab();
    }

    /**
     * @param errors the specific error(s) from ErrorEnum.Errors class to be verified
     * @param td the test data to be used to finish binding the policy
     * @return boolean value representing error verification results
     */
    protected void verifyErrorsOverrideAndBind(TestData td, ErrorEnum.Errors... errors) {
        verifyErrorsAndOverride(errors);
        bindTab.submitTab();
        if (!PolicySummaryPage.labelPolicyNumber.isPresent()) {
            purchaseTab.fillTab(td);
            purchaseTab.submitTab();
        }
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    /**
     * This is will ONLY verify and override the errors (used with 'Override Errors' button on Claims tab)
     * @param errors the specific error(s) from ErrorEnum.Errors class to be verified
     */
    private void verifyErrorsAndOverride(ErrorEnum.Errors... errors) {
        errorTab.verify.errorsPresent(errors);
        errorTab.overrideAllErrors();
        errorTab.override();
    }
}
