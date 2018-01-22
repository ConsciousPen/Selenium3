package aaa.modules.regression.sales.home_ss.ho3.functional;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.Month;
import org.apache.commons.lang.math.IntRange;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name Test PA Revised Home Tier UI Change View Rating Detail
 * @scenario
 * 1. Create PA HO3 Policy
 * 2. Fill All required fields and Calculate Premium
 * 3. View Rating Details
 * 4. Check that Auto tier value is between 1 and 16
 * 5. Issue Policy
 * 6. Initiate renewal
 * 7. Calculate Premium
 * 8. Check that Auto tier value is between 1 and 16
 * @details
 **/
public class TestPAViewRatingDetailsAutoTier extends HomeSSHO3BaseTest {

	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private PurchaseTab purchaseTab = new PurchaseTab();

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PA Revised Home Tier  - UI Change : View Rating Details screen")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6676")
	public void pas6676_testPAViewRatingDetailsAutoTier(@Optional("PA") String state) {

		IntRange range = new IntRange(1, 16);

		TestData tdHome = getPolicyDefaultTD();

		// TODO This needs to be removed after 5/28/18 (new algo implementation)
		LocalDateTime algoEffective = LocalDateTime.of(2018, Month.JUNE, 1, 0, 0);
		if (TimeSetterUtil.getInstance().getCurrentTime().isBefore(algoEffective)) {
			TimeSetterUtil.getInstance().nextPhase(algoEffective);
		}


		mainApp().open();
		createCustomerIndividual();

		// Create Auto policy and adjust HomeTD to search for it
		PolicyType.AUTO_SS.get().createPolicy(getTdAuto());
		TestData tdOtherActive = getTestSpecificTD("OtherActiveAAAPolicies")
				.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH
						.getLabel(), HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesSearch.POLICY_NUMBER.getLabel()), PolicySummaryPage.getPolicyNumber());

		tdHome.adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()), tdOtherActive);

		// Initiate Home Policy and add Auto policy as a companion
		policy.initiate();
		policy.getDefaultView().fillUpTo(tdHome, PremiumsAndCoveragesQuoteTab.class, true);

		// Assert that the Auto Tier Rating present and is between 1-16
		PropertyQuoteTab.RatingDetailsView.open();
		assertThat(range.containsInteger(Integer.parseInt(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Auto Tier")))).isTrue();
		PropertyQuoteTab.RatingDetailsView.close();

		// Issue Policy and initiate renewal
		premiumsAndCoveragesQuoteTab.submitTab();
		policy.getDefaultView().fillFromTo(tdHome, MortgageesTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();
		policy.renew().start().submit();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.calculatePremium();

		// Assert that the Auto Tier Rating present and is between 1-16
		PropertyQuoteTab.RatingDetailsView.open();
		assertThat(range.containsInteger(Integer.parseInt(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Auto Tier")))).isTrue();
		PropertyQuoteTab.RatingDetailsView.close();

		mainApp().close();
	}



	private TestData getTdAuto() {
		return getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData");
	}
}