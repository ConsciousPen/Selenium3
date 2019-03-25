package aaa.modules.openl;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.helpers.openl.model.auto_ss.AutoSSOpenLVehicle;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLPolicy;
import aaa.helpers.openl.testdata_generator.AutoSSTestDataGenerator;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import org.apache.commons.lang3.StringUtils;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class AutoSSPremiumCalculationTest extends OpenLRatingBaseTest<AutoSSOpenLPolicy> {

	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new VehicleTab().getMetaKey(), new PremiumAndCoveragesTab().getMetaKey());
	}

	@Override
	protected String createQuote(AutoSSOpenLPolicy openLPolicy) {
		PremiumAndCoveragesTab pacTab = new PremiumAndCoveragesTab();
		AutoSSTestDataGenerator tdGenerator = openLPolicy.getTestDataGenerator(getRatingDataPattern());
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		TestData cappingData = tdGenerator.getCappingData(openLPolicy);

		if (openLPolicy.isLegacyConvPolicy()) {
			TestData renewalEntryData = tdGenerator.getRenewalEntryData(openLPolicy);
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
			}
			customer.initiateRenewalEntry().perform(renewalEntryData);
		} else {
			policy.get().initiate();
		}

		policy.get().getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
		pacTab.getAssetList().fill(quoteRatingData);

		if (openLPolicy.isCappedPolicy()) {
			if (!openLPolicy.isLegacyConvPolicy()) {
				policyPurchaseAndRenew(tdGenerator.getPolicyPurchaseData(), tdGenerator.getPolicyRenewData(), openLPolicy);
			}
			pacTab.calculatePremium();
			assertThat(PremiumAndCoveragesTab.buttonViewCappingDetails).as("View Capping Details button did not appear after premium calculation").isPresent();
			pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.VIEW_CAPPING_DETAILS_DIALOG).fill(cappingData);
		}

		return Tab.labelPolicyNumber.getValue();
	}

	@Override
	protected Dollar calculatePremium(AutoSSOpenLPolicy openLPolicy) {
		new PremiumAndCoveragesTab().calculatePremium();
		Dollar totalPremium = PremiumAndCoveragesTab.getTotalTermPremium();
		if (PremiumAndCoveragesTab.tableStateAndLocalTaxesSummaryDetailed.isPresent() || PremiumAndCoveragesTab.tableStateAndLocalTaxesSummary.isPresent()) { // WV and KY states have AP/RP taxes
			totalPremium = totalPremium.subtract(PremiumAndCoveragesTab.getStateAndLocalTaxesAndPremiumSurchargesPremium());
		}
		return totalPremium;
	}

	@Override
	protected String createCustomerIndividual(AutoSSOpenLPolicy openLPolicy) {
		int driverAge = openLPolicy.isNewRenPasCappedPolicy() ? openLPolicy.getDrivers().get(0).getDriverAge() - 1 : openLPolicy.getDrivers().get(0).getDriverAge();
		TestData td = getCustomerIndividualTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.DATE_OF_BIRTH.getLabel()),
						AutoSSTestDataGenerator.getDriverTabDateOfBirth(driverAge, openLPolicy.getEffectiveDate()));
		return createCustomerIndividual(td);
	}

	/** This method issues quote and creates renewal for tests which have capping but are not LegacyConv. **/
	private void policyPurchaseAndRenew(TestData tdPurchase, TestData tdRenew, AutoSSOpenLPolicy openLPolicy) {
		VehicleTab vehicleTab = new VehicleTab();
		new PremiumAndCoveragesTab().calculatePremium();
		PremiumAndCoveragesTab.buttonContinue.click();
		ErrorTab errorTab = new ErrorTab();
		if (errorTab.isVisible()) {
			errorTab.overrideAllErrors();
			errorTab.override();
			PremiumAndCoveragesTab.buttonContinue.click();
		}
		policy.get().getDefaultView().fillUpTo(tdPurchase, PurchaseTab.class, false);
		if (errorTab.isVisible()) {
			errorTab.overrideAllErrors();
			errorTab.submitTab();
		}
		policy.get().getDefaultView().fill(DataProviderFactory.dataOf(PurchaseTab.class.getSimpleName(), tdPurchase.getTestData(PurchaseTab.class.getSimpleName())));
		policy.get().renew().perform();

		policy.get().getDefaultView().fillUpTo(tdRenew, VehicleTab.class, false);

		//update VIN codes or you will get error 500 on Premium tab
		for (AutoSSOpenLVehicle vehicle : openLPolicy.getVehicles()) {
			if (StringUtils.isNotBlank(vehicle.getVinCode())) {
				vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LIST_OF_VEHICLE).getTable().getRow(openLPolicy.getVehicles().indexOf(vehicle)+1).
						getCell(5).controls.links.get("View/Edit").click();
				vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).clear();
				vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).waitForPageUpdate();
				vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue(vehicle.getVinCode());
			}
		}

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
	}
}
