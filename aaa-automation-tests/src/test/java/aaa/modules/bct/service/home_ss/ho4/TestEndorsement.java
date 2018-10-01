package aaa.modules.bct.service.home_ss.ho4;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.EndorsementTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestEndorsement extends EndorsementTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}

	private BindTab bindTab = new BindTab();
	private GeneralTab generalTab = new GeneralTab();

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementHomeSSHo4(String state, String policyNumber) {
		Dollar policyPremium = getPreEndorsementPremium(getPolicyType().get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(getPolicyType(), TESTDATA_INQUIRY_HOME_SS, generalTab, bindTab);
		assertThat(bindTab.btnPurchase.isPresent()).isTrue();
		bindTab.cancel();

		performNonBearingEndorsement(TESTDATA_NAME_ENDORSE_HOME_SS);
		PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();

		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
	}

	/**
	 * @author Deloite
	 * @name
	 * @scenario
	 * 1.Retrieve an HO4 policy and initiate an Endorsement.
	 * 2.Under property Info page validate the following fields:
	 * a.Coverage A should not be present
	 * b.Plumbing renovation(% completed and year of completion) ,Electrical Renovation , Roof Renovation,
	 * Heating/Cooling renovation,  hail resistance rating should not be present.
	 * c.Calculate the premium and bind the endorsement.
	 */

	@Parameters({"state"})
	@Test
	@StateList(states = {NJ})
	public void BCT_ONL_079_Endorsement(@Optional("") String state) {
		mainApp().open();
		IPolicy policy = PolicyType.HOME_SS_HO4.get();
		String policyNumber = getPoliciesByQuery("BCT_ONL_079_Endorsement", SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openPolicy(policyNumber);
		if (PolicySummaryPage.buttonPendedEndorsement.isEnabled()) {
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.deletePendedTransaction().perform(new SimpleDataProvider());
		}
		policy.endorse().perform(getTestSpecificTD("TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		AssetDescriptor[] labelsAreNotDisplayed = {
				HomeSSMetaData.PropertyInfoTab.COVERAGE_A_DWELLING_LIMIT,
				HomeSSMetaData.PropertyInfoTab.PLUMBING_RENOVATION,
				HomeSSMetaData.PropertyInfoTab.ELECTRICAL_RENOVATION,
				HomeSSMetaData.PropertyInfoTab.ROOF_RENOVATION,
				HomeSSMetaData.PropertyInfoTab.HEATING_COOLING_RENOVATION
		};
		Assertions.assertThat(policy.dataGather().getView().getTab(PropertyInfoTab.class).getAssetList().getAssets(labelsAreNotDisplayed)).extractingResultOf("isPresent").containsOnly(false);

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	@Override
	public void reorderReports() {
		new ReportsTab().reorderReports();
	}

	@Override
	public void performNonBearingEndorsement(String testDataName) {
		TestData testDataEnd = getTestSpecificTD(testDataName);

		policy.endorse().perform(testDataEnd);
		policy.dataGather().getView()
				.fillFromTo(testDataEnd, GeneralTab.class, ReportsTab.class, false);

		reorderReports();

		policy.dataGather().getView()
				.fillFromTo(testDataEnd, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, false);
	}
}
