package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLFile;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLPolicy;
import aaa.helpers.openl.testdata_builder.AutoCaChoiceTestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import toolkit.datax.TestData;

public class AutoCaChoicePremiumCalculationTest extends OpenLRatingBaseTest<AutoCaChoiceOpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new DriverTab().getMetaKey(), new VehicleTab().getMetaKey(), new PremiumAndCoveragesTab().getMetaKey());
	}

	@Override
	protected String createAndRateQuote(TestDataGenerator<AutoCaChoiceOpenLPolicy> tdGenerator, AutoCaChoiceOpenLPolicy openLPolicy) {
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
		new PremiumAndCoveragesTab().fillTab(quoteRatingData);
		return Tab.labelPolicyNumber.getValue();
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<AutoCaChoiceOpenLPolicy> tdGenerator = new AutoCaChoiceTestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, AutoCaChoiceOpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
