package aaa.helpers.openl.testdata_builder;

import aaa.helpers.openl.model.OpenLPolicy;
import toolkit.datax.TestData;

@FunctionalInterface
public interface TestDataGenerator<P extends OpenLPolicy> {
	TestData getRatingData(P openLPolicy);
}
