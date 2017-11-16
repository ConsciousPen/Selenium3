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
	private static final String MESSAGE_INFO_1 = "This customer is not eligible for the eValue discount due to the following reason(s):";
	private static final String MESSAGE_BULLET_8 = "Does not have an active AAA membership";
	private static final String MESSAGE_INFO_2 ="This customer is eligible for the eValue discount, but the following steps must be completed in order to bind.";

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

		membershipEligibilityConfiguration("TRUE", "Active", true, true);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationTrueForPendingMembership(@Optional("VA") String state) {

		membershipEligibilityConfiguration("TRUE", "Pending", true, true);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationTrueForNotActiveMembership(@Optional("VA") String state) {

		membershipEligibilityConfiguration("TRUE", "Not Active", false, false);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationFalseForActiveMembership(@Optional("VA") String state) {

		membershipEligibilityConfiguration("FALSE", "Active", true, true);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationFalseForPendingMembership(@Optional("VA") String state) {

		membershipEligibilityConfiguration("FALSE", "Pending", false, true);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationFalseForNotActiveMembership(@Optional("VA") String state) {

		membershipEligibilityConfiguration("FALSE", "Not Active", false, true);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationTrueForCancelledMembershipRenewal(@Optional("VA") String state) {

		String membershipEligibilitySwitch = "TRUE";
		String membershipStatusActive = "Cancelled";

		renewalMembershipProcessCheck(membershipEligibilitySwitch, membershipStatusActive);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_membershipEligibilityConfigurationFalseForCancelledMembershipRenewal(@Optional("VA") String state) {

		String membershipEligibilitySwitch = "FALSE";
		String membershipStatusActive = "Cancelled";

		renewalMembershipProcessCheck(membershipEligibilitySwitch, membershipStatusActive);
	}

	private void renewalMembershipProcessCheck(String membershipEligibilitySwitch, String membershipStatusActive) {
		preconditionMembershipEligibilityCheck(membershipEligibilitySwitch);

		membershipEligibilityPolicyCreation(membershipStatusActive);
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
		if("TRUE".equals(membershipEligibilitySwitch)) {
			PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).verify.value(MESSAGE_INFO_1);
			PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).verify.contains(MESSAGE_BULLET_8);
			premiumAndCoveragesTab.getInquiryAssetList().getStaticElement("Apply eValue Discount").verify.value("No");
			CustomAssert.assertTrue("13.5%"
					.equals(DocGenHelper.getDocumentDataElemByName("eValDiscAmt", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			CustomAssert.assertTrue(DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDEXX, query).get(0).toString().contains("eValue Discount"));
			CustomAssert.assertTrue(ahdexxDiscountTagPresentInTheForm(query, "eValue Discount"));
		} else {
			PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).verify.value(MESSAGE_INFO_2);
			premiumAndCoveragesTab.getInquiryAssetList().getStaticElement("Apply eValue Discount").verify.value("Yes");
			CustomAssert.assertFalse("13.5%"
					.equals(DocGenHelper.getDocumentDataElemByName("eValDiscAmt", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			CustomAssert.assertFalse(DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDEXX, query).get(0).toString().contains("eValue Discount"));
			CustomAssert.assertFalse(ahdexxDiscountTagPresentInTheForm(query, "eValue Discount"));
		}
	}

	private void membershipEligibilityConfiguration(String membershipEligibilitySwitch, String membershipStatusActive, boolean membershipDiscountPresent, boolean eValueDiscountPresent) {

		preconditionMembershipEligibilityCheck(membershipEligibilitySwitch);
		membershipEligibilityPolicyCreation(membershipStatusActive);

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		jobsNBplus15plus30(policyNumber, "NBplus15", membershipStatusActive, membershipStatusActive);

		jobsNBplus15plus30(policyNumber, "NBplus30", membershipStatusActive, membershipStatusActive);

		lastTransactionHistoryOpen();

		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDRXX", "ENDORSEMENT_ISSUE");

		if ("Not Active".equals(membershipStatusActive)) {
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
			generalTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel()).verify.value("No");
		}
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		if (membershipDiscountPresent) {
			CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount"));
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "AAA Membership Discount").getCell(2).verify.value("Yes");
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
			if (DBService.get().getValue(query).isPresent()) {
				CustomAssert.assertFalse(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
			}
		} else {
			CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount"));
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "AAA Membership Discount").getCell(3).verify.value("None");
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
			CustomAssert.assertTrue(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
			lastTransactionHistoryExit();
			Efolder.isDocumentExist("Endorsement", "DISCOUNT REMOVED");
		}

		if (eValueDiscountPresent) {
			lastTransactionHistoryOpen();
			CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
			premiumAndCoveragesTab.getInquiryAssetList().getStaticElement("Apply eValue Discount").verify.value("Yes");
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, E_VALUE_DISCOUNT).getCell(5).verify.value("Yes");
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
			if (DBService.get().getValue(query).isPresent()) {
				CustomAssert.assertFalse(ahdrxxDiscountTagPresentInTheForm(query, "eValue Discount"));
			}
		} else {
			lastTransactionHistoryOpen();
			CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
			premiumAndCoveragesTab.getInquiryAssetList().getStaticElement("Apply eValue Discount").verify.value("No");
			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, "eValue Discount").getCell(6).verify.value("None");
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
			if ("TRUE".equals(membershipEligibilitySwitch)) {
				PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).verify.value(MESSAGE_INFO_1);
				PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).verify.contains(MESSAGE_BULLET_8);
				CustomAssert.assertTrue(ahdrxxDiscountTagPresentInTheForm(query, "eValue Discount"));
				CustomAssert.assertTrue("13.5%"
						.equals(DocGenHelper.getDocumentDataElemByName("eValDiscAmt", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
				CustomAssert.assertTrue(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
				CustomAssert.assertTrue("5.0%"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemDiscAmt", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
				lastTransactionHistoryExit();
				Efolder.isDocumentExist("Endorsement", "DISCOUNT REMOVED");
			} else {
				CustomAssert.assertFalse(ahdrxxDiscountTagPresentInTheForm(query, "eValue Discount"));
				CustomAssert.assertTrue(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
				lastTransactionHistoryExit();
				Efolder.isDocumentExist("Endorsement", "DISCOUNT REMOVED");
			}
		}

		postconditionMembershipEligibilityCheck();
	}

	private void lastTransactionHistoryExit() {
		premiumAndCoveragesTab.cancel();
		Tab.buttonCancel.click();
	}


	private void lastTransactionHistoryOpen() {
		if(PolicySummaryPage.buttonTransactionHistory.isPresent()) {
			PolicySummaryPage.buttonTransactionHistory.click();
			PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ENDORSEMENT);
			PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).controls.links.get(1).click();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		}
	}

	private boolean ahdrxxDiscountTagPresentInTheForm(String query, String discountTag) {
		return DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().toString().contains(discountTag);
	}

	private boolean ahdexxDiscountTagPresentInTheForm(String query, String discountTag) {
		return DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDEXX, query).get(0).getDocumentDataElements().toString().contains(discountTag);
	}

	private void membershipEligibilityPolicyCreation(String membershipStatusActive) {
		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.eValueQuoteCreation();

		//CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		if ("Active".equals(membershipStatusActive)) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("5251111111111118");
		} else if ("Pending".equals(membershipStatusActive)) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("");
		}else if("Cancelled".equals(membershipStatusActive)) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("3111111111111121");
		}else {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");//Membership Pending
			List<String> inactiveMembershipNumberList = new ArrayList<>();
			//inactiveMembershipNumberList.add("4333333333333457");//Expired                - bad no membersince
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
		if ("".equals(ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
				.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.MEMBER_SINCE_DATE.getLabel()).getValue())) {
			ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ACTION.getLabel()).controls.links.get(1).click();
			ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.MEMBER_SINCE).setValue("11/14/2016");
			Page.dialogConfirmation.confirm();
		}
		log.info("Membership number used: ()", ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
				.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.MEMBERSHIP_NO.getLabel()).getValue());
		log.info("Member Since Date used or returned: ()", ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
				.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.MEMBER_SINCE_DATE.getLabel()).getValue());
		log.info("Membership Status returned: ()", ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
				.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.STATUS.getLabel()).getValue());

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		testEValueDiscount.simplifiedQuoteIssue();
	}

	private void jobsNBplus15plus30(String policyNumber, String plusX, String membershipSetToActive, String membershipOriginallyActive) {
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		//NB+30 jobs
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(15));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);

		JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		//BUG PAS-3691 Notes that are created by NB+30 jobs are incorrect
		String descriptionTask1 = "Task Created Complete or Cancel Pended Endorsement";
		String descriptionNote1 = "No message [automatedEndorsementInit]";
		if ("Not Active".equals(membershipOriginallyActive) || "NBplus15".equals(plusX)) {
			CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(2).getCell("Description").getValue().contains(descriptionTask1));
			CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description").getValue().contains(descriptionNote1));
		}
		//TODO check after Membership is refactored if conditions are changed for pas3697_membershipEligibilityConfigurationFalseForInactiveMembership
		/*if ("NBplus15".equals(plusX)) {
			if (membershipOriginallyActive) {
				String partVariable;
				if (membershipSetToActive) {
					partVariable = "ACTIVE";
					String membershipLogicNote = "Evalue information / Status was updated as : '" + partVariable + "' for the policy based on Preferences and Membership logic.";
					CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(4).getCell("Description").getValue().contains(membershipLogicNote));
				}
				String membershipLogicNote = "Membership information was updated for the policy based on best membership logic.";
				CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(3).getCell("Description").getValue().contains(membershipLogicNote));
			}
		} else {
			if (!membershipOriginallyActive) {
				String partVariable;
				if (membershipSetToActive) {
					partVariable = "ACTIVE";
				} else {
					partVariable = "INACTIVE";
				}
				String membershipLogicNote = "Evalue information / Status was updated as : '" + partVariable + "' for the policy based on Preferences and Membership logic.";
				CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(3).getCell("Description").getValue().contains(membershipLogicNote));
			}
		}*/
		PolicySummaryPage.buttonPendedEndorsement.verify.present();

		JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		String descriptionNote2 = "No message [automatedEndorsementRate]";
		if ("Not Active".equals(membershipOriginallyActive)) {
			CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description").getValue().contains(descriptionNote2));
		}
		//JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
		//nothing from UI side happens here to check

		JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		String descriptionNote33 = "No message [automatedEndorsementIssue]";
		String descriptionTask3 = "Complete Task Complete or Cancel Pended Endorsement";
		String descriptionNote31 = "Bind Endorsement effective ";
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(3).getCell("Description").getValue().contains(descriptionNote33));
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(2).getCell("Description").getValue().contains(descriptionTask3));
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description").getValue().contains(descriptionNote31));

		//JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
		//BUG PAS-6162 automatedProcessingBypassingAndErrorsReportGenerationJob is failing with Error, failed to retrieve 'placeholder' Report Entity
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
