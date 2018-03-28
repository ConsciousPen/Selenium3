package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.home_ca.ho4.HomeCaHO4OpenLFile;
import aaa.helpers.openl.model.home_ca.ho4.HomeCaHO4OpenLPolicy;
import aaa.helpers.openl.testdata_builder.HomeCaHO4TestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import toolkit.datax.TestData;

public class HomeCaHO4PremiumCalculationTest extends OpenLRatingBaseTest<HomeCaHO4OpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO4;
	}

	@Override
	protected TestData getRatingDataPattern() {
		return getPolicyTD("DataGather", "TestData_CA").mask(new PurchaseTab().getMetaKey());
	}

	@Override
	protected String createAndRateQuote(TestDataGenerator<HomeCaHO4OpenLPolicy> tdGenerator, HomeCaHO4OpenLPolicy openLPolicy) {
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumsAndCoveragesQuoteTab.class, false);
		new PremiumsAndCoveragesQuoteTab().fillTab(quoteRatingData);
		return Tab.labelPolicyNumber.getValue();
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<HomeCaHO4OpenLPolicy> tdGenerator = new HomeCaHO4TestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, HomeCaHO4OpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
