/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.TestEValueMembershipProcessPreConditions;
import aaa.modules.regression.service.helper.HelperWireMockPaperlessPreferences;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.waiters.Waiters;

public class TestEValueMembershipProcess extends AutoSSBaseTest implements TestEValueMembershipProcessPreConditions {

	private static final String E_VALUE_DISCOUNT = "eValue Discount"; //PAS-440 - rumors have it, that discount might be renamed
	private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
	private static final String MESSAGE_INFO_1 = "This customer is not eligible for eValue discount due to one or more of the following reasons:";
	private static final String MESSAGE_BULLET_8 = "Does not have an active AAA membership";
	private static final String MESSAGE_INFO_4 = "eValue Discount Requirements:";
	private static List<String> requestIdList = new LinkedList<>();
	private Random random = new Random();
	private GeneralTab generalTab = new GeneralTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private SSHController sshControllerRemote = new SSHController(
			PropertyProvider.getProperty(CustomTestProperties.APP_HOST),
			PropertyProvider.getProperty(CustomTestProperties.SSH_USER),
			PropertyProvider.getProperty(CustomTestProperties.SSH_PASSWORD));

	@Test(description = "Check membership endpoint", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void retrieveMembershipSummaryEndpointCheck() {
		CustomAssert.assertTrue("retrieveMembershipSummary doesn't use stub endpoint. Please run retrieveMembershipSummaryStubEndpointUpdate", DBService.get()
				.getValue(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK).get().contains(APP_HOST));
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

	@Test(groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public void preconditionsClearFolders() throws SftpException, JSchException {
		printToLog("Clear membership folders started");

		sshControllerRemote.deleteFile(new File(PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER) + "PAS_B_EXGPAS_PASHUB_4004_D/archive" + "/*.*"));
		sshControllerRemote.deleteFile(new File(PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER) + "PAS_B_PASHUB_EXGPAS_4004_D/archive" + "/*.*"));

		if (RemoteHelper.isPathExist(PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER) + "PAS_B_EXGPAS_PASHUB_4004_D/outbound")) {
			RemoteHelper.clearFolder(PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER) + "PAS_B_EXGPAS_PASHUB_4004_D/outbound");
		}
		printToLog("Clear membership folders completed");
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount and Membership Discount are NOT removed when Membership is required for eValue and membership status = Active.
	 * @scenario
	 * 0. Check no email record present in admin log (no "eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is not removed on NB+30
	 * 2. Check eValue discount is not removed on NB+30
	 * 3. Check AHDRXX is not produced on NB+30
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationTrueForActiveMembership(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Active", true);

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(false, "already activated by previous job");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);
		checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount and Membership Discount are removed when Membership is required for eValue and membership status = Pending.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is removed on NB+30
	 * 2. Check eValue discount is removed on NB+30
	 * 3. Check AHDRXX is produced on NB+30 and contains both eValue and Membership discount info
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationTrueForPendingMembership(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Pending", true);

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(false, "no records created");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		//BUG PAS-11150 eValue doesnt become INACTIVE on NB+30 when Membership status is Pending
		eValueDiscountStatusCheck(policyNumber, "INACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "INACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 2, "eValue and Membership Discounts Removed - Membership");
		latestTransactionMembershipAndEvalueDiscountsCheck(false, false, membershipDiscountEligibilitySwitch);
		checkDocumentContentAHDRXX(policyNumber, true, true, true, false, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount and Membership Discount are removed when Membership is required for eValue and membership status = Not Active.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is removed on NB+30
	 * 2. Check eValue discount is removed on NB+30
	 * 3. Check AHDRXX is produced on NB+30 and contains both eValue and Membership discount info
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationTrueForNotActiveMembership(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Non-Active", true);

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(true, "Membership information was updated for the policy based on best membership logic");
		transactionHistoryRecordCountCheck(policyNumber, 2, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "INACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "INACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 3, "eValue and Membership Discounts Removed - Membership");
		latestTransactionMembershipAndEvalueDiscountsCheck(false, false, membershipDiscountEligibilitySwitch);
		checkDocumentContentAHDRXX(policyNumber, true, true, true, false, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount and Membership Discount NOT removed when Membership is NOT required for eValue and membership status = Active.
	 * @scenario
	 * 0. Check no email record present in admin log (no "eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is not removed on NB+30
	 * 2. Check eValue discount is not removed on NB+30
	 * 3. Check AHDRXX is not produced on NB+30
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationFalseForActiveMembership(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "FALSE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Active", true, false);

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(false, "already processed by previous job");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);
		checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name eValue Discount not removed and Membership Discount is removed when Membership is NOT required for eValue and membership status = Pending.
	 * @scenario
	 * 0. Check email record is NOT present in admin log (no "eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is removed on NB+30
	 * 2. Check eValue discount is NOT removed on NB+30
	 * 3. Check AHDRXX is produced on NB+30 and contains only Membership discount info
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationFalseForPendingMembership(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "FALSE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Pending", true, false);

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");

		jobsNBplus15plus30runNoChecks();
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 2, "Membership Discount Removed");
		latestTransactionMembershipAndEvalueDiscountsCheck(false, true, membershipDiscountEligibilitySwitch);
		//BUG PAS-7149 AHDRXX is generated when MembershipEligibility=FALSE and eValue discount is not removed
		checkDocumentContentAHDRXX(policyNumber, true, true, false, false, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount not removed and Membership Discount is removed when Membership is NOT required for eValue and membership status = Not Active.
	 * @scenario
	 * 0. Check email record is NOT present in admin log (no "eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is removed on NB+30
	 * 2. Check eValue discount NOT removed on NB+30
	 * 3. Check AHDRXX is produced on NB+30 and contains only Membership discount info
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationFalseForNotActiveMembership(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "FALSE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Non-Active", true, false);

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 2, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 3, "Membership Discount Removed");
		latestTransactionMembershipAndEvalueDiscountsCheck(false, true, membershipDiscountEligibilitySwitch);
		//BUG PAS-7149 AHDRXX is generated when MembershipEligibility=FALSE and eValue discount is not removed
		checkDocumentContentAHDRXX(policyNumber, true, true, false, false, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationTrueForCancelledMembershipRenewal(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		String membershipStatusActive = "Cancelled";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		CustomAssert.enableSoftMode();
		renewalMembershipProcessCheck(membershipDiscountEligibilitySwitch, membershipStatusActive);
		postConditionMembershipEligibilityCheck();
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10229")
	public void pas10229_membershipEligibilityConfigurationTrueForActiveMembershipActiveEValueRenewal(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		String membershipStatus = "Active";
		boolean eValueSet = true;

		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);
		String policyNumber = membershipEligibilityPolicyCreation(membershipStatus, eValueSet);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate); //-96
		LocalDateTime renewReportOrderingDate = getTimePoints().getRenewReportsDate(policyExpirationDate); //-63

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

		executeMembershipJobsRminus63Rminus48(renewReportOrderingDate, true);
		ahdexxGeneratedCheck(false, policyNumber, 0);

		executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
		renewalTransactionHistoryCheck(policyNumber, true, true, "inquiry");
		ahdexxGeneratedCheck(false, policyNumber, 0);
		renewalTransactionHistoryCheck(policyNumber, true, true, "dataGather");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10229")
	public void pas10229_membershipEligibilityConfigurationTrueForActiveMembershipInActiveEValueRenewal(@Optional("VA") String state) {
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
		ahdexxGeneratedCheck(false, policyNumber, 0);

		executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
		renewalTransactionHistoryCheck(policyNumber, true, false, "inquiry");
		ahdexxGeneratedCheck(false, policyNumber, 0);
		renewalTransactionHistoryCheck(policyNumber, true, false, "dataGather");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10229")
	public void pas10229_membershipEligConfigurationTrueForInActiveMembershipActiveEValueRenewalMinus48(@Optional("VA") String state) {
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
		ahdexxGeneratedCheck(false, policyNumber, 0);

		executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
		renewalTransactionHistoryCheck(policyNumber, true, true, "inquiry");
		ahdexxGeneratedCheck(true, policyNumber, 1);
		checkDocumentContentAHDEXX(policyNumber, true, true, true, false, false);
		renewalTransactionHistoryCheck(policyNumber, false, false, "dataGather");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10229")
	public void pas10229_membershipEligConfigurationTrueForInActiveMembershipNotActiveEValueRenewalMinus48(@Optional("VA") String state) {
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
		ahdexxGeneratedCheck(false, policyNumber, 0);

		executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
		renewalTransactionHistoryCheck(policyNumber, true, false, "inquiry");
		ahdexxGeneratedCheck(true, policyNumber, 1);
		checkDocumentContentAHDEXX(policyNumber, true, true, false, false, false);
		renewalTransactionHistoryCheck(policyNumber, false, false, "dataGather");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10229")
	public void pas10229_membershipEligConfigurationTrueForInActiveMembershipActiveEValueRenewalMinus63(@Optional("VA") String state) {
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
		renewalTransactionHistoryCheck(policyNumber, true, true, "inquiry");
		ahdexxGeneratedCheck(true, policyNumber, 1);
		checkDocumentContentAHDEXX(policyNumber, true, true, true, false, false);

		executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
		renewalTransactionHistoryCheck(policyNumber, true, true, "inquiry");
		//BUG PAS-10735 AHDEXX is generated on R-48, though it was generated on R-63
		ahdexxGeneratedCheck(true, policyNumber, 1);
		checkDocumentContentAHDEXX(policyNumber, true, true, true, false, false);
		renewalTransactionHistoryCheck(policyNumber, false, false, "dataGather");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10229")
	public void pas10229_membershipEligConfigurationTrueForInActiveMembershipNotActiveEValueRenewalMinus63(@Optional("VA") String state) {
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
		renewalTransactionHistoryCheck(policyNumber, true, false, "inquiry");
		ahdexxGeneratedCheck(true, policyNumber, 1);

		executeMembershipJobsRminus63Rminus48(policyExpirationDate.minusDays(48));
		renewalTransactionHistoryCheck(policyNumber, true, false, "inquiry");
		ahdexxGeneratedCheck(true, policyNumber, 1);
		checkDocumentContentAHDEXX(policyNumber, true, true, false, false, false);
		renewalTransactionHistoryCheck(policyNumber, false, false, "dataGather");
	}

	private void cancelReinstateToAvoidNbPlus15Plus30Jobs(String policyNumber) {
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(2));
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
	}

	private void renewalTransactionHistoryCheck(String policyNumber, boolean membershipDiscountPresent, boolean eValueDiscountPresent, String mode) {
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
			CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount"));
		} else {
			CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount"));
		}
		if (eValueDiscountPresent) {
			CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("eValue Discount"));
		} else {
			CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("eValue Discount"));
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationFalseForCancelledMembershipRenewal(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "FALSE";
		String membershipStatusActive = "Cancelled";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		CustomAssert.enableSoftMode();
		renewalMembershipProcessCheck(membershipDiscountEligibilitySwitch, membershipStatusActive);
		postConditionMembershipEligibilityCheck();
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * The test is created to check the endpoints and correctness of the methods
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8275"})
	public void pas111_paperlessMockTest(@Optional("VA") String state) {
		String policyNumber = "VASS926232072";

		String requestId = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());
		//Always need to delete the added request ot stub
		deleteSinglePaperlessPreferenceRequest(requestId);

		String requestId2 = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());

		//PolicySummaryPage.getPolicyNumber();

		deleteSinglePaperlessPreferenceRequest(requestId2);
		String requestId3 = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());
		deleteSinglePaperlessPreferenceRequest(requestId3);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount is removed, Membership Discount is not when Membership is required for eValue
	 * and membership status = Active, but PaperlessPreferences = Pending.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is not removed on NB+30
	 * 2. Check eValue discount removed on NB+30
	 * 3. Check AHDRXX is produced on NB+30:
	 * Membership discount tag - not present in the document
	 * Evalue discount tag - present
	 * Paperless info tag - present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligConfTrueForActiveMembershipPendingPaperless(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		String requestId = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(false, "no record created");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "INACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "INACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 2, "eValue Discount Removed - Paperless");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, false, membershipDiscountEligibilitySwitch, false);
		checkDocumentContentAHDRXX(policyNumber, true, false, true, true, false);

		deleteSinglePaperlessPreferenceRequest(requestId);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount is removed, Membership Discount is removed when Membership is required for eValue
	 * and membership status = Pending, but PaperlessPreferences = Pending.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is removed on NB+30
	 * 2. Check eValue discount removed on NB+30
	 * 3. Check AHDRXX is produced on NB+30:
	 * Membership discount tag - present
	 * Evalue discount tag - present
	 * Paperless info tag - present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligConfTrueForPendingMembershipPendingPaperless(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Pending", true);
		String requestId = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(false, "no record created");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "INACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "INACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 2, "eValue and Membership Discounts Removed - Membership, Paperless");
		latestTransactionMembershipAndEvalueDiscountsCheck(false, false, membershipDiscountEligibilitySwitch, false);
		checkDocumentContentAHDRXX(policyNumber, true, true, true, true, false);

		deleteSinglePaperlessPreferenceRequest(requestId);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount is removed, Membership Discount is removed when Membership is required for eValue
	 * and membership status = Not Active, but PaperlessPreferences = Pending.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is removed on NB+30
	 * 2. Check eValue discount removed on NB+30
	 * 3. Check AHDRXX is produced on NB+30:
	 * Membership discount tag - present
	 * Evalue discount tag - present
	 * Paperless info tag - present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligConfTrueForNotActiveMembershipPendingPaperless(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Non-Active", true);
		String requestId = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(false, "no record created");
		transactionHistoryRecordCountCheck(policyNumber, 2, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "INACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "INACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 3, "eValue and Membership Discounts Removed - Membership, Paperless");
		latestTransactionMembershipAndEvalueDiscountsCheck(false, false, membershipDiscountEligibilitySwitch, false);
		checkDocumentContentAHDRXX(policyNumber, true, true, true, true, false);

		deleteSinglePaperlessPreferenceRequest(requestId);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount is removed but Membership Discount is not removed when Membership is not required for eValue
	 * and membership status = Active, but PaperlessPreferences = Pending.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is removed on NB+30
	 * 2. Check eValue discount removed on NB+30
	 * 3. Check AHDRXX is produced on NB+30:
	 * Membership discount tag - not present in the document
	 * Evalue discount tag - present
	 * Paperless info tag - present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligConfFalseForActiveMembershipPendingPaperless(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "FALSE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Active", true, false);
		String requestId = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(false, "no record created");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "INACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "INACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 2, "eValue Discount Removed - Paperless");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, false, membershipDiscountEligibilitySwitch);
		checkDocumentContentAHDRXX(policyNumber, true, false, true, true, false);

		deleteSinglePaperlessPreferenceRequest(requestId);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount is removed, Membership Discount is removed when Membership is not required for eValue
	 * and membership status = Pending, but PaperlessPreferences = Pending.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is removed on NB+30
	 * 2. Check eValue discount removed on NB+30
	 * 3. Check AHDRXX is produced on NB+30:
	 * Membership discount tag - present?
	 * Evalue discount tag - present
	 * Paperless info tag - present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligConfFalseForPendingMembershipPendingPaperless(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "FALSE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Pending", true, false);
		String requestId = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(false, "no record created");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "INACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "INACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 2, "Membership Discount Removed; eValue Discount Removed - Paperless");
		latestTransactionMembershipAndEvalueDiscountsCheck(false, false, membershipDiscountEligibilitySwitch);
		checkDocumentContentAHDRXX(policyNumber, true, true, true, true, false);

		deleteSinglePaperlessPreferenceRequest(requestId);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount is removed Membership Discount is removed when Membership is not required for eValue
	 * and membership status = Not-Active, but PaperlessPreferences = Pending.
	 * @scenario
	 * 0. Check email record present in admin log ("eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is removed on NB+30
	 * 2. Check eValue discount removed on NB+30
	 * 3. Check AHDRXX is produced on NB+30:
	 * Membership discount tag - not present in the document
	 * Evalue discount tag - present
	 * Paperless info tag - present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligConfFalseForNotActiveMembershipPendingPaperless(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "FALSE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Non-Active", true, false);
		String requestId = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(false, "no record created");
		transactionHistoryRecordCountCheck(policyNumber, 2, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "INACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "INACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 3, "Membership Discount Removed; eValue Discount Removed - Paperless");
		latestTransactionMembershipAndEvalueDiscountsCheck(false, false, membershipDiscountEligibilitySwitch);
		checkDocumentContentAHDRXX(policyNumber, true, true, true, true, false);

		deleteSinglePaperlessPreferenceRequest(requestId);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount when PaperlessPreferences changes.
	 * @scenario
	 * 1. Create a policy with eValue discount with Paperless Preferences = Pending
	 * 2. Run NB+15 jobs, in admin log there is ("eValue Discount Pending Notification Url:") on NB+15
	 * 3. Before NB + 30 change Paperless Preferences = Yes
	 * 4. Run NB+30 jobs
	 * 5. Check eValue discount is not removed
	 * 6. Check No AHDRXX is produced
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligConfTrueForActiveMembershipNoPaperlessChangedToYesBeforeNB30(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		String requestId1 = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(false, "no record created");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);
		deleteSinglePaperlessPreferenceRequest(requestId1);

		String requestId2 = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN.get());
		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, false);
		checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false);
		deleteSinglePaperlessPreferenceRequest(requestId2);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount when PaperlessPreferences changes.
	 * @scenario
	 * 1. Create a policy with eValue discount with Paperless Preferences = Pending
	 * 2. Before NB + 15 change Paperless Preferences = Yes
	 * 3. Run NB+15 jobs, in admin log there is no("eValue Discount Pending Notification Url:") on NB+15
	 * 4. Run NB+30 jobs
	 * 5. Check eValue discount is not removed
	 * 6. Check No AHDRXX is produced
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligConfTrueForActiveMembershipNoPaperlessChangedToYesBeforeNB15(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Active", true);
		String requestId1 = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());
		deleteSinglePaperlessPreferenceRequest(requestId1);

		CustomAssert.enableSoftMode();
		String requestId2 = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN.get());
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(false, "no record created");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch, false);
		checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false);
		deleteSinglePaperlessPreferenceRequest(requestId2);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount is removed on NB+30 when PaperlessPreferences is set to Pending
	 * @scenario
	 * 0. Create a policy with eValue = Yes and Paperless Preferences = Pending (DC state)
	 * 1. change date to NB+15 and run NB+15jobs
	 * 2. Check admin log contains email that eValue will be removed (manual action) ( "eValue Discount Pending Notification Url:")
	 * 3. change date to NB+30 and run NB+30jobs (same as NV+15jobs)
	 * 4. Check eValue Discount = No in P&C tab
	 * 5. Check AHDRXX is produced and contains eValue information
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-5837")
	public void pas5837_eValueDiscountRemovedIfPaperlessPreferenceIsPending(@Optional("DC") String state) {
		String membershipDiscountEligibilitySwitch = "FALSE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipEligibilityPolicyCreation("Active", true, false);

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(false, "no changes");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "INACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "INACTIVE");
		transactionHistoryRecordCountCheck(policyNumber, 2, "eValue Discount Removed - Paperless");
		//BUG Membership Discount infor printed in the doc
		latestTransactionMembershipAndEvalueDiscountsCheck(true, false, membershipDiscountEligibilitySwitch);
		//BUG PAS-7265 Paperless preference reason isn't displayed in AHDRXX document in case Paperless is Pending at NB+30
		checkDocumentContentAHDRXX(policyNumber, true, false, true, true, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "preconditionsClearFolders")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11740")
	public void pas11740_membershipEligConfTrueForPendingMembershipNotEvalueState(@Optional("OK") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);
		String policyNumber = membershipEligibilityPolicyCreation("Pending", false);

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		membershipLogicActivitiesAndNotesCheck(true, "Membership information was updated for the policy based on best membership logic");
		transactionHistoryRecordCountCheck(policyNumber, 2, "");
		lastTransactionHistoryMembershipDiscountCheck(true);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		transactionHistoryRecordCountCheck(policyNumber, 3, "Membership Discount Removed");
		lastTransactionHistoryMembershipDiscountCheck(false);
		checkDocumentContentAHDRXX(policyNumber, true, true, false, false, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void renewalMembershipProcessCheck(String membershipEligibilitySwitch, String membershipStatus) {
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

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		ahdexxContentCheck(membershipEligibilitySwitch, policyNumber);
	}

	private String membershipEligibilityPolicyCreation(String membershipStatus, boolean eValueSet) {
		return membershipEligibilityPolicyCreation(membershipStatus, eValueSet, false);
	}

	private String membershipEligibilityPolicyCreation(String membershipStatus, boolean eValueSet, boolean defaultEvalueQuote) {
		testEValueDiscount.eValueQuoteCreation(defaultEvalueQuote);
		policy.dataGather().start();
		setMembershipAndRate(membershipStatus, eValueSet);
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();
		return policyNumber;
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
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
	}

	//@Test
	private void jobsNBplus15plus30runNoChecks() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(15));
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

	private void executeMembershipJobsRminus63Rminus48(LocalDateTime renewReportOrderingDate, boolean clearExgPasArchiveFolder) {
		if (clearExgPasArchiveFolder) {
			//RemoteHelper.clearFolder(PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER) + "PAS_B_EXGPAS_PASHUB_4004_D/archive");
			//RemoteHelper.clearFolder(PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER) + "PAS_B_PASHUB_EXGPAS_4004_D/archive");
			try {
				sshControllerRemote.deleteFile(new File(PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER) + "PAS_B_EXGPAS_PASHUB_4004_D/archive" + "/*.*"));
				sshControllerRemote.deleteFile(new File(PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER) + "PAS_B_PASHUB_EXGPAS_4004_D/archive" + "/*.*"));
			} catch (JSchException | SftpException e) {
				e.printStackTrace();
			}
		}
		TimeSetterUtil.getInstance().nextPhase(renewReportOrderingDate);
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
		Waiters.SLEEP(5000).go();
		HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_MEMBERSHIP_SUMMARY_BATCH);
		Waiters.SLEEP(5000).go();
		//RemoteHelper.clearFolder(PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER) + "PAS_B_EXGPAS_PASHUB_4004_D/outbound");
		try {
			sshControllerRemote.deleteFile(new File(PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER) + "PAS_B_EXGPAS_PASHUB_4004_D/outbound" + "/*.*"));
		} catch (JSchException | SftpException e) {
			e.printStackTrace();
		}

		Waiters.SLEEP(5000).go();
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchReceiveAsyncJob);
	}

	private void eValueDiscountStatusCheck(String policyNumber, String status) {
		String getEvalueStatusSQL = "select evalueStatus from (\n"
				+ "  select ps.policynumber, emd.evaluestatus\n"
				+ "  from PolicySummary ps, AAAEMemberDetailsEntity emd\n"
				+ "  where ps.ememberdetail_id = emd.id\n"
				+ "  and ps.policynumber = '%s'\n"
				+ "  order by emd.id desc)\n"
				+ "where rownum = 1";
		CustomAssert.assertEquals(status, DBService.get().getValue(String.format(getEvalueStatusSQL, policyNumber)).get());
	}

	private void membershipLogicActivitiesAndNotesCheck(boolean presence, String status) {
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		if (presence) {
			NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", status).getCell("Date/Time").verify
					.contains(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
		} else {
			if (NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", "for the policy based on Preferences and Membership logic").isPresent()) {
				int rowNum = NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", "for the policy based on Preferences and Membership logic").getIndex();
				CustomAssert.assertFalse(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(rowNum).getCell("Date/Time").getValue()
						.contains(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY)));
			}
		}
	}

	private void transactionHistoryRecordCountCheck(String policyNumber, int rowCount, String value) {
		PolicySummaryPage.buttonTransactionHistory.click();
		CustomAssert.assertEquals(PolicySummaryPage.tableTransactionHistory.getRowsCount(), rowCount);
		String valueShort = "";
		if (!StringUtils.isEmpty(value)) {
			valueShort = value.substring(0, 20);
			assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").getHintValue()).contains(value);
		}
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").getValue()).contains(valueShort);

		String transactionHistoryQuery = "select TXREASONTEXT \n"
				+ "from PolicyTransaction \n"
				+ "where POLICYID in \n"
				+ "    (select id from \n"
				+ "        (select id from POLICYSUMMARY \n"
				+ "        where POLICYNUMBER = '%s'\n"
				+ "        order by id desc)\n"
				+ "where rownum=1)";
		assertThat(DBService.get().getValue(String.format(transactionHistoryQuery, policyNumber)).orElse(StringUtils.EMPTY)).isEqualTo(value);
	}

	/**
	 * Check if getHintValue works
	 * @param state
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void getHint(@Optional("VA") String state) {
		mainApp().open();
		SearchPage.openPolicy("VASS952918544");
		PolicySummaryPage.buttonTransactionHistory.click();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").getHintValue()).isNotEqualTo("eValue Discount Removed - Paperless");
	}

	/**
	 * Check if getWarning works
	 * @param state
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void getWarning(@Optional("VA") String state) {
		mainApp().open();
		SearchPage.openQuote("QVASS952918545");
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());
		DriverTab driverTab = new DriverTab();
		driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.AGE_FIRST_LICENSED).setValue("01");
		assertThat(driverTab.getAssetList().getWarning(AutoSSMetaData.DriverTab.AGE_FIRST_LICENSED).getValue()).contains("Age First Licensed must be 14 or greater");
		driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.DATE_OF_BIRTH).setValue("01/01/2022");
		assertThat(driverTab.getAssetList().getWarning(AutoSSMetaData.DriverTab.DATE_OF_BIRTH).getValue()).contains("The date of birth provided for the Driver Available for Rating should be between 01/01/1900 and ");
	}

	private void lastTransactionHistoryExit() {
		if (Tab.buttonCancel.isPresent()) {
			premiumAndCoveragesTab.cancel();
			Tab.buttonCancel.click();
		}
	}

	private void lastTransactionHistoryOpen() {
		if (PolicySummaryPage.buttonTransactionHistory.isPresent()) {
			PolicySummaryPage.buttonTransactionHistory.click();
		}
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).controls.links.get(1).click();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
	}

	private void lastTransactionHistoryMembershipDiscountCheck(boolean membershipDiscount) {
		lastTransactionHistoryOpen();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		if (membershipDiscount) {
			CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount"));
		} else {
			CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount"));
		}
	}

	private void latestTransactionMembershipAndEvalueDiscountsCheck(boolean membershipDiscountPresent, boolean eValueDiscountPresent, String membershipEligibilitySwitch) {
		latestTransactionMembershipAndEvalueDiscountsCheck(membershipDiscountPresent, eValueDiscountPresent, membershipEligibilitySwitch, true);
	}

	private void latestTransactionMembershipAndEvalueDiscountsCheck(boolean membershipDiscountPresent, boolean eValueDiscountPresent, String membershipEligibilitySwitch, boolean checkMessages) {
		lastTransactionHistoryOpen();
		if (!membershipDiscountPresent) {
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
			generalTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel()).verify.value("No");
		}
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		if (membershipDiscountPresent) {
			CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount"));
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "AAA Membership Discount").getCell(2).verify.value("Yes");
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		} else {
			CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount"));
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "AAA Membership Discount").getCell(3).verify.value("None");
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		}
		lastTransactionHistoryExit();
		if (eValueDiscountPresent) {
			lastTransactionHistoryOpen();
			CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
			premiumAndCoveragesTab.getInquiryAssetList().getStaticElement("Apply eValue Discount").verify.value("Yes");
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			if (PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, E_VALUE_DISCOUNT).isPresent()) {
				PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, E_VALUE_DISCOUNT).getCell(5).verify.value("Yes");
			} else {
				PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT).getCell(4).verify.value("Yes");
			}
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		} else {
			lastTransactionHistoryOpen();
			CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
			premiumAndCoveragesTab.getInquiryAssetList().getStaticElement("Apply eValue Discount").verify.value("No");
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, "eValue Discount").getCell(6).verify.value("None");
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
			if (checkMessages) {
				if ("TRUE".equals(membershipEligibilitySwitch)) {
					PremiumAndCoveragesTab.tableEValueMessages.getRow(1).getCell(1).verify.value(MESSAGE_INFO_1);
					PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).verify.contains(MESSAGE_BULLET_8);
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

	private void checkDocumentContentAHDRXX(String policyNumber, boolean isGenerated, boolean isMembershipDataPresent, boolean isEvalueDataPresent, boolean isPaperlessDiscDataPresent, boolean isPaperlessDlvryDataPresent) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDRXX", "ENDORSEMENT_ISSUE");

		if (isGenerated) {
			if (isMembershipDataPresent) {
				CustomAssert.assertTrue(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
				//PAS-1549 Start
				CustomAssert.assertTrue("5.0%"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemDiscAmt", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
				//PAS-1549 End
				CustomAssert.assertTrue("Y"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			} else {
				CustomAssert.assertFalse(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
				CustomAssert.assertTrue("N"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			}

			if (isEvalueDataPresent) {
				CustomAssert.assertTrue(ahdrxxDiscountTagPresentInTheForm(query, "eValue Discount"));
				//PAS-1549 Start
				CustomAssert.assertTrue("13.5%"
						.equals(DocGenHelper.getDocumentDataElemByName("eValDiscAmt", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
				//PAS-1549 End
			} else {
				CustomAssert.assertFalse(ahdrxxDiscountTagPresentInTheForm(query, "eValue Discount"));
			}

			if (isPaperlessDiscDataPresent) {
				CustomAssert.assertTrue("Y"
						.equals(DocGenHelper.getDocumentDataElemByName("PapPrefDiscYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
			} else {
				CustomAssert.assertTrue("N"
						.equals(DocGenHelper.getDocumentDataElemByName("PapPrefDiscYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
			}

			if (isPaperlessDlvryDataPresent) {
				CustomAssert.assertTrue("Y"
						.equals(DocGenHelper.getDocumentDataElemByName("PaplssDlvryYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
			} else {
				CustomAssert.assertTrue("N"
						.equals(DocGenHelper.getDocumentDataElemByName("PaplssDlvryYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
			}
			lastTransactionHistoryExit();
			//TODO OSI return the check when EM team confirms why the docs are generated with such a long delay - INC0655981
			//Efolder.isDocumentExist("Endorsement", "Discount Removed");
		} else {
			//BUG PAS-7149 AHDRXX is generated when MembershipEligibility=FALSE and eValue discount is not removed
			CustomAssert.assertFalse(DBService.get().getValue(query).isPresent());
		}
	}

	private void checkDocumentContentAHDEXX(String policyNumber, boolean isGenerated, boolean isMembershipDataPresent, boolean isEvalueDataPresent, boolean isPaperlessDiscDataPresent, boolean isPaperlessDlvryDataPresent) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDEXX", "MEMBERSHIP_VALIDATE");

		if (isGenerated) {
			if (isMembershipDataPresent) {
				CustomAssert.assertTrue(ahdexxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
				//PAS-1549 Start
				CustomAssert.assertTrue("5.0%"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemDiscAmt", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
				//PAS-1549 End
				CustomAssert.assertTrue("Y"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			} else {
				CustomAssert.assertFalse(ahdexxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
				CustomAssert.assertTrue("N"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			}

			if (isEvalueDataPresent) {
				//BUG no evalue info in AHDEXX
				CustomAssert.assertTrue(ahdexxDiscountTagPresentInTheForm(query, "eValue Discount"));
				//PAS-1549 Start
				CustomAssert.assertTrue("13.5%".equals(DocGenHelper.getDocumentDataElemByName("eValDiscAmt", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
				//PAS-1549 End
			} else {
				CustomAssert.assertFalse(ahdexxDiscountTagPresentInTheForm(query, "eValue Discount"));
			}

			if (isPaperlessDiscDataPresent) {
				CustomAssert.assertTrue("Y"
						.equals(DocGenHelper.getDocumentDataElemByName("PapPrefDiscYN", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
			} else {
				if (!DocGenHelper.getDocumentDataElemByName("PapPrefDiscYN", DocGenEnum.Documents.AHDEXX, query).isEmpty()) {
					CustomAssert.assertTrue("N".equals(DocGenHelper.getDocumentDataElemByName("PapPrefDiscYN", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
				}
			}

			if (isPaperlessDlvryDataPresent) {
				CustomAssert.assertTrue("Y"
						.equals(DocGenHelper.getDocumentDataElemByName("PaplssDlvryYN", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
			} else {
				if (!DocGenHelper.getDocumentDataElemByName("PaplssDlvryYN", DocGenEnum.Documents.AHDEXX, query).isEmpty()) {
					CustomAssert.assertTrue("N".equals(DocGenHelper.getDocumentDataElemByName("PaplssDlvryYN", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
				}
			}
		} else {
			CustomAssert.assertFalse(DBService.get().getValue(query).isPresent());
		}
	}

	private void ahdexxGeneratedCheck(boolean isGenerated, String policyNumber, int numberOfDocuments) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDEXX", "MEMBERSHIP_VALIDATE");
		String query2 = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "AHDEXX", "MEMBERSHIP_VALIDATE");
		if (isGenerated) {
			CustomAssert.assertTrue(DBService.get().getValue(query).isPresent());
			CustomAssert.assertEquals(Integer.parseInt(DBService.get().getValue(query2).get()), numberOfDocuments);
		} else {
			CustomAssert.assertFalse(DBService.get().getValue(query).isPresent());
		}
	}

	private void ahdexxContentCheck(String membershipEligibilitySwitch, String policyNumber) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDEXX", "MEMBERSHIP_VALIDATE");

		CustomAssert.assertTrue("Membership discount tag problem", "5.0%".equals(DocGenHelper.getDocumentDataElemByName("AAAMemDiscAmt", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
		CustomAssert.assertTrue(DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDEXX, query).get(0).toString().contains("AAA Membership Discount"));
		CustomAssert.assertTrue(ahdexxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
		if ("TRUE".equals(membershipEligibilitySwitch)) {
			PremiumAndCoveragesTab.tableEValueMessages.getRow(1).getCell(1).verify.value(MESSAGE_INFO_1);
			PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).verify.contains(MESSAGE_BULLET_8);
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("No");
			CustomAssert.assertTrue("eValue discount tag problem", "13.5%"
					.equals(DocGenHelper.getDocumentDataElemByName("eValDiscAmt", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			CustomAssert.assertTrue(DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDEXX, query).get(0).toString().contains("eValue Discount"));
			CustomAssert.assertTrue(ahdexxDiscountTagPresentInTheForm(query, "eValue Discount"));
		} else {
			PremiumAndCoveragesTab.tableEValueMessages.getRow(1).getCell(1).verify.value(MESSAGE_INFO_4);
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("Yes");
			CustomAssert.assertFalse(ahdexxDiscountTagPresentInTheForm(query, "eValue Discount"));
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

	private String createPaperlessPreferencesRequestId(String policyNumber, String scenarioJsonFile) {
		String requestId = HelperWireMockPaperlessPreferences.setPaperlessPreferencesToValue(policyNumber, scenarioJsonFile);
		requestIdList.add(requestId);
		printToLog("THE REQUEST ID WAS CREATED " + requestId);
		return requestId;
	}

	private void deleteMultiplePaperlessPreferencesRequests() {
		for (Object requestId : requestIdList) {
			HelperWireMockPaperlessPreferences.deleteProcessedRequestFromStub(requestId.toString());
			printToLog("MULTIPLE REQUEST DELETION WAS EXECUTED for " + requestId);
		}
		requestIdList.clear();
	}

	private void deleteSinglePaperlessPreferenceRequest(String requestId) {
		HelperWireMockPaperlessPreferences.deleteProcessedRequestFromStub(requestId);
		requestIdList.remove(requestId);
		printToLog("DELETE SINGLE REQUEST WAS EXECUTED for " + requestId);
	}
}
