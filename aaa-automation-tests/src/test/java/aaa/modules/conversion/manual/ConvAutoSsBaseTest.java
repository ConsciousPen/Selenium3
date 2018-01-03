package aaa.modules.conversion.manual;

import aaa.main.modules.customer.CustomerActions;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;

public class ConvAutoSsBaseTest extends AutoSSBaseTest implements ManualConversionHelper {

    @Override
    protected TestData getManualConversionInitiationTd() {
        TestData td = super.getManualConversionInitiationTd();
        return modifyInitiationTd(td, tdCustomerIndividual, getState());
    }

    @Override
    protected TestData getConversionPolicyDefaultTD() {
        return getStateTestData(tdCustomerIndividual, CustomerActions.InitiateRenewalEntry.class.getSimpleName(), "TestData");
    }

}
