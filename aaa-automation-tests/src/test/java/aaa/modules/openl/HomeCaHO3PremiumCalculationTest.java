package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLFile;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLPolicy;
import aaa.helpers.openl.testdata_builder.HomeCaHO3TestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import toolkit.datax.TestData;

public class HomeCaHO3PremiumCalculationTest extends OpenLRatingBaseTest<HomeCaHO3OpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Override
	protected String createAndRateQuote(TestDataGenerator<HomeCaHO3OpenLPolicy> tdGenerator, HomeCaHO3OpenLPolicy openLPolicy) {
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumsAndCoveragesQuoteTab.class, false);
		new PremiumsAndCoveragesQuoteTab().fillTab(quoteRatingData);
		return Tab.labelPolicyNumber.getValue();
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<HomeCaHO3OpenLPolicy> tdGenerator = new HomeCaHO3TestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, HomeCaHO3OpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
