package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLFile;
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
	protected void createAndRateQuote(TestDataGenerator<AutoSSOpenLPolicy> tdGenerator, AutoSSOpenLPolicy openLPolicy) {
		if ("LegacyConv".equals(openLPolicy.getCappingDetails().get(0).getProgramCode())) {
			TestData renewalEntryData = ((AutoSSTestDataGenerator) tdGenerator).getRenewalEntryData(openLPolicy);
			TestData quoteRatingData = ((AutoSSTestDataGenerator) tdGenerator).getRatingData(openLPolicy, true);

			customer.initiateRenewalEntry().perform(renewalEntryData);
			policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
			new PremiumAndCoveragesTab().fillTab(quoteRatingData);
		} else {
			super.createAndRateQuote(tdGenerator, openLPolicy);
		}
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<AutoSSOpenLPolicy> tdGenerator = new AutoSSTestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, AutoSSOpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
