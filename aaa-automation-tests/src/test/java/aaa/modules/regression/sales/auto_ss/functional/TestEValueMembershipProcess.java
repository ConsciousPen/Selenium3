/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.helpers.rest.wiremock.dto.PaperlessPreferencesTemplateData.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.ws.rs.core.Response;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.ipb.etcsa.utils.batchjob.JobGroup;
import com.exigen.ipb.etcsa.utils.batchjob.SoapJobActions;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.admin.pages.general.GeneralAsyncTasksPage;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.config.CsaaTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.rest.wiremock.HelperWireMockStub;
import aaa.helpers.rest.wiremock.dto.PaperlessPreferencesTemplateData;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ss.actiontabs.CancelNoticeActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.ViewRatingDetailsPage;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.TestEValueMembershipProcessPreConditions;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.utils.StateList;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.waiters.Waiters;

public class TestEValueMembershipProcess extends AutoSSBaseTest implements TestEValueMembershipProcessPreConditions {

	private static final String E_VALUE_DISCOUNT = "eValue Discount"; //PAS-440 - rumors have it, that discount might be renamed
	private static final String APP_HOST = PropertyProvider.getProperty(CsaaTestProperties.APP_HOST);
	private static final String MESSAGE_INFO_1 = "This customer is not eligible for eValue discount due to one or more of the following reasons:";
	private static final String MESSAGE_BULLET_8 = "Does not have an active AAA membership";
	private static final String MESSAGE_INFO_4 = "eValue Discount Requirements:";
	private static final String MESSAGE_JEOPARDY = "Discount in Jeopardy email sent";
	private static List<HelperWireMockStub> stubList = new LinkedList<>();
	private Random random = new Random();
	private GeneralTab generalTab = new GeneralTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private SSHController sshControllerRemote = new SSHController(PropertyProvider.getProperty(CsaaTestProperties.APP_HOST), PropertyProvider.getProperty(CsaaTestProperties.SSH_USER), PropertyProvider.getProperty(CsaaTestProperties.SSH_PASSWORD));

	public static void retrieveMembershipSummaryEndpointCheck() {
		assertThat(DBService.get().getValue(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK).orElse(""))
				.as("retrieveMembershipSummary doesn't use stub endpoint. Please run retrieveMembershipSummaryStubEndpointUpdate").containsIgnoringCase(APP_HOST);
	}

