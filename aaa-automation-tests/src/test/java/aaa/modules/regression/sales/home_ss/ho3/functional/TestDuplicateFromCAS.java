package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

/**
 * Functional Test Case
 * @author Tyrone Jemison (CIO)
 */
public class TestDuplicateFromCAS extends AutoSSBaseTest
{
    // Scenario Level Parameters
    TestDataHelper tdHelper = new TestDataHelper();
    String _createdPolicyByNumber = "";

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-19133 Internal claims are not being included in calculating years of claim free (YCF)")
    @TestInfo(component = ComponentConstant.Service.HOME_SS_HO3,  testCaseId = "PAS-19133")
    public void pas19133_testYearsClaimFreeCalculatedCorrectly(@Optional("") String state) {
        //Test Level Data
        TestData _td = getPolicyDefaultTD();
        tdHelper.adjustTD(_td, DriverTab.class, AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel(), "D55555248");
        //tdHelper.adjustTD(_td, DriverActivityReportsTab.class, AutoSSMetaData.DriverActivityReportsTab.ORDER_INTERNAL_CLAIMS_REPORT.getLabel(), "D55555248");

        commonTestFlow(_td, 63L);
    }

    private void commonTestFlow(TestData td, Long timePointAsRMinus) {


        // 1 - Create SS HO policy. // 2 - No Claims will be retrieved at NB.
        openAppAndCreatePolicy(td);

        // Get Timepoint Data
        LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime renewalImageGenerationDate = getTimePoints().getRenewImageGenerationDate(expirationDate);
        LocalDateTime membershipTP1 = expirationDate.minusDays(timePointAsRMinus);

        _createdPolicyByNumber = PolicySummaryPage.getPolicyNumber();
        mainApp().close();

        // 3 - Move to Renewal Timepoint 1 AKA STAGE3 (R-N).
        TimeSetterUtil.getInstance().nextPhase(renewalImageGenerationDate);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);
        TimeSetterUtil.getInstance().nextPhase(membershipTP1);
        // 4 - Use Batch Job to Order Claims Reports @ R-N. // 5 - Batch job picks up a new internal claim.
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);

        // 6 - Open policy. Go to VRD. Use algorithm to determine if YCF is calculated correctly and has correct value.
        mainApp().open();
        SearchPage.openPolicy(_createdPolicyByNumber);
    }
}
