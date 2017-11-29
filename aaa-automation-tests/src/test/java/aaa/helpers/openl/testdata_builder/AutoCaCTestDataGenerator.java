package aaa.helpers.openl.testdata_builder;

import aaa.helpers.openl.model.auto_ca.AutoCaCOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class AutoCaCTestDataGenerator implements TestDataGenerator<AutoCaCOpenLPolicy> {
	@Override
	public TestData getRatingData(AutoCaCOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
}
