package aaa.modules.regression.document_fulfillment.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.*;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

public class PasDoc_OnlineBatch_Notice extends AutoSSBaseTest {

	/**
	 * <b> Test PasDoc Scenarios - Remove Cancel Notice </b>
	 * <p> Steps:
	 * <p> Create policy
	 * <p> Cancel Notice
	 * <p> Remove Cancel Notice
	 * <p> Verify document - AHCWXX(true), AH35XX(false)
	 *
	 * @param state
	 * @author Denis Semenov
	 */

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario52(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
		PolicySummaryPage.verifyCancelNoticeFlagPresent();

		policy.deleteCancelNotice().perform(new SimpleDataProvider());
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();

		PasDocImpl.verifyDocumentsGenerated(true, policyNumber, AHCWXX);
		PasDocImpl.verifyDocumentsGenerated(false, policyNumber, AH35XX);
	}

	/**
	 * <b> Test PasDoc Scenarios - Remove Cancel Notice </b>
	 * <p> Steps:
	 * <p> Create policy with Auto Pay
	 * <p> Cancel Notice (Reason - UW)
	 * <p> Remove Cancel Notice
	 * <p> Verify document - AHCWXX(true), AH35XX(true)
	 *
	 * @author Denis Semenov
	 * @param state
	 */

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario53(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Select payment plan = Monthly and activate AutoPay
		TestData tdAutoPay = getPolicyTD().adjust(getTestSpecificTD("TestData_EnabledAutoPay").resolveLinks());
		String policyNumber = createPolicy(tdAutoPay);

		PasDocImpl.verifyDocumentsGenerated(true, policyNumber, AH35XX);

		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData").adjust(TestData
						.makeKeyPath("CancelNoticeActionTab", "Cancellation Reason"),
				"Underwriting - Fraudulent Misrepresentation"));
		PolicySummaryPage.verifyCancelNoticeFlagPresent();
		policy.deleteCancelNotice().perform(new SimpleDataProvider());
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();

		PasDocImpl.verifyDocumentsGenerated(true, policyNumber, AHCWXX);
	}

	/**
	 * <b> Test PasDoc Scenarios - Remove Cancel Notice </b>
	 * <p> Steps:
	 * <p> Create policy
	 * <p> Shift time to R-35 and execute both renewalOfferGeneration
	 * <p> Shift time to R-20 and execute aaaRenewalNoticeBillAsyncJob
	 * <p> Shift time to R and execute aaaDataUpdateJob
	 * <p> Shift time to R+10 and execute aaaRenewalNoticeBillAsyncJob
	 * <p> Verify document - AH64XX
	 *
	 * @author Denis Semenov
	 * @param state
	 */

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario43(@Optional("") String state) {
		LocalDateTime renewalDate;

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		renewalDate = PolicySummaryPage.getExpirationDate();

		//R-35
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		//R-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);

		//R
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(renewalDate));
		JobUtils.executeJob(Jobs.aaaDataUpdateJob);

		//R+10
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getInsuranceRenewalReminderDate(renewalDate));
		JobUtils.executeJob(Jobs.aaaRenewalReminderGenerationAsyncJob);

		PasDocImpl.verifyDocumentsGenerated(policyNumber, AH64XX);
	}

}