package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.ho3.functional.TestDisableReorderReport;
import toolkit.datax.TestData;

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
			mainApp().close();

			// Create Renewal with Jobs and exact Time points
			createProposedRenewal(renewalTime);

			// Purchase Renewal and Navigate to Renewal Policy Summary
			payTotalAmtDue(renewalTime, policyNumber);
			PolicySummaryPage.buttonRenewals.click();
			// Endorse Renewal. Calculate Premium. Bind the endorsement
			policy.endorse().perform(initiateEndorsement);
			premiumsAndCoveragesQuoteTabCA.calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
			bindTabCA.submitTab();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		}
	}

	private void createProposedRenewal(LocalDateTime renewalTime){
		// Initiate Quote
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(renewalTime));
		JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		// Run Reports/Services
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(renewalTime));
		JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		// Rate Quote
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(renewalTime));
		JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		// Offer/Issue Quote
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalTime));
		JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
	}
}