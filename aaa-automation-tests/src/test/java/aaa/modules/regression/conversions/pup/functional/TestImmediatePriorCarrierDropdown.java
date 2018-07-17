package aaa.modules.regression.conversions.pup.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksPropertyTab;
import aaa.modules.regression.conversions.ConvPUPBaseTest;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.util.List;

public class TestImmediatePriorCarrierDropdown extends ConvPUPBaseTest {

	/**
	 * @author Rokas Lazdauskas
	 * @name Checking "Immediate Prior Carrier" dropdown values
	 * @scenario 1. Create Customer
	 * 2. Do 'Initiate Renewal Entry' action
	 * 3. On General Tab check "Immediate prior carrier" dropdown contains values
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-16330")
	public void testCheckImmediatePriorCarrierDropdown(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();

		//Create underlying policy
		TestData td = getConversionPolicyDefaultTD();

		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());

		//Initiate pup policy creation up until "Underlying Risks Property" tab
		policy.getDefaultView().fillUpTo(td, UnderlyingRisksPropertyTab.class, true);

		//Click 'Add' on 'AdditionalResidencies' section
		MultiInstanceAfterAssetList underlyingRisksPropertyTab = new UnderlyingRisksPropertyTab().getAdditionalResidenciesAssetList();
		underlyingRisksPropertyTab.getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.ADD).click();

		//Get values from 'Current carrier' dropdown
		List<String> currentCarrierDropdownValues = underlyingRisksPropertyTab.getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.CURRENT_CARRIER).getAllValues();

		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(currentCarrierDropdownValues).contains("CSAA Fire & Casualty Insurance Company");
			softly.assertThat(currentCarrierDropdownValues).contains("CSAA Affinity Insurance Company");
			softly.assertThat(currentCarrierDropdownValues).contains("CSAA General Insurance Company");
		});
	}
}
