package aaa.helpers.openl.testdata_generator;

import java.util.List;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLForm;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

public class HomeSSHO3TestDataGenerator extends HomeSSTestDataGenerator {
	public HomeSSHO3TestDataGenerator(String state, TestData ratingDataPattern) {
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
			if (!endorsementData.containsKey(HomeSSHO3FormTestDataGenerator.getFormMetaKey(formCode))) {
				List<TestData> tdList = HomeSSHO3FormTestDataGenerator.getFormTestData(openLPolicy, formCode);
				if (tdList != null) {
					TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeSSHO3FormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeSSHO3FormTestDataGenerator.getFormMetaKey(formCode), tdList);
					endorsementData.adjust(td);
				}
			}
		}
		return endorsementData;
	}
}
