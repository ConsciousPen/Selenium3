package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMidTermReinstatementPointLock extends AutoSSBaseTest {

	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private PurchaseTab purchaseTab = new PurchaseTab();

	/**
	 * @author Dominykas Razgunas
	 * @name Check if Mid Term Endorsement does not include reinstatement point change
	 * @scenario
	 * 1. Create customer
	 * 2. Create Auto SS Policy and Save Reinstatement Points Score value
	 * 3. Cancel Policy
	 * 4. Change Time TO today+2Months
	 * 5. Reinstate Policy
	 * 6. Endorse Policy
	 * 7. Navigate To P&C page
	 * 8. Calculate Premium
	 * 9. View Rating Details
	 * 10. Assert That the Reinstatement Points are the same
	 * @details
	 */
    @StateList(statesExcept = Constants.States.CA)
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9687")
	public void pas9687_MidTermReinstatementPointsLocked(@Optional("NJ") String state) {

		LocalDateTime reinstatementDate = TimeSetterUtil.getInstance().getCurrentTime().plusMonths(2);

		TestData testData = getPolicyTD();

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();

		//Calculate premium and open view rating details
		policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, true);
		PremiumAndCoveragesTab.RatingDetailsView.open();

		//Save current Policies reinstatement factors score
		String reinstatementHistory = PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(6).getCell("Score").getValue();

		// Issue Policy and cancel it
		PremiumAndCoveragesTab.RatingDetailsView.close();
		premiumAndCoveragesTab.submitTab();
		policy.getDefaultView().fillFromTo(testData, DriverActivityReportsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		//Change system date to get policy reinstated with lapse
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(reinstatementDate);
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		//Reinstate policy
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));

		//Endorse policy and check reinstatementHistory score
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		PremiumAndCoveragesTab.RatingDetailsView.open();

		//Check that the saved value is the same during mid term endorsement even after reinstatement was made. Change time back to current day.
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(6).getCell("Score")).hasValue(reinstatementHistory);
	}
}
