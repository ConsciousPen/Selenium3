package aaa.helpers.openl.testdata_builder;

import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class AutoCaTestDataGenerator implements TestDataGenerator<AutoCaOpenLPolicy> {
	@Override
	public TestData getRatingData(AutoCaOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
}
