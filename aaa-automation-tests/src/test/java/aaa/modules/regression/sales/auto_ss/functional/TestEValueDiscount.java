/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;


import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.components.Efolder;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.DbAwaitHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.openqa.selenium.By;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.Dollar;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.waiters.Waiters;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;

public class TestEValueDiscount extends AutoSSBaseTest {

	private static final String E_VALUE_DISCOUNT = "eValue Discount"; //PAS-440 - rumors have it, that discount might be renamed

	private static String messageInfo2 = "This customer is eligible for the eValue discount, but the following steps must be completed in order to bind.";
	private static String messageBullet4 = "Maintain continuous insurance coverage and bodily injury limits of $50,000/$100,000 or higher"; //$50,000/$100,000 is configurable
	private static String messageBullet10 = "Agree to and sign the eValue acknowledgement";
	private static String messageBullet2 = "Self-Service online using the MyPolicy site";

	private GeneralTab generalTab = new GeneralTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab(); //TODO test with policy.dataGather().getView().getTab(DocumentsAndBindTab.class); instead of new Tab();
	private ErrorTab errorTab = new ErrorTab();

	private static final String EVALUE_CONFIGURATION_PER_STATE_CHECK = "select dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id from LOOKUPVALUE\n" +
			" where lookuplist_id = \n" +
			" (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup')\n" +
			" and CODE = 'eMember'\n" +
			" and RISKSTATECD = '%s'";

	private static final String PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_CHECK = "select dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id from LOOKUPVALUE\n" +
			" where lookuplist_id = \n" +
			" (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup')\n" +
			" and CODE = 'PaperlessPreferences'\n" +
			" and RISKSTATECD = '%s'";

	private static final String EVALUE_CONFIGURATION_PER_STATE_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			" (dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id)\n" +
			" values\n" +
			" ('AAARolloutEligibilityLookupValue', 'eMember', 'TRUE', 'AAA_SS', '%s', null, null, null,\n" +
			" (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	private static final String PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			" (dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id)\n" +
			" values\n" +
			" ('AAARolloutEligibilityLookupValue', 'PaperlessPreferences', 'TRUE', 'AAA_SS', '%s', null, null, null,\n" +
			" (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";



	private static final String EVALUE_CURRENT_BI_LIMIT_CONFIGURATION_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			"(DTYPE, CODE, DISPLAYVALUE, PRODUCTCD, RISKSTATECD, EFFECTIVE, EXPIRATION, LOOKUPLIST_ID)\n" +
	"values\n" +
			"('BaseProductLookupValue', 'currentBILimits', '50000/100000', 'AAA_SS', '%s', TO_DATE('1-MAY-2017'), TO_DATE('1-MAY-2018'),\n" +
			"(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))";

	private static final String EVALUE_PRIOR_BI_LIMIT_CONFIGURATION_INSERT =	"INSERT INTO LOOKUPVALUE\n" +
		"(DTYPE, CODE, DISPLAYVALUE, PRODUCTCD, RISKSTATECD, EFFECTIVE, EXPIRATION, LOOKUPLIST_ID)\n" +
	"values\n" +
			"('BaseProductLookupValue', 'priorBILimits', '25000/50000', 'AAA_SS', '%s', TO_DATE('1-MAY-2017'), TO_DATE('1-MAY-2018'),\n" +
			"(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))";


	@Test
	@TestInfo(isAuxiliary = true)
	public static void eValueConfigCheck() {
		CustomAssert.enableSoftMode();
		List<String> configForStates = Arrays.asList("VA", "MD", "DC");
		for (String configForState : configForStates) {
			CustomAssert.assertTrue("eValue is not configured for " + configForState + ". Insert configuration (run eValueConfigInsert) and restart the env", DBService.get().getValue(String.format(EVALUE_CONFIGURATION_PER_STATE_CHECK, configForState)).isPresent());
			CustomAssert.assertTrue("Paperless Preferences is not configured for " + configForState + ". Insert configuration (run eValueConfigInsert) and restart the env", DBService.get().getValue(String.format(PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_CHECK, configForState)).isPresent());
		}
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Test(enabled = false)
	@TestInfo(isAuxiliary = true)
	public static void eValueConfigInsert() {
		List<String> configForStates = Arrays.asList("VA"  //for Paperless Preferences = Yes
				, "MD"  //for Paperless Preferences = Pending
				, "DC"); //for Paperless Preferences = No
		//PA should not have eValue or Paperless Preferences Configuration
		for (String configForState : configForStates) {
			insertConfigForRegularStates(configForState);
		}

		List<String> configForStatesLimits = Arrays.asList(
				"MD"
				, "DC");
		for (String configForStatesLimit : configForStatesLimits) {
			insertConfigForLimitsRegularStates(configForStatesLimit);
		}
	}

	@Test
	@TestInfo(isAuxiliary = true)
	public void precondJobAdding() {
		adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_BATCH_MARKER_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_AUTOMATED_PROCESSING_INITIATION_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AUTOMATED_PROCESSING_RATING_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AUTOMATED_PROCESSING_ISSUING_OR_PROPOSING_JOB);
	}

