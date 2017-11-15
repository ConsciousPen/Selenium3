/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
* CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

public class TestEValueMembershipProcess extends AutoSSBaseTest {

	private static final String E_VALUE_DISCOUNT = "eValue Discount"; //PAS-440 - rumors have it, that discount might be renamed
	private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
	private static String messageInfo2 = "This customer is eligible for the eValue discount, but the following steps must be completed in order to bind.";
	private static String messageBullet4 = "Maintain continuous insurance coverage and bodily injury limits of $50,000/$100,000 or higher"; //$50,000/$100,000 is configurable
	private static String messageBullet10 = "Agree to and sign the eValue acknowledgement";
	private static String messageBullet2 = "Self-Service online using the MyPolicy site";

	private Random random = new Random();
	private GeneralTab generalTab = new GeneralTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab(); //TODO test with policy.dataGather().getView().getTab(DocumentsAndBindTab.class); instead of new Tab();
	private ErrorTab errorTab = new ErrorTab();
	private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();

	private static final String RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK = "select  value\n"
			+ "from PROPERTYCONFIGURERENTITY\n"
			+ "where propertyname ='retrieveMembershipSummaryServiceImpl.endpointRetrieveMembershipSummaryUri'";

	private static final String MEMBERSHIP_ELIGIBILITY_CHECK_FOR_VA_EXISTS = "SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
			+ "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAAeMemberQualifications')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'membershipEligibility'\n"
			+ "and EXPIRATION is null";

	private static final String MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_CHECK = "select displayValue from \n"
			+ "(SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
			+ "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAAeMemberQualifications')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'membershipEligibility'\n"
			+ "and EXPIRATION is null)";

	private static final String EVALUE_MEMBERSHIP_ELIGIBILITY_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			"(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n" +
			"values\n" +
			"('BaseProductLookupValue', 'membershipEligibility', 'TRUE', 'AAA_SS', 'VA', null, null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n";

	private static final String MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_UPDATE = "update LOOKUPVALUE\n"
			+ "set displayValue = '%s'\n"
			+ "WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAAeMemberQualifications')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'membershipEligibility'\n"
			+ "and EXPIRATION is null";

	@Test
	@TestInfo(isAuxiliary = true)
	public static void retrieveMembershipSummaryEndpointCheck() {
		CustomAssert.assertTrue("retrieveMembershipSummary doesn't use stub endpoint. Please run retrieveMembershipSummaryStubEndpointUpdate", DBService.get()
				.getValue(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK).get().contains(APP_HOST));
	}

	@Test
	@TestInfo(isAuxiliary = true)
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

	//TODO OSI: Blocked by Membership discount and eValue discount not resetting on NB+30

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount not shown for state where it is not configured
	 * @scenario 1. Create new eValue eligible quote but for the not eligible state (PA)
	 * 1.1. Check "Has the insured ever been enrolled in eValue?" is not shown for Non-Applicable state
	 * 2. Check eValue Discount field is not shown in P&C
	 * 3. Check eValue Discount field is not shown in Rating Details
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationTrueForActiveMembership(@Optional("VA") String state) {
		//precondJobAdding();

		membershipEligibilityConfiguration("TRUE", true, true, true);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationTrueForInactiveMembership(@Optional("VA") String state) {

		membershipEligibilityConfiguration("TRUE", false, false, false);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationFalseForActiveMembership(@Optional("VA") String state) {

		membershipEligibilityConfiguration("FALSE", true, true, true);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationFalseForInactiveMembership(@Optional("VA") String state) {

		membershipEligibilityConfiguration("FALSE", false, false, true);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationTrueForInactiveMembershipRenewal(@Optional("VA") String state) {

		String membershipEligibilitySwitch = "TRUE";
		boolean membershipActive = false;

		preconditionMembershipEligibilityCheck(membershipEligibilitySwitch);

		membershipEligibilityPolicyCreation(membershipActive);

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate); //-96
		LocalDateTime renewReportOrderingDate = getTimePoints().getRenewReportsDate(policyExpirationDate); //-63
		LocalDateTime renewCheckUWRulesDate = getTimePoints().getRenewCheckUWRules(policyExpirationDate); //-57
		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate); //-52
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate); //-35
		LocalDateTime renewOfferBillGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate); //-20

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

		TimeSetterUtil.getInstance().nextPhase(renewReportOrderingDate);
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderJob);

