package aaa.modules.conversion.manual;

import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;

public class ConvAutoSsBaseTest extends AutoSSBaseTest implements ManualConversionHelper {

    @Override
    protected TestData prepareManualConversionTd() {
        TestData td = super.prepareManualConversionTd();
        return modifyTd(td, tdCustomerIndividual, getState());
    }

}
