package aaa.modules.conversion.manual;

import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;

/**
 * @author Josh Carpenter
 * This class houses methods specific to PUP conversion policies and their corresponding test data requirements.
 */
public class ConvPUPBaseTest extends PersonalUmbrellaBaseTest implements ManualConversionHelper {

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
}
