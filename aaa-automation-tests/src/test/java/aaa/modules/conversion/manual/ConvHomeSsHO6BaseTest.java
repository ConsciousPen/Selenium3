package aaa.modules.conversion.manual;

import aaa.modules.policy.HomeSSHO6BaseTest;
import toolkit.datax.TestData;

public class ConvHomeSsHO6BaseTest extends HomeSSHO6BaseTest implements ManualConversionHelper {

    @Override
    protected TestData prepareManualConversionTd() {
        TestData td = super.prepareManualConversionTd();

        return modifyForHome(getPolicyType(), modifyTd(td, tdCustomerIndividual, getState()));

    }

}
