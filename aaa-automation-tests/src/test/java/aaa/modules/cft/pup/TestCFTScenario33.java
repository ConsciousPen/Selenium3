package aaa.modules.cft.pup;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.modules.cft.ControlledFinancialBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * Controlled Financial Testing Scenario 33
 * Quarterly payment plan
 * Bill generation
 * Refund
 * Escheatment
 */
public class TestCFTScenario33 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT, Groups.TIMEPOINT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	@StateList(states = {Constants.States.CA})
	public void cftTestScenario33(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		generateInstallmentBill(1);
		acceptTotalDuePayment();
		generateRenewalImage();
		generateRenewalOffer();
		generateRenewalOfferBill();
		verifyRenewCustomerDecline();
		acceptTotalDuePlusOverpaymentOnRenewCustomerDeclineDate(new Dollar(400));
		automatedRefundOnRefundDate();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(TestData.makeKeyPath(PremiumAndCoveragesQuoteTab.class.getSimpleName(), PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), getTestSpecificTD(
				"PremiumAndCoveragesQuoteTab_DataGather").getValue(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel())).resolveLinks();
		return new PrefillTab().adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
	}
}
