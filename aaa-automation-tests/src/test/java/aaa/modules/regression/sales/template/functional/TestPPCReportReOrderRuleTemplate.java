package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
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

		TestData endorsementTd1 = testDataManager.getDefault(TestDisableReorderReport.class).getTestData("TestData_Endorsement1");
		TestData endorsementTd2 = testDataManager.getDefault(TestDisableReorderReport.class).getTestData("TestData_Endorsement2");

		openAppAndCreatePolicy();

		policy.createEndorsement(endorsementTd1.adjust(getPolicyTD("Endorsement", "TestData")));
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());

		applicantEndorsementTabCA.getDwellingAddressAssetList().getAsset(HomeCaMetaData.ApplicantTab.DwellingAddress.COUNTY)
				.setValue("Los Angeles");

		policy.getDefaultView().fillFromTo(endorsementTd2, ApplicantTab.class, BindTab.class, true);
		bindTabCA.submitTab();

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime renewalTime = PolicySummaryPage.getExpirationDate();

		TimeSetterUtil.getInstance().nextPhase(renewalTime.minusDays(83));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		TimeSetterUtil.getInstance().nextPhase(renewalTime.minusDays(73));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		TimeSetterUtil.getInstance().nextPhase(renewalTime.minusDays(58));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		TimeSetterUtil.getInstance().nextPhase(renewalTime.minusDays(48));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		purchaseRenewal(renewalTime, policyNumber);
		PolicySummaryPage.buttonRenewals.click();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTabCA.calculatePremium();

		if (policyType.equals(PolicyType.HOME_CA_HO4)) {
			errorTabCA.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_CA7150360);
		} else {
			NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
			bindTabCA.submitTab();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		}
	}
}