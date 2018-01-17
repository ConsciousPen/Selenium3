package aaa.modules.conversion.manual;

import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
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
     * Verifies an rule has fired, overrides it, and binds the policy and confirms it is active.
     * @param errors the specific error(s) from ErrorEnum.Errors class to be verified
     * @param td the test data to be used to finish binding the policy
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
    protected void verifyErrorsAndOverride(ErrorEnum.Errors... errors) {
        errorTab.verify.errorsPresent(errors);
        errorTab.overrideAllErrors();
        errorTab.override();
    }
}
