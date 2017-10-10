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
 * Controlled Financial Testing Scenario 2
 * For any product and any defined state from params
 * NB With Emp Ben
 * Down pay_ Check
 * Cancel
 * waive fee
 */
public class TestCFTScenario2 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario2(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		endorsePolicyEffDatePlus2Days();
		generateInstallmentBill(1);
		waiveFee();
		manualFutureCancellationEffDatePlus25Days();
		updatePolicyStatusForPendedCancellation();
		manualReinstatement();
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
		td.adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel()),
				getTestSpecificTD("ApplicantTab_DataGather").getValue(HomeSSMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel()));
		/*if (getState().equals(Constants.States.CT)) {
			TestData data = DataProviderFactory.dataOf(DialogsMetaData.AddressValidationMetaData.STREET_NUMBER.getLabel(),
					getTestSpecificTD("ValidateAddressDialog_DataGather").getValue(DialogsMetaData.AddressValidationMetaData.STREET_NUMBER.getLabel()),
					DialogsMetaData.AddressValidationMetaData.STREET_NAME.getLabel(),
					getTestSpecificTD("ValidateAddressDialog_DataGather").getValue(DialogsMetaData.AddressValidationMetaData.STREET_NAME.getLabel()));
			td.adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), HomeSSMetaData.ApplicantTab.DwellingAddress.VALIDATE_ADDRESS_DIALOG.getLabel()),
					data);
			td.adjust(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.class.getSimpleName()),
					getTestSpecificTD("PublicProtectionClass_DataGather"));
		}*/
		return td.resolveLinks();
	}

}
