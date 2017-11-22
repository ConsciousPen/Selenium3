package aaa.utils.openl.testdata_builder;

import aaa.utils.openl.model.HomeSSOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class HomeSSTestDataBuilder implements TestDataBuilder<HomeSSOpenLPolicy> {
	@Override
	public TestData buildRatingData(HomeSSOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
}
