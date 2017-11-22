package aaa.utils.openl.testdata_builder;

import aaa.utils.openl.model.OpenLPolicy;
import toolkit.datax.TestData;

public interface TestDataBuilder<P extends OpenLPolicy> {
	TestData buildRatingData(P openLPolicy);
}
