package aaa.helpers.openl.testdata_generator;

import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import toolkit.datax.TestData;

public class HomeSSHO6TestDataGenerator extends HomeSSTestDataGenerator {
	public HomeSSHO6TestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeSSOpenLPolicy openLPolicy) {
		return super.getRatingData(openLPolicy);
	}

}
