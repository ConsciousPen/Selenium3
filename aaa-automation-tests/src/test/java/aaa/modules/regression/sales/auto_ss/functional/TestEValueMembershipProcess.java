/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
* CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.Tab;
import aaa.common.components.Efolder;
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
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.TestEValueMembershipProcessPreConditions;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class TestEValueMembershipProcess extends AutoSSBaseTest implements TestEValueMembershipProcessPreConditions {

	private static final String E_VALUE_DISCOUNT = "eValue Discount"; //PAS-440 - rumors have it, that discount might be renamed
	private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
	private static final String MESSAGE_INFO_1 = "This customer is not eligible for eValue discount due to one or more of the following reasons:";
	private static final String MESSAGE_BULLET_8 = "Does not have an active AAA membership";
	private static final String MESSAGE_INFO_4 = "eValue Discount Requirements:";

	private Random random = new Random();
	private GeneralTab generalTab = new GeneralTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab(); //TODO test with policy.dataGather().getView().getTab(DocumentsAndBindTab.class); instead of new Tab();
	private ErrorTab errorTab = new ErrorTab();
	private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();

	@Test(description = "Check membership endpoint")
	public static void retrieveMembershipSummaryEndpointCheck() {
		CustomAssert.assertTrue("retrieveMembershipSummary doesn't use stub endpoint. Please run retrieveMembershipSummaryStubEndpointUpdate", DBService.get()
				.getValue(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK).get().contains(APP_HOST));
	}

	@Test(description = "Renewal job adding")
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
			log.info("INSERT MEMBERSHIP ELIGIBILITY CONFIGURATION IS REQUIRED");
		}
		if (DBService.get().getValue(MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_CHECK).get().equals(membershipEligibilitySwitch)) {
			log.info("MEMBERSHIP ELIGIBILITY CONFIGURATION IS CORRECT, NO UPDATES REQUIRED");
		} else {
			DBService.get().executeUpdate(String.format(MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_UPDATE, membershipEligibilitySwitch));
			log.info("UPDATE OF MEMBERSHIP ELIGIBILITY CONFIGURATION to {} was executed here", membershipEligibilitySwitch);
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
		}
	}

	//@Test
	private void postconditionMembershipEligibilityCheck() {
		String eligibilitySwitch = "TRUE";
		DBService.get().executeUpdate(String.format(MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_UPDATE, eligibilitySwitch));
		log.info("UPDATE OF MEMBERSHIP ELIGIBILITY CONFIGURATION To TRUE WAS EXECUTED HERE");
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
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
		preconditionMembershipEligibilityCheck(membershipDiscountEligibilitySwitch);

		membershipEligibilityPolicyCreation("Active");
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(1);
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(false, "already activated by previous job");
		transactionHistoryRecordCountCheck(1);
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
		preconditionMembershipEligibilityCheck(membershipDiscountEligibilitySwitch);

		membershipEligibilityPolicyCreation("Pending");
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(false, "no records created");
		transactionHistoryRecordCountCheck(1);
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "INACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "INACTIVE");
		transactionHistoryRecordCountCheck(2);
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
		preconditionMembershipEligibilityCheck(membershipDiscountEligibilitySwitch);

		membershipEligibilityPolicyCreation("Non-Active");
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(true, "Membership information was updated for the policy based on best membership logic");
		transactionHistoryRecordCountCheck(2);
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "INACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "INACTIVE");
		transactionHistoryRecordCountCheck(3);
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
		mainApp().open();
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1));

		String membershipDiscountEligibilitySwitch = "FALSE";
		preconditionMembershipEligibilityCheck(membershipDiscountEligibilitySwitch);

		membershipEligibilityPolicyCreation("Active");
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(1);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(false, "already processed by previous job");
		transactionHistoryRecordCountCheck(1);
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
		mainApp().open();
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1));

		String membershipDiscountEligibilitySwitch = "FALSE";
		preconditionMembershipEligibilityCheck(membershipDiscountEligibilitySwitch);

		membershipEligibilityPolicyCreation("Pending");
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(1);

		jobsNBplus15plus30runNoChecks();
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(2);
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
		mainApp().open();
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1));

		String membershipDiscountEligibilitySwitch = "FALSE";
		preconditionMembershipEligibilityCheck(membershipDiscountEligibilitySwitch);

		membershipEligibilityPolicyCreation("Non-Active");
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(2);
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "ACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "ACTIVE");
		transactionHistoryRecordCountCheck(3);
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

		String membershipEligibilitySwitch = "TRUE";
		String membershipStatusActive = "Cancelled";

		CustomAssert.enableSoftMode();
		renewalMembershipProcessCheck(membershipEligibilitySwitch, membershipStatusActive);
		postconditionMembershipEligibilityCheck();
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationFalseForCancelledMembershipRenewal(@Optional("VA") String state) {

		mainApp().open();
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1));

		String membershipEligibilitySwitch = "FALSE";
		String membershipStatusActive = "Cancelled";

		CustomAssert.enableSoftMode();
		renewalMembershipProcessCheck(membershipEligibilitySwitch, membershipStatusActive);
		postconditionMembershipEligibilityCheck();
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void renewalMembershipProcessCheck(String membershipEligibilitySwitch, String membershipStatus) {
		preconditionMembershipEligibilityCheck(membershipEligibilitySwitch);

		membershipEligibilityPolicyCreation(membershipStatus);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate); //-96
		LocalDateTime renewReportOrderingDate = getTimePoints().getRenewReportsDate(policyExpirationDate); //-63

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

		TimeSetterUtil.getInstance().nextPhase(renewReportOrderingDate);
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
		HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_MEMBERSHIP_SUMMARY_BATCH);
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchReceiveAsyncJob);

		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(48));
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
		HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_MEMBERSHIP_SUMMARY_BATCH);
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchReceiveAsyncJob);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDEXX", "MEMBERSHIP_VALIDATE");
		CustomAssert
				.assertTrue("5.0%".equals(DocGenHelper.getDocumentDataElemByName("AAAMemDiscAmt", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()));
		CustomAssert.assertTrue(DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDEXX, query).get(0).toString().contains("AAA Membership Discount"));
		CustomAssert.assertTrue(ahdexxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
		if ("TRUE".equals(membershipEligibilitySwitch)) {
			PremiumAndCoveragesTab.tableEValueMessages.getRow(1).getCell(1).verify.value(MESSAGE_INFO_1);
			PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).verify.contains(MESSAGE_BULLET_8);
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("No");
			CustomAssert.assertTrue("13.5%"
					.equals(DocGenHelper.getDocumentDataElemByName("eValDiscAmt", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			CustomAssert.assertTrue(DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDEXX, query).get(0).toString().contains("eValue Discount"));
			CustomAssert.assertTrue(ahdexxDiscountTagPresentInTheForm(query, "eValue Discount"));
		} else {
			PremiumAndCoveragesTab.tableEValueMessages.getRow(1).getCell(1).verify.value(MESSAGE_INFO_4);
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("Yes");
			CustomAssert.assertFalse(ahdexxDiscountTagPresentInTheForm(query, "eValue Discount"));
		}
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

	private boolean ahdrxxDiscountTagPresentInTheForm(String query, String discountTag) {
		return DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().toString().contains(discountTag);
	}

	private boolean ahdexxDiscountTagPresentInTheForm(String query, String discountTag) {
		return DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().toString().contains(discountTag);
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
		mainApp().open();
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1));

		String membershipDiscountEligibilitySwitch = "FALSE";
		preconditionMembershipEligibilityCheck(membershipDiscountEligibilitySwitch);

		membershipEligibilityPolicyCreation("Active");
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "PENDING");
		membershipLogicActivitiesAndNotesCheck(false, "no changes");
		transactionHistoryRecordCountCheck(1);
		latestTransactionMembershipAndEvalueDiscountsCheck(true, true, membershipDiscountEligibilitySwitch);

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		eValueDiscountStatusCheck(policyNumber, "INACTIVE");
		membershipLogicActivitiesAndNotesCheck(true, "INACTIVE");
		transactionHistoryRecordCountCheck(2);
		//BUG Membership Discount infor printed in the doc
		latestTransactionMembershipAndEvalueDiscountsCheck(true, false, membershipDiscountEligibilitySwitch);
		//BUG PAS-7265 Paperless preference reason isn't displayed in AHDRXX document in case Paperless is Pending at NB+30
		checkDocumentContentAHDRXX(policyNumber, true, false, true, true, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void membershipEligibilityPolicyCreation(String membershipStatus) {
		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.eValueQuoteCreation();

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		if ("Active".equals(membershipStatus)) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("5251111111111118");
		} else if ("Pending".equals(membershipStatus)) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("");
		} else if ("Cancelled".equals(membershipStatus)) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("3111111111111121");
		} else {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");//Membership Pending
			List<String> inactiveMembershipNumberList = new ArrayList<>();
			//TODO check othe membership number for better randomization
			//inactiveMembershipNumberList.add("4333333333333457");//Expired                - bad
			//inactiveMembershipNumberList.add("9920702826992041");//Inactive               - error
			inactiveMembershipNumberList.add("4343433333333335");//FutureDated
			//inactiveMembershipNumberList.add("4222222222222123");//No Hit                   - error
			//inactiveMembershipNumberList.add("8800400000000002");//Lapsed                     - Active
			//inactiveMembershipNumberList.add("4290061311384005");//Pending                  //TODO updated to Active based on some logic
			//inactiveMembershipNumberList.add("4333333333333338");//Expired                     - error
			String randomInactiveMembershipNumber = inactiveMembershipNumberList.get(random.nextInt(inactiveMembershipNumberList.size()));
			log.info("Value used {}", randomInactiveMembershipNumber);
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

			log.info("Membership number used: {}", ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.MEMBERSHIP_NO.getLabel()).getValue());
			log.info("Member Since Date used or returned: {}", ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.MEMBER_SINCE_DATE.getLabel()).getValue());
			log.info("Membership Status returned: {}", ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.STATUS.getLabel()).getValue());
		}
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		testEValueDiscount.simplifiedQuoteIssue();
	}

	//@Test
	private void jobsNBplus15plus30runNoChecks() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(15));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
		JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
		//JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
		JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
		//JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
		//BUG PAS-6162 automatedProcessingBypassingAndErrorsReportGenerationJob is failing with Error, failed to retrieve 'placeholder' Report Entity
		//JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
	}

	private void eValueDiscountStatusCheck(String policyNumber, String status) {
		String getEvalueStatusSQL = "select evalueStatus from (\n"
				+ "    select ps.policynumber, emd.evaluestatus\n"
				+ "    from PolicySummary ps, AAAEMemberDetailsEntity emd\n"
				+ "    where ps.ememberdetail_id = emd.id\n"
				+ "    and ps.policynumber = '%s'\n"
				+ "    order by emd.id desc)\n"
				+ "where rownum = 1";
		CustomAssert.assertEquals(DBService.get().getValue(String.format(getEvalueStatusSQL, policyNumber)).get(), status);
	}

	private void membershipLogicActivitiesAndNotesCheck(boolean presence, String status) {
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		if (presence) {
			//TODO check apostrophe in the message
/*			String membershipLogicNote = "Evalue information / Status was updated as : '" + status + "' for the policy based on Preferences and Membership logic.";
			NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", membershipLogicNote).getCell("Date/Time").verify
					.contains(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));*/
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

	private void transactionHistoryRecordCountCheck(int rowCount) {
		PolicySummaryPage.buttonTransactionHistory.click();
		CustomAssert.assertEquals(PolicySummaryPage.tableTransactionHistory.getRowsCount(), rowCount);
	}

	private void latestTransactionMembershipAndEvalueDiscountsCheck(boolean membershipDiscountPresent, boolean eValueDiscountPresent, String membershipEligibilitySwitch) {
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
			if ("TRUE".equals(membershipEligibilitySwitch)) {
				PremiumAndCoveragesTab.tableEValueMessages.getRow(1).getCell(1).verify.value(MESSAGE_INFO_1);
				PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).verify.contains(MESSAGE_BULLET_8);
			}
			lastTransactionHistoryExit();
		}
	}

	private void checkDocumentContentAHDRXX(String policyNumber, boolean isGenerated, boolean isMembershipDataPresent, boolean isEvalueDataPresent, boolean isPaperlessDiscDataPresent, boolean isPaperlessDlvryDataPresent) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDRXX", "ENDORSEMENT_ISSUE");

		if (isGenerated) {
			lastTransactionHistoryExit();
			Efolder.isDocumentExist("Endorsement", "DISCOUNT REMOVED");

			if (isMembershipDataPresent) {
				CustomAssert.assertTrue(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
				CustomAssert.assertTrue("5.0%"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemDiscAmt", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
				CustomAssert.assertTrue("Y"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			} else {
				CustomAssert.assertFalse(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
				CustomAssert.assertTrue("N"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			}

			if (isEvalueDataPresent) {
				CustomAssert.assertTrue(ahdrxxDiscountTagPresentInTheForm(query, "eValue Discount"));
				CustomAssert.assertTrue("13.5%"
						.equals(DocGenHelper.getDocumentDataElemByName("eValDiscAmt", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
			} else {
				CustomAssert.assertFalse(ahdrxxDiscountTagPresentInTheForm(query, "eValue Discount"));
			}

			if (isPaperlessDiscDataPresent){
				CustomAssert.assertTrue("Y"
						.equals(DocGenHelper.getDocumentDataElemByName("PapPrefDiscYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			} else {
				CustomAssert.assertTrue("N"
						.equals(DocGenHelper.getDocumentDataElemByName("PapPrefDiscYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			}

			if (isPaperlessDlvryDataPresent){
				CustomAssert.assertTrue("Y"
						.equals(DocGenHelper.getDocumentDataElemByName("PaplssDlvryYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			} else {
				CustomAssert.assertTrue("N"
						.equals(DocGenHelper.getDocumentDataElemByName("PaplssDlvryYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			}

		} else {
			//BUG PAS-7149 AHDRXX is generated when MembershipEligibility=FALSE and eValue discount is not removed
			CustomAssert.assertFalse(DBService.get().getValue(query).isPresent());
		}
	}
}
