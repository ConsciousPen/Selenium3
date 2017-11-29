package aaa.helpers.openl.testdata_builder;

import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class HomeSSTestDataGenerator implements TestDataGenerator<HomeSSOpenLPolicy> {
	@Override
	public TestData getRatingData(HomeSSOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
}
