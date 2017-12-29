package aaa.modules.conversion.manual;

import aaa.modules.policy.HomeSSDP3BaseTest;
import toolkit.datax.TestData;

public class ConvHomeSsDP3BaseTest extends HomeSSDP3BaseTest implements ManualConversionHelper {

    @Override
    protected TestData prepareManualConversionTd() {
        TestData td = super.prepareManualConversionTd();

        return modifyForHome(getPolicyType(), modifyTd(td, tdCustomerIndividual, getState()));

    }

}
