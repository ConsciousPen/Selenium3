package aaa.modules.openl;

import org.testng.ITestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLPolicy;
import aaa.helpers.openl.testdata_builder.AutoSSTestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import toolkit.datax.TestData;

public class AutoSSPremiumCalculationTest extends OpenLRatingBaseTest<AutoSSOpenLPolicy> {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new DriverTab().getMetaKey(), new VehicleTab().getMetaKey(), new PremiumAndCoveragesTab().getMetaKey());
	}

	@Override
	protected Dollar createAndRateQuote(AutoSSOpenLPolicy openLPolicy) {
		boolean isLegacyConvPolicy = false;
		AutoSSTestDataGenerator tdGenerator = openLPolicy.getTestDataGenerator(getState(), getRatingDataPattern());

		if (TestDataGenerator.LEGACY_CONV_PROGRAM_CODE.equals(openLPolicy.getCappingDetails().getProgramCode())) {
			isLegacyConvPolicy = true;
			TestData renewalEntryData = tdGenerator.getRenewalEntryData(openLPolicy);

			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
			}
			customer.initiateRenewalEntry().perform(renewalEntryData);
		} else {
			policy.initiate();
		}
		
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy, isLegacyConvPolicy);
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
		new PremiumAndCoveragesTab().fillTab(quoteRatingData);
		Dollar totalPremium = PremiumAndCoveragesTab.getTotalTermPremium();
		if (PremiumAndCoveragesTab.tableStateAndLocalTaxesSummary.isPresent()) { // WV and KY states have AP/RP taxes
			totalPremium = totalPremium.subtract(PremiumAndCoveragesTab.getStateAndLocalTaxesAndPremiumSurchargesPremium());
		}
		return totalPremium;
	}
	
	@Parameters({"state"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, ITestContext context) {
		verifyPremiums(context);
	}
}
