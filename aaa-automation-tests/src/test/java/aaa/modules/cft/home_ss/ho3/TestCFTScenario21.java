package aaa.modules.cft.home_ss.ho3;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.cft.ControlledFinancialBaseTest;

import com.exigen.ipb.etcsa.utils.Dollar;

/**
 * Controlled Financial Testing Scenario 21
 * Annual_Renewal with Emp Ben
 * Annual-Full payment
 * Cash Down Payment
 * Renewal
 * Emp Benefit
 */
public class TestCFTScenario21 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario21(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		generateRenewalImage();
		generateRenewalOffer();
		generateRenewalOfferBill();
		verifyRenewCustomerDecline();
		acceptTotalDuePlusOverpaymentOnRenewCustomerDeclineDate(new Dollar(400));
		issuedRefundOnRefundDate();
		verifyEscheatmentOnExpDatePlus25Plus13Months();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel()),
			getTestSpecificTD("ApplicantTab_DataGather").getValue(HomeSSMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel()));
		td.adjust(TestData.makeKeyPath(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), getTestSpecificTD(
			"PremiumsAndCoveragesQuoteTab_DataGather").getValue(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()));
		return td.resolveLinks();
	}
}
