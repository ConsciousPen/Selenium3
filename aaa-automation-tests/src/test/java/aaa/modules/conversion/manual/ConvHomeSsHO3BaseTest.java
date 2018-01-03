package aaa.modules.conversion.manual;

import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;

public class ConvHomeSsHO3BaseTest extends HomeSSHO3BaseTest implements ManualConversionHelper {

    @Override
    protected TestData getManualConversionInitiationTd() {
        TestData td = super.getManualConversionInitiationTd();
        return modifyInitiationTdForHome(getPolicyType(), modifyInitiationTd(td, tdCustomerIndividual, getState()));
    }

    @Override
    protected TestData getConversionPolicyDefaultTD(){
        TestData td = super.getConversionPolicyDefaultTD();
        return modifyConversionPolicyDefaultTD(td, getPolicyType());
    }
}
