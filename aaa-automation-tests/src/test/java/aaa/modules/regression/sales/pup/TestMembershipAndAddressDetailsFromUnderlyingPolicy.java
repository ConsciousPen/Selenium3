package aaa.modules.regression.sales.pup;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.GeneralTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.util.HashMap;
import java.util.Map;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author mlaptsionak
 * @name Validate Membership details and Dwelling address details should be fetched from the underlying policy  ("PC81-M-F-PUP-CA-3604")
 * @scernario 1. Create a CA Auto policy with the valid membership number and bind the policy
 * 2. Create a HO policy with the same valid membership number as used in Auto policy and bind the policy
 * 3. Initiate a PUP policy and add those two Auto and HO as an underlying policy(Auto policy should be added first
 * 4. Enter all mandatory fields and click order prefill on corresponding underlying HO policy. Set HO policy sa Primary poliy.
 * 5. Navigate to general page and validate membership and dwelling address details got prefilled correctly from the underlying policy
 * Expected: Membership details and Dwelling address details should be fetched from the underlying policy and should not display empty.
 */

public class TestMembershipAndAddressDetailsFromUnderlyingPolicy extends PersonalUmbrellaBaseTest {

	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testMembershipAndAddressDetailsFromUnderlyingPolicy(@Optional("CA") String state) {

		TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_CA_SELECT), "DataGather", "TestData");
		TestData tdHO3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO3), "DataGather", "TestData");
		Map<String, String> policies = new HashMap<>();
		// Create Customer
		mainApp().open();
		createCustomerIndividual();
		// Create a CA Auto policy
		PolicyType.AUTO_CA_SELECT.get().createPolicy(tdAuto);
		policies.put("autoPolicy", PolicySummaryPage.getPolicyNumber());
		// Create a HO policy with the same valid membership number
		PolicyType.HOME_CA_HO3.get().createPolicy(tdHO3);
		policies.put("ho3Policy", PolicySummaryPage.getPolicyNumber());
		// Generate PUP test Data
		TestData tdPrefillTab = getTestSpecificTD("TestData")
				.adjust("ActiveUnderlyingPolicies[0]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("autoPolicy"))
				.adjust("ActiveUnderlyingPolicies[1]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("ho3Policy"));
		TestData tdPUP = getPolicyTD().adjust(PrefillTab.class.getSimpleName(), tdPrefillTab);
		// Initiate a PUP policy and add those two Auto and HO as an underlying policy
		policy.initiate();
		policy.getDefaultView().fillUpTo(tdPUP, GeneralTab.class);
		//validate membership number
		assertThat(new GeneralTab().getAAAMembershipAssetList().getAsset(PersonalUmbrellaMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel()).getValue())
				.isEqualTo(tdAuto.getValue(GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoCaMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel()));
		assertThat(new GeneralTab().getAAAMembershipAssetList().getAsset(PersonalUmbrellaMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue())
				.isEqualTo(tdAuto.getValue(GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel()));
		//validate dwelling details
		assertThat(new GeneralTab().getDwellingAddressAssetList().getAsset(PersonalUmbrellaMetaData.GeneralTab.DwellingAddress.STREET_ADDRESS_1.getLabel()).getValue())
				.isEqualTo(tdAuto.getValue(aaa.main.modules.policy.auto_ca.defaulttabs.PrefillTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.NamedInsuredInformation.ADDRESS_LINE_1.getLabel()));
		assertThat(new GeneralTab().getDwellingAddressAssetList().getAsset(PersonalUmbrellaMetaData.GeneralTab.DwellingAddress.ZIP_CODE.getLabel()).getValue())
				.isEqualTo(tdAuto.getValue(aaa.main.modules.policy.auto_ca.defaulttabs.PrefillTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.NamedInsuredInformation.ZIP_CODE.getLabel()));
	}

}
