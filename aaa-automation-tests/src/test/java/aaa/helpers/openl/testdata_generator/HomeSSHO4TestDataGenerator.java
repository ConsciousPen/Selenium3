package aaa.helpers.openl.testdata_generator;

import java.util.List;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLForm;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

public class HomeSSHO4TestDataGenerator extends HomeSSTestDataGenerator {
	public HomeSSHO4TestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeSSOpenLPolicy openLPolicy) {
		return super.getRatingData(openLPolicy);
	}

	@Override
	protected TestData getEndorsementTabData(HomeSSOpenLPolicy openLPolicy) {
		TestData endorsementData = new SimpleDataProvider();
		for (HomeSSOpenLForm openLForm : openLPolicy.getForms()) {
			String formCode = openLForm.getFormCode();
			if (!endorsementData.containsKey(HomeSSHO4FormTestDataGenerator.getFormMetaKey(formCode))) {
				List<TestData> tdList = HomeSSHO4FormTestDataGenerator.getFormTestData(openLPolicy, formCode);
				if (tdList != null) {
					TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeSSHO4FormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeSSHO4FormTestDataGenerator.getFormMetaKey(formCode), tdList);
					endorsementData.adjust(td);
				}
			}
		}
		return endorsementData;
	}
}
