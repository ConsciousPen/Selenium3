package aaa.utils.openl.testdata_builder;

import aaa.utils.openl.model.HomeCaOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class HomeCaTestDataBuilder implements TestDataBuilder<HomeCaOpenLPolicy> {
	@Override
	public TestData buildRatingData(HomeCaOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
}