	private static void insertConfigForRegularStates(String state) {
		DBService.get().executeUpdate(String.format(EVALUE_CONFIGURATION_PER_STATE_INSERT, state));
		DBService.get().executeUpdate(String.format(PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_INSERT, state));
	}

	private static void insertConfigForLimitsRegularStates(String state) {
		DBService.get().executeUpdate(String.format(EVALUE_CURRENT_BI_LIMIT_CONFIGURATION_INSERT, state));
		DBService.get().executeUpdate(String.format(EVALUE_PRIOR_BI_LIMIT_CONFIGURATION_INSERT, state));
	}

	//TODO Replace below TCs with DataProvider when the Optional parameter State will be removed

	/**PAS-436
	 * @author Viktoriia Lutsenko
	 * @name Test presence/status of eValue discount on P&C and consolidated pages(Membership = Active, Evalue = Yes)
	 * @scenario 1. Create customer
	 * 2. Create active policy with next conditions:
	 * TS1: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'Yes'
	 * TS2: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
	 * TS3: Current AAA Member = 'No', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
	 * TS4: Current AAA Member = 'Membership Pending', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'Yes'
	 * TS5: Current AAA Member = 'Membership Pending', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
	 * TS6: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed > 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
	 * TS7: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $15,000/$30,000, Apply eValue Discount  = 'No'
	 * 3. Verify that on P&C page 'eValue Discount' is present in Discounts & Surcharges table (for TS1 and TS4) and 'eValue Discount' is absent in Discounts & Surcharges table (for TS2, TS3, TS5, TS6 and TS7) .
	 * 4. Bind policy.
	 * 5. Verify that 'eEvalue Status' = 'Pending' (for TS1 and TS4) and 'eEvalue Status' is empty (for TS2, TS3, TS5, TS6 and TS7) on Consolidated page.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-436")
	public void pas436_eValueDiscountVariations(@Optional("VA") String state) {
		testEvalueDiscount("AAAProductOwned_Active", "CurrentCarrierInformation", true, true, "Pending");
		testEvalueDiscount("AAAProductOwned_Active", "CurrentCarrierInformation", false, false, "");
		testEvalueDiscount("AAAProductOwned_No", "CurrentCarrierInformation", false, false, "");
		testEvalueDiscount("AAAProductOwned_Pending", "CurrentCarrierInformation", true, true, "Pending");
		testEvalueDiscount("AAAProductOwned_Pending", "CurrentCarrierInformation", false, false, "");
		testEvalueDiscount("AAAProductOwned_Active", "CurrentCarrierInformation_DayLapsedMore4", false, false, "");
		testEvalueDiscount("AAAProductOwned_Active", "CurrentCarrierInformation_BILimitLess", false, false, "");
	}


	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount
	 * @scenario 1. Create new eValue eligible quote
	 * 2. Check Default properties of eValue Discount field
	 * 3. Get premium before eValue discount is applied and after discount is applied
	 * 4. Compare premiums
	 * <p>
	 * Intermediate checks in Rating Details, Policy Summary Page
	 * @details
	 */
	//epic PAS-1438 eValue - New Business
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-305")
	public void pas305_eValueDiscountApplied(@Optional("VA") String state) {

		eValueQuoteCreationVA();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();

		//PAS-439 start
		generalTab.getInquiryAssetList().assetFieldsAbsence("Apply eValue Discount");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//Check field properties and default value of eValue Discount
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("No");
		//PAS-439 end
		//PAS-305 start
		CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
		//PAS-305 end

		//Get premiums before discount is applied
		Dollar policyLevelLiabilityCoveragesPremiumWithoutEvalueDiscount = premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium();
		Dollar vehicleCoveragePremiumWithoutEvalueDiscount = premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle1(1);
		Dollar totalPremiumWithoutEvalueDiscount = policyLevelLiabilityCoveragesPremiumWithoutEvalueDiscount.add(vehicleCoveragePremiumWithoutEvalueDiscount);

		//PAS-305 start
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT).getCell(4).verify.value("None");
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		//PAS-305 end

