package aaa.helpers.openl.testdata_builder;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLPolicy;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class AutoCaSelectTestDataGenerator extends TestDataGenerator<AutoCaSelectOpenLPolicy> {
	public AutoCaSelectTestDataGenerator(String state) {
		super(state);
	}

	public AutoCaSelectTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(AutoCaSelectOpenLPolicy openLPolicy) {
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

	private TestData getGeneralTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		TestData td = DataProviderFactory.dataOf(
		);
		return DataProviderFactory.emptyData();
	}

	private List<TestData> getDriverTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		return Collections.singletonList(DataProviderFactory.emptyData());
	}

	private TestData getMembershipTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private List<TestData> getVehicleTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		return Collections.singletonList(DataProviderFactory.emptyData());
	}

	private TestData getAssignmentTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private TestData getFormsTabTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private TestData getPremiumAndCoveragesTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
}
