package aaa.modules.regression.sales.auto_ca.choice;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author mlaptsionak
 * @name Test Special Hazard Surcharge
 * @scernario
 * 1. Start CA Choice auto quote
 * 2. 1 vehicle with Symbol being C (2005 Nissan Titan VIN=1N6AA07A25N519526)
 * 3. go to Premium and Calculations tab
 * 4. Check Discounts and Surcharges section
 * 5. Open VRD and check Quote Info section (CA Choice vehicles with BI/PD symbol C,D,E will have the "Special Hazard Surcharge")
 */

public class TestSpecialHazardSurcharge extends AutoCaChoiceBaseTest {

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
	public void testSpecialHazardSurcharge(@Optional("CA") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData testData = getPolicyTD("DataGather", "TestData").adjust(getTestSpecificTD("TestData_Adjustment").resolveLinks());
		PolicyType.AUTO_CA_CHOICE.get().initiate();
		policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class);
		CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(2).getValue().toString()).contains("Special Hazard Surcharge (2005, NISSAN, TITAN)");
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "Special Hazard Surcharge").getCell(2).getValue()).isEqualTo("Yes");
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "BI Symbol").getCell(2).getValue()).isEqualTo("C");
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "PD Symbol").getCell(2).getValue()).isEqualTo("C");
	}

}