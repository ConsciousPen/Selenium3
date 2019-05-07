package aaa.modules.regression.sales.home_ca;

import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO4BaseTest;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.modules.regression.sales.auto_ca.select.functional.TestEValueMembershipProcess.retrieveMembershipSummaryEndpointCheck;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class TestAbstractAHDRXXDiscountRemovalDocument extends PolicyBaseTest {

	protected void pas550_membershipEligibilityConfigurationTrueForCancelledMembership(TestData testData) {
		CustomSoftAssertions.assertSoftly(softly -> {
			retrieveMembershipSummaryEndpointCheck();
			mainApp().open();
			createCustomerIndividual();
			policy.createPolicy(testData);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			transactionHistoryRecordCountCheck(policyNumber, 2, "", softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			transactionHistoryRecordCountCheck(policyNumber, 3, "Membership Discount Removed", softly);
			checkDocumentContentAHDRXX(policyNumber, true, true, softly);

		});
	}

	private void jobsNBplus15plus30runNoChecks() {
		jobsNBplus15plus30runNoChecks(DateTimeUtils.getCurrentDateTime().plusDays(15));
	}

	private void jobsNBplus15plus30runNoChecks(LocalDateTime dateToShiftTo) {
		TimeSetterUtil.getInstance().nextPhase(dateToShiftTo);
		JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
		JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
		JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
		JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
		JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
		JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
	}

	private void transactionHistoryRecordCountCheck(String policyNumber, int rowCount, String value, ETCSCoreSoftAssertions softly) {
		PolicySummaryPage.buttonTransactionHistory.click();
		softly.assertThat(PolicySummaryPage.tableTransactionHistory).hasRows(rowCount);

		String valueShort = "";
		if (!StringUtils.isEmpty(value)) {
			valueShort = value.substring(0, 20);
			assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").getHintValue()).contains(value);
		}
		softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").getValue()).contains(valueShort);

		String transactionHistoryQuery = "select * from(\n"
				+ "select pt.TXREASONTEXT\n"
				+ "from PolicyTransaction pt\n"
				+ "where POLICYID in \n"
				+ "        (select id from POLICYSUMMARY \n"
				+ "        where POLICYNUMBER = '%s')\n"
				+ "    order by pt.TXDATE desc)\n"
				+ "    where rownum=1";
		softly.assertThat(DBService.get().getValue(String.format(transactionHistoryQuery, policyNumber)).orElse(StringUtils.EMPTY)).isEqualTo(value);
	}

	private void checkDocumentContentAHDRXX(String policyNumber, boolean isGenerated, boolean isMembershipDataPresent, ETCSCoreSoftAssertions softly) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDRXX", "ENDORSEMENT_ISSUE");

		if (isGenerated) {
			if (isMembershipDataPresent) {
				//PAS-1549, PAS-2872} Start
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("AAAMemDiscAmt", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()).isEqualTo("5.0%");
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()).isEqualTo("Y");
			}
		}
	}

	private boolean ahdrxxDiscountTagPresentInTheForm(String query, String discountTag) {
		return DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().toString().contains(discountTag);
	}
}