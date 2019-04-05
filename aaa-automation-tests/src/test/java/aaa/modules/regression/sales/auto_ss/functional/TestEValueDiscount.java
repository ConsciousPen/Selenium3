/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.helpers.rest.wiremock.dto.PaperlessPreferencesTemplateData.OPT_IN;
import static aaa.helpers.rest.wiremock.dto.PaperlessPreferencesTemplateData.OPT_OUT;
import static aaa.main.enums.DocGenEnum.Documents.AHEVAXX;
import static aaa.main.pages.summary.PolicySummaryPage.buttonRenewals;
import static toolkit.verification.CustomAssertions.assertThat;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableList;
import aaa.admin.modules.administration.generateproductschema.defaulttabs.CacheManager;
import aaa.common.Tab;
import aaa.common.efolder.Efolder;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.config.CsaaTestProperties;
import aaa.helpers.TestDataManager;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.DbAwaitHelper;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.rest.wiremock.HelperWireMockStub;
import aaa.helpers.rest.wiremock.dto.PaperlessPreferencesErrorTemplateData;
import aaa.helpers.rest.wiremock.dto.PaperlessPreferencesTemplateData;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.TestEValueDiscountPreConditions;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.config.PropertyProvider;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.waiters.Waiters;

public class TestEValueDiscount extends AutoSSBaseTest implements TestEValueDiscountPreConditions {

	private static final String PAPERLESS_WIRE_MOCK_STUB_URL = PropertyProvider.getProperty(CsaaTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE) + "/" + PropertyProvider.getProperty(CsaaTestProperties.APP_HOST) + "/policy/preferences";
	private static final String E_VALUE_DISCOUNT = "eValue Discount"; //PAS-440, PAS-235 - rumors have it, that discount might be renamed
	private static Map<String, Integer> policyCount = new HashMap<>();
	private static final String PP_ERROR_MESSAGE_NB = "eValue status set to Pending. Unable to verify Paperless Preferences.";
	private static final String PP_ERROR_MESSAGE_NB15 = "Discount in Jeopardy email sent. Unable to verify Paperless Preferences.";
	private static final String PP_ERROR_MESSAGE_NB30 = "Evalue information / Status was updated as : 'ACTIVE' for the policy based on Preferences and Membership logic. Unable to verify Paperless Preferences.";
	private static final String PP_UPDATED_OUTSIDE_OF_THE_PAS="Preferences Updated. Unable to verify Paperless Preferences for eValue.";

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
	private static final String MESSAGE_BULLET_9 = "Has not maintained continuous insurance coverage and bodily injury limits";
	//TODO replace PRIOR_CARRIER_CONTINUOUS_INSURANCE_MESSAGE with this bullet point afer 2018/04/20 if nightly run passes
	private static final String MESSAGE_BULLET_10 = "eValue Acknowledgement: Agree to and sign the eValue acknowledgement";
	private static final String MESSAGE_BULLET_11 = "Membership: Have an active membership";
	private static final String PRIOR_CARRIER_CONTINUOUS_INSURANCE_MESSAGE = "Has not maintained continuous insurance coverage and bodily injury limits";

	private static final String PAPERLESS_PREFERENCES_NOT_ENROLLED_1 =
			"In order to successfully bind with eValue discount,the customer must be enrolled into paperless preferences for Billing and Policy documents.";
	private static final String PAPERLESS_PREFERENCES_NOT_ENROLLED_2 = "The customer must choose to Opt In to Paperless Billing and Policy Documents ...";

	private static final List<String> PRE_QUALIFICATIONS = Arrays.asList(MESSAGE_BULLET_11, MESSAGE_BULLET_4_A, MESSAGE_BULLET_1, MESSAGE_BULLET_10, MESSAGE_BULLET_3);
	private static final List<String> NOT_PRE_QUALIFICATIONS = Arrays.asList(MESSAGE_BULLET_8, MESSAGE_BULLET_9, MESSAGE_BULLET_7);
	private static final List<String> MEMBERSHIP_FALSE_YES = Arrays.asList(MESSAGE_BULLET_4, MESSAGE_BULLET_1, MESSAGE_BULLET_10, MESSAGE_BULLET_3);
	private static final List<String> CURRENT_BI_FALSE_YES = Arrays.asList(MESSAGE_BULLET_11, MESSAGE_BULLET_1_A, MESSAGE_BULLET_10, MESSAGE_BULLET_3);
	private static final List<String> PAY_PLAN_FALSE_YES = Arrays.asList(MESSAGE_BULLET_11, MESSAGE_BULLET_4_A, MESSAGE_BULLET_10, MESSAGE_BULLET_3);
	private static final List<String> PAPERLESS_AND_PRIOR_INS_FALSE_YES = Arrays.asList(MESSAGE_BULLET_11, MESSAGE_BULLET_4_A, MESSAGE_BULLET_1, MESSAGE_BULLET_10);
	private static final List<String> ALL_FALSE = Arrays.asList(MESSAGE_BULLET_10);
	private static final String AUTOPAY_KEEP_EVALUE = "To keep the eValue discount, the customer must choose AutoPay to make recurring payments via a checking/savings account or debit card.";

	private static List<HelperWireMockStub> stubList = new LinkedList<>();
	private GeneralTab generalTab = new GeneralTab();
	private aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab generalTabHome = new aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab(); //TODO test with policy.dataGather().getView().getTab(DocumentsAndBindTab.class); instead of new Tab();
	private ErrorTab errorTab = new ErrorTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
	private UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();

	private static final String EVALUE_MEMBERSHIP_ACKNOWLEDGEMENT_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeValueQualifications", "membershipEligibility", "FALSE");

	private static final String EVALUE_CURRENT_BI_ACKNOWLEDGEMENT_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeValueQualifications", "currentBIRequired", "FALSE");

	private static final String EVALUE_PAYPLAN_ACKNOWLEDGEMENT_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeValueQualifications", "paymentPlanRequired", "FALSE");

	private static final String EVALUE_MYPOLICY_ACKNOWLEDGEMENT_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeValueQualifications", "myPolicyRequired", "FALSE");

	private static final String EVALUE_CREDITCARD_ACKNOWLEDGEMENT_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeValueQualifyingPaymentMethods", "pciCreditCard", "TRUE");

	private static final String EVALUE_PAPERLESS_PREFERENCES_BLUE_BOX_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeValueQualifications", "paperlessPreferencesRequired", "FALSE");

	private static final String EVALUE_PRIOR_INSURANCE_BLUE_BOX_CHECK =
			MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeValueQualifications", "priorInsurance", "FALSE");
	private VehicleTab vehicleTab = new VehicleTab();

