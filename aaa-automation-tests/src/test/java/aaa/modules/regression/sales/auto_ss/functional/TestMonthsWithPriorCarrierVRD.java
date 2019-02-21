package aaa.modules.regression.sales.auto_ss.functional;


import static toolkit.verification.CustomAssertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

@StateList(statesExcept = Constants.States.CA)
public class TestMonthsWithPriorCarrierVRD extends AutoSSBaseTest {

	/**
	 * @author Dominykas Razgunas
	 * @name Test Months With Prior Carrier VRD
	 * @scenario
	 * 1. Create Customer.
	 * 2. Initiate Auto SS Quote.
	 * 3. Order Prefill.
	 * 4. Navigate to General tab.
	 * 5. Override prior carrier information such that Days Lapsed is > 3 days for non-AAA carrier or is > 30 days for AAA carrier.
	 * 6. Navigate to P&C.
	 * 7. Calculate premium.
	 * 8. Review the VRD page.
	 * 9. Expected Result Moths with prior carrier  = 0.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23966")
	public void pas23966_testMonthsWithPriorCarrierVRD(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();

		TestData testData = getPolicyTD();
		policy.initiate();

		policy.getDefaultView().fillUpTo(testData, GeneralTab.class, true);
		new GeneralTab().getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(396).format(DateTimeUtils.MM_DD_YYYY));
		new GeneralTab().getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(31).format(DateTimeUtils.MM_DD_YYYY));
		new GeneralTab().submitTab();
		policy.getDefaultView().fillFromTo(testData, DriverTab.class, PremiumAndCoveragesTab.class, true);
		PremiumAndCoveragesTab.RatingDetailsView.open();

		assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, PolicyConstants.ViewRatingDetailsAuto.MONTHS_WITH_PRIOR_INSURANCE_CARRIER).getCell(2).getValue()).contains("0");

	}

}