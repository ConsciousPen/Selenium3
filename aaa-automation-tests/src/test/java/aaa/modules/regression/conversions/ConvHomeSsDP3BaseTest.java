package aaa.modules.regression.conversions;

import aaa.modules.policy.HomeSSDP3BaseTest;
import toolkit.datax.TestData;

public class ConvHomeSsDP3BaseTest extends HomeSSDP3BaseTest implements ManualConversionHelper {

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
