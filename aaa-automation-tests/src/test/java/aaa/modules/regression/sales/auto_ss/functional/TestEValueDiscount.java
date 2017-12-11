/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
* CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.main.enums.DocGenEnum.Documents.AHEVAXX;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableList;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.components.Efolder;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.DbAwaitHelper;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.models.Document;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.TestEValueDiscountPreConditions;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.config.PropertyProvider;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.waiters.Waiters;

public class TestEValueDiscount extends AutoSSBaseTest implements TestEValueDiscountPreConditions {

	private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
	private static final String E_VALUE_DISCOUNT = "eValue Discount"; //PAS-440 - rumors have it, that discount might be renamed

	private static final ImmutableList<String> EXPECTED_BI_LIMITS = ImmutableList.of(
			"$25,000/$50,000",
			"$50,000/$100,000",
			"$100,000/$300,000",
			"$250,000/$500,000",
			"$300,000/$500,000 ",
			"$500,000/$500,000",
			"$500,000/$1,000,000",
			"$1,000,000/$1,000,000");
	private static final String LOWER_BI_LIMIT = "$25,000/$50,000";
	private static final String CONFIGURED_BI_LIMIT = "$50,000/$100,000";
	private static final String HIGHER_BI_LIMIT = "$1,000,000/$1,000,000";
	private static final String DEFAULT_BI_LIMIT = "$100,000/$300,000";
	private static String successfulCalculation = "Premium should be calculated successfully";

	private static final String MESSAGE_INFO_1 = "This customer is not eligible for eValue discount due to one or more of the following reasons:";
	private static final String MESSAGE_INFO_4 = "eValue Discount Requirements:";
	private static final String MESSAGE_BULLET_1 = "Payment Options: Pay in full with any payment method or enroll in AutoPay with a checking/savings account or debit card";
	private static final String MESSAGE_BULLET_1_A = "Payment Options: Pay in full with any payment method or enroll in AutoPay";
	private static final String MESSAGE_BULLET_3 = "Paperless Preferences: Enroll in paperless notifications for policy and billing documents";
	private static final String MESSAGE_BULLET_4 = "Coverage and BI Limits: Maintain continuous insurance coverage and bodily injury limits of at least $100,000/$300,000";
	private static final String MESSAGE_BULLET_4_A = "Coverage and BI Limits: Maintain continuous insurance coverage and bodily injury limits of at least $50,000/$100,000";
	private static final String MESSAGE_BULLET_7 = "Has held CSAA Insurance for less than one term";
	private static final String MESSAGE_BULLET_8 = "Does not have an active AAA membership";
	private static final String MESSAGE_BULLET_9 = "Does not have prior insurance or prior insurance BI limit";
	private static final String MESSAGE_BULLET_10 = "eValue Acknowledgement: Agree to and sign the eValue acknowledgement";
	private static final String MESSAGE_BULLET_11 = "Membership: Have an active membership";

	private static final List<String> PRE_QUALIFICATIONS = Arrays.asList(MESSAGE_BULLET_11, MESSAGE_BULLET_4_A, MESSAGE_BULLET_1, MESSAGE_BULLET_10, MESSAGE_BULLET_3);
	private static final List<String> NOT_PRE_QUALIFICATIONS = Arrays.asList(MESSAGE_BULLET_8, MESSAGE_BULLET_9, MESSAGE_BULLET_7);
	private static final List<String> MEMBERSHIP_FALSE_YES = Arrays.asList(MESSAGE_BULLET_4, MESSAGE_BULLET_1, MESSAGE_BULLET_10, MESSAGE_BULLET_3);
	private static final List<String> CURRENT_BI_FALSE_YES = Arrays.asList(MESSAGE_BULLET_11, MESSAGE_BULLET_1_A, MESSAGE_BULLET_10, MESSAGE_BULLET_3);
	private static final List<String> PAY_PLAN_FALSE_YES = Arrays.asList(MESSAGE_BULLET_11, MESSAGE_BULLET_4_A, MESSAGE_BULLET_10, MESSAGE_BULLET_3);
	private static final List<String> PAPERLESS_AND_PRIOR_INS_FALSE_YES = Arrays.asList(MESSAGE_BULLET_11, MESSAGE_BULLET_4_A, MESSAGE_BULLET_1, MESSAGE_BULLET_10);
	private static final List<String> ALL_FALSE = Arrays.asList(MESSAGE_BULLET_10);

	private GeneralTab generalTab = new GeneralTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab(); //TODO test with policy.dataGather().getView().getTab(DocumentsAndBindTab.class); instead of new Tab();
	private ErrorTab errorTab = new ErrorTab();
	private PurchaseTab purchaseTab = new PurchaseTab();

	private static final String EVALUE_MEMBERSHIP_ACKNOWLEDGEMENT_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeMemberQualifications", "membershipEligibility", "FALSE");

	private static final String EVALUE_CURRENT_BI_ACKNOWLEDGEMENT_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeMemberQualifications", "currentBIRequired", "FALSE");

	private static final String EVALUE_PAYPLAN_ACKNOWLEDGEMENT_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeMemberQualifications", "paymentPlanRequired", "FALSE");

	private static final String EVALUE_MYPOLICY_ACKNOWLEDGEMENT_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeMemberQualifications", "myPolicyRequired", "FALSE");

	private static final String EVALUE_CREDITCARD_ACKNOWLEDGEMENT_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeValueQualifyingPaymentMethods", "pciCreditCard", "TRUE");

	private static final String EVALUE_PAPERLESS_PREFERENCES_BLUE_BOX_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeMemberQualifications", "paperlessPreferencesRequired", "FALSE");

	private static final String EVALUE_PRIOR_INSURANCE_BLUE_BOX_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeMemberQualifications", "priorInsurance", "FALSE");

