package aaa.modules.rating.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.openl.OpenLHelper;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.rating.RatingBaseTest;
import aaa.utils.openl.model.AutoSSOpenLFile;
import aaa.utils.openl.model.AutoSSOpenLPolicy;
import aaa.utils.openl.testdata_builder.AutoSSTestDataBuilder;

public class PremiumCalculationTest extends RatingBaseTest<AutoSSOpenLPolicy> {
	public PremiumCalculationTest() {
		super(new AutoSSTestDataBuilder());
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test
	public void premiumCalculationTest(@Optional("") String state) {
		//OpenLFileParser<AutoSSOpenLPolicy, AutoSSOpenLFields> openLParser = new AutoSSOpenLFileParser(String.format("%1$s/%2$sTests-20170915.xls", OPENL_RATING_TESTS_FOLDER, getState()));
		AutoSSOpenLFile openLFile = OpenLHelper.excelToModel(String.format("%1$s/%2$sTests-20170915.xls", OPENL_RATING_TESTS_FOLDER, getState()), AutoSSOpenLFile.class);
		verifyPremiums(openLFile.getPolicies());
	}
}
