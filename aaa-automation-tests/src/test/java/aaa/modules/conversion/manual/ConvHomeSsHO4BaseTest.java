package aaa.modules.conversion.manual;

import aaa.modules.policy.HomeSSHO4BaseTest;
import toolkit.datax.TestData;

public class ConvHomeSsHO4BaseTest extends HomeSSHO4BaseTest implements ManualConversionHelper {

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