	@Test(description = "Precondition")
	public static void paperlessPreferencesConfigCheck() {
		CustomAssert
				.assertTrue("paperless preference stub endpoint. Please run paperlessPreferencesStubEndpointUpdate", DBService.get().getValue(String.format(PAPERLESS_PRFERENCE_STUB_POINT, APP_HOST)).get()
						.contains(APP_HOST));
	}

	@Test(description = "Precondition")
	public static void eValueConfigCheck() {
		CustomAssert.enableSoftMode();
		List<String> configForStates = Arrays.asList("VA", "MD", "DC");
		for (String configForState : configForStates) {
			CustomAssert.assertTrue("eValue is not configured for " + configForState + ". Insert configuration (run eValueConfigInsert) and restart the env", DBService.get()
					.getValue(String.format(EVALUE_CONFIGURATION_PER_STATE_CHECK, configForState)).isPresent());
			CustomAssert.assertTrue("Paperless Preferences is not configured for " + configForState + ". Insert configuration (run eValueConfigInsert) and restart the env", DBService.get()
					.getValue(String.format(PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_CHECK, configForState)).isPresent());
		}
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Test(description = "Precondition")
	public static void eValuePriorBiConfigCheck() {
		CustomAssert.enableSoftMode();
		CustomAssert.assertTrue("eValue configuration for Prior BI limits is missing. Please run eValuePriorBiConfigUpdateInsert", DBService.get().getValue(EVALUE_PRIOR_BI_CONFIG_CHECK)
				.isPresent());
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Test(description = "Precondition")
	public static void eValueCurrentBiConfigCheck() {
		CustomAssert.enableSoftMode();
		CustomAssert.assertTrue("eValue configuration for Current BI limits is missing. Please run eValueCurrentBiConfigUpdateInsert", DBService.get().getValue(EVALUE_CURRENT_BI_CONFIG_CHECK)
				.isPresent());
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Test(description = "Precondition")
	public static void eValueMembershipConfigCheck() {
		CustomAssert.enableSoftMode();
		CustomAssert.assertTrue("eValue configuration for membership not require. Please run eValueMembershipConfigInsert", DBService.get().getValue(EVALUE_MEMBERSHIP_CONFIG_CHECK).isPresent());
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Test(description = "Precondition")
	public void precondJobAdding() {
		adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_BATCH_MARKER_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_AUTOMATED_PROCESSING_INITIATION_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AUTOMATED_PROCESSING_RATING_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AUTOMATED_PROCESSING_ISSUING_OR_PROPOSING_JOB);
	}

	@Test(description = "Precondition")
	public static void eValueTerritoryChannelForVAConfigCheck() {
		CustomAssert
				.assertEquals("Territory for VA is not configured, please run eValueTerritoryChannelForVAConfigUpdate", DBService.get().getValue(EVALUE_TERRITORY_FOR_VA_CONFIG_CHECK).get(), "212");
		CustomAssert.assertEquals("Channel for VA is not configured, please run eValueTerritoryChannelForVAConfigUpdate", DBService.get().getValue(EVALUE_CHANNEL_FOR_VA_CONFIG_CHECK)
				.get(), "AZ Club Agent");
	}

	@Test(description = "Precondition")
	public static void eValueAcknowledgementConfigCheck() {
		CustomAssert.enableSoftMode();
		verifyAcknowledgementConfiguration(EVALUE_MEMBERSHIP_ACKNOWLEDGEMENT_CHECK, 10, 6, "eValueMembershipAcknowledgementConfigInsert");
		verifyAcknowledgementConfiguration(EVALUE_MEMBERSHIP_ACKNOWLEDGEMENT_CHECK, 5, 1, "eValueMembershipAcknowledgementConfigInsert");
		verifyAcknowledgementConfiguration(EVALUE_CURRENT_BI_ACKNOWLEDGEMENT_CHECK, 13, 11, "eValueCurrentBIAcknowledgementConfigInsert");
		verifyAcknowledgementConfiguration(EVALUE_CURRENT_BI_ACKNOWLEDGEMENT_CHECK, 5, 1, "eValueCurrentBIAcknowledgementConfigInsert");
		verifyAcknowledgementConfiguration(EVALUE_PAYPLAN_ACKNOWLEDGEMENT_CHECK, 20, 17, "eValuePayPlanAcknowledgementConfigInsert");
		verifyAcknowledgementConfiguration(EVALUE_PAYPLAN_ACKNOWLEDGEMENT_CHECK, 5, 1, "eValuePayPlanAcknowledgementConfigInsert");
		verifyAcknowledgementConfiguration(EVALUE_MYPOLICY_ACKNOWLEDGEMENT_CHECK, 16, 14, "eValueMyPolicyAcknowledgementConfigInsert");
		verifyAcknowledgementConfiguration(EVALUE_MYPOLICY_ACKNOWLEDGEMENT_CHECK, 5, 1, "eValueMyPolicyAcknowledgementConfigInsert");
		verifyAcknowledgementConfiguration(EVALUE_CREDITCARD_ACKNOWLEDGEMENT_CHECK, 13, 11, "eValueCreditCardAcknowledgementConfigInsert");
		verifyAcknowledgementConfiguration(EVALUE_PAPERLESS_PREFERENCES_BLUE_BOX_CHECK, 16, 14, "eValuePaperlessPreferencesBlueBoxConfigInsert");
		verifyAcknowledgementConfiguration(EVALUE_PAPERLESS_PREFERENCES_BLUE_BOX_CHECK, 5, 1, "eValuePaperlessPreferencesBlueBoxConfigInsert");
		verifyAcknowledgementConfiguration(EVALUE_PRIOR_INSURANCE_BLUE_BOX_CHECK, 16, 14, "eValuePriorInsuranceBlueBoxConfigInsert");
		verifyAcknowledgementConfiguration(EVALUE_PRIOR_INSURANCE_BLUE_BOX_CHECK, 5, 1, "eValuePriorInsuranceBlueBoxConfigInsert");
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private static void verifyAcknowledgementConfiguration(String eValueConfiguration, int sysAndEffDateDelta, int sysAndExpDateDelta, String insertQuery) {
		String query = MessageFormat.format(eValueConfiguration, sysAndEffDateDelta + 1, sysAndEffDateDelta, sysAndExpDateDelta + 1, sysAndExpDateDelta);
		CustomAssert.assertTrue("Configuration for acknowledgement should be present. Please run " + insertQuery, DBService.get().getValue(query).isPresent());
	}

	//TODO Replace below TCs with DataProvider when the Optional parameter State will be removed

	/**
	 * PAS-436
	 *
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

		eValueQuoteCreation();

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

		log.info("totalPremiumWithoutEvalueDiscount: {}", totalPremiumWithoutEvalueDiscount);
		log.info("totalPremiumWithEvalueDiscount: {}", totalPremiumWithEvalueDiscount);

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
		documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION.getLabel(), AssetList.class)
				.getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL).setValue("");
		DocumentsAndBindTab.btnPurchase.click();
		CustomAssert.assertTrue(errorTab.getErrorsControl().getTable().getColumn("Message").getValue().toString().contains("'Email' is required"));
		//PAS-276 end

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 1. Create new eValue eligible policy with membership pending and paperless preferences yes evalue no
	 * 2. Check policy consolidated view.
	 * 3. See if eMember status = No
	 * 4. DB check for evalue status in the Database NOTENROLLED
	 * @details

	 * @scenario 3. Create new eValue eligible policy with membership yes and paperless preferences yes evalue yes
	 * 2. Check policy consolidated view.
	 * 3. See if eMember status = active
	 * 4. DB check for evalue status in the Database Pending
	 * @scenario 4. Create new eValue eligible policy with membership yes and paperless preferences yes evalue yes
	 * 2. while doing endorsment set evalue no
	 * 3. DB check for evalue status in the Database Inactive
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-300")
	public void pas300_eValueStatusConsViewPaperPrefYes(@Optional("VA") String state) {
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();

		//PAS-302 start VC4
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("");
		CustomAssert.assertEquals(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber)).get(), "NOTENROLLED");
		//PAS-302 end

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedPendedEndorsementIssue();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("Active");
		//PAS-302 start VC2
		CustomAssert.assertEquals(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber)).get(), "ACTIVE");
		//PAS-302 end

		//PAS-302 start VC3
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		//BUG PAS-6591 PAS-316 There is no Dialog Confirmation popup when setting EValue to No in endorsement
		Page.dialogConfirmation.confirm();
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedPendedEndorsementIssue();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("");
		CustomAssert.assertEquals(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber)).get(), "INACTIVE");
		//PAS-302 end
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 2. Create new eValue eligible policy with membership pending and paperless preferences yes evalue yes
	 * 2. Check policy consolidated view.
	 * 3. See if eMember status = Pending
	 * 4. DB check for evalue status in the Database Pending
	 * @details
	 * */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-300")
	public void pas300_eValueStatusConsViewPaperPrefPendingVa(@Optional("VA") String state) {
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("");
		simplifiedQuoteIssue();

		//BUG PAS-4279 Evalue status showing wrong
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("Pending");

		//PAS-302 start VC1
		CustomAssert.assertEquals(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber)).get(), "PENDING");
		//PAS-302 end
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
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
	public void pas300_eValueStatusConsViewPaperPrefPendingDc(@Optional("DC") String state) {
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("");
		simplifiedQuoteIssue("ACH");
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("Pending");
	}

	/**
	 * new feature
	 *
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 1. Create new eligible policy for the state  where eValue has not yet been rolled out
	 * 2. Check policy consolidated view.
	 * 3.should not  see eValue Status in the General Info section.
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3708")
	public void pas3708_eValueStatusConsViewNotConfigured(@Optional("PA") String state) {
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present(false);
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		CustomAssert.assertFalse(PolicySummaryPage.tableGeneralInformation.getHeader().getValue().contains("eValue Status"));
		simplifiedQuoteIssue();
		CustomAssert.assertFalse(PolicySummaryPage.tableGeneralInformation.getHeader().getValue().contains("eValue Status"));
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
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

		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());

		//PAS-306, PAS-320, PAS-323 start
		commissionTypeCheck(expectedNonEvalueCommissionTypeOptions, "No", "New Business");
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue New Business");
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
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "No", "eValue New Business");//because the issue happened with eValue Discount = True
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue New Business");
		pas316_eValueRemovalPopUpCheck();
		//PAS-306, PAS-320, PAS-323 end
		generalTab.cancel();
		Page.dialogConfirmation.buttonDeleteEndorsement.click();

		//Renewal doesn't show the field
		policy.renew().start();
		generalTab.getInquiryAssetList().assetFieldUnionCheck(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE.getLabel(), false, false, false);
		//PAS-306, PAS-320, PAS-323, PAS-318 - Renewal is not covered by this story
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "No", "eValue Renewal");
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue Renewal");
		//PAS-306, PAS-320, PAS-323, PAS-318 end
		pas316_eValueRemovalPopUpCheck();
		generalTab.saveAndExit();

		//Cancel and Rewrite
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE).verify
				.present();
		//There might be a new requirement to default the field to yes for the rewrite and Split in case if original policy had eValue Discount=true
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE).verify
				.value("No");
		//PAS-306, PAS-320, PAS-323 start
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify
				.optionsContain(expectedNonEvalueCommissionTypeOptions);
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.value("Renewal");
		//PAS-306, PAS-320, PAS-323 end

		//Logic requested by business - not to carry over eValue from Original Policy and to have Commission Type dependent on HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE value which is set by Agent
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE)
				.setValue("Yes");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.value("eValue Renewal");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE)
				.setValue("No");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.value("Renewal");
		generalTab.cancel();

		//PAS-302 start
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, originalPolicyNumber);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData_Plus14Days"));
		TestData adjustedEndorsementActionData = getPolicyTD("Endorsement", "TestData").getTestData("EndorsementActionTab").adjust("Endorsement Date", "$<today+15d>");
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData").adjust("EndorsementActionTab", adjustedEndorsementActionData));

		//PAS-306, PAS-320, PAS-323 start
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "No", "eValue New Business");//because the Issue Action happened with eValue Discount = True, and the agent is locked in eValue commissions forever
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue New Business");
		//PAS-306, PAS-320, PAS-323 end

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("Yes");
		//PAS-302 end
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void pas316_eValueRemovalPopUpCheck() {
		//PAS-316 start
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		//BUG PAS-6591 PAS-316 There is no Dialog Confirmation popup when setting EValue to No in endorsement
		CustomAssert.assertTrue(Page.dialogConfirmation.isPresent());
		Page.dialogConfirmation.labelMessage.verify.value("If you remove the eValue discount, the premium will increase. Are you sure you want to remove the discount?");
		Page.dialogConfirmation.reject();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("Yes");
		//PAS-316 end
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
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();

		//PAS-325 start
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE).verify
				.present(false);
		//PAS-325 end

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present(false);
		//PAS-309 start
		PremiumAndCoveragesTab.tableEValueMessages.verify.present(false);
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
		documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION.getLabel(), AssetList.class)
				.getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL).setValue("");

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
	 * 5. Check DB for the form AHEVAXX with tag eSignaturePackage
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-278")
	public void pas278_eValueeSignedPledgeDocumentAHEVAXX(@Optional("VA") String state) {

		eValueQuoteCreation();

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
		errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS8120577").getCell("Message").verify
				.value("A signed eValue Acknowledgement must be received prior to issuing this transa...");
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

		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_ESIGNATURE_DOCUMENTS)
				.click(Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
		documentsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.RECIPIENT_EMAIL_ADDRESS)
				.setValue("test@email.com");
		documentsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.BTN_OK).click();
		Page.dialogConfirmation.buttonOk.click();

		//Delay is required for the document to appear in DB and in eFolder
		//PAS-264 start
		String query = GET_DOCUMENT_BY_EVENT_NAME + "and data like '%%ESignatureChannel%%'";
		String queryFull = String.format(query, policyNum, "AHEVAXX", "ADHOC_DOC_GENERATE");
		CustomAssert.assertTrue(DbAwaitHelper.waitForQueryResult(queryFull, 60));
		log.info("Delay start");
		Waiters.SLEEP(60000).go();
		log.info("Delay end");
		documentsAndBindTab.saveAndExit();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, policyNum);
		Efolder.isDocumentExist("Miscellaneous", "ACKNOWLEDGEMENT FORM");
		//PAS-264 end
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * *@author Oleg Stasyuk
	 * *@name Test Configuration for eValue PriorBILimit
	 * *@scenario 1. Create new eValue eligible quote for VA
	 * 2. set Prior BI Limit to the one that Disables eValue
	 * 3. Check eValueDiscount field is disable in P&C tab
	 * 4. set Prior BI Limit to the one that Enables eValue
	 * 5. Check eValueDiscount field is enabled in P&C tab
	 * 6. set eValue = Yes
	 * 7. Change Effective Date of the Quote to 8 days in the past
	 * 8. Check Different Prior BI Limit enable/disable eValue configuration
	 * *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValuePriorBiConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-436")
	public void pas436_eValuePriorBiConfigurationDependency(@Optional("VA") String state) {
		eValueQuoteCreation();
		CustomAssert.enableSoftMode();

		policy.dataGather().start();
		pas436_eValuePriorBiConfigurationDependencyCheck("$25,000/$50,000", "$50,000/$100,000");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(TimeSetterUtil
				.getInstance().getCurrentTime().minusDays(8).format(DateTimeUtils.MM_DD_YYYY));
		pas436_eValuePriorBiConfigurationDependencyCheck("$20,000/$40,000", "$25,000/$50,000");

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void pas436_eValuePriorBiConfigurationDependencyCheck(String disableEvaluePriorBiLimit, String enableEvaluePriorBiLimit) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue(disableEvaluePriorBiLimit);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled(false);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue(enableEvaluePriorBiLimit);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled(true);
	}

	/**
	 * *@author Viktoriia Lutsenko
	 * *@name Test BI limit behavior based on eValue selection and remove eValue discount popup.
	 * *@scenario
	 * 1.For new business scenarios see {@link TestEValueDiscount#testDefaultBILimitValue} and {@link TestEValueDiscount#testRemoveEvalueDiscountPopup(RadioGroup, ComboBox)}
	 * 2.For endorsement scenarios see {@link TestEValueDiscount#testRemoveEvalueDiscountPopup(RadioGroup, ComboBox)}
	 * 3.For renewal scenarios see {@link TestEValueDiscount#testRemoveEvalueDiscountPopup(RadioGroup, ComboBox)}
	 * *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueCurrentBiConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4264")
	public void pas4264_ShowAllBILimitsWhenEvalueIsSelected(@Optional("VA") String state) {
		RadioGroup applyEvalueDiscountAsset = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT);
		ComboBox biAsset = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY);
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		verifyBILimitNB(applyEvalueDiscountAsset, biAsset);
		verifyBILimitEndorsement(applyEvalueDiscountAsset, biAsset);
		verifyBILimitRenewal(applyEvalueDiscountAsset, biAsset);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void verifyBILimitNB(RadioGroup applyEvalueDiscountAsset, ComboBox biAsset) {
		ComboBox uumbiAsset = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY);
		policy.dataGather().start();
		testDefaultBILimitValue(applyEvalueDiscountAsset, biAsset, uumbiAsset);
		testRemoveEvalueDiscountPopup(applyEvalueDiscountAsset, biAsset);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("Annual");
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		fillDocumentAndBindTab(true);
		purchaseTab.fillTab(getPolicyTD()).submitTab();
	}

	private void verifyBILimitEndorsement(RadioGroup applyEvalueDiscountAsset, ComboBox biAsset) {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		testRemoveEvalueDiscountPopup(applyEvalueDiscountAsset, biAsset);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("Annual");
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab.btnPurchase.click();
		Page.dialogConfirmation.confirm();
	}

	private void verifyBILimitRenewal(RadioGroup applyEvalueDiscountAsset, ComboBox biAsset) {
		policy.renew().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		testRemoveEvalueDiscountPopup(applyEvalueDiscountAsset, biAsset);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("Annual (Renewal)");
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.saveAndExit();
	}

	private void processEvalueDiscountPopUp(boolean deletePopup) {
		CustomAssert.assertTrue(Page.dialogConfirmation.isPresent());
		Page.dialogConfirmation.labelMessage.verify.value("BI limit of at least "+CONFIGURED_BI_LIMIT +" must be selected when eValue discount is applied. Please select Yes to confirm this change.");
		if (deletePopup){
			Page.dialogConfirmation.confirm();
		}
		else {
			Page.dialogConfirmation.reject();
		}
	}

	/**
	 * *@author Viktoriia Lutsenko
	 * *@name Test Remove eValue Discount popup
	 * *@scenario
	 * 1. Go to P&C page, set BI limit < required BI limit and verify that pop-up with error message EV1000005 is displayed (message is dynamic, based on configuration in DB for Current BI limit).
	 * 2. Select 'Yes' in pop-up and verify that BI limit is changed to selected one.
	 * 3. Calculate policy and verify that evalue discount is deleted.
	 * 4. Seelect eValue = 'Yes'.
	 * 5. Select BI limit < required BI limit and verify that pop-up with error message EV1000005 is displayed (message is dynamic, based on configuration in DB for Current BI limit).
	 * 6. Select 'Cancel' in pop-up and verify that eValue = 'Yes' and BI limmit is changed to previous value.
	 * 7. Change BI limit to required, but not the lowesr one.
	 * 8. Change any other coverage and pay plan, verify that BI limit isn't chnaged to lowest required BI limit.
	 * 9. Calculate premium and verify that eValue discount is present.
	 * *@details
	 */
	private void testRemoveEvalueDiscountPopup(RadioGroup applyEvalueDiscountAsset, ComboBox biAsset) {
		String currentBI;
		List<String> allValues = biAsset.getAllValues();
		biAsset.setValue(searchBILimit(allValues,LOWER_BI_LIMIT));
		processEvalueDiscountPopUp(true);
		applyEvalueDiscountAsset.verify.value("No");
		CustomAssert.assertTrue("BI limit should be changed to lowest BI limit "+LOWER_BI_LIMIT, biAsset.getValue().contains(LOWER_BI_LIMIT));
		PremiumAndCoveragesTab.calculatePremium();
		CustomAssert.assertFalse(successfulCalculation, isTotalTermPremiumEquals0());
		CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
		applyEvalueDiscountAsset.setValue("Yes");
		CustomAssert.assertTrue("BI limit should be changed to configured BI limit "+CONFIGURED_BI_LIMIT, biAsset.getValue().contains(CONFIGURED_BI_LIMIT));
		biAsset.setValue(searchBILimit(biAsset.getAllValues(),LOWER_BI_LIMIT));
		processEvalueDiscountPopUp(false);
		applyEvalueDiscountAsset.verify.value("Yes");
		CustomAssert.assertTrue("BI limit should be changed to prevoius BI limit "+CONFIGURED_BI_LIMIT, biAsset.getValue().contains(CONFIGURED_BI_LIMIT));
		biAsset.setValueContains(HIGHER_BI_LIMIT);
		currentBI = biAsset.getValue();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValueContains("Semi-Annual");
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE).setValueContains("$500");
		CustomAssert.assertTrue("BI limit shouldn't be changed when we update coverages/ pay plan", biAsset.getValue().equals(currentBI));
		PremiumAndCoveragesTab.calculatePremium();
		CustomAssert.assertFalse(successfulCalculation, isTotalTermPremiumEquals0());
		CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
	}

	/**
	 * *@author Viktoriia Lutsenko
	 * *@name Test Default BI limit values
	 * *@scenario
	 * 1. Initiate quote and enter all needed data, go to P&C page and verify that default value = 'No'.
	 * 2. Select eValue = 'Yes' and BI limit isn't changed (BI/UIMBI/UMBI limits have all values in dropdown), premium is reseted to 0.
	 * 3. Calculate premium.
	 * 4. Select eValue = 'No' and Bl limit isn't changed (BI/UIMBI/UMBI limits have all values in dropdown).
	 * 5. Select BI limit < required BI limit (BI/UIMBI/UMBI limits have all values in dropdown).
	 * 6. Calculate premium.
	 * *@details
	 */
	private void testDefaultBILimitValue(RadioGroup applyEvalueDiscountAsset, ComboBox biAsset, ComboBox uumbiAsset) {
		String currentBI;
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		applyEvalueDiscountAsset.verify.value("No");
		currentBI = biAsset.getValue();
		applyEvalueDiscountAsset.setValue("Yes");
		CustomAssert.assertTrue("BI limit shouldn't be changed as default BI limit ($100,000/$300,000) > required BI limit "+CONFIGURED_BI_LIMIT, biAsset.getValue().contains(currentBI));
		verifyBILimitCoverages(biAsset, uumbiAsset);
		CustomAssert.assertTrue("Premium should be reseted to 0", isTotalTermPremiumEquals0());
		PremiumAndCoveragesTab.calculatePremium();
		CustomAssert.assertFalse(successfulCalculation, isTotalTermPremiumEquals0());
		applyEvalueDiscountAsset.setValue("No");
		verifyBILimitCoverages(biAsset, uumbiAsset);
		biAsset.setValueContains(LOWER_BI_LIMIT);
		applyEvalueDiscountAsset.setValue("Yes");
		CustomAssert.assertTrue("BI limit should be changed to required BI limit "+CONFIGURED_BI_LIMIT, biAsset.getValue().contains(CONFIGURED_BI_LIMIT));
		verifyBILimitCoverages(biAsset, uumbiAsset);
		PremiumAndCoveragesTab.calculatePremium();
		CustomAssert.assertFalse(successfulCalculation, isTotalTermPremiumEquals0());
	}

	private void verifyBILimitCoverages(ComboBox biAsset, ComboBox uumbiAsset) {
		verifyBILimits(biAsset);
		verifyBILimits(uumbiAsset);
	}

	private boolean isTotalTermPremiumEquals0() {
		return "$0.00".equals(PremiumAndCoveragesTab.totalTermPremium.getValue());
	}

	private void verifyBILimits(ComboBox biAsset) {
		List<String> actualBILimits = biAsset.getAllValues();
		CustomAssert.assertEquals("Incorrect BI limits numbers in dropdown", EXPECTED_BI_LIMITS.size(), actualBILimits.size());
		for (String expectedBILimit : EXPECTED_BI_LIMITS) {
			String foundBILimit = searchBILimit(actualBILimits, expectedBILimit);
			CustomAssert.assertFalse("BI limit " + expectedBILimit + " isn't found", foundBILimit == null);
		}
	}

	private String searchBILimit(List<String> actualBILimits, String expectedBILimit) {
		for (String actualBILimit : actualBILimits) {
			if (actualBILimit.startsWith(expectedBILimit)) {
				return actualBILimit;
			}
		}
		return null;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Configuration for eValue based on Territory and Channel
	 * @scenario 1. Create new eValue eligible quote for VA
	 * 2. set Territory <> MidAtlantic
	 * 3. Check eValueDiscount field is not available in P&C tab
	 * 4. set Territory = MidAtlantic
	 * 5. Check eValueDiscount field is present in P&C tab
	 * 6. set Channel = Independent Agent
	 * 7. Check eValueDiscount field is not available in P&C tab
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueTerritoryChannelForVAConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-233")
	public void pas233_eValueTerritoryChannelDependency(@Optional("VA") String state) {
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		eValueDependencyOnTerrirotyChannelCheck(getTestSpecificTD("PolicyInformationForTerritoriesEValueNotApplicable1"), false);

		eValueDependencyOnTerrirotyChannelCheck(getTestSpecificTD("PolicyInformationForTerritoriesEValueApplicable"), true);

		eValueDependencyOnTerrirotyChannelCheck(getTestSpecificTD("PolicyInformationForTerritoriesEValueNotApplicable2"), false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void eValueDependencyOnTerrirotyChannelCheck(TestData territoryChannelData, boolean eValueDiscountPresence) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.CHANNEL_TYPE).fill(territoryChannelData);
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY).fill(territoryChannelData);
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY_LOCATION).fill(territoryChannelData);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present(eValueDiscountPresence);
	}

	/**
	 * @author Megha Gubbala
	 * @name Test Configuration for eValue for Membership eligibility
	 * @scenario 1. Create new eValue eligible quote for VA
	 * 2. set Membership = no
	 * 3. Check eValueDiscount field is disabled in P&C tab
	 * 4. change the effective date when configuration added for membership eligibility
	 * 5. Check eValueDiscount field is enabled in P&C tab
	 * 6. set eValue = Yes in P&C tab
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueMembershipConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3007")
	public void pas3007_eValueMembershipConfiguration(@Optional("VA") String state) {

		eValueQuoteCreation();

		CustomAssert.enableSoftMode();

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("No");
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("");

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled(false);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(8).format(DateTimeUtils.MM_DD_YYYY));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	//TODO Replace below TCs with DataProvider when the Optional parameter State will be removed

	/**
	 * *@author Viktoriia Lutsenko
	 * *@name Evalue acknowledgement document (AHEVAXX) generation.
	 * *@scenario
	 * 1.Make next configurations for evalue state:
	 * - membershipEligibility = N, currentBIRequired = Y, paymentPlanRequired = Y, myPolicyRequired = Y, pciCreditCard = N (systemdate-10;sytemdate-6)
	 * - membershipEligibility = Y, currentBIRequired = N, paymentPlanRequired = Y, myPolicyRequired = Y, pciCreditCard = Y (systemdate-13;sytemdate-11)
	 * - membershipEligibility = Y, currentBIRequired = Y, paymentPlanRequired = N, myPolicyRequired = Y, pciCreditCard = N (systemdate-20;sytemdate-17)
	 * - membershipEligibility = Y, currentBIRequired = Y, paymentPlanRequired = Y, myPolicyRequired = N, pciCreditCard = N (systemdate-16;sytemdate-14)
	 * - membershipEligibility = N, currentBIRequired = N, paymentPlanRequired = N, myPolicyRequired = N, pciCreditCard = N (systemdate-5;sytemdate-1)
	 * 2.Initiate quote creation.
	 * 3. Apply evalue discount.
	 * 4. Calculate premium.
	 * 5. Go to Documents and Bind tab.
	 * 6. Click on Generate Documents..
	 * 7. AHEVAXX for each case has next data:
	 * - AAAMemYN = N, CurrentBIYN = Y, PayPlnYN = Y, PlcyPayFullAmtYN = Y, MyPolicyYN = Y (systemdate-10;sytemdate-6)
	 * - AAAMemYN = Y, CurrentBIYN = N, PayPlnYN = Y, PlcyPayFullAmtYN = N, MyPolicyYN = Y (systemdate-13;sytemdate-11)
	 * - AAAMemYN = Y, CurrentBIYN = Y, PayPlnYN = N, PlcyPayFullAmtYN = Y, MyPolicyYN = Y (systemdate-20;sytemdate-17)
	 * - AAAMemYN = Y, CurrentBIYN = Y, PayPlnYN = Y, PlcyPayFullAmtYN = Y, MyPolicyYN = N (systemdate-16;sytemdate-14)
	 * - AAAMemYN = N, CurrentBIYN = N, PayPlnYN = N, PlcyPayFullAmtYN = Y, MyPolicyYN = N (systemdate-5;sytemdate-1)
	 * *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueAcknowledgementConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3693")
	public void pas3693_eValueConfiguration(@Optional("VA") String state) {
		CustomAssert.enableSoftMode();
		verifyEvalueAcknowledgement(8, "N", "Y", "Y", "Y", "Y");
		checkBlueBoxMessagesWithDiffData(8, MESSAGE_INFO_4, MEMBERSHIP_FALSE_YES, MESSAGE_INFO_4, MEMBERSHIP_FALSE_YES, "membership");
		verifyEvalueAcknowledgement(12, "Y", "N", "Y", "N", "Y");
		checkBlueBoxMessagesWithDiffData(12, MESSAGE_INFO_4, CURRENT_BI_FALSE_YES, MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS, "membership");
		verifyEvalueAcknowledgement(18, "Y", "Y", "N", "Y", "Y");
		checkBlueBoxMessagesWithDiffData(18, MESSAGE_INFO_4, PAY_PLAN_FALSE_YES, MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS, "membership");
		verifyEvalueAcknowledgement(15, "Y", "Y", "Y", "Y", "N");
		checkBlueBoxMessagesWithDiffData(15, MESSAGE_INFO_4, PAPERLESS_AND_PRIOR_INS_FALSE_YES, MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS, "priorCarior");
		verifyEvalueAcknowledgement(3, "N", "N", "N", "Y", "N");
		checkBlueBoxMessagesWithDiffData(3, MESSAGE_INFO_4, ALL_FALSE, MESSAGE_INFO_4, ALL_FALSE, "priorCarior");
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void verifyEvalueAcknowledgement(int days, String aaaMemYN, String currentBIYN, String payPlnYN, String plcyPayFullAmtYN, String myPolicyYN) {
		String quoteNumber;
		eValueQuoteCreation();
		quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(TimeSetterUtil
				.getInstance().getCurrentTime().minusDays(days).format(DateTimeUtils.MM_DD_YYYY));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab.btnGenerateDocuments.click();
		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(AHEVAXX, quoteNumber, "ADHOC_DOC_GENERATE");
		if (document != null) {
			verifyAHEVAXXTag(document, "AAAMemYN", aaaMemYN);
			verifyAHEVAXXTag(document, "CurrentBIYN", currentBIYN);
			verifyAHEVAXXTag(document, "PayPlnYN", payPlnYN);
			if ("Y".equals(payPlnYN)) {
				verifyAHEVAXXTag(document, "PlcyPayFullAmtYN", plcyPayFullAmtYN);
			}
			verifyAHEVAXXTag(document, "MyPolicyYN", myPolicyYN);
		}
		documentsAndBindTab.saveAndExit();
	}

	private void verifyAHEVAXXTag(Document document, String tag, String expectedValue) {
		CustomAssert.assertTrue(MessageFormat.format("Problem is in tag: [{0}]", tag), expectedValue
				.equals(DocGenHelper.getDocumentDataElemByName(tag, document).getDataElementChoice().getTextField()));
	}

	/**
	 * @author Alex Tinkovan
	 * @name Test Blue Box instead of Grey Box and Text is static
	 * @scenario 1. Create new eValue eligible quote. Check Massages in Blue Box
	 * 2. Select Membership No. Check Messages in Blue Box
	 * 3. Select PriorInsur No. Check Messages in Blue Box
	 * 4. Perform Endorsment with eValue. Check Massages in Blue Box
	 * 5. Select Membership No. Check Messages in Blue Box
	 * 6. Select PriorInsur No. Check Messages in Blue Box
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3694")
	public void pas3694_eValueBlueBoxAndStaticText(@Optional("VA") String state) {

		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		checkBlueBoxMessages(MESSAGE_INFO_4, PRE_QUALIFICATIONS);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("No");
		checkBlueBoxMessages(MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER).setValue("No");
		checkBlueBoxMessages(MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
		checkBlueBoxMessages(MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS);

		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		TestData defaultTestData = getPolicyTD("DataGather", "TestData");
		TestData currentCarrierSectionAdjusted = getTestSpecificTD("CurrentCarrierInformation");
		TestData generalTabAdjusted = DataProviderFactory.emptyData().adjust("CurrentCarrierInformation", currentCarrierSectionAdjusted);
		TestData currentCarrierData = defaultTestData.adjust("GeneralTab", generalTabAdjusted);
		generalTab.fillTab(currentCarrierData);

		checkBlueBoxMessages(MESSAGE_INFO_4, PRE_QUALIFICATIONS);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled(true);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("No");
		checkBlueBoxMessages(MESSAGE_INFO_4, PRE_QUALIFICATIONS);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		checkBlueBoxMessages(MESSAGE_INFO_4, PRE_QUALIFICATIONS);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("No");
		checkBlueBoxMessages(MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER).setValue("No");
		checkBlueBoxMessages(MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
		checkBlueBoxMessages(MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS);

		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		simplifiedPendedEndorsementIssue();

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
		String currentCarrierInformationKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel());
		String policyInformationKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel());
		String aaaProductOwnedKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

		TestData tdPolicyCreation = getPolicyTD("DataGather", "TestData")
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
		log.info("TEST: Policy created with #{}", PolicySummaryPage.labelPolicyNumber.getValue());
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void commissionTypeCheck(List<String> expectedCommissionTypeOptions, String eValueValue, String defaultCommissionTypeValue) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue(eValueValue);
		if (Page.dialogConfirmation.isPresent()) {
			Page.dialogConfirmation.confirm();
		}
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify
				.optionsContain(expectedCommissionTypeOptions);
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.value(defaultCommissionTypeValue);
	}

	void eValueQuoteCreation() {
		//Default VA test data didn't work, so had to use multiple adjustments
		TestData defaultTestData = getPolicyTD("DataGather", "TestData");
		TestData policyInformationSectionAdjusted = getTestSpecificTD("PolicyInformation").adjust("TollFree Number", "1");
		TestData currentCarrierSectionTestSpecific = getTestSpecificTD("CurrentCarrierInformation");
		TestData generalTabAdjusted = defaultTestData.getTestData("GeneralTab")
				.adjust("PolicyInformation", policyInformationSectionAdjusted)
				.adjust("CurrentCarrierInformation", currentCarrierSectionTestSpecific);

		TestData eValuePolicyData = defaultTestData
				.adjust("GeneralTab", generalTabAdjusted)
				.resolveLinks();

		mainApp().open();
		createCustomerIndividual();

		getPolicyType().get().createQuote(eValuePolicyData);
		String policyNum = PolicySummaryPage.getPolicyNumber();
		log.info("policyNum: {}", policyNum);
	}

	public void simplifiedQuoteIssue() {
		simplifiedQuoteIssue("");
	}

	/**
	 * Issues policy with 1 specific payment method
	 *
	 * @param paymentMethod - DC - Debit Card, CC - Credit Card, ACH - EFT
	 */
	public void simplifiedQuoteIssue(String paymentMethod) {
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.AGREEMENT).setValue("I agree");

		for (int i = 0; i < 3; i++) {
			if (DocumentsAndBindTab.btnPurchase.isPresent()) {
				DocumentsAndBindTab.btnPurchase.click();
				errorTab.overrideAllErrors();
			}
			if (Page.dialogConfirmation.isPresent()) {
				Page.dialogConfirmation.reject();
			}
		}
		policy.bind().submit();
		TestData purchaseTabData = getPolicyTD("DataGather", "TestData");
		if (!StringUtils.isEmpty(paymentMethod)) {
			purchaseTabData.adjust("PurchaseTab", getTestSpecificTD("PurchaseTab_" + paymentMethod));
		}
		new PurchaseTab().fillTab(purchaseTabData).submitTab();
	}

