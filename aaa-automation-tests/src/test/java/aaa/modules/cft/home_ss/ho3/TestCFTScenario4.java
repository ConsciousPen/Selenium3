package aaa.modules.cft.home_ss.ho3;

import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.cft.ControlledFinancialBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * Controlled Financial Testing Scenario 4
 * NB_Down_Credit Card
 * 1st installment PT
 * Lapse w/o emp ben
 */
public class TestCFTScenario4 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void createPolicy(@Optional(StringUtils.EMPTY) String state) {
		super.createPolicyForTest();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "createPolicy")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateBillForFirstInstallment(@Optional(StringUtils.EMPTY) String state) {
		super.generateFirstInstallmentBill();
		super.payInstallmentWithMinDue();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "generateBillForFirstInstallment")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void endorsePolicy(@Optional(StringUtils.EMPTY) String state) {
		super.endorsePolicyEffDatePlus16Days();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "endorsePolicy")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void acceptPayment(@Optional(StringUtils.EMPTY) String state) {
		super.acceptPaymentEffDatePlus25();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), getTestSpecificTD("PremiumsAndCoveragesQuoteTab_DataGather"));
		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
		td.adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(),HomeSSMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel()),
				getTestSpecificTD("ApplicantTab_DataGather").getValue(HomeSSMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel()));
		return td.resolveLinks();
	}

}
