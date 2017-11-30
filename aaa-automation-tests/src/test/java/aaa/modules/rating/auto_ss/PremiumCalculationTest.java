package aaa.modules.rating.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLFile;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLPolicy;
import aaa.helpers.openl.testdata_builder.AutoSS_OR_TestDataGenerator;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.rating.RatingBaseTest;
import toolkit.datax.TestData;

public class PremiumCalculationTest extends RatingBaseTest<AutoSSOpenLPolicy> {
	public PremiumCalculationTest() {
		super(new AutoSS_OR_TestDataGenerator());
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern()
				.mask(new DriverTab().getMetaKey(), new VehicleTab().getMetaKey())

				//not necessary
				.mask(new DriverActivityReportsTab().getMetaKey(), new DocumentsAndBindTab().getMetaKey())
				.mask(TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(),
						AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getLabel()));
	}

	@Parameters({"state"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state) {
		String openLFileName = getState() + "Tests-20170915.xls";
		verifyPremiums(openLFileName, AutoSSOpenLFile.class);
	}
}
