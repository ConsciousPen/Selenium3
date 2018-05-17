/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.ho3.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
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
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.TestEValueMembershipProcessPreConditions;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class TestEValueMembershipProcess extends HomeSSHO3BaseTest implements TestEValueMembershipProcessPreConditions {

	private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
	private static List<String> requestIdList = new LinkedList<>();
	private Random random = new Random();
	private ApplicantTab applicantTab = new ApplicantTab();
	private UnderwritingAndApprovalTab underwritingAndApprovalTab = new UnderwritingAndApprovalTab();
	private PremiumsAndCoveragesQuoteTab premiumAndCoveragesTab = new PremiumsAndCoveragesQuoteTab();
	private ReportsTab reportsTab = new ReportsTab();
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

	/**
	 * @author Oleg Stasyuk
	 * @name Test Membership Discount is removed for membership status = Active.
	 * @scenario
	 * 0. Check email record present in admin log (no "eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is not removed on NB+30
	 * 3. Check AHDRXX is not produced on NB+30
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = {"PAS-356", "PAS-2872", "PAS-312"})
	public void pas356_membershipEligibilityConfigurationTrueForActiveMembership(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipPolicyCreation("Active");

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		eValueDiscountStatusCheck(policyNumber, "");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		eValueDiscountStatusCheck(policyNumber, "");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");
		checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Membership Discount is removed for membership status = Pending.
	 * @scenario
	 * 0. Check email record present in admin log (no "eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is not removed on NB+30
	 * 3. Check AHDRXX is not produced on NB+30
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = {"PAS-356", "PAS-2872", "PAS-312"})
	public void pas356_membershipEligibilityConfigurationTrueForPendingMembership(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipPolicyCreation("Pending");

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		eValueDiscountStatusCheck(policyNumber, "");
		transactionHistoryRecordCountCheck(policyNumber, 1, "");

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		eValueDiscountStatusCheck(policyNumber, "");
		transactionHistoryRecordCountCheck(policyNumber, 2, "Membership Discount Removed");
		checkDocumentContentAHDRXX(policyNumber, true, true, false, false, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Membership Discount is removed for membership status = Cancelled.
	 * @scenario
	 * 0. Check email record present in admin log (no "eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is removed on NB+30
	 * 3. Check AHDRXX is produced on NB+30
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = {"PAS-356", "PAS-2872", "PAS-312"})
	public void pas356_membershipEligibilityConfigurationTrueForCancelledMembership(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipPolicyCreation("Cancelled");

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		eValueDiscountStatusCheck(policyNumber, "");
		transactionHistoryRecordCountCheck(policyNumber, 2, "");

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		eValueDiscountStatusCheck(policyNumber, "");
		transactionHistoryRecordCountCheck(policyNumber, 3, "Membership Discount Removed");
		checkDocumentContentAHDRXX(policyNumber, true, true, false, false, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Membership Discount is removed for membership status = Not Active (Future Dated).
	 * @scenario
	 * 0. Check email record present in admin log (no "eValue Discount Pending Notification Url:") on NB+15
	 * 1. Check Membership discount is removed on NB+30
	 * 3. Check AHDRXX is produced on NB+30
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = {"PAS-356", "PAS-2872", "PAS-312"})
	public void pas356_membershipEligibilityConfigurationTrueForNotActiveMembership(@Optional("VA") String state) {
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipPolicyCreation("NotActive");

		CustomAssert.enableSoftMode();
		jobsNBplus15plus30runNoChecks();
		//implementEmailCheck from Admin Log?
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		eValueDiscountStatusCheck(policyNumber, "");
		transactionHistoryRecordCountCheck(policyNumber, 2, "");

		jobsNBplus15plus30runNoChecks();
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		eValueDiscountStatusCheck(policyNumber, "");

		transactionHistoryRecordCountCheck(policyNumber, 3, "Membership Discount Removed");
		checkDocumentContentAHDRXX(policyNumber, true, true, false, false, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void setMembershipAndRate(String membershipStatus, boolean eValueSet) {
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		if ("Active".equals(membershipStatus)) {
			applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");
			applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER).setValue("5251111111111118");
		} else if ("Pending".equals(membershipStatus)) {
			applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Membership Pending");
			//applicantTab.getAssetList().getAsset(HomeSSMetaData.applicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.applicantTab.AAAMembership.MEMBERSHIP_NUMBER).setValue("4290061311384005");
		} else if ("Cancelled".equals(membershipStatus)) {
			applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");
			applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER).setValue("3111111111111121");
		} else {
			applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");//Membership Pending
			List<String> inactiveMembershipNumberList = new ArrayList<>();
			inactiveMembershipNumberList.add("4343433333333335");//FutureDated
			String randomInactiveMembershipNumber = inactiveMembershipNumberList.get(random.nextInt(inactiveMembershipNumberList.size()));
			printToLog("Value used " + randomInactiveMembershipNumber);
			applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER)
					.setValue(randomInactiveMembershipNumber);
		}

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.REPORTS.get());
		reportsTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
		if(!"Pending".equals(membershipStatus)) {
			reportsTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(HomeSSMetaData.ReportsTab.AaaMembershipReportRow.REPORT.getLabel()).controls.links.get(1).click();
		}
		if (reportsTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).isPresent()) {
			if ("".equals(reportsTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(HomeSSMetaData.ReportsTab.AaaMembershipReportRow.MEMBER_SINCE_DATE.getLabel()).getValue())) {

				reportsTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1).getCell(8).controls.links.get(1).click();

				reportsTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT)
						.getAsset(HomeSSMetaData.ReportsTab.AaaMembershipReportRow.ADD_MEMBER_SINCE_DIALOG.getLabel(), AssetList.class)
						.getAsset(HomeSSMetaData.ReportsTab.AddMemberSinceDialog.MEMBER_SINCE.getLabel(), TextBox.class).setValue("11/14/2016");

				Page.dialogConfirmation.confirm();
			}
			if (Page.dialogConfirmation.isPresent()) {
				Page.dialogConfirmation.reject();
			}

			printToLog("Membership number used: " + reportsTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(HomeSSMetaData.ReportsTab.AaaMembershipReportRow.MEMBERSHIP_NO.getLabel()).getValue());
			printToLog("Member Since Date used or returned: " + reportsTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(HomeSSMetaData.ReportsTab.AaaMembershipReportRow.MEMBER_SINCE_DATE.getLabel()).getValue());
			printToLog("Membership Status returned: " + reportsTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(HomeSSMetaData.ReportsTab.AaaMembershipReportRow.STATUS.getLabel()).getValue());
		}
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		//TODO eValue is currently not applicable for HO_SS
		/*		if (eValueSet) {
			premiumAndCoveragesTab.getAssetList().getAsset(HomeSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		} else {
			if (premiumAndCoveragesTab.getAssetList().getAsset(HomeSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).isPresent()) {
				premiumAndCoveragesTab.getAssetList().getAsset(HomeSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
			}
		}*/
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
	}

	private String membershipPolicyCreation(String status) {
		mainApp().open();
		getCopiedQuote();

		//SearchPage.openQuote("QVAH3952918547");

		policy.dataGather().start();
		setMembershipAndRate(status, false);

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
		if (underwritingAndApprovalTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.UNDERWRITER_SELECTED_INSPECTION_TYPE).isPresent() && underwritingAndApprovalTab.getAssetList()
				.getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.UNDERWRITER_SELECTED_INSPECTION_TYPE).isVisible()) {
			underwritingAndApprovalTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.UNDERWRITER_SELECTED_INSPECTION_TYPE).setValue("index=1");
		}
		policy.dataGather().getView().fillFromTo(getPolicyDefaultTD(), UnderwritingAndApprovalTab.class, BindTab.class, true);
		premiumAndCoveragesTab.saveAndExit();

		return testEValueDiscount.simplifiedQuoteIssue();
	}

	//@Test
	private static void jobsNBplus15plus30runNoChecks() {
		jobsNBplus15plus30runNoChecks(DateTimeUtils.getCurrentDateTime().plusDays(15));
	}

	private static void jobsNBplus15plus30runNoChecks(LocalDateTime dateToShiftTo) {
		TimeSetterUtil.getInstance().nextPhase(dateToShiftTo);
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

	private void eValueDiscountStatusCheck(String policyNumber, String status) {
		String getEvalueStatusSQL = "select evalueStatus from (\n"
				+ "  select ps.policynumber, emd.evaluestatus\n"
				+ "  from PolicySummary ps, AAAEMemberDetailsEntity emd\n"
				+ "  where ps.ememberdetail_id = emd.id\n"
				+ "  and ps.policynumber = '%s'\n"
				+ "  order by emd.id desc)\n"
				+ "where rownum = 1";
		CustomAssert.assertEquals(status, DBService.get().getValue(String.format(getEvalueStatusSQL, policyNumber)).orElse(""));
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

		String transactionHistoryQuery = "select * from(\n"
				+ "select pt.TXREASONTEXT\n"
				+ "from PolicyTransaction pt\n"
				+ "where POLICYID in \n"
				+ "        (select id from POLICYSUMMARY \n"
				+ "        where POLICYNUMBER = '%s')\n"
				+ "    order by pt.TXDATE desc)\n"
				+ "    where rownum=1";
		assertThat(DBService.get().getValue(String.format(transactionHistoryQuery, policyNumber)).orElse(StringUtils.EMPTY)).isEqualTo(value);
	}

	private void lastTransactionHistoryExit() {
		if (Tab.buttonCancel.isPresent()) {
			premiumAndCoveragesTab.cancel();
			if (Tab.buttonCancel.isPresent()) {
				Tab.buttonCancel.click();
			}
		}
	}

	private boolean ahdrxxDiscountTagPresentInTheForm(String query, String discountTag) {
		return DocGenHelper.getDocumentDataElemByName("DiscNm", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().toString().contains(discountTag);
	}

	private void checkDocumentContentAHDRXX(String policyNumber, boolean isGenerated, boolean isMembershipDataPresent, boolean isEvalueDataPresent, boolean isPaperlessDiscDataPresent,
			boolean isPaperlessDlvryDataPresent) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDRXX", "ENDORSEMENT_ISSUE");

		if (isGenerated) {
			if (isMembershipDataPresent) {
				CustomAssert.assertTrue(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Discount"));
				//PAS-1549, PAS-2872} Start
				CustomAssert.assertTrue("5.0%"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemDiscAmt", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
				CustomAssert.assertTrue("Y"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
				//PAS-1549, PAS-2872 End
			} else {
				CustomAssert.assertFalse(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Advantage Program"));
				CustomAssert.assertTrue("N"
						.equals(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()));
			}

			if (isEvalueDataPresent) {
				CustomAssert.assertTrue(ahdrxxDiscountTagPresentInTheForm(query, "eValue Discount"));
				//PAS-1549, PAS-310 Start
				CustomAssert.assertTrue("13.5%"
						.equals(DocGenHelper.getDocumentDataElemByName("eValDiscAmt", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
								.getTextField()));
				//PAS-1549, PAS-310 Start
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
}
