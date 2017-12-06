package aaa.modules.rating.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLFile;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLPolicy;
import aaa.helpers.openl.testdata_builder.AutoSSTestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.rating.RatingBaseTest;
import toolkit.datax.TestData;

public class PremiumCalculationTest extends RatingBaseTest<AutoSSOpenLPolicy> {
	public PremiumCalculationTest() {
		super(new AutoSSTestDataGenerator());
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern()
				.mask(new DriverTab().getMetaKey(), new VehicleTab().getMetaKey());

		//not necessary
				/*.mask(new DriverActivityReportsTab().getMetaKey(), new DocumentsAndBindTab().getMetaKey())
				.mask(TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(),
						AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getLabel()));*/
	}

	@Parameters({"state", "filePath", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, @Optional("src/test/resources/openl/auto_ss/ORTests-20170915.xls") String filePath, @Optional("") String policyNumbers) {
		verifyPremiums(filePath, AutoSSOpenLFile.class, getPolicyNumbers(policyNumbers));
	}
}
