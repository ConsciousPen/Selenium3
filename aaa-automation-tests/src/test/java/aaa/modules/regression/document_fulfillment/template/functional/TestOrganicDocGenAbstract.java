package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.common.enums.Constants;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.apache.commons.lang.StringUtils;
import toolkit.datax.TestData;

import java.time.LocalDateTime;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.EXPIRATION_NOTICE;
import static aaa.helpers.docgen.DocGenHelper.getPackageDataElemByName;
import static aaa.main.enums.DocGenEnum.Documents.AH64XX;
import static org.assertj.core.api.Assertions.assertThat;

public class TestOrganicDocGenAbstract extends PolicyBaseTest {
    /**
     * @name Creation organic policy for checking 'Expiration Notice' letter
     * @scenario
     * @details
     */
    public void pas29335_expirationNoticeFormGeneration(String state) throws NoSuchFieldException {
        expirationNoticeFormGenerationOrganic(AH64XX);
    }
    /**
     * @name Creation organic policy for checking 'Expiration Notice' letter
     * @scenario
     * @details
     */
    private void expirationNoticeFormGenerationOrganic(DocGenEnum.Documents form) throws NoSuchFieldException {
        //Create Policy with default test data
        TestData testData1 = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData");
        String policyNumber = openAppAndCreatePolicy(testData1);
        LocalDateTime policyExpirationDate = PolicySummaryPage.getEffectiveDate().plusYears(1);

        //Move time and generate the renewal image, generate renewal bill, move to expiration date and generate expiration notice - Run necessary batch jobs at each step
        expirationNoticeJobExecution(policyExpirationDate);

        //Wait for the specified document to be generated in the DB and grab it
        DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, EXPIRATION_NOTICE);
        String policyTransactionCode = getPackageTag(policyNumber, "PlcyTransCd", EXPIRATION_NOTICE);

        //Verify the correct transaction code is seen for the document
        String expectedPolicyTransCode = StringUtils.EMPTY;
        if (getPolicyType().equals(PolicyType.HOME_SS_HO4) || getPolicyType().equals(PolicyType.HOME_SS_HO3) || getPolicyType().equals(PolicyType.HOME_SS_HO6) || getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            if(getState().equals(Constants.States.AZ) || getState().equals(Constants.States.NY) || getState().equals(Constants.States.DC) || getState().equals(Constants.States.OH)){
                expectedPolicyTransCode = "CANB";
            }else{
                expectedPolicyTransCode = "STMT";
            }
        }
        assertThat(policyTransactionCode).as("PlcyTransCd is not correct for " + getState()).isEqualTo(expectedPolicyTransCode);
    }

    private void expirationNoticeJobExecution(LocalDateTime expirationDate){
        renewalBillJobExecution(expirationDate);
    }
    private void renewalBillJobExecution(LocalDateTime expirationDate){

        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(expirationDate));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(expirationDate));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(expirationDate));
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);
        JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(expirationDate));
        JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
        JobUtils.executeJob(Jobs.aaaRenewalReminderGenerationAsyncJob);
        JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
    }

    /**
     * Verify that tag value is present in the Package
     */
    private String getPackageTag(String policyNumber, String tag, AaaDocGenEntityQueries.EventNames name) throws NoSuchFieldException {
        return getPackageDataElemByName(policyNumber, "PolicyDetails", tag, name);
    }
}
