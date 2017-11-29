package aaa.helpers.openl.testdata_builder;

import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class HomeCaTestDataGenerator implements TestDataGenerator<HomeCaOpenLPolicy> {
	@Override
	public TestData getRatingData(HomeCaOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
}
