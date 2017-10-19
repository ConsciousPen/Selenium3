package aaa.modules.cft.auto_ca.choice;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.actiontabs.CancellationActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.cft.ControlledFinancialBaseTest;

/**
 * NB_FD _Flat_cancel
 * Reinstatement_Endorse
 * Standard monthly
 * Policy Effective Date (Future Dated) = X+2
 * Cash Down Payment
 * 
 *
 */
public class TestCFTScenario13 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario13(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		acceptPaymentStartDatePlus2();
		manualCancellationStartDatePlus16(TestData.makeKeyPath(CancellationActionTab.class.getSimpleName(), AutoCaMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()));
		manualReinstatementStartDatePlus25();
		endorsePolicyCancellationNoticeDate();

	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(GeneralTab.class.getSimpleName(), getTestSpecificTD("GeneralTab_DataGather"));
		td.adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab_DataGather"));
		return td.resolveLinks();
	}
}
