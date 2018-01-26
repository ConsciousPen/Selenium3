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
import aaa.main.modules.policy.auto_ca.actiontabs.EndorsementActionTab;
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
		acceptPaymentOnStartDatePlus2();
		addSuspenseOnStartDatePlus2();
		generateInstallmentBill(1);
		flatCancellationOnStartDatePlus16();
		clearSuspenseOnStartDatePlus16();
		acceptMinDuePaymentOnStartDatePlus25();
		manualReinstatementOnStartDatePlus25();
		endorsementDateDataKeys = new String[]{new EndorsementActionTab().getMetaKey(), AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()};
		endorsePolicyOnCancellationNoticeDate();
		declineSuspensePaymentOnCancellationDate();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(TestData
			.makeKeyPath(GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
			getTestSpecificTD("GeneralTab_DataGather").getValue(
				AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()));
		td.adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), getTestSpecificTD(
			"PremiumAndCoveragesTab_DataGather").getValue(AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()));
		return td.resolveLinks();
	}
}