		//PAS-2053
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableAppliedDiscountsPolicy.getRowContains(2, E_VALUE_DISCOUNT).verify.present(false);
		//PAS-2053


		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		//Set discount to Yes
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		//PAS-304 start
		premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium().verify.equals(new Dollar(0));
		//PAS-304 end

		PremiumAndCoveragesTab.calculatePremium();

		//PAS-305 start
		CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
		//PAS-305 end

		//Get premiums after discount is applied
		Dollar policyLevelLiabilityCoveragesPremiumWithEvalueDiscount = premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium();
		Dollar vehicleCoveragePremiumWithEvalueDiscount = premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle1(1);
		Dollar totalPremiumWithEvalueDiscount = policyLevelLiabilityCoveragesPremiumWithEvalueDiscount.add(vehicleCoveragePremiumWithEvalueDiscount);

		log.info("totalPremiumWithoutEvalueDiscount: " + totalPremiumWithoutEvalueDiscount);
		log.info("totalPremiumWithEvalueDiscount: " + totalPremiumWithEvalueDiscount);

		//Compare premiums before discount and after
		CustomAssert.assertTrue(totalPremiumWithoutEvalueDiscount.moreThan(totalPremiumWithEvalueDiscount));
		//PAS-2053 eValue Status on Policy Summary Page - Don't Show it When not enabled