	@Test(description = "Renewal job adding", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public void precondJobAdding() {
		adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());

		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_BATCH_MARKER_JOB);

		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_AUTOMATED_PROCESSING_INITIATION_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AUTOMATED_PROCESSING_RATING_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AUTOMATED_PROCESSING_RUN_REPORTS_SERVICES_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AUTOMATED_PROCESSING_ISSUING_OR_PROPOSING_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AUTOMATED_PROCESSING_STRATEGY_STATUS_UPDATE_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AUTOMATED_PROCESSING_BYPASSING_AND_ERRORS_REPORT_GENERATION_JOB);

		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.POLICY_AUTOMATED_RENEWAL_ASYNC_TASK_GENERATION_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_MEMBERSHIP_RENEWAL_BATCH_RECEIVE_ASYNC_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_MEMBERSHIP_RENEWAL_BATCH_ORDER_ASYNC_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.RENEWAL_IMAGE_RATING_ASYNC_TASK_JOB);
	}

	//@Test
	private void preconditionMembershipEligibilityCheck(String membershipEligibilitySwitch) {
		if (!DBService.get().getValue(MEMBERSHIP_ELIGIBILITY_CHECK_FOR_VA_EXISTS).isPresent()) {
			DBService.get().executeUpdate(EVALUE_MEMBERSHIP_ELIGIBILITY_CONFIG_INSERT);
			printToLog("INSERT MEMBERSHIP ELIGIBILITY CONFIGURATION IS REQUIRED");
		}
		if (DBService.get().getValue(MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_CHECK).get().equals(membershipEligibilitySwitch)) {
			printToLog("MEMBERSHIP ELIGIBILITY CONFIGURATION IS CORRECT, NO UPDATES REQUIRED");
		} else {
			DBService.get().executeUpdate(String.format(MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_UPDATE, membershipEligibilitySwitch));
			printToLog("UPDATE OF MEMBERSHIP ELIGIBILITY CONFIGURATION to {} was executed here", membershipEligibilitySwitch);
		}
	}

	private void settingMembershipEligibilityConfig(String membershipDiscountEligibilitySwitch) {
		mainApp().open();
		if ("FALSE".equals(membershipDiscountEligibilitySwitch)) {
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1));
		}
		preconditionMembershipEligibilityCheck(membershipDiscountEligibilitySwitch);
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
	}

	private void postConditionMembershipEligibilityCheck() {
		String eligibilitySwitch = "TRUE";
		DBService.get().executeUpdate(String.format(MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_UPDATE, eligibilitySwitch));
		printToLog("UPDATE OF MEMBERSHIP ELIGIBILITY CONFIGURATION To TRUE WAS EXECUTED HERE");
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
	}

	public void preconditionsClearFolders() {
		printToLog("Clear membership folders started");

		try {
			sshControllerRemote.deleteFile(new File(PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER) + "PAS_B_EXGPAS_PASHUB_4004_D/archive" + "/*.*"));
			sshControllerRemote.deleteFile(new File(PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER) + "PAS_B_PASHUB_EXGPAS_4004_D/archive" + "/*.*"));
		} catch (JSchException | SftpException e) {
			throw new IstfException("Precondition failed: /n", e);
		}

		if (RemoteHelper.get().isPathExist(PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER) + "PAS_B_EXGPAS_PASHUB_4004_D/outbound")) {
			RemoteHelper.get().clearFolder(PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER) + "PAS_B_EXGPAS_PASHUB_4004_D/outbound");
		}
		printToLog("Clear membership folders completed");
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount and Membership Discount are NOT removed when Membership is required for eValue and membership status = Active.
	 * @scenario
	 * 0. Check no email record present in admin log (no "eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check if discount in Jeopardy email wasn't send.
	 * 2. Check Membership discount is not removed on NB+30
	 * 3. Check eValue discount is not removed on NB+30
	 * 4. Check AHDRXX is not produced on NB+30
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-327", "PAS-1928", "PAS-12822", "PAS-312"})
	public void pas3697_membershipEligibilityConfigurationTrueForActiveMembership(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);
		testEValueDiscount.pas111_clearCache();

		String policyNumber = membershipEligibilityPolicyCreation("Active", true);

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "ACTIVE", softly);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, false, softly);
			//End PAS-12822
			membershipLogicActivitiesAndNotesCheck(true, "ACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "ACTIVE", softly);
			membershipLogicActivitiesAndNotesCheck(false, "already activated by previous job", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);
			checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false, softly);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount and Membership Discount are NOT removed when Membership is required for eValue and membership status = Active.
	 * @scenario
	 * 0. Check no email record present in admin log (no "eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check if discount in Jeopardy email wasn't send.
	 * 2. Check Membership discount is not removed on NB+30
	 * 3. Check eValue discount is not removed on NB+30
	 * 4. Check AHDRXX is not produced on NB+30
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-327", "PAS-1928", "PAS-12822", "PAS-331"})
	public void pas331_membershipEligibilityConfigurationTrueForErredMembership(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Error", true);

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "PENDING", softly);
			//Start PAS-12822
			//BUG PAS-13891 Jeopardy Email is generated for Policy which has Membership order response = Error
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, false, softly);
			//End PAS-12822
			membershipLogicActivitiesAndNotesCheck(false, "", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			//BUG PAS-15479 EValue remains Pending after NB+30 if Membership Status = Erred
			eValueDiscountStatusCheck(policyNumber, "ACTIVE", softly);
			membershipLogicActivitiesAndNotesCheck(true, "ACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);
			checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false, softly);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount and Membership Discount are removed when Membership is required for eValue and membership status = Not Active.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check if discount in Jeopardy email was send.
	 * 2. Check Membership discount is removed on NB+30
	 * 3. Check eValue discount is removed on NB+30
	 * 4. Check AHDRXX is produced on NB+30 and contains both eValue and Membership discount info
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-327", "PAS-1928", "PAS-12822", "PAS-312"})
	public void pas3697_membershipEligibilityConfigurationTrueForNotActiveMembership(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Non-Active", true);

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "PENDING", softly);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, true, softly);
			//End PAS-12822
			membershipLogicActivitiesAndNotesCheck(true, "Membership information was updated for the policy based on best membership logic", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "INACTIVE", softly);
			membershipLogicActivitiesAndNotesCheck(true, "INACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 3, "eValue and Membership Discounts Removed - Membership", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(false, false, membershipDiscountEligibilitySwitch, softly);
			checkDocumentContentAHDRXX(policyNumber, true, true, true, false, false, softly);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount and Membership Discount NOT removed when Membership is NOT required for eValue and membership status = Active.
	 * @scenario
	 * 0. Check no email record present in admin log (no "eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check if discount in Jeopardy email wasn't send.
	 * 2. Check Membership discount is not removed on NB+30
	 * 3. Check eValue discount is not removed on NB+30
	 * 4. Check AHDRXX is not produced on NB+30
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-327", "PAS-12822"})
	public void pas3697_membershipEligibilityConfigurationFalseForActiveMembership(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "FALSE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);
		testEValueDiscount.pas111_clearCache();

		String policyNumber = membershipEligibilityPolicyCreation("Active", true, false);

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "ACTIVE", softly);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, false, softly);
			//End PAS-12822
			membershipLogicActivitiesAndNotesCheck(true, "ACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "ACTIVE", softly);
			membershipLogicActivitiesAndNotesCheck(false, "already processed by previous job", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);
			checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false, softly);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount not removed and Membership Discount is removed when Membership is NOT required for eValue and membership status = Not Active.
	 * @scenario
	 * 0. Check email record is NOT present in admin log (no "eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check if discount in Jeopardy email wasn't send.
	 * 2. Check Membership discount is removed on NB+30
	 * 3. Check eValue discount NOT removed on NB+30
	 * 4. Check AHDRXX is produced on NB+30 and contains only Membership discount info
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-327", "PAS-1928", "PAS-12822"})
	public void pas3697_membershipEligibilityConfigurationFalseForNotActiveMembership(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "FALSE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Non-Active", true, false);

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "ACTIVE", softly);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, false, softly);
			//End PAS-12822
			membershipLogicActivitiesAndNotesCheck(true, "ACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "ACTIVE", softly);
			membershipLogicActivitiesAndNotesCheck(true, "ACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 3, "Membership Discount Removed", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(false, true, membershipDiscountEligibilitySwitch, softly);
			//BUG PAS-7149 AHDRXX is generated when MembershipEligibility=FALSE and eValue discount is not removed
			checkDocumentContentAHDRXX(policyNumber, true, true, false, false, false, softly);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount and Membership Discount removed when Membership is required for eValue and membership status = Cancelled. Renewal
	 * @scenario
	 * 0. upload configuration to require Membership for eValue
	 * 1. change time to R-96, generate Renewal Image
	 * 2. change time to R-63, run Order Membership Report Job, run Membership stub service, run Receive Membership Report Job
	 * 3. change time to R-48, run Order Membership Report Job, run Membership stub service, run Receive Membership Report Job
	 * 4. Check eValue discount is set to No in P&C tab of renewal
	 * 5. Check AHDEXX is produced in the DB and contains both Membership and eValue discounts information
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-324", "PAS-1928", "PAS-319"})
	public void pas3697_membershipEligibilityConfigurationTrueForCancelledMembershipRenewal(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		String membershipStatusActive = "Cancelled";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		CustomSoftAssertions.assertSoftly(softly -> {
			renewalMembershipProcessCheck(membershipDiscountEligibilitySwitch, membershipStatusActive, false, softly);
			postConditionMembershipEligibilityCheck();
		});
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-10229", "PAS-832", "PAS-324"})
	public void pas10229_membershipEligibilityConfigurationTrueForActiveMembershipActiveEValueRenewal(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		String membershipStatus = "Active";
		boolean eValueSet = true;

		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);
		String policyNumber = membershipEligibilityPolicyCreation(membershipStatus, eValueSet);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate); //-96
		LocalDateTime renewReportOrderingDate = getTimePoints().getRenewReportsDate(policyExpirationDate); //-63
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		LocalDateTime renewBillGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

		executeMembershipJobsRminus63Rminus48(renewReportOrderingDate, true);
		CustomSoftAssertions.assertSoftly(softly -> {
			ahdexxGeneratedCheck(false, policyNumber, 0, softly);

			executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
			renewalTransactionHistoryCheck(policyNumber, true, true, "inquiry", softly);
			ahdexxGeneratedCheck(false, policyNumber, 0, softly);
			renewalTransactionHistoryCheck(policyNumber, true, true, "dataGather", softly);
			eValueDiscountStatusCheck(policyNumber, "PENDING", softly);
		});

		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		TimeSetterUtil.getInstance().nextPhase(renewBillGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar renewalMinDue = BillingSummaryPage.getMinimumDue();
		BillingAccount billingAccount = new BillingAccount();
		TestData tdBilling = testDataManager.billingAccount;
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), renewalMinDue);
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.EVALUE_STATUS)).hasValue("Active");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-10229", "PAS-832", "PAS-324"})
	public void pas10229_membershipEligibilityConfigurationTrueForActiveMembershipInActiveEValueRenewal(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		String membershipStatus = "Active";
		boolean eValueSet = false;

		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);
		String policyNumber = membershipEligibilityPolicyCreation(membershipStatus, eValueSet);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate); //-96
		LocalDateTime renewReportOrderingDate = getTimePoints().getRenewReportsDate(policyExpirationDate); //-63

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

		executeMembershipJobsRminus63Rminus48(renewReportOrderingDate, true);
		CustomSoftAssertions.assertSoftly(softly -> {
			ahdexxGeneratedCheck(false, policyNumber, 0, softly);

			executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
			renewalTransactionHistoryCheck(policyNumber, true, false, "inquiry", softly);
			ahdexxGeneratedCheck(false, policyNumber, 0, softly);
			renewalTransactionHistoryCheck(policyNumber, true, false, "dataGather", softly);
			eValueDiscountStatusCheck(policyNumber, "NOTENROLLED", softly);
		});
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-10229", "PAS-832", "PAS-324", "PAS-1928"})
	public void pas10229_membershipEligConfigurationTrueForInActiveMembershipActiveEValueRenewalMinus48(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		String membershipStatus = "Cancelled";
		boolean eValueSet = true;

		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);
		String policyNumber = membershipEligibilityPolicyCreation(membershipStatus, eValueSet);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		cancelReinstateToAvoidNbPlus15Plus30Jobs(policyNumber);

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate); //-96

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

		cancelReinstateToAvoidRminusJobs(policyNumber, policyExpirationDate);
		CustomSoftAssertions.assertSoftly(softly -> {
			ahdexxGeneratedCheck(false, policyNumber, 0, softly);

			executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
			renewalTransactionHistoryCheck(policyNumber, true, true, "inquiry", softly);
			ahdexxGeneratedCheck(true, policyNumber, 1, softly);
			checkDocumentContentAHDEXX(policyNumber, true, true, true, false, false, softly);
			renewalTransactionHistoryCheck(policyNumber, false, false, "dataGather", softly);
			eValueDiscountStatusCheck(policyNumber, "INACTIVE", softly);
		});
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-10229", "PAS-832", "PAS-324", "PAS-1928"})
	public void pas10229_membershipEligConfigurationTrueForInActiveMembershipNotActiveEValueRenewalMinus48(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		String membershipStatus = "Cancelled";
		boolean eValueSet = false;

		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);
		String policyNumber = membershipEligibilityPolicyCreation(membershipStatus, eValueSet);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		cancelReinstateToAvoidNbPlus15Plus30Jobs(policyNumber);

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate); //-96

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

		cancelReinstateToAvoidRminusJobs(policyNumber, policyExpirationDate);

		CustomSoftAssertions.assertSoftly(softly -> {
			ahdexxGeneratedCheck(false, policyNumber, 0, softly);

			executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
			renewalTransactionHistoryCheck(policyNumber, true, false, "inquiry", softly);
			ahdexxGeneratedCheck(true, policyNumber, 1, softly);
			checkDocumentContentAHDEXX(policyNumber, true, true, false, false, false, softly);
			renewalTransactionHistoryCheck(policyNumber, false, false, "dataGather", softly);
			eValueDiscountStatusCheck(policyNumber, "NOTENROLLED", softly);
		});
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.OK})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-15287"})
	public void pas15287_eValueNotEligibleActiveMembershipNoEValueRenewalMinus48(@Optional("OK") String state) {
		preconditionsClearFolders();
		String membershipDiscountEligibilitySwitch = "TRUE";
		String membershipStatus = "No";
		boolean eValueSet = false;

		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);
		String policyNumber = membershipEligibilityPolicyCreation(membershipStatus, eValueSet);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		cancelReinstateToAvoidNbPlus15Plus30Jobs(policyNumber);

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate); //-96
		LocalDateTime renewReportOrderingDate = getTimePoints().getRenewReportsDate(policyExpirationDate); //-63

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(35));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		CustomSoftAssertions.assertSoftly(softly -> {
			/*executeMembershipJobsRminus63Rminus48(renewReportOrderingDate, true);
			renewalTransactionHistoryCheck(policyNumber, true, false, "inquiry");
			ahdexxGeneratedCheck(true, policyNumber, 1);

			executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
			renewalTransactionHistoryCheck(policyNumber, true, false, "inquiry");
			ahdexxGeneratedCheck(true, policyNumber, 1);
			checkDocumentContentAHDEXX(policyNumber, true, true, false, false, false);
			renewalTransactionHistoryCheck(policyNumber, false, false, "dataGather");*/
			eValueDiscountStatusCheck(policyNumber, "NOTENROLLED", softly);
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
		});
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-10229", "PAS-832", "PAS-324", "PAS-1928"})
	public void pas10229_membershipEligConfigurationTrueForInActiveMembershipActiveEValueRenewalMinus63(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		String membershipStatus = "Cancelled";
		boolean eValueSet = true;

		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);
		String policyNumber = membershipEligibilityPolicyCreation(membershipStatus, eValueSet);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		cancelReinstateToAvoidNbPlus15Plus30Jobs(policyNumber);

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate); //-96
		LocalDateTime renewReportOrderingDate = getTimePoints().getRenewReportsDate(policyExpirationDate); //-63

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

		executeMembershipJobsRminus63Rminus48(renewReportOrderingDate, true);
		CustomSoftAssertions.assertSoftly(softly -> {
			renewalTransactionHistoryCheck(policyNumber, true, true, "inquiry", softly);
			ahdexxGeneratedCheck(true, policyNumber, 1, softly);
			checkDocumentContentAHDEXX(policyNumber, true, true, true, false, false, softly);

			executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
			renewalTransactionHistoryCheck(policyNumber, true, true, "inquiry", softly);
			//BUG PAS-10735 AHDEXX is generated on R-48, though it was generated on R-63
			ahdexxGeneratedCheck(true, policyNumber, 1, softly);
			checkDocumentContentAHDEXX(policyNumber, true, true, true, false, false, softly);
			renewalTransactionHistoryCheck(policyNumber, false, false, "dataGather", softly);
			eValueDiscountStatusCheck(policyNumber, "INACTIVE", softly);
		});
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-10229", "PAS-832", "PAS-324"})
	public void pas10229_membershipEligConfigurationTrueForInActiveMembershipNotActiveEValueRenewalMinus63(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		String membershipStatus = "Cancelled";
		boolean eValueSet = false;

		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);
		String policyNumber = membershipEligibilityPolicyCreation(membershipStatus, eValueSet);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		cancelReinstateToAvoidNbPlus15Plus30Jobs(policyNumber);

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate); //-96
		LocalDateTime renewReportOrderingDate = getTimePoints().getRenewReportsDate(policyExpirationDate); //-63

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

		executeMembershipJobsRminus63Rminus48(renewReportOrderingDate, true);
		CustomSoftAssertions.assertSoftly(softly -> {
			renewalTransactionHistoryCheck(policyNumber, true, false, "inquiry", softly);
			ahdexxGeneratedCheck(true, policyNumber, 1, softly);

			executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
			renewalTransactionHistoryCheck(policyNumber, true, false, "inquiry", softly);
			ahdexxGeneratedCheck(true, policyNumber, 1, softly);
			checkDocumentContentAHDEXX(policyNumber, true, true, false, false, false, softly);
			renewalTransactionHistoryCheck(policyNumber, false, false, "dataGather", softly);
			eValueDiscountStatusCheck(policyNumber, "NOTENROLLED", softly);
		});
	}

	private void cancelReinstateToAvoidNbPlus15Plus30Jobs(String policyNumber) {
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(2));
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
	}

	private void renewalTransactionHistoryCheck(String policyNumber, boolean membershipDiscountPresent, boolean eValueDiscountPresent, String mode, ETCSCoreSoftAssertions softly) {
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		if ("inquiry".equals(mode)) {
			policy.policyInquiry().start();
		} else {
			policy.dataGather().start();
		}
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		if (membershipDiscountPresent) {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount")).isTrue();
		} else {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount")).isFalse();
		}
		if (eValueDiscountPresent) {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("eValue Discount")).isTrue();
		} else {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("eValue Discount")).isFalse();
		}
	}

	private void executeMembershipJobsRminus63Rminus48(LocalDateTime renewReportOrderingDate) {
		executeMembershipJobsRminus63Rminus48(renewReportOrderingDate, false);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount and Membership Discount removed when Membership is Not required for eValue and membership status = Cancelled. Renewal
	 * @scenario
	 * 0. upload configuration to require Membership for eValue
	 * 1. change time to R-96, generate Renewal Image
	 * 2. change time to R-63, run Order Membership Report Job, run Membership stub service, run Receive Membership Report Job
	 * 3. change time to R-48, run Order Membership Report Job, run Membership stub service, run Receive Membership Report Job
	 * 4. Check eValue discount is set to Yes in P&C tab of renewal
	 * 5. Check AHDEXX is produced in the DB and contains only Membership discount info and no eValue discounts information
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-324", "PAS-1928", "PAS-319"})
	public void pas3697_membershipEligibilityConfigurationFalseForCancelledMembershipRenewal(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "FALSE";
		String membershipStatusActive = "Cancelled";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		CustomSoftAssertions.assertSoftly(softly -> {
			renewalMembershipProcessCheck(membershipDiscountEligibilitySwitch, membershipStatusActive, true, softly);
			postConditionMembershipEligibilityCheck();
		});
	}

	/**
	 * The test is created to check the endpoints and correctness of the methods
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8275"})
	public void pas111_paperlessMockTest(@Optional("VA") String state) {
		String policyNumber = "VASS952918556";

		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);
		//Always need to delete the added request ot stub
		deleteSinglePaperlessPreferenceRequest(stub);

		HelperWireMockStub stub2 = createPaperlessPreferencesRequestId(policyNumber, OPT_IN_PENDING);

		//PolicySummaryPage.getPolicyNumber();

		deleteSinglePaperlessPreferenceRequest(stub2);
		HelperWireMockStub stub3 = createPaperlessPreferencesRequestId(policyNumber, OPT_IN_PENDING);
		deleteSinglePaperlessPreferenceRequest(stub3);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount is removed, Membership Discount is not when Membership is required for eValue
	 * and membership status = Active, but PaperlessPreferences = Pending.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check if discount in Jeopardy email was send.
	 * 2. Check Membership discount is not removed on NB+30
	 * 3. Check eValue discount removed on NB+30
	 * 4. Check AHDRXX is produced on NB+30:
	 * Membership discount tag - not present in the document
	 * Evalue discount tag - present
	 * Paperless info tag - present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-329", "PAS-12822"})
	public void pas3697_membershipEligConfTrueForActiveMembershipPendingPaperless(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_IN_PENDING);

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "PENDING", softly);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, true, softly);
			//End PAS-12822
			membershipLogicActivitiesAndNotesCheck(false, "no record created", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "INACTIVE", softly);
			membershipLogicActivitiesAndNotesCheck(true, "INACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "eValue Discount Removed - Paperless", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, false, membershipDiscountEligibilitySwitch, false, softly);
			checkDocumentContentAHDRXX(policyNumber, true, false, true, true, false, softly);

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount is removed, Membership Discount is removed when Membership is required for eValue
	 * and membership status = Not Active, but PaperlessPreferences = Pending.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check if discount in Jeopardy email was send.
	 * 2. Check Membership discount is removed on NB+30
	 * 3. Check eValue discount removed on NB+30
	 * 4. Check AHDRXX is produced on NB+30:
	 * Membership discount tag - present
	 * Evalue discount tag - present
	 * Paperless info tag - present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-329", "PAS-1928", "PAS-12822"})
	public void pas3697_membershipEligConfTrueForNotActiveMembershipPendingPaperless(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Non-Active", true);
		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_IN_PENDING);

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, true, softly);
			//End PAS-12822
			eValueDiscountStatusCheck(policyNumber, "PENDING", softly);
			membershipLogicActivitiesAndNotesCheck(false, "no record created", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "INACTIVE", softly);
			membershipLogicActivitiesAndNotesCheck(true, "INACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 3, "eValue and Membership Discounts Removed - Membership, Paperless", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(false, false, membershipDiscountEligibilitySwitch, false, softly);
			checkDocumentContentAHDRXX(policyNumber, true, true, true, true, false, softly);

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount is removed but Membership Discount is not removed when Membership is not required for eValue
	 * and membership status = Active, but PaperlessPreferences = Pending.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check if discount in Jeopardy email was send.
	 * 2. Check Membership discount is removed on NB+30
	 * 3. Check eValue discount removed on NB+30
	 * 4. Check AHDRXX is produced on NB+30:
	 * Membership discount tag - not present in the document
	 * Evalue discount tag - present
	 * Paperless info tag - present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-329", "PAS-12822"})
	public void pas3697_membershipEligConfFalseForActiveMembershipPendingPaperless(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "FALSE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Active", true, false);
		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_IN_PENDING);

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "PENDING", softly);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, true, softly);
			//End PAS-12822
			membershipLogicActivitiesAndNotesCheck(false, "no record created", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "INACTIVE", softly);
			membershipLogicActivitiesAndNotesCheck(true, "INACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "eValue Discount Removed - Paperless", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, false, membershipDiscountEligibilitySwitch, softly);
			checkDocumentContentAHDRXX(policyNumber, true, false, true, true, false, softly);

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount is removed Membership Discount is removed when Membership is not required for eValue
	 * and membership status = Not-Active, but PaperlessPreferences = Pending.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check if discount in Jeopardy email was send.
	 * 2. Check Membership discount is removed on NB+30
	 * 3. Check eValue discount removed on NB+30
	 * 4. Check AHDRXX is produced on NB+30:
	 * Membership discount tag - not present in the document
	 * Evalue discount tag - present
	 * Paperless info tag - present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-329", "PAS-12822"})
	public void pas3697_membershipEligConfFalseForNotActiveMembershipPendingPaperless(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "FALSE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Non-Active", true, false);
		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_IN_PENDING);

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "PENDING", softly);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, true, softly);
			//End PAS-12822
			membershipLogicActivitiesAndNotesCheck(false, "no record created", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "INACTIVE", softly);
			membershipLogicActivitiesAndNotesCheck(true, "INACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 3, "Membership Discount Removed; eValue Discount Removed - Paperless", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(false, false, membershipDiscountEligibilitySwitch, softly);
			checkDocumentContentAHDRXX(policyNumber, true, true, true, true, false, softly);

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount when PaperlessPreferences changes.
	 * @scenario
	 * 1. Create a policy with eValue discount with Paperless Preferences = Pending
	 * 2. Run NB+15 jobs, in admin log there is ("eValue Discount Pending Notification Url:") on NB+15
	 * 3. Check if discount in Jeopardy email was send.
	 * 4. Before NB + 30 change Paperless Preferences = Yes
	 * 5. Run NB+30 jobs
	 * 6. Check eValue discount is not removed
	 * 7. Check No AHDRXX is produced
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-329", "PAS-12822"})
	public void pas3697_membershipEligConfTrueForActiveMembershipNoPaperlessChangedToYesBeforeNB30(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_IN_PENDING);

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "PENDING", softly);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, true, softly);
			//End PAS-12822
			membershipLogicActivitiesAndNotesCheck(false, "no record created", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);
			deleteSinglePaperlessPreferenceRequest(stub);

			HelperWireMockStub stub2 = createPaperlessPreferencesRequestId(policyNumber, OPT_IN);
			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "ACTIVE", softly);
			membershipLogicActivitiesAndNotesCheck(true, "ACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, false, softly);
			checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false, softly);
			deleteSinglePaperlessPreferenceRequest(stub2);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount when PaperlessPreferences changes.
	 * @scenario
	 * 1. Create a policy with eValue discount with Paperless Preferences = Pending
	 * 2. Before NB + 15 change Paperless Preferences = Yes
	 * 3. Run NB+15 jobs, in admin log there is no("eValue Discount Pending Notification Url:") on NB+15
	 * 4. Check if discount in Jeopardy email wasn't send.
	 * 4. Run NB+30 jobs
	 * 5. Check eValue discount is not removed
	 * 6. Check No AHDRXX is produced
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3697", "PAS-329", "PAS-12822"})
	public void pas3697_membershipEligConfTrueForActiveMembershipNoPaperlessChangedToYesBeforeNB15(@Optional("VA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_IN_PENDING);
		deleteSinglePaperlessPreferenceRequest(stub);

		CustomSoftAssertions.assertSoftly(softly -> {
			HelperWireMockStub stub2 = createPaperlessPreferencesRequestId(policyNumber, OPT_IN);
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "ACTIVE", softly);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, false, softly);
			//End PAS-12822
			membershipLogicActivitiesAndNotesCheck(true, "ACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "ACTIVE", softly);
			membershipLogicActivitiesAndNotesCheck(false, "no record created", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, false, softly);
			checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false, softly);
			deleteSinglePaperlessPreferenceRequest(stub2);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount is removed on NB+30 when PaperlessPreferences is set to Pending
	 * @scenario
	 * 0. Create a policy with eValue = Yes and Paperless Preferences = Pending (DC state)
	 * 1. Change date to NB+15 and run NB+15jobs
	 * 2. Check if discount in Jeopardy email wasn't send.
	 * 3. Check admin log contains email that eValue will be removed (manual action) ( "eValue Discount Pending Notification Url:")
	 * 4. Change date to NB+30 and run NB+30jobs (same as NV+15jobs)
	 * 5. Check eValue Discount = No in P&C tab
	 * 6. Check AHDRXX is produced and contains eValue information
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-5837", "PAS-3697", "PAS-327", "PAS-329", "PAS-12822"})
	public void pas5837_eValueDiscountRemovedIfPaperlessPreferenceIsPending(@Optional("DC") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "FALSE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Active", true, false);

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "PENDING", softly);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, true, softly);
			//End PAS-12822

			membershipLogicActivitiesAndNotesCheck(false, "no changes", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			eValueDiscountStatusCheck(policyNumber, "INACTIVE", softly);
			membershipLogicActivitiesAndNotesCheck(true, "INACTIVE", softly);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "eValue Discount Removed - Paperless", softly);
			//BUG Membership Discount infor printed in the doc
			latestTransactionMembershipAndEvalueDiscountsCheck(true, false, membershipDiscountEligibilitySwitch, softly);
			//BUG PAS-7265 Paperless preference reason isn't displayed in AHDRXX document in case Paperless is Pending at NB+30
			checkDocumentContentAHDRXX(policyNumber, true, false, true, true, false, softly);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Cancelled policy is not picked up by NB+15, NB+30 jobs
	 * @scenario
	 * 0. upload configuration to require Membership for eValue
	 * 1. Create a policy with eValue with Active mebership
	 * 2. Cancel policy
	 * 3. Run NB+15
	 * 4. Run NB+30
	 * 5. Check eValue discount is still in place in the cancelled policy transaction
	 * 6. Check AHDRXX is not generated
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11740")
	public void pas13528_membershipEligConfTrueForActiveMembershipCancelledPolicy(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);
		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "Insured's Request - Rates too high", softly);
			lastTransactionHistoryEValueDiscountCheck(true, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "Insured's Request - Rates too high", softly);
			lastTransactionHistoryEValueDiscountCheck(true, softly);
			checkDocumentContentAHDRXX(policyNumber, false, true, false, false, false, softly);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Update Policy Preferences Service and AHDRXX check
	 * @scenario
	 * 1. Create a VA E Value Policy
	 * 2. Set Paperless Preferences to No via stub
	 * 3. Execute UpdatePolicyPreferences Service
	 * 4. Check Transaction history and that eValue was removed
	 * 5. Run NB+15 jobs
	 * 6. Check if discount in Jeopardy email wasn't send.
	 * 7. Run NB+30 jobs
	 * 8. Check AHDRXX is not generated
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-1451", "PAS-335"})
	public void pas1451_eValueRemovedByServiceNoAHDRXX(@Optional("VA") String state) {
		testEValueDiscount.eValueQuoteCreation(true);

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.submitTab();
		premiumAndCoveragesTab.saveAndExit();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();
		mainApp().close();

		CustomSoftAssertions.assertSoftly(softly -> {
			HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);

			HelperCommon.updatePolicyPreferences(policyNumber, Response.Status.OK.getStatusCode());

			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "eValue Removed - Paperless Preferences Removed - External", softly);
			lastTransactionHistoryEValueDiscountCheck(false, softly);

			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, false);
			//End PAS-12822
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "eValue Removed - Paperless Preferences Removed - External", softly);
			lastTransactionHistoryEValueDiscountCheck(false, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "eValue Removed - Paperless Preferences Removed - External", softly);
			lastTransactionHistoryEValueDiscountCheck(false, softly);
			checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false, softly);

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Update Policy Preferences Service for Cancelled policy and AHDRXX check
	 * @scenario
	 * 1. Create a VA E Value Policy
	 * 2. Set Paperless Preferences to No via stub
	 * 3. Execute UpdatePolicyPreferences Service can't update cancelled policy
	 * 4. Check Transaction history no new transactions appeared
	 * 5. Run NB+15 jobs
	 * 6. Check if discount in Jeopardy email wasn't send.
	 * 7. Run NB+30 jobs
	 * 8. Check AHDRXX is not generated
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-13528"})
	public void pas13528_eValueNotRemovedByServiceNoAHDRXXforCancelledPolicy(@Optional("VA") String state) {
		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		mainApp().close();

		CustomSoftAssertions.assertSoftly(softly -> {
			HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);

			HelperCommon.updatePolicyPreferences(policyNumber, 422);

			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "Insured's Request - Rates too high", softly);
			lastTransactionHistoryEValueDiscountCheck(true, softly);

			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			//Start PAS-12822
			NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(MESSAGE_JEOPARDY, false, softly);
			//End PAS-12822
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "Insured's Request - Rates too high", softly);
			lastTransactionHistoryEValueDiscountCheck(true, softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "Insured's Request - Rates too high", softly);
			lastTransactionHistoryEValueDiscountCheck(true, softly);
			checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false, softly);

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Update Policy Preferences Service Reinstated Policy
	 * @scenario
	 * 1. Create a VA E Value Policy
	 * 2. Set Paperless Preferences to No via stub
	 * 2.1. Cancel Policy
	 * 2.2. Reinstate Policy
	 * 3. Execute UpdatePolicyPreferences Service can update Reinstated policy
	 * 4. Check Transaction history related to eValue removal appears
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-13528"})
	public void pas13528_eValueRemovedByServiceForReinstatedPolicy(@Optional("VA") String state) {
		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);

		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
		mainApp().close();

		CustomSoftAssertions.assertSoftly(softly -> {
			HelperCommon.updatePolicyPreferences(policyNumber, Response.Status.OK.getStatusCode());

			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 4, "eValue Removed - Paperless Preferences Removed - External", softly);
			lastTransactionHistoryEValueDiscountCheck(false, softly);

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Update Policy Preferences Service Reinstated Policy
	 * @scenario
	 * 1. Create a VA E Value Policy
	 * 2. Set Paperless Preferences to No via stub
	 * 2.1. Cancel Policy
	 * 2.2. Reinstate Policy effective in future
	 * 3. Execute UpdatePolicyPreferences Service can update Reinstated policy
	 * 4. Check Transaction history related to eValue removal appears
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-13528"})
	public void pas13528_eValueRemovedByServiceForFutureDatedReinstatedPolicy(@Optional("VA") String state) {
		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);

		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData_Plus10Days"));
		mainApp().close();

		CustomSoftAssertions.assertSoftly(softly -> {
			HelperCommon.updatePolicyPreferences(policyNumber, Response.Status.OK.getStatusCode());

			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 3, "", softly);
			lastTransactionHistoryEValueDiscountCheck(true, softly);

			//there is no eValue removal transaction. Instead a task for OOSE is create.
			lastTransactionHistoryExit();
			NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
			softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", "Task Created").getCell("Date/Time"))
				.valueContains(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Update Policy Preferences Service Future Dated Cancelled Policy
	 * @scenario
	 * 1. Create a VA E Value Policy
	 * 2. Set Paperless Preferences to No via stub
	 * 2.1. Cancel Policy with effective date 10 days in future
	 * 3. Execute UpdatePolicyPreferences Service can update Pending Cancel policy
	 * 4. Check Transaction history related to eValue removal appears
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-13528"})
	public void pas13528_eValueRemovedByServiceForFutureDatedCancelledPolicy(@Optional("VA") String state) {
		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);

		policy.cancel().perform(getPolicyTD("Cancellation", "TestData_Plus10Days"));
		mainApp().close();

		CustomSoftAssertions.assertSoftly(softly -> {
			HelperCommon.updatePolicyPreferences(policyNumber, Response.Status.OK.getStatusCode());

			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			//BUG PAS-13884 When running PaperlessPreferences update for policy with Future Dated Cancellation, no eValue removal happens and no task is created
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "", softly);
			lastTransactionHistoryEValueDiscountCheck(true, softly);

			//there is no eValue removal transaction. Instead a task for OOSE is create.
			lastTransactionHistoryExit();
			NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
			softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", "Task Created").getCell("Date/Time"))
					.valueContains(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Update Policy Preferences Service for Pending Policy
	 * @scenario
	 * 1. Create a VA E Value Policy
	 * 2. Set Paperless Preferences to No via stub
	 * 3. Execute UpdatePolicyPreferences Service
	 * 4. Check Transaction history and that eValue was removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-13528"})
	public void pas13528_eValueRemovedByServicePendingPolicy(@Optional("VA") String state) {
		testEValueDiscount.eValueQuoteCreation(true);

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().plusDays(10).format(DateTimeUtils.MM_DD_YYYY));
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().plusDays(10).format(DateTimeUtils.MM_DD_YYYY));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.submitTab();
		premiumAndCoveragesTab.saveAndExit();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();
		mainApp().close();

		CustomSoftAssertions.assertSoftly(softly -> {
			HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);

			HelperCommon.updatePolicyPreferences(policyNumber, Response.Status.OK.getStatusCode());

			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "eValue Removed - Paperless Preferences Removed - External", softly);
			lastTransactionHistoryEValueDiscountCheck(false, softly);

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}


	/**
	 * @author Oleg Stasyuk
	 * @name Update Policy Preferences Service for Cancelled policy and AHDRXX check
	 * @scenario
	 * 1. Create a VA E Value Policy
	 * 2. Set Paperless Preferences to No via stub
	 * 3. Execute UpdatePolicyPreferences Service can't update cancelled policy
	 * 4. Check Transaction history and that eValue was removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-13528"})
	public void pas13528_eValueRemovedByServiceCancPendingPolicy(@Optional("VA") String state) {
		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
		CancelNoticeActionTab cancelNoticeActionTab = new CancelNoticeActionTab();
		if(cancelNoticeActionTab.getAssetList().getAsset(AutoSSMetaData.CancelNoticeActionTab.CANCELLATION_DUE_TO_CONSUMER_REPORT_INFORMATION).isPresent() && cancelNoticeActionTab.getAssetList().getAsset(AutoSSMetaData.CancelNoticeActionTab.CANCELLATION_DUE_TO_CONSUMER_REPORT_INFORMATION).isVisible()) {
			cancelNoticeActionTab.getAssetList().getAsset(AutoSSMetaData.CancelNoticeActionTab.CANCELLATION_DUE_TO_CONSUMER_REPORT_INFORMATION).setValue("No");
			CancelNoticeActionTab.buttonOk.click();
		}
		mainApp().close();

		CustomSoftAssertions.assertSoftly(softly -> {
			HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);

			HelperCommon.updatePolicyPreferences(policyNumber, Response.Status.OK.getStatusCode());

			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 3, "eValue Removed - Paperless Preferences Removed - External", softly);
			lastTransactionHistoryEValueDiscountCheck(false, softly);

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Update Policy Preferences Service for Expired Policy
	 * @scenario
	 * 1. Create a VA E Value Policy
	 * 1.1. Renew the policy
	 * 2. Set Paperless Preferences to No via stub
	 * 3. Execute UpdatePolicyPreferences Service can't update cancelled policy
	 * 4. Check Transaction history and that eValue was not removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-13528"})
	public void pas13528_eValueNotRemovedByServiceExpiredPolicy(@Optional("VA") String state) {
		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);
		HelperCommon.updatePolicyPreferences(policyNumber, 422);

		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);

		CustomSoftAssertions.assertSoftly(softly -> {
			//BUG PAS-13857 PaperlessPreferences service updates Expired policies
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			lastTransactionHistoryEValueDiscountCheck(true, softly);

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Update Policy Preferences Service for Expired Policy
	 * @scenario
	 * 1. Create a VA E Value Policy
	 * 1.1. Renew the policy
	 * 2. Set Paperless Preferences to No via stub
	 * 3. Execute UpdatePolicyPreferences Service can't update cancelled policy
	 * 4. Check Transaction history and that eValue was not removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-13528"})
	public void pas13528_eValueNotRemovedByServiceLapsedPolicy(@Optional("VA") String state) {
		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusDays(15));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);
		HelperCommon.updatePolicyPreferences(policyNumber, 422);

		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);

		CustomSoftAssertions.assertSoftly(softly -> {
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			lastTransactionHistoryEValueDiscountCheck(true, softly);

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Update Policy Preferences Service for Policy with Proposed renewal
	 * @scenario
	 * 1. Create a VA E Value Policy
	 * 1.1. Renew the policy
	 * 2. Set Paperless Preferences to No via stub
	 * 3. Execute UpdatePolicyPreferences Service can update Policy with Proposed renewal
	 * 4. Check Transaction history and that eValue was removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-13528"})
	public void pas13528_eValueRemovedByServiceProposedRenewal(@Optional("VA") String state) {
		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);
		HelperCommon.updatePolicyPreferences(policyNumber, Response.Status.OK.getStatusCode());

		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		CustomSoftAssertions.assertSoftly(softly -> {
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "eValue Removed - Paperless Preferences Removed - External", softly);
			lastTransactionHistoryEValueDiscountCheck(false, softly);

			lastTransactionHistoryExit();
			renewalTransactionEValueDiscountCheck(false, softly);

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Update Policy Preferences Service for Policy with InForce renewal
	 * @scenario
	 * 1. Create a VA E Value Policy
	 * 1.1. Renew the policy
	 * 2. Set Paperless Preferences to No via stub
	 * 3. Execute UpdatePolicyPreferences Service can update Policy with Proposed renewal
	 * 4. Check Transaction history and that eValue was removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-13528"})
	public void pas13528_eValueRemovedByServiceInForceRenewal(@Optional("VA") String state) {
		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar totalDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue);

		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);

		//BUG PAS-13952 Can't issue an endorsement to current term, when renewal was proposed and paid
		HelperCommon.updatePolicyPreferences(policyNumber, Response.Status.OK.getStatusCode());

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		CustomSoftAssertions.assertSoftly(softly -> {
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "", softly);
			lastTransactionHistoryEValueDiscountCheck(true, softly);

			//there is no eValue removal transaction. Instead a task for OOSE is create.
			lastTransactionHistoryExit();
			NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
			softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", "Task Created").getCell("Date/Time"))
					.valueContains(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));

			deleteSinglePaperlessPreferenceRequest(stub);
		});
	}

	private void renewalMembershipProcessCheck(String membershipEligibilitySwitch, String membershipStatus, boolean eValueApplied, ETCSCoreSoftAssertions softly) {
		membershipEligibilityPolicyCreation(membershipStatus, true, false);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		membershipEligibilityEndorsementCreation(membershipStatus);
		cancelReinstateToAvoidNbPlus15Plus30Jobs(policyNumber);

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate); //-96
		LocalDateTime renewReportOrderingDate = getTimePoints().getRenewReportsDate(policyExpirationDate); //-63

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

		executeMembershipJobsRminus63Rminus48(renewReportOrderingDate);
		executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
		//String policyNumber = "VASS952918864";
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		//PAS-319 start
		softly.assertThat(generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE)).hasValue("eValue Renewal");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		if(eValueApplied){
			softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("Yes");
			eValueDiscountStatusCheck(policyNumber, "ACTIVE", softly);
		}else{
			softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("No");
			eValueDiscountStatusCheck(policyNumber, "INACTIVE", softly);
		}
		//PAS-319 end

		ahdexxContentCheck(membershipEligibilitySwitch, policyNumber, softly);
	}

	private String membershipEligibilityPolicyCreation(String membershipStatus, boolean eValueSet) {
		return membershipEligibilityPolicyCreation(membershipStatus, eValueSet, false);
	}

	private String membershipEligibilityPolicyCreation(String membershipStatus, boolean eValueSet, boolean defaultEvalueQuote) {
		testEValueDiscount.eValueQuoteCreation(defaultEvalueQuote);
		policy.dataGather().start();
		setMembershipAndRate(membershipStatus, eValueSet);
		return testEValueDiscount.simplifiedQuoteIssue();
	}

	void membershipEligibilityEndorsementCreation(String membershipStatus) {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		setMembershipAndRate(membershipStatus, true);
		testEValueDiscount.simplifiedPendedEndorsementIssue();
	}

	private void setMembershipAndRate(String membershipStatus, boolean eValueSet) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		if ("Active".equals(membershipStatus)) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("5251111111111118");
		} else if ("Pending".equals(membershipStatus)) {
			//BUG PAS-11150 Membership isn't removed on NB+30 when Membership status is Pending
/*			if (generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).isPresent() && generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).isVisible()) {
				generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("");
			}*/
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
		} else if ("Cancelled".equals(membershipStatus)) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("3111111111111121");
		} else if ("Error".equals(membershipStatus)) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("5610591081018250");
		} else {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");//Membership Pending
			List<String> inactiveMembershipNumberList = new ArrayList<>();
			//TODO check othe membership number for better randomization
			//inactiveMembershipNumberList.add("4333333333333457");//Expired        - bad
			//inactiveMembershipNumberList.add("9920702826992041");//Inactive        - error
			inactiveMembershipNumberList.add("4343433333333335");//FutureDated
			//inactiveMembershipNumberList.add("4222222222222123");//No Hit          - error
			//inactiveMembershipNumberList.add("8800400000000002");//Lapsed           - Active
			//inactiveMembershipNumberList.add("4290061311384005");//Pending         //TODO updated to Active based on some logic
			//inactiveMembershipNumberList.add("4333333333333338");//Expired           - error
			String randomInactiveMembershipNumber = inactiveMembershipNumberList.get(random.nextInt(inactiveMembershipNumberList.size()));
			printToLog("Value used " + randomInactiveMembershipNumber);
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER)
					.setValue(randomInactiveMembershipNumber);
		}

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.ORDER_REPORT).click();
		if (ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).isPresent()) {
			if ("".equals(ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.MEMBER_SINCE_DATE.getLabel()).getValue())) {

				ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
						.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ACTION.getLabel()).controls.links.get(1).click();

				ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT)
						.getAsset(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ADD_MEMBER_SINCE_DIALOG.getLabel(), AssetList.class)
						.getAsset(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.MEMBER_SINCE.getLabel(), TextBox.class).setValue("11/14/2016");

				Page.dialogConfirmation.confirm();
			}
			printToLog("Membership number used: " + ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.MEMBERSHIP_NO.getLabel()).getValue());
			printToLog("Member Since Date used or returned: " + ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.MEMBER_SINCE_DATE.getLabel()).getValue());
			printToLog("Membership Status returned: " + ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.STATUS.getLabel()).getValue());
		}
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		if (eValueSet) {
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		} else {
			if (premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).isPresent()) {
				premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
			}
		}
		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
	}

	//@Test
	static void jobsNBplus15plus30runNoChecks() {
		jobsNBplus15plus30runNoChecks(DateTimeUtils.getCurrentDateTime().plusDays(15));
	}

	static void jobsNBplus15plus30runNoChecks(LocalDateTime dateToShiftTo) {
		TimeSetterUtil.getInstance().nextPhase(dateToShiftTo);
		//the job might not exist in AWS
		if(new SoapJobActions().isJobExist(JobGroup.fromSingleJob(Jobs.membershipValidationJob.getJobName()))){
			JobUtils.executeJob(Jobs.membershipValidationJob);
		} else {
		//JobUtils.executeJob(Jobs.aaaBatchMarkerJob); //OSI: job is not required
		JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
		JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
		JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
		JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
		JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
		//BUG INC0635200 PAS-ASM: multiple VDMs: We have a failing job on the VDMs. - the next line is closed as not a defect and this one was opened
		//BUG PAS-6162 automatedProcessingBypassingAndErrorsReportGenerationJob is failing with Error, failed to retrieve 'placeholder' Report Entity
		JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
		}
	}


	private void executeMembershipJobsRminus63Rminus48(LocalDateTime renewReportOrderingDate, boolean clearExgPasArchiveFolder) {
		//TODO commented out to avoid hanging of SSH session in VDMs
		/*		if (clearExgPasArchiveFolder) {
			try {
				sshControllerRemote.deleteFile(new File(PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER) + "PAS_B_EXGPAS_PASHUB_4004_D/archive" + "/*.*"));
				sshControllerRemote.deleteFile(new File(PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER) + "PAS_B_PASHUB_EXGPAS_4004_D/archive" + "/*.*"));
			} catch (JSchException | SftpException e) {
				e.printStackTrace();
			}
		}*/
		TimeSetterUtil.getInstance().nextPhase(renewReportOrderingDate);
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
		Waiters.SLEEP(5000).go();
		HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_MEMBERSHIP_SUMMARY_BATCH);
		Waiters.SLEEP(5000).go();
		/*try {
			sshControllerRemote.deleteFile(new File(PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER) + "PAS_B_EXGPAS_PASHUB_4004_D/outbound" + "/*.*"));
		} catch (JSchException | SftpException e) {
			e.printStackTrace();
		}*/
		Waiters.SLEEP(5000).go();
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchReceiveAsyncJob);
	}

	private void eValueDiscountStatusCheck(String policyNumber, String status, ETCSCoreSoftAssertions softly) {
		String getEvalueStatusSQL = "select evalueStatus from (\n"
				+ "  select ps.policynumber, emd.evaluestatus\n"
				+ "  from PolicySummary ps, AAAEMemberDetailsEntity emd\n"
				+ "  where ps.ememberdetail_id = emd.id\n"
				+ "  and ps.policynumber = '%s'\n"
				+ "  order by emd.id desc)\n"
				+ "where rownum = 1";
		assertThat(DBService.get().getValue(String.format(getEvalueStatusSQL, policyNumber)).orElse("")).isEqualTo(status);
	}

	private void membershipLogicActivitiesAndNotesCheck(boolean presence, String status, ETCSCoreSoftAssertions softly) {
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		if (presence) {
			softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", status).getCell("Date/Time"))
					.valueContains(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
		} else {
			if (NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", "for the policy based on Preferences and Membership logic").isPresent()) {
				int rowNum = NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", "for the policy based on Preferences and Membership logic").getIndex();
				softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(rowNum).getCell("Date/Time").getValue())
						.doesNotContain(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
			}
		}
	}

	private void lastTransactionHistoryExit() {
		if (Tab.buttonCancel.isPresent()) {
			premiumAndCoveragesTab.cancel();
			if (Tab.buttonCancel.isPresent()) {
				Tab.buttonCancel.click();
			}
		}
	}

	private void lastTransactionHistoryOpen() {
		if (PolicySummaryPage.buttonTransactionHistory.isPresent()) {
			PolicySummaryPage.buttonTransactionHistory.click();
		}
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).controls.links.get(1).click();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
	}

	private void lastTransactionHistoryMembershipDiscountCheck(boolean membershipDiscount, ETCSCoreSoftAssertions softly) {
		lastTransactionHistoryOpen();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		if (membershipDiscount) {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges).valueContains("Membership Discount");
		} else {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue()).doesNotContain("Membership Discount");
		}
	}

	private void lastTransactionHistoryEValueDiscountCheck(boolean eValueDiscount, ETCSCoreSoftAssertions softly) {
		PolicySummaryPage.lastTransactionHistoryOpen();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		if (eValueDiscount) {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges).valueContains("eValue Discount");
		} else {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue()).doesNotContain("eValue Discount");
		}
	}

	private void renewalTransactionEValueDiscountCheck(boolean eValueDiscount, ETCSCoreSoftAssertions softly) {
		PolicySummaryPage.buttonRenewals.click();
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		if (eValueDiscount) {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges).valueContains("eValue Discount");
		} else {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue()).doesNotContain("eValue Discount");
		}
	}

	private void latestTransactionMembershipAndEvalueDiscountsCheck(boolean membershipDiscountPresent, boolean eValueDiscountPresent, String membershipEligibilitySwitch, ETCSCoreSoftAssertions softly) {
		latestTransactionMembershipAndEvalueDiscountsCheck(membershipDiscountPresent, eValueDiscountPresent, membershipEligibilitySwitch, true, softly);
	}

	private void latestTransactionMembershipAndEvalueDiscountsCheck(boolean membershipDiscountPresent, boolean eValueDiscountPresent, String membershipEligibilitySwitch, boolean checkMessages, ETCSCoreSoftAssertions softly) {
		lastTransactionHistoryOpen();
		if (!membershipDiscountPresent) {
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
			softly.assertThat(generalTab.getAAAProductOwnedInquiryAssetList().getStaticElement(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER)).hasValue("No");
		}
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		if (membershipDiscountPresent) {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount")).isTrue();
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "AAA Membership Discount").getCell(2)).hasValue("Yes");
			ViewRatingDetailsPage.buttonRatingDetailsOk.click();
		} else {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount")).isFalse();
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "AAA Membership Discount").getCell(3)).hasValue("None");
			ViewRatingDetailsPage.buttonRatingDetailsOk.click();
		}
		lastTransactionHistoryExit();
		if (eValueDiscountPresent) {
			lastTransactionHistoryOpen();
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges).valueContains(E_VALUE_DISCOUNT);
			softly.assertThat(premiumAndCoveragesTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("Yes");
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			if (PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, E_VALUE_DISCOUNT).isPresent()) {
				softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, E_VALUE_DISCOUNT).getCell(5)).hasValue("Yes");
			} else {
				softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT).getCell(4)).hasValue("Yes");
			}
			ViewRatingDetailsPage.buttonRatingDetailsOk.click();
		} else {
			lastTransactionHistoryOpen();
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT)).isFalse();
			softly.assertThat(premiumAndCoveragesTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("No");
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, "eValue Discount").getCell(6)).hasValue("None");
			ViewRatingDetailsPage.buttonRatingDetailsOk.click();
			if (checkMessages) {
				if ("TRUE".equals(membershipEligibilitySwitch)) {
					softly.assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(1).getCell(1)).hasValue(MESSAGE_INFO_1);
					softly.assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1)).valueContains(MESSAGE_BULLET_8);
				}
			}
			lastTransactionHistoryExit();
		}
	}

	private boolean ahdrxxDiscountTagPresentInTheForm(String query, String discountTag) {
		return DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().toString().contains(discountTag);
	}

	private boolean ahdexxDiscountTagPresentInTheForm(String query, String discountTag) {
		return DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().toString().contains(discountTag);
	}

	void checkDocumentContentAHDRXX(String policyNumber, boolean isGenerated, boolean isMembershipDataPresent, boolean isEvalueDataPresent, boolean isPaperlessDiscDataPresent,
			boolean isPaperlessDlvryDataPresent, ETCSCoreSoftAssertions softly) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDRXX", "ENDORSEMENT_ISSUE");

		if (isGenerated) {
			if (isMembershipDataPresent) {
				softly.assertThat(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Discount")).isTrue();
				//PAS-1549 Start
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("AAAMemDiscAmt", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()).isEqualTo("5.0%");
				//PAS-1549 End
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()).isEqualTo("Y");
			} else {
				softly.assertThat(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Discount")).isFalse();
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()).isEqualTo("N");
			}

			if (isEvalueDataPresent) {
				softly.assertThat(ahdrxxDiscountTagPresentInTheForm(query, "eValue Discount")).isTrue();
				//PAS-1549, PAS-310 Start
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("eValDiscAmt", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()).isEqualTo("13.5%");
				//PAS-1549, PAS-310 Start
			} else {
				softly.assertThat(ahdrxxDiscountTagPresentInTheForm(query, "eValue Discount")).isFalse();
			}

			if (isPaperlessDiscDataPresent) {
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("PapPrefDiscYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()).isEqualTo("Y");
			} else {
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("PapPrefDiscYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()).isEqualTo("N");
			}

			if (isPaperlessDlvryDataPresent) {
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("PaplssDlvryYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()).isEqualTo("Y");
			} else {
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("PaplssDlvryYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()).isEqualTo("N");
			}
			//PAS-1928 start
			softly.assertThat(DocGenHelper.getDocumentDataElemByName("PlcyTransCd", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField())
					.isEqualTo("0100");
			//PAS-1928 end
			lastTransactionHistoryExit();
			//TODO OSI return the check when EM team confirms why the docs are generated with such a long delay - INC0655981
			//Efolder.isDocumentExist("Endorsement", "Discount Removed");
		} else {
			//BUG PAS-7149 AHDRXX is generated when MembershipEligibility=FALSE and eValue discount is not removed
			softly.assertThat(DBService.get().getValue(query).isPresent()).isFalse();
		}
	}

	private void checkDocumentContentAHDEXX(String policyNumber, boolean isGenerated, boolean isMembershipDataPresent, boolean isEvalueDataPresent, boolean isPaperlessDiscDataPresent,
			boolean isPaperlessDlvryDataPresent, ETCSCoreSoftAssertions softly) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDEXX", "MEMBERSHIP_VALIDATE");

		if (isGenerated) {
			if (isMembershipDataPresent) {
				softly.assertThat(ahdexxDiscountTagPresentInTheForm(query, "AAA Membership Discount")).isTrue();
				//PAS-1549 Start
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("AAAMemDiscAmt", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0)
						.getDataElementChoice().getTextField()).isEqualTo("5.0%");
				//PAS-1549 End
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0)
						.getDataElementChoice().getTextField()).isEqualTo("Y");
			} else {
				softly.assertThat(ahdexxDiscountTagPresentInTheForm(query, "AAA Membership Discount")).isFalse();
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0)
						.getDataElementChoice().getTextField()).isEqualTo("N");
			}

			if (isEvalueDataPresent) {
				//BUG no evalue info in AHDEXX
				softly.assertThat(ahdexxDiscountTagPresentInTheForm(query, "eValue Discount")).isTrue();
				//PAS-1549, PAS-310 Start
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("eValDiscAmt", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0)
						.getDataElementChoice().getTextField()).isEqualTo("13.5%");
				//PAS-1549, PAS-310 End
			} else {
				softly.assertThat(ahdexxDiscountTagPresentInTheForm(query, "eValue Discount")).isFalse();
			}

			if (isPaperlessDiscDataPresent) {
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("PapPrefDiscYN", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0)
						.getDataElementChoice().getTextField()).isEqualTo("Y");
			} else if (!DocGenHelper.getDocumentDataElemByName("PapPrefDiscYN", DocGenEnum.Documents.AHDEXX, query).isEmpty()) {
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("PapPrefDiscYN", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0)
						.getDataElementChoice().getTextField()).isEqualTo("N");
			}

			if (isPaperlessDlvryDataPresent) {
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("PaplssDlvryYN", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0)
						.getDataElementChoice().getTextField()).isEqualTo("Y");
			} else if (!DocGenHelper.getDocumentDataElemByName("PaplssDlvryYN", DocGenEnum.Documents.AHDEXX, query).isEmpty()) {
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("PaplssDlvryYN", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0)
						.getDataElementChoice().getTextField()).isEqualTo("N");
			}
			//PAS-1928 start
			softly.assertThat(DocGenHelper.getDocumentDataElemByName("PlcyTransCd", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField())
					.isEqualTo("0210");
			//PAS-1928 end
		} else {
			softly.assertThat(DBService.get().getValue(query).isPresent()).isFalse();
		}
	}

	private void ahdexxGeneratedCheck(boolean isGenerated, String policyNumber, int numberOfDocuments, ETCSCoreSoftAssertions softly) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDEXX", "MEMBERSHIP_VALIDATE");
		String query2 = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "AHDEXX", "MEMBERSHIP_VALIDATE");
		if (isGenerated) {
			softly.assertThat(DBService.get().getValue(query)).isPresent();
			softly.assertThat(DBService.get().getValue(query2).map(Integer::parseInt)).hasValue(numberOfDocuments);
		} else {
			softly.assertThat(DBService.get().getValue(query)).isNotPresent();
		}
	}

	private void ahdexxContentCheck(String membershipEligibilitySwitch, String policyNumber, ETCSCoreSoftAssertions softly) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDEXX", "MEMBERSHIP_VALIDATE");

		softly.assertThat(DocGenHelper.getDocumentDataElemByName("AAAMemDiscAmt", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField())
				.as("Membership discount tag problem").isEqualTo("5.0%");
		softly.assertThat(DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDEXX, query).get(0).toString()).contains("AAA Membership Discount");
		softly.assertThat(ahdexxDiscountTagPresentInTheForm(query, "AAA Membership Discount")).isTrue();
		if ("TRUE".equals(membershipEligibilitySwitch)) {
			softly.assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(1).getCell(1)).hasValue(MESSAGE_INFO_1);
			softly.assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1)).valueContains(MESSAGE_BULLET_8);
			softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("No");
			softly.assertThat(DocGenHelper.getDocumentDataElemByName("eValDiscAmt", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField())
					.as("eValue discount tag problem").isEqualTo("13.5%");
			softly.assertThat(DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDEXX, query).get(0).toString()).contains("eValue Discount");
			softly.assertThat(ahdexxDiscountTagPresentInTheForm(query, "eValue Discount")).isTrue();
		} else {
			softly.assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(1).getCell(1)).hasValue(MESSAGE_INFO_4);
			softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("Yes");
			softly.assertThat(ahdexxDiscountTagPresentInTheForm(query, "eValue Discount")).isFalse();
		}
	}

	private void cancelReinstateToAvoidRminusJobs(String policyNumber, LocalDateTime policyExpirationDate) {
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(64));
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(49));
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
		policy.renew().start();
		premiumAndCoveragesTab.saveAndExit();
	}


	@AfterSuite()
	public void deleteAllPaperlessPreferencesRequests() {
		deleteMultiplePaperlessPreferencesRequests();
		printToLog("ALL REQUEST DELETION WAS EXECUTED");
	}

	private HelperWireMockStub createPaperlessPreferencesRequestId(String policyNumber, String paperlessAction) {
		PaperlessPreferencesTemplateData template = create(policyNumber, paperlessAction);
		HelperWireMockStub stub = HelperWireMockStub.create("paperless-preferences-200", template).mock();
		stubList.add(stub);
		printToLog("THE REQUEST ID WAS CREATED " + stub.getId());
		return stub;
	}

	private void deleteMultiplePaperlessPreferencesRequests() {
		for (HelperWireMockStub stub : stubList) {
			stub.cleanUp();
			printToLog("MULTIPLE REQUEST DELETION WAS EXECUTED for " + stub.getId());
		}
		stubList.clear();
	}

	private void deleteSinglePaperlessPreferenceRequest(HelperWireMockStub stub) {
		stub.cleanUp();
		printToLog("DELETE SINGLE REQUEST WAS EXECUTED for " + stub.getId());
		stubList.remove(stub);
	}

	/**
	 * Post Condition  - Checks that number of failed async tasks is not huge
	 */
	@Test(groups = {Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"N/A"})
	public void xAsyncTaskCheck() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusYears(2));
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.GENERAL.get());
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_ASYNC_TASKS.get());
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(Integer.valueOf(GeneralAsyncTasksPage.labelFailedTasks.getValue())).isLessThan(2);
			softly.assertThat(Integer.valueOf(GeneralAsyncTasksPage.labelLockedTasks.getValue())).isLessThan(2);
		});
	}
}
