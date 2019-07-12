package aaa.modules.regression.sales.home_ss.ho3.functional;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSDP3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

public class TestYearsClaimFreeCalculation extends HomeSSDP3BaseTest
{
    // Scenario Level Parameters
    TestDataHelper tdHelper = new TestDataHelper();
    String _createdPolicyByNumber = "";

    /**
     * 1. Create SS HO/DP policy. <br>
     * 2. At new business no internal claim is returned. <br>
     * 3. Move to renewal time frame. <br>
     * 4. Batch order internal claims report at renewal. <br>
     * 5. Reports return at least one chargeable Internal Claim from CAS. <br>
     * 6. Review YCF value (Years of Claim Free) in the VRD screen. <br>
     *     @author - Tyrone Jemison
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-19133 Internal claims are not being included in calculating years of claim free (YCF)")
    @TestInfo(component = ComponentConstant.Service.HOME_SS_DP3,  testCaseId = "PAS-19133")
    public void pas19133_testYearsClaimFreeCalculatedCorrectly(@Optional("") String state) {
        //Test Level Data
        TestData _td = getPolicyDefaultTD();
        tdHelper.adjustTD(_td, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel(), "Rick");
        tdHelper.adjustTD(_td, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel(), "Sanchez");

        commonTestFlow(_td, 63L);
    }

    /**
     *
     * @param td TestData for use when creating the policy.
     * @param timePointAsRMinus Long that represents R-(n) with 'n' being the Long.
     */
    private void commonTestFlow(TestData td, Long timePointAsRMinus) {

        // 1 - Create SS HO policy. // 2 - No Claims will be retrieved at NB.
        openAppAndCreatePolicy(td);

        // Get Timepoint Data
        LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime renewalImageGenerationDate = getTimePoints().getRenewImageGenerationDate(expirationDate);
        LocalDateTime membershipTP1 = expirationDate.minusDays(timePointAsRMinus);
        LocalDateTime r40 = expirationDate.minusDays(40l);
        _createdPolicyByNumber = PolicySummaryPage.getPolicyNumber();
        mainApp().close();

        // 3 - Move to Renewal Timepoint 1 AKA STAGE3 (R-N).
        TimeSetterUtil.getInstance().nextPhase(renewalImageGenerationDate);
		JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
		JobUtils.executeJob(BatchJob.policyAutomatedRenewalAsyncTaskGenerationJob);
        TimeSetterUtil.getInstance().nextPhase(membershipTP1);

        // 4 - Use Batch Job to Order Claims Reports @ R-N. // 5 - Batch job picks up a new internal claim.
		JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
		JobUtils.executeJob(BatchJob.renewalClaimOrderAsyncJob);
        //HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_MEMBERSHIP_SUMMARY_BATCH);
		JobUtils.executeJob(BatchJob.renewalClaimReceiveAsyncJob);

        TimeSetterUtil.getInstance().nextPhase(renewalImageGenerationDate);

        // 6 - Open policy. Go to VRD. Use algorithm to determine if YCF is calculated correctly and has correct value.
        mainApp().open();
        SearchPage.openPolicy(_createdPolicyByNumber);
        policy.renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab pnc = new PremiumsAndCoveragesQuoteTab();
        pnc.calculatePremium();
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(r40);

        mainApp().open();
        SearchPage.openPolicy(_createdPolicyByNumber);
        policy.renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        pnc.calculatePremium();
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();

        CustomAssertions.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Number of years claims free")).isEqualToIgnoringCase("5");
    }
}