		//PAS-305 start
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT).getCell(4).verify.value("Yes");
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		//PAS-305 end

		//PAS-2053
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableAppliedDiscountsPolicy.getRowContains(2, E_VALUE_DISCOUNT).verify.present();
		//PAS-2053


		//PAS-276 start
		policy.dataGather().start();
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		InquiryAssetList inquiryAssetDocumentTabGeneralInfoSection = new InquiryAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.class);
		inquiryAssetDocumentTabGeneralInfoSection.assetFieldUnionCheck(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL.getLabel(), true, true, true);
		documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION.getLabel(), AssetList.class).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL).setValue("");
		DocumentsAndBindTab.btnPurchase.click();
		errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS6591343").getCell("Message").verify.contains("'Email' is required");
		//PAS-276 end

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}


	/**
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 1. Create new eValue eligible policy with membership pending and paperless preferences yes
	 * 2. Check policy consolidated view.
	 * 3. See if eMember status = Pending
	 * @details
	 *  @scenario 1. Create new eValue eligible policy with membership yes and paperless preferences yes
	 * 2. Check policy consolidated view.
	 * 3. See if eMember status = active
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-300")
	public void pas300_eValueStatusConsViewPaperPrefYes(@Optional("VA") String state){
		eValueQuoteCreationVA();

		CustomAssert.enableSoftMode();

		//issue quote evalue no
		//VC4 DB check


		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("");
		simplifiedQuoteIssue();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("Pending");
//Pas-302 start-
//VC1
		//sql query for DB check
		//Pas-302 end

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.buttonPendedEndorsement.click();
		simplifiedPendedEndorsementIssue();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("Active");
		//Pas-302 start
		//VC2
		//sql query for DB check
		//Pas-302 end

		//Pas-302 start
		//VC3
		//endorsment setting evalue discount no issuing.
		//sql query for DB check -
		//Pas-302 end

	}
	/**
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 1. Create new eValue eligible policy with membership pending and paperless preferences Pending
	 * 2. Check policy consolidated view.
	 * 3. See if eMember status = Pending
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-300")
	public void pas300_eValueStatusConsViewPaperPrefPending(@Optional("MD") String state){
		eValueQuoteCreationVA();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("");
		simplifiedQuoteIssue();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("Pending");
	}

	/**new feature
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 1. Create new eligible policy for the state  where eValue has not yet been rolled out
	 * 2. Check policy consolidated view.
	 * 3.should not  see eValue Status in the General Info section.
	 * @details**/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3708")
	public void pas3708_eValueStatusConsViewNotConfigured(@Optional("PA") String state){
		eValueQuoteCreationVA();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present(false);
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.present(false);
		simplifiedQuoteIssue();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.present(false);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test "Has the insured ever been enrolled in eValue?" visibility in different flows
	 * @scenario 1. Create new eValue eligible quote with eValue Discount = true
	 * 2. Check "Has the insured ever been enrolled in eValue?" is available in Data Gather mode for NB
	 * 2. Check "Has the insured ever been enrolled in eValue?" is Not available in Inquiry mode for NB
	 * 3. Check "Has the insured ever been enrolled in eValue?" is Not available in Endorsement
	 * 4. Check "Has the insured ever been enrolled in eValue?" is Not available in Renewal
	 * 5. Cancel Policy, Rewrite
	 * 6. Check "Has the insured ever been enrolled in eValue?" is available for the quote generate as a result of Rewrite and is Not defaulted to Yes (might change in future)
	 * PAS-306 Commission Type checked as part of the script
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-325")
	public void pas325_eValueCommissionRelatedFields(@Optional("VA") String state) {
		List<String> expectedNonEvalueCommissionTypeOptions = Arrays.asList("New Business", "Renewal");
		List<String> expectedEvalueCommissionTypeOptions = Arrays.asList("eValue New Business", "eValue Renewal");

		eValueQuoteCreationVA();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());

		//PAS-306, PAS-320, PAS-323 start
		CommissionTypeCheck(expectedNonEvalueCommissionTypeOptions, "No", "New Business");
		CommissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue New Business");
		//PAS-306, PAS-320, PAS-323 end

		//PAS-2054 start
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		generalTab.getInquiryAssetList().assetFieldsAbsence("Has the insured ever been enrolled in eValue?");
		//PAS-2054 end
		//PAS-325 end
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();
		String originalPolicyNumber = PolicySummaryPage.getPolicyNumber();

		//Inquiry doesn't show the field
		policy.policyInquiry().start();
		generalTab.getInquiryAssetList().assetFieldUnionCheck(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE.getLabel(), false, false, false);
		generalTab.cancel();

		//Endorsement doesn't show the field
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		generalTab.getInquiryAssetList().assetFieldUnionCheck(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE.getLabel(), false, false, false);
		//PAS-306, PAS-320, PAS-323 start
		CommissionTypeCheck(expectedEvalueCommissionTypeOptions, "No", "eValue New Business");//because the issue happened with eValue Discount = True
		CommissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue New Business");
		//PAS-306, PAS-320, PAS-323 end
		generalTab.cancel();
		Page.dialogConfirmation.buttonDeleteEndorsement.click();

		//Renewal doesn't show the field
		policy.renew().start();
		generalTab.getInquiryAssetList().assetFieldUnionCheck(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE.getLabel(), false, false, false);
		//PAS-306, PAS-320, PAS-323, PAS-318 - Renewal is not covered by this story
		CommissionTypeCheck(expectedEvalueCommissionTypeOptions, "No", "eValue Renewal");
		CommissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue Renewal");
		//PAS-306, PAS-320, PAS-323, PAS-318 end
		generalTab.saveAndExit();

		//Cancel and Rewrite
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE).verify.present();
		//There might be a new requirement to default the field to yes for the rewrite and Split in case if original policy had eValue Discount=true
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE).verify.value("No");
		//PAS-306, PAS-320, PAS-323 start
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.optionsContain(expectedNonEvalueCommissionTypeOptions);
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.value("Renewal");
		//PAS-306, PAS-320, PAS-323 end

		//Logic requested by business - not to carry over eValue from Original Policy and to have Commission Type dependent on HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE value which is set by Agent
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE).setValue("Yes");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.value("eValue Renewal");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE).setValue("No");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.value("Renewal");
		generalTab.cancel();

		//PAS-302 start
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, originalPolicyNumber);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData_Plus14Days"));
		TestData adjustedEndorsementActionData = getPolicyTD("Endorsement", "TestData").getTestData("EndorsementActionTab").adjust("Endorsement Date", "/today+15d");
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData").adjust("EndorsementActionTab", adjustedEndorsementActionData));

		//PAS-306, PAS-320, PAS-323 start
		CommissionTypeCheck(expectedEvalueCommissionTypeOptions, "No", "eValue New Business");//because the Issue Action happened with eValue Discount = True, and the agent is locked in eValue commissions forever
		CommissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue New Business");
		//PAS-306, PAS-320, PAS-323 end

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("Yes");
		//PAS-302 end
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}


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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-305")
	public void pas305_eValueNotApplicableForState(@Optional("PA") String state) {
		eValueQuoteCreationVA();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();

		//PAS-325 start
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE).verify.present(false);
		//PAS-325 end

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present(false);
		//PAS-309 start
		PremiumAndCoveragesTab.tableGreyBox.verify.present(false);
		//PAS-309 end
		//PAS-305 start
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT).verify.present(false);
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		//PAS-305 end
		//PAS-2053 start
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableAppliedDiscountsPolicy.getRowContains(2, E_VALUE_DISCOUNT).verify.present(false);
		//PAS-2053 end

		//PAS-276 start
		policy.dataGather().start();
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		InquiryAssetList inquiryAssetDocumentTabGeneralInfoSection = new InquiryAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.class);
		inquiryAssetDocumentTabGeneralInfoSection.assetFieldPresence(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL.getLabel(), true);
		documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION.getLabel(), AssetList.class).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL).setValue("");
		documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.AUTHORIZED_BY).getValue();

		DocumentsAndBindTab.btnPurchase.click();
		errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS6591343").verify.present(false);
		//PAS-276 end
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test AHEVAXX eValue Acknowledgement document is can be generated with Generate eSignature Documents
	 * @scenario 1. Create new eValue eligible quote for VA
	 * 2. set eValue Discount = true in P&C
	 * 3. set eValue Acknowledgement = true in Documents and Bind tab
	 * 4. Generate eSignature Documents
	 * 5. heck DB for the form AHEVAXX with tag eSignaturePackage
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-278")
	public void pas278_eValueeSignedPledgeDocumentAHEVAXX(@Optional("VA") String state) {

		eValueQuoteCreationVA();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();

		//PAS-264 start
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).verify.present(false);
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).verify.present(false);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).verify.present();
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).verify.value("Yes");
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).verify.present();
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).verify.value("Not Signed");

		Collection<String> expectedValues = new ArrayList<>();
		expectedValues.add("Physically Signed");
		expectedValues.add("Electronically Signed");
		expectedValues.add("Voice Signed");
		expectedValues.add("Not Signed");
		CustomAssert.assertTrue(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT)
				.getAllValues().containsAll(expectedValues));

		DocumentsAndBindTab.btnPurchase.click();
		errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS8120577").getCell("Message").verify.value("A signed eValue Acknowledgement must be received prior to issuing this transa...");
		errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS8120577").getCell("Code").controls.links.get(1).click();
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).setValue("Electronically Signed");

		DocumentsAndBindTab.btnPurchase.click();
		//if clause used fot the case if there will be no overridable errors
		if (errorTab.getErrorsControl().getTable().isPresent()) {
			errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS8120577").verify.present(false);
			errorTab.cancel();
		} else {
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		}
		//PAS-264 end
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		String policyNum = documentsAndBindTab.getPolicyNumber();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).setValue("Yes");

		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_ESIGNATURE_DOCUMENTS).click(Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
		documentsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.RECIPIENT_EMAIL_ADDRESS).setValue("test@email.com");
		documentsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.BTN_OK).click();
		Page.dialogConfirmation.buttonOk.click();

		//Delay is required for the document to appear in DB and in eFolder
		//PAS-264 start
		String query = (String.format(GET_DOCUMENT_BY_EVENT_NAME + "and data like '%%eSignaturePackage%%'", policyNum, "AHEVAXX", "ADHOC_DOC_GENERATE"));
		CustomAssert.assertTrue(DbAwaitHelper.waitForQueryResult(query, 60));

		documentsAndBindTab.saveAndExit();
		Efolder.isDocumentExist("Miscellaneous", "ACKNOWLEDGEMENT FORM");
		//PAS-264 end
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount not shown for state where it is not configured
	 * @scenario 1. Create new eValue eligible quote
	 * 2. Check Grey Box messages when eValue Discount can be selected
	 * 3. Check Grey Box messages when eValue Discount can't be selected
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-309")
	public void pas309_eValueGreyBox(@Optional("VA") String state) {
		String messageInfo1 = "This customer is not eligible for the eValue discount due to the following reason(s):";
		String messageBullet1 = "Pay in full with any payment method or enroll in AutoPay with a checking/savings account or debit card";
		String messageBullet3 = "Enrollment in paperless notifications for policy and billing documents";
		String messageBullet7 = "Has held CSAA Insurance for less than one term";
		String messageBullet8 = "Does not have an active AAA membership";
		String messageBullet9 = "Does not have prior insurance or prior insurance BI limit";

		eValueQuoteCreationVA();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();

		CustomAssert.enableSoftMode();
		//precondition Grey Box with eligible for Discount messages
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		eValueDiscountEligibleGreyBoxCheck();

		//eValue Acknowledgement related GreyBox message
		eValueAcknowledgementMessagesGreyBoxCheck();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).setValue("Not Signed");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		eValueDiscountEligibleGreyBoxCheck();

		//Prior BI Limit related greyBox message
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		String oldAgentEnteredBiLimitsValue = generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).getValue();
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue("contains=15,000");
		eValueDiscountNotEligibleGreyBoxCheck();


		PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).verify.value(messageInfo1);
		PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).verify.contains(messageBullet9);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue(oldAgentEnteredBiLimitsValue);
		eValueDiscountEligibleGreyBoxCheck();

		//membership related greyBox
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
		eValueDiscountEligibleGreyBoxCheck();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("No");
		eValueDiscountNotEligibleGreyBoxCheck();

		PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).verify.value(messageInfo1);
		PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).verify.contains(messageBullet8);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
		eValueDiscountEligibleGreyBoxCheck();


		premiumAndCoveragesTab.getAssetList().getAsset("Payment Plan", ComboBox.class).setValue("contains=Month");
		PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).verify.contains(messageBullet1);
		premiumAndCoveragesTab.getAssetList().getAsset("Payment Plan", ComboBox.class).setValue("Annual");
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet1));

		//TODO Add bullet3 verification once stub returns Paperless Preferences = No
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet3));

		//Check No Prior Carrier greyBox
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER).setValue("No");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());


		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet7));
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet9));
		eValueDiscountNotEligibleGreyBoxCheck();

		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet7));
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet9));
		eValueDiscountNotEligibleGreyBoxCheck();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		TestData defaultTestData = getPolicyTD("DataGather", "TestData");
		TestData currentCarrierSectionAdjusted = getTestSpecificTD("CurrentCarrierInformation");
		TestData generalTabAdjusted = DataProviderFactory.emptyData().adjust("CurrentCarrierInformation", currentCarrierSectionAdjusted);
		TestData currentCarrierData = defaultTestData.adjust("GeneralTab", generalTabAdjusted);
		generalTab.fillTab(currentCarrierData);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet7));
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet9));
		eValueDiscountEligibleGreyBoxCheck();

		//eValue Aknowldgement related GreyBox message
		eValueAcknowledgementMessagesGreyBoxCheck();

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Alex Tinkovan
	 * @name Test that limits are greater than or equal to the defined Current BI Threshold
	 * @scenario 1. Create new eValue eligible quote for VA (Prior BI and Membership Conditions)
	 * 2. Set 'Apply eValue Discount' = Yes
	 * 3. Verify that 'Bodily Injury Limit' first row from drop down is $25,000/$50,000
	 * 4. Verify that 'Bodily Injury Limit' drop down contains 7 rows
	 * 5. Set 'Apply eValue Discount' = No
	 * 6. Verify that 'Bodily Injury Limit' first row from drop down is $25,000/$50,000 value
	 * 7. Verify that 'Bodily Injury Limit' drop down contains 8 rows
	 * 8. Set 'Apply eValue Discount' = Yes
	 * 9. Verify that 'Bodily Injury Limit' drop down does not contain $25,000/$50,000
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-265")
	public void pas265_MinimumStateLimitsForBIPreBind(@Optional("VA") String state) {

		eValueQuoteCreationVA();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();

		String lowerLimit = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).getAllValues().get(0);
		CustomAssert.assertTrue("Bodily Injury Limit has incorrect lower limit" + lowerLimit, lowerLimit.contains("$50,000/$100,000"));
		CustomAssert.assertTrue(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).getAllValues().size()==7);

		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		lowerLimit = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).getAllValues().get(0);
		CustomAssert.assertTrue("Bodily Injury Limit has incorrect lower limit" + lowerLimit, lowerLimit.contains("$25,000/$50,000"));
		CustomAssert.assertTrue(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).getAllValues().size()==8);

		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).setValue(lowerLimit);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");

		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).verify.noOption(lowerLimit);
		PremiumAndCoveragesTab.calculatePremium();

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount Paper;less Preference related GreyBox message is shown correctly
	 * @scenario 1. Create new eValue eligible quote for DC state with Paperless Preferences = Pending
	 * 2. Check Grey Box messages when eValue Discount can be selected
	 * 3. Check Grey Box messages when eValue Discount can not be selected
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-309")
	public void pas309_eValueGreyBoxPaperlessPendingDC(@Optional("DC") String state) {

		pas309_eValueGreyBoxPaperlessCheck("Pending");
	}


	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount Paper;less Preference related GreyBox message is shown correctly
	 * @scenario 1. Create new eValue eligible quote for MD state with Paperless Preferences = No
	 * 2. Check Grey Box messages when eValue Discount can be selected
	 * 3. Check Grey Box messages when eValue Discount can not be selected
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-309")
	public void pas309_eValueGreyBoxPaperlessNoMD(@Optional("MD") String state) {

		pas309_eValueGreyBoxPaperlessCheck("No");
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-XXX")
	public void pasXXX_eValueNotApplicableForState(@Optional("VA") String state) {

		eValueQuoteCreationVA();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("4382122719291009"); //4382122719291009 - cancelled memembrship in SOA3

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		simplifiedQuoteIssue();

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		NB_15_30jobs(policyNumber);

		NB_15_30jobs(policyNumber);

	}

	private void NB_15_30jobs(String policyNumber) {
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

		PolicySummaryPage.buttonPendedEndorsement.verify.present();
		JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		String descriptionNote2 = "No message [automatedEndorsementRate]";
		//CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description").getValue().contains(descriptionNote2));


		JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		String descriptionTask3 = "Complete Task Complete or Cancel Pended Endorsement";
		String descriptionNote3 = "Bind Endorsement effective " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " for Policy " + policyNumber;
		//CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(2).getCell("Description").getValue().contains(descriptionTask3));
		//CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description").getValue().contains(descriptionNote3));
	}



	private void pas309_eValueGreyBoxPaperlessCheck(String paperlessPreferenceValue) {
		String messageBullet3 = "Enrollment in paperless notifications for policy and billing documents";

		eValueQuoteCreationVA();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		//paperlessPreferenceValue is state dependent in Stub
		documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value(paperlessPreferenceValue);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		eValueDiscountEligibleGreyBoxCheck();
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet3));

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue("contains=15,000");
		eValueDiscountNotEligibleGreyBoxCheck();
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet3));

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void testEvalueDiscount(String membershipStatus, String currentCarrier, boolean evalueIsSelected, boolean evalueIsPresent, String evalueStatus) {
		prefillEvalueTestData(membershipStatus, currentCarrier);
		fillPremiumAndCoveragesTab(evalueIsSelected);
		fillDriverActivityReportsTab();
		TestData tdPolicyCreation = fillDocumentAndBindTab(evalueIsPresent);
		new PurchaseTab().fillTab(tdPolicyCreation).submitTab();

		validateEvalueStatus(evalueStatus);
		validatePolicyStatus();
	}

	private void prefillEvalueTestData(String aaaProductOwned, String currentCarrierInformation) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();

		CustomAssert.enableSoftMode();
		String validateAddressDialogKey = TestData.makeKeyPath(new PrefillTab().getMetaKey(), AutoSSMetaData.PrefillTab.VALIDATE_ADDRESS_DIALOG.getLabel());
		String currentCarrierInformationKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel());
		String policyInformationKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel());
		String aaaProductOwnedKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

		TestData tdPolicyCreation = getPolicyTD("DataGather", "TestData")
				.adjust(validateAddressDialogKey, getTestSpecificTD("ValidateAddressDialog"))
				.adjust(currentCarrierInformationKey, getTestSpecificTD(currentCarrierInformation))
				.adjust(policyInformationKey, getTestSpecificTD("PolicyInformation"))
				.adjust(aaaProductOwnedKey, getTestSpecificTD(aaaProductOwned));

		policy.getDefaultView().fillUpTo(tdPolicyCreation, PremiumAndCoveragesTab.class, true);
	}

	private void fillPremiumAndCoveragesTab(boolean eValueIsPresent) {
		if (eValueIsPresent) {
			new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
			PremiumAndCoveragesTab.buttonCalculatePremium.click();
			PremiumAndCoveragesTab.discountsAndSurcharges.verify.contains(E_VALUE_DISCOUNT);
		} else {
			CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
		}
		PremiumAndCoveragesTab.buttonContinue.click();
	}

	private void fillDriverActivityReportsTab() {
		DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
		AbstractContainer<?, ?> assetList = driverActivityReportsTab.getAssetList();
		assetList.getAsset(AutoSSMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_QUOTE).setValue("Yes");
		assetList.getAsset(AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();
		driverActivityReportsTab.submitTab();
	}

	private TestData fillDocumentAndBindTab(boolean evalueIsPresent) {
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
		String metaKey = documentsAndBindTab.getMetaKey();
		String evalueAcknowledgementKey = TestData.makeKeyPath(metaKey,
				AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(),
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT.getLabel());
		String proofOfPriorInsuranceKey = TestData.makeKeyPath(metaKey,
				AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(),
				AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE.getLabel());
		TestData tdPolicyCreation = getPolicyTD("DataGather", "TestData")
				.adjust(proofOfPriorInsuranceKey, "Yes");
		if (evalueIsPresent) {
			tdPolicyCreation = tdPolicyCreation.adjust(evalueAcknowledgementKey, "Physically Signed");
		}
		documentsAndBindTab.fillTab(tdPolicyCreation).submitTab();
		return tdPolicyCreation;
	}

	private void validateEvalueStatus(String expectedEvalueStatus) {
		PolicySummaryPage.tableGeneralInformation.getRows().get(0)
				.getCell("eValue Status").verify.valueByRegex("Invalid eValue status", expectedEvalueStatus);
	}

	private void validatePolicyStatus() {
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void eValueAcknowledgementMessagesGreyBoxCheck() {
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).setValue("Physically Signed");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).getValue().contains(messageInfo2));
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet4));
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet10));
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet2));
	}

	private void eValueDiscountEligibleGreyBoxCheck() {
		//Check field properties and default value of eValue Discount
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");


		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).getValue().contains(messageInfo2));
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet4));
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet10));
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet2));
	}

	private void eValueDiscountNotEligibleGreyBoxCheck() {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled(false);
		//BUG PAS-3103 if eValue was set to yes, changing Prior BI Limit to non-eligible for Discount does't reset discount to No
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("No");

		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).getValue().contains(messageInfo2));
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet4));
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet10));
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet2));
	}

	private void CommissionTypeCheck(List<String> expectedCommissionTypeOptions, String eValueValue, String defaultCommissionTypeValue) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue(eValueValue);
		if (Page.dialogConfirmation.isPresent()) {
			Page.dialogConfirmation.confirm();
		}
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.optionsContain(expectedCommissionTypeOptions);
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.value(defaultCommissionTypeValue);
	}

	void eValueQuoteCreationVA() {
		//Default VA test data didn't work, so had to use multiple adjustments
		TestData defaultTestData = getPolicyTD("DataGather", "TestData");
		TestData policyInformationSectionAdjusted = getTestSpecificTD("PolicyInformation").adjust("TollFree Number", "1");
		TestData currentCarrierSectionTestSpecific = getTestSpecificTD("CurrentCarrierInformation");
		TestData generalTabAdjusted = defaultTestData.getTestData("GeneralTab")
				.adjust("PolicyInformation", policyInformationSectionAdjusted)
				.adjust("CurrentCarrierInformation", currentCarrierSectionTestSpecific);

		TestData eValuePolicyData = defaultTestData
				.adjust("PrefillTab", getTestSpecificTD("PrefillTab_eValue"))
				.adjust("GeneralTab", generalTabAdjusted)
				.resolveLinks();

		mainApp().open();
		createCustomerIndividual();

		getPolicyType().get().createQuote(eValuePolicyData);
		String policyNum = PolicySummaryPage.getPolicyNumber();
		log.info("policyNum: " + policyNum);
	}

	void simplifiedQuoteIssue() {
		policy.bind().start();
		DocumentsAndBindTab.btnPurchase.click();
		errorTab.overrideAllErrors();
		policy.bind().submit();
		new PurchaseTab().fillTab(getPolicyTD("DataGather", "TestData")).submitTab();
	}

	public void simplifiedPendedEndorsementIssue() {
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.AUTHORIZED_BY).setValue("Megha");
		DocumentsAndBindTab.btnPurchase.click();
		Page.dialogConfirmation.confirm();

		ErrorTab errorTab = new ErrorTab();
		errorTab.overrideAllErrors();
		DocumentsAndBindTab.btnPurchase.click();
		Page.dialogConfirmation.confirm();
	}
}