	public void simplifiedPendedEndorsementIssue() {
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		if (documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.AUTHORIZED_BY)
				.isPresent()) {
			documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.AUTHORIZED_BY)
					.setValue("Megha");
		}
		DocumentsAndBindTab.btnPurchase.click();
		Page.dialogConfirmation.confirm();

		ErrorTab errorTab = new ErrorTab();
		errorTab.overrideAllErrors();
		if (DocumentsAndBindTab.btnPurchase.isPresent()) {
			DocumentsAndBindTab.btnPurchase.click();
			Page.dialogConfirmation.confirm();
		}
	}

	private void checkBlueBoxMessages(String topic, List<String> messages) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.tableEValueMessages.getRow(1).getCell(1).verify.value(topic);
		List<String> currentValues = Arrays.asList(PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).getValue().split("\n"));
		CustomAssert.assertEquals("Blue Box contains wrong Messages", messages, currentValues);
	}

	private void selectMembershipOrPriorInsur(String membershipOrPrior, String value) {
		if ("membership".equals(membershipOrPrior)) {
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
			generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue(value);
		} else if ("priorCarior".equals(membershipOrPrior)) {
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
			if ("No".equals(value)) {
				generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER).setValue(value);
			} else if ("Yes".equals(value)) {
				TestData defaultTestData = getPolicyTD("DataGather", "TestData");
				TestData currentCarrierSectionAdjusted = getTestSpecificTD("CurrentCarrierInformation");
				TestData generalTabAdjusted = DataProviderFactory.emptyData().adjust("CurrentCarrierInformation", currentCarrierSectionAdjusted);
				TestData currentCarrierData = defaultTestData.adjust("GeneralTab", generalTabAdjusted);
				generalTab.fillTab(currentCarrierData);
			}
		}
	}

	private void endorsementDataGather(int days) {
		TestData adjustedEndorsementActionData = getPolicyTD("Endorsement", "TestData")
				.getTestData("EndorsementActionTab")
				.adjust("Endorsement Date", TimeSetterUtil.getInstance().getCurrentTime().minusDays(days).format(DateTimeUtils.MM_DD_YYYY));
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData")
				.adjust("EndorsementActionTab", adjustedEndorsementActionData));
	}

	private void checkBlueBoxMessagesWithDiffData(int days, String messageInfo, List<String> messageBullet, String messageInfo1, List<String> messageBullet1, String membershipOrPrior) {
		policy.dataGather().start();

		checkBlueBoxMessages(messageInfo, messageBullet);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		checkBlueBoxMessages(messageInfo, messageBullet);

		selectMembershipOrPriorInsur(membershipOrPrior, "No");
		checkBlueBoxMessages(messageInfo1, messageBullet1);

		selectMembershipOrPriorInsur(membershipOrPrior, "Yes");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		checkBlueBoxMessages(messageInfo, messageBullet);
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		simplifiedQuoteIssue();

		endorsementDataGather(days);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		checkBlueBoxMessages(messageInfo, messageBullet);
		selectMembershipOrPriorInsur(membershipOrPrior, "No");
		checkBlueBoxMessages(messageInfo1, messageBullet1);

		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		simplifiedPendedEndorsementIssue();
	}
}
