package aaa.modules.regression.conversions.template;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class TestPolicyRmeNyLegacyTierFieldTemplate extends PolicyBaseTest {

	protected static String LEGACY_TIER_REQUIRED_MESSAGE = "Legacy Tier is required";
	protected static String LEGACY_TIER_IS_OUT_OF_RANGE_MESSAGE = "Legacy Tier is out of range if the field is populated with anything other than the numbers 1-50";

	private InitiateRenewalEntryActionTab initiateRenewalEntryActionTab = new InitiateRenewalEntryActionTab();

	/**
	 * @author Sushil Sivaram, Rokas Lazdauskas
	 * @name Test Policy RME Legacy Tier field
	 * @scenario
	 * 1. Create Individual Customer / Account
	 * 2. Select RME Action with HSS product
	 * 3. Fill everything in RME screen except "Legacy Tier"
	 * 4. Verify "Legacy  Tier" field is exist on RME screen
	 * 5. Submit tab and check "Legacy Tier Is Required" message
	 * 6. Try filling alphabetical, special charecter or numeric value which is not in range 1-50 and submiting tab
	 * 7. Check that "Legacy tier message is out of range" message appears.
	 * 8. Fill "Legacy Tier" field with correct value
	 * 9. Check that user is able to proceed.
	 */
	protected void testPolicyRmeLegacyTier() {
		mainApp().open();
		createCustomerIndividual();

		customer.initiateRenewalEntry().start();

		initiateRenewalEntryActionTab.fillTab(getManualConversionInitiationTd()
				.mask(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(),
						CustomerMetaData.InitiateRenewalEntryActionTab.LEGACY_TIER.getLabel())));

		//Verify "Legacy Tier" Text Box is exist on RME screen
		assertThat(initiateRenewalEntryActionTab.getAssetList().getAsset(CustomerMetaData
				.InitiateRenewalEntryActionTab.LEGACY_TIER)).isPresent();

		initiateRenewalEntryActionTab.submitTab();
		assertThat(initiateRenewalEntryActionTab.getAssetList().getWarning((CustomerMetaData
				.InitiateRenewalEntryActionTab.LEGACY_TIER))).hasValue(LEGACY_TIER_REQUIRED_MESSAGE);

		checkLegacyTierIsOutOfRangeErrorMessage("a");
		checkLegacyTierIsOutOfRangeErrorMessage("$");
		checkLegacyTierIsOutOfRangeErrorMessage("-1");
		checkLegacyTierIsOutOfRangeErrorMessage("51");

		initiateRenewalEntryActionTab.getAssetList().getAsset(CustomerMetaData
				.InitiateRenewalEntryActionTab.LEGACY_TIER).setValue("50");
		initiateRenewalEntryActionTab.submitTab();

		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get());
	}

	private void checkLegacyTierIsOutOfRangeErrorMessage(String value) {
		initiateRenewalEntryActionTab.getAssetList().getAsset(CustomerMetaData
				.InitiateRenewalEntryActionTab.LEGACY_TIER).setValue(value);
		initiateRenewalEntryActionTab.submitTab();
		assertThat(initiateRenewalEntryActionTab.getAssetList().getWarning((CustomerMetaData
				.InitiateRenewalEntryActionTab.LEGACY_TIER))).hasValue(LEGACY_TIER_IS_OUT_OF_RANGE_MESSAGE);
	}
}