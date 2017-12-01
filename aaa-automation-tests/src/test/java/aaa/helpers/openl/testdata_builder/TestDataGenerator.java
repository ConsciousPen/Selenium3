package aaa.helpers.openl.testdata_builder;

import aaa.helpers.openl.model.OpenLPolicy;
import toolkit.datax.TestData;

public abstract class TestDataGenerator<P extends OpenLPolicy> {
	private TestData ratingDataPattern;

	TestData getRatingDataPattern() {
		return ratingDataPattern;
	}

	public void setRatingDataPattern(TestData ratingDataPattern) {
		this.ratingDataPattern = ratingDataPattern;
	}

	public abstract TestData getRatingData(P openLPolicy);

	String getYesOrNo(boolean value) {
		return value ? "Yes" : "No";
	}

	String getYesOrNo(String value) {
		return "Y".equalsIgnoreCase(value) ? "Yes" : "No";
	}
}
