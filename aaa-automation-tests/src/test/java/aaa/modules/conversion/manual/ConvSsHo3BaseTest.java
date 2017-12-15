package aaa.modules.conversion.manual;

import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;

public class ConvSsHo3BaseTest extends HomeSSHO3BaseTest implements ManualConversionHelper{

    @Override
    protected TestData prepareManualConversionTd() {
        TestData td = super.prepareManualConversionTd();

        return modifyForHome(getPolicyType(), modifyTd(td, tdCustomerIndividual, getState()));

    }
}