		TimeSetterUtil.getInstance().nextPhase(renewReportOrderingDate);
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchReceiveAsyncJob);

		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

	}

	private void membershipEligibilityConfiguration(String membershipEligibilitySwitch, boolean membershipActive, boolean membershipDiscountPresent, boolean evalueDiscountPresent) {

		preconditionMembershipEligibilityCheck(membershipEligibilitySwitch);

		membershipEligibilityPolicyCreation(membershipActive);

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		jobsNBplus15plus30(policyNumber);

		jobsNBplus15plus30(policyNumber);

		PolicySummaryPage.buttonTransactionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ENDORSEMENT);
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).controls.links.get(1).click();

		if (!membershipActive) {
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
			//TODO Defect
			PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "AAA Membership Discount").getCell(2).verify.value("None");
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		}

		if (evalueDiscountPresent) {
			CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
			premiumAndCoveragesTab.getInquiryAssetList().getStaticElement("Apply eValue Discount").verify.value("Yes");
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT).getCell(4).verify.value("Yes");
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		} else {
			CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
			premiumAndCoveragesTab.getInquiryAssetList().getStaticElement("Apply eValue Discount").verify.value("No");
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT).getCell(4).verify.value("None");
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
			if ("TRUE".equals(membershipEligibilitySwitch)) {
				String messageInfo1 = "This customer is not eligible for the eValue discount due to the following reason(s):";
				String messageBullet8 = "Does not have an active AAA membership";
				PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).verify.value(messageInfo1);
				PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).verify.contains(messageBullet8);
			}
		}

		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDRXX", "%");
		CustomAssert.assertTrue(DBService.get().getValue(query).isPresent());
		CustomAssert.assertTrue(DocGenHelper.getDocumentDataElemByName("AcctNum", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()
				.contains("Membership"));
		CustomAssert.assertTrue(DocGenHelper.getDocumentDataElemByName("AcctNum", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()
				.contains("eValue"));

		postconditionMembershipEligibilityCheck();
	}

	private void membershipEligibilityPolicyCreation(boolean membershipActive) {
		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		if (membershipActive) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("5251111111111118");
		} else {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");//Membership Pending
			List<String> inactiveMembershipNumberList = new ArrayList<>();
			inactiveMembershipNumberList.add("4333333333333457");//Expired                - bad no membersince
			//inactiveMembershipNumberList.add("9920702826992041");//Inactive               - error
			inactiveMembershipNumberList.add("4343433333333335");//FutureDated
			//inactiveMembershipNumberList.add("3111111111111121");//Cancelled
			inactiveMembershipNumberList.add("4222222222222123");//No Hit                   - bad no membersince
			inactiveMembershipNumberList.add("8800400000000002");//Lapsed
			inactiveMembershipNumberList.add("4290061311384005");//Pending                  - bad no membersince
			//inactiveMembershipNumberList.add("4333333333333338");//Expired                     - error
			String randomInactiveMembershipNumber = inactiveMembershipNumberList.get(random.nextInt(inactiveMembershipNumberList.size()));
			log.info("Value used {}", randomInactiveMembershipNumber);
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER)
					.setValue(randomInactiveMembershipNumber);
		}

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.ORDER_REPORT).click();
		if ("".equals(ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
				.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.MEMBER_SINCE_DATE.getLabel()).getValue())) {
			ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ACTION.getLabel()).controls.links.get(1).click();
			ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.MEMBER_SINCE).setValue("11/14/2016");
			Page.dialogConfirmation.confirm();
		}

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		testEValueDiscount.simplifiedQuoteIssue();
	}

	private void jobsNBplus15plus30(String policyNumber) {
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		//NB+30 jobs
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(15));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);

		JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		String membershipLogicNote = "Membership information was updated for the policy based on best membership logic.";
		String descriptionTask1 = "Task Created Complete or Cancel Pended Endorsement";
		String descriptionNote1 = "No message [automatedEndorsementInit]";
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(3).getCell("Description").getValue().contains(membershipLogicNote));
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(2).getCell("Description").getValue().contains(descriptionTask1));
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description").getValue().contains(descriptionNote1));
		//line 3 - Evalue information / Status was updated as : 'INACTIVE' for the policy based on Preferences and Membership logic.

		PolicySummaryPage.buttonPendedEndorsement.verify.present();
		JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		String descriptionNote2 = "No message [automatedEndorsementRate]";
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description").getValue().contains(descriptionNote2));

		JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
		//nothing from UI side happens here to check

		JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		String descriptionNote33 = "No message [automatedEndorsementIssue]";
		String descriptionTask3 = "Complete Task Complete or Cancel Pended Endorsement";
		String descriptionNote31 = "Bind Endorsement effective " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " for Policy " + policyNumber;
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(3).getCell("Description").getValue().contains(descriptionNote33));
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(2).getCell("Description").getValue().contains(descriptionTask3));
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description").getValue().contains(descriptionNote31));

		JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
		//JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
	}

	@Test
	private void preconditionMembershipEligibilityCheck(String membershipEligibilitySwitch) {
		if (!DBService.get().getValue(MEMBERSHIP_ELIGIBILITY_CHECK_FOR_VA_EXISTS).isPresent()) {
			DBService.get().executeUpdate(EVALUE_MEMBERSHIP_ELIGIBILITY_CONFIG_INSERT);
			log.info("INSERT MEMBERSHIP ELIGIBILITY CONFIGURATION IS REQUIRED");
		}
		if (DBService.get().getValue(MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_CHECK).get().equals(membershipEligibilitySwitch)) {
			log.info("MEMBERSHIP ELIGIBILITY CONFIGURATION IS CORRECT, NO UPDATES REQUIRED");
		} else {
			DBService.get().executeUpdate(String.format(MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_UPDATE, membershipEligibilitySwitch));
			log.info("UPDATE OF MEMBERSHIP ELIGIBILITY CONFIGURATION WAS EXECUTED HERE");
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
		}
	}

	@Test
	private void postconditionMembershipEligibilityCheck() {
		String eligibilitySwitch = "TRUE";
		DBService.get().executeUpdate(String.format(MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_UPDATE, eligibilitySwitch));
		log.info("UPDATE OF MEMBERSHIP ELIGIBILITY CONFIGURATION To TRUE WAS EXECUTED HERE");
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
	}

}
