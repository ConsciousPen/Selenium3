package aaa.helpers.openl.testdata_builder;

import java.util.Collections;
import java.util.List;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLPolicy;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PrefillTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class AutoCaChoiceTestDataGenerator extends TestDataGenerator<AutoCaChoiceOpenLPolicy> {
	public AutoCaChoiceTestDataGenerator(String state) {
		super(state);
	}

	public AutoCaChoiceTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(AutoCaChoiceOpenLPolicy openLPolicy) {
		TestData td = DataProviderFactory.dataOf(
				new PrefillTab().getMetaKey(), getPrefillTabData(),
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new DriverTab().getMetaKey(), getDriverTabData(openLPolicy),
				new MembershipTab().getMetaKey(), getMembershipTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new AssignmentTab().getMetaKey(), getAssignmentTabData(openLPolicy),
				new FormsTab().getMetaKey(), getFormsTabTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy));
		return TestDataHelper.merge(getRatingDataPattern(), td);
	}

	private TestData getPrefillTabData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getGeneralTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private List<TestData> getDriverTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		return Collections.singletonList(DataProviderFactory.emptyData());
	}

	private TestData getMembershipTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private List<TestData> getVehicleTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		return Collections.singletonList(DataProviderFactory.emptyData());
	}

	private TestData getAssignmentTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private TestData getFormsTabTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private TestData getPremiumAndCoveragesTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
}
