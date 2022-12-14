package aaa.modules.regression.conversions.home_ss.ho3.functional;

import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(statesExcept = {Constants.States.CA})
public class TestImmediatePriorCarrierDropdown extends HomeSSHO3BaseTest {

	/**
	 * @author Rokas Lazdauskas
	 * @name Checking "Immediate Prior Carrier" dropdown values
	 * @scenario 1. Create Customer
	 * 2. Do 'Initiate Renewal Entry' action
	 * 3. On General Tab check "Immediate prior carrier" dropdown contains values
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-16330")
	public void testCheckImmediatePriorCarrierDropdown(@Optional("") String state) {

		Tab generalTab = new GeneralTab();

		mainApp().open();

		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());

		getPolicyType().get().getDefaultView().fillUpTo(getConversionPolicyDefaultTD(),GeneralTab.class, false);

		List<String> immediatePriorCarrierDropdownValues = generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER).getAllValues();

		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(immediatePriorCarrierDropdownValues).contains("CSAA Fire & Casualty Insurance Company");
			softly.assertThat(immediatePriorCarrierDropdownValues).contains("CSAA Affinity Insurance Company");
			softly.assertThat(immediatePriorCarrierDropdownValues).contains("CSAA General Insurance Company");
		});

	}
}
