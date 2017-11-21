package aaa.utils.openl.testdata_builder;

import toolkit.datax.TestData;

public interface OpenLTestDataBuilder<P> {
	TestData buildRatingData(P openLPolicy);
}
