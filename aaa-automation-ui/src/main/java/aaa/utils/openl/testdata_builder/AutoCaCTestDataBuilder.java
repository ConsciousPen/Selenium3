package aaa.utils.openl.testdata_builder;

import aaa.utils.openl.model.AutoCaCOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class AutoCaCTestDataBuilder implements TestDataBuilder<AutoCaCOpenLPolicy> {
	@Override
	public TestData buildRatingData(AutoCaCOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
}
