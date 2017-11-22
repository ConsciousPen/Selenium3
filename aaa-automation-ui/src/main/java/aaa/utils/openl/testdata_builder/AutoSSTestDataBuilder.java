package aaa.utils.openl.testdata_builder;

import java.util.ArrayList;
import java.util.List;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.utils.openl.model.AutoSSOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class AutoSSTestDataBuilder implements TestDataBuilder<AutoSSOpenLPolicy> {
	@Override
	public TestData buildRatingData(AutoSSOpenLPolicy openLPolicy) {
		return DataProviderFactory.dataOf(
				new PrefillTab().getMetaKey(), getPrefillTabData(),
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new DriverTab().getMetaKey(), getDriverTabData(openLPolicy),
				new RatingDetailReportsTab().getMetaKey(), getRatingDetailReportsTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new FormsTab().getMetaKey(), getFormsTabTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy));
	}

	private TestData getPrefillTabData() {
		return DataProviderFactory.dataOf(
				AutoSSMetaData.PrefillTab.VALIDATE_ADDRESS_BTN.getLabel(), "click",
				AutoSSMetaData.PrefillTab.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.emptyData(),
				AutoSSMetaData.PrefillTab.ORDER_PREFILL.getLabel(), "click");
	}

	private TestData getGeneralTabData(AutoSSOpenLPolicy openLPolicy) {
		return DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), DataProviderFactory.dataOf(
						AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TYPE.getLabel(), "Standard",
						AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
						AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel(), getTermValue(openLPolicy))
		);
				/*AutoSSMetaData.PrefillTab.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.emptyData(),
				AutoSSMetaData.PrefillTab.ORDER_PREFILL.getLabel(), "click");*/
	}

	private List<TestData> getDriverTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		List<TestData> tdList = new ArrayList<>(openLPolicy.getDrivers().size());
		tdList.add(DataProviderFactory.emptyData());
		return tdList;
	}

	private TestData getRatingDetailReportsTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		return DataProviderFactory.emptyData();
	}

	private List<TestData> getVehicleTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		List<TestData> tdList = new ArrayList<>(openLPolicy.getVehicles().size());
		tdList.add(DataProviderFactory.emptyData());
		return tdList;
	}

	private TestData getFormsTabTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		return DataProviderFactory.emptyData();
	}

	private TestData getPremiumAndCoveragesTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		return DataProviderFactory.emptyData();
	}

	private String getTermValue(AutoSSOpenLPolicy openLPolicy) {
		switch (openLPolicy.getTerm()) {
			case 12:
				return "Annual";
			case 6:
				return "Semi-Annual";
			default:
				throw new IstfException("Unable to build test data. Unsupported openL policy term: " + openLPolicy.getTerm());
		}
	}

}
