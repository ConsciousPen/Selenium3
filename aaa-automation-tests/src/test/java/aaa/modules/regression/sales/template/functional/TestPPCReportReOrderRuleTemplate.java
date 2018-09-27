package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantEndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.ho3.functional.TestDisableReorderReport;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import java.time.LocalDateTime;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestPPCReportReOrderRuleTemplate extends PolicyBaseTest {

	private ApplicantEndorsementTab applicantEndorsementTabCA = new ApplicantEndorsementTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTabCA = new PremiumsAndCoveragesQuoteTab();
	private BindTab bindTabCA = new BindTab();
	private ErrorTab errorTabCA = new ErrorTab();

	protected void testPPCReportReOrderRuleCA(PolicyType policyType) {

		// Testdata for Policy Endorsement Part1 (Change Address)
		TestData endorsementTd1 = testDataManager.getDefault(TestDisableReorderReport.class).getTestData("TestData_Endorsement1");
		// Testdata for Policy Endorsement Part2
		TestData endorsementTd2 = testDataManager.getDefault(TestDisableReorderReport.class).getTestData("TestData_Endorsement2");
		// Testdata for Endorsement initiation
		TestData initiateEndorsement = getPolicyTD("Endorsement", "TestData");

		openAppAndCreatePolicy();

		// Endorse Policy up to Reports Tab
		policy.createEndorsement(endorsementTd1.adjust(initiateEndorsement));
		// Navigate to Applicant Tab and Add Los Angeles
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
		applicantEndorsementTabCA.getDwellingAddressAssetList().getAsset(HomeCaMetaData.ApplicantTab.DwellingAddress.COUNTY)
				.setValue("Los Angeles");

		// If HO4 Calculate Premium Throws Error (Expected Behavior)
		if (policyType.equals(PolicyType.HOME_CA_HO4)) {
			policy.getDefaultView().fillFromTo(endorsementTd2, ApplicantTab.class, PremiumsAndCoveragesQuoteTab.class, true);
			errorTabCA.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_CA7150360);
		} else {
			// Bind Endorsement for HO6,O3,DP3
			policy.getDefaultView().fillFromTo(endorsementTd2, ApplicantTab.class, BindTab.class, true);
			bindTabCA.submitTab();
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			LocalDateTime renewalTime = PolicySummaryPage.getExpirationDate();

			// Create Renewal with Jobs and exact Time points
			createProposedRenewal(renewalTime);

			// Purchase Renewal and Navigate to Renewal Policy Summary
			purchaseRenewal(renewalTime, policyNumber);
			PolicySummaryPage.buttonRenewals.click();
			// Endorse Renewal. Calculate Premium. Bind the endorsement
			policy.endorse().perform(initiateEndorsement);
			NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
			premiumsAndCoveragesQuoteTabCA.calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
			bindTabCA.submitTab();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		}
	}

	private void createProposedRenewal(LocalDateTime renewalTime){
		// CA Renewal Offer generation Time Point
		TimeSetterUtil.getInstance().nextPhase(renewalTime.minusDays(83));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		// CA Reports Re order Time Point
		TimeSetterUtil.getInstance().nextPhase(renewalTime.minusDays(73));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		// CA Rate Quote Time Point
		TimeSetterUtil.getInstance().nextPhase(renewalTime.minusDays(58));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		// CA Renewal Issue time point
		TimeSetterUtil.getInstance().nextPhase(renewalTime.minusDays(48));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}
}