	@Test(description = "Precondition", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void paperlessPreferencesStubEndpointConfigCheck() {
		assertThat(DBService.get().getValue(String.format(PAPERLESS_PREFERENCE_STUB_POINT, PAPERLESS_WIRE_MOCK_STUB_URL)).orElse(""))
				.as("paperless preference stub endpoint. Please run paperlessPreferencesStubEndpointUpdate").contains(PAPERLESS_WIRE_MOCK_STUB_URL);
	}

	public static void eValueConfigCheck() {
		assertThat(DBService.get().getValue(String.format(EVALUE_CONFIGURATION_PER_STATE_CHECK, getState())))
				.as("eValue is not configured for " + getState() + ". Insert configuration (run eValueConfigInsert) and restart the env").isPresent();
	}

	public static void eValuePriorBiConfigCheck() {
		assertThat(DBService.get().getValue(EVALUE_PRIOR_BI_CONFIG_CHECK)).as("eValue configuration for Prior BI limits is missing. Please run eValuePriorBiConfigUpdateInsert").isPresent();
	}

	public static void eValueCurrentBiConfigCheck() {
		assertThat(DBService.get().getValue(EVALUE_CURRENT_BI_CONFIG_CHECK)).as("eValue configuration for Current BI limits is missing. Please run eValueCurrentBiConfigUpdateInsert").isPresent();
	}

	public static void eValueMembershipConfigCheck() {
		assertThat(DBService.get().getValue(EVALUE_MEMBERSHIP_CONFIG_CHECK)).as("eValue configuration for membership not require. Please run eValueMembershipAcknowledgementConfigInsert").isPresent();
	}

	@Test(description = "Precondition", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public void precondJobAdding() {
		JobUtils.createJob(BatchJob.aaaBatchMarkerJob);
		JobUtils.createJob(BatchJob.aaaAutomatedProcessingInitiationJob);
		JobUtils.createJob(BatchJob.automatedProcessingRatingJob);
		JobUtils.createJob(BatchJob.automatedProcessingIssuingOrProposingJob);
		JobUtils.createJob(BatchJob.aaaPaymentCentralRejectFeedAsyncJob);
	}

	public static void eValueTerritoryChannelForORConfigCheck() {
		assertThat(DBService.get().getValue(EVALUE_TERRITORY_FOR_VA_CONFIG_CHECK)).as("Territory for VA is not configured, please run eValueTerritoryChannelForVAConfigUpdate").hasValue("212");
		assertThat(DBService.get().getValue(EVALUE_CHANNEL_FOR_VA_CONFIG_CHECK)).as("Channel for VA is not configured, please run eValueTerritoryChannelForVAConfigUpdate").hasValue("AZ Club Agent");
	}

	public static void eValueAcknowledgementConfigCheck() {
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			verifyAcknowledgementConfiguration(EVALUE_MEMBERSHIP_ACKNOWLEDGEMENT_CHECK, 10, 6, "eValueMembershipAcknowledgementConfigInsert", softly);
			verifyAcknowledgementConfiguration(EVALUE_MEMBERSHIP_ACKNOWLEDGEMENT_CHECK, 5, 1, "eValueMembershipAcknowledgementConfigInsert", softly);
			verifyAcknowledgementConfiguration(EVALUE_CURRENT_BI_ACKNOWLEDGEMENT_CHECK, 13, 11, "eValueCurrentBIAcknowledgementConfigInsert", softly);
			verifyAcknowledgementConfiguration(EVALUE_CURRENT_BI_ACKNOWLEDGEMENT_CHECK, 5, 1, "eValueCurrentBIAcknowledgementConfigInsert", softly);
			verifyAcknowledgementConfiguration(EVALUE_PAYPLAN_ACKNOWLEDGEMENT_CHECK, 20, 17, "eValuePayPlanAcknowledgementConfigInsert", softly);
			verifyAcknowledgementConfiguration(EVALUE_PAYPLAN_ACKNOWLEDGEMENT_CHECK, 5, 1, "eValuePayPlanAcknowledgementConfigInsert", softly);
			verifyAcknowledgementConfiguration(EVALUE_MYPOLICY_ACKNOWLEDGEMENT_CHECK, 16, 14, "eValueMyPolicyAcknowledgementConfigInsert", softly);
			verifyAcknowledgementConfiguration(EVALUE_MYPOLICY_ACKNOWLEDGEMENT_CHECK, 5, 1, "eValueMyPolicyAcknowledgementConfigInsert", softly);
			verifyAcknowledgementConfiguration(EVALUE_CREDITCARD_ACKNOWLEDGEMENT_CHECK, 13, 11, "eValueCreditCardAcknowledgementConfigInsert", softly);
			verifyAcknowledgementConfiguration(EVALUE_PAPERLESS_PREFERENCES_BLUE_BOX_CHECK, 16, 14, "eValuePaperlessPreferencesBlueBoxConfigInsert", softly);
			verifyAcknowledgementConfiguration(EVALUE_PAPERLESS_PREFERENCES_BLUE_BOX_CHECK, 5, 1, "eValuePaperlessPreferencesBlueBoxConfigInsert", softly);
			verifyAcknowledgementConfiguration(EVALUE_PRIOR_INSURANCE_BLUE_BOX_CHECK, 16, 14, "eValuePriorInsuranceBlueBoxConfigInsert", softly);
			verifyAcknowledgementConfiguration(EVALUE_PRIOR_INSURANCE_BLUE_BOX_CHECK, 5, 1, "eValuePriorInsuranceBlueBoxConfigInsert", softly);
		softly.close();
	}

	private static void verifyAcknowledgementConfiguration(String eValueConfiguration, int sysAndEffDateDelta, int sysAndExpDateDelta, String insertQuery, ETCSCoreSoftAssertions softly) {
		String query = MessageFormat.format(eValueConfiguration, sysAndEffDateDelta + 1, sysAndEffDateDelta, sysAndExpDateDelta + 1, sysAndExpDateDelta);
		softly.assertThat(DBService.get().getValue(query)).as("Configuration for acknowledgement should be present. Please run " + insertQuery).isPresent();
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
	 * TS4: Current AAA Member = 'Membership Override', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'Yes'
	 * TS5: Current AAA Member = 'Membership Override', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
	 * TS6: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed > 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
	 * TS7: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $15,000/$30,000, Apply eValue Discount  = 'No'
	 * 3. Verify that on P&C page 'eValue Discount' is present in Discounts & Surcharges table (for TS1 and TS4) and 'eValue Discount' is absent in Discounts & Surcharges table (for TS2, TS3, TS5, TS6 and TS7) .
	 * 4. Bind policy.
	 * 5. Verify that 'eEvalue Status' = 'Pending' (for TS1 and TS4) and 'eEvalue Status' is empty (for TS2, TS3, TS5, TS6 and TS7) on Consolidated page.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-436", "PAS-231", "PAS-288"})
	public void pas436_eValueDiscountVariations(@Optional("VA") String state) {
		eValueConfigCheck();
		testEvalueDiscount("AAAMembership_Active", "CurrentCarrierInformation", true, true, "Pending");
		testEvalueDiscount("AAAMembership_Active", "CurrentCarrierInformation", false, false, "");
		testEvalueDiscount("AAAMembership_No", "CurrentCarrierInformation", false, false, "");
		testEvalueDiscount("AAAMembership_Override", "CurrentCarrierInformation", true, true, "Pending");
		testEvalueDiscount("AAAMembership_Override", "CurrentCarrierInformation", false, false, "");
		testEvalueDiscount("AAAMembership_Active", "CurrentCarrierInformation_DayLapsedMore4", false, false, "");
		testEvalueDiscount("AAAMembership_Active", "CurrentCarrierInformation_BILimitLess", false, false, "");
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Configurations check
	 * @scenario 1. Check DB lookups
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-436", "PAS-231", "PAS-10359"})
	public void pas436_AAAeValueLookups(@Optional("VA") String state) {

		String eValueCurrentConfigPaInsert = "INSERT ALL\n"
				+ " INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
				+ " values ('BaseProductLookupValue', 'currentBIRequired', 'FALSE', 'AAA_SS', 'PA',(select to_char(to_date(SYSDATE-27, 'DD-MM-YYYY')) from dual), null ,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"

				+ "INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwritercd, EFFECTIVE, EXPIRATION, lookuplist_id) \n"
				+ "values ('AAARolloutEligibilityLookupValue', 'eValue', 'FALSE', 'AAA_SS', 'PA','212', 'AZ Club Agent', null, (select to_char(to_date(SYSDATE-27, 'DD-MM-YYYY')) from dual), null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))\n"

				+ "INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwritercd, EFFECTIVE, EXPIRATION, lookuplist_id) \n"
				+ "values ('AAARolloutEligibilityLookupValue', 'eValue', 'TRUE', 'AAA_SS', 'PA',null, 'AZ Independent Agent', null, (select to_char(to_date(SYSDATE-27, 'DD-MM-YYYY')) from dual), null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))\n"
				+ "Select * from dual";
		DBService.get().executeUpdate(eValueCurrentConfigPaInsert);

		String lookupCheckNoState = "select dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id\n"
				+ "from lookupvalue where 1=1\n"
				+ "and dtype = '%s'\n"
				+ "and code = '%s'\n"
				+ "and displayvalue = '%s'\n"
				+ "and PRODUCTCD = '%s'\n"
				+ "and RISKSTATECD is null\n"
				+ "and lookuplist_id = (select id from lookuplist where lookupname = '%s')";

		String lookupCheckWithState = "select dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id\n"
				+ "from lookupvalue where 1=1\n"
				+ "and dtype = '%s'\n"
				+ "and code = '%s'\n"
				+ "and displayvalue = '%s'\n"
				+ "and PRODUCTCD = '%s'\n"
				+ "and RISKSTATECD ='%s'\n"
				+ "and lookuplist_id = (select id from lookuplist where lookupname = '%s')";

		String lookupCheckNoStateNoProduct = "select dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id\n"
				+ "from lookupvalue where 1=1\n"
				+ "and dtype = '%s'\n"
				+ "and code = '%s'\n"
				+ "and displayvalue = '%s'\n"
				+ "and PRODUCTCD is null\n"
				+ "and RISKSTATECD is null\n"
				+ "and lookuplist_id = (select id from lookuplist where lookupname = '%s')";

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "annualSS", "TRUE", "AAA_SS", "AAAeValueQualifyingPaymentPlans"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "semiAnnual6SS", "TRUE", "AAA_SS", "AAAeValueQualifyingPaymentPlans"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "annualSS_R", "TRUE", "AAA_SS", "AAAeValueQualifyingPaymentPlans"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "semiAnnual6SS_R", "TRUE", "AAA_SS", "AAAeValueQualifyingPaymentPlans"))).isPresent();

			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "pciDebitCard", "TRUE", "AAA_SS", "AAAeValueQualifyingPaymentMethods"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "pciCreditCard", "FALSE", "AAA_SS", "AAAeValueQualifyingPaymentMethods"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "eft", "TRUE", "AAA_SS", "AAAeValueQualifyingPaymentMethods"))).isPresent();

			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "priorBILimits", "15000/30000", "AAA_SS", "AAAeValueQualifications"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "currentBILimits", "50000/100000", "AAA_SS", "AAAeValueQualifications"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "priorInsurance", "TRUE", "AAA_SS", "AAAeValueQualifications"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "membershipEligibility", "TRUE", "AAA_SS", "AAAeValueQualifications"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "currentBIRequired", "TRUE", "AAA_SS", "AAAeValueQualifications"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "paymentPlanRequired", "TRUE", "AAA_SS", "AAAeValueQualifications"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "myPolicyRequired", "TRUE", "AAA_SS", "AAAeValueQualifications"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoState, "BaseProductLookupValue", "paperlessPreferencesRequired", "TRUE", "AAA_SS", "AAAeValueQualifications"))).isPresent();

			softly.assertThat(DBService.get().getValue(String.format(lookupCheckWithState, "AAARolloutEligibilityLookupValue", "eValue", "TRUE", "AAA_SS", state, "AAARolloutEligibilityLookup"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoStateNoProduct, "AAARolloutEligibilityLookupValue", "AHDRXX", "TRUE",  "AAARolloutEligibilityLookup"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoStateNoProduct, "AAARolloutEligibilityLookupValue", "AHDEXX", "TRUE",  "AAARolloutEligibilityLookup"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoStateNoProduct, "AAARolloutEligibilityLookupValue", "PaperlessPreferences", "TRUE", "AAARolloutEligibilityLookup"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoStateNoProduct, "AAARolloutEligibilityLookupValue", "AHMVXX2", "FALSE", "AAARolloutEligibilityLookup"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoStateNoProduct, "AAARolloutEligibilityLookupValue", "AHMVNBXX", "FALSE",  "AAARolloutEligibilityLookup"))).isPresent();

			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoStateNoProduct, "AAARolloutEligibilityLookupValue", "eValue", "FALSE", "AAARolloutEligibilityLookup"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoStateNoProduct, "AAARolloutEligibilityLookupValue", "eRefunds", "TRUE", "AAARolloutEligibilityLookup"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoStateNoProduct, "AAARolloutEligibilityLookupValue", "eValueNotification", "TRUE", "AAARolloutEligibilityLookup"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckNoStateNoProduct, "AAARolloutEligibilityLookupValue", "vinRefresh", "TRUE", "AAARolloutEligibilityLookup"))).isPresent();

			//new state specific configs for NJ/NY/MD- PAS-10359
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckWithState, "BaseProductLookupValue", "membershipEligibility", "FALSE", "AAA_SS", "NJ", "AAAeValueQualifications"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckWithState, "BaseProductLookupValue", "priorInsurance", "FALSE", "AAA_SS", "NJ", "AAAeValueQualifications"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckWithState, "BaseProductLookupValue", "priorInsurance", "FALSE", "AAA_SS", "NY", "AAAeValueQualifications"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckWithState, "BaseProductLookupValue", "currentBIRequired", "FALSE", "AAA_SS", "NJ", "AAAeValueQualifications"))).isPresent();
			softly.assertThat(DBService.get().getValue(String.format(lookupCheckWithState, "BaseProductLookupValue", "pciCreditCard", "TRUE", "AAA_SS", "MD", "AAAeValueQualifyingPaymentMethods"))).isPresent();
		softly.close();
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
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-305")
	public void pas305_eValueDiscountApplied(@Optional("VA") String state) {
		eValueQuoteCreation();

		policy.dataGather().start();

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			//PAS-439, PAS-234 start
			softly.assertThat(generalTab.isFieldThatIsNotInAssetListIsPresent("Apply eValue Discount")).as("Control 'Apply eValue Discount' should not be present").isFalse();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

			//Check field properties and default value of eValue Discount
			softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isPresent();
			softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();
			softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("No");
			//PAS-439, PAS-234 end
			//PAS-305 start
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT)).isFalse();
			//PAS-305 end

			//Get premiums before discount is applied
			Dollar policyLevelLiabilityCoveragesPremiumWithoutEvalueDiscount = premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium();
			Dollar vehicleCoveragePremiumWithoutEvalueDiscount = premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle(1);
			Dollar totalPremiumWithoutEvalueDiscount = policyLevelLiabilityCoveragesPremiumWithoutEvalueDiscount.add(vehicleCoveragePremiumWithoutEvalueDiscount);

			//PAS-305 start
			PremiumAndCoveragesTab.RatingDetailsView.open();
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT).getCell(4)).hasValue("None");
			PremiumAndCoveragesTab.RatingDetailsView.close();
			//PAS-305 end

			//PAS-2053
			premiumAndCoveragesTab.saveAndExit();
			softly.assertThat(PolicySummaryPage.tableAppliedDiscountsPolicy.getRowContains(2, E_VALUE_DISCOUNT)).isPresent(false);
			//PAS-2053

			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

			//Set discount to Yes
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
			//PAS-304 start
			premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium().verify.equals(new Dollar(0));
			//PAS-304 end

			new PremiumAndCoveragesTab().calculatePremium();

			//PAS-305 start
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT)).isTrue();
			//PAS-305 end

			//Get premiums after discount is applied
			Dollar policyLevelLiabilityCoveragesPremiumWithEvalueDiscount = premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium();
			Dollar vehicleCoveragePremiumWithEvalueDiscount = premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle(1);
			Dollar totalPremiumWithEvalueDiscount = policyLevelLiabilityCoveragesPremiumWithEvalueDiscount.add(vehicleCoveragePremiumWithEvalueDiscount);

			log.info("totalPremiumWithoutEvalueDiscount: {}", totalPremiumWithoutEvalueDiscount);
			log.info("totalPremiumWithEvalueDiscount: {}", totalPremiumWithEvalueDiscount);

			//Compare premiums before discount and after
			softly.assertThat(totalPremiumWithoutEvalueDiscount.moreThan(totalPremiumWithEvalueDiscount)).isTrue();
			//PAS-2053 eValue Status on Policy Summary Page - Don't Show it When not enabled

			//PAS-305, PAS-310 start
			PremiumAndCoveragesTab.RatingDetailsView.open();
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT).getCell(4)).hasValue("Yes");
			PremiumAndCoveragesTab.RatingDetailsView.close();
			//PAS-305, PAS-310 end

			//PAS-2053
			premiumAndCoveragesTab.saveAndExit();
			softly.assertThat(PolicySummaryPage.tableAppliedDiscountsPolicy.getRowContains(2, E_VALUE_DISCOUNT)).isPresent();
			//PAS-2053

			//PAS-276 start
			policy.dataGather().start();
			new PremiumAndCoveragesTab().calculatePremium();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			softly.assertThat(documentsAndBindTab.getGeneralInformationAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL)).isPresent().isEnabled().isRequired();
			documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION.getLabel(), AssetList.class)
					.getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL).setValue("");
			DocumentsAndBindTab.btnPurchase.click();
			assertThat(documentsAndBindTab.getGeneralInformationAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL).getWarning().get()).contains("'Email' is required");
			//PAS-276 end
		softly.close();
	}

	/**
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 1. Create new eValue eligible policy with membership override and paperless preferences yes evalue no
	 * 2. Check policy consolidated view.
	 * 3. See if eValue status = No
	 * 4. DB check for evalue status in the Database NOTENROLLED
	 * @details
	 * @scenario 3. Create new eValue eligible policy with membership yes and paperless preferences yes evalue yes
	 * 2. Check policy consolidated view.
	 * 3. See if eValue status = active
	 * 4. DB check for evalue status in the Database Pending
	 * @scenario 4. Create new eValue eligible policy with membership yes and paperless preferences yes evalue yes
	 * 2. while doing endorsment set evalue no
	 * 3. DB check for evalue status in the Database Inactive
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-300")
	public void pas300_eValueStatusConsViewPaperPrefYes(@Optional("VA") String state) {
		eValueQuoteCreation();

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Membership Override");
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBER_SINCE_DATE).setValue("01/01/2016");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			//PAS-302 start VC4
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("");
			softly.assertThat(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber))).hasValue("NOTENROLLED");
			//PAS-302 end

			TestEValueMembershipProcess testEValueMembershipProcess = new TestEValueMembershipProcess();
			testEValueMembershipProcess.membershipEligibilityEndorsementCreation("Active");

			softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("Active");
			//PAS-302 start VC2
			softly.assertThat(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber))).hasValue("ACTIVE");
			//PAS-302 end

			//PAS-302 start VC3
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
			//BUG PAS-6591 PAS-316 There is no Dialog Confirmation popup when setting EValue to No in endorsement
			Page.dialogConfirmation.confirm();
			new PremiumAndCoveragesTab().calculatePremium();
			premiumAndCoveragesTab.saveAndExit();
			simplifiedPendedEndorsementIssue();
			softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("");
			softly.assertThat(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber))).hasValue("INACTIVE");
			//PAS-302 end
		softly.close();
	}

	/**
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 2. Create new eValue eligible policy with membership override and paperless preferences yes evalue yes
	 * 2. Check policy consolidated view.
	 * 3. See if eValue status = Pending
	 * 4. DB check for evalue status in the Database Pending
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-300")
	public void pas300_eValueStatusConsViewPaperPrefPendingVa(@Optional("VA") String state) {
		eValueConfigCheck();
		eValueQuoteCreation();

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Membership Override");
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBER_SINCE_DATE).setValue("01/01/2016");

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("");
			simplifiedQuoteIssue();

			//BUG PAS-4279 Evalue status showing wrong
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("Pending");

			//PAS-302 start VC1
			softly.assertThat(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber))).hasValue("PENDING");
			//PAS-302 end
		softly.close();
	}

	/**
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 1. Create new eValue eligible policy with membership pending and paperless preferences Pending
	 * 2. Check policy consolidated view.
	 * 3. See if eValue status = Pending
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-300")
	public void pas300_eValueStatusConsViewPaperPrefPendingDc(@Optional("DC") String state) {
		eValueConfigCheck();
		eValueQuoteCreation();

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("");
		simplifiedQuoteIssue("ACH");
		assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("Pending");
	}

	/**
	 * @author Oleg Stasyuk
	 * 	 * @name eValue discount is applicable, when issueing policy with Payment Method DC
	 * 	 * @scenario 1. Create new eValue Quote with Pay Plan <> Annual
	 * 	 * 2. Issue it using payment method DC
	 * 	 * 3. See if eValue status = Pending
	 * 	 * @details
	 * @param state
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-281")
	public void pas281_eValueIssuedWithDebitCard(@Optional("VA") String state) {
		eValueConfigCheck();
		eValueQuoteCreation();

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("contains=Standard");
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("");

		simplifiedQuoteIssue("DC");
		assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("Pending");
	}

	/**
	 * @author Oleg Stasyuk
	 * @name eValue discount is applicable, when issueing policy with Payment Method DC
	 * @scenario 1. Create new eValue eligible Quote with Pay Plan <> Annual
	 * 2. Issue it using payment method DC
	 * 3. Start an endorsement, set eValue, check message about Autopay is present
	 * 4. Try to issue, see there is an error about Autopay preventing issue
	 * 5. Start a renewal, set eValue, check message about Autopay is present
	 * 6. Try to issue, see there is an error about Autopay preventing issue
	 * @details
	 * @param state
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-321", "PAS-317"})
	public void pas321_eValueAtRenewalOrMidtermNoAutopay(@Optional("VA") String state) {
		eValueConfigCheck();
		eValueQuoteCreation();

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("contains=Standard");
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("");

		simplifiedQuoteIssue();
		assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("");

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		autopayKeepMessageEndorseRenewCheck();

		policy.renew().start();
		autopayKeepMessageEndorseRenewCheck();
	}

	private void autopayKeepMessageEndorseRenewCheck() {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("contains=Standard");
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		assertThat(PremiumAndCoveragesTab.tableeMemberMessageGrid.getRow(1).getCell(4)).hasValue(AUTOPAY_KEEP_EVALUE);
		premiumAndCoveragesTab.calculatePremium();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
		assertThat(errorTab.tableErrors.getRowContains("Code", "AAA_SS11020532").getCell("Message").getValue().toLowerCase().contains(AUTOPAY_KEEP_EVALUE.toLowerCase().substring(0, 30))).isTrue();
		errorTab.cancel();
		documentsAndBindTab.saveAndExit();
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
	 * @NOTE pas436_AAAeValueLookups (eValueCurrentConfigPaInsert) has to be run before this test so that eValue is configured properly for PA
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3708")
	public void pas3708_eValueStatusConsViewNotConfigured(@Optional("PA") String state) {
		eValueConfigCheck();
		eValueQuoteCreation();

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isPresent(false);
			new PremiumAndCoveragesTab().calculatePremium();
			premiumAndCoveragesTab.saveAndExit();
			softly.assertThat(PolicySummaryPage.tableGeneralInformation.getHeader().getValue().contains("eValue Status")).isFalse();
			simplifiedQuoteIssue();
			softly.assertThat(PolicySummaryPage.tableGeneralInformation.getHeader().getValue().contains("eValue Status")).isFalse();
		softly.close();
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
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-325")
	public void pas325_eValueCommissionRelatedFields(@Optional("VA") String state) {
		eValueConfigCheck();
		List<String> expectedNonEvalueCommissionTypeOptions = Arrays.asList("New Business", "Renewal");
		List<String> expectedEvalueCommissionTypeOptions = Arrays.asList("eValue New Business", "eValue Renewal");

		eValueQuoteCreation();

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			//PAS-306, PAS-320, PAS-323 start
			commissionTypeCheck(expectedNonEvalueCommissionTypeOptions, "No", "New Business", softly);
			commissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue New Business", softly);
			//PAS-306, PAS-320, PAS-323 end

			//PAS-2054 start
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
			new PremiumAndCoveragesTab().calculatePremium();
			softly.assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE)).isAbsent();
			//PAS-2054 end
			//PAS-325 end
			premiumAndCoveragesTab.saveAndExit();
			simplifiedQuoteIssue();
			String originalPolicyNumber = PolicySummaryPage.getPolicyNumber();

			//Inquiry doesn't show the field
			policy.policyInquiry().start();
			softly.assertThat(generalTab.getPolicyInfoInquiryAssetList().getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE)).isAbsent();
			generalTab.cancel();

			//Endorsement doesn't show the field
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
			softly.assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE)).isAbsent();
			//PAS-306, PAS-320, PAS-323, PAS-289 start
			commissionTypeCheck(expectedEvalueCommissionTypeOptions, "No", "eValue New Business", softly);//because the issue happened with eValue Discount = True
			commissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue New Business", softly);
			pas316_eValueRemovalPopUpCheck();
			//PAS-306, PAS-320, PAS-323, PAS-289 end
			generalTab.cancel(false);
			Page.dialogConfirmation.buttonDeleteEndorsement.click();

			//Renewal doesn't show the field
			policy.renew().start();
			softly.assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE)).isAbsent();
			//PAS-306, PAS-320, PAS-323, PAS-318, PAS-292 start
			commissionTypeCheck(expectedEvalueCommissionTypeOptions, "No", "eValue Renewal", softly);
			commissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue Renewal", softly);
			//PAS-306, PAS-320, PAS-323, PAS-318, PAS-292 end
			pas316_eValueRemovalPopUpCheck();
			generalTab.saveAndExit();

			//Cancel and Rewrite
			policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
			policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
			softly.assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE)).isPresent();
			//There might be a new requirement to default the field to yes for the rewrite and Split in case if original policy had eValue Discount=true
			softly.assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE)).hasValue("No");
			//PAS-306, PAS-320, PAS-323 start
			softly.assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE)).containsAllOptions(expectedNonEvalueCommissionTypeOptions);
			softly.assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE)).hasValue("Renewal");
			//PAS-306, PAS-320, PAS-323 end

			//Logic requested by business - not to carry over eValue from Original Policy and to have Commission Type dependent on HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE value which is set by Agent
			generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE)
					.setValue("Yes");
			//BUG PAS-12248 When doing rewrite of eValue policy, Commission Type has no eValue* values
			softly.assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE)).hasValue("eValue Renewal");
			generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE).setValue("No");
			softly.assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE)).hasValue("Renewal");
			generalTab.cancel();

			//PAS-302 start
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, originalPolicyNumber);
			policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData_Plus14Days"));
			TestData adjustedEndorsementActionData = getPolicyTD("Endorsement", "TestData").getTestData("EndorsementActionTab").adjust("Endorsement Date", "$<today+15d>");
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData").adjust("EndorsementActionTab", adjustedEndorsementActionData));

			//PAS-306, PAS-320, PAS-323 start
			commissionTypeCheck(expectedEvalueCommissionTypeOptions, "No", "eValue New Business", softly);//because the Issue Action happened with eValue Discount = True, and the agent is locked in eValue commissions forever
			commissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue New Business", softly);
			//PAS-306, PAS-320, PAS-323 end

			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("Yes");
			//PAS-302 end
		softly.close();
	}

	private void pas316_eValueRemovalPopUpCheck() {
		//PAS-316 start
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		//BUG PAS-6591 PAS-316 There is no Dialog Confirmation popup when setting EValue to No in endorsement
		assertThat(Page.dialogConfirmation.isPresent()).isTrue();
		assertThat(Page.dialogConfirmation.labelMessage).hasValue("If you remove the eValue discount, the premium will increase. Are you sure you want to remove the discount?");
		Page.dialogConfirmation.reject();
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("Yes");
		//PAS-316 end
	}

	/**
	 * @author Oleg Stasyuk
	 * @name PA Default Config
	 * @scenario 1. Create new eValue eligible quote but for PA Eligible state (PA)
	 *
	 * Membership is required
	 * Prior BI is required - same as VA
	 * No payment plan is required
	 * No current BI requirement
	 * South Jersey is not included - Used Mid-Atlantic/AAA Agent
	 * @details
	 * @NOTE pas436_AAAeValueLookups (eValueCurrentConfigPaInsert) has to be run before this test so that eValue is configured properly for PA
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-12972", "PAS-438", "PAS-288"})
	public void pas12972_defaultConfigPA(@Optional("PA") String state) {
		eValueQuoteCreation();

		//Precondition for test
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(26).format(DateTimeUtils.MM_DD_YYYY));

		//Agent/Agency config check
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.CHANNEL_TYPE).setValue("AAA Agent");
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY).setValue("contains=Mid-Atlantic");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//BUG PAS-13339 Agency Configuration for eValue doesnt work if FALSE record is inserted in the DB after TRUE record
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isPresent(false);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.CHANNEL_TYPE).setValue("Independent Agent");
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY).setValue("index=1");
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AGENT).setValue("index=1");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isPresent();
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();

		//Current BI check
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		ComboBox biAsset = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY);
		ComboBox pdAsset = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY);
		biAsset.setValueByIndex(0);
		pdAsset.setValueByIndex(0);
		premiumAndCoveragesTab.calculatePremium();
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isPresent();
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();

		//Membership check
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("No");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isDisabled();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER).setValue("4290023667710001");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();

		//PriorBi
		pas232_eValuePriorBiConfigurationDependencyCheck("None", "$15,000/$30,000");
		pas232_eValuePriorBiNoneConfigurationDependencyCheck();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name PA Default Config
	 * @scenario 1. Create new eValue eligible quote but for PA Eligible state (PA)
	 *
	 * Membership is required
	 * Prior BI is required - same as VA
	 * No payment plan is required
	 * No current BI requirement
	 * South Jersey is not included - Used Mid-Atlantic/AAA Agent
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-317"})
	public void pas317_membershipAndBiForRenewal(@Optional("VA") String state) {
		eValueQuoteCreation();
		simplifiedQuoteIssue("ACH");
		//Precondition for test
		policy.renew().start();

		//Current BI check
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		ComboBox biAsset = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY);
		ComboBox pdAsset = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY);
		ComboBox umbiAsset = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY);
		ComboBox umpdAsset = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_PROPERTY_DAMAGE);
		biAsset.setValueByIndex(0);
		Page.dialogConfirmation.confirm();
		pdAsset.setValueByIndex(0);
		umbiAsset.setValueByIndex(0);
		umpdAsset.setValueByIndex(0);

		premiumAndCoveragesTab.calculatePremium();
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isPresent();
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");

		//Membership check
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("No");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isDisabled();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER).setValue("4290023667710001");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();

		//PriorBi
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue("$15,000/$30,000");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER).setValue("None");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER).setValue("No");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount not shown for state where it is not configured
	 * @scenario 1. Create new eValue eligible quote but for the not eligible state (PA)
	 * 1.1. Check "Has the insured ever been enrolled in eValue?" is not shown for Non-Applicable state
	 * 2. Check eValue Discount field is not shown in P&C
	 * 3. Check eValue Discount field is not shown in Rating Details
	 * @details
	 * @NOTE pas436_AAAeValueLookups (eValueCurrentConfigPaInsert) has to be run before this test so that eValue is configured properly for PA
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-305")
	public void pas305_eValueNotApplicableForState(@Optional("PA") String state) {
		eValueConfigCheck();
		eValueQuoteCreation();

		policy.dataGather().start();

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			//PAS-325 start
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
			assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE)).isPresent(false);
			//PAS-325 end

			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

			softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isPresent(false);
			//PAS-309 start
			softly.assertThat(PremiumAndCoveragesTab.tableEValueMessages).isPresent(false);
			//PAS-309 end
			//PAS-305 start
			PremiumAndCoveragesTab.RatingDetailsView.open();
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT)).isPresent(false);
			PremiumAndCoveragesTab.RatingDetailsView.close();
			//PAS-305 end
			//PAS-2053 start
			premiumAndCoveragesTab.saveAndExit();
			softly.assertThat(PolicySummaryPage.tableAppliedDiscountsPolicy.getRowContains(2, E_VALUE_DISCOUNT)).isPresent(false);
			//PAS-2053 end

			//PAS-276 start
			policy.dataGather().start();
			new PremiumAndCoveragesTab().calculatePremium();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			InquiryAssetList inquiryAssetDocumentTabGeneralInfoSection = new InquiryAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.class);
			softly.assertThat(inquiryAssetDocumentTabGeneralInfoSection.getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL)).isPresent();
			documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION.getLabel(), AssetList.class)
					.getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL).setValue("");

			DocumentsAndBindTab.btnPurchase.click();
			softly.assertThat(errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS6591343")).isPresent(false);
			//PAS-276 end
		softly.close();
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
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-278", "PAS-721", "PAS-3647"})
	public void pas278_eValueeSignedPledgeDocumentAHEVAXX(@Optional("VA") String state) {
		eValueConfigCheck();
		eValueQuoteCreation();

		policy.dataGather().start();

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			//PAS-264 start
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
			new PremiumAndCoveragesTab().calculatePremium();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			softly.assertThat(documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT)).isPresent(false);
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT)).isPresent(false);

			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
			new PremiumAndCoveragesTab().calculatePremium();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			softly.assertThat(documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT)).isPresent();
			softly.assertThat(documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT)).hasValue("Yes");
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT)).isPresent();
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT)).hasValue("Not Signed");

			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT))
					.containsAllOptions("Physically Signed", "Electronically Signed", "Voice Signed", "Not Signed");

			DocumentsAndBindTab.btnPurchase.click();
			//PAS-293 start
			softly.assertThat(errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS8120577").getCell("Message"))
					.hasValue("A signed eValue Acknowledgement must be received prior to issuing this transa...");
			errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS8120577").getCell("Code").controls.links.get(1).click();
			documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).setValue("Electronically Signed");

			DocumentsAndBindTab.btnPurchase.click();
			//if clause used fot the case if there will be no overridable errors
			if (errorTab.getErrorsControl().getTable().isPresent()) {
				softly.assertThat(errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS8120577")).isPresent(false);
				errorTab.cancel();
			} else if (Page.dialogConfirmation.isPresent()) {
				Page.dialogConfirmation.reject();
			}
			//PAS-293 end
			//PAS-264 end
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
			new PremiumAndCoveragesTab().calculatePremium();
			String policyNum = documentsAndBindTab.getPolicyNumber();

			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).setValue("Yes");

			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_ESIGNATURE_DOCUMENTS)
					.click(Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
			documentsAndBindTab.getEnterRecipientEmailAddressDialogAssetList()
					.getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.RECIPIENT_EMAIL_ADDRESS)
					.setValue("test@email.com");
			documentsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.BTN_OK).click();
			Page.dialogConfirmation.buttonOk.click();

			//Delay is required for the document to appear in DB and in eFolder
			//PAS-264 start
			String query = GET_DOCUMENT_BY_EVENT_NAME + "and data like '%%ESignatureChannel%%'";
			String queryFull = String.format(query, policyNum, "AHEVAXX", "ADHOC_DOC_GENERATE");
			softly.assertThat(DbAwaitHelper.waitForQueryResult(queryFull, 60)).isTrue();
			log.info("Delay start");
			Waiters.SLEEP(60000).go();
			log.info("Delay end");
			documentsAndBindTab.saveAndExit();
			SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, policyNum);
			//BUG INC0655981: summary: "New PAS18.3 Master - AHEVAXX for is not placed in eFolder"
			softly.assertThat(Efolder.isDocumentExist("MISCELLANEOUS", "EVALUE ACKNOWLEDGEMENT FORM")).isTrue();
			//PAS-264 end

			//PAS-721 Start
			String queryFull2 = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNum, "AHEPXX", "ADHOC_DOC_GENERATE");
			softly.assertThat(DBService.get().getValue(queryFull2).isPresent()).isFalse();
			//PAS-721 End
		softly.close();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test there is a message, when Autopay is nto set and Payment Plan <> Annual
	 * @scenario 1. Create new eValue policy
	 * 2. Start endorsement, set Payment plan to non-Annual
	 * 3. Check Message
	 * 4. Start renewal, set Payment plan to non-Annual
	 * 5. Check Message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-295", "PAS-311"})
	public void pas295_autopayKeepValueMessage(@Optional("VA") String state) {
		eValueConfigCheck();
		eValueQuoteCreation();

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.submitTab();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		autopayKeepValueMessageCheck();

		policy.renew().start().submit();
		autopayKeepValueMessageCheck();
	}

	private void autopayKeepValueMessageCheck() {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("contains=Standard");
		assertThat(PremiumAndCoveragesTab.tableeMemberMessageGrid.getRow(1).getCell(4)).hasValue(AUTOPAY_KEEP_EVALUE);
		premiumAndCoveragesTab.saveAndExit();
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
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3694", "PAS-2789", "PAS-299", "PAS-2794"})
	public void pas3694_eValueBlueBoxAndStaticText(@Optional("OR") String state) {
		eValueConfigCheck();
		eValueQuoteCreation();

		policy.dataGather().start();

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		checkBlueBoxMessages(MESSAGE_INFO_4, PRE_QUALIFICATIONS, softly);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("No");
		checkBlueBoxMessages(MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS, softly);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER).setValue("No");
		checkBlueBoxMessages(MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS, softly);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");
		checkBlueBoxMessages(MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS, softly);

		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		TestData defaultTestData = getPolicyTD("DataGather", "TestData");
		TestData currentCarrierSectionAdjusted = getTestSpecificTD("CurrentCarrierInformation");
		TestData generalTabAdjusted = DataProviderFactory.emptyData().adjust("CurrentCarrierInformation", currentCarrierSectionAdjusted);
		TestData currentCarrierData = defaultTestData.adjust("GeneralTab", generalTabAdjusted);
		generalTab.fillTab(currentCarrierData);

		checkBlueBoxMessages(MESSAGE_INFO_4, PRE_QUALIFICATIONS, softly);
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("No");
		checkBlueBoxMessages(MESSAGE_INFO_4, PRE_QUALIFICATIONS, softly);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		checkBlueBoxMessages(MESSAGE_INFO_4, PRE_QUALIFICATIONS, softly);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("No");
		checkBlueBoxMessages(MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS, softly);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER).setValue("No");
		checkBlueBoxMessages(MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS, softly);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");
		checkBlueBoxMessages(MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS, softly);
		softly.close();

		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		simplifiedPendedEndorsementIssue();
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-232", "PAS-437", "PAS-3008", "PAS-438", "PAS-288"})
	public void pas232_eValuePriorBiConfigurationDependency(@Optional("OR") String state) {
		eValuePriorBiConfigCheck();
		eValueQuoteCreation();

		policy.dataGather().start();
		pas232_eValuePriorBiConfigurationDependencyCheck("$25,000/$50,000", "$50,000/$100,000");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(TimeSetterUtil
				.getInstance().getCurrentTime().minusDays(8).format(DateTimeUtils.MM_DD_YYYY));
		pas232_eValuePriorBiConfigurationDependencyCheck("$20,000/$40,000", "$25,000/$50,000");
		pas232_eValuePriorBiNoneConfigurationDependencyCheck();
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-233", "PAS-438"})
	public void pas233_eValueTerritoryChannelDependency(@Optional("OR") String state) {
		eValueTerritoryChannelForORConfigCheck();
		eValueQuoteCreation();

		policy.dataGather().start();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		eValueDependencyOnTerrirotyChannelCheck(getTestSpecificTD("PolicyInformationForTerritoriesEValueNotApplicable1"), false, softly);

		eValueDependencyOnTerrirotyChannelCheck(getTestSpecificTD("PolicyInformationForTerritoriesEValueApplicable"), true, softly);

		eValueDependencyOnTerrirotyChannelCheck(getTestSpecificTD("PolicyInformationForTerritoriesEValueNotApplicable2"), false, softly);
		softly.close();
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4264")
	public void pas4264_ShowAllBILimitsWhenEvalueIsSelected(@Optional("VA") String state) {
		eValueCurrentBiConfigCheck();
		RadioGroup applyEvalueDiscountAsset = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT);
		ComboBox biAsset = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY);
		eValueQuoteCreation();

		verifyBILimitNB(applyEvalueDiscountAsset, biAsset);
		verifyBILimitEndorsement(applyEvalueDiscountAsset, biAsset);
		verifyBILimitRenewal();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Midterm Opt In - Notification to Agent about Paperless Preferences
	 * @scenario 1. Create new eValue eligible quote.
	 * 2. Set Paperless Preference to Opt Out.
	 * 3. Bind Policy, check eValueDiscount field is disabled in P&C tab
	 * 4. set eValue = Yes in P&C tab
	 * 5. Check if error message is not displayed, when response from API is OPT_OUT. (Data gather mode)
	 * 6. set eValue = No in P&C tab
	 * 7. Bind the policy.
	 * 8. Do midTerm Endorsement ( +5d first endorsement).
	 * 9. Set eValue = Yes in P&C tab
	 * 10. Check if error message is displayed, when response from API is OPT_OUT.
	 * 11. Navigate to the Documents & Bind Page.
	 * 12. Check if Paperless Preference is No.
	 * 13. Try to bind endorsement. Check if error message is displayed.
	 * 14. Go back to P&C page, and change eValue to No.
	 * 15. Calculate premium and bind endorsement.
	 * 16. Start create second endorsement (+10d.)
	 * 17. Set Paperless Preference to Opt In.
	 * 18. Go to Documents & Bind Page, and check if Paperless Preference is Yes.
	 * 19. Go back to the P&C tab.
	 * 20. Check if error message is not displayed, when response from API is OPT_IN.
	 * 21. Bind endorsement, check if policy status is active.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-297", "PAS-296"})
	public void pas297_MidTermOptInNotificationToAgentAboutPaperlessPreferences(@Optional("VA") String state) {

		eValueQuoteCreation();
		String quoteNumber = PolicySummaryPage.getPolicyNumber();
		HelperWireMockStub stub = createPaperlessPreferencesRequestId(quoteNumber, OPT_OUT);

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		assertThat(PremiumAndCoveragesTab.eValuePaperlessWarning.getValue().contains(PAPERLESS_PREFERENCES_NOT_ENROLLED_1)).isFalse();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");

		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();

		deleteSinglePaperlessPreferenceRequest(stub);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		//Start PAS-296
		//first endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));
		HelperWireMockStub stub2 = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).hasValue("No");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		assertThat(PremiumAndCoveragesTab.eValuePaperlessWarning.getValue().contains(PAPERLESS_PREFERENCES_NOT_ENROLLED_1))
				.isTrue();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
		assertThat(errorTab.tableErrors.getRow(1).getCell("Message").getValue()).isEqualTo(PAPERLESS_PREFERENCES_NOT_ENROLLED_2);
		errorTab.cancel();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		Page.dialogConfirmation.confirm();
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
		deleteSinglePaperlessPreferenceRequest(stub2);

		//start new endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
		HelperWireMockStub stub3 = createPaperlessPreferencesRequestId(policyNumber, OPT_IN);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).hasValue("Yes");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		assertThat(PremiumAndCoveragesTab.eValuePaperlessWarning.getValue().contains(PAPERLESS_PREFERENCES_NOT_ENROLLED_1)).isFalse();
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).setValue("Physically Signed");
		documentsAndBindTab.submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		deleteSinglePaperlessPreferenceRequest(stub3);
		//End PAS-296
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3007")
	public void pas3007_eValueMembershipConfiguration(@Optional("OR") String state) {
		eValueMembershipConfigCheck();

		eValueQuoteCreation();

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("No");

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//TODO Is it the only verification in test case? Why do we need other steps than?
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isDisabled();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(8).format(DateTimeUtils.MM_DD_YYYY));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();
	}

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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3693", "PAS-2794", "PAS-3685"})
	public void pas3693_eValueConfiguration1(@Optional("OR") String state) {
		eValueAcknowledgementConfigCheck();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		pas3685_verifyEvalueAcknowledgement(8, "N", "Y", "Y", "Y", "Y", softly);
		checkBlueBoxMessagesWithDiffData(8, MESSAGE_INFO_4, MEMBERSHIP_FALSE_YES, MESSAGE_INFO_4, MEMBERSHIP_FALSE_YES, "membership", softly);
		softly.close();
	}

	private void processEvalueDiscountPopUp(boolean deletePopup, ETCSCoreSoftAssertions softly) {
		softly.assertThat(Page.dialogConfirmation.isPresent()).isTrue();
		softly.assertThat(Page.dialogConfirmation.labelMessage)
				.hasValue("BI limit of at least " + CONFIGURED_BI_LIMIT + " must be selected when eValue discount is applied. Please select Yes to confirm this change.");
		if (deletePopup) {
			Page.dialogConfirmation.confirm();
		} else {
			Page.dialogConfirmation.reject();
		}
	}

	private void verifyBILimitNB(RadioGroup applyEvalueDiscountAsset, ComboBox biAsset) {
		ComboBox uumbiAsset = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY);
		policy.dataGather().start();
		testDefaultBILimitValue(applyEvalueDiscountAsset, biAsset, uumbiAsset);
		testRemoveEvalueDiscountPopup(applyEvalueDiscountAsset, biAsset);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("Annual");
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		fillDocumentAndBindTab(true);
		purchaseTab.fillTab(getPolicyTD()).submitTab();
	}

	private void verifyBILimitEndorsement(RadioGroup applyEvalueDiscountAsset, ComboBox biAsset) {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		testRemoveEvalueDiscountPopup(applyEvalueDiscountAsset, biAsset);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("Annual");
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab.btnPurchase.click();
		Page.dialogConfirmation.confirm();
	}

	private void verifyBILimitCoverages(ComboBox biAsset, ComboBox uumbiAsset, ETCSCoreSoftAssertions softly) {
		verifyBILimits(biAsset, softly);
		verifyBILimits(uumbiAsset, softly);
	}

	private boolean isTotalTermPremiumEquals0() {
		return "$0.00".equals(PremiumAndCoveragesTab.totalTermPremium.getValue());
	}

	private void verifyBILimits(ComboBox biAsset, ETCSCoreSoftAssertions softly) {
		List<String> actualBILimits = biAsset.getAllValues();
		softly.assertThat(actualBILimits).as("Incorrect BI limits numbers in dropdown").hasSameSizeAs(EXPECTED_BI_LIMITS);
		for (String expectedBILimit : EXPECTED_BI_LIMITS) {
			String foundBILimit = searchBILimit(actualBILimits, expectedBILimit);
			softly.assertThat(foundBILimit).as("BI limit " + expectedBILimit + " isn't found").isNotNull();
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

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3693", "PAS-2794"})
	public void pas3693_eValueConfiguration2(@Optional("OR") String state) {
		eValueAcknowledgementConfigCheck();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		pas3685_verifyEvalueAcknowledgement(12, "Y", "N", "Y", "N", "Y", softly);
		checkBlueBoxMessagesWithDiffData(12, MESSAGE_INFO_4, CURRENT_BI_FALSE_YES, MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS, "membership", softly);
		softly.close();
	}

	private void eValueDependencyOnTerrirotyChannelCheck(TestData territoryChannelData, boolean eValueDiscountPresence, ETCSCoreSoftAssertions softly) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.CHANNEL_TYPE).fill(territoryChannelData);
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY).fill(territoryChannelData);
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY_LOCATION).fill(territoryChannelData);
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AGENT).fill(territoryChannelData);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isPresent(eValueDiscountPresence);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Bind Endorsement - Paperless Billing Preferences Other Than OPT_IN
	 * @scenario 1. Create new eValue eligible quote for DC
	 * 2. Bind Policy, check eValueDiscount field is disabled in P&C tab
	 * 3. Do midTerm Endorsement,
	 * 4. set eValue = Yes in P&C tab
	 * 5. Check if error message is displayed, when response from API is OPT_IN. (state==DC)
	 * 6. Do Endorsement NB+14d,
	 * 7. Check if error message is not displayed, when response from API is other than OPT_IN
	 * (state==MD).
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-294")
	public void pas294_PaperlessBillingPreferencesOtherThanOptIn(@Optional("DC") String state) {
		eValueConfigCheck();
		eValueQuoteCreation();
		simplifiedQuoteIssue();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab
				.APPLY_EVALUE_DISCOUNT).setValue("Yes");

		if ("DC".equals(state)) {
			//Check if error message is displayed, when response from API is OPT_OUT. (state==DC)
			assertThat(PremiumAndCoveragesTab.eValuePaperlessWarning.getValue().contains(PAPERLESS_PREFERENCES_NOT_ENROLLED_1)).isTrue();
		} else {
			//Check if error message is not displayed, when response from API is OPT_IN
			assertThat(PremiumAndCoveragesTab.eValuePaperlessWarning.getValue().contains(PAPERLESS_PREFERENCES_NOT_ENROLLED_1)).isFalse();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-12294", "PAS-3008"})
	public void pas12294_PriorCarrierWithLapsePriorCarrierRequired(@Optional("VA") String state) {
		eValueConfigCheck();
		eValueQuoteCreation();

		policy.dataGather().start();
		pas12294_eValueLapseCheck(true);
		simplifiedQuoteIssue();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		pas12294_eValueLapseCheck(true);
		simplifiedPendedEndorsementIssue();

		eValueEnabledForRenewal();
	}

	@AfterSuite(alwaysRun = true)
	public void deleteAllPaperlessPreferencesRequests() {
		deleteMultiplePaperlessPreferencesRequests();
	}

	private HelperWireMockStub createPaperlessPreferencesRequestId(String policyNumber, String paperlessAction) {
		PaperlessPreferencesTemplateData template = PaperlessPreferencesTemplateData.create(policyNumber, paperlessAction);
		HelperWireMockStub stub = HelperWireMockStub.create("paperless-preferences-200", template).mock();
		stubList.add(stub);
		return stub;
	}

	private HelperWireMockStub createPaperlessPreferencesErrorRequest(String policyNumber) {
		PaperlessPreferencesErrorTemplateData template = PaperlessPreferencesErrorTemplateData.create(policyNumber);
		HelperWireMockStub stub = HelperWireMockStub.create("paperless-preferences-error", template).mock();
		stubList.add(stub);
		return stub;
	}

	private void deleteMultiplePaperlessPreferencesRequests() {
		for (HelperWireMockStub stub : stubList) {
			stub.cleanUp();
		}
		stubList.clear();
	}

	private void deleteSinglePaperlessPreferenceRequest(HelperWireMockStub stub) {
		stub.cleanUp();
		stubList.remove(stub);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3693", "PAS-2794"})
	public void pas3693_eValueConfiguration3(@Optional("OR") String state) {
		eValueAcknowledgementConfigCheck();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		pas3685_verifyEvalueAcknowledgement(18, "Y", "Y", "N", "Y", "Y", softly);
		checkBlueBoxMessagesWithDiffData(18, MESSAGE_INFO_4, PAY_PLAN_FALSE_YES, MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS, "membership", softly);
		softly.close();
	}

	//TODO Replace below TCs with DataProvider when the Optional parameter State will be removed

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, priority = 1)
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3693", "PAS-2794"})
	public void pas3693_eValueConfiguration4(@Optional("OR") String state) {
		eValueAcknowledgementConfigCheck();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		pas3685_verifyEvalueAcknowledgement(15, "Y", "Y", "Y", "Y", "N", softly);
		checkBlueBoxMessagesWithDiffData(15, MESSAGE_INFO_4, PAPERLESS_AND_PRIOR_INS_FALSE_YES, MESSAGE_INFO_1, NOT_PRE_QUALIFICATIONS, "priorCarior", softly);
		softly.close();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-3693", "PAS-2794"})
	public void pas3693_eValueConfiguration5(@Optional("OR") String state) {
		eValueAcknowledgementConfigCheck();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		pas3685_verifyEvalueAcknowledgement(3, "N", "N", "N", "Y", "N", softly);
		checkBlueBoxMessagesWithDiffData(3, MESSAGE_INFO_4, ALL_FALSE, MESSAGE_INFO_4, ALL_FALSE, "priorCarior", softly);
		softly.close();
	}

	public void eValueQuoteCreation() {
		eValueQuoteCreationFromZero();
	}

	private void pas232_eValuePriorBiNoneConfigurationDependencyCheck() {
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER).setValue("None");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isDisabled();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER).setValue("No");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isDisabled();
		softly.close();
	}

	private void pas232_eValuePriorBiConfigurationDependencyCheck(String disableEvaluePriorBiLimit, String enableEvaluePriorBiLimit) {
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue(disableEvaluePriorBiLimit);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled(false);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue(enableEvaluePriorBiLimit);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled(true);
		softly.close();
	}


	/**
	 * @author Megha Gubbala
	 * @name: Paperless Preferences Time Out - What to do?
	 * @scenario 1. Create new eValue policy With PP service down.(ERROR)
	 * 2. Bind Policy, check eValueDiscount is there and its pending
	 * 3. Update Preferences from outside of pas
	 * 4. Open policy in pas and verify the error message.
	 * @details
	 */
	/// Outside pas
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23992")
	public void pas23992_PaperlessPreferanceScenarioOutsidePas(@Optional("MD") String state) {
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		eValueConfigCheck();
		//Create evalue quote
		createEvaluePolicyForPPError("Error", "Yes");
		//add neb scenario
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		HelperWireMockStub stub = createPaperlessPreferencesErrorRequest(policyNumber);

		HelperCommon.updatePolicyPreferences(policyNumber, 200);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(PP_UPDATED_OUTSIDE_OF_THE_PAS, true);
		softly.assertThat(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber))).hasValue("Pending");

		deleteSinglePaperlessPreferenceRequest(stub);
	}

	/**
	 * @author Megha Gubbala
	 * @name: Paperless Preferences Time Out - What to do?
	 * @scenario 1. Create new eValue policy With PP service down.(ERROR)
	 * 2. Bind Policy, check eValueDiscount is there and its pending and verify note "eValue status set to Pending. Unable to verify Paperless Preferences."
	 * 3. Run NB+15 job Verify PP note on NB15
	 * 4. run nb 30 Run NB+30 job and Verify PP note on NB30 make sure evalue is still there
	 * 4. go to the renewal and renew policy and verify evalue is still there in active status for renewal
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23992")
	public void pas23992_PaperlessPreferanceScenario1(@Optional("MD") String state) {
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		eValueConfigCheck();
		//Create evalue quote
		createEvaluePolicyForPPError("Error", "Yes");
		//add neb scenario
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(PP_ERROR_MESSAGE_NB, true);
		softly.assertThat(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber))).hasValue("Pending");

		HelperWireMockStub stub1 = createPaperlessPreferencesErrorRequest(policyNumber);
		// Verify user note for paperless preference
		NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(PP_ERROR_MESSAGE_NB, true);
		softly.assertThat(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber))).hasValue("Pending");

		// Set pp Error on policy
		//Chane time NB+15 and run nb 15 Job
		TestEValueMembershipProcess.jobsNBplus15plus30runNoChecks();
		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		//Verify PP note on NB15
		NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(PP_ERROR_MESSAGE_NB15, true, softly);
		softly.assertThat(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber))).hasValue("Pending");

		TestEValueMembershipProcess.jobsNBplus15plus30runNoChecks();
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(PP_ERROR_MESSAGE_NB30, true, softly);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(20));
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		Dollar totalDue = BillingSummaryPage.getTotalDue();
		IBillingAccount billing = new BillingAccount();
		TestData tdBilling = testDataManager.billingAccount;
		TestData cashPayment = tdBilling.getTestData("AcceptPayment", "TestData_Cash");
		billing.acceptPayment().perform(cashPayment, totalDue);
		SearchPage.openPolicy(policyNumber);

		softly.assertThat(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber))).hasValue("Active");
		softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("Active");

		buttonRenewals.click();
		softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("Active");

		deleteSinglePaperlessPreferenceRequest(stub1);
	}

	private void createEvaluePolicyForPPError(String ppStaus, String eValue) {
		eValueQuoteCreation();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue(eValue);
		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		String quoteNumber = PolicySummaryPage.getPolicyNumber();
		if (ppStaus.equals("Error")) {

			HelperWireMockStub stub = createPaperlessPreferencesErrorRequest(quoteNumber);

		} else if (ppStaus.equals("OPT_IN")) {
			HelperWireMockStub stub = createPaperlessPreferencesRequestId(quoteNumber, OPT_IN);

		}
		simplifiedQuoteIssue();

	}

	/**
	 * @author Megha Gubbala
	 * @name: paperless Preferences Time Out - What to do?
	 * @scenario 1. Create new eValue policy With PP service down.(ERROR) and evalue selected yes.
	 * 2. Bind Policy, check NB note is not there
	 * 3. Create endorsement to add vehicle and verify evalue is still there and pending
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23992")
	public void pas23992_PaperlessPreferanceScenario2(@Optional("MD") String state) {
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		eValueConfigCheck();
		createEvaluePolicyForPPError("Error", "Yes");

		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		// Verify user note for paperless preference
		NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(PP_ERROR_MESSAGE_NB, true, softly);

		HelperWireMockStub stub2 = createPaperlessPreferencesErrorRequest(policyNumber);
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(20));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		vehicleTab.buttonAddVehicle.click();
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.USAGE).setValue(getTestSpecificTD("VehicleTab").getValue("Usage"));
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue(getTestSpecificTD("VehicleTab").getValue("VIN"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("");
		softly.assertThat(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber))).hasValue("Pending");
		deleteSinglePaperlessPreferenceRequest(stub2);
	}

	/**
	 * @author Megha Gubbala
	 * @name: paperless Preferences Time Out - What to do?
	 * @scenario 1. Create new eValue policy With PP service down.(ERROR) and evalue selected no.
	 * 2. Bind Policy, check NB note is not there
	 * 3. Create an endorsement to opt in for evalue bind policy
	 * 4.verify pp error that you need PP for Evalue cancle error
	 * 5. Evalue staus is blank
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23992")
	public void pas23992_PaperlessPreferanceScenario3(@Optional("MD") String state) {
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		eValueConfigCheck();
		//Create evalue eligible policy
		createEvaluePolicyForPPError("Error", "No");

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		// Verify user note for paperless preference
		NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(PP_ERROR_MESSAGE_NB, false);
		HelperWireMockStub stub1 = createPaperlessPreferencesErrorRequest(policyNumber);
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(25));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		new PremiumAndCoveragesTab().calculatePremium();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		assertThat(errorTab.tableErrors.getRow(1).getCell("Message").getValue()).isEqualTo(PAPERLESS_PREFERENCES_NOT_ENROLLED_2);
		errorTab.cancel();
		new DocumentsAndBindTab().saveAndExit();
		softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("");
		softly.assertThat(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber))).hasValue("");
		NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(PP_ERROR_MESSAGE_NB, false);
		deleteSinglePaperlessPreferenceRequest(stub1);
	}


	/**
	 * @author Megha Gubbala
	 * @name: paperless Preferences Time Out - What to do?
	 * @scenario 1. Create new eValue policy With PP service down.(ERROR) and evalue selected yes.
	 * 2. Bind Policy, check NB note is not there
	 * 3. generate renewal image create endorsement on renewal add vehicle
	 * 4.we should be able to bind renwal without Error message
	 * 5. Evalue staus is blank
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23992")
	public void pas23992_PaperlessPreferanceScenarioRenewa1(@Optional("MD") String state) {
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		eValueConfigCheck();
		//Create evalue quote
		eValueQuoteCreation();
		simplifiedQuoteIssue();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		HelperWireMockStub stub2 = createPaperlessPreferencesErrorRequest(policyNumber);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(30));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		vehicleTab.buttonAddVehicle.click();
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.USAGE).setValue(getTestSpecificTD("VehicleTab").getValue("Usage"));
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue(getTestSpecificTD("VehicleTab").getValue("VIN"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();

		softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("");

		NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(PP_ERROR_MESSAGE_NB, false, softly);
		deleteSinglePaperlessPreferenceRequest(stub2);
	}

	/**
	 * @author Megha Gubbala
	 * @name: paperless Preferences Time Out - What to do?
	 * @scenario 1. Create new eValue policy With PP Opt_in and evalue selected yes.
	 * 2. Bind Policy, check NB note is not there
	 * 3. create endorsement after 2 days verify evalue status is active not pending
	 * 4.create one more endorsement to add the car and we should be able to bind endorsement with no error
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23992")
	public void pas23992_PaperlessPreferanceScenario4(@Optional("MD") String state) {
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		eValueConfigCheck();

		createEvaluePolicyForPPError("OPT_IN", "Yes");

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		HelperWireMockStub stub1 = createPaperlessPreferencesRequestId(policyNumber, OPT_IN);
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(2));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("Active");
		deleteSinglePaperlessPreferenceRequest(stub1);

		HelperWireMockStub stub2 = createPaperlessPreferencesErrorRequest(policyNumber);
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(25));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		vehicleTab.buttonAddVehicle.click();
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.USAGE).setValue(getTestSpecificTD("VehicleTab").getValue("Usage"));
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue(getTestSpecificTD("VehicleTab").getValue("VIN"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		deleteSinglePaperlessPreferenceRequest(stub2);
		softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status")).hasValue("Active");
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
		List<String> allValues = biAsset.getAllValues();
		biAsset.setValue(searchBILimit(allValues, LOWER_BI_LIMIT));
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		processEvalueDiscountPopUp(true, softly);
			softly.assertThat(applyEvalueDiscountAsset).hasValue("No");
		softly.assertThat(biAsset).as("BI limit should be changed to lowest BI limit " + LOWER_BI_LIMIT).valueContains(LOWER_BI_LIMIT);
			new PremiumAndCoveragesTab().calculatePremium();
			softly.assertThat(isTotalTermPremiumEquals0()).as(successfulCalculation).isFalse();
		softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges).valueContains(E_VALUE_DISCOUNT);
			applyEvalueDiscountAsset.setValue("Yes");
		softly.assertThat(biAsset).as("BI limit should be changed to configured BI limit " + CONFIGURED_BI_LIMIT).valueContains(CONFIGURED_BI_LIMIT);
		biAsset.setValue(searchBILimit(biAsset.getAllValues(), LOWER_BI_LIMIT));
		processEvalueDiscountPopUp(false, softly);
		softly.assertThat(applyEvalueDiscountAsset).hasValue("Yes");
		softly.assertThat(biAsset).as("BI limit should be changed to prevoius BI limit " + CONFIGURED_BI_LIMIT).valueContains(CONFIGURED_BI_LIMIT);
		biAsset.setValueContains(HIGHER_BI_LIMIT);
		String currentBI = biAsset.getValue();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValueContains("Semi-Annual");
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE).setValueContains("$500");
		softly.assertThat(biAsset).as("BI limit shouldn't be changed when we update coverages/ pay plan").hasValue(currentBI);
			new PremiumAndCoveragesTab().calculatePremium();
			softly.assertThat(isTotalTermPremiumEquals0()).as(successfulCalculation).isFalse();
		softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges).valueContains(E_VALUE_DISCOUNT);
		softly.close();
	}

	private void verifyAHEVAXXTag(Document document, String tag, String expectedValue, ETCSCoreSoftAssertions softly) {
		softly.assertThat(DocGenHelper.getDocumentDataElemByName(tag, document).getDataElementChoice().getTextField()).as(MessageFormat.format("Problem is in tag: [{0}]", tag)).isEqualTo(expectedValue);
	}

	private void pas3685_verifyEvalueAcknowledgement(int days, String aaaMemYN, String currentBIYN, String payPlnYN, String plcyPayFullAmtYN, String myPolicyYN, ETCSCoreSoftAssertions softly) {
		String quoteNumber;
		eValueQuoteCreation();
		quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(TimeSetterUtil
				.getInstance().getCurrentTime().minusDays(days).format(DateTimeUtils.MM_DD_YYYY));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab.btnGenerateDocuments.click();
		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(AHEVAXX, quoteNumber, AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE);
		if (document != null) {
			verifyAHEVAXXTag(document, "AAAMemYN", aaaMemYN, softly);
			verifyAHEVAXXTag(document, "CurrentBIYN", currentBIYN, softly);
			verifyAHEVAXXTag(document, "PayPlnYN", payPlnYN, softly);
			if ("Y".equals(payPlnYN)) {
				verifyAHEVAXXTag(document, "PlcyPayFullAmtYN", plcyPayFullAmtYN, softly);
			}
			verifyAHEVAXXTag(document, "MyPolicyYN", myPolicyYN, softly);
		}
		documentsAndBindTab.saveAndExit();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Test when System automatically removes the eValue discount.
	 * @scenario 1. Create new eValue eligible quote for VA.
	 * 2. Add two ACH Accounts registered as payment methods.
	 * 3. Select payment plan other than Annual (Quarterly).Bind the policy.
	 * 4. Create pended endorsement.
	 * 5. Check if pended endorsement button is active.
	 * 6. Go to Billing tab, switch ACH Billing Accounts. Save it.
	 * 7. Check if eValue discount was not removed by System.
	 * 8. Go to Billing tab again and remove payment method.
	 * 9. Check if Confirmation popup with warning message is displaying.Click yes.
	 * 10. Check if System automatically removed the eValue discount. (Billing tab).
	 * 11. Check if Transaction History (Policy tab) shows "eValue Removed - ACH Modified"
	 * 12. Check if Endorsement was created in table of "Payments and Other Transactions"
	 * 13. Check if pended endorsement (which was created before) was removed from the policy.
	 * @details
	 * Two tests with the same scenario:
	 * 1. Policy Eff date==today.
	 * 2. Policy Eff date in the future.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-333", "PAS-336", "PAS-238", "PAS-313"})
	public void pas333_eValueDiscountRemovedBySystem(@Optional("VA") String state) {

		String agentExpirationDate = TimeSetterUtil.getInstance().getCurrentTime().minusDays(1).format(DateTimeUtils.MM_DD_YYYY);
		String today = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		pas_333_pas339_eValueDiscountRemovedBySystem(today, agentExpirationDate);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-339")
	public void pas339_eValueDiscountRemovedBySystemFuturePolicy(@Optional("VA") String state) {
		eValueConfigCheck();
		String agentExpirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(9).format(DateTimeUtils.MM_DD_YYYY);
		String futureDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(10).format(DateTimeUtils.MM_DD_YYYY);
		pas_333_pas339_eValueDiscountRemovedBySystem(futureDate, agentExpirationDate);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-12294", "PAS-3008"})
	public void pas12294_PriorCarrierWithLapsePriorCarrierNotRequired(@Optional("OR") String state) {
		eValueQuoteCreation();

		policy.dataGather().start();
		pas12294_eValueLapseCheck(false);
		simplifiedQuoteIssue();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-12294", "PAS-3008"})
	public void pas12294_PriorCarrierLessThanSixMonthsPriorCarrierRequired(@Optional("VA") String state) {
		eValueQuoteCreation();

		policy.dataGather().start();
		pas12294_PriorCarrierLessThanSixMonthsCheck(true);
		simplifiedQuoteIssue();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		pas12294_PriorCarrierLessThanSixMonthsCheck(true);
		simplifiedPendedEndorsementIssue();

		eValueEnabledForRenewal();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, priority = 1)
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-12294", "PAS-3008"})
	public void pas12294_PriorCarrierLessThanSixMonthsPriorCarrierNotRequired(@Optional("OR") String state) {
		eValueQuoteCreation();

		policy.dataGather().start();
		pas12294_PriorCarrierLessThanSixMonthsCheck(false);
		simplifiedQuoteIssue();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-12294", "PAS-3008"})
	public void pas12294_PriorCarrierNone(@Optional("VA") String state) {
		eValueQuoteCreation();

		policy.dataGather().start();
		pas12294_PriorCarrierNoneCheck();
		simplifiedQuoteIssue();
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
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		softly.assertThat(applyEvalueDiscountAsset).hasValue("No");
		String currentBI = biAsset.getValue();
		applyEvalueDiscountAsset.setValue("Yes");
		softly.assertThat(biAsset).as("BI limit shouldn't be changed as default BI limit ($100,000/$300,000) > required BI limit " + CONFIGURED_BI_LIMIT).valueContains(currentBI);
		verifyBILimitCoverages(biAsset, uumbiAsset, softly);
		assertThat(isTotalTermPremiumEquals0()).as("Premium should be reseted to 0").isTrue();
		new PremiumAndCoveragesTab().calculatePremium();
		softly.assertThat(isTotalTermPremiumEquals0()).as(successfulCalculation).isFalse();
		applyEvalueDiscountAsset.setValue("No");
		verifyBILimitCoverages(biAsset, uumbiAsset, softly);
		biAsset.setValueContains(LOWER_BI_LIMIT);
		applyEvalueDiscountAsset.setValue("Yes");
		softly.assertThat(biAsset).as("BI limit should be changed to required BI limit " + CONFIGURED_BI_LIMIT).valueContains(CONFIGURED_BI_LIMIT);
		verifyBILimitCoverages(biAsset, uumbiAsset, softly);
		new PremiumAndCoveragesTab().calculatePremium();
		softly.assertThat(isTotalTermPremiumEquals0()).as(successfulCalculation).isFalse();
		softly.close();
	}

	private void verifyBILimitRenewal() {
		policy.renew().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		assertThat(premiumAndCoveragesTab.getAssetList().getWarning(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT.getLabel()).getValue().contains(PAPERLESS_PREFERENCES_NOT_ENROLLED_1))
				.isFalse();
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.saveAndExit();
	}

	private void pas_333_pas339_eValueDiscountRemovedBySystem(String policyEffectiveDate, String agentExpirationDate) {
		TestData dcVisa = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(0);
		eValueQuoteCreation();

		//Update Quote
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(policyEffectiveDate);
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE).setValue(agentExpirationDate);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab
				.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("Quarterly");
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM).click();
		premiumAndCoveragesTab.saveAndExit();
		String policyNumber = simplifiedQuoteIssue("ACH");

		//PAS-238 Start
		//Start make Pended Endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS).setValue("Yes");
		premiumAndCoveragesTab.saveAndExit();
		assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled();

		//PAS-336 Start
		//Add new card to the billing account
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.linkUpdateBillingAccount.click();
		AddPaymentMethodsMultiAssetList.buttonAddUpdateCreditCard.click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("contains=Card");
		updateBillingAccountAddNewCard(dcVisa, "Debit");
		updateBillingAccountActionTab.back();

		//Change payment method from ACH to the Debit card
		updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION.getLabel(), ComboBox.class).setValue("contains=Visa");
		updateBillingAccountActionTab.save();

		//Check If eValue wasn't removed
		checkIfEvalueWasRemovedBySystem(policyNumber, false);
		//PAS-238 End

		//LogOut is needed because policy is lock
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		//Remove autoPay
		BillingSummaryPage.linkUpdateBillingAccount.click();
		updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).setValue(false);
		updateBillingAccountActionTab.save();

		//Check if eValue was removed by system
		assertThat(Page.dialogConfirmation.labelMessage).hasValue("Customer acknowledges that removing recurring payments will cause the eValue to be removed.");
		Page.dialogConfirmation.buttonYes.click();
		checkIfEvalueWasRemovedBySystem(policyNumber, true);

		//Check if pended endorsement was deleted by system
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
		PolicySummaryPage.tableSelectPolicy.getRow(1).getCell(1).controls.links.get(1).click();
		assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
		//PAS-336 END
	}

	private void pas12294_eValueLapseCheck(boolean isPriorCarrierRequired) {
		int daysBeforeToday;
		if (isPriorCarrierRequired) {
			daysBeforeToday = 0;
		} else {
			daysBeforeToday = 3;
		}
		LocalDateTime policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().minusDays(daysBeforeToday);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		if (generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).isEnabled()) {
			generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(policyEffectiveDate.format(DateTimeUtils.MM_DD_YYYY));
		}

		lapseDaysRequirementsMet(policyEffectiveDate, 3, "No");

		PremiumAndCoveragesTab.RatingDetailsView.open();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, "Prior Term Bodily Injury (BI) limit").getCell("Value").getValue()).isNotEqualTo("N");
		PremiumAndCoveragesTab.RatingDetailsView.close();

		if (isPriorCarrierRequired) {
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
			generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE)
					.setValue(policyEffectiveDate.minusDays(4).format(DateTimeUtils.MM_DD_YYYY));
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled(false);
			assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("No");
			assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1)).valueContains(PRIOR_CARRIER_CONTINUOUS_INSURANCE_MESSAGE);
		} else {
			lapseDaysRequirementsMet(policyEffectiveDate, 4, "Yes");
		}
		PremiumAndCoveragesTab.RatingDetailsView.open();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, "Prior Term Bodily Injury (BI) limit").getCell("Value")).hasValue("N");
		PremiumAndCoveragesTab.RatingDetailsView.close();

		premiumAndCoveragesTab.saveAndExit();
	}

	private void lapseDaysRequirementsMet(LocalDateTime policyEffectiveDate, int daysLapsed, String expectedValue) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE)
				.setValue(policyEffectiveDate.minusDays(daysLapsed).format(DateTimeUtils.MM_DD_YYYY));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue(expectedValue);//if it was reset
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).getValue()).doesNotContain(PRIOR_CARRIER_CONTINUOUS_INSURANCE_MESSAGE);
	}

	private void pas12294_PriorCarrierLessThanSixMonthsCheck(boolean isPriorCarrierRequired) {
		int daysBeforeToday;
		if (isPriorCarrierRequired) {
			daysBeforeToday = 0;
		} else {
			daysBeforeToday = 3;
		}
		LocalDateTime policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().minusDays(daysBeforeToday);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		if (generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).isEnabled()) {
			generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(policyEffectiveDate.format(DateTimeUtils.MM_DD_YYYY));
		}

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE)
				.setValue(policyEffectiveDate.minusDays(daysBeforeToday).format(DateTimeUtils.MM_DD_YYYY));
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE)
				.setValue(policyEffectiveDate.minusDays(daysBeforeToday).minusMonths(6).format(DateTimeUtils.MM_DD_YYYY));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("No");
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).getValue()).doesNotContain(PRIOR_CARRIER_CONTINUOUS_INSURANCE_MESSAGE);

		PremiumAndCoveragesTab.RatingDetailsView.open();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, "Prior Term Bodily Injury (BI) limit").getCell("Value").getValue()).isNotEqualTo("N");
		PremiumAndCoveragesTab.RatingDetailsView.close();
		if (isPriorCarrierRequired) {
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
			generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE)
					.setValue(policyEffectiveDate.minusDays(daysBeforeToday).format(DateTimeUtils.MM_DD_YYYY));
			generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE)
					.setValue(policyEffectiveDate.minusDays(daysBeforeToday).minusMonths(6).plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled(false);
			assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("No");
			assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).getValue()).contains(PRIOR_CARRIER_CONTINUOUS_INSURANCE_MESSAGE);
		} else {
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
			generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE)
					.setValue(policyEffectiveDate.minusDays(daysBeforeToday).format(DateTimeUtils.MM_DD_YYYY));
			generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE)
					.setValue(policyEffectiveDate.minusDays(daysBeforeToday).minusMonths(6).plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).isEnabled();
			assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("Yes");
			assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).getValue()).doesNotContain(PRIOR_CARRIER_CONTINUOUS_INSURANCE_MESSAGE);
		}
		PremiumAndCoveragesTab.RatingDetailsView.open();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, "Prior Term Bodily Injury (BI) limit").getCell("Value")).hasValue("N");
		PremiumAndCoveragesTab.RatingDetailsView.close();

		premiumAndCoveragesTab.saveAndExit();
	}

	private void pas12294_PriorCarrierNoneCheck() {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER).setValue("None");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).isEnabled()).isFalse();
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("No");
		assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).getValue()).contains(PRIOR_CARRIER_CONTINUOUS_INSURANCE_MESSAGE);

		PremiumAndCoveragesTab.RatingDetailsView.open();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, "Prior Term Bodily Injury (BI) limit").getCell("Value")).hasValue("N");
		PremiumAndCoveragesTab.RatingDetailsView.close();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER).setValue("No");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).isEnabled()).isFalse();
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT)).hasValue("No");
		assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).getValue()).contains(PRIOR_CARRIER_CONTINUOUS_INSURANCE_MESSAGE);

		PremiumAndCoveragesTab.RatingDetailsView.open();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, "Prior Term Bodily Injury (BI) limit").getCell("Value")).hasValue("N");
		PremiumAndCoveragesTab.RatingDetailsView.close();

		premiumAndCoveragesTab.saveAndExit();
	}

	private void updateBillingAccountAddNewCard(TestData cardData, String cardType) {
		updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.PAYMENT_METHODS).getAsset(BillingAccountMetaData.AddPaymentMethodTab.TYPE)
				.fill(cardData);
		updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHODS).getAsset(BillingAccountMetaData.AddPaymentMethodTab.NUMBER).fill(cardData);
		AddPaymentMethodsMultiAssetList.buttonAddUpdatePaymentMethod.click();
	}

	private void checkIfEvalueWasRemovedBySystem(String policyNumber, Boolean removed) {
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonTransactionHistory.click();
		assertThat("eValue Removed - Pay...".equals(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").getValue())).isEqualTo(removed);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		assertThat("Endorsement - Other".equals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(5).getValue())).isEqualTo(removed);
	}

	private void testEvalueDiscount(String membershipStatus, String currentCarrier, boolean evalueIsSelected, boolean evalueIsPresent, String evalueStatus) {
		prefillEvalueTestData(membershipStatus, currentCarrier);
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		fillPremiumAndCoveragesTab(evalueIsSelected, softly);
		fillDriverActivityReportsTab();
		fillDocumentAndBindTab(evalueIsPresent);
		Tab.buttonSaveAndExit.click();
		simplifiedQuoteIssue();

		softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1)
				.getCell("eValue Status")).as("Invalid eValue status").valueMatches(evalueStatus);
		softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: Policy created with #{}", PolicySummaryPage.labelPolicyNumber.getValue());
		softly.close();
	}

	private void prefillEvalueTestData(String aaaProductOwned, String currentCarrierInformation) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();

		String currentCarrierInformationKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel());
		String policyInformationKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel());
		String aaaProductOwnedKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel());

		TestData tdPolicyCreation = getPolicyTD("DataGather", "TestData")
				.adjust(currentCarrierInformationKey, getTestSpecificTD(currentCarrierInformation))
				.adjust(policyInformationKey, getTestSpecificTD("PolicyInformation"))
				.adjust(aaaProductOwnedKey, getTestSpecificTD(aaaProductOwned));

		policy.getDefaultView().fillUpTo(tdPolicyCreation, PremiumAndCoveragesTab.class, true);
	}

	private void fillPremiumAndCoveragesTab(boolean eValueIsPresent, ETCSCoreSoftAssertions softly) {
		if (eValueIsPresent) {
			new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
			new PremiumAndCoveragesTab().btnCalculatePremium().click();
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges).valueContains(E_VALUE_DISCOUNT);
		} else {
			softly.assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT)).isFalse();
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

	private void fillDocumentAndBindTab(boolean evalueIsPresent) {
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
		documentsAndBindTab.fillTab(tdPolicyCreation);
	}

	private void commissionTypeCheck(List<String> expectedCommissionTypeOptions, String eValueValue, String defaultCommissionTypeValue, ETCSCoreSoftAssertions softly) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue(eValueValue);
		if (Page.dialogConfirmation.isPresent()) {
			Page.dialogConfirmation.confirm();
		}
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		softly.assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE)).containsAllOptions(expectedCommissionTypeOptions);
		softly.assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE)).hasValue(defaultCommissionTypeValue);
	}

	private void eValueEnabledForRenewal() {
		policy.renew().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).isEnabled()).isTrue();
			softly.assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).getValue()).isEqualTo("No");
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
			softly.assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).getValue()).doesNotContain(PRIOR_CARRIER_CONTINUOUS_INSURANCE_MESSAGE);
		softly.close();
		premiumAndCoveragesTab.saveAndExit();
	}

	private void eValueQuoteCreationFromZero() {
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
	}

	public String simplifiedQuoteIssue() {
		simplifiedQuoteIssue("");
		return PolicySummaryPage.getPolicyNumber();
	}

	/**
	 * Issues policy with 1 specific payment method
	 *
	 * @param paymentMethod - DC - Debit Card, CC - Credit Card, ACH - EFT
	 */
	public String simplifiedQuoteIssue(String paymentMethod) {
		policy.dataGather().start();
		if (generalTabHome.getAssetList().getAsset(HomeSSMetaData.GeneralTab.POLICY_TYPE).isPresent()) {
			NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
		} else {
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			if (documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.AGREEMENT).isPresent() && documentsAndBindTab.getAssetList()
					.getAsset(AutoSSMetaData.DocumentsAndBindTab.AGREEMENT).isEnabled()) {
				documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.AGREEMENT).setValue("I agree");
			}
		}

		for (int i = 0; i < 3; i++) {
			if (DocumentsAndBindTab.btnPurchase.isPresent()) {
				DocumentsAndBindTab.btnPurchase.click();
				if (Page.dialogConfirmation.isPresent()) {
					Page.dialogConfirmation.confirm();
				}
				errorTab.overrideAllErrors();
				if (errorTab.buttonOverride.isPresent()) {
					errorTab.override();
				}
			}
		}

		TestData purchaseTabData = getPolicyTD("DataGather", "TestData");
		if (null == purchaseTabData) {
			purchaseTabData = new TestDataManager().policy.get(PolicyType.AUTO_SS).getTestData("DataGather", "TestData_AZ");
		}
		if (!StringUtils.isEmpty(paymentMethod)) {
			purchaseTabData.adjust("PurchaseTab", getTestSpecificTD("PurchaseTab_" + paymentMethod));
		}
		new PurchaseTab().fillTab(purchaseTabData).submitTab();
		if (Page.dialogConfirmation.isPresent()) {
			Page.dialogConfirmation.confirm();
		}
		return PolicySummaryPage.getPolicyNumber();
	}

	public String simplifiedPendedEndorsementIssue() {
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
		if (errorTab.buttonOverride.isPresent()) {
			errorTab.override();
		}
		if (DocumentsAndBindTab.btnPurchase.isPresent()) {
			DocumentsAndBindTab.btnPurchase.click();
			Page.dialogConfirmation.confirm();
		}
		return PolicySummaryPage.getPolicyNumber();
	}

	private void checkBlueBoxMessages(String topic, List<String> messages, ETCSCoreSoftAssertions softly) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		softly.assertThat(PremiumAndCoveragesTab.tableEValueMessages.getRow(1).getCell(1)).hasValue(topic);
		List<String> currentValues = Arrays.asList(PremiumAndCoveragesTab.tableEValueMessages.getRow(2).getCell(1).getValue().split("\n"));
		softly.assertThat(currentValues).as("Blue Box contains wrong Messages").isEqualTo(messages);
	}

	private void selectMembershipOrPriorInsur(String membershipOrPrior, String value) {
		if ("membership".equals(membershipOrPrior)) {
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
			generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue(value);
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

	private void checkBlueBoxMessagesWithDiffData(int days, String messageInfo, List<String> messageBullet, String messageInfo1, List<String> messageBullet1, String membershipOrPrior,
			ETCSCoreSoftAssertions softly) {

		int effDateWhenMembershipIsRequired = 12;
		int effDateWhenPriorInsurIsRequired = 18;

		policy.dataGather().start();

		checkBlueBoxMessages(messageInfo, messageBullet, softly);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		checkBlueBoxMessages(messageInfo, messageBullet, softly);

		selectMembershipOrPriorInsur(membershipOrPrior, "No");
		if (effDateWhenMembershipIsRequired == days || effDateWhenPriorInsurIsRequired == days) {
			checkBlueBoxMessages(messageInfo1, messageBullet1, softly);
		} else {
			checkBlueBoxMessages(messageInfo, messageBullet, softly);
		}

		selectMembershipOrPriorInsur(membershipOrPrior, "Yes");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		checkBlueBoxMessages(messageInfo, messageBullet, softly);
		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		simplifiedQuoteIssue();

		endorsementDataGather(days);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		checkBlueBoxMessages(messageInfo, messageBullet, softly);
		selectMembershipOrPriorInsur(membershipOrPrior, "No");
		if (effDateWhenMembershipIsRequired == days || effDateWhenPriorInsurIsRequired == days) {
			checkBlueBoxMessages(messageInfo1, messageBullet1, softly);
		} else {
			checkBlueBoxMessages(messageInfo, messageBullet, softly);
		}

		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		simplifiedPendedEndorsementIssue();
	}

	public void secondEndorsementIssueCheck() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.simplifiedPendedEndorsementIssue();
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	/**
	 * example to clear cache for the product
	 */
	@Test(groups = {Groups.FUNCTIONAL, Groups.LOW})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-111")
	public void pas111_clearCache() {
		adminApp().open();
		new CacheManager().goClearCacheManagerTable();
	}
}
