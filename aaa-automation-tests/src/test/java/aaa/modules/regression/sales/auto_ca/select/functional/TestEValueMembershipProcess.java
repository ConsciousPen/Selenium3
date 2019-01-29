/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import aaa.config.CsaaTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.TestEValueMembershipProcessPreConditions;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;

public class TestEValueMembershipProcess extends AutoCaSelectBaseTest implements TestEValueMembershipProcessPreConditions {

	private static final String APP_HOST = PropertyProvider.getProperty(CsaaTestProperties.APP_HOST);
	private Random random = new Random();
	private GeneralTab generalTab = new GeneralTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

	public static void retrieveMembershipSummaryEndpointCheck() {
		assertThat(DBService.get().getValue(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK).orElse(""))
				.as("retrieveMembershipSummary doesn't use stub endpoint. Please run retrieveMembershipSummaryStubEndpointUpdate").contains(APP_HOST);
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
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = {"PAS-550", "PAS-2872", "PAS-312"})
	public void pas550_membershipEligibilityConfigurationTrueForActiveMembership(@Optional("CA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipPolicyCreation("Active");

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			eValueDiscountStatusCheck(policyNumber, "", softly);
			transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			eValueDiscountStatusCheck(policyNumber, "", softly);
			transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false, softly);
		});
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
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-550", "PAS-2872", "PAS-312"})
	public void pas550_membershipEligibilityConfigurationTrueForPendingMembership(@Optional("CA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipPolicyCreation("Pending");

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			eValueDiscountStatusCheck(policyNumber, "", softly);
			transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			eValueDiscountStatusCheck(policyNumber, "", softly);
			transactionHistoryRecordCountCheck(policyNumber, 1, "", softly);
			checkDocumentContentAHDRXX(policyNumber, false, false, false, false, false, softly);
		});
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
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-550", "PAS-2872", "PAS-312"})
	public void pas550_membershipEligibilityConfigurationTrueForCancelledMembership(@Optional("CA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipPolicyCreation("Cancelled");

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			eValueDiscountStatusCheck(policyNumber, "", softly);
			transactionHistoryRecordCountCheck(policyNumber, 2, "", softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			eValueDiscountStatusCheck(policyNumber, "", softly);
			transactionHistoryRecordCountCheck(policyNumber, 3, "Membership Discount Removed", softly);
			checkDocumentContentAHDRXX(policyNumber, true, true, false, false, false, softly);
		});
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
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-550", "PAS-2872", "PAS-312"})
	public void pas550_membershipEligibilityConfigurationTrueForNotActiveMembership(@Optional("CA") String state) {
		retrieveMembershipSummaryEndpointCheck();
		String membershipDiscountEligibilitySwitch = "TRUE";
		settingMembershipEligibilityConfig(membershipDiscountEligibilitySwitch);

		String policyNumber = membershipPolicyCreation("NotActive");

		CustomSoftAssertions.assertSoftly(softly -> {
			jobsNBplus15plus30runNoChecks();
			//implementEmailCheck from Admin Log?
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			eValueDiscountStatusCheck(policyNumber, "", softly);
			transactionHistoryRecordCountCheck(policyNumber, 2, "", softly);

			jobsNBplus15plus30runNoChecks();
			mainApp().reopen();
			SearchPage.openPolicy(policyNumber);
			eValueDiscountStatusCheck(policyNumber, "", softly);

			transactionHistoryRecordCountCheck(policyNumber, 3, "Membership Discount Removed", softly);
			checkDocumentContentAHDRXX(policyNumber, true, true, false, false, false, softly);
		});
	}

	private void setMembershipAndRate(String membershipStatus, boolean eValueSet) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		if ("Active".equals(membershipStatus)) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP).getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP).getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER).setValue("5251111111111118");
		} else if ("Pending".equals(membershipStatus)) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP).getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP).getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER).setValue("4290061311384005");
		} else if ("Cancelled".equals(membershipStatus)) {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP).getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP).getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER).setValue("3111111111111121");
		} else {
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP).getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");//Membership Pending
			List<String> inactiveMembershipNumberList = new ArrayList<>();
			inactiveMembershipNumberList.add("4343433333333335");//FutureDated
			String randomInactiveMembershipNumber = inactiveMembershipNumberList.get(random.nextInt(inactiveMembershipNumberList.size()));
			printToLog("Value used " + randomInactiveMembershipNumber);
			generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP).getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER)
					.setValue(randomInactiveMembershipNumber);
		}

		NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.MEMBERSHIP.get());
		ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.ORDER_REPORT).click();
		if (ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).isPresent()) {
			if (Page.dialogConfirmation.isPresent()) {
				Page.dialogConfirmation.reject();
			}

			printToLog("Membership number used: " + ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.MEMBERSHIP_NO.getLabel()).getValue());
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

	private String membershipPolicyCreation(String status) {
		mainApp().open();
		getCopiedQuote();
		policy.dataGather().start();
		setMembershipAndRate(status, false);
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

	private void eValueDiscountStatusCheck(String policyNumber, String status, ETCSCoreSoftAssertions softly) {
		String getEvalueStatusSQL = "select evalueStatus from (\n"
				+ "  select ps.policynumber, emd.evaluestatus\n"
				+ "  from PolicySummary ps, AAAEMemberDetailsEntity emd\n"
				+ "  where ps.ememberdetail_id = emd.id\n"
				+ "  and ps.policynumber = '%s'\n"
				+ "  order by emd.id desc)\n"
				+ "where rownum = 1";
		softly.assertThat(DBService.get().getValue(String.format(getEvalueStatusSQL, policyNumber)).orElse("")).isEqualTo(status);
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
			boolean isPaperlessDlvryDataPresent, ETCSCoreSoftAssertions softly) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDRXX", "ENDORSEMENT_ISSUE");

		if (isGenerated) {
			if (isMembershipDataPresent) {
				softly.assertThat(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Advantage Program")).isTrue();
				//PAS-1549, PAS-2872} Start
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("AAAMemDiscAmt", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()).isEqualTo("5%");
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField()).isEqualTo("Y");
				//PAS-1549, PAS-2872 End
			} else {
				softly.assertThat(ahdrxxDiscountTagPresentInTheForm(query, "AAA Membership Advantage Program")).isFalse();
				softly.assertThat(DocGenHelper.getDocumentDataElemByName("AAAMemYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0)
						.getDataElementChoice().getTextField()).isEqualTo("N");
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
			lastTransactionHistoryExit();
			//TODO OSI return the check when EM team confirms why the docs are generated with such a long delay - INC0655981
			//Efolder.isDocumentExist("Endorsement", "Discount Removed");
		} else {
			//BUG PAS-7149 AHDRXX is generated when MembershipEligibility=FALSE and eValue discount is not removed
			softly.assertThat(DBService.get().getValue(query)).isNotPresent();
		}
	}
}
