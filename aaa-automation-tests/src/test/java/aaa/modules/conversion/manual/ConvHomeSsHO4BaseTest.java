package aaa.modules.conversion.manual;

import aaa.modules.policy.HomeSSHO4BaseTest;
import toolkit.datax.TestData;

public class ConvHomeSsHO4BaseTest extends HomeSSHO4BaseTest implements ManualConversionHelper {

    @Override
    protected TestData prepareManualConversionTd() {
        TestData td = super.prepareManualConversionTd();

        return modifyForHome(getPolicyType(), modifyTd(td, tdCustomerIndividual, getState()));

    }

}
