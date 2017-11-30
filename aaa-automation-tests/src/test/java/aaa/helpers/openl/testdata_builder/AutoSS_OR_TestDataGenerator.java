package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLPolicy;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class AutoSS_OR_TestDataGenerator extends AutoSSTestDataGenerator<AutoSSOpenLPolicy> {
	protected TestData getPrefillTabData() {
		return DataProviderFactory.dataOf(
				AutoSSMetaData.PrefillTab.VALIDATE_ADDRESS_BTN.getLabel(), "click",
				AutoSSMetaData.PrefillTab.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.emptyData(),
				AutoSSMetaData.PrefillTab.ORDER_PREFILL.getLabel(), "click");
	}

	@Override
	public TestData getRatingData(AutoSSOpenLPolicy openLPolicy) {
		TestData td = getRatingDataPattern();
		td.adjust(DataProviderFactory.dataOf(
				new PrefillTab().getMetaKey(), getPrefillTabData(),
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new DriverTab().getMetaKey(), getDriverTabData(openLPolicy),
				new RatingDetailReportsTab().getMetaKey(), getRatingDetailReportsTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new FormsTab().getMetaKey(), getFormsTabTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy)));
		return td;
	}

	protected TestData getGeneralTabData(AutoSSOpenLPolicy openLPolicy) {
		TestData namedInsuredInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE, getGeneralTabResidence(openLPolicy.isHomeOwner())
				/* to be continued */);

		TestData aAAProductOwnedData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER, getYesOrNo(openLPolicy.isHomeOwner()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.HOME, openLPolicy.getAaaHomePolicy(),
				AutoSSMetaData.GeneralTab.AAAProductOwned.RENTERS, getYesOrNo(openLPolicy.getAaaRentersPolicy()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.CONDO, getYesOrNo(openLPolicy.getAaaCondoPolicy()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.LIFE, openLPolicy.isAaaLifePolicy(),
				AutoSSMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE, openLPolicy.isAaaMotorcyclePolicy()
		);

		TestData contactInformationData = DataProviderFactory.dataOf(/* to be done */);

		TestData currentCarrierInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER, "Yes",
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS, getGeneralTabPriorBILimit(openLPolicy.getPriorBILimit()),
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.INCEPTION_DATE, openLPolicy.getCappingDetails().get(0).getPlcyInceptionDate().format(DateTimeUtils.MM_DD_YYYY));
				/* to be continued */

		TestData policyInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel(), getGeneralTabTerm(openLPolicy.getTerm()),
				AutoSSMetaData.GeneralTab.PolicyInformation.ADVANCED_SHOPPING_DISCOUNTS.getLabel(), generalTabIsAdvanceShopping(openLPolicy.isAdvanceShopping())
		/* to be continued */);

		return DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), namedInsuredInformationData,
				AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), aAAProductOwnedData,
				AutoSSMetaData.GeneralTab.CONTACT_INFORMATION.getLabel(), contactInformationData,
				AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(), currentCarrierInformationData,
				AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), policyInformationData);
				/*AutoSSMetaData.PrefillTab.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.emptyData(),
				AutoSSMetaData.PrefillTab.ORDER_PREFILL.getLabel(), "click");*/
	}

	protected List<TestData> getDriverTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		List<TestData> tdList = new ArrayList<>(openLPolicy.getDrivers().size());
		tdList.add(DataProviderFactory.emptyData());
		return tdList;
	}

	protected TestData getRatingDetailReportsTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		return DataProviderFactory.emptyData();
	}

	protected List<TestData> getVehicleTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		List<TestData> tdList = new ArrayList<>(openLPolicy.getVehicles().size());
		tdList.add(DataProviderFactory.emptyData());
		return tdList;
	}

	protected TestData getFormsTabTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		return DataProviderFactory.emptyData();
	}

	protected TestData getPremiumAndCoveragesTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		return DataProviderFactory.emptyData();
	}
}